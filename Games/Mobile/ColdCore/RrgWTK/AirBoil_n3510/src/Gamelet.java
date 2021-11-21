
import java.util.Random;
import java.io.*;

/**
 * Игра "Air boil" (Code name)
 *
 * @author Д.И.Рожнев, И.А.Мазница
 * @version 5.8
 * @since 01-AUG-2005
 *        (С) 2003-2005 Raydac Reserach Group Ltd.
 *        Сюжет игры 1.0:
 *        --------------
 *        Игрок управляет небольшим вертолетом, перемещающемся в пределах экрана, как по горизонтали, так и по вертикали,
 *        так же он может осуществлять одновременное перемещение как по оси X так и по оси Y, одновременно он может производить
 *        стрельбу по целям, стрельба производится один раз при каждом нажатии игрока на кнопку стрельбы. Движение вертолета
 *        производится непрерывно, если не нажата клавиша набора высоты, то вертолет плавно снижается, если вертолет достигнет
 *        уровня земной поверхности, то он взрывается. В качестве противников игрока выступают другие вертолеты, которые
 *        вылетают из-за краев экрана (исключая нижний край, так как там находится виртуальная поверхность земли) и
 *        перемещаются по экрану, отражаясь от его краев по алгоритму "мячика". Периодически они стреляют в игрока, производя
 *        выстрел с учетом направления движения игрока. Все (и игрок и противники) могут производить только горизонтальную
 *        стрельбу причем по направлению своего движения, т.е. если вертолет направлен вправо то он и стрелять может только
 *        вправо, а для выстрела влево ему потребуется развернуться, но делать это может только вертолет игрока так как
 *        вертолеты противников меняют направление только при касании границы экрана. При соприкосновении вертолета игрока с
 *        вертолетом противника, происходит уничтожение обоих вертолетов с отрисовкой взрывов на их месте, а с игрока снимается
 *        попытка. При соприкосновении вертолета игрока с  ракетой противника, производится уничтожение обоих объектов,
 *        на месте вертолета игрока рисуется взрыв, на месте ракеты рисовать взрыв не нужно. В этом случае с игрока так же
 *        снимается  попытка. Игрок имеет ограниченное количество снарядов,  периодически  с верху вниз по экрану перемещаются
 *        на парашюте ящики со снарядами (не больше одного одновременно), они так же являются целями противника, который по ним
 *        производит стрельбу а при соприкосновении ящика и вертолета противника производится исчезновение ящика. Если игрок
 *        соприкасается с ящиком то количество его снарядов увеличивается. Частота генерации ящиков со снарядами определяется
 *        количеством снарядов у игрока.
 *        Одновременно на экране может присутствовать ограниченное количество вертолетов противника и снарядов противника. На
 *        каждый уровень сложности отводится определенное количество вертолетов противника и максимальное количество вертолетов
 *        на экране, а так же уровнем сложности регулируется частота выстрелов противника.
 */

/*
    Preprocessor variable list:
    BONUS - enable generation of SPRITE_BONUSx on enemy copters death
*/

//#global BONUS=true
public class Gamelet
{
    /**
     * Количество уровней игры
     */
    public static final int MAX_STAGE_NUMBER = 20;

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
    //$protected static final int SCALE_WIDTH = 0xBA;
    //$protected static final int SCALE_HEIGHT = 0xAE;
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
    protected static final int SCALE_WIDTH = 0x0100;
    protected static final int SCALE_HEIGHT = 0x0100;
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

    protected static final long SCALE_SPEED = 10;
    /**
     * коэффициент масштабирования всех задержек времени
     */
    protected static final int I_TIMEDELAY = 90;
    /**
     * корень квадратный из двух
     */
    protected static final long I8_SQRT2 = 0x00B5;
    /**
     * вероятность событий в игре
     */
    protected static final int I8_PROBABILITY = 0x0200; //50%

    /**
     * Kоличество типов вертушек противника
     */
    public  static final int I_COPTERS_TYPE_NUMBER = 4;

    /**
     * частота между горизонтальными разворотами вертушек противника
     */
    private static final int I_COPTER_X_FLIP_FREQ = (int) ((1.0 * SCALE_SPEED));//1
    /**
     * частота между вертикальными разворотами вертушек противника
     */
    private static final int I_COPTER_Y_FLIP_FREQ = (int) ((1.0 * SCALE_SPEED));//1
    /**
     * задержка между разворотами вертушки-терминатора противника
     */
    private static final int I_COPTER3_FLIP_DELAY = 40;
    /**
     * Задрержка между гориз. разворотами вертушек
     */
    private static final int I_HORIZ_ROTATE_DELAY = 16;   // 10

    private static final int I_SMOKE_DELAY = 2;
    private static final int I8_SMOKE_Y_OFS = (int) ((0x0A00 * SCALE_HEIGHT) >> 8);

    private static final int I8_FIN_AXCELERATION = 0x0110;
    private static final int I8_FIN_BOSS_AXCELERATION = 0x0108;
    /**
     * Макс Y Скорости вертушек
     */
    private static final int[] I8_COPTER_Y_SPEED = {
            (int) ((0x0030 * SCALE_HEIGHT * SCALE_SPEED) >> 8),
            (int) ((0x0040 * SCALE_HEIGHT * SCALE_SPEED) >> 8),
            (int) ((0x0050 * SCALE_HEIGHT * SCALE_SPEED) >> 8),
            (int) ((0x0030 * SCALE_HEIGHT * SCALE_SPEED) >> 8)
    };
    /**
     * Макс X Скорости вертушек
     */
    private static final int[] I8_COPTER_X_SPEED = {
            (int) ((0x0020 * SCALE_WIDTH * SCALE_SPEED) >> 8),
            (int) ((0x0030 * SCALE_WIDTH * SCALE_SPEED) >> 8),
            (int) ((0x0040 * SCALE_WIDTH * SCALE_SPEED) >> 8),
            (int) ((0x0020 * SCALE_WIDTH * SCALE_SPEED) >> 8)
    };

    /**
     * Минимальная скорость вертушки
     */
    private static final int I8_MIN_COPTER_SPEED = (int) ((0x0015 * ((SCALE_WIDTH + SCALE_HEIGHT) / 2) * SCALE_SPEED) >> 8);
    /**
     * Скорость падения подбитой вертушки
     */
    private static final int I8_COPTER_FALL_SPEED = (int) ((0x0080 * SCALE_HEIGHT * SCALE_SPEED) >> 8);
    /**
     * Пассивная скорость нашей вертушки вниз
     */
    private static final int I8_MY_PASIVE_Y_SPEED = (int) ((0x0008 * SCALE_HEIGHT * SCALE_SPEED) >> 8);
    /**
     * Пассивная скорость нашей вертушки вправо
     */
    private static final int I8_MY_PASSIVE_X_SPEED = (int) ((0x0000 * SCALE_WIDTH * SCALE_SPEED) >> 8);
    /**
     * скорость нашей вертушки по вертикали
     */
    private static final int I8_MYCOPTER_Y_SPEED = (int) ((0x0060 * SCALE_HEIGHT * SCALE_SPEED) >> 8);
    /**
     * скорость нашей вертушки по горизонтали
     */
    private static final int I8_MYCOPTER_X_SPEED = (int) ((0x0030 * SCALE_WIDTH * SCALE_SPEED) >> 8);
    /**
     * Скорость ухода вверх вертушки игрока
     */
    private static final int I8_MY_FLIGHTUP_SPEED = (int) ((0x0080 * SCALE_HEIGHT * SCALE_SPEED) >> 8);
    /**
     * Скорость влета/вылета вертушки игрока
     */
    private static final int I8_MY_FLIGHT_INOUT_SPEED = (int) ((0x00A0 * SCALE_WIDTH * SCALE_SPEED) >> 8);
    /**
     * Скорость предпадения вертушки игрока
     */
    private static final int I8_MY_FALL_SPEED = (int) ((0x0040 * SCALE_HEIGHT * SCALE_SPEED) >> 8);
    /**
     * Скорость снаряда по горизонтали
     */
    private static final int I8_SHELL_SPEED = (int) ((0x0080 * SCALE_WIDTH * SCALE_SPEED) >> 8);
    /**
     * Скорость снаряда по диагонали
     */
    private static final int I8_SHELL_DIAGONAL_SPEED = (int) ((((0x0080 * (SCALE_WIDTH + SCALE_HEIGHT) * SCALE_SPEED) >> 9) * I8_SQRT2) >> 8);
    /**
     * Скорость снаряда игрока по горизонтали
     */
    private static final int I8_MY_SHELL_SPEED = (int) ((0x0100 * SCALE_WIDTH * SCALE_SPEED) >> 8);
    /**
     * Скорость метеорита
     */
    private static final int I8_FIREBALL_SPEED = (int) ((0x0090 * SCALE_HEIGHT * SCALE_SPEED) >> 8);
    /**
     * Скорость боеприпаса
     */
    private static final int I8_AMMOnLIFE_SPEED = (int) ((0x0020 * SCALE_HEIGHT * SCALE_SPEED) >> 8);
    /**
     * Скорость бонуса
     */
    //#if BONUS
    private static final int I8_BONUS_SPEED_Y = (int) ((-0x0020 * SCALE_HEIGHT * SCALE_SPEED) >> 8);
    private static final int I8_BONUS_SPEED_X = (int) ((0x0 * SCALE_WIDTH * SCALE_SPEED) >> 8);
    //#endif
    /**
     * Скорость истребителя
     */
    private static final int I8_FIGHTER_SPEED = (int) ((0x0090 * SCALE_WIDTH * SCALE_SPEED) >> 8);
    /**
     * Скорость босса по X
     */
    private static final int I8_BOSS_X_SPEED = (int) ((0x0012 * SCALE_WIDTH * SCALE_SPEED) >> 8);
    /**
     * Скорость босса по X
     */
    private static final int I8_BOSS_Y_SPEED = (int) ((0x0016 * SCALE_HEIGHT * SCALE_SPEED) >> 8);
    /**
     * Скорость босса по X при смерти
     */
    private static final int I8_BOSS_DEATH_X_SPEED = (int) ((-0x0010 * SCALE_WIDTH * SCALE_SPEED) >> 8);
    /**
     * Скорость босса по X при смерти
     */
    private static final int I8_BOSS_DEATH_Y_SPEED = (int) ((0x0020 * SCALE_HEIGHT * SCALE_SPEED) >> 8);

    /**
     * Х Координата выстрела относительно вертушки
     */
    private static final int I8_SHOT_OFFSET_X = (int) ((16 * 256 * SCALE_WIDTH) >> 8);
    /**
     * Y Координата выстрела относительно вертушки
     */
    private static final int I8_SHOT_OFFSET_Y = (int) ((0x0 * SCALE_HEIGHT) >> 8);
    /**
     * Х Координата выстрела относительно вертушки
     */
    private static final int I8_BONUS_OFFSET_X = (int) ((0 * SCALE_WIDTH) >> 8);
    /**
     * Y Координата выстрела относительно вертушки
     */
    private static final int I8_BONUS_OFFSET_Y = (int) ((0x0 * SCALE_HEIGHT) >> 8);
    /**
     * Угол видимости противника
     */
    private static final int I8_ENEMY_FIRE_ANGLE = (int) (0x0080);
    /**
     * Правая граница области передвижения босса относительно правой границы экрана
     */
    private static final int I8_BOSS_BOUNDS_RIGHT = (int) ((-20 * 256 * SCALE_WIDTH) >> 8);
    /**
     * Левая граница области передвижения босса относительно правой границы экрана
     */
    private static final int I8_BOSS_BOUNDS_LEFT = (int) ((-40 * 256 * SCALE_WIDTH) >> 8);
    /**
     * граница относительно правой границы экрана из-за которой начинают стреллять башни
     */
    private static final int I8_TOWER_FIRE_LINE = (int) ((-0x0800 * SCALE_WIDTH) >> 8);

    /**
     * Задержка вылета истребителя в тактах игры
     */
    private static final int I_FIGHTER_FLIGHT_DELAY = 40;
    /**
     * задержка после смерти игрока
     */
    private static final int I_POSTPLAY_DELAY = 10;

    /**
     * Максимальное количество снарядов у игрока
     */
    private static final int MAX_PLAYER_SHELLS_NUMBER = 10;
    /**
     * Максимальное количество снарядов врага на экране
     */
    private static final int MAX_ENEMY_SHELLS_NUMBER = 10;
    /**
     * Максимальное количество метеоритов на экране
     */
    private static final int MAX_FIREBALLS_NUMBER = 5;
    /**
     * Максимальное количество снарядов на экране
     */
    public  static final int MAX_SHELLS_NUMBER = MAX_PLAYER_SHELLS_NUMBER + MAX_ENEMY_SHELLS_NUMBER + MAX_FIREBALLS_NUMBER;
    /**
     * Максимальное количество вертушек противника на экране
     */
    public  static final int MAX_SCREEN_COPTERS_NUMBER = 5;
    /**
     * Максимальное количество взрывов на экране
     */
    public  static final int MAX_EXPLOSIONS_NUMBER = 5;
    /**
     * Максимальное количество бонусов на экране
     */
    public static final int MAX_BONUS_NUMBER = 5;

    /**
     * очки за взятый ящик со снарядами
     */
    private static final int BONUS_AMMO = 5;
    /**
     * очки за сбитую вертушку
     */
    private static final int[] BONUS_COPTER = {5, 10, 15, 20};
    /**
     * очки за неизрасходованные попытки
     */
    private static final int BONUS_ATTEMPTION = 50;
    /**
     * очки уничтожение башни босса
     */
    private static final int BONUS_TOWER_DESTROY = 20;
    /**
     * очки за попадение башню босса
     */
    private static final int BONUS_TOWER = 5;
    /**
     * очки для получения жизни
     */
    private static final int BONUS_NEEDED_FOR_LIFE = 103;
    /**
     * очки за взятие жизни
     */
    private static final int BONUS_LIFE = 20;

    // Кнопки управления
    public static final int BUTTON_NONE = 0;
    public static final int BUTTON_LEFT = 1;
    public static final int BUTTON_RIGHT = 2;
    public static final int BUTTON_UP = 4;
    public static final int BUTTON_DOWN = 8;
    public static final int BUTTON_FIRE = 16;

    //уровни сложности игры
    public static final int GAMELEVEL_EASY = 0;
    public static final int GAMELEVEL_NORMAL = 1;
    public static final int GAMELEVEL_HARD = 2;

    // Игровые события
    public static final int GAMEACTION_GAME_START = 0;
    public static final int GAMEACTION_PLAYER_FIRE = 1;
    public static final int GAMEACTION_ENEMY_FIRE = 2;
    public static final int GAMEACTION_EXPLOSION = 3;
    public static final int GAMEACTION_PLAYER_GET_AMMO = 4;
    public static final int GAMEACTION_ENEMY_GET_AMMO = 5;
    public static final int GAMEACTION_AMMO = 6;
    public static final int GAMEACTION_VICTORY = 7;
    public static final int GAMEACTION_FIREBALL = 8;
    public static final int GAMEACTION_ENEMY_FALL = 9;
    public static final int GAMEACTION_PLAYER_FALL = 10;
    public static final int GAMEACTION_FIGHTER = 11;
    public static final int GAMEACTION_LIFE = 12;
    public static final int GAMEACTION_PLAYER_GET_LIFE = 13;
    public static final int GAMEACTION_ENEMY_GET_LIFE = 14;
    public static final int GAMEACTION_FIGHTER_FLY = 15;
    public static final int GAMEACTION_ATTEMPTION_CHANGE = 16;

    /**
     * Количество ячеек, описывающих один спрайт в массиве
     */
    private static final int SPRITE_DATA_LENGTH = 10;

    /**
     * Вертушка, повернутая вправо/влево
     */
    public static final int SPRITE_COPTER_ = 0;
    /**
     * Вертушка, повернутая вправо
     */
    public static final int SPRITE_COPTER_RIGHT = 0;
    /**
     * Вертушка, повернутая влево
     */
    public static final int SPRITE_COPTER_LEFT = 1;
    /**
     * Поворот Вертушки
     */
    public static final int SPRITE_COPTER_xTy_ = 2;
    /**
     * Поворот Вертушки, справа налево
     */
    public static final int SPRITE_COPTER_RTL = 2;
    /**
     * Поворот Вертушки, слева направо
     */
    public static final int SPRITE_COPTER_LTR = 3;
    /**
     * начало спрайтов падения Вертушки
     */
    public static final int SPRITE_COPTER_FALL_ = 4;
    /**
     * падение Вертушки, справа налево
     */
    public static final int SPRITE_COPTER_FALL_RTL = 4;
    /**
     * падение Вертушки, слева направо
     */
    public static final int SPRITE_COPTER_FALL_LTR = 5;

