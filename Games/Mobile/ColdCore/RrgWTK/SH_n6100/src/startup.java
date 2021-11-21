
import javax.microedition.midlet.MIDlet;
import javax.microedition.midlet.MIDletStateChangeException;
import javax.microedition.lcdui.*;

/**
 * Шаблон для изготовления игровых визуализированных модулей
 *
 * @author Igor A. Maznitsa
 *         (C) 2005 Raydac Research Group Ltd.
 * @version 3.9 (07.06.2005)
 */
public class startup extends MIDlet implements Runnable, CommandListener
{
    //#local MCF_SPLASH = (VENDOR=="NOKIA" && MODEL=="6100")

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
    //#if MODEL=="7650"
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
    //#if MODEL=="X100"
    //$private static final int SCREEN_WIDTH = 128;
    //$private static final int SCREEN_HEIGHT = 128;
    //#endif
    //#if MODEL=="C100"
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
    private static final int SCREEN_WIDTH = 132;
    private static final int SCREEN_HEIGHT = 176;
    //#+

    //#if (VENDOR=="MOTOROLA" && MODEL=="E398")
    //$private static final int GAMESCREEN_OFFSETX = 0;
    //$private static final int GAMESCREEN_OFFSETY = 8;
    //$private static final int GAMESCREEN_WIDTH = 176;
    //$private static final int GAMESCREEN_HEIGHT = 188;
    //#else
    //#if (VENDOR=="NOKIA" && MODEL=="7650")
    //$private static final int GAMESCREEN_OFFSETX = 0;
    //$private static final int GAMESCREEN_OFFSETY = 10;
    //$private static final int GAMESCREEN_WIDTH = 176;
    //$private static final int GAMESCREEN_HEIGHT = 188;
    //#else
    //#if VENDOR=="SIEMENS" && MODEL=="CX65"
    private static final int GAMESCREEN_OFFSETX = 0;
    private static final int GAMESCREEN_OFFSETY = 10;
    private static final int GAMESCREEN_WIDTH = 132;
    private static final int GAMESCREEN_HEIGHT = 156;
    //#else
    //#if VENDOR=="SIEMENS" && MODEL=="C65"
    //$private static final int GAMESCREEN_OFFSETX = 0;
    //$private static final int GAMESCREEN_OFFSETY = 0;
    //$private static final int GAMESCREEN_WIDTH = 128;
    //$private static final int GAMESCREEN_HEIGHT = 128;
    //#else
    //$private static final int GAMESCREEN_OFFSETX = 0;
    //$private static final int GAMESCREEN_OFFSETY = 0;
    //$private static final int GAMESCREEN_WIDTH = SCREEN_WIDTH;
    //$private static final int GAMESCREEN_HEIGHT = SCREEN_HEIGHT;
    //#endif
    //#endif
    //#endif
    //#endif
    //==============================================================

    //#if SHOWSYS
    // Переменные для профилирования производительности
    private static int i_Profil_TimeNextGameStep;
    private static int i_Profil_TimePaint;
    //#endif

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

    private static final String RESOURCE_LOADING_LOGO = "/cc.png";

    //#if MCF_SPLASH
    //$private static final String RESOURCE_SPLASH = "/splash.mcf";
    //$private static final String RESOURCE_WINIMAGE = "/win.mcf";
    //$private static final String RESOURCE_LOSTIMAGE = "/lost.mcf";
    //#else

        //#if VENDOR=="MOTOROLA"
        //$    private static final String RESOURCE_SPLASH = "/splash.jpg";
        //$    private static final String RESOURCE_WINIMAGE = "/win.jpg";
        //$    private static final String RESOURCE_LOSTIMAGE = "/lost.jpg";
        //#else
            private static final String RESOURCE_SPLASH = "/splash.png";
            private static final String RESOURCE_WINIMAGE = "/win.png";
            private static final String RESOURCE_LOSTIMAGE = "/lost.png";
        //#endif
    //#endif

    private static final String RESOURCE_DEALER = "/dealer.png";

    private static final int DELAY_STAGESCREEN = 100;
    private static final int DELAY_FINALSCREEN = 400;

    private static final int LETTER_RECORDNAME_FIRSTCODE = 0x40;
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
    private static class TextForm extends Canvas
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
            //#if VENDOR=="SAMSUNG"
            //$if (_keyCode == -11) return;
            //#endif

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


        public void paint(Graphics _graphics)
        {
            //#if MIDP=="2.0" || (VENDOR=="SIEMENS" && (MODEL=="M55" || MODEL=="S55"))
            //$if (_graphics==null) _graphics = this.getGraphics();
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
                        //$MCFFormRender.paint(_graphics,0,0,true);
                        //#else
                        if (p_Image_Splash != null)
                        {
                            int i_iw = p_Image_Splash.getWidth();
                            int i_ih = p_Image_Splash.getHeight();
                            _graphics.drawImage(p_Image_Splash, (SCREEN_WIDTH - i_iw) / 2, (SCREEN_HEIGHT - i_ih) / 2, 0);
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
                        paintGameOver(_graphics);
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
            //$final int GTL = Graphics.TOP | Graphics.LEFT;
            //$_graphics.setClip(0, 0, i_screenWidth, i_screenHeight);
            // Отрисовываем объем занятой и свободной памяти
            //$Font p_fnt = Font.getFont(Font.FACE_SYSTEM, Font.STYLE_BOLD, Font.SIZE_MEDIUM);
            //$_graphics.setFont(p_fnt);
            //$_graphics.setColor(0);
            //$_graphics.drawString("/*$DEMO_STRING$*/", 1, 0 + 2, GTL);
            //$_graphics.setColor(0xFFFFFF);
            //$_graphics.drawString("/*$DEMO_STRING$*/", 0, 0, GTL);
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
        }

        protected synchronized void keyPressed(int _keyCode)
        {
            //#if VENDOR=="SAMSUNG"
            //$if (_keyCode == -11) return;
            //#endif
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
                        p_parent.keyPressed(_keyCode);
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

                        //#if MIDP=="2.0" || (VENDOR=="SIEMENS" && (MODEL=="M55" || MODEL=="S55"))
                        //$p_InsideCanvas.paint(null);
                        //$p_InsideCanvas.flushGraphics();
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
                        //#if MIDP=="2.0" || (VENDOR=="SIEMENS" && (MODEL=="M55" || MODEL=="S55"))
                        //$paint(null);
                        //$flushGraphics();
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
            //#if VENDOR=="SAMSUNG"
            //$if (_keyCode == -11) return;
            //#endif

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
                            //#if MIDP=="2.0" || (VENDOR=="SIEMENS" && (MODEL=="M55" || MODEL=="S55"))
                            //$p_InsideCanvas.paint(null);
                            //$p_InsideCanvas.flushGraphics();
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
                        p_parent.keyReleased(_keyCode);
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
                        //#if MIDP=="2.0" || (VENDOR=="SIEMENS" && (MODEL=="M55" || MODEL=="S55"))
                        //$p_InsideCanvas.paint(null);
                        //$p_InsideCanvas.flushGraphics();
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

    private static int i_KeyReactionDelayCounter;

    private static int i_LoadingProgress;

    protected static final InsideCanvas p_InsideCanvas = new InsideCanvas();
    private static Class p_This;
    private static startup p_ThisClass;

    protected static Display p_Display;

    private static Image p_Image_LoadingLogo;

    //#if !MCF_SPLASH
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
                        lg_cycle = false;
                    }
            ;
                    break;
                case MODE_GAMEPLAY:
                    {
                        //#ifdefined DEMO_STRING
                        //$if (i_PrevMidletMode == MODE_MAINMENU) i_demoGameCounter = DEMO_DELAY;
                        //#endif

                        //#if MCF_SPLASH
                        //$MCFFormRender.realize();
                        //#else
                        p_Image_Splash = null;
                        //#endif
                        lg_cycle = false;
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
                        //#if !((VENDOR=="SE" && MODEL=="T610") || (VENDOR=="SAMSUNG" && MODEL=="X100")|| (VENDOR=="SAMSUNG" && MODEL=="C100"))
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
                            //$MCFFormRender.init(p_This,s_imageResource);
                            //$MCFFormRender.selectForm(0);
                            //#else
                            p_Image_Splash = Image.createImage(s_imageResource);
                            //#endif
                        }
                        catch (Exception e)
                        {
                            //#if MCF_SPLASH
                            //$MCFFormRender.realize();
                            //#else
                            p_Image_Splash = null;
                            //#endif
                        }

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
                            //$MCFFormRender.realize();
                            //$MCFFormRender.init(p_This,RESOURCE_SPLASH);
                            //$MCFFormRender.selectForm(0);
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
        }

        i_lastPressedKey = -1;

        onMidletModeChanged(i_MidletMode, i_PrevMidletMode);

        if (p_InsideCanvas.isShown())
        {
            //#if MIDP=="2.0" || (VENDOR=="SIEMENS" && (MODEL=="M55" || MODEL=="S55"))
            //$p_InsideCanvas.paint(null);
            //$p_InsideCanvas.flushGraphics();
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
        //#ifdefined DEMO_STRING
        //#else
        try
        {
            Gamelet.pauseGame();
            byte[] ab_array = Gamelet.saveGameStateToByteArray();
            DataStorage.saveDataBlock(ab_array);
            ab_array = null;
            Runtime.getRuntime().gc();
        }
        catch (Exception e)
        {
            s_errorString = e.getMessage();
            setMode(MODE_ERROR);
        }
        //#endif
    }

