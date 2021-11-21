import java.io.DataOutputStream;
import java.io.IOException;
import java.io.DataInputStream;

public class GameletImpl extends Gamelet
{


    private static final int SCALEFACTOR = 1;

    //=============GAME ACTIONS==================
    public static final int GAMEACTION_SND_PLAYERREADY = 0;
    public static final int GAMEACTION_SND_PLAYERDEATH = 1;
    public static final int GAMEACTION_SND_MUMMYKILLED = 2;
    public static final int GAMEACTION_SND_VESSELTAKEN = 3;
    public static final int GAMEACTION_SND_DIAMONDTAKEN = 4;
    public static final int GAMEACTION_SND_HAMMERTAKEN = 5;
    public static final int GAMEACTION_SND_PICKTAKEN = 6;
    public static final int GAMEACTION_SND_PICKUSED = 7;
    public static final int GAMEACTION_SND_HAMMERUSED = 8;
    public static final int GAMEACTION_SND_MUMMYREADY = 9;
    public static final int GAMEACTION_SND_HAMMERDEACTIVATED = 10;
    public static final int GAMEACTION_SND_KEYSHOWED = 11;
    public static final int GAMEACTION_SND_KEYTAKEN = 13;
    public static final int GAMEACTION_SND_DOOROPENED = 14;
    public static final int GAMEACTION_SND_MUMMYATTACK = 15;

    public static final int GAMEACTION_ARRAY_CHANGED = 15;
    public static final int GAMEACTION_SLOW_MOTION   = 16;
    public static final int GAMEACTION_NORMAL_MOTION = 17;
    //===========================================

    public static final int LEVEL0 = 0;
    public static final int LEVEL0_TIMEDELAY = 100;
    public static final int LEVEL0_ATTEMPTIONS = 4;

    public static final int LEVEL1 = 1;
    public static final int LEVEL1_TIMEDELAY = 90;
    public static final int LEVEL1_ATTEMPTIONS = 3;

    public static final int LEVEL2 = 2;
    public static final int LEVEL2_TIMEDELAY = 80;
    public static final int LEVEL2_ATTEMPTIONS = 2;

    public static final int PLAYERKEY_NONE = 0;
    public static final int PLAYERKEY_LEFT = 1;
    public static final int PLAYERKEY_RIGHT = 2;
    public static final int PLAYERKEY_DOWN = 4;
    public static final int PLAYERKEY_UP = 8;
    public static final int PLAYERKEY_FIRE = 16;
    public static final int PLAYERKEY_JUMP = 32;

    public static final int VIRTUALBLOCKWIDTH = 0x1000 * SCALEFACTOR;
    public static final int VIRTUALBLOCKHEIGHT = 0x1000 * SCALEFACTOR;

    public int i_PlayerKey;

    public int i_currentStageWidth;
    public int i_currentStageHeight;
    public byte[] ab_currentStageArray;
    private byte[] ab_stageDump;

    public static final int OBJECT_PLAYER = 0;
    public static final int OBJECT_DOOR = 1;
    public static final int OBJECT_DIAMOND = 2;
    public static final int OBJECT_KEY = 3;
    public static final int OBJECT_REDMUMMY = 4;
    public static final int OBJECT_YELLOWMUMMY = 5;
    public static final int OBJECT_BLACKMUMMY = 6;
    public static final int OBJECT_HAMMER = 7;
    public static final int OBJECT_VESSEL = 8;
    public static final int OBJECT_PICK = 9;

    public static final int STATE_APPEARANCE = 0;
    public static final int STATE_MOVE = 1;
    public static final int STATE_STAY = 2;
    public static final int STATE_FIRE = 3;
    public static final int STATE_JUMP = 4;
    public static final int STATE_DEATH = 5;
    public static final int STATE_UP = 6;
    public static final int STATE_DOWN = 7;
    public static final int STATE_PICKER = 8;
    public static final int STATE_FALL = 9;

    private static final int REDMUMMYDELAYTICKS = 30;
    private static final int REDMUMMYSTRIKE = 10;
    private static final int YELLOWMUMMYDELAYTICKS = 45;
    private static final int YELLOWMUMMYSTRIKE = 20;
    private static final int BLACKMUMMYDELAYTICKS = 60;
    private static final int BLACKMUMMYSTRIKE = 30;

    private static final int BLACKMUMMYSPEED = 0x300;
    private static final int REDMUMMYSPEED = 0x300;
    private static final int YELLOWMUMMYSPEED = 0x300;

    private static final int SCORES_YELLOWMUMMY = 40;
    private static final int SCORES_BLACKMUMMY = 30;
    private static final int SCORES_REDMUMMY = 50;
    private static final int SCORES_DIAMOND = 10;

    public static final int INITPLAYERFORCE = 100;
    public int i_PlayerForce;

    public int i_TheIndexOfLastTakenHammer;
    public int i_TheIndexOfLastTakenPick;

    private int GRAVITYCONSTANT = VIRTUALBLOCKHEIGHT >> 1;

    private int PLAYER_HORZSPEED = 0x400 * SCALEFACTOR;
    private int HAMMER_HORZSPEED = 0x800 * SCALEFACTOR;
    private int JUMPSTEP = 0x400 * SCALEFACTOR;
    private int JUMPLEN = 0x2000 * SCALEFACTOR;

    private int BORDER_GAP = 0x300;

    private int i_insideTimer;

    private boolean lg_StageCompleted;

    protected int i_stageDiamonds;

    // Start point of stage
    protected int i_StageAttemptions;
    protected int i_StagePlayerForce;
    protected int i_StagePlayerScore;

