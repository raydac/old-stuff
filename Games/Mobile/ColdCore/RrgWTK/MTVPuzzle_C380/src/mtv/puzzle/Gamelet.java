package mtv.puzzle;

import java.util.Random;
import java.io.*;

/**
 * ����� ��������� ���� "�����" � ����� �������� � �������������� �������� �������� (����������� ������������� ����������� ���������� ���������).
 * @author  ����� �������
 * @version 1.00
 * @since 01-FEB-2004
 * (�) 2003-2005 Raydac Research Group Ltd.
 */
public class Gamelet
{
    //---------------------------------------------------------
    /**
     * ���������� ��������� �� ����� ������� ��� ������� ������
     */
    private static final int LEVEL_EASY_SIDEPARTS = 3;
    /**
     * ���������� ��������� �� ����� ������� ��� �������� ������
     */
    private static final int LEVEL_NORMAL_SIDEPARTS = 4;

    /**
     * ���������� ��������� �� ����� ������� ��� �������� ������
     */
    private static final int LEVEL_HARD_SIDEPARTS = 5;

    /**
     * ���������� ����� ��� ������������� ��� �������� ������
     */
    private static final int LEVEL_EASY_MIXSTEPS = 16;

    /**
     * ���������� ����� ��� ������������� ��� ����������� ������
     */
    private static final int LEVEL_NORMAL_MIXSTEPS = 46;

    /**
     * ���������� ����� ��� ������������� ��� �������� ������
     */
    private static final int LEVEL_HARD_MIXSTEPS = 108;

    /**
     * ������ ������� ���� ��� �������� ������
     */
    private static final int LEVE_EASY_EMPTYINDEX = 0;

    /**
     * ������ ������� ���� ��� ����������� ������
     */
    private static final int LEVE_NORMAL_EMPTYINDEX = 0;

    /**
     * ������ ������� ���� ��� �������� ������
     */
    private static final int LEVE_HARD_EMPTYINDEX = 0;


    /**
     * ���� ��� ������ ������
     */
    public static final int KEY_NONE = 0;

    /**
     * ���� ������ �����
     */
    public static final int KEY_UP = 1;

    /**
     * ���� ������ ����
     */
    public static final int KEY_DOWN = 2;

    /**
     * ���� ������ �����
     */
    public static final int KEY_LEFT = 4;

    /**
     * ���� ������ ������
     */
    public static final int KEY_RIGHT = 8;

    /**
     * ������� ������� �� ������������ ����� �������� �����
     */
    public static final int GAMEACTION_SOUND_MOVEBLOCK = 0;

    /**
     * ������� ������� �� ������������ ����� ���������� ��������
     */
    public static final int GAMEACTION_SOUND_WIN = 1;

    /**
     * ������� ������� �� ������������ ����� ���������� ����������� �������� �����
     */
    public static final int GAMEACTION_SOUND_CANTMOVE = 2;

    /**
     * ������� ���������� ��������� ��� �������� ������
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
     * ������� ���������� ��������� ��� ����������� ������
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
     * ������� ���������� ��������� ��� �������� ������
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
     * ������ ���������� ������� ������
     */
    public static Sprite [] ap_Sprites;

    /**
     * ������ ������ ��������� ����������� �����
     */
    public static Sprite p_arrowLeft;

    /**
     * ������ ������� ��������� ����������� �����
     */
    public static Sprite p_arrowRight;

    /**
     * ������ �������� ��������� ����������� �����
     */
    public static Sprite p_arrowTop;

    /**
     * ������ ������� ��������� ����������� �����
     */
    public static Sprite p_arrowDown;

    /**
     * ������ ��������� ������ ������
     */
    public static Sprite p_Pointer;

    /**
     * ���������� ������ �� ������ ��������� ��� �������� ������
     */
    public static int i_CurrentSidePartsNumber;

    /**
     * ������ ������ ������ ��� �������� ������ ���������
     */
    public static int i_EmptyPartIndex;

    /**
     * ������� ���������� �����
     */
    private static byte [] ab_CurrentCombination;

    /**
     * ������� ���������� ���������� ��� ���������� ������ ���������
     */
    private static byte [][] ab_currentWinCombination;

    /**
     * �������� ����������� ����� �� �����������
     */
    //#if VENDOR=="MOTOROLA" && MODEL=="C380"
    private static final int I8_STEPX = 0x410;
    /**
     * �������� ����������� ����� �� ���������
     */
    private static final int I8_STEPY = 0x410;
    //#else
    //#if VENDOR=="MOTOROLA" && MODEL=="E398"
    //$private static final int I8_STEPX = 0x710;
    //$private static final int I8_STEPY = 0x710;
    //#endif
    //#endif

