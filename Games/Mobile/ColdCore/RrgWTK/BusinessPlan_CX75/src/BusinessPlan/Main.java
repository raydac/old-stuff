package BusinessPlan;

import javax.microedition.midlet.MIDlet;
import javax.microedition.midlet.MIDletStateChangeException;
import javax.microedition.lcdui.*;
import javax.microedition.lcdui.game.Sprite;
import javax.microedition.rms.RecordStoreException;
import java.io.*;
import java.util.Date;

public class Main extends MIDlet implements Runnable, MenuActionListener, AppActionListener
{
    private class InsideCanvas extends Canvas implements Runnable
    {
        protected int i_ScreenWidth;
        protected int i_ScreenHeight;
        protected int i_BackgroundColor;

        protected Font p_MessageFont;
        protected int i_FontColor;

        private static final int COLOR_LOADING_BAR_BACKGROUND = 0x888888;
        private static final int COLOR_LOADING_BAR = 0xFF0000;

        private static final int LOADING_BAR_HEIGHT = 5;
        private static final int LOADING_BAR_WIDTH = 100;
        private static final int LOADING_BAR_OFFSET_FROM_SPLASH = 3;
        private static final int I8_LOADING_BAR_PERCENT = (LOADING_BAR_WIDTH << 8) / 100;

        protected int i_ClockFrameCounter;
        protected Image p_clockImage;

        protected boolean lg_started = true;

        public void run()
        {
            while (lg_started)
            {
                try
                {
                    Thread.sleep(200);
                }
                catch (InterruptedException e)
                {
                    break;
                }

                i_ClockFrameCounter++;
                if (i_ClockFrameCounter >= 8)
                {
                    i_ClockFrameCounter = 0;
                }
                if (isShown()) repaint();
            }

        }

        public InsideCanvas(int _backgroundColor, int _messageTextColor, Image _clockImage)
        {
            super();
            setFullScreenMode(true);

            i_ScreenWidth = getWidth();
            i_ScreenHeight = getHeight();
            i_BackgroundColor = _backgroundColor;
            i_FontColor = _messageTextColor;

            p_clockImage = _clockImage;
            i_ClockFrameCounter = 0;

            p_MessageFont = Font.getFont(Font.FACE_PROPORTIONAL, Font.STYLE_BOLD, Font.SIZE_MEDIUM);

            new Thread(this).start();
        }

        private void drawMessageOnCenterScreen(Graphics _g, String _message)
        {
            int i_stringWidth = p_MessageFont.stringWidth(_message);
            int i_height = p_MessageFont.getHeight();

            _g.setColor(i_FontColor);
            _g.setFont(p_MessageFont);
            _g.drawString(_message, (i_ScreenWidth - i_stringWidth) >> 1, (i_ScreenHeight - i_height) >> 1, 0);
        }

        protected void keyPressed(int _code)
        {
            switch (i_CurrentMidletState)
            {
                case STATE_INITING:
                    {

                    }
                    ;
                    break;
                case STATE_LOADING:
                    {
                    }
                    ;
                    break;
                case STATE_SPLASH:
                    {
                        synchronized (p_synchroObject)
                        {
                            p_synchroObject.notify();
                        }
                    }
                    ;
                    break;
                case STATE_WORKING:
                    {
                    }
                    ;
                    break;
                case STATE_PAUSED:
                    {
                    }
                    ;
                    break;
                case STATE_RELEASING:
                    {
                    }
                    ;
                    break;
            }
        }

        protected void keyReleased(int _code)
        {
            switch (i_CurrentMidletState)
            {
                case STATE_INITING:
                    {
                    }
                    ;
                    break;
                case STATE_LOADING:
                    {
                    }
                    ;
                    break;
                case STATE_WORKING:
                    {
                    }
                    ;
                    break;
                case STATE_PAUSED:
                    {
                    }
                    ;
                    break;
                case STATE_RELEASING:
                    {
                    }
                    ;
                    break;
            }
        }

        public void paint(Graphics _g)
        {
            switch (i_CurrentMidletState)
            {
                case STATE_INITING:
                    {
                        _g.setColor(i_BackgroundColor);
                        _g.fillRect(0, 0, i_ScreenWidth, i_ScreenHeight);
                        drawMessageOnCenterScreen(_g, "Initing...");
                    }
                    ;
                    break;
                case STATE_LOADING:
                    {
                        _g.setColor(0x050505);
                        _g.fillRect(0, 0, i_ScreenWidth, i_ScreenHeight);

                        int i_splImgX = 0;
                        int i_splImgY = 0;

                        if (p_loadingLogo != null)
                        {
                            i_splImgX = (i_ScreenWidth - p_loadingLogo.getWidth()) >> 1;
                            i_splImgY = ((i_ScreenHeight + LOADING_BAR_HEIGHT + LOADING_BAR_OFFSET_FROM_SPLASH) - p_loadingLogo.getHeight()) >> 1;
                            _g.drawImage(p_loadingLogo, i_splImgX, i_splImgY, 0);

                            i_splImgY += p_loadingLogo.getHeight() + LOADING_BAR_OFFSET_FROM_SPLASH;
                        }
                        else
                        {
                            i_splImgY = i_ScreenHeight >> 1;
                        }
                        i_splImgY = i_splImgY > i_ScreenHeight - LOADING_BAR_HEIGHT - 1 ? i_ScreenHeight - LOADING_BAR_HEIGHT - 1 : i_splImgY;

                        i_splImgX = (i_ScreenWidth - LOADING_BAR_WIDTH) >> 1;
                        _g.setColor(COLOR_LOADING_BAR_BACKGROUND);
                        _g.fillRect(i_splImgX, i_splImgY, LOADING_BAR_WIDTH, LOADING_BAR_HEIGHT);
                        _g.setColor(COLOR_LOADING_BAR);
                        _g.fillRect(i_splImgX, i_splImgY, (i_loadingProgress * I8_LOADING_BAR_PERCENT) >> 8, LOADING_BAR_HEIGHT);
                    }
                    ;
                    break;
                case STATE_SPLASH:
                    {
                        if (p_splashLogo != null)
                        {
                            int i_outX = (i_ScreenWidth - p_splashLogo.getWidth()) >> 1;
                            int i_outY = (i_ScreenHeight - p_splashLogo.getHeight()) >> 1;

                            if (i_outX > 0 && i_outY > 0)
                            {
                                _g.setColor(i_BackgroundColor);
                                _g.fillRect(0, 0, i_ScreenWidth, i_ScreenHeight);
                            }

                            _g.drawImage(p_splashLogo, i_outX, i_outY, 0);
                        }
                        else
                        {
                            _g.setColor(i_BackgroundColor);
                            _g.fillRect(0, 0, i_ScreenWidth, i_ScreenHeight);
                        }
                    }
                    ;
                    break;
                case STATE_WORKING:
                    {
                        _g.setColor(i_BackgroundColor);
                        _g.fillRect(0, 0, i_ScreenWidth, i_ScreenHeight);

                        String s_str = getStringForIndex(WaitPleaseTXT);
                        int i_strW = p_MessageFont.stringWidth(s_str);
                        int i_strH = p_MessageFont.getHeight();

                        int i_coordY = 0;
                        int i_coordX = 0;
                        if (p_clockImage != null)
                        {
                            int i_frameWidth = p_clockImage.getWidth() / 8;
                            int i_frameHeight = p_clockImage.getHeight();

                            i_coordX = (i_ScreenWidth - i_frameWidth) >> 1;
                            i_coordY = (i_ScreenHeight - i_frameHeight) >> 1;

                            _g.setClip(i_coordX, i_coordY, i_frameWidth, i_frameHeight);
                            _g.drawImage(p_clockImage, i_coordX - i_frameWidth * i_ClockFrameCounter, i_coordY, 0);
                            _g.setClip(0, 0, i_ScreenWidth, i_ScreenHeight);
                        }
                        else
                        {
                            i_coordY = (i_ScreenHeight - i_strH) >> 1;
                        }
                        i_coordX = (i_ScreenWidth - i_strW) >> 1;
                        _g.setColor(i_FontColor);
                        _g.drawString(s_str, i_coordX, i_coordY, Graphics.LEFT | Graphics.BOTTOM);
                    }
                    ;
                    break;
                case STATE_PAUSED:
                    {
                        _g.setColor(i_BackgroundColor);
                        _g.fillRect(0, 0, i_ScreenWidth, i_ScreenHeight);
                        drawMessageOnCenterScreen(_g, "Paused...");
                    }
                    ;
                    break;
                case STATE_RELEASING:
                    {
                        _g.setColor(i_BackgroundColor);
                        _g.fillRect(0, 0, i_ScreenWidth, i_ScreenHeight);
                        drawMessageOnCenterScreen(_g, "Releasing...");
                    }
                    ;
                    break;
            }
        }
    }