    /**
     * Начало описания в массиве вертушки игрока
     */
    public static final int SPRITE_MYCOPTER_ = 0;
    /**
     * Вертушка игрока, повернутая вправо
     */
    public static final int SPRITE_MYCOPTER_RIGHT = SPRITE_MYCOPTER_ + SPRITE_COPTER_RIGHT;
    /**
     * Вертушка игрока, повернутая влево
     */
    public static final int SPRITE_MYCOPTER_LEFT = SPRITE_MYCOPTER_ + SPRITE_COPTER_LEFT;
    /**
     * Поворот Вертушки игрока, справа налево
     */
    public static final int SPRITE_MYCOPTER_RTL = SPRITE_MYCOPTER_ + SPRITE_COPTER_RTL;
    /**
     * Поворот Вертушки игрока, слева направо
     */
    public static final int SPRITE_MYCOPTER_LTR = SPRITE_MYCOPTER_ + SPRITE_COPTER_LTR;
    /**
     * падение Вертушки игрока, справа налево
     */
    public static final int SPRITE_MYCOPTER_FALL_RTL = SPRITE_MYCOPTER_ + SPRITE_COPTER_FALL_RTL;
    /**
     * падение Вертушки игрока, слева направо
     */
    public static final int SPRITE_MYCOPTER_FALL_LTR = SPRITE_MYCOPTER_ + SPRITE_COPTER_FALL_LTR;
    /**
     * Начало спрайтов снарядов игрока
     */
    public static final int SPRITE_MY_SHELL_ = 6;
    /**
     * Снаряд игрока повернутый вправо
     */
    public static final int SPRITE_MY_SHELL_RIGTH = 6;
    /**
     * Снаряд игрока повернутый влево
     */
    public static final int SPRITE_MY_SHELL_LEFT = 7;
    /**
     * Ящик с боеприпасами
     */
    public static final int SPRITE_AMMO = 8;
    /**
     * Взрыв
     */
    public static final int SPRITE_EXPLOSION = 9;
    /**
     * Начало спрайтов снарядов противника
     */
    public static final int SPRITE_ENEMY_SHELL_ = 10;
    /**
     * Снаряд противника повернутый вправо
     */
    public static final int SPRITE_ENEMY_SHELL_RIGTH = 10;
    /**
     * Снаряд противника повернутый влево
     */
    public static final int SPRITE_ENEMY_SHELL_LEFT = 11;
    /**
     * Метеорит
     */
    public static final int SPRITE_FIREBALL = 12;
    /**
     * Влет и вылет игрока со стадии
     */
    public static final int SPRITE_MYCOPTER_FLIGHT_INOUT = 13;
    /**
     * Истребитель вправо
     */
    public static final int SPRITE_FIGHTER_RIGHT = 14;
    /**
     * Истребитель влево
     */
    public static final int SPRITE_FIGHTER_LEFT = 15;
    /**
     * Босс
     */
    public static final int SPRITE_BOSS1 = 16;
    /**
     * Башни у Босса
     */
    public static final int SPRITE_TOWER = 17;
    /**
     * Выстрел босса по диагонали вверх
     */
    public static final int SPRITE_ENEMY_SHELL_LEFT_UP = 18;
    /**
     * Выстрел босса по диагонали вниз
     */
    public static final int SPRITE_ENEMY_SHELL_LEFT_DOWN = 19;
    /**
     * Взрыв Босса
     */
    public static final int SPRITE_BOSS1_EXPLOSION = 20;
    /**
     * Жизнь
     */
    public static final int SPRITE_LIFE = 21;

    /**
     * Вертушка противника, N1
     */
    public static final int SPRITE_COPTER1_ = 22;
    /**
     * Вертушка противника N1, повернутая вправо
     */
    public static final int SPRITE_COPTER1_RIGHT = SPRITE_MYCOPTER_RIGHT + SPRITE_COPTER1_;
    /**
     * Вертушка противника N1, повернутая влево
     */
    public static final int SPRITE_COPTER1_LEFT = SPRITE_MYCOPTER_LEFT + SPRITE_COPTER1_;
    /**
     * Поворот Вертушки противника N1, справа налево
     */
    public static final int SPRITE_COPTER1_RTL = SPRITE_MYCOPTER_RTL + SPRITE_COPTER1_;
    /**
     * Поворот Вертушки противника N1, слева направо
     */
    public static final int SPRITE_COPTER1_LTR = SPRITE_MYCOPTER_LTR + SPRITE_COPTER1_;
    /**
     * падение Вертушки противника N1, справа налево
     */
    public static final int SPRITE_COPTER1_FALL_RTL = SPRITE_MYCOPTER_FALL_RTL + SPRITE_COPTER1_;
    /**
     * падение Вертушки противника N1, слева направо
     */
    public static final int SPRITE_COPTER1_FALL_LTR = SPRITE_MYCOPTER_FALL_LTR + SPRITE_COPTER1_;
    /**
     * Вертушка противника, N2
     */
    public static final int SPRITE_COPTER2_ = 28;
    /**
     * Вертушка противника N2, повернутая вправо
     */
    public static final int SPRITE_COPTER2_RIGHT = SPRITE_MYCOPTER_RIGHT + SPRITE_COPTER2_;
    /**
     * Вертушка противника N2, повернутая влево
     */
    public static final int SPRITE_COPTER2_LEFT = SPRITE_MYCOPTER_LEFT + SPRITE_COPTER2_;
    /**
     * Поворот Вертушки противника N2, справа налево
     */
    public static final int SPRITE_COPTER2_RTL = SPRITE_MYCOPTER_RTL + SPRITE_COPTER2_;
    /**
     * Поворот Вертушки противника N2, слева направо
     */
    public static final int SPRITE_COPTER2_LTR = SPRITE_MYCOPTER_LTR + SPRITE_COPTER2_;
    /**
     * падение Вертушки противника N2, справа налево
     */
    public static final int SPRITE_COPTER2_FALL_RTL = SPRITE_MYCOPTER_FALL_RTL + SPRITE_COPTER2_;
    /**
     * падение Вертушки противника N2, слева направо
     */
    public static final int SPRITE_COPTER2_FALL_LTR = SPRITE_MYCOPTER_FALL_LTR + SPRITE_COPTER2_;
    /**
     * Вертушка противника N3
     */
    public static final int SPRITE_COPTER3_ = 34;
    /**
     * Вертушка противника N3, повернутая вправо
     */
    public static final int SPRITE_COPTER3_RIGHT = SPRITE_MYCOPTER_RIGHT + SPRITE_COPTER3_;
    /**
     * Вертушка противника N3, повернутая влево
     */
    public static final int SPRITE_COPTER3_LEFT = SPRITE_MYCOPTER_LEFT + SPRITE_COPTER3_;
    /**
     * Поворот Вертушки противника N3, справа налево
     */
    public static final int SPRITE_COPTER3_RTL = SPRITE_MYCOPTER_RTL + SPRITE_COPTER3_;
    /**
     * Поворот Вертушки противника N3, слева направо
     */
    public static final int SPRITE_COPTER3_LTR = SPRITE_MYCOPTER_LTR + SPRITE_COPTER3_;
    /**
     * падение Вертушки противника N3, справа налево
     */
    public static final int SPRITE_COPTER3_FALL_RTL = SPRITE_MYCOPTER_FALL_RTL + SPRITE_COPTER3_;
    /**
     * падение Вертушки противника N3, слева направо
     */
    public static final int SPRITE_COPTER3_FALL_LTR = SPRITE_MYCOPTER_FALL_LTR + SPRITE_COPTER3_;
    /**
     * Вертушка противника, N4
     */
    public static final int SPRITE_COPTER4_ = 40;
    /**
     * Вертушка противника N4, повернутая вправо
     */
    public static final int SPRITE_COPTER4_RIGHT = SPRITE_MYCOPTER_RIGHT + SPRITE_COPTER4_;
    /**
     * Вертушка противника N4, повернутая влево
     */
    public static final int SPRITE_COPTER4_LEFT = SPRITE_MYCOPTER_LEFT + SPRITE_COPTER4_;
    /**
     * Поворот Вертушки противника N4, справа налево
     */
    public static final int SPRITE_COPTER4_RTL = SPRITE_MYCOPTER_RTL + SPRITE_COPTER4_;
    /**
     * Поворот Вертушки противника N4, слева направо
     */
    public static final int SPRITE_COPTER4_LTR = SPRITE_MYCOPTER_LTR + SPRITE_COPTER4_;
    /**
     * падение Вертушки противника N4, справа налево
     */
    public static final int SPRITE_COPTER4_FALL_RTL = SPRITE_MYCOPTER_FALL_RTL + SPRITE_COPTER4_;
    /**
     * падение Вертушки противника N4, слева направо
     */
    public static final int SPRITE_COPTER4_FALL_LTR = SPRITE_MYCOPTER_FALL_LTR + SPRITE_COPTER4_;
    /**
     * Босс 2
     */
    public static final int SPRITE_BOSS2 = 46;
    /**
     * Босс 3
     */
    public static final int SPRITE_BOSS3 = 47;
    /**
     * Босс 4
     */
    public static final int SPRITE_BOSS4 = 48;
    /**
     * дым
     */
    public static final int SPRITE_SMOKE = 49;
    public static final int SPRITE_BONUS_ = 50;
    public static final int SPRITE_BONUS1 = 50;
    public static final int SPRITE_BONUS2 = 51;
    public static final int SPRITE_BONUS3 = 52;
    public static final int SPRITE_BONUS4 = 53;
    public static final int SPRITE_BONUS_LIFE = 54;
    public static final int SPRITE_BONUS_AMMO = 55;

    /**
     * Несуществующий спрайт
     */
    private static final int SPRITE_NONE = 99;

    /**
     * Кол-во спрайтов на один тип вертушек противника
     */
    public static final int SPRITE_COPTER_LENGHT = 6;

    /**
     * Массив параметров игровых спрайтов
     * левые и правые пары спрайтов должни находится рядом и начинаться с четного номера
     */
    private static int[] aiSpriteParameters;
    /**
     * расстояния от основной точки спрайта вертушки игрока до краев этого спрайта
     */
    private static int I8_MY_BOUNDS_X;
    /**
     * расстояния от основной точки спрайта вертушки игрока до краев этого спрайта
     */
    private static int I8_MY_BOUNDS_Y;
    /**
     * расстояния от основной точки спрайта вертушки игрока до краев этого спрайта
     */
    private static int I8_MY_BOUNDS_BOTTOM;

    /**
     * расстояния от основной точки спрайта вертушки противника до краев этого спрайта
     */
    private static int I8_COPTER_BOUNDS_X;
    /**
     * расстояния от основной точки спрайта вертушки противника до краев этого спрайта
     */
    private static int I8_COPTER_BOUNDS_Y;
    /**
     * расстояния от основной точки спрайта вертушки противника до краев этого спрайта
     */
    private static int I8_SHELL_BOUNDS_X;
    /**
     * расстояния от основной точки спрайта метеорита до краев этого спрайта
     */
    private static int I8_FIREBALL_BOUNDS_Y;
    /**
     * расстояния от основной точки спрайта ящика с боеприпасами до краев этого спрайта
     */
    private static int I8_AMMO_BOUNDS_X;
    /**
     * расстояния от основной точки спрайта ящика с боеприпасами до краев этого спрайта
     */
    private static int I8_AMMO_BOUNDS_Y;
    /**
     * расстояния от основной точки спрайта жизни до краев этого спрайта
     */
    private static int I8_LIFE_BOUNDS_X;
    /**
     * расстояния от основной точки спрайта жизни до краев этого спрайта
     */
    private static int I8_LIFE_BOUNDS_Y;
    /**
     * расстояния от основной точки спрайта истребителя до краев этого спрайта
     */
    private static int I8_FIGHTER_BOUNDS_X;
    /**
     * расстояния от основной точки спрайта истребителя до краев этого спрайта
     */
    private static int I8_FIGHTER_BOUNDS_Y;
    /**
     * расстояния от основной точки спрайта вертушки противника до краев этого спрайта
     */
    private static int I8_BOSS_BOUNDS_X;
    /**
     * расстояния от основной точки спрайта вертушки противника до краев этого спрайта
     */
    private static int I8_BOSS_BOUNDS_Y;
    /**
     * расстояния от основной точки спрайта башни до краев этого спрайта
     */
    private static int I8_TOWER_BOUNDS_X;
    /**
     * расстояния от основной точки спрайта башни до краев этого спрайта
     */
    private static int I8_TOWER_BOUNDS_Y;

    // Параметры Описателя босса
    /**
     * 1/частота выстрелов босса
     */
    private static final int OFS_I8_BOSS_FIRE_RATE = 0;
    /**
     * Босс
     */
    private static final int OFS_BOSS_SPRITE = 1;
    /**
     * Башни у Босса
     */
    private static final int OFS_BOSS_TOWER_SPRITE = 2;
    /**
     * Взрыв Босса
     */
    private static final int OFS_BOSS_EXPLOSION_SPRITE = 3;
    /**
     * Количество башен у босса
     */
    private static final int OFS_BOSS_TOWERS_COUNT = 4;
    /**
     * Параметры башен у босса
     */
    private static final int OFS_BOSS_TOWERS_ = 5;
    /**
     * Количество ячеек, описывающих одну башню в массиве
     */
    private static final int BOSS_TOWERS_DATA_LENGTH = 3;
    /**
     * X координата башни
     */
    private static final int OFS_TOWER_X = 0;
    /**
     * Y координата башни
     */
    private static final int OFS_TOWER_Y = 1;
    /**
     * Защита башен у босса
     */
    private static final int OFS_TOWER_HEALTH = 2;

    /**
     * Боссы
     */
    private static final int OFS_BOSS_N1 = 0;
    private static int OFS_BOSS_N2;
    private static int OFS_BOSS_N3;
    private static int OFS_BOSS_N4;

    /**
     * Массив Описателей боссов
     */
    private static int[] aiBossParams;

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
    // расстояния от основной точки спрайта вертушки игрока до краев этого спрайта
    /**
     * Массив, скоростей вражеских вертушек
     */
    private static int[] ai8CopterVX, ai8CopterVY;
    /**
     * Счетчик задержки выстрелов в очереди
     */
    private static int[] aiEnemyFireCounter;
    /**
     * Счетчик задержки начала новой очереди
     */
    private static int[] aiEnemyBurstCounter;
    /**
     * Счетчик количества выстрелов в очереди
     */
    private static int[] aiEnemyFireNumberCounter;

    /**
     * Массив, содержащий спрайты вражеских вертушек (башен босса)
     */
    public static Sprite[] ap_CopterSprites;
    /**
     * Спрайт вертушки игрока
     */
    public static Sprite p_MyCopterSprite;
    /**
     * Массив спрайтов снарядов
     */
    public static Sprite[] ap_ShellAndFireballSprites;
    /**
     * Массив, содержащий спрайты взрывов и дыма
     */
    public static Sprite[] ap_ExplosionSprites;
    /**
     * Спрайт ящика с боеприпасами
     */
    public static Sprite p_AmmoSprite;
    /**
     * Массив спрайтов метеоритов
     */
    public static Sprite[] ap_FireballSprites;
    /**
     * Спрайт истребителя (босса)
     */
    public static Sprite p_FighterSprite;
    /**
     * Спрайт жизни
     */
    public static Sprite p_LifeSprite;
    /**
     * Массив, содержащий спрайты взрывов и дыма
     */
    //#if BONUS
    public static Sprite[] ap_BonusSprites;
    //#endif

    private static int iSmokeCounter;
    /**
     * задержка между вылетами вертолетов противника
     */
    private static int iEnemyDelay;
    /**
     * счетчик задержки между вылетами вертолетов противника
     */
    private static int iEnemyCounter;
    /**
     * Задержка между выстрелами в очереди
     */
    private static int iEnemyFireDelay;
    /**
     * Задержка между очередями
     */
    private static int iEnemyBurstDelay;
    /**
     * Максимальная длинна очереди выстрелов
     */
    private static int iEnemyBurstLength;

    /**
     * задержка между выстрелами игрока
     */
    private static int iPlayerFireDelay;
    /**
     * счетчик задержки между выстрелами игрока
     */
    private static int iPlayerFireCounter;

    /**
     * задержка между появлениями ящика с боеприпасами
     */
    private static int iAmmoDelay;
    /**
     * счетчик задержки между появлениями ящика с боеприпасами
     */
    private static int iAmmoCounter;
    //    private static int i_SpeedScale;

    /**
     * задержка между появлениями метеоритов
     */
    private static int iFireballDelay;
    /**
     * счетчик задержки между появлениями метеоритов
     */
    private static int iFireballCounter;

    /**
     * задержка между появлениями истребителей
     */
    private static int iFighterDelay;
    /**
     * счетчик задержки между появлениями истребителей
     */
    private static int iFighterCounter;

    /**
     * счетчик задержки вылета истребителя
     */
    public static int iFighterFlightCounter;

    /**
     * счетчик задержка после смерти игрока
     */
    private static int iPostplayCounter;

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
     * ID босса текущей стадии
     */
    private static int iBossOfs;
    /**
     * Переменная содержит состояние игрока
     */
    public static int i_PlayerState;
    /**
     * Переменная содержит очки игрока
     */
    private static int iPlayerScore;
    /**
     * Переменная сохраняет очки игрока
     */
    private static int rollbackScore;
    /**
     * количество выданых жизней
     */
    private static int iLifeCount;
    /**
     * сохраняет количество выданых жизней
     */
    private static int rollbackLifeCount;

    /**
     * Переменная содержит количество пикселей по ширине игровой зоны
     */
    protected static int iScreenWidth;
    /**
     * Переменная содержит количество пикселей по высоте игровой зоны
     */
    protected static int iScreenHeight;

    /**
     * Переменная содержит количество игровых попыток играющего
     */
    public static int iPlayerAttemptions;
    /**
     * Переменная сохраняет количество игровых попыток играющего
     */
    public static int rollbackAttemptions;
    /**
     * Переменная содержит максимальное количество запасных игровых попыток играющего
     */
    public static int iMaxPlayerAttemptions;
    /**
     * Переменная содержит количество патронов у играющего
     */
    public static int iPlayerAmmo;

    /**
     * Счетчик активных вертушек на уровне
     */
    private static int[] aiActiveCoptersNumber;
    /**
     * Счетчик пассивных вертушек на уровне
     */
    private static int[] aiPassiveCoptersNumber;
    /**
     * Переменная содержит максимальное количество вертушек противника на экране
     */
    private static int[] aiMaxActiveCopters;
    /**
     * Счетчик общего количества активных вертушек на уровне
     */
    public static int iTotalActiveCoptersNumber;
    /**
     * Счетчик общего количества пвссивных вертушек на уровне
     */
    public static int iTotalPassiveCoptersNumber;

