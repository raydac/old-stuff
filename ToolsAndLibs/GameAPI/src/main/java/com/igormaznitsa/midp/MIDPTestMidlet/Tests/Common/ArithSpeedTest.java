package com.igormaznitsa.midp.MIDPTestMidlet.Tests.Common;

import com.igormaznitsa.midp.MIDPTestMidlet.Testlet.NonVisualTest;
import com.igormaznitsa.midp.MIDPTestMidlet.Testlet.TestletListener;
import com.igormaznitsa.midp.LanguageBlock;

public class ArithSpeedTest extends NonVisualTest
{
    StringBuffer p_ResultBuffer;

    private static final int ITERATION_NUMBER = 500000;
    private static final int ITERATION_BLOCK_NUMBER = 2;

    public static final int ITERATION_STRING_NUMBER = 100;
    public static final int ITERATION_STRING_BLOCK_NUMBER = 10;

    private static final long L_COEFF = 10000l;

    public transient byte  b_b0 = 0x10,b_b1 = 0x20,b_b2;
    public transient short s_b0 = 0x1000,s_b1 = 0x2000,s_b2;
    public transient int   i_b0 = 0x10000000,i_b1 = 0x200000,i_b2;
    public transient long  l_b0 = 0x1000000000000000l,l_b1 = 0x2000000000000000l,l_b2;

    public void init(TestletListener _listener, LanguageBlock _languageBlock, int _backStringIndex, int _waitPleaseItem, int _cancelStringIndex)
    {
        super.init(_listener,_languageBlock,_backStringIndex,_waitPleaseItem,_cancelStringIndex,false);
    }

    public boolean startTest()
    {
        super.startTest();

        p_ResultBuffer = new StringBuffer();

        Runtime.getRuntime().gc();
        try
        {
            Thread.sleep(500);
        }
        catch (InterruptedException e)
        {
            return false;
        }

        Thread.currentThread().setPriority(Thread.MAX_PRIORITY);

        p_ResultBuffer.append("+(byte) :");
        p_ResultBuffer.append(testAddByte());
        p_ResultBuffer.append('\n');

        p_ResultBuffer.append("+(short) :");
        p_ResultBuffer.append(testAddShort());
        p_ResultBuffer.append('\n');

        p_ResultBuffer.append("+(int) :");
        p_ResultBuffer.append(testAddInt());
        p_ResultBuffer.append('\n');

        p_ResultBuffer.append("+(long) :");
        p_ResultBuffer.append(testAddLong());
        p_ResultBuffer.append('\n');

        p_ResultBuffer.append("-(byte) :");
        p_ResultBuffer.append(testSUBByte());
        p_ResultBuffer.append('\n');

        p_ResultBuffer.append("-(short) :");
        p_ResultBuffer.append(testSUBShort());
        p_ResultBuffer.append('\n');

        p_ResultBuffer.append("-(int) :");
        p_ResultBuffer.append(testSUBInt());
        p_ResultBuffer.append('\n');

        p_ResultBuffer.append("-(long) :");
        p_ResultBuffer.append(testSUBLong());
        p_ResultBuffer.append('\n');

        p_ResultBuffer.append("*(byte) :");
        p_ResultBuffer.append(testMULByte());
        p_ResultBuffer.append('\n');

        p_ResultBuffer.append("*(short) :");
        p_ResultBuffer.append(testMULShort());
        p_ResultBuffer.append('\n');

        p_ResultBuffer.append("*(int) :");
        p_ResultBuffer.append(testMULInt());
        p_ResultBuffer.append('\n');

        p_ResultBuffer.append("*(long) :");
        p_ResultBuffer.append(testMULLong());
        p_ResultBuffer.append('\n');

        p_ResultBuffer.append("/(byte) :");
        p_ResultBuffer.append(testDIVByte());
        p_ResultBuffer.append('\n');

        p_ResultBuffer.append("/(short) :");
        p_ResultBuffer.append(testDIVShort());
        p_ResultBuffer.append('\n');

        p_ResultBuffer.append("/(int) :");
        p_ResultBuffer.append(testDIVInt());
        p_ResultBuffer.append('\n');

        p_ResultBuffer.append("/(long) :");
        p_ResultBuffer.append(testDIVLong());
        p_ResultBuffer.append('\n');

        p_ResultBuffer.append("%(byte) :");
        p_ResultBuffer.append(testMODByte());
        p_ResultBuffer.append('\n');

        p_ResultBuffer.append("%(short) :");
        p_ResultBuffer.append(testMODShort());
        p_ResultBuffer.append('\n');

        p_ResultBuffer.append("%(int) :");
        p_ResultBuffer.append(testMODInt());
        p_ResultBuffer.append('\n');

        p_ResultBuffer.append("%(long) :");
        p_ResultBuffer.append(testMODLong());
        p_ResultBuffer.append('\n');

        p_ResultBuffer.append("<<(byte) :");
        p_ResultBuffer.append(testSHLByte());
        p_ResultBuffer.append('\n');

        p_ResultBuffer.append("<<(short) :");
        p_ResultBuffer.append(testSHLShort());
        p_ResultBuffer.append('\n');

        p_ResultBuffer.append("<<(int) :");
        p_ResultBuffer.append(testSHLInt());
        p_ResultBuffer.append('\n');

        p_ResultBuffer.append("<<(long) :");
        p_ResultBuffer.append(testSHLLong());
        p_ResultBuffer.append('\n');

        p_ResultBuffer.append(">>(byte) :");
        p_ResultBuffer.append(testSHRByte());
        p_ResultBuffer.append('\n');

        p_ResultBuffer.append(">>(short) :");
        p_ResultBuffer.append(testSHRShort());
        p_ResultBuffer.append('\n');

        p_ResultBuffer.append(">>(int) :");
        p_ResultBuffer.append(testSHRInt());
        p_ResultBuffer.append('\n');

        p_ResultBuffer.append(">>(long) :");
        p_ResultBuffer.append(testSHRLong());
        p_ResultBuffer.append('\n');

        p_ResultBuffer.append(">>>(byte) :");
        p_ResultBuffer.append(testSHRSByte());
        p_ResultBuffer.append('\n');

        p_ResultBuffer.append(">>>(short) :");
        p_ResultBuffer.append(testSHRSShort());
        p_ResultBuffer.append('\n');

        p_ResultBuffer.append(">>>(int) :");
        p_ResultBuffer.append(testSHRSInt());
        p_ResultBuffer.append('\n');

        p_ResultBuffer.append(">>>(long) :");
        p_ResultBuffer.append(testSHRSLong());
        p_ResultBuffer.append('\n');

        Thread.currentThread().setPriority(Thread.NORM_PRIORITY);

        this.Completed();
        return true;
    }

