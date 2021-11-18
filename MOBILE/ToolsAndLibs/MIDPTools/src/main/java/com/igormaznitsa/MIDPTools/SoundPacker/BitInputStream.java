package com.igormaznitsa.MIDPTools.SoundPacker;

import java.io.*;

public class BitInputStream
{
    InputStream is;
    int buff = 0;
    int pos = -1;

    public BitInputStream(InputStream istr)
    {
        is = istr;
    }

    public void octetAlign()
    {
        if (pos != 0)
        {
            buff = 0;
            pos = -1;
        }
    }

    public int readBits(int bitnum) throws IOException
    {
        if (pos < 0)
        {
            buff = is.read();
            if (buff < 0) throw new IOException("Stream is empty");
            pos = 0;
        }
        int acc = 0;

        for (int lk = bitnum;lk > 0;lk--)
        {
            acc = acc << 1;
            if ((buff & 0x80) != 0)
            {
                acc |= 0x01;
            }
            buff = buff << 1;
            pos++;
            if (pos == 8)
            {
                buff = is.read();
                pos = 0;
            }
        }
        return acc;
    }

}
