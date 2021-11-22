//package com.GameKit_3.SantaClaus;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.DataInputStream;
import java.io.ByteArrayOutputStream;

// ---------8<------------------------

public class GameletImpl extends java.util.Random
{

public long [][]alal_masks;

public static final int[] ai_wayarray = new int[]
 {
        //   0, 2, 3, 11, 5, 6, 8, 7, 9, 4, 12, 1, 12, 10, 0, 11, 2, 2, 3, 12, 1, 12, 10, 8, 9, 0, 7, 7, 3, 5, 6, 8,11,7,0, 9, 4, 3, 2, 11, 5, 8,12,1,12,10
        //8,8,8,3,8,8,4,12,7,13,11,1,5,9,6,3,1,12,10,8,
        7, 7, 7, 7, 7, 7, 2, 7, 7, 3, 11/*!*/, 6, 12, 10, 0, 4, 8, 5, 2, 9, 11/*!*/, 9, 7, //23
        //5, 2, 13, 2, 13, 11, 8, 3,10, 9, 4, 6, 7, 13,2,13,11,1,12,9,
        4/*S*/, 1, 12, 1, 12, 10, 7, 2, 9, 8, 3, 9, 6, 12, 1, 12, 10, 9, 11/*!*/, 8, //43
        //3,10,1,6,4,9,5,13,11,//50
        2, 2, 0, 5, 3, 8, 4, 12, 10,
        //8,8,4,9,3,6,3,7,13,2,13,11,8,1,10,5,12,4,5,11,
        7, 7, 3, 8, 2, 5, 9, 6, 12, 1, 12, 10, 7, 0, 4/*S*/, 9, 11/*!*/, 3, 4, 10,
        //9,13,2,13,11,6,4,4,3,3,10,10,10,1,12,1,8,3,9,4,
        8, 12, 1, 12, 10, 5, 3, 3, 2, 2, 9, 9, 3, 9, 11/*!*/, 0, 7, 2, 8, 3,
        //7,13,2,13,11,9,5,11,8,4,8,1,9,6,5,2,13,11,4,3,
        6, 12, 1, 12, 10, 8, 4/*S*/, 10, 7, 3, 7, 0, 8, 5, 4, 1, 12, 10, 3, 2,
        //3,3,12,9,1,4,8,8,8,10,10,3,3,7,13,2,13,11,1,3,
        2, 9, 11/*!*/, 8, 0, 3, 7, 7, 7, 9, 4/*S*/, 2, 2, 6, 12, 1, 12, 10, 0, 2,
        //6,9,10,3,4,5,11,7,13,2,13,11,8,9,6,4,12,9,1,3,4// 101
        5, 8, 9, 2, 3, 4, 10, 6, 12, 1, 12, 10, 7, 8, 5, 9, 11/*!*/, 8, 0, 2, 4, 3, 3, 3, 7, 7};


//public void setMaskArray(long[][] _mask) {  alal_masks = _mask; }
//public void setMapArray(byte[] _map) {  ai_wayarray = _map; }

    public static final int ALIGN_CENTER = 0;
    public static final int ALIGN_LEFT = 1;
    public static final int ALIGN_RIGHT = 2;

    public static final int[] ai_AlignArray = new int[]
    {
        //0
        ALIGN_CENTER,
        //1
        ALIGN_CENTER,
        //2
        ALIGN_CENTER,
        //3
        ALIGN_CENTER,
        //4
        ALIGN_RIGHT,
        //5
        ALIGN_LEFT,
        //6
        ALIGN_RIGHT,
        //7
        ALIGN_CENTER,
        //8
        ALIGN_CENTER,
        //9
        ALIGN_CENTER,
        //10
        ALIGN_LEFT,
        //11
        ALIGN_CENTER,
        //12
        ALIGN_CENTER
    };

    public static final int BLOCKLENGTH = 40;
    public static final int BLOCK_NUMBER = 12;

    // Format [i8_X,i8_Y]
    protected static final int[] ai_InitPlayerArray = new int[]{/*0x2f08a0, 0x6760*/0x11480, 0x60a0, 0x76bc0, 0x6040, 0x14da00, 0x60a0, 0x1edf20, 0x5f40, 0x265a60, 0x60a0};

    // Format [i8_X,i8_Y,freq]
    protected static final int[] ai_ChimneyArray = new int[]{0x476e0+(8<<8), 0x2500, 70, 0x62320, 0x300, 80, 0x6c220, 0x300, 80,
                                                             0x9e300, 0x300, 80, 0xad240, 0x300, 100, 0xcb380, 0x300, 100, 0xe46a0-(5<<8), 0x2500, 80,
                                                             0x125300, 0x300, 80,
                                                             0x1483e0-(3<<8), 0x2500, 80, 0x152320, 0x300, 80, 0x19d400, 0x300, 80, 0x1a2320, 0x300, 80,
                                                             0x1ac480, 0x300, 90, 0x1b4460+(5<<8), 0x2500, 80, 0x1b6760-(7<<8), 0x2500, 80, 0x206760-(7<<8), 0x2500, 100,
                                                             0x204480-(7<<8), 0x2500, 100, 0x238260, 0x300, 80, 0x247b40-(7<<8), 0x2500, 80, 0x2604e0, 0x300, 80,
                                                             0x28b420+(5<<8), 0x2500, 80, 0x2a1340, 0x300, 80, 0x2e2320, 0x300, 80, 0x2ef1a0+(7<<8), 0x2500, 80,
                                                             0x2f1ba0, 0x2500, 70


    };


//=======================Useful functions====================
    /**
     * Decompressing a byte array with RLE
     * @param inarray incomming packed byte array
     * @param dstlen length of outgoing bayte array
     * @return unpacked byte array
     */
    public static final byte[] RLEdecompress(byte[] inarray, int dstlen)
    {
        Runtime.getRuntime().gc();
        byte[] out = new byte[dstlen];
        int indx = 0;
        int outindx = 0;
        while (indx < inarray.length)
        {
            int val = inarray[indx++] & 0xFF;
            int counter = 1;
            int value = 0;
            if ((val & 0xC0) == 0xC0)
            {
                counter = val & 0x3F;
                value = inarray[indx++] & 0xFF;
            }
            else
            {
                value = val;
            }

            while (counter != 0)
            {
                out[outindx++] = (byte) value;
                counter--;
            }
        }
        Runtime.getRuntime().gc();
        return out;
    }

    /**
     * Compressing a byte array with RLE
     * @param _inarray incoming byte array
     * @return packed byte array
     */
    public static final byte[] RLEcompress(byte[] _inarray)
    {
        Runtime.getRuntime().gc();
        ByteArrayOutputStream baos = new ByteArrayOutputStream(_inarray.length);

        int inindx = 0;
        while (inindx < _inarray.length)
        {
            int value = _inarray[inindx++] & 0xFF;
            int count = 1;
            while (inindx < _inarray.length)
            {
                if ((_inarray[inindx] & 0xFF) == value)
                {
                    count++;
                    inindx++;
                    if (count == 63) break;
                }
                else
                    break;
            }
            if (count > 1)
            {
                baos.write(count | 0xC0);
                baos.write(value);
            }
            else
            {
                if (value > 63)
                {
                    baos.write(0xC1);
                    baos.write(value);
                }
                else
                    baos.write(value);
            }
        }

        byte[] outarray = baos.toByteArray();
        baos = null;
        System.gc();
        return outarray;
    }


    public final static short[] ai_sineTable = new short[]{0, 25, 50, 74, 98, 121, 142, 162, 181, 198, 213, 226, 237, 245, 251, 255, 256, 255, 251, 245, 237, 226, 213, 198, 181, 162, 142, 121, 98, 74, 50, 25, 0, -25, -50, -74, -98, -121, -142, -162, -181, -198, -213, -226, -237, -245, -251, -255, -256, -255, -251, -245, -237, -226, -213, -198, -181, -162, -142, -121, -98, -74, -50, -25};
    public static int xSine(int x, int index)       {  return (x * ai_sineTable[index]) >> 8;  }
    public static int xCoSine(int _x, int _index)   {  return (_x * ai_sineTable[(_index + 16) & 63]) >> 8;   }
    public static int xSineFloat(int _x, int _index){  return (_x * ai_sineTable[_index]); }
    public static int xCoSineFloat(int _x, int _index){return (_x * ai_sineTable[(_index + 16)& 63]);
    }

//===========================================================

    public static final int GAMESTATE_PLAYED = 0;
    public static final int GAMESTATE_OVER = 1;
    public static final int PLAYERSTATE_NORMAL = 0;
    public static final int PLAYERSTATE_WON = 1;
    public static final int PLAYERSTATE_LOST = 2;

    public int i_ScreenWidth;
    public int i_ScreenHeight;

    public void loadGameState(DataInputStream _dataInputStream) throws IOException
    {
        System.gc();

        //#if (FLAG_LEVELS_SUPPORT)
        int _level = _dataInputStream.readUnsignedByte();
        newGameSession(_level);
        //#endif

        //#if (FLAG_STAGES_SUPPORT)
        int _stage = _dataInputStream.readUnsignedByte();
        initStage(_stage);
        //#endif

        i_PlayerScore = _dataInputStream.readInt();
        i_AiScore = _dataInputStream.readInt();
        i_PlayerState = _dataInputStream.readUnsignedByte();
        i_AiState = _dataInputStream.readByte();

        readFromStream(_dataInputStream);

        _dataInputStream = null;
        System.gc();
    }

    /**
     * Save current game state to an output stream
     * @param _dataOutputStream
     * @throws java.io.IOException
     */
    public void saveGameState(DataOutputStream _dataOutputStream) throws IOException
    {
        System.gc();

        //#if (FLAG_LEVELS_SUPPORT)
        _dataOutputStream.writeByte(i_GameLevel);
        //#endif

        //#if (FLAG_STAGES_SUPPORT)
        _dataOutputStream.writeByte(i_GameStage);
        //#endif

        _dataOutputStream.writeInt(i_PlayerScore);
        _dataOutputStream.writeInt(i_AiScore);
        _dataOutputStream.writeByte(i_PlayerState);
        _dataOutputStream.writeByte(i_AiState);
        _dataOutputStream.flush();

        writeToStream(_dataOutputStream);

        _dataOutputStream = null;

        System.gc();
    }
//#endif

