package mtv.slideshow;

import javax.microedition.midlet.*;
import javax.microedition.lcdui.*;
import javax.microedition.lcdui.game.Sprite;
import javax.microedition.rms.RecordStoreException;
import javax.microedition.media.*;
import javax.microedition.media.control.VolumeControl;
import java.io.*;

public class Main extends MIDlet implements Runnable, MenuActionListener
{
    private static final int SOUND_LEVEL = 20;

    //#if DEVICE=="MOTO_C380"
    private static final int SCREEN_WIDTH = 128;
    private static final int SCREEN_HEIGHT = 116;
    //#else
    //#if DEVICE=="MOTO_E398"
    //$private static final int SCREEN_WIDTH = 176;
    //$private static final int SCREEN_HEIGHT = 204;
    //#else
    //#assert "------Unsupported device-------"
    //#endif
    //#endif

    protected static final int COLOR_CURSOR_FIELD_UNDER = 0x0000FF;
    protected static final int COLOR_CURSOR_FIELD_OVER = 0x00FFFF;
    protected static final int COLOR_CURSOR_SHOW_UNDER = 0x0000FF;
    protected static final int COLOR_CURSOR_SHOW_OVER = 0xFF0000;

    private static final int COLOR_LOADING_BAR_BACKGROUND = 0x00B4FF;
    private static final int COLOR_LOADING_BAR = 0x0F00EC;
    private static final int COLOR_LOADING_BACKGROUND = 0xFF8900;
    private static final int COLOR_MAIN_BACKGROUND = 0x000000;
    private static final int COLOR_TEXT_OVER = 0xFFFFFF;
    private static final int COLOR_TEXT_UNDER = 0x000000;

    protected static final int CURSOR_STEP = 5;

    private static final int ITERATION_DELAY = 30;
    private static final int TEXT_MOVE_DELAY = 3;
    private static final int TEXT_STEP = 1;

    private static final int INDICATORS_HEIGHT = 20;
    private static final int INDICATORS_WIDTH = 20;
    private static final int DIGITS_WIDTH = 6;
    private static final int DIGITS_HEIGHT = 10;

    private static final int SLIDES_NUMBER = 7;
    private static final Font TEXT_FONT = Font.getDefaultFont();

    private static final int CURSOR_PANEL_HEIGHT = 40;

    private static final int KEY_CODE_LEFT = -2;
    private static final int KEY_CODE_RIGHT = -5;
    private static final int KEY_CODE_UP = -1;
    private static final int KEY_CODE_DOWN = -6;
    private static final int KEY_CODE_FIRE = -20;
    private static final int KEY_CODE_EXIT = -21;
    private static final int KEY_CODE_ZOOM = -22;

    private static final String[][] SLIDES_COMMENTS = new String[][]
    {
        new String[]
        {
            "Slide1",
            "Slide2",
            "Slide3",
            "Slide4",
            "Slide5",
            "Slide6",
            "Slide7",
        },
        new String[]
        {
            //#if VOL=="1"
            "Родина-мать зовет...",
            "Аты-баты, шли солдаты...",
            "Понимаешь? (Таня и Кулагин)",
            "Улыбайся, нас снимают (Таня и Кулагин)",
            "Александр Анатольевич",
            "Тутта Ларсен",
            "Интервью с The Rasmus",
            //#else
            "Воздушный поцелуй (\"Виа Гра\" и Валерий Меладзе)",
            "\"Это между нами любовь\" (Глюк'ozа)",
            "Господа! Мы звери, господа! (\"Звери\")",
            "Горячие финские парни (The Rasmus)",
            "Даррен Хейз и Тутта Ларсен",
            "Рома Зверь говорит речь",
            "Даррен Хейз в Кремлe"
            //#endif
        }
    };


    private class InsideCanvas extends Canvas implements Runnable
    {
        protected int i_BackgroundColor;

        protected Font p_MessageFont;
        protected int i_FontColor;

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
                if (i_ClockFrameCounter >= 7)
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
            _g.drawString(_message, (SCREEN_WIDTH - i_stringWidth) >> 1, (SCREEN_HEIGHT - i_height) >> 1, 0);
}

        protected synchronized void keyRepeated(int _code)
        {
            switch (i_CurrentMidletState)
            {
                case STATE_WORKING:
                    {
                        switch (_code)
                        {
                            case Canvas.KEY_NUM2:
                            case KEY_CODE_UP:
                                {
                                    if (lg_Zoomed)
                                    {
                                        if (i8_cursorPosY > 0)
                                        {
                                            i8_cursorPosY -= i8_stepY;
                                            if (i8_cursorPosY < 0) i8_cursorPosY = 0;
                                            p_MainCanvas.repaint();
                                        }
                                    }
                                }
                                ;
                                break;
                            case Canvas.KEY_NUM8:
                            case KEY_CODE_DOWN:
                                {
                                    if (lg_Zoomed)
                                    {
                                        if (i8_cursorPosY + i8_CursorHeight < i8_cursorZoneHeight)
                                        {
                                            i8_cursorPosY += i8_stepY;
                                            if (i8_cursorPosY < 0) i8_cursorPosY = 0;
                                            p_MainCanvas.repaint();
                                        }
                                    }
                                }
                                ;
                                break;
                            case Canvas.KEY_NUM4:
                            case KEY_CODE_LEFT:
                                {
                                    if (lg_Zoomed)
                                    {
                                        if (i8_cursorPosX > 0)
                                        {
                                            i8_cursorPosX -= i8_stepX;
                                            if (i8_cursorPosX < 0) i8_cursorPosX = 0;
                                            p_MainCanvas.repaint();
                                        }
                                    }
                                }
                                ;
                                break;
                            case KEY_CODE_RIGHT:
                            case Canvas.KEY_NUM6:
                                {
                                    if (lg_Zoomed)
                                    {
                                        if (i8_cursorPosX + i8_CursorWidth < i8_cursorZoneWidth)
                                        {
                                            i8_cursorPosX += i8_stepX;
                                            p_MainCanvas.repaint();
                                        }
                                    }
                                }
                                ;
                                break;
                        }
                    }
        }
        }

