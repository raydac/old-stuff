package com.igormaznitsa.DynamicEngine;

import com.nokia.mid.ui.DirectGraphics;

import javax.microedition.lcdui.Image;
import java.io.IOException;

public class DynamicImage_8bpp
{
    private static final int[] ai_png_header = new int[]{0x89504E47, 0x0D0A1A0A, 0x0000000D, 0x49484452};
    private static final int[] ai_png_end = new int[]{0x00000000, 0x49454E44, 0xAE426082};

    // the line buffer for using by the drawSpriteFromLibrary function
    private static byte[] ab_linebuffer = new byte[257];
    // Line buffer for using by drawToGraphicContextAsARGB
    private static short[] ash_linebuffer;
    // inside palette representation as an array of integer RGB
    private short[] ash_palette = new short[256];
    // inside palette array what contains byte palette representation
    private byte[] ab_palette_array = null;
    //This object is used for synchronization between the drawing process and an image array modification
    private final Object p_blockObject = new Object();

    /**
     * The width of the image
     */
    public int i_ImageWidth;
    /**
     * The height of the image
     */
    public int i_ImageHeight;
    /**
     * The number of colors in the image palette
     */
    public int i_PaletteWidth;

    // If the flag is true then the image part was changed
    private boolean lg_imagechanged;
    // If the flag is true then the palette part was changed
    private boolean lg_palettechanged;
    // the offset to the CRC32 code for the palette data
    private int i_palettecrcoffset;
    // the offset to the palette data
    private int i_palettedataoffset;
    // The array contains all image data
    private byte[] ab_imagedata;
    // The variable contains length of the raw image data block
    private int i_imagelen;
    // The offset of adler32 code in the image array
    private int i_adler32offset;
    // The table contains values for fast CRC32 calculating
    private static int[] ai_crctable;

    // The position of the CRC32 code for IDAT chunk
    private int i_imageDataCRC32Offset;
    // The position of the IDAT chunk
    private int i_startOffsetForIDATChunk;
    // The length of the IDAT chunk
    private int i_lengthIDATChunk;
    // The position of the image data
    private int i_startPosForImage;

//==================== The CRC32 section ============================
    private static final void make_crc_table()
    {
        ai_crctable = new int[256];
        for (int n = 0; n < 256; n++)
        {
            int c = n;
            for (int k = 0; k < 8; k++)
            {
                if ((c & 1) != 0)
                    c = 0xedb88320 ^ (c >>> 1);
                else
                    c = c >>> 1;
            }
            ai_crctable[n] = c;
        }
    }

    private static final int calculateCRC32(byte[] _array, int _startpos, int _len)
    {
        int c = -1;
        int i_end = _startpos + _len;

        for (int n = _startpos; n < i_end; n++)
        {
            c = ai_crctable[(c ^ _array[n]) & 0xff] ^ (c >>> 8);
        }
        return c ^ (0xFFFFFFFF);
    }
//===================================================================

    /**
     * Calculating Adler32 code for a byte array
     * @param _inarray the incomming byte array
     * @param _start the index of the start position
     * @param _length the length of the calculated block
     * @return Adler32 code as an int value
     */
    private static final int calculateAdler32(byte[] _inarray, int _start, int _length)
    {
        final int ADLER32_BASE = 0xFFF1;
        int i_end = _start + _length;

        //int i_adler = 1;
        int i_s1 = 1; //i_adler & 0xffff;
        int i_s2 = 0;// (i_adler >>> 16);

        for (int li = _start; li < i_end; li++)
        {
            i_s1 = (i_s1 + (((int) _inarray[li]) & 0xff)) % ADLER32_BASE;
            i_s2 = (i_s2 + i_s1) % ADLER32_BASE;
        }
        return (i_s2 << 16) | i_s1;
    }

    /**
     * The function writes an int value to the image array
     * @param _offset the offset for start of writing
     * @param _value the written value
     * @return
     */
    private final int writeIntToArray(int _offset, int _value)
    {
        ab_imagedata[_offset++] = (byte) (_value >>> 24);
        ab_imagedata[_offset++] = (byte) (_value >>> 16);
        ab_imagedata[_offset++] = (byte) (_value >>> 8);
        ab_imagedata[_offset++] = (byte) _value;
        return _offset;
    }

    // Write an int array into the image array
    private final int writeIntArray(int _offset, int[] _array)
    {
        int i_len = _array.length;
        for (int li = 0; li < i_len; li++)
        {
            int i_elem = _array[li];
            _offset = writeIntToArray(_offset, i_elem);
        }
        return _offset;
    }

    private final void _upgradePaletteToShPalette()
    {
        int i_offst = i_palettedataoffset;
        for (int li = 0; li < i_PaletteWidth; li++)
        {
            byte b_r = ab_imagedata[i_offst++];
            byte b_g = ab_imagedata[i_offst++];
            byte b_b = ab_imagedata[i_offst++];
            ash_palette[li] = (short) (((b_r & 0xF0) << 4) | ((b_g & 0xF0) | (b_b & 0xF0) >>> 4));
        }
    }