    /**
     * This function generates a pseudorandom value from 0 to a limit value (the limit value is included in the range)
     * @param _limit Limit value for generation
     * @return int value
     */
    public int getRandomInt(int _limit)
    {
        _limit++;
        _limit = (int) (((long) Math.abs(nextInt()) * (long) _limit) >>> 31);
        return _limit;
    }

    public void pauseGame()
    {
    }

    /**
     * Resume the game after pause
     */
    public void resumeGame()
    {
    }
//#if (FLAG_LEVELS_SUPPORT)
    /**
     * Current game level
     */
    public int i_GameLevel;
//#endif

//#if (FLAG_STAGES_SUPPORT)
    /**
     * Current game stage
     */
    public int i_GameStage;
//#endif

    /**
     * Current player's state
     */
    public int i_PlayerState;

    /**
     * Current AI state
     */
    public int i_AiState;

    /**
     * Current game state
     */
    public int i_GameState;

    /**
     * Current player score
     */
    public int i_PlayerScore;

    /**
     * Current ai score
     */
    public int i_AiScore;

//#if (FLAG_LEVELS_SUPPORT)
    /**
     * Initing of a game level
     */
    public void initLevel(int _level)
    {
        i_AiState = PLAYERSTATE_NORMAL;
        i_PlayerState = PLAYERSTATE_NORMAL;
        i_GameState = GAMESTATE_PLAYED;
        i_GameLevel = _level;
        i_PlayerScore = 0;
        i_AiScore = 0;
    }
//#endif

//#if (FLAG_STAGES_SUPPORT)
    /**
     * Initing of a game stage
     */
    public void initStage(int _stage)
    {
        i_GameStage = _stage;
        i_AiState = PLAYERSTATE_NORMAL;
        i_PlayerState = PLAYERSTATE_NORMAL;
        i_GameState = GAMESTATE_PLAYED;
    }
//#endif

    /**
     * Get current ai's score
     * @return the current ai scores as int value
     */
    public int getAIScore()
    {
        return i_AiScore;
    }
//------------------->8----------------------

    public static final int BUTTON_NONE = 0;
    public static final int BUTTON_LEFT = 1;
    public static final int BUTTON_RIGHT = 2;
    public static final int BUTTON_JUMPUPDIR = 3;
    public static final int BUTTON_FIRE = 4;
    public static final int BUTTON_COWER = 5;

    final static int MAX_HEIGHT = 128;
    private int I8_INIT_MOON_X_SCR;
    private int I8_INIT_MOON_Y_SCR;
    private boolean LG_MOON_LINK2PLAYER = false;

    public static final int LEVEL_0 = 0;
    private static final int LEVEL_TIMEDELAY_0 = 120;
    private static final int LEVEL_ATTEMPTIONS_0 = 3;
    private static final int LEVEL_CAT_FREQ_0 = 400;
    private static final int LEVEL_GHOST_FREQ_0 = 400;
    private static final int LEVEL_MOON_FREQ_0 = 600;

    public static final int LEVEL_1 = 1;
    private static final int LEVEL_TIMEDELAY_1 = 100;
    private static final int LEVEL_ATTEMPTIONS_1 = 3;
    private static final int LEVEL_CAT_FREQ_1 = 300;
    private static final int LEVEL_GHOST_FREQ_1 = 300;
    private static final int LEVEL_MOON_FREQ_1 = 500;

    public static final int LEVEL_2 = 2;
    private static final int LEVEL_TIMEDELAY_2 = 70;
    private static final int LEVEL_ATTEMPTIONS_2 = 3;
    private static final int LEVEL_CAT_FREQ_2 = 200;
    private static final int LEVEL_GHOST_FREQ_2 = 200;
    private static final int LEVEL_MOON_FREQ_2 = 450;

    private static final int I8_LEFT_BORDER_X = 100 << 8;
    public static final int I8_MAINPIPE_CENTER_X = 12217 << 8;
    public static final int I8_MAINPIPE_CENTER_Y = 102 << 8;

    private static final int PLAYER_ALIGN_LIMIT = 5;

//    private static final int I8_INIT_MOON_X_SCR = 160 << 8;
//    private static final int I8_INIT_MOON_Y_SCR = 70 << 8;
//    private static final boolean LG_MOON_LINK2PLAYER = true;

    public static final int MAX_MOVING_OBJECTS = 30;

    private static final int I8_JUMP_LEN = 0x2000;
    private static final int I8_JUMP_ANGLE_SPEED = 0x100;
    public static final int I8_HORIZ_PLAYER_SPEED = 0x200;

    private static final int I8_FSTAR_dX = 0x500;
    private static final int I8_FSTAR_dY = 0x500;

    private static final int I_DOWNBACKGROUNDBORDER = 60;
    private static final int I_UPBACKGROUNDBORDER = 20;
    private static final int I_BACKGROUNDSTEPGENERATION = 10;

    public static final int I8_STARTGROUND_OFFSETY = 0x4000;
    public static final int I_STARTGROUND_OFFSETY = I8_STARTGROUND_OFFSETY >>> 8;

    private static final int I8_FALLSPEED = 0x200;

    public static final int VIRTUALCELL_WIDTH = 2;
    public static final int VIRTUALCELL_HEIGHT = 1;

    private static final int I8_BLOCKHEIGHT = (VIRTUALCELL_HEIGHT * 64) << 8;
    private static final int SNOWBALL_FORCE_DELAY = 1;

    private static final int I8_SNOWBALL_START_YOFFSET = -5 << 8;
    private static final int I8_SNOWBALL_VERTSPEED_DELTA = 0x120;

    public int i_TimeDelay;
    public int i_Attemptions;

    public static final int MAX_SNOWBALL_FORCE = 100;
    public static final int SNOWBALL_FORCE_STEP = 10;

    // Distance for background objects when they collided
    private static int BO_COLLIDED_DISTANCE = 0x600;
    private static int BO_FALL_SPEED = 0x300;

    private static final int TTL_FALLEDSTAR = 60;
    private static final int TTL_ICECREAM = 100;

    private int i8_ScreenWidthHalf;
    protected static long[] al_maskarray;
    public static final int MAX_PLAYER_HEALTH = 100;
    private int i_GhostFreq,i_CatFreq,i_MoonFreq;

    private int i8_deadCoords = 0;

    public int i_snowballForce;
    public int i8_curInitPlayerX,i8_curInitPlayerY;
    public AnimeObject p_Moon,p_Ufo,p_Witch;
    public AnimeObject p_Player,p_Snowball;
    public AnimeObject[] ap_MoveObjects;
    private int i_snowballForceDelay;
    public int i8_offsetOfViewX;
    public int i_PlayerHealth;
    private int i_WayPointIndex;
    private int i8_NextWaypointX;
    private int i_startIndexOfChimneyArray;
    private int i_endIndexOfChimneyArray;

    //7210
//    private final static int I8_MOON_FIRE_OFFSET = 32 << 8;
//    private final static int MOON_Y_OFFSET = 0x0;
    // Other
    private final static int I8_MOON_FIRE_OFFSET = 17<<8;
    private final static int MOON_Y_OFFSET = 0x0A;

    public GameletImpl(int _screenWidth, int _screenHeight)
    {
        super(System.currentTimeMillis());
        i_ScreenWidth = _screenWidth;
        i_ScreenHeight = _screenHeight;

        al_maskarray = new long[64];

        i8_ScreenWidthHalf = (i_ScreenWidth >>> 1) << 8;

        long l_mask = 1L;
        for (int li = 1; li < al_maskarray.length; li++)
        {
            al_maskarray[li] = l_mask;
            l_mask |= l_mask << 1;
        }

        I8_INIT_MOON_X_SCR = (i_ScreenWidth - 13/*AnimeObject.ai_AnimationValues[AnimeObject.OBJECT_MOON * 9 * 7 + 0]*/ / 2 - 4) << 8;
        I8_INIT_MOON_Y_SCR = (23/*AnimeObject.ai_AnimationValues[AnimeObject.OBJECT_MOON * 9 * 7 + 1]*/ + 1) << 7;
        if (i_ScreenHeight < MAX_HEIGHT) LG_MOON_LINK2PLAYER = true;

    }

    private void generateCats()
    {
        if (getRandomInt(i_CatFreq) == (i_CatFreq >> 1))
        {
            AnimeObject p_obj = getFirstInactiveObject();

            if (p_obj == null) return;

            p_obj.initObject(AnimeObject.OBJECT_CAT);
            p_obj.initState(AnimeObject.STATE_JUMP);

            int i8_x = 0;
            while (true)
            {
                i8_x = i8_offsetOfViewX + (getRandomInt(i_ScreenWidth << 8) / p_obj.i8_Width) * p_obj.i8_Width + p_obj.i8_halfWidth;
                long l_elem = getLineElement((i8_x >> 8) / VIRTUALCELL_WIDTH);
                if (l_elem != 0) break;
            }

            p_obj.setCenterXY_8(i8_x, I8_STARTGROUND_OFFSETY + I8_BLOCKHEIGHT - 0x100);
            p_obj.i_Param0 = -1;
            p_obj.i_Param1 = -1;
        }
    }

    private void processChimney()
    {
        for (int lindx = i_startIndexOfChimneyArray; lindx <= i_endIndexOfChimneyArray;)
        {
            int i8_x = ai_ChimneyArray[lindx++];
            int i8_y = I8_STARTGROUND_OFFSETY + ai_ChimneyArray[lindx++];
            int i_freq = ai_ChimneyArray[lindx++];

            if (getRandomInt(i_freq) == (i_freq >>> 1))
            {
                // Generate a smoke cloud
                AnimeObject p_obj = getFirstInactiveObject();
                if (p_obj != null)
                {
                    p_obj.initObject(AnimeObject.OBJECT_SMOKE);
                    p_obj.setCenterXY_8(i8_x, i8_y);

                    // Initing of the horizontal speed parameter for the cloud
                    p_obj.i8_Speed = ((getRandomInt(0x200) / 5) + 3) * 5;

                    // Initing of the vertical speed parameter for the cloud
                    p_obj.i_Param1 = (getRandomInt(0x100) / 5) * 5;

                    // Init TTL of the Smoke
                    p_obj.i_Param0 = (getRandomInt(100) / 20) * 20;

                    if (getRandomInt(100) >= 80)
                    {
                        if (i8_x > p_Player.i8_centerX)
                            p_obj.i_ObjectDirection = AnimeObject.STATE_RIGHT;
                        else
                            p_obj.i_ObjectDirection = AnimeObject.STATE_LEFT;
                    }
                    else
                    {
                        if (getRandomInt(1000) >= 500)
                            p_obj.i_ObjectDirection = AnimeObject.STATE_RIGHT;
                        else
                            p_obj.i_ObjectDirection = AnimeObject.STATE_LEFT;
                    }
                }
            }
        }
    }

