package mtv.pillow;

import javax.microedition.midlet.MIDlet;
import javax.microedition.midlet.MIDletStateChangeException;
import javax.microedition.lcdui.*;
import java.io.DataInputStream;
import java.io.IOException;

/**
 * Шаблон для изготовления игровых визуализированных модулей
 *
 * @author Igor A. Maznitsa
 *         (C) 2005 Raydac Research Group Ltd.
 * @version 1.9
 */
public class startup extends MIDlet implements Runnable, GameMenu.MenuListener, Gamelet.GameActionListener, CommandListener
{
    private static final Object SYNCHRO_OBJECT = new Object();

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
    //#else
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
    //#endif
    //======================================================

    //#if MODEL=="C380"
    //$private static final int SCREEN_WIDTH = 128;
    //$private static final int SCREEN_HEIGHT = 116;
    //#endif
    //#if MODEL=="E398"
    private static final int SCREEN_WIDTH = 176;
    private static final int SCREEN_HEIGHT = 204;
    //#endif

    private static final int COLOR_MAIN_BACKGROUND = 0x000000;

    private static final int COLOR_LOADING_BACKGROUND = 0xFFB533;
    private static final int COLOR_LOADING_BAR_BACKGROUND = 0x32B5FA;
    private static final int COLOR_LOADING_BAR = 0xFF338A;

    private static final int COLOR_BORDER = 0x000055;

    private static final int COLOR_RECORD_BACKGROUND = 0xFF9733;
    private static final int COLOR_RECORD_TEXT = 0xCC0033;
    private static final int COLOR_RECORD_CHAR = 0xFFF733;
    private static final int COLOR_RECORD_BCKGNDCHAR = 0x307BEF;

    private static final int LOADING_BAR_HEIGHT = 5;
    private static final int LOADING_BAR_WIDTH = 60;
    private static final int LOADING_BAR_OFFSET_FROM_LOGO = 5;
    private static final int I8_LOADING_BAR_PERCENT = (LOADING_BAR_WIDTH << 8) / 100;

    private static final String RESOURCE_LOADING_LOGO = "/loading.png";
    private static final String RESOURCE_SPLASH = "/splash.png";

    private static final String RESOURCE_LANGUAGES = "/langs.bin";
    private static final String RESOURCE_MENU = "/gmenu.bin";

    private static final int DELAY_STAGESCREEN = 100;
    private static final int DELAY_FINALSCREEN = 400;

    private static final int LETTER_RECORDNAME_FIRSTCODE = 0x40;
    private static final int LETTER_RECORDNAME_LASTCODE = 0x59;

    private static class InsideCanvas extends Canvas
    {
        private int i_screenOffsetX = 0;
        private int i_screenOffsetY = 0;
        private boolean lg_drawBorder;
        private int i_screenWidth;
        private int i_screenHeight;

        private static startup p_parent;

        public InsideCanvas()
        {
            super();

            //#if MIDP=="2.0"
            //$setFullScreenMode(true);
            //#endif

            i_screenHeight = getHeight();
            i_screenWidth = getWidth();

            i_screenWidth = i_screenWidth < SCREEN_WIDTH ? SCREEN_WIDTH : i_screenWidth;
            i_screenHeight = i_screenHeight < SCREEN_HEIGHT ? SCREEN_HEIGHT : i_screenHeight;

            i_screenOffsetX = (i_screenWidth - SCREEN_WIDTH) >> 1;
            i_screenOffsetY = (i_screenHeight - SCREEN_HEIGHT) >> 1;
            lg_drawBorder = (i_screenOffsetX | i_screenOffsetY) != 0;
        }

        private void paintRecordNamePanel(Graphics _graphics)
        {
            _graphics.setColor(COLOR_RECORD_BACKGROUND);
            _graphics.fillRect(0, 0, SCREEN_WIDTH, SCREEN_HEIGHT);
            Font p_chFont = Font.getFont(Font.FACE_PROPORTIONAL, Font.STYLE_BOLD, Font.SIZE_LARGE);

            final int i_point = Graphics.TOP | Graphics.LEFT;

            _graphics.setFont(p_chFont);
            String s_title = LangBlock.getStringForIndex(RecordNameTXT);
            int i_x = (SCREEN_WIDTH - p_chFont.stringWidth(s_title)) >> 1;
            int i_y = 2;
            _graphics.setColor(COLOR_RECORD_TEXT);
            _graphics.drawString(s_title, i_x, i_y, i_point);
            i_y += p_chFont.getHeight() + 3;


            _graphics.setFont(GameMenu.p_MenuFont);
            s_title = LangBlock.getStringForIndex(ScoreTXT) + ": " + Gamelet.getPlayerScore();
            i_x = (SCREEN_WIDTH - GameMenu.p_MenuFont.stringWidth(s_title)) >> 1;
            _graphics.drawString(s_title, i_x, i_y, i_point);
            i_y += GameMenu.p_MenuFont.getHeight() + 8;

            _graphics.setFont(p_chFont);

            final int CHAR_INTERVAL = 3;
            final int RECT_WIDTH = p_chFont.stringWidth("WW");
            final int RECT_HEIGHT = p_chFont.getHeight() + (CHAR_INTERVAL << 1);

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
                }
                else
                {
                    _graphics.setColor(i_bckgcolor);
                    _graphics.fillRect(i_x, i_y, RECT_WIDTH, RECT_HEIGHT);
                }

                _graphics.setColor(i_chrcolor);

                String s_str = "" + LangBlock.CHARSETS[ai_RecordNameChars[li] >>> 6].charAt(ai_RecordNameChars[li] & 0x3F);
                int i_w = p_chFont.stringWidth(s_str);
                _graphics.drawString(s_str, i_x + ((RECT_WIDTH - i_w) >> 1), i_chY, i_point);

                i_x += CHAR_INTERVAL + RECT_WIDTH;
            }

            _graphics.setFont(GameMenu.p_MenuFont);
            // Отрисовываем ВВОД ОТМЕНА в правом и левом нижнем углу
            final int OFFSET_HORZ = 1;
            final int OFFSET_VERT = 1;

            String s_strEnter = LangBlock.getStringForIndex(SaveTXT);
            String s_strCancel = LangBlock.getStringForIndex(CancelTXT);
            i_y = SCREEN_HEIGHT - GameMenu.p_MenuFont.getHeight() - OFFSET_VERT;

            if (i_lastPressedKey == KEY_CODE_SOFT_LEFT)
                _graphics.setColor(COLOR_RECORD_TEXT);
            else
                _graphics.setColor(~COLOR_RECORD_TEXT);
            _graphics.drawString(s_strCancel, OFFSET_HORZ, i_y, i_point);