    /**
     * Drawing of a sprite from  a sprite library to the image
     * @param _spritelibrary a pointer to a sprite library
     * @param _offset the offset to the sprite in the sprite library
     * @param _x The X coordinate of the drawing
     * @param _y The Y coordinate of the drawing
     */
    public void drawSpriteFromLibrary(byte[] _spritelibrary, int _offset, int _x, int _y) throws IOException
    {
        int i_type = _spritelibrary[_offset++];
        boolean lg_hastransparent = false;
        byte b_transparentindex = -1;
        if ((i_type & 0x1) != 0)
        {
            lg_hastransparent = true;
            b_transparentindex = _spritelibrary[_offset++];
        }

        i_type = i_type >>> 4;

        int i_width = _spritelibrary[_offset++] & 0xFF;
        int i_height = _spritelibrary[_offset++] & 0xFF;

        if (_x >= i_ImageWidth || _y >= i_ImageHeight) return;

        int i_viswidth = _x < 0 ? i_width + _x : i_width + _x >= i_ImageWidth ? i_ImageWidth - _x : i_width;
        i_viswidth = i_viswidth > i_ImageWidth ? i_ImageWidth : i_viswidth;
        int i_vishght = _y < 0 ? i_height + _y : i_height + _y >= i_ImageHeight ? i_ImageHeight - _y : i_height;
        i_vishght = i_vishght > i_ImageHeight ? i_ImageHeight : i_vishght;
        if (i_vishght <= 0 || i_viswidth <= 0) return;

        int i_xoffst = _x >= 0 ? _x : 0 - _x;
        int i_yoffst = _y >= 0 ? _y : 0 - _y;

        int i_x;
        int i_y;

        if (_x < 0)
        {
            i_x = i_xoffst;
            _x = 0;
        }
        else
            i_x = 0;

        if (_y < 0)
        {
            i_y = i_yoffst;
            _y = 0;
        }
        else
            i_y = 0;

        int i_nextlinebufferoffset = i_ImageWidth + 1;
        int i_curlinebufferoffset = i_startPosForImage + 1 + _x + _y * i_nextlinebufferoffset;

        i_height = i_yoffst + i_vishght;

        switch (i_type)
        {
            case 0:
                {
                    // A sprite without any compression
                    _offset += i_x + i_y * i_width;

                    if (lg_hastransparent)
                    {
                        for (int li = i_yoffst; li < i_height; li++)
                        {
                            for (int lx = 0; lx < i_viswidth; lx++)
                            {
                                byte b_curval = _spritelibrary[_offset + lx];
                                if (b_transparentindex == b_curval) continue;
                                ab_imagedata[i_curlinebufferoffset + lx] = b_curval;
                            }
                            i_curlinebufferoffset += i_nextlinebufferoffset;
                            _offset += i_width;
                        }
                    }
                    else
                    {
                        for (int li = i_yoffst; li < i_height; li++)
                        {
                            System.arraycopy(_spritelibrary, _offset, ab_imagedata, i_curlinebufferoffset, i_viswidth);
                            i_curlinebufferoffset += i_nextlinebufferoffset;
                            _offset += i_width;
                        }
                    }
                }
                ;
                break;
            case 1:
                {
                    // RLE compressed sprite
                    boolean lg_visible = false;
                    for (int ly = 0; ly < i_vishght; ly++)
                    {
                        if (ly >= i_y)
                            lg_visible = true;
                        else
                            lg_visible = false;

                        int i_indx = 0;
                        while (i_indx != i_width)
                        {
                            int i_value = _spritelibrary[_offset++];
                            if ((i_value & 0xC0) == 0xC0)
                            {
                                int i_counter = i_value & 0x3F;
                                i_value = _spritelibrary[_offset++];

                                if (lg_visible)
                                {
                                    while (i_counter != 0)
                                    {
                                        ab_linebuffer[i_indx++] = (byte) i_value;
                                        i_counter--;
                                    }
                                }
                                else
                                    i_indx += i_counter;
                            }
                            else
                            {
                                if (lg_visible) ab_linebuffer[i_indx] = (byte) i_value;
                                i_indx++;
                            }
                        }

                        if (lg_visible)
                        {
                            if (lg_hastransparent)
                            {
                                for (int ldx = 0; ldx < i_viswidth; ldx++)
                                {
                                    byte b_curval = ab_linebuffer[ldx + i_x];
                                    if (b_transparentindex == b_curval) continue;
                                    ab_imagedata[i_curlinebufferoffset + ldx] = b_curval;
                                }
                            }
                            else
                            {
                                System.arraycopy(ab_linebuffer, i_x, ab_imagedata, i_curlinebufferoffset, i_viswidth);
                            }
                            i_curlinebufferoffset += i_nextlinebufferoffset;
                        }
                    }
                }
                ;
                break;
            case 2:
                {
                    // Full screen without any compression and without transparent... hard copy of the screen
                    if (i_width != i_ImageWidth || i_height != i_ImageHeight) throw new IOException("Error sprite size");
                    System.arraycopy(_spritelibrary, _offset, ab_imagedata, i_startPosForImage, (i_width + 1) * i_height);
                }
                ;
                break;
            case 3:
                {
                    // Full screen with RLE compression without transparent... hard copy of the screen
                    if (i_width != i_ImageWidth || i_height != i_ImageHeight) throw new IOException("Error sprite size");

                    i_curlinebufferoffset = i_startPosForImage;

                    int i_len = i_nextlinebufferoffset * i_height;

                    int i_indx = 0;
                    while (i_indx < i_len)
                    {
                        int i_value = _spritelibrary[_offset++];
                        if ((i_value & 0xC0) == 0xC0)
                        {
                            int i_counter = i_value & 0x3F;
                            i_value = _spritelibrary[_offset++];

                            while (i_counter > 0)
                            {
                                ab_imagedata[i_indx + i_startPosForImage] = (byte) i_value;
                                i_counter--;
                                i_indx++;
                            }
                        }
                        else
                        {
                            ab_imagedata[i_indx + i_startPosForImage] = (byte) i_value;
                            i_indx++;
                        }
                    }
                }
                ;
                break;
        }
        ab_linebuffer[0] = 0;
        lg_imagechanged = true;
    }

