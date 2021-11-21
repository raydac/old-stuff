import java.util.Random;
import java.io.*;

/**
 * Gamelet v1.01 (13.09.2005)
 */
public class Gamelet
{
    /**
     * Количество уровней игры
     */
    public static final int MAX_STAGE_NUMBER = 4;

    //уровни сложности игры
    public static final int GAMELEVEL_EASY = 0;
    public static final int GAMELEVEL_NORMAL = 1;
    public static final int GAMELEVEL_HARD = 2;

    public static final int I_TIMEDELAY = 80;

    /**
     * Генератор случайных чисел
     */
    private static final Random p_RNDGenerator = new Random(System.currentTimeMillis());

    //--------------- состояния игрока -------------------------
    /**
     * Константа, показывающая что игрок в игре
     */
    public static final int PLAYER_PLAYING = 0;
    /**
     * Константа, показывающая что игрок выиграл
     */
    public static final int PLAYER_WIN = 1;
    /**
     * Константа, показывающая что игрок проиграл
     */
    public static final int PLAYER_LOST = 2;

    //--------------- состояния игры -------------------------
    /**
     * Константа, показывающая игра в неинициализированном состоянии или деинициализирована
     */
    public static final int STATE_UNKNON = 0;
    /**
     * Константа, показывающая игра в инициализированном состоянии
     */
    public static final int STATE_INITED = 1;
    /**
     * Константа, показывающая игра в запущенном состоянии
     */
    public static final int STATE_STARTED = 2;
    /**
     * Константа, показывающая игра в приостановленном состоянии
     */
    public static final int STATE_PAUSED = 3;
    /**
     * Константа, показывающая игра в законченном состоянии
     */
    public static final int STATE_OVER = 4;

    //----------------------------------------------------
    //-------------- переменные --------------------------
    //----------------------------------------------------

    /**
     * Переменная содержит состояние игры
     */
    public static int i_GameState = STATE_UNKNON;
    /**
     * Переменная содержит предыдущее состояние игры
     */
    public static int i_PrevGameState = STATE_UNKNON;
    /**
     * Переменная содержит текущий уровень игры
     */
    public static int i_GameLevel;
    /**
     * Переменная содержит текущую фазу игры
     */
    public static int i_GameStage;
    /**
     * Переменная содержит состояние игрока
     */
    public static int i_PlayerState;
    /**
     * Переменная содержит очки игрока
     */
    private static int i_PlayerScore;

    /**
     * Переменная содержит количество пикселей по ширине игровой зоны
     */
    protected static int i_ScreenWidth;
    /**
     * Переменная содержит количество пикселей по высоте игровой зоны
     */
    protected static int i_ScreenHeight;

    /**
     * Переменная содержит количество игровых попыток играющего
     */
    public static int i_PlayerAttemptions;

    /**
     * Переменная содержит максимальное количество запасных игровых попыток играющего
     */
    public static int i_MaxPlayerAttemptions;

    /**
     * Функция отвечающая за инициализацию блока, до её вызова любые операции с блоком запрещены. Переводит блок в состояние INITED..
     *
     * @return true если инициализация прошла успешно, иначе false.
     */
    public static final boolean init(Class _class)
    {
        if (i_GameState != STATE_UNKNON) return false;
        i_ScreenHeight = -1;
        i_ScreenWidth = -1;
        //------------Вставьте свой код здесь--------------------
        PathController.SCALE_WIDTH = startup.SCALE_WIDTH;
        PathController.SCALE_HEIGHT = startup.SCALE_HEIGHT;

        try
        {
            ash_SpritesTable = loadSpriteArray(_class, "/spr.bin");
            ash_Paths = (short[])startup.loadArray(_class, "/paths.bin");
        }
        catch (Exception e)
        {
            return false;
        }

        //--------------------------------------------------
        setState(STATE_INITED);
        return true;
    }

    /**
     * Функция отвечающая за деинициализацию блока, после её вызова любые операции с блоком запрещены
     */
    public static final void release()
    {
        if (i_GameState == STATE_UNKNON) return;
        //------------Вставьте свой код здесь--------------------
        //ash_Paths = null;
        ash_SpritesTable = null;
        //--------------------------------------------------
        i_ScreenHeight = -1;
        i_ScreenWidth = -1;

        setState(STATE_UNKNON);
    }