    //=================Testing blocks ========================

    //=========================ADD================================
    private long testAddByte()
    {
        long l_summary = 0;
        for(int lb=0;lb<ITERATION_BLOCK_NUMBER;lb++)
        {
            long l_startTime = System.currentTimeMillis();
            for(int li=0;li<ITERATION_NUMBER;li++)
            {
                b_b2 = (byte)(b_b0 + b_b1);
            }
            long l_endTime = System.currentTimeMillis();

            l_summary += ((l_endTime - l_startTime)*L_COEFF)/ITERATION_NUMBER;
        }

        return l_summary / ITERATION_BLOCK_NUMBER;
    }

    private long testAddShort()
    {
        long l_summary = 0;
        for(int lb=0;lb<ITERATION_BLOCK_NUMBER;lb++)
        {
            long l_startTime = System.currentTimeMillis();
            for(int li=0;li<ITERATION_NUMBER;li++)
            {
                s_b2 = (short)(s_b0 + s_b1);
            }
            long l_endTime = System.currentTimeMillis();

            l_summary += ((l_endTime - l_startTime)*L_COEFF)/ITERATION_NUMBER;
        }

        return l_summary / ITERATION_BLOCK_NUMBER;
    }

    private long testAddInt()
    {
        long l_summary = 0;
        for(int lb=0;lb<ITERATION_BLOCK_NUMBER;lb++)
        {
            long l_startTime = System.currentTimeMillis();
            for(int li=0;li<ITERATION_NUMBER;li++)
            {
                i_b2 = i_b0 + i_b1;
            }
            long l_endTime = System.currentTimeMillis();

            l_summary += ((l_endTime - l_startTime)*L_COEFF)/ITERATION_NUMBER;
        }

        return l_summary / ITERATION_BLOCK_NUMBER;
    }

