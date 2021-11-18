package com.igormaznitsa.midp.MIDPTestMidlet.Tests;

import com.igormaznitsa.midp.MenuBlock;
import com.igormaznitsa.midp.LanguageBlock;
import com.igormaznitsa.midp.ImageBlock;
import com.igormaznitsa.midp.MIDPTestMidlet.Testlet.TestletListener;
import com.igormaznitsa.midp.MIDPTestMidlet.Testlet.Testlet;
import com.igormaznitsa.midp.MIDPTestMidlet.Testlet.NonVisualTest;
import com.igormaznitsa.midp.MIDPTestMidlet.Testlet.VisualTest;

import javax.microedition.lcdui.*;
import java.io.IOException;

public class MenuImpl extends MenuBlock implements TestletListener
{
    Displayable p_currentTestInterface;
    Canvas p_canvas;

    public MenuImpl(Canvas _canvas,Display _display, String _menuResource, LanguageBlock _languageBlock, ImageBlock _imageBlock) throws IOException
    {
        super(_display, _menuResource, _languageBlock, _imageBlock);
        p_canvas = _canvas;
    }

    public Object customItem(int _screenId, int _itemId, boolean _getImage)
    {
        return null;
    }

    public void customScreen(int _screenId, Displayable _screen)
    {
    }

    public boolean enableCommand(int _screenId, int _commandId)
    {
        return true;
    }

    public boolean enableItem(int _screenId, int _itemId)
    {
        return true;
    }

    public void onExitScreen(Displayable _screen, int _screenId)
    {
    }

    public void processCommand(int _screenId, int _commandId)
    {
    }

    private void activateTest(int _itemId)
    {
        String s_className = null;
        switch(_itemId)
        {
            case ITEM_ArithTestItem : s_className   = "com.igormaznitsa.midp.MIDPTestMidlet.Tests.Common.ArithSpeedTest"    ;break;
            case ITEM_RNDTestItem : s_className     = "com.igormaznitsa.midp.MIDPTestMidlet.Tests.Common.RNDtest"           ;break;
            case ITEM_StringTestItem: s_className   = "com.igormaznitsa.midp.MIDPTestMidlet.Tests.Common.StringTest"        ;break;
            case ITEM_ExitItem : deactivateMenu(); return;
        }

        if (s_className != null)
        {
            try
            {
                Class p_class = Class.forName(s_className);
                Testlet p_testlet = (Testlet) p_class.newInstance();

                if (p_testlet instanceof NonVisualTest)
                {
                    NonVisualTest p_nv = (NonVisualTest) p_testlet;
                    p_nv.init(this,p_languageBlock,BackCMD,WaitPleaseStr,CancelStr);
                    p_testlet = p_nv;

                }
                else
                if (p_testlet instanceof VisualTest)
                {
                    VisualTest p_nv = (VisualTest) p_testlet;
                    p_nv.init(this,p_languageBlock,BackCMD,WaitPleaseStr,CancelStr);
                    p_nv.p_canvas = p_canvas;
                    p_testlet = p_nv;
                }

                Displayable p_dsp = p_testlet.getInerface();
                p_Display.setCurrent(p_dsp);
                p_testlet.startTest();
            }
            catch (Exception e)
            {
                Alert p_alertError = new Alert("Test error",e.getMessage(),null,AlertType.ERROR);
                p_alertError.setTimeout(Alert.FOREVER);
                p_Display.setCurrent(p_alertError);
            }
        }
    }

    public void processListItem(int _screenId, int _itemId)
    {
        activateTest(_itemId);
    }

//=====================================================
    public void endTest(Testlet _testlet)
    {
        p_currentTestInterface = null;
        reinitScreen();
        Runtime.getRuntime().gc();
    }

    public void testCanceled(Testlet _testlet)
    {
        p_currentTestInterface = null;
        reinitScreen();
        Runtime.getRuntime().gc();
    }

    public void testCompleted(Testlet _testlet)
    {
        if (p_currentTestInterface != null)
        {
            if (p_currentTestInterface instanceof Canvas)
                ((Canvas) p_currentTestInterface).repaint();
        }
    }

//===================================================================
    private static final int ExitItemText = 0;
    private static final int CancelStr = 1;
    private static final int CommonTestItemText = 2;
    private static final int MainScreenTitle = 3;
    private static final int ArithmeticTestText = 4;
    private static final int StringTestText = 5;
    private static final int RNDTestText = 6;
    private static final int SiemensTestItemText = 7;
    private static final int BackCMD = 8;
    private static final int NokiaTestItemText = 9;
    private static final int WaitPleaseStr = 10;

//=======================================================================
// Screens

// Screen SiemensScreen
    private static final int SCR_SiemensScreen = 0;
// Screen MainScreen
    private static final int SCR_MainScreen = 8;
// Screen CommonScreen
    private static final int SCR_CommonScreen = 34;
// Screen NokiaScreen
    private static final int SCR_NokiaScreen = 57;

// Items

// Item StringTestItem
    private static final int ITEM_StringTestItem = 5;
// Item NokiaTestsItem
    private static final int ITEM_NokiaTestsItem = 1;
// Item RNDTestItem
    private static final int ITEM_RNDTestItem = 6;
// Item ExitItem
    private static final int ITEM_ExitItem = 3;
// Item SiemensTestsItem
    private static final int ITEM_SiemensTestsItem = 2;
// Item ArithTestItem
    private static final int ITEM_ArithTestItem = 4;
// Item CommonTestsItem
    private static final int ITEM_CommonTestsItem = 0;

// Commands

// Command BackCMD
    private static final int COMMAND_BackCMD = 65;
}