            if (i_lastPressedKey == KEY_CODE_SOFT_RIGHT)
                _graphics.setColor(COLOR_RECORD_TEXT);
            else
                _graphics.setColor(~COLOR_RECORD_TEXT);
            _graphics.drawString(s_strEnter, SCREEN_WIDTH - OFFSET_HORZ - GameMenu.p_MenuFont.stringWidth(s_strEnter), i_y, i_point);
        }

        protected void paint(Graphics _graphics)
        {
            _graphics.translate(i_screenOffsetX, i_screenOffsetY);

            _graphics.setClip(0, 0, SCREEN_WIDTH, SCREEN_HEIGHT);
            switch (i_MidletMode)
            {
                case MODE_INITED:
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
                            int i_logoWidth = 50;
                            int i_logoHeight = 54;

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
                        _graphics.setColor(COLOR_LOADING_BAR_BACKGROUND);
                        _graphics.fillRect(i_splImgX, i_splImgY, LOADING_BAR_WIDTH, LOADING_BAR_HEIGHT);
                        _graphics.setColor(COLOR_LOADING_BAR);
                        _graphics.fillRect(i_splImgX, i_splImgY, (i_LoadingProgress * I8_LOADING_BAR_PERCENT) >> 8, LOADING_BAR_HEIGHT);
                    }
            ;
                    break;
                case MODE_MAINMENU:
                    {
                        _graphics.setColor(COLOR_MAIN_BACKGROUND);
                        _graphics.fillRect(0, 0, SCREEN_WIDTH, SCREEN_HEIGHT);

                        //#if MODEL=="C380"
                        //$if (p_Image_Splash != null)
                        //${
                        //$    _graphics.drawImage(p_Image_Splash, 7, 13, 0);
                        //$}
                        //#endif
                        //#if MODEL=="E398"
                        _graphics.drawImage(p_headerImage, 28, 50, 0);
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
                        paintGameProcess(_graphics);

                        _graphics.setClip(0, 0, SCREEN_WIDTH, SCREEN_HEIGHT);
                        GameMenu.paintMenu(_graphics, 0, 0, SCREEN_WIDTH, SCREEN_HEIGHT);
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
                        paintGameProcess(_graphics);
                    }
            ;
                    break;
                case MODE_SHOWSTAGE:
                    {
                        paintGameStage(_graphics);
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

            _graphics.translate(-i_screenOffsetX, -i_screenOffsetY);
            // Отрисовываем бордюры
            if (lg_drawBorder)
            {
                _graphics.setClip(0, 0, i_screenWidth, i_screenHeight);
                _graphics.setColor(COLOR_BORDER);
                // Верхний бордюр
                _graphics.fillRect(0, 0, i_screenWidth, i_screenOffsetY);
                // Нижний бордюр
                int i_hght = i_screenOffsetY + SCREEN_HEIGHT;
                _graphics.fillRect(0, i_hght, i_screenWidth, i_screenHeight - i_hght);
                // Левый бордюр
                _graphics.fillRect(0, i_screenOffsetY, i_screenOffsetX, SCREEN_HEIGHT);
                // Правый бордюр
                _graphics.fillRect(i_screenOffsetX + SCREEN_WIDTH, i_screenOffsetY, i_screenOffsetX, SCREEN_HEIGHT);
            }

            //#if SHOWSYS
            _graphics.setClip(0, 0, i_screenWidth, i_screenHeight);
            // Отрисовываем объем занятой и свободной памяти
            String s_str = "F/T:" + Runtime.getRuntime().freeMemory() + "/" + Runtime.getRuntime().totalMemory();
            _graphics.setColor(0);
            _graphics.drawString(s_str, 1, 1, Graphics.TOP | Graphics.LEFT);
            _graphics.setColor(0xFF0000);
            _graphics.drawString(s_str, 0, 0, Graphics.TOP | Graphics.LEFT);
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
                        p_parent.keyPressed(_keyCode);
                    }
            ;
                    break;
                case MODE_RECORDNAME:
                    {
                        p_InsideCanvas.repaint();
                    }
            ;
                    break;
                case MODE_GAMEFINAL:
                    {

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
                        repaint();
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
                            p_InsideCanvas.repaint();
                        }
                    }
            ;
                    break;
                case MODE_GAMEFINAL:
                    {
                        if (i_KeyReactionDelayCounter == 0 && i_lastPressedKey == _keyCode)
                        {
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
                        repaint();
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

    private static int i_MidletMode = -1;
    private static int i_PrevMidletMode;
    private static boolean lg_Working;

    private static int i_KeyReactionDelayCounter;

    private static int i_LoadingProgress;

    private static final InsideCanvas p_InsideCanvas = new InsideCanvas();
    private static Display p_Display;

    private static Image p_Image_LoadingLogo;
    private static Image p_Image_Splash;

    private static boolean lg_Option_Sound;
    private static boolean lg_Option_Vibra;
    private static boolean lg_Option_Light;
    private static int i_LanguageIndex;

    private static int i_KeyFlags;
    private static int i_selectedGameLevel;
    private static int i_selectedGameStage;
    private static int i_DelayTickCounter;

    private static byte[] ab_ScoreTable;
    private static final int[] ai_RecordNameChars = new int [3];

    private static int i_RecordCharPosition;

    private static String s_errorString;

    private final void initRecordName()
    {
        ai_RecordNameChars[0] = LETTER_RECORDNAME_FIRSTCODE;
        ai_RecordNameChars[1] = LETTER_RECORDNAME_FIRSTCODE;
        ai_RecordNameChars[2] = LETTER_RECORDNAME_FIRSTCODE;
        i_RecordCharPosition = 0;
    }

    private final void setMode(int _newMode)
    {
        if (i_MidletMode == _newMode) return;
        if (i_MidletMode == MODE_ERROR) return;

        i_lastPressedKey = -1;

        i_DelayTickCounter = 0;

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
                        p_Image_Splash = null;
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
                    {
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
                        lg_cycle = false;
                    }
            ;
                    break;
                case MODE_ERROR:
                    {
                        lg_cycle = false;
                    }
            ;
                    break;
                case MODE_GAMEFINAL:
                    {
                        onReleaseGame();
                        Gamelet.releaseGame();

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
                        if (i_PrevMidletMode == MODE_GAMEMENU)
                        {
                            onReleaseGame();
                            Gamelet.releaseGame();
                        }
                        p_Image_LoadingLogo = null;
                        i_selectedGameLevel = -1;
                        ab_ScoreTable = null;
                        //#if MODEL=="C380"
                        //$try
                        //${
                        //$    p_Image_Splash = Image.createImage(RESOURCE_SPLASH);
                        //$}
                        //$catch (Exception e)
                        //${
                        //$}
                        //#endif
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
            p_InsideCanvas.repaint();
        Runtime.getRuntime().gc();
    }

    /**
     * Сгенерировать форму таблицы игровых рекордов
     *
     * @param _scoreTable массив, содержащий таблицу
     * @return форму, содержащую список рекордов
     */
    private final List makeScoreTableForm(byte[] _scoreTable)
    {
        List p_form = new List(LangBlock.getStringForIndex(TopTXT), List.IMPLICIT);
        for (int li = 0; li < DataStorage.MAX_SCORE_RECORDS; li++)
        {
            String s_name = DataStorage.getNameInPosition(_scoreTable, li);
            if (s_name == null) break;
            int i_scores = DataStorage.getTopScoresInPosition(_scoreTable, li);
            String s_str = (li + 1) + ". " + s_name + "..." + Integer.toString(i_scores);
            p_form.append(s_str, null);
        }

        p_form.addCommand(new Command(LangBlock.getStringForIndex(BackTXT), Command.SCREEN, 1));
        p_form.setCommandListener(this);
        return p_form;
    }

    /**
     * Сгенерировать форму помощи по игре или информацию по игре
     *
     * @param _help флаг, показывающий что надо генерировать помощь по игре, если false то about
     * @return форму, содержащую текст помощи или информацию
     */
    private final Form makeHelpOrAboutBox(boolean _help)
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
        Form p_tbox = new Form(s_titl);
        StringItem p_str = new StringItem(null, s_str);
        p_tbox.append(p_str);
        p_tbox.addCommand(new Command(LangBlock.getStringForIndex(BackTXT), Command.SCREEN, 1));
        p_tbox.setCommandListener(this);
        return p_tbox;
    }

    public void commandAction(Command command, Displayable displayable)
    {
        p_Display.setCurrent(p_InsideCanvas);
    }

    protected void startApp() throws MIDletStateChangeException
    {
        if (i_MidletMode < 0)
        {
            if (!processStartApp(this))
            {
                throw new MIDletStateChangeException("processStartApp");
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

    private final void saveCurrentGame()
    {
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
    }

    private final void loadGame()
    {
        try
        {
            byte[] ab_array = DataStorage.loadDataBlock();
            Gamelet.loadGameStateFromByteArray(ab_array, this);
            i_selectedGameStage = Gamelet.i_GameStage;
            i_selectedGameLevel = Gamelet.i_GameLevel;
            if (!onInitNewGame(this, i_selectedGameLevel)) throw new Exception("E0");
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

    private void increaseLoadingProgress(int _deltaPercents)
    {
        i_LoadingProgress += _deltaPercents;
        p_InsideCanvas.repaint();
    }

    protected void destroyApp(boolean b) throws MIDletStateChangeException
    {
        switch (i_MidletMode)
        {
            case MODE_SHOWSTAGE:
            case MODE_GAMEPLAY:
            case MODE_GAMEMENU:
                {
                    onAppDestroyed();
                    synchronized (SYNCHRO_OBJECT)
                    {
                        saveCurrentGame();
                    }
                }
        ;
                break;
        }
        setMode(MODE_RELEASING);
        lg_Working = false;
    }

    public boolean onEnable(int _itemId, int _subitemId)
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
                        return true;
                    case SUBITEM_ID_Vibra:
                    case SUBITEM_ID_Light:
                        return false;
                }
            }
        }
        return true;
    }

    public void onEnter(int _itemId)
    {

    }

    public void onExit(int _itemId)
    {

    }

    public void onState(int _itemId, int _subitemId, boolean _newState)
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
                        synchronized (SYNCHRO_OBJECT)
                        {
                            saveCurrentGame();
                            setMode(MODE_MAINMENU);
                        }
                    }
            ;
                    break;
                case ITEM_ID_ResumeGame:
                    {
                        if (i_MidletMode == MODE_MAINMENU)
                        {
                            loadGame();
                            onAfterLoadGame(this);
                        }
                        Gamelet.i_GameState = Gamelet.STATE_PAUSED;
                        Gamelet.resumeGameAfterPauseOrPlayerLost();
                        setMode(MODE_GAMEPLAY);
                    }
            ;
                    break;
                case ITEM_ID_About:
                    {
                        Form p_abForm = makeHelpOrAboutBox(false);
                        p_Display.setCurrent(p_abForm);
                    }
            ;
                    break;
                case ITEM_ID_Help:
                    {
                        Form p_helpForm = makeHelpOrAboutBox(true);
                        p_Display.setCurrent(p_helpForm);
                    }
            ;
                    break;
                case ITEM_ID_Top:
                    {
                        try
                        {
                            byte[] ab_sTable = DataStorage.getTopScores();
                            List p_listScores = makeScoreTableForm(ab_sTable);
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
                            case SUBITEM_ID_Light:
                                {
                                    lg_Option_Light = _newState;
                                }
                        ;
                                break;
                            case SUBITEM_ID_Sound:
                                {
                                    lg_Option_Sound = _newState;

                                    if (lg_Option_Sound)
                                    {
                                        if (i_MidletMode == MODE_MAINMENU)
                                        {
                                            SoundManager.playSound(SoundManager.SOUND_THEME, 1);
                                        }
                                    }
                                    else
                                        SoundManager.stopAllSound();
                                }
                        ;
                                break;
                            case SUBITEM_ID_Vibra:
                                {
                                    lg_Option_Vibra = _newState;
                                }
                        ;
                                break;
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
                                LangBlock.setLanguage(this.getClass(), i_newLanguage);
                            }
                            catch (Exception e)
                            {
                                i_newLanguage = i_currentLanguage;
                                try
                                {
                                    LangBlock.setLanguage(this.getClass(), i_newLanguage);
                                }
                                catch (Exception e1)
                                {
                                    lg_Working = false;
                                }
                            }
                            i_LanguageIndex = i_newLanguage;
                            packAndSaveOptions();

                            GameMenu.focusToItem(ITEM_ID_Language);
                            p_InsideCanvas.repaint();
                        }
                    }
            ;
                    break;
            }
        }
    }

    private static final void packAndSaveOptions()
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

    public boolean isSelected(int _itemId, int _subitemId)
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

    public int onCustom(int _itemId, int _subitemIndex)
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

                    int i_result = (i_flags << OFFSET_FLAGS) | (_subitemIndex << OFFSET_ID) | (i_selected << OFFSET_SELECTED) | (i_textid << OFFSET_TEXT);
                    return i_result;
                }
        }
        return 0;
    }

    public void run()
    {
        setMode(MODE_LOADING);

        boolean lg_firstStart = false;

        try
        {
            switch(DataStorage.init(Gamelet.getID()))
            {
                    case DataStorage.STORE_FIRSTSTART :
            {
                i_LanguageIndex = -1;
                lg_Option_Light = true;
                lg_Option_Sound = true;
                lg_Option_Vibra = true;
                lg_firstStart = true;
            };break;
                    case DataStorage.STORE_INITED :
            {
                i_LanguageIndex = DataStorage.ab_OptionsArray[0] & 0xFF;
                int i_options = DataStorage.ab_OptionsArray[1];
                lg_Option_Light = (i_options & 0x1) != 0;
                lg_Option_Sound = (i_options & 0x2) != 0;
                lg_Option_Vibra = (i_options & 0x4) != 0;
            };break;
                case DataStorage.STORE_NOMEMORY:
                    {
                        Alert p_alert = new Alert("RMS error","No memory for data\r\n"+DataStorage.s_Status,null,AlertType.ERROR);
                        p_alert.setTimeout(3000);
                        p_Display.setCurrent(p_alert,p_InsideCanvas);
                        try
                        {
                            Thread.sleep(4000);
                        }
                        catch (InterruptedException e)
                        {
                        }
                        p_alert = null;
                    };break;
            }
            Runtime.getRuntime().gc();

            increaseLoadingProgress(25);

            i_LanguageIndex = LangBlock.initLanguageBlock(this.getClass(), null, 1, 1, 1, RESOURCE_LANGUAGES, i_LanguageIndex);
            GameMenu.initMenuBlock(this.getClass(), this, RESOURCE_MENU, Font.getFont(Font.FACE_PROPORTIONAL, Font.STYLE_PLAIN, Font.SIZE_MEDIUM));
            Runtime.getRuntime().gc();

            if (lg_firstStart)
            {
                packAndSaveOptions();
            }
            Runtime.getRuntime().gc();

            increaseLoadingProgress(25);

            //#if DEBUG
            System.out.println("Load game resources");
            //#endif
            loadGameResources(this);
            Runtime.getRuntime().gc();

            increaseLoadingProgress(25);
        }
        catch (Exception e)
        {
            s_errorString = e.getMessage();
            setMode(MODE_ERROR);
            try
            {
                Thread.sleep(3000);
            }
            catch (InterruptedException e1)
            {
            }

            notifyDestroyed();
            return;
        }

        try
        {
            Thread.sleep(500);
        }
        catch (Exception e)
        {
        }

        Gamelet.init(this.getClass());

        increaseLoadingProgress(25);
        try
        {
            Thread.sleep(250);
        }
        catch (Exception e)
        {
        }

        setMode(MODE_MAINMENU);

        while (lg_Working)
        {
            int i_timedelay = 100;
            switch (i_MidletMode)
            {
                case MODE_MAINMENU:
                    {
                        if (i_selectedGameLevel >= 0)
                        {
                            if (!Gamelet.initNewGame(GAMESCREEN_WIDTH, GAMESCREEN_HEIGHT, i_selectedGameLevel, this) || !onInitNewGame(this, i_selectedGameLevel))
                                {
                                    s_errorString = "Can't init game";
                                    setMode(MODE_ERROR);
                                    continue;
                                }
                            setMode(MODE_SHOWSTAGE);
                            i_KeyFlags = 0;
                        }
                        else
                        {
                            i_timedelay = 50;
                            if (GameMenu.processMenu()) p_InsideCanvas.repaint();
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
                        i_timedelay = 50;
                        if (GameMenu.processMenu()) p_InsideCanvas.repaint();
                    }
            ;
                    break;
                case MODE_SHOWSTAGE:
                    {
                        if (i_DelayTickCounter >= DELAY_STAGESCREEN)
                        {
                            setMode(MODE_GAMEPLAY);
                        }
                    }
            ;
                    break;
                case MODE_GAMEFINAL:
                    {
                        if (i_KeyReactionDelayCounter > 0) i_KeyReactionDelayCounter--;
                        if (i_DelayTickCounter >= DELAY_FINALSCREEN)
                        {
                            setMode(MODE_RECORDNAME);
                        }
                    }
            ;
                    break;
                case MODE_GAMEPLAY:
                    {
                        synchronized (SYNCHRO_OBJECT)
                        {
                            int i_gameDelay = Gamelet.i_GameStepDelay;
                            long l_startTime = System.currentTimeMillis();
                            int i_gameletState = Gamelet.nextGameStep(i_KeyFlags);

                            i_KeyFlags &= ~(Gamelet.KEY_FIRE|Gamelet.KEY_UP|Gamelet.KEY_DOWN);

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
                                                    s_errorString = "Can't init stage";
                                                    setMode(MODE_ERROR);
                                                }
                                            }
                                            else
                                                //#if MODEL=="C380"
                                                //$setMode(MODE_GAMEFINAL);
                                                //#else
                                                onReleaseGame();
                                                Gamelet.releaseGame();
                                                setMode(MODE_RECORDNAME);
                                                //#endif
                                        }
                                ;
                                        break;
                                }
                            }
                            else
                            {
                                p_InsideCanvas.repaint();

                                // Блок вычисления оставшейся задержки времени
                                long l_endTime = System.currentTimeMillis();
                                l_endTime -= l_startTime;
                                if (l_endTime > i_gameDelay || l_endTime < 0)
                                    i_timedelay = 10;
                                else if (l_endTime <= i_gameDelay) i_timedelay = i_gameDelay - (int) l_endTime;
                            }
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

            i_DelayTickCounter++;
        }
        this.notifyDestroyed();
    }

    private static final int STAGENUMBER_FIRST = 0;
    private static final int STAGENUMBER_LAST = 0;

    //================================Обработка игровых событий=========================
    public int processGameAction(int _arg)
    {
        if (lg_Option_Sound)
        {
        switch(_arg)
        {
            case Gamelet.GAMEACTION_CAR : SoundManager.playSound(SoundManager.SOUND_CAR,1);break;
            case Gamelet.GAMEACTION_DOOR : SoundManager.playSound(SoundManager.SOUND_DOOR,1);break;
            case Gamelet.GAMEACTION_HIT : SoundManager.playSound(SoundManager.SOUND_HIT,1);break;
            case Gamelet.GAMEACTION_MIMO : SoundManager.playSound(SoundManager.SOUND_MIMO,1);break;
            case Gamelet.GAMEACTION_LIFT : SoundManager.playSound(SoundManager.SOUND_LIFT,1);break;
            case Gamelet.GAMEACTION_TAKEHEART : SoundManager.playSound(SoundManager.SOUND_TAKEHEART,1);break;
            case Gamelet.GAMEACTION_DEADROOM : SoundManager.playSound(SoundManager.SOUND_HIT,8);break;
        }
        }
        return 0;
    }

    //================================Переменные и массивы==============================
    private static Image p_gameWinImage;
    private static byte[] ab_buildingTileMap;
    private static byte[] ab_roomTileMap;
    private static Image p_buildingTileImage;
    private static Image p_roomTileImage;
    //#if MODEL=="E398"
    private static Image p_headerImage;
    //#endif

    private static TileBackground p_TileBackground;
    private static Image [] ap_FullImages;
    private static Image p_LincolnImage;
    private static Image p_HammerImage;
    private static Image p_LiftImage;

    private static final int MAP_ARROW = 0;
    private static final int MAP_AVARIA3_0001 = 9;
    private static final int MAP_AVARIA3_0002 = 18;
    private static final int MAP_AVARIA3_0003 = 27;
    private static final int MAP_AVARIA3_0004 = 36;
    private static final int MAP_AVARIA3_0005 = 45;
    private static final int MAP_AVARIA3_0006_HAND = 54;
    private static final int MAP_AVARIA3_0006 = 63;
    private static final int MAP_BRITTNEY_0001 = 72;
    private static final int MAP_BRITTNEY_0002 = 81;
    private static final int MAP_BRITTNEY_0003 = 90;
    private static final int MAP_BRITTNEY_0004 = 99;
    private static final int MAP_BSBBODY_0001 = 108;
    private static final int MAP_BSBBODY_0002 = 117;
    private static final int MAP_BSBBODY_0003 = 126;
    private static final int MAP_BSBBODY_0004 = 135;
    private static final int MAP_BSBHEAD_0001 = 144;
    private static final int MAP_BSBHEAD_0002 = 153;
    private static final int MAP_BSBHEAD_0003 = 162;
    private static final int MAP_BSBHEAD_0004 = 171;
    private static final int MAP_BSBHEAD_0005 = 180;
    private static final int MAP_DIGIT0 = 189;
    private static final int MAP_DIGIT1 = 198;
    private static final int MAP_DIGIT2 = 207;
    private static final int MAP_DIGIT3 = 216;
    private static final int MAP_DIGIT4 = 225;
    private static final int MAP_DIGIT5 = 234;
    private static final int MAP_DIGIT6 = 243;
    private static final int MAP_DIGIT7 = 252;
    private static final int MAP_DIGIT8 = 261;
    private static final int MAP_DIGIT9 = 270;
    private static final int MAP_ENRIKE_0001 = 279;
    private static final int MAP_ENRIKE_0002 = 288;
    private static final int MAP_ENRIKE_0003 = 297;
    private static final int MAP_ENRIKE_0004 = 306;
    private static final int MAP_FRAME = 315;
    private static final int MAP_HEARTBIG = 324;
    private static final int MAP_HEARTLITTLE = 333;
    private static final int MAP_HERO_0001 = 342;
    private static final int MAP_HERO_0002 = 351;
    private static final int MAP_HERO_0003 = 360;
    private static final int MAP_HERO_0004 = 369;
    private static final int MAP_HERO_0005_HAND = 378;
    private static final int MAP_HERO_0005 = 387;
    private static final int MAP_HERO_0006_HAND = 396;
    private static final int MAP_HERO_0006 = 405;
    private static final int MAP_HERO_0007_HAND = 414;
    private static final int MAP_HERO_0007 = 423;
    private static final int MAP_ICON_LIFE = 432;
    private static final int MAP_ICON_PILLOW = 441;
    private static final int MAP_ICON_SUPERPILLOW = 450;
    private static final int MAP_LOGO1 = 459;
    private static final int MAP_LOGO2 = 468;
    private static final int MAP_PEOPLE1_0001 = 477;
    private static final int MAP_PEOPLE1_0002 = 486;
    private static final int MAP_PEOPLE1_0003 = 495;
    private static final int MAP_PEOPLE1_0004 = 504;
    private static final int MAP_PEOPLE2_0001 = 513;
    private static final int MAP_PEOPLE2_0002 = 522;
    private static final int MAP_PEOPLE2_0003 = 531;
    private static final int MAP_PEOPLE2_0004 = 540;
    private static final int MAP_PEOPLE4_0001 = 549;
    private static final int MAP_PEOPLE4_0002 = 558;
    private static final int MAP_PEOPLE4_0003 = 567;
    private static final int MAP_PEOPLE4_0004 = 576;
    private static final int MAP_PEOPLE5_0001 = 585;
    private static final int MAP_PEOPLE5_0002 = 594;
    private static final int MAP_PEOPLE5_0003 = 603;
    private static final int MAP_PEOPLE5_0004 = 612;
    private static final int MAP_SCORES10 = 621;
    private static final int MAP_SCORES20 = 630;
    private static final int MAP_SCORES50 = 639;
    private static final int MAP_SCORES5 = 648;
    private static final int MAP_SECURITY_0001 = 657;
    private static final int MAP_SECURITY_0002_HAND = 666;
    private static final int MAP_SECURITY_0002 = 675;
    private static final int MAP_SECURITY_0003 = 684;
    private static final int MAP_SECURITY_0004_HAND = 693;
    private static final int MAP_SECURITY_0004 = 702;
    private static final int MAP_SECURITY_0005_HAND = 711;
    private static final int MAP_SECURITY_0005 = 720;
    private static final int MAP_SECURITY_0006 = 729;
    private static final int MAP_SECURITY_0007_HAND = 738;
    private static final int MAP_SECURITY_0007 = 747;
    private static final int MAP_SMOKE01 = 756;
    private static final int MAP_SMOKE02 = 765;
    private static final int MAP_SMOKE03 = 774;
    private static final int MAP_SMOKE04 = 783;
    private static final int MAP_STARBIG = 792;
    private static final int MAP_STARLITTLE = 801;
    private static final int MAP_TATOOBODY_0001 = 810;
    private static final int MAP_TATOOBODY_0002 = 819;
    private static final int MAP_TATOOBODY_0003 = 828;
    private static final int MAP_TATOOBODY_0004 = 837;
    private static final int MAP_TATOOHEAD_0001 = 846;
    private static final int MAP_TATOOHEAD_0002 = 855;
    //================================Игровые функции===================================
    private static final byte[] loadTitleMapArray(startup _this, String _resource) throws Exception
    {
        DataInputStream p_instr = new DataInputStream(_this.getClass().getResourceAsStream(_resource));
        int i_length = p_instr.readUnsignedShort();

        byte[] ab_Array = new byte[i_length];

        if (p_instr.readUnsignedByte()==1)
        {
            // Пакованные данные
            int i_index = 0;
            while(i_index<i_length)
            {
                int i_len = p_instr.readUnsignedByte();
                byte i_val = p_instr.readByte();
                int li=0;

                while(li<=i_len)
                {
                    ab_Array[i_index] = i_val;
                    i_index ++;
                    li++;
                }
            }
        }
        else
        {
            p_instr.read(ab_Array);
        }

        return ab_Array;
    }

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

    private static void onAfterLoadGame(startup _this)
    {
        p_TileBackground.setXY(Gamelet.i8_ViewAreaX, Gamelet.i8_ViewAreaY);
    }

    private static final void loadGameResources(startup _this) throws Exception
    {
        //TODO
        //#if DEBUG
        System.out.println("Initing of sound block");
        //#endif
        //#if MODEL=="E398"
        ab_buildingTileMap = loadTitleMapArray(_this, "/btilimap.bin");
        ab_roomTileMap = loadTitleMapArray(_this, "/rtilimap.bin");
        SoundManager.initBlock(_this.getClass(), 80);
        //#endif
        //#if DEBUG
        System.out.println("Initing of sound block...ok");
        //#endif
        p_TileBackground.initTileBackground(GAMESCREEN_WIDTH, GAMESCREEN_HEIGHT, Gamelet.CELL_WIDTH, Gamelet.CELL_HEIGHT, false);

        //        p_TileBackground.setBlockImage(p_tileImage);
        //        p_TileBackground.setGameRoomArray(Gamelet.CELLSNUMBER_WIDTH, ab_TileMap);
    }

    private static final void onAppDestroyed()
    {
        p_gameWinImage = null;
        p_Image_Splash = null;
        Runtime.getRuntime().gc();
    }

    private static final void onMidletModeChanged(int _newMode, int _oldMode)
    {
        switch (_newMode)
        {
            case MODE_RECORDNAME:
                {
                    p_gameWinImage = null;
                    Runtime.getRuntime().gc();
                    SoundManager.stopAllSound();
                }
        ;
                break;
            case MODE_MAINMENU:
                {
                    p_gameWinImage = null;
                    Runtime.getRuntime().gc();

                    SoundManager.stopAllSound();
                    if (lg_Option_Sound)
                    {
                        SoundManager.playSound(SoundManager.SOUND_THEME, 1);
                    }
                }
        ;
                break;
            case MODE_GAMEFINAL:
                {
                    Runtime.getRuntime().gc();

                    try
                    {
                        if (Gamelet.i_PlayerState == Gamelet.PLAYER_WIN)
                        {
                            p_gameWinImage = Image.createImage("/final.png");
                        }
                    }
                    catch (IOException e)
                    {
                    }
                    Runtime.getRuntime().gc();

                    if (lg_Option_Sound)
                    {
                        if (Gamelet.i_PlayerState == Gamelet.PLAYER_WIN)
                        {
                            SoundManager.playSound(SoundManager.SOUND_FINAL, 1);
                        }
                    }
                }
        ;
                break;
            case MODE_GAMEPLAY:
            case MODE_GAMEMENU:
                {
                    SoundManager.stopAllSound();
                }
        ;
                break;
        }
    }

    private static final boolean onInitNewGameStage(int _gameStage)
    {
        return true;
    }

    private static final boolean onInitNewGame(startup _this, int _selectedGameLevel)
    {
        try
        {
            Runtime.getRuntime().gc();
            ImageManager.init(_this.getClass(), ap_FullImages);
        }
        catch (Exception e)
        {
            return false;
        }
        return true;
    }

    private static final void onReleaseGame()
    {
        ImageManager.release();
    }

    private static final boolean processStartApp(startup _this)
    {
        Runtime.getRuntime().gc();
        try
        {
            Image p_img0 = Image.createImage("/fimg0.png");
            Runtime.getRuntime().gc();
            Image p_img1 = Image.createImage("/fimg1.png");
            Runtime.getRuntime().gc();
            Image p_img2 = Image.createImage("/fimg2.png");
            Runtime.getRuntime().gc();
            p_buildingTileImage = Image.createImage("/btiling.png");
            Runtime.getRuntime().gc();
            p_roomTileImage = Image.createImage("/rtiling.png");
            Runtime.getRuntime().gc();
            ap_FullImages = new Image[]{p_img0,p_img1,p_img2};

            p_LiftImage = Image.createImage("/lift.png");
            Runtime.getRuntime().gc();
            p_HammerImage = Image.createImage("/hammer.png");
            Runtime.getRuntime().gc();
            p_LincolnImage = Image.createImage("/limo.png");
            Runtime.getRuntime().gc();
            //#if MODEL=="E398"
            p_headerImage = Image.createImage("/header.png");
            Runtime.getRuntime().gc();
            //#else
            //$ab_buildingTileMap = loadTitleMapArray(_this, "/btilimap.bin");
            //$SoundManager.initBlock(_this.getClass(), 80);
            //$ab_roomTileMap = loadTitleMapArray(_this, "/rtilimap.bin");
            //#endif
        }
        catch (Exception _ex)
        {
            return false;
        }

        p_TileBackground = new TileBackground();
        return true;
    }

    private static final void paintVisibleEnemies(Graphics _g)
    {
        Sprite[] ap_spr = Gamelet.ap_Sprites;

        int i8_scrX = Gamelet.i8_ViewAreaX;
        int i8_scrY = Gamelet.i8_ViewAreaY;

        for (int li = 0; li < Gamelet.MAX_SPRITES; li++)
        {
            Sprite p_spr = ap_spr[li];
            if (!p_spr.lg_SpriteActive || p_spr.lg_SpriteInvisible) continue;

            if (Gamelet.isSpriteVisible(p_spr))
            {
                // Спрайт виден
                int i_sprType = p_spr.i_ObjectType;

                int i_x = ((p_spr.i_ScreenX - i8_scrX) >> 8)+GAMESCREEN_OFFSETX;
                int i_y = ((p_spr.i_ScreenY - i8_scrY) >> 8)+GAMESCREEN_OFFSETY;

                int i_mx = ((p_spr.i_mainX - i8_scrX) >> 8)+GAMESCREEN_OFFSETX;

                int i_transform = (p_spr.i_ObjectType & 1) == 0 ? javax.microedition.lcdui.game.Sprite.TRANS_MIRROR : javax.microedition.lcdui.game.Sprite.TRANS_NONE;
                int i_frame = p_spr.i_Frame;

                final int TYPE_NOSTAR = 0;
                final int TYPE_SMALLSTAR = 1;
                final int TYPE_BIGSTAR = 2;

                int i_starType = TYPE_NOSTAR;

                if (i_sprType < Gamelet.FIRSTINDEX_GUARDIAN)
                {
                    // Иконки и сердечко
                    switch (i_sprType)
                    {
                        case Gamelet.SPRITE_CLOUD:
                            {
                                i_frame = MAP_SMOKE01 + i_frame * ImageManager.IMAGEINFO_LENGTH;
                                ImageManager.drawImage(i_frame, _g, i_x, i_y);
                            }
                    ;
                            break;
                        case Gamelet.SPRITE_BONUS_HEART:
                            {
                                i_frame = MAP_HEARTBIG;
                                ImageManager.drawImage(i_frame, _g, i_x, i_y);
                            }
                    ;
                            break;
                    }
                }
                else if (i_sprType < Gamelet.FIRSTINDEX_SMALLSTARS)
                {
                    // Телохранитель и охранник
                    switch (i_sprType)
                    {
                        case Gamelet.SPRITE_BODYGUARD_LEFT_BEAT:
                        case Gamelet.SPRITE_GUARDIAN_LEFT_BEAT:
                        case Gamelet.SPRITE_BODYGUARD_RIGHT_BEAT:
                        case Gamelet.SPRITE_GUARDIAN_RIGHT_BEAT:
                            {
                                if (i_sprType  == Gamelet.SPRITE_BODYGUARD_LEFT_BEAT || i_sprType  == Gamelet.SPRITE_BODYGUARD_RIGHT_BEAT) i_starType = TYPE_SMALLSTAR;
                                switch (i_frame)
                                {
                                    case 0:
                                        {
                                            i_frame = MAP_SECURITY_0005;
                                            ImageManager.drawImage(i_frame, _g, i_x, i_y, i_transform);
                                            i_frame = MAP_SECURITY_0005_HAND;
                                            ImageManager.drawImage(i_frame, _g, i_x, i_y, i_transform);
                                        }
                                ;
                                        break;
                                    case 1:
                                        {
                                            i_frame = MAP_SECURITY_0006;
                                            ImageManager.drawImage(i_frame, _g, i_x, i_y, i_transform);
                                        }
                                ;
                                        break;
                                    case 2:
                                        {
                                            i_frame = MAP_SECURITY_0007;
                                            ImageManager.drawImage(i_frame, _g, i_x, i_y, i_transform);
                                            i_frame = MAP_SECURITY_0007_HAND;
                                            ImageManager.drawImage(i_frame, _g, i_x, i_y, i_transform);
                                        }
                                ;
                                        break;
                                }
                            }
                    ;
                            break;
                        case Gamelet.SPRITE_BODYGUARD_LEFT_MOVE:
                        case Gamelet.SPRITE_GUARDIAN_LEFT_MOVE:
                        case Gamelet.SPRITE_BODYGUARD_RIGHT_MOVE:
                        case Gamelet.SPRITE_GUARDIAN_RIGHT_MOVE:
                            {
                                if (i_sprType  == Gamelet.SPRITE_BODYGUARD_LEFT_MOVE || i_sprType  == Gamelet.SPRITE_BODYGUARD_RIGHT_MOVE) i_starType = TYPE_SMALLSTAR;
                                switch (i_frame)
                                {
                                    case 0:
                                        {
                                            i_frame = MAP_SECURITY_0001;
                                            ImageManager.drawImage(i_frame, _g, i_x, i_y, i_transform);
                                        }
                                ;
                                        break;
                                    case 1:
                                        {
                                            i_frame = MAP_SECURITY_0002;
                                            ImageManager.drawImage(i_frame, _g, i_x, i_y, i_transform);
                                            i_frame = MAP_SECURITY_0002_HAND;
                                            ImageManager.drawImage(i_frame, _g, i_x, i_y, i_transform);
                                        }
                                ;
                                        break;
                                    case 2:
                                        {
                                            i_frame = MAP_SECURITY_0003;
                                            ImageManager.drawImage(i_frame, _g, i_x, i_y, i_transform);
                                        }
                                ;
                                        break;
                                    case 3:
                                        {
                                            i_frame = MAP_SECURITY_0004;
                                            ImageManager.drawImage(i_frame, _g, i_x, i_y, i_transform);
                                            i_frame = MAP_SECURITY_0004_HAND;
                                            ImageManager.drawImage(i_frame, _g, i_x, i_y, i_transform);
                                        }
                                ;
                                        break;
                                }
                            }
                    ;
                            break;
                    }
                }
                else if (i_sprType < Gamelet.FIRSTINDEX_BIGSTARS)
                {
                    // Виджеи
                    switch (i_sprType)
                    {
                        case Gamelet.SPRITE_SMALLSTAR_BOY_BLACK_JACKET_MOVERIGHT:
                        case Gamelet.SPRITE_SMALLSTAR_BOY_BLACK_JACKET_MOVELEFT:
                            {
                                i_frame = MAP_PEOPLE4_0001 + i_frame * ImageManager.IMAGEINFO_LENGTH;
                            }
                    ;
                            break;
                        case Gamelet.SPRITE_SMALLSTAR_BOY_WHITEHAIR_SUIT_MOVERIGHT:
                        case Gamelet.SPRITE_SMALLSTAR_BOY_WHITEHAIR_SUIT_MOVELEFT:
                            {
                                i_frame = MAP_PEOPLE5_0001 + i_frame * ImageManager.IMAGEINFO_LENGTH;
                            }
                    ;
                            break;
                        case Gamelet.SPRITE_SMALLSTAR_GIRL_CATSUIT_MOVERIGHT:
                        case Gamelet.SPRITE_SMALLSTAR_GIRL_CATSUIT_MOVELEFT:
                            {
                                i_frame = MAP_PEOPLE1_0001 + i_frame * ImageManager.IMAGEINFO_LENGTH;
                            }
                    ;
                            break;
                        case Gamelet.SPRITE_SMALLSTAR_BOY_WHITEHAIR_VEST_MOVERIGHT:
                        case Gamelet.SPRITE_SMALLSTAR_BOY_WHITEHAIR_VEST_MOVELEFT:
                            {
                                i_frame = MAP_PEOPLE2_0001 + i_frame * ImageManager.IMAGEINFO_LENGTH;
                            }
                    ;
                            break;
                    //#if DEBUG
                    default:
                            {
                                System.out.println("Error sprite "+i_sprType);
                            }
                    //#endif
                    }

                    i_starType = TYPE_SMALLSTAR;

                    ImageManager.drawImage(i_frame, _g, i_x, i_y, i_transform);
                }
                else if (i_sprType < Gamelet.FIRSTINDEX_ANGRYSTARS)
                {
                    // Звезды
                    switch(i_sprType)
                    {
                            case Gamelet.SPRITE_BIGSTAR_BOY_GRAY_SHIRT_MOVELEFT :
                            case Gamelet.SPRITE_BIGSTAR_BOY_GRAY_SHIRT_MOVERIGHT :
                            {
                                i_frame = MAP_ENRIKE_0001+i_frame*ImageManager.IMAGEINFO_LENGTH;
                                ImageManager.drawImage(i_frame, _g, i_x, i_y,i_transform);
                            };break;
                            case Gamelet.SPRITE_BIGSTAR_BOY_GROUP_MOVELEFT :
                            case Gamelet.SPRITE_BIGSTAR_BOY_GROUP_MOVERIGHT :
                            {
                                int i_headFrameOffset = 0;
                                if ((i_frame & 1)!=0 ) i_headFrameOffset = -3;

                                i_frame = MAP_BSBBODY_0001+i_frame*ImageManager.IMAGEINFO_LENGTH;
                                ImageManager.drawImage(i_frame, _g, i_x, i_y,i_transform);

                                i_frame = MAP_BSBHEAD_0001+p_spr.i_ObjectHitSteps*ImageManager.IMAGEINFO_LENGTH;
                                ImageManager.drawImage(i_frame, _g, i_x, i_y+i_headFrameOffset,i_transform);
                            };break;
                            case Gamelet.SPRITE_BIGSTAR_GIRL_RED_BIKINI_MOVELEFT :
                            case Gamelet.SPRITE_BIGSTAR_GIRL_RED_BIKINI_MOVERIGHT :
                            {
                                i_frame = MAP_BRITTNEY_0001+i_frame*ImageManager.IMAGEINFO_LENGTH;
                                ImageManager.drawImage(i_frame, _g, i_x, i_y,i_transform);
                            };break;
                            case Gamelet.SPRITE_BIGSTAR_GIRL_TATU_MOVELEFT :
                            case Gamelet.SPRITE_BIGSTAR_GIRL_TATU_MOVERIGHT :
                            {
                                int i_headFrameOffset = 0;
                                if ((i_frame & 1)!=0 ) i_headFrameOffset = -1;

                                i_frame = MAP_TATOOBODY_0001+i_frame*ImageManager.IMAGEINFO_LENGTH;
                                ImageManager.drawImage(i_frame, _g, i_x, i_y,i_transform);

                                i_frame = MAP_TATOOHEAD_0001+p_spr.i_ObjectHitSteps*ImageManager.IMAGEINFO_LENGTH;
                                ImageManager.drawImage(i_frame, _g, i_x, i_y+i_headFrameOffset,i_transform);
                            };break;
                    }

                    i_starType = TYPE_BIGSTAR;
                }
                else
                {
                    // Боевик
                    switch(i_sprType)
                    {
                            case Gamelet.SPRITE_ANGRYSTAR_LEFT_BEAT :
                            case Gamelet.SPRITE_ANGRYSTAR_RIGHT_BEAT :
                            {
                                i_transform = (i_sprType & 1)!=0 ? javax.microedition.lcdui.game.Sprite.TRANS_NONE : javax.microedition.lcdui.game.Sprite.TRANS_MIRROR;
                                switch (i_frame)
                                {
                                    case 0:
                                        {
                                            i_frame = MAP_AVARIA3_0004;
                                            ImageManager.drawImage(i_frame, _g, i_x, i_y, i_transform);
                                        }
                                ;
                                        break;
                                    case 1:
                                        {
                                            i_frame = MAP_AVARIA3_0005;
                                            ImageManager.drawImage(i_frame, _g, i_x, i_y, i_transform);
                                        }
                                ;
                                        break;
                                    case 2:
                                        {
                                            i_frame = MAP_AVARIA3_0006;
                                            ImageManager.drawImage(i_frame, _g, i_x, i_y, i_transform);
                                            i_frame = MAP_AVARIA3_0006_HAND;
                                            ImageManager.drawImage(i_frame, _g, i_x, i_y, i_transform);
                                        }
                                ;
                                        break;
                                }
                            };break;
                            case Gamelet.SPRITE_ANGRYSTAR_LEFT_MOVE :
                            case Gamelet.SPRITE_ANGRYSTAR_RIGHT_MOVE :
                            {
                                i_transform = (i_sprType & 1)!=0 ? javax.microedition.lcdui.game.Sprite.TRANS_NONE : javax.microedition.lcdui.game.Sprite.TRANS_MIRROR;
                                i_frame = MAP_AVARIA3_0001+i_frame*ImageManager.IMAGEINFO_LENGTH;
                                ImageManager.drawImage(i_frame, _g, i_x, i_y,i_transform);
                            };break;
                    }
                    i_starType = TYPE_BIGSTAR;
                }

                switch(i_starType)
                {
                    case TYPE_NOSTAR:
                        {

                        };break;
                    case TYPE_SMALLSTAR:
                        {
                            i_frame = MAP_STARLITTLE;
                            ImageManager.drawImage(i_frame, _g, i_mx-2, i_y-12);
                        };break;
                    case TYPE_BIGSTAR:
                        {
                            i_frame = MAP_STARBIG;
                            ImageManager.drawImage(i_frame, _g, i_mx-3, i_y-12);
                        };break;
                }

            }
        }
    }

    //#if MODEL=="C380"
    //$private static final int GAMESCREEN_OFFSETX =0;
    //$private static final int GAMESCREEN_OFFSETY = 0;
    //$private static final int GAMESCREEN_HEIGHT = SCREEN_HEIGHT;
    //$private static final int GAMESCREEN_WIDTH = SCREEN_WIDTH;
    //#endif
    //#if MODEL=="E398"
    private static final int GAMESCREEN_OFFSETX =12;
    private static final int GAMESCREEN_OFFSETY =61;
    private static final int GAMESCREEN_WIDTH = 150;
    private static final int GAMESCREEN_HEIGHT = 116;
    //#endif

    private static final void paintGameProcess(Graphics _graphics)
    {
        _graphics.setClip(0, 0, SCREEN_WIDTH, SCREEN_HEIGHT);

        switch (Gamelet.i_CurrentPlayMode)
        {
            case Gamelet.INSIDE_MODE_ANGRYSTARINCOMING :
                {
                    p_TileBackground.setGameRoomArray(Gamelet.CELLSNUMBER_ROOM_WIDTH, ab_roomTileMap);
                    p_TileBackground.setBlockImage(p_roomTileImage);
                    p_TileBackground.setXY(0,(Gamelet.ROOMTYPE_ENTRANCE*Gamelet.CELLSNUMBER_ROOM_HEIGHT*Gamelet.CELL_HEIGHT)<<8);
                    p_TileBackground.directPaint(_graphics, GAMESCREEN_OFFSETX, GAMESCREEN_OFFSETY);

                    // Лимузин
                    _graphics.setClip(0, 0, SCREEN_WIDTH, SCREEN_HEIGHT);
                    _graphics.drawImage(p_HammerImage,0,GAMESCREEN_OFFSETY+GAMESCREEN_HEIGHT-1,Graphics.BOTTOM|Graphics.LEFT);

                    // Отрисовываем входящих звезд
                    for(int li=0;li<Gamelet.MAX_SPRITES;li++)
                    {
                        Sprite p_spr = Gamelet.ap_Sprites[li];
                        if (!p_spr.lg_SpriteActive || p_spr.i_ObjectState>=0) continue;

                        int i_x = (p_spr.i_ScreenX >> 8)+GAMESCREEN_OFFSETX;
                        int i_y = (p_spr.i_ScreenY >> 8)+GAMESCREEN_OFFSETY;

                        int i_frame = p_spr.i_Frame;

                        switch(p_spr.i_ObjectType)
                        {
                                case Gamelet.SPRITE_BODYGUARD_RIGHT_MOVE :
                                {
                                    switch (i_frame)
                                    {
                                        case 0:
                                            {
                                                i_frame = MAP_SECURITY_0001;
                                                ImageManager.drawImage(i_frame, _graphics, i_x, i_y,javax.microedition.lcdui.game.Sprite.TRANS_MIRROR);
                                            }
                                    ;
                                            break;
                                        case 1:
                                            {
                                                i_frame = MAP_SECURITY_0002;
                                                ImageManager.drawImage(i_frame, _graphics, i_x, i_y,javax.microedition.lcdui.game.Sprite.TRANS_MIRROR);
                                                i_frame = MAP_SECURITY_0002_HAND;
                                                ImageManager.drawImage(i_frame, _graphics, i_x, i_y,javax.microedition.lcdui.game.Sprite.TRANS_MIRROR);
                                            }
                                    ;
                                            break;
                                        case 2:
                                            {
                                                i_frame = MAP_SECURITY_0003;
                                                ImageManager.drawImage(i_frame, _graphics, i_x, i_y,javax.microedition.lcdui.game.Sprite.TRANS_MIRROR);
                                            }
                                    ;
                                            break;
                                        case 3:
                                            {
                                                i_frame = MAP_SECURITY_0004;
                                                ImageManager.drawImage(i_frame, _graphics, i_x, i_y,javax.microedition.lcdui.game.Sprite.TRANS_MIRROR);
                                                i_frame = MAP_SECURITY_0004_HAND;
                                                ImageManager.drawImage(i_frame, _graphics, i_x, i_y,javax.microedition.lcdui.game.Sprite.TRANS_MIRROR);
                                            }
                                    ;
                                            break;
                                    }
                                };break;
                                case Gamelet.SPRITE_ANGRYSTAR_RIGHT_MOVE:
                                {
                                    i_frame = MAP_AVARIA3_0001+i_frame*ImageManager.IMAGEINFO_LENGTH;
                                    ImageManager.drawImage(i_frame, _graphics, i_x, i_y,javax.microedition.lcdui.game.Sprite.TRANS_MIRROR);
                                };break;
                        }
                    }
                };break;
            case Gamelet.INSIDE_MODE_STARINCOMING :
                {
                    p_TileBackground.setGameRoomArray(Gamelet.CELLSNUMBER_ROOM_WIDTH, ab_roomTileMap);
                    p_TileBackground.setBlockImage(p_roomTileImage);
                    p_TileBackground.setXY(0,(Gamelet.ROOMTYPE_ENTRANCE*Gamelet.CELLSNUMBER_ROOM_HEIGHT*Gamelet.CELL_HEIGHT)<<8);
                    p_TileBackground.directPaint(_graphics, GAMESCREEN_OFFSETX, GAMESCREEN_OFFSETY);

                    // Лимузин
                    _graphics.setClip(0, 0, SCREEN_WIDTH, SCREEN_HEIGHT);
                    _graphics.drawImage(p_LincolnImage,GAMESCREEN_WIDTH+GAMESCREEN_OFFSETX,GAMESCREEN_OFFSETY+GAMESCREEN_HEIGHT-1,Graphics.BOTTOM|Graphics.RIGHT);

                    // Отрисовываем входящих звезд
                    for(int li=0;li<Gamelet.MAX_SPRITES;li++)
                    {
                        Sprite p_spr = Gamelet.ap_Sprites[li];
                        if (!p_spr.lg_SpriteActive || p_spr.i_ObjectState>=0) continue;

                        int i_x = (p_spr.i_ScreenX >> 8)+GAMESCREEN_OFFSETX;
                        int i_y = (p_spr.i_ScreenY >> 8)+GAMESCREEN_OFFSETY;

                        int i_frame = p_spr.i_Frame;

                        switch(p_spr.i_ObjectType)
                        {
                                case Gamelet.SPRITE_BODYGUARD_LEFT_MOVE :
                                {
                                    switch (i_frame)
                                    {
                                        case 0:
                                            {
                                                i_frame = MAP_SECURITY_0001;
                                                ImageManager.drawImage(i_frame, _graphics, i_x, i_y);
                                            }
                                    ;
                                            break;
                                        case 1:
                                            {
                                                i_frame = MAP_SECURITY_0002;
                                                ImageManager.drawImage(i_frame, _graphics, i_x, i_y);
                                                i_frame = MAP_SECURITY_0002_HAND;
                                                ImageManager.drawImage(i_frame, _graphics, i_x, i_y);
                                            }
                                    ;
                                            break;
                                        case 2:
                                            {
                                                i_frame = MAP_SECURITY_0003;
                                                ImageManager.drawImage(i_frame, _graphics, i_x, i_y);
                                            }
                                    ;
                                            break;
                                        case 3:
                                            {
                                                i_frame = MAP_SECURITY_0004;
                                                ImageManager.drawImage(i_frame, _graphics, i_x, i_y);
                                                i_frame = MAP_SECURITY_0004_HAND;
                                                ImageManager.drawImage(i_frame, _graphics, i_x, i_y);
                                            }
                                    ;
                                            break;
                                    }
                                };break;
                                case Gamelet.SPRITE_BIGSTAR_BOY_GRAY_SHIRT_MOVELEFT:
                                {
                                    i_frame = MAP_ENRIKE_0001+i_frame*ImageManager.IMAGEINFO_LENGTH;
                                    ImageManager.drawImage(i_frame, _graphics, i_x, i_y);

                                };break;
                                case Gamelet.SPRITE_BIGSTAR_BOY_GROUP_MOVELEFT:
                                {
                                    int i_headOffset = 0;
                                    if ((i_frame & 1)!=0) i_headOffset = -3;

                                    i_frame = MAP_BSBBODY_0001+i_frame*ImageManager.IMAGEINFO_LENGTH;
                                    ImageManager.drawImage(i_frame, _graphics, i_x, i_y);

                                    i_frame = MAP_BSBHEAD_0001+p_spr.i_ObjectHitSteps*ImageManager.IMAGEINFO_LENGTH;
                                    ImageManager.drawImage(i_frame, _graphics, i_x, i_y+i_headOffset);
                                };break;
                                case Gamelet.SPRITE_BIGSTAR_GIRL_RED_BIKINI_MOVELEFT:
                                {
                                    i_frame = MAP_BRITTNEY_0001+i_frame*ImageManager.IMAGEINFO_LENGTH;
                                    ImageManager.drawImage(i_frame, _graphics, i_x, i_y);
                                };break;
                                case Gamelet.SPRITE_BIGSTAR_GIRL_TATU_MOVELEFT:
                                {
                                    int i_headOffset = 0;
                                    if ((i_frame & 1)!=0) i_headOffset = -1;

                                    i_frame = MAP_TATOOBODY_0001+i_frame*ImageManager.IMAGEINFO_LENGTH;
                                    ImageManager.drawImage(i_frame, _graphics, i_x, i_y);

                                    i_frame = MAP_TATOOHEAD_0001+p_spr.i_ObjectHitSteps*ImageManager.IMAGEINFO_LENGTH;
                                    ImageManager.drawImage(i_frame, _graphics, i_x, i_y+i_headOffset);
                                };break;
                        }
                    }
                };break;
            case Gamelet.INSIDE_MODE_DEADROOM :
                {
                    int i8_scrX = Gamelet.i8_ViewAreaX;
                    int i8_scrY = Gamelet.i8_ViewAreaY;

                    p_TileBackground.setGameRoomArray(Gamelet.CELLSNUMBER_WIDTH, ab_buildingTileMap);
                    p_TileBackground.setBlockImage(p_buildingTileImage);
                    p_TileBackground.setXY(i8_scrX, i8_scrY);
                    p_TileBackground.directPaint(_graphics, GAMESCREEN_OFFSETX, GAMESCREEN_OFFSETY);

                    // Рисуем звезд
                    paintVisibleEnemies(_graphics);
                };break;
            case Gamelet.INSIDE_MODE_ROOM :
                {
                    if (Gamelet.i_RoomShowCounter>0)
                    {
                        // Отрисовываем комнату
                        int i8_verYOffset = ((Gamelet.i_ActiveRoomType * Gamelet.ROOM_HEIGHT_CELLS)*Gamelet.CELL_HEIGHT)<<8;
                        p_TileBackground.setGameRoomArray(Gamelet.CELLSNUMBER_ROOM_WIDTH, ab_roomTileMap);
                        p_TileBackground.setBlockImage(p_roomTileImage);
                        p_TileBackground.setXY(0,i8_verYOffset);
                        //#if MODEL=="C380"
                        //$p_TileBackground.directPaint(_graphics, GAMESCREEN_OFFSETX-Gamelet.CELL_WIDTH, GAMESCREEN_OFFSETY);
                        //#else
                        p_TileBackground.directPaint(_graphics, GAMESCREEN_OFFSETX+1, GAMESCREEN_OFFSETY-2);
                        //#endif

                        //Рисуем игрока
                        int i_frame = MAP_HERO_0002;
                        //#if MODEL=="C380"
                        //$int i_x = -50+GAMESCREEN_OFFSETX;
                        //#else
                        int i_x = -40+GAMESCREEN_OFFSETX;
                        //#endif
                        int i_y = GAMESCREEN_HEIGHT - 88+GAMESCREEN_OFFSETY;
                        ImageManager.drawImage(i_frame,_graphics,i_x,i_y,javax.microedition.lcdui.game.Sprite.TRANS_MIRROR);
                    }
                    else
                    {
                        int i8_scrX = Gamelet.i8_ViewAreaX;
                        int i8_scrY = Gamelet.i8_ViewAreaY;

                        // РИсуем задний фон
                        p_TileBackground.setGameRoomArray(Gamelet.CELLSNUMBER_WIDTH, ab_buildingTileMap);
                        p_TileBackground.setBlockImage(p_buildingTileImage);
                        p_TileBackground.setXY(i8_scrX, i8_scrY);
                        p_TileBackground.directPaint(_graphics, GAMESCREEN_OFFSETX, GAMESCREEN_OFFSETY);

                        // Рисуем звезд
                        paintVisibleEnemies(_graphics);

                        // Рисуем стрелку
                        final int ARROW_WIDTH = 13;
                        final int HEIGHT_OVER_PLAYERY = 55;

                        int i_x = ((Gamelet.p_PlayerSprite.i_mainX-i8_scrX)>>8)-(ARROW_WIDTH>>>1)+GAMESCREEN_OFFSETX;
                        int i_y = ((Gamelet.p_PlayerSprite.i_mainY-i8_scrY)>>8)-(HEIGHT_OVER_PLAYERY)+GAMESCREEN_OFFSETY;

                        int i_tick = i_DelayTickCounter % 12;
                        if (i_tick < 6)
                        {
                            i_y -= i_tick;
                        }
                        else
                        {
                            i_y = i_y-6+(i_tick-6);
                        }

                        ImageManager.drawImage(MAP_ARROW,_graphics,i_x,i_y);
                    }
                };break;
            default :
                {
                    int i8_scrX = Gamelet.i8_ViewAreaX;
                    int i8_scrY = Gamelet.i8_ViewAreaY;

                    // РИсуем задний фон
                    p_TileBackground.setGameRoomArray(Gamelet.CELLSNUMBER_WIDTH, ab_buildingTileMap);
                    p_TileBackground.setBlockImage(p_buildingTileImage);
                    p_TileBackground.setXY(i8_scrX, i8_scrY);
                    p_TileBackground.directPaint(_graphics, GAMESCREEN_OFFSETX, GAMESCREEN_OFFSETY);

                    // Рисуем звезд
                    paintVisibleEnemies(_graphics);

                    // Рисуем игрока если виден
                    if (!Gamelet.p_PlayerSprite.lg_SpriteInvisible)
                    {
                        Sprite p_spr = Gamelet.p_PlayerSprite;
                        int i_x = ((p_spr.i_ScreenX - i8_scrX) >> 8)+GAMESCREEN_OFFSETX;
                        int i_y = ((p_spr.i_ScreenY - i8_scrY) >> 8)+GAMESCREEN_OFFSETY;

                        int i_transform = (p_spr.i_ObjectType & 1) != 0 ? javax.microedition.lcdui.game.Sprite.TRANS_MIRROR : javax.microedition.lcdui.game.Sprite.TRANS_NONE;

                        int i_frame = p_spr.i_Frame;

                        switch (p_spr.i_ObjectType)
                        {
                            case Gamelet.SPRITE_PLAYER_LEFT_BEAT:
                            case Gamelet.SPRITE_PLAYER_RIGHT_BEAT:
                                {
                                    switch (i_frame)
                                    {
                                        case 0:
                                            {
                                                int i_startFrame = MAP_HERO_0004;
                                                ImageManager.drawImage(i_startFrame, _graphics, i_x, i_y, i_transform);
                                            }
                                    ;
                                            break;
                                        case 1:
                                            {
                                                int i_startFrame = MAP_HERO_0005;
                                                ImageManager.drawImage(i_startFrame, _graphics, i_x, i_y, i_transform);
                                                i_startFrame = MAP_HERO_0005_HAND;
                                                ImageManager.drawImage(i_startFrame, _graphics, i_x, i_y, i_transform);
                                            }
                                    ;
                                            break;
                                        case 2:
                                            {
                                                int i_startFrame = MAP_HERO_0006;
                                                ImageManager.drawImage(i_startFrame, _graphics, i_x, i_y, i_transform);
                                                i_startFrame = MAP_HERO_0006_HAND;
                                                ImageManager.drawImage(i_startFrame, _graphics, i_x, i_y, i_transform);
                                            }
                                    ;
                                            break;
                                        case 3:
                                            {
                                                int i_startFrame = MAP_HERO_0007;
                                                ImageManager.drawImage(i_startFrame, _graphics, i_x, i_y, i_transform);
                                                i_startFrame = MAP_HERO_0007_HAND;
                                                ImageManager.drawImage(i_startFrame, _graphics, i_x, i_y, i_transform);
                                            }
                                    ;
                                            break;
                                    }
                                }
                        ;
                                break;
                            case Gamelet.SPRITE_PLAYER_RIGHT_STAND:
                            case Gamelet.SPRITE_PLAYER_LEFT_STAND:
                                {
                                    int i_startFrame = MAP_HERO_0002;
                                    ImageManager.drawImage(i_startFrame, _graphics, i_x, i_y, i_transform);
                                };break;
                            case Gamelet.SPRITE_PLAYER_RIGHT_MOVE:
                            case Gamelet.SPRITE_PLAYER_LEFT_MOVE:
                                {
                                    int i_startFrame = MAP_HERO_0001;
                                    ImageManager.drawImage(i_startFrame + i_frame * ImageManager.IMAGEINFO_LENGTH, _graphics, i_x, i_y, i_transform);
                                }
                        ;
                                break;
                        }
                    }

                    // Рисуем лифт и металлодетектор если видны
                    if (!Gamelet.p_LiftSprite.lg_SpriteInvisible)
                    {
                        // Рамка
                        int i_x = (48 * Gamelet.CELL_WIDTH) - (i8_scrX >> 8)+GAMESCREEN_OFFSETX;
                        int i_y = (16 * Gamelet.CELL_HEIGHT) - (i8_scrY >> 8)+GAMESCREEN_OFFSETY;
                        ImageManager.drawImage(MAP_FRAME,_graphics,i_x,i_y);


                        // Лифт, с отсечением
                        int i_cx = (56 * Gamelet.CELL_WIDTH) - (i8_scrX >> 8)+GAMESCREEN_OFFSETX;
                        int i_cy = (16 * Gamelet.CELL_HEIGHT) - (i8_scrY >> 8)+GAMESCREEN_OFFSETY;
                        int i_cw = 4 * Gamelet.CELL_WIDTH;
                        int i_ch = 8 * Gamelet.CELL_HEIGHT;

                        _graphics.setClip(i_cx, i_cy, i_cw, i_ch);

                        Sprite p_spr = Gamelet.p_LiftSprite;
                        i_x = ((p_spr.i_ScreenX - i8_scrX) >> 8)+GAMESCREEN_OFFSETX;
                        i_y = ((p_spr.i_ScreenY - i8_scrY) >> 8)+GAMESCREEN_OFFSETY;
                        _graphics.drawImage(p_LiftImage, i_x, i_y, 0);
                    }

                    // ОТрисовываем иконку если активна
                    if (Gamelet.p_ScoreSprite.lg_SpriteActive)
                    {
                        Sprite p_spr = Gamelet.p_ScoreSprite;
                        int i_x = ((p_spr.i_ScreenX - i8_scrX) >> 8)+GAMESCREEN_OFFSETX;
                        int i_y = (((p_spr.i_ScreenY - i8_scrY) >> 8)-p_spr.i_Frame)+GAMESCREEN_OFFSETY;

                         int i_startFrame = 0;
                        switch(p_spr.i_ObjectType)
                        {
                                case Gamelet.SPRITE_CLOUD :
                                {
                                    i_startFrame = MAP_SMOKE01+p_spr.i_Frame*ImageManager.IMAGEINFO_LENGTH;
                                };break;
                                case Gamelet.SPRITE_ICON_SCORE_5 :
                                {
                                    i_startFrame = MAP_SCORES5;
                                };break;
                                case Gamelet.SPRITE_ICON_SCORE_10 :
                                {
                                    i_startFrame = MAP_SCORES10;
                                };break;
                                case Gamelet.SPRITE_ICON_SCORE_20 :
                                {
                                    i_startFrame = MAP_SCORES20;
                               };break;
                                case Gamelet.SPRITE_ICON_SCORE_50 :
                                {
                                    i_startFrame = MAP_SCORES50;
                                };break;
                                case Gamelet.SPRITE_ICON_HEART :
                                {
                                    i_startFrame = MAP_HEARTLITTLE;
                                };break;
                        }
                        ImageManager.drawImage(i_startFrame, _graphics, i_x, i_y);
                    }
                }
        ;
                break;
        }

        //#if MODEL=="E398"
        // Ограничивающие области
        _graphics.setClip(0,0,SCREEN_WIDTH,SCREEN_HEIGHT);
        _graphics.setColor(0x40);
        // Верхняя
        _graphics.fillRect(0,0,SCREEN_WIDTH,61);
        // Нижняя
        _graphics.fillRect(0,177,SCREEN_WIDTH,27);
        // Левая
        _graphics.fillRect(0,61,13,116);
        // Правая
        _graphics.fillRect(163,61,13,116);
        // Объемная рамка
        _graphics.setColor(0x70);
        _graphics.drawLine(12,61,162,61);
        _graphics.drawLine(12,61,12,177);
        _graphics.setColor(0x20);
        _graphics.drawLine(162,61,162,177);
        _graphics.drawLine(162,177,12,177);

        // Заголовок
        _graphics.drawImage(p_headerImage,28,5,0);
        //#endif

        final int YDSTART = SCREEN_HEIGHT-12;
        //#if MODEL=="C380"
        //$final int YHSTART = 1;
        //#else
        final int YHSTART = 186;
        //#endif

        // ОТрисовываем иконку меню
        if (i_lastPressedKey == KEY_CODE_SOFT_LEFT)
            ImageManager.drawImage(MAP_LOGO2, _graphics, 1, YDSTART - 5);
        else
            ImageManager.drawImage(MAP_LOGO1, _graphics, 1, YDSTART - 5);

        // Отрисовка показателя жизни
        //#if MODEL=="C380"
        //$final int LIFE_INDICATOR_X = 1;
        //#else
        final int LIFE_INDICATOR_X = 24;
        //#endif
        ImageManager.drawImage(MAP_ICON_LIFE,_graphics,LIFE_INDICATOR_X,YHSTART);
        drawScores(_graphics,LIFE_INDICATOR_X +12,YHSTART,10,Gamelet.i_PlayerLifes);

        // Индикатор подушек
        //#if MODEL=="C380"
        //$final int SHELL_INDICATOR_X = 35;
        //#else
        final int SHELL_INDICATOR_X = 67;
        //#endif
        if (Gamelet.i_SuperShells>0)
        ImageManager.drawImage(MAP_ICON_SUPERPILLOW,_graphics,SHELL_INDICATOR_X,YHSTART);
        else
        ImageManager.drawImage(MAP_ICON_PILLOW,_graphics,SHELL_INDICATOR_X,YHSTART);
        drawScores(_graphics,SHELL_INDICATOR_X +13,YHSTART,100,Gamelet.i_PlayerShells);

        // Набранные очки
        //#if MODEL=="C380"
        //$final int SCORE_INDICATOR_X = 79;
        //#else
        final int SCORE_INDICATOR_X = 117;
        //#endif
        ImageManager.drawImage(MAP_STARLITTLE,_graphics,SCORE_INDICATOR_X,YHSTART+1);
        drawScores(_graphics,SCORE_INDICATOR_X +8,YHSTART,10000,Gamelet.i_PlayerScores);
    }

    private static final void drawBar(Graphics _g, int _color, int _x, int _y, int _width, int _height)
    {
        _g.setClip(_x, _y, _width, _height);
        _g.setColor(_color);
        _g.fillRect(_x, _y, _width, _height);
    }

    private static final void paintGameStage(Graphics _graphics)
    {
        _graphics.setColor(0xFFFF00);
        _graphics.fillRect(0, 0, SCREEN_WIDTH, SCREEN_HEIGHT);
        _graphics.setColor(0x0000FF);
        _graphics.drawString(LangBlock.getStringForIndex(StageTXT) + " " + (i_selectedGameStage + 1), 0, 20, 0);
    }

    private static final void paintGameOver(Graphics _graphics)
    {
        _graphics.drawImage(p_gameWinImage, 0, 0, 0);
    }

    private final void keyPressed(int _keyCode)
    {
        switch (_keyCode)
        {
            case KEY_CODE_UP:
            case JOY_СODE_UP:
                {
                    i_KeyFlags |= Gamelet.KEY_UP;
                }
        ;
                break;
            case KEY_CODE_LEFT:
            case JOY_CODE_LEFT:
                {
                    i_KeyFlags |= Gamelet.KEY_LEFT;
                }
        ;
                break;
            case KEY_CODE_RIGHT:
            case JOY_CODE_RIGHT:
                {
                    i_KeyFlags |= Gamelet.KEY_RIGHT;
                }
        ;
                break;
            case KEY_CODE_DOWN:
            case JOY_CODE_DOWN:
                {
                    i_KeyFlags |= Gamelet.KEY_DOWN;
                }
        ;
                break;
            case KEY_CODE_FIRE:
            case JOY_CODE_FIRE:
                {
                    i_KeyFlags |= Gamelet.KEY_FIRE;
                }
        ;
                break;
            case KEY_CODE_KEY1:
                break;
            case KEY_CODE_KEY2:
                break;
            case KEY_CODE_SOFT_LEFT:
                break;
        }
    }

    private final void keyReleased(int _keyCode)
    {
        switch (_keyCode)
        {
            case KEY_CODE_UP:
            case JOY_СODE_UP:
                {
                    i_KeyFlags &= ~Gamelet.KEY_UP;
                }
        ;
                break;
            case KEY_CODE_LEFT:
            case JOY_CODE_LEFT:
                {
                    i_KeyFlags &= ~Gamelet.KEY_LEFT;
                }
        ;
                break;
            case KEY_CODE_RIGHT:
            case JOY_CODE_RIGHT:
                {
                    i_KeyFlags &= ~Gamelet.KEY_RIGHT;
                }
        ;
                break;
            case KEY_CODE_DOWN:
            case JOY_CODE_DOWN:
                {
                    i_KeyFlags &= ~Gamelet.KEY_DOWN;
                }
        ;
                break;
            case KEY_CODE_FIRE:
            case JOY_CODE_FIRE:
                {
                    i_KeyFlags &= ~Gamelet.KEY_FIRE;
                }
        ;
                break;
            case KEY_CODE_KEY1:
                break;
            case KEY_CODE_KEY2:
                break;
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
}
