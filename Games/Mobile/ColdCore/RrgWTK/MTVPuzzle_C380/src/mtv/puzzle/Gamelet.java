package mtv.puzzle;

import java.util.Random;
import java.io.*;

/**
 * Класс описывает игру "Пазлы" с тремя уровнями и многоуровневой системой выигрыша (возможность неоднозначных комбиннаций выигрывших элементов).
 * @author  Игорь Мазница
 * @version 1.00
 * @since 01-FEB-2004
 * (С) 2003-2005 Raydac Research Group Ltd.
 */
public class Gamelet
{
    //---------------------------------------------------------
    /**
     * Количество элементов на одной стороне для слабого уровня
     */
    private static final int LEVEL_EASY_SIDEPARTS = 3;
    /**
     * Количество элементов на одной стороне для обычного уровня
     */
    private static final int LEVEL_NORMAL_SIDEPARTS = 4;

    /**
     * Количество элементов на одной стороне для сложного уровня
     */
    private static final int LEVEL_HARD_SIDEPARTS = 5;

    /**
     * Количество шагов при перемешивании для простого уровня
     */
    private static final int LEVEL_EASY_MIXSTEPS = 16;

    /**
     * Количество шагов при перемешивании для нормального уровня
     */
    private static final int LEVEL_NORMAL_MIXSTEPS = 46;

    /**
     * Количество шагов при перемешивании для сложного уровня
     */
    private static final int LEVEL_HARD_MIXSTEPS = 108;

    /**
     * Индекс пустого поля для простого уровня
     */
    private static final int LEVE_EASY_EMPTYINDEX = 0;

    /**
     * Индекс пустого поля для нормального уровня
     */
    private static final int LEVE_NORMAL_EMPTYINDEX = 0;

    /**
     * Индекс пустого поля для сложного уровня
     */
    private static final int LEVE_HARD_EMPTYINDEX = 0;


    /**
     * Флаг для пустой кнопки
     */
    public static final int KEY_NONE = 0;

    /**
     * Флаг кнопки ВВЕРХ
     */
    public static final int KEY_UP = 1;

    /**
     * Флаг кнопки ВНИЗ
     */
    public static final int KEY_DOWN = 2;

    /**
     * Флаг кнопки ВЛЕВО
     */
    public static final int KEY_LEFT = 4;

    /**
     * Флаг кнопки ВПРАВО
     */
    public static final int KEY_RIGHT = 8;

    /**
     * Игровое событие на проигрывание звука движения блока
     */
    public static final int GAMEACTION_SOUND_MOVEBLOCK = 0;

    /**
     * Игровое событие на проигрывание звука выигрышной ситуации
     */
    public static final int GAMEACTION_SOUND_WIN = 1;

    /**
     * Игровое событие на проигрывание звука отсутствия возможности движения блока
     */
    public static final int GAMEACTION_SOUND_CANTMOVE = 2;

    /**
     * Таблица выигрышных состояний для простого уровня
     */
    private static final byte [][] WINELEMENTS_EASY = new byte[][]
    {
        //#if VENDOR=="MOTOROLA" && MODEL=="C380"
        new byte[]{0},
        new byte[]{1},
        new byte[]{2},
        new byte[]{3},
        new byte[]{4},
        new byte[]{5},
        new byte[]{6},
        new byte[]{7},
        new byte[]{8}
        //#else
        //#if VENDOR=="MOTOROLA" && MODEL=="E398"
        //$new byte[]{0},
        //$new byte[]{1,2},
        //$new byte[]{2,1},
        //$new byte[]{3},
        //$new byte[]{4},
        //$new byte[]{5},
        //$new byte[]{6},
        //$new byte[]{7},
        //$new byte[]{8}
        //#endif
        //#endif
    };

    /**
     * Таблица выигрышных состояний для нормального уровня
     */
    private static final byte [][] WINELEMENTS_NORMAL = new byte[][]
    {
        //#if VENDOR=="MOTOROLA" && MODEL=="C380"
        new byte[]{0},
        new byte[]{1},
        new byte[]{2},
        new byte[]{3},
        new byte[]{4,7,8,12},
        new byte[]{5},
        new byte[]{6},
        new byte[]{7,4,8,12},
        new byte[]{8,7,4,12},
        new byte[]{9},
        new byte[]{10},
        new byte[]{11},
        new byte[]{12,7,8,4},
        new byte[]{13},
        new byte[]{14},
        new byte[]{15}
        //#else
        //#if VENDOR=="MOTOROLA" && MODEL=="E398"
        //$new byte[]{0},
        //$new byte[]{1},
        //$new byte[]{2},
        //$new byte[]{3},
        //$new byte[]{4,7},
        //$new byte[]{5},
        //$new byte[]{6},
        //$new byte[]{7,4},
        //$new byte[]{8},
        //$new byte[]{9},
        //$new byte[]{10},
        //$new byte[]{11},
        //$new byte[]{12},
        //$new byte[]{13},
        //$new byte[]{14},
        //$new byte[]{15}
        //#endif
        //#endif
    };

