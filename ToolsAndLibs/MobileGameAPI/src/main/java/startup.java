import javax.microedition.lcdui.*;
import javax.microedition.midlet.MIDlet;
import javax.microedition.midlet.MIDletStateChangeException;

public class startup extends MIDlet implements Runnable, IStartupConst
{
    /**
     * Объект использующийся для синхронизации вывода графики
     */
    private static final Object p_SyncObject = new Object();

    /**
     * Хэш значение ожидаемой зоны отрисовки
     */
    private static long l_ZoneWaitForPaint;

    /**
     * Задержка в миллисекундах показа окна нового игрового уровня
     */
    private static final int DELAYFORGAMESTAGESHOW = 3000;

    /**
     * Указатель предельного времени показа окна нового игрового уровня
     */
    private static long l_LimitTimeOfGameStage = -1;

    /**
     * Количество уровней в игре
     */
    private static final int STAGES_NUMBER = 3;

    /**
     * Текущий номер уровня
     */
    private static int i_CurrentStage;

    /**
     * Переменная содержит строку с идентификатором возникшей ошибки 
     */
    private static String s_ErrorMessage = null;

    /**
     * Состояние загрузки  
     */
    public static final int APPSTATE_LOADING = 1;

    /**
     * Состояние в режиме главного меню
     */
    public static final int APPSTATE_MAINMENU = 2;

    /**
     * Состояние в режиме игрового меню
     */
    public static final int APPSTATE_GAMEMENU = 3;

    /**
     * Состояние инициализации игрового процесса
     */
    public static final int APPSTATE_GAMEINITING = 4;

    /**
     * Состояние в режиме отображения информации о новом игровом уровне
     */
    public static final int APPSTATE_SHOWGAMESTAGE = 5;

    /**
     * Состояние в режиме игры
     */
    public static final int APPSTATE_GAMEPLAY = 6;

    /**
     * Состояние в режиме отображения информации о выигрыше игрока
     */
    public static final int APPSTATE_SHOWENDGAME = 7;

    /**
     * Состояние в режиме отображения информации об ошибке
     */
    public static final int APPSTATE_ERROR = 8;

    /**
     * Флаг того, что приложение может управлять приоритетом потока
     */
    private static boolean lg_threadPriorityCanBeModified;

    /**
     * Кнопка вверх
     */
    public static final int BUTTON_UP = 1;

    /**
     * Кнопка вниз
     */
    public static final int BUTTON_DOWN = 2;

    /**
     * Кнопка влево
     */
    public static final int BUTTON_LEFT = 4;

    /**
     * Кнопка вправо
     */
    public static final int BUTTON_RIGHT = 8;

    /**
     * Кнопка SELECT
     */
    public static final int BUTTON_SELECT = 16;

    /**
     * Кнопка A
     */
    public static final int BUTTON_A = 32;

    /**
     * Кнопка B
     */
    public static final int BUTTON_B = 64;

    /**
     * Переменная содержит количество тиков, прошедших от старта приложения
     */
    public static int i_TicksFromStateStart;

    /**
     * Флаг требования перерисовки компонента
     */
    public static boolean lg_Repaint;

    /**
     * Переменная содержит флаги нажатых кнопок
     */
    public static int i_KeyFlagsPressed;

    /**
     * Переменная содержит флаги отпущенных кнопок
     */
    public static int i_KeyFlagsReleased;

    /**
     * Указатель на объект приложения
     */
    public static startup p_This;

    /**
     * Дисплей
     */
    public static Display p_RootScene;

    /**
     * Контекст
     */
    public static Canvas p_MainCanvas;

    /**
     * Неинициализированное состояние
     */
    private static final int XLETSTATE_UNINITED = 0;

    /**
     * Пауза
     */
    private static final int XLETSTATE_PAUSED = 1;

    /**
     * Активизирован
     */
    private static final int XLETSTATE_STARTED = 2;

    /**
     * Статус работы
     */
    private static int i_XletStatus = XLETSTATE_UNINITED;

    /**
     * Состояние приложения
     */
    private static int i_AppState;

    /**
     * Предыдущее состояние приложения
     */
    private static int i_prevAppState;

    /**
     * Объект хранящий изображение "загрузочный логотип"
     */
    private static Image p_loadingLogo;

    /**
     * Объект хранящий изображение "панель уровня"
     */
    private static Image p_levelPanel;

    /**
     * Флаг работы внутреннего цикла обработки состояний
     */
    private static boolean lg_workflag;

    /**
     * Переменная содержит идентификатор события, сгенерированного по нажатию клавиши
     */
    private static String s_menuButtonEvent;

    /**
     * Переменная содержит громкость звукового сопровождения игры (0-выключено)
     */
    public static int i_Options_SoundLevel = 4;

    /**
     * Переменная используется для выбора рабочего значения громкости звука
     */
    private static int i_Options_Tmp_SoundLevel = 0;

    /**
     * Объект, содержащий флаги кнопок управления для передачи в игру
     */
    private static final ControlObject p_GameControl = new ControlObject();

    /**
     * Поток, в котором работает приложение
     */
    private Thread p_MainThread;

    /**
     * Конструктор
     */
    public startup()
    {
        super();
        i_XletStatus = XLETSTATE_UNINITED;
    }