    /**
     * Return the width for a sprite in the library
     * @param _library
     * @param _offset
     * @return the width as an int value
     */
    public int getWidthForSprite(byte[] _library, int _offset)
    {
        int i_t = _library[_offset++];
        if ((i_t & 1) != 0) _offset++;
        return _library[_offset] & 0xFF;
    }

    /**
     * Return the height for a sprite in the library
     * @param _library
     * @param _offset
     * @return the height as an int value
     */
    public int getHeightForSprite(byte[] _library, int _offset)
    {
        int i_t = _library[_offset++];
        if ((i_t & 1) != 0) _offset++;
        _offset++;
        return _library[_offset] & 0xFF;
    }

    public int getHeight()
    {
        return i_ImageHeight;
    }

    public int getWidth()
    {
        return i_ImageWidth;
    }

    public int getPaletteWidth()
    {
        return i_PaletteWidth;
    }

    public DynamicImage_8bpp(int _width, int _height, byte[] _paletteArray) throws IOException
    {
        if (_paletteArray == null) throw new IOException("The palette array is NULL");
        int i_paletteSize = _paletteArray[0] & 0xFF;
        ab_palette_array = _paletteArray;
        // Initing of the engine
        _initEngine(_width, _height, i_paletteSize);
        // Load the palette into the palette array
        loadPaletteFromPaletteArray(0x100,0,false);
    }

    private final void _initEngine(int _width, int _height, int _palettewidth) throws IOException
    {
        if (ai_crctable == null) make_crc_table();

        i_ImageWidth = -1;
        i_ImageHeight = -1;
        i_imagelen = (_width + 1) * _height;
        int i_back = i_imagelen ^ 0xFFFF;
        if (i_imagelen > 0x8FFF) throw new IOException("Too big image[" + i_imagelen + "]");

        i_ImageWidth = _width;
        i_ImageHeight = _height;
        i_PaletteWidth = _palettewidth;
        int i_palettebytes = i_PaletteWidth * 3;

        ab_imagedata = new byte[(ai_png_header.length * 4 + 17) + (i_palettebytes + 12) + (i_imagelen + 11 + 12) + ai_png_end.length * 4];

        // Writing the PNG header
        int i_offset = 0;
        i_offset = writeIntArray(i_offset, ai_png_header);
        i_offset = writeIntToArray(i_offset, i_ImageWidth);
        i_offset = writeIntToArray(i_offset, i_ImageHeight);
        ab_imagedata[i_offset++] = 8;
        ab_imagedata[i_offset++] = 3;
        ab_imagedata[i_offset++] = 0;
        ab_imagedata[i_offset++] = 0;
        ab_imagedata[i_offset++] = 0;

        int i_crc32 = calculateCRC32(ab_imagedata, 12, 17);// calculateCrc32(ab_imagedata,12,17);
        i_offset = writeIntToArray(i_offset, i_crc32);

        //Writing the palette data
        i_offset = writeIntToArray(i_offset, i_palettebytes);
        i_offset = writeIntToArray(i_offset, 0x504c5445);
        i_palettedataoffset = i_offset;
        i_offset += i_palettebytes;
        i_palettecrcoffset = i_offset;
        i_offset += 4;

        // Writing the IDAT chunk
        i_offset = writeIntToArray(i_offset, i_imagelen + 11);
        i_startOffsetForIDATChunk = i_offset;
        i_lengthIDATChunk = i_imagelen + 15;
        i_offset = writeIntToArray(i_offset, 0x49444154);

        ab_imagedata[i_offset++] = 0x78;
        ab_imagedata[i_offset++] = (byte) 0xda;
        ab_imagedata[i_offset++] = 0x01;
        ab_imagedata[i_offset++] = (byte) i_imagelen;
        ab_imagedata[i_offset++] = (byte) (i_imagelen >>> 8);
        ab_imagedata[i_offset++] = (byte) i_back;
        ab_imagedata[i_offset++] = (byte) (i_back >>> 8);

        i_startPosForImage = i_offset;
        i_offset += i_imagelen;
        i_adler32offset = i_offset;
        i_offset += 4;
        i_imageDataCRC32Offset = i_offset;
        i_offset += 4;

        // Writing the end of the PNG image
        writeIntArray(i_offset, ai_png_end);

        lg_imagechanged = true;
        lg_palettechanged = true;
    }

    /**
     * The constructor of the class
     * @param _width the width of the image
     * @param _height the height of the image
     * @param _palettewidth the number of colors in the palette for the image
     * @throws java.io.IOException the constructor throws the exception if the size of the image is more than 32kB
     */
    public DynamicImage_8bpp(int _width, int _height, int _palettewidth) throws IOException
    {
        _initEngine(_width, _height, _palettewidth);
    }

    /**
     * The function returns the pointer to the image array which contains the PNG image data
     * @return the image array as a byte array
     */
    public byte[] getImageArray()
    {
        return ab_imagedata;
    }

    /**
     * This function returns the pointer to the synchronized object and you can use that for synchronizing of graphical operations
     * @return the synchronized object as an Object object
     */
    public final Object getSynchronizedObject()
    {
        return p_blockObject;
    }

