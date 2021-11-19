package com.raydac_research.TGA;

import java.util.Hashtable;
import java.util.Iterator;
import java.io.*;

public class RRG_RGB_Palette
{
    private Hashtable indexes;
    private Hashtable colors;

    private boolean  lgGrayscale;
    private boolean  lgBlackWhite;
    private int iTransparentColor;

    public boolean isBlackwhite()
    {
        return lgBlackWhite;
    }

    public boolean isGrayscale()
    {
        return lgGrayscale;
    }

    public int getPaletteSize()
    {
        return indexes.size();
    }

    public int getTransparentColorIndex()
    {
        return iTransparentColor;
    }

    public RRG_RGB_Palette(int [] _colorsRGB) throws IOException
    {
        indexes = new Hashtable(256);
        colors  = new Hashtable(256);

        resetPalette();

        if (_colorsRGB!=null)
        {
            for (int li=0;li<_colorsRGB.length;li++)
            {
                addColor(_colorsRGB[li]);
            }
        }
    }

    public void resetPalette()
    {
        indexes.clear();
        colors.clear();
        lgBlackWhite = true;
        lgGrayscale = true;
        iTransparentColor = -1;
    }

    private int processColor(int _value)
    {
        int i_l = _value & 0xFF;
        return i_l;
    }

    public void loadACTorACOpaletteAs565(byte [] _array) throws IOException
    {
            int [] aiResult = null;
            int i_colornumber = 0;
            int i_transparent = -1;

            try
            {
                DataInputStream p_dis = new DataInputStream(new ByteArrayInputStream(_array));

                // trying to recognize ACT palette file, we need an
                if (_array.length==0x304)
                {
                    p_dis.skip(0x300);
                    i_colornumber = p_dis.readUnsignedShort();
                    i_transparent = p_dis.readUnsignedShort();
                    p_dis.close();
                    p_dis = null;
                    if (i_colornumber>0x100 || i_colornumber==0) throw new IOException("Incorrect value of palete size");

                    p_dis = new DataInputStream(new ByteArrayInputStream(_array));

                    aiResult = new int[i_colornumber];

                    for(int i=0;i<i_colornumber;i++)
                    {
                       int i_r = p_dis.readUnsignedByte();
                       int i_g = p_dis.readUnsignedByte();
                       int i_b = p_dis.readUnsignedByte();
                       int i_rgb = (i_r<<16) | (i_g<<8) | i_b;

                       aiResult[i] = i_rgb;
                    }
                }
                  else
                  {
                    if (p_dis.readUnsignedShort() != 1) throw new IOException("Unknown format of given palette. Palette should be ACO or ACT file.");

                    i_colornumber = p_dis.readUnsignedShort();

                    aiResult = new int[i_colornumber];

                    for (int li = 0; li < i_colornumber; li++)
                    {
                         int i_readcolorspace = p_dis.readUnsignedShort();
                         if (i_readcolorspace != 0) throw new IOException("The palette file contains a color with an unsupported color scheme, only the RGB scheme is supported by the encoder version [" + i_readcolorspace + "]");

                         int i_r = processColor(p_dis.readUnsignedShort());
                         int i_g = processColor(p_dis.readUnsignedShort());
                         int i_b = processColor(p_dis.readUnsignedShort());

                         p_dis.readUnsignedShort();

                         int i_rgb = (i_r<<16) | (i_g<<8) | i_b;

                         aiResult[li] = i_rgb;
                    }
                  }
            }
            catch (Exception p_ex)
            {
                throw new IOException(p_ex.getMessage());
            }

        resetPalette();

        for(int li=0;li<aiResult.length;li++)
        {
            addColor(aiResult[li]);
        }

        iTransparentColor = i_transparent;
    }

    public void fillPaletteFromImageArray(int [] _aiARGB) throws IOException
    {
        resetPalette();

        for (int li=0;li<_aiARGB.length;li++)
        {
            int iIndex = addColor(_aiARGB[li]&0xFFFFFF);
            if ((_aiARGB[li] & 0xFF000000) == 0) iTransparentColor = iIndex;
        }
    }

    public int addColor(int _colorRGB) throws IOException
    {
        Integer pColor = new Integer(_colorRGB);

        if (indexes.containsKey(pColor))
        {
            Integer index = (Integer) indexes.get(pColor);
            return index.intValue();
        }
        else
        {
            if (indexes.size()==0x100)
                 throw new IOException("Too much used colors in your image, I can't create 256 color palette");

            int index = indexes.size();

            Integer pIndex = new Integer(index);

            indexes.put(pColor,pIndex);
            colors.put(pIndex,pColor);

            int iR = (_colorRGB>>16) & 0xFF;
            int iG = (_colorRGB>>8) & 0xFF;
            int iB = _colorRGB & 0xFF;

            if ((iR != iG) || (iG != iB) || (iR != iB)) lgGrayscale = false;
            if ((iR != 0 && iR != 0xFF) || (iG != 0 && iG != 0xFF) || (iB != 0 && iB != 0xFF)) lgBlackWhite = false;

            return index;
        }
    }

    public int getColorForIndex(int _index)
    {
        Integer pIndex = new Integer(_index);
        if (!colors.containsKey(pIndex))
            return -1;
        Integer pColor = (Integer)colors.get(pIndex);
        return pColor.intValue();
    }

    public int getColorIndex(int rgb)
    {
        Integer p_color = new Integer(rgb);
        Integer p_index = (Integer) indexes.get(p_color);

        if (p_index == null)
        {
            int i_Index = getEquColorIndex(rgb);
            return i_Index;
        }
        else
            return p_index.intValue();
    }

    public int getEquColorIndex(int _rgb)
    {
        int iRGB = _rgb;
        int iR = (iRGB >>> 16) & 0xFF;
        int iG = (iRGB >>> 8) & 0xFF;
        int iB = iRGB & 0xFF;

        int i_indx = -1;
        int i_diff = 0x7FFFFFFF;

        Iterator pIter = indexes.keySet().iterator();

        while(pIter.hasNext())
        {
            Integer pColor = (Integer) pIter.next();
            int i_rgb = pColor.intValue();
            int i_dr = (i_rgb >>> 16) & 0xFF;
            int i_dg = (i_rgb >>> 8) & 0xFF;
            int i_db = i_rgb & 0xFF;

            int i_v = Math.abs(30*((i_dr-iR)*(i_dr-iR))+59*((i_dg-iG)*(i_dg-iG))+11*((i_db-iB)*(i_db-iB)));

            if (i_v < i_diff)
            {
                i_diff = i_v;
                i_indx = ((Integer)indexes.get(pColor)).intValue();
            }
        }

        return i_indx;
    }

    public boolean containsColor(int _colorRGB)
    {
        int iColorRGB = _colorRGB;
        Integer p_color = new Integer(iColorRGB);
        if (indexes.containsKey(p_color)) return true;
        p_color = new Integer(iColorRGB);
        if (indexes.containsKey(p_color)) return true;

        if (getEquColorIndex(iColorRGB) >= 0) return true;
        return false;
    }
}