    /**
     * Таблица выигрышных состояний для сложного уровня
     */
    private static final byte [][] WINELEMENTS_HARD = new byte[][]
    {
        //#if VENDOR=="MOTOROLA" && MODEL=="C380"
        new byte[]{0},
        new byte[]{1},
        new byte[]{2},
        new byte[]{3},
        new byte[]{4},
        new byte[]{5},
        new byte[]{6},
        new byte[]{7},
        new byte[]{8},
        new byte[]{9},
        new byte[]{10},
        new byte[]{11},
        new byte[]{12},
        new byte[]{13},
        new byte[]{14},
        new byte[]{15},
        new byte[]{16},
        new byte[]{17},
        new byte[]{18},
        new byte[]{19},
        new byte[]{20},
        new byte[]{21},
        new byte[]{22},
        new byte[]{23},
        new byte[]{24}
        //#else
        //#if VENDOR=="MOTOROLA" && MODEL=="E398"
        //$new byte[]{0},
        //$new byte[]{1},
        //$new byte[]{2},
        //$new byte[]{3},
        //$new byte[]{4},
        //$new byte[]{5},
        //$new byte[]{6},
        //$new byte[]{7},
        //$new byte[]{8},
        //$new byte[]{9},
        //$new byte[]{10},
        //$new byte[]{11},
        //$new byte[]{12},
        //$new byte[]{13},
        //$new byte[]{14},
        //$new byte[]{15},
        //$new byte[]{16},
        //$new byte[]{17},
        //$new byte[]{18},
        //$new byte[]{19},
        //$new byte[]{20},
        //$new byte[]{21},
        //$new byte[]{22},
        //$new byte[]{23},
        //$new byte[]{24}
        //#endif
        //#endif
    };

    /**
     * Массив содержащий спрайты пазлов
     */
    public static Sprite [] ap_Sprites;

    /**
     * Спрайт левого указателя перемещения блока
     */
    public static Sprite p_arrowLeft;

    /**
     * Спрайт правого указателя перемещения блока
     */
    public static Sprite p_arrowRight;

    /**
     * Спрайт верхнего указателя перемещения блока
     */
    public static Sprite p_arrowTop;

    /**
     * Спрайт нижнего указателя перемещения блока
     */
    public static Sprite p_arrowDown;

    /**
     * Спрайт указателя пустой ячейки
     */
    public static Sprite p_Pointer;

    /**
     * Количество блоков по одному измерению для текущего уровня
     */
    public static int i_CurrentSidePartsNumber;

    /**
     * Индекс пустой ячейки для текущего уровня сложности
     */
    public static int i_EmptyPartIndex;

    /**
     * Текущая комбинация ячеек
     */
    private static byte [] ab_CurrentCombination;

    /**
     * Текущая выигрышная комбинация для выбранного уровня сложности
     */
    private static byte [][] ab_currentWinCombination;

    /**
     * Скорость перемещения блока по горизонтали
     */
    //#if VENDOR=="MOTOROLA" && MODEL=="C380"
    private static final int I8_STEPX = 0x410;
    /**
     * Скорость перемещения блока по вертикали
     */
    private static final int I8_STEPY = 0x410;
    //#else
    //#if VENDOR=="MOTOROLA" && MODEL=="E398"
    //$private static final int I8_STEPX = 0x710;
    //$private static final int I8_STEPY = 0x710;
    //#endif
    //#endif

    /**
     * Флаг показывает что игра в режиме перемещения блока
     */
    private static boolean lg_AnimationMode;


    /**
     * Индекс спрайта перемещаемого блока
     */
    private static int i_AnimatedBlockIndex;

    /**
     * Текущая координата X пустой ячейки
     */
    private static int i_PointerCellX;

    /**
     * Текущая координата Y пустой ячейки
     */
    private static int i_PointerCellY;

    /**
     * Координата назначения X пустой ячейки
     */
    private static int i_nextPointerCellX;

    /**
     * Координата назначения Y пустой ячейки
     */
    private static int i_nextPointerCellY;

    /**
     * Количество шагов при перемешивании ячеек для выбранного уровня сложности
     */
    private static int i_CurrentMixSteps;

    /**
     * Счетчик ходов игрока
     */
    private static int i_PlayerMoveCounter;

