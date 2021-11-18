package com.igormaznitsa.midp.MIDPTestMidlet.Tests.Common;

import com.igormaznitsa.midp.MIDPTestMidlet.Testlet.NonVisualTest;
import com.igormaznitsa.midp.MIDPTestMidlet.Testlet.TestletListener;
import com.igormaznitsa.midp.LanguageBlock;

public class StringTest extends NonVisualTest
{
    StringBuffer p_ResultBuffer;

    public static final int ITERATION_STRING_NUMBER = 40000;
    public static final int ITERATION_STRING_BLOCK_NUMBER = 1;

    public transient StringBuffer p_tstBuffer;
    public transient String s_s0,s_s1,s_s2;

    public transient byte b_b;
    public transient short s_b;
    public transient int i_b;
    public transient long l_b;

    private static final long L_COEFF = 10000l;

    public void init(TestletListener _listener, LanguageBlock _languageBlock, int _backStringIndex, int _waitPleaseItem, int _cancelStringIndex)
    {
        super.init(_listener,_languageBlock,_backStringIndex,_waitPleaseItem,_cancelStringIndex,false);
    }

    public boolean startTest()
    {
        super.startTest();

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

        p_ResultBuffer = new StringBuffer();

        p_ResultBuffer.append("+(pure) :");
        p_ResultBuffer.append(testStringAddPure());
        p_ResultBuffer.append('\n');

        p_ResultBuffer.append("+(buff) :");
        p_ResultBuffer.append(testStringAddBuffer());
        p_ResultBuffer.append('\n');

        p_ResultBuffer.append("getChar :");
        p_ResultBuffer.append(testStringGetChar());
        p_ResultBuffer.append('\n');

        p_ResultBuffer.append("getSubstr :");
        p_ResultBuffer.append(testStringGetSubstring());
        p_ResultBuffer.append('\n');

        p_ResultBuffer.append("toByte :");
        p_ResultBuffer.append(testStringToByte());
        p_ResultBuffer.append('\n');

        p_ResultBuffer.append("toShort :");
        p_ResultBuffer.append(testStringToShort());
        p_ResultBuffer.append('\n');

        p_ResultBuffer.append("toInt :");
        p_ResultBuffer.append(testStringToInt());
        p_ResultBuffer.append('\n');

        p_ResultBuffer.append("toLong :");
        p_ResultBuffer.append(testStringToLong());
        p_ResultBuffer.append('\n');

        Thread.currentThread().setPriority(Thread.NORM_PRIORITY);

        this.Completed();
        return true;
    }

    //=================Testing blocks ========================
    private long testStringAddPure()
    {
        s_s0 = " ";
        s_s1 = " ";
        s_s2 = " ";

        long l_summary = 0;
        for(int lb=0;lb<ITERATION_STRING_BLOCK_NUMBER;lb++)
        {
            long l_startTime = System.currentTimeMillis();
            for(int li=0;li<ITERATION_STRING_NUMBER;li++)
            {
                s_s0 = s_s1 + s_s2;
            }
            long l_endTime = System.currentTimeMillis();

            l_summary += ((l_endTime - l_startTime)*L_COEFF)/ITERATION_STRING_NUMBER;
        }

        Runtime.getRuntime().gc();
        try
        {
            Thread.sleep(100);
        }
        catch (InterruptedException e)
        {
            return 0;
        }

        return l_summary / ITERATION_STRING_BLOCK_NUMBER;
    }

    private long testStringAddBuffer()
    {
        s_s0 = " ";
        s_s1 = " ";
        s_s2 = " ";

        long l_summary = 0;
        for(int lb=0;lb<ITERATION_STRING_BLOCK_NUMBER;lb++)
        {
            long l_startTime = System.currentTimeMillis();
            for(int li=0;li<ITERATION_STRING_NUMBER;li++)
            {
                p_tstBuffer = new StringBuffer();
                p_tstBuffer.append(s_s1);
                p_tstBuffer.append(s_s2);
                s_s0 = p_tstBuffer.toString();
            }
            long l_endTime = System.currentTimeMillis();

            l_summary += ((l_endTime - l_startTime)*L_COEFF)/ITERATION_STRING_NUMBER;
        }

        Runtime.getRuntime().gc();
        try
        {
            Thread.sleep(100);
        }
        catch (InterruptedException e)
        {
            return 0;
        }

        return l_summary / ITERATION_STRING_BLOCK_NUMBER;
    }

