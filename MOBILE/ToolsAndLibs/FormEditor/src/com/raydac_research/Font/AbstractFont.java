package com.raydac_research.Font;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.io.File;
import java.io.IOException;

public abstract class AbstractFont
{
    public abstract int getHeight();
    public abstract int getStringWidth(String _string);
    public abstract int getBaseline();
    public abstract int getCharWidth(char _char);
    public abstract void drawString(Graphics _g,int _x,int _y,String _string);
    public abstract boolean containsChar(char _char);
    public abstract boolean loadFontFromFile(File _file) throws IOException;

    protected boolean lg_Bold;
    protected boolean lg_Italic;
    protected boolean lg_Underline;
    protected int i_Size;

    public int getLinesNumber(String _string)
    {
        char [] ab_chars = _string.toCharArray();
        int i_strings = 1;
        for(int li=0;li<ab_chars.length;li++) if (ab_chars[li]=='\n') i_strings++;
        return i_strings;
    }

    public BufferedImage cropImage(BufferedImage _argb)
    {
        int[] ai_ImageBuffer = ((DataBufferInt) _argb.getRaster().getDataBuffer()).getData();

        int i_x1=_argb.getWidth()-1;
        int i_x2=0;
        int i_y1=_argb.getHeight()-1;
        int i_y2=0;

        // Вычисляем интервал между символами
             for(int lx=0;lx<_argb.getWidth();lx++)
             {
                 int i_pntr = lx;
                 for(int ly=0;ly<_argb.getHeight();ly++)
                 {
                    int i_color = ai_ImageBuffer[i_pntr];
                    if ((i_color&0xFF000000)!=0)
                    {
                       if (i_y1>ly) i_y1 = ly;
                       if (i_x1>lx) i_x1 = lx;
                       if (i_x2<lx) i_x2 = lx;
                       if (i_y2<ly) i_y2 = ly;
                    }
                    i_pntr += _argb.getWidth();
                 }
             }

        i_y2 = Math.min(i_y2+1,_argb.getHeight());
        i_x2 = Math.min(i_x2+1,_argb.getWidth());

        BufferedImage p_newImage = new BufferedImage(i_x2,i_y2,BufferedImage.TYPE_INT_ARGB);
        p_newImage.getGraphics().drawImage(_argb,0,0,null);
        return p_newImage;
    }

    public AbstractFont()
    {
        lg_Bold = false;
        lg_Italic = false;
        lg_Underline = false;
        i_Size = 8;
    }

    public void setSize(int _size)
    {
        i_Size = _size;
    }

    public int getSize()
    {
        return i_Size;
    }

    public boolean isBold()
    {
        return lg_Bold;
    }

    public boolean isItalic()
    {
        return lg_Italic;
    }

    public boolean isUnderline()
    {
        return lg_Underline;
    }

    public void setBold(boolean _flag)
    {
        lg_Bold = _flag;
    }

    public void setItalic(boolean _flag)
    {
        lg_Italic = _flag;
    }

    public void setUnderline(boolean _flag)
    {
        lg_Underline = _flag;
    }

    public boolean supportsBold()
    {
        return false;
    }

    public int [] getSupportedSizes()
    {
        return null;
    }

    public boolean supportsItalic()
    {
        return false;
    }

    public boolean supportsUnderline()
    {
        return false;
    }

    public boolean isCustomized()
    {
        return false;
    }

    public static AbstractFont getAbstractFont(File _font) throws IOException
    {
        AbstractFont p_font = new BFTFont();

        if (p_font.loadFontFromFile(_font)) return p_font;

        p_font = new TTFFont();

        if (p_font.loadFontFromFile(_font)) return p_font;

        p_font = new GIFFont();

        if (p_font.loadFontFromFile(_font)) return p_font;

        throw new IOException("Unknown font format");
    }

    public abstract BufferedImage makeTransparentStringImage(String _str,Color _color);

    public abstract Object clone();
}