    /**
     * Специальные режимы (подьема/влета/вылета) вертушки игрока
     */
    private static int iPlayerSpecMode;
    /**
     * Отсутствие специального режима
     */
    private static final int psmNONE = 0;
    /**
     * Специальный режим подьема вертушки игрока при победе
     */
    private static final int psmFLIGHTUP = 1;
    /**
     * Специальные режимы влета вертушки игрока в уровень
     */
    private static final int psmFLIGHTIN = 2;
    /**
     * Специальные режимы вылета вертушки игрока из уровня
     */
    private static final int psmFLIGHTOUT = 3;
    /**
     * Специальный режим предпадения вертушки игрока перед взрывом
     */
    private static final int psmFALL = 4;

    /**
     * Счетчик метеоритов на экране
     */
    private static int iActiveFireballsNumber;

    /**
     * Функция возвращает знак числа (1 или -1)
     */
    private static int sign(int a)
    {
        return (a > 0) ? 1 : -1;
    }

    /**
     * Функция отвечающая за инициализацию блока, до её вызова любые операции с блоком запрещены. Переводит блок в состояние INITED..
     *
     * @return true если инициализация прошла успешно, иначе false.
     */
    public static final boolean init(Class _class)
    {
        if (i_GameState != STATE_UNKNON) return false;
        iScreenHeight = -1;
        iScreenWidth = -1;
        try
        {
            aiSpriteParameters = (int[]) loadArray(_class, "copters.bin");
            /** расстояния от основной точки спрайта вертушки игрока до краев этого спрайта */
            I8_MY_BOUNDS_X = (int) (((long) aiSpriteParameters[SPRITE_MYCOPTER_RIGHT * SPRITE_DATA_LENGTH] * SCALE_WIDTH) >> 9);
            /** расстояния от основной точки спрайта вертушки игрока до краев этого спрайта */
            I8_MY_BOUNDS_Y = (int) (((long) aiSpriteParameters[SPRITE_MYCOPTER_RIGHT * SPRITE_DATA_LENGTH + 1] * SCALE_HEIGHT) >> 9);
            /** расстояния от основной точки спрайта вертушки игрока до краев этого спрайта */
            I8_MY_BOUNDS_BOTTOM = (int) (((long)
                    (-aiSpriteParameters[SPRITE_MYCOPTER_RIGHT * SPRITE_DATA_LENGTH + 1] / 2 +
                    aiSpriteParameters[SPRITE_MYCOPTER_RIGHT * SPRITE_DATA_LENGTH + 3] +
                    aiSpriteParameters[SPRITE_MYCOPTER_RIGHT * SPRITE_DATA_LENGTH + 5]
                    ) * SCALE_HEIGHT) >> 8);

            /** расстояния от основной точки спрайта вертушки противника до краев этого спрайта */
            I8_SHELL_BOUNDS_X = (int) (((long) aiSpriteParameters[SPRITE_MY_SHELL_ * SPRITE_DATA_LENGTH] * SCALE_WIDTH) >> 9);
            /** расстояния от основной точки спрайта вертушки противника до краев этого спрайта */
            I8_COPTER_BOUNDS_X = (int) (((long) aiSpriteParameters[SPRITE_COPTER2_RIGHT * SPRITE_DATA_LENGTH] * SCALE_WIDTH) >> 9);
            /** расстояния от основной точки спрайта вертушки противника до краев этого спрайта */
            I8_COPTER_BOUNDS_Y = (int) (((long) aiSpriteParameters[SPRITE_COPTER2_RIGHT * SPRITE_DATA_LENGTH + 1] * SCALE_HEIGHT) >> 9);
            /** расстояния от основной точки спрайта метеорита до краев этого спрайта */
            I8_FIREBALL_BOUNDS_Y = (int) (((long) aiSpriteParameters[SPRITE_FIREBALL * SPRITE_DATA_LENGTH + 1] * SCALE_HEIGHT) >> 9);
            /** расстояния от основной точки спрайта ящика с боеприпасами до краев этого спрайта */
            I8_AMMO_BOUNDS_X = (int) (((long) aiSpriteParameters[SPRITE_AMMO * SPRITE_DATA_LENGTH] * SCALE_WIDTH) >> 9);
            /** расстояния от основной точки спрайта ящика с боеприпасами до краев этого спрайта */
            I8_AMMO_BOUNDS_Y = (int) (((long) aiSpriteParameters[SPRITE_AMMO * SPRITE_DATA_LENGTH + 1] * SCALE_HEIGHT) >> 9);
            /** расстояния от основной точки спрайта жизни до краев этого спрайта */
            I8_LIFE_BOUNDS_X = (int) (((long) aiSpriteParameters[SPRITE_LIFE * SPRITE_DATA_LENGTH] * SCALE_WIDTH) >> 9);
            /** расстояния от основной точки спрайта жизни до краев этого спрайта */
            I8_LIFE_BOUNDS_Y = (int) (((long) aiSpriteParameters[SPRITE_LIFE * SPRITE_DATA_LENGTH + 1] * SCALE_HEIGHT) >> 9);
            /** расстояния от основной точки спрайта истребителя до краев этого спрайта */
            I8_FIGHTER_BOUNDS_X = (int) (((long) aiSpriteParameters[SPRITE_FIGHTER_RIGHT * SPRITE_DATA_LENGTH] * SCALE_WIDTH) >> 9) + 0x0100;
            /** расстояния от основной точки спрайта истребителя до краев этого спрайта */
            I8_FIGHTER_BOUNDS_Y = (int) (((long) aiSpriteParameters[SPRITE_FIGHTER_RIGHT * SPRITE_DATA_LENGTH + 1] * SCALE_HEIGHT) >> 9);

            I8_TOWER_BOUNDS_X = (int) (((long) aiSpriteParameters[SPRITE_TOWER * SPRITE_DATA_LENGTH] * SCALE_WIDTH) >> 9);
            I8_TOWER_BOUNDS_Y = (int) (((long) aiSpriteParameters[SPRITE_TOWER * SPRITE_DATA_LENGTH + 1] * SCALE_HEIGHT) >> 9);

            aiBossParams = (int[]) loadArray(_class, "boss.bin");
            int iBossOfs = 0;
            int iTowersCount;

            for (int j = 0; j < 4; j++)
            {
                switch (j)
                {
                    case 0:
                        break;
                    case 1:
                        OFS_BOSS_N2 = iBossOfs;
                        break;
                    case 2:
                        OFS_BOSS_N3 = iBossOfs;
                        break;
                    case 3:
                        OFS_BOSS_N4 = iBossOfs;
                        break;
                }

                iTowersCount = aiBossParams[iBossOfs + OFS_BOSS_TOWERS_COUNT];
                for (int i = 0; i < iTowersCount; i++)
                {
                    aiBossParams[iBossOfs + OFS_BOSS_TOWERS_ + i * BOSS_TOWERS_DATA_LENGTH + OFS_TOWER_X] =
                            (aiBossParams[iBossOfs + OFS_BOSS_TOWERS_ + i * BOSS_TOWERS_DATA_LENGTH + OFS_TOWER_X] * SCALE_WIDTH) >> 8;
                    aiBossParams[iBossOfs + OFS_BOSS_TOWERS_ + i * BOSS_TOWERS_DATA_LENGTH + OFS_TOWER_Y] =
                            (aiBossParams[iBossOfs + OFS_BOSS_TOWERS_ + i * BOSS_TOWERS_DATA_LENGTH + OFS_TOWER_Y] * SCALE_HEIGHT) >> 8;
                }
                iBossOfs += 5 + iTowersCount * 3;
            }
        }
        catch (Exception e1)
        {
            return false;
        }

        //------------Вставьте свой код здесь--------------------
        ap_CopterSprites = new Sprite[MAX_SCREEN_COPTERS_NUMBER];
        ai8CopterVX = new int[MAX_SCREEN_COPTERS_NUMBER];
        ai8CopterVY = new int[MAX_SCREEN_COPTERS_NUMBER];
        aiEnemyBurstCounter = new int[MAX_SCREEN_COPTERS_NUMBER];
        aiEnemyFireCounter = new int[MAX_SCREEN_COPTERS_NUMBER];
        aiEnemyFireNumberCounter = new int[MAX_SCREEN_COPTERS_NUMBER];
        ap_ShellAndFireballSprites = new Sprite[MAX_SHELLS_NUMBER];
        ap_ExplosionSprites = new Sprite[MAX_EXPLOSIONS_NUMBER];
        ap_FireballSprites = new Sprite[MAX_FIREBALLS_NUMBER];
        aiActiveCoptersNumber = new int[I_COPTERS_TYPE_NUMBER];
        aiPassiveCoptersNumber = new int[I_COPTERS_TYPE_NUMBER];
        aiMaxActiveCopters = new int[I_COPTERS_TYPE_NUMBER];

        for (int li = 0; li < MAX_SCREEN_COPTERS_NUMBER; li++)
        {
            ap_CopterSprites[li] = new Sprite(0);
        }
        for (int li = 0; li < MAX_SHELLS_NUMBER; li++)
        {
            ap_ShellAndFireballSprites[li] = new Sprite(0);
        }
        for (int li = 0; li < MAX_EXPLOSIONS_NUMBER; li++)
        {
            ap_ExplosionSprites[li] = new Sprite(0);
        }
        for (int li = 0; li < MAX_FIREBALLS_NUMBER; li++)
        {
            ap_FireballSprites[li] = new Sprite(0);
        }

        p_AmmoSprite = new Sprite(0);
        p_FighterSprite = new Sprite(0);
        p_LifeSprite = new Sprite(0);
        p_MyCopterSprite = new Sprite(0);
        //#if BONUS
        ap_BonusSprites = new Sprite[MAX_BONUS_NUMBER];
        for (int li = 0; li < MAX_BONUS_NUMBER; li++)
            ap_BonusSprites[li] = new Sprite(0);
        //#endif
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
        ap_CopterSprites = null;
        ap_ShellAndFireballSprites = null;
        ap_ExplosionSprites = null;
        p_AmmoSprite = null;
        p_LifeSprite = null;
        p_MyCopterSprite = null;
        aiActiveCoptersNumber = null;
        aiPassiveCoptersNumber = null;
        aiMaxActiveCopters = null;
        //#if BONUS
        ap_BonusSprites = null;
        //#endif
        //--------------------------------------------------

        iScreenHeight = -1;
        iScreenWidth = -1;

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
        iScreenHeight = _gameAreaHeight;
        iScreenWidth = _gameAreaWidth;
        i_GameLevel = _gameLevel;
        i_GameStage = 0;

        switch (_gameLevel)
        {
                //------------Вставьте свой код здесь--------------------
                case GAMELEVEL_EASY:
                {
                    //количество дополнительных попыток играющего
                    iMaxPlayerAttemptions = 3;  // 3
                    //задержка между вылетами вертолетов противника
                    iEnemyDelay = 8; // 10
                    //Задержка между очередями
                    iEnemyBurstDelay = 25;
                    //задержка между выстрелами игрока
                    iPlayerFireDelay = 3;
                    //задержка между появлениями ящика с боеприпасами
                    iAmmoDelay = 80;
                }
        ;
                break;
            case GAMELEVEL_NORMAL:
                {
                    //количество дополнительных попыток играющего
                    iMaxPlayerAttemptions = 2;
                    //задержка между вылетами вертолетов противника
                    iEnemyDelay = 6;
                    //Задержка между очередями
                    iEnemyBurstDelay = 20;
                    //задержка между выстрелами игрока
                    iPlayerFireDelay = 4;
                    //задержка между появлениями ящика с боеприпасами
                    iAmmoDelay = 100;
                }
        ;
                break;
            case GAMELEVEL_HARD:
                {
                    //количество дополнительных попыток играющего
                    iMaxPlayerAttemptions = 1;
                    //задержка между вылетами вертолетов противника
                    iEnemyDelay = 4;
                    //Задержка между очередями
                    iEnemyBurstDelay = 16;
                    //задержка между выстрелами игрока
                    iPlayerFireDelay = 4;
                    //задержка между появлениями ящика с боеприпасами
                    iAmmoDelay = 120;
                }
        ;
                break;
            default:
                return false;
        }
        //        i_SpeedScale = 12;

        //прочее
        iPlayerAttemptions = iMaxPlayerAttemptions;
        iPlayerAmmo = MAX_PLAYER_SHELLS_NUMBER;
        iEnemyFireDelay = 8;//8     // 2 - интервал между выстрелами в очереди
        iEnemyBurstLength = 2;//2   // максимальное число выстрелов в очереди

        iPlayerScore = 0;
        iLifeCount = 0;

        for (int li = 0; li < I_COPTERS_TYPE_NUMBER; li++)
        {
            aiActiveCoptersNumber[li] = 0;
        }

        //--------------------------------------------------
        setState(STATE_STARTED);
        return true;
    }