    /**
     * Функция задает новое состояние приложения 
     * @param _newState идентификатор нового состояния
     * @throws Throwable порождается в случае проблем при работе функции
     */
    public final static void setAppState(int _newState) throws Throwable
    {
        i_prevAppState = i_AppState;
        i_AppState = _newState;

        if (_newState != APPSTATE_ERROR)
        {
            try
            {
                //System.out.println("new state "+_newState);

                onAppStateChanged(_newState, i_prevAppState);
            }
            catch (Throwable _thr)
            {
                _thr.printStackTrace();
                s_ErrorMessage = _thr.getMessage();
                i_AppState = APPSTATE_ERROR;
            }
        }

        lg_Repaint = true;
    }


    /**
     * Запуск XLETa или снятие его с паузы
     */
    public void startApp() throws MIDletStateChangeException
    {
        switch (i_XletStatus)
        {
            case XLETSTATE_PAUSED:
            {
                i_XletStatus = XLETSTATE_STARTED;
                if (lg_threadPriorityCanBeModified) p_MainThread.setPriority(Thread.NORM_PRIORITY);
                lg_Repaint = true;
            }
                ;
                break;
            case XLETSTATE_STARTED:
            {
                lg_Repaint = true;
            }
                ;
                break;
            case XLETSTATE_UNINITED:
            {
                p_This = this;
                i_XletStatus = XLETSTATE_STARTED;
                p_MainThread = new Thread(this);

                try
                {
                    p_MainThread.setPriority(Thread.MIN_PRIORITY);
                    lg_threadPriorityCanBeModified = true;
                }
                catch (SecurityException _ex)
                {
                    lg_threadPriorityCanBeModified = false;
                }

                p_MainThread.start();
            };break;
        }
    }

    public void pauseApp()
    {
        switch (i_XletStatus)
        {
            case XLETSTATE_STARTED:
            {
                i_XletStatus = XLETSTATE_PAUSED;
                if (lg_threadPriorityCanBeModified) p_MainThread.setPriority(Thread.MIN_PRIORITY);
            };break;
        }
    }

    public void destroyApp(boolean _b) throws MIDletStateChangeException
    {
        //#-
        System.out.println("Xlet destroyed");
        //#+

        if (i_XletStatus == XLETSTATE_UNINITED) throw new MIDletStateChangeException("Wrong Xlet state for destroy");
        i_XletStatus = XLETSTATE_UNINITED;
        if (lg_threadPriorityCanBeModified) p_MainThread.setPriority(Thread.MAX_PRIORITY);
        p_MainThread = null;
        p_This = null;
        System.gc();
    }

    protected final void processKeyEvent(int _keyCode,boolean _pressedKey)
    {
        if (_pressedKey)
            {
                int i_code = 0;
                switch (_keyCode)
                {
                    case KEY_UP:
                    {
                        i_code |= BUTTON_UP;
                    }
                        ;
                        break;
                    case KEY_DOWN:
                    {
                        i_code |= BUTTON_DOWN;
                    }
                        ;
                        break;
                    case KEY_LEFT:
                    {
                        i_code |= BUTTON_LEFT;
                    }
                        ;
                        break;
                    case KEY_RIGHT:
                    {
                        i_code |= BUTTON_RIGHT;
                    }
                        ;
                        break;
                    case KEY_FIRE:
                    {
                        i_code |= BUTTON_SELECT;
                    }
                        ;
                        break;
                    case KEY_A:
                    {
                        i_code |= BUTTON_A;
                    }
                        ;
                        break;
                    case KEY_B:
                    {
                        i_code |= BUTTON_B;
                    }
                        ;
                        break;
                }

                i_KeyFlagsPressed |= i_code;
                i_KeyFlagsReleased &= ~i_code;
            }
            else
            {
                int i_code = 0;
                switch (_keyCode)
                {
                    case KEY_UP:
                    {
                        i_code |= BUTTON_UP;
                    }
                        ;
                        break;
                    case KEY_DOWN:
                    {
                        i_code |= BUTTON_DOWN;
                    }
                        ;
                        break;
                    case KEY_LEFT:
                    {
                        i_code |= BUTTON_LEFT;
                    }
                        ;
                        break;
                    case KEY_RIGHT:
                    {
                        i_code |= BUTTON_RIGHT;
                    }
                        ;
                        break;
                    case KEY_FIRE:
                    {
                        i_code |= BUTTON_SELECT;
                    }
                        ;
                        break;
                    case KEY_A:
                    {
                        i_code |= BUTTON_A;
                    }
                        ;
                        break;
                    case KEY_B:
                    {
                        i_code |= BUTTON_B;
                    }
                        ;
                        break;
                }

                i_KeyFlagsReleased |= i_code;
            }
    }