    private void initWaypoint(int _waypoint)
    {
        i_WayPointIndex = _waypoint;
        if (((i_WayPointIndex + 1) << 1) >= ai_InitPlayerArray.length)
            i8_NextWaypointX = 0x7FFFFFFF;
        else
            i8_NextWaypointX = ai_InitPlayerArray[(i_WayPointIndex + 1) << 1];

        int i_offset = _waypoint << 1;
        i8_curInitPlayerX = ai_InitPlayerArray[i_offset++];
        i8_curInitPlayerY = ai_InitPlayerArray[i_offset];
    }

    private void calculateChimneyArrayIndexes()
    {
        int i8_startX = i8_offsetOfViewX - (10 << 8);
        int i8_endX = i8_offsetOfViewX + (i_ScreenWidth << 8) + (10 << 8);

        int i_endIndex = -3;
        int i_startIndex = -3;
        for (int li = 0; li < ai_ChimneyArray.length; li += 3)
        {
            if (ai_ChimneyArray[li] >= i8_startX)
            {
                i_startIndex = li;
                break;
            }
        }

        if (i_startIndex < 0) i_startIndex = ai_ChimneyArray.length;

        for (int li = i_startIndex; li < ai_ChimneyArray.length; li += 3)
        {
            if (ai_ChimneyArray[li] > i8_endX)
            {
                break;
            }
            else
                i_endIndex = li;
        }

        i_startIndexOfChimneyArray = i_startIndex;
        i_endIndexOfChimneyArray = i_endIndex;
    }

    public void newGameSession(int _level)
    {
        initLevel(_level);
        initWaypoint(0);

        switch (_level)
        {
            case LEVEL_0:
                {
                    i_TimeDelay = LEVEL_TIMEDELAY_0;
                    i_Attemptions = LEVEL_ATTEMPTIONS_0;
                    i_GhostFreq = LEVEL_GHOST_FREQ_0;
                    i_CatFreq = LEVEL_CAT_FREQ_0;
                    i_MoonFreq = LEVEL_MOON_FREQ_0;
                }
                ;
                break;
            case LEVEL_1:
                {
                    i_TimeDelay = LEVEL_TIMEDELAY_1;
                    i_Attemptions = LEVEL_ATTEMPTIONS_1;
                    i_GhostFreq = LEVEL_GHOST_FREQ_1;
                    i_CatFreq = LEVEL_CAT_FREQ_1;
                    i_MoonFreq = LEVEL_MOON_FREQ_1;
                }
                ;
                break;
            case LEVEL_2:
                {
                    i_TimeDelay = LEVEL_TIMEDELAY_2;
                    i_Attemptions = LEVEL_ATTEMPTIONS_2;
                    i_GhostFreq = LEVEL_GHOST_FREQ_2;
                    i_CatFreq = LEVEL_CAT_FREQ_2;
                    i_MoonFreq = LEVEL_MOON_FREQ_2;
                }
                ;
                break;
        }

        ap_MoveObjects = new AnimeObject[MAX_MOVING_OBJECTS];
        for (int li = 0; li < MAX_MOVING_OBJECTS; li++)
        {
            ap_MoveObjects[li] = new AnimeObject();
        }

        p_Player = new AnimeObject();
        p_Ufo = new AnimeObject();
        p_Witch = new AnimeObject();
        p_Moon = new AnimeObject();
        p_Snowball = new AnimeObject();

        p_Moon.initObject(AnimeObject.OBJECT_MOON);
        p_Snowball.initObject(AnimeObject.OBJECT_SNOWBAL);
        p_Ufo.initObject(AnimeObject.OBJECT_UFO);
        p_Witch.initObject(AnimeObject.OBJECT_WITCH);

        initPlayer();
    }

    public void increaseSnowBallForce()
    {
        i_snowballForceDelay--;
        if (i_snowballForceDelay <= 0)
        {
            i_snowballForce += SNOWBALL_FORCE_STEP;
            if (i_snowballForce > MAX_SNOWBALL_FORCE) i_snowballForce = SNOWBALL_FORCE_STEP;
            i_snowballForceDelay = SNOWBALL_FORCE_DELAY;
        }
    }

    private void initPlayer()
    {
        p_Player.initObject(AnimeObject.OBJECT_PLAYER);
        p_Player.i_ObjectDirection = AnimeObject.STATE_RIGHT;

        p_Player.setCenterXY_8(i8_curInitPlayerX, i8_curInitPlayerY);
        p_Snowball.lg_Active = false;
        p_Ufo.lg_Active = false;
        p_Witch.lg_Active = false;
        p_Moon.initState(AnimeObject.STATE_APPEARANCE);
        i_snowballForce = 0;
        i_PlayerHealth = MAX_PLAYER_HEALTH;
        i_PlayerState = PLAYERSTATE_NORMAL;

        calculateChimneyArrayIndexes();
    }

    private AnimeObject getFirstInactiveObject()
    {
        for (int li = 0; li < MAX_MOVING_OBJECTS; li++)
            if (!ap_MoveObjects[li].lg_Active) return ap_MoveObjects[li];
        return null;
    }

    private void deinitAllAnimeObjects()
    {
        for (int li = 0; li < MAX_MOVING_OBJECTS; li++) ap_MoveObjects[li].lg_Active = false;
        p_Witch.lg_Active = false;
        p_Ufo.lg_Active = false;
        p_Snowball.lg_Active = false;
    }

    private void generateFallingStars()
    {
        if (getRandomInt(300) == 150)
        {
            AnimeObject p_obj = getFirstInactiveObject();
            if (p_obj == null) return;
            p_obj.initObject(AnimeObject.OBJECT_FALLINGSTAR);
            int i8_xstart = i8_offsetOfViewX;
            int i8_scrwdth = i_ScreenWidth << 8;
            i8_xstart = getRandomInt(i8_scrwdth / 5) * 5 + i8_xstart;

            if (i8_xstart > p_Player.i8_centerX)
            {
                p_obj.i_Param0 = 0 - I8_FSTAR_dX;
            }
            else
            {
                p_obj.i_Param0 = I8_FSTAR_dX;
            }

            p_obj.setCenterXY_8(i8_xstart, 0 - p_obj.i8_halfHeight);
        }
    }

    private void processMoon()
    {
        boolean lg_result = p_Moon.processAnimation();

        if (p_Snowball.lg_Active)
        {
            if (p_Snowball.checkCollide(p_Moon))
            {
                if (!(p_Moon.i_State == AnimeObject.STATE_RIGHT || p_Moon.i_State == AnimeObject.STATE_FIRE))
                {
                    p_Moon.initState(p_Moon.i_State + 1);
                }
                else
                    if (p_Moon.i_State == AnimeObject.STATE_RIGHT)
                    {
                        p_Moon.initState(AnimeObject.STATE_FIRE);
                    }
                p_Snowball.lg_Active = false;
            }
        }

        if (lg_result && p_Moon.i_State == AnimeObject.STATE_FIRE)
        {
            p_Moon.initState(AnimeObject.STATE_RIGHT);
            AnimeObject p_obj = getFirstInactiveObject();
            if (p_obj == null) return;

            p_obj.initObject(AnimeObject.OBJECT_FIRE);
            p_obj.setCenterXY_8(p_Moon.i8_ScreenX - p_obj.i8_halfWidth, p_Moon.i8_ScreenY + I8_MOON_FIRE_OFFSET);

            // Calculating of firing parameters
            p_obj.i_Param0 = p_Player.i8_centerX;
            p_obj.i_Param1 = p_Player.i8_centerY;
            p_obj.i_Param2 = (p_Player.i8_centerY - p_obj.i8_centerY) / ((p_obj.i8_centerX - p_Player.i8_centerX) / p_obj.i8_Speed);
        }
        else
            if (p_Moon.i_State == AnimeObject.STATE_RIGHT)
            {
                //The moon is very angry
                if (getRandomInt(i_MoonFreq) == (i_MoonFreq >>> 1))
                {
                    p_Moon.initState(AnimeObject.STATE_FIRE);
                }
            }

        // Recalculating of the MOON's screen coords
        int i8_scrX = convertScrXtoAbsX(I8_INIT_MOON_X_SCR);
        int i8_scrY = I8_INIT_MOON_Y_SCR;

        if (LG_MOON_LINK2PLAYER)
        {
            i8_scrY = ((p_Player.i8_centerY - p_Player.i8_halfHeight) >> 8) + MOON_Y_OFFSET;
            int yOffs = 0;

            if (i_ScreenHeight > MAX_HEIGHT)
                yOffs = (i_ScreenHeight - MAX_HEIGHT) >> 1;
            else
                if (i8_scrY <= ((i_ScreenHeight - p_Player.i_Height) >> 1))
                    yOffs = 0;
                else
                    if (i8_scrY >= (MAX_HEIGHT - ((i_ScreenHeight + p_Player.i_Height) >> 1)))
                        yOffs = i_ScreenHeight - MAX_HEIGHT;
                    else
                        yOffs = ((i_ScreenHeight - p_Player.i_Height) >> 1) - i8_scrY;
            i8_scrY = (Math.abs(yOffs) << 8) + I8_INIT_MOON_Y_SCR;

        }

        p_Moon.setCenterXY_8(i8_scrX, i8_scrY);
    }