    /**
     * The function encodes the image array and returns a completed PNG image as a byte array
     * @return a completed PNG image as a byte array
     */
    public synchronized byte[] encodeToPNGByteArray()
    {
        synchronized (p_blockObject)
        {
            if (lg_palettechanged)
            {
                int i_crc32 = calculateCRC32(ab_imagedata, i_palettedataoffset - 4, i_PaletteWidth * 3 + 4);
                writeIntToArray(i_palettecrcoffset, i_crc32);
                lg_palettechanged = false;
            }

            if (lg_imagechanged)
            {
                int i_adler32code = calculateAdler32(ab_imagedata, i_startPosForImage, i_imagelen);
                writeIntToArray(i_adler32offset, i_adler32code);
                int i_crc32 = calculateCRC32(ab_imagedata, i_startOffsetForIDATChunk, i_lengthIDATChunk);
                writeIntToArray(i_imageDataCRC32Offset, i_crc32);
                lg_imagechanged = false;
            }
        }
        return ab_imagedata;
    }

    public synchronized Image encodeToImage()
    {
        synchronized (p_blockObject)
        {
            if (lg_palettechanged)
            {
                int i_crc32 = calculateCRC32(ab_imagedata, i_palettedataoffset - 4, i_PaletteWidth * 3 + 4);
                writeIntToArray(i_palettecrcoffset, i_crc32);
                lg_palettechanged = false;
            }

            if (lg_imagechanged)
            {
                int i_adler32code = calculateAdler32(ab_imagedata, i_startPosForImage, i_imagelen);
                writeIntToArray(i_adler32offset, i_adler32code);
                int i_crc32 = calculateCRC32(ab_imagedata, i_startOffsetForIDATChunk, i_lengthIDATChunk);
                writeIntToArray(i_imageDataCRC32Offset, i_crc32);
                lg_imagechanged = false;
            }
        }

        Image p_newImage = Image.createImage(ab_imagedata,0,ab_imagedata.length);
        return p_newImage;
    }



    //=====================PALETTE OPERATIONS=======================
    /**
     * To download new palette from a byte array
     * @param _newpalette a byte array contains new palette
     * @throws java.io.IOException the exctption throws if new palette ia not compatible with the number of colors in the image
     */
    public void loadPaletteFromArray(byte[] _newpalette) throws IOException
    {
        int i_len = i_PaletteWidth * 3;
        if (_newpalette.length != i_len) throw new IOException("Incompatible palette");
        int i_indx = i_palettedataoffset;
        for (int li = 0; li < i_len; li++) ab_imagedata[i_indx++] = _newpalette[li];
        lg_palettechanged = true;

        // You can remove below string if you don't use the code in Nokia phone
        _upgradePaletteToShPalette();
    }

    /**
     * The function returns a palette value for its index
     * @param _index the index of a palette entity
     * @return the color representation of the palette entity as a packed RGB value
     */
    public int getPaletteForIndex(int _index)
    {
        int i_offst = i_palettedataoffset + _index * 3;
        int i_r = ab_imagedata[i_offst++] & 0xFF;
        int i_g = ab_imagedata[i_offst++] & 0xFF;
        int i_b = ab_imagedata[i_offst] & 0xFF;

        return (i_r << 16) | (i_g << 8) | i_b;
    }

    public void copyCurrentPaletteToPaletteArray()
    {
        synchronized(p_blockObject)
        {
            if (ab_palette_array!=null)
            {
                System.arraycopy(ab_imagedata,i_palettedataoffset,ab_palette_array,0,i_PaletteWidth*3);
            }
        }
    }

    /**
     * This function allows you to load the palette value from the palette array and to multiply that on the coefficient
     * @param _i8coeff the coefficient what will be used in multiply. It has format of fixed point integer: 1 = 0x100, 2 = 0x200
     * @param _brightnessIncreasing
     * @param _useSaturation
     */
    public void loadPaletteFromPaletteArray(int _i8coeff,int _brightnessIncreasing,boolean _useSaturation)
    {
        synchronized (p_blockObject)
        {
            if (ab_palette_array != null)
            {
                int i_offset = i_palettedataoffset;
                int i_arroffset = 1;
                for (int li = 0; li < i_PaletteWidth; li++)
                {
                    int i_r = (((ab_palette_array[i_arroffset++] & 0xFF)+_brightnessIncreasing) * _i8coeff) >> 8;
                    int i_g = (((ab_palette_array[i_arroffset++] & 0xFF)+_brightnessIncreasing) * _i8coeff) >> 8;
                    int i_b = (((ab_palette_array[i_arroffset++] & 0xFF)+_brightnessIncreasing) * _i8coeff) >> 8;

                    if (_useSaturation)
                    {
                        if (i_r<0)
                            i_r = 0;
                        else
                            if (i_r>0xFF) i_r = 0xFF;

                        if (i_g<0)
                            i_g = 0;
                        else
                            if (i_g>0xFF) i_g = 0xFF;

                        if (i_b<0)
                            i_b = 0;
                        else
                            if (i_b>0xFF) i_b = 0xFF;
                    }

                    ash_palette[li] = (short) (((i_r & 0xF0) << 4) | ((i_g & 0xF0) | (i_b & 0xF0) >>> 4));
                    ab_imagedata[i_offset++] = (byte) i_r;
                    ab_imagedata[i_offset++] = (byte) i_g;
                    ab_imagedata[i_offset++] = (byte) i_b;
                }
                lg_palettechanged = true;
            }
        }
    }

    /**
     * Set new value to a palette entity.
     * @param _index The index of a palette entity
     * @param _rgbvalue the packed int value in the RGB format
     */
    public void setPaletteForIndex(int _index, int _rgbvalue)
    {
        synchronized (p_blockObject)
        {
            int i_offset = i_palettedataoffset + _index * 3;
            byte b_r = (byte) (_rgbvalue >>> 16);
            byte b_g = (byte) (_rgbvalue >>> 8);
            byte b_b = (byte) _rgbvalue;
            ash_palette[_index] = (short) (((b_r & 0xF0) << 4) | ((b_g & 0xF0) | (b_b & 0xF0) >>> 4));

            ab_imagedata[i_offset++] = b_r;
            ab_imagedata[i_offset++] = b_g;
            ab_imagedata[i_offset] = b_b;

            if (ab_palette_array != null)
            {
                int i_off = _index * 3;
                i_off++;
                ab_palette_array[i_off++] = b_r;
                ab_palette_array[i_off++] = b_g;
                ab_palette_array[i_off] = b_b;
            }

            lg_palettechanged = true;
        }
    }