    //Sprite defenition array
    public static final short[] ai_SpriteDefinitionArray = new short[]
    {
        // Width, Height, OffX, OffY, OffWdth, OffHght, Frames, Delay, Animation type, Anchor
        //PLAYER
        //APPEARANCE
        0x2000 * SCALEFACTOR, 0x2000 * SCALEFACTOR, 0x600 * SCALEFACTOR, 0x400 * SCALEFACTOR, 0x1400 * SCALEFACTOR, 0x1c00 * SCALEFACTOR, 6, 2, Sprite.ANIMATION_FROZEN, Sprite.SPRITE_ALIGN_DOWN | Sprite.SPRITE_ALIGN_CENTER,
        //MOVE
        0x2000 * SCALEFACTOR, 0x2000 * SCALEFACTOR, 0x600 * SCALEFACTOR, 0x400 * SCALEFACTOR, 0x1400 * SCALEFACTOR, 0x1c00 * SCALEFACTOR, 5, 1, Sprite.ANIMATION_PENDULUM, Sprite.SPRITE_ALIGN_DOWN | Sprite.SPRITE_ALIGN_CENTER,
        //STAY
        0x2000 * SCALEFACTOR, 0x2000 * SCALEFACTOR, 0x600 * SCALEFACTOR, 0x400 * SCALEFACTOR, 0x1400 * SCALEFACTOR, 0x1c00 * SCALEFACTOR, 1, 1, Sprite.ANIMATION_FROZEN, Sprite.SPRITE_ALIGN_DOWN | Sprite.SPRITE_ALIGN_CENTER,
        //FIRE
        0x2000 * SCALEFACTOR, 0x2000 * SCALEFACTOR, 0x600 * SCALEFACTOR, 0x400 * SCALEFACTOR, 0x1400 * SCALEFACTOR, 0x1c00 * SCALEFACTOR, 4, 1, Sprite.ANIMATION_FROZEN, Sprite.SPRITE_ALIGN_DOWN | Sprite.SPRITE_ALIGN_CENTER,
        //JUMP
        0x2000 * SCALEFACTOR, 0x2000 * SCALEFACTOR, 0x600 * SCALEFACTOR, 0x400 * SCALEFACTOR, 0x1400 * SCALEFACTOR, 0x1c00 * SCALEFACTOR, 5, 1, Sprite.ANIMATION_PENDULUM, Sprite.SPRITE_ALIGN_DOWN | Sprite.SPRITE_ALIGN_CENTER,
        //DEATH
        0x2000 * SCALEFACTOR, 0x2000 * SCALEFACTOR, 0x600 * SCALEFACTOR, 0x400 * SCALEFACTOR, 0x1400 * SCALEFACTOR, 0x1c00 * SCALEFACTOR, 6, 4, Sprite.ANIMATION_FROZEN, Sprite.SPRITE_ALIGN_DOWN | Sprite.SPRITE_ALIGN_CENTER,
        //UP
        0x2000 * SCALEFACTOR, 0x2000 * SCALEFACTOR, 0x600 * SCALEFACTOR, 0x400 * SCALEFACTOR, 0x1400 * SCALEFACTOR, 0x1c00 * SCALEFACTOR, 5, 1, Sprite.ANIMATION_PENDULUM, Sprite.SPRITE_ALIGN_DOWN | Sprite.SPRITE_ALIGN_CENTER,
        //DOWN
        0x2000 * SCALEFACTOR, 0x2000 * SCALEFACTOR, 0x600 * SCALEFACTOR, 0x400 * SCALEFACTOR, 0x1400 * SCALEFACTOR, 0x1c00 * SCALEFACTOR, 5, 1, Sprite.ANIMATION_PENDULUM, Sprite.SPRITE_ALIGN_DOWN | Sprite.SPRITE_ALIGN_CENTER,
        //PICKER
        0x2000 * SCALEFACTOR, 0x2000 * SCALEFACTOR, 0x600 * SCALEFACTOR, 0x400 * SCALEFACTOR, 0x1400 * SCALEFACTOR, 0x1c00 * SCALEFACTOR, 5, 2, Sprite.ANIMATION_FROZEN, Sprite.SPRITE_ALIGN_DOWN | Sprite.SPRITE_ALIGN_CENTER,
        //FALL
        0x2000 * SCALEFACTOR, 0x2000 * SCALEFACTOR, 0x600 * SCALEFACTOR, 0x400 * SCALEFACTOR, 0x1400 * SCALEFACTOR, 0x1c00 * SCALEFACTOR, 2, 3, Sprite.ANIMATION_FROZEN, Sprite.SPRITE_ALIGN_DOWN | Sprite.SPRITE_ALIGN_CENTER,

        //DOOR
        //APPEARANCE
        0x2000 * SCALEFACTOR, 0x2000 * SCALEFACTOR, 0xe00 * SCALEFACTOR, 0x1c00 * SCALEFACTOR, 0x400 * SCALEFACTOR, 0x400 * SCALEFACTOR, 5, 1, Sprite.ANIMATION_FROZEN, Sprite.SPRITE_ALIGN_DOWN | Sprite.SPRITE_ALIGN_CENTER,
        //MOVE
        0 * SCALEFACTOR, 0 * SCALEFACTOR, 0 * SCALEFACTOR, 0 * SCALEFACTOR, 0 * SCALEFACTOR, 0 * SCALEFACTOR, 0, 0, 0, 0,
        //STAY
        0x2000 * SCALEFACTOR, 0x2000 * SCALEFACTOR, 0xe00 * SCALEFACTOR, 0x1c00 * SCALEFACTOR, 0x400 * SCALEFACTOR, 0x400 * SCALEFACTOR, 12, 2, Sprite.ANIMATION_CYCLIC, Sprite.SPRITE_ALIGN_DOWN | Sprite.SPRITE_ALIGN_CENTER,
        //FIRE
        0 * SCALEFACTOR, 0 * SCALEFACTOR, 0 * SCALEFACTOR, 0 * SCALEFACTOR, 0 * SCALEFACTOR, 0 * SCALEFACTOR, 0, 0, 0, 0,
        //JUMP
        0 * SCALEFACTOR, 0 * SCALEFACTOR, 0 * SCALEFACTOR, 0 * SCALEFACTOR, 0 * SCALEFACTOR, 0 * SCALEFACTOR, 0, 0, 0, 0,
        //DEATH
        0 * SCALEFACTOR, 0 * SCALEFACTOR, 0 * SCALEFACTOR, 0 * SCALEFACTOR, 0 * SCALEFACTOR, 0 * SCALEFACTOR, 0, 0, 0, 0,
        //UP
        0 * SCALEFACTOR, 0 * SCALEFACTOR, 0 * SCALEFACTOR, 0 * SCALEFACTOR, 0 * SCALEFACTOR, 0 * SCALEFACTOR, 0, 0, 0, 0,
        //DOWN
        0 * SCALEFACTOR, 0 * SCALEFACTOR, 0 * SCALEFACTOR, 0 * SCALEFACTOR, 0 * SCALEFACTOR, 0 * SCALEFACTOR, 0, 0, 0, 0,
        //PICKER
        0 * SCALEFACTOR, 0 * SCALEFACTOR, 0 * SCALEFACTOR, 0 * SCALEFACTOR, 0 * SCALEFACTOR, 0 * SCALEFACTOR, 0, 0, 0, 0,
        //FALL
        0 * SCALEFACTOR, 0 * SCALEFACTOR, 0 * SCALEFACTOR, 0 * SCALEFACTOR, 0 * SCALEFACTOR, 0 * SCALEFACTOR, 0, 0, 0, 0,

        //DIAMOND
        //APPEARANCE
        0x1000 * SCALEFACTOR, 0x1000 * SCALEFACTOR, 0x400 * SCALEFACTOR, 0x400 * SCALEFACTOR, 0x800 * SCALEFACTOR, 0x800 * SCALEFACTOR, 8, 2, Sprite.ANIMATION_PENDULUM, Sprite.SPRITE_ALIGN_DOWN | Sprite.SPRITE_ALIGN_CENTER,
        //MOVE
        0 * SCALEFACTOR, 0 * SCALEFACTOR, 0 * SCALEFACTOR, 0 * SCALEFACTOR, 0 * SCALEFACTOR, 0 * SCALEFACTOR, 0, 0, 0, 0,
        //STAY
        0 * SCALEFACTOR, 0 * SCALEFACTOR, 0 * SCALEFACTOR, 0 * SCALEFACTOR, 0 * SCALEFACTOR, 0 * SCALEFACTOR, 0, 0, 0, 0,
        //FIRE
        0 * SCALEFACTOR, 0 * SCALEFACTOR, 0 * SCALEFACTOR, 0 * SCALEFACTOR, 0 * SCALEFACTOR, 0 * SCALEFACTOR, 0, 0, 0, 0,
        //JUMP
        0 * SCALEFACTOR, 0 * SCALEFACTOR, 0 * SCALEFACTOR, 0 * SCALEFACTOR, 0 * SCALEFACTOR, 0 * SCALEFACTOR, 0, 0, 0, 0,
        //DEATH
        0x1000 * SCALEFACTOR, 0x1000 * SCALEFACTOR, 0x400 * SCALEFACTOR, 0x400 * SCALEFACTOR, 0x800 * SCALEFACTOR, 0x800 * SCALEFACTOR, 5, 2, Sprite.ANIMATION_FROZEN, Sprite.SPRITE_ALIGN_DOWN | Sprite.SPRITE_ALIGN_CENTER,
        //UP
        0 * SCALEFACTOR, 0 * SCALEFACTOR, 0 * SCALEFACTOR, 0 * SCALEFACTOR, 0 * SCALEFACTOR, 0 * SCALEFACTOR, 0, 0, 0, 0,
        //DOWN
        0 * SCALEFACTOR, 0 * SCALEFACTOR, 0 * SCALEFACTOR, 0 * SCALEFACTOR, 0 * SCALEFACTOR, 0 * SCALEFACTOR, 0, 0, 0, 0,
        //PICKER
        0 * SCALEFACTOR, 0 * SCALEFACTOR, 0 * SCALEFACTOR, 0 * SCALEFACTOR, 0 * SCALEFACTOR, 0 * SCALEFACTOR, 0, 0, 0, 0,
        //FALL
        0 * SCALEFACTOR, 0 * SCALEFACTOR, 0 * SCALEFACTOR, 0 * SCALEFACTOR, 0 * SCALEFACTOR, 0 * SCALEFACTOR, 0, 0, 0, 0,

        //KEY
        //APPEARANCE
        0x1000 * SCALEFACTOR, 0x1000 * SCALEFACTOR, 0x400 * SCALEFACTOR, 0x400 * SCALEFACTOR, 0x800 * SCALEFACTOR, 0x800 * SCALEFACTOR, 5, 2, Sprite.ANIMATION_FROZEN, Sprite.SPRITE_ALIGN_DOWN | Sprite.SPRITE_ALIGN_CENTER,
        //MOVE
        0 * SCALEFACTOR, 0 * SCALEFACTOR, 0 * SCALEFACTOR, 0 * SCALEFACTOR, 0 * SCALEFACTOR, 0 * SCALEFACTOR, 0, 0, 0, 0,
        //STAY
        0x1000 * SCALEFACTOR, 0x1000 * SCALEFACTOR, 0x400 * SCALEFACTOR, 0x400 * SCALEFACTOR, 0x800 * SCALEFACTOR, 0x800 * SCALEFACTOR, 14, 1, Sprite.ANIMATION_CYCLIC, Sprite.SPRITE_ALIGN_DOWN | Sprite.SPRITE_ALIGN_CENTER,
        //FIRE
        0x1000 * SCALEFACTOR, 0x1000 * SCALEFACTOR, 0x400 * SCALEFACTOR, 0x400 * SCALEFACTOR, 0x800 * SCALEFACTOR, 0x800 * SCALEFACTOR, 0, 0, 0, 0,
        //JUMP
        0 * SCALEFACTOR, 0 * SCALEFACTOR, 0 * SCALEFACTOR, 0 * SCALEFACTOR, 0 * SCALEFACTOR, 0 * SCALEFACTOR, 0, 0, 0, 0,
        //DEATH
        0x1000 * SCALEFACTOR, 0x1000 * SCALEFACTOR, 0x400 * SCALEFACTOR, 0x400 * SCALEFACTOR, 0x800 * SCALEFACTOR, 0x800 * SCALEFACTOR, 5, 2, Sprite.ANIMATION_FROZEN, Sprite.SPRITE_ALIGN_DOWN | Sprite.SPRITE_ALIGN_CENTER,
        //UP
        0 * SCALEFACTOR, 0 * SCALEFACTOR, 0 * SCALEFACTOR, 0 * SCALEFACTOR, 0 * SCALEFACTOR, 0 * SCALEFACTOR, 0, 0, 0, 0,
        //DOWN
        0 * SCALEFACTOR, 0 * SCALEFACTOR, 0 * SCALEFACTOR, 0 * SCALEFACTOR, 0 * SCALEFACTOR, 0 * SCALEFACTOR, 0, 0, 0, 0,
        //PICKER
        0 * SCALEFACTOR, 0 * SCALEFACTOR, 0 * SCALEFACTOR, 0 * SCALEFACTOR, 0 * SCALEFACTOR, 0 * SCALEFACTOR, 0, 0, 0, 0,
        //FALL
        0 * SCALEFACTOR, 0 * SCALEFACTOR, 0 * SCALEFACTOR, 0 * SCALEFACTOR, 0 * SCALEFACTOR, 0 * SCALEFACTOR, 0, 0, 0, 0,

        //REDMUMMY
        //APPEARANCE
        0x2000 * SCALEFACTOR, 0x2000 * SCALEFACTOR, 0x200 * SCALEFACTOR, 0x0800 * SCALEFACTOR, 0x1C00 * SCALEFACTOR, 0x1800 * SCALEFACTOR, 9, 2, Sprite.ANIMATION_FROZEN, Sprite.SPRITE_ALIGN_DOWN | Sprite.SPRITE_ALIGN_CENTER,
        //MOVE
        0x2000 * SCALEFACTOR, 0x2000 * SCALEFACTOR, 0x200 * SCALEFACTOR, 0x0800 * SCALEFACTOR, 0x1C00 * SCALEFACTOR, 0x1800 * SCALEFACTOR, 6, 2, Sprite.ANIMATION_CYCLIC, Sprite.SPRITE_ALIGN_DOWN | Sprite.SPRITE_ALIGN_CENTER,
        //STAY
        0x2000 * SCALEFACTOR, 0x2000 * SCALEFACTOR, 0x200 * SCALEFACTOR, 0x0800 * SCALEFACTOR, 0x1C00 * SCALEFACTOR, 0x1800 * SCALEFACTOR, 6, 2, Sprite.ANIMATION_CYCLIC, Sprite.SPRITE_ALIGN_DOWN | Sprite.SPRITE_ALIGN_CENTER,
        //FIRE
        0x2000 * SCALEFACTOR, 0x2000 * SCALEFACTOR, 0x200 * SCALEFACTOR, 0x0800 * SCALEFACTOR, 0x1C00 * SCALEFACTOR, 0x1800 * SCALEFACTOR, 5, 1, Sprite.ANIMATION_CYCLIC, Sprite.SPRITE_ALIGN_DOWN | Sprite.SPRITE_ALIGN_CENTER,
        //JUMP
        0x2000 * SCALEFACTOR, 0x2000 * SCALEFACTOR, 0x200 * SCALEFACTOR, 0x0800 * SCALEFACTOR, 0x1C00 * SCALEFACTOR, 0x1800 * SCALEFACTOR, 3, 2, Sprite.ANIMATION_FROZEN, Sprite.SPRITE_ALIGN_DOWN | Sprite.SPRITE_ALIGN_CENTER,
        //DEATH
        0x2000 * SCALEFACTOR, 0x2000 * SCALEFACTOR, 0x200 * SCALEFACTOR, 0x0800 * SCALEFACTOR, 0x1C00 * SCALEFACTOR, 0x1800 * SCALEFACTOR, 8, 2, Sprite.ANIMATION_FROZEN, Sprite.SPRITE_ALIGN_DOWN | Sprite.SPRITE_ALIGN_CENTER,
        //UP
        0x2000 * SCALEFACTOR, 0x2000 * SCALEFACTOR, 0x200 * SCALEFACTOR, 0x0800 * SCALEFACTOR, 0x1C00 * SCALEFACTOR, 0x1800 * SCALEFACTOR, 6, 4, Sprite.ANIMATION_CYCLIC, Sprite.SPRITE_ALIGN_DOWN | Sprite.SPRITE_ALIGN_CENTER,
        //DOWN
        0x2000 * SCALEFACTOR, 0x2000 * SCALEFACTOR, 0x200 * SCALEFACTOR, 0x0800 * SCALEFACTOR, 0x1C00 * SCALEFACTOR, 0x1800 * SCALEFACTOR, 6, 4, Sprite.ANIMATION_CYCLIC, Sprite.SPRITE_ALIGN_DOWN | Sprite.SPRITE_ALIGN_CENTER,
        //PICKER
        0x2000 * SCALEFACTOR, 0x2000 * SCALEFACTOR, 0x200 * SCALEFACTOR, 0x0800 * SCALEFACTOR, 0x1C00 * SCALEFACTOR, 0x1800 * SCALEFACTOR, 6, 4, Sprite.ANIMATION_FROZEN, Sprite.SPRITE_ALIGN_DOWN | Sprite.SPRITE_ALIGN_CENTER,
        //FALL
        0x2000 * SCALEFACTOR, 0x2000 * SCALEFACTOR, 0x200 * SCALEFACTOR, 0x0800 * SCALEFACTOR, 0x1C00 * SCALEFACTOR, 0x1800 * SCALEFACTOR, 2, 4, Sprite.ANIMATION_FROZEN, Sprite.SPRITE_ALIGN_DOWN | Sprite.SPRITE_ALIGN_CENTER,

        //YELLOWMUMMY
        //APPEARANCE
        0x3700 * SCALEFACTOR, 0x2500 * SCALEFACTOR, 0x1100 * SCALEFACTOR, 0x0900 * SCALEFACTOR, 0x1500 * SCALEFACTOR, 0x1600 * SCALEFACTOR, 12, 2, Sprite.ANIMATION_FROZEN, Sprite.SPRITE_ALIGN_DOWN | Sprite.SPRITE_ALIGN_CENTER,
        //MOVE
        0x3700 * SCALEFACTOR, 0x2500 * SCALEFACTOR, 0x1100 * SCALEFACTOR, 0x0900 * SCALEFACTOR, 0x1500 * SCALEFACTOR, 0x1600 * SCALEFACTOR, 6, 2, Sprite.ANIMATION_CYCLIC, Sprite.SPRITE_ALIGN_DOWN | Sprite.SPRITE_ALIGN_CENTER,
        //STAY
        0x3700 * SCALEFACTOR, 0x2500 * SCALEFACTOR, 0x1100 * SCALEFACTOR, 0x0900 * SCALEFACTOR, 0x1500 * SCALEFACTOR, 0x1600 * SCALEFACTOR, 6, 2, Sprite.ANIMATION_CYCLIC, Sprite.SPRITE_ALIGN_DOWN | Sprite.SPRITE_ALIGN_CENTER,
        //FIRE
        0x3700 * SCALEFACTOR, 0x2500 * SCALEFACTOR, 0x1100 * SCALEFACTOR, 0x0900 * SCALEFACTOR, 0x1500 * SCALEFACTOR, 0x1600 * SCALEFACTOR, 13, 1, Sprite.ANIMATION_CYCLIC, Sprite.SPRITE_ALIGN_DOWN | Sprite.SPRITE_ALIGN_CENTER,
        //JUMP
        0x3700 * SCALEFACTOR, 0x2500 * SCALEFACTOR, 0x1100 * SCALEFACTOR, 0x0900 * SCALEFACTOR, 0x1500 * SCALEFACTOR, 0x1600 * SCALEFACTOR, 6, 1, Sprite.ANIMATION_FROZEN, Sprite.SPRITE_ALIGN_DOWN | Sprite.SPRITE_ALIGN_CENTER,
        //DEATH
        0x3700 * SCALEFACTOR, 0x2500 * SCALEFACTOR, 0x1100 * SCALEFACTOR, 0x0900 * SCALEFACTOR, 0x1500 * SCALEFACTOR, 0x1600 * SCALEFACTOR, 8, 1, Sprite.ANIMATION_FROZEN, Sprite.SPRITE_ALIGN_DOWN | Sprite.SPRITE_ALIGN_CENTER,
        //UP
        0x3700 * SCALEFACTOR, 0x2500 * SCALEFACTOR, 0x1100 * SCALEFACTOR, 0x0900 * SCALEFACTOR, 0x1500 * SCALEFACTOR, 0x1600 * SCALEFACTOR, 6, 4, Sprite.ANIMATION_CYCLIC, Sprite.SPRITE_ALIGN_DOWN | Sprite.SPRITE_ALIGN_CENTER,
        //DOWN
        0x3700 * SCALEFACTOR, 0x2500 * SCALEFACTOR, 0x1100 * SCALEFACTOR, 0x0900 * SCALEFACTOR, 0x1500 * SCALEFACTOR, 0x1600 * SCALEFACTOR, 6, 4, Sprite.ANIMATION_CYCLIC, Sprite.SPRITE_ALIGN_DOWN | Sprite.SPRITE_ALIGN_CENTER,
        //PICKER
        0x3700 * SCALEFACTOR, 0x2500 * SCALEFACTOR, 0x1100 * SCALEFACTOR, 0x0900 * SCALEFACTOR, 0x1500 * SCALEFACTOR, 0x1600 * SCALEFACTOR, 6, 4, Sprite.ANIMATION_FROZEN, Sprite.SPRITE_ALIGN_DOWN | Sprite.SPRITE_ALIGN_CENTER,
        //FALL
        0x3700 * SCALEFACTOR, 0x2500 * SCALEFACTOR, 0x1100 * SCALEFACTOR, 0x0900 * SCALEFACTOR, 0x1500 * SCALEFACTOR, 0x1600 * SCALEFACTOR, 6, 4, Sprite.ANIMATION_FROZEN, Sprite.SPRITE_ALIGN_DOWN | Sprite.SPRITE_ALIGN_CENTER,

        //BLACKMUMMY
        //APPEARANCE
        0x2000 * SCALEFACTOR, 0x2000 * SCALEFACTOR, 0 * SCALEFACTOR, 0 * SCALEFACTOR, 0x2000 * SCALEFACTOR, 0x2000 * SCALEFACTOR, 9, 2, Sprite.ANIMATION_FROZEN, Sprite.SPRITE_ALIGN_DOWN | Sprite.SPRITE_ALIGN_CENTER,
        //MOVE
        0x2000 * SCALEFACTOR, 0x2000 * SCALEFACTOR, 0 * SCALEFACTOR, 0 * SCALEFACTOR, 0x2000 * SCALEFACTOR, 0x2000 * SCALEFACTOR, 4, 1, Sprite.ANIMATION_CYCLIC, Sprite.SPRITE_ALIGN_DOWN | Sprite.SPRITE_ALIGN_CENTER,
        //STAY
        0x2000 * SCALEFACTOR, 0x2000 * SCALEFACTOR, 0 * SCALEFACTOR, 0 * SCALEFACTOR, 0x2000 * SCALEFACTOR, 0x2000 * SCALEFACTOR, 4, 1, Sprite.ANIMATION_CYCLIC, Sprite.SPRITE_ALIGN_DOWN | Sprite.SPRITE_ALIGN_CENTER,
        //FIRE
        0x2000 * SCALEFACTOR, 0x2000 * SCALEFACTOR, 0 * SCALEFACTOR, 0 * SCALEFACTOR, 0x2000 * SCALEFACTOR, 0x2000 * SCALEFACTOR, 4, 2, Sprite.ANIMATION_CYCLIC, Sprite.SPRITE_ALIGN_DOWN | Sprite.SPRITE_ALIGN_CENTER,
        //JUMP
        0 * SCALEFACTOR, 0 * SCALEFACTOR, 0 * SCALEFACTOR, 0 * SCALEFACTOR, 0 * SCALEFACTOR, 0 * SCALEFACTOR, 0, 0, 0, 0,
        //DEATH
        0x2000 * SCALEFACTOR, 0x2000 * SCALEFACTOR, 0 * SCALEFACTOR, 0 * SCALEFACTOR, 0x2000 * SCALEFACTOR, 0x2000 * SCALEFACTOR, 5, 2, Sprite.ANIMATION_CYCLIC, Sprite.SPRITE_ALIGN_DOWN | Sprite.SPRITE_ALIGN_CENTER,
        //UP
        0x2000 * SCALEFACTOR, 0x2000 * SCALEFACTOR, 0 * SCALEFACTOR, 0 * SCALEFACTOR, 0x2000 * SCALEFACTOR, 0x2000 * SCALEFACTOR, 4, 4, Sprite.ANIMATION_CYCLIC, Sprite.SPRITE_ALIGN_DOWN | Sprite.SPRITE_ALIGN_CENTER,
        //DOWN
        0x2000 * SCALEFACTOR, 0x2000 * SCALEFACTOR, 0 * SCALEFACTOR, 0 * SCALEFACTOR, 0x2000 * SCALEFACTOR, 0x2000 * SCALEFACTOR, 4, 4, Sprite.ANIMATION_CYCLIC, Sprite.SPRITE_ALIGN_DOWN | Sprite.SPRITE_ALIGN_CENTER,
        //PICKER
        0x2000 * SCALEFACTOR, 0x2000 * SCALEFACTOR, 0 * SCALEFACTOR, 0 * SCALEFACTOR, 0x2000 * SCALEFACTOR, 0x2000 * SCALEFACTOR, 4, 4, Sprite.ANIMATION_FROZEN, Sprite.SPRITE_ALIGN_DOWN | Sprite.SPRITE_ALIGN_CENTER,
        //FALL
        0x2000 * SCALEFACTOR, 0x2000 * SCALEFACTOR, 0 * SCALEFACTOR, 0 * SCALEFACTOR, 0x2000 * SCALEFACTOR, 0x2000 * SCALEFACTOR, 4, 4, Sprite.ANIMATION_FROZEN, Sprite.SPRITE_ALIGN_DOWN | Sprite.SPRITE_ALIGN_CENTER,

        //HAMMER
        //APPEARANCE
        0x1000 * SCALEFACTOR, 0x1000 * SCALEFACTOR, 0x400 * SCALEFACTOR, 0x400 * SCALEFACTOR, 0x800 * SCALEFACTOR, 0x800 * SCALEFACTOR, 1, 1, Sprite.ANIMATION_FROZEN, Sprite.SPRITE_ALIGN_DOWN | Sprite.SPRITE_ALIGN_CENTER,
        //MOVE
        0x1000 * SCALEFACTOR, 0x1000 * SCALEFACTOR, 0x400 * SCALEFACTOR, 0x400 * SCALEFACTOR, 0x800 * SCALEFACTOR, 0x800 * SCALEFACTOR, 1, 1, Sprite.ANIMATION_CYCLIC, Sprite.SPRITE_ALIGN_DOWN | Sprite.SPRITE_ALIGN_CENTER,
        //STAY
        0x1000 * SCALEFACTOR, 0x1000 * SCALEFACTOR, 0x400 * SCALEFACTOR, 0x400 * SCALEFACTOR, 0x800 * SCALEFACTOR, 0x800 * SCALEFACTOR, 1, 2, Sprite.ANIMATION_FROZEN, Sprite.SPRITE_ALIGN_DOWN | Sprite.SPRITE_ALIGN_CENTER,
        //FIRE
        0x1000 * SCALEFACTOR, 0x1000 * SCALEFACTOR, 0x400 * SCALEFACTOR, 0x400 * SCALEFACTOR, 0x800 * SCALEFACTOR, 0x800 * SCALEFACTOR, 4, 2, Sprite.ANIMATION_FROZEN, Sprite.SPRITE_ALIGN_DOWN | Sprite.SPRITE_ALIGN_CENTER,
        //JUMP
        0 * SCALEFACTOR, 0 * SCALEFACTOR, 0 * SCALEFACTOR, 0 * SCALEFACTOR, 0 * SCALEFACTOR, 0 * SCALEFACTOR, 0, 0, 0, 0,
        //DEATH
        0x1000 * SCALEFACTOR, 0x1000 * SCALEFACTOR, 0 * SCALEFACTOR, 0 * SCALEFACTOR, 0x1000 * SCALEFACTOR, 0x1000 * SCALEFACTOR, 4, 1, Sprite.ANIMATION_FROZEN, Sprite.SPRITE_ALIGN_DOWN | Sprite.SPRITE_ALIGN_CENTER,
        //UP
        0 * SCALEFACTOR, 0 * SCALEFACTOR, 0 * SCALEFACTOR, 0 * SCALEFACTOR, 0 * SCALEFACTOR, 0 * SCALEFACTOR, 0, 0, 0, 0,
        //DOWN
        0 * SCALEFACTOR, 0 * SCALEFACTOR, 0 * SCALEFACTOR, 0 * SCALEFACTOR, 0 * SCALEFACTOR, 0 * SCALEFACTOR, 0, 0, 0, 0,
        //PICKER
        0 * SCALEFACTOR, 0 * SCALEFACTOR, 0 * SCALEFACTOR, 0 * SCALEFACTOR, 0 * SCALEFACTOR, 0 * SCALEFACTOR, 0, 0, 0, 0,
        //FALL
        0x1000 * SCALEFACTOR, 0x1000 * SCALEFACTOR, 0x400 * SCALEFACTOR, 0x400 * SCALEFACTOR, 0x800 * SCALEFACTOR, 0x800 * SCALEFACTOR, 1, 1, Sprite.ANIMATION_FROZEN, Sprite.SPRITE_ALIGN_DOWN | Sprite.SPRITE_ALIGN_CENTER,

        //VESSEL
        //APPEARANCE
        0x1000 * SCALEFACTOR, 0x1000 * SCALEFACTOR, 0x400 * SCALEFACTOR, 0x400 * SCALEFACTOR, 0x800 * SCALEFACTOR, 0x800 * SCALEFACTOR, 5, 3, Sprite.ANIMATION_PENDULUM, Sprite.SPRITE_ALIGN_DOWN | Sprite.SPRITE_ALIGN_CENTER,
        //MOVE
        0 * SCALEFACTOR, 0 * SCALEFACTOR, 0 * SCALEFACTOR, 0 * SCALEFACTOR, 0 * SCALEFACTOR, 0 * SCALEFACTOR, 0, 0, 0, 0,
        //STAY
        0 * SCALEFACTOR, 0 * SCALEFACTOR, 0 * SCALEFACTOR, 0 * SCALEFACTOR, 0 * SCALEFACTOR, 0 * SCALEFACTOR, 0, 0, 0, 0,
        //FIRE
        0 * SCALEFACTOR, 0 * SCALEFACTOR, 0 * SCALEFACTOR, 0 * SCALEFACTOR, 0 * SCALEFACTOR, 0 * SCALEFACTOR, 0, 0, 0, 0,
        //JUMP
        0 * SCALEFACTOR, 0 * SCALEFACTOR, 0 * SCALEFACTOR, 0 * SCALEFACTOR, 0 * SCALEFACTOR, 0 * SCALEFACTOR, 0, 0, 0, 0,
        //DEATH
        0x1000 * SCALEFACTOR, 0x1000 * SCALEFACTOR, 0 * SCALEFACTOR, 0 * SCALEFACTOR, 0x1000 * SCALEFACTOR, 0x1000 * SCALEFACTOR, 4, 3, Sprite.ANIMATION_FROZEN, Sprite.SPRITE_ALIGN_DOWN | Sprite.SPRITE_ALIGN_CENTER,
        //UP
        0 * SCALEFACTOR, 0 * SCALEFACTOR, 0 * SCALEFACTOR, 0 * SCALEFACTOR, 0 * SCALEFACTOR, 0 * SCALEFACTOR, 0, 0, 0, 0,
        //DOWN
        0 * SCALEFACTOR, 0 * SCALEFACTOR, 0 * SCALEFACTOR, 0 * SCALEFACTOR, 0 * SCALEFACTOR, 0 * SCALEFACTOR, 0, 0, 0, 0,
        //PICKER
        0 * SCALEFACTOR, 0 * SCALEFACTOR, 0 * SCALEFACTOR, 0 * SCALEFACTOR, 0 * SCALEFACTOR, 0 * SCALEFACTOR, 0, 0, 0, 0,
        //FALL
        0 * SCALEFACTOR, 0 * SCALEFACTOR, 0 * SCALEFACTOR, 0 * SCALEFACTOR, 0 * SCALEFACTOR, 0 * SCALEFACTOR, 0, 0, 0, 0,

        //PICK
        //APPEARANCE
        0x1000 * SCALEFACTOR, 0x1000 * SCALEFACTOR, 0x400 * SCALEFACTOR, 0x400 * SCALEFACTOR, 0x800 * SCALEFACTOR, 0x800 * SCALEFACTOR, 1, 1, Sprite.ANIMATION_FROZEN, Sprite.SPRITE_ALIGN_DOWN | Sprite.SPRITE_ALIGN_CENTER,
        //MOVE
        0 * SCALEFACTOR, 0 * SCALEFACTOR, 0 * SCALEFACTOR, 0 * SCALEFACTOR, 0 * SCALEFACTOR, 0 * SCALEFACTOR, 0, 0, 0, 0,
        //STAY
        0 * SCALEFACTOR, 0 * SCALEFACTOR, 0 * SCALEFACTOR, 0 * SCALEFACTOR, 0 * SCALEFACTOR, 0 * SCALEFACTOR, 0, 0, 0, 0,
        //FIRE
        0 * SCALEFACTOR, 0 * SCALEFACTOR, 0 * SCALEFACTOR, 0 * SCALEFACTOR, 0 * SCALEFACTOR, 0 * SCALEFACTOR, 0, 0, 0, 0,
        //JUMP
        0 * SCALEFACTOR, 0 * SCALEFACTOR, 0 * SCALEFACTOR, 0 * SCALEFACTOR, 0 * SCALEFACTOR, 0 * SCALEFACTOR, 0, 0, 0, 0,
        //DEATH
        0x1000 * SCALEFACTOR, 0x1000 * SCALEFACTOR, 0 * SCALEFACTOR, 0 * SCALEFACTOR, 0x1000 * SCALEFACTOR, 0x1000 * SCALEFACTOR, 3, 1, Sprite.ANIMATION_FROZEN, Sprite.SPRITE_ALIGN_DOWN | Sprite.SPRITE_ALIGN_CENTER,
        //UP
        0 * SCALEFACTOR, 0 * SCALEFACTOR, 0 * SCALEFACTOR, 0 * SCALEFACTOR, 0 * SCALEFACTOR, 0 * SCALEFACTOR, 0, 0, 0, 0,
        //DOWN
        0 * SCALEFACTOR, 0 * SCALEFACTOR, 0 * SCALEFACTOR, 0 * SCALEFACTOR, 0 * SCALEFACTOR, 0 * SCALEFACTOR, 0, 0, 0, 0,
        //PICKER
        0 * SCALEFACTOR, 0 * SCALEFACTOR, 0 * SCALEFACTOR, 0 * SCALEFACTOR, 0 * SCALEFACTOR, 0 * SCALEFACTOR, 0, 0, 0, 0,
        //FALL
        0 * SCALEFACTOR, 0 * SCALEFACTOR, 0 * SCALEFACTOR, 0 * SCALEFACTOR, 0 * SCALEFACTOR, 0 * SCALEFACTOR, 0, 0, 0, 0
    };

