
import javax.microedition.lcdui.Alert;
import javax.microedition.lcdui.AlertType;
import javax.microedition.lcdui.Font;
import javax.microedition.lcdui.Image;

public class LoadingThread implements Runnable
{
    private static startup p_startup;
    public static boolean lg_isCompleted;
    public static String s_ErrorCode;

    private static final String RESOURCE_LANGUAGES = "/langs.bin";
    private static final String RESOURCE_MENU = "/gmenu.bin";

    private static Thread p_loadingThread;

    private static boolean lg_midletLoading = true;

    private static int i_StageNumber = 0;

    private static LoadingThread p_This;

    public LoadingThread(startup _class)
    {
        p_startup = _class;
        p_This = this;
    }

    public static final void go()
    {
        p_loadingThread = new Thread(p_This);
        lg_midletLoading = true;
        lg_isCompleted = false;
        s_ErrorCode = null;
        p_loadingThread.start();
    }

    public static final void loadStageData(int _stage)
    {
        p_loadingThread = new Thread(p_This);
        lg_midletLoading = false;
        i_StageNumber = _stage;
        lg_isCompleted = false;
        s_ErrorCode = null;
        p_loadingThread.start();
    }

    public void run()
    {
        Class p_This = p_startup.getClass();

        //#if SHOWSYS
        int i_errPosition = 0;
        //#endif

        try
        {
            if (lg_midletLoading)
            {
                //#if SHOWSYS
                System.out.println("Init RMS");
                //#endif
                boolean lg_firstStart = false;

                switch (DataStorage.init(Gamelet.getID()))
                {
                    case DataStorage.STORE_FIRSTSTART:
                        {
                            startup.i_LanguageIndex = -1;
                            startup.lg_Option_Light = true;
                            startup.lg_Option_Sound = true;
                            startup.lg_Option_Vibra = true;
                            lg_firstStart = true;
                        }
                ;
                        break;
                    case DataStorage.STORE_INITED:
                        {
                            startup.i_LanguageIndex = DataStorage.ab_OptionsArray[0] & 0xFF;
                            int i_options = DataStorage.ab_OptionsArray[1];
                            startup.lg_Option_Light = (i_options & 0x1) != 0;
                            startup.lg_Option_Sound = (i_options & 0x2) != 0;
                            startup.lg_Option_Vibra = (i_options & 0x4) != 0;
                        }
                ;
                        break;
                    case DataStorage.STORE_NOMEMORY:
                        {
                            Alert p_alert = new Alert("RMS error", "No memory for data\r\n" + DataStorage.s_Status, null, AlertType.ERROR);
                            p_alert.setTimeout(3000);
                            startup.p_Display.setCurrent(p_alert, startup.p_InsideCanvas);
                            try
                            {
                                Thread.sleep(4000);
                            }
                            catch (InterruptedException e)
                            {
                            }
                            p_alert = null;
                        }
                ;
                        break;
                }
                Runtime.getRuntime().gc();

                startup.increaseLoadingProgress(25);

                //#if SHOWSYS
                System.out.println("Init menu and language blocks");
                i_errPosition++;
                //#endif

                //#if VENDOR=="SAMSUNG"
                //$Image p_fntImg = Image.createImage("/gf.png");
                //$startup.p_bfImg = Image.createImage("/bf.png");
                //$startup.i_LanguageIndex = LangBlock.initLanguageBlock(p_This, p_fntImg, p_fntImg.getWidth()>>>4, p_fntImg.getHeight()/9, RESOURCE_LANGUAGES, startup.i_LanguageIndex);
                //$p_fntImg = null;
                //#else
                startup.i_LanguageIndex = LangBlock.initLanguageBlock(p_This, null, 1, 1, RESOURCE_LANGUAGES, startup.i_LanguageIndex);
                //#endif

                //#if VENDOR=="MOTOROLA" && MODEL=="E398"
                //$Font p_menuFont = Font.getFont(Font.FACE_PROPORTIONAL, Font.STYLE_BOLD, Font.SIZE_MEDIUM);
                //#else
                //#if VENDOR=="SUN" || VENDOR=="MOTOROLA" || VENDOR=="SE"
                Font p_menuFont = Font.getFont(Font.FACE_PROPORTIONAL, Font.STYLE_PLAIN, Font.SIZE_MEDIUM);
                //#endif
                //#if VENDOR=="SIEMENS"
                //$Font p_menuFont = Font.getFont(Font.FACE_PROPORTIONAL, Font.STYLE_PLAIN, Font.SIZE_SMALL);
                //#endif
                //#if VENDOR=="LG"
                //$Font p_menuFont = Font.getFont(Font.FACE_PROPORTIONAL, Font.STYLE_PLAIN, Font.SIZE_MEDIUM);
                //#endif
                //#if VENDOR=="SAMSUNG"
                //$Font p_menuFont = null;
                //#endif

                //#if VENDOR=="NOKIA"
                //#if MODEL=="6100"
                //$Font p_menuFont = Font.getFont(Font.FACE_PROPORTIONAL, Font.STYLE_PLAIN, Font.SIZE_MEDIUM);
                //#endif
                //#if MODEL=="7650" || MODEL=="7610"
                //$Font p_menuFont = Font.getFont(Font.FACE_PROPORTIONAL, Font.STYLE_BOLD, Font.SIZE_MEDIUM);
                //#endif
                //#if MODEL=="3410" || MODEL=="3510"
                //$Font p_menuFont = Font.getFont(Font.FACE_PROPORTIONAL, Font.STYLE_PLAIN, Font.SIZE_SMALL);
                //#endif
                //#endif
                //#endif

                //#if SHOWSYS
                i_errPosition++;
                //#endif

                GameMenu.initMenuBlock(p_This, RESOURCE_MENU, p_menuFont);
                Runtime.getRuntime().gc();

                //#if SHOWSYS
                i_errPosition++;
                //#endif

                if (lg_firstStart)
                {
                    //#if SHOWSYS
                    System.out.println("Pack and save options for first start");
                    //#endif
                    startup.packAndSaveOptions();
                }
                Runtime.getRuntime().gc();

                startup.increaseLoadingProgress(25);

                //#if SHOWSYS
                System.out.println("Load game resources");
                i_errPosition++;
                //#endif

                startup.onLoadingGameResources(p_This);
                Runtime.getRuntime().gc();

                startup.increaseLoadingProgress(25);

                //#if SHOWSYS
                System.out.println("Init gamelet");
                i_errPosition++;
                //#endif
                if (!Gamelet.init(p_This))
                {
                    throw new Exception("Er7623");
                }

                startup.increaseLoadingProgress(25);
            }
            else
            {
                if (!Gamelet.initGameStage(i_StageNumber) || !startup.onInitNewGameStage(i_StageNumber)) s_ErrorCode = "Er981";
            }
        }
        catch (Exception _ex)
        {
            s_ErrorCode = _ex.getMessage();

            //#if SHOWSYS
            if (s_ErrorCode == null) s_ErrorCode = "[" + i_errPosition + "]";
            //#else
            //$if (s_ErrorCode == null) s_ErrorCode = "Unknown";
            //#endif
        }
        finally
        {
            lg_isCompleted = true;
            p_loadingThread = null;
        }
    }
}
