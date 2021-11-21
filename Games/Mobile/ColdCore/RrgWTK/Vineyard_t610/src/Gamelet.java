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
    public static final int MAX_STAGE_NUMBER = 2;

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
            ash_Paths = (short[]) startup.loadArray(_class, "/paths.bin");
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

        ap_SpriteCollections = new SpriteCollection[4];
        ap_SpriteCollections[COLLECTION_PLAYER] = new SpriteCollection(COLLECTION_PLAYER, MAXPLAYEROBJECTS, ash_SpritesTable);
        ap_SpriteCollections[COLLECTION_BASKETS] = new SpriteCollection(COLLECTION_BASKETS, MAXBASKETSOBJECTS, ash_SpritesTable);
        ap_SpriteCollections[COLLECTION_BUNCHES] = new SpriteCollection(COLLECTION_BUNCHES, MAXBUNCHESOBJECTS, ash_SpritesTable);
        ap_SpriteCollections[COLLECTION_SNAILS] = new SpriteCollection(COLLECTION_SNAILS, MAXSNAILSOBJECTS, ash_SpritesTable);

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

        // инициализируем игрока
        int i_x = PLAYERINITX;
        int i_y = PLAYERINITY;
        long l_xy = calculatePositionXY(i_x, i_y, ANCHOR_BOTTOM);
        i_x = (int) (l_xy >>> 32);
        i_y = (int) l_xy;

        SpriteCollection p_spites = ap_SpriteCollections[COLLECTION_PLAYER];
        p_spites.releaseAllSprites();
        int i_player = 0;
        p_spites.activateSprite(SPRITE_OBJ_PLAYER_EMPTY, SPRITE_STATE_PLAYER_EMPTY_STANDRIGHT, i_player);
        p_spites.setMainPointXY(i_player, i_x, i_y);

        i8_lastGoodPlayerX = i_x;
        i8_lastGoodPlayerY = i_y;

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
        try
        {
            String s_name = "/lvl" + _stage + ".bin";
            //#-
            System.out.println("Start loading stage array " + s_name);
            //#+
            TERRAIN_MAP = null;
            TERRAIN_MAP = (byte[]) startup.loadArray(_class, s_name);

            //#-
            System.out.println("End of loading stage array");
            //#+
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        deactivateAllPaths();
        generateEnemyObjects();
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

        i8_lastGoodPlayerX = p_dis.readInt();
        i8_lastGoodPlayerY = p_dis.readInt();
        i8_lastTakenBunchX = p_dis.readInt();
        i8_lastTakenBunchY = p_dis.readInt();
        i_EmptyBasketsCounter = p_dis.readInt();

        for (int li = 0; li < ap_SpriteCollections.length; li++) ap_SpriteCollections[li].loadFromStream(p_dis);
        for (int li = 0; li < ap_Paths.length; li++)
            ap_Paths[li].readPathFromStream(p_dis, ap_SpriteCollections, ash_Paths);

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

        p_dos.writeInt(i8_lastGoodPlayerX);
        p_dos.writeInt(i8_lastGoodPlayerY);
        p_dos.writeInt(i8_lastTakenBunchX);
        p_dos.writeInt(i8_lastTakenBunchY);
        p_dos.writeInt(i_EmptyBasketsCounter);

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
        i_Size += 20;
        i_Size += SpriteCollection.getDataSize(MAXPLAYEROBJECTS);
        i_Size += SpriteCollection.getDataSize(MAXBUNCHESOBJECTS);
        i_Size += SpriteCollection.getDataSize(MAXBASKETSOBJECTS);
        i_Size += SpriteCollection.getDataSize(MAXSNAILSOBJECTS);
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
        return "SIM_VINYRD";
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
        int i_type = _collection.getSpriteType(_spriteOffset);
        switch (i_type)
        {
            case SPRITE_OBJ_PLAYER_KILLED :
            {
                i_PlayerAttemptions--;
                if (i_PlayerAttemptions == 0)
                {
                    i_PlayerState = PLAYER_LOST;
                    i_GameState = STATE_OVER;
                }
                else
                {
                    startup.processGameAction(GAMEACTION_ATTEMPTIONSCHANGED);
                    initPlayerForGame(false);
                }
            }
            ;
            break;
            case SPRITE_OBJ_PLAYER_WIN :
            {
                i_GameState = STATE_OVER;
                i_PlayerState = PLAYER_WIN;
            }
            ;
            break;
            case SPRITE_OBJ_SNAIL_LEFT :
            case SPRITE_OBJ_SNAIL_RIGHT :
            {
                PathController p_path = getPathForSprite(_collection.i_CollectionID, _spriteOffset);
                if (p_path != null)
                {
                    // Снимаем с паузы
                    p_path.lg_Paused = false;
                }

                int i_state = _collection.getSpriteState(_spriteOffset);
                switch (i_state)
                {
                    case SPRITE_STATE_SNAIL_LEFT_BITE :
                    {
                        int i_lastType = _collection.getOptionalData(_spriteOffset);
                        _collection.activateSprite(i_lastType, SPRITE_STATE_SNAIL_LEFT_MOVING, _spriteOffset);
                    }
                    ;
                    break;
                    case SPRITE_STATE_SNAIL_LEFT_TURN :
                    {
                        if (i_type == SPRITE_OBJ_SNAIL_RIGHT)
                        {
                            _collection.activateSprite(SPRITE_OBJ_SNAIL_LEFT, SPRITE_STATE_SNAIL_LEFT_MOVING, _spriteOffset);
                        }
                        else
                        {
                            _collection.activateSprite(SPRITE_OBJ_SNAIL_RIGHT, SPRITE_STATE_SNAIL_LEFT_MOVING, _spriteOffset);
                        }
                    }
                    ;
                    break;
                }
            }
            ;
            break;
        }
    }

    /**
     * Уведомление о прохождении спрайтом определенной точки пути
     *
     * @param _controller
     */
    public static final void notifyPathPointPassed(PathController _controller)
    {
        switch (_controller.i_PathControllerID)
        {
            case PATH_WormMovePath:
            {
                SpriteCollection p_snails = ap_SpriteCollections[COLLECTION_SNAILS];
                int i_sprite = _controller.i_spriteOffset;
                p_snails.activateSprite(p_snails.getSpriteType(i_sprite), SPRITE_STATE_SNAIL_RIGHT_TURN, i_sprite);
                p_snails.setSpriteNextTypeState(i_sprite, SpriteCollection.SPRITEBEHAVIOUR_NOTIFY);
                _controller.lg_Paused = true;
            }
            ;
            break;
        }
    }

    /**
     * Уведомление об окончании прохождения пути спрайтом
     *
     * @param _controller
     */
    public static final void notifyPathCompleted(PathController _controller)
    {
        switch (_controller.i_PathControllerID)
        {
            case PATH_ScorePath:
            {
                _controller.realiseSprite();
            }
            ;
            break;
            case PATH_PlayerLeftJump:
            {
                SpriteCollection p_col = ap_SpriteCollections[COLLECTION_PLAYER];
                p_col.activateSprite(p_col.getSpriteType(0), SPRITE_STATE_PLAYER_EMPTY_STANDLEFT, 0);
            }
            ;
            break;
            case PATH_PlayerRightJump:
            {
                SpriteCollection p_col = ap_SpriteCollections[COLLECTION_PLAYER];
                p_col.activateSprite(p_col.getSpriteType(0), SPRITE_STATE_PLAYER_EMPTY_STANDRIGHT, 0);
            }
            ;
            break;
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

        // Обработка анимаций
        SpriteCollection [] ap_sprites = ap_SpriteCollections;
        for (int li = 0; li < 4; li++) ap_sprites[li].processAnimationForActiveSprites();

        // Обработка путей
        PathController [] ap_paths = ap_Paths;
        for (int li = 0; li < MAXPATHS; li++) if (!ap_paths[li].lg_Completed) ap_paths[li].processStep();

        SpriteCollection p_playerCol = ap_SpriteCollections[COLLECTION_PLAYER];

        boolean lg_playerCanBeEaten = false;

        int i_playerType = p_playerCol.getSpriteType(0);
        switch (i_playerType)
        {
            case SPRITE_OBJ_PLAYER_EMPTY:
            case SPRITE_OBJ_PLAYER_FULL:
            {
                // Игрок жив
                int i_playerState = p_playerCol.getSpriteState(0);

                switch (i_playerState)
                {
                    case SPRITE_STATE_PLAYER_EMPTY_JUMPLEFT:
                    case SPRITE_STATE_PLAYER_EMPTY_JUMPRIGHT:
                    {
                        if (checkIntersectionAndAlign(true) != 0)
                        {
                            int i_s = i_playerState == SPRITE_STATE_PLAYER_EMPTY_JUMPRIGHT ? SPRITE_STATE_PLAYER_EMPTY_STANDRIGHT : SPRITE_STATE_PLAYER_EMPTY_STANDLEFT;
                            p_playerCol.activateSprite(i_playerType, i_s, 0);
                            // Отключаем путь
                            PathController p_path = getPathForSprite(COLLECTION_PLAYER, 0);
                            if (p_path != null)
                            {
                                p_path.lg_Completed = true;
                            }
                            checkIntersectionAndAlign(false);
                        }

                        if (i_playerType == SPRITE_OBJ_PLAYER_EMPTY)
                        {
                            checkBunch();
                        }
                    }
                    ;
                    break;
                    case SPRITE_STATE_PLAYER_EMPTY_MOVINGLEFT:
                    case SPRITE_STATE_PLAYER_EMPTY_STANDLEFT:
                    case SPRITE_STATE_PLAYER_EMPTY_MOVINGRIGHT:
                    case SPRITE_STATE_PLAYER_EMPTY_STANDRIGHT:
                    {
                        int i_dx = 0, i_dy = 0;
                        if ((_keyStateFlags & BUTTON_LEFT) != 0)
                        {
                            i_dx = -I8_PLAYER_HORZ_SPEED;
                        }
                        else if ((_keyStateFlags & BUTTON_RIGHT) != 0)
                        {
                            i_dx = I8_PLAYER_HORZ_SPEED;
                        }
                        i_dy = I8_GRAVITATION;

                        p_playerCol.moveMainPointXY(0, 0, i_dy);
                        if (checkIntersectionAndAlign(true) != 0)
                        {
                            p_playerCol.moveMainPointXY(0, i_dx, 0);
                            int i_intersection = checkIntersectionAndAlign(true);
                            if (i_intersection == 0)
                            {
                                // нет столкновения со стенками
                                if ((_keyStateFlags & (BUTTON_FIRE | BUTTON_UP)) != 0)
                                {
                                    // Прыжок
                                    PathController p_path = getInactivePath();
                                    if (p_path != null)
                                    {
                                        int i_path;
                                        int i_state;

                                        if (i_playerState == SPRITE_STATE_PLAYER_EMPTY_MOVINGLEFT || i_playerState == SPRITE_STATE_PLAYER_EMPTY_STANDLEFT)
                                        {
                                            i_path = PATH_PlayerLeftJump;
                                            i_state = SPRITE_STATE_PLAYER_EMPTY_JUMPLEFT;
                                        }
                                        else
                                        {
                                            i_path = PATH_PlayerRightJump;
                                            i_state = SPRITE_STATE_PLAYER_EMPTY_JUMPRIGHT;
                                        }
                                        int i_mx = p_playerCol.ai_spriteDataArray[SpriteCollection.SPRITEDATAOFFSET_MAINX];
                                        int i_my = p_playerCol.ai_spriteDataArray[SpriteCollection.SPRITEDATAOFFSET_MAINY];

                                        p_playerCol.activateSprite(i_playerType, i_state, 0);
                                        p_path.initPath(i_path, i_mx, i_my, 0x100, 0x100, ap_sprites, COLLECTION_PLAYER, 0, ash_Paths, i_path, 0, 0, 0);
                                    }
                                }
                                else
                                {
                                    // игрок на поверхности
                                    lg_playerCanBeEaten = true;
                                    if (i_dx == 0)
                                    {
                                        if (i_playerState != SPRITE_STATE_PLAYER_EMPTY_STANDLEFT && i_playerState != SPRITE_STATE_PLAYER_EMPTY_STANDRIGHT)
                                        {
                                            if (i_playerState == SPRITE_STATE_PLAYER_EMPTY_MOVINGLEFT)
                                            {
                                                p_playerCol.activateSprite(i_playerType, SPRITE_STATE_PLAYER_EMPTY_STANDLEFT, 0);
                                                checkIntersectionAndAlign(false);
                                            }
                                            else if (i_playerState == SPRITE_STATE_PLAYER_EMPTY_MOVINGRIGHT)
                                            {
                                                p_playerCol.activateSprite(i_playerType, SPRITE_STATE_PLAYER_EMPTY_STANDRIGHT, 0);
                                                checkIntersectionAndAlign(false);
                                            }
                                        }
                                    }
                                    else
                                    {
                                        if (i_dx < 0)
                                        {
                                            if (i_playerState != SPRITE_STATE_PLAYER_EMPTY_MOVINGLEFT)
                                            {
                                                p_playerCol.activateSprite(i_playerType, SPRITE_STATE_PLAYER_EMPTY_MOVINGLEFT, 0);
                                                checkIntersectionAndAlign(false);
                                            }
                                        }
                                        else
                                        {
                                            if (i_playerState != SPRITE_STATE_PLAYER_EMPTY_MOVINGRIGHT)
                                            {
                                                p_playerCol.activateSprite(i_playerType, SPRITE_STATE_PLAYER_EMPTY_MOVINGRIGHT, 0);
                                                checkIntersectionAndAlign(false);
                                            }
                                        }
                                    }

                                }
                            }
                            else
                            {
                                // игрок на поверхности
                                lg_playerCanBeEaten = true;

                                // проверка для блокировки анимации
                                if ((i_intersection & FLAGINERSECTION_LEFT) != 0)
                                {
                                    p_playerCol.activateSprite(i_playerType, SPRITE_STATE_PLAYER_EMPTY_STANDLEFT, 0);
                                    p_playerCol.moveMainPointXY(0, 0 - I8_BLOCKWIDTH, 0);
                                    checkIntersectionAndAlign(false);
                                }
                                else
                                {
                                    p_playerCol.activateSprite(i_playerType, SPRITE_STATE_PLAYER_EMPTY_STANDRIGHT, 0);
                                    p_playerCol.moveMainPointXY(0, I8_BLOCKWIDTH, 0);
                                    checkIntersectionAndAlign(false);
                                }
                            }
                        }
                        else
                        {
                            // Игрок падает
                            switch (i_playerState)
                            {
                                case SPRITE_STATE_PLAYER_EMPTY_MOVINGRIGHT:
                                {
                                    p_playerCol.activateSprite(i_playerType, SPRITE_STATE_PLAYER_EMPTY_STANDRIGHT, 0);
                                }
                                ;
                                break;
                                case SPRITE_STATE_PLAYER_EMPTY_MOVINGLEFT:
                                {
                                    p_playerCol.activateSprite(i_playerType, SPRITE_STATE_PLAYER_EMPTY_STANDLEFT, 0);
                                }
                                ;
                                break;
                            }

                            if (i_playerType == SPRITE_OBJ_PLAYER_EMPTY)
                            {
                                checkBunch();
                            }
                        }
                    }
                }

                if (lg_playerCanBeEaten)
                {
                    if (!checkSnails())
                    {
                        if (i_playerType == SPRITE_OBJ_PLAYER_FULL)
                        {
                            checkBasket();
                        }
                        else
                        {
                            checkBunch();
                        }
                    }

                }
            }
            ;
            break;
            case SPRITE_OBJ_PLAYER_WIN:
            case SPRITE_OBJ_PLAYER_KILLED:
            {
                p_playerCol.moveMainPointXY(0, 0, I8_GRAVITATION);
                checkIntersectionAndAlign(true);
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

//todo Константы
    public static final int SCORE_FOR_BUNCH = 100;

    public static final int MAXPLAYEROBJECTS = 2;
    public static final int MAXBUNCHESOBJECTS = 25;
    public static final int MAXBASKETSOBJECTS = 25;
    public static final int MAXSNAILSOBJECTS = 15;

    private static final int I8_GRAVITATION = 6 * startup.SCALE_HEIGHT;
    private static final int I8_PLAYER_HORZ_SPEED = 4 * startup.SCALE_WIDTH;

    /**
     * индекс коллекции спрайта игрока
     */
    public static final int COLLECTION_PLAYER = 0;

    /**
     * индекс коллекции спрайтов корзин
     */
    public static final int COLLECTION_BASKETS = 1;

    /**
     * индекс коллекции улиток
     */
    public static final int COLLECTION_SNAILS = 2;

    /**
     * индекс коллекции винограда
     */
    public static final int COLLECTION_BUNCHES = 3;

    /**
     * Максимальное количество путей в игре
     */
    public static final int MAXPATHS = MAXSNAILSOBJECTS + 3;

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


    /**
     * Ширина поля в ячейках
     */
    public static final int TERRAIN_CELLS_WIDTH = 200;

    /**
     * Высота поля в ячейках
     */
    public static final int TERRAIN_CELLS_HEIGHT = 21;

    private static final int PLAYERINITX = 40;
    private static final int PLAYERINITY = (TERRAIN_CELLS_HEIGHT-2)*16;


    public static final int I8_BLOCKWIDTH = 16 * startup.SCALE_WIDTH;
    public static final int I8_BLOCKHEIGHT = 16 * startup.SCALE_HEIGHT;

    public static final int VIEWCELLWIDTH = (I8_BLOCKWIDTH + 0x7F) >> 8;
    public static final int VIEWCELLHEIGHT = (I8_BLOCKHEIGHT + 0x7F) >> 8;

    public static final int TERRAIN_PIXELS_WIDTH = ((I8_BLOCKWIDTH + 0x7f) >> 8) * TERRAIN_CELLS_WIDTH;
    public static final int TERRAIN_PIXELS_HEIGHT = ((I8_BLOCKHEIGHT + 0x7f) >> 8) * TERRAIN_CELLS_HEIGHT;

//todo Переменные
    /**
     * Массив коллекций спрайтов
     */
    public static SpriteCollection [] ap_SpriteCollections;
    /**
     * Массив контроллеров пути
     */
    private static PathController [] ap_Paths;
    /**
     * Координата X последней взятой грозди
     */
    private static int i8_lastTakenBunchX;
    /**
     * Координата Y последней взятой грозди
     */
    private static int i8_lastTakenBunchY;
    /**
     * Количество пустых корзин на уровне
     */
    public static int i_EmptyBasketsCounter;

//todo Функции

    private static int i8_lastGoodPlayerX;// Последняя безопасная координата игрока X
    private static int i8_lastGoodPlayerY;// Последняя безопасная координата игрока Y

    /**
     * Проверка на взятие игроком винограда
     */
    private static void checkBunch()
    {
        SpriteCollection p_sprCol = ap_SpriteCollections[COLLECTION_BUNCHES];
        SpriteCollection p_player = ap_SpriteCollections[COLLECTION_PLAYER];

        int i_collidedBunch = p_sprCol.findCollidedSprite(0, p_player, -1);
        if (i_collidedBunch < 0) return;

        i_PlayerScore += SCORE_FOR_BUNCH;

        int i_bunchX = p_sprCol.ai_spriteDataArray[i_collidedBunch + SpriteCollection.SPRITEDATAOFFSET_MAINX];
        int i_bunchY = p_sprCol.ai_spriteDataArray[i_collidedBunch + SpriteCollection.SPRITEDATAOFFSET_MAINY];

        // Запоминаем координаты
        i8_lastTakenBunchX = i_bunchX;
        i8_lastTakenBunchY = i_bunchY;

        p_sprCol.releaseSprite(i_collidedBunch);
        p_player.activateSprite(SPRITE_OBJ_PLAYER_FULL, p_player.getSpriteState(0), 0);

        startup.processGameAction(GAMEACTION_PLAYERTAKEBUNCH);

        final int i_scoreOffset = SpriteCollection.getOffsetForSpriteWithIndex(1);

        // генерируем картинку очков
        PathController p_pathcontr = getPathForSprite(COLLECTION_PLAYER, i_scoreOffset);
        if (p_pathcontr == null)
        {
            p_pathcontr = getInactivePath();
        }

        if (p_pathcontr != null)
        {
            p_player.activateSprite(SPRITE_OBJ_SCORE, SPRITE_STATE_SCORE_100, i_scoreOffset);
            p_pathcontr.initPath(PATH_ScorePath, i_bunchX, i_bunchY, 0x100, 0x100, ap_SpriteCollections, COLLECTION_PLAYER, i_scoreOffset, ash_Paths, PATH_ScorePath, 0, 0, 0);
        }
    }

    /**
     * Проверка на корзину
     */
    private static void checkBasket()
    {
        SpriteCollection p_sprCol = ap_SpriteCollections[COLLECTION_BASKETS];
        SpriteCollection p_player = ap_SpriteCollections[COLLECTION_PLAYER];

        int i_collidedBasket = p_sprCol.findCollidedSprite(0, p_player, -1);
        if (i_collidedBasket < 0) return;

        if (p_sprCol.getSpriteState(i_collidedBasket) == SPRITE_STATE_BASKET_EMPTY)
        {
            // Загружаем корзину
            p_sprCol.activateSprite(SPRITE_OBJ_BASKET, SPRITE_STATE_BASKET_FULL, i_collidedBasket);

            i_EmptyBasketsCounter--;

            if (i_EmptyBasketsCounter == 0)
            {
                // Уровень пройден
                p_player.activateSprite(SPRITE_OBJ_PLAYER_WIN, SPRITE_STATE_PLAYER_WIN_PLAYED, 0);
                p_player.setSpriteNextTypeState(0, SpriteCollection.SPRITEBEHAVIOUR_NOTIFY);
                startup.processGameAction(GAMEACTION_PLAYERWIN);
            }
            else
            {
                // Разгружаем игрока
                int i_state = p_player.getSpriteState(0);
                p_player.activateSprite(SPRITE_OBJ_PLAYER_EMPTY, i_state, 0);
                startup.processGameAction(GAMEACTION_BASKETNUMBERCHANGED);
            }
        }
    }

    /**
     * @return true если игрок пойман
     */
    private static boolean checkSnails()
    {
        SpriteCollection p_sprCol = ap_SpriteCollections[COLLECTION_SNAILS];
        SpriteCollection p_player = ap_SpriteCollections[COLLECTION_PLAYER];

        int i_collidedSnail = p_sprCol.findCollidedSprite(0, p_player, -1);
        if (i_collidedSnail < 0) return false;

        int i_playerX = p_player.ai_spriteDataArray[SpriteCollection.SPRITEDATAOFFSET_MAINX];
        int i_snailX = p_sprCol.ai_spriteDataArray[i_collidedSnail + SpriteCollection.SPRITEDATAOFFSET_MAINX];

        int i_state = p_sprCol.getSpriteState(i_collidedSnail);
        if (i_state == SPRITE_STATE_SNAIL_RIGHT_MOVING)
        {
            // ставим путь улитки на паузу
            PathController p_path = getPathForSprite(COLLECTION_SNAILS, i_collidedSnail);
            p_path.lg_Paused = true;

            // проверяем позицию игрока и запускаем анимацию укуса
            int i_type = p_sprCol.getSpriteType(i_collidedSnail);
            p_sprCol.setOptionalData(i_collidedSnail, i_type);

            switch (i_type)
            {
                case SPRITE_OBJ_SNAIL_LEFT:
                {
                    if (i_playerX > i_snailX)
                    {
                        p_sprCol.activateSprite(SPRITE_OBJ_SNAIL_RIGHT, SPRITE_STATE_SNAIL_RIGHT_BITE, i_collidedSnail);
                    }
                    else
                    {
                        p_sprCol.activateSprite(SPRITE_OBJ_SNAIL_LEFT, SPRITE_STATE_SNAIL_LEFT_BITE, i_collidedSnail);
                    }
                }
                ;
                break;
                case SPRITE_OBJ_SNAIL_RIGHT:
                {
                    if (i_playerX < i_snailX)
                    {
                        p_sprCol.activateSprite(SPRITE_OBJ_SNAIL_LEFT, SPRITE_STATE_SNAIL_LEFT_BITE, i_collidedSnail);
                    }
                    else
                    {
                        p_sprCol.activateSprite(SPRITE_OBJ_SNAIL_RIGHT, SPRITE_STATE_SNAIL_RIGHT_BITE, i_collidedSnail);
                    }
                }
                ;
                break;
            }
            p_sprCol.setSpriteNextTypeState(i_collidedSnail, SpriteCollection.SPRITEBEHAVIOUR_NOTIFY);
            return true;
        }
        else
        {
            if (i_state == SPRITE_STATE_SNAIL_RIGHT_BITE)
            {
                // если у игрока была ветвь то возвращаем её на место
                if (p_player.getSpriteType(0) == SPRITE_OBJ_PLAYER_FULL)
                {
                    p_sprCol = ap_SpriteCollections[COLLECTION_BUNCHES];
                    int i_lastSprite = p_sprCol.i_lastInactiveSpriteOffset;
                    p_sprCol.activateSprite(SPRITE_OBJ_BUNCH, SPRITE_STATE_BUNCH_PLAYED, i_lastSprite);
                    p_sprCol.setMainPointXY(i_lastSprite, i8_lastTakenBunchX, i8_lastTakenBunchY);
                }

                // Убиваем игрока
                p_player.activateSprite(SPRITE_OBJ_PLAYER_KILLED, SPRITE_STATE_PLAYER_KILLED_PLAYED, 0);
                p_player.setSpriteNextTypeState(0, SpriteCollection.SPRITEBEHAVIOUR_NOTIFY);
                startup.processGameAction(GAMEACTION_PLAYERKILLED);

                return true;
            }
        }
        return false;
    }


    public static final int FLAGINERSECTION_DOWN = 1;
    public static final int FLAGINERSECTION_UP = 2;
    public static final int FLAGINERSECTION_LEFT = 4;
    public static final int FLAGINERSECTION_RIGHT = 8;

    /**
     * Проверка пересечения спрайта с тайлами в массиве
     * v1.00
     *
     * @param _mayUseLastGoodPoint флаг показывающий что можно использовать данные о последней безопасной точке
     * @return false если не было пересечения, true если была коррекция
     */
    private static int checkIntersectionAndAlign(boolean _mayUseLastGoodPoint)
    {
        _mayUseLastGoodPoint = true;
        SpriteCollection p_sprCol = ap_SpriteCollections[COLLECTION_PLAYER];
        int i_xy = p_sprCol.getSpriteScreenXY(0);

        // получаем координаты верхнего левого угла
        int i_scrx = i_xy >> 16;
        int i_scry = (short) i_xy;

        // получаем координаты смещения "горячей" зоны
        i_xy = p_sprCol.getSpriteHotzoneXY(0);
        i_scrx += (i_xy >> 16);
        i_scry += (short) i_xy;

        // получаем ширину и высоту "горячей" зоны
        i_xy = p_sprCol.getSpriteHotzoneWidthHeight(0);
        int i_w = i_xy >>> 16;
        int i_h = i_xy & 0xFFFF;


        // получаем размеры ячеек в пикселях
        final int CELLWIDTH = (I8_BLOCKWIDTH + 0x7F) >> 8;
        final int CELLHEIGHT = (I8_BLOCKHEIGHT + 0x7F) >> 8;


        // выравниваем по краям пространства
        {
            int i_dx = 0;
            int i_dy = 0;

            if (i_scrx<CELLWIDTH) i_dx = CELLWIDTH-i_scrx;
            else
            {
                int i_scrx2 = i_scrx + i_w+1;
                final int RIGHT_BORDER = (TERRAIN_CELLS_WIDTH-1)*CELLWIDTH;
                if (i_scrx2>RIGHT_BORDER) i_dx = RIGHT_BORDER-i_scrx2;
            }

            int i_scry2 = i_scry + i_h+1;
            final int BOTTOM_BORDER = (TERRAIN_CELLS_HEIGHT-1)*CELLHEIGHT;
            if (i_scry2>BOTTOM_BORDER) i_dy = BOTTOM_BORDER-i_scry2;

            if ((i_dx | i_dy)!=0)
            {
                p_sprCol.moveMainPointXY(0, i_dx<<8, i_dy<<8);

                int i_xflag = 0;
                int i_yflag = 0;

                if (i_dx != 0) i_xflag = i_dx > 0 ? FLAGINERSECTION_LEFT : FLAGINERSECTION_RIGHT;
                if (i_dy != 0) i_yflag = i_dy > 0 ? FLAGINERSECTION_UP : FLAGINERSECTION_DOWN;

                i8_lastGoodPlayerX = p_sprCol.ai_spriteDataArray[SpriteCollection.SPRITEDATAOFFSET_MAINX];
                i8_lastGoodPlayerY = p_sprCol.ai_spriteDataArray[SpriteCollection.SPRITEDATAOFFSET_MAINY];

                return i_xflag | i_yflag;
            }
        }

        // Выявляем направление движения
        // i8_lastGoodPlayerX и i8_lastGoodPlayerY - переменные, содержащие последние координаты игрока (безопасные в плане пересечения)
        int i_dx = p_sprCol.ai_spriteDataArray[SpriteCollection.SPRITEDATAOFFSET_MAINX] - i8_lastGoodPlayerX;
        int i_dy = p_sprCol.ai_spriteDataArray[SpriteCollection.SPRITEDATAOFFSET_MAINY] - i8_lastGoodPlayerY;
        // если нет движения то выходим
        if (_mayUseLastGoodPoint && (i_dx | i_dy) == 0) return 0;


        // переменные с результатом смещения
        int i_resultX = 0;
        int i_resultY = 0;

        // делаем массив тайлов локальным, для ускорения
        final byte [] ab_terra = TERRAIN_MAP;

        int i_checkingFlags = 0;

        if (!_mayUseLastGoodPoint)
        {
            // Полная проверка пересечения
            i_checkingFlags = FLAGINERSECTION_DOWN | FLAGINERSECTION_LEFT | FLAGINERSECTION_RIGHT | FLAGINERSECTION_UP;
        }
        else
        {
            i_checkingFlags = (i_dx < 0 ? FLAGINERSECTION_LEFT : i_dx > 0 ? FLAGINERSECTION_RIGHT : 0);
            i_checkingFlags |= (i_dy < 0 ? FLAGINERSECTION_UP : i_dy > 0 ? FLAGINERSECTION_DOWN : 0);
        }

        // проверяем пересечение по вертикальным краям в случае горизонтального перемещения
        int i_checkX;

        int i_maxX;
        int i_minX;
        int i_step;

        int i_celly = i_scry / CELLHEIGHT;

        int i_lenY = (((i_scry + i_h) / CELLHEIGHT) - i_celly) + 1;

        if ((i_checkingFlags & FLAGINERSECTION_LEFT) != 0)
        {
            // Левая кромка
            i_checkX = i_scrx / CELLWIDTH;

            i_maxX = i_scrx;
            i_minX = ((i_checkX + 1) * CELLWIDTH);

            i_step = i_minX - i_maxX;

            int i_cellPos = i_checkX + (i_celly * TERRAIN_CELLS_WIDTH);
            int ly = i_lenY;
            while (ly != 0)
            {
                //Блок проверки выхода за пределы массива, за пределами пространство заполнено
                int i_val = 0;
                if (i_cellPos >= 0 && i_cellPos < ab_terra.length) i_val = ab_terra[i_cellPos];

                if (i_val != 0)
                {
                    // Проверяем тонкое пересечение
                    if (i_maxX < i_minX)
                    {
                        i_resultX = i_step;
                        break;
                    }
                }
                i_cellPos += TERRAIN_CELLS_WIDTH;
                ly--;
            }
        }

        if ((i_checkingFlags & FLAGINERSECTION_RIGHT) != 0)
        {
            // Правая кромка
            i_minX = i_scrx + i_w;
            i_checkX = i_minX / CELLWIDTH;
            i_maxX = (i_checkX * CELLWIDTH) - 1;
            i_step = i_maxX - i_minX;

            int i_cellPos = i_checkX + (i_celly * TERRAIN_CELLS_WIDTH);
            int ly = i_lenY;

            while (ly != 0)
            {
                //Блок проверки выхода за пределы массива, за пределами пространство заполнено
                int i_val = 0;
                if (i_cellPos >= 0 && i_cellPos < ab_terra.length) i_val = ab_terra[i_cellPos];

                if (i_val != 0)
                {
                    // Проверяем тонкое пересечение
                    if (i_maxX < i_minX)
                    {
                        i_resultX = i_step;
                        break;
                    }
                }
                i_cellPos += TERRAIN_CELLS_WIDTH;
                ly--;
            }
        }

        // проверяем пересечение по горизонтальным краям в случае вертикального перемещения
        int i_checkY;

        int i_maxY;
        int i_minY;

        int i_cellx = i_scrx / CELLWIDTH;
        int i_lenX = (((i_scrx + i_w) / CELLWIDTH) - i_cellx) + 1;

        if ((i_checkingFlags & FLAGINERSECTION_UP) != 0)
        {
            // Верхняя кромка
            i_checkY = i_scry / CELLHEIGHT;

            i_maxY = i_scry;
            i_minY = ((i_checkY + 1) * CELLHEIGHT);

            i_step = i_minY - i_maxY;

            int i_cellPos = i_cellx + (i_checkY * TERRAIN_CELLS_WIDTH);
            int lx = i_lenX;
            while (lx != 0)
            {
                //Блок проверки выхода за пределы массива, за пределами пространство заполнено
                int i_val = 0;
                if (i_cellPos >= 0 && i_cellPos < ab_terra.length) i_val = ab_terra[i_cellPos];

                if (i_val != 0)
                {
                    // Проверяем тонкое пересечение
                    if (i_maxY < i_minY)
                    {
                        i_resultY = i_step;
                        break;
                    }
                }
                i_cellPos++;
                lx--;
            }
        }

        if ((i_checkingFlags & FLAGINERSECTION_DOWN) != 0)
        {
            // Нижняя кромка
            i_minY = i_scry + i_h;
            i_checkY = i_minY / CELLHEIGHT;
            i_maxY = i_checkY * CELLHEIGHT - 1;

            i_step = i_maxY - i_minY;

            int i_cellPos = i_cellx + (i_checkY * TERRAIN_CELLS_WIDTH);

            int lx = i_lenX;

            while (lx != 0)
            {
                //Блок проверки выхода за пределы массива, за пределами пространство заполнено
                int i_val = 0;
                if (i_cellPos >= 0 && i_cellPos < ab_terra.length) i_val = ab_terra[i_cellPos];

                if (i_val != 0)
                {
                    // Проверяем тонкое пересечение
                    if (i_maxY < i_minY)
                    {
                        i_resultY = i_step;
                        break;
                    }
                }
                i_cellPos++;
                lx--;
            }
        }

        // Проверяем надо ли сместить объект
        boolean lg_changed = (i_resultX | i_resultY) != 0;
        if (lg_changed)
        {
            i_resultX <<= 8;
            i_resultY <<= 8;
            // Что бы не было мощных искажений координат, проверяем смещение на расстояние до безопасной координаты и берем наименьшее
            if (i_resultX != 0)
            {
                if (Math.abs(i_resultX) > Math.abs(i_dx)) i_resultX = -i_dx;
            }

            if (i_resultY != 0)
            {
                if (Math.abs(i_resultY) > Math.abs(i_dy)) i_resultY = -i_dy;
            }

            p_sprCol.moveMainPointXY(0, i_resultX, i_resultY);
        }

        int i_xflag = 0;
        int i_yflag = 0;

        if (i_resultX != 0) i_xflag = i_resultX > 0 ? FLAGINERSECTION_LEFT : FLAGINERSECTION_RIGHT;
        if (i_resultY != 0) i_yflag = i_resultY > 0 ? FLAGINERSECTION_UP : FLAGINERSECTION_DOWN;


        i8_lastGoodPlayerX = p_sprCol.ai_spriteDataArray[SpriteCollection.SPRITEDATAOFFSET_MAINX];
        i8_lastGoodPlayerY = p_sprCol.ai_spriteDataArray[SpriteCollection.SPRITEDATAOFFSET_MAINY];

        return i_xflag | i_yflag;
    }


    private static void generateEnemyObjects()
    {
        for (int li = 0; li < ap_SpriteCollections.length; li++) ap_SpriteCollections[li].releaseAllSprites();

        int i_snails = 0;
        int i_baskets = 0;

        int i_GenerationPathBaskets = 0;
        int i_GenerationPathSnails = 0;
        int i_GenerationPathBunches = 0;

        switch (i_GameStage)
        {
            case 1:
            {
                i_GenerationPathBaskets = PATH_Lvl1_Baskets;
                i_GenerationPathBunches = PATH_Lvl1_Grapes;
                i_GenerationPathSnails = PATH_Lvl1_Worms;

                i_snails = 8;
                i_baskets = 15;
            }
            ;
            break;
            case 2:
            {
                i_GenerationPathBaskets = PATH_Lvl2_Baskets;
                i_GenerationPathBunches = PATH_Lvl2_Grapes;
                i_GenerationPathSnails = PATH_Lvl2_Worms;

                i_snails = 10;
                i_baskets = 20;
            }
            ;
            break;
            case 3:
            {
                i_GenerationPathBaskets = PATH_Lvl3_Baskets;
                i_GenerationPathBunches = PATH_Lvl3_Grapes;
                i_GenerationPathSnails = PATH_Lvl3_Worms;

                i_snails = 15;
                i_baskets = 25;
            }
            ;
            break;
        }

        int i_bunchNumber = i_baskets;

        final short[] ash_paths = ash_Paths;

        // генерация улиток
        SpriteCollection p_sprCol = ap_SpriteCollections[COLLECTION_SNAILS];

        int i_offset = i_GenerationPathSnails;
        int i_maxNumber = ash_paths[i_offset] + 1;
        boolean [] alg_points = new boolean[i_maxNumber];

        //#-
        System.out.println("Generating SNAILS points " + i_maxNumber + " number of objects " + i_snails);
        //#+


        while (i_snails > 0)
        {
            for (int li = 0; li < i_maxNumber; li++)
            {
                if (!alg_points[li] && getRandomInt(1000) < 200)
                {
                    int i_off = i_offset + 2 + (li << 1);
                    int i8_x = ash_paths[i_off++];
                    int i8_y = ash_paths[i_off];

                    long l_xy = calculatePositionXY(i8_x, i8_y, ANCHOR_BOTTOM);
                    i8_x = (int) (l_xy >>> 32);
                    i8_y = (int) l_xy;

                    int i_spr = p_sprCol.i_lastInactiveSpriteOffset;
                    PathController p_path = getInactivePath();

                    p_sprCol.activateSprite(SPRITE_OBJ_SNAIL_LEFT, SPRITE_STATE_SNAIL_LEFT_MOVING, i_spr);
                    p_path.initPath(PATH_WormMovePath, i8_x, i8_y, 0x100, 0x100, ap_SpriteCollections, COLLECTION_SNAILS, i_spr, ash_paths, PATH_WormMovePath, 0, 0, 0);

                    alg_points[li] = true;

                    i_snails--;

                    break;
                }
            }
        }

        // Выставляем количество пустых корзин
        i_EmptyBasketsCounter = i_baskets;

        // генерация корзин
        i_offset = i_GenerationPathBaskets;
        i_maxNumber = ash_paths[i_offset] + 1;
        alg_points = new boolean[i_maxNumber];
        p_sprCol = ap_SpriteCollections[COLLECTION_BASKETS];

        //#-
        System.out.println("Generating BASKETS points " + i_maxNumber + " number of objects " + i_baskets);
        //#+

        while (i_baskets > 0)
        {
            for (int li = 0; li < i_maxNumber; li++)
            {
                if (!alg_points[li] && getRandomInt(1000) < 200)
                {
                    int i_off = i_offset + 2 + (li << 1);
                    int i8_x = ash_paths[i_off++];
                    int i8_y = ash_paths[i_off];

                    long l_xy = calculatePositionXY(i8_x, i8_y, ANCHOR_BOTTOM);
                    i8_x = (int) (l_xy >>> 32);
                    i8_y = (int) l_xy;

                    int i_spr = p_sprCol.i_lastInactiveSpriteOffset;

                    p_sprCol.activateSprite(SPRITE_OBJ_BASKET, SPRITE_STATE_BASKET_EMPTY, i_spr);
                    p_sprCol.setMainPointXY(i_spr, i8_x, i8_y);

                    alg_points[li] = true;

                    i_baskets--;

                    break;
                }
            }
        }

        // генерация гроздей
        i_offset = i_GenerationPathBunches;
        i_maxNumber = ash_paths[i_offset] + 1;
        alg_points = new boolean[i_maxNumber];
        p_sprCol = ap_SpriteCollections[COLLECTION_BUNCHES];

        //#-
        System.out.println("Generating BUNCHES points " + i_maxNumber + " number of objects " + i_bunchNumber);
        //#+

        while (i_bunchNumber > 0)
        {
            for (int li = 0; li < i_maxNumber; li++)
            {
                if (!alg_points[li] && getRandomInt(1000) < 200)
                {
                    int i_off = i_offset + 2 + (li << 1);
                    int i8_x = ash_paths[i_off++];
                    int i8_y = ash_paths[i_off];

                    long l_xy = calculatePositionXY(i8_x, i8_y, ANCHOR_TOP);
                    i8_x = (int) (l_xy >>> 32);
                    i8_y = (int) l_xy;

                    int i_spr = p_sprCol.i_lastInactiveSpriteOffset;

                    p_sprCol.activateSprite(SPRITE_OBJ_BUNCH, SPRITE_STATE_BUNCH_PLAYED, i_spr);
                    p_sprCol.setMainPointXY(i_spr, i8_x, i8_y);

                    alg_points[li] = true;

                    i_bunchNumber--;

                    break;
                }
            }
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

    private static PathController getPathForSprite(int _collectionID, int _spriteOffset)
    {
        PathController [] ap_paths = ap_Paths;
        for (int li = 0; li < MAXPATHS; li++)
        {
            PathController p_path = ap_paths[li];
            if (p_path.lg_Completed) continue;
            if (p_path.i_spriteCollectionID == _collectionID && p_path.i_spriteOffset == _spriteOffset) return p_path;
        }
        return null;
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

    /**
     * Флаг отсутствия выравнивания в ячейке
     */
    private static final int ANCHOR_NONE = 0;

    /**
     * Флаг ыравнивания в ячейке по левому краю (объект будет прижат к левому краю)
     */
    private static final int ANCHOR_LEFT = 1;

    /**
     * Флаг ыравнивания в ячейке по правому краю (объект будет прижат к правому краю)
     */
    private static final int ANCHOR_RIGHT = 2;

    /**
     * Флаг ыравнивания в ячейке по верхнему краю (объект будет прижат к верхнему краю)
     */
    private static final int ANCHOR_TOP = 4;

    /**
     * Флаг ыравнивания в ячейке по нижнему краю (объект будет прижат к нижнему краю)
     */
    private static final int ANCHOR_BOTTOM = 8;

    /**
     * Расчет и выравнивания позиции точки в пережатом тайловом массиве
     *
     * @param _x      кордината X точки в оригинальных координатах (в пикселях)
     * @param _y      координата Y точки в оргинилальных координатах (в пикселях)
     * @param _anchor указатель выравнивания точки по краю ячейки
     * @return упакованное значение LONG, в старших 32-х разрядах значение X, а в младших 32-х разрядах Y (оба значения в I8)
     */
    private static long calculatePositionXY(int _x, int _y, int _anchor)
    {
        final int ORIGCELLHEIGHT = 16;// Оригинальная (не пережатая) высота ячейки в пикселях
        final int ORIGCELLWIDTH = 16;// Оригинальная (не пережатая) ширина ячейки в пикселях

        final int VIEWCELLWIDTH = ((ORIGCELLWIDTH * startup.SCALE_WIDTH) + 0x7F) >> 8; // Пережатая ширина ячейки
        final int VIEWCELLHEIGHT = ((ORIGCELLHEIGHT * startup.SCALE_HEIGHT) + 0x7F) >> 8; // Пережатая высота ячейки

        // Вычисляем координаты ячейки, в которой эта точка была бы в оригинале
        int i_cellX = _x / ORIGCELLWIDTH;
        int i_cellY = _y / ORIGCELLHEIGHT;
        int i_cellXoffst = _x % ORIGCELLWIDTH;
        int i_cellYoffst = _y % ORIGCELLHEIGHT;

        // Вычисляем новые координаты для пережатого пространства
        i_cellX = i_cellX * VIEWCELLWIDTH;
        i_cellY = i_cellY * VIEWCELLHEIGHT;

        if ((_anchor & ANCHOR_LEFT) != 0)
        {
            i_cellX <<= 8;
        }
        else if ((_anchor & ANCHOR_RIGHT) != 0)
        {
            i_cellX += VIEWCELLWIDTH;
            i_cellX <<= 8;
        }
        else
        {
            i_cellX = (i_cellX << 8) + (i_cellXoffst * startup.SCALE_WIDTH);
        }

        if ((_anchor & ANCHOR_TOP) != 0)
        {
            i_cellY <<= 8;
        }
        else if ((_anchor & ANCHOR_BOTTOM) != 0)
        {
            i_cellY += VIEWCELLHEIGHT;
            i_cellY <<= 8;
        }
        else
        {
            i_cellY = (i_cellY << 8) + (i_cellYoffst * startup.SCALE_HEIGHT);
        }

        return ((long) (i_cellX & 0xFFFFFF00) << 32) | ((long) (i_cellY & 0xFFFFFF00) & 0xFFFFFFFFl);
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
    /**
     * Смена количества пустых корзин
     */
    protected static final int GAMEACTION_BASKETNUMBERCHANGED = 0;

    /**
     * Смена количества попыток
     */
    protected static final int GAMEACTION_ATTEMPTIONSCHANGED = 1;

    /**
     * Игрок убит
     */
    protected static final int GAMEACTION_PLAYERKILLED = 2;

    /**
     * Игрок выиграл
     */
    protected static final int GAMEACTION_PLAYERWIN = 3;

    /**
     * Игрок сорвал гроздь
     */
    protected static final int GAMEACTION_PLAYERTAKEBUNCH = 4;

//todo Массивы
    protected static short [] ash_SpritesTable;

    // The array contains values for path controllers
    public static short [] ash_Paths;

    // PATH offsets
    private static final int PATH_Lvl1_Grapes = 0;
    private static final int PATH_Lvl1_Baskets = 62;
    private static final int PATH_Lvl1_Worms = 114;
    private static final int PATH_Lvl2_Grapes = 140;
    private static final int PATH_Lvl2_Baskets = 216;
    private static final int PATH_Lvl2_Worms = 284;
    private static final int PATH_Lvl3_Grapes = 316;
    private static final int PATH_Lvl3_Baskets = 410;
    private static final int PATH_Lvl3_Worms = 492;
    private static final int PATH_WormMovePath = 534;
    private static final int PATH_PlayerLeftJump = 542;
    private static final int PATH_PlayerRightJump = 568;
    private static final int PATH_ScorePath = 594;

    public static byte [] TERRAIN_MAP;

//------------------Sprite constants-----------------
    public static final int SPRITE_OBJ_PLAYER_EMPTY = 0;
    public static final int SPRITE_STATE_PLAYER_EMPTY_MOVINGLEFT = 0;
    public static final int SPRITE_STATE_PLAYER_EMPTY_MOVINGRIGHT = 1;
    public static final int SPRITE_STATE_PLAYER_EMPTY_STANDLEFT = 2;
    public static final int SPRITE_STATE_PLAYER_EMPTY_STANDRIGHT = 3;
    public static final int SPRITE_STATE_PLAYER_EMPTY_JUMPLEFT = 4;
    public static final int SPRITE_STATE_PLAYER_EMPTY_JUMPRIGHT = 5;
    public static final int SPRITE_OBJ_PLAYER_FULL = 1;
    public static final int SPRITE_STATE_PLAYER_FULL_MOVINGLEFT = 0;
    public static final int SPRITE_STATE_PLAYER_FULL_MOVINGRIGHT = 1;
    public static final int SPRITE_STATE_PLAYER_FULL_STANDLEFT = 2;
    public static final int SPRITE_STATE_PLAYER_FULL_STANDRIGHT = 3;
    public static final int SPRITE_STATE_PLAYER_FULL_JUMPLEFT = 4;
    public static final int SPRITE_STATE_PLAYER_FULL_JUMPRIGHT = 5;
    public static final int SPRITE_OBJ_PLAYER_KILLED = 2;
    public static final int SPRITE_STATE_PLAYER_KILLED_PLAYED = 0;
    public static final int SPRITE_OBJ_PLAYER_WIN = 3;
    public static final int SPRITE_STATE_PLAYER_WIN_PLAYED = 0;
    public static final int SPRITE_OBJ_BASKET = 4;
    public static final int SPRITE_STATE_BASKET_EMPTY = 0;
    public static final int SPRITE_STATE_BASKET_FULL = 1;
    public static final int SPRITE_OBJ_SCORE = 5;
    public static final int SPRITE_STATE_SCORE_100 = 0;
    public static final int SPRITE_OBJ_BUNCH = 6;
    public static final int SPRITE_STATE_BUNCH_PLAYED = 0;
    public static final int SPRITE_OBJ_SNAIL_LEFT = 7;
    public static final int SPRITE_STATE_SNAIL_LEFT_MOVING = 0;
    public static final int SPRITE_STATE_SNAIL_LEFT_TURN = 1;
    public static final int SPRITE_STATE_SNAIL_LEFT_BITE = 2;
    public static final int SPRITE_OBJ_SNAIL_RIGHT = 8;
    public static final int SPRITE_STATE_SNAIL_RIGHT_MOVING = 0;
    public static final int SPRITE_STATE_SNAIL_RIGHT_TURN = 1;
    public static final int SPRITE_STATE_SNAIL_RIGHT_BITE = 2;
}