    public static final int MAXSPRITENUMBER = 30;

    public Sprite[] ap_SpriteArray;
    public Sprite p_PlayerSprite;
    public Sprite p_KeySprite;
    public Sprite p_DoorSprite;
    public Sprite p_HammerSprite;

public GameletImpl(int _screenWidth, int _screenHeight, startup _gameActionListener, String _staticArrayResource)
    {
        super(_screenWidth, _screenHeight, _gameActionListener, _staticArrayResource);
    }

    public void resumeGameAfterPlayerLost()
    {
        i_PlayerForce = INITPLAYERFORCE;
        i_PlayerState = PLAYERSTATE_NORMAL;
        initPlayRoom();
        i_PlayerKey = PLAYERKEY_NONE;
    }

    public void initStage(int _stage)
    {
        if (i_GameStage != _stage)
        {
          i_StageAttemptions = i_Attemptions;
          i_StagePlayerForce = i_PlayerForce;
          i_StagePlayerScore = i_PlayerScore;
        }
          else
               {
                  i_Attemptions = i_StageAttemptions;
                  i_PlayerForce = i_StagePlayerForce;
                  i_PlayerScore = i_StagePlayerScore;
               }

        super.initStage(_stage);


        i_stageDiamonds = 0;
        ab_currentStageArray = Stages.getStage(_stage);
        i_currentStageHeight = Stages.getLastStageHeight();
        i_currentStageWidth = Stages.getLastStageWidth();

        deactivateAllSprite();

        p_DoorSprite.lg_SpriteActive = true;
        p_KeySprite.lg_SpriteActive = true;

        int i_spriteCounter = 0;
        i_TheIndexOfLastTakenHammer = -1;
        i_TheIndexOfLastTakenPick = -1;
        p_HammerSprite = null;

        for (int ly = 0; ly < i_currentStageHeight; ly++)
        {
            for (int lx = 0; lx < i_currentStageWidth; lx++)
            {
                int i_offset = ly * i_currentStageWidth + lx;
                int i_element = ab_currentStageArray[i_offset];

                boolean lg_changed = false;

                Sprite p_spr = ap_SpriteArray[i_spriteCounter];

                int i_sprx = VIRTUALBLOCKWIDTH * lx + VIRTUALBLOCKWIDTH;
                int i_spry = VIRTUALBLOCKHEIGHT * (ly + 1);

                p_spr.lg_SpriteActive = true;
                lg_changed = true;


                switch (i_element)
                {
                    case Stages.ELE_HAMMER:
                        {
                            p_spr.lg_SpriteInvisible = false;
                            p_spr.i_ObjectType = OBJECT_HAMMER;

                            i_sprx -= (VIRTUALBLOCKWIDTH>>1);
                            i_spriteCounter++;
                        }
                        ;
                        break;
                    case Stages.ELE_BRILLIANT:
                        {
                            p_spr.lg_SpriteInvisible = false;
                            p_spr.i_ObjectType = OBJECT_DIAMOND;

                            i_sprx -= (VIRTUALBLOCKWIDTH>>1);
                            i_spriteCounter++;
                            i_stageDiamonds++;
                        }
                        ;
                        break;
                    case Stages.ELE_PICK:
                        {
                            p_spr.lg_SpriteInvisible = false;
                            p_spr.i_ObjectType = OBJECT_PICK;

                            i_sprx -= (VIRTUALBLOCKWIDTH>>1);
                            i_spriteCounter++;
                        }
                        ;
                        break;
                    case Stages.ELE_VESSEL:
                        {
                            p_spr.lg_SpriteInvisible = false;
                            p_spr.i_ObjectType = OBJECT_VESSEL;

                            i_sprx -= (VIRTUALBLOCKWIDTH>>1);
                            i_spriteCounter++;
                        }
                        ;
                        break;
                    case Stages.ELE_DOOR:
                        {
                            p_spr = p_DoorSprite;
                            p_spr.lg_SpriteInvisible = true;
                        }
                        ;
                        break;
                    case Stages.ELE_KEY:
                        {
                            p_spr = p_KeySprite;
                            p_spr.lg_SpriteInvisible = true;

                            i_sprx -= (VIRTUALBLOCKWIDTH>>1);
                        }
                        ;
                        break;
                    case Stages.ELE_REDMUMMY:
                        {
                            p_spr.lg_SpriteInvisible = false;
                            p_spr.i_ObjectType = OBJECT_REDMUMMY;

                            i_spriteCounter++;
                        }
                        ;
                        break;
                    case Stages.ELE_YELLOWMUMMY:
                        {
                            p_spr.lg_SpriteInvisible = false;
                            p_spr.i_ObjectType = OBJECT_YELLOWMUMMY;

                            i_spriteCounter++;
                        }
                        ;
                        break;
                    case Stages.ELE_BLACKMUMMY:
                        {
                            p_spr.lg_SpriteInvisible = false;
                            p_spr.i_ObjectType = OBJECT_BLACKMUMMY;

                            i_spriteCounter++;
                        }
                        ;
                        break;
                    case Stages.ELE_PLAYER:
                        {
                            p_spr = p_PlayerSprite;
                        }
                        ;
                        break;
                    default:
                        {
                            p_spr.lg_SpriteActive = false;
                            lg_changed = false;
                        }

                }


                p_spr.i_initMainX = i_sprx;
                p_spr.i_initMainY = i_spry;

                if (lg_changed) ab_currentStageArray[i_offset] = Stages.ELE_EMPTY;

            }
        }

        p_PlayerSprite.i_ObjectType = OBJECT_PLAYER;
        p_KeySprite.i_ObjectType = OBJECT_KEY;
        p_DoorSprite.i_ObjectType = OBJECT_DOOR;

        ab_stageDump = RLEcompress(ab_currentStageArray);

        lg_StageCompleted = false;

        initPlayRoom();
    }

