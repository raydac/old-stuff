import javax.microedition.midlet.MIDlet;
import javax.microedition.midlet.MIDletStateChangeException;
import javax.microedition.lcdui.*;

/**
 * Шаблон для изготовления игровых визуализированных модулей
 *
 * @author Igor A. Maznitsa
 *         (C) 2005 Raydac Research Group Ltd.
 * @version 5.1 (22.06.2005)
 */
public class startup extends MIDlet implements Runnable, CommandListener
{
    //#local MCF_SPLASH = false

    //==============================================================
    //#if VENDOR=="MOTOROLA"
    //#if MODEL=="C380"
    //$private static final int SCREEN_WIDTH = 128;
    //$private static final int SCREEN_HEIGHT = 116;
    //#endif
    //#if MODEL=="E398"
    //$private static final int SCREEN_WIDTH = 176;
    //$private static final int SCREEN_HEIGHT = 204;
    //#endif
    //#endif
    //#if VENDOR=="NOKIA"
    //#if MODEL=="6100"
    //$private static final int SCREEN_WIDTH = 128;
    //$private static final int SCREEN_HEIGHT = 128;
    //#endif
    //#if (MODEL=="7650" || MODEL=="7610")
    //$private static final int SCREEN_WIDTH = 176;
    //$private static final int SCREEN_HEIGHT = 208;
    //#endif
    //#if MODEL=="3410"
    //$private static final int SCREEN_WIDTH = 101;
    //$private static final int SCREEN_HEIGHT = 64;
    //#endif
    //#if MODEL=="3510"
    //$private static final int SCREEN_WIDTH = 101;
    //$private static final int SCREEN_HEIGHT = 64;
    //#endif
    //#endif

    //#if VENDOR=="SIEMENS"
    //#if MODEL=="M55"
    //$private static final int SCREEN_WIDTH = 101;
    //$private static final int SCREEN_HEIGHT = 80;
    //#endif
    //#if MODEL=="S55"
    //$private static final int SCREEN_WIDTH = 101;
    //$private static final int SCREEN_HEIGHT = 80;
    //#endif
    //#if MODEL=="M50"
    //$private static final int SCREEN_WIDTH = 101;
    //$private static final int SCREEN_HEIGHT = 64;
    //#endif
    //#if MODEL=="CX65"
    //$private static final int SCREEN_WIDTH = 132;
    //$private static final int SCREEN_HEIGHT = 176;
    //#endif
    //#if MODEL=="C65"
    //$private static final int SCREEN_WIDTH = 128;
    //$private static final int SCREEN_HEIGHT = 128;
    //#endif
    //#if MODEL=="SX1"
    //$private static final int SCREEN_WIDTH = 176;
    //$private static final int SCREEN_HEIGHT = 220;
    //#endif
    //#endif
    //#if VENDOR=="SE"
    //#if MODEL=="T610"
    //$private static final int SCREEN_WIDTH = 128;
    //$private static final int SCREEN_HEIGHT = 127;
    //#endif
    //#endif
    //#if VENDOR=="SAMSUNG"
    //#if MODEL=="C100"
    //$private static final int SCREEN_WIDTH = 128;
    //$private static final int SCREEN_HEIGHT = 128;
    //#endif
    //#if MODEL=="X100"
    //$private static final int SCREEN_WIDTH = 128;
    //$private static final int SCREEN_HEIGHT = 128;
    //#endif
    //#if MODEL=="E700"
    //$private static final int SCREEN_WIDTH = 128;
    //$private static final int SCREEN_HEIGHT = 160;
    //#endif
    //#endif
    //#if VENDOR=="LG"
    //#if MODEL=="G1600"
    //$private static final int SCREEN_WIDTH = 128;
    //$private static final int SCREEN_HEIGHT = 128;
    //#endif
    //#endif

    //#-
    /**
     * Константа определяет размер физического экрана по ширенк в пикселях
     */
    private static final int SCREEN_WIDTH = 176;
    /**
     * Константа определяет размер физического экрана по высоте в пикселях
     */
    private static final int SCREEN_HEIGHT = 208;
    //#+


    /**
     * Константы GAMESCREEN_OFFSETX, GAMESCREEN_OFFSETY, GAMESCREEN_WIDTH, GAMESCREEN_HEIGHT определяют положение и размер игрового экрана на экране физическом
     * соответствую смещению относительно верхнего левого угла физического экрана по оси X, оси Y и ширине и высоте
     */

    //#if (VENDOR=="MOTOROLA" && MODEL=="E398")
    //$private static final int GAMESCREEN_OFFSETX = 0;
    //$private static final int GAMESCREEN_OFFSETY = 8;
    //$private static final int GAMESCREEN_WIDTH = 176;
    //$private static final int GAMESCREEN_HEIGHT = 188;
    //#else
        //#if (VENDOR=="NOKIA" && (MODEL=="7650" || MODEL=="7610"))
            private static final int GAMESCREEN_OFFSETX = 0;
            private static final int GAMESCREEN_OFFSETY = 10;
            private static final int GAMESCREEN_WIDTH = 176;
            private static final int GAMESCREEN_HEIGHT = 188;
    //#else
    //#if VENDOR=="SIEMENS" && MODEL=="CX65"
    //$private static final int GAMESCREEN_OFFSETX = 0;
    //$private static final int GAMESCREEN_OFFSETY = 10;
    //$private static final int GAMESCREEN_WIDTH = 132;
    //$private static final int GAMESCREEN_HEIGHT = 156;
    //#else
    //#if VENDOR=="SIEMENS" && MODEL=="C65"
    //$private static final int GAMESCREEN_OFFSETX = 0;
    //$private static final int GAMESCREEN_OFFSETY = 0;
    //$private static final int GAMESCREEN_WIDTH = 128;
    //$private static final int GAMESCREEN_HEIGHT = 128;
    //#else
    //#if VENDOR=="MOTOROLA" && MODEL=="C380"
    //$private static final int GAMESCREEN_OFFSETX = 0;
    //$private static final int GAMESCREEN_OFFSETY = -6;
    //$private static final int GAMESCREEN_WIDTH = SCREEN_WIDTH;
    //$private static final int GAMESCREEN_HEIGHT = 128;
    //#else

    //#if VENDOR=="NOKIA" && MODEL=="3510"
    //$private static final int GAMESCREEN_OFFSETX = -1;
    //$private static final int GAMESCREEN_OFFSETY = 0;
    //$private static final int GAMESCREEN_WIDTH = SCREEN_WIDTH;
    //$private static final int GAMESCREEN_HEIGHT = SCREEN_HEIGHT;
    //#else
    //$private static final int GAMESCREEN_OFFSETX = 0;
    //$private static final int GAMESCREEN_OFFSETY = 0;
    //$private static final int GAMESCREEN_WIDTH = SCREEN_WIDTH;
    //$private static final int GAMESCREEN_HEIGHT = SCREEN_HEIGHT;
    //#endif
    //#endif
    //#endif
    //#endif
    //#endif
    //#endif
    //==============================================================

    //#if SHOWSYS
    // Переменные для профилирования производительности
    private static int i_Profil_TimeNextGameStep;
    private static int i_Profil_TimePaint;
    public static String s_CurrentExceptionMessage = null;
    //#endif

    //========================GAME MENU CONSTANTS==========================
    //==================================================================================

    // Menu item offsets
    private static final int ITEM_NewGame_OFFSET = 3;
    private static final int ITEM_RestartLevel_OFFSET = 17;
    private static final int ITEM_ResumeGame_OFFSET = 22;
    private static final int ITEM_Help_OFFSET = 27;
    private static final int ITEM_Top_OFFSET = 32;
    private static final int ITEM_Options_OFFSET = 37;
    private static final int ITEM_Language_OFFSET = 51;
    private static final int ITEM_About_OFFSET = 56;
    private static final int ITEM_EndGame_OFFSET = 61;
    private static final int ITEM_Exit_OFFSET = 66;
    // Identifiers
    private static final int ITEM_ID_NewGame = 0;
    private static final int SUBITEM_ID_Easy = 1;
    private static final int SUBITEM_ID_Normal = 2;
    private static final int SUBITEM_ID_Hard = 3;
    private static final int ITEM_ID_RestartLevel = 4;
    private static final int ITEM_ID_ResumeGame = 5;
    private static final int ITEM_ID_Help = 6;
    private static final int ITEM_ID_Top = 7;
    private static final int ITEM_ID_Options = 8;
    private static final int SUBITEM_ID_Sound = 9;
    private static final int SUBITEM_ID_Vibra = 10;
    private static final int SUBITEM_ID_Light = 11;
    private static final int ITEM_ID_Language = 12;
    private static final int ITEM_ID_About = 13;
    private static final int ITEM_ID_EndGame = 14;
    private static final int ITEM_ID_Exit = 15;
    // Offset array
    private static final short[] MENU_OFFSETS = new short[]{(short) 3, (short) 17, (short) 22, (short) 27, (short) 32, (short) 37, (short) 51, (short) 56, (short) 61, (short) 66};

    private static final int NewGameTXT = 0;
    private static final int HelpTXT = 1;
    private static final int CancelTXT = 2;
    private static final int TopTXT = 3;
    private static final int NormalGameTXT = 4;
    private static final int EasyGameTXT = 5;
    private static final int AboutTextTXT = 6;
    private static final int OptionsTXT = 7;
    private static final int SaveTXT = 8;
    private static final int ScoreTXT = 9;
    private static final int ExitTXT = 10;
    private static final int LanguageTXT = 11;
    private static final int LightTXT = 12;
    private static final int VibraTXT = 13;
    private static final int BackTXT = 14;
    private static final int HelpTextTXT = 15;
    private static final int StageTXT = 16;
    private static final int RecordNameTXT = 17;
    private static final int HardGameTXT = 18;
    private static final int AboutTXT = 19;
    private static final int SoundTXT = 20;
    private static final int ResumeGameTXT = 21;
    private static final int RestartLevelTXT = 22;
    private static final int EndGameTXT = 23;

    /**
     * Состояние неинициализированного или деинициализированного приложения
     */
    private static final int MODE_UNKNOWN = 0;
    /**
     * Состояние инициализированного приложения
     */
    private static final int MODE_INITED = 1;
    /**
     * Состояние загрузки данных приложения
     */
    private static final int MODE_LOADING = 2;
    /**
     * Состояние отображения главного меню
     */
    private static final int MODE_MAINMENU = 3;
    /**
     * Состояние отображения номера игрового уровня
     */
    private static final int MODE_SHOWSTAGE = 4;
    /**
     * Состояние отображения игрового процесса
     */
    private static final int MODE_GAMEPLAY = 6;
    /**
     * Состояние отображения игрового меню
     */
    private static final int MODE_GAMEMENU = 7;
    /**
     * Состояние отображения финала игрового процесса
     */
    private static final int MODE_GAMEFINAL = 8;
    /**
     * Состояние отображения формы ввода имени игрока для записи в таблицу рекордов
     */
    private static final int MODE_RECORDNAME = 9;
    /**
     * Состояние выгрузки приложения
     */
    private static final int MODE_RELEASING = 10;
    /**
     * Состояние ошибки приложения
     */
    private static final int MODE_ERROR = 11;

    /**
     * Задержка на реакцию на клавиши при переводе в режим окончания игры
     */
    private static final int REACTIONDELAY_GAMEFINAL = 3;

    /**
     * Задержка на реакцию на клавиши при переводе в режим ввода рекорда
     */
    private static final int REACTIONDELAY_RECORD = 3;

    //====================Коды клавиш======================
    //#if VENDOR=="SUN"
    private static final int JOY_СODE_UP = -1;
    private static final int JOY_CODE_LEFT = -3;
    private static final int JOY_CODE_RIGHT = -4;
    private static final int JOY_CODE_DOWN = -2;
    private static final int JOY_CODE_FIRE = -5;

    private static final int KEY_CODE_UP = 50;//Canvas.KEY_NUM2
    private static final int KEY_CODE_LEFT = 52;//Canvas.KEY_NUM4;
    private static final int KEY_CODE_RIGHT = 54;//Canvas.KEY_NUM6;
    private static final int KEY_CODE_DOWN = 56;//Canvas.KEY_NUM8;
    private static final int KEY_CODE_FIRE = 53;//Canvas.KEY_NUM5;

    private static final int KEY_CODE_KEY1 = 55;//Canvas.KEY_NUM7;
    private static final int KEY_CODE_KEY2 = 57;//Canvas.KEY_NUM9;

    private static final int KEY_CODE_SOFT_LEFT = -6;// Левая софт кнопка
    private static final int KEY_CODE_SOFT_RIGHT = -7;// Правая софт кнопка
    //#endif
    //#if VENDOR=="MOTOROLA"
    //$private static final int JOY_СODE_UP = -1;
    //$private static final int JOY_CODE_LEFT = -2;
    //$private static final int JOY_CODE_RIGHT = -5;
    //$private static final int JOY_CODE_DOWN = -6;
    //$private static final int JOY_CODE_FIRE = -20;
    //$private static final int KEY_CODE_UP = 50;
    //$private static final int KEY_CODE_LEFT = 52;
    //$private static final int KEY_CODE_RIGHT = 54;
    //$private static final int KEY_CODE_DOWN = 56;
    //$private static final int KEY_CODE_FIRE = 53;
    //$private static final int KEY_CODE_KEY1 = 55;
    //$private static final int KEY_CODE_KEY2 = 57;
    //$private static final int KEY_CODE_SOFT_LEFT = -21;
    //$private static final int KEY_CODE_SOFT_RIGHT = -22;
    //#endif

    //#if VENDOR=="SIEMENS"
    //$private static final int JOY_СODE_UP = -59;
    //$private static final int JOY_CODE_LEFT = -61;
    //$private static final int JOY_CODE_RIGHT = -62;
    //$private static final int JOY_CODE_DOWN = -60;
    //$private static final int JOY_CODE_FIRE = -26;

    //$private static final int KEY_CODE_UP = 50;
    //$private static final int KEY_CODE_LEFT = 52;
    //$private static final int KEY_CODE_RIGHT = 54;
    //$private static final int KEY_CODE_DOWN = 56;
    //$private static final int KEY_CODE_FIRE = 53;
    //$private static final int KEY_CODE_KEY1 = 55;
    //$private static final int KEY_CODE_KEY2 = 57;
    //$private static final int KEY_CODE_SOFT_LEFT = -1;
    //$private static final int KEY_CODE_SOFT_RIGHT = -4;
    //#endif

    //#if VENDOR=="SE"
    //$private static final int JOY_СODE_UP = -1;
    //$private static final int JOY_CODE_LEFT = -3;
    //$private static final int JOY_CODE_RIGHT = -4;
    //$private static final int JOY_CODE_DOWN = -2;
    //$private static final int JOY_CODE_FIRE = -5;

    //$private static final int KEY_CODE_UP = 50;
    //$private static final int KEY_CODE_LEFT = 52;
    //$private static final int KEY_CODE_RIGHT = 54;
    //$private static final int KEY_CODE_DOWN = 56;
    //$private static final int KEY_CODE_FIRE = 53;
    //$private static final int KEY_CODE_KEY1 = 55;
    //$private static final int KEY_CODE_KEY2 = 57;
    //$private static final int KEY_CODE_SOFT_LEFT = 49;
    //$private static final int KEY_CODE_SOFT_RIGHT = 51;
    //#endif

    //#if VENDOR=="SAMSUNG"
    //$private static final int JOY_СODE_UP = -1;
    //$private static final int JOY_CODE_LEFT = -3;
    //$private static final int JOY_CODE_RIGHT = -4;
    //$private static final int JOY_CODE_DOWN = -2;
    //$private static final int JOY_CODE_FIRE = -5;

    //$private static final int KEY_CODE_UP = 50;
    //$private static final int KEY_CODE_LEFT = 52;
    //$private static final int KEY_CODE_RIGHT = 54;
    //$private static final int KEY_CODE_DOWN = 56;
    //$private static final int KEY_CODE_FIRE = 53;
    //$private static final int KEY_CODE_KEY1 = 55;
    //$private static final int KEY_CODE_KEY2 = 57;
    //$private static final int KEY_CODE_SOFT_LEFT = 49;
    //$private static final int KEY_CODE_SOFT_RIGHT = 51;
    //#endif

    //#if VENDOR=="LG"
    //$private static final int JOY_СODE_UP = -1;
    //$private static final int JOY_CODE_LEFT = -3;
    //$private static final int JOY_CODE_RIGHT = -4;
    //$private static final int JOY_CODE_DOWN = -2;
    //$private static final int JOY_CODE_FIRE = -5;

    //$private static final int KEY_CODE_UP = 50;
    //$private static final int KEY_CODE_LEFT = 52;
    //$private static final int KEY_CODE_RIGHT = 54;
    //$private static final int KEY_CODE_DOWN = 56;
    //$private static final int KEY_CODE_FIRE = 53;
    //$private static final int KEY_CODE_KEY1 = 55;
    //$private static final int KEY_CODE_KEY2 = 57;
    //$private static final int KEY_CODE_SOFT_LEFT = 49;
    //$private static final int KEY_CODE_SOFT_RIGHT = 51;
    //#endif

    //#if VENDOR=="NOKIA"
    //$private static final int JOY_СODE_UP = -1;
    //$private static final int JOY_CODE_LEFT = -3;
    //$private static final int JOY_CODE_RIGHT = -4;
    //$private static final int JOY_CODE_DOWN = -2;
    //$private static final int JOY_CODE_FIRE = -5;

    //$private static final int KEY_CODE_UP = 50;
    //$private static final int KEY_CODE_LEFT = 52;
    //$private static final int KEY_CODE_RIGHT = 54;
    //$private static final int KEY_CODE_DOWN = 56;
    //$private static final int KEY_CODE_FIRE = 53;
    //$private static final int KEY_CODE_KEY1 = 55;
    //$private static final int KEY_CODE_KEY2 = 57;
    //$private static final int KEY_CODE_SOFT_LEFT = -6;
    //$private static final int KEY_CODE_SOFT_RIGHT = -7;
    //#endif

    //======================================================

    private static final int COLOR_MAIN_BACKGROUND = 0x000000;

    //#if (VENDOR=="SIEMENS" && MODEL=="M50") || (VENDOR=="NOKIA" && MODEL=="3410")
    //$private static final int COLOR_LOADING_BACKGROUND = 0xFFFFFF;
    //$private static final int COLOR_LOADING_BAR_BACKGROUND = 0xFFFFFF;
    //$private static final int COLOR_LOADING_BAR = 0x000000;
    //$private static final int COLOR_BORDER = 0xFFFFFF;
    //$private static final int COLOR_RECORD_BACKGROUND = 0xFFFFFF;
    //$private static final int COLOR_RECORD_TEXT = 0x000000;
    //$private static final int COLOR_RECORD_CHAR = 0xFFFFFF;
    //$private static final int COLOR_RECORD_BCKGNDCHAR = 0x000000;
    //#else
    private static final int COLOR_LOADING_BACKGROUND = 0xFFFFFF;
    private static final int COLOR_LOADING_BAR_BACKGROUND = 0x32B5FA;
    private static final int COLOR_LOADING_BAR = 0x0000FF;

    private static final int COLOR_BORDER = 0x000055;

    /**
     * Задний фон панели рекордов
     */
    private static final int COLOR_RECORD_BACKGROUND = 0x008ED6;

    /**
     * Цвет текста панели рекордов
     */
    private static final int COLOR_RECORD_TEXT = 0xFFFFFF;

    /**
     * Цвет символа имени рекорда
     */
    private static final int COLOR_RECORD_CHAR = 0xFFE600;

    /**
     * Цвет заднего фона символа имени рекорда
     */
    private static final int COLOR_RECORD_BCKGNDCHAR = 0x6800BA;
    //#endif

    //#if (VENDOR=="SIEMENS" && MODEL=="M50") || (VENDOR=="NOKIA" && (MODEL=="3410" || MODEL=="3510"))
    //$private static final int LOADING_BAR_HEIGHT = 2;
    //#else
    private static final int LOADING_BAR_HEIGHT = 5;
    //#endif
    private static final int LOADING_BAR_WIDTH = 60;
    private static final int LOADING_BAR_OFFSET_FROM_LOGO = 3;
    private static final int I8_LOADING_BAR_PERCENT = (LOADING_BAR_WIDTH << 8) / 100;

    /**
     * Имя файла, содержащего картинку логотипа
     */
    private static final String RESOURCE_LOADING_LOGO = "/cc.png";

    //#if MCF_SPLASH
    //$private static final String RESOURCE_SPLASH = "/splash.mcf";
    //$private static final String RESOURCE_WINIMAGE = "/win.mcf";
    //$private static final String RESOURCE_LOSTIMAGE = "/lost.mcf";
    //#else

