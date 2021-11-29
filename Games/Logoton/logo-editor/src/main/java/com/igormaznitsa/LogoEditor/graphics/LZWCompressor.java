/*
    Author : Igor A. Maznitsa
    EMail : rrg@forth.org.ru
    Date : 24.03.2002
*/
package com.igormaznitsa.LogoEditor.graphics;

import java.io.IOException;
import java.io.OutputStream;

class LZWCompressor
{

    public static void LZWCompress(OutputStream outputstream, int i, byte abyte0[]) throws IOException
    {
        short word1 = -1;
        BitFile bitfile = new BitFile(outputstream);
        LZWStringTable lzwstringtable = new LZWStringTable();
        int j = 1 << i;
        int k = j + 1;
        int l = i + 1;
        int i1 = (1 << l) - 1;
        lzwstringtable.ClearTable(i);
        bitfile.WriteBits(j, l);
        for (int j1 = 0; j1 < abyte0.length; j1++)
        {
            byte byte0 = abyte0[j1];
            short word0;
            if (((word0 = lzwstringtable.FindCharString(word1, byte0)) != -1))
            {
                word1 = word0;
            }
            else
            {
                bitfile.WriteBits(word1, l);
                if (lzwstringtable.AddCharString(word1, byte0) > i1)
                {
                    if (++l > 12)
                    {
                        bitfile.WriteBits(j, l - 1);
                        lzwstringtable.ClearTable(i);
                        l = i + 1;
                    }
                    i1 = (1 << l) - 1;
                }
                word1 = (short) ((short) byte0 & 0xff);
            }
        }

        if (word1 != -1)
            bitfile.WriteBits(word1, l);
        bitfile.WriteBits(k, l);
        bitfile.Flush();
    }

    public LZWCompressor()
    {
    }
}