    /**
     * This function scrolls the palette of the image
     * @param _startIndex the start index of the scrollable area
     * @param _length the length of the scrollable area
     * @param _leftward if the flag is true then the scrolling will be leftward else rightward
     * @param _cyclic the flag shows what the scrolling is cyclic
     */
    public void scrollPalette(int _startIndex, int _length, boolean _leftward, boolean _cyclic)
    {
        synchronized (p_blockObject)
        {
            int i_indxOffset = i_palettedataoffset + _startIndex * 3;
            _length--;
            int i_length3 = _length * 3;

            if (_leftward)
            {
                // Scroll leftward
                short sh_cell = ash_palette[_startIndex];
                int i_doffset = i_indxOffset;
                byte b_r = ab_imagedata[i_doffset++];
                byte b_g = ab_imagedata[i_doffset++];
                byte b_b = ab_imagedata[i_doffset];

                System.arraycopy(ash_palette, _startIndex + 1, ash_palette, _startIndex, _length);
                System.arraycopy(ab_imagedata, i_indxOffset + 3, ab_imagedata, i_indxOffset, i_length3);

                if (_cyclic)
                {
                    ash_palette[_startIndex + _length] = sh_cell;
                    i_doffset = i_indxOffset + i_length3;
                    ab_imagedata[i_doffset++] = b_r;
                    ab_imagedata[i_doffset++] = b_g;
                    ab_imagedata[i_doffset] = b_b;
                }
            }
            else
            {
                // Scroll rightward
                short sh_cell = ash_palette[_startIndex + _length];
                int i_doffset = i_indxOffset + i_length3;
                byte b_r = ab_imagedata[i_doffset++];
                byte b_g = ab_imagedata[i_doffset++];
                byte b_b = ab_imagedata[i_doffset];

                int i_shIndx = _startIndex + _length - 1;
                int i_arrIndex = i_indxOffset + i_length3 - 3;

                for (int li = 0; li < _length; li++)
                {
                    // Processing the short array
                    ash_palette[i_shIndx + 1] = ash_palette[i_shIndx];
                    i_shIndx--;
                    //Processing of the image array
                    int i_indx = i_arrIndex;
                    byte b_fr = ab_imagedata[i_indx++];
                    byte b_fg = ab_imagedata[i_indx++];
                    byte b_fb = ab_imagedata[i_indx++];

                    ab_imagedata[i_indx++] = b_fr;
                    ab_imagedata[i_indx++] = b_fg;
                    ab_imagedata[i_indx++] = b_fb;
                    i_arrIndex -= 3;
                }

                if (_cyclic)
                {
                    ash_palette[_startIndex] = sh_cell;
                    i_doffset = i_indxOffset;
                    ab_imagedata[i_doffset++] = b_r;
                    ab_imagedata[i_doffset++] = b_g;
                    ab_imagedata[i_doffset] = b_b;
                }
            }
            lg_palettechanged = true;
        }
    }

    //==============================================================

    //=====================IMAGE OPERATIONS=======================
    /**
     * The vertical flip effect
     */
    public void flipVertical()
    {
        synchronized (p_blockObject)
        {
            if (i_ImageHeight < 2) return;
            int i_topLineOffset = i_startPosForImage + 1;
            int i_offset = i_ImageWidth + 1;
            int i_downLineOffset = i_topLineOffset + ((i_ImageHeight - 1) * i_offset);

            while (true)
            {
                // Copy of the top line to the buffer
                System.arraycopy(ab_imagedata, i_topLineOffset, ab_linebuffer, 1, i_ImageWidth);
                // Copy of the down line to the top line
                System.arraycopy(ab_imagedata, i_downLineOffset, ab_imagedata, i_topLineOffset, i_ImageWidth);
                // Copy the buffer to the down line
                System.arraycopy(ab_linebuffer, 1, ab_imagedata, i_downLineOffset, i_ImageWidth);

                i_topLineOffset += i_offset;
                i_downLineOffset -= i_offset;

                if (i_topLineOffset > i_downLineOffset) break;
            }
            lg_imagechanged = true;
        }
    }

    /**
     * The horizontal flip effect
     */
    public void flipHorizontal()
    {
        synchronized (p_blockObject)
        {
            if (i_ImageWidth < 2) return;
            int i_leftLineOffset = i_startPosForImage + 1;
            int i_offset = i_ImageWidth + 1;
            int i_rightLineOffset = i_leftLineOffset + i_ImageWidth - 1;
            int i_maxDown = i_rightLineOffset + ((i_ImageHeight - 1) * i_offset);

            while (i_leftLineOffset < i_rightLineOffset)
            {
                int i_vertLOffst = i_leftLineOffset;
                int i_vertROffst = i_rightLineOffset;
                while (i_vertLOffst < i_maxDown)
                {
                    byte b_buff = ab_imagedata[i_vertLOffst];
                    ab_imagedata[i_vertLOffst] = ab_imagedata[i_vertROffst];
                    ab_imagedata[i_vertROffst] = b_buff;
                    i_vertLOffst += i_offset;
                    i_vertROffst += i_offset;
                }

                i_leftLineOffset++;
                i_rightLineOffset--;
            }

            lg_imagechanged = true;
        }
    }

