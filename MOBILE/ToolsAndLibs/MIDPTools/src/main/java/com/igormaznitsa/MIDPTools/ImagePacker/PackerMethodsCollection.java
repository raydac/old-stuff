package com.igormaznitsa.MIDPTools.ImagePacker;

import com.igormaznitsa.Utils.WaveletCodec;

import java.io.*;
import java.util.*;
import java.util.zip.CRC32;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.Raster;
import java.awt.image.PixelGrabber;

/**
 * Copyright (C) 2003 Igor A. Maznitsa (igor.maznitsa@igormaznitsa.com).  All rights reserved.
 * Software is confidential and copyrighted. Title to Software and all associated intellectual property rights
 * is retained by Igor A. Maznitsa. You may not make copies of Software, other than a single copy of Software for
 * archival purposes.  Unless enforcement is prohibited by applicable law, you may not modify, decompile, or reverse
 * engineer Software.  You acknowledge that Software is not designed, licensed or intended for use in the design,
 * construction, operation or maintenance of any nuclear facility. Igor A. Maznitsa disclaims any express or implied
 * warranty of fitness for such uses.
 *
 * This is an utility for packing PNG or GIF files into a bin block.
 */

public class PackerMethodsCollection
{
    protected static MediaTracker p_mtracker = new MediaTracker(new Button());
    protected static String s_descriptorname = "index.lst";
    protected static boolean lg_verbose = false;


