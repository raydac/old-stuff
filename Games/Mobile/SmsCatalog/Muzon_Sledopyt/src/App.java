import ru.coldcore.Coordinator;
//#if GAMEMODE!=""
import ru.coldcore.game.Game;
//#endif

import javax.microedition.lcdui.*;
import javax.microedition.midlet.MIDlet;

public class App
{
    //#-
    private static final int CATALOGID = 0xAB823472;
    //#+
    //$private static final int CATALOGID = /*$CATALOGID$*/;

    public static final int DATABLOCKSIZE = 128;

    public static final int FIELD_MENU = 0;
    public static final int FIELD_CANVAS = 1;

    public static boolean lg_isAlive;

    private static final int MAXSCREENSSTACK = 128;

    private static short[] ash_ScreensStack;
    private static short[] ash_ScreensSelectedItemsStack;

    private static int i_ScreenStackPointer;
    private static SMSCatalog p_catalog = null;

    public static Image [] ap_Icons;

    private static int i_SelectedContentOffset = -1;

    private static int i_CurrentSelectedItem;

    public static MIDlet p_ParentMidlet;

    //#if GAMEMODE!=""
    private static boolean lg_GameMode;
    //#endif

    private static byte [] ab_AppData;

    /**
     * Идентификатор канала дистрибьютера для подстановки в код запроса
     */
    public static String s_DistributorChannel;

    /**
     * Идентификатор номера дистрибьютера для подстановки в SMS номер запроса
     */
    public static String s_DistributorNumber;

    //-----lifecycle operations------
    private static void initDataBlock(byte [] _dataBlock)
    {
        int i_id = CATALOGID;
        _dataBlock[0] = (byte) (i_id >>> 24);
        _dataBlock[1] = (byte) (i_id >>> 16);
        _dataBlock[2] = (byte) (i_id >>> 8);
        _dataBlock[3] = (byte) i_id;

        // остальное пространство сбрасываем в 0
        for (int li = 4; li < _dataBlock.length; li++) _dataBlock[li] = 0;
        // записываем новый блок
        Main.saveDataBlock();
    }


    /**
     * Инициализация приложения
     *
     * @param _canvas    класс канваса для загрузки ресурсов если требуется
     * @param _midlet    указатель на родительский мидлет
     * @param _dataBlock блок данных приложения
     * @return true если все удачно и false если неудачно
     */
    protected static boolean init(Class _canvas, MIDlet _midlet, byte[] _dataBlock)
    {
        p_ParentMidlet = _midlet;

        s_DistributorChannel = _midlet.getAppProperty("dchannel");
        if (s_DistributorChannel!=null) s_DistributorChannel = s_DistributorChannel.trim();
        s_DistributorNumber = _midlet.getAppProperty("dnumber");
        if (s_DistributorNumber!=null) s_DistributorNumber = s_DistributorNumber.trim();

        ab_AppData = _dataBlock;
        if (ab_AppData == null)
        {
            //#-
            System.out.println("First version of the catalog");
            //#+

            ab_AppData = new byte[getDataBlockSize()];
            initDataBlock(ab_AppData);
        }
        else
        {
            // проверяем идентификатор приложения
            int i_id = ((_dataBlock[0] & 0xFF) << 24) | ((_dataBlock[1] & 0xFF) << 16) | ((_dataBlock[2] & 0xFF) << 8) | (_dataBlock[3] & 0xFF);
            if (i_id != CATALOGID)
            {
                //#-
                System.out.println("New version of the catalog");
                //#+

                // новая версия каталога, производим инициализацию
                initDataBlock(_dataBlock);
            }
            //#-
            else
                System.out.println("Current version of the catalog");
            //#+
        }

        //#if GAMEMODE!=""
        lg_GameMode = false;
        //#endif

        ash_ScreensStack = new short[MAXSCREENSSTACK];
        ash_ScreensSelectedItemsStack = new short[MAXSCREENSSTACK];

        ap_Icons = new Image[7];
        for(int li=0;li<7;li++)
        {
            try
            {
                ap_Icons[li] = Image.createImage("/ico"+(li+1)+".png");
            }
            catch (Throwable _ex)
            {
            }
        }

        try
        {
            p_catalog = new SMSCatalog("/cat.bin");
            return true;
        }
        catch (Throwable _ex)
        {
            return false;
        }
    }

    protected static void pressCanvasKey(int _key)
    {
        //#if GAMEMODE!=""
        if (lg_GameMode)
        {
            Game.keyPressed(_key);
        }
        //#endif
    }

    protected static void releaseCanvasKey(int _key)
    {
        //#if GAMEMODE!=""
        if (lg_GameMode)
        {
            Game.keyReleased(_key);
        }
        //#endif
    }

    protected static void start()
    {
        lg_isAlive = true;

        //#if AGREEMENT
        if (p_catalog.i_AgreementText!=0xFFFF)
        {
            MenuBlock.MB_activateMenu(Main.p_MainCanvas,App.SCR_AgreementSCR);
        }
        else
        //#else
        MenuBlock.MB_activateMenu(Main.p_MainCanvas, App.SCR_MainPageCR);
        //#endif
    }

