package com.raydac_research.Font;

import java.awt.*;
import java.awt.image.BufferedImage;
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
    public abstract boolean loadForntFromFile(File _file) throws IOException;

    protected boolean lg_Bold;
    protected boolean lg_Italic;
    protected boolean lg_Underline;
    protected int i_Size;

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

        try
        {
            if (p_font.loadForntFromFile(_font)) return p_font;
        }
        catch (IOException e)
        {
        }

        p_font = new TTFFont();

        try
        {
            if (p_font.loadForntFromFile(_font)) return p_font;
        }
        catch (IOException e)
        {
        }

        throw new IOException("Unknown font format");
    }

    public abstract BufferedImage makeTransparentStringImage(String _str,Color _color);

    public abstract Object clone();
}
