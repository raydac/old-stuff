package com.igormaznitsa.Utils;

import com.igormaznitsa.Utils.WDecoder;
import com.igormaznitsa.Utils.LZWCompressor;

import java.awt.*;
import java.awt.image.PixelGrabber;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.HashMap;
import java.util.Iterator;

public class WaveletCodec
{
    public byte [] ab_nonpackedArray;

    public WaveletCodec()
    {
    }

    public byte[] encodeImage(byte[] _imagearray, int _iterations) throws IOException
    {
        return encodeImage(_imagearray, _iterations, true);
    }

    public byte[] encodeImage(byte[] _imagearray, int _iterations, boolean _compress) throws IOException
    {
        int i_width,i_height;

        double[] ad_Yarray = null;
        double[] ad_Uarray = null;
        double[] ad_Varray = null;

        MediaTracker p_mtracker = new MediaTracker(new Button());
        Image p_image = Toolkit.getDefaultToolkit().createImage(_imagearray);
        p_mtracker.addImage(p_image, 0);

        try
        {
            p_mtracker.waitForAll();
        }
        catch (InterruptedException e)
        {
            return null;
        }

        p_mtracker.removeImage(p_image);

        i_width = p_image.getWidth(null);
        i_height = p_image.getHeight(null);

        int i_xw = (i_width & 0x1) == 0 ? i_width : i_width + 1;
        int i_len2 = i_xw * i_height;

        ad_Yarray = new double[i_len2];
        ad_Uarray = new double[i_len2 >>> 1];
        ad_Varray = new double[i_len2 >>> 1];

        for (int li = 0; li < ad_Yarray.length; li++) ad_Yarray[li] = 0d;
        for (int li = 0; li < ad_Uarray.length; li++)
        {
            ad_Uarray[li] = 0d;
            ad_Varray[li] = 0d;
        }
        ;

        int[] ai_pixels = new int[i_width * i_height];

        PixelGrabber p_grabber = new PixelGrabber(p_image, 0, 0, i_width, i_height, ai_pixels, 0, i_width);

        try
        {
            p_grabber.grabPixels();
        }
        catch (InterruptedException e)
        {
            return null;
        }

        double U = 0;
        double V = 0;
        double Y = 0;

        int indxUV = 0;

        for (int ly = 0; ly < i_height; ly++)
        {
            int i_uvoffst = ly * (i_xw >>> 1);
            indxUV = 0;

            for (int lx = 0; lx < i_width; lx++)
            {
                int i_argb = ai_pixels[lx + ly * i_width] & 0x00FFFFFF;
                double i_r = (double) (((i_argb >>> 16) & 0xFF));
                double i_g = (double) (((i_argb >>> 8) & 0xFF));
                double i_b = (double) ((i_argb & 0xFF));

                //float Y = 0.299f * i_r + 0.587f * i_g + 0.114f * i_b;
                Y = 0.299d * i_r + 0.587d * i_g + 0.114d * i_b;//)*1048576;
                ad_Yarray[lx + ly * i_xw] = Y;

                if ((lx & 0x1) != 0)
                {
                    //U = 0.596f * i_r - 0.274f * i_g - 0.322f * i_b;
                    U = (U + 0.596d * i_r - 0.274d * i_g - 0.322d * i_b) / 2d;
                    ad_Uarray[indxUV + i_uvoffst] = U;

                    //V = 0.211f * i_r - 0.523f * i_g + 0.312f * i_b;
                    V = (V + 0.211d * i_r - 0.523d * i_g + 0.312d * i_b) / 2d;
                    ad_Varray[indxUV + i_uvoffst] = V;
                    indxUV++;
                }
                else
                {
                    U = 0.596d * i_r - 0.274d * i_g - 0.322d * i_b;
                    V = 0.211d * i_r - 0.523d * i_g + 0.312d * i_b;
                }
            }
        }

        byte[] o_a = new byte[ad_Yarray.length + ad_Varray.length + ad_Uarray.length];

        int i_arrindx = 0;
        int i_halfwidth = i_xw >>> 1;

        for (int ly = 0; ly < i_height; ly++)
        {
            int i_yoffset = ly * i_xw;
            int i_yoffsetUV = ly * i_halfwidth;

            // Writing Y
            for (int lx = 0; lx < i_xw; lx += 2)
            {
                double f_a1 = ad_Yarray[lx + i_yoffset];
                double f_a2 = ad_Yarray[i_yoffset + lx + 1];

                for (int li = 0; li < _iterations; li++)
                {
                    double f_b1 = (f_a1 + f_a2) / 2d;
                    double f_b2 = (f_a1 - f_a2) / 2d;

                    f_a1 = f_b1;
                    f_a2 = f_b2;
                }

                o_a[i_arrindx++] = (byte) Math.round(f_a1 * 262144D);
                o_a[i_arrindx++] = (byte) Math.round(f_a2 * 262144D);
            }

            // Writing U
            for (int lx = 0; lx < i_halfwidth; lx += 2)
            {
                double f_a1 = ad_Uarray[lx + i_yoffsetUV];
                double f_a2 = ad_Uarray[i_yoffsetUV + lx + 1];

                for (int li = 0; li < _iterations; li++)
                {
                    double f_b1 = (f_a1 + f_a2) / 2d;
                    double f_b2 = (f_a1 - f_a2) / 2d;

                    f_a1 = f_b1;
                    f_a2 = f_b2;
                }

                o_a[i_arrindx++] = (byte) Math.round(f_a1 * 262144D);
                o_a[i_arrindx++] = (byte) Math.round(f_a2 * 262144D);
            }

            // Writing V
            for (int lx = 0; lx < i_halfwidth; lx += 2)
            {
                double f_a1 = ad_Varray[lx + i_yoffsetUV];
                double f_a2 = ad_Varray[i_yoffsetUV + lx + 1];

                for (int li = 0; li < _iterations; li++)
                {
                    double f_b1 = (f_a1 + f_a2) / 2d;
                    double f_b2 = (f_a1 - f_a2) / 2d;

                    f_a1 = f_b1;
                    f_a2 = f_b2;
                }

                o_a[i_arrindx++] = (byte) Math.round(f_a1 * 262144D);
                o_a[i_arrindx++] = (byte) Math.round(f_a2 * 262144D);
            }
        }

        ab_nonpackedArray = o_a;

        // Packing to half bytes
        HashMap p_map = new HashMap();
        int i_indx = 0;
        for (int li = 0; li < o_a.length; li++)
        {
            byte b_value = o_a[li];
            Byte p_key = new Byte(b_value);
            if (!p_map.containsKey(p_key))
            {
                p_map.put(new Byte(b_value), new Integer(i_indx));
                i_indx++;
            }
        }

        if (p_map.size() >= 0xF) throw new IOException("Unique bytes number is more than 0xF [" + p_map.size() + "]");

        ByteArrayOutputStream p_prevpack = new ByteArrayOutputStream();

        for (int li = 0; li < o_a.length; li += 2)
        {
            byte b_value = o_a[li];
            Byte p_key = new Byte(b_value);
            i_indx = ((Integer) p_map.get(p_key)).intValue();

            int i_indx2 = 0;
            if (li + 1 < o_a.length)
            {
                b_value = o_a[li + 1];
                p_key = new Byte(b_value);
                i_indx2 = ((Integer) p_map.get(p_key)).intValue();
            }

            i_indx = ((i_indx << 4) | (i_indx2 & 0xF)) & 0xFF;


            p_prevpack.write(i_indx);
        }

        if (!_compress)
        {
            return o_a;
        }


        LZWCompressor p_compr = new LZWCompressor();
        p_compr.reset();
        ByteArrayOutputStream p_outstream = new ByteArrayOutputStream();
        DataOutputStream p_dos = new DataOutputStream(p_outstream);

        p_compr.compress(new DataInputStream(new ByteArrayInputStream(p_prevpack.toByteArray())), p_dos);
        p_dos.close();
        o_a = p_outstream.toByteArray();

        LZWDecompressor p_decom = new LZWDecompressor();
        p_decom.fullExpand(o_a, 0, new byte[p_prevpack.size()], 0);
        p_decom.i_maxTableSize++;
        p_decom.i_maxStackDepth++;
        System.out.println("MaxDT = " + p_decom.i_maxTableSize + " MaxSD = " + p_decom.i_maxStackDepth);

        byte[] ab_outarray = new byte[o_a.length + 8 + p_map.size()];
        // Writing other values
        ab_outarray[0] = (byte) i_width;
        ab_outarray[1] = (byte) i_height;
        ab_outarray[2] = (byte) _iterations;
        ab_outarray[3] = (byte) p_map.size();

        System.out.println("Local map size = " + p_map.size());

        Iterator p_iter = p_map.keySet().iterator();
        while (p_iter.hasNext())
        {
            Byte p_key = (Byte) p_iter.next();
            int i_value = ((Integer) p_map.get(p_key)).intValue();
            ab_outarray[4 + i_value] = p_key.byteValue();
        }

        int i_off = 4 + p_map.size();

        // Writing the max table size and max stack depth values
        ab_outarray[i_off++] = (byte) (p_decom.i_maxTableSize >>> 8);
        ab_outarray[i_off++] = (byte) p_decom.i_maxTableSize;
        ab_outarray[i_off++] = (byte) (p_decom.i_maxStackDepth >>> 8);
        ab_outarray[i_off++] = (byte) p_decom.i_maxStackDepth;


        System.arraycopy(o_a, 0, ab_outarray, i_off, o_a.length);

        //new FileOutputStream("wavelet.wav").write(ab_outarray);

        return ab_outarray;
    }

