package mtv.pillow;

import java.util.Random;
import java.io.*;

/**
 * Класс описывает минимальную игровую модель.
 *
 * @author Игорь Мазница
 * @version 4.1
 *          (С) 2003-2005 Raydac Reserach Group Ltd.
 */
public class Gamelet
{
    /**
     * Интерфейс определяет функции слушателя игровых сообщений
     */
    public interface GameActionListener
    {
        /**
         * Отработка игрового события, с одним числовым параметром и числовым возвращаемым значением
         */
        public int processGameAction(int _arg);
    }

    /**
     * Генератор случайных чисел
     */
    private static final Random p_RNDGenerator = new Random(System.currentTimeMillis());

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

    /**
     * Константа, показывающая что уровень игры "Простой"
     */
    public static final int GAMELEVEL_EASY = 0;

    /**
     * Константа, показывающая что уровень игры "Нормальный"
     */
    public static final int GAMELEVEL_NORMAL = 1;

    /**
     * Константа, показывающая что уровень игры "Сложный"
     */
    public static final int GAMELEVEL_HARD = 2;

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
     * Переменная содержит указатель на слушатель игровых событий
     */
    private static GameActionListener p_actionListener;

    /**
     * Переменная содержит количество пикселей по ширине игровой зоны
     */
    protected static int i_GameAreaWidth;

    /**
     * Переменная содержит количество пикселей по высоте игровой зоны
     */
    protected static int i_GameAreaHeight;

    /**
     * Переменная содержит состояние игрока
     */
    public static int i_PlayerState;

    /**
     * Переменная содержит задержку в миллисекундах между игровыми ходами.
     */
    public static int i_GameStepDelay;

    /**
     * Переменная содержит количество игровых попыток играющего
     */
    public static int i_PlayerAttemptions;