    private AnimeObject processAnimationObjects()
    {
        AnimeObject p_collided = null;

        for (int li = 0; li < MAX_MOVING_OBJECTS; li++)
        {
            AnimeObject p_obj = ap_MoveObjects[li];
            if (!p_obj.lg_Active) continue;

            int i8_speed = p_obj.i8_Speed;
            int i8_centerX = p_obj.i8_centerX;
            int i8_centerY = p_obj.i8_centerY;
            int i8_height = p_obj.i8_Height;
            //int i8_screenX = p_obj.i8_ScreenX;
            int i8_screenY = p_obj.i8_centerY - p_obj.i8_halfHeight;

            int i_type = p_obj.i_Type;
            int i_state = p_obj.i_State;

            boolean lg_result = p_obj.processAnimation();

            boolean lg_snowballcollided = false;

            if (p_Snowball.lg_Active)
            {
                if (p_obj.checkCollide(p_Snowball)) lg_snowballcollided = true;
            }

            switch (i_type)
            {
                case AnimeObject.OBJECT_CAT:
                    {
                        if (lg_snowballcollided && i_state != AnimeObject.STATE_DEATH)
                        {
                            i_PlayerScore += 2;
                            p_obj.initState(AnimeObject.STATE_DEATH);
                            p_Snowball.lg_Active = false;
                            continue;
                        }

                        switch (i_state)
                        {
                            case AnimeObject.STATE_JUMP:
                                {
                                    i8_centerY -= i8_speed;

                                    int i_startIndx = ((i8_centerX / VIRTUALCELL_WIDTH) >>> 8) - 1;
                                    int i_yoffst = i8_screenY + i8_height;

                                    int i_maskoffset = (0x4000 - i_yoffst + I8_STARTGROUND_OFFSETY - 0x100) >> 8;
                                    long l_mask = 0;
                                    if (i_maskoffset < 0)
                                        l_mask = p_obj.l_mask >>> (0 - i_maskoffset);
                                    else
                                        l_mask = p_obj.l_mask << i_maskoffset;
                                    long l_elem = getLineElement(i_startIndx);

                                    if ((l_elem & l_mask) == 0)
                                    {
                                        if (getRandomInt(100) > 50)
                                        {
                                            p_obj.initState(AnimeObject.STATE_LEFT);
                                        }
                                        else
                                        {
                                            p_obj.initState(AnimeObject.STATE_RIGHT);
                                        }
                                    }
                                    else
                                    {
                                        p_obj.setCenterXY_8(i8_centerX, i8_centerY);
                                    }
                                }
                                ;
                                break;
                            case AnimeObject.STATE_FIRE:
                                {
                                    if (lg_result)
                                        p_obj.initState(p_obj.i_ObjectDirection);
                                }
                                ;
                                break;
                            case AnimeObject.STATE_COWER:
                            case AnimeObject.STATE_DEATH:
                                {
                                    i8_centerY += i8_speed;
                                    p_obj.setCenterXY_8(i8_centerX, i8_centerY);
                                }
                                ;
                                break;
                            default :
                                {
                                    boolean lg_changed = false;

                                    if (!checkGroundForObject(p_obj, true, 1))
                                    {
                                        i8_centerY += 0x100;
                                    }
                                    else
                                    {
                                        boolean lg_PlayerCollided = p_Player.checkCollide(p_obj);

                                        if (p_obj.i_ObjectDirection == AnimeObject.STATE_LEFT)
                                        {
                                            if (lg_PlayerCollided && i8_centerX > p_Player.i8_centerX)
                                            {
                                                p_obj.initState(AnimeObject.STATE_FIRE);
                                                i_PlayerHealth -= p_obj.i_Energy;
                                            }
                                            else
                                                if (checkLeftSide(p_obj, true, 0))
                                                {
                                                    i8_centerX -= i8_speed;
                                                    p_obj.i_Param1++;
                                                }
                                                else
                                                {
                                                    if (p_obj.i_Param0 < 0 || p_obj.i_Param1 < 0)
                                                    {
                                                        p_obj.initState(AnimeObject.STATE_RIGHT);
                                                        p_obj.i_Param0 = 0;
                                                    }
                                                    else
                                                    {
                                                        if (p_obj.i_Param0 < 2 && p_obj.i_Param1 < 2)
                                                            p_obj.initState(AnimeObject.STATE_COWER);
                                                        else
                                                        {
                                                            p_obj.initState(AnimeObject.STATE_RIGHT);
                                                            p_obj.i_Param0 = 0;
                                                        }
                                                    }
                                                    lg_changed = true;
                                                }
                                        }
                                        else
                                        {
                                            if (lg_PlayerCollided && i8_centerX <= p_Player.i8_centerX)
                                            {
                                                p_obj.initState(AnimeObject.STATE_FIRE);
                                                i_PlayerHealth -= p_obj.i_Energy;
                                            }
                                            else
                                                if (checkRightSide(p_obj, true, 0))
                                                {
                                                    i8_centerX += i8_speed;
                                                    p_obj.i_Param0++;
                                                }
                                                else
                                                {
                                                    if (p_obj.i_Param0 < 0 || p_obj.i_Param1 < 0)
                                                    {
                                                        p_obj.initState(AnimeObject.STATE_LEFT);
                                                        p_obj.i_Param1 = 0;
                                                    }
                                                    else
                                                    {
                                                        if (p_obj.i_Param0 < 2 && p_obj.i_Param1 < 2)
                                                            p_obj.initState(AnimeObject.STATE_COWER);
                                                        else
                                                        {
                                                            p_obj.initState(AnimeObject.STATE_LEFT);
                                                            p_obj.i_Param1 = 0;
                                                        }
                                                    }

                                                    lg_changed = true;
                                                }
                                        }
                                    }
                                    p_obj.setCenterXY_8(i8_centerX, i8_centerY);

                                    if (getRandomInt(300) == 150)
                                    {
                                        p_obj.initState(AnimeObject.STATE_COWER);
                                    }
                                    else
                                        if (!lg_changed)
                                        {
                                            if (getRandomInt(300) == 150)
                                            {
                                                if (p_obj.i_ObjectDirection == AnimeObject.STATE_LEFT)
                                                    p_obj.initState(AnimeObject.STATE_RIGHT);
                                                else
                                                    p_obj.initState(AnimeObject.STATE_LEFT);
                                            }
                                        }
                                }
                        }
                    }
                    ;
                    break;
                case AnimeObject.OBJECT_FALLINGSTAR:
                    {
                        p_obj.i8_centerX = i8_centerX + p_obj.i_Param0;
                        p_obj.i8_centerY = i8_centerY + I8_FSTAR_dY;
                        p_obj.updateXY();

                        if (checkGroundForObject(p_obj, true, 2))
                        {
                            if (getRandomInt(200) > 180)
                            {
                                p_obj.initObject(AnimeObject.OBJECT_ICECREAM);
                                p_obj.i_Param0 = TTL_ICECREAM;
                            }
                            else
                            {
                                p_obj.initObject(AnimeObject.OBJECT_FALLEDSTAR);
                                p_obj.i_Param0 = TTL_FALLEDSTAR;
                            }
                        }
                    }
                    ;
                    break;
                case AnimeObject.OBJECT_FIRE:
                    {
                        if (i_state == AnimeObject.STATE_APPEARANCE)
                        {
                            p_obj.i8_centerX -= i8_speed;
                            p_obj.i8_centerY = i8_centerY + p_obj.i_Param2;
                            p_obj.updateXY();
                            if (checkGroundForObject(p_obj, false, 0))
                            {
                                p_obj.initState(AnimeObject.STATE_FIRE);
                            }
                        }
                        else
                        {
                            if (lg_result)
                            {
                                p_obj.lg_Active = false;
                                continue;
                            }
                        }
                    }
                    ;
                    break;
                case AnimeObject.OBJECT_GHOST:
                    {
                        if (lg_snowballcollided && i_state != AnimeObject.STATE_DEATH)
                        {
                            i_PlayerScore += 3;
                            p_obj.initState(AnimeObject.STATE_DEATH);
                            p_Snowball.lg_Active = false;
                            continue;
                        }
                        else
                            if (i_state == AnimeObject.STATE_APPEARANCE && lg_result)
                            {
                                p_obj.initState(p_obj.i_ObjectDirection);
                                if (p_obj.i_Param0 == 2)
                                    p_obj.i8_Speed = 0x150;
                                else
                                    p_obj.i8_Speed = p_obj.i_Param2;
                            }
                            else
                                if (i_state == AnimeObject.STATE_DEATH && lg_result)
                                {
                                    p_obj.lg_Active = false;
                                    continue;
                                }
                                else
                                    if (i_state != AnimeObject.STATE_DEATH)
                                    {
                                        if (getRandomInt(1000) == 500)
                                        {
                                            p_obj.initState(AnimeObject.STATE_DEATH);
                                        }
                                        else
                                        {
                                            int i8_topBorder = i8_height;
                                            int i8_bottomBorder = I8_STARTGROUND_OFFSETY + I8_BLOCKHEIGHT - p_obj.i8_Height;

                                            switch (p_obj.i_Param0)
                                            {
                                                case 0:
                                                    {
                                                        // Linear horiz
                                                        if (p_obj.i_ObjectDirection == AnimeObject.STATE_LEFT)
                                                            i8_centerX -= i8_speed;
                                                        else
                                                            i8_centerX += i8_speed;
                                                    }
                                                    ;
                                                    break;
                                                case 1:
                                                    {
                                                        i8_centerY = p_obj.i_Param1;
                                                        // Vertical moving
                                                        if (p_obj.i_ObjectDirection == AnimeObject.STATE_LEFT)
                                                        {
                                                            // Moving up
                                                            i8_centerY -= i8_speed;
                                                            if (i8_centerY < i8_topBorder) p_obj.i_ObjectDirection = AnimeObject.STATE_RIGHT;
                                                        }
                                                        else
                                                        {
                                                            // Moving down
                                                            i8_centerY += i8_speed;
                                                            if (i8_centerY > i8_bottomBorder) p_obj.i_ObjectDirection = AnimeObject.STATE_LEFT;
                                                        }
                                                        p_obj.i_Param1 = i8_centerY;
                                                    }
                                                    ;
                                                    break;
                                                case 2:
                                                    {
                                                        // Chaotic
                                                        i8_centerY = p_obj.i_Param1;
                                                        // Vertical moving
                                                        if (p_obj.i_Param2 == AnimeObject.STATE_LEFT)
                                                        {
                                                            // Moving up
                                                            i8_centerY -= i8_speed;
                                                            if (i8_centerY < i8_topBorder) p_obj.i_ObjectDirection = AnimeObject.STATE_RIGHT;
                                                        }
                                                        else
                                                        {
                                                            // Moving down
                                                            i8_centerY += i8_speed;
                                                            if (i8_centerY > i8_bottomBorder) p_obj.i_ObjectDirection = AnimeObject.STATE_LEFT;
                                                        }
                                                        p_obj.i_Param1 = i8_centerY;

                                                        if (p_obj.i_ObjectDirection == AnimeObject.STATE_LEFT)
                                                        // Moving left
                                                            i8_centerX -= i8_speed;
                                                        else
                                                        // Moving right
                                                            i8_centerX += i8_speed;

                                                        if (i8_centerY < i8_topBorder) p_obj.lg_Active = false;
                                                        if (i8_centerY > i8_bottomBorder) p_obj.lg_Active = false;

                                                        if (getRandomInt(100) == 50)
                                                        {
                                                            if (p_obj.i_ObjectDirection == AnimeObject.STATE_LEFT)
                                                                p_obj.i_ObjectDirection = AnimeObject.STATE_RIGHT;
                                                            else
                                                                p_obj.i_ObjectDirection = AnimeObject.STATE_LEFT;
                                                        }

                                                        if (getRandomInt(100) == 50)
                                                        {
                                                            if (p_obj.i_Param2 == AnimeObject.STATE_LEFT)
                                                                p_obj.i_Param2 = AnimeObject.STATE_RIGHT;
                                                            else
                                                                p_obj.i_Param2 = AnimeObject.STATE_LEFT;
                                                        }
                                                    }
                                                    ;
                                                    break;
                                            }

                                            i8_centerY = p_obj.i_Param1 + (p_obj.i_Frame << 8);
                                            p_obj.setCenterXY_8(i8_centerX, i8_centerY);
                                        }
                                    }
                    }
                    ;
                    break;
                case AnimeObject.OBJECT_SMOKE:
                    {
                        if (i_state == AnimeObject.STATE_DEATH && lg_result)
                        {
                            p_obj.lg_Active = false;
                            continue;
                        }
                        else
                            if (i_state == AnimeObject.STATE_APPEARANCE && lg_result)
                            {
                                p_obj.initState(p_obj.i_ObjectDirection);
                                p_obj.i8_Speed = i8_speed;
                            }
                            else
                                if (lg_snowballcollided)
                                {
                                    p_obj.initState(AnimeObject.STATE_DEATH);
                                    p_Snowball.lg_Active = false;
                                }
                                else
                                    if (--p_obj.i_Param0 <= 0 && lg_result)
                                    {
                                        p_obj.initState(AnimeObject.STATE_DEATH);
                                    }
                                    else
                                    {
                                        if (p_obj.i_ObjectDirection == AnimeObject.STATE_LEFT)
                                        {
                                            // Moving to left
                                            i8_centerX -= i8_speed;
                                        }
                                        else
                                        {
                                            // Moving to right
                                            i8_centerX += i8_speed;
                                        }

                                        if (getRandomInt(50) == 25)
                                        {
                                            // Changing of the vertical direct
                                            p_obj.i_Param1 = 0 - p_obj.i_Param1;
                                        }

                                        i8_centerY += p_obj.i_Param1;
                                        p_obj.setCenterXY_8(i8_centerX, i8_centerY);
                                    }
                    }
                    ;
                    break;
                case AnimeObject.OBJECT_ICECREAM:
                case AnimeObject.OBJECT_FALLEDSTAR:
                    {
                        if (i_state != AnimeObject.STATE_DEATH)
                        {
                            if (--p_obj.i_Param0 <= 0)
                            {
                                p_obj.lg_Active = false;
                                //p_obj.initState(AnimeObject.STATE_DEATH);
                            }
                        }
                        else
                        {
                            if (lg_result) p_obj.lg_Active = false;
                        }
                    }
                    ;
                    break;
            }

            if (p_obj.i8_centerX < I8_LEFT_BORDER_X || p_obj.i8_centerX > I8_MAINPIPE_CENTER_X)
            {
                p_obj.lg_Active = false;
            }
            else
                if (p_obj.i8_ScreenY > I8_BLOCKHEIGHT + I8_STARTGROUND_OFFSETY || (Math.abs(p_obj.i8_centerX - p_Player.i8_centerX) >= (i8_ScreenWidthHalf << 1)))
                {
                    p_obj.lg_Active = false;
                }
                else
                {
                    if (p_Player.checkCollide(p_obj)) p_collided = p_obj;
                }
        }

        if (i_PlayerHealth < 0) i_PlayerHealth = 0;

        return p_collided;
    }