    protected static void packingPNGtoLitePNG(byte[] _inarray, DataOutputStream _outputstream) throws IOException
    {
        final int HEADER_IHDR = 0;
        final int HEADER_PLTE = 1;
        final int HEADER_IDAT = 2;
        final int HEADER_tRNS = 3;

        int i_offset = 8;
        int i_len = 8;

        ByteArrayOutputStream p_baos = new ByteArrayOutputStream(512);

        // Writing two bytes for length of PNG file
        p_baos.write(0);
        p_baos.write(0);

        while (i_offset < _inarray.length)
        {
            // Reading of the chunk length field
            int i_chunklen = 0;
            i_chunklen = (_inarray[i_offset++] & 0xFF) << 24;
            i_chunklen |= ((_inarray[i_offset++] & 0xFF) << 16);
            i_chunklen = ((_inarray[i_offset++] & 0xFF) << 8);
            i_chunklen |= (_inarray[i_offset++] & 0xFF);

            // Reading of the chunk name field
            String s_chnk = "";
            s_chnk += (char) _inarray[i_offset++];
            s_chnk += (char) _inarray[i_offset++];
            s_chnk += (char) _inarray[i_offset++];
            s_chnk += (char) _inarray[i_offset++];

            if (s_chnk.equals("IHDR"))
            {
                i_len += (i_chunklen + 12);
                p_baos.write(HEADER_IHDR);

                int i_wdth = 0;
                i_wdth = (_inarray[i_offset++] & 0xFF) << 24;
                i_wdth |= ((_inarray[i_offset++] & 0xFF) << 16);
                i_wdth = ((_inarray[i_offset++] & 0xFF) << 8);
                i_wdth |= (_inarray[i_offset++] & 0xFF);

                if (i_wdth > 255) throw new IOException("Too big width in the image");

                int i_hght = 0;
                i_hght = (_inarray[i_offset++] & 0xFF) << 24;
                i_hght |= ((_inarray[i_offset++] & 0xFF) << 16);
                i_hght = ((_inarray[i_offset++] & 0xFF) << 8);
                i_hght |= (_inarray[i_offset++] & 0xFF);

                if (i_hght > 255) throw new IOException("Too big height in the image");

                int i_bitdepth = _inarray[i_offset++] & 0xFF;
                int i_colortype = _inarray[i_offset++] & 0xFF;

                // Compression method
                if (_inarray[i_offset++] != 0) throw new IOException("Unsupported compression method");

                // Filter method
                if (_inarray[i_offset++] != 0) throw new IOException("Unsupported filter method");

                // Interlace method
                int i_interlace = (_inarray[i_offset++] & 0xFF);
                if (i_interlace > 1) throw new IOException("Unsupported interlace mode");

                // Reading CRC
                i_offset += 4;

                // Writing data to stream

                // Writing of the width of the image
                p_baos.write(i_wdth);
                // Writing of the height of the image
                p_baos.write(i_hght);
                // Writing of the bitdepth and the colortype of the image
                p_baos.write(((i_bitdepth << 4) & 0xF0) | ((i_colortype & 0x0F) | (i_interlace << 3)));


                String s_statistic = i_wdth + "x" + i_hght;

                s_statistic += " bpp: " + i_bitdepth;
                if (i_bitdepth > 8)
                {
                    s_statistic = "!" + s_statistic;
                    //Toolkit.getDefaultToolkit().beep();
                }

                switch (i_colortype)
                {
                    case 0:
                        s_statistic += " GRAY";
                        break;
                    case 2:
                        s_statistic += " RGB";
                        break;
                    case 3:
                        {
                            s_statistic += " PALETTE";
                            s_statistic = "!" + s_statistic;
                            //Toolkit.getDefaultToolkit().beep();
                        }
                        ;
                        break;
                    case 4:
                        s_statistic += " GRAY+ALPHA";
                        break;
                    case 6:
                        s_statistic += " RGB+ALPHA";
                        break;
                    default :
                        throw new IOException("Unknown color type [" + i_colortype + "]");
                }

                if (i_interlace == 1) s_statistic += " (!)INTERLACED";
                if(lg_verbose)
                     System.out.println(s_statistic);

            }
            else if (s_chnk.equals("IDAT"))
            {
                i_len += (i_chunklen + 12);
                p_baos.write(HEADER_IDAT);
                // Writing length of data
                if (i_chunklen > 0x7F)
                {
                    p_baos.write(((i_chunklen >> 8) & 0x7F) | 0x80);
                    p_baos.write(i_chunklen & 0xFF);
                }
                else
                {
                    p_baos.write(i_chunklen);
                }
                // Writing data
                for (int li = 0; li < i_chunklen; li++)
                {
                    p_baos.write(_inarray[i_offset++]);
                }
                // Reading CRC
                i_offset += 4;
            }
            else if (s_chnk.equals("PLTE"))
            {
                i_len += (i_chunklen + 12);
                p_baos.write(HEADER_PLTE);
                // Writing length of data
                if (i_chunklen > 0x7F)
                {
                    p_baos.write(((i_chunklen >> 8) & 0x7F) | 0x80);
                    p_baos.write(i_chunklen & 0xFF);
                }
                else
                {
                    p_baos.write(i_chunklen);
                }
                // Writing data
                for (int li = 0; li < i_chunklen; li++)
                {
                    p_baos.write(_inarray[i_offset++]);
                }
                // Reading CRC
                i_offset += 4;
            }
            else if (s_chnk.equals("tRNS"))
            {
                i_len += (i_chunklen + 12);
                p_baos.write(HEADER_tRNS);
                // Writing length of data
                if (i_chunklen > 0x7F)
                {
                    p_baos.write(((i_chunklen >> 8) & 0x7F) | 0x80);
                    p_baos.write(i_chunklen & 0xFF);
                }
                else
                {
                    p_baos.write(i_chunklen);
                }
                // Writing data
                for (int li = 0; li < i_chunklen; li++)
                {
                    p_baos.write(_inarray[i_offset++]);
                }
                // Reading CRC
                i_offset += 4;
            }
            else if (s_chnk.equals("IEND"))
            {
                i_len += 12;
                break;
            }
            else
            {
                i_offset += i_chunklen + 4;
            }
        }

        p_baos.flush();
        byte[] ab_litepng = p_baos.toByteArray();
        ab_litepng[0] = (byte) (i_len >>> 8);
        ab_litepng[1] = (byte) i_len;

        // Writing the lite png to output stream
        _outputstream.write(ab_litepng);
        _outputstream.flush();
        ab_litepng = null;
    }

