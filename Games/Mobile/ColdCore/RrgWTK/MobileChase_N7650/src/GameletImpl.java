import java.io.DataOutputStream;
import java.io.IOException;
import java.io.DataInputStream;

public class GameletImpl extends Gamelet
{
    private static final int SCALEFACTOR = 1;
    //===================================================================================//
    // PLAYER
    public static final int PLAYER_AREA_BORDER = 2;
    public static final int PLAYER_WIDTH = 14;
    public static final int PLAYER_HEIGHT = 6;

    private static final int PLAYER_FIRE_FREQ = 5; // 5 sec

    private static final int RIFLEMAN_Y_OFFSET = 1 << 8;
    private static final int RIFLEMAN_TIMEOUT = 15;
    private static final int RIFLEMAN_OUTCOMING_SPEED = 0x0100;
    //
    public static final int PLAYER_MAX_HSPEED = 0x400;
    public static final int PLAYER_MAX_VSPEED = 0x0B00;
    //
    public static final int PLAYER_HSPEED_INCREASE = 0x100;
    public static final int PLAYER_HSPEED_DECREASE = 0x100;
    //
    public static final int PLAYER_VSPEED_INCREASE = 0x200;
    public static final int PLAYER_VSPEED_DECREASE = 0x100;

    public static final int INIT_PLAYER_Y = 117 << 8;//0x8000 - ( PLAYER_HEIGHT / 2 << 8 ); //zone above player
    public static final int INIT_ACTIVE_ROAD_RANGE = INIT_PLAYER_Y + (20 << 8);      // active range
                                                                               // was 40<<8
    public static final int ROAD_BUFFER_LENGHT = 0x1000;      // 2 zones , above and below the active road
                                                              // used like hystory/buffer for generated objects


    //===================================================================================//

    public static final int VSPEED_INCREASE = 0x100;
    public static final int VSPEED_DECREASE = 0x20;

    public static final int HSPEED_INCREASE = 0x0100;
    public static final int HSPEED_DECREASE = 0x0100;

    public static final int SAFE_ZONE       = 0x0700;

    public static final int INIT_PLAYER_CAR_POSITION = 0;

    // public static final int TUNNEL_POSITION       = 1000; //16384;
    // public static final int TUNNEL_SIZE           = 512;

    //=============GAME ACTIONS==================
    public static final int GAMEACTION_SND_PLAYERREADY = 0;
    public static final int GAMEACTION_SND_PLAYERDEATH = 1;

    public static final int GAMEACTION_SND_COLLISION   = 2;
    public static final int GAMEACTION_SND_SIRENA      = 3;
    public static final int GAMEACTION_SND_FIRE        = 4;
    public static final int GAMEACTION_SND_EXPLOSION   = 5;
    public static final int GAMEACTION_SND_COPDIED     = 6;
    public static final int GAMEACTION_SND_WATERSPLASH = 7;
    public static final int GAMEACTION_SND_OUTOFROAD   = 8;
    //===========================================

    public static final int LEVEL_EASY = 0;
    public static final int LEVEL_EASY_TIMEDELAY = 100;
    public static final int LEVEL_EASY_ATTEMPTIONS = 4;
    public static final int LEVEL_EASY_ROAD_SCORE = 10;
    public static final int LEVEL_EASY_CAR_FREQ = 20;
    public static final int LEVEL_EASY_ENEMY_CAR_POSITION  = 3000;

    public static final int LEVEL_NORMAL = 1;
    public static final int LEVEL_NORMAL_TIMEDELAY = 90;
    public static final int LEVEL_NORMAL_ATTEMPTIONS = 3;
    public static final int LEVEL_NORMAL_ROAD_SCORE = 15;
    public static final int LEVEL_NORMAL_CAR_FREQ = 15;
    public static final int LEVEL_NORMAL_ENEMY_CAR_POSITION  = 5000;

    public static final int LEVEL_HARD = 2;
    public static final int LEVEL_HARD_TIMEDELAY = 80;
    public static final int LEVEL_HARD_ATTEMPTIONS = 2;
    public static final int LEVEL_HARD_ROAD_SCORE = 20;
    public static final int LEVEL_HARD_CAR_FREQ = 10;
    public static final int LEVEL_HARD_ENEMY_CAR_POSITION  = 7000;

    public static final int PLAYER_KEY_NONE  = 0;
    public static final int PLAYER_KEY_LEFT  = 1;
    public static final int PLAYER_KEY_RIGHT = 2;
    public static final int PLAYER_KEY_DOWN  = 4;
    public static final int PLAYER_KEY_UP    = 8;
    public static final int PLAYER_KEY_SPACE = 16;
    public static final int PLAYER_KEY_ENTER = 32;

    public static final int VIRTUALBLOCKWIDTH = 0x1000 * SCALEFACTOR;
    public static final int VIRTUALBLOCKHEIGHT = 0x1000 * SCALEFACTOR;

    public static final int INITPLAYERFORCE = 100;
    public static final int INIT_ENEMY_FORCE = 100;
    // Engine delays
    public static final int COLLIDED_PLAYER_DELAY = 2;
    public static final int IMMORTALITY_DELAY = 20;
    public static final int DEATH_STAGE_DELAY = 20;
    public static final int WIN_STAGE_DELAY = 40;
    public static final int CRASH_ENEMY_STARTENGINE_DELAY = 8;
    public static final int CRASH_STARTENGINE_DELAY = 80;
    public static final int CRASH_STOPENGINE_DELAY = 10;
    public static final int CRASH_DELAY = 10;
    public static final int PUDDLE_DELAY = 8;

    public static final int CAR_FLY_DELAY = 0x06;
    // Speed limits
    public static final int SPEED_JUMP_LIMIT = 0x880;
    private static final int TOP_APPEARING_OBJECTS_BORDER = 32;

    public int MinPlayerArea_X;
    public int MaxPlayerArea_X;
    public int MinPlayerArea_Y;
    public int MaxPlayerArea_Y;

    public int i_PlayerKey;

    public int i_currentStageWidth;
    public int i_currentStageHeight;
    public byte[] ab_currentStageArray;

    // Enemy strategies

    public static final int STRATEGY_DEFAULT        = 0;
    public static final int STRATEGY_DEFENCE        = 1;
    public static final int STRATEGY_RANDOM_SPEED   = 2;
    public static final int STRATEGY_BOARD_STRIKE   = 3;
    public static final int STRATEGY_BREAK_SPEED    = 4;
    public static final int STRATEGY_SPEED_UP       = 5;
    public static final int STRATEGY_ATTACK         = 6;
    public static final int STRATEGY_7              = 7;
    public static final int STRATEGY_8              = 8;

    // Map objects
    public static final int MAP_OBJECT_BOX              = 1;
    public static final int MAP_OBJECT_TREE             = 2;
    public static final int MAP_OBJECT_SIGN             = 3;
    public static final int MAP_OBJECT_PUDDLE           = 4;
    public static final int MAP_OBJECT_TUNNEL_ENTRANCE  = 5;
    public static final int MAP_OBJECT_TUNNEL_EXIT      = 6;

    public static final int OBJECT_PLAYER           = 0;
    public static final int OBJECT_ENEMY_CAR        = 1;
    public static final int OBJECT_CAR1             = 2;
    public static final int OBJECT_CAR2             = 3;
    public static final int OBJECT_CYCLE            = 4;
    public static final int OBJECT_BULLET           = 5;
    public static final int OBJECT_TREE             = 6;
    public static final int OBJECT_SIGN             = 7;
    public static final int OBJECT_PUDDLE           = 8;
    public static final int OBJECT_BOX              = 9;
    public static final int OBJECT_RIFLEMAN         = 10;
    public static final int OBJECT_EXPLOSION        = 11;
    public static final int OBJECT_TUNNEL_ENTRANCE  = 12;
    public static final int OBJECT_TUNNEL_EXIT      = 13;

    // Object sizes
    public static final int ENEMY_CAR_W = 18;
    public static final int ENEMY_CAR_H = 4;
    //
    public static final int CAR1_W      = 14;
    public static final int CAR1_H      = 4;
    //
    public static final int CAR2_W      = 12;
    public static final int CAR2_H      = 4;
    //
    public static final int CYCLE_W     = 8;
    public static final int CYCLE_H     = 4;
    //
    public static final int BULLET_W    = 3;
    public static final int BULLET_H    = 12;
    //
    public static final int TREE_W      = 12;
    public static final int TREE_H      = 4;
    //
    public static final int SIGN_W      = 20;
    public static final int SIGN_H      = 4;
    //
    public static final int PUDDLE_W     = 16;
    public static final int PUDDLE_H     = 8;
    //
    public static final int DROPS_PUDDLE_W  = 20;
    public static final int DROPS_PUDDLE_H  = 20;
    //
    public static final int BOX_W     = 12;
    public static final int BOX_H     = 8;
    //
    public static final int EXPL_BOX_W   = 12;
    public static final int EXPL_BOX_H   = 4;
    //
    public static final int RIFLEMAN_W   = 4;
    public static final int RIFLEMAN_H   = 4;
    //
    public static final int EXPLOSION_W   = 28;
    public static final int EXPLOSION_H   = 4;
    //
    public static final int TUNNEL_ENTRANCE_W   = 72;
    public static final int TUNNEL_ENTRANCE_H   = 16;
    //
    public static final int TUNNEL_EXIT_W   = 72;
    public static final int TUNNEL_EXIT_H   = 16;

    // Object Speeds
    public static final int ENEMY_CAR_SPEED = 0x800; //0x900
    public static final int ENEMY_CAR_FAST_SPEED = 0xA00;
    public static final int CAR1_SPEED      = 0x800;
    public static final int CAR2_SPEED      = 0x700;
    public static final int CYCLE_SPEED     = 0x900;
    public static final int BULLET_SPEED    = 0x1200;
    public static final int TREE_SPEED      = 0;
    public static final int SIGN_SPEED      = 0;
    public static final int PUDDLE_SPEED    = 0;
    public static final int BOX_SPEED       = 0;
    public static final int EXPLOSION_SPEED = 0;
    public static final int TUNNEL_SPEED    = 0;

    // Object Scores
    public static final int KILLED_CAR_SCORE    = -100 /10;
    public static final int BOX_SCORE           =  -10 /10;
    public static final int TREE_SCORE          =  -50 /10;
    public static final int ENEMY_COLLIDE_SCORE = -100 /10;


    // STATES
    // Car states
    public static final int STATE_NORMAL = 0x00; //UDLR
    public static final int STATE_RIGHT  = 0x01; //0001
    public static final int STATE_LEFT   = 0x02; //0010
    public static final int STATE_STOP   = 0x03; //0011
    public static final int STATE_DOWN   = 0x04; //0100
    //                       DOWN_RIGHT  = 0x05; //0101
    //                       DOWN_LEFT   = 0x06; //0110
    public static final int STATE_HIT    = 0x07; //0111
    public static final int STATE_UP     = 0x08; //1000
    //                         UP_RIGHT  = 0x09; //1001
    //                         UP_LEFT   = 0x0A; //1010
    public static final int STATE_FLY    = 0x0B; //1011
    //
    //
    public static final int BOX_STATE_NORMAL = 0;
    public static final int BOX_STATE_EXPLOSION = 1;
    //
    public static final int PUDDLE_STATE_DROPS = 1;

    // Turn
    public static final int TURNRIGHT   = 1;
    public static final int TURNLEFT    = 2;
    public static final int TURNCENTER  = 3;

    // Directions                            //SWEN
    public static final int NORTH       = 1; //0001
    public static final int EAST        = 2; //0010
    public static final int NORTH_EAST  = 3; //0011
    public static final int WEST        = 4; //0100
    public static final int NORTH_WEST  = 5; //0101
    public static final int SOUTH       = 8; //1000
    public static final int SOUTH_EAST  = 10;//1010
    public static final int SOUTH_WEST  = 12;//1100

    public int i_EnemyForce;
    public int strategy = 0;

    public int i_PlayerForce;

    public int i_player_HSpeed;
    public int i_player_VSpeed;
    public boolean i_player_Slowdown;
    public boolean lg_player_alive;
    public int i_player_collided_time;
    public int i_immortality_time;
    public int i_ScoreMultiplicator;

    public int i_View_Position;
    public int i_Bottom_Position;

    public final static int i_RoadWidth = 64;

    public boolean lg_Tunnel = false;
    public int i_LastRoad;
    public int i_LastRoadHeight;

    public int i_Timer;

    private boolean lg_StageCompleted;

    public int i_StageDecorationNo;
    private int fire_time = 0;

    private int object_time = 0;
    private int object_freq = 32;

    // Horizont position where was born last enemy
    private int born_time = 0;
    // Current enemy born frequency
    private int born_freq = 8;



