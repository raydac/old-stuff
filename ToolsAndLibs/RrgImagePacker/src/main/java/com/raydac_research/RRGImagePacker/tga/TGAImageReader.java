package com.raydac_research.RRGImagePacker.tga;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.awt.image.WritableRaster;
import java.io.IOException;
import java.io.FileInputStream;
import java.nio.ByteOrder;

import javax.imageio.stream.MemoryCacheImageInputStream;

public class TGAImageReader
{
    private MemoryCacheImageInputStream inputStream;
    private TGAHeader header;

    public TGAImageReader()
    {
    }

    private synchronized TGAHeader getHeader() throws IOException
    {
        return header;
    }

    public int getHeight() throws IOException
    {
        return getHeader().getHeight();
    }

    public int getWidth() throws IOException
    {
        return getHeader().getWidth();
    }

    public BufferedImage read(String _fileName) throws IOException
    {
        FileInputStream p_fis = new FileInputStream(_fileName);
        return read(p_fis);
    }

    public BufferedImage read(FileInputStream _file) throws IOException
    {
        MemoryCacheImageInputStream p_miis = new MemoryCacheImageInputStream(_file);
        return read(p_miis);
    }

    public BufferedImage read(MemoryCacheImageInputStream imageStream) throws IOException
    {
        imageStream.setByteOrder(ByteOrder.LITTLE_ENDIAN);

        inputStream = imageStream;

        // read and get the header
        header = new TGAHeader(imageStream);

        // get the height and width from the header for convenience
        final int width = header.getWidth();
        final int height = header.getHeight();

        // read the color map data.  If the image does not contain a color map
        // then null will be returned.
        final int[] colorMap = readColorMap(header);

        inputStream.seek(header.getPixelDataOffset());

        // get the destination image and WritableRaster for the image type and 
        // size
        final BufferedImage image = new BufferedImage(width,height,BufferedImage.TYPE_INT_ARGB);// getDestination(param, imageTypes,width, height);
        final WritableRaster imageRaster = image.getRaster();         

        final WritableRaster raster = imageRaster.createWritableChild(0, 0,width, height,0, 0,null);

        // set up to read the data
        final int[] intData = ((DataBufferInt) raster.getDataBuffer()).getData(); // CHECK:  is this valid / acceptible?
        int index = 0; // the index in the intData array
        int runLength = 0; // the number of pixels in a run length
        boolean readPixel = true; // if true then a raw pixel is read.  Used by the RLE.
        boolean isRaw = false; // if true then the next pixels should be read.  Used by the RLE.
        int pixel = 0; // the current pixel data

        for (int y = 0; y < height; y++)
        {
            // if the image is flipped top-to-bottom then set the index in 
            // intData appropriately
            if (!header.isBottomToTop())
                index = y;
            else /* is top-to-bottom */
                index = (height - y) - 1;  

            // account for the width
            // TODO:  this doesn't take into account the destination size or bands
            index *= width;

            // loop over the columns
            // TODO:  this should be destinationROI.width (right?)
            // NOTE:  *if* destinations are used the RLE will break as this will
            //        cause the repetition count field to be missed.
            for (int x = 0; x < width; x++)
            {
                // if the image is compressed (run length encoded) then determine
                // if a pixel should be read or if the current one should be
                // used (using the current one is part of the RLE'ing).
                if (header.isCompressed())
                {
                    // if there is a non-zero run length then there are still
                    // compressed pixels
                    if (runLength > 0)
                    {
                        // decrement the run length and flag that a pixel should
                        // not be read
                        // NOTE:  a pixel is only read from the input if the
                        //        packet was raw.  If it was a run length packet
                        //        then the previous (current) pixel is used.
                        runLength--;
                        readPixel = isRaw;
                    }
                    else /* non-positive run length */
                    {
                        // read the repetition count field 
                        runLength = inputStream.readByte() & 0xFF; // unsigned

                        // determine which packet type:  raw or runlength
                        isRaw = ((runLength & 0x80) == 0); // bit 7 == 0 -> raw; bit 7 == 1 -> runlength

                        // if a run length packet then shift to get the number
                        if (!isRaw)
                            runLength -= 0x80;
                        /* else -- is raw so there's no need to shift */ 

                        // the next field is always read (it's the pixel data)
                        readPixel = true;
                    }
                }

                // read the next pixel
                // NOTE:  only don't read when in a run length packet
                if (readPixel)
                {
                    // NOTE:  the alpha must hav a default value since it is
                    //        not guaranteed to be present for each pixel read
                    int red = 0, green = 0, blue = 0, alpha = 0xFF;

                    // read based on the number of bits per pixel
                    switch (header.getBitsPerPixel())
                    {
                        // grey scale (R = G = B)
                        case 8:
                        default:
                            {
                                // read the data -- it is either the color map index
                                // or the color for each pixel
                                final int data = inputStream.readByte() & 0xFF; // unsigned

                                // if the image is a color mapped image then the
                                // resulting pixel is pulled from the color map, 
                                // otherwise each pixel gets the data 
                                if (header.hasColorMap())
                                {
                                    // the pixel is pulled from the color map
                                    // CHECK:  do sanity bounds check?
                                    pixel = (alpha << 24) | colorMap[header.getFirstColorMapEntryIndex()+data];
                                }
                                else /* no color map */
                                {
                                    // each color component is set to the color
                                    red = green = blue = data;
                                
                                    // combine each component into the result
                                    pixel = (alpha<<24) | (red<<16) | (green << 8) | blue;
                                }

                                break;
                            }

                            // 5-5-5 (RGB)
                        case 15:
                        case 16:
                            {
                                // read the two bytes 
                                final int data = inputStream.readShort() & 0xFFFF; // unsigned

                                // get each color component -- each is 5 bits
                                red = ((data >> 10) & 0x1F) << 3;
                                green = ((data >> 5) & 0x1F) << 3;
                                blue = (data & 0x1F) << 3;

                                // combine each component into the result

                                pixel = (alpha<<24) | (red << 16) | (green << 8) | blue;

                                break;
                            }

                            // true color RGB(A) (8 bits per pixel)
                        case 24:
                        case 32:
                            // read each color component -- the alpha is only
                            // read if there are 32 bits per pixel
                            blue = inputStream.readByte() & 0xFF; // unsigned
                            green = inputStream.readByte() & 0xFF; // unsigned
                            red = inputStream.readByte() & 0xFF; // unsigned
                            if (header.getBitsPerPixel() == 32)
                                alpha = (inputStream.readByte() & 0xFF); // unsigned
                            /* else -- 24 bits per pixel (i.e. no alpha) */

                            // combine each component into the result
                            pixel =  (alpha << 24) | (red << 16) | (green << 8) | blue ;

                            break;
                    }
                }

                // put the pixel in the data array
                intData[index] = pixel;

                // advance to the next pixel
                // TODO:  the right-to-left switch
                index++;
            }
        }

        return image;
    }

