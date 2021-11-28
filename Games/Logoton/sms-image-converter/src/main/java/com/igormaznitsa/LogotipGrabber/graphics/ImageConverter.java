/*
    Author : Igor A. Maznitsa
    EMail : rrg@forth.org.ru
    Date : 24.03.2002
*/
package com.igormaznitsa.LogotipGrabber.graphics;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.awt.*;


public class ImageConverter
{
//=====OTB Image
    public static final int OTB_IF_CONCATENATIONFLAG = 0x80;
    public static final int OTB_IF_COMPRESSION = 0x40;
    public static final int OTB_IF_EXTERNALPALETTE = 0x20;
    public static final int OTB_IF_MAXSIZEOFICON = 0x10;
    public static final int OTB_IF_NUMBEROFANIMATEDICONS = 0x0F;

    public static final int OTB_EF0_CONCATENATIONFLAG = 0x80;
    public static final int OTB_EF0_BITMAPVER_r4 = 0x70;
    public static final int OTB_EF0_RESERVED = 0x0F;

    public static final int OTB_EF1_CONCATENATIONFLAG = 0x80;
    public static final int OTB_EF1_RESERVED = 0x7F;

    public static boolean checkColor(int color1, int color2)
    {
        color1 = color1 & 0xE0E0E0;
        color2 = color2 & 0xE0E0E0;
        return color1 == color2;
    }

    public static int reverseInt(int in)
    {
        return ((in & 0xFF) << 24) | ((in & 0xFF00) << 8) | ((in & 0xFF0000) >> 8) | ((in & 0xFF000000) >> 24);
    }

    public static short reverseShort(short in)
    {
        return (short) (((in & 0xFF) << 8) | ((in & 0xFF00) >> 8));
    }

    public static byte[] encodeOTBImage(int[] imgarr, int wdth, int hght) throws IOException
    {
        //System.out.println("w=" + wdth + " h=" + hght);

        ByteArrayOutputStream barr = new ByteArrayOutputStream(140);
        DataOutputStream baos = new DataOutputStream(barr);
        // Creating Infofield
        int inffld = 0;
        boolean longWH = false;
        if ((wdth > 0xFF) | (hght > 0xFF))
        {
            longWH = true;
            inffld |= OTB_IF_MAXSIZEOFICON;
        }
        baos.write(inffld);
        if (longWH)
        {
            baos.writeShort(wdth);
            baos.writeShort(hght);
        }
        else
        {
            baos.write(wdth);
            baos.write(hght);
        }

        // Color depth
        baos.write(01);

        for (int ly = 0; ly < hght; ly++)
        {
            int lbx = 0;
            bry:
			{
                int lxl = wdth >> 3;
                if ((wdth & 0x07) != 0) lxl++;
                for (int lx = 0; lx < lxl; lx++)
                {
                    int lms = 0x80;
                    int lba = 0;
                    for (int lmr = 0; lmr < 8; lmr++)
                    {
                        if ((lx << 3) + lmr < wdth)
                        {
                            if (imgarr[(lx << 3) + lmr + ly * wdth] == 0x00)
                            {
                                lba |= lms;
                            }
                        }
                        lms = lms >>> 1;
                        lbx++;
                        if (lbx == wdth)
                        {
                            baos.write(lba);
                            break bry;
                        }
                    }
                    baos.write(lba);
                }
            }
        }

        byte[] newarr = barr.toByteArray();

        //System.out.println(newarr.length);

        return newarr;
    }

//=======BMP image
    public int bfSize;
    public int bfOffbits;
    public int biSize;
    public int biWidth;
    public int biHeight;
    public short biPlanes;
    public short biBitCount;
    public int biCompression;
    public int biSizeImage;
    public int biXPelsPerMeter;
    public int biYPelsPerMeter;
    public int biClrUsed;
    public int biClrImportant;

