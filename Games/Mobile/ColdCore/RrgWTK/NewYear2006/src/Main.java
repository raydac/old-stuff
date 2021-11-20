import ru.coldcore.SMSSender;

import javax.microedition.lcdui.*;
import javax.microedition.midlet.MIDlet;
import javax.microedition.midlet.MIDletStateChangeException;

public class Main extends MIDlet implements Runnable
{
    //==============================================================

    //#if SHOWSYS
    public static String s_CurrentExceptionMessage = null;
    //#endif

    public static final int STATE_INITING = 0;
    public static final int STATE_LOADING = 1;
    public static final int STATE_SPLASH = 2;
    public static final int STATE_WORKING = 3;
    public static final int STATE_WAITING = 4;
    public static final int STATE_RELEASING = 5;

    public static int i_CurrentMidletState = STATE_INITING;
    public static Display p_CurrentDisplay;
    protected static Canvas p_MainCanvas;

    private static int i_BackgroundColor = 0xFFFFFF;
    private static int i_ForegroundColor = 0x000000;

    public static Object p_synchroObject = new Object();

    public static LanguageBlock p_languageBlock;

    private static final String CLOCK_IMAGE = "/clock.png";
    private static final String LOADING_LOGO = "/loading.png";
    private static final String SPLASH_LOGO = "/splash";
    private static final String RESOURCE_LANGUAGELIST = "/langs.bin";
    private static final String RESOURCE_MENU = "/menu.bin";

    public static Image p_loadingLogo;
    public static Image p_splashLogo;

    public static Image p_Image_FontImage;

    public static int i_loadingProgress;

    private static int i_Option_LanguageID;

    protected void startApp() throws MIDletStateChangeException
    {
        if (i_CurrentMidletState == STATE_INITING)
        {
            Image p_clockImage = null;

            try
            {
                p_clockImage = Image.createImage(CLOCK_IMAGE);
            }
            catch (Throwable e)
            {
            }

            p_CurrentDisplay = Display.getDisplay(this);
            i_BackgroundColor = 0x000000;
            i_ForegroundColor = 0xFFFFFF;

            p_MainCanvas = null;

            //#if FCNOKIA && MIDP!="2.0"
            if (!SMSSender.isMIDP20())
            {
                // ѕровер€ем на наличие FullCanvas
                try
                {
                    Class p_class = Class.forName("com.nokia.mid.ui.FullCanvas");
                    p_class = Class.forName("NkFc");
                    p_MainCanvas = (Canvas) p_class.newInstance();
                    p_MainCanvas.equals(new int[]{i_BackgroundColor,i_ForegroundColor});
                    p_MainCanvas.equals(p_clockImage);
                }
                catch (Throwable e)
                {
                    p_MainCanvas = null;
                }
            }
            //#endif

            //#if FCMIDP20
            if (p_MainCanvas==null)
            {
                p_MainCanvas = new InsideCanvas(i_BackgroundColor, i_ForegroundColor, p_clockImage);
                SMSSender.setFullCanvas(p_MainCanvas,true);
                ((InsideCanvas)p_MainCanvas).setWidthHeight(p_MainCanvas.getWidth(),p_MainCanvas.getHeight());
            }
            //#endif

            p_CurrentDisplay.setCurrent(p_MainCanvas);

            p_languageBlock = new LanguageBlock();

            try
            {
                p_loadingLogo = Image.createImage(LOADING_LOGO);
            }
            catch (Throwable e)
            {
                p_loadingLogo = null;
            }

            new Thread(this).start();
        }
        else
        {
            i_CurrentMidletState = STATE_WAITING;
        }
    }

    protected void pauseApp()
    {
    }


    protected void destroyApp(boolean b) throws MIDletStateChangeException
    {
        if (i_CurrentMidletState == STATE_INITING || i_CurrentMidletState == STATE_LOADING)
        {
            if (!b) throw new MIDletStateChangeException();
            i_CurrentMidletState = STATE_RELEASING;
            p_CurrentDisplay.setCurrent(p_MainCanvas);
            p_MainCanvas.repaint();
        }
        else
        {
            if (i_CurrentMidletState != STATE_RELEASING)
            {
            }
        }
    }

    public static final int COMMAND_NONE = 0;
    public static final int COMMAND_COMMAND = 1;
    public static final int COMMAND_LISTITEM = 2;

    public static Displayable p_ArgScreen;
    public static int i_ArgScreenID;
    public static int i_ArgSelectedID;
    public static int i_ArgCommandID;
    public static int i_ProcessCommand;

    private static synchronized void _showCanvas()
    {
        p_CurrentDisplay.setCurrent(p_MainCanvas);
        while (!p_MainCanvas.isShown())
        {
            try
            {
                Thread.sleep(30);
            }
            catch (Throwable e)
            {
                break;
            }
        }
    }