    /**
     * Массив параметров игровых спрайтов
     */
    private static final int[] ai_SpriteParameters = new int[]
    {
        // Width, Height,ColOffsetX, ColOffsetY, ColWidth, ColHeight, Frames, FrameDelay, Main point, Animation
        //#if VENDOR=="MOTOROLA" && MODEL=="C380"
            //SPRITE FOR EASY MODE
            0x2A00, 0x2600, 1, 1, Sprite.SPRITE_ALIGN_LEFT|Sprite.SPRITE_ALIGN_TOP, Sprite.ANIMATION_FROZEN,
            //SPRITE FOR NORMAL MODE
            0x2000, 0x1D00, 1, 1, Sprite.SPRITE_ALIGN_LEFT|Sprite.SPRITE_ALIGN_TOP, Sprite.ANIMATION_FROZEN,
            ////SPRITE FOR HARD MODE
            0x1900, 0x1700, 1, 1, Sprite.SPRITE_ALIGN_LEFT|Sprite.SPRITE_ALIGN_TOP, Sprite.ANIMATION_FROZEN,
            //SPRITE ARROW LEFT
            0xD00, 0xD00,  3, 2, Sprite.SPRITE_ALIGN_RIGHT|Sprite.SPRITE_ALIGN_CENTER, Sprite.ANIMATION_PENDULUM,
            //SPRITE ARROW TOP
            0xD00, 0xD00,  3, 2, Sprite.SPRITE_ALIGN_DOWN|Sprite.SPRITE_ALIGN_CENTER, Sprite.ANIMATION_PENDULUM,
            //SPRITE ARROW DOWN
            0xD00, 0xD00,  3, 2, Sprite.SPRITE_ALIGN_TOP|Sprite.SPRITE_ALIGN_CENTER, Sprite.ANIMATION_PENDULUM,
            //SPRITE ARROW RIGHT
            0xD00, 0xD00,  3, 2, Sprite.SPRITE_ALIGN_LEFT|Sprite.SPRITE_ALIGN_CENTER, Sprite.ANIMATION_PENDULUM,
            //SPRITE POINTEREASY
            0x2A00, 0x2600, 3, 2, Sprite.SPRITE_ALIGN_LEFT|Sprite.SPRITE_ALIGN_TOP, Sprite.ANIMATION_CYCLIC,
            //SPRITE POINTERNORMAL
            0x2000, 0x1D00, 3, 2, Sprite.SPRITE_ALIGN_LEFT|Sprite.SPRITE_ALIGN_TOP, Sprite.ANIMATION_CYCLIC,
            //SPRITE POINTERHARD
            0x1900, 0x1700, 3, 2, Sprite.SPRITE_ALIGN_LEFT|Sprite.SPRITE_ALIGN_TOP, Sprite.ANIMATION_CYCLIC,
        //#else
            //#if VENDOR=="MOTOROLA" && MODEL=="E398"
            //SPRITE FOR EASY MODE
            //$0x3A00, 0x4400, 1, 1, Sprite.SPRITE_ALIGN_LEFT|Sprite.SPRITE_ALIGN_TOP, Sprite.ANIMATION_FROZEN,
            //SPRITE FOR NORMAL MODE
            //$0x2C00, 0x3300, 1, 1, Sprite.SPRITE_ALIGN_LEFT|Sprite.SPRITE_ALIGN_TOP, Sprite.ANIMATION_FROZEN,
            ////SPRITE FOR HARD MODE
            //$0x2300, 0x2800, 1, 1, Sprite.SPRITE_ALIGN_LEFT|Sprite.SPRITE_ALIGN_TOP, Sprite.ANIMATION_FROZEN,
            //SPRITE ARROW LEFT
            //$0xD00, 0xD00,  5, 1, Sprite.SPRITE_ALIGN_RIGHT|Sprite.SPRITE_ALIGN_CENTER, Sprite.ANIMATION_PENDULUM,
            //SPRITE ARROW TOP
            //$0xD00, 0xD00,  5, 1, Sprite.SPRITE_ALIGN_DOWN|Sprite.SPRITE_ALIGN_CENTER, Sprite.ANIMATION_PENDULUM,
            //SPRITE ARROW DOWN
            //$0xD00, 0xD00,  5, 1, Sprite.SPRITE_ALIGN_TOP|Sprite.SPRITE_ALIGN_CENTER, Sprite.ANIMATION_PENDULUM,
            //SPRITE ARROW RIGHT
            //$0xD00, 0xD00,  5, 1, Sprite.SPRITE_ALIGN_LEFT|Sprite.SPRITE_ALIGN_CENTER, Sprite.ANIMATION_PENDULUM,
            //SPRITE POINTEREASY
            //$0x3A00, 0x4400, 3, 1, Sprite.SPRITE_ALIGN_LEFT|Sprite.SPRITE_ALIGN_TOP, Sprite.ANIMATION_CYCLIC,
            //SPRITE POINTERNORMAL
            //$0x2C00, 0x3300, 3, 1, Sprite.SPRITE_ALIGN_LEFT|Sprite.SPRITE_ALIGN_TOP, Sprite.ANIMATION_CYCLIC,
            //SPRITE POINTERHARD
            //$0x2300, 0x2800, 3, 1, Sprite.SPRITE_ALIGN_LEFT|Sprite.SPRITE_ALIGN_TOP, Sprite.ANIMATION_CYCLIC,
            //#else
            //#assert "---Unsupported device---"
            //#endif
        //#endif
    };

