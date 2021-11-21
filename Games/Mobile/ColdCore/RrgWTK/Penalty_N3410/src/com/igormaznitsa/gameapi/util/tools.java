package com.igormaznitsa.gameapi.util;

import java.io.ByteArrayOutputStream;

public class tools
{
    /**
     * Decompressing a byte array with RLE
     * @param inarray incomming packed byte array
     * @param dstlen length of outgoing bayte array
     * @return unpacked byte array
     */
    public static final byte[] RLEdecompress(byte[] inarray, int dstlen)
    {
        System.gc();
        byte[] out = new byte[dstlen];
        int indx = 0;
        int outindx = 0;
        while (indx < inarray.length)
        {
            int val = inarray[indx++] & 0xFF;
            int counter = 1;
            int value = 0;
            if ((val & 0xC0) == 0xC0)
            {
                counter = val & 0x3F;
                value = inarray[indx++] & 0xFF;
            }
            else
            {
                value = val;
            }

            while (counter != 0)
            {
                out[outindx++] = (byte) value;
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
    public static final byte[] RLEcompress(byte[] inarray)
    {
        System.gc();
        ByteArrayOutputStream baos = new ByteArrayOutputStream(inarray.length);

        int inindx = 0;
        while (inindx < inarray.length)
        {
            int value = inarray[inindx++] & 0xFF;
            int count = 1;
            while (inindx < inarray.length)
            {
                if ((inarray[inindx] & 0xFF) == value)
                {
                    count++;
                    inindx++;
                    if (count == 63) break;
                }
                else
                    break;
            }
            if (count > 1)
            {
                baos.write(count | 0xC0);
                baos.write(value);
            }
            else
            {
                if (value > 63)
                {
                    baos.write(0xC1);
                    baos.write(value);
                }
                else
                    baos.write(value);
            }
        }

        byte[] outarray = baos.toByteArray();
        baos = null;
        System.gc();
        return outarray;
    }

    /**
     * Square root
     * @param x
     * @return int value
     */
    public static int sqr(int x)
    {
        int bx = x;
        int ax = 1,di = 2,cx = 0,dx = 0;
        while (cx <= bx)
        {
            cx += ax;
            ax += di;
            dx++;
        }
        return dx - 1;
    }


    public final static int[] sineTable = new int[]{0,25,50,74,98,121,142,162,181,198,213,226,237,245,251,255,256,255,251,245,237,226,213,198,181,162,142,121,98,74,50,25,0,-25,-50,-74,-98,-121,-142,-162,-181,-198,-213,-226,-237,-245,-251,-255,-256,-255,-251,-245,-237,-226,-213,-198,-181,-162,-142,-121,-98,-74,-50,-25};

    /**
     * Converting radian value to 64 based angle value
     * @param _radians radian value in I16 format;
     * @return
     */
    public int convertRadiansTo64BasedAngle(int _radians)
    {
        return (_radians/6536);
    }

    /**
     * Calculating of sin with multiply in index
     * @param x Multiplier
     * @param index Angle (0..63)
     * @return
     */
    public static int xSine(int x, int index)
    {
        return (x * sineTable[index]) >> 8;
    }

    /**
     * Calculating of cos with multiply in index
     * @param x Multiplier
     * @param index Angle (0..63)
     * @return
     */
    public static int xCoSine(int x, int index)
    {
        index +=16;
        return (x * sineTable[index&63]) >> 8;
    }

    /**
     * Calculating of sin with multiply in index without right shift
     * @param x Multiplier
     * @param index Angle (0..63)
     * @return
     */
    public static int xSineFloat(int x, int index)
    {
        return (x * sineTable[index]);
    }

    /**
     * Calculating of cos with multiply in index without right shift
     * @param x Multiplier
     * @param index Angle (0..63)
     * @return
     */
    public static int xCoSineFloat(int x, int index)
    {
        index +=16;
        return (x * sineTable[index & 63]);
    }

}