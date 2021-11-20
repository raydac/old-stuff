package PasswordsSafe;

import javax.microedition.midlet.MIDlet;
import javax.microedition.midlet.MIDletStateChangeException;
import javax.microedition.lcdui.*;
import javax.microedition.lcdui.game.Sprite;
import javax.microedition.rms.RecordStoreException;
import java.io.*;

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

    private Object p_synchroObject = new Object();

    private PrivateSafe p_model;
    private LanguageBlock p_languageBlock;
    private MenuBlock p_menuBlock;
    private rmsFS p_rmsBlock;

    private static final String CLOCK_IMAGE = "/res/clock.png";
    private static final String LOADING_LOGO = "/res/loading.png";
    private static final String SPLASH_LOGO = "/res/splash.png";
    private static final String RESOURCE_LANGUAGELIST = "/res/langs.bin";
    private static final String RESOURCE_MENU = "/res/menu.bin";
    private static final String RESOURCE_SAFE_ICONS = "/res/safeicons.png";
    private static final String RESOURCE_ABOUT_ICON = "/res/abouticon.png";
    private static final String RESOURCE_CELL_ICONS = "/res/cellicons.png";
    private static final String RMS_OPTIONS_RECORD_NAME = "%$OPTNS$%";

    private Image p_loadingLogo;
    private Image p_splashLogo;

    private int i_askCommand;

    private int i_loadingProgress;

    private int i_Option_LanguageID;
    private boolean lg_Option_Light;
    private boolean lg_Option_Vibration;
    private boolean lg_Option_Sound;

    private Image[] ap_safeIcons;
    private Image[] ap_cellIcons;
    private Image p_aboutIcon;

    private SafeCell p_currentSelectedCell = null;
    private SafeRecord p_currentRecord = null;

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

    private void loadOptionsFromRMS() throws Exception
    {
        rmsFS.FSRecord p_record = p_rmsBlock.getRecordForName(RMS_OPTIONS_RECORD_NAME);
        if (p_record == null)
        {
            i_Option_LanguageID = -1;
            lg_Option_Light = true;
            lg_Option_Vibration = true;
            lg_Option_Sound = true;
            p_rmsBlock.createNewRecord(RMS_OPTIONS_RECORD_NAME, 32);
            saveOptionsToRMS();
        }
        else
        {
            byte[] ab_data = p_record.getData();
            ByteArrayInputStream p_inStr = new ByteArrayInputStream(ab_data);
            DataInputStream p_dataStream = new DataInputStream(p_inStr);
            i_Option_LanguageID = p_dataStream.readByte();
            lg_Option_Light = p_dataStream.readBoolean();
            lg_Option_Vibration = p_dataStream.readBoolean();
            lg_Option_Sound = p_dataStream.readBoolean();
            p_dataStream.close();
            p_dataStream = null;
            p_inStr = null;
            ab_data = null;
        }
    }

    private void saveOptionsToRMS() throws Exception
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
            p_MainCanvas = new InsideCanvas(p_CurrentDisplay.getColor(Display.COLOR_BACKGROUND), p_CurrentDisplay.getColor(Display.COLOR_FOREGROUND), p_clockImage);
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
                        p_MainCanvas.lg_started = false;
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
            case COMMAND_ResetCMD:
                {
                    i_askCommand = COMMAND_ResetCMD;
                    switch (_screenId)
                    {
                        case SCR_EnterNamePasswordSCR:
                            {
                                p_menuBlock.MB_initScreen(SCR_ConfirmationSCR, true);
                            }
                            ;
                            break;
                        case SCR_InputKeySCR:
                            {
                                p_menuBlock.MB_replaceCurrentScreen(SCR_ConfirmationSCR, true);
                            }
                            ;
                            break;
                    }
                }
                ;
                break;
            case COMMAND_ChangeIconCMD:
                {
                    p_menuBlock.MB_initScreen(SCR_ChangeIconSCR, true);
                }
                ;
                break;
            case COMMAND_SetKeyCMD:
                {
                    switch (_screenId)
                    {
                        case SCR_SafeListSCR:
                            {
                                p_currentRecord = p_model.getRecordForIndex(p_menuBlock.MB_getIndexOfSelectedItem());
                                p_currentSelectedCell = null;
                            }
                            ;
                            break;
                        case SCR_SafeCellListSCR:
                            {
                                p_currentSelectedCell = p_currentRecord.getCellForIndex(p_menuBlock.MB_getIndexOfSelectedItem());
                            }
                            ;
                            break;
                    }
                    p_menuBlock.MB_initScreen(SCR_SetKeySCR, true);
                }
                ;
                break;
            case COMMAND_YesCMD:
                {
                    switch (i_askCommand)
                    {
                        case COMMAND_DeleteCMD:
                            {
                                _showWait();
                                try
                                {
                                    p_currentRecord.removeCell(p_model.getUserName(), p_model.getUserPassword(), p_currentSelectedCell);
                                    p_model.saveChangedSafesToRMS(p_rmsBlock);
                                    p_menuBlock.MB_back(true);
                                }
                                catch (Exception e)
                                {
                                    p_menuBlock.MB_back(false);
                                    p_menuBlock.MB_viewAlert(getStringForIndex(ErrorTXT), getStringForIndex(DeleteErrorTXT), AlertType.ERROR, 2000);
                                    return;
                                }
                                _hiddeWait();
                            }
                            ;
                            break;
                        case COMMAND_ResetCMD:
                            {
                                if (p_currentRecord == null)
                                {
                                    _showWait();
                                    try
                                    {
                                        p_model.resetSafe();
                                        p_model.saveChangedSafesToRMS(p_rmsBlock);
                                    }
                                    catch (Exception e)
                                    {
                                        p_menuBlock.MB_back(true);
                                        p_menuBlock.MB_viewAlert(getStringForIndex(ErrorTXT), e.getMessage(), AlertType.ERROR, 8000);
                                        return;
                                    }
                                    p_menuBlock.MB_clearScreenStack(SCR_SafeListSCR, true);
                                    _hiddeWait();
                                }
                                else
                                {
                                    _showWait();
                                    try
                                    {
                                        p_currentRecord.resetRecord(p_model.getUserName(), p_model.getUserPassword());
                                        p_model.saveChangedSafesToRMS(p_rmsBlock);
                                        p_menuBlock.MB_back(true);
                                    }
                                    catch (Exception e)
                                    {
                                        p_menuBlock.MB_back(false);
                                        p_menuBlock.MB_viewAlert(getStringForIndex(ErrorTXT), e.getMessage(), AlertType.ERROR, 8000);
                                        return;
                                    }

                                    _hiddeWait();
                                }
                            }
                            ;
                            break;
                    }
                }
                ;
                break;
            case COMMAND_CreateCMD:
                {
                    p_currentSelectedCell = null;
                    p_menuBlock.MB_initScreen(SCR_CellContentSCR, true);
                }
                ;
                break;
            case COMMAND_ChangePasswordCMD:
                {
                    p_menuBlock.MB_initScreen(SCR_SetNamePasswordSCR, true);
                }
                ;
                break;
            case COMMAND_DeleteCMD:
                {
                    p_currentSelectedCell = p_currentRecord.getCellForIndex(p_menuBlock.MB_getIndexOfSelectedItem());
                    i_askCommand = COMMAND_DeleteCMD;
                    p_menuBlock.MB_initScreen(SCR_ConfirmationSCR, true);
                }
                ;
                break;
            case COMMAND_HelpCMD:
                {
                    switch (_screenId)
                    {
                        case SCR_SafeListSCR:
                            p_menuBlock.MB_initScreen(SCR_HelpSafeListSCR, true);
                            break;
                        case SCR_SafeCellListSCR:
                            p_menuBlock.MB_initScreen(SCR_HelpSafeCellListSCR, true);
                            break;
                        case SCR_CellContentSCR:
                            p_menuBlock.MB_initScreen(SCR_HelpCellSCR, true);
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
            case COMMAND_OpenCMD:
                {
                    switch (_screenId)
                    {
                        case SCR_SafeListSCR:
                            {
                                p_currentRecord = p_model.getRecordForIndex(p_menuBlock.MB_getIndexOfSelectedItem());
                                p_currentSelectedCell = null;
                            }
                            ;
                            break;
                        case SCR_SafeCellListSCR:
                            {
                                p_currentSelectedCell = p_currentRecord.getCellForIndex(p_menuBlock.MB_getIndexOfSelectedItem());
                            }
                            ;
                            break;
                    }
                    openCommand();
                }
                ;
                break;
            case COMMAND_EnterCMD:
                {
                    switch (_screenId)
                    {
                        case SCR_ChangeIconSCR:
                            {
                                changeIcon(p_menuBlock.MB_getIndexOfSelectedItem());
                            }
                            ;
                            break;
                        case SCR_InputKeySCR:
                            {
                                String s_key = getKeyFromInputKeyForm((Form) _screen);
                                if (p_currentSelectedCell != null)
                                {
                                    if (!p_currentSelectedCell.checkKey(s_key))
                                    {
                                        p_menuBlock.MB_back(true);
                                        p_menuBlock.MB_viewAlert(getStringForIndex(ErrorTXT), getStringForIndex(WrongKeyTXT), AlertType.ERROR, 2000);
                                        return;
                                    }

                                    p_currentSelectedCell.setKeyString(s_key);
                                    openCommand();
                                }
                                else
                                {
                                    if (!p_currentRecord.checkKeyCode(s_key))
                                    {
                                        p_menuBlock.MB_back(true);
                                        p_menuBlock.MB_viewAlert(getStringForIndex(ErrorTXT), getStringForIndex(WrongKeyTXT), AlertType.ERROR, 2000);
                                        return;
                                    }

                                    p_currentRecord.setKey(s_key);
                                    openCommand();
                                }
                                return;
                            }
                        case SCR_EnterNamePasswordSCR:
                            {
                                NamePasswordRecord p_record = getDataFromEnterNamePasswordForm((Form) _screen);
                                if (!p_model.checkNamePassword(p_record.s_Name, p_record.s_Password))
                                {
                                    p_menuBlock.MB_viewAlert(getStringForIndex(ErrorTXT), getStringForIndex(WrongPassworTXT), AlertType.ERROR, 2000);
                                    return;
                                }

                                _showWait();
                                p_model.setNamePassword(p_record.s_Name, p_record.s_Password);
                                try
                                {
                                    p_model.saveChangedSafesToRMS(p_rmsBlock);
                                }
                                catch (Exception e)
                                {
                                    p_menuBlock.MB_viewAlert(getStringForIndex(ErrorTXT), getStringForIndex(WrongPassworTXT), AlertType.ERROR, 2000);
                                }
                                p_menuBlock.MB_replaceCurrentScreen(SCR_SafeListSCR, true);
                                _hiddeWait();
                                return;
                            }
                        case SCR_CellContentSCR:
                            {
                                if (p_currentSelectedCell == null)
                                {
                                    _showWait();
                                    try
                                    {
                                        SafeCell p_newCell = fillCellDataFromForm((Form) _screen, null);
                                        p_currentRecord.addCell(p_model.getUserName(), p_model.getUserPassword(), p_newCell);
                                        p_currentRecord.dataUpdated(p_model.getUserName(), p_model.getUserPassword());
                                        p_model.saveChangedSafesToRMS(p_rmsBlock);
                                        p_menuBlock.MB_back(false);
                                    }
                                    catch (Exception e)
                                    {
                                        p_menuBlock.MB_back(false);
                                        p_menuBlock.MB_viewAlert(getStringForIndex(ErrorTXT), getStringForIndex(CreateErrorTXT), AlertType.ERROR, Alert.FOREVER);
                                    }
                                    _hiddeWait();
                                    return;
                                }
                                else
                                {
                                    try
                                    {
                                        fillCellDataFromForm((Form) _screen, p_currentSelectedCell);
                                        p_menuBlock.MB_back(true);
                                    }
                                    catch (IOException ex)
                                    {
                                        viewAlert(getStringForIndex(ErrorTXT), getStringForIndex(ChangeCellErrorTXT), AlertType.ERROR, Alert.FOREVER);
                                        return;
                                    }

                                    try
                                    {
                                        p_currentRecord.dataUpdated(p_model.getUserName(), p_model.getUserPassword());
                                    }
                                    catch (Exception e)
                                    {
                                        viewAlert(getStringForIndex(ErrorTXT), getStringForIndex(ChangeCellErrorTXT), AlertType.ERROR, Alert.FOREVER);
                                    }
                                }
                            }
                            ;
                            break;
                        case SCR_SetNamePasswordSCR:
                            {
                                NamePasswordRecord p_record = getDataFromSetNamePasswordForm((Form) _screen);
                                if (!p_model.checkNamePassword(p_model.getUserName(), p_record.s_Text))
                                {
                                    p_menuBlock.MB_viewAlert(getStringForIndex(ErrorTXT), getStringForIndex(WrongPassworTXT), AlertType.ERROR, Alert.FOREVER);
                                    return;
                                }

                                final NamePasswordRecord p_rec = p_record;
                                _showWait();
                                try
                                {
                                    if (!p_model.changeNamePassword(p_rmsBlock, p_rec.s_Name, p_rec.s_Password))
                                    {
                                        p_menuBlock.MB_viewAlert(getStringForIndex(ErrorTXT), getStringForIndex(ChangePasswordErrorTXT), AlertType.ERROR, Alert.FOREVER);
                                        return;
                                    }

                                    p_model.saveChangedSafesToRMS(p_rmsBlock);
                                }
                                catch (Exception e)
                                {
                                    p_menuBlock.MB_viewAlert(getStringForIndex(ErrorTXT), getStringForIndex(ErrorTXT) + ": " + e.getMessage(), AlertType.ERROR, Alert.FOREVER);
                                }
                                finally
                                {
                                    p_menuBlock.MB_back(false);
                                }
                                _hiddeWait();

                                return;
                            }
                        case SCR_SetKeySCR:
                            {
                                final Displayable p_scr = _screen;

                                final NamePasswordRecord p_record = getDataFromSetKeyForm((Form) p_scr);
                                if (p_currentSelectedCell != null)
                                {
                                    if (!p_currentSelectedCell.checkKey(p_record.s_Text))
                                    {
                                        p_menuBlock.MB_viewAlert(getStringForIndex(ErrorTXT), getStringForIndex(WrongOldKeyTXT), AlertType.ERROR, 2000);
                                    }
                                    else
                                    {
                                        try
                                        {
                                            p_currentSelectedCell.changeKeyCode(p_model.getUserName(), p_model.getUserPassword(), p_record.s_Password, p_record.s_Text);
                                            p_currentRecord.dataUpdated(p_model.getUserName(), p_model.getUserPassword());
                                        }
                                        catch (Exception e)
                                        {
                                            p_menuBlock.MB_back(false);
                                            p_menuBlock.MB_viewAlert(getStringForIndex(ErrorTXT), e.getMessage(), AlertType.ERROR, Alert.FOREVER);
                                            return;
                                        }
                                    }
                                }
                                else if (p_currentRecord != null)
                                {
                                    if (!p_currentRecord.checkKeyCode(p_record.s_Text))
                                    {
                                        p_menuBlock.MB_viewAlert(getStringForIndex(ErrorTXT), getStringForIndex(WrongOldKeyTXT), AlertType.ERROR, 2000);
                                    }
                                    else
                                    {
                                        try
                                        {
                                            p_currentRecord.changeKey(p_model.getUserName(), p_model.getUserPassword(), p_record.s_Password, p_record.s_Text);
                                        }
                                        catch (Exception e)
                                        {
                                            p_menuBlock.MB_back(false);
                                            p_menuBlock.MB_viewAlert(getStringForIndex(ErrorTXT), e.getMessage(), AlertType.ERROR, Alert.FOREVER);
                                            return;
                                        }
                                    }
                                }
                                p_menuBlock.MB_back(false);
                            }
                            ;
                            break;
                    }

                    try
                    {
                        _showWait();
                        p_model.saveChangedSafesToRMS(p_rmsBlock);
                        _hiddeWait();
                    }
                    catch (Exception e)
                    {
                        _hiddeWait();
                        p_menuBlock.MB_viewAlert(getStringForIndex(ErrorTXT), getStringForIndex(RMSerrorTXT), AlertType.ERROR, Alert.FOREVER);
                    }
                }
                ;
                break;
            case COMMAND_OptionsCMD:
                {
                    p_menuBlock.MB_initScreen(SCR_OptionsSCR, true);
                }
                ;
                break;
            case COMMAND_ExitCMD:
                {
                    i_CurrentMidletState = STATE_RELEASING;
                }
                ;
                break;
        }
    }

    private void _processListItem(int _screenId, int _itemId)
    {
        switch (_screenId)
        {
            case SCR_ChangeIconSCR:
                {
                    changeIcon(_itemId);
                }
                ;
                break;
            case SCR_SafeCellListSCR:
                {
                    p_currentSelectedCell = p_currentRecord.getCellForIndex(_itemId);
                    openCommand();
                }
                ;
                break;
            case SCR_SafeListSCR:
                {
                    p_currentRecord = p_model.getRecordForIndex(_itemId);
                    p_currentSelectedCell = null;
                    openCommand();
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
                        p_menuBlock.MB_viewAlert(getStringForIndex(ErrorTXT), e.getMessage(), AlertType.ERROR, Alert.FOREVER);
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

        p_model = new PrivateSafe();

        try
        {
            try
            {
                p_rmsBlock = new rmsFS(p_model.getAppID());
            }
            catch (RecordStoreException e)
            {
                throw new IOException("Fatal rms error");
            }
            i_loadingProgress += 25;
            p_MainCanvas.repaint();

            loadOptionsFromRMS();
            p_languageBlock.LB_initLanguageBlock(RESOURCE_LANGUAGELIST, i_Option_LanguageID);
            i_loadingProgress += 25;
            p_MainCanvas.repaint();

            p_menuBlock.MB_initMenuBlock(RESOURCE_MENU);
            i_loadingProgress += 25;
            p_MainCanvas.repaint();

            try
            {
                p_model.initApplication(p_rmsBlock, this);
            }
            catch (Exception e)
            {
                viewAlert("Init error", e.getMessage(), AlertType.ERROR, 5000);
                this.notifyDestroyed();
                return;
            }

            Image p_image = Image.createImage(RESOURCE_SAFE_ICONS);
            ap_safeIcons = parseImage(p_image, 6);
            p_image = Image.createImage(RESOURCE_CELL_ICONS);
            ap_cellIcons = parseImage(p_image, 10);

            p_aboutIcon = Image.createImage(RESOURCE_ABOUT_ICON);

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

        if (p_model.hasNamePassword())
        {
            p_menuBlock.MB_activateMenu(p_MainCanvas, SCR_EnterNamePasswordSCR, this);
        }
        else
        {
            p_menuBlock.MB_activateMenu(p_MainCanvas, SCR_SafeListSCR, this);
        }

        p_splashLogo = null;

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
            p_MainCanvas.lg_started = false;
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
            case PrivateSafe.ACTION_GET_RMS:
                {
                    return p_rmsBlock;
                }
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

    private void changeIcon(int _newIcon)
    {
        p_currentSelectedCell.setIconIndex(_newIcon);
        try
        {
            p_currentRecord.dataUpdated(p_model.getUserName(), p_model.getUserPassword());
        }
        catch (IOException e)
        {
            p_menuBlock.MB_back(true);
            p_menuBlock.MB_viewAlert(getStringForIndex(ErrorTXT), getStringForIndex(CantChangeIconTXT), AlertType.ERROR, 3000);
            return;
        }
        p_menuBlock.MB_back(true);
    }

    private void openCommand()
    {
        if (p_currentSelectedCell != null)
        {
            if (p_currentSelectedCell.hasKeyCode() && p_currentSelectedCell.s_keyCode.equals(""))
            {
                p_menuBlock.MB_initScreen(SCR_InputKeySCR, true);
            }
            else
            {
                if (p_menuBlock.MB_currentScreenId == SCR_InputKeySCR)
                {
                    p_menuBlock.MB_replaceCurrentScreen(SCR_CellContentSCR, true);
                }
                else
                {
                    p_menuBlock.MB_initScreen(SCR_CellContentSCR, true);
                }
            }
        }
        else
        {
            if (p_currentRecord.hasKeyCode() && p_currentRecord.s_Key.equals(""))
            {
                p_menuBlock.MB_initScreen(SCR_InputKeySCR, true);
            }
            else
            {
                try
                {
                    if (!p_currentRecord.decodeCells(p_model.getUserName(), p_model.getUserPassword()))
                    {
                        p_menuBlock.MB_viewAlert(getStringForIndex(ErrorTXT), getStringForIndex(CantExtractCellsTXT), AlertType.ERROR, Alert.FOREVER);
                    }
                }
                catch (Exception e)
                {
                    p_menuBlock.MB_viewAlert(getStringForIndex(ErrorTXT), getStringForIndex(ErrorTXT), AlertType.ERROR, Alert.FOREVER);
                    return;
                }

                if (p_menuBlock.MB_currentScreenId == SCR_InputKeySCR)
                {
                    p_menuBlock.MB_replaceCurrentScreen(SCR_SafeCellListSCR, true);
                }
                else
                {
                    p_menuBlock.MB_initScreen(SCR_SafeCellListSCR, true);
                }
            }
        }
    }

    private String getKeyFromInputKeyForm(Form _enterKeyForm)
    {
        String s_result = ((TextField) _enterKeyForm.get(0)).getString();
        return s_result;
    }

    private Form createInputKeyForm(Object _destObject)
    {
        String s_ObjectName = "";

        if (_destObject instanceof SafeRecord)
        {
            SafeRecord p_record = (SafeRecord) _destObject;
            s_ObjectName = p_record.s_viewName;
        }
        else
        {
            SafeCell p_cell = (SafeCell) _destObject;
            s_ObjectName = p_cell.getName();
        }

        Form p_newForm = new Form(getStringForIndex(SetKeyTitleTXT));

        TextField p_newKey = new TextField(getStringForIndex(KeyForTXT) + " \"" + s_ObjectName + "\"", "", 8, TextField.NUMERIC);
        p_newForm.append(p_newKey);

        return p_newForm;
    }

    private Form createSetKeyForm(Object _destObject)
    {
        boolean lg_hasKeyAlready = false;
        String s_ObjectName = "";

        if (_destObject instanceof SafeRecord)
        {
            SafeRecord p_record = (SafeRecord) _destObject;
            lg_hasKeyAlready = p_record.hasKeyCode();
            s_ObjectName = p_record.s_viewName;
        }
        else
        {
            SafeCell p_cell = (SafeCell) _destObject;
            lg_hasKeyAlready = p_cell.hasKeyCode();
            s_ObjectName = p_cell.getName();
        }

        Form p_newForm = new Form(getStringForIndex(SetKeyTitleTXT));

        if (lg_hasKeyAlready)
        {
            TextField p_oldKey = new TextField(getStringForIndex(OldKeyForTXT) + " \"" + s_ObjectName + "\"", "", 8, TextField.NUMERIC);
            TextField p_newKey = new TextField(getStringForIndex(NewKeyForTXT) + " \"" + s_ObjectName + "\"", "", 8, TextField.NUMERIC);
            p_newForm.append(p_oldKey);
            p_newForm.append(p_newKey);
        }
        else
        {
            TextField p_newKey = new TextField(getStringForIndex(KeyForTXT) + " \"" + s_ObjectName + "\"", "", 8, TextField.NUMERIC);
            p_newForm.append(p_newKey);
        }

        return p_newForm;
    }

    private Form createEnterNamePasswordForm()
    {
        Form p_form = new Form(getStringForIndex(EnterNamePasswordTitleTXT));
        TextField p_nameField = new TextField(getStringForIndex(NameTXT), "", 8, TextField.ANY);
        TextField p_passwordField = new TextField(getStringForIndex(PasswordTXT), "", 8, TextField.PASSWORD);
        p_form.append(p_nameField);
        p_form.append(p_passwordField);
        return p_form;
    }

    private NamePasswordRecord getDataFromEnterNamePasswordForm(Form _enterNamePasswordFrom)
    {
        String s_name = ((TextField) _enterNamePasswordFrom.get(0)).getString();
        String s_password = ((TextField) _enterNamePasswordFrom.get(1)).getString();
        return new NamePasswordRecord(s_name, s_password, null);
    }

    private Form createSetNamePasswordForm(NamePasswordRecord _oldNamePassword)
    {
        Form p_form = new Form(getStringForIndex(SetNamePasswordTitleTXT));

        if (!p_model.getUserPassword().equals(""))
        {
            TextField p_nameField = new TextField(getStringForIndex(NameTXT), _oldNamePassword.s_Name, 8, TextField.ANY);
            TextField p_oldPasswordField = new TextField(getStringForIndex(OldPasswordTXT), "", 8, TextField.PASSWORD);
            TextField p_newPasswordField = new TextField(getStringForIndex(PasswordTXT), "", 8, TextField.PASSWORD);
            p_form.append(p_nameField);
            p_form.append(p_oldPasswordField);
            p_form.append(p_newPasswordField);
        }
        else
        {
            TextField p_nameField = new TextField(getStringForIndex(NameTXT), "", 8, TextField.ANY);
            TextField p_newPasswordField = new TextField(getStringForIndex(PasswordTXT), "", 8, TextField.PASSWORD);
            p_form.append(p_nameField);
            p_form.append(p_newPasswordField);
        }

        return p_form;
    }

    private NamePasswordRecord getDataFromSetNamePasswordForm(Form _enterNamePasswordFrom)
    {
        if (_enterNamePasswordFrom.size() == 3)
        {
            String s_name = ((TextField) _enterNamePasswordFrom.get(0)).getString();
            String s_oldpassword = ((TextField) _enterNamePasswordFrom.get(1)).getString();
            String s_newpassword = ((TextField) _enterNamePasswordFrom.get(2)).getString();
            return new NamePasswordRecord(s_name, s_newpassword, s_oldpassword);
        }
        else
        {
            String s_name = ((TextField) _enterNamePasswordFrom.get(0)).getString();
            String s_newpassword = ((TextField) _enterNamePasswordFrom.get(1)).getString();
            return new NamePasswordRecord(s_name, s_newpassword, "");
        }
    }

    private NamePasswordRecord getDataFromSetKeyForm(Form _changeKeyForm)
    {
        String s_newKey = null;
        String s_oldKey = null;

        if (_changeKeyForm.size() == 2)
        {
            s_oldKey = ((TextField) _changeKeyForm.get(0)).getString();
            s_newKey = ((TextField) _changeKeyForm.get(1)).getString();
        }
        else
        {
            s_newKey = ((TextField) _changeKeyForm.get(0)).getString();
        }
        return new NamePasswordRecord("", s_newKey, s_oldKey);
    }

    private Form createCellDataForm(SafeCell _safeCell) throws IOException
    {
        Form p_form = new Form(getStringForIndex(CellContentTitleTXT));

        if (_safeCell == null)
        {
            p_form.setTitle(getStringForIndex(NewCellTXT));

            ImageItem p_imageItem = new ImageItem(null, ap_cellIcons[0], ImageItem.LAYOUT_CENTER, null);

            TextField p_cellTitleField = new TextField(getStringForIndex(TitleTXT), "", 12, TextField.ANY);

            TextField p_cellNameField = new TextField(getStringForIndex(NameTXT), "", 16, TextField.ANY);
            TextField p_cellPasswordField = new TextField(getStringForIndex(PasswordTXT), "", 16, TextField.ANY);
            TextField p_cellComments = new TextField(getStringForIndex(CommentsTXT), "", 64, TextField.ANY);

            p_form.append(p_imageItem);
            p_form.append(p_cellTitleField);
            p_form.append(p_cellNameField);
            p_form.append(p_cellPasswordField);
            p_form.append(p_cellComments);
        }
        else
        {
            ImageItem p_imageItem = new ImageItem(null, ap_cellIcons[_safeCell.getIconIndex()], ImageItem.LAYOUT_CENTER, null);
            TextField p_cellTitleField = new TextField(getStringForIndex(TitleTXT), _safeCell.getName(), 12, TextField.ANY);

            NamePasswordRecord p_npr = _safeCell.decodeData(p_model.getUserName(), p_model.getUserPassword());

            TextField p_cellNameField = new TextField(getStringForIndex(NameTXT), p_npr.s_Name, 16, TextField.ANY);
            TextField p_cellPasswordField = new TextField(getStringForIndex(PasswordTXT), p_npr.s_Password, 16, TextField.ANY);
            TextField p_cellComments = new TextField(getStringForIndex(CommentsTXT), p_npr.s_Text, 64, TextField.ANY);

            p_form.append(p_imageItem);
            p_form.append(p_cellTitleField);
            p_form.append(p_cellNameField);
            p_form.append(p_cellPasswordField);
            p_form.append(p_cellComments);
        }

        return p_form;
    }

    private SafeCell fillCellDataFromForm(Form _form, SafeCell _safeCell) throws IOException
    {
        String s_title = ((TextField) _form.get(1)).getString();
        String s_name = ((TextField) _form.get(2)).getString();
        String s_password = ((TextField) _form.get(3)).getString();
        String s_comments = ((TextField) _form.get(4)).getString();

        NamePasswordRecord p_record = new NamePasswordRecord(s_name, s_password, s_comments);
        if (_safeCell != null)
        {
            _safeCell.setName(s_title);
            _safeCell.setNewData(p_model.getUserName(), p_model.getUserPassword(), p_record);
            return null;
        }
        else
        {
            SafeCell p_cell = new SafeCell(p_model.getUserName(), p_model.getUserPassword(), s_title, p_record, 0);
            return p_cell;
        }
    }

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
            case SCR_SafeListSCR:
                {
                    switch (_commandId)
                    {
                        case COMMAND_ChangePasswordCMD:
                            {
                                if (p_model.ownKeysPresented()) return false; else return true;
                            }
                    }
                }
            case SCR_CellContentSCR:
                {
                    switch (_commandId)
                    {
                        case COMMAND_ChangeIconCMD:
                            if (p_currentSelectedCell == null) return false; else return true;
                    }
                }
                ;
                break;
            case SCR_InputKeySCR:
                {
                    switch (_commandId)
                    {
                        case COMMAND_ResetCMD:
                            if (p_currentSelectedCell == null) return true; else return false;
                    }
                }
                ;
                break;
            case SCR_SafeCellListSCR:
                {
                    switch (_commandId)
                    {
                        case COMMAND_DeleteCMD:
                        case COMMAND_SetKeyCMD:
                        case COMMAND_OpenCMD:
                            {
                                if (p_currentRecord.getCellsNumber() != 0) return true;
                                return false;
                            }
                    }
                }
                ;
                break;
        }
        return true;
    }

    public Displayable customScreen(int _screenId)
    {
        Displayable p_result = null;
        switch (_screenId)
        {
            case SCR_InputKeySCR:
                {
                    if (p_currentSelectedCell != null)
                    {
                        return createInputKeyForm(p_currentSelectedCell);
                    }
                    else
                    {
                        return createInputKeyForm(p_currentRecord);
                    }
                }
            case SCR_SafeListSCR:
                {
                    List p_list = new List(getStringForIndex(SafeListTitleTXT), List.IMPLICIT);

                    int i_imageIndex = p_model.getRecordForIndex(0).hasKeyCode() ? 5 : 0;
                    p_list.append(getStringForIndex(CreditCardsTXT), ap_safeIcons[i_imageIndex]);
                    p_model.getRecordForIndex(0).s_viewName = getStringForIndex(CreditCardsTXT);

                    i_imageIndex = p_model.getRecordForIndex(1).hasKeyCode() ? 5 : 1;
                    p_list.append(getStringForIndex(InternetTXT), ap_safeIcons[i_imageIndex]);
                    p_model.getRecordForIndex(1).s_viewName = getStringForIndex(InternetTXT);

                    i_imageIndex = p_model.getRecordForIndex(2).hasKeyCode() ? 5 : 2;
                    p_list.append(getStringForIndex(OfficeTXT), ap_safeIcons[i_imageIndex]);
                    p_model.getRecordForIndex(2).s_viewName = getStringForIndex(OfficeTXT);

                    i_imageIndex = p_model.getRecordForIndex(3).hasKeyCode() ? 5 : 3;
                    p_list.append(getStringForIndex(HouseTXT), ap_safeIcons[i_imageIndex]);
                    p_model.getRecordForIndex(3).s_viewName = getStringForIndex(HouseTXT);

                    i_imageIndex = p_model.getRecordForIndex(4).hasKeyCode() ? 5 : 4;
                    p_list.append(getStringForIndex(MiscTXT), ap_safeIcons[i_imageIndex]);
                    p_model.getRecordForIndex(4).s_viewName = getStringForIndex(MiscTXT);

                    p_result = p_list;
                }
                ;
                break;
            case SCR_ChangeIconSCR:
                {
                    List p_list = new List(getStringForIndex(ChangeIconTitleTXT), List.IMPLICIT);

                    for (int li = 0; li < ap_cellIcons.length; li++)
                    {
                        p_list.append(getStringForIndex(IconTXT) + " " + Integer.toString(li + 1), ap_cellIcons[li]);
                    }

                    p_result = p_list;
                }
                ;
                break;
            case SCR_SafeCellListSCR:
                {
                    List p_list = new List(p_currentRecord.s_viewName, List.IMPLICIT);

                    for (int li = 0; li < p_currentRecord.getCellsNumber(); li++)
                    {
                        SafeCell p_cell = p_currentRecord.getCellForIndex(li);
                        if (p_cell.hasKeyCode())
                        {
                            p_list.append(p_cell.getName(), ap_safeIcons[5]);
                        }
                        else
                        {
                            p_list.append(p_cell.getName(), ap_cellIcons[p_cell.getIconIndex()]);
                        }
                    }

                    p_result = p_list;
                }
                ;
                break;
            case SCR_SetKeySCR:
                {
                    if (p_currentSelectedCell != null)
                    {
                        return createSetKeyForm(p_currentSelectedCell);
                    }
                    else
                    {
                        return createSetKeyForm(p_currentRecord);
                    }
                }
            case SCR_CellContentSCR:
                {
                    try
                    {
                        return createCellDataForm(p_currentSelectedCell);
                    }
                    catch (IOException e)
                    {
                        p_menuBlock.MB_viewAlert(getStringForIndex(ErrorTXT), e.getMessage(), AlertType.ERROR, Alert.FOREVER);
                    }
                }
                ;
                break;
            case SCR_EnterNamePasswordSCR:
                {
                    return createEnterNamePasswordForm();
                }
            case SCR_SetNamePasswordSCR:
                {
                    return createSetNamePasswordForm(new NamePasswordRecord(p_model.getUserName(), p_model.getUserPassword(), null));
                }
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
                return p_CurrentDisplay.flashBacklight(0);
            case ITEM_LanguageSelectITM:
                return p_languageBlock.LB_as_LanguageNames.length > 1;
        }
        return false;
    }

    public void onExitScreen(Displayable _screen, int _screenId)
    {
        switch (_screenId)
        {
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
        }
    }

    private static final int ChangeKeyTitleTXT = 0;
    private static final int WrongPassworTXT = 1;
    private static final int HelpSafeCellListTextTXT = 2;
    private static final int KeyTXT = 3;
    private static final int AboutCmdTXT = 4;
    private static final int OldKeyForTXT = 5;
    private static final int OffTXT = 6;
    private static final int NewKeyForTXT = 7;
    private static final int RMSerrorTXT = 8;
    private static final int TitleTXT = 9;
    private static final int DeleteErrorTXT = 10;
    private static final int MiscTXT = 11;
    private static final int HelpSafeListTitleTXT = 12;
    private static final int HelpCmdTXT = 13;
    private static final int CantExtractCellsTXT = 14;
    private static final int ChangeIconCmdTXT = 15;
    private static final int ChangePasswordErrorTXT = 16;
    private static final int PasswordTXT = 17;
    private static final int DeleteCmdTXT = 18;
    private static final int NewCellTXT = 19;
    private static final int AboutTextTXT = 20;
    private static final int YesCmdTXT = 21;
    private static final int OldKeyTXT = 22;
    private static final int IconTXT = 23;
    private static final int NameTXT = 24;
    private static final int OptionsCmdTXT = 25;
    private static final int EnterNamePasswordTitleTXT = 26;
    private static final int SetKeyCmdTXT = 27;
    private static final int HelpSafeCellListTitleTXT = 28;
    private static final int CantChangeIconTXT = 29;
    private static final int CommentsTXT = 30;
    private static final int InputKeyTitleTXT = 31;
    private static final int InternetTXT = 32;
    private static final int BackCmdTXT = 33;
    private static final int CreditCardsTXT = 34;
    private static final int KeyForTXT = 35;
    private static final int RenameCmdTXT = 36;
    private static final int OfficeTXT = 37;
    private static final int CantSaveDataTXT = 38;
    private static final int HouseTXT = 39;
    private static final int ChangeCellErrorTXT = 40;
    private static final int OnTXT = 41;
    private static final int HelpSafeListTextTXT = 42;
    private static final int ConfirmationTXT = 43;
    private static final int OldPasswordTXT = 44;
    private static final int NoCmdTXT = 45;
    private static final int ChangePasswordCmdTXT = 46;
    private static final int BackLightTXT = 47;
    private static final int ResetCmdTXT = 48;
    private static final int WrongKeyTXT = 49;
    private static final int SafeListTitleTXT = 50;
    private static final int ChangeKeyCmdTXT = 51;
    private static final int HelpCellTextTXT = 52;
    private static final int LanguageSelectTitleTXT = 53;
    private static final int LanguageSelectTXT = 54;
    private static final int ConfirmationTitleTXT = 55;
    private static final int ChangeIconTitleTXT = 56;
    private static final int RenameTitleTXT = 57;
    private static final int CreateCmdTXT = 58;
    private static final int EnterCmdTXT = 59;
    private static final int OldNameTXT = 60;
    private static final int CreateErrorTXT = 61;
    private static final int WrongOldKeyTXT = 62;
    private static final int ErrorTXT = 63;
    private static final int RepeatCmdTXT = 64;
    private static final int ExitCmdTXT = 65;
    private static final int SetNamePasswordTitleTXT = 66;
    private static final int SafeCellListTitleTXT = 67;
    private static final int WaitPleaseTXT = 68;
    private static final int OpenCmdTXT = 69;
    private static final int CellContentTitleTXT = 70;
    private static final int AboutTitleTXT = 71;
    private static final int HelpCellTitleTXT = 72;
    private static final int SetKeyTitleTXT = 73;
    private static final int OptionsTitleTXT = 74;


    private static final int SCR_OptionsSCR = 0;
    private static final int SCR_SetNamePasswordSCR = 18;
    private static final int SCR_AboutSCR = 28;
    private static final int SCR_ChangeKeySCR = 46;
    private static final int SCR_ChangeIconSCR = 56;
    private static final int SCR_SafeCellListSCR = 66;
    private static final int SCR_InputKeySCR = 86;
    private static final int SCR_HelpCellSCR = 98;
    private static final int SCR_OnOffSCR = 111;
    private static final int SCR_SetKeySCR = 119;
    private static final int SCR_CellContentSCR = 129;
    private static final int SCR_ConfirmationSCR = 145;
    private static final int SCR_LanguageSelectSCR = 160;
    private static final int SCR_EnterNamePasswordSCR = 168;
    private static final int SCR_SafeListSCR = 180;
    private static final int SCR_HelpSafeListSCR = 200;
    private static final int SCR_HelpSafeCellListSCR = 213;


    private static final int ITEM_LanguageSelectITM = 0;
    private static final int ITEM_ConfirmationTextITM = 2;
    private static final int ITEM_AboutImageITM = 6;
    private static final int ITEM_HelpSafeListTextITM = 3;
    private static final int ITEM_BackLightITM = 1;
    private static final int ITEM_AboutTextITM = 7;
    private static final int ITEM_HelpCellTextITM = 5;
    private static final int ITEM_HelpSafeCellListTextITM = 4;


    private static final int COMMAND_EnterCMD = 226;
    private static final int COMMAND_ResetCMD = 227;
    private static final int COMMAND_ChangeIconCMD = 228;
    private static final int COMMAND_DeleteCMD = 229;
    private static final int COMMAND_BackCMD = 230;
    private static final int COMMAND_SetKeyCMD = 231;
    private static final int COMMAND_AboutCMD = 232;
    private static final int COMMAND_OpenCMD = 233;
    private static final int COMMAND_HelpCMD = 234;
    private static final int COMMAND_ChangePasswordCMD = 235;
    private static final int COMMAND_OptionsCMD = 236;
    private static final int COMMAND_CreateCMD = 237;
    private static final int COMMAND_NoCMD = 238;
    private static final int COMMAND_ExitCMD = 239;
    private static final int COMMAND_YesCMD = 240;
}
