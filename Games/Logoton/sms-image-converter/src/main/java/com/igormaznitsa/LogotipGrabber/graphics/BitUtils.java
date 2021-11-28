/*
    Author : Igor A. Maznitsa
    EMail : rrg@forth.org.ru
    Date : 24.03.2002
*/
package com.igormaznitsa.LogotipGrabber.graphics;

import java.io.IOException;
import java.io.OutputStream;

class BitUtils
{

    public static byte BitsNeeded(int i)
    {
        byte byte0 = 1;
        if (i-- == 0)
            return 0;
        while ((i >>= 1) != 0)
            byte0++;
        return byte0;
    }

    public static void WriteWord(OutputStream outputstream, short word0)
            throws IOException
    {
        outputstream.write(word0 & 0xff);
        outputstream.write(word0 >> 8 & 0xff);
    }

    static void WriteString(OutputStream outputstream, String s)
            throws IOException
    {
        for (int i = 0; i < s.length(); i++)
            outputstream.write((byte) s.charAt(i));

    }

    public BitUtils()
    {
    }
}