    /**
     *  Rotate the image 90 clockwise. Warning! This fuctions will work if the image has equivalent width and height parameters
     */
    public void rotateRight()
    {
        if (i_ImageWidth != i_ImageHeight) return;
        synchronized (p_blockObject)
        {
            int i_lineOffset = i_ImageWidth + 1;
            int i_tlCornerI = i_startPosForImage + 1;
            int i_trCornerI = i_tlCornerI + i_ImageWidth - 1;
            int i_drCornerI = i_trCornerI + i_lineOffset * (i_ImageHeight - 1);
            int i_dlCornerI = i_tlCornerI + i_lineOffset * (i_ImageHeight - 1);

            int i_lineWidth = i_ImageWidth - 1;

            while (i_lineWidth > 0)
            {
                int i_tlCorner = i_tlCornerI;
                int i_trCorner = i_trCornerI;
                int i_drCorner = i_drCornerI;
                int i_dlCorner = i_dlCornerI;

                int i_loffst = 0;

                while (i_loffst < i_lineWidth)
                {
                    byte b_tl = ab_imagedata[i_tlCorner];
                    byte b_tr = ab_imagedata[i_trCorner];
                    byte b_dr = ab_imagedata[i_drCorner];
                    byte b_dl = ab_imagedata[i_dlCorner];

                    ab_imagedata[i_tlCorner] = b_dl;
                    ab_imagedata[i_trCorner] = b_tl;
                    ab_imagedata[i_drCorner] = b_tr;
                    ab_imagedata[i_dlCorner] = b_dr;

                    i_tlCorner++;
                    i_trCorner += i_lineOffset;
                    i_drCorner--;
                    i_dlCorner -= i_lineOffset;

                    i_loffst++;
                }

                i_tlCornerI = i_tlCornerI + i_lineOffset + 1;
                i_trCornerI = i_trCornerI + i_lineOffset - 1;
                i_drCornerI = i_drCornerI - i_lineOffset - 1;
                i_dlCornerI = i_dlCornerI - i_lineOffset + 1;

                i_lineWidth -= 2;
            }
            lg_imagechanged = true;
        }
    }

    /**
     *  Rotate the image 90 counter-clockwise. Warning! This fuctions will work if the image has equivalent width and height parameters
     */
    public void rotateLeft()
    {
        if (i_ImageWidth != i_ImageHeight) return;
        synchronized (p_blockObject)
        {
            int i_lineOffset = i_ImageWidth + 1;
            int i_tlCornerI = i_startPosForImage + 1;
            int i_trCornerI = i_tlCornerI + i_ImageWidth - 1;
            int i_drCornerI = i_trCornerI + i_lineOffset * (i_ImageHeight - 1);
            int i_dlCornerI = i_tlCornerI + i_lineOffset * (i_ImageHeight - 1);

            int i_lineWidth = i_ImageWidth - 1;

            while (i_lineWidth > 0)
            {
                int i_tlCorner = i_tlCornerI;
                int i_trCorner = i_trCornerI;
                int i_drCorner = i_drCornerI;
                int i_dlCorner = i_dlCornerI;

                int i_loffst = 0;

                while (i_loffst < i_lineWidth)
                {
                    byte b_tl = ab_imagedata[i_tlCorner];
                    byte b_tr = ab_imagedata[i_trCorner];
                    byte b_dr = ab_imagedata[i_drCorner];
                    byte b_dl = ab_imagedata[i_dlCorner];

                    ab_imagedata[i_tlCorner] = b_tr;
                    ab_imagedata[i_trCorner] = b_dr;
                    ab_imagedata[i_drCorner] = b_dl;
                    ab_imagedata[i_dlCorner] = b_tl;

                    i_tlCorner += i_lineOffset;
                    i_trCorner--;
                    i_drCorner -= i_lineOffset;
                    i_dlCorner++;

                    i_loffst++;
                }

                i_tlCornerI = i_tlCornerI + i_lineOffset + 1;
                i_trCornerI = i_trCornerI + i_lineOffset - 1;
                i_drCornerI = i_drCornerI - i_lineOffset - 1;
                i_dlCornerI = i_dlCornerI - i_lineOffset + 1;

                i_lineWidth -= 2;
            }
            lg_imagechanged = true;
        }
    }

    /**
     * Set a pixel into the image
     * @param x the X coordinate
     * @param y the Y coordinate
     * @param color the color index from the palette
     */
    public void setPixel(int x, int y, int color)
    {
        synchronized (p_blockObject)
        {
            ab_imagedata[i_startPosForImage + (1 + i_ImageWidth) * y + x + 1] = (byte) color;
            lg_imagechanged = true;
        }
    }

    /**
     * Return the color index for a pixel in the image array
     * @param x the X coord
     * @param y the Y coord
     * @return the color index of the pixel as an int value
     */
    public int getPixel(int x, int y)
    {
        return (ab_imagedata[i_startPosForImage + (1 + i_ImageWidth) * y + x + 1] & 0xFF);
    }

    /**
     * Fill the image with the specified color
     * @param _backgroundColorIndex the index of selected color
     */
    public void clearImage(int _backgroundColorIndex)
    {
        synchronized (p_blockObject)
        {
            for (int li = 1; li <= i_ImageWidth; li++)
            {
                ab_linebuffer[li] = (byte) _backgroundColorIndex;
            }

            int i_offset = i_startPosForImage;
            int i_wdth = i_ImageWidth + 1;
            for (int li = 0; li < i_ImageHeight; li++)
            {
                System.arraycopy(ab_linebuffer, 0, ab_imagedata, i_offset, i_wdth);
                i_offset += i_wdth;
            }
            lg_imagechanged = true;
        }
    }