    private long testAddLong()
    {
        long l_summary = 0;
        for(int lb=0;lb<ITERATION_BLOCK_NUMBER;lb++)
        {
            long l_startTime = System.currentTimeMillis();
            for(int li=0;li<ITERATION_NUMBER;li++)
            {
                l_b2 = l_b0 + l_b1;
            }
            long l_endTime = System.currentTimeMillis();

            l_summary += ((l_endTime - l_startTime)*L_COEFF)/ITERATION_NUMBER;
        }

        return l_summary / ITERATION_BLOCK_NUMBER;
    }

    //=========================MUL================================
    private long testMULByte()
    {
        long l_summary = 0;
        for(int lb=0;lb<ITERATION_BLOCK_NUMBER;lb++)
        {
            long l_startTime = System.currentTimeMillis();
            for(int li=0;li<ITERATION_NUMBER;li++)
            {
                b_b2 = (byte)(b_b0 * b_b1);
            }
            long l_endTime = System.currentTimeMillis();

            l_summary += ((l_endTime - l_startTime)*L_COEFF)/ITERATION_NUMBER;
        }

        return l_summary / ITERATION_BLOCK_NUMBER;
    }

    private long testMULShort()
    {
        long l_summary = 0;
        for(int lb=0;lb<ITERATION_BLOCK_NUMBER;lb++)
        {
            long l_startTime = System.currentTimeMillis();
            for(int li=0;li<ITERATION_NUMBER;li++)
            {
                s_b2 = (short)(s_b0 * s_b1);
            }
            long l_endTime = System.currentTimeMillis();

            l_summary += ((l_endTime - l_startTime)*L_COEFF)/ITERATION_NUMBER;
        }

        return l_summary / ITERATION_BLOCK_NUMBER;
    }

    private long testMULInt()
    {
        long l_summary = 0;
        for(int lb=0;lb<ITERATION_BLOCK_NUMBER;lb++)
        {
            long l_startTime = System.currentTimeMillis();
            for(int li=0;li<ITERATION_NUMBER;li++)
            {
                i_b2 = i_b0 * i_b1;
            }
            long l_endTime = System.currentTimeMillis();

            l_summary += ((l_endTime - l_startTime)*L_COEFF)/ITERATION_NUMBER;
        }

        return l_summary / ITERATION_BLOCK_NUMBER;
    }

    private long testMULLong()
    {
        long l_summary = 0;
        for(int lb=0;lb<ITERATION_BLOCK_NUMBER;lb++)
        {
            long l_startTime = System.currentTimeMillis();
            for(int li=0;li<ITERATION_NUMBER;li++)
            {
                l_b2 = l_b0 * l_b1;
            }
            long l_endTime = System.currentTimeMillis();

            l_summary += ((l_endTime - l_startTime)*L_COEFF)/ITERATION_NUMBER;
        }
        return l_summary / ITERATION_BLOCK_NUMBER;
    }
    //=========================DIV================================
    private long testDIVByte()
    {
        long l_summary = 0;
        for(int lb=0;lb<ITERATION_BLOCK_NUMBER;lb++)
        {
            long l_startTime = System.currentTimeMillis();
            for(int li=0;li<ITERATION_NUMBER;li++)
            {
                b_b2 = (byte)(b_b0 / b_b1);
            }
            long l_endTime = System.currentTimeMillis();

            l_summary += ((l_endTime - l_startTime)*L_COEFF)/ITERATION_NUMBER;
        }

        return l_summary / ITERATION_BLOCK_NUMBER;
    }

    private long testDIVShort()
    {
        long l_summary = 0;
        for(int lb=0;lb<ITERATION_BLOCK_NUMBER;lb++)
        {
            long l_startTime = System.currentTimeMillis();
            for(int li=0;li<ITERATION_NUMBER;li++)
            {
                s_b2 = (short)(s_b0 / s_b1);
            }
            long l_endTime = System.currentTimeMillis();

            l_summary += ((l_endTime - l_startTime)*L_COEFF)/ITERATION_NUMBER;
        }

        return l_summary / ITERATION_BLOCK_NUMBER;
    }