    public final synchronized void paint(Graphics _g)
    {
        final int i_cx = _g.getClipX();
        final int i_cy = _g.getClipY();
        final int i_cw = _g.getClipWidth();
        final int i_ch = _g.getClipHeight();

        long l_hash = (i_cx<<48)|(i_cy<<32)|(i_cw<<16)|i_ch;

        //System.out.println("zone painting hash = "+l_hash+" coords = "+i_cx+","+i_cy+","+i_cw+","+i_ch);

        try
        {
            switch (i_AppState)
            {
                case APPSTATE_LOADING:
                {
                    onPaintLoading(_g);
                }
                    ;
                    break;
                case APPSTATE_MAINMENU:
                {
                    onPaintMenu(_g,l_hash==l_ZoneWaitForPaint);
                };break;
                case APPSTATE_GAMEMENU:
                {
                    if (i_cx!=180)
                    {
                        GameView.paint(_g,i_prevAppState==APPSTATE_GAMEPLAY);
                    }
                    onPaintMenu(_g,l_hash==l_ZoneWaitForPaint);
                }
                    ;
                    break;
                case APPSTATE_GAMEPLAY:
                {
                    onPaintGameplay(_g);
                }
                    ;
                    break;
                case APPSTATE_SHOWGAMESTAGE:
                {
                    onPaintGamestage(_g);
                }
                    ;
                    break;
                case APPSTATE_SHOWENDGAME:
                {
                    onPaintEndGame(_g);
                }
                    ;
                    break;
                case APPSTATE_GAMEINITING:
                {
                    onPaintGameIniting(_g);
                }
                ;
                break;
                case APPSTATE_ERROR:
                {
                    _g.setClip(0, 0, SCREEN_WIDTH, SCREEN_HEIGHT);
                    _g.setColor(0xFF0000);
                    _g.fillRect(0, 0, SCREEN_WIDTH, SCREEN_HEIGHT);
                    _g.setColor(0xF0F0F0);
                    _g.setFont(Font.getDefaultFont());
                    int i_y = 0;
                    _g.drawString("ERROR", 100, i_y,Graphics.TOP | Graphics.LEFT);
                    i_y += Font.getDefaultFont().getHeight();
                    if (s_ErrorMessage != null) _g.drawString(s_ErrorMessage, 0, i_y,Graphics.TOP | Graphics.LEFT);
                }
                    ;
                    break;
            }
        }
        catch (Throwable _thr)
        {
            _thr.printStackTrace();
        }
        finally
        {
            if (l_hash==l_ZoneWaitForPaint)
            {
                // ожидаемая зона отрисована
                synchronized (p_SyncObject)
                {
                    l_ZoneWaitForPaint = 0;
                    p_SyncObject.notifyAll();
                }
            }
            else
            {
                // проверяем не включает ли отрисованная зона, зону ожидаемую

                long l_waitingHash = l_ZoneWaitForPaint;
                int i_wx = (short)(l_waitingHash>>>48);
                int i_wy = (short)(l_waitingHash>>>32);
                int i_ww = (short)(l_waitingHash>>>16);
                int i_wh = (short)l_waitingHash;

                int i_cx2 = i_cx+i_cw;
                int i_cy2 = i_cy+i_ch;
                int i_wx2 = i_wx+i_ww;
                int i_wy2 = i_wy+i_wh;

                if (i_wx>=i_cx && i_wy>=i_cy && i_wx2<=i_cx2 && i_wy2<=i_cy2)
                {
                    synchronized (p_SyncObject)
                    {
                        // зона включена
                        l_ZoneWaitForPaint = 0;
                        p_SyncObject.notifyAll();
                    }

                }
            }
        }
    }

    public final void update(Graphics _g)
    {
        //paint(_g);
    }

    public static final void Repaint()
    {
        lg_Repaint = true;
    }

    public final void Sync(int _x,int _y,int _width,int _height) throws Throwable
    {
        long l_hash = (_x<<48)|(_y<<32)|(_width<<16)|_height;

        //System.out.println("wait zone hash="+l_hash);

        while(true)
        {
            l_ZoneWaitForPaint = l_hash;
            synchronized(p_SyncObject)
            {
                p_MainCanvas.repaint(_x,_y,_width,_height);
                p_SyncObject.wait(2000);
                if (l_ZoneWaitForPaint!=l_hash) break;
            }
        }

        //System.out.println("zone hash="+l_hash+" painted");
    }