    private long testStringGetChar()
    {
        s_s0 = "0123456789 ";
        s_s1 = " ";
        s_s2 = " ";
        char c_ch = ' ';

        long l_summary = 0;
        for(int lb=0;lb<ITERATION_STRING_BLOCK_NUMBER;lb++)
        {
            long l_startTime = System.currentTimeMillis();
            for(int li=0;li<ITERATION_STRING_NUMBER;li++)
            {
                c_ch = s_s0.charAt(4);
            }
            long l_endTime = System.currentTimeMillis();

            s_s2 = ""+c_ch;

            l_summary += ((l_endTime - l_startTime)*L_COEFF)/ITERATION_STRING_NUMBER;
        }

        Runtime.getRuntime().gc();
        try
        {
            Thread.sleep(100);
        }
        catch (InterruptedException e)
        {
            return 0;
        }

        return l_summary / ITERATION_STRING_BLOCK_NUMBER;
    }

    private long testStringGetSubstring()
    {
        s_s0 = "0123456789 ";
        s_s1 = " ";
        s_s2 = " ";

        long l_summary = 0;
        for(int lb=0;lb<ITERATION_STRING_BLOCK_NUMBER;lb++)
        {
            long l_startTime = System.currentTimeMillis();
            for(int li=0;li<ITERATION_STRING_NUMBER;li++)
            {
                s_s2 = s_s0.substring(3,6);
            }
            long l_endTime = System.currentTimeMillis();

            l_summary += ((l_endTime - l_startTime)*L_COEFF)/ITERATION_STRING_NUMBER;
        }
        Runtime.getRuntime().gc();
        try
        {
            Thread.sleep(100);
        }
        catch (InterruptedException e)
        {
            return 0;
        }

        return l_summary / ITERATION_STRING_BLOCK_NUMBER;
    }

    private long testStringToByte()
    {
        s_s0 = "";
        s_s1 = "";
        s_s2 = "";

        b_b = 0x7F;

        long l_summary = 0;
        for(int lb=0;lb<ITERATION_STRING_BLOCK_NUMBER;lb++)
        {
            long l_startTime = System.currentTimeMillis();
            for(int li=0;li<ITERATION_STRING_NUMBER;li++)
            {
                s_s0 = ""+b_b;
            }
            long l_endTime = System.currentTimeMillis();

            l_summary += ((l_endTime - l_startTime)*L_COEFF)/ITERATION_STRING_NUMBER;
        }
        Runtime.getRuntime().gc();
        try
        {
            Thread.sleep(100);
        }
        catch (InterruptedException e)
        {
            return 0;
        }

        return l_summary / ITERATION_STRING_BLOCK_NUMBER;
    }

    private long testStringToShort()
    {
        s_s0 = "";
        s_s1 = "";
        s_s2 = "";

        s_b = 0x7FFF;

        long l_summary = 0;
        for(int lb=0;lb<ITERATION_STRING_BLOCK_NUMBER;lb++)
        {
            long l_startTime = System.currentTimeMillis();
            for(int li=0;li<ITERATION_STRING_NUMBER;li++)
            {
                s_s0 = ""+s_b;
            }
            long l_endTime = System.currentTimeMillis();

            l_summary += ((l_endTime - l_startTime)*L_COEFF)/ITERATION_STRING_NUMBER;
        }
        Runtime.getRuntime().gc();
        try
        {
            Thread.sleep(100);
        }
        catch (InterruptedException e)
        {
            return 0;
        }

        return l_summary / ITERATION_STRING_BLOCK_NUMBER;
    }

    private long testStringToInt()
    {
        s_s0 = "";
        s_s1 = "";
        s_s2 = "";

        i_b = 0x7FFFFFFF;

        long l_summary = 0;
        for(int lb=0;lb<ITERATION_STRING_BLOCK_NUMBER;lb++)
        {
            long l_startTime = System.currentTimeMillis();
            for(int li=0;li<ITERATION_STRING_NUMBER;li++)
            {
                s_s0 = ""+i_b;
            }
            long l_endTime = System.currentTimeMillis();

            l_summary += ((l_endTime - l_startTime)*L_COEFF)/ITERATION_STRING_NUMBER;
        }
        Runtime.getRuntime().gc();
        try
        {
            Thread.sleep(100);
        }
        catch (InterruptedException e)
        {
            return 0;
        }

        return l_summary / ITERATION_STRING_BLOCK_NUMBER;
    }

    private long testStringToLong()
    {
        s_s0 = "";
        s_s1 = "";
        s_s2 = "";

        l_b = 0x7FFFFFFFFFFFFFFFl;

        long l_summary = 0;
        for(int lb=0;lb<ITERATION_STRING_BLOCK_NUMBER;lb++)
        {
            long l_startTime = System.currentTimeMillis();
            for(int li=0;li<ITERATION_STRING_NUMBER;li++)
            {
                s_s0 = ""+l_b;
            }
            long l_endTime = System.currentTimeMillis();

            l_summary += ((l_endTime - l_startTime)*L_COEFF)/ITERATION_STRING_NUMBER;
        }
        Runtime.getRuntime().gc();
        try
        {
            Thread.sleep(100);
        }
        catch (InterruptedException e)
        {
            return 0;
        }

        return l_summary / ITERATION_STRING_BLOCK_NUMBER;
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
        return "String test";
    }
}