    private long testDIVInt()
    {
        long l_summary = 0;
        for(int lb=0;lb<ITERATION_BLOCK_NUMBER;lb++)
        {
            long l_startTime = System.currentTimeMillis();
            for(int li=0;li<ITERATION_NUMBER;li++)
            {
                i_b2 = i_b0 / i_b1;
            }
            long l_endTime = System.currentTimeMillis();

            l_summary += ((l_endTime - l_startTime)*L_COEFF)/ITERATION_NUMBER;
        }

        return l_summary / ITERATION_BLOCK_NUMBER;
    }

    private long testDIVLong()
    {
        long l_summary = 0;
        for(int lb=0;lb<ITERATION_BLOCK_NUMBER;lb++)
        {
            long l_startTime = System.currentTimeMillis();
            for(int li=0;li<ITERATION_NUMBER;li++)
            {
                l_b2 = l_b0 / l_b1;
            }
            long l_endTime = System.currentTimeMillis();

            l_summary += ((l_endTime - l_startTime)*L_COEFF)/ITERATION_NUMBER;
        }
        return l_summary / ITERATION_BLOCK_NUMBER;
    }

    //=========================MOD================================
    private long testMODByte()
    {
        long l_summary = 0;
        for(int lb=0;lb<ITERATION_BLOCK_NUMBER;lb++)
        {
            long l_startTime = System.currentTimeMillis();
            for(int li=0;li<ITERATION_NUMBER;li++)
            {
                b_b2 = (byte)(b_b0 % b_b1);
            }
            long l_endTime = System.currentTimeMillis();

            l_summary += ((l_endTime - l_startTime)*L_COEFF)/ITERATION_NUMBER;
        }

        return l_summary / ITERATION_BLOCK_NUMBER;
    }

    private long testMODShort()
    {
        long l_summary = 0;
        for(int lb=0;lb<ITERATION_BLOCK_NUMBER;lb++)
        {
            long l_startTime = System.currentTimeMillis();
            for(int li=0;li<ITERATION_NUMBER;li++)
            {
                s_b2 = (short)(s_b0 % s_b1);
            }
            long l_endTime = System.currentTimeMillis();

            l_summary += ((l_endTime - l_startTime)*L_COEFF)/ITERATION_NUMBER;
        }

        return l_summary / ITERATION_BLOCK_NUMBER;
    }

    private long testMODInt()
    {
        long l_summary = 0;
        for(int lb=0;lb<ITERATION_BLOCK_NUMBER;lb++)
        {
            long l_startTime = System.currentTimeMillis();
            for(int li=0;li<ITERATION_NUMBER;li++)
            {
                i_b2 = i_b0 % i_b1;
            }
            long l_endTime = System.currentTimeMillis();

            l_summary += ((l_endTime - l_startTime)*L_COEFF)/ITERATION_NUMBER;
        }

        return l_summary / ITERATION_BLOCK_NUMBER;
    }

    private long testMODLong()
    {
        long l_summary = 0;
        for(int lb=0;lb<ITERATION_BLOCK_NUMBER;lb++)
        {
            long l_startTime = System.currentTimeMillis();
            for(int li=0;li<ITERATION_NUMBER;li++)
            {
                l_b2 = l_b0 % l_b1;
            }
            long l_endTime = System.currentTimeMillis();

            l_summary += ((l_endTime - l_startTime)*L_COEFF)/ITERATION_NUMBER;
        }
        return l_summary / ITERATION_BLOCK_NUMBER;
    }

    //=========================SHL================================
    private long testSHLByte()
    {
        long l_summary = 0;
        for(int lb=0;lb<ITERATION_BLOCK_NUMBER;lb++)
        {
            long l_startTime = System.currentTimeMillis();
            for(int li=0;li<ITERATION_NUMBER;li++)
            {
                b_b2 = (byte)(b_b2 << 1);
            }
            long l_endTime = System.currentTimeMillis();

            l_summary += ((l_endTime - l_startTime)*L_COEFF)/ITERATION_NUMBER;
        }

        return l_summary / ITERATION_BLOCK_NUMBER;
    }

    private long testSHLShort()
    {
        long l_summary = 0;
        for(int lb=0;lb<ITERATION_BLOCK_NUMBER;lb++)
        {
            long l_startTime = System.currentTimeMillis();
            for(int li=0;li<ITERATION_NUMBER;li++)
            {
                s_b2 = (short)(s_b2 << 1);
            }
            long l_endTime = System.currentTimeMillis();

            l_summary += ((l_endTime - l_startTime)*L_COEFF)/ITERATION_NUMBER;
        }

        return l_summary / ITERATION_BLOCK_NUMBER;
    }

