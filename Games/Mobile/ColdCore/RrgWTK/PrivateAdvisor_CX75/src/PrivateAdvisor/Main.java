package PrivateAdvisor;

import javax.microedition.midlet.MIDlet;
import javax.microedition.midlet.MIDletStateChangeException;
import javax.microedition.lcdui.*;
import javax.microedition.rms.RecordStoreException;
import javax.microedition.media.Player;
import javax.microedition.media.Manager;
import javax.microedition.media.MediaException;
import java.io.*;
import java.util.Random;

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

        protected Main p_parent;

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

        public InsideCanvas(Main _parent, int _backgroundColor, int _messageTextColor, Image _clockImage)
        {
            super();
            setFullScreenMode(true);

            p_parent = _parent;

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
                        switch (_code)
                        {
                            case -4:
                                {
                                    i_pressedKey = KEY_MENU;
                                    p_MainCanvas.repaint();
                                };break;
                            case -1:
                            case -3:
                                {
                                    i_pressedKey = KEY_EXIT;
                                    p_MainCanvas.repaint();
                                };break;
                            default:
                                {
                                    i_pressedKey = KEY_ASK;
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
                        i_pressedKey = KEY_NONE;

                        switch (_code)
                        {
                            case -4:
                                {
                                    p_menuBlock.MB_activateMenu(p_MainCanvas,SCR_MainSCR,p_parent);
                                    initWaitMode();
                                };break;
                            case -1:
                            case -3:
                                {
                                    i_CurrentMidletState = STATE_RELEASING;
                                };break;
                            default:
                                {
                                    initWaitMode();
                                    i_CurrentAnistate = ANISTATE_GIVETICKET;
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
                        int i_startX = (i_ScreenWidth - 132) >> 1;
                        int i_startY = (i_ScreenHeight - 176) >> 1;
                        _g.drawImage(p_background, i_startX, i_startY, 0);

                        int i_DX = i_startX+1;
                        int i_DY = i_startY+1;

                        int i_frameWidth = p_man_hand.getWidth() / 4;
                        _g.setClip(i_DX, i_startY + 49, i_frameWidth, p_man_hand.getHeight());
                        _g.drawImage(p_man_hand, i_DX - i_frameWidth * i_ManHandFrame, i_startY + 49, 0);

                        i_frameWidth = p_monkey_head.getWidth() / 3;
                        int i_headX = i_DX + 132 - i_frameWidth;
                        int i_headHeight = p_monkey_head.getHeight() >> 1;
                        _g.setClip(i_headX, i_DY, i_frameWidth, i_headHeight);
                        int i_posY = lg_MonkeyEyesClosed ? 0 : p_monkey_head.getHeight() >> 1;
                        _g.drawImage(p_monkey_head, i_headX - i_MonkeyHeadFrame * i_frameWidth, i_DY - i_posY, 0);

                        i_frameWidth = p_monkey_hand.getWidth() / 5;
                        int i_handX = i_DX + 132 - i_frameWidth;
                        _g.setClip(i_handX, i_DY, i_frameWidth, p_monkey_hand.getHeight());
                        _g.drawImage(p_monkey_hand, i_handX - i_MonkeyHandFrame * i_frameWidth, i_DY, 0);

                        _g.setClip(i_startX, i_startY, 132, 150);

                        int i_x = 0;
                        int i_y = 0;
                        if (!p_TicketSprite.lg_SpriteInvisible)
                        {
                            i_x = (p_TicketSprite.i_ScreenX >> 8) + i_startX;
                            i_y = (p_TicketSprite.i_ScreenY >> 8) + i_startY;

                            _g.drawImage(ap_ticketFrames[p_TicketSprite.i_ObjectState],i_x,i_y,0);

                            if (i_CurrentAnistate == ANISTATE_SHOWTICKET || i_CurrentAnistate == ANISTATE_TICKETDOWN)
                            {
                                int i_fHeight = p_TicketText.getHeight() / 10;
                                int i_fWidth = p_TicketText.getWidth();

                                int i_tickW = p_TicketSprite.i_width >> 8;
                                int i_tickH = p_TicketSprite.i_height >> 8;

                                i_x = i_x+((i_tickW - i_fWidth)>>1);
                                i_y = i_y+((i_tickH - i_fHeight)>>1);

                                _g.setClip(i_x,i_y,i_fWidth,i_fHeight);
                                _g.drawImage(p_TicketText,i_x,i_y-p_TicketSprite.i_ObjectType *i_fHeight,0);
                            }
                        }


                        int i_panelY = 0;
                        i_startY += 150;
                        if (i_pressedKey == KEY_EXIT)
                        {
                            i_panelY = p_PanelIcons.getHeight()>>1;
                        }
                        else
                        {
                            i_panelY = 0;
                        }
                        _g.setClip(i_startX,i_startY,32,26);
                        _g.drawImage(p_PanelIcons,i_startX,i_startY-i_panelY,0);

                        if (i_pressedKey == KEY_ASK)
                        {
                            i_panelY = p_PanelIcons.getHeight()>>1;
                        }
                        else
                        {
                            i_panelY = 0;
                        }
                        _g.setClip(i_startX+31,i_startY,50,26);
                        _g.drawImage(p_PanelIcons,i_startX,i_startY-i_panelY,0);

                        if (i_pressedKey == KEY_MENU)
                        {
                            i_panelY = p_PanelIcons.getHeight()>>1;
                        }
                        else
                        {
                            i_panelY = 0;
                        }
                        _g.setClip(i_startX+81,i_startY,50,26);
                        _g.drawImage(p_PanelIcons,i_startX,i_startY-i_panelY,0);
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

    private static final int ANISTATE_WAITING = 0;
    private static final int ANISTATE_GIVETICKET = 1;
    private static final int ANISTATE_SHOWTICKET = 2;
    private static final int ANISTATE_TICKETDOWN = 3;

    private int i_CurrentAnistate;

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

    private static final String CLOCK_IMAGE = "/res/clock.png";
    private static final String LOADING_LOGO = "/res/loading.png";
    private static final String SPLASH_LOGO = "/res/splash.png";
    private static final String RESOURCE_LANGUAGELIST = "/res/langs.bin";
    private static final String RESOURCE_MENU = "/res/menu.bin";
    private static final String RESOURCE_ABOUT_ICON = "/res/abouticon.png";
    private static final String RESOURCE_BACKGROUND = "/res/bckgr.png";
    private static final String RESOURCE_MAN_HAND = "/res/sh_hand.png";
    private static final String RESOURCE_MONKEY_HEAD = "/res/m_head.png";
    private static final String RESOURCE_MONKEY_HAND = "/res/m_hand.png";
    private static final String RESOURCE_TICKET = "/res/ticket.png";
    private static final String RMS_OPTIONS_RECORD_NAME = "%$OPTNS$%";

    private Image p_loadingLogo;
    private Image p_splashLogo;

    private int i_loadingProgress;

    private int i_Option_LanguageID;
    private boolean lg_Option_Light;
    private boolean lg_Option_Vibration;
    private boolean lg_Option_Sound;

    private Image p_aboutIcon;

    private static final int INDICATORS_HEIGHT = 22;
    private static final int INDICATORS_WIDTH = 22;
    private static final int DIGITS_WIDTH = 12;

    private static int i_pressedKey = 0;

    private static final int KEY_NONE = 0;
    private static final int KEY_EXIT = 1;
    private static final int KEY_ASK = 2;
    private static final int KEY_MENU = 3;

    private static final int FRAME_WIDTH = 132;
    private static final int FRAME_HEIGHT = 150;
    private static final int FRAME_BACKGROUND_COLOR = 0x0A0B0F;

    private Image p_TicketText;
    private Image p_PanelIcons;

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
            p_MainCanvas = new InsideCanvas(this,p_CurrentDisplay.getColor(Display.COLOR_BACKGROUND), p_CurrentDisplay.getColor(Display.COLOR_FOREGROUND), p_clockImage);
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
            case COMMAND_AskCMD:
                {
                    p_menuBlock.MB_back(true);
                    initWaitMode();
                    i_CurrentAnistate = ANISTATE_GIVETICKET;
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

    private Image p_background;
    private Image p_monkey_head;
    private Image p_monkey_hand;
    private Image p_man_hand;

    private int i_ManHandFrame;
    private int i_MonkeyHeadFrame;
    private int i_MonkeyHandFrame;
    private boolean lg_MonkeyEyesClosed;

    private Player p_SoundPlayer;

    private PathController p_controller = new PathController();
    private Sprite p_TicketSprite = new Sprite(0);

    public static final short [] ash_Paths= new short[] {
         (short)11,(short)0,(short)110,(short)51,(short)3,(short)103,(short)64,(short)3,(short)89,(short)64,(short)3,(short)75,(short)55,(short)3,(short)80,(short)74,(short)3,(short)98,(short)82,(short)3,(short)120,(short)86,(short)3,(short)85,(short)99,(short)3,(short)58,(short)99,(short)3,(short)21,(short)91,(short)3,(short)33,(short)113,(short)3,(short)66,(short)125,(short)10,
    };

    private static final int PATH_TICKET_PATH = 0;


    private int[] ai_TicketSprites = new int[]
    {
        19, 7,
        35, 13,
        65, 24,
        132, 49
    };

    private int[] ai_PathPointIndexes = new int[]
    {
        3,
        6,
        9,
        1000
    };

    private Image [] ap_ticketFrames;

    private void initTicketSprite(int _index)
    {
        int i_index = _index << 1;
        int i_width = ai_TicketSprites[i_index++];
        int i_height = ai_TicketSprites[i_index];

        p_TicketSprite.i_ObjectState = _index;
        p_TicketSprite.setAnimation(i_width<<8, i_height<<8, 1, 0, 1);

        int i_answer = getRandomInt(999)/100;
        p_TicketSprite.i_ObjectType = i_answer;

    }

    private boolean lg_handHasProcessed = false;

    private void initWaitMode()
    {
        lg_handHasProcessed = false;
        i_CurrentAnistate = ANISTATE_WAITING;
        p_TicketSprite.lg_SpriteInvisible = true;
        p_controller.deactivate();
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
                p_rmsBlock = new rmsFS("PSDE723");
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

            try
            {
                InputStream p_instr = getClass().getResourceAsStream("/res/snd.mid");
                p_SoundPlayer = Manager.createPlayer(p_instr,"audio/midi");
                p_SoundPlayer.prefetch();
            }
            catch(Exception _ex)
            {
                p_SoundPlayer = null;
            }

            i_loadingProgress += 25;
            p_MainCanvas.repaint();

            p_aboutIcon = Image.createImage(RESOURCE_ABOUT_ICON);

            p_background = Image.createImage(RESOURCE_BACKGROUND);
            p_monkey_head = Image.createImage(RESOURCE_MONKEY_HEAD);
            p_monkey_hand = Image.createImage(RESOURCE_MONKEY_HAND);
            p_man_hand = Image.createImage(RESOURCE_MAN_HAND);

            ap_ticketFrames = new Image[4];
            ap_ticketFrames[0] = Image.createImage("/res/Ticket01.png");
            ap_ticketFrames[1] = Image.createImage("/res/Ticket02.png");
            ap_ticketFrames[2] = Image.createImage("/res/Ticket03.png");
            ap_ticketFrames[3] = Image.createImage("/res/Ticket04.png");

            p_TicketText = Image.createImage("/res/txt_rus.png");
            p_PanelIcons = Image.createImage("/res/icons.png");

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

        final int MAN_HAND_DELAY = 3;
        final int CMN_DELAY = 3;
        final int MONKEY_HEAD_DELAY = 2;
        final int MONKEY_HAND_DELAY = 3;
        final int TICKET_SHOW_DELAY = 50;
        final int TICKET_DOWN_DELAY = 1;

        final int I8_TICKET_DOWN_SPEED = 0x100;

        int i_man_hand_delay = MAN_HAND_DELAY;
        int i_cmn_delay = CMN_DELAY;
        int i_monkey_head_delay = MONKEY_HEAD_DELAY;
        int i_monkey_hand_delay = MONKEY_HAND_DELAY;
        int i_ticketShowDownDelay = TICKET_SHOW_DELAY;

        int i_nextKeyPathPoint = 0;

        initWaitMode();

        if (lg_Option_Sound)
        {
            if (p_SoundPlayer != null)
            {
                p_SoundPlayer.setLoopCount(-1);
                try
                {
                    p_SoundPlayer.start();
                }
                catch (MediaException e)
                {
                }
            }
        }

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

            if (lg_Option_Sound)
            {
                if (p_SoundPlayer!=null)
                {
                    if (p_SoundPlayer.getState() != Player.STARTED)
                    {
                        p_SoundPlayer.setLoopCount(-1);
                        try
                        {
                            p_SoundPlayer.prefetch();
                            p_SoundPlayer.start();
                        }
                        catch (MediaException e)
                        {
                        }
                    }
                }
            }
            else
            {
                if (p_SoundPlayer!=null)
                {
                    if (p_SoundPlayer.getState() == Player.STARTED)
                    {
                        try
                        {
                            p_SoundPlayer.stop();
                        }
                        catch (MediaException e)
                        {
                        }
                    }
                }
            }

            if (lg_Option_Light)
                com.siemens.mp.game.Light.setLightOn();
            else
                com.siemens.mp.game.Light.setLightOff();

            i_cmn_delay--;
            if (i_cmn_delay <= 0)
            {
                i_cmn_delay = CMN_DELAY;
                i_man_hand_delay--;

                if (i_man_hand_delay <= 0)
                {
                    i_man_hand_delay = MAN_HAND_DELAY;
                    i_ManHandFrame++;
                    if (i_ManHandFrame >= 4)
                    {
                        i_ManHandFrame = 0;
                    }
                }

                i_monkey_hand_delay--;
                if (i_monkey_hand_delay <= 0)
                {
                    i_monkey_hand_delay = MONKEY_HAND_DELAY;

                    if (i_CurrentAnistate == ANISTATE_GIVETICKET && !lg_handHasProcessed)
                    {
                        i_MonkeyHandFrame++;
                        if (i_MonkeyHandFrame >= 5)
                        {
                            i_MonkeyHandFrame = 0;
                            lg_handHasProcessed = true;

                            initTicketSprite(0);
                            p_TicketSprite.lg_SpriteInvisible = false;
                            p_controller.initPath(0, 0, p_TicketSprite, ash_Paths, 0, 0, -1);

                            i_nextKeyPathPoint = ai_PathPointIndexes[0];
                        }
                    }
                }

                boolean lg_stateeyechanged = false;
                if (lg_MonkeyEyesClosed)
                {
                    lg_MonkeyEyesClosed = false;
                    lg_stateeyechanged = true;
                }

                i_monkey_head_delay--;
                if (i_monkey_head_delay <= 0)
                {
                    i_monkey_head_delay = MONKEY_HEAD_DELAY;

                    if (!lg_stateeyechanged && !lg_MonkeyEyesClosed && getRandomInt(30) == 15)
                    {
                        lg_MonkeyEyesClosed = true;
                    }

                    if (i_CurrentAnistate == ANISTATE_GIVETICKET)
                    {
                        i_MonkeyHeadFrame = 1;
                    }
                    else if (getRandomInt(30) == 15)
                    {
                        switch (i_MonkeyHeadFrame)
                        {
                            case 0:
                                {
                                    i_MonkeyHeadFrame = 1;
                                }
                                ;
                                break;
                            case 1:
                                {
                                    if (getRandomInt(100) > 50)
                                    {
                                        i_MonkeyHeadFrame = 2;
                                    }
                                    else
                                    {
                                        i_MonkeyHeadFrame = 0;
                                    }
                                }
                                ;
                                break;
                            case 2:
                                {
                                    i_MonkeyHeadFrame = 1;
                                }
                                ;
                                break;
                        }
                    }
                }

                if (i_CurrentAnistate == ANISTATE_GIVETICKET && lg_handHasProcessed)
                {
                    if (p_controller.processStep())
                    {
                        if (p_controller.isCompleted())
                        {
                            i_CurrentAnistate = ANISTATE_SHOWTICKET;
                            i_ticketShowDownDelay = TICKET_SHOW_DELAY;
                        }
                        else
                        {
                            if (p_controller.getCurrentPointIndex() >= i_nextKeyPathPoint)
                            {
                                int i_objState = 0;
                                i_objState = p_TicketSprite.i_ObjectState + 1;
                                initTicketSprite(i_objState);
                                i_nextKeyPathPoint = ai_PathPointIndexes[i_objState];
                            }
                        }
                    }
                }
                else if (i_CurrentAnistate == ANISTATE_SHOWTICKET)
                {
                    i_ticketShowDownDelay--;
                    if (i_ticketShowDownDelay <= 0)
                    {
                        i_CurrentAnistate = ANISTATE_TICKETDOWN;
                    }
                }
                else if (i_CurrentAnistate == ANISTATE_TICKETDOWN)
                {
                    i_ticketShowDownDelay--;
                    if (i_ticketShowDownDelay <= 0)
                    {
                        i_ticketShowDownDelay = TICKET_DOWN_DELAY;
                        p_TicketSprite.setMainPointXY(p_TicketSprite.i_mainX, p_TicketSprite.i_mainY + I8_TICKET_DOWN_SPEED);

                        if (p_TicketSprite.i_ScreenY > (150 << 8))
                        {
                            initWaitMode();
                        }
                    }
                }

                if (p_MainCanvas.isShown()) p_MainCanvas.repaint();
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

        }

        if (p_SoundPlayer!=null)
        {
            try
            {
                p_SoundPlayer.stop();
            }
            catch (MediaException e)
            {
            }
            try
            {
                p_SoundPlayer.realize();
            }
            catch (MediaException e)
            {
            }
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

    private Random p_random = new Random();

    public int getRandomInt(int _limit)
    {
        _limit++;
        _limit = (int) (((long) Math.abs(p_random.nextInt()) * (long) _limit) >>> 31);
        return _limit;
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
                        case ITEM_SoundITM:
                            s_caption = getStringForIndex(SoundTXT);
                            i_selIndex = lg_Option_Sound ? 0 : 1;
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
            case ITEM_SoundITM:
                return true;
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
                        case ITEM_SoundITM:
                            {
                                lg_Option_Sound = alg_flagarray[0];
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
    private static final int HelpCmdTXT = 2;
    private static final int AboutTextTXT = 3;
    private static final int YesCmdTXT = 4;
    private static final int OptionsCmdTXT = 5;
    private static final int AskCmdTXT = 6;
    private static final int BackCmdTXT = 7;
    private static final int CantSaveDataTXT = 8;
    private static final int OnTXT = 9;
    private static final int BackLightTXT = 10;
    private static final int MainScreenTitleTXT = 11;
    private static final int LanguageSelectTitleTXT = 12;
    private static final int LanguageSelectTXT = 13;
    private static final int HelpTextTXT = 14;
    private static final int ErrorTXT = 15;
    private static final int ExitCmdTXT = 16;
    private static final int HelpTitleTXT = 17;
    private static final int SoundTXT = 18;
    private static final int WaitPleaseTXT = 19;
    private static final int AboutTitleTXT = 20;
    private static final int OptionsTitleTXT = 21;


    private static final int SCR_OptionsSCR = 0;
    private static final int SCR_AboutSCR = 23;
    private static final int SCR_MainSCR = 41;
    private static final int SCR_LanguageSelectSCR = 71;
    private static final int SCR_HelpSCR = 79;
    private static final int SCR_OnOffSCR = 92;


    private static final int ITEM_OptionsITM = 0;
    private static final int ITEM_HelpTextITM = 7;
    private static final int ITEM_LanguageSelectITM = 4;
    private static final int ITEM_ExitITM = 3;
    private static final int ITEM_AboutImageITM = 8;
    private static final int ITEM_BackLightITM = 5;
    private static final int ITEM_AboutITM = 2;
    private static final int ITEM_HelpITM = 1;
    private static final int ITEM_AboutTextITM = 9;
    private static final int ITEM_SoundITM = 6;


    private static final int COMMAND_OptionsCMD = 100;
    private static final int COMMAND_BackCMD = 101;
    private static final int COMMAND_ExitCMD = 102;
    private static final int COMMAND_AskCMD = 103;
}