    /**
     * Деинициализация игровой сессии
     */
    public static final void releaseGame()
    {
        if (i_GameState == STATE_INITED || i_GameState == STATE_UNKNON) return;
        iScreenHeight = -1;
        iScreenWidth = -1;

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
        return iPlayerScore * (i_GameLevel + 1);
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
        i_GameStage = _stage;

        for (int li = 0; li < I_COPTERS_TYPE_NUMBER; li++)
        {
            aiActiveCoptersNumber[li] = 0;
            aiPassiveCoptersNumber[li] = 0;
        }

        switch (i_GameStage)
        {// описание стадий игры
                case 5:
                {
                    iBossOfs = OFS_BOSS_N3;
                    aiActiveCoptersNumber[0] = aiBossParams[iBossOfs + OFS_BOSS_TOWERS_COUNT];
                }
                break;
            case 10:
                {
                    iBossOfs = OFS_BOSS_N1;
                    aiActiveCoptersNumber[0] = aiBossParams[iBossOfs + OFS_BOSS_TOWERS_COUNT];
                }
                break;
            case 15:
                {
                    iBossOfs = OFS_BOSS_N2;
                    aiActiveCoptersNumber[0] = aiBossParams[iBossOfs + OFS_BOSS_TOWERS_COUNT];
                }
                break;
            case 20:
                {
                    iBossOfs = OFS_BOSS_N4;
                    aiActiveCoptersNumber[0] = aiBossParams[iBossOfs + OFS_BOSS_TOWERS_COUNT];
                }
                break;
            case 1:
                {
                    iBossOfs = -1;         // -1 = стадия без босса
                    iFireballDelay = -1;   // -1 = стадия без метеоритов
                    iFighterDelay = -1;    // -1 = стадия без истребителей
                    aiPassiveCoptersNumber[0] = 4;
                    aiMaxActiveCopters[0] = 2;
                    aiPassiveCoptersNumber[1] = 0;
                    aiMaxActiveCopters[1] = 0;
                    aiPassiveCoptersNumber[2] = 0;
                    aiMaxActiveCopters[2] = 0;
                    aiPassiveCoptersNumber[3] = 0;
                    aiMaxActiveCopters[3] = 0;
                }
                break;
            case 2:
                {
                    iBossOfs = -1;         // -1 = стадия без босса
                    iFireballDelay = -1;   // -1 = стадия без метеоритов
                    iFighterDelay = -1;    // -1 = стадия без истребителей
                    aiPassiveCoptersNumber[0] = 3;
                    aiMaxActiveCopters[0] = 2;
                    aiPassiveCoptersNumber[1] = 2;
                    aiMaxActiveCopters[1] = 1;
                    aiPassiveCoptersNumber[2] = 0;
                    aiMaxActiveCopters[2] = 0;
                    aiPassiveCoptersNumber[3] = 0;
                    aiMaxActiveCopters[3] = 0;
                }
                break;
            case 3:
                {
                    iBossOfs = -1;         // -1 = стадия без босса
                    iFireballDelay = 40;   // -1 = стадия без метеоритов
                    iFighterDelay = -1;    // -1 = стадия без истребителей
                    aiPassiveCoptersNumber[0] = 3;
                    aiMaxActiveCopters[0] = 2;
                    aiPassiveCoptersNumber[1] = 3;
                    aiMaxActiveCopters[1] = 1;
                    aiPassiveCoptersNumber[2] = 0;
                    aiMaxActiveCopters[2] = 0;
                    aiPassiveCoptersNumber[3] = 0;
                    aiMaxActiveCopters[3] = 0;
                }
                break;
            case 4:
                {
                    iBossOfs = -1;         // -1 = стадия без босса
                    iFireballDelay = -1;   // -1 = стадия без метеоритов
                    iFighterDelay = -1;    // -1 = стадия без истребителей
                    aiPassiveCoptersNumber[0] = 4;
                    aiMaxActiveCopters[0] = 1;
                    aiPassiveCoptersNumber[1] = 3;
                    aiMaxActiveCopters[1] = 1;
                    aiPassiveCoptersNumber[2] = 2;
                    aiMaxActiveCopters[2] = 1;
                    aiPassiveCoptersNumber[3] = 0;
                    aiMaxActiveCopters[3] = 0;
                }
                break;
            case 6:
                {
                    iBossOfs = -1;         // -1 = стадия без босса
                    iFireballDelay = 36;   // -1 = стадия без метеоритов
                    iFighterDelay = -1;    // -1 = стадия без истребителей
                    aiPassiveCoptersNumber[0] = 4;
                    aiMaxActiveCopters[0] = 1;
                    aiPassiveCoptersNumber[1] = 3;
                    aiMaxActiveCopters[1] = 1;
                    aiPassiveCoptersNumber[2] = 3;
                    aiMaxActiveCopters[2] = 1;
                    aiPassiveCoptersNumber[3] = 0;
                    aiMaxActiveCopters[3] = 0;
                }
                break;
            case 7:
                {
                    iBossOfs = -1;         // -1 = стадия без босса
                    iFireballDelay = -1;   // -1 = стадия без метеоритов
                    iFighterDelay = -1;    // -1 = стадия без истребителей
                    aiPassiveCoptersNumber[0] = 5;
                    aiMaxActiveCopters[0] = 1;
                    aiPassiveCoptersNumber[1] = 2;
                    aiMaxActiveCopters[1] = 1;
                    aiPassiveCoptersNumber[2] = 2;
                    aiMaxActiveCopters[2] = 1;
                    aiPassiveCoptersNumber[3] = 2;
                    aiMaxActiveCopters[3] = 1;
                }
                break;
            case 8:
                {
                    iBossOfs = -1;         // -1 = стадия без босса
                    iFireballDelay = 34;   // -1 = стадия без метеоритов
                    iFighterDelay = -1;    // -1 = стадия без истребителей
                    aiPassiveCoptersNumber[0] = 4;
                    aiMaxActiveCopters[0] = 1;
                    aiPassiveCoptersNumber[1] = 3;
                    aiMaxActiveCopters[1] = 1;
                    aiPassiveCoptersNumber[2] = 4;
                    aiMaxActiveCopters[2] = 1;
                    aiPassiveCoptersNumber[3] = 2;
                    aiMaxActiveCopters[3] = 1;
                }
                break;
            case 9:
                {
                    iBossOfs = -1;         // -1 = стадия без босса
                    iFireballDelay = -1;   // -1 = стадия без метеоритов
                    iFighterDelay = -1;    // -1 = стадия без истребителей
                    aiPassiveCoptersNumber[0] = 4;
                    aiMaxActiveCopters[0] = 1;
                    aiPassiveCoptersNumber[1] = 4;
                    aiMaxActiveCopters[1] = 1;
                    aiPassiveCoptersNumber[2] = 4;
                    aiMaxActiveCopters[2] = 1;
                    aiPassiveCoptersNumber[3] = 2;
                    aiMaxActiveCopters[3] = 1;
                }
                break;
            case 11:
                {
                    iBossOfs = -1;         // -1 = стадия без босса
                    iFireballDelay = -1;   // -1 = стадия без метеоритов
                    iFighterDelay = 60;    // -1 = стадия без истребителей
                    aiPassiveCoptersNumber[0] = 4;
                    aiMaxActiveCopters[0] = 1;
                    aiPassiveCoptersNumber[1] = 4;
                    aiMaxActiveCopters[1] = 1;
                    aiPassiveCoptersNumber[2] = 4;
                    aiMaxActiveCopters[2] = 1;
                    aiPassiveCoptersNumber[3] = 2;
                    aiMaxActiveCopters[3] = 1;
                }
                break;
            case 12:
                {
                    iBossOfs = -1;         // -1 = стадия без босса
                    iFireballDelay = 32;   // -1 = стадия без метеоритов
                    iFighterDelay = -1;    // -1 = стадия без истребителей
                    aiPassiveCoptersNumber[0] = 4;
                    aiMaxActiveCopters[0] = 1;
                    aiPassiveCoptersNumber[1] = 4;
                    aiMaxActiveCopters[1] = 1;
                    aiPassiveCoptersNumber[2] = 3;
                    aiMaxActiveCopters[2] = 1;
                    aiPassiveCoptersNumber[3] = 3;
                    aiMaxActiveCopters[3] = 1;
                }
                break;
            case 13:
                {
                    iBossOfs = -1;         // -1 = стадия без босса
                    iFireballDelay = -1;   // -1 = стадия без метеоритов
                    iFighterDelay = 55;    // -1 = стадия без истребителей
                    aiPassiveCoptersNumber[0] = 4;
                    aiMaxActiveCopters[0] = 1;
                    aiPassiveCoptersNumber[1] = 6;
                    aiMaxActiveCopters[1] = 2;
                    aiPassiveCoptersNumber[2] = 0;
                    aiMaxActiveCopters[2] = 0;
                    aiPassiveCoptersNumber[3] = 3;
                    aiMaxActiveCopters[3] = 1;
                }
                break;
            case 14:
                {
                    iBossOfs = -1;         // -1 = стадия без босса
                    iFireballDelay = -1;   // -1 = стадия без метеоритов
                    iFighterDelay = -1;    // -1 = стадия без истребителей
                    aiPassiveCoptersNumber[0] = 6;
                    aiMaxActiveCopters[0] = 1;
                    aiPassiveCoptersNumber[1] = 0;
                    aiMaxActiveCopters[1] = 0;
                    aiPassiveCoptersNumber[2] = 4;
                    aiMaxActiveCopters[2] = 2;
                    aiPassiveCoptersNumber[3] = 4;
                    aiMaxActiveCopters[3] = 1;
                }
                break;
            case 16:
                {
                    iBossOfs = -1;         // -1 = стадия без босса
                    iFireballDelay = 30;   // -1 = стадия без метеоритов
                    iFighterDelay = -1;    // -1 = стадия без истребителей
                    aiPassiveCoptersNumber[0] = 0;
                    aiMaxActiveCopters[0] = 0;
                    aiPassiveCoptersNumber[1] = 6;
                    aiMaxActiveCopters[1] = 2;
                    aiPassiveCoptersNumber[2] = 4;
                    aiMaxActiveCopters[2] = 1;
                    aiPassiveCoptersNumber[3] = 3;
                    aiMaxActiveCopters[3] = 1;
                }
                break;
            case 17:
                {
                    iBossOfs = -1;         // -1 = стадия без босса
                    iFireballDelay = -1;   // -1 = стадия без метеоритов
                    iFighterDelay = 55;    // -1 = стадия без истребителей
                    aiPassiveCoptersNumber[0] = 0;
                    aiMaxActiveCopters[0] = 0;
                    aiPassiveCoptersNumber[1] = 6;
                    aiMaxActiveCopters[1] = 2;
                    aiPassiveCoptersNumber[2] = 4;
                    aiMaxActiveCopters[2] = 1;
                    aiPassiveCoptersNumber[3] = 4;
                    aiMaxActiveCopters[3] = 1;
                }
                break;
            case 18:
                {
                    iBossOfs = -1;         // -1 = стадия без босса
                    iFireballDelay = 28;   // -1 = стадия без метеоритов
                    iFighterDelay = 55;    // -1 = стадия без истребителей
                    aiPassiveCoptersNumber[0] = 0;
                    aiMaxActiveCopters[0] = 0;
                    aiPassiveCoptersNumber[1] = 6;
                    aiMaxActiveCopters[1] = 1;
                    aiPassiveCoptersNumber[2] = 5;
                    aiMaxActiveCopters[2] = 2;
                    aiPassiveCoptersNumber[3] = 4;
                    aiMaxActiveCopters[3] = 1;
                }
                break;
            case 19:
                {
                    iBossOfs = -1;         // -1 = стадия без босса
                    iFireballDelay = 28;   // -1 = стадия без метеоритов
                    iFighterDelay = 50;    // -1 = стадия без истребителей
                    aiPassiveCoptersNumber[0] = 0;
                    aiMaxActiveCopters[0] = 0;
                    aiPassiveCoptersNumber[1] = 6;
                    aiMaxActiveCopters[1] = 1;
                    aiPassiveCoptersNumber[2] = 6;
                    aiMaxActiveCopters[2] = 2;
                    aiPassiveCoptersNumber[3] = 5;
                    aiMaxActiveCopters[3] = 1;

                }
                break;
        }
    ;
        if (iBossOfs >= 0)
        {
            /** расстояния от основной точки спрайта вертушки противника до краев этого спрайта */
            I8_BOSS_BOUNDS_X = (int) (((long) aiSpriteParameters[aiBossParams[iBossOfs + OFS_BOSS_SPRITE] * SPRITE_DATA_LENGTH] * SCALE_WIDTH) >> 9);
            /** расстояния от основной точки спрайта вертушки противника до краев этого спрайта */
            I8_BOSS_BOUNDS_Y = (int) (((long) aiSpriteParameters[aiBossParams[iBossOfs + OFS_BOSS_SPRITE] * SPRITE_DATA_LENGTH + 1] * SCALE_HEIGHT) >> 9);

            for (int li = 0; li < aiBossParams[iBossOfs + OFS_BOSS_TOWERS_COUNT]; li++)
            {
                ap_CopterSprites[li].i_ObjectState = aiBossParams[iBossOfs + OFS_BOSS_TOWERS_ + li * BOSS_TOWERS_DATA_LENGTH + OFS_TOWER_HEALTH];
            }
        }

        beginNewAttemtion();
        save4rollback();

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
        DataInputStream s = new DataInputStream(p_arrayInputStream);
        s.skip(16);
        int i_gLevel = s.readUnsignedByte();
        int i_gStage = s.readUnsignedByte();
        int i_gScreenWidth = s.readUnsignedShort();
        int i_gScreenHeight = s.readUnsignedShort();
        if (!initNewGame(i_gScreenWidth, i_gScreenHeight, i_gLevel)) throw new Exception();
        if (!initGameStage(i_gStage)) throw new Exception();
        iPlayerAttemptions = s.readByte();
        iMaxPlayerAttemptions = s.readByte();
        rollbackScore = s.readInt();
        rollbackAttemptions = s.readByte();
        rollbackLifeCount = s.readByte();
        //------------Вставьте свой код здесь--------------------
        iActiveFireballsNumber = s.readByte();
        iBossOfs = s.readShort();
        for (int li = 0; li < I_COPTERS_TYPE_NUMBER; li++)
        {
            aiActiveCoptersNumber[li] = s.readByte();
            aiMaxActiveCopters[li] = s.readByte();
            aiPassiveCoptersNumber[li] = s.readByte();
        }
        iPlayerAmmo = s.readByte();
        iPlayerSpecMode = s.readByte();
        iPlayerScore = s.readShort();

        activateSprite(p_MyCopterSprite, s.readByte());
        int i8_MyX = s.readInt();
        int i8_MyY = s.readInt();
        p_MyCopterSprite.setMainPointXY(i8_MyX, i8_MyY);
        p_MyCopterSprite.lg_SpriteActive = s.readBoolean();

        if (s.readBoolean())
            activateSprite(p_AmmoSprite, SPRITE_AMMO);
        else
            p_AmmoSprite.lg_SpriteActive = false;
        p_AmmoSprite.setMainPointXY(s.readInt(), s.readInt());
        p_AmmoSprite.i_ObjectState = I8_AMMOnLIFE_SPEED;

        if (s.readBoolean())
            activateSprite(p_LifeSprite, SPRITE_LIFE);
        else
            p_LifeSprite.lg_SpriteActive = false;
        p_LifeSprite.setMainPointXY(s.readInt(), s.readInt());
        p_LifeSprite.i_ObjectState = I8_AMMOnLIFE_SPEED;

        iLifeCount = s.readByte();

        p_FighterSprite.lg_SpriteActive = s.readBoolean();
        p_FighterSprite.i_ObjectType = s.readByte();
        p_FighterSprite.i8_mainX = s.readInt();
        p_FighterSprite.i8_mainY = s.readInt();

        for (int li = 0; li < MAX_SCREEN_COPTERS_NUMBER; li++)
        {
            Sprite p_Sprite = ap_CopterSprites[li];
            byte iObjectType = s.readByte();
            if (iObjectType == SPRITE_NONE)
            {
                p_Sprite.i_ObjectState = s.readByte();
                s.skipBytes(4 + 4 + 4 + 4);
                p_Sprite.lg_SpriteActive = false;
                continue;
            }
            activateSprite(p_Sprite, iObjectType);
            p_Sprite.i_ObjectState = s.readByte();

            int i8X = s.readInt();
            int i8Y = s.readInt();
            p_Sprite.setMainPointXY(i8X, i8Y);
            ai8CopterVX[li] = s.readInt();
            ai8CopterVY[li] = s.readInt();
        }

        for (int li = 0; li < MAX_SHELLS_NUMBER; li++)
        {
            Sprite p_Sprite = ap_ShellAndFireballSprites[li];
            byte iObjectType = s.readByte();
            if (iObjectType == SPRITE_NONE)
            {
                s.skipBytes(4 + 4);
                p_Sprite.lg_SpriteActive = false;
                continue;
            }
            activateSprite(p_Sprite, iObjectType);
            int i8X = s.readInt();
            int i8Y = s.readInt();
            p_Sprite.setMainPointXY(i8X, i8Y);
        }

        //--------------------------------------------------
        s.close();
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
        DataOutputStream s = new DataOutputStream(p_arrayOutputStream);
        s.writeLong(0);
        s.writeLong(0);
        s.writeByte(i_GameLevel);
        s.writeByte(i_GameStage);
        s.writeShort(iScreenWidth);
        s.writeShort(iScreenHeight);
        s.writeByte(iPlayerAttemptions);
        s.writeByte(iMaxPlayerAttemptions);
        s.writeInt(rollbackScore);
        s.writeByte(rollbackAttemptions);
        s.writeByte(rollbackLifeCount);

        //------------Вставьте свой код здесь--------------------
        s.writeByte(iActiveFireballsNumber);
        s.writeShort(iBossOfs);
        for (int li = 0; li < I_COPTERS_TYPE_NUMBER; li++)
        {
            s.writeByte(aiActiveCoptersNumber[li]);
            s.writeByte(aiMaxActiveCopters[li]);
            s.writeByte(aiPassiveCoptersNumber[li]);
        }
        s.writeByte(iPlayerAmmo);
        s.writeByte(iPlayerSpecMode);
        s.writeShort(iPlayerScore);

        s.writeByte(p_MyCopterSprite.i_ObjectType);
        s.writeInt(p_MyCopterSprite.i8_mainX);
        s.writeInt(p_MyCopterSprite.i8_mainY);
        s.writeBoolean(p_MyCopterSprite.lg_SpriteActive);

        s.writeBoolean(p_AmmoSprite.lg_SpriteActive);
        s.writeInt(p_AmmoSprite.i8_mainX);
        s.writeInt(p_AmmoSprite.i8_mainY);
        s.writeBoolean(p_LifeSprite.lg_SpriteActive);
        s.writeInt(p_LifeSprite.i8_mainX);
        s.writeInt(p_LifeSprite.i8_mainY);
        s.writeByte(iLifeCount);

        s.writeBoolean(p_FighterSprite.lg_SpriteActive);
        s.writeByte(p_FighterSprite.i_ObjectType);
        s.writeInt(p_FighterSprite.i8_mainX);
        s.writeInt(p_FighterSprite.i8_mainY);

        for (int li = 0; li < MAX_SCREEN_COPTERS_NUMBER; li++)
        {
            Sprite p_Sprite = ap_CopterSprites[li];
            if (p_Sprite.lg_SpriteActive)
                s.writeByte(p_Sprite.i_ObjectType);
            else
                s.writeByte(SPRITE_NONE);
            s.writeByte(p_Sprite.i_ObjectState);
            s.writeInt(p_Sprite.i8_mainX);
            s.writeInt(p_Sprite.i8_mainY);
            s.writeInt(ai8CopterVX[li]);
            s.writeInt(ai8CopterVY[li]);
        }

        for (int li = 0; li < MAX_SHELLS_NUMBER; li++)
        {
            Sprite p_Sprite = ap_ShellAndFireballSprites[li];
            if (p_Sprite.lg_SpriteActive)
                s.writeByte(p_Sprite.i_ObjectType);
            else
                s.writeByte(SPRITE_NONE);
            s.writeInt(p_Sprite.i8_mainX);
            s.writeInt(p_Sprite.i8_mainY);
        }

        //--------------------------------------------------
        s.close();
        byte[] ab_result = p_arrayOutputStream.toByteArray();
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
        int Size = 16 + 7 + 6;
        //------------Вставьте свой код здесь--------------------

        Size += 47 + 12;

        Size += 18 * MAX_SCREEN_COPTERS_NUMBER;

        Size += 9 * MAX_SHELLS_NUMBER;
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
        return "RRG_COPTERS";
    }

    public static final void rollbackStage()
    {
        iPlayerScore = rollbackScore;
        iPlayerAttemptions = rollbackAttemptions;
        iLifeCount = rollbackLifeCount;
    }

    private static final void save4rollback()
    {
        rollbackScore = iPlayerScore;
        rollbackAttemptions = iPlayerAttemptions;
        rollbackLifeCount = iLifeCount;
    }