    private long testSHLInt()
    {
        long l_summary = 0;
        for(int lb=0;lb<ITERATION_BLOCK_NUMBER;lb++)
        {
            long l_startTime = System.currentTimeMillis();
            for(int li=0;li<ITERATION_NUMBER;li++)
            {
                i_b2 = i_b2 << 1;
            }
            long l_endTime = System.currentTimeMillis();

            l_summary += ((l_endTime - l_startTime)*L_COEFF)/ITERATION_NUMBER;
        }

        return l_summary / ITERATION_BLOCK_NUMBER;
    }

    private long testSHLLong()
    {
        long l_summary = 0;
        for(int lb=0;lb<ITERATION_BLOCK_NUMBER;lb++)
        {
            long l_startTime = System.currentTimeMillis();
            for(int li=0;li<ITERATION_NUMBER;li++)
            {
                l_b2 = l_b2 << 1;
            }
            long l_endTime = System.currentTimeMillis();

            l_summary += ((l_endTime - l_startTime)*L_COEFF)/ITERATION_NUMBER;
        }
        return l_summary / ITERATION_BLOCK_NUMBER;
    }

    //=========================SHR================================
    private long testSHRByte()
    {
        long l_summary = 0;
        for(int lb=0;lb<ITERATION_BLOCK_NUMBER;lb++)
        {
            long l_startTime = System.currentTimeMillis();
            for(int li=0;li<ITERATION_NUMBER;li++)
            {
                b_b2 = (byte)(b_b2 >> 1);
            }
            long l_endTime = System.currentTimeMillis();

            l_summary += ((l_endTime - l_startTime)*L_COEFF)/ITERATION_NUMBER;
        }

        return l_summary / ITERATION_BLOCK_NUMBER;
    }

    private long testSHRShort()
    {
        long l_summary = 0;
        for(int lb=0;lb<ITERATION_BLOCK_NUMBER;lb++)
        {
            long l_startTime = System.currentTimeMillis();
            for(int li=0;li<ITERATION_NUMBER;li++)
            {
                s_b2 = (short)(s_b2 >> 1);
            }
            long l_endTime = System.currentTimeMillis();

            l_summary += ((l_endTime - l_startTime)*L_COEFF)/ITERATION_NUMBER;
        }

        return l_summary / ITERATION_BLOCK_NUMBER;
    }

    private long testSHRInt()
    {
        long l_summary = 0;
        for(int lb=0;lb<ITERATION_BLOCK_NUMBER;lb++)
        {
            long l_startTime = System.currentTimeMillis();
            for(int li=0;li<ITERATION_NUMBER;li++)
            {
                i_b2 = i_b2 >> 1;
            }
            long l_endTime = System.currentTimeMillis();

            l_summary += ((l_endTime - l_startTime)*L_COEFF)/ITERATION_NUMBER;
        }

        return l_summary / ITERATION_BLOCK_NUMBER;
    }

    private long testSHRLong()
    {
        long l_summary = 0;
        for(int lb=0;lb<ITERATION_BLOCK_NUMBER;lb++)
        {
            long l_startTime = System.currentTimeMillis();
            for(int li=0;li<ITERATION_NUMBER;li++)
            {
                l_b2 = l_b2 >> 1;
            }
            long l_endTime = System.currentTimeMillis();

            l_summary += ((l_endTime - l_startTime)*L_COEFF)/ITERATION_NUMBER;
        }
        return l_summary / ITERATION_BLOCK_NUMBER;
    }

    //=========================SHRS================================
    private long testSHRSByte()
    {
        long l_summary = 0;
        for(int lb=0;lb<ITERATION_BLOCK_NUMBER;lb++)
        {
            long l_startTime = System.currentTimeMillis();
            for(int li=0;li<ITERATION_NUMBER;li++)
            {
                b_b2 = (byte)(b_b2 >>> 1);
            }
            long l_endTime = System.currentTimeMillis();

            l_summary += ((l_endTime - l_startTime)*L_COEFF)/ITERATION_NUMBER;
        }

        return l_summary / ITERATION_BLOCK_NUMBER;
    }