        protected synchronized void keyPressed(int _code)
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
                            case Canvas.KEY_NUM2:
                            case KEY_CODE_UP:
                                {
                                    i_pressedKey = KEY_UPS;
                                }
                                ;
                                break;
                            case Canvas.KEY_NUM4:
                            case KEY_CODE_LEFT:
                                {
                                    i_pressedKey = KEY_PREV;
                                    if (!lg_Zoomed)
                                        p_MainCanvas.repaint();
                                }
                                ;
                                break;
                            case Canvas.KEY_NUM8:
                            case KEY_CODE_DOWN:
                                {
                                    i_pressedKey = KEY_DWN;
                                }
                                ;
                                break;
                            case KEY_CODE_RIGHT:
                            case Canvas.KEY_NUM6:
                                {
                                    i_pressedKey = KEY_NEXT;
                                    if (!lg_Zoomed)
                                        p_MainCanvas.repaint();
                                }
                                ;
                                break;
                            case Canvas.KEY_NUM1:
                            case KEY_CODE_EXIT:
                                {
                                    i_pressedKey = KEY_EXIT;
                                    p_MainCanvas.repaint();
                                }
                                ;
                                break;
                            case Canvas.KEY_NUM3:
                            case Canvas.KEY_NUM5:
                            case KEY_CODE_FIRE:
                            case KEY_CODE_ZOOM:
                                {
                                    i_pressedKey = KEY_ZOOM;
                                    if (p_SlideImage_small != null)
                                        p_MainCanvas.repaint();
                                }
                                ;
                                break;
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

        protected synchronized void keyReleased(int _code)
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
                            case Canvas.KEY_NUM2:
                            case KEY_CODE_UP:
                                {
                                    if (lg_Zoomed)
                                    {
                                        if (i8_cursorPosY > 0)
                                        {
                                            i8_cursorPosY -= i8_stepY;
                                            if (i8_cursorPosY < 0) i8_cursorPosY = 0;
                                            p_MainCanvas.repaint();
                                        }
                                    }
                                }
                                ;
                                break;
                            case Canvas.KEY_NUM8:
                            case KEY_CODE_DOWN:
                                {
                                    if (lg_Zoomed)
                                    {
                                        if (i8_cursorPosY + i8_CursorHeight < i8_cursorZoneHeight)
                                        {
                                            i8_cursorPosY += i8_stepY;
                                            if (i8_cursorPosY < 0) i8_cursorPosY = 0;
                                            p_MainCanvas.repaint();
                                        }
                                    }
                                }
                                ;
                                break;
                            case Canvas.KEY_NUM4:
                            case KEY_CODE_LEFT:
                                {
                                    if (lg_Zoomed)
                                    {
                                        if (i8_cursorPosX > 0)
                                        {
                                            i8_cursorPosX -= i8_stepX;
                                            if (i8_cursorPosX < 0) i8_cursorPosX = 0;
                                            p_MainCanvas.repaint();
                                        }
                                    }
                                    else
                                    {
                                        p_MainCanvas.repaint();
                                        if (i_currentFrame > 0)
                                        {
                                            i_WorkPressedKey = PREV_FRAME;
                                        }
                                    }
                                }
                                ;
                                break;
                            case KEY_CODE_RIGHT:
                            case Canvas.KEY_NUM6:
                                {
                                    if (lg_Zoomed)
                                    {
                                        if (i8_cursorPosX + i8_CursorWidth < i8_cursorZoneWidth)
                                        {
                                            i8_cursorPosX += i8_stepX;
                                            p_MainCanvas.repaint();
                                        }
                                    }
                                    else
                                    {
                                        p_MainCanvas.repaint();
                                        if (i_currentFrame < SLIDES_NUMBER - 1)
                                        {
                                            i_WorkPressedKey = NEXT_FRAME;
                                        }
                                    }
                                }
                                ;
                                break;
                            case Canvas.KEY_NUM1:
                            case KEY_CODE_EXIT:
                                {
                                    p_menuBlock.MB_clearScreenStack(SCR_MainSCR, true);
                                }
                                ;
                                break;
                            case Canvas.KEY_NUM3:
                            case KEY_CODE_FIRE:
                            case Canvas.KEY_NUM5:
                            case KEY_CODE_ZOOM:
                                {
                                    if (p_SlideImage_small != null)
                                    {
                                        p_MainCanvas.repaint();
                                        ZoomImageMode();
                                    }
                                }
                                ;
                                break;
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

