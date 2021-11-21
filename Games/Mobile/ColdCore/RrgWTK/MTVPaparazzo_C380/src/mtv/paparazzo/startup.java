package mtv.paparazzo;

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

    //#if VENDOR=="MOTOROLA" && MODEL=="C380"
    //$private static final int SCREEN_WIDTH = 128;
    //$private static final int SCREEN_HEIGHT = 116;
    //#else
    //#if VENDOR=="MOTOROLA" && MODEL=="E398"
    private static final int SCREEN_WIDTH = 176;
    private static final int SCREEN_HEIGHT = 204;
    //#endif

    //#endif

    private static final int COLOR_MAIN_BACKGROUND = 0x990000;

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

                     if (p_Image_Splash != null)
                     {
                         _graphics.drawImage(p_Image_Splash, 0, 0, 0);
                     }
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
            case MODE_GAMEFINAL:
                   {
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
                       Gamelet.releaseGame();
                       p_Image_LoadingLogo = null;
                       i_selectedGameLevel = -1;
                       ab_ScoreTable = null;
                       try
                             {
                           p_Image_Splash = Image.createImage(RESOURCE_SPLASH);
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
            if (!onInitNewGame(i_selectedGameLevel)) throw new Exception("E0");
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
            if (DataStorage.init(Gamelet.getID()))
            {
                i_LanguageIndex = -1;
                lg_Option_Light = true;
                lg_Option_Sound = true;
                lg_Option_Vibra = true;
                lg_firstStart = true;
            }
            else
            {
                i_LanguageIndex = DataStorage.ab_OptionsArray[0] & 0xFF;
                int i_options = DataStorage.ab_OptionsArray[1];
                lg_Option_Light = (i_options & 0x1) != 0;
                lg_Option_Sound = (i_options & 0x2) != 0;
                lg_Option_Vibra = (i_options & 0x4) != 0;
            }

            increaseLoadingProgress(25);

            i_LanguageIndex = LangBlock.initLanguageBlock(this.getClass(), null, 1, 1, 1, RESOURCE_LANGUAGES, i_LanguageIndex);

            GameMenu.initMenuBlock(this.getClass(), this, RESOURCE_MENU, Font.getFont(Font.FACE_PROPORTIONAL, Font.STYLE_PLAIN, Font.SIZE_MEDIUM));

            if (lg_firstStart)
            {
                packAndSaveOptions();
            }

            increaseLoadingProgress(25);

            //#if DEBUG
            System.out.println("Load game resources");
            //#endif
            loadGameResources(this);

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

        Gamelet.init();

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
                         //#if MODEL=="C380"
                         if (!Gamelet.initNewGame(SCREEN_WIDTH, SCREEN_HEIGHT, i_selectedGameLevel, this) || !onInitNewGame(i_selectedGameLevel))
                         //#else
                         if (!Gamelet.initNewGame(SCREEN_WIDTH, 150, i_selectedGameLevel, this) || !onInitNewGame(i_selectedGameLevel))
                         //#endif
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

                         //i_KeyFlags = Gamelet.KEY_NONE;

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
                                          setMode(MODE_GAMEFINAL);
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
        switch (_arg)
        {
            case Gamelet.GAMEACTION_GUARDIAN:
                    if (lg_Option_Sound)
                    {
                        SoundManager.playSound(SoundManager.SOUND_SIREN,1);
                    };break;
            default:
                if (lg_Option_Sound)
                {
                    SoundManager.playSound(SoundManager.SOUND_SHOOT,1);
                }
        }
        return 0;
    }

    //================================Переменные и массивы==============================
    private static Image p_gameWinImage;
    private static byte[] ab_TileMap;
    private static Image p_tileImage;

    private static TileBackground p_TileBackground;

    private static final int MAP_DIGIT0 = 0;
    private static final int MAP_DIGIT1 = 7;
    private static final int MAP_DIGIT2 = 14;
    private static final int MAP_DIGIT3 = 21;
    private static final int MAP_DIGIT4 = 28;
    private static final int MAP_DIGIT5 = 35;
    private static final int MAP_DIGIT6 = 42;
    private static final int MAP_DIGIT7 = 49;
    private static final int MAP_DIGIT8 = 56;
    private static final int MAP_DIGIT9 = 63;
    private static final int MAP_ICON_BADFILM = 70;
    private static final int MAP_ICON_FRAMES = 77;
    private static final int MAP_ICON_STAR = 84;
    private static final int MAP_ICON_TIMER = 91;
    private static final int MAP_LOGO_ICO01 = 98;
    private static final int MAP_LOGO_ICO02 = 105;
    private static final int MAP_PERSON00 = 112;
    private static final int MAP_PERSON01 = 119;
    private static final int MAP_PERSON02 = 126;
    private static final int MAP_PERSON03 = 133;
    private static final int MAP_PERSON04 = 140;
    private static final int MAP_PERSON05 = 147;
    private static final int MAP_PERSON06 = 154;
    private static final int MAP_PERSON07 = 161;
    private static final int MAP_PERSON08 = 168;
    private static final int MAP_PERSON09 = 175;
    private static final int MAP_PERSON10 = 182;
    private static final int MAP_PERSON11 = 189;
    private static final int MAP_VIEWCORNERLB = 196;
    private static final int MAP_VIEWCORNERLT = 203;
    private static final int MAP_VIEWCORNERRB = 210;
    private static final int MAP_VIEWCORNERRT = 217;
    private static final int MAP_WINDOWCLOSED = 224;
    private static final int MAP_WINDOWOPEN0 = 231;
    private static final int MAP_WINDOWOPEN1 = 238;
    //================================Игровые функции===================================
    private static final byte[] loadTitleMapArray(startup _this) throws Exception
    {
        final int SIZE = 4380;

        byte[] ab_Array = new byte[SIZE];

        DataInputStream p_instr = new DataInputStream(_this.getClass().getResourceAsStream("/maptile.bin"));

        for (int li = 0; li < ab_Array.length; li++)
        {
            int i_b0 = p_instr.readUnsignedByte();
            int i_b1 = p_instr.readUnsignedByte();
            int i_data = ((i_b1 << 8) | i_b0) / 32;
            ab_Array[li] = (byte) i_data;
        }

        int i_tileWidth = 16;

        for (int li = 0; li < ab_Array.length; li++)
        {
            int i_val = ab_Array[li] & 0xFF;
            int i_x = i_val % i_tileWidth;
            int i_y = i_val / i_tileWidth;
            ab_Array[li] = (byte) ((i_x << 4) | i_y);
        }

        p_instr.close();
        return ab_Array;
    }

    private static final void drawScores(Graphics _g, int _x, int _y, int _zeroNumber, int _value)
    {
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
        p_TileBackground.setXY(Gamelet.i8_screenPosX, Gamelet.i8_screenPosY);
    }

    private static final void loadGameResources(startup _this) throws Exception
    {
        ab_TileMap = loadTitleMapArray(_this);
        ImageManager.init(_this.getClass());

        //#if DEBUG
        System.out.println("Initing of sound block");
        //#endif
        SoundManager.initBlock(_this.getClass(), 50);
        //#if DEBUG
        System.out.println("Initing of sound block...ok");
        //#endif

        //#if MODEL=="C380"
        //$p_TileBackground.initTileBackground(SCREEN_WIDTH, SCREEN_HEIGHT, Gamelet.CELL_WIDTH, Gamelet.CELL_HEIGHT, false);
        //#else
        p_TileBackground.initTileBackground(SCREEN_WIDTH, 150, Gamelet.CELL_WIDTH, Gamelet.CELL_HEIGHT, false);
        //#endif
        p_TileBackground.setBlockImage(p_tileImage);
        p_TileBackground.setGameRoomArray(Gamelet.CELLNUMBER_WIDTH, ab_TileMap);
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
                 p_tileImage = null;
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
                 p_tileImage = null;
                 Runtime.getRuntime().gc();

                 try
                       {
                     if (Gamelet.i_PlayerState==Gamelet.PLAYER_WIN)
                     {
                         p_gameWinImage = Image.createImage("/win.jpg");
                     }
                     else
                     {
                         p_gameWinImage = Image.createImage("/lost.jpg");
                     }
                 }
                 catch (IOException e)
                 {
                 }
                 Runtime.getRuntime().gc();

                 if (lg_Option_Sound)
                 {
                     if (Gamelet.i_PlayerState==Gamelet.PLAYER_WIN)
                     {
                         SoundManager.playSound(SoundManager.SOUND_WIN, 1);
                     }
                     else
                     {
                         SoundManager.playSound(SoundManager.SOUND_LOST, 1);
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

    private static final boolean onInitNewGame(int _selectedGameLevel)
    {
        try
        {
            p_tileImage = Image.createImage("/tiles.png");
            p_TileBackground.setBlockImage(p_tileImage);
        }
        catch (IOException e)
        {
        }
        return true;
    }

    private static final boolean processStartApp(startup _this)
    {
        p_TileBackground = new TileBackground();
        return true;
    }

    private static final void paintGameProcess(Graphics _graphics)
    {
        _graphics.setClip(0, 0, SCREEN_WIDTH, SCREEN_HEIGHT);
        p_TileBackground.setXY(Gamelet.i8_screenPosX, Gamelet.i8_screenPosY);

        //#if MODEL=="C380"
        //$p_TileBackground.directPaint(_graphics, 0, 0);
        //$final int VERT_OFFSET = 0;
        //$final int VERT_SCREEN = SCREEN_HEIGHT;
        //#else
        p_TileBackground.directPaint(_graphics,0,27);
        final int VERT_OFFSET = 27;
        final int VERT_SCREEN = 150;
        //#endif

        if (Gamelet.lg_Flash)
        {
            _graphics.setClip(0, 0, SCREEN_WIDTH, SCREEN_HEIGHT);
            _graphics.setColor(0xFFFFFF);
            _graphics.fillRect(0, 0, SCREEN_WIDTH, SCREEN_HEIGHT);
        }
        else
        {
            // Рисуем окна
            for (int li = 0; li < Gamelet.WINDOW_NUMBER; li++)
            {
                Sprite p_spr = Gamelet.WINDOW_SPRITES[li];
                if (Gamelet.isSpriteVisible(p_spr))
                {
                    int i_x = (p_spr.i_ScreenX - Gamelet.i8_screenPosX) >> 8;
                    int i_y = ((p_spr.i_ScreenY - Gamelet.i8_screenPosY) >> 8)+VERT_OFFSET;

                    int i_frame = p_spr.i_Frame * ImageManager.IMAGEINFO_LENGTH;

                    switch (p_spr.i_ObjectType)
                    {
                    case Gamelet.SPRITE_CLOSEDWINDOW:
                         {
                             ImageManager.drawImage(MAP_WINDOWCLOSED, _graphics, i_x, i_y);
                         }
                    ;
                         break;
                    case Gamelet.SPRITE_EMPTYWINDOW:
                         {
                             ImageManager.drawImage(MAP_WINDOWOPEN0, _graphics, i_x, i_y);
                             ImageManager.drawImage(MAP_WINDOWOPEN1, _graphics, i_x, i_y);
                         }
                    ;
                         break;
                    case Gamelet.SPRITE_GUARDIAN:
                         {
                             ImageManager.drawImage(MAP_PERSON00 + i_frame, _graphics, i_x, i_y);
                             ImageManager.drawImage(MAP_WINDOWOPEN0, _graphics, i_x, i_y);
                             ImageManager.drawImage(MAP_WINDOWOPEN1, _graphics, i_x, i_y);
                         }
                    ;
                         break;
                    case Gamelet.SPRITE_STAR0:
                         {
                             ImageManager.drawImage(MAP_PERSON02 + i_frame, _graphics, i_x, i_y);
                             ImageManager.drawImage(MAP_WINDOWOPEN0, _graphics, i_x, i_y);
                             ImageManager.drawImage(MAP_WINDOWOPEN1, _graphics, i_x, i_y);
                         }
                    ;
                         break;
                    case Gamelet.SPRITE_STAR1:
                         {
                             ImageManager.drawImage(MAP_PERSON04 + i_frame, _graphics, i_x, i_y);
                             ImageManager.drawImage(MAP_WINDOWOPEN0, _graphics, i_x, i_y);
                             ImageManager.drawImage(MAP_WINDOWOPEN1, _graphics, i_x, i_y);
                         }
                    ;
                         break;
                    case Gamelet.SPRITE_STAR2:
                         {
                             ImageManager.drawImage(MAP_PERSON06 + i_frame, _graphics, i_x, i_y);
                             ImageManager.drawImage(MAP_WINDOWOPEN0, _graphics, i_x, i_y);
                             ImageManager.drawImage(MAP_WINDOWOPEN1, _graphics, i_x, i_y);
                         }
                    ;
                         break;
                    case Gamelet.SPRITE_STAR3:
                         {
                             ImageManager.drawImage(MAP_PERSON08 + i_frame, _graphics, i_x, i_y);
                             ImageManager.drawImage(MAP_WINDOWOPEN0, _graphics, i_x, i_y);
                             ImageManager.drawImage(MAP_WINDOWOPEN1, _graphics, i_x, i_y);
                         }
                    ;
                         break;
                    case Gamelet.SPRITE_STAR4:
                         {
                             ImageManager.drawImage(MAP_PERSON10 + i_frame, _graphics, i_x, i_y);
                             ImageManager.drawImage(MAP_WINDOWOPEN0, _graphics, i_x, i_y);
                             ImageManager.drawImage(MAP_WINDOWOPEN1, _graphics, i_x, i_y);
                         }
                    ;
                         break;
                    }

                    //#if DEBUG
                    i_x = (p_spr.i_ScreenX+p_spr.i_col_offsetX-Gamelet.i8_screenPosX) >> 8;
                    i_y = (p_spr.i_ScreenY+p_spr.i_col_offsetY - Gamelet.i8_screenPosY) >> 8;
                    int i_w = p_spr.i_col_width >> 8;
                    int i_h = p_spr.i_col_height >> 8;

                    _graphics.setClip(0,0,SCREEN_WIDTH,SCREEN_HEIGHT);
                    _graphics.setColor(0x00FFFF);
                    _graphics.drawRect(i_x,i_y+VERT_OFFSET,i_w,i_h);
                    //#endif
                }
            }
            // Рисуем звездочку
            if (Gamelet.p_IconSprite.lg_SpriteActive)
            {
                int i_x = (Gamelet.p_IconSprite.i_ScreenX - Gamelet.i8_screenPosX) >> 8;
                int i_y = (Gamelet.p_IconSprite.i_ScreenY - Gamelet.i8_screenPosY) >> 8;

                if (Gamelet.p_IconSprite.i_ObjectType == Gamelet.SPRITE_STARICO)
                    ImageManager.drawImage(MAP_ICON_STAR, _graphics, i_x, i_y);
                else
                    ImageManager.drawImage(MAP_ICON_BADFILM, _graphics, i_x, i_y);
            }
        }

        // Рисуем фокус
        int i_x = (Gamelet.p_FocusSprite.i_ScreenX - Gamelet.i8_screenPosX) >> 8;
        int i_y = ((Gamelet.p_FocusSprite.i_ScreenY - Gamelet.i8_screenPosY) >> 8)+VERT_OFFSET;
        int i_w = Gamelet.p_FocusSprite.i_width >> 8;
        int i_h = Gamelet.p_FocusSprite.i_height >> 8;

        final int CORNER_WIDTH = 7;
        final int CORNER_HEIGHT = 7;

        ImageManager.drawImage(MAP_VIEWCORNERLT, _graphics, i_x, i_y);
        ImageManager.drawImage(MAP_VIEWCORNERLB, _graphics, i_x, i_y + i_h - CORNER_HEIGHT);
        ImageManager.drawImage(MAP_VIEWCORNERRT, _graphics, i_x + i_w - CORNER_WIDTH, i_y);
        ImageManager.drawImage(MAP_VIEWCORNERRB, _graphics, i_x + i_w - CORNER_WIDTH, i_y + i_h - CORNER_HEIGHT);

        //#if MODEL=="C380"
        //$final int YSTART = 104;
        //#else
        final int YSTART = 192;
        _graphics.setColor(0x333333);
        _graphics.setClip(0, 0, SCREEN_WIDTH, SCREEN_HEIGHT);
        _graphics.fillRect(0,0,SCREEN_WIDTH,VERT_OFFSET);
        _graphics.fillRect(0,VERT_OFFSET+VERT_SCREEN,SCREEN_WIDTH,VERT_OFFSET);
        //#endif

        //Рисуем иконку
        if (i_lastPressedKey == KEY_CODE_SOFT_LEFT)
            ImageManager.drawImage(MAP_LOGO_ICO02, _graphics, 1, YSTART - 5);
        else
            ImageManager.drawImage(MAP_LOGO_ICO01, _graphics, 1, YSTART - 5);

        //#if MODEL=="C380"
        //$final int FRAMES_WIDTH = 10;
        //$final int FRAMES_ICO_X = 98;
        //$final int FRAMES_ICO_Y = 2;

        //$final int TIMER_WIDTH = 10;
        //$final int TIMER_ICO_X = 2;
        //$final int TIMER_ICO_Y = 2;
        //#else
        final int FRAMES_WIDTH = 10;
        final int FRAMES_ICO_X = 146;
        final int FRAMES_ICO_Y = 2;

        final int TIMER_WIDTH = 10;
        final int TIMER_ICO_X = 2;
        final int TIMER_ICO_Y = 2;
        //#endif

        // Рисуем показатель количества кадров
        ImageManager.drawImage(MAP_ICON_FRAMES, _graphics, FRAMES_ICO_X, FRAMES_ICO_Y);
        drawScores(_graphics, FRAMES_ICO_X + FRAMES_WIDTH + 3, FRAMES_ICO_Y, 10, Gamelet.i_currentCadr);

        // Рисуем показатель времени
        if (Gamelet.i_GameState != Gamelet.STATE_PAUSED)
        {
            ImageManager.drawImage(MAP_ICON_TIMER, _graphics, TIMER_ICO_X, TIMER_ICO_Y);
            int i_widthBar = (30 * ((int) ((Gamelet.l_FinalTime - System.currentTimeMillis()) << 8) / Gamelet.i_TimeForLevel)) >> 8;
            drawBar(_graphics, 0x0000FF, TIMER_ICO_X + TIMER_WIDTH + 2, TIMER_ICO_Y + 2, i_widthBar, 6);
        }

        // Выводим количество очков
        drawScores(_graphics, SCREEN_WIDTH - 41, YSTART, 10000, Gamelet.getPlayerScore());
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