    public Image decodeArray(byte[] _array) throws IOException
    {
        int _offset = 0;

        int i_width = _array[_offset++] & 0xFF;
        int i_height = _array[_offset++] & 0xFF;
        int i_iterations = _array[_offset++] & 0xFF;

        //Restoring of image colors from YUV arrays
        Image p_image = new BufferedImage(i_width, i_height, BufferedImage.TYPE_INT_RGB);
        Graphics p_gr = p_image.getGraphics();

        int i_decodeTableLength = _array[_offset++];
        byte[] ab_decodeTable = new byte[i_decodeTableLength];
        int i_indx = 0;
        while (i_indx < i_decodeTableLength)
        {
            ab_decodeTable[i_indx++] = _array[_offset++];
        }

        short[] ash_rgbbuffer = new short[i_width];

        int i_xwidth = (i_width & 0x1) == 0 ? i_width : i_width + 1;

        WDecoder.reset(_array, _offset);

        int i_halfxw = i_xwidth >>> 1;
        int[] i_buffer = new int[i_xwidth + (i_halfxw << 1)];
        int i_buffer_len = i_buffer.length;
        byte[] ab_buffer = new byte[i_buffer_len >> 1];
        int i_bufferB_len = ab_buffer.length;

        int i_y = 0;

        for (int ly = 0; ly < i_height; ly++)
        {
            int i_len = WDecoder.expandBlock(ab_buffer, 0, i_bufferB_len);

            i_indx = 0;
            for (int li = 0; li < i_bufferB_len; li++)
            {
                int i_value = ab_buffer[li] & 0xFF;

                int i_indx0 = i_value >>> 4;
                int i_indx1 = i_value & 0xF;

                int i_val0 = ab_decodeTable[i_indx0];
                int i_val1 = ab_decodeTable[i_indx1];
                i_buffer[i_indx++] = i_val0;
                i_buffer[i_indx++] = i_val1;
            }

            if (i_len < 0) break;
            for (int li = 0; li < i_buffer_len; li += 2)
            {
                int f_b1 = i_buffer[li];
                int f_b2 = i_buffer[li + 1];
                for (int ln = 0; ln < i_iterations; ln++)
                {
                    int f_a1 = f_b1 + f_b2;
                    int f_a2 = f_b1 - f_b2;
                    f_b1 = f_a1;
                    f_b2 = f_a2;
                }
                i_buffer[li] = f_b1;
                i_buffer[li + 1] = f_b2;
            }

            int i_offsetU = i_xwidth;
            int i_offsetV = i_xwidth + i_halfxw;
            long l_u = 0;
            long l_y = 0;
            long l_v = 0;

            long l_u_r = 0;
            long l_u_g = 0;
            long l_u_b = 0;

            long l_v_r = 0;
            long l_v_g = 0;
            long l_v_b = 0;

            long l_ur_plus_l_vr = 0;
            long l_ug_minus_l_vg = 0;
            long l_ub_plus_l_vb = 0;

            for (int i_offsetY = 0; i_offsetY < i_width; i_offsetY++)
            {
                l_y = ((long) i_buffer[i_offsetY]) << 20;

                if ((i_offsetY & 1) == 0)
                {
                    l_u = ((long) i_buffer[i_offsetU++]);
                    l_v = ((long) i_buffer[i_offsetV++]);

                    l_u_r = 1002617L * l_u;
                    l_u_g = 285936L * l_u;
                    l_u_b = 1157355L * l_u;

                    l_v_r = 651617L * l_v;
                    l_v_g = 678229L * l_v;
                    l_v_b = 1783229L * l_v;

                    l_ur_plus_l_vr = l_u_r + l_v_r;
                    l_ug_minus_l_vg = l_u_g + l_v_g;
                    l_ub_plus_l_vb = l_u_b - l_v_b;
                }

                //r = y + .95617 * u + .62143 * v;
                int r = (int) ((l_y + l_ur_plus_l_vr) >> 38);
                // g = y - 0.27269 * u - .64681 * v;
                int g = (int) ((l_y - l_ug_minus_l_vg) >> 38);
                //b = *p++ = (*y++) - 1.10374 * (*u++) + 1.70062 * (*v++);
                int b = (int) ((l_y - l_ub_plus_l_vb) >> 38);

                if (r > 255) r = 255;
                if (g > 255) g = 255;
                if (b > 255) b = 255;

                if (r < 0) r = 0;
                if (g < 0) g = 0;
                if (b < 0) b = 0;

                ash_rgbbuffer[i_offsetY] = (short) (((r & 0xF0) << 4) | (g & 0xF0) | (b >>> 4));

                p_gr.setColor(new Color(r, g, b));
                p_gr.fillRect(i_offsetY, ly, 1, 1);
            }
        }
        ab_decodeTable = null;
        ab_buffer = null;
        ash_rgbbuffer = null;
        i_buffer = null;
        Runtime.getRuntime().gc();

        return p_image;
    }

}