    protected static void packingForNokia(byte[] _inarray, DataOutputStream _outputstream) throws IOException
    {
        Image p_img = Toolkit.getDefaultToolkit().createImage(_inarray);

        p_mtracker.addImage(p_img, 0);

        try
        {
            p_mtracker.waitForAll();
        }
        catch (InterruptedException e)
        {
            return;
        }

        p_mtracker.removeImage(p_img);

        int i_width = p_img.getWidth(null);
        int i_height = p_img.getHeight(null);

        BufferedImage p_bimg = new BufferedImage(i_width, i_height, BufferedImage.TYPE_4BYTE_ABGR);
        p_bimg.getGraphics().drawImage(p_img, 0, 0, null);
        BufferedImage p_binimg = new BufferedImage(i_width, i_height, BufferedImage.TYPE_BYTE_BINARY);
        p_binimg.getGraphics().drawImage(p_img, 0, 0, null);

        boolean lg_is_mask = false;
        Raster p_raster = p_bimg.getAlphaRaster();
        for (int ly = 0; ly < i_height; ly++)
        {
            for (int lx = 0; lx < i_width; lx++)
            {
                if (p_raster.getSample(lx, ly, 0) != 0xFF)
                {
                    lg_is_mask = true;
                    break;
                }
            }
            if (lg_is_mask) break;
        }

        // Filling of the image and mask arrays
        int i_akkum = 0;
        int i_rastakkum = 0;
        int i_cntr = 0;
        int i_mask = 0;

        Raster p_imgraster = p_binimg.getRaster();

        byte[] ab_rasterarray = new byte[((i_width * i_height) + 7) >> 3];

        byte[] ab_maskarray = null;
        if (lg_is_mask) ab_maskarray = new byte[((i_width * i_height) + 7) >> 3];

        int i_maxpixels = 8;
        int i_initmask = 0x80;

        i_cntr = 0;
        int i_indx = 0;
        i_akkum = 0;
        i_rastakkum = 0;
        i_mask = i_initmask;

        for (int ly = 0; ly < i_height; ly++)
        {

            for (int lx = 0; lx < i_width; lx++)
            {
                int i_r = p_imgraster.getSample(lx, ly, 0);
                int i_amask = 0;

                if (lg_is_mask) i_amask = p_raster.getSample(lx, ly, 0);

                if (i_r == 0)
                {
                    if (lg_is_mask)
                    {
                        if (i_amask == 0xFF)
                        {
                            i_akkum |= i_mask;
                        }
                    }
                    else
                        i_akkum |= i_mask;
                }

                if (lg_is_mask && i_amask == 0xFF)
                {
                    i_rastakkum |= i_mask;
                }


                i_cntr++;
                i_mask >>>= 1;
                if (i_cntr == i_maxpixels)
                {
                    i_cntr = 0;
                    i_mask = i_initmask;

                    ab_rasterarray[i_indx] = (byte) i_akkum;

                    if (lg_is_mask)
                    {
                        ab_maskarray[i_indx] = (byte) i_rastakkum;
                    }

                    i_indx++;
                    i_rastakkum = 0;
                    i_akkum = 0;
                }
            }
        }
        if (i_cntr != 0)
        {
            ab_rasterarray[i_indx] = (byte) i_akkum;

            if (lg_is_mask)
            {
                ab_maskarray[i_indx] = (byte) i_rastakkum;
            }
        }

        byte[] ab_packimgarray = null;
        byte[] ab_packmaskarray = null;

        ab_packimgarray = RLEcompress(ab_rasterarray);
        if (ab_rasterarray.length <= 20 || (ab_packimgarray.length > (ab_rasterarray.length - ab_rasterarray.length / 3)))
        {
            ab_packimgarray = null;
        }

        if (lg_is_mask)
        {
            ab_packmaskarray = RLEcompress(ab_maskarray);
            if (ab_maskarray.length <= 20 || (ab_packmaskarray.length > (ab_maskarray.length - ab_maskarray.length / 3)))
            {
                ab_packmaskarray = null;
            }
        }

        // writting command byte
        int i_byte = 0;
        if (ab_packimgarray != null) i_byte |= 0x80;

        if (lg_is_mask)
        {
            i_byte |= 0x20;
            if (ab_packmaskarray != null) i_byte |= 0x40;
        }
        _outputstream.writeByte(i_byte);

        // Writting width and height of the image
        _outputstream.writeByte(i_width);
        _outputstream.writeByte(i_height);

        // Writing image
        if (ab_packimgarray != null)
        {
            // Writting length of the packed data in  bytes
            if (ab_packimgarray.length > 0x7F)
            {
                _outputstream.writeByte(0x80 | ((ab_packimgarray.length >>> 8) & 0xFF));
                _outputstream.writeByte(ab_packimgarray.length & 0xFF);
            }
            else
            {
                _outputstream.writeByte(ab_packimgarray.length);
            }

            _outputstream.write(ab_packimgarray);
        }
        else
        {
            _outputstream.write(ab_rasterarray);
        }

        // Writing mask
        if (ab_packmaskarray != null)
        {
            // Writting length of the packed data in  bytes
            if (ab_packmaskarray.length > 0x7F)
            {
                _outputstream.writeByte(0x80 | ((ab_packmaskarray.length >>> 8) & 0xFF));
                _outputstream.writeByte(ab_packmaskarray.length & 0xFF);
            }
            else
            {
                _outputstream.writeByte(ab_packmaskarray.length & 0xFF);
            }

            _outputstream.write(ab_packmaskarray);
        }
        else
        {
            if (lg_is_mask)
            {
                _outputstream.write(ab_maskarray);
            }
        }

        _outputstream.flush();
    }