    /**
     * Функция устанавливает состояние игры.
     *
     * @param _state новое состояние игры.
     */
    private static final void setState(int _state)
    {
        i_PrevGameState = i_GameState;
        i_GameState = _state;
    }

    /**
     * Функция генерирует и возвращает псевдослучайное числовое значение в заданном пределе (включительно).
     *
     * @param _limit предел генерируемого числового значения (включительно)
     * @return сгенерированное псевдослучайное значение в заданном пределе.
     */
    private static final int getRandomInt(int _limit)
    {
        _limit++;
        _limit = (int) (((long) Math.abs(p_RNDGenerator.nextInt()) * (long) _limit) >>> 31);
        return _limit;
    }

    /**
     * Инициализация новой игровой сессии
     *
     * @param _gameAreaWidth  величина ширины игровой зоны в пикселях
     * @param _gameAreaHeight величина высоты игровой зоны в пикселях
     * @param _gameLevel      сложность игровой сессии
     * @return true если инициализация игровой сессии прошла успешно, иначе false.
     */
    public static final boolean initNewGame(final int _gameAreaWidth, final int _gameAreaHeight, final int _gameLevel)
    {
        if (i_GameState != STATE_INITED) return false;
        i_ScreenHeight = _gameAreaHeight;
        i_ScreenWidth = _gameAreaWidth;
        i_GameLevel = _gameLevel;
        i_GameStage = 0;

        //------------Вставьте свой код здесь--------------------
        switch (_gameLevel)
        {
            case GAMELEVEL_EASY:
            {
                i_PlayerAttemptions = ATTEMPTIONS_EASY;
            }
            ;
            break;
            case GAMELEVEL_NORMAL:
            {
                i_PlayerAttemptions = ATTEMPTIONS_NORMAL;
            }
            ;
            break;
            case GAMELEVEL_HARD:
            {
                i_PlayerAttemptions = ATTEMPTIONS_HARD;
            }
            ;
            break;
            default:
                return false;
        }

        i_PlayerScore = 0;

        ap_SpriteCollections = new SpriteCollection[1];
        ap_SpriteCollections[0] = new SpriteCollection(0,3, ash_SpritesTable);

        ap_Paths = new PathController[MAXPATHS];
        for (int li = 0; li < MAXPATHS; li++)
        {
            ap_Paths[li] = new PathController();
        }

        //--------------------------------------------------
        initPlayerForGame(true);
        setState(STATE_STARTED);
        return true;
    }

    /**
     * Деинициализация игровой сессии
     */
    public static final void releaseGame()
    {
        if (i_GameState == STATE_INITED || i_GameState == STATE_UNKNON) return;
        i_ScreenHeight = -1;
        i_ScreenWidth = -1;

        //------------Вставьте свой код здесь--------------------

        //--------------------------------------------------

        setState(STATE_INITED);
    }

    /**
     * Постановка игрового процесса на паузу.
     */
    public static final void pauseGame()
    {
        if (i_GameState == STATE_STARTED)
        {
            setState(STATE_PAUSED);
            //------------Вставьте свой код здесь--------------------

            //--------------------------------------------------
        }
        else
            return;
    }

    /**
     * Продолжение игрового процесса после постановки на паузу
     */
    public static final void resumeGameAfterPause()
    {
        if (i_GameState == STATE_PAUSED || i_GameState == STATE_OVER)
        {
            setState(STATE_STARTED);
            //------------Вставьте свой код здесь--------------------

            //--------------------------------------------------
        }
        else
            return;
    }

    /**
     * Возвращает количество очков игрока по окончании игровой сессии
     *
     * @return значение количества очков игрока.
     */
    public static final int getPlayerScore()
    {
        return i_PlayerScore * (i_GameLevel + 1);
    }

