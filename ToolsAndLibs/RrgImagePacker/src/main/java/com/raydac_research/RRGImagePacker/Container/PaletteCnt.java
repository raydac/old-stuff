package com.raydac_research.RRGImagePacker.Container;

import java.util.Map;
import java.util.HashMap;
import java.util.Vector;
import java.awt.*;
import java.io.*;

public class PaletteCnt
{
    protected String s_name;
    protected Integer[] ap_colorTable;
    protected Map p_indexes;
    protected int i_paletteSize;
    protected String s_comments;

    protected String s_paletteFile;

    public PaletteCnt makeCopy()
    {
        PaletteCnt p_new = new PaletteCnt();
        p_new.setName(s_name);
        p_new.setComments(s_comments);
        p_new.setFileName(s_paletteFile);
        for(int li=0;li<ap_colorTable.length;li++)
        {
            int i_color = ((Integer) ap_colorTable[li]).intValue();
            p_new.addColor(i_color);
        }
        return p_new;
    }

    public void setFileName(String _file)
    {
        s_paletteFile = _file;
    }

    public int getLength()
    {
        return i_paletteSize;
    }

    public String getComments()
    {
        return s_comments;
    }

    public void setComments(String _comments)
    {
        s_comments = _comments;
    }

    public void writeToStream(DataOutputStream _outStream) throws IOException
    {
        // Имя
        _outStream.writeUTF(s_name);
        // Файл
        _outStream.writeUTF(s_paletteFile);
        // Комментарии
        _outStream.writeUTF(s_comments);
        // Размер
        _outStream.writeByte(i_paletteSize);
        // Данные RGB
        for(int li=0;li<i_paletteSize;li++)
        {
            int i_rgb = ap_colorTable[li].intValue();
            int i_r = (i_rgb >>> 16) & 0xFF;
            int i_g = (i_rgb >>> 8) & 0xFF;
            int i_b = i_rgb & 0xFF;
            _outStream.writeByte(i_r);
            _outStream.writeByte(i_g);
            _outStream.writeByte(i_b);
        }
    }

    public String getFileName()
    {
        return s_paletteFile;
    }

    public void readFromStream(DataInputStream _inStream) throws IOException
    {
        // Имя
        s_name = _inStream.readUTF();
        // Файл
        s_paletteFile = _inStream.readUTF();
        // Комментарии
        s_comments = _inStream.readUTF();
        // Размер
        i_paletteSize = _inStream.readUnsignedByte();
        p_indexes.clear();
        // Данные RGB
        for(int li=0;li<i_paletteSize;li++)
        {
            int i_r = _inStream.readUnsignedByte();
            int i_g = _inStream.readUnsignedByte();
            int i_b = _inStream.readUnsignedByte();

            int i_val = (i_r << 16) | (i_g << 8) | i_b;

            Integer p_newColor = new Integer(i_val);

            p_indexes.put(p_newColor,new Integer(li));
            ap_colorTable[li] = p_newColor;
        }
    }

    public void loadFromArray(int [] _palette)
    {
        p_indexes.clear();
        i_paletteSize = 0;
        for(int li=0;li<_palette.length;li++)
        {
            addColor(_palette[li]);
        }
    }

    public PaletteCnt()
    {
        s_name = null;
        p_indexes = new HashMap();
        ap_colorTable = new Integer[256];
        i_paletteSize = 0;
        s_comments = "";
    }

    public void setName(String _name)
    {
        s_name = _name;
    }

    public String getName()
    {
        return s_name;
    }

    public Color getColorAsCOLOR(int _index)
    {
        return new Color(ap_colorTable[_index].intValue());
    }

    public int getColorAt(int _index)
    {
        return ap_colorTable[_index].intValue();
    }

    public Integer getEquColor(int color)
    {
        //fi = 30*(Ri-R0)^2+59*(Gi-G0)^2+11*(Bi-B0)^2;

        int i_r = (color >>> 16) & 0xFF;
        int i_g = (color >>> 8) & 0xFF;
        int i_b = color & 0xFF;

        int i_indx = -1;
        int i_diff = 0x7FFFFFFF;

        for (int li = 0; li < i_paletteSize; li++)
        {
            int i_rgb = ap_colorTable[li].intValue();
            int i_dr = (i_rgb >>> 16) & 0xFF;
            int i_dg = (i_rgb >>> 8) & 0xFF;
            int i_db = i_rgb & 0xFF;

            int i_v = Math.abs(30*(i_dr-i_r)^2+59*(i_dg-i_g)^2+11*(i_db-i_b)^2);

            if (i_v < i_diff)
            {
                i_diff = i_v;
                i_indx = li;
            }
        }

        if (i_indx < 0)
            return null;
        else
            return ap_colorTable[i_indx];
    }

    public Integer getColorIndex(int _color)
    {
        Integer p_color = new Integer(_color);
        Integer p_index = (Integer) p_indexes.get(p_color);
        return p_index;
    }

    public boolean containsColor(int _color)
    {
        Integer p_color = new Integer(_color);
        if (p_indexes.containsKey(p_color)) return true;
        p_color = new Integer(_color);
        if (p_indexes.containsKey(p_color)) return true;
        return false;
    }

    public void addColor(int _color)
    {
        if (containsColor(_color)) return;
        if (i_paletteSize == 256) return;
        Integer p_newColor = new Integer(_color);
        p_indexes.put(p_newColor,new Integer(i_paletteSize));
        ap_colorTable[i_paletteSize++] = p_newColor;
    }