    //#if false
    //$    private static final String RESOURCE_SPLASH = "/splash.jpg";
    //$    private static final String RESOURCE_WINIMAGE = "/win.jpg";
    //$    private static final String RESOURCE_LOSTIMAGE = "/lost.jpg";
    //#else
    private static final String RESOURCE_SPLASH = "/splash.png";
    private static final String RESOURCE_WINIMAGE = "/win.png";
    private static final String RESOURCE_LOSTIMAGE = "/lost.png";
    //#endif
    //#endif

    /**
     * Ресурс, содержащий изображение логотипа диллера
     */
    private static final String RESOURCE_DEALER = "/dealer.png";

    /**
     * Конечно время показа номера уровня
     */
    private static long l_EndTimeForScreen;
    /**
     * Задержка на показ экрана с номером игрового уровня
     */
    private static final int DELAY_STAGESCREEN = 3000;
    /**
     * Задержка на показ финального экрана
     */
    private static final int DELAY_FINALSCREEN = 10000;

    /**
     * Код первого символа при вводе имени рекорда
     */
    private static final int LETTER_RECORDNAME_FIRSTCODE = 0x40;
    /**
     * Код последнего символа при вводе имени рекорда
     */
    private static final int LETTER_RECORDNAME_LASTCODE = 0x59;

    //#if VENDOR=="LG" || (VENDOR=="NOKIA" && (MODEL=="3410" || MODEL=="3510"))


    //#if MIDP=="2.0"
    //$private static class TextForm extends javax.microedition.lcdui.game.GameCanvas
    //#else
    //#if VENDOR=="NOKIA"
    //$private static class TextForm extends com.nokia.mid.ui.FullCanvas
    //#else
    //#if VENDOR=="SIEMENS" && (MODEL=="M55" || MODEL=="S55")
    //$private static class TextForm extends com.siemens.mp.color_game.GameCanvas
    //#else
    private static class TextForm extends Canvas // Реализует форму с возможностью скролла и отображения длинного текста
    //#endif
    //#endif
    //#endif
    {
        //#if VENDOR=="NOKIA" && MODEL=="3410"
        //$private final static Font p_outFont = Font.getFont(Font.FACE_PROPORTIONAL, Font.STYLE_PLAIN, Font.SIZE_SMALL);
        //#else
        private final static Font p_outFont = Font.getDefaultFont();
        //#endif
        private final static int i_FontHeight = p_outFont.getHeight();

        //#if VENDOR=="NOKIA" && MODEL=="3410"
        //$private final static int COLOR_BACKGROUND = 0xFFFFFF;
        //$private final static int COLOR_TEXT = 0x0;
        //$private final static int COLOR_BORDER = 0x0;
        //#else
        private final static int COLOR_BACKGROUND = 0x008ED6;
        private final static int COLOR_TEXT = 0xFFFFFF;
        private final static int COLOR_BORDER = 0x0000CC;
        //#endif

        private final static int SCROLLBAR_WIDTH = 5;
        private final static int STARTTEXT_X = 2;

        private static int i_Width;
        private static int i_Height;

        private static int i_ViewWidth;
        private static int i_ViewHeight;

        private static String s_Title;
        private static String s_Text;
        private static String s_Button;

        private static boolean lg_isVertScroll;

        private static int i_XTitle;
        private static int i_XButton;
        private static int i_Position;

        private static int i_stringsNumber;
        private static int i_stringsOnScreen;
        private static int i_ScrollBarHeight;

        private static int i8_perString;

        private static String[] as_stringArray;

        public TextForm(String _title, String _text, boolean _textWrap)
        {
            super();

            s_Title = _title;
            s_Text = _text;

            s_Button = LangBlock.getStringForIndex(BackTXT);
            i_Width = getWidth();
            i_Height = getHeight();

            i_XButton = i_Width - p_outFont.stringWidth(s_Button) - p_outFont.charWidth(' ');

            i_ViewWidth = i_Width - 2 - STARTTEXT_X - SCROLLBAR_WIDTH;
            i_ViewHeight = i_Height - (i_FontHeight << 1);

            i_XTitle = (i_Width - p_outFont.stringWidth(s_Title)) >> 1;

            final int i_len = _text.length();
            i_stringsNumber = 1;

            if (!_textWrap)
            {
                for (int li = 0; li < i_len; li++)
                {
                    if (_text.charAt(li) == '\n') i_stringsNumber++;
                }
                as_stringArray = new String [i_stringsNumber];
                int i_pos = 0;
                for (int li = 0; li < i_stringsNumber; li++)
                {
                    StringBuffer p_buf = new StringBuffer(32);
                    while (i_pos < i_len)
                    {
                        char ch_char = _text.charAt(i_pos++);
                        if (ch_char == '\n') break;
                        p_buf.append(ch_char);
                    }
                    as_stringArray[li] = p_buf.toString();
                    p_buf = null;
                }
            }
            else
            {
                // Нарезаем по словам с максимально вписывающимися размерами
                int i_index = 0;

                java.util.Vector p_vector = new java.util.Vector(64);

                StringBuffer p_strBuffer = new StringBuffer(32);
                int i_curIndex = 0;
                while (i_index < i_len)
                {
                    final char ch_char = _text.charAt(i_index++);
                    if (ch_char == '\n')
                    {
                        p_vector.addElement(p_strBuffer.toString());
                        p_strBuffer = new StringBuffer(32);
                        i_curIndex = 0;
                    }
                    else
                    {
                        int i_chW = p_outFont.charWidth(ch_char);
                        i_curIndex += i_chW;
                        if (i_curIndex >= i_ViewWidth)
                        {
                            p_vector.addElement(p_strBuffer.toString());
                            p_strBuffer = new StringBuffer(32);
                            p_strBuffer.append(ch_char);
                            i_curIndex = i_chW;
                        }
                        else
                            p_strBuffer.append(ch_char);
                    }
                }
                p_vector.addElement(p_strBuffer.toString());
                p_strBuffer = null;

                i_stringsNumber = p_vector.size();
                as_stringArray = new String [i_stringsNumber];
                for (int li = 0; li < i_stringsNumber; li++)
                {
                    as_stringArray[li] = (String) p_vector.elementAt(li);
                }
                p_vector = null;
            }

            i_stringsOnScreen = i_ViewHeight / i_FontHeight;
            i_Position = 0;
            if (!_textWrap && i_stringsOnScreen >= as_stringArray.length)
            {
                lg_isVertScroll = false;
                i_ViewWidth += SCROLLBAR_WIDTH;
            }
            else
                lg_isVertScroll = true;

            int i8_scrollBarFullHeight = (i_ViewHeight - (SCROLLBAR_WIDTH << 1)) << 8;
            final int FULL_HEIGHT = i_stringsNumber * i_FontHeight;
            i8_perString = i8_scrollBarFullHeight / FULL_HEIGHT;

            i_ScrollBarHeight = (((((i_FontHeight * i_stringsOnScreen) << 8) / FULL_HEIGHT) * i8_scrollBarFullHeight + 0x7FFF) >> 16);
        }

        protected void keyPressed(int _key)
        {
            final int i_step = i_stringsOnScreen > 5 ? i_stringsOnScreen >> 1 : i_stringsOnScreen;

            switch (_key)
            {
                    //#if VENDOR=="LG"
                    //$case -7:
                    //#endif
                    case KEY_CODE_SOFT_RIGHT:
                    {
                        // Выход из формы
                        p_Display.setCurrent(p_InsideCanvas);
                        s_Title = null;
                        s_Text = null;
                    }
            ;
                    break;
                case KEY_CODE_DOWN:
                case JOY_CODE_DOWN:
                    {
                        // Скролл вниз
                        int i_newPos = i_Position + i_step;
                        if (i_newPos + i_stringsOnScreen < as_stringArray.length)
                        {
                            i_Position = i_newPos;
                        }
                        else
                        {
                            i_newPos = as_stringArray.length - i_stringsOnScreen;
                            i_Position = i_newPos >= 0 ? i_newPos : 0;
                        }

                        repaint();
                    }
            ;
                    break;
                case KEY_CODE_UP:
                case JOY_СODE_UP:
                    {
                        // Скролл вверх
                        int i_newPos = i_Position - i_step;
                        if (i_newPos >= 0)
                        {
                            i_Position = i_newPos;
                        }
                        else
                        {
                            i_Position = 0;
                        }
                        repaint();
                    }
            ;
                    break;
            }
        }

        private static boolean lg_inPaint = false;

        protected void paint(Graphics _g)
        {
            if (lg_inPaint) return;
            lg_inPaint = true;

            if (s_Title == null) return;
            final int TXT = Graphics.LEFT | Graphics.TOP;

            _g.setClip(0, 0, getWidth(), getHeight());
            _g.setColor(COLOR_BACKGROUND);
            _g.fillRect(0, 0, i_Width, i_Height);
            _g.setColor(COLOR_BORDER);
            _g.drawRect(0, 0, i_Width - 1, i_Height - 1);

            _g.setFont(p_outFont);
            // Заголовок
            _g.setColor(COLOR_TEXT);
            _g.fillRect(0, 0, i_Width, i_FontHeight);
            _g.setColor(COLOR_BACKGROUND);
            int i_x = (i_Width - p_outFont.stringWidth(s_Title)) >> 1;
            _g.drawString(s_Title, i_x, 0, TXT);

            // Кнопка
            int i_y = i_Height - i_FontHeight;
            _g.setColor(COLOR_TEXT);
            _g.fillRect(0, i_y, i_Width - 1, i_FontHeight);
            _g.setColor(COLOR_BACKGROUND);

            _g.drawString(s_Button, i_XButton, i_y, TXT);

            if (lg_isVertScroll)
            {
                // Скроллбар
                _g.setColor(COLOR_BORDER);
                if (i_stringsNumber <= i_stringsOnScreen)
                {
                    i_x = i_Width - SCROLLBAR_WIDTH - 1;
                    _g.fillRect(i_x, i_FontHeight, SCROLLBAR_WIDTH, i_ViewHeight);
                }
                else
                {
                    // Вертикальная линия
                    i_x = i_Width - 1 - (SCROLLBAR_WIDTH >> 1);
                    _g.drawLine(i_x, i_FontHeight, i_x, i_Height - i_FontHeight);
                    // Границы
                    i_x = i_Width - 1 - SCROLLBAR_WIDTH;
                    _g.fillRect(i_x, i_FontHeight, SCROLLBAR_WIDTH, SCROLLBAR_WIDTH);
                    _g.fillRect(i_x, i_Height - i_FontHeight - SCROLLBAR_WIDTH, SCROLLBAR_WIDTH, SCROLLBAR_WIDTH);
                    // Позиция
                    i_y = 1 + i_FontHeight + SCROLLBAR_WIDTH + ((i_Position * i_FontHeight * i8_perString + 0x7F) >> 8);
                    _g.fillRect(i_x, i_y, SCROLLBAR_WIDTH, i_ScrollBarHeight);
                }
            }

            // Текст
            i_y = i_FontHeight + 1;
            final int i_endY = i_y + i_ViewHeight;
            _g.setClip(STARTTEXT_X, i_FontHeight, i_ViewWidth - STARTTEXT_X, i_ViewHeight);
            _g.setColor(COLOR_TEXT);
            for (int li = 0; li < i_stringsOnScreen; li++)
            {
                final int i_strIndex = li + i_Position;
                if (i_strIndex >= as_stringArray.length) break;

                final String s_str = as_stringArray[i_strIndex];
                _g.drawString(s_str, STARTTEXT_X, i_y, TXT);

                i_y += i_FontHeight;
                if (i_y >= i_endY) break;
            }

            lg_inPaint = false;
        }
    }

    //#endif

