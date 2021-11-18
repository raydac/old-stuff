package com.raydac_research.Font;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.io.File;
import java.io.IOException;
import java.io.FileInputStream;
import java.io.DataInputStream;

public class BFTFont extends AbstractFont
{
    protected int i_baseline;
    protected int i_bitsPerPixel;
    protected int i_spaceWidth;
    protected int i_fontHeight;
    protected int i_chars;

    protected int[] ai_offsets;
    protected BFTChar[] ap_offsets;

    public Object clone()
    {
        BFTFont p_font = new BFTFont();
        p_font.i_baseline = i_baseline;
        p_font.i_bitsPerPixel = i_bitsPerPixel;
        p_font.i_spaceWidth = i_spaceWidth;
        p_font.i_fontHeight = i_fontHeight;
        p_font.i_chars = i_chars;

        p_font.ai_offsets = new int [ai_offsets.length];
        System.arraycopy(ai_offsets,0,p_font.ai_offsets,0,ai_offsets.length);

        p_font.ap_offsets = new BFTChar[ap_offsets.length];

        for(int li=0;li<p_font.ap_offsets.length;li++)
        {
            p_font.ap_offsets[li] = ap_offsets[li] == null ? null : (BFTChar)ap_offsets[li].clone();
        }

        return p_font;
    }

    private class BFTChar
    {
        public int i_Index;
        public int i_YOffset;
        public int i_CWidth;
        public int i_CHeight;
        public int i_XOffset;
        public int i_OutWidth;

        public int[] ai_Array;

        public BFTChar()
        {

        }

        public Object clone()
        {
            BFTChar p_char = new BFTChar();

            p_char.i_Index = i_Index;
            p_char.i_YOffset = i_YOffset;
            p_char.i_CWidth = i_CWidth;
            p_char.i_CHeight = i_CHeight;
            p_char.i_XOffset = i_XOffset;
            p_char.i_OutWidth = i_OutWidth;

            p_char.ai_Array = new int [ai_Array.length];

            System.arraycopy(ai_Array,0,p_char.ai_Array,0,ai_Array.length);

            return p_char;
        }

        public void readCharFromStream(int _bits, byte[] _array, int _offset) throws IOException
        {
            i_Index = ((_array[_offset++] & 0xFF) << 8) | (_array[_offset++] & 0xFF);
            i_YOffset = ((_array[_offset++] & 0xFF) << 8) | (_array[_offset++] & 0xFF);
            i_CWidth = ((_array[_offset++] & 0xFF) << 8) | (_array[_offset++] & 0xFF);
            i_CHeight = ((_array[_offset++] & 0xFF) << 8) | (_array[_offset++] & 0xFF);
            i_XOffset = ((_array[_offset++] & 0xFF) << 8) | (_array[_offset++] & 0xFF);
            i_OutWidth = ((_array[_offset++] & 0xFF) << 8) | (_array[_offset++] & 0xFF);
            int i_size;

            if (_bits == 0)
            {
                i_size = (i_CHeight * i_CWidth + 8) >> 3;
            }
            else
            {
                i_size = (i_CWidth * i_CHeight * 4 + 7) / 8;
            }
            ai_Array = new int[i_size];

            for (int li = 0; li < i_size; li++) ai_Array[li] = _array[_offset++] & 0xFF;
        }
    }

    public BFTFont()
    {
        super();
    }


    public boolean containsChar(char _char)
    {
        return false;
    }

    public BFTChar getChar(int _code)
    {
        if (_code < 0 || _code >= ap_offsets.length) return null;
        return ap_offsets[_code];
    }

    protected Color[] calculateColorTable(Color _color)
    {
        int i_colors = i_bitsPerPixel == 0 ? 2 : 16;

        Color[] ap_colors = new Color[i_colors];

        int i_r = _color.getRed();
        int i_g = _color.getGreen();
        int i_b = _color.getBlue();

        float f_stepR = (float) i_r / (float) (i_colors - 1);
        float f_stepG = (float) i_g / (float) (i_colors - 1);
        float f_stepB = (float) i_b / (float) (i_colors - 1);

        for (int li = 0; li < i_colors; li++)
        {
            int i_nr = Math.round((float) li * f_stepR);
            int i_ng = Math.round((float) li * f_stepG);
            int i_nb = Math.round((float) li * f_stepB);

            ap_colors[li] = new Color(i_nr, i_ng, i_nb);
        }

        return ap_colors;
    }