    /**
     * Инициализация игрока после смерти или при инициализации игровой сессии
     *
     * @param _initGame если true то инициализация игры, если false то после смерти игрока
     */
    private static final void initPlayerForGame(boolean _initGame)
    {
        //------------Вставьте свой код здесь--------------------
        startup.i_KeyFlags = 0;
        //--------------------------------------------------
        i_PlayerState = PLAYER_PLAYING;
    }

    /**
     * Инициализация игрового уровня
     *
     * @param _stage ID игрового уровня
     * @return true если инициализация прошла удачно иначе false
     */
    public static final boolean initGameStage(Class _class, int _stage)
    {
        i_GameStage = _stage;
        setState(STATE_STARTED);
        //------------Вставьте свой код здесь--------------------
        i_CurrentPlayerCard = 0;
        i_CurrentOpponentCard = 0;
        i_OpponentCardsPosition = 18;
        i_PlayerCardsPosition = 18;
        i_PlayerHeapCardsNumber = 0;
        i_OpponentHeapCardsNumber = 0;

        generateColodes();

        ap_SpriteCollections[0].releaseAllSprites();
        deactivateAllPaths();

        setSubmode(SUBMODE_WAITOPPONENTMOVE);
        //--------------------------------------------------
        initPlayerForGame(false);
        return true;
    }


    /**
     * Загрузка игрового состояния из массива байт
     *
     * @param _data массив байт, описывающих состояние
     * @throws Exception если произошла ошибка при загрузке состояния или игра находилась в состоянии несовместимом с загрузкой.
     */
    public static final void loadGameStateFromByteArray(Class _class, final byte[] _data) throws Exception
    {
        if (i_GameState != STATE_INITED) throw new Exception();
        ByteArrayInputStream p_arrayInputStream = new ByteArrayInputStream(_data);
        DataInputStream p_dis = new DataInputStream(p_arrayInputStream);
        p_dis.readLong();
        p_dis.readLong();
        int i_gLevel = p_dis.readUnsignedByte();
        int i_gStage = p_dis.readUnsignedByte();
        int i_gScreenWidth = p_dis.readUnsignedShort();
        int i_gScreenHeight = p_dis.readUnsignedShort();
        if (!initNewGame(i_gScreenWidth, i_gScreenHeight, i_gLevel)) throw new Exception();
        if (!initGameStage(_class, i_gStage)) throw new Exception();
        i_PlayerAttemptions = p_dis.readByte();
        i_MaxPlayerAttemptions = p_dis.readByte();
        i_PlayerScore = p_dis.readInt();

        //------------Вставьте свой код здесь--------------------

        i_CurrentGameSubmode = p_dis.readInt();
        i_CurrentOpponentCard = p_dis.readInt();
        i_CurrentPlayerCard = p_dis.readInt();
        i_CurrentSubmodeTimer = p_dis.readInt();
        i_OpponentCardsPosition = p_dis.readInt();
        i_OpponentHeapCardsNumber = p_dis.readInt();
        i_PlayerCardsPosition = p_dis.readInt();
        i_PlayerHeapCardsNumber = p_dis.readInt();
        p_dis.read(ab_OpponentColoda);
        p_dis.read(ab_PlayerColoda);

        for (int li = 0; li < ap_SpriteCollections.length; li++) ap_SpriteCollections[li].loadFromStream(p_dis);
        for (int li = 0; li < ap_Paths.length; li++) ap_Paths[li].readPathFromStream(p_dis, ap_SpriteCollections,ash_Paths);

        //--------------------------------------------------

        p_dis.close();
        Runtime.getRuntime().gc();
    }