    /**
     * ���� ���������� ��� ���� � ������ ����������� �����
     */
    private static boolean lg_AnimationMode;


    /**
     * ������ ������� ������������� �����
     */
    private static int i_AnimatedBlockIndex;

    /**
     * ������� ���������� X ������ ������
     */
    private static int i_PointerCellX;

    /**
     * ������� ���������� Y ������ ������
     */
    private static int i_PointerCellY;

    /**
     * ���������� ���������� X ������ ������
     */
    private static int i_nextPointerCellX;

    /**
     * ���������� ���������� Y ������ ������
     */
    private static int i_nextPointerCellY;

    /**
     * ���������� ����� ��� ������������� ����� ��� ���������� ������ ���������
     */
    private static int i_CurrentMixSteps;

    /**
     * ������� ����� ������
     */
    private static int i_PlayerMoveCounter;

    /**
     * ������ ���������� ������� ��������
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
     * ������ ����� ��� �������� ������
     */
    public static final int SPRITE_PARTEASYMODE = 0;

    /**
     * ������ ����� ��� �������� ������
     */
    public static final int SPRITE_PARTNORMALMODE = 1;

    /**
     * ������ ����� ��� �������� ������
     */
    public static final int SPRITE_PARTHARDMODE = 2;

    /**
     * ������ ����� �������
     */
    public static final int SPRITE_ARROWLEFT = 3;

    /**
     * ������ ������� �������
     */
    public static final int SPRITE_ARROWTOP = 4;

    /**
     * ������ ������ �������
     */
    public static final int SPRITE_ARROWDOWN = 5;

    /**
     * ������ ������ �������
     */
    public static final int SPRITE_ARROWRIGHT = 6;

    /**
     * ������ ��������� ������ ������ ��� �������� ������
     */
    public static final int SPRITE_POINTEREASY = 7;

    /**
     * ������ ��������� ������ ������ ��� ����������� ������
     */
    public static final int SPRITE_POINTERNORMAL = 8;

    /**
     * ������ ��������� ������ ������ ��� �������� ������
     */
    public static final int SPRITE_POINTERHARD = 9;

    /**
     * ������ ������� ��� �������� ������
     */
    public static int i8_PartWidth;

    /**
     * ������ ������� ��� �������� ������
     */
    public static int i8_PartHeight;
    //---------------------------------------------------------


    /**
     * ��������� ���������� ������� ��������� ������� ���������
     */
    public interface GameActionListener
    {
        /**
            ��������� �������� �������, � ����� �������� ���������� � �������� ������������ ���������
        */
        public int processGameAction(int _arg);
    }

    /**
     * ��������� ��������� �����
     */
    private static final Random p_RNDGenerator = new Random(System.currentTimeMillis());

    /**
     * ���������, ������������ ��� ����� � ����
     */
    public static final int PLAYER_PLAYING = 0;

    /**
     * ���������, ������������ ��� ����� �������
     */
    public static final int PLAYER_WIN = 1;

    /**
     * ���������, ������������ ��� ����� ��������
     */
    public static final int PLAYER_LOST = 2;

    /**
     * ���������, ������������ ���� � �������������������� ��������� ��� ������������������
     */
    public static final int STATE_UNKNON = 0;

    /**
     * ���������, ������������ ���� � ������������������ ���������
     */
    public static final int STATE_INITED = 1;

    /**
     * ���������, ������������ ���� � ���������� ���������
     */
    public static final int STATE_STARTED = 2;

    /**
     * ���������, ������������ ���� � ���������������� ���������
     */
    public static final int STATE_PAUSED = 3;

    /**
     * ���������, ������������ ���� � ����������� ���������
     */
    public static final int STATE_OVER = 4;

    /**
     * ���������, ������������ ��� ������� ���� "�������"
     */
    public static final int GAMELEVEL_EASY = 0;

    /**
     * ���������, ������������ ��� ������� ���� "����������"
     */
    public static final int GAMELEVEL_NORMAL = 1;

    /**
     * ���������, ������������ ��� ������� ���� "�������"
     */
    public static final int GAMELEVEL_HARD = 2;

    /**
     * ���������� �������� ��������� ����
     */
    public static int i_GameState = STATE_UNKNON;

    /**
     * ���������� �������� ���������� ��������� ����
     */
    public static int i_PrevGameState = STATE_UNKNON;

    /**
     * ���������� �������� ������� ������� ����
     */
    public static int i_GameLevel;

    /**
     * ���������� �������� ������� ���� ����
     */
    public static int i_GameStage;

    /**
     * ���������� �������� ��������� �� ��������� ������� �������
     */
    private static GameActionListener p_actionListener;

    /**
     * ���������� �������� ���������� �������� �� ������ ������� ����
     */
    protected static int i_GameAreaWidth;