    private static final void loadGame()
    {
        try
        {
            byte[] ab_array = DataStorage.loadDataBlock();
            Gamelet.loadGameStateFromByteArray(ab_array);
            i_selectedGameStage = Gamelet.i_GameStage;
            i_selectedGameLevel = Gamelet.i_GameLevel;
            if (!onInitNewGame(p_This, i_selectedGameLevel)) throw new Exception("E0");
            if (!onInitNewGameStage(i_selectedGameStage)) throw new Exception("E1");
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
        //#if MIDP=="2.0" || (VENDOR=="SIEMENS" && (MODEL=="M55" || MODEL=="S55"))
        //$p_InsideCanvas.paint(null);
        //$p_InsideCanvas.flushGraphics();
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
        while(i_MidletMode!=MODE_RELEASING)
        {
            try
            {
                Thread.sleep(20);
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
                            //#if !((VENDOR=="SE" && MODEL=="T610") || (VENDOR=="SAMSUNG" && MODEL=="X100")|| (VENDOR=="SAMSUNG" && MODEL=="C100"))
                            ImageManager.clearDynamicCahce();
                            //#endif
                            //#endif

                            saveCurrentGame();
                            setMode(MODE_MAINMENU);
                    }
            ;
                    break;
                case ITEM_ID_ResumeGame:
                    {
                        if (i_MidletMode == MODE_MAINMENU)
                        {
                            loadGame();
                            onAfterLoadGame();
                        }
                        Gamelet.i_GameState = Gamelet.STATE_PAUSED;
                        Gamelet.resumeGameAfterPauseOrPlayerLost();
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
                            //#if MIDP=="2.0" || (VENDOR=="SIEMENS" && (MODEL=="M55" || MODEL=="S55"))
                            //$p_InsideCanvas.paint(null);
                            //$p_InsideCanvas.flushGraphics();
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
            //#if MIDP=="2.0" || (VENDOR=="SIEMENS" && (MODEL=="M55" || MODEL=="S55"))
            //$p_InsideCanvas.paint(null);
            //$p_InsideCanvas.flushGraphics();
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

            p_InsideCanvas.repaint();
            Thread.sleep(i_waitDelay);
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
                            if (!Gamelet.initNewGame(GAMESCREEN_WIDTH, GAMESCREEN_HEIGHT, i_selectedGameLevel) || !onInitNewGame(p_This, i_selectedGameLevel))
                            {
                                s_errorString = "Err1231"; // Can't init new game
                                setMode(MODE_ERROR);
                                continue;
                            }
                            setMode(MODE_SHOWSTAGE);
                            i_KeyFlags = 0;
                        }
                        else
                        {
                            i_timedelay = 200;
                            if (GameMenu.processMenu())
                            {
                                //#if MIDP=="2.0" || (VENDOR=="SIEMENS" && (MODEL=="M55" || MODEL=="S55"))
                                //$p_InsideCanvas.paint(null);
                                //$p_InsideCanvas.flushGraphics();
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
                            //#if MIDP=="2.0" || (VENDOR=="SIEMENS" && (MODEL=="M55" || MODEL=="S55"))
                            //$p_InsideCanvas.paint(null);
                            //$p_InsideCanvas.flushGraphics();
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
                        if (i_CurrentModeTickCounter >= DELAY_STAGESCREEN)
                        {
                            setMode(MODE_GAMEPLAY);
                        }
                    }
            ;
                    break;
                case MODE_GAMEFINAL:
                    {
                        if (i_KeyReactionDelayCounter > 0) i_KeyReactionDelayCounter--;
                        if (i_CurrentModeTickCounter >= DELAY_FINALSCREEN)
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
                            if (i_newCruiserAlertCounter > 0) i_newCruiserAlertCounter--;

                            //#if SHOWSYS
                            i_Profil_TimeNextGameStep = ((int) System.currentTimeMillis() & 0xFFFFFF) - i_Profil_TimeNextGameStep;
                            //#endif

                            i_KeyFlags &= ~Gamelet.BUTTON_SELECTTARGET;

                            if (i_gameletState == Gamelet.STATE_OVER)
                            {
                                DataStorage.resetSavedDataFlag();
                                switch (Gamelet.i_PlayerState)
                                {
                                    case Gamelet.PLAYER_LOST:
                                        {
                                            setMode(MODE_GAMEFINAL);
                                        }
                                ;
                                        break;
                                    case Gamelet.PLAYER_WIN:
                                        {
                                            if ((Gamelet.getSupportedModes() & Gamelet.FLAG_STAGESUPPORT) != 0 && i_selectedGameStage < STAGENUMBER_LAST)
                                            {
                                                i_selectedGameStage++;
                                                setMode(MODE_SHOWSTAGE);
                                                if (!Gamelet.initGameStage(i_selectedGameStage) || !onInitNewGameStage(i_selectedGameStage))
                                                {
                                                    s_errorString = "Err1232"; // Can't init new stage
                                                    setMode(MODE_ERROR);
                                                }
                                            }
                                            else
                                                setMode(MODE_GAMEFINAL);
                                        }
                                ;
                                        break;
                                }
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

                                //#if MIDP=="2.0" || (VENDOR=="SIEMENS" && (MODEL=="M55" || MODEL=="S55"))
                                //$p_InsideCanvas.paint(null);
                                //$p_InsideCanvas.flushGraphics();
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

    private static final int STAGENUMBER_FIRST = 0;
    private static final int STAGENUMBER_LAST = 0;

    //================================Обработка игровых событий=========================
    public static final int processGameAction(int _arg)
    {
        switch (_arg)
        {
            case Gamelet.GAMEACTION_ENEMY_HIT:
                {
                    //#if SOUND
                    if (lg_Option_Sound)
                    {
                        //#if VENDOR!="MOTOROLA" && VENDOR!="SE" && !(VENDOR=="SIEMENS" && (MODEL=="M55" || MODEL=="S55" || MODEL=="CX65" || MODEL=="C65"))
                        SoundManager.playSound(SoundManager.SOUND_HIT, 1);
                        //#endif
                    }
                    //#endif
                    //#if VIBRA
                    if (lg_Option_Vibra)
                    {
                        startVibration(400);
                    }
                    //#endif
                }
        ;
                break;
            case Gamelet.GAMEACTION_MIDDLE_EXPLOSION:
            case Gamelet.GAMEACTION_NEAR_EXPLOSION:
                {
                    //#if SOUND
                    if (lg_Option_Sound)
                    {
                        SoundManager.playSound(SoundManager.SOUND_EXPL, 1);
                    }
                    //#endif
                }
        ;
                break;
            case Gamelet.GAMEACTION_CRUISER_FROM_HYPERSPACE:
                {
                    i_newCruiserAlertCounter = NEWCRUISER_COUNTER_INIT;
                    //#if SOUND
                    if (lg_Option_Sound)
                    {
                        SoundManager.playSound(SoundManager.SOUND_CRUISER, 1);
                    }
                    //#endif
                }
        ;
                break;
        }
        return 0;
    }

    //================================Игровые функции===================================
    /*
        private static final void drawScores(Graphics _g, int _x, int _y, int _zeroNumber, int _value)
        {
            if (_value>(_zeroNumber*10)) return;
            int i_antiAcc = 0;
            final int NUMBER_CHAR_WIDTH = 8;
            while (_zeroNumber > 0)
            {
                int i_acc = _value / _zeroNumber - i_antiAcc;

                ImageManager.drawImage(MAP_DIGIT0 + (i_acc * ImageManager.IMAGEINFO_LENGTH), _g, _x, _y);

                i_antiAcc = (i_antiAcc + i_acc) * 10;

                _x += NUMBER_CHAR_WIDTH;
                _zeroNumber /= 10;
            }
        }

        private static final void drawBar(Graphics _g, int _color, int _x, int _y, int _width, int _height)
        {
            _g.setClip(_x, _y, _width, _height);
            _g.setColor(_color);
            _g.fillRect(_x, _y, _width, _height);
        }
    */

    private static void onAfterLoadGame()
    {
    }

    protected static final void loadGameResources(Class _this) throws Exception
    {
        //#if SHOWSYS
        System.out.println("Init image manager");
        //#endif

        //#if (VENDOR=="SE" && MODEL=="T610") || (VENDOR=="SAMSUNG" && MODEL=="X100")|| (VENDOR=="SAMSUNG" && MODEL=="C100")
        ImageManager.init(p_This, true); //TODO
        //#else
        //$ImageManager.init(p_This,false);
        //#endif

        //#if SOUND
        //#if SHOWSYS
        System.out.println("Init sound manager");
        //#endif
        //#if VENDOR=="SIEMENS" && (MODEL=="CX65" || MODEL=="C65")
        //$SoundManager.initBlock(p_This, 100);
        //#else
        SoundManager.initBlock(p_This, 75);
        //#endif
        //#endif

        //#if TILE
        Image p_img = Image.createImage("/tile.png");

        //#if SHOWSYS
        System.out.println("Tile image loaded");
        //#endif
        byte[] ab_tileArray = (byte[]) Gamelet.loadArray(p_This, "/tile.bin");
        //#if SHOWSYS
        System.out.println("Tile array loaded");
        //#endif

        //#if TILE
        //#if (VENDOR=="SIEMENS" && (MODEL=="S55" || MODEL=="M55" || MODEL=="M50")) || (VENDOR=="NOKIA" && (MODEL=="3410" || MODEL=="3510"))
        p_backgroundTile.initTileBackground(GAMESCREEN_WIDTH, GAMESCREEN_HEIGHT, 8, 8);
        p_backgroundTile.setGameRoomArray(37, ab_tileArray);
        //#else
        //#if (VENDOR=="NOKIA" && MODEL=="6100") || (VENDOR=="SE" && MODEL=="T610") || (VENDOR=="LG" && MODEL=="G1600")
        p_backgroundTile.initTileBackground(GAMESCREEN_WIDTH, GAMESCREEN_HEIGHT, 32, 16);
        p_backgroundTile.setGameRoomArray(12, ab_tileArray);
        //#else
        //#if VENDOR=="SIEMENS" && MODEL=="CX65"
        p_backgroundTile.initTileBackground(GAMESCREEN_WIDTH, GAMESCREEN_HEIGHT, 32, 26);
        p_backgroundTile.setGameRoomArray(12, ab_tileArray);
        //#else
        //#if (VENDOR=="NOKIA" && MODEL=="7650") || (VENDOR=="MOTOROLA" && MODEL=="E398")
        //$p_backgroundTile.initTileBackground(GAMESCREEN_WIDTH,GAMESCREEN_HEIGHT,16,16);
        //$p_backgroundTile.setGameRoomArray(32,ab_tileArray);
        //#else
        //#if (VENDOR=="SAMSUNG" && MODEL=="C100") && (VENDOR=="SAMSUNG" && MODEL=="X100") && (VENDOR=="SIEMENS" && MODEL=="C65")
        //$p_backgroundTile.initTileBackground(GAMESCREEN_WIDTH, GAMESCREEN_HEIGHT, 16, 16);
        //#else
        //$p_backgroundTile.initTileBackground(GAMESCREEN_WIDTH, GAMESCREEN_HEIGHT, 16, 16);
        //#endif
        //$p_backgroundTile.setGameRoomArray(24, ab_tileArray);
        //#endif
        //#endif
        //#endif
        //#endif
        p_backgroundTile.setBlockImage(p_img);
        //#endif
        //#endif


        //#if (VENDOR=="NOKIA" && MODEL=="7650")||(VENDOR=="SIEMENS" && MODEL=="CX65")||(VENDOR=="MOTOROLA" && MODEL=="E398")
        p_FakedPanel = Image.createImage(GAMESCREEN_WIDTH,(SCREEN_HEIGHT-GAMESCREEN_HEIGHT)>>1);
        Graphics p_gr = p_FakedPanel.getGraphics();
        final int COLOR_FAKED = 0x00004C;
        p_gr.setColor(COLOR_FAKED);
        p_gr.fillRect(0,0,p_FakedPanel.getWidth(),p_FakedPanel.getHeight());
        p_gr = null;
        //#endif
    }

    private static final void onMidletModeChanged(int _newMode, int _oldMode)
    {
        switch (_newMode)
        {
                //#if SOUND
                case MODE_RECORDNAME:
            case MODE_GAMEMENU:
            case MODE_GAMEPLAY:
                {
                    SoundManager.stopAllSound();
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

    private static final boolean onInitNewGameStage(int _gameStage)
    {
        for (int li = 0; li < MAX_OBJECTS; li++) ap_sortedObjects[li] = null;
        return true;
    }

    private static final boolean onInitNewGame(Class _this, int _selectedGameLevel)
    {
        try
        {
            i_newCruiserAlertCounter = 0;
        }
        catch (Exception e)
        {
            return false;
        }
        return true;
    }

    private static final void onReleaseGame()
    {
        Runtime.getRuntime().gc();
    }

    private static final boolean onStartApp(startup _this)
    {
        return true;
    }

    private static final void onAppDestroyed()
    {
        //==========================
        //#if SOUND
        SoundManager.stopAllSound();
        //#endif

        //#if VENDOR!="SE"
        ImageManager.release();
        //#endif
        //==========================
        //#if MCF_SPLASH
        //$MCFFormRender.realize();
        //#else
        p_Image_Splash = null;
        //#endif
    }

    private static final int MAX_OBJECTS = Gamelet.MAX_CRUISERS_NUMBER + Gamelet.MAX_FIGHTERS_NUMBER + Gamelet.MAX_EXPLOSIONS_NUMBER + Gamelet.MAX_ENEMY_SHELLS_NUMBER + Gamelet.MAX_PLAYER_SHELLS_NUMBER;
    private static final Sprite[] ap_sortedObjects = new Sprite[MAX_OBJECTS * 3];
    private static final int[] ai_spriteDecodeTable = new int[27];


    public static final boolean areaComponentPaint(Graphics _gr, int _componentIndex, int _x, int _y, int _width, int _height, int _channel)
    {
        return false;
    }

    public static final int getButtonState(int _componentIndex, int _channel)
    {
        return 0;
    }

    private static final void onPaintGame(Graphics _graphics)
    {
        final int GS_OX = GAMESCREEN_OFFSETX;
        final int GS_OY = GAMESCREEN_OFFSETY;
        final int GS_W = GAMESCREEN_WIDTH;
        final int GS_H = GAMESCREEN_HEIGHT;

        ImageManager.p_DestinationGraphics = _graphics;

        // Сортируем игровые объекты для правильного вывода
        int i_farIndex = 0;
        int i_midIndex = MAX_OBJECTS;
        int i_nearIndex = MAX_OBJECTS << 1;

        final Sprite[] ap_sortedObjects = startup.ap_sortedObjects;

        // Крейсера противника
        Sprite[] ap_array = Gamelet.ap_CruiserSprites;
        int i_li = Gamelet.MAX_CRUISERS_NUMBER;
        while ((--i_li) >= 0)
        {
            Sprite p_spr = ap_array[i_li];
            if (!p_spr.lg_SpriteActive) continue;
            switch (p_spr.i_ObjectType)
            {
                case Gamelet.SPRITE_ENEMY_CRUISER_DESTROYED_LEFT:
                case Gamelet.SPRITE_ENEMY_CRUISER_LEFT:
                case Gamelet.SPRITE_ENEMY_CRUISER_TO_HYPERJUMP_LEFT:
                case Gamelet.SPRITE_ENEMY_CRUISER_FROM_HYPERJUMP_LEFT:
                    {
                        ap_sortedObjects[i_farIndex++] = p_spr;
                    }
            ;
                    break;
            }
        }

        i_li = Gamelet.MAX_CRUISERS_NUMBER;
        while ((--i_li) >= 0)
        {
            Sprite p_spr = ap_array[i_li];
            if (!p_spr.lg_SpriteActive) continue;
            switch (p_spr.i_ObjectType)
            {
                case Gamelet.SPRITE_ENEMY_CRUISER_DESTROYED_RIGHT:
                case Gamelet.SPRITE_ENEMY_CRUISER_RIGHT:
                case Gamelet.SPRITE_ENEMY_CRUISER_TO_HYPERJUMP_RIGHT:
                case Gamelet.SPRITE_ENEMY_CRUISER_FROM_HYPERJUMP_RIGHT:
                    {
                        ap_sortedObjects[i_farIndex++] = p_spr;
                    }
            ;
                    break;
            }
        }

        // Истребители противника
        ap_array = Gamelet.ap_FighterSprites;
        i_li = Gamelet.MAX_FIGHTERS_NUMBER;
        while ((--i_li) >= 0)
        {
            Sprite p_spr = ap_array[i_li];
            if (!p_spr.lg_SpriteActive) continue;
            switch (p_spr.i_ObjectType)
            {
                case Gamelet.SPRITE_ENEMY_BACK_FIGHTER_LONG:
                case Gamelet.SPRITE_ENEMY_FRONT_FIGHTER_LONG:
                    {
                        ap_sortedObjects[i_farIndex++] = p_spr;
                    }
            ;
                    break;
                case Gamelet.SPRITE_ENEMY_BACK_FIGHTER_MIDDLE:
                case Gamelet.SPRITE_ENEMY_FRONT_FIGHTER_MIDDLE:
                    {
                        ap_sortedObjects[i_midIndex++] = p_spr;
                    }
            ;
                    break;
                case Gamelet.SPRITE_ENEMY_BACK_FIGHTER_NEAR:
                case Gamelet.SPRITE_ENEMY_FRONT_FIGHTER_NEAR:
                    {
                        ap_sortedObjects[i_nearIndex++] = p_spr;
                    }
            ;
                    break;
            }
        }

        // Снаряды игрока
        ap_array = Gamelet.ap_PlayerShellsSprites;
        i_li = Gamelet.MAX_PLAYER_SHELLS_NUMBER;
        while ((--i_li) >= 0)
        {
            Sprite p_spr = ap_array[i_li];
            if (!p_spr.lg_SpriteActive) continue;

            switch (p_spr.i_ObjectType)
            {
                case Gamelet.SPRITE_PLAYER_SHELL_LONG:
                    {
                        ap_sortedObjects[i_farIndex++] = p_spr;
                    }
            ;
                    break;
                case Gamelet.SPRITE_PLAYER_SHELL_MIDDLE:
                    {
                        ap_sortedObjects[i_midIndex++] = p_spr;
                    }
            ;
                    break;
                case Gamelet.SPRITE_PLAYER_SHELL_NEAR:
                    {
                        ap_sortedObjects[i_nearIndex++] = p_spr;
                    }
            ;
                    break;
            }
        }

        // Выстрелы противника
        ap_array = Gamelet.ap_EnemyShellsSprites;

        for (int li = 0; li < Gamelet.MAX_ENEMY_SHELLS_NUMBER; li++)
        {
            Sprite p_spr = ap_array[li];
            if (!p_spr.lg_SpriteActive) continue;
            switch (p_spr.i_ObjectType)
            {
                case Gamelet.SPRITE_ENEMY_SHELL_LONG:
                    {
                        ap_sortedObjects[i_farIndex++] = p_spr;
                    }
            ;
                    break;
                case Gamelet.SPRITE_ENEMY_SHELL_MIDDLE:
                    {
                        ap_sortedObjects[i_midIndex++] = p_spr;
                    }
            ;
                    break;
                case Gamelet.SPRITE_ENEMY_SHELL_NEAR:
                    {
                        ap_sortedObjects[i_nearIndex++] = p_spr;
                    }
            ;
                    break;
            }
        }

        // Взрывы
        ap_array = Gamelet.ap_ExplosionSprites;
        i_li = Gamelet.MAX_EXPLOSIONS_NUMBER;
        while ((--i_li) >= 0)
        {
            Sprite p_spr = ap_array[i_li];
            if (!p_spr.lg_SpriteActive) continue;
            switch (p_spr.i_ObjectType)
            {
                case Gamelet.SPRITE_EXPLOSION_LONG:
                    {
                        ap_sortedObjects[i_farIndex++] = p_spr;
                    }
            ;
                    break;
                case Gamelet.SPRITE_EXPLOSION_MIDDLE:
                case Gamelet.SPRITE_PARTS_MIDDLE:
                    {
                        ap_sortedObjects[i_midIndex++] = p_spr;
                    }
            ;
                    break;
                case Gamelet.SPRITE_EXPLOSION_NEAR:
                case Gamelet.SPRITE_PARTS_NEAR:
                    {
                        ap_sortedObjects[i_nearIndex++] = p_spr;
                    }
            ;
                    break;
            }
        }

        int i8_viewX = Gamelet.i8_viewRectX + 0x7F;

        // Отрисовываем задний фон
        //#if TILE
        p_backgroundTile.setXY((i8_viewX) >> 8, 0);

        //#if (VENDOR=="SAMSUNG" && MODEL=="C100") || (VENDOR=="SAMSUNG" && MODEL=="X100") || (VENDOR=="SIEMENS" && (MODEL=="S55" || MODEL=="M55" || MODEL=="M50")) || (VENDOR=="NOKIA" && (MODEL=="7650" || MODEL=="3410" || MODEL=="3510"))
        //$p_backgroundTile.drawBufferToGraphics(_graphics, GS_OX, GS_OY);
        //#else
        p_backgroundTile.directPaint(_graphics, GS_OX, GS_OY);
        _graphics.setClip(GS_OX, GS_OY, GS_W, GS_H);
        //#endif
        //#else
        //$_graphics.setColor(0);
        //$_graphics.fillRect(GS_OX, GS_OY, GS_W, GS_H);
        //$drawStarField(_graphics);
        //#endif

        // Выводим объекты
        final int LIM = MAX_OBJECTS * 3;
        final int[] ai_spriteDT = ai_spriteDecodeTable;
        for (int i_startIndex = 0; i_startIndex < LIM; i_startIndex++)
        {
            Sprite p_spr = ap_sortedObjects[i_startIndex];
            if (p_spr == null) continue;
            ap_sortedObjects[i_startIndex] = null;

            int i_sprX = (p_spr.i_ScreenX - i8_viewX) >> 8;
            int i_sprW = (p_spr.i_width) >> 8;
            if (i_sprX > GS_W || i_sprX + i_sprW < 0) continue;
            int i_sprY = (p_spr.i_ScreenY) >> 8;

            //#if VENDOR=="NOKIA" && MODEL=="6100"
            //$switch (p_spr.i_ObjectType)
            //${
            //$    case Gamelet.SPRITE_ENEMY_BACK_FIGHTER_LONG:
            //$    case Gamelet.SPRITE_ENEMY_BACK_FIGHTER_MIDDLE:
            //$    case Gamelet.SPRITE_ENEMY_BACK_FIGHTER_NEAR:
            //$    case Gamelet.SPRITE_ENEMY_FRONT_FIGHTER_LONG:
            //$    case Gamelet.SPRITE_ENEMY_FRONT_FIGHTER_MIDDLE:
            //$    case Gamelet.SPRITE_ENEMY_FRONT_FIGHTER_NEAR:
            //$        {
            //$            // Выводим с модификацией
            //$            boolean lg_vertmirror = false;
            //$            boolean lg_horzmirror = false;
            //$            int i_frame = p_spr.i_Frame;
            //$
            //$            switch (p_spr.i_Frame)
            //$            {
            //$                case 0:
            //$                case 1:
            //$                    break;
            //$                case 2:
            //$                    {
            //$                        i_frame = 0;
            //$                        lg_horzmirror = true;
            //$                    }
            //$           ;
            //$                   break;
            //$                case 3:
            //$                case 4:
            //$                    i_frame --;break;
            //$                case 5:
            //$                    {
            //$                        i_frame = 2;
            //$                        lg_horzmirror = true;
            //$                    }
            //$            ;
            //$                    break;
            //$                case 6:
            //$                case 7:
            //$                    i_frame -=2;break;
            //$                case 8:
            //$                    {
            //$                        i_frame = 4;
            //$                        lg_horzmirror = true;
            //$                    }
            //$            ;
            //$                    break;
            //$            }
            //$
            //$            i_frame *= ImageManager.IMAGEINFO_LENGTH;
            //$            i_frame += ai_spriteDecodeTable[p_spr.i_ObjectType];
            //$            ImageManager.drawImage(i_frame, i_sprX+GS_OX, i_sprY+GS_OY,lg_vertmirror,lg_horzmirror);
            //$        }
            //$;
            //$        break;
            //$        default:
            //$       {
            //$           int i_frame = p_spr.i_Frame * ImageManager.IMAGEINFO_LENGTH;
            //$            i_frame += ai_spriteDecodeTable[p_spr.i_ObjectType];
            //$            ImageManager.drawImage(i_frame, i_sprX+GS_OX, i_sprY+GS_OY);
            //$        }
            //$ }
            //#else
            int i_frame = p_spr.i_Frame * ImageManager.IMAGEINFO_LENGTH;
            i_frame += ai_spriteDT[p_spr.i_ObjectType];

            ImageManager.drawImage(i_frame, i_sprX + GS_OX, i_sprY + GS_OY);
            //#endif
        }

        Sprite p_spr = null;
        int i_sprX, i_sprY;

        // Отрисовываем стволы игрока
        // Прицел
        p_spr = Gamelet.p_PlayerSight;

        i_sprX = (p_spr.i_ScreenX - i8_viewX) >> 8;
        i_sprY = (p_spr.i_ScreenY) >> 8;

        //#if (VENDOR=="NOKIA" && MODEL=="6100") || (VENDOR=="SE" && MODEL=="T610")
        //$final int i_offset = MAP_PLAYER_SIGN_01A + (p_spr.i_Frame<<1) * ImageManager.IMAGEINFO_LENGTH;
        //$int i_frame = i_offset;
        //$ImageManager.drawImage(i_frame, i_sprX + GS_OX, i_sprY + GS_OY);
        //$i_frame += ImageManager.IMAGEINFO_LENGTH;
        //$ImageManager.drawImage(i_frame, i_sprX + GS_OX, i_sprY + GS_OY);
        //#else
        int i_frame = MAP_PLAYER_SIGN01 + (p_spr.i_Frame * ImageManager.IMAGEINFO_LENGTH);
        ImageManager.drawImage(i_frame, i_sprX + GS_OX, i_sprY + GS_OY);
        //#endif

        // Стволы
        p_spr = Gamelet.ap_PlayerBarrels[0];
        i_sprX = (p_spr.i_ScreenX - i8_viewX) >> 8;
        i_sprY = (p_spr.i_ScreenY) >> 8;
        i_frame = MAP_PLAYER_BARREL_LEFT;
        ImageManager.drawImage(i_frame, i_sprX + GS_OX, i_sprY + GS_OY);

        p_spr = Gamelet.ap_PlayerBarrels[1];
        i_sprX = (p_spr.i_ScreenX - i8_viewX) >> 8;

        //#if VENDOR=="MOTOROLA" && MODEL=="C380"
        //$i_sprY = ((p_spr.i_ScreenY) >> 8)-6;
        //#else
        i_sprY = (p_spr.i_ScreenY) >> 8;
        //#endif
        i_frame = MAP_PLAYER_BARREL_BOTTOM;
        ImageManager.drawImage(i_frame, i_sprX + GS_OX, i_sprY + GS_OY);

        p_spr = Gamelet.ap_PlayerBarrels[2];
        i_sprX = (p_spr.i_ScreenX - i8_viewX) >> 8;
        i_sprY = (p_spr.i_ScreenY) >> 8;
        i_frame = MAP_PLAYER_BARREL_RIGHT;
        ImageManager.drawImage(i_frame, i_sprX + GS_OX, i_sprY + GS_OY);

        //---------
        final int I8_XCOEFF = (GS_W << 16) / Gamelet.I8_VIRTUAL_SECTOR_WIDTH;
        //#if (VENDOR=="SIEMENS" && MODEL=="M50") || (VENDOR=="NOKIA" && (MODEL=="3410" || MODEL=="3510"))
        //$final int INDPOS_HEIGHT = 2;
        //#else
        final int INDPOS_HEIGHT = 4;
        //#endif
        // целеуказатель
        if (Gamelet.p_TargetSprite != null)
        {
            final int IND_OFFSET = 5;

            //#if (VENDOR=="SIEMENS" && MODEL=="M50") || (VENDOR=="NOKIA" && MODEL=="3410")
            //$final int COLOR_EN_LFIND = 0xFFFFFF;
            //$final int LIFEIND_EN_HEIGHT = 2;
            //#else
            final int COLOR_EN_LFIND = 0x18C800;
            final int LIFEIND_EN_HEIGHT = 3;
            //#endif

            p_spr = Gamelet.p_TargetSprite;

            // Отрисовываем цель и показатель энергии
            if (Gamelet.lg_IsTargeCruiser)
            {
                // Крейсер
                //#if (VENDOR=="SIEMENS" && MODEL=="M50") || (VENDOR=="NOKIA" && (MODEL=="3410" || MODEL=="3510"))
                //$final int i_lifewidth = 29;
                //$final int i_iconheight = 10;
                //#else
                //#if VENDOR=="SIEMENS" && (MODEL=="M55" || MODEL=="S55")
                //$final int i_lifewidth = 29;
                //$final int i_iconheight = 12;
                //#else
                //#if VENDOR=="SIEMENS" && MODEL=="CX65"
                final int i_lifewidth = 37;
                final int i_iconheight = 23;
                //#else
                //#if (VENDOR=="NOKIA" && MODEL=="7650") || (VENDOR=="MOTOROLA" && MODEL=="E398")
                //$final int i_lifewidth = 50;
                //$final int i_iconheight = 28;
                //#else
                //$final int i_lifewidth = 36;
                //$final int i_iconheight = 19;
                //#endif
                //#endif
                //#endif
                //#endif
                int i_x = GS_OX + GS_W - i_lifewidth - IND_OFFSET;
                int i_y = GS_OY + INDPOS_HEIGHT + IND_OFFSET + 1;
                ImageManager.drawImage(MAP_CRUISER_WIRE, i_x, i_y);
                i_y += i_iconheight + 2;
                final int i_lifeWdth = (i_lifewidth * ((p_spr.i_ObjectState << 8) / Gamelet.ENEMY_CRUISER_INIT_POWER)) >> 8;

                _graphics.setClip(GS_OX, GS_OY, GS_W, GS_H);
                _graphics.setColor(COLOR_EN_LFIND);
                _graphics.fillRect(i_x, i_y, i_lifeWdth, LIFEIND_EN_HEIGHT);
            }
            else
            {
                // Итсребитель
                //#if (VENDOR=="SIEMENS" && MODEL=="M50") || (VENDOR=="NOKIA" && (MODEL=="3410" || MODEL=="3510"))
                //$final int i_iconheight = 11;
                //$final int i_iconwidth = 22;
                //#else
                //#if VENDOR=="SIEMENS" && (MODEL=="M55" || MODEL=="S55")
                //$final int i_iconheight = 13;
                //$final int i_iconwidth = 22;
                //#else
                //#if VENDOR=="SIEMENS" && MODEL=="CX65"
                final int i_iconheight = 26;
                final int i_iconwidth = 28;
                //#else
                //#if (VENDOR=="NOKIA" && MODEL=="7650") || (VENDOR=="MOTOROLA" && MODEL=="E398")
                //$final int i_iconheight = 31;
                //$final int i_iconwidth = 38;
                //#else
                //$final int i_iconheight = 21;
                //$final int i_iconwidth = 28;
                //#endif
                //#endif
                //#endif
                //#endif
                int i_x = GS_OX + GS_W - i_iconwidth - IND_OFFSET;
                int i_y = GS_OY + INDPOS_HEIGHT + IND_OFFSET + 1;
                ImageManager.drawImage(MAP_FIGHTER_WIRE, i_x, i_y);
            }

            i_sprX = (p_spr.i_ScreenX + p_spr.i_col_offsetX - i8_viewX) >> 8;
            i_sprY = (p_spr.i_ScreenY + p_spr.i_col_offsetY) >> 8;
            int i_sprW = (p_spr.i_col_width) >> 8;
            int i_sprH = (p_spr.i_col_height) >> 8;

            //#if (VENDOR=="SIEMENS" && MODEL=="M50") || (VENDOR=="NOKIA" && (MODEL=="3410" || MODEL=="3510"))
            //$final int RAMKA_WIDTH = 28;
            //$final int RAMKA_HEIGHT = 11;
            //#else
            //#if VENDOR=="SIEMENS" && (MODEL=="M55" || MODEL=="S55")
            //$final int RAMKA_WIDTH = 28;
            //$final int RAMKA_HEIGHT = 14;
            //#else
            //#if VENDOR=="SIEMENS" && MODEL=="CX65"
            final int RAMKA_WIDTH = 37;
            final int RAMKA_HEIGHT = 26;
            //#else
            //#if (VENDOR=="NOKIA" && MODEL=="7650") || (VENDOR=="MOTOROLA" && MODEL=="E398")
            //$final int RAMKA_WIDTH = 49;
            //$final int RAMKA_HEIGHT = 32;
            //#else
            //$final int RAMKA_WIDTH = 36;
            //$final int RAMKA_HEIGHT = 22;
            //#endif
            //#endif
            //#endif
            //#endif

            i_sprX += ((i_sprW - RAMKA_WIDTH) >> 1);
            i_sprY += ((i_sprH - RAMKA_HEIGHT) >> 1);

            if (!(i_sprX > GS_W || i_sprX + i_sprW < 0))
            {
                // Рисуем рамку
                //#if (VENDOR=="NOKIA" && MODEL=="6100") || (VENDOR=="SE" && MODEL=="T610")
                //$   _graphics.setClip(0,0,GS_W,GS_H);
                //$   int i_rX = GS_OX + i_sprX;
                //$   int i_rY = GS_OY + i_sprY;
                //$   final int COLOR_RAMKA = 0x03DF01;
                //$   _graphics.setColor(COLOR_RAMKA);
                //$   _graphics.drawRect(i_rX+1,i_rY+1,RAMKA_WIDTH-2,RAMKA_HEIGHT-2);
                //$   _graphics.drawRect(i_rX+3,i_rY+3,RAMKA_WIDTH-6,RAMKA_HEIGHT-6);
                //$   final int i_clX = i_rX + (RAMKA_WIDTH>>1);
                //$   final int i_clY = i_rY + (RAMKA_HEIGHT>>1);
                //$   final int LINE_LEN = 5;
                //$   _graphics.drawLine(i_clX,i_rY,i_clX,i_rY+LINE_LEN);
                //$   _graphics.drawLine(i_clX,i_rY+RAMKA_HEIGHT,i_clX,i_rY+RAMKA_HEIGHT-LINE_LEN);
                //$   _graphics.drawLine(i_rX,i_clY,i_rX+LINE_LEN,i_clY);
                //$   _graphics.drawLine(i_rX+RAMKA_WIDTH-LINE_LEN,i_clY,i_rX+RAMKA_WIDTH,i_clY);
                //#else
                //$ImageManager.drawImage(MAP_RAMKA_TARGET,GS_OX + i_sprX,GS_OY + i_sprY);
                //#endif
            }
            // Указатель
            int i_xT = ((p_spr.i_ScreenX + p_spr.i_col_offsetX + (p_spr.i_col_width >> 1)) * I8_XCOEFF + 0x7FFF) >> 16;

            //#if (VENDOR=="NOKIA" && MODEL=="3510")
            //$ImageManager.drawImage(MAP_TARGET_POINTER, GS_OX + i_xT - 3, GS_OY + INDPOS_HEIGHT);
            //#else
            //#if (VENDOR=="SIEMENS" && MODEL=="M50") || (VENDOR=="NOKIA" && MODEL=="3410")
            //$ImageManager.drawImage(MAP_TARGET_POINTER, GS_OX + i_xT - 3, GS_OY + INDPOS_HEIGHT+2);
            //#else
            //#if VENDOR=="SIEMENS" && (MODEL=="M55" || MODEL=="S55")
            //$ImageManager.drawImage(MAP_TARGET_POINTER, GS_OX + i_xT - 3, GS_OY + INDPOS_HEIGHT);
            //#else
            //#if (VENDOR=="NOKIA" && MODEL=="7650") || (VENDOR=="MOTOROLA" && MODEL=="E398")
            //$ImageManager.drawImage(MAP_TARGET_POINTER, GS_OX + i_xT - 5, GS_OY + INDPOS_HEIGHT);
            //#else
            ImageManager.drawImage(MAP_TARGET_POINTER, GS_OX + i_xT - 4, GS_OY + INDPOS_HEIGHT);
            //#endif
            //#endif
            //#endif
            //#endif
        }


        // ОТрисовка индикаторов
        _graphics.setClip(GS_OX, GS_OY, GS_W, GS_H);

        // Индикатор смещения экрана
        //#if (VENDOR=="SIEMENS" && MODEL=="M50") || (VENDOR=="NOKIA" && MODEL=="3410")
        //$final int COLOR_BCKG = 0xFFFFFF;
        //$final int COLOR_POS = 0x000000;
        //#else
        final int COLOR_BCKG = 0x2180C2;
        final int COLOR_POS = 0x2E1EB5;
        //#endif
        final int VWIDTH = ((GS_W * I8_XCOEFF)) >> 8;

        int i_xP = (Gamelet.i8_viewRectX * I8_XCOEFF + 0x7FFF) >> 16;

        //#if (VENDOR=="SIEMENS" && MODEL=="M50") || (VENDOR=="NOKIA" && MODEL=="3410")
        //$_graphics.setColor(COLOR_BCKG);
        //$_graphics.drawRect(GS_OX, GS_OY, GS_W, INDPOS_HEIGHT+1);
        //$_graphics.fillRect(GS_OX + i_xP, GS_OY + 0, VWIDTH, INDPOS_HEIGHT+1);
        //#else
        _graphics.setColor(COLOR_BCKG);
        _graphics.fillRect(GS_OX, GS_OY, GS_W, INDPOS_HEIGHT);
        _graphics.setColor(COLOR_POS);
        _graphics.fillRect(GS_OX + i_xP, GS_OY + 0, VWIDTH, INDPOS_HEIGHT);
        //#endif


        // Индикатор жизненного показателя
        //#if (VENDOR=="SIEMENS" && MODEL=="M50") || (VENDOR=="NOKIA" && MODEL=="3410")
        //$final int COLOR_LFBCKG = 0x000000;
        //$final int COLOR_LFIND = 0xFFFFFF;
        //#else
        final int COLOR_LFBCKG = 0xFFD52B;
        final int COLOR_LFIND = 0xFA2A38;
        //#endif

        //#if VENDOR=="SIEMENS" && (MODEL=="M55" || MODEL=="S55")
        //$final int LIFEIND_WIDTH = 30;
        //$final int LIFEIND_HEIGHT = 3;
        //#else
        //#if (VENDOR=="SIEMENS" && MODEL=="M50") || (VENDOR=="NOKIA" && (MODEL=="3410" || MODEL=="3510"))
        //$final int LIFEIND_WIDTH = 30;
        //$final int LIFEIND_HEIGHT = 2;
        //#else
        final int LIFEIND_WIDTH = 40;
        final int LIFEIND_HEIGHT = 5;
        //#endif
        //#endif

        //#if (VENDOR=="NOKIA" && (MODEL=="3410" || MODEL=="3510")) || (VENDOR=="SIEMENS" && MODEL=="M50")
        //$final int i_indX = GS_OX + GS_W - LIFEIND_WIDTH - 4;
        //#else
        final int i_indX = GS_OX + GS_W - LIFEIND_WIDTH - 2;
        //#endif

        final int i_indY = GS_OY + GS_H - LIFEIND_HEIGHT - 5;

        final int i_lifeWdth = (LIFEIND_WIDTH * ((Gamelet.i_playerHealth << 8) / Gamelet.PLAYER_INIT_POWER)) >> 8;

        _graphics.setColor(COLOR_LFBCKG);
        _graphics.fillRect(i_indX, i_indY, LIFEIND_WIDTH, LIFEIND_HEIGHT);
        _graphics.setColor(COLOR_LFIND);
        _graphics.fillRect(i_indX, i_indY, i_lifeWdth, LIFEIND_HEIGHT);
        //#if (VENDOR=="SIEMENS" && MODEL=="M50") || (VENDOR=="NOKIA" && MODEL=="3410")
        //$_graphics.drawRect(i_indX-2, i_indY-2, LIFEIND_WIDTH+3, LIFEIND_HEIGHT+3);
        //#endif

        //#if (VENDOR=="NOKIA" && MODEL=="7650") || (VENDOR=="SIEMENS" && MODEL=="CX65") || (VENDOR=="MOTOROLA" && MODEL=="E398")
        _graphics.setClip(0, 0, SCREEN_WIDTH, SCREEN_HEIGHT);
        //        final int BORDER_PANEL_COLOR = 0x00004C;
        //        _graphics.setColor(BORDER_PANEL_COLOR);
        //        _graphics.fillRect(0, 0, SCREEN_WIDTH, GS_OY);
        //        _graphics.fillRect(0, GS_OY + GS_H, SCREEN_WIDTH, GS_OY);
        _graphics.drawImage(p_FakedPanel, 0, 0, 0);
        _graphics.drawImage(p_FakedPanel, 0, GS_OY + GS_H, 0);
        //#endif

        // Индикатор появления нового крейсера
        if (i_newCruiserAlertCounter > 0)
        {
            if (((i_newCruiserAlertCounter >>> 1) & 1) != 0)
            {
                // Рисуем иконку
                //#if (VENDOR=="SIEMENS" && MODEL=="M50") || (VENDOR=="NOKIA" && (MODEL=="3410" || MODEL=="3510"))
                //$ImageManager.drawImage(MAP_ICON_CRUISER, 2, GS_OY + INDPOS_HEIGHT + 3);
                //#else
                ImageManager.drawImage(MAP_ICON_CRUISER, 1, GS_OY + INDPOS_HEIGHT + 1);
                //#endif
            }
        }

        // Индикатор выхода в меню
        //#if (VENDOR=="SIEMENS" && MODEL=="M50") || (VENDOR=="NOKIA" && (MODEL=="3410" || MODEL=="3510"))
        //$final int MENU_IND_WIDTH = 8;
        //$final int MENU_IND_HEIGHT = 7;
        //#else
        //#if VENDOR=="SIEMENS" && (MODEL=="M55" || MODEL=="S55")
        //$final int MENU_IND_WIDTH = 6;
        //$final int MENU_IND_HEIGHT = 6;
        //#else
        //#if VENDOR=="SIEMENS" && MODEL=="CX65"
        final int MENU_IND_WIDTH = 8;
        final int MENU_IND_HEIGHT = 12;
        //#else
        //#if (VENDOR=="NOKIA" && MODEL=="7650") || (VENDOR=="MOTOROLA" && MODEL=="E398")
        //$final int MENU_IND_WIDTH = 11;
        //$final int MENU_IND_HEIGHT = 15;
        //#else
        //$final int MENU_IND_WIDTH = 8;
        //$final int MENU_IND_HEIGHT = 10;
        //#endif
        //#endif
        //#endif
        //#endif


        final int BORDOFFSET = 3;
        final int i_x = GS_OX + BORDOFFSET;
        final int i_y = GS_OY + GS_H - MENU_IND_HEIGHT - BORDOFFSET;
        ImageManager.drawImage(MAP_MENU_POINTER, i_x, i_y);
    }


    private static final void onPaintGameStage(Graphics _graphics)
    {
        _graphics.setColor(0xFFFF00);
        _graphics.fillRect(0, 0, SCREEN_WIDTH, SCREEN_HEIGHT);
        _graphics.setColor(0x0000FF);
        _graphics.drawString(LangBlock.getStringForIndex(StageTXT) + " " + (i_selectedGameStage + 1), 0, 20, 0);
    }

    private static final void paintGameOver(Graphics _graphics)
    {
        _graphics.setColor(COLOR_MAIN_BACKGROUND);
        _graphics.fillRect(0, 0, SCREEN_WIDTH, SCREEN_HEIGHT);

        //#if MCF_SPLASH
        //$MCFFormRender.paint(_graphics,0,0,true);
        //#else
        if (p_Image_Splash != null)
        {
            int i_iw = p_Image_Splash.getWidth();
            int i_ih = p_Image_Splash.getHeight();
            _graphics.drawImage(p_Image_Splash, (SCREEN_WIDTH - i_iw) / 2, (SCREEN_HEIGHT - i_ih) / 2, 0);
        }
        //#endif
    }

    private final static void keyPressed(int _keyCode)
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
                    i_KeyFlags |= Gamelet.BUTTON_SELECTTARGET;
                }
        ;
                break;
            case KEY_CODE_UP:
            case JOY_СODE_UP:
                {
                    i_KeyFlags |= Gamelet.BUTTON_UP;
                    i_KeyFlags &= ~Gamelet.BUTTON_DOWN;
                }
        ;
                break;
            case KEY_CODE_LEFT:
            case JOY_CODE_LEFT:
                {
                    i_KeyFlags |= Gamelet.BUTTON_LEFT;
                    i_KeyFlags &= ~Gamelet.BUTTON_RIGHT;
                }
        ;
                break;
            case KEY_CODE_RIGHT:
            case JOY_CODE_RIGHT:
                {
                    i_KeyFlags |= Gamelet.BUTTON_RIGHT;
                    i_KeyFlags &= ~Gamelet.BUTTON_LEFT;
                }
        ;
                break;
            case KEY_CODE_DOWN:
            case JOY_CODE_DOWN:
                {
                    i_KeyFlags |= Gamelet.BUTTON_DOWN;
                    i_KeyFlags &= ~Gamelet.BUTTON_UP;
                }
        ;
                break;
            case KEY_CODE_FIRE:
            case JOY_CODE_FIRE:
                {
                    i_KeyFlags |= Gamelet.BUTTON_FIRE;
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

    private final void keyReleased(int _keyCode)
    {
        //#if VENDOR=="SAMSUNG"
        //$if (_keyCode == -11) return;
        //#endif

        switch (_keyCode)
        {
            case KEY_CODE_UP:
            case JOY_СODE_UP:
                {
                    i_KeyFlags &= ~Gamelet.BUTTON_UP;
                }
        ;
                break;
            case KEY_CODE_LEFT:
            case JOY_CODE_LEFT:
                {
                    i_KeyFlags &= ~Gamelet.BUTTON_LEFT;
                }
        ;
                break;
            case KEY_CODE_RIGHT:
            case JOY_CODE_RIGHT:
                {
                    i_KeyFlags &= ~Gamelet.BUTTON_RIGHT;
                }
        ;
                break;
            case KEY_CODE_DOWN:
            case JOY_CODE_DOWN:
                {
                    i_KeyFlags &= ~Gamelet.BUTTON_DOWN;
                }
        ;
                break;
            case KEY_CODE_FIRE:
            case JOY_CODE_FIRE:
                {
                    i_KeyFlags &= ~Gamelet.BUTTON_FIRE;
                }
        ;
                break;
            case KEY_CODE_KEY1:
                break;
            case KEY_CODE_KEY2:
                {
                    i_KeyFlags &= ~Gamelet.BUTTON_SELECTTARGET;
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


    //======================================================================
    //#if (VENDOR=="SE" && MODEL=="T610")
    //$ private static final int MAP_BACK_FIGHTER_LONG01 = 0;
    //$ private static final int MAP_BACK_FIGHTER_LONG02 = 7;
    //$ private static final int MAP_BACK_FIGHTER_LONG03 = 14;
    //$ private static final int MAP_BACK_FIGHTER_LONG04 = 21;
    //$ private static final int MAP_BACK_FIGHTER_LONG05 = 28;
    //$ private static final int MAP_BACK_FIGHTER_LONG06 = 35;
    //$ private static final int MAP_BACK_FIGHTER_LONG07 = 42;
    //$ private static final int MAP_BACK_FIGHTER_LONG08 = 49;
    //$ private static final int MAP_BACK_FIGHTER_LONG09 = 56;
    //$ private static final int MAP_BACK_FIGHTER_MIDDLE01 = 63;
    //$ private static final int MAP_BACK_FIGHTER_MIDDLE02 = 70;
    //$ private static final int MAP_BACK_FIGHTER_MIDDLE03 = 77;
    //$ private static final int MAP_BACK_FIGHTER_MIDDLE04 = 84;
    //$ private static final int MAP_BACK_FIGHTER_MIDDLE05 = 91;
    //$ private static final int MAP_BACK_FIGHTER_MIDDLE06 = 98;
    //$ private static final int MAP_BACK_FIGHTER_MIDDLE07 = 105;
    //$ private static final int MAP_BACK_FIGHTER_MIDDLE08 = 112;
    //$ private static final int MAP_BACK_FIGHTER_MIDDLE09 = 119;
    //$ private static final int MAP_BACK_FIGHTER_NEAR01 = 126;
    //$ private static final int MAP_BACK_FIGHTER_NEAR02 = 133;
    //$ private static final int MAP_BACK_FIGHTER_NEAR03 = 140;
    //$ private static final int MAP_BACK_FIGHTER_NEAR04 = 147;
    //$ private static final int MAP_BACK_FIGHTER_NEAR05 = 154;
    //$ private static final int MAP_BACK_FIGHTER_NEAR06 = 161;
    //$ private static final int MAP_BACK_FIGHTER_NEAR07 = 168;
    //$ private static final int MAP_BACK_FIGHTER_NEAR08 = 175;
    //$ private static final int MAP_BACK_FIGHTER_NEAR09 = 182;
    //$ private static final int MAP_CRUISER_DESTROYED_LEFT01 = 189;
    //$ private static final int MAP_CRUISER_DESTROYED_LEFT02 = 196;
    //$ private static final int MAP_CRUISER_DESTROYED_LEFT03 = 203;
    //$ private static final int MAP_CRUISER_DESTROYED_LEFT04 = 210;
    //$ private static final int MAP_CRUISER_DESTROYED_RIGHT01 = 217;
    //$ private static final int MAP_CRUISER_DESTROYED_RIGHT02 = 224;
    //$ private static final int MAP_CRUISER_DESTROYED_RIGHT03 = 231;
    //$ private static final int MAP_CRUISER_DESTROYED_RIGHT04 = 238;
    //$ private static final int MAP_CRUISER_FROM_HUPERJUMP_LEFT01 = 245;
    //$ private static final int MAP_CRUISER_FROM_HUPERJUMP_LEFT02 = 252;
    //$ private static final int MAP_CRUISER_FROM_HUPERJUMP_LEFT03 = 259;
    //$ private static final int MAP_CRUISER_FROM_HUPERJUMP_LEFT04 = 266;
    //$ private static final int MAP_CRUISER_FROM_HUPERJUMP_LEFT05 = 273;
    //$ private static final int MAP_CRUISER_FROM_HUPERJUMP_RIGHT01 = 280;
    //$ private static final int MAP_CRUISER_FROM_HUPERJUMP_RIGHT02 = 287;
    //$ private static final int MAP_CRUISER_FROM_HUPERJUMP_RIGHT03 = 294;
    //$ private static final int MAP_CRUISER_FROM_HUPERJUMP_RIGHT04 = 301;
    //$ private static final int MAP_CRUISER_FROM_HUPERJUMP_RIGHT05 = 308;
    //$ private static final int MAP_CRUISER_LEFT = 315;
    //$ private static final int MAP_CRUISER_RIGHT = 322;
    //$ private static final int MAP_CRUISER_SHELL_LONG = 329;
    //$ private static final int MAP_CRUISER_SHELL_MIDDLE01 = 336;
    //$ private static final int MAP_CRUISER_SHELL_MIDDLE02 = 343;
    //$ private static final int MAP_CRUISER_SHELL_MIDDLE03 = 350;
    //$ private static final int MAP_CRUISER_SHELL_NEAR01 = 357;
    //$ private static final int MAP_CRUISER_SHELL_NEAR02 = 364;
    //$ private static final int MAP_CRUISER_SHELL_NEAR03 = 371;
    //$ private static final int MAP_CRUISER_TO_HUPERJUMP_LEFT01 = 378;
    //$ private static final int MAP_CRUISER_TO_HUPERJUMP_LEFT02 = 385;
    //$ private static final int MAP_CRUISER_TO_HUPERJUMP_LEFT03 = 392;
    //$ private static final int MAP_CRUISER_TO_HUPERJUMP_LEFT04 = 399;
    //$ private static final int MAP_CRUISER_TO_HUPERJUMP_LEFT05 = 406;
    //$ private static final int MAP_CRUISER_TO_HUPERJUMP_LEFT06 = 413;
    //$ private static final int MAP_CRUISER_TO_HUPERJUMP_RIGHT01 = 420;
    //$ private static final int MAP_CRUISER_TO_HUPERJUMP_RIGHT02 = 427;
    //$ private static final int MAP_CRUISER_TO_HUPERJUMP_RIGHT03 = 434;
    //$ private static final int MAP_CRUISER_TO_HUPERJUMP_RIGHT04 = 441;
    //$ private static final int MAP_CRUISER_TO_HUPERJUMP_RIGHT05 = 448;
    //$ private static final int MAP_CRUISER_TO_HUPERJUMP_RIGHT06 = 455;
    //$ private static final int MAP_CRUISER_WIRE = 462;
    //$ private static final int MAP_EXPLOSION_LONG01 = 469;
    //$ private static final int MAP_EXPLOSION_LONG02 = 476;
    //$ private static final int MAP_EXPLOSION_LONG03 = 483;
    //$ private static final int MAP_EXPLOSION_LONG04 = 490;
    //$ private static final int MAP_EXPLOSION_LONG05 = 497;
    //$ private static final int MAP_EXPLOSION_LONG06 = 504;
    //$ private static final int MAP_EXPLOSION_LONG07 = 511;
    //$ private static final int MAP_EXPLOSION_LONG08 = 518;
    //$ private static final int MAP_EXPLOSION_MIDDLE01 = 525;
    //$ private static final int MAP_EXPLOSION_MIDDLE02 = 532;
    //$ private static final int MAP_EXPLOSION_MIDDLE03 = 539;
    //$ private static final int MAP_EXPLOSION_MIDDLE04 = 546;
    //$ private static final int MAP_EXPLOSION_MIDDLE05 = 553;
    //$ private static final int MAP_EXPLOSION_MIDDLE06 = 560;
    //$ private static final int MAP_EXPLOSION_MIDDLE07 = 567;
    //$ private static final int MAP_EXPLOSION_MIDDLE08 = 574;
    //$ private static final int MAP_EXPLOSION_NEAR01 = 581;
    //$ private static final int MAP_EXPLOSION_NEAR02 = 588;
    //$ private static final int MAP_EXPLOSION_NEAR03 = 595;
    //$ private static final int MAP_EXPLOSION_NEAR04 = 602;
    //$ private static final int MAP_EXPLOSION_NEAR05 = 609;
    //$ private static final int MAP_EXPLOSION_NEAR06 = 616;
    //$ private static final int MAP_EXPLOSION_NEAR07 = 623;
    //$ private static final int MAP_EXPLOSION_NEAR08 = 630;
    //$ private static final int MAP_EXPLOSION_NEAR09 = 637;
    //$ private static final int MAP_FIGHTER_SHELL_LONG = 644;
    //$ private static final int MAP_FIGHTER_SHELL_MIDDLE01 = 651;
    //$ private static final int MAP_FIGHTER_SHELL_MIDDLE02 = 658;
    //$ private static final int MAP_FIGHTER_SHELL_MIDDLE03 = 665;
    //$ private static final int MAP_FIGHTER_SHELL_NEAR01 = 672;
    //$ private static final int MAP_FIGHTER_SHELL_NEAR02 = 679;
    //$ private static final int MAP_FIGHTER_SHELL_NEAR03 = 686;
    //$ private static final int MAP_FIGHTER_WIRE = 693;
    //$ private static final int MAP_FRONT_FIGHTER_LONG01 = 700;
    //$ private static final int MAP_FRONT_FIGHTER_LONG02 = 707;
    //$ private static final int MAP_FRONT_FIGHTER_LONG03 = 714;
    //$ private static final int MAP_FRONT_FIGHTER_LONG04 = 721;
    //$ private static final int MAP_FRONT_FIGHTER_LONG05 = 728;
    //$ private static final int MAP_FRONT_FIGHTER_LONG06 = 735;
    //$ private static final int MAP_FRONT_FIGHTER_LONG07 = 742;
    //$ private static final int MAP_FRONT_FIGHTER_LONG08 = 749;
    //$ private static final int MAP_FRONT_FIGHTER_LONG09 = 756;
    //$ private static final int MAP_FRONT_FIGHTER_MIDDLE01 = 763;
    //$ private static final int MAP_FRONT_FIGHTER_MIDDLE02 = 770;
    //$ private static final int MAP_FRONT_FIGHTER_MIDDLE03 = 777;
    //$ private static final int MAP_FRONT_FIGHTER_MIDDLE04 = 784;
    //$ private static final int MAP_FRONT_FIGHTER_MIDDLE05 = 791;
    //$ private static final int MAP_FRONT_FIGHTER_MIDDLE06 = 798;
    //$ private static final int MAP_FRONT_FIGHTER_MIDDLE07 = 805;
    //$ private static final int MAP_FRONT_FIGHTER_MIDDLE08 = 812;
    //$ private static final int MAP_FRONT_FIGHTER_MIDDLE09 = 819;
    //$ private static final int MAP_FRONT_FIGHTER_NEAR01 = 826;
    //$ private static final int MAP_FRONT_FIGHTER_NEAR02 = 833;
    //$ private static final int MAP_FRONT_FIGHTER_NEAR03 = 840;
    //$ private static final int MAP_FRONT_FIGHTER_NEAR04 = 847;
    //$ private static final int MAP_FRONT_FIGHTER_NEAR05 = 854;
    //$ private static final int MAP_FRONT_FIGHTER_NEAR06 = 861;
    //$ private static final int MAP_FRONT_FIGHTER_NEAR07 = 868;
    //$ private static final int MAP_FRONT_FIGHTER_NEAR08 = 875;
    //$ private static final int MAP_FRONT_FIGHTER_NEAR09 = 882;
    //$ private static final int MAP_ICON_CRUISER = 889;
    //$ private static final int MAP_MENU_POINTER = 896;
    //$ private static final int MAP_PARTS_MIDDLE01 = 903;
    //$ private static final int MAP_PARTS_MIDDLE02 = 910;
    //$ private static final int MAP_PARTS_MIDDLE03 = 917;
    //$ private static final int MAP_PARTS_MIDDLE04 = 924;
    //$ private static final int MAP_PARTS_MIDDLE05 = 931;
    //$ private static final int MAP_PARTS_MIDDLE06 = 938;
    //$ private static final int MAP_PARTS_MIDDLE07 = 945;
    //$ private static final int MAP_PARTS_MIDDLE08 = 952;
    //$ private static final int MAP_PARTS_MIDDLE09 = 959;
    //$ private static final int MAP_PARTS_MIDDLE10 = 966;
    //$ private static final int MAP_PARTS_NEAR01 = 973;
    //$ private static final int MAP_PARTS_NEAR02 = 980;
    //$ private static final int MAP_PARTS_NEAR03 = 987;
    //$ private static final int MAP_PARTS_NEAR04 = 994;
    //$ private static final int MAP_PARTS_NEAR05 = 1001;
    //$ private static final int MAP_PARTS_NEAR06 = 1008;
    //$ private static final int MAP_PARTS_NEAR07 = 1015;
    //$ private static final int MAP_PARTS_NEAR08 = 1022;
    //$ private static final int MAP_PARTS_NEAR09 = 1029;
    //$ private static final int MAP_PARTS_NEAR10 = 1036;
    //$ private static final int MAP_PLAYER_BARREL_BOTTOM = 1043;
    //$ private static final int MAP_PLAYER_BARREL_LEFT = 1050;
    //$ private static final int MAP_PLAYER_BARREL_RIGHT = 1057;
    //$ private static final int MAP_PLAYER_SHELL_LONG = 1064;
    //$ private static final int MAP_PLAYER_SHELL_MIDDLE01 = 1071;
    //$ private static final int MAP_PLAYER_SHELL_MIDDLE02 = 1078;
    //$ private static final int MAP_PLAYER_SHELL_MIDDLE03 = 1085;
    //$ private static final int MAP_PLAYER_SHELL_NEAR01 = 1092;
    //$ private static final int MAP_PLAYER_SHELL_NEAR02 = 1099;
    //$ private static final int MAP_PLAYER_SHELL_NEAR03 = 1106;
    //$ private static final int MAP_PLAYER_SIGN_01A = 1113;
    //$ private static final int MAP_PLAYER_SIGN_01B = 1120;
    //$ private static final int MAP_PLAYER_SIGN_02A = 1127;
    //$ private static final int MAP_PLAYER_SIGN_02B = 1134;
    //$ private static final int MAP_PLAYER_SIGN_03A = 1141;
    //$ private static final int MAP_PLAYER_SIGN_03B = 1148;
    //$ private static final int MAP_PLAYER_SIGN_04A = 1155;
    //$ private static final int MAP_PLAYER_SIGN_04B = 1162;
    //$ private static final int MAP_TARGET_POINTER = 1169;


    //#_if VENDOR=="SE" && MODEL=="T610"
    //#global IMAGES_DYNAMIC =false
    //#global IMAGES_EXTERNAL =true
    //#global IMAGES_NORMAL =true
    //#global IMAGES_LINK =true
    //#global IMAGES_EXT2ONE =true
    //#_endif
    //#endif

    //#if (VENDOR=="NOKIA" && MODEL=="7650") || (VENDOR=="SIEMENS" && MODEL=="CX65") || (VENDOR=="MOTOROLA" && MODEL=="E398")
    //$private static final int MAP_BACK_FIGHTER_LONG01 = 0;
    //$private static final int MAP_BACK_FIGHTER_LONG02 = 7;
    //$private static final int MAP_BACK_FIGHTER_LONG03 = 14;
    //$private static final int MAP_BACK_FIGHTER_LONG04 = 21;
    //$private static final int MAP_BACK_FIGHTER_LONG05 = 28;
    //$private static final int MAP_BACK_FIGHTER_LONG06 = 35;
    //$private static final int MAP_BACK_FIGHTER_LONG07 = 42;
    //$private static final int MAP_BACK_FIGHTER_LONG08 = 49;
    //$private static final int MAP_BACK_FIGHTER_LONG09 = 56;
    //$private static final int MAP_BACK_FIGHTER_MIDDLE01 = 63;
    //$private static final int MAP_BACK_FIGHTER_MIDDLE02 = 70;
    //$private static final int MAP_BACK_FIGHTER_MIDDLE03 = 77;
    //$private static final int MAP_BACK_FIGHTER_MIDDLE04 = 84;
    //$private static final int MAP_BACK_FIGHTER_MIDDLE05 = 91;
    //$private static final int MAP_BACK_FIGHTER_MIDDLE06 = 98;
    //$private static final int MAP_BACK_FIGHTER_MIDDLE07 = 105;
    //$private static final int MAP_BACK_FIGHTER_MIDDLE08 = 112;
    //$private static final int MAP_BACK_FIGHTER_MIDDLE09 = 119;
    //$private static final int MAP_BACK_FIGHTER_NEAR01 = 126;
    //$private static final int MAP_BACK_FIGHTER_NEAR02 = 133;
    //$private static final int MAP_BACK_FIGHTER_NEAR03 = 140;
    //$private static final int MAP_BACK_FIGHTER_NEAR04 = 147;
    //$private static final int MAP_BACK_FIGHTER_NEAR05 = 154;
    //$private static final int MAP_BACK_FIGHTER_NEAR06 = 161;
    //$private static final int MAP_BACK_FIGHTER_NEAR07 = 168;
    //$private static final int MAP_BACK_FIGHTER_NEAR08 = 175;
    //$private static final int MAP_BACK_FIGHTER_NEAR09 = 182;
    //$private static final int MAP_CRUISER_DESTROYED_LEFT01 = 189;
    //$private static final int MAP_CRUISER_DESTROYED_LEFT02 = 196;
    //$private static final int MAP_CRUISER_DESTROYED_LEFT03 = 203;
    //$private static final int MAP_CRUISER_DESTROYED_LEFT04 = 210;
    //$private static final int MAP_CRUISER_DESTROYED_RIGHT01 = 217;
    //$private static final int MAP_CRUISER_DESTROYED_RIGHT02 = 224;
    //$private static final int MAP_CRUISER_DESTROYED_RIGHT03 = 231;
    //$private static final int MAP_CRUISER_DESTROYED_RIGHT04 = 238;
    //$private static final int MAP_CRUISER_FROM_HUPERJUMP_LEFT01 = 245;
    //$private static final int MAP_CRUISER_FROM_HUPERJUMP_LEFT02 = 252;
    //$private static final int MAP_CRUISER_FROM_HUPERJUMP_LEFT03 = 259;
    //$private static final int MAP_CRUISER_FROM_HUPERJUMP_LEFT04 = 266;
    //$private static final int MAP_CRUISER_FROM_HUPERJUMP_LEFT05 = 273;
    //$private static final int MAP_CRUISER_FROM_HUPERJUMP_RIGHT01 = 280;
    //$private static final int MAP_CRUISER_FROM_HUPERJUMP_RIGHT02 = 287;
    //$private static final int MAP_CRUISER_FROM_HUPERJUMP_RIGHT03 = 294;
    //$private static final int MAP_CRUISER_FROM_HUPERJUMP_RIGHT04 = 301;
    //$private static final int MAP_CRUISER_FROM_HUPERJUMP_RIGHT05 = 308;
    //$private static final int MAP_CRUISER_LEFT = 315;
    //$private static final int MAP_CRUISER_RIGHT = 322;
    //$private static final int MAP_CRUISER_SHELL_LONG = 329;
    //$private static final int MAP_CRUISER_SHELL_MIDDLE01 = 336;
    //$private static final int MAP_CRUISER_SHELL_MIDDLE02 = 343;
    //$private static final int MAP_CRUISER_SHELL_MIDDLE03 = 350;
    //$private static final int MAP_CRUISER_SHELL_NEAR01 = 357;
    //$private static final int MAP_CRUISER_SHELL_NEAR02 = 364;
    //$private static final int MAP_CRUISER_SHELL_NEAR03 = 371;
    //$private static final int MAP_CRUISER_TO_HUPERJUMP_LEFT01 = 378;
    //$private static final int MAP_CRUISER_TO_HUPERJUMP_LEFT02 = 385;
    //$private static final int MAP_CRUISER_TO_HUPERJUMP_LEFT03 = 392;
    //$private static final int MAP_CRUISER_TO_HUPERJUMP_LEFT04 = 399;
    //$private static final int MAP_CRUISER_TO_HUPERJUMP_LEFT05 = 406;
    //$private static final int MAP_CRUISER_TO_HUPERJUMP_LEFT06 = 413;
    //$private static final int MAP_CRUISER_TO_HUPERJUMP_RIGHT01 = 420;
    //$private static final int MAP_CRUISER_TO_HUPERJUMP_RIGHT02 = 427;
    //$private static final int MAP_CRUISER_TO_HUPERJUMP_RIGHT03 = 434;
    //$private static final int MAP_CRUISER_TO_HUPERJUMP_RIGHT04 = 441;
    //$private static final int MAP_CRUISER_TO_HUPERJUMP_RIGHT05 = 448;
    //$private static final int MAP_CRUISER_TO_HUPERJUMP_RIGHT06 = 455;
    //$private static final int MAP_CRUISER_WIRE = 462;
    //$private static final int MAP_EXPLOSION_LONG01 = 469;
    //$private static final int MAP_EXPLOSION_LONG02 = 476;
    //$private static final int MAP_EXPLOSION_LONG03 = 483;
    //$private static final int MAP_EXPLOSION_LONG04 = 490;
    //$private static final int MAP_EXPLOSION_LONG05 = 497;
    //$private static final int MAP_EXPLOSION_LONG06 = 504;
    //$private static final int MAP_EXPLOSION_LONG07 = 511;
    //$private static final int MAP_EXPLOSION_LONG08 = 518;
    //$private static final int MAP_EXPLOSION_MIDDLE01 = 525;
    //$private static final int MAP_EXPLOSION_MIDDLE02 = 532;
    //$private static final int MAP_EXPLOSION_MIDDLE03 = 539;
    //$private static final int MAP_EXPLOSION_MIDDLE04 = 546;
    //$private static final int MAP_EXPLOSION_MIDDLE05 = 553;
    //$private static final int MAP_EXPLOSION_MIDDLE06 = 560;
    //$private static final int MAP_EXPLOSION_MIDDLE07 = 567;
    //$private static final int MAP_EXPLOSION_MIDDLE08 = 574;
    //$private static final int MAP_EXPLOSION_NEAR01 = 581;
    //$private static final int MAP_EXPLOSION_NEAR02 = 588;
    //$private static final int MAP_EXPLOSION_NEAR03 = 595;
    //$private static final int MAP_EXPLOSION_NEAR04 = 602;
    //$private static final int MAP_EXPLOSION_NEAR05 = 609;
    //$private static final int MAP_EXPLOSION_NEAR06 = 616;
    //$private static final int MAP_EXPLOSION_NEAR07 = 623;
    //$private static final int MAP_EXPLOSION_NEAR08 = 630;
    //$private static final int MAP_EXPLOSION_NEAR09 = 637;
    //$private static final int MAP_FIGHTER_SHELL_LONG = 644;
    //$private static final int MAP_FIGHTER_SHELL_MIDDLE01 = 651;
    //$private static final int MAP_FIGHTER_SHELL_MIDDLE02 = 658;
    //$private static final int MAP_FIGHTER_SHELL_MIDDLE03 = 665;
    //$private static final int MAP_FIGHTER_SHELL_NEAR01 = 672;
    //$private static final int MAP_FIGHTER_SHELL_NEAR02 = 679;
    //$private static final int MAP_FIGHTER_SHELL_NEAR03 = 686;
    //$private static final int MAP_FIGHTER_WIRE = 693;
    //$private static final int MAP_FRONT_FIGHTER_LONG01 = 700;
    //$private static final int MAP_FRONT_FIGHTER_LONG02 = 707;
    //$private static final int MAP_FRONT_FIGHTER_LONG03 = 714;
    //$private static final int MAP_FRONT_FIGHTER_LONG04 = 721;
    //$private static final int MAP_FRONT_FIGHTER_LONG05 = 728;
    //$private static final int MAP_FRONT_FIGHTER_LONG06 = 735;
    //$private static final int MAP_FRONT_FIGHTER_LONG07 = 742;
    //$private static final int MAP_FRONT_FIGHTER_LONG08 = 749;
    //$private static final int MAP_FRONT_FIGHTER_LONG09 = 756;
    //$private static final int MAP_FRONT_FIGHTER_MIDDLE01 = 763;
    //$private static final int MAP_FRONT_FIGHTER_MIDDLE02 = 770;
    //$private static final int MAP_FRONT_FIGHTER_MIDDLE03 = 777;
    //$private static final int MAP_FRONT_FIGHTER_MIDDLE04 = 784;
    //$private static final int MAP_FRONT_FIGHTER_MIDDLE05 = 791;
    //$private static final int MAP_FRONT_FIGHTER_MIDDLE06 = 798;
    //$private static final int MAP_FRONT_FIGHTER_MIDDLE07 = 805;
    //$private static final int MAP_FRONT_FIGHTER_MIDDLE08 = 812;
    //$private static final int MAP_FRONT_FIGHTER_MIDDLE09 = 819;
    //$private static final int MAP_FRONT_FIGHTER_NEAR01 = 826;
    //$private static final int MAP_FRONT_FIGHTER_NEAR02 = 833;
    //$private static final int MAP_FRONT_FIGHTER_NEAR03 = 840;
    //$private static final int MAP_FRONT_FIGHTER_NEAR04 = 847;
    //$private static final int MAP_FRONT_FIGHTER_NEAR05 = 854;
    //$private static final int MAP_FRONT_FIGHTER_NEAR06 = 861;
    //$private static final int MAP_FRONT_FIGHTER_NEAR07 = 868;
    //$private static final int MAP_FRONT_FIGHTER_NEAR08 = 875;
    //$private static final int MAP_FRONT_FIGHTER_NEAR09 = 882;
    //$private static final int MAP_ICON_CRUISER = 889;
    //$private static final int MAP_MENU_POINTER = 896;
    //$private static final int MAP_PARTS_MIDDLE01 = 903;
    //$private static final int MAP_PARTS_MIDDLE02 = 910;
    //$private static final int MAP_PARTS_MIDDLE03 = 917;
    //$private static final int MAP_PARTS_MIDDLE04 = 924;
    //$private static final int MAP_PARTS_MIDDLE05 = 931;
    //$private static final int MAP_PARTS_MIDDLE06 = 938;
    //$private static final int MAP_PARTS_MIDDLE07 = 945;
    //$private static final int MAP_PARTS_MIDDLE08 = 952;
    //$private static final int MAP_PARTS_MIDDLE09 = 959;
    //$private static final int MAP_PARTS_MIDDLE10 = 966;
    //$private static final int MAP_PARTS_NEAR01 = 973;
    //$private static final int MAP_PARTS_NEAR02 = 980;
    //$private static final int MAP_PARTS_NEAR03 = 987;
    //$private static final int MAP_PARTS_NEAR04 = 994;
    //$private static final int MAP_PARTS_NEAR05 = 1001;
    //$private static final int MAP_PARTS_NEAR06 = 1008;
    //$private static final int MAP_PARTS_NEAR07 = 1015;
    //$private static final int MAP_PARTS_NEAR08 = 1022;
    //$private static final int MAP_PARTS_NEAR09 = 1029;
    //$private static final int MAP_PARTS_NEAR10 = 1036;
    //$private static final int MAP_PLAYER_BARREL_BOTTOM = 1043;
    //$private static final int MAP_PLAYER_BARREL_LEFT = 1050;
    //$private static final int MAP_PLAYER_BARREL_RIGHT = 1057;
    //$private static final int MAP_PLAYER_SHELL_LONG = 1064;
    //$private static final int MAP_PLAYER_SHELL_MIDDLE01 = 1071;
    //$private static final int MAP_PLAYER_SHELL_MIDDLE02 = 1078;
    //$private static final int MAP_PLAYER_SHELL_MIDDLE03 = 1085;
    //$private static final int MAP_PLAYER_SHELL_NEAR01 = 1092;
    //$private static final int MAP_PLAYER_SHELL_NEAR02 = 1099;
    //$private static final int MAP_PLAYER_SHELL_NEAR03 = 1106;
    //$private static final int MAP_PLAYER_SIGN01 = 1113;
    //$private static final int MAP_PLAYER_SIGN02 = 1120;
    //$private static final int MAP_PLAYER_SIGN03 = 1127;
    //$private static final int MAP_PLAYER_SIGN04 = 1134;
    //$private static final int MAP_RAMKA_TARGET = 1141;
    //$private static final int MAP_TARGET_POINTER = 1148;

    //#_if (VENDOR=="NOKIA" && MODEL=="7650") || (VENDOR=="SIEMENS" && MODEL=="CX65") || (VENDOR=="MOTOROLA" && MODEL=="E398")
    //#global IMAGES_DYNAMIC=false
    //#global IMAGES_EXTERNAL=false
    //#global IMAGES_NORMAL=true
    //#global IMAGES_LINK=false
    //#global IMAGES_EXT2ONE=false
    //#_endif

    //#endif

    //#if VENDOR=="NOKIA"  && MODEL=="6100"
    //$private static final int MAP_BACK_FIGHTER_LONG01 = 0;
    //$private static final int MAP_BACK_FIGHTER_LONG02 = 9;
    //$private static final int MAP_BACK_FIGHTER_LONG04 = 18;
    //$private static final int MAP_BACK_FIGHTER_LONG05 = 27;
    //$private static final int MAP_BACK_FIGHTER_LONG07 = 36;
    //$private static final int MAP_BACK_FIGHTER_LONG08 = 45;
    //$private static final int MAP_BACK_FIGHTER_MIDDLE01 = 54;
    //$private static final int MAP_BACK_FIGHTER_MIDDLE02 = 63;
    //$private static final int MAP_BACK_FIGHTER_MIDDLE04 = 72;
    //$private static final int MAP_BACK_FIGHTER_MIDDLE05 = 81;
    //$private static final int MAP_BACK_FIGHTER_MIDDLE07 = 90;
    //$private static final int MAP_BACK_FIGHTER_MIDDLE08 = 99;
    //$private static final int MAP_BACK_FIGHTER_NEAR01 = 108;
    //$private static final int MAP_BACK_FIGHTER_NEAR02 = 117;
    //$private static final int MAP_BACK_FIGHTER_NEAR04 = 126;
    //$private static final int MAP_BACK_FIGHTER_NEAR05 = 135;
    //$private static final int MAP_BACK_FIGHTER_NEAR07 = 144;
    //$private static final int MAP_BACK_FIGHTER_NEAR08 = 153;
    //$private static final int MAP_CRUISER_DESTROYED_LEFT01 = 162;
    //$private static final int MAP_CRUISER_DESTROYED_LEFT02 = 171;
    //$private static final int MAP_CRUISER_DESTROYED_LEFT03 = 180;
    //$private static final int MAP_CRUISER_DESTROYED_LEFT04 = 189;
    //$private static final int MAP_CRUISER_DESTROYED_RIGHT01 = 198;
    //$private static final int MAP_CRUISER_DESTROYED_RIGHT02 = 207;
    //$private static final int MAP_CRUISER_DESTROYED_RIGHT03 = 216;
    //$private static final int MAP_CRUISER_DESTROYED_RIGHT04 = 225;
    //$private static final int MAP_CRUISER_FROM_HUPERJUMP_LEFT01 = 234;
    //$private static final int MAP_CRUISER_FROM_HUPERJUMP_LEFT02 = 243;
    //$private static final int MAP_CRUISER_FROM_HUPERJUMP_LEFT03 = 252;
    //$private static final int MAP_CRUISER_FROM_HUPERJUMP_LEFT04 = 261;
    //$private static final int MAP_CRUISER_FROM_HUPERJUMP_LEFT05 = 270;
    //$private static final int MAP_CRUISER_FROM_HUPERJUMP_RIGHT01 = 279;
    //$private static final int MAP_CRUISER_FROM_HUPERJUMP_RIGHT02 = 288;
    //$private static final int MAP_CRUISER_FROM_HUPERJUMP_RIGHT03 = 297;
    //$private static final int MAP_CRUISER_FROM_HUPERJUMP_RIGHT04 = 306;
    //$private static final int MAP_CRUISER_FROM_HUPERJUMP_RIGHT05 = 315;
    //$private static final int MAP_CRUISER_LEFT = 324;
    //$private static final int MAP_CRUISER_RIGHT = 333;
    //$private static final int MAP_CRUISER_SHELL_LONG = 342;
    //$private static final int MAP_CRUISER_SHELL_MIDDLE01 = 351;
    //$private static final int MAP_CRUISER_SHELL_MIDDLE02 = 360;
    //$private static final int MAP_CRUISER_SHELL_MIDDLE03 = 369;
    //$private static final int MAP_CRUISER_SHELL_NEAR01 = 378;
    //$private static final int MAP_CRUISER_SHELL_NEAR02 = 387;
    //$private static final int MAP_CRUISER_SHELL_NEAR03 = 396;
    //$private static final int MAP_CRUISER_TO_HUPERJUMP_LEFT01 = 405;
    //$private static final int MAP_CRUISER_TO_HUPERJUMP_LEFT02 = 414;
    //$private static final int MAP_CRUISER_TO_HUPERJUMP_LEFT03 = 423;
    //$private static final int MAP_CRUISER_TO_HUPERJUMP_LEFT04 = 432;
    //$private static final int MAP_CRUISER_TO_HUPERJUMP_LEFT05 = 441;
    //$private static final int MAP_CRUISER_TO_HUPERJUMP_LEFT06 = 450;
    //$private static final int MAP_CRUISER_TO_HUPERJUMP_RIGHT01 = 459;
    //$private static final int MAP_CRUISER_TO_HUPERJUMP_RIGHT02 = 468;
    //$private static final int MAP_CRUISER_TO_HUPERJUMP_RIGHT03 = 477;
    //$private static final int MAP_CRUISER_TO_HUPERJUMP_RIGHT04 = 486;
    //$private static final int MAP_CRUISER_TO_HUPERJUMP_RIGHT05 = 495;
    //$private static final int MAP_CRUISER_TO_HUPERJUMP_RIGHT06 = 504;
    //$private static final int MAP_CRUISER_WIRE = 513;
    //$private static final int MAP_EXPLOSION_LONG01 = 522;
    //$private static final int MAP_EXPLOSION_LONG02 = 531;
    //$private static final int MAP_EXPLOSION_LONG03 = 540;
    //$private static final int MAP_EXPLOSION_LONG04 = 549;
    //$private static final int MAP_EXPLOSION_LONG05 = 558;
    //$private static final int MAP_EXPLOSION_LONG06 = 567;
    //$private static final int MAP_EXPLOSION_LONG07 = 576;
    //$private static final int MAP_EXPLOSION_LONG08 = 585;
    //$private static final int MAP_EXPLOSION_MIDDLE01 = 594;
    //$private static final int MAP_EXPLOSION_MIDDLE02 = 603;
    //$private static final int MAP_EXPLOSION_MIDDLE03 = 612;
    //$private static final int MAP_EXPLOSION_MIDDLE04 = 621;
    //$private static final int MAP_EXPLOSION_MIDDLE05 = 630;
    //$private static final int MAP_EXPLOSION_MIDDLE06 = 639;
    //$private static final int MAP_EXPLOSION_MIDDLE07 = 648;
    //$private static final int MAP_EXPLOSION_MIDDLE08 = 657;
    //$private static final int MAP_EXPLOSION_NEAR01 = 666;
    //$private static final int MAP_EXPLOSION_NEAR02 = 675;
    //$private static final int MAP_EXPLOSION_NEAR03 = 684;
    //$private static final int MAP_EXPLOSION_NEAR04 = 693;
    //$private static final int MAP_EXPLOSION_NEAR05 = 702;
    //$private static final int MAP_EXPLOSION_NEAR06 = 711;
    //$private static final int MAP_EXPLOSION_NEAR07 = 720;
    //$private static final int MAP_EXPLOSION_NEAR08 = 729;
    //$private static final int MAP_EXPLOSION_NEAR09 = 738;
    //$private static final int MAP_FIGHTER_SHELL_LONG = 747;
    //$private static final int MAP_FIGHTER_SHELL_MIDDLE01 = 756;
    //$private static final int MAP_FIGHTER_SHELL_MIDDLE02 = 765;
    //$private static final int MAP_FIGHTER_SHELL_MIDDLE03 = 774;
    //$private static final int MAP_FIGHTER_SHELL_NEAR01 = 783;
    //$private static final int MAP_FIGHTER_SHELL_NEAR02 = 792;
    //$private static final int MAP_FIGHTER_SHELL_NEAR03 = 801;
    //$private static final int MAP_FIGHTER_WIRE = 810;
    //$private static final int MAP_FRONT_FIGHTER_LONG01 = 819;
    //$private static final int MAP_FRONT_FIGHTER_LONG02 = 828;
    //$private static final int MAP_FRONT_FIGHTER_LONG04 = 837;
    //$private static final int MAP_FRONT_FIGHTER_LONG05 = 846;
    //$private static final int MAP_FRONT_FIGHTER_LONG07 = 855;
    //$private static final int MAP_FRONT_FIGHTER_LONG08 = 864;
    //$private static final int MAP_FRONT_FIGHTER_MIDDLE01 = 873;
    //$private static final int MAP_FRONT_FIGHTER_MIDDLE02 = 882;
    //$private static final int MAP_FRONT_FIGHTER_MIDDLE04 = 891;
    //$private static final int MAP_FRONT_FIGHTER_MIDDLE05 = 900;
    //$private static final int MAP_FRONT_FIGHTER_MIDDLE07 = 909;
    //$private static final int MAP_FRONT_FIGHTER_MIDDLE08 = 918;
    //$private static final int MAP_FRONT_FIGHTER_NEAR01 = 927;
    //$private static final int MAP_FRONT_FIGHTER_NEAR02 = 936;
    //$private static final int MAP_FRONT_FIGHTER_NEAR04 = 945;
    //$private static final int MAP_FRONT_FIGHTER_NEAR05 = 954;
    //$private static final int MAP_FRONT_FIGHTER_NEAR07 = 963;
    //$private static final int MAP_FRONT_FIGHTER_NEAR08 = 972;
    //$private static final int MAP_ICON_CRUISER = 981;
    //$private static final int MAP_MENU_POINTER = 990;
    //$private static final int MAP_PARTS_MIDDLE01 = 999;
    //$private static final int MAP_PARTS_MIDDLE02 = 1008;
    //$private static final int MAP_PARTS_MIDDLE03 = 1017;
    //$private static final int MAP_PARTS_MIDDLE04 = 1026;
    //$private static final int MAP_PARTS_MIDDLE05 = 1035;
    //$private static final int MAP_PARTS_MIDDLE06 = 1044;
    //$private static final int MAP_PARTS_MIDDLE07 = 1053;
    //$private static final int MAP_PARTS_MIDDLE08 = 1062;
    //$private static final int MAP_PARTS_MIDDLE09 = 1071;
    //$private static final int MAP_PARTS_MIDDLE10 = 1080;
    //$private static final int MAP_PARTS_NEAR01 = 1089;
    //$private static final int MAP_PARTS_NEAR02 = 1098;
    //$private static final int MAP_PARTS_NEAR03 = 1107;
    //$private static final int MAP_PARTS_NEAR04 = 1116;
    //$private static final int MAP_PARTS_NEAR05 = 1125;
    //$private static final int MAP_PARTS_NEAR06 = 1134;
    //$private static final int MAP_PARTS_NEAR07 = 1143;
    //$private static final int MAP_PARTS_NEAR08 = 1152;
    //$private static final int MAP_PARTS_NEAR09 = 1161;
    //$private static final int MAP_PARTS_NEAR10 = 1170;
    //$private static final int MAP_PLAYER_BARREL_BOTTOM = 1179;
    //$private static final int MAP_PLAYER_BARREL_LEFT = 1188;
    //$private static final int MAP_PLAYER_BARREL_RIGHT = 1197;
    //$private static final int MAP_PLAYER_SHELL_LONG = 1206;
    //$private static final int MAP_PLAYER_SHELL_MIDDLE01 = 1215;
    //$private static final int MAP_PLAYER_SHELL_MIDDLE02 = 1224;
    //$private static final int MAP_PLAYER_SHELL_MIDDLE03 = 1233;
    //$private static final int MAP_PLAYER_SHELL_NEAR01 = 1242;
    //$private static final int MAP_PLAYER_SHELL_NEAR02 = 1251;
    //$private static final int MAP_PLAYER_SHELL_NEAR03 = 1260;
    //$private static final int MAP_PLAYER_SIGN_01A = 1269;
    //$private static final int MAP_PLAYER_SIGN_01B = 1278;
    //$private static final int MAP_PLAYER_SIGN_02A = 1287;
    //$private static final int MAP_PLAYER_SIGN_02B = 1296;
    //$private static final int MAP_PLAYER_SIGN_03A = 1305;
    //$private static final int MAP_PLAYER_SIGN_03B = 1314;
    //$private static final int MAP_PLAYER_SIGN_04A = 1323;
    //$private static final int MAP_PLAYER_SIGN_04B = 1332;
    //$private static final int MAP_TARGET_POINTER = 1341;

    //#_if VENDOR=="NOKIA" && MODEL=="6100"
    //#global IMAGES_DYNAMIC=true
    //#global IMAGES_EXTERNAL=false
    //#global IMAGES_NORMAL=true
    //#global IMAGES_LINK=true
    //#global IMAGES_EXT2ONE=false
    //#_endif
    //#endif

    //#if  (VENDOR=="LG" && MODEL=="G1600") ||(VENDOR=="MOTOROLA" && MODEL=="C380") || (VENDOR=="SAMSUNG" && MODEL=="X100") || (VENDOR=="SAMSUNG" && MODEL=="C100") || (VENDOR=="SIEMENS" && (MODEL=="M55" || MODEL=="S55" || MODEL=="M50" || MODEL=="C65")) || (VENDOR=="NOKIA" && (MODEL=="3410" || MODEL=="3510"))
    private static final int MAP_BACK_FIGHTER_LONG01 = 0;
    private static final int MAP_BACK_FIGHTER_LONG02 = 7;
    private static final int MAP_BACK_FIGHTER_LONG03 = 14;
    private static final int MAP_BACK_FIGHTER_LONG04 = 21;
    private static final int MAP_BACK_FIGHTER_LONG05 = 28;
    private static final int MAP_BACK_FIGHTER_LONG06 = 35;
    private static final int MAP_BACK_FIGHTER_LONG07 = 42;
    private static final int MAP_BACK_FIGHTER_LONG08 = 49;
    private static final int MAP_BACK_FIGHTER_LONG09 = 56;
    private static final int MAP_BACK_FIGHTER_MIDDLE01 = 63;
    private static final int MAP_BACK_FIGHTER_MIDDLE02 = 70;
    private static final int MAP_BACK_FIGHTER_MIDDLE03 = 77;
    private static final int MAP_BACK_FIGHTER_MIDDLE04 = 84;
    private static final int MAP_BACK_FIGHTER_MIDDLE05 = 91;
    private static final int MAP_BACK_FIGHTER_MIDDLE06 = 98;
    private static final int MAP_BACK_FIGHTER_MIDDLE07 = 105;
    private static final int MAP_BACK_FIGHTER_MIDDLE08 = 112;
    private static final int MAP_BACK_FIGHTER_MIDDLE09 = 119;
    private static final int MAP_BACK_FIGHTER_NEAR01 = 126;
    private static final int MAP_BACK_FIGHTER_NEAR02 = 133;
    private static final int MAP_BACK_FIGHTER_NEAR03 = 140;
    private static final int MAP_BACK_FIGHTER_NEAR04 = 147;
    private static final int MAP_BACK_FIGHTER_NEAR05 = 154;
    private static final int MAP_BACK_FIGHTER_NEAR06 = 161;
    private static final int MAP_BACK_FIGHTER_NEAR07 = 168;
    private static final int MAP_BACK_FIGHTER_NEAR08 = 175;
    private static final int MAP_BACK_FIGHTER_NEAR09 = 182;
    private static final int MAP_CRUISER_DESTROYED_LEFT01 = 189;
    private static final int MAP_CRUISER_DESTROYED_LEFT02 = 196;
    private static final int MAP_CRUISER_DESTROYED_LEFT03 = 203;
    private static final int MAP_CRUISER_DESTROYED_LEFT04 = 210;
    private static final int MAP_CRUISER_DESTROYED_RIGHT01 = 217;
    private static final int MAP_CRUISER_DESTROYED_RIGHT02 = 224;
    private static final int MAP_CRUISER_DESTROYED_RIGHT03 = 231;
    private static final int MAP_CRUISER_DESTROYED_RIGHT04 = 238;
    private static final int MAP_CRUISER_FROM_HUPERJUMP_LEFT01 = 245;
    private static final int MAP_CRUISER_FROM_HUPERJUMP_LEFT02 = 252;
    private static final int MAP_CRUISER_FROM_HUPERJUMP_LEFT03 = 259;
    private static final int MAP_CRUISER_FROM_HUPERJUMP_LEFT04 = 266;
    private static final int MAP_CRUISER_FROM_HUPERJUMP_LEFT05 = 273;
    private static final int MAP_CRUISER_FROM_HUPERJUMP_RIGHT01 = 280;
    private static final int MAP_CRUISER_FROM_HUPERJUMP_RIGHT02 = 287;
    private static final int MAP_CRUISER_FROM_HUPERJUMP_RIGHT03 = 294;
    private static final int MAP_CRUISER_FROM_HUPERJUMP_RIGHT04 = 301;
    private static final int MAP_CRUISER_FROM_HUPERJUMP_RIGHT05 = 308;
    private static final int MAP_CRUISER_LEFT = 315;
    private static final int MAP_CRUISER_RIGHT = 322;
    private static final int MAP_CRUISER_SHELL_LONG = 329;
    private static final int MAP_CRUISER_SHELL_MIDDLE01 = 336;
    private static final int MAP_CRUISER_SHELL_MIDDLE02 = 343;
    private static final int MAP_CRUISER_SHELL_MIDDLE03 = 350;
    private static final int MAP_CRUISER_SHELL_NEAR01 = 357;
    private static final int MAP_CRUISER_SHELL_NEAR02 = 364;
    private static final int MAP_CRUISER_SHELL_NEAR03 = 371;
    private static final int MAP_CRUISER_TO_HUPERJUMP_LEFT01 = 378;
    private static final int MAP_CRUISER_TO_HUPERJUMP_LEFT02 = 385;
    private static final int MAP_CRUISER_TO_HUPERJUMP_LEFT03 = 392;
    private static final int MAP_CRUISER_TO_HUPERJUMP_LEFT04 = 399;
    private static final int MAP_CRUISER_TO_HUPERJUMP_LEFT05 = 406;
    private static final int MAP_CRUISER_TO_HUPERJUMP_LEFT06 = 413;
    private static final int MAP_CRUISER_TO_HUPERJUMP_RIGHT01 = 420;
    private static final int MAP_CRUISER_TO_HUPERJUMP_RIGHT02 = 427;
    private static final int MAP_CRUISER_TO_HUPERJUMP_RIGHT03 = 434;
    private static final int MAP_CRUISER_TO_HUPERJUMP_RIGHT04 = 441;
    private static final int MAP_CRUISER_TO_HUPERJUMP_RIGHT05 = 448;
    private static final int MAP_CRUISER_TO_HUPERJUMP_RIGHT06 = 455;
    private static final int MAP_CRUISER_WIRE = 462;
    private static final int MAP_EXPLOSION_LONG01 = 469;
    private static final int MAP_EXPLOSION_LONG02 = 476;
    private static final int MAP_EXPLOSION_LONG03 = 483;
    private static final int MAP_EXPLOSION_LONG04 = 490;
    private static final int MAP_EXPLOSION_LONG05 = 497;
    private static final int MAP_EXPLOSION_LONG06 = 504;
    private static final int MAP_EXPLOSION_LONG07 = 511;
    private static final int MAP_EXPLOSION_LONG08 = 518;
    private static final int MAP_EXPLOSION_MIDDLE01 = 525;
    private static final int MAP_EXPLOSION_MIDDLE02 = 532;
    private static final int MAP_EXPLOSION_MIDDLE03 = 539;
    private static final int MAP_EXPLOSION_MIDDLE04 = 546;
    private static final int MAP_EXPLOSION_MIDDLE05 = 553;
    private static final int MAP_EXPLOSION_MIDDLE06 = 560;
    private static final int MAP_EXPLOSION_MIDDLE07 = 567;
    private static final int MAP_EXPLOSION_MIDDLE08 = 574;
    private static final int MAP_EXPLOSION_NEAR01 = 581;
    private static final int MAP_EXPLOSION_NEAR02 = 588;
    private static final int MAP_EXPLOSION_NEAR03 = 595;
    private static final int MAP_EXPLOSION_NEAR04 = 602;
    private static final int MAP_EXPLOSION_NEAR05 = 609;
    private static final int MAP_EXPLOSION_NEAR06 = 616;
    private static final int MAP_EXPLOSION_NEAR07 = 623;
    private static final int MAP_EXPLOSION_NEAR08 = 630;
    private static final int MAP_EXPLOSION_NEAR09 = 637;
    private static final int MAP_FIGHTER_SHELL_LONG = 644;
    private static final int MAP_FIGHTER_SHELL_MIDDLE01 = 651;
    private static final int MAP_FIGHTER_SHELL_MIDDLE02 = 658;
    private static final int MAP_FIGHTER_SHELL_MIDDLE03 = 665;
    private static final int MAP_FIGHTER_SHELL_NEAR01 = 672;
    private static final int MAP_FIGHTER_SHELL_NEAR02 = 679;
    private static final int MAP_FIGHTER_SHELL_NEAR03 = 686;
    private static final int MAP_FIGHTER_WIRE = 693;
    private static final int MAP_FRONT_FIGHTER_LONG01 = 700;
    private static final int MAP_FRONT_FIGHTER_LONG02 = 707;
    private static final int MAP_FRONT_FIGHTER_LONG03 = 714;
    private static final int MAP_FRONT_FIGHTER_LONG04 = 721;
    private static final int MAP_FRONT_FIGHTER_LONG05 = 728;
    private static final int MAP_FRONT_FIGHTER_LONG06 = 735;
    private static final int MAP_FRONT_FIGHTER_LONG07 = 742;
    private static final int MAP_FRONT_FIGHTER_LONG08 = 749;
    private static final int MAP_FRONT_FIGHTER_LONG09 = 756;
    private static final int MAP_FRONT_FIGHTER_MIDDLE01 = 763;
    private static final int MAP_FRONT_FIGHTER_MIDDLE02 = 770;
    private static final int MAP_FRONT_FIGHTER_MIDDLE03 = 777;
    private static final int MAP_FRONT_FIGHTER_MIDDLE04 = 784;
    private static final int MAP_FRONT_FIGHTER_MIDDLE05 = 791;
    private static final int MAP_FRONT_FIGHTER_MIDDLE06 = 798;
    private static final int MAP_FRONT_FIGHTER_MIDDLE07 = 805;
    private static final int MAP_FRONT_FIGHTER_MIDDLE08 = 812;
    private static final int MAP_FRONT_FIGHTER_MIDDLE09 = 819;
    private static final int MAP_FRONT_FIGHTER_NEAR01 = 826;
    private static final int MAP_FRONT_FIGHTER_NEAR02 = 833;
    private static final int MAP_FRONT_FIGHTER_NEAR03 = 840;
    private static final int MAP_FRONT_FIGHTER_NEAR04 = 847;
    private static final int MAP_FRONT_FIGHTER_NEAR05 = 854;
    private static final int MAP_FRONT_FIGHTER_NEAR06 = 861;
    private static final int MAP_FRONT_FIGHTER_NEAR07 = 868;
    private static final int MAP_FRONT_FIGHTER_NEAR08 = 875;
    private static final int MAP_FRONT_FIGHTER_NEAR09 = 882;
    private static final int MAP_ICON_CRUISER = 889;
    private static final int MAP_MENU_POINTER = 896;
    private static final int MAP_PARTS_MIDDLE01 = 903;
    private static final int MAP_PARTS_MIDDLE02 = 910;
    private static final int MAP_PARTS_MIDDLE03 = 917;
    private static final int MAP_PARTS_MIDDLE04 = 924;
    private static final int MAP_PARTS_MIDDLE05 = 931;
    private static final int MAP_PARTS_MIDDLE06 = 938;
    private static final int MAP_PARTS_MIDDLE07 = 945;
    private static final int MAP_PARTS_MIDDLE08 = 952;
    private static final int MAP_PARTS_MIDDLE09 = 959;
    private static final int MAP_PARTS_MIDDLE10 = 966;
    private static final int MAP_PARTS_NEAR01 = 973;
    private static final int MAP_PARTS_NEAR02 = 980;
    private static final int MAP_PARTS_NEAR03 = 987;
    private static final int MAP_PARTS_NEAR04 = 994;
    private static final int MAP_PARTS_NEAR05 = 1001;
    private static final int MAP_PARTS_NEAR06 = 1008;
    private static final int MAP_PARTS_NEAR07 = 1015;
    private static final int MAP_PARTS_NEAR08 = 1022;
    private static final int MAP_PARTS_NEAR09 = 1029;
    private static final int MAP_PARTS_NEAR10 = 1036;
    private static final int MAP_PLAYER_BARREL_BOTTOM = 1043;
    private static final int MAP_PLAYER_BARREL_LEFT = 1050;
    private static final int MAP_PLAYER_BARREL_RIGHT = 1057;
    private static final int MAP_PLAYER_SHELL_LONG = 1064;
    private static final int MAP_PLAYER_SHELL_MIDDLE01 = 1071;
    private static final int MAP_PLAYER_SHELL_MIDDLE02 = 1078;
    private static final int MAP_PLAYER_SHELL_MIDDLE03 = 1085;
    private static final int MAP_PLAYER_SHELL_NEAR01 = 1092;
    private static final int MAP_PLAYER_SHELL_NEAR02 = 1099;
    private static final int MAP_PLAYER_SHELL_NEAR03 = 1106;
    private static final int MAP_PLAYER_SIGN01 = 1113;
    private static final int MAP_PLAYER_SIGN02 = 1120;
    private static final int MAP_PLAYER_SIGN03 = 1127;
    private static final int MAP_PLAYER_SIGN04 = 1134;
    private static final int MAP_RAMKA_TARGET = 1141;
    private static final int MAP_TARGET_POINTER = 1148;

    //#_if  (VENDOR=="LG" && MODEL=="G1600") || (VENDOR=="MOTOROLA" && MODEL=="C380")  || (VENDOR=="SIEMENS" && (MODEL=="M55" || MODEL=="S55" || MODEL=="M50" || MODEL=="C65")) || (VENDOR=="NOKIA" && (MODEL=="3410" || MODEL=="3510"))

    //#global IMAGES_DYNAMIC=false
    //#global IMAGES_EXTERNAL=false
    //#global IMAGES_NORMAL=true
    //#global IMAGES_LINK=false
    //#global IMAGES_EXT2ONE=false

    //#_endif

    //#_if  (VENDOR=="SAMSUNG" && MODEL=="X100") || (VENDOR=="SAMSUNG" && MODEL=="C100")

    //#global IMAGES_DYNAMIC=false
    //#global IMAGES_EXTERNAL=false
    //#global IMAGES_NORMAL=true
    //#global IMAGES_LINK=false
    //#global IMAGES_EXT2ONE=false

    //#_endif

    //#endif


    //==================Игровые переменные и объекты========================
    //#if !TILE

    private static final int STARS_ON_SCREEN = 25;

    private static final int COLOR_STAR_FIELD0 = 0x9C7100;
    private static final int COLOR_STAR_FIELD1 = 0x8C0028;
    private static final int COLOR_STAR_FIELD2 = 0x004B72;

    private static final int[] ai_starsFieldX = new int[STARS_ON_SCREEN];
    private static final int[] ai_starsFieldY = new int[STARS_ON_SCREEN];
    private static final int[] ai_starsColor = new int[STARS_ON_SCREEN];

    static
    {
        int i_step = 5;

        int i_limW = GAMESCREEN_WIDTH / i_step;
        int i_limH = GAMESCREEN_HEIGHT / i_step;


        for (int li = 0; li < STARS_ON_SCREEN; li++)
        {
            int i_x = (Gamelet.getRandomInt((i_limW * 1000) - 1) / 1000) * i_step;
            int i_y = (Gamelet.getRandomInt((i_limH * 1000) - 1) / 1000) * i_step;
            int i_c = Gamelet.getRandomInt(29999) / 10000;

            //#if VENDOR=="NOKIA" && MODEL=="3410"
            //$ai_starsColor[li] = 0xFFFFFF;
            //#else
            switch (i_c)
            {
                case 0:
                    ai_starsColor[li] = COLOR_STAR_FIELD0;
                    break;
                case 1:
                    ai_starsColor[li] = COLOR_STAR_FIELD1;
                    break;
                case 2:
                    ai_starsColor[li] = COLOR_STAR_FIELD2;
                    break;
            }
            //#endif

            ai_starsFieldX[li] = i_x;
            ai_starsFieldY[li] = i_y;
        }
    }

    private static final void drawStarField(Graphics _g)
    {
        final int[] ai_x = ai_starsFieldX;
        final int[] ai_y = ai_starsFieldY;
        final int[] ai_c = ai_starsColor;

        int i_offsetX = ((Gamelet.i8_viewRectX + 0x7F) >> 8) % GAMESCREEN_WIDTH;

        for (int li = STARS_ON_SCREEN - 1; li >= 0; li--)
        {
            final int i_color = ai_c[li];
            _g.setColor(i_color);

            int i_x = ai_x[li];
            int i_y = ai_y[li] + GAMESCREEN_OFFSETY;

            i_x -= i_offsetX;
            if (i_x < 0) i_x += GAMESCREEN_WIDTH;

            i_x += GAMESCREEN_OFFSETX;

            switch (i_color)
            {
                case COLOR_STAR_FIELD0:
                case COLOR_STAR_FIELD2:
                    _g.fillRect(i_x, i_y, 2, 2);
                    break;
                default:
                    _g.drawLine(i_x, i_y, i_x, i_y);
            }
        }
    }
    //#endif

    private static int i_newCruiserAlertCounter;

    //#if TILE
    private static TileBckgnd p_backgroundTile = new TileBckgnd();
    //#endif

    //#if (VENDOR=="NOKIA" && MODEL=="7650") || (VENDOR=="SIEMENS" && MODEL=="CX65") || (VENDOR=="MOTOROLA" && MODEL=="E398")
    private static Image p_FakedPanel;
    //#endif

    private static final int NEWCRUISER_COUNTER_INIT = 40;

    static
    {
        ai_spriteDecodeTable[Gamelet.SPRITE_ENEMY_SHELL_LONG] = MAP_FIGHTER_SHELL_LONG;
        ai_spriteDecodeTable[Gamelet.SPRITE_ENEMY_SHELL_MIDDLE] = MAP_FIGHTER_SHELL_MIDDLE01;
        ai_spriteDecodeTable[Gamelet.SPRITE_ENEMY_SHELL_NEAR] = MAP_FIGHTER_SHELL_NEAR01;

        ai_spriteDecodeTable[Gamelet.SPRITE_PLAYER_SHELL_LONG] = MAP_PLAYER_SHELL_LONG;
        ai_spriteDecodeTable[Gamelet.SPRITE_PLAYER_SHELL_MIDDLE] = MAP_PLAYER_SHELL_MIDDLE01;
        ai_spriteDecodeTable[Gamelet.SPRITE_PLAYER_SHELL_NEAR] = MAP_PLAYER_SHELL_NEAR01;

        ai_spriteDecodeTable[Gamelet.SPRITE_ENEMY_BACK_FIGHTER_LONG] = MAP_BACK_FIGHTER_LONG01;
        ai_spriteDecodeTable[Gamelet.SPRITE_ENEMY_BACK_FIGHTER_NEAR] = MAP_BACK_FIGHTER_NEAR01;
        ai_spriteDecodeTable[Gamelet.SPRITE_ENEMY_BACK_FIGHTER_MIDDLE] = MAP_BACK_FIGHTER_MIDDLE01;

        ai_spriteDecodeTable[Gamelet.SPRITE_ENEMY_FRONT_FIGHTER_LONG] = MAP_FRONT_FIGHTER_LONG01;
        ai_spriteDecodeTable[Gamelet.SPRITE_ENEMY_FRONT_FIGHTER_NEAR] = MAP_FRONT_FIGHTER_NEAR01;
        ai_spriteDecodeTable[Gamelet.SPRITE_ENEMY_FRONT_FIGHTER_MIDDLE] = MAP_FRONT_FIGHTER_MIDDLE01;

        ai_spriteDecodeTable[Gamelet.SPRITE_EXPLOSION_LONG] = MAP_EXPLOSION_LONG01;
        ai_spriteDecodeTable[Gamelet.SPRITE_EXPLOSION_MIDDLE] = MAP_EXPLOSION_MIDDLE01;
        ai_spriteDecodeTable[Gamelet.SPRITE_EXPLOSION_NEAR] = MAP_EXPLOSION_NEAR01;

        ai_spriteDecodeTable[Gamelet.SPRITE_PARTS_MIDDLE] = MAP_PARTS_MIDDLE01;
        ai_spriteDecodeTable[Gamelet.SPRITE_PARTS_NEAR] = MAP_PARTS_NEAR01;

        ai_spriteDecodeTable[Gamelet.SPRITE_ENEMY_CRUISER_LEFT] = MAP_CRUISER_LEFT;
        ai_spriteDecodeTable[Gamelet.SPRITE_ENEMY_CRUISER_TO_HYPERJUMP_LEFT] = MAP_CRUISER_TO_HUPERJUMP_LEFT01;
        ai_spriteDecodeTable[Gamelet.SPRITE_ENEMY_CRUISER_FROM_HYPERJUMP_LEFT] = MAP_CRUISER_FROM_HUPERJUMP_LEFT01;
        ai_spriteDecodeTable[Gamelet.SPRITE_ENEMY_CRUISER_DESTROYED_LEFT] = MAP_CRUISER_DESTROYED_LEFT01;

        ai_spriteDecodeTable[Gamelet.SPRITE_ENEMY_CRUISER_RIGHT] = MAP_CRUISER_RIGHT;
        ai_spriteDecodeTable[Gamelet.SPRITE_ENEMY_CRUISER_TO_HYPERJUMP_RIGHT] = MAP_CRUISER_TO_HUPERJUMP_RIGHT01;
        ai_spriteDecodeTable[Gamelet.SPRITE_ENEMY_CRUISER_FROM_HYPERJUMP_RIGHT] = MAP_CRUISER_FROM_HUPERJUMP_RIGHT01;
        ai_spriteDecodeTable[Gamelet.SPRITE_ENEMY_CRUISER_DESTROYED_RIGHT] = MAP_CRUISER_DESTROYED_RIGHT01;
    }

}