    /**
     * Функция формирует блок данных, описывающий текущее игровое состояние
     *
     * @return байтовый массив, содержащий записанное состояние игрового процесса
     * @throws Exception если игра или игрок находится в несовместимом состоянии, произошла ошибка сохранения или сформированный массив неправильного размера
     */
    public static final byte[] saveGameStateToByteArray() throws Exception
    {
        if (i_GameState != STATE_STARTED && i_PlayerState != PLAYER_PLAYING) throw new Exception();
        Runtime.getRuntime().gc();
        ByteArrayOutputStream p_arrayOutputStream = new ByteArrayOutputStream(getGameStateDataBlockSize());
        DataOutputStream p_dos = new DataOutputStream(p_arrayOutputStream);
        p_dos.writeLong(0);// специальные значения
        p_dos.writeLong(0);//...
        p_dos.writeByte(i_GameLevel);
        p_dos.writeByte(i_GameStage);
        p_dos.writeShort(i_ScreenWidth);
        p_dos.writeShort(i_ScreenHeight);
        p_dos.writeByte(i_PlayerAttemptions);
        p_dos.writeByte(i_MaxPlayerAttemptions);
        p_dos.writeInt(i_PlayerScore);
        //------------Вставьте свой код здесь--------------------

        p_dos.writeInt(i_CurrentGameSubmode);
        p_dos.writeInt(i_CurrentOpponentCard);
        p_dos.writeInt(i_CurrentPlayerCard);
        p_dos.writeInt(i_CurrentSubmodeTimer);
        p_dos.writeInt(i_OpponentCardsPosition);
        p_dos.writeInt(i_OpponentHeapCardsNumber);
        p_dos.writeInt(i_PlayerCardsPosition);
        p_dos.writeInt(i_PlayerHeapCardsNumber);
        p_dos.write(ab_OpponentColoda);
        p_dos.write(ab_PlayerColoda);

        for (int li = 0; li < ap_SpriteCollections.length; li++) ap_SpriteCollections[li].saveToStream(p_dos);
        for (int li = 0; li < ap_Paths.length; li++) ap_Paths[li].writePathToStream(p_dos);

        //--------------------------------------------------
        p_dos.flush();
        p_dos.close();
        byte[] ab_result = p_arrayOutputStream.toByteArray();

        //#-
        System.out.println("Saved data length = " + ab_result.length);
        System.out.println("Needed data length = " + getGameStateDataBlockSize());
        //#+

        if (ab_result.length != getGameStateDataBlockSize()) throw new Exception();
        Runtime.getRuntime().gc();
        return ab_result;
    }

    /**
     * Функция возвращает размер, требуемый для сохранения блока игровых данных.
     *
     * @return требуемый размер блока данных.
     */
    public static final int getGameStateDataBlockSize()
    {
        int i_Size = 28;
        //------------Вставьте свой код здесь--------------------
        i_Size += 32;
        i_Size += 36;
        i_Size += SpriteCollection.getDataSize(3);
        i_Size += (MAXPATHS * PathController.DATASIZE_BYTES);
        //--------------------------------------------------
        return i_Size;
    }

    /**
     * Возвращает текстовый идентификатор игры
     *
     * @return строка, идентифицирующая игру.
     */
    public static final String getID()
    {
        return "SIM_TPLR";
    }

    /**
     * Функция отката назад к состоянию старта уровня
     */
    public static final void rollbackStage()
    {
    }

    /**
     * Функция, получает сигнал об окончании обработки анимации спрайта
     *
     * @param _collection   коллекция
     * @param _spriteOffset смещение спрайта в коллекции
     */
    public static final void notifySpriteAnimationCompleted(SpriteCollection _collection, int _spriteOffset)
    {
    }

    /**
     * Уведомление о прохождении спрайтом определенной точки пути
     *
     * @param _controller
     */
    public static final void notifyPathPointPassed(PathController _controller)
    {
    }