    //#if MIDP=="2.0"
    //$private static class InsideCanvas extends javax.microedition.lcdui.game.GameCanvas
    //#else
    //#if VENDOR=="NOKIA"
    //$private static class InsideCanvas extends com.nokia.mid.ui.FullCanvas
    //#else
    //#if VENDOR=="SIEMENS" && (MODEL=="M55" || MODEL=="S55")
    //$private static class InsideCanvas extends com.siemens.mp.color_game.GameCanvas
    //#else
    //#if VENDOR=="SE"
    //$private static class InsideCanvas extends Canvas implements CommandListener
    //#else
    private static class InsideCanvas extends Canvas
    //#endif
    //#endif
    //#endif
    //#endif
    {
        private static int i_screenOffsetX = 0;
        private static int i_screenOffsetY = 0;
        private static boolean lg_drawBorder;
        private static int i_screenWidth;
        private static int i_screenHeight;

        private static startup p_parent;

        private static boolean lg_firstPaint = true;

        //#if MIDP=="2.0" || VENDOR=="SIEMENS"
        private static Graphics p_CanvasGraphics = null;
        //#endif

        //#if DBUFFER
        //#if VENDOR=="SIEMENS" && MODEL=="M50"
        //$private static com.siemens.mp.game.ExtendedImage p_doubleBuffer;
        //#else
        //$private static Image p_doubleBuffer;
        //#endif
        //$private static Graphics p_doubleGraphics;
        //#endif


        //#if VENDOR=="SE"
        //$private static final Command p_leftCommand = new Command("",Command.ITEM,0);
        //$private static final Command p_rightCommand = new Command("",Command.ITEM,1);

        //$public void commandAction(Command command, Displayable displayable)
        //${
        //$    if (command.equals(p_leftCommand)) keyPressed(-6);
        //$    else
        //$    if (command.equals(p_rightCommand)) keyPressed(-7);
        //$}
        //#endif

        protected void showNotify()
        {
            lg_firstPaint = true;
            i_KeyFlags = 0;
            i_lastPressedKey = -1;
        }

        protected void hideNotify()
        {
            if (startup.i_MidletMode == MODE_GAMEPLAY) startup.setMode(MODE_GAMEMENU);
            //#if SOUND
            SoundManager.stopAllSound();
            //#endif
            i_KeyFlags = 0;
            i_lastPressedKey = -1;
        }

        public InsideCanvas()
        {
            //#if MIDP=="2.0" || (VENDOR=="SIEMENS" && (MODEL=="M55" || MODEL=="S55"))
            //$super(false);
            //#else
            super();
            //#endif

            //#if MIDP=="2.0"
            //$setFullScreenMode(true);
            //#endif

            //#if MIDP=="2.0" || VENDOR=="SIEMENS"
            //$p_CanvasGraphics = this.getGraphics();
            //#endif


            //#if DBUFFER
            //#if VENDOR=="SIEMENS" && MODEL=="M50"
            //$Image p_imG = Image.createImage((SCREEN_WIDTH & 7)!=0 ? ((SCREEN_WIDTH >>> 3)+1)<<3 : SCREEN_WIDTH + 8,SCREEN_HEIGHT);
            //$p_doubleBuffer = new com.siemens.mp.game.ExtendedImage(p_imG);
            //$p_doubleGraphics = p_imG.getGraphics();
            //#else
            //$p_doubleBuffer = Image.createImage(SCREEN_WIDTH,SCREEN_HEIGHT);
            //$p_doubleGraphics = p_doubleBuffer.getGraphics();
            //#endif
            //#endif

            i_screenHeight = getHeight();
            i_screenWidth = getWidth();

            //#if SHOWSYS
            System.out.println("Screen: " + i_screenWidth + "x" + i_screenHeight);
            //#endif

            //i_screenWidth = i_screenWidth < SCREEN_WIDTH ? SCREEN_WIDTH : i_screenWidth;
            //i_screenHeight = i_screenHeight < SCREEN_HEIGHT ? SCREEN_HEIGHT : i_screenHeight;

            i_screenOffsetX = (i_screenWidth - SCREEN_WIDTH) / 2;
            i_screenOffsetY = (i_screenHeight - SCREEN_HEIGHT) / 2;

            //#if VENDOR=="SAMSUNG" || VENDOR=="MOTOROLA"
            // Сбрасываем в ноль смещение по вертикали если оно отрицательное, иначе проблема при выводе из-за ошибки возвращаемым значением вертикальной величины экрана
            //$if (i_screenOffsetY<0) i_screenOffsetY = 0;
            //#endif
            //#if SHOWSYS
            System.out.println("SX:" + i_screenOffsetX + " SY:" + i_screenOffsetY);
            //#endif

            lg_drawBorder = (i_screenOffsetX | i_screenOffsetY) != 0;


            //#if VENDOR=="SE"
            //$addCommand(p_rightCommand);
            //$addCommand(p_leftCommand);
            //$setCommandListener(this);
            //#endif
        }

        private static final void paintRecordNamePanel(Graphics _graphics)
        {
            _graphics.setColor(COLOR_RECORD_BACKGROUND);
            _graphics.fillRect(0, 0, SCREEN_WIDTH, SCREEN_HEIGHT);
            final int i_point = Graphics.TOP | Graphics.LEFT;

            //#if VENDOR=="SAMSUNG"

            // Кнопки
            //$final int OTSTUP = 3;
            //$int i_strY = SCREEN_HEIGHT - LangBlock.i_FontImage_CharHeight - OTSTUP;
            // кнопка "Отменить"
            //$LangBlock.drawStringForIndex(CancelTXT,_graphics,OTSTUP,i_lastPressedKey == KEY_CODE_SOFT_LEFT ? i_strY + OTSTUP : i_strY);
            // кнопка "Записать"
            //$LangBlock.drawStringForIndex(SaveTXT,_graphics,SCREEN_WIDTH-OTSTUP-LangBlock.getStringWidth(SaveTXT),i_lastPressedKey == KEY_CODE_SOFT_RIGHT ? i_strY + OTSTUP : i_strY);

            // количество очков
            //$LangBlock.drawStringForIndex(ScoreTXT,_graphics,OTSTUP,OTSTUP);
            //$int i_scX = OTSTUP+LangBlock.getStringWidth(ScoreTXT)+LangBlock.i_FontImage_CharWidth;
            //$LangBlock.drawInteger(Gamelet.getPlayerScore(),_graphics,i_scX,OTSTUP);

            // Надпись "Имя рекорда"
            //$i_strY = LangBlock.i_FontImage_CharHeight*3;
            //$i_scX = (SCREEN_WIDTH-LangBlock.getStringWidth(RecordNameTXT))>>1;
            //$LangBlock.drawStringForIndex(RecordNameTXT,_graphics,i_scX,i_strY);
            //$i_strY += (LangBlock.i_FontImage_CharHeight<<1);

            // Буквы имени
            //$final int CHAR_INTERVAL_S = 4;
            //$final int CHAR_WIDTH = 10;
            //$final int CHAR_HEIGHT = 11;
            //$final int RCT_WDTH = 14;
            //$final int RCT_HGHT = 15;
            //$final int CH_X_OFFST = (RCT_WDTH-CHAR_WIDTH)>>1;
            //$final int CH_Y_OFFST = (RCT_HGHT-CHAR_HEIGHT)>>1;

            //$i_scX = (SCREEN_WIDTH - ((CHAR_INTERVAL_S<<1)+(RCT_WDTH*3)))>>1;


            //$for (int li = 0; li < 3; li++)
            //${
            //$    _graphics.setClip(0,0,SCREEN_WIDTH,SCREEN_HEIGHT);
            //$    int i_bckgcolor = COLOR_RECORD_BCKGNDCHAR;
            //$    int i_chrcolor = COLOR_RECORD_CHAR;
            //$    if (i_RecordCharPosition == li)
            //$    {
            //$        i_bckgcolor = COLOR_RECORD_CHAR;
            //$        i_chrcolor = COLOR_RECORD_BCKGNDCHAR;
            //$        _graphics.setColor(i_bckgcolor);
            //$        _graphics.fillRect(i_scX, i_strY, RCT_WDTH, RCT_HGHT);
            //$    }
            //$    else
            //$    {
            //$        _graphics.setColor(i_bckgcolor);
            //$        _graphics.fillRect(i_scX, i_strY, RCT_WDTH, RCT_HGHT);
            //$    }

            //$    int i_index = ai_RecordNameChars[li] - LETTER_RECORDNAME_FIRSTCODE;
            //$    _graphics.setClip(i_scX+CH_X_OFFST,i_strY+CH_Y_OFFST,CHAR_WIDTH,CHAR_HEIGHT);
            //$    _graphics.drawImage(p_bfImg,i_scX+CH_X_OFFST-i_index*CHAR_WIDTH,i_strY+CH_Y_OFFST,i_point);

            //$    i_scX += RCT_WDTH + CHAR_INTERVAL_S;
            //$}


            //#else

            //#if VENDOR=="NOKIA" && (MODEL=="3410" || MODEL=="3510")
            //$Font p_chFont = Font.getFont(Font.FACE_PROPORTIONAL, Font.STYLE_BOLD, Font.SIZE_SMALL);
            //#else
            //#if VENDOR=="SIEMENS" && (MODEL=="S55" || MODEL=="M55" || MODEL=="M50")
            //$Font p_chFont = Font.getFont(Font.FACE_PROPORTIONAL, Font.STYLE_BOLD, Font.SIZE_MEDIUM);
            //#else
            //#if VENDOR=="NOKIA" && MODEL=="6100"
            //$Font p_chFont = Font.getFont(Font.FACE_PROPORTIONAL, Font.STYLE_BOLD, Font.SIZE_MEDIUM);
            //#else
            Font p_chFont = Font.getFont(Font.FACE_PROPORTIONAL, Font.STYLE_BOLD, Font.SIZE_LARGE);
            //#endif
            //#endif
            //#endif


            _graphics.setFont(p_chFont);

            String s_title = LangBlock.getStringForIndex(RecordNameTXT);
            int i_x = (SCREEN_WIDTH - p_chFont.stringWidth(s_title)) >> 1;
            int i_y = 2;
            _graphics.setColor(COLOR_RECORD_TEXT);
            _graphics.drawString(s_title, i_x, i_y, i_point);
            i_y += p_chFont.getHeight() + 3;

            _graphics.setFont(GameMenu.p_MenuFont);
            s_title = LangBlock.getStringForIndex(ScoreTXT) + " " + Gamelet.getPlayerScore();
            i_x = (SCREEN_WIDTH - GameMenu.p_MenuFont.stringWidth(s_title)) >> 1;
            _graphics.drawString(s_title, i_x, i_y, i_point);

            //#if (VENDOR=="SIEMENS" && MODEL=="M50") || (VENDOR=="NOKIA" && (MODEL=="3410" || MODEL=="3510"))
            //$i_y += GameMenu.p_MenuFont.getHeight() + 3;
            //#else
            i_y += GameMenu.p_MenuFont.getHeight() + 8;
            //#endif

            _graphics.setFont(p_chFont);

            final int CHAR_INTERVAL = 3;
            //#if (VENDOR=="SIEMENS" && MODEL=="M50") || (VENDOR=="NOKIA" && (MODEL=="3410" || MODEL=="3510"))
            //$final int RECT_WIDTH = (p_chFont.charWidth('W') << 1)-2;
            //$final int RECT_HEIGHT = (p_chFont.getHeight() + (CHAR_INTERVAL << 1))-2;
            //#else
            final int RECT_WIDTH = p_chFont.charWidth('W') << 1;
            final int RECT_HEIGHT = p_chFont.getHeight() + (CHAR_INTERVAL << 1);
            //#endif
            i_x = (SCREEN_WIDTH - (RECT_WIDTH * 3 + (CHAR_INTERVAL << 1))) >> 1;
            int i_chY = i_y + CHAR_INTERVAL;

            for (int li = 0; li < 3; li++)
            {
                int i_bckgcolor = COLOR_RECORD_BCKGNDCHAR;
                int i_chrcolor = COLOR_RECORD_CHAR;
                if (i_RecordCharPosition == li)
                {
                    i_bckgcolor = COLOR_RECORD_CHAR;
                    i_chrcolor = COLOR_RECORD_BCKGNDCHAR;
                    _graphics.setColor(i_bckgcolor);
                    _graphics.fillRect(i_x - 1, i_y - 1, RECT_WIDTH + 2, RECT_HEIGHT + 2);

                    //#if (VENDOR=="SIEMENS" && MODEL=="M50") || (VENDOR=="NOKIA" && MODEL=="3410")
                    //$_graphics.setColor(~i_bckgcolor);
                    //$_graphics.drawRect(i_x - 1, i_y - 1, RECT_WIDTH + 2, RECT_HEIGHT + 2);
                    //#endif
                }
                else
                {
                    _graphics.setColor(i_bckgcolor);
                    _graphics.fillRect(i_x, i_y, RECT_WIDTH, RECT_HEIGHT);
                }

                _graphics.setColor(i_chrcolor);

                char ch_char = LangBlock.CHARSETS[ai_RecordNameChars[li] >>> 6].charAt(ai_RecordNameChars[li] & 0x3F);

                String s_str = new StringBuffer(1).append(ch_char).toString();

                int i_w = p_chFont.stringWidth(s_str);
                _graphics.drawString(s_str, i_x + ((RECT_WIDTH - i_w) >> 1), i_chY, i_point);

                i_x += CHAR_INTERVAL + RECT_WIDTH;
            }

            _graphics.setFont(GameMenu.p_MenuFont);
            // Отрисовываем ВВОД ОТМЕНА в правом и левом нижнем углу

            //#if (VENDOR=="NOKIA") && (MODEL=="3410" || MODEL=="3510")
            //$final int OFFSET_HORZ = 3;
            //$final int OFFSET_VERT = 2;
            //#else
            final int OFFSET_HORZ = 1;
            final int OFFSET_VERT = 1;
            //#endif

            String s_strEnter = LangBlock.getStringForIndex(SaveTXT);
            String s_strCancel = LangBlock.getStringForIndex(CancelTXT);
            i_y = SCREEN_HEIGHT - GameMenu.p_MenuFont.getHeight() - OFFSET_VERT;

            //#if (VENDOR=="SIEMENS" && MODEL=="M50") || (VENDOR=="NOKIA" && MODEL=="3410")
            //$if (i_lastPressedKey == KEY_CODE_SOFT_LEFT)
            //$    _graphics.setColor(COLOR_RECORD_TEXT^0xFFFFFF);
            //$else
            //$    _graphics.setColor(COLOR_RECORD_TEXT);
            //#else
            if (i_lastPressedKey == KEY_CODE_SOFT_LEFT)
                _graphics.setColor(COLOR_RECORD_TEXT);
            else
                _graphics.setColor(~COLOR_RECORD_TEXT);
            //#endif
            _graphics.drawString(s_strCancel, OFFSET_HORZ, i_y, i_point);

            //#if (VENDOR=="SIEMENS" && MODEL=="M50") || (VENDOR=="NOKIA" && MODEL=="3410")
            //$if (i_lastPressedKey == KEY_CODE_SOFT_RIGHT)
            //$    _graphics.setColor(COLOR_RECORD_TEXT^0xFFFFFF);
            //$else
            //$    _graphics.setColor(COLOR_RECORD_TEXT);
            //#else
            if (i_lastPressedKey == KEY_CODE_SOFT_RIGHT)
                _graphics.setColor(COLOR_RECORD_TEXT);
            else
                _graphics.setColor(~COLOR_RECORD_TEXT);
            //#endif
            _graphics.drawString(s_strEnter, SCREEN_WIDTH - OFFSET_HORZ - GameMenu.p_MenuFont.stringWidth(s_strEnter), i_y, i_point);
            //#endif
        }


        //#if MIDP=="2.0" || VENDOR=="SIEMENS"
        //$private static boolean lg_underPaint = false;
        //$public final void paintFlush()
        //#else
        public void paint(Graphics _graphics)
        //#endif
        {
            //#if MIDP=="2.0" || VENDOR=="SIEMENS"
            //$if (lg_underPaint) return;
            //$try{
            //$lg_underPaint = true;
            //$Graphics _graphics = p_CanvasGraphics;
            //#endif

            //#if DBUFFER
            //$final Graphics p_mainGraphics = _graphics;
            //$_graphics = p_doubleGraphics;

            // Увеличиваем размеры области если первый запуск
            //$if (lg_firstPaint)
            //${
            //$    p_mainGraphics.setClip(0,0,SCREEN_WIDTH,SCREEN_HEIGHT);
            //$    lg_firstPaint = false;
            //$}
            //#endif

            //#if !DBUFFER
            if (lg_drawBorder) _graphics.translate(i_screenOffsetX, i_screenOffsetY);
            //#endif
            _graphics.setClip(0, 0, SCREEN_WIDTH, SCREEN_HEIGHT);

            switch (i_MidletMode)
            {
                case MODE_INITED:
                    {
                        _graphics.setColor(COLOR_LOADING_BACKGROUND);
                        _graphics.fillRect(0, 0, SCREEN_WIDTH, SCREEN_HEIGHT);

                        //#if DEALERDELAY>0
                        if (p_Image_LoadingLogo != null)
                        {
                            int i_logoWidth = p_Image_LoadingLogo.getWidth();
                            int i_logoHeight = p_Image_LoadingLogo.getHeight();
                            int i_splImgX = (SCREEN_WIDTH - i_logoWidth) >> 1;
                            int i_splImgY = (SCREEN_HEIGHT - i_logoHeight) >> 1;
                            _graphics.drawImage(p_Image_LoadingLogo, i_splImgX, i_splImgY, 0);
                        }
                        //#endif
                    }
            ;
                    break;
                case MODE_UNKNOWN:
                    {
                    }
            ;
                    break;
                case MODE_LOADING:
                    {
                        _graphics.setColor(COLOR_LOADING_BACKGROUND);
                        _graphics.fillRect(0, 0, SCREEN_WIDTH, SCREEN_HEIGHT);

                        int i_splImgX = 0;
                        int i_splImgY = 0;

                        if (p_Image_LoadingLogo != null)
                        {
                            int i_logoWidth = p_Image_LoadingLogo.getWidth();
                            int i_logoHeight = p_Image_LoadingLogo.getHeight();

                            i_splImgX = (SCREEN_WIDTH - i_logoWidth) >> 1;
                            i_splImgY = (SCREEN_HEIGHT - (i_logoHeight + LOADING_BAR_HEIGHT + LOADING_BAR_OFFSET_FROM_LOGO)) >> 1;
                            _graphics.drawImage(p_Image_LoadingLogo, i_splImgX, i_splImgY, 0);
                            _graphics.setClip(0, 0, SCREEN_WIDTH, SCREEN_HEIGHT);
                            i_splImgY += i_logoHeight + LOADING_BAR_OFFSET_FROM_LOGO;
                        }
                        else
                        {
                            i_splImgY = SCREEN_HEIGHT >> 1;
                        }
                        i_splImgY = i_splImgY > SCREEN_HEIGHT - LOADING_BAR_HEIGHT - 1 ? SCREEN_HEIGHT - LOADING_BAR_HEIGHT - 1 : i_splImgY;

                        i_splImgX = (SCREEN_WIDTH - LOADING_BAR_WIDTH) >> 1;

                        //#if (VENDOR=="SIEMENS" && MODEL=="M50") || (VENDOR=="NOKIA" && MODEL=="3410")
                        //$_graphics.setColor(COLOR_LOADING_BAR);
                        //$_graphics.drawRect(i_splImgX-2, i_splImgY-2, LOADING_BAR_WIDTH+3, LOADING_BAR_HEIGHT+3);
                        //#else
                        _graphics.setColor(COLOR_LOADING_BAR_BACKGROUND);
                        _graphics.fillRect(i_splImgX, i_splImgY, LOADING_BAR_WIDTH, LOADING_BAR_HEIGHT);
                        //#endif
                        _graphics.setColor(COLOR_LOADING_BAR);
                        _graphics.fillRect(i_splImgX, i_splImgY, (i_LoadingProgress * I8_LOADING_BAR_PERCENT + 0x7F) >> 8, LOADING_BAR_HEIGHT);
                    }
            ;
                    break;
                case MODE_MAINMENU:
                    {
                        _graphics.setColor(COLOR_MAIN_BACKGROUND);
                        _graphics.fillRect(0, 0, SCREEN_WIDTH, SCREEN_HEIGHT);

                        //#if MCF_SPLASH
                        //$p_Image_Splash.paint(_graphics,0,0,true);
                        //#else
                        if (p_Image_Splash != null)
                        {
                            int i_iw = p_Image_Splash.getWidth();
                            int i_ih = p_Image_Splash.getHeight();
                            _graphics.drawImage(p_Image_Splash, (SCREEN_WIDTH - i_iw) >> 2, (SCREEN_HEIGHT - i_ih) >> 2, 0);
                        }
                        //#endif
                        GameMenu.paintMenu(_graphics, 0, 0, SCREEN_WIDTH, SCREEN_HEIGHT);
                    }
            ;
                    break;
                case MODE_RECORDNAME:
                    {
                        paintRecordNamePanel(_graphics);
                    }
            ;
                    break;
                case MODE_GAMEMENU:
                    {
                        //#if !DBUFFER || (VENDOR=="SAMSUNG" && MODEL=="X100")
                        onPaintGame(_graphics);
                        _graphics.setClip(0, 0, SCREEN_WIDTH, SCREEN_HEIGHT);
                        GameMenu.paintMenu(_graphics, 0, 0, SCREEN_WIDTH, SCREEN_HEIGHT);
                        //#endif
                    }
            ;
                    break;
                case MODE_GAMEFINAL:
                    {
                        onPaintGameOver(_graphics);
                    }
            ;
                    break;
                case MODE_GAMEPLAY:
                    {
                        //#if !DBUFFER
                        onPaintGame(_graphics);
                        //#endif
                    }
            ;
                    break;
                case MODE_SHOWSTAGE:
                    {
                        //#if !DBUFFER
                        onPaintGameStage(_graphics);
                        //#endif
                    }
            ;
                    break;
                case MODE_RELEASING:
                    {
                        _graphics.fillRect(0, 0, SCREEN_WIDTH, SCREEN_HEIGHT);
                    }
            ;
                    break;
                case MODE_ERROR:
                    {
                        _graphics.setColor(0xA00000);
                        _graphics.fillRect(0, 0, SCREEN_WIDTH, SCREEN_HEIGHT);
                        _graphics.setColor(0xFFFFFF);

                        Font p_fnt = Font.getFont(Font.FACE_SYSTEM, Font.STYLE_BOLD, Font.SIZE_LARGE);
                        _graphics.setFont(p_fnt);
                        _graphics.drawString("FATAL ERROR", 0, 0, 0);
                        int i_y = p_fnt.getHeight() + 3;
                        p_fnt = Font.getFont(Font.FACE_SYSTEM, Font.STYLE_PLAIN, Font.SIZE_SMALL);
                        _graphics.setFont(p_fnt);
                        _graphics.drawString(s_errorString == null ? "NULL" : s_errorString, 0, i_y, 0);
                    }
            ;
                    break;
            }

            // Отрисовываем бордюры
            if (lg_drawBorder)
            {
                //#if !DBUFFER
                _graphics.translate(-i_screenOffsetX, -i_screenOffsetY);
                //#endif

                //#if DBUFFER
                //$p_mainGraphics.setColor(COLOR_BORDER);
                // Верхний бордюр
                //$p_mainGraphics.fillRect(0, 0, i_screenWidth, i_screenOffsetY);

                // Нижний бордюр
                //$int i_hght = i_screenOffsetY + SCREEN_HEIGHT;
                //$p_mainGraphics.fillRect(0, i_hght, i_screenWidth, i_screenOffsetY);

                // Левый бордюр
                //$p_mainGraphics.fillRect(0, i_screenOffsetY, i_screenOffsetX, SCREEN_HEIGHT);

                // Правый бордюр
                //$p_mainGraphics.fillRect(i_screenOffsetX + SCREEN_WIDTH, i_screenOffsetY, i_screenOffsetX, SCREEN_HEIGHT);

                //#else

                _graphics.setClip(0, 0, i_screenWidth, i_screenHeight);
                _graphics.setColor(COLOR_BORDER);
                // Верхний бордюр
                _graphics.fillRect(0, 0, i_screenWidth, i_screenOffsetY);
                // Нижний бордюр
                int i_hght = i_screenOffsetY + SCREEN_HEIGHT;
                _graphics.fillRect(0, i_hght, i_screenWidth, i_screenOffsetY);
                // Левый бордюр
                _graphics.fillRect(0, i_screenOffsetY, i_screenOffsetX, SCREEN_HEIGHT);
                // Правый бордюр
                _graphics.fillRect(i_screenOffsetX + SCREEN_WIDTH, i_screenOffsetY, i_screenOffsetX, SCREEN_HEIGHT);
                //#endif
            }

            //#ifdefined DEMO_STRING
            //#if !SHOWSYS

            // Выводим строку о том что это демоверсия в левом верхнем углу

            //$final int GTL = Graphics.TOP | Graphics.LEFT;
            //$_graphics.setClip(0, 0, i_screenWidth, i_screenHeight);
            // Отрисовываем объем занятой и свободной памяти
            //$Font p_fnt = Font.getFont(Font.FACE_SYSTEM, Font.STYLE_BOLD, Font.SIZE_MEDIUM);
            //$_graphics.setFont(p_fnt);
            //$_graphics.setColor(0);
            //$_graphics.drawString("/*$DEMO_STRING$*/", 3, 3, GTL);
            //$_graphics.setColor(0xFFFFFF);
            //$_graphics.drawString("/*$DEMO_STRING$*/", 1, 1, GTL);
            //#endif
            //#endif

            //#if SHOWSYS
            _graphics.setClip(0, 0, i_screenWidth, i_screenHeight);
            // Отрисовываем объем занятой и свободной памяти
            Font p_fnt = Font.getFont(Font.FACE_SYSTEM, Font.STYLE_PLAIN, Font.SIZE_SMALL);
            _graphics.setFont(p_fnt);
            int i_y = 0;
            StringBuffer p_strBuf = new StringBuffer("F/T:");
            p_strBuf.append(Runtime.getRuntime().freeMemory());
            p_strBuf.append('/');
            p_strBuf.append(Runtime.getRuntime().totalMemory());
            String s_out = p_strBuf.toString();

            final int GTL = Graphics.TOP | Graphics.LEFT;

            _graphics.setColor(0);
            _graphics.drawString(s_out, 1, i_y + 1, GTL);
            _graphics.setColor(0xFFFFFF);
            _graphics.drawString(s_out, 0, i_y, GTL);
            i_y += p_fnt.getHeight();

            if (i_MidletMode == MODE_GAMEPLAY)
            {
                // Профилирование игрового шага и отрисовки
                p_strBuf = new StringBuffer("S/P:");
                p_strBuf.append(i_Profil_TimeNextGameStep);
                p_strBuf.append('/');
                p_strBuf.append(i_Profil_TimePaint);
                s_out = p_strBuf.toString();
                _graphics.setColor(0);
                _graphics.drawString(s_out, 1, i_y + 1, GTL);
                _graphics.setColor(0xFF0000);
                _graphics.drawString(s_out, 0, i_y, GTL);
                i_y += p_fnt.getHeight();
            }

            // Код нажатой клавиши
            p_strBuf = new StringBuffer("K:");
            p_strBuf.append(i_lastPressedKey);
            s_out = p_strBuf.toString();
            _graphics.setColor(0);
            _graphics.drawString(s_out, 1, i_y + 1, GTL);
            _graphics.setColor(0xFF0000);
            _graphics.drawString(s_out, 0, i_y, GTL);
            i_y += p_fnt.getHeight();

            // Выводим сообщение исключения если есть
            if (s_CurrentExceptionMessage != null)
            {
                s_out = s_CurrentExceptionMessage;
                _graphics.setColor(0);
                _graphics.drawString(s_out, 1, i_y + 1, GTL);
                _graphics.setColor(0xFF0000);
                _graphics.drawString(s_out, 0, i_y, GTL);
                i_y += p_fnt.getHeight();
            }

            //#endif

            //#if DBUFFER
            //#if VENDOR=="SIEMENS" && MODEL=="M50"
            //$p_mainGraphics.drawImage(p_doubleBuffer.getImage(),i_screenOffsetX,i_screenOffsetY,0);
            //#else
            //$p_mainGraphics.drawImage(p_doubleBuffer,i_screenOffsetX,i_screenOffsetY,0);
            //#endif
            //#if !(VENDOR=="SAMSUNG" && MODEL=="X100")
            //$if (i_MidletMode == MODE_GAMEMENU)
            //${
            //$GameMenu.paintMenu(p_mainGraphics, i_screenOffsetX, i_screenOffsetY ,SCREEN_WIDTH, SCREEN_HEIGHT);
            //$p_mainGraphics.setClip(i_screenOffsetX, i_screenOffsetY, SCREEN_WIDTH, SCREEN_HEIGHT);
            //$}
            //#endif
            //#endif


            //#if MIDP=="2.0" || VENDOR=="SIEMENS"
            //$if (this.isShown()) flushGraphics();
            //$}finally{lg_underPaint = false;}
            //#endif
        }

        protected synchronized void keyPressed(int _keyCode)
        {
            i_lastPressedKey = _keyCode;

            switch (i_MidletMode)
            {
                case MODE_UNKNOWN:
                    {
                    }
            ;
                    break;
                case MODE_LOADING:
                    {
                    }
            ;
                    break;
                case MODE_INITED:
                    {
                    }
            ;
                    break;
                case MODE_GAMEPLAY:
                    {
                        p_parent.onKeyPressed(_keyCode);
                    }
            ;
                    break;
                case MODE_RECORDNAME:
                    {
                        //#if VENDOR=="LG" || VENDOR=="SE"
                        // У LG отрабатывается только нажатие софт клавиш, поэтому отработаем их сразу
                        //$switch(_keyCode)
                        //${
                        //$    case -7:
                        //$        {
                        //$            if (i_KeyReactionDelayCounter<=0)
                        //$            {
                        //$            DataStorage.addScoreInTable(ab_ScoreTable, (byte) ai_RecordNameChars[0], (byte) ai_RecordNameChars[1], (byte) ai_RecordNameChars[2], Gamelet.getPlayerScore());
                        //$            try
                        //$            {
                        //$                DataStorage.saveScores(ab_ScoreTable);
                        //$            }
                        //$            catch (Exception e)
                        //$            {
                        //$                s_errorString = e.getMessage();
                        //$               p_parent.setMode(MODE_ERROR);
                        //$                return;
                        //$           }
                        //$           }
                        //$    }
                        //$    case -6:
                        //$        {
                        //$            if (i_KeyReactionDelayCounter<=0)
                        //$            {
                        //$            p_parent.setMode(MODE_MAINMENU);
                        //$            return;
                        //$            }
                        //$        }
                        //$ }
                        //#endif

                        //#if MIDP=="2.0" || VENDOR=="SIEMENS"
                        //$p_InsideCanvas.paintFlush();
                        //#else
                        p_InsideCanvas.repaint();
                        //#endif
                    }
            ;
                    break;
                case MODE_GAMEFINAL:
                    {
                        //#if VENDOR=="LG" || VENDOR=="SE"
                        //$if (_keyCode==-6 || _keyCode==-7)
                        //${
                        //$if (i_KeyReactionDelayCounter<=0)
                        //${
                        //$  i_lastPressedKey = -1;
                        //$  p_parent.setMode(MODE_RECORDNAME);
                        //$}
                        //$}
                        //#endif
                    }
            ;
                    break;
                case MODE_GAMEMENU:
                case MODE_MAINMENU:
                    {
                        switch (_keyCode)
                        {
                                //#if VENDOR=="LG" || VENDOR=="SE"
                                //$// Отработка на телефонах LG бага не дающего событие отпускания софт кнопки
                                //$case -6 :
                                //$    {
                                //$        GameMenu.pressMenuKey(GameMenu.MENUKEY_LEFT);
                                //$        repaint();
                                //$        try
                                //$        {
                                //$            Thread.sleep(200);
                                //$        }
                                //$        catch (Exception e)
                                //$        {
                                //$        }
                                //$        GameMenu.releaseMenuKey(GameMenu.MENUKEY_LEFT);
                                //$    };break;
                                //$case -7 :
                                //$    {
                                //$        GameMenu.pressMenuKey(GameMenu.MENUKEY_RIGHT);
                                //$        repaint();
                                //$        try
                                //$        {
                                //$            Thread.sleep(200);
                                //$        }
                                //$        catch (Exception e)
                                //$        {
                                //$        }
                                //$        GameMenu.releaseMenuKey(GameMenu.MENUKEY_RIGHT);
                                //$    };break;
                                //#endif

                                case KEY_CODE_UP:
                            case JOY_СODE_UP:
                                GameMenu.pressMenuKey(GameMenu.MENUKEY_UP);
                                break;
                            case KEY_CODE_SOFT_LEFT:
                            case KEY_CODE_LEFT:
                            case JOY_CODE_LEFT:
                                GameMenu.pressMenuKey(GameMenu.MENUKEY_LEFT);
                                break;
                            case KEY_CODE_SOFT_RIGHT:
                            case KEY_CODE_RIGHT:
                            case JOY_CODE_RIGHT:
                                GameMenu.pressMenuKey(GameMenu.MENUKEY_RIGHT);
                                break;
                            case KEY_CODE_DOWN:
                            case JOY_CODE_DOWN:
                                GameMenu.pressMenuKey(GameMenu.MENUKEY_DOWN);
                                break;
                            case KEY_CODE_FIRE:
                            case JOY_CODE_FIRE:
                                GameMenu.pressMenuKey(GameMenu.MENUKEY_SELECT);
                                break;
                        }


                        //#if MIDP=="2.0" || VENDOR=="SIEMENS"
                        //$paintFlush();
                        //#else
                        repaint();
                        //#if VENDOR!="SE"
                        serviceRepaints();
                        //#endif
                        //#endif

                    }
            ;
                    break;
            }
        }

        protected synchronized void keyReleased(int _keyCode)
        {
            switch (i_MidletMode)
            {
                case MODE_UNKNOWN:
                    {
                    }
            ;
                    break;
                case MODE_LOADING:
                    {
                        if (_keyCode == KEY_CODE_SOFT_RIGHT)
                            if (i_lastPressedKey == KEY_CODE_SOFT_RIGHT)
                            {
                                // Останавливаем загрузку
                                lg_Working = false;
                            }
                    }
            ;
                    break;
                case MODE_INITED:
                    {
                    }
            ;
                    break;
                case MODE_RECORDNAME:
                    {
                        if (i_KeyReactionDelayCounter > 0) i_KeyReactionDelayCounter--;
                        else
                        {
                            switch (_keyCode)
                            {
                                case KEY_CODE_UP:
                                case JOY_СODE_UP:
                                    {
                                        int i_code = ai_RecordNameChars[i_RecordCharPosition];
                                        if (i_code == LETTER_RECORDNAME_LASTCODE)
                                        {
                                            i_code = LETTER_RECORDNAME_FIRSTCODE;
                                        }
                                        else
                                        {
                                            i_code++;
                                        }
                                        ai_RecordNameChars[i_RecordCharPosition] = i_code;
                                    }
                            ;
                                    break;
                                case KEY_CODE_LEFT:
                                case JOY_CODE_LEFT:
                                    {
                                        if (i_RecordCharPosition == 0)
                                        {
                                            i_RecordCharPosition = ai_RecordNameChars.length - 1;
                                        }
                                        else
                                        {
                                            i_RecordCharPosition--;
                                        }
                                    }
                            ;
                                    break;
                                case KEY_CODE_RIGHT:
                                case JOY_CODE_RIGHT:
                                    {
                                        if (i_RecordCharPosition == (ai_RecordNameChars.length - 1))
                                        {
                                            i_RecordCharPosition = 0;
                                        }
                                        else
                                        {
                                            i_RecordCharPosition++;
                                        }
                                    }
                            ;
                                    break;
                                case KEY_CODE_DOWN:
                                case JOY_CODE_DOWN:
                                    {
                                        int i_code = ai_RecordNameChars[i_RecordCharPosition];
                                        if (i_code == LETTER_RECORDNAME_FIRSTCODE)
                                        {
                                            i_code = LETTER_RECORDNAME_LASTCODE;
                                        }
                                        else
                                        {
                                            i_code--;
                                        }
                                        ai_RecordNameChars[i_RecordCharPosition] = i_code;
                                    }
                            ;
                                    break;
                                case KEY_CODE_SOFT_RIGHT:
                                    {
                                        DataStorage.addScoreInTable(ab_ScoreTable, (byte) ai_RecordNameChars[0], (byte) ai_RecordNameChars[1], (byte) ai_RecordNameChars[2], Gamelet.getPlayerScore());
                                        try
                                        {
                                            DataStorage.saveScores(ab_ScoreTable);
                                        }
                                        catch (Exception e)
                                        {
                                            s_errorString = e.getMessage();
                                            p_parent.setMode(MODE_ERROR);
                                            return;
                                        }
                                    }
                                case KEY_CODE_SOFT_LEFT:
                                    {
                                        p_parent.setMode(MODE_MAINMENU);
                                        return;
                                    }
                            }
                            //#if MIDP=="2.0" || VENDOR=="SIEMENS"
                            //$p_InsideCanvas.paintFlush();
                            //#else
                            p_InsideCanvas.repaint();
                            //#endif
                        }
                    }
            ;
                    break;
                case MODE_GAMEFINAL:
                    {
                        if (i_KeyReactionDelayCounter == 0 && i_lastPressedKey == _keyCode)
                        {
                            i_lastPressedKey = -1;
                            p_parent.setMode(MODE_RECORDNAME);
                        }
                    }
            ;
                    break;
                case MODE_GAMEPLAY:
                    {
                        p_parent.onKeyReleased(_keyCode);
                    }
            ;
                    break;
                case MODE_GAMEMENU:
                case MODE_MAINMENU:
                    {
                        switch (_keyCode)
                        {
                            case KEY_CODE_UP:
                            case JOY_СODE_UP:
                                GameMenu.releaseMenuKey(GameMenu.MENUKEY_UP);
                                break;
                            case KEY_CODE_SOFT_LEFT:
                            case KEY_CODE_LEFT:
                            case JOY_CODE_LEFT:
                                GameMenu.releaseMenuKey(GameMenu.MENUKEY_LEFT);
                                break;
                            case KEY_CODE_SOFT_RIGHT:
                            case KEY_CODE_RIGHT:
                            case JOY_CODE_RIGHT:
                                GameMenu.releaseMenuKey(GameMenu.MENUKEY_RIGHT);
                                break;
                            case KEY_CODE_DOWN:
                            case JOY_CODE_DOWN:
                                GameMenu.releaseMenuKey(GameMenu.MENUKEY_DOWN);
                                break;
                            case KEY_CODE_FIRE:
                            case JOY_CODE_FIRE:
                                GameMenu.releaseMenuKey(GameMenu.MENUKEY_SELECT);
                                break;
                        }


                        //#if MIDP=="2.0" || VENDOR=="SIEMENS"
                        //$p_InsideCanvas.paintFlush();
                        //#else
                        p_InsideCanvas.repaint();
                        //#endif

                    }
            ;
                    break;
            }
        }
    }

