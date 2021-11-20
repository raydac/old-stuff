
import java.util.Random;
import java.io.*;

/**
 * Игра "Space combat" (Code name)
 *
 * @author И.А.Мазница
 * @version 1.6
 * @since 05-MAY-2005
 *        Сюжет игры
 *        -----------
 *        Игрок управляет турелью на патрульном космическом крейсере. Его задача уничтожить вражеский флот, переместившийся в сектор его ответственности
 *        Флот противника состояит из крейсеров и истребителей, расположенных на этих крейсерах. Крейсера проявляются из гиперпространства и ведут огонь по игроку.
 *        Так же они выпускают истребители, которые перемещаются по экрану и осуществляют обстрел игрока, но с меньшей мощностью чем крейсера. Попадающими в игрока считаются только выстрелы, идущие в нижнюю часть
 *        экрана, все что идет вверх, считаются промахом.Крейсера иногда могут уходить в гиперпространство и появляться в новой точке, с пополненным (!) запасом энергии.
 *        Игра заканчивается в следующих случаях:
 *        Выигрышная ситуация:
 *        1) Игрок уничтожил все крейсера и противник не имеет больше запасных и активных боевых единиц
 *        2) Игрок уничтожил все истребители противника
 *        Проигрышная ситуация:
 *        1) Противник обладает хотя бы одной боевой единицей (крейсер или истребитель)
 *        2) Энергия игрока на нуле или ниже
 */
public class Gamelet
{
    // Кнопки управления
    public static final int BUTTON_NONE = 0;
    public static final int BUTTON_LEFT = 1;
    public static final int BUTTON_RIGHT = 2;
    public static final int BUTTON_UP = 4;
    public static final int BUTTON_DOWN = 8;
    public static final int BUTTON_FIRE = 16;
    public static final int BUTTON_SELECTTARGET = 32;

    //#global PARTS_PRESENTED=true

    // 0x100 = 176x188
    //#if VENDOR=="SIEMENS"
        //#if MODEL=="M55" || MODEL=="S55"
            //$protected static final int SCALE_WIDTH = 0x93;
            //$protected static final int SCALE_HEIGHT = 0x6D;
        //#endif
        //#if MODEL=="CX65"
            //$protected static final int SCALE_WIDTH = 0xC0;
            //$protected static final int SCALE_HEIGHT = 0xD4;
        //#endif
        //#if MODEL=="C65"
            //$protected static final int SCALE_WIDTH = 0xBA;
            //$protected static final int SCALE_HEIGHT = 0xAE;
        //#endif
        //#if MODEL=="M50"
            //$protected static final int SCALE_WIDTH = 0x93;
            //$protected static final int SCALE_HEIGHT = 0x57;
        //#endif
    //#endif

    //#if VENDOR=="SE"
        //#if MODEL=="T610"
            protected static final int SCALE_WIDTH = 0xBA;
            protected static final int SCALE_HEIGHT = 0xAE;
        //#endif
    //#endif

    //#if VENDOR=="MOTOROLA"
        //#if MODEL=="C380"
            //$protected static final int SCALE_WIDTH = 0xBA;
            //$protected static final int SCALE_HEIGHT = 0xAE;
        //#endif
        //#if MODEL=="E398"
            //$protected static final int SCALE_WIDTH = 0x100;
            //$protected static final int SCALE_HEIGHT = 0x100;
        //#endif
    //#endif

    //#if VENDOR=="SAMSUNG"
        //#if MODEL=="X100"
            //$protected static final int SCALE_WIDTH = 0xBA;
            //$protected static final int SCALE_HEIGHT = 0xAE;
        //#endif
            //#if MODEL=="C100"
                //$protected static final int SCALE_WIDTH = 0xBA;
                //$protected static final int SCALE_HEIGHT = 0xAE;
            //#endif
    //#endif

    //#if VENDOR=="LG"
        //#if MODEL=="G1600"
            //$protected static final int SCALE_WIDTH = 0xBA;
            //$protected static final int SCALE_HEIGHT = 0xAE;
        //#endif
    //#endif

    //#if VENDOR=="NOKIA"
        //#if MODEL=="6100"
            //$protected static final int SCALE_WIDTH = 0xBA;
            //$protected static final int SCALE_HEIGHT = 0xAE;
        //#endif
        //#if MODEL=="7650"
            //$protected static final int SCALE_WIDTH = 0x100;
            //$protected static final int SCALE_HEIGHT = 0x100;
        //#endif
            //#if MODEL=="3410"
                //$protected static final int SCALE_WIDTH = 0x93;
                //$protected static final int SCALE_HEIGHT = 0x57;
            //#endif
            //#if MODEL=="3510"
                //$protected static final int SCALE_WIDTH = 0x93;
                //$protected static final int SCALE_HEIGHT = 0x57;
            //#endif
    //#endif

    //------------------Игровые события------------------------
    /**
     * Дальний взрыв
     */
    protected static final int GAMEACTION_LONG_EXPLOSION = 0;

    /**
     * Взрыв на средней дистанции
     */
    protected static final int GAMEACTION_MIDDLE_EXPLOSION = 1;

    /**
     * Взрыв на ближней дистанции
     */
    protected static final int GAMEACTION_NEAR_EXPLOSION = 2;

    /**
     * Крейсер появляется из гиперпространства
     */
    protected static final int GAMEACTION_CRUISER_FROM_HYPERSPACE = 3;

    /**
     * Крейсер уходит в гиперпространство
     */
    protected static final int GAMEACTION_CRUISER_INTO_HYPERSPACE = 4;

    /**
     * Крейсер стреляет
     */
    protected static final int GAMEACTION_CRUISER_FIRING = 5;

    /**
     * Крейсер уничтожен
     */
    protected static final int GAMEACTION_CRUISER_DESTROYED = 6;

    /**
     * Истребитель стреляет с ближней дистанции
     */
    protected static final int GAMEACTION_FIGHTER_FIRING_NEAR = 7;

    /**
     * Истребитель стреляет со средней дистанции
     */
    protected static final int GAMEACTION_FIGHTER_FIRING_MIDDLE = 8;

    /**
     * Истребитель вылетает с крейсера
     */
    protected static final int GAMEACTION_FIGHTER_GENERATION = 9;

    /**
     * Истребитель атакует
     */
    protected static final int GAMEACTION_FIGHTER_ATTACK = 10;

    /**
     * Игрок стреляет
     */
    protected static final int GAMEACTION_PLAYER_FIRE = 11;

    /**
     * Попадание в игрока
     */
    protected static final int GAMEACTION_ENEMY_HIT = 12;

    /**
     * Цель, которая в данный момент отслеживается игроком
     */
    public static Sprite p_TargetSprite;

    /**
     * Переменная содержит true если а качестве отслеживаемой цели выступает крейсер и false если истребитель
     */
    public static boolean lg_IsTargeCruiser;

    /**
     * Массив параметров игровых спрайтов
     */
    private static int[] ai_SpriteParameters;

    /**
     * Максимальное количество игровых тиков до генерации крейсера
     */
    private static final int MAX_TICKS_TILL_CRUISER_GENERATION = 120;

    /**
     * Ширина виртуального сектора стрельбы
     */
    public static final int I8_VIRTUAL_SECTOR_WIDTH = ((int) ((0x20000 * SCALE_WIDTH) + 0x7F) >> 8);

    /**
     * Высота виртуального сектора стрельбы
     */
    public static final int I8_VIRTUAL_SECTOR_HEIGHT = ((int) ((0xBC00 * SCALE_HEIGHT) + 0x7F) >> 8);

    /**
     * Левая граница ограничения перемещения прицела
     */
    public static final int I8_LEFT_SIGHT_BORDER = 0;

    /**
     * Правая граница ограничения перемещения прицела
     */
    public static final int I8_RIGHT_SIGHT_BORDER = I8_VIRTUAL_SECTOR_WIDTH;

    /**
     * Верхняя граница ограничения перемещения прицела
     */
    public static final int I8_TOP_SIGHT_BORDER = 0;

    /**
     * Нижняя граница ограничения перемещения прицела
     */
    public static final int I8_BOTTOM_SIGHT_BORDER = I8_VIRTUAL_SECTOR_HEIGHT - 0x200;

    /**
     * Максимальная вертикальная скорость прицела
     */
    private static final int I8_VERTICAL_SIGHT_SPEED_MAX = ((int) ((0x800 * SCALE_HEIGHT) + 0x7F) >> 8);
    /**
     * Минимальная вертикальная скорость прицела
     */
    private static final int I8_VERTICAL_SIGHT_SPEED_MIN = ((int) ((0x500 * SCALE_HEIGHT) + 0x7F) >> 8);
    /**
     * Максимальная горизонтальная скорость прицела
     */
    private static final int I8_HORIZONTAL_SIGHT_SPEED_MAX = ((int) ((0x800 * SCALE_WIDTH) + 0x7F) >> 8);
    /**
     * Минимальная горизонтальная скорость прицела
     */
    private static final int I8_HORIZONTAL_SIGHT_SPEED_MIN = ((int) ((0x500 * SCALE_WIDTH) + 0x7F) >> 8);

    /**
     * Координата X первой макроячейки в которой будут генерироваться крейсера противника
     */
    public static final int RECTANGLE_GENERATEAREA_X = 2;

    /**
     * Координата Y первой макроячейки в которой будут генерироваться крейсера противника
     */
    public static final int RECTANGLE_GENERATEAREA_Y = 1;

    /**
     * Ширина прямоугольной области макроячеек, в которой будут гененироваться крейсера противника
     */
    public static final int RECTANGLE_GENERATEAREA_WIDTH = 7;

    /**
     * Высота прямоугольной области макроячеек, в которой будут гененироваться крейсера противника
     */
    public static final int RECTANGLE_GENERATEAREA_HEIGHT = 4;

    /**
     * Задержка переключения перемещения прицела с минимальной скорости на максимальную, в игровых тиках
     */
    private static final int SPEED_SIGHT_CHANGE_DELAY = 5;

    /**
     * Счетчик перемещения игрока по вертикали
     */
    private static int i_PlayerVertSpeedDelayCounter;

    /**
     * Счетчик перемещений игрока по горизонтали
     */
    private static int i_PlayerHorzSpeedDelayCounter;

    /**
     * Счетчик выстрелов произведенных выстрелов игрока
     */
    private static int i_PlayerShootsCounter;

    /**
     * Счетчик попаданий игрока
     */
    private static int i_PlayerHitCounter;

    /**
     * Индекс выбранного пути для последнего сгенерированного истребителя
     */
    private static int i_lastGeneatedFighterIndex;


    /**
     * количество вражеских крейсеров на простом уровне
     */
    public static final int LEVEL_EASY_CRUISERS_NUM = 5;

    /**
     * частота смены крейсерами позиций на уровне
     */
    public static final int LEVEL_EASY_CRUISERS_POSITION_FREQ = 150;

    /**
     * частота генерации истребителей
     */
    public static final int LEVEL_EASY_FIGHTERS_FREQ = 30;

    /**
     * частота генерации выстрелов крейсеров
     */
    public static final int LEVEL_EASY_CRUISER_FIRE_FREQ = 28;

    /**
     * частота генерации выстрелов истребителей
     */
    public static final int LEVEL_EASY_FIGHTER_FIRE_FREQ = 20;

    /**
     * задержка между игровыми шагами для простого уровня
     */
    private static final int LEVEL_EASY_TIMEDELAY = 100;


    /**
     * частота смены крейсерами позиций на уровне
     */
    public static final int LEVEL_NORMAL_CRUISERS_POSITION_FREQ = 150;

    /**
     * количество вражеских крейсеров
     */
    public static final int LEVEL_NORMAL_CRUISERS_NUM = 6;

    /**
     * частота генерации истребителей
     */
    public static final int LEVEL_NORMAL_FIGHTERS_FREQ = 18;

    /**
     * частота генерации выстрелов крейсеров
     */
    public static final int LEVEL_NORMAL_CRUISER_FIRE_FREQ = 25;

    /**
     * частота генерации выстрелов истребителей
     */
    public static final int LEVEL_NORMAL_FIGHTER_FIRE_FREQ = 25;

    /**
     * задержка между игровыми шагами для обычного уровня
     */
    private static final int LEVEL_NORMAL_TIMEDELAY = 100;

    /**
     * количество вражеских крейсеров
     */
    public static final int LEVEL_HARD_CRUISERS_NUM = 8;

    /**
     * частота смены крейсерами позиций на уровне
     */
    public static final int LEVEL_HARD_CRUISERS_POSITION_FREQ = 150;   // 150

    /**
     * частота генерации истребителей
     */
    public static final int LEVEL_HARD_FIGHTERS_FREQ = 12;

    /**
     * частота генерации выстрелов крейсеров
     */
    public static final int LEVEL_HARD_CRUISER_FIRE_FREQ = 20;

    /**
     * частота генерации выстрелов истребителей
     */
    public static final int LEVEL_HARD_FIGHTER_FIRE_FREQ = 20;

    /**
     * задержка между игровыми шагами для сложного уровня
     */
    private static final int LEVEL_HARD_TIMEDELAY = 100;

    /**
     * Уровень защиты игрока
     */
    public static int i_playerHealth;

    /**
     * Количество внутриигровых, набранных игроком очков
     */
    private static int i_playerGameScores;

    /**
     * Счетчик количества выпущенных игроком снарядов
     */
    public static int i_playerShellsNumber;

    /**
     * Счетчик количества активных истребителей
     */
    private static int i_fighterNumber;