    /**
     *  Scroll the image upward
     * @param _step a number of pixels for scrolling
     */
    public void scrollUp(int _step)
    {
        synchronized (p_blockObject)
        {
            int i_offset = i_ImageWidth + 1;
            int i_topOffset = i_startPosForImage + 1;
            int i_stOffset = i_topOffset + i_offset * _step;
            int i_maxData = i_startPosForImage + (i_ImageHeight - 1) * i_offset;

            while (i_stOffset < i_maxData)
            {
                System.arraycopy(ab_imagedata, i_stOffset, ab_imagedata, i_topOffset, i_ImageWidth);
                i_topOffset += i_offset;
                i_stOffset += i_offset;
            }
            lg_imagechanged = true;
        }
    }

    /**
     *  Scroll the image downward
     * @param _step a number of pixels for scrolling
     */
    public void scrollDown(int _step)
    {
        synchronized (p_blockObject)
        {
            int i_offset = i_ImageWidth + 1;
            int i_downOffset = i_startPosForImage + 1 + (i_ImageHeight - 1) * i_offset;
            int i_stOffset = i_downOffset - (i_offset * _step);

            while (i_stOffset > i_startPosForImage)
            {
                System.arraycopy(ab_imagedata, i_stOffset, ab_imagedata, i_downOffset, i_ImageWidth);
                i_downOffset -= i_offset;
                i_stOffset -= i_offset;
            }
            lg_imagechanged = true;
        }
    }

    /**
     *  Scroll the image to right
     * @param _step a number of pixels for scrolling
     */
    public void scrollRight(int _step)
    {
        synchronized (p_blockObject)
        {
            if (_step == 0) return;
            int i_imageoffset = i_startPosForImage + 1;
            int i_imageoffset2 = _step + i_startPosForImage + 1;

            int i_width = i_ImageWidth - _step;

            for (int li = 0; li < i_ImageHeight; li++)
            {
                System.arraycopy(ab_imagedata, i_imageoffset, ab_imagedata, i_imageoffset2, i_width);
                i_imageoffset += i_ImageWidth + 1;
                i_imageoffset2 += i_ImageWidth + 1;
            }
            lg_imagechanged = true;
        }
    }

    /**
     *  Scroll the image to left
     * @param _step a number of pixels for scrolling
     */
    public void scrollLeft(int _step)
    {
        synchronized (p_blockObject)
        {
            if (_step == 0) return;
            int i_imageoffset = _step + i_startPosForImage + 1;
            int i_imageoffset2 = i_startPosForImage + 1;

            int i_width = i_ImageWidth - _step;

            for (int li = 0; li < i_ImageHeight; li++)
            {
                System.arraycopy(ab_imagedata, i_imageoffset, ab_imagedata, i_imageoffset2, i_width);
                i_imageoffset += i_ImageWidth + 1;
                i_imageoffset2 += i_ImageWidth + 1;
            }
            lg_imagechanged = true;
        }
    }
    //==============================================================

    //====For NOKIA Only====
    public void fillGraphicContextByTheImage(DirectGraphics _context)
    {
        // This synchronizing allows us to save the image from any changing during drawing time
        synchronized (p_blockObject)
        {
            if (ash_linebuffer == null) ash_linebuffer = new short[256];
            int i_offst = i_startPosForImage;
            for (int ly = 0; ly < i_ImageHeight; ly++)
            {
                i_offst++;
                int i_x = 0;
                while (i_x < i_ImageWidth)
                {
                    int i_indx = ab_imagedata[i_offst++];
                    ash_linebuffer[i_x] = ash_palette[i_indx & 0xFF];
                    i_x++;
                }
                _context.drawPixels(ash_linebuffer, false, 0, i_ImageWidth, 0, ly, i_ImageWidth, 1, 0, DirectGraphics.TYPE_USHORT_444_RGB);
            }
        }
    }
    //======================

    //=================Graphical primitives==============
    private byte b_penColor;

    public void setPenColor(byte _index)
    {
        if (_index>=i_PaletteWidth || _index<0) new Throwable("The bad palette index ["+(_index&0xFF)+"]");
        b_penColor = _index;
    }

    private final void _setPoint(int _x, int _y)
    {
        if (_x < 0 || _x >= i_ImageWidth) return;
        if (_y < 0 || _y >= i_ImageHeight) return;

        ab_imagedata[i_startPosForImage + (1 + i_ImageWidth) * _y + _x + 1] = b_penColor;
    }

    public void drawRectangle(int _x, int _y, int _width, int _height)
    {
        synchronized (p_blockObject)
        {
            int xd,yd;
            int i_x2 = _x + _width;
            int i_y2 = _y + _height;
            if (i_x2 < _x) xd = -1; else xd = 1;
            if (i_y2 < _y) yd = -1; else yd = 1;
            for (int x = _x; x != i_x2; x += xd)
            {
                _setPoint(x, _y);
                _setPoint(x, i_y2);
            }
            for (int y = _y; y != i_y2; y += yd)
            {
                _setPoint(_x, y);
                _setPoint(i_x2, y);
            }

            lg_imagechanged = true;
        }
    }

    public void fillRectangle(int _x, int _y, int _width, int _height)
    {
        synchronized (p_blockObject)
        {
            int xd,yd;
            int i_x2 = _x + _width;
            int i_y2 = _y + _height;
            if (i_x2 < _x) xd = -1; else xd = 1;
            if (i_y2 < _y) yd = -1; else yd = 1;
            for (int y = _y; y != i_y2; y += yd)
            {
                for (int x = _x; x != i_x2; x += xd) _setPoint(x, y);
            }
            lg_imagechanged = true;
        }
    }