    public void resumeGameAfterPlayerLost()
    {
        i_PlayerState = PLAYERSTATE_NORMAL;
        i_GameState = GAMESTATE_PLAYED;
        initPlayer();
        deinitAllAnimeObjects();
    }

    private boolean checkGroundForObject(AnimeObject _object, boolean _align, int _alignlimit)
    {
        int i_yoffst = _object.i8_ScreenY + _object.i8_Height;
        if (i_yoffst <= I8_STARTGROUND_OFFSETY) return false;

        int i_objSX = _object.i8_ScreenX;
        int i_startIndx = ((i_objSX + _object.i8_leftOffset) / VIRTUALCELL_WIDTH) >> 8;
        int i_endIndex = (((i_objSX + _object.i8_Width - _object.i8_rightOffset) / VIRTUALCELL_WIDTH) >> 8) - 1;

        int i_maskoffset = (0x4000 - i_yoffst + I8_STARTGROUND_OFFSETY) >> 8;
        long l_mask = _object.l_mask << (i_maskoffset - 1);

        boolean lg_grounded = false;
        long l_maxmask = 0;
        for (int li = i_startIndx; li < i_endIndex; li++)
        {
            long l_elem = getLineElement(li);
            if ((l_mask & l_elem) == 0) continue;
            if (l_maxmask < l_elem) l_maxmask = l_elem;
            lg_grounded = true;
        }

        if (lg_grounded && l_maxmask != 0 && _align)
        {
            l_mask = _object.l_mask << i_maskoffset;
            l_maxmask = l_maxmask & l_mask;

            // Check for aligning
            if (l_mask != 0)
            {
                // aligning is needed :(
                l_mask = 0x8000000000000000L;
                int i_num = 0;
                for (int li = 0; li < 64; li++)
                {
                    if ((l_maxmask & l_mask) != 0)
                    {
                        i_num++;
                    }
                    l_mask >>>= 1;
                }

                if (_alignlimit < i_num) return true;

                _object.i8_centerY -= (i_num << 8);
                _object.updateXY();
            }
        }
        return lg_grounded;
    }

    private boolean checkLeftSide(AnimeObject _object, boolean _changed, int _offset)
    {
        int i8_ScrX = _object.i8_ScreenX + _object.i8_leftOffset;
        if (i8_ScrX <= I8_LEFT_BORDER_X) return false;

        int i_startIndx = ((i8_ScrX / VIRTUALCELL_WIDTH) >> 8);
        int i_yoffst = _object.i8_ScreenY + _object.i8_Height;
        boolean lg_moveToLeft = true;

        if (i_yoffst > I8_STARTGROUND_OFFSETY)
        {
            int i_maskoffset = (0x4000 - i_yoffst + I8_STARTGROUND_OFFSETY) >> 8;
            long l_mask = _object.l_mask << (i_maskoffset - 1 + _offset);

            long l_elemLeft = getLineElement(i_startIndx);

            if (_changed)
            {
                l_mask = -1L << i_maskoffset;
                lg_moveToLeft = (l_elemLeft & l_mask) == 0 ? true : false;
                if (lg_moveToLeft)
                {
                    l_mask ^= -1L;
                    if ((l_elemLeft & l_mask) == l_mask) return true; else return false;
                }
            }
            else
                lg_moveToLeft = (l_elemLeft & l_mask) == 0 ? true : false;
        }
        return lg_moveToLeft;
    }

    private boolean checkRightSide(AnimeObject _object, boolean _changed, int _offset)
    {
        int i8_RightScrX = _object.i8_ScreenX + _object.i8_Width - _object.i8_rightOffset;
        if (i8_RightScrX >= I8_MAINPIPE_CENTER_X) return false;
        int i_endIndex = ((i8_RightScrX / VIRTUALCELL_WIDTH) >> 8) - 1;
        int i_yoffst = _object.i8_ScreenY + _object.i8_Height;
        boolean lg_moveToRight = true;

        if (i_yoffst > I8_STARTGROUND_OFFSETY)
        {
            int i_maskoffset = (0x4000 - i_yoffst + I8_STARTGROUND_OFFSETY) >> 8;
            long l_mask = _object.l_mask << (i_maskoffset - 1 + _offset);

            long l_elemRight = getLineElement(i_endIndex);
            if (_changed)
            {
                l_mask = -1L << i_maskoffset;
                lg_moveToRight = (l_elemRight & l_mask) == 0 ? true : false;
                if (lg_moveToRight)
                {
                    l_mask ^= -1L;
                    if ((l_elemRight & l_mask) == l_mask) return true; else return false;
                }
            }
            else
                lg_moveToRight = (l_elemRight & l_mask) == 0 ? true : false;
        }
        return lg_moveToRight;
    }