    /**
     * Функция отрабатывает игровой шаг на последней стадии
     *
     * @param _keyStateFlags флаги управления игроком.
     * @return статус игры после отработки игровой интерации
     */
    public static final int nextGameStepBoss(int _keyStateFlags)
    {
        //------------Вставьте свой код здесь--------------------
        boolean b_PlayerHealth = processMyCopter(_keyStateFlags);
        if (iPlayerSpecMode == psmFLIGHTIN) return i_GameState;
        boolean b_ShellsExist = processShellsAndFireballs();
        if (processSprite(p_AmmoSprite))
        {
            iPlayerAmmo = MAX_PLAYER_SHELLS_NUMBER;
            iPlayerScore += BONUS_AMMO;
            startup.processGameAction(GAMEACTION_PLAYER_GET_AMMO);
            //#if BONUS
            generateBonus(p_AmmoSprite, SPRITE_BONUS_AMMO);
            //#endif
        }
        if (processSprite(p_LifeSprite))
        {
            if (iPlayerAttemptions < iMaxPlayerAttemptions)
            {
                iPlayerAttemptions++;
                startup.processGameAction(GAMEACTION_ATTEMPTION_CHANGE);
            }
            startup.processGameAction(GAMEACTION_PLAYER_GET_LIFE);
            iPlayerScore += BONUS_LIFE;
            //#if BONUS
            generateBonus(p_LifeSprite, SPRITE_BONUS_LIFE);
            //#endif
        }
        //        boolean b_BossExist
        if (iTotalActiveCoptersNumber > 0)
            processBoss((_keyStateFlags & BUTTON_UP) != 0);

        processExplosions();
        processBonus();

        if (b_PlayerHealth)
        {
            // генерируем жизнь
            if (iPlayerScore >= (iLifeCount + 1) * BONUS_NEEDED_FOR_LIFE)
            {
                activateSprite(p_LifeSprite, SPRITE_LIFE);
                iLifeCount++;
                p_LifeSprite.setMainPointXY(getRandomInt((iScreenWidth << 8) - I8_LIFE_BOUNDS_Y * 2) + I8_LIFE_BOUNDS_Y, -I8_LIFE_BOUNDS_Y);
                p_LifeSprite.i_ObjectState = I8_AMMOnLIFE_SPEED;
                startup.processGameAction(GAMEACTION_LIFE);
            }
            // генерируем боеприпасы
            if (iAmmoCounter == 0)
            {
                iAmmoCounter = iAmmoDelay;
                if (!p_AmmoSprite.lg_SpriteActive && ((getRandomInt(iPlayerAmmo * I8_PROBABILITY) >> 8) == 0))
                {
                    activateSprite(p_AmmoSprite, SPRITE_AMMO);
                    p_AmmoSprite.setMainPointXY(getRandomInt((iScreenWidth << 8) - I8_AMMO_BOUNDS_X * 2) + I8_AMMO_BOUNDS_X, -I8_AMMO_BOUNDS_Y);
                    p_AmmoSprite.i_ObjectState = I8_AMMOnLIFE_SPEED;
                    startup.processGameAction(GAMEACTION_AMMO);
                }
            }
            else
                iAmmoCounter--;
        }
        // Проверка на победу
        if ((iTotalActiveCoptersNumber == 0))
        {
            if (ai8CopterVX[1] == 0)
            {
                ai8CopterVX[1] = 1;
                startup.processGameAction(GAMEACTION_VICTORY);
                ai8CopterVX[0] = I8_BOSS_DEATH_X_SPEED;
                ai8CopterVY[0] = I8_BOSS_DEATH_Y_SPEED;
            }
            //если босс не исчез с экрана
            if (p_FighterSprite.i8_mainY < (iScreenHeight << 8) + I8_BOSS_BOUNDS_Y)
            {
                final int i8_vx = (ai8CopterVX[0] * I8_FIN_BOSS_AXCELERATION) >> 8;
                final int i8_vy = (ai8CopterVY[0] * I8_FIN_BOSS_AXCELERATION) >> 8;
                p_FighterSprite.moveMainPointXY(i8_vx, i8_vy);
                ai8CopterVX[0] = i8_vx;
                ai8CopterVY[0] = i8_vy;
                ap_CopterSprites[0].setMainPointXY(p_FighterSprite.i8_ScreenX + p_FighterSprite.i8_col_offsetX + getRandomInt(p_FighterSprite.i8_col_width),
                        p_FighterSprite.i8_ScreenY + p_FighterSprite.i8_col_offsetY + getRandomInt(p_FighterSprite.i8_col_height));
                generateExplosion(ap_CopterSprites[0]);
            }
            else
            {
                p_FighterSprite.lg_SpriteActive = false;
                if ((!b_ShellsExist) && (!p_LifeSprite.lg_SpriteActive))
                    if (i_GameStage == MAX_STAGE_NUMBER)
                    //на последнем уровне вылетаем вверх
                        iPlayerSpecMode = psmFLIGHTUP;
                    else
                    //на остальных право
                        iPlayerSpecMode = psmFLIGHTOUT;
            }
        }

        // Проверка игры на окончание
        if ((iPlayerSpecMode == psmFLIGHTOUT))
        {
            if ((p_MyCopterSprite.i8_mainX >= ((iScreenWidth << 8) + I8_MY_BOUNDS_X)) && (!p_AmmoSprite.lg_SpriteActive))
            {
                // Игрок прошел уровень
                setState(STATE_OVER);
                i_PlayerState = PLAYER_WIN;
            }
            else
            {
                p_AmmoSprite.i_ObjectState = (p_AmmoSprite.i_ObjectState * I8_FIN_AXCELERATION) >> 8;
            }
        }
        else if ((iPlayerSpecMode == psmFLIGHTUP))
        {
            if ((p_MyCopterSprite.i8_mainY <= -I8_MY_BOUNDS_Y) && (!p_AmmoSprite.lg_SpriteActive))
            {
                // Игрок выиграл
                setState(STATE_OVER);
                i_PlayerState = PLAYER_WIN;
                iPlayerScore += iPlayerAttemptions * BONUS_ATTEMPTION;
            }
            else
            {
                p_AmmoSprite.i_ObjectState = (p_AmmoSprite.i_ObjectState * I8_FIN_AXCELERATION) >> 8;
            }
        }
        //        else if ((!bPlayerHealth) && (iTotalActiveCoptersNumber == 0) &&  && (!p_FighterSprite.lg_SpriteActive))
        if ((!b_PlayerHealth) &&
                    (!p_FighterSprite.lg_SpriteActive) &&
                    (iActiveFireballsNumber == 0) &&
                    (!p_LifeSprite.lg_SpriteActive) &&
                    (!b_ShellsExist) &&
                    (!p_AmmoSprite.lg_SpriteActive))
        {

            if (iPostplayCounter > 0)
                iPostplayCounter--;
            else
            {
                if (iPlayerAttemptions == 0)
                {// Игрок проиграл
                    i_PlayerState = PLAYER_LOST;
                    setState(STATE_OVER);
                }
                else if ((iTotalActiveCoptersNumber == 0))
                {// Игрок лишился попытки но прошел уровень
                    iPlayerAttemptions--;
                    startup.processGameAction(GAMEACTION_ATTEMPTION_CHANGE);
                    setState(STATE_OVER);
                    i_PlayerState = PLAYER_WIN;
                }
                else
                {// Игрок лишился попытки
                    iPlayerAttemptions--;
                    startup.processGameAction(GAMEACTION_ATTEMPTION_CHANGE);
                    beginNewAttemtion();
                }
            }
        }
        //--------------------------------------------------
        return i_GameState;
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
        iTotalActiveCoptersNumber = 0;
        iTotalPassiveCoptersNumber = 0;
        for (int li = 0; li < I_COPTERS_TYPE_NUMBER; li++)
        {
            iTotalActiveCoptersNumber += aiActiveCoptersNumber[li];
            iTotalPassiveCoptersNumber += aiPassiveCoptersNumber[li];
        }

        if (iBossOfs >= 0) return nextGameStepBoss(_keyStateFlags);

        boolean b_ShellsExist = processShellsAndFireballs();
        boolean bPlayerHealth = processMyCopter(_keyStateFlags);
        if (iPlayerSpecMode == psmFLIGHTIN) return i_GameState;
        if (processSprite(p_AmmoSprite))
        {
            iPlayerAmmo = MAX_PLAYER_SHELLS_NUMBER;
            iPlayerScore += BONUS_AMMO;
            //#if BONUS
            generateBonus(p_AmmoSprite, SPRITE_BONUS_AMMO);
            //#endif
            startup.processGameAction(GAMEACTION_PLAYER_GET_AMMO);
        }
        if (processSprite(p_LifeSprite))
        {
            if (iPlayerAttemptions < iMaxPlayerAttemptions)
            {
                iPlayerAttemptions++;
                startup.processGameAction(GAMEACTION_ATTEMPTION_CHANGE);
            }
            startup.processGameAction(GAMEACTION_PLAYER_GET_LIFE);
            //#if BONUS
            generateBonus(p_LifeSprite, SPRITE_BONUS_LIFE);
            //#endif
            iPlayerScore += BONUS_LIFE;
        }
        final boolean bFighterExist = processFighter();
        processCopters();
        processExplosions();
        processBonus();

        if (bPlayerHealth)
        {
            // генерируем жизнь
            if (iPlayerScore >= (iLifeCount + 1) * BONUS_NEEDED_FOR_LIFE)
            {
                activateSprite(p_LifeSprite, SPRITE_LIFE);
                iLifeCount++;
                p_LifeSprite.setMainPointXY(getRandomInt((iScreenWidth << 8) - I8_LIFE_BOUNDS_Y * 2) + I8_LIFE_BOUNDS_Y, -I8_LIFE_BOUNDS_Y);
                p_LifeSprite.i_ObjectState = I8_AMMOnLIFE_SPEED;
                startup.processGameAction(GAMEACTION_LIFE);
            }
            // генерируем боеприпасы
            if (iAmmoCounter == 0)
            {
                iAmmoCounter = iAmmoDelay;
                if (!p_AmmoSprite.lg_SpriteActive && ((getRandomInt(iPlayerAmmo * I8_PROBABILITY) >> 8) == 0))
                {
                    activateSprite(p_AmmoSprite, SPRITE_AMMO);
                    p_AmmoSprite.setMainPointXY(getRandomInt((iScreenWidth << 8) - I8_AMMO_BOUNDS_X * 2) + I8_AMMO_BOUNDS_X, -I8_AMMO_BOUNDS_Y);
                    p_AmmoSprite.i_ObjectState = I8_AMMOnLIFE_SPEED;
                    startup.processGameAction(GAMEACTION_AMMO);
                }
            }
            else
                iAmmoCounter--;

            // генерируем истребитель
            if (iFighterCounter > 0)
                iFighterCounter--;
            else if (iFighterCounter == 0)
            {
                iFighterCounter = iFighterDelay;
                if (!bFighterExist && (iTotalActiveCoptersNumber > 0) && ((getRandomInt(I8_PROBABILITY) >> 8) == 0))
                {
                    int i8_x;
                    if (getRandomInt(1) == 0)
                    {
                        activateSprite(p_FighterSprite, SPRITE_FIGHTER_RIGHT);
                        i8_x = -I8_FIGHTER_BOUNDS_X;
                    }
                    else
                    {
                        activateSprite(p_FighterSprite, SPRITE_FIGHTER_LEFT);
                        i8_x = (iScreenWidth << 8) + I8_FIGHTER_BOUNDS_X;
                    }
                    p_FighterSprite.setMainPointXY(i8_x, getRandomInt((iScreenHeight << 8) - I8_FIGHTER_BOUNDS_Y * 2) + I8_FIGHTER_BOUNDS_Y);
                    iFighterFlightCounter = I_FIGHTER_FLIGHT_DELAY;
                    startup.processGameAction(GAMEACTION_FIGHTER);
                }
            }

            // генерируем метеориты
            if (iFireballCounter > 0)
                iFireballCounter--;
            else if (iFireballCounter == 0)
            {
                iFireballCounter = iFireballDelay;
                if ((iActiveFireballsNumber < MAX_FIREBALLS_NUMBER) && (iTotalActiveCoptersNumber > 0) && ((getRandomInt(I8_PROBABILITY) >> 8) == 0))
                {
                    generateNewFireball();
                }
            }

            // генерируем вертушки противника
            if (iEnemyCounter == 0)
            {
                iEnemyCounter = iEnemyDelay;
                for (int li = 0; li < I_COPTERS_TYPE_NUMBER; li++)
                {
                    if ((aiActiveCoptersNumber[li] < aiMaxActiveCopters[li]) && (aiPassiveCoptersNumber[li] > 0) && ((iTotalActiveCoptersNumber == 0) || ((getRandomInt(I8_PROBABILITY * 4) >> 8) == 0)))
                    {
                        if (generateNewCopter(li))
                        {
                            aiActiveCoptersNumber[li]++;
                            aiPassiveCoptersNumber[li]--;
                            iTotalActiveCoptersNumber++;
                            iTotalPassiveCoptersNumber--;
                            break;
                        }
                    }
                }
            }
            else
                iEnemyCounter--;
        }
        // Проверка на победу
        if ((iPlayerSpecMode == psmNONE) && (iTotalActiveCoptersNumber == 0) && (iTotalPassiveCoptersNumber == 0) && (!b_ShellsExist) && (!bFighterExist) && (!p_LifeSprite.lg_SpriteActive))
        {
            iPlayerSpecMode = psmFLIGHTOUT;
            startup.processGameAction(GAMEACTION_VICTORY);
        }

        // Проверка игры на окончание
        if ((iPlayerSpecMode == psmFLIGHTOUT))
        {
            if ((p_MyCopterSprite.i8_mainX >= ((iScreenWidth << 8) + I8_MY_BOUNDS_X)) && (!p_AmmoSprite.lg_SpriteActive))
            {
                // Игрок прошел уровень
                setState(STATE_OVER);
                i_PlayerState = PLAYER_WIN;
            }
            else
            {
                p_AmmoSprite.i_ObjectState = (p_AmmoSprite.i_ObjectState * I8_FIN_AXCELERATION) >> 8;
            }
        }

        if ((!bPlayerHealth) && (iTotalActiveCoptersNumber == 0) && (iActiveFireballsNumber == 0) && (!p_LifeSprite.lg_SpriteActive) && (!p_AmmoSprite.lg_SpriteActive) && (!p_FighterSprite.lg_SpriteActive))
        {
            if (iPostplayCounter > 0)
                iPostplayCounter--;
            else
            {
                if (iPlayerAttemptions == 0)
                {// Игрок проиграл
                    i_PlayerState = PLAYER_LOST;
                    setState(STATE_OVER);
                }
                else if ((iTotalPassiveCoptersNumber == 0))
                {// Игрок лишился попытки но прошел уровень
                    iPlayerAttemptions--;
                    i_PlayerState = PLAYER_WIN;
                    setState(STATE_OVER);
                    startup.processGameAction(GAMEACTION_ATTEMPTION_CHANGE);
                }
                else
                {// Игрок лишился попытки
                    iPlayerAttemptions--;
                    startup.processGameAction(GAMEACTION_ATTEMPTION_CHANGE);
                    beginNewAttemtion();
                }
            }
        }

        //--------------------------------------------------
        return i_GameState;
    }

    /**
     * Начало новой попытки
     */
    protected static void beginNewAttemtion()
    {
        startup.processGameAction(GAMEACTION_GAME_START);

        activateSprite(p_MyCopterSprite, SPRITE_MYCOPTER_FLIGHT_INOUT);
        setMyPosition(-I8_MY_BOUNDS_X, iScreenHeight << 7);
        iPlayerSpecMode = psmFLIGHTIN;
        iActiveFireballsNumber = 0;
        iPlayerFireCounter = 0;
        iEnemyCounter = 0;
        iAmmoCounter = 0;
        iSmokeCounter = 0;
        iPostplayCounter = I_POSTPLAY_DELAY;

        for (int li = 0; li < MAX_SCREEN_COPTERS_NUMBER; li++)
        {
            ap_CopterSprites[li].lg_SpriteActive = false;
            aiEnemyBurstCounter[li] = 0;
        }

        for (int li = 0; li < MAX_SHELLS_NUMBER; li++)
        {
            ap_ShellAndFireballSprites[li].lg_SpriteActive = false;
        }

        for (int li = 0; li < MAX_EXPLOSIONS_NUMBER; li++)
        {
            ap_ExplosionSprites[li].lg_SpriteActive = false;
        }

        p_AmmoSprite.lg_SpriteActive = false;
        p_LifeSprite.lg_SpriteActive = false;
        p_FighterSprite.lg_SpriteActive = false;
        //#if BONUS
        for (int li = 0; li < MAX_BONUS_NUMBER; li++)
            ap_BonusSprites[li].lg_SpriteActive = false;
        //#endif

        iPlayerAmmo = MAX_PLAYER_SHELLS_NUMBER;
        if (iBossOfs >= 0)
        {   //стадия с Боссом
            activateSprite(p_FighterSprite, aiBossParams[iBossOfs + OFS_BOSS_SPRITE]);

            int i8_offsetX = (iScreenWidth << 8) + I8_BOSS_BOUNDS_X;
            int i8_offsetY = (iScreenHeight << 7);

            p_FighterSprite.setMainPointXY(i8_offsetX, i8_offsetY);
            ai8CopterVX[0] = I8_BOSS_X_SPEED;
            ai8CopterVY[0] = I8_BOSS_Y_SPEED;
            ai8CopterVX[1] = 0;

            int i_mainX = p_FighterSprite.i8_mainX;
            int i_mainY = p_FighterSprite.i8_mainY;

            for (int li = 0; li < aiBossParams[iBossOfs + OFS_BOSS_TOWERS_COUNT]; li++)
            {
                activateSprite(ap_CopterSprites[li], aiBossParams[iBossOfs + OFS_BOSS_TOWER_SPRITE]);
                ap_CopterSprites[li].i_spriteID = li;
                //                    ap_CopterSprites[li].i_ObjectState = aiBossParams[iBossOfs + OFS_BOSS_TOWERS_ + li*BOSS_TOWERS_DATA_LENGTH + OFS_TOWER_HEALTH];

                if (ap_CopterSprites[li].i_ObjectState < 0)
                    ap_CopterSprites[li].lg_SpriteActive = false;
                else
                    ap_CopterSprites[li].setMainPointXY(
                            i_mainX + aiBossParams[iBossOfs + OFS_BOSS_TOWERS_ + li * BOSS_TOWERS_DATA_LENGTH + OFS_TOWER_X],
                            i_mainY + aiBossParams[iBossOfs + OFS_BOSS_TOWERS_ + li * BOSS_TOWERS_DATA_LENGTH + OFS_TOWER_Y]);
            }
        }
        else
        {//стадия без босса
            for (int li = 0; li < I_COPTERS_TYPE_NUMBER; li++)
            {
                aiPassiveCoptersNumber[li] += aiActiveCoptersNumber[li];
                aiActiveCoptersNumber[li] = 0;
            }
            iFireballCounter = iFireballDelay;
            iFighterCounter = iFighterDelay;
            iFighterFlightCounter = 0;
        }
    }