    protected static void processStep()
    {
        boolean lg_AsTranslit = Main.p_languageBlock.i_CurrentLanguageIndex == 0;

        //#if AGREEMENT
        if (MenuBlock.MB_currentScreenId==App.SCR_AgreementSCR)
            return;
        else
        //#endif
        //#if GAMEMODE!=""
        if (lg_GameMode)
        {
            if (!Game.processStep())
            {
                // если false то переходим в обычный режим
                lg_GameMode = false;
                Main.changeVideoMode(Main.VIDEOMODE_CANVAS);
                byte [] ab_gameData = Game.releaseGame();
                if (ab_gameData != null)
                {
                    System.arraycopy(ab_gameData, 0, ab_AppData, ab_AppData.length - Game.GAMEDATAMAXLEN, Game.GAMEDATAMAXLEN);
                }
                Main.saveDataBlock();
                Main.changeVideoMode(Main.VIDEOMODE_MENU);
            }
        }
        else
        //#endif
        {
            long l_CurrentDate = System.currentTimeMillis();
            // Проверяем валидность каталога
            if (p_catalog.l_ValidityCatalogDate != 0)
            {
                if (p_catalog.l_ValidityCatalogDate < l_CurrentDate)
                {
                    StringBuffer p_strbuff = new StringBuffer();
                    p_strbuff.append(Main.getStringForIndex(ValidityTextTXT));

                    if (!Coordinator.isMIDP20() && p_catalog.getURL() != null)
                    {
                        p_strbuff.append('\n');
                        p_strbuff.append(p_catalog.getURL());
                    }

                    String s_msg = p_strbuff.toString();

                    MenuBlock.MB_viewAlert(Main.getStringForIndex(ValidityCaptionTXT), s_msg, null,AlertType.INFO, 3000, true);
                }

                p_catalog.l_ValidityCatalogDate = 0;
            }
            //#if MAINADVERTISMENT
            if (p_catalog.s_MainAdvertisment!=null)
            {
                if (p_catalog.l_ValidityCatalogDate==0 || p_catalog.l_MainAdvertismentValidity<l_CurrentDate)
                {
                    Image p_advImage = null;
                    try
                    {
                        p_advImage = Image.createImage("/adv.png");
                    }
                    catch (Exception e)
                    {
                    }

                    String s_msg = p_catalog.s_MainAdvertisment;
                    s_msg = lg_AsTranslit ? SMSCatalog.russian2translit(s_msg) : s_msg;

                    p_catalog.s_MainAdvertisment = null;

                    MenuBlock.MB_viewAlert(lg_AsTranslit ? SMSCatalog.russian2translit("Реклама") : "Реклама", s_msg,p_advImage, AlertType.INFO, 3000, true);
                }
            }
            //#endif
        }
    }

    public static void paint(Graphics _graphics, int _screenWidth, int _screenHeight)
    {
        //#if GAMEMODE!=""
        if (lg_GameMode)
        {
            Game.paint(_graphics);
        }
        //#endif
    }

    public static byte[] getDataBlock() throws Exception
    {
        return ab_AppData;
    }

    protected static void destroy()
    {
        lg_isAlive = false;
    }


    //-----menu operations------
    //#if SMSENGINE
    private static void sendOrderSMS(boolean _flag,String _resourceID)
    {
        // Отсылаем СМС для заказа
        String s_SMSNumber = p_catalog.getItemSMSnumForIndex(i_SelectedContentOffset);
        String s_ResourceID = _resourceID;
        if (Coordinator.sendTxtSMS(s_ResourceID, s_SMSNumber))
        {
            // Удачно
            MenuBlock.MB_viewAlert(Main.getStringForIndex(SMSSentTitleTXT), Main.getStringForIndex(SMSSentTextTXT),null, AlertType.INFO, 3000, true);
        }
        else
        {
            // Неудачно
            MenuBlock.MB_viewAlert(Main.getStringForIndex(SMSNoSentTitleTXT), Main.getStringForIndex(SMSNoSentTextTXT), null,AlertType.ERROR, Alert.FOREVER, true);
        }
        MenuBlock.MB_back(_flag);
    }

    //#if SEND2FRIEND
    private static void sendOrderSMSForFriend(String _friendNumber, boolean _flag)
    {
        // Отсылаем СМС для заказа
        String s_Send2Friend = p_catalog.getSend2FriendNumber();
        if (s_Send2Friend == null)
        {
            // Берем номер для контента
            s_Send2Friend = p_catalog.getItemSMSnumForIndex(i_SelectedContentOffset);
        }
        String s_ResourceID = p_catalog.getItemResourceIDForIndex(i_SelectedContentOffset);
        StringBuffer p_strBuff = new StringBuffer(s_ResourceID);
        p_strBuff.append(' ');
        p_strBuff.append(_friendNumber);
        String s_content = p_strBuff.toString();
        p_strBuff = null;
        if (Coordinator.sendTxtSMS(s_content, s_Send2Friend))
        {
            // Удачно
            MenuBlock.MB_viewAlert(Main.getStringForIndex(SMSSentTitleTXT), Main.getStringForIndex(SMSSentTextTXT), null,AlertType.INFO, 3000, true);
        }
        else
        {
            // Неудачно
            MenuBlock.MB_viewAlert(Main.getStringForIndex(SMSNoSentTitleTXT), Main.getStringForIndex(SMSNoSentTextTXT),null, AlertType.ERROR, Alert.FOREVER, true);
        }
        MenuBlock.MB_back(_flag);
    }
    //#endif
    //#endif

    private static Form p_LocalForm;
    private static byte [] ab_LocalBytes;