    /**
     * Спрайт блока для простого режима
     */
    public static final int SPRITE_PARTEASYMODE = 0;

    /**
     * Спрайт блока для обычного режима
     */
    public static final int SPRITE_PARTNORMALMODE = 1;

    /**
     * Спрайт блока для сложного режима
     */
    public static final int SPRITE_PARTHARDMODE = 2;

    /**
     * Спрайт левой стрелки
     */
    public static final int SPRITE_ARROWLEFT = 3;

    /**
     * Спрайт верхней стрелки
     */
    public static final int SPRITE_ARROWTOP = 4;

    /**
     * Спрайт нижней стрелки
     */
    public static final int SPRITE_ARROWDOWN = 5;

    /**
     * Спрайт правой стрелки
     */
    public static final int SPRITE_ARROWRIGHT = 6;

    /**
     * Спрайт указателя пустой ячейки для простого режима
     */
    public static final int SPRITE_POINTEREASY = 7;

    /**
     * Спрайт указателя пустой ячейки для нормального режима
     */
    public static final int SPRITE_POINTERNORMAL = 8;

    /**
     * Спрайт указателя пустой ячейки для сложного режима
     */
    public static final int SPRITE_POINTERHARD = 9;

    /**
     * Ширина спрайта для текущего режима
     */
    public static int i8_PartWidth;

    /**
     * Высота спрайта для текущего режима
     */
    public static int i8_PartHeight;
    //---------------------------------------------------------


    /**
     * Интерфейс определяет функции слушателя игровых сообщений
     */
    public interface GameActionListener
    {
        /**
            Отработка игрового события, с одним числовым параметром и числовым возвращаемым значением
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
     * @return true если инициализация прошла успешно, иначе false.
     */
    public static final boolean init()
    {
        if (i_GameState != STATE_UNKNON) return false;
        p_actionListener = null;
        i_GameAreaHeight = -1;
        i_GameAreaWidth = -1;

        //------------Вставьте свой код здесь--------------------
        ap_Sprites = new Sprite[LEVEL_HARD_SIDEPARTS*LEVEL_HARD_SIDEPARTS];
        ab_CurrentCombination = new byte[LEVEL_HARD_SIDEPARTS*LEVEL_HARD_SIDEPARTS];
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
        ap_Sprites = null;
        p_Pointer = null;
        p_arrowDown = null;
        p_arrowLeft = null;
        p_arrowRight = null;
        p_arrowTop = null;
        //--------------------------------------------------

        i_GameAreaHeight = -1;
        i_GameAreaWidth = -1;
        p_actionListener = null;

        setState(STATE_UNKNON);
    }

    /**
     * Функция устанавливает состояние игры.
     * @param _state новое состояние игры.
     */
    private static final void setState(int _state)
    {
        i_PrevGameState = i_GameState;
        i_GameState = _state;
    }