    private void _processMummy(Sprite _obj)
    {
        int i_indxTlX = _obj.i_ScreenX / VIRTUALBLOCKWIDTH;
        int i_indxTlY = _obj.i_ScreenY / VIRTUALBLOCKHEIGHT;

        int i_indxTrX = (_obj.i_ScreenX + _obj.i_width) / VIRTUALBLOCKWIDTH;
        int i_indxDrY = (_obj.i_ScreenY + _obj.i_height) / VIRTUALBLOCKHEIGHT;

        int i_mX = _obj.i_mainX;
        int i_mY = _obj.i_mainY;

        switch (_obj.i_ObjectType)
        {
            case OBJECT_BLACKMUMMY:
                {
                    i_indxDrY = (_obj.i_ScreenY + _obj.i_height - (VIRTUALBLOCKHEIGHT >> 1)) / VIRTUALBLOCKHEIGHT;
                    // Left-Right alghoritm
                    if ((_obj.i_keySet & PLAYERKEY_LEFT) != 0)
                    {
                        // Moving leftward
                        int i_el0 = getElementForXYIndex(i_indxTlX, i_indxDrY);
                        int i_el1 = getElementForXYIndex(i_indxTlX, i_indxDrY + 1);
                        if (i_el0 == Stages.ELE_BOULDER || i_el0 == Stages.ELE_STONE || (i_el1 != Stages.ELE_BOULDER && i_el1 != Stages.ELE_STONE && i_el1 != Stages.ELE_STAIR))
                        {
                            loadStateForSprite(_obj, STATE_MOVE);
                            _obj.lg_MoveLeft = false;
                            _obj.i_keySet &= ~PLAYERKEY_LEFT;
                            _obj.i_keySet |= PLAYERKEY_RIGHT;
                        }
                        else
                            i_mX -= BLACKMUMMYSPEED;
                    }
                    else
                    {
                        // Moving rightward
                        int i_el0 = getElementForXYIndex(i_indxTrX, i_indxDrY);
                        int i_el1 = getElementForXYIndex(i_indxTrX, i_indxDrY + 1);
                        if (i_el0 == Stages.ELE_BOULDER || i_el0 == Stages.ELE_STONE || (i_el1 != Stages.ELE_BOULDER && i_el1 != Stages.ELE_STONE && i_el1 != Stages.ELE_STAIR))
                        {
                            loadStateForSprite(_obj, STATE_MOVE);
                            _obj.lg_MoveLeft = true;
                            _obj.i_keySet &= ~PLAYERKEY_RIGHT;
                            _obj.i_keySet |= PLAYERKEY_LEFT;
                        }
                        else
                            i_mX += BLACKMUMMYSPEED;
                    }
                    _obj.setMainPointXY(i_mX, i_mY);
                }
                ;
                break;
            case OBJECT_REDMUMMY:
                {
                    int i_keySet = _obj.i_keySet;
                    // The terminator alghoritm
                    if (_obj.i_ObjectState == STATE_MOVE)
                    {
                        i_keySet = PLAYERKEY_NONE;
                        int sence_border_start = p_PlayerSprite.i_ScreenX+(p_PlayerSprite.i_col_width>>2);
                        if (i_mX < sence_border_start)
                        {
                            i_keySet |= PLAYERKEY_RIGHT;
                        }
                        else
                        if (i_mX > sence_border_start + (p_PlayerSprite.i_col_width >> 1))
                        {
                            i_keySet |= PLAYERKEY_LEFT;
                        }

                        int i_diff = p_PlayerSprite.i_mainY - i_mY;
                        if (i_diff > VIRTUALBLOCKHEIGHT)
                        {
                            i_keySet |= PLAYERKEY_DOWN;
                        }
                        else if (i_diff < (0 - VIRTUALBLOCKHEIGHT))
                        {
                            i_keySet |= PLAYERKEY_UP;
                        }
                        else
                            i_keySet &= ~(PLAYERKEY_UP | PLAYERKEY_DOWN);

                        if (_obj.i_ObjectState == STATE_MOVE)
                        {
                            if (getRandomInt(40) == 20)
                                i_keySet |= PLAYERKEY_JUMP;
                        }

                    }
                    processSprite(_obj, i_keySet, REDMUMMYSPEED, false);
                }
                ;
                break;

            case OBJECT_YELLOWMUMMY:
                {
                    // The ball alghoritm
                    if ((_obj.i_keySet & PLAYERKEY_LEFT) != 0)
                    {
                        // Leftward
                        if (i_indxTlX == 0)
                        {
                            loadStateForSprite(_obj, STATE_MOVE);
                            _obj.lg_MoveLeft = false;
                            _obj.i_keySet &= ~PLAYERKEY_LEFT;
                            _obj.i_keySet |= PLAYERKEY_RIGHT;
                        }
                        else
                        {
                            i_mX -= YELLOWMUMMYSPEED;
                        }
                    }
                    else
                    {
                        // Rightward
                        if (i_indxTrX == (i_currentStageWidth - 1))
                        {
                            loadStateForSprite(_obj, STATE_MOVE);
                            _obj.lg_MoveLeft = true;
                            _obj.i_keySet &= ~PLAYERKEY_RIGHT;
                            _obj.i_keySet |= PLAYERKEY_LEFT;
                        }
                        else
                        {
                            i_mX += YELLOWMUMMYSPEED;
                        }
                    }

                    if ((_obj.i_keySet & PLAYERKEY_DOWN) != 0)
                    {
                        // Downward
                        if (i_indxDrY == (i_currentStageHeight - 1))
                        {
                            _obj.i_keySet &= ~PLAYERKEY_DOWN;
                            _obj.i_keySet |= PLAYERKEY_UP;
                        }
                        else
                        {
                            i_mY += YELLOWMUMMYSPEED;
                        }
                    }
                    else
                    {
                        if (i_indxTlY == 0)
                        {
                            _obj.i_keySet &= ~PLAYERKEY_UP;
                            _obj.i_keySet |= PLAYERKEY_DOWN;
                        }
                        else
                        {
                            i_mY -= YELLOWMUMMYSPEED;
                        }
                    }
                    _obj.setMainPointXY(i_mX, i_mY);
                }
                ;
                break;
        }
    }

    public void newGameSession(int _level)
    {
        initLevel(_level);

        switch (_level)
        {
            case LEVEL0:
                {
                    i_GameTimeDelay = LEVEL0_TIMEDELAY;
                    i_Attemptions = LEVEL0_ATTEMPTIONS;
                }
                ;
                break;
            case LEVEL1:
                {
                    i_GameTimeDelay = LEVEL1_TIMEDELAY;
                    i_Attemptions = LEVEL1_ATTEMPTIONS;
                }
                ;
                break;
            case LEVEL2:
                {
                    i_GameTimeDelay = LEVEL2_TIMEDELAY;
                    i_Attemptions = LEVEL2_ATTEMPTIONS;
                }
                ;
                break;
        }
        i_PlayerForce = INITPLAYERFORCE;
        ap_SpriteArray = new Sprite[MAXSPRITENUMBER];
        for (int li = 0; li < MAXSPRITENUMBER; li++) ap_SpriteArray[li] = new Sprite(li);
        p_PlayerSprite = new Sprite(-1);
        p_DoorSprite = new Sprite(-1);
        p_KeySprite = new Sprite(-1);

        i_StageAttemptions = i_Attemptions;
        i_StagePlayerForce = i_PlayerForce;
        i_StagePlayerScore = i_PlayerScore;
    }

    public void endGameSession()
    {
        //for (int li = 0; li < MAXSPRITENUMBER; li++) ap_SpriteArray[li] = null;
        ap_SpriteArray=null;
        p_PlayerSprite = null;
        p_KeySprite = null;
        p_DoorSprite = null;

        Runtime.getRuntime().gc();
    }