        private final void drawTextTitle(int _y, Graphics _g)
        {
            int i_X = i_TextMessagePosition + 1;
            int i_Y = _y + 1;
            _g.setFont(TEXT_FONT);
            _g.setColor(COLOR_TEXT_UNDER);
            _g.drawString(s_TextMessageForSlide, i_X, i_Y, Graphics.TOP | Graphics.LEFT);
            i_X = i_TextMessagePosition;
            i_Y = _y;
            _g.setColor(COLOR_TEXT_OVER);
            _g.drawString(s_TextMessageForSlide, i_X, i_Y, Graphics.TOP | Graphics.LEFT);
}

        private final void drawFrameNumber(int _x, int _y, Graphics _g)
        {
            if (i_currentFrame < 0) return;
            int i_indiX = _x;
            int i_iconY = _y;

            _g.setClip(i_indiX, i_iconY, DIGITS_WIDTH, INDICATORS_HEIGHT);
            _g.drawImage(ap_digits[((i_currentFrame + 1) / 10) + 1], i_indiX, i_iconY, 0);
            i_indiX += DIGITS_WIDTH;

            _g.setClip(i_indiX, i_iconY, DIGITS_WIDTH, INDICATORS_HEIGHT);
            _g.drawImage(ap_digits[((i_currentFrame + 1) % 10) + 1], i_indiX, i_iconY, 0);
            i_indiX += DIGITS_WIDTH;

            _g.setClip(i_indiX, i_iconY, DIGITS_WIDTH, INDICATORS_HEIGHT);
            _g.drawImage(ap_digits[0], i_indiX, i_iconY, 0);
            i_indiX += DIGITS_WIDTH;

            _g.setClip(i_indiX, i_iconY, DIGITS_WIDTH, INDICATORS_HEIGHT);
            _g.drawImage(ap_digits[(SLIDES_NUMBER / 10) + 1], i_indiX, i_iconY, 0);
            i_indiX += DIGITS_WIDTH;

            _g.setClip(i_indiX, i_iconY, DIGITS_WIDTH, INDICATORS_HEIGHT);
            _g.drawImage(ap_digits[(SLIDES_NUMBER % 10) + 1], i_indiX, i_iconY, 0);

            _g.setClip(0, 0, SCREEN_WIDTH, SCREEN_HEIGHT);
}