    /**
     * Генерация метеорита
     */
    protected static boolean generateNewFireball()
    {
        // Ищем свободный спрайт
        Sprite p_Sprite = getFirstInactiveSprite(ap_ShellAndFireballSprites);
        if (p_Sprite == null) return false;

        activateSprite(p_Sprite, SPRITE_FIREBALL);
        p_Sprite.setMainPointXY(getRandomInt(iScreenWidth << 8), -I8_FIREBALL_BOUNDS_Y);
        startup.processGameAction(GAMEACTION_FIREBALL);
        iActiveFireballsNumber++;
        return true;
    }

    /**
     * Генерация новой вертушки противника
     */
    protected static boolean generateNewCopter(int Type)
    {
        // Ищем свободный спрайт
        int li = getFirstInactiveSpriteNum(ap_CopterSprites);
        if (li < 0) return false;

        int i8_x, i8_y;

        int i8_vx = (int) ((long) I8_COPTER_Y_SPEED[Type]);
        int i8_vy = (int) ((long) I8_COPTER_X_SPEED[Type]);

        switch (getRandomInt(2))
        {
            case 2:// Вылет вертушки справа
                i8_y = -I8_COPTER_BOUNDS_Y + getRandomInt(iScreenHeight << 8);
                i8_x = (iScreenWidth << 8) + I8_COPTER_BOUNDS_X;
                ai8CopterVY[li] = getRandomInt(i8_vy * 2) - i8_vy;
                ai8CopterVX[li] = -getRandomInt(i8_vx) - 1;
                break;
            case 1:// Вылет вертушки слева
                i8_y = -I8_COPTER_BOUNDS_Y + getRandomInt(iScreenHeight << 8);
                i8_x = -I8_COPTER_BOUNDS_X;
                ai8CopterVY[li] = getRandomInt(i8_vy * 2) - i8_vy;
                ai8CopterVX[li] = getRandomInt(i8_vx) + 1;
                break;
            default:// Вылет вертушки сверху
                i8_x = -I8_COPTER_BOUNDS_X + getRandomInt((iScreenHeight << 8) + I8_COPTER_BOUNDS_X * 2);
                i8_y = -I8_COPTER_BOUNDS_Y;
                ai8CopterVX[li] = getRandomInt(i8_vx * 2) - i8_vx;
                ai8CopterVY[li] = getRandomInt(i8_vy) + 1;
                break;
        }

        if (Math.abs(ai8CopterVX[li]) < I8_MIN_COPTER_SPEED)
            ai8CopterVX[li] = I8_MIN_COPTER_SPEED * sign(ai8CopterVX[li]);
        if (Math.abs(ai8CopterVY[li]) < I8_MIN_COPTER_SPEED)
            ai8CopterVY[li] = I8_MIN_COPTER_SPEED * sign(ai8CopterVY[li]);

        int i_Sprite;

        i_Sprite = SPRITE_COPTER1_ + Type * SPRITE_COPTER_LENGHT + SPRITE_MYCOPTER_RIGHT;

        if (ai8CopterVX[li] < 0)
            i_Sprite += -SPRITE_MYCOPTER_RIGHT + SPRITE_MYCOPTER_LEFT;

        activateSprite(ap_CopterSprites[li], i_Sprite);
        ap_CopterSprites[li].setMainPointXY(i8_x, i8_y);
        return true;
    }

    /**
     * Обработка взрывов:
     * - анимация и уничтожение
     *
     * @return true если взрывы присутствуют
     */
    private static boolean processExplosions()
    {
        boolean lg_exists = false;

        for (int li = 0; li < MAX_EXPLOSIONS_NUMBER; li++)
        {
            Sprite p_Sprite = ap_ExplosionSprites[li];
            if (!p_Sprite.lg_SpriteActive) continue;
            lg_exists = true;

            if (p_Sprite.processAnimation())
            {
                p_Sprite.lg_SpriteActive = false;
            }
        ;
        }

        return lg_exists;
    }

    //#if BONUS
    /**
     * Обработка бонусов:
     * - анимация и уничтожение
     */
    private static void processBonus()
    {
        Sprite p_Sprite;
        for (int li = 0; li < MAX_EXPLOSIONS_NUMBER; li++)
        {
            p_Sprite = ap_BonusSprites[li];
            if (!p_Sprite.lg_SpriteActive) continue;

            if (p_Sprite.processAnimation())
                p_Sprite.lg_SpriteActive = false;
            else
                p_Sprite.moveMainPointXY(I8_BONUS_SPEED_X, I8_BONUS_SPEED_Y);
        }
    }
    //#endif

    /**
     * переводим игрока в режим падения
     */
    private static void fallPlayer()
    {
        if (p_MyCopterSprite.i_ObjectType == SPRITE_MYCOPTER_RIGHT)
            activateSprite(p_MyCopterSprite, SPRITE_MYCOPTER_FALL_RTL);
        if (p_MyCopterSprite.i_ObjectType == SPRITE_MYCOPTER_LEFT)
            activateSprite(p_MyCopterSprite, SPRITE_MYCOPTER_FALL_LTR);
        if (p_MyCopterSprite.i_ObjectType == SPRITE_MYCOPTER_RTL)
        {
            int i_Frame = p_MyCopterSprite.i_Frame;
            activateSprite(p_MyCopterSprite, SPRITE_MYCOPTER_FALL_RTL);
            p_MyCopterSprite.i_Frame = i_Frame;
        }
        if (p_MyCopterSprite.i_ObjectType == SPRITE_MYCOPTER_LTR)
        {
            int i_Frame = p_MyCopterSprite.i_Frame;
            activateSprite(p_MyCopterSprite, SPRITE_MYCOPTER_FALL_LTR);
            p_MyCopterSprite.i_Frame = i_Frame;
        }
        startup.processGameAction(GAMEACTION_PLAYER_FALL);
        iPlayerSpecMode = psmFALL;
    }

    /**
     * Обработка снарядов:
     * - движение и выход за экран
     * - попадание в игрока
     * - попадание во врага
     * - попадание в боеприпасы
     *
     * @return true если снаряды присутствуют
     */
    private static boolean processShellsAndFireballs()
    {
        boolean lg_exists = false;

        for (int li = 0; li < MAX_SHELLS_NUMBER; li++)
        {
            Sprite p_shellSprite = ap_ShellAndFireballSprites[li];
            if (!p_shellSprite.lg_SpriteActive) continue;
            if ((p_shellSprite.i_ObjectType >> 1) != (SPRITE_MY_SHELL_RIGTH >> 1)) lg_exists = true;

            p_shellSprite.processAnimation();

            // обработка движения выстрела
            int i8_x = p_shellSprite.i8_mainX;
            int i8_y = p_shellSprite.i8_mainY;
            if ((p_shellSprite.i_ObjectType == SPRITE_FIREBALL))
                i8_y += (I8_FIREBALL_SPEED);
            else if (p_shellSprite.i_ObjectType == SPRITE_ENEMY_SHELL_LEFT_DOWN)
            {
                i8_x -= (I8_SHELL_DIAGONAL_SPEED);
                i8_y += (I8_SHELL_DIAGONAL_SPEED);
            }
            else if (p_shellSprite.i_ObjectType == SPRITE_ENEMY_SHELL_LEFT_UP)
            {
                i8_x -= (I8_SHELL_DIAGONAL_SPEED);
                i8_y -= (I8_SHELL_DIAGONAL_SPEED);
            }
            else if (p_shellSprite.i_ObjectType == SPRITE_ENEMY_SHELL_RIGTH)
                i8_x += (I8_SHELL_SPEED);
            else if (p_shellSprite.i_ObjectType == SPRITE_MY_SHELL_RIGTH)
                i8_x += (I8_MY_SHELL_SPEED);
            else if (p_shellSprite.i_ObjectType == SPRITE_ENEMY_SHELL_LEFT)
                i8_x -= (I8_SHELL_SPEED);
            else if (p_shellSprite.i_ObjectType == SPRITE_MY_SHELL_LEFT)
                i8_x -= (I8_MY_SHELL_SPEED);

            p_shellSprite.setMainPointXY(i8_x, i8_y);

            // Выход выстрела за экран
            if (!p_shellSprite.isCollided(-I8_SHELL_BOUNDS_X, Sprite.COL_RIGHT))
            {
                p_shellSprite.lg_SpriteActive = false;
            }
            if (!p_shellSprite.isCollided((iScreenWidth << 8) + I8_SHELL_BOUNDS_X, Sprite.COL_LEFT))
            {
                p_shellSprite.lg_SpriteActive = false;
            }
            if (!p_shellSprite.isCollided((iScreenHeight << 8) + I8_FIREBALL_BOUNDS_Y, Sprite.COL_UP))
            {
                deactivateShell(p_shellSprite);
            }
            if (!p_shellSprite.isCollided(-I8_FIREBALL_BOUNDS_Y, Sprite.COL_DOWN))
            {
                p_shellSprite.lg_SpriteActive = false;
            }

            // Если снаряд игрока то
            if ((p_shellSprite.i_ObjectType >> 1) == (SPRITE_MY_SHELL_ >> 1))
            {
                // Проверка на попадание во вражескую вертушку
                for (int lj = 0; lj < MAX_SCREEN_COPTERS_NUMBER; lj++)
                {
                    Sprite pEnemy = ap_CopterSprites[lj];
                    if (!pEnemy.lg_SpriteActive) continue;

                    final int eX = pEnemy.i8_mainX;
                    final int eY = pEnemy.i8_mainY;
                    final boolean bInScreen = (eX > 0) && (eY > 0) && (eX < (iScreenWidth << 8)) && (eY < (iScreenHeight << 8));
                    if (!bInScreen) continue;
                    if (!p_shellSprite.isCollided(pEnemy)) continue;
                    p_shellSprite.lg_SpriteActive = false;
                    if (iBossOfs >= 0)
                    {  // стадия с боссом
                        if (pEnemy.i_ObjectState > 0)
                        {
                            pEnemy.i_ObjectState--;
                            iPlayerScore += BONUS_TOWER;
                        }
                        else
                        {
                            // взрываем башню босса
                            generateExplosion(pEnemy);
                            pEnemy.lg_SpriteActive = false;
                            aiActiveCoptersNumber[0]--;
                            pEnemy.i_ObjectState = -1;
                            iPlayerScore += BONUS_TOWER_DESTROY + BONUS_TOWER;
                            startup.processGameAction(GAMEACTION_EXPLOSION);
                        }
                    }
                    else
                    {// стадия без босса
                        int i_Sprite = getCopterSpriteOffset(pEnemy.i_ObjectType);
                        // переводим вертушку в режим падения
                        if (i_Sprite == SPRITE_COPTER_RIGHT)
                            activateSprite(pEnemy, pEnemy.i_ObjectType - SPRITE_COPTER_RIGHT + SPRITE_COPTER_FALL_RTL);
                        if (i_Sprite == SPRITE_COPTER_LEFT)
                            activateSprite(pEnemy, pEnemy.i_ObjectType - SPRITE_COPTER_LEFT + SPRITE_COPTER_FALL_LTR);
                        if (i_Sprite == SPRITE_COPTER_RTL)
                        {
                            int i_Frame = pEnemy.i_Frame;
                            activateSprite(pEnemy, pEnemy.i_ObjectType - SPRITE_COPTER_RTL + SPRITE_COPTER_FALL_RTL);
                            pEnemy.i_Frame = i_Frame;
                        }
                        if (i_Sprite == SPRITE_COPTER_LTR)
                        {
                            int i_Frame = pEnemy.i_Frame;
                            activateSprite(pEnemy, pEnemy.i_ObjectType - SPRITE_COPTER_LTR + SPRITE_COPTER_FALL_LTR);
                            pEnemy.i_Frame = i_Frame;
                        }
                        iPlayerScore += BONUS_COPTER[getCopterSpriteID(pEnemy.i_ObjectType)];
                        startup.processGameAction(GAMEACTION_ENEMY_FALL);
                    }
                }
            }
            else if (p_MyCopterSprite.lg_SpriteActive && (p_shellSprite.isCollided(p_MyCopterSprite)))
            {
                // Проверка на попадание в игрока
                deactivateShell(p_shellSprite);
                fallPlayer();
            }

            // Проверка на попадание в снаряжение
            if (p_AmmoSprite.lg_SpriteActive)
            {
                if (p_shellSprite.isCollided(p_AmmoSprite))
                {
                    deactivateShell(p_shellSprite);
                    generateExplosion(p_AmmoSprite);
                    startup.processGameAction(GAMEACTION_EXPLOSION);
                    p_AmmoSprite.lg_SpriteActive = false;
                }
            }
            // Проверка на попадание в жизнь
            if (p_LifeSprite.lg_SpriteActive)
            {
                if (p_shellSprite.isCollided(p_LifeSprite))
                {
                    deactivateShell(p_shellSprite);
                    generateExplosion(p_LifeSprite);
                    startup.processGameAction(GAMEACTION_EXPLOSION);
                    p_LifeSprite.lg_SpriteActive = false;
                }
            }

        }
        return lg_exists;
    }

    private static void deactivateShell(Sprite p_shellSprite)
    {
        p_shellSprite.lg_SpriteActive = false;
        if (p_shellSprite.i_ObjectType == SPRITE_FIREBALL)
            iActiveFireballsNumber--;
    }

    /**
     * Обработка босса:
     * - движение и отражение от стенок
     * - столкновение с игроком
     * - столкновение с боеприпасом
     *
     * @return true если вертушки присутствуют
     */
    private static boolean processBoss(final boolean _playerUp)
    {
        boolean lg_exists = false;

        Sprite pBoss = p_FighterSprite;

        if (!pBoss.lg_SpriteActive) return false;

        if (pBoss.processAnimation())
            if (pBoss.i_ObjectType == aiBossParams[iBossOfs + OFS_BOSS_EXPLOSION_SPRITE])
            {
                pBoss.lg_SpriteActive = false;
            }

        if (aiActiveCoptersNumber[0] == 0) return false;

        // обработка движения
        int i8_vx = ai8CopterVX[0];
        int i8_vy = ai8CopterVY[0];

        if (!p_MyCopterSprite.lg_SpriteActive)
        {
            if (i8_vx < 0)
                i8_vx *= -1;
            i8_vx = (i8_vx * I8_FIN_BOSS_AXCELERATION) >> 8;
            ai8CopterVX[0] = i8_vx;
            i8_vy = (i8_vy * I8_FIN_BOSS_AXCELERATION) >> 8;
            ai8CopterVY[0] = i8_vy;
        }

        int i8_x = pBoss.i8_mainX + i8_vx;
        int i8_y = pBoss.i8_mainY + i8_vy;

        // Вычисляем новое расположение
        if (p_MyCopterSprite.lg_SpriteActive)
        {
            if (i8_vy > 0)
            {
                if (i8_y > ((iScreenHeight << 8) - I8_BOSS_BOUNDS_Y))
                { //стук о нижнюю стенку
                    i8_vy = -i8_vy;
                }
            }
            else
            {
                if (i8_y < I8_BOSS_BOUNDS_Y)
                {// верхний стук
                    i8_vy = -i8_vy;
                }
            }
            if (i8_vx > 0)
            {
                if (i8_x > ((iScreenWidth << 8) + I8_BOSS_BOUNDS_RIGHT))
                { //стук о правую стенку
                    i8_vx = -i8_vx;
                }
            }
        }
        else
        {
            if ((i8_y > ((iScreenHeight << 8) + I8_BOSS_BOUNDS_Y)) ||
                        (i8_y < -I8_BOSS_BOUNDS_Y) ||
                        (i8_x > ((iScreenWidth << 8) + I8_BOSS_BOUNDS_X)))
            {
                p_FighterSprite.lg_SpriteActive = false;
                return false;
            }
        }

        if (i8_x < ((iScreenWidth << 8) + I8_BOSS_BOUNDS_LEFT))
        {
            i8_vx = -i8_vx;
        }

        pBoss.setMainPointXY(i8_x, i8_y);

        i8_x = (i8_x + 0x7F) & 0xFFFFFF00;
        i8_y = (i8_y + 0x7F) & 0xFFFFFF00;

        ai8CopterVX[0] = i8_vx;
        ai8CopterVY[0] = i8_vy;

        // Проверка на столкновение с игроком
        if (p_MyCopterSprite.lg_SpriteActive)
        {
            if (p_MyCopterSprite.isCollided(pBoss))
            {
                generateExplosion(p_MyCopterSprite);
                startup.processGameAction(GAMEACTION_EXPLOSION);
                p_MyCopterSprite.lg_SpriteActive = false;
            }
        }
        // Проверка на столкновение с боеприпасами
        if (p_AmmoSprite.lg_SpriteActive)
        {
            if (p_AmmoSprite.isCollided(pBoss))
            {
                startup.processGameAction(GAMEACTION_ENEMY_GET_AMMO);
                generateExplosion(p_AmmoSprite);
                p_AmmoSprite.lg_SpriteActive = false;
            }
        }
        // Проверка на столкновение с жизнью
        if (p_LifeSprite.lg_SpriteActive)
        {
            if (p_LifeSprite.isCollided(pBoss))
            {
                startup.processGameAction(GAMEACTION_ENEMY_GET_LIFE);
                generateExplosion(p_LifeSprite);
                p_LifeSprite.lg_SpriteActive = false;
            }
        }

        boolean Fire = false;
        final int playerX = p_MyCopterSprite.i8_mainX;
        final int playerY = p_MyCopterSprite.i8_mainY;

        //Обрабатываем башни босса
        for (int li = 0; li < aiBossParams[iBossOfs + OFS_BOSS_TOWERS_COUNT]; li++)
        {
            pBoss = ap_CopterSprites[li];
            if (!pBoss.lg_SpriteActive) continue;

            lg_exists = true;
            final int copterX = i8_x + aiBossParams[iBossOfs + OFS_BOSS_TOWERS_ + li * BOSS_TOWERS_DATA_LENGTH + OFS_TOWER_X];
            final int copterY = i8_y + aiBossParams[iBossOfs + OFS_BOSS_TOWERS_ + li * BOSS_TOWERS_DATA_LENGTH + OFS_TOWER_Y];
            pBoss.setMainPointXY(copterX, copterY);

            int dir = 1;
            final int dy = copterY - playerY;
            final int dx = copterX - playerX;

            //  Choice of fire direction and shooting
            if (p_MyCopterSprite.lg_SpriteActive)
            {
                if (dx <= 0)
                {
                    if (dy > 0)
                    {
                        dir = 0;
                    }
                    else
                    {
                        dir = 2;
                    }
                }
                else
                {
                    if (dy > 0)
                    {
                        if (dx < (dy << 1)) dir = 0;
                    }
                    else if (dx < (0 - dy) << 1) dir = 2;
                }

                /*
                               if (getRandomInt(1000)>950)
                               {
                                   switch(dir)
                                   {
                                       case 0 : dir = 1;break;
                                       case 1 : dir = (getRandomInt(100)>50)?0 : 2;break;
                                       case 2 : dir = 1;break;
                                   }
                               }
               */
                // Отрабатываем стреляние
                if (copterX <= (iScreenWidth << 8) + I8_TOWER_FIRE_LINE)
                {
                    int i_Param = aiBossParams[iBossOfs + OFS_I8_BOSS_FIRE_RATE];
                    if (getRandomInt(i_Param) <= ((i_Param >> 5)))
                        if (generateBossShell(pBoss, dir))
                            Fire = true;
                }
            }
            else
                dir = 1;

            pBoss.i_Frame = dir;
        }

        if (Fire) startup.processGameAction(GAMEACTION_ENEMY_FIRE);
        return lg_exists;
    }


