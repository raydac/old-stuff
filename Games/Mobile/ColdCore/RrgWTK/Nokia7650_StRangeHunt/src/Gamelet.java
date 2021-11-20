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
        ash_SpritesTable = loadSpriteArray(_class,"/spr.bin");

        PathController.SCALE_WIDTH = startup.SCALE_WIDTH;
        PathController.SCALE_HEIGHT = startup.SCALE_HEIGHT;

        ap_SpriteCollection = new SpriteCollection[6];
        ap_SpriteCollection[COLLECTION_PLAYER] = new SpriteCollection(COLLECTION_PLAYER,NUMBER_PLAYER,ash_SpritesTable);
        ap_SpriteCollection[COLLECTION_PLAYERSIGHT] = new SpriteCollection(COLLECTION_PLAYERSIGHT,NUMBER_PLAYERSIGHT,ash_SpritesTable);
        ap_SpriteCollection[COLLECTION_BACKGROUNDOBJECTS] = new SpriteCollection(COLLECTION_BACKGROUNDOBJECTS,NUMBER_BACKGROUNDOBJECTS,ash_SpritesTable);
        ap_SpriteCollection[COLLECTION_ENEMYFIRING] = new SpriteCollection(COLLECTION_ENEMYFIRING,NUMBER_ENEMYFIRING,ash_SpritesTable);
        ap_SpriteCollection[COLLECTION_FIRSTPLANEOBJECTS] = new SpriteCollection(COLLECTION_FIRSTPLANEOBJECTS,NUMBER_FIRSTPLANEOBJECTS,ash_SpritesTable);
        ap_SpriteCollection[COLLECTION_CANE] = new SpriteCollection(COLLECTION_CANE,NUMBER_CANE,ash_SpritesTable);

        ap_Paths = new PathController[MAXPATHS];
        for(int li=0;li<MAXPATHS;li++) ap_Paths[li] = new PathController();

        // Создаем массив ячеек занятости и задержки кабана и танка
        int i_cellsNum = ash_Paths[PATH_BOARTANKGENERATEDPATH]+1;
        ab_BoarTankCells = new byte[i_cellsNum];

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

        ap_SpriteCollection = null;
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
        switch (_gameLevel)
        {
            case GAMELEVEL_EASY:
            {
                i_PlayerAttemptions = ATTEMPTIONS_EASY;

                i_FreqBoar = FREQ_EASY_BOAR;
                i_FreqDuck = FREQ_EASY_DUCK;
                i_FreqFighter = FREQ_EASY_FIGHTER;
                i_FreqTank = FREQ_EASY_TANK;
                i_FreqFighterFiring = FREQ_EASY_FIGHTERFIRE;
                i_FreqTankFiring = FREQ_EASY_TANKFIRE;

                i_MaxCurrentInterval = MAXINTERVAL_EASY;
            }
            ;
            break;
            case GAMELEVEL_NORMAL:
            {
                i_PlayerAttemptions = ATTEMPTIONS_NORMAL;

                i_FreqBoar = FREQ_NORMAL_BOAR;
                i_FreqDuck = FREQ_NORMAL_DUCK;
                i_FreqFighter = FREQ_NORMAL_FIGHTER;
                i_FreqTank = FREQ_NORMAL_TANK;
                i_FreqFighterFiring = FREQ_NORMAL_FIGHTERFIRE;
                i_FreqTankFiring = FREQ_NORMAL_TANKFIRE;

                i_MaxCurrentInterval = MAXINTERVAL_NORMAL;
            }
            ;
            break;
            case GAMELEVEL_HARD:
            {
                i_PlayerAttemptions = ATTEMPTIONS_HARD;

                i_FreqBoar = FREQ_HARD_BOAR;
                i_FreqDuck = FREQ_HARD_DUCK;
                i_FreqFighter = FREQ_HARD_FIGHTER;
                i_FreqTank = FREQ_HARD_TANK;
                i_FreqFighterFiring = FREQ_HARD_FIGHTERFIRE;
                i_FreqTankFiring = FREQ_HARD_TANKFIRE;

                i_MaxCurrentInterval = MAXINTERVAL_HARD;
            }
            ;
            break;
            default:
                return false;
        }

        i_PlayerScore = 0;

        lg_FigherPresented = false;

        for(int li=0;li<ab_BoarTankCells.length;li++) ab_BoarTankCells[li] = 0;

        // Деактивизация всех объектов
        for(int li=0;li<ap_SpriteCollection.length;li++) ap_SpriteCollection[li].releaseAllSprites();

        initNewPlayerAttemption();

        // Тростник
        SpriteCollection p_cane = ap_SpriteCollection[COLLECTION_CANE];
        p_cane.activateSprite(SPRITE_OBJ_CANE,SPRITE_STATE_CANE_STAND,0);
        p_cane.setMainPointXY(0,0,CANE_Y);

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
        i8_CurrentInterval = p_dis.readInt();
        i_PlayerScore = p_dis.readInt();
        i_SubState =  p_dis.readUnsignedByte();
        lg_FigherPresented = p_dis.readBoolean();
        lg_lastGen = p_dis.readBoolean();
        for(int li=0;li<ab_BoarTankCells.length;li++) ab_BoarTankCells [li] = p_dis.readByte();
        ap_SpriteCollection[COLLECTION_PLAYER].loadFromStream(p_dis);
        ap_SpriteCollection[COLLECTION_ENEMYFIRING].loadFromStream(p_dis);
        ap_SpriteCollection[COLLECTION_FIRSTPLANEOBJECTS].loadFromStream(p_dis);
        ap_SpriteCollection[COLLECTION_PLAYERSIGHT].loadFromStream(p_dis);
        ap_SpriteCollection[COLLECTION_BACKGROUNDOBJECTS].loadFromStream(p_dis);
        for(int li=0;li<MAXPATHS;li++) ap_Paths[li].readPathFromStream(p_dis,ap_SpriteCollection,ash_Paths);

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
        p_dos.writeInt(i8_CurrentInterval);
        p_dos.writeInt(i_PlayerScore);
        p_dos.writeByte(i_SubState);
        p_dos.writeBoolean(lg_FigherPresented);
        p_dos.writeBoolean(lg_lastGen);
        for(int li=0;li<ab_BoarTankCells.length;li++) p_dos.writeByte(ab_BoarTankCells[li]);
        ap_SpriteCollection[COLLECTION_PLAYER].saveToStream(p_dos);
        ap_SpriteCollection[COLLECTION_ENEMYFIRING].saveToStream(p_dos);
        ap_SpriteCollection[COLLECTION_FIRSTPLANEOBJECTS].saveToStream(p_dos);
        ap_SpriteCollection[COLLECTION_PLAYERSIGHT].saveToStream(p_dos);
        ap_SpriteCollection[COLLECTION_BACKGROUNDOBJECTS].saveToStream(p_dos);
        for(int li=0;li<MAXPATHS;li++) ap_Paths[li].writePathToStream(p_dos);

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
        Size += 11;
        Size += SpriteCollection.getDataSize(NUMBER_PLAYER);
        Size += SpriteCollection.getDataSize(NUMBER_BACKGROUNDOBJECTS);
        Size += SpriteCollection.getDataSize(NUMBER_ENEMYFIRING);
        Size += SpriteCollection.getDataSize(NUMBER_FIRSTPLANEOBJECTS);
        Size += SpriteCollection.getDataSize(NUMBER_PLAYERSIGHT);
        Size += PathController.DATASIZE_BYTES*MAXPATHS;
        Size += ash_Paths[PATH_BOARTANKGENERATEDPATH]+1;
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
        return "SIM_RAHNT";
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
        SpriteCollection p_sprCol = ap_SpriteCollection[_collectionID];
        int i_type = p_sprCol.getSpriteType(_spriteOffset);
        int i_state = p_sprCol.getSpriteState(_spriteOffset);
        if (i_type==SPRITE_OBJ_TANK && i_state==SPRITE_STATE_TANK_SHOOTING)
        {
            PathController p_controller = getInactivePathController();

            if (p_controller==null)
            {
                // принудительно удаляем объект
                int i_opt = p_sprCol.getOptionalData(_spriteOffset);
                ab_BoarTankCells[i_opt] = 0;
                p_sprCol.releaseSprite(_spriteOffset);
                return;
            }
            p_sprCol.activateSprite(SPRITE_OBJ_TANK,SPRITE_STATE_TANK_STAND,_spriteOffset);
            int i_mx = p_sprCol.ai_spriteDataArray[_spriteOffset+SpriteCollection.SPRITEDATAOFFSET_MAINX];
            int i_my = p_sprCol.ai_spriteDataArray[_spriteOffset+SpriteCollection.SPRITEDATAOFFSET_MAINY];
            p_controller.initPath(PATH_BOARTANKDOWNPATH,i_mx,i_my,0x100,0x100,ap_SpriteCollection,COLLECTION_BACKGROUNDOBJECTS,_spriteOffset,ash_Paths,PATH_BOARTANKDOWNPATH,0,0,0);
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

    static boolean lg_lastGen = false;

    /**
     * Функция отрабатывает игровой шаг
     *
     * @param _keyStateFlags флаги управления игроком.
     * @return статус игры после отработки игровой интерации
     */
    public static int nextGameStep(int _keyStateFlags)
    {
        //------------Вставьте свой код здесь--------------------

        // Обработка путей
        for(int li=0;li<MAXPATHS;li++)
        {
            if (ap_Paths[li].lg_completed) continue;
            ap_Paths[li].processStep();
        }

        // Отработка анимаций
        SpriteCollection [] ap_sprites = ap_SpriteCollection;

        ap_sprites[COLLECTION_BACKGROUNDOBJECTS].processAnimationForActiveSprites();
        ap_sprites[COLLECTION_FIRSTPLANEOBJECTS].processAnimationForActiveSprites();
        ap_sprites[COLLECTION_PLAYER].processAnimationForActiveSprites();
        ap_sprites[COLLECTION_PLAYERSIGHT].processAnimationForActiveSprites();
        ap_sprites[COLLECTION_ENEMYFIRING].processAnimationForActiveSprites();

        switch (i_SubState)
        {
            case SUBSTATE_PLAYERIN :
            {

            };break;
            case SUBSTATE_PLAYERKILLED :
            {
                if (ap_SpriteCollection[COLLECTION_PLAYER].i_lastActiveSpriteOffset<0)
                    if (ap_SpriteCollection[COLLECTION_ENEMYFIRING].i_lastActiveSpriteOffset<0)
                        if (ap_SpriteCollection[COLLECTION_FIRSTPLANEOBJECTS].i_lastActiveSpriteOffset<0)
                            if (ap_SpriteCollection[COLLECTION_BACKGROUNDOBJECTS].i_lastActiveSpriteOffset<0)
                            {
                                if (i_PlayerAttemptions==0)
                                {
                                    i_GameState = STATE_OVER;
                                    i_PlayerState = PLAYER_LOST;
                                }
                                else
                                {
                                    i_PlayerAttemptions--;
                                    initNewPlayerAttemption();
                                }
                            }

            };break;
            case SUBSTATE_PLAYERHUNTING :
            {
                // Отработка уменьшения интервала появления объект
                if (i8_CurrentInterval > 0)
                    i8_CurrentInterval -= INTERVALSTEP;
                if (i8_CurrentInterval <= 0)
                {
                    i8_CurrentInterval = i_MaxCurrentInterval;

                    // Генерация
                    // Уток
                    if (!generateHorzDuck())
                    {
                        if (!generateDuckFighter() || lg_FigherPresented)
                        {
                            if (!generateBoarTank())
                            {
                            }
                        }
                    }
                }

                // Отработка движения игрока по экрану
                SpriteCollection p_sprCol = ap_SpriteCollection[COLLECTION_PLAYERSIGHT];
                int i_curX = p_sprCol.ai_spriteDataArray[SpriteCollection.SPRITEDATAOFFSET_MAINX];
                int i_curY = p_sprCol.ai_spriteDataArray[SpriteCollection.SPRITEDATAOFFSET_MAINY];

                int i_oldX = i_curX;
                int i_oldY = i_curY;

                if ((_keyStateFlags & BUTTON_LEFT) != 0)
                {
                    // прицел влево
                    i_curX -= I8_HORZPLAYERSPEED;
                }
                else if ((_keyStateFlags & BUTTON_RIGHT) != 0)
                {
                    // прицел вправо
                    i_curX += I8_HORZPLAYERSPEED;
                }

                if ((_keyStateFlags & BUTTON_UP) != 0)
                {
                    // прицел вверх
                    i_curY -= I8_VERTPLAYERSPEED;
                }
                else if ((_keyStateFlags & BUTTON_DOWN) != 0)
                {
                    // прицел вниз
                    i_curY += I8_VERTPLAYERSPEED;
                }

                p_sprCol.setMainPointXY(0,i_curX,i_curY);

                // Выравниваем  прицелв зоне
                p_sprCol.alignSpriteToArea(0, LEFTSIGHTAREA_X, LEFTSIGHTAREA_Y, RIGHTSIGHTAREA_X, RIGHTSIGHTAREA_Y, true);

                // проверяем перемещение прицела
                i_curX = p_sprCol.ai_spriteDataArray[SpriteCollection.SPRITEDATAOFFSET_MAINX];

                SpriteCollection p_hunterCollection = ap_SpriteCollection[COLLECTION_PLAYER];
                int i_state = p_hunterCollection.getSpriteState(0);

                if (i_oldX != i_curX)
                {
                    // Было осуществлено премещение прицела по горизонтали, включаем анимацию охотника (если она уже не включена)

                    if (i_oldX<i_curX)
                    {
                        // Идет в право
                        if (i_state!=SPRITE_STATE_HUNTER_MOVERIGHT)
                        {
                            p_hunterCollection.activateSprite(SPRITE_OBJ_HUNTER,SPRITE_STATE_HUNTER_MOVERIGHT,0);
                        }
                    }
                    else
                    {
                        // Идет в лево
                        if (i_state!=SPRITE_STATE_HUNTER_MOVELEFT)
                        {
                            p_hunterCollection.activateSprite(SPRITE_OBJ_HUNTER,SPRITE_STATE_HUNTER_MOVELEFT,0);
                        }
                    }

                    // Выравниваем точки по координате X
                    int i_hY = p_hunterCollection.ai_spriteDataArray[SpriteCollection.SPRITEDATAOFFSET_MAINY];

                    p_hunterCollection.setMainPointXY(0,i_curX,i_hY);
                }
                else
                {
                    // поворачиваем игрока если он идет
                    if (i_state!=SPRITE_STATE_HUNTER_STAND)
                    {
                        p_hunterCollection.activateSprite(SPRITE_OBJ_HUNTER,SPRITE_STATE_HUNTER_STAND,0);
                    }

                    if ((_keyStateFlags & BUTTON_FIRE)!=0)
                    {
                        // Проверяем текущий выстрел
                        if (i_state!=SPRITE_STATE_HUNTER_SHOOTING)
                        {
                            p_hunterCollection.activateSprite(SPRITE_OBJ_HUNTER,SPRITE_STATE_HUNTER_SHOOTING,0);
                            p_hunterCollection.setSpriteNextTypeState(0,SPRITE_STATE_HUNTER_STAND);

                            // активизируем выстрел
                            p_sprCol.activateSprite(SPRITE_OBJ_SHOOT,SPRITE_STATE_SHOOT_STAND,i_OffsetSprite_Shot);
                            // выравниваем координаты с прицелом
                            p_sprCol.alignMainPoint(i_OffsetSprite_Shot,0);
                            p_sprCol.setSpriteNextTypeState(i_OffsetSprite_Shot,SpriteCollection.SPRITEBEHAVIOUR_RELEASE);

                            // уничтожаем объекты попавшие в прицел
                            checkPlayerKilling();
                        }
                    }
                }

                // Проверка на гибель игрока от снарядов
                checkPlayerKilled();
            }
            ;
            break;
        }
        //--------------------------------------------------
        return i_GameState;
    }

    //------------------Игровые события---------------------
    public static final int GAMEACTION_FIGHTERFIRE = 0;
    public static final int GAMEACTION_HIT = 1;
    public static final int GAMEACTION_TANKFIRE = 2;
    public static final int GAMEACTION_PLAYERKILLED = 3;

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
     * Очки игроку за сбитую горизонтальною утку
     */
    private static final int SCORE_HORZDUCK = 10;

    /**
     * Очки игроку за сбитую дальнюю утку
     */
    private static final int SCORE_LONGDUCK = 20;

    /**
     * Очки игроку за кабана
     */
    private static final int SCORE_BOAR = 50;

    private static final int SPLASHPOINTSTOACTIVE = 2; // Количество точек до перехода всплеска от выстрела самолета в активное состояние
    private static final int SUBSTATE_PLAYERIN = 0; // подсостояние игры
    private static final int SUBSTATE_PLAYERHUNTING = 1; // игрок в состоянии охоты
    private static final int SUBSTATE_PLAYERKILLED = 2; // игрок в состоянии смерти

    public static final int LEFTSIGHTAREA_X = 0;
    public static final int LEFTSIGHTAREA_Y = 0;
    public static final int RIGHTSIGHTAREA_X = 176 * startup.SCALE_WIDTH;
    public static final int RIGHTSIGHTAREA_Y = 100 * startup.SCALE_HEIGHT;

    /**
     * Количество спрайтов в коллекции "Игрок", должно быть 1
     */
    private static final int NUMBER_PLAYER = 1;
    /**
     * Количество спрайтов в коллекции "Прицел игрока", должно быть три "прицел","вспышка","очки"
     */
    private static final int NUMBER_PLAYERSIGHT = 3;
    /**
     * Количество спрайтов в коллекции дальних объектов
     */
    private static final int NUMBER_BACKGROUNDOBJECTS = 8;
    /**
     * Количество спрайтов в коллекции "Выстрелы противника"
     */
    private static final int NUMBER_ENEMYFIRING = 8;
    /**
     * Количетсво объектов в коллекции первого плана (летящие горизонтальные утки)
     */
    private static final int NUMBER_FIRSTPLANEOBJECTS = 5;
    /**
     * Коллекция, содержит объекты "Тростник"
     */
    private static final int NUMBER_CANE = 1;

    /**
     * Координата верхнего левого края тростника
     */
    private static final int CANE_Y = 78*startup.SCALE_HEIGHT;

    /**
     * Координата верхнего левого края воды, там где начинаются всплески от выстрелов самолета
     */
    private static final int SPLASH_Y = 150*startup.SCALE_HEIGHT;

    /**
     * Максимальное количество путей
     */
    private static final int MAXPATHS = 10;

    /**
     * Коллекция содержит объект ИГРОКА
     */
    public static final int COLLECTION_PLAYER = 0;

    /**
     * Коллекция ПРИЦЕЛ ИГРОКА и анимации выстрела со всплывающими очками
     */
    public static final int COLLECTION_PLAYERSIGHT = 1;

    /**
     * Коллекция объекта Камыш
     */
    public static final int COLLECTION_CANE = 2;

    /**
     * Коллекция объектов на самом заднем плане САМОЛЕТ, УТКА ВДАЛИ, ТАНК,
     */
    public static final int COLLECTION_BACKGROUNDOBJECTS = 3;

    /**
     * Коллекция объектов первого плана (летящие по горизонтали утки)
     */
    public static final int COLLECTION_FIRSTPLANEOBJECTS = 4;

    /**
     * Коллекция объектов "выстрелов противника"
     */
    public static final int COLLECTION_ENEMYFIRING = 5;

    private static final int FREQ_EASY_BOAR = 15;
    private static final int FREQ_EASY_TANK = 20;
    private static final int FREQ_EASY_FIGHTER = 20;
    private static final int FREQ_EASY_DUCK = 15;
    private static final int FREQ_EASY_TANKFIRE = 2;
    private static final int FREQ_EASY_FIGHTERFIRE = 2;

    private static final int FREQ_NORMAL_BOAR = 25;
    private static final int FREQ_NORMAL_TANK = 10;
    private static final int FREQ_NORMAL_FIGHTER = 10;
    private static final int FREQ_NORMAL_DUCK = 20;
    private static final int FREQ_NORMAL_TANKFIRE = 1;
    private static final int FREQ_NORMAL_FIGHTERFIRE = 1;

    private static final int FREQ_HARD_BOAR = 40;
    private static final int FREQ_HARD_TANK = 10;
    private static final int FREQ_HARD_FIGHTER = 10;
    private static final int FREQ_HARD_DUCK = 50;
    private static final int FREQ_HARD_TANKFIRE = 1;
    private static final int FREQ_HARD_FIGHTERFIRE = 0;

    private static final int MAXINTERVAL_HARD = 40;
    private static final int MAXINTERVAL_NORMAL = 40;
    private static final int MAXINTERVAL_EASY = 50;

    private static final int ATTEMPTIONS_EASY = 3;
    private static final int ATTEMPTIONS_NORMAL = 2;
    private static final int ATTEMPTIONS_HARD = 1;

    /**
     * Шаг уменьшения интервала между появлением объектов противника
     */
    private static final int INTERVALSTEP = 0x10;

    /**
     * Горизонтальная скорость игрока
     */
    private static final int I8_HORZPLAYERSPEED = 0x400;

    /**
     * Вертикальная скорость прицела
     */
    private static final int I8_VERTPLAYERSPEED = 0x500;

//todo Переменные
    /**
     * Флаг, показывет что на экране есть боковая утка или самолет
     */
    private static boolean lg_FigherPresented;

    /**
     * Смещение спрайта выстрела
     */
    private static int i_OffsetSprite_Shot;

    /**
     * Смещение спрайта очков
     */
    private static int i_OffsetSprite_Score;

    /**
     * Текущее подсостояние игры
     */
    public static int i_SubState;

    /**
     * Массив коллекция спрайтов
     */
    public static SpriteCollection [] ap_SpriteCollection;

    /**
     * Массив путей спрайтов
     */
    private static PathController [] ap_Paths;

    /**
     * Массив флагов занятости ячеек кабана и танка
     */
    private static byte [] ab_BoarTankCells;

    /**
     * Макимальное значение интервала для уровня
     */
    private static int i_MaxCurrentInterval;

    /**
     * Текущее значение интервала между поялениями
     */
    private static int i8_CurrentInterval;

    /**
     * Частота появления кабана на уровне
     */
    private static int i_FreqBoar;

    /**
     * Частота появления танка на уровне
     */
    private static int i_FreqTank;

    /**
     * Частота появления истребителя на уровне
     */
    private static int i_FreqFighter;

    /**
     * Частота стрельбы истребителя на уровне
     */
    private static int i_FreqFighterFiring;

    /**
     * Частота стрельбы танка на уровне
     */
    private static int i_FreqTankFiring;

    /**
     * Частота появления утки на уровне
     */
    private static int i_FreqDuck;

//todo Функции

    private static final void releaseAllPaths()
    {
        for(int li=0;li<MAXPATHS;li++)
        {
            ap_Paths[li].lg_completed = true;
        }
    }

    private static final int calculateFreeBoarTankPosition()
    {
        int i_num = 0;
        for(int li=0;li<ab_BoarTankCells.length;li++)
        {
            if(ab_BoarTankCells[li]==0) i_num++;
        }
        return i_num;
    }

    private static final boolean generateBoarTank()
    {
        boolean lg_generated = false;
        if (getRandomInt(i_FreqTank)==(i_FreqTank>>1))
        {
            int i_freePos = calculateFreeBoarTankPosition();
            if (i_freePos==0) return false;

            // производим генерацию
            lg_generated = addBoarTank(i_freePos,false);
        }
        else
        if (getRandomInt(i_FreqBoar)==(i_FreqBoar>>1))
        {
            int i_freePos = calculateFreeBoarTankPosition();
            if (i_freePos==0) return false;

            // производим генерацию
            lg_generated = addBoarTank(i_freePos,true);
        }

        return lg_generated;
    }

    private static final boolean generateDuckFighter()
    {
        if (lg_FigherPresented) return false;
        boolean lg_generated = false;
        if (getRandomInt(i_FreqFighter)!=(i_FreqFighter>>1)) return false;

        PathController p_path = getInactivePathController();
        if (p_path==null) return false;

        SpriteCollection p_sprCol = ap_SpriteCollection[COLLECTION_BACKGROUNDOBJECTS];

        int i_spr = p_sprCol.i_lastInactiveSpriteOffset;
        if (i_spr<0) return false;

        int i_object = 0;
        int i_state = 0;
        int i_path = 0;

        if (getRandomInt(1000)>500)
        {
            i_path = PATH_FIGHTERLEFT;
            if (getRandomInt(1000)>500)
            {
                i_object = SPRITE_OBJ_FIGHTERLEFT;
                i_state = SPRITE_STATE_FIGHTERLEFT_STATE1;
            }
            else
            {
                i_object = SPRITE_OBJ_DUCKLONGLEFT;
                i_state = SPRITE_STATE_DUCKLONGLEFT_STATE1;
            }
        }
        else
        {
            i_path = PATH_FIGHTERRIGHT;
            if (getRandomInt(1000)>500)
            {
                i_object = SPRITE_OBJ_FIGHTERRIGHT;
                i_state = SPRITE_STATE_FIGHTERLEFT_STATE1;
            }
            else
            {
                i_object = SPRITE_OBJ_DUCKLONGRIGHT;
                i_state = SPRITE_STATE_DUCKLONGRIGHT_STATE1;
            }
        }

        p_sprCol.activateSprite(i_object,i_state,i_spr);
        p_path.initPath(i_path,0,0,0x100,0x100,ap_SpriteCollection,COLLECTION_BACKGROUNDOBJECTS,i_spr,ash_Paths,i_path,0,0,0);

        lg_FigherPresented = true;

        return lg_generated;
    }

    private static final boolean addBoarTank(int _emptiFieldsNumber,boolean _boar)
    {
        int i_pos = 0;
        int i_lastpos = 0;
        int i_selected = (getRandomInt((_emptiFieldsNumber-1)*1000-1)/1000)+1;

        while(i_selected>0)
        {
            if (ab_BoarTankCells[i_pos]==0)
            {
                i_lastpos = i_pos;
                i_selected--;
            }
            i_pos++;
        }

        int i_offst = PATH_BOARTANKGENERATEDPATH+2+(i_lastpos<<1);
        int i_x = ash_Paths[i_offst++]*startup.SCALE_WIDTH;
        int i_y = ash_Paths[i_offst]*startup.SCALE_HEIGHT;

        int i_object = _boar ? SPRITE_OBJ_BOAR : SPRITE_OBJ_TANK;
        int i_objectState = _boar ? SPRITE_STATE_BOAR_LIVING : SPRITE_STATE_TANK_STAND;

        SpriteCollection p_col = ap_SpriteCollection[COLLECTION_BACKGROUNDOBJECTS];
        int i_sprite = p_col.i_lastInactiveSpriteOffset;
        if (i_sprite<0) return false;
        PathController p_path = getInactivePathController();
        if (p_path==null) return false;

        p_col.activateSprite(i_object,i_objectState,i_sprite);
        p_path.initPath(PATH_BOARTANKUPPATH,i_x,i_y,0x100,0x100,ap_SpriteCollection,COLLECTION_BACKGROUNDOBJECTS,i_sprite,ash_Paths,PATH_BOARTANKUPPATH,0,0,0);
        p_col.setOptionalData(i_sprite,i_lastpos);
        ab_BoarTankCells[i_lastpos]=1;

        return true;
    }

    private static final void checkPlayerKilled()
    {
        SpriteCollection p_player = ap_SpriteCollection[COLLECTION_PLAYER];
        SpriteCollection p_enemyFire = ap_SpriteCollection[COLLECTION_ENEMYFIRING];

        int i_spr = -1;
        boolean lg_killed = false;
        while(true)
        {
            i_spr = p_enemyFire.findCollidedSprite(0,p_player,i_spr);
            if (i_spr<0) return;
            int i_type = p_enemyFire.getSpriteType(i_spr);
            if (i_type==SPRITE_OBJ_TANKSHELL)
            {
                // Должен быть последний кадр анимации, который значит что бомба на уровне игрока
                int i_sprinfo = p_enemyFire.getSpriteFrameInfo(i_spr);
                if ((i_sprinfo&0xFFFF) == ((i_sprinfo>>>16)-1))
                {
                    lg_killed = true;
                    break;
                }
            }
            else
            {
                // Всплеск должен быть активизирован
                if (p_enemyFire.getOptionalData(i_spr)!=0)
                {
                    lg_killed = true;
                    break;
                }
            }
        }

        if (lg_killed)
        {
            PathController p_path = getActivePathForSprite(COLLECTION_ENEMYFIRING,i_spr);
            p_enemyFire.releaseSprite(i_spr);

            i_SubState = SUBSTATE_PLAYERKILLED;

            startup.processGameAction(GAMEACTION_PLAYERKILLED);

            ap_SpriteCollection[COLLECTION_PLAYERSIGHT].releaseAllSprites();

            p_player.activateSprite(SPRITE_OBJ_HUNTER,SPRITE_STATE_HUNTER_DEATH,0);
            int i_x = p_player.ai_spriteDataArray[SpriteCollection.SPRITEDATAOFFSET_MAINX];
            int i_y = p_player.ai_spriteDataArray[SpriteCollection.SPRITEDATAOFFSET_MAINY];

            p_path.initPath(PATH_HUNTERDEATH,i_x,i_y,0x100,0x100,ap_SpriteCollection,COLLECTION_PLAYER,0,ash_Paths,PATH_HUNTERDEATH,0,0,0);
        }
    }

    private static final boolean generateHorzDuck()
    {
        if (getRandomInt(i_FreqDuck)!=(i_FreqDuck>>1)) return false;
        int i_offset = PATH_HORZDUCKGENERATIONPATH;
        int i_pointsNum = ash_Paths[i_offset++];
        i_offset++;
        int i_index = getRandomInt(i_pointsNum*1000-1)/1000;
        boolean lg_left = getRandomInt(1000)>500 ? true : false;

        int i_path;
        int i_spritestate;

        int i_x = 0;

        if (lg_left)
        {
            i_path = PATH_DUCKTOLEFT;
            i_x = 176*startup.SCALE_WIDTH;
            i_spritestate = SPRITE_STATE_DUCKLEFTRIGHT_LEFT;
        }
        else
        {
            i_path = PATH_DUCKTORIGHT;
            i_spritestate = SPRITE_STATE_DUCKLEFTRIGHT_RIGHT;
        }

        SpriteCollection p_coll = ap_SpriteCollection[COLLECTION_FIRSTPLANEOBJECTS];

        int i_spriteOffset = p_coll.i_lastInactiveSpriteOffset;
        if (i_spriteOffset<0) return false;
        PathController p_path = getInactivePathController();
        if (p_path==null) return false;

        i_index = i_offset+(i_index<<1)+1;
        int i_y = ash_Paths[i_index]*startup.SCALE_HEIGHT;

        p_coll.activateSprite(SPRITE_OBJ_DUCKLEFTRIGHT,i_spritestate,i_spriteOffset);
        p_path.initPath(i_path,i_x,i_y,0x100,0x100,ap_SpriteCollection,COLLECTION_FIRSTPLANEOBJECTS,i_spriteOffset,ash_Paths,i_path,0,0,0);

        return true;
    }

    /**
     * Проверка напопадание игрока в убиваемые объекты
     */
    private static final void checkPlayerKilling()
    {
        // Проверка на летящих уток переднего плана
        SpriteCollection p_sprCol = ap_SpriteCollection[COLLECTION_FIRSTPLANEOBJECTS];
        SpriteCollection p_sight = ap_SpriteCollection[COLLECTION_PLAYERSIGHT];

        boolean lg_killed = false;

        int i_spr = -1;
        int [] ai_sprdata = p_sprCol.ai_spriteDataArray;
        while(true)
        {
            i_spr = p_sprCol.findCollidedSprite(0,p_sight,i_spr);
            if (i_spr<0) break;

            // проверяем не погибшая ли уже это утка
            int i_sprState = p_sprCol.getSpriteState(i_spr);
            if (i_sprState == SPRITE_STATE_DUCKLEFTRIGHT_KILLED) continue;

            lg_killed =true;

            // переводим объект в разряд убитой утки
            p_sprCol.activateSprite(SPRITE_OBJ_DUCKLEFTRIGHT,SPRITE_STATE_DUCKLEFTRIGHT_KILLED,i_spr);

            // переключаем путь утки на падение
            PathController p_path = getActivePathForSprite(COLLECTION_FIRSTPLANEOBJECTS,i_spr);

            int i_x = ai_sprdata[i_spr+SpriteCollection.SPRITEDATAOFFSET_MAINX];
            int i_y = ai_sprdata[i_spr+SpriteCollection.SPRITEDATAOFFSET_MAINY];

            p_path.initPath(PATH_DUCKDOWN,i_x,i_y,0x100,0x100,ap_SpriteCollection,COLLECTION_FIRSTPLANEOBJECTS,i_spr,ash_Paths,PATH_DUCKDOWN,0,0,0);

            i_PlayerScore+=SCORE_HORZDUCK;
            generateScore(i_x,i_y,SPRITE_STATE_SCORE_ONE);
        }

        if (lg_killed) return;

        // проверка на тростник
        p_sprCol = ap_SpriteCollection[COLLECTION_CANE];
        boolean lg_cane = p_sprCol.findCollidedSprite(0,p_sight,-1)>=0;

        // проверка на задний план
        p_sprCol = ap_SpriteCollection[COLLECTION_BACKGROUNDOBJECTS];
        i_spr = -1;
        ai_sprdata = p_sprCol.ai_spriteDataArray;
        while(true)
        {
            i_spr = p_sprCol.findCollidedSprite(0,p_sight,i_spr);

            if (i_spr<0) break;

            // проверяем не танк ли это
            int i_speType = p_sprCol.getSpriteType(i_spr);
            if (i_spr == SPRITE_OBJ_TANK) continue;

            int i_sprState = p_sprCol.getSpriteState(i_spr);

            switch(i_speType)
            {
                case SPRITE_OBJ_BOAR :
                {
                    if (lg_cane || i_sprState == SPRITE_STATE_BOAR_KILLED) continue;

                    // Убиваем кабана
                    PathController p_path = getActivePathForSprite(COLLECTION_BACKGROUNDOBJECTS,i_spr);

                    p_sprCol.activateSprite(SPRITE_OBJ_BOAR,SPRITE_STATE_BOAR_KILLED,i_spr);

                    int i_x = ai_sprdata[i_spr+SpriteCollection.SPRITEDATAOFFSET_MAINX];
                    int i_y = ai_sprdata[i_spr+SpriteCollection.SPRITEDATAOFFSET_MAINY];

                    p_path.initPath(PATH_BOARKILLEDPATH,i_x,i_y,0x100,0x100,ap_SpriteCollection,COLLECTION_BACKGROUNDOBJECTS,i_spr,ash_Paths,PATH_BOARKILLEDPATH,0,0,0);

                    lg_killed = true;

                    i_PlayerScore+=SCORE_BOAR;
                    generateScore(i_x,i_y,SPRITE_STATE_SCORE_THREE);
                    return;
                }
                case SPRITE_OBJ_FIGHTERLEFT:
                case SPRITE_OBJ_FIGHTERRIGHT:
                case SPRITE_OBJ_TANK :
                {
                    continue;
                }
                case SPRITE_OBJ_DUCKLONGRIGHT :
                case SPRITE_OBJ_DUCKLONGLEFT :
                {
                    if (i_sprState<SPRITE_STATE_DUCKLONGLEFT_STATE3) continue;

                    // переключаем на падение утки
                    PathController p_path = getActivePathForSprite(COLLECTION_BACKGROUNDOBJECTS,i_spr);

                    p_sprCol.activateSprite(i_speType,SPRITE_STATE_DUCKLONGLEFT_KILLED,i_spr);

                    int i_x = ai_sprdata[i_spr+SpriteCollection.SPRITEDATAOFFSET_MAINX];
                    int i_y = ai_sprdata[i_spr+SpriteCollection.SPRITEDATAOFFSET_MAINY];

                    p_path.initPath(PATH_DUCKDOWN,i_x,i_y,0x100,0x100,ap_SpriteCollection,COLLECTION_BACKGROUNDOBJECTS,i_spr,ash_Paths,PATH_DUCKDOWN,0,0,0);
                    generateScore(i_x,i_y,SPRITE_STATE_SCORE_TWO);


                    i_PlayerScore += SCORE_LONGDUCK;
                    lg_FigherPresented = false;

                    return;
                }
            }

            lg_killed =true;
        }
    }

    private static final boolean generateShell(int _x,int _y,boolean _tank)
    {
        SpriteCollection p_sprCol = ap_SpriteCollection[COLLECTION_ENEMYFIRING];
        int i_sprite = p_sprCol.i_lastInactiveSpriteOffset;
        if (i_sprite<0) return false;
        PathController p_path = getInactivePathController();
        if (p_path==null) return false;

        SpriteCollection p_player = ap_SpriteCollection[COLLECTION_PLAYER];
        int [] ai_arr = p_player.ai_spriteDataArray;
        int i_playerX = ai_arr[SpriteCollection.SPRITEDATAOFFSET_MAINX];
        int i_playerY = ai_arr[SpriteCollection.SPRITEDATAOFFSET_MAINY];

        if (_tank)
        {
            // Снаряд танка
            p_sprCol.activateSprite(SPRITE_OBJ_TANKSHELL,SPRITE_STATE_TANKSHELL_FLYING,i_sprite);

            int i_pathModify = PathController.MODIFY_NONE;
            if (i_playerX>_x) i_pathModify = PathController.MODIFY_FLIPHORZ;
            int i_w = (Math.abs(i_playerX-_x)+0x7F)>>8;
            long l_coeff = PathController.calculateLocalPathCoeff(i_w,30,PATH_TANKBOMB,ash_Paths);
            i_w = (int)(l_coeff>>32);

            p_path.initPath(PATH_TANKBOMB,_x,_y,i_w,0x100,ap_SpriteCollection,COLLECTION_ENEMYFIRING,i_sprite,ash_Paths,PATH_TANKBOMB,0,0,i_pathModify);
        }
        else
        {
            //Всплески самолета
            p_sprCol.activateSprite(SPRITE_OBJ_FIGHTERSPLASH,SPRITE_STATE_FIGHTERSPLASH_SPLASHED,i_sprite);
            int i_pathModify = PathController.MODIFY_NONE;
            int i_w = i_playerX;
            int i_x = 0;
            if (_x>i_playerX)
            {
                i_pathModify = PathController.MODIFY_FLIPHORZ;
                i_x = 176*startup.SCALE_WIDTH;
                i_w = i_x-i_w;
            }
            i_w = (i_w+0x7F)>>8;
            long l_coeff = PathController.calculateLocalPathCoeff(i_w,30,PATH_FIGHTERFIRING,ash_Paths);
            i_w = (int)(l_coeff>>32);
            p_sprCol.setOptionalData(i_sprite,SPLASHPOINTSTOACTIVE);
            p_path.initPath(PATH_FIGHTERFIRING,i_x,SPLASH_Y,i_w,0x100,ap_SpriteCollection,COLLECTION_ENEMYFIRING,i_sprite,ash_Paths,PATH_FIGHTERFIRING,0,0,i_pathModify | PathController.MODIFY_MOVEPOINTS);
        }

        return true;
    }

    private static final void generateScore(int _x,int _y,int _state)
    {
        startup.processGameAction(GAMEACTION_HIT);

        final int i_scoreOffset = SpriteCollection.getOffsetForSpriteWithIndex(1);
        SpriteCollection p_sight = ap_SpriteCollection[COLLECTION_PLAYERSIGHT];

        // генерируем картинку очков
        PathController p_pathcontr = getActivePathForSprite(COLLECTION_PLAYERSIGHT,i_scoreOffset);
        if (p_pathcontr==null)
        {
            p_pathcontr = getInactivePathController();
        }
        if (p_pathcontr!=null)
        {
            p_sight.activateSprite(SPRITE_OBJ_SCORE,_state,i_scoreOffset);
            p_pathcontr.initPath(PATH_SCOREPATH,_x,_y,0x100,0x100,ap_SpriteCollection,COLLECTION_PLAYERSIGHT,i_scoreOffset,ash_Paths,PATH_SCOREPATH,0,0,0);
        }
    }

    private static final PathController getActivePathForSprite(int _collection,int _spriteoffset)
    {
        for(int li=0;li<MAXPATHS;li++)
        {
            PathController p_contr = ap_Paths[li];
            if (p_contr.lg_completed) continue;
            if (p_contr.i_spriteCollectionID == _collection && p_contr.i_spriteOffset == _spriteoffset) return p_contr;
        }
        return null;
    }

    /**
     * Инициализация игрока для новой попытки
     */
    private static final void initNewPlayerAttemption()
    {
        releaseAllPaths();

        // Активизируем выход игрока
        i_SubState = SUBSTATE_PLAYERIN;
        PathController p_path = getInactivePathController();

        SpriteCollection p_player = ap_SpriteCollection[COLLECTION_PLAYER];
        p_player.activateSprite(SPRITE_OBJ_HUNTER,SPRITE_STATE_HUNTER_MOVERIGHT,0);

        p_path.initPath(PATH_HUNTERIN,0,0,0x100,0x100,ap_SpriteCollection,COLLECTION_PLAYER,0,ash_Paths,PATH_HUNTERIN,0,0,0);

        // Выставляем смещения спрайтов
        ap_SpriteCollection[COLLECTION_PLAYERSIGHT].releaseAllSprites();

        i_OffsetSprite_Shot = SpriteCollection.getOffsetForSpriteWithIndex(2);
        i_OffsetSprite_Score = SpriteCollection.getOffsetForSpriteWithIndex(1);
    }

    /**
     * Возвращает первый неактивный контроллер пути
     * @return контроллер пути или null если все заняты
     */
    private static final PathController getInactivePathController()
    {
        for(int li=0;li<ap_Paths.length;li++)
        {
            if (ap_Paths[li].lg_completed) return ap_Paths[li];
        }
        return null;
    }

    private static final short [] loadSpriteArray(Class _class,String _resource)
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

    public static final void notifyPathPointPassed(PathController _controller)
    {
        SpriteCollection p_sprcol = ap_SpriteCollection[_controller.i_spriteCollectionID];
        int i_spriteOffset = _controller.i_spriteOffset;

        int i_sprType = p_sprcol.getSpriteType(i_spriteOffset);
        int i_sprState = p_sprcol.getSpriteState(i_spriteOffset);

        switch(i_sprType)
        {
            case SPRITE_OBJ_FIGHTERSPLASH :
            {
               p_sprcol.setOptionalData(i_spriteOffset,p_sprcol.getOptionalData(i_spriteOffset)-1);
            };break;
            case SPRITE_OBJ_FIGHTERLEFT :
            case SPRITE_OBJ_FIGHTERRIGHT :
            {
                int i_newState = i_sprState;
                switch(i_sprState)
                {
                    case SPRITE_STATE_FIGHTERLEFT_STATE1 :
                    {
                        i_newState = SPRITE_STATE_FIGHTERLEFT_STATE2;
                    };break;
                    case SPRITE_STATE_FIGHTERLEFT_STATE2 :
                    {
                        if (getRandomInt(i_FreqFighterFiring)==(i_FreqFighterFiring>>1))
                        {
                            i_newState = SPRITE_STATE_FIGHTERLEFT_FIRING;
                            int i_x = p_sprcol.ai_spriteDataArray[i_spriteOffset+SpriteCollection.SPRITEDATAOFFSET_MAINX];
                            int i_y = p_sprcol.ai_spriteDataArray[i_spriteOffset+SpriteCollection.SPRITEDATAOFFSET_MAINY];
                            if (generateShell(i_x,i_y,false)) startup.processGameAction(GAMEACTION_FIGHTERFIRE);
                        }
                        else
                            i_newState = SPRITE_STATE_FIGHTERLEFT_STATE3;
                    };break;
                    case SPRITE_STATE_FIGHTERLEFT_FIRING :
                    case SPRITE_STATE_FIGHTERLEFT_STATE3 :
                    {
                        i_newState = SPRITE_STATE_FIGHTERLEFT_STATE4;

                    };break;
                }

                if (i_newState!=i_sprState)
                {
                    p_sprcol.activateSprite(i_sprType,i_newState,i_spriteOffset);
                }
            };break;
            case SPRITE_OBJ_DUCKLONGLEFT :
            case SPRITE_OBJ_DUCKLONGRIGHT :
            {
                int i_newState = i_sprState;
                switch(i_sprState)
                {
                    case SPRITE_STATE_FIGHTERLEFT_STATE1 :
                    {
                        i_newState = SPRITE_STATE_FIGHTERLEFT_STATE2;
                    };break;
                    case SPRITE_STATE_FIGHTERLEFT_STATE2 :
                    {
                        i_newState = SPRITE_STATE_FIGHTERLEFT_STATE3;
                    };break;
                    case SPRITE_STATE_FIGHTERLEFT_STATE3 :
                    {
                        i_newState = SPRITE_STATE_FIGHTERLEFT_STATE4;

                    };break;
                }

                if (i_newState!=i_sprState)
                {
                    p_sprcol.activateSprite(i_sprType,i_newState,i_spriteOffset);
                }
            };break;
        }
    }

    //todo PATH COMPLETED
    public static final void notifyPathCompleted(PathController _controller)
    {
        switch(i_SubState)
        {
            case SUBSTATE_PLAYERIN :
            {
                // Охотник вышел, переключаем в режим охоты
                SpriteCollection p_player = ap_SpriteCollection[COLLECTION_PLAYER];
                int i_x = p_player.ai_spriteDataArray[SpriteCollection.SPRITEDATAOFFSET_MAINX];

                // переводим игрока в стойку
                p_player.activateSprite(SPRITE_OBJ_HUNTER,SPRITE_STATE_HUNTER_STAND,0);

                // выставляем и включаем прицел
                SpriteCollection p_sight = ap_SpriteCollection[COLLECTION_PLAYERSIGHT];
                p_sight.activateSprite(SPRITE_OBJ_SIGHT,SPRITE_STATE_SIGHT_STAND,0);

                p_sight.setMainPointXY(0,i_x,(RIGHTSIGHTAREA_Y-LEFTSIGHTAREA_Y)/2);

                i_SubState = SUBSTATE_PLAYERHUNTING;
            };break;
            case SUBSTATE_PLAYERKILLED :
            case SUBSTATE_PLAYERHUNTING :
            {
                SpriteCollection p_sprc = ap_SpriteCollection[_controller.i_spriteCollectionID];
                int i_parhID =_controller.i_PathControllerID;

                if (i_parhID==PATH_HUNTERDEATH)
                {
                    ap_SpriteCollection[COLLECTION_PLAYER].releaseAllSprites();
                }
                else
                if ((i_parhID == PATH_BOARKILLEDPATH) || (i_parhID  == PATH_BOARTANKDOWNPATH))
                {
                    // для танка и кабана освобождаем ячейку занятости
                    int i_optionalData = p_sprc.getOptionalData(_controller.i_spriteOffset);
                    p_sprc.releaseSprite(_controller.i_spriteOffset);
                    ab_BoarTankCells[i_optionalData] = 0;
                }
                else
                if (i_parhID  == PATH_BOARTANKUPPATH)
                {
                    int i_spr = _controller.i_spriteOffset;
                    int i_type = p_sprc.getSpriteType(i_spr);
                    int i_state = p_sprc.getSpriteState(i_spr);

                    int i_mx = p_sprc.ai_spriteDataArray[i_spr+SpriteCollection.SPRITEDATAOFFSET_MAINX];
                    int i_my = p_sprc.ai_spriteDataArray[i_spr+SpriteCollection.SPRITEDATAOFFSET_MAINY];

                    if (i_type == SPRITE_OBJ_TANK)
                    {
                        if (getRandomInt(i_FreqTankFiring)==(i_FreqTankFiring>>1))
                        {
                            if (generateShell(i_mx,i_my,true))
                            {
                                startup.processGameAction(GAMEACTION_TANKFIRE);
                                // спрайт переводим в режим выстрел
                                p_sprc.activateSprite(SPRITE_OBJ_TANK,SPRITE_STATE_TANK_SHOOTING,i_spr);
                                p_sprc.setSpriteNextTypeState(i_spr,SpriteCollection.SPRITEBEHAVIOUR_NOTIFY);
                                return;
                            }
                        }
                    }

                    _controller.initPath(PATH_BOARTANKDOWNPATH,i_mx,i_my,0x100,0x100,ap_SpriteCollection,_controller.i_spriteCollectionID,_controller.i_spriteOffset,ash_Paths,PATH_BOARTANKDOWNPATH,0,0,0);
                }
                else
                {
                    if (i_parhID == PATH_FIGHTERLEFT || i_parhID == PATH_FIGHTERRIGHT)
                    {
                        lg_FigherPresented = false;
                    }
                    p_sprc.releaseSprite(_controller.i_spriteOffset);
                }
            };break;
        }
    }

//todo Массивы

protected static short [] ash_SpritesTable;

//------------------Sprite constants-----------------
public static final int SPRITE_OBJ_SIGHT = 0;
public static final int SPRITE_STATE_SIGHT_STAND = 0;
public static final int SPRITE_OBJ_SHOOT = 1;
public static final int SPRITE_STATE_SHOOT_STAND = 0;
public static final int SPRITE_OBJ_HUNTER = 2;
public static final int SPRITE_STATE_HUNTER_STAND = 0;
public static final int SPRITE_STATE_HUNTER_MOVELEFT = 1;
public static final int SPRITE_STATE_HUNTER_MOVERIGHT = 2;
public static final int SPRITE_STATE_HUNTER_DEATH = 3;
public static final int SPRITE_STATE_HUNTER_SHOOTING = 4;
public static final int SPRITE_OBJ_SCORE = 3;
public static final int SPRITE_STATE_SCORE_ONE = 0;
public static final int SPRITE_STATE_SCORE_TWO = 1;
public static final int SPRITE_STATE_SCORE_THREE = 2;
public static final int SPRITE_OBJ_CANE = 4;
public static final int SPRITE_STATE_CANE_STAND = 0;
public static final int SPRITE_OBJ_TANK = 5;
public static final int SPRITE_STATE_TANK_STAND = 0;
public static final int SPRITE_STATE_TANK_SHOOTING = 1;
public static final int SPRITE_OBJ_BOAR = 6;
public static final int SPRITE_STATE_BOAR_LIVING = 0;
public static final int SPRITE_STATE_BOAR_KILLED = 1;
public static final int SPRITE_OBJ_TANKSHELL = 7;
public static final int SPRITE_STATE_TANKSHELL_FLYING = 0;
public static final int SPRITE_OBJ_FIGHTERSPLASH = 8;
public static final int SPRITE_STATE_FIGHTERSPLASH_SPLASHED = 0;
public static final int SPRITE_OBJ_DUCKLONGLEFT = 9;
public static final int SPRITE_STATE_DUCKLONGLEFT_STATE1 = 0;
public static final int SPRITE_STATE_DUCKLONGLEFT_STATE2 = 1;
public static final int SPRITE_STATE_DUCKLONGLEFT_STATE3 = 2;
public static final int SPRITE_STATE_DUCKLONGLEFT_STATE4 = 3;
public static final int SPRITE_STATE_DUCKLONGLEFT_KILLED = 4;
public static final int SPRITE_OBJ_DUCKLONGRIGHT = 10;
public static final int SPRITE_STATE_DUCKLONGRIGHT_STATE1 = 0;
public static final int SPRITE_STATE_DUCKLONGRIGHT_STATE2 = 1;
public static final int SPRITE_STATE_DUCKLONGRIGHT_STATE3 = 2;
public static final int SPRITE_STATE_DUCKLONGRIGHT_STATE4 = 3;
public static final int SPRITE_STATE_DUCKLONGRIGHT_KILLED = 4;
public static final int SPRITE_OBJ_FIGHTERLEFT = 11;
public static final int SPRITE_STATE_FIGHTERLEFT_STATE1 = 0;
public static final int SPRITE_STATE_FIGHTERLEFT_STATE2 = 1;
public static final int SPRITE_STATE_FIGHTERLEFT_STATE3 = 2;
public static final int SPRITE_STATE_FIGHTERLEFT_STATE4 = 3;
public static final int SPRITE_STATE_FIGHTERLEFT_FIRING = 4;
public static final int SPRITE_OBJ_FIGHTERRIGHT = 12;
public static final int SPRITE_STATE_FIGHTERRIGHT_STATE1 = 0;
public static final int SPRITE_STATE_FIGHTERRIGHT_STATE2 = 1;
public static final int SPRITE_STATE_FIGHTERRIGHT_STATE3 = 2;
public static final int SPRITE_STATE_FIGHTERRIGHT_STATE4 = 3;
public static final int SPRITE_STATE_FIGHTERRIGHT_FIRING = 4;
public static final int SPRITE_OBJ_DUCKLEFTRIGHT = 13;
public static final int SPRITE_STATE_DUCKLEFTRIGHT_LEFT = 0;
public static final int SPRITE_STATE_DUCKLEFTRIGHT_RIGHT = 1;
public static final int SPRITE_STATE_DUCKLEFTRIGHT_KILLED = 2;

    // The array contains values for path controllers
    public static final short [] ash_Paths= new short[] {
         // PATH_HUNTERIN
         (short)1,(short)32,(short)-20,(short)188,(short)18,(short)91,(short)188,(short)10,
         // PATH_BOARTANKGENERATEDPATH
         (short)5,(short)0,(short)16,(short)114,(short)45,(short)114,(short)72,(short)114,(short)100,(short)114,(short)130,(short)114,(short)156,(short)114,
         // PATH_BOARTANKUPPATH
         (short)1,(short)32,(short)0,(short)0,(short)12,(short)0,(short)-32,(short)10,
         // PATH_BOARTANKDOWNPATH
         (short)1,(short)32,(short)0,(short)0,(short)10,(short)0,(short)32,(short)10,
         // PATH_BOARKILLEDPATH
         (short)3,(short)32,(short)0,(short)1,(short)3,(short)-8,(short)-2,(short)3,(short)-16,(short)5,(short)3,(short)-26,(short)26,(short)3,
         // PATH_HORZDUCKGENERATIONPATH
         (short)3,(short)0,(short)1,(short)75,(short)1,(short)66,(short)1,(short)57,(short)1,(short)47,
         // PATH_DUCKTOLEFT
         (short)5,(short)32,(short)1,(short)2,(short)5,(short)-44,(short)-3,(short)5,(short)-84,(short)2,(short)5,(short)-130,(short)-3,(short)5,(short)-174,(short)6,(short)5,(short)-217,(short)0,(short)5,
         // PATH_DUCKTORIGHT
         (short)5,(short)32,(short)-3,(short)1,(short)5,(short)52,(short)-3,(short)5,(short)97,(short)1,(short)5,(short)139,(short)-5,(short)5,(short)175,(short)3,(short)5,(short)219,(short)-1,(short)5,
         // PATH_DUCKDOWN
         (short)2,(short)32,(short)0,(short)0,(short)2,(short)0,(short)25,(short)2,(short)0,(short)55,(short)2,
         // PATH_SCOREPATH
         (short)4,(short)32,(short)1,(short)1,(short)3,(short)15,(short)-9,(short)3,(short)-10,(short)-17,(short)3,(short)15,(short)-27,(short)3,(short)-7,(short)-35,(short)3,
         // PATH_FIGHTERRIGHT
         (short)4,(short)48,(short)105,(short)-7,(short)16,(short)107,(short)13,(short)10,(short)110,(short)29,(short)8,(short)115,(short)44,(short)6,(short)202,(short)30,(short)10,
         // PATH_FIGHTERLEFT
         (short)4,(short)48,(short)69,(short)-7,(short)16,(short)69,(short)13,(short)10,(short)66,(short)29,(short)8,(short)62,(short)44,(short)6,(short)-30,(short)36,(short)10,
         // PATH_TANKBOMB
         (short)94,(short)151,(short)6,(short)48,(short)-8,(short)-6,(short)4,(short)-18,(short)-24,(short)4,(short)-40,(short)-34,(short)4,(short)-64,(short)-26,(short)4,(short)-82,(short)6,(short)4,(short)-90,(short)58,(short)4,(short)-94,(short)117,(short)4,
         // PATH_HUNTERDEATH
         (short)1,(short)32,(short)0,(short)0,(short)20,(short)0,(short)30,(short)10,
         // PATH_FIGHTERFIRING
         (short)92,(short)60,(short)7,(short)48,(short)0,(short)1,(short)3,(short)15,(short)10,(short)3,(short)28,(short)19,(short)3,(short)42,(short)28,(short)3,(short)56,(short)37,(short)3,(short)68,(short)45,(short)3,(short)80,(short)52,(short)3,(short)92,(short)60,(short)3,
    };

    // PATH offsets
    private static final int PATH_HUNTERIN = 0;
    private static final int PATH_BOARTANKGENERATEDPATH = 8;
    private static final int PATH_BOARTANKUPPATH = 22;
    private static final int PATH_BOARTANKDOWNPATH = 30;
    private static final int PATH_BOARKILLEDPATH = 38;
    private static final int PATH_HORZDUCKGENERATIONPATH = 52;
    private static final int PATH_DUCKTOLEFT = 62;
    private static final int PATH_DUCKTORIGHT = 82;
    private static final int PATH_DUCKDOWN = 102;
    private static final int PATH_SCOREPATH = 113;
    private static final int PATH_FIGHTERRIGHT = 130;
    private static final int PATH_FIGHTERLEFT = 147;
    private static final int PATH_TANKBOMB = 166;
    private static final int PATH_HUNTERDEATH = 189;
    private static final int PATH_FIGHTERFIRING = 199;
}