    public startup()
    {
        InsideCanvas.p_parent = this;
    }

    private static int i_lastPressedKey = -1;

    //#if VENDOR=="SAMSUNG"
    //$protected static Image p_bfImg;
    //#endif

    private static int i_MidletMode = -1;
    private static int i_PrevMidletMode;
    private static boolean lg_Working;

    private static int i_GameOptionalValue1;
    private static int i_GameOptionalValue2;

    private static int i_startStageGameOptionalValue1;
    private static int i_startStageGameOptionalValue2;

    private static int i_KeyReactionDelayCounter;

    private static int i_LoadingProgress;

    protected static final InsideCanvas p_InsideCanvas = new InsideCanvas();
    private static Class p_This;
    private static startup p_ThisClass;

    protected static Display p_Display;

    private static Image p_Image_LoadingLogo;

    //#if MCF_SPLASH
    //$private static MCFFormRender p_Image_Splash;
    //#else
    private static Image p_Image_Splash;
    //#endif

    protected static boolean lg_Option_Sound;
    protected static boolean lg_Option_Vibra;
    protected static boolean lg_Option_Light;
    protected static int i_LanguageIndex;

    private static int i_KeyFlags;
    private static int i_selectedGameLevel;
    private static int i_selectedGameStage;
    private static int i_CurrentModeTickCounter;

    private static byte[] ab_ScoreTable;
    private static final int[] ai_RecordNameChars = new int [3];

    private static int i_RecordCharPosition;
    private static String s_errorString;

    private static final void initRecordName()
    {
        ai_RecordNameChars[0] = LETTER_RECORDNAME_FIRSTCODE;
        ai_RecordNameChars[1] = LETTER_RECORDNAME_FIRSTCODE;
        ai_RecordNameChars[2] = LETTER_RECORDNAME_FIRSTCODE;
        i_RecordCharPosition = 0;
    }

    //#ifdefined DEMO_STRING
    //$private static final int DEMO_DELAY = 300;
    //$private static int i_demoGameCounter = 0;
    //#endif

    private static final void setMode(int _newMode)
    {
        if (i_MidletMode == _newMode) return;
        if (i_MidletMode == MODE_ERROR || i_MidletMode == MODE_RELEASING) return;

        i_lastPressedKey = -1;

        i_CurrentModeTickCounter = 0;

        i_PrevMidletMode = i_MidletMode;
        i_MidletMode = _newMode;

        boolean lg_cycle = true;
        boolean lg_setScreenTime = false;
        int i_ScreenDelay = 0;

        while (lg_cycle)
        {
            switch (i_MidletMode)
            {
                case MODE_UNKNOWN:
                    {
                        lg_cycle = false;
                    }
            ;
                    break;
                case MODE_INITED:
                    {
                        //#if DEALERDELAY>0
                        if (i_PrevMidletMode != MODE_INITED)
                        {
                            try
                            {
                                p_Image_LoadingLogo = Image.createImage(RESOURCE_DEALER);
                            }
                            catch (Exception e)
                            {
                                p_Image_LoadingLogo = null;
                            }
                        }
                        //#endif
                        p_Display.setCurrent(p_InsideCanvas);
                        lg_cycle = false;
                    }
            ;
                    break;
                case MODE_LOADING:
                    {
                        if (i_PrevMidletMode != MODE_LOADING)
                        {
                            i_LoadingProgress = 0;
                            try
                            {
                                p_Image_LoadingLogo = Image.createImage(RESOURCE_LOADING_LOGO);
                            }
                            catch (Exception e)
                            {
                            }
                        }
                        lg_cycle = false;
                    }
            ;
                    break;
                case MODE_SHOWSTAGE:
                    {
                        if ((Gamelet.getSupportedModes() & Gamelet.FLAG_STAGESUPPORT) == 0)
                        {
                            i_MidletMode = MODE_GAMEPLAY;
                            continue;
                        }
                        lg_setScreenTime = true;
                        i_ScreenDelay = DELAY_STAGESCREEN;
                        lg_cycle = false;
                    }
            ;
                    break;
                case MODE_GAMEPLAY:
                    {
                        //#ifdefined DEMO_STRING
                        //$if (i_PrevMidletMode == MODE_MAINMENU || i_PrevMidletMode == MODE_SHOWSTAGE) i_demoGameCounter = DEMO_DELAY;
                        //#endif

                        //#if MCF_SPLASH
                        //$if (p_Image_Splash!=null) p_Image_Splash.realize();
                        //#endif
                        p_Image_Splash = null;
                        lg_cycle = false;

                        if (i_PrevMidletMode == MODE_SHOWSTAGE) Gamelet.resumeGameAfterPause();
                    }
            ;
                    break;
                case MODE_GAMEMENU:
                    {
                        Gamelet.pauseGame();
                        GameMenu.focusToItem(ITEM_ID_ResumeGame);
                        lg_cycle = false;
                    }
            ;
                    break;
                case MODE_RELEASING:
                case MODE_ERROR:
                    {
                        lg_cycle = false;
                    }
            ;
                    break;
                case MODE_GAMEFINAL:
                    {
                        //#if IMAGES_DYNAMIC || IMAGES_EXTERNAL
                        //#if !((VENDOR=="SE" && MODEL=="T610") || (VENDOR=="SAMSUNG" && MODEL=="X100")|| (VENDOR=="SAMSUNG" && MODEL=="C100") || (VENDOR=="LG" && MODEL=="G1600"))
                        ImageManager.clearDynamicCahce();
                        //#endif
                        //#endif

                        onReleaseGame();
                        Gamelet.releaseGame();

                        Runtime.getRuntime().gc();

                        final String s_imageResource = Gamelet.i_PlayerState == Gamelet.PLAYER_WIN ? RESOURCE_WINIMAGE : RESOURCE_LOSTIMAGE;
                        try
                        {
                            //#if MCF_SPLASH
                            //$p_Image_Splash = new MCFFormRender();
                            //$p_Image_Splash.init(p_This,s_imageResource);
                            //$p_Image_Splash.selectForm(0);
                            //#else
                            p_Image_Splash = Image.createImage(s_imageResource);
                            //#endif
                        }
                        catch (Exception e)
                        {
                            p_Image_Splash = null;
                        }

                        lg_setScreenTime = true;
                        i_ScreenDelay = DELAY_FINALSCREEN;
                        i_KeyReactionDelayCounter = REACTIONDELAY_GAMEFINAL;
                        lg_cycle = false;
                    }
            ;
                    break;
                case MODE_RECORDNAME:
                    {
                        initRecordName();
                        try
                        {
                            ab_ScoreTable = DataStorage.getTopScores();
                            //#if DEBUG
                            System.out.println("Score : " + Gamelet.getPlayerScore());
                            //#endif

                            if (Gamelet.getPlayerScore() < 0 || !DataStorage.checkScores(ab_ScoreTable, Gamelet.getPlayerScore()))
                            {
                                ab_ScoreTable = null;
                                i_MidletMode = MODE_MAINMENU;
                                continue;
                            }
                            i_KeyReactionDelayCounter = REACTIONDELAY_RECORD;
                        }
                        catch (Exception e)
                        {
                            s_errorString = e.getMessage();
                            i_MidletMode = MODE_ERROR;
                        }
                        lg_cycle = false;
                    }
            ;
                    break;
                case MODE_MAINMENU:
                    {
                        if (i_PrevMidletMode == MODE_GAMEMENU || i_PrevMidletMode == MODE_GAMEPLAY)
                        {
                            onReleaseGame();
                            Gamelet.releaseGame();
                        }
                        p_Image_LoadingLogo = null;
                        i_selectedGameLevel = -1;
                        ab_ScoreTable = null;
                        try
                        {
                            //#if MCF_SPLASH
                            //$if (p_Image_Splash==null) p_Image_Splash = new MCFFormRender();
                            //$p_Image_Splash.realize();
                            //$p_Image_Splash.init(p_This,RESOURCE_SPLASH);
                            //$p_Image_Splash.selectForm(0);
                            //#else
                            p_Image_Splash = null;
                            Runtime.getRuntime().gc();
                            p_Image_Splash = Image.createImage(RESOURCE_SPLASH);
                            //#endif
                        }
                        catch (Exception e)
                        {
                        }
                        lg_cycle = false;

                        if (DataStorage.hasSavedData())
                        {
                            GameMenu.focusToItem(ITEM_ID_ResumeGame);
                        }
                        else
                        {
                            GameMenu.focusToItem(ITEM_ID_NewGame);
                        }
                    }
            ;
                    break;
                default:
                    {
                        //#if DEBUG
                        System.out.println("Unknown mode [" + i_MidletMode + "]");
                        //#endif
                    }
            }

            if (lg_setScreenTime)
            {
                long l_currentTime = System.currentTimeMillis();
                long l_endTime = l_currentTime + i_ScreenDelay;
                long l_dt = l_endTime - System.currentTimeMillis();
                if (l_dt < 0 || l_dt > i_ScreenDelay) l_endTime = System.currentTimeMillis() + 2000;
                l_EndTimeForScreen = l_endTime;
            }
        }

        i_lastPressedKey = -1;

        onMidletModeChanged(i_MidletMode, i_PrevMidletMode);

        if (p_InsideCanvas.isShown())
        {
            //#if MIDP=="2.0" || VENDOR=="SIEMENS"
            //$p_InsideCanvas.paintFlush();
            //#else
            p_InsideCanvas.repaint();
            //#endif
        }
        Runtime.getRuntime().gc();
    }

    /**
     * Сгенерировать форму таблицы игровых рекордов
     *
     * @param _scoreTable массив, содержащий таблицу
     * @return форму, содержащую список рекордов
     */
    private static final Displayable makeScoreTableForm(byte[] _scoreTable)
    {
        //#if VENDOR=="LG" || (VENDOR=="NOKIA" && (MODEL=="3410" || MODEL=="3510"))
        StringBuffer p_strBuff = new StringBuffer();
        for (int li = 0; li < DataStorage.MAX_SCORE_RECORDS; li++)
        {
            String s_name = DataStorage.getNameInPosition(_scoreTable, li);
            if (s_name == null) break;
            int i_scores = DataStorage.getTopScoresInPosition(_scoreTable, li);

            p_strBuff.append("" + (li + 1));
            p_strBuff.append('.');
            p_strBuff.append(s_name);
            p_strBuff.append("...");
            p_strBuff.append(i_scores);

            p_strBuff.append('\n');
        }

        TextForm p_form = new TextForm(LangBlock.getStringForIndex(TopTXT), p_strBuff.toString(), false);
        p_strBuff = null;
        //#else
        //$List p_form = new List(LangBlock.getStringForIndex(TopTXT), List.IMPLICIT);
        //$for (int li = 0; li < DataStorage.MAX_SCORE_RECORDS; li++)
        //${
        //$    String s_name = DataStorage.getNameInPosition(_scoreTable, li);
        //$    if (s_name == null) break;
        //$    int i_scores = DataStorage.getTopScoresInPosition(_scoreTable, li);
        //$    StringBuffer p_strBuff = new StringBuffer();

        //#if !(VENDOR=="SAMSUNG" && MODEL=="X100")
        // На Samsung X100 строки в списке автоматически нумеруются
        //$p_strBuff.append("" + (li + 1));
        //$p_strBuff.append('.');
        //#endif
        //$    p_strBuff.append(s_name);
        //$    p_strBuff.append("...");
        //$    p_strBuff.append(i_scores);

        //$    String s_str = p_strBuff.toString();
        //$    p_form.append(s_str, null);
        //$}

        //$if (p_form.size() == 0) p_form.append(" ", null); // Вставлено из-за неотображения пустой формы на X100

        //$p_form.addCommand(new Command(LangBlock.getStringForIndex(BackTXT), Command.SCREEN, 1));
        //$p_form.setCommandListener(p_ThisClass);
        //#endif
        return p_form;
    }