    /**
     * ���������� �������� ���������� �������� �� ������ ������� ����
     */
    protected static int i_GameAreaHeight;

    /**
     * ���������� �������� ��������� ������
     */
    public static int i_PlayerState;

    /**
     * ���������� �������� �������� � ������������� ����� �������� ������.
     */
    public static int i_GameStepDelay;

    /**
     * ���������� �������� ���������� ������� ������� ���������
     */
    public static int i_PlayerAttemptions;

    /**
     * ������� ���������� �� ������������� �����, �� � ������ ����� �������� � ������ ���������. ��������� ���� � ��������� INITED..
     * @return true ���� ������������� ������ �������, ����� false.
     */
    public static final boolean init()
    {
        if (i_GameState != STATE_UNKNON) return false;
        p_actionListener = null;
        i_GameAreaHeight = -1;
        i_GameAreaWidth = -1;

        //------------�������� ���� ��� �����--------------------
        ap_Sprites = new Sprite[LEVEL_HARD_SIDEPARTS*LEVEL_HARD_SIDEPARTS];
        ab_CurrentCombination = new byte[LEVEL_HARD_SIDEPARTS*LEVEL_HARD_SIDEPARTS];
        //--------------------------------------------------

        setState(STATE_INITED);
        return true;
    }

    /**
     * ������� ���������� �� ��������������� �����, ����� � ������ ����� �������� � ������ ���������
     */
    public static final void release()
    {
        if (i_GameState == STATE_UNKNON) return;

        //------------�������� ���� ��� �����--------------------
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
     * ������� ������������� ��������� ����.
     * @param _state ����� ��������� ����.
     */
    private static final void setState(int _state)
    {
        i_PrevGameState = i_GameState;
        i_GameState = _state;
    }

    /**
     * ������� ���������� � ���������� ��������������� �������� �������� � �������� ������� (������������).
     * @param _limit ������ ������������� ��������� �������� (������������)
     * @return ��������������� ��������������� �������� � �������� �������.
     */
    private static final int getRandomInt(int _limit)
    {
        _limit++;
        _limit = (int) (((long) Math.abs(p_RNDGenerator.nextInt()) * (long) _limit) >>> 31);
        return _limit;
    }

    /**
     * ������������� ����� ������� ������
     * @param _gameAreaWidth �������� ������ ������� ���� � ��������
     * @param _gameAreaHeight �������� ������ ������� ���� � ��������
     * @param _gameLevel ��������� ������� ������
     * @param _actionListener ��������� ������� �������
     * @return true ���� ������������� ������� ������ ������ �������, ����� false.
     */
    public static final boolean initNewGame(final int _gameAreaWidth,final int _gameAreaHeight,final int _gameLevel,final GameActionListener _actionListener)
    {
        if (i_GameState != STATE_INITED) return false;
        p_actionListener = _actionListener;
        i_GameAreaHeight = _gameAreaHeight;
        i_GameAreaWidth = _gameAreaWidth;
        i_GameLevel = _gameLevel;
        initPlayerForGame(true);
        //------------�������� ���� ��� �����--------------------
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

        // ��������� ������
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

        // ������������
        mixGameArray(i_CurrentMixSteps);

        // �������������� ������� �� ����������
        fillSpritesForCombination();

        showCursor();
        //--------------------------------------------------

        setState(STATE_STARTED);
        return true;
    }

    /**
     * ��������������� ������ ������
     */
    public static final void releaseGame()
    {
        if (i_GameState == STATE_INITED || i_GameState == STATE_UNKNON) return;
        p_actionListener = null;
        i_GameAreaHeight = -1;
        i_GameAreaWidth = -1;

        //------------�������� ���� ��� �����--------------------
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
     * ���������� �������� �������� �� �����.
     */
    public static final void pauseGame()
    {
        if (i_GameState == STATE_STARTED)
        {
            setState(STATE_PAUSED);
            //------------�������� ���� ��� �����---------------

            //--------------------------------------------------
        }
        else
            return;
    }

    /**
     * ����������� �������� �������� ����� ������ ������ ��� ����� ���������� �� �����
     */
    public static final void resumeGameAfterPauseOrPlayerLost()
    {
        if (i_GameState == STATE_STARTED)
        {
            initPlayerForGame(false);
            //------------�������� ���� ��� �����-----------------------------
            //------��� �������������� ��� ������� ����� ������ ������--------

            //----------------------------------------------------------------
        }
        else
        if (i_GameState == STATE_PAUSED)
        {
            setState(STATE_STARTED);
            //------------�������� ���� ��� �����--------------------
            //------��� �������������� ��� ������ � �����--------

            //--------------------------------------------------
        }
        else
            return;
    }

    /**
     * ���������� ���������� ����� ������ �� ��������� ������� ������
     * @return �������� ���������� ����� ������.
     */
    public static final int getPlayerScore()
    {
        //------------�������� ���� ��� �����--------------------
        int i_scores = 77*((i_GameLevel+1)*(i_GameLevel+1))+i_CurrentMixSteps-i_PlayerMoveCounter;
        if (i_scores<0) i_scores = 0;
        //--------------------------------------------------
        return i_scores;
    }

    /**
     * ������������� ������ ����� ������ ��� ��� ������������� ������� ������
     * @param _initGame ���� true �� ������������� ����, ���� false �� ����� ������ ������
     */
    private static final void initPlayerForGame(boolean _initGame)
    {
        //------------�������� ���� ��� �����--------------------

        //--------------------------------------------------
        i_PlayerState = PLAYER_PLAYING;
    }

    /**
     * ������������� �������� ������
     * @param _stage ID �������� ������
     * @return true ���� ������������� ������ ������ ����� false
     */
    public static final boolean initGameStage(int _stage)
    {
        //------------�������� ���� ��� �����--------------------

        //--------------------------------------------------
        i_GameStage = _stage;
        initPlayerForGame(false);
        return true;
    }

    /**
     * �������� �������� ��������� �� ������� ����
     * @param _data ������ ����, ����������� ���������
     * @throws Exception ���� ��������� ������ ��� �������� ��������� ��� ���� ���������� � ��������� ������������� � ���������.
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
        //------------�������� ���� ��� �����--------------------
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
     * ������� ��������� ���� ������, ����������� ������� ������� ���������
     * @return �������� ������, ���������� ���������� ��������� �������� ��������
     * @throws Exception ���� ���� ��� ����� ��������� � ������������� ���������, ��������� ������ ���������� ��� �������������� ������ ������������� �������
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
        //------------�������� ���� ��� �����--------------------
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
     * ������� ���������� ������, ��������� ��� ���������� ����� ������� ������.
     * @return ��������� ������ ����� ������.
     */
    public static final int getGameStateDataBlockSize()
    {
        int MINIMUM_SIZE = 10;

        MINIMUM_SIZE += (LEVEL_HARD_SIDEPARTS*LEVEL_HARD_SIDEPARTS);
        MINIMUM_SIZE += 4;

        return MINIMUM_SIZE;
    }

    /**
     * ���������� ��������� ������������� ����
     * @return ������, ���������������� ����.
     */
    public static final String getID()
    {
        return "mtvpzzl";
    }

    /**
     * ������� ������������ ������� ���
     * @param _keyStateFlags ����� ���������� �������.
     * @return ������ ���� ����� ��������� ������� ���������
     */
    public static final int nextGameStep(int _keyStateFlags)
    {
        //------------�������� ���� ��� �����--------------------
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
     * �������������� ������������ ����
     * @param _curPosIndex ������� � ������� ���������
     * @param _newIndex ������� � ������� ��������� �����������
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
     * ����, ������������ ��� ���� ������������ ���������� �������� ������ (����)
     */
    public static final int FLAG_SUPPORTRESTART = 1;
    /**
     * ����, ������������ ��� ���� ������������ ������� ������ (����)
     */
    public static final int FLAG_STAGESUPPORT = 2;

    public static final int getSupportedModes()
    {
        //#global STAGESUPPORT=false
        return 0;
    }

    /**
     * ������� ������������ �������� ������, �������� ������� �� ������� ��������
     * @param _sprite     �������������� ������
     * @param _actorIndex ������ ����������� ������
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
     * ���������� �����, ����������� ���� ������ � �������
     */
    private static final int SPRITEDATALENGTH = 6;

    /**
     * �������� ��������� ������ ������
     */
    private static final void hideCursor()
    {
        p_Pointer.lg_SpriteInvisible = true;
    }

    /**
     * ���������� ��������� ������ ������
     */
    private static final void showCursor()
    {
        p_Pointer.lg_SpriteInvisible = false;
        int i8_mx = p_Pointer.i_mainX;
        int i8_my = p_Pointer.i_mainY;
        int i8_cx = p_Pointer.i_mainX+(p_Pointer.i_width>>1);
        int i8_cy = p_Pointer.i_mainY+(p_Pointer.i_height>>1);

        // ������������ ����� ��������� �������
        // �����
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
     * ��������� ������� ������� ���������� �� ������������
     * @return true ���� ������� � false ���� ��������
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
     * ��������� ������ ���� ����������� �����
     * @return true ���� ��������� � false ���� �����������
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
     * ���������������� ���������� � ��������� �������� ��� ������� ������� ����������
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
     * ������������ ������� ������
     * @param _stepsNumber ���������� �����
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
     * ��������� �������� ������� ��������
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
