package com.igormaznitsa.midp.MIDPTestMidlet.Tests.Common;

import com.igormaznitsa.midp.MIDPTestMidlet.Testlet.VisualTest;
import com.igormaznitsa.midp.MIDPTestMidlet.Testlet.TestletListener;
import com.igormaznitsa.midp.LanguageBlock;

import javax.microedition.lcdui.Graphics;
import java.util.Random;

public class RNDtest extends VisualTest
{
    public static final int MAX_LEVEL = 99;
    public static final int ITERATIONS = 1000;

    private int [] ai_result;
    private Random p_rnd;

    public void init(TestletListener _listener, LanguageBlock _languageBlock, int _backStringIndex, int _waitPleaseItem, int _cancelStringIndex)
    {
        ai_result = new int[MAX_LEVEL+1];
        p_rnd = new Random(System.currentTimeMillis());
        super.init(_listener,_languageBlock,_backStringIndex,_waitPleaseItem,_cancelStringIndex,false);
    }

    public RNDtest()
    {
        super();
    }

     public void Paint(Graphics _graphics)
    {
        int i_maxValue = 0;
        int i_linewidth;
        for(int li=0;li<ai_result.length;li++)
        {
            if (ai_result[li]>i_maxValue) i_maxValue = ai_result[li];
        }

        i_maxValue <<= 8;
        int i8_widthCoeff = ((getScreenWidth()-2)<<8)/(MAX_LEVEL+1);

		i_linewidth = (i8_widthCoeff>>8)==0? 1 : (i8_widthCoeff+0xFF)>>8;

        _graphics.setColor(0xFFFFFFFF);
        _graphics.fillRect(0,0,getScreenWidth(),getScreenHeight());

        _graphics.setColor(0x00);
        _graphics.drawRect(0,0,getScreenWidth()-1,getScreenHeight()-1);

        int i_hgt = getScreenHeight() - 1;

        _graphics.setColor(0x0000FF);

        for(int li=0;li<=MAX_LEVEL;li++)
        {
            int i_curValue = ai_result[li];
            int i_height = (((i_curValue<<16) / i_maxValue) * i_hgt)>>8;
            int i_x = ((i8_widthCoeff * li)>>8)+1;
            _graphics.fillRect(i_x,i_hgt - i_height,i_linewidth,i_height);
        }
    }

    public Object getResult()
    {
        return ai_result;
    }

    public String getTestName()
    {
        return "RND test";
    }

    public boolean startTest()
    {
        for(int li=0;li<MAX_LEVEL;li++) ai_result[li] = 0;

        for(int lt=0;lt<ITERATIONS;lt++)
        {
            for(int li=0;li<MAX_LEVEL;li++)
            {
                ai_result[getRandomInt(MAX_LEVEL)]++;
            }
        }

        Completed();
        return true;
    }

    private int getRandomInt(int _limit)
    {
        _limit++;
        _limit = (int) (((long) Math.abs(p_rnd.nextInt()) * (long) _limit) >>> 31);
        return _limit;
    }

}