    /**
     * Сгенерировать форму помощи по игре или информацию по игре
     *
     * @param _help флаг, показывающий что надо генерировать помощь по игре, если false то about
     * @return форму, содержащую текст помощи или информацию
     */
    private static final Displayable makeHelpOrAboutBox(boolean _help)
    {
        String s_str = null;
        String s_titl = null;
        if (_help)
        {
            s_str = LangBlock.getStringForIndex(HelpTextTXT);
            s_titl = LangBlock.getStringForIndex(HelpTXT);
        }
        else
        {
            s_str = LangBlock.getStringForIndex(AboutTextTXT);
            s_titl = LangBlock.getStringForIndex(AboutTXT);
        }

        s_str = s_str.replace('~', '\n');

        //#if VENDOR=="LG" || (VENDOR=="NOKIA" && (MODEL=="3410" || MODEL=="3510"))
        TextForm p_tbox = new TextForm(s_titl, s_str, true);
        //#else
        //$  Form p_tbox = new Form(s_titl);
        //$  StringItem p_str = new StringItem(null, s_str);
        //$  p_tbox.append(p_str);
        //$  p_tbox.addCommand(new Command(LangBlock.getStringForIndex(BackTXT), Command.SCREEN, 1));
        //$  p_tbox.setCommandListener(p_ThisClass);
        //#endif


        return p_tbox;
    }

    public void commandAction(Command command, Displayable displayable)
    {
        p_Display.setCurrent(p_InsideCanvas);
    }

    //#if VIBRA
    protected static final void startVibration(int _timedelay)
    {
        if (!lg_Option_Vibra) return;
        //#if VENDOR=="LG"
        //$mmpp.media.Vibration.start(mmpp.media.Vibration.getLevelNum(),_timedelay);
        //#endif
        //#if VENDOR=="SE" || VENDOR=="MOTOROLA"

        //#endif
        //#if VENDOR=="NOKIA"
        //$try
        //${
        //$    com.nokia.mid.ui.DeviceControl.startVibra(70,_timedelay);
        //$}
        //$catch (Exception e)
        //${
        //$}
        //#endif
        //#if VENDOR=="SIEMENS"
        //$com.siemens.mp.game.Vibrator.triggerVibrator(_timedelay);
        //#endif
        //#if VENDOR=="SAMSUNG"
        //$if (com.samsung.util.Vibration.isSupported())
        //${
        //$    com.samsung.util.Vibration.start(_timedelay,5);
        //$}
        //#endif
    }
    //#endif

    protected void startApp() throws MIDletStateChangeException
    {
        p_ThisClass = this;
        p_This = this.getClass();
        if (i_MidletMode < 0)
        {
            if (!onStartApp(this))
            {
                throw new MIDletStateChangeException("E0");
            }

            setMode(MODE_UNKNOWN);
            p_Display = Display.getDisplay(this);

            lg_Working = true;

            setMode(MODE_INITED);

            new Thread(this).start();
        }
    }

    protected void pauseApp()
    {
        switch (i_MidletMode)
        {
            case MODE_SHOWSTAGE:
            case MODE_GAMEPLAY:
                {
                    setMode(MODE_GAMEMENU);
                }
        ;
                break;
        }
    }

    private static final void saveCurrentGame()
    {
        try
        {
            Gamelet.pauseGame();
            byte[] ab_array = Gamelet.saveGameStateToByteArray();

            long l_accum = ((long) i_GameOptionalValue1 << 32) | ((long) i_GameOptionalValue2 & 0xFFFFFFFFl);
            for (int li = 7; li >= 0; li--)
            {
                byte b_vaue = (byte) l_accum;
                l_accum >>>= 8;
                ab_array[li] = b_vaue;
            }

            l_accum = ((long) i_startStageGameOptionalValue1 << 32) | ((long) i_startStageGameOptionalValue2 & 0xFFFFFFFFl);
            for (int li = 15; li >= 8; li--)
            {
                byte b_vaue = (byte) l_accum;
                l_accum >>>= 8;
                ab_array[li] = b_vaue;
            }

            DataStorage.saveDataBlock(ab_array);
            ab_array = null;
            Runtime.getRuntime().gc();
        }
        catch (Exception e)
        {
            e.printStackTrace();
            s_errorString = e.getMessage();
            setMode(MODE_ERROR);
        }
    }

    private static final void loadGame()
    {
        try
        {
            byte[] ab_array = DataStorage.loadDataBlock();

            long l_accum = 0;
            for (int li = 0; li < 8; li++)
            {
                l_accum = l_accum << 8;
                l_accum |= ((long) (ab_array[li] & 0xFF));
            }
            int i_GOValue1 = (int) (l_accum >>> 32);
            int i_GOValue2 = (int) l_accum;

            l_accum = 0;
            for (int li = 8; li < 16; li++)
            {
                l_accum = l_accum << 8;
                l_accum |= ((long) (ab_array[li] & 0xFF));
            }
            int i_sSGOValue1 = (int) (l_accum >>> 32);
            int i_sSGOValue2 = (int) l_accum;


            //#if SHOWSYS
            System.out.println("Load game array from storage");
            //#endif

            Gamelet.loadGameStateFromByteArray(ab_array);
            i_selectedGameStage = Gamelet.i_GameStage;
            i_selectedGameLevel = Gamelet.i_GameLevel;

            //#if SHOWSYS
            System.out.println("Game level=" + i_selectedGameLevel + " Game stage=" + i_selectedGameStage);
            //#endif

            if (!onInitNewGame(p_This, i_selectedGameLevel)) throw new Exception("E0");
            if (!onInitNewGameStage(i_selectedGameStage)) throw new Exception("E1");

            i_startStageGameOptionalValue1 = i_sSGOValue1;
            i_startStageGameOptionalValue2 = i_sSGOValue2;
            i_GameOptionalValue1 = i_GOValue1;
            i_GameOptionalValue2 = i_GOValue2;

            ab_array = null;
            Runtime.getRuntime().gc();
        }
        catch (Exception e)
        {
            s_errorString = e.getMessage();
            setMode(MODE_ERROR);
        }
    }

    protected static final void increaseLoadingProgress(int _deltaPercents)
    {
        i_LoadingProgress += _deltaPercents;

        //#if SHOWSYS
        System.out.println("loading " + i_LoadingProgress);
        //#endif

        //#if MIDP=="2.0" || VENDOR=="SIEMENS"
        //$p_InsideCanvas.paintFlush();
        //#else
        p_InsideCanvas.repaint();
        //#if VENDOR!="SE"
        p_InsideCanvas.serviceRepaints();
        //#endif
        //#endif

        //#if DEBUG
        try
        {
            Thread.sleep(500);
        }
        catch (InterruptedException e)
        {
        }
        //#endif
    }

    protected void destroyApp(boolean b) throws MIDletStateChangeException
    {
        lg_Working = false;
        while (i_MidletMode != MODE_RELEASING)
        {
            try
            {
                Thread.sleep(30);
            }
            catch (Exception e)
            {
                break;
            }
        }
        p_This = null;
        p_Display = null;
        p_ThisClass = null;
        System.gc();
        notifyDestroyed();
    }

    /**
     * Функция разрешения отображения пункта и подпункта меню
     *
     * @param _itemId    идентификатор пункта меню
     * @param _subitemId идентификатор подпункта меню, если пункт меню то -1
     * @return true если разрешен и false если запрещен
     */
    public static final boolean onEnable(int _itemId, int _subitemId)
    {
        if (_subitemId < 0)
        {
            switch (_itemId)
            {
                case ITEM_ID_Exit:
                    if (i_MidletMode == MODE_MAINMENU) return true;
                    else return false;
                case ITEM_ID_EndGame:
                    if (i_MidletMode == MODE_GAMEMENU) return true;
                    else return false;
                case ITEM_ID_ResumeGame:
                    {
                        switch (i_MidletMode)
                        {
                            case MODE_GAMEMENU:
                                {
                                    return true;
                                }
                            case MODE_MAINMENU:
                                {
                                    if (DataStorage.hasSavedData()) return true;
                                    else return false;
                                }
                        }
                    }
            ;
                    break;
                case ITEM_ID_RestartLevel:
                    {
                        if (i_MidletMode != MODE_GAMEMENU) return false;
                        if ((Gamelet.getSupportedModes() & Gamelet.FLAG_SUPPORTRESTART) == 0) return false;
                        return true;
                    }
                case ITEM_ID_NewGame:
                case ITEM_ID_Top:
                case ITEM_ID_Language:
                    {
                        if (i_MidletMode == MODE_GAMEMENU) return false;
                        else return true;
                    }
            }
        }
        else
        {
            if (_itemId == ITEM_ID_Options)
            {
                switch (_subitemId)
                {
                    case SUBITEM_ID_Sound:
                        {
                            //#if SOUND
                            return true;
                            //#else
                            //$return false;
                            //#endif
                        }
                    case SUBITEM_ID_Vibra:
                        {
                            //#if VIBRA
                            return true;
                            //#else
                            //$return false;
                            //#endif
                        }
                    case SUBITEM_ID_Light:
                        {
                            //#if LIGHT
                            return true;
                            //#else
                            //$return false;
                            //#endif
                        }
                }
            }
        }
        return true;
    }

    /**
     * Отработка активизации пункта меню
     *
     * @param _itemId идентификатор пункта меню
     */
    public static final void onEnter(int _itemId)
    {

    }

    /**
     * Отработка деактивизации пункта меню
     *
     * @param _itemId идентификатор пункта меню
     */
    public static final void onExit(int _itemId)
    {

    }

    /**
     * Отработка изменения состояния подпункта или пункта
     *
     * @param _itemId    идентификатор пункта
     * @param _subitemId идентификатор подпункта, если только пункт то -1
     * @param _newState  новое состояние пункта, true если выбран и false если не выбран
     */
    public static final void onState(int _itemId, int _subitemId, boolean _newState)
    {
        if (_subitemId < 0 && _newState)
        {
            switch (_itemId)
            {
                case ITEM_ID_Exit:
                    {
                        lg_Working = false;
                    }
            ;
                    break;
                case ITEM_ID_EndGame:
                    {
                        //#if IMAGES_DYNAMIC || IMAGES_EXTERNAL
                        //#if !((VENDOR=="SE" && MODEL=="T610") || (VENDOR=="SAMSUNG" && MODEL=="X100")|| (VENDOR=="SAMSUNG" && MODEL=="C100") || (VENDOR=="LG" && MODEL=="G1600"))
                        ImageManager.clearDynamicCahce();
                        //#endif
                        //#endif

                        saveCurrentGame();
                        setMode(MODE_MAINMENU);
                    }
            ;
                    break;
                case ITEM_ID_RestartLevel:
                    {
                        i_GameOptionalValue1 = i_startStageGameOptionalValue1;
                        i_GameOptionalValue2 = i_startStageGameOptionalValue2;
                        Gamelet.rollbackStage();
                        setMode(MODE_SHOWSTAGE);
                        LoadingThread.loadStageData(i_selectedGameStage);
                        onStageRestart();
                    }
            ;
                    break;
                case ITEM_ID_ResumeGame:
                    {
                        if (i_MidletMode == MODE_MAINMENU)
                        {
                            loadGame();
                            onAfterGameLoading();
                        }
                        Gamelet.i_GameState = Gamelet.STATE_PAUSED;
                        Gamelet.resumeGameAfterPause();
                        setMode(MODE_GAMEPLAY);
                    }
            ;
                    break;
                case ITEM_ID_About:
                    {
                        Displayable p_abForm = makeHelpOrAboutBox(false);
                        p_Display.setCurrent(p_abForm);
                    }
            ;
                    break;
                case ITEM_ID_Help:
                    {
                        Displayable p_helpForm = makeHelpOrAboutBox(true);
                        p_Display.setCurrent(p_helpForm);
                    }
            ;
                    break;
                case ITEM_ID_Top:
                    {
                        try
                        {
                            byte[] ab_sTable = DataStorage.getTopScores();
                            Displayable p_listScores = makeScoreTableForm(ab_sTable);
                            ab_sTable = null;
                            p_Display.setCurrent(p_listScores);
                        }
                        catch (Exception e)
                        {
                            s_errorString = e.getMessage();
                            setMode(MODE_ERROR);
                            return;
                        }
                    }
            ;
                    break;
            }
        }
        else
        {
            switch (_itemId)
            {
                case ITEM_ID_NewGame:
                    {
                        i_selectedGameStage = STAGENUMBER_FIRST;
                        switch (_subitemId)
                        {
                            case SUBITEM_ID_Easy:
                                {
                                    i_selectedGameLevel = Gamelet.GAMELEVEL_EASY;
                                }
                        ;
                                break;
                            case SUBITEM_ID_Normal:
                                {
                                    i_selectedGameLevel = Gamelet.GAMELEVEL_NORMAL;
                                }
                        ;
                                break;
                            case SUBITEM_ID_Hard:
                                {
                                    i_selectedGameLevel = Gamelet.GAMELEVEL_HARD;
                                }
                        ;
                                break;
                        }
                    }
            ;
                    break;
                case ITEM_ID_Options:
                    {
                        switch (_subitemId)
                        {
                                //#if LIGHT
                                case SUBITEM_ID_Light:
                                {
                                    lg_Option_Light = _newState;
                                }
                        ;
                                break;
                                //#endif

                                //#if SOUND
                                case SUBITEM_ID_Sound:
                                {
                                    lg_Option_Sound = _newState;
                                    if (!lg_Option_Sound) SoundManager.stopAllSound();
                                }
                        ;
                                break;
                                //#endif
                                //#if VIBRA
                                case SUBITEM_ID_Vibra:
                                {
                                    lg_Option_Vibra = _newState;
                                }
                        ;
                                break;
                                //#endif
                        }

                        packAndSaveOptions();
                    }
            ;
                    break;
                case ITEM_ID_Language:
                    {
                        if (_newState)
                        {
                            int i_currentLanguage = LangBlock.i_CurrentLanguageIndex;
                            int i_newLanguage = _subitemId;

                            try
                            {
                                LangBlock.setLanguage(p_This, i_newLanguage);
                            }
                            catch (Exception e)
                            {
                                i_newLanguage = i_currentLanguage;
                                try
                                {
                                    LangBlock.setLanguage(p_This, i_newLanguage);
                                }
                                catch (Exception e1)
                                {
                                    lg_Working = false;
                                }
                            }
                            i_LanguageIndex = i_newLanguage;
                            packAndSaveOptions();

                            GameMenu.focusToItem(ITEM_ID_Language);
                            //#if MIDP=="2.0" || VENDOR=="SIEMENS"
                            //$p_InsideCanvas.paintFlush();
                            //#else
                            p_InsideCanvas.repaint();
                            //#if VENDOR!="SE"
                            p_InsideCanvas.serviceRepaints();
                            //#endif
                            //#endif
                        }
                    }
            ;
                    break;
            }
        }
    }

    protected static final void packAndSaveOptions()
    {
        try
        {
            DataStorage.ab_OptionsArray[0] = (byte) i_LanguageIndex;
            int i_packValue = (lg_Option_Light ? 1 : 0) | (lg_Option_Sound ? 2 : 0) | (lg_Option_Vibra ? 4 : 0);
            DataStorage.ab_OptionsArray[1] = (byte) i_packValue;
            DataStorage.saveOptions();
        }
        catch (Exception e)
        {
        }
    }

    /**
     * Запрашивает состояние выводимого подпункта для CHECKBOX, RADIOLIST
     *
     * @param _itemId    идентификатор пункта меню
     * @param _subitemId идентификатор подпункта меню
     * @return false если не выбран и true если выбран
     */
    public static final boolean isSelected(int _itemId, int _subitemId)
    {
        switch (_itemId)
        {
            case ITEM_ID_Options:
                {
                    switch (_subitemId)
                    {
                        case SUBITEM_ID_Sound:
                            return lg_Option_Sound;
                        case SUBITEM_ID_Vibra:
                            return lg_Option_Vibra;
                        case SUBITEM_ID_Light:
                            return lg_Option_Light;
                    }
                }
        ;
                break;
        }
        return false;
    }

    /**
     * Заполнение сабитемов для настраемового пункта
     *
     * @param _itemId       идентификатор пункта меню
     * @param _subitemIndex номер подпункта меню
     * @return возвращает запакованное значение подпункта
     */
    public static final int onCustom(int _itemId, int _subitemIndex)
    {
        switch (_itemId)
        {
            case ITEM_ID_Language:
                {
                    if (_subitemIndex >= LangBlock.i_LanguageNumber) return 0;

                    int i_selected = 0;

                    if (LangBlock.i_CurrentLanguageIndex == _subitemIndex)
                    {
                        i_selected = 0xFF;
                    }

                    int i_textid = LangBlock.i_FirstLanguageIndex + _subitemIndex;
                    int i_flags = 0;

                    int i_result = (i_flags << GameMenu.OFFSET_FLAGS) | (_subitemIndex << GameMenu.OFFSET_ID) | (i_selected << GameMenu.OFFSET_SELECTED) | (i_textid << GameMenu.OFFSET_TEXT);
                    return i_result;
                }
        }
        return 0;
    }