    /**
     * Функция генерирует и возвращает псевдослучайное числовое значение в заданном пределе (включительно).
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
     * @param _gameAreaWidth величина ширины игровой зоны в пикселях
     * @param _gameAreaHeight величина высоты игровой зоны в пикселях
     * @param _gameLevel сложность игровой сессии
     * @param _actionListener слушатель игровых событий
     * @return true если инициализация игровой сессии прошла успешно, иначе false.
     */
    public static final boolean initNewGame(final int _gameAreaWidth,final int _gameAreaHeight,final int _gameLevel,final GameActionListener _actionListener)
    {
        if (i_GameState != STATE_INITED) return false;
        p_actionListener = _actionListener;
        i_GameAreaHeight = _gameAreaHeight;
        i_GameAreaWidth = _gameAreaWidth;
        i_GameLevel = _gameLevel;
        initPlayerForGame(true);
        //------------Вставьте свой код здесь--------------------
        i_GameStepDelay = 80;
        int i_partSprite = 0;
        int i_pointerSprite = 0;
        i_CurrentMixSteps  = 0;
        switch(i_GameLevel)
        {
            case GAMELEVEL_EASY :
                {
                    i_CurrentSidePartsNumber = LEVEL_EASY_SIDEPARTS;
                    i_EmptyPartIndex = LEVE_EASY_EMPTYINDEX;
                    i_partSprite = SPRITE_PARTEASYMODE;
                    i_pointerSprite = SPRITE_POINTEREASY;
                    ab_currentWinCombination = WINELEMENTS_EASY;
                    i_CurrentMixSteps = LEVEL_EASY_MIXSTEPS;
                };break;

            case GAMELEVEL_NORMAL :
                {
                    i_CurrentSidePartsNumber = LEVEL_NORMAL_SIDEPARTS;
                    i_EmptyPartIndex = LEVE_NORMAL_EMPTYINDEX;
                    i_partSprite = SPRITE_PARTNORMALMODE;
                    i_pointerSprite = SPRITE_POINTERNORMAL;
                    ab_currentWinCombination = WINELEMENTS_NORMAL;
                    i_CurrentMixSteps = LEVEL_NORMAL_MIXSTEPS;
                };break;

            case GAMELEVEL_HARD :
                {
                    i_CurrentSidePartsNumber = LEVEL_HARD_SIDEPARTS;
                    i_EmptyPartIndex = LEVE_HARD_EMPTYINDEX;
                    i_partSprite = SPRITE_PARTHARDMODE;
                    i_pointerSprite = SPRITE_POINTERHARD;
                    ab_currentWinCombination = WINELEMENTS_HARD;
                    i_CurrentMixSteps = LEVEL_HARD_MIXSTEPS;
                };break;
        }

        int i_parts = i_CurrentSidePartsNumber*i_CurrentSidePartsNumber;

        for(int li=0;li<i_parts;li++)
        {
            ap_Sprites[li] = new Sprite(li);
            activateSprite(ap_Sprites[li] ,i_partSprite);
            if (li == i_EmptyPartIndex) ap_Sprites[li].lg_SpriteInvisible = true;
        }

        i8_PartWidth = ap_Sprites[0].i_width;
        i8_PartHeight = ap_Sprites[0].i_height;

        p_arrowDown = new Sprite(0);
        p_arrowTop = new Sprite(0);
        p_arrowLeft = new Sprite(0);
        p_arrowRight = new Sprite(0);

        activateSprite(p_arrowDown,SPRITE_ARROWDOWN);
        activateSprite(p_arrowTop,SPRITE_ARROWTOP);
        activateSprite(p_arrowLeft,SPRITE_ARROWLEFT);
        activateSprite(p_arrowRight,SPRITE_ARROWRIGHT);

        p_Pointer = new Sprite(i_pointerSprite);
        activateSprite(p_Pointer,i_pointerSprite);

        // Заполняем массив
        int i_x = 0;
        int i_y = 0;
        for(int li=0;li<ab_CurrentCombination.length;li++)
        {
            ab_CurrentCombination[li] = (byte)li;
            if (li == i_EmptyPartIndex)
            {
                i_PointerCellX = i_x;
                i_PointerCellY = i_y;
            }
        }

        i_PlayerMoveCounter = 0;

        lg_AnimationMode = false;

        // Перемешиваем
        mixGameArray(i_CurrentMixSteps);

        // Инициализируем спрайты из комбинации
        fillSpritesForCombination();

        showCursor();
        //--------------------------------------------------

        setState(STATE_STARTED);
        return true;
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
        for(int li=0;li<ap_Sprites.length;li++) ap_Sprites[li] = null;

        p_Pointer = null;
        p_arrowDown = null;
        p_arrowLeft = null;
        p_arrowRight = null;
        p_arrowTop = null;
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
            //------------Вставьте свой код здесь---------------

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
        else
        if (i_GameState == STATE_PAUSED)
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
     * @return значение количества очков игрока.
     */
    public static final int getPlayerScore()
    {
        //------------Вставьте свой код здесь--------------------
        int i_scores = 77*((i_GameLevel+1)*(i_GameLevel+1))+i_CurrentMixSteps-i_PlayerMoveCounter;
        if (i_scores<0) i_scores = 0;
        //--------------------------------------------------
        return i_scores;
    }

    /**
     * Инициализация игрока после смерти или при инициализации игровой сессии
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
     * @param _data массив байт, описывающих состояние
     * @throws Exception если произошла ошибка при загрузке состояния или игра находилась в состоянии несовместимом с загрузкой.
     */
    public static final void loadGameStateFromByteArray(final byte [] _data,GameActionListener _actionListener) throws Exception
    {
        if (i_GameState != STATE_INITED) throw new Exception();
        ByteArrayInputStream p_arrayInputStream = new ByteArrayInputStream(_data);
        DataInputStream p_inputStream = new DataInputStream(p_arrayInputStream);
        int i_gameLevel = p_inputStream.readUnsignedByte();
        int i_gameStage = p_inputStream.readUnsignedByte();
        int i_gameScreenWidth = p_inputStream.readUnsignedShort();
        int i_gameScreenHeight = p_inputStream.readUnsignedShort();
        if (!initNewGame(i_gameScreenWidth,i_gameScreenHeight,i_gameLevel,_actionListener)) throw new Exception();
        if (!initGameStage(i_gameStage)) throw new Exception();
        i_PlayerAttemptions = p_inputStream.readInt();
        //------------Вставьте свой код здесь--------------------
        p_inputStream.read(ab_CurrentCombination);
        i_PlayerMoveCounter = p_inputStream.readInt();
        fillSpritesForCombination();
        lg_AnimationMode = false;
        showCursor();
        //--------------------------------------------------
        p_inputStream.close();
        p_arrayInputStream = null;
        p_inputStream = null;
        Runtime.getRuntime().gc();
    }

    /**
     * Функция формирует блок данных, описывающий текущее игровое состояние
     * @return байтовый массив, содержащий записанное состояние игрового процесса
     * @throws Exception если игра или игрок находится в несовместимом состоянии, произошла ошибка сохранения или сформированный массив неправильного размера
     */
    public static final byte [] saveGameStateToByteArray() throws Exception
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
        p_outputStream.write(ab_CurrentCombination);
        p_outputStream.writeInt(i_PlayerMoveCounter);
        //--------------------------------------------------
        p_outputStream.close();
        p_outputStream = null;
        byte [] ab_result = p_arrayOutputStream.toByteArray();
        p_arrayOutputStream = null;
        if (ab_result.length != getGameStateDataBlockSize()) throw new Exception();
        Runtime.getRuntime().gc();
        return ab_result;
    }