    private void deactivateAllSprite()
    {
        p_KeySprite.lg_SpriteActive = false;
        p_DoorSprite.lg_SpriteActive = false;
        for (int li = 0; li < MAXSPRITENUMBER; li++)
        {
            ap_SpriteArray[li].lg_SpriteActive = false;
        }
    }

    private void initPlayRoom()
    {
        i_PlayerKey = PLAYERKEY_NONE;

        if (i_TheIndexOfLastTakenHammer >= 0)
        {
            ap_SpriteArray[i_TheIndexOfLastTakenHammer].lg_SpriteInvisible = false;
            i_TheIndexOfLastTakenHammer = -1;
        }

        if (i_TheIndexOfLastTakenPick >= 0)
        {
            ap_SpriteArray[i_TheIndexOfLastTakenPick].lg_SpriteInvisible = false;
            i_TheIndexOfLastTakenPick = -1;
        }

        p_HammerSprite = null;

        // Initing of common sprites
        for (int li = 0; li < MAXSPRITENUMBER; li++)
        {
            Sprite p_spr = ap_SpriteArray[li];
            if (!p_spr.lg_SpriteActive) continue;
            switch (p_spr.i_ObjectType)
            {
                case OBJECT_BLACKMUMMY:
                case OBJECT_REDMUMMY:
                case OBJECT_YELLOWMUMMY:
                    {
                        p_spr.setMainPointXY(p_spr.i_mainX, p_spr.i_mainY);
                        p_spr.lg_SpriteInvisible = false;
                        p_spr.setMainPointXY(p_spr.i_initMainX, p_spr.i_initMainY);
                    }
                    ;
                    break;
                case OBJECT_HAMMER:
                case OBJECT_PICK:
                case OBJECT_DIAMOND:
                case OBJECT_VESSEL:
                    {
                        if (!p_spr.lg_SpriteInvisible)
                        {
                            loadStateForSprite(p_spr, STATE_APPEARANCE);
                            p_spr.setMainPointXY(p_spr.i_initMainX, p_spr.i_initMainY);
                        }
                    }
                    ;
                    break;
            }
            loadStateForSprite(p_spr, STATE_APPEARANCE);
        }
        // Initing of the door sprite
        p_DoorSprite.setMainPointXY(p_DoorSprite.i_initMainX, p_DoorSprite.i_initMainY);
        if (!p_DoorSprite.lg_SpriteInvisible)
        {
            p_DoorSprite.i_Frame = p_DoorSprite.i_maxFrames - 1;
        }

        // Initing of the key sprite
        p_KeySprite.setMainPointXY(p_KeySprite.i_initMainX, p_KeySprite.i_initMainY);

        // Initing of the player
        p_PlayerSprite.setMainPointXY(p_PlayerSprite.i_initMainX, p_PlayerSprite.i_initMainY);
        loadStateForSprite(p_PlayerSprite, STATE_APPEARANCE);
        p_GameActionListener.gameAction(GAMEACTION_SND_PLAYERREADY);
        p_GameActionListener.gameAction(GAMEACTION_NORMAL_MOTION);
    }

    private void alignVertSpriteToTop(Sprite _spr, int _cellY)
    {
        int i_y = _cellY * VIRTUALBLOCKHEIGHT;
        _spr.setMainPointXY(_spr.i_mainX, i_y);
    }

    private void alignHorzSpriteToTop(Sprite _spr, int _cX, boolean _leftEdge)
    {
        int i_x;

        if (_leftEdge)
        {
            i_x = ((_cX + 1) * VIRTUALBLOCKWIDTH + 0x100) - _spr.i_offsetX;
        }
        else
        {
            i_x = (_cX * VIRTUALBLOCKWIDTH - 0x100) + _spr.i_offsetX;
        }
        _spr.setMainPointXY(i_x, _spr.i_mainY);
    }

    private int getElementForXYIndex(int _x, int _y)
    {
        return ab_currentStageArray[_y * i_currentStageWidth + _x];
    }

    private boolean alignSpriteIfNeed(Sprite _spr, boolean _horz, boolean _vert)
    {
        int i8_startPointX = _spr.i_ScreenX + _spr.i_col_offsetX;

        int i_sX = i8_startPointX / VIRTUALBLOCKWIDTH;
        int i_eX = (i8_startPointX + _spr.i_col_width) / VIRTUALBLOCKWIDTH;
        int i_eY;

        boolean lg_result = false;
        boolean lg_processing;
        int i_iteration = 2;

        do {

           lg_processing = false;
           i_eY = (_spr.i_ScreenY + _spr.i_col_offsetY + _spr.i_col_height - 0x100) / VIRTUALBLOCKHEIGHT;

           if (_horz)
           {
              int i_elem = getElementForXYIndex(i_sX, i_eY);
              if (i_elem == Stages.ELE_STONE || i_elem == Stages.ELE_BOULDER)
              {
                  alignHorzSpriteToTop(_spr, i_sX, true);
                  lg_processing = lg_result = true;

              }
              else
              {
                  i_elem = getElementForXYIndex(i_eX, i_eY);
                  if (i_elem == Stages.ELE_STONE || i_elem == Stages.ELE_BOULDER)
                  {
                      alignHorzSpriteToTop(_spr, i_eX, false);
                      lg_processing = lg_result = true;
                  }
              }
           }


           if (_vert)
           {
              if ( lg_processing )
              {
                  i_sX = (_spr.i_ScreenX + _spr.i_col_offsetX + 0x100) / VIRTUALBLOCKWIDTH;
                  i_eX = (_spr.i_ScreenX + +_spr.i_col_offsetX + _spr.i_col_width - 0x100) / VIRTUALBLOCKWIDTH;
                  lg_processing = false;
              }
              int i_mX = _spr.i_mainX / VIRTUALBLOCKWIDTH;

              int i_elem0 = getElementForXYIndex(i_sX, i_eY);
              int i_elem1 = getElementForXYIndex(i_mX, i_eY);
              int i_elem2 = getElementForXYIndex(i_eX, i_eY);

              if ((i_elem0 == Stages.ELE_STAKES || i_elem0 == Stages.ELE_STONE || i_elem0 == Stages.ELE_BOULDER) || (i_elem1 == Stages.ELE_STAKES || i_elem1 == Stages.ELE_STONE || i_elem1 == Stages.ELE_BOULDER) || (i_elem2 == Stages.ELE_STAKES || i_elem2 == Stages.ELE_STONE || i_elem2 == Stages.ELE_BOULDER))
              {
                  alignVertSpriteToTop(_spr, i_eY);
                  lg_processing = lg_result = true;
              }
           }
        } while ( lg_processing && --i_iteration>0);
        return lg_result;
    }