    // Return true if the player is lost
    private boolean processPlayer(AnimeObject _collidedobject, int _button)
    {
        boolean lg_playerResult = p_Player.processAnimation();
        if (p_Player.i_State == AnimeObject.STATE_JUMP && lg_playerResult)
        {
            i_snowballForce = 0;
            if (p_Player.i_ObjectDirection == AnimeObject.STATE_LEFT)
            {
                int i8_angle = p_Player.i_Param2;
                int i8_cx = p_Player.i_Param0;
                int i8_cy = p_Player.i_Param1;

                i8_angle += I8_JUMP_ANGLE_SPEED;

                if (i8_angle >= 0x2000)
                {
                    p_Player.initState(p_Player.i_ObjectDirection);
                }
                else
                {
                    int i_delta = (i8_angle >>> 8) + 32;
                    i_delta &= 63;
                    i8_cx = i8_cx - xCoSine(I8_JUMP_LEN, i_delta);
                    i8_cy = i8_cy + xSine(I8_JUMP_LEN, i_delta);

                    int i8_oldCx = p_Player.i8_centerX;
                    int i8_oldCy = p_Player.i8_centerY;

                    p_Player.i8_centerX = i8_cx;
                    p_Player.i8_centerY = i8_cy;
                    p_Player.updateXY();

                    if (p_Player.i8_centerX <= I8_LEFT_BORDER_X) p_Player.initState(p_Player.i_ObjectDirection);

                    if (i8_angle > 0x0F)
                        if (checkGroundForObject(p_Player, false, 0))
                        {
                            p_Player.i8_centerX = i8_oldCx;
                            p_Player.i8_centerY = i8_oldCy;
                            p_Player.updateXY();
                            p_Player.initState(p_Player.i_ObjectDirection);
                        }

                    p_Player.i_Param2 = i8_angle;
                }
            }
            else
            {
                int i8_angle = p_Player.i_Param2;
                int i8_cx = p_Player.i_Param0;
                int i8_cy = p_Player.i_Param1;

                i8_angle += I8_JUMP_ANGLE_SPEED;

                if (i8_angle >= 0x2000)
                {
                    p_Player.initState(p_Player.i_ObjectDirection);
                }
                else
                {
                    int i_delta = (i8_angle >>> 8) + 32;
                    i_delta &= 63;
                    i8_cx = i8_cx + xCoSine(I8_JUMP_LEN, i_delta);
                    i8_cy = i8_cy + xSine(I8_JUMP_LEN, i_delta);

                    int i8_oldCx = p_Player.i8_centerX;
                    int i8_oldCy = p_Player.i8_centerY;

                    p_Player.i8_centerX = i8_cx;
                    p_Player.i8_centerY = i8_cy;
                    p_Player.updateXY();

                    if ((p_Player.i8_centerX + p_Player.i8_halfWidth + 0x100) >= I8_MAINPIPE_CENTER_X) p_Player.initState(p_Player.i_ObjectDirection);

                    if (i8_angle > 0x0F)
                        if (checkGroundForObject(p_Player, false, 0))
                        {
                            p_Player.i8_centerX = i8_oldCx;
                            p_Player.i8_centerY = i8_oldCy;
                            p_Player.updateXY();
                            p_Player.initState(p_Player.i_ObjectDirection);
                        }

                    p_Player.i_Param2 = i8_angle;
                }
            }
        }
        else
        {
            if (p_Player.i_State == AnimeObject.STATE_DEATH && lg_playerResult)
            {
                if (lg_playerResult)
                {
                    i_PlayerState = PLAYERSTATE_LOST;
                    i8_deadCoords = p_Player.i8_centerX;
                    i_Attemptions--;
                    if (i_Attemptions <= 0) i_GameState = GAMESTATE_OVER;
                }
                return true;
            }

            boolean lg_IsGround = checkGroundForObject(p_Player, true, PLAYER_ALIGN_LIMIT);

            // Check existing of the ground under the player
            if (!lg_IsGround)
            {
                p_Player.i8_centerY = p_Player.i8_centerY + I8_FALLSPEED;
                p_Player.updateXY();

                if (p_Player.i8_ScreenY >= (I8_STARTGROUND_OFFSETY + (64 << 8)) && p_Player.i_State != AnimeObject.STATE_DEATH)
                {
                    p_Player.initState(AnimeObject.STATE_DEATH);
                    return false;
                }
                else
                    if (p_Player.i_State != AnimeObject.STATE_DEATH) p_Player.initState(p_Player.i_ObjectDirection);
            }
            else
            {
                if (p_Snowball.lg_Active)
                {
                    p_Player.initState(p_Player.i_ObjectDirection);
                    i_snowballForce = 0;
                }
                else
                {
                    boolean lg_moveToLeft = checkLeftSide(p_Player, false, PLAYER_ALIGN_LIMIT);
                    boolean lg_moveToRight = checkRightSide(p_Player, false, PLAYER_ALIGN_LIMIT);

                    switch (p_Player.i_State)
                    {
                        case AnimeObject.STATE_APPEARANCE:
                            {
                                if (lg_playerResult)
                                {
                                    p_Player.initState(AnimeObject.STATE_RIGHT);
                                }
                            }
                            ;
                            break;
                        case AnimeObject.STATE_COWER:
                            {
                                if (lg_playerResult)
                                {
                                    p_Player.initState(p_Player.i_ObjectDirection);
                                }
                            }
                            ;
                            break;
                        case AnimeObject.STATE_JUMP:
                            {
                                i_snowballForce = 0;
                            }
                            ;
                            break;
                        case AnimeObject.STATE_FIRE:
                            {
                                if (lg_playerResult)
                                {
                                    p_Player.initState(p_Player.i_ObjectDirection);
                                    activateSnowBall();
                                    i_snowballForce = 0;
                                }
                            }
                            ;
                            break;
                        case AnimeObject.STATE_LEFT:
                        case AnimeObject.STATE_RIGHT:
                            {
                                switch (_button)
                                {
                                    case BUTTON_NONE:
                                        {
                                            p_Player.initState(p_Player.i_ObjectDirection);
                                        }
                                        ;
                                        break;
                                    case BUTTON_LEFT:
                                        {
                                            if (lg_moveToLeft)
                                            {
                                                p_Player.i8_centerX -= I8_HORIZ_PLAYER_SPEED;
                                                p_Player.updateXY();
                                            }
                                            else
                                            {
                                                p_Player.initState(AnimeObject.STATE_LEFT);
                                            }
                                            p_Player.i_ObjectDirection = AnimeObject.STATE_LEFT;
                                        }
                                        ;
                                        break;
                                    case BUTTON_RIGHT:
                                        {
                                            if (lg_moveToRight)
                                            {
                                                p_Player.i8_centerX += I8_HORIZ_PLAYER_SPEED;
                                                p_Player.updateXY();
                                            }
                                            else
                                            {
                                                p_Player.initState(AnimeObject.STATE_RIGHT);
                                            }
                                            p_Player.i_ObjectDirection = AnimeObject.STATE_RIGHT;
                                        }
                                        ;
                                        break;
                                    case BUTTON_FIRE:
                                        {
                                            if (!p_Snowball.lg_Active)
                                            {
                                                p_Player.initState(AnimeObject.STATE_FIRE);
                                            }
                                        }
                                        ;
                                        break;
                                    case BUTTON_COWER:
                                        {
                                            p_Player.initState(AnimeObject.STATE_COWER);
                                        }
                                        ;
                                        break;
                                    case BUTTON_JUMPUPDIR:
                                        {

                                            if (p_Player.i_ObjectDirection == AnimeObject.STATE_RIGHT)
                                            {
                                                if (lg_moveToRight)
                                                {
                                                    p_Player.i_Param0 = p_Player.i8_centerX + I8_JUMP_LEN;
                                                    p_Player.initState(AnimeObject.STATE_JUMP);

                                                }
                                                else
                                                {
                                                    p_Player.initState(AnimeObject.STATE_RIGHT);
                                                }
                                            }
                                            else
                                            {
                                                if (lg_moveToLeft)
                                                {
                                                    p_Player.i_Param0 = p_Player.i8_centerX - I8_JUMP_LEN;
                                                    p_Player.initState(AnimeObject.STATE_JUMP);
                                                }
                                                else
                                                {
                                                    p_Player.initState(AnimeObject.STATE_LEFT);
                                                }
                                            }
                                            p_Player.i_Param2 = 0x0;

                                            p_Player.i_Param1 = p_Player.i8_centerY;
                                        }
                                        ;
                                        break;
                                }
                            }
                    }
                }
            }
        }

        if (_collidedobject != null && _collidedobject.i_State != AnimeObject.STATE_DEATH)
        {
            int i_power = _collidedobject.i_Energy;
            int i_playerState = p_Player.i_State;
            switch (_collidedobject.i_Type)
            {
                case AnimeObject.OBJECT_FALLEDSTAR:
                    {
                        _collidedobject.lg_Active = false;
                        i_PlayerScore += i_power;
                    }
                    ;
                    break;
                case AnimeObject.OBJECT_FIRE:
                    {
                        if (i_playerState != AnimeObject.STATE_COWER)
                        {
                            i_PlayerHealth -= i_power;
                        }
                        _collidedobject.lg_Active = false;
                    }
                    ;
                    break;
                case AnimeObject.OBJECT_GHOST:
                    {
                        if (i_playerState != AnimeObject.STATE_COWER)
                        {
                            i_PlayerHealth -= i_power;
                            _collidedobject.initState(AnimeObject.STATE_DEATH);
                        }
                    }
                    ;
                    break;
                case AnimeObject.OBJECT_ICECREAM:
                    {
                        _collidedobject.lg_Active = false;
                        i_PlayerHealth += i_power;
                        if (i_PlayerHealth > MAX_PLAYER_HEALTH) i_PlayerHealth = MAX_PLAYER_HEALTH;
                    }
                    ;
                    break;
                case AnimeObject.OBJECT_SMOKE:
                    {
                        i_PlayerHealth -= i_power;
                    }
                    ;
                    break;
            }
        }

        if (i_PlayerHealth <= 0 && p_Player.i_State != AnimeObject.STATE_DEATH)
        {
            i_PlayerHealth = 0;
            p_Player.initState(AnimeObject.STATE_DEATH);
        }
        else
        {
            if (i_PlayerHealth < 0) i_PlayerHealth = 0;
        }

        if (p_Player.i8_ScreenY >= I8_BLOCKHEIGHT + I8_STARTGROUND_OFFSETY) return true;

        return false;
    }

    public void endGameSession()
    {
        ap_MoveObjects = null;
        p_Player = null;
        p_Ufo = null;
        p_Witch = null;
	Runtime.getRuntime().gc();
    }

    public int convertAbsXtoScrX(int _i8_absx)
    {
        return (_i8_absx - i8_offsetOfViewX) >>> 8;
    }