    protected static void processMenuCommand(Displayable _screen, int _screenId, int _commandId, int _selectedId)
    {
        //#if GAMEMODE!=""
        if (lg_GameMode)
        {
            switch (_commandId)
            {
                case 0 :
                {
                    // Restart game
                    Game.restartGame();
                }
                ;
                break;
                case 1 :
                {
                    // Exit
                    lg_GameMode = false;
                    Main.changeVideoMode(Main.VIDEOMODE_CANVAS);
                    ab_LocalBytes = Game.releaseGame();
                    if (ab_LocalBytes != null)
                    {
                        System.arraycopy(ab_LocalBytes, 0, ab_AppData, ab_AppData.length - Game.GAMEDATAMAXLEN, Game.GAMEDATAMAXLEN);
                    }
                    Main.saveDataBlock();
                    Main.changeVideoMode(Main.VIDEOMODE_MENU);
                }
                ;
                break;
            }
        }
        else
        //#endif
        {
            switch (_commandId)
            {
                //#if SEND2FRIEND && SMSENGINE
                case COMMAND_Send2FriendCMD :
                {
                    MenuBlock.MB_replaceCurrentScreen(SCR_Send2FriendSCR, true);
                }
                ;
                break;
                //#endif
                //#if GAMEMODE!=""
                case COMMAND_GameCMD:
                {
                    // Инициализируем игру
                    Main.changeVideoMode(Main.VIDEOMODE_CANVAS);

                    ab_LocalBytes = new byte[Game.GAMEDATAMAXLEN];
                    System.arraycopy(ab_AppData, ab_AppData.length - Game.GAMEDATAMAXLEN, ab_LocalBytes, 0, Game.GAMEDATAMAXLEN);

                    //#-
                    try{Thread.sleep(2000);}catch(Throwable _ex){}
                    //#+

                    if (!Game.initNewGame(Main.p_MainCanvas, Main.p_CurrentDisplay.numColors(), ab_LocalBytes))
                    {
                        lg_GameMode = false;
                        Main.changeVideoMode(Main.VIDEOMODE_MENU);
                        MenuBlock.MB_viewAlert("WARNING", "Can't init game", null,AlertType.WARNING, 3000, true);
                    }
                    else
                    {
                        Main.changeVideoMode(Main.VIDEOMODE_GAMECANVAS);
                        lg_GameMode = true;
                    }
                }
                ;
                break;
                //#endif
                //#if CONFIRMATION || AGREEMENT
                case COMMAND_YesCMD:
                {
                    switch(MenuBlock.MB_currentScreenId)
                    {
                        //#if AGREEMENT
                        case App.SCR_AgreementSCR:
                        {
                            MenuBlock.MB_replaceCurrentScreen(SCR_MainPageCR,true);
                            return;
                        }
                        //#endif
                        //#if CONFIRMATION
                        case App.SCR_ConfirmationSCR:
                        {
                            sendOrderSMS(false,p_catalog.getItemResourceIDForIndex(i_SelectedContentOffset));
                            MenuBlock.MB_back(true);
                            return;
                        }
                        //#endif
                    }
                }
                case COMMAND_NoCMD:
                {
                    switch(MenuBlock.MB_currentScreenId)
                    {
                        //#if AGREEMENT
                        case App.SCR_AgreementSCR:
                        {
                            lg_isAlive = false;
                            return;
                        }
                        //#endif
                        //#if CONFIRMATION
                        case App.SCR_ConfirmationSCR:
                        {
                            MenuBlock.MB_back(true);
                            return;
                        }
                        //#endif
                    }
                }
                //#endif
                //#if SMSENGINE
                case COMMAND_SendCMD:
                case COMMAND_OrderCMD:
                {
                    // Переходим на экран заказа
                    //#if CONFIRMATION
                    //$MenuBlock.MB_initScreen(SCR_ConfirmationSCR, true);
                    //#else
                    //#if SEND2FRIEND
                    if (_screenId == SCR_Send2FriendSCR)
                    {
                        p_LocalForm = (Form) _screen;
                        TextField p_phone = (TextField) p_LocalForm.get(1);
                        TextField p_msg = (TextField) p_LocalForm.get(2);
                        p_LocalForm = null;
                        String s_phone = p_phone.getString();
                        String s_msg = p_msg.getString().trim();
                        if (s_phone.startsWith("+")) s_phone = s_phone.substring(1);
                        if (s_phone.length() != 11)
                        {
                            String s_str = "Неправильный формат номера";
                            if (Main.p_languageBlock.i_CurrentLanguageIndex == 0)
                                s_str = SMSCatalog.russian2translit(s_str);
                            MenuBlock.MB_viewAlert("Warning", s_str, null,AlertType.WARNING, 1500, true);
                        }
                        else
                            sendOrderSMSForFriend(s_phone+' '+s_msg, true);
                    }
                    else
                    //#endif
                    {
                        // Собираем введенную информацию
                        String s_resID = p_catalog.getItemResourceIDForIndex(i_SelectedContentOffset);

                        //#if SMSSERVICEPARSER
                        if (ap_SMSServiceTextFields!=null)
                        {
                            s_resID = makeSMSServiceQueryFromTextFields(s_resID,ap_SMSServiceTextFields);
                            if (s_resID==null)
                            {
                                MenuBlock.MB_viewAlert("Warning",Main.getStringForIndex(ErrorFieldFillingTXT),null,AlertType.WARNING,1000,true);
                                return;
                            }
                        }
                        //#endif

                        sendOrderSMS(true,s_resID);
                    }
                    //#endif
                }
                ;
                break;
                //#endif
                case COMMAND_ExitCMD:
                {
                    lg_isAlive = false;
                }
                ;
                break;
                //#if UPDATEURL
                case COMMAND_UpgradeCMD:
                {
                    String s_url = p_catalog.getURL();

                    if (s_url != null && Coordinator.isMIDP20())
                    {
                        if (Coordinator.plReq(p_ParentMidlet, s_url))
                        {
                            lg_isAlive = false;
                        }
                        else
                        {
                            MenuBlock.MB_viewAlert("Warning", Main.getStringForIndex(CantUpgradeTXT) + ':' + s_url,null, AlertType.WARNING, 1500, true);
                        }
                    }
                }
                ;
                break;
                //#endif
                case COMMAND_OptionsCMD:
                {
                    MenuBlock.MB_initScreen(SCR_OptionsSCR, true);
                }
                ;
                break;
                case COMMAND_Back2CMD:
                {
                    i_ScreenStackPointer--;
                    if (i_ScreenStackPointer < 0) i_ScreenStackPointer = 0;
                    MenuBlock.MB_clearScreenStack();
                    i_CurrentSelectedItem = ash_ScreensSelectedItemsStack[i_ScreenStackPointer];
                    if (i_ScreenStackPointer == 0)
                    {
                        p_catalog.setCurrentScreenOffset(SMSCatalog.OFFSET_MAINSCREEN);
                        MenuBlock.MB_initScreen(SCR_MainPageCR, true);
                    }
                    else
                    {
                        int i_initscr = ash_ScreensStack[i_ScreenStackPointer];
                        p_catalog.setCurrentScreenOffset(i_initscr);
                        MenuBlock.MB_initScreen(SCR_OtherPageSCR, true);
                    }
                }
                ;
                break;
                case COMMAND_HelpCMD:
                {
                    MenuBlock.MB_initScreen(SCR_HelpSCR, true);
                }
                ;
                break;
                case COMMAND_SupportCMD:
                {
                    MenuBlock.MB_initScreen(SCR_SupportSCR, true);
                }
                ;
                break;
                case COMMAND_AboutCMD:
                {
                    MenuBlock.MB_initScreen(SCR_AboutSCR, true);
                }
                ;
                break;
                default:
                {
                }
            }
        }
        ab_LocalBytes = null;
    }

