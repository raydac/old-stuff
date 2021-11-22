
public class Stages
{
    public static final int NUMBER_OF_LEVELS = 8;
    // Helicopter base - right-bottom corner of game level (width 10 points)

    public static final byte PEOPLE = 0;
    public static final byte VULCANE = 1;

    public static final byte DEF_WIDTH = 4;   // Fog - 20?
    public static final byte DEF_HEIGHT = 4;

    public static final byte FLY_LEFT = 0;    // Stone
    public static final byte FLY_RIGHT = 1;   // Stone
    public static final byte FLYFOG_UP = 2;   // Fog

    public static final byte DEF_RADIUS = 90; // 0-100
    public static final byte DEF_XRAD = 2;    // If = 100 ball can fly direct up

    public static final byte DEF_SPEED = 1;   // Y increase number

    public static final byte DEF_EXPLOSION = 0;  // 0 - stone/fog ,
    public static final byte DEF_ON_EXPLOSION = 1; //1 - explosive stone

    // 1 level point = 2 screen point
    /**
     * первый уровень расчет ячейка 5х5
     */
//    static byte level0[][] = new byte[][]{
//        {1,1,1,1,1,1,1,1,1,1,6, 7, 7, 7, 10, 13, 16, 19, 20, 21, 23, 22, 21, 21, 23, 18, 17, 14, 13, 9, 8, 6, 6, 6, 7, 8, 10, 15, 20, 27, 26, 25, 25, 26, 26, 27, 28, 25, 20,
//         19, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
    static byte level0[][] = new byte[][]{
        {31, 32, 32, 32, 33, 33, 34, 34, 34, 34, 32, 31, 30, 32, 42, 49, 53, 60, 60, 58, 56, 56, 56, 57, 59, 61, 60, 58, 53, 51, 45, 43, 41, 41, 42, 43, 44, 44,
         42, 38, 33, 29, 28, 27, 21, 17, 5, 3, 2, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
        {0, 0, 1, 0, 1, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 2, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
         0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0}
    };

    static byte level1[][] = new byte[][]{
        {51, 51, 51, 51, 51, 50, 50, 49, 49, 48, 47, 52, 55, 62, 68, 73, 74, 74, 74, 73, 70, 68, 63, 55, 52, 51, 34, 32, 31, 30, 29, 29, 28, 28, 28, 28, 28,
         28, 28, 30, 36, 41, 42, 43, 45, 48, 57, 58, 58, 58, 58, 56, 50, 44, 43, 42, 43, 45, 45, 45, 42, 37, 34, 26, 24, 23, 20, 14, 12, 10, 9, 7, 5, 2, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
        {0, 0, 1, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 2, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 1,
         0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 3, 0, 0, 0, 0, 4, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0}

    };
    static byte level2[][] = new byte[][]{
        {40, 41, 41, 43, 33, 32, 30, 30, 30, 30, 30, 33, 34, 34, 35, 36, 37, 38, 38, 40, 44, 45, 47, 48, 48, 51,
         50, 49, 48, 46, 45, 42, 40, 38, 37, 36, 37, 38, 43, 47, 51, 53, 55, 55, 58, 57, 57, 55, 55, 56, 56, 59,
         58, 57, 56, 55, 52, 50, 50, 51, 40, 36, 30, 26, 26, 25, 25, 25, 24, 15, 13, 8, 6, 4, 3, 3, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
        {5, 0, 6, 0, 0, 0, 1, 0, 1, 0, 0, 1, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 7, 0, 0,
         0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 2, 0, 0, 0, 0, 0, 0, 0, 0, 5, 0, 0, 6, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0}
    };
    static byte level3[][] = new byte[][]{
        {30, 31, 31, 34, 29, 29, 28, 27, 25, 23, 23, 23, 21, 21, 20, 20, 19, 19, 19, 19, 20, 20, 20, 20, 20, 20, 20, 20, 20, 21, 21, 21, 21, 21, 21, 22, 23, 23,
         24, 25, 27, 30, 31, 34, 35, 44, 42, 41, 40, 41, 45, 48, 50, 52, 58, 61, 71, 72, 70, 69, 68, 68, 68, 68, 71, 71, 68, 64, 62, 59, 56, 53, 54, 44, 44, 44,
         42, 41, 41, 41, 41, 41, 41, 41, 39, 38, 38, 36, 34, 33, 32, 27, 24, 11, 4, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
        {10, 0, 0, 9, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 1, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1,
         0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 11, 0, 12, 0, 0, 0, 0, 0, 0, 0, 0, 0, 2, 0, 0, 0, 0, 0, 0, 0, 8, 0, 0, 0, 7, 0, 0,
         0, 0, 0, 0, 0, 1, 0, 1, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0}   //15,18,22,27,32,37,81,83,86
    };
    static byte level4[][] = new byte[][]{
        {41, 41, 41, 41, 41, 41, 40, 40, 38, 34, 34, 33, 32, 30, 28, 24, 21, 19, 19, 18, 18, 18, 18, 18, 18, 18, 18, 18, 18, 18, 18, 20, 24, 28, 30, 31, 34, 41,
         43, 45, 41, 40, 40, 41, 41, 41, 41, 40, 45, 45, 44, 41, 34, 32, 29, 27, 23, 25, 30, 32, 33, 34, 40, 42, 44, 45, 46, 49, 50, 50, 50, 49, 50, 50, 51, 51,
         51, 51, 51, 51, 51, 51, 52, 52, 52, 52, 52, 49, 49, 46, 45, 44, 43, 40, 38, 31, 30, 28, 26, 21
         , 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
        {0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 1, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
         0, 0, 15, 16, 14, 13, 7, 8, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 1, 0, 0, 1, 0,
         0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0}
    };
    static byte level5[][] = new byte[][]{
        {10, 10, 11, 12, 12, 12, 13, 15, 17, 19, 23, 27, 30, 34, 42, 42, 39, 39, 39, 39, 39, 39, 41, 43, 34, 23, 17, 15, 14, 12, 12, 11, 10, 10, 9, 9, 9, 9, 9,
         9, 8, 7, 7, 7, 7, 7, 6, 6, 6, 6, 5, 5, 5, 4, 4, 4, 4, 4, 4, 4, 5, 5, 5, 5, 5, 5, 5, 5, 5, 6, 6, 6, 7, 7, 8, 8, 8, 10, 12, 13, 15, 18, 21, 22, 23, 27, 33, 35, 41,
         45, 57, 61, 67, 66, 65, 63, 63, 63, 63, 63, 63, 65, 66, 67, 57, 51, 47, 45, 40, 38, 36, 32, 30, 30, 30, 30, 30, 30, 30, 32, 34, 52, 65, 67, 68, 67, 65,
         64, 64, 65, 65, 66, 66, 69, 64, 54, 51, 38, 36, 34, 27, 24, 21, 20, 19, 18, 16, 11, 7, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
        {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 15, 16, 14, 0/*17*/, 13, 18, 7, 5, 10, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
         1, 0, 0, 0, 1, 0, 0, 0, 0, 1, 0, 0, 1, 0, 0, 1, 0, 0, 0, 0, 1, 0, 0, 0, 0, 1, 0, 0, 0, 1, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
         0, 0, 0, 0, 0, 0, 0, 0, 0, 3, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
         0, 2, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0}  //39,43,48,51,54,59,64,68,72,113,118
    };
    static byte level6[][] = new byte[][]{
        {14, 13, 12, 10, 7, 7, 7, 7, 6, 5, 5, 5, 4, 4, 4, 4, 4, 4, 11, 15, 18, 22, 29, 33, 30, 29, 28, 28, 28, 28, 28, 29, 31, 18, 15, 14, 13, 8, 8, 8, 7, 7, 6, 6, 6,
         6, 5, 5, 6, 6, 8, 10, 12, 13, 19, 23, 26, 30, 40, 44, 51, 61, 63, 65, 68, 70, 69, 68, 67, 67, 67, 67, 66, 66, 66, 66, 67, 67, 68, 68, 70, 64, 60, 40, 33,
         31, 30, 28, 27, 26, 25, 20, 19, 17, 15, 12, 12, 11, 10, 9, 9, 9, 10, 10, 10, 10, 11, 11, 12, 14, 15, 17, 23, 28, 25, 24, 23, 22, 22, 22, 22, 22, 22, 22,
         23, 23, 25, 28, 24, 25, 19, 17, 15, 14, 13, 12, 10, 9, 9, 9, 8, 8, 8, 8, 7, 6, 3, 2, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
        {0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 11, 12, 14, 15, 16, 19, 9, 10, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0,
         0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 3, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
         0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 11, 12, 14, 15, 16, 19, 9, 10, 0,
         7, 0, 0, 0, 4, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0} //5,12,43,48,100,104,140
    };

    static byte level7[][] = new byte[][]{
        {34, 34, 34, 37, 38, 38, 35, 31, 28, 27, 26, 26, 24, 24, 24, 23, 23, 23, 23, 22, 22, 21, 21, 20, 20, 20, 19, 19, 19, 19, 19, 19, 18, 18, 19, 19, 19, 19,
         19, 19, 19, 19, 20, 20, 23, 26, 29, 31, 40, 42, 49, 54, 51, 50, 49, 49, 49, 49, 49, 51, 52, 52, 54, 51, 45, 34, 28, 20, 18, 17, 17, 16, 16, 16, 16, 15,
         15, 15, 16, 16, 16, 16, 16, 16, 16, 17, 17, 17, 19, 19, 19, 20, 21, 21, 21, 22, 24, 25, 27, 29, 32, 34, 37, 49, 52, 52, 50, 49, 48, 48, 48, 49, 49, 49,
         49, 51, 52, 52, 32, 30, 29, 28, 28, 27, 27, 26, 25, 24, 23, 23, 22, 22, 22, 22, 23, 23, 23, 26, 27, 29, 31, 32, 32, 33, 33, 33, 30, 30, 24, 11, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
        {0, 0, 19, 9, 4, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 1, 0, 0, 0, 1,
         0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 11, 12, 0, 0, 2, 19, 9, 8, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
         1, 0, 0, 0, 0, 1, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 22, 0, 0, 0, 0, 2, 0, 21,
         0, 10, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 1, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0}
        //11,16,22,26,29,33,37,76,81,85,92,128,131,135    11,12,14,15,16,19,9,10
    };
    static byte level8[][];
    static byte level9[][];

    // ObjType,W,H,vtype,radius,xdecr,speed,explosive
    // 10 parameters per object
    public static int OBJ_LENGHT = 10;
    static byte objects[][] =
            {
/* 1 */      {PEOPLE, DEF_WIDTH, DEF_HEIGHT, FLY_RIGHT, DEF_RADIUS, DEF_XRAD, DEF_SPEED, DEF_EXPLOSION}, // 1

/* 2 */      {VULCANE, DEF_WIDTH * 3, DEF_HEIGHT * 2, FLYFOG_UP, DEF_RADIUS, DEF_XRAD, DEF_SPEED, DEF_EXPLOSION}, // 2
/* 3 */      {VULCANE, DEF_WIDTH * 3, DEF_HEIGHT * 2, FLYFOG_UP, DEF_RADIUS, DEF_XRAD, DEF_SPEED * 2, DEF_EXPLOSION}, // 2

/* 4 */      {VULCANE, DEF_WIDTH, DEF_HEIGHT, FLY_RIGHT, 15, DEF_XRAD, DEF_SPEED, DEF_ON_EXPLOSION}, // 3

/* 5 */      {VULCANE, DEF_WIDTH, DEF_HEIGHT, FLY_RIGHT, 30, 2, DEF_SPEED, DEF_ON_EXPLOSION}, // 3
/* 6 */      {VULCANE, DEF_WIDTH, DEF_HEIGHT, FLY_RIGHT, 30, 1, DEF_SPEED, DEF_EXPLOSION}, // 3

/* 7 */      {VULCANE, DEF_WIDTH, DEF_HEIGHT, FLY_RIGHT, 40, 1, DEF_SPEED * 2, DEF_EXPLOSION}, // 3
/* 8 */      {VULCANE, DEF_WIDTH, DEF_HEIGHT, FLY_RIGHT, 30, 1, DEF_SPEED, DEF_ON_EXPLOSION},
/* 9 */      {VULCANE, DEF_WIDTH, DEF_HEIGHT, FLY_RIGHT, 50, 2, DEF_SPEED, DEF_ON_EXPLOSION}, // 3
/*10 */      {VULCANE, DEF_WIDTH, DEF_HEIGHT, FLY_RIGHT, 50, 1, DEF_SPEED, DEF_EXPLOSION}, // 3
/*11 */      {VULCANE, DEF_WIDTH, DEF_HEIGHT, FLY_LEFT, 50, 2, DEF_SPEED, DEF_ON_EXPLOSION}, // 3
/*12 */      {VULCANE, DEF_WIDTH, DEF_HEIGHT, FLY_LEFT, 50, 1, DEF_SPEED, DEF_EXPLOSION}, // 3

/*13 */      {VULCANE, DEF_WIDTH, DEF_HEIGHT, FLY_RIGHT, 55, 4, DEF_SPEED, DEF_EXPLOSION}, // 3
/*14 */      {VULCANE, DEF_WIDTH, DEF_HEIGHT, FLY_LEFT, 55, 4, DEF_SPEED, DEF_ON_EXPLOSION}, // 3
/*15 */      {VULCANE, DEF_WIDTH, DEF_HEIGHT, FLY_LEFT, 20, 1, DEF_SPEED, DEF_ON_EXPLOSION}, // 3
/*16 */      {VULCANE, DEF_WIDTH, DEF_HEIGHT, FLY_LEFT, 40, 1, DEF_SPEED, DEF_EXPLOSION}, // 3

/*17 */      {VULCANE, DEF_WIDTH, DEF_HEIGHT, FLY_RIGHT, 60, 1, DEF_SPEED * 2, DEF_EXPLOSION}, // 3
/*18 */      {VULCANE, DEF_WIDTH, DEF_HEIGHT, FLY_RIGHT, 30, 1, DEF_SPEED * 2, DEF_EXPLOSION}, // 3

/*19 */      {VULCANE, DEF_WIDTH, DEF_HEIGHT, FLY_RIGHT, 70, 10, DEF_SPEED, DEF_ON_EXPLOSION}, // 3
/*20 */      {VULCANE, DEF_WIDTH, DEF_HEIGHT, FLY_LEFT, 30, 1, DEF_SPEED * 2, DEF_EXPLOSION}, // 3

/*21 */      {VULCANE, DEF_WIDTH, DEF_HEIGHT, FLY_RIGHT, 30, 1, DEF_SPEED, DEF_EXPLOSION}, // 3

/*22 */      {VULCANE, DEF_WIDTH, DEF_HEIGHT, FLY_LEFT, 30, 1, DEF_SPEED, DEF_EXPLOSION}, // 3

            };

    public static int[] initStageObjects(int _stage)
    {
        byte tmp[][] = null;
        switch (_stage)
        {
            case 0:
                tmp = level0;
                break;
            case 1:
                tmp = level1;
                break;
            case 2:
                tmp = level2;
                break;
            case 3:
                tmp = level3;
                break;
            case 4:
                tmp = level4;
                break;
            case 5:
                tmp = level5;
                break;
            case 6:
                tmp = level6;
                break;
            case 7:
                tmp = level7;
                break;       /*
                   case 8:
                       tmp = level8;
                       break;
                   case 9:
                       tmp = level9;
                       break;         */
        }
        int ppl = 0;
        int vlk = 0;
        for (int i = 0; i < tmp[1].length; i++)
        {
            if (tmp[1][i] != 0)
            {
                int idx = tmp[1][i] - 1;
                if (objects[idx][0] == VULCANE)
                {
                    RescueHelicopter.vulcan[vlk] = new RescueHelicopter.VulcanObject(objects[idx][1], objects[idx][2], true);
                    RescueHelicopter.vulcan[vlk].vtype = objects[idx][3];
                    RescueHelicopter.vulcan[vlk].radius = objects[idx][4];
                    RescueHelicopter.vulcan[vlk].xdecr = objects[idx][5];
                    RescueHelicopter.vulcan[vlk].speed = objects[idx][6];
                    RescueHelicopter.vulcan[vlk].aY = (byte)(RescueHelicopter.H - tmp[0][i] - (objects[idx][2] / 2));
                    RescueHelicopter.vulcan[vlk].aX = (byte)i;
                    if (objects[idx][3] == 2)
                    {
                        RescueHelicopter.vulcan[vlk].VCOUNTERMAX = (byte) (RescueHelicopter.vulcan[vlk].aY + objects[idx][2]);
                        RescueHelicopter.vulcan[vlk].aX = (byte)(i - 4);
                        RescueHelicopter.vulcan[vlk].aY = (byte)(RescueHelicopter.H - tmp[0][i - 4] - (objects[idx][2] / 2));
                    }
                    if (objects[idx][7] == DEF_EXPLOSION)
                        RescueHelicopter.vulcan[vlk].explosive = false;
                    else
                        RescueHelicopter.vulcan[vlk].explosive = true;
                    vlk++;
                }
                else
                    if (objects[idx][0] == PEOPLE)
                    {
                        RescueHelicopter.peoples[ppl] = new RescueHelicopter.VulcanObject((byte) 5, (byte) 5, true);
                        RescueHelicopter.peoples[ppl].aY = (byte)(RescueHelicopter.H - tmp[0][i] - 5);
                        RescueHelicopter.peoples[ppl].aX = (byte)i;
                        ppl++;
                    }
            }
        }
        return null;
    }

    public static byte[] getStage(int _stage)
    {
        byte tmp[][] = null;
        switch (_stage)
        {
            case 0:
                tmp = level0;
                break;
            case 1:
                tmp = level1;
                break;
            case 2:
                tmp = level2;
                break;
            case 3:
                tmp = level3;
                break;
            case 4:
                tmp = level4;
                break;
            case 5:
                tmp = level5;
                break;
            case 6:
                tmp = level6;
                break;
            case 7:
                tmp = level7;
                break;
                /*case 8:
                    tmp = level8;
                    break;
                case 9:
                    tmp = level9;
                    break;  */
        }
        byte[] a = new byte[tmp[0].length];
        System.arraycopy(tmp[0], 0, a, 0, tmp[0].length);
        return a;
    }

}