    private static synchronized void _hiddeCanvas()
    {
        Displayable p_dsp = MenuBlock.MB_p_Form;
        p_CurrentDisplay.setCurrent(p_dsp);
        while (!p_dsp.isShown())
        {
            try
            {
                Thread.sleep(30);
            }
            catch (Throwable e)
            {
                break;
            }
        }
        try
        {
            Thread.sleep(50);
        }
        catch (Throwable e)
        {
        }
    }

    private void _processCommand(Displayable _screen, int _screenId, int _commandId, int _selectedId)
    {
        App.processMenuCommand(_screen, _screenId, _commandId, _selectedId);
    }

    private void _processListItem(int _screenId, int _itemId)
    {
        switch (_screenId)
        {
            case App.SCR_LanguageSelectSCR:
            {
                int i_curMode = i_CurrentMidletState;
                try
                {
                    i_CurrentMidletState = STATE_WAITING;
                    _showCanvas();

                    //#-
                    try
                    {
                        Thread.sleep(2000);
                    }
                    catch(Throwable _t)
                    {
                    }
                    //#+

                    i_Option_LanguageID = _itemId;
                    p_languageBlock.LB_setLanguage(i_Option_LanguageID);

                    packAndSaveOptions();

                    MenuBlock.MB_reinitScreen(true, true);
                    _hiddeCanvas();
                }
                catch (Throwable  e)
                {
                    //#-
                    e.printStackTrace();
                    //#+
                    _hiddeCanvas();
                    MenuBlock.MB_viewAlert("Error", e.getMessage(),null, AlertType.ERROR, Alert.FOREVER, false);
                }
                finally
                {
                    i_CurrentMidletState = i_curMode;
                }
            }
            ;
            break;
            default:
                App.processListItem(_screenId, _itemId);
        }
    }

    protected static final int VIDEOMODE_CANVAS = 0;
    protected static final int VIDEOMODE_MENU = 1;
    protected static final int VIDEOMODE_GAMECANVAS = 2;

    protected static int i_VideoMode = VIDEOMODE_CANVAS;

    public static final Command p_Command_Restart = new Command("Restart", Command.ITEM, 0);
    public static final Command p_Command_Exit = new Command("End game", Command.ITEM, 1);

    public static void changeVideoMode(int _mode)
    {
        if (_mode == i_VideoMode) return;

        switch (_mode)
        {
            case VIDEOMODE_GAMECANVAS:
            case VIDEOMODE_CANVAS:
            {
                _showCanvas();
                if (i_VideoMode != _mode)
                {
                    switch (_mode)
                    {
                        case VIDEOMODE_CANVAS :
                        {
                            i_CurrentMidletState = STATE_WAITING;
                            p_MainCanvas.removeCommand(p_Command_Exit);
                            p_MainCanvas.removeCommand(p_Command_Restart);
                            p_MainCanvas.setCommandListener(null);
                        }
                        ;
                        break;
                        case VIDEOMODE_GAMECANVAS :
                        {
                            i_CurrentMidletState = STATE_WORKING;
                            p_MainCanvas.addCommand(p_Command_Restart);
                            p_MainCanvas.addCommand(p_Command_Exit);
                            p_MainCanvas.setCommandListener((CommandListener)p_MainCanvas);
                        }
                        ;
                        break;
                    }
                    p_MainCanvas.repaint();
                }
            }
            ;
            break;
            case VIDEOMODE_MENU:
            {
                _hiddeCanvas();
            }
            ;
            break;
        }
        i_VideoMode = _mode;
    }

    private static int [] SPLASHFORMATS = new int[]{208,176,132,128};

    private static Image loadSplash(int _width)
    {
        if (_width<=101) return null;
        Image p_result = null;

        // ищем стартовый индекс
        for(int li=0;li<SPLASHFORMATS.length;li++)
        {
            int i_width = SPLASHFORMATS[li];
            if (i_width>_width) continue;

            String s_screenName = SPLASH_LOGO+i_width+".png";
            try
            {
                p_result = Image.createImage(s_screenName);
                break;
            }
            catch (Throwable e)
            {
            }
        }

        if (p_result==null)
        {
            try
            {
                p_result = Image.createImage(SPLASH_LOGO+".png");
            }
            catch (Throwable e)
            {
            }
        }

        return p_result;
    }