    protected static void packWaveletImage(byte[] _imagearray, int _packlevel, DataOutputStream _out) throws IOException
    {
        WaveletCodec p_wc = new WaveletCodec();
        byte[] ab_array = p_wc.encodeImage(_imagearray, _packlevel);

        _out.writeShort(ab_array.length);
        _out.write(ab_array);

        System.out.println("Wavelet packed size = " + ab_array.length);

        _out.flush();
    }

    protected static byte[] repackGifToPNG(byte[] _gifarray, int _packlevel, boolean _alphachannel, Palette _palette, boolean _removepalette)
    {
        Image i_img = Toolkit.getDefaultToolkit().createImage(_gifarray);
        p_mtracker.addImage(i_img, 0);

        try
        {
            p_mtracker.waitForID(0);
        }
        catch (InterruptedException e)
        {
            return null;
        }

        p_mtracker.removeImage(i_img);

        ByteArrayOutputStream p_is = new ByteArrayOutputStream(_gifarray.length);
        PNGEncoder p_encoder = new PNGEncoder(i_img, p_is, _palette, _removepalette);
        p_encoder.setCompressionLevel(_packlevel);
        p_encoder.setAlpha(_alphachannel);

        try
        {
            p_encoder.encodeImage();
        }
        catch (IOException e)
        {
            System.out.println(e.getMessage());
            System.exit(1);
        }

        return p_is.toByteArray();
    }

