package com.igormaznitsa.midp.MIDPTestMidlet.Tests;

import com.igormaznitsa.midp.LanguageBlock;
import com.igormaznitsa.midp.MenuBlock;
import com.igormaznitsa.midp.MIDPTestMidlet.Testlet.VisualTest;
import com.igormaznitsa.midp.MIDPTestMidlet.Testlet.Testlet;

import javax.microedition.midlet.MIDlet;
import javax.microedition.midlet.MIDletStateChangeException;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Canvas;
import javax.microedition.lcdui.Graphics;

public class TestMIDlet extends MIDlet implements Runnable
{
    LanguageBlock p_LangBlock;
    MenuBlock p_MenuBlock;
    Display p_Display;

    public class insideCanvas extends Canvas
    {
        public int i_displayWidth,i_displayHeight;
        private VisualTest p_testlet;

        public insideCanvas()
        {
            super();
            i_displayWidth = this.getWidth();
            i_displayHeight = this.getHeight();
        }

        public void setVisualTestlet(VisualTest _visualtestlet)
        {
            p_testlet = _visualtestlet;
        }

        protected void paint(Graphics _graphics)
        {
            if (p_testlet == null)
            {
                _graphics.setColor(0xFFFFFF);
                _graphics.fillRect(0, 0, i_displayWidth, i_displayHeight);
            }
            else
            if (p_testlet.i_state == Testlet.STATE_COMPLETED)
            {
                p_testlet.Paint(_graphics);
            }
            else
            {
                _graphics.setColor(0xFFFFFF);
                _graphics.fillRect(0, 0, i_displayWidth, i_displayHeight);
                _graphics.setColor(0x0);
                _graphics.drawString(p_testlet.s_waitPlease, 0, _graphics.getFont().getHeight() * 2, Graphics.VCENTER | Graphics.HCENTER);
            }
        }
    }

    protected void destroyApp(boolean b) throws MIDletStateChangeException
    {
    }

    protected void pauseApp()
    {
    }

    protected void startApp() throws MIDletStateChangeException
    {
        p_Display = Display.getDisplay(this);
        System.out.println("Start midp");
        try
        {
            p_LangBlock = new LanguageBlock("/res/langs.bin", 0);
            p_MenuBlock = new MenuImpl(null,p_Display, "/res/menu.bin", p_LangBlock, null);
        }
        catch (Exception e)
        {
            System.out.println("ERROR: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }

        new Thread(this).start();
    }

    public void run()
    {
        try
        {
            p_MenuBlock.initScreen(SCR_MainScreen);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        while (p_MenuBlock.lg_Active)
        {
            try
            {
                Thread.sleep(100);
            }
            catch (InterruptedException e)
            {
                break;
            }
        }
        notifyDestroyed();
    }

    private static final int SCR_MainScreen = 8;
}