    public String toString()
    {
        return s_name+"("+i_paletteSize+")";
    }


    public static int [] loadACTPAlette(File _paletteFile) throws IOException
    {
        FileInputStream p_fis = new FileInputStream(_paletteFile);
        DataInputStream p_dis = new DataInputStream(p_fis);

        Vector p_set = new Vector();

        p_dis.skip(0x300);
        int i_colornumber = p_dis.readUnsignedShort();
        int i_transparent = p_dis.readUnsignedShort();
        p_dis.close();
        p_dis = null;
        if(i_colornumber>0x100 || i_colornumber==0) throw new IOException("Incorrect value of the palete size");

        p_dis = new DataInputStream(new FileInputStream(_paletteFile));

        for(int i=0;i<i_colornumber;i++)
        {
           int i_r = p_dis.readUnsignedByte();
           int i_g = p_dis.readUnsignedByte();
           int i_b = p_dis.readUnsignedByte();
           int i_rgb = 0x00000000 | (i_r << 16) | (i_g << 8) | i_b;

           if(i == i_transparent)
               p_set.insertElementAt(new Integer(i_rgb/*&0xffffff*/),0);
             else
               p_set.add(new Integer(i_rgb));
        }

        int [] ai_palette = new int[p_set.size()];
        for(int li=0;li<ai_palette.length;li++)
        {
            Integer p_int = (Integer) p_set.elementAt(li);
            ai_palette[li] = p_int.intValue();
        }

        return ai_palette;
    }

    public static int [] loadPALPalette(File _paletteFile) throws IOException
    {
        FileInputStream p_fis = new FileInputStream(_paletteFile);
        DataInputStream p_dis = new DataInputStream(p_fis);

        int i_id = p_dis.readInt();
        if (i_id != (('R'<<24)|('I'<<16)|('F'<<8)|'F')) throw new IOException("Bad format id");
        readBackIntFromStream(p_dis);
        i_id = p_dis.readInt();
        if (i_id != (('P'<<24)|('A'<<16)|('L'<<8)|' ')) throw new IOException("Bad format id");
        i_id = p_dis.readInt();
        if (i_id != (('d'<<24)|('a'<<16)|('t'<<8)|'a')) throw new IOException("Bad format id");

        readBackIntFromStream(p_dis);

        int i_version = readBackShortFromStream(p_dis);
        int i_items = readBackShortFromStream(p_dis);

        if (i_version != 0x300) throw new IOException("Unsupported version");

        int [] ai_palette = new int[i_items];

        for(int li=0;li<i_items;li++)
        {
            int i_b = p_dis.readUnsignedByte();
            int i_g = p_dis.readUnsignedByte();
            int i_r = p_dis.readUnsignedByte();
            p_dis.readUnsignedByte();

            ai_palette[li] = (i_r<<16)|(i_g<<8)|i_b;
        }

        return ai_palette;
    }

    private static int readBackIntFromStream(InputStream _stream) throws IOException
    {
        int i_b0 = _stream.read() & 0xFF;
        int i_b1 = _stream.read() & 0xFF;
        int i_b2 = _stream.read() & 0xFF;
        int i_b3 = _stream.read() & 0xFF;
        return (i_b3<<24)|(i_b2<<16)|(i_b1<<8)|i_b0;
    }

    private static int readBackShortFromStream(InputStream _stream) throws IOException
    {
        int i_b0 = _stream.read() & 0xFF;
        int i_b1 = _stream.read() & 0xFF;
        return (i_b1<<8)|i_b0;
    }

    private static void writeBackShortToStream(OutputStream _stream,int _value) throws IOException
    {
        _stream.write(_value);
        _stream.write(_value>>>8);
    }

    private static void writeBackIntToStream(OutputStream _stream,int _value) throws IOException
    {
        _stream.write(_value);
        _stream.write(_value>>>8);
        _stream.write(_value>>>16);
        _stream.write(_value>>>24);
    }

    public void savePALPalette(File _file) throws IOException
    {
        FileOutputStream p_fos = new FileOutputStream(_file);
        DataOutputStream p_dos = new DataOutputStream(p_fos);

        ByteArrayOutputStream p_baos = new ByteArrayOutputStream(1024);
        DataOutputStream p_bd = new DataOutputStream(p_baos);

        p_dos.writeByte('R');
        p_dos.writeByte('I');
        p_dos.writeByte('F');
        p_dos.writeByte('F');

        p_bd.writeByte('P');
        p_bd.writeByte('A');
        p_bd.writeByte('L');
        p_bd.writeByte(' ');
        p_bd.writeByte('d');
        p_bd.writeByte('a');
        p_bd.writeByte('t');
        p_bd.writeByte('a');


        writeBackIntToStream(p_bd,getLength()*4+4);
        writeBackShortToStream(p_bd,0x300);
        writeBackShortToStream(p_bd,getLength());

        for(int li=0;li<getLength();li++)
        {
            int _rgb = getColorAt(li);
            writeBackIntToStream(p_bd,_rgb);
        }

        p_bd.close();
        byte [] ab_array = p_baos.toByteArray();

        writeBackIntToStream(p_dos,ab_array.length);
        p_dos.write(ab_array);

        p_dos.close();
    }
}
