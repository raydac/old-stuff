import javax.microedition.lcdui.*;

public class InsideCanvas extends javax.microedition.lcdui.Canvas implements Runnable, CommandListener
{
    public int i_screenWidth;
    public int i_screenHeight;
    protected int i_BackgroundColor;

    protected final Font p_MessageFont = Font.getFont(Font.FACE_PROPORTIONAL, Font.STYLE_BOLD, Font.SIZE_MEDIUM);
    protected int i_FontColor;

    private static final int COLOR_LOADING_SCREEN = 0xFFFFFF;
    private static final int COLOR_LOADING_BAR_BACKGROUND = 0xAAAAFF;
    private static final int COLOR_LOADING_BAR = 0x0000FF;
    private static final int COLOR_WAITING = 0x000063;

    private static final int LOADING_BAR_HEIGHT = 5;
    private static final int LOADING_BAR_WIDTH = 60;
    private static final int LOADING_BAR_OFFSET_FROM_SPLASH = 3;
    private static final int I8_LOADING_BAR_PERCENT = (LOADING_BAR_WIDTH << 8) / 100;

    protected int i_ClockFrameCounter;
    public Image p_clockImage;

    protected boolean lg_started = true;

    private static boolean lg_firstPaint = true;

    public void commandAction(Command command, Displayable displayable)
    {
        if (Main.i_ProcessCommand != Main.COMMAND_NONE) return;
        if (Main.p_Command_Exit.equals(command))
        {
            // Выход из игры
            Main.i_ArgScreenID = 0;
            Main.i_ArgCommandID = 1;
            Main.i_ArgSelectedID = 0;
            Main.i_ProcessCommand = Main.COMMAND_COMMAND;
        }
        else if (Main.p_Command_Restart.equals(command))
        {
            // Выход из игры
            Main.i_ArgScreenID = 0;
            Main.i_ArgCommandID = 0;
            Main.i_ArgSelectedID = 0;
            Main.i_ProcessCommand = Main.COMMAND_COMMAND;
        }
    }

    public void run()
    {
        while (lg_started)
        {
            try
            {
                Thread.sleep(200);
            }
            catch (Throwable e)
            {
                break;
            }

            i_ClockFrameCounter++;

            Image p_clIm=p_clockImage;
            if (p_clIm!=null)
            {
                int i_frNumber = p_clIm.getWidth() / p_clIm.getHeight();

                if (i_ClockFrameCounter >= i_frNumber)
                {
                    i_ClockFrameCounter = 0;
                }
            }
            else
                i_ClockFrameCounter = 0;

            if (Main.i_CurrentMidletState == Main.STATE_WAITING)
                this.repaint();
        }
    }

    public void showNotify()
    {
        lg_firstPaint = true;
        repaint();
    }

    //#if FCNOKIA
    public InsideCanvas(int _backgroundColor, int _messageTextColor, Image _clockImage,int _width,int _height)
    {
        super();

        i_screenWidth = _width;
        i_screenHeight = _height;

        i_BackgroundColor = _backgroundColor;
        i_FontColor = _messageTextColor;

        p_clockImage = _clockImage;
        i_ClockFrameCounter = 0;

        new Thread(this).start();
    }
    //#endif

    //#if FCMIDP20
    public void setWidthHeight(int _width,int _height)
    {
        i_screenWidth = _width;
        if (_width==128 && _height<116) _height= 116;
        else
        if (_width==176 && _height<204) _height= 204;
        i_screenWidth = _width;
        i_screenHeight = _height;
    }
    //#endif

    public InsideCanvas(int _backgroundColor, int _messageTextColor, Image _clockImage)
    {
        i_screenWidth = getWidth();
        i_screenHeight = getHeight();

        i_BackgroundColor = _backgroundColor;
        i_FontColor = _messageTextColor;

        p_clockImage = _clockImage;
        i_ClockFrameCounter = 0;

        new Thread(this).start();
    }

    private void drawMessageOnCenterScreen(Graphics _g, String _message)
    {
        Font p_msF = p_MessageFont;
        int i_stringWidth = p_msF.stringWidth(_message);
        int i_height = p_msF.getHeight();

        _g.setColor(i_FontColor);
        _g.setFont(p_msF);
        _g.drawString(_message, (i_screenWidth - i_stringWidth) >> 1, (i_screenHeight - i_height) >> 1, 0);
    }

    public void keyPressed(int _code)
    {
        switch (Main.i_CurrentMidletState)
        {
            case Main.STATE_INITING:
            {

            }
            ;
            break;
            case Main.STATE_LOADING:
            {
            }
            ;
            break;
            case Main.STATE_SPLASH:
            {
                synchronized (Main.p_synchroObject)
                {
                    Main.p_synchroObject.notify();
                }
            }
            ;
            break;
            case Main.STATE_WORKING:
            {
                App.pressCanvasKey(getGameAction(_code));
            }
            ;
            break;
            case Main.STATE_WAITING:
            {
            }
            ;
            break;
            case Main.STATE_RELEASING:
            {
            }
            ;
            break;
        }
    }

    public void keyReleased(int _code)
    {
        switch (Main.i_CurrentMidletState)
        {
            case Main.STATE_INITING:
            {
            }
            ;
            break;
            case Main.STATE_LOADING:
            {
            }
            ;
            break;
            case Main.STATE_WORKING:
            {
                App.releaseCanvasKey(getGameAction(_code));
            }
            ;
            break;
            case Main.STATE_WAITING:
            {
            }
            ;
            break;
            case Main.STATE_RELEASING:
            {
            }
            ;
            break;
        }
    }