    protected synchronized static Displayable customScreen(int _screenId)
    {
        //#if SMSSERVICEPARSER
        ap_SMSServiceTextFields = null;
        //#endif

        boolean lg_AsTranslit = Main.p_languageBlock.i_CurrentLanguageIndex == 0;
        switch (_screenId)
        {
            //#if SEND2FRIEND
            case SCR_Send2FriendSCR:
            {
                String s_contentID = p_catalog.getItemNameForIndex(i_SelectedContentOffset, lg_AsTranslit);

                p_LocalForm = new Form(s_contentID);
                String s_str = Main.getStringForIndex(Send2FriendMsgTXT);
                StringItem p_strItm = new StringItem(null, s_str);
                Coordinator.algnItem(p_strItm, Coordinator.LAYOUT_NEWLINE_AFTER | Coordinator.LAYOUT_NEWLINE_BEFORE);

                //#-
                TextField p_phone = new TextField(Main.getStringForIndex(PhoneNumberTXT), "+7", 16, TextField.PHONENUMBER);
                //#+
                //$TextField p_phone = new TextField(Main.getStringForIndex(PhoneNumberTXT), "/*$COUNTRYCODE$*/", 16, TextField.PHONENUMBER);

                Coordinator.algnItem(p_phone, Coordinator.LAYOUT_NEWLINE_AFTER | Coordinator.LAYOUT_NEWLINE_BEFORE);
                TextField p_message = new TextField(Main.getStringForIndex(Send2FriendMessageFieldCaptionTXT), "", 32, TextField.ANY);
                Coordinator.algnItem(p_message, Coordinator.LAYOUT_NEWLINE_AFTER | Coordinator.LAYOUT_NEWLINE_BEFORE);
                p_LocalForm.append(p_strItm);
                p_LocalForm.append(p_phone);
                p_LocalForm.append(p_message);
            }
            ;
            break;
            //#endif
            case SCR_SupportSCR:
            {
                p_LocalForm = new Form(Main.getStringForIndex(SupportTitleTXT));
                StringItem p_itm = new StringItem(null, Main.getStringForIndex(SupportTextTXT));
                Coordinator.algnItem(p_itm, Coordinator.LAYOUT_LEFT | Coordinator.LAYOUT_NEWLINE_AFTER);
                p_LocalForm.append(p_itm);
                if (p_catalog.i_SupportPhones != 0xFFFF)
                {
                    String s_phones = p_catalog.getSupportPhones();
                    p_itm = new StringItem(lg_AsTranslit ? SMSCatalog.russian2translit("Телефон") : "Телефон", s_phones);
                    Coordinator.algnItem(p_itm, Coordinator.LAYOUT_LEFT | Coordinator.LAYOUT_NEWLINE_AFTER | Coordinator.LAYOUT_NEWLINE_BEFORE);
                    p_LocalForm.append(p_itm);
                }
                if (p_catalog.i_SupportEmail != 0xFFFF)
                {
                    String s_email = p_catalog.getSupportEmail();
                    p_itm = new StringItem("E-mail", s_email);
                    Coordinator.algnItem(p_itm, Coordinator.LAYOUT_LEFT | Coordinator.LAYOUT_NEWLINE_AFTER | Coordinator.LAYOUT_NEWLINE_BEFORE);
                    p_LocalForm.append(p_itm);
                }
                p_itm = null;
            }
            ;
            break;
            case SCR_MainPageCR:
            {
                List p_lst = p_catalog.makeScreenForOffset(SMSCatalog.OFFSET_MAINSCREEN, lg_AsTranslit);
                ash_ScreensStack[0] = SMSCatalog.OFFSET_MAINSCREEN;
                i_ScreenStackPointer = 0;

                p_lst.setSelectedIndex(i_CurrentSelectedItem, true);
                return p_lst;
            }
            case SCR_OtherPageSCR:
            {
                List p_lst = p_catalog.makeScreenForOffset(p_catalog.getCurrentScreenOffset(), lg_AsTranslit);
                p_lst.setSelectedIndex(i_CurrentSelectedItem, true);
                return p_lst;
            }
            //#if AGREEMENT
            case SCR_AgreementSCR:
            {
                String s_str = "Соглашение";
                if(lg_AsTranslit) s_str = SMSCatalog.russian2translit(s_str);
                p_LocalForm = new Form(s_str);
                s_str = p_catalog.getStringForIndex(p_catalog.i_AgreementText);
                if(lg_AsTranslit) s_str = SMSCatalog.russian2translit(s_str);
                StringItem p_text = new StringItem(null,s_str);
                p_LocalForm.append(p_text);
            }
            ;
            break;
            //#endif
            //#if CONFIRMATION
            case SCR_ConfirmationSCR:
            {
                p_LocalForm = new Form(Main.getStringForIndex(ConfirmationTitleTXT));
                Ticker p_ticker = new Ticker(Main.getStringForIndex(SMSWillBeSendTXT));
                p_LocalForm.setTicker(p_ticker);
                StringItem p_text = new StringItem(null, Main.getStringForIndex(OrderConfirmTXT) + " \'" + p_catalog.getItemNameForIndex(i_SelectedContentOffset, lg_AsTranslit) + '\'');
                p_LocalForm.append(p_text);
            }
            ;
            break;
            //#endif
            case SCR_ResourceInfoSCR:
            {
                Main.changeVideoMode(Main.VIDEOMODE_CANVAS);

                int i_type = p_catalog.getItemTypeForIndex(i_CurrentSelectedItem);

                String s_contentID = p_catalog.getItemNameForIndex(i_SelectedContentOffset, lg_AsTranslit);
                //System.out.println("s_contentID "+s_contentID);

                String s_resID = p_catalog.getItemResourceIDForIndex(i_SelectedContentOffset);
                //System.out.println("s_resID "+s_resID );

                String s_phoneSMS = p_catalog.getItemSMSnumForIndex(i_SelectedContentOffset);
                //System.out.println("s_phoneSMS "+s_phoneSMS );

                String s_contentReference = p_catalog.getItemReferenceForIndex(i_SelectedContentOffset, lg_AsTranslit);
                //System.out.println("s_contentReference "+s_contentReference );

                String s_contentCost = p_catalog.getItemCostForIndex(i_SelectedContentOffset, lg_AsTranslit);
                //System.out.println("s_contentCost "+s_contentCost );

                String s_compatibleSet = null;
                if (i_type!=SMSCatalog.CONTENTTYPE_INFO ) s_compatibleSet = p_catalog.getItemCompatibleModels(i_SelectedContentOffset);

                int i_previewURL = p_catalog.getItemPrevURLForIndex(i_SelectedContentOffset);

                Item p_previewImageItem = null;

                if (i_previewURL != 0xFFFF)
                {
                    String s_error = null;

                    // грузим изображение в PNG формате
                    try
                    {
                        Image p_image = null;

                        //#if RSRC_FILE
                        if ((i_previewURL & 0x8000) != 0)
                        {
                            ab_LocalBytes = p_catalog.getResourceForIndex(i_previewURL & 0x7FFF);
                            p_image = Image.createImage(ab_LocalBytes , 0, ab_LocalBytes.length);
                        }
                        else
                        //#endif
                        {
                            //#if RSRC_HTTP || RSRC_JAR

                            //#if RSRC_HTTP
                            String s_previewURL = p_catalog.getStringForIndex(i_previewURL - 1);
                            if (s_previewURL.startsWith("http://"))
                            {
                                // Загрузка через вап
                                ab_LocalBytes = HTTPInterface.processGETRequestToServer(s_previewURL);
                                p_image = Image.createImage(ab_LocalBytes, 0, ab_LocalBytes.length);
                            }
                            //#endif

                            //#if RSRC_JAR
                            if (s_previewURL.startsWith("jar://"))
                            {
                                // Загрузка из ресурса JAR
                                s_previewURL = s_previewURL.substring(6);
                                if (!s_previewURL.startsWith("/")) s_previewURL = '/' + s_previewURL;
                                p_image = Image.createImage(s_previewURL);
                            }
                            //#endif

                            //#endif
                        }

                        if (p_image != null)
                        {
                            p_previewImageItem = new ImageItem(null, p_image, ImageItem.LAYOUT_CENTER, null);
                        }
                    }
                    catch (Throwable _ex)
                    {
                        s_error = _ex.getMessage();
                        if (s_error == null || s_error.length() == 0) s_error = _ex.getClass().getName();
                    }
                    finally
                    {
                        if (s_error != null)
                        {
                            MenuBlock.MB_viewAlert("Warning", "Can't load preview image", null,AlertType.WARNING, 1500, true);

                            p_previewImageItem = null;
                        }
                    }
                }

                ab_LocalBytes = null;

                boolean lg_midp20 = Coordinator.isMIDP20();

                p_LocalForm = new Form(s_contentID);
                StringItem p_text = null;
                StringItem p_cost = null;

                StringItem p_operation = null;
                StringItem p_compatible = null;

                if (i_type!=SMSCatalog.CONTENTTYPE_INFO  && s_compatibleSet != null)
                {
                    p_compatible = new StringItem(Main.getStringForIndex(SupportedModelsTXT), s_compatibleSet);
                    if (lg_midp20)
                    {
                        Coordinator.algnItem(p_compatible, Coordinator.LAYOUT_LEFT | Coordinator.LAYOUT_NEWLINE_AFTER);
                    }
                }


                //#if SMSENGINE
                if (i_type!=SMSCatalog.CONTENTTYPE_INFO && i_type!=SMSCatalog.CONTENTTYPE_SMSSERVICE  && s_resID != null && s_phoneSMS!=null)
                {
                    String s_str = null;
                    if (Coordinator.doesSupportSMS()!=Coordinator.TYPE_NONE)
                    {
                        s_str = Main.getStringForIndex(OperationSMSReferenceTXT);
                    }
                    else
                    {
                        s_str = Main.getStringForIndex(HandOperationSMSReferenceTXT);
                    }

                    s_str = macroChange(s_str,"%res%",s_resID);
                    s_str = macroChange(s_str,"%num%",s_phoneSMS);


                    if (s_contentReference!=null)
                    {
                        s_contentReference = macroChange(s_contentReference,"%res%",s_resID);
                        s_contentReference = macroChange(s_contentReference,"%num%",s_phoneSMS);
                    }

                    p_operation = new StringItem(null, s_str);
                }
                //#endif

                if (s_contentReference!=null) p_text = new StringItem(null, s_contentReference);
                if (s_contentCost!=null) p_cost = new StringItem(null, Main.getStringForIndex(CostTXT) + s_contentCost);

                if (lg_midp20)
                {
                    if (p_text!=null) Coordinator.algnItem(p_text, Coordinator.LAYOUT_LEFT | Coordinator.LAYOUT_NEWLINE_AFTER);
                    if (p_cost!=null) Coordinator.algnItem(p_cost, Coordinator.LAYOUT_LEFT | Coordinator.LAYOUT_NEWLINE_AFTER);
                }

                if (lg_midp20 && p_operation != null)
                {
                    Coordinator.algnItem(p_operation, Coordinator.LAYOUT_LEFT | Coordinator.LAYOUT_NEWLINE_AFTER);
                }

                if (p_previewImageItem != null)
                {
                    if (lg_midp20)
                    {
                        Coordinator.algnItem(p_previewImageItem, Coordinator.LAYOUT_CENTER | Coordinator.LAYOUT_NEWLINE_AFTER);
                    }
                    p_LocalForm.append(p_previewImageItem);
                }

                if (p_text!=null) p_LocalForm.append(p_text);
                if (i_type!=SMSCatalog.CONTENTTYPE_INFO && p_cost != null) p_LocalForm.append(p_cost);

                //#if SMSENGINE
                if (i_type!=SMSCatalog.CONTENTTYPE_INFO && i_type!=SMSCatalog.CONTENTTYPE_SMSSERVICE && p_operation != null) p_LocalForm.append(p_operation);
                //#endif

                if (p_compatible != null) p_LocalForm.append(p_compatible);

                //#if SMSENGINE
                if (i_type!=SMSCatalog.CONTENTTYPE_INFO && i_type!=SMSCatalog.CONTENTTYPE_SMSSERVICE && s_resID != null && s_phoneSMS != null)
                {
                    Ticker p_ticker = new Ticker(s_resID + Main.getStringForIndex(NaTXT) + s_phoneSMS);
                    p_LocalForm.setTicker(p_ticker);
                }
                //#endif

                //#if SMSSERVICEPARSER
                if (i_type==SMSCatalog.CONTENTTYPE_SMSSERVICE && s_resID!=null && s_phoneSMS!=null)
                {
                    ap_SMSServiceTextFields = makeTextFieldsArrayForSMSService(s_resID);
                    if (ap_SMSServiceTextFields !=null)
                    {
                        for(int li=0;li<ap_SMSServiceTextFields.length;li++)
                        {
                            TextField p_field = ap_SMSServiceTextFields[li];
                            Coordinator.algnItem(p_field, Coordinator.LAYOUT_CENTER | Coordinator.LAYOUT_NEWLINE_AFTER);
                            p_LocalForm.append(p_field);
                        }
                    }
                }
                //#endif
                Main.changeVideoMode(Main.VIDEOMODE_MENU);
            }
            ;
            break;
        }
        Displayable p_frm = p_LocalForm;
        p_LocalForm = null;
        return p_frm;
    }