    private int[] readColorMap(final TGAHeader header) throws IOException
    {
        // determine if the image contains a color map.  If not, return null
        if (!header.hasColorMap())
            return null;
        /* else -- there is a color map */

        // seek to the start of the color map in the input stream
        inputStream.seek(header.getColorMapDataOffset());

        // get the number of colros in the color map and the number of bits
        // per color map entry
        final int numberOfColors = header.getColorMapLength();
        final int bitsPerEntry = header.getBitsPerColorMapEntry();

        // create the array that will contain the color map data
        // CHECK:  why is tge explicit +1 needed here ?!? 
        final int[] colorMap = new int[numberOfColors + 1];

        // read each color map entry
        for (int i = 0; i < numberOfColors; i++)
        {
            int red = 0, green = 0, blue = 0, alpha = 0;

            // read based on the number of bits per color map entry
            switch (bitsPerEntry)
            {
                // grey scale (R = G = B)
                case 8:
                default:
                    {
                        final int data = inputStream.readByte() & 0xFF; // unsigned
                        red = green = blue = data;

                        break;
                    }

                    // 5-5-5 (RGB)
                case 15:
                case 16:
                    {
                        // read the two bytes 
                        final int data = inputStream.readShort() & 0xFFFF; // unsigned

                        // get each color component -- each is 5 bits
                        red = ((data >> 10) & 0x1F) << 3;
                        green = ((data >> 5) & 0x1F) << 3;
                        blue = (data & 0x1F) << 3;

                        if (bitsPerEntry == 16)
                        {
                            alpha = (data>>15)&0x1;
                        }

                        break;
                    }

                    // true color RGB(A) (8 bits per pixel)
                case 24:
                case 32:
                    // read each color component 
                    // CHECK:  is there an alpha?!?
                    blue = inputStream.readByte() & 0xFF; // unsigned
                    green = inputStream.readByte() & 0xFF; // unsigned
                    red = inputStream.readByte() & 0xFF; // unsigned

                    if (bitsPerEntry == 32)
                    {
                        alpha = inputStream.readByte() & 0xFF;
                    }

                    break;
            }

            // combine each component into the result
            colorMap[i] = ((0xFF-alpha)<<24) | (red << 16) | (green << 8) | blue;
        }

        return colorMap;
    }
}
