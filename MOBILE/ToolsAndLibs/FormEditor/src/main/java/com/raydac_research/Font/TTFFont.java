package com.raydac_research.Font;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.io.File;
import java.io.IOException;
import java.io.FileInputStream;
import java.util.StringTokenizer;

public class TTFFont extends AbstractFont
{
    protected Font p_font;
    protected FontMetrics p_fontMentrics;

    public TTFFont()
    {
        super();
        p_font = null;
        p_fontMentrics = null;
    }

    public Object clone()
    {
        TTFFont p_newFont = new TTFFont();

        if (p_font!=null)
        {
            p_newFont.p_font = p_font.deriveFont((float)getSize());
            p_newFont.p_fontMentrics = Toolkit.getDefaultToolkit().getFontMetrics(p_newFont.p_font);
        }

        p_newFont.lg_Bold = lg_Bold;
        p_newFont.lg_Italic = lg_Italic;
        p_newFont.lg_Underline = lg_Underline;
        p_newFont.i_Size = i_Size;

        return p_newFont;
    }

    public void setBold(boolean _flag)
    {
        super.setBold(_flag);
        _deriveFont();
    }

    public void setItalic(boolean _flag)
    {
        super.setItalic(_flag);
        _deriveFont();
    }

    public void setUnderline(boolean _flag)
    {
        super.setUnderline(_flag);
        _deriveFont();
    }

    public void setSize(int _size)
    {
        super.setSize(_size);
        _deriveFont();
    }

    private void _deriveFont()
    {
        int i_style = (lg_Bold ? Font.BOLD : Font.PLAIN) | (lg_Italic ? Font.ITALIC : Font.PLAIN);
        p_font = p_font.deriveFont(i_style,i_Size);
        p_fontMentrics = Toolkit.getDefaultToolkit().getFontMetrics(p_font);
    }

    public boolean supportsBold()
    {
        return true;
    }

    public boolean supportsItalic()
    {
        return true;
    }

    public boolean supportsUnderline()
    {
        return true;
    }

    public boolean containsChar(char _char)
    {
        return p_font.canDisplay(_char);
    }

    private static final int [] ai_sizes = new int[]{8,9,10,11,12,14,16,18,20,22,24,26,28,36,48,72};

    public int[] getSupportedSizes()
    {
        return ai_sizes;
    }

    public void setFontSize(int _size)
    {
        if (p_font!=null)
        {
            p_font = p_font.deriveFont(p_font.getStyle(),_size);
            p_fontMentrics = Toolkit.getDefaultToolkit().getFontMetrics(p_font);
        }
    }

    public void setFontStyle(int _style)
    {
        if (p_font!=null)
        {
            p_font = p_font.deriveFont(_style, p_font.getSize());
            p_fontMentrics = Toolkit.getDefaultToolkit().getFontMetrics(p_font);
        }
    }

    public void drawString(Graphics _g, int _x, int _y, String _string)
    {
        if (p_font==null) return;
        _g.setFont(p_font);
        _y += p_fontMentrics.getMaxAscent();
        _g.drawString(_string,_x,_y);
        if (lg_Underline)
        {
            _g.drawLine(0,_y,getStringWidth(_string),_y);
        }
    }

    public boolean isCustomized()
    {
        return true;
    }

    public int getBaseline()
    {
        return p_fontMentrics.getMaxAscent();
    }

    public int getCharWidth(char _char)
    {
        return p_fontMentrics.charWidth(_char);
    }

    public int getHeight()
    {
        return p_fontMentrics.getMaxAscent()+p_fontMentrics.getMaxDescent();
    }

    public int getStringWidth(String _string)
    {
        return p_fontMentrics.stringWidth(_string);
    }

    public boolean loadFontFromFile(File _file) throws IOException
    {
        try
        {
            p_font = Font.createFont(Font.TRUETYPE_FONT,new FileInputStream(_file));
            p_fontMentrics = Toolkit.getDefaultToolkit().getFontMetrics(p_font);
        }
        catch (FontFormatException e)
        {
            return false;
        }
        return true;
    }

    private String [] parseToArray(String _string)
    {
        int i_strings = getLinesNumber(_string);
        String [] as_arra = new String[i_strings];
        int i_indx = 0;
        String s_buff ="";
        char [] ach_chars = _string.toCharArray();
        for(int li=0;li<ach_chars.length;li++)
        {
            char ch_char = ach_chars[li];
            if (ch_char=='\n')
            {
                as_arra[i_indx++] = s_buff;
                s_buff="";
            }
            else
            {
                s_buff+=ch_char;
            }
        }
        if (i_indx<i_strings) as_arra[i_indx] = s_buff;
        return as_arra;
    }

    public BufferedImage makeTransparentStringImage(String _str, Color _color)
    {
        if (p_font == null) return null;

        int i_width = getStringWidth(_str);
        int i_lines = getLinesNumber(_str);
        int i_height = getHeight()*i_lines;

        BufferedImage p_buffImage = new BufferedImage(i_width,i_height,BufferedImage.TYPE_INT_ARGB);
        int[] ai_ImageBuffer = ((DataBufferInt) p_buffImage.getRaster().getDataBuffer()).getData();
        for(int li=0;li<ai_ImageBuffer.length;li++) ai_ImageBuffer[li] = 0x00000000;

        Graphics p_graphics = p_buffImage.getGraphics();
        p_graphics.setColor(_color);
        p_graphics.setFont(p_font);

        String [] as_arra = parseToArray(_str);

        int i_y = 0;

        for(int li=0;li<i_lines;li++)
        {
            p_graphics.drawString(as_arra[li],0,i_y+p_fontMentrics.getMaxAscent());
            i_y += getHeight();
        }

        if (lg_Underline)
        {
            i_y = getBaseline();
            while(i_lines>0)
            {
                p_graphics.drawLine(0,i_y,i_width,i_y);
                i_y +=getBaseline();
                i_lines--;
            }
        }
        return cropImage(p_buffImage);
    }
}