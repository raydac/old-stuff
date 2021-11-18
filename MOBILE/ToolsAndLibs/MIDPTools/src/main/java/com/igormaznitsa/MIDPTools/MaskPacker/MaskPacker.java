package com.igormaznitsa.MIDPTools.MaskPacker;

import java.awt.*;
import java.awt.image.PixelGrabber;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;
import java.util.Enumeration;
import java.util.Vector;

/*
* Copyright (C) 2002 Igor A. Maznitsa (igor.maznitsa@igormaznitsa.com).  All rights reserved.
* Software is confidential and copyrighted. Title to Software and all associated intellectual property rights
* is retained by Igor A. Maznitsa. You may not make copies of Software, other than a single copy of Software for
* archival purposes.  Unless enforcement is prohibited by applicable law, you may not modify, decompile, or reverse
* engineer Software.  You acknowledge that Software is not designed, licensed or intended for use in the design,
* construction, operation or maintenance of any nuclear facility. Igor A. Maznitsa disclaims any express or implied
* warranty of fitness for such uses.
*/

public class MaskPacker
{
    private static final int PACKING_BYTE = 0;
    private static final int PACKING_SHORT = 1;
    private static final int PACKING_INT = 2;
    private static final int PACKING_LONG = 3;

    private static void outHelp()
    {
        System.out.println("MaskPacker utility.\r\n(C) 2002 All Copyright by Igor Maznitsa");
        System.out.println("\r\nCommand string:");
        System.out.println("com.igormaznitsa.MIDPTools.MaskPacker.MaskPacker [/B] [/C] [/I] [/L] [/T:B|S|I|L] [/D:directory_name] [package_name]");
        System.out.println("/B - back order of bits\r\n/I - invert data\r\n/C - use color data from the image (default use transparency data)\r\n/L - every line in new string(default none)\r\n/T:B - byte packing\r\n/T:S - short packing\r\n/T:I - int packing (default)\r\n/T:L - long packing");
    }

    private static String convertImage(Image _img, int _type, boolean _everylineinnewstring, boolean _usecolor, boolean _inverted, boolean _backorder)
    {
        int i_width = _img.getWidth(null);
        int i_height = _img.getHeight(null);

        if (_img == null) return null;

        int[] ai_imgarr = new int[i_width * i_height];
        PixelGrabber pxg = new PixelGrabber(_img, 0, 0, i_width, i_height, ai_imgarr, 0, i_width);
        try
        {
            pxg.grabPixels();
        }
        catch (InterruptedException exx)
        {
            return null;
        }

        for (int li = 0; li < ai_imgarr.length; li++)
        {
            int a = (ai_imgarr[li] & 0xFF000000) >>> 24;
            int r = (ai_imgarr[li] & 0x00FF0000) >>> 16;
            int g = (ai_imgarr[li] & 0x0000FF00) >>> 8;
            int b = ai_imgarr[li] & 0x000000FF;

            int new_ = Math.round(0.3f * r + 0.59f * g + 0.11f * b);

            if (_usecolor)
            {
                if (new_ >= 0x7F)
                    ai_imgarr[li] = 0x7FFFFFFF;
                else
                    ai_imgarr[li] = 0x0;
            }
            else
            {
                if (a == 0xFF)
                    ai_imgarr[li] = 0x7FFFFFFF;
                else
                    ai_imgarr[li] = 0x0;
            }
        }

        int i_b_width = i_width / 8;
        if (i_width % 8 != 0) i_b_width++;

        int i_maxbyte = 0;
        String s_prefix = null;
        switch (_type)
        {
            case PACKING_BYTE:
                {
                    i_maxbyte = 1;
                    s_prefix = "(byte)0x";
                }
                ;
                break;
            case PACKING_SHORT:
                {
                    i_maxbyte = 2;
                    s_prefix = "(short)0x";
                }
                ;
                break;
            case PACKING_INT:
                {
                    i_maxbyte = 4;
                    s_prefix = "(int)0x";
                }
                ;
                break;
            case PACKING_LONG:
                {
                    i_maxbyte = 8;
                    s_prefix = "(long)0x";
                }
                ;
                break;
        }

        long l_akkum;
        long l_startmask = 0;

        if (_backorder)
            l_startmask = 1L;
        else
            l_startmask = (1L << (i_maxbyte * 8 - 1));

        boolean lg_nofirst = false;

        String l_str = "line width (in unit) = " + (i_b_width / i_maxbyte) + "\r\n" + "height (in line) = " + i_height + "\r\n\r\n";

        for (int ly = 0; ly < i_height; ly++)
        {
            l_akkum = 0;
            int i_bitcounter = 0;
            for (int lx = 0; lx < i_width; lx++)
            {
                if (ai_imgarr[ly * i_width + lx] != 0)
                {
                    if (_backorder)
                        l_akkum <<= 1;
                    else
                        l_akkum >>>= 1;

                    l_akkum |= l_startmask;
                }
                else
                {
                    if (_backorder)
                        l_akkum <<= 1;
                    else
                        l_akkum >>>= 1;
                }
                i_bitcounter++;
                if (i_bitcounter % (i_maxbyte * 8) == 0)
                {
                    if (lg_nofirst) l_str += ","; else lg_nofirst = true;
                    if (_inverted) l_akkum = l_akkum ^ 0xFFFFFFFFFFFFFFFFL;
                    l_str += s_prefix + Long.toHexString(l_akkum);
                    if (_type == PACKING_LONG) l_str += "L";
                    l_akkum = 0;
                }
            }
            int i_rest = i_bitcounter % (i_maxbyte << 3);
            if (i_rest != 0)
            {
                if (_backorder)
                    l_akkum <<= ((i_maxbyte << 3) - i_rest);
                else
                    l_akkum >>>= ((i_maxbyte << 3) - i_rest);

                if (lg_nofirst) l_str += ","; else lg_nofirst = true;
                if (_inverted) l_akkum = l_akkum ^ 0xFFFFFFFFFFFFFFFFL;
                l_str += s_prefix + Long.toHexString(l_akkum);
                if (_type == PACKING_LONG) l_str += "L";
                l_akkum = 0;
            }
            if (_everylineinnewstring) l_str += "\r\n";
        }
        return l_str;
    }

