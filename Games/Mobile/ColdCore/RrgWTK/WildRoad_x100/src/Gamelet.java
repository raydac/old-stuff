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
    public static final int MAX_STAGE_NUMBER = 0;

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
    public static int i_PlayerScore;

    /**
     * Переменная содержит номер последнего активного набора
     */
    public static int i_LastUsedPathSet;

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
        try
        {
            ash_SpritesTable = loadSpriteArray(_class, "/spr.bin");
            ash_Paths = (short[]) startup.loadArray(_class, "/paths.bin");
        }
        catch (Exception e)
        {
            //#-
            e.printStackTrace();
            //#+
            return false;
        }

        PathController.SCALE_WIDTH = startup.SCALE_WIDTH;
        PathController.SCALE_HEIGHT = startup.SCALE_HEIGHT;

        ap_SpriteCollections = new SpriteCollection[4];
        ap_SpriteCollections[COLLECTIONID_ACTIVEOBJECTS] = new SpriteCollection(COLLECTIONID_ACTIVEOBJECTS, MAX_ENEMY_OBJECTS_ON_SCREEN, ash_SpritesTable);
        ap_SpriteCollections[COLLECTIONID_OVERROADOBJECTS] = new SpriteCollection(COLLECTIONID_OVERROADOBJECTS, MAX_SERVICE_OBJECTS_ON_SCREEN, ash_SpritesTable);
        ap_SpriteCollections[COLLECTIONID_PASSIVEOBJECTS] = new SpriteCollection(COLLECTIONID_PASSIVEOBJECTS, MAX_ROAD_OBJECTS_ON_SCREEN, ash_SpritesTable);
        ap_SpriteCollections[COLLECTIONID_PLAYER] = new SpriteCollection(COLLECTIONID_PLAYER, 1, ash_SpritesTable);

        ap_Paths = new PathController[MAX_PATH_OBJECTS];
        for (int li = 0; li < MAX_PATH_OBJECTS; li++)
        {
            ap_Paths[li] = new PathController();
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
        ap_SpriteCollections = null;
        ap_Paths = null;
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
        i_PlayerScore = 0;

        switch (_gameLevel)
        {
            case GAMELEVEL_EASY:
            {
                i_PlayerAttemptions = ATTEMPTIONS_EASY;
                i_Current_Phase = EASY_PHASENUMBER;
                i_DelayValueToNextGrenadeOrFireball = EASY_SHOTGRENADEDELAY;
                i_DelayValueToNextJerrican = EASY_JERRICANDELAY;
            }
            ;
            break;
            case GAMELEVEL_NORMAL:
            {
                i_PlayerAttemptions = ATTEMPTIONS_NORMAL;
                i_Current_Phase = NORMAL_PHASENUMBER;
                i_DelayValueToNextGrenadeOrFireball = NORMAL_SHOTGRENADEDELAY;
                i_DelayValueToNextJerrican = NORMAL_JERRICANDELAY;
            }
            ;
            break;
            case GAMELEVEL_HARD:
            {
                i_PlayerAttemptions = ATTEMPTIONS_HARD;
                i_Current_Phase = HARD_PHASENUMBER;
                i_DelayValueToNextGrenadeOrFireball = HARD_SHOTGRENADEDELAY;
                i_DelayValueToNextJerrican = HARD_JERRICANDELAY;
            }
            ;
            break;
            default:
                return false;
        }

        for (int li = 0; li < ap_SpriteCollections.length; li++) ap_SpriteCollections[li].releaseAllSprites();
        for (int li = 0; li < MAX_PATH_OBJECTS; li++) ap_Paths[li].deactivate();

        i_LastUsedPathSet = -1;

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
        activatePlayerForNewGame();
        //--------------------------------------------------
        i_PlayerState = PLAYER_PLAYING;
    }

    /**
     * Инициализация игрового уровня
     *
     * @param _stage ID игрового уровня
     * @return true если инициализация прошла удачно иначе false
     */
    public static final boolean initGameStage(int _stage)
    {
        //------------Вставьте свой код здесь--------------------
        i_GameStage = _stage;

        //--------------------------------------------------
        i_GameStage = _stage;
        initPlayerForGame(false);
        return true;
    }


    /**
     * Загрузка игрового состояния из массива байт
     *
     * @param _data массив байт, описывающих состояние
     * @throws Exception если произошла ошибка при загрузке состояния или игра находилась в состоянии несовместимом с загрузкой.
     */
    public static final void loadGameStateFromByteArray(final byte[] _data) throws Exception
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
        if (!initGameStage(i_gStage)) throw new Exception();
        i_PlayerAttemptions = p_dis.readByte();
        i_MaxPlayerAttemptions = p_dis.readByte();
        //------------Вставьте свой код здесь--------------------
        i8_CurrentPlayerFuel = MAXFUEL;

        i_PlayerScore = p_dis.readInt();
        i_Current_Phase = p_dis.readUnsignedShort();
        i_CurrentSubMode = p_dis.readUnsignedByte();
        i_DelayCounter = p_dis.readInt();
        i_JerricanDelayCounter = p_dis.readShort();
        i_LastUsedPathSet = p_dis.readUnsignedByte();
        i_PhaseScoreAcc = p_dis.readInt();
        i8_BackgroundPos = p_dis.readInt();

        ap_SpriteCollections[COLLECTIONID_PLAYER].loadFromStream(p_dis);
        ap_SpriteCollections[COLLECTIONID_ACTIVEOBJECTS].loadFromStream(p_dis);
        ap_SpriteCollections[COLLECTIONID_OVERROADOBJECTS].loadFromStream(p_dis);
