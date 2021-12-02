package com.raydac_research.RRGImagePacker.Container;

import java.util.Vector;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.DataInputStream;

public class PaletteContainer
{
    protected String s_Name;
    protected Vector p_palettes;

    public PaletteContainer(String _name)
    {
        p_palettes = new Vector();
        s_Name = _name;
    }

    public void writeDataToStream(DataOutputStream _stream) throws IOException
    {
        // Количество палитр
        _stream.writeInt(p_palettes.size());
        for(int li=0;li<p_palettes.size();li++)
        {
            PaletteCnt p_img = (PaletteCnt) p_palettes.elementAt(li);
            p_img.writeToStream(_stream);
        }
    }

    public void loadDataFromStream(DataInputStream _stream) throws IOException
    {
        // Количество палитр
        p_palettes.removeAllElements();
        int i_length = _stream.readInt();
        for(int li=0;li<i_length;li++)
        {
            PaletteCnt p_img = new PaletteCnt();
            p_img.readFromStream(_stream);
            p_palettes.add(p_img);
        }
    }

    public PaletteCnt getPaletteForName(String _name)
    {
        for(int li=0;li<p_palettes.size();li++)
        {
            PaletteCnt p_cnt = (PaletteCnt) p_palettes.elementAt(li);
            if (_name.equals(p_cnt.getName())) return p_cnt;
        }
        return null;
    }

    public PaletteCnt getPaletteAt(int _index)
    {
        if (_index < 0 || _index>=p_palettes.size()) return null;
        return (PaletteCnt) p_palettes.elementAt(_index);
    }

    public int getIndex(Object _obj)
    {
        return p_palettes.indexOf(_obj);
    }

    public PaletteCnt addPalette()
    {
        PaletteCnt p_palette = new PaletteCnt();
        p_palettes.add(p_palette);
        return p_palette;
    }

    public int getSize()
    {
        return p_palettes.size();
    }

    public String toString()
    {
        return s_Name;
    }
}