    /**
     * Уведомление об окончании прохождения пути спрайтом
     *
     * @param _controller
     */
    public static final void notifyPathCompleted(PathController _controller)
    {
        switch(_controller.i_PathControllerID)
        {
            case PATH_OPPONENTCARD :
            {
                setSubmode(SUBMODE_WAITPLAYERMOVE);
                startup.processGameAction(GAMEACTION_OPPONENTMOVEEND);
            };break;
            case PATH_PLAYERCARD :
            {
                setSubmode(SUBMODE_CARDSONTABLE);
            };break;
            case PATH_OPPONENTHEAP :
            {
                i_OpponentHeapCardsNumber += 2;
                ap_SpriteCollections[0].releaseAllSprites();

                if (i_PlayerCardsPosition==0 && i_OpponentCardsPosition==0)
                {
                    i_PlayerState = i_PlayerHeapCardsNumber>=i_OpponentHeapCardsNumber ? PLAYER_WIN : PLAYER_LOST;
                    i_GameState = STATE_OVER;
                }
                else
                    setSubmode(SUBMODE_WAITOPPONENTMOVE);
            };break;
            case PATH_PLAYERHEAP :
            {
                i_PlayerHeapCardsNumber += 2;
                i_PlayerScore += (i_CurrentPlayerCard & 0xF)+(i_CurrentOpponentCard & 0xF);
                ap_SpriteCollections[0].releaseAllSprites();

                if (i_PlayerCardsPosition==0 && i_OpponentCardsPosition==0)
                {
                    i_PlayerState = i_PlayerHeapCardsNumber>=i_OpponentHeapCardsNumber ? PLAYER_WIN : PLAYER_LOST;
                    i_GameState = STATE_OVER;
                }
                else
                    setSubmode(SUBMODE_WAITOPPONENTMOVE);
            };break;
        }
    }

    /**
     * Флаг, показывающий что игра поддерживает перезапуск игрового уровня (фазы)
     */
    public static final int FLAG_SUPPORTRESTART = 1;
    /**
     * Флаг, показывающий что игра поддерживает игровые уровни (фазы)
     */
    public static final int FLAG_STAGESUPPORT = 2;

    public static final int getSupportedModes()
    {
        return FLAG_STAGESUPPORT;
    }

    /**
     * Функция отрабатывает игровой шаг
     *
     * @param _keyStateFlags флаги управления игроком.
     * @return статус игры после отработки игровой интерации
     */
    public static final int nextGameStep(int _keyStateFlags)
    {
        //------------Вставьте свой код здесь--------------------
        if (i_CurrentSubmodeTimer>0) i_CurrentSubmodeTimer--;

        ap_SpriteCollections[0].processAnimationForActiveSprites();

        switch(i_CurrentGameSubmode)
        {
            case SUBMODE_CARDSONTABLE :
            {
                if (i_CurrentSubmodeTimer==0)
                {
                    setSubmode(SUBMODE_CARDSTOHEAP);
                }
            };break;
            case SUBMODE_WAITPLAYERMOVE :
            {
                if ((_keyStateFlags & BUTTON_FIRE)!=0 || (_keyStateFlags & BUTTON_UP)!=0)
                {
                    setSubmode(SUBMODE_PLAYERMOVE);
                    startup.processGameAction(GAMEACTION_PLAYERMOVE);
                }
            };break;
            case SUBMODE_WAITOPPONENTMOVE :
            {
                if (i_CurrentSubmodeTimer==0) setSubmode(SUBMODE_OPPONENTMOVE);
            };break;
        }
        startup.i_KeyFlags = 0;

        for(int li=0;li<MAXPATHS;li++)
        {
            PathController p_path = ap_Paths[li];
            if (!p_path.lg_Completed)p_path.processStep();
        }

        //--------------------------------------------------
        return i_GameState;
    }

    //------------------Игровые события---------------------

    //------------------Игровые клавиши---------------------
    /**
     * Нет нажатой кнопки
     */
    public static final int BUTTON_NONE = 0;

    /**
     * Нажата кнопка влево
     */
    public static final int BUTTON_LEFT = 1;

    /**
     * Нажата кнопка вправо
     */
    public static final int BUTTON_RIGHT = 2;

    /**
     * Нажата кнопка вверх
     */
    public static final int BUTTON_UP = 4;

    /**
     * Нажата кнопка вниз
     */
    public static final int BUTTON_DOWN = 8;

    /**
     * Нажата кнопка огонь
     */
    public static final int BUTTON_FIRE = 16;

//todo Константы
    public  static final int SUBMODE_WAITOPPONENTMOVE = 0;
    public  static final int SUBMODE_WAITPLAYERMOVE = 1;
    public  static final int SUBMODE_OPPONENTMOVE = 2;
    public  static final int SUBMODE_PLAYERMOVE = 3;
    public  static final int SUBMODE_CARDSONTABLE = 4;
    public  static final int SUBMODE_CARDSTOHEAP = 5;

