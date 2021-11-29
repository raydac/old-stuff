/*
    Author : Igor A. Maznitsa
    EMail : rrg@forth.org.ru
    Date : 24.03.2002
*/
package com.igormaznitsa.LogoEditor.graphics;

import java.io.IOException;
import java.io.OutputStream;

class GIFScreenDescriptor
{

    public GIFScreenDescriptor(short word0, short word1, int i)
    {
        localScreenWidth_ = word0;
        localScreenHeight_ = word1;
        SetGlobalColorTableSize((byte) (BitUtils.BitsNeeded(i) - 1));
        SetGlobalColorTableFlag((byte) 1);
        SetSortFlag((byte) 0);
        SetColorResolution((byte) (BitUtils.BitsNeeded(i) - 1));
        backgroundColorIndex_ = 0;
        pixelAspectRatio_ = 0;
    }

    public void Write(OutputStream outputstream)
            throws IOException
    {
        BitUtils.WriteWord(outputstream, localScreenWidth_);
        BitUtils.WriteWord(outputstream, localScreenHeight_);
        outputstream.write(byte_);
        outputstream.write(backgroundColorIndex_);
        outputstream.write(pixelAspectRatio_);
    }

    public void SetGlobalColorTableSize(byte byte0)
    {
        byte_ |= (byte) (byte0 & 0x7);
    }

    public void SetSortFlag(byte byte0)
    {
        byte_ |= (byte) ((byte0 & 0x1) << 3);
    }

    public void SetColorResolution(byte byte0)
    {
        byte_ |= (byte) ((byte0 & 0x7) << 4);
    }

    public void SetGlobalColorTableFlag(byte byte0)
    {
        byte_ |= (byte) ((byte0 & 0x1) << 7);
    }

    public short localScreenWidth_;
    public short localScreenHeight_;
    private byte byte_;
    public byte backgroundColorIndex_;
    public byte pixelAspectRatio_;
}