    // Return true if object has completed animation phase
    private boolean processSprite(Sprite _obj, int _keySet, int _horzSpeed, boolean _flyingObject)
    {
        boolean lg_processCompleted = _obj.processAnimation();

        int i_oMainX = _obj.i_mainX;
        int i_oMainY = _obj.i_mainY;

        switch (_obj.i_ObjectState)
        {
            case STATE_DEATH:
                {
                    _obj.setMainPointXY(_obj.i_mainX, _obj.i_mainY + GRAVITYCONSTANT);
                    alignSpriteIfNeed(_obj, false, true);
                }
                ;
                break;
            case STATE_FALL:
                {
                    _obj.setMainPointXY(_obj.i_mainX, _obj.i_mainY + GRAVITYCONSTANT);

                    if (alignSpriteIfNeed(_obj, false, true))
                    {
                        if (_obj.i_ObjectState != STATE_DEATH)
                        {
                            if (_flyingObject)
                                loadStateForSprite(_obj, STATE_APPEARANCE);
                              else
                                 loadStateForSprite(_obj, STATE_MOVE);
                        }
                    }
                }
                ;
                break;
            case STATE_APPEARANCE:
                {
                    if (lg_processCompleted)
                    {
                        if ((_keySet & PLAYERKEY_LEFT) != 0)
                        {
                            loadStateForSprite(_obj, STATE_MOVE);
                            _obj.lg_MoveLeft = true;
                        }
                        else if ((_keySet & PLAYERKEY_RIGHT) != 0)
                        {
                            loadStateForSprite(_obj, STATE_MOVE);
                            _obj.lg_MoveLeft = false;
                        }
                        else
                        {
                                int i_indxY = (_obj.i_mainY - (VIRTUALBLOCKHEIGHT >> 1)) / VIRTUALBLOCKHEIGHT;
                                int i_indxXs = (_obj.i_ScreenX + _obj.i_col_offsetX) / VIRTUALBLOCKWIDTH;
                                int i_indxXe = (_obj.i_ScreenX + _obj.i_col_offsetX + _obj.i_col_width) / VIRTUALBLOCKWIDTH;
                                int i_indxXm = _obj.i_mainX / VIRTUALBLOCKWIDTH;

                                i_indxY++;

                                if (!_flyingObject)
                                {
                                    int i_elem0 = getElementForXYIndex(i_indxXm, i_indxY);
                                    int i_elem1 = getElementForXYIndex(i_indxXs, i_indxY);
                                    int i_elem2 = getElementForXYIndex(i_indxXe, i_indxY);

                                    if (!(i_elem0 == Stages.ELE_STONE || i_elem0 == Stages.ELE_BOULDER) && !(i_elem1 == Stages.ELE_STONE || i_elem1 == Stages.ELE_BOULDER) && !(i_elem2 == Stages.ELE_STONE || i_elem2 == Stages.ELE_BOULDER))
                                    {
                                        loadStateForSprite(_obj, STATE_FALL);
                                    }
                                }
                        }
                    }
                }
                ;
                break;
            case STATE_UP:
                {
                    if ((i_insideTimer & 0x1) != 0) break;

                    if ((_keySet & PLAYERKEY_DOWN) != 0)
                    {
                        _obj.lg_MoveLeft = !_obj.lg_MoveLeft;
                        loadStateForSprite(_obj, STATE_DOWN);
                    }
                    else
                    {
                        int i_dY = (i_oMainY - (VIRTUALBLOCKHEIGHT >> 1)) / VIRTUALBLOCKHEIGHT;
                        int i_dX = i_oMainX / VIRTUALBLOCKWIDTH;
                        if (_obj.lg_MoveLeft)
                        {
                            i_dX -= 1;
                            i_oMainX -= VIRTUALBLOCKWIDTH;
                        }
                        else
                        {
                            i_dX += 1;
                            i_oMainX += VIRTUALBLOCKWIDTH;
                        }

                        int i_elem = getElementForXYIndex(i_dX, i_dY);

                        if (i_elem != Stages.ELE_STAIR)
                        {
                            loadStateForSprite(_obj, STATE_MOVE);
                            p_GameActionListener.gameAction(GAMEACTION_NORMAL_MOTION);
                        }
                        else
                        {
                            _obj.setMainPointXY(i_oMainX, i_oMainY - VIRTUALBLOCKHEIGHT);
                        }
                    }
                }
                ;
                break;
            case STATE_DOWN:
                {
                    if ((i_insideTimer & 0x1) != 0) break;

                    if ((_keySet & PLAYERKEY_UP) != 0)
                    {
                        _obj.lg_MoveLeft = !_obj.lg_MoveLeft;
                        loadStateForSprite(_obj, STATE_UP);
                    }
                    else
                    {
                        int i_dY = (i_oMainY - (VIRTUALBLOCKHEIGHT >> 1)) / VIRTUALBLOCKHEIGHT;
                        int i_dX = i_oMainX / VIRTUALBLOCKWIDTH;

                        int i_elem = getElementForXYIndex(i_dX, i_dY + 1);

                        if (i_elem == Stages.ELE_STONE || i_elem == Stages.ELE_BOULDER)
                        {
                            loadStateForSprite(_obj, STATE_MOVE);
                            p_GameActionListener.gameAction(GAMEACTION_NORMAL_MOTION);
                        }
                        else
                        {
                            if (_obj.lg_MoveLeft)
                            {
                                i_dX -= 1;
                                i_oMainX -= VIRTUALBLOCKWIDTH;
                            }
                            else
                            {
                                i_dX += 1;
                                i_oMainX += VIRTUALBLOCKWIDTH;
                            }

                            _obj.setMainPointXY(i_oMainX, i_oMainY + VIRTUALBLOCKHEIGHT);
                        }
                    }
                }
                ;
                break;
            case STATE_STAY:
                             _horzSpeed = 0;
            case STATE_MOVE:
                {
                    if ((_keySet & PLAYERKEY_JUMP) != 0)
                    {
                        int i_cx = _obj.i_mainX;

                        if (_obj.lg_MoveLeft)
                            i_cx -= JUMPLEN;
                        else
                            i_cx += JUMPLEN;

                        _obj.i_JumpAngle = 0;
                        _obj.i_JumpMainX = i_cx;
                        _obj.i_JumpMainY = _obj.i_mainY;

                        loadStateForSprite(_obj, STATE_JUMP);
                    }
                    else if ((_keySet & PLAYERKEY_RIGHT) != 0 && _obj.lg_MoveLeft)
                    {
                        loadStateForSprite(_obj, STATE_MOVE);
                        _obj.lg_MoveLeft = false;
                    }
                    else if ((_keySet & PLAYERKEY_LEFT) != 0 && !_obj.lg_MoveLeft)
                    {
                        loadStateForSprite(_obj, STATE_MOVE);
                        _obj.lg_MoveLeft = true;
                    }
                    else
                    {
                        int i_indxX = _obj.i_mainX / VIRTUALBLOCKWIDTH;
                        int i_indxY = (_obj.i_mainY - (VIRTUALBLOCKHEIGHT >> 1)) / VIRTUALBLOCKHEIGHT;
                        int i_elem = getElementForXYIndex(i_indxX, i_indxY);

                        int i_ix = i_indxX * VIRTUALBLOCKWIDTH + (VIRTUALBLOCKWIDTH >> 1);

                        if ((_keySet & PLAYERKEY_UP) != 0 && i_elem == Stages.ELE_STAIR)
                        {
                            _obj.lg_MoveLeft = (getElementForXYIndex(i_indxX - 1, i_indxY - 1)
                                                    == Stages.ELE_STAIR);
                            loadStateForSprite(_obj, STATE_UP);
                     p_GameActionListener.gameAction(GAMEACTION_SLOW_MOTION);
                            _obj.setMainPointXY(i_ix, i_oMainY - VIRTUALBLOCKHEIGHT);
                        }
                        else
                        {
                            i_elem = getElementForXYIndex(i_indxX, i_indxY + 1);

                            if ((_keySet & PLAYERKEY_DOWN) != 0 && i_elem == Stages.ELE_STAIR)
                            {
                                _obj.lg_MoveLeft = (getElementForXYIndex(i_indxX - 1, i_indxY + 2)
                                                       == Stages.ELE_STAIR);
                                loadStateForSprite(_obj, STATE_DOWN);
                                p_GameActionListener.gameAction(GAMEACTION_SLOW_MOTION);
                                _obj.setMainPointXY(i_ix, i_oMainY);
                            }
                            else
                            {
                                int i_startPoint = _obj.i_ScreenX + _obj.i_col_offsetX;
                                int i_indxXs = i_startPoint / VIRTUALBLOCKWIDTH;
                                int i_indxXe = (i_startPoint + _obj.i_col_width) / VIRTUALBLOCKWIDTH;
                                int i_eY = (_obj.i_ScreenY + _obj.i_col_offsetY + _obj.i_col_height - 0x100) / VIRTUALBLOCKHEIGHT;

                                if (_obj.lg_MoveLeft)
                                {
                                    i_elem = getElementForXYIndex(i_indxXs-1, i_eY);
                                    if (i_elem == Stages.ELE_STONE || i_elem == Stages.ELE_BOULDER)
                                          _horzSpeed = Math.min(i_startPoint % VIRTUALBLOCKWIDTH - BORDER_GAP
                                                                , _horzSpeed);
                                    _horzSpeed = - _horzSpeed;
                                }
                                  else
                                       {
                                         i_elem = getElementForXYIndex(i_indxXe+1, i_eY);
                                         if (i_elem == Stages.ELE_STONE || i_elem == Stages.ELE_BOULDER)
                                             _horzSpeed = Math.min(VIRTUALBLOCKWIDTH - (i_startPoint + _obj.i_col_width) % VIRTUALBLOCKWIDTH - BORDER_GAP
                                                                   , _horzSpeed);
                                       }

                                _obj.setMainPointXY(_obj.i_mainX + _horzSpeed, _obj.i_mainY);
                                boolean lg_horz = alignSpriteIfNeed(_obj, true, false);

                                i_indxY++;

                                if (_flyingObject)
                                {
                                    if (lg_horz || alignSpriteIfNeed(_obj, false, true) || _horzSpeed==0)
                                    {
                                        loadStateForSprite(_obj, STATE_FALL);
                                    }
                                }
                                else
                                {
                                    int i_elem0 = getElementForXYIndex(i_indxX, i_indxY);
                                    int i_elem1 = getElementForXYIndex(i_indxXs, i_indxY);
                                    int i_elem2 = getElementForXYIndex(i_indxXe, i_indxY);

                                    if (!(i_elem0 == Stages.ELE_STONE || i_elem0 == Stages.ELE_BOULDER) && !(i_elem1 == Stages.ELE_STONE || i_elem1 == Stages.ELE_BOULDER) && !(i_elem2 == Stages.ELE_STONE || i_elem2 == Stages.ELE_BOULDER))
                                    {
                                        loadStateForSprite(_obj, STATE_FALL);
                                    }
                                     else
                                       if (_horzSpeed == 0 && _obj == p_PlayerSprite
                                              && _obj.i_ObjectState != STATE_STAY)
                                       {
                                            loadStateForSprite(_obj, STATE_STAY);
                                       }
                                }
                            }
                        }
                    }
                }
                ;
                break;
            case STATE_JUMP:
                {
                    _obj.i_JumpAngle += JUMPSTEP;
                    if (_obj.i_JumpAngle >= 0x2400)
                    {
                        loadStateForSprite(_obj, STATE_FALL);
                    }
                    else
                    {
                        int i_delta = (_obj.i_JumpAngle >>> 8) + 32;
                        i_delta &= 63;
                        int i_cx = _obj.i_JumpMainX;
                        int i_cy = _obj.i_JumpMainY;

                        if (_obj.lg_MoveLeft)
                            i_cx = i_cx - xCoSine(JUMPLEN, i_delta);
                        else
                            i_cx = i_cx + xCoSine(JUMPLEN, i_delta);

                        i_cy = i_cy + xSine(JUMPLEN, i_delta);

//------------------------------------

                        int i_xL=-1, i_xR=-1, i_xF=-1;
                        int i_startPoint;
                        int i_yF = (i_cy + _obj.i_offsetY + _obj.i_col_offsetY + _obj.i_col_height - 0x100) / VIRTUALBLOCKHEIGHT;
                        boolean lg_F_low = false, lg_F_high = false;
                        boolean lg_M_low = false, lg_M_high = true;
                        int i_e, i_dist = BORDER_GAP;

                        if(i_yF>1)
                        {

                           i_startPoint = _obj.i_ScreenX + _obj.i_col_offsetX;
                           i_xL = i_startPoint / VIRTUALBLOCKWIDTH;
                           i_xR = (i_startPoint + _obj.i_col_width - 0x100) / VIRTUALBLOCKWIDTH;

                           i_xF = _obj.lg_MoveLeft ? i_xL-1 : i_xR+1 ;



                           // collect information

                           lg_F_low = checkPassage(i_xF,i_yF);    // check border point
                           lg_M_low = checkPassage(i_xL,i_yF)
                                      || checkPassage(i_xR,i_yF); // check inner point
                           i_yF--;                                // preparing to check top points

                           lg_F_high = checkPassage(i_xF,i_yF);    // check border point
                           lg_M_high = checkPassage(i_xL,i_yF)
                                      || checkPassage(i_xR,i_yF); // check inner point

                          if (!lg_M_high || !lg_M_low)
                          {
                            // block horizontal motion
                            if (lg_F_low && lg_F_high)
                            {
                               int i_dx = Math.abs(_obj.i_mainX - i_cx);
                                    if (_obj.lg_MoveLeft)
                                    {
                                            i_cx = _obj.i_mainX -
                                                        Math.min(i_startPoint % VIRTUALBLOCKWIDTH - BORDER_GAP
                                                                 , i_dx);
                                    }
                                      else
                                           {
                                               i_cx = _obj.i_mainX +
                                                            Math.min(VIRTUALBLOCKWIDTH - (i_startPoint + _obj.i_col_width) % VIRTUALBLOCKWIDTH - BORDER_GAP
                                                                     , i_dx);
                                           }
                            }



                         }


                           i_startPoint = i_cx + _obj.i_offsetX + _obj.i_col_offsetX;
                           i_xL = i_startPoint / VIRTUALBLOCKWIDTH;
                           i_xR = (i_startPoint + _obj.i_col_width - 0x100) / VIRTUALBLOCKWIDTH;

                           i_xF = _obj.lg_MoveLeft ? i_xL-1 : i_xR+1 ;


                           // collect information
                           i_yF++;                                // preparing to check bottom points

                           lg_F_low = checkPassage(i_xF,i_yF);    // check border point
                           lg_M_low = checkPassage(i_xL,i_yF)
                                      || checkPassage(i_xR,i_yF); // check inner point
                           i_yF--;                                // preparing to check top points

                           lg_F_high = checkPassage(i_xF,i_yF);    // check border point
                           lg_M_high = checkPassage(i_xL,i_yF)
                                      || checkPassage(i_xR,i_yF); // check inner point

                        }

//------------------------------------

                        _obj.setMainPointXY(i_cx, i_cy);


                        if(lg_F_low && lg_F_high && (i_dist>>8)<1)
                        {
                            alignSpriteIfNeed(_obj, true, !(lg_M_low && lg_M_high));
                            loadStateForSprite(_obj, STATE_FALL);
                        }
                         else

                        if(lg_M_low && !lg_M_high){
                            alignVertSpriteToTop(_obj, i_yF+1);
                            loadStateForSprite(_obj, STATE_FALL);
                        }
                         else

                        if (alignSpriteIfNeed(_obj, true, !(lg_M_low && lg_M_high))
//                        if (alignSpriteIfNeed(_obj, true, true)
//                            || (lg_F_high && lg_F_low)
                        )
                        {
                            loadStateForSprite(_obj, STATE_FALL);
                        }
                    }
                }
                ;
                break;
        }

        return lg_processCompleted;
    }

    // Return true if the player is dead
    private boolean processPlayer()
    {
        if (i_PlayerForce <= 0)
        {
            i_PlayerForce = 0;
            if (p_PlayerSprite.i_ObjectState != STATE_DEATH)
            {
                p_GameActionListener.gameAction(GAMEACTION_SND_PLAYERDEATH);
                loadStateForSprite(p_PlayerSprite, STATE_DEATH);
                return false;
            }
        }

        boolean lg_animeCompleted = false;
        if (p_PlayerSprite.i_ObjectState != STATE_DEATH)
        {
            int i_oldPLayerState = p_PlayerSprite.i_ObjectState;
            lg_animeCompleted = processSprite(p_PlayerSprite, i_PlayerKey, PLAYER_HORZSPEED, false);
            if (i_oldPLayerState != p_PlayerSprite.i_ObjectState)
            {
                i_PlayerKey = PLAYERKEY_NONE;
            }
        }
        else
        {
            lg_animeCompleted = processSprite(p_PlayerSprite, PLAYERKEY_NONE, PLAYER_HORZSPEED, false);
            //lg_animeCompleted = p_PlayerSprite.processAnimation();
        }

        switch (p_PlayerSprite.i_ObjectState)
        {
            case STATE_FIRE:
                {
                    if (lg_animeCompleted)
                    {
                        i_PlayerKey = PLAYERKEY_NONE;
                        {
                            int i_hX;
                            int i_hY = p_PlayerSprite.i_mainY;
                            loadStateForSprite(p_HammerSprite, STATE_MOVE);
                            if (p_PlayerSprite.lg_MoveLeft)
                            {
                                p_HammerSprite.lg_MoveLeft = true;
                                i_hX = p_PlayerSprite.i_mainX - 0x1000;
                            }
                            else
                            {
                                p_HammerSprite.lg_MoveLeft = false;
                                i_hX = p_PlayerSprite.i_mainX + (0x1000>>1);
                            }
                            p_HammerSprite.setMainPointXY(i_hX, i_hY - 0x1000);
                            i_TheIndexOfLastTakenHammer = -1;
                            p_HammerSprite.lg_SpriteInvisible = false;
                            loadStateForSprite(p_PlayerSprite, STATE_MOVE);
                        }
                    }
                }
                ;
                break;
            case STATE_PICKER:
                {
                    if (lg_animeCompleted)
                    {
                        i_PlayerKey = PLAYERKEY_NONE;
                        {
                            // Using of the pick
                            int i_indxX = p_PlayerSprite.i_mainX / VIRTUALBLOCKWIDTH;
                            int i_indxY = (p_PlayerSprite.i_mainY + (VIRTUALBLOCKHEIGHT >> 1)) / VIRTUALBLOCKHEIGHT;

                            if (ab_currentStageArray[i_indxX + i_indxY * i_currentStageWidth] == Stages.ELE_STONE)
                            {
                                ab_currentStageArray[i_indxX + i_indxY * i_currentStageWidth] = Stages.ELE_EMPTY;
                                loadStateForSprite(p_PlayerSprite, STATE_MOVE);
                                i_TheIndexOfLastTakenPick = -1;
                                p_GameActionListener.gameAction(GAMEACTION_ARRAY_CHANGED, i_indxX, i_indxY);
                            }
                            else
                            {
                                loadStateForSprite(p_PlayerSprite, STATE_MOVE);
                            }

                        }
                    }
                }
                ;
                break;
            case STATE_FALL:
                {
                    int i_indxXs = (p_PlayerSprite.i_ScreenX + p_PlayerSprite.i_col_offsetX) / VIRTUALBLOCKWIDTH;
                    int i_indxXe = (p_PlayerSprite.i_ScreenX + p_PlayerSprite.i_col_offsetX + p_PlayerSprite.i_col_width) / VIRTUALBLOCKWIDTH;
                    int i_indxXm = p_PlayerSprite.i_mainX / VIRTUALBLOCKWIDTH;
                    int i_indxY = (p_PlayerSprite.i_mainY + 0x100) / VIRTUALBLOCKHEIGHT;

                    if (getElementForXYIndex(i_indxXm, i_indxY) == Stages.ELE_STAKES ||
                            getElementForXYIndex(i_indxXs, i_indxY) == Stages.ELE_STAKES ||
                            getElementForXYIndex(i_indxXe, i_indxY) == Stages.ELE_STAKES)
                    {
                        alignSpriteIfNeed(p_PlayerSprite, false, true);
                        i_PlayerForce = 0;
                    }
                }
                ;
                break;
            case STATE_STAY:
            case STATE_MOVE:
                {
                    if ((i_PlayerKey & PLAYERKEY_FIRE) != 0)
                    {
                      if (i_TheIndexOfLastTakenHammer >= 0)
                        {
                            loadStateForSprite(p_PlayerSprite, STATE_FIRE);
                            p_GameActionListener.gameAction(GAMEACTION_SND_HAMMERUSED);
                        }
                      if (i_TheIndexOfLastTakenPick >= 0)
                        {
                            loadStateForSprite(p_PlayerSprite, STATE_PICKER);
                            p_GameActionListener.gameAction(GAMEACTION_SND_PICKUSED);
                        }
                    }
                }
            case STATE_APPEARANCE:
                {
                    int i_indxXs = (p_PlayerSprite.i_ScreenX + p_PlayerSprite.i_col_offsetX) / VIRTUALBLOCKWIDTH;
                    int i_indxXe = (p_PlayerSprite.i_ScreenX + p_PlayerSprite.i_col_offsetX + p_PlayerSprite.i_col_width) / VIRTUALBLOCKWIDTH;
                    int i_indxXm = p_PlayerSprite.i_mainX / VIRTUALBLOCKWIDTH;
                    int i_indxY = (p_PlayerSprite.i_mainY - (VIRTUALBLOCKHEIGHT >> 1)) / VIRTUALBLOCKHEIGHT + 1;

                    if (getElementForXYIndex(i_indxXm, i_indxY) == Stages.ELE_STAKES ||
                            getElementForXYIndex(i_indxXs, i_indxY) == Stages.ELE_STAKES ||
                            getElementForXYIndex(i_indxXe, i_indxY) == Stages.ELE_STAKES)
                    {
                        i_PlayerForce = 0;
                    }
                }
                ;
                break;
            case STATE_DEATH:
                if (lg_animeCompleted) return true;
        }

        return false;
    }