    public final void run()
    {
        System.out.println("Thread started");

        try
        {
            int i_oldKeyFlagsPressed = 0;

            setAppState(APPSTATE_LOADING);
            Sync(0,0,SCREEN_WIDTH, SCREEN_HEIGHT);

            GameView.initResources(Gamelet.MAINRESOLUTION_WIDTH,Gamelet.MAINRESOLUTION_HEIGHT);
            SoundManager.initBlock(p_This.getClass(),75);

            // инициализация геймлета
            Gamelet.init(p_This.getClass());

            // иницицализация менеджера форм 
            GUIFormManager.init("forms.txt");

            //Thread.sleep(2000);

            setAppState(APPSTATE_MAINMENU);

            lg_workflag = true;

            long l_menuNextAnimationDelay = System.currentTimeMillis() + 1000;

            while (lg_workflag)
            {
                // вычисление размера памяти
                //System.out.println("Memory taked "+((Runtime.getRuntime().totalMemory()-Runtime.getRuntime().freeMemory())/1024)+"kBt");

                Thread.yield();

                boolean lg_keyChanged = (i_oldKeyFlagsPressed != i_KeyFlagsPressed) || ((i_KeyFlagsPressed & i_KeyFlagsReleased) != 0);

                switch (i_AppState)
                {
                    case APPSTATE_GAMEPLAY:
                    {
                        int i_flags = ((i_KeyFlagsPressed & BUTTON_LEFT)!=0 ? p_GameControl.BUTTON_LEFT : 0) | ((i_KeyFlagsPressed & BUTTON_RIGHT)!=0 ? p_GameControl.BUTTON_RIGHT : 0) | ((i_KeyFlagsPressed & BUTTON_SELECT)!=0 || (i_KeyFlagsPressed & BUTTON_UP)!=0 ? p_GameControl.BUTTON_FIRE: 0);
                        p_GameControl.BUTTON_FLAGS = i_flags;

                        if ((i_KeyFlagsPressed & BUTTON_A) != 0)
                        {
                            setAppState(APPSTATE_GAMEMENU);
                        }

                        Repaint();

                    };break;
                    case APPSTATE_SHOWGAMESTAGE:
                    {
                        if ((i_KeyFlagsPressed & BUTTON_A) != 0)
                        {
                            setAppState(APPSTATE_GAMEMENU);
                        }
                    }
                        ;
                        break;
                    default:
                    {
                        if (lg_keyChanged) processServiceButtons(i_KeyFlagsPressed, i_oldKeyFlagsPressed, i_KeyFlagsReleased);
                    }
                }

                // Сбрасываем отпущенные кнопки
                i_KeyFlagsPressed &= ~i_KeyFlagsReleased;
                i_KeyFlagsReleased = 0;

                i_oldKeyFlagsPressed = i_KeyFlagsPressed;

                // сброс флагов служебных кнопок
                i_KeyFlagsPressed &= ~(BUTTON_A | BUTTON_B);

                if (lg_Repaint)
                {
                    lg_Repaint = false;
                    switch (i_AppState)
                    {
                        case APPSTATE_GAMEPLAY:
                        {
                            long l_endTime = System.currentTimeMillis() + Gamelet.TIMEDELAY_ITERATION;

                            GameView.beforeGameIteration();
                            int i_gameState = Gamelet.processIteration(p_GameControl);

                            GameView.afterGameIteration();

                            GameView.prepareDrawingList();

                            while(GameView.i_AreasForDrawingNumber>0)
                            {
                                long l_xywh = GameView.getRepaintArea();
                                int i_x = (int)(l_xywh>>>48);
                                int i_y = (int)((l_xywh>>>32) & 0xFFFFl);
                                int i_w = (int)((l_xywh>>>16) & 0xFFFFl);
                                int i_h = (int)(l_xywh & 0xFFFFl);

                                Sync(i_x, i_y, i_w, i_h);
                            }

                            long l_curTime = System.currentTimeMillis();
                            if (l_curTime<l_endTime)
                            {
                                Thread.sleep(l_endTime-l_curTime);
                            }

                            switch(i_gameState)
                            {
                                case Gamelet.GAMESTATE_PLAYERWIN :
                                {
                                    // игрок выиграл, переводим на следующий уровень если не окончательный
                                    Gamelet.disposeStage();
                                    i_CurrentStage++;
                                    if (i_CurrentStage==STAGES_NUMBER)
                                    {
                                        setAppState(APPSTATE_MAINMENU);
                                    }
                                    else
                                    {
                                        setAppState(APPSTATE_SHOWGAMESTAGE);
                                    }
                                    System.out.println("Player win");
                                };break;
                                case Gamelet.GAMESTATE_PLAYERLOST :
                                {
                                    System.out.println("Player lost");
                                    // игрок проиграл, выходим в меню
                                    setAppState(APPSTATE_MAINMENU);
                                };break;
                            }

                        };break;
                        case APPSTATE_GAMEMENU:
                        case APPSTATE_MAINMENU:
                        {
                            if (i_menuUpdateComponentsNumber > 0)
                            {
                                while (i_menuUpdateComponentsNumber > 0)
                                {
                                    String s_componentName = as_menuUpdateComponents[i_menuUpdateComponentsNumber - 1];

                                    long l_bounds = GUIFormManager.getBoundsOfComponent(s_componentName);
                                    int i_h = (int) (l_bounds & 0xFFFFl);
                                    int i_w = (int) ((l_bounds >>> 16) & 0xFFFFl);
                                    int i_y = (short) (l_bounds >>> 32);
                                    int i_x = (short) (l_bounds >>> 48);

                                    if (GUIFormManager.s_SelectedFormName.equals(FORM_GAMEMENU))
                                        Sync(i_x+180, i_y+190, i_w, i_h);
                                    else
                                        Sync(i_x, i_y, i_w, i_h);

                                    i_menuUpdateComponentsNumber--;
                                }
                            }
                            else
                            {
                                Sync(0, 0, SCREEN_WIDTH, SCREEN_HEIGHT);
                            }
                        }
                            ;
                            break;
                        default:
                        {
                            Sync(0, 0, SCREEN_WIDTH, SCREEN_HEIGHT);
                        }
                    }
                }

                switch (i_AppState)
                {
                    case APPSTATE_SHOWGAMESTAGE:
                    {
                        if (l_LimitTimeOfGameStage <= System.currentTimeMillis())
                        {
                            setAppState(APPSTATE_GAMEPLAY);
                        }
                    };break;
                    case APPSTATE_MAINMENU:
                    case APPSTATE_GAMEMENU:
                    {
                        if (GUIFormManager.i_SelectedFormAnimationDelay > 0)
                        {
                            if (l_menuNextAnimationDelay <= System.currentTimeMillis())
                            {
                                if (processFormAnimation(GUIFormManager.s_SelectedFormName))
                                {
                                    if (i_menuUpdateComponentsNumber > 0)
                                    {
                                        while (i_menuUpdateComponentsNumber > 0)
                                        {
                                            String s_componentName = as_menuUpdateComponents[i_menuUpdateComponentsNumber - 1];

                                            long l_bounds = GUIFormManager.getBoundsOfComponent(s_componentName);
                                            int i_h = (int) (l_bounds & 0xFFFFl);
                                            int i_w = (int) ((l_bounds >>> 16) & 0xFFFFl);
                                            int i_y = (short) (l_bounds >>> 32);
                                            int i_x = (short) (l_bounds >>> 48);

                                            Sync(i_x, i_y, i_w, i_h);

                                            i_menuUpdateComponentsNumber--;
                                        }
                                    }
                                    else
                                    {
                                        Sync(0, 0, SCREEN_WIDTH, SCREEN_HEIGHT);
                                    }
                                }

                                l_menuNextAnimationDelay = System.currentTimeMillis() + GUIFormManager.i_SelectedFormAnimationDelay;
                                // Увеличиваем количество тиков
                                i_TicksFromStateStart++;
                            }

                        }

                        if (s_menuButtonEvent != null)
                        {
                            onMenuButtonClicked(s_menuButtonEvent);
                            s_menuButtonEvent = null;
                        }
                    };break;
                    default:
                    {
                        // Увеличиваем количество тиков
                        i_TicksFromStateStart++;
                    }
                }

            }

            // деинициаизация системы отображения
            GameView.releaseResources();

            // Деинициализация системы звука
            SoundManager.release();

            // освобождаем данные для геймлета
            Gamelet.release();

        }
        catch (Throwable _ex)
        {
            s_ErrorMessage = _ex.getMessage();
            _ex.printStackTrace();

            try
            {
                setAppState(APPSTATE_ERROR);
                Sync(0, 0, 640, 480);
                Thread.sleep(2000);
            }
            catch (Throwable _thr)
            {
            }
        }

        notifyDestroyed();
    }