    //#if SMSSERVICEPARSER
    /**
     * генерируем список полей для SMS сервиса
     * @param _serviceId  строка с идентификатором сервиса, содержащая или нет поля
     * @return массив объектов полей или null если нет полей ввода
     */
    private static TextField [] makeTextFieldsArrayForSMSService(String _serviceId)
    {
        char [] ach_arr = _serviceId.toCharArray();
        int i_len = ach_arr.length;
        int i_tokensNumber = 0;
        for(int li=0;li<i_len;li++)
        {
            if (ach_arr[li]=='%') i_tokensNumber++;
        }
        if (i_tokensNumber==0) return null;
        TextField [] ap_txtField = new TextField[i_tokensNumber];
        boolean lg_AsTranslit = Main.p_languageBlock.i_CurrentLanguageIndex == 0;

        StringBuffer p_token = null;
        String s_token = null;
        int i_index = 0;
        for(int li=0;li<i_len;li++)
        {
            char ch_char = ach_arr[li];
            boolean lg_tobuffer = p_token!=null;
            switch (ch_char)
            {
                case '%' :
                {
                    if (lg_tobuffer)
                    {
                        s_token = p_token.toString();
                        p_token = null;
                    }
                    p_token = new StringBuffer();
                };break;
                default:
                {
                    if (lg_tobuffer)
                    {
                        p_token.append(ch_char);
                    }
                }
            }
            if (s_token!=null)
            {
                if (lg_AsTranslit) s_token = SMSCatalog.russian2translit(s_token);
                ap_txtField[i_index++] = new TextField(s_token,"",32,TextField.ANY);
                s_token = null;
            }
        }
        if (p_token!=null)
        {
            s_token = p_token.toString();
            p_token = null;
            if (lg_AsTranslit) s_token = SMSCatalog.russian2translit(s_token);
            ap_txtField[i_index++] = new TextField(s_token,"",32,TextField.ANY);
            s_token = null;
        }

        return ap_txtField;
    }