    public static byte[] encodeBMPImage(int[] inimg, int wdth, int hght) throws IOException
    {
        int lwidth = wdth;
        int lw = lwidth >> 3;
        if ((lwidth & 0x07)!=0) lw++;

        switch(lw &3)
        {
                case 1:	lw += 3;break;
                case 2:	lw += 2;break;
                case 3:	lw ++ ;break;
        }

        byte[] imgdata = new byte[lw * hght];

        byte accum = 0;
        int lry = 0;

        for (int ly = hght - 1; ly >= 0; ly--)
        {
                for (int lx = 0; lx < lw; lx++)
                {
                    int lmask = 0x80;
                    accum = 0;
                    for (int lmc = 0; lmc < 8; lmc++)
                    {
                        if ((lmc + (lx << 3)) < wdth)
                        {
                            if (inimg[(lry * wdth) + lmc + (lx << 3)] == 0x00)
                            {
                                accum |= lmask;
                            }
                        }
                        lmask = lmask >>> 1;
                    }

                    try
                    {
                        imgdata[ly * lw + lx] = (byte) accum;
                    }
                    catch (Exception ex)
                    {
                        System.out.println("ly=" + ly + " lx=" + lx + " lw=" + lw);
                    }
                }
            lry++;
        }

        ByteArrayOutputStream bos = new ByteArrayOutputStream(140);
        DataOutputStream dos = new DataOutputStream(bos);

        // File type
        dos.writeShort(0x424d);
        // Size of the file
        dos.writeInt(reverseInt(62 + imgdata.length));
        // Reserved
        dos.writeInt(0);
        // Offset of the image data
        dos.writeInt(reverseInt(62));
        // Size of the header
        dos.writeInt(reverseInt(40));
        // Width of the image
        dos.writeInt(reverseInt(wdth));
        // Height of the image
        dos.writeInt(reverseInt(hght));
        // Color planes count
        dos.writeShort(reverseShort((short) 1));
        // Bits per pixel
        dos.writeShort(reverseShort((short) 1));
        // Compression
        dos.writeInt(0);
        // Size of bitmap
        dos.writeInt(0);
        // Horizontal resolution
        dos.writeInt(reverseInt(24));
        // Vertical resolution
        dos.writeInt(reverseInt(24));
        // Color used
        dos.writeInt(reverseInt(0));
        // Colors important
        dos.writeInt(reverseInt(2));

        // Color palette
        // Color 1
        dos.write(102);
        dos.write(204);
        dos.write(102);
        dos.write(0);

        // Color 2
        dos.write(0);
        dos.write(0);
        dos.write(0);
        dos.write(0);

        dos.write(imgdata);
        dos.flush();
        return bos.toByteArray();
    }

//==============GIF image
    public static byte[] encodeGIFImage(int[] image, int wdth, int hght, boolean interl, Color transcolor) throws IOException
    {
        ByteArrayOutputStream baos = new ByteArrayOutputStream(140);
        DataOutputStream dos = new DataOutputStream(baos);
        boolean interlaced = interl;
        int imgwidth = (short) wdth;
        int imgheight = (short) hght;

        int trnscolor = -1;
        int trnsRGB = -1;
        if (transcolor != null)
        {
            trnsRGB = transcolor.getRGB() & 0xFFFFFF;
        }

        byte abyte0[][] = new byte[imgwidth][imgheight];
        byte abyte1[][] = new byte[imgwidth][imgheight];
        byte abyte2[][] = new byte[imgwidth][imgheight];
        int i = 0;
        byte[] pixels_ = new byte[imgwidth * imgheight];
        byte[] colors_ = new byte[768];
        int ai1[] = new int[256];
        int l = 0;
        for (int i1 = 0; i1 < imgheight; i1++)
        {
            for (int j1 = 0; j1 < imgwidth; j1++)
            {
                int k = image[i] & 0xffffff;
                if (trnsRGB >= 0)
                {
                    if (trnscolor < 0)
                    {
                        if (trnsRGB == k) trnscolor = l;
                    }
                }
                abyte0[j1][i1] = (byte) (k >>> 16);
                abyte1[j1][i1] = (byte) (k >>> 8);
                abyte2[j1][i1] = (byte) k;
                int j;
                for (j = 0; j < l; j++)
                    if (k == ai1[j])
                        break;

                if (j > 255)
                    System.err.println("Too many colors.");
                pixels_[i1 * imgwidth + j1] = (byte) j;
                if (j == l)
                {
                    ai1[j] = k;
                    colors_[j *= 3] = abyte0[j1][i1];
                    colors_[++j] = abyte1[j1][i1];
                    colors_[++j] = abyte2[j1][i1];
                    l++;
                }
                i++;
            }

        }

        int numColors_ = 1 << BitUtils.BitsNeeded(l);
        byte abyte3[] = new byte[numColors_ * 3];
        System.arraycopy(colors_, 0, abyte3, 0, numColors_ * 3);
        colors_ = abyte3;
        try
        {
            BitUtils.WriteString(dos, "GIF89a");
            GIFScreenDescriptor screendescriptor = new GIFScreenDescriptor((short) imgwidth, (short) imgheight, numColors_);
            screendescriptor.Write(dos);
            dos.write(colors_, 0, colors_.length);
            if (trnscolor < 0) trnscolor = 0;
            GIFGraphicControlExtension ext = new GIFGraphicControlExtension(trnscolor);
            ext.Write(dos);
            GIFImageDescriptor imagedescriptor = new GIFImageDescriptor((short) imgwidth, (short) imgheight, ',', interlaced);
            imagedescriptor.Write(dos);
            byte byte0 = BitUtils.BitsNeeded(numColors_);
            if (byte0 == 1) byte0++;
            dos.write(byte0);
            LZWCompressor.LZWCompress((OutputStream) dos, byte0, pixels_);
            dos.write(0);
//            imagedescriptor = new GIFImageDescriptor((short)0, (short)0, ';', interlaced);
//            imagedescriptor.Write(dos);
            dos.write((byte) ';');
            dos.flush();
        }
        catch (IOException _ex)
        {
        }

        return baos.toByteArray();
    }
}
