package com.igormaznitsa.DynamicEngine;

import javax.microedition.lcdui.Image;
import javax.microedition.lcdui.Graphics;

public class ImageScaler
{
    public static Image scaleImageToNewImage(Image src, int dstW, int dstH)
    {
        int srcW = src.getWidth();
        int srcH = src.getHeight();

        Image tmp = Image.createImage(dstW, srcH);
        Graphics g = tmp.getGraphics();

        int delta = (srcW << 8) / dstW;
        int pos = delta >> 1;
        int pos_2 = pos >> 8;
        for (int x = 0; x < dstW; x++)
        {
            g.setClip(x, 0, 1, srcH);
            g.drawImage(src, x - pos_2, 0, 0);
            pos += delta;
        }

        Image dst = Image.createImage(dstW, dstH);
        g = dst.getGraphics();

        delta = (srcH << 8) / dstH;
        pos = delta >> 1;
        pos_2 = pos >> 8;

        for (int y = 0; y < dstH; y++)
        {
            g.setClip(0, y, dstW, 1);
            g.drawImage(tmp, 0, y - pos_2, 0);
            pos += delta;
        }
        return dst;
    }

    /**
     * To draw an image onto another image with the scale effect
     * @param _img The image for scaling
     * @param _dst the destination image
     * @param _x the x coordinate of output of the scaled image
     * @param _y the y coordinate of output of the scaled image
     * @param _width  the width of the scaled image
     * @param _height the height of the scaled image
     */
    public static void drawScaledImage(Image _img, Image _dst, int _x, int _y, int _width, int _height)
    {
        int i_srcW = _img.getWidth();
        int i_srcH = _img.getHeight();

        Graphics p_g = _dst.getGraphics();

        int i_deltaX = (i_srcW << 8) / _width;
        int i_initposX = i_deltaX >> 2;

        int i_deltaY = (i_srcH << 8) / _height;
        int i_posY = i_deltaY >> 2;

        int i_endX = _x + _width;
        int i_endY = _y + _height;

        for (int y = _y; y < i_endY; y++)
        {
            int i_posX = i_initposX;
            for (int x = _x; x < i_endX; x++)
            {
                p_g.setClip(x, y, 1, 1);
                p_g.drawImage(_img, x - (i_posX >> 8), y - (i_posY >> 8), 0);
                i_posX += i_deltaX;
            }
            i_posY += i_deltaY;
        }
    }
}