    //--------------------------------------------------------------
    private static final String[] as_menuUpdateComponents = new String[16];

    private static final String[] as_menuFormsStack = new String[16];

    private static int i_menuUpdateComponentsNumber = 0;

    private static int i_menuFormsStackDepth = 0;

    /**
     * Функция вызывается при смене активной кнопки меню
     * @param _newButtonID идентификатор вновь выбранной кнопки 
     * @param _oldButtonID идентификатор предыдущей кнопки
     * @throws Throwable порождается в случае ошибки при обработки
     */
    private static final void onMenuButtonChanged(String _newButtonID, String _oldButtonID) throws Throwable
    {
        if (GUIFormManager.s_SelectedFormName.equals(FORM_MAIN))
        {
            as_menuUpdateComponents[i_menuUpdateComponentsNumber++] = "mm_cursor";
        }

        if (i_Options_SoundLevel>0)
        {
            SoundManager.playSound(SoundManager.SOUND_BASKET,1);
        }
    }

    /**
     * Функция вызывается при щелчке пользователя на кнопке меню или сервисной кнопке
     * @param _menuButton имя кнопки
     * @throws Throwable порождается в случае проблем при отработке
     */
    private static final void onMenuButtonClicked(String _menuButton) throws Throwable
    {
        System.out.println("clicked "+_menuButton);

        if (i_Options_SoundLevel>0)
        {
            SoundManager.playSound(SoundManager.SOUND_BASKET,1);
        }

        if (_menuButton.equals(MENUBUTTON_RETURNTOGAME))
        {
            setAppState(i_prevAppState);
        }
        else
            if (_menuButton.equals(MENUBUTTON_EXIT2MENU))
            {
                setAppState(APPSTATE_MAINMENU);
            }
            else
        if (_menuButton.equals(MENUBUTTON_START))
        {
            setAppState(APPSTATE_SHOWGAMESTAGE);
        }
        else
        if (_menuButton.equals(MENUBUTTON_HELP))
        {
            selectForm(FORM_HELP, false);
            Repaint();
        }
        else
        if (_menuButton.equals(MENUBUTTON_OPTIONS))
        {
            selectForm(FORM_OPTIONS, false);
            Repaint();
        }
        else
            if (_menuButton.equals(MENUBUTTON_EXIT) || _menuButton.equals(MENUBUTTON_EXITFROMGAME))
            {
                lg_workflag = false;
            }
            else
            if (_menuButton.equals(SPECBUTTON_LEFT))
            {
                if (GUIFormManager.s_SelectedFormName.equals(FORM_OPTIONS))
                {
                    if (i_Options_Tmp_SoundLevel>0) i_Options_Tmp_SoundLevel--;
                    as_menuUpdateComponents[i_menuUpdateComponentsNumber++] = "snd_cursor";
                }
                else
                    if (GUIFormManager.s_SelectedFormName.equals(FORM_HELP))
                    {
                        replaceForm(FORM_ABOUT,false);
                        Repaint();
                    }
                    else
                        if (GUIFormManager.s_SelectedFormName.equals(FORM_CONTROLS))
                        {
                            replaceForm(FORM_HELP,false);
                            Repaint();
                        }
                        else
                            if (GUIFormManager.s_SelectedFormName.equals(FORM_ABOUT))
                            {
                                replaceForm(FORM_CONTROLS,false);
                                Repaint();
                            }
            }
            else
                if (_menuButton.equals(SPECBUTTON_RIGHT))
                {
                    if (GUIFormManager.s_SelectedFormName.equals(FORM_OPTIONS))
                    {
                        if (i_Options_Tmp_SoundLevel<5) i_Options_Tmp_SoundLevel++;
                        as_menuUpdateComponents[i_menuUpdateComponentsNumber++] = "snd_cursor";
                    }
                    else
                    if (GUIFormManager.s_SelectedFormName.equals(FORM_HELP))
                    {
                        replaceForm(FORM_CONTROLS,false);
                        Repaint();
                    }
                    else
                        if (GUIFormManager.s_SelectedFormName.equals(FORM_CONTROLS))
                        {
                            replaceForm(FORM_ABOUT,false);
                            Repaint();
                        }
                        else
                            if (GUIFormManager.s_SelectedFormName.equals(FORM_ABOUT))
                            {
                                replaceForm(FORM_HELP,false);
                                Repaint();
                            }
                }
            if (_menuButton.equals(SPECBUTTON_A))
            {
                if (GUIFormManager.s_SelectedFormName.equals(FORM_OPTIONS))
                {
                    i_Options_SoundLevel = i_Options_Tmp_SoundLevel;
                    if (previousForm(true))
                    {
                        Repaint();
                    }
                }
            }else
                if (_menuButton.equals(SPECBUTTON_C))
                {
                    if (previousForm(true))
                    {
                        Repaint();
                    }
                }
    }