    /**
     * Счетчик количества активных снарядов противника
     */
    private static int i_enemyShellsNumber;

    /**
     * Счетчик активных крейсеров на уровне
     */
    protected static int i_activeCruisersNumber;

    /**
     * Счетчик пассивных крейсеров на уровне
     */
    protected static int i_passiveCruisersNumber;

    /**
     * Частота генерации выстрелов крейсеров
     */
    private static int i_cruisersFiringFreq;

    /**
     * Частота генерации выстрелов истребителей
     */
    private static int i_fightersFiringFreq;

    /**
     * Частота генерации истребителей
     */
    private static int i_fightersGenerationFreq;

    /**
     * Частота смены позиции крейсерами
     */
    private static int i_cruiserChangeLoactionFreq;

    /**
     * Частота генерации крейсеров
     */
    private static final int GENERATE_CRUISER_FREQ = 100;

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
    public static final int STATE_OVER = 3;

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
    public static int I_TIMEDELAY;

    /**
     * Переменная содержит количество игровых попыток играющего
     */
    public static int i_PlayerAttemptions;

    /**
     * Переменная содержит текущий показатель счетчика до генерации нового крейсера
     */
    private static int i_NewCruiserDelayCounter;

    /**
         * Функция отвечающая за инициализацию блока, до её вызова любые операции с блоком запрещены. Переводит блок в состояние INITED..
         *
         * @return true если инициализация прошла успешно, иначе false.
         */
    public static final boolean init(Class _class)
    {
        if (i_GameState != STATE_UNKNON) return false;
        i_GameAreaHeight = -1;
        i_GameAreaWidth = -1;

        //------------Вставьте свой код здесь--------------------
        try
        {
            ai_SpriteParameters = (int[]) loadArray(_class,"/sprites.bin");
            ash_Paths = (short[])loadArray(_class,"/paths.bin");
        }
        catch (Exception e)
        {
            return false;
        }

        ap_CruiserSprites = new Sprite[MAX_CRUISERS_NUMBER];
        ai_CruiserMacrocellsIndexes = new int[MAX_CRUISERS_NUMBER];
        ap_EnemyShellsSprites = new Sprite[MAX_ENEMY_SHELLS_NUMBER];
        ap_FighterSprites = new Sprite[MAX_FIGHTERS_NUMBER];
        ap_PlayerShellsSprites = new Sprite[MAX_PLAYER_SHELLS_NUMBER];
        ap_EnemyShellsPaths = new PathController[MAX_ENEMY_SHELLS_NUMBER];
        ap_PlayerShellsPaths = new PathController[MAX_PLAYER_SHELLS_NUMBER];
        ap_FighterPaths = new PathController[MAX_FIGHTERS_NUMBER];
        ap_PlayerBarrels = new Sprite[PLAYER_BARRELS_NUMBER];
        ap_PlayerBarrelsPaths = new PathController[PLAYER_BARRELS_NUMBER];

        ap_ExplosionSprites = new Sprite[MAX_EXPLOSIONS_NUMBER];
        ap_ExplosionsPaths = new PathController[MAX_EXPLOSIONS_NUMBER];

        for (int li = 0; li < MAX_CRUISERS_NUMBER; li++)
        {
            ap_CruiserSprites[li] = new Sprite(li);
        }
        for (int li = 0; li < MAX_EXPLOSIONS_NUMBER; li++)
        {
            ap_ExplosionSprites[li] = new Sprite(li);
            ap_ExplosionsPaths[li] = new PathController();
        }
        for (int li = 0; li < MAX_FIGHTERS_NUMBER; li++)
        {
            ap_FighterSprites[li] = new Sprite(li);
            ap_FighterPaths[li] = new PathController();
        }
        for (int li = 0; li < MAX_ENEMY_SHELLS_NUMBER; li++)
        {
            ap_EnemyShellsSprites[li] = new Sprite(li);
            ap_EnemyShellsPaths[li] = new PathController();
        }
        for (int li = 0; li < MAX_PLAYER_SHELLS_NUMBER; li++)
        {
            ap_PlayerShellsSprites[li] = new Sprite(li);
            ap_PlayerShellsPaths[li] = new PathController();
        }
        for (int li = 0; li < PLAYER_BARRELS_NUMBER; li++)
        {
            ap_PlayerBarrels[li] = new Sprite(li);
            ap_PlayerBarrelsPaths[li] = new PathController();
        }

        p_PlayerSight = new Sprite(0);

        // Создаем таблицу макроячеек и инициализируем её
        int i_cw = I8_VIRTUAL_SECTOR_WIDTH / MACROCELL_WIDTH;
        int i_ch = I8_VIRTUAL_SECTOR_HEIGHT / MACROCELL_HEIGHT;

        alg_macroCells = new boolean[i_cw * i_ch];
        i_macrocellsArrayWidth = i_cw;
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
        ap_CruiserSprites = null;
        ai_CruiserMacrocellsIndexes = null;
        ap_EnemyShellsPaths = null;
        ap_EnemyShellsSprites = null;
        ap_FighterPaths = null;
        ap_FighterSprites = null;
        ap_PlayerShellsPaths = null;
        ap_PlayerShellsSprites = null;
        ap_PlayerBarrels = null;
        ap_PlayerBarrelsPaths = null;
        p_PlayerSight = null;
        ap_ExplosionsPaths = null;
        ap_ExplosionSprites = null;
        //--------------------------------------------------

        i_GameAreaHeight = -1;
        i_GameAreaWidth = -1;

        setState(STATE_UNKNON);
        Runtime.getRuntime().gc();
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
    protected static final int getRandomInt(int _limit)
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
        i_GameAreaHeight = _gameAreaHeight;
        i_GameAreaWidth = _gameAreaWidth;
        i_GameLevel = _gameLevel;

        //------------Вставьте свой код здесь--------------------
        switch (_gameLevel)
        {
            case GAMELEVEL_EASY:
                {
                    I_TIMEDELAY = LEVEL_EASY_TIMEDELAY;

                    i_passiveCruisersNumber = LEVEL_EASY_CRUISERS_NUM;
                    i_fightersFiringFreq = LEVEL_EASY_FIGHTER_FIRE_FREQ;
                    i_cruisersFiringFreq = LEVEL_EASY_FIGHTER_FIRE_FREQ;
                    i_cruiserChangeLoactionFreq = LEVEL_EASY_CRUISERS_POSITION_FREQ;
                    i_fightersGenerationFreq = LEVEL_EASY_FIGHTERS_FREQ;
                }
        ;
                break;
            case GAMELEVEL_NORMAL:
                {
                    I_TIMEDELAY = LEVEL_NORMAL_TIMEDELAY;

                    i_passiveCruisersNumber = LEVEL_NORMAL_CRUISERS_NUM;
                    i_fightersFiringFreq = LEVEL_NORMAL_FIGHTER_FIRE_FREQ;
                    i_cruisersFiringFreq = LEVEL_NORMAL_FIGHTER_FIRE_FREQ;
                    i_cruiserChangeLoactionFreq = LEVEL_NORMAL_CRUISERS_POSITION_FREQ;
                    i_fightersGenerationFreq = LEVEL_NORMAL_FIGHTERS_FREQ;
                }
        ;
                break;
            case GAMELEVEL_HARD:
                {
                    I_TIMEDELAY = LEVEL_HARD_TIMEDELAY;

                    i_passiveCruisersNumber = LEVEL_HARD_CRUISERS_NUM;
                    i_fightersFiringFreq = LEVEL_HARD_FIGHTER_FIRE_FREQ;
                    i_cruisersFiringFreq = LEVEL_HARD_FIGHTER_FIRE_FREQ;
                    i_cruiserChangeLoactionFreq = LEVEL_HARD_CRUISERS_POSITION_FREQ;
                    i_fightersGenerationFreq = LEVEL_HARD_FIGHTERS_FREQ;
                }
        ;
                break;
        }

        i_playerShellsNumber = MAX_PLAYER_SHELLS_NUMBER;
        i_playerHealth = PLAYER_INIT_POWER;
        i_PlayerHorzSpeedDelayCounter = SPEED_SIGHT_CHANGE_DELAY;
        i_PlayerVertSpeedDelayCounter = SPEED_SIGHT_CHANGE_DELAY;

        i_activeCruisersNumber = 0;

        i_NewCruiserDelayCounter = MAX_TICKS_TILL_CRUISER_GENERATION;

        p_TargetSprite = null;

        for (int li = 0; li < MAX_CRUISERS_NUMBER; li++) ap_CruiserSprites[li].lg_SpriteActive = false;
        for (int li = 0; li < MAX_ENEMY_SHELLS_NUMBER; li++) ap_EnemyShellsSprites[li].lg_SpriteActive = false;
        for (int li = 0; li < MAX_EXPLOSIONS_NUMBER; li++) ap_ExplosionSprites[li].lg_SpriteActive = false;
        for (int li = 0; li < MAX_FIGHTERS_NUMBER; li++) ap_FighterSprites[li].lg_SpriteActive = false;
        for (int li = 0; li < MAX_PLAYER_SHELLS_NUMBER; li++) ap_PlayerShellsSprites[li].lg_SpriteActive = false;


        activateSprite(ap_PlayerBarrels[0], SPRITE_PLAYER_BARREL_LEFT);
        activateSprite(ap_PlayerBarrels[1], SPRITE_PLAYER_BARREL_BOTTOM);
        activateSprite(ap_PlayerBarrels[2], SPRITE_PLAYER_BARREL_RIGHT);

        PathController.SCALE_WIDTH = SCALE_WIDTH;
        PathController.SCALE_HEIGHT = SCALE_HEIGHT;

        activateSprite(p_PlayerSight, SPRITE_PLAYER_SIGN);

        for (int li = 0; li < alg_macroCells.length; li++) alg_macroCells[li] = false;

        i_playerShellsNumber = MAX_PLAYER_SHELLS_NUMBER;
        i_PlayerShootsCounter = 0;
        i_PlayerHitCounter = 0;
        i_playerGameScores = 0;
        i_lastGeneatedFighterIndex = -1;

        i_fighterNumber = 0;
        i_enemyShellsNumber = 0;

        initViewRectPosition();

        ap_PlayerBarrelsPaths[0].deactivate();
        ap_PlayerBarrelsPaths[1].deactivate();
        ap_PlayerBarrelsPaths[2].deactivate();

        //--------------------------------------------------
        initPlayerForGame(true);
        setState(STATE_STARTED);
        return true;
    }