    /**
     * Функция возвращает размер, требуемый для сохранения блока игровых данных.
     * @return требуемый размер блока данных.
     */
    public static final int getGameStateDataBlockSize()
    {
        int MINIMUM_SIZE = 10;

        MINIMUM_SIZE += (LEVEL_HARD_SIDEPARTS*LEVEL_HARD_SIDEPARTS);
        MINIMUM_SIZE += 4;

        return MINIMUM_SIZE;
    }

    /**
     * Возвращает текстовый идентификатор игры
     * @return строка, идентифицирующая игру.
     */
    public static final String getID()
    {
        return "mtvpzzl";
    }

    /**
     * Функция отрабатывает игровой шаг
     * @param _keyStateFlags флаги управления игроком.
     * @return статус игры после отработки игровой интерации
     */
    public static final int nextGameStep(int _keyStateFlags)
    {
        //------------Вставьте свой код здесь--------------------
        processAnimation();
        if (lg_AnimationMode)
        {
            if (processBlockMoveAnimation())
            {
                lg_AnimationMode = false;
                i_PointerCellX = i_nextPointerCellX;
                i_PointerCellY = i_nextPointerCellY;

                if (checkCombinationForWin())
                {
                    p_actionListener.processGameAction(GAMEACTION_SOUND_CANTMOVE);
                    setState(STATE_OVER);
                    i_PlayerState = PLAYER_WIN;
                }
                else
                {
                    p_Pointer.setMainPointXY(i_PointerCellX*i8_PartWidth,i_PointerCellY*i8_PartHeight);
                    showCursor();
                }
            }
        }
        else
        {
            if (checkCombinationForWin())
            {
                p_actionListener.processGameAction(GAMEACTION_SOUND_CANTMOVE);
                setState(STATE_OVER);
                i_PlayerState = PLAYER_WIN;
            }
            else
            {
            int i_posIndex = i_PointerCellX+i_PointerCellY*i_CurrentSidePartsNumber;
            if ((_keyStateFlags & KEY_DOWN)!=0)
            {
                if (!p_arrowTop.lg_SpriteInvisible)
                {
                    activateMoveBlock(i_posIndex,i_posIndex - i_CurrentSidePartsNumber);
                    i_PlayerMoveCounter++;
                }
                else
                {
                    p_actionListener.processGameAction(GAMEACTION_SOUND_CANTMOVE);
                }
            }
            else
            if ((_keyStateFlags & KEY_UP)!=0)
            {
                if (!p_arrowDown.lg_SpriteInvisible)
                {
                    activateMoveBlock(i_posIndex,i_posIndex + i_CurrentSidePartsNumber);
                    i_PlayerMoveCounter++;
                }
                else
                {
                    p_actionListener.processGameAction(GAMEACTION_SOUND_CANTMOVE);
                }
            }
            else
            if ((_keyStateFlags & KEY_LEFT)!=0)
            {
                if (!p_arrowRight.lg_SpriteInvisible)
                {
                    activateMoveBlock(i_posIndex,i_posIndex+1);
                    i_PlayerMoveCounter++;
                }
                else
                {
                    p_actionListener.processGameAction(GAMEACTION_SOUND_CANTMOVE);
                }
            }
            else
            if ((_keyStateFlags & KEY_RIGHT)!=0)
            {
                if (!p_arrowLeft.lg_SpriteInvisible)
                {
                    activateMoveBlock(i_posIndex,i_posIndex-1);
                    i_PlayerMoveCounter++;
                }
                else
                {
                    p_actionListener.processGameAction(GAMEACTION_SOUND_CANTMOVE);
                }
            }
            }
        }

        //--------------------------------------------------
        return i_GameState;
    }

    /**
     * Активизировать перемещаемый блок
     * @param _curPosIndex позиция в которой находится
     * @param _newIndex позиция в которую требуется переместить
     */
    private static final void activateMoveBlock(int _curPosIndex,int _newIndex)
    {
        hideCursor();
        int i_sprIndex = ab_CurrentCombination[_newIndex];

        byte b_a = ab_CurrentCombination[_curPosIndex];
        ab_CurrentCombination[_curPosIndex] = ab_CurrentCombination[_newIndex];
        ab_CurrentCombination[_newIndex] = b_a;

        i_AnimatedBlockIndex = i_sprIndex;

        lg_AnimationMode = true;
        Sprite p_spr = ap_Sprites[i_sprIndex];
        i_nextPointerCellX = p_spr.i_mainX / i8_PartWidth;
        i_nextPointerCellY = p_spr.i_mainY / i8_PartHeight;

        p_actionListener.processGameAction(GAMEACTION_SOUND_MOVEBLOCK);
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
        //#global STAGESUPPORT=false
        return 0;
    }