    private static TextField [] ap_SMSServiceTextFields;

    private static String makeSMSServiceQueryFromTextFields(String _smsServiceID,TextField [] _fieldsArray)
    {
        if (_fieldsArray==null) return _smsServiceID;
        StringBuffer p_outStrBuff = new StringBuffer(_smsServiceID.length());
        char [] ach_string = _smsServiceID.toCharArray();
        int i_index = 0;
        boolean lg_inTag = false;
        for(int li=0;li<_smsServiceID.length();li++)
        {
            char ch_char = ach_string[li];
            switch(ch_char)
            {
                case '%' :
                {
                    if (lg_inTag)
                    {
                        TextField p_txt = _fieldsArray[i_index];
                        i_index++;
                        p_outStrBuff.append(p_txt.getString().trim());
                    }
                    else
                    {
                        TextField p_txt = _fieldsArray[i_index];
                        i_index++;
                        String s_str = p_txt.getString().trim();
                        if (s_str.length()==0) return null;
                        p_outStrBuff.append(s_str);
                        lg_inTag = true;
                    }
                };break;
                default:
                {
                    if (!lg_inTag) p_outStrBuff.append(ch_char);
                }
            }
        }
        return p_outStrBuff.toString();
    }
    //#endif

    protected synchronized static void processListItem(int _screenId, int _itemId)
    {

        switch (_screenId)
        {
            case SCR_MainPageCR:
            case SCR_OtherPageSCR:
            {
                if (p_catalog.isCurrentScreenLinksScreen())
                {
                    ash_ScreensSelectedItemsStack[i_ScreenStackPointer] = (short) _itemId;
                    ash_ScreensStack[i_ScreenStackPointer] = (short) p_catalog.getCurrentScreenOffset();
                    i_ScreenStackPointer++;

                    // Выбран линк
                    i_CurrentSelectedItem = 0;
                    int i_newScreenOffset = p_catalog.getScreenOffsetForIndexedLink(_itemId);
                    p_catalog.setCurrentScreenOffset(i_newScreenOffset);
                    MenuBlock.MB_initScreen(SCR_OtherPageSCR, true);
                }
                else
                {
                    i_CurrentSelectedItem = _itemId;
                    // Выбран контент
                    // Осуществляем переход на экран информации
                    i_SelectedContentOffset = _itemId;
                    MenuBlock.MB_initScreen(SCR_ResourceInfoSCR, true);
                }
            }
            ;
            break;
        }
    }