    public static void main(String[] args)
    {
        String s_directory = ".\\";
        String s_javaname = "mask.java";
        int i_type = PACKING_INT;

/*        if (args == 0)
        {
            outHelp();
            System.exit(0);
        }*/

        Button p_button = new Button();

        boolean lg_everylineinnewstring = false;
        boolean lg_colordata = false;
        boolean lg_invertdata = false;
        boolean lg_backorder = false;

        for (int li = 0; li < args.length; li++)
        {
            String s_buffer = args[li].toLowerCase();
            if (s_buffer.startsWith("/i"))
            {
                lg_invertdata = true;
            }
            else if (s_buffer.startsWith("/d:"))
            {
                s_directory = args[0].substring(3);
            }
            else if (s_buffer.startsWith("/b"))
            {
                lg_backorder = true;
            }
            else if (s_buffer.startsWith("/c"))
            {
                lg_colordata = true;
            }
            else if (s_buffer.startsWith("/l"))
            {
                lg_everylineinnewstring = true;
            }
            else if (s_buffer.startsWith("/t:"))
            {
                if (s_buffer.endsWith("b"))
                    i_type = PACKING_BYTE;
                else if (s_buffer.endsWith("s"))
                    i_type = PACKING_SHORT;
                else if (s_buffer.endsWith("i"))
                    i_type = PACKING_INT;
                else if (s_buffer.endsWith("l"))
                    i_type = PACKING_LONG;
                else
                {
                    outHelp();
                    System.exit(1);
                }
            }
            else if (s_buffer.startsWith("/?"))
            {
                outHelp();
                System.exit(0);
            }
        }

        try
        {

            File p_file = new File(s_directory);
            File[] ap_files = p_file.listFiles();

            Vector p_vector = new Vector();
            Vector p_string = new Vector();
            for (int li = 0; li < ap_files.length; li++)
            {
                p_file = ap_files[li];
                if (!p_file.isFile()) continue;
                String s_name = p_file.getName().toUpperCase();
                if (!s_name.endsWith(".GIF")) continue;
                s_name = s_name.substring(0, s_name.length() - 4);
                s_name.replace(' ', '_');
                String s_maskname = s_name;
/*                switch (i_type)
                {
                    case PACKING_BYTE:
                        s_maskname += "_BYTE";
                        break;
                    case PACKING_INT:
                        s_maskname += "_INT";
                        break;
                    case PACKING_SHORT:
                        s_maskname += "_SHORT";
                        break;
                    case PACKING_LONG:
                        s_maskname += "_LONG";
                        break;
                }*/


                MediaTracker p_trck = new MediaTracker(p_button);
                Image p_nimg = null;

                p_nimg = Toolkit.getDefaultToolkit().getImage(p_file.getAbsolutePath());
                p_trck.addImage(p_nimg, 0);

                try
                {
                    p_trck.waitForAll();
                }
                catch (InterruptedException e)
                {
                    return;
                }

                p_trck.removeImage(p_nimg);

                p_vector.add(s_maskname);
                p_string.add(convertImage(p_nimg, i_type, lg_everylineinnewstring, lg_colordata, lg_invertdata,lg_backorder));
            }

            Enumeration p_enum = p_vector.elements();
            Enumeration p_strings = p_string.elements();

            FileOutputStream p_fos = new FileOutputStream(s_javaname);
            DataOutputStream p_dos = new DataOutputStream(p_fos);
            p_dos.writeBytes("/*\r\nThis is a mask file was generated by the MaskPacker utility\r\nDate : " + new Date(System.currentTimeMillis()) + "\r\n*/\r\n\r\n");

            while (p_enum.hasMoreElements())
            {
                String s_name = (String) p_enum.nextElement();
                String s_content = (String) p_strings.nextElement();

                p_dos.writeBytes("//----------------------------\r\n");
                p_dos.writeBytes(s_name + "\r\n\r\n");
                p_dos.writeBytes(s_content + "\r\n");
            }

            p_dos.flush();
            ;
            p_dos.close();
        }
        catch (IOException ex)
        {
            System.err.println("IOException [" + ex + "]");
            System.exit(1);
        }
        System.exit(0);
    }
}