    //Sprite defenition array
    public static final short[] ai_SpriteDefinitionArray = new short[]
    {
        // Width, Height, OffX, OffY, OffWdth, OffHght, Frames, Delay, Animation type, Anchor
        //PLAYER
        //NORMAL CAR
        (PLAYER_WIDTH << 8) * SCALEFACTOR, (PLAYER_HEIGHT << 8) * SCALEFACTOR, 0x000 * SCALEFACTOR, 0x000 * SCALEFACTOR, (PLAYER_WIDTH << 8) * SCALEFACTOR, (PLAYER_HEIGHT << 8) * SCALEFACTOR, 1, 1, Sprite.ANIMATION_FROZEN, Sprite.SPRITE_ALIGN_TOP | Sprite.SPRITE_ALIGN_LEFT,
        //TURN RIGHT
        (PLAYER_WIDTH << 8) * SCALEFACTOR, (PLAYER_HEIGHT << 8) * SCALEFACTOR, 0x000 * SCALEFACTOR, 0x000 * SCALEFACTOR, (PLAYER_WIDTH << 8) * SCALEFACTOR, (PLAYER_HEIGHT << 8) * SCALEFACTOR, 1, 1, Sprite.ANIMATION_FROZEN, Sprite.SPRITE_ALIGN_TOP | Sprite.SPRITE_ALIGN_LEFT,
        //TURN LEFT
        (PLAYER_WIDTH << 8) * SCALEFACTOR, (PLAYER_HEIGHT << 8) * SCALEFACTOR, 0x000 * SCALEFACTOR, 0x000 * SCALEFACTOR, (PLAYER_WIDTH << 8) * SCALEFACTOR, (PLAYER_HEIGHT << 8) * SCALEFACTOR, 1, 1, Sprite.ANIMATION_FROZEN, Sprite.SPRITE_ALIGN_TOP | Sprite.SPRITE_ALIGN_LEFT,
        //STOP CAR
        (PLAYER_WIDTH << 8) * SCALEFACTOR, (PLAYER_HEIGHT << 8) * SCALEFACTOR, 0x000 * SCALEFACTOR, 0x000 * SCALEFACTOR, (PLAYER_WIDTH << 8) * SCALEFACTOR, (PLAYER_HEIGHT << 8) * SCALEFACTOR, 1, 1, Sprite.ANIMATION_FROZEN, Sprite.SPRITE_ALIGN_TOP | Sprite.SPRITE_ALIGN_LEFT,
        //DOWN CAR
        (PLAYER_WIDTH << 8) * SCALEFACTOR, (PLAYER_HEIGHT << 8) * SCALEFACTOR, 0x000 * SCALEFACTOR, 0x000 * SCALEFACTOR, (PLAYER_WIDTH << 8) * SCALEFACTOR, (PLAYER_HEIGHT << 8) * SCALEFACTOR, 1, 1, Sprite.ANIMATION_FROZEN, Sprite.SPRITE_ALIGN_TOP | Sprite.SPRITE_ALIGN_LEFT,
        //DOWN CAR + RIGHT TURN
        (PLAYER_WIDTH << 8) * SCALEFACTOR, (PLAYER_HEIGHT << 8) * SCALEFACTOR, 0x000 * SCALEFACTOR, 0x000 * SCALEFACTOR, (PLAYER_WIDTH << 8) * SCALEFACTOR, (PLAYER_HEIGHT << 8) * SCALEFACTOR, 1, 1, Sprite.ANIMATION_FROZEN, Sprite.SPRITE_ALIGN_TOP | Sprite.SPRITE_ALIGN_LEFT,
        //DOWN CAR + LEFT TURN
        (PLAYER_WIDTH << 8) * SCALEFACTOR, (PLAYER_HEIGHT << 8) * SCALEFACTOR, 0x000 * SCALEFACTOR, 0x000 * SCALEFACTOR, (PLAYER_WIDTH << 8) * SCALEFACTOR, (PLAYER_HEIGHT << 8) * SCALEFACTOR, 1, 1, Sprite.ANIMATION_FROZEN, Sprite.SPRITE_ALIGN_TOP | Sprite.SPRITE_ALIGN_LEFT,
        //HIT CAR
        (PLAYER_WIDTH << 8) * SCALEFACTOR, (PLAYER_HEIGHT << 8) * SCALEFACTOR, 0x000 * SCALEFACTOR, 0x000 * SCALEFACTOR, (PLAYER_WIDTH << 8) * SCALEFACTOR, (PLAYER_HEIGHT << 8) * SCALEFACTOR, 1, 1, Sprite.ANIMATION_FROZEN, Sprite.SPRITE_ALIGN_TOP | Sprite.SPRITE_ALIGN_LEFT,
        //UP CAR
        (PLAYER_WIDTH << 8) * SCALEFACTOR, (PLAYER_HEIGHT << 8) * SCALEFACTOR, 0x000 * SCALEFACTOR, 0x000 * SCALEFACTOR, (PLAYER_WIDTH << 8) * SCALEFACTOR, (PLAYER_HEIGHT << 8) * SCALEFACTOR, 1, 1, Sprite.ANIMATION_FROZEN, Sprite.SPRITE_ALIGN_TOP | Sprite.SPRITE_ALIGN_LEFT,
        //UP CAR  + RIGHT TURN
        (PLAYER_WIDTH << 8) * SCALEFACTOR, (PLAYER_HEIGHT << 8) * SCALEFACTOR, 0x000 * SCALEFACTOR, 0x000 * SCALEFACTOR, (PLAYER_WIDTH << 8) * SCALEFACTOR, (PLAYER_HEIGHT << 8) * SCALEFACTOR, 1, 1, Sprite.ANIMATION_FROZEN, Sprite.SPRITE_ALIGN_TOP | Sprite.SPRITE_ALIGN_LEFT,
        //UP CAR  + LEFT TURN
        (PLAYER_WIDTH << 8) * SCALEFACTOR, (PLAYER_HEIGHT << 8) * SCALEFACTOR, 0x000 * SCALEFACTOR, 0x000 * SCALEFACTOR, (PLAYER_WIDTH << 8) * SCALEFACTOR, (PLAYER_HEIGHT << 8) * SCALEFACTOR, 1, 1, Sprite.ANIMATION_FROZEN, Sprite.SPRITE_ALIGN_TOP | Sprite.SPRITE_ALIGN_LEFT,
        //FLY CAR
        (PLAYER_WIDTH << 8) * SCALEFACTOR, (PLAYER_HEIGHT << 8) * SCALEFACTOR, 0x000 * SCALEFACTOR, 0x000 * SCALEFACTOR, (PLAYER_WIDTH << 8) * SCALEFACTOR, (PLAYER_HEIGHT << 8) * SCALEFACTOR, 1, 1, Sprite.ANIMATION_FROZEN, Sprite.SPRITE_ALIGN_TOP | Sprite.SPRITE_ALIGN_LEFT,

        //ENEMY_CAR
        //NORMAL CAR
        (ENEMY_CAR_W << 8) * SCALEFACTOR, (ENEMY_CAR_H << 8) * SCALEFACTOR, 0x000 * SCALEFACTOR, 0x000 * SCALEFACTOR, (ENEMY_CAR_W << 8) * SCALEFACTOR, (ENEMY_CAR_H << 8) * SCALEFACTOR, 1, 1, Sprite.ANIMATION_FROZEN, Sprite.SPRITE_ALIGN_TOP | Sprite.SPRITE_ALIGN_LEFT,
        //TURN RIGHT
        (ENEMY_CAR_W << 8) * SCALEFACTOR, (ENEMY_CAR_H << 8) * SCALEFACTOR, 0x000 * SCALEFACTOR, 0x000 * SCALEFACTOR, (ENEMY_CAR_W << 8) * SCALEFACTOR, (ENEMY_CAR_H << 8) * SCALEFACTOR, 1, 1, Sprite.ANIMATION_FROZEN, Sprite.SPRITE_ALIGN_TOP | Sprite.SPRITE_ALIGN_LEFT,
        //TURN LEFT
        (ENEMY_CAR_W << 8) * SCALEFACTOR, (ENEMY_CAR_H << 8) * SCALEFACTOR, 0x000 * SCALEFACTOR, 0x000 * SCALEFACTOR, (ENEMY_CAR_W << 8) * SCALEFACTOR, (ENEMY_CAR_H << 8) * SCALEFACTOR, 1, 1, Sprite.ANIMATION_FROZEN, Sprite.SPRITE_ALIGN_TOP | Sprite.SPRITE_ALIGN_LEFT,
        //NOT USED (LEFT & RIGHT)
        (ENEMY_CAR_W << 8) * SCALEFACTOR, (ENEMY_CAR_H << 8) * SCALEFACTOR, 0x000 * SCALEFACTOR, 0x000 * SCALEFACTOR, (ENEMY_CAR_W << 8) * SCALEFACTOR, (ENEMY_CAR_H << 8) * SCALEFACTOR, 1, 1, Sprite.ANIMATION_FROZEN, Sprite.SPRITE_ALIGN_TOP | Sprite.SPRITE_ALIGN_LEFT,
        //DOWN CAR
        (ENEMY_CAR_W << 8) * SCALEFACTOR, (ENEMY_CAR_H << 8) * SCALEFACTOR, 0x000 * SCALEFACTOR, 0x000 * SCALEFACTOR, (ENEMY_CAR_W << 8) * SCALEFACTOR, (ENEMY_CAR_H << 8) * SCALEFACTOR, 1, 1, Sprite.ANIMATION_FROZEN, Sprite.SPRITE_ALIGN_TOP | Sprite.SPRITE_ALIGN_LEFT,
        //DOWN CAR + RIGHT TURN
        (ENEMY_CAR_W << 8) * SCALEFACTOR, (ENEMY_CAR_H << 8) * SCALEFACTOR, 0x000 * SCALEFACTOR, 0x000 * SCALEFACTOR, (ENEMY_CAR_W << 8) * SCALEFACTOR, (ENEMY_CAR_H << 8) * SCALEFACTOR, 1, 1, Sprite.ANIMATION_FROZEN, Sprite.SPRITE_ALIGN_TOP | Sprite.SPRITE_ALIGN_LEFT,
        //DOWN CAR + LEFT TURN
        (ENEMY_CAR_W << 8) * SCALEFACTOR, (ENEMY_CAR_H << 8) * SCALEFACTOR, 0x000 * SCALEFACTOR, 0x000 * SCALEFACTOR, (ENEMY_CAR_W << 8) * SCALEFACTOR, (ENEMY_CAR_H << 8) * SCALEFACTOR, 1, 1, Sprite.ANIMATION_FROZEN, Sprite.SPRITE_ALIGN_TOP | Sprite.SPRITE_ALIGN_LEFT,
        //NOT USED (LEFT & RIGHT)
        (ENEMY_CAR_W << 8) * SCALEFACTOR, (ENEMY_CAR_H << 8) * SCALEFACTOR, 0x000 * SCALEFACTOR, 0x000 * SCALEFACTOR, (ENEMY_CAR_W << 8) * SCALEFACTOR, (ENEMY_CAR_H << 8) * SCALEFACTOR, 1, 1, Sprite.ANIMATION_FROZEN, Sprite.SPRITE_ALIGN_TOP | Sprite.SPRITE_ALIGN_LEFT,
        //UP CAR
        (ENEMY_CAR_W << 8) * SCALEFACTOR, (ENEMY_CAR_H << 8) * SCALEFACTOR, 0x000 * SCALEFACTOR, 0x000 * SCALEFACTOR, (ENEMY_CAR_W << 8) * SCALEFACTOR, (ENEMY_CAR_H << 8) * SCALEFACTOR, 1, 1, Sprite.ANIMATION_FROZEN, Sprite.SPRITE_ALIGN_TOP | Sprite.SPRITE_ALIGN_LEFT,
        //UP CAR  + RIGHT TURN
        (ENEMY_CAR_W << 8) * SCALEFACTOR, (ENEMY_CAR_H << 8) * SCALEFACTOR, 0x000 * SCALEFACTOR, 0x000 * SCALEFACTOR, (ENEMY_CAR_W << 8) * SCALEFACTOR, (ENEMY_CAR_H << 8) * SCALEFACTOR, 1, 1, Sprite.ANIMATION_FROZEN, Sprite.SPRITE_ALIGN_TOP | Sprite.SPRITE_ALIGN_LEFT,
        //UP CAR  + LEFT TURN
        (ENEMY_CAR_W << 8) * SCALEFACTOR, (ENEMY_CAR_H << 8) * SCALEFACTOR, 0x000 * SCALEFACTOR, 0x000 * SCALEFACTOR, (ENEMY_CAR_W << 8) * SCALEFACTOR, (ENEMY_CAR_H << 8) * SCALEFACTOR, 1, 1, Sprite.ANIMATION_FROZEN, Sprite.SPRITE_ALIGN_TOP | Sprite.SPRITE_ALIGN_LEFT,
        //NOT USED (LEFT & RIGHT)
        (ENEMY_CAR_W << 8) * SCALEFACTOR, (ENEMY_CAR_H << 8) * SCALEFACTOR, 0x000 * SCALEFACTOR, 0x000 * SCALEFACTOR, (ENEMY_CAR_W << 8) * SCALEFACTOR, (ENEMY_CAR_H << 8) * SCALEFACTOR, 1, 1, Sprite.ANIMATION_FROZEN, Sprite.SPRITE_ALIGN_TOP | Sprite.SPRITE_ALIGN_LEFT,

       //CAR1
        //NORMAL CAR
        (CAR1_W << 8) * SCALEFACTOR, (CAR1_H << 8) * SCALEFACTOR, 0x000 * SCALEFACTOR, 0x000 * SCALEFACTOR, (CAR1_W << 8) * SCALEFACTOR, (CAR1_H << 8) * SCALEFACTOR, 1, 1, Sprite.ANIMATION_FROZEN, Sprite.SPRITE_ALIGN_TOP | Sprite.SPRITE_ALIGN_LEFT,
        //TURN RIGHT
        (CAR1_W << 8) * SCALEFACTOR, (CAR1_H << 8) * SCALEFACTOR, 0x000 * SCALEFACTOR, 0x000 * SCALEFACTOR, (CAR1_W << 8) * SCALEFACTOR, (CAR1_H << 8) * SCALEFACTOR, 1, 1, Sprite.ANIMATION_FROZEN, Sprite.SPRITE_ALIGN_TOP | Sprite.SPRITE_ALIGN_LEFT,
        //TURN LEFT
        (CAR1_W << 8) * SCALEFACTOR, (CAR1_H << 8) * SCALEFACTOR, 0x000 * SCALEFACTOR, 0x000 * SCALEFACTOR, (CAR1_W << 8) * SCALEFACTOR, (CAR1_H << 8) * SCALEFACTOR, 1, 1, Sprite.ANIMATION_FROZEN, Sprite.SPRITE_ALIGN_TOP | Sprite.SPRITE_ALIGN_LEFT,
        //NOT USED (LEFT & RIGHT)
        (CAR1_W << 8) * SCALEFACTOR, (CAR1_H << 8) * SCALEFACTOR, 0x000 * SCALEFACTOR, 0x000 * SCALEFACTOR, (CAR1_W << 8) * SCALEFACTOR, (CAR1_H << 8) * SCALEFACTOR, 1, 1, Sprite.ANIMATION_FROZEN, Sprite.SPRITE_ALIGN_TOP | Sprite.SPRITE_ALIGN_LEFT,
        //DOWN CAR
        (CAR1_W << 8) * SCALEFACTOR, (CAR1_H << 8) * SCALEFACTOR, 0x000 * SCALEFACTOR, 0x000 * SCALEFACTOR, (CAR1_W << 8) * SCALEFACTOR, (CAR1_H << 8) * SCALEFACTOR, 1, 1, Sprite.ANIMATION_FROZEN, Sprite.SPRITE_ALIGN_TOP | Sprite.SPRITE_ALIGN_LEFT,
        //DOWN CAR + RIGHT TURN
        (CAR1_W << 8) * SCALEFACTOR, (CAR1_H << 8) * SCALEFACTOR, 0x000 * SCALEFACTOR, 0x000 * SCALEFACTOR, (CAR1_W << 8) * SCALEFACTOR, (CAR1_H << 8) * SCALEFACTOR, 1, 1, Sprite.ANIMATION_FROZEN, Sprite.SPRITE_ALIGN_TOP | Sprite.SPRITE_ALIGN_LEFT,
        //DOWN CAR + LEFT TURN
        (CAR1_W << 8) * SCALEFACTOR, (CAR1_H << 8) * SCALEFACTOR, 0x000 * SCALEFACTOR, 0x000 * SCALEFACTOR, (CAR1_W << 8) * SCALEFACTOR, (CAR1_H << 8) * SCALEFACTOR, 1, 1, Sprite.ANIMATION_FROZEN, Sprite.SPRITE_ALIGN_TOP | Sprite.SPRITE_ALIGN_LEFT,
        //HIT CAR
        (CAR1_W << 8) * SCALEFACTOR, (CAR1_H << 8) * SCALEFACTOR, 0x000 * SCALEFACTOR, 0x000 * SCALEFACTOR, (CAR1_W << 8) * SCALEFACTOR, (CAR1_H << 8) * SCALEFACTOR, 6, 2, Sprite.ANIMATION_FROZEN, Sprite.SPRITE_ALIGN_TOP | Sprite.SPRITE_ALIGN_LEFT,
        //UP CAR
        (CAR1_W << 8) * SCALEFACTOR, (CAR1_H << 8) * SCALEFACTOR, 0x000 * SCALEFACTOR, 0x000 * SCALEFACTOR, (CAR1_W << 8) * SCALEFACTOR, (CAR1_H << 8) * SCALEFACTOR, 1, 1, Sprite.ANIMATION_FROZEN, Sprite.SPRITE_ALIGN_TOP | Sprite.SPRITE_ALIGN_LEFT,
        //UP CAR  + RIGHT TURN
        (CAR1_W << 8) * SCALEFACTOR, (CAR1_H << 8) * SCALEFACTOR, 0x000 * SCALEFACTOR, 0x000 * SCALEFACTOR, (CAR1_W << 8) * SCALEFACTOR, (CAR1_H << 8) * SCALEFACTOR, 1, 1, Sprite.ANIMATION_FROZEN, Sprite.SPRITE_ALIGN_TOP | Sprite.SPRITE_ALIGN_LEFT,
        //UP CAR  + LEFT TURN
        (CAR1_W << 8) * SCALEFACTOR, (CAR1_H << 8) * SCALEFACTOR, 0x000 * SCALEFACTOR, 0x000 * SCALEFACTOR, (CAR1_W << 8) * SCALEFACTOR, (CAR1_H << 8) * SCALEFACTOR, 1, 1, Sprite.ANIMATION_FROZEN, Sprite.SPRITE_ALIGN_TOP | Sprite.SPRITE_ALIGN_LEFT,
        //NOT USED (LEFT & RIGHT)
        (CAR1_W << 8) * SCALEFACTOR, (CAR1_H << 8) * SCALEFACTOR, 0x000 * SCALEFACTOR, 0x000 * SCALEFACTOR, (CAR1_W << 8) * SCALEFACTOR, (CAR1_H << 8) * SCALEFACTOR, 1, 1, Sprite.ANIMATION_FROZEN, Sprite.SPRITE_ALIGN_TOP | Sprite.SPRITE_ALIGN_LEFT,

       //CAR2
        //NORMAL CAR
        (CAR2_W << 8) * SCALEFACTOR, (CAR2_H << 8) * SCALEFACTOR, 0x000 * SCALEFACTOR, 0x000 * SCALEFACTOR, (CAR2_W << 8) * SCALEFACTOR, (CAR2_H << 8) * SCALEFACTOR, 1, 1, Sprite.ANIMATION_FROZEN, Sprite.SPRITE_ALIGN_TOP | Sprite.SPRITE_ALIGN_LEFT,
        //TURN RIGHT
        (CAR2_W << 8) * SCALEFACTOR, (CAR2_H << 8) * SCALEFACTOR, 0x000 * SCALEFACTOR, 0x000 * SCALEFACTOR, (CAR2_W << 8) * SCALEFACTOR, (CAR2_H << 8) * SCALEFACTOR, 1, 1, Sprite.ANIMATION_FROZEN, Sprite.SPRITE_ALIGN_TOP | Sprite.SPRITE_ALIGN_LEFT,
        //TURN LEFT
        (CAR2_W << 8) * SCALEFACTOR, (CAR2_H << 8) * SCALEFACTOR, 0x000 * SCALEFACTOR, 0x000 * SCALEFACTOR, (CAR2_W << 8) * SCALEFACTOR, (CAR2_H << 8) * SCALEFACTOR, 1, 1, Sprite.ANIMATION_FROZEN, Sprite.SPRITE_ALIGN_TOP | Sprite.SPRITE_ALIGN_LEFT,
        //NOT USED (LEFT & RIGHT)
        (CAR2_W << 8) * SCALEFACTOR, (CAR2_H << 8) * SCALEFACTOR, 0x000 * SCALEFACTOR, 0x000 * SCALEFACTOR, (CAR2_W << 8) * SCALEFACTOR, (CAR2_H << 8) * SCALEFACTOR, 1, 1, Sprite.ANIMATION_FROZEN, Sprite.SPRITE_ALIGN_TOP | Sprite.SPRITE_ALIGN_LEFT,
        //DOWN CAR
        (CAR2_W << 8) * SCALEFACTOR, (CAR2_H << 8) * SCALEFACTOR, 0x000 * SCALEFACTOR, 0x000 * SCALEFACTOR, (CAR2_W << 8) * SCALEFACTOR, (CAR2_H << 8) * SCALEFACTOR, 1, 1, Sprite.ANIMATION_FROZEN, Sprite.SPRITE_ALIGN_TOP | Sprite.SPRITE_ALIGN_LEFT,
        //DOWN CAR + RIGHT TURN
        (CAR2_W << 8) * SCALEFACTOR, (CAR2_H << 8) * SCALEFACTOR, 0x000 * SCALEFACTOR, 0x000 * SCALEFACTOR, (CAR2_W << 8) * SCALEFACTOR, (CAR2_H << 8) * SCALEFACTOR, 1, 1, Sprite.ANIMATION_FROZEN, Sprite.SPRITE_ALIGN_TOP | Sprite.SPRITE_ALIGN_LEFT,
        //DOWN CAR + LEFT TURN
        (CAR2_W << 8) * SCALEFACTOR, (CAR2_H << 8) * SCALEFACTOR, 0x000 * SCALEFACTOR, 0x000 * SCALEFACTOR, (CAR2_W << 8) * SCALEFACTOR, (CAR2_H << 8) * SCALEFACTOR, 1, 1, Sprite.ANIMATION_FROZEN, Sprite.SPRITE_ALIGN_TOP | Sprite.SPRITE_ALIGN_LEFT,
        //HIT CAR
        (CAR2_W << 8) * SCALEFACTOR, (CAR2_H << 8) * SCALEFACTOR, 0x000 * SCALEFACTOR, 0x000 * SCALEFACTOR, (CAR2_W << 8) * SCALEFACTOR, (CAR2_H << 8) * SCALEFACTOR, 6, 2, Sprite.ANIMATION_FROZEN, Sprite.SPRITE_ALIGN_TOP | Sprite.SPRITE_ALIGN_LEFT,
        //UP CAR
        (CAR2_W << 8) * SCALEFACTOR, (CAR2_H << 8) * SCALEFACTOR, 0x000 * SCALEFACTOR, 0x000 * SCALEFACTOR, (CAR2_W << 8) * SCALEFACTOR, (CAR2_H << 8) * SCALEFACTOR, 1, 1, Sprite.ANIMATION_FROZEN, Sprite.SPRITE_ALIGN_TOP | Sprite.SPRITE_ALIGN_LEFT,
        //UP CAR  + RIGHT TURN
        (CAR2_W << 8) * SCALEFACTOR, (CAR2_H << 8) * SCALEFACTOR, 0x000 * SCALEFACTOR, 0x000 * SCALEFACTOR, (CAR2_W << 8) * SCALEFACTOR, (CAR2_H << 8) * SCALEFACTOR, 1, 1, Sprite.ANIMATION_FROZEN, Sprite.SPRITE_ALIGN_TOP | Sprite.SPRITE_ALIGN_LEFT,
        //UP CAR  + LEFT TURN
        (CAR2_W << 8) * SCALEFACTOR, (CAR2_H << 8) * SCALEFACTOR, 0x000 * SCALEFACTOR, 0x000 * SCALEFACTOR, (CAR2_W << 8) * SCALEFACTOR, (CAR2_H << 8) * SCALEFACTOR, 1, 1, Sprite.ANIMATION_FROZEN, Sprite.SPRITE_ALIGN_TOP | Sprite.SPRITE_ALIGN_LEFT,
        //NOT USED (LEFT & RIGHT)
        (CAR2_W << 8) * SCALEFACTOR, (CAR2_H << 8) * SCALEFACTOR, 0x000 * SCALEFACTOR, 0x000 * SCALEFACTOR, (CAR2_W << 8) * SCALEFACTOR, (CAR2_H << 8) * SCALEFACTOR, 1, 1, Sprite.ANIMATION_FROZEN, Sprite.SPRITE_ALIGN_TOP | Sprite.SPRITE_ALIGN_LEFT,

        //CYCLE
        //NORMAL CAR
        (CYCLE_W << 8) * SCALEFACTOR, (CYCLE_H << 8) * SCALEFACTOR, 0x000 * SCALEFACTOR, 0x000 * SCALEFACTOR, (CYCLE_W << 8) * SCALEFACTOR, (CYCLE_H << 8) * SCALEFACTOR, 1, 1, Sprite.ANIMATION_FROZEN, Sprite.SPRITE_ALIGN_TOP | Sprite.SPRITE_ALIGN_LEFT,
        //TURN RIGHT
        (CYCLE_W << 8) * SCALEFACTOR, (CYCLE_H << 8) * SCALEFACTOR, 0x000 * SCALEFACTOR, 0x000 * SCALEFACTOR, (CYCLE_W << 8) * SCALEFACTOR, (CYCLE_H << 8) * SCALEFACTOR, 1, 1, Sprite.ANIMATION_FROZEN, Sprite.SPRITE_ALIGN_TOP | Sprite.SPRITE_ALIGN_LEFT,
        //TURN LEFT
        (CYCLE_W << 8) * SCALEFACTOR, (CYCLE_H << 8) * SCALEFACTOR, 0x000 * SCALEFACTOR, 0x000 * SCALEFACTOR, (CYCLE_W << 8) * SCALEFACTOR, (CYCLE_H << 8) * SCALEFACTOR, 1, 1, Sprite.ANIMATION_FROZEN, Sprite.SPRITE_ALIGN_TOP | Sprite.SPRITE_ALIGN_LEFT,
        //NOT USED (LEFT & RIGHT)
        (CYCLE_W << 8) * SCALEFACTOR, (CYCLE_H << 8) * SCALEFACTOR, 0x000 * SCALEFACTOR, 0x000 * SCALEFACTOR, (CYCLE_W << 8) * SCALEFACTOR, (CYCLE_H << 8) * SCALEFACTOR, 1, 1, Sprite.ANIMATION_FROZEN, Sprite.SPRITE_ALIGN_TOP | Sprite.SPRITE_ALIGN_LEFT,
        //DOWN CAR
        (CYCLE_W << 8) * SCALEFACTOR, (CYCLE_H << 8) * SCALEFACTOR, 0x000 * SCALEFACTOR, 0x000 * SCALEFACTOR, (CYCLE_W << 8) * SCALEFACTOR, (CYCLE_H << 8) * SCALEFACTOR, 1, 1, Sprite.ANIMATION_FROZEN, Sprite.SPRITE_ALIGN_TOP | Sprite.SPRITE_ALIGN_LEFT,
        //DOWN CAR + RIGHT TURN
        (CYCLE_W << 8) * SCALEFACTOR, (CYCLE_H << 8) * SCALEFACTOR, 0x000 * SCALEFACTOR, 0x000 * SCALEFACTOR, (CYCLE_W << 8) * SCALEFACTOR, (CYCLE_H << 8) * SCALEFACTOR, 1, 1, Sprite.ANIMATION_FROZEN, Sprite.SPRITE_ALIGN_TOP | Sprite.SPRITE_ALIGN_LEFT,
        //DOWN CAR + LEFT TURN
        (CYCLE_W << 8) * SCALEFACTOR, (CYCLE_H << 8) * SCALEFACTOR, 0x000 * SCALEFACTOR, 0x000 * SCALEFACTOR, (CYCLE_W << 8) * SCALEFACTOR, (CYCLE_H << 8) * SCALEFACTOR, 1, 1, Sprite.ANIMATION_FROZEN, Sprite.SPRITE_ALIGN_TOP | Sprite.SPRITE_ALIGN_LEFT,
        //HIT CAR
        (CYCLE_W << 8) * SCALEFACTOR, (CYCLE_H << 8) * SCALEFACTOR, 0x000 * SCALEFACTOR, 0x000 * SCALEFACTOR, (CYCLE_W << 8) * SCALEFACTOR, (CYCLE_H << 8) * SCALEFACTOR, 6, 2, Sprite.ANIMATION_FROZEN, Sprite.SPRITE_ALIGN_TOP | Sprite.SPRITE_ALIGN_LEFT,
        //UP CAR
        (CYCLE_W << 8) * SCALEFACTOR, (CYCLE_H << 8) * SCALEFACTOR, 0x000 * SCALEFACTOR, 0x000 * SCALEFACTOR, (CYCLE_W << 8) * SCALEFACTOR, (CYCLE_H << 8) * SCALEFACTOR, 1, 1, Sprite.ANIMATION_FROZEN, Sprite.SPRITE_ALIGN_TOP | Sprite.SPRITE_ALIGN_LEFT,
        //UP CAR  + RIGHT TURN
        (CYCLE_W << 8) * SCALEFACTOR, (CYCLE_H << 8) * SCALEFACTOR, 0x000 * SCALEFACTOR, 0x000 * SCALEFACTOR, (CYCLE_W << 8) * SCALEFACTOR, (CYCLE_H << 8) * SCALEFACTOR, 1, 1, Sprite.ANIMATION_FROZEN, Sprite.SPRITE_ALIGN_TOP | Sprite.SPRITE_ALIGN_LEFT,
        //UP CAR  + LEFT TURN
        (CYCLE_W << 8) * SCALEFACTOR, (CYCLE_H << 8) * SCALEFACTOR, 0x000 * SCALEFACTOR, 0x000 * SCALEFACTOR, (CYCLE_W << 8) * SCALEFACTOR, (CYCLE_H << 8) * SCALEFACTOR, 1, 1, Sprite.ANIMATION_FROZEN, Sprite.SPRITE_ALIGN_TOP | Sprite.SPRITE_ALIGN_LEFT,
        //NOT USED (LEFT & RIGHT)
        (CYCLE_W << 8) * SCALEFACTOR, (CYCLE_H << 8) * SCALEFACTOR, 0x000 * SCALEFACTOR, 0x000 * SCALEFACTOR, (CYCLE_W << 8) * SCALEFACTOR, (CYCLE_H << 8) * SCALEFACTOR, 1, 1, Sprite.ANIMATION_FROZEN, Sprite.SPRITE_ALIGN_TOP | Sprite.SPRITE_ALIGN_LEFT,

       //BULLET
        //NORMAL BULLET
        (BULLET_W << 8) * SCALEFACTOR, (BULLET_H << 8) * SCALEFACTOR, 0x000 * SCALEFACTOR, 0x000 * SCALEFACTOR, (BULLET_W << 8) * SCALEFACTOR, (BULLET_H << 8) * SCALEFACTOR, 1, 1, Sprite.ANIMATION_FROZEN, Sprite.SPRITE_ALIGN_TOP | Sprite.SPRITE_ALIGN_LEFT,
        //TURN RIGHT
        (BULLET_W << 8) * SCALEFACTOR, (BULLET_H << 8) * SCALEFACTOR, 0x000 * SCALEFACTOR, 0x000 * SCALEFACTOR, (BULLET_W << 8) * SCALEFACTOR, (BULLET_H << 8) * SCALEFACTOR, 1, 1, Sprite.ANIMATION_FROZEN, Sprite.SPRITE_ALIGN_TOP | Sprite.SPRITE_ALIGN_LEFT,
        //TURN LEFT
        (BULLET_W << 8) * SCALEFACTOR, (BULLET_H << 8) * SCALEFACTOR, 0x000 * SCALEFACTOR, 0x000 * SCALEFACTOR, (BULLET_W << 8) * SCALEFACTOR, (BULLET_H << 8) * SCALEFACTOR, 1, 1, Sprite.ANIMATION_FROZEN, Sprite.SPRITE_ALIGN_TOP | Sprite.SPRITE_ALIGN_LEFT,
        //NOT USED (LEFT & RIGHT)
        (BULLET_W << 8) * SCALEFACTOR, (BULLET_H << 8) * SCALEFACTOR, 0x000 * SCALEFACTOR, 0x000 * SCALEFACTOR, (BULLET_W << 8) * SCALEFACTOR, (BULLET_H << 8) * SCALEFACTOR, 1, 1, Sprite.ANIMATION_FROZEN, Sprite.SPRITE_ALIGN_TOP | Sprite.SPRITE_ALIGN_LEFT,
        //DOWN CAR
        (BULLET_W << 8) * SCALEFACTOR, (BULLET_H << 8) * SCALEFACTOR, 0x000 * SCALEFACTOR, 0x000 * SCALEFACTOR, (BULLET_W << 8) * SCALEFACTOR, (BULLET_H << 8) * SCALEFACTOR, 1, 1, Sprite.ANIMATION_FROZEN, Sprite.SPRITE_ALIGN_TOP | Sprite.SPRITE_ALIGN_LEFT,
        //DOWN CAR + RIGHT TURN
        (BULLET_W << 8) * SCALEFACTOR, (BULLET_H << 8) * SCALEFACTOR, 0x000 * SCALEFACTOR, 0x000 * SCALEFACTOR, (BULLET_W << 8) * SCALEFACTOR, (BULLET_H << 8) * SCALEFACTOR, 1, 1, Sprite.ANIMATION_FROZEN, Sprite.SPRITE_ALIGN_TOP | Sprite.SPRITE_ALIGN_LEFT,
        //DOWN CAR + LEFT TURN
        (BULLET_W << 8) * SCALEFACTOR, (BULLET_H << 8) * SCALEFACTOR, 0x000 * SCALEFACTOR, 0x000 * SCALEFACTOR, (BULLET_W << 8) * SCALEFACTOR, (BULLET_H << 8) * SCALEFACTOR, 1, 1, Sprite.ANIMATION_FROZEN, Sprite.SPRITE_ALIGN_TOP | Sprite.SPRITE_ALIGN_LEFT,
        //NOT USED (LEFT & RIGHT)
        (BULLET_W << 8) * SCALEFACTOR, (BULLET_H << 8) * SCALEFACTOR, 0x000 * SCALEFACTOR, 0x000 * SCALEFACTOR, (BULLET_W << 8) * SCALEFACTOR, (BULLET_H << 8) * SCALEFACTOR, 1, 1, Sprite.ANIMATION_FROZEN, Sprite.SPRITE_ALIGN_TOP | Sprite.SPRITE_ALIGN_LEFT,
        //UP CAR
        (BULLET_W << 8) * SCALEFACTOR, (BULLET_H << 8) * SCALEFACTOR, 0x000 * SCALEFACTOR, 0x000 * SCALEFACTOR, (BULLET_W << 8) * SCALEFACTOR, (BULLET_H << 8) * SCALEFACTOR, 1, 1, Sprite.ANIMATION_FROZEN, Sprite.SPRITE_ALIGN_TOP | Sprite.SPRITE_ALIGN_LEFT,
        //UP CAR  + RIGHT TURN
        (BULLET_W << 8) * SCALEFACTOR, (BULLET_H << 8) * SCALEFACTOR, 0x000 * SCALEFACTOR, 0x000 * SCALEFACTOR, (BULLET_W << 8) * SCALEFACTOR, (BULLET_H << 8) * SCALEFACTOR, 1, 1, Sprite.ANIMATION_FROZEN, Sprite.SPRITE_ALIGN_TOP | Sprite.SPRITE_ALIGN_LEFT,
        //UP CAR  + LEFT TURN
        (BULLET_W << 8) * SCALEFACTOR, (BULLET_H << 8) * SCALEFACTOR, 0x000 * SCALEFACTOR, 0x000 * SCALEFACTOR, (BULLET_W << 8) * SCALEFACTOR, (BULLET_H << 8) * SCALEFACTOR, 1, 1, Sprite.ANIMATION_FROZEN, Sprite.SPRITE_ALIGN_TOP | Sprite.SPRITE_ALIGN_LEFT,
        //NOT USED (LEFT & RIGHT)
        (BULLET_W << 8) * SCALEFACTOR, (BULLET_H << 8) * SCALEFACTOR, 0x000 * SCALEFACTOR, 0x000 * SCALEFACTOR, (BULLET_W << 8) * SCALEFACTOR, (BULLET_H << 8) * SCALEFACTOR, 1, 1, Sprite.ANIMATION_FROZEN, Sprite.SPRITE_ALIGN_TOP | Sprite.SPRITE_ALIGN_LEFT,

        //TREE
        //NORMAL TREE
        (TREE_W << 8) * SCALEFACTOR, (TREE_H << 8) * SCALEFACTOR, 0x000 * SCALEFACTOR, 0x000 * SCALEFACTOR, (TREE_W << 8) * SCALEFACTOR, (TREE_H << 8) * SCALEFACTOR, 1, 1, Sprite.ANIMATION_FROZEN, Sprite.SPRITE_ALIGN_TOP | Sprite.SPRITE_ALIGN_LEFT,
        //TURN RIGHT
        (TREE_W << 8) * SCALEFACTOR, (TREE_H << 8) * SCALEFACTOR, 0x000 * SCALEFACTOR, 0x000 * SCALEFACTOR, (TREE_W << 8) * SCALEFACTOR, (TREE_H << 8) * SCALEFACTOR, 1, 1, Sprite.ANIMATION_FROZEN, Sprite.SPRITE_ALIGN_TOP | Sprite.SPRITE_ALIGN_LEFT,
        //TURN LEFT
        (TREE_W << 8) * SCALEFACTOR, (TREE_H << 8) * SCALEFACTOR, 0x000 * SCALEFACTOR, 0x000 * SCALEFACTOR, (TREE_W << 8) * SCALEFACTOR, (TREE_H << 8) * SCALEFACTOR, 1, 1, Sprite.ANIMATION_FROZEN, Sprite.SPRITE_ALIGN_TOP | Sprite.SPRITE_ALIGN_LEFT,
        //NOT USED (LEFT & RIGHT)
        (TREE_W << 8) * SCALEFACTOR, (TREE_H << 8) * SCALEFACTOR, 0x000 * SCALEFACTOR, 0x000 * SCALEFACTOR, (TREE_W << 8) * SCALEFACTOR, (TREE_H << 8) * SCALEFACTOR, 1, 1, Sprite.ANIMATION_FROZEN, Sprite.SPRITE_ALIGN_TOP | Sprite.SPRITE_ALIGN_LEFT,
        //DOWN CAR
        (TREE_W << 8) * SCALEFACTOR, (TREE_H << 8) * SCALEFACTOR, 0x000 * SCALEFACTOR, 0x000 * SCALEFACTOR, (TREE_W << 8) * SCALEFACTOR, (TREE_H << 8) * SCALEFACTOR, 1, 1, Sprite.ANIMATION_FROZEN, Sprite.SPRITE_ALIGN_TOP | Sprite.SPRITE_ALIGN_LEFT,
        //DOWN CAR + RIGHT TURN
        (TREE_W << 8) * SCALEFACTOR, (TREE_H << 8) * SCALEFACTOR, 0x000 * SCALEFACTOR, 0x000 * SCALEFACTOR, (TREE_W << 8) * SCALEFACTOR, (TREE_H << 8) * SCALEFACTOR, 1, 1, Sprite.ANIMATION_FROZEN, Sprite.SPRITE_ALIGN_TOP | Sprite.SPRITE_ALIGN_LEFT,
        //DOWN CAR + LEFT TURN
        (TREE_W << 8) * SCALEFACTOR, (TREE_H << 8) * SCALEFACTOR, 0x000 * SCALEFACTOR, 0x000 * SCALEFACTOR, (TREE_W << 8) * SCALEFACTOR, (TREE_H << 8) * SCALEFACTOR, 1, 1, Sprite.ANIMATION_FROZEN, Sprite.SPRITE_ALIGN_TOP | Sprite.SPRITE_ALIGN_LEFT,
        //NOT USED (LEFT & RIGHT)
        (TREE_W << 8) * SCALEFACTOR, (TREE_H << 8) * SCALEFACTOR, 0x000 * SCALEFACTOR, 0x000 * SCALEFACTOR, (TREE_W << 8) * SCALEFACTOR, (TREE_H << 8) * SCALEFACTOR, 1, 1, Sprite.ANIMATION_FROZEN, Sprite.SPRITE_ALIGN_TOP | Sprite.SPRITE_ALIGN_LEFT,
        //UP CAR
        (TREE_W << 8) * SCALEFACTOR, (TREE_H << 8) * SCALEFACTOR, 0x000 * SCALEFACTOR, 0x000 * SCALEFACTOR, (TREE_W << 8) * SCALEFACTOR, (TREE_H << 8) * SCALEFACTOR, 1, 1, Sprite.ANIMATION_FROZEN, Sprite.SPRITE_ALIGN_TOP | Sprite.SPRITE_ALIGN_LEFT,
        //UP CAR  + RIGHT TURN
        (TREE_W << 8) * SCALEFACTOR, (TREE_H << 8) * SCALEFACTOR, 0x000 * SCALEFACTOR, 0x000 * SCALEFACTOR, (TREE_W << 8) * SCALEFACTOR, (TREE_H << 8) * SCALEFACTOR, 1, 1, Sprite.ANIMATION_FROZEN, Sprite.SPRITE_ALIGN_TOP | Sprite.SPRITE_ALIGN_LEFT,
        //UP CAR  + LEFT TURN
        (TREE_W << 8) * SCALEFACTOR, (TREE_H << 8) * SCALEFACTOR, 0x000 * SCALEFACTOR, 0x000 * SCALEFACTOR, (TREE_W << 8) * SCALEFACTOR, (TREE_H << 8) * SCALEFACTOR, 1, 1, Sprite.ANIMATION_FROZEN, Sprite.SPRITE_ALIGN_TOP | Sprite.SPRITE_ALIGN_LEFT,
        //NOT USED (LEFT & RIGHT)
        (TREE_W << 8) * SCALEFACTOR, (TREE_H << 8) * SCALEFACTOR, 0x000 * SCALEFACTOR, 0x000 * SCALEFACTOR, (TREE_W << 8) * SCALEFACTOR, (TREE_H << 8) * SCALEFACTOR, 1, 1, Sprite.ANIMATION_FROZEN, Sprite.SPRITE_ALIGN_TOP | Sprite.SPRITE_ALIGN_LEFT,

        //SIGN
        //NORMAL SIGN
        (SIGN_W << 8) * SCALEFACTOR, (SIGN_H << 8) * SCALEFACTOR, 0x000 * SCALEFACTOR, 0x000 * SCALEFACTOR, (SIGN_W << 8) * SCALEFACTOR, (SIGN_H << 8) * SCALEFACTOR, 1, 1, Sprite.ANIMATION_FROZEN, Sprite.SPRITE_ALIGN_TOP | Sprite.SPRITE_ALIGN_LEFT,
        //TURN RIGHT
        (SIGN_W << 8) * SCALEFACTOR, (SIGN_H << 8) * SCALEFACTOR, 0x000 * SCALEFACTOR, 0x000 * SCALEFACTOR, (SIGN_W << 8) * SCALEFACTOR, (SIGN_H << 8) * SCALEFACTOR, 1, 1, Sprite.ANIMATION_FROZEN, Sprite.SPRITE_ALIGN_TOP | Sprite.SPRITE_ALIGN_LEFT,
        //TURN LEFT
        (SIGN_W << 8) * SCALEFACTOR, (SIGN_H << 8) * SCALEFACTOR, 0x000 * SCALEFACTOR, 0x000 * SCALEFACTOR, (SIGN_W << 8) * SCALEFACTOR, (SIGN_H << 8) * SCALEFACTOR, 1, 1, Sprite.ANIMATION_FROZEN, Sprite.SPRITE_ALIGN_TOP | Sprite.SPRITE_ALIGN_LEFT,
        //NOT USED (LEFT & RIGHT)
        (SIGN_W << 8) * SCALEFACTOR, (SIGN_H << 8) * SCALEFACTOR, 0x000 * SCALEFACTOR, 0x000 * SCALEFACTOR, (SIGN_W << 8) * SCALEFACTOR, (SIGN_H << 8) * SCALEFACTOR, 1, 1, Sprite.ANIMATION_FROZEN, Sprite.SPRITE_ALIGN_TOP | Sprite.SPRITE_ALIGN_LEFT,
        //DOWN CAR
        (SIGN_W << 8) * SCALEFACTOR, (SIGN_H << 8) * SCALEFACTOR, 0x000 * SCALEFACTOR, 0x000 * SCALEFACTOR, (SIGN_W << 8) * SCALEFACTOR, (SIGN_H << 8) * SCALEFACTOR, 1, 1, Sprite.ANIMATION_FROZEN, Sprite.SPRITE_ALIGN_TOP | Sprite.SPRITE_ALIGN_LEFT,
        //DOWN CAR + RIGHT TURN
        (SIGN_W << 8) * SCALEFACTOR, (SIGN_H << 8) * SCALEFACTOR, 0x000 * SCALEFACTOR, 0x000 * SCALEFACTOR, (SIGN_W << 8) * SCALEFACTOR, (SIGN_H << 8) * SCALEFACTOR, 1, 1, Sprite.ANIMATION_FROZEN, Sprite.SPRITE_ALIGN_TOP | Sprite.SPRITE_ALIGN_LEFT,
        //DOWN CAR + LEFT TURN
        (SIGN_W << 8) * SCALEFACTOR, (SIGN_H << 8) * SCALEFACTOR, 0x000 * SCALEFACTOR, 0x000 * SCALEFACTOR, (SIGN_W << 8) * SCALEFACTOR, (SIGN_H << 8) * SCALEFACTOR, 1, 1, Sprite.ANIMATION_FROZEN, Sprite.SPRITE_ALIGN_TOP | Sprite.SPRITE_ALIGN_LEFT,
        //NOT USED (LEFT & RIGHT)
        (SIGN_W << 8) * SCALEFACTOR, (SIGN_H << 8) * SCALEFACTOR, 0x000 * SCALEFACTOR, 0x000 * SCALEFACTOR, (SIGN_W << 8) * SCALEFACTOR, (SIGN_H << 8) * SCALEFACTOR, 1, 1, Sprite.ANIMATION_FROZEN, Sprite.SPRITE_ALIGN_TOP | Sprite.SPRITE_ALIGN_LEFT,
        //UP CAR
        (SIGN_W << 8) * SCALEFACTOR, (SIGN_H << 8) * SCALEFACTOR, 0x000 * SCALEFACTOR, 0x000 * SCALEFACTOR, (SIGN_W << 8) * SCALEFACTOR, (SIGN_H << 8) * SCALEFACTOR, 1, 1, Sprite.ANIMATION_FROZEN, Sprite.SPRITE_ALIGN_TOP | Sprite.SPRITE_ALIGN_LEFT,
        //UP CAR  + RIGHT TURN
        (SIGN_W << 8) * SCALEFACTOR, (SIGN_H << 8) * SCALEFACTOR, 0x000 * SCALEFACTOR, 0x000 * SCALEFACTOR, (SIGN_W << 8) * SCALEFACTOR, (SIGN_H << 8) * SCALEFACTOR, 1, 1, Sprite.ANIMATION_FROZEN, Sprite.SPRITE_ALIGN_TOP | Sprite.SPRITE_ALIGN_LEFT,
        //UP CAR  + LEFT TURN
        (SIGN_W << 8) * SCALEFACTOR, (SIGN_H << 8) * SCALEFACTOR, 0x000 * SCALEFACTOR, 0x000 * SCALEFACTOR, (SIGN_W << 8) * SCALEFACTOR, (SIGN_H << 8) * SCALEFACTOR, 1, 1, Sprite.ANIMATION_FROZEN, Sprite.SPRITE_ALIGN_TOP | Sprite.SPRITE_ALIGN_LEFT,
        //NOT USED (LEFT & RIGHT)
        (SIGN_W << 8) * SCALEFACTOR, (SIGN_H << 8) * SCALEFACTOR, 0x000 * SCALEFACTOR, 0x000 * SCALEFACTOR, (SIGN_W << 8) * SCALEFACTOR, (SIGN_H << 8) * SCALEFACTOR, 1, 1, Sprite.ANIMATION_FROZEN, Sprite.SPRITE_ALIGN_TOP | Sprite.SPRITE_ALIGN_LEFT,

        //PUDDLE
        //NORMAL PUDDLE
        (PUDDLE_W << 8) * SCALEFACTOR, (PUDDLE_H << 8) * SCALEFACTOR, 0x000 * SCALEFACTOR, 0x000 * SCALEFACTOR, (PUDDLE_W << 8) * SCALEFACTOR, (PUDDLE_H << 8) * SCALEFACTOR, 1, 1, Sprite.ANIMATION_FROZEN, Sprite.SPRITE_ALIGN_TOP | Sprite.SPRITE_ALIGN_LEFT,
        //PUDDLE DROPS
        (DROPS_PUDDLE_W << 8) * SCALEFACTOR, (DROPS_PUDDLE_H << 8) * SCALEFACTOR, 0x000 * SCALEFACTOR, 0x000 * SCALEFACTOR, (DROPS_PUDDLE_W << 8) * SCALEFACTOR, (DROPS_PUDDLE_H << 8) * SCALEFACTOR, 5, 2, Sprite.ANIMATION_FROZEN, Sprite.SPRITE_ALIGN_TOP | Sprite.SPRITE_ALIGN_LEFT,
        //TURN LEFT
        (PUDDLE_W << 8) * SCALEFACTOR, (PUDDLE_H << 8) * SCALEFACTOR, 0x000 * SCALEFACTOR, 0x000 * SCALEFACTOR, (PUDDLE_W << 8) * SCALEFACTOR, (PUDDLE_H << 8) * SCALEFACTOR, 1, 1, Sprite.ANIMATION_FROZEN, Sprite.SPRITE_ALIGN_TOP | Sprite.SPRITE_ALIGN_LEFT,
        //NOT USED (LEFT & RIGHT)
        (PUDDLE_W << 8) * SCALEFACTOR, (PUDDLE_H << 8) * SCALEFACTOR, 0x000 * SCALEFACTOR, 0x000 * SCALEFACTOR, (PUDDLE_W << 8) * SCALEFACTOR, (PUDDLE_H << 8) * SCALEFACTOR, 1, 1, Sprite.ANIMATION_FROZEN, Sprite.SPRITE_ALIGN_TOP | Sprite.SPRITE_ALIGN_LEFT,
        //DOWN CAR
        (PUDDLE_W << 8) * SCALEFACTOR, (PUDDLE_H << 8) * SCALEFACTOR, 0x000 * SCALEFACTOR, 0x000 * SCALEFACTOR, (PUDDLE_W << 8) * SCALEFACTOR, (PUDDLE_H << 8) * SCALEFACTOR, 1, 1, Sprite.ANIMATION_FROZEN, Sprite.SPRITE_ALIGN_TOP | Sprite.SPRITE_ALIGN_LEFT,
        //DOWN CAR + RIGHT TURN
        (PUDDLE_W << 8) * SCALEFACTOR, (PUDDLE_H << 8) * SCALEFACTOR, 0x000 * SCALEFACTOR, 0x000 * SCALEFACTOR, (PUDDLE_W << 8) * SCALEFACTOR, (PUDDLE_H << 8) * SCALEFACTOR, 1, 1, Sprite.ANIMATION_FROZEN, Sprite.SPRITE_ALIGN_TOP | Sprite.SPRITE_ALIGN_LEFT,
        //DOWN CAR + LEFT TURN
        (PUDDLE_W << 8) * SCALEFACTOR, (PUDDLE_H << 8) * SCALEFACTOR, 0x000 * SCALEFACTOR, 0x000 * SCALEFACTOR, (PUDDLE_W << 8) * SCALEFACTOR, (PUDDLE_H << 8) * SCALEFACTOR, 1, 1, Sprite.ANIMATION_FROZEN, Sprite.SPRITE_ALIGN_TOP | Sprite.SPRITE_ALIGN_LEFT,
        //NOT USED (LEFT & RIGHT)
        (PUDDLE_W << 8) * SCALEFACTOR, (PUDDLE_H << 8) * SCALEFACTOR, 0x000 * SCALEFACTOR, 0x000 * SCALEFACTOR, (PUDDLE_W << 8) * SCALEFACTOR, (PUDDLE_H << 8) * SCALEFACTOR, 1, 1, Sprite.ANIMATION_FROZEN, Sprite.SPRITE_ALIGN_TOP | Sprite.SPRITE_ALIGN_LEFT,
        //UP CAR
        (PUDDLE_W << 8) * SCALEFACTOR, (PUDDLE_H << 8) * SCALEFACTOR, 0x000 * SCALEFACTOR, 0x000 * SCALEFACTOR, (PUDDLE_W << 8) * SCALEFACTOR, (PUDDLE_H << 8) * SCALEFACTOR, 1, 1, Sprite.ANIMATION_FROZEN, Sprite.SPRITE_ALIGN_TOP | Sprite.SPRITE_ALIGN_LEFT,
        //UP CAR  + RIGHT TURN
        (PUDDLE_W << 8) * SCALEFACTOR, (PUDDLE_H << 8) * SCALEFACTOR, 0x000 * SCALEFACTOR, 0x000 * SCALEFACTOR, (PUDDLE_W << 8) * SCALEFACTOR, (PUDDLE_H << 8) * SCALEFACTOR, 1, 1, Sprite.ANIMATION_FROZEN, Sprite.SPRITE_ALIGN_TOP | Sprite.SPRITE_ALIGN_LEFT,
        //UP CAR  + LEFT TURN
        (PUDDLE_W << 8) * SCALEFACTOR, (PUDDLE_H << 8) * SCALEFACTOR, 0x000 * SCALEFACTOR, 0x000 * SCALEFACTOR, (PUDDLE_W << 8) * SCALEFACTOR, (PUDDLE_H << 8) * SCALEFACTOR, 1, 1, Sprite.ANIMATION_FROZEN, Sprite.SPRITE_ALIGN_TOP | Sprite.SPRITE_ALIGN_LEFT,
        //NOT USED (LEFT & RIGHT)
        (PUDDLE_W << 8) * SCALEFACTOR, (PUDDLE_H << 8) * SCALEFACTOR, 0x000 * SCALEFACTOR, 0x000 * SCALEFACTOR, (PUDDLE_W << 8) * SCALEFACTOR, (PUDDLE_H << 8) * SCALEFACTOR, 1, 1, Sprite.ANIMATION_FROZEN, Sprite.SPRITE_ALIGN_TOP | Sprite.SPRITE_ALIGN_LEFT,

        //BOX
        //NORMAL BOX
        (BOX_W << 8) * SCALEFACTOR, (BOX_H << 8) * SCALEFACTOR, 0x000 * SCALEFACTOR, 0x000 * SCALEFACTOR, (BOX_W << 8) * SCALEFACTOR, (BOX_H << 8) * SCALEFACTOR, 1, 1, Sprite.ANIMATION_FROZEN, Sprite.SPRITE_ALIGN_TOP | Sprite.SPRITE_ALIGN_LEFT,
        //EXPLOSION BOX
        (EXPL_BOX_W << 8) * SCALEFACTOR, (EXPL_BOX_H << 8) * SCALEFACTOR, 0x000 * SCALEFACTOR, 0x000 * SCALEFACTOR, (EXPL_BOX_W << 8) * SCALEFACTOR, (EXPL_BOX_H << 8) * SCALEFACTOR, 2, 2, Sprite.ANIMATION_FROZEN, Sprite.SPRITE_ALIGN_TOP | Sprite.SPRITE_ALIGN_LEFT,
        //TURN LEFT
        (BOX_W << 8) * SCALEFACTOR, (BOX_H << 8) * SCALEFACTOR, 0x000 * SCALEFACTOR, 0x000 * SCALEFACTOR, (BOX_W << 8) * SCALEFACTOR, (BOX_H << 8) * SCALEFACTOR, 1, 1, Sprite.ANIMATION_FROZEN, Sprite.SPRITE_ALIGN_TOP | Sprite.SPRITE_ALIGN_LEFT,
        //NOT USED (LEFT & RIGHT)
        (BOX_W << 8) * SCALEFACTOR, (BOX_H << 8) * SCALEFACTOR, 0x000 * SCALEFACTOR, 0x000 * SCALEFACTOR, (BOX_W << 8) * SCALEFACTOR, (BOX_H << 8) * SCALEFACTOR, 1, 1, Sprite.ANIMATION_FROZEN, Sprite.SPRITE_ALIGN_TOP | Sprite.SPRITE_ALIGN_LEFT,
        //DOWN CAR
        (BOX_W << 8) * SCALEFACTOR, (BOX_H << 8) * SCALEFACTOR, 0x000 * SCALEFACTOR, 0x000 * SCALEFACTOR, (BOX_W << 8) * SCALEFACTOR, (BOX_H << 8) * SCALEFACTOR, 1, 1, Sprite.ANIMATION_FROZEN, Sprite.SPRITE_ALIGN_TOP | Sprite.SPRITE_ALIGN_LEFT,
        //DOWN CAR + RIGHT TURN
        (BOX_W << 8) * SCALEFACTOR, (BOX_H << 8) * SCALEFACTOR, 0x000 * SCALEFACTOR, 0x000 * SCALEFACTOR, (BOX_W << 8) * SCALEFACTOR, (BOX_H << 8) * SCALEFACTOR, 1, 1, Sprite.ANIMATION_FROZEN, Sprite.SPRITE_ALIGN_TOP | Sprite.SPRITE_ALIGN_LEFT,
        //DOWN CAR + LEFT TURN
        (BOX_W << 8) * SCALEFACTOR, (BOX_H << 8) * SCALEFACTOR, 0x000 * SCALEFACTOR, 0x000 * SCALEFACTOR, (BOX_W << 8) * SCALEFACTOR, (BOX_H << 8) * SCALEFACTOR, 1, 1, Sprite.ANIMATION_FROZEN, Sprite.SPRITE_ALIGN_TOP | Sprite.SPRITE_ALIGN_LEFT,
        //NOT USED (LEFT & RIGHT)
        (BOX_W << 8) * SCALEFACTOR, (BOX_H << 8) * SCALEFACTOR, 0x000 * SCALEFACTOR, 0x000 * SCALEFACTOR, (BOX_W << 8) * SCALEFACTOR, (BOX_H << 8) * SCALEFACTOR, 1, 1, Sprite.ANIMATION_FROZEN, Sprite.SPRITE_ALIGN_TOP | Sprite.SPRITE_ALIGN_LEFT,
        //UP CAR
        (BOX_W << 8) * SCALEFACTOR, (BOX_H << 8) * SCALEFACTOR, 0x000 * SCALEFACTOR, 0x000 * SCALEFACTOR, (BOX_W << 8) * SCALEFACTOR, (BOX_H << 8) * SCALEFACTOR, 1, 1, Sprite.ANIMATION_FROZEN, Sprite.SPRITE_ALIGN_TOP | Sprite.SPRITE_ALIGN_LEFT,
        //UP CAR  + RIGHT TURN
        (BOX_W << 8) * SCALEFACTOR, (BOX_H << 8) * SCALEFACTOR, 0x000 * SCALEFACTOR, 0x000 * SCALEFACTOR, (BOX_W << 8) * SCALEFACTOR, (BOX_H << 8) * SCALEFACTOR, 1, 1, Sprite.ANIMATION_FROZEN, Sprite.SPRITE_ALIGN_TOP | Sprite.SPRITE_ALIGN_LEFT,
        //UP CAR  + LEFT TURN
        (BOX_W << 8) * SCALEFACTOR, (BOX_H << 8) * SCALEFACTOR, 0x000 * SCALEFACTOR, 0x000 * SCALEFACTOR, (BOX_W << 8) * SCALEFACTOR, (BOX_H << 8) * SCALEFACTOR, 1, 1, Sprite.ANIMATION_FROZEN, Sprite.SPRITE_ALIGN_TOP | Sprite.SPRITE_ALIGN_LEFT,
        //NOT USED (LEFT & RIGHT)
        (BOX_W << 8) * SCALEFACTOR, (BOX_H << 8) * SCALEFACTOR, 0x000 * SCALEFACTOR, 0x000 * SCALEFACTOR, (BOX_W << 8) * SCALEFACTOR, (BOX_H << 8) * SCALEFACTOR, 1, 1, Sprite.ANIMATION_FROZEN, Sprite.SPRITE_ALIGN_TOP | Sprite.SPRITE_ALIGN_LEFT,

        //RIFLEMAN
        //NORMAL RIFLEMAN
        (RIFLEMAN_W << 8) * SCALEFACTOR, (RIFLEMAN_H << 8) * SCALEFACTOR, 0x000 * SCALEFACTOR, 0x000 * SCALEFACTOR, (RIFLEMAN_W << 8) * SCALEFACTOR, (RIFLEMAN_H << 8) * SCALEFACTOR, 3, 1, Sprite.ANIMATION_FROZEN, Sprite.SPRITE_ALIGN_TOP | Sprite.SPRITE_ALIGN_LEFT,
        //EXPLOSION RIFLEMAN
        (RIFLEMAN_W << 8) * SCALEFACTOR, (RIFLEMAN_H << 8) * SCALEFACTOR, 0x000 * SCALEFACTOR, 0x000 * SCALEFACTOR, (RIFLEMAN_W << 8) * SCALEFACTOR, (RIFLEMAN_H << 8) * SCALEFACTOR, 4, 2, Sprite.ANIMATION_FROZEN, Sprite.SPRITE_ALIGN_TOP | Sprite.SPRITE_ALIGN_LEFT,
        //TURN LEFT
        (RIFLEMAN_W << 8) * SCALEFACTOR, (RIFLEMAN_H << 8) * SCALEFACTOR, 0x000 * SCALEFACTOR, 0x000 * SCALEFACTOR, (RIFLEMAN_W << 8) * SCALEFACTOR, (RIFLEMAN_H << 8) * SCALEFACTOR, 1, 1, Sprite.ANIMATION_FROZEN, Sprite.SPRITE_ALIGN_TOP | Sprite.SPRITE_ALIGN_LEFT,
        //NOT USED (LEFT & RIGHT)
        (RIFLEMAN_W << 8) * SCALEFACTOR, (RIFLEMAN_H << 8) * SCALEFACTOR, 0x000 * SCALEFACTOR, 0x000 * SCALEFACTOR, (RIFLEMAN_W << 8) * SCALEFACTOR, (RIFLEMAN_H << 8) * SCALEFACTOR, 1, 1, Sprite.ANIMATION_FROZEN, Sprite.SPRITE_ALIGN_TOP | Sprite.SPRITE_ALIGN_LEFT,
        //DOWN CAR
        (RIFLEMAN_W << 8) * SCALEFACTOR, (RIFLEMAN_H << 8) * SCALEFACTOR, 0x000 * SCALEFACTOR, 0x000 * SCALEFACTOR, (RIFLEMAN_W << 8) * SCALEFACTOR, (RIFLEMAN_H << 8) * SCALEFACTOR, 1, 1, Sprite.ANIMATION_FROZEN, Sprite.SPRITE_ALIGN_TOP | Sprite.SPRITE_ALIGN_LEFT,
        //DOWN CAR + RIGHT TURN
        (RIFLEMAN_W << 8) * SCALEFACTOR, (RIFLEMAN_H << 8) * SCALEFACTOR, 0x000 * SCALEFACTOR, 0x000 * SCALEFACTOR, (RIFLEMAN_W << 8) * SCALEFACTOR, (RIFLEMAN_H << 8) * SCALEFACTOR, 1, 1, Sprite.ANIMATION_FROZEN, Sprite.SPRITE_ALIGN_TOP | Sprite.SPRITE_ALIGN_LEFT,
        //DOWN CAR + LEFT TURN
        (RIFLEMAN_W << 8) * SCALEFACTOR, (RIFLEMAN_H << 8) * SCALEFACTOR, 0x000 * SCALEFACTOR, 0x000 * SCALEFACTOR, (RIFLEMAN_W << 8) * SCALEFACTOR, (RIFLEMAN_H << 8) * SCALEFACTOR, 1, 1, Sprite.ANIMATION_FROZEN, Sprite.SPRITE_ALIGN_TOP | Sprite.SPRITE_ALIGN_LEFT,
        //NOT USED (LEFT & RIGHT)
        (RIFLEMAN_W << 8) * SCALEFACTOR, (RIFLEMAN_H << 8) * SCALEFACTOR, 0x000 * SCALEFACTOR, 0x000 * SCALEFACTOR, (RIFLEMAN_W << 8) * SCALEFACTOR, (RIFLEMAN_H << 8) * SCALEFACTOR, 1, 1, Sprite.ANIMATION_FROZEN, Sprite.SPRITE_ALIGN_TOP | Sprite.SPRITE_ALIGN_LEFT,
        //UP CAR
        (RIFLEMAN_W << 8) * SCALEFACTOR, (RIFLEMAN_H << 8) * SCALEFACTOR, 0x000 * SCALEFACTOR, 0x000 * SCALEFACTOR, (RIFLEMAN_W << 8) * SCALEFACTOR, (RIFLEMAN_H << 8) * SCALEFACTOR, 1, 1, Sprite.ANIMATION_FROZEN, Sprite.SPRITE_ALIGN_TOP | Sprite.SPRITE_ALIGN_LEFT,
        //UP CAR  + RIGHT TURN
        (RIFLEMAN_W << 8) * SCALEFACTOR, (RIFLEMAN_H << 8) * SCALEFACTOR, 0x000 * SCALEFACTOR, 0x000 * SCALEFACTOR, (RIFLEMAN_W << 8) * SCALEFACTOR, (RIFLEMAN_H << 8) * SCALEFACTOR, 1, 1, Sprite.ANIMATION_FROZEN, Sprite.SPRITE_ALIGN_TOP | Sprite.SPRITE_ALIGN_LEFT,
        //UP CAR  + LEFT TURN
        (RIFLEMAN_W << 8) * SCALEFACTOR, (RIFLEMAN_H << 8) * SCALEFACTOR, 0x000 * SCALEFACTOR, 0x000 * SCALEFACTOR, (RIFLEMAN_W << 8) * SCALEFACTOR, (RIFLEMAN_H << 8) * SCALEFACTOR, 1, 1, Sprite.ANIMATION_FROZEN, Sprite.SPRITE_ALIGN_TOP | Sprite.SPRITE_ALIGN_LEFT,
        //NOT USED (LEFT & RIGHT)
        (RIFLEMAN_W << 8) * SCALEFACTOR, (RIFLEMAN_H << 8) * SCALEFACTOR, 0x000 * SCALEFACTOR, 0x000 * SCALEFACTOR, (RIFLEMAN_W << 8) * SCALEFACTOR, (RIFLEMAN_H << 8) * SCALEFACTOR, 1, 1, Sprite.ANIMATION_FROZEN, Sprite.SPRITE_ALIGN_TOP | Sprite.SPRITE_ALIGN_LEFT,

        //EXPLOSION
        //NORMAL EXPLOSION
        (EXPLOSION_W << 8) * SCALEFACTOR, (EXPLOSION_H << 8) * SCALEFACTOR, 0x000 * SCALEFACTOR, 0x000 * SCALEFACTOR, (EXPLOSION_W << 8) * SCALEFACTOR, (EXPLOSION_H << 8) * SCALEFACTOR, 5, 1, Sprite.ANIMATION_FROZEN, Sprite.SPRITE_ALIGN_TOP | Sprite.SPRITE_ALIGN_LEFT,
        //EXPLOSION
        (EXPLOSION_W << 8) * SCALEFACTOR, (EXPLOSION_H << 8) * SCALEFACTOR, 0x000 * SCALEFACTOR, 0x000 * SCALEFACTOR, (EXPLOSION_W << 8) * SCALEFACTOR, (EXPLOSION_H << 8) * SCALEFACTOR, 1, 1, Sprite.ANIMATION_FROZEN, Sprite.SPRITE_ALIGN_TOP | Sprite.SPRITE_ALIGN_LEFT,
        //TURN LEFT
        (EXPLOSION_W << 8) * SCALEFACTOR, (EXPLOSION_H << 8) * SCALEFACTOR, 0x000 * SCALEFACTOR, 0x000 * SCALEFACTOR, (EXPLOSION_W << 8) * SCALEFACTOR, (EXPLOSION_H << 8) * SCALEFACTOR, 1, 1, Sprite.ANIMATION_FROZEN, Sprite.SPRITE_ALIGN_TOP | Sprite.SPRITE_ALIGN_LEFT,
        //NOT USED (LEFT & RIGHT)
        (EXPLOSION_W << 8) * SCALEFACTOR, (EXPLOSION_H << 8) * SCALEFACTOR, 0x000 * SCALEFACTOR, 0x000 * SCALEFACTOR, (EXPLOSION_W << 8) * SCALEFACTOR, (EXPLOSION_H << 8) * SCALEFACTOR, 1, 1, Sprite.ANIMATION_FROZEN, Sprite.SPRITE_ALIGN_TOP | Sprite.SPRITE_ALIGN_LEFT,
        //DOWN CAR
        (EXPLOSION_W << 8) * SCALEFACTOR, (EXPLOSION_H << 8) * SCALEFACTOR, 0x000 * SCALEFACTOR, 0x000 * SCALEFACTOR, (EXPLOSION_W << 8) * SCALEFACTOR, (EXPLOSION_H << 8) * SCALEFACTOR, 1, 1, Sprite.ANIMATION_FROZEN, Sprite.SPRITE_ALIGN_TOP | Sprite.SPRITE_ALIGN_LEFT,
        //DOWN CAR + RIGHT TURN
        (EXPLOSION_W << 8) * SCALEFACTOR, (EXPLOSION_H << 8) * SCALEFACTOR, 0x000 * SCALEFACTOR, 0x000 * SCALEFACTOR, (EXPLOSION_W << 8) * SCALEFACTOR, (EXPLOSION_H << 8) * SCALEFACTOR, 1, 1, Sprite.ANIMATION_FROZEN, Sprite.SPRITE_ALIGN_TOP | Sprite.SPRITE_ALIGN_LEFT,
        //DOWN CAR + LEFT TURN
        (EXPLOSION_W << 8) * SCALEFACTOR, (EXPLOSION_H << 8) * SCALEFACTOR, 0x000 * SCALEFACTOR, 0x000 * SCALEFACTOR, (EXPLOSION_W << 8) * SCALEFACTOR, (EXPLOSION_H << 8) * SCALEFACTOR, 1, 1, Sprite.ANIMATION_FROZEN, Sprite.SPRITE_ALIGN_TOP | Sprite.SPRITE_ALIGN_LEFT,
        //NOT USED (LEFT & RIGHT)
        (EXPLOSION_W << 8) * SCALEFACTOR, (EXPLOSION_H << 8) * SCALEFACTOR, 0x000 * SCALEFACTOR, 0x000 * SCALEFACTOR, (EXPLOSION_W << 8) * SCALEFACTOR, (EXPLOSION_H << 8) * SCALEFACTOR, 1, 1, Sprite.ANIMATION_FROZEN, Sprite.SPRITE_ALIGN_TOP | Sprite.SPRITE_ALIGN_LEFT,
        //UP CAR
        (EXPLOSION_W << 8) * SCALEFACTOR, (EXPLOSION_H << 8) * SCALEFACTOR, 0x000 * SCALEFACTOR, 0x000 * SCALEFACTOR, (EXPLOSION_W << 8) * SCALEFACTOR, (EXPLOSION_H << 8) * SCALEFACTOR, 1, 1, Sprite.ANIMATION_FROZEN, Sprite.SPRITE_ALIGN_TOP | Sprite.SPRITE_ALIGN_LEFT,
        //UP CAR  + RIGHT TURN
        (EXPLOSION_W << 8) * SCALEFACTOR, (EXPLOSION_H << 8) * SCALEFACTOR, 0x000 * SCALEFACTOR, 0x000 * SCALEFACTOR, (EXPLOSION_W << 8) * SCALEFACTOR, (EXPLOSION_H << 8) * SCALEFACTOR, 1, 1, Sprite.ANIMATION_FROZEN, Sprite.SPRITE_ALIGN_TOP | Sprite.SPRITE_ALIGN_LEFT,
        //UP CAR  + LEFT TURN
        (EXPLOSION_W << 8) * SCALEFACTOR, (EXPLOSION_H << 8) * SCALEFACTOR, 0x000 * SCALEFACTOR, 0x000 * SCALEFACTOR, (EXPLOSION_W << 8) * SCALEFACTOR, (EXPLOSION_H << 8) * SCALEFACTOR, 1, 1, Sprite.ANIMATION_FROZEN, Sprite.SPRITE_ALIGN_TOP | Sprite.SPRITE_ALIGN_LEFT,
        //NOT USED (LEFT & RIGHT)
        (EXPLOSION_W << 8) * SCALEFACTOR, (EXPLOSION_H << 8) * SCALEFACTOR, 0x000 * SCALEFACTOR, 0x000 * SCALEFACTOR, (EXPLOSION_W << 8) * SCALEFACTOR, (EXPLOSION_H << 8) * SCALEFACTOR, 1, 1, Sprite.ANIMATION_FROZEN, Sprite.SPRITE_ALIGN_TOP | Sprite.SPRITE_ALIGN_LEFT,

        //TUNNEL_ENTRANCE
        //NORMAL TUNNEL_ENTRANCE
        (TUNNEL_ENTRANCE_W << 8) * SCALEFACTOR, (TUNNEL_ENTRANCE_H << 8) * SCALEFACTOR, 0x000 * SCALEFACTOR, 0x000 * SCALEFACTOR, (TUNNEL_ENTRANCE_W << 8) * SCALEFACTOR, (TUNNEL_ENTRANCE_H << 8) * SCALEFACTOR, 3, 1, Sprite.ANIMATION_FROZEN, Sprite.SPRITE_ALIGN_TOP | Sprite.SPRITE_ALIGN_LEFT,
        //TUNNEL_ENTRANCE
        (TUNNEL_ENTRANCE_W << 8) * SCALEFACTOR, (TUNNEL_ENTRANCE_H << 8) * SCALEFACTOR, 0x000 * SCALEFACTOR, 0x000 * SCALEFACTOR, (TUNNEL_ENTRANCE_W << 8) * SCALEFACTOR, (TUNNEL_ENTRANCE_H << 8) * SCALEFACTOR, 1, 1, Sprite.ANIMATION_FROZEN, Sprite.SPRITE_ALIGN_TOP | Sprite.SPRITE_ALIGN_LEFT,
        //TURN LEFT
        (TUNNEL_ENTRANCE_W << 8) * SCALEFACTOR, (TUNNEL_ENTRANCE_H << 8) * SCALEFACTOR, 0x000 * SCALEFACTOR, 0x000 * SCALEFACTOR, (TUNNEL_ENTRANCE_W << 8) * SCALEFACTOR, (TUNNEL_ENTRANCE_H << 8) * SCALEFACTOR, 1, 1, Sprite.ANIMATION_FROZEN, Sprite.SPRITE_ALIGN_TOP | Sprite.SPRITE_ALIGN_LEFT,
        //NOT USED (LEFT & RIGHT)
        (TUNNEL_ENTRANCE_W << 8) * SCALEFACTOR, (TUNNEL_ENTRANCE_H << 8) * SCALEFACTOR, 0x000 * SCALEFACTOR, 0x000 * SCALEFACTOR, (TUNNEL_ENTRANCE_W << 8) * SCALEFACTOR, (TUNNEL_ENTRANCE_H << 8) * SCALEFACTOR, 1, 1, Sprite.ANIMATION_FROZEN, Sprite.SPRITE_ALIGN_TOP | Sprite.SPRITE_ALIGN_LEFT,
        //DOWN CAR
        (TUNNEL_ENTRANCE_W << 8) * SCALEFACTOR, (TUNNEL_ENTRANCE_H << 8) * SCALEFACTOR, 0x000 * SCALEFACTOR, 0x000 * SCALEFACTOR, (TUNNEL_ENTRANCE_W << 8) * SCALEFACTOR, (TUNNEL_ENTRANCE_H << 8) * SCALEFACTOR, 1, 1, Sprite.ANIMATION_FROZEN, Sprite.SPRITE_ALIGN_TOP | Sprite.SPRITE_ALIGN_LEFT,
        //DOWN CAR + RIGHT TURN
        (TUNNEL_ENTRANCE_W << 8) * SCALEFACTOR, (TUNNEL_ENTRANCE_H << 8) * SCALEFACTOR, 0x000 * SCALEFACTOR, 0x000 * SCALEFACTOR, (TUNNEL_ENTRANCE_W << 8) * SCALEFACTOR, (TUNNEL_ENTRANCE_H << 8) * SCALEFACTOR, 1, 1, Sprite.ANIMATION_FROZEN, Sprite.SPRITE_ALIGN_TOP | Sprite.SPRITE_ALIGN_LEFT,
        //DOWN CAR + LEFT TURN
        (TUNNEL_ENTRANCE_W << 8) * SCALEFACTOR, (TUNNEL_ENTRANCE_H << 8) * SCALEFACTOR, 0x000 * SCALEFACTOR, 0x000 * SCALEFACTOR, (TUNNEL_ENTRANCE_W << 8) * SCALEFACTOR, (TUNNEL_ENTRANCE_H << 8) * SCALEFACTOR, 1, 1, Sprite.ANIMATION_FROZEN, Sprite.SPRITE_ALIGN_TOP | Sprite.SPRITE_ALIGN_LEFT,
        //NOT USED (LEFT & RIGHT)
        (TUNNEL_ENTRANCE_W << 8) * SCALEFACTOR, (TUNNEL_ENTRANCE_H << 8) * SCALEFACTOR, 0x000 * SCALEFACTOR, 0x000 * SCALEFACTOR, (TUNNEL_ENTRANCE_W << 8) * SCALEFACTOR, (TUNNEL_ENTRANCE_H << 8) * SCALEFACTOR, 1, 1, Sprite.ANIMATION_FROZEN, Sprite.SPRITE_ALIGN_TOP | Sprite.SPRITE_ALIGN_LEFT,
        //UP CAR
        (TUNNEL_ENTRANCE_W << 8) * SCALEFACTOR, (TUNNEL_ENTRANCE_H << 8) * SCALEFACTOR, 0x000 * SCALEFACTOR, 0x000 * SCALEFACTOR, (TUNNEL_ENTRANCE_W << 8) * SCALEFACTOR, (TUNNEL_ENTRANCE_H << 8) * SCALEFACTOR, 1, 1, Sprite.ANIMATION_FROZEN, Sprite.SPRITE_ALIGN_TOP | Sprite.SPRITE_ALIGN_LEFT,
        //UP CAR  + RIGHT TURN
        (TUNNEL_ENTRANCE_W << 8) * SCALEFACTOR, (TUNNEL_ENTRANCE_H << 8) * SCALEFACTOR, 0x000 * SCALEFACTOR, 0x000 * SCALEFACTOR, (TUNNEL_ENTRANCE_W << 8) * SCALEFACTOR, (TUNNEL_ENTRANCE_H << 8) * SCALEFACTOR, 1, 1, Sprite.ANIMATION_FROZEN, Sprite.SPRITE_ALIGN_TOP | Sprite.SPRITE_ALIGN_LEFT,
        //UP CAR  + LEFT TURN
        (TUNNEL_ENTRANCE_W << 8) * SCALEFACTOR, (TUNNEL_ENTRANCE_H << 8) * SCALEFACTOR, 0x000 * SCALEFACTOR, 0x000 * SCALEFACTOR, (TUNNEL_ENTRANCE_W << 8) * SCALEFACTOR, (TUNNEL_ENTRANCE_H << 8) * SCALEFACTOR, 1, 1, Sprite.ANIMATION_FROZEN, Sprite.SPRITE_ALIGN_TOP | Sprite.SPRITE_ALIGN_LEFT,
        //NOT USED (LEFT & RIGHT)
        (TUNNEL_ENTRANCE_W << 8) * SCALEFACTOR, (TUNNEL_ENTRANCE_H << 8) * SCALEFACTOR, 0x000 * SCALEFACTOR, 0x000 * SCALEFACTOR, (TUNNEL_ENTRANCE_W << 8) * SCALEFACTOR, (TUNNEL_ENTRANCE_H << 8) * SCALEFACTOR, 1, 1, Sprite.ANIMATION_FROZEN, Sprite.SPRITE_ALIGN_TOP | Sprite.SPRITE_ALIGN_LEFT,

        //TUNNEL_EXIT
        //NORMAL TUNNEL_EXIT
        (TUNNEL_EXIT_W << 8) * SCALEFACTOR, (TUNNEL_EXIT_H << 8) * SCALEFACTOR, 0x000 * SCALEFACTOR, 0x000 * SCALEFACTOR, (TUNNEL_EXIT_W << 8) * SCALEFACTOR, (TUNNEL_EXIT_H << 8) * SCALEFACTOR, 3, 1, Sprite.ANIMATION_FROZEN, Sprite.SPRITE_ALIGN_TOP | Sprite.SPRITE_ALIGN_LEFT,
        //TUNNEL_EXIT
        (TUNNEL_EXIT_W << 8) * SCALEFACTOR, (TUNNEL_EXIT_H << 8) * SCALEFACTOR, 0x000 * SCALEFACTOR, 0x000 * SCALEFACTOR, (TUNNEL_EXIT_W << 8) * SCALEFACTOR, (TUNNEL_EXIT_H << 8) * SCALEFACTOR, 1, 1, Sprite.ANIMATION_FROZEN, Sprite.SPRITE_ALIGN_TOP | Sprite.SPRITE_ALIGN_LEFT,
        //TURN LEFT
        (TUNNEL_EXIT_W << 8) * SCALEFACTOR, (TUNNEL_EXIT_H << 8) * SCALEFACTOR, 0x000 * SCALEFACTOR, 0x000 * SCALEFACTOR, (TUNNEL_EXIT_W << 8) * SCALEFACTOR, (TUNNEL_EXIT_H << 8) * SCALEFACTOR, 1, 1, Sprite.ANIMATION_FROZEN, Sprite.SPRITE_ALIGN_TOP | Sprite.SPRITE_ALIGN_LEFT,
        //NOT USED (LEFT & RIGHT)
        (TUNNEL_EXIT_W << 8) * SCALEFACTOR, (TUNNEL_EXIT_H << 8) * SCALEFACTOR, 0x000 * SCALEFACTOR, 0x000 * SCALEFACTOR, (TUNNEL_EXIT_W << 8) * SCALEFACTOR, (TUNNEL_EXIT_H << 8) * SCALEFACTOR, 1, 1, Sprite.ANIMATION_FROZEN, Sprite.SPRITE_ALIGN_TOP | Sprite.SPRITE_ALIGN_LEFT,
        //DOWN CAR
        (TUNNEL_EXIT_W << 8) * SCALEFACTOR, (TUNNEL_EXIT_H << 8) * SCALEFACTOR, 0x000 * SCALEFACTOR, 0x000 * SCALEFACTOR, (TUNNEL_EXIT_W << 8) * SCALEFACTOR, (TUNNEL_EXIT_H << 8) * SCALEFACTOR, 1, 1, Sprite.ANIMATION_FROZEN, Sprite.SPRITE_ALIGN_TOP | Sprite.SPRITE_ALIGN_LEFT,
        //DOWN CAR + RIGHT TURN
        (TUNNEL_EXIT_W << 8) * SCALEFACTOR, (TUNNEL_EXIT_H << 8) * SCALEFACTOR, 0x000 * SCALEFACTOR, 0x000 * SCALEFACTOR, (TUNNEL_EXIT_W << 8) * SCALEFACTOR, (TUNNEL_EXIT_H << 8) * SCALEFACTOR, 1, 1, Sprite.ANIMATION_FROZEN, Sprite.SPRITE_ALIGN_TOP | Sprite.SPRITE_ALIGN_LEFT,
        //DOWN CAR + LEFT TURN
        (TUNNEL_EXIT_W << 8) * SCALEFACTOR, (TUNNEL_EXIT_H << 8) * SCALEFACTOR, 0x000 * SCALEFACTOR, 0x000 * SCALEFACTOR, (TUNNEL_EXIT_W << 8) * SCALEFACTOR, (TUNNEL_EXIT_H << 8) * SCALEFACTOR, 1, 1, Sprite.ANIMATION_FROZEN, Sprite.SPRITE_ALIGN_TOP | Sprite.SPRITE_ALIGN_LEFT,
        //NOT USED (LEFT & RIGHT)
        (TUNNEL_EXIT_W << 8) * SCALEFACTOR, (TUNNEL_EXIT_H << 8) * SCALEFACTOR, 0x000 * SCALEFACTOR, 0x000 * SCALEFACTOR, (TUNNEL_EXIT_W << 8) * SCALEFACTOR, (TUNNEL_EXIT_H << 8) * SCALEFACTOR, 1, 1, Sprite.ANIMATION_FROZEN, Sprite.SPRITE_ALIGN_TOP | Sprite.SPRITE_ALIGN_LEFT,
        //UP CAR
        (TUNNEL_EXIT_W << 8) * SCALEFACTOR, (TUNNEL_EXIT_H << 8) * SCALEFACTOR, 0x000 * SCALEFACTOR, 0x000 * SCALEFACTOR, (TUNNEL_EXIT_W << 8) * SCALEFACTOR, (TUNNEL_EXIT_H << 8) * SCALEFACTOR, 1, 1, Sprite.ANIMATION_FROZEN, Sprite.SPRITE_ALIGN_TOP | Sprite.SPRITE_ALIGN_LEFT,
        //UP CAR  + RIGHT TURN
        (TUNNEL_EXIT_W << 8) * SCALEFACTOR, (TUNNEL_EXIT_H << 8) * SCALEFACTOR, 0x000 * SCALEFACTOR, 0x000 * SCALEFACTOR, (TUNNEL_EXIT_W << 8) * SCALEFACTOR, (TUNNEL_EXIT_H << 8) * SCALEFACTOR, 1, 1, Sprite.ANIMATION_FROZEN, Sprite.SPRITE_ALIGN_TOP | Sprite.SPRITE_ALIGN_LEFT,
        //UP CAR  + LEFT TURN
        (TUNNEL_EXIT_W << 8) * SCALEFACTOR, (TUNNEL_EXIT_H << 8) * SCALEFACTOR, 0x000 * SCALEFACTOR, 0x000 * SCALEFACTOR, (TUNNEL_EXIT_W << 8) * SCALEFACTOR, (TUNNEL_EXIT_H << 8) * SCALEFACTOR, 1, 1, Sprite.ANIMATION_FROZEN, Sprite.SPRITE_ALIGN_TOP | Sprite.SPRITE_ALIGN_LEFT,
        //NOT USED (LEFT & RIGHT)
        (TUNNEL_EXIT_W << 8) * SCALEFACTOR, (TUNNEL_EXIT_H << 8) * SCALEFACTOR, 0x000 * SCALEFACTOR, 0x000 * SCALEFACTOR, (TUNNEL_EXIT_W << 8) * SCALEFACTOR, (TUNNEL_EXIT_H << 8) * SCALEFACTOR, 1, 1, Sprite.ANIMATION_FROZEN, Sprite.SPRITE_ALIGN_TOP | Sprite.SPRITE_ALIGN_LEFT

    };