    public void drawLine(int _x, int _y, int _x2, int _y2)
    {
        synchronized (p_blockObject)
        {
            int dx = Math.abs(_x2 - _x);
            int dy = Math.abs(_y2 - _y);
            int sx = _x2 >= _x ? 1 : -1;
            int sy = _y2 >= _y ? 1 : -1;
            if (dy <= dx)
            {
                int d = (dy << 1) - dx;
                int d1 = dy << 1;
                int d2 = (dy - dx) << 1;

                _setPoint(_x, _y);

                for (int x = _x + sx,y = _y, i = 1; i <= dx; i++, x += sx)
                {
                    if (d > 0)
                    {
                        d += d2;
                        y += sy;
                    }
                    else
                        d += d1;
                    _setPoint(x, y);
                }

            }
            else
            {
                int d = (dx << 1) - dy;
                int d1 = dx << 1;
                int d2 = (dx - dy) << 1;
                _setPoint(_x, _y);
                for (int x = _x, y = _y + sy, i = 1; i <= dy; i++, y += sy)
                {
                    if (d > 0)
                    {
                        d += d2;
                        x += sx;
                    }
                    else
                        d += d1;
                    _setPoint(x, y);
                }
            }
            lg_imagechanged = true;
        }
    }

    private final void _plot_circle_points(int _x, int _y, int _cx, int _cy)
    {
        int startx,starty,endx,endy,x1,y1;
        starty = _y;
        endy = (_y + 1);
        startx = _x;
        endx = (_x + 1);

        for (x1 = startx; x1 < endx; ++x1)
        {
            _setPoint(x1 + _cx, _y + _cy);
            _setPoint(x1 + _cx, _cy - _y);
            _setPoint(_cx - x1, _y + _cy);
            _setPoint(_cx - x1, _cy - _y);
        }

        for (y1 = starty; y1 < endy; ++y1)
        {
            _setPoint(y1 + _cx, _x + _cy);
            _setPoint(y1 + _cx, _cy - _x);
            _setPoint(_cx - y1, _x + _cy);
            _setPoint(_cx - y1, _cy - _x);
        }
    }

    public void drawCircle(int _centerX, int _centerY, int _radius)
    {
        synchronized (p_blockObject)
        {
            int x,y,delta;
            y = _radius;
            delta = 3 - 2 * _radius;
            for (x = 0; x < y;)
            {
                _plot_circle_points(x, y, _centerX, _centerY);
                if (delta < 0)
                    delta += 4 * x + 6;
                else
                {
                    delta += 4 * (x - y) + 10;
                    y--;
                }
                x++;
            }
            x = y;
            if (y != 0) _plot_circle_points(x, y, _centerX, _centerY);
            lg_imagechanged = true;
        }
    }

    public void drawPoint(int _x,int _y)
    {
        synchronized(p_blockObject)
        {
            _setPoint(_x,_y);
            lg_imagechanged = true;
        }
    }

    public void drawEllipse(int _x1, int _y1, int _x2, int _y2)
    {
        synchronized (p_blockObject)
        {
            int WorkingX,WorkingY,Threshold,XAdjust,YAdjust;
            WorkingX = Math.min(_x1, _x2);
            WorkingY = Math.max(_x1, _x2);
            _x1 = WorkingX;
            _x2 = WorkingY;
            WorkingX = Math.min(_y1, _y2);
            WorkingY = Math.max(_y1, _y2);
            _y1 = WorkingX;
            _y2 = WorkingY;
            int A = (_x2 - _x1) >> 1;
            int B = (_y2 - _y1) >> 1;
            int X = (_x1 + _x2) >> 1;
            int Y = (_y2 + _y1) >> 1;
            int ASquared = A * A;
            int BSquared = B * B;

            _setPoint(X, Y + B);
            _setPoint(X, Y - B);

            WorkingX = 0;
            WorkingY = 0;
            XAdjust = 0;
            YAdjust = (ASquared << 1) * B;
            Threshold = (ASquared >> 2) - ASquared * B;
            for (; ;)
            {
                Threshold += XAdjust + BSquared;
                if (Threshold >= 0)
                {
                    YAdjust -= (ASquared << 1);
                    Threshold -= YAdjust;
                    WorkingY--;
                }

                XAdjust += (BSquared << 1);
                WorkingX++;

                if (XAdjust >= YAdjust) break;

                _setPoint(X + WorkingX, _y1 - WorkingY);
                _setPoint(X - WorkingX, _y1 - WorkingY);
                _setPoint(X + WorkingX, _y2 + WorkingY);
                _setPoint(X - WorkingX, _y2 + WorkingY);
            }

            WorkingX = A;
            WorkingY = 0;
            XAdjust = (BSquared << 1) * A;
            YAdjust = 0;
            Threshold = (BSquared >> 2) - BSquared * A;

            _setPoint(X + A, Y);
            _setPoint(X - A, Y);

            for (; ;)
            {
                Threshold += YAdjust + ASquared;
                if (Threshold >= 0)
                {
                    XAdjust -= (BSquared << 1);
                    Threshold = Threshold - XAdjust;
                    WorkingX--;
                }

                YAdjust += (ASquared << 1);
                WorkingY++;
                if (YAdjust > XAdjust) break;
                _setPoint(X + WorkingX, Y - WorkingY);
                _setPoint(X - WorkingX, Y - WorkingY);
                _setPoint(X + WorkingX, Y + WorkingY);
                _setPoint(X - WorkingX, Y + WorkingY);
            }
            lg_imagechanged = true;
        }
    }
}