    /**
     * Обработка вертушек противника:
     * - движение и отражение от стенок
     * - столкновение с игроком
     * - столкновение с боеприпасом
     *
     * @return true если вертушки присутствуют
     */
    private static boolean processCopters()
    {
        boolean coptersExists = false;
        final boolean playerExist = p_MyCopterSprite.lg_SpriteActive;
        boolean bInScreen = true;


        int i8MyX = p_MyCopterSprite.i8_mainX;
        int i8MyY = p_MyCopterSprite.i8_mainY;

        for (int li = 0; li < MAX_SCREEN_COPTERS_NUMBER; li++)
        {
            Sprite pSprite = ap_CopterSprites[li];

            if (!pSprite.lg_SpriteActive) continue;
            coptersExists = true;

            int iOfs = getCopterSpriteOffset(pSprite.i_ObjectType);

            if (pSprite.processAnimation())
            {//отрабатываем завершение поворота или предпадения
                if (iOfs == SPRITE_COPTER_LTR)
                    activateSprite(pSprite, pSprite.i_ObjectType - SPRITE_COPTER_LTR + SPRITE_COPTER_RIGHT);
                else if (iOfs == SPRITE_COPTER_RTL)
                    activateSprite(pSprite, pSprite.i_ObjectType - SPRITE_COPTER_RTL + SPRITE_COPTER_LEFT);
                else if ((iOfs >> 1) == (SPRITE_COPTER_FALL_ >> 1))
                {
                    final int copterID = getCopterSpriteID(pSprite.i_ObjectType);
                    if (copterID >= 0)
                    {
                        generateExplosion(pSprite);
                        //#if BONUS
                        generateBonus(pSprite, SPRITE_BONUS_ + copterID);
                        //#endif
                        startup.processGameAction(GAMEACTION_EXPLOSION);
                        pSprite.lg_SpriteActive = false;
                        iTotalActiveCoptersNumber--;
                        aiActiveCoptersNumber[copterID]--;
                    }
                    continue;
                }
                iOfs = getCopterSpriteOffset(pSprite.i_ObjectType);
            }

            if ((iOfs >> 1) == (SPRITE_COPTER_FALL_ >> 1))
            {
                if (iSmokeCounter == 0)
                    generateSmoke(pSprite);
            }

            // Если в фазе полета
            if (((iOfs >> 1) == (SPRITE_COPTER_ >> 1)))
            {
                // обработка движения
                int i8_vx = ai8CopterVX[li];
                int i8_vy = ai8CopterVY[li];

                int i8_x, i8_y;

                if (!playerExist)
                {
                    i8_vx = (i8_vx * I8_FIN_AXCELERATION) >> 8;
                    i8_vy = (i8_vy * I8_FIN_AXCELERATION) >> 8;

                    if (i8_vy > 0) i8_vy *= -1;

                    i8_x = pSprite.i8_mainX + i8_vx;
                    i8_y = pSprite.i8_mainY + i8_vy;
                    bInScreen = (i8_x > 0) && (i8_y > 0) && (i8_x < (iScreenWidth << 8)) && (i8_y < (iScreenHeight << 8));

                    // Вычисляем новое расположение
                    if ((i8_x > ((iScreenWidth << 8) + I8_COPTER_BOUNDS_X)) ||
                                (i8_x < -I8_COPTER_BOUNDS_X) ||
                                (i8_y < -I8_COPTER_BOUNDS_Y))
                    {
                        pSprite.lg_SpriteActive = false;
                        iTotalActiveCoptersNumber--;
                        iTotalPassiveCoptersNumber++;
                        final int i = getCopterSpriteID(pSprite.i_ObjectType);
                        aiActiveCoptersNumber[i]--;
                        aiPassiveCoptersNumber[i]++;
                    }
                    else if (i8_y > ((iScreenHeight << 8) - I8_COPTER_BOUNDS_Y))
                    {
                        if (i8_vy > 0)
                        {//стук о нижнюю стенку
                            i8_y = (iScreenHeight << 8) - I8_COPTER_BOUNDS_Y;
                            i8_vy = -i8_vy;
                        }
                    }
                }
                else
                {
                    i8_x = pSprite.i8_mainX + i8_vx;
                    i8_y = pSprite.i8_mainY + i8_vy;
                    bInScreen = (i8_x > 0) && (i8_y > 0) && (i8_x < (iScreenWidth << 8)) && (i8_y < (iScreenHeight << 8));

                    // Отрабатываем стреляние
                    if (bInScreen)
                    {
                        final boolean b_FireRight = (iOfs == SPRITE_COPTER_RIGHT);
                        final boolean b_FireLeft = (iOfs == SPRITE_COPTER_LEFT);

                        if (aiEnemyBurstCounter[li] == 0)
                        {
                            aiEnemyBurstCounter[li] = iEnemyBurstDelay;
                            if ((getRandomInt(I8_PROBABILITY) >> 8) == 0)
                            {
                                aiEnemyFireNumberCounter[li] = iEnemyBurstLength;
                                aiEnemyFireCounter[li] = iEnemyFireDelay;
                            }
                        }
                        else aiEnemyBurstCounter[li]--;

                        if (aiEnemyFireCounter[li] == 0)
                        {
                            if (aiEnemyFireNumberCounter[li] > 0)
                            {
                                aiEnemyFireNumberCounter[li]--;
                                aiEnemyFireCounter[li] = iEnemyFireDelay;
                                // Если игрок с нужной стороны от вертушки */
                                if ((b_FireRight && i8MyX - i8_x > 0 || b_FireLeft && i8MyX - i8_x < 0) &&
                                            // Если игрок в секторе видимости */
                                            (Math.abs(i8MyY - i8_y) < Math.abs(i8MyX - i8_x) * I8_ENEMY_FIRE_ANGLE >> 8))
                                {
                                    if ((getRandomInt(I8_PROBABILITY) >> 8) == 0)
                                        if (generateShell(pSprite))
                                            startup.processGameAction(GAMEACTION_ENEMY_FIRE);
                                }
                                else
                                {
                                    aiEnemyFireNumberCounter[li] = 0;
                                    aiEnemyFireCounter[li] = 0;
                                }
                            }
                        }
                        else aiEnemyFireCounter[li]--;
                    }


                    // Вычисляем новое расположение
                    if (i8_x > ((iScreenWidth << 8) - I8_COPTER_BOUNDS_X))
                    {
                        if (i8_vx > 0)
                        {//стук о правую стенку
                            i8_x = (iScreenWidth << 8) - I8_COPTER_BOUNDS_X;
                            iOfs = SPRITE_COPTER_RTL;
                            activateSprite(pSprite, getCopterFirstSprite(pSprite.i_ObjectType) + iOfs);
                            i8_vx = -i8_vx;
                            switch (getCopterSpriteID(pSprite.i_ObjectType))
                            {
                                case 0:
                                    break;
                                case 1:
                                case 2:
                                    pSprite.i_spriteID = I_HORIZ_ROTATE_DELAY;
                                    break;
                                case 3:
                                    pSprite.i_spriteID = I_COPTER3_FLIP_DELAY;
                            }
                        }
                    }
                    else if (i8_x < I8_COPTER_BOUNDS_X)
                    {
                        if (i8_vx < 0)
                        {// левый стук
                            i8_x = I8_COPTER_BOUNDS_X;
                            iOfs = SPRITE_COPTER_LTR;
                            activateSprite(pSprite, getCopterFirstSprite(pSprite.i_ObjectType) + iOfs);
                            i8_vx = -i8_vx;
                            switch (getCopterSpriteID(pSprite.i_ObjectType))
                            {
                                case 0:
                                    break;
                                case 1:
                                case 2:
                                    pSprite.i_spriteID = I_HORIZ_ROTATE_DELAY;
                                    break;
                                case 3:
                                    pSprite.i_spriteID = I_COPTER3_FLIP_DELAY;
                            }
                        }
                    }
                    else if (pSprite.i_spriteID == 0)
                    {
                        if (i8_x < ((iScreenWidth << 8) - I8_COPTER_BOUNDS_X))
                            if (i8_x > I8_COPTER_BOUNDS_X)
                                switch (getCopterSpriteID(pSprite.i_ObjectType))
                                {
                                    case 0:
                                        break;
                                    case 1:
                                    case 2:
                                        pSprite.i_spriteID = I_HORIZ_ROTATE_DELAY;
                                        if (getRandomInt(I_COPTER_X_FLIP_FREQ) == 0) //сработала вероятность
                                        {
                                            i8_vx = -i8_vx;
                                            if (i8_vx > 0)
                                                activateSprite(pSprite, getCopterFirstSprite(pSprite.i_ObjectType) + SPRITE_COPTER_LTR);
                                            else
                                                activateSprite(pSprite, getCopterFirstSprite(pSprite.i_ObjectType) + SPRITE_COPTER_RTL);
                                            aiEnemyFireNumberCounter[li] = 0;
                                            aiEnemyFireCounter[li] = 0;
                                        }
                                        break;
                                    case 3:
                                        pSprite.i_spriteID = I_COPTER3_FLIP_DELAY;
                                        //                            if (iCopter3Counter == 0)
                                        {
                                            if (((i8_vx < 0) && (i8MyX - i8_x > 0)))
                                            {
                                                activateSprite(pSprite, getCopterFirstSprite(pSprite.i_ObjectType) + SPRITE_COPTER_LTR);
                                                i8_vx = -i8_vx;
                                                aiEnemyFireNumberCounter[li] = 0;
                                                aiEnemyFireCounter[li] = 0;
                                            }
                                            else if (((i8_vx > 0) && (i8MyX - i8_x < 0)))
                                            {
                                                activateSprite(pSprite, getCopterFirstSprite(pSprite.i_ObjectType) + SPRITE_COPTER_RTL);
                                                i8_vx = -i8_vx;
                                                aiEnemyFireNumberCounter[li] = 0;
                                                aiEnemyFireCounter[li] = 0;
                                            }
                                        }
                                }
                    }
                    else pSprite.i_spriteID--;

                    if (i8_y > ((iScreenHeight << 8) - I8_COPTER_BOUNDS_Y))
                    {
                        if (i8_vy > 0)
                        {//стук о нижнюю стенку
                            i8_y = (iScreenHeight << 8) - I8_COPTER_BOUNDS_Y;
                            i8_vy = -i8_vy;
                        }
                    }
                    else if (i8_y < I8_COPTER_BOUNDS_Y)
                    {
                        if (i8_vy < 0)
                        {// верхний стук
                            i8_y = I8_COPTER_BOUNDS_Y;
                            i8_vy = -i8_vy;
                        }
                    }
                    else if (i8_y < ((iScreenHeight << 8) - I8_COPTER_BOUNDS_Y))
                        if (i8_y > I8_COPTER_BOUNDS_Y)
                            switch (getCopterSpriteID(pSprite.i_ObjectType))
                            {
                                case 0:
                                case 1:
                                    break;
                                case 2:
                                    if (getRandomInt(I_COPTER_Y_FLIP_FREQ) == 0) //сработала вероятность
                                        i8_vy = -i8_vy;
                                    break;
                                case 3:
                                    if (pSprite.i_spriteID == 0)
                                        if (((i8_vy < 0) && (i8MyY - i8_y > 0)) || ((i8_vy > 0) && (i8MyY - i8_y < 0)))
                                            i8_vy = -i8_vy;
                            }
                }
                pSprite.setMainPointXY(i8_x, i8_y);

                ai8CopterVX[li] = i8_vx;
                ai8CopterVY[li] = i8_vy;
            }
            // Если в фазе падения
            else if ((iOfs >> 1) == (SPRITE_COPTER_FALL_ >> 1))
            {
                pSprite.moveMainPointXY(0, I8_COPTER_FALL_SPEED);
                continue;
            }

            if (bInScreen)
            {
                // Проверка на столкновение с игроком
                if (p_MyCopterSprite.lg_SpriteActive)
                {
                    if (p_MyCopterSprite.isCollided(pSprite))
                    {
                        pSprite.lg_SpriteActive = false;
                        generateExplosion(pSprite);
                        generateExplosion(p_MyCopterSprite);
                        startup.processGameAction(GAMEACTION_EXPLOSION);
                        p_MyCopterSprite.lg_SpriteActive = false;
                        iTotalActiveCoptersNumber--;
                        aiActiveCoptersNumber[getCopterSpriteID(pSprite.i_ObjectType)]--;
                    }
                }
                // Проверка на столкновение с боеприпасами
                if (p_AmmoSprite.lg_SpriteActive)
                {
                    if (p_AmmoSprite.isCollided(pSprite))
                    {
                        startup.processGameAction(GAMEACTION_ENEMY_GET_AMMO);
                        generateExplosion(p_AmmoSprite);
                        p_AmmoSprite.lg_SpriteActive = false;
                    }
                }
                // Проверка на столкновение с жизнью
                if (p_LifeSprite.lg_SpriteActive)
                {
                    if (p_LifeSprite.isCollided(pSprite))
                    {
                        startup.processGameAction(GAMEACTION_ENEMY_GET_LIFE);
                        generateExplosion(p_LifeSprite);
                        p_LifeSprite.lg_SpriteActive = false;
                    }
                }
            }
        }

        return coptersExists;
    }

    /**
     * Обработка боеприпасов и жизни:
     * - движение и выход за экран
     * - попадание в игрока
     *
     * @return true если боеприпас присутствует
     */
    private static boolean processSprite(Sprite pSprite)
    {
        if (!pSprite.lg_SpriteActive) return false;

        pSprite.processAnimation();

        int vy = pSprite.i_ObjectState;
        if (!p_MyCopterSprite.lg_SpriteActive)
        {
            vy = (vy * I8_FIN_AXCELERATION) >> 8;
            pSprite.i_ObjectState = vy;
        }

        // обработка движения
        pSprite.moveMainPointXY(0, vy);
        // Выход выстрела за экран
        if (pSprite.i8_ScreenY > (iScreenHeight << 8))
        {
            pSprite.lg_SpriteActive = false;
        }

        // Проверка на попадание в игрока
        if (p_MyCopterSprite.lg_SpriteActive)
        {
            if (p_MyCopterSprite.isCollided(pSprite))
            {
                pSprite.lg_SpriteActive = false;
                return true;
            }
        }
        return false;
    }

    /**
     * Обработка боеприпасов:
     * - движение и выход за экран
     * - попадание в игрока
     *
     * @return true если боеприпас присутствует
     */
    private static boolean processFighter()
    {
        if (!p_FighterSprite.lg_SpriteActive) return false;

        if (iFighterFlightCounter > 1)
        {
            iFighterFlightCounter--;
            return true;
        }
        else if (iFighterFlightCounter == 1)
        {
            startup.processGameAction(GAMEACTION_FIGHTER_FLY);
            iFighterFlightCounter--;
        }

        p_FighterSprite.processAnimation();

        // обработка движения
        int i8_x;
        if (p_FighterSprite.i_ObjectType == SPRITE_FIGHTER_RIGHT)
            i8_x = p_FighterSprite.i8_mainX + I8_FIGHTER_SPEED;
        else
            i8_x = p_FighterSprite.i8_mainX - I8_FIGHTER_SPEED;
        int i8_y = p_FighterSprite.i8_mainY;
        p_FighterSprite.setMainPointXY(i8_x, i8_y);

        // Выход за экран
        if (((p_FighterSprite.i_ObjectType == SPRITE_FIGHTER_RIGHT) && (!p_FighterSprite.isCollided(iScreenWidth << 8, Sprite.COL_LEFT))) ||
                    ((p_FighterSprite.i_ObjectType == SPRITE_FIGHTER_LEFT) && (!p_FighterSprite.isCollided(0, Sprite.COL_RIGHT))))
        {
            p_FighterSprite.lg_SpriteActive = false;
        }

        // Проверка на попадание в игрока
        if (p_MyCopterSprite.lg_SpriteActive)
        {
            if (p_MyCopterSprite.isCollided(p_FighterSprite))
            {
                generateExplosion(p_MyCopterSprite);
                startup.processGameAction(GAMEACTION_EXPLOSION);
                p_MyCopterSprite.lg_SpriteActive = false;
            }
        }
        return true;
    }