    /**
     * Задержка показа карт на игровом столе в тиках
     */
    private static final int DELAY_CARDSONTABLE = 5;

    /**
     * Задержка перед ходом оппонтента
     */
    private static final int DELAY_OPPONENTMOVE = 5;

    /**
     * Пики
     */
    public static final int CARDMASK_SPADES = 0;

    /**
     * Черви
     */
    public static final int CARDMASK_HEARTS = 1;

    /**
     * Бубны
     */
    public static final int CARDMASK_DIAMONDS = 2;

    /**
     * Крести
     */
    public static final int CARDMASK_CROSSES = 3;

    /**
     * Максимальное количество путей в игре
     */
    public static final int MAXPATHS = 5;

    /**
     * количество попыток игрока на простом уровне
     */
    private static final int ATTEMPTIONS_EASY = 4;

    /**
     * количество попыток игрока на нормальном уровне
     */
    private static final int ATTEMPTIONS_NORMAL = 3;

    /**
     * количество попыток игрока на тяжелом уровне
     */
    private static final int ATTEMPTIONS_HARD = 2;

    

//todo Переменные
    private static int i_CurrentSubmodeTimer;

    /**
     * Массив содержит карты игрока
     */
    private static final byte [] ab_PlayerColoda = new byte[18];

    /**
     * Массив содержит карты оппонента
     */
    private static final byte [] ab_OpponentColoda = new byte[18];

    /**
     * Массив содержит количество карт в куче оппонента
     */
    public static int i_OpponentHeapCardsNumber;

    /**
     * Массив содержит количество карт в куче игрока
     */
    public static int i_PlayerHeapCardsNumber;

    /**
     * Массив содержит позицию последней карты в колоде игрока
     */
    public static int i_OpponentCardsPosition;

    /**
     * Массив содержит позицию последней карты в колоде оппонента
     */
    public static int i_PlayerCardsPosition;

    /**
     * Текущая карта игрока
     */
    public static int i_CurrentPlayerCard;

    /**
     * Текущая карта оппонента
     */
    public static int i_CurrentOpponentCard;

    /**
     * Текущий игровой подрежим
     */
    public static int i_CurrentGameSubmode;