    public int convertScrXtoAbsX(int _i8_scrx)
    {
        return i8_offsetOfViewX + _i8_scrx;
    }
/*
    private void processBackgroundObjects()
    {
        if (!p_Ufo.lg_Active)
        {
            // Generating of the UFO
            if (getRandomInt(300) == 150)
            {
                int i_x;
                if (getRandomInt(200) >= 100)
                {
                    p_Ufo.initState(AnimeObject.STATE_LEFT);
                    i_x = i_ScreenWidth + p_Ufo.i_Width;
                }
                else
                {
                    p_Ufo.initState(AnimeObject.STATE_RIGHT);
                    i_x = 0 - p_Ufo.i_Width;
                }

                int i_y = getRandomInt((I_DOWNBACKGROUNDBORDER - I_UPBACKGROUNDBORDER) / I_BACKGROUNDSTEPGENERATION) * I_BACKGROUNDSTEPGENERATION;

                p_Ufo.setCenterXY(i_x, i_y);
                p_Ufo.lg_Active = true;
            }
        }

        if (!p_Witch.lg_Active)
        {
            // Generating of the witch
            if (getRandomInt(200) == 150)
            {
                int i_x;
                if (getRandomInt(200) >= 100)
                {
                    p_Witch.initState(AnimeObject.STATE_LEFT);
                    i_x = i_ScreenWidth + p_Witch.i_Width;
                }
                else
                {
                    p_Witch.initState(AnimeObject.STATE_RIGHT);
                    i_x = 0 - p_Witch.i_Width;
                }

                int i_y = getRandomInt((I_DOWNBACKGROUNDBORDER - I_UPBACKGROUNDBORDER) / I_BACKGROUNDSTEPGENERATION) * I_BACKGROUNDSTEPGENERATION;

                p_Witch.setCenterXY(i_x, i_y);
                p_Witch.lg_Active = true;
            }
        }

        if (p_Ufo.lg_Active)
        {
            p_Ufo.processAnimation();
            int i8_speed;
            if (p_Ufo.i_State == AnimeObject.STATE_DEATH)
            {
                if (p_Ufo.i_Param0 > 0)
                {
                    p_Ufo.i8_centerX -= p_Ufo.i_Param0;
                    p_Ufo.i_Param0 -= (BO_FALL_SPEED >> 1);
                }
                p_Ufo.i8_centerY += BO_FALL_SPEED;
                p_Ufo.updateXY();

                if (p_Ufo.i8_ScreenY >= I8_STARTGROUND_OFFSETY + I8_BLOCKHEIGHT) p_Ufo.lg_Active = false;
            }
            else
            {
                if (p_Ufo.i_State == AnimeObject.STATE_LEFT)
                    i8_speed = 0 - p_Ufo.i8_Speed;
                else
                    i8_speed = p_Ufo.i8_Speed;
                p_Ufo.i8_centerX += i8_speed;
                int i_cx = (p_Ufo.i8_centerX >> 8);
                p_Ufo.updateXY();

                if (p_Ufo.i_State == AnimeObject.STATE_LEFT)
                {
                    if (i_cx < (0 - p_Ufo.i_Width)) p_Ufo.lg_Active = false;
                }
                else
                {
                    if (i_cx > (i_ScreenWidth + p_Ufo.i_Width)) p_Ufo.lg_Active = false;
                }
            }
        }

        if (p_Witch.lg_Active)
        {
            p_Witch.processAnimation();
            int i8_speed;
            if (p_Witch.i_State == AnimeObject.STATE_DEATH)
            {
                if (p_Witch.i_Param0 > 0)
                {
                    p_Witch.i8_centerX += p_Ufo.i_Param0;
                    p_Witch.i_Param0 -= (BO_FALL_SPEED >> 1);
                }
                p_Witch.i8_centerY += BO_FALL_SPEED;
                p_Witch.updateXY();

                if (p_Witch.i8_ScreenY >= I8_STARTGROUND_OFFSETY + I8_BLOCKHEIGHT) p_Witch.lg_Active = false;
            }
            else
            {
                if (p_Witch.i_State == AnimeObject.STATE_LEFT)
                    i8_speed = 0 - p_Witch.i8_Speed;
                else
                    i8_speed = p_Witch.i8_Speed;
                p_Witch.i8_centerX += i8_speed;
                int i_cx = (p_Witch.i8_centerX >> 8);
                p_Witch.updateXY();

                if (p_Witch.i_State == AnimeObject.STATE_LEFT)
                {
                    if (i_cx < (0 - p_Witch.i_Width)) p_Witch.lg_Active = false;
                }
                else
                {
                    if (i_cx > (i_ScreenWidth + p_Witch.i_Width)) p_Witch.lg_Active = false;
                }
            }
        }

        if (p_Ufo.lg_Active && p_Witch.lg_Active && !(p_Ufo.i_State == AnimeObject.STATE_DEATH || p_Witch.i_State == AnimeObject.STATE_DEATH))
        {
            if (p_Ufo.checkCollide(p_Witch))
            {
                switch (getRandomInt(40) / 10)
                {
                    case 0:
                        {
                            // UFO is won
                            p_Witch.initState(AnimeObject.STATE_DEATH);
                            p_Witch.i_Param0 = BO_COLLIDED_DISTANCE;
                        }
                        ;
                        break;
                    case 1:
                        {
                            // WITCH is won
                            p_Ufo.initState(AnimeObject.STATE_DEATH);
                            p_Ufo.i_Param0 = BO_COLLIDED_DISTANCE;
                        }
                        ;
                        break;
                    case 2:
                        {
                            // UFO and WITCH are lost
                            p_Witch.initState(AnimeObject.STATE_DEATH);
                            p_Ufo.initState(AnimeObject.STATE_DEATH);
                            p_Ufo.i_Param0 = BO_COLLIDED_DISTANCE;
                            p_Witch.i_Param0 = BO_COLLIDED_DISTANCE;
                        }
                        ;
                        break;
                }
            }
        }
    }
*/
    private void processSnowball()
    {
        if (p_Snowball.lg_Active)
        {
            boolean lg_state = p_Snowball.processAnimation();
            if (p_Snowball.i_State == AnimeObject.STATE_APPEARANCE)
            {
                int i8_centerX = p_Snowball.i8_centerX;
                int i8_centerY = p_Snowball.i8_centerY;
                int i8_speed;

                boolean lg_grounded = checkGroundForObject(p_Snowball, false, 0);
                if (lg_grounded)
                {
                    p_Snowball.setCenterXY_8(i8_centerX, i8_centerY);
                    p_Snowball.initState(AnimeObject.STATE_LEFT);
                }

                if (p_Snowball.i_ObjectDirection == AnimeObject.STATE_LEFT)
                    i8_speed = 0 - p_Snowball.i8_Speed;
                else
                    i8_speed = p_Snowball.i8_Speed;

                i8_centerX += i8_speed;

                i8_centerY += p_Snowball.i_Param0;

                p_Snowball.i_Param0 += I8_SNOWBALL_VERTSPEED_DELTA;

                if (i8_centerX < (i8_offsetOfViewX - (10 << 8)) || i8_centerX > (i8_offsetOfViewX + ((i_ScreenWidth + 10) << 8)))
                    p_Snowball.lg_Active = false;
                else
                {
                    p_Snowball.setCenterXY_8(i8_centerX, i8_centerY);
                }
            }
            else
            {
                if (lg_state) p_Snowball.lg_Active = false;
            }
        }
    }

    private void generateGhost()
    {
        //if (getRandomInt(30) == (15))
        if (getRandomInt(i_GhostFreq) == (i_GhostFreq >>> 1))
        {
            AnimeObject p_obj = getFirstInactiveObject();
            if (p_obj != null)
            {
                p_obj.initObject(AnimeObject.OBJECT_GHOST);

                // Selecting X
                int i8_x = 0;

                while (true)
                {
                    i8_x = i8_offsetOfViewX + getRandomInt((i_ScreenWidth << 8) / p_obj.i_Width) * p_obj.i_Width;
                    if (i8_x < (I8_LEFT_BORDER_X + 0x3000)) continue;

                    if (i8_x >= p_Player.i8_ScreenX && i8_x <= p_Player.i8_ScreenX + p_Player.i8_Width) continue;
                    break;
                }
                // Selecting direction
                if (getRandomInt(1000) < 500)
                    p_obj.i_ObjectDirection = AnimeObject.STATE_LEFT;
                else
                    p_obj.i_ObjectDirection = AnimeObject.STATE_RIGHT;

                // Selecting type of moving 0 - horz , 1 - vert, 2 - haotic
                p_obj.i_Param0 = (getRandomInt(299) / 100);

                if (p_obj.i_Param0 == 2)
                {
                    // Selecting derection
                    if (getRandomInt(1000) > 500)
                        p_obj.i_Param2 = AnimeObject.STATE_LEFT;
                    else
                        p_obj.i_Param2 = AnimeObject.STATE_RIGHT;
                }
                else
                {
                    // Selecting speed
                    p_obj.i_Param2 = (getRandomInt(1) + 1) << 8;
                }

                p_obj.setCenterXY_8(i8_x, p_Player.i8_centerY);
                p_obj.i_Param1 = p_Player.i8_centerY;
            }
        }
    }

    private void activateSnowBall()
    {
        int i8_player_centerX = p_Player.i8_centerX;
        int i8_player_centerY = p_Player.i8_centerY + I8_SNOWBALL_START_YOFFSET;
        int i8_player_halfwidth = p_Player.i8_halfWidth;

        if (p_Player.i_ObjectDirection == AnimeObject.STATE_LEFT)
        {
            i8_player_centerX -= i8_player_halfwidth;
        }
        else
        {
            i8_player_centerX += i8_player_halfwidth;
        }

        p_Snowball.i_ObjectDirection = p_Player.i_ObjectDirection;
        p_Snowball.initState(AnimeObject.STATE_APPEARANCE);
        p_Snowball.setCenterXY_8(i8_player_centerX, i8_player_centerY);
        p_Snowball.lg_Active = true;

        // Calculating of the throw force
        int i_force = (MAX_SNOWBALL_FORCE - i_snowballForce) / SNOWBALL_FORCE_STEP;
        i_force = i_force - (MAX_SNOWBALL_FORCE / SNOWBALL_FORCE_STEP);

        p_Snowball.i_Param0 = 0 + (i_force * I8_SNOWBALL_VERTSPEED_DELTA);
    }

    public void nextGameStep(int i_button)
    {

        AnimeObject p_collidedobject = processAnimationObjects();

        if (!processPlayer(p_collidedobject, i_button))
        {
            if (p_Player.i8_centerX >= i8_NextWaypointX)
            {
                initWaypoint(i_WayPointIndex + 1);
            }

            if (p_Player.i8_centerX + p_Player.i8_Width >= I8_MAINPIPE_CENTER_X)
            {
                i_PlayerState = PLAYERSTATE_WON;
                i8_deadCoords = p_Player.i8_centerX;
                i_PlayerScore += 100;
                i_GameState = GAMESTATE_OVER;
                return;
            }
        }

        i8_offsetOfViewX = p_Player.i8_centerX - i8_ScreenWidthHalf;

        calculateChimneyArrayIndexes();
        processChimney();
        processSnowball();
        processMoon();
        generateFallingStars();
//        if (i_button == BUTTON_FIRE) p_playerMoveObject.i_Button = BUTTON_NONE;
//        processBackgroundObjects();

        generateGhost();
        generateCats();
    }

    public String getGameID()
    {
        return "XMASQUEST";
    }

    public int getMaxSizeOfSavedGameBlock()
    {
        return 1100;
    }