    private void processCommonObjects()
    {
        for (int li = 0; li < MAXSPRITENUMBER; li++)
        {
            Sprite p_spr = ap_SpriteArray[li];
            if (!p_spr.lg_SpriteActive) continue;

            if (p_spr.lg_SpriteInvisible)
            {
                switch (p_spr.i_ObjectType)
                {
                    case OBJECT_BLACKMUMMY:
                    case OBJECT_REDMUMMY:
                    case OBJECT_YELLOWMUMMY:
                        {
                            if ((--p_spr.i_Frame) <= 0)
                            {
                                p_spr.setMainPointXY(p_spr.i_initMainX, p_spr.i_initMainY);
                                loadStateForSprite(p_spr, STATE_APPEARANCE);
                                p_spr.lg_SpriteInvisible = false;
                                p_GameActionListener.gameAction(GAMEACTION_SND_MUMMYREADY);
                            }
                        }
                        ;
                        break;
                }
            }
            else
            {
                boolean lg_PlayerCollided = false;
                if (p_PlayerSprite.i_ObjectState != STATE_DEATH)
                {
                    lg_PlayerCollided = p_spr.isCollided(p_PlayerSprite);
                }

                switch (p_spr.i_ObjectType)
                {
                    case OBJECT_PICK:
                        {
                            boolean lg_AnimationResult = p_spr.processAnimation();
                            if (p_spr.i_ObjectState == STATE_DEATH)
                            {
                                if (lg_AnimationResult)
                                {
                                    p_spr.lg_SpriteInvisible = true;
                                }
                            }
                            else
                            {
                              if (i_TheIndexOfLastTakenHammer >= 0 || i_TheIndexOfLastTakenPick >= 0) continue;
                              if (lg_PlayerCollided)
                              {
                                loadStateForSprite(p_spr, STATE_DEATH);
                                i_TheIndexOfLastTakenPick = li;
                                p_GameActionListener.gameAction(GAMEACTION_SND_PICKTAKEN);
                              }
                            }
                        }
                        ;
                        break;
                    case OBJECT_HAMMER:
                        {
                            switch (p_spr.i_ObjectState)
                            {
                                case STATE_DEATH:
                                    {
                                        boolean lg_AnimationResult = p_spr.processAnimation();
                                        if (lg_AnimationResult)
                                        {
                                            p_spr.lg_SpriteInvisible = true;
                                            p_HammerSprite = p_spr;
                                        }
                                    }
                                    ;
                                    break;
                                case STATE_MOVE:
                                    {
                                        if(p_spr.lg_MoveLeft)
                                        {
                                            processSprite(p_spr, PLAYERKEY_LEFT, HAMMER_HORZSPEED, true);
                                            if (p_spr.i_ObjectState == STATE_APPEARANCE || p_spr.i_ObjectState == STATE_FALL)
                                            {
                                               p_GameActionListener.gameAction(GAMEACTION_SND_HAMMERDEACTIVATED);
                                               p_HammerSprite = null;
                                            }
                                        }
                                        else
                                        {
                                            processSprite(p_spr, PLAYERKEY_RIGHT, HAMMER_HORZSPEED, true);
                                            if (p_spr.i_ObjectState == STATE_APPEARANCE || p_spr.i_ObjectState == STATE_FALL)
                                            {
                                               p_GameActionListener.gameAction(GAMEACTION_SND_HAMMERDEACTIVATED);
                                               p_HammerSprite = null;
                                            }
                                        }
                                    }
                                    ;
                                    break;
                                case STATE_FIRE:
                                    {
                                        if (p_spr.processAnimation())  p_spr.lg_SpriteInvisible = true;
                                    }
                                    ;
                                    break;
                                case STATE_APPEARANCE:
                                    {
                                        p_spr.processAnimation();
                                        if (i_TheIndexOfLastTakenHammer >= 0 || i_TheIndexOfLastTakenPick >= 0 || p_HammerSprite != null) continue;
                                        if (lg_PlayerCollided)
                                        {
                                            loadStateForSprite(p_spr, STATE_DEATH);
                                            i_TheIndexOfLastTakenHammer = li;
                                            p_GameActionListener.gameAction(GAMEACTION_SND_HAMMERTAKEN);
                                        }
                                    }
                                    ;
                                    break;
                                default :
                                    processSprite(p_spr, PLAYERKEY_NONE, 0, true);
                            }
                        }
                        ;
                        break;
                    case OBJECT_DIAMOND:
                        {
                            boolean lg_AnimationResult = p_spr.processAnimation();
                            if (p_spr.i_ObjectState == STATE_DEATH)
                            {
                                if (lg_AnimationResult)
                                {
                                    p_spr.lg_SpriteInvisible = true;
                                    i_stageDiamonds--;
                                    i_PlayerScore += SCORES_DIAMOND;
                                }

                                if (i_stageDiamonds == 0)
                                {
                                    p_KeySprite.lg_SpriteInvisible = false;
                                    loadStateForSprite(p_KeySprite, STATE_APPEARANCE);
                                    p_GameActionListener.gameAction(GAMEACTION_SND_KEYSHOWED);
                                }
                            }
                            else if (lg_PlayerCollided)
                            {
                                loadStateForSprite(p_spr, STATE_DEATH);
                                p_GameActionListener.gameAction(GAMEACTION_SND_DIAMONDTAKEN);
                            }
                        }
                        ;
                        break;
                    case OBJECT_VESSEL:
                        {
                            boolean lg_AnimationResult = p_spr.processAnimation();
                            if (p_spr.i_ObjectState == STATE_DEATH)
                            {
                                if (lg_AnimationResult)
                                {
                                    p_spr.lg_SpriteInvisible = true;
                                    i_PlayerForce = INITPLAYERFORCE;
                                }
                            }
                            else if (lg_PlayerCollided && i_PlayerForce < INITPLAYERFORCE)
                            {
                                loadStateForSprite(p_spr, STATE_DEATH);
                                p_GameActionListener.gameAction(GAMEACTION_SND_VESSELTAKEN);
                            }
                        }
                        ;
                        break;
                    case OBJECT_REDMUMMY:
                        {
                            if (!
                                 (i_TheIndexOfLastTakenHammer >= 0 || p_HammerSprite == null
                                  || p_spr.i_ObjectState == STATE_APPEARANCE
                                  || p_spr.i_ObjectState == STATE_DEATH)
                               )
                            {
                                if (p_spr.isCollided(p_HammerSprite))
                                {
                                    loadStateForSprite(p_spr, STATE_DEATH);
                                    loadStateForSprite(p_HammerSprite, STATE_FIRE);
                                    p_HammerSprite.setMainPointXY(
                                          p_spr.i_ScreenX +  p_spr.i_col_offsetX + (p_HammerSprite.lg_MoveLeft ? p_spr.i_col_width-(p_HammerSprite.i_col_width>>1):(p_HammerSprite.i_col_width>>1)),
                                          p_HammerSprite.i_mainY //+ (p_HammerSprite.i_col_height>>1)
                                      );
                                    i_PlayerScore += SCORES_REDMUMMY;
                                    p_HammerSprite = null;
                                    p_GameActionListener.gameAction(GAMEACTION_SND_MUMMYKILLED);
                                    continue;
                                }
                            }

                            boolean lg_anime = p_spr.processAnimation();
                            switch (p_spr.i_ObjectState)
                            {
                                case STATE_APPEARANCE:
                                    {
                                        //boolean lg_anime = p_spr.processAnimation();
                                        if (lg_anime)
                                        {
                                            loadStateForSprite(p_spr, STATE_MOVE);
                                            p_spr.lg_MoveLeft = true;
                                            p_spr.i_keySet = PLAYERKEY_LEFT;
                                        }
                                    }
                                    ;
                                    break;
                                case STATE_DEATH:
                                    {
                                        //boolean lg_anime = p_spr.processAnimation();
                                        if (lg_anime)
                                        {
                                            p_spr.lg_SpriteInvisible = true;
                                            p_spr.i_Frame = REDMUMMYDELAYTICKS;
                                        }
                                    }
                                    ;
                                    break;
                                case STATE_FIRE:
                                    {
                                        //boolean lg_anime = p_spr.processAnimation();
                                        if (lg_anime)
                                        {
                                            loadStateForSprite(p_spr, STATE_MOVE);
                                            if (lg_PlayerCollided)
                                            {
                                                i_PlayerForce -= REDMUMMYSTRIKE;
                                            }
                                        }
                                    }
                                    ;
                                    break;
                                case STATE_STAY:
                                case STATE_MOVE:
                                    {
                                        if (lg_PlayerCollided)
                                        {
                                            p_spr.lg_MoveLeft = (p_PlayerSprite.i_mainX < p_spr.i_mainX);
                                            loadStateForSprite(p_spr, STATE_FIRE);
                                            p_GameActionListener.gameAction(GAMEACTION_SND_MUMMYATTACK);
                                        }
                                        else
                                        {
                                            _processMummy(p_spr);
                                        }
                                    }
                                    ;
                                    break;
                                case STATE_JUMP:
                                case STATE_UP:
                                case STATE_DOWN:
                                case STATE_FALL:
                                    {
                                        _processMummy(p_spr);
                                    }
                                    ;
                                    break;
                            }
                        }
                        ;
                        break;
                    case OBJECT_YELLOWMUMMY:
                        {
                            if (!
                                 (i_TheIndexOfLastTakenHammer >= 0 || p_HammerSprite == null
                                  || p_spr.i_ObjectState == STATE_APPEARANCE
                                  || p_spr.i_ObjectState == STATE_DEATH)
                               )
                            {
                                if (p_spr.isCollided(p_HammerSprite))
                                {
                                    loadStateForSprite(p_spr, STATE_DEATH);
                                    loadStateForSprite(p_HammerSprite, STATE_FIRE);
                                    p_HammerSprite.setMainPointXY(
                                          p_spr.i_ScreenX +  p_spr.i_col_offsetX + (p_HammerSprite.lg_MoveLeft ? p_spr.i_col_width-(p_HammerSprite.i_col_width>>1):(p_HammerSprite.i_col_width>>1)),
                                          p_HammerSprite.i_mainY //+ (p_HammerSprite.i_col_height>>1)
                                      );
                                    i_PlayerScore += SCORES_YELLOWMUMMY;
                                    p_HammerSprite = null;
                                    p_GameActionListener.gameAction(GAMEACTION_SND_MUMMYKILLED);
                                    continue;
                                }
                            }


                            boolean lg_anime = p_spr.processAnimation();
                            switch (p_spr.i_ObjectState)
                            {
                                case STATE_APPEARANCE:
                                    {
                                        //boolean lg_anime = p_spr.processAnimation();
                                        if (lg_anime)
                                        {
                                            p_spr.i_keySet = PLAYERKEY_NONE;
                                            if (getRandomInt(100) >= 50)
                                                p_spr.i_keySet |= PLAYERKEY_LEFT;
                                            else
                                                p_spr.i_keySet |= PLAYERKEY_RIGHT;

                                            if (getRandomInt(100) >= 50)
                                                p_spr.i_keySet |= PLAYERKEY_UP;
                                            else
                                                p_spr.i_keySet |= PLAYERKEY_DOWN;

                                            p_spr.lg_MoveLeft = ((p_spr.i_keySet & PLAYERKEY_LEFT) != 0);

                                            loadStateForSprite(p_spr, STATE_MOVE);
                                        }
                                    }
                                    ;
                                    break;
                                case STATE_DEATH:
                                    {
                                        //boolean lg_anime = p_spr.processAnimation();
                                        if (lg_anime)
                                        {
                                            p_spr.lg_SpriteInvisible = true;
                                            p_spr.i_Frame = YELLOWMUMMYDELAYTICKS;
                                        }
                                    }
                                    ;
                                    break;
                                case STATE_FIRE:
                                    {
                                        //boolean lg_anime = p_spr.processAnimation();
                                        if (lg_anime)
                                        {
                                            loadStateForSprite(p_spr, STATE_MOVE);
                                            if (lg_PlayerCollided)
                                            {
                                                i_PlayerForce -= YELLOWMUMMYSTRIKE;
                                            }
                                        }
                                    }
                                    ;
                                    break;
                                case STATE_STAY:
                                case STATE_MOVE:
                                    {
                                        if (lg_PlayerCollided)
                                        {
                                            if (p_PlayerSprite.i_mainX < p_spr.i_mainX)
                                            {
                                                p_spr.lg_MoveLeft = true;
                                                p_spr.i_keySet &= ~PLAYERKEY_RIGHT;
                                                p_spr.i_keySet |= PLAYERKEY_LEFT;
                                            }
                                            else
                                            {
                                                p_spr.lg_MoveLeft = false;
                                                p_spr.i_keySet &= ~PLAYERKEY_LEFT;
                                                p_spr.i_keySet |= PLAYERKEY_RIGHT;
                                            }
                                            loadStateForSprite(p_spr, STATE_FIRE);
                                            p_GameActionListener.gameAction(GAMEACTION_SND_MUMMYATTACK);
                                        }
                                        else
                                        {
                                            _processMummy(p_spr);
                                        }
                                    }
                                    ;
                                    break;
                            }
                        }
                        ;
                        break;
                    case OBJECT_BLACKMUMMY:
                        {
                            if (!
                                 (i_TheIndexOfLastTakenHammer >= 0 || p_HammerSprite == null
                                  || p_spr.i_ObjectState == STATE_APPEARANCE
                                  || p_spr.i_ObjectState == STATE_DEATH)
                               )
                            {
                                if (p_spr.isCollided(p_HammerSprite))
                                {
                                    loadStateForSprite(p_spr, STATE_DEATH);
                                    loadStateForSprite(p_HammerSprite, STATE_FIRE);
                                    p_HammerSprite.setMainPointXY(
                                          p_spr.i_ScreenX +  p_spr.i_col_offsetX + (p_HammerSprite.lg_MoveLeft ? p_spr.i_col_width-(p_HammerSprite.i_col_width>>1):(p_HammerSprite.i_col_width>>1)),
                                          p_HammerSprite.i_mainY //+ (p_HammerSprite.i_col_height>>1)
                                      );
                                    i_PlayerScore += SCORES_BLACKMUMMY;
                                    p_HammerSprite = null;
                                    p_GameActionListener.gameAction(GAMEACTION_SND_MUMMYKILLED);
                                    continue;
                                }
                            }

                            boolean lg_anime = p_spr.processAnimation();
                            switch (p_spr.i_ObjectState)
                            {
                                case STATE_APPEARANCE:
                                    {
                                        //boolean lg_anime = p_spr.processAnimation();
                                        if (lg_anime)
                                        {
                                            if (getRandomInt(100) >= 50)
                                                p_spr.i_keySet |= PLAYERKEY_LEFT;
                                            else
                                                p_spr.i_keySet |= PLAYERKEY_RIGHT;

                                            p_spr.lg_MoveLeft = ((p_spr.i_keySet & PLAYERKEY_LEFT) != 0);

                                            loadStateForSprite(p_spr, STATE_MOVE);
                                        }
                                    }
                                    ;
                                    break;
                                case STATE_DEATH:
                                    {
                                        //boolean lg_anime = p_spr.processAnimation();
                                        if (lg_anime)
                                        {
                                            p_spr.lg_SpriteInvisible = true;
                                            p_spr.i_Frame = BLACKMUMMYDELAYTICKS;
                                        }
                                    }
                                    ;
                                    break;
                                case STATE_FIRE:
                                    {
                                        //boolean lg_anime = p_spr.processAnimation();
                                        if (lg_anime)
                                        {
                                            loadStateForSprite(p_spr, STATE_MOVE);
                                            if (lg_PlayerCollided)
                                            {
                                                i_PlayerForce -= BLACKMUMMYSTRIKE;
                                            }
                                        }
                                    }
                                    ;
                                    break;
                                case STATE_STAY:
                                case STATE_MOVE:
                                    {
                                        if (lg_PlayerCollided)
                                        {
                                            if (p_PlayerSprite.i_mainX < p_spr.i_mainX)
                                            {
                                                p_spr.lg_MoveLeft = true;
                                                p_spr.i_keySet &= ~PLAYERKEY_RIGHT;
                                                p_spr.i_keySet |= PLAYERKEY_LEFT;
                                            }
                                            else
                                            {
                                                p_spr.lg_MoveLeft = false;
                                                p_spr.i_keySet &= ~PLAYERKEY_LEFT;
                                                p_spr.i_keySet |= PLAYERKEY_RIGHT;
                                            }
                                            loadStateForSprite(p_spr, STATE_FIRE);
                                            p_GameActionListener.gameAction(GAMEACTION_SND_MUMMYATTACK);
                                        }
                                        else
                                        {
                                            _processMummy(p_spr);
                                        }
                                    }
                                    ;
                                    break;
                            }
                        }
                        ;
                        break;
                }
            }
        }

        if (!p_KeySprite.lg_SpriteInvisible)
        {
            boolean lg_Animation = p_KeySprite.processAnimation();
            switch (p_KeySprite.i_ObjectState)
            {
                case STATE_APPEARANCE:
                    {
                        if (lg_Animation)
                        {
                            loadStateForSprite(p_KeySprite, STATE_STAY);
                        }
                    }
                case STATE_STAY:
                    {
                        if (p_KeySprite.isCollided(p_PlayerSprite))
                        {
                            loadStateForSprite(p_KeySprite, STATE_DEATH);
                            loadStateForSprite(p_DoorSprite, STATE_APPEARANCE);
                            p_DoorSprite.lg_SpriteInvisible = false;
                            p_GameActionListener.gameAction(GAMEACTION_SND_KEYTAKEN);
                        }
                    }
                    ;
                    break;
                case STATE_DEATH:
                    {
                        if (lg_Animation)
                        {
                            p_KeySprite.lg_SpriteInvisible = true;
                        }
                    }
                    ;
                    break;
            }
        }

        if (!p_DoorSprite.lg_SpriteInvisible)
        {
            boolean lg_Animation = p_DoorSprite.processAnimation();
            switch (p_DoorSprite.i_ObjectState)
            {
                case STATE_APPEARANCE:
                    {
                        if (lg_Animation)
                        {
                          loadStateForSprite(p_DoorSprite,STATE_STAY);
                        }
                    }
                    ;
                    break;
                case STATE_STAY:
                    {
                        if (p_DoorSprite.isCollided(p_PlayerSprite))
                        {
                            lg_StageCompleted = true;
                            p_GameActionListener.gameAction(GAMEACTION_SND_DOOROPENED);
                        }
                    }
                    ;
                    break;
            }
        }

    }
    private boolean checkPassage(int i_x, int i_y)
    {
        int i_element = getElementForXYIndex(i_x, i_y);
        return (i_element == Stages.ELE_STONE || i_element == Stages.ELE_BOULDER);

    }