    /**
     * Функция активизирует заданный спрайт, загружая данными из массива спрайтов
     * @param _sprite     активизируемый спрайт
     * @param _actorIndex индекс загружаемых данных
     */
    private static void activateSprite(Sprite _sprite, int _actorIndex)
    {
        _sprite.i_ObjectType = _actorIndex;
        _actorIndex *= SPRITEDATALENGTH;

        int [] ai_sprParameters = ai_SpriteParameters;

        int i8_w = (int) ai_sprParameters[_actorIndex++];
        int i8_h = (int) ai_sprParameters[_actorIndex++];

        int i_f = ai_sprParameters[_actorIndex++];
        int i_fd = ai_sprParameters[_actorIndex++];
        int i_mp = ai_sprParameters[_actorIndex++];
        int i_an = ai_sprParameters[_actorIndex++];

        _sprite.setAnimation(i_an, i_mp, i8_w, i8_h, i_f, 0, i_fd);

        _sprite.lg_SpriteActive = true;
    }

    /**
     * Количество ячеек, описывающих один спрайт в массиве
     */
    private static final int SPRITEDATALENGTH = 6;

    /**
     * Спрятать указатель пустой клетки
     */
    private static final void hideCursor()
    {
        p_Pointer.lg_SpriteInvisible = true;
    }

    /**
     * Отобразить указатель пустой клетки
     */
    private static final void showCursor()
    {
        p_Pointer.lg_SpriteInvisible = false;
        int i8_mx = p_Pointer.i_mainX;
        int i8_my = p_Pointer.i_mainY;
        int i8_cx = p_Pointer.i_mainX+(p_Pointer.i_width>>1);
        int i8_cy = p_Pointer.i_mainY+(p_Pointer.i_height>>1);

        // Рассчитываем новые положения стрелок
        // Левая
        p_arrowLeft.setMainPointXY(i8_mx-0x100,i8_cy);
        p_arrowLeft.lg_SpriteInvisible = false;
        p_arrowRight.setMainPointXY(i8_mx+p_Pointer.i_width+0x100,i8_cy);
        p_arrowRight.lg_SpriteInvisible = false;
        p_arrowTop.setMainPointXY(i8_cx,i8_my-0x100);
        p_arrowTop.lg_SpriteInvisible = false;
        p_arrowDown.setMainPointXY(i8_cx,i8_my+p_Pointer.i_height+0x100);
        p_arrowDown.lg_SpriteInvisible = false;

        int i_X = i8_mx / i8_PartWidth;
        int i_Y = i8_my / i8_PartHeight;

        if (i_X==0) p_arrowLeft.lg_SpriteInvisible = true;
        else
        if (i_X==i_CurrentSidePartsNumber-1) p_arrowRight.lg_SpriteInvisible = true;

        if (i_Y==0) p_arrowTop.lg_SpriteInvisible = true;
        else
        if (i_Y==i_CurrentSidePartsNumber-1) p_arrowDown.lg_SpriteInvisible = true;
    }

    /**
     * Проверить текущую игровую комбинацию на выигрышность
     * @return true если выигрыш и false если проигрыш
     */
    private static final boolean checkCombinationForWin()
    {
        for(int li=0;li<ab_currentWinCombination.length;li++)
        {
            int i_index = ab_CurrentCombination[li];
            byte [] ab_comb = ab_currentWinCombination[li];
            boolean lg_good = false;
            for(int ll=0;ll<ab_comb.length;ll++)
            {
                if (ab_comb[ll]==i_index)
                {
                    lg_good = true;
                    break;
                }
            }
            if (!lg_good) return false;
        }
        return true;
    }

    /**
     * Отработка одного шага перемещения пазла
     * @return true если закончено и false если незакончено
     */
    private static final boolean processBlockMoveAnimation()
    {
        Sprite p_movSpr = ap_Sprites[i_AnimatedBlockIndex];
        int i8_srcX = p_movSpr.i_mainX;
        int i8_srcY = p_movSpr.i_mainY;
        int i8_destX = p_Pointer.i_mainX;
        int i8_destY = p_Pointer.i_mainY;

        if (i8_destY == i8_srcY && i8_destX == i8_srcX) return true;

        if (i8_destX<i8_srcX)
        {
            i8_srcX -= I8_STEPX;
            if (i8_srcX<i8_destX) i8_srcX = i8_destX;
        }
        else
            if (i8_destX>i8_srcX)
            {
                i8_srcX += I8_STEPX;
                if (i8_srcX>i8_destX) i8_srcX = i8_destX;
            }

        if (i8_destY<i8_srcY)
        {
            i8_srcY -= I8_STEPY;
            if (i8_srcY<i8_destY) i8_srcY = i8_destY;
        }
        else
            if (i8_destY>i8_srcY)
            {
                i8_srcY += I8_STEPY;
                if (i8_srcY>i8_destY) i8_srcY = i8_destY;
            }

        p_movSpr.setMainPointXY(i8_srcX,i8_srcY);


        return false;
    }