    public void run()
    {
        LoadingThread p_LoadingThread = new LoadingThread(this);

        //#if DEALERDELAY>0
        LoadingThread.go();
        if (p_Image_LoadingLogo == null)
        {
            // Отображаем иконку дилера
            //#if MIDP=="2.0" || VENDOR=="SIEMENS"
            //$p_InsideCanvas.paintFlush();
            //#else
            p_InsideCanvas.repaint();
            p_InsideCanvas.serviceRepaints();
            //#endif
            try
            {
                //$Thread.sleep(/*$DEALERDELAY$*/);
                //#-
                Thread.sleep(2000);
                //#+
            }
            catch (InterruptedException e)
            {
            }
        }
        setMode(MODE_LOADING);
        //#else
        //$setMode(MODE_LOADING);
        //$LoadingThread.go();
        //#endif

        // Если загрузка прошла быстрее чем был показан логотип дистрибутора, то выставляем принудительный показ нашего логотипа на 3 секунды
        int i_waitDelay = LoadingThread.lg_isCompleted ? 3000 : 300;

        try
        {
            while (!LoadingThread.lg_isCompleted)
            {
                Thread.sleep(100);
            }

            if (LoadingThread.s_ErrorCode != null)
            {
                s_errorString = LoadingThread.s_ErrorCode;
                setMode(MODE_ERROR);

                Thread.sleep(3000);

                notifyDestroyed();
                return;
            }

            //#if MIDP=="2.0" || VENDOR=="SIEMENS"
            //$p_InsideCanvas.paintFlush();
            //#else
            p_InsideCanvas.repaint();
            Thread.sleep(i_waitDelay);
            //#endif
        }
        catch (Exception _exx)
        {
            return;
        }


        p_LoadingThread = null;
        Runtime.getRuntime().gc();

        setMode(MODE_MAINMENU);
        //setMode(MODE_RECORDNAME);

        while (lg_Working)
        {
            int i_timedelay = 100;
            switch (i_MidletMode)
            {

                case MODE_MAINMENU:
                    {
                        if (i_selectedGameLevel >= 0)
                        {
                            i_selectedGameStage = STAGENUMBER_FIRST;
                            if (!Gamelet.initNewGame(GAMESCREEN_WIDTH, GAMESCREEN_HEIGHT, i_selectedGameLevel) || !onInitNewGame(p_This, i_selectedGameLevel))
                            {
                                s_errorString = "Err1231"; // Can't init new game
                                setMode(MODE_ERROR);
                                continue;
                            }

                            if ((Gamelet.getSupportedModes() & Gamelet.FLAG_STAGESUPPORT) != 0)
                            {
                                LoadingThread.loadStageData(i_selectedGameStage);
                            }

                            setMode(MODE_SHOWSTAGE);
                            i_KeyFlags = 0;
                        }
                        else
                        {
                            i_timedelay = 200;
                            if (GameMenu.processMenu())
                            {
                                //#if MIDP=="2.0" || VENDOR=="SIEMENS"
                                //$p_InsideCanvas.paintFlush();
                                //#else
                                p_InsideCanvas.repaint();
                                //#if VENDOR!="SE"
                                p_InsideCanvas.serviceRepaints();
                                //#endif
                                //#endif
                            }
                        }
                    }
            ;
                    break;
                case MODE_RECORDNAME:
                    {
                        if (i_KeyReactionDelayCounter > 0) i_KeyReactionDelayCounter--;
                    }
            ;
                    break;
                case MODE_GAMEMENU:
                    {
                        i_timedelay = 200;
                        if (GameMenu.processMenu())
                        {
                            //#if MIDP=="2.0" || VENDOR=="SIEMENS"
                            //$p_InsideCanvas.paintFlush();
                            //#else
                            p_InsideCanvas.repaint();
                            //#if VENDOR!="SE"
                            p_InsideCanvas.serviceRepaints();
                            //#endif
                            //#endif
                        }
                    }
            ;
                    break;
                case MODE_SHOWSTAGE:
                    {
                        i_timedelay = Gamelet.I_TIMEDELAY;

                        if (LoadingThread.lg_isCompleted)
                        {
                            if (LoadingThread.s_ErrorCode != null)
                            {
                                s_errorString = LoadingThread.s_ErrorCode;
                                setMode(MODE_ERROR);
                                continue;
                            }
                            if (System.currentTimeMillis() >= l_EndTimeForScreen) setMode(MODE_GAMEPLAY);
                        }

                        i_GameOptionalValue1 += I8_SCROLLTILE_SPEED;

                        //#if DBUFFER
                        //$onPaintGameStage(p_InsideCanvas.p_doubleGraphics);
                        //#endif

                        //#if DBUFFER && (VENDOR=="SIEMENS" && MODEL=="M50")
                        //$p_InsideCanvas.p_doubleBuffer.blitToScreen(p_InsideCanvas.i_screenOffsetX,p_InsideCanvas.i_screenOffsetY);
                        //#else

                        //#if MIDP=="2.0" || VENDOR=="SIEMENS"
                        //$p_InsideCanvas.paintFlush();
                        //#else
                        p_InsideCanvas.repaint();
                        //#if VENDOR!="SE"
                        p_InsideCanvas.serviceRepaints();
                        //#endif
                        //#endif
                        //#endif
                    }
            ;
                    break;
                case MODE_GAMEFINAL:
                    {
                        if (i_KeyReactionDelayCounter > 0) i_KeyReactionDelayCounter--;
                        if (System.currentTimeMillis() >= l_EndTimeForScreen)
                        {
                            setMode(MODE_RECORDNAME);
                        }
                    }
            ;
                    break;
                case MODE_GAMEPLAY:
                    {
                        //#ifdefined DEMO_STRING
                        //$if (i_demoGameCounter<0)
                        //${
                        //$    setMode(MODE_MAINMENU);
                        //$    continue;
                        //$}
                        //$else
                        //$i_demoGameCounter--;
                        //#endif

                        int i_gameDelay = Gamelet.I_TIMEDELAY;
                        long l_startTime = System.currentTimeMillis();

                        //#if SHOWSYS
                        i_Profil_TimeNextGameStep = (int) l_startTime & 0xFFFFFF;
                        //#endif

                        int i_gameletState = Gamelet.nextGameStep(i_KeyFlags);

                        onGameStep();

                        //#if SHOWSYS
                        i_Profil_TimeNextGameStep = ((int) System.currentTimeMillis() & 0xFFFFFF) - i_Profil_TimeNextGameStep;
                        //#endif

                        if (i_gameletState == Gamelet.STATE_OVER)
                        {
                            //#ifdefined DEMO_STRING
                            //$setMode(MODE_MAINMENU);
                            //#else
                            DataStorage.resetSavedDataFlag();
                            switch (Gamelet.i_PlayerState)
                            {
                                case Gamelet.PLAYER_LOST:
                                    {
                                        //#if SHOWSYS
                                        System.out.println("Player lost");
                                        //#endif
                                        setMode(MODE_GAMEFINAL);
                                    }
                            ;
                                    break;
                                case Gamelet.PLAYER_WIN:
                                    {
                                        //#if SHOWSYS
                                        System.out.println("Player win");
                                        //#endif

                                        if ((Gamelet.getSupportedModes() & Gamelet.FLAG_STAGESUPPORT) != 0 && i_selectedGameStage < STAGENUMBER_LAST)
                                        {
                                            if (i_selectedGameStage == STAGENUMBER_LAST)
                                            {
                                                setMode(MODE_GAMEFINAL);
                                            }
                                            else
                                            {
                                                i_selectedGameStage++;
                                                setMode(MODE_SHOWSTAGE);

                                                LoadingThread.loadStageData(i_selectedGameStage);
                                            }
                                        }
                                        else
                                            setMode(MODE_GAMEFINAL);
                                    }
                            ;
                                    break;
                            }
                            //#endif
                        }
                        else
                        {
                            //#if SHOWSYS
                            int i_tempProfValue = (int) System.currentTimeMillis() & 0xFFFFFF;
                            //#endif

                            //#if DBUFFER
                            //$onPaintGame(p_InsideCanvas.p_doubleGraphics);
                            //#endif

                            //#if DBUFFER && (VENDOR=="SIEMENS" && MODEL=="M50")
                            //$p_InsideCanvas.p_doubleBuffer.blitToScreen(p_InsideCanvas.i_screenOffsetX,p_InsideCanvas.i_screenOffsetY);
                            //#else

                            //#if MIDP=="2.0" || VENDOR=="SIEMENS"
                            //$p_InsideCanvas.paintFlush();
                            //#else
                            p_InsideCanvas.repaint();
                            //#if VENDOR!="SE"
                            p_InsideCanvas.serviceRepaints();
                            //#endif
                            //#endif
                            //#endif
                            // Блок вычисления оставшейся задержки времени
                            long l_endTime = System.currentTimeMillis();
                            //#if SHOWSYS
                            i_Profil_TimePaint = ((int) l_endTime & 0xFFFFFF) - i_tempProfValue;
                            //#endif

                            l_endTime -= l_startTime;
                            if (l_endTime > i_gameDelay || l_endTime < 0)
                                i_timedelay = 10;
                            else if (l_endTime <= i_gameDelay) i_timedelay = i_gameDelay - (int) l_endTime;
                        }
                    }
            ;
                    break;
            }
            try
            {
                Thread.sleep(i_timedelay);
            }
            catch (InterruptedException e)
            {
            }

            //#if LIGHT
            //#if VENDOR=="NOKIA"
            //$if (lg_Option_Light)
            //$com.nokia.mid.ui.DeviceControl.setLights(0,100);
            //$else
            //$com.nokia.mid.ui.DeviceControl.setLights(0,0);
            //#endif

            //#if VENDOR=="SIEMENS"
            //$if (lg_Option_Light)
            //$    com.siemens.mp.game.Light.setLightOn();
            //$else
            //$    com.siemens.mp.game.Light.setLightOff();
            //#endif

            //#if VENDOR=="SAMSUNG"
            //$if (lg_Option_Light)
            //$    com.samsung.util.LCDLight.on(0xFFFF);
            //$else
            //$    com.samsung.util.LCDLight.off();
            //#endif
            //#endif

            i_CurrentModeTickCounter++;
        }

        p_LoadingThread = null;

        //#if !(VENDOR=="SAMSUNG" && MODEL=="C100")
        switch (i_MidletMode)
        {
            case MODE_SHOWSTAGE:
            case MODE_GAMEPLAY:
            case MODE_GAMEMENU:
                {
                    saveCurrentGame();
                }
        ;
                break;
        }
        //#endif

        try
        {
            if (Gamelet.i_GameState == Gamelet.STATE_INITED)
            {
                Gamelet.releaseGame();
            }
            Gamelet.release();
        }
        catch (Exception e)
        {
        }

        onAppDestroyed();

        setMode(MODE_RELEASING);
        notifyDestroyed();
    }

    //#-
    private static final int STAGENUMBER_FIRST = 11;
    //#+
    //$private static final int STAGENUMBER_FIRST = /*$STARTLEVEL$*/;
    private static final int STAGENUMBER_LAST = 20;

    //================================Обработка игровых событий=========================
    public static final int processGameAction(int _arg)
    {
        //#if SHOWSYS
        System.out.println("Game Action " + _arg);
        //#endif
        switch (_arg)
        {
            case Gamelet.GAMEACTION_PLAYER_GET_AMMO:
                {
                    i_Flash_Ammo_Counter = TIMER_INIT;
                    if (lg_Option_Sound) SoundManager.playSound(SoundManager.SOUND_PLAYER_HET_AMMO,1);
                }
                break;
            case Gamelet.GAMEACTION_ATTEMPTION_CHANGE:
                {
                    i_Flash_Life_Counter = TIMER_INIT;
                }
                break;
            case Gamelet.GAMEACTION_PLAYER_GET_LIFE:
                {
                    if (lg_Option_Sound) SoundManager.playSound(SoundManager.SOUND_PLAYER_GET_LIFE,1);
                }
                break;
            case Gamelet.GAMEACTION_VICTORY:
                {
                    if (lg_Option_Sound) SoundManager.playSound(SoundManager.SOUND_VICTORY,1);
                }
                break;
            case Gamelet.GAMEACTION_EXPLOSION:
                {
                    if (lg_Option_Sound) SoundManager.playSound(SoundManager.SOUND_EXPLOSION,1);
                }
                break;
            case Gamelet.GAMEACTION_PLAYER_FALL:
                {
                    if (lg_Option_Sound) SoundManager.playSound(SoundManager.SOUND_PLAYER_FALL,1);
                }
                break;
            case Gamelet.GAMEACTION_FIGHTER:
                {
                    if (lg_Option_Sound) SoundManager.playSound(SoundManager.SOUND_FIGHTER,1);
                }
                break;
            case Gamelet.GAMEACTION_ENEMY_FALL:
                {
                    if (lg_Option_Sound) SoundManager.playSound(SoundManager.SOUND_ENEMY_HIT,1);
                }
                break;
        }
        return 0;
    }

    //================================Игровые функции===================================
    private static final void drawScores(int _x, int _y, int _zeroNumbers, int _value)
    {
        String s_str = Integer.toString(_value);
        int i_len = s_str.length();

        int i_zeroNum = _zeroNumbers - i_len;

        final int NUMBER_CHAR_WIDTH = 7;

        while (i_zeroNum != 0)
        {
            ImageManager.drawImage(MAP_DIGITS0, _x, _y);
            _x += NUMBER_CHAR_WIDTH;
            i_zeroNum--;
        }

        byte[] ab_chars = s_str.getBytes();
        for (int li = 0; li < i_len; li++)
        {
            int i_char = ab_chars[li] - 0x30;
            ImageManager.drawImage(MAP_DIGITS0 + (i_char * ImageManager.IMAGEINFO_LENGTH), _x, _y);
            _x += NUMBER_CHAR_WIDTH;
        }
    }
    /*
            private static final void drawBar(Graphics _g, int _color, int _x, int _y, int _width, int _height)
            {
                _g.setClip(_x, _y, _width, _height);
                _g.setColor(_color);
                _g.fillRect(_x, _y, _width, _height);
            }
        */

    private static void onStageRestart() //ONEVENT onStageRestart
    {
        i_Flash_Ammo_Counter = 0;
        i_Flash_Life_Counter = 0;
    }

    private static void onAfterGameLoading() //ONEVENT onAfterGameLoading
    {
    }

    protected static final void onLoadingGameResources(Class _this) throws Exception //ONEVENT onLoadingGameResources
    {
        //#if (VENDOR=="NOKIA" && (MODEL=="7650" || MODEL=="7610"))||(VENDOR=="SIEMENS" && MODEL=="CX65")||(VENDOR=="MOTOROLA" && MODEL=="E398")
        p_FakedPanel = Image.createImage(GAMESCREEN_WIDTH, (SCREEN_HEIGHT - GAMESCREEN_HEIGHT) >> 1);
        Graphics p_gr = p_FakedPanel.getGraphics();
        final int COLOR_FAKED = 0x00004C;
        p_gr.setColor(COLOR_FAKED);
        p_gr.fillRect(0, 0, p_FakedPanel.getWidth(), p_FakedPanel.getHeight());
        p_gr = null;
        //#endif

        //#if (VENDOR=="SE" || VENDOR=="SAMSUNG") || (VENDOR=="LG" && MODEL=="G1600")
        //$ImageManager.init(_this, true);
        //#else
        ImageManager.init(_this, false);
        //#endif

        p_GameForm = new MCFFormRender();
        p_GameForm.init(_this, "/garea.mcf");

        //#if TILE
        Image p_img = null;
        byte[] ab_tileArray = (byte[]) Gamelet.loadArray(p_This, "/tile.bin");

        Image[] ap_separatedImages = null;

        //#if (VENDOR=="SIEMENS" && MODEL=="M55") || (VENDOR=="SAMSUNG" && (MODEL=="X100" || MODEL=="C100")) || (VENDOR=="SE" && MODEL=="T610") || (VENDOR=="LG" && MODEL=="G1600")
            //$ap_separatedImages = TileBckgnd.loadSeparatedImagesContainer(p_This,"/tile.tls");
            //$TileBckgnd.initTileBackground(GAMESCREEN_WIDTH, 32, 16, 16,GAMESCREEN_WIDTH,GAMESCREEN_HEIGHT);
        //#else
            p_img = Image.createImage("/tile.png");
            TileBckgnd.initTileBackground(GAMESCREEN_WIDTH, GAMESCREEN_HEIGHT, 16, 16, GAMESCREEN_WIDTH, GAMESCREEN_HEIGHT);
        //#endif

        //#if (VENDOR=="SIEMENS" && MODEL=="M55") || (VENDOR=="SAMSUNG" && (MODEL=="X100" || MODEL=="C100")) || (VENDOR=="SE" && MODEL=="T610") || (VENDOR=="LG" && MODEL=="G1600")
            //$TileBckgnd.setGameRoomArray(19, ab_tileArray);
        //#else
            //#if (VENDOR=="NOKIA" && (MODEL=="3510" || MODEL=="6100")) || (VENDOR=="MOTOROLA" && MODEL=="C380") || (VENDOR=="SIEMENS" && (MODEL=="CX65" || MODEL=="M55" || MODEL=="S55" || MODEL=="C65" || MODEL=="S55"))
                //$TileBckgnd.setGameRoomArray(27, ab_tileArray);
            //#else
                TileBckgnd.setGameRoomArray(44, ab_tileArray);
            //#endif
        //#endif

            TileBckgnd.setBlockImage(p_img, ap_separatedImages);
        //#endif

        //#if SOUND
        SoundManager.initBlock(p_This, 75);
        //#endif
    }

    private static final void onMidletModeChanged(int _newMode, int _oldMode) //ONEVENT onMidletModeChanged
    {
        //#if MCF_SPLASH
        //$if (_oldMode == MODE_MAINMENU && (_newMode == MODE_SHOWSTAGE || _newMode == MODE_GAMEPLAY))
        //${
        //$    if (p_Image_Splash!=null) p_Image_Splash.realize();
        //$    p_Image_Splash = null;
        //$}
        //#endif

        switch (_newMode)
        {
                //#if SOUND
                case MODE_RECORDNAME:
            case MODE_GAMEMENU:
            case MODE_GAMEPLAY:
            case MODE_SHOWSTAGE:
                {
                    if (!(_newMode==MODE_SHOWSTAGE && _oldMode==MODE_GAMEPLAY)) SoundManager.stopAllSound();
                }
        ;
                break;
            case MODE_GAMEFINAL:
                {
                    SoundManager.stopAllSound();
                    if (lg_Option_Sound)
                    {
                        if (Gamelet.i_PlayerState == Gamelet.PLAYER_LOST)
                            SoundManager.playSound(SoundManager.SOUND_LOST, 1);
                        else
                            SoundManager.playSound(SoundManager.SOUND_WIN, 1);
                    }
                }
        ;
                break;
                //#endif
                case MODE_MAINMENU:
                {
                    //#if SOUND
                    SoundManager.stopAllSound();
                    if (lg_Option_Sound)
                    {
                        SoundManager.playSound(SoundManager.SOUND_THEME, 1);
                    }
                    //#endif
                }
        ;
                break;
        }
    }

    public static final boolean onInitNewGameStage(int _gameStage) //ONEVENT onInitNewGameStage
    {
        i_startStageGameOptionalValue1 = i_GameOptionalValue1;
        i_startStageGameOptionalValue2 = i_GameOptionalValue2;

        return true;
    }

    private static final boolean onInitNewGame(Class _this, int _selectedGameLevel) //ONEVENT onInitNewGame
    {
        i_GameOptionalValue1 = 0;
        i_Flash_Ammo_Counter = 0;
        i_Flash_Life_Counter = 0;

        //#if TILE
        TileBckgnd.setXY((i_GameOptionalValue1 + 0x7F) >> 8, 0);
        //#endif

        try
        {
        }
        catch (Exception e)
        {
            return false;
        }
        return true;
    }

    private static final void onReleaseGame() //ONEVENT onReleaseGame
    {
        Runtime.getRuntime().gc();
    }

    private static final boolean onStartApp(startup _this) //ONEVENT onStartApp
    {
        return true;
    }

    private static final void onGameStep() //ONEVENT onGameStep
    {
        i_GameOptionalValue1 += I8_SCROLLTILE_SPEED;
        if (i_Flash_Ammo_Counter > 0) i_Flash_Ammo_Counter--;
        if (i_Flash_Life_Counter > 0) i_Flash_Life_Counter--;
    }

    private static final void onAppDestroyed() //ONEVENT onAppDestroyed
    {
        // Для SonyEricssona не надо делать принудительную очистку картиинок, так как иначе выход затяется
        //==========================

        //==========================
        p_Image_Splash = null;
        Runtime.getRuntime().gc();
    }