    public void run()
    {
        i_loadingProgress = 0;
        i_CurrentMidletState = STATE_LOADING;

        Canvas p_insCan = p_MainCanvas;

        p_insCan.repaint();

        try
        {
            switch (DataStorage.init(App.getID()))
            {
                case DataStorage.STORE_FIRSTSTART:
                {
                    i_Option_LanguageID = -1;
                }
                ;
                break;
                case DataStorage.STORE_INITED:
                {
                    i_Option_LanguageID = DataStorage.ab_OptionsArray[0] & 0xFF;
                    int i_options = DataStorage.ab_OptionsArray[1];
                }
                ;
                break;
                case DataStorage.STORE_NOMEMORY:
                {
                    Alert p_alert = new Alert("Error", "No memory for data\r\n" + DataStorage.s_Status, null, AlertType.ERROR);
                    p_alert.setTimeout(3000);
                    p_CurrentDisplay.setCurrent(p_alert, p_MainCanvas);
                    try
                    {
                        Thread.sleep(4000);
                    }
                    catch (Throwable  e)
                    {
                    }
                    p_alert = null;
                }
                ;
                break;
            }
            i_loadingProgress += 25;

            p_insCan.repaint();

            p_languageBlock.LB_initLanguageBlock(RESOURCE_LANGUAGELIST, i_Option_LanguageID);
            int i_oldLang = i_Option_LanguageID;
            i_Option_LanguageID = p_languageBlock.LB_i_CurrentLanguageIndex;

            if (i_oldLang!=i_Option_LanguageID)
            {
                packAndSaveOptions();
            }

            i_loadingProgress += 25;

            p_insCan.repaint();

            MenuBlock.MB_initMenuBlock(RESOURCE_MENU);
            i_loadingProgress += 25;

            p_insCan.repaint();

            if (!App.init(p_MainCanvas.getClass(), this, DataStorage.hasSavedData() ? DataStorage.loadDataBlock() : null))
                throw new Exception("Init error");

            i_loadingProgress += 25;

            p_insCan.repaint();

            Thread.sleep(300);

            p_loadingLogo = null;
            Runtime.getRuntime().gc();

            p_splashLogo = loadSplash(p_MainCanvas.getWidth());
            SPLASHFORMATS = null;
        }
        catch (Throwable _ex)
        {
            //#-
            _ex.printStackTrace();
            //#+

            //#if SHOWSYS
            s_CurrentExceptionMessage = _ex.getMessage();

            p_insCan.repaint();

            try
            {
                Thread.sleep(2000);
            }
            catch (Throwable e)
            {
            }
            //#endif

            String s_error = _ex.getMessage();
            if (s_error == null || s_error.trim().length() == 0) s_error = _ex.getClass().getName();
            MenuBlock.MB_viewAlert("Loading error", s_error,null, AlertType.ERROR, 2000,true);

            //#if VENDOR!="SAMSUNG"
            this.notifyDestroyed();
            //#endif
            return;
        }

        final int ANIMATION_DELAY = 3;
        int i_animationDelay = ANIMATION_DELAY;

        //#if SHOWSYS
        try
        {
            //#endif

            if (p_splashLogo != null)
            {
                i_CurrentMidletState = STATE_SPLASH;

                p_insCan.repaint();

                synchronized (p_synchroObject)
                {
                    try
                    {
                        p_synchroObject.wait(3000);
                    }
                    catch (Throwable e)
                    {
                        return;
                    }
                }

                p_splashLogo = null;

                Runtime.getRuntime().gc();

                p_loadingLogo = null;
                i_CurrentMidletState = STATE_WAITING;

                p_insCan.repaint();

                p_splashLogo = null;

                Runtime.getRuntime().gc();
            }
            //#if SHOWSYS
        }
        catch (Throwable _ex)
        {
            s_CurrentExceptionMessage = _ex.getMessage();
            p_insCan.repaint();
        }
        //#endif

        i_CurrentMidletState = STATE_WAITING;
        App.start();

        while (App.lg_isAlive && (i_CurrentMidletState == STATE_WORKING || i_CurrentMidletState == STATE_WAITING))
        {
            if (i_VideoMode != VIDEOMODE_GAMECANVAS)
            {
                try
                {
                    Thread.sleep(20);
                }
                catch (Throwable e)
                {
                    break;
                }
            }
            switch (i_ProcessCommand)
            {
                case COMMAND_COMMAND:
                {
                    _processCommand(p_ArgScreen, i_ArgScreenID, i_ArgCommandID, i_ArgSelectedID);
                }
                ;
                break;
                case COMMAND_LISTITEM:
                {
                    _processListItem(i_ArgScreenID, i_ArgSelectedID);
                }
                ;
                break;
            }
            i_ProcessCommand = COMMAND_NONE;

            //#if GAMEMODE!=""
            if (i_VideoMode == VIDEOMODE_GAMECANVAS)
            {
                App.processStep();
                p_insCan.repaint();
                try
                {
                    Thread.sleep(ru.coldcore.game.Game.getTimedelay());
                }
                catch (Throwable e)
                {
                    break;
                }
            }
            else
            //#endif
            {
                i_animationDelay--;
                if (i_animationDelay <= 0)
                {
                    App.processStep();
                    p_insCan.repaint();
                    i_animationDelay = ANIMATION_DELAY;
                }
            }
        }

        i_CurrentMidletState = STATE_RELEASING;

        _showCanvas();

        p_insCan.repaint();

        App.destroy();
        DataStorage.release();

        this.notifyDestroyed();
    }

