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
            ash_SpritesTable = loadSpriteArray(_class,"/spr.bin");
            ash_Paths = (short[])startup.loadArray(_class,"/paths.bin");
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
        ash_Paths = null;
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
                i_DelayEnemyFiring = ENEMYFIRINGDELAY_EASY;
                i_DelayEnemyGenerateCopter = ENEMYCOPTERINTERVAL_EASY;
                i_DelayEnemyGenerateDerigible = DERIGIBLEINTERVAL_EASY;
                i_DelayEnemyGeneratePlane = ENEMYPLANEINTERVAL_EASY;
            }
            ;
            break;
            case GAMELEVEL_NORMAL:
            {
                i_PlayerAttemptions = ATTEMPTIONS_NORMAL;
                i_DelayEnemyFiring = ENEMYFIRINGDELAY_NORMAL;
                i_DelayEnemyGenerateCopter = ENEMYCOPTERINTERVAL_NORMAL;
                i_DelayEnemyGenerateDerigible = DERIGIBLEINTERVAL_NORMAL;
                i_DelayEnemyGeneratePlane = ENEMYPLANEINTERVAL_NORMAL;
            }
            ;
            break;
            case GAMELEVEL_HARD:
            {
                i_PlayerAttemptions = ATTEMPTIONS_HARD;
                i_DelayEnemyFiring = ENEMYFIRINGDELAY_HARD;
                i_DelayEnemyGenerateCopter = ENEMYCOPTERINTERVAL_HARD;
                i_DelayEnemyGenerateDerigible = DERIGIBLEINTERVAL_HARD;
                i_DelayEnemyGeneratePlane = ENEMYPLANEINTERVAL_HARD;
            }
            ;
            break;
            default:
                return false;
        }

        i_PlayerScore = 0;

        ap_SpriteCollections = new SpriteCollection[5];
        ap_SpriteCollections[COLLECTION_PLAYER] = new SpriteCollection(COLLECTION_PLAYER, 1, ash_SpritesTable);
        ap_SpriteCollections[COLLECTION_EXPLOSIONS] = new SpriteCollection(COLLECTION_EXPLOSIONS, MAXEXPLOSIONSNUMBER, ash_SpritesTable);
        ap_SpriteCollections[COLLECTION_FLYINGENEMYOBJECTS] = new SpriteCollection(COLLECTION_FLYINGENEMYOBJECTS, MAXFLYINGENEMIES, ash_SpritesTable);
        ap_SpriteCollections[COLLECTION_GROUNDENEMYOBJECTS] = new SpriteCollection(COLLECTION_GROUNDENEMYOBJECTS, MAXGROUNDENEMIES, ash_SpritesTable);
        ap_SpriteCollections[COLLECTION_PLAYERBOMBS] = new SpriteCollection(COLLECTION_PLAYERBOMBS, MAXPLAYERBOMBS, ash_SpritesTable);

        ap_Paths = new PathController[MAXPATHS];
        for (int li = 0; li < MAXPATHS; li++)
        {
            ap_Paths[li] = new PathController();
        }

        i8_TerrainPos = 0;

        deactivateAllPaths();
        generateEnemyObjects();
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

        // инициализируем игрока
        SpriteCollection p_spr = ap_SpriteCollections[COLLECTION_PLAYER];
        p_spr.activateSprite(SPRITE_OBJ_PLAYER, SPRITE_STATE_PLAYER_FLYING, 0);

        PathController p_path = getInactivePath();
        p_path.initPath(PATH_Player_In, 0, 0, 0x100, 0x100, ap_SpriteCollections, COLLECTION_PLAYER, 0, ash_Paths, PATH_Player_In, 0, 0, 0);

        i_GameSubstate = SUBSTATE_PLAYERIN;
        i_PlayerCantBeKilledCounter = CANTKILLEDTICKS;

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
        i_PlayerScore = p_dis.readInt();

        i_DeregibleGenerationCounter = p_dis.readInt();
        i_EnemyCopterGenerationCounter = p_dis.readInt();
        i_EnemyPlaneGenerationCounter = p_dis.readInt();
        i_GameSubstate = p_dis.readInt();
        i_PlayerCantBeKilledCounter = p_dis.readInt();
        i8_TerrainPos = p_dis.readInt();

        ap_SpriteCollections[COLLECTION_FLYINGENEMYOBJECTS].loadFromStream(p_dis);
        ap_SpriteCollections[COLLECTION_GROUNDENEMYOBJECTS].loadFromStream(p_dis);
        ap_SpriteCollections[COLLECTION_PLAYER].loadFromStream(p_dis);
        ap_SpriteCollections[COLLECTION_PLAYERBOMBS].loadFromStream(p_dis);

        for(int li=0;li<MAXPATHS;li++)
        {
            ap_Paths[li].readPathFromStream(p_dis,ap_SpriteCollections,ash_Paths);
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

        p_dos.writeInt(i_DeregibleGenerationCounter);
        p_dos.writeInt(i_EnemyCopterGenerationCounter);
        p_dos.writeInt(i_EnemyPlaneGenerationCounter);
        p_dos.writeInt(i_GameSubstate);
        p_dos.writeInt(i_PlayerCantBeKilledCounter);
        p_dos.writeInt(i8_TerrainPos);

        ap_SpriteCollections[COLLECTION_FLYINGENEMYOBJECTS].saveToStream(p_dos);
        ap_SpriteCollections[COLLECTION_GROUNDENEMYOBJECTS].saveToStream(p_dos);
        ap_SpriteCollections[COLLECTION_PLAYER].saveToStream(p_dos);
        ap_SpriteCollections[COLLECTION_PLAYERBOMBS].saveToStream(p_dos);

        for(int li=0;li<MAXPATHS;li++)
        {
            ap_Paths[li].writePathToStream(p_dos);
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
        int i_Size = 16 + 8;
        //------------Вставьте свой код здесь--------------------
        i_Size += 28;
        i_Size += SpriteCollection.getDataSize(MAXFLYINGENEMIES);
        i_Size += SpriteCollection.getDataSize(MAXGROUNDENEMIES);
        i_Size += SpriteCollection.getDataSize(MAXPLAYERBOMBS);
        i_Size += SpriteCollection.getDataSize(1);
        i_Size += (MAXPATHS*PathController.DATASIZE_BYTES);
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
        return "SIM_CPTR";
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
     * @param _collection коллекция
     * @param _spriteOffset смещение спрайта в коллекции
     */
    public static final void notifySpriteAnimationCompleted(SpriteCollection _collection, int _spriteOffset)
    {
        int i_type = _collection.getSpriteType(_spriteOffset);
        int i_state = _collection.getSpriteState(_spriteOffset);
        boolean lg_activatePath = false;
        switch(i_type)
        {
            case SPRITE_OBJ_AAGUN :
            {
                switch(i_state)
                {
                    case SPRITE_STATE_AAGUN_TURNTOLEFT :
                    {
                        _collection.activateSprite(SPRITE_OBJ_AAGUN,SPRITE_STATE_AAGUN_MOVINGLEFT,_spriteOffset);
                        lg_activatePath = true;
                    };break;
                    case SPRITE_STATE_AAGUN_TURNTORIGHT :
                    {
                        _collection.activateSprite(SPRITE_OBJ_AAGUN,SPRITE_STATE_AAGUN_MOVINGRIGHT,_spriteOffset);
                        lg_activatePath = true;
                    };break;
                }
            };break;
            case SPRITE_OBJ_TANK :
            {
                switch(i_state)
                {
                    case SPRITE_STATE_TANK_TURNTOLEFT :
                    {
                        _collection.activateSprite(SPRITE_OBJ_TANK,SPRITE_STATE_TANK_MOVINGLEFT,_spriteOffset);
                        lg_activatePath = true;
                    };break;
                    case SPRITE_STATE_TANK_TURNTORIGHT :
                    {
                        _collection.activateSprite(SPRITE_OBJ_TANK,SPRITE_STATE_TANK_MOVINGRIGHT,_spriteOffset);
                        lg_activatePath = true;
                    };break;
                }
            };break;
        }
        if (lg_activatePath)
        {
            PathController p_path = getPathForSprite(COLLECTION_GROUNDENEMYOBJECTS,_spriteOffset);
            if (p_path!=null) p_path.lg_Paused = false;
        }
    }

    /**
     * Уведомление о прохождении спрайтом определенной точки пути
     *
     * @param _controller
     */
    public static final void notifyPathPointPassed(PathController _controller)
    {
        SpriteCollection p_spr = ap_SpriteCollections[_controller.i_spriteCollectionID];
        int i_sprOffset = _controller.i_spriteOffset;
        int i_type = p_spr.getSpriteType(i_sprOffset);
        int i_state = p_spr.getSpriteState(i_sprOffset);

        switch(i_type)
        {
            case SPRITE_OBJ_AAGUN :
            {
                _controller.lg_Paused = true;

                switch(i_state)
                {
                    case SPRITE_STATE_AAGUN_MOVINGLEFT :
                    {
                        p_spr.activateSprite(SPRITE_OBJ_AAGUN,SPRITE_STATE_AAGUN_TURNTORIGHT,i_sprOffset);
                        p_spr.setSpriteNextTypeState(i_sprOffset,SpriteCollection.SPRITEBEHAVIOUR_NOTIFY);
                    };break;
                    case SPRITE_STATE_AAGUN_MOVINGRIGHT :
                    {
                        p_spr.activateSprite(SPRITE_OBJ_AAGUN,SPRITE_STATE_AAGUN_TURNTOLEFT,i_sprOffset);
                        p_spr.setSpriteNextTypeState(i_sprOffset,SpriteCollection.SPRITEBEHAVIOUR_NOTIFY);
                    };break;
                }

            };break;
            case SPRITE_OBJ_TANK :
            {
                _controller.lg_Paused = true;

                switch(i_state)
                {
                    case SPRITE_STATE_TANK_MOVINGLEFT :
                    {
                        p_spr.activateSprite(SPRITE_OBJ_TANK,SPRITE_STATE_TANK_TURNTORIGHT,i_sprOffset);
                        p_spr.setSpriteNextTypeState(i_sprOffset,SpriteCollection.SPRITEBEHAVIOUR_NOTIFY);
                    };break;
                    case SPRITE_STATE_TANK_MOVINGRIGHT :
                    {
                        p_spr.activateSprite(SPRITE_OBJ_TANK,SPRITE_STATE_TANK_TURNTOLEFT,i_sprOffset);
                        p_spr.setSpriteNextTypeState(i_sprOffset,SpriteCollection.SPRITEBEHAVIOUR_NOTIFY);
                    };break;
                }
            };break;
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
            case PATH_Player_Bomb_move :
            {
                ap_SpriteCollections[_controller.i_spriteCollectionID].releaseSprite(_controller.i_spriteOffset);
            }
            ;
            break;
            case PATH_Player_In :
            {
                i_GameSubstate = SUBSTATE_PLAYERPLAYING;
            }
            ;
            break;
            case PATH_Player_Out :
            {
                i_PlayerState = PLAYER_WIN;
                i_GameState = STATE_OVER;
            }
            ;
            break;
            case PATH_Enemy_missile_move_to_left :
            case PATH_Enemy_missile_move_to_right_up :
            case PATH_Enemy_missile_move_to_left_up :
            case PATH_Enemy_missile_move_to_up :
            case PATH_Enemy_Rocket_move :
            {
                i_PlayerScore += SCORE_ENEMYFIRING;
            }
            default:
            {
                SpriteCollection p_col = ap_SpriteCollections[_controller.i_spriteCollectionID];
                p_col.releaseSprite(_controller.i_spriteOffset);
            }
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
        //------------Вставьте свой код здесь--------------------
        startup.i_KeyFlags &= ~BUTTON_FIRE;

        // Уменьшение счетчика неубиваемости игрока
        if (i_PlayerCantBeKilledCounter>0) i_PlayerCantBeKilledCounter--;


        // сдвиг террейна
        i8_TerrainPos += I8_TERRAIN_SPEED;
        if (i8_TerrainPos>=((VIEWCELLWIDTH*(TERRAIN_CELLS_WIDTH>>1))<<8)) i8_TerrainPos-=((VIEWCELLWIDTH*(TERRAIN_CELLS_WIDTH>>1))<<8);

        offsetStaticObjects();

        // отработка анимаций
        for (int li = 0; li < ap_SpriteCollections.length; li++)
        {
            ap_SpriteCollections[li].processAnimationForActiveSprites();
        }

        // отработка бомб игрока
        processPlayerBombs();

        // отработка путей
        for (int li = 0; li < MAXPATHS; li++)
        {
            PathController p_path = ap_Paths[li];
            p_path.processStep();
            if (p_path.lg_Completed) continue;

            // Смещаем пути от статических объектов протисника
            switch (p_path.i_PathControllerID)
            {
                case PATH_Enemy_missile_move_to_right_up:
                case PATH_Enemy_missile_move_to_left_up:
                case PATH_Enemy_missile_move_to_up:
                case PATH_Enemy_Rocket_move:
                {
                    p_path.moveMainPoint(0 - I8_TERRAIN_SPEED, 0);
                }
                ;
                break;
            }
        }

        switch (i_GameSubstate)
        {
            case SUBSTATE_PLAYERIN :
            {
                processGroundObjects();
            }
            ;
            break;
            case SUBSTATE_PLAYEROUT :
            {

            }
            ;
            break;
            case SUBSTATE_PLAYERPLAYING :
            {
                SpriteCollection p_spr = ap_SpriteCollections[COLLECTION_PLAYER];

                // проверка на столкновение игрока с землей
                if ((i_PlayerCantBeKilledCounter==0 && checkPlayerWithObjects()) || checkSpriteCollisionWithTerrain(p_spr, 0))
                {
                    // Столкнулись, переводим во взрыв
                    startup.processGameAction(GAMEACTION_EXPLOSION);
                    p_spr.activateSprite(SPRITE_OBJ_EXPLOSION, SPRITE_STATE_EXPLOSION_MOVE, 0);
                    p_spr.setSpriteNextTypeState(0, SpriteCollection.SPRITEBEHAVIOUR_RELEASE);
                    i_GameSubstate = SUBSTATE_PLAYERKILLED;
                }
                else
                {
                    generateFiringOfFlyingObjects();

                    if (i_DeregibleGenerationCounter == 0)
                    {
                        if (generateDerigible())
                        {
                            i_DeregibleGenerationCounter = i_DelayEnemyGenerateDerigible;
                        }
                    }
                    else
                        i_DeregibleGenerationCounter--;

                    if (i_EnemyCopterGenerationCounter == 0)
                    {
                        if (generateEnemyCopter())
                        {
                            i_EnemyCopterGenerationCounter = i_DelayEnemyGenerateCopter;
                        }
                    }
                    else
                        i_EnemyCopterGenerationCounter--;

                    if (i_EnemyPlaneGenerationCounter == 0)
                    {
                        if (generateEnemyPlane())
                        {
                            i_EnemyPlaneGenerationCounter = i_DelayEnemyGeneratePlane;
                        }
                    }
                    else
                        i_EnemyPlaneGenerationCounter--;

                    // отработка движений игрока
                    if ((_keyStateFlags & BUTTON_DOWN) != 0)
                    {
                        // вниз
                        p_spr.moveMainPointXY(0, 0, PLAYERVERTSPEED);
                    }
                    else if ((_keyStateFlags & BUTTON_UP) != 0)
                    {
                        // вверх
                        p_spr.moveMainPointXY(0, 0, 0 - PLAYERVERTSPEED);
                    }
                    else
                    {
                        // постоянное смещение
                        p_spr.moveMainPointXY(0, 0, COPTER_ALT_DECREMENT);
                    }

                    // отработка движений игрока
                    if ((_keyStateFlags & BUTTON_LEFT) != 0)
                    {
                        // вниз
                        p_spr.moveMainPointXY(0, 0 - PLAYERHORZSPEED, 0);
                    }
                    else if ((_keyStateFlags & BUTTON_RIGHT) != 0)
                    {
                        // вверх
                        p_spr.moveMainPointXY(0, PLAYERHORZSPEED, 0);
                    }

                    // выравнивание игрока по экрану
                    p_spr.alignSpriteToArea(0, 0, 0, 176 * startup.SCALE_WIDTH, 188 * startup.SCALE_HEIGHT, true);

                    if ((_keyStateFlags & BUTTON_FIRE) != 0)
                    {
                        // попытка сброса бомбы
                        SpriteCollection p_bombs = ap_SpriteCollections[COLLECTION_PLAYERBOMBS];
                        int i_spr = p_bombs.i_lastInactiveSpriteOffset;
                        if (i_spr >= 0)
                        {
                            PathController p_path = getInactivePath();
                            if (p_path != null)
                            {
                                int i_x = p_spr.ai_spriteDataArray[SpriteCollection.SPRITEDATAOFFSET_MAINX];
                                int i_y = p_spr.ai_spriteDataArray[SpriteCollection.SPRITEDATAOFFSET_MAINY];

                                p_bombs.activateSprite(SPRITE_OBJ_PLAYERBOMB, SPRITE_STATE_PLAYERBOMB_MOVE, i_spr);
                                p_path.initPath(PATH_Player_Bomb_move, i_x, i_y, 0x100, 0x100, ap_SpriteCollections, COLLECTION_PLAYERBOMBS, i_spr, ash_Paths, PATH_Player_Bomb_move, 0, 0, 0);

                                //#if SHOWSYS
                                {
                                    startup.s_CurrentExceptionMessage = null;
                                }
                                //#endif
                            }
                            //#if SHOWSYS
                            else
                            {
                                startup.s_CurrentExceptionMessage = "No bomb path";
                            }
                            //#endif
                        }
                        //#if SHOWSYS
                        else
                        {
                                startup.s_CurrentExceptionMessage = "No bomb spr";
                        }
                        //#endif
                    }

                    processGroundObjects();

                    // Проверка выигрыша
                    SpriteCollection [] ap_sprites = ap_SpriteCollections;
                    if (ap_sprites[COLLECTION_FLYINGENEMYOBJECTS].i_lastActiveSpriteOffset<0)
                        if (ap_sprites[COLLECTION_GROUNDENEMYOBJECTS].i_lastActiveSpriteOffset<0)
                        {
                            // Выигрышная ситуация
                            PathController p_path = getInactivePath();
                            int i_mx = p_spr.ai_spriteDataArray[SpriteCollection.SPRITEDATAOFFSET_MAINX];
                            int i_my = p_spr.ai_spriteDataArray[SpriteCollection.SPRITEDATAOFFSET_MAINY];
                            p_path.initPath(PATH_Player_Out,i_mx,i_my,0x100,0x100,ap_sprites,COLLECTION_PLAYER,0,ash_Paths,PATH_Player_Out,0,0,0);
                            i_GameSubstate = SUBSTATE_PLAYEROUT;
                        }
                }
            }
            ;
            break;
            case SUBSTATE_PLAYERKILLED :
            {
                if (ap_SpriteCollections[COLLECTION_PLAYER].i_lastActiveSpriteOffset == -1)
                {
                    if (i_PlayerAttemptions == 0)
                    {
                        i_PlayerState = PLAYER_LOST;
                        i_GameState = STATE_OVER;
                    }
                    else
                    {
                        i_PlayerAttemptions--;
                        initPlayerForGame(false);
                    }
                }
                else
                {
                    // Смещаем обломки игрока
                    ap_SpriteCollections[COLLECTION_PLAYER].moveMainPointXY(0, 0 - I8_TERRAIN_SPEED, 0);
                }

                processGroundObjects();
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
    /**
     * Очки за уничтожение танка
     */
    private static final int SCORE_TANK = 20;

    /**
     * Очки за уничтожение радара
     */
    private static final int SCORE_RADAR = 5;

    /**
     * Очки за уничтожение склада
     */
    private static final int SCORE_STORE = 10;

    /**
     * Очки за уничтожение зенитки
     */
    private static final int SCORE_AAGUN = 30;

    /**
     * Очки за уничтожение пушки
     */
    private static final int SCORE_GUN = 15;

    /**
     * Очки за уничтожение ракетной шахты
     */
    private static final int SCORE_ROCKETLAUNCHER = 15;

    /**
     * Очки за каждый выстрел противника
     */
    private static final int SCORE_ENEMYFIRING = 2;

    /**
     * Вероятность выстрела противников в случае достижения их таймерами нулевого значения
     */
    private static final int ENEMYFIRINGPROBABILITY = 18;  // 4

    /**
     * Постоянный декремент высоты вертолета за игровой шаг
     */
    private static final int COPTER_ALT_DECREMENT = (0x100 * startup.SCALE_HEIGHT) >> 8;

    /**
     * Вертикальная скорость вертолета игрока
     */
    private static final int PLAYERVERTSPEED = (0x400 * startup.SCALE_HEIGHT) >> 8;

    /**
     * Горизонтальная скорость вертолета игрока
     */
    private static final int PLAYERHORZSPEED = (0x200 * startup.SCALE_WIDTH) >> 8;

    /**
     * Подсостояние игры "игрок вылетает"
     */
    private static final int SUBSTATE_PLAYERIN = 0;

    /**
     * Подсостояние игры "игрок играет"
     */
    private static final int SUBSTATE_PLAYERPLAYING = 1;

    /**
     * Подсостояние игры "игрок убит"
     */
    private static final int SUBSTATE_PLAYERKILLED = 2;

    /**
     * Подсостояние игры "игрок улетает"
     */
    private static final int SUBSTATE_PLAYEROUT = 3;

    /**
     * максимальное количество взрывов
     */
    private static final int MAXEXPLOSIONSNUMBER = 6;

    /**
     * максимальное количество наземных противников
     */
    private static final int MAXGROUNDENEMIES = 40;

    /**
     * максимальное количество бомб игрока
     */
    private static final int MAXPLAYERBOMBS = 3;

    /**
     * максимальное количество летающих противников
     */
    private static final int MAXFLYINGENEMIES = 2;

    /**
     * индекс коллекции спрайта игрока
     */
    public static final int COLLECTION_PLAYER = 0;

    /**
     * индекс коллекции спрайтов бомб игрока
     */
    public static final int COLLECTION_PLAYERBOMBS = 1;

    /**
     * индекс коллекции спрайтов взрывов
     */
    public static final int COLLECTION_EXPLOSIONS = 2;

    /**
     * индекс коллекции спрайтов наземных объектов противника
     */
    public static final int COLLECTION_GROUNDENEMYOBJECTS = 3;

    /**
     * индекс коллекции спрайтов воздушных объектов противника
     */
    public static final int COLLECTION_FLYINGENEMYOBJECTS = 4;


    /**
     * Максимальное количество путей в игре
     */
    public static final int MAXPATHS = 42;

    /**
     * количество попыток игрока на простом уровне
     */
    private static final int ATTEMPTIONS_EASY = 3;

    /**
     * количество попыток игрока на нормальном уровне
     */
    private static final int ATTEMPTIONS_NORMAL = 2;

    /**
     * количество попыток игрока на тяжелом уровне
     */
    private static final int ATTEMPTIONS_HARD = 1;


    /**
     * интервал между стрельбой противника
     */
    private static final int ENEMYFIRINGDELAY_EASY = 4;
    private static final int ENEMYFIRINGDELAY_NORMAL = 3;
    private static final int ENEMYFIRINGDELAY_HARD = 2;

    /**
     * интервал до появления вражеского вертолета
     */
    private static final int ENEMYCOPTERINTERVAL_EASY = 70;
    private static final int ENEMYCOPTERINTERVAL_NORMAL = 60;
    private static final int ENEMYCOPTERINTERVAL_HARD = 50;


    /**
     * интервал до появления вражеского самолета
     */
    private static final int ENEMYPLANEINTERVAL_EASY = 200;
    private static final int ENEMYPLANEINTERVAL_NORMAL = 160;
    private static final int ENEMYPLANEINTERVAL_HARD = 130;

    /**
     * интервал до появления дерижабля
     */
    private static final int DERIGIBLEINTERVAL_EASY = 550;
    private static final int DERIGIBLEINTERVAL_NORMAL = 400;
    private static final int DERIGIBLEINTERVAL_HARD = 350;

    /**
     * количество пушек на уровне
     */
    private static final int ENEMYGUNSNUMBER_EASY = 7;
    private static final int ENEMYGUNSNUMBER_NORMAL = 8;
    private static final int ENEMYGUNSNUMBER_HARD = 9;

    /**
     * количество зениток на уровне
     */
    private static final int ENEMYAAGUNSNUMBER_EASY = 3;
    private static final int ENEMYAAGUNSNUMBER_NORMAL = 3;
    private static final int ENEMYAAGUNSNUMBER_HARD = 4;

    /**
     * количество ракетных шахт на уровне
     */
    private static final int ENEMYROCKETLAUNCHERSNUMBER_EASY = 6;
    private static final int ENEMYROCKETLAUNCHERSNUMBER_NORMAL = 7;
    private static final int ENEMYROCKETLAUNCHERSNUMBER_HARD = 8;


    /**
     * Количество вражеских танков
     */
    private static final int ENEMYTANKSNUMBER = 7;

    /**
     * Количество вражеских складов
     */
    private static final int ENEMYSTORAGESNUMBER = 4;

    /**
     * Количество вражеских локаторов
     */
    private static final int ENEMYLOCATORSNUMBER = 3;

    /**
     * Ширина поля в ячейках
     */
    public static final int TERRAIN_CELLS_WIDTH = 440;

    /**
     * Количество тиков, которые игрок неубиваем после его вылета
     */
    public static final int CANTKILLEDTICKS = 26;

    /**
     * Скорость перемещения террейна
     */
    private static final int I8_TERRAIN_SPEED = 3 * startup.SCALE_WIDTH;

    public static final int I8_BLOCKWIDTH = 8 * startup.SCALE_WIDTH;
    public static final int I8_BLOCKHEIGHT = 8 * startup.SCALE_HEIGHT;

    public static final int VIEWCELLWIDTH = (16 * startup.SCALE_WIDTH+0x7F)>>8;

//todo Игровые события
    /**
     * Генерируется взрыв
     */
    public static final int GAMEACTION_EXPLOSION = 0;

    /**
     * Выстрел наземного объекта
     */
    public static final int GAMEACTION_GROUNDFIRING = 1;

    /**
     * Вылет воздушного объекта
     */
    public static final int GAMEACTION_FLYINGOBJECTGENERATED = 2;

    /**
     * Старт ракеты
     */
    public static final int GAMEACTION_ROCKETLAUNCHED = 3;


//todo Переменные
    /**
     * переменная содержит счетчик неубиваемости игрока
     */
    public static int i_PlayerCantBeKilledCounter;

    /**
     * Переменная содержит субсостояние игры
     */
    private static int i_GameSubstate;

    /**
     * Счетчик до генерации дирижабля
     */
    private static int i_DeregibleGenerationCounter;

    /**
     * Счетчик до генерации вражеского самолета
     */
    private static int i_EnemyPlaneGenerationCounter;

    /**
     * Счетчик до генерации вражеского вертолета
     */
    private static int i_EnemyCopterGenerationCounter;

    /**
     * Задержка генерации выстрелов противнка для текущего уровня
     */
    private static int i_DelayEnemyFiring;

    /**
     * Задержка генерации самолетов противника
     */
    private static int i_DelayEnemyGeneratePlane;

    /**
     * Задержка генерации вертолетов противника
     */
    private static int i_DelayEnemyGenerateCopter;

    /**
     * Задержка генерации дерижаблей противника
     */
    private static int i_DelayEnemyGenerateDerigible;

    /**
     * Позиция террейна
     */
    public static int i8_TerrainPos;

    public static SpriteCollection [] ap_SpriteCollections;
    private static PathController [] ap_Paths;

//todo Функции
    /**
     * Обработка анимаций и выстрелов наземных объектов
     */
    private static void processGroundObjects()
    {
        SpriteCollection p_player = ap_SpriteCollections[COLLECTION_PLAYER];
        SpriteCollection p_grounds = ap_SpriteCollections[COLLECTION_GROUNDENEMYOBJECTS];
        SpriteCollection p_flying = ap_SpriteCollections[COLLECTION_FLYINGENEMYOBJECTS];
        SpriteCollection p_bombs = ap_SpriteCollections[COLLECTION_PLAYERBOMBS];
        SpriteCollection p_expl = ap_SpriteCollections[COLLECTION_EXPLOSIONS];

        int i_plX = p_player.ai_spriteDataArray[SpriteCollection.SPRITEDATAOFFSET_MAINX]+i8_TerrainPos;
        int i_plY = p_player.ai_spriteDataArray[SpriteCollection.SPRITEDATAOFFSET_MAINY];

        int [] ai_grounds = p_grounds.ai_spriteDataArray;

        int i_rightBorder = i8_TerrainPos+(176*startup.SCALE_WIDTH);

        p_grounds.initIterator();
        while(true)
        {
            int i_spr = p_grounds.nextActiveSpriteOffset();
            if (i_spr<0) break;

            int i_type = p_grounds.getSpriteType(i_spr);

            int i_gx = ai_grounds[i_spr+SpriteCollection.SPRITEDATAOFFSET_MAINX];
            int i_gy = ai_grounds[i_spr+SpriteCollection.SPRITEDATAOFFSET_MAINY];

            int i_diff = i_plX - i_gx;

            boolean lg_visibled = true;

            if (i_gx<i8_TerrainPos || i_gx>i_rightBorder)
                lg_visibled = false;

            // Проверка на столкновение с бомбой игрока
            p_grounds.moveMainPointXY(i_spr,0-i8_TerrainPos,0);
            int i_colBomb = p_bombs.findCollidedSprite(i_spr,p_grounds,-1);
            if (i_colBomb>=0)
            {
                // начисляем очки
                switch(i_type)
                {
                    case SPRITE_OBJ_AAGUN : i_PlayerScore += SCORE_AAGUN;break;
                    case SPRITE_OBJ_GUN : i_PlayerScore += SCORE_GUN;break;
                    case SPRITE_OBJ_TANK : i_PlayerScore += SCORE_TANK;break;
                    case SPRITE_OBJ_ROCKETLAUNCHER : i_PlayerScore += SCORE_ROCKETLAUNCHER;break;
                    case SPRITE_OBJ_RADAR : i_PlayerScore += SCORE_RADAR;break;
                    case SPRITE_OBJ_STORAGE : i_PlayerScore += SCORE_STORE;break;
                }

                int i_expl = p_expl.i_lastInactiveSpriteOffset;
                if (i_expl>=0)
                {
                    // генерируем взрыв
                    int i_mx = i_gx-i8_TerrainPos;
                    p_expl.activateSprite(SPRITE_OBJ_EXPLOSION,SPRITE_STATE_EXPLOSION_MOVE,i_expl);
                    startup.processGameAction(GAMEACTION_EXPLOSION);
                    p_expl.setSpriteNextTypeState(i_expl,SpriteCollection.SPRITEBEHAVIOUR_RELEASE);
                    p_expl.setMainPointXY(i_expl,i_mx&0xFFFFFF00,i_gy);
                }
                p_bombs.releaseSprite(i_colBomb);
                PathController p_pathObj = getPathForSprite(COLLECTION_PLAYERBOMBS,i_colBomb);
                if (p_pathObj!=null) p_pathObj.lg_Completed = true;
                p_pathObj = getPathForSprite(COLLECTION_GROUNDENEMYOBJECTS,i_spr);
                p_grounds.releaseSprite(i_spr);
                if (p_pathObj!=null) p_pathObj.lg_Completed = true;
                continue;
            }
            else
            {
                p_grounds.moveMainPointXY(i_spr,i8_TerrainPos,0);
            }

            switch(i_type)
            {
                case SPRITE_OBJ_GUN :
                {
                    // Объект пушка, поворачивается за игроком
                    if (lg_visibled)
                    {
                        int i_state = p_grounds.getSpriteState(i_spr);
                        boolean lg_canBeFiring = false;
                        int i_shellPath = 0;
                        int i_st = 0;


                        // Проверяем на положение по отношению к игроку

                        // проверка на потребность в повороте орудия
                        switch(i_state)
                        {
                            case SPRITE_STATE_GUN_TOLEFT :
                            {
                                // Влево пушка смотрит если разница по X между пушкой и вретолетом больше четеврти экрана иначе переключаем на центр
                                if (i_diff>(0-((176*startup.SCALE_WIDTH)>>2)))
                                {
                                    p_grounds.activateSprite(SPRITE_OBJ_GUN,SPRITE_STATE_GUN_TOCENTER,i_spr);
                                }
                                else
                                {
                                    lg_canBeFiring = true;
                                    i_shellPath = PATH_Enemy_missile_move_to_left_up;
                                    i_st = SPRITE_STATE_ENEMYSHELL_UPLEFT;
                                }
                            };break;
                            case SPRITE_STATE_GUN_TORIGHT :
                            {
                                // Вправо пушка смотрит если разница по X между пушкой и вретолетом больше 5 пикселей
                                // иначе поворачиваем на центр
                                if (i_diff<0x500)
                                {
                                    p_grounds.activateSprite(SPRITE_OBJ_GUN,SPRITE_STATE_GUN_TOCENTER,i_spr);
                                }
                                else
                                {
                                    lg_canBeFiring = true;
                                    i_shellPath = PATH_Enemy_missile_move_to_right_up;
                                    i_st = SPRITE_STATE_ENEMYSHELL_UPRIGHT;
                                }
                            };break;
                            case SPRITE_STATE_GUN_TOCENTER :
                            {
                                if (i_diff>0x500)
                                {
                                    p_grounds.activateSprite(SPRITE_OBJ_GUN,SPRITE_STATE_GUN_TORIGHT,i_spr);
                                }
                                else
                                if (i_diff<=(0-((176*startup.SCALE_WIDTH)>>2)))
                                {
                                    p_grounds.activateSprite(SPRITE_OBJ_GUN,SPRITE_STATE_GUN_TOLEFT,i_spr);
                                }
                                else
                                {
                                    lg_canBeFiring = true;
                                    i_shellPath = PATH_Enemy_missile_move_to_up;
                                    i_st = SPRITE_STATE_ENEMYSHELL_UP;
                                }
                            };break;
                        }

                        if (lg_canBeFiring)
                        {
                            // Проверка на выстрел орудия
                            int i_delayToFire = p_grounds.getOptionalData(i_spr);
                            if (i_delayToFire>0)
                            {
                                p_grounds.setOptionalData(i_spr,i_delayToFire-1);
                            }
                            else
                            {
                                if (getRandomInt(ENEMYFIRINGPROBABILITY)==(ENEMYFIRINGPROBABILITY>>1))
                                {
                                    // Генерируем снаряд
                                    int i_fly = p_flying.i_lastInactiveSpriteOffset;
                                    if (i_fly>=0)
                                    {
                                        PathController p_path = getInactivePath();
                                        if (p_path!=null)
                                        {
                                            p_flying.activateSprite(SPRITE_OBJ_ENEMYSHELL,i_st,i_fly);
                                            startup.processGameAction(GAMEACTION_GROUNDFIRING);
                                            p_path.initPath(i_shellPath,i_gx-i8_TerrainPos,i_gy,0x100,0x100,ap_SpriteCollections,COLLECTION_FLYINGENEMYOBJECTS,i_fly,ash_Paths,i_shellPath,0,0,0);
                                            p_grounds.setOptionalData(i_spr,i_DelayEnemyFiring);
                                        }
                                    }
                                }
                                else
                                {
                                    p_grounds.setOptionalData(i_spr,i_DelayEnemyFiring);
                                }
                            }
                        }
                    }
                };break;
                case SPRITE_OBJ_AAGUN :
                {
                    // Объект зенитка, стреляем
                    int i_delayToFire = p_grounds.getOptionalData(i_spr);
                    if (i_delayToFire>0)
                    {
                        p_grounds.setOptionalData(i_spr,i_delayToFire-1);
                    }
                    else
                    if (lg_visibled)
                    {
                        if (getRandomInt(ENEMYFIRINGPROBABILITY)==(ENEMYFIRINGPROBABILITY>>1))
                        {
                            // Генерируем снаряд
                            int i_fly = p_flying.i_lastInactiveSpriteOffset;
                            if (i_fly>=0)
                            {
                                PathController p_path = getInactivePath();
                                if (p_path!=null)
                                {
                                    startup.processGameAction(GAMEACTION_GROUNDFIRING);
                                    p_flying.activateSprite(SPRITE_OBJ_ENEMYSHELL,SPRITE_STATE_ENEMYSHELL_UP,i_fly);
                                    p_path.initPath(PATH_Enemy_missile_move_to_up,i_gx-i8_TerrainPos,i_gy,0x100,0x100,ap_SpriteCollections,COLLECTION_FLYINGENEMYOBJECTS,i_fly,ash_Paths,PATH_Enemy_missile_move_to_up,0,0,0);
                                    p_grounds.setOptionalData(i_spr,i_DelayEnemyFiring);
                                }
                            }
                        }
                        else
                        {
                            p_grounds.setOptionalData(i_spr,i_DelayEnemyFiring);
                        }
                    }
                };break;
                case SPRITE_OBJ_ROCKETLAUNCHER :
                {
                    int i_state = p_grounds.getSpriteState(i_spr);

                    // Объект ракетная установка
                    if (lg_visibled)
                    {
                        if (i_diff>(-20*startup.SCALE_WIDTH) && i_state == SPRITE_STATE_ROCKETLAUNCHER_CLOSED && getRandomInt(ENEMYFIRINGPROBABILITY)==(ENEMYFIRINGPROBABILITY>>1))
                        {
                            // Генерируем ракету
                            int i_fly = p_flying.i_lastInactiveSpriteOffset;
                            if (i_fly>=0)
                            {
                                PathController p_path = getInactivePath();
                                if (p_path!=null)
                                {
                                    p_grounds.activateSprite(SPRITE_OBJ_ROCKETLAUNCHER,SPRITE_STATE_ROCKETLAUNCHER_OPENED,i_spr);
                                    p_flying.activateSprite(SPRITE_OBJ_ENEMYROCKET,SPRITE_STATE_ENEMYROCKET_FLYING,i_fly);
                                    startup.processGameAction(GAMEACTION_ROCKETLAUNCHED);
                                    p_path.initPath(PATH_Enemy_Rocket_move,i_gx-i8_TerrainPos,i_gy,0x100,0x100,ap_SpriteCollections,COLLECTION_FLYINGENEMYOBJECTS,i_fly,ash_Paths,PATH_Enemy_Rocket_move,0,0,0);
                                }
                            }
                        }
                    }
                    else
                    {
                        if (i_state == SPRITE_STATE_ROCKETLAUNCHER_OPENED)
                        {
                            p_grounds.activateSprite(SPRITE_OBJ_ROCKETLAUNCHER,SPRITE_STATE_ROCKETLAUNCHER_CLOSED,i_spr);
                        }
                    }

                };break;
            }

        }
    }

    /**
     * Генерация выстрелов летающих объектов
     */
    private static void generateFiringOfFlyingObjects()
    {
        SpriteCollection p_sprCol = ap_SpriteCollections[COLLECTION_FLYINGENEMYOBJECTS];

        int i_playerX = ap_SpriteCollections[COLLECTION_PLAYER].ai_spriteDataArray[SpriteCollection.SPRITEDATAOFFSET_MAINX];

        // Проверяем есть ли свободные спрайты для выстрела
        int i_sprite = p_sprCol.i_lastInactiveSpriteOffset;
        if (i_sprite<0) return;
        PathController p_path = getInactivePath();
        if (p_path==null) return;

        p_sprCol.initIterator();
        while(true)
        {
            int i_spr = p_sprCol.nextActiveSpriteOffset();
            if (i_spr<0) break;

            int i_type = p_sprCol.getSpriteType(i_spr);

            switch(i_type)
            {
                case SPRITE_OBJ_ENEMYCOPTER :
                case SPRITE_OBJ_ENEMYPLANE :
                {
                    int i_delayToFiring = p_sprCol.getOptionalData(i_spr);
                    if (i_delayToFiring >0)
                    {
                        p_sprCol.setOptionalData(i_spr,i_delayToFiring-1);
                        continue;
                    }

                    // элемент случайности
                    if (getRandomInt(ENEMYFIRINGPROBABILITY)==(ENEMYFIRINGPROBABILITY>>1))
                    {
                        // Генерируем выстрел
                        int i_mx = p_sprCol.ai_spriteDataArray[i_spr+SpriteCollection.SPRITEDATAOFFSET_MAINX];
                        if (i_playerX>i_mx) continue;
                        startup.processGameAction(GAMEACTION_GROUNDFIRING);
                        p_sprCol.activateSprite(SPRITE_OBJ_ENEMYSHELL,SPRITE_STATE_ENEMYSHELL_LEFT,i_sprite);
                        int i_my = p_sprCol.ai_spriteDataArray[i_spr+SpriteCollection.SPRITEDATAOFFSET_MAINY];
                        p_path.initPath(PATH_Enemy_missile_move_to_left,i_mx,i_my,0x100,0x100,ap_SpriteCollections,COLLECTION_FLYINGENEMYOBJECTS,i_sprite,ash_Paths,PATH_Enemy_missile_move_to_left,0,0,0);
                    }
                    p_sprCol.setOptionalData(i_spr,i_DelayEnemyFiring);
                }
            }
        }
    }

    /**
     * Генерация вражеского самолета
     *
     * @return true если сгенерирован и false если неудача
     */
    private static boolean generateEnemyPlane()
    {
        // элемент случайности
        if (getRandomInt(1000) > 500) return true;

        //#-
        System.out.println("Plane generated");
        //#+

        SpriteCollection p_fly = ap_SpriteCollections[COLLECTION_FLYINGENEMYOBJECTS];

        int i_spr = p_fly.i_lastInactiveSpriteOffset;
        if (i_spr < 0) return false;
        PathController p_path = getInactivePath();
        if (p_path == null) return false;

        short [] ash_paths = ash_Paths;

        int i_pathOfset = PATH_Enemy_fly_gener_points+(((getRandomInt(1000 * (ash_paths[PATH_Enemy_fly_gener_points] + 1) - 1) / 1000) << 1) + 2);

        int i8_x = ash_paths[i_pathOfset++] * startup.SCALE_WIDTH;
        int i8_y = ash_paths[i_pathOfset] * startup.SCALE_HEIGHT;

        p_fly.activateSprite(SPRITE_OBJ_ENEMYPLANE, SPRITE_STATE_ENEMYPLANE_FLYING, i_spr);
        p_path.initPath(PATH_Enemy_Plane_move, i8_x, i8_y, 0x100, 0x100, ap_SpriteCollections, COLLECTION_FLYINGENEMYOBJECTS, i_spr, ash_paths, PATH_Enemy_Plane_move, 0, 0, 0);
                startup.processGameAction(GAMEACTION_FLYINGOBJECTGENERATED);
        p_fly.setOptionalData(i_spr, i_DelayEnemyFiring);

        return true;
    }

    /**
     * Генерация вражеского вертолета
     *
     * @return true если сгенерирован и false если неудача
     */
    private static boolean generateEnemyCopter()
    {
        // элемент случайности
        if (getRandomInt(1000) > 500) return true;

        //#-
        System.out.println("Copter generated");
        //#+

        SpriteCollection p_fly = ap_SpriteCollections[COLLECTION_FLYINGENEMYOBJECTS];

        int i_spr = p_fly.i_lastInactiveSpriteOffset;
        if (i_spr < 0) return false;
        PathController p_path = getInactivePath();
        if (p_path == null) return false;

        short [] ash_paths = ash_Paths;

        int i_pathType = getRandomInt(2999) / 1000;

        switch (i_pathType)
        {
            case 0 :
                i_pathType = PATH_Enemy_Copter_move1;
                break;
            case 1 :
                i_pathType = PATH_Enemy_Copter_move2;
                break;
            case 2 :
                i_pathType = PATH_Enemy_Copter_move3;
                break;
        }

        startup.processGameAction(GAMEACTION_FLYINGOBJECTGENERATED);
        p_fly.activateSprite(SPRITE_OBJ_ENEMYCOPTER, SPRITE_STATE_ENEMYCOPTER_FLYING, i_spr);
        p_path.initPath(i_pathType, 0, 0, 0x100, 0x100, ap_SpriteCollections, COLLECTION_FLYINGENEMYOBJECTS, i_spr, ash_paths, i_pathType, 0, 0, 0);

        p_fly.setOptionalData(i_spr, i_DelayEnemyFiring);

        return true;
    }

    /**
     * Генерация дерижабля
     *
     * @return true если сгенерирован и false если неудача
     */
    private static boolean generateDerigible()
    {
        // элемент случайности
        if (getRandomInt(1000) > 500) return true;

        //#-
        System.out.println("Derigible generated");
        //#+

        SpriteCollection p_fly = ap_SpriteCollections[COLLECTION_FLYINGENEMYOBJECTS];

        int i_spr = p_fly.i_lastInactiveSpriteOffset;
        if (i_spr < 0) return false;
        PathController p_path = getInactivePath();
        if (p_path == null) return false;

        short [] ash_paths = ash_Paths;

        int i_pathOfset = PATH_Enemy_fly_gener_points+(((getRandomInt(1000 * (ash_paths[PATH_Enemy_fly_gener_points] + 1) - 1) / 1000) << 1) + 2);

        int i8_x = ash_paths[i_pathOfset++] * startup.SCALE_WIDTH;
        int i8_y = ash_paths[i_pathOfset] * startup.SCALE_HEIGHT;

        p_fly.activateSprite(SPRITE_OBJ_DERIGIBLE, SPRITE_STATE_DERIGIBLE_FLYING, i_spr);
        p_path.initPath(PATH_Enemy_Dirigible_move, i8_x, i8_y, 0x100, 0x100, ap_SpriteCollections, COLLECTION_FLYINGENEMYOBJECTS, i_spr, ash_paths, PATH_Enemy_Dirigible_move, 0, 0, 0);
                startup.processGameAction(GAMEACTION_FLYINGOBJECTGENERATED);
        return true;
    }

    private static void offsetStaticObjects()
    {
        // смещаем взрывы
        SpriteCollection p_expl = ap_SpriteCollections[COLLECTION_EXPLOSIONS];
        p_expl.initIterator();

        while (true)
        {
            int i_spr = p_expl.nextActiveSpriteOffset();
            if (i_spr < 0) break;
            p_expl.moveMainPointXY(i_spr, 0 - I8_TERRAIN_SPEED, 0);
        }
    }

    private static boolean checkPlayerWithObjects()
    {
        SpriteCollection p_spr = ap_SpriteCollections[COLLECTION_PLAYER];
        SpriteCollection p_flying = ap_SpriteCollections[COLLECTION_FLYINGENEMYOBJECTS];
        SpriteCollection p_grnd = ap_SpriteCollections[COLLECTION_GROUNDENEMYOBJECTS];

        SpriteCollection p_col = p_flying;

        int i_collided = p_flying.findCollidedSprite(0, p_spr, -1);

        int i_delta = 0;

        if (i_collided < 0)
        {
            i_delta = i8_TerrainPos;
            p_spr.moveMainPointXY(0,i_delta,0);
            i_collided = p_grnd.findCollidedSprite(0,p_spr,-1);
            p_spr.moveMainPointXY(0,0-i_delta,0);
            if (i_collided<0) return false;
            p_col = p_grnd;
            PathController p_path = getPathForSprite(COLLECTION_GROUNDENEMYOBJECTS,i_collided);
            if (p_path!=null) p_path.lg_Completed = true;
        }
        else
        {
            PathController p_path = getPathForSprite(COLLECTION_FLYINGENEMYOBJECTS,i_collided);
            if (p_path!=null) p_path.lg_Completed = true;
        }

        int [] ai_arr = p_col.ai_spriteDataArray;
        int i_off = i_collided + SpriteCollection.SPRITEDATAOFFSET_MAINX;
        int i_mx = ai_arr[i_off++]-i_delta;
        int i_my = ai_arr[i_off];



        p_col.releaseSprite(i_collided);

        // переводим летающий объект во взрыв
        p_spr = ap_SpriteCollections[COLLECTION_EXPLOSIONS];
        int i_expl = p_spr.i_lastInactiveSpriteOffset;
        if (i_expl >= 0)
        {
            p_spr.activateSprite(SPRITE_OBJ_EXPLOSION, SPRITE_STATE_EXPLOSION_MOVE, i_expl);
            startup.processGameAction(GAMEACTION_EXPLOSION);
            p_spr.setSpriteNextTypeState(i_expl, SpriteCollection.SPRITEBEHAVIOUR_RELEASE);
            p_spr.setMainPointXY(i_expl,i_mx&0xFFFFFF00,i_my);
        }

        return true;
    }

    private static void processPlayerBombs()
    {
        SpriteCollection p_bombs = ap_SpriteCollections[COLLECTION_PLAYERBOMBS];
        SpriteCollection p_expl = ap_SpriteCollections[COLLECTION_EXPLOSIONS];
        p_bombs.initIterator();

        while (true)
        {
            int i_spr = p_bombs.nextActiveSpriteOffset();
            if (i_spr < 0) break;

            // проверка на столкновение с землей
            if (checkSpriteCollisionWithTerrain(p_bombs, i_spr))
            {
                PathController p_contr = getPathForSprite(COLLECTION_PLAYERBOMBS, i_spr);
                p_contr.lg_Completed = true;

                int i_mx = p_bombs.ai_spriteDataArray[i_spr + SpriteCollection.SPRITEDATAOFFSET_MAINX];
                int i_my = p_bombs.ai_spriteDataArray[i_spr + SpriteCollection.SPRITEDATAOFFSET_MAINY];

                p_bombs.releaseSprite(i_spr);

                int i_expl = p_expl.i_lastInactiveSpriteOffset;
                if (i_expl >= 0)
                {
                    p_expl.activateSprite(SPRITE_OBJ_EXPLOSION, SPRITE_STATE_EXPLOSION_MOVE, i_expl);
                    startup.processGameAction(GAMEACTION_EXPLOSION);
                    p_expl.setSpriteNextTypeState(i_expl, SpriteCollection.SPRITEBEHAVIOUR_RELEASE);
                    p_expl.setMainPointXY(i_expl, i_mx&0xFFFFFF00, i_my);
                }
            }
        }
    }


    /**
     * Проверка спрайта на столкновение с поверхностью
     *
     * @param _collection
     * @param _spriteOffset
     * @return true если столкнулся и false если нет
     */
    private static boolean checkSpriteCollisionWithTerrain(SpriteCollection _collection, int _spriteOffset)
    {
        int i_xy = _collection.getSpriteScreenXY(_spriteOffset);
        int i_scrx = i_xy >> 16;
        int i_scry = (short) i_xy;

        i_xy = _collection.getSpriteHotzoneXY(_spriteOffset);
        i_scrx += (i_xy >> 16);
        i_scry += (short) i_xy;

        i_xy = _collection.getSpriteHotzoneWidthHeight(_spriteOffset);
        int i_w = i_xy >>> 16;
        int i_h = i_xy & 0xFFFF;

        i_scry += i_h;

        int i_startCell = (((i8_TerrainPos+0x7F)>>8)+i_scrx)/Gamelet.VIEWCELLWIDTH;
        i_startCell = (i_startCell<<1) + (i_startCell%(Gamelet.VIEWCELLWIDTH<<8)>=(Gamelet.VIEWCELLWIDTH<<7)? 1 : 0);

        if (i_startCell>=TERRAIN_CELLS_WIDTH) i_startCell-=TERRAIN_CELLS_WIDTH;
        int i_cellsNumber = ((i_w << 16) / I8_BLOCKWIDTH + 0x7F) >> 8;

        byte [] ab_terra = TERRAIN_MAP;

        final int SCRHEIGHT = 188 * startup.SCALE_HEIGHT;

        while (i_cellsNumber >= 0)
        {
            int i_alt = ((SCRHEIGHT - (ab_terra[i_startCell] + 1) * I8_BLOCKHEIGHT) + 0x7F) >> 8;

            if (i_alt <= i_scry) return true;

            i_startCell++;
            if (i_startCell == TERRAIN_CELLS_WIDTH) i_startCell = 0;
            i_cellsNumber--;
        }

        return false;
    }

    private static void generateEnemyObjects()
    {
        int i_aaguns = 0;
        int i_tanks = ENEMYTANKSNUMBER;
        int i_storages = ENEMYSTORAGESNUMBER;
        int i_rockets = 0;
        int i_guns = 0;
        int i_radars = ENEMYLOCATORSNUMBER;

        switch (i_GameLevel)
        {
            case GAMELEVEL_EASY :
            {
                i_aaguns = ENEMYAAGUNSNUMBER_EASY;
                i_rockets = ENEMYROCKETLAUNCHERSNUMBER_EASY;
                i_guns = ENEMYGUNSNUMBER_EASY;
            }
            ;
            break;
            case GAMELEVEL_NORMAL :
            {
                i_aaguns = ENEMYAAGUNSNUMBER_NORMAL;
                i_rockets = ENEMYROCKETLAUNCHERSNUMBER_NORMAL;
                i_guns = ENEMYGUNSNUMBER_NORMAL;
            }
            ;
            break;
            case GAMELEVEL_HARD :
            {
                i_aaguns = ENEMYAAGUNSNUMBER_HARD;
                i_rockets = ENEMYROCKETLAUNCHERSNUMBER_HARD;
                i_guns = ENEMYGUNSNUMBER_HARD;
            }
            ;
            break;
        }

        final short[] ash_paths = ash_Paths;

        // генерация зениток
        SpriteCollection p_sprCol = ap_SpriteCollections[COLLECTION_GROUNDENEMYOBJECTS];

        int i_offset = PATH_Enemy_Zenitka_gener_points;
        int i_maxNumber = ash_paths[i_offset] + 1;
        boolean [] alg_points = new boolean[i_maxNumber];

        //#-
        System.out.println("Generating AAGUN points "+i_maxNumber+" number of objects "+i_aaguns);
        //#+

        while (i_aaguns > 0)
        {
            for (int li = 0; li < i_maxNumber; li++)
            {
                if (!alg_points[li] && getRandomInt(1000) < 200)
                {
                    int i_off = i_offset + 2 + (li << 1);
                    int i8_x = ash_paths[i_off++];
                    int i8_y = ash_paths[i_off];

                    long l_xy = calculatePositionXY(i8_x,i8_y);
                    i8_x = (int)(l_xy>>>32);
                    i8_y = (int)l_xy;

                    int i_spr = p_sprCol.i_lastInactiveSpriteOffset;
                    PathController p_path = getInactivePath();

                    p_sprCol.activateSprite(SPRITE_OBJ_AAGUN, SPRITE_STATE_AAGUN_MOVINGLEFT, i_spr);
                    p_path.initPath(PATH_Enemy_Zenitka_move, i8_x, i8_y, 0x100, 0x100, ap_SpriteCollections, COLLECTION_GROUNDENEMYOBJECTS, i_spr, ash_paths, PATH_Enemy_Zenitka_move, 0, 0, 0);

                    alg_points[li] = true;

                    i_aaguns--;
                }
            }
        }

        // генерация танков
        i_offset = PATH_Enemy_Tank_gener_points;
        i_maxNumber = ash_paths[i_offset] + 1;
        alg_points = new boolean[i_maxNumber];

        //#-
        System.out.println("Generating TANKS points "+i_maxNumber+" number of objects "+i_tanks);
        //#+

        while (i_tanks > 0)
        {
            for (int li = 0; li < i_maxNumber; li++)
            {
                if (!alg_points[li] && getRandomInt(1000) < 200)
                {
                    int i_off = i_offset + 2 + (li << 1);
                    int i8_x = ash_paths[i_off++];
                    int i8_y = ash_paths[i_off];

                    long l_xy = calculatePositionXY(i8_x,i8_y);
                    i8_x = (int)(l_xy>>>32);
                    i8_y = (int)l_xy;

                    int i_spr = p_sprCol.i_lastInactiveSpriteOffset;
                    PathController p_path = getInactivePath();

                    p_sprCol.activateSprite(SPRITE_OBJ_TANK, SPRITE_STATE_TANK_MOVINGLEFT, i_spr);
                    p_path.initPath(PATH_Enemy_Tank_move, i8_x, i8_y, 0x100, 0x100, ap_SpriteCollections, COLLECTION_GROUNDENEMYOBJECTS, i_spr, ash_paths, PATH_Enemy_Tank_move, 0, 0, 0);

                    alg_points[li] = true;

                    i_tanks--;
                }
            }
        }

        // генерация шахт
        i_offset = PATH_Enemy_Rocket_gener_points;
        i_maxNumber = ash_paths[i_offset] + 1;
        alg_points = new boolean[i_maxNumber];

        //#-
        System.out.println("Generating ROCKETS points "+i_maxNumber+" number of objects "+i_rockets);
        //#+

        while (i_rockets > 0)
        {
            for (int li = 0; li < i_maxNumber; li++)
            {
                if (!alg_points[li] && getRandomInt(1000) < 200)
                {
                    int i_off = i_offset + 2 + (li << 1);
                    int i8_x = ash_paths[i_off++];
                    int i8_y = ash_paths[i_off];

                    long l_xy = calculatePositionXY(i8_x,i8_y);
                    i8_x = (int)(l_xy>>>32);
                    i8_y = (int)l_xy;

                    int i_spr = p_sprCol.i_lastInactiveSpriteOffset;

                    p_sprCol.activateSprite(SPRITE_OBJ_ROCKETLAUNCHER, SPRITE_STATE_ROCKETLAUNCHER_CLOSED, i_spr);
                    p_sprCol.setMainPointXY(i_spr, i8_x, i8_y);

                    alg_points[li] = true;

                    i_rockets--;
                }
            }
        }

        // генерация складов
        i_offset = PATH_Enemy_Safe_gener_points;
        i_maxNumber = ash_paths[i_offset] + 1;
        alg_points = new boolean[i_maxNumber];

        //#-
        System.out.println("Generating SAFE points "+i_maxNumber+" number of objects "+i_storages);
        //#+

        while (i_storages > 0)
        {
            for (int li = 0; li < i_maxNumber; li++)
            {
                if (!alg_points[li] && getRandomInt(1000) < 200)
                {
                    int i_off = i_offset + 2 + (li << 1);
                    int i8_x = ash_paths[i_off++];
                    int i8_y = ash_paths[i_off];


                    long l_xy = calculatePositionXY(i8_x,i8_y);
                    i8_x = (int)(l_xy>>>32);
                    i8_y = (int)l_xy;

                    int i_spr = p_sprCol.i_lastInactiveSpriteOffset;

                    p_sprCol.activateSprite(SPRITE_OBJ_STORAGE, SPRITE_STATE_STORAGE_STAND, i_spr);
                    p_sprCol.setMainPointXY(i_spr, i8_x, i8_y);

                    alg_points[li] = true;

                    i_storages--;
                }
            }
        }

        // генерация локаторов
        i_offset = PATH_Enemy_Station_gener_points;
        i_maxNumber = ash_paths[i_offset] + 1;
        alg_points = new boolean[i_maxNumber];

        //#-
        System.out.println("Generating RADARS points "+i_maxNumber+" number of objects "+i_radars);
        //#+

        while (i_radars > 0)
        {
            for (int li = 0; li < i_maxNumber; li++)
            {
                if (!alg_points[li] && getRandomInt(1000) < 200)
                {
                    int i_off = i_offset + 2 + (li << 1);
                    int i8_x = ash_paths[i_off++];
                    int i8_y = ash_paths[i_off];

                    long l_xy = calculatePositionXY(i8_x,i8_y);
                    i8_x = (int)(l_xy>>>32);
                    i8_y = (int)l_xy;

                    int i_spr = p_sprCol.i_lastInactiveSpriteOffset;

                    p_sprCol.activateSprite(SPRITE_OBJ_RADAR, SPRITE_STATE_RADAR_STAND, i_spr);
                    p_sprCol.setMainPointXY(i_spr, i8_x, i8_y);

                    alg_points[li] = true;

                    i_radars--;
                }
            }
        }

        // генерация пушек
        i_offset = PATH_Enemy_Gun_gener_points;
        i_maxNumber = ash_paths[i_offset] + 1;
        alg_points = new boolean[i_maxNumber];

        //#-
        System.out.println("Generating GUNS points "+i_maxNumber+" number of objects "+i_guns);
        //#+

        while (i_guns > 0)
        {
            for (int li = 0; li < i_maxNumber; li++)
            {
                if (!alg_points[li] && getRandomInt(1000) < 200)
                {
                    int i_off = i_offset + 2 + (li << 1);
                    int i8_x = ash_paths[i_off++];
                    int i8_y = ash_paths[i_off];

                    long l_xy = calculatePositionXY(i8_x,i8_y);
                    i8_x = (int)(l_xy>>>32);
                    i8_y = (int)l_xy;

                    int i_spr = p_sprCol.i_lastInactiveSpriteOffset;

                    p_sprCol.activateSprite(SPRITE_OBJ_GUN, SPRITE_STATE_GUN_TOCENTER, i_spr);
                    p_sprCol.setMainPointXY(i_spr, i8_x, i8_y);

                    alg_points[li] = true;

                    i_guns--;
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

    private static long calculatePositionXY(int _x,int _y)
    {
        final int ORIGCELL = 16;
        final int VIEWCELLHEIGHT = ((ORIGCELL * startup.SCALE_HEIGHT)+0x7F)>>8;
        final int CELLSHEIGHT = 5;
        final int ORIGHEIGHT = 188;

        final int OFFSETY = ORIGHEIGHT-(CELLSHEIGHT*ORIGCELL);
        final int OFFSETNEW =  (((ORIGHEIGHT*startup.SCALE_HEIGHT+0x7F)>>8)-(VIEWCELLHEIGHT*CELLSHEIGHT))<<8;

        _y -= OFFSETY;
        if (_y<0) _y=0;

        int i_cellX = _x / ORIGCELL;
        int i_cellY = _y / ORIGCELL;
        int i_cellXoffst = _x % ORIGCELL;

        i_cellX = ((i_cellX*VIEWCELLWIDTH)<<8)+(i_cellXoffst*startup.SCALE_WIDTH)&0xFFFFFF00;
        i_cellY = OFFSETNEW+((i_cellY*VIEWCELLHEIGHT)<<8)+VIEWCELLHEIGHT;

        return ((long)(i_cellX)<<32)|(((long)i_cellY) & 0xFFFFFFFFl);
    }


    private static short [] loadSpriteArray(Class _this,String _resource) throws Exception
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

//todo Массивы
    protected static short [] ash_SpritesTable;

    public static final byte [] TERRAIN_MAP = {
            3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3,
            3, 3, 4, 5, 5, 5, 5, 4, 3, 2, 1, 1, 1, 1, 1, 1,
            1, 1, 2, 3, 3, 3, 3, 3, 4, 5, 5, 5, 5, 5, 5, 5,
            6, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7,
            7, 7, 7, 6, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5,
            5, 5, 5, 5, 5, 5, 5, 5, 5, 4, 3, 3, 3, 3, 3, 3,
            3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3,
            3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 4, 5, 6, 7, 7, 7,
            7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7,
            7, 7, 7, 6, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5,
            5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5,
            5, 5, 5, 5, 5, 5, 6, 7, 8, 9, 9, 9, 9, 9, 9, 9,
            9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 8, 7, 7, 7, 7,
            7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7,
            7, 7, 7, 7, 7, 6, 5, 4, 3, 3, 3, 3, 3, 3, 3, 3,
            3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3,
            3, 3, 3, 3, 3, 2, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1,
            1, 1, 1, 1, 1, 1, 2, 3, 4, 5, 5, 5, 5, 5, 5, 5,
            5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 4,
            3, 2, 1, 1, 1, 1, 1, 1, 1, 1, 2, 3, 4, 5, 6, 7,
            7, 7, 7, 7, 8, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9,
            9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9,
            9, 9, 9, 8, 7, 6, 5, 4, 3, 3, 3, 3, 3, 3, 3, 3,
            3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3,
            3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3,
            3, 2, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1,
            1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 2, 3, 3, 3, 3, 3,
            3, 3, 3, 3, 3, 3, 3, 3};

//------------------Sprite constants-----------------
    public static final int SPRITE_OBJ_PLAYER = 0;
    public static final int SPRITE_STATE_PLAYER_FLYING = 0;
    public static final int SPRITE_OBJ_PLAYERBOMB = 1;
    public static final int SPRITE_STATE_PLAYERBOMB_MOVE = 0;
    public static final int SPRITE_OBJ_EXPLOSION = 2;
    public static final int SPRITE_STATE_EXPLOSION_MOVE = 0;
    public static final int SPRITE_OBJ_ENEMYCOPTER = 3;
    public static final int SPRITE_STATE_ENEMYCOPTER_FLYING = 0;
    public static final int SPRITE_OBJ_ENEMYPLANE = 4;
    public static final int SPRITE_STATE_ENEMYPLANE_FLYING = 0;
    public static final int SPRITE_OBJ_ENEMYSHELL = 5;
    public static final int SPRITE_STATE_ENEMYSHELL_LEFT = 0;
    public static final int SPRITE_STATE_ENEMYSHELL_UPLEFT = 1;
    public static final int SPRITE_STATE_ENEMYSHELL_UPRIGHT = 2;
    public static final int SPRITE_STATE_ENEMYSHELL_UP = 3;
    public static final int SPRITE_OBJ_ENEMYROCKET = 6;
    public static final int SPRITE_STATE_ENEMYROCKET_FLYING = 0;
    public static final int SPRITE_OBJ_DERIGIBLE = 7;
    public static final int SPRITE_STATE_DERIGIBLE_FLYING = 0;
    public static final int SPRITE_OBJ_ROCKETLAUNCHER = 8;
    public static final int SPRITE_STATE_ROCKETLAUNCHER_CLOSED = 0;
    public static final int SPRITE_STATE_ROCKETLAUNCHER_OPENED = 1;
    public static final int SPRITE_OBJ_RADAR = 9;
    public static final int SPRITE_STATE_RADAR_STAND = 0;
    public static final int SPRITE_OBJ_STORAGE = 10;
    public static final int SPRITE_STATE_STORAGE_STAND = 0;
    public static final int SPRITE_OBJ_GUN = 11;
    public static final int SPRITE_STATE_GUN_TOLEFT = 0;
    public static final int SPRITE_STATE_GUN_TOCENTER = 1;
    public static final int SPRITE_STATE_GUN_TORIGHT = 2;
    public static final int SPRITE_OBJ_TANK = 12;
    public static final int SPRITE_STATE_TANK_MOVINGLEFT = 0;
    public static final int SPRITE_STATE_TANK_MOVINGRIGHT = 1;
    public static final int SPRITE_STATE_TANK_TURNTOLEFT = 2;
    public static final int SPRITE_STATE_TANK_TURNTORIGHT = 3;
    public static final int SPRITE_OBJ_AAGUN = 13;
    public static final int SPRITE_STATE_AAGUN_MOVINGLEFT = 0;
    public static final int SPRITE_STATE_AAGUN_MOVINGRIGHT = 1;
    public static final int SPRITE_STATE_AAGUN_TURNTOLEFT = 2;
    public static final int SPRITE_STATE_AAGUN_TURNTORIGHT = 3;

    // The array contains values for path controllers
    public static short [] ash_Paths;

   // PATH offsets
   private static final int PATH_Enemy_Rocket_gener_points = 0;
   private static final int PATH_Enemy_Gun_gener_points = 22;
   private static final int PATH_Enemy_Zenitka_gener_points = 44;
   private static final int PATH_Enemy_Tank_gener_points = 56;
   private static final int PATH_Enemy_Safe_gener_points = 72;
   private static final int PATH_Enemy_Station_gener_points = 82;
   private static final int PATH_Enemy_Copter_move1 = 90;
   private static final int PATH_Enemy_Copter_move2 = 110;
   private static final int PATH_Enemy_Copter_move3 = 136;
   private static final int PATH_Enemy_Plane_move = 165;
   private static final int PATH_Enemy_Dirigible_move = 176;
   private static final int PATH_Player_Bomb_move = 184;
   private static final int PATH_Enemy_Rocket_move = 204;
   private static final int PATH_Enemy_missile_move_to_left = 221;
   private static final int PATH_Enemy_missile_move_to_left_up = 229;
   private static final int PATH_Enemy_missile_move_to_up = 237;
   private static final int PATH_Enemy_missile_move_to_right_up = 245;
   private static final int PATH_Enemy_Tank_move = 253;
   private static final int PATH_Enemy_Zenitka_move = 261;
   private static final int PATH_Player_In = 269;
   private static final int PATH_Player_Out = 277;
   private static final int PATH_Enemy_fly_gener_points = 285;
}