    private static final void selectForm(String _formName, boolean _clearCache) throws Throwable
    {
        onFormSelected(_formName);
        GUIFormManager.selectForm(_formName, _clearCache);
        as_menuFormsStack[i_menuFormsStackDepth++] = _formName;
        i_menuUpdateComponentsNumber = 0;
    }

    private static final void replaceForm(String _formName, boolean _clearCache) throws Throwable
    {
        onFormSelected(_formName);
        GUIFormManager.selectForm(_formName, _clearCache);
        as_menuFormsStack[i_menuFormsStackDepth-1] = _formName;
        i_menuUpdateComponentsNumber = 0;
    }

    private static final boolean previousForm(boolean _clearCache) throws Throwable
    {
        if (i_menuFormsStackDepth > 1)
        {
            i_menuFormsStackDepth--;
            String s_name = as_menuFormsStack[i_menuFormsStackDepth - 1];
            i_menuUpdateComponentsNumber = 0;
            GUIFormManager.selectForm(s_name, _clearCache);
            return true;
        }
        return false;
    }

    /**
     * Отработка анимации компонент текущей формы
     * @return true если требуется перерисовка всей или отдельных компонент формы и false если не требуется
     */
    private static final boolean processFormAnimation(String _formId)
    {
        if (_formId.equals(FORM_MAIN))
        {
            as_menuUpdateComponents[i_menuUpdateComponentsNumber++] = "millarea";
            as_menuUpdateComponents[i_menuUpdateComponentsNumber++] = "mm_cursor";
            return true;
        }
        else
        if (_formId.equals(FORM_OPTIONS))
        {
            as_menuUpdateComponents[i_menuUpdateComponentsNumber++] = "snd_cursor";
            return true;
        }
        else
            if (_formId.equals(FORM_HELP))
            {
                as_menuUpdateComponents[i_menuUpdateComponentsNumber++] = "hlp_bonus";
                as_menuUpdateComponents[i_menuUpdateComponentsNumber++] = "hlp_ball";
                return true;
            }
            else
                if (_formId.equals(FORM_CONTROLS))
                {
                    as_menuUpdateComponents[i_menuUpdateComponentsNumber++] = "cntr_ball";
                    as_menuUpdateComponents[i_menuUpdateComponentsNumber++] = "cntr_wagon";
                    return true;
                }
                else
                    if (_formId.equals(FORM_ABOUT))
                    {
                        as_menuUpdateComponents[i_menuUpdateComponentsNumber++] = "about_ball";
                        return true;
                    }
        return false;
    }

    /**
     * Обработка нажатых кнопок
     * @param _keyPressed флаги нажатых кнопок
     * @param _oldKeyPressed флаги предыдущего состояния кнопок
     * @param _keyReleased флаги отжатых кнопок
     * @throws Throwable прождается если произошло исключение в процессе обработки
     */
    private static final void processServiceButtons(int _keyPressed, int _oldKeyPressed, int _keyReleased) throws Throwable
    {
        switch (i_AppState)
        {
            case APPSTATE_MAINMENU:
            case APPSTATE_GAMEMENU:
            {
                if (GUIFormManager.hasButtons())
                {
                    if (_keyPressed == BUTTON_A && GUIFormManager.s_SelectedFormName.equals(FORM_MAIN))
                    {
                        lg_workflag =false;
                        return;
                    }

                    if ((_keyPressed & _keyReleased & BUTTON_DOWN) != 0)
                    {
                        // выбор следующей кнопки

                        as_menuUpdateComponents[i_menuUpdateComponentsNumber++] = GUIFormManager.getNameOfSelectedButton();
                        String s_oldSelectedButtonName = GUIFormManager.getNameOfSelectedButton();
                        GUIFormManager.selectNextButton();
                        as_menuUpdateComponents[i_menuUpdateComponentsNumber++] = GUIFormManager.getNameOfSelectedButton();
                        onMenuButtonChanged(GUIFormManager.getNameOfSelectedButton(), s_oldSelectedButtonName);
                        Repaint();
                    }
                    else
                        if ((_keyPressed & _keyReleased & BUTTON_UP) != 0)
                        {
                            // выбор предыдущей кнопки

                            as_menuUpdateComponents[i_menuUpdateComponentsNumber++] = GUIFormManager.getNameOfSelectedButton();
                            String s_oldSelectedButtonName = GUIFormManager.getNameOfSelectedButton();
                            GUIFormManager.selectPrevButton();
                            as_menuUpdateComponents[i_menuUpdateComponentsNumber++] = GUIFormManager.getNameOfSelectedButton();
                            onMenuButtonChanged(GUIFormManager.getNameOfSelectedButton(), s_oldSelectedButtonName);
                            Repaint();
                        }
                        else
                        {
                            // проверяем нажатие выбора, так как кнопка "серьезная", то она должна быть нажата одна
                            if (_keyPressed == BUTTON_SELECT)
                            {
                                if ((_oldKeyPressed & BUTTON_SELECT) == 0)
                                {
                                    // кнопка выбора нажата
                                    as_menuUpdateComponents[i_menuUpdateComponentsNumber++] = GUIFormManager.getNameOfSelectedButton();
                                    Repaint();
                                }

                                if ((_keyReleased & BUTTON_SELECT) != 0)
                                {
                                    // конпка отжата
                                    as_menuUpdateComponents[i_menuUpdateComponentsNumber++] = GUIFormManager.getNameOfSelectedButton();
                                    Repaint();
                                    // помещаем имя выбранной кнопки в переменную команды
                                    s_menuButtonEvent = GUIFormManager.getNameOfSelectedButton();
                                }

                            }
                            else
                            {
                                switch (_keyPressed & _keyReleased)
                                {
                                    case BUTTON_A:
                                        s_menuButtonEvent = SPECBUTTON_A;
                                        break;
                                    case BUTTON_B:
                                        s_menuButtonEvent = SPECBUTTON_B;
                                        break;
                                    case BUTTON_LEFT:
                                        s_menuButtonEvent = SPECBUTTON_LEFT;
                                        break;
                                    case BUTTON_RIGHT:
                                        s_menuButtonEvent = SPECBUTTON_RIGHT;
                                        break;
                                }
                            }
                        }
                }
                else
                {
                    // Форма не имеет кнопок, передаем события нажатий спецклавиш напрямую
                    switch(_keyPressed)
                    {
                    case BUTTON_A:
                        s_menuButtonEvent = SPECBUTTON_A;
                        break;
                    case BUTTON_B:
                        s_menuButtonEvent = SPECBUTTON_B;
                        break;
                        default:
                        {
                            switch (_keyPressed & _keyReleased)
                            {
                                case BUTTON_SELECT:
                                    s_menuButtonEvent = SPECBUTTON_SELECT;
                                    break;
                                case BUTTON_UP:
                                    s_menuButtonEvent = SPECBUTTON_UP;
                                    break;
                                case BUTTON_DOWN:
                                    s_menuButtonEvent = SPECBUTTON_DOWN;
                                    break;
                                case BUTTON_LEFT:
                                    s_menuButtonEvent = SPECBUTTON_LEFT;
                                    break;
                                case BUTTON_RIGHT:
                                    s_menuButtonEvent = SPECBUTTON_RIGHT;
                                    break;
                            }
                        }
                    }

                }
            }
                ;
                break;
            case APPSTATE_SHOWENDGAME:
            {

            }
                ;
                break;
            case APPSTATE_LOADING:
            {

            }
                ;
                break;
        }
    }

