package mtv.paparazzo;

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
    //============================================================================
    public static final int GAMEACTION_FLASH = 0;
    public static final int GAMEACTION_STAR0 = 1;
    public static final int GAMEACTION_STAR1 = 2;
    public static final int GAMEACTION_STAR2 = 3;
    public static final int GAMEACTION_STAR3 = 4;
    public static final int GAMEACTION_STAR4 = 5;
    public static final int GAMEACTION_STAR5 = 6;
    public static final int GAMEACTION_GUARDIAN = 7;

    public static final int KEY_NONE = 0;
    public static final int KEY_LEFT = 1;
    public static final int KEY_RIGHT = 2;
    public static final int KEY_UP = 4;
    public static final int KEY_DOWN = 8;
    public static final int KEY_FIRE = 16;

    public static final int INT_MAX_CADR_NUMBER = 36;

    private static int i_DirectionKey;

    private static final int TIME_EASY = 210000;
    private static final int TIME_NORMAL = 145000;
    private static final int TIME_HARD = 90000;

    public static int i_TimeForLevel;
    public static long l_FinalTime;

    private static long l_pausedDelayTillFinal;
    protected static int i_currentCadr;
    protected static int i_coolCadres;
    protected static boolean lg_Flash;
    private static int i_PlayerScore;

    private static final int HORZ_SPEED = 0x700;
    private static final int VERT_SPEED = 0x700;

    protected static final int WINDOW_NUMBER = 20;
    private static final int TIME_DELAY = 80;

    protected static final Sprite[] WINDOW_SPRITES = new Sprite[WINDOW_NUMBER];
    private static final int TILE_WINDOW_INDEX = 1;

    protected static int i8_screenPosX;
    protected static int i8_screenPosY;

    protected static final int CELL_WIDTH = 12;
    protected static final int CELL_HEIGHT = 12;

    protected static final int CELLNUMBER_WIDTH = 73;
    protected static final int CELLNUMBER_HEIGHT = 60;

    protected static Sprite p_FocusSprite;
    protected static Sprite p_IconSprite;
    protected static PathController p_IconController;

    public static final int SPRITE_EMPTYWINDOW = 0;
    public static final int SPRITE_CLOSEDWINDOW = 1;
    public static final int SPRITE_STAR0 = 2;
    public static final int SPRITE_STAR1 = 3;
    public static final int SPRITE_STAR2 = 4;
    public static final int SPRITE_STAR3 = 5;
    public static final int SPRITE_STAR4 = 6;
    public static final int SPRITE_GUARDIAN = 7;
    public static final int SPRITE_STARICO = 8;
    public static final int SPRITE_GUARDICO = 9;
    public static final int SPRITE_FOCUS = 10;

    public static final short [] ash_Paths= new short[] {
         // PATH_ICON
         (short)5,(short)0,(short)0,(short)0,(short)4,(short)7,(short)-11,(short)4,(short)-8,(short)-23,(short)4,(short)11,(short)-39,(short)4,(short)-12,(short)-57,(short)4,(short)0,(short)-74,(short)4,
    };

    // PATH offsets
    private static final int PATH_ICON = 0;


    private static final byte[] ab_tileArray = new byte[]
            {
0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
0, 0, 0, 0, 0, 0, 0, 0, 0,
0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
0, 0, 0, 0, 0, 0, 0, 0, 0,
0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
0, 0, 0, 0, 0, 0, 0, 0, 0,
0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
0, 0, 0, 0, 0, 0, 0, 0, 0,
0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
0, 0, 0, 0, 0, 0, 0, 0, 0,
0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
0, 0, 0, 0, 0, 0, 0, 0, 0,
0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0,
0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0,
0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0,
0, 0, 0, 0, 0, 0, 0, 0, 0,
0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
0, 0, 0, 0, 0, 0, 0, 0, 0,
0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
0, 0, 0, 0, 0, 0, 0, 0, 0,
0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
0, 0, 0, 0, 0, 0, 0, 0, 0,
0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
0, 0, 0, 0, 0, 0, 0, 0, 0,
0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
0, 0, 0, 0, 0, 0, 0, 0, 0,
0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
0, 0, 0, 0, 0, 0, 0, 0, 0,
0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
0, 0, 0, 0, 0, 0, 0, 0, 0,
0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
0, 0, 0, 0, 0, 0, 0, 0, 0,
0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
0, 0, 0, 0, 0, 0, 0, 0, 0,
0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
0, 0, 0, 0, 0, 0, 0, 0, 0,
0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
0, 0, 0, 0, 0, 0, 0, 0, 0,
0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
0, 0, 0, 0, 0, 0, 0, 0, 0,
0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0,
0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0,
0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0,
0, 0, 0, 0, 0, 0, 0, 0, 0,
0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
0, 0, 0, 0, 0, 0, 0, 0, 0,
0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
0, 0, 0, 0, 0, 0, 0, 0, 0,
0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
0, 0, 0, 0, 0, 0, 0, 0, 0,
0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
0, 0, 0, 0, 0, 0, 0, 0, 0,
0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
0, 0, 0, 0, 0, 0, 0, 0, 0,
0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
0, 0, 0, 0, 0, 0, 0, 0, 0,
0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
0, 0, 0, 0, 0, 0, 0, 0, 0,
0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
0, 0, 0, 0, 0, 0, 0, 0, 0,
0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
0, 0, 0, 0, 0, 0, 0, 0, 0,
0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
0, 0, 0, 0, 0, 0, 0, 0, 0,
0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
0, 0, 0, 0, 0, 0, 0, 0, 0,
0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
0, 0, 0, 0, 0, 0, 0, 0, 0,
0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0,
0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0,
0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0,
0, 0, 0, 0, 0, 0, 0, 0, 0,
0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
0, 0, 0, 0, 0, 0, 0, 0, 0,
0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
0, 0, 0, 0, 0, 0, 0, 0, 0,
0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
0, 0, 0, 0, 0, 0, 0, 0, 0,
0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
0, 0, 0, 0, 0, 0, 0, 0, 0,
0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
0, 0, 0, 0, 0, 0, 0, 0, 0,
0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
0, 0, 0, 0, 0, 0, 0, 0, 0,
0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
0, 0, 0, 0, 0, 0, 0, 0, 0,
0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
0, 0, 0, 0, 0, 0, 0, 0, 0,
0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
0, 0, 0, 0, 0, 0, 0, 0, 0,
0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
0, 0, 0, 0, 0, 0, 0, 0, 0,
0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
0, 0, 0, 0, 0, 0, 0, 0, 0,
0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
0, 0, 0, 0, 0, 0, 0, 0, 0,
0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0,
0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0,
0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0,
0, 0, 0, 0, 0, 0, 0, 0, 0,
0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
0, 0, 0, 0, 0, 0, 0, 0, 0,
0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
0, 0, 0, 0, 0, 0, 0, 0, 0,
0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
0, 0, 0, 0, 0, 0, 0, 0, 0,
0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
0, 0, 0, 0, 0, 0, 0, 0, 0,
0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
0, 0, 0, 0, 0, 0, 0, 0, 0,
0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
0, 0, 0, 0, 0, 0, 0, 0, 0,
0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
0, 0, 0, 0, 0, 0, 0, 0, 0,
0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
0, 0, 0, 0, 0, 0, 0, 0, 0,
0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
0, 0, 0, 0, 0, 0, 0, 0, 0,
0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
0, 0, 0, 0, 0, 0, 0, 0, 0,
0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
0, 0, 0, 0, 0, 0, 0, 0, 0,
0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
0, 0, 0, 0, 0, 0, 0, 0, 0,
0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
0, 0, 0, 0, 0, 0, 0, 0, 0,
0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
0, 0, 0, 0, 0, 0, 0, 0, 0
            };

    public static final boolean isSpriteVisible(Sprite _spr)
    {
        final int i_sX = i8_screenPosX;
        final int i_sY = i8_screenPosY;
        final int i_sw = i_GameAreaWidth << 8;
        final int i_sh = i_GameAreaHeight << 8;

        final int i_dX = _spr.i_ScreenX;
        final int i_dY = _spr.i_ScreenY;
        final int i_dw = _spr.i_width;
        final int i_dh = _spr.i_height;

        if (i_sX + i_sw < i_dX || i_sY + i_sh < i_dY || i_dX + i_dw < i_sX || i_dY + i_dh < i_sY)
            return false;
        else
            return true;
    }

    private static int calculateScoresForCadr(Sprite _spr)
    {
        if (_spr == null) return 0;

        if (_spr.i_Frame == 1) return 0;
        if (_spr.i_ObjectType == SPRITE_CLOSEDWINDOW || _spr.i_ObjectType == SPRITE_GUARDIAN) return 0;

        return _spr.i_ObjectState;
    }

    private static final Sprite getFocusedSprite()
    {
        for(int li=0;li<WINDOW_NUMBER;li++)
        {
            Sprite p_spr = WINDOW_SPRITES[li];

            // Проверка на попадание точки прице
            if (p_spr.isCollided(p_FocusSprite)) return p_spr;
        }
        return null;
    }

    private static final void initSprites()
    {
        int i_index = 0;
        for (int li = 0; li < ab_tileArray.length; li++)
        {
            int i_val = ab_tileArray[li];
            if (i_val == TILE_WINDOW_INDEX)
            {
                int i_x = li % CELLNUMBER_WIDTH;
                int i_y = li / CELLNUMBER_WIDTH;

                Sprite p_spr = new Sprite(i_index);
                p_spr.setMainPointXY((i_x * CELL_WIDTH) << 8, (i_y * CELL_HEIGHT) << 8);

                WINDOW_SPRITES[i_index] = p_spr;

                i_index++;
            }
        }

        p_FocusSprite = new Sprite(0);
        activateSprite(p_FocusSprite,SPRITE_FOCUS);
        p_IconSprite = new Sprite(0);
        p_IconSprite.lg_SpriteActive = false;
        p_IconController = new PathController();

        changeSpriteState(true);
    }

    /**
     * Массив параметров игровых спрайтов
     */
    private static final int[] ai_SpriteParameters = new int[]
            {
                        // Width, Height,ColOffsetX, ColOffsetY, ColWidth, ColHeight, Frames, FrameDelay, Main point, Animation
                        //SPRITE_EMPTYWINDOW
                        0x6C00, 0x6C00, 0x1500, 0x700, 0xB00, 0x1E00, 1, 1, Sprite.SPRITE_ALIGN_LEFT | Sprite.SPRITE_ALIGN_TOP, Sprite.ANIMATION_FROZEN,
                        //SPRITE_CLOSEDWINDOW
                        0x6C00, 0x6C00, 0x1500, 0x700, 0xB00, 0x1E00, 1, 1, Sprite.SPRITE_ALIGN_LEFT | Sprite.SPRITE_ALIGN_TOP, Sprite.ANIMATION_FROZEN,
                        //SPRITE_STAR0 (ТИнейджер)
                        0x6C00, 0x6C00, 44*256, 57*256, 18*256, 21*256, 1, 1, Sprite.SPRITE_ALIGN_LEFT | Sprite.SPRITE_ALIGN_TOP, Sprite.ANIMATION_FROZEN,
                        //SPRITE_STAR1 (Модель)
                        0x6C00, 0x6C00, 45*256, 44*256, 20*256, 20*256, 1, 1, Sprite.SPRITE_ALIGN_LEFT | Sprite.SPRITE_ALIGN_TOP, Sprite.ANIMATION_FROZEN,
                        //SPRITE_STAR2 (Телефонист)
                        0x6C00, 0x6C00, 44*256, 46*256, 16*256, 16*256, 1, 1, Sprite.SPRITE_ALIGN_LEFT | Sprite.SPRITE_ALIGN_TOP, Sprite.ANIMATION_FROZEN,
                        //SPRITE_STAR3 (Хиппи)
                        0x6C00, 0x6C00, 41*256, 39*256, 20*256, 18*256, 1, 1, Sprite.SPRITE_ALIGN_LEFT | Sprite.SPRITE_ALIGN_TOP, Sprite.ANIMATION_FROZEN,
                        //SPRITE_STAR4 (Олигархи)
                        0x6C00, 0x6C00, 25*256, 45*256, 16*256, 13*256, 1, 1, Sprite.SPRITE_ALIGN_LEFT | Sprite.SPRITE_ALIGN_TOP, Sprite.ANIMATION_FROZEN,
                        //SPRITE_GUARDIAN (Охранник)
                        0x6C00, 0x6C00, 34*256, 34*256, 37*256, 52*256, 1, 1, Sprite.SPRITE_ALIGN_LEFT | Sprite.SPRITE_ALIGN_TOP, Sprite.ANIMATION_FROZEN,
                        //SPRITE_STARICO
                        0x1000, 0x1000, 0x1500, 0x700, 0xB00, 0x1E00, 1, 1, Sprite.SPRITE_ALIGN_CENTER, Sprite.ANIMATION_FROZEN,
                        //SPRITE_GUARDICO
                        0x1000, 0x1000, 0x1500, 0x700, 0xB00, 0x1E00, 1, 1, Sprite.SPRITE_ALIGN_CENTER, Sprite.ANIMATION_FROZEN,
                        //SPRITE_FOCUS
                        0x2000, 0x2000, 0x0, 0x0, 0x2000, 0x2000, 1, 1, Sprite.SPRITE_ALIGN_CENTER, Sprite.ANIMATION_FROZEN,
                };

    private static final void processGame(int _key)
    {
        changeSpriteState(false);

        p_IconController.processStep();

        if (p_IconController.isCompleted()) p_IconSprite.lg_SpriteActive = false;

        if ((System.currentTimeMillis()>l_FinalTime) || (i_currentCadr == 0 && !p_IconSprite.lg_SpriteActive))
        {
            p_IconSprite.lg_SpriteActive = false;
            if (i_coolCadres > INT_MAX_CADR_NUMBER/2)
            {
                i_PlayerState = PLAYER_WIN;
            }
            else
            {
                i_PlayerState = PLAYER_LOST;
            }
            return;
        }

        int i_fire = i_DirectionKey & KEY_FIRE;

        // Обрабатываем кнопки
        if ((_key & KEY_DOWN) != 0)
        {
            // Двигаемся вниз
            i_DirectionKey = KEY_DOWN;
        }
        else if ((_key & KEY_UP) != 0)
        {
            // Двигаемся вверх
            i_DirectionKey = KEY_UP;
        }
        else if ((_key & KEY_LEFT) != 0)
        {
            // Двигаемся вверх
            i_DirectionKey = KEY_LEFT;
        }
        else if ((_key & KEY_RIGHT) != 0)
        {
            // Двигаемся вверх
            i_DirectionKey = KEY_RIGHT;
        }
        i_DirectionKey |= i_fire;

        // Выявляем вспышку
        lg_Flash = false;
        if ((_key & KEY_FIRE) != 0)
        {
            if (i_fire == 0 && i_currentCadr>0)
            {
                lg_Flash = true;
                i_DirectionKey |= KEY_FIRE;
            }
        }
        else
        {
            i_DirectionKey &= ~KEY_FIRE;
        }

        // Отработка съемки
        if (lg_Flash)
        {
            i_currentCadr --;

            // Проверка на попавшие в кадр спрайты
            Sprite p_focused = getFocusedSprite();
            int i_scores = calculateScoresForCadr(p_focused);

            i_PlayerScore += i_scores;
            if (i_PlayerScore<0) i_PlayerScore = 0;

            if (i_scores<=0)
            {
                activateSprite(p_IconSprite,SPRITE_GUARDICO);
            }
            else
            {
                activateSprite(p_IconSprite,SPRITE_STARICO);
                i_coolCadres ++;
            }

            if (p_focused!=null)
            {
                if (p_focused.i_Frame == 0)
                {
                    p_focused.i_Frame = 1;
                    p_focused.i_ObjectState = PAUSE_TILL_PROCESSING;
                }

                if (p_focused.i_ObjectType == SPRITE_GUARDIAN)
                {
                    // Уменьшаем время в два раза
                    long l_cur = System.currentTimeMillis();
                    int i_tillEnd = (int)(l_FinalTime - l_cur);
                    l_FinalTime = (i_tillEnd>>1)+l_cur;
                }

                switch(p_focused.i_ObjectType)
                {
                    case SPRITE_CLOSEDWINDOW  :
                    case SPRITE_EMPTYWINDOW :
                            p_actionListener.processGameAction(GAMEACTION_FLASH);
                        ;break;
                    case SPRITE_GUARDIAN :
                            p_actionListener.processGameAction(GAMEACTION_GUARDIAN);
                        ;break;
                    case SPRITE_STAR0 :
                            p_actionListener.processGameAction(GAMEACTION_STAR0);
                        ;break;
                    case SPRITE_STAR1:
                        p_actionListener.processGameAction(GAMEACTION_STAR1);
                        ;break;
                    case SPRITE_STAR2:
                        p_actionListener.processGameAction(GAMEACTION_STAR2);
                        ;break;
                    case SPRITE_STAR3:
                        p_actionListener.processGameAction(GAMEACTION_STAR3);
                        ;break;
                    case SPRITE_STAR4:
                        p_actionListener.processGameAction(GAMEACTION_STAR4);
                        ;break;
                }
            }
            else
                p_actionListener.processGameAction(GAMEACTION_FLASH);

            p_IconController.initPath(p_FocusSprite.i_mainX,p_FocusSprite.i_mainY,0x100,0x100,p_IconSprite,ash_Paths,PATH_ICON,0,0,PathController.MODIFY_NONE);
        }

        // Отрабатываем перемещение
        switch (i_DirectionKey & ~KEY_FIRE)
        {
        case KEY_DOWN:
             {
                 i8_screenPosY += VERT_SPEED;

                 if (i8_screenPosY+(i_GameAreaHeight<<8)>=(CELL_HEIGHT*CELLNUMBER_HEIGHT<<8))
                 {
                     i8_screenPosY = (CELL_HEIGHT*CELLNUMBER_HEIGHT<<8) - (i_GameAreaHeight<<8);
                     i_DirectionKey &= ~KEY_DOWN;
                     i_DirectionKey |= KEY_UP;
                 }
             }
        ;
             break;
        case KEY_LEFT:
             {
                 i8_screenPosX -= HORZ_SPEED;

                 if (i8_screenPosX<=0)
                 {
                     i8_screenPosX = 0;
                     i_DirectionKey &= ~KEY_LEFT;
                     i_DirectionKey |= KEY_RIGHT;
                 }
             }
        ;
             break;
        case KEY_RIGHT:
             {
                 i8_screenPosX += HORZ_SPEED;

                 if (i8_screenPosX+(i_GameAreaWidth<<8)>=(CELL_WIDTH*CELLNUMBER_WIDTH<<8))
                 {
                     i8_screenPosX = (CELL_WIDTH*CELLNUMBER_WIDTH<<8) - (i_GameAreaWidth<<8);
                     i_DirectionKey &= ~KEY_RIGHT;
                     i_DirectionKey |= KEY_LEFT;
                 }
             }
        ;
             break;
        case KEY_UP:
             {
                 i8_screenPosY -= VERT_SPEED;

                 if (i8_screenPosY<=0)
                 {
                     i8_screenPosY = 0;
                     i_DirectionKey &= ~KEY_UP;
                     i_DirectionKey |= KEY_DOWN;
                 }
             }
        ;
             break;
        }

        setFocusCoordinate();
    }

    private static final void setFocusCoordinate()
    {
        //#if MODEL=="C380"
        //$p_FocusSprite.setMainPointXY(i8_screenPosX+(i_GameAreaWidth<<7),i8_screenPosY+(i_GameAreaHeight<<7));
        //#else
        p_FocusSprite.setMainPointXY(i8_screenPosX+(i_GameAreaWidth<<7),i8_screenPosY+(i_GameAreaHeight<<7));
        //#endif
    }

    private static final void initGame()
    {
        i_currentCadr = INT_MAX_CADR_NUMBER;
        i_coolCadres = 0;
        i_DirectionKey = KEY_RIGHT;
        lg_Flash = false;

        i8_screenPosX = 0;
        i8_screenPosY = 0;

        setFocusCoordinate();
    }

    private static final int PAUSE_TILL_PROCESSING = 70;

    private static final void changeSpriteState(boolean _all)
    {
        final int CONST_CLOSEDWINDOW = 6;
        final int CONST_OPENEDWINDOW = 6;
        final int CONST_GUARDIAN = 3;

        for(int li=0;li<WINDOW_NUMBER;li++)
        {
            Sprite p_spr = WINDOW_SPRITES[li];

            if ((p_spr.i_ObjectType == SPRITE_EMPTYWINDOW || p_spr.i_ObjectType == SPRITE_GUARDIAN) || _all)
            {
              if (!_all && p_spr.i_ObjectState>0)
              {
                  p_spr.i_ObjectState --;
                  continue;
              }

            if (!isSpriteVisible(p_spr) || _all)
            {
                if (getRandomInt(CONST_CLOSEDWINDOW)==(CONST_CLOSEDWINDOW>>>1))
                {
                    activateSprite(p_spr,SPRITE_CLOSEDWINDOW);
                }
                else
                        if (getRandomInt(CONST_GUARDIAN)==(CONST_GUARDIAN>>>1))
                        {
                            activateSprite(p_spr,SPRITE_GUARDIAN);
                        }
                    else
                    if (getRandomInt(CONST_OPENEDWINDOW)==(CONST_OPENEDWINDOW>>>1))
                    {
                        activateSprite(p_spr,SPRITE_EMPTYWINDOW);
                    }
                else
                    {
                        int i_star = getRandomInt(69999)/10000;
                        activateSprite(p_spr,i_star);
                    }

                int i_score = 0;

                switch(p_spr.i_ObjectType)
                {
                    case SPRITE_GUARDIAN : i_score = 300;break;
                    case SPRITE_EMPTYWINDOW :
                        {
                            i_score = 0;
                            p_spr.i_ObjectState = PAUSE_TILL_PROCESSING;
                        };break;
                    case SPRITE_CLOSEDWINDOW : i_score = 30;break;
                    case SPRITE_STAR0 : i_score = 10;break;
                    case SPRITE_STAR1 : i_score = 20;break;
                    case SPRITE_STAR2 : i_score = 30;break;
                    case SPRITE_STAR3 : i_score = 40;break;
                    case SPRITE_STAR4 : i_score = 50;break;
                }
                p_spr.i_ObjectState = i_score;
            }
            }
            else
            if (p_spr.i_ObjectType != SPRITE_CLOSEDWINDOW)
            {
                if (p_spr.i_Frame == 1)
                {
                    if (p_spr.i_ObjectState<=0)
                    {
                       activateSprite(p_spr,SPRITE_EMPTYWINDOW);
                       p_spr.i_ObjectState = PAUSE_TILL_PROCESSING;

                    }
                    else
                        p_spr.i_ObjectState--;
                }
            }
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

    //============================================================================
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
     * @return true если инициализация прошла успешно, иначе false.
     */
    public static final boolean init()
    {
        if (i_GameState != STATE_UNKNON) return false;
        p_actionListener = null;
        i_GameAreaHeight = -1;
        i_GameAreaWidth = -1;

        //------------Вставьте свой код здесь--------------------

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
        initSprites();
        initGame();

        i_PlayerScore = 0;

        i_GameStepDelay = TIME_DELAY;

        switch(_gameLevel)
        {
            case GAMELEVEL_EASY :  i_TimeForLevel = TIME_EASY;break;
            case GAMELEVEL_NORMAL :  i_TimeForLevel = TIME_NORMAL;break;
            case GAMELEVEL_HARD :  i_TimeForLevel = TIME_HARD;break;
        }

        l_FinalTime = System.currentTimeMillis() + i_TimeForLevel;
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
        p_FocusSprite = null;
        p_IconController = null;
        p_IconSprite = null;
        for(int li=0;li<WINDOW_NUMBER;li++) WINDOW_SPRITES[li] = null;
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
            l_pausedDelayTillFinal = l_FinalTime - System.currentTimeMillis();
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
            l_FinalTime = System.currentTimeMillis() + l_pausedDelayTillFinal;
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
        int i_result = i_PlayerScore*(i_GameLevel+1)+(INT_MAX_CADR_NUMBER-i_currentCadr)*5;
        //--------------------------------------------------
        return i_result;
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
        l_pausedDelayTillFinal = p_inputStream.readLong();
        i_currentCadr = p_inputStream.readInt();
        i_coolCadres = p_inputStream.readInt();
        lg_Flash = p_inputStream.readBoolean();
        i_PlayerScore = p_inputStream.readInt();

        i8_screenPosX = p_inputStream.readInt();
        i8_screenPosY = p_inputStream.readInt();

        i_DirectionKey = p_inputStream.readInt();

        for(int li=0;li<WINDOW_NUMBER;li++)
        {
            Sprite p_spr = WINDOW_SPRITES[li];

            int i_type = p_inputStream.readUnsignedByte();
            int i_state = p_inputStream.readInt();
            int i_frame = p_inputStream.readByte();

            activateSprite(p_spr,i_type);
            p_spr.i_ObjectState = i_state;
            p_spr.i_Frame = i_frame;
        }

        setFocusCoordinate();

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

        p_outputStream.writeLong(l_pausedDelayTillFinal);
        p_outputStream.writeInt(i_currentCadr);
        p_outputStream.writeInt(i_coolCadres);
        p_outputStream.writeBoolean(lg_Flash);
        p_outputStream.writeInt(i_PlayerScore);

        p_outputStream.writeInt(i8_screenPosX);
        p_outputStream.writeInt(i8_screenPosY);

        p_outputStream.writeInt(i_DirectionKey);

        for(int li=0;li<WINDOW_NUMBER;li++)
        {
            Sprite p_spr = WINDOW_SPRITES[li];

            p_outputStream.writeByte(p_spr.i_ObjectType);
            p_outputStream.writeInt(p_spr.i_ObjectState);
            p_outputStream.writeByte(p_spr.i_Frame);
        }

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

        //l_pausedDelayTillFinal;
        //i_currentCadr;
        //lg_Flash;
        //i8_screenPosX
        //i8_screenPosY
        //i_PlayerScore;

        MINIMUM_SIZE += 8+4+1+4+8+4+4;

        MINIMUM_SIZE += (WINDOW_NUMBER*6);

        return MINIMUM_SIZE;
    }

    /**
     * Возвращает текстовый идентификатор игры
     *
     * @return строка, идентифицирующая игру.
     */
    public static final String getID()
    {
        return "9874i2";
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
        processGame(_keyStateFlags);
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
}