//        ap_SpriteCollections[COLLECTIONID_PASSIVEOBJECTS].loadFromStream(p_dis);

        for(int li=0;li<ap_Paths.length;li++)
        {
            PathController p_contr = ap_Paths[li];
            p_contr.readPathFromStream(p_dis,ap_SpriteCollections,ash_Paths);
        }

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
        //------------Вставьте свой код здесь--------------------

        p_dos.writeInt(i_PlayerScore);
        p_dos.writeShort(i_Current_Phase);
        p_dos.writeByte(i_CurrentSubMode);
        p_dos.writeInt(i_DelayCounter);
        p_dos.writeShort(i_JerricanDelayCounter);
        p_dos.writeByte(i_LastUsedPathSet);
        p_dos.writeInt(i_PhaseScoreAcc);
        p_dos.writeInt(i8_BackgroundPos);

        ap_SpriteCollections[COLLECTIONID_PLAYER].saveToStream(p_dos);
        ap_SpriteCollections[COLLECTIONID_ACTIVEOBJECTS].saveToStream(p_dos);
        ap_SpriteCollections[COLLECTIONID_OVERROADOBJECTS].saveToStream(p_dos);
        //ap_SpriteCollections[COLLECTIONID_PASSIVEOBJECTS].saveToStream(p_dos);

        for(int li=0;li<ap_Paths.length;li++)
        {
            PathController p_contr = ap_Paths[li];
            p_contr.writePathToStream(p_dos);
        }
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
        int Size = 16 + 8;
        //------------Вставьте свой код здесь--------------------
        Size += 22;
        Size += SpriteCollection.getDataSize(MAX_ENEMY_OBJECTS_ON_SCREEN)
                //+SpriteCollection.getDataSize(MAX_ROAD_OBJECTS_ON_SCREEN)
                +SpriteCollection.getDataSize(1)
                +SpriteCollection.getDataSize(MAX_SERVICE_OBJECTS_ON_SCREEN);
        Size += PathController.DATASIZE_BYTES*MAX_PATH_OBJECTS;
        //--------------------------------------------------
        return Size;
    }

    /**
     * Возвращает текстовый идентификатор игры
     *
     * @return строка, идентифицирующая игру.
     */
    public static final String getID()
    {
        return "SIM_WROAD";
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
     * @param _collectionID идентификатор коллекции
     * @param _spriteOffset смещение спрайта в коллекции
     */
    public static final void notifySpriteAnimationCompleted(int _collectionID, int _spriteOffset)
    {
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
        return 0;
    }

    /**
     * Функция отрабатывает игровой шаг
     *
     * @param _keyStateFlags флаги управления игроком.
     * @return статус игры после отработки игровой интерации
     */
    public static final int nextGameStep(int _keyStateFlags)
    {
        if (i_DelayCounter > 0) i_DelayCounter--;

        //------------Вставьте свой код здесь--------------------
        // Уменьшаем координату фона (что бы сдвигался вниз)
        i8_BackgroundPos -= BACKGROUND_SPEED_I8;

        processPaths();
        processStaticObjects();

        SpriteCollection p_collPlayer = ap_SpriteCollections[COLLECTIONID_PLAYER];
        p_collPlayer.processAnimationForActiveSprites();
        int [] ai_arr = p_collPlayer.ai_spriteDataArray;

        switch (i_CurrentSubMode)
        {
            case SUBMODE_ATTEMPTIONSDECREASED:
            {
                if (i_DelayCounter == 0)
                {
                    activatePlayerForNewGame();
                    i_DelayCounter = i_DelayValueToNextGrenadeOrFireball;
                }
            }
            ;
            break;
            case SUBMODE_PLAYERSTOPPING :
            case SUBMODE_GAMEPLAY :
            {
                // Отработка остановки игрока
                if (i_CurrentSubMode == SUBMODE_PLAYERSTOPPING)
                {
                    // Игрок не может двигаться
                    p_collPlayer.moveMainPointXY(0, 0, BACKGROUND_SPEED_I8);

                    processGameObjects();

                    if (ai_arr[SpriteCollection.SPRITEDATAOFFSET_SCREENY] > I8_EDGERIGHTY)
                    {

                        if (ap_SpriteCollections[COLLECTIONID_ACTIVEOBJECTS].i_lastActiveSpriteOffset == -1)
                        {
                            if (ap_SpriteCollections[COLLECTIONID_PASSIVEOBJECTS].i_lastActiveSpriteOffset == -1)
                            {
                                if (ap_SpriteCollections[COLLECTIONID_OVERROADOBJECTS].i_lastActiveSpriteOffset == -1)
                                {
                                    i_PlayerAttemptions--;
                                    if (i_PlayerAttemptions == 0)
                                    {
                                        i_GameState = STATE_OVER;
                                        i_PlayerState = PLAYER_LOST;
                                    }
                                    else
                                    {
                                        i_CurrentSubMode = SUBMODE_ATTEMPTIONSDECREASED;
                                        i_DelayCounter = BETWEENATTEMPTIONSTICKS;
                                        i_Current_Phase++;
                                    }
                                }
                            }
                        }
                    }
                }
                else
                {
                    if (i8_CurrentPlayerFuel > 0)
                    {
                        i8_CurrentPlayerFuel -= FUELSPEED;
                        if (i8_CurrentPlayerFuel <= 0)
                        {
                            i8_CurrentPlayerFuel = 0;
                            i_CurrentSubMode = SUBMODE_PLAYERSTOPPING;
                            allActiveObjectsToNewPath();
                            p_collPlayer.activateSprite(SPRITE_OBJ_PLAYERCAR, SPRITE_STATE_PLAYERCAR_STOPING, 0);
                        }
                    }

                    if (i_JerricanDelayCounter > 0) i_JerricanDelayCounter--;
                    else
                        generateJerrican();

                    int i_dx = 0;
                    int i_dy = 0;

                    // Игрок может двигаться
                    if ((_keyStateFlags & BUTTON_LEFT) != 0)
                    {
                        i_dx = -I8_PLAYERHORZSPEED;
                    }
                    else if ((_keyStateFlags & BUTTON_RIGHT) != 0)
                    {
                        i_dx = I8_PLAYERHORZSPEED;
                    }

                    if ((_keyStateFlags & BUTTON_UP) != 0)
                    {
                        // Вверх
                        i_dy = -I8_PLAYERVERTSPEED;
                    }
                    else if ((_keyStateFlags & BUTTON_DOWN) != 0)
                    {
                        // Вниз
                        i_dy = I8_PLAYERHORZSPEED;
                    }

                    p_collPlayer.moveMainPointXY(0, i_dx, i_dy);
                    p_collPlayer.alignSpriteToArea(0, I8_EDGELEFTX, I8_EDGELEFTY, I8_EDGERIGHTX, I8_EDGERIGHTY, true);

                    processGameObjects();

                    if (ap_SpriteCollections[COLLECTIONID_ACTIVEOBJECTS].i_lastActiveSpriteOffset == -1)
                    {
                        if (ap_SpriteCollections[COLLECTIONID_PASSIVEOBJECTS].i_lastActiveSpriteOffset == -1)
                        {
                            if (ap_SpriteCollections[COLLECTIONID_OVERROADOBJECTS].i_lastActiveSpriteOffset == -1)
                            {
                                i_PlayerScore += i_PhaseScoreAcc;
                                if (!activateNewPhase())
                                {
                                    // Игра закончена
                                    PathController p_pth = getInactivePathController();

                                    int i_x = p_collPlayer.ai_spriteDataArray[SpriteCollection.SPRITEDATAOFFSET_MAINX];
                                    int i_y = p_collPlayer.ai_spriteDataArray[SpriteCollection.SPRITEDATAOFFSET_MAINY];

                                    p_pth.initPath(0,i_x,i_y,0x100,0x100,ap_SpriteCollections,COLLECTIONID_PLAYER,0,ash_Paths,PATH_OUT_PATH,0,0,0);
                                    i_CurrentSubMode = SUBMODE_PLAYERPASSING;
                                }
                            }
                        }
                    }
                }
            }
            ;
            break;
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

    //------------------Игровые переменные------------------
    /**
     * Количество попыток на тяжелом уровне
     */
    private static final int ATTEMPTIONS_HARD = 2;

    /**
     * Количество попыток на нормальном уровне
     */
    private static final int ATTEMPTIONS_NORMAL = 3;

    /**
     * Количество попыток на простом уровне
     */
    private static final int ATTEMPTIONS_EASY = 4;

    /**
     * Количество фаз на тяжелом уровне
     */
    private static final int HARD_PHASENUMBER = 16;

    /**
     * Количество фаз на нормальном уровне
     */
    private static final int NORMAL_PHASENUMBER = 14;

    /**
     * Количество фаз на простом уровне
     */
    private static final int EASY_PHASENUMBER = 10;

    /**
     * Задержка выстрела и гранаты на простом уровне
     */
    private static final int EASY_SHOTGRENADEDELAY = 40;

    /**
     * Задержка выстрела и гранаты на нормальном уровне
     */
    private static final int NORMAL_SHOTGRENADEDELAY = 30;

    /**
     * Задержка выстрела и гранаты на сложном уровне
     */
    private static final int HARD_SHOTGRENADEDELAY = 25;

    /**
     * Задержка до следующей канистры на простом уровне
     */
    private static final int EASY_JERRICANDELAY = 60;

    /**
     * Задержка до следующей канистры на нормальном уровне
     */
    private static final int NORMAL_JERRICANDELAY = 70;

    /**
     * Задержка до следующей канистры на сложном уровне
     */
    private static final int HARD_JERRICANDELAY = 75;

    /**
     * Максимальное количество противника на экране
     */
    private static final int MAX_ENEMY_OBJECTS_ON_SCREEN = 10;

    /**
     * Максимальное количество вспомогательных объектов (выстрелы, гранаты, разрывы, дым)
     */
    private static final int MAX_SERVICE_OBJECTS_ON_SCREEN = 8;

    /**
     * Максимальное количество пассивных объектов (нижний слой, упавшие мотоциклисты, канистры, упавшие мотоциклы)
     */
    private static final int MAX_ROAD_OBJECTS_ON_SCREEN = 10;

    /**
     * Максимальное количество объектов путей
     */
    private static final int MAX_PATH_OBJECTS = 7;

    /**
     * Время между уходом игрока и началом его новой попытки
     */
    private static final int SUBMODE_ATTEMPTIONSDECREASED = 0;

    /**
     * Игрок выезжает на середину экрана
     */
    private static final int SUBMODE_PLAYERINCOMING = 1;

    /**
     * Игрок сломан или остановлен и неуправляем, уезжает за экран
     */
    private static final int SUBMODE_PLAYERSTOPPING = 2;

    /**
     * Игрок выиграл и уезжает за экран
     */
    private static final int SUBMODE_PLAYERPASSING = 3;

    /**
     * Процесс игры
     */
    private static final int SUBMODE_GAMEPLAY = 4;

    /**
     * Количество тиков между попытками
     */
    private static final int BETWEENATTEMPTIONSTICKS = 10;

    /**
     * Скорость уменьшения топлива
     */
    private static final int FUELSPEED = 0x100;

    /**
     * Максимальное количество топлива игрока
     */
    public static final int MAXFUEL = 0x10000;

    /**
     * Значение которое используется для инициализации внутреннего счетчика задержки выстрела или броска гранаты
     */
    private static int i_DelayValueToNextJerrican;

    /**
     * Значение которое используется для инициализации внутреннего счетчика задержки появления канистры
     */
    private static int i_DelayValueToNextGrenadeOrFireball;


    /**
     * Значение текущего состояния топлива игрока
     */
    public static int i8_CurrentPlayerFuel;

    /**
     * Внутриигровой универсальный счетчик
     */
    private static int i_DelayCounter;

    /**
     * Внутриигровой счетчик до появления следющей канистры
     */
    public static int i_JerricanDelayCounter;

    /**
     * Аккумулятор очков за текущую фазу
     */
    private static int i_PhaseScoreAcc;

    /**
     * Текущий подрежим игры
     */
    private static int i_CurrentSubMode;

    /**
     * Левая координата X начала обочины
     */
    public static final int I8_EDGELEFTX = 8*startup.SCALE_WIDTH;

    /**
     * Левая координата Y начала обочины
     */
    public static final int I8_EDGELEFTY = 0;

    /**
     * Правая координата X начала обочины
     */
    public static final int I8_EDGERIGHTX = 168*startup.SCALE_WIDTH;

    /**
     * Правая координата Y начала обочины
     */
    public static final int I8_EDGERIGHTY = 188*startup.SCALE_HEIGHT;

    public static int i_Current_Phase; // Номер текущей фазы (этапа) от максимума до 0, при 0 игрок прошел этапы

    /**
     * Массив колекций спрайтов
     */
    public static SpriteCollection [] ap_SpriteCollections;

    /**
     * Идентификатор коллекции спрайтов, содержащей пассивные объекты на дороге
     */
    public static final int COLLECTIONID_PASSIVEOBJECTS = 0;

    /**
     * Идентификатор коллекции спрайтов, содержащей спрайт игрока
     */
    public static final int COLLECTIONID_PLAYER = 1;

    /**
     * Идентификатор коллекции спрайтов, содержащей активные объекты на дороге
     */
    public static final int COLLECTIONID_ACTIVEOBJECTS = 2;

    /**
     * Идентификатор коллекции спрайтов, содержащей объекты наддорогой (гранаты, выстрелы и прочее)
     */
    public static final int COLLECTIONID_OVERROADOBJECTS = 3;

    /**
     * Вертикальная скорость игрока
     */
    private static final int I8_PLAYERVERTSPEED = (4*startup.SCALE_HEIGHT);

    /**
     * Горизонтальная скорость игрока
     */
    private static final int I8_PLAYERHORZSPEED = (4*startup.SCALE_WIDTH);

    /**
     * Пути объектов в игре
     */
    private static PathController [] ap_Paths;

    /**
     * Скорость фона в игре
     */
    public static final int BACKGROUND_SPEED_I8 = (5*startup.SCALE_HEIGHT);

    /**
     * Позиция фона
     */
    public static int i8_BackgroundPos = 0;

    /**
     * Очки за мотоцикл
     */
    private static final int SCORE_FOR_MOTORCYCLE = 5;

    /**
     * Очки за машину-таран
     */
    private static final int SCORE_FOR_TARANCAR = 10;

    /**
     * Очки за машину-огнемет
     */
    private static final int SCORE_FOR_FLAMECAR = 20;

    /**
     * Очки за машину-гранатомет
     */
    private static final int SCORE_FOR_GRENADECAR = 15;

//todo Функции

    private static final void allActiveObjectsToNewPath()
    {
        // Переключаем все активные объекты (ездящие) на путь ухода с экрана
        SpriteCollection p_sp = ap_SpriteCollections[COLLECTIONID_ACTIVEOBJECTS];
        p_sp.initIterator();
        while (true)
        {
            int i_spr = p_sp.nextActiveSpriteOffset();
            if (i_spr < 0) break;
            int i_type = p_sp.getSpriteType(i_spr);
            int i_state = p_sp.getSpriteState(i_spr);

            if (i_type == SPRITE_OBJ_SPECIALZONE)
            {
                int i_obj = p_sp.getOptionalData(i_spr);
                p_sp.setOptionalData(i_obj, -1);
                p_sp.releaseSprite(i_spr);
                continue;
            }
            else
            {
                if (i_state == SPRITE_STATE_FLAMECAR_MOVING || i_state == SPRITE_STATE_GRENADE_MOVING || i_state == SPRITE_STATE_MOTORCYCLE_MOVING || i_state == SPRITE_STATE_TARANCAR_CAR1MOVING || i_state == SPRITE_STATE_TARANCAR_CAR2MOVING)
                {
                    // новый путь
                    PathController p_cntr = getWayForObject(COLLECTIONID_ACTIVEOBJECTS, i_spr);

                    int i_mx = p_sp.ai_spriteDataArray[i_spr + SpriteCollection.SPRITEDATAOFFSET_MAINX];
                    int i_my = p_sp.ai_spriteDataArray[i_spr + SpriteCollection.SPRITEDATAOFFSET_MAINY];

                    p_cntr.initPath(999, i_mx, i_my, 0x100, 0x100, ap_SpriteCollections, COLLECTIONID_ACTIVEOBJECTS, i_spr, ash_Paths, PATH_OUT_PATH, 0, 0, 0);
                }
            }

        }
    }

    private static final void generateJerrican()
    {
        SpriteCollection p_onRoad = ap_SpriteCollections[COLLECTIONID_PASSIVEOBJECTS];
        int i_freeSpr = p_onRoad.i_lastInactiveSpriteOffset;
        if (i_freeSpr < 0) return;
        i_JerricanDelayCounter = i_DelayValueToNextJerrican;

        p_onRoad.activateSprite(SPRITE_OBJ_JERRICAN, SPRITE_STATE_JERRICAN_PLAYED, i_freeSpr);

        int i_wh = p_onRoad.getSpriteWidthHeight(i_freeSpr);
        int i_w = (i_wh >>> 16) << 8;
        int i_h = (i_wh & 0xFFFF) << 8;

        int i_step = ((I8_EDGERIGHTX - i_w) - (I8_EDGELEFTX + i_w)) / 8;


        int i_posX = getRandomInt(7) * i_step;

        p_onRoad.setMainPointXY(i_freeSpr, I8_EDGELEFTX+i_w + i_posX, -(i_h + 0x500));
    }

    private static final void processGameObjects()
    {
        boolean lg_playerStoped = false;

        // Отрабатываем объекты на дороге
        SpriteCollection p_onRoadObjects = ap_SpriteCollections[COLLECTIONID_PASSIVEOBJECTS];
        SpriteCollection p_activeObjects = ap_SpriteCollections[COLLECTIONID_ACTIVEOBJECTS];
        SpriteCollection p_overRoadObjects = ap_SpriteCollections[COLLECTIONID_OVERROADOBJECTS];
        SpriteCollection p_playerObject = ap_SpriteCollections[COLLECTIONID_PLAYER];

        int i_playerMX = p_playerObject.ai_spriteDataArray[SpriteCollection.SPRITEDATAOFFSET_MAINX];
        int i_playerMY = p_playerObject.ai_spriteDataArray[SpriteCollection.SPRITEDATAOFFSET_MAINY];

        // ОТрабатываем анимацию, исключая игрока
        p_onRoadObjects.processAnimationForActiveSprites();
        p_activeObjects.processAnimationForActiveSprites();
        p_overRoadObjects.processAnimationForActiveSprites();

        // Отрабатываем столкновения активных объектов и зон с игроком
        int i_lastCollided = -1;
        int [] ai_sprArray = p_activeObjects.ai_spriteDataArray;
        while (true)
        {
            i_lastCollided = p_playerObject.findCollidedSprite(0, p_activeObjects, i_lastCollided);
            if (i_lastCollided < 0) break;

            // Проверяем на машину и мотоциклиста
            int i_type = p_activeObjects.getSpriteType(i_lastCollided);
            int i_mx = ai_sprArray[i_lastCollided + SpriteCollection.SPRITEDATAOFFSET_MAINX];
            int i_my = ai_sprArray[i_lastCollided + SpriteCollection.SPRITEDATAOFFSET_MAINY];

            switch (i_type)
            {
                case SPRITE_OBJ_MOTORCYCLE:
                {
                    if (p_activeObjects.getOptionalData(i_lastCollided)==-10) continue;

                    // Столкновение с мотоциклистом, игрок зарабатывает очки, а мотоцикл ломается
                    i_PlayerScore += SCORE_FOR_MOTORCYCLE;

                    // удаляем зону и мотоцикл
                    int i_zoneOffset = p_activeObjects.getOptionalData(i_lastCollided);

                    // помечаем зону к удалению
                    if (i_zoneOffset >= 0)
                        p_activeObjects.setOptionalData(i_zoneOffset, -1);

                    // помечаем мотоцикл к удалению
                    p_activeObjects.setOptionalData(i_lastCollided, -10);

                    // деактивизируем путь
                    deactivatePathForObject(COLLECTIONID_ACTIVEOBJECTS, i_lastCollided);

                    //breakCar(i_lastCollided);

                    // Упавший мотоциклист
                    int i_spr = p_onRoadObjects.i_lastInactiveSpriteOffset;
                    if (i_spr >= 0)
                    {
                            p_onRoadObjects.activateSprite(SPRITE_OBJ_MOTORCYCLIST, SPRITE_STATE_MOTORCYCLIST_FALLEN, i_spr);
                            int i_xoffset = 0xA00;
                            if (i_playerMX > i_mx) i_xoffset = -i_xoffset;
                            p_onRoadObjects.setMainPointXY(i_spr, i_mx + i_xoffset, i_my);
                    }
                }
                ;
                break;
                case SPRITE_OBJ_FLAMECAR :
                case SPRITE_OBJ_GRENADECAR :
                case SPRITE_OBJ_TARANCAR :
                {
                    lg_playerStoped = (i_CurrentSubMode == SUBMODE_GAMEPLAY);
                    if (p_playerObject.getSpriteState(0)==SPRITE_STATE_PLAYERCAR_MOVING) p_playerObject.activateSprite(SPRITE_OBJ_PLAYERCAR, SPRITE_STATE_PLAYERCAR_STOPING, 0);
                    int i_newState = 0;
                    deactivatePathForObject(COLLECTIONID_ACTIVEOBJECTS, i_lastCollided);

                    switch (i_type)
                    {
                        case SPRITE_OBJ_FLAMECAR :
                        {
                            i_newState = SPRITE_STATE_FLAMECAR_STOPING;
                        }
                        ;
                        break;
                        case SPRITE_OBJ_GRENADECAR :
                        {
                            i_newState = SPRITE_STATE_GRENADECAR_STOPING;
                        }
                        ;
                        break;
                        case SPRITE_OBJ_TARANCAR :
                        {
                            i_newState = p_activeObjects.getSpriteState(i_lastCollided);
                            switch (i_newState)
                            {
                                case SPRITE_STATE_TARANCAR_CAR1MOVING :
                                    i_newState = SPRITE_STATE_TARANCAR_CAR1STOPING;
                                    break;
                                case SPRITE_STATE_TARANCAR_CAR2MOVING :
                                    i_newState = SPRITE_STATE_TARANCAR_CAR2STOPING;
                                    break;
                            }
                        }
                        ;
                        break;
                    }
                    p_activeObjects.activateSprite(i_type, i_newState, i_lastCollided);

                    // Отключаем зону
                    int i_zone = p_activeObjects.getOptionalData(i_lastCollided);
                    if (i_zone >= 0)
                    {
                        p_activeObjects.setOptionalData(i_zone, -1);
                    }
                }
                ;
                break;
                case SPRITE_OBJ_SPECIALZONE :
                {
                    // проверка на актуальность зоны
                    int i_zoneOwner = p_activeObjects.getOptionalData(i_lastCollided);
                    if (i_zoneOwner < 0) continue;

                    switch (p_activeObjects.getSpriteState(i_lastCollided))
                    {
                        case SPRITE_STATE_SPECIALZONE_FIRING :
                        {
                            int i_fire = p_overRoadObjects.i_lastInactiveSpriteOffset;

                            if (i_fire < 0 || i_DelayCounter > 0) continue;
                            PathController p_pathController = getInactivePathController();
                            if (p_pathController == null) continue;

                            i_DelayCounter = i_DelayValueToNextGrenadeOrFireball;

                            // получаем Y для выстрела, по нижней границе горячей зоны
                            int i_hwh = p_activeObjects.getSpriteHotzoneWidthHeight(i_lastCollided);
                            int i_sxy = (((short) p_activeObjects.getSpriteScreenXY(i_lastCollided)) + (i_hwh & 0xFFFF)) << 8;

                            p_overRoadObjects.activateSprite(SPRITE_OBJ_FLAMEBLOB, SPRITE_STATE_FLAMEBLOB_MOVING, i_fire);
                            startup.processGameAction(GAMEACTION_FIRING);
                            p_overRoadObjects.setOptionalData(i_fire, i_zoneOwner);
                            p_pathController.initPath(666, i_mx, i_sxy, 0x100, 0x100, ap_SpriteCollections, COLLECTIONID_OVERROADOBJECTS, i_fire, ash_Paths, PATH_PATH_FIREBLOB, 0, 0, 0);
                        }
                        ;
                        break;
                        case SPRITE_STATE_SPECIALZONE_JUMPING :
                        {
                            if (i_CurrentSubMode != SUBMODE_GAMEPLAY) continue;

                            int i_motorcyclist = p_overRoadObjects.i_lastInactiveSpriteOffset;

                            if (i_motorcyclist < 0) continue;
                            PathController p_pathController = getInactivePathController();
                            if (p_pathController == null) continue;

                            int i_pathType;
                            int i_State;
                            if (i_playerMX < i_mx)
                            {
                                i_pathType = PATH_PATH_MOTORCYCLIST_LEFT;
                                i_State = SPRITE_STATE_MOTORCYCLIST_JUMPINGLEFT;
                            }
                            else
                            {
                                i_pathType = PATH_PATH_MOTORCYCLIST_RIGHT;
                                i_State = SPRITE_STATE_MOTORCYCLIST_JUMPINGRIGHT;
                            }

                            p_overRoadObjects.activateSprite(SPRITE_OBJ_MOTORCYCLIST, i_State, i_motorcyclist);
                            startup.processGameAction(GAMEACTION_BIKERJUMP);
                            p_pathController.initPath(111, i_mx, i_my, 0x100, 0x100, ap_SpriteCollections, COLLECTIONID_OVERROADOBJECTS, i_motorcyclist, ash_Paths, i_pathType, 0, 0, 0);

                            int i_zoneOffset = p_activeObjects.getOptionalData(i_lastCollided);

                            p_activeObjects.setOptionalData(i_lastCollided, -1);
                            p_activeObjects.setOptionalData(i_zoneOffset, -10);
                            deactivatePathForObject(COLLECTIONID_ACTIVEOBJECTS, i_zoneOffset);
                        }
                        ;
                        break;
                        case SPRITE_STATE_SPECIALZONE_THROWING :
                        {
                            int i_grenade = p_overRoadObjects.i_lastInactiveSpriteOffset;

                            if (i_grenade < 0 || i_DelayCounter > 0) continue;
                            PathController p_pathController = getInactivePathController();
                            if (p_pathController == null) continue;

                            i_DelayCounter = i_DelayValueToNextGrenadeOrFireball;

                            int i_pathType;
                            if (i_playerMX < i_mx)
                                i_pathType = PATH_PATH_GRENADELEFT;
                            else
                                i_pathType = PATH_PATH_GRENADERIGHT;

                            p_overRoadObjects.activateSprite(SPRITE_OBJ_GRENADE, SPRITE_STATE_GRENADE_MOVING, i_grenade);
                            startup.processGameAction(GAMEACTION_GRENADETHREW);
                            p_pathController.initPath(545, i_mx, i_my, 0x100, 0x100, ap_SpriteCollections, COLLECTIONID_OVERROADOBJECTS, i_grenade, ash_Paths, i_pathType, 0, 0, 0);
                        }
                        ;
                        break;
                    }
                }
            }
        }

        // Отрабатываем столкновения активных объектов между собой
        p_activeObjects.initIterator();
        while (true)
        {
            int i_activeObject = p_activeObjects.nextActiveSpriteOffset();
            if (i_activeObject < 0) break;

            int i_curObjectType = p_activeObjects.getSpriteType(i_activeObject);
            int i_opt = p_activeObjects.getOptionalData(i_activeObject);

            if (i_curObjectType == SPRITE_OBJ_SPECIALZONE)
            {
                // Удаляем несвязанную активную зону
                if (i_opt < 0) p_activeObjects.releaseSprite(i_activeObject);
                continue;
            }

            // переносим сломанный мотоцикл на нижний план
            if (i_opt == -10 && i_curObjectType == SPRITE_OBJ_MOTORCYCLE)
            {
                int i_onroad = p_onRoadObjects.i_lastInactiveSpriteOffset;
                if (i_onroad >= 0)
                {
                    int i_mx = ai_sprArray[i_activeObject + SpriteCollection.SPRITEDATAOFFSET_MAINX];
                    int i_my = ai_sprArray[i_activeObject + SpriteCollection.SPRITEDATAOFFSET_MAINY];
                    p_onRoadObjects.activateSprite(SPRITE_OBJ_MOTORCYCLE, SPRITE_STATE_MOTORCYCLE_FALLEN, i_onroad);
                    p_onRoadObjects.setMainPointXY(i_onroad, i_mx, i_my);
                }
                p_activeObjects.releaseSprite(i_activeObject);
                continue;
            }

            // не обрабатываем проверку для стоящих или сломанных объектов
            switch(p_activeObjects.getSpriteState(i_activeObject))
            {
                //case SPRITE_STATE_FLAMECAR_BROKEN :
                //case SPRITE_STATE_FLAMECAR_STOPING :
                //case SPRITE_STATE_GRENADECAR_BROKEN :
                //case SPRITE_STATE_GRENADECAR_STOPING :
                case SPRITE_STATE_TARANCAR_CAR1BROKEN :
                case SPRITE_STATE_TARANCAR_CAR1STOPING :
                case SPRITE_STATE_TARANCAR_CAR2BROKEN :
                case SPRITE_STATE_TARANCAR_CAR2STOPING : continue;
            }


            i_lastCollided = -1;
            while (true)
            {
                i_lastCollided = p_activeObjects.findCollidedSprite(i_activeObject, p_activeObjects, i_lastCollided);
                if (i_lastCollided < 0) break;
                int i_collidedObjectType = p_activeObjects.getSpriteType(i_lastCollided);
                if (i_collidedObjectType != SPRITE_OBJ_SPECIALZONE)
                {
                    switch (i_curObjectType)
                    {
                        case SPRITE_OBJ_FLAMECAR:
                        {
                            if (i_collidedObjectType != SPRITE_OBJ_MOTORCYCLE)
                            {
                                deactivatePathForObject(COLLECTIONID_ACTIVEOBJECTS, i_activeObject);

                                int i_zone = p_activeObjects.getOptionalData(i_activeObject);
                                if (i_zone >= 0) p_activeObjects.setOptionalData(i_zone, -1);

                                p_activeObjects.activateSprite(i_curObjectType, SPRITE_STATE_FLAMECAR_STOPING, i_activeObject);
                            }
                        }
                        ;
                        break;
                        case SPRITE_OBJ_GRENADECAR:
                        {
                            if (i_collidedObjectType != SPRITE_OBJ_MOTORCYCLE)
                            {
                                deactivatePathForObject(COLLECTIONID_ACTIVEOBJECTS, i_activeObject);

                                int i_zone = p_activeObjects.getOptionalData(i_activeObject);
                                if (i_zone >= 0) p_activeObjects.setOptionalData(i_zone, -1);

                                p_activeObjects.activateSprite(i_curObjectType, SPRITE_STATE_GRENADECAR_STOPING, i_activeObject);
                            }
                        }
                        ;
                        break;
                        case SPRITE_OBJ_TARANCAR:
                        {
                            if (i_collidedObjectType != SPRITE_OBJ_MOTORCYCLE)
                            {
                                deactivatePathForObject(COLLECTIONID_ACTIVEOBJECTS, i_activeObject);

                                int i_zone = p_activeObjects.getOptionalData(i_activeObject);
                                if (i_zone >= 0) p_activeObjects.setOptionalData(i_zone, -1);

                                int i_state = 0;
                                if (p_activeObjects.getSpriteState(i_activeObject) == SPRITE_STATE_TARANCAR_CAR1MOVING)
                                    i_state = SPRITE_STATE_TARANCAR_CAR1STOPING;
                                else
                                    i_state = SPRITE_STATE_TARANCAR_CAR2STOPING;

                                p_activeObjects.activateSprite(i_curObjectType, i_state, i_activeObject);
                            }
                        }
                        ;
                        break;
                        case SPRITE_OBJ_MOTORCYCLE:
                        {
                            // переводим мотоцикл на нижний план
                            deactivatePathForObject(COLLECTIONID_ACTIVEOBJECTS, i_activeObject);

                            // гасим зону
                            int i_zone = p_activeObjects.getOptionalData(i_activeObject);
                            if (i_zone >= 0) p_activeObjects.setOptionalData(i_zone, -1);

                            // переносим мотоцикл
                            int i_onroad = p_onRoadObjects.i_lastInactiveSpriteOffset;
                            int i_mx = ai_sprArray[i_activeObject + SpriteCollection.SPRITEDATAOFFSET_MAINX];
                            int i_my = ai_sprArray[i_activeObject + SpriteCollection.SPRITEDATAOFFSET_MAINY];

                            if (i_onroad >= 0)
                            {
                                p_onRoadObjects.activateSprite(SPRITE_OBJ_MOTORCYCLE, SPRITE_STATE_MOTORCYCLE_FALLEN, i_onroad);
                                p_onRoadObjects.setMainPointXY(i_onroad, i_mx, i_my);

                                // мотоциклист
                                i_onroad = p_onRoadObjects.i_lastInactiveSpriteOffset;
                                if (i_onroad >= 0)
                                {
                                    p_onRoadObjects.activateSprite(SPRITE_OBJ_MOTORCYCLIST, SPRITE_STATE_MOTORCYCLIST_FALLEN, i_onroad);
                                    p_onRoadObjects.setMainPointXY(i_onroad, i_mx, i_my);
                                }
                            }

                            p_activeObjects.releaseSprite(i_activeObject);
                        }
                        ;
                        break;
                    }
                    ;

                    break;
                }
            }
        }

        // убираем отработанные зоны
        p_activeObjects.initIterator();
        while (true)
        {
            int i_spr = p_activeObjects.nextActiveSpriteOffset();
            if (i_spr < 0) break;
            if (p_activeObjects.getOptionalData(i_spr) < 0)
            {
                if (p_activeObjects.getSpriteType(i_spr) == SPRITE_OBJ_SPECIALZONE)
                {
                    p_activeObjects.releaseSprite(i_spr);
                }
            }
        }

        if (lg_playerStoped)
        {
            i_CurrentSubMode = SUBMODE_PLAYERSTOPPING;
            p_playerObject.activateSprite(SPRITE_OBJ_PLAYERCAR, SPRITE_STATE_PLAYERCAR_BROKEN, 0);
            allActiveObjectsToNewPath();
            // Гененируем взрыв над игроком
            SpriteCollection p_overroad = ap_SpriteCollections[COLLECTIONID_OVERROADOBJECTS];
            int i_sprite = p_overroad.i_lastInactiveSpriteOffset;
            if (i_sprite>=0)
            {
                p_overroad.activateSprite(SPRITE_OBJ_EXPLOSION,SPRITE_STATE_EXPLOSION_PLAYED,i_sprite);
                startup.processGameAction(GAMEACTION_EXPLOSION);
                p_overroad.setMainPointXY(i_sprite,i_playerMX,i_playerMY);
                p_overroad.setSpriteNextTypeState(i_sprite,SpriteCollection.SPRITEBEHAVIOUR_RELEASE);
            }
        }
    }

    // обрабатываем смещения для недвижимых объектов
    private static final void processStaticObjects()
    {
        SpriteCollection p_activeObjects = ap_SpriteCollections[COLLECTIONID_ACTIVEOBJECTS];
        SpriteCollection p_overRoadObjects = ap_SpriteCollections[COLLECTIONID_OVERROADOBJECTS];
        SpriteCollection p_onRoadObjects = ap_SpriteCollections[COLLECTIONID_PASSIVEOBJECTS];
        SpriteCollection p_PlayerObjects = ap_SpriteCollections[COLLECTIONID_PLAYER];

        p_activeObjects.initIterator();
        int [] ai_arr = p_activeObjects.ai_spriteDataArray;
        while (true)
        {
            int i_sprite = p_activeObjects.nextActiveSpriteOffset();
            if (i_sprite < 0) break;

            if (p_activeObjects.getSpriteType(i_sprite) == SPRITE_OBJ_SPECIALZONE) continue;

            switch (p_activeObjects.getSpriteState(i_sprite))
            {
                case SPRITE_STATE_FLAMECAR_STOPING :
                case SPRITE_STATE_TARANCAR_CAR1STOPING :
                case SPRITE_STATE_TARANCAR_CAR2STOPING :
                case SPRITE_STATE_TARANCAR_CAR2BROKEN :
                //case SPRITE_STATE_TARANCAR_CAR1BROKEN :
                    //case SPRITE_STATE_GRENADECAR_STOPING :
                {
                    p_activeObjects.moveMainPointXY(i_sprite, 0, BACKGROUND_SPEED_I8);
                    if (ai_arr[i_sprite + SpriteCollection.SPRITEDATAOFFSET_SCREENY] > I8_EDGERIGHTY)
                    {
                        p_activeObjects.releaseSprite(i_sprite);
                    }
                }
                ;
                break;
            }
        }

        p_overRoadObjects.initIterator();
        ai_arr = p_overRoadObjects.ai_spriteDataArray;
        while (true)
        {
            int i_sprite = p_overRoadObjects.nextActiveSpriteOffset();
            if (i_sprite < 0) break;

            switch (p_overRoadObjects.getSpriteType(i_sprite))
            {
                case SPRITE_OBJ_FLAMEBLOB:
                {
                    // Проверяем на столкновение с игроком
                    int i_collided = p_overRoadObjects.findCollidedSprite(i_sprite, p_PlayerObjects, -1);
                    if (i_collided >= 0)
                    {
                        p_PlayerObjects.activateSprite(SPRITE_OBJ_PLAYERCAR, SPRITE_STATE_PLAYERCAR_BROKEN, 0);
                        int i_mx = p_PlayerObjects.ai_spriteDataArray[SpriteCollection.SPRITEDATAOFFSET_MAINX];
                        int i_my = p_PlayerObjects.ai_spriteDataArray[SpriteCollection.SPRITEDATAOFFSET_MAINY];

                        deactivatePathForObject(COLLECTIONID_OVERROADOBJECTS, i_sprite);

                        int i_spr = p_overRoadObjects.i_lastInactiveSpriteOffset;
                        if (i_spr >= 0)
                        {
                            // дым
                            p_overRoadObjects.activateSprite(SPRITE_OBJ_SMOKE, SPRITE_STATE_SMOKE_PLAYED, i_spr);
                            p_overRoadObjects.setMainPointXY(i_spr, i_mx, i_my);
                        }

                        // взрыв
                        p_overRoadObjects.activateSprite(SPRITE_OBJ_EXPLOSION, SPRITE_STATE_EXPLOSION_PLAYED, i_sprite);
                        startup.processGameAction(GAMEACTION_EXPLOSION);
                        p_overRoadObjects.setSpriteNextTypeState(i_sprite, SpriteCollection.SPRITEBEHAVIOUR_RELEASE);
                        p_overRoadObjects.setMainPointXY(i_sprite, i_mx, i_my);

                        i_CurrentSubMode = SUBMODE_PLAYERSTOPPING;
                        allActiveObjectsToNewPath();
                    }
                    else
                    {
                        // проверяем на столкновение с активными и неактивными объектами машинами
                        i_collided = -1;
                        int i_owner = p_overRoadObjects.getOptionalData(i_sprite);
                        while (true)
                        {
                            i_collided = p_overRoadObjects.findCollidedSprite(i_sprite, p_activeObjects, i_collided);
                            if (i_collided < 0) break;

                            // Проверяем, не столкнулись ли с владельцем
                            if (i_owner == i_collided) continue;

                            int i_sprType = p_activeObjects.getSpriteType(i_collided);
                            if (i_sprType == SPRITE_OBJ_SPECIALZONE) continue;
                            deactivatePathForObject(COLLECTIONID_OVERROADOBJECTS, i_sprite);
                            breakCar(i_collided);
                            p_overRoadObjects.activateSprite(SPRITE_OBJ_EXPLOSION, SPRITE_STATE_EXPLOSION_PLAYED, i_sprite);
                            startup.processGameAction(GAMEACTION_EXPLOSION);
                            p_overRoadObjects.setSpriteNextTypeState(i_sprite, SpriteCollection.SPRITEBEHAVIOUR_RELEASE);
                            break;
                        }
                    }
                }
                ;
                break;
                case SPRITE_OBJ_MOTORCYCLIST:
                {
                    // проверяем мотоциклиста на совмещение с игроком
                    int i_collided = p_overRoadObjects.findCollidedSprite(i_sprite, p_PlayerObjects, -1);
                    if (i_collided >= 0)
                    {
                        // деактивируем мотоциклиста
                        deactivatePathForObject(COLLECTIONID_OVERROADOBJECTS, i_sprite);
                        p_overRoadObjects.releaseSprite(i_sprite);

                        if (i_CurrentSubMode == SUBMODE_GAMEPLAY)
                        {
                            p_PlayerObjects.activateSprite(SPRITE_OBJ_PLAYERCAR, SPRITE_STATE_PLAYERCAR_STOPING, 0);
                            i_CurrentSubMode = SUBMODE_PLAYERSTOPPING;
                            allActiveObjectsToNewPath();
                        }
                    }
                }
                ;
                break;
                case SPRITE_OBJ_SMOKE :
                case SPRITE_OBJ_EXPLOSION :
                {
                    p_overRoadObjects.moveMainPointXY(i_sprite, 0, BACKGROUND_SPEED_I8);
                    if (ai_arr[i_sprite + SpriteCollection.SPRITEDATAOFFSET_SCREENY] > I8_EDGERIGHTY)
                    {
                        p_overRoadObjects.releaseSprite(i_sprite);
                    }
                }
                ;
                break;
            }
        }

        p_onRoadObjects.initIterator();
        ai_arr = p_onRoadObjects.ai_spriteDataArray;
        while (true)
        {
            int i_sprite = p_onRoadObjects.nextActiveSpriteOffset();
            if (i_sprite < 0) break;
            p_onRoadObjects.moveMainPointXY(i_sprite, 0, BACKGROUND_SPEED_I8);

            // проверка канистры
            if (p_onRoadObjects.getSpriteType(i_sprite) == SPRITE_OBJ_JERRICAN)
            {
                // Проверка с игроком
                int i_collided = p_onRoadObjects.findCollidedSprite(i_sprite, p_PlayerObjects, -1);
                if (i_collided >= 0)
                {
                    // Игрок подобрал
                    p_onRoadObjects.releaseSprite(i_sprite);
                    startup.processGameAction(GAMEACTION_GASOLINETAKEN);
                    i8_CurrentPlayerFuel = MAXFUEL;
                    continue;
                }

                // Проверка с другими объектами

                i_collided = -1;
                while (true)
                {
                    i_collided = p_onRoadObjects.findCollidedSprite(i_sprite, p_activeObjects, i_collided);
                    if (i_collided < 0) break;
                    if (p_activeObjects.getSpriteType(i_collided) == SPRITE_OBJ_SPECIALZONE) continue;
                    // противник забрал канистру
                    p_onRoadObjects.releaseSprite(i_sprite);
                    break;
                }
            }

            if (ai_arr[i_sprite + SpriteCollection.SPRITEDATAOFFSET_SCREENY] > I8_EDGERIGHTY)
            {
                p_onRoadObjects.releaseSprite(i_sprite);
            }
        }
    }

    private static final short [] loadSpriteArray(Class _class,String _resource) throws Exception
    {
            InputStream p_instr = _class.getResourceAsStream(_resource);
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

    /*
    public static final int getActivePathsNumber()
    {
        int i_number = 0;
        final PathController [] ap_paths = ap_Paths;
        for (int li = 0; li < MAX_PATH_OBJECTS; li++)
        {
            if (!ap_paths[li].lg_completed) i_number++;
        }
        return i_number;
    }
    */

    private static final void processPaths()
    {
        final PathController [] ap_paths = ap_Paths;
        for (int li = 0; li < MAX_PATH_OBJECTS; li++)
        {
            ap_paths[li].processStep();
        }

        // Выставляем смещения зон
        SpriteCollection p_col = ap_SpriteCollections[COLLECTIONID_ACTIVEOBJECTS];
        p_col.initIterator();
        while (true)
        {
            int i_sprite = p_col.nextActiveSpriteOffset();
            if (i_sprite < 0) break;
            if (p_col.getSpriteType(i_sprite) == SPRITE_OBJ_SPECIALZONE)
            {
                int i_obj = p_col.getOptionalData(i_sprite);
                p_col.alignMainPoint(i_sprite, i_obj);
            }
        }
    }

    private static final void activatePlayerForNewGame()
    {
        i_CurrentSubMode = SUBMODE_PLAYERINCOMING;
        SpriteCollection p_PlayerCollection = ap_SpriteCollections[COLLECTIONID_PLAYER];
        p_PlayerCollection.releaseAllSprites();
        int i_spriteOffset = 0;
        p_PlayerCollection.activateSprite(SPRITE_OBJ_PLAYERCAR, SPRITE_STATE_PLAYERCAR_MOVING, i_spriteOffset);
        PathController p_controller = getInactivePathController();
        p_controller.initPath(199, 0, 0, 0x100, 0x100, ap_SpriteCollections, COLLECTIONID_PLAYER, i_spriteOffset, ash_Paths, PATH_PLAYER_IN, 0, 0, 0);
        i8_CurrentPlayerFuel = MAXFUEL;
        i_JerricanDelayCounter = i_DelayValueToNextJerrican;
    }

    private static final PathController getInactivePathController()
    {
        final PathController [] ap_arr = ap_Paths;
        for (int li = 0; li < MAX_PATH_OBJECTS; li++)
        {
            if (ap_arr[li].lg_completed) return ap_arr[li];
        }
        return null;
    }

    private static final boolean activateNewPhase()
    {
        if (i_Current_Phase == 0) return false;
        i_Current_Phase--;

        int i_indexPhase = 0;
        while(true)
        {
            i_indexPhase = getRandomInt((aiai_PhaseVariants.length * 1000 - 1)) / 1000;
            if (i_indexPhase==i_LastUsedPathSet) continue;
            break;
        }

        int [] ai_phase = aiai_PhaseVariants[i_indexPhase];
        i_LastUsedPathSet = i_indexPhase;

        int i_objectsNumber = ai_phase.length / 3;

        // Активизируем объекты
        int i_offst = 0;

        SpriteCollection p_activeObjects = ap_SpriteCollections[COLLECTIONID_ACTIVEOBJECTS];

        i_PhaseScoreAcc = 0;

        for (int li = 0; li < i_objectsNumber; li++)
        {
            int i_objectType = ai_phase[i_offst++];
            int i_objectState = ai_phase[i_offst++];
            int i_objectPath = ai_phase[i_offst++];

            int i_serviceObjectState = -1;

            switch (i_objectType)
            {
                case SPRITE_OBJ_FLAMECAR :
                {
                    i_serviceObjectState = SPRITE_STATE_SPECIALZONE_FIRING;
                    i_PhaseScoreAcc += SCORE_FOR_FLAMECAR;

                }
                ;
                break;
                case SPRITE_OBJ_MOTORCYCLE :
                {
                    i_serviceObjectState = SPRITE_STATE_SPECIALZONE_JUMPING;
                    i_PhaseScoreAcc += SCORE_FOR_MOTORCYCLE;
                }
                ;
                break;
                case SPRITE_OBJ_GRENADECAR :
                {
                    i_serviceObjectState = SPRITE_STATE_SPECIALZONE_THROWING;
                    i_PhaseScoreAcc += SCORE_FOR_GRENADECAR;
                }
                ;
                break;
                case SPRITE_OBJ_TARANCAR :
                {
                    i_PhaseScoreAcc += SCORE_FOR_TARANCAR;
                }
                ;
                break;
            }

            int i_sprOffst = p_activeObjects.i_lastInactiveSpriteOffset;
            p_activeObjects.activateSprite(i_objectType, i_objectState, i_sprOffst);

            // Активизация пути
            PathController p_cntr = getInactivePathController();
            p_cntr.initPath(911, 0, 0, 0x100, 0x100, ap_SpriteCollections, COLLECTIONID_ACTIVEOBJECTS, i_sprOffst, ash_Paths, i_objectPath, 0, 0, 0);


            if (i_serviceObjectState < 0)
            {
                p_activeObjects.setOptionalData(i_sprOffst, -1);
                continue;
            }

            // Активизация вспомогательного объекта
            if (i_serviceObjectState < 0) continue;

            int i_servObject = p_activeObjects.i_lastInactiveSpriteOffset;
            p_activeObjects.activateSprite(SPRITE_OBJ_SPECIALZONE, i_serviceObjectState, i_servObject);

            // Выравниваем и выставляем перекрестные ссылки
            p_activeObjects.alignMainPoint(i_servObject, i_sprOffst);
            p_activeObjects.setOptionalData(i_sprOffst, i_servObject);
            p_activeObjects.setOptionalData(i_servObject, i_sprOffst);
        }
        return true;
    }

    private static final void deactivatePathForObject(int _collectionId, int _spriteOffset)
    {
        PathController p_way = getWayForObject(_collectionId, _spriteOffset);
        if (p_way != null) p_way.deactivate();
    }

    private static final PathController getWayForObject(int _collectionId, int _spriteOffset)
    {
        PathController [] ap_paths = ap_Paths;
        for (int li = 0; li < MAX_PATH_OBJECTS; li++)
        {
            PathController p_p = ap_paths[li];
            if (p_p.lg_completed) continue;
            if (p_p.i_spriteCollectionID == _collectionId && p_p.i_spriteOffset == _spriteOffset)
            {
                return p_p;
            }
        }
        return null;
    }

    public static final void notifyPathPointPassed(PathController _controller)
    {

    }

    public static final void notifyPathCompleted(PathController _controller)
    {
        SpriteCollection p_coll = ap_SpriteCollections[_controller.i_spriteCollectionID];
        int i_sprite = _controller.i_spriteOffset;
        int i_type = p_coll.getSpriteType(i_sprite);

        switch (i_type)
        {
            case SPRITE_OBJ_MOTORCYCLIST:
            {
                // мотоциклист падает на землю
                int i_mx = p_coll.ai_spriteDataArray[i_sprite + SpriteCollection.SPRITEDATAOFFSET_MAINX];
                int i_my = p_coll.ai_spriteDataArray[i_sprite + SpriteCollection.SPRITEDATAOFFSET_MAINY];

                p_coll.releaseSprite(i_sprite);

                p_coll = ap_SpriteCollections[COLLECTIONID_PASSIVEOBJECTS];

                int i_spr = p_coll.i_lastInactiveSpriteOffset;
                if (i_spr >= 0)
                {
                    p_coll.activateSprite(SPRITE_OBJ_MOTORCYCLIST, SPRITE_STATE_MOTORCYCLIST_FALLEN, i_spr);
                    p_coll.setMainPointXY(i_spr, i_mx, i_my);
                    p_coll.setOptionalData(i_spr, -1);
                }

            }
            ;
            break;
            case SPRITE_OBJ_GRENADE:
            {
                // Взрыв гранаты
                // Проверка зоны на игрока и объекты

                // проверка гранаты на столкновение с игроком
                int i_collided = p_coll.findCollidedSprite(i_sprite,ap_SpriteCollections[COLLECTIONID_PLAYER],-1);
                if (i_collided < 0)
                {
                    // проверка гранаты на попадание в стоящую или движущуюся машину
                    SpriteCollection p_act = ap_SpriteCollections[COLLECTIONID_ACTIVEOBJECTS];
                    i_collided = -1;
                    while(true)
                    {
                        i_collided = p_coll.findCollidedSprite(i_sprite,p_act,i_collided);
                        if (i_collided<0) break;
                        if (p_act.getSpriteType(i_collided)==SPRITE_OBJ_SPECIALZONE) continue;
                        break;
                    }
                    if (i_collided >= 0)
                    {
                            breakCar(i_collided);
                    }
                }
                else
                {
                    SpriteCollection p_PlayerObjects = ap_SpriteCollections[COLLECTIONID_PLAYER];

                    // взрыв
                    p_coll.activateSprite(SPRITE_OBJ_EXPLOSION, SPRITE_STATE_EXPLOSION_PLAYED, i_sprite);
                    startup.processGameAction(GAMEACTION_EXPLOSION);
                    p_coll.setSpriteNextTypeState(i_sprite, SpriteCollection.SPRITEBEHAVIOUR_RELEASE);

                    if (p_PlayerObjects.getSpriteState(0)!=SPRITE_STATE_PLAYERCAR_BROKEN)
                    {
                        p_PlayerObjects.activateSprite(SPRITE_OBJ_PLAYERCAR, SPRITE_STATE_PLAYERCAR_BROKEN, 0);
                        int i_mx = p_PlayerObjects.ai_spriteDataArray[SpriteCollection.SPRITEDATAOFFSET_MAINX];
                        int i_my = p_PlayerObjects.ai_spriteDataArray[SpriteCollection.SPRITEDATAOFFSET_MAINY];

                        deactivatePathForObject(COLLECTIONID_OVERROADOBJECTS, i_sprite);

                        int i_spr = p_coll.i_lastInactiveSpriteOffset;
                        if (i_spr >= 0)
                        {
                            // дым
                            p_coll.activateSprite(SPRITE_OBJ_SMOKE, SPRITE_STATE_SMOKE_PLAYED, i_spr);
                            p_coll.setMainPointXY(i_spr, i_mx, i_my);
                        }
                    }

                    i_CurrentSubMode = SUBMODE_PLAYERSTOPPING;
                    allActiveObjectsToNewPath();
                }

                SpriteCollection p_overRoad = ap_SpriteCollections[COLLECTIONID_OVERROADOBJECTS];
                // взрыв
                p_overRoad.activateSprite(SPRITE_OBJ_EXPLOSION, SPRITE_STATE_EXPLOSION_PLAYED, i_sprite);
                startup.processGameAction(GAMEACTION_EXPLOSION);
                p_overRoad.setSpriteNextTypeState(i_sprite, SpriteCollection.SPRITEBEHAVIOUR_RELEASE);
            }
            ;
            break;
            case SPRITE_OBJ_PLAYERCAR:
            {
                switch (i_CurrentSubMode)
                {
                    case SUBMODE_PLAYERINCOMING :
                    {
                        i_CurrentSubMode = SUBMODE_GAMEPLAY;
                        activateNewPhase();
                    }
                    ;
                    break;
                    case SUBMODE_PLAYERPASSING :
                    {
                        i_PlayerState = PLAYER_WIN;
                        i_GameState = STATE_OVER;
                    }
                    ;
                    break;
                }
            }
            ;
            break;
            default:
            {
                // Деактивизация
                if (i_type != SPRITE_OBJ_TARANCAR && i_type != SPRITE_OBJ_FLAMEBLOB)
                {
                    // Деактивизация зоны
                    int i_zone = p_coll.getOptionalData(i_sprite);
                    if (i_zone >= 0) p_coll.releaseSprite(i_zone);
                }
                p_coll.releaseSprite(i_sprite);
            }
        }
    }

    // отработка взрыва машины при попадании гранаты или снаряда
    private static final void breakCar(int _sprite)
    {
        SpriteCollection p_collection = ap_SpriteCollections[COLLECTIONID_ACTIVEOBJECTS];

        int i_type = p_collection.getSpriteType(_sprite);
        int i_state = p_collection.getSpriteState(_sprite);
        switch (i_state)
        {
            case SPRITE_STATE_FLAMECAR_BROKEN :
                //case SPRITE_STATE_GRENADECAR_BROKEN :
            case SPRITE_STATE_TARANCAR_CAR2BROKEN :
            case SPRITE_STATE_TARANCAR_CAR1BROKEN :
            {
                return;
            }
        }

        // отключаем путь если есть
        deactivatePathForObject(COLLECTIONID_ACTIVEOBJECTS, _sprite);

        // деактивизируем зонну
        if (i_type != SPRITE_OBJ_TARANCAR)
        {
            int i_zone = p_collection.getOptionalData(_sprite);
            if (i_zone >= 0)
            {
                p_collection.releaseSprite(i_zone);
            }
        }

        int i_newState = 0;
        switch (i_type)
        {
            case SPRITE_OBJ_MOTORCYCLE :
            {
                p_collection.releaseSprite(_sprite);
                return;
            }
            case SPRITE_OBJ_FLAMECAR :
            {
                i_newState = SPRITE_STATE_FLAMECAR_BROKEN;
            }
            ;
            break;
            case SPRITE_OBJ_GRENADECAR :
            {
                i_newState = SPRITE_STATE_GRENADECAR_BROKEN;
            }
            ;
            break;
            case SPRITE_OBJ_TARANCAR :
            {
                if (i_state == SPRITE_STATE_TARANCAR_CAR2MOVING || i_state == SPRITE_STATE_TARANCAR_CAR2STOPING)
                    i_newState = SPRITE_STATE_TARANCAR_CAR2BROKEN;
                else
                    i_newState = SPRITE_STATE_TARANCAR_CAR1BROKEN;
            }
            ;
            break;
        }

        p_collection.activateSprite(i_type, i_newState, _sprite);
        int i_mx = p_collection.ai_spriteDataArray[_sprite + SpriteCollection.SPRITEDATAOFFSET_MAINX];
        int i_my = p_collection.ai_spriteDataArray[_sprite + SpriteCollection.SPRITEDATAOFFSET_MAINY];

        // дым
        p_collection = ap_SpriteCollections[COLLECTIONID_OVERROADOBJECTS];
        int i_smoke = p_collection.i_lastInactiveSpriteOffset;
        if (i_smoke >= 0)
        {
            // активация
            p_collection.activateSprite(SPRITE_OBJ_SMOKE, SPRITE_STATE_SMOKE_PLAYED, i_smoke);
            p_collection.setMainPointXY(i_smoke, i_mx, i_my);
        }
    }

//todo игровые акции
    /**
     * Мотоциклист прыгает
     */
    public static final int GAMEACTION_BIKERJUMP = 0;
    /**
     * Взрыв
     */
    public static final int GAMEACTION_EXPLOSION = 1;
    /**
     * Выстрел огнемета
     */
    public static final int GAMEACTION_FIRING = 2;
    /**
     * Брошена граната
     */
    public static final int GAMEACTION_GRENADETHREW = 3;
    /**
     * Взята канистра
     */
    public static final int GAMEACTION_GASOLINETAKEN = 4;

//todo Массивы

    protected static short [] ash_SpritesTable;

//------------------Sprite constants-----------------
    public static final int SPRITE_OBJ_PLAYERCAR = 0;
    public static final int SPRITE_STATE_PLAYERCAR_MOVING = 0;
    public static final int SPRITE_STATE_PLAYERCAR_STOPING = 1;
    public static final int SPRITE_STATE_PLAYERCAR_BROKEN = 2;
    public static final int SPRITE_OBJ_EXPLOSION = 1;
    public static final int SPRITE_STATE_EXPLOSION_PLAYED = 0;
    public static final int SPRITE_OBJ_SMOKE = 2;
    public static final int SPRITE_STATE_SMOKE_PLAYED = 0;
    public static final int SPRITE_OBJ_JERRICAN = 3;
    public static final int SPRITE_STATE_JERRICAN_PLAYED = 0;
    public static final int SPRITE_OBJ_FLAMECAR = 4;
    public static final int SPRITE_STATE_FLAMECAR_MOVING = 0;
    public static final int SPRITE_STATE_FLAMECAR_STOPING = 1;
    public static final int SPRITE_STATE_FLAMECAR_BROKEN = 2;
    public static final int SPRITE_OBJ_GRENADECAR = 5;
    public static final int SPRITE_STATE_GRENADECAR_MOVING = 0;
    public static final int SPRITE_STATE_GRENADECAR_STOPING = 1;
    public static final int SPRITE_STATE_GRENADECAR_BROKEN = 2;
    public static final int SPRITE_OBJ_GRENADE = 6;
    public static final int SPRITE_STATE_GRENADE_MOVING = 0;
    public static final int SPRITE_OBJ_SPECIALZONE = 7;
    public static final int SPRITE_STATE_SPECIALZONE_FIRING = 0;
    public static final int SPRITE_STATE_SPECIALZONE_JUMPING = 1;
    public static final int SPRITE_STATE_SPECIALZONE_THROWING = 2;
    public static final int SPRITE_OBJ_FLAMEBLOB = 8;
    public static final int SPRITE_STATE_FLAMEBLOB_MOVING = 0;
    public static final int SPRITE_OBJ_MOTORCYCLE = 9;
    public static final int SPRITE_STATE_MOTORCYCLE_MOVING = 0;
    public static final int SPRITE_STATE_MOTORCYCLE_FALLEN = 1;
    public static final int SPRITE_OBJ_MOTORCYCLIST = 10;
    public static final int SPRITE_STATE_MOTORCYCLIST_JUMPINGLEFT = 0;
    public static final int SPRITE_STATE_MOTORCYCLIST_JUMPINGRIGHT = 1;
    public static final int SPRITE_STATE_MOTORCYCLIST_FALLEN = 2;
    public static final int SPRITE_OBJ_TARANCAR = 11;
    public static final int SPRITE_STATE_TARANCAR_CAR1MOVING = 0;
    public static final int SPRITE_STATE_TARANCAR_CAR1BROKEN = 1;
    public static final int SPRITE_STATE_TARANCAR_CAR1STOPING = 2;
    public static final int SPRITE_STATE_TARANCAR_CAR2MOVING = 3;
    public static final int SPRITE_STATE_TARANCAR_CAR2BROKEN = 4;
    public static final int SPRITE_STATE_TARANCAR_CAR2STOPING = 5;

//----Пути

    // The array contains values for path controllers
    public static short [] ash_Paths;

    // PATH offsets
    private static final int PATH_PLAYER_IN = 0;
    private static final int PATH_PATH_GRENADERIGHT = 8;
    private static final int PATH_PATH_GRENADELEFT = 22;
    private static final int PATH_PATH_FIREBLOB = 36;
    private static final int PATH_PATH_MOTORCYCLIST_RIGHT = 50;
    private static final int PATH_PATH_MOTORCYCLIST_LEFT = 64;
    private static final int PATH_OUT_PATH = 78;
    private static final int PATH_PATH_FLAMECAR_1 = 89;
    private static final int PATH_PATH_TARAN_1 = 139;
    private static final int PATH_PATH_MOTORCYCLER_1 = 177;
    private static final int PATH_PATH_MOTORCYCLER_2_3 = 251;
    private static final int PATH_PATH_MOTORCYCLER_2_1 = 301;
    private static final int PATH_PATH_MOTORCYCLER_2_2 = 348;
    private static final int PATH_PATH_FLAMECAR_3_1 = 395;
    private static final int PATH_PATH_TARANCAR1_3_2 = 460;
    private static final int PATH_PATH_MOTORCYCLER_3_3 = 531;
    private static final int PATH_PATH_MOTORCYCLER_3_4 = 602;
    private static final int PATH_PATH_TARANCAR1_4_1 = 676;
    private static final int PATH_PATH_TARANCAR2_4_2 = 741;
    private static final int PATH_PATH_MOTORCYCLER_4_3 = 821;
    private static final int PATH_PATH_MOTORCYCLER_5_1 = 904;
    private static final int PATH_PATH_MOTORCYCLER_5_2 = 996;
    private static final int PATH_PATH_GRANADECAR_5_3 = 1085;
    private static final int PATH_PATH_GRENADERCAR_6_1 = 1201;
    private static final int PATH_PATH_TARANCAR2_6_2 = 1287;
    private static final int PATH_PATH_MOTORCYCLER_7_3 = 1379;
    private static final int PATH_PATH_MOTORCYCLER_7_5 = 1459;
    private static final int PATH_PATH_MOTORCYCLER_7_4 = 1551;
    private static final int PATH_PATH_MOTORCYCLER_7_2 = 1637;
    private static final int PATH_PATH_FLAMECAR_7_1 = 1711;
    private static final int PATH_PATH_MOTORCYCLER_8_1 = 1788;
    private static final int PATH_PATH_MOTORCYCLER_9_1 = 1805;
    private static final int PATH_PATH_MOTORCYCLER_10_1 = 1822;
    private static final int PATH_PATH_GRANADECAR_10_3 = 1926;
    private static final int PATH_PATH_MOTORCYCLER_10_2 = 2024;
    private static final int PATH_PATH_GRANADECAR_11_1 = 2125;
    private static final int PATH_PATH_MOTORCYCLER_11_2 = 2217;
    private static final int PATH_PATH_MOTORCYCLER_11_3 = 2294;
    private static final int PATH_PATH_MOTORCYCLER_12_2 = 2383;
    private static final int PATH_PATH_MOTORCYCLER_12_1 = 2442;
    private static final int PATH_PATH_MOTORCYCLER_12_4 = 2507;
    private static final int PATH_PATH_MOTORCYCLER_12_3 = 2575;
    private static final int PATH_PATH_GRANADECAR_13_3 = 2646;
    private static final int PATH_PATH_TARANCAR_13_1 = 2732;
    private static final int PATH_PATH_MOTORCYCLER_13_2 = 2833;
    private static final int PATH_PATH_TARANCAR_14_3 = 2925;
    private static final int PATH_PATH_MOTORCYCLER_14_1 = 3014;
    private static final int PATH_PATH_MOTORCYCLER_14_2 = 3085;




 protected static final int [][] aiai_PhaseVariants = new int[][]
 {

             new int []
                     {
// набор 1
                             SPRITE_OBJ_MOTORCYCLE, SPRITE_STATE_MOTORCYCLE_MOVING, PATH_PATH_MOTORCYCLER_1,
                             SPRITE_OBJ_FLAMECAR, SPRITE_STATE_FLAMECAR_MOVING, PATH_PATH_FLAMECAR_1,
                             SPRITE_OBJ_GRENADECAR, SPRITE_STATE_GRENADECAR_MOVING, PATH_PATH_TARAN_1
                     },

               new int []
                     {
//  набор 2
                             SPRITE_OBJ_MOTORCYCLE, SPRITE_STATE_MOTORCYCLE_MOVING, PATH_PATH_MOTORCYCLER_2_1,
                             SPRITE_OBJ_MOTORCYCLE, SPRITE_STATE_MOTORCYCLE_MOVING, PATH_PATH_MOTORCYCLER_2_2,
                             SPRITE_OBJ_MOTORCYCLE, SPRITE_STATE_MOTORCYCLE_MOVING, PATH_PATH_MOTORCYCLER_2_3
                     },

             new int []
                   {
// набор 3
                             SPRITE_OBJ_MOTORCYCLE, SPRITE_STATE_MOTORCYCLE_MOVING, PATH_PATH_MOTORCYCLER_3_3,
                             SPRITE_OBJ_MOTORCYCLE, SPRITE_STATE_MOTORCYCLE_MOVING, PATH_PATH_MOTORCYCLER_3_4,
                             SPRITE_OBJ_FLAMECAR, SPRITE_STATE_FLAMECAR_MOVING, PATH_PATH_FLAMECAR_3_1,
                             SPRITE_OBJ_TARANCAR, SPRITE_STATE_TARANCAR_CAR1MOVING, PATH_PATH_TARANCAR1_3_2
                   },

             new int []
                   {
// набор 4
                              SPRITE_OBJ_MOTORCYCLE, SPRITE_STATE_MOTORCYCLE_MOVING, PATH_PATH_MOTORCYCLER_4_3,
                              SPRITE_OBJ_TARANCAR, SPRITE_STATE_TARANCAR_CAR1MOVING, PATH_PATH_TARANCAR1_4_1,
                              SPRITE_OBJ_TARANCAR, SPRITE_STATE_TARANCAR_CAR2MOVING, PATH_PATH_TARANCAR2_4_2
                   },

             new int []
                   {
// набор 5
                              SPRITE_OBJ_MOTORCYCLE, SPRITE_STATE_MOTORCYCLE_MOVING, PATH_PATH_MOTORCYCLER_5_1,
                              SPRITE_OBJ_MOTORCYCLE, SPRITE_STATE_MOTORCYCLE_MOVING, PATH_PATH_MOTORCYCLER_5_2,
                              SPRITE_OBJ_GRENADECAR, SPRITE_STATE_GRENADECAR_MOVING, PATH_PATH_GRANADECAR_5_3
                   },

             new int []
                   {
// набор 6
                              SPRITE_OBJ_GRENADECAR, SPRITE_STATE_GRENADECAR_MOVING, PATH_PATH_GRENADERCAR_6_1,
                              SPRITE_OBJ_TARANCAR, SPRITE_STATE_TARANCAR_CAR2MOVING, PATH_PATH_TARANCAR2_6_2
                   },

             new int []
                   {
// набор 7
                             SPRITE_OBJ_MOTORCYCLE, SPRITE_STATE_MOTORCYCLE_MOVING, PATH_PATH_MOTORCYCLER_7_2,
                             SPRITE_OBJ_MOTORCYCLE, SPRITE_STATE_MOTORCYCLE_MOVING, PATH_PATH_MOTORCYCLER_7_3,
                             SPRITE_OBJ_MOTORCYCLE, SPRITE_STATE_MOTORCYCLE_MOVING, PATH_PATH_MOTORCYCLER_7_4,
                             SPRITE_OBJ_MOTORCYCLE, SPRITE_STATE_MOTORCYCLE_MOVING, PATH_PATH_MOTORCYCLER_7_5,
                             SPRITE_OBJ_FLAMECAR, SPRITE_STATE_FLAMECAR_MOVING, PATH_PATH_FLAMECAR_7_1
                   },

             new int []
                   {
// набор 8
                             SPRITE_OBJ_MOTORCYCLE, SPRITE_STATE_MOTORCYCLE_MOVING, PATH_PATH_MOTORCYCLER_8_1,
                   },

             new int []
                   {
// набор 9
                             SPRITE_OBJ_MOTORCYCLE, SPRITE_STATE_MOTORCYCLE_MOVING, PATH_PATH_MOTORCYCLER_9_1,
                   },


             new int []
                   {
// набор 10
                              SPRITE_OBJ_MOTORCYCLE, SPRITE_STATE_MOTORCYCLE_MOVING, PATH_PATH_MOTORCYCLER_10_1,
                              SPRITE_OBJ_MOTORCYCLE, SPRITE_STATE_MOTORCYCLE_MOVING, PATH_PATH_MOTORCYCLER_10_2,
                              SPRITE_OBJ_GRENADECAR, SPRITE_STATE_GRENADECAR_MOVING, PATH_PATH_GRANADECAR_10_3
                   },

             new int []
                   {
// набор 11
                              SPRITE_OBJ_GRENADECAR, SPRITE_STATE_GRENADECAR_MOVING, PATH_PATH_GRANADECAR_11_1,
                              SPRITE_OBJ_MOTORCYCLE, SPRITE_STATE_MOTORCYCLE_MOVING, PATH_PATH_MOTORCYCLER_11_2,
                              SPRITE_OBJ_MOTORCYCLE, SPRITE_STATE_MOTORCYCLE_MOVING, PATH_PATH_MOTORCYCLER_11_3

                   },

             new int []
                   {
// набор 12
                              SPRITE_OBJ_MOTORCYCLE, SPRITE_STATE_MOTORCYCLE_MOVING, PATH_PATH_MOTORCYCLER_12_1,
                              SPRITE_OBJ_MOTORCYCLE, SPRITE_STATE_MOTORCYCLE_MOVING, PATH_PATH_MOTORCYCLER_12_2,
                              SPRITE_OBJ_MOTORCYCLE, SPRITE_STATE_MOTORCYCLE_MOVING, PATH_PATH_MOTORCYCLER_12_3,
                              SPRITE_OBJ_MOTORCYCLE, SPRITE_STATE_MOTORCYCLE_MOVING, PATH_PATH_MOTORCYCLER_12_4,
                   },

             new int []
                   {
// набор 13
                              SPRITE_OBJ_GRENADECAR, SPRITE_STATE_GRENADECAR_MOVING, PATH_PATH_TARANCAR_13_1,
                              SPRITE_OBJ_MOTORCYCLE, SPRITE_STATE_MOTORCYCLE_MOVING, PATH_PATH_MOTORCYCLER_13_2,
                              SPRITE_OBJ_GRENADECAR, SPRITE_STATE_GRENADECAR_MOVING, PATH_PATH_GRANADECAR_13_3
                    },

             new int []
                   {
// набор 14
                              SPRITE_OBJ_MOTORCYCLE, SPRITE_STATE_MOTORCYCLE_MOVING, PATH_PATH_MOTORCYCLER_14_1,
                              SPRITE_OBJ_MOTORCYCLE, SPRITE_STATE_MOTORCYCLE_MOVING, PATH_PATH_MOTORCYCLER_14_2,
                              SPRITE_OBJ_TARANCAR, SPRITE_STATE_TARANCAR_CAR2MOVING, PATH_PATH_TARANCAR_14_3
                    },


     };
}

