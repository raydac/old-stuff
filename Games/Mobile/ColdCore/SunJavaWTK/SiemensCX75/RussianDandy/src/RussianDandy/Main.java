package RussianDandy;

import javax.microedition.midlet.MIDlet;
import javax.microedition.midlet.MIDletStateChangeException;
import javax.microedition.lcdui.*;
import javax.microedition.lcdui.game.Sprite;
import javax.microedition.rms.RecordStoreException;
import java.io.*;

public class Main extends MIDlet implements Runnable, MenuActionListener
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
                        int i_action = getGameAction(_code);

                        switch (i_action)
                        {
                            case Canvas.UP:
                            case Canvas.LEFT:
                                {
                                    i_pressedKey = KEY_PREV;
                                    p_MainCanvas.repaint();
                                }
                                ;
                                break;
                            case Canvas.DOWN:
                            case Canvas.RIGHT:
                                {
                                    i_pressedKey = KEY_NEXT;
                                    p_MainCanvas.repaint();
                                }
                                ;
                                break;
                            default:
                                {
                                    i_pressedKey = KEY_EXIT;
                                    p_MainCanvas.repaint();
                                }
                        }
                    }
                    ;
                    break;
                case STATE_WAITING:
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
                        int i_action = getGameAction(_code);

                        i_pressedKey = KEY_NONE;

                        switch (i_action)
                        {
                            case Canvas.UP:
                            case Canvas.LEFT:
                                {
                                    if (i_currentFrame > 0)
                                    {
                                        i_currentFrame--;
                                    }
                                    p_MainCanvas.repaint();
                                }
                                ;
                                break;
                            case Canvas.DOWN:
                            case Canvas.RIGHT:
                                {
                                    if (i_currentFrame < i_framesNumber - 1)
                                    {
                                        i_currentFrame++;
                                    }
                                    p_MainCanvas.repaint();
                                }
                                ;
                                break;
                            default:
                                {
                                    p_menuBlock.MB_clearScreenStack(SCR_MainSCR, true);
                                }
                        }
                    }
                    ;
                    break;
                case STATE_WAITING:
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
                        _g.setColor(0x202020);
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
                        _g.setColor(FRAME_BACKGROUND_COLOR);
                        _g.fillRect(0, 0, i_ScreenWidth, i_ScreenHeight);

                        int i_xoff = (i_ScreenWidth - FRAME_WIDTH) >> 1;
                        int i_yoff = (i_ScreenHeight - FRAME_HEIGHT) >> 1;

                        if (i_yoff < INDICATORS_HEIGHT) i_yoff = 0;

                        int i_frameXOffset = FRAME_WIDTH * i_currentFrame;

                        int i_clipX = Math.max(i_xoff, 0);
                        int i_clipY = Math.max(i_yoff, 0);

                        _g.setClip(i_clipX, i_clipY, FRAME_WIDTH, FRAME_HEIGHT);
                        _g.drawImage(p_FramesImages, i_xoff - i_frameXOffset, i_yoff, 0);

                        int i_iconY = i_ScreenHeight - 2 - INDICATORS_HEIGHT;

                        int i_indiX = 0;

                        if (i_pressedKey == KEY_EXIT)
                        {
                            _g.setClip(i_indiX, i_iconY, INDICATORS_WIDTH, INDICATORS_HEIGHT);
                            _g.drawImage(ap_indicators[0], i_indiX, i_iconY - INDICATORS_HEIGHT, 0);
                        }
                        else
                        {
                            _g.setClip(i_indiX, i_iconY, INDICATORS_WIDTH, INDICATORS_HEIGHT);
                            _g.drawImage(ap_indicators[0], i_indiX, i_iconY, 0);
                        }

                        i_indiX = (i_ScreenWidth - (INDICATORS_WIDTH << 1)) >> 1;

                        if (i_currentFrame > 0)
                        {
                            if (i_pressedKey == KEY_PREV)
                            {
                                _g.setClip(i_indiX, i_iconY, INDICATORS_WIDTH, INDICATORS_HEIGHT);
                                _g.drawImage(ap_indicators[1], i_indiX, i_iconY - INDICATORS_HEIGHT, 0);
                            }
                            else
                            {
                                _g.setClip(i_indiX, i_iconY, INDICATORS_WIDTH, INDICATORS_HEIGHT);
                                _g.drawImage(ap_indicators[1], i_indiX, i_iconY, 0);
                            }
                        }
                        i_indiX += INDICATORS_WIDTH;

                        if (i_currentFrame < i_framesNumber - 1)
                        {
                            if (i_pressedKey == KEY_NEXT)
                            {
                                _g.setClip(i_indiX, i_iconY, INDICATORS_WIDTH, INDICATORS_HEIGHT);
                                _g.drawImage(ap_indicators[2], i_indiX, i_iconY - INDICATORS_HEIGHT, 0);
                            }
                            else
                            {
                                _g.setClip(i_indiX, i_iconY, INDICATORS_WIDTH, INDICATORS_HEIGHT);
                                _g.drawImage(ap_indicators[2], i_indiX, i_iconY, 0);
                            }
                        }

                        i_indiX = (i_ScreenWidth - DIGITS_WIDTH * 3 - 3);

                        _g.setClip(i_indiX, i_iconY, DIGITS_WIDTH, INDICATORS_HEIGHT);
                        _g.drawImage(ap_digits[i_currentFrame + 2], i_indiX, i_iconY, 0);
                        i_indiX += DIGITS_WIDTH;

                        _g.setClip(i_indiX, i_iconY, DIGITS_WIDTH, INDICATORS_HEIGHT);
                        _g.drawImage(ap_digits[0], i_indiX, i_iconY, 0);
                        i_indiX += DIGITS_WIDTH;

                        _g.setClip(i_indiX, i_iconY, DIGITS_WIDTH, INDICATORS_HEIGHT);
                        _g.drawImage(ap_digits[i_framesNumber + 1], i_indiX, i_iconY, 0);

                        _g.setClip(0, 0, i_ScreenWidth, i_ScreenHeight);
                    }
                    ;
                    break;
                case STATE_WAITING:
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
    private static final int STATE_WAITING = 4;
    private static final int STATE_PAUSED = 5;
    private static final int STATE_RELEASING = 6;

    private int i_CurrentMidletState = STATE_INITING;
    private Display p_CurrentDisplay;
    private InsideCanvas p_MainCanvas;

    private Object p_synchroObject = new Object();

    private LanguageBlock p_languageBlock;
    private MenuBlock p_menuBlock;
    private rmsFS p_rmsBlock;

    private static final String[] LIST_IMAGES = new String[]
    {
        "/res/s1.png",
        "/res/s2.png",
        "/res/s3.png",
        "/res/s4.png",
    };

    private static final String CLOCK_IMAGE = "/res/clock.png";
    private static final String LOADING_LOGO = "/res/loading.png";
    private static final String SPLASH_LOGO = "/res/splash.png";
    private static final String RESOURCE_LANGUAGELIST = "/res/langs.bin";
    private static final String RESOURCE_MENU = "/res/menu.bin";
    private static final String RESOURCE_ABOUT_ICON = "/res/abouticon.png";
    private static final String RESOURCE_NUMBERS_FONT = "/res/digits.png";
    private static final String RESOURCE_INDICATORS = "/res/ind.png";
    private static final String RMS_OPTIONS_RECORD_NAME = "%$OPTNS$%";

    private Image p_loadingLogo;
    private Image p_splashLogo;

    private int i_loadingProgress;

    private int i_Option_LanguageID;
    private boolean lg_Option_Light;
    private boolean lg_Option_Vibration;
    private boolean lg_Option_Sound;

    private Image p_aboutIcon;

    private Image[] ap_digits;
    private Image[] ap_indicators;

    private static final int INDICATORS_HEIGHT = 22;
    private static final int INDICATORS_WIDTH = 22;
    private static final int DIGITS_WIDTH = 12;

    private static int i_currentFrame = 0;
    private static Image p_FramesImages = null;
    private static int i_framesNumber = 0;
    private static int i_pressedKey = 0;

    private static final int KEY_NONE = 0;
    private static final int KEY_EXIT = 1;
    private static final int KEY_PREV = 2;
    private static final int KEY_NEXT = 3;

    private static final int FRAME_WIDTH = 132;
    private static final int FRAME_HEIGHT = 150;
    private static final int FRAME_BACKGROUND_COLOR = 0x0A0B0F;

    private static void loadImagesFromResource(String _resource) throws IOException
    {
        p_FramesImages = Image.createImage(_resource);
        i_framesNumber = p_FramesImages.getWidth() / FRAME_WIDTH;
        i_currentFrame = 0;
    }

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
            lg_Option_Light = true;
            lg_Option_Vibration = true;
            lg_Option_Sound = true;
            p_rmsBlock.createNewRecord(RMS_OPTIONS_RECORD_NAME, 32);
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

            saveOptionsToRMS();
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
            try
            {
                saveOptionsToRMS();
            }
            catch (Exception e)
            {
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
        i_CurrentMidletState = STATE_WAITING;
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
        i_CurrentMidletState = STATE_WORKING;
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
            case COMMAND_StartCMD:
                {
                    _processListItem(_screenId, _selectedId);
                }
                ;
                break;
            case COMMAND_HelpCMD:
                {
                    p_menuBlock.MB_initScreen(SCR_HelpSCR, true);
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
            case SCR_MainSCR:
                {
                    int i_selectedIndex = p_menuBlock.MB_getIndexOfSelectedItem();
                    String s_imageName = LIST_IMAGES[i_selectedIndex];

                    _showWait();
                    try
                    {
                        loadImagesFromResource(s_imageName);

                        i_currentFrame = 0;
                        i_CurrentMidletState = STATE_WORKING;
                        p_CurrentDisplay.setCurrent(p_MainCanvas);
                    }
                    catch (Exception e)
                    {
                        _hiddeWait();
                        p_menuBlock.MB_viewAlert(getStringForIndex(ErrorTXT), "Loading error", AlertType.ERROR, 5000);
                    }
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

        try
        {
            try
            {
                p_rmsBlock = new rmsFS("CHKR2432");
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

            p_aboutIcon = Image.createImage(RESOURCE_ABOUT_ICON);

            Image p_img = Image.createImage(RESOURCE_INDICATORS);
            ap_indicators = parseImage(p_img, 3);
            p_img = null;
            p_img = Image.createImage(RESOURCE_NUMBERS_FONT);
            ap_digits = parseImage(p_img, 11);
            p_img = null;

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

        p_menuBlock.MB_activateMenu(p_MainCanvas, SCR_MainSCR, this);

        p_loadingLogo = null;
        i_CurrentMidletState = STATE_WAITING;
        p_MainCanvas.repaint();

        p_splashLogo = null;

        while (i_CurrentMidletState == STATE_WAITING || i_CurrentMidletState == STATE_WORKING || i_CurrentMidletState == STATE_PAUSED)
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
            saveOptionsToRMS();
            p_rmsBlock.close(true);
        }
        catch (Exception e)
        {
            viewAlert(getStringForIndex(ErrorTXT), "RMS error", AlertType.WARNING, Alert.FOREVER);
            this.notifyDestroyed();
        }

        p_MainCanvas.lg_started = false;
        i_CurrentMidletState = STATE_RELEASING;
        this.notifyDestroyed();
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
        }
        return true;
    }

    public Displayable customScreen(int _screenId)
    {
        Displayable p_result = null;
        switch (_screenId)
        {
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

    private static final int AboutCmdTXT = 0;
    private static final int OffTXT = 1;
    private static final int Sposob4TitleTXT = 2;
    private static final int HelpCmdTXT = 3;
    private static final int AboutTextTXT = 4;
    private static final int YesCmdTXT = 5;
    private static final int OptionsCmdTXT = 6;
    private static final int StartCmdTXT = 7;
    private static final int Sposob1TitleTXT = 8;
    private static final int BackCmdTXT = 9;
    private static final int CantSaveDataTXT = 10;
    private static final int Sposob3TitleTXT = 11;
    private static final int OnTXT = 12;
    private static final int BackLightTXT = 13;
    private static final int MainScreenTitleTXT = 14;
    private static final int LanguageSelectTitleTXT = 15;
    private static final int LanguageSelectTXT = 16;
    private static final int CreateCmdTXT = 17;
    private static final int HelpTextTXT = 18;
    private static final int ErrorTXT = 19;
    private static final int ExitCmdTXT = 20;
    private static final int HelpTitleTXT = 21;
    private static final int WaitPleaseTXT = 22;
    private static final int Sposob2TitleTXT = 23;
    private static final int OpenCmdTXT = 24;
    private static final int AboutTitleTXT = 25;
    private static final int OptionsTitleTXT = 26;


    private static final int SCR_OptionsSCR = 0;
    private static final int SCR_AboutSCR = 18;
    private static final int SCR_MainSCR = 36;
    private static final int SCR_LanguageSelectSCR = 72;
    private static final int SCR_HelpSCR = 80;
    private static final int SCR_OnOffSCR = 93;


    private static final int ITEM_HelpTextITM = 6;
    private static final int ITEM_LanguageSelectITM = 4;
    private static final int ITEM_Sposob1ITM = 0;
    private static final int ITEM_AboutImageITM = 7;
    private static final int ITEM_BackLightITM = 5;
    private static final int ITEM_Sposob2ITM = 1;
    private static final int ITEM_AboutTextITM = 8;
    private static final int ITEM_Sposob3ITM = 2;
    private static final int ITEM_Sposob4ITM = 3;


    private static final int COMMAND_OptionsCMD = 101;
    private static final int COMMAND_BackCMD = 102;
    private static final int COMMAND_AboutCMD = 103;
    private static final int COMMAND_StartCMD = 104;
    private static final int COMMAND_ExitCMD = 105;
    private static final int COMMAND_HelpCMD = 106;
}