    public static final void onAreaComponentPaint(Graphics _graphics, int _formIndex, int _x, int _y, int _width, int _height, int _channel) //ONEVENT onAreaComponentPaint
    {
        ImageManager.p_DestinationGraphics = _graphics;

        switch (_formIndex)
        {
            case 0:
                {
                    switch (_channel)
                    {
                            // Игровое поле
                            case 0:
                            {
                                final int GS_W = GAMESCREEN_WIDTH;
                                final int GS_H = GAMESCREEN_HEIGHT;
                                //#if !TILE
                                    //$_graphics.setColor(0x3186C6);
                                    //$_graphics.fillRect(0,0,GS_W,GS_H);
                                //#else
                                    TileBckgnd.setXY((i_GameOptionalValue1 + 0x7F) >> 8, 0);

                                    //#if (VENDOR=="SIEMENS" && MODEL=="M55") || VENDOR=="SAMSUNG" || VENDOR=="LG" || VENDOR=="SE"
                                       //#if VENDOR=="NOKIA"
                                            //$_graphics.setColor(0x3388CC);
                                        //#else
                                            //$_graphics.setColor(0x3186C6);
                                        //#endif
                                        //$_graphics.fillRect(0,0,GS_W,GS_H-32);

                                        //#if VENDOR=="NOKIA"
                                            //$TileBckgnd.directPaint(_graphics, 0, GS_H-32);
                                        //#else
                                            //$TileBckgnd.drawBufferToGraphics(_graphics, 0, GS_H-32);
                                        //#endif
                                         //$_graphics.setClip(0,0,GS_W,GS_H);
                                   //#else
                                        //#if VENDOR=="SIEMENS" && MODEL=="CX65"
                                            //$TileBckgnd.drawBufferToGraphics(_graphics, 0, -4);
                                        //#else
                                            //#if VENDOR=="SIEMENS" && MODEL=="C65"
                                                //$TileBckgnd.drawBufferToGraphics(_graphics, GAMESCREEN_OFFSETX, 0);
                                            //#else
                                                TileBckgnd.directPaint(_graphics, GAMESCREEN_OFFSETX, 0);
                                            //#endif
                                   //#endif

                                //#endif
                            //#endif

                                if (i_MidletMode != MODE_SHOWSTAGE)
                                {
                                    int i_x = 0;
                                    int i_y = 0;

                                    Sprite p_Sprite;
                                    final int [] ai_sdt = SPRITE_DECODE_TABLE;

                                    // Рисуем нашу вертушку
                                    p_Sprite = Gamelet.p_MyCopterSprite;

                                    if (p_Sprite.lg_SpriteActive)
                                    {
                                        i_y = ((p_Sprite.i8_ScreenY + 0x7F) >> 8);


                                        int i_index = ai_sdt[p_Sprite.i_ObjectType];

                                        i_index += (ImageManager.IMAGEINFO_LENGTH * p_Sprite.i_Frame);

                                        i_x = ((p_Sprite.i8_ScreenX + 0x7F) >> 8);
                                        ImageManager.drawImage(i_index, i_x, i_y);
                                    }

                                    // Рисуем ящик
                                    p_Sprite = Gamelet.p_AmmoSprite;
                                    if (p_Sprite.lg_SpriteActive)
                                    {
                                        i_x = ((p_Sprite.i8_ScreenX + 0x7F) >> 8);
                                        i_y = ((p_Sprite.i8_ScreenY + 0x7F) >> 8);

                                        ImageManager.drawImage(MAP_SHELLS, i_x, i_y);
                                    }

                                    // Рисуем истребитель
                                    p_Sprite = Gamelet.p_FighterSprite;
                                    if (p_Sprite.lg_SpriteActive)
                                    {
                                        i_y = ((p_Sprite.i8_ScreenY + 0x7F) >> 8);

                                        int i_index = ai_sdt[p_Sprite.i_ObjectType];
                                        i_index += (p_Sprite.i_Frame * ImageManager.IMAGEINFO_LENGTH);


                                        i_x = ((p_Sprite.i8_ScreenX + 0x7F) >> 8);
                                        int i_x2 = i_x + ((p_Sprite.i8_width + 0x7F) >> 8);
                                        if (i_x2 >= 0 && i_x < GAMESCREEN_WIDTH) ImageManager.drawImage(i_index, i_x, i_y);
                                    }

                                    // Рисуем вертушки
                                    Sprite [] ap_sprts = Gamelet.ap_CopterSprites;
                                    for (int li = 0; li < Gamelet.MAX_SCREEN_COPTERS_NUMBER; li++)
                                    {
                                        p_Sprite = ap_sprts[li];
                                        if (!p_Sprite.lg_SpriteActive) continue;

                                        i_y = ((p_Sprite.i8_ScreenY + 0x7F) >> 8);

                                        int i_index = ai_sdt[p_Sprite.i_ObjectType];
                                        i_index += (ImageManager.IMAGEINFO_LENGTH * p_Sprite.i_Frame);

                                        i_x = ((p_Sprite.i8_ScreenX + 0x7F) >> 8);
                                        int i_x2 = i_x + ((p_Sprite.i8_width + 0x7F) >> 8);
                                        if (i_x2 >= 0 && i_x < GAMESCREEN_WIDTH) ImageManager.drawImage(i_index, i_x, i_y);
                                    }

                                    // Рисуем взрывы
                                    ap_sprts = Gamelet.ap_ExplosionSprites;
                                    for (int li = 0; li < Gamelet.MAX_EXPLOSIONS_NUMBER; li++)
                                    {
                                        p_Sprite = ap_sprts[li];
                                        if (!p_Sprite.lg_SpriteActive) continue;

                                        i_y = ((p_Sprite.i8_ScreenY + 0x7F) >> 8);

                                        int i_index = ai_sdt[p_Sprite.i_ObjectType];
                                        i_index += (ImageManager.IMAGEINFO_LENGTH * p_Sprite.i_Frame);

                                        i_x = ((p_Sprite.i8_ScreenX + 0x7F) >> 8);
                                        int i_x2 = i_x + ((p_Sprite.i8_width + 0x7F) >> 8);
                                        if (i_x2 >= 0 && i_x < GAMESCREEN_WIDTH) ImageManager.drawImage(i_index, i_x, i_y);
                                    }

                                    // Рисуем выстрелы и метеориты
                                    ap_sprts = Gamelet.ap_ShellAndFireballSprites;
                                    for (int li = 0; li < Gamelet.MAX_SHELLS_NUMBER; li++)
                                    {
                                        p_Sprite = ap_sprts[li];
                                        if (!p_Sprite.lg_SpriteActive) continue;

                                        int i_index = ai_sdt[p_Sprite.i_ObjectType];
                                        i_index += (ImageManager.IMAGEINFO_LENGTH * p_Sprite.i_Frame);

                                        i_y = ((p_Sprite.i8_ScreenY + 0x7F) >> 8);

                                        i_x = ((p_Sprite.i8_ScreenX + 0x7F) >> 8);
                                        int i_x2 = i_x + ((p_Sprite.i8_width + 0x7F) >> 8);
                                        if (i_x2 >= 0 && i_x < GAMESCREEN_WIDTH) ImageManager.drawImage(i_index, i_x, i_y);
                                    }

                                    // Рисуем Жизнь
                                    p_Sprite = Gamelet.p_LifeSprite;
                                    if (p_Sprite.lg_SpriteActive)
                                    {
                                        i_x = ((p_Sprite.i8_ScreenX + 0x7F) >> 8);
                                        i_y = ((p_Sprite.i8_ScreenY + 0x7F) >> 8);

                                        int i_index = MAP_BONUS_LIFE_01 + (p_Sprite.i_Frame * ImageManager.IMAGEINFO_LENGTH);

                                        ImageManager.drawImage(i_index, i_x, i_y);
                                    }

                                    // Очки за объект
                                    ap_sprts = Gamelet.ap_BonusSprites;
                                    for (int li = 0; li < Gamelet.MAX_BONUS_NUMBER; li++)
                                    {
                                        p_Sprite = ap_sprts[li];
                                        if (p_Sprite.lg_SpriteActive)
                                        {
                                            i_x = ((p_Sprite.i8_ScreenX + 0x7F) >> 8);
                                            i_y = ((p_Sprite.i8_ScreenY + 0x7F) >> 8);

                                            int i_index = ai_sdt[p_Sprite.i_ObjectType];
                                            ImageManager.drawImage(i_index, i_x, i_y);
                                        }
                                    }
                                }
                            }
                    ;
                            break;
                        case 1:
                            {
                                // Жизни игрока
                                boolean lg_show = true;
                                if (i_Flash_Life_Counter > 0)
                                {
                                    if (i_Flash_Life_Counter % 6 < 4) lg_show = false;
                                }
                                if (lg_show)
                                {

                                    //#if (VENDOR=="NOKIA" && (MODEL=="7650"||MODEL=="7610")) || (VENDOR=="SIEMENS" && MODEL=="CX65")|| (VENDOR=="MOTOROLA" && MODEL=="E398")
                                    final int ICOLIFE_WIDTH = 15;
                                    //#else
                                    //$final int ICOLIFE_WIDTH = 11;
                                    //#endif
                                    int i_number = Gamelet.iPlayerAttemptions;
                                    int i_initX = _x;
                                    while (i_number != 0)
                                    {
                                        ImageManager.drawImage(MAP_IND_LIVES, i_initX, _y);
                                        i_initX += ICOLIFE_WIDTH;
                                        i_number--;
                                    }
                                }
                            }
                    ;
                            break;
                        case 2:
                            {
                                // Player's shells
                                boolean lg_show = true;
                                if (i_Flash_Ammo_Counter > 0)
                                {
                                    if (i_Flash_Ammo_Counter % 6 < 4) lg_show = false;
                                }
                                if (lg_show)
                                {
                                    final int ICOSHELL_WIDTH = 15;
                                    int i_initX = _x;
                                    ImageManager.drawImage(MAP_IND_SHELLS, i_initX, _y);
                                    i_initX += ICOSHELL_WIDTH;
                                    drawScores(i_initX, _y + 1, 2, Gamelet.iPlayerAmmo);
                                }
                            }
                    ;
                            break;
                        case 3:
                            {
                                if (Gamelet.iFighterFlightCounter > 0)
                                {
                                    // Иконка истребителя
                                    if (i_CurrentModeTickCounter % 6 > 3) ImageManager.drawImage(MAP_FIGHTERICON, _x, _y);
                                }
                            }
                    ;
                            break;
                        case 4:
                            {
                                // Область вывода номера уровня
                                i_nextLevelAreaX = _x;
                                i_nextLevelAreaY = _y;
                            }
                    ;
                            break;
                    }
                    _graphics.setClip(0, 0, GAMESCREEN_WIDTH, GAMESCREEN_HEIGHT);
                }
        ;
                break;
            case 1:
            case 2:
                {
                    // Форма номера уровня и очков
                    switch (_channel)
                    {
                        case 0:
                            {
                                // Номер уровня
                                drawScores(_x, _y, 2, i_selectedGameStage);
                            }
                    ;
                            break;
                        case 1:
                            {
                                // Количество очков
                                drawScores(_x, _y, 4, Gamelet.getPlayerScore());
                            }
                    ;
                            break;
                    }
                }
        ;
                break;
        }
    }

    public static final int onGetButtonState(int _componentIndex, int _channel) //ONEVENT onGetButtonState
    {
        return 0;
    }

    private static final void onPaintGame(Graphics _graphics) //ONEVENT onPaintGame
    {
        p_GameForm.selectForm(0);
        p_GameForm.paint(_graphics, GAMESCREEN_OFFSETX, GAMESCREEN_OFFSETY, false);

        //#if (VENDOR=="NOKIA" && (MODEL=="7650" || MODEL=="7610")) || (VENDOR=="SIEMENS" && MODEL=="CX65") || (VENDOR=="MOTOROLA" && MODEL=="E398")
        _graphics.setClip(0, 0, SCREEN_WIDTH, SCREEN_HEIGHT);
        _graphics.drawImage(p_FakedPanel, 0, 0, 0);
        _graphics.drawImage(p_FakedPanel, 0, GAMESCREEN_OFFSETY + GAMESCREEN_HEIGHT, 0);
        //#endif
    }

    private static final void onPaintGameStage(Graphics _graphics) //ONEVENT onPaintGameStage
    {
        onPaintGame(_graphics);
        _graphics.setClip(GAMESCREEN_OFFSETX, GAMESCREEN_OFFSETY, GAMESCREEN_WIDTH, GAMESCREEN_HEIGHT);
        if (i_CurrentModeTickCounter % 6 < 3)
        {
            if (LangBlock.i_CurrentLanguageIndex == 1) p_GameForm.selectForm(2);
            else p_GameForm.selectForm(1);
            p_GameForm.paint(_graphics, i_nextLevelAreaX, i_nextLevelAreaY, false);
        }
    }

    private static final void onPaintGameOver(Graphics _graphics) //ONEVENT onPaintGameOver
    {
        _graphics.setColor(COLOR_MAIN_BACKGROUND);
        _graphics.fillRect(0, 0, SCREEN_WIDTH, SCREEN_HEIGHT);

        //#if MCF_SPLASH
        //$p_Image_Splash.paint(_graphics,0,0,true);
        //#else
        if (p_Image_Splash != null)
        {
            int i_iw = p_Image_Splash.getWidth();
            int i_ih = p_Image_Splash.getHeight();
            _graphics.drawImage(p_Image_Splash, (SCREEN_WIDTH - i_iw) / 2, (SCREEN_HEIGHT - i_ih) / 2, 0);
        }
        //#endif
    }

    private final static void onKeyPressed(int _keyCode) //ONEVENT onKeyPressed
    {
        switch (_keyCode)
        {
                //#if VENDOR=="LG" || VENDOR=="SE"
                //У телефонов LG нет события отжатия софт клавиши поэтому отрабатываем выход в меню на нажатие
                //$case -6:
                //$    {
                //$    setMode(MODE_GAMEMENU);
                //$    };break;
                //$ case -7:
                //#endif
                case KEY_CODE_SOFT_RIGHT:
                {
                }
        ;
                break;
            case KEY_CODE_UP:
            case JOY_СODE_UP:
                {
                    //#if VENDOR=="SIEMENS"
                    //$i_KeyFlags = Gamelet.BUTTON_UP;
                    //#else
                    i_KeyFlags |= Gamelet.BUTTON_UP;
                    i_KeyFlags &= ~Gamelet.BUTTON_DOWN;
                    //#endif
                }
        ;
                break;
            case KEY_CODE_LEFT:
            case JOY_CODE_LEFT:
                {
                    //#if VENDOR=="SIEMENS"
                    //$i_KeyFlags = Gamelet.BUTTON_LEFT;
                    //#else
                    i_KeyFlags |= Gamelet.BUTTON_LEFT;
                    i_KeyFlags &= ~Gamelet.BUTTON_RIGHT;
                    //#endif
                }
        ;
                break;
            case KEY_CODE_RIGHT:
            case JOY_CODE_RIGHT:
                {
                    //#if VENDOR=="SIEMENS"
                    //$i_KeyFlags = Gamelet.BUTTON_RIGHT;
                    //#else
                    i_KeyFlags |= Gamelet.BUTTON_RIGHT;
                    i_KeyFlags &= ~Gamelet.BUTTON_LEFT;
                    //#endif
                }
        ;
                break;
            case KEY_CODE_DOWN:
            case JOY_CODE_DOWN:
                {
                    //#if VENDOR=="SIEMENS"
                    //$i_KeyFlags = Gamelet.BUTTON_DOWN;
                    //#else
                    i_KeyFlags |= Gamelet.BUTTON_DOWN;
                    i_KeyFlags &= ~Gamelet.BUTTON_UP;
                    //#endif
                }
        ;
                break;
            case KEY_CODE_FIRE:
            case JOY_CODE_FIRE:
                {
                    //#if VENDOR=="SIEMENS"
                    //$i_KeyFlags = Gamelet.BUTTON_FIRE;
                    //#else
                    i_KeyFlags |= Gamelet.BUTTON_FIRE;
                    //#endif
                }
        ;
                break;
            case KEY_CODE_KEY1:
                break;
            case KEY_CODE_KEY2:
                {
                }
        ;
                break;
            case KEY_CODE_SOFT_LEFT:
                break;
        }
    }

    private final void onKeyReleased(int _keyCode) //ONEVENT onKeyReleased
    {
        switch (_keyCode)
        {
            case KEY_CODE_UP:
            case JOY_СODE_UP:
                {
                    //#if VENDOR=="SIEMENS"
                    //$i_KeyFlags = Gamelet.BUTTON_NONE;
                    //#else
                    i_KeyFlags &= ~Gamelet.BUTTON_UP;
                    //#endif
                }
        ;
                break;
            case KEY_CODE_LEFT:
            case JOY_CODE_LEFT:
                {
                    //#if VENDOR=="SIEMENS"
                    //$i_KeyFlags = Gamelet.BUTTON_NONE;
                    //#else
                    i_KeyFlags &= ~Gamelet.BUTTON_LEFT;
                    //#endif
                }
        ;
                break;
            case KEY_CODE_RIGHT:
            case JOY_CODE_RIGHT:
                {
                    //#if VENDOR=="SIEMENS"
                    //$i_KeyFlags = Gamelet.BUTTON_NONE;
                    //#else
                    i_KeyFlags &= ~Gamelet.BUTTON_RIGHT;
                    //#endif
                }
        ;
                break;
            case KEY_CODE_DOWN:
            case JOY_CODE_DOWN:
                {
                    //#if VENDOR=="SIEMENS"
                    //$i_KeyFlags = Gamelet.BUTTON_NONE;
                    //#else
                    i_KeyFlags &= ~Gamelet.BUTTON_DOWN;
                    //#endif
                }
        ;
                break;
            case KEY_CODE_FIRE:
            case JOY_CODE_FIRE:
                {
                    //#if VENDOR=="SIEMENS"
                    //$i_KeyFlags = Gamelet.BUTTON_NONE;
                    //#else
                    i_KeyFlags &= ~Gamelet.BUTTON_FIRE;
                    //#endif
                }
        ;
                break;
            case KEY_CODE_KEY1:
                break;
            case KEY_CODE_KEY2:
                {
                }
        ;
                break;
                //#if VENDOR=="SIEMENS"
                // кнопка "положить трубку" - выход в меню
                case -12:
                //#endif
                //#if VENDOR=="MOTOROLA"
                // кнопка "выход в меню" - выход в меню
                case -23:
                //#endif
                //#if VENDOR=="SE" || VENDOR=="SAMSUNG" || VENDOR=="LG"
                // кнопка "C" - выход в меню
                case -8:
                //#endif
                //#if VENDOR=="NOKIA"
                // кнопка "снять трубку" - выход в меню
                case -10:
                //#endif
                case KEY_CODE_SOFT_LEFT:
                {
                    if (i_lastPressedKey == _keyCode) setMode(MODE_GAMEMENU);
                }
        ;
                break;
        }
    }

    //==================Игровые переменные и объекты========================
    //#if (VENDOR=="NOKIA" && (MODEL=="7650" || MODEL=="7610")) || (VENDOR=="SIEMENS" && MODEL=="CX65") || (VENDOR=="MOTOROLA" && MODEL=="E398")
    private static Image p_FakedPanel;
    //#endif

    private static MCFFormRender p_GameForm;

    private static int i_nextLevelAreaX;
    private static int i_nextLevelAreaY;

    private static final int I8_SCROLLTILE_SPEED = 0x70;

    private static final int TIMER_INIT = 30;

    private static int i_Flash_Ammo_Counter = 0;
    private static int i_Flash_Life_Counter = 0;

    //--------------------------------
    //#if IMAGES_INFO_LENGTH == 7
    //$private static final int MAP_BONUS_LIFE_01 = 0;
    //$private static final int MAP_BOSS1_01 = 21;
    //$private static final int MAP_BOSS2_01 = 42;
    //$private static final int MAP_BOSS3_01 = 63;
    //$private static final int MAP_BOSS4_01 = 84;
    //$private static final int MAP_BOSSCANON_01 = 105;
    //$private static final int MAP_COPTER00_MOVE_LEFT01 = 126;
    //$private static final int MAP_COPTER00_MOVE_RIGHT01 = 147;
    //$private static final int MAP_COPTER00_ROT_LEFT01 = 168;
    //$private static final int MAP_COPTER00_ROT_RIGHT01 = 189;
    //$private static final int MAP_COPTER01_MOVE_LEFT01 = 210;
    //$private static final int MAP_COPTER01_MOVE_RIGHT01 = 231;
    //$private static final int MAP_COPTER01_ROT_TOLEFT01 = 273;
    //$private static final int MAP_COPTER01_ROT_TORIGHT01 = 294;
    //$private static final int MAP_COPTER02_MOVE_LEFT01 = 315;
    //$private static final int MAP_COPTER02_MOVE_RIGHT01 = 336;
    //$private static final int MAP_COPTER02_ROT_TOLEFT01 = 378;
    //$private static final int MAP_COPTER02_ROT_TORIGHT01 = 399;
    //$private static final int MAP_COPTER03_MOVE_LEFT01 = 420;
    //$private static final int MAP_COPTER03_MOVE_RIGHT01 = 448;
    //$private static final int MAP_COPTER03_ROT_01 = 476;
    //$private static final int MAP_COPTER04_MOVE_LEFT01 = 483;
    //$private static final int MAP_COPTER04_MOVE_RIGHT01 = 511;
    //$private static final int MAP_COPTER04_ROT_01 = 539;
    //$private static final int MAP_COPTER05_MOVE_LEFT01 = 546;
    //$private static final int MAP_COPTER05_MOVE_RIGHT01 = 567;
    //$private static final int MAP_DIGITS0 = 588;
    //$private static final int MAP_ENEMYSHELL_LEFTDOWN_01 = 658;
    //$private static final int MAP_ENEMYSHELL_LEFTUP_01 = 679;
    //$private static final int MAP_ENEMYSHELL_LEFT_01 = 700;
    //$private static final int MAP_ENEMYSHELL_RIGHT_01 = 721;
    //$private static final int MAP_EXPLOSION_01 = 742;
    //$private static final int MAP_FIGHTERICON = 805;
    //$private static final int MAP_HEROSHELL_LEFT_01 = 812;
    //$private static final int MAP_HEROSHELL_RIGHT_01 = 833;
    //$private static final int MAP_ICO_LIFE_SMALL = 854;
    //$private static final int MAP_ICO_SHELL_SMALL = 861;
    //$private static final int MAP_IND_LIVES = 868;
    //$private static final int MAP_IND_SHELLS = 875;
    //$private static final int MAP_METEOR_01 = 882;
    //$private static final int MAP_SCORES10 = 910;
    //$private static final int MAP_SCORES15 = 917;
    //$private static final int MAP_SCORES20 = 924;
    //$private static final int MAP_SCORES5 = 931;
    //$private static final int MAP_SHELLS = 938;
    //$private static final int MAP_SMOKE_01 = 945;
    //#else

