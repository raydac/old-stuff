/*
    Author : Igor A. Maznitsa
    EMail : rrg@forth.org.ru
    Date : 24.03.2002
*/
package com.igormaznitsa.LogotipGrabber;

import java.applet.Applet;
import java.awt.*;
import java.awt.image.PixelGrabber;

public class Filters
{
    public static int RGBtoBW(int r, int g, int b)
    {
        return Math.round(0.3f * r + 0.59f * g + 0.11f * b);
    }

    public static int[] contrastGray(int[] grayimage, int level)
    {
        if (grayimage == null) return null;

        int[] nimage = new int[grayimage.length];

        for (int li = 0; li < grayimage.length; li++)
        {
            int val = 0xFF - grayimage[li];

            double lv = 1d - (Math.abs(level) / 1000d);

            double vl = val;

            if (level < 0)
            {
                vl = (double) val * lv;
            }
            else if (level > 0)
            {
                vl = (double) val / lv;
                if (vl > 0xFF) vl = 0xFF;
            }
            val = 0xFF - (int) vl;

            nimage[li] = val;
        }

        return nimage;
    }

    public static int[] ColorToGray(Image img)
    {
        if (img == null) return null;

        int[] imgarr = new int[img.getWidth(null) * img.getHeight(null)];
        PixelGrabber pxg = new PixelGrabber(img, 0, 0, img.getWidth(null), img.getHeight(null), imgarr, 0, img.getWidth(null));
        try
        {
            pxg.grabPixels();
        }
        catch (InterruptedException exx)
        {
            return null;
        }

        for (int li = 0; li < imgarr.length; li++)
        {
            int r = (imgarr[li] & 0x00FF0000) >> 16;
            int g = (imgarr[li] & 0x0000FF00) >> 8;
            int b = imgarr[li] & 0x000000FF;

            int new_ = Math.round(0.3f * r + 0.59f * g + 0.11f * b);

            imgarr[li] = new_;
        }

        return imgarr;
    }

    public static int[] filterDIFFUSION(int grayimg[], int width, int level)
    {
        int height = grayimg.length / width;

        double[] image = new double[grayimg.length];

        for (int li = 0; li < grayimg.length; li++) image[li] = grayimg[li];

        double d = 42D;
        for (int i1 = 0; i1 < height; i1++)
        {
            for (int j1 = 0; j1 < width; j1++)
            {
                double d1 = image[i1 * width + j1];

                if (image[i1 * width + j1] > (double) level)
                    image[i1 * width + j1] = 255D;
                else
                    image[i1 * width + j1] = 0.0D;

                double d2 = d1 - image[i1 * width + j1];
                if (j1 < width - 1) image[i1 * width + j1 + 1] = image[i1 * width + j1 + 1] + (8D / d) * d2;
                if (j1 < width - 2) image[i1 * width + j1 + 2] = image[i1 * width + j1 + 2] + (4D / d) * d2;
                if (i1 < height - 1 && j1 > 1) image[((i1 + 1) * width + j1) - 2] = image[((i1 + 1) * width + j1) - 2] + (2D / d) * d2;
                if (i1 < height - 1 && j1 > 0) image[((i1 + 1) * width + j1) - 1] = image[((i1 + 1) * width + j1) - 1] + (4D / d) * d2;
                if (i1 < height - 1) image[(i1 + 1) * width + j1] = image[(i1 + 1) * width + j1] + (8D / d) * d2;
                if (i1 < height - 1 && j1 < width - 1) image[(i1 + 1) * width + j1 + 1] = image[(i1 + 1) * width + j1 + 1] + (4D / d) * d2;
                if (i1 < height - 1 && j1 < width - 2) image[(i1 + 1) * width + j1 + 2] = image[(i1 + 1) * width + j1 + 2] + (2D / d) * d2;
                if (i1 < height - 2 && j1 > 1) image[((i1 + 2) * width + j1) - 2] = image[((i1 + 2) * width + j1) - 2] + (1.0D / d) * d2;
                if (i1 < height - 2 && j1 > 0) image[((i1 + 2) * width + j1) - 1] = image[((i1 + 2) * width + j1) - 1] + (2D / d) * d2;
                if (i1 < height - 2) image[(i1 + 2) * width + j1] = image[(i1 + 2) * width + j1] + (4D / d) * d2;
                if (i1 < height - 2 && j1 < width - 1) image[(i1 + 2) * width + j1 + 1] = image[(i1 + 2) * width + j1 + 1] + (2D / d) * d2;
                if (i1 < height - 2 && j1 < width - 2) image[(i1 + 2) * width + j1 + 2] = image[(i1 + 2) * width + j1 + 2] + (1.0D / d) * d2;
            }

        }

        int[] bwpict = new int[grayimg.length];

        for (int k1 = 0; k1 < bwpict.length; k1++)
        {
            int l1 = (int) image[k1];
            if (l1 > level)
                bwpict[k1] = 0xFFFFFF;
            else
                bwpict[k1] = 0x000000;
        }

        image = null;

        return bwpict;
    }

    public static int[] filterDITHER(int[] grayarray, int wdth)
    {
        if (grayarray == null) return null;
        int[] newarray = new int[grayarray.length];

        int lhght = grayarray.length / wdth;

        int[] matrix = new int[]{0, 2, 3, 1};

        for (int ly = 0; ly < lhght; ly++)
        {
            int lw = wdth * ly;
            int lj = ly % 2;
            for (int lx = 0; lx < wdth; lx++)
            {
                int li = lx % 2;
                int lcoord = lx + lw;
                if (lcoord > grayarray.length) continue;

                int lt = grayarray[lcoord] / 28;

                if (lt == 0)
                    newarray[lcoord] = 0x000000;
                else if (matrix[li + (lj << 1)] <= lt)
                {
                    newarray[lcoord] = 0xFFFFFF;
                }
                else
                {
                    newarray[lcoord] = 0x000000;
                }
            }
        }

        return newarray;
    }

    public static int[] filterHALFTONE(int[] grayarray, int wdth)
    {
        if (grayarray == null) return null;
        int[] newarray = new int[grayarray.length];

        int ly = 0;

        int lymax =  grayarray.length-wdth;

        while (ly < lymax)
        {
            for (int lx = 0; lx < wdth; lx += 2)
            {
                int lxly = lx + ly;
                int lxlylw = lx + ly + wdth;

                int l0 = grayarray[lxly];
                int l2 = grayarray[lxlylw];

                int l1 = l0;
                int l3 = l2;

                final boolean notEdge = lx + 1 < wdth;

                if (notEdge) {
                    l1 = grayarray[lxly + 1];
                    l3 = grayarray[lxlylw + 1];
                }

                int lsumm = (l0 + l1 + l2 + l3) >> 2;

                if (lsumm > 0xFF) lsumm = 0xFF;

                if (lsumm < 51)
                {
                    newarray[lxly] = 0;
                    newarray[lxlylw] = 0;
                    if (notEdge) {
                        newarray[lxly + 1] = 0;
                        newarray[lxlylw + 1] = 0;
                    }
                }
                else if (lsumm < 102)
                {
                    newarray[lxly] = 0xFFFFFF;
                    newarray[lxlylw] = 0;

                    if (notEdge) {
                        newarray[lxly + 1] = 0;
                        newarray[lxlylw + 1] = 0;
                    }
                }
                else if (lsumm < 153)
                {
                    newarray[lxly] = 0xFFFFFF;
                    newarray[lxlylw] = 0;
                    if (notEdge) {
                        newarray[lxly + 1] = 0;
                        newarray[lxlylw + 1] = 0xFFFFFF;
                    }
                }
                else if (lsumm < 205)
                {
                    newarray[lxly] = 0xFFFFFF;
                    newarray[lxlylw] = 0;
                    if (notEdge) {
                        newarray[lxly + 1] = 0xFFFFFF;
                        newarray[lxlylw + 1] = 0xFFFFFF;
                    }
                }
                else if (lsumm < 256)
                {
                    newarray[lxly] = 0xFFFFFF;
                    newarray[lxlylw] = 0xFFFFFF;
                    if (notEdge) {
                        newarray[lxly + 1] = 0xFFFFFF;
                        newarray[lxlylw + 1] = 0xFFFFFF;
                    }
                }
            }
            ly = ly + (wdth << 1);
        }

        return newarray;
    }

    public static int[] filterLINEART(int[] grayarray)
    {
        if (grayarray == null) return null;
        int[] newarray = new int[grayarray.length];
        for (int li = 0; li < grayarray.length; li++)
        {
            if (grayarray[li] < 128) newarray[li] = 0; else newarray[li] = 0xFFFFFF;
        }

        return newarray;
    }

    public static Image convertBWArrayToImage(Applet apl, int[] bwarray, int wdth, Color white, Color black)
    {
        Image newimage = apl.createImage(wdth, bwarray.length / wdth);

        int ly = 0;
        int lx = 0;

        Graphics g = newimage.getGraphics();

        for (int li = 0; li < bwarray.length; li++)
        {
            if (bwarray[li] != 0)
                g.setColor(white);
            else
                g.setColor(black);

            g.drawLine(lx, ly, lx, ly);

            lx++;
            if (lx == wdth)
            {
                ly++;
                lx = 0;
            }
        }

        return newimage;
    }

}