    public static final int MAXSPRITENUMBER = 35;
    public static final int ZERO_SPRITE_AREA = 10;

    public Sprite[] ap_SpriteArray;
    public Sprite p_PlayerSprite;
    public Sprite p_RiflemanSprite;
    public Sprite p_EnemySprite;

    public static short[] ab_Road;
    public static byte[] ab_HeightRoad;
    public static byte[] ab_RoadObjectID;
    public static short[] ab_RoadObjectX;

    public int getRoadObjectX(int index)
    {
        if ((index & 0x01) == 0)
        {
            index >>= 1;
            return ab_RoadObjectX[(index & 0xFFF)];
        }
        else
        {
            return 0x0000;
        }
    }

    public int getRoadObjectID(int index)
    {
        if ((index & 0x01) == 0)
        {
            index >>= 1;
            return ab_RoadObjectID[(index & 0xFFF)];
        }
        else
        {
            return 0x0000;
        }
    }

    public int getRoad(int index)
    {
        index >>= 1;
            i_LastRoad = ab_Road[(index & 0xFFF)];
            return i_LastRoad;
    }

    public int getHeightRoad(int index)
    {
        index >>= 1;
        i_LastRoadHeight = ab_HeightRoad[(index & 0xFFF)];
        return i_LastRoadHeight;
    }

    private void MakeTrafficObject(int objID, int Position)
    {
        int lc_type;
        int lc_speed;
        int lc_x;
        int lc_y;
        int lc_h;
        int lc_w;

        Sprite p_object = getZeroInactiveSprite();
        int objX = getRoadObjectX(Position);
        lc_speed = 0;

        if(objID ==  MAP_OBJECT_TUNNEL_EXIT && p_object == null)
        {
             p_object = ap_SpriteArray[0];
        }


            if (p_object != null)
            {
                switch (objID)
                {
                    case MAP_OBJECT_TUNNEL_ENTRANCE:
                        objX = getRoad(Position);//0
                        lc_type = OBJECT_TUNNEL_ENTRANCE;
                        lc_speed = TUNNEL_SPEED;
                        lc_w = TUNNEL_ENTRANCE_W;//i_RoadWidth;
                        lc_h = TUNNEL_ENTRANCE_H;
                        break;
                    case MAP_OBJECT_TUNNEL_EXIT:
                        objX = getRoad(Position);
                        lc_type = OBJECT_TUNNEL_EXIT;
                        lc_speed = TUNNEL_SPEED;
                        lc_w = TUNNEL_EXIT_W;
                        lc_h = TUNNEL_EXIT_H;
                        break;
                    case MAP_OBJECT_BOX:
                        lc_type = OBJECT_BOX;
                        lc_speed = BOX_SPEED;
                        lc_w = BOX_W;
                        lc_h = BOX_H;
                        break;
                    case MAP_OBJECT_TREE:
                        lc_type = OBJECT_TREE;
                        lc_speed = TREE_SPEED;
                        lc_w = TREE_W;
                        lc_h = TREE_H;
                        break;
                    case MAP_OBJECT_SIGN:
                        lc_type = OBJECT_SIGN;
                        lc_speed = SIGN_SPEED;
                        lc_w = SIGN_W;
                        lc_h = SIGN_H;
                        break;
                    case MAP_OBJECT_PUDDLE:
                        lc_type = OBJECT_PUDDLE;
                        lc_speed = PUDDLE_SPEED;
                        lc_w = PUDDLE_W;
                        lc_h = PUDDLE_H;
                        break;
                    default:
                        lc_type = OBJECT_TREE;
                        lc_speed = TREE_SPEED;
                        lc_w = TREE_W;
                        lc_h = TREE_H;
                        break;
                }

                lc_y = 0; //
                lc_x = (objX - (lc_w>>1)) << 8;

                    p_object.i_ObjectType = lc_type;
                    p_object.i_Speed = lc_speed;
                    p_object.i_HSpeed = 0;
                    p_object.i_RoadPosition = Position << 8;
                    loadStateForSprite(p_object, STATE_NORMAL);

                    // WARNING: ORIGINAL WIDTH OF TUNNEL ENTRANCE WILL BE CHANGED HERE
                    if(objID == MAP_OBJECT_TUNNEL_ENTRANCE || objID == MAP_OBJECT_TUNNEL_EXIT)
                    {
                        p_object.i_width = lc_w<<8;
                        lc_x = objX<<8;
                    }
                    p_object.setMainPointXY(lc_x, lc_y);

            }
    }

    private void initTrafficObjects()
    {
        int start = i_Bottom_Position>>8;
        int end = (i_View_Position>>8)+TOP_APPEARING_OBJECTS_BORDER;
        int pos;
        int player_speed = p_PlayerSprite.i_Speed >> 8;

        for(pos=start; pos < end ; pos++)
        {
            int objID = getRoadObjectID(pos);
            if (objID != 0)
            {
                MakeTrafficObject(objID, pos);
            }
        }
        processTraffic();
    }

    // Generate Traffic
    // This method generate new traffic object with use born_freq property
    private void generateTraffic()
    {
        int lc_type;
        int lc_speed;
        int lc_direction;
        int lc_x;
        int lc_y;
        int lc_h;
        int lc_w;
        int lc_x_road;

        int i = i_View_Position >> 8;
        int player_speed = (p_PlayerSprite.i_Speed + i_View_Position>> 8);

        for(; i < player_speed; i++)
        {
            int objID = getRoadObjectID(i);
            if (objID != 0)
            {
                MakeTrafficObject(objID, i);
            }
        }

            if (born_time + born_freq < i_Timer) //i_Player_Position)
                {
                        int rndTrafficType = getRandomInt(6) / 2;
                        Sprite p_traffic   = getFirstInactiveSprite();

                        lc_direction = SOUTH;
                        lc_x = 4 + (getRandomInt(120) << 8);

                        switch (rndTrafficType)
                            {
                                case 0:
                                case 3:
                                    lc_type = OBJECT_CAR1;
                                    lc_speed = CAR1_SPEED;
                                    lc_w = CAR1_W;
                                    lc_h = CAR1_H;
                                    lc_y = 1 - lc_h;
                                    break;
                                case 1:
                                    lc_type = OBJECT_CAR2;
                                    lc_speed = CAR2_SPEED;
                                    lc_w = CAR2_W;
                                    lc_h = CAR2_H;
                                    lc_y = 1 - lc_h;
                                    break;
                                case 2:
                                    lc_type = OBJECT_CYCLE;
                                    lc_speed = CYCLE_SPEED;
                                    lc_w = CYCLE_W;
                                    lc_h = CYCLE_H;
                                    lc_y = 1 - lc_h;
                                    break;
                                default:
                                    lc_type = OBJECT_CYCLE;
                                    lc_speed = CYCLE_SPEED;
                                    lc_w = CYCLE_W;
                                    lc_h = CYCLE_H;
                                    lc_y = 1 - lc_h;
                                    break;
                            }

                        lc_y = (0 - lc_h - 64) << 8;

                    if (p_traffic != null)
                    {
                        p_traffic.i_ObjectType = lc_type;
                        p_traffic.i_Speed = lc_speed;
                        p_traffic.i_HSpeed = 0;
                        p_traffic.i_CrashTime = 0;
                        p_traffic.i_Control = 0;
                        loadStateForSprite(p_traffic, STATE_NORMAL);
                        lc_x_road = getRandomInt(i_RoadWidth - (p_traffic.i_width >> 8));
                        p_traffic.i_RoadX = lc_x_road << 8;
                        if ((getRandomInt(4) & 0x01) == 0)
                        {
                            // The car generates on top of screen
                            p_traffic.i_RoadPosition = i_View_Position + p_traffic.i_height + ROAD_BUFFER_LENGHT + (getRandomInt(4) << 10);
                        }
                        else
                        {
                            // The car generates on bottom of screen
                            p_traffic.i_RoadPosition = i_Bottom_Position - ROAD_BUFFER_LENGHT - (getRandomInt(4) << 12);
                        }
                        p_traffic.setMainPointXY(lc_x, lc_y);
                    }

                        born_time = i_Timer;// i_Player_Position;
                }

    }

    // Process Traffic
    // This method updates status and position of all enemy objects for one game turn
    public void processTraffic()
    {
            Sprite p_sprite, p_sprite_ex;
            for (int ei = 0; ei < MAXSPRITENUMBER; ei++)
            {
                p_sprite = ap_SpriteArray[ei];
                if (p_sprite.lg_SpriteActive)
                {
                    // Movement
                    int objectX = p_sprite.i_mainX;
                    int objectY = p_sprite.i_mainY;
                    int objectW = p_sprite.i_width;
                    int objectH = p_sprite.i_height;
                    int objectNewX;

                boolean lg_animationCompleted = p_sprite.processAnimation();

                int obj_type = p_sprite.i_ObjectType;
                switch (obj_type)
                {
                case OBJECT_BULLET:
                    objectY = p_sprite.i_mainY -= p_sprite.i_Speed;
                    objectX = p_sprite.i_mainX;
                    p_sprite.setMainPointXY(objectX, objectY);

                    if (p_sprite.i_mainY < 0 - p_sprite.i_height)
                    {
                        p_sprite.lg_SpriteActive = false;
                    }

                    if (p_sprite.isCollided(p_EnemySprite))
                    {
                        p_sprite.lg_SpriteActive = false;
                        // ap_SpriteArray[ex].lg_SpriteActive = false;
                        i_EnemyForce -= 5;
                    }

                    for (int ex = 0; ex < MAXSPRITENUMBER; ex++)
                    {
                        if ((p_sprite_ex = ap_SpriteArray[ex]).lg_SpriteActive)
                        {
                            switch (p_sprite_ex.i_ObjectType)
                            {
                                case OBJECT_CAR1:
                                case OBJECT_CAR2:
                                case OBJECT_CYCLE:
                                    if (p_sprite.isCollided(p_sprite_ex))
                                    {
                                        p_sprite.lg_SpriteActive = false;
                                        ExplodeCar(p_sprite_ex);
                                        i_PlayerScore += KILLED_CAR_SCORE;
                                        i_player_collided_time = i_Timer + COLLIDED_PLAYER_DELAY;
                                        p_GameActionListener.gameAction(GAMEACTION_SND_EXPLOSION);
                                    }
                                    break;
                            }
                        }
                    }
                    break;
                case OBJECT_BOX:
                    objectX = p_sprite.i_mainX;
                    objectY = i_View_Position - p_sprite.i_RoadPosition;
                    p_sprite.setMainPointXY(objectX, objectY);

                    if (p_sprite.i_ObjectState == BOX_STATE_EXPLOSION)
                    {
                        if (lg_animationCompleted)
                        {
                            // Delete box explosion
                            p_sprite.lg_SpriteActive = false;
                        }
                    }
                    else
                    {
                        if (p_sprite.isCollided(p_PlayerSprite) && lg_player_alive)
                        {
                            i_PlayerForce -= 5;
                            i_PlayerScore += BOX_SCORE;
                            loadStateForSprite(p_sprite, BOX_STATE_EXPLOSION);
                            p_GameActionListener.gameAction(GAMEACTION_SND_EXPLOSION);

                        }
                    }
                        ///////////////////////////////////////////////////////////////////////
                        // Deactivate object that has position out of game screen + 0x8000
                        if ((p_sprite.i_RoadPosition > i_View_Position + 0x8000) | (p_sprite.i_RoadPosition < i_Bottom_Position - 0x8000))
                        {
                            p_sprite.lg_SpriteActive = false;
                        }
                        ///////////////////////////////////////////////////////////////////////
                    break;
                case OBJECT_PUDDLE:
                    objectX = p_sprite.i_mainX;
                    objectY = i_View_Position - p_sprite.i_RoadPosition;
                    p_sprite.setMainPointXY(objectX, objectY);

                    //if (p_sprite.i_ObjectState == BOX_STATE_EXPLOSION)
                    //{
                    //    if (lg_animationCompleted)
                    //    {
                    //        // Delete puddle drops
                    //        p_sprite.lg_SpriteActive = false;
                    //    }
                    //}
                    //else
                    //{

                        if (p_sprite.isCollided(p_PlayerSprite) && lg_player_alive)
                        {

                            if (p_PlayerSprite.i_Speed >= SPEED_JUMP_LIMIT)
                            {
                                p_PlayerSprite.i_Control = PUDDLE_DELAY;
                                i_PlayerForce -= 0;
                                i_PlayerScore += 0;
                                //loadStateForSprite(p_sprite, PUDDLE_STATE_DROPS);
                                p_GameActionListener.gameAction(GAMEACTION_SND_WATERSPLASH);

                                int rndval = (PLAYER_MAX_HSPEED / PLAYER_HSPEED_INCREASE) * 2;
                                int rndspeed = getRandomInt(rndval);
                                i_player_HSpeed += (0 - PLAYER_MAX_HSPEED) + (rndspeed * PLAYER_HSPEED_INCREASE);
                            }
                        }
                    //}
                        ///////////////////////////////////////////////////////////////////////
                        // Deactivate object that has position out of game screen + 0x8000
                        if ((p_sprite.i_RoadPosition > i_View_Position + 0x8000) | (p_sprite.i_RoadPosition < i_Bottom_Position - 0x8000))
                        {
                            p_sprite.lg_SpriteActive = false;
                        }
                        ///////////////////////////////////////////////////////////////////////
                    break;
                case OBJECT_TUNNEL_ENTRANCE:
                    objectX = p_sprite.i_mainX;
                    objectY = i_View_Position - p_sprite.i_RoadPosition;
                    p_sprite.setMainPointXY(objectX, objectY);

                    if (p_PlayerSprite.i_RoadPosition >= p_sprite.i_RoadPosition - p_sprite.i_height)
                    {
                        if (p_PlayerSprite.i_mainX > p_sprite.i_mainX
                        && p_PlayerSprite.i_mainX + p_PlayerSprite.i_width < p_sprite.i_mainX + p_sprite.i_width)
                        {
                          if(p_PlayerSprite.i_RoadPosition - p_PlayerSprite.i_height >= p_sprite.i_RoadPosition - p_sprite.i_height)
                          {
                            if(!lg_Tunnel)i_StageDecorationNo++;
                            lg_Tunnel = true;
                            p_PlayerSprite.i_JumpAngle = 0;

                            for (int li = 0; li < ZERO_SPRITE_AREA; li++)
                            {
                              if (ap_SpriteArray[li].lg_SpriteActive) ap_SpriteArray[li].lg_SpriteActive = false;
                            }

                            p_sprite.lg_SpriteActive = false;
                          }
                          break;
                        }
                         else
                         {
                           p_GameActionListener.gameAction(GAMEACTION_SND_COLLISION);
                           p_PlayerSprite.i_RoadPosition = p_sprite.i_RoadPosition - p_sprite.i_height-(2<<8);
                           p_PlayerSprite.i_CrashTime = CRASH_STARTENGINE_DELAY;
                           i_player_collided_time = i_Timer + 1 + (p_PlayerSprite.i_Speed >>9);

                           if (p_PlayerSprite.i_Speed >= SPEED_JUMP_LIMIT)
                           {
                             i_PlayerForce = 0;

                             p_PlayerSprite.i_Control = 0;
                             p_PlayerSprite.i_Speed = 0;
                             i_player_HSpeed = 0;
                             i_player_VSpeed = 0;
                           }
                           else
                           {
                            i_PlayerForce -= (p_PlayerSprite.i_Speed>>6) +1;

                             p_PlayerSprite.i_Control = CRASH_STOPENGINE_DELAY;
                             p_PlayerSprite.i_Speed = -p_PlayerSprite.i_Speed;
                             i_player_VSpeed = -i_player_VSpeed;
                           }
                         }
                    }
                        ///////////////////////////////////////////////////////////////////////
                        // Deactivate object that has position out of game screen + 0x8000
                        if ((p_sprite.i_RoadPosition > i_View_Position + 0x8000) | (p_sprite.i_RoadPosition < i_Bottom_Position - 0x8000))
                        {
                            p_sprite.lg_SpriteActive = false;
                        }
                        ///////////////////////////////////////////////////////////////////////
                    break;
                case OBJECT_TUNNEL_EXIT:
                    objectX = p_sprite.i_mainX;
                    objectY = i_View_Position - p_sprite.i_RoadPosition;
                    p_sprite.setMainPointXY(objectX, objectY);

                    if (p_PlayerSprite.i_RoadPosition >= p_sprite.i_RoadPosition - p_sprite.i_height)
//                    if (p_sprite.isCollided(p_PlayerSprite))
                    {
                        lg_Tunnel = false;
                    }
                        ///////////////////////////////////////////////////////////////////////
                        // Deactivate object that has position out of game screen + 0x8000
                        if ((p_sprite.i_RoadPosition > i_View_Position + 0x8000) | (p_sprite.i_RoadPosition < i_Bottom_Position - 0x8000))
                        {
                            p_sprite.lg_SpriteActive = false;
                        }
                        ///////////////////////////////////////////////////////////////////////
                    break;
                case OBJECT_EXPLOSION:
                    objectX = p_sprite.i_mainX;
                    objectY = i_View_Position - p_sprite.i_RoadPosition;
                    p_sprite.setMainPointXY(objectX, objectY);

                        if (lg_animationCompleted)
                        {
                            // Delete box explosion
                            p_sprite.lg_SpriteActive = false;
                        }

                        ///////////////////////////////////////////////////////////////////////
                        // Deactivate object that has position out of game screen + 0x8000
                        if ((p_sprite.i_RoadPosition > i_View_Position + 0x8000) | (p_sprite.i_RoadPosition < i_Bottom_Position - 0x8000))
                        {
                            p_sprite.lg_SpriteActive = false;
                        }
                        ///////////////////////////////////////////////////////////////////////
                    break;
                case OBJECT_TREE:
                case OBJECT_SIGN:
                    objectX = p_sprite.i_mainX;
                    objectY = i_View_Position - p_sprite.i_RoadPosition;
                    p_sprite.setMainPointXY(objectX, objectY);

                    if (p_sprite.isCollided(p_PlayerSprite)  && lg_player_alive)
                    {
                        p_GameActionListener.gameAction(GAMEACTION_SND_COLLISION);
                        p_PlayerSprite.i_CrashTime = CRASH_STARTENGINE_DELAY;
                        p_PlayerSprite.i_Control = CRASH_STOPENGINE_DELAY;
                        i_player_collided_time = i_Timer + 1 + (p_PlayerSprite.i_Speed >>9);

                        if (p_PlayerSprite.i_Speed > SPEED_JUMP_LIMIT)
                        {
                            p_PlayerSprite.i_RoadPosition = p_sprite.i_RoadPosition-p_sprite.i_height -1;
                            i_PlayerForce = 0;
                            i_PlayerScore += TREE_SCORE;

                            p_PlayerSprite.i_Control = 0;
                            p_PlayerSprite.i_Speed = 0;
                            i_player_HSpeed = 0;
                            i_player_VSpeed = 0;
                        }
                        else
                        {
                            i_PlayerForce -= (p_PlayerSprite.i_Speed>>7) +1;
                            i_PlayerScore += TREE_SCORE;

                            if( p_PlayerSprite.i_RoadPosition > p_sprite.i_RoadPosition-(p_sprite.i_height>>1))
                            {
                                if(p_PlayerSprite.i_mainX > p_sprite.i_mainX + (p_sprite.i_width>>1))
                                {
                                  p_PlayerSprite.i_mainX = p_sprite.i_mainX + p_sprite.i_width +0x100;
                                  i_player_HSpeed = Math.abs(i_player_HSpeed);
                                }
                                 else
                                 {
                                    p_PlayerSprite.i_mainX = p_sprite.i_mainX - p_PlayerSprite.i_width -0x100;
                                    i_player_HSpeed = -Math.abs(i_player_HSpeed);
                                 }
                            }
                             else
                              {
                                   p_PlayerSprite.i_RoadPosition = p_sprite.i_RoadPosition - p_sprite.i_height -(2<<8);
                                   p_PlayerSprite.i_Speed = -p_PlayerSprite.i_Speed;
                                   i_player_VSpeed = -i_player_VSpeed;

                                   int centerX = (getRoad(p_PlayerSprite.i_RoadPosition>>8) + (i_RoadWidth - (p_PlayerSprite.i_width>>8)>>1))<<8;
                                   i_player_HSpeed += p_PlayerSprite.i_mainX > centerX? -PLAYER_HSPEED_INCREASE : PLAYER_HSPEED_INCREASE;


                              }
                        }
                    }
                        ///////////////////////////////////////////////////////////////////////
                        // Deactivate object that has position out of game screen + 0x8000
                        if ((p_sprite.i_RoadPosition > i_View_Position + 0x8000) | (p_sprite.i_RoadPosition < i_Bottom_Position - 0x8000))
                        {
                            p_sprite.lg_SpriteActive = false;
                        }
                        ///////////////////////////////////////////////////////////////////////
                    break;
                case OBJECT_CAR1:
                case OBJECT_CAR2:
                case OBJECT_CYCLE:
                    ////////////////////////////////////////////////////////////////
                    int turn = 0;
                    p_sprite.i_RoadPosition += p_sprite.i_Speed;
                    if (p_sprite.i_Control == 0)
                    {

                        objectNewX = (getRoad((p_sprite.i_RoadPosition >> 8) + 4) << 8) + p_sprite.i_RoadX;
                        if (objectNewX > objectX)
                        {
                            //Turn Right
                            turn = TURNRIGHT;
                        }
                        if (objectNewX < objectX)
                        {
                            //Turn Left
                            turn = TURNLEFT;
                        }
                        if (objectNewX == objectX)
                        {
                            //Turn Center
                            turn = TURNCENTER;
                        }

                        Car_AI(p_sprite);

                        if ((p_sprite.i_RoadPosition > p_PlayerSprite.i_RoadPosition - p_PlayerSprite.i_height * 3) && (p_sprite.i_RoadPosition - objectH * 3  < p_PlayerSprite.i_RoadPosition))
                        {
                            if ((objectX > p_PlayerSprite.i_mainX + p_PlayerSprite.i_width + SAFE_ZONE) | (objectX + objectW + SAFE_ZONE < p_PlayerSprite.i_mainX))
                            {
                                // Return to road
                                //
                                if ((i_Timer & 0x0F) == 0)
                                {
                                if (p_sprite.i_RoadX < 0)
                                {
                                    p_sprite.i_RoadX += 0x0280;
                                    turn = TURNRIGHT;

                                }
                                if (p_sprite.i_RoadX > ((i_RoadWidth << 8) - p_sprite.i_width))
                                {
                                    p_sprite.i_RoadX -= 0x0280;
                                    turn = TURNLEFT;
                                }
                                }
                                //
                            }
                            else
                            {
                                if((objectX < p_PlayerSprite.i_mainX) && (objectX + objectW + SAFE_ZONE > p_PlayerSprite.i_mainX))
                                {
                                    if (p_sprite.i_RoadX > 0 - SAFE_ZONE)
                                    {
                                        p_sprite.i_RoadX -= 0x0280;
                                        turn = TURNLEFT;
                                    }
                                }

                                if((objectX  > p_PlayerSprite.i_mainX) && (objectX < p_PlayerSprite.i_mainX + p_PlayerSprite.i_width + SAFE_ZONE))
                                {
                                    if (p_sprite.i_RoadX < (i_RoadWidth << 8) - objectW + SAFE_ZONE)
                                    {
                                        p_sprite.i_RoadX += 0x0280;
                                        turn = TURNRIGHT;
                                    }
                                }
                            }
                        }

                    }
                    else
                    {
                        p_sprite.i_Control--;
                    }
                    objectX = (getRoad((p_sprite.i_RoadPosition >> 8)) << 8) + p_sprite.i_RoadX;
                    objectY = i_View_Position - p_sprite.i_RoadPosition;

                    p_sprite.setMainPointXY(objectX, objectY);

                    CarStateControl(p_sprite, turn);

                        ////////////////////////////////////////////////////////////////
                        // Collision
                        if (p_sprite.isCollided(p_PlayerSprite) && lg_player_alive)
                        {
                            p_PlayerSprite.i_CrashTime = CRASH_STARTENGINE_DELAY;
                            p_sprite.i_CrashTime = CRASH_STARTENGINE_DELAY;
                            i_PlayerForce -= 20;
                            i_PlayerScore += KILLED_CAR_SCORE;
                            i_player_collided_time = i_Timer + COLLIDED_PLAYER_DELAY;
                            //loadStateForSprite(p_sprite, STATE_HIT);
                            p_sprite.i_Control = CRASH_STOPENGINE_DELAY;
                            p_PlayerSprite.i_Control = CRASH_STOPENGINE_DELAY;

                            HitPlayerCar(p_sprite);
                            ExplodeCar(p_sprite);
                            p_GameActionListener.gameAction(GAMEACTION_SND_EXPLOSION);
                        }

                        if (p_sprite.isCollided(p_EnemySprite))
                        {
                            p_EnemySprite.i_CrashTime = CRASH_ENEMY_STARTENGINE_DELAY;
                            p_sprite.i_CrashTime = CRASH_STARTENGINE_DELAY;
                            i_EnemyForce -= 0;

                            ExplodeCar(p_sprite);
                            p_GameActionListener.gameAction(GAMEACTION_SND_EXPLOSION);
                        }

                        for (int ex = 0; ex < MAXSPRITENUMBER; ex++)
                        {
                            if ((p_sprite_ex = ap_SpriteArray[ex]).lg_SpriteActive && (p_sprite != p_sprite_ex))
                            {
                                switch (p_sprite_ex.i_ObjectType)
                                {
                                    case OBJECT_CAR1:
                                    case OBJECT_CAR2:
                                    case OBJECT_CYCLE:
                                    case OBJECT_TREE:
                                    case OBJECT_SIGN:
                                        if (p_sprite.isCollided(p_sprite_ex))
                                        {
                                            ExplodeCar(p_sprite);
                                            p_GameActionListener.gameAction(GAMEACTION_SND_EXPLOSION);
                                        }
                                    break;
                                }
                            }
                        }

                        int leftX = getRoad(p_sprite.i_RoadPosition >> 8) << 8;
                        int rightX = leftX + (i_RoadWidth << 8);

                        if (lg_Tunnel)
                        {
                            if ((p_sprite.i_mainX < leftX) || (p_sprite.i_mainX + p_sprite.i_width > rightX))
                            {
                                ExplodeCar(p_sprite);
                            }
                        }
                        ////////////////////////////////////////////////////////////////

                        ////////////////////////////////////////////////////////////////
                        // Speed up
                                if (--p_sprite.i_CrashTime <= 0)
                                {
                                    switch(p_sprite.i_ObjectType)
                                    {
                                        case OBJECT_CAR1:
                                            if (p_sprite.i_Speed < CAR1_SPEED)
                                            {
                                                p_sprite.i_Speed += PLAYER_VSPEED_INCREASE;
                                            }
                                            else
                                            {
                                                p_sprite.i_CrashTime = 0;
                                            }
                                            break;
                                        case OBJECT_CAR2:
                                            if (p_sprite.i_Speed < CAR2_SPEED)
                                            {
                                                p_sprite.i_Speed += PLAYER_VSPEED_INCREASE;
                                            }
                                            else
                                            {
                                                p_sprite.i_CrashTime = 0;
                                            }
                                            break;
                                        case OBJECT_CYCLE:
                                            if (p_sprite.i_Speed < CYCLE_SPEED)
                                            {
                                                p_sprite.i_Speed += PLAYER_VSPEED_INCREASE;
                                            }
                                            else
                                            {
                                                p_sprite.i_CrashTime = 0;
                                            }
                                            break;
                                    }
                                }
                        ////////////////////////////////////////////////////////////////

                ///////////////////////////////////////////////////////////////////////
                // Deactivate object that has position out of game screen + 0x8000
                if ((p_sprite.i_RoadPosition > i_View_Position + 0x8000) | (p_sprite.i_RoadPosition < i_Bottom_Position - 0x8000))
                {
                    p_sprite.lg_SpriteActive = false;
                }
                ///////////////////////////////////////////////////////////////////////
                default:
                    break;
                } // end main switch
                }
            }
    }

    private void processEnemy()
    {
        //
        int turn = 0;
        int objectX = p_EnemySprite.i_mainX;
        int objectY;
        int objectW = p_EnemySprite.i_width;
        int objectH = p_EnemySprite.i_height;
        int objectNewX;
        p_EnemySprite.i_RoadPosition += p_EnemySprite.i_Speed;
        if (p_EnemySprite.i_Control == 0)
        {

            objectNewX = (getRoad((p_EnemySprite.i_RoadPosition >> 8) + 4) << 8) + p_EnemySprite.i_RoadX;
            if (objectNewX > objectX)
            {
                //Turn Right
                turn = TURNRIGHT;
            }
            if (objectNewX < objectX)
            {
                //Turn Left
                turn = TURNLEFT;
            }
            if (objectNewX == objectX)
            {
                //Turn Center
                turn = TURNCENTER;
            }

            EnemyCar_AI(p_EnemySprite);

            objectX = (getRoad((p_EnemySprite.i_RoadPosition >> 8)) << 8) + p_EnemySprite.i_RoadX;

            if((p_EnemySprite.i_RoadPosition < i_View_Position + p_EnemySprite.i_height) && (p_EnemySprite.i_RoadPosition > i_Bottom_Position) )
            {
                //Enemy car enter to the screen
            ///////////////////////////////////////////////////////////////////////////////////////////
                if (p_EnemySprite.isCollided(p_PlayerSprite)  && lg_player_alive)
                {
                    HitPlayerCar(p_EnemySprite);
                    i_PlayerForce -= 20;
                    i_PlayerScore += ENEMY_COLLIDE_SCORE;
                    i_player_collided_time = i_Timer + COLLIDED_PLAYER_DELAY;
                    // p_EnemySprite.i_Speed = ENEMY_CAR_SPEED;
                    strategy = STRATEGY_SPEED_UP;
                }
            //-----------------------------------------------------------------------------------------
                //int strategy = STRATEGY_DEFENCE;
                //strategy = STRATEGY_DEFENCE;

                ///////////////////////////////////////////////////////////////////////////////
                // Select Strategy
                if (strategy != STRATEGY_SPEED_UP)
                {
                if ((fire_time + PLAYER_FIRE_FREQ * 1000 > i_Timer)) //Player has fired
                {
                    strategy = STRATEGY_DEFENCE;
                }
                else
                {
                    strategy = STRATEGY_ATTACK;
                }

                if ((objectX + objectW + SAFE_ZONE < p_PlayerSprite.i_mainX) || (objectX > p_PlayerSprite.i_mainX + p_PlayerSprite.i_width + SAFE_ZONE) && (strategy==STRATEGY_DEFENCE) && (p_PlayerSprite.i_Speed < p_EnemySprite.i_Speed))
                {
                    strategy = STRATEGY_BREAK_SPEED;
                }

                if ( (p_EnemySprite.i_RoadPosition > p_PlayerSprite.i_RoadPosition) && (p_PlayerSprite.i_RoadPosition > (p_EnemySprite.i_RoadPosition - p_EnemySprite.i_height / 4 * 3)))
                {
                    strategy = STRATEGY_BOARD_STRIKE;
                }

                if (p_EnemySprite.i_CrashTime != 0)
                {
                   if (--p_EnemySprite.i_CrashTime <= 0)
                   {
                        p_EnemySprite.i_CrashTime = 0;
                        strategy = STRATEGY_SPEED_UP;
                   }
                }
                }
                ///////////////////////////////////////////////////////////////////////////////

                switch(strategy)
                {
                    case STRATEGY_ATTACK:
                        if((objectX < p_PlayerSprite.i_mainX) && (objectX + objectW < p_PlayerSprite.i_mainX + p_PlayerSprite.i_width))
                        {
                            if (p_EnemySprite.i_RoadX < (i_RoadWidth << 8) - objectW)
                            {
                                p_EnemySprite.i_RoadX += 0x0180;
                                turn = TURNRIGHT;
                            }
                        }

                        if((objectX  > p_PlayerSprite.i_mainX) && (objectX + objectW > p_PlayerSprite.i_mainX + p_PlayerSprite.i_width / 2))
                        {
                            if (p_EnemySprite.i_RoadX > 0)
                            {
                                p_EnemySprite.i_RoadX -= 0x0180;
                                turn = TURNLEFT;
                            }
                        }
                        break;
                    case STRATEGY_DEFENCE:
                        //if((objectX < p_PlayerSprite.i_mainX) && (objectX + objectW + SAFE_ZONE < p_PlayerSprite.i_mainX + p_PlayerSprite.i_width))
                        if((objectX < p_PlayerSprite.i_mainX) && (objectX + objectW + SAFE_ZONE > p_PlayerSprite.i_mainX))
                        {
                            if (objectX - p_EnemySprite.i_RoadX + objectW < p_PlayerSprite.i_mainX)
                            {
                                if (p_EnemySprite.i_RoadX > 0)
                                {
                                    p_EnemySprite.i_RoadX -= 0x0180;
                                    turn = TURNLEFT;
                                }
                            }
                            else
                            {
                                if (p_EnemySprite.i_RoadX < (i_RoadWidth << 8) - objectW)
                                {
                                    p_EnemySprite.i_RoadX += 0x0180;
                                    turn = TURNRIGHT;
                                }
                            }
                        }

                        if((objectX  > p_PlayerSprite.i_mainX) && (objectX < p_PlayerSprite.i_mainX + p_PlayerSprite.i_width + SAFE_ZONE))
                        {
                            if (objectX + ((i_RoadWidth << 8) - p_EnemySprite.i_RoadX) - objectW > p_PlayerSprite.i_mainX + p_PlayerSprite.i_width + SAFE_ZONE)
                            {
                                if (p_EnemySprite.i_RoadX < (i_RoadWidth << 8) - objectW)
                                {
                                    p_EnemySprite.i_RoadX += 0x0180;
                                    turn = TURNRIGHT;
                                }
                            }
                            else
                            {
                                if (p_EnemySprite.i_RoadX > 0)
                                {
                                    p_EnemySprite.i_RoadX -= 0x0180;
                                    turn = TURNLEFT;
                                }
                            }
                        }
                        break;
                    case STRATEGY_RANDOM_SPEED:
                        /*
                        int rndValue = (0 - 8 + getRandomInt(16) << 5);
                        if ((rndValue + p_EnemySprite.i_RoadPosition & 0x0020) == 0)
                        {
                            if (rndValue > 0)
                            {
                                p_EnemySprite.i_Speed += rndValue;
                                if (p_EnemySprite.i_Speed > ENEMY_CAR_SPEED) p_EnemySprite.i_Speed = ENEMY_CAR_SPEED;
                            }
                            else
                            {
                                p_EnemySprite.i_Speed += rndValue;
                                if (p_EnemySprite.i_Speed < (p_PlayerSprite.i_Speed / 4)) p_EnemySprite.i_Speed = ENEMY_CAR_SPEED;
                            }
                        }
                        */
                        break;
                    case STRATEGY_BOARD_STRIKE:
                        if((objectX < p_PlayerSprite.i_mainX) && (objectX + objectW < p_PlayerSprite.i_mainX + p_PlayerSprite.i_width))
                        {
                            if (p_EnemySprite.i_RoadX < (i_RoadWidth << 8) - objectW)
                            {
                                p_EnemySprite.i_RoadX += 0x0600;
                                turn = TURNRIGHT;
                            }
                        }

                        if((objectX  > p_PlayerSprite.i_mainX) && (objectX + objectW > p_PlayerSprite.i_mainX + p_PlayerSprite.i_width / 2))
                        {
                            if (p_EnemySprite.i_RoadX > 0)
                            {
                                p_EnemySprite.i_RoadX -= 0x0600;
                                turn = TURNLEFT;
                            }
                        }
                        break;
                    case STRATEGY_BREAK_SPEED:
                        boolean break_speed = ((getRandomInt(16) & 0x02) == 0);
                        if (break_speed)
                        {
                            if (p_EnemySprite.i_Speed > ENEMY_CAR_SPEED / 2)
                            {
                                p_EnemySprite.i_Speed -= VSPEED_DECREASE;
                                if(p_EnemySprite.i_Speed < ENEMY_CAR_SPEED / 2) p_EnemySprite.i_Speed = ENEMY_CAR_SPEED / 2;
                            }
                        }
                        break;
                    case STRATEGY_SPEED_UP:
                        // Increase speed up to FAST
                        if ((p_EnemySprite.i_Speed < ENEMY_CAR_FAST_SPEED))
                        {
                            p_EnemySprite.i_Speed += VSPEED_INCREASE;
                            if(p_EnemySprite.i_Speed > ENEMY_CAR_FAST_SPEED) p_EnemySprite.i_Speed = ENEMY_CAR_FAST_SPEED;
                        }
                        else
                        {
                        strategy = STRATEGY_DEFENCE;
                        }
                        break;
                    case STRATEGY_7:
                        break;
                    case STRATEGY_8:
                        break;
                    default:
                        break;
                }
                // Align car
                if (p_EnemySprite.i_RoadX < 0)
                {
                    p_EnemySprite.i_RoadX += 0x0280;
                    turn = TURNRIGHT;
                }
                if (p_EnemySprite.i_RoadX > ((i_RoadWidth << 8) - p_EnemySprite.i_width))
                {
                    p_EnemySprite.i_RoadX -= 0x0280;
                    turn = TURNLEFT;
                }

                // Increase speed up to FAST
                if ((p_EnemySprite.i_Speed < ENEMY_CAR_FAST_SPEED) && strategy != STRATEGY_BREAK_SPEED)
                {
                    p_EnemySprite.i_Speed += VSPEED_INCREASE;
                    if(p_EnemySprite.i_Speed > ENEMY_CAR_FAST_SPEED) p_EnemySprite.i_Speed = ENEMY_CAR_FAST_SPEED;
                }

            ///////////////////////////////////////////////////////////////////////////////////////////
            }
            else
            {
                //Enemy car leave from the screen

                // Decrease speed down to NORMAL
                if (p_EnemySprite.i_Speed > ENEMY_CAR_SPEED)
                {
                    p_EnemySprite.i_Speed -= VSPEED_DECREASE;
                    if(p_EnemySprite.i_Speed < ENEMY_CAR_SPEED) p_EnemySprite.i_Speed = ENEMY_CAR_SPEED;
                }
                else if (p_EnemySprite.i_Speed < ENEMY_CAR_SPEED)
                {
                    p_EnemySprite.i_Speed += VSPEED_INCREASE;
                    if(p_EnemySprite.i_Speed > ENEMY_CAR_SPEED) p_EnemySprite.i_Speed = ENEMY_CAR_SPEED;
                }
            }

        }
        else
        {
            p_EnemySprite.i_Control--;
        }
        objectY = i_View_Position - p_EnemySprite.i_RoadPosition;

        p_EnemySprite.setMainPointXY(objectX, objectY);
    ///////////////////////////////////////////////////////////////////////
        CarStateControl(p_EnemySprite, turn);

        boolean lg_animationCompleted = p_EnemySprite.processAnimation();
    }

    private void ExplodeCar(Sprite _object)
    {
            int oldX = _object.i_mainX + _object.i_width / 2;
            int oldY = _object.i_mainY + _object.i_height / 2;

            _object.i_ObjectType = OBJECT_EXPLOSION;
            _object.i_Speed = EXPLOSION_SPEED;
            _object.i_HSpeed = 0;
            loadStateForSprite(_object, STATE_NORMAL);
            _object.setMainPointXY( oldX - (_object.i_width / 2), oldY - (_object.i_height / 2) );
    }

    private void HitPlayerCar(Sprite _object)
    {
        // front
        int fPlayer =  p_PlayerSprite.i_RoadPosition >> 8;
        // back
        int bPlayer =  p_PlayerSprite.i_RoadPosition - p_PlayerSprite.i_height >> 8;
        // left
        int lPlayer =  p_PlayerSprite.i_mainX >> 8;
        // back
        int rPlayer =  p_PlayerSprite.i_mainX + p_PlayerSprite.i_width >> 8;

        // front
        int fEnemy = _object.i_RoadPosition >> 8;
        // back
        int bEnemy = _object.i_RoadPosition - _object.i_height >> 8;
        // left
        int lEnemy = _object.i_mainX >> 8;
        // right
        int rEnemy = _object.i_mainX + _object.i_width >> 8;

        int deltaA;
        /*
        8
        7  +v--v+ - - - - - -
        6  |____|             = DeltaA
        5  |___+v--v+ - - - -
        4  |   |____|       = DeltaB
        3  +---|____| - - -
        2      |    |
        1      +----+
        0123456789ABCDEF
        */
        int deltaB;

        if (fEnemy < fPlayer)
        {
            deltaA = fPlayer - fEnemy;
            deltaB = fEnemy - bPlayer;

            //back strike
            _object.i_RoadPosition -= 0x0400 - _object.i_Speed / 2;
            _object.i_Speed = 0;
            _object.i_HSpeed = 0;
            //
            p_PlayerSprite.i_RoadPosition += 0x0400 + _object.i_Speed / 2;
            p_PlayerSprite.i_Speed = p_PlayerSprite.i_Speed / 4;
        }
        else
        {
            deltaA = fEnemy - fPlayer;
            deltaB = fPlayer - bEnemy;
            //front strike
            _object.i_RoadPosition += 0x0400 + p_PlayerSprite.i_Speed / 2;
            //_object.i_Speed = 0;
            //
            p_PlayerSprite.i_RoadPosition -= 0x0400 - p_PlayerSprite.i_Speed / 2;
            p_PlayerSprite.i_Speed = p_PlayerSprite.i_Speed / 4;
        }

        if (deltaA < deltaB)
        {
            if (lEnemy < lPlayer)
            {
                //left strike
                _object.i_RoadX -= 0x0600;
                _object.i_Speed -= _object.i_Speed / 4;
                _object.i_HSpeed = 0;
                //
                int tX = p_PlayerSprite.i_mainX;
                int tY = p_PlayerSprite.i_mainY;
                tX += 0x0600;
                p_PlayerSprite.setMainPointXY(tX, tY);
                p_PlayerSprite.i_Speed -= p_PlayerSprite.i_Speed / 4;
            }
            else
            {
                //right strike
                _object.i_RoadX += 0x0600;
                _object.i_Speed -= _object.i_Speed / 4;
                _object.i_HSpeed = 0;
                //
                int tX = p_PlayerSprite.i_mainX;
                int tY = p_PlayerSprite.i_mainY;
                tX -= 0x0600;
                p_PlayerSprite.setMainPointXY(tX, tY);
                p_PlayerSprite.i_Speed -= p_PlayerSprite.i_Speed / 4;
            }
        }
        else
        {
            p_PlayerSprite.setMainPointXY(p_PlayerSprite.i_mainX, p_PlayerSprite.i_mainY);
        }
        p_PlayerSprite.i_Control = CRASH_STOPENGINE_DELAY;
        i_player_HSpeed = 0;
        i_player_VSpeed = p_PlayerSprite.i_Speed;

        p_PlayerSprite.i_CrashTime = CRASH_STARTENGINE_DELAY;
        _object.i_CrashTime = CRASH_STARTENGINE_DELAY;

    }

    private void PlayerFire()
    {

      if (p_RiflemanSprite.lg_SpriteActive)
      {

          if(p_RiflemanSprite.i_RoadX == 0)
          {
             if(fire_time + PLAYER_FIRE_FREQ <= i_Timer)
             {
                Sprite p_weapon   = getFirstInactiveSprite();

                if (p_weapon != null)
                {
                    p_weapon.i_ObjectType = OBJECT_BULLET;
                    p_weapon.i_Speed = BULLET_SPEED;
                    p_weapon.i_HSpeed = i_player_HSpeed;
                    loadStateForSprite(p_weapon, STATE_NORMAL);
                    int lc_x = p_PlayerSprite.i_mainX + p_PlayerSprite.i_width
                               + p_RiflemanSprite.i_width - (p_weapon.i_width>>1);
                    int lc_y = p_RiflemanSprite.i_mainY-(p_weapon.i_height)-(2<<8);
                    p_weapon.setMainPointXY(lc_x, lc_y);
                    p_GameActionListener.gameAction(GAMEACTION_SND_FIRE);
                    //p_weapon.i_RoadPosition = i_player_HSpeed;
                }
                fire_time = i_Timer;
             }
          }
          else
          {
            p_RiflemanSprite.i_Control = 0;
            fire_time = i_Timer;
          }
      }
      else
      {
          p_RiflemanSprite.i_ObjectType = OBJECT_RIFLEMAN;
          p_RiflemanSprite.i_Speed = 0;
          p_RiflemanSprite.i_HSpeed = 0;
          loadStateForSprite(p_RiflemanSprite, STATE_NORMAL);
          p_RiflemanSprite.i_RoadX = p_RiflemanSprite.i_width;
          p_RiflemanSprite.i_Control = 0;
          p_RiflemanSprite.setMainPointXY(p_PlayerSprite.i_mainX + p_PlayerSprite.i_width - p_RiflemanSprite.i_width, p_PlayerSprite.i_mainY);
          p_RiflemanSprite.lg_SpriteActive = true;
          fire_time = i_Timer;
//          fire_time = i_Timer - PLAYER_FIRE_FREQ + p_RiflemanSprite.i_width / RIFLEMAN_OUTCOMING_SPEED - 1;
      }
    }


public GameletImpl(int _screenWidth, int _screenHeight, startup _gameActionListener, String _staticArrayResource)
    {
        super(_screenWidth, _screenHeight, _gameActionListener, _staticArrayResource);
        if(ab_Road==null)
        {
          Runtime.getRuntime().gc();
          try
          {
             DataInputStream ds = new DataInputStream(getClass().getResourceAsStream("res/data.bin"));
             ds.readUnsignedByte();
             int len = ds.readShort();
             ab_Road = new short[len];
             ab_HeightRoad = new byte[len];
             ab_RoadObjectID = new byte[len];
             ab_RoadObjectX = new short[len];
             ds.read(ab_HeightRoad);
             ds.readShort();
             for(int i=0;i<ab_Road.length;i++)
             {
                ab_Road[i] = (short) (ds.readUnsignedByte());
             }
             ds.readShort();
             ds.read(ab_RoadObjectID);

             ds.readShort();
             for(int i=0;i<ab_RoadObjectX.length;i++)
             {
               ab_RoadObjectX[i] |= ds.readUnsignedByte();
             }
          } catch(Exception e) {
          // #if DEBUG
            e.printStackTrace();
            System.out.println("Name of requested resource: /res/data.bin");
          // #endif
          }
        }

    }

    public void resumeGameAfterPlayerLost()
    {
        i_PlayerKey = 0;
        lg_Tunnel = false;
        lg_player_alive = true;
        i_player_collided_time = 0;
        i_immortality_time = IMMORTALITY_DELAY;
        i_PlayerForce = INITPLAYERFORCE;
        i_PlayerState = PLAYERSTATE_NORMAL;

        // Initing of the player
        p_PlayerSprite.lg_SpriteActive = true;
        p_PlayerSprite.i_ObjectType = OBJECT_PLAYER;
        p_PlayerSprite.i_initMainY = INIT_PLAYER_Y;
        p_PlayerSprite.i_initMainX = (getRoad(p_PlayerSprite.i_RoadPosition>>8) + (i_RoadWidth - (p_PlayerSprite.i_width>>8)>>1))<<8;
        p_PlayerSprite.setMainPointXY(p_PlayerSprite.i_initMainX, p_PlayerSprite.i_initMainY);
        loadStateForSprite(p_PlayerSprite, STATE_NORMAL);

        p_RiflemanSprite.i_ObjectType = OBJECT_RIFLEMAN;
        loadStateForSprite(p_RiflemanSprite, STATE_NORMAL);
        p_RiflemanSprite.lg_SpriteActive = false;

        // initPlayRoom();
        i_PlayerKey = PLAYER_KEY_NONE;
        p_PlayerSprite.i_Control = 0;
        p_PlayerSprite.i_CrashTime = 0;
        p_PlayerSprite.i_Speed = 0;
        i_player_HSpeed = 0;
        i_player_VSpeed = 0;
        i_player_Slowdown = false;

        i_View_Position = p_PlayerSprite.i_RoadPosition + INIT_PLAYER_Y;
        i_Bottom_Position = i_View_Position - INIT_ACTIVE_ROAD_RANGE;

        fire_time = 0;
        born_time = 0;
        strategy = 0;
        p_GameActionListener.gameAction(GAMEACTION_SND_SIRENA);
    }
    public void newGameSession(int _level)
    {
        initLevel(_level);
        int i_init_enemy_car_position = LEVEL_HARD_ENEMY_CAR_POSITION;

        switch (_level)
        {
            case LEVEL_EASY:
                {
                    i_GameTimeDelay = LEVEL_EASY_TIMEDELAY;
                    i_Attemptions = LEVEL_EASY_ATTEMPTIONS;
                    born_freq = LEVEL_EASY_CAR_FREQ;
                    i_init_enemy_car_position = LEVEL_EASY_ENEMY_CAR_POSITION;
                    i_ScoreMultiplicator = LEVEL_EASY_ROAD_SCORE;

                }
                ;
                break;
            case LEVEL_NORMAL:
                {
                    i_GameTimeDelay = LEVEL_NORMAL_TIMEDELAY;
                    i_Attemptions = LEVEL_NORMAL_ATTEMPTIONS;
                    born_freq = LEVEL_NORMAL_CAR_FREQ;
                    i_init_enemy_car_position = LEVEL_NORMAL_ENEMY_CAR_POSITION;
                    i_ScoreMultiplicator = LEVEL_NORMAL_ROAD_SCORE;
                }
                ;
                break;
            case LEVEL_HARD:
                {
                    i_GameTimeDelay = LEVEL_HARD_TIMEDELAY;
                    i_Attemptions = LEVEL_HARD_ATTEMPTIONS;
                    born_freq = LEVEL_HARD_CAR_FREQ;
                    i_init_enemy_car_position = LEVEL_HARD_ENEMY_CAR_POSITION;
                    i_ScoreMultiplicator = LEVEL_HARD_ROAD_SCORE;
                }
                ;
                break;
        }
        ap_SpriteArray = new Sprite[MAXSPRITENUMBER];
        for (int li = 0; li < MAXSPRITENUMBER; li++) ap_SpriteArray[li] = new Sprite(li);
        p_PlayerSprite = new Sprite(-1);
        p_RiflemanSprite = new Sprite(-1);
        p_EnemySprite = new Sprite(-1);

        MinPlayerArea_X = (0 + PLAYER_AREA_BORDER) << 8;
        MaxPlayerArea_X = (/*i_ScreenWidth*/1024 - PLAYER_AREA_BORDER - PLAYER_WIDTH) << 8;
        MinPlayerArea_Y = (0 + PLAYER_AREA_BORDER) << 8;
        MaxPlayerArea_Y = (i_ScreenWidth - PLAYER_AREA_BORDER - PLAYER_HEIGHT) << 8;

        i_Timer = 0; // Reset game timer

        i_StageDecorationNo = 0;

        i_EnemyForce = INIT_ENEMY_FORCE;
        p_PlayerSprite.i_RoadPosition = 0 + (INIT_PLAYER_CAR_POSITION << 8);
        i_PlayerForce = INITPLAYERFORCE;
        p_PlayerSprite.i_Control = 0;

        //
        //the enemy
        p_EnemySprite.i_ObjectType = OBJECT_ENEMY_CAR;
        p_EnemySprite.i_Speed = ENEMY_CAR_SPEED;
        p_EnemySprite.i_HSpeed = 0;
        p_EnemySprite.i_initMainX = getRoad(INIT_PLAYER_Y>>8) + i_RoadWidth/2;//INIT_PLAYER_X;
        p_EnemySprite.i_initMainY = INIT_PLAYER_Y - (i_ScreenHeight * 2 << 8);
        p_EnemySprite.setMainPointXY(p_EnemySprite.i_initMainX, p_PlayerSprite.i_initMainY);
        loadStateForSprite(p_EnemySprite, STATE_NORMAL);
        p_EnemySprite.i_RoadX = getRandomInt(i_RoadWidth - (p_EnemySprite.i_width >> 8));
        p_EnemySprite.i_RoadPosition = i_init_enemy_car_position << 8;
        //
        resumeGameAfterPlayerLost();
        initTrafficObjects();
        i_immortality_time = 0;
    }

    public void endGameSession()
    {
        //for (int li = 0; li < MAXSPRITENUMBER; li++) ap_SpriteArray[li] = null;
        ap_SpriteArray=null;
        p_PlayerSprite = null;
        p_RiflemanSprite = null;
        p_EnemySprite = null;

        Runtime.getRuntime().gc();
    }

    private Sprite getLastInactiveSprite()
    {
        for (int li = MAXSPRITENUMBER; li >= 0 ; li--)
        {
            if (!ap_SpriteArray[li].lg_SpriteActive) return ap_SpriteArray[li];
        }
        return null;
    }

    private Sprite getZeroInactiveSprite()
    {
        for (int li = 0; li < ZERO_SPRITE_AREA; li++)
        {
            if (!ap_SpriteArray[li].lg_SpriteActive) return ap_SpriteArray[li];
        }
        return null;
    }

    private Sprite getFirstInactiveSprite()
    {
        for (int li = ZERO_SPRITE_AREA; li < MAXSPRITENUMBER; li++)
        {
            if (!ap_SpriteArray[li].lg_SpriteActive) return ap_SpriteArray[li];
        }
        return null;
    }


    private void deactivateAllSprite()
    {
        for (int li = 0; li < MAXSPRITENUMBER; li++)
        {
            ap_SpriteArray[li].lg_SpriteActive = false;
        }
    }

    // Return true if the player is dead
    private boolean processPlayer()
    {

        if(i_immortality_time > 0)
        {
          i_PlayerForce = INITPLAYERFORCE;
          i_immortality_time--;
        }
         else
        if (i_PlayerForce <= 0)
        {
            if(lg_player_alive)
            {
              lg_player_alive = false;
              loadStateForSprite(p_PlayerSprite, STATE_NORMAL);
              i_PlayerForce = 0;
              p_PlayerSprite.i_Control = 0;
              i_player_HSpeed = 0;
              p_PlayerSprite.i_CrashTime = DEATH_STAGE_DELAY;
            }
            return true;
        }

        GameControl();

        if (p_PlayerSprite.i_Speed <0 )
        {
          p_PlayerSprite.i_Speed -= Math.max(p_PlayerSprite.i_Speed,-PLAYER_VSPEED_DECREASE>>1);
          i_player_VSpeed = p_PlayerSprite.i_Speed;
        }


        int leftX = getRoad(p_PlayerSprite.i_RoadPosition >> 8) << 8;
        int rightX = leftX + (i_RoadWidth << 8);

        if (lg_Tunnel)
        {
            if ((p_PlayerSprite.i_mainX < leftX) || (p_PlayerSprite.i_mainX + p_PlayerSprite.i_width > rightX))
            {
                i_PlayerForce = 0;
            }
        }
        else
        {
            if ((p_PlayerSprite.i_mainX < leftX) || (p_PlayerSprite.i_mainX + p_PlayerSprite.i_width > rightX))
            {
                if (p_PlayerSprite.i_Speed > PLAYER_MAX_VSPEED / 8)
                {
                    p_PlayerSprite.i_Speed -= PLAYER_VSPEED_DECREASE;
                    if (p_PlayerSprite.i_Speed < PLAYER_MAX_VSPEED / 8) p_PlayerSprite.i_Speed = PLAYER_MAX_VSPEED / 8;
                    i_player_VSpeed = p_PlayerSprite.i_Speed;
                }
                if((i_Timer&3)==0 && lg_player_alive && i_PlayerForce>0)
                {
                  // every 4 ticks the vibra will shake phone
                  p_GameActionListener.gameAction(GAMEACTION_SND_OUTOFROAD);
                }
            }
        }

        if(p_PlayerSprite.i_CrashTime != 0 && p_PlayerSprite.i_Control == 0)
        {
            p_PlayerSprite.i_CrashTime = 0;
        }

        p_PlayerSprite.i_Speed = i_player_VSpeed;


        if (p_RiflemanSprite.lg_SpriteActive)
        {

            if ((fire_time + RIFLEMAN_TIMEOUT < i_Timer) && (p_RiflemanSprite.i_RoadX == 0))
            {
                p_RiflemanSprite.i_RoadX = 1;
            }

            p_RiflemanSprite.setMainPointXY(
                   p_PlayerSprite.i_mainX + p_PlayerSprite.i_width - p_RiflemanSprite.i_RoadX, // x
                   p_PlayerSprite.i_mainY + RIFLEMAN_Y_OFFSET                                  // y
            );


            Sprite p_sprite_ex;
            for (int ex = 0; ex < MAXSPRITENUMBER; ex++)
            {
                if ((p_sprite_ex = ap_SpriteArray[ex]).lg_SpriteActive)
                {
                    switch (p_sprite_ex.i_ObjectType)
                    {
                        case OBJECT_CAR1:
                        case OBJECT_CAR2:
                        case OBJECT_CYCLE:
                        case OBJECT_TREE:
                        case OBJECT_SIGN:
                            if (p_sprite_ex.isCollided(p_RiflemanSprite)  && lg_player_alive)
                            {
                                KillRifleman(p_sprite_ex);
                            }
                        break;
                    }
                }
                //if (true)
                {
                    if (p_EnemySprite.isCollided(p_RiflemanSprite)  && lg_player_alive)
                    {
                        KillRifleman(p_EnemySprite);
                    }
                }
            }

            if (p_RiflemanSprite.i_RoadX != 0)
            {
                if (p_RiflemanSprite.i_Control == 0)
                {
                    p_RiflemanSprite.i_RoadX -= RIFLEMAN_OUTCOMING_SPEED;
                    if (p_RiflemanSprite.i_RoadX <= 0)
                    {
                        p_RiflemanSprite.i_Control = 1;
                        p_RiflemanSprite.i_RoadX = 0;
                        fire_time = i_Timer - PLAYER_FIRE_FREQ -1;
                        PlayerFire();
                    }
                }
                else
                {
                    p_RiflemanSprite.i_RoadX += RIFLEMAN_OUTCOMING_SPEED;
                    if (p_RiflemanSprite.i_RoadX >= p_RiflemanSprite.i_width)
                    {
                        p_RiflemanSprite.i_Control = 0;
                        p_RiflemanSprite.i_RoadX = 0;
                        p_RiflemanSprite.lg_SpriteActive = false;
                    }
                }
            }
        }

        PlayerStateControl();

        return false;
    }

    private void KillRifleman(Sprite obj)
    {
        i_PlayerForce -= 100;

        loadStateForSprite(p_RiflemanSprite, STATE_RIGHT);
        p_RiflemanSprite.setMainPointXY(p_PlayerSprite.i_mainX + p_PlayerSprite.i_width, p_PlayerSprite.i_mainY);
        p_GameActionListener.gameAction(GAMEACTION_SND_COPDIED);
    }

    private void loadStateForSprite(Sprite _sprite, int _state)
    {
        int i_offset = _sprite.i_ObjectType * 10 * 12 + (10 * _state);
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


    public void PlayerStateControl()
    {
        //
        int Car_front = getHeightRoad(p_PlayerSprite.i_RoadPosition >> 8);
        int Car_back = getHeightRoad((p_PlayerSprite.i_RoadPosition - (22<<8)/*p_PlayerSprite.i_height*/) >> 8);

        if (Car_back < Car_front)
        {
            // State UP
            loadStateForSprite(p_PlayerSprite, (p_PlayerSprite.i_ObjectState | STATE_UP) & ~STATE_DOWN);
        }
        if (Car_back > Car_front)
        {
            // State DOWN
            loadStateForSprite(p_PlayerSprite, (p_PlayerSprite.i_ObjectState | STATE_DOWN) & ~STATE_UP);
        }
        if (Car_back == Car_front)
        {
            if (((p_PlayerSprite.i_ObjectState) != STATE_FLY) & ((p_PlayerSprite.i_ObjectState & STATE_UP) != 0) & (p_PlayerSprite.i_Speed >= SPEED_JUMP_LIMIT) & (p_PlayerSprite.i_Control == 0))
            {
                loadStateForSprite(p_PlayerSprite, STATE_FLY);
                p_PlayerSprite.i_Control = CAR_FLY_DELAY + (p_PlayerSprite.i_Speed >> 10);
            }
            else
            {
                if (p_PlayerSprite.i_Control == 0)
                {
                // State NORMAL
                loadStateForSprite(p_PlayerSprite, STATE_NORMAL);
                }
            }
        }


        if (p_PlayerSprite.i_ObjectState != STATE_FLY)
        {
        int value;
        value = p_PlayerSprite.i_ObjectState & (STATE_RIGHT | STATE_LEFT);
        if (i_player_HSpeed == 0)
        {
            if ((value == STATE_RIGHT ) || (value == STATE_LEFT ))
            {
                loadStateForSprite(p_PlayerSprite, p_PlayerSprite.i_ObjectState & ~(STATE_RIGHT | STATE_LEFT));
            }
        }
        else
        {
            if ((i_player_HSpeed > 0) & (value != STATE_RIGHT))
            {
                loadStateForSprite(p_PlayerSprite, (p_PlayerSprite.i_ObjectState | STATE_RIGHT) & ~STATE_LEFT);
            }
            if ((i_player_HSpeed < 0) & (value != STATE_LEFT))
            {
                loadStateForSprite(p_PlayerSprite, (p_PlayerSprite.i_ObjectState | STATE_LEFT) & ~STATE_RIGHT);
            }
        }
        }
        /*
        if (i_player_Slowdown)
        {
            loadStateForSprite(p_PlayerSprite, STATE_STOP);
        }
        */
    }

    public void CarStateControl(Sprite _object, int turn)
    {
        //
        int Car_front = getHeightRoad(_object.i_RoadPosition >> 8);
        int Car_back = getHeightRoad((_object.i_RoadPosition - _object.i_height) >> 8);

        if (Car_back < Car_front)
        {
            // State UP
            loadStateForSprite(_object, (_object.i_ObjectState | STATE_UP) & ~STATE_DOWN);
        }
        if (Car_back > Car_front)
        {
            // State DOWN
            loadStateForSprite(_object, (_object.i_ObjectState | STATE_DOWN) & ~STATE_UP);
        }
        if (Car_back == Car_front)
        {
            if ((_object.i_ObjectState == STATE_UP) & (_object.i_Speed >= SPEED_JUMP_LIMIT) & (_object.i_Control == 0))
            {
                loadStateForSprite(_object, STATE_FLY);
                _object.i_Control = CAR_FLY_DELAY + (_object.i_Speed >> 10);
            }
            else
            {
                if (_object.i_Control == 0)
                {
                // State NORMAL
                loadStateForSprite(_object, STATE_NORMAL);
                }
            }
        }

        if (turn == TURNRIGHT)
        {
            loadStateForSprite(_object, (_object.i_ObjectState | STATE_RIGHT) & ~STATE_LEFT);
            _object.i_HSpeed += HSPEED_INCREASE;
        }

        if (turn == TURNLEFT)
        {
            loadStateForSprite(_object, (_object.i_ObjectState | STATE_LEFT) & ~STATE_RIGHT);
            _object.i_HSpeed -= HSPEED_DECREASE;
        }
        if (turn == TURNCENTER)
        {
//            loadStateForSprite(_object, STATE_NORMAL);
            if (_object.i_HSpeed != 0)
            {
                if (_object.i_HSpeed > 0)
                {
                    _object.i_HSpeed -= HSPEED_DECREASE;
                }
                else
                {
                    _object.i_HSpeed += HSPEED_INCREASE;
                }
            }

        }
    }

public void GameControl()
    {
      // If Player is not dead then ...
      if (p_PlayerSprite.i_Control == 0)
      {
        if ((i_PlayerKey & PLAYER_KEY_UP) != 0)
        {
            if (i_player_VSpeed < PLAYER_MAX_VSPEED)
            {
                if (i_player_VSpeed + PLAYER_VSPEED_INCREASE < PLAYER_MAX_VSPEED)
                {
                    i_player_VSpeed += PLAYER_VSPEED_INCREASE;
                }
                else
                {
                    i_player_VSpeed = PLAYER_MAX_VSPEED;
                }
            }
        }

        if ((i_PlayerKey & PLAYER_KEY_DOWN) != 0)
        {
            if (i_player_VSpeed > 0) // (0 - PLAYER_MAX_VSPEED)
            {
                if (i_player_VSpeed > PLAYER_VSPEED_DECREASE)
                {
                    i_player_VSpeed -= PLAYER_VSPEED_DECREASE;
                }
                else
                {
                    i_player_VSpeed = 0;
                }
            }
            i_player_Slowdown = true;
        }
        else
        {
            i_player_Slowdown = false;
        }

        if ((i_PlayerKey & PLAYER_KEY_RIGHT) != 0)
        {
            if (i_player_HSpeed < PLAYER_MAX_HSPEED & i_player_VSpeed !=0 )
            {
                i_player_HSpeed += PLAYER_HSPEED_INCREASE;
            }
        }

        if ((i_PlayerKey & PLAYER_KEY_LEFT) != 0)
        {
            if (i_player_HSpeed > (0 - PLAYER_MAX_HSPEED) & i_player_VSpeed !=0 )
            {
                i_player_HSpeed -= PLAYER_HSPEED_DECREASE;
            }
        }

      }
      else
      {
      p_PlayerSprite.i_Control--;
      }

        int i_t_mainX = p_PlayerSprite.i_mainX + i_player_HSpeed;
        int i_t_mainY = p_PlayerSprite.i_mainY; //+ i_player_VSpeed;

        if ((i_PlayerKey & (PLAYER_KEY_RIGHT | PLAYER_KEY_LEFT)) == 0)
        {
            if (i_player_HSpeed != 0)
                {
                    i_player_HSpeed += i_player_HSpeed>0 ? -Math.min(i_player_HSpeed,PLAYER_HSPEED_DECREASE)
                                                         : Math.min(-i_player_HSpeed,PLAYER_HSPEED_DECREASE);
                }
            /*
            if (i_player_VSpeed != 0)
                {
                    if (i_player_VSpeed > 0) {i_player_VSpeed -= PLAYER_VSPEED_DECREASE;}
                    else                         {i_player_VSpeed += PLAYER_VSPEED_DECREASE;}
                }
            */
        }
        if  (i_t_mainX < MinPlayerArea_X) { i_t_mainX = MinPlayerArea_X; i_player_HSpeed = 0; }
        if  (i_t_mainX > MaxPlayerArea_X) { i_t_mainX = MaxPlayerArea_X; i_player_HSpeed = 0; }
        if  (i_t_mainY < MinPlayerArea_Y) { i_t_mainY = MinPlayerArea_Y; i_player_VSpeed = 0; }
        if  (i_t_mainY > MaxPlayerArea_Y) { i_t_mainY = MaxPlayerArea_Y; i_player_VSpeed = 0; }

        p_PlayerSprite.setMainPointXY(i_t_mainX, i_t_mainY);

        if ((i_PlayerKey & PLAYER_KEY_SPACE) != 0)
        {
            PlayerFire();
        }


    }

    public void EnemyCar_AI(Sprite car)
    {
        for (int ix = 0; ix < MAXSPRITENUMBER; ix++)
        {
            CheckCar(car, ap_SpriteArray[ix]);
        }
    }

    public void Car_AI(Sprite car)
    {
        for (int ix = 0; ix < MAXSPRITENUMBER; ix++)
        {
            CheckCar(car, ap_SpriteArray[ix]);
        }
        p_PlayerSprite.i_RoadX = p_PlayerSprite.i_mainX - (getRoad(p_PlayerSprite.i_RoadPosition >> 8) << 8) ;
        CheckCar(car, p_PlayerSprite);
        CheckCar(car, p_EnemySprite);
    }

    public void CheckCar(Sprite car, Sprite obj)
    {
        if (obj.lg_SpriteActive && (car != obj))
        {
            switch (obj.i_ObjectType)
            {
                case OBJECT_PLAYER:
                case OBJECT_ENEMY_CAR:
                case OBJECT_CAR1:
                case OBJECT_CAR2:
                case OBJECT_CYCLE:
                case OBJECT_BOX:

                    if (car.i_Speed > obj.i_Speed)
                    {
                        if ((car.i_RoadPosition < obj.i_RoadPosition) && (car.i_RoadPosition > obj.i_RoadPosition - obj.i_height * 2))
                        {
                            if (car.i_RoadX > obj.i_RoadX)
                            {
                                if (obj.i_RoadX + obj.i_width + SAFE_ZONE > car.i_RoadX)
                                // RIGHT TURN
                                {
                                    if (car.i_RoadX < (i_RoadWidth << 8) - car.i_width + SAFE_ZONE)
                                    {
                                        car.i_RoadX += 0x0280;
                                    }
                                    else
                                    {
                                        car.i_Speed = 0;
                                        car.i_HSpeed = 0;
                                        car.i_CrashTime = CRASH_STARTENGINE_DELAY;
                                    }
                                }
                            }
                            else
                            {
                                if (car.i_RoadX + car.i_width + SAFE_ZONE > obj.i_RoadX)
                                // LEFT TURN
                                {
                                    if (car.i_RoadX > 0 - SAFE_ZONE)
                                    {
                                        car.i_RoadX -= 0x0280;
                                    }
                                    else
                                    {
                                        car.i_Speed = 0;
                                        car.i_HSpeed = 0;
                                        car.i_CrashTime = CRASH_STARTENGINE_DELAY;
                                    }
                                }
                            }
                        }
                    }
                break;
            }
        }
    }

    public void nextGameStep(Object _playermoveobject)
    {
        i_Timer++;

        p_PlayerSprite.i_RoadPosition += i_player_VSpeed;
        i_View_Position = p_PlayerSprite.i_RoadPosition + INIT_PLAYER_Y;
        i_Bottom_Position = i_View_Position - INIT_ACTIVE_ROAD_RANGE;

        generateTraffic();
        processTraffic();

        if (i_EnemyForce <= 0 && i_PlayerForce > 0)
        {
               //
               // last scenario:
               //   enemy's car and player should stops slowly
               //
               if(lg_player_alive)                                 // init scenario states
               {
                   lg_player_alive = false;                        // turning to _ghost_ mode
                                                                   // for inhibite collisions
                   loadStateForSprite(p_PlayerSprite, STATE_NORMAL);
                   p_PlayerSprite.i_Control = 0;                   // Land player
                   i_player_HSpeed = 0;

                   loadStateForSprite(p_EnemySprite, STATE_NORMAL);
                   p_EnemySprite.i_Control = 0;                    // Land enemy

                   i_EnemyForce = 0;
                   p_EnemySprite.i_CrashTime = WIN_STAGE_DELAY<<4; // set fixed timedelay
               }
                                                                   // driving car's
               // drive enemy

                    int enemy_radius = p_EnemySprite.i_mainX - (getRoad(p_EnemySprite.i_RoadPosition >> 8) << 8);
                    if(p_EnemySprite.i_Speed > 0)
                    {
                      // fix radius
                      int oldPosition = p_EnemySprite.i_RoadPosition >> 8;
                      p_EnemySprite.i_RoadPosition += p_EnemySprite.i_Speed;
                      int objectY = i_View_Position - p_EnemySprite.i_RoadPosition;
                      int objectX = enemy_radius + (getRoad(p_EnemySprite.i_RoadPosition >> 8) << 8);
                      p_EnemySprite.setMainPointXY(objectX, objectY);

                      //speed down
                      p_EnemySprite.i_Speed -= Math.min(p_EnemySprite.i_Speed,VSPEED_DECREASE);
                    }

               // drive player
               int HaltDistance = 12<<8;

                    i_PlayerKey = 0;                                               // clear controls

                    int deltaY =  (p_EnemySprite.i_RoadPosition - p_PlayerSprite.i_RoadPosition);

                    if(deltaY > HaltDistance)
                    {
                      // horizontal motion
                        int player_radius = p_PlayerSprite.i_mainX - (getRoad(p_PlayerSprite.i_RoadPosition >> 8) << 8);

                        int deltaX =  enemy_radius - player_radius;
                        i_player_HSpeed = Math.min(PLAYER_MAX_HSPEED, Math.abs(deltaX>>1));
                        if(deltaX < 0) i_player_HSpeed = -i_player_HSpeed;

                      // vertical motion
                        i_player_VSpeed = Math.min(PLAYER_MAX_VSPEED, Math.abs(deltaY>>2));
                        if(deltaY < 0) i_player_VSpeed = -i_player_VSpeed;
                        p_PlayerSprite.i_Speed = i_player_VSpeed;

                        p_EnemySprite.i_CrashTime = WIN_STAGE_DELAY; // set fixed timedelay

                    }
                    else
                    {
                      // hor.speed down
                        int step = Math.min(Math.abs(i_player_HSpeed),PLAYER_HSPEED_DECREASE);
                        i_player_HSpeed -= (i_player_HSpeed > 0) ? step : -step;
                      // vert.speed down
                        step = Math.min(Math.abs(i_player_VSpeed),PLAYER_VSPEED_DECREASE>>1);
                        i_player_VSpeed -= (i_player_VSpeed > 0) ? step : -step;
                        p_PlayerSprite.i_Speed = i_player_VSpeed;
                    }

               if(--p_EnemySprite.i_CrashTime <= 0 )                // that's all
               {
                   i_PlayerState = PLAYERSTATE_WON;
                   i_GameState = GAMESTATE_OVER;
               }
        }
         else
             processEnemy();

        if (processPlayer())
        {
          if(--p_PlayerSprite.i_CrashTime>0)
          {

            // check rifleman
            if(p_RiflemanSprite.lg_SpriteActive)
            {
              if(p_RiflemanSprite.i_ObjectState != STATE_NORMAL)
              {
                p_RiflemanSprite.setMainPointXY(
                           p_PlayerSprite.i_mainX + p_PlayerSprite.i_width
                         , p_PlayerSprite.i_mainY + RIFLEMAN_Y_OFFSET);

                // animation and speed down
                if(p_RiflemanSprite.processAnimation())
                if (p_PlayerSprite.i_Speed >0 )
                {
                  p_PlayerSprite.i_Speed -= Math.min(p_PlayerSprite.i_Speed,PLAYER_VSPEED_DECREASE>>1);
                  i_player_VSpeed = p_PlayerSprite.i_Speed;
                }
                else
                if (p_PlayerSprite.i_Speed <0 )
                {
                  p_PlayerSprite.i_Speed += Math.min(-p_PlayerSprite.i_Speed,PLAYER_VSPEED_DECREASE);
                  i_player_VSpeed = p_PlayerSprite.i_Speed;
                }
              }
              else
                p_RiflemanSprite.lg_SpriteActive = false;

            }
            else

            // else explode car
            {
             if (p_PlayerSprite.i_ObjectType != OBJECT_EXPLOSION)
             {
                ExplodeCar(p_PlayerSprite);
                p_PlayerSprite.i_Speed = 0;
                i_player_VSpeed = p_PlayerSprite.i_Speed;

                p_GameActionListener.gameAction(GAMEACTION_SND_PLAYERDEATH);

             }
             else
              if(p_PlayerSprite.processAnimation())
                 p_PlayerSprite.lg_SpriteActive = false;
            }
          }
          else
          {

               int centerX = (getRoad(p_PlayerSprite.i_RoadPosition>>8) + (i_RoadWidth - (p_PlayerSprite.i_width>>8)>>1))<<8;
               if(!p_PlayerSprite.lg_SpriteActive && i_Attemptions > 1 && Math.abs(p_PlayerSprite.i_mainX - centerX)>(5<<8))
               {
                 p_PlayerSprite.i_mainX += p_PlayerSprite.i_mainX > centerX ? -(5<<8) : (5<<8);
               }
               else
               {
                 i_PlayerState = PLAYERSTATE_LOST;
                 i_Attemptions--;
                 if (i_Attemptions <= 0)
                 {
                  i_GameState = GAMESTATE_OVER;
                 }
               }
          }
        }

        i_PlayerScore = Math.max(-p_PlayerSprite.i_RoadPosition >> 15, i_PlayerScore);
    }


    public void writeToStream(DataOutputStream _dataOutputStream) throws IOException
    {

        _dataOutputStream.writeShort(i_PlayerForce);
        _dataOutputStream.writeByte(i_Attemptions);
        _dataOutputStream.writeShort(i_player_HSpeed);
        _dataOutputStream.writeShort(i_player_VSpeed);
        _dataOutputStream.writeBoolean(lg_player_alive);

        _dataOutputStream.writeShort(i_EnemyForce);
//        _dataOutputStream.writeByte(strategy);

        _dataOutputStream.writeBoolean(lg_StageCompleted);
        _dataOutputStream.writeByte(i_StageDecorationNo);
        _dataOutputStream.writeBoolean(lg_Tunnel);
        _dataOutputStream.writeByte(i_LastRoad);
        _dataOutputStream.writeByte(i_LastRoadHeight);

        _dataOutputStream.writeInt(i_Timer);
        _dataOutputStream.writeInt(fire_time);
        _dataOutputStream.writeInt(object_time);
        _dataOutputStream.writeInt(born_time);
        _dataOutputStream.writeByte(i_immortality_time);


        _dataOutputStream.writeByte(p_PlayerSprite.i_ObjectType);
        _dataOutputStream.writeByte(p_PlayerSprite.i_ObjectState);
        p_PlayerSprite.writeSpriteToStream(_dataOutputStream);

        _dataOutputStream.writeByte(p_RiflemanSprite.i_ObjectType);
        _dataOutputStream.writeByte(p_RiflemanSprite.i_ObjectState);
        p_RiflemanSprite.writeSpriteToStream(_dataOutputStream);

        _dataOutputStream.writeByte(p_EnemySprite.i_ObjectType);
        _dataOutputStream.writeByte(p_EnemySprite.i_ObjectState);
        p_EnemySprite.writeSpriteToStream(_dataOutputStream);

        Sprite p_spr;
        for (int li = 0; li < MAXSPRITENUMBER; li++)
        {
            p_spr = ap_SpriteArray[li];
            _dataOutputStream.writeByte(p_spr.i_ObjectType);
            _dataOutputStream.writeByte(p_spr.i_ObjectState);
            p_spr.writeSpriteToStream(_dataOutputStream);
        }
    }

    public void readFromStream(DataInputStream _dataInputStream) throws IOException
    {

        i_PlayerForce = _dataInputStream.readShort();
        i_Attemptions = _dataInputStream.readByte();
        i_player_HSpeed = _dataInputStream.readShort();
        i_player_VSpeed = _dataInputStream.readShort();
        lg_player_alive = _dataInputStream.readBoolean();

        i_EnemyForce = _dataInputStream.readShort();
//        strategy = _dataInputStream.readByte();

        lg_StageCompleted = _dataInputStream.readBoolean();
        i_StageDecorationNo = _dataInputStream.readUnsignedByte();
        lg_Tunnel = _dataInputStream.readBoolean();
        i_LastRoad = _dataInputStream.readUnsignedByte();
        i_LastRoadHeight = _dataInputStream.readUnsignedByte();

        i_Timer = _dataInputStream.readInt();
        fire_time = _dataInputStream.readInt();
        object_time = _dataInputStream.readInt();
        born_time = _dataInputStream.readInt();
        i_immortality_time = _dataInputStream.readByte();


        p_PlayerSprite.i_ObjectType = _dataInputStream.readUnsignedByte();
        loadStateForSprite(p_PlayerSprite, _dataInputStream.readUnsignedByte());
        p_PlayerSprite.readSpriteFromStream(_dataInputStream);

        p_RiflemanSprite.i_ObjectType = _dataInputStream.readUnsignedByte();
        loadStateForSprite(p_RiflemanSprite, _dataInputStream.readUnsignedByte());
        p_RiflemanSprite.readSpriteFromStream(_dataInputStream);

        p_EnemySprite.i_ObjectType = _dataInputStream.readUnsignedByte();
        loadStateForSprite(p_EnemySprite, _dataInputStream.readUnsignedByte());
        p_EnemySprite.readSpriteFromStream(_dataInputStream);

        Sprite p_spr;
        for (int li = 0; li < MAXSPRITENUMBER; li++)
        {
            p_spr = ap_SpriteArray[li];
            p_spr.i_ObjectType = _dataInputStream.readUnsignedByte();
            loadStateForSprite(p_spr, _dataInputStream.readUnsignedByte());
            p_spr.readSpriteFromStream(_dataInputStream);
        }
    }


    public String getGameID()
    {
        return "RoadRace";
    }

    public int getMaxSizeOfSavedGameBlock()
    {
        return 1300;
    }

    public int getPlayerScore()
    {
            // (p_PlayerSprite.i_RoadPosition >> 8) / 128 * 10 + i_PlayerScore * 10
            // = ((p_PlayerSprite.i_RoadPosition >> 15)  + i_PlayerScore ) * 10  , 2^7 = 128

            return Math.max(((p_PlayerSprite.i_RoadPosition >> 15 ) + i_PlayerScore) * i_ScoreMultiplicator, 0);
    }
}
