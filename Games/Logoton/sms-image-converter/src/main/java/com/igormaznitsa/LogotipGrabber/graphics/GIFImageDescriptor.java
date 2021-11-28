/*
    Author : Igor A. Maznitsa
    EMail : rrg@forth.org.ru
    Date : 24.03.2002
*/
package com.igormaznitsa.LogotipGrabber.graphics;

import java.io.IOException;
import java.io.OutputStream;

class GIFImageDescriptor
{
    public GIFImageDescriptor(short word0, short word1, char c, boolean flag)
    {
        separator_ = (byte) c;
        leftPosition_ = 0;
        topPosition_ = 0;
        width_ = word0;
        height_ = word1;
        SetLocalColorTableSize((byte) 0);
        SetReserved((byte) 0);
        SetSortFlag((byte) 0);
        if (flag)
            SetInterlaceFlag((byte) 64);
        else
            SetInterlaceFlag((byte) 0);
        SetLocalColorTableFlag((byte) 0);
    }

    public void Write(OutputStream outputstream)
            throws IOException
    {
        outputstream.write(separator_);
        BitUtils.WriteWord(outputstream, leftPosition_);
        BitUtils.WriteWord(outputstream, topPosition_);
        BitUtils.WriteWord(outputstream, width_);
        BitUtils.WriteWord(outputstream, height_);
        outputstream.write(byte_);
    }

    public void SetLocalColorTableSize(byte byte0)
    {
        byte_ |= (byte) (byte0 & 0x7);
    }

    public void SetReserved(byte byte0)
    {
        byte_ |= (byte) ((byte0 & 0x3) << 3);
    }

    public void SetSortFlag(byte byte0)
    {
        byte_ |= (byte) ((byte0 & 0x1) << 5);
    }

    public void SetInterlaceFlag(byte byte0)
    {
        byte_ |= (byte) ((byte0 & 0x1) << 6);
    }

    public void SetLocalColorTableFlag(byte byte0)
    {
        byte_ |= (byte) ((byte0 & 0x1) << 7);
    }

    public byte separator_;
    public short leftPosition_;
    public short topPosition_;
    public short width_;
    public short height_;
    private byte byte_;
}