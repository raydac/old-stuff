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

    protected static short [] ash_SpritesTable;

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
        ash_SpritesTable = loadSpriteArray(_class, "/spr.bin");

        PathController.SCALE_WIDTH = startup.SCALE_WIDTH;
        PathController.SCALE_HEIGHT = startup.SCALE_HEIGHT;

        p_SpriteBricksCollection = new SpriteCollection(0, MAX_BRICKSSPRITES, ash_SpritesTable);
        p_SpritePartsCollection = new SpriteCollection(1, MAX_PARTSSPRITES, ash_SpritesTable);
        ap_SpriteCollections = new SpriteCollection[]{p_SpriteBricksCollection, p_SpritePartsCollection};

        ap_PartsPaths = new PathController[MAX_PARTSSPRITES];
        for (int li = 0; li < MAX_PARTSSPRITES; li++) ap_PartsPaths[li] = new PathController();

        p_MovedBrickPath = new PathController();

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
        p_SpriteBricksCollection = null;
        p_SpritePartsCollection = null;
        ap_PartsPaths = null;
        ap_SpriteCollections = null;
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

        i_PlayerScore = 0;

        switch (_gameLevel)
        {
            //------------Вставьте свой код здесь--------------------
            case GAMELEVEL_EASY:
            {
                i_ScoresForAllBricks = SCORE_FORALL_BRICKS_EASY;
                i_ScoresForOneBrick = SCORE_PER_BRICK_EASY;
                i_HowManyToGenerateBricksForLevel = NUMBERGENERATEDBLCOKS_EASY;
            }
            ;
            break;
            case GAMELEVEL_NORMAL:
            {
                i_ScoresForAllBricks = SCORE_FORALL_BRICKS_NORMAL;
                i_ScoresForOneBrick = SCORE_PER_BRICK_NORMAL;
                i_HowManyToGenerateBricksForLevel = NUMBERGENERATEDBLCOKS_NORMAL;
            }
            ;
            break;
            case GAMELEVEL_HARD:
            {
                i_ScoresForAllBricks = SCORE_FORALL_BRICKS_HARD;
                i_ScoresForOneBrick = SCORE_PER_BRICK_HARD;
                i_HowManyToGenerateBricksForLevel = NUMBERGENERATEDBLCOKS_HARD;
            }
            ;
            break;
            default:
                return false;
        }

        p_SpriteBricksCollection.releaseAllSprites();
        p_SpritePartsCollection.releaseAllSprites();

        for (int li = 0; li < MAX_PARTSSPRITES; li++) ap_PartsPaths[li].deactivate();
        p_MovedBrickPath.deactivate();

        initField();

        i_NumberOfGeneratedNewBlocks = i_HowManyToGenerateBricksForLevel;
        i_CurrentSubMode = SUBMODE_GENERATEBRICK;

        // Ставим четыре квадрата по углам
        //ab_GameField[0] =BRICK_RED;
        //ab_GameField[GAMEFIELDWIDTH-1] =BRICK_RED;
        //ab_GameField[GAMEFIELDWIDTH*(GAMEFIELDHEIGHT-1)] =BRICK_RED;
        //ab_GameField[GAMEFIELDWIDTH*(GAMEFIELDHEIGHT-1)+GAMEFIELDWIDTH-1] =BRICK_RED;

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
        i_PlayerCursorX = 0;
        i_PlayerCursorY = 0;
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
        // Координаты курсора
        i_PlayerCursorX = p_dis.readInt();
        i_PlayerCursorY = p_dis.readInt();

        i_SelectedBrickDestCoordX = p_dis.readInt();
        i_SelectedBrickDestCoordY = p_dis.readInt();

        i_CounterToNextDestroyedBlock = p_dis.readInt();
        i_NumberOfGeneratedNewBlocks = p_dis.readInt();
        i_SelectedBrickCoordX = p_dis.readInt();
        i_SelectedBrickCoordY = p_dis.readInt();
        ;
        i_SelectedBrickSpriteOffset = p_dis.readInt();
        i_SelectedBrickType = p_dis.readInt();

        for (int li = 0; li < ash_MoveBrickPath.length; li++) ash_MoveBrickPath[li] = p_dis.readShort();

        // Очки
        i_PlayerScore = p_dis.readInt();

        // Субрежим
        i_CurrentSubMode = p_dis.readUnsignedByte();
        i_SubModeAfterRemoving = p_dis.readUnsignedByte();

        // Массив к удалению
        int i_len = GAMEFIELDHEIGHT * GAMEFIELDWIDTH;
        for (int li = 0; li < i_len; li++) alg_DestroyedBricks[li] = p_dis.readBoolean();

        // Игровой массив
        for (int li = 0; li < i_len; li++) ab_GameField[li] = p_dis.readByte();
        ;

        // Спрайты
        p_SpriteBricksCollection.loadFromStream(p_dis);

        // Путь движущегося спрайта
        p_MovedBrickPath.readPathFromStream(p_dis, ap_SpriteCollections, ash_MoveBrickPath);

        p_SpritePartsCollection.releaseAllSprites();
        for (int li = 0; li < MAX_PARTSSPRITES; li++) ap_PartsPaths[li].deactivate();

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
        if (i_GameState != STATE_STARTED && i_PlayerState != PLAYER_PLAYING)
        {
            //#-
            System.out.println("Wrong game state [GS=" + i_GameState + ",PL=" + i_PlayerState + "]");
            //#+
            throw new Exception();
        }
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
        // Координаты курсора
        p_dos.writeInt(i_PlayerCursorX);
        p_dos.writeInt(i_PlayerCursorY);

        p_dos.writeInt(i_SelectedBrickDestCoordX);
        p_dos.writeInt(i_SelectedBrickDestCoordY);

        p_dos.writeInt(i_CounterToNextDestroyedBlock);
        p_dos.writeInt(i_NumberOfGeneratedNewBlocks);
        p_dos.writeInt(i_SelectedBrickCoordX);
        p_dos.writeInt(i_SelectedBrickCoordY);
        p_dos.writeInt(i_SelectedBrickSpriteOffset);
        p_dos.writeInt(i_SelectedBrickType);

        for (int li = 0; li < ash_MoveBrickPath.length; li++) p_dos.writeShort(ash_MoveBrickPath[li]);

        // Очки
        p_dos.writeInt(i_PlayerScore);

        // Субрежим
        p_dos.writeByte(i_CurrentSubMode);
        p_dos.writeByte(i_SubModeAfterRemoving);

        // Массив к удалению
        int i_len = GAMEFIELDHEIGHT * GAMEFIELDWIDTH;
        for (int li = 0; li < i_len; li++) p_dos.writeBoolean(alg_DestroyedBricks[li]);

        // Игровой массив
        for (int li = 0; li < i_len; li++) p_dos.writeByte(ab_GameField[li]);

        // Спрайты
        p_SpriteBricksCollection.saveToStream(p_dos);

        // Путь движущегося спрайта
        p_MovedBrickPath.writePathToStream(p_dos);

        //--------------------------------------------------
        p_dos.flush();
        p_dos.close();
        byte[] ab_result = p_arrayOutputStream.toByteArray();

        //#-
        System.out.println("Saved data length = " + ab_result.length);
        //#+

        if (ab_result.length != getGameStateDataBlockSize())
        {
            //#-
            System.out.println("Wrong data length " + ab_result.length + "!=" + getGameStateDataBlockSize());
            //#+
            throw new Exception();
        }
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
        int Size = 16 + 8 + 4;
        //------------Вставьте свой код здесь--------------------
        Size += 868;
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
        return "SIM_ROTR";
    }

    /**
     * Функция отката назад к состоянию старта уровня
     */
    public static final void rollbackStage()
    {
    }

    public static final void notifyPathPointPassed(PathController _controller)
    {

    }

    public static final void notifyPathCompleted(PathController _controller)
    {
        switch (i_CurrentSubMode)
        {
            case SUBMODE_BRICKMOVING :
            {
                // Цикл закончен
                p_SpriteBricksCollection.releaseAllSprites();

                int i_offst = i_SelectedBrickDestCoordX + i_SelectedBrickDestCoordY * GAMEFIELDWIDTH;
                ab_GameField[i_offst] &= ~BRICK_PROCESSFLAG;

                startup.updateCell(i_offst);

                i_CounterToNextDestroyedBlock = 0;
                i_NumberOfGeneratedNewBlocks = i_HowManyToGenerateBricksForLevel;

                // Проверяем на удаление
                int i_numForRemoving = checkBricksForDestroying();
                if (i_numForRemoving > 0)
                {
                    i_NumberOfGeneratedNewBlocks = i_HowManyToGenerateBricksForLevel - i_numForRemoving;
                    if (i_NumberOfGeneratedNewBlocks < 0) i_NumberOfGeneratedNewBlocks = 0;
                    i_SubModeAfterRemoving = SUBMODE_GENERATEBRICK;
                    i_CurrentSubMode = SUBMODE_DESTROYINGBRICK;
                }
                else
                {
                    i_CurrentSubMode = SUBMODE_GENERATEBRICK;
                }
            }
            ;
            break;
            case SUBMODE_DESTROYINGBRICK :
            {
                _controller.realiseSprite();
            }
            ;
            break;
        }
    }

    /**
     * Функция, получает сигнал об окончании обработки анимации спрайта
     *
     * @param _collectionID идентификатор коллекции
     * @param _spriteOffset смещение спрайта в коллекции
     */
    public static final void notifySpriteAnimationCompleted(int _collectionID, int _spriteOffset)
    {
        switch (i_CurrentSubMode)
        {
            case SUBMODE_BRICKROTATING:
            case SUBMODE_GENERATEBRICK:
            {
                int _offset = i_SelectedBrickSpriteOffset;
                ab_GameField[_offset] &= 0xF;
                startup.updateCell(_offset);

                if (i_CurrentSubMode == SUBMODE_BRICKROTATING)
                {
                    i_CounterToNextDestroyedBlock = 0;
                    int i_numForRemoving = checkBricksForDestroying();
                    if (i_numForRemoving > 0)
                    {
                        i_NumberOfGeneratedNewBlocks = i_HowManyToGenerateBricksForLevel - i_numForRemoving;
                        if (i_NumberOfGeneratedNewBlocks < 0) i_NumberOfGeneratedNewBlocks = 0;
                        i_SubModeAfterRemoving = SUBMODE_WAITPLAYERMOVE;
                        i_CurrentSubMode = SUBMODE_DESTROYINGBRICK;
                    }
                    else
                    {
                        i_NumberOfGeneratedNewBlocks = i_HowManyToGenerateBricksForLevel;
                        if (i_NumberOfGeneratedNewBlocks < 0) i_NumberOfGeneratedNewBlocks = 0;
                        i_CurrentSubMode = SUBMODE_GENERATEBRICK;
                    }
                }
            }
            ;
            break;
        }
    }

    /**
     * Функция отрабатывает игровой шаг
     *
     * @param _keyStateFlags флаги управления игроком.
     * @return статус игры после отработки игровой интерации
     */
    public static final int nextGameStep(int _keyStateFlags)
    {
        // Обрабатываем анимацию спрайтов
        p_SpriteBricksCollection.processAnimationForActiveSprites();
        p_SpritePartsCollection.processAnimationForActiveSprites();

        //------------Вставьте свой код здесь--------------------
        switch (i_CurrentSubMode)
        {
            case SUBMODE_DESTROYINGBRICK:
            {

                boolean lg_isThereBlocks = true;

                if (i_CounterToNextDestroyedBlock <= 0)
                {
                    i_CounterToNextDestroyedBlock = DELAYBETWEENDESTROYEDBLOCKS;

                    int i_nextBrick = getBricksCoordsForDestroying();
                    if (i_nextBrick >= 0)
                    {
                        if (activateBrickDestroying(i_nextBrick))
                        {
                            startup.processGameAction(GAMEACTION_REMOVEBRICKFROMFIELD);
                            alg_DestroyedBricks[i_nextBrick] = false;
                        }
                    }
                    else
                        lg_isThereBlocks = false;
                }
                else
                {
                    i_CounterToNextDestroyedBlock--;
                }

                // Отработка путей
                boolean lg_isThereActivePaths = false;
                for (int li = 0; li < MAX_PARTSSPRITES; li++)
                {
                    PathController p_path = ap_PartsPaths[li];
                    if (!p_path.isCompleted())
                    {
                        lg_isThereActivePaths = true;
                        p_path.processStep();
                    }
                }

                if (!lg_isThereActivePaths && !lg_isThereBlocks)
                {
                    // Проверяем не были ли удалены все блоки
                    int i_bricksNum = getBricksNum();
                    switch (i_bricksNum)
                    {
                        case 0:
                        {
                            i_PlayerScore += i_ScoresForAllBricks;
                            i_PlayerState = PLAYER_WIN;
                            i_GameState = STATE_OVER;
                            return i_GameState;
                        }
                        case GAMEFIELDWIDTH * GAMEFIELDHEIGHT:
                        {
                            i_PlayerState = PLAYER_LOST;
                            i_GameState = STATE_OVER;
                            return i_GameState;
                        }
                        default:
                        {
                            //Переходим в режим генерации блоков
                            p_SpriteBricksCollection.releaseAllSprites();
                            p_SpritePartsCollection.releaseAllSprites();
                            i_CurrentSubMode = i_SubModeAfterRemoving;
                        }
                    }
                }
            }
            ;
            break;
            case SUBMODE_GENERATEBRICK:
            {
                // Производим генерацию блоков если нет активных
                if (p_SpriteBricksCollection.i_lastActiveSpriteOffset < 0)
                {
                    if (i_NumberOfGeneratedNewBlocks > 0)
                    {
                        int i_coords = getCoordsForNewBricks();
                        if (i_coords < 0)
                        {
                            // Всё поле заполнено, переход на проигрыш игрока
                            i_PlayerState = PLAYER_LOST;
                            i_GameState = STATE_OVER;
                            return i_GameState;
                        }
                        else
                        {
                            int i_spr = p_SpriteBricksCollection.getOffsetOfLastInactiveSprite();

                            int i_brickType = 0;
                            int i_sprType = 0;
                            int i_sprState = 0;
                            switch (getRandomInt(3999) / 1000)
                            {
                                case 0:
                                {
                                    i_brickType = BRICK_RED;
                                    i_sprType = SPRITE_BRICKRED;
                                    i_sprState = SPRITE_BRICKRED_INCOMING;
                                }
                                ;
                                break;
                                case 1:
                                {
                                    i_brickType = BRICK_GREEN;
                                    i_sprType = SPRITE_BRICKGREEN;
                                    i_sprState = SPRITE_BRICKGREEN_INCOMING;
                                }
                                ;
                                break;
                                case 2:
                                {
                                    i_brickType = BRICK_BLUE;
                                    i_sprType = SPRITE_BRICKBLUE;
                                    i_sprState = SPRITE_BRICKBLUE_INCOMING;
                                }
                                ;
                                break;
                                case 3:
                                {
                                    i_brickType = BRICK_YELLOW;
                                    i_sprType = SPRITE_BRICKYELLOW;
                                    i_sprState = SPRITE_BRICKYELLOW_INCOMING;
                                }
                                ;
                                break;
                            }

                            ab_GameField[i_coords] = (byte) (BRICK_PROCESSFLAG | i_brickType);

                            p_SpriteBricksCollection.activateSprite(i_sprType, i_sprState, i_spr);
                            p_SpriteBricksCollection.setSpriteNextTypeState(i_spr, SpriteCollection.SPRITEBEHAVIOUR_NOTIFY | SpriteCollection.SPRITEBEHAVIOUR_RELEASE);

                            int i_x = getCellX8(i_coords % GAMEFIELDWIDTH)+ (CELLWIDTH >> 1);
                            int i_y = getCellY8(i_coords / GAMEFIELDWIDTH) + (CELLHEIGHT >> 1);
                            i_SelectedBrickSpriteOffset = i_coords;

                            p_SpriteBricksCollection.setMainPointXY(i_spr, i_x, i_y);
                            startup.processGameAction(GAMEACTION_PLACEBRICKONFIELD);
                        }

                        i_NumberOfGeneratedNewBlocks--;
                    }
                    else
                    {
                        p_SpriteBricksCollection.releaseAllSprites();

                        // Проверка на квадраты к удалению
                        int i_numForRemoving = checkBricksForDestroying();
                        if (i_numForRemoving > 0)
                        {
                            // Переходим в режим удаления квадратов
                            i_CounterToNextDestroyedBlock = 0;
                            i_CurrentSubMode = SUBMODE_DESTROYINGBRICK;
                            i_SubModeAfterRemoving = SUBMODE_WAITPLAYERMOVE;
                        }
                        else
                        {
                            // Переходим в режим ожидания действий игрока
                            i_CurrentSubMode = SUBMODE_WAITPLAYERMOVE;
                        }
                    }
                }
            }
            ;
            break;
            case SUBMODE_BRICKROTATING:
            {
                // Производим поворот блока
            }
            ;
            break;
            case SUBMODE_BRICKMOVING:
            {
                // Производим перемещение блока
                p_MovedBrickPath.processStep();
            }
            ;
            break;
            case SUBMODE_WAITPLAYERMOVE:
            {
                // Ждем ход игрока, он может перемещать курсор по экрану
                int i_kf = _keyStateFlags;

                if ((i_kf & BUTTON_DOWN) != 0)
                {
                    if (i_PlayerCursorY < (GAMEFIELDHEIGHT - 1)) i_PlayerCursorY++;
                    else
                        i_PlayerCursorY = 0;
                }
                else if ((i_kf & BUTTON_UP) != 0)
                {
                    if (i_PlayerCursorY > 0)
                        i_PlayerCursorY--;
                    else
                        i_PlayerCursorY = GAMEFIELDHEIGHT - 1;
                }
                else if ((i_kf & BUTTON_LEFT) != 0)
                {
                    if (i_PlayerCursorX > 0) i_PlayerCursorX--;
                    else
                    i_PlayerCursorX = GAMEFIELDWIDTH - 1;
                }
                else if ((i_kf & BUTTON_RIGHT) != 0)
                {
                    if (i_PlayerCursorX < (GAMEFIELDWIDTH - 1)) i_PlayerCursorX++;
                    else
                        i_PlayerCursorX = 0;
                }
                else if ((i_kf & BUTTON_FIRE) != 0)
                {
                    // Проверка есть ли под курсором объект и выделение его
                    int i_offset = i_PlayerCursorX + i_PlayerCursorY * GAMEFIELDWIDTH;
                    int i_brick = ab_GameField[i_offset];

                    if (i_brick != BRICK_NONE)
                    {
                        ab_GameField[i_offset] |= BRICK_PROCESSFLAG;

                        startup.updateCell(i_offset);

                        // активизируем спрайт
                        int i_spriteOffset = p_SpriteBricksCollection.getOffsetOfLastInactiveSprite();
                        int i_spriteType = 0, i_spriteState = 0;
                        i_SelectedBrickType = i_brick;
                        switch (i_brick)
                        {
                            case BRICK_GREEN:
                            {
                                i_spriteType = SPRITE_BRICKGREEN;
                                i_spriteState = SPRITE_BRICKGREEN_SELECTED;
                            }
                            ;
                            break;
                            case BRICK_RED:
                            {
                                i_spriteType = SPRITE_BRICKRED;
                                i_spriteState = SPRITE_BRICKRED_SELECTED;
                            }
                            ;
                            break;
                            case BRICK_YELLOW:
                            {
                                i_spriteType = SPRITE_BRICKYELLOW;
                                i_spriteState = SPRITE_BRICKYELLOW_SELECTED;
                            }
                            ;
                            break;
                            case BRICK_BLUE:
                            {
                                i_spriteType = SPRITE_BRICKBLUE;
                                i_spriteState = SPRITE_BRICKBLUE_SELECTED;
                            }
                            ;
                            break;
                            //#-
                            default:
                                System.err.println("unknown type");
                                //#+
                        }
                        i_SelectedBrickSpriteOffset = i_spriteOffset;
                        p_SpriteBricksCollection.activateSprite(i_spriteType, i_spriteState, i_spriteOffset);

                        int i_x = getCellX8(i_PlayerCursorX) + (CELLWIDTH >> 1);
                        int i_y = getCellY8(i_PlayerCursorY) + (CELLHEIGHT >> 1);

                        p_SpriteBricksCollection.setMainPointXY(i_spriteOffset, i_x, i_y);

                        i_SelectedBrickCoordX = i_PlayerCursorX;
                        i_SelectedBrickCoordY = i_PlayerCursorY;

                        i_CurrentSubMode = SUBMODE_BRICKSELECTED;
                    }
                }
            }
            ;
            break;
            case SUBMODE_BRICKSELECTED:
            {
                boolean lg_overBrick = ab_GameField[i_PlayerCursorX + (i_PlayerCursorY * GAMEFIELDWIDTH)] != BRICK_NONE; //(i_SelectedBrickCoordX == i_PlayerCursorX && i_SelectedBrickCoordY == i_PlayerCursorY);

                int i_kf = _keyStateFlags;
                if ((i_kf & BUTTON_DOWN) != 0)
                {
                    if (i_PlayerCursorY < (GAMEFIELDHEIGHT - 1)) i_PlayerCursorY++;
                    else
                        i_PlayerCursorY = 0;
                }
                else if ((i_kf & BUTTON_UP) != 0)
                {
                    if (i_PlayerCursorY > 0) i_PlayerCursorY--;
                    else
                    i_PlayerCursorY = GAMEFIELDHEIGHT - 1;
                }
                else if ((i_kf & BUTTON_LEFT) != 0)
                {
                    if (i_PlayerCursorX > 0) i_PlayerCursorX--;
                    else
                    i_PlayerCursorX = GAMEFIELDWIDTH - 1;
                }
                else if ((i_kf & BUTTON_RIGHT) != 0)
                {
                    if (i_PlayerCursorX < (GAMEFIELDWIDTH - 1)) i_PlayerCursorX++;
                    else
                        i_PlayerCursorX = 0;
                }
                else if ((i_kf & BUTTON_FIRE) != 0)
                {
                    i_SelectedBrickDestCoordX = i_PlayerCursorX;
                    i_SelectedBrickDestCoordY = i_PlayerCursorY;
                    if (lg_overBrick)
                    {
                        // отменяем выбор
                        int i_off = i_SelectedBrickCoordX + i_SelectedBrickCoordY * GAMEFIELDWIDTH;
                        ab_GameField[i_off] &= ~BRICK_PROCESSFLAG;
                        p_SpriteBricksCollection.releaseAllSprites();
                        startup.updateCell(i_off);
                        i_CurrentSubMode = SUBMODE_WAITPLAYERMOVE;
                    }
                    else
                    {
                        int i_srcPos = i_SelectedBrickCoordX + (i_SelectedBrickCoordY * GAMEFIELDWIDTH);
                        int i_dstPos = i_SelectedBrickDestCoordX + (i_SelectedBrickDestCoordY * GAMEFIELDWIDTH);

                        if (ab_GameField[i_dstPos] == BRICK_NONE)
                        {
                            // строим путь
                            if (findPathForBrick(i_srcPos, i_dstPos))
                            {
                                int i_SprState = 0;
                                int i_SprType = 0;
                                switch (i_SelectedBrickType & ~BRICK_PROCESSFLAG)
                                {
                                    case BRICK_RED:
                                    {
                                        i_SprType = SPRITE_BRICKRED;
                                        i_SprState = SPRITE_BRICKRED_MOVING;
                                    }
                                    ;
                                    break;
                                    case BRICK_GREEN:
                                    {
                                        i_SprType = SPRITE_BRICKGREEN;
                                        i_SprState = SPRITE_BRICKGREEN_MOVING;
                                    }
                                    ;
                                    break;
                                    case BRICK_BLUE:
                                    {
                                        i_SprType = SPRITE_BRICKBLUE;
                                        i_SprState = SPRITE_BRICKBLUE_MOVING;
                                    }
                                    ;
                                    break;
                                    case BRICK_YELLOW:
                                    {
                                        i_SprType = SPRITE_BRICKYELLOW;
                                        i_SprState = SPRITE_BRICKYELLOW_MOVING;
                                    }
                                    ;
                                    break;
                                }

                                ab_GameField[i_srcPos] = BRICK_NONE;
                                ab_GameField[i_dstPos] = (byte) (i_SelectedBrickType | BRICK_PROCESSFLAG);

                                p_SpriteBricksCollection.activateSprite(i_SprType, i_SprState, i_SelectedBrickSpriteOffset);
                                p_MovedBrickPath.initPath(0, 0, 0, 0x100, 0x100, ap_SpriteCollections, p_SpriteBricksCollection.i_CollectionID, i_SelectedBrickSpriteOffset, ash_MoveBrickPath, 0, 0, 0, PathController.MODIFY_NONE);
                                p_MovedBrickPath.setNotifyFlags(PathController.NOTIFY_ENDPOINT);
                                i_CurrentSubMode = SUBMODE_BRICKMOVING;
                            }
                        }
                    }
                }
                else if ((i_kf & BUTTON_RIGHTROTATE) != 0)
                {
                    // Проверка возможности поворота блока
                    if (lg_overBrick)
                    {
                        int i_x = getCellX8(i_PlayerCursorX) + (CELLWIDTH >> 1);
                        int i_y = getCellY8(i_PlayerCursorY) + (CELLHEIGHT >> 1);

                        // Можно поворачивать
                        int i_newType = 0;
                        int i_newState = 0;
                        int i_newBrState = 0;
                        switch (i_SelectedBrickType)
                        {
                            case BRICK_BLUE:
                                i_newState = SPRITE_BRICKBLUE_ROTATING;
                                i_newType = SPRITE_BRICKBLUE;
                                i_newBrState = BRICK_GREEN;
                                break;
                            case BRICK_RED:
                                i_newState = SPRITE_BRICKRED_ROTATING;
                                i_newType = SPRITE_BRICKRED;
                                i_newBrState = BRICK_YELLOW;
                                break;
                            case BRICK_GREEN:
                                i_newState = SPRITE_BRICKGREEN_ROTATING;
                                i_newType = SPRITE_BRICKGREEN;
                                i_newBrState = BRICK_RED;
                                break;
                            case BRICK_YELLOW:
                                i_newState = SPRITE_BRICKYELLOW_ROTATING;
                                i_newType = SPRITE_BRICKYELLOW;
                                i_newBrState = BRICK_BLUE;
                                break;
                        }
                        int i_off = i_SelectedBrickCoordX + i_SelectedBrickCoordY * GAMEFIELDWIDTH;
                        ab_GameField[i_off] = (byte) (BRICK_PROCESSFLAG | i_newBrState);
                        p_SpriteBricksCollection.activateSprite(i_newType, i_newState, i_SelectedBrickSpriteOffset);
                        p_SpriteBricksCollection.setSpriteNextTypeState(i_SelectedBrickSpriteOffset, SpriteCollection.SPRITEBEHAVIOUR_NOTIFY | SpriteCollection.SPRITEBEHAVIOUR_RELEASE);
                        p_SpriteBricksCollection.setMainPointXY(i_SelectedBrickSpriteOffset, i_x, i_y);
                        i_CurrentSubMode = SUBMODE_BRICKROTATING;
                        i_SelectedBrickSpriteOffset = i_off;
                        startup.updateCell(i_off);
                    }
                    else
                    {
                        // Переносим курсор на блок
                        i_PlayerCursorX = i_SelectedBrickCoordX;
                        i_PlayerCursorY = i_SelectedBrickCoordY;
                    }
                }
            }
            ;
            break;
        }
        i_lastKeyFlags = _keyStateFlags;

        //--------------------------------------------------
        return i_GameState;
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


    //------------------Таблица анимаций--------------------
    // массив анимации
    private static final int[] ai_spriteAnimationArray = new int []
            {
                    // Brick
                    1,
                    // Появление
                    4,
                    // Уничтожение
                    15,
                    // Кусок блока
                    26,

                    // width, height, hotzoneoffsetx, hotzoneoffsety, hotzonewidth, hotzoneheight, frames, animation_delay, animation_type, main_offsetx, main_offsety
                    //data state1
                    0x3000, 0x3000, 0x1000, 0x1000, 0x1000, 0x1000, 10, 10, SpriteCollection.ANIMATION_CYCLIC, 0, 0,
                    //data state2
                    0x3000, 0x3000, 0x1000, 0x1000, 0x1000, 0x1000, 10, 10, SpriteCollection.ANIMATION_PENDULUM, 0, 0,
                    //data state3
                    0x3000, 0x3000, 0x1000, 0x1000, 0x1000, 0x1000, 10, 10, SpriteCollection.ANIMATION_FROZEN, 0, 0,
            };

    //------------------Параметры спрайтов-----------------
    public static final int SPRITEOBJECT_BRICK = 0;
    public static final int SPRITEOBJECT_BRICK_APPEARANCE = 0;
    public static final int SPRITEOBJECT_BRICK_DESTROYING = 1;
    public static final int SPRITEOBJECT_BRICK_RED_SELECTED = 1;
    public static final int SPRITEOBJECT_BRICK_GREEN_SELECTED = 1;
    public static final int SPRITEOBJECT_BRICK_BLUE_SELECTED = 1;
    public static final int SPRITEOBJECT_BRICK_YELLOW_SELECTED = 1;
    public static final int SPRITEOBJECT_BRICK_RED_ROTATING = 1;
    public static final int SPRITEOBJECT_BRICK_GREEN_ROTATING = 1;
    public static final int SPRITEOBJECT_BRICK_BLUE_ROTATING = 1;
    public static final int SPRITEOBJECT_BRICK_YELLOW_ROTATING = 1;

    //------------------Игровые события---------------------
    /**
     * Поставить блок на поле
     */
    public static final int GAMEACTION_PLACEBRICKONFIELD = 0;

    /**
     * Убрать блок с поля
     */
    public static final int GAMEACTION_REMOVEBRICKFROMFIELD = 1;
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

    /**
     * Нажата кнопка повернуть вправо на 90 градусов
     */
    public static final int BUTTON_RIGHTROTATE = 32;

    //------------------Игровые переменные------------------
    /**
     * Ширина игрового поля
     */
    public static final int GAMEFIELDWIDTH = 8;
    /**
     * Высота игрового поля
     */
    public static final int GAMEFIELDHEIGHT = 8;

    /**
     * Количество генерируемых кубиков для простого уровня
     */
    private static final int NUMBERGENERATEDBLCOKS_EASY = 3;
    /**
     * Количество генерируемых кубиков для нормального уровня
     */
    private static final int NUMBERGENERATEDBLCOKS_NORMAL = 4;
    /**
     * Количество генерируемых кубиков для сложного уровня
     */
    private static final int NUMBERGENERATEDBLCOKS_HARD = 5;

    /**
     * Массив содержит игровое поле
     */
    public static final byte[] ab_GameField = new byte [GAMEFIELDHEIGHT * GAMEFIELDWIDTH];
    /**
     * Массив содержит список удаляемых кубиков
     */
    public static final boolean[] alg_DestroyedBricks = new boolean [GAMEFIELDHEIGHT * GAMEFIELDWIDTH];

    private static final int MAX_BRICKSSPRITES = 3;// Максимальное количество спрайтов блоков, которые могут быть одновременно на экране
    private static final int PARTSPERBRICK = 5; // Количество обломков на блок
    private static final int MAX_PARTSSPRITES = MAX_BRICKSSPRITES * PARTSPERBRICK; // Максимальное количество спрайтов обломков, которые могут быть одновременно на экране

    private static final int DELAYBETWEENDESTROYEDBLOCKS = 3; // задержка между уничтожением блоков
    private static int i_CounterToNextDestroyedBlock; // счетчки до уничтожения следующего блока

    /**
     * Коллекция спрайтов, содержащая блоки
     */
    public static SpriteCollection p_SpriteBricksCollection;
    /**
     * Коллекция спрайтов, содержащая обломки
     */
    public static SpriteCollection p_SpritePartsCollection;

    private static SpriteCollection[] ap_SpriteCollections;

    /**
     * Путь, по которому двигается выбранный блок
     */
    public static PathController p_MovedBrickPath;
    /**
     * Пути, по которым двигаются обломки
     */
    public static PathController[] ap_PartsPaths;

    //TODO Игровые константы
    /**
     * Ширина ячейки
     */
    public static final int CELLWIDTH = 20 * startup.SCALE_WIDTH;
    /**
     * Высота ячейки
     */
    public static final int CELLHEIGHT = 20 * startup.SCALE_HEIGHT;

    /**
     * Количество шагов при перемещении блока к месту назначения, на одну ячейку
     */
    private static final int BRICKPATHSTEPS = 2;

    /**
     * Код отсутствующего куба
     */
    public static final byte BRICK_NONE = 0;

    /**
     * Код красного куба
     */
    public static final byte BRICK_RED = 1;

    /**
     * Код зеленого куба
     */
    public static final byte BRICK_GREEN = 2;

    /**
     * Код синего куба
     */
    public static final byte BRICK_BLUE = 3;

    /**
     * Код желтого куба
     */
    public static final byte BRICK_YELLOW = 4;

    /**
     * Флаг куба, находящегося в обработке
     */
    public static final int BRICK_PROCESSFLAG = 0x10;

    /**
     * Стартовая константа поиска пути
     */
    public static final int BRICK_PATHSVALUESTART = 8;

    /**
     * Конечная константа поиска пути
     */
    public static final int BRICK_PATHSVALUEEND = 14;

    /**
     * Очки за убранный блок в легком режиме
     */
    private static final int SCORE_PER_BRICK_EASY = 2;
    /**
     * Очки за убранный блок в нормальном режиме
     */
    private static final int SCORE_PER_BRICK_NORMAL = 4;
    /**
     * Очки за убранный блок в тяжелом режиме
     */
    private static final int SCORE_PER_BRICK_HARD = 6;

    /**
     * Очки за все убранные блоки в легком режиме
     */
    private static final int SCORE_FORALL_BRICKS_EASY = 2;
    /**
     * Очки за все убранные блоки в нормальном режиме
     */
    private static final int SCORE_FORALL_BRICKS_NORMAL = 4;
    /**
     * Очки за все убранные блоки в тяжелом режиме
     */
    private static final int SCORE_FORALL_BRICKS_HARD = 6;

    protected static final int SUBMODE_GENERATEBRICK = 0; // Генерация нового блока(ов)
    protected static final int SUBMODE_DESTROYINGBRICK = 1; // Уничтожение блока(ов)
    protected static final int SUBMODE_WAITPLAYERMOVE = 2; // Ожидание действий игрока, перемещение курсора
    protected static final int SUBMODE_BRICKROTATING = 3; // Поворот выбранного блока
    protected static final int SUBMODE_BRICKSELECTED = 4; // Блок выбран пользователем
    protected static final int SUBMODE_BRICKMOVING = 5; // Блок двигается к месту назначения

    public static int i_CurrentSubMode;
    public static int i_SubModeAfterRemoving;
    private static int i_HowManyToGenerateBricksForLevel;
    private static int i_NumberOfGeneratedNewBlocks;
    private static int i_ScoresForAllBricks;
    private static int i_ScoresForOneBrick;

    public static int i_PlayerCursorX;
    public static int i_PlayerCursorY;

    private static int i_lastKeyFlags;

    /**
     * Ищет совпадающие квадраты и выставляет флаги в массиве на уничтожение
     *
     * @return возвращает количество блоков к удалению
     */
    private static final int checkBricksForDestroying()
    {
        final int i_len = GAMEFIELDHEIGHT * GAMEFIELDWIDTH;
        int i_offst = 0;
        final byte[] ab_arr = ab_GameField;
        int i_result = 0;

        for (int ly = 0; ly < GAMEFIELDHEIGHT; ly++)
        {
            for (int lx = 0; lx < GAMEFIELDWIDTH; lx++)
            {
                int i_off2 = lx + i_offst;
                int i_val = ab_arr[i_off2];
                alg_DestroyedBricks[i_off2] = false;
                if (i_val == BRICK_NONE) continue;

                // верхний
                if (ly > 0)
                {
                    int i_val2 = ab_arr[i_off2 - GAMEFIELDWIDTH];
                    if (i_val2 != BRICK_NONE)
                    {
                        i_val2 = i_val - i_val2;
                        if (i_val2 == 2 || i_val2 == -2)
                        {
                            alg_DestroyedBricks[i_off2 - GAMEFIELDWIDTH] = true;
                            alg_DestroyedBricks[i_off2] = true;
                        }
                    }
                }

                // Левый
                if (lx > 0)
                {
                    int i_val2 = ab_arr[i_off2 - 1];
                    if (i_val2 != BRICK_NONE)
                    {
                        i_val2 = i_val - i_val2;
                        if (i_val2 == 2 || i_val2 == -2)
                        {
                            alg_DestroyedBricks[i_off2 - 1] = true;
                            alg_DestroyedBricks[i_off2] = true;
                        }
                    }
                }

                // Правый
                if (lx < (GAMEFIELDWIDTH - 1))
                {
                    int i_val2 = ab_arr[i_off2 + 1];
                    if (i_val2 != BRICK_NONE)
                    {
                        i_val2 = i_val - i_val2;
                        if (i_val2 == 2 || i_val2 == -2)
                        {
                            alg_DestroyedBricks[i_off2 + 1] = true;
                            alg_DestroyedBricks[i_off2] = true;
                        }
                    }
                }

                // Нижний
                if (ly < (GAMEFIELDHEIGHT - 1))
                {
                    int i_val2 = ab_arr[i_off2 + GAMEFIELDWIDTH];
                    if (i_val2 != BRICK_NONE)
                    {
                        i_val2 = i_val - i_val2;
                        if (i_val2 == 2 || i_val2 == -2)
                        {
                            alg_DestroyedBricks[i_off2 + GAMEFIELDWIDTH] = true;
                            alg_DestroyedBricks[i_off2] = true;
                        }
                    }
                }
            }
            i_offst += GAMEFIELDWIDTH;
        }

        for (int li = 0; li < i_len; li++) if (alg_DestroyedBricks[li]) i_result++;

        return i_result;
    }

    private static final short[] ash_MoveBrickPath = new short[256];


    /**
     * Ищет путь от стартовой точки к конечной в плоских координатах, заполняет массив пути точками для раьоты контроллера
     *
     * @param _startCoords стартовое смещение элемента
     * @param _endCoords   смещение целевого элемента
     * @return true если найден путь иначе false
     */
    private static final boolean findPathForBrick(int _startCoords, int _endCoords)
    {
        // Очистка массива
        final int i_len = GAMEFIELDWIDTH * GAMEFIELDHEIGHT;
        final byte[] ab_arr = ab_GameField;
        for (int li = 0; li < i_len; li++)
        {
            byte b_val = ab_arr[li];
            ab_arr[li] = b_val >= BRICK_PATHSVALUESTART && b_val <= BRICK_PATHSVALUEEND ? BRICK_NONE : b_val;
        }

        // Выставляем начальные значения
        boolean lg_foundEndPoint = false;
        boolean lg_newPointPlaced = true;
        byte b_lastVal = -1;

        byte b_oldBrickState = ab_GameField[_startCoords];
        ab_GameField[_startCoords] = BRICK_PATHSVALUESTART;

        while (lg_newPointPlaced && !lg_foundEndPoint)
        {
            lg_newPointPlaced = false;
            for (int li = 0; li < i_len; li++)
            {
                byte b_val2 = ab_arr[li];
                byte b_val = b_val2;
                if ((b_val >= BRICK_PATHSVALUESTART && b_val <= BRICK_PATHSVALUEEND))
                {
                    // Расставляем волну
                    b_val++;
                    if (b_val > BRICK_PATHSVALUEEND) b_val = BRICK_PATHSVALUESTART;

                    // Верх
                    int i_coord = li - GAMEFIELDWIDTH;
                    if (i_coord >= 0)
                    {
                        if (i_coord == _endCoords)
                        {
                            b_lastVal = b_val2;
                            lg_foundEndPoint = true;
                            break;
                        }
                        byte b_v = ab_arr[i_coord];
                        if (b_v == BRICK_NONE)
                        {
                            ab_arr[i_coord] = b_val;
                            lg_newPointPlaced = true;
                        }
                    }

                    // Низ
                    i_coord = li + GAMEFIELDWIDTH;
                    if (i_coord < i_len)
                    {
                        if (i_coord == _endCoords)
                        {
                            b_lastVal = b_val2;
                            lg_foundEndPoint = true;
                            break;
                        }
                        byte b_v = ab_arr[i_coord];
                        if (b_v == BRICK_NONE)
                        {
                            ab_arr[i_coord] = b_val;
                            lg_newPointPlaced = true;
                        }
                    }

                    // Влево
                    if (li % GAMEFIELDWIDTH > 0)
                    {
                        i_coord = li - 1;
                        if (i_coord < i_len)
                        {
                            if (i_coord == _endCoords)
                            {
                                b_lastVal = b_val2;
                                lg_foundEndPoint = true;
                                break;
                            }
                            byte b_v = ab_arr[i_coord];
                            if (b_v == BRICK_NONE)
                            {
                                ab_arr[i_coord] = b_val;
                                lg_newPointPlaced = true;
                            }
                        }
                    }

                    // Вправо
                    if (li % GAMEFIELDWIDTH < (GAMEFIELDWIDTH - 1))
                    {
                        i_coord = li + 1;
                        if (i_coord < i_len)
                        {
                            if (i_coord == _endCoords)
                            {
                                b_lastVal = b_val2;
                                lg_foundEndPoint = true;
                                break;
                            }
                            byte b_v = ab_arr[i_coord];
                            if (b_v == BRICK_NONE)
                            {
                                ab_arr[i_coord] = b_val;
                                lg_newPointPlaced = true;
                            }
                        }
                    }
                }
            }
        }

        ab_GameField[_startCoords] = b_oldBrickState;

        if (lg_foundEndPoint)
        {
            int i_pointsNumber = 0;
            final short[] ash_arra = ash_MoveBrickPath;
            int i_pointOffset = 1;
            ash_arra[i_pointOffset++] = PathController.TYPE_NORMAL;

            // Заполняем путь
            int li = _endCoords;
            while (true)
            {
                int i_x = ((li % GAMEFIELDWIDTH) * 20 + 10);
                int i_y = ((li / GAMEFIELDWIDTH) * 20 + 10);

                // Добавляем точку в путь

                ash_arra[i_pointOffset++] = (short) i_x;
                ash_arra[i_pointOffset++] = (short) i_y;
                ash_arra[i_pointOffset++] = BRICKPATHSTEPS;

                i_pointsNumber++;

                if (li == _startCoords) break;

                // Ищем рядом
                // Расставляем волну

                // Верх
                int i_coord = li - GAMEFIELDWIDTH;
                if (i_coord >= 0)
                {
                    if (i_coord == _startCoords)
                    {
                        li = i_coord;
                        continue;
                    }

                    byte b_v = ab_arr[i_coord];
                    if (b_v == b_lastVal)
                    {
                        li = i_coord;
                        b_lastVal--;
                        if (b_lastVal < BRICK_PATHSVALUESTART) b_lastVal = BRICK_PATHSVALUEEND;
                        continue;
                    }
                }

                // Низ
                i_coord = li + GAMEFIELDWIDTH;
                if (i_coord < i_len)
                {
                    if (i_coord == _startCoords)
                    {
                        li = i_coord;
                        continue;
                    }

                    byte b_v = ab_arr[i_coord];
                    if (b_v == b_lastVal)
                    {
                        li = i_coord;
                        b_lastVal--;
                        if (b_lastVal < BRICK_PATHSVALUESTART) b_lastVal = BRICK_PATHSVALUEEND;
                        continue;
                    }
                }

                // Влево
                if (li % GAMEFIELDWIDTH > 0)
                {
                    i_coord = li - 1;
                    if (i_coord < i_len)
                    {
                        if (i_coord == _startCoords)
                        {
                            li = i_coord;
                            continue;
                        }

                        byte b_v = ab_arr[i_coord];
                        if (b_v == b_lastVal)
                        {
                            li = i_coord;
                            b_lastVal--;
                            if (b_lastVal < BRICK_PATHSVALUESTART) b_lastVal = BRICK_PATHSVALUEEND;
                            continue;
                        }
                    }
                }

                // Вправо
                if (li % GAMEFIELDWIDTH < (GAMEFIELDWIDTH - 1))
                {
                    i_coord = li + 1;
                    if (i_coord < i_len)
                    {
                        if (i_coord == _startCoords)
                        {
                            li = i_coord;
                            continue;
                        }

                        byte b_v = ab_arr[i_coord];
                        if (b_v == b_lastVal)
                        {
                            li = i_coord;
                            b_lastVal--;
                            if (b_lastVal < BRICK_PATHSVALUESTART) b_lastVal = BRICK_PATHSVALUEEND;
                            continue;
                        }
                    }
                }
            }

            ash_arra[0] = (short) (i_pointsNumber - 1);

            // Инверсия пути
            int i_pw = i_pointsNumber / 2;
            int i_startPoint = 2;
            int i_endPoint = i_pointOffset - 3;
            for (int lg = 0; lg < i_pw; lg++)
            {
                short sh_cx = ash_MoveBrickPath[i_startPoint];
                short sh_cy = ash_MoveBrickPath[i_startPoint + 1];
                short sh_cs = ash_MoveBrickPath[i_startPoint + 2];

                short sh_cx2 = ash_MoveBrickPath[i_endPoint];
                short sh_cy2 = ash_MoveBrickPath[i_endPoint + 1];
                short sh_cs2 = ash_MoveBrickPath[i_endPoint + 2];

                ash_MoveBrickPath[i_startPoint++] = sh_cx2;
                ash_MoveBrickPath[i_startPoint++] = sh_cy2;
                ash_MoveBrickPath[i_startPoint++] = sh_cs2;

                ash_MoveBrickPath[i_endPoint] = sh_cx;
                ash_MoveBrickPath[i_endPoint + 1] = sh_cy;
                ash_MoveBrickPath[i_endPoint + 2] = sh_cs;

                i_endPoint -= 3;
            }

        }

        // Очистка массива от значений
        for (int li = 0; li < i_len; li++)
        {
            byte b_val = ab_arr[li];
            ab_arr[li] = b_val >= BRICK_PATHSVALUESTART && b_val <= BRICK_PATHSVALUEEND ? BRICK_NONE : b_val;
        }

        return lg_foundEndPoint;
    }

    private static final boolean activateBrickDestroying(int _offset)
    {
        int i_x = (_offset % GAMEFIELDWIDTH) * CELLWIDTH + (CELLWIDTH >> 1);
        int i_y = (_offset / GAMEFIELDWIDTH) * CELLHEIGHT + (CELLHEIGHT >> 1);
        int i_sprOffset = p_SpriteBricksCollection.getOffsetOfLastInactiveSprite();
        if (i_sprOffset >= 0)
        {
            int i_val = ab_GameField[_offset];
            // Активизируем спрайт
            int i_spriteType = 0;
            int i_spriteState = 0;
            switch (i_val)
            {
                case BRICK_BLUE:
                {
                    i_spriteType = SPRITE_BRICKBLUE;
                    i_spriteState = SPRITE_BRICKBLUE_EXPLODING;
                }
                ;
                break;
                case BRICK_RED:
                {
                    i_spriteType = SPRITE_BRICKRED;
                    i_spriteState = SPRITE_BRICKRED_EXPLODING;
                }
                ;
                break;
                case BRICK_GREEN:
                {
                    i_spriteType = SPRITE_BRICKGREEN;
                    i_spriteState = SPRITE_BRICKGREEN_EXPLODING;
                }
                ;
                break;
                case BRICK_YELLOW:
                {
                    i_spriteType = SPRITE_BRICKYELLOW;
                    i_spriteState = SPRITE_BRICKYELLOW_EXPLODING;
                }
                ;
                break;
            }

            p_SpriteBricksCollection.activateSprite(i_spriteType, i_spriteState, i_sprOffset);
            p_SpriteBricksCollection.setSpriteNextTypeState(i_sprOffset, SpriteCollection.SPRITEBEHAVIOUR_RELEASE);
            p_SpriteBricksCollection.setMainPointXY(i_sprOffset, i_x, i_y);
            int i_pathWay = 0;
            for (int li = 0; li < PARTSPERBRICK; li++)
            {
                int i_partBlock = p_SpritePartsCollection.getOffsetOfLastInactiveSprite();
                if (i_partBlock < 0) break;
                PathController p_contr = getFirstInactivePath();
                if (p_contr == null) break;

                p_SpritePartsCollection.activateSprite(SPRITE_PART, SPRITE_PART_MOVING, i_partBlock);
                p_SpritePartsCollection.setMainPointXY(i_partBlock, i_x, i_y);

                int i_pathWayID = 0;
                switch (i_pathWay)
                {
                    case 0:
                    {
                        i_pathWayID = PATH_path_0;
                    }
                    ;
                    break;
                    case 1:
                    {
                        i_pathWayID = PATH_path_1;
                    }
                    ;
                    break;
                    case 2:
                    {
                        i_pathWayID = PATH_path_2;
                    }
                    ;
                    break;
                    case 3:
                    {
                        i_pathWayID = PATH_path_3;
                    }
                    ;
                    break;
                    case 4:
                    {
                        i_pathWayID = PATH_path_4;
                    }
                    ;
                    break;
                }
                i_pathWay++;
                p_contr.initPath(0, i_x, i_y, 0x100, 0x100, ap_SpriteCollections, p_SpritePartsCollection.i_CollectionID, i_partBlock, ash_Paths, i_pathWayID, 0, 0, PathController.MODIFY_NONE);
                p_contr.setNotifyFlags(PathController.NOTIFY_ENDPOINT);
            }
            ab_GameField[_offset] = BRICK_NONE;
            i_PlayerScore += i_ScoresForOneBrick;
            startup.updateCell(_offset);
            return true;
        }
        else return false;
    }

    private static final int getBricksCoordsForDestroying()
    {
        final int i_len = GAMEFIELDHEIGHT * GAMEFIELDWIDTH;
        int i_summaryNumber = 0;
        for (int li = 0; li < i_len; li++) i_summaryNumber += alg_DestroyedBricks[li] ? 1 : 0;
        if (i_summaryNumber == 0) return -1;

        int i_pos = getRandomInt(((i_summaryNumber - 1) * 1000) + 999) / 1000;

        int i_indx = 0;
        for (int li = 0; li < i_len; li++)
        {
            if (alg_DestroyedBricks[li])
            {
                if (i_indx == i_pos) return li;
                i_indx++;
            }
        }
        return -1;
    }

    /**
     * Возвращает количество кубиков на поле
     *
     * @return количество кубиков
     */
    private static final int getBricksNum()
    {
        final int i_len = GAMEFIELDHEIGHT * GAMEFIELDWIDTH;
        int i_num = 0;
        for (int li = 0; li < i_len; li++) i_num += ab_GameField[li] == BRICK_NONE ? 0 : 1;
        return i_num;
    }

    private static final int getCoordsForNewBricks()
    {
        int i_summaryNumber = 0;

        final byte [] ab_generationCells = GENERATIONCELLS;
        final int i_len = ab_generationCells.length;
        for (int li = 0; li < i_len; li++)
        {
            int i_coord = ab_generationCells[li];
            if (ab_GameField[i_coord] == BRICK_NONE) i_summaryNumber ++;
        }

        if (i_summaryNumber == 0) return -1;

        int i_pos = getRandomInt(((i_summaryNumber - 1) * 1000) + 999) / 1000;

        int i_indx = 0;
        for (int li = 0; li < i_len; li++)
        {
            int i_coord = ab_generationCells[li];

            if (ab_GameField[i_coord] == BRICK_NONE)
            {
                if (i_indx == i_pos) return i_coord;
                i_indx++;
            }
        }
        return -1;
    }

    private static final void initField()
    {
        for (int li = 0; li < ab_GameField.length; li++) ab_GameField[li] = BRICK_NONE;
    }

    private static final short [] loadSpriteArray(Class _class, String _resource)
    {
        try
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
        catch (Throwable _thr)
        {
            return null;
        }
    }

    public static int getCellX8(int _x)
    {
        return (_x * Gamelet.CELLWIDTH + 0x7F)&0xFFFFFF00;
    }

    public static int getCellY8(int _y)
    {
        return  (_y * Gamelet.CELLHEIGHT + 0x7F)&0xFFFFFF00;
    }

    private static final PathController getFirstInactivePath()
    {
        final PathController[] ap_arr = ap_PartsPaths;
        for (int li = 0; li < MAX_PARTSSPRITES; li++)
        {
            if (ap_arr[li].isCompleted()) return ap_arr[li];
        }
        return null;
    }

    /**
     * Координата X выбранного кубика
     */
    protected static int i_SelectedBrickCoordX;
    /**
     * Координата Y выбранного кубика
     */
    protected static int i_SelectedBrickCoordY;

    /**
     * Тип выбранного кубика
     */
    protected static int i_SelectedBrickType;

    /**
     * Смещение спрайта выбранного кубика
     */
    protected static int i_SelectedBrickSpriteOffset;

    /**
     * Координата X точки назначения кубика
     */
    protected static int i_SelectedBrickDestCoordX;

    /**
     * Координата Y точки назначения кубика
     */
    protected static int i_SelectedBrickDestCoordY;


    public static final byte [] GENERATIONCELLS = new byte[]
            {

                    1, 3, 5, 7,
                    8, 10, 12, 14,
                    17, 19, 21, 23,
                    24, 26, 28, 30,
                    33, 35, 37, 39,
                    40, 42, 44, 46,
                    49, 51, 53, 55,
                    56, 58, 60, 62


            };

    // The array contains values for path controllers
    public static final short [] ash_Paths = new short[]{
            // PATH_path_0
            (short) 1, (short) 0, (short) 0, (short) 0, (short) 10, (short) 58, (short) 58, (short) 10,
            // PATH_path_1
            (short) 1, (short) 0, (short) 0, (short) 0, (short) 10, (short) 75, (short) -9, (short) 10,
            // PATH_path_2
            (short) 1, (short) 0, (short) 0, (short) 0, (short) 10, (short) -33, (short) -11, (short) 10,
            // PATH_path_3
            (short) 1, (short) 0, (short) 0, (short) 0, (short) 10, (short) -26, (short) 28, (short) 10,
            // PATH_path_4
            (short) 1, (short) 0, (short) 0, (short) 0, (short) 10, (short) 22, (short) -26, (short) 10,
    };

    // PATH offsets
    private static final int PATH_path_0 = 0;
    private static final int PATH_path_1 = 8;
    private static final int PATH_path_2 = 16;
    private static final int PATH_path_3 = 24;
    private static final int PATH_path_4 = 32;

//------------------Sprite constants-----------------
    public static final int SPRITE_BRICKRED = 0;
    public static final int SPRITE_BRICKRED_SELECTED = 0;
    public static final int SPRITE_BRICKRED_ROTATING = 1;
    public static final int SPRITE_BRICKRED_MOVING = 2;
    public static final int SPRITE_BRICKRED_EXPLODING = 3;
    public static final int SPRITE_BRICKRED_INCOMING = 4;
    public static final int SPRITE_BRICKGREEN = 1;
    public static final int SPRITE_BRICKGREEN_SELECTED = 0;
    public static final int SPRITE_BRICKGREEN_ROTATING = 1;
    public static final int SPRITE_BRICKGREEN_MOVING = 2;
    public static final int SPRITE_BRICKGREEN_EXPLODING = 3;
    public static final int SPRITE_BRICKGREEN_INCOMING = 4;
    public static final int SPRITE_BRICKBLUE = 2;
    public static final int SPRITE_BRICKBLUE_SELECTED = 0;
    public static final int SPRITE_BRICKBLUE_ROTATING = 1;
    public static final int SPRITE_BRICKBLUE_MOVING = 2;
    public static final int SPRITE_BRICKBLUE_EXPLODING = 3;
    public static final int SPRITE_BRICKBLUE_INCOMING = 4;
    public static final int SPRITE_BRICKYELLOW = 3;
    public static final int SPRITE_BRICKYELLOW_SELECTED = 0;
    public static final int SPRITE_BRICKYELLOW_ROTATING = 1;
    public static final int SPRITE_BRICKYELLOW_MOVING = 2;
    public static final int SPRITE_BRICKYELLOW_EXPLODING = 3;
    public static final int SPRITE_BRICKYELLOW_INCOMING = 4;
    public static final int SPRITE_PART = 4;
    public static final int SPRITE_PART_MOVING = 0;

}