    /**
     * Массив коллекций спрайтов
     */
    public static SpriteCollection [] ap_SpriteCollections;
    /**
     * Массив контроллеров пути
     */
    private static PathController [] ap_Paths;

//todo Функции
    private static final void setSubmode(int _submode)
    {
        SpriteCollection p_sprCol = ap_SpriteCollections[0];

        i_CurrentGameSubmode = _submode;
        switch(_submode)
        {
            case SUBMODE_WAITOPPONENTMOVE :
            {
                i_CurrentSubmodeTimer = DELAY_OPPONENTMOVE;
            };break;
            case SUBMODE_CARDSONTABLE :
            {
                i_CurrentSubmodeTimer = DELAY_CARDSONTABLE;
            };break;
            case SUBMODE_CARDSTOHEAP :
            {
                p_sprCol.releaseAllSprites();
                deactivateAllPaths();

                int i_sprite = p_sprCol.i_lastInactiveSpriteOffset;

                p_sprCol.activateSprite(SPRITE_OBJ_CARD,SPRITE_STATE_CARD_TWOBACK,i_sprite);

                PathController p_path = getInactivePath();
                int i_val1= i_CurrentPlayerCard & 0xF;
                int i_val2= i_CurrentOpponentCard & 0xF;

                if (i_val1>i_val2)
                {
                    // В кучу игрока
                    p_path.initPath(PATH_PLAYERHEAP,0,0,0x100,0x100,ap_SpriteCollections,0,i_sprite,ash_Paths,PATH_PLAYERHEAP,0,0,0);
                    startup.processGameAction(GAMEACTION_PLAYERTAKE);
                }
                else
                {
                    // В кучу оппонента
                    p_path.initPath(PATH_OPPONENTHEAP,0,0,0x100,0x100,ap_SpriteCollections,0,i_sprite,ash_Paths,PATH_OPPONENTHEAP,0,0,0);
                    startup.processGameAction(GAMEACTION_OPPONENTTAKE);
                }
            };break;
            case SUBMODE_OPPONENTMOVE :
            {
                // берем карту оппонента
                int i_value = ab_OpponentColoda[i_OpponentCardsPosition-1] & 0xFF;
                i_OpponentCardsPosition--;
                int i_sprite = 0;
                p_sprCol.activateSprite(SPRITE_OBJ_CARD,SPRITE_STATE_CARD_FACE,i_sprite);
                p_sprCol.setOptionalData(i_sprite,i_value);
                i_CurrentOpponentCard = i_value;

                PathController p_path = getInactivePath();
                p_path.initPath(PATH_OPPONENTCARD,0,0,0x100,0x100,ap_SpriteCollections,0,i_sprite,ash_Paths,PATH_OPPONENTCARD,0,0,0);
            };break;
            case SUBMODE_PLAYERMOVE :
            {
                // Берем карту игрока
                int i_value = ab_PlayerColoda[i_PlayerCardsPosition-1] & 0xFF;
                i_PlayerCardsPosition--;

                int i_sprite = SpriteCollection.getOffsetForSpriteWithIndex(1);
                p_sprCol.activateSprite(SPRITE_OBJ_CARD,SPRITE_STATE_CARD_FACE,i_sprite);
                p_sprCol.setOptionalData(i_sprite,i_value);

                i_CurrentPlayerCard = i_value;

                PathController p_path = getInactivePath();

                p_path.initPath(PATH_PLAYERCARD,0,0,0x100,0x100,ap_SpriteCollections,0,i_sprite,ash_Paths,PATH_PLAYERCARD,0,0,0);
            };break;
            case SUBMODE_WAITPLAYERMOVE :
            {
                deactivateAllPaths();
            };break;
        }
    }

    private static final void generateColodes()
    {
        byte [] ab_colodes = new byte[36];
        byte [] ab_colodes2 = new byte[36];

        // раскладываем карты упорядоченно
        int i_index = 0;
        for(int lm=0;lm<4;lm++)
        {
            for(int lc=6;lc<=14;lc++)
            {
                ab_colodes[i_index++] = (byte)((lm<<4) | lc);
            }
        }

        // Перемешиваем
        i_index = 0;
        while(i_index<36)
        {
            int i_srcPos = getRandomInt(359999)/10000;
            int i_val = ab_colodes[i_srcPos]&0xFF;
            while(i_val==0)
            {
                i_srcPos++;
                if (i_srcPos>35) i_srcPos = 0;
                i_val = ab_colodes[i_srcPos]&0xFF;
            }
            ab_colodes[i_srcPos] = 0;
            ab_colodes2[i_index++] = (byte) i_val;
        }

        // раскладываем по колодам игрока и оппонента
        System.arraycopy(ab_colodes2,0,ab_OpponentColoda,0,18);
        System.arraycopy(ab_colodes2,18,ab_PlayerColoda,0,18);

        // проверяем на совпадение весов карт и подменяем
        while(true)
        {
            boolean lg_hasPair = false;
            int i_pos = 0;
            for(int li=0;li<18;li++)
            {
                int i_card1 = ab_OpponentColoda[li] & 0xF;
                int i_card2 = ab_PlayerColoda[li] & 0xF;
                if (i_card1 == i_card2)
                {
                    lg_hasPair = true;
                    i_pos = li;
                    break;
                }
            }

            if (lg_hasPair)
            {
                // смещаем карту
                if (getRandomInt(1000)>500)
                {
                    // меняем карту игрока
                    int i_posDst = getRandomInt(179999)/10000;
                    byte b_card = ab_PlayerColoda[i_posDst];
                    byte b_card2 = ab_PlayerColoda[i_pos];
                    ab_PlayerColoda[i_pos] = b_card;
                    ab_PlayerColoda[i_posDst] = b_card2;
                }
                else
                {
                    // меняем карту оппонента
                    int i_posDst = getRandomInt(179999)/10000;
                    byte b_card = ab_OpponentColoda[i_posDst];
                    byte b_card2 = ab_OpponentColoda[i_pos];
                    ab_OpponentColoda[i_pos] = b_card;
                    ab_OpponentColoda[i_posDst] = b_card2;
                }
            }
            else
                break;
        }
    }