    private void loadStateForSprite(Sprite _sprite, int _state)
    {
        int i_offset = _sprite.i_ObjectType * 10 * 10 + 10 * _state;
        // Width, Height, OffX, OffY, OffWdth, OffHght, Frames, Delay, Animation type, Anchor
        int i_wdth = ai_SpriteDefinitionArray[i_offset++];
        int i_hght = ai_SpriteDefinitionArray[i_offset++];
        int i_offx = ai_SpriteDefinitionArray[i_offset++];
        int i_offy = ai_SpriteDefinitionArray[i_offset++];
        int i_offwdth = ai_SpriteDefinitionArray[i_offset++];
        int i_offhght = ai_SpriteDefinitionArray[i_offset++];
        int i_frames = ai_SpriteDefinitionArray[i_offset++];
        int i_delay = ai_SpriteDefinitionArray[i_offset++];
        int i_animtype = ai_SpriteDefinitionArray[i_offset++];
        int i_anchor = ai_SpriteDefinitionArray[i_offset];

        _sprite.setAnimation(i_animtype, i_anchor, i_wdth, i_hght, i_frames, 0, i_delay);
        _sprite.setCollisionBounds(i_offx, i_offy, i_offwdth, i_offhght);
        _sprite.lg_SpriteActive = true;

        _sprite.i_ObjectState = _state;
    }

    public void writeToStream(DataOutputStream _dataOutputStream) throws IOException
    {
        // these variables were used for restart stage

        _dataOutputStream.writeByte(i_StageAttemptions);
        _dataOutputStream.writeShort(i_StagePlayerForce);
        _dataOutputStream.writeShort(i_StagePlayerScore);

        // regular variables

        _dataOutputStream.writeByte(i_Attemptions);
        _dataOutputStream.writeShort(i_PlayerForce);
        _dataOutputStream.writeShort(i_TheIndexOfLastTakenHammer);
        _dataOutputStream.writeShort(i_TheIndexOfLastTakenPick);
        _dataOutputStream.writeBoolean(lg_StageCompleted);
        _dataOutputStream.writeInt(i_stageDiamonds);

        if (p_HammerSprite == null)
        {
            _dataOutputStream.writeShort(-1);
        }
        else
        {
            _dataOutputStream.writeShort(p_HammerSprite.i_spriteID);
        }

        _dataOutputStream.writeByte(p_PlayerSprite.i_ObjectType);
        _dataOutputStream.writeByte(p_PlayerSprite.i_ObjectState);
        p_PlayerSprite.writeSpriteToStream(_dataOutputStream);

        _dataOutputStream.writeByte(p_DoorSprite.i_ObjectType);
        _dataOutputStream.writeByte(p_DoorSprite.i_ObjectState);
        p_DoorSprite.writeSpriteToStream(_dataOutputStream);

        _dataOutputStream.writeByte(p_KeySprite.i_ObjectType);
        _dataOutputStream.writeByte(p_KeySprite.i_ObjectState);
        p_KeySprite.writeSpriteToStream(_dataOutputStream);

        for (int li = 0; li < MAXSPRITENUMBER; li++)
        {
            Sprite p_spr = ap_SpriteArray[li];
            _dataOutputStream.writeByte(p_spr.i_ObjectType);
            _dataOutputStream.writeByte(p_spr.i_ObjectState);
            p_spr.writeSpriteToStream(_dataOutputStream);
        }


        byte[] _orig_map = RLEdecompress(ab_stageDump,ab_currentStageArray.length);
        for (int li = 0; li < ab_currentStageArray.length; li++)
        {
            if (ab_currentStageArray[li]!=_orig_map[li] && _orig_map[li] == Stages.ELE_STONE)
            {
                _dataOutputStream.writeShort(li);
                _dataOutputStream.writeByte(ab_currentStageArray[li]);
                _dataOutputStream.writeByte(Stages.ab_decoration[li]);
            }
        }
        _dataOutputStream.writeShort(0xffff);

    }

    public void readFromStream(DataInputStream _dataInputStream) throws IOException
    {
        // these variables were used for restart stage

        i_StageAttemptions = _dataInputStream.readByte();
        i_StagePlayerForce = _dataInputStream.readShort();
        i_StagePlayerScore = _dataInputStream.readUnsignedShort();

        // regular variables

        i_Attemptions = _dataInputStream.readByte();
        i_PlayerForce = _dataInputStream.readShort();
        i_TheIndexOfLastTakenHammer = _dataInputStream.readShort();
        i_TheIndexOfLastTakenPick = _dataInputStream.readShort();
        lg_StageCompleted = _dataInputStream.readBoolean();
        i_stageDiamonds = _dataInputStream.readInt();

        int i_hmrIndx = _dataInputStream.readShort();
        if (i_hmrIndx < 0)
        {
            p_HammerSprite = null;
        }
        else
        {
            p_HammerSprite = ap_SpriteArray[i_hmrIndx];
        }

        p_PlayerSprite.i_ObjectType = _dataInputStream.readUnsignedByte();
        loadStateForSprite(p_PlayerSprite, _dataInputStream.readUnsignedByte());
        p_PlayerSprite.readSpriteFromStream(_dataInputStream);

        p_DoorSprite.i_ObjectType = _dataInputStream.readUnsignedByte();
        loadStateForSprite(p_DoorSprite, _dataInputStream.readUnsignedByte());
        p_DoorSprite.readSpriteFromStream(_dataInputStream);

        p_KeySprite.i_ObjectType = _dataInputStream.readUnsignedByte();
        loadStateForSprite(p_KeySprite, _dataInputStream.readUnsignedByte());
        p_KeySprite.readSpriteFromStream(_dataInputStream);

        for (int li = 0; li < MAXSPRITENUMBER; li++)
        {
            Sprite p_spr = ap_SpriteArray[li];
            p_spr.i_ObjectType = _dataInputStream.readUnsignedByte();
            loadStateForSprite(p_spr, _dataInputStream.readUnsignedByte());
            p_spr.readSpriteFromStream(_dataInputStream);
        }

        int _index;
        while ((_index = _dataInputStream.readUnsignedShort())!= 0xffff)
        {
            ab_currentStageArray[_index] =_dataInputStream.readByte();
            Stages.ab_decoration[_index] =_dataInputStream.readByte();
        }
        if(p_PlayerSprite.i_ObjectState == STATE_UP
           || p_PlayerSprite.i_ObjectState == STATE_DOWN)
        p_GameActionListener.gameAction(GAMEACTION_SLOW_MOTION);
    }

    public void nextGameStep(Object _playermoveobject)
    {

        processCommonObjects();
        if (processPlayer())
        {
            i_PlayerState = PLAYERSTATE_LOST;
            i_Attemptions--;
            if (i_Attemptions == 0)
            {
                i_GameState = GAMESTATE_OVER;
            }
        }
        else
        {
            if (lg_StageCompleted)
            {
                i_PlayerState = PLAYERSTATE_WON;
                i_GameState = GAMESTATE_OVER;
            }
        }
        i_insideTimer++;
    }

    public String getGameID()
    {
        return "KVll";
    }

    public int getMaxSizeOfSavedGameBlock()
    {
        return 1500; // 1254 thik
    }

    public int getPlayerScore()
    {
        return (i_PlayerScore * ((i_Attemptions / (i_GameLevel + 1)) + 1) * (i_GameStage + 1));
    }
}