    protected static boolean enableMenuItem(int _screenId, int _itemId)
    {
        return false;
    }

    protected static boolean enableMenuCommand(int _screenId, int _commandId)
    {
        switch (_screenId)
        {
            case SCR_MainPageCR:
            {
                switch (_commandId)
                {
                    case COMMAND_SupportCMD:
                        return p_catalog.i_SupportEmail != 0xFFFF || p_catalog.i_SupportPhones != 0xFFFF;
                    case COMMAND_GameCMD:
                        //#if GAMEMODE!=""
                        return Game.hasGame();
                        //#else
                        //$return false;
                        //#endif
                    case COMMAND_BackCMD:
                        return false;
                    //#if UPDATEURL
                    case COMMAND_UpgradeCMD:
                    {
                        return (p_catalog.getURL() != null && Coordinator.isMIDP20());
                    }
                    //#endif
                }
            }
            ;
            break;
            case SCR_ResourceInfoSCR:
            {
                //#if SMSENGINE
                boolean lg_supportSMS = Coordinator.doesSupportSMS()!=Coordinator.TYPE_NONE;
                switch (_commandId)
                {
                    case COMMAND_OrderCMD:
                    {
                        if (!lg_supportSMS) return false;
                        String s_num = p_catalog.getItemSMSnumForIndex(i_CurrentSelectedItem);
                        int i_type = p_catalog.getItemTypeForIndex(i_CurrentSelectedItem);
                        if (i_type==SMSCatalog.CONTENTTYPE_SMSSERVICE || i_type==SMSCatalog.CONTENTTYPE_INFO) return false;
                        return s_num!=null;
                    }
                    case COMMAND_SendCMD:
                    {
                        int i_type = p_catalog.getItemTypeForIndex(i_CurrentSelectedItem);
                        if (i_type!=SMSCatalog.CONTENTTYPE_SMSSERVICE) return false;
                        if (!lg_supportSMS) return false;
                        String s_num = p_catalog.getItemSMSnumForIndex(i_CurrentSelectedItem);
                        return s_num!=null;
                    }
                    case COMMAND_Send2FriendCMD :
                    {
                        //#if SEND2FRIEND
                        int i_type = p_catalog.getItemTypeForIndex(i_CurrentSelectedItem);
                        if (i_type==SMSCatalog.CONTENTTYPE_SMSSERVICE || i_type==SMSCatalog.CONTENTTYPE_INFO) return false;
                        String s_resid = p_catalog.getItemResourceIDForIndex(i_CurrentSelectedItem);
                        if (s_resid == null) return false;

                        return Coordinator.enableSMSServices();
                        //#else
                        //$return false;
                        //#endif
                    }
                }
                //#endif
            }
            ;
            break;
        }
        return false;
    }

    public static String macroChange(String _string,String _macrosName,String _macrosValue)
    {
        //#-
        System.out.println("STR = "+_string+" MACROS="+_macrosName+" VALUE="+_macrosValue);
        //#+
        int i_macroLen = _macrosName.length();
        while(true)
        {
            int i_substr = _string.indexOf(_macrosName);
            if (i_substr<0) break;
            _string = _string.substring(0,i_substr)+_macrosValue+(i_substr+i_macroLen<_string.length() ? _string.substring(i_substr+i_macroLen):"");
        }
        return _string;
    }

    //#if _MENU_ITEM_CUSTOM
    protected static Object customMenuItem(int _screenId, int _itemId, boolean _getImage)
    {
        return null;
    }
    //#endif

