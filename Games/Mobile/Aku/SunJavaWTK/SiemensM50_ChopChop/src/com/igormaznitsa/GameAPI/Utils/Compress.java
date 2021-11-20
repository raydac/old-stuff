package com.igormaznitsa.GameAPI.Utils;

import java.io.ByteArrayOutputStream;

public class Compress
{
    /**
     * Decompressing a byte array with RLE
     * @param inarray incomming packed byte array
     * @param dstlen length of outgoing bayte array
     * @return unpacked byte array
     */
    public static final byte [] RLEdecompress(byte [] inarray,int dstlen)
    {
        System.gc();
        byte [] out = new byte [dstlen];
        int indx = 0;
        int outindx = 0;
        while(indx<inarray.length)
        {
            int val = inarray[indx++] & 0xFF;
            int counter = 1;
            int value = 0;
            if ((val&0xC0)==0xC0)
            {
                counter = val & 0x3F;
                value = inarray[indx++] & 0xFF;
            }
            else
            {
                value = val;
            }

            while(counter!=0)
            {
                out [outindx++] = (byte)value;
                counter--;
            }
        }
        System.gc();
        return out;
    }

    /**
     * Compressing a byte array with RLE
     * @param inarray incoming byte array
     * @return packed byte array
     */
    public static final byte [] RLEcompress(byte [] inarray)
    {
        System.gc();
        ByteArrayOutputStream baos = new ByteArrayOutputStream(inarray.length);

        int inindx = 0;
        while(inindx<inarray.length)
        {
            int value = inarray[inindx++]&0xFF;
            int count = 1;
            while(inindx<inarray.length)
            {
                if ((inarray[inindx]&0xFF)==value)
                {
                    count++;
                    inindx++;
                    if (count==63) break;
                }
                else
                    break;
            }
            if (count>1)
            {
                baos.write(count|0xC0);
                baos.write(value);
            }
            else
            {
                if (value>63)
                {
                    baos.write(0xC1);
                    baos.write(value);
                }
                else
                    baos.write(value);
            }
        }

        byte [] outarray = baos.toByteArray();
        baos = null;
        System.gc();
        return outarray;
    }
}