    public static void saveDataBlock()
    {
        try
        {
            byte [] ab_dataBlock = App.getDataBlock();
            if (ab_dataBlock != null)
            {
                DataStorage.saveDataBlock(ab_dataBlock);
            }
        }
        catch (Throwable e)
        {
        }
    }

    protected static void packAndSaveOptions()
    {
        try
        {
            DataStorage.ab_OptionsArray[0] = (byte) i_Option_LanguageID;
            int i_packValue = 0;
            DataStorage.ab_OptionsArray[1] = (byte) i_packValue;
            DataStorage.saveOptions();
        }
        catch (Throwable e)
        {
        }
    }

    public static String getStringForIndex(int _index)
    {
        return p_languageBlock.LB_as_TextStringArray[_index];
    }

    public static void processFormItem(int _screenId, int _index, int _commandID, Item _formItem)
    {
        processCommand(MenuBlock.MB_p_Form, _screenId, _commandID, _index);
    }

    public static void processListItem(int _screenId, int _itemId)
    {
        if (i_ProcessCommand != COMMAND_NONE) return;
        p_ArgScreen = null;
        i_ArgScreenID = _screenId;
        i_ArgCommandID = -1;
        i_ArgSelectedID = _itemId;
        i_ProcessCommand = COMMAND_LISTITEM;
    }

    public synchronized static void processCommand(Displayable _screen, int _screenId, int _commandId, int _selectedId)
    {
        if (i_ProcessCommand != COMMAND_NONE) return;
        p_ArgScreen = _screen;
        i_ArgScreenID = _screenId;
        i_ArgCommandID = _commandId;
        i_ArgSelectedID = _selectedId;
        i_ProcessCommand = COMMAND_COMMAND;
    }

    public static boolean enableCommand(int _screenId, int _commandId)
    {
        return App.enableMenuCommand(_screenId, _commandId);
    }

    public static Displayable customScreen(int _screenId)
    {
        Displayable p_result = null;
        switch (_screenId)
        {
            /*
            case App.SCR_OnOffSCR:
            {
                String s_caption = null;
                int i_selIndex = 1;

                switch (MenuBlock.MB_getLastItemSelected())
                {
                    case App.ITEM_BackLightITM:
                        s_caption = getStringForIndex(App.BackLightTXT);
                        i_selIndex = lg_Option_Light ? 0 : 1;
                        break;
                }
                List p_lst = new List(s_caption, List.EXCLUSIVE);
                p_lst.append(getStringForIndex(App.OnTXT), null);
                p_lst.append(getStringForIndex(App.OffTXT), null);
                p_lst.setSelectedIndex(i_selIndex, true);
                return p_lst;
            }
            */
            case App.SCR_LanguageSelectSCR:
            {
                List p_list = new List(getStringForIndex(App.LanguageSelectTXT), List.IMPLICIT);

                for (int li = 0; li < p_languageBlock.LB_as_LanguageNames.length; li++)
                {
                    p_list.append(p_languageBlock.LB_as_LanguageNames[li], null);
                }
                return p_list;
            }
            default:
                p_result = App.customScreen(_screenId);
        }

        return p_result;
    }

    //#if _MENU_ITEM_CUSTOM
    public static Object customItem(int _screenId, int _itemId, boolean _getImage)
    {
        return App.customMenuItem(_screenId, _itemId, _getImage);
    }
    //#endif

    public static boolean enableItem(int _screenId, int _itemId)
    {
        switch (_itemId)
        {
            /*
            case App.ITEM_BackLightITM:
                return false;
                */
            case App.ITEM_LanguageSelectITM:
                return p_languageBlock.LB_as_LanguageNames.length > 1;
            default:
                return App.enableMenuItem(_screenId, _itemId);
        }
    }

    public static void onExitScreen(Displayable _screen, int _screenId)
    {
        switch (_screenId)
        {
            case App.SCR_OptionsSCR:
            {
                packAndSaveOptions();
            }
            ;
            break;
            /*
            case App.SCR_OnOffSCR:
            {
                List p_list = (List) _screen;
                boolean[] alg_flagarray = new boolean[2];
                p_list.getSelectedFlags(alg_flagarray);
                switch (MenuBlock.MB_getLastItemSelected())
                {
                    case App.ITEM_BackLightITM:
                    {
                        lg_Option_Light = alg_flagarray[0];
                    }
                    ;
                    break;
                }
            }
            ;
            break;
            */
        }
    }

}