    private static final int STATE_INITING = 0;
    private static final int STATE_LOADING = 1;
    private static final int STATE_SPLASH = 2;
    private static final int STATE_WORKING = 3;
    private static final int STATE_PAUSED = 4;
    private static final int STATE_RELEASING = 5;

    private int i_CurrentMidletState = STATE_INITING;
    private Display p_CurrentDisplay;
    private InsideCanvas p_MainCanvas;

    private int i_BackgroundColor = 0xFFFFFF;
    private int i_ForegroundColor = 0x000000;

    protected Image[] ap_icons;

    private Object p_synchroObject = new Object();

    private ProjectsContainer p_model;
    private LanguageBlock p_languageBlock;
    private MenuBlock p_menuBlock;
    private rmsFS p_rmsBlock;

    private static final String CLOCK_IMAGE = "/res/clock.png";
    private static final String LOADING_LOGO = "/res/loading.png";
    private static final String SPLASH_LOGO = "/res/splash.png";
    private static final String RESOURCE_LANGUAGELIST = "/res/langs.bin";
    private static final String RESOURCE_MENU = "/res/menu.bin";
    private static final String RESOURCE_ABOUT_ICON = "/res/abouticon.png";
    private static final String RESOURCE_ICONS = "/res/icons.png";
    private static final String RMS_OPTIONS_RECORD_NAME = "%$OPTNS$%";

    private Image p_loadingLogo;
    private Image p_splashLogo;

    private int i_askCommand;

    private int i_loadingProgress;

    private int i_Option_LanguageID;
    private boolean lg_Option_Light;
    private boolean lg_Option_Vibration;
    private boolean lg_Option_Sound;
    private boolean lg_Option_Autostatistics;

    private Image p_aboutIcon;

    private static Image[] parseImage(Image _source, int _itemNumber)
    {
        int i_itemWidth = _source.getWidth() / _itemNumber;
        int i_itemHeight = _source.getHeight();
        Image[] ap_result = new Image[_itemNumber];
        int i_xoffset = 0;
        for (int li = 0; li < _itemNumber; li++)
        {
            Image p_newImage = Image.createImage(_source, i_xoffset, 0, i_itemWidth, i_itemHeight, Sprite.TRANS_NONE);
            ap_result[li] = p_newImage;
            i_xoffset += i_itemWidth;
        }
        return ap_result;
    }

    private void loadOptionsFromRMS() throws RecordStoreException, IOException
    {
        rmsFS.FSRecord p_record = p_rmsBlock.getRecordForName(RMS_OPTIONS_RECORD_NAME);
        if (p_record == null)
        {
            i_Option_LanguageID = -1;
            lg_Option_Autostatistics = false;
            lg_Option_Light = true;
            lg_Option_Autostatistics = false;
            lg_Option_Vibration = true;
            lg_Option_Sound = true;
            p_rmsBlock.createNewRecord(RMS_OPTIONS_RECORD_NAME, 32);
        }
        else
        {
            try
            {
                byte[] ab_data = p_record.getData();
                ByteArrayInputStream p_inStr = new ByteArrayInputStream(ab_data);
                DataInputStream p_dataStream = new DataInputStream(p_inStr);
                i_Option_LanguageID = p_dataStream.readByte();
                lg_Option_Light = p_dataStream.readBoolean();
                lg_Option_Vibration = p_dataStream.readBoolean();
                lg_Option_Sound = p_dataStream.readBoolean();
                lg_Option_Autostatistics = p_dataStream.readBoolean();
                p_dataStream.close();
                p_dataStream = null;
                p_inStr = null;
                ab_data = null;
            }
            catch (Exception _ex)
            {
                i_Option_LanguageID = -1;
                lg_Option_Light = true;
                lg_Option_Vibration = true;
                lg_Option_Sound = true;
                lg_Option_Autostatistics = false;
                p_rmsBlock.createNewRecord(RMS_OPTIONS_RECORD_NAME, 32);
                saveOptionsToRMS();
            }
        }
    }

    private void saveOptionsToRMS() throws RecordStoreException, IOException
    {
        rmsFS.FSRecord p_record = p_rmsBlock.getRecordForName(RMS_OPTIONS_RECORD_NAME);
        if (p_record != null)
        {
            ByteArrayOutputStream p_inStr = new ByteArrayOutputStream(64);
            DataOutputStream p_dataStream = new DataOutputStream(p_inStr);
            p_dataStream.writeByte(i_Option_LanguageID);
            p_dataStream.writeBoolean(lg_Option_Light);
            p_dataStream.writeBoolean(lg_Option_Vibration);
            p_dataStream.writeBoolean(lg_Option_Sound);
            p_dataStream.writeBoolean(lg_Option_Autostatistics);
            p_dataStream.close();
            p_dataStream = null;
            byte[] ab_data = p_inStr.toByteArray();
            p_inStr = null;
            p_record.setData(ab_data, false);
        }
    }

    protected void startApp() throws MIDletStateChangeException
    {
        if (i_CurrentMidletState == STATE_INITING)
        {
            Image p_clockImage = null;

            try
            {
                p_clockImage = Image.createImage(CLOCK_IMAGE);
            }
            catch (IOException e)
            {
            }

            p_CurrentDisplay = Display.getDisplay(this);
            i_BackgroundColor = p_CurrentDisplay.getColor(Display.COLOR_BACKGROUND);
            i_ForegroundColor = p_CurrentDisplay.getColor(Display.COLOR_FOREGROUND);
            p_MainCanvas = new InsideCanvas(i_BackgroundColor, i_ForegroundColor, p_clockImage);
            p_CurrentDisplay.setCurrent(p_MainCanvas);

            p_languageBlock = new LanguageBlock();
            p_menuBlock = new MenuBlock();

            try
            {
                p_loadingLogo = Image.createImage(LOADING_LOGO);
            }
            catch (IOException e)
            {
                p_loadingLogo = null;
            }

            new Thread(this).start();
        }
        else
        {
            i_CurrentMidletState = STATE_WORKING;
        }
    }