    /**
     * Функция отвечающая за инициализацию блока, до её вызова любые операции с блоком запрещены. Переводит блок в состояние INITED..
     *
     * @param _class параметр содержащий класс оболочки игры
     * @return true если инициализация прошла успешно, иначе false.
     */
    public static final boolean init(Class _class)
    {
        if (i_GameState != STATE_UNKNON) return false;
        p_actionListener = null;
        i_GameAreaHeight = -1;
        i_GameAreaWidth = -1;

        //------------Вставьте свой код здесь--------------------
        try
        {
            ash_Building = (short[]) loadArrayFromResource(_class, "/building.bin");
            ai_SpriteParameters = (int[]) loadArrayFromResource(_class, "/sprparams.bin");
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

        //--------------------------------------------------

        i_GameAreaHeight = -1;
        i_GameAreaWidth = -1;
        p_actionListener = null;

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
     * @param _actionListener слушатель игровых событий
     * @return true если инициализация игровой сессии прошла успешно, иначе false.
     */
    public static final boolean initNewGame(final int _gameAreaWidth, final int _gameAreaHeight, final int _gameLevel, final GameActionListener _actionListener)
    {
        if (i_GameState != STATE_INITED) return false;
        p_actionListener = _actionListener;
        i_GameAreaHeight = _gameAreaHeight;
        i_GameAreaWidth = _gameAreaWidth;
        i_GameLevel = _gameLevel;
        initPlayerForGame(true);
        //------------Вставьте свой код здесь--------------------
        int i_guardians = 0;
        i_StartStackPointer = -1;
        switch (_gameLevel)
        {
            case GAMELEVEL_EASY:
                i_guardians = LEVELEASY_GUARDIANNUMBER;
                break;
            case GAMELEVEL_NORMAL:
                i_guardians = LEVELNORMAL_GUARDIANNUMBER;
                break;
            case GAMELEVEL_HARD:
                i_guardians = LEVELHARD_GUARDIANNUMBER;
                break;
        }

        p_PlayerSprite = new Sprite(0);
        p_ScoreSprite = new Sprite(0);
        p_LiftSprite = new Sprite(0);

        i_GameStepDelay = STEP_TIMEDELAY;
        i_DelayToNextStar = STAR_GENERATION_DELAY;
        i_SuperStarNumber = 0;

        ap_Doors = new Sprite[MAX_DOORS];
        initDoorsFromArray();

        i_LastSuperStarType = -1;

        ai_StarStack = new int[MAX_SPRITES];
        i_guardians--;
        for (int li = 0; li < i_guardians; li++)
        {
            pushStarToStack(SPRITE_GUARDIAN_LEFT_MOVE);
        }
        pushStarToStack(SPRITE_SMALLSTAR_BOY_BLACK_JACKET_MOVELEFT);
        pushStarToStack(SPRITE_SMALLSTAR_BOY_WHITEHAIR_SUIT_MOVELEFT);
        pushStarToStack(SPRITE_SMALLSTAR_BOY_WHITEHAIR_VEST_MOVELEFT);
        pushStarToStack(SPRITE_SMALLSTAR_GIRL_CATSUIT_MOVELEFT);
        //pushStarToStack(SPRITE_SMALLSTAR_GIRL_SKIRT_MOVELEFT);

        ap_Sprites = new Sprite[MAX_SPRITES];
        for (int li = 0; li < MAX_SPRITES; li++)
        {
            ap_Sprites[li] = new Sprite(li);
        }

        initPlayer();

        // Выставляем одного охранника идущим навстречу игроку перед лифтом
        final int FIRSTGUARDIAN_CELLX = 56;
        final int FIRSTGUARDIAN_CELLY = 23;
        Sprite p_grdspr = getFirstInactiveSprite();
        activateSprite(p_grdspr, SPRITE_GUARDIAN_LEFT_MOVE);
        alignCoordinatesOfSpriteToCell(FIRSTGUARDIAN_CELLX, FIRSTGUARDIAN_CELLY, p_grdspr);
        p_grdspr.i_ObjectSpeed = GUARDIAN_NORMAL_SPEED;
        p_grdspr.i_ObjectState = HEALTH_GUARDIAN;
        p_grdspr.i_ObjectHitSteps = 0;

        activateSprite(p_LiftSprite, SPRITE_LIFT);
        p_LiftSprite.setMainPointXY(LIFT_STARTCELL_X * CELL_WIDTH << 8, (LIFT_STARTCELL_Y * CELL_HEIGHT << 8) + 0x900);

        i_PlayerScores = 0;
        i_PlayerShells = INIT_PLAYER_SHELLS;
        i_PlayerLifes = INIT_PLAYER_LIFES;

        i_SuperShells = 0;


        i_CurrentPlayMode = INSIDE_MODE_PLAYING;
        //--------------------------------------------------

        //p_PlayerSprite.lg_SpriteInvisible = true;

        setState(STATE_STARTED);
        return true;
    }

    private static final void alignCoordinatesOfSpriteToCell(int _cellX, int _cellY, Sprite _sprite)
    {
        int i_x = (_cellX * CELL_WIDTH) << 8;
        int i_y = (((_cellY + 1) * CELL_HEIGHT) << 8) - 0x100;
        _sprite.setMainPointXY(i_x, i_y);
    }

    private static final boolean isPlayerInLift()
    {
        int i_plX1 = p_PlayerSprite.i_ScreenX + p_PlayerSprite.i_col_offsetX;
        int i_lftX1 = p_LiftSprite.i_ScreenX + p_LiftSprite.i_col_offsetX + 0xF00;

        if (i_plX1 >= i_lftX1) return true;
        return false;
    }

    /**
     * Деинициализация гровой сессии
     */
    public static final void releaseGame()
    {
        if (i_GameState == STATE_INITED || i_GameState == STATE_UNKNON) return;
        p_actionListener = null;
        i_GameAreaHeight = -1;
        i_GameAreaWidth = -1;

        //------------Вставьте свой код здесь--------------------
        p_LiftSprite = null;
        p_PlayerSprite = null;
        p_ScoreSprite = null;
        ap_Doors = null;
        ap_Sprites = null;

        Runtime.getRuntime().gc();
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
     * Продолжение игрового процесса после смерти игрока или после постановки на паузу
     */
    public static final void resumeGameAfterPauseOrPlayerLost()
    {
        if (i_GameState == STATE_STARTED)
        {
            initPlayerForGame(false);
            //------------Вставьте свой код здесь-----------------------------
            //------Код обрабатываемый при запуске после смерти игрока--------

            //----------------------------------------------------------------
        }
        else if (i_GameState == STATE_PAUSED)
        {
            setState(STATE_STARTED);
            //------------Вставьте свой код здесь--------------------
            //------Код обрабатываемый при снятии с паузы--------

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
        //------------Вставьте свой код здесь--------------------
        int i_score = i_PlayerScores * (i_GameLevel + 1);
        //--------------------------------------------------
        return i_score;
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
        setState(STATE_STARTED);
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
    public static final void loadGameStateFromByteArray(final byte[] _data, GameActionListener _actionListener) throws Exception
    {
        if (i_GameState != STATE_INITED) throw new Exception();
        ByteArrayInputStream p_arrayInputStream = new ByteArrayInputStream(_data);
        DataInputStream p_inputStream = new DataInputStream(p_arrayInputStream);
        int i_gameLevel = p_inputStream.readUnsignedByte();
        int i_gameStage = p_inputStream.readUnsignedByte();
        int i_gameScreenWidth = p_inputStream.readUnsignedShort();
        int i_gameScreenHeight = p_inputStream.readUnsignedShort();
        if (!initNewGame(i_gameScreenWidth, i_gameScreenHeight, i_gameLevel, _actionListener)) throw new Exception();
        if (!initGameStage(i_gameStage)) throw new Exception();
        i_PlayerAttemptions = p_inputStream.readInt();
        //------------Вставьте свой код здесь--------------------

        i_CurrentPlayMode = p_inputStream.readUnsignedByte();
        i_PlayerLifes = p_inputStream.readShort();
        i_PlayerShells = p_inputStream.readShort();
        i_SuperShells = p_inputStream.readByte();
        i_PlayerScores = p_inputStream.readInt();
        i_StartStackPointer = p_inputStream.readInt();
        i_SuperStarNumber = p_inputStream.readByte();
        i_DestLocationIndex = p_inputStream.readByte();
        i_RoomShowCounter = p_inputStream.readInt();
        i_RoomDoCounter = p_inputStream.readInt();
        i_ActiveRoomType =  p_inputStream.readByte();
        i_DelayToNextStar = p_inputStream.readShort();
        i_PrevDeadRoomXOffset = p_inputStream.readInt();
        i_PrevDeadRoomYOffset= p_inputStream.readInt();
        i_LastSuperStarType = p_inputStream.readInt();

        for(int li=0;li<MAX_SPRITES;li++)
        {
            ai_StarStack[li] = p_inputStream.readInt();
        }

        int i_type = p_inputStream.readUnsignedByte();
        activateSprite(p_PlayerSprite,i_type);
        p_PlayerSprite.readSpriteFromStream(p_inputStream);

        for(int li=0;li<MAX_SPRITES;li++)
        {
            i_type = p_inputStream.readUnsignedByte();
            Sprite p_spr = ap_Sprites[li];
            activateSprite(p_spr,i_type);
            p_spr.readSpriteFromStream(p_inputStream);
        }

        p_LiftSprite.readSpriteFromStream(p_inputStream);

        p_ScoreSprite.lg_SpriteActive = false;
        alignScreenCoordsToPlayer();
        //--------------------------------------------------
        p_inputStream.close();
        p_arrayInputStream = null;
        p_inputStream = null;
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
        if ((i_GameState != STATE_STARTED || i_GameState != STATE_PAUSED) && i_PlayerState != PLAYER_PLAYING) throw new Exception();
        Runtime.getRuntime().gc();
        ByteArrayOutputStream p_arrayOutputStream = new ByteArrayOutputStream(getGameStateDataBlockSize());
        DataOutputStream p_outputStream = new DataOutputStream(p_arrayOutputStream);
        p_outputStream.writeByte(i_GameLevel);
        p_outputStream.writeByte(i_GameStage);
        p_outputStream.writeShort(i_GameAreaWidth);
        p_outputStream.writeShort(i_GameAreaHeight);
        p_outputStream.writeInt(i_PlayerAttemptions);
        //------------Вставьте свой код здесь--------------------

        p_outputStream.writeByte(i_CurrentPlayMode);
        p_outputStream.writeShort(i_PlayerLifes);
        p_outputStream.writeShort(i_PlayerShells);
        p_outputStream.writeByte(i_SuperShells);
        p_outputStream.writeInt(i_PlayerScores);
        p_outputStream.writeInt(i_StartStackPointer);
        p_outputStream.writeByte(i_SuperStarNumber);
        p_outputStream.writeByte(i_DestLocationIndex);
        p_outputStream.writeInt(i_RoomShowCounter);
        p_outputStream.writeInt(i_RoomDoCounter);
        p_outputStream.writeByte(i_ActiveRoomType);
        p_outputStream.writeShort(i_DelayToNextStar);
        p_outputStream.writeInt(i_PrevDeadRoomXOffset);
        p_outputStream.writeInt(i_PrevDeadRoomYOffset);
        p_outputStream.writeInt(i_LastSuperStarType);

        for(int li=0;li<MAX_SPRITES;li++) p_outputStream.writeInt(ai_StarStack[li]);

        p_outputStream.writeByte(p_PlayerSprite.i_ObjectType);
        p_PlayerSprite.writeSpriteToStream(p_outputStream);

        for(int li=0;li<MAX_SPRITES;li++)
        {
            p_outputStream.writeByte(ap_Sprites[li].i_ObjectType);
            ap_Sprites[li].writeSpriteToStream(p_outputStream);
        }

        p_LiftSprite.writeSpriteToStream(p_outputStream);

        //--------------------------------------------------
        p_outputStream.close();
        p_outputStream = null;
        byte[] ab_result = p_arrayOutputStream.toByteArray();
        p_arrayOutputStream = null;
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
        int MINIMUM_SIZE = 10;

        //p_outputStream.writeByte(i_CurrentPlayMode);
        //p_outputStream.writeShort(i_PlayerLifes);
        //p_outputStream.writeShort(i_PlayerShells);
        //p_outputStream.writeByte(i_SuperShells);
        //p_outputStream.writeInt(i_PlayerScores);
        //p_outputStream.writeInt(i_StartStackPointer);
        //p_outputStream.writeByte(i_SuperStarNumber);
        //p_outputStream.writeByte(i_DestLocationIndex);
        //p_outputStream.writeInt(i_RoomShowCounter);
        //p_outputStream.writeInt(i_RoomDoCounter);
        //p_outputStream.writeByte(i_ActiveRoomType);
        //p_outputStream.writeShort(i_DelayToNextStar);
        //p_outputStream.writeInt(i_PrevDeadRoomXOffset);
        //p_outputStream.writeInt(i_PrevDeadRoomYOffset);

        MINIMUM_SIZE += 39;

        //for(int li=0;li<MAX_SPRITES;li++) p_outputStream.writeInt(ai_StarStack[li]);
        MINIMUM_SIZE += (MAX_SPRITES*4);

        //p_outputStream.writeByte(p_PlayerSprite.i_ObjectType);
        //p_PlayerSprite.writeSpriteToStream(p_outputStream);
        MINIMUM_SIZE += (Sprite.DATASIZE_BYTES+1);

        /*
        for(int li=0;li<MAX_SPRITES;li++)
        {
            p_outputStream.writeByte(ap_Sprites[li].i_ObjectType);
            ap_Sprites[li].writeSpriteToStream(p_outputStream);
        }
        */
        MINIMUM_SIZE += ((Sprite.DATASIZE_BYTES+1)*MAX_SPRITES);

        //p_LiftSprite.writeSpriteToStream(p_outputStream);
        MINIMUM_SIZE += Sprite.DATASIZE_BYTES;

        return MINIMUM_SIZE;
    }

    /**
     * Возвращает текстовый идентификатор игры
     *
     * @return строка, идентифицирующая игру.
     */
    public static final String getID()
    {
        return "Pillow234";
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
        _processGameStep(_keyStateFlags);
        if (i_PlayerState != PLAYER_PLAYING)
        {
            setState(STATE_OVER);
        }
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

    //=======================================================================
    protected static final int KEY_NONE = 0;
    protected static final int KEY_LEFT = 1;
    protected static final int KEY_RIGHT = 2;
    protected static final int KEY_UP = 4;
    protected static final int KEY_DOWN = 8;
    protected static final int KEY_FIRE = 16;

    protected static final int CELL_WIDTH = 15;
    protected static final int CELL_HEIGHT = 15;

    protected static final int CELLSNUMBER_WIDTH = 104;
    protected static final int CELLSNUMBER_HEIGHT = 25;

    protected static final int CELLSNUMBER_ROOM_WIDTH = 10;
    protected static final int CELLSNUMBER_ROOM_HEIGHT = 8;

    /**
     * Игрок находится в игре
     */
    protected static final int INSIDE_MODE_PLAYING = 0;

    /**
     * Игрок находится в едущем лифте и не может действовать
     */
    protected static final int INSIDE_MODE_LIFT = 1;

    /**
     * Игрок находится в одной из комнат
     */
    protected static final int INSIDE_MODE_ROOM = 2;

    /**
     * Игрок находится в комнате смерти
     */
    protected static final int INSIDE_MODE_DEADROOM = 3;

    /**
     * Игрок перемещается между локациями
     */
    protected static final int INSIDE_MODE_LOCATIONMOVE = 4;

    /**
     * Приезд звезд
     */
    protected static final int INSIDE_MODE_STARINCOMING = 5;

    /**
     * Приезд звезд
     */
    protected static final int INSIDE_MODE_ANGRYSTARINCOMING = 6;

    /**
     * Смерть игрока от ударов
     */
    protected static final int INSIDE_MODE_PLAYERDEATH = 7;

    public static final int GAMEACTION_LIFT = 0;
    public static final int GAMEACTION_HIT = 1;
    public static final int GAMEACTION_MIMO = 2;
    public static final int GAMEACTION_CAR = 3;
    public static final int GAMEACTION_DOOR = 4;
    public static final int GAMEACTION_TAKEHEART = 5;
    public static final int GAMEACTION_DEADROOM = 6;

    protected static int i_CurrentPlayMode;
    protected static int i_PlayerLifes;
    protected static int i_PlayerShells;
    protected static int i_PlayerScores;
    protected static Sprite p_PlayerSprite;
    protected static Sprite[] ap_Sprites;
    private static int i_StartStackPointer;
    private static int[] ai_StarStack;
    protected static Sprite[] ap_Doors;
    protected static Sprite p_ScoreSprite;
    protected static Sprite p_LiftSprite;
    protected static int i_SuperStarNumber;
    private static int i_DestLocationIndex;
    protected static int i_RoomShowCounter;
    protected static int i_RoomDoCounter;
    protected static int i_ActiveRoomType;
    private static int i_DelayToNextStar;
    protected static int i_SuperShells;
    private static int i_PrevDeadRoomXOffset;
    private static int i_PrevDeadRoomYOffset;
    protected static int i_LastSuperStarType;

    protected static final int STEP_TIMEDELAY = 95;
    protected static final int MAX_SPRITES = 20;
    protected static final int MAX_DOORS = 13;
    private static final int TICKS_ROOM_DO = 40;

    //TODO перенести сюда
    private static final int HIT_STEPS = 3;
    private static final int HIT_SPEED = 0x900;

    private static final int SHOW_ROOM_DELAY = 50;

    private static final int FREQ_BIGSTAR_GENERATION = 2;

    private static final Sprite[] ap_starGenArray = new Sprite[7];

    private static final int SUPERSHELL_NUMBER = 10;

    private static final int FREQ_STAR_HIDDEN = 300;
    private static final int FREQ_HEART_GEN = 10;
    private static final int PILLOW_WIDTH = 0x2A00;
    private static final int PILLOW_ZONE_OFFSET = 0x500;

    private static final int GUARDIAN_DISTANCE_BEAT = 0x3000;
    private static final int GUARDIAN_NORMAL_SPEED = 0x200;
    private static final int GUARDIAN_FAST_SPEED = 0x400;

    protected static final int PLAYER_ROOMDOOR_X = 2 * CELL_WIDTH;
    protected static final int ROOM_HEIGHT_CELLS = 8;

    public static final int ROOMTYPE_EMPTYTABLE = 0;
    public static final int ROOMTYPE_STUDIO = 1;
    public static final int ROOMTYPE_SUPERPILLOW = 2;
    public static final int ROOMTYPE_BATHROOM = 3;
    public static final int ROOMTYPE_COMPUTERTABLE = 4;
    public static final int ROOMTYPE_PILLOWTABLE = 5;
    public static final int ROOMTYPE_GUARDIAN = 6;
    public static final int ROOMTYPE_ENTRANCE = 7;

    private static final int LEVELEASY_GUARDIANNUMBER = 2;
    private static final int LEVELNORMAL_GUARDIANNUMBER = 3;
    private static final int LEVELHARD_GUARDIANNUMBER = 4;

    protected static final int SPRITE_PLAYER_LEFT_STAND = 0;
    protected static final int SPRITE_PLAYER_RIGHT_STAND = 1;
    protected static final int SPRITE_PLAYER_LEFT_MOVE = 2;
    protected static final int SPRITE_PLAYER_RIGHT_MOVE = 3;
    protected static final int SPRITE_PLAYER_LEFT_BEAT = 4;
    protected static final int SPRITE_PLAYER_RIGHT_BEAT = 5;

    protected static final int SPRITE_BONUS_HEART = 6;
    protected static final int SPRITE_ICON_HEART = 7;
    protected static final int SPRITE_ICON_SCORE_5 = 8;
    protected static final int SPRITE_ICON_SCORE_10 = 9;
    protected static final int SPRITE_ICON_SCORE_20 = 10;
    protected static final int SPRITE_ICON_SCORE_50 = 11;
    protected static final int SPRITE_CLOUD = 12;

    protected static final int SPRITE_DOOR = 13;
    protected static final int SPRITE_LIFT = 14;

    // Охранник
    protected static final int FIRSTINDEX_GUARDIAN = 15;
    protected static final int SPRITE_GUARDIAN_LEFT_MOVE = 15;
    protected static final int SPRITE_GUARDIAN_RIGHT_MOVE = 16;
    protected static final int SPRITE_GUARDIAN_LEFT_BEAT = 17;
    protected static final int SPRITE_GUARDIAN_RIGHT_BEAT = 18;

    // Секьюрити
    protected static final int FIRSTINDEX_BODYGUARD = 19;
    protected static final int SPRITE_BODYGUARD_LEFT_MOVE = 19;
    protected static final int SPRITE_BODYGUARD_RIGHT_MOVE = 20;
    protected static final int SPRITE_BODYGUARD_LEFT_BEAT = 21;
    protected static final int SPRITE_BODYGUARD_RIGHT_BEAT = 22;

    // Маленькие звезды
    protected static final int FIRSTINDEX_SMALLSTARS = 23;
    protected static final int SPRITE_SMALLSTAR_BOY_WHITEHAIR_SUIT_MOVELEFT = 23;
    protected static final int SPRITE_SMALLSTAR_BOY_WHITEHAIR_SUIT_MOVERIGHT = 24;
    protected static final int SPRITE_SMALLSTAR_BOY_BLACK_JACKET_MOVELEFT = 25;
    protected static final int SPRITE_SMALLSTAR_BOY_BLACK_JACKET_MOVERIGHT = 26;
    protected static final int SPRITE_SMALLSTAR_GIRL_CATSUIT_MOVELEFT = 27;
    protected static final int SPRITE_SMALLSTAR_GIRL_CATSUIT_MOVERIGHT = 28;
    protected static final int SPRITE_SMALLSTAR_BOY_WHITEHAIR_VEST_MOVELEFT = 29;
    protected static final int SPRITE_SMALLSTAR_BOY_WHITEHAIR_VEST_MOVERIGHT = 30;
    //protected static final int SPRITE_SMALLSTAR_GIRL_SKIRT_MOVELEFT = 31;
    //protected static final int SPRITE_SMALLSTAR_GIRL_SKIRT_MOVERIGHT = 32;

    // Большие звезды
    protected static final int FIRSTINDEX_BIGSTARS = 33;
    protected static final int SPRITE_BIGSTAR_GIRL_RED_BIKINI_MOVELEFT = 33;
    protected static final int SPRITE_BIGSTAR_GIRL_RED_BIKINI_MOVERIGHT = 34;
    protected static final int SPRITE_BIGSTAR_GIRL_TATU_MOVELEFT = 35;
    protected static final int SPRITE_BIGSTAR_GIRL_TATU_MOVERIGHT = 36;
    protected static final int SPRITE_BIGSTAR_BOY_GROUP_MOVELEFT = 37;
    protected static final int SPRITE_BIGSTAR_BOY_GROUP_MOVERIGHT = 38;
    protected static final int SPRITE_BIGSTAR_BOY_GRAY_SHIRT_MOVELEFT = 39;
    protected static final int SPRITE_BIGSTAR_BOY_GRAY_SHIRT_MOVERIGHT = 40;

    // Крутые звезды
    protected static final int FIRSTINDEX_ANGRYSTARS = 41;
    protected static final int SPRITE_ANGRYSTAR_LEFT_MOVE = 41;
    protected static final int SPRITE_ANGRYSTAR_RIGHT_MOVE = 42;
    protected static final int SPRITE_ANGRYSTAR_LEFT_BEAT = 43;
    protected static final int SPRITE_ANGRYSTAR_RIGHT_BEAT = 44;

    protected static final int TILE_EMPTY = 0;
    protected static final int TILE_WALL = 151;
    protected static final int TILE_CABINET = 144;
    protected static final int TILE_STUDIO = 145;
    protected static final int TILE_DEATHROOM = 146;
    protected static final int TILE_TOLOC20 = 147;
    protected static final int TILE_TOLOC15 = 148;
    protected static final int TILE_TOLOC17 = 149;
    protected static final int TILE_TOLOC12 = 150;

    private static final int PLAYER_HORZ_SPEED = 0x500;
    private static final int ENEMY_STAR_SPEED_SLOW = 0x300;
    private static final int ENEMY_STAR_SPEED_FAST = 0x400;

    private static final int HEALTH_STAR = 3;
    private static final int HEALTH_BODYGUARD = 4;
    private static final int HEALTH_ANGRYSTAR = 4;
    private static final int HEALTH_SUPERSTAR = 3;
    private static final int HEALTH_GUARDIAN = 3;

    protected static int i8_ViewAreaX;
    protected static int i8_ViewAreaY;

    private static final int PLAYER_STARTCELL_X = 55;
    private static final int PLAYER_STARTCELL_Y = 23;

    private static final int LIFT_STARTCELL_X = 56;
    private static final int LIFT_STARTCELL_Y = 24;
    private static final int LIFT_SPEED = 0x300;
    private static final int LOCATIONMOVE_SPEED = 0x800;

    private static final int INIT_PLAYER_LIFES = 10;
    private static final int INIT_PLAYER_SHELLS = 50;
    private static final int SHELLS_IN_A_ROOM = 30;

    private static final int STAR_GENERATION_DELAY = 20;

    private static final void initPlayer()
    {
        i_PlayerLifes = INIT_PLAYER_LIFES;
        i_PlayerShells = INIT_PLAYER_SHELLS;
        i_PlayerScores = 0;

        activateSprite(p_PlayerSprite, SPRITE_PLAYER_RIGHT_STAND);
        p_PlayerSprite.setMainPointXY(((PLAYER_STARTCELL_X * CELL_WIDTH) << 8) - p_PlayerSprite.i_col_offsetX, ((PLAYER_STARTCELL_Y + 1) * CELL_HEIGHT - 1) << 8);

        alignScreenCoordsToPlayer();
    }

    private static final void initDoorsFromArray()
    {
        int i_index = 0;

        for (int li = 0; li < ash_Building.length; li++)
        {
            int i_obj = ash_Building[li];
            if (i_obj >= TILE_CABINET && i_obj != TILE_WALL)
            {
                Sprite p_spr = new Sprite(i_index);
                ap_Doors[i_index++] = p_spr;
                activateSprite(p_spr, SPRITE_DOOR);

                int i_ycell = li / CELLSNUMBER_WIDTH;
                int i_xcell = li % CELLSNUMBER_WIDTH;

                p_spr.setMainPointXY((i_xcell * CELL_WIDTH) << 8, (i_ycell * CELL_HEIGHT) << 8);

                p_spr.i_ObjectState = i_obj << 16;
            }
        }
    }

    private static final Sprite getDoor(Sprite _sprite)
    {
        for (int li = 0; li < MAX_DOORS; li++)
        {
            Sprite p_spr = ap_Doors[li];
            if (p_spr.isCollided(_sprite)) return p_spr;
        }
        return null;
    }

    private static final Sprite getDestinationForLocation(Sprite _source)
    {
        int i_dstIndex = -1;
        switch (_source.i_ObjectState >>> 16)
        {
            case TILE_TOLOC12:
                {
                    i_dstIndex = TILE_TOLOC17;
                }
        ;
                break;
            case TILE_TOLOC15:
                {
                    i_dstIndex = TILE_TOLOC20;
                }
        ;
                break;
            case TILE_TOLOC17:
                {
                    i_dstIndex = TILE_TOLOC12;
                }
        ;
                break;
            case TILE_TOLOC20:
                {
                    i_dstIndex = TILE_TOLOC15;
                }
        ;
                break;
        }

        if (i_dstIndex < 0) return null;
        for (int li = 0; li < ap_Doors.length; li++)
        {
            Sprite p_spr = ap_Doors[li];
            if ((p_spr.i_ObjectState >>> 16) == i_dstIndex) return p_spr;
        }
        return null;
    }

    private static final void _processGameStep(final int _keyStateFlags)
    {
        if (i_RoomShowCounter > 0)
        {
            i_RoomShowCounter--;
        }

        boolean lg_playerStrike = false;

        // Осуществляем перемещение игрока
        switch (i_CurrentPlayMode)
        {
            case INSIDE_MODE_DEADROOM:
                {
                    i_RoomDoCounter--;
                    if (i_RoomDoCounter <= 0)
                    {
                        i_PlayerLifes = 0;
                        i_PlayerState = PLAYER_WIN;
                    }

                    i8_ViewAreaX -= i_PrevDeadRoomXOffset;
                    i8_ViewAreaY -= i_PrevDeadRoomYOffset;

                    i_PrevDeadRoomXOffset = (getRandomInt(10) - 5) << 8;
                    i_PrevDeadRoomYOffset = (getRandomInt(10) - 5) << 8;

                    i8_ViewAreaX += i_PrevDeadRoomXOffset;
                    i8_ViewAreaY += i_PrevDeadRoomYOffset;

                    //alignScreenCoordsToPlayer();
                    processStars(false);
                }
        ;
                break;
            case INSIDE_MODE_LIFT:
                {
                    int i_y = p_LiftSprite.i_mainY - LIFT_SPEED;
                    p_LiftSprite.setMainPointXY(p_LiftSprite.i_mainX, i_y);

                    if (i_y < (16 * CELL_HEIGHT << 8))
                    {
                        p_LiftSprite.lg_SpriteActive = false;
                        p_PlayerSprite.lg_SpriteInvisible = false;
                        p_PlayerSprite.setMainPointXY(p_PlayerSprite.i_mainX, (16 * CELL_HEIGHT << 8) - 0x100);
                        i_CurrentPlayMode = INSIDE_MODE_PLAYING;
                    }
                    else
                    {
                        i_y = Math.max(p_PlayerSprite.i_mainY - LIFT_SPEED, (16 * CELL_HEIGHT << 8) - 0x100);
                        p_PlayerSprite.setMainPointXY(p_PlayerSprite.i_mainX, i_y);
                    }

                    alignScreenCoordsToPlayer();
                    processStars(false);
                }
        ;
                break;
            case INSIDE_MODE_LOCATIONMOVE:
                {
                    Sprite p_spr = ap_Doors[i_DestLocationIndex];
                    int i8_dy = p_spr.i_ScreenY + p_spr.i_height;
                    int i8_dx = p_spr.i_ScreenX + (p_spr.i_width >> 1);
                    int i8_py = p_PlayerSprite.i_mainY;
                    int i8_px = p_PlayerSprite.i_mainX;

                    if (i8_px < i8_dx)
                    {
                        i8_px += LOCATIONMOVE_SPEED;
                        if (i8_px > i8_dx)
                        {
                            i8_px = i8_dx;
                        }
                    }
                    else if (i8_px > i8_dx)
                    {
                        i8_px -= LOCATIONMOVE_SPEED;
                        if (i8_px < i8_dx)
                        {
                            i8_px = i8_dx;
                        }
                    }

                    if (i8_py > i8_dy)
                    {
                        i8_py -= LOCATIONMOVE_SPEED;
                        if (i8_py <= i8_dy)
                        {
                            i8_py = i8_dy - 0x100;
                            p_PlayerSprite.i_ObjectHitSteps = 0;
                            p_PlayerSprite.i_ObjectState = 0;
                            i_CurrentPlayMode = INSIDE_MODE_PLAYING;
                            p_PlayerSprite.lg_SpriteInvisible = false;
                        }
                    }
                    else
                    {
                        i8_py += LOCATIONMOVE_SPEED;
                        if (i8_py >= i8_dy)
                        {
                            i8_py = i8_dy - 0x100;
                            p_PlayerSprite.i_ObjectHitSteps = 0;
                            p_PlayerSprite.i_ObjectState = 0;
                            i_CurrentPlayMode = INSIDE_MODE_PLAYING;
                            p_PlayerSprite.lg_SpriteInvisible = false;
                        }
                    }
                    p_PlayerSprite.setMainPointXY(i8_px, i8_py);

                    alignScreenCoordsToPlayer();
                    processStars(false);
                }
        ;
                break;
            case INSIDE_MODE_PLAYERDEATH:
                {
                    i_RoomDoCounter--;

                    if (p_ScoreSprite.lg_SpriteActive)
                        if (p_ScoreSprite.processAnimation())
                        {
                            p_ScoreSprite.lg_SpriteActive = false;
                        }
                    if (i_RoomDoCounter <= 0)
                    {
                        i_PlayerState = PLAYER_WIN;
                    }

                    alignScreenCoordsToPlayer();
                    processStars(false);
                }
        ;
                break;
            case INSIDE_MODE_PLAYING:
                {
                    generateStar();

                    if (p_LiftSprite.lg_SpriteActive)
                    {
                        // Проверка пользователя в лифте
                        if (isPlayerInLift())
                        {
                            p_PlayerSprite.lg_SpriteInvisible = true;
                            p_PlayerSprite.setMainPointXY(p_LiftSprite.i_ScreenX + (p_LiftSprite.i_width >> 1), p_PlayerSprite.i_mainY);
                            activateSprite(p_PlayerSprite, SPRITE_PLAYER_RIGHT_MOVE);
                            i_CurrentPlayMode = INSIDE_MODE_LIFT;
                            p_PlayerSprite.i_ObjectHitSteps = 0;
                            p_actionListener.processGameAction(GAMEACTION_LIFT);
                        }
                    }
                    lg_playerStrike = processPlayer(_keyStateFlags);

                    alignScreenCoordsToPlayer();
                    processStars(lg_playerStrike);
                }
        ;
                break;
            case INSIDE_MODE_ROOM:
                {
                    i_RoomDoCounter--;
                    if (i_RoomDoCounter <= 0)
                    {
                        i_RoomDoCounter = TICKS_ROOM_DO;
                        if (i_ActiveRoomType == ROOMTYPE_GUARDIAN)
                        {
                            i_PlayerLifes--;
                            p_actionListener.processGameAction(GAMEACTION_HIT);
                        }
                    }
                    processPlayer(_keyStateFlags);
                    processStars(false);
                }
        ;
                break;
            case INSIDE_MODE_STARINCOMING:
                {
                    if (processStarEntering())
                    {
                        i_CurrentPlayMode = INSIDE_MODE_PLAYING;
                        // Переносим
                        for (int li = 0; li < MAX_SPRITES; li++)
                        {
                            Sprite p_spr = ap_Sprites[li];
                            if (!p_spr.lg_SpriteActive) continue;
                            if (p_spr.i_ObjectState < 0)
                            {
                                int i_type = p_spr.i_ObjectType;
                                pushStarToStack(0x10000000 | (p_spr.i_ObjectHitSteps << 8) | i_type);
                                p_spr.lg_SpriteActive = false;
                            }
                        }
                    }
                }
        ;
                break;
            case INSIDE_MODE_ANGRYSTARINCOMING:
                {
                    if (processStarEntering())
                    {
                        i_CurrentPlayMode = INSIDE_MODE_PLAYING;
                        // Переносим
                        for (int li = 0; li < MAX_SPRITES; li++)
                        {
                            Sprite p_spr = ap_Sprites[li];
                            if (!p_spr.lg_SpriteActive) continue;
                            if (p_spr.i_ObjectState < 0)
                            {
                                pushStarToStack(0x10000000 | (p_spr.i_ObjectHitSteps << 8) | p_spr.i_ObjectType - 1);
                                p_spr.lg_SpriteActive = false;
                            }
                        }
                    }
                }
        ;
                break;
        }

        if (i_CurrentPlayMode != INSIDE_MODE_PLAYERDEATH && i_PlayerLifes <= 0)
        {
            i_RoomDoCounter = TICKS_ROOM_DO;
            i_CurrentPlayMode = INSIDE_MODE_PLAYERDEATH;
            activateSprite(p_ScoreSprite, SPRITE_CLOUD);
            p_ScoreSprite.setMainPointXY(p_PlayerSprite.i_mainX, p_PlayerSprite.i_mainY);
            p_PlayerSprite.lg_SpriteInvisible = true;
        }
    }

    protected static final boolean isSpriteVisible(Sprite _spr)
    {
        int i_x = _spr.i_ScreenX;
        int i_y = _spr.i_ScreenY;
        int i_x2 = i_x + _spr.i_width;
        int i_y2 = i_y + _spr.i_height;

        int i_scrX2 = i8_ViewAreaX + (i_GameAreaWidth << 8);
        int i_scrY2 = i8_ViewAreaY + (i_GameAreaHeight << 8);

        if (i_scrX2 < i_x || i_scrY2 < i_y || i_x2 < i8_ViewAreaX || i_y2 < i8_ViewAreaY)
            return false;
        else
            return true;
    }

    private static final void generateSuperStar()
    {
        if (i_SuperStarNumber > 0) return;
        if (i_PlayerScores > 2000)
        {
            if (i_LastSuperStarType!=SPRITE_ANGRYSTAR_LEFT_MOVE && getRandomInt(10000) > 5000)
                generateAngryStar();
            else
                generateJustStar();
        }
        else if (i_PlayerScores > 100)
        {
            generateJustStar();
        }
    }

    private static final boolean processStarEntering()
    {
        boolean lg_allVisible = true;

        switch (i_CurrentPlayMode)
        {
            case INSIDE_MODE_STARINCOMING:
                {
                    // Идут справа налево
                    for (int li = 0; li < MAX_SPRITES; li++)
                    {
                        Sprite p_spr = ap_Sprites[li];
                        if (!p_spr.lg_SpriteActive) continue;

                        if (p_spr.i_ObjectState < 0)
                        {
                            p_spr.processAnimation();
                            if (p_spr.i_mainX + p_spr.i_width > (i_GameAreaWidth << 8)) lg_allVisible = false;
                            p_spr.setMainPointXY(p_spr.i_mainX - ENEMY_STAR_SPEED_FAST, p_spr.i_mainY);
                        }
                    }
                }
        ;
                break;
            case INSIDE_MODE_ANGRYSTARINCOMING:
                {
                    // Идут слева направо
                    for (int li = 0; li < MAX_SPRITES; li++)
                    {
                        Sprite p_spr = ap_Sprites[li];
                        if (!p_spr.lg_SpriteActive) continue;

                        if (p_spr.i_ObjectState < 0)
                        {
                            p_spr.processAnimation();
                            if (p_spr.i_mainX - p_spr.i_width < 0) lg_allVisible = false;
                            p_spr.setMainPointXY(p_spr.i_mainX + ENEMY_STAR_SPEED_FAST, p_spr.i_mainY);
                        }
                    }
                }
        ;
                break;
        }

        return lg_allVisible;
    }

    private static final int STAR_ENTERING_INTERVAL = 45 * 256;

    private static final void generateJustStar()
    {
        for (int li = 0; li < ap_starGenArray.length; li++) ap_starGenArray[li] = null;
        int i_number = 0;
        int i_sprType = -1;

        while(true)
        {
        int i_type = getRandomInt(3999999) / 1000000;
        switch (i_type)
        {
            case 0:
                {
                    // Девица в красном бикини
                    i_number = 3;
                    i_sprType = SPRITE_BIGSTAR_GIRL_RED_BIKINI_MOVELEFT;
                }
        ;
                break;
            case 1:
                {
                    // Парень в серой футболке
                    i_number = 3;
                    i_sprType = SPRITE_BIGSTAR_BOY_GRAY_SHIRT_MOVELEFT;
                }
        ;
                break;
            case 2:
                {
                    // Пять персонажей
                    i_number = 7;
                    i_sprType = SPRITE_BIGSTAR_BOY_GROUP_MOVELEFT;
                }
        ;
                break;
            case 3:
                {
                    // Два персонажа
                    i_number = 4;
                    i_sprType = SPRITE_BIGSTAR_GIRL_TATU_MOVELEFT;
                }
        ;
                break;
        }
        if (i_LastSuperStarType != i_sprType) break;
        }

        // Набираем спрайты
        for (int li = 0; li < i_number; li++)
        {
            Sprite p_spr = getFirstInactiveSprite();
            if (p_spr == null)
            {
                for (int lx = 0; lx < ap_starGenArray.length; lx++)
                {
                    if (ap_starGenArray[lx] != null) ap_starGenArray[lx].lg_SpriteActive = false;
                }
                return;
            }

            p_spr.lg_SpriteActive = true;
            p_spr.i_ObjectHitSteps = 0;
            ap_starGenArray[li] = p_spr;
        }

        // Формируем спрайты
        int i_Ycoord = (i_GameAreaHeight << 8);
        int i_Xcoord = (i_GameAreaWidth << 8) - 0x2000;

        int i_headNumber = 0;
        for (int li = 0; li < i_number; li++)
        {
            Sprite p_spr = ap_starGenArray[li];

            p_spr.i_ObjectState = -1;

            if (li == 0 || li == (i_number - 1))
            {
                // Охранник
                activateSprite(p_spr, SPRITE_BODYGUARD_LEFT_MOVE);
            }
            else
            {
                // Звезды
                activateSprite(p_spr, i_sprType);
                p_spr.i_ObjectHitSteps = i_headNumber;
                i_headNumber++;
            }

            i_Xcoord += STAR_ENTERING_INTERVAL;
            p_spr.i_Frame = getRandomInt(p_spr.i_maxFrames - 1);
            p_spr.setMainPointXY(i_Xcoord, i_Ycoord);
        }
        i_SuperStarNumber = i_number;
        i_CurrentPlayMode = INSIDE_MODE_STARINCOMING;
        i_LastSuperStarType = i_sprType;
        p_actionListener.processGameAction(GAMEACTION_CAR);
    }

    private static final void generateAngryStar()
    {
        for (int li = 0; li < ap_starGenArray.length; li++) ap_starGenArray[li] = null;

        int i_type = 0;
        int i_number = 0;
        int i_sprType = -1;
        switch (i_type)
        {
            case 0:
                {
                    // Парень в красной куртке
                    i_number = 3;
                    i_sprType = SPRITE_ANGRYSTAR_RIGHT_MOVE;
                }
        ;
                break;
        }

        // Набираем спрайты
        for (int li = 0; li < i_number; li++)
        {
            Sprite p_spr = getFirstInactiveSprite();
            if (p_spr == null)
            {
                for (int lx = 0; lx < ap_starGenArray.length; lx++)
                {
                    if (ap_starGenArray[lx] != null) ap_starGenArray[lx].lg_SpriteActive = false;
                }
                return;
            }

            p_spr.lg_SpriteActive = true;
            ap_starGenArray[li] = p_spr;
        }

        // Формируем спрайты
        int i_Ycoord = (i_GameAreaHeight << 8) - 0x200;
        int i_Xcoord = 0x2000;

        int i_headNumber = 0;
        for (int li = 0; li < i_number; li++)
        {
            Sprite p_spr = ap_starGenArray[li];

            p_spr.i_ObjectState = -1;

            if (li == 0 || li == (i_number - 1))
            {
                // Охранник
                activateSprite(p_spr, SPRITE_BODYGUARD_RIGHT_MOVE);
            }
            else
            {
                // Звезды
                activateSprite(p_spr, i_sprType);
                p_spr.i_ObjectHitSteps = i_headNumber;
                i_headNumber++;
            }

            i_Xcoord -= STAR_ENTERING_INTERVAL;

            p_spr.i_Frame = getRandomInt(p_spr.i_maxFrames - 1);
            p_spr.setMainPointXY(i_Xcoord, i_Ycoord);
        }

        i_CurrentPlayMode = INSIDE_MODE_ANGRYSTARINCOMING;
        i_SuperStarNumber = i_number;
        i_LastSuperStarType = SPRITE_ANGRYSTAR_LEFT_MOVE;
        p_actionListener.processGameAction(GAMEACTION_CAR);
    }


    private static final void generateStar()
    {
        if (i_DelayToNextStar > 0)
        {
            i_DelayToNextStar--;
            return;
        }

        if (getRandomInt(FREQ_BIGSTAR_GENERATION) == (FREQ_BIGSTAR_GENERATION >>> 1)) generateSuperStar();

        if (i_StartStackPointer < 0) return;

        Sprite p_spr = getFirstInactiveSprite();
        if (p_spr == null) return;

        int i_starType = popStarFromStack();
        int i_starID = i_starType & 0xFF;

        //#if DEBUG
        System.out.println("Generation of sprite " + i_starID);
        //#endif

        Sprite p_doorGen = null;
        int i_tile = -1;

        int i_startIndex = 0;
        int i_endIndex = MAX_DOORS - 1;
        int i_step = 1;

        if (getRandomInt(1000) > 500)
        {
            i_startIndex = MAX_DOORS - 1;
            i_endIndex = -1;
            i_step = -1;
        }

        int li = i_startIndex;
        while (li != i_endIndex)
        {
            Sprite p_door = ap_Doors[li];
            if (!isSpriteVisible(p_door))
            {
                i_tile = p_door.i_ObjectState >>> 16;
                if (i_starID >= FIRSTINDEX_BIGSTARS | i_starID == SPRITE_BODYGUARD_LEFT_MOVE)
                {
                    if (i_tile == TILE_TOLOC17 || i_tile == TILE_TOLOC20)
                    {
                        p_doorGen = p_door;
                        break;
                    }
                }
                else
                {
                    if (i_tile == TILE_TOLOC17 || i_tile == TILE_TOLOC20 || i_tile == TILE_TOLOC12 || i_tile == TILE_TOLOC15)
                    {
                        p_doorGen = p_door;
                        break;
                    }
                }
            }
            li += i_step;
        }

        if (p_doorGen != null)
        {
            activateSprite(p_spr, i_starID);

            int i_x = p_doorGen.i_mainX;
            int i_y = (p_doorGen.i_ScreenY + p_doorGen.i_height) - 0x100;
            p_spr.setMainPointXY(i_x, i_y);

            switch (i_starID)
            {
                case SPRITE_BIGSTAR_BOY_GRAY_SHIRT_MOVELEFT:
                case SPRITE_BIGSTAR_BOY_GROUP_MOVELEFT:
                case SPRITE_BIGSTAR_GIRL_RED_BIKINI_MOVELEFT:
                case SPRITE_BIGSTAR_GIRL_TATU_MOVELEFT:
                    {
                        p_spr.i_ObjectSpeed = ENEMY_STAR_SPEED_SLOW;
                        p_spr.i_ObjectState = HEALTH_SUPERSTAR;
                    }
            ;
                    break;
                case SPRITE_BODYGUARD_LEFT_MOVE:
                    {
                        p_spr.i_ObjectSpeed = GUARDIAN_NORMAL_SPEED;
                        p_spr.i_ObjectState = HEALTH_BODYGUARD;
                    }
            ;
                    break;
                case SPRITE_ANGRYSTAR_LEFT_MOVE:
                    {
                        p_spr.i_ObjectSpeed = GUARDIAN_NORMAL_SPEED;
                        p_spr.i_ObjectState = HEALTH_ANGRYSTAR;
                    }
            ;
                    break;
                case SPRITE_GUARDIAN_LEFT_MOVE:
                    {
                        p_spr.i_ObjectSpeed = GUARDIAN_NORMAL_SPEED;
                        p_spr.i_ObjectState = HEALTH_GUARDIAN;
                    }
            ;
                    break;
                default:
                    {
                        p_spr.i_ObjectSpeed = ENEMY_STAR_SPEED_SLOW;
                        p_spr.i_ObjectState = HEALTH_STAR;
                    }
            }

            p_spr.i_Frame = getRandomInt(p_spr.i_maxFrames - 1);


            if ((i_starType & 0x10000000) != 0)
            {
                p_spr.i_ObjectHitSteps = (i_starType >>> 8) & 0xFF;
            }
            p_spr.lg_SpriteInvisible = false;


            i_DelayToNextStar = STAR_GENERATION_DELAY;
        }
    }

    private static final boolean hitStar(Sprite _star)
    {
        int i_type = _star.i_ObjectType;

        boolean lg_lastBeat = false;

        if (--_star.i_ObjectState == 0)
        {
            lg_lastBeat = true;
            _star.i_ObjectState = 0;
        }

        boolean lg_bigStarKilled = false;

        int i_score = 0;

        if (i_type < FIRSTINDEX_BODYGUARD)
        {
            // Охранник
            i_score = lg_lastBeat ? 10 : 5;
            if (lg_lastBeat) pushStarToStackAsLast(i_type);
        }
        else if (i_type < FIRSTINDEX_SMALLSTARS)
        {
            // Телохранитель
            if (lg_lastBeat)
            {
                i_score = 10;
                i_SuperStarNumber--;
            }
            else
            {
                i_score = 5;
            }
        }
        else if (i_type < FIRSTINDEX_BIGSTARS)
        {
            // Малые звезды
            if (lg_lastBeat)
            {
                i_score = 20;
                if ((i_type & 1) == 0) i_type--;
                pushStarToStackAsLast(i_type);
            }
            else
            {
                i_score = 10;
            }
            lg_bigStarKilled = true;
        }
        else if (i_type < FIRSTINDEX_ANGRYSTARS)
        {
            // Большие звезды
            if (lg_lastBeat)
            {
                i_score = 50;
                i_SuperStarNumber--;
            }
            else
            {
                i_score = 20;
            }
            lg_bigStarKilled = true;
        }
        else
        {
            // Сердитые звезды
            if (lg_lastBeat)
            {
                i_score = 50;
                i_SuperStarNumber--;
            }
            else
            {
                i_score = 20;
            }

            lg_bigStarKilled = true;
        }

        if (lg_lastBeat)
        {
            if ((getRandomInt(FREQ_HEART_GEN) == (FREQ_HEART_GEN >> 1)))
            {
                // Генерируем сердце
                activateSprite(_star, SPRITE_BONUS_HEART);
            }
            else
            {
                activateSprite(_star, SPRITE_CLOUD);
            }
        }

        // Генерируем очки
        int i_sprType = 0;
        switch (i_score)
        {
            case 5:
                i_sprType = SPRITE_ICON_SCORE_5;
                break;
            case 10:
                i_sprType = SPRITE_ICON_SCORE_10;
                break;
            case 20:
                i_sprType = SPRITE_ICON_SCORE_20;
                break;
            case 50:
                i_sprType = SPRITE_ICON_SCORE_50;
                break;
        }

        activateSprite(p_ScoreSprite, i_sprType);
        p_ScoreSprite.i_ObjectState = 0;
        p_ScoreSprite.setMainPointXY(_star.i_mainX, _star.i_mainY);

        i_PlayerScores += i_score;

        return (_star.i_ObjectState == 0);
    }

    private static final void processStars(boolean _playerStrike)
    {
        if (p_ScoreSprite.lg_SpriteActive)
        {
            if (p_ScoreSprite.processAnimation()) p_ScoreSprite.lg_SpriteActive = false;
        }

        boolean lg_playerVisible = !p_PlayerSprite.lg_SpriteInvisible;

        boolean lg_playerHasStrike = false;
        boolean lg_playerUnderAttack = p_PlayerSprite.i_ObjectHitSteps > 0;

        int i_x0 = -90000;
        int i_x1 = -90000;

        int i_playerX = p_PlayerSprite.i_mainX;

        if (_playerStrike)
        {
            if (p_PlayerSprite.i_ObjectType == SPRITE_PLAYER_LEFT_BEAT)
            {
                // Бьет влево
                i_x0 = p_PlayerSprite.i_ScreenX + PILLOW_ZONE_OFFSET;
                i_x1 = i_x0 + PILLOW_WIDTH;
            }
            else
            {
                // Бьет вправо
                i_x1 = p_PlayerSprite.i_ScreenX + p_PlayerSprite.i_width - PILLOW_ZONE_OFFSET;
                i_x0 = i_x1 - PILLOW_WIDTH;
            }
        }

        boolean lg_hitToStar = false;

        for (int li = 0; li < MAX_SPRITES; li++)
        {
            Sprite p_spr = ap_Sprites[li];
            if (!p_spr.lg_SpriteActive) continue;

            p_spr.lg_SpriteInvisible = false;

            boolean lg_anim = p_spr.processAnimation();
            boolean lg_isVisible = isSpriteVisible(p_spr);

            final int STATE_NONE = 0;
            final int STATE_MOVELEFT = 1;
            final int STATE_MOVERIGHT = 2;
            final int STATE_HITMOVELEFT = 3;
            final int STATE_HITMOVERIGHT = 4;

            int i_state = STATE_NONE;

            int i_sprX = p_spr.i_mainX;

            boolean lg_onTheLevel = p_spr.i_mainY == p_PlayerSprite.i_mainY;

            boolean lg_hit = false;
            if (lg_onTheLevel && lg_isVisible && _playerStrike)
            {
                boolean lg_process = false;
                if (p_spr.i_ObjectType >= FIRSTINDEX_SMALLSTARS && p_spr.i_ObjectType < FIRSTINDEX_ANGRYSTARS)
                {
                    lg_process = true;
                }
                else
                {
                    if (p_spr.i_ObjectHitSteps == 0) lg_process = true;
                }

                if (lg_process)
                {
                    int i_sx0 = p_spr.i_ScreenX + p_spr.i_col_offsetX;
                    int i_sx1 = i_sx0 + p_spr.i_col_width;

                    if (i_x1 < i_sx0 || i_sx1 < i_x0)
                    {
                        lg_hit = false;
                    }
                    else
                    {
                        lg_hit = true;
                    }
                }
            }

            lg_hitToStar |= lg_hit;

            switch (p_spr.i_ObjectType)
            {
                case SPRITE_BONUS_HEART:
                    {
                        if (p_PlayerSprite.isCollided(p_spr))
                        {
                            activateSprite(p_ScoreSprite, SPRITE_ICON_HEART);
                            p_actionListener.processGameAction(GAMEACTION_TAKEHEART);
                            p_spr.lg_SpriteActive = false;
                            i_PlayerLifes++;
                            continue;
                        }
                    }
            ;
                    break;
                case SPRITE_CLOUD:
                    {
                        if (lg_anim)
                        {
                            p_spr.lg_SpriteActive = false;
                            continue;
                        }
                    }
            ;
                    break;
                case SPRITE_GUARDIAN_RIGHT_BEAT:
                case SPRITE_GUARDIAN_LEFT_BEAT:
                    {
                        if (lg_anim)
                        {
                            if (i_CurrentPlayMode == INSIDE_MODE_PLAYING && !(lg_playerHasStrike | lg_playerUnderAttack))
                            {
                                if (p_spr.i_ObjectType == SPRITE_GUARDIAN_RIGHT_BEAT)
                                {
                                    int i_dist = i_playerX - i_sprX;
                                    if (i_dist < GUARDIAN_DISTANCE_BEAT && i_dist > 0)
                                    {
                                        p_PlayerSprite.i_ObjectHitSteps = HIT_STEPS;
                                        i_PlayerLifes--;
                                        p_PlayerSprite.i_ObjectState = HIT_SPEED;
                                        lg_playerHasStrike = true;
                                        p_PlayerSprite.lg_SpriteInvisible = true;
                                    }
                                }
                                else
                                {
                                    int i_dist = i_sprX - i_playerX;
                                    if (i_dist < GUARDIAN_DISTANCE_BEAT && i_dist > 0)
                                    {
                                        p_PlayerSprite.i_ObjectHitSteps = HIT_STEPS;
                                        i_PlayerLifes--;
                                        p_PlayerSprite.i_ObjectState = 0 - HIT_SPEED;
                                        lg_playerHasStrike = true;
                                        p_PlayerSprite.lg_SpriteInvisible = true;
                                    }
                                }

                            }

                            if (i_sprX < p_PlayerSprite.i_mainX)
                            {
                                activateSprite(p_spr, SPRITE_GUARDIAN_RIGHT_MOVE);
                            }
                            else
                                activateSprite(p_spr, SPRITE_GUARDIAN_LEFT_MOVE);
                        }
                    }
            ;
                    break;
                case SPRITE_ANGRYSTAR_RIGHT_BEAT:
                case SPRITE_ANGRYSTAR_LEFT_BEAT:
                    {
                        if (lg_anim)
                        {
                            if (i_CurrentPlayMode == INSIDE_MODE_PLAYING && !(lg_playerHasStrike | lg_playerUnderAttack))
                            {
                                if (p_spr.i_ObjectType == SPRITE_ANGRYSTAR_RIGHT_BEAT)
                                {
                                    int i_dist = i_playerX - i_sprX;
                                    if (i_dist < GUARDIAN_DISTANCE_BEAT && i_dist > 0)
                                    {
                                        p_PlayerSprite.i_ObjectHitSteps = HIT_STEPS;
                                        i_PlayerLifes--;
                                        p_PlayerSprite.i_ObjectState = HIT_SPEED;
                                        lg_playerHasStrike = true;
                                        p_PlayerSprite.lg_SpriteInvisible = true;
                                    }
                                }
                                else
                                {
                                    int i_dist = i_sprX - i_playerX;
                                    if (i_dist < GUARDIAN_DISTANCE_BEAT && i_dist > 0)
                                    {
                                        i_PlayerLifes--;
                                        p_PlayerSprite.i_ObjectHitSteps = HIT_STEPS;
                                        p_PlayerSprite.i_ObjectState = 0 - HIT_SPEED;
                                        lg_playerHasStrike = true;
                                        p_PlayerSprite.lg_SpriteInvisible = true;
                                    }
                                }

                            }

                            if (i_sprX < p_PlayerSprite.i_mainX)
                            {
                                activateSprite(p_spr, SPRITE_ANGRYSTAR_RIGHT_MOVE);
                            }
                            else
                                activateSprite(p_spr, SPRITE_ANGRYSTAR_LEFT_MOVE);
                        }
                    }
            ;
                    break;
                case SPRITE_BODYGUARD_RIGHT_BEAT:
                case SPRITE_BODYGUARD_LEFT_BEAT:
                    {
                        if (lg_anim)
                        {
                            if (i_CurrentPlayMode == INSIDE_MODE_PLAYING && !(lg_playerHasStrike | lg_playerUnderAttack))
                            {
                                if (p_spr.i_ObjectType == SPRITE_BODYGUARD_RIGHT_BEAT)
                                {
                                    int i_dist = i_playerX - i_sprX;
                                    if (i_dist < GUARDIAN_DISTANCE_BEAT && i_dist > 0)
                                    {
                                        p_PlayerSprite.i_ObjectHitSteps = HIT_STEPS;
                                        i_PlayerLifes--;
                                        p_PlayerSprite.i_ObjectState = HIT_SPEED;
                                        lg_playerHasStrike = true;
                                        p_PlayerSprite.lg_SpriteInvisible = true;
                                    }
                                }
                                else
                                {
                                    int i_dist = i_sprX - i_playerX;
                                    if (i_dist < GUARDIAN_DISTANCE_BEAT && i_dist > 0)
                                    {
                                        p_PlayerSprite.i_ObjectHitSteps = HIT_STEPS;
                                        i_PlayerLifes--;
                                        p_PlayerSprite.i_ObjectState = 0 - HIT_SPEED;
                                        lg_playerHasStrike = true;
                                        p_PlayerSprite.lg_SpriteInvisible = true;
                                    }
                                }
                            }

                            if (i_sprX < p_PlayerSprite.i_mainX)
                            {
                                activateSprite(p_spr, SPRITE_BODYGUARD_RIGHT_MOVE);
                            }
                            else
                                activateSprite(p_spr, SPRITE_BODYGUARD_LEFT_MOVE);
                        }
                    }
            ;
                    break;
                case SPRITE_GUARDIAN_RIGHT_MOVE:
                    {
                        if (p_spr.i_ObjectHitSteps > 0)
                        {
                            p_spr.i_ObjectHitSteps--;
                            p_spr.i_Frame = 0;
                            p_spr.i_ObjectSpeed = HIT_SPEED;
                            i_state = STATE_HITMOVELEFT;
                        }
                        else if (lg_isVisible && lg_playerVisible)
                        {
                            if (lg_hit)
                            {
                                if (!hitStar(p_spr))
                                {
                                    p_spr.i_ObjectHitSteps = HIT_STEPS;
                                    p_spr.i_Frame = 0;
                                    p_spr.lg_SpriteInvisible = true;
                                    continue;
                                }
                                else
                                    continue;
                            }
                            else if (lg_onTheLevel)
                            {
                                // На одном уровне и экране
                                // Поворачиваем охранника если идет в другую сторону
                                int i_pX = p_PlayerSprite.i_mainX;

                                if (i_sprX > i_pX)
                                {
                                    activateSprite(p_spr, SPRITE_GUARDIAN_LEFT_MOVE);
                                }
                                else
                                {
                                    p_spr.i_ObjectSpeed = GUARDIAN_NORMAL_SPEED;
                                    // Проверяем на возможность удара
                                    int i_dist = i_pX - i_sprX;
                                    if (!lg_playerUnderAttack && (i_dist < GUARDIAN_DISTANCE_BEAT && i_dist > 0))
                                    {
                                        activateSprite(p_spr, SPRITE_GUARDIAN_RIGHT_BEAT);
                                    }
                                    else
                                        i_state = STATE_MOVERIGHT;
                                }
                            }
                        }
                        else
                        {
                            if (!lg_isVisible)
                            {
                                Sprite p_door = getDoor(p_spr);
                                if (p_door != null)
                                {
                                    if (getRandomInt(FREQ_STAR_HIDDEN) == (FREQ_STAR_HIDDEN >> 1))
                                    {
                                        pushStarToStackAsLast(SPRITE_GUARDIAN_LEFT_MOVE);
                                        p_spr.lg_SpriteActive = false;
                                        continue;
                                    }
                                }
                            }
                            p_spr.i_ObjectSpeed = GUARDIAN_FAST_SPEED;
                            i_state = STATE_MOVERIGHT;
                        }
                    }
            ;
                    break;
                case SPRITE_GUARDIAN_LEFT_MOVE:
                    {
                        if (p_spr.i_ObjectHitSteps > 0)
                        {
                            p_spr.i_ObjectHitSteps--;
                            p_spr.i_Frame = 0;
                            p_spr.i_ObjectSpeed = HIT_SPEED;
                            i_state = STATE_HITMOVERIGHT;
                        }
                        else if (lg_isVisible && lg_playerVisible)
                        {
                            if (lg_hit)
                            {
                                if (!hitStar(p_spr))
                                {
                                    p_spr.i_ObjectHitSteps = HIT_STEPS;
                                    p_spr.lg_SpriteInvisible = true;
                                    continue;
                                }
                                else
                                    continue;
                            }
                            else if (lg_onTheLevel)
                            {
                                // На одном уровне и экране
                                // Поворачиваем охранника если идет в другую сторону
                                int i_pX = p_PlayerSprite.i_mainX;

                                if (i_sprX < i_pX)
                                {
                                    activateSprite(p_spr, SPRITE_GUARDIAN_RIGHT_MOVE);
                                }
                                else
                                {
                                    p_spr.i_ObjectSpeed = GUARDIAN_NORMAL_SPEED;
                                    // Проверяем на возможность удара
                                    int i_dist = i_sprX - i_pX;
                                    if (!lg_playerUnderAttack && (i_dist < GUARDIAN_DISTANCE_BEAT && i_dist > 0))
                                    {
                                        activateSprite(p_spr, SPRITE_GUARDIAN_LEFT_BEAT);
                                    }
                                    else
                                        i_state = STATE_MOVELEFT;
                                }
                            }
                        }
                        else
                        {
                            if (!lg_isVisible)
                            {
                                Sprite p_door = getDoor(p_spr);
                                if (p_door != null)
                                {
                                    if (getRandomInt(FREQ_STAR_HIDDEN) == (FREQ_STAR_HIDDEN >> 1))
                                    {
                                        pushStarToStackAsLast(SPRITE_GUARDIAN_LEFT_MOVE);
                                        p_spr.lg_SpriteActive = false;
                                        continue;
                                    }
                                }
                            }

                            p_spr.i_ObjectSpeed = GUARDIAN_FAST_SPEED;
                            i_state = STATE_MOVELEFT;
                        }
                    }
            ;
                    break;
                case SPRITE_ANGRYSTAR_RIGHT_MOVE:
                case SPRITE_BODYGUARD_RIGHT_MOVE:
                    {
                        if (p_spr.i_ObjectHitSteps > 0)
                        {
                            p_spr.i_ObjectHitSteps--;
                            p_spr.i_Frame = 0;
                            p_spr.i_ObjectSpeed = HIT_SPEED;
                            i_state = STATE_HITMOVELEFT;
                        }
                        else if (lg_isVisible && lg_playerVisible)
                        {
                            if (lg_hit)
                            {
                                if (!hitStar(p_spr))
                                {
                                    p_spr.i_ObjectHitSteps = HIT_STEPS;
                                    p_spr.lg_SpriteInvisible = true;
                                    continue;
                                }
                                else
                                    continue;
                            }
                            else if (lg_onTheLevel)
                            {
                                // На одном уровне и экране
                                // Поворачиваем если идет в другую сторону
                                int i_pX = p_PlayerSprite.i_mainX;

                                if (i_sprX > i_pX)
                                {
                                    activateSprite(p_spr, p_spr.i_ObjectType - 1);
                                }
                                else
                                {
                                    p_spr.i_ObjectSpeed = GUARDIAN_NORMAL_SPEED;
                                    // Проверяем на возможность удара
                                    p_spr.i_ObjectSpeed = GUARDIAN_NORMAL_SPEED;
                                    // Проверяем на возможность удара
                                    int i_dist = i_pX - i_sprX;
                                    if (!lg_playerUnderAttack && (i_dist < GUARDIAN_DISTANCE_BEAT && i_dist > 0))
                                    {
                                        if (p_spr.i_ObjectType == SPRITE_ANGRYSTAR_RIGHT_MOVE)
                                            activateSprite(p_spr, SPRITE_ANGRYSTAR_RIGHT_BEAT);
                                        else
                                            activateSprite(p_spr, SPRITE_BODYGUARD_RIGHT_BEAT);
                                    }
                                    else
                                        i_state = STATE_MOVERIGHT;
                                }
                            }
                        }
                        else
                        {
                            p_spr.i_ObjectSpeed = GUARDIAN_FAST_SPEED;
                            i_state = STATE_MOVERIGHT;
                        }
                    }
            ;
                    break;
                case SPRITE_ANGRYSTAR_LEFT_MOVE:
                case SPRITE_BODYGUARD_LEFT_MOVE:
                    {
                        if (p_spr.i_ObjectHitSteps > 0)
                        {
                            p_spr.i_ObjectHitSteps--;
                            p_spr.i_Frame = 0;
                            p_spr.i_ObjectSpeed = HIT_SPEED;
                            i_state = STATE_HITMOVERIGHT;
                        }
                        else if (lg_isVisible && lg_playerVisible)
                        {
                            if (lg_hit)
                            {
                                if (!hitStar(p_spr))
                                {
                                    p_spr.i_ObjectHitSteps = HIT_STEPS;
                                    p_spr.lg_SpriteInvisible = true;
                                    continue;
                                }
                                else
                                    continue;
                            }
                            else if (lg_onTheLevel)
                            {
                                // На одном уровне и экране
                                // Поворачиваем охранника если идет в другую сторону
                                int i_pX = p_PlayerSprite.i_mainX;

                                if (i_sprX < i_pX)
                                {
                                    activateSprite(p_spr, p_spr.i_ObjectType + 1);
                                }
                                else
                                {
                                    p_spr.i_ObjectSpeed = GUARDIAN_NORMAL_SPEED;
                                    // Проверяем на возможность удара
                                    int i_dist = i_sprX - i_pX;
                                    if (!lg_playerUnderAttack && (i_dist < GUARDIAN_DISTANCE_BEAT && i_dist > 0))
                                    {
                                        if (p_spr.i_ObjectType == SPRITE_ANGRYSTAR_LEFT_MOVE)
                                            activateSprite(p_spr, SPRITE_ANGRYSTAR_LEFT_BEAT);
                                        else
                                            activateSprite(p_spr, SPRITE_BODYGUARD_LEFT_BEAT);
                                    }
                                    else
                                        i_state = STATE_MOVELEFT;
                                }
                            }
                        }
                        else
                        {
                            p_spr.i_ObjectSpeed = GUARDIAN_FAST_SPEED;
                            i_state = STATE_MOVELEFT;
                        }
                    }
            ;
                    break;
                case SPRITE_BIGSTAR_BOY_GRAY_SHIRT_MOVELEFT:
                case SPRITE_BIGSTAR_BOY_GROUP_MOVELEFT:
                case SPRITE_BIGSTAR_GIRL_RED_BIKINI_MOVELEFT:
                case SPRITE_BIGSTAR_GIRL_TATU_MOVELEFT:
                    {
                        if (_playerStrike && lg_hit)
                        {
                            p_spr.i_ObjectSpeed = ENEMY_STAR_SPEED_FAST;

                            if (hitStar(p_spr))
                            {
                                continue;
                            }

                            if (i_sprX > i_playerX)
                            {
                                activateSprite(p_spr, p_spr.i_ObjectType + 1);
                            }
                            p_spr.lg_SpriteInvisible = true;
                        }
                        else
                        {
                            i_state = STATE_MOVELEFT;
                        }
                    }
            ;
                    break;
                case SPRITE_BIGSTAR_BOY_GRAY_SHIRT_MOVERIGHT:
                case SPRITE_BIGSTAR_BOY_GROUP_MOVERIGHT:
                case SPRITE_BIGSTAR_GIRL_RED_BIKINI_MOVERIGHT:
                case SPRITE_BIGSTAR_GIRL_TATU_MOVERIGHT:
                    {
                        if (_playerStrike && lg_hit)
                        {
                            p_spr.i_ObjectSpeed = ENEMY_STAR_SPEED_FAST;

                            if (hitStar(p_spr))
                            {
                                continue;
                            }

                            if (i_sprX < i_playerX)
                            {
                                activateSprite(p_spr, p_spr.i_ObjectType - 1);
                            }
                            p_spr.lg_SpriteInvisible = true;
                        }
                        else
                        {
                            i_state = STATE_MOVERIGHT;
                        }
                    }
            ;
                    break;
                case SPRITE_SMALLSTAR_BOY_BLACK_JACKET_MOVELEFT:
                case SPRITE_SMALLSTAR_BOY_WHITEHAIR_SUIT_MOVELEFT:
                case SPRITE_SMALLSTAR_BOY_WHITEHAIR_VEST_MOVELEFT:
                case SPRITE_SMALLSTAR_GIRL_CATSUIT_MOVELEFT:
                    //case SPRITE_SMALLSTAR_GIRL_SKIRT_MOVELEFT:
                    {
                        if (_playerStrike && lg_hit)
                        {
                            p_spr.i_ObjectSpeed = ENEMY_STAR_SPEED_FAST;

                            if (hitStar(p_spr))
                            {
                                continue;
                            }

                            if (i_sprX > i_playerX)
                            {
                                activateSprite(p_spr, p_spr.i_ObjectType + 1);
                            }
                            p_spr.lg_SpriteInvisible = true;
                        }
                        else
                        {
                            i_state = STATE_MOVELEFT;

                            if (!lg_isVisible)
                            {
                                Sprite p_door = getDoor(p_spr);
                                if (p_door != null)
                                {
                                    if (getRandomInt(FREQ_STAR_HIDDEN) == (FREQ_STAR_HIDDEN >> 1))
                                    {
                                        pushStarToStackAsLast(p_spr.i_ObjectType);
                                        p_spr.lg_SpriteActive = false;
                                        continue;
                                    }
                                }
                            }
                        }
                    }
            ;
                    break;
                case SPRITE_SMALLSTAR_BOY_BLACK_JACKET_MOVERIGHT:
                case SPRITE_SMALLSTAR_BOY_WHITEHAIR_SUIT_MOVERIGHT:
                case SPRITE_SMALLSTAR_BOY_WHITEHAIR_VEST_MOVERIGHT:
                case SPRITE_SMALLSTAR_GIRL_CATSUIT_MOVERIGHT:
                    //case SPRITE_SMALLSTAR_GIRL_SKIRT_MOVERIGHT:
                    {

                        if (_playerStrike && lg_hit)
                        {
                            p_spr.i_ObjectSpeed = ENEMY_STAR_SPEED_FAST;

                            if (hitStar(p_spr))
                            {
                                continue;
                            }

                            if (i_sprX < i_playerX)
                            {
                                activateSprite(p_spr, p_spr.i_ObjectType - 1);
                            }

                            p_spr.lg_SpriteInvisible = true;
                        }
                        else
                        {
                            i_state = STATE_MOVERIGHT;

                            if (!lg_isVisible)
                            {
                                Sprite p_door = getDoor(p_spr);
                                if (p_door != null)
                                {
                                    if (getRandomInt(FREQ_STAR_HIDDEN) == (FREQ_STAR_HIDDEN >> 1))
                                    {
                                        pushStarToStackAsLast(p_spr.i_ObjectType - 1);
                                        p_spr.lg_SpriteActive = false;
                                        continue;
                                    }
                                }
                            }
                        }
                    }
            ;
                    break;
            }

            int i_speed = p_spr.i_ObjectSpeed;
            int i_x = p_spr.i_mainX;
            int i_y = p_spr.i_mainY;
            int i_w = (p_spr.i_col_width >> 1);

            switch (i_state)
            {
                case STATE_MOVERIGHT:
                    {
                        i_x += i_speed;
                        int i_xcell = ((i_x + i_w) / (CELL_WIDTH << 8));
                        int i_ycell = i_y / (CELL_HEIGHT << 8);
                        int i_cell = (i_ycell * CELLSNUMBER_WIDTH) + i_xcell;
                        if (ash_Building[i_cell] == TILE_WALL)
                        {
                            activateSprite(p_spr, p_spr.i_ObjectType - 1);
                        }
                        p_spr.setMainPointXY(i_x, i_y);
                    }
            ;
                    break;
                case STATE_MOVELEFT:
                    {
                        i_x -= i_speed;
                        int i_xcell = ((i_x - i_w) / (CELL_WIDTH << 8));
                        int i_ycell = i_y / (CELL_HEIGHT << 8);
                        int i_cell = (i_ycell * CELLSNUMBER_WIDTH) + i_xcell;
                        if (ash_Building[i_cell] == TILE_WALL)
                        {
                            activateSprite(p_spr, p_spr.i_ObjectType + 1);
                        }
                        p_spr.setMainPointXY(i_x, i_y);
                    }
            ;
                    break;
                case STATE_HITMOVERIGHT:
                    {
                        i_x += i_speed;
                        int i_xcell = ((i_x + i_w) / (CELL_WIDTH << 8));
                        int i_ycell = i_y / (CELL_HEIGHT << 8);
                        int i_cell = (i_ycell * CELLSNUMBER_WIDTH) + i_xcell;
                        if (ash_Building[i_cell] == TILE_WALL)
                        {
                            int i_cellX = i_xcell * (CELL_WIDTH << 8);
                            i_x = i_cellX - i_w - 0x100;
                        }
                        p_spr.setMainPointXY(i_x, i_y);
                    }
            ;
                    break;
                case STATE_HITMOVELEFT:
                    {
                        i_x -= i_speed;
                        int i_xcell = ((i_x - i_w) / (CELL_WIDTH << 8));
                        int i_ycell = i_y / (CELL_HEIGHT << 8);
                        int i_cell = (i_ycell * CELLSNUMBER_WIDTH) + i_xcell;
                        if (ash_Building[i_cell] == TILE_WALL)
                        {
                            int i_cellX = ((i_xcell + 1) * (CELL_WIDTH << 8));
                            i_x = i_cellX + i_w + 0x100;
                        }
                        p_spr.setMainPointXY(i_x, i_y);
                    }
            ;
                    break;
            }
        }

        if(lg_playerHasStrike || lg_hitToStar)
            p_actionListener.processGameAction(GAMEACTION_HIT);
        else
        {
            if (_playerStrike && !lg_hitToStar)
            {
                p_actionListener.processGameAction(GAMEACTION_MIMO);
            }
        }

        if (lg_hitToStar)
        {
            if (i_SuperShells > 0)
            {
                i_SuperShells--;
                if (i_SuperShells == 0)
                {
                    i_PlayerShells--;
                }
            }
            else
            {
                i_PlayerShells--;
            }
        }
    }

    private static final boolean processPlayer(final int _playerKeys)
    {
        boolean lg_anim = p_PlayerSprite.processAnimation();

        int i_prevX = p_PlayerSprite.i_mainX;

        if (p_PlayerSprite.i_ObjectHitSteps > 0)
        {
            p_PlayerSprite.lg_SpriteInvisible = false;

            p_PlayerSprite.i_ObjectHitSteps--;
            p_PlayerSprite.i_Frame = 0;
            int i_x = p_PlayerSprite.i_mainX + p_PlayerSprite.i_ObjectState;
            p_PlayerSprite.setMainPointXY(i_x, p_PlayerSprite.i_mainY);
        }
        else
            switch (p_PlayerSprite.i_ObjectType)
            {
                case SPRITE_PLAYER_LEFT_BEAT:
                    {
                        if (lg_anim)
                        {
                            activateSprite(p_PlayerSprite, SPRITE_PLAYER_LEFT_STAND);
                            return false;
                        }
                        else if (p_PlayerSprite.i_Frame == p_PlayerSprite.i_maxFrames - 1)
                        {
                            return true;
                        }
                    }
                case SPRITE_PLAYER_RIGHT_BEAT:
                    {
                        if (lg_anim)
                        {
                            activateSprite(p_PlayerSprite, SPRITE_PLAYER_RIGHT_STAND);
                            return false;
                        }
                        else if (p_PlayerSprite.i_Frame == p_PlayerSprite.i_maxFrames - 1)
                        {
                            return true;
                        }
                    }
                default:
                    {
                        switch (i_CurrentPlayMode)
                        {
                            case INSIDE_MODE_PLAYING:
                                {
                                    if (p_PlayerSprite.i_ObjectHitSteps == 0)
                                    {
                                        if (_playerKeys == KEY_NONE)
                                        {
                                            switch (p_PlayerSprite.i_ObjectType)
                                            {
                                                case SPRITE_PLAYER_LEFT_MOVE:
                                                    {
                                                        activateSprite(p_PlayerSprite, SPRITE_PLAYER_LEFT_STAND);
                                                    }
                                            ;
                                                    break;
                                                case SPRITE_PLAYER_RIGHT_MOVE:
                                                    {
                                                        activateSprite(p_PlayerSprite, SPRITE_PLAYER_RIGHT_STAND);
                                                    }
                                            ;
                                                    break;
                                            }
                                        }
                                        else if ((_playerKeys & KEY_FIRE) != 0)
                                        {
                                            if (i_PlayerShells > 0)
                                            {
                                                switch (p_PlayerSprite.i_ObjectType)
                                                {
                                                    case SPRITE_PLAYER_LEFT_MOVE:
                                                    case SPRITE_PLAYER_LEFT_STAND:
                                                        {
                                                            activateSprite(p_PlayerSprite, SPRITE_PLAYER_LEFT_BEAT);
                                                        }
                                                ;
                                                        break;
                                                    case SPRITE_PLAYER_RIGHT_MOVE:
                                                    case SPRITE_PLAYER_RIGHT_STAND:
                                                        {
                                                            activateSprite(p_PlayerSprite, SPRITE_PLAYER_RIGHT_BEAT);
                                                        }
                                                ;
                                                        break;
                                                }
                                            }
                                        }
                                        else if ((_playerKeys & KEY_LEFT) != 0)
                                        {
                                            if (p_PlayerSprite.i_ObjectType != SPRITE_PLAYER_LEFT_MOVE)
                                            {
                                                activateSprite(p_PlayerSprite, SPRITE_PLAYER_LEFT_MOVE);
                                            }
                                            else
                                            {
                                                int i_x = p_PlayerSprite.i_mainX - PLAYER_HORZ_SPEED;
                                                p_PlayerSprite.setMainPointXY(i_x, p_PlayerSprite.i_mainY);
                                            }
                                        }
                                        else if ((_playerKeys & KEY_RIGHT) != 0)
                                        {
                                            if (p_PlayerSprite.i_ObjectType != SPRITE_PLAYER_RIGHT_MOVE)
                                            {
                                                activateSprite(p_PlayerSprite, SPRITE_PLAYER_RIGHT_MOVE);
                                            }
                                            else
                                            {
                                                int i_x = p_PlayerSprite.i_mainX + PLAYER_HORZ_SPEED;
                                                p_PlayerSprite.setMainPointXY(i_x, p_PlayerSprite.i_mainY);
                                            }
                                        }
                                        else if ((_playerKeys & KEY_UP) != 0 || (_playerKeys & KEY_DOWN) != 0)
                                        {
                                            Sprite p_src = getDoor(p_PlayerSprite);
                                            if (p_src != null)
                                            {
                                                Sprite p_dest = getDestinationForLocation(p_src);
                                                if (p_dest == null)
                                                {
                                                    // Попали в комнату
                                                    p_PlayerSprite.lg_SpriteInvisible = true;

                                                    // Проверка на комнату смерти
                                                    int i_roomID = (p_src.i_ObjectState >>> 16);
                                                    i_RoomDoCounter = TICKS_ROOM_DO;
                                                    switch (i_roomID)
                                                    {
                                                        case TILE_DEATHROOM:
                                                            {
                                                                i_CurrentPlayMode = INSIDE_MODE_DEADROOM;
                                                                i_PrevDeadRoomXOffset = 0;
                                                                i_PrevDeadRoomYOffset = 0;
                                                                p_actionListener.processGameAction(GAMEACTION_DEADROOM);
                                                            }
                                                    ;
                                                            break;
                                                        case TILE_STUDIO:
                                                            {
                                                                i_CurrentPlayMode = INSIDE_MODE_ROOM;
                                                                i_RoomShowCounter = SHOW_ROOM_DELAY;
                                                                i_ActiveRoomType = ROOMTYPE_STUDIO;
                                                            }
                                                    ;
                                                            break;
                                                        case TILE_CABINET:
                                                            {
                                                                boolean lg_superShell = false;
                                                                if (i_PlayerScores > 1000)
                                                                {
                                                                    if (getRandomInt(10000) > 9000)
                                                                    {
                                                                        // Супернаволочка
                                                                        i_CurrentPlayMode = INSIDE_MODE_ROOM;
                                                                        i_RoomShowCounter = SHOW_ROOM_DELAY;
                                                                        i_ActiveRoomType = ROOMTYPE_SUPERPILLOW;

                                                                        i_SuperShells = SUPERSHELL_NUMBER;
                                                                        i_PlayerShells++;
                                                                        lg_superShell = true;
                                                                    }
                                                                }
                                                                if (!lg_superShell)
                                                                {
                                                                    switch (getRandomInt(99999) / 10000)
                                                                    {
                                                                        case 0:
                                                                        case 1:
                                                                        case 2:
                                                                            i_ActiveRoomType = ROOMTYPE_EMPTYTABLE;
                                                                            break;
                                                                        case 3:
                                                                        case 4:
                                                                            {
                                                                                i_ActiveRoomType = ROOMTYPE_PILLOWTABLE;
                                                                                i_PlayerShells += SHELLS_IN_A_ROOM;
                                                                            }
                                                                    ;
                                                                            break;
                                                                        case 5:
                                                                        case 6:
                                                                            i_ActiveRoomType = ROOMTYPE_COMPUTERTABLE;
                                                                            break;
                                                                        case 7:
                                                                            i_ActiveRoomType = ROOMTYPE_BATHROOM;
                                                                            break;
                                                                        case 8:
                                                                        case 9:
                                                                            {
                                                                                i_ActiveRoomType = ROOMTYPE_GUARDIAN;

                                                                                i_PlayerLifes--;
                                                                            }
                                                                    ;
                                                                            break;
                                                                    }
                                                                    i_CurrentPlayMode = INSIDE_MODE_ROOM;
                                                                    i_RoomShowCounter = SHOW_ROOM_DELAY;
                                                                }
                                                                p_PlayerSprite.i_ObjectHitSteps = 0;
                                                                p_PlayerSprite.i_ObjectState = 0;
                                                            }
                                                    ;
                                                            break;
                                                    }
                                                    if (i_roomID != TILE_DEATHROOM)
                                                        p_actionListener.processGameAction(GAMEACTION_DOOR);
                                                    else
                                                        if (i_ActiveRoomType == ROOMTYPE_GUARDIAN)
                                                        {
                                                            p_actionListener.processGameAction(GAMEACTION_HIT);
                                                        }
                                                }
                                                else
                                                {
                                                    // Переход
                                                    p_PlayerSprite.i_ObjectHitSteps = 0;
                                                    p_PlayerSprite.i_ObjectState = 0;
                                                    i_CurrentPlayMode = INSIDE_MODE_LOCATIONMOVE;
                                                    p_PlayerSprite.lg_SpriteInvisible = true;
                                                    i_DestLocationIndex = p_dest.i_spriteID;
                                                }
                                            }
                                        }
                                    }
                                }
                        ;
                                break;
                            case INSIDE_MODE_ROOM:
                                {
                                    if ((_playerKeys & KEY_UP) != 0 || (_playerKeys & KEY_DOWN) != 0)
                                    {
                                        //activateSprite(p_PlayerSprite, SPRITE_PLAYER_LEFT_STAND);
                                        p_PlayerSprite.lg_SpriteInvisible = false;
                                        i_CurrentPlayMode = INSIDE_MODE_PLAYING;
                                        i_RoomShowCounter = 0;
                                        i_RoomDoCounter = 0;
                                    }
                                }
                        ;
                                break;
                        }
                    }
            }

        // Проверка на пересечение с вертикальной стеной и выравнивание
        switch (p_PlayerSprite.i_ObjectType)
        {
            case SPRITE_PLAYER_LEFT_BEAT:
            case SPRITE_PLAYER_LEFT_MOVE:
            case SPRITE_PLAYER_LEFT_STAND:
                {
                    int i_plX = p_PlayerSprite.i_ScreenX + p_PlayerSprite.i_col_offsetX;
                    int i_x = (i_plX / CELL_WIDTH) >> 8;
                    int i_y = ((p_PlayerSprite.i_ScreenY) / CELL_HEIGHT) >> 8;
                    int i_cellCoord = i_y * CELLSNUMBER_WIDTH + i_x;

                    int i_cell = ash_Building[i_cellCoord];
                    if (i_cell == TILE_WALL)
                    {
                        p_PlayerSprite.setMainPointXY(i_prevX, p_PlayerSprite.i_mainY);
                    }

                    if (p_PlayerSprite.i_ObjectHitSteps > 0)
                    {
                        i_plX = p_PlayerSprite.i_ScreenX + p_PlayerSprite.i_col_offsetX + p_PlayerSprite.i_col_width;
                        i_x = (i_plX / CELL_WIDTH) >> 8;
                        i_y = ((p_PlayerSprite.i_ScreenY) / CELL_HEIGHT) >> 8;
                        i_cellCoord = i_y * CELLSNUMBER_WIDTH + i_x;

                        i_cell = ash_Building[i_cellCoord];
                        if (i_cell == TILE_WALL)
                        {
                            p_PlayerSprite.setMainPointXY(i_prevX, p_PlayerSprite.i_mainY);
                        }
                    }
                }
        ;
                break;
            case SPRITE_PLAYER_RIGHT_BEAT:
            case SPRITE_PLAYER_RIGHT_MOVE:
            case SPRITE_PLAYER_RIGHT_STAND:
                {
                    int i_plX = p_PlayerSprite.i_ScreenX + p_PlayerSprite.i_col_offsetX + p_PlayerSprite.i_col_width;
                    int i_x = (i_plX / CELL_WIDTH) >> 8;
                    int i_y = ((p_PlayerSprite.i_ScreenY) / CELL_HEIGHT) >> 8;
                    int i_cellCoord = i_y * CELLSNUMBER_WIDTH + i_x;

                    int i_cell = ash_Building[i_cellCoord];
                    if (i_cell == TILE_WALL)
                    {
                        p_PlayerSprite.setMainPointXY(i_prevX, p_PlayerSprite.i_mainY);
                    }

                    if (p_PlayerSprite.i_ObjectHitSteps > 0)
                    {
                        i_plX = p_PlayerSprite.i_ScreenX + p_PlayerSprite.i_col_offsetX;
                        i_x = (i_plX / CELL_WIDTH) >> 8;
                        i_y = ((p_PlayerSprite.i_ScreenY) / CELL_HEIGHT) >> 8;
                        i_cellCoord = i_y * CELLSNUMBER_WIDTH + i_x;

                        i_cell = ash_Building[i_cellCoord];
                        if (i_cell == TILE_WALL)
                        {
                            p_PlayerSprite.setMainPointXY(i_prevX, p_PlayerSprite.i_mainY);
                        }
                    }
                }
        ;
                break;
        }

        return false;
    }

    private static final int popStarFromStack()
    {
        if (i_StartStackPointer < 0) return -1;
        int i_res = ai_StarStack[i_StartStackPointer];
        i_StartStackPointer--;
        return i_res;
    }

    private static final void pushStarToStack(int _star)
    {
        i_StartStackPointer++;
        ai_StarStack[i_StartStackPointer] = _star;
    }

    private static final void pushStarToStackAsLast(int _star)
    {
        System.arraycopy(ai_StarStack, 0, ai_StarStack, 1, ai_StarStack.length - 1);
        ai_StarStack[0] = _star;
        i_StartStackPointer++;
    }

    private static final void alignScreenCoordsToPlayer()
    {
        int i8_playerX = p_PlayerSprite.i_ScreenX;
        int i8_playerY = p_PlayerSprite.i_ScreenY;
        int i8_playerW = p_PlayerSprite.i_width;
        int i8_playerH = p_PlayerSprite.i_height;

        int i8_screenW = i_GameAreaWidth << 8;
        int i8_screenH = i_GameAreaHeight << 8;

        // Выравниваем экранс рассчетом что бы игрок был по центру
        i8_ViewAreaX = i8_playerX - ((i8_screenW - i8_playerW) >> 1);
        i8_ViewAreaY = i8_playerY - (i8_screenH - i8_playerH);

        // Проверяем координаты отображаемой области и выравниваем их в пределах экрана
        if (i8_ViewAreaX < 0)
            i8_ViewAreaX = 0;
        else if (i8_ViewAreaX + i8_screenW > (CELL_WIDTH * CELLSNUMBER_WIDTH << 8))
        {
            i8_ViewAreaX = (CELL_WIDTH * CELLSNUMBER_WIDTH << 8) - i8_screenW;
        }

        if (i8_ViewAreaY < 0)
        {
            i8_ViewAreaY = 0;
        }
        else if (i8_ViewAreaY + i8_screenH > (CELL_HEIGHT * CELLSNUMBER_HEIGHT << 8))
        {
            i8_ViewAreaY = (CELL_HEIGHT * CELLSNUMBER_HEIGHT << 8) - i8_screenH;
        }
    }

    /**
     * Количество ячеек, описывающих один спрайт в массиве
     */
    private static final int SPRITEDATALENGTH = 10;

    /**
     * Функция активизирует заданный спрайт, загружая данными из массива спрайтов
     *
     * @param _sprite     активизируемый спрайт
     * @param _actorIndex индекс загружаемых данных
     */
    private static void activateSprite(Sprite _sprite, int _actorIndex)
    {
        _sprite.i_ObjectType = _actorIndex;
        _actorIndex *= SPRITEDATALENGTH;

        int[] ai_sprParameters = ai_SpriteParameters;

        int i8_w = ai_sprParameters[_actorIndex++];
        int i8_h = ai_sprParameters[_actorIndex++];

        int i8_cx = ai_sprParameters[_actorIndex++];
        int i8_cy = ai_sprParameters[_actorIndex++];
        int i8_aw = ai_sprParameters[_actorIndex++];
        int i8_ah = ai_sprParameters[_actorIndex++];

        int i_f = ai_sprParameters[_actorIndex++];
        int i_fd = ai_sprParameters[_actorIndex++];
        int i_mp = ai_sprParameters[_actorIndex++];
        int i_an = ai_sprParameters[_actorIndex++];

        _sprite.setAnimation(i_an, i_mp, i8_w, i8_h, i_f, 0, i_fd);
        _sprite.setCollisionBounds(i8_cx, i8_cy, i8_aw, i8_ah);

        _sprite.lg_SpriteActive = true;
    }

    // Поиск первого неактивного спрайта
    private static final Sprite getFirstInactiveSprite()
    {
        Sprite[] ap_spr = ap_Sprites;
        for (int li = 0; li < MAX_SPRITES; li++)
        {
            Sprite p_spr = ap_spr[li];
            if (p_spr.lg_SpriteActive) continue;
            return p_spr;
        }
        return null;
    }

    private static int[] ai_SpriteParameters = null;
    protected static short[] ash_Building = null;

    private static final Object loadArrayFromResource(Class _class, String _resource) throws Exception
    {
        final int TYPE_BYTE = 0;
        final int TYPE_SHORT = 1;
        final int TYPE_INT = 2;
        final int TYPE_LONG = 3;

        DataInputStream p_dis = new DataInputStream(_class.getResourceAsStream(_resource));
        int i_type = p_dis.readByte();
        int i_length = p_dis.readInt();

        Object p_result = null;

        switch (i_type)
        {
            case TYPE_BYTE:
                {
                    byte[] ab_array = new byte[i_length];
                    p_dis.read(ab_array);
                    p_result = ab_array;
                }
        ;
                break;
            case TYPE_SHORT:
                {
                    short[] ash_array = new short[i_length];
                    for (int li = 0; li < i_length; li++) ash_array[li] = p_dis.readShort();
                    p_result = ash_array;
                }
        ;
                break;
            case TYPE_INT:
                {
                    int[] ai_array = new int[i_length];
                    for (int li = 0; li < i_length; li++) ai_array[li] = p_dis.readInt();
                    p_result = ai_array;
                }
        ;
                break;
            case TYPE_LONG:
                {
                    long[] al_array = new long[i_length];
                    for (int li = 0; li < i_length; li++) al_array[li] = p_dis.readLong();
                    p_result = al_array;
                }
        ;
                break;
        }
        p_dis.close();
        Runtime.getRuntime().gc();
        return p_result;
    }
}