    public void writeToStream(DataOutputStream _dataOutputStream) throws IOException
    {
        _dataOutputStream.writeByte(i_snowballForce);
        _dataOutputStream.writeInt(i8_curInitPlayerX);
        _dataOutputStream.writeInt(i8_curInitPlayerY);

        _dataOutputStream.writeByte(i_snowballForceDelay);
        _dataOutputStream.writeInt(i8_offsetOfViewX);
        _dataOutputStream.writeShort(i_PlayerHealth);
        _dataOutputStream.writeShort(i_WayPointIndex);
        _dataOutputStream.writeInt(i8_NextWaypointX);
        _dataOutputStream.writeShort(i_startIndexOfChimneyArray);
        _dataOutputStream.writeShort(i_endIndexOfChimneyArray);

        // Writing objects

        AnimeObject p_obj = null;

        // Writing Moon
        p_obj = p_Moon;
        _dataOutputStream.writeByte(p_obj.i_State);
        _dataOutputStream.writeByte(p_obj.i_Frame);
        _dataOutputStream.writeBoolean(p_obj.lg_back);
        _dataOutputStream.writeByte(p_obj.i_delay);
/*
        // Writing UFO
        p_obj = p_Ufo;
        _dataOutputStream.writeByte(p_obj.i_State);
        _dataOutputStream.writeBoolean(p_obj.lg_Active);
        _dataOutputStream.writeByte(p_obj.i_Frame);
        _dataOutputStream.writeInt(p_obj.i8_centerX);
        _dataOutputStream.writeInt(p_obj.i8_centerY);
        _dataOutputStream.writeBoolean(p_obj.lg_back);
        _dataOutputStream.writeByte(p_obj.i_delay);
        _dataOutputStream.writeByte(p_obj.i_ObjectDirection);

        // Writing WITCH
        p_obj = p_Witch;
        _dataOutputStream.writeByte(p_obj.i_State);
        _dataOutputStream.writeBoolean(p_obj.lg_Active);
        _dataOutputStream.writeByte(p_obj.i_Frame);
        _dataOutputStream.writeInt(p_obj.i8_centerX);
        _dataOutputStream.writeInt(p_obj.i8_centerY);
        _dataOutputStream.writeBoolean(p_obj.lg_back);
        _dataOutputStream.writeByte(p_obj.i_delay);
        _dataOutputStream.writeByte(p_obj.i_ObjectDirection);
*/
        // Writing SNOWBALL
        p_obj = p_Snowball;
        _dataOutputStream.writeByte(p_obj.i_State);
        _dataOutputStream.writeBoolean(p_obj.lg_Active);
        _dataOutputStream.writeByte(p_obj.i_Frame);

        _dataOutputStream.writeInt(p_obj.i8_centerX);
        _dataOutputStream.writeInt(p_obj.i8_centerY);
        _dataOutputStream.writeBoolean(p_obj.lg_back);
        _dataOutputStream.writeByte(p_obj.i_delay);
        _dataOutputStream.writeByte(p_obj.i_ObjectDirection);

        _dataOutputStream.writeInt(p_obj.i_Param0);
        _dataOutputStream.writeInt(p_obj.i_Param1);
        _dataOutputStream.writeInt(p_obj.i_Param2);

        // Writing PLAYER
        p_obj = p_Player;
        _dataOutputStream.writeByte(p_obj.i_State);
        _dataOutputStream.writeByte(p_obj.i_Frame);

        _dataOutputStream.writeInt(p_obj.i8_centerX);
        _dataOutputStream.writeInt(p_obj.i8_centerY);
        _dataOutputStream.writeBoolean(p_obj.lg_back);
        _dataOutputStream.writeByte(p_obj.i_delay);
        _dataOutputStream.writeByte(p_obj.i_ObjectDirection);

        _dataOutputStream.writeInt(p_obj.i_Param0);
        _dataOutputStream.writeInt(p_obj.i_Param1);
        _dataOutputStream.writeInt(p_obj.i_Param2);

        for (int li = 0; li < ap_MoveObjects.length; li++)
        {
            p_obj = ap_MoveObjects[li];
            _dataOutputStream.writeByte(p_obj.i_Type);
            _dataOutputStream.writeByte(p_obj.i_State);
            _dataOutputStream.writeBoolean(p_obj.lg_Active);
            _dataOutputStream.writeByte(p_obj.i_Frame);

            _dataOutputStream.writeInt(p_obj.i8_centerX);
            _dataOutputStream.writeInt(p_obj.i8_centerY);
            _dataOutputStream.writeBoolean(p_obj.lg_back);
            _dataOutputStream.writeByte(p_obj.i_delay);
            _dataOutputStream.writeByte(p_obj.i_ObjectDirection);

            _dataOutputStream.writeInt(p_obj.i_Param0);
            _dataOutputStream.writeInt(p_obj.i_Param1);
            _dataOutputStream.writeInt(p_obj.i_Param2);
            _dataOutputStream.writeInt(p_obj.i8_Speed);
        }
    }

    public void readFromStream(DataInputStream _dataInputStream) throws IOException
    {
        i_snowballForce = _dataInputStream.readByte() & 0xFF;
        i8_curInitPlayerX = _dataInputStream.readInt();
        i8_curInitPlayerY = _dataInputStream.readInt();

        i_snowballForceDelay = _dataInputStream.readByte() & 0xFF;
        i8_offsetOfViewX = _dataInputStream.readInt();
        i_PlayerHealth = _dataInputStream.readShort();
        i_WayPointIndex = _dataInputStream.readShort();
        i8_NextWaypointX = _dataInputStream.readInt();
        i_startIndexOfChimneyArray = _dataInputStream.readShort();
        i_endIndexOfChimneyArray = _dataInputStream.readShort();

        // Writing objects

        AnimeObject p_obj = null;

        // Writing Moon
        p_obj = p_Moon;
        p_obj.initObject(AnimeObject.OBJECT_MOON);
        p_obj.initState(_dataInputStream.readByte());
        p_obj.i_Frame = _dataInputStream.readByte();
        p_obj.lg_back = _dataInputStream.readBoolean();
        p_obj.i_delay = _dataInputStream.readByte();
/*
        // Writing UFO
        p_obj = p_Ufo;
        p_obj.initObject(AnimeObject.OBJECT_UFO);
        p_obj.initState(_dataInputStream.readByte());
        p_obj.lg_Active = _dataInputStream.readBoolean();
        p_obj.i_Frame = _dataInputStream.readByte();
        p_obj.i8_centerX = _dataInputStream.readInt();
        p_obj.i8_centerY = _dataInputStream.readInt();
        p_obj.updateXY();
        p_obj.lg_back = _dataInputStream.readBoolean();
        p_obj.i_delay = _dataInputStream.readByte();
        p_obj.i_ObjectDirection = _dataInputStream.readByte();

        // Writing WITCH
        p_obj = p_Witch;
        p_obj.initObject(AnimeObject.OBJECT_WITCH);
        p_obj.initState(_dataInputStream.readByte());
        p_obj.lg_Active = _dataInputStream.readBoolean();
        p_obj.i_Frame = _dataInputStream.readByte();
        p_obj.i8_centerX = _dataInputStream.readInt();
        p_obj.i8_centerY = _dataInputStream.readInt();
        p_obj.updateXY();
        p_obj.lg_back = _dataInputStream.readBoolean();
        p_obj.i_delay = _dataInputStream.readByte();
        p_obj.i_ObjectDirection = _dataInputStream.readByte();
*/
        // Writing SNOWBALL
        p_obj = p_Snowball;
        p_obj.initObject(AnimeObject.OBJECT_SNOWBAL);
        p_obj.initState(_dataInputStream.readByte());
        p_obj.lg_Active = _dataInputStream.readBoolean();
        p_obj.i_Frame = _dataInputStream.readByte();

        p_obj.i8_centerX = _dataInputStream.readInt();
        p_obj.i8_centerY = _dataInputStream.readInt();
        p_obj.updateXY();
        p_obj.lg_back = _dataInputStream.readBoolean();
        p_obj.i_delay = _dataInputStream.readByte();
        p_obj.i_ObjectDirection = _dataInputStream.readByte();

        p_obj.i_Param0 = _dataInputStream.readInt();
        p_obj.i_Param1 = _dataInputStream.readInt();
        p_obj.i_Param2 = _dataInputStream.readInt();

        // Writing PLAYER
        p_obj = p_Player;
        p_obj.initObject(AnimeObject.OBJECT_PLAYER);
        p_obj.lg_Active = true;
        p_obj.initState(_dataInputStream.readByte());
        p_obj.i_Frame = _dataInputStream.readByte();

        p_obj.i8_centerX = _dataInputStream.readInt();
        p_obj.i8_centerY = _dataInputStream.readInt();
        p_obj.updateXY();

        p_obj.lg_back = _dataInputStream.readBoolean();
        p_obj.i_delay = _dataInputStream.readByte();
        p_obj.i_ObjectDirection = _dataInputStream.readByte();

        p_obj.i_Param0 = _dataInputStream.readInt();
        p_obj.i_Param1 = _dataInputStream.readInt();
        p_obj.i_Param2 = _dataInputStream.readInt();

        for (int li = 0; li < ap_MoveObjects.length; li++)
        {
            p_obj = ap_MoveObjects[li];
            p_obj.initObject(_dataInputStream.readByte());
            p_obj.initState(_dataInputStream.readByte());
            p_obj.lg_Active = _dataInputStream.readBoolean();
            p_obj.i_Frame = _dataInputStream.readByte();

            p_obj.i8_centerX = _dataInputStream.readInt();
            p_obj.i8_centerY = _dataInputStream.readInt();
            p_obj.updateXY();
            p_obj.lg_back = _dataInputStream.readBoolean();
            p_obj.i_delay = _dataInputStream.readByte();
            p_obj.i_ObjectDirection = _dataInputStream.readByte();

            p_obj.i_Param0 = _dataInputStream.readInt();
            p_obj.i_Param1 = _dataInputStream.readInt();
            p_obj.i_Param2 = _dataInputStream.readInt();
            p_obj.i8_Speed = _dataInputStream.readInt();
        }
    }

    public int getPlayerScore()
    {
        int i_score = i_PlayerScore;
        int i_blocknum = (i8_deadCoords>>8)/BLOCKLENGTH;

        switch (i_GameLevel)
        {
            case LEVEL_0:
                {
                    i_score = i_score+i_blocknum*2;
                }
                ;
                break;
            case LEVEL_1:
                {
                    i_score = ((i_score * 0x180)>>8)+i_blocknum*3;
                }
                ;
                break;
            case LEVEL_2:
                {
                    i_score = ((i_score * 0x200)>>8)+i_blocknum*4;
                }
                ;
                break;
        }
        return i_score;
    }

    public long getLineElement(int _num)
    {
        int i_block = _num / BLOCKLENGTH;
        int i_indx = _num % BLOCKLENGTH;

        return alal_masks[ai_wayarray[i_block]][i_indx];
    }
}