    private long testSHRSShort()
    {
        long l_summary = 0;
        for(int lb=0;lb<ITERATION_BLOCK_NUMBER;lb++)
        {
            long l_startTime = System.currentTimeMillis();
            for(int li=0;li<ITERATION_NUMBER;li++)
            {
                s_b2 = (short)(s_b2 >>> 1);
            }
            long l_endTime = System.currentTimeMillis();

            l_summary += ((l_endTime - l_startTime)*L_COEFF)/ITERATION_NUMBER;
        }

        return l_summary / ITERATION_BLOCK_NUMBER;
    }

    private long testSHRSInt()
    {
        long l_summary = 0;
        for(int lb=0;lb<ITERATION_BLOCK_NUMBER;lb++)
        {
            long l_startTime = System.currentTimeMillis();
            for(int li=0;li<ITERATION_NUMBER;li++)
            {
                i_b2 = i_b2 >>> 1;
            }
            long l_endTime = System.currentTimeMillis();

            l_summary += ((l_endTime - l_startTime)*L_COEFF)/ITERATION_NUMBER;
        }

        return l_summary / ITERATION_BLOCK_NUMBER;
    }

    private long testSHRSLong()
    {
        long l_summary = 0;
        for(int lb=0;lb<ITERATION_BLOCK_NUMBER;lb++)
        {
            long l_startTime = System.currentTimeMillis();
            for(int li=0;li<ITERATION_NUMBER;li++)
            {
                l_b2 = l_b2 >>> 1;
            }
            long l_endTime = System.currentTimeMillis();

            l_summary += ((l_endTime - l_startTime)*L_COEFF)/ITERATION_NUMBER;
        }
        return l_summary / ITERATION_BLOCK_NUMBER;
    }
    //=========================SUB================================
    private long testSUBByte()
    {
        long l_summary = 0;
        for(int lb=0;lb<ITERATION_BLOCK_NUMBER;lb++)
        {
            long l_startTime = System.currentTimeMillis();
            for(int li=0;li<ITERATION_NUMBER;li++)
            {
                b_b2 = (byte)(b_b0 - b_b1);
            }
            long l_endTime = System.currentTimeMillis();

            l_summary += ((l_endTime - l_startTime)*L_COEFF)/ITERATION_NUMBER;
        }

        return l_summary / ITERATION_BLOCK_NUMBER;
    }

    private long testSUBShort()
    {
        long l_summary = 0;
        for(int lb=0;lb<ITERATION_BLOCK_NUMBER;lb++)
        {
            long l_startTime = System.currentTimeMillis();
            for(int li=0;li<ITERATION_NUMBER;li++)
            {
                s_b2 = (short)(s_b0 - s_b1);
            }
            long l_endTime = System.currentTimeMillis();

            l_summary += ((l_endTime - l_startTime)*L_COEFF)/ITERATION_NUMBER;
        }

        return l_summary / ITERATION_BLOCK_NUMBER;
    }

    private long testSUBInt()
    {
        long l_summary = 0;
        for(int lb=0;lb<ITERATION_BLOCK_NUMBER;lb++)
        {
            long l_startTime = System.currentTimeMillis();
            for(int li=0;li<ITERATION_NUMBER;li++)
            {
                i_b2 = i_b0 - i_b1;
            }
            long l_endTime = System.currentTimeMillis();

            l_summary += ((l_endTime - l_startTime)*L_COEFF)/ITERATION_NUMBER;
        }

        return l_summary / ITERATION_BLOCK_NUMBER;
    }

    private long testSUBLong()
    {
        long l_summary = 0;
        for(int lb=0;lb<ITERATION_BLOCK_NUMBER;lb++)
        {
            long l_startTime = System.currentTimeMillis();
            for(int li=0;li<ITERATION_NUMBER;li++)
            {
                l_b2 = l_b0 - l_b1;
            }
            long l_endTime = System.currentTimeMillis();

            l_summary += ((l_endTime - l_startTime)*L_COEFF)/ITERATION_NUMBER;
        }
        return l_summary / ITERATION_BLOCK_NUMBER;
    }
    //========================================================

    public String getCustomCommandItem(int _commandId)
    {
        return null;
    }

    public Object getResult()
    {
        return p_ResultBuffer.toString();
    }

    public String getTestName()
    {
        return "Arithmetic test";
    }
}