    //-----Other---------
    protected static final String getID()
    {
        return "SMSCAT2";
    }

    protected static final int getDataBlockSize()
    {
        return 256;
    }

    protected static final int Send2FriendMsgTXT = 0;
    protected static final int AboutCmdTXT = 1;
    protected static final int SMSWillBeSendTXT = 2;
    protected static final int ResourceInfoTitleTXT = 3;
    protected static final int OffTXT = 4;
    protected static final int RMSErrorTXT = 5;
    protected static final int HandOperationSMSReferenceTXT = 6;
    protected static final int CostTXT = 7;
    protected static final int HelpCmdTXT = 8;
    protected static final int OrderCmdTXT = 9;
    protected static final int OperationSMSReferenceTXT = 10;
    protected static final int AboutTextTXT = 11;
    protected static final int YesCmdTXT = 12;
    protected static final int OptionsCmdTXT = 13;
    protected static final int ValidityCaptionTXT = 14;
    protected static final int SupportTextTXT = 15;
    protected static final int SMSNoSentTextTXT = 16;
    protected static final int BackCmdTXT = 17;
    protected static final int RenewSuccessTXT = 18;
    protected static final int SMSSentTextTXT = 19;
    protected static final int NaTXT = 20;
    protected static final int OnTXT = 21;
    protected static final int PhoneNumberTXT = 22;
    protected static final int NoCmdTXT = 23;
    protected static final int ExitTXT = 24;
    protected static final int BackLightTXT = 25;
    protected static final int ErrorFieldFillingTXT = 26;
    protected static final int Send2FriendMessageFieldCaptionTXT = 27;
    protected static final int LanguageSelectTXT = 28;
    protected static final int SupportTitleTXT = 29;
    protected static final int ConfirmationTitleTXT = 30;
    protected static final int ValidityTextTXT = 31;
    protected static final int CantUpgradeTXT = 32;
    protected static final int SaveRMSErrorTXT = 33;
    protected static final int ConfirmOrderTXT = 34;
    protected static final int RenewTitleTXT = 35;
    protected static final int HelpTextTXT = 36;
    protected static final int SupportTXT = 37;
    protected static final int SMSNoSentTitleTXT = 38;
    protected static final int ErrorTXT = 39;
    protected static final int GameTXT = 40;
    protected static final int UpgradeCmdTXT = 41;
    protected static final int SMSSentTitleTXT = 42;
    protected static final int MainMenuTitleTXT = 43;
    protected static final int ExitCmdTXT = 44;
    protected static final int SupportedModelsTXT = 45;
    protected static final int WaitPleaseTXT = 46;
    protected static final int OpenCmdTXT = 47;
    protected static final int Send2FriendCmdTXT = 48;
    protected static final int SendCmdTXT = 49;
    protected static final int OrderConfirmTXT = 50;

//#global _MENU_ITEM_CUSTOM=false
//#global _MENU_ITEM_DELIMITER=false
//#global _MENU_ITEM_IMAGE=false
//#global _MENU_ITEM_MENUITEM=true
//#global _MENU_ITEM_TEXTBOX=true

// Screens

// Screen OptionsSCR
    protected static final int SCR_OptionsSCR = 0;
// Screen AboutSCR
    protected static final int SCR_AboutSCR = 18;
// Screen AgreementSCR
    protected static final int SCR_AgreementSCR = 31;
// Screen ConfirmationSCR
    protected static final int SCR_ConfirmationSCR = 41;
// Screen Send2FriendSCR
    protected static final int SCR_Send2FriendSCR = 51;
// Screen LanguageSelectSCR
    protected static final int SCR_LanguageSelectSCR = 61;
// Screen MainPageCR
    protected static final int SCR_MainPageCR = 69;
// Screen HelpSCR
    protected static final int SCR_HelpSCR = 89;
// Screen ResourceInfoSCR
    protected static final int SCR_ResourceInfoSCR = 102;
// Screen SupportSCR
    protected static final int SCR_SupportSCR = 116;
// Screen OtherPageSCR
    protected static final int SCR_OtherPageSCR = 124;
// Screen OnOffSCR
    protected static final int SCR_OnOffSCR = 132;

// Items

// Item HelpTextITM
    protected static final int ITEM_HelpTextITM = 0;
// Item LanguageSelectITM
    protected static final int ITEM_LanguageSelectITM = 1;
// Item BackLightITM
    protected static final int ITEM_BackLightITM = 2;
// Item AboutTextITM
    protected static final int ITEM_AboutTextITM = 3;

// Commands

// Command GameCMD
    protected static final int COMMAND_GameCMD = 140;
// Command UpgradeCMD
    protected static final int COMMAND_UpgradeCMD = 141;
// Command BackCMD
    protected static final int COMMAND_BackCMD = 142;
// Command AboutCMD
    protected static final int COMMAND_AboutCMD = 143;
// Command OrderCMD
    protected static final int COMMAND_OrderCMD = 144;
// Command HelpCMD
    protected static final int COMMAND_HelpCMD = 145;
// Command OptionsCMD
    protected static final int COMMAND_OptionsCMD = 146;
// Command Send2FriendCMD
    protected static final int COMMAND_Send2FriendCMD = 147;
// Command SupportCMD
    protected static final int COMMAND_SupportCMD = 148;
// Command SendCMD
    protected static final int COMMAND_SendCMD = 149;
// Command Back2CMD
    protected static final int COMMAND_Back2CMD = 150;
// Command NoCMD
    protected static final int COMMAND_NoCMD = 151;
// Command YesCMD
    protected static final int COMMAND_YesCMD = 152;
// Command ExitCMD
    protected static final int COMMAND_ExitCMD = 153;

}