    /**
     * Деинициализация гровой сессии
     */
    public static final void releaseGame()
    {
        if (i_GameState == STATE_INITED || i_GameState == STATE_UNKNON) return;
        i_GameAreaHeight = -1;
        i_GameAreaWidth = -1;

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
        int i_scores = calculateScores(i_playerGameScores);
        //--------------------------------------------------
        return i_scores;
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
        DataInputStream p_inputStream = new DataInputStream(p_arrayInputStream);
        int i_gameLevel = p_inputStream.readUnsignedByte();
        int i_gameScreenWidth = p_inputStream.readUnsignedShort();
        int i_gameScreenHeight = p_inputStream.readUnsignedShort();
        if (!initNewGame(i_gameScreenWidth, i_gameScreenHeight, i_gameLevel)) throw new Exception();
        i_PlayerAttemptions = p_inputStream.readInt();
        //------------Вставьте свой код здесь--------------------
        //ap_CruiserSprites = null;
        for (int li = 0; li < MAX_CRUISERS_NUMBER; li++)
        {
            int i_ObjectType = p_inputStream.readUnsignedByte();
            activateSprite(ap_CruiserSprites[li], i_ObjectType);
            ap_CruiserSprites[li].readSpriteFromStream(p_inputStream);
        }
        //ai_CruiserMacrocellsIndexes = null;
        for (int li = 0; li < MAX_CRUISERS_NUMBER; li++) ai_CruiserMacrocellsIndexes[li] = p_inputStream.readInt();

        //ap_EnemyShellsSprites = null;
        for (int li = 0; li < MAX_ENEMY_SHELLS_NUMBER; li++)
        {
            int i_ObjectType = p_inputStream.readUnsignedByte();
            activateSprite(ap_EnemyShellsSprites[li], i_ObjectType);
            ap_EnemyShellsSprites[li].readSpriteFromStream(p_inputStream);
        }
        //ap_EnemyShellsPaths = null;
        for (int li = 0; li < MAX_ENEMY_SHELLS_NUMBER; li++)
            ap_EnemyShellsPaths[li].readPathFromStream(p_inputStream, ap_EnemyShellsSprites, ash_Paths);

        //ap_FighterSprites = null;
        for (int li = 0; li < MAX_FIGHTERS_NUMBER; li++)
        {
            int i_ObjectType = p_inputStream.readUnsignedByte();
            activateSprite(ap_FighterSprites[li], i_ObjectType);
            ap_FighterSprites[li].readSpriteFromStream(p_inputStream);
        }
        //ap_FighterPaths = null;
        for (int li = 0; li < MAX_FIGHTERS_NUMBER; li++)
            ap_FighterPaths[li].readPathFromStream(p_inputStream, ap_FighterSprites, ash_Paths);

        //ap_PlayerShellsSprites = null;
        for (int li = 0; li < MAX_PLAYER_SHELLS_NUMBER; li++)
        {
            int i_ObjectType = p_inputStream.readUnsignedByte();
            activateSprite(ap_PlayerShellsSprites[li], i_ObjectType);
            ap_PlayerShellsSprites[li].readSpriteFromStream(p_inputStream);
        }

        //ap_PlayerShellsPaths = null;
        for (int li = 0; li < MAX_PLAYER_SHELLS_NUMBER; li++)
            ap_PlayerShellsPaths[li].readPathFromStream(p_inputStream, ap_PlayerShellsSprites, ash_Paths);

        //ap_PlayerBarrels = null;
        for (int li = 0; li < PLAYER_BARRELS_NUMBER; li++)
        {
            int i_ObjectType = p_inputStream.readUnsignedByte();
            activateSprite(ap_PlayerBarrels[li], i_ObjectType);
            ap_PlayerBarrels[li].readSpriteFromStream(p_inputStream);
        }

        //ap_PlayerBarrelsPaths = null;
        for (int li = 0; li < PLAYER_BARRELS_NUMBER; li++)
            ap_PlayerBarrelsPaths[li].readPathFromStream(p_inputStream, ap_PlayerBarrels, ash_Paths);

        //p_PlayerSight = null;
        activateSprite(p_PlayerSight, SPRITE_PLAYER_SIGN);
        p_PlayerSight.readSpriteFromStream(p_inputStream);

        //i8_viewRectX = 0;
        i8_viewRectX = p_inputStream.readInt();

        //i_activeCruisersNumber=0;
        i_activeCruisersNumber = p_inputStream.readInt();

        //i_enemyShellsNumber=0;
        i_enemyShellsNumber = p_inputStream.readInt();

        //i_fighterNumber=0;
        i_fighterNumber = p_inputStream.readInt();

        //i_enemyShellsNumber=0;
        i_enemyShellsNumber = p_inputStream.readInt();

        //i_passiveCruisersNumber=0;
        i_passiveCruisersNumber = p_inputStream.readInt();

        //i_playerGameScores=0;
        i_playerGameScores = p_inputStream.readInt();

        //i_playerHealth=0;
        i_playerHealth = p_inputStream.readInt();

        //i_PlayerHitCounter=0;
        i_PlayerHitCounter = p_inputStream.readInt();

        //i_PlayerHorzSpeedDelayCounter=0;
        i_PlayerHorzSpeedDelayCounter = p_inputStream.readInt();

        //i_playerShellsNumber=0;
        i_playerShellsNumber = p_inputStream.readInt();

        //i_PlayerShootsCounter=0;
        i_PlayerShootsCounter = p_inputStream.readInt();

        int i_target = p_inputStream.readInt();
        if (i_target == -1)
        {
            p_TargetSprite = null;
        }
        else
        {
            int i_id = i_target & 0x7FFFFFFF;
            if ((i_target & 0x80000000) == 0)
            {
                p_TargetSprite = ap_CruiserSprites[i_id];
                lg_IsTargeCruiser = true;
            }
            else
            {
                p_TargetSprite = ap_FighterSprites[i_id];
                lg_IsTargeCruiser = false;
            }
        }

        i_NewCruiserDelayCounter = p_inputStream.readUnsignedShort();
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
        if (i_GameState != STATE_STARTED && i_PlayerState != PLAYER_PLAYING) throw new Exception();
        Runtime.getRuntime().gc();
        ByteArrayOutputStream p_arrayOutputStream = new ByteArrayOutputStream(getGameStateDataBlockSize());
        DataOutputStream p_outputStream = new DataOutputStream(p_arrayOutputStream);
        p_outputStream.writeByte(i_GameLevel);
        p_outputStream.writeShort(i_GameAreaWidth);
        p_outputStream.writeShort(i_GameAreaHeight);
        p_outputStream.writeInt(i_PlayerAttemptions);
        //------------Вставьте свой код здесь--------------------
        //ap_CruiserSprites = null;
        for (int li = 0; li < MAX_CRUISERS_NUMBER; li++)
        {
            p_outputStream.writeByte(ap_CruiserSprites[li].i_ObjectType);
            ap_CruiserSprites[li].writeSpriteToStream(p_outputStream);
        }

        //ai_CruiserMacrocellsIndexes = null;
        for (int li = 0; li < MAX_CRUISERS_NUMBER; li++) p_outputStream.writeInt(ai_CruiserMacrocellsIndexes[li]);

        //ap_EnemyShellsSprites = null;
        for (int li = 0; li < MAX_ENEMY_SHELLS_NUMBER; li++)
        {
            p_outputStream.writeByte(ap_EnemyShellsSprites[li].i_ObjectType);
            ap_EnemyShellsSprites[li].writeSpriteToStream(p_outputStream);
        }

        //ap_EnemyShellsPaths = null;
        for (int li = 0; li < MAX_ENEMY_SHELLS_NUMBER; li++) ap_EnemyShellsPaths[li].writePathToStream(p_outputStream);

        //ap_FighterSprites = null;
        for (int li = 0; li < MAX_FIGHTERS_NUMBER; li++)
        {
            p_outputStream.writeByte(ap_FighterSprites[li].i_ObjectType);
            ap_FighterSprites[li].writeSpriteToStream(p_outputStream);
        }

        //ap_FighterPaths = null;
        for (int li = 0; li < MAX_FIGHTERS_NUMBER; li++) ap_FighterPaths[li].writePathToStream(p_outputStream);

        //ap_PlayerShellsSprites = null;
        for (int li = 0; li < MAX_PLAYER_SHELLS_NUMBER; li++)
        {
            p_outputStream.writeByte(ap_PlayerShellsSprites[li].i_ObjectType);
            ap_PlayerShellsSprites[li].writeSpriteToStream(p_outputStream);
        }

        //ap_PlayerShellsPaths = null;
        for (int li = 0; li < MAX_PLAYER_SHELLS_NUMBER; li++) ap_PlayerShellsPaths[li].writePathToStream(p_outputStream);

        //ap_PlayerBarrels = null;
        for (int li = 0; li < PLAYER_BARRELS_NUMBER; li++)
        {
            p_outputStream.writeByte(ap_PlayerBarrels[li].i_ObjectType);
            ap_PlayerBarrels[li].writeSpriteToStream(p_outputStream);
        }

        //ap_PlayerBarrelsPaths = null;
        for (int li = 0; li < PLAYER_BARRELS_NUMBER; li++) ap_PlayerBarrelsPaths[li].writePathToStream(p_outputStream);

        //p_PlayerSight = null;
        p_PlayerSight.writeSpriteToStream(p_outputStream);

        //i8_viewRectX = 0;
        p_outputStream.writeInt(i8_viewRectX);

        //i_activeCruisersNumber=0;
        p_outputStream.writeInt(i_activeCruisersNumber);

        //i_enemyShellsNumber=0;
        p_outputStream.writeInt(i_enemyShellsNumber);

        //i_fighterNumber=0;
        p_outputStream.writeInt(i_fighterNumber);

        //i_enemyShellsNumber=0;
        p_outputStream.writeInt(i_enemyShellsNumber);

        //i_passiveCruisersNumber=0;
        p_outputStream.writeInt(i_passiveCruisersNumber);

        //i_playerGameScores=0;
        p_outputStream.writeInt(i_playerGameScores);

        //i_playerHealth=0;
        p_outputStream.writeInt(i_playerHealth);

        //i_PlayerHitCounter=0;
        p_outputStream.writeInt(i_PlayerHitCounter);

        //i_PlayerHorzSpeedDelayCounter=0;
        p_outputStream.writeInt(i_PlayerHorzSpeedDelayCounter);

        //i_playerShellsNumber=0;
        p_outputStream.writeInt(i_playerShellsNumber);

        //i_PlayerShootsCounter=0;
        p_outputStream.writeInt(i_PlayerShootsCounter);

        int i_target = -1;
        if (p_TargetSprite != null)
        {
            if (lg_IsTargeCruiser)
            {
                i_target = p_TargetSprite.i_spriteID;
            }
            else
            {
                i_target = 0x80000000 | p_TargetSprite.i_spriteID;
            }
        }
        p_outputStream.writeInt(i_target);

        p_outputStream.writeShort(i_NewCruiserDelayCounter);
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
        final int MINIMUM_SIZE = 9;

        int i_length = 0;

        //ap_CruiserSprites = null;
        i_length = (Sprite.DATASIZE_BYTES + 1) * MAX_CRUISERS_NUMBER;

        //ai_CruiserMacrocellsIndexes = null;
        i_length += 4 * MAX_CRUISERS_NUMBER;

        //ap_EnemyShellsPaths = null;
        i_length += PathController.DATASIZE_BYTES * MAX_ENEMY_SHELLS_NUMBER;

        //ap_EnemyShellsSprites = null;
        i_length += (Sprite.DATASIZE_BYTES + 1) * MAX_ENEMY_SHELLS_NUMBER;

        //ap_FighterPaths = null;
        i_length += PathController.DATASIZE_BYTES * MAX_FIGHTERS_NUMBER;

        //ap_FighterSprites = null;
        i_length += (Sprite.DATASIZE_BYTES + 1) * MAX_FIGHTERS_NUMBER;

        //ap_PlayerShellsPaths = null;
        i_length += PathController.DATASIZE_BYTES * MAX_PLAYER_SHELLS_NUMBER;

        //ap_PlayerShellsSprites = null;
        i_length += (Sprite.DATASIZE_BYTES + 1) * MAX_PLAYER_SHELLS_NUMBER;

        //ap_PlayerBarrels = null;
        i_length += (Sprite.DATASIZE_BYTES + 1) * PLAYER_BARRELS_NUMBER;

        //ap_PlayerBarrelsPaths = null;
        i_length += PathController.DATASIZE_BYTES * PLAYER_BARRELS_NUMBER;

        //p_PlayerSight = null;
        i_length += Sprite.DATASIZE_BYTES;

        //i8_viewRectX = 0;
        //i_activeCruisersNumber=0;
        //i_enemyShellsNumber=0;
        //i_fighterNumber=0;
        //i_enemyShellsNumber=0;
        //i_passiveCruisersNumber=0;
        //i_playerGameScores=0;
        //i_playerHealth=0;
        //i_PlayerHitCounter=0;
        //i_PlayerHorzSpeedDelayCounter=0;
        //i_playerShellsNumber=0;
        //i_PlayerShootsCounter=0;
        i_length += 4 * 13;

        i_length += 2;

        return MINIMUM_SIZE + i_length;
    }

    /**
         * Возвращает текстовый идентификатор игры
         *
         * @return строка, идентифицирующая игру.
         */
    public static final String getID()
    {
        return "SPCCM04";
    }


    /**
     * Функция вычисляет количество очков для данного уровня сложности, желательно данную функцию делать inline
     *
     * @param _scores исходное количество очков
     * @return пересчитанное количество очков в зависимости от уровня сложности игроыого процесса
     */
    private static int calculateScores(int _scores)
    {
        //TODO можно сделать инлайн
        return _scores * (i_GameLevel + 1);
    }


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

        int i8_w = ai_sprParameters[_actorIndex++] * SCALE_WIDTH;
        int i8_h = ai_sprParameters[_actorIndex++] * SCALE_HEIGHT;

        int i8_cx = ai_sprParameters[_actorIndex++] * SCALE_WIDTH;
        int i8_cy = ai_sprParameters[_actorIndex++] * SCALE_HEIGHT;
        int i8_aw = ai_sprParameters[_actorIndex++] * SCALE_WIDTH;
        int i8_ah = ai_sprParameters[_actorIndex++] * SCALE_HEIGHT;

        int i_f = ai_sprParameters[_actorIndex++];
        int i_fd = ai_sprParameters[_actorIndex++];
        int i_mp = ai_sprParameters[_actorIndex++];
        int i_an = ai_sprParameters[_actorIndex++];

        _sprite.setAnimation(i_an, i_mp, i8_w, i8_h, i_f, 0, i_fd);
        _sprite.setCollisionBounds(i8_cx, i8_cy, i8_aw, i8_ah);