    /**
     * Возвращает состояние кнопки меню
     * @param _buttonname имя кнопки меню
     * @param _selected флаг, показывающий что кнопка выделена
     * @return константа, отражающая состояние кнопки
     */
    public static final int getButtonState(String _buttonname, boolean _selected)
    {
        String s_formName = GUIFormManager.s_SelectedFormName;
        if (s_formName == null) return 0;

        if (_selected)
        {
            if ((i_KeyFlagsPressed & BUTTON_SELECT) != 0) return GUIFormManager.BUTTONSTATE_PRESSED;
            return GUIFormManager.BUTTONSTATE_SELECTED;
        }
        else
            return GUIFormManager.BUTTONSTATE_NORMAL;
    }

    /**
     * Отрисовка курсора поверх выбранной кнопки
     * @param _g контекст графического устройства
     * @param _x верхняя левая координата X области кнопки
     * @param _y верхняя левая координата Y области кнопки
     * @param _w ширина области кнопки
     * @param _h высота области кнопки
     */
    public static final void drawCursor(Graphics _g, int _x, int _y, int _w, int _h)
    {
        if (GUIFormManager.s_SelectedFormName.equals(FORM_GAMEMENU))
        {
            _g.setColor(0x00CC00);
            _g.drawRect(_x,_y,_w-1,_h-1);
            _g.drawRect(_x+1,_y+1,_w-3,_h-3);
        }
    }

    /**
     * Отрисовка содержимого настраиваемой области 
     * @param _g контекст графического устройства
     * @param _areaName имя области
     * @param _x верхняя левая координата X области
     * @param _y верхняя левая координата Y области
     * @param _w ширина области
     * @param _h высота области
     */
    public static final void drawCustomArea(Graphics _g, String _areaName, int _x, int _y, int _w, int _h)
    {
        if (_areaName.equals("millarea"))
        {
            int i_cadr = i_TicksFromStateStart % 3;
            GUIFormManager.drawImageFromCache(_g,"mill"+i_cadr,_x,_y);
        }
        else
        if (_areaName.equals("mm_cursor"))
        {
            int i_cadr = i_TicksFromStateStart % 3;
            String s_selectedButton = GUIFormManager.getNameOfSelectedButton();

            int i_y = _y;
            if (s_selectedButton.equals("btnstart")) i_y=_y;
            else
            if (s_selectedButton.equals("btnoptions")) i_y=186;
            else
            if (s_selectedButton.equals("btnhelp")) i_y=262;
            else
                if (s_selectedButton.equals("btnexit")) i_y=342;

            GUIFormManager.drawImageFromCache(_g,"ball"+i_cadr,_x,i_y);
        }
        else
            if (_areaName.equals("snd_cursor"))
            {
                int i_cadr = i_TicksFromStateStart % 3;
                int i_x = _x+33*i_Options_Tmp_SoundLevel;
                GUIFormManager.drawImageFromCache(_g,"ball"+i_cadr,i_x,_y);
            }
            else
                if (_areaName.equals("hlp_bonus"))
                {
                    int i_cadr = i_TicksFromStateStart % 3;
                    GUIFormManager.drawImageFromCache(_g,"bonus"+i_cadr,_x,_y);
                }
                else
                    if (_areaName.equals("hlp_ball") || _areaName.equals("cntr_ball") || _areaName.equals("about_ball"))
                    {
                        int i_cadr = i_TicksFromStateStart % 3;
                        GUIFormManager.drawImageFromCache(_g,"ball"+i_cadr,_x,_y);
                    }
                    else
                        if (_areaName.equals("cntr_wagon"))
                        {
                            int i_cadr = i_TicksFromStateStart % 4;
                            GUIFormManager.drawImageFromCache(_g,"wagon"+i_cadr,_x,_y);
                        }
                        else
                            if (_areaName.equals("hlptxt"))
                            {
                            }
    }