    private static void deactivateAllPaths()
    {
        PathController [] ap_paths = ap_Paths;
        for (int li = 0; li < MAXPATHS; li++)
        {
            PathController p_path = ap_paths[li];
            p_path.lg_Completed = true;
        }
    }

    private static PathController getInactivePath()
    {
        PathController [] ap_paths = ap_Paths;
        for (int li = 0; li < MAXPATHS; li++)
        {
            PathController p_path = ap_paths[li];
            if (p_path.lg_Completed) return p_path;
        }
        return null;
    }

    private static short [] loadSpriteArray(Class _this, String _resource) throws Exception
    {
        InputStream p_instr = _this.getResourceAsStream(_resource);
        DataInputStream p_dis = new DataInputStream(p_instr);
        int i_cells = p_dis.readUnsignedShort();
        int i_scells = p_dis.readUnsignedShort();
        short [] ash_result = new short[i_cells];

        int i_offst = 0;
        for (int li = 0; li < i_scells; li++)
        {
            ash_result[i_offst++] = p_dis.readShort();
        }

        i_cells -= i_scells;
        boolean lg_savedAsBytes = p_dis.readBoolean();

        for (int li = 0; li < i_cells; li++)
        {
            ash_result[i_offst++] = (short) (lg_savedAsBytes ? p_dis.readUnsignedByte() : p_dis.readUnsignedShort());
        }

        // Рескалинг параметров
        i_offst = i_scells;
        while (true)
        {
            if (i_offst >= ash_result.length) break;

            ash_result[i_offst] = (short) (((ash_result[i_offst] * startup.SCALE_WIDTH) + 0x7f) >> 8);
            i_offst++;
            ash_result[i_offst] = (short) (((ash_result[i_offst] * startup.SCALE_HEIGHT) + 0x7f) >> 8);
            i_offst++;
            ash_result[i_offst] = (short) (((ash_result[i_offst] * startup.SCALE_WIDTH) + 0x7f) >> 8);
            i_offst++;
            ash_result[i_offst] = (short) (((ash_result[i_offst] * startup.SCALE_HEIGHT) + 0x7f) >> 8);
            i_offst++;
            ash_result[i_offst] = (short) (((ash_result[i_offst] * startup.SCALE_WIDTH) + 0x7f) >> 8);
            i_offst++;
            ash_result[i_offst] = (short) (((ash_result[i_offst] * startup.SCALE_HEIGHT) + 0x7f) >> 8);
            i_offst += 4;
            ash_result[i_offst] = (short) (((ash_result[i_offst] * startup.SCALE_WIDTH) + 0x7f) >> 8);
            i_offst++;
            ash_result[i_offst] = (short) (((ash_result[i_offst] * startup.SCALE_HEIGHT) + 0x7f) >> 8);
            i_offst++;
        }

        p_dis.close();
        p_dis = null;
        return ash_result;
    }

//todo Акции

    public static final int GAMEACTION_OPPONENTMOVEEND = 0;
    public static final int GAMEACTION_PLAYERMOVE = 1;
    public static final int GAMEACTION_OPPONENTTAKE = 2;
    public static final int GAMEACTION_PLAYERTAKE = 3;

//todo Массивы
    protected static short [] ash_SpritesTable;


    // The array contains values for path controllers
    public static short [] ash_Paths;

    // PATH offsets
    private static final int PATH_PLAYERHEAP = 0;
    private static final int PATH_OPPONENTCARD = 8;
    private static final int PATH_OPPONENTHEAP = 16;
    private static final int PATH_PLAYERCARD = 24;

    public static final int SPRITE_OBJ_CARD = 0;
    public static final int SPRITE_STATE_CARD_FACE = 0;
    public static final int SPRITE_STATE_CARD_BACK = 1;
    public static final int SPRITE_STATE_CARD_TWOBACK = 2;
}