        _sprite.lg_SpriteActive = true;
    }

    /**
     * Поиск первого неактивного спрайта
     *
     * @return null если нет или объект если найден
     */
    private static Sprite getFirstInactiveSprite(Sprite[] _array)
    {
        int i_array_len = _array.length;
        for (int li = 0; li < i_array_len; li++)
        {
            if (!_array[li].lg_SpriteActive) return _array[li];
        }
        return null;
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
        if (i_NewCruiserDelayCounter > 0 && i_activeCruisersNumber == 0) i_NewCruiserDelayCounter--;

        processPlayerShells();
        processEnemyShells();

        processSight(_keyStateFlags);

        boolean lg_fighterPresented = processFighters();

        processCruisers();
        boolean lg_explosions = processExplosionParts();

        if (i_passiveCruisersNumber > 0 && (getRandomInt(GENERATE_CRUISER_FREQ) == (GENERATE_CRUISER_FREQ >> 1) || i_NewCruiserDelayCounter == 0))
        {
            i_NewCruiserDelayCounter = MAX_TICKS_TILL_CRUISER_GENERATION;
            generateNewCruiser();
        }

        // Проверка игры на окончание
        if (i_activeCruisersNumber == 0 && i_passiveCruisersNumber == 0 && !(lg_fighterPresented || lg_explosions))
        {
            // Игрок выиграл
            setState(STATE_OVER);
            i_PlayerState = PLAYER_WIN;
        }
        else if (i_playerHealth <= 0 && !lg_explosions)
        {
            i_playerHealth = 0;
            // Игрок проиграл
            setState(STATE_OVER);
            i_PlayerState = PLAYER_LOST;
        }
        else
        {
            if ((_keyStateFlags & BUTTON_SELECTTARGET) != 0)
            {
                selectTarget();
            }
        }
        //--------------------------------------------------
        return i_GameState;
    }

    /**
     * Инициализация позиции прямоугольной области, видимой игроку, так же тут инициализируются прицел и орудия
     */
    private static void initViewRectPosition()
    {
        i8_viewRectX = (I8_VIRTUAL_SECTOR_WIDTH - VIEW_RECT_WIDTH) >> 1;

        // Вычисляем новое расположение
        p_PlayerSight.setMainPointXY(i8_viewRectX + (VIEW_RECT_WIDTH >> 1), I8_VIRTUAL_SECTOR_HEIGHT >> 1);

        // Расстановка и обработка хода стволов
        // Левый
        ap_PlayerBarrels[0].setMainPointXY(i8_viewRectX, (I8_VIRTUAL_SECTOR_HEIGHT >> 1));
        // Нижний
        ap_PlayerBarrels[1].setMainPointXY(i8_viewRectX + (VIEW_RECT_WIDTH >> 1), I8_VIRTUAL_SECTOR_HEIGHT);
        // Правый
        ap_PlayerBarrels[2].setMainPointXY(i8_viewRectX + VIEW_RECT_WIDTH, (I8_VIRTUAL_SECTOR_HEIGHT >> 1));
    }

    /**
     * Обработка прицела игрока
     *
     * @param _buttons значение выставленных кнопок
     */
    private static void processSight(int _buttons)
    {
        p_PlayerSight.processAnimation();

        int i8_curViewX = i8_viewRectX;
        int i8_curSightY = p_PlayerSight.i_mainY;
        //int i8_sightWidth = p_PlayerSight.i_width;
        int i8_sightHeight = p_PlayerSight.i_height;

        int i_deltaX = 0;
        int i_deltaY = 0;

        if (_buttons == BUTTON_NONE)
        {
            i_PlayerVertSpeedDelayCounter = SPEED_SIGHT_CHANGE_DELAY;
            i_PlayerHorzSpeedDelayCounter = SPEED_SIGHT_CHANGE_DELAY;
        }
        if ((_buttons & BUTTON_DOWN) != 0)
        {
            if (i_PlayerVertSpeedDelayCounter == 0)
            {
                i_deltaY = I8_VERTICAL_SIGHT_SPEED_MAX;
            }
            else
            {
                i_PlayerVertSpeedDelayCounter--;
                i_deltaY = I8_VERTICAL_SIGHT_SPEED_MIN;
            }
        }
        if ((_buttons & BUTTON_UP) != 0)
        {
            if (i_PlayerVertSpeedDelayCounter == 0)
            {
                i_deltaY = 0 - I8_VERTICAL_SIGHT_SPEED_MAX;
            }
            else
            {
                i_PlayerVertSpeedDelayCounter--;
                i_deltaY = 0 - I8_VERTICAL_SIGHT_SPEED_MIN;
            }
        }
        if ((_buttons & BUTTON_LEFT) != 0)
        {
            if (i_PlayerHorzSpeedDelayCounter == 0)
            {
                i_deltaX = 0 - I8_HORIZONTAL_SIGHT_SPEED_MAX;
            }
            else
            {
                i_PlayerHorzSpeedDelayCounter--;
                i_deltaX = 0 - I8_HORIZONTAL_SIGHT_SPEED_MIN;
            }
        }
        if ((_buttons & BUTTON_RIGHT) != 0)
        {
            if (i_PlayerHorzSpeedDelayCounter == 0)
            {
                i_deltaX = I8_HORIZONTAL_SIGHT_SPEED_MAX;
            }
            else
            {
                i_PlayerHorzSpeedDelayCounter--;
                i_deltaX = I8_HORIZONTAL_SIGHT_SPEED_MIN;
            }
        }

        // Вычисляем новое расположение
        if (i_deltaX != 0)
        {
            i8_curViewX += i_deltaX;
            if (i_deltaX > 0)
            {
                if (i8_curViewX + VIEW_RECT_WIDTH >= I8_RIGHT_SIGHT_BORDER)
                {
                    i8_curViewX = I8_RIGHT_SIGHT_BORDER - VIEW_RECT_WIDTH;
                }
            }
            else
            {
                if (i8_curViewX < I8_LEFT_SIGHT_BORDER)
                {
                    i8_curViewX = I8_LEFT_SIGHT_BORDER;
                }
            }
        }

        if (i_deltaY != 0)
        {
            i8_curSightY += i_deltaY;
            if (i_deltaY > 0)
            {
                if (i8_curSightY + i8_sightHeight >= I8_BOTTOM_SIGHT_BORDER)
                {
                    i8_curSightY = I8_BOTTOM_SIGHT_BORDER - i8_sightHeight;
                }
            }
            else
            {
                if ((i8_curSightY - i8_sightHeight) < I8_TOP_SIGHT_BORDER)
                {
                    i8_curSightY = I8_TOP_SIGHT_BORDER + i8_sightHeight;
                }
            }
        }

        if ((i_deltaX | i_deltaY) != 0)
        {
            i8_viewRectX = i8_curViewX;
            p_PlayerSight.setMainPointXY(i8_curViewX + (VIEW_RECT_WIDTH >> 1), i8_curSightY);

            // Расстановка и обработка хода стволов
            // Левый
            if (ap_PlayerBarrelsPaths[0].isCompleted())
            {
                ap_PlayerBarrels[0].setMainPointXY(i8_curViewX, i8_curSightY);
            }
            else
            {
                ap_PlayerBarrelsPaths[0].i8_centerX = i8_curViewX;
                ap_PlayerBarrelsPaths[0].i8_centerY = i8_curSightY;
            }

            // Нижний
            if (ap_PlayerBarrelsPaths[1].isCompleted())
            {
                ap_PlayerBarrels[1].setMainPointXY(i8_curViewX + (VIEW_RECT_WIDTH >> 1), I8_VIRTUAL_SECTOR_HEIGHT);
            }
            else
            {
                ap_PlayerBarrelsPaths[1].i8_centerX = i8_curViewX + (VIEW_RECT_WIDTH >> 1);
                ap_PlayerBarrelsPaths[1].i8_centerY = I8_VIRTUAL_SECTOR_HEIGHT;
            }

            // Правый
            if (ap_PlayerBarrelsPaths[2].isCompleted())
            {
                ap_PlayerBarrels[2].setMainPointXY(i8_curViewX + VIEW_RECT_WIDTH, i8_curSightY);
            }
            else
            {
                ap_PlayerBarrelsPaths[2].i8_centerX = i8_curViewX + VIEW_RECT_WIDTH;
                ap_PlayerBarrelsPaths[2].i8_centerY = i8_curSightY;
            }

        }

        // Обработка путей хода стволов
        int i_firstFreeBarrel = -1;

        for (int li = 0; li < PLAYER_BARRELS_NUMBER; li++)
        {
            PathController p_cont = ap_PlayerBarrelsPaths[li];
            if (p_cont.isCompleted())
            {
                if (i_firstFreeBarrel < 0) i_firstFreeBarrel = li;
                continue;
            }
            p_cont.processStep();
        }

        if ((_buttons & BUTTON_FIRE) != 0)
        {
            if (i_playerShellsNumber > 0 && i_firstFreeBarrel >= 0)
            {
                // Ищем свободный спрайт выстрела
                Sprite p_playerShell = getFirstInactiveSprite(ap_PlayerShellsSprites);
                PathController p_shellPath = ap_PlayerShellsPaths[p_playerShell.i_spriteID];

                // Выставляем путь движения ствола
                PathController p_cont = ap_PlayerBarrelsPaths[i_firstFreeBarrel];
                Sprite p_spr = ap_PlayerBarrels[i_firstFreeBarrel];

                int i_shellX = 0;
                int i_shellY = 0;

                int i8_coeffHorz = 0x100;
                int i8_coeffVert = 0x100;

                int i_pathOffset = PATH_GUN_HORZ_FIRE_LEFT;
                int i_pathModify = PathController.MODIFY_NONE;

                activateSprite(p_playerShell, SPRITE_PLAYER_SHELL_NEAR);
                i_PlayerShootsCounter++;

                boolean lg_isShell_Vertical = false;

                switch (i_firstFreeBarrel)
                {
                    case 0:// Левый
                        {
                            i_shellX = p_spr.i_ScreenX + p_spr.i_width;
                            i_shellY = p_spr.i_mainY;

                            i8_coeffHorz = (int) (calculateLocalPathCoeff(((VIEW_RECT_WIDTH >> 1) + i8_curViewX - i_shellX + 0x7F) >> 8, 1, PATH_GUN_HORZ_FIRE_LEFT) >>> 32);
                            p_cont.initPath(p_spr.i_mainX, p_spr.i_mainY, 0x100, 0x100, p_spr, ash_Paths, PATH_GUN_HORZ_WAY_LEFT, 0, 0, PathController.MODIFY_NONE);
                        }
                ;
                        break;
                    case 1: // Нижний
                        {
                            i_shellY = p_spr.i_ScreenY;
                            i_shellX = p_spr.i_mainX;

                            p_cont.initPath(p_spr.i_mainX, p_spr.i_mainY, 0x100, 0x100, p_spr, ash_Paths, PATH_GUN_VERT_WAY_DOWN, 0, 0, PathController.MODIFY_NONE);
                            i8_coeffVert = (int) calculateLocalPathCoeff(1, (i_shellY - (i8_curSightY)+0x7F) >> 8, PATH_GUN_VERT_FIRE_DOWN);

                            i_pathOffset = PATH_GUN_VERT_FIRE_DOWN;

                            lg_isShell_Vertical = true;
                        }
                ;
                        break;
                    case 2: // Правый
                        {
                            i_shellX = p_spr.i_ScreenX;
                            i_shellY = p_spr.i_mainY;

                            i8_coeffHorz = (int) (calculateLocalPathCoeff((i_shellX - ((VIEW_RECT_WIDTH >> 1) + i8_curViewX+0x7F)) >> 8, 1, PATH_GUN_HORZ_FIRE_LEFT) >>> 32);
                            i_pathModify = PathController.MODIFY_FLIPHORZ;

                            p_cont.initPath(p_spr.i_mainX, p_spr.i_mainY, 0x100, 0x100, p_spr, ash_Paths, PATH_GUN_HORZ_WAY_LEFT, 0, 0, PathController.MODIFY_FLIPHORZ);
                        }
                ;
                        break;
                }

                // Проверяем наличие целевого крейсера в зоне прицела
                int i8_curSightX = p_PlayerSight.i_mainX;
                int i_cruiserID = -1;
                for (int li = 0; li < MAX_CRUISERS_NUMBER; li++)
                {
                    Sprite p_cruiser = ap_CruiserSprites[li];
                    if (!p_cruiser.lg_SpriteActive) continue;

                    int i8_x1 = p_cruiser.i_ScreenX;
                    int i8_y1 = p_cruiser.i_ScreenY;
                    int i8_x2 = i8_x1 + p_cruiser.i_width;
                    int i8_y2 = i8_y1 + p_cruiser.i_height;

                    if (i8_curSightX < i8_x1) continue;
                    if (i8_curSightX > i8_x2) continue;
                    if (i8_curSightY < i8_y1) continue;
                    if (i8_curSightY > i8_y2) continue;

                    i_cruiserID = li;
                    break;
                }

                p_playerShell.lg_SpriteInvisible = lg_isShell_Vertical;

                p_playerShell.i_ObjectState = i_cruiserID;
                startup.processGameAction(GAMEACTION_PLAYER_FIRE);

                p_shellPath.initPath(i_shellX, i_shellY, i8_coeffHorz, i8_coeffVert, p_playerShell, ash_Paths, i_pathOffset, 0, 0, i_pathModify);

                i_playerShellsNumber--;
            }
        }
    }

    /**
     * Расчитываем коэффициенты, для сжатия - растяжения пути
     *
     * @param _width  ширина затребованного прямоугольника, в который должен вписаться путь
     * @param _height высота затребованного прямоугольника, в который должен вписаться путь
     * @return пакованные коэффициенты старшие 32 бита это коэфф. по X, младшие 32 бита это коэфф. по Y
     */
    private static long calculateLocalPathCoeff(int _width, int _height, int _pathOffset)
    {
        _pathOffset -= 2;
        int i_w = ((int) ((ash_Paths[_pathOffset++] * SCALE_WIDTH) + 0x7F) >> 8);
        int i_h = ((int) ((ash_Paths[_pathOffset++] * SCALE_HEIGHT) + 0x7F) >> 8);

        if (i_w == 0) i_w = 1;
        if (i_h == 0) i_h = 1;

        // Рассчитываем коэффициенты
        long l_coeffW = ((long) _width << 8) / (long) i_w;
        long l_coeffH = ((long) _height << 8) / (long) i_h;

        return (l_coeffW << 32) | l_coeffH;
    }

    /**
     * Генерация нового крейсера противника
     */
    protected static void generateNewCruiser()
    {
        Sprite p_cruiserSprite = getFirstInactiveSprite(ap_CruiserSprites);
        if (p_cruiserSprite == null) return;

        // Поиск макроячейки, в которой будет новый крейсер
        int i_x = -1;
        int i_y = -1;
        boolean lg_notfound = false;
        for (int li = 0; li < 10; li++)
        {
            i_x = RECTANGLE_GENERATEAREA_X + getRandomInt(RECTANGLE_GENERATEAREA_WIDTH - 1);
            i_y = RECTANGLE_GENERATEAREA_Y + getRandomInt(RECTANGLE_GENERATEAREA_HEIGHT - 1);

            // Проверка на занятость ячейки и окружения
            lg_notfound = true;
            int i_index = i_x + i_y * i_macrocellsArrayWidth;
            if (alg_macroCells[i_index]) continue;
            alg_macroCells[i_index] = true;
            ai_CruiserMacrocellsIndexes[p_cruiserSprite.i_spriteID] = i_index;
            lg_notfound = false;
            break;
        }

        if (lg_notfound) return;

        // Выбираем направление крейсера
        if (getRandomInt(300) > 150)
        {
            // Правый
            activateSprite(p_cruiserSprite, SPRITE_ENEMY_CRUISER_FROM_HYPERJUMP_RIGHT);
        }
        else
        {
            // Левый
            activateSprite(p_cruiserSprite, SPRITE_ENEMY_CRUISER_FROM_HYPERJUMP_LEFT);
        }

        p_cruiserSprite.i_ObjectState = ENEMY_CRUISER_INIT_POWER;

        int i_coordX = i_x * MACROCELL_WIDTH;
        int i_coordY = i_y * MACROCELL_HEIGHT;

        i_passiveCruisersNumber--;
        i_activeCruisersNumber++;

        startup.processGameAction(GAMEACTION_CRUISER_FROM_HYPERSPACE);

        p_cruiserSprite.setMainPointXY(i_coordX, i_coordY);
    }

    /**
     * Обрабатываем истребители противника
     *
     * @return true если присутствует хоть один истребитель
     */
    private static boolean processFighters()
    {
        boolean lg_result = false;

        Sprite[] ap_fighterSprites = ap_FighterSprites;
        PathController[] ap_fighterPaths = ap_FighterPaths;

        for (int li = 0; li < MAX_FIGHTERS_NUMBER; li++)
        {
            Sprite p_fighter = ap_fighterSprites[li];
            if (!p_fighter.lg_SpriteActive) continue;

            lg_result = true;

            //p_fighter.processAnimation();

            PathController p_path = ap_fighterPaths[li];
            if (p_path.processStep())
            {
                if (p_fighter.i_ObjectState != 0)
                {
                    p_path.initPath(0, 0, 0x100, 0x100, p_fighter, ash_Paths, p_fighter.i_ObjectState, 0, 0, PathController.MODIFY_NONE);
                    p_fighter.i_ObjectState = 0;
                    setFighterDirection(p_fighter);
                    continue;
                }
                else
                {
                    switch (p_fighter.i_ObjectType)
                    {
                        case SPRITE_ENEMY_BACK_FIGHTER_LONG:
                            {
                                activateSprite(p_fighter, SPRITE_ENEMY_FRONT_FIGHTER_LONG);
                            }
                    ;
                            break;
                        case SPRITE_ENEMY_BACK_FIGHTER_NEAR:
                            {
                                activateSprite(p_fighter, SPRITE_ENEMY_BACK_FIGHTER_MIDDLE);
                            }
                    ;
                            break;
                        case SPRITE_ENEMY_BACK_FIGHTER_MIDDLE:
                            {
                                activateSprite(p_fighter, SPRITE_ENEMY_BACK_FIGHTER_LONG);
                            }
                    ;
                            break;
                        case SPRITE_ENEMY_FRONT_FIGHTER_LONG:
                            {
                                activateSprite(p_fighter, SPRITE_ENEMY_FRONT_FIGHTER_MIDDLE);
                            }
                    ;
                            break;
                        case SPRITE_ENEMY_FRONT_FIGHTER_NEAR:
                            {
                                activateSprite(p_fighter, SPRITE_ENEMY_BACK_FIGHTER_NEAR);
                            }
                    ;
                            break;
                        case SPRITE_ENEMY_FRONT_FIGHTER_MIDDLE:
                            {
                                activateSprite(p_fighter, SPRITE_ENEMY_FRONT_FIGHTER_NEAR);
                                startup.processGameAction(GAMEACTION_FIGHTER_ATTACK);
                            }
                    ;
                            break;
                    }
                    setFighterDirection(p_fighter);
                }
            }
            else
                switch (p_fighter.i_ObjectType)
                {
                        // Проверка на готовность к выстрелу (если расстояние среднее или ближнее и повернут к игроку)
                        case SPRITE_ENEMY_FRONT_FIGHTER_MIDDLE:
                    case SPRITE_ENEMY_FRONT_FIGHTER_NEAR:
                        {
                            if (i_enemyShellsNumber < MAX_ENEMY_SHELLS_NUMBER)
                            {
                                if (getRandomInt(i_fightersFiringFreq) == (i_fightersFiringFreq >> 1))
                                {
                                    generateFighterShell(p_fighter);
                                }
                            }
                        }
                ;
                        break;
                }

        }
        return lg_result;
    }

    /**
     * Обрабатываем крейсера противника
     */
    private static void processCruisers()
    {
        Sprite[] ap_cruiserSprites = ap_CruiserSprites;

        for (int li = 0; li < MAX_CRUISERS_NUMBER; li++)
        {
            Sprite p_cruiser = ap_cruiserSprites[li];
            if (!p_cruiser.lg_SpriteActive) continue;

            boolean lg_result = p_cruiser.processAnimation();

            switch (p_cruiser.i_ObjectType)
            {
                case SPRITE_ENEMY_CRUISER_DESTROYED_LEFT:
                case SPRITE_ENEMY_CRUISER_DESTROYED_RIGHT:
                    {
                        p_cruiser.setMainPointXY(p_cruiser.i_mainX, p_cruiser.i_mainY + CRUISER_FALL_SPEED);

                        if (p_cruiser.i_ScreenY > I8_VIRTUAL_SECTOR_HEIGHT)
                        {
                            p_cruiser.lg_SpriteActive = false;
                            i_activeCruisersNumber--;
                            alg_macroCells[ai_CruiserMacrocellsIndexes[p_cruiser.i_spriteID]] = false;
                            continue;
                        }
                        else if (getRandomInt(60) == 30)
                        {
                            generateExplosionAndPartsFromObject(p_cruiser, SPRITE_EXPLOSION_NEAR, SPRITE_PARTS_MIDDLE);
                            startup.processGameAction(GAMEACTION_NEAR_EXPLOSION);
                            p_cruiser.lg_SpriteActive = false;
                            i_activeCruisersNumber--;
                            alg_macroCells[ai_CruiserMacrocellsIndexes[p_cruiser.i_spriteID]] = false;
                            continue;
                        }
                    }
            ;
                    break;
                case SPRITE_ENEMY_CRUISER_FROM_HYPERJUMP_LEFT:
                    {
                        if (lg_result) activateSprite(p_cruiser, SPRITE_ENEMY_CRUISER_LEFT);
                    }
            ;
                    break;
                case SPRITE_ENEMY_CRUISER_FROM_HYPERJUMP_RIGHT:
                    {
                        if (lg_result) activateSprite(p_cruiser, SPRITE_ENEMY_CRUISER_RIGHT);
                    }
            ;
                    break;
                case SPRITE_ENEMY_CRUISER_TO_HYPERJUMP_RIGHT:
                    {
                        if (lg_result)
                        {
                            p_cruiser.lg_SpriteActive = false;
                            i_activeCruisersNumber--;
                            i_passiveCruisersNumber++;
                            alg_macroCells[ai_CruiserMacrocellsIndexes[p_cruiser.i_spriteID]] = false;
                        }
                    }
            ;
                    break;
                case SPRITE_ENEMY_CRUISER_TO_HYPERJUMP_LEFT:
                    {
                        if (lg_result)
                        {
                            p_cruiser.lg_SpriteActive = false;
                            i_activeCruisersNumber--;
                            i_passiveCruisersNumber++;
                            alg_macroCells[ai_CruiserMacrocellsIndexes[p_cruiser.i_spriteID]] = false;
                        }
                    }
            ;
                    break;
                case SPRITE_ENEMY_CRUISER_RIGHT:
                case SPRITE_ENEMY_CRUISER_LEFT:
                    {
                        // Проверка на желание смены локации крейсером
                        if (getRandomInt(i_cruiserChangeLoactionFreq) == (i_cruiserChangeLoactionFreq >> 1))
                        {
                            // Запускаем гиперпереход крейсера
                            if (p_cruiser.i_ObjectType == SPRITE_ENEMY_CRUISER_LEFT)
                            {
                                activateSprite(p_cruiser, SPRITE_ENEMY_CRUISER_TO_HYPERJUMP_LEFT);
                            }
                            else
                            {
                                activateSprite(p_cruiser, SPRITE_ENEMY_CRUISER_TO_HYPERJUMP_RIGHT);
                            }

                            if (lg_IsTargeCruiser)
                            {
                                if (p_cruiser.equals(p_TargetSprite))
                                {
                                    p_TargetSprite = null;
                                }
                            }

                            startup.processGameAction(GAMEACTION_CRUISER_INTO_HYPERSPACE);

                            continue;
                        }

                        // Проверяем выстрел
                        if (i_enemyShellsNumber < MAX_ENEMY_SHELLS_NUMBER)
                        {
                            if (getRandomInt(i_cruisersFiringFreq) == (i_cruisersFiringFreq >> 1))
                            {
                                // Генерируем выстрел
                                generateCruiserShell(p_cruiser);
                            }
                        }

                        // Проверяем вылет нового истребителя
                        if (i_fighterNumber < MAX_FIGHTERS_NUMBER)
                        {
                            if (getRandomInt(i_fightersGenerationFreq) == (i_fightersGenerationFreq >> 1))
                            {
                                // Генерируем вылет истребителя
                                generateNewFighter(p_cruiser);
                            }
                        }
                    }
            ;
                    break;
            }
        }
    }

    /**
     * Генерация взрывов и обломков на месте заданного объекта
     *
     * @param _object        целевой объект
     * @param _explosionType тип взрыва
     * @param _partsType     типа разлетающихся частиц, -1 - частицы отсутствуют
     */
    private static void generateExplosionAndPartsFromObject(Sprite _object, int _explosionType, int _partsType)
    {
        int i_x = _object.i_mainX;
        int i_y = _object.i_mainY;

        Sprite p_explosion = getFirstInactiveSprite(ap_ExplosionSprites);

        if (p_explosion == null) return;

        activateSprite(p_explosion, _explosionType);
        p_explosion.setMainPointXY(i_x, i_y);

        //#if PARTS_PRESENTED
        int i_partsNumber;

        int i_partsPath;
        int i_maxStep;
        int i_partsStep;

        if (_partsType >= 0)
        {
            switch (_partsType)
            {
                case SPRITE_PARTS_LONG:
                    {
                        i_partsNumber = 2;
                        i_partsPath = PATH_PARTS_LONG_WAY_DOWN_LEFT;
                        i_partsStep = ((int) (5 * SCALE_WIDTH + 0x7F) >> 8);
                        i_maxStep = 5;
                    }
            ;
                    break;
                case SPRITE_PARTS_MIDDLE:
                    {
                        i_partsNumber = 4;
                        i_partsPath = PATH_PARTS_MIDDLE_WAY_DOWN_LEFT;
                        i_partsStep = ((int) (15 * SCALE_WIDTH + 0x7F) >> 8);
                        i_maxStep = 5;
                    }
            ;
                    break;
                default:
                    {
                        i_partsNumber = 5;
                        i_partsPath = PATH_PARTS_NEAR_WAY_DOWN_LEFT;
                        i_partsStep = ((int) (45 * SCALE_WIDTH+0x7F) >> 8);
                        i_maxStep = 4;
                    }
            }

            if (i_partsStep > 2)
            {
                for (int li = 0; li < i_partsNumber; li++)
                {
                    Sprite p_part = getFirstInactiveSprite(ap_ExplosionSprites);
                    if (p_part == null) break;

                    int i_partRectWidth = (1 + getRandomInt(i_maxStep)) * i_partsStep;
                    int i_partRectHeight = (1 + getRandomInt(i_maxStep)) * i_partsStep;

                    int i_pathModify = getRandomInt(40) > 20 ? PathController.MODIFY_FLIPHORZ : PathController.MODIFY_NONE;
                    i_pathModify |= getRandomInt(40) > 20 ? PathController.MODIFY_FLIPVERT : PathController.MODIFY_NONE;

                    long l_coeff = calculateLocalPathCoeff(i_partRectWidth, i_partRectHeight, i_partsPath);

                    int i8_horzCoeff = (int) (l_coeff >> 32);
                    int i8_vertCoeff = (int) l_coeff;

                    activateSprite(p_part, _partsType);
                    PathController p_contr = ap_ExplosionsPaths[p_part.i_spriteID];

                    // Выставляем случайные номера стартовых кадров
                    int i_frame = getRandomInt(p_part.i_maxFrames * 100 - 5) / 100;
                    p_part.i_Frame = i_frame;

                    p_contr.initPath(i_x, i_y, i8_horzCoeff, i8_vertCoeff, p_part, ash_Paths, i_partsPath, 0, 0, i_pathModify);
                }
            }
        }
        //#endif
    }

    /**
     * Генерация выстрела истребителя
     *
     * @param _parentFighter стреляющий истребитель
     */
    private static void generateFighterShell(Sprite _parentFighter)
    {
        Sprite p_shell = getFirstInactiveSprite(ap_EnemyShellsSprites);
        if (p_shell == null) return;

        int i_pathType = 0;


        switch (_parentFighter.i_ObjectType)
        {
            case SPRITE_ENEMY_FRONT_FIGHTER_MIDDLE:
                {
                    activateSprite(p_shell, SPRITE_ENEMY_SHELL_MIDDLE);
                    i_pathType = PATH_ENEMY_MIDDLE_DISTANCE_FIRE_TOLEFT;
                    startup.processGameAction(GAMEACTION_FIGHTER_FIRING_MIDDLE);
                }
        ;
                break;
            case SPRITE_ENEMY_FRONT_FIGHTER_NEAR:
                {
                    activateSprite(p_shell, SPRITE_ENEMY_SHELL_NEAR);
                    i_pathType = PATH_ENEMY_NEAR_DISTANCE_FIRE_TOLEFT;
                    startup.processGameAction(GAMEACTION_FIGHTER_FIRING_NEAR);
                }
        ;
                break;
            default:
                return;
        }

        PathController p_path = ap_EnemyShellsPaths[p_shell.i_spriteID];

        int i_centerX = _parentFighter.i_mainX;
        int i_centerY = _parentFighter.i_mainY;

        // Флаг выстрела в верхнюю границу
        boolean lg_directUp = false;

        if (getRandomInt(300) > 150) lg_directUp = true;

        final int FIRE_STEP = ((int) ((0x1000 * SCALE_WIDTH) + 0x7F) >> 8);
        final int NUMBER = I8_VIRTUAL_SECTOR_WIDTH / FIRE_STEP;

        int i_Xcoord = (getRandomInt(NUMBER) - (NUMBER >> 1)) * FIRE_STEP;
        int i_Ycoord = lg_directUp ? 0 : I8_VIRTUAL_SECTOR_HEIGHT;

        int i_pathModifier = PathController.MODIFY_NONE;

        if (i_Xcoord > 0)
        {
            i_pathModifier |= PathController.MODIFY_FLIPHORZ;
        }

        if (lg_directUp)
        {
            i_pathModifier |= PathController.MODIFY_FLIPVERT;
            p_shell.i_ObjectState = 0;
        }
        else
            p_shell.i_ObjectState = ENEMY_FIGHTER_SHELL_POWER;

        long l_coeff = calculateLocalPathCoeff((Math.abs(i_Xcoord)+0x7F) >> 8, (Math.abs(i_Ycoord - i_centerY)+0x7F) >> 8, i_pathType);

        int i8_horzCoeff = (int) (l_coeff >>> 32);
        int i8_vertCoeff = (int) (l_coeff);

        p_shell.lg_SpriteInvisible = false;

        i_enemyShellsNumber++;

        p_path.initPath(i_centerX, i_centerY, i8_horzCoeff, i8_vertCoeff, p_shell, ash_Paths, i_pathType, 0, 0, i_pathModifier);
    }

    /**
     * Генерация выстрела крейсера
     *
     * @param _parentCruiser стреляющий крейсер
     */
    private static void generateCruiserShell(Sprite _parentCruiser)
    {
        Sprite p_shell = getFirstInactiveSprite(ap_EnemyShellsSprites);
        if (p_shell == null) return;

        activateSprite(p_shell, SPRITE_ENEMY_SHELL_LONG);
        PathController p_path = ap_EnemyShellsPaths[p_shell.i_spriteID];

        int i_centerX = _parentCruiser.i_mainX;
        int i_centerY = _parentCruiser.i_mainY;

        // Флаг выстрела в верхнюю границу
        boolean lg_directUp = false;

        if (getRandomInt(300) > 150) lg_directUp = true;

        final int FIRE_STEP = ((int) ((0x1000 * SCALE_WIDTH) + 0x7F) >> 8);
        final int NUMBER = I8_VIRTUAL_SECTOR_WIDTH / FIRE_STEP;

        int i_Xcoord = (getRandomInt(NUMBER) - (NUMBER >> 1)) * FIRE_STEP;
        int i_Ycoord = lg_directUp ? 0 : I8_VIRTUAL_SECTOR_HEIGHT;

        int i_pathModifier = PathController.MODIFY_NONE;

        if (i_Xcoord > 0)
        {
            i_pathModifier |= PathController.MODIFY_FLIPHORZ;
        }

        if (lg_directUp)
        {
            i_pathModifier |= PathController.MODIFY_FLIPVERT;
            p_shell.i_ObjectState = 0;
        }
        else
            p_shell.i_ObjectState = ENEMY_CRUISER_SHELL_POWER;

        long l_coeff = calculateLocalPathCoeff((Math.abs(i_Xcoord)+0x7F) >> 8, (Math.abs(i_Ycoord - i_centerY)+0x7F) >> 8, PATH_ENEMY_LONG_DISTANCE_FIRE_TOLEFT);

        int i8_horzCoeff = (int) (l_coeff >>> 32);
        int i8_vertCoeff = (int) (l_coeff);

        p_shell.lg_SpriteInvisible = true;

        startup.processGameAction(GAMEACTION_CRUISER_FIRING);

        i_enemyShellsNumber++;

        p_path.initPath(i_centerX, i_centerY, i8_horzCoeff, i8_vertCoeff, p_shell, ash_Paths, PATH_ENEMY_LONG_DISTANCE_FIRE_TOLEFT, 0, 0, i_pathModifier);
    }

    /**
     * Генерация нового истребителья противника
     *
     * @param _parentCruiser порождающий крейсер
     */
    private static void generateNewFighter(Sprite _parentCruiser)
    {
        Sprite p_fighter = getFirstInactiveSprite(ap_FighterSprites);
        if (p_fighter == null) return;

        PathController p_controller = ap_FighterPaths[p_fighter.i_spriteID];
        activateSprite(p_fighter, SPRITE_ENEMY_FRONT_FIGHTER_LONG);

        // Выбираем тип пути для истребителя (0,1,2)
        int i_pathType;
        while (true)
        {
            i_pathType = getRandomInt(2999) / 1000;
            if (i_pathType != i_lastGeneatedFighterIndex) break;
        }
        i_lastGeneatedFighterIndex = i_pathType;

        switch (i_pathType)
        {
            case 0:
                i_pathType = PATH_FIGHTER_PATH_1;
                break;
            case 1:
                i_pathType = PATH_FIGHTER_PATH_2;
                break;
            case 2:
                i_pathType = PATH_FIGHTER_PATH_3;
                break;
        }

        int i_firstX = (int) ((long) ash_Paths[i_pathType + 2] * SCALE_WIDTH);
        int i_firstY = (int) ((long) ash_Paths[i_pathType + 3] * SCALE_HEIGHT);

        int i8_vertCoeff = 0;
        int i8_horzCoeff = 0;
        int i_modify = PathController.MODIFY_NONE;

        int i_centerX = _parentCruiser.i_mainX;
        int i_centerY = _parentCruiser.i_mainY;

        if (i_firstX < i_centerX) i_modify |= PathController.MODIFY_FLIPHORZ;
        if (i_firstY < i_centerY) i_modify |= PathController.MODIFY_FLIPVERT;

        long l_coef = calculateLocalPathCoeff((Math.abs(i_firstX - i_centerX)+0x7F) >> 8, (Math.abs(i_firstY - i_centerY)+0x7F) >> 8, PATH_FIGHTER_POS_PATH);

        i8_horzCoeff = (int) (l_coef >>> 32);
        i8_vertCoeff = (int) l_coef;

        startup.processGameAction(GAMEACTION_FIGHTER_GENERATION);

        i_fighterNumber++;

        p_controller.initPath(i_centerX, i_centerY, i8_horzCoeff, i8_vertCoeff, p_fighter, ash_Paths, PATH_FIGHTER_POS_PATH, 0, 0, i_modify);
        p_fighter.i_ObjectState = i_pathType;
        setFighterDirection(p_fighter);
    }

    /**
     * Обработка вражеских снарядов
     *
     * @return возвращает true если присутствуют, иначе false
     */
    private static boolean processEnemyShells()
    {
        boolean lg_exists = false;

        Sprite[] ap_enShells = ap_EnemyShellsSprites;
        PathController[] ap_enShellsPaths = ap_EnemyShellsPaths;

        for (int li = 0; li < MAX_ENEMY_SHELLS_NUMBER; li++)
        {
            Sprite p_shell = ap_enShells[li];
            if (!p_shell.lg_SpriteActive) continue;
            lg_exists = true;

            p_shell.processAnimation();
            PathController p_shellPath = ap_enShellsPaths[li];
            if (p_shellPath.processStep())
            {
                switch (p_shell.i_ObjectType)
                {
                    case SPRITE_ENEMY_SHELL_LONG:
                        {
                            activateSprite(p_shell, SPRITE_ENEMY_SHELL_MIDDLE);
                        }
                ;
                        break;
                    case SPRITE_ENEMY_SHELL_MIDDLE:
                        {
                            activateSprite(p_shell, SPRITE_ENEMY_SHELL_NEAR);
                        }
                ;
                        break;
                    case SPRITE_ENEMY_SHELL_NEAR:
                        {
                            p_shell.lg_SpriteActive = false;
                            int i_power = p_shell.i_ObjectState;
                            i_playerHealth -= i_power;

                            i_enemyShellsNumber--;
                            if (i_power != 0) startup.processGameAction(GAMEACTION_ENEMY_HIT);
                            continue;
                        }
                }
            }
        }

        return lg_exists;
    }

    /**
     * Функция осуществляет выбор цели, по которой перед игроком будут выводиться параметры и направляющие
     */
    private static final void selectTarget()
    {
        // Первым делом проверяем неповрежденные крейсера
        int i_targetCruiser = 0;
        int i_targetFighter = 0;

        lg_IsTargeCruiser = false;

        if (p_TargetSprite != null)
        {
            switch (p_TargetSprite.i_ObjectType)
            {
                case SPRITE_ENEMY_CRUISER_LEFT:
                case SPRITE_ENEMY_CRUISER_RIGHT:
                    {
                        i_targetCruiser = p_TargetSprite.i_spriteID + 1;
                    }
            ;
                    break;
                default:
                    {
                        i_targetFighter = p_TargetSprite.i_spriteID + 1;
                    }
            }
        }

        p_TargetSprite = null;

        // Выставляем крейсер, начиная со следующего за текущим
        for (int li = i_targetCruiser; li < MAX_CRUISERS_NUMBER; li++)
        {
            Sprite p_spr = ap_CruiserSprites[li];
            if (p_spr.lg_SpriteActive)
            {
                switch (p_spr.i_ObjectType)
                {
                    case SPRITE_ENEMY_CRUISER_LEFT:
                    case SPRITE_ENEMY_CRUISER_RIGHT:
                        {
                            li = MAX_CRUISERS_NUMBER;
                            p_TargetSprite = p_spr;
                            lg_IsTargeCruiser = true;
                        }
                ;
                        break;
                }
            }
        }

        if (p_TargetSprite == null)
        {
            for (int li = 0; li < i_targetCruiser; li++)
            {
                Sprite p_spr = ap_CruiserSprites[li];
                if (p_spr.lg_SpriteActive)
                {
                    switch (p_spr.i_ObjectType)
                    {
                        case SPRITE_ENEMY_CRUISER_LEFT:
                        case SPRITE_ENEMY_CRUISER_RIGHT:
                            {
                                li = i_targetCruiser;
                                p_TargetSprite = p_spr;
                                lg_IsTargeCruiser = true;
                            }
                    ;
                            break;
                    }
                }
            }
        }

        if (p_TargetSprite != null) return;

        // Проверяем истребители
        // Выставляем истребитель, начиная со следующего за текущим
        for (int li = i_targetFighter; li < MAX_FIGHTERS_NUMBER; li++)
        {
            Sprite p_spr = ap_FighterSprites[li];
            if (p_spr.lg_SpriteActive)
            {
                p_TargetSprite = p_spr;
                break;
            }
        }

        if (p_TargetSprite == null)
        {
            for (int li = 0; li < i_targetFighter; li++)
            {
                Sprite p_spr = ap_FighterSprites[li];
                if (p_spr.lg_SpriteActive)
                {
                    p_TargetSprite = p_spr;
                    break;
                }
            }
        }
    }


    /**
     * Обработка снарядов игрока
     *
     * @return true если снаряды присутствуют
     */
    private static boolean processPlayerShells()
    {
        boolean lg_exists = false;

        Sprite[] ap_plShells = ap_PlayerShellsSprites;
        PathController[] ap_plShellsPaths = ap_PlayerShellsPaths;
        Sprite[] ap_enemyFighters = ap_FighterSprites;
        Sprite[] ap_enemyCruisers = ap_CruiserSprites;

        for (int li = 0; li < MAX_PLAYER_SHELLS_NUMBER; li++)
        {
            Sprite p_shellSprite = ap_plShells[li];
            if (!p_shellSprite.lg_SpriteActive) continue;
            lg_exists = true;

            p_shellSprite.processAnimation();
            PathController p_shellPath = ap_plShellsPaths[li];
            if (p_shellPath.processStep())
            {
                switch (p_shellSprite.i_ObjectType)
                {
                    case SPRITE_PLAYER_SHELL_NEAR:
                        {
                            activateSprite(p_shellSprite, SPRITE_PLAYER_SHELL_MIDDLE);
                        }
                ;
                        break;
                    case SPRITE_PLAYER_SHELL_MIDDLE:
                        {
                            activateSprite(p_shellSprite, SPRITE_PLAYER_SHELL_LONG);
                        }
                ;
                        break;
                    case SPRITE_PLAYER_SHELL_LONG:
                        {
                            p_shellSprite.lg_SpriteActive = false;
                            i_playerShellsNumber++;
                            continue;
                        }
                }
            }

            switch (p_shellSprite.i_ObjectType)
            {
                case SPRITE_PLAYER_SHELL_NEAR:
                    {
                        // Проверка на попадание в ближний истребитель
                        for (int lf = 0; lf < MAX_FIGHTERS_NUMBER; lf++)
                        {
                            Sprite p_fight = ap_enemyFighters[lf];
                            if (!p_fight.lg_SpriteActive) continue;
                            switch (p_fight.i_ObjectType)
                            {
                                case SPRITE_ENEMY_BACK_FIGHTER_NEAR:
                                case SPRITE_ENEMY_FRONT_FIGHTER_NEAR:
                                    {
                                        if (p_shellSprite.isCollided(p_fight))
                                        {
                                            p_shellSprite.lg_SpriteActive = false;
                                            i_playerShellsNumber++;
                                            i_PlayerHitCounter++;
                                            i_playerGameScores += SCORES_FOR_FIGHTER;
                                            generateExplosionAndPartsFromObject(p_fight, SPRITE_EXPLOSION_NEAR, SPRITE_PARTS_NEAR);

                                            startup.processGameAction(GAMEACTION_NEAR_EXPLOSION);

                                            p_fight.lg_SpriteActive = false;
                                            lf = MAX_FIGHTERS_NUMBER;
                                            i_fighterNumber--;

                                            if (!lg_IsTargeCruiser)
                                            {
                                                if (p_fight.equals(p_TargetSprite))
                                                {
                                                    p_TargetSprite = null;
                                                }
                                            }
                                        }
                                    }
                            ;
                                    break;
                            }
                        }
                        if (!p_shellSprite.lg_SpriteActive) continue;
                    }
            ;
                    break;
                case SPRITE_PLAYER_SHELL_MIDDLE:
                    {
                        // Проверка на попадание в средний истребитель
                        for (int lf = 0; lf < MAX_FIGHTERS_NUMBER; lf++)
                        {
                            Sprite p_fight = ap_enemyFighters[lf];
                            if (!p_fight.lg_SpriteActive) continue;
                            switch (p_fight.i_ObjectType)
                            {
                                case SPRITE_ENEMY_BACK_FIGHTER_MIDDLE:
                                case SPRITE_ENEMY_FRONT_FIGHTER_MIDDLE:
                                    {
                                        if (p_shellSprite.isCollided(p_fight))
                                        {
                                            p_shellSprite.lg_SpriteActive = false;
                                            i_playerShellsNumber++;
                                            i_PlayerHitCounter++;
                                            i_playerGameScores += SCORES_FOR_FIGHTER;
                                            generateExplosionAndPartsFromObject(p_fight, SPRITE_EXPLOSION_MIDDLE, SPRITE_PARTS_MIDDLE);

                                            startup.processGameAction(GAMEACTION_MIDDLE_EXPLOSION);

                                            p_fight.lg_SpriteActive = false;
                                            lf = MAX_FIGHTERS_NUMBER;
                                            i_fighterNumber--;

                                            if (!lg_IsTargeCruiser)
                                            {
                                                if (p_fight.equals(p_TargetSprite))
                                                {
                                                    p_TargetSprite = null;
                                                }
                                            }
                                        }
                                    }
                            ;
                                    break;
                            }
                        }
                        if (!p_shellSprite.lg_SpriteActive) continue;
                    }
            ;
                    break;
                case SPRITE_PLAYER_SHELL_LONG:
                    {
                        // Проверка на попадание в дальний истребитель
                        for (int lf = 0; lf < MAX_FIGHTERS_NUMBER; lf++)
                        {
                            Sprite p_fight = ap_enemyFighters[lf];
                            if (!p_fight.lg_SpriteActive) continue;
                            switch (p_fight.i_ObjectType)
                            {
                                case SPRITE_ENEMY_BACK_FIGHTER_LONG:
                                case SPRITE_ENEMY_FRONT_FIGHTER_LONG:
                                    {
                                        if (p_shellSprite.isCollided(p_fight))
                                        {
                                            p_shellSprite.lg_SpriteActive = false;
                                            i_playerShellsNumber++;
                                            i_PlayerHitCounter++;
                                            i_playerGameScores += SCORES_FOR_FIGHTER;
                                            generateExplosionAndPartsFromObject(p_fight, SPRITE_EXPLOSION_LONG, -1);

                                            startup.processGameAction(GAMEACTION_LONG_EXPLOSION);

                                            p_fight.lg_SpriteActive = false;
                                            lf = MAX_FIGHTERS_NUMBER;
                                            i_fighterNumber--;

                                            if (!lg_IsTargeCruiser)
                                            {
                                                if (p_fight.equals(p_TargetSprite))
                                                {
                                                    p_TargetSprite = null;
                                                }
                                            }
                                        }
                                    }
                            ;
                                    break;
                            }
                        }
                        if (!p_shellSprite.lg_SpriteActive) continue;

                        // Проверяем, направлен ли выстрел в крейсер
                        int i_cruiserID = p_shellSprite.i_ObjectState;
                        if (i_cruiserID < 0) continue;

                        // Проверка на попадание в крейсер
                        Sprite p_cruiser = ap_enemyCruisers[i_cruiserID];
                        if (!p_cruiser.lg_SpriteActive) continue;
                        if (p_cruiser.isCollided(p_shellSprite))
                        {
                            switch (p_cruiser.i_ObjectType)
                            {
                                case SPRITE_ENEMY_CRUISER_LEFT:
                                case SPRITE_ENEMY_CRUISER_RIGHT:
                                case SPRITE_ENEMY_CRUISER_FROM_HYPERJUMP_LEFT:
                                case SPRITE_ENEMY_CRUISER_FROM_HYPERJUMP_RIGHT:
                                case SPRITE_ENEMY_CRUISER_TO_HYPERJUMP_LEFT:
                                case SPRITE_ENEMY_CRUISER_TO_HYPERJUMP_RIGHT:
                                    {
                                        i_playerShellsNumber++;
                                        generateExplosionAndPartsFromObject(p_shellSprite, SPRITE_EXPLOSION_LONG, -1);
                                        startup.processGameAction(GAMEACTION_LONG_EXPLOSION);

                                        p_shellSprite.lg_SpriteActive = false;

                                        p_cruiser.i_ObjectState -= PLAYER_SHELL_POWER;
                                        i_PlayerHitCounter++;

                                        if (p_cruiser.i_ObjectState <= 0)
                                        {
                                            i_playerGameScores += SCORES_FOR_CRUISER;

                                            if (lg_IsTargeCruiser)
                                            {
                                                if (p_cruiser.equals(p_TargetSprite))
                                                {
                                                    p_TargetSprite = null;
                                                }
                                            }

                                            if (getRandomInt(100) > 90)
                                            {
                                                generateExplosionAndPartsFromObject(p_cruiser, SPRITE_EXPLOSION_NEAR, SPRITE_PARTS_MIDDLE);

                                                startup.processGameAction(GAMEACTION_NEAR_EXPLOSION);
                                                alg_macroCells[ai_CruiserMacrocellsIndexes[p_cruiser.i_spriteID]] = false;
                                                p_cruiser.lg_SpriteActive = false;
                                                i_activeCruisersNumber--;
                                            }
                                            else
                                            {
                                                switch (p_cruiser.i_ObjectType)
                                                {
                                                    case SPRITE_ENEMY_CRUISER_LEFT:
                                                    case SPRITE_ENEMY_CRUISER_FROM_HYPERJUMP_LEFT:
                                                    case SPRITE_ENEMY_CRUISER_TO_HYPERJUMP_LEFT:
                                                        {
                                                            activateSprite(p_cruiser, SPRITE_ENEMY_CRUISER_DESTROYED_LEFT);
                                                            startup.processGameAction(GAMEACTION_CRUISER_DESTROYED);
                                                        }
                                                ;
                                                        break;
                                                    case SPRITE_ENEMY_CRUISER_FROM_HYPERJUMP_RIGHT:
                                                    case SPRITE_ENEMY_CRUISER_RIGHT:
                                                    case SPRITE_ENEMY_CRUISER_TO_HYPERJUMP_RIGHT:
                                                        {
                                                            activateSprite(p_cruiser, SPRITE_ENEMY_CRUISER_DESTROYED_RIGHT);
                                                            startup.processGameAction(GAMEACTION_CRUISER_DESTROYED);
                                                        }
                                                ;
                                                        break;
                                                }
                                            }
                                        }
                                    }
                            }
                        }
                    }
            ;
                    break;
            }
        }
        return lg_exists;
    }

    /**
     * Обработка взрывов и обломков
     *
     * @return true если присутствуют
     */
    private static boolean processExplosionParts()
    {
        boolean lg_exists = false;
        Sprite[] ap_explParts = ap_ExplosionSprites;
        PathController[] ap_explPartsPaths = ap_ExplosionsPaths;

        for (int li = 0; li < MAX_EXPLOSIONS_NUMBER; li++)
        {
            Sprite p_sprite = ap_explParts[li];
            if (!p_sprite.lg_SpriteActive) continue;
            lg_exists = true;

            boolean lg_animation = p_sprite.processAnimation();
            //#if PARTS_PRESENTED
            switch (p_sprite.i_ObjectType)
            {
                case SPRITE_EXPLOSION_LONG:
                case SPRITE_EXPLOSION_NEAR:
                case SPRITE_EXPLOSION_MIDDLE:
                    {
                        //#endif
                        if (lg_animation) p_sprite.lg_SpriteActive = false;
                        //#if PARTS_PRESENTED
                    }
            ;
                    break;
                default:
                    {
                        PathController p_cont = ap_explPartsPaths[li];
                        p_cont.processStep();
                        if (p_cont.isCompleted())
                        {
                            p_sprite.lg_SpriteActive = false;
                        }
                    }
                    //#endif
            }
        }
        return lg_exists;
    }

    public static final int getScores()
    {
        i_PlayerShootsCounter = i_PlayerShootsCounter == 0 ? 1 : i_PlayerShootsCounter;
        int i_stateHit = getHitPercent();
        int i_result = i_playerGameScores + (i_stateHit * 1000) / 100 + SCORES_FOR_HIT * i_PlayerHitCounter;
        i_result = calculateScores(i_result);
        return i_result;
    }

    public static final int getHitPercent()
    {
        if (i_PlayerShootsCounter == 0) return 0;
        return (i_PlayerHitCounter * 100) / i_PlayerShootsCounter;
    }

    // The array contains values for path controllers
    public static short[] ash_Paths;

    // PATH offsets
    private static final int PATH_ENEMY_LONG_DISTANCE_FIRE_TOLEFT = 2;
    private static final int PATH_ENEMY_MIDDLE_DISTANCE_FIRE_TOLEFT = 18;
    private static final int PATH_GUN_HORZ_WAY_LEFT = 31;
    private static final int PATH_GUN_VERT_WAY_DOWN = 44;
    private static final int PATH_GUN_HORZ_FIRE_LEFT = 57;
    private static final int PATH_GUN_VERT_FIRE_DOWN = 73;
    private static final int PATH_FIGHTER_PATH_1 = 89;
    private static final int PATH_FIGHTER_PATH_2 = 417;
    private static final int PATH_FIGHTER_PATH_3 = 763;
    private static final int PATH_FIGHTER_POS_PATH = 1055;
    private static final int PATH_ENEMY_NEAR_DISTANCE_FIRE_TOLEFT = 1065;
    private static final int PATH_PARTS_LONG_WAY_DOWN_LEFT = 1075;
    private static final int PATH_PARTS_MIDDLE_WAY_DOWN_LEFT = 1085;
    private static final int PATH_PARTS_NEAR_WAY_DOWN_LEFT = 1098;

    /**
     * Максимальное количество спрайтов истребителей, используемых в игре
     */
    public static final int MAX_FIGHTERS_NUMBER = 8;

    /**
     * Максимальное количество взрывов и обломков, используемых в игре
     */
    public static final int MAX_EXPLOSIONS_NUMBER = 10;

    /**
     * Максимальное количество спрайтов крейсеров, используемых в игре
     */
    public static final int MAX_CRUISERS_NUMBER = 10;

    /**
     * Максимальное количество спрайтов вражеских выстрелов, используемых в игре
     */
    public static final int MAX_ENEMY_SHELLS_NUMBER = 20;

    /**
     * Максимальное количество спрайтов выстрелов игрока, используемых в игре
     */
    public static final int MAX_PLAYER_SHELLS_NUMBER = 10;

    /**
     * Массив, содержащий спрайты истребителей
     */
    public static Sprite[] ap_FighterSprites;

    /**
     * Массив, содержащий пути спрайтов истребителей
     */
    public static PathController[] ap_FighterPaths;

    /**
     * Скорость уничтоженного ухода крейсера вниз по экрану
     */
    private static final int CRUISER_FALL_SPEED = 0x80;

    /**
     * Массив, содержащий спрайты крейсеров
     */
    public static Sprite[] ap_CruiserSprites;

    /**
     * Массив, содержащий номера макроячеек,занятых крейсерами
     */
    public static int[] ai_CruiserMacrocellsIndexes;

    /**
     * Массив содержит спра1ты снарядов игрока
     */
    public static Sprite[] ap_PlayerShellsSprites;

    /**
     * Массив содержит пути спрайтов снарядов игрока
     */
    public static PathController[] ap_PlayerShellsPaths;

    /**
     * Массив, содержащий спрайты выстрелов противника
     */
    public static Sprite[] ap_EnemyShellsSprites;

    /**
     * Массив, содержащий спрайты взрывов и обломков
     */
    public static Sprite[] ap_ExplosionSprites;

    /**
     * Массив, содержащий пути взрывов и обломков
     */
    public static PathController[] ap_ExplosionsPaths;

    /**
     * Массив, содержащий пути спрайтов выстрелов противника
     */
    public static PathController[] ap_EnemyShellsPaths;

    /**
     * Прицел игрока
     */
    public static Sprite p_PlayerSight;

    /**
     * Ширина видимой области
     */
    public static final int VIEW_RECT_WIDTH = ((int) (0xB0 * SCALE_WIDTH) + 0x7F); // 176

    /**
     * Высота макроячейки, (!) при редактировании данного параметра надо проверить его совместимомсть с прямоугольной областью генерации крейсеров
     */
    protected static final int MACROCELL_HEIGHT = ((int) ((0x2000 * SCALE_HEIGHT) + 0x7F) >> 8);//32

    /**
     * Ширина макроячейки,  (!) при редактировании данного параметра надо проверить его совместимомсть с прямоугольной областью генерации крейсеров
     */
    protected static final int MACROCELL_WIDTH = ((int) ((0x3000 * SCALE_WIDTH) + 0x7F) >> 8);//32

    /**
     * Массив содержит состояние занятости макроячеек (что бы не было крейсеров в одной ячейке)
     */
    private static boolean[] alg_macroCells;

    /**
     * Ширина массива макроячеек
     */
    private static int i_macrocellsArrayWidth;

    /**
     * Координата X видимой области
     */
    public static int i8_viewRectX;

    /**
     * Стволы игрока
     */
    public static Sprite[] ap_PlayerBarrels;

    /**
     * Пути стволов игрока
     */
    public static PathController[] ap_PlayerBarrelsPaths;

    /**
     * Количество стволов игрока
     */
    public static final int PLAYER_BARRELS_NUMBER = 3;

    /**
     * Мощность снаряда игрока
     */
    private static final int PLAYER_SHELL_POWER = 10;

    /**
     * Количество очков, начисляемых за сбитый истебитель
     */
    private static final int SCORES_FOR_FIGHTER = 3;

    /**
     * Количество очков, начисляемых за сбитый крейсер
     */
    private static final int SCORES_FOR_CRUISER = 10;

    /**
     * Количество очков, начисляемых за попадание
     */
    private static final int SCORES_FOR_HIT = 1;

    /**
     * Мощность снаряда крейсера противника
     */
    private static final int ENEMY_CRUISER_SHELL_POWER = 10;

    /**
     * Мощность снаряда истребителя противника
     */
    private static final int ENEMY_FIGHTER_SHELL_POWER = 2;

    /**
     * Начальное значение мощности защиты игрока
     */
    public static final int PLAYER_INIT_POWER = 700;

    /**
     * Начальное значение мощности защиты вражеского истребителя
     */
    public static final int ENEMY_FIGHTER_INIT_POWER = 10;

    /**
     * Начальное значение мощности защиты вражеского крейсера
     */
    public static final int ENEMY_CRUISER_INIT_POWER = 200;

    /**
     * Количество ячеек, описывающих один спрайт в массиве
     */
    private static final int SPRITEDATALENGTH = 10;

    /**
     * Уничтоженный крейсер противника, повернутый влево
     */
    public static final int SPRITE_ENEMY_CRUISER_DESTROYED_LEFT = 0;

    /**
     * Уничтоженный крейсер противника, повернутый вправо
     */
    public static final int SPRITE_ENEMY_CRUISER_DESTROYED_RIGHT = 1;

    /**
     * Крейсер противника, уходящий в гиперпространство, повернутый влево
     */
    public static final int SPRITE_ENEMY_CRUISER_TO_HYPERJUMP_LEFT = 2;

    /**
     * Крейсер противника, уходящий в гиперпространство, повернутый вправо
     */
    public static final int SPRITE_ENEMY_CRUISER_TO_HYPERJUMP_RIGHT = 3;

    /**
     * Крейсер противника, выходящий из гиперпространства, повернутый влево
     */
    public static final int SPRITE_ENEMY_CRUISER_FROM_HYPERJUMP_LEFT = 4;

    /**
     * Крейсер противника, выходящий из гиперпространства, повернутый вправо
     */
    public static final int SPRITE_ENEMY_CRUISER_FROM_HYPERJUMP_RIGHT = 5;

    /**
     * Крейсер противника, повернутый влево
     */
    public static final int SPRITE_ENEMY_CRUISER_LEFT = 6;

    /**
     * Крейсер противника, повернутый вправо
     */
    public static final int SPRITE_ENEMY_CRUISER_RIGHT = 7;

    /**
     * Истребитель противника на максимальной дистанции, фронтальный вид
     */
    public static final int SPRITE_ENEMY_FRONT_FIGHTER_LONG = 8;

    /**
     * Истребитель противника на средней дистанции, фронтальный вид
     */
    public static final int SPRITE_ENEMY_FRONT_FIGHTER_MIDDLE = 9;

    /**
     * Истребитель противника на ближней дистанции, фронтальный вид
     */
    public static final int SPRITE_ENEMY_FRONT_FIGHTER_NEAR = 10;

    /**
     * Истребитель противника на дальней дистанции, задний вид
     */
    public static final int SPRITE_ENEMY_BACK_FIGHTER_LONG = 11;

    /**
     * Истребитель противника на средней дистанции, задний вид
     */
    public static final int SPRITE_ENEMY_BACK_FIGHTER_MIDDLE = 12;

    /**
     * Истребитель противника на ближней дистанции, задний вид
     */
    public static final int SPRITE_ENEMY_BACK_FIGHTER_NEAR = 13;

    /**
     * Взрыв на дальней дистанции
     */
    public static final int SPRITE_EXPLOSION_LONG = 14;

    /**
     * Взрыв на средней дистанции
     */
    public static final int SPRITE_EXPLOSION_MIDDLE = 15;

    /**
     * Взрыв на ближней дистанции
     */
    public static final int SPRITE_EXPLOSION_NEAR = 16;

    /**
     * Снаряд игрока на ближней дистанции
     */
    public static final int SPRITE_PLAYER_SHELL_NEAR = 17;

    /**
     * Снаряд игрока на средней дистанции
     */
    public static final int SPRITE_PLAYER_SHELL_MIDDLE = 18;

    /**
     * Снаряд игрока на дальней дистанции
     */
    public static final int SPRITE_PLAYER_SHELL_LONG = 19;

    /**
     * Прицел игрока
     */
    public static final int SPRITE_PLAYER_SIGN = 20;

    /**
     * Обломки на ближней дистанции
     */
    public static final int SPRITE_PARTS_NEAR = 21;

    /**
     * Обломки на средней дистанции
     */
    public static final int SPRITE_PARTS_MIDDLE = 22;

    /**
     * Обломки на дальней дистанции
     */
    public static final int SPRITE_PARTS_LONG = 23;

    /**
     * Снаряд противника на дальней дистанции
     */
    public static final int SPRITE_ENEMY_SHELL_LONG = 24;

    /**
     * Снаряд противника на средней дистанции
     */
    public static final int SPRITE_ENEMY_SHELL_MIDDLE = 25;

    /**
     * Снаряд противника на ближней дистанции
     */
    public static final int SPRITE_ENEMY_SHELL_NEAR = 26;

    /**
     * Орудие игрока, левое
     */
    public static final int SPRITE_PLAYER_BARREL_LEFT = 27;

    /**
     * Орудие игрока, нижнее
     */
    public static final int SPRITE_PLAYER_BARREL_BOTTOM = 28;

    /**
     * Орудие игрока, правое
     */
    public static final int SPRITE_PLAYER_BARREL_RIGHT = 29;


    /**
     * Флаг, показывающий что игра поддерживает перезапуск игрового уровня (фазы)
     */
    protected static final int FLAG_SUPPORTRESTART = 1;
    /**
     * Флаг, показывающий что игра поддерживает игровые уровни (фазы)
     */
    protected static final int FLAG_STAGESUPPORT = 2;

    public static final int getSupportedModes()
    {
        return 0;
    }

    private static final void setFighterDirection(Sprite _fighter)
    {
        PathController p_pathcontr = ap_FighterPaths[_fighter.i_spriteID];

        int i_dx = p_pathcontr.i8_dx >> 9;
        int i_dy = p_pathcontr.i8_dy >> 9;

        int i_result = 1;

        if (i_dy == 0) i_result += 3;
        else if (i_dy > 0) i_result += 6;

        if (i_dx < 0) i_result--;
        else if (i_dx > 0) i_result++;

        _fighter.i_Frame = i_result;
    }

    protected static final Object loadArray(Class _class,String _resource) throws Exception
    {
        final int ARRAY_BYTE = 0;
        final int ARRAY_CHAR = 1;
        final int ARRAY_SHORT = 2;
        final int ARRAY_INT = 3;
        final int ARRAY_LONG = 4;

        DataInputStream p_instr = new DataInputStream(_class.getResourceAsStream(_resource));

        byte[] ab_byteArr = null;
        char[] ach_charArr = null;
        short[] ash_shortArr = null;
        int[] ai_intArr = null;
        long[] al_longArr = null;

        int i_type = p_instr.readUnsignedByte();
        int i_length = p_instr.readUnsignedShort();
        int i_byteSize = p_instr.readUnsignedByte();

        //System.out.println("type "+i_type);
        //System.out.println("len "+i_length);
        //System.out.println("bsize "+i_byteSize);

        switch (i_type)
        {
            case ARRAY_BYTE:
                {
                    ab_byteArr = new byte[i_length];
                }
        ;
                break;
            case ARRAY_CHAR:
                {
                    ach_charArr = new char[i_length];
                }
        ;
                break;
            case ARRAY_SHORT:
                {
                    ash_shortArr = new short[i_length];
                }
        ;
                break;
            case ARRAY_INT:
                {
                    ai_intArr = new int[i_length];
                }
        ;
                break;
            case ARRAY_LONG:
                {
                    al_longArr = new long[i_length];
                }
        ;
                break;
        }

        if (p_instr.readUnsignedByte() == 1)
        {
            // Пакованные данные
            int i_index = 0;
            while (i_index < i_length)
            {
                int i_len = p_instr.readUnsignedByte();
                long l_val = 0;
                switch (i_byteSize)
                {
                    case 1:
                        {
                            l_val = p_instr.readByte();
                        }
                ;
                        break;
                    case 2:
                        {
                            l_val = p_instr.readShort();
                        }
                ;
                        break;
                    case 4:
                        {
                            l_val = p_instr.readInt();
                        }
                ;
                        break;
                    case 8:
                        {
                            l_val = p_instr.readLong();
                        }
                ;
                        break;
                }

                int li = 0;

                while (i_len>=0)
                {
                    switch (i_type)
                    {
                        case ARRAY_BYTE:
                            {
                                ab_byteArr [i_index] = (byte) l_val;
                            }
                    ;
                            break;
                        case ARRAY_CHAR:
                            {
                                ach_charArr [i_index] = (char) l_val;
                            }
                    ;
                            break;
                        case ARRAY_SHORT:
                            {
                                ash_shortArr [i_index] = (short) l_val;
                            }
                    ;
                            break;
                        case ARRAY_INT:
                            {
                                ai_intArr [i_index] = (int) l_val;
                            }
                    ;
                            break;
                        case ARRAY_LONG:
                            {
                                al_longArr [i_index] = l_val;
                            }
                    ;
                            break;
                    }

                    i_index++;
                    i_len--;
                }
            }
        }
        else
        {
            int i_index = 0;
            while (i_index < i_length)
            {
                long l_val = 0;
                switch (i_byteSize)
                {
                    case 1:
                        {
                            l_val = p_instr.readByte();
                        }
                ;
                        break;
                    case 2:
                        {
                            l_val = p_instr.readShort();
                        }
                ;
                        break;
                    case 4:
                        {
                            l_val = p_instr.readInt();
                        }
                ;
                        break;
                    case 8:
                        {
                            l_val = p_instr.readLong();
                        }
                ;
                        break;
                }


                    switch (i_type)
                    {
                        case ARRAY_BYTE:
                            {
                                ab_byteArr [i_index] = (byte) l_val;
                            }
                    ;
                            break;
                        case ARRAY_CHAR:
                            {
                                ach_charArr [i_index] = (char) l_val;
                            }
                    ;
                            break;
                        case ARRAY_SHORT:
                            {
                                ash_shortArr [i_index] = (short) l_val;
                            }
                    ;
                            break;
                        case ARRAY_INT:
                            {
                                ai_intArr [i_index] = (int) l_val;
                            }
                    ;
                            break;
                        case ARRAY_LONG:
                            {
                                al_longArr [i_index] = l_val;
                            }
                    ;
                            break;
                    }

                    i_index++;
            }
        }

        switch (i_type)
        {
            case ARRAY_BYTE:
                {
                    return ab_byteArr;
                }
            case ARRAY_CHAR:
                {
                    return ach_charArr;
                }
            case ARRAY_SHORT:
                {
                    return ash_shortArr;
                }
            case ARRAY_INT:
                {
                    return ai_intArr;
                }
            case ARRAY_LONG:
                {
                    return al_longArr;
                }
        }

        return null;
    }


}