    /**
     * Функция вызывается при смене состояния приложения
     * @param _newState новое состояние приложения
     * @param _oldState старое состояние приложения
     * @throws Throwable порождается в случае исключительной ситуации
     */
    private static final void onAppStateChanged(int _newState, int _oldState) throws Throwable
    {
        if (_newState == _oldState) return;
        i_TicksFromStateStart = 0;
        switch (_newState)
        {
            case APPSTATE_SHOWGAMESTAGE :
            {
                // грузим панель отображающую номер уровня
                if (p_levelPanel==null) p_levelPanel = Image.createImage("/lvlpanel.png");

                if (_oldState==APPSTATE_MAINMENU)
                {
                    i_CurrentStage = 0;
                    Gamelet.initSession(p_This.getClass(),0);
                    Gamelet.initStage(p_This.getClass(),i_CurrentStage);
                    GameView.afterInitGameStage();
                    GameView.showNotify();
                }

                if (_oldState!=APPSTATE_GAMEMENU)
                {
                    l_LimitTimeOfGameStage = System.currentTimeMillis()+DELAYFORGAMESTAGESHOW;
                    if (_oldState==APPSTATE_GAMEPLAY)
                    {
                        // инициализируем новый уровень
                        Gamelet.initStage(p_This.getClass(),i_CurrentStage);
                        GameView.afterInitGameStage();
                        GameView.showNotify();
                    }
                }

            };break;
            case APPSTATE_GAMEPLAY:
            {
                if (p_levelPanel!=null) p_levelPanel = null;
                GUIFormManager.clearResourceCache(true);
                GameView.showNotify();
            };break;
            case APPSTATE_GAMEMENU:
            {
                i_menuUpdateComponentsNumber = 0;
                i_menuFormsStackDepth = 0;
                selectForm(FORM_GAMEMENU,true);
            };break;
            case APPSTATE_MAINMENU:
            {
                if (_oldState ==APPSTATE_GAMEPLAY || _oldState ==APPSTATE_GAMEMENU)
                {
                   Gamelet.disposeStage();
                   Gamelet.disposeSession();
                   //ImageManager.clearDynamicCahce();
                }

                if (p_loadingLogo != null) p_loadingLogo = null;
                if (p_levelPanel != null) p_levelPanel = null;
                i_menuUpdateComponentsNumber = 0;
                i_menuFormsStackDepth = 0;
                selectForm(FORM_MAIN, true);
            }
                ;
                break;
            case APPSTATE_LOADING:
            {
                if (p_loadingLogo == null)
                {
                    p_loadingLogo = Image.createImage(LOADING_LOGO);
                }
            }
                break;
        }
    }

    private static final void onPaintGameIniting(Graphics _g)
    {

    }

    private static final void onPaintEndGame(Graphics _g)
    {

    }

    private static final void onPaintGamestage(Graphics _g) throws Throwable
    {
        GameView.paint(_g,false);
    }

    private static final void onPaintGameplay(Graphics _g) throws Throwable
    {
        GameView.paint(_g,true);
    }

    private static final void onPaintMenu(Graphics _g,boolean _updateFormComponent) throws Throwable
    {
        if (_updateFormComponent && i_menuUpdateComponentsNumber > 0)
        {
                // обновляем самый верхний в стеке элемент
                String s_elementName = as_menuUpdateComponents[i_menuUpdateComponentsNumber - 1];
                if (GUIFormManager.s_SelectedFormName.equals(FORM_GAMEMENU))
                {
                    GUIFormManager.redrawComponent(_g, s_elementName, 180, 190);
                }
                else
                {
                    GUIFormManager.redrawComponent(_g, s_elementName, 0, 0);
                }
        }
        else
        {
            // обновляются все элементы
            if (GUIFormManager.s_SelectedFormName.equals(FORM_GAMEMENU))
            {
                GUIFormManager.drawCurrentForm(_g, 180, 190);
            }
            else
            {
                GUIFormManager.drawCurrentForm(_g, 0, 0);
            }
        }
    }

    private static final void onFormSelected(String _formName)
    {
        if (_formName.equals(FORM_OPTIONS))
        {
            i_Options_Tmp_SoundLevel = i_Options_SoundLevel;
        }
    }

    private static final void onPaintLoading(Graphics _g)
    {
        _g.setClip(0, 0, SCREEN_WIDTH, SCREEN_HEIGHT);
        _g.setColor(BACKGROUND_COLOR);
        _g.fillRect(0, 0, SCREEN_WIDTH, SCREEN_HEIGHT);

        if (p_loadingLogo != null)
        {
            int i_w = p_loadingLogo.getWidth();
            int i_h = p_loadingLogo.getHeight();

            int i_xoff = (SCREEN_WIDTH - i_w) >> 1;
            int i_yoff = (SCREEN_HEIGHT - i_h) >> 1;

            _g.drawImage(p_loadingLogo, i_xoff, i_yoff, 0);
        }
    }

}