    protected void pauseApp()
    {
        i_CurrentMidletState = STATE_PAUSED;
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
                synchronized (p_model)
                {
                    try
                    {
                        p_model.releaseApplication(p_rmsBlock);
                        saveOptionsToRMS();
                        p_rmsBlock.close(true);
                    }
                    catch (Exception e)
                    {
                    }
                }
            }
        }
    }

    private void viewAlert(String _caption, String _text, AlertType _type, int _delay)
    {
        Alert p_alert = new Alert(_caption, _text, null, _type);
        p_alert.setTimeout(_delay);

        p_CurrentDisplay.setCurrent(p_alert);

        if (_delay != Alert.FOREVER)
        {
            try
            {
                Thread.sleep(_delay);
            }
            catch (InterruptedException e)
            {
            }
        }
    }

    private static final int COMMAND_NONE = 0;
    private static final int COMMAND_COMMAND = 1;
    private static final int COMMAND_LISTITEM = 2;

    private Displayable p_ArgScreen;
    private int i_ArgScreenID;
    private int i_ArgSelectedID;
    private int i_ArgCommandID;
    private int i_ProcessCommand;

    private synchronized void _showWait()
    {
        p_CurrentDisplay.setCurrent(p_MainCanvas);
        while (!p_MainCanvas.isShown())
        {
            try
            {
                Thread.sleep(30);
            }
            catch (InterruptedException e)
            {
                break;
            }
        }
    }

    private synchronized void _hiddeWait()
    {
        Displayable p_dsp = p_menuBlock.MB_p_Form;
        p_CurrentDisplay.setCurrent(p_dsp);
        while (!p_dsp.isShown())
        {
            try
            {
                Thread.sleep(30);
            }
            catch (InterruptedException e)
            {
                break;
            }
        }
    }

    private void _processCommand(Displayable _screen, int _screenId, int _commandId, int _selectedId)
    {
        switch (_commandId)
        {
            case COMMAND_DeleteCMD:
                {
                    i_selectedItem = _selectedId;
                    p_menuBlock.MB_initScreen(SCR_ConfirmationSCR, true);
                }
                ;
                break;
            case COMMAND_StatisticsCMD:
                {
                    switch (_screenId)
                    {
                        case SCR_ProjListSCR:
                            p_menuBlock.MB_initScreen(SCR_SummStatSCR, true);
                            break;
                        case SCR_ProjFormSCR:
                            p_menuBlock.MB_initScreen(SCR_ProjStatSCR, true);
                            break;
                        case SCR_ResourceFormSCR:
                            p_menuBlock.MB_initScreen(SCR_ResourceStatSCR, true);
                            break;
                    }
                }
                ;
                break;
            case COMMAND_ResourcesCMD:
                {
                    p_menuBlock.MB_initScreen(SCR_ResourcesListSCR, true);
                }
                ;
                break;
            case COMMAND_ProjectCMD:
                {
                    p_menuBlock.MB_initScreen(SCR_ProjFormSCR, true);
                }
                ;
                break;
            case COMMAND_EnterCMD:
                {
                    switch (_screenId)
                    {
                        case SCR_ProjFormSCR:
                            {
                                fillProjectFromForm((Form) _screen, p_model.getActiveProject());
                                p_menuBlock.MB_back(true);
                            }
                            ;
                            break;
                        case SCR_TaskFormSCR:
                            {
                                fillTaskFromForm((Form) _screen, p_model.getActiveProject().getActiveTask());
                                p_model.getActiveProject().closeActiveTask();
                                p_menuBlock.MB_back(true);
                            }
                            ;
                            break;
                        case SCR_ResourceFormSCR:
                            {
                                fillResourceFromForm((Form) _screen, p_model.getResourcesContainer().getActiveResource());
                                p_model.getResourcesContainer().closeActiveResource();
                                p_menuBlock.MB_back(true);
                            }
                            ;
                            break;
                    }
                }
                ;
                break;
            case COMMAND_AboutCMD:
                {
                    p_menuBlock.MB_initScreen(SCR_AboutSCR, true);
                }
                ;
                break;
            case COMMAND_OptionsCMD:
                {
                    p_menuBlock.MB_initScreen(SCR_OptionsSCR, true);
                }
                ;
                break;
            case COMMAND_HelpCMD:
                {
                    switch (_screenId)
                    {
                        case SCR_ProjListSCR:
                            {
                                p_menuBlock.MB_initScreen(SCR_HelpProjListSCR, true);
                            }
                            ;
                            break;
                        case SCR_ResourcesListSCR:
                            {
                                p_menuBlock.MB_initScreen(SCR_HelpResourcesListSCR, true);
                            }
                            ;
                            break;
                        case SCR_TasksListSCR:
                            {
                                p_menuBlock.MB_initScreen(SCR_HelpTaskSCR, true);
                            }
                            ;
                            break;
                    }
                }
                ;
                break;
            case COMMAND_ExitCMD:
                {
                    i_CurrentMidletState = STATE_RELEASING;
                }
                ;
                break;
            case COMMAND_YesCMD:
                {
                    switch (p_menuBlock.MB_getPreviousScreenID())
                    {
                        case SCR_ProjListSCR:
                            {
                                try
                                {
                                    p_model.removeProject(i_selectedItem, p_rmsBlock);
                                    p_menuBlock.MB_back(true);
                                }
                                catch (Exception e)
                                {
                                    p_menuBlock.MB_back(false);
                                    p_menuBlock.MB_viewAlert(getStringForIndex(ErrorTXT), getStringForIndex(CantDeleteProjectTXT), AlertType.ERROR, 3000, false);
                                }
                            }
                            ;
                            break;
                        case SCR_ResourcesListSCR:
                            {
                                try
                                {
                                    p_model.getResourcesContainer().removeResource(i_selectedItem);
                                    p_menuBlock.MB_back(true);
                                }
                                catch (Exception e)
                                {
                                    p_menuBlock.MB_back(false);
                                    p_menuBlock.MB_viewAlert(getStringForIndex(ErrorTXT), getStringForIndex(CantDeleteResourceTXT), AlertType.ERROR, 3000, false);
                                }
                            }
                            ;
                            break;
                        case SCR_TasksListSCR:
                            {
                                try
                                {
                                    p_model.getActiveProject().removeTask(i_selectedItem);
                                    p_menuBlock.MB_back(true);
                                }
                                catch (Exception e)
                                {
                                    p_menuBlock.MB_back(false);
                                    p_menuBlock.MB_viewAlert(getStringForIndex(ErrorTXT), getStringForIndex(CantDeleteTaskTXT), AlertType.ERROR, 3000, false);
                                }
                            }
                            ;
                            break;
                    }
                }
                ;
                break;
            case COMMAND_OpenCMD:
                {
                    _processListItem(_screenId, _selectedId);
                }
                ;
                break;
            case COMMAND_CreateCMD:
                {
                    switch (_screenId)
                    {
                        case SCR_TasksListSCR:
                            {
                                Project p_proj = p_model.getActiveProject();
                                synchronized (p_proj)
                                {
                                    long l_uid = p_proj.addNewTask(getStringForIndex(TaskTXT));

                                    if (p_proj.setActiveTask(l_uid))
                                    {
                                        p_menuBlock.MB_initScreen(SCR_TaskFormSCR, true);
                                    }
                                    else
                                    {
                                        p_menuBlock.MB_replaceCurrentScreen(SCR_TasksListSCR, true);
                                    }
                                }
                            }
                            ;
                            break;
                        case SCR_ResourcesListSCR:
                            {
                                ResourcesContainer p_rsrcc = p_model.getResourcesContainer();
                                synchronized (p_rsrcc)
                                {
                                    long l_uid = p_rsrcc.createNewResource(getStringForIndex(ResourceTXT));
                                    if (p_rsrcc.setActiveResource(l_uid))
                                    {
                                        p_menuBlock.MB_initScreen(SCR_ResourceFormSCR, true);
                                    }
                                    else
                                        p_menuBlock.MB_replaceCurrentScreen(SCR_ResourcesListSCR, true);
                                }

                            }
                            ;
                            break;
                        case SCR_ProjListSCR:
                            {
                                Project p_newProj = new Project(p_model, getStringForIndex(ProjectTXT));
                                try
                                {
                                    p_model.addNewProject(p_newProj, p_rmsBlock);
                                    p_menuBlock.MB_initScreen(SCR_ProjFormSCR, true);
                                }
                                catch (Exception e)
                                {
                                    p_menuBlock.MB_reinitScreen(true, false);
                                    p_menuBlock.MB_viewAlert(getStringForIndex(ErrorTXT), getStringForIndex(CantCreateProjectTXT), AlertType.ERROR, 3000, true);
                                }
                            }
                            ;
                            break;
                    }
                }
                ;
                break;
        }

    }

    private void _processListItem(int _screenId, int _itemId)
    {
        switch (_screenId)
        {
            case SCR_TasksListSCR:
                {
                    p_model.getActiveProject().setActiveTask(_itemId);
                    p_menuBlock.MB_initScreen(SCR_TaskFormSCR, true);
                }
                ;
                break;
            case SCR_ProjListSCR:
                {
                    try
                    {
                        p_model.openProject(_itemId);
                        p_menuBlock.MB_initScreen(SCR_TasksListSCR, true);
                    }
                    catch (Exception e)
                    {
                        p_menuBlock.MB_reinitScreen(true, false);
                        p_menuBlock.MB_viewAlert(getStringForIndex(ErrorTXT), getStringForIndex(CantOpenProjectTXT), AlertType.ERROR, Alert.FOREVER, false);
                    }
                }
                ;
                break;
            case SCR_ResourcesListSCR:
                {
                    p_model.getResourcesContainer().setActiveResource(_itemId);
                    p_menuBlock.MB_initScreen(SCR_ResourceFormSCR, true);
                }
                ;
                break;
            case SCR_LanguageSelectSCR:
                {
                    try
                    {
                        _showWait();
                        i_Option_LanguageID = _itemId;
                        p_languageBlock.LB_setLanguage(i_Option_LanguageID);
                        p_menuBlock.MB_reinitScreen(true, true);
                        _hiddeWait();
                    }
                    catch (Exception e)
                    {
                        _hiddeWait();
                        p_menuBlock.MB_viewAlert(getStringForIndex(ErrorTXT), e.getMessage(), AlertType.ERROR, Alert.FOREVER, false);
                    }
                }
                ;
                break;
        }
    }

    public void run()
    {
        i_loadingProgress = 0;
        i_CurrentMidletState = STATE_LOADING;
        p_MainCanvas.repaint();

        p_model = new ProjectsContainer();

        try
        {
            try
            {
                p_rmsBlock = new rmsFS(p_model.getAppID());
                loadOptionsFromRMS();
            }
            catch (RecordStoreException e)
            {
                throw new IOException("Fatal rms error");
            }
            i_loadingProgress += 25;
            p_MainCanvas.repaint();

            p_languageBlock.LB_initLanguageBlock(RESOURCE_LANGUAGELIST, i_Option_LanguageID);
            i_loadingProgress += 25;
            p_MainCanvas.repaint();

            p_menuBlock.MB_initMenuBlock(RESOURCE_MENU);
            i_loadingProgress += 25;
            p_MainCanvas.repaint();

            p_model.initApplication(p_rmsBlock, this);

            p_aboutIcon = Image.createImage(RESOURCE_ABOUT_ICON);

            Image p_icn = Image.createImage(RESOURCE_ICONS);
            ap_icons = parseImage(p_icn, 9);
            p_icn = null;

            try
            {
                p_splashLogo = Image.createImage(SPLASH_LOGO);
            }
            catch (IOException e)
            {
                p_splashLogo = null;
            }
            i_loadingProgress += 25;
            p_MainCanvas.repaint();

            Thread.sleep(300);
        }
        catch (Exception _ex)
        {
            viewAlert("Loading error..", _ex.getMessage(), AlertType.ERROR, Alert.FOREVER);
            this.notifyDestroyed();
            return;
        }

        i_CurrentMidletState = STATE_SPLASH;
        p_MainCanvas.repaint();

        synchronized (p_synchroObject)
        {
            try
            {
                p_synchroObject.wait(3000);
            }
            catch (InterruptedException e)
            {
                return;
            }
        }

        p_loadingLogo = null;
        i_CurrentMidletState = STATE_WORKING;
        p_MainCanvas.repaint();

        p_splashLogo = null;

        p_menuBlock.MB_activateMenu(p_MainCanvas, SCR_ProjListSCR, this);

        if (lg_Option_Autostatistics && p_model.getProjectsNumber()>0)
        {
            p_menuBlock.MB_initScreen(SCR_SummStatSCR, true);
        }

        while (i_CurrentMidletState == STATE_WORKING || i_CurrentMidletState == STATE_PAUSED)
        {
            try
            {
                Thread.sleep(20);
            }
            catch (InterruptedException e)
            {
                break;
            }

            if (lg_Option_Light)
                com.siemens.mp.game.Light.setLightOn();
            else
                com.siemens.mp.game.Light.setLightOff();

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
        }

        p_CurrentDisplay.setCurrent(p_MainCanvas);
        p_MainCanvas.repaint();

        try
        {
            p_model.releaseApplication(p_rmsBlock);
            saveOptionsToRMS();
            p_rmsBlock.close(true);
        }
        catch (Exception e)
        {
            viewAlert(getStringForIndex(ErrorTXT), getStringForIndex(CantSaveDataTXT), AlertType.WARNING, Alert.FOREVER);
            this.notifyDestroyed();
        }

        i_CurrentMidletState = STATE_RELEASING;
        this.notifyDestroyed();
    }

    public Object processAction(int _actionNumber, Object _args)
    {
        switch (_actionNumber)
        {
        }
        return null;
    }

    public Display getDisplay()
    {
        return p_CurrentDisplay;
    }

    public Image getImageForIndex(int _index)
    {
        switch (_index)
        {
            case 221:
                return p_aboutIcon;
        }
        return null;
    }

    public String getStringForIndex(int _index)
    {
        return p_languageBlock.LB_as_TextStringArray[_index];
    }


    public void processFormItem(int _screenId, int _index, int _commandID, Item _formItem)
    {
        processCommand(p_menuBlock.MB_p_Form, _screenId, _commandID, _index);
    }

    private int i_selectedItem = -1;

    public void processListItem(int _screenId, int _itemId)
    {
        if (i_ProcessCommand != COMMAND_NONE) return;
        p_ArgScreen = null;
        i_ArgScreenID = _screenId;
        i_ArgCommandID = -1;
        i_ArgSelectedID = _itemId;
        i_ProcessCommand = COMMAND_LISTITEM;
    }

    public synchronized void processCommand(Displayable _screen, int _screenId, int _commandId, int _selectedId)
    {
        if (i_ProcessCommand != COMMAND_NONE) return;
        p_ArgScreen = _screen;
        i_ArgScreenID = _screenId;
        i_ArgCommandID = _commandId;
        i_ArgSelectedID = _selectedId;
        i_ProcessCommand = COMMAND_COMMAND;
    }

    public boolean enableCommand(int _screenId, int _commandId)
    {
        switch (_screenId)
        {
            case SCR_ProjListSCR:
                {
                    if (p_model.getProjectsNumber() > 0)
                        return true;
                    else
                        return false;
                }
            case SCR_TasksListSCR:
                {
                    if (p_model.getActiveProject().getWorksNumber() > 0)
                        return true;
                    else
                        return false;
                }
            case SCR_ResourcesListSCR:
                {
                    if (p_model.getResourcesContainer().getResourcesNumber() > 0)
                        return true;
                    else
                        return false;
                }
        }
        return true;
    }

    private Form generateProjectPropForm(Project _project)
    {
        Form p_form = new Form(getStringForIndex(ProjectTXT) + " \"" + _project.getName() + "\"");
        synchronized (_project)
        {
            TextField p_titleField = new TextField(getStringForIndex(NameTXT), _project.getName(), 12, TextField.ANY);
            DateField p_startDate = new DateField(getStringForIndex(StartDateTXT), DateField.DATE);
            p_startDate.setDate(_project.getStartDate() >= 0 ? new Date(_project.getStartDate()) : null);

            DateField p_endDate = new DateField(getStringForIndex(EndDateTXT), DateField.DATE);
            p_endDate.setDate(_project.getEndDate() >= 0 ? new Date(_project.getEndDate()) : null);

            ChoiceGroup p_priority = new ChoiceGroup(getStringForIndex(PriorityTXT), ChoiceGroup.POPUP);

            p_priority.append(getStringForIndex(LowTXT), null);
            p_priority.append(getStringForIndex(NormalTXT), null);
            p_priority.append(getStringForIndex(HighTXT), null);
            p_priority.setSelectedIndex(_project.getPriority(), true);

            ChoiceGroup p_resPerson = new ChoiceGroup(getStringForIndex(ResPersonTXT), ChoiceGroup.POPUP);
            ResourcesContainer p_resContainer = p_model.getResourcesContainer();
            int i_index = 0;
            p_resPerson.append("   ", null);
            synchronized (p_resContainer)
            {
                long l_projPers = _project.getResponsiblePerson();
                for (int li = 0; li < p_resContainer.getResourcesNumber(); li++)
                {
                    Resource p_res = p_resContainer.getResourceForIndex(li);
                    p_resPerson.append(p_res.getName(), null);
                    if (l_projPers == p_res.getUID())
                    {
                        i_index = li + 1;
                    }
                }

                p_resPerson.setSelectedIndex(i_index, true);
            }

            TextField p_comments = new TextField(getStringForIndex(CommentsTXT), _project.getComments(), 64, TextField.ANY);


            p_form.append(p_titleField);
            p_form.append(p_startDate);
            p_form.append(p_endDate);
            p_form.append(p_priority);
            p_form.append(p_resPerson);
            p_form.append(p_comments);
        }
        return p_form;
    }

    private void fillResourceFromForm(Form _form, Resource _resource)
    {
        synchronized (_resource)
        {
            TextField p_title = (TextField) _form.get(0);
            TextField p_phone = (TextField) _form.get(1);
            TextField p_email = (TextField) _form.get(2);
            TextField p_comments = (TextField) _form.get(3);

            _resource.setName(p_title.getString());
            _resource.setPhoneNumber(p_phone.getString());
            _resource.setEmail(p_email.getString());
            _resource.setComments(p_comments.getString());
        }
    }

    private ChoiceGroup generateResourceChoiceGroup(long _selected)
    {
        ChoiceGroup p_resPerson = new ChoiceGroup(getStringForIndex(ResPersonTXT), ChoiceGroup.POPUP);
        ResourcesContainer p_resContainer = p_model.getResourcesContainer();
        int i_index = 0;
        p_resPerson.append("   ", null);
        synchronized (p_resContainer)
        {
            for (int li = 0; li < p_resContainer.getResourcesNumber(); li++)
            {
                Resource p_res = p_resContainer.getResourceForIndex(li);
                p_resPerson.append(p_res.getName(), null);
                if (_selected == p_res.getUID())
                {
                    i_index = li + 1;
                }
            }

            p_resPerson.setSelectedIndex(i_index, true);
        }
        return p_resPerson;
    }

    public long getResourceIDFromChoiceGroup(ChoiceGroup _resources)
    {
        int i_personSelIndex = _resources.getSelectedIndex();
        if (i_personSelIndex == 0)
        {
            return -1;
        }
        else
        {
            Resource p_r = p_model.getResourcesContainer().getResourceForIndex(i_personSelIndex - 1);
            if (p_r == null) return -1;
            return p_r.getUID();
        }
    }

    private long getSelectedTaskIndex(ChoiceGroup _group, long _excludedTask)
    {
        synchronized (p_model.getActiveProject())
        {
            int i_selected = _group.getSelectedIndex();
            if (i_selected == 0) return -1;

            int i_number = p_model.getActiveProject().getWorksNumber();
            int i_itmIndex = 1;
            for (int li = 0; li < i_number; li++)
            {
                Task p_task = p_model.getActiveProject().getWorkForIndex(li);
                long l_curUID = p_task.getUID();
                if (l_curUID == _excludedTask) continue;
                if (i_itmIndex == i_selected)
                {
                    return l_curUID;
                }
                i_itmIndex++;
            }
            return -1;
        }
    }

    private ChoiceGroup generateTasksList(String _label, long _excludeTask, long _selectedTask)
    {
        synchronized (p_model.getActiveProject())
        {
            ChoiceGroup p_group = new ChoiceGroup(_label, ChoiceGroup.POPUP);
            p_group.append("   ", null);
            int i_number = p_model.getActiveProject().getWorksNumber();
            int i_selectedIndex = 0;
            int i_itmIndex = 1;
            for (int li = 0; li < i_number; li++)
            {
                Task p_task = p_model.getActiveProject().getWorkForIndex(li);
                long l_curUID = p_task.getUID();
                if (l_curUID == _excludeTask) continue;
                if (l_curUID == _selectedTask)
                {
                    i_selectedIndex = i_itmIndex;
                }
                p_group.append(p_task.getTitle(), null);
                i_itmIndex++;
            }
            p_group.setSelectedIndex(i_selectedIndex, true);
            return p_group;
        }
    }

    private String convertLongToDayHourString(long _duration)
    {
        if (_duration < 0) return "";

        long l_days = _duration / ProjectsContainer.MILLISECONDS_WORKDAYDAY;
        long l_hours = (_duration - l_days * ProjectsContainer.MILLISECONDS_WORKDAYDAY) / ProjectsContainer.MILLISECONDS_HOUR;
        StringBuffer p_strBuffer = new StringBuffer(32);
        if (l_days > 0)
        {
            p_strBuffer.append(l_days);
            p_strBuffer.append(getStringForIndex(DayShTXT));
        }
        if (l_hours > 0)
        {
            p_strBuffer.append(l_hours);
            p_strBuffer.append(getStringForIndex(HourShTXT));
        }
        return p_strBuffer.toString();
    }

    private Form generateTaskPropForm(Task _task)
    {
        Form p_form = new Form(getStringForIndex(TaskFormTitleTXT));
        synchronized (_task)
        {
            TextField p_title = new TextField(getStringForIndex(TitleTXT), _task.getTitle(), 16, TextField.ANY);

            ChoiceGroup p_resources = generateResourceChoiceGroup(_task.getResource());
            ChoiceGroup p_tasks = generateTasksList(getStringForIndex(MainTaskTXT), _task.getUID(), _task.getParentTask());
            DateField p_startDate = new DateField(getStringForIndex(StartDateTXT), DateField.DATE);
            p_startDate.setDate(_task.getStartDate() >= 0 ? new Date(_task.getStartDate()) : null);

            DateField p_endDate = new DateField(getStringForIndex(EndDateTXT), DateField.DATE);
            p_endDate.setDate(_task.getDeadline() >= 0 ? new Date(_task.getDeadline()) : null);

            TextField p_duration = new TextField(getStringForIndex(DurationTXT), convertLongToDayHourString(_task.getDuration()), 16, TextField.ANY);
            TextField p_progress = new TextField(getStringForIndex(ProgressTXT), Integer.toString(_task.getProgress()), 3, TextField.NUMERIC);
            TextField p_comments = new TextField(getStringForIndex(CommentsTXT), _task.getComments(), 64, TextField.ANY);

            p_form.append(p_title);
            p_form.append(p_progress);
            p_form.append(p_startDate);
            p_form.append(p_duration);
            p_form.append(p_endDate);
            p_form.append(p_resources);
            p_form.append(p_tasks);
            p_form.append(p_comments);
        }
        return p_form;
    }

    private long getDateTime(DateField _field)
    {
        Date p_date = _field.getDate();
        if (p_date == null) return -1;
        long l_val = p_date.getTime();
        if (l_val <= 0) return -1;
        return l_val;
    }

    private long durationFromString(String _duration)
    {
        _duration = _duration.trim().toLowerCase();

        final int ST_HOURS = 0;
        final int ST_DAYS = 1;
        final int ST_UNKNOWN = 2;
        int i_curState = ST_DAYS;

        char ch_hR = 'ч';
        char ch_hE = 'h';
        char ch_dR = 'д';
        char ch_dE = 'd';

        long l_acc = 0;
        long l_cur = 0;
        long l_mulCoeff = 1;

        _duration = ch_dE + _duration;

        for (int li = _duration.length() - 1; li >= 0; li--)
        {
            char ch_cur = _duration.charAt(li);
            if (ch_cur == ch_hR || ch_cur == ch_hE)
            {
                switch (i_curState)
                {
                    case ST_DAYS:
                        {
                            l_acc += l_cur * ProjectsContainer.MILLISECONDS_WORKDAYDAY;
                            l_cur = 0;
                            l_mulCoeff = 1;
                        }
                        ;
                        break;
                    case ST_HOURS:
                        {
                            l_acc += l_cur * ProjectsContainer.MILLISECONDS_HOUR;
                            l_cur = 0;
                            l_mulCoeff = 1;
                        }
                        ;
                        break;
                }
                i_curState = ST_HOURS;
            }
            else if (ch_cur == ch_dE || ch_cur == ch_dR)
            {
                switch (i_curState)
                {
                    case ST_DAYS:
                        {
                            l_acc += l_cur * ProjectsContainer.MILLISECONDS_WORKDAYDAY;
                            l_cur = 0;
                            l_mulCoeff = 1;
                        }
                        ;
                        break;
                    case ST_HOURS:
                        {
                            l_acc += l_cur * ProjectsContainer.MILLISECONDS_HOUR;
                            l_cur = 0;
                            l_mulCoeff = 1;
                        }
                        ;
                        break;
                }
                i_curState = ST_DAYS;
            }
            else
            {
                int i_n = -1;
                switch (ch_cur)
                {
                    case ' ':
                        continue;
                    case '0':
                        i_n = 0;
                        break;
                    case '1':
                        i_n = 1;
                        break;
                    case '2':
                        i_n = 2;
                        break;
                    case '3':
                        i_n = 3;
                        break;
                    case '4':
                        i_n = 4;
                        break;
                    case '5':
                        i_n = 5;
                        break;
                    case '6':
                        i_n = 6;
                        break;
                    case '7':
                        i_n = 7;
                        break;
                    case '8':
                        i_n = 8;
                        break;
                    case '9':
                        i_n = 9;
                        break;
                    default:
                        {
                            switch (i_curState)
                            {
                                case ST_DAYS:
                                    {
                                        l_acc += l_cur * ProjectsContainer.MILLISECONDS_WORKDAYDAY;
                                        l_cur = 0;
                                        l_mulCoeff = 1;
                                    }
                                    ;
                                    break;
                                case ST_HOURS:
                                    {
                                        l_acc += l_cur * ProjectsContainer.MILLISECONDS_HOUR;
                                        l_cur = 0;
                                        l_mulCoeff = 1;
                                    }
                                    ;
                                    break;
                            }
                            i_curState = ST_UNKNOWN;
                            l_cur = 0;
                            l_mulCoeff = 1;
                            i_n = 0;
                        }
                }
                if (i_n >= 0)
                {
                    l_cur += i_n * l_mulCoeff;
                }

                l_mulCoeff *= 10;
            }
        }
        return l_acc;
    }

    private void fillTaskFromForm(Form _form, Task _task)
    {
        synchronized (_task)
        {
            TextField p_title = (TextField) _form.get(0);
            TextField p_progress = (TextField) _form.get(1);
            DateField p_startDate = (DateField) _form.get(2);
            TextField p_duration = (TextField) _form.get(3);
            DateField p_endDate = (DateField) _form.get(4);
            ChoiceGroup p_resources = (ChoiceGroup) _form.get(5);
            ChoiceGroup p_tasks = (ChoiceGroup) _form.get(6);
            TextField p_comments = (TextField) _form.get(7);

            _task.setTitle(p_title.getString());
            _task.setComments(p_comments.getString());
            _task.setResource(getResourceIDFromChoiceGroup(p_resources));
            _task.setParentWork(getSelectedTaskIndex(p_tasks, _task.getUID()));

            int i_progress = 0;

            try
            {
                i_progress = Integer.parseInt(p_progress.getString().trim());

                if (i_progress < 0)
                    i_progress = 0;
                else if (i_progress > 100) i_progress = 100;
            }
            catch (NumberFormatException e)
            {
            }

            _task.setProgress(i_progress);

            long l_startDate = getDateTime(p_startDate);
            long l_endDate = getDateTime(p_endDate);
            if (l_startDate != _task.getStartDate()) _task.setStartDate(l_startDate);

            if (l_endDate != _task.getDeadline()) _task.setDeadline(l_endDate);

            long l_duration = durationFromString(p_duration.getString());
            _task.setDuration(l_duration);
        }
    }


    private Form generateResourcePropForm(Resource _resource)
    {
        Form p_form = new Form(getStringForIndex(ResourceFormTitleTXT));
        synchronized (_resource)
        {
            TextField p_title = new TextField(getStringForIndex(TitleTXT), _resource.getName(), 16, TextField.ANY);
            TextField p_phone = new TextField(getStringForIndex(PhoneTXT), _resource.getPhoneNumber(), 16, TextField.PHONENUMBER);
            TextField p_email = new TextField(getStringForIndex(EmailTXT), _resource.getEmail(), 24, TextField.EMAILADDR);
            TextField p_comments = new TextField(getStringForIndex(CommentsTXT), _resource.getComments(), 64, TextField.ANY);

            p_form.append(p_title);
            p_form.append(p_phone);
            p_form.append(p_email);
            p_form.append(p_comments);
        }
        return p_form;
    }


    private void fillProjectFromForm(Form _form, Project _project)
    {
        TextField p_titleField = (TextField) _form.get(0);
        DateField p_startDate = (DateField) _form.get(1);
        DateField p_endDate = (DateField) _form.get(2);
        ChoiceGroup p_priority = (ChoiceGroup) _form.get(3);
        ChoiceGroup p_resPreson = (ChoiceGroup) _form.get(4);
        TextField p_comments = (TextField) _form.get(5);

        _project.setName(p_titleField.getString());
        Date p_date = p_startDate.getDate();
        _project.setStartDate(p_date == null ? -1 : p_date.getTime());

        p_date = p_endDate.getDate();
        _project.setEndDate(p_date == null ? -1 : p_date.getTime());

        _project.setPriority(p_priority.getSelectedIndex());
        int i_personSelIndex = p_resPreson.getSelectedIndex();
        if (i_personSelIndex == 0)
        {
            _project.setResponsiblePerson(-1);
        }
        else
        {
            Resource p_r = p_model.getResourcesContainer().getResourceForIndex(i_personSelIndex - 1);
            _project.setResponsiblePerson(p_r.getUID());
        }

        _project.setComments(p_comments.getString());
    }

    private Form generateStatisticsForCurrentResource()
    {
        Resource p_actRes = p_model.getResourcesContainer().getActiveResource();
        Form p_form = new Form(getStringForIndex(ResourceStatTitleTXT) + " \"" + p_actRes.getName() + "\"");

        StringItem p_tasksNumber = new StringItem(getStringForIndex(NumberOfUsingTaskTXT), Integer.toString(p_actRes.getNumberOfUsing()));
        p_form.append(p_tasksNumber);

        return p_form;
    }

    private Form generateSummaryStatistics()
    {
        if (p_model.getActiveProject() != null) return null;
        long l_current = System.currentTimeMillis();

        synchronized (p_model)
        {
            _showWait();
            Form p_form = new Form(getStringForIndex(SummStatTitleTXT));
            StringBuffer p_overdueTasks = new StringBuffer(512);
            StringBuffer p_overdueProjects = new StringBuffer(512);
            StringBuffer p_todayTasks = new StringBuffer(512);

            for (int li = 0; li < p_model.getProjectsNumber(); li++)
            {
                try
                {
                    p_model.openProject(li);
                }
                catch (Exception e)
                {
                    p_menuBlock.MB_viewAlert(getStringForIndex(ErrorTXT), getStringForIndex(CantOpenProjectTXT) + "[" + li + "]", AlertType.ERROR, 4000, true);
                    return null;
                }

                Project p_act = p_model.getActiveProject();

                String s_projectName = p_act.getName();
                if (p_act.getEndDate() > 0)
                {
                    long l_endDate = p_act.getEndDate();
                    int i_progress = p_act.getProgress();
                    if (i_progress < 100 && l_endDate < l_current)
                    {
                        p_overdueProjects.append(s_projectName);
                        p_overdueProjects.append("\r\n");
                    }
                }

                for (int lt = 0; lt < p_act.getWorksNumber(); lt++)
                {
                    Task p_task = p_act.getWorkForIndex(lt);
                    long l_deadline = p_task.getDeadline();
                    int i_progress = p_task.getProgress();


                    if (l_deadline > 0 && i_progress < 100)
                    {
                        if (Utils.isToday(l_deadline))
                        {
                            String s_str = s_projectName + ":" + p_task.getTitle();
                            p_todayTasks.append(s_str);
                            p_todayTasks.append("\r\n");
                        }
                        else if (l_deadline < l_current)
                        {
                            String s_str = s_projectName + ":" + p_task.getTitle();
                            p_overdueTasks.append(s_str);
                            p_overdueTasks.append("\r\n");
                        }
                    }
                }

                try
                {
                    p_model.closeActiveProject(p_rmsBlock, false);
                }
                catch (Exception e)
                {
                }
            }

            if (p_overdueProjects.length() == 0)
            {
                p_overdueProjects.append(getStringForIndex(EmptyTXT));
            }
            if (p_overdueTasks.length() == 0)
            {
                p_overdueTasks.append(getStringForIndex(EmptyTXT));
            }
            if (p_todayTasks.length() == 0)
            {
                p_todayTasks.append(getStringForIndex(EmptyTXT));
            }

            StringItem p_overdueProjectsItm = new StringItem(getStringForIndex(OverdueProjectsTXT), p_overdueProjects.toString());
            StringItem p_overdueTasksItm = new StringItem(getStringForIndex(OverdueTasksTXT), p_overdueTasks.toString());
            StringItem p_todayTasksItm = new StringItem(getStringForIndex(TodayTasksTXT), p_todayTasks.toString());

            p_form.append(p_todayTasksItm);
            p_form.append(p_overdueProjectsItm);
            p_form.append(p_overdueTasksItm);

            _hiddeWait();
            return p_form;
        }
    }

    private Form generateStatisticsForCurrentProject()
    {
        Project p_actProj = p_model.getActiveProject();
        Form p_form = new Form(getStringForIndex(ProjStatTitleTXT) + " \"" + p_actProj.getName() + "\"");

        p_actProj.calculateRateOfDelivery();
        int i_progress = p_actProj.getProgress();

        StringItem p_proress = new StringItem(getStringForIndex(ProgressTXT), Integer.toString(i_progress));
        p_form.append(p_proress);

        return p_form;
    }

    private static final int ICON_SIGNAL_RED = 0;
    private static final int ICON_SIGNAL_YELLOW = 1;
    private static final int ICON_SIGNAL_GREEN = 2;
    private static final int ICON_COMPLETED = 3;
    private static final int ICON_PERSON = 4;
    private static final int ICON_TODAY = 5;
    private static final int ICON_WORKTASK = 6;
    private static final int ICON_WARNING = 7;
    private static final int ICON_LINKED = 8;

    public Displayable customScreen(int _screenId)
    {
        Displayable p_result = null;
        switch (_screenId)
        {
            case SCR_SummStatSCR:
                {
                    p_result = generateSummaryStatistics();
                }
                ;
                break;
            case SCR_ResourceStatSCR:
                {
                    p_result = generateStatisticsForCurrentResource();
                }
                ;
                break;
            case SCR_ProjStatSCR:
                {
                    p_result = generateStatisticsForCurrentProject();
                }
                ;
                break;
            case SCR_ResourceFormSCR:
                {
                    p_result = generateResourcePropForm(p_model.getResourcesContainer().getActiveResource());
                }
                ;
                break;
            case SCR_TaskFormSCR:
                {
                    p_result = generateTaskPropForm(p_model.getActiveProject().getActiveTask());
                }
                ;
                break;
            case SCR_ResourcesListSCR:
                {
                    ResourcesContainer p_cntr = p_model.getResourcesContainer();
                    p_cntr.closeActiveResource();
                    synchronized (p_cntr)
                    {
                        List p_list = new List(getStringForIndex(ResourcesListTitleTXT), List.IMPLICIT);
                        int i_rsrcNumber = p_model.getResourcesContainer().getResourcesNumber();
                        for (int li = 0; li < i_rsrcNumber; li++)
                        {
                            Resource p_rsrc = p_cntr.getResourceForIndex(li);
                            p_list.append(p_rsrc.getName(), ap_icons[4]);
                        }
                        p_result = p_list;
                    }
                }
                ;
                break;
            case SCR_OnOffSCR:
                {
                    String s_caption = null;
                    int i_selIndex = 1;

                    switch (p_menuBlock.MB_getLastItemSelected())
                    {
                        case ITEM_BackLightITM:
                            s_caption = getStringForIndex(BackLightTXT);
                            i_selIndex = lg_Option_Light ? 0 : 1;
                            break;
                        case ITEM_AvtoStatisticsITM:
                            s_caption = getStringForIndex(AvtoStatisticsTXT);
                            i_selIndex = lg_Option_Autostatistics ? 0 : 1;
                            break;
                    }
                    List p_lst = new List(s_caption, List.EXCLUSIVE);
                    p_lst.append(getStringForIndex(OnTXT), null);
                    p_lst.append(getStringForIndex(OffTXT), null);
                    p_lst.setSelectedIndex(i_selIndex, true);
                    return p_lst;
                }
            case SCR_LanguageSelectSCR:
                {
                    List p_list = new List(getStringForIndex(LanguageSelectTitleTXT), List.IMPLICIT);

                    for (int li = 0; li < p_languageBlock.LB_as_LanguageNames.length; li++)
                    {
                        p_list.append(p_languageBlock.LB_as_LanguageNames[li], null);
                    }
                    return p_list;
                }
            case SCR_ProjFormSCR:
                {
                    p_result = generateProjectPropForm(p_model.getActiveProject());
                }
                ;
                break;
            case SCR_TasksListSCR:
                {
                    p_model.getResourcesContainer().closeActiveResource();

                    Project p_proj = p_model.getActiveProject();
                    synchronized (p_proj)
                    {
                        Form p_form = new Form(getStringForIndex(TasksListTitleTXT) + " \"" + p_model.getActiveProject().getName() + "\"");

                        for (int li = 0; li < p_proj.getWorksNumber(); li++)
                        {
                            Task p_task = p_proj.getWorkForIndex(li);
                            ProjectTaskItem p_projTaskItem = new ProjectTaskItem(p_task, i_ForegroundColor, ap_icons);
                            p_form.append(p_projTaskItem);
                        }

                        p_result = p_form;
                    }
                }
                ;
                break;
            case SCR_ProjListSCR:
                {
                    synchronized (p_model)
                    {
                        try
                        {
                            p_model.closeActiveProject(p_rmsBlock, true);
                        }
                        catch (Exception e)
                        {
                        }

                        Form p_form = new Form(getStringForIndex(ProjListTitleTXT));
                        for (int li = 0; li < p_model.getProjectsNumber(); li++)
                        {
                            ProjectRecord p_proj = (ProjectRecord) p_model.getProjectRecordForIndex(li);
                            ProjectTaskItem p_itm = new ProjectTaskItem(p_proj, i_ForegroundColor, ap_icons);
                            p_form.append(p_itm);
                        }

                        p_result = p_form;
                    }
                }
                ;
                break;
        }

        return p_result;
    }

    public Object customItem(int _screenId, int _itemId, boolean _getImage)
    {
        return null;
    }

    public boolean enableItem(int _screenId, int _itemId)
    {
        switch (_itemId)
        {
            case ITEM_BackLightITM:
                return true;
            case ITEM_LanguageSelectITM:
                return p_languageBlock.LB_as_LanguageNames.length > 1;
        }
        return false;
    }

    public void onExitScreen(Displayable _screen, int _screenId)
    {
        switch (_screenId)
        {
            case SCR_TasksListSCR:
                {
                    try
                    {
                        p_model.closeActiveProject(p_rmsBlock, true);
                    }
                    catch (Exception e)
                    {
                    }
                }
                ;
                break;
            case SCR_ResourcesListSCR:
                {
                    try
                    {
                        p_model.saveResources(p_rmsBlock);
                    }
                    catch (Exception e)
                    {
                    }
                }
                ;
                break;
            case SCR_OptionsSCR:
                {
                    try
                    {
                        saveOptionsToRMS();
                    }
                    catch (Exception e)
                    {
                    }
                }
                ;
                break;
            case SCR_OnOffSCR:
                {
                    List p_list = (List) _screen;
                    boolean[] alg_flagarray = new boolean[2];
                    p_list.getSelectedFlags(alg_flagarray);
                    switch (p_menuBlock.MB_getLastItemSelected())
                    {
                        case ITEM_BackLightITM:
                            {
                                lg_Option_Light = alg_flagarray[0];
                            }
                            ;
                            break;
                        case ITEM_AvtoStatisticsITM:
                            {
                                lg_Option_Autostatistics = alg_flagarray[0];
                            }
                            ;
                            break;
                    }
                }
                ;
                break;
        }
    }

    private static final int HelpProjListTextTXT = 0;
    private static final int NormalTXT = 1;
    private static final int AboutCmdTXT = 2;
    private static final int LowTXT = 3;
    private static final int OffTXT = 4;
    private static final int CantDeleteProjectTXT = 5;
    private static final int HelpProjListTitleTXT = 6;
    private static final int RMSerrorTXT = 7;
    private static final int TitleTXT = 8;
    private static final int OverdueTasksTXT = 9;
    private static final int HighTXT = 10;
    private static final int ResPersonTXT = 11;
    private static final int HelpCmdTXT = 12;
    private static final int ProgressTXT = 13;
    private static final int ResourceTXT = 14;
    private static final int ChangeIconCmdTXT = 15;
    private static final int HourShTXT = 16;
    private static final int TasksListTitleTXT = 17;
    private static final int PhoneTXT = 18;
    private static final int CantOpenProjectTXT = 19;
    private static final int CantDeleteResourceTXT = 20;
    private static final int DeleteCmdTXT = 21;
    private static final int HelpTaskTitleTXT = 22;
    private static final int ResourcesCmdTXT = 23;
    private static final int AboutTextTXT = 24;
    private static final int YesCmdTXT = 25;
    private static final int IconTXT = 26;
    private static final int ProjectCmdTXT = 27;
    private static final int NameTXT = 28;
    private static final int OptionsCmdTXT = 29;
    private static final int MainTaskTXT = 30;
    private static final int EndDateTXT = 31;
    private static final int HelpTaskTextTXT = 32;
    private static final int CommentsTXT = 33;
    private static final int BackCmdTXT = 34;
    private static final int CantSaveDataTXT = 35;
    private static final int HelpResourcesListTitleTXT = 36;
    private static final int ProjectTXT = 37;
    private static final int OnTXT = 38;
    private static final int TaskTXT = 39;
    private static final int ConfirmationTXT = 40;
    private static final int ProjListTitleTXT = 41;
    private static final int NumberOfUsingTaskTXT = 42;
    private static final int NoCmdTXT = 43;
    private static final int BackLightTXT = 44;
    private static final int LanguageSelectTitleTXT = 45;
    private static final int HelpResourcesListTextTXT = 46;
    private static final int LanguageSelectTXT = 47;
    private static final int EmailTXT = 48;
    private static final int AvtoStatisticsTXT = 49;
    private static final int HelpProjectTextTXT = 50;
    private static final int SummStatTitleTXT = 51;
    private static final int ResourcesListTitleTXT = 52;
    private static final int ConfirmationTitleTXT = 53;
    private static final int DurationTXT = 54;
    private static final int CreateCmdTXT = 55;
    private static final int OverdueProjectsTXT = 56;
    private static final int EnterCmdTXT = 57;
    private static final int EmptyTXT = 58;
    private static final int CantDeleteTaskTXT = 59;
    private static final int TodayTasksTXT = 60;
    private static final int ResourceFormTitleTXT = 61;
    private static final int CantCreateProjectTXT = 62;
    private static final int ErrorTXT = 63;
    private static final int StartDateTXT = 64;
    private static final int ProjFormTitleTXT = 65;
    private static final int HelpProjectTitleTXT = 66;
    private static final int TaskFormTitleTXT = 67;
    private static final int RepeatCmdTXT = 68;
    private static final int ExitCmdTXT = 69;
    private static final int SafeCellListTitleTXT = 70;
    private static final int AllOkTXT = 71;
    private static final int WaitPleaseTXT = 72;
    private static final int OpenCmdTXT = 73;
    private static final int PriorityTXT = 74;
    private static final int ResourceStatTitleTXT = 75;
    private static final int StatisticsCmdTXT = 76;
    private static final int DayShTXT = 77;
    private static final int AboutTitleTXT = 78;
    private static final int ProjStatTitleTXT = 79;
    private static final int OptionsTitleTXT = 80;


    private static final int SCR_OptionsSCR = 0;
    private static final int SCR_AboutSCR = 23;
    private static final int SCR_SummStatSCR = 41;
    private static final int SCR_ProjStatSCR = 51;
    private static final int SCR_HelpResourcesListSCR = 61;
    private static final int SCR_TaskFormSCR = 74;
    private static final int SCR_TasksListSCR = 88;
    private static final int SCR_ProjListSCR = 114;
    private static final int SCR_HelpProjectSCR = 138;
    private static final int SCR_OnOffSCR = 151;
    private static final int SCR_ResourceFormSCR = 159;
    private static final int SCR_HelpTaskSCR = 173;
    private static final int SCR_ResourcesListSCR = 186;
    private static final int SCR_ConfirmationSCR = 208;
    private static final int SCR_LanguageSelectSCR = 223;
    private static final int SCR_ResourceStatSCR = 231;
    private static final int SCR_HelpProjListSCR = 241;
    private static final int SCR_ProjFormSCR = 254;


    private static final int ITEM_LanguageSelectITM = 0;
    private static final int ITEM_AvtoStatisticsITM = 1;
    private static final int ITEM_ConfirmationTextITM = 3;
    private static final int ITEM_AboutImageITM = 8;
    private static final int ITEM_HelpTaskTextITM = 7;
    private static final int ITEM_BackLightITM = 2;
    private static final int ITEM_HelpProjListTextITM = 4;
    private static final int ITEM_AboutTextITM = 9;
    private static final int ITEM_HelpProjectTextITM = 6;
    private static final int ITEM_HelpResourcesListTextITM = 5;


    private static final int COMMAND_EnterCMD = 270;
    private static final int COMMAND_StatisticsCMD = 271;
    private static final int COMMAND_ProjectCMD = 272;
    private static final int COMMAND_DeleteCMD = 273;
    private static final int COMMAND_BackCMD = 274;
    private static final int COMMAND_AboutCMD = 275;
    private static final int COMMAND_OpenCMD = 276;
    private static final int COMMAND_HelpCMD = 277;
    private static final int COMMAND_OptionsCMD = 278;
    private static final int COMMAND_CreateCMD = 279;
    private static final int COMMAND_ResourcesCMD = 280;
    private static final int COMMAND_NoCMD = 281;
    private static final int COMMAND_ExitCMD = 282;
    private static final int COMMAND_YesCMD = 283;
}
