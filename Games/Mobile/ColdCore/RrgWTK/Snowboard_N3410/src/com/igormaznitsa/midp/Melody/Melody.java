package com.igormaznitsa.midp.Melody;

import java.io.InputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.DataInputStream;

/**
 * This class contains a melody
 */
public class Melody
{
    public static final int MELODY_TYPE_OTT = 0;
    public static final int MELODY_TYPE_WAV = 1;

    protected byte [] ab_arr;
    protected int i_type;
    protected Object p_converted;
    protected int i_indx;
    protected int i_timelen;

    public Melody(int _indx, int _type,byte [] _arr)
    {
        i_indx = _indx;
        i_type = _type;
        ab_arr = _arr;
        p_converted = null;
    }

    public int getMelodyID()
    {
        return i_indx;
    }

    public Melody(int _id,DataInputStream _inputStream) throws IOException
    {
        i_indx = _id;
        i_type = _inputStream.readUnsignedByte();
        int i_size = _inputStream.readUnsignedByte();
        if ((i_size & 0x80)!=0) i_size = ((i_size & 0x7F)<< 8) | _inputStream.readUnsignedByte();

        ab_arr = new byte[i_size];
        _inputStream.read(ab_arr);
        p_converted = null;
    }

    public void setNativeObject(Object _native)
    {
        p_converted = _native;
        ab_arr = null;
    }

    public void setTimeLen(int _delay)
    {
        i_timelen = _delay;
    }

    public int getTimeLen()
    {
        return i_timelen;
    }

    public InputStream getMelodyStream()
    {
        return new ByteArrayInputStream(ab_arr);
    }

    public byte [] getMelodyArray()
    {
        return ab_arr;
    }

    public int getMelodyType()
    {
        return i_type;
    }

    public Object getNativeObject()
    {
        return p_converted;
    }
}
