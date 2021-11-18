package com.igormaznitsa.midp.DynamicEngine;

import com.nokia.mid.ui.DirectGraphics;

import java.io.IOException;

//import java.io.FileOutputStream;

/*
 * Copyright (C) 2002 Igor A. Maznitsa (igor.maznitsa@igormaznitsa.com).  All rights reserved.
 * Software is confidential and copyrighted. Title to Software and all associated intellectual property rights
 * is retained by Igor A. Maznitsa. You may not make copies of Software, other than a single copy of Software for
 * archival purposes.  Unless enforcement is prohibited by applicable law, you may not modify, decompile, or reverse
 * engineer Software.  You acknowledge that Software is not designed, licensed or intended for use in the design,
 * construction, operation or maintenance of any nuclear facility. Igor A. Maznitsa disclaims any express or implied
 * warranty of fitness for such uses.
 *
 * This java file describes the class which is used to create dynamic png images
 */

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
    private static void make_crc_table()
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

    private static int calculateCRC32(byte[] _array, int _startpos, int _len)
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
    public static final int calculateAdler32(byte[] _inarray, int _start, int _length)
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
    public int writeIntToArray(int _offset, int _value)
    {
        ab_imagedata[_offset++] = (byte) (_value >>> 24);
        ab_imagedata[_offset++] = (byte) (_value >>> 16);
        ab_imagedata[_offset++] = (byte) (_value >>> 8);
        ab_imagedata[_offset++] = (byte) _value;
        return _offset;
    }

    // Write an int array into the image array
    private int writeIntArray(int _offset, int[] _array)
    {
        int i_len = _array.length;
        for (int li = 0; li < i_len; li++)
        {
            int i_elem = _array[li];
            _offset = writeIntToArray(_offset, i_elem);
        }
        return _offset;
    }

    /**
     *  Scroll the image to left
     * @param _pixels a number of pixels for scrolling
     */
    public void scrollToLeft(int _pixels)
    {
        if (_pixels == 0) return;
        int i_imageoffset = _pixels + i_startPosForImage + 1;
        int i_imageoffset2 = i_startPosForImage + 1;

        int i_width = i_ImageWidth - _pixels;

        for (int li = 0; li < i_ImageHeight; li++)
        {
            System.arraycopy(ab_imagedata, i_imageoffset, ab_imagedata, i_imageoffset2, i_width);
            i_imageoffset += i_ImageWidth + 1;
            i_imageoffset2 += i_ImageWidth + 1;
        }
        lg_imagechanged = true;
    }

    /**
     *  Scroll the image to right
     * @param _pixels a number of pixels for scrolling
     */
    public void scrollToRight(int _pixels)
    {
        if (_pixels == 0) return;
        int i_imageoffset = i_startPosForImage + 1;
        int i_imageoffset2 = _pixels + i_startPosForImage + 1;


        int i_width = i_ImageWidth - _pixels;

        for (int li = 0; li < i_ImageHeight; li++)
        {
            System.arraycopy(ab_imagedata, i_imageoffset, ab_imagedata, i_imageoffset2, i_width);
            i_imageoffset += i_ImageWidth + 1;
            i_imageoffset2 += i_ImageWidth + 1;
        }
        lg_imagechanged = true;
    }

    private void _upgradePaletteToShPalette()
    {
        int i_offst = i_palettedataoffset;
        for(int li=0;li<i_PaletteWidth;li++)
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
    public synchronized void drawSpriteFromLibrary(byte[] _spritelibrary, int _offset, int _x, int _y) throws IOException
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


        //int i_dheight = i_height;
        i_height = i_yoffst + i_vishght;

        switch (i_type)
        {
            // Without any compression
            case 0:
                {
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
                                    byte b_curval = ab_linebuffer[ldx+i_x];
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
                // Full screen without any compression and without transparent... hard copy of the screen
            case 2:
                {
                    if (i_width != i_ImageWidth || i_height != i_ImageHeight) throw new IOException("Error sprite size");
                    System.arraycopy(_spritelibrary, _offset, ab_imagedata, i_startPosForImage, (i_width + 1) * i_height);
                }
                ;
                break;
                // Full screen with RLE compression without transparent... hard copy of the screen
            case 3:
                {
                    if (i_width != i_ImageWidth || i_height != i_ImageHeight) throw new IOException("Error sprite size");

                    //ab_linebuffer[0] = 0;
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
     * Fill the image with the specified color
     * @param _colorIndex the index of selected color
     */
    public void fillImage(int _colorIndex)
    {
        //ab_linebuffer[0] = 0;
        for(int li=1;li<=i_ImageWidth;li++)
        {
            ab_linebuffer [li] = (byte)_colorIndex;
        }

        int i_offset = i_startPosForImage;
        int i_wdth = i_ImageWidth+1;
        for(int li=0;li<i_ImageHeight;li++)
        {
            System.arraycopy(ab_linebuffer,0,ab_imagedata,i_offset,i_wdth);
            i_offset+=i_wdth;
        }

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

    /**
     * The constructor of the class
     * @param _width the width of the image
     * @param _height the height of the image
     * @param _palettewidth the number of colors in the palette for the image
     * @throws java.io.IOException the constructor throws the exception if the size of the image is more than 32kB
     */
    public DynamicImage_8bpp(int _width, int _height, int _palettewidth) throws IOException
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
     * The function returns the pointer to the image array which contains the PNG image data
     * @return the image array as a byte array
     */
    public byte[] getImageArray()
    {
        return ab_imagedata;
    }

    /**
     * Set new value to a palette entity.
     * @param _index The index of a palette entity
     * @param _rgbvalue the packed int value in the RGB format
     */
    public void setPaletteForIndex(int _index, int _rgbvalue)
    {
        int i_offset = i_palettedataoffset + _index * 3;
        byte b_r = (byte) (_rgbvalue >>> 16);
        byte b_g = (byte) (_rgbvalue >>> 8);
        byte b_b = (byte) _rgbvalue;
        ash_palette[_index] = (short) (((b_r & 0xF0) << 4) | ((b_g & 0xF0) | (b_b & 0xF0) >>> 4));

        ab_imagedata[i_offset++] = b_r;
        ab_imagedata[i_offset++] = b_g;
        ab_imagedata[i_offset] = b_b;

        lg_palettechanged = true;
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

        return (i_r << 24) | (i_g << 16) | i_b;
    }

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
        _upgradePaletteToShPalette();
    }

    public void loadPNGPaletteFromArray(byte[] _newpalette) throws IOException
    {
        int i_len = i_PaletteWidth * 3+12;
        if (_newpalette.length != i_len) throw new IOException("Incompatible palette");
        int i_indx = i_palettedataoffset - 8;
        System.arraycopy(_newpalette,0,ab_imagedata,i_indx,_newpalette.length);
        _upgradePaletteToShPalette();
    }

//====For NOKIA Only====
    public void fillGraphicContextByTheImage(DirectGraphics _context)
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
//======================

    /**
     * The function encodes the image array and returns a completed PNG image as a byte array
     * @return a completed PNG image as a byte array
     */
    public byte[] encodeToPNG()
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
        return ab_imagedata;
    }

    /**
     * Set a pixel into the image
     * @param x the X coordinate
     * @param y the Y coordinate
     * @param color the color index from the palette
     */
    public void setPixel(int x, int y, int color)
    {
        ab_imagedata[i_startPosForImage + (1 + i_ImageWidth) * y + x + 1] = (byte) color;
        lg_imagechanged = true;
    }

    /**
     * Return the color index for a pixel in the image array
     * @param x the X coord
     * @param y the Y coord
     * @return the color index of the pixel as an int value
     */
    public int getPixel(int x, int y)
    {
        return ab_imagedata[i_startPosForImage + (1 + i_ImageWidth) * y + x + 1];
    }
}