    /**
     * Инициализировать координаты и видимость спрайтов для текущей игровой комбинации
     */
    private static final void fillSpritesForCombination()
    {
        int i_elems = i_CurrentSidePartsNumber*i_CurrentSidePartsNumber;
        int i_x = 0;
        int i_y = 0;
        for(int li=0;li<i_elems;li++)
        {
            int i_index = ab_CurrentCombination[li];

            if (i_index == i_EmptyPartIndex)
            {
                p_Pointer.setMainPointXY(i8_PartWidth*i_x,i8_PartHeight*i_y);
                i_PointerCellX = i_x;
                i_PointerCellY = i_y;
            }
            else
            {
                Sprite p_spr = ap_Sprites[i_index];
                p_spr.setMainPointXY(i8_PartWidth*i_x,i8_PartHeight*i_y);
            }

            i_x ++;
            if (i_x == i_CurrentSidePartsNumber)
            {
                i_y++;
                i_x = 0;
            }
        }
    }

    /**
     * Перемешиваем игровой массив
     * @param _stepsNumber количество шагов
     */
    private static final void mixGameArray(int _stepsNumber)
    {
        byte [] ab_array = ab_CurrentCombination;

        int i_startX = i_PointerCellX;
        int i_startY = i_PointerCellY;

        final int MOVE_UP = 0;
        final int MOVE_DOWN = 1;
        final int MOVE_LEFT = 2;
        final int MOVE_RIGHT = 3;

        int i_forbiddenMove = -1;
        int i_prevMove = -1;

        while(_stepsNumber>0)
        {
            int i_curPos = i_startX+i_startY*i_CurrentSidePartsNumber;

            boolean lg_newMove = true;
            while(lg_newMove)
            {
               int i_direction = getRandomInt(39999)/10000;
               if (i_forbiddenMove == i_direction) continue;
               if (i_prevMove == i_direction) continue;
               switch(i_direction)
               {
                   case MOVE_UP :
                       {
                           if (i_startY == i_CurrentSidePartsNumber-1) continue;

                           int i_cp = i_curPos;
                           int i_np = i_curPos+i_CurrentSidePartsNumber;
                           byte b_acc = ab_array[i_np];
                           ab_array[i_np] = ab_array[i_cp];
                           ab_array[i_cp] = b_acc;
                           i_startY += 1;

                           i_forbiddenMove = MOVE_DOWN;
                       };break;
                   case MOVE_DOWN :
                       {
                           if (i_startY == 0) continue;

                           int i_cp = i_curPos;
                           int i_np = i_curPos-i_CurrentSidePartsNumber;
                           byte b_acc = ab_array[i_np];
                           ab_array[i_np] = ab_array[i_cp];
                           ab_array[i_cp] = b_acc;
                           i_startY -= 1;

                           i_forbiddenMove = MOVE_UP;
                       };break;
                   case MOVE_LEFT :
                       {
                           if (i_startX == i_CurrentSidePartsNumber-1) continue;

                           int i_cp = i_curPos;
                           int i_np = i_curPos+1;
                           byte b_acc = ab_array[i_np];
                           ab_array[i_np] = ab_array[i_cp];
                           ab_array[i_cp] = b_acc;
                           i_startX += 1;

                           i_forbiddenMove = MOVE_RIGHT;
                       };break;
                   case MOVE_RIGHT :
                       {
                           if (i_startX == 0) continue;

                           int i_cp = i_curPos;
                           int i_np = i_curPos-1;
                           byte b_acc = ab_array[i_np];
                           ab_array[i_np] = ab_array[i_cp];
                           ab_array[i_cp] = b_acc;
                           i_startX -= 1;
                           i_forbiddenMove = MOVE_LEFT;
                       };break;
               }
               i_prevMove = i_direction;
               lg_newMove = false;
            }

            i_PointerCellX = i_startX;
            i_PointerCellY = i_startY;

            _stepsNumber--;
        }
    }

    /**
     * Отработка анимации игровых объектов
     */
    private static final void processAnimation()
    {
        p_Pointer.processAnimation();
        p_arrowDown.processAnimation();
        p_arrowLeft.processAnimation();
        p_arrowRight.processAnimation();
        p_arrowTop.processAnimation();

        for(int li=0;li<ap_Sprites.length;li++)
        {
            if (ap_Sprites[li]!=null) ap_Sprites[li].processAnimation();
        }
    }
}