    private static final int MAP_BONUS_LIFE_01 = 0;
    private static final int MAP_BONUS_LIFE_02 = 9;
    private static final int MAP_BONUS_LIFE_03 = 18;
    private static final int MAP_BOSS1_01 = 27;
    private static final int MAP_BOSS1_02 = 36;
    private static final int MAP_BOSS1_03 = 45;
    private static final int MAP_BOSS2_01 = 54;
    private static final int MAP_BOSS2_02 = 63;
    private static final int MAP_BOSS2_03 = 72;
    private static final int MAP_BOSS3_01 = 81;
    private static final int MAP_BOSS3_02 = 90;
    private static final int MAP_BOSS3_03 = 99;
    private static final int MAP_BOSS4_01 = 108;
    private static final int MAP_BOSS4_02 = 117;
    private static final int MAP_BOSS4_03 = 126;
    private static final int MAP_BOSSCANON_01 = 135;
    private static final int MAP_BOSSCANON_02 = 144;
    private static final int MAP_BOSSCANON_03 = 153;
    private static final int MAP_COPTER00_MOVE_LEFT01 = 162;
    private static final int MAP_COPTER00_MOVE_LEFT02 = 171;
    private static final int MAP_COPTER00_MOVE_LEFT03 = 180;
    private static final int MAP_COPTER00_MOVE_RIGHT01 = 189;
    private static final int MAP_COPTER00_MOVE_RIGHT02 = 198;
    private static final int MAP_COPTER00_MOVE_RIGHT03 = 207;
    private static final int MAP_COPTER00_ROT_LEFT01 = 216;
    private static final int MAP_COPTER00_ROT_LEFT02 = 225;
    private static final int MAP_COPTER00_ROT_LEFT03 = 234;
    private static final int MAP_COPTER00_ROT_RIGHT01 = 243;
    private static final int MAP_COPTER00_ROT_RIGHT02 = 252;
    private static final int MAP_COPTER00_ROT_RIGHT03 = 261;
    private static final int MAP_COPTER01_MOVE_LEFT01 = 270;
    private static final int MAP_COPTER01_MOVE_LEFT02 = 279;
    private static final int MAP_COPTER01_MOVE_LEFT03 = 288;
    private static final int MAP_COPTER01_MOVE_RIGHT01 = 297;
    private static final int MAP_COPTER01_MOVE_RIGHT02 = 306;
    private static final int MAP_COPTER01_MOVE_RIGHT03 = 315;
    private static final int MAP_COPTER01_ROT_01 = 324;
    private static final int MAP_COPTER01_ROT_02 = 333;
    private static final int MAP_COPTER01_ROT_03 = 342;
    private static final int MAP_COPTER01_ROT_TOLEFT01 = 351;
    private static final int MAP_COPTER01_ROT_TOLEFT02 = 360;
    private static final int MAP_COPTER01_ROT_TOLEFT03 = 369;
    private static final int MAP_COPTER01_ROT_TORIGHT01 = 378;
    private static final int MAP_COPTER01_ROT_TORIGHT02 = 387;
    private static final int MAP_COPTER01_ROT_TORIGHT03 = 396;
    private static final int MAP_COPTER02_MOVE_LEFT01 = 405;
    private static final int MAP_COPTER02_MOVE_LEFT02 = 414;
    private static final int MAP_COPTER02_MOVE_LEFT03 = 423;
    private static final int MAP_COPTER02_MOVE_RIGHT01 = 432;
    private static final int MAP_COPTER02_MOVE_RIGHT02 = 441;
    private static final int MAP_COPTER02_MOVE_RIGHT03 = 450;
    private static final int MAP_COPTER02_ROT_01 = 459;
    private static final int MAP_COPTER02_ROT_02 = 468;
    private static final int MAP_COPTER02_ROT_03 = 477;
    private static final int MAP_COPTER02_ROT_TOLEFT01 = 486;
    private static final int MAP_COPTER02_ROT_TOLEFT02 = 495;
    private static final int MAP_COPTER02_ROT_TOLEFT03 = 504;
    private static final int MAP_COPTER02_ROT_TORIGHT01 = 513;
    private static final int MAP_COPTER02_ROT_TORIGHT02 = 522;
    private static final int MAP_COPTER02_ROT_TORIGHT03 = 531;
    private static final int MAP_COPTER03_MOVE_LEFT01 = 540;
    private static final int MAP_COPTER03_MOVE_LEFT02 = 549;
    private static final int MAP_COPTER03_MOVE_LEFT03 = 558;
    private static final int MAP_COPTER03_MOVE_LEFT04 = 567;
    private static final int MAP_COPTER03_MOVE_RIGHT01 = 576;
    private static final int MAP_COPTER03_MOVE_RIGHT02 = 585;
    private static final int MAP_COPTER03_MOVE_RIGHT03 = 594;
    private static final int MAP_COPTER03_MOVE_RIGHT04 = 603;
    private static final int MAP_COPTER03_ROT_01 = 612;
    private static final int MAP_COPTER04_MOVE_LEFT01 = 621;
    private static final int MAP_COPTER04_MOVE_LEFT02 = 630;
    private static final int MAP_COPTER04_MOVE_LEFT03 = 639;
    private static final int MAP_COPTER04_MOVE_LEFT04 = 648;
    private static final int MAP_COPTER04_MOVE_RIGHT01 = 657;
    private static final int MAP_COPTER04_MOVE_RIGHT02 = 666;
    private static final int MAP_COPTER04_MOVE_RIGHT03 = 675;
    private static final int MAP_COPTER04_MOVE_RIGHT04 = 684;
    private static final int MAP_COPTER04_ROT_01 = 693;
    private static final int MAP_COPTER05_MOVE_LEFT01 = 702;
    private static final int MAP_COPTER05_MOVE_LEFT02 = 711;
    private static final int MAP_COPTER05_MOVE_LEFT03 = 720;
    private static final int MAP_COPTER05_MOVE_RIGHT01 = 729;
    private static final int MAP_COPTER05_MOVE_RIGHT02 = 738;
    private static final int MAP_COPTER05_MOVE_RIGHT03 = 747;
    private static final int MAP_DIGITS0 = 756;
    private static final int MAP_DIGITS1 = 765;
    private static final int MAP_DIGITS2 = 774;
    private static final int MAP_DIGITS3 = 783;
    private static final int MAP_DIGITS4 = 792;
    private static final int MAP_DIGITS5 = 801;
    private static final int MAP_DIGITS6 = 810;
    private static final int MAP_DIGITS7 = 819;
    private static final int MAP_DIGITS8 = 828;
    private static final int MAP_DIGITS9 = 837;
    private static final int MAP_ENEMYSHELL_LEFTDOWN_01 = 846;
    private static final int MAP_ENEMYSHELL_LEFTDOWN_02 = 855;
    private static final int MAP_ENEMYSHELL_LEFTDOWN_03 = 864;
    private static final int MAP_ENEMYSHELL_LEFTUP_01 = 873;
    private static final int MAP_ENEMYSHELL_LEFTUP_02 = 882;
    private static final int MAP_ENEMYSHELL_LEFTUP_03 = 891;
    private static final int MAP_ENEMYSHELL_LEFT_01 = 900;
    private static final int MAP_ENEMYSHELL_LEFT_02 = 909;
    private static final int MAP_ENEMYSHELL_LEFT_03 = 918;
    private static final int MAP_ENEMYSHELL_RIGHT_01 = 927;
    private static final int MAP_ENEMYSHELL_RIGHT_02 = 936;
    private static final int MAP_ENEMYSHELL_RIGHT_03 = 945;
    private static final int MAP_EXPLOSION_01 = 954;
    private static final int MAP_EXPLOSION_02 = 963;
    private static final int MAP_EXPLOSION_03 = 972;
    private static final int MAP_EXPLOSION_04 = 981;
    private static final int MAP_EXPLOSION_05 = 990;
    private static final int MAP_EXPLOSION_06 = 999;
    private static final int MAP_EXPLOSION_07 = 1008;
    private static final int MAP_EXPLOSION_08 = 1017;
    private static final int MAP_EXPLOSION_09 = 1026;
    private static final int MAP_FIGHTERICON = 1035;
    private static final int MAP_HEROSHELL_LEFT_01 = 1044;
    private static final int MAP_HEROSHELL_LEFT_02 = 1053;
    private static final int MAP_HEROSHELL_LEFT_03 = 1062;
    private static final int MAP_HEROSHELL_RIGHT_01 = 1071;
    private static final int MAP_HEROSHELL_RIGHT_02 = 1080;
    private static final int MAP_HEROSHELL_RIGHT_03 = 1089;
    private static final int MAP_ICO_LIFE_SMALL = 1098;
    private static final int MAP_ICO_SHELL_SMALL = 1107;
    private static final int MAP_IND_LIVES = 1116;
    private static final int MAP_IND_SHELLS = 1125;
    private static final int MAP_METEOR_01 = 1134;
    private static final int MAP_METEOR_02 = 1143;
    private static final int MAP_METEOR_03 = 1152;
    private static final int MAP_METEOR_04 = 1161;
    private static final int MAP_SCORES10 = 1170;
    private static final int MAP_SCORES15 = 1179;
    private static final int MAP_SCORES20 = 1188;
    private static final int MAP_SCORES5 = 1197;
    private static final int MAP_SHELLS = 1206;
    private static final int MAP_SMOKE_01 = 1215;
    private static final int MAP_SMOKE_02 = 1224;
    private static final int MAP_SMOKE_03 = 1233;
    private static final int MAP_SMOKE_04 = 1242;
    private static final int MAP_SMOKE_05 = 1251;
    //#endif


    //#_if (VENDOR=="NOKIA" && (MODEL=="3510" || MODEL=="7610")) || (VENDOR=="MOTOROLA" && (MODEL=="C380" || MODEL=="E398")) || (VENDOR=="SIEMENS" && (MODEL=="CX65" || MODEL=="C65"))
        //#global IMAGES_INFO_LENGTH = 9
        //#global IMAGES_DYNAMIC =false
        //#global IMAGES_FLIPPEDLINK =true
        //#global IMAGES_EXTERNAL =false
        //#global IMAGES_NORMAL =true
        //#global IMAGES_LINK =true
        //#global IMAGES_MACRO =true
        //#global IMAGES_LINK2LINK =true
        //#global IMAGES_EXT2ONE =false

    //#_else
    //#_if (VENDOR=="NOKIA" && MODEL=="6100")
        //#global IMAGES_INFO_LENGTH = 9
        //#global IMAGES_DYNAMIC =true
        //#global IMAGES_FLIPPEDLINK =true
        //#global IMAGES_EXTERNAL =false
        //#global IMAGES_NORMAL =true
        //#global IMAGES_LINK =true
        //#global IMAGES_MACRO =true
        //#global IMAGES_LINK2LINK =true
        //#global IMAGES_EXT2ONE =false
    //#_else

    //#_if (VENDOR=="NOKIA" && MODEL=="7650")
        //#global IMAGES_INFO_LENGTH = 7
        //#global IMAGES_DYNAMIC =false
        //#global IMAGES_FLIPPEDLINK =false
        //#global IMAGES_EXTERNAL =false
        //#global IMAGES_NORMAL =true
        //#global IMAGES_LINK =true
        //#global IMAGES_MACRO =true
        //#global IMAGES_LINK2LINK =false
        //#global IMAGES_EXT2ONE =false
    //#_else

    //#_if (VENDOR=="SE" && MODEL=="T610")
        //#global IMAGES_INFO_LENGTH = 7
        //#global IMAGES_DYNAMIC =false
        //#global IMAGES_FLIPPEDLINK =false
        //#global IMAGES_EXTERNAL =true
        //#global IMAGES_NORMAL =true
        //#global IMAGES_LINK =true
        //#global IMAGES_MACRO =true
        //#global IMAGES_LINK2LINK =false
        //#global IMAGES_EXT2ONE =true
    //#_else
        //#_if VENDOR=="SAMSUNG" && (MODEL=="C100" || MODEL=="X100")
            //#global IMAGES_INFO_LENGTH = 7
            //#global IMAGES_DYNAMIC =false
            //#global IMAGES_FLIPPEDLINK =false
            //#global IMAGES_EXTERNAL =true
            //#global IMAGES_NORMAL =false
            //#global IMAGES_LINK =true
            //#global IMAGES_MACRO =true
            //#global IMAGES_LINK2LINK =false
            //#global IMAGES_EXT2ONE =true

        //#_else
        //#global IMAGES_INFO_LENGTH = 7
        //#global IMAGES_DYNAMIC =false
        //#global IMAGES_FLIPPEDLINK =false
        //#global IMAGES_EXTERNAL =false
        //#global IMAGES_NORMAL =true
        //#global IMAGES_LINK =true
        //#global IMAGES_MACRO =true
        //#global IMAGES_LINK2LINK =false
        //#global IMAGES_EXT2ONE =false
        //#_endif
    //#_endif
    //#_endif
    //#_endif
    //#_endif

    private static final int[] SPRITE_DECODE_TABLE = new int[128];

    static
    {
        SPRITE_DECODE_TABLE[Gamelet.SPRITE_MYCOPTER_RTL] = MAP_COPTER00_ROT_LEFT01;
        SPRITE_DECODE_TABLE[Gamelet.SPRITE_MYCOPTER_FALL_LTR] = MAP_COPTER00_ROT_LEFT01;
        SPRITE_DECODE_TABLE[Gamelet.SPRITE_MYCOPTER_LTR] = MAP_COPTER00_ROT_RIGHT01;
        SPRITE_DECODE_TABLE[Gamelet.SPRITE_MYCOPTER_FALL_RTL] = MAP_COPTER00_ROT_RIGHT01;
        SPRITE_DECODE_TABLE[Gamelet.SPRITE_MYCOPTER_FLIGHT_INOUT] = MAP_COPTER00_MOVE_RIGHT01;
        SPRITE_DECODE_TABLE[Gamelet.SPRITE_MYCOPTER_RIGHT] = MAP_COPTER00_MOVE_RIGHT01;
        SPRITE_DECODE_TABLE[Gamelet.SPRITE_MYCOPTER_LEFT] = MAP_COPTER00_MOVE_LEFT01;

        SPRITE_DECODE_TABLE[Gamelet.SPRITE_COPTER1_LEFT] = MAP_COPTER01_MOVE_LEFT01;
        SPRITE_DECODE_TABLE[Gamelet.SPRITE_COPTER1_RIGHT] = MAP_COPTER01_MOVE_RIGHT01;
        SPRITE_DECODE_TABLE[Gamelet.SPRITE_COPTER1_LTR] = MAP_COPTER01_ROT_TORIGHT01;
        SPRITE_DECODE_TABLE[Gamelet.SPRITE_COPTER1_RTL] = MAP_COPTER01_ROT_TOLEFT01;
        SPRITE_DECODE_TABLE[Gamelet.SPRITE_COPTER1_FALL_LTR] = MAP_COPTER01_ROT_TORIGHT01;
        SPRITE_DECODE_TABLE[Gamelet.SPRITE_COPTER1_FALL_RTL] = MAP_COPTER01_ROT_TOLEFT01;

        SPRITE_DECODE_TABLE[Gamelet.SPRITE_COPTER2_LEFT] = MAP_COPTER02_MOVE_LEFT01;
        SPRITE_DECODE_TABLE[Gamelet.SPRITE_COPTER2_RIGHT] = MAP_COPTER02_MOVE_RIGHT01;
        SPRITE_DECODE_TABLE[Gamelet.SPRITE_COPTER2_LTR] = MAP_COPTER02_ROT_TORIGHT01;
        SPRITE_DECODE_TABLE[Gamelet.SPRITE_COPTER2_RTL] = MAP_COPTER02_ROT_TOLEFT01;
        SPRITE_DECODE_TABLE[Gamelet.SPRITE_COPTER2_FALL_LTR] = MAP_COPTER02_ROT_TORIGHT01;
        SPRITE_DECODE_TABLE[Gamelet.SPRITE_COPTER2_FALL_RTL] = MAP_COPTER02_ROT_TOLEFT01;

        SPRITE_DECODE_TABLE[Gamelet.SPRITE_COPTER3_LEFT] = MAP_COPTER03_MOVE_LEFT01;
        SPRITE_DECODE_TABLE[Gamelet.SPRITE_COPTER3_RIGHT] = MAP_COPTER03_MOVE_RIGHT01;
        SPRITE_DECODE_TABLE[Gamelet.SPRITE_COPTER3_LTR] = MAP_COPTER03_ROT_01;
        SPRITE_DECODE_TABLE[Gamelet.SPRITE_COPTER3_RTL] = MAP_COPTER03_ROT_01;
        SPRITE_DECODE_TABLE[Gamelet.SPRITE_COPTER3_FALL_LTR] = MAP_COPTER03_ROT_01;
        SPRITE_DECODE_TABLE[Gamelet.SPRITE_COPTER3_FALL_RTL] = MAP_COPTER03_ROT_01;

        SPRITE_DECODE_TABLE[Gamelet.SPRITE_COPTER4_LEFT] = MAP_COPTER04_MOVE_LEFT01;
        SPRITE_DECODE_TABLE[Gamelet.SPRITE_COPTER4_RIGHT] = MAP_COPTER04_MOVE_RIGHT01;
        SPRITE_DECODE_TABLE[Gamelet.SPRITE_COPTER4_LTR] = MAP_COPTER04_ROT_01;
        SPRITE_DECODE_TABLE[Gamelet.SPRITE_COPTER4_RTL] = MAP_COPTER04_ROT_01;
        SPRITE_DECODE_TABLE[Gamelet.SPRITE_COPTER4_FALL_LTR] = MAP_COPTER04_ROT_01;
        SPRITE_DECODE_TABLE[Gamelet.SPRITE_COPTER4_FALL_RTL] = MAP_COPTER04_ROT_01;

        SPRITE_DECODE_TABLE[Gamelet.SPRITE_FIGHTER_LEFT] = MAP_COPTER05_MOVE_LEFT01;
        SPRITE_DECODE_TABLE[Gamelet.SPRITE_FIGHTER_RIGHT] = MAP_COPTER05_MOVE_RIGHT01;

        SPRITE_DECODE_TABLE[Gamelet.SPRITE_TOWER] = MAP_BOSSCANON_01;

        SPRITE_DECODE_TABLE[Gamelet.SPRITE_BOSS1] = MAP_BOSS1_01;
        SPRITE_DECODE_TABLE[Gamelet.SPRITE_BOSS2] = MAP_BOSS2_01;
        SPRITE_DECODE_TABLE[Gamelet.SPRITE_BOSS3] = MAP_BOSS3_01;
        SPRITE_DECODE_TABLE[Gamelet.SPRITE_BOSS4] = MAP_BOSS4_01;

        SPRITE_DECODE_TABLE[Gamelet.SPRITE_SMOKE] = MAP_SMOKE_01;
        SPRITE_DECODE_TABLE[Gamelet.SPRITE_EXPLOSION] = MAP_EXPLOSION_01;
        SPRITE_DECODE_TABLE[Gamelet.SPRITE_EXPLOSION] = MAP_EXPLOSION_01;

        SPRITE_DECODE_TABLE[Gamelet.SPRITE_FIREBALL] = MAP_METEOR_01;
        SPRITE_DECODE_TABLE[Gamelet.SPRITE_MY_SHELL_LEFT] = MAP_HEROSHELL_LEFT_01;
        SPRITE_DECODE_TABLE[Gamelet.SPRITE_MY_SHELL_RIGTH] = MAP_HEROSHELL_RIGHT_01;
        SPRITE_DECODE_TABLE[Gamelet.SPRITE_ENEMY_SHELL_LEFT] = MAP_ENEMYSHELL_LEFT_01;
        SPRITE_DECODE_TABLE[Gamelet.SPRITE_ENEMY_SHELL_LEFT_DOWN] = MAP_ENEMYSHELL_LEFTDOWN_01;
        SPRITE_DECODE_TABLE[Gamelet.SPRITE_ENEMY_SHELL_LEFT_UP] = MAP_ENEMYSHELL_LEFTUP_01;
        SPRITE_DECODE_TABLE[Gamelet.SPRITE_ENEMY_SHELL_RIGTH] = MAP_ENEMYSHELL_RIGHT_01;

        SPRITE_DECODE_TABLE[Gamelet.SPRITE_BONUS1] = MAP_SCORES5;
        SPRITE_DECODE_TABLE[Gamelet.SPRITE_BONUS2] = MAP_SCORES10;
        SPRITE_DECODE_TABLE[Gamelet.SPRITE_BONUS3] = MAP_SCORES15;
        SPRITE_DECODE_TABLE[Gamelet.SPRITE_BONUS4] = MAP_SCORES20;

        SPRITE_DECODE_TABLE[Gamelet.SPRITE_BONUS_AMMO] = MAP_ICO_SHELL_SMALL;
        SPRITE_DECODE_TABLE[Gamelet.SPRITE_BONUS_LIFE] = MAP_ICO_LIFE_SMALL;
    }

}