    public void paint(Graphics _graphics)
    {
        int i_scrH = i_screenHeight;
        int i_scrW = i_screenWidth;

        int i_bckgColor = i_BackgroundColor;
        Image p_clockIm = p_clockImage;

        _graphics.setClip(0, 0, i_scrW, i_scrH);

        switch (Main.i_CurrentMidletState)
        {
            case Main.STATE_INITING:
            {
                _graphics.setColor(i_bckgColor);
                _graphics.fillRect(0, 0, i_scrW, i_scrH);
                drawMessageOnCenterScreen(_graphics, "Initing...");
            }
            ;
            break;
            case Main.STATE_LOADING:
            {
                _graphics.setColor(COLOR_LOADING_SCREEN);
                _graphics.fillRect(0, 0, i_scrW, i_scrH);

                int i_splImgX;
                int i_splImgY;

                if (Main.p_loadingLogo != null)
                {
                    i_splImgX = (i_scrW- Main.p_loadingLogo.getWidth()) >> 1;
                    i_splImgY = ((i_scrH + LOADING_BAR_HEIGHT + LOADING_BAR_OFFSET_FROM_SPLASH) - Main.p_loadingLogo.getHeight()) >> 1;
                    _graphics.drawImage(Main.p_loadingLogo, i_splImgX, i_splImgY, 0);

                    i_splImgY += Main.p_loadingLogo.getHeight() + LOADING_BAR_OFFSET_FROM_SPLASH;
                }
                else
                {
                    i_splImgY = i_scrH >> 1;
                }
                i_splImgY = i_splImgY > i_scrH - LOADING_BAR_HEIGHT - 1 ? i_scrH - LOADING_BAR_HEIGHT - 1 : i_splImgY;

                i_splImgX = (i_scrW - LOADING_BAR_WIDTH) >> 1;
                _graphics.setColor(COLOR_LOADING_BAR_BACKGROUND);
                _graphics.fillRect(i_splImgX, i_splImgY, LOADING_BAR_WIDTH, LOADING_BAR_HEIGHT);
                _graphics.setColor(COLOR_LOADING_BAR);
                _graphics.fillRect(i_splImgX, i_splImgY, (Main.i_loadingProgress * I8_LOADING_BAR_PERCENT) >> 8, LOADING_BAR_HEIGHT);
            }
            ;
            break;
            case Main.STATE_SPLASH:
            {
                _graphics.setColor(0xFFFFFF);
                _graphics.fillRect(0, 0, i_scrW, i_scrH);

                if (Main.p_splashLogo != null)
                {
                    int i_offsetX = (i_scrW - Main.p_splashLogo.getWidth()) / 2;
                    int i_offsetY = (i_scrH - Main.p_splashLogo.getHeight()) / 2;
                    _graphics.drawImage(Main.p_splashLogo, i_offsetX, i_offsetY, 0);
                }
            }
            ;
            break;
            case Main.STATE_WORKING:
            {
                App.paint(_graphics, i_scrW, i_scrH);
            }
            ;
            break;
            case Main.STATE_WAITING:
            {
                _graphics.setColor(COLOR_WAITING);
                _graphics.fillRect(0, 0, i_scrW, i_scrH);
                _graphics.setFont(p_MessageFont);
                String s_str = Main.getStringForIndex(App.WaitPleaseTXT);
                int i_strW = p_MessageFont.stringWidth(s_str);
                int i_strH = p_MessageFont.getHeight();

                int i_coordY;
                int i_coordX;

                if (p_clockIm != null)
                {
                    int i_frNumber = p_clockIm.getWidth() / p_clockIm.getHeight();
                    int i_frameWidth = p_clockIm.getWidth() / i_frNumber;
                    int i_frameHeight = p_clockIm.getHeight();

                    i_coordX = (i_scrW - i_frameWidth) >> 1;
                    i_coordY = (i_scrH - i_frameHeight) >> 1;

                    _graphics.setClip(i_coordX, i_coordY, i_frameWidth, i_frameHeight);
                    _graphics.drawImage(p_clockIm, i_coordX - i_frameWidth * i_ClockFrameCounter, i_coordY, 0);
                    _graphics.setClip(0, 0, i_scrW, i_scrH);
                }
                else
                {
                    i_coordY = (i_scrH - i_strH) >> 1;
                }
                i_coordX = (i_scrW - i_strW) >> 1;
                _graphics.setColor(i_FontColor);
                _graphics.drawString(s_str, i_coordX, i_coordY - 2, Graphics.LEFT | Graphics.BOTTOM);
            }
            ;
            break;
            case Main.STATE_RELEASING:
            {
                _graphics.setColor(i_bckgColor);
                _graphics.fillRect(0, 0, i_scrW, i_scrH);
                drawMessageOnCenterScreen(_graphics, "Releasing...");
            }
            ;
            break;
        }

        //#if SHOWSYS
        _graphics.setClip(0, 0, i_scrW, i_scrH);
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

        // Выводим сообщение исключения если есть
        if (Main.s_CurrentExceptionMessage != null)
        {
            s_out = Main.s_CurrentExceptionMessage;
            _graphics.setColor(0);
            _graphics.drawString(s_out, 1, i_y + 1, GTL);
            _graphics.setColor(0xFF0000);
            _graphics.drawString(s_out, 0, i_y, GTL);
            i_y += p_fnt.getHeight();
        }
        //#endif

    }
}