    /**
     * Обработка вертушки игрока:
     * - перемешение и удар об землю
     * - стрелляние
     *
     * @return true если игрок жив
     */
    private static boolean processMyCopter(int _buttons)
    {
        if (iSmokeCounter == 0)
            iSmokeCounter = I_SMOKE_DELAY;
        else
            iSmokeCounter--;

        if (!p_MyCopterSprite.lg_SpriteActive) return false;

        if (p_MyCopterSprite.processAnimation())
        {
            if (p_MyCopterSprite.i_ObjectType == SPRITE_MYCOPTER_LTR)
                activateSprite(p_MyCopterSprite, SPRITE_MYCOPTER_RIGHT);
            if (p_MyCopterSprite.i_ObjectType == SPRITE_MYCOPTER_RTL)
                activateSprite(p_MyCopterSprite, SPRITE_MYCOPTER_LEFT);
            if (p_MyCopterSprite.i_ObjectType == SPRITE_MYCOPTER_RTL)
                activateSprite(p_MyCopterSprite, SPRITE_MYCOPTER_LEFT);
            if ((p_MyCopterSprite.i_ObjectType >> 1) == (SPRITE_MYCOPTER_FALL_RTL >> 1))
            {
                generateExplosion(p_MyCopterSprite);
                startup.processGameAction(GAMEACTION_EXPLOSION);
                p_MyCopterSprite.lg_SpriteActive = false;
            }
        }

        if ((p_MyCopterSprite.i_ObjectType >> 1) == (SPRITE_COPTER_FALL_ >> 1))
        {
            if (iSmokeCounter == 0)
                generateSmoke(p_MyCopterSprite);
        }

        int i8_CurX = p_MyCopterSprite.i8_mainX;
        int i8_CurY = p_MyCopterSprite.i8_mainY;

        int i8_deltaX = I8_MY_PASSIVE_X_SPEED;
        int i8_deltaY = I8_MY_PASIVE_Y_SPEED;

        //отрабатываем спец режимы (влет, вылет, предпадение)
        if (iPlayerSpecMode != 0)
        {
            switch (iPlayerSpecMode)
            {
                case psmFLIGHTUP:
                    if (p_MyCopterSprite.i_ObjectType == SPRITE_MYCOPTER_LEFT)
                        activateSprite(p_MyCopterSprite, SPRITE_MYCOPTER_LTR);
                    /*    это для постоянного вращения при подьеме после победы
               else if (p_MyCopterSprite.i_ObjectType == SPRITE_MYCOPTER_RIGHT)
                   activateSprite(p_MyCopterSprite, SPRITE_MYCOPTER_RTL);
                */
                    setMyPosition(i8_CurX, i8_CurY - I8_MY_FLIGHTUP_SPEED);
                    break;
                case psmFLIGHTIN:
                    if (i8_CurX >= (iScreenWidth << 7))
                    {
                        iPlayerSpecMode = psmNONE;
                        activateSprite(p_MyCopterSprite, SPRITE_MYCOPTER_RIGHT);
                    }
                    /*
                                        else
                                        {
                                            if (p_MyCopterSprite.i_ObjectType == SPRITE_MYCOPTER_LEFT)
                                                activateSprite(p_MyCopterSprite, SPRITE_MYCOPTER_LTR);
                                        }
                    */
                    setMyPosition(i8_CurX + I8_MY_FLIGHT_INOUT_SPEED, i8_CurY);
                    break;
                case psmFLIGHTOUT:
                    if (p_MyCopterSprite.i_ObjectType == SPRITE_MYCOPTER_LEFT)
                        activateSprite(p_MyCopterSprite, SPRITE_MYCOPTER_LTR);
                    else if (p_MyCopterSprite.i_ObjectType == SPRITE_MYCOPTER_RIGHT)
                        activateSprite(p_MyCopterSprite, SPRITE_MYCOPTER_FLIGHT_INOUT);
                    setMyPosition(i8_CurX + I8_MY_FLIGHT_INOUT_SPEED, i8_CurY);
                    break;
                case psmFALL:
                    setMyPosition(i8_CurX, i8_CurY + I8_MY_FALL_SPEED);
                    break;
            }
            return true;
        }

        if ((_buttons & BUTTON_DOWN) != 0)
        {
            i8_deltaY += I8_MYCOPTER_Y_SPEED;
        }
        else if ((_buttons & BUTTON_UP) != 0)
        {
            i8_deltaY -= I8_MYCOPTER_Y_SPEED;
        }

        //* Если не в фазе поворота */
        if ((p_MyCopterSprite.i_ObjectType >> 1) != (SPRITE_MYCOPTER_RTL >> 1))
        {
            if ((_buttons & BUTTON_LEFT) != 0)
            {
                i8_deltaX -= I8_MYCOPTER_X_SPEED;
                if (p_MyCopterSprite.i_ObjectType == SPRITE_MYCOPTER_RIGHT)
                    activateSprite(p_MyCopterSprite, SPRITE_MYCOPTER_RTL);
            }
            else if (((_buttons & BUTTON_RIGHT) != 0) || (p_MyCopterSprite.i_ObjectType == SPRITE_MYCOPTER_RIGHT))
            {
                i8_deltaX += I8_MYCOPTER_X_SPEED;
                if (p_MyCopterSprite.i_ObjectType == SPRITE_MYCOPTER_LEFT)
                    activateSprite(p_MyCopterSprite, SPRITE_MYCOPTER_LTR);
            }
            else
            {
                i8_deltaX -= I8_MYCOPTER_X_SPEED;
            }
        }

        // Вычисляем новое расположение
        if (i8_deltaX != 0)
        {
            i8_CurX += i8_deltaX;
            if (i8_deltaX > 0)
            {
                if (i8_CurX > ((iScreenWidth << 8) - I8_MY_BOUNDS_X))
                {
                    i8_CurX = (iScreenWidth << 8) - I8_MY_BOUNDS_X;
                }
            }
            else
            {
                if (i8_CurX < I8_MY_BOUNDS_X)
                {
                    i8_CurX = I8_MY_BOUNDS_X;
                }
            }
        }

        if (i8_deltaY != 0)
        {
            i8_CurY += i8_deltaY;
            if (i8_deltaY > 0)
            {
                if (i8_CurY > ((iScreenHeight << 8) - I8_MY_BOUNDS_BOTTOM))
                {
                    generateExplosion(p_MyCopterSprite);
                    p_MyCopterSprite.lg_SpriteActive = false;
                    startup.processGameAction(GAMEACTION_EXPLOSION);
                }
            }
            else
            {
                if (i8_CurY < I8_MY_BOUNDS_Y)
                {
                    i8_CurY = I8_MY_BOUNDS_Y;
                }
            }
        }
        setMyPosition(i8_CurX, i8_CurY);

        //* Если не в фазе поворота */
        if (iPlayerFireCounter == 0)
        {
            if ((p_MyCopterSprite.i_ObjectType >> 1) == (SPRITE_MYCOPTER_ >> 1))
            {
                if ((_buttons & BUTTON_FIRE) != 0)
                {
                    _buttons &= ~BUTTON_FIRE;
                    if (iPlayerAmmo > 0)
                    {
                        if (generateShell(p_MyCopterSprite))
                        {
                            startup.processGameAction(GAMEACTION_PLAYER_FIRE);
                            iPlayerAmmo--;
                            iPlayerFireCounter = iPlayerFireDelay;
                        }
                    }
                }
            }
        }
        else iPlayerFireCounter--;
        return true;
    }

    private static void setMyPosition(int i8_CurX, int i8_CurY)
    {
        p_MyCopterSprite.setMainPointXY(i8_CurX, i8_CurY);
    }

    /**
     * Функция возвращает относительный номер спрайта в блоке спрайтов одного типа вертолетов
     */
    public static int getCopterSpriteOffset(int i_Sprite)
    {
        if (i_Sprite < SPRITE_COPTER1_)
            return -1;
        else if (i_Sprite < SPRITE_COPTER2_)
        {// Вертушка 1
            return i_Sprite - SPRITE_COPTER1_;
        }
        else if (i_Sprite < SPRITE_COPTER3_)
        {// Вертушка 2
            return i_Sprite - SPRITE_COPTER2_;
        }
        else if (i_Sprite < SPRITE_COPTER4_)
        {// Вертушка 3
            return i_Sprite - SPRITE_COPTER3_;
        }
        else
        {// Вертушка 4
            return i_Sprite - SPRITE_COPTER4_;
        }
    }

    /**
     * Функция возвращает начало блока спрайтов данного типа вертолетов
     */
    private static int getCopterFirstSprite(int i_Sprite)
    {
        if (i_Sprite < SPRITE_COPTER1_)
            return -1;
        else if (i_Sprite < SPRITE_COPTER2_)
        {// Вертушка 1
            return SPRITE_COPTER1_;
        }
        else if (i_Sprite < SPRITE_COPTER3_)
        {// Вертушка 2
            return SPRITE_COPTER2_;
        }
        else if (i_Sprite < SPRITE_COPTER4_)
        {// Вертушка 3
            return SPRITE_COPTER3_;
        }
        else
        {// Вертушка 4
            return SPRITE_COPTER4_;
        }
    }

    /**
     * Функция номер типа вертолетов противника по номеру спрайта
     */
    public static int getCopterSpriteID(int i_Sprite)
    {
        if (i_Sprite < SPRITE_COPTER1_)
            return -1;
        else if (i_Sprite < SPRITE_COPTER2_)
        {// Вертушка 1
            return 0;
        }
        else if (i_Sprite < SPRITE_COPTER3_)
        {// Вертушка 2
            return 1;
        }
        else if (i_Sprite < SPRITE_COPTER4_)
        {// Вертушка 3
            return 2;
        }
        else
        {// Вертушка 4
            return 3;
        }
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
        _actorIndex *= SPRITE_DATA_LENGTH;

        int[] ai_sprParameters = aiSpriteParameters;

        int i8_w = (int) (((long) ai_sprParameters[_actorIndex++] * SCALE_WIDTH) >> 8) + 0x7F & 0xFFFFFF00;
        int i8_h = (int) (((long) ai_sprParameters[_actorIndex++] * SCALE_HEIGHT) >> 8) + 0x7F & 0xFFFFFF00;

        int i8_cx = (int) (((long) ai_sprParameters[_actorIndex++] * SCALE_WIDTH) >> 8);
        int i8_cy = (int) (((long) ai_sprParameters[_actorIndex++] * SCALE_HEIGHT) >> 8);
        int i8_aw = (int) (((long) ai_sprParameters[_actorIndex++] * SCALE_WIDTH) >> 8);
        int i8_ah = (int) (((long) ai_sprParameters[_actorIndex++] * SCALE_HEIGHT) >> 8);

        int i_f = ai_sprParameters[_actorIndex++];
        int i_fd = ai_sprParameters[_actorIndex++];
        int i_mp = ai_sprParameters[_actorIndex++];
        int i_an = ai_sprParameters[_actorIndex];

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
     * Поиск номера первого неактивного спрайта
     *
     * @return null если нет или объект если найден
     */
    private static int getFirstInactiveSpriteNum(Sprite[] _array)
    {
        int i_array_len = _array.length;
        for (int li = 0; li < i_array_len; li++)
        {
            if (!_array[li].lg_SpriteActive) return li;
        }
        return -1;
    }

    /**
     * Генерация выстрела боссом из заданной точки стрельбы
     */
    private static boolean generateBossShell(final Sprite src, final int dir)
    {
        // Ищем свободный спрайт выстрела
        Sprite p_Shell = getFirstInactiveSprite(ap_ShellAndFireballSprites);
        if (p_Shell == null) return false;

        final int i8_shellX = src.i8_mainX - I8_TOWER_BOUNDS_X;
        int i8_shellY = src.i8_mainY;
        int iShellSpriteID;

        switch (dir)
        {
                // up
                case 0:
                iShellSpriteID = SPRITE_ENEMY_SHELL_LEFT_UP;
                i8_shellY -= I8_TOWER_BOUNDS_Y;
                break;
                // forward
                case 1:
                iShellSpriteID = SPRITE_ENEMY_SHELL_LEFT;
                break;
                // down
                case 2:
                iShellSpriteID = SPRITE_ENEMY_SHELL_LEFT_DOWN;
                i8_shellY += I8_TOWER_BOUNDS_Y;
                break;
            default:
                return false;
        }

        activateSprite(p_Shell, iShellSpriteID);
        p_Shell.setMainPointXY(i8_shellX, i8_shellY);
        return true;
    }

    /**
     * Генерация выстрела заданным объектом
     */
    private static boolean generateShell(Sprite p_Sprite)
    {
        // Ищем свободный спрайт выстрела
        Sprite p_Shell = getFirstInactiveSprite(ap_ShellAndFireballSprites);
        if (p_Shell == null) return false;

        int i8_shellX = p_Sprite.i8_mainX;
        int i8_shellY = p_Sprite.i8_mainY;

        int iShellSpriteID;

        if (p_Sprite.i_ObjectType == SPRITE_MYCOPTER_RIGHT)
        {
            iShellSpriteID = SPRITE_MY_SHELL_RIGTH;
            i8_shellX += I8_SHOT_OFFSET_X;
            i8_shellY += I8_SHOT_OFFSET_Y;
        }
        else if (p_Sprite.i_ObjectType == SPRITE_MYCOPTER_LEFT)
        {
            iShellSpriteID = SPRITE_MY_SHELL_LEFT;
            i8_shellX -= I8_SHOT_OFFSET_X;
            i8_shellY += I8_SHOT_OFFSET_Y;
        }
        else switch (getCopterSpriteOffset(p_Sprite.i_ObjectType))
             {
                 case SPRITE_COPTER_LEFT:
                     iShellSpriteID = SPRITE_ENEMY_SHELL_LEFT;
                     i8_shellX -= I8_SHOT_OFFSET_X;
                     i8_shellY += I8_SHOT_OFFSET_Y;
                     break;
                 case SPRITE_COPTER_RIGHT:
                     iShellSpriteID = SPRITE_ENEMY_SHELL_RIGTH;
                     i8_shellX += I8_SHOT_OFFSET_X;
                     i8_shellY += I8_SHOT_OFFSET_Y;
                     break;
                 default:
                     return false;
             }
        activateSprite(p_Shell, iShellSpriteID);
        p_Shell.setMainPointXY(i8_shellX, i8_shellY);
        return true;
    }

    /**
     * Генерация взрыва на месте заданного объекта
     *
     * @param _object целевой объект
     */
    private static void generateExplosion(Sprite _object)
    {
        Sprite p_Sprite = getFirstInactiveSprite(ap_ExplosionSprites);
        if (p_Sprite == null) return;
        activateSprite(p_Sprite, SPRITE_EXPLOSION);
        p_Sprite.setMainPointXY(_object.i8_mainX, _object.i8_mainY);
    }


    /**
     * Генерация очков на месте заданного объекта
     *
     * @param _object целевой объект
     */
    //#if BONUS
    private static void generateBonus(final Sprite _object, final int bonusID)
    {
        Sprite p_Sprite = getFirstInactiveSprite(ap_BonusSprites);
        if (p_Sprite == null) return;
        activateSprite(p_Sprite, bonusID);
        p_Sprite.setMainPointXY(_object.i8_mainX + I8_BONUS_OFFSET_X, _object.i8_mainY + I8_BONUS_OFFSET_Y);
    }
    //#endif

    /**
     * Генерация дыма на месте заданного объекта
     *
     * @param _object целевой объект
     */
    private static void generateSmoke(Sprite _object)
    {
        Sprite p_Sprite = getFirstInactiveSprite(ap_ExplosionSprites);
        if (p_Sprite == null) return;
        activateSprite(p_Sprite, SPRITE_SMOKE);
        p_Sprite.setMainPointXY(_object.i8_mainX, _object.i8_mainY - I8_SMOKE_Y_OFS);
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
        return FLAG_SUPPORTRESTART | FLAG_STAGESUPPORT;
    }

    public static final Object loadArray(Class _class, String _resource) throws Exception
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

                while (li <= i_len)
                {
                    switch (i_type)
                    {
                        case ARRAY_BYTE:
                            {
                                ab_byteArr[li] = (byte) l_val;
                            }
                    ;
                            break;
                        case ARRAY_CHAR:
                            {
                                ach_charArr[li] = (char) l_val;
                            }
                    ;
                            break;
                        case ARRAY_SHORT:
                            {
                                ash_shortArr[li] = (short) l_val;
                            }
                    ;
                            break;
                        case ARRAY_INT:
                            {
                                ai_intArr[li] = (int) l_val;
                            }
                    ;
                            break;
                        case ARRAY_LONG:
                            {
                                al_longArr[li] = l_val;
                            }
                    ;
                            break;
                    }

                    i_index++;
                    li++;
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
                            ab_byteArr[i_index] = (byte) l_val;
                        }
                ;
                        break;
                    case ARRAY_CHAR:
                        {
                            ach_charArr[i_index] = (char) l_val;
                        }
                ;
                        break;
                    case ARRAY_SHORT:
                        {
                            ash_shortArr[i_index] = (short) l_val;
                        }
                ;
                        break;
                    case ARRAY_INT:
                        {
                            ai_intArr[i_index] = (int) l_val;
                        }
                ;
                        break;
                    case ARRAY_LONG:
                        {
                            al_longArr[i_index] = l_val;
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