    // Comp level can be 0,1,2
    // 0 - not compressed
    // 1 - RLE compressed
    // 2 - full screen without compression (with filter values)
    // 3 - RLE compressed full screen (with filter values)
    protected static void packingForD8BPP(byte[] _inarray, DataOutputStream _outputstream, Palette _palette, int _complevel) throws IOException
    {
        if (_palette == null) throw new IOException("The palette is not defined");

        Image p_img = Toolkit.getDefaultToolkit().createImage(_inarray);
        p_mtracker.addImage(p_img, 0);

        try
        {
            p_mtracker.waitForAll();
        }
        catch (InterruptedException e)
        {
            return;
        }

        p_mtracker.removeImage(p_img);

        int i_width = p_img.getWidth(null);
        int i_height = p_img.getHeight(null);

        int[] ai_grabarray = new int[i_width * i_height];
        PixelGrabber p_pixgrab = new PixelGrabber(p_img, 0, 0, i_width, i_height, ai_grabarray, 0, i_width);

        try
        {
            p_pixgrab.grabPixels();
        }
        catch (InterruptedException e)
        {
            return;
        }

        ByteArrayOutputStream p_rast = new ByteArrayOutputStream(i_width * i_height + 2);
        Integer p_alphacolor = null;

        for (int ly = 0; ly < i_height; ly++)
        {
            for (int lx = 0; lx < i_width; lx++)
            {
                int i_argb = ai_grabarray[lx + ly * i_width];
                int i_a = (i_argb >>> 24) & 0xFF;

                int i_rgb = i_argb & 0xFFFFFF;// (i_r << 16)|(i_g<<8)|i_b;

                if (!_palette.containsColor(i_rgb | 0xFF000000))
                {
                    Integer p_equcolor = _palette.getEquColor(i_rgb);
                    if (p_equcolor == null) throw new IOException("Undefined color in the image [" + lx + "," + ly + ",0x" + Integer.toHexString(i_rgb) + "] and I can't select an equivalent color");
//                    System.out.println("WARNING: Color 0x"+Integer.toHexString(i_rgb)+" will be changed to 0x"+Integer.toHexString(p_equcolor.intValue()));
                    int i_newvalue = (i_argb & 0xFF000000) | p_equcolor.intValue();
                    ai_grabarray[lx + ly * i_width] = i_newvalue;
                    i_rgb = i_newvalue & 0xFFFFFF;
                }

                if (i_a != 0xFF)
                {
                    if (p_alphacolor == null)
                    {
                        p_alphacolor = new Integer(i_rgb);
                    }
                    else
                    {
                        if (p_alphacolor.intValue() != i_rgb)
                            throw new IOException("There are too many transparent colors in the image");
                    }

                    int i_indx = _palette.getColorIndex(i_rgb);
                    p_rast.write(i_indx);
                }
                else
                {
                    int i_indx = _palette.getColorIndex(i_rgb | 0xFF000000);
                    p_rast.write(i_indx);
                }
            }
        }

        if (p_alphacolor != null)
        {
            int i_indx = _palette.getColorIndex(p_alphacolor.intValue());
            _outputstream.writeByte(1 | (_complevel << 4));
            _outputstream.writeByte(i_indx);
        }
        else
        {
            _outputstream.writeByte((_complevel << 4));
        }
        _outputstream.writeByte(i_width);
        _outputstream.writeByte(i_height);

        byte[] ab_allimage = p_rast.toByteArray();
        switch (_complevel)
        {
            case 0:
                // Without any compression
                _outputstream.write(ab_allimage);
                _outputstream.flush();
                break;
            case 1:
                {
                    // RLE compression
                    byte[] ab_line = new byte[i_width];
                    int i_indx = 0;
                    while (i_indx < ab_allimage.length)
                    {
                        System.arraycopy(ab_allimage, i_indx, ab_line, 0, i_width);
                        byte[] ab_rlearray = RLEcompress(ab_line);
                        _outputstream.write(ab_rlearray);
                        i_indx += i_width;
                    }
                }
                ;
                break;
            case 2:
                {
                    // Full screen with filters without any compression
                    byte[] ab_line = new byte[i_width + 1];
                    int i_indx = 0;
                    while (i_indx < ab_allimage.length)
                    {
                        System.arraycopy(ab_allimage, i_indx, ab_line, 1, i_width);
                        ab_line[0] = 0;
                        _outputstream.write(ab_line);
                        i_indx += i_width;
                    }
                }
                ;
                break;
            case 3:
                {
                    // RLE compressed full screen
                    int i_indx = 0;
                    ByteArrayOutputStream p_baos = new ByteArrayOutputStream((i_width + 1) * i_height);
                    byte[] ab_line = new byte[i_width + 1];

                    while (i_indx < ab_allimage.length)
                    {
                        System.arraycopy(ab_allimage, i_indx, ab_line, 1, i_width);
                        ab_line[0] = 0;
                        p_baos.write(ab_line);
                        i_indx += i_width;
                    }

                    ab_line = RLEcompress(p_baos.toByteArray());
                    _outputstream.write(ab_line);
                }
                ;
                break;
            default :
                {
                    if (_complevel >= 47 && _complevel <= 50)
                    {
                        // Wavelet packing
                        packWaveletImage(_inarray, _complevel, _outputstream);
                    }
                    else
                    {
                        System.out.println("Unsupported compression level");
                        System.exit(1);
                    }
                }
        }
        _outputstream.flush();
    }

