package FinancialCalc;

import FinancialCalc.EvalModules.*;

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
                            int i_frameWidth = p_clockImage.getWidth() / 5;
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

    private static final String CLOCK_IMAGE = "/res/clock.png";
    private static final String LOADING_LOGO = "/res/loading.png";
    private static final String SPLASH_LOGO = "/res/splash.png";
    private static final String RESOURCE_LANGUAGELIST = "/res/langs.bin";
    private static final String RESOURCE_MENU = "/res/menu.bin";
    private static final String RESOURCE_ICONS = "/res/icons.png";
    private static final String RESOURCE_ABOUT_ICON = "/res/abouticon.png";
    private static final String RMS_OPTIONS_RECORD_NAME = "%$OPTNS$%";

    private Image p_loadingLogo;
    private Image p_splashLogo;

    private int i_loadingProgress;

    private int i_Option_LanguageID;
    private boolean lg_Option_Light;
    private boolean lg_Option_Vibration;
    private boolean lg_Option_Sound;

    private Image p_aboutIcon;
    private Image [] ap_Icons;

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

    private EvalModule p_selectedModule;

    private Displayable createResultForm()
    {
        List p_result = new List(getStringForIndex(ResultFormTitleTXT)+" \""+s_moduleName+"\"",List.IMPLICIT);

        for(int li=0;li<p_selectedModule.getFieldsNumber();li++)
        {
            String s_str = p_selectedModule.getNameForField(li,this);
            String s_value = p_selectedModule.getFieldValue(li);
            s_str = s_str + "="+s_value;

            p_result.append(s_str,ap_Icons[3]);
        }

        int i_index = p_result.size();

        for(int li=0;li<p_selectedModule.getResultsNumber();li++)
        {
            String s_name = p_selectedModule.getNameForResult(li,this);
            String s_value = p_selectedModule.getResult(li);

            s_name = s_name +"="+s_value;

            p_result.append(s_name,ap_Icons[4]);
        }

        p_result.setSelectedIndex(i_index,true);

        return p_result;
    }

    private Form createFormForModule()
    {
        Form p_form = new Form(getStringForIndex(CalculateFormTitleTXT)+" \""+s_moduleName+"\"");
        for (int li = 0; li < p_selectedModule.getFieldsNumber(); li++)
        {
            String s_label = p_selectedModule.getNameForField(li, this);
            String s_initValue = null;
            s_initValue = p_selectedModule.getFieldValue(li);

            TextField p_field = new TextField(s_label, s_initValue, 32, TextField.DECIMAL);

            p_form.append(p_field);
        }

        return p_form;
    }

    private String s_lastError;

    private String calculateCurrentForm(Form _form)
    {
        p_selectedModule.beginTransaction();
        String s_error = null;
        for (int li = 0; li < p_selectedModule.getFieldsNumber(); li++)
        {
            TextField p_textfield = (TextField) _form.get(li);
            String s_value = p_textfield.getString().trim();
            String s_diag = p_selectedModule.setFieldValue(li, s_value);
            if (s_diag != null)
            {
                if (s_error == null)
                {
                    s_error = s_diag;
                }
            }
        }
        if (s_error != null)
        {
            return s_error;
        }

        s_error = p_selectedModule.checkFields();
        if (s_error != null)
        {
            return s_error;
        }

        p_selectedModule.calculate();

        return null;
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

    String s_moduleName = null;

    public void run()
    {
        i_loadingProgress = 0;
        i_CurrentMidletState = STATE_LOADING;
        p_MainCanvas.repaint();

        try
        {
            try
            {
                p_rmsBlock = new rmsFS("FC76432");
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
            Image p_icn = Image.createImage(RESOURCE_ICONS);
            ap_Icons = parseImage(p_icn,5);
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

        p_menuBlock.MB_activateMenu(p_MainCanvas, SCR_MainSCR, this);

        p_loadingLogo = null;
        i_CurrentMidletState = STATE_WAITING;
        p_MainCanvas.repaint();

        p_splashLogo = null;

        final int COUNTER_DELAY = 10;

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
            case 0 :
            case 1 :
            case 2 :
                {
                    return ap_Icons[_index];
                }
        }

        return null;
    }

    public String getStringForIndex(int _index)
    {
        return p_languageBlock.LB_as_TextStringArray[_index];
    }

    private CurrentRatio p_CurrentRatio = new CurrentRatio();
    private AssetsTurnover p_AssetsTurnover = new AssetsTurnover();
    private CashFlow p_CashFlow = new CashFlow();
    private DependenceRatio p_DependenceRatio = new DependenceRatio();
    private EBITDA p_Ebitda = new EBITDA();
    private EconomicValueAdded p_EconomicValueAdded = new EconomicValueAdded();
    private NetPresentValue p_NetPresentValue = new NetPresentValue();
    private PEratio p_PEratio = new PEratio();
    private QuickRatio p_QuickRatio = new QuickRatio();
    private ReturnOnAssets p_ReturnOnAssets = new ReturnOnAssets();
    private ROE p_Roe = new ROE();
    private ROI p_Roi = new ROI();
    private Sprad p_Sprad = new Sprad();

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

    public void _processListItem(int _screenId, int _itemId)
    {
        switch (_screenId)
        {
            case SCR_EvalCoeffSCR:
                {
                    switch (_itemId)
                    {
                        case ITEM_CurLiquidityITM :
                            {
                               p_selectedModule = p_CurrentRatio;
                                s_moduleName = getStringForIndex(CurLiquidityTitleTXT);
                            };break;
                        case ITEM_TerLiquidityITM :
                            {
                               p_selectedModule = p_QuickRatio;
                                s_moduleName = getStringForIndex(TermLiquidityTitleTXT);
                            };break;
                        case ITEM_TurnoverAssetsITM :
                            {
                               p_selectedModule = p_AssetsTurnover;
                                s_moduleName = getStringForIndex(TurnoverAssetsTitleTXT);
                            };break;
                        case ITEM_RoeITM :
                            {
                               p_selectedModule = p_Roe;
                                s_moduleName = getStringForIndex(RoeTitleTXT);
                            };break;
                        case ITEM_RoiITM :
                            {
                               p_selectedModule = p_Roi;
                                s_moduleName = getStringForIndex(RoiTitleTXT);

                            };break;
                        case ITEM_ProfitabilityAssetsITM:
                            {
                               p_selectedModule = p_ReturnOnAssets;
                                s_moduleName = getStringForIndex(ProfitabilityAssetsTitleTXT);

                            };break;
                        case ITEM_FinDependenceITM:
                            {
                               p_selectedModule = p_DependenceRatio;
                                s_moduleName = getStringForIndex(FinDependenceTitleTXT);

                            };break;
                        default:
                            {
                                return;
                            }
                    }
                    p_selectedModule.beginTransaction();
                    p_menuBlock.MB_initScreen(SCR_CalculateFormSCR,true);
                }
                ;
                break;
            case SCR_SharesSCR:
                {
                    switch (_itemId)
                    {
                        case ITEM_PEratioITM:
                            {
                               p_selectedModule = p_PEratio;
                                s_moduleName = getStringForIndex(PEratioTitleTXT);
                            };break;
                        case ITEM_SpreadITM:
                            {
                               p_selectedModule = p_Sprad;
                                s_moduleName = getStringForIndex(SpreadTitleTXT);
                            };break;
                        default:
                            {
                                if (true) return;
                            }
                    }
                    p_selectedModule.beginTransaction();
                    p_menuBlock.MB_initScreen(SCR_CalculateFormSCR,true);
                }
                ;
                break;
            case SCR_FinApprSCR:
                {
                    switch (_itemId)
                    {
                        case ITEM_DiscFlowITM:
                            {
                               p_selectedModule = p_NetPresentValue;
                                s_moduleName = getStringForIndex(DiscFlowTitleTXT);
                            };break;
                        case ITEM_CashFlowITM:
                            {
                               p_selectedModule = p_CashFlow;
                                s_moduleName = getStringForIndex(CashFlowTitleTXT);

                            };break;
                        case ITEM_EbitdaITM:
                            {
                               p_selectedModule = p_Ebitda;
                                s_moduleName = getStringForIndex(EbitdaTitleTXT);

                            };break;
                        case ITEM_EconValueAddedITM:
                            {
                               p_selectedModule = p_EconomicValueAdded;
                                s_moduleName = getStringForIndex(EconValueAddedTitleTXT);

                            };break;
                        default:
                            {
                               if (true) return;
                            }
                    }
                    p_selectedModule.beginTransaction();
                    p_menuBlock.MB_initScreen(SCR_CalculateFormSCR,true);
                }
                ;
                break;
            case SCR_LanguageSelectSCR:
                {
                    _showWait();
                    try
                    {
                        i_Option_LanguageID = _itemId;
                        p_languageBlock.LB_setLanguage(i_Option_LanguageID);
                        try
                        {
                            saveOptionsToRMS();
                        }
                        catch (Exception we)
                        {
                        }
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

    public void _processCommand(Displayable _screen, int _screenId, int _commandId, int _selectedId)
    {
        switch (_commandId)
        {
            case COMMAND_CalculateCMD:
                {
                    s_lastError = calculateCurrentForm((Form)_screen);
                    if (s_lastError==null)
                    {
                        p_menuBlock.MB_replaceCurrentScreen(SCR_ResultFormSCR,true);
                    }
                    else
                    {
                        p_menuBlock.MB_viewAlert(getStringForIndex(ErrorTXT),s_lastError,AlertType.ERROR,Alert.FOREVER);
                    }
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
            case SCR_ResultFormSCR:
                {
                    return createResultForm();
                }
            case SCR_CalculateFormSCR:
                {
                    Form p_form  = null;
                    p_form  = createFormForModule();
                    return p_form;
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
            case ITEM_BackLightITM: return true;
            case ITEM_LanguageSelectITM : return p_languageBlock.LB_as_LanguageNames.length>1;
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
                };break;
        }
    }



    private static final int SCR_OptionsSCR = 0;
    private static final int SCR_AboutSCR = 18;
    private static final int SCR_ResultFormSCR = 36;
    private static final int SCR_MainSCR = 44;
    private static final int SCR_CalculateFormSCR = 75;
    private static final int SCR_LanguageSelectSCR = 93;
    private static final int SCR_FinApprSCR = 101;
    private static final int SCR_HelpSCR = 139;
    private static final int SCR_EvalCoeffSCR = 152;
    private static final int SCR_ErrorValueFormSCR = 205;
    private static final int SCR_SharesSCR = 213;
    private static final int SCR_OnOffSCR = 241;


    private static final int ITEM_FinApprITM = 1;
    private static final int ITEM_HelpTextITM = 18;
    private static final int ITEM_DiscFlowITM = 5;
    private static final int ITEM_PEratioITM = 3;
    private static final int ITEM_CurLiquidityITM = 9;
    private static final int ITEM_TerLiquidityITM = 10;
    private static final int ITEM_AboutImageITM = 19;
    private static final int ITEM_EbitdaITM = 7;
    private static final int ITEM_RoiITM = 15;
    private static final int ITEM_RoeITM = 14;
    private static final int ITEM_ProfitabilityAssetsITM = 12;
    private static final int ITEM_AboutTextITM = 20;
    private static final int ITEM_SharesITM = 2;
    private static final int ITEM_TurnoverAssetsITM = 11;
    private static final int ITEM_CashFlowITM = 6;
    private static final int ITEM_LanguageSelectITM = 16;
    private static final int ITEM_BackLightITM = 17;
    private static final int ITEM_EvalCoeffITM = 0;
    private static final int ITEM_SpreadITM = 4;
    private static final int ITEM_EconValueAddedITM = 8;
    private static final int ITEM_FinDependenceITM = 13;


    private static final int COMMAND_OptionsCMD = 249;
    private static final int COMMAND_BackCMD = 250;
    private static final int COMMAND_CalculateCMD = 251;
    private static final int COMMAND_AboutCMD = 252;
    private static final int COMMAND_ExitCMD = 253;
    private static final int COMMAND_OpenCMD = 254;
    private static final int COMMAND_HelpCMD = 255;

    private static final int CalculateFormTitleTXT = 0;
    private static final int AboutCmdTXT = 1;
    private static final int EvalCoeffTitleTXT = 2;
    private static final int OffTXT = 3;
    private static final int Sposob4TitleTXT = 4;
    private static final int LiquidityTitleTXT = 5;
    private static final int PEratioTitleTXT = 6;
    private static final int HelpCmdTXT = 7;
    private static final int ErrorValueFormTitleTXT = 8;
    private static final int RoeTitleTXT = 9;
    private static final int ResultFormTitleTXT = 10;
    private static final int AboutTextTXT = 11;
    private static final int YesCmdTXT = 12;
    private static final int CurLiquidityTitleTXT = 13;
    private static final int FinDependenceTitleTXT = 14;
    private static final int CashFlowTitleTXT = 15;
    private static final int OptionsCmdTXT = 16;
    private static final int SpreadTitleTXT = 17;
    private static final int DiscFlowTitleTXT = 18;
    private static final int StartCmdTXT = 19;
    private static final int SharesTitleTXT = 20;
    private static final int ProfitabilityAssetsTitleTXT = 21;
    private static final int BackCmdTXT = 22;
    private static final int CantSaveDataTXT = 23;
    private static final int EbitdaTitleTXT = 24;
    private static final int RoiTitleTXT = 25;
    private static final int OnTXT = 26;
    private static final int BackLightTXT = 27;
    private static final int MainScreenTitleTXT = 28;
    private static final int LanguageSelectTitleTXT = 29;
    private static final int LanguageSelectTXT = 30;
    private static final int CoeffFreSharesTitleTXT = 31;
    private static final int FinApprTitleTXT = 32;
    private static final int CreateCmdTXT = 33;
    private static final int HelpTextTXT = 34;
    private static final int ErrorTXT = 35;
    private static final int ExitCmdTXT = 36;
    private static final int CalculateCmdTXT = 37;
    private static final int TurnoverAssetsTitleTXT = 38;
    private static final int TermLiquidityTitleTXT = 39;
    private static final int HelpTitleTXT = 40;
    private static final int WaitPleaseTXT = 41;
    private static final int OpenCmdTXT = 42;
    private static final int AboutTitleTXT = 43;
    private static final int EconValueAddedTitleTXT = 44;
    private static final int OptionsTitleTXT = 45;
}