        public void paint(Graphics _g)
        {
            switch (i_CurrentMidletState)
            {
                case STATE_INITING:
                    {
                        _g.setColor(i_BackgroundColor);
                        _g.fillRect(0, 0, SCREEN_WIDTH, SCREEN_HEIGHT);
                        drawMessageOnCenterScreen(_g, "Initing...");
                    }
                    ;
                    break;
                case STATE_LOADING:
                    {
                        _g.setColor(COLOR_LOADING_BACKGROUND);
                        _g.fillRect(0, 0, SCREEN_WIDTH, SCREEN_HEIGHT);

                        // Высчитываем координаты вывода
                        int i_splImgX = 0;
                        int i_splImgY = 0;

                        if (p_loadingLogo != null)
                        {
                            int i_logoWidth = 50;//p_loadingLogo.getWidth();
                            int i_logoHeight = p_loadingLogo.getHeight();

                            i_splImgX = (SCREEN_WIDTH - i_logoWidth) >> 1;
                            i_splImgY = ((SCREEN_HEIGHT + LOADING_BAR_HEIGHT + LOADING_BAR_OFFSET_FROM_SPLASH) - i_logoHeight) >> 1;
                            _g.drawImage(p_loadingLogo, i_splImgX, i_splImgY, 0);

                            i_splImgY += i_logoHeight + LOADING_BAR_OFFSET_FROM_SPLASH;
                        }
                        else
                        {
                            i_splImgY = SCREEN_HEIGHT >> 1;
                        }
                        i_splImgY = i_splImgY > SCREEN_HEIGHT - LOADING_BAR_HEIGHT - 1 ? SCREEN_HEIGHT - LOADING_BAR_HEIGHT - 1 : i_splImgY;

                        i_splImgX = (SCREEN_WIDTH - LOADING_BAR_WIDTH) >> 1;
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
                            int i_outX = (SCREEN_WIDTH - p_splashLogo.getWidth()) >> 1;
                            int i_outY = (SCREEN_HEIGHT - p_splashLogo.getHeight()) >> 1;

                            if (i_outX > 0 && i_outY > 0)
                            {
                                _g.setColor(i_BackgroundColor);
                                _g.fillRect(0, 0, SCREEN_WIDTH, SCREEN_HEIGHT);
                            }

                            _g.drawImage(p_splashLogo, i_outX, i_outY, 0);
                        }
                        else
                        {
                            _g.setColor(i_BackgroundColor);
                            _g.fillRect(0, 0, SCREEN_WIDTH, SCREEN_HEIGHT);
                        }
                    }
                    ;
                    break;
                case STATE_WORKING:
                    {
                        // Отрисовываем рабочую панель
                        _g.setColor(COLOR_MAIN_BACKGROUND);
                        _g.fillRect(0, 0, SCREEN_WIDTH, SCREEN_HEIGHT);

                        // Отрисовка фрейма
                        _g.setClip(0, 0, SCREEN_WIDTH, SCREEN_HEIGHT);
                        if (lg_Zoomed)
                        {
                            int i_xoffset = 0 - ((i8_cursorPosX * i8_coeffX) >> 16);
                            int i_yoffset = 0 - ((i8_cursorPosY * i8_coeffY) >> 16);

                            _g.drawImage(p_SlideImage_big, i_xoffset, i_yoffset, 0);
                        }
                        else
                        {
                            if (p_SlideImage_small == null)
                            {
                                _g.drawImage(p_SlideImage_big, (SCREEN_WIDTH - p_SlideImage_big.getWidth()) >> 1, (SCREEN_HEIGHT - p_SlideImage_big.getHeight()) >> 1, Graphics.TOP | Graphics.LEFT);
                            }
                            else
                            {
                                _g.drawImage(p_SlideImage_small, (SCREEN_WIDTH - p_SlideImage_small.getWidth()) >> 1, (SCREEN_HEIGHT - p_SlideImage_small.getHeight()) >> 1, Graphics.TOP | Graphics.LEFT);
                            }
                        }

                        // Отрисовка текста
                        if (lg_Option_Vibration) drawTextTitle(1, _g);

                        // Отрисовываем иконки
                        int i_iconY = SCREEN_HEIGHT - 2 - INDICATORS_HEIGHT;

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

                        if (lg_Option_Indicators)
                        {
                            i_indiX = (SCREEN_WIDTH - (INDICATORS_WIDTH << 1)) >> 1;

                            if (!lg_Zoomed)
                            {
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

                                if (i_currentFrame < SLIDES_NUMBER - 1)
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
                            }

                            i_indiX = SCREEN_WIDTH - INDICATORS_WIDTH;

                            if (p_SlideImage_small != null)
                            {
                                int i_indIndex = 3;
                                if (lg_Zoomed) i_indIndex = 4;

                                if (i_pressedKey == KEY_ZOOM)
                                {
                                    _g.setClip(i_indiX, i_iconY, INDICATORS_WIDTH, INDICATORS_HEIGHT);
                                    _g.drawImage(ap_indicators[i_indIndex], i_indiX, i_iconY - INDICATORS_HEIGHT, 0);
                                }
                                else
                                {
                                    _g.setClip(i_indiX, i_iconY, INDICATORS_WIDTH, INDICATORS_HEIGHT);
                                    _g.drawImage(ap_indicators[i_indIndex], i_indiX, i_iconY, 0);
                                }
                            }

                            // Выводим номер фрейма только если отображалась уменьшенная картинка
                            if (lg_Zoomed & p_SlideImage_small != null)
                            {
                                _g.setClip(0, 0, SCREEN_WIDTH, SCREEN_HEIGHT);
                                int i_xC = 0;
                                int i_yC = 0;

                                int i_w = i8_cursorZoneWidth >> 8;
                                int i_h = i8_cursorZoneHeight >> 8;
                                i_xC = (SCREEN_WIDTH - i_w) >> 1;
                                i_yC = SCREEN_HEIGHT - i_h;

                                // Поле
                                _g.setColor(COLOR_CURSOR_FIELD_UNDER);
                                _g.drawRect(i_xC + 1, i_yC + 1, i_w, i_h);
                                _g.setColor(COLOR_CURSOR_FIELD_OVER);
                                _g.drawRect(i_xC, i_yC, i_w, i_h);

                                // Курсор
                                int i_x = (i8_cursorPosX >> 8);
                                int i_y = (i8_cursorPosY >> 8);

                                _g.setColor(COLOR_CURSOR_SHOW_UNDER);
                                int i_curW = i8_CursorWidth >> 8;
                                int i_curH = i8_CursorHeight >> 8;
                                _g.drawRect(i_xC + i_x + 1, i_yC + i_y + 1, i_curW, i_curH);
                                _g.setColor(COLOR_CURSOR_SHOW_OVER);
                                _g.drawRect(i_xC + i_x, i_yC + i_y, i_curW, i_curH);
                            }
                            else
                            {
                                i_indiX = (SCREEN_WIDTH - DIGITS_WIDTH * 5 - 3);
                                drawFrameNumber(i_indiX, i_iconY - DIGITS_HEIGHT, _g);
                            }
                        }
                    }
                    ;
                    break;
                case STATE_WAITING:
                    {
                        _g.setColor(i_BackgroundColor);
                        _g.fillRect(0, 0, SCREEN_WIDTH, SCREEN_HEIGHT);

                        String s_str = getStringForIndex(WaitPleaseTXT);
                        int i_strW = p_MessageFont.stringWidth(s_str);
                        int i_strH = p_MessageFont.getHeight();

                        int i_coordY = 0;
                        int i_coordX = 0;
                        if (p_clockImage != null)
                        {
                            int i_frameWidth = p_clockImage.getWidth() / 7;
                            int i_frameHeight = p_clockImage.getHeight();

                            i_coordX = (SCREEN_WIDTH - i_frameWidth) >> 1;
                            i_coordY = (SCREEN_HEIGHT - i_frameHeight) >> 1;

                            _g.setClip(i_coordX, i_coordY, i_frameWidth, i_frameHeight);
                            _g.drawImage(p_clockImage, i_coordX - i_frameWidth * i_ClockFrameCounter, i_coordY, 0);
                            _g.setClip(0, 0, SCREEN_WIDTH, SCREEN_HEIGHT);
                        }
                        else
                        {
                            i_coordY = (SCREEN_HEIGHT - i_strH) >> 1;
                        }
                        i_coordX = (SCREEN_WIDTH - i_strW) >> 1;
                        _g.setColor(i_FontColor);
                        _g.drawString(s_str, i_coordX, i_coordY, Graphics.LEFT | Graphics.BOTTOM);
                    }
                    ;
                    break;
                case STATE_PAUSED:
                    {
                        _g.setColor(i_BackgroundColor);
                        _g.fillRect(0, 0, SCREEN_WIDTH, SCREEN_HEIGHT);
                        drawMessageOnCenterScreen(_g, "Paused...");
                    }
                    ;
                    break;
                case STATE_RELEASING:
                    {
                        _g.setColor(i_BackgroundColor);
                        _g.fillRect(0, 0, SCREEN_WIDTH, SCREEN_HEIGHT);
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
    private static final String RESOURCE_SLIDEPREF = "/res/slides/slide";
    private static final String RESOURCE_ABOUT_ICON = "/res/abouticon.png";
    private static final String RESOURCE_NUMBERS_FONT = "/res/digits.png";
    private static final String RESOURCE_INDICATORS = "/res/ind.png";
    private static final String RESOURCE_BCKGNDMUSIC = "/res/bckgnd.mid";
    private static final String RMS_OPTIONS_RECORD_NAME = "%$OPTNS$%";

    private Image p_loadingLogo;
    private Image p_splashLogo;

    private int i_loadingProgress;

    private int i_Option_LanguageID;
    private boolean lg_Option_Light;
    private boolean lg_Option_Vibration;
    private boolean lg_Option_Sound;
    private boolean lg_Option_Indicators;

    private static Player p_BackgroundMusicPlayer;

    private Image p_aboutIcon;

    private Image[] ap_digits;
    private Image[] ap_indicators;
    private Image[] ap_Thumbs;

    private static boolean lg_Zoomed;

    private static String s_TextMessageForSlide;
    private static int i_TextMessageWidth;
    private static int i_TextMessagePosition;
    private static boolean lg_TextMessageToLeft;

    private static int i_WorkPressedKey = 0;

    private static final int NEXT_FRAME = 1;
    private static final int PREV_FRAME = 2;

    private static int i_currentFrame = 0;
    private static int i_pressedKey = 0;

    private static final int KEY_NONE = 0;
    private static final int KEY_EXIT = 1;
    private static final int KEY_PREV = 2;
    private static final int KEY_NEXT = 3;
    private static final int KEY_ZOOM = 4;
    private static final int KEY_UPS = 5;
    private static final int KEY_DWN = 6;

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
            lg_Option_Indicators = true;
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
            lg_Option_Indicators = p_dataStream.readBoolean();
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
            p_dataStream.writeBoolean(lg_Option_Indicators);
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

    private void loadSlide(int _number)
    {
        if (i_currentFrame == _number) return;
        _showWait();

        try
        {
            //#if VOL=="1"
            String s_res = RESOURCE_SLIDEPREF + (_number + 1) + ".jpg";
            //#else
            //$String s_res = RESOURCE_SLIDEPREF + (_number + 8) + ".jpg";
            //#endif
            loadSlideImage(s_res);

            i_currentFrame = _number;
            s_TextMessageForSlide = SLIDES_COMMENTS[i_Option_LanguageID][_number];
            i_TextMessagePosition = 5;
            i_TextMessageWidth = TEXT_FONT.stringWidth(s_TextMessageForSlide + 2);

            int i_w = SCREEN_WIDTH;
            if (i_w >= i_TextMessageWidth) i_TextMessagePosition = (i_w - i_TextMessageWidth) >> 1;
            lg_TextMessageToLeft = true;

            lg_Zoomed = false;

            _hiddeWait(false);
        }
        catch (Exception e)
        {
            _hiddeWait(true);
            p_menuBlock.MB_viewAlert(getStringForIndex(ErrorTXT), "Loading error", AlertType.ERROR, 5000);
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
            if (p_BackgroundMusicPlayer != null)
            {
                try
                {
                    p_BackgroundMusicPlayer.stop();
                    p_BackgroundMusicPlayer.realize();
                }
                catch (MediaException e)
                {
                }
                p_BackgroundMusicPlayer = null;
            }
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

    private synchronized void _hiddeWait(boolean _backToMenu)
    {
        Displayable p_dsp = null;
        if (_backToMenu)
            p_dsp = p_menuBlock.MB_p_Form;
        else
            p_dsp = p_MainCanvas;

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
            case COMMAND_ShowCMD:
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
                    // Выбран стартовый слайд
                    loadSlide(_itemId);
                    i_CurrentMidletState = STATE_WORKING;
                    p_CurrentDisplay.setCurrent(p_MainCanvas);
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
                        _hiddeWait(true);
                    }
                    catch (Exception e)
                    {
                        _hiddeWait(true);
                        p_menuBlock.MB_viewAlert(getStringForIndex(ErrorTXT), e.getMessage(), AlertType.ERROR, Alert.FOREVER);
                    }
                }
                ;
                break;
        }
}

    protected static Image p_SlideImage_small;
    protected static Image p_SlideImage_big;
    protected static int i8_cursorPosX, i8_cursorPosY;
    protected static int i8_CursorWidth, i8_CursorHeight;
    protected static int i8_cursorZoneWidth, i8_cursorZoneHeight;
    protected static int i8_stepX, i8_stepY;
    protected static int i8_coeffX, i8_coeffY;

    protected final void loadSlideImage(String _image) throws IOException
    {
        p_SlideImage_big = null;
        p_SlideImage_small = null;
        Runtime.getRuntime().gc();

        // Загружаем большую картинку
        p_SlideImage_big = Image.createImage(_image);
        int i8_coeff = (p_SlideImage_big.getWidth() << 8) / p_SlideImage_big.getHeight();

        // Делаем маленькую картинку вписанную в размер экрана
        int i_smallW = 0;
        int i_smallH = 0;

        if (p_SlideImage_big.getHeight() <= SCREEN_HEIGHT && p_SlideImage_big.getWidth() <= SCREEN_WIDTH)
        {
            return;
        }

        if (p_SlideImage_big.getHeight() > p_SlideImage_big.getWidth())
        {
            // Вписываем по высоте
            i_smallH = SCREEN_HEIGHT;
            i_smallW = i_smallH * i8_coeff;
            if ((i_smallW & 0xFF) > 0x80) i_smallW = (i_smallW >> 8) + 1; else i_smallW >>= 8;
        }
        else
        {
            // Вписываем по ширине
            i_smallW = SCREEN_WIDTH;
            i_smallH = (i_smallW << 16) / i8_coeff;
            if ((i_smallH & 0xFF) > 0x80) i_smallH = (i_smallH >> 8) + 1; else i_smallH >>= 8;
        }

        p_SlideImage_small = createThumbnail(p_SlideImage_big, i_smallW, i_smallH);

        // Рассчитываем размеры курсора, смещение и положение
        i8_cursorZoneHeight = (CURSOR_PANEL_HEIGHT << 8);
        i8_cursorZoneWidth = (i8_cursorZoneHeight * i8_coeff) >> 8;
        i8_CursorWidth = (((SCREEN_WIDTH << 8) / p_SlideImage_big.getWidth()) * i8_cursorZoneWidth) >> 8;
        i8_CursorHeight = (((SCREEN_HEIGHT << 8) / p_SlideImage_big.getHeight()) * i8_cursorZoneHeight) >> 8;
        i8_cursorPosX = (i8_cursorZoneWidth - i8_CursorWidth) >> 1;
        i8_cursorPosY = (i8_cursorZoneHeight - i8_CursorHeight) >> 1;

        i8_coeffX = (p_SlideImage_big.getWidth() << 16) / i8_cursorZoneWidth;
        i8_coeffY = (p_SlideImage_big.getHeight() << 16) / i8_cursorZoneHeight;

        i8_stepX = (CURSOR_STEP << 16) / i8_coeffX;
        i8_stepY = (CURSOR_STEP << 16) / i8_coeffY;
    }

    protected static final void ZoomImageMode()
    {
        lg_Zoomed = !lg_Zoomed;
}

    private final void processTextTitle()
    {
        if (!lg_Option_Vibration) return;

        int i_scrW = SCREEN_WIDTH;

        if (i_scrW >= i_TextMessageWidth)
        {
            i_TextMessagePosition = (i_scrW - i_TextMessageWidth) >> 1;
        }
        else
        {
            if (lg_TextMessageToLeft)
            {
                i_TextMessagePosition -= TEXT_STEP;

                if (i_TextMessagePosition + i_TextMessageWidth < i_scrW)
                {
                    lg_TextMessageToLeft = false;
                }
            }
            else
            {
                i_TextMessagePosition += TEXT_STEP;

                if (i_TextMessagePosition > 0)
                {
                    lg_TextMessageToLeft = true;
                }
            }
        }
}

    public void run()
    {
        i_currentFrame = -1;
        i_loadingProgress = 0;
        i_CurrentMidletState = STATE_LOADING;
        p_MainCanvas.repaint();

        try
        {
            try
            {
                //#if VOL=="1"
                p_rmsBlock = new rmsFS("CHKR2432");
                //#else
                p_rmsBlock = new rmsFS("CHKR8322");
                //#endif
            }
            catch (RecordStoreException e)
            {
                throw new IOException("Fatal rms error");
            }
            i_loadingProgress += 25;
            p_MainCanvas.repaint();

            loadOptionsFromRMS();
            i_Option_LanguageID = p_languageBlock.LB_initLanguageBlock(RESOURCE_LANGUAGELIST, i_Option_LanguageID);
            i_loadingProgress += 25;
            p_MainCanvas.repaint();

            p_menuBlock.MB_initMenuBlock(RESOURCE_MENU);
            i_loadingProgress += 25;
            p_MainCanvas.repaint();

            p_aboutIcon = Image.createImage(RESOURCE_ABOUT_ICON);

            Image p_img = Image.createImage(RESOURCE_INDICATORS);
            ap_indicators = parseImage(p_img, 5);
            p_img = null;
            p_img = Image.createImage(RESOURCE_NUMBERS_FONT);
            ap_digits = parseImage(p_img, 11);
            p_img = null;
            try
            {
                p_img = Image.createImage(RESOURCE_SLIDEPREF + "thumbs.jpg");
                ap_Thumbs = parseImage(p_img, 14);
            }
            catch (IOException e)
            {
                ap_Thumbs = null;
            }

            try
            {
                p_BackgroundMusicPlayer = Manager.createPlayer(getClass().getResourceAsStream(RESOURCE_BCKGNDMUSIC), "audio/midi");
                p_BackgroundMusicPlayer.prefetch();
                Control[] ap_Controls = p_BackgroundMusicPlayer.getControls();
                VolumeControl p_vol = null;
                for (int li = 0; li < ap_Controls.length; li++)
                {
                    if (ap_Controls[li] instanceof VolumeControl)
                    {
                        p_vol = (VolumeControl) ap_Controls[li];
                        break;
                    }
                }
                if (p_vol != null)
                {
                    p_vol.setLevel(SOUND_LEVEL);
                }
            }
            catch (IOException e)
            {
                p_BackgroundMusicPlayer = null;
            }
            catch (MediaException e)
            {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }

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

        if (p_BackgroundMusicPlayer != null)
        {
            if (lg_Option_Sound)
            {
                try
                {
                    p_BackgroundMusicPlayer.setLoopCount(-1);
                    p_BackgroundMusicPlayer.start();
                }
                catch (MediaException e)
                {
                }
            }
        }

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

        int i_textTitleDelay = 0;

        while (i_CurrentMidletState == STATE_WAITING || i_CurrentMidletState == STATE_WORKING || i_CurrentMidletState == STATE_PAUSED)
        {
            try
            {
                Thread.sleep(ITERATION_DELAY);
            }
            catch (InterruptedException e)
            {
                break;
            }

            /*
            if (lg_Option_Light)
                com.motorola.multimedia.Lighting.backlightOn();
            else
                com.motorola.multimedia.Lighting.backlightOff();
            */

            if (i_CurrentMidletState == STATE_WORKING)
            {
                i_textTitleDelay++;
                if (i_textTitleDelay >= TEXT_MOVE_DELAY)
                {
                    i_textTitleDelay = 0;
                    processTextTitle();
                }

                switch (i_WorkPressedKey)
                {
                    case PREV_FRAME:
                        {
                            loadSlide(i_currentFrame - 1);
                        }
                        ;
                        break;
                    case NEXT_FRAME:
                        {
                            loadSlide(i_currentFrame + 1);
                        }
                        ;
                        break;
                }
                i_WorkPressedKey = 0;
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
                        case ITEM_SoundITM:
                            s_caption = getStringForIndex(SoundTXT);
                            i_selIndex = lg_Option_Sound ? 0 : 1;
                            break;
                        case ITEM_IndicatorsITM:
                            s_caption = getStringForIndex(IndicatorsTXT);
                            i_selIndex = lg_Option_Indicators ? 0 : 1;
                            break;
                        case ITEM_ShowTextITM:
                            s_caption = getStringForIndex(ShowTextTXT);
                            i_selIndex = lg_Option_Vibration ? 0 : 1;
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
            case SCR_MainSCR:
                {
                    List p_list = new List(getStringForIndex(SlideListTXT), List.IMPLICIT);

                    for (int li = 0; li < SLIDES_NUMBER; li++)
                    {
                        //#if VOL=="1"
                        p_list.append(SLIDES_COMMENTS[i_Option_LanguageID][li], ap_Thumbs == null ? null : ap_Thumbs[li]);
                        //#else
                        //$p_list.append(SLIDES_COMMENTS[i_Option_LanguageID][li], ap_Thumbs == null ? null : ap_Thumbs[li+7]);
                        //#endif
                    }

                    p_list.setSelectedIndex(i_currentFrame >= 0 ? i_currentFrame : 0, true);

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
            case ITEM_IndicatorsITM:
            case ITEM_SoundITM:
            case ITEM_LanguageSelectITM:
            case ITEM_ShowTextITM:
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
                        case ITEM_SoundITM:
                            {
                                lg_Option_Sound = alg_flagarray[0];
                                if (lg_Option_Sound)
                                {
                                    if (p_BackgroundMusicPlayer != null && p_BackgroundMusicPlayer.getState() != Player.STARTED)
                                    {
                                        try
                                        {
                                            p_BackgroundMusicPlayer.setLoopCount(-1);
                                            p_BackgroundMusicPlayer.prefetch();
                                            p_BackgroundMusicPlayer.start();
                                        }
                                        catch (MediaException e)
                                        {
                                        }
                                    }
                                }
                                else
                                {
                                    if (p_BackgroundMusicPlayer != null && p_BackgroundMusicPlayer.getState() == Player.STARTED)
                                    {
                                        try
                                        {
                                            p_BackgroundMusicPlayer.stop();
                                        }
                                        catch (MediaException e)
                                        {
                                        }
                                    }
                                }
                            }
                            ;
                            break;
                        case ITEM_IndicatorsITM:
                            {
                                lg_Option_Indicators = alg_flagarray[0];
                            }
                            ;
                            break;
                        case ITEM_ShowTextITM:
                            {
                                lg_Option_Vibration = alg_flagarray[0];
                            }
                            ;
                            break;
                    }
                }
                ;
                break;
            case SCR_OptionsSCR:
                {
                    //_showWait();
                    try
                    {
                        saveOptionsToRMS();
                    }
                    catch (Exception e)
                    {
                    }
                    //_hiddeWait();
                }
                ;
                break;
        }
}

    private Image createThumbnail(Image _srcImage, int _width, int _height)
    {
        int sourceWidth = _srcImage.getWidth();
        int sourceHeight = _srcImage.getHeight();

        int thumbWidth = _width;
        int thumbHeight = _height;

        if (thumbHeight == -1)
            thumbHeight = (thumbWidth * (sourceHeight << 8) / sourceWidth) >> 8;

        if (thumbWidth == -1)
            thumbWidth = (thumbHeight * (sourceWidth << 8) / sourceHeight) >> 8;

        Image thumb = Image.createImage(thumbWidth, thumbHeight);
        Graphics g = thumb.getGraphics();

        int i8_cx = (sourceWidth << 8) / thumbWidth;
        int i8_cy = (sourceHeight << 8) / thumbHeight;

        for (int y = 0; y < thumbHeight; y++)
        {
            for (int x = 0; x < thumbWidth; x++)
            {
                g.setClip(x, y, 1, 1);
                int dx = (x * i8_cx) >> 8;
                int dy = (y * i8_cy) >> 8;
                g.drawImage(_srcImage, x - dx, y - dy, 0);
            }
        }

        return thumb;
}
//--------------------------------------------------------------------
//#global _MENU_ITEM_CUSTOM=false
//#global _MENU_ITEM_DELIMITER=false
//#global _MENU_ITEM_IMAGE=true
//#global _MENU_ITEM_MENUITEM=true
//#global _MENU_ITEM_TEXTBOX=true

// Screens

// Screen OptionsSCR
    private static final int SCR_OptionsSCR = 0;
// Screen AboutSCR
    private static final int SCR_AboutSCR = 28;
// Screen MainSCR
    private static final int SCR_MainSCR = 46;
// Screen LanguageSelectSCR
    private static final int SCR_LanguageSelectSCR = 62;
// Screen HelpSCR
    private static final int SCR_HelpSCR = 70;
// Screen OnOffSCR
    private static final int SCR_OnOffSCR = 83;

// Items

// Item HelpTextITM
    private static final int ITEM_HelpTextITM = 4;
// Item LanguageSelectITM
    private static final int ITEM_LanguageSelectITM = 0;
// Item ShowTextITM
    private static final int ITEM_ShowTextITM = 2;
// Item IndicatorsITM
    private static final int ITEM_IndicatorsITM = 3;
// Item AboutImageITM
    private static final int ITEM_AboutImageITM = 5;
// Item AboutTextITM
    private static final int ITEM_AboutTextITM = 6;
// Item SoundITM
    private static final int ITEM_SoundITM = 1;

// Commands

// Command OptionsCMD
    private static final int COMMAND_OptionsCMD = 91;
// Command ShowCMD
    private static final int COMMAND_ShowCMD = 92;
// Command BackCMD
    private static final int COMMAND_BackCMD = 93;
// Command AboutCMD
    private static final int COMMAND_AboutCMD = 94;
// Command ExitCMD
    private static final int COMMAND_ExitCMD = 95;
// Command HelpCMD
    private static final int COMMAND_HelpCMD = 96;

    private static final int AboutCmdTXT = 0;
    private static final int ShowCmdTXT = 1;
    private static final int OffTXT = 2;
    private static final int HelpCmdTXT = 3;
    private static final int AboutTextTXT = 4;
    private static final int YesCmdTXT = 5;
    private static final int OptionsCmdTXT = 6;
    private static final int SlideListTXT = 7;
    private static final int BackCmdTXT = 8;
    private static final int CantSaveDataTXT = 9;
    private static final int OnTXT = 10;
    private static final int IndicatorsTXT = 11;
    private static final int ShowTextTXT = 12;
    private static final int LanguageSelectTitleTXT = 13;
    private static final int LanguageSelectTXT = 14;
    private static final int CreateCmdTXT = 15;
    private static final int HelpTextTXT = 16;
    private static final int ErrorTXT = 17;
    private static final int ExitCmdTXT = 18;
    private static final int HelpTitleTXT = 19;
    private static final int SoundTXT = 20;
    private static final int WaitPleaseTXT = 21;
    private static final int OpenCmdTXT = 22;
    private static final int AboutTitleTXT = 23;
    private static final int OptionsTitleTXT = 24;
}
