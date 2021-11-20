package com.raydac_research.Font;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.io.File;
import java.io.IOException;
import java.io.FileInputStream;

public class GIFFont extends AbstractFont
{
    protected static final MediaTracker pMediaTracker = new MediaTracker(new Button());

    public BufferedImage p_FontImage;
    public int i_charWidth;
    public int i_charHeight;
    public int i_BaseLine;
    public int i_Interval;

    public int i_CharsNumber;

    public static final String CHARSET = "\n 0123456789:.,!?+-/\'\"()ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyzАБВГДЕЖЗИЙКЛМНОПРСТУФХЦЧШЩЪЫЬЭЮЯабвгдежзийклмнопрстуфхцчшщъыьэюя";

    public int getHeight()
    {
        return i_charHeight;
    }

    public int getStringWidth(String _string)
    {
        if (_string == null) return 0;
        return (_string.length()*i_charWidth);
    }

    public int getBaseline()
    {
        return i_BaseLine;
    }

    public int getCharWidth(char _char)
    {
        return i_charWidth;
    }

    public void drawString(Graphics _g, int _x, int _y, String _string)
    {
        Graphics p_graphics = _g;

        int i_len = _string.length();
        int i_clipX = _x;
        for(int li=0;li<i_len;li++)
        {
            int i_char = _string.charAt(li);
            if (i_char==' ')
            {
                i_clipX += i_charWidth;
                continue;
            }
            else
            if (i_char =='\n')
            {
                _y += getHeight();
                i_clipX = _x;
                continue;
            }

            int i_index = CHARSET.indexOf(i_char);
            p_graphics.setClip(i_clipX,_y,i_charWidth,i_charHeight);
            if (i_index<0)
            {
                // Рисуем квадрат с крестиком, в знак что символа нет
                Color p_color = p_graphics.getColor();
                p_graphics.fillRect(i_clipX,0,i_charWidth,i_charHeight);

                p_graphics.setColor(p_color.darker());
                p_graphics.drawLine(i_clipX,0,i_clipX+i_charWidth,i_charHeight);
                p_graphics.drawLine(i_clipX,i_charHeight,i_clipX+i_charWidth,0);
                p_graphics.setColor(p_color);
            }
            else
            {
                // Выводим букву
                p_graphics.drawImage(p_FontImage,i_clipX-(i_charWidth*i_index),_y,null);
            }

            i_clipX += i_charWidth;
        }
    }

    public boolean containsChar(char _char)
    {
        return CHARSET.indexOf(_char)>=0;
    }

    public boolean loadFontFromFile(File _file) throws IOException
    {
        i_CharsNumber = CHARSET.length();

        BufferedImage p_image = convertFileToImage(_file);
        if (p_image == null) return false;

        String s_Name = _file.getName();
        if (s_Name.lastIndexOf('.')>=0)
        {
            s_Name = s_Name.substring(0,s_Name.lastIndexOf('.')).trim();
        }
        if (s_Name.lastIndexOf("#")>=0)
        {
            s_Name = s_Name.substring(s_Name.indexOf('#')+1).trim();
            i_CharsNumber = Integer.parseInt(s_Name);
            if (i_CharsNumber<=0) throw new IOException("Error chars number after #");
        }

        i_charWidth = p_image.getWidth()/i_CharsNumber;
        i_charHeight = p_image.getHeight();

        if (i_charWidth*i_CharsNumber != p_image.getWidth()) throw new IOException("Bad width of a char"); 

        //System.out.println("Symbols = "+CHARSET.length()+" Charset "+i_charWidth+'x'+i_charHeight);

        if (i_charWidth<=0) throw new IOException("Bad width of the font image");
        if (i_charHeight<=0) throw new IOException("Bad height of the font image");

        p_FontImage = p_image;

        int[] ai_ImageBuffer = ((DataBufferInt) p_image.getRaster().getDataBuffer()).getData();

        int i_x1=i_charWidth-1;
        int i_x2=0;
        int i_y1=i_charHeight-1;
        int i_y2=0;

        // Вычисляем интервал между символами
        for(int i_char=0;i_char<p_image.getWidth();i_char+=i_charWidth)
        {
             for(int lx=0;lx<i_charWidth;lx++)
             {
                 int i_pntr = i_char+lx;
                 for(int ly=0;ly<i_charHeight;ly++)
                 {
                    int i_color = ai_ImageBuffer[i_pntr];
                    if ((i_color&0xFF000000)!=0)
                    {
                       if (i_y1>ly) i_y1 = ly;
                       if (i_x1>lx) i_x1 = lx;
                       if (i_x2<lx) i_x2 = lx;
                       if (i_y2<ly) i_y2 = ly;
                    }
                    i_pntr += p_image.getWidth();
                 }
             }
        }

        i_Interval = i_x1;
        i_BaseLine = Math.min(i_y2+1,i_charHeight);

        //System.out.println("Interval = "+i_Interval+" Baseline="+i_BaseLine);
        return true;
    }

    public BufferedImage makeTransparentStringImage(String _str, Color _color)
    {
        int i_strings = getLinesNumber(_str);
        int i_stringWidth = getStringWidth(_str);
        BufferedImage p_buffImage = new BufferedImage(i_stringWidth,i_strings*i_charHeight,BufferedImage.TYPE_INT_ARGB);
        Graphics p_graphics = p_buffImage.getGraphics();
        p_graphics.setColor(_color);
        drawString(p_graphics,0,0,_str);
        p_buffImage = cropImage(p_buffImage);
        return p_buffImage;
    }

    public Object clone()
    {
        GIFFont p_font = new GIFFont();
        p_font.i_charHeight = i_charHeight;
        p_font.i_charWidth = i_charWidth;
        p_font.p_FontImage = p_FontImage;

        return p_font;
    }


    public static BufferedImage convertFileToImage(File _imageFile) throws IOException
    {
        byte[] ab_byte = new byte[(int) _imageFile.length()];
        FileInputStream p_fis = new FileInputStream(_imageFile);
        p_fis.read(ab_byte);
        p_fis.close();
        return convertFileToImage(ab_byte);
    }

    public static BufferedImage convertFileToImage(byte[] _imageFile) throws IOException
    {
        // May be  it is GIF or JPEG?
        BufferedImage pResultImage = null;
        Image pImage = Toolkit.getDefaultToolkit().createImage(_imageFile);

        pMediaTracker.addImage(pImage, 1);

        try
        {
            pMediaTracker.waitForAll();
        }
        catch (InterruptedException e)
        {
            return null;
        }

        if (pMediaTracker.isErrorAny())
        {
            if ((pMediaTracker.statusID(1, true) & MediaTracker.ERRORED) != 0)
            {
                pMediaTracker.removeImage(pImage);
                pImage = null;
            }
        }
        else
        {
            pMediaTracker.removeImage(pImage);
        }


        if (pImage != null)
        {
            pResultImage = new BufferedImage(pImage.getWidth(null), pImage.getHeight(null), BufferedImage.TYPE_INT_ARGB);
            pResultImage.getGraphics().drawImage(pImage, 0, 0, null);
            pImage = null;
        }

        if (pResultImage == null)
        {
            throw new IOException("Unsupported image format, you must use GIF,JPEG or TGA format");
        }

        return pResultImage;
    }

}