    public void drawString(Graphics _g, int _x, int _y, String _string)
    {
        Color[] ap_colorTable = calculateColorTable(_g.getColor());

        for (int li = 0; li < _string.length(); li++)
        {
            char p_ch = _string.charAt(li);
            if (p_ch == ' ')
            {
                _x += i_spaceWidth;
                continue;
            }
            BFTChar p_char = getChar(p_ch);
            if (p_char == null) continue;
            int i_YOffset = p_char.i_YOffset;
            int i_XOffset = p_char.i_XOffset;
            for (int ly = 0; ly < p_char.i_CHeight; ly++)
            {
                int i_oy = ly + i_YOffset;
                for (int lx = 0; lx < p_char.i_OutWidth; lx++)
                {
                    int i_pix = getPixelAtSymbol(p_char, lx, ly);
                    int i_x = _x + lx + i_XOffset;
                    int i_y = _y + i_oy;
                    if (i_pix != 0)
                    {
                        _g.setColor(ap_colorTable[i_pix]);
                        _g.drawLine(i_x, i_y, i_x, i_y);
                    }
                }
            }
            _x += p_char.i_OutWidth;
        }
    }

    public int getBaseline()
    {
        return i_baseline;
    }

    public int getCharWidth(char _char)
    {
        if (_char >= ap_offsets.length) return 0;
        if (_char == ' ') return i_spaceWidth;
        if (ap_offsets[_char] == null) return 0; else return ap_offsets[_char].i_OutWidth;
    }

    public int getHeight()
    {
        return i_fontHeight;
    }

    public int getStringWidth(String _string)
    {
        if (_string == null) return 0;
        int i_w = 0;
        for (int li = 0; li < _string.length(); li++) i_w += getCharWidth(_string.charAt(li));
        return i_w;
    }

    private int getPixelAtSymbol(BFTChar _char, int _x, int _y)
    {
        int[] ai_binData = _char.ai_Array;

        if (i_bitsPerPixel == 4)
        {
            int i_offset = _char.i_CWidth * _y + _x;
            int i_int = ai_binData[(i_offset << 2) >> 3];
            return (i_offset & 1) == 0 ? i_int >>> 4 : i_int & 0x0F;
        }
        else
        {
            int i_offset = _char.i_CWidth * _y + _x;
            int i_int = ai_binData[i_offset >> 3];
            return (i_int & (0x80 >>> (i_offset & 0x7))) == 0 ? 0 : 1;
        }
    }

    public boolean loadForntFromFile(File _file) throws IOException
    {
        if (_file.length() < 12) return false;

        final int SIGNATURE = ('P' << 24) | ('T' << 16) | ('V' << 8) | 'F';

        FileInputStream p_fileInputStream = new FileInputStream(_file);

        DataInputStream p_dis = new DataInputStream(p_fileInputStream);

        long i_signature = p_dis.readInt();

        if (i_signature != SIGNATURE) return false;

        int i_version = p_dis.readInt();
        if (i_version != 1) return false;
        p_dis.readShort();

        i_bitsPerPixel = p_dis.readUnsignedShort();
        i_chars = p_dis.readUnsignedShort();
        i_spaceWidth = p_dis.readUnsignedShort();
        i_fontHeight = p_dis.readUnsignedShort();
        i_baseline = p_dis.readUnsignedShort();

        ap_offsets = new BFTChar[i_chars];
        ai_offsets = new int[i_chars];

        for (int li = 0; li < i_chars; li++)
        {
            int i_index = p_dis.readInt();
            if (i_index < 0)
            {
                ap_offsets[li] = null;
                ai_offsets[li] = -1;
            }
            else
            {
                ap_offsets[li] = new BFTChar();
                ai_offsets[li] = i_index;
            }
        }
        p_dis.close();

        byte[] ab_byteArray = new byte[(int) _file.length()];
        p_fileInputStream = new FileInputStream(_file);
        p_fileInputStream.read(ab_byteArray);

        for (int li = 0; li < i_chars; li++)
        {
            if (ap_offsets[li] != null)
            {
                ap_offsets[li].readCharFromStream(i_bitsPerPixel, ab_byteArray, ai_offsets[li]);
            }
        }
        return true;
    }

    public BufferedImage makeTransparentStringImage(String _str, Color _color)
    {
        if (_str== null || _color == null) return null;

        int i_strWidth = getStringWidth(_str);
        int i_strHeight = getHeight();

        if (i_strWidth == 0) i_strWidth = 5;
        if (i_strHeight == 0) i_strHeight = 5;

        BufferedImage p_bufferImage = null;

        if (i_strHeight <= 0 || i_strWidth <= 0)
        {
            p_bufferImage = null;
        }
        else
        {
            p_bufferImage = new BufferedImage(i_strWidth, i_strHeight, BufferedImage.TYPE_INT_ARGB);
            final int[] intNData = ((DataBufferInt) p_bufferImage.getRaster().getDataBuffer()).getData();

            for (int li = 0; li < intNData.length; li++)
            {                                       
                intNData[li] = 0x0;
            }

            Graphics p_Ngr = p_bufferImage.getGraphics();

            p_Ngr.setColor(_color);
            drawString(p_Ngr, 0, 0, _str);
        }
        return p_bufferImage;
    }
}