    protected static void packingForSiemens(byte[] _inarray, DataOutputStream _outputstream) throws IOException
    {
        Image p_img = Toolkit.getDefaultToolkit().createImage(_inarray);

        p_mtracker.addImage(p_img, 0);

        try
        {
            p_mtracker.waitForAll();
        }
        catch (InterruptedException e)
        {
            return;
        }

        p_mtracker.removeImage(p_img);

        int i_width = p_img.getWidth(null);
        int i_height = p_img.getHeight(null);

        BufferedImage p_bimg = new BufferedImage(i_width, i_height, BufferedImage.TYPE_4BYTE_ABGR);
        p_bimg.getGraphics().drawImage(p_img, 0, 0, null);
        BufferedImage p_binimg = new BufferedImage(i_width, i_height, BufferedImage.TYPE_BYTE_BINARY);
        p_binimg.getGraphics().drawImage(p_img, 0, 0, null);

        boolean lg_is_mask = false;
        Raster p_raster = p_bimg.getAlphaRaster();
        for (int ly = 0; ly < i_height; ly++)
        {
            for (int lx = 0; lx < i_width; lx++)
            {
                if (p_raster.getSample(lx, ly, 0) != 0xFF)
                {
                    lg_is_mask = true;
                    break;
                }
            }
            if (lg_is_mask) break;
        }

        // Creating raster for image
        int i_alignwidth = i_width >>> 3;
        if ((i_width % 8) != 0) i_alignwidth++;

        // Filling of image array
        int i_akkum = 0;
        int i_rastakkum = 0;
        int i_cntr = 0;
        int i_mask = 0;

        Raster p_imgraster = p_binimg.getRaster();
        ByteArrayOutputStream p_baos = new ByteArrayOutputStream(i_alignwidth * i_height * 2);

        int i_maxpixels = 8;
        int i_initmask = 0x80;
        if (lg_is_mask)
        {
            i_maxpixels = 4;
            i_initmask = 0x8;
        }

        for (int ly = 0; ly < i_height; ly++)
        {
            i_akkum = 0;
            i_rastakkum = 0;
            i_mask = i_initmask;
            i_cntr = 0;

            for (int lx = 0; lx < i_width; lx++)
            {
                int i_r = p_imgraster.getSample(lx, ly, 0);
                int i_amask = 0;

                if (lg_is_mask) i_amask = p_raster.getSample(lx, ly, 0);

                if (i_r == 0)
                {
                    if (lg_is_mask)
                    {
                        if (i_amask == 0xFF)
                        {
                            i_akkum |= i_mask;
                        }
                    }
                    else
                        i_akkum |= i_mask;
                }

                if (lg_is_mask && i_amask == 0xFF)
                {
                    i_rastakkum |= i_mask;
                }


                i_cntr++;
                i_mask >>>= 1;
                if (i_cntr == i_maxpixels)
                {
                    i_cntr = 0;
                    i_mask = i_initmask;

                    if (lg_is_mask)
                    {
                        int i_xmask = 0x8;
                        int i_xakkum = 0;
                        for (int li = 0; li < 4; li++)
                        {
                            if ((i_rastakkum & i_xmask) != 0)
                            {
                                if ((i_akkum & i_xmask) != 0)
                                {
                                    i_xakkum <<= 1;
                                    i_xakkum |= 1;
                                    i_xakkum <<= 1;
                                }
                                else
                                {
                                    i_xakkum <<= 2;
                                    i_xakkum |= 1;
                                }
                            }
                            else
                            {
                                i_xakkum <<= 2;
                            }
                            i_xmask >>>= 1;
                        }
                        p_baos.write(i_xakkum);
                    }
                    else
                    {
                        p_baos.write(i_akkum);
                    }

                    i_rastakkum = 0;
                    i_akkum = 0;
                }
            }
            if (i_cntr != 0)
            {
                if (lg_is_mask)
                {
                    int i_xmask = 0x8;
                    int i_xakkum = 0;
                    for (int li = 0; li < 4; li++)
                    {
                        if ((i_rastakkum & i_xmask) != 0)
                        {
                            if ((i_akkum & i_xmask) != 0)
                            {
                                i_xakkum <<= 1;
                                i_xakkum |= 1;
                                i_xakkum <<= 1;
                            }
                            else
                            {
                                i_xakkum <<= 2;
                                i_xakkum |= 1;
                            }
                        }
                        else
                        {
                            i_xakkum <<= 2;
                        }
                        i_xmask >>>= 1;
                    }
                    p_baos.write(i_xakkum);
                }
                else
                {
                    p_baos.write(i_akkum);
                }
            }
        }

        p_baos.flush();

        byte[] ab_imgarray = p_baos.toByteArray();

        int i_abslen = i_alignwidth * i_height;
        byte[] ab_packimgarray = RLEcompress(ab_imgarray);
        if (ab_imgarray.length <= 20 || (ab_packimgarray.length > (i_abslen - i_abslen / 3)))
        {
            ab_packimgarray = null;
        }

        // writting command byte
        int i_byte = 0;
        if (ab_packimgarray != null) i_byte |= 0x80;

        if (lg_is_mask)
        {
            i_byte |= 0x20;
        }
        _outputstream.writeByte(i_byte);

        // Writting width and height of the image
        _outputstream.writeByte(i_width);
        _outputstream.writeByte(i_height);

        // Writing image
        if (ab_packimgarray != null)
        {
            // Writting length of the packed data in  bytes
            if (ab_packimgarray.length > 0x7F)
            {
                _outputstream.writeByte(0x80 | ((ab_packimgarray.length >>> 8) & 0xFF));
                _outputstream.writeByte(ab_packimgarray.length & 0xFF);
            }
            else
            {
                _outputstream.writeByte(ab_packimgarray.length & 0xFF);
            }

            _outputstream.write(ab_packimgarray);
        }
        else
        {
            _outputstream.write(ab_imgarray);
        }
        _outputstream.flush();
    }

    public static final byte[] convertPalette2PNGchunk(Integer [] ap_colors)
    {
      // Writing the palette in the PNG format
      byte[] ab_palettearray = new byte[ap_colors.length * 3 + 12];
      int i_len = ap_colors.length * 3;
      ab_palettearray[0] = (byte) ((i_len >>> 24) & 0xFF);
      ab_palettearray[1] = (byte) ((i_len >>> 16) & 0xFF);
      ab_palettearray[2] = (byte) ((i_len >>> 8) & 0xFF);
      ab_palettearray[3] = (byte) (i_len & 0xFF);

      ab_palettearray[4] = (byte) 'P';
      ab_palettearray[5] = (byte) 'L';
      ab_palettearray[6] = (byte) 'T';
      ab_palettearray[7] = (byte) 'E';

      int i_palindx = 8;
      for (int li = 0; li < ap_colors.length; li++)
      {
          int i_rgb = ap_colors[li].intValue();
          byte i_r = (byte) ((i_rgb >>> 16) & 0xFF);
          byte i_g = (byte) ((i_rgb >>> 8) & 0xFF);
          byte i_b = (byte) (i_rgb & 0xFF);

          ab_palettearray[i_palindx++] = i_r;
          ab_palettearray[i_palindx++] = i_g;
          ab_palettearray[i_palindx++] = i_b;
      }

      CRC32 p_crc32gen = new CRC32();
      p_crc32gen.reset();
      p_crc32gen.update(ab_palettearray, 4, ab_palettearray.length - 8);

      i_len = (int) p_crc32gen.getValue();
      ab_palettearray[i_palindx++] = (byte) ((i_len >>> 24) & 0xFF);
      ab_palettearray[i_palindx++] = (byte) ((i_len >>> 16) & 0xFF);
      ab_palettearray[i_palindx++] = (byte) ((i_len >>> 8) & 0xFF);
      ab_palettearray[i_palindx++] = (byte) (i_len & 0xFF);
      return ab_palettearray;
    }

    public static final byte[] RLEcompress(byte[] inarray)
    {
        System.gc();
        ByteArrayOutputStream baos = new ByteArrayOutputStream(inarray.length);

        int inindx = 0;
        while (inindx < inarray.length)
        {
            int value = inarray[inindx++] & 0xFF;
            int count = 1;
            while (inindx < inarray.length)
            {
                if ((inarray[inindx] & 0xFF) == value)
                {
                    count++;
                    inindx++;
                    if (count == 63) break;
                }
                else
                    break;
            }
            if (count > 1)
            {
                baos.write(count | 0xC0);
                baos.write(value);
            }
            else
            {
                if (value > 63)
                {
                    baos.write(0xC1);
                    baos.write(value);
                }
                else
                    baos.write(value);
            }
        }

        byte[] outarray = baos.toByteArray();
        baos = null;
        System.gc();
        return outarray;
    }

}