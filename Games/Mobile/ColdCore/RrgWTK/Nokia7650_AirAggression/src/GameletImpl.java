

import java.io.DataInputStream;
import java.io.IOException;
import java.io.DataOutputStream;

public class GameletImpl extends Gamelet
{
    private static final int SCALEFACTOR = 1;
//=====================================================================//
    // Size of landscape objects array
    public static final int MAX_LANDSCAPE = 10;
    // Size of game objects array
    public static final int SPRITES_NUMBER = 60;

    // Game objects
    public static final int OBJECT_PLAYER       = 0;
    public static final int OBJECT_MISSLE       = 1;
    public static final int OBJECT_BULLET       = 2;
    public static final int OBJECT_AIR_FIGHTER  = 3;
    public static final int OBJECT_AIR_BOMBER   = 4;
    public static final int OBJECT_AIR_PLANE    = 5;
    public static final int OBJECT_AIR_SPY      = 6;
    public static final int OBJECT_E_BULLET1    = 7;
    public static final int OBJECT_E_BULLET2    = 8;
    public static final int OBJECT_E_MISSLE     = 9;
    public static final int OBJECT_EXPLOSION    = 10;
    public static final int OBJECT_BONUS1       = 11;
    public static final int OBJECT_BONUS2       = 12;
    public static final int OBJECT_BONUS3       = 13;
    public static final int OBJECT_BONUS4       = 14;
    //
    public static final int OBJECT_BONUS5       = 15;
    public static final int OBJECT_BONUS6       = 16;
    public static final int OBJECT_BONUS7       = 17;
    public static final int OBJECT_BONUS8       = 18;
    public static final int OBJECT_BONUS9       = 19;
    public static final int OBJECT_BONUS10      = 20;
    public static final int OBJECT_BONUS11      = 21;
    //
    public static final int OBJECT_PARACHUTER   = 22;
    public static final int OBJECT_DEADBODY     = 23;
    public static final int OBJECT_CLOUD        = 24;
    // Landscape objects
    public static final int OBJECT_ISLAND1      = 25;
    public static final int OBJECT_ISLAND2      = 26;
    public static final int OBJECT_ISLAND3      = 27;
    public static final int OBJECT_STONE1       = 28;
    public static final int OBJECT_STONE2       = 29;
    public static final int OBJECT_STONE3       = 30;
    // Boss
    public static final int OBJECT_BOSS         = 31;
    // Boss explosion
    public static final int OBJECT_BOSS_EXPLOSION = 32;
    //
    public static final int OBJECT_DEAD_PLANE   = 33;
    public static final int OBJECT_DEAD_BOMBER  = 34;
    public static final int OBJECT_DEAD_FIGHTER = 35;
    public static final int OBJECT_DEAD_SPY     = 36;
    public static final int OBJECT_DEAD_PLAYER  = 37;
    public static final int OBJECT_HI_EXPLOSION = 38;
    public static final int OBJECT_HELICOPTER   = 39;

    // Landscape types
    public static final int ISLAND1       = 1;
    public static final int ISLAND2       = 2;
    public static final int ISLAND3       = 3;
    public static final int STONE1        = 4;
    public static final int STONE2        = 5;
    public static final int STONE3        = 6;

    // Object Sizes
     public static final int MISSLE_W      = 3;
     public static final int MISSLE_H      = 11;
     //
     public static final int BULLET_W      = 3;
     public static final int BULLET_H      = 3;
     //
     public static final int E_MISSLE_W    = 3;
     public static final int E_MISSLE_H    = 6;
     //
     public static final int E_BULLET1_W   = 3;
     public static final int E_BULLET1_H   = 3;
     //
     public static final int E_BULLET2_W   = 3;
     public static final int E_BULLET2_H   = 3;
     //
     public static final int AIR_PLANE_W   = 24;
     public static final int AIR_PLANE_H   = 24;
     //
     public static final int AIR_BOMBER_W  = 20;
     public static final int AIR_BOMBER_H  = 20;
     //
     public static final int AIR_FIGHTER_W = 16;
     public static final int AIR_FIGHTER_H = 16;
     //
     public static final int AIR_SPY_W     = 16;
     public static final int AIR_SPY_H     = 16;
     //
     public static final int PARACHUTER_W  = 9;
     public static final int PARACHUTER_H  = 9;
     //
     public static final int DEADBODY_W    = 9;
     public static final int DEADBODY_H    = 9;
     //
     public static final int CLOUD_W       = 64;
     public static final int CLOUD_H       = 48;
     //
     public static final int BONUS1_W      = 24;
     public static final int BONUS1_H      = 24;
     //
     public static final int BONUS2_W      = 24;
     public static final int BONUS2_H      = 24;
     //
     public static final int BONUS3_W      = 16;
     public static final int BONUS3_H      = 16;
     //
     public static final int BONUS4_W      = 16;
     public static final int BONUS4_H      = 16;
     // bonus5, 6, 7, 8, 9, 10
     public static final int BONUSA_W      = 66;
     public static final int BONUSA_H      = 43;
     public static final int BONUSA_INT_W  = 15;
     public static final int BONUSA_INT_H  = 14;
     public static final int BONUSA_OFS_X  = 26;
     public static final int BONUSA_OFS_Y  = 13;
     //
     public static final int EXPLOSION_W   = 32;
     public static final int EXPLOSION_H   = 32;
     // Landscape sizes
     public static final int ISLAND1_W     = 32;
     public static final int ISLAND1_H     = 24;
     //
     public static final int ISLAND2_W     = 48;
     public static final int ISLAND2_H     = 24;
     //
     public static final int ISLAND3_W     = 12;
     public static final int ISLAND3_H     = 28;
     //
     public static final int STONE1_W      = 10;
     public static final int STONE1_H      = 10;
     //
     public static final int STONE2_W      = 8;
     public static final int STONE2_H      = 8;
     //
     public static final int STONE3_W      = 4;
     public static final int STONE3_H      = 4;
     //
     public static final int BOSS_W        = 94; //64
     public static final int BOSS_OFS_Y    = 0; //64
     public static final int BOSS_INT_H    = 55; //64
     public static final int BOSS_H        = 64; //48
     //
     public static final int BOSS_EXPLOSION_W   = 80;
     public static final int BOSS_EXPLOSION_H   = 48;

    // Speeds
    public static final int MISSLE_SPEED      = 0x1200;  //600;
    public static final int BULLET_SPEED      = 0x0900;  //300;
    public static final int AIR_FIGHTER_SPEED = 0x06F0;  //250;
    public static final int AIR_BOMBER_SPEED  = 0x03F0;  //150;
    public static final int AIR_PLANE_SPEED   = 0x0360;
    public static final int AIR_SPY_SPEED     = 0x0240;
    //
    public static final int PARACHUTER_SPEED  = 0x180;
    //
    public static final int BOSS_SPEED        = 0x0C0;
    // Landscape movement factor
    public static final int CLOUD_SPEED       = 0x200;
    //
    public static final int LANDSCAPE_SPEED = 0x0C0;
    //
    public static final int BONUS1_SPEED  = 0x300;
    public static final int BONUS2_SPEED  = 0x300;
    public static final int BONUS3_SPEED  = 0x400;
    public static final int BONUS4_SPEED  = 0x400;
    //
    public static final int BONUSA_SPEED  = 0x0F0;
    public static final int HELICOPTER_AWAY_SPEED  = 0x200;
    //
    public static final int EXPLOSION_SPEED = 0x300;

    // Directions                            //SWEN
    public static final int NORTH       = 1; //0001
    public static final int EAST        = 2; //0010
    public static final int NORTH_EAST  = 3; //0011
    public static final int WEST        = 4; //0100
    public static final int NORTH_WEST  = 5; //0101
    public static final int SOUTH       = 8; //1000
    public static final int SOUTH_EAST  = 10;//1010
    public static final int SOUTH_WEST  = 12;//1100

    // Weapons
    public static final int WEAPON_SINGLE_BULLET = 0;
    public static final int WEAPON_DOUBLE_BULLET = 1;
    public static final int HEALTH_BONUS         = 2;
    public static final int WEAPON_TRIBLE_BULLET = 3;
    public static final int WEAPON_QUADRA_BULLET = 4;
    public static final int WEAPON_QUANTA_BULLET = 5;
    public static final int WEAPON_EXTRA_BULLET  = 6;


    // Weapons Scores                              easy   normal  hard
    private final static short[] WEAPON_SCORES = {
                                                     0     ,0      ,0
                                                    ,100   ,100    ,100
                                                    ,500   ,500    ,500
                                                    ,800   ,800    ,800
                                                    ,2000  ,2000   ,2000
                                                    ,3000  ,3500   ,4000
                                                    ,5000  ,5500   ,6000
                                                 };

    public static int SINGLE_AVAILABLE_SCORE;
    public static int DOUBLE_AVAILABLE_SCORE;
    public static int HEALTH_AVAILABLE_SCORE;
    public static int TRIBLE_AVAILABLE_SCORE;
    public static int QUADRA_AVAILABLE_SCORE;
    public static int QUANTA_AVAILABLE_SCORE;
    public static int EXTRA_AVAILABLE_SCORE ;

    private final static short[] BORN_FREQUENCY = {

        // Update born frequency if required  /////
        //                      easy   normal hard
        /* horizont == 100  */   21     ,20   ,20
        /* horizont == 500  */  ,20     ,18   ,18
        /* horizont == 1500 */  ,19     ,16   ,16
        /* horizont == 2000 */  ,18     ,14   ,14
        /* horizont == 4000 */  ,17     ,13   ,12
        /* horizont == 6000 */  ,16     ,12   ,10
        /* horizont == 8000 */  ,15     ,11   ,9
        /* horizont == 10000*/  ,14     ,10   ,8
        /* horizont == 12000*/  ,13     ,9    ,7
        /* horizont == 14000*/  ,12     ,8    ,6
        /* horizont == 15000*/  ,11     ,7    ,5
        /* horizont == 16000*/  ,10     ,6    ,4
        /* horizont == 16500*/  ,50     ,50   ,50
   };

    // Boss styles
    public static final int BOSS_STYLE_NULL  = 0; // This style used only for waiting first boss.
    public static final int BOSS_STYLE_ONE   = 1; // When player meet the first boss then his style will be BOSS_STYLE_ONE
    public static final int BOSS_STYLE_TWO   = 2;
    public static final int BOSS_STYLE_THREE = 3;
    public static final int BOSS_STYLE_FOUR  = 4;
    public static final int BOSS_STYLE_FIVE  = 5;
    public static final int BOSS_STYLE_SIX   = 6;
    public static final int BOSS_STYLE_SEVEN = 7;
    public static final int BOSS_STYLE_EIGHT = 8;

    public static final int BOSS_DISTANCE = 2047;
    public static final int BOSS_HEALTH = 100;
    public static final int BOSS_HEALTH_INC = 10;

//==================SOUND ACTIONS================================
    // Hero coming on scene
    public static final int GAMEACTION_SND_PLAYERAPPEARED = 0;
    // Player's shot
    public static final int GAMEACTION_SND_PLAYERSHOT     = 1;
    // Player dead
    public static final int GAMEACTION_SND_PLAYERDEAD     = 2;
    // Small explosion
    public static final int GAMEACTION_SND_SMALLEXPLOSION = 3;
    // Huge explosion
    public static final int GAMEACTION_SND_HUGEEXPLOSION  = 4;
    // Bird's sadly cry
    public static final int GAMEACTION_SND_BIRDSCRY       = 5;
    // Boss coming
    public static final int GAMEACTION_SND_BOSSAPPEARS    = 6;
    // Paratrooper deceased
    public static final int GAMEACTION_SND_PARADEAD       = 7;
    // Appearing of helicopter
    public static final int GAMEACTION_SND_HELYAPPEARS    = 8;
    // Bonus captured
    public static final int GAMEACTION_SND_BONUSTAKEN     = 9;
    // Player wounded
    public static final int GAMEACTION_SND_PLAYERWOUNDED  = 10;
//================================================================

//===================== KEYS======================================
    public static final int PLAYER_BUTTON_NONE = 0;
    public static final int PLAYER_BUTTON_LEFT = 1;
    public static final int PLAYER_BUTTON_RIGHT = 2;
    public static final int PLAYER_BUTTON_TOP = 4;
    public static final int PLAYER_BUTTON_DOWN = 8;
    public static final int PLAYER_BUTTON_MISSLEFIRE = 16;
    public static final int PLAYER_BUTTON_BULLETFIRE = 32;
//================================================================

    public static final int GAMELEVEL_EASY = 0;
    public static final int GAMELEVEL_NORMAL = 1;
    public static final int GAMELEVEL_HARD = 2;

    private static final int GAMELEVEL_EASY_TIMEDELAY = 100; //30;
    private static final int GAMELEVEL_NORMAL_TIMEDELAY = 90; //20;
    private static final int GAMELEVEL_HARD_TIMEDELAY = 80; //10;

    private static final int GAMELEVEL_EASY_ATTEMPTIONS = 4;
    private static final int GAMELEVEL_NORMAL_ATTEMPTIONS = 3;
    private static final int GAMELEVEL_HARD_ATTEMPTIONS = 2;

    private static final int GAMELEVEL_EASY_PLAYERHEALTH = 100;
    private static final int GAMELEVEL_NORMAL_PLAYERHEALTH = 100;
    private static final int GAMELEVEL_HARD_PLAYERHEALTH = 100;

//================================================================

    public  static final int I8_PLAYER_MAX_HORZSPEED = 0x300;
    private static final int I8_PLAYER_HORZSPEED_INCREASE = 0x180;
    private static final int I8_PLAYER_HORZSPEED_DECREASE = 0x180;

    private static final int I8_PLAYER_MAX_VERTSPEED = 0x300;
    private static final int I8_PLAYER_VERTSPEED_INCREASE = 0x180;
    private static final int I8_PLAYER_VERTSPEED_DECREASE = 0x180;

//=================================================================
    public static final int PLAYER_AREA_BORDER = 2;           //4
    public static final int PLAYER_WIDTH = 21;
    public static final int PLAYER_OFS_X = 3;
    public static final int PLAYER_INT_W = PLAYER_WIDTH - PLAYER_OFS_X*2;
    public static final int PLAYER_HEIGHT = 26;
    private final static int IMMORTALITY_TIMEDELAY = 50;      //50 cycles = ~5 sec
    private final static int GAMEOVER_TIMEDELAY = 50;         //50 cycles = ~5 sec
private final static int BOTTOM_GAP = 0;                 // Minimal gap between bottom of screen and player

    public int MinPlayerArea_X;
    public int MaxPlayerArea_X;
    public int MinPlayerArea_Y;
    public int MaxPlayerArea_Y;
//=================================================================

    public  int i8_player_HorzSpeed;
    private int i8_player_VertSpeed;

    public int i_playerHealthInit;

    public int i_playerHealth;
    public int i_Attemptions;
    public int i_PlayerKey;
    //Weapon
    public int i_Weapon;

    public int i_bossStyle;
    public int i_bossInitHealth;
    public int i_bossHealth;

    public boolean i_bossmode;
    public boolean i_boss_active;

    private int i8_Init_Player_X;
    private int i8_Init_Player_Y;
    public boolean lg_justStarted;
    public boolean lg_immortality;
    private int i_immortality_timeleft;

    public int i_GameTimeDelay;

    //==================Unique sprites============================
    public Sprite p_PlayerSprite;
    //=====================New VARs===============================
    // Current game position
    public int horizont;
    //=====================New VARs===============================
    public Sprite[] ap_Sprites;
    public Sprite[] ap_Landscape;

    private Sprite _getFirstInactiveLandscape()
    {
        for (int li = 0; li < MAX_LANDSCAPE; li++)
        {
            if (!ap_Landscape[li].lg_SpriteActive) return ap_Landscape[li];
        }
        return null;
    }

    private Sprite _getFirstInactiveSprite()
    {
        for (int li = 0; li < SPRITES_NUMBER; li++)
        {
            if (!ap_Sprites[li].lg_SpriteActive) return ap_Sprites[li];
        }
        for (int li = 0; li < SPRITES_NUMBER; li++)
        {
            if (ap_Sprites[li].i_ObjectType == OBJECT_EXPLOSION) return ap_Sprites[li];
        }
        return null;
    }

    private void _deactivateAllSprites(boolean _deactivateSunkShips)
    {
        for (int li = 0; li < SPRITES_NUMBER; li++)
        {
            if (_deactivateSunkShips)
                ap_Sprites[li].lg_SpriteActive = false;
            else
                switch (ap_Sprites[li].i_ObjectType)
                {

                    default :
                        ap_Sprites[li].lg_SpriteActive = false;
                }
        }
    }

    private int landscape_time;
    private int landscape_freq;

    // Horizont position when player launched missle or bullet
    private int fire_time;
    // Current player fire frequency
    private int fire_freq;
    // Current player missle frequency
    private int player_missle_freq;
    // Horizont position where was born last enemy
    private int born_time;
    // Current enemy born frequency
    private int born_freq;

    // Generate enemy
    // This method generate new enemy object with use born_freq property
    // Also it updates born_freq depend on current game position
    private void generateEnemy()
    {
        int lc_type;
        int lc_speed;
        int lc_direction;
        int lc_x;
        int lc_y;
        int lc_h;
        int lc_w;

        // Update born frequency if required  /////

        switch(horizont)
        {
            case 100  :  born_freq = BORN_FREQUENCY[ 0 * 3 + i_GameLevel]; break;
            case 500  :  born_freq = BORN_FREQUENCY[ 1 * 3 + i_GameLevel]; break;
            case 1500 :  born_freq = BORN_FREQUENCY[ 2 * 3 + i_GameLevel]; break;
            case 2000 :  born_freq = BORN_FREQUENCY[ 3 * 3 + i_GameLevel]; break;
            case 4000 :  born_freq = BORN_FREQUENCY[ 4 * 3 + i_GameLevel]; break;
            case 6000 :  born_freq = BORN_FREQUENCY[ 5 * 3 + i_GameLevel]; break;
            case 8000 :  born_freq = BORN_FREQUENCY[ 6 * 3 + i_GameLevel]; break;
            case 10000:  born_freq = BORN_FREQUENCY[ 7 * 3 + i_GameLevel]; break;
            case 12000:  born_freq = BORN_FREQUENCY[ 8 * 3 + i_GameLevel]; break;
            case 14000:  born_freq = BORN_FREQUENCY[ 9 * 3 + i_GameLevel]; break;
            case 15000:  born_freq = BORN_FREQUENCY[10 * 3 + i_GameLevel]; break;
            case 16000:  born_freq = BORN_FREQUENCY[11 * 3 + i_GameLevel]; break;
            case 16500:  born_freq = BORN_FREQUENCY[ 0 * 3 + i_GameLevel]; break;
        }
        ///////////////////////////////////////////
            if (i_bossmode)
            {
                if (!i_boss_active)
                {
                Sprite p_boss   = _getFirstInactiveSprite();

                lc_direction = SOUTH;
                lc_x = ((i_ScreenWidth / 2) - (BOSS_W / 2) << 8);

                lc_y = (1 - BOSS_H) << 8;

                p_boss.i_ObjectType = OBJECT_BOSS;
                p_boss.setMainPointXY(lc_x, lc_y);
                p_boss.i_Speed = BOSS_SPEED;
                p_boss.i_Direction = lc_direction;
                loadStateForSprite(p_boss, OBJECT_BOSS);
                    i_bossInitHealth = BOSS_HEALTH + i_bossStyle*BOSS_HEALTH_INC;
                    i_bossHealth = i_bossInitHealth;
                    i_boss_active = true;
                }
            }
            else
            {
            if (born_time + born_freq < horizont)
                {
                        int rndEnemyType = getRandomInt(16);
                        Sprite p_enemy   = _getFirstInactiveSprite();

                        lc_direction = SOUTH;
                        lc_x = 4 + (getRandomInt(120) << 8);

                        switch (rndEnemyType)
                            {
                                case 15:
                                case 1:
                                    lc_type = OBJECT_AIR_SPY;
                                    lc_speed = AIR_SPY_SPEED;
                                    lc_w = AIR_SPY_W;
                                    lc_h = AIR_SPY_H;
                                    lc_direction = NORTH;
                                    lc_y = (i_ScreenHeight - 1);
                                    break;
                                case 8:
                                    lc_type = OBJECT_AIR_FIGHTER;
                                    lc_speed = AIR_FIGHTER_SPEED;
                                    lc_w = AIR_FIGHTER_W;
                                    lc_h = AIR_FIGHTER_H;
                                    lc_y = 1 - lc_h;
                                    break;
                                case 6:
                                case 4:
                                    lc_type = OBJECT_AIR_BOMBER;
                                    lc_speed = AIR_BOMBER_SPEED;
                                    lc_w = AIR_BOMBER_W;
                                    lc_h = AIR_BOMBER_H;
                                    lc_y = 1 - lc_h;
                                    break;
                                default:
                                    lc_type = OBJECT_AIR_PLANE;
                                    lc_speed = AIR_PLANE_SPEED;
                                    lc_w = AIR_PLANE_W;
                                    lc_h = AIR_PLANE_H;
                                    lc_y = 1 - lc_h;
                                    break;
                            }

                        lc_y <<= 8;

                        p_enemy.i_ObjectType = lc_type;
                        p_enemy.setMainPointXY(lc_x, lc_y);
                        p_enemy.i_Speed = lc_speed;
                        p_enemy.i_Direction = lc_direction;
                        loadStateForSprite(p_enemy, lc_type);

                        born_time = horizont;
                }
            }
    }

    // Generate landscape
    // This method generate new landscape object with use landscape_freq property
    private void generateLandscape()
    {
        int lc_type;
        int lc_speed;
        int lc_direction;
        int lc_x;
        int lc_y;
        int lc_h;
        int lc_w;

         if (landscape_time + landscape_freq < horizont)
          {
              Sprite p_land   = _getFirstInactiveLandscape();

                        int rndObjectType = (getRandomInt(12) / 2) + 1;
                        lc_direction = SOUTH;
                        lc_x = 4 + (getRandomInt(120) << 8);
                        lc_speed =  LANDSCAPE_SPEED;
                        switch (rndObjectType)
                            {
                                case ISLAND1:
                                    lc_type = OBJECT_ISLAND1;
                                    lc_w = ISLAND1_W;
                                    lc_h = ISLAND1_H;
                                    lc_y = 1 - lc_h;
                                    break;
                                case ISLAND2:
                                    lc_type = OBJECT_ISLAND2;
                                    lc_w = ISLAND2_W;
                                    lc_h = ISLAND2_H;
                                    lc_y = 1 - lc_h;
                                    break;
                                case ISLAND3:
                                    lc_type = OBJECT_ISLAND3;
                                    lc_w = ISLAND3_W;
                                    lc_h = ISLAND3_H;
                                    lc_y = 1 - lc_h;
                                    break;
                                case STONE1:
                                    lc_type = OBJECT_STONE1;
                                    lc_w = STONE1_W;
                                    lc_h = STONE1_H;
                                    lc_y = 1 - lc_h;
                                    break;
                                case STONE2:
                                    lc_type = OBJECT_STONE2;
                                    lc_w = STONE2_W;
                                    lc_h = STONE2_H;
                                    lc_y = 1 - lc_h;
                                    break;
                                case STONE3:
                                    lc_type = OBJECT_STONE3;
                                    lc_w = STONE3_W;
                                    lc_h = STONE3_H;
                                    lc_y = 1 - lc_h;
                                    break;
                                default:
                                    lc_type = OBJECT_STONE3;
                                    lc_w = STONE3_W;
                                    lc_h = STONE3_H;
                                    lc_y = 1 - lc_h;
                                    break;
                            }

                        lc_y <<= 8;

                        p_land.i_ObjectType = lc_type;
                        p_land.setMainPointXY(lc_x, lc_y + (((horizont-1) * lc_speed) & 0xff));
                        p_land.i_Speed = lc_speed;
                        p_land.i_Direction = lc_direction;
                        loadStateForSprite(p_land, lc_type);

                        landscape_time = horizont;
          }
    }


    // Process Landscape
    // This method updates status and position of all landscape objects for one game turn
    public void processLandscape()
       {
           for (int ei = 0; ei < MAX_LANDSCAPE; ei++)

           {
                       Sprite p_land = ap_Landscape[ei];
                           if (p_land.lg_SpriteActive)
                           {
                               // Movement
                               int dir = p_land.i_Direction;
                               int landX = p_land.i_mainX;
                               int landY = p_land.i_mainY;

                               switch (dir)
                               {
                                   case NORTH:
                                       landY = p_land.i_mainY - p_land.i_Speed;
                                       break;
                                   case NORTH_WEST:
                                       landY = p_land.i_mainY - p_land.i_Speed;
                                       landX = p_land.i_mainX - p_land.i_Speed;
                                       break;
                                   case NORTH_EAST:
                                       landY = p_land.i_mainY - p_land.i_Speed;
                                       landX = p_land.i_mainX + p_land.i_Speed;
                                       break;
                                   case SOUTH:
                                       landY = p_land.i_mainY + p_land.i_Speed;
                                       break;
                                   case SOUTH_WEST:
                                       landY = p_land.i_mainY + p_land.i_Speed;
                                       landX = p_land.i_mainX - p_land.i_Speed;
                                       break;
                                   case SOUTH_EAST:
                                       landY = p_land.i_mainY + p_land.i_Speed;
                                       landX = p_land.i_mainX + p_land.i_Speed;
                                       break;

                                   case WEST:
                                       landX = p_land.i_mainX - p_land.i_Speed;
                                       break;
                                   case EAST:
                                       landX = p_land.i_mainX + p_land.i_Speed;
                                       break;
                               }

                               p_land.setMainPointXY(landX, landY);
                           // Deactivate object that has position out of game screen
                               int obj_y = landY >> 8;
                               int obj_x = landX >> 8;
                               int obj_w = p_land.i_width >> 8;
                               int obj_h = p_land.i_height >> 8;

                               if (obj_x < 0 - obj_w | obj_x > i_ScreenWidth | obj_y < 0 - obj_h | obj_y > i_ScreenHeight)
                                   {
                                       p_land.lg_SpriteActive = false;
                                   }
                           ////////////////////////////////////////////////////////////
                           }
           }
    }


    // Process Enemy
    // This method updates status and position of all enemy objects for one game turn
    public void processEnemy()
       {
            for (int ei = 0; ei < SPRITES_NUMBER; ei++)
            {
                Sprite sprite = ap_Sprites[ei];
                if (sprite.lg_SpriteActive)
                {
                    // Movement
                    int dir = sprite.i_Direction;
                    int enemyX = sprite.i_mainX;
                    int enemyY = sprite.i_mainY;

                    switch (dir)
                    {
                        case NORTH:
                            enemyY = sprite.i_mainY - sprite.i_Speed;
                            break;
                        case NORTH_WEST:
                            enemyY = sprite.i_mainY - sprite.i_Speed;
                            enemyX = sprite.i_mainX - sprite.i_Speed;
                            break;
                        case NORTH_EAST:
                            enemyY = sprite.i_mainY - sprite.i_Speed;
                            enemyX = sprite.i_mainX + sprite.i_Speed;
                            break;
                        case SOUTH:
                            enemyY = sprite.i_mainY + sprite.i_Speed;
                            break;
                        case SOUTH_WEST:
                            enemyY = sprite.i_mainY + sprite.i_Speed;
                            enemyX = sprite.i_mainX - sprite.i_Speed;
                            break;
                        case SOUTH_EAST:
                            enemyY = sprite.i_mainY + sprite.i_Speed;
                            enemyX = sprite.i_mainX + sprite.i_Speed;
                            break;
                        case WEST:
                            enemyX = sprite.i_mainX - sprite.i_Speed;
                            break;
                        case EAST:
                            enemyX = sprite.i_mainX + sprite.i_Speed;
                            break;
                    }

                    sprite.setMainPointXY(enemyX, enemyY);
                // Deactivate object that has position out of game screen
                    int obj_y = enemyY >> 8;
                    int obj_x = enemyX >> 8;
                    int obj_w = sprite.i_width >> 8;
                    int obj_h = sprite.i_height >> 8;

                    if (obj_x < 0 - obj_w | obj_x > i_ScreenWidth | obj_y < 0 - obj_h | obj_y > i_ScreenHeight)
                        {
                            sprite.lg_SpriteActive = false;
                        }
                ////////////////////////////////////////////////////////////

                // Enemy fire //////////////////////////////////////////////
                        switch (sprite.i_ObjectType)
                        {
                            case OBJECT_AIR_FIGHTER:
                                if ((horizont & 0x0017) == 0)
                                {
                                    EnemyLaunchMissle(sprite);
                                }
                                break;
                            case OBJECT_AIR_BOMBER:
                                if ((horizont & 0x0017) == 0)
                                {
                                    EnemyLaunchBullet(sprite);
                                }
                                break;
                            case OBJECT_BOSS:

                                 if ( sprite.i_mainY > - sprite.i_height + (sprite.i_height>>2) )
                                 {
                                   if ((horizont & 0x0013) == 0)
                                    {
                                        EnemyBossLaunchBullet(sprite);
                                    }
                                   else
                                   if ((horizont & 0x0017) == 0)
                                    {
                                       EnemyLaunchMissle(sprite);
                                    }
                                 }
                                    if ( sprite.i_mainY >= (MaxPlayerArea_Y - sprite.i_height - ( 4 << 8))  )
                                    {
                                         sprite.i_Direction = (sprite.i_Direction & ~SOUTH) | NORTH;
                                    }
                                    if ( sprite.i_mainY <= 0 )
                                    {
                                         sprite.i_Direction = (sprite.i_Direction & ~NORTH) | SOUTH;
                                    }
                                    if((sprite.i_Direction & (WEST | EAST)) == 0)
                                    {
                                           sprite.i_Direction |= getRandomInt(1)==0 ?WEST : EAST;
                                    }

                                    switch (i_bossStyle)
                                    {
                                        case 5:
                                        case 6:
                                        case 7:
                                        case 8:
                                                if ( sprite.i_mainX + (sprite.i_width / 2) >= (p_PlayerSprite.i_mainX + p_PlayerSprite.i_width)  )
                                                {
                                                     sprite.i_Direction = (sprite.i_Direction & ~EAST) | WEST;
                                                }
                                                if ( sprite.i_mainX + (sprite.i_width >> 1) <= (p_PlayerSprite.i_mainX) )
                                                {
                                                     sprite.i_Direction = (sprite.i_Direction & ~WEST) | EAST;
                                                }
                                                break;

                                        default:
                                                if ( sprite.i_mainX >= (MaxPlayerArea_X + p_PlayerSprite.i_width - sprite.i_width + ( 4 << 8))  )
                                                {
                                                     sprite.i_Direction = (sprite.i_Direction & ~EAST) | WEST;
                                                }
                                                else
                                                if ( sprite.i_mainX <= 0 - ( 4 << 8) )
                                                {
                                                     sprite.i_Direction = (sprite.i_Direction & ~WEST) | EAST;
                                                }
                                                break;
                                    }

                                    ////////////////
                                break;

                            default:
                                break;
                        }
                ////////////////////////////////////////////////////////////

                ////////////////////////////////////////////////////////////
                        int obj_type = sprite.i_ObjectType;
                        int weapon_type;
                        boolean lg_animationCompleted = sprite.processAnimation();
                        switch (obj_type)
                        {
                            case OBJECT_EXPLOSION:
                            case OBJECT_HI_EXPLOSION:
                                if (lg_animationCompleted)
                                {
                                    // Delete explosion
                                    sprite.lg_SpriteActive = false;
                                }
                                break;
                            case OBJECT_BOSS_EXPLOSION:
                                if (lg_animationCompleted)
                                {
                                    // Delete Boss explosion
                                    sprite.lg_SpriteActive = false;
                                }
                                break;
                            case OBJECT_DEADBODY:
                            case OBJECT_DEAD_PLANE:
                            case OBJECT_DEAD_BOMBER:
                            case OBJECT_DEAD_FIGHTER:
                            case OBJECT_DEAD_SPY:
                            case OBJECT_DEAD_PLAYER:
                                if (lg_animationCompleted)
                                {
                                    // Delete dead parachuter
                                    sprite.lg_SpriteActive = false;
                                }
                                break;
                            case OBJECT_BONUS5:
                                if (checkBonusColision(sprite)) {
                                  i_Weapon = WEAPON_SINGLE_BULLET;
                                  p_GameActionListener.gameAction(GAMEACTION_SND_BONUSTAKEN);
                                }
                                break;
                            case OBJECT_BONUS6:
                                if (checkBonusColision(sprite)) {
                                  i_Weapon = WEAPON_DOUBLE_BULLET;
                                  p_GameActionListener.gameAction(GAMEACTION_SND_BONUSTAKEN);
                                }
                                break;
                            case OBJECT_BONUS7:
                                if (checkBonusColision(sprite)) {
                                  i_Weapon = WEAPON_TRIBLE_BULLET;
                                  p_GameActionListener.gameAction(GAMEACTION_SND_BONUSTAKEN);
                                }
                                break;
                            case OBJECT_BONUS8:
                                if (checkBonusColision(sprite)) {
                                  i_Weapon = WEAPON_QUADRA_BULLET;
                                  p_GameActionListener.gameAction(GAMEACTION_SND_BONUSTAKEN);
                                }
                                break;
                            case OBJECT_BONUS9:
                                if (checkBonusColision(sprite)) {
                                  i_Weapon = WEAPON_QUANTA_BULLET;
                                  p_GameActionListener.gameAction(GAMEACTION_SND_BONUSTAKEN);
                                }
                                break;
                            case OBJECT_BONUS10:
                                if (checkBonusColision(sprite)) {
                                  i_Weapon = WEAPON_EXTRA_BULLET;
                                  p_GameActionListener.gameAction(GAMEACTION_SND_BONUSTAKEN);
                                }
                                break;
                            case OBJECT_BONUS11:
                                if (checkBonusColision(sprite)) {
                                  i_playerHealth = i_playerHealthInit;
                                  p_GameActionListener.gameAction(GAMEACTION_SND_BONUSTAKEN);
                                }
                                break;
                            case OBJECT_AIR_PLANE:
                            case OBJECT_AIR_BOMBER:
                            case OBJECT_AIR_FIGHTER:
                                if (p_PlayerSprite.i_ObjectType == OBJECT_PLAYER)
                                {
                                    if (sprite.isCollided(p_PlayerSprite))
                                    {
                                        if ((getRandomInt(16) & 3) == 0)
                                        {
                                            // Make parachuter
                                            EnemyPancake(sprite);
                                        }
                                    ExplodeEnemy(sprite);
                                    if(!lg_immortality)
                                      {
                                         i_playerHealth -= 20;
                                         p_GameActionListener.gameAction(GAMEACTION_SND_PLAYERWOUNDED);
                                      }
                                    }
                                }
                                break;
                            case OBJECT_AIR_SPY:
                                if (p_PlayerSprite.i_ObjectType == OBJECT_PLAYER)
                                {
                                    if (sprite.isCollided(p_PlayerSprite))
                                    {
                                    ExplodeEnemy(sprite);
                                    if(!lg_immortality)
                                      {
                                         i_playerHealth -= 20;
                                         p_GameActionListener.gameAction(GAMEACTION_SND_PLAYERWOUNDED);
                                      }
                                    }
                                }
                                break;
                            case OBJECT_BOSS:
                                if (p_PlayerSprite.i_ObjectType == OBJECT_PLAYER)
                                {
                                    if (sprite.isCollided(p_PlayerSprite))
                                    {
                                      if(!lg_immortality)
                                      {
                                         i_playerHealth -= 20;
                                         p_GameActionListener.gameAction(GAMEACTION_SND_PLAYERWOUNDED);
                                      }
                                    }
                                }
                                break;
                            case OBJECT_E_BULLET1:
                            case OBJECT_E_BULLET2:
                            case OBJECT_E_MISSLE:
                                if (p_PlayerSprite.i_ObjectType == OBJECT_PLAYER)
                                {
                                    if (sprite.isCollided(p_PlayerSprite))
                                    {
                                        ExplodeEnemy(sprite);
                                        if(!lg_immortality)
                                        {
                                           i_playerHealth -= 5;
                                           p_GameActionListener.gameAction(GAMEACTION_SND_PLAYERWOUNDED);
                                        }
                                    }
                                }
                                break;
                            case OBJECT_MISSLE:
                            case OBJECT_BULLET:
                                for (int xi = 0; xi < SPRITES_NUMBER; xi++)
                                    {
                                        Sprite target = ap_Sprites[xi];
                                        if (target.lg_SpriteActive)
                                            {
                                                switch (target.i_ObjectType)
                                                {
                                                    case OBJECT_BONUS3:
                                                    case OBJECT_BONUS4:
                                                        if (sprite.isCollided(target))
                                                        {
                                                            target.i_ObjectType = OBJECT_BOSS;
                                                            ExplodeEnemy(target);
                                                            sprite.lg_SpriteActive = false;
                                                            for (int ij = 0; ij < SPRITES_NUMBER; ij++)
                                                            {
                                                               Sprite sp = ap_Sprites[ij];
                                                               if (sp.lg_SpriteActive)
                                                                 switch (sp.i_ObjectType)
                                                                 {
                                                                    case OBJECT_BOSS:
                                                                           i_bossHealth = Math.max(0, i_bossHealth - 50);
                                                                           break;
                                                                    case OBJECT_BONUS1:
                                                                    case OBJECT_BONUS2:
                                                                    case OBJECT_BULLET:
                                                                    case OBJECT_MISSLE:
                                                                    case OBJECT_E_BULLET1:
                                                                    case OBJECT_E_BULLET2:
                                                                    case OBJECT_E_MISSLE:
                                                                    case OBJECT_EXPLOSION:
                                                                    case OBJECT_HI_EXPLOSION:
                                                                    case OBJECT_BOSS_EXPLOSION:
                                                                           break;
                                                                      default:
                                                                          ExplodeEnemy(sp);
                                                                 }
                                                            }

                                                            i_PlayerScore += 50;
                                                        }
                                                        break;

                                                    case OBJECT_BONUS5:
                                                    case OBJECT_BONUS6:
                                                    case OBJECT_BONUS7:
                                                    case OBJECT_BONUS8:
                                                    case OBJECT_BONUS9:
                                                    case OBJECT_BONUS10:
                                                    case OBJECT_BONUS11:
                                                    case OBJECT_HELICOPTER:
                                                        if (sprite.isCollided(target))
                                                        {
                                                            ExplodeEnemy(target);
                                                            sprite.lg_SpriteActive = false;
                                                            // i_PlayerScore += 5;
                                                        }
                                                        break;
                                                    case OBJECT_PARACHUTER:
                                                        if (sprite.isCollided(target))
                                                        {
                                                            KillParachuterEnemy(target);
                                                            sprite.lg_SpriteActive = false;
                                                            i_PlayerScore += 5;
                                                        }
                                                        break;
                                                    case OBJECT_AIR_PLANE:
                                                        if (sprite.isCollided(target))
                                                        {
                                                            //
                                                            if ((getRandomInt(16) & 3) == 0)
                                                                {
                                                                    EnemyPancake(target);
                                                                }
                                                            //
                                                            ExplodeEnemy(target);
                                                            sprite.lg_SpriteActive = false;
                                                            i_PlayerScore += 5;
                                                        }
                                                        break;
                                                    case OBJECT_AIR_SPY:
                                                        if (sprite.isCollided(target))
                                                        {
                                                            ExplodeEnemy(target);
                                                            sprite.lg_SpriteActive = false;
                                                            i_PlayerScore += 15;
                                                        }
                                                        break;
                                                    case OBJECT_AIR_BOMBER:
                                                        if (sprite.isCollided(target))
                                                        {
                                                            //
                                                            if ((getRandomInt(16) & 3) == 0)
                                                                {
                                                                    EnemyPancake(target);
                                                                }
                                                            //
                                                            ExplodeEnemy(target);
                                                            sprite.lg_SpriteActive = false;
                                                            i_PlayerScore += 10;
                                                        }
                                                        break;
                                                    case OBJECT_AIR_FIGHTER:
                                                        if (sprite.isCollided(target))
                                                        {
                                                            //
                                                            if ((getRandomInt(16) & 3) == 0)
                                                                {
                                                                    EnemyPancake(target);
                                                                }
                                                            //
                                                            ExplodeEnemy(target);
                                                            sprite.lg_SpriteActive = false;
                                                            i_PlayerScore += 20;
                                                        }
                                                        break;
                                                    case OBJECT_BOSS:
                                                        if (sprite.isCollided(target))
                                                        {
                                                            i_bossHealth -= 5;
                                                            if (i_bossHealth < 0)
                                                            {
                                                                horizont = i_bossStyle * BOSS_DISTANCE + 10;
                                                                landscape_time = horizont+10;
                                                                fire_time = horizont;
                                                                born_time = horizont+5;
                                                                ExplodeEnemy(target);
                                                                i_bossmode = false;
                                                                i_boss_active = false;
                                                                i_PlayerScore += 50;
                                                            }
                                                            ExplodeEnemy(sprite);
                                                            i_PlayerScore += 5;
                                                        }
                                                        break;
                                                    default:
                                                        break;
                                                }
                                            }
                                    }
                                break;
                            default:
                                break;
                        }
                ////////////////////////////////////////////////////////////
                }
            }
    }
    private boolean checkBonusColision(Sprite sprite)
    {
       if (p_PlayerSprite.i_ObjectType == OBJECT_PLAYER
           && sprite.isCollided(p_PlayerSprite))
       {
          sprite.i_ObjectType = OBJECT_HELICOPTER;
          sprite.i_Direction |= (sprite.i_mainX + (sprite.i_width>>1) > (i_ScreenWidth<<7))
                                                         ? EAST : WEST;
          sprite.i_Speed = HELICOPTER_AWAY_SPEED;
          return true;
       }
       return false;
    }

    private void launchBullet()
    {
       if (fire_time + fire_freq < horizont & p_PlayerSprite.i_ObjectType == OBJECT_PLAYER)
        {
        Sprite p_bullet = _getFirstInactiveSprite();
        p_GameActionListener.gameAction(GAMEACTION_SND_PLAYERSHOT);

            switch(i_Weapon)
            {
                case WEAPON_DOUBLE_BULLET:
                {
                    p_bullet.i_ObjectType = OBJECT_BULLET;
                    loadStateForSprite(p_bullet, OBJECT_BULLET);
                    p_bullet.setMainPointXY(p_PlayerSprite.i_mainX + (p_PlayerSprite.i_width / 8) - (p_bullet.i_width>>1), p_PlayerSprite.i_mainY);
                    p_bullet.i_Speed = BULLET_SPEED;
                    p_bullet.i_Direction = NORTH;

                    p_bullet = _getFirstInactiveSprite();
                    // Right bullet
                    p_bullet.i_ObjectType = OBJECT_BULLET;
                    loadStateForSprite(p_bullet, OBJECT_BULLET);
                    p_bullet.setMainPointXY(p_PlayerSprite.i_mainX + (p_PlayerSprite.i_width - p_PlayerSprite.i_width / 8) - (p_bullet.i_width>>1) , p_PlayerSprite.i_mainY);
                    p_bullet.i_Speed = BULLET_SPEED;
                    p_bullet.i_Direction = NORTH;

                }
                    break;
                case WEAPON_TRIBLE_BULLET:
                {
                    // Center bullet
                    p_bullet.i_ObjectType = OBJECT_BULLET;
                    loadStateForSprite(p_bullet, OBJECT_BULLET);
                    p_bullet.setMainPointXY(p_PlayerSprite.i_mainX + (p_PlayerSprite.i_width / 2) - (p_bullet.i_width>>1), p_PlayerSprite.i_mainY);
                    p_bullet.i_Speed = BULLET_SPEED;
                    p_bullet.i_Direction = NORTH;

                    p_bullet = _getFirstInactiveSprite();
                    // Left bullet
                    p_bullet.i_ObjectType = OBJECT_BULLET;
                    loadStateForSprite(p_bullet, OBJECT_BULLET);
                    p_bullet.setMainPointXY(p_PlayerSprite.i_mainX + (p_PlayerSprite.i_width / 8) - (p_bullet.i_width>>1), p_PlayerSprite.i_mainY);
                    p_bullet.i_Speed = BULLET_SPEED;
                    p_bullet.i_Direction = NORTH_WEST;

                    p_bullet = _getFirstInactiveSprite();
                    // Right bullet
                    p_bullet.i_ObjectType = OBJECT_BULLET;
                    loadStateForSprite(p_bullet, OBJECT_BULLET);
                    p_bullet.setMainPointXY(p_PlayerSprite.i_mainX + (p_PlayerSprite.i_width - p_PlayerSprite.i_width / 8) - (p_bullet.i_width>>1), p_PlayerSprite.i_mainY);
                    p_bullet.i_Speed = BULLET_SPEED;
                    p_bullet.i_Direction = NORTH_EAST;
                }
                    break;
                case WEAPON_QUADRA_BULLET:
                {
                    // Center missle
                    p_bullet.i_ObjectType = OBJECT_MISSLE;
                    loadStateForSprite(p_bullet, OBJECT_MISSLE);
                    p_bullet.setMainPointXY(p_PlayerSprite.i_mainX + (p_PlayerSprite.i_width/ 2) - (p_bullet.i_width>>1), p_PlayerSprite.i_mainY);
                    p_bullet.i_Speed = MISSLE_SPEED;
                    p_bullet.i_Direction = NORTH;

                    p_bullet = _getFirstInactiveSprite();
                    // Left bullet
                    p_bullet.i_ObjectType = OBJECT_BULLET;
                    loadStateForSprite(p_bullet, OBJECT_BULLET);
                    p_bullet.setMainPointXY(p_PlayerSprite.i_mainX + (p_PlayerSprite.i_width / 8) - (p_bullet.i_width>>1), p_PlayerSprite.i_mainY);
                    p_bullet.i_Speed = BULLET_SPEED;
                    p_bullet.i_Direction = NORTH_WEST;

                    p_bullet = _getFirstInactiveSprite();
                    // Right bullet
                    p_bullet.i_ObjectType = OBJECT_BULLET;
                    loadStateForSprite(p_bullet, OBJECT_BULLET);
                    p_bullet.setMainPointXY(p_PlayerSprite.i_mainX + (p_PlayerSprite.i_width - p_PlayerSprite.i_width / 8) - (p_bullet.i_width>>1), p_PlayerSprite.i_mainY);
                    p_bullet.i_Speed = BULLET_SPEED;
                    p_bullet.i_Direction = NORTH_EAST;

                }
                    break;

                case WEAPON_QUANTA_BULLET:
                {
                    // Center missle
                    p_bullet.i_ObjectType = OBJECT_MISSLE;
                    loadStateForSprite(p_bullet, OBJECT_MISSLE);
                    p_bullet.setMainPointXY(p_PlayerSprite.i_mainX + (p_PlayerSprite.i_width / 2) - (p_bullet.i_width>>1), p_PlayerSprite.i_mainY);
                    p_bullet.i_Speed = MISSLE_SPEED;
                    p_bullet.i_Direction = NORTH;

                    p_bullet = _getFirstInactiveSprite();
                    // Left bullet
                    p_bullet.i_ObjectType = OBJECT_BULLET;
                    loadStateForSprite(p_bullet, OBJECT_BULLET);
                    p_bullet.setMainPointXY(p_PlayerSprite.i_mainX + (p_PlayerSprite.i_width / 8) - (p_bullet.i_width>>1), p_PlayerSprite.i_mainY);
                    p_bullet.i_Speed = BULLET_SPEED;
                    p_bullet.i_Direction = NORTH_WEST;

                    p_bullet = _getFirstInactiveSprite();
                    // Right bullet
                    p_bullet.i_ObjectType = OBJECT_BULLET;
                    loadStateForSprite(p_bullet, OBJECT_BULLET);
                    p_bullet.setMainPointXY(p_PlayerSprite.i_mainX + (p_PlayerSprite.i_width - p_PlayerSprite.i_width / 8) - (p_bullet.i_width>>1), p_PlayerSprite.i_mainY);
                    p_bullet.i_Speed = BULLET_SPEED;
                    p_bullet.i_Direction = NORTH_EAST;

                    p_bullet = _getFirstInactiveSprite();
                    // Back bullet
                    p_bullet.i_ObjectType = OBJECT_BULLET;
                    loadStateForSprite(p_bullet, OBJECT_BULLET);
                    p_bullet.setMainPointXY(p_PlayerSprite.i_mainX + (p_PlayerSprite.i_width / 2) - (p_bullet.i_width>>1), p_PlayerSprite.i_mainY + p_PlayerSprite.i_width);
                    p_bullet.i_Speed = BULLET_SPEED;
                    p_bullet.i_Direction = SOUTH;
                }
                    break;
                case WEAPON_EXTRA_BULLET:
                {
                    // Center bullet
                    p_bullet.i_ObjectType = OBJECT_BULLET;
                    loadStateForSprite(p_bullet, OBJECT_BULLET);
                    p_bullet.setMainPointXY(p_PlayerSprite.i_mainX + (p_PlayerSprite.i_width / 2) - (p_bullet.i_width>>1), p_PlayerSprite.i_mainY);
                    p_bullet.i_Speed = BULLET_SPEED;
                    p_bullet.i_Direction = NORTH;

                    p_bullet = _getFirstInactiveSprite();
                    // Left bullet
                    p_bullet.i_ObjectType = OBJECT_BULLET;
                    loadStateForSprite(p_bullet, OBJECT_BULLET);
                    p_bullet.setMainPointXY(p_PlayerSprite.i_mainX + (p_PlayerSprite.i_width / 8) - (p_bullet.i_width>>1), p_PlayerSprite.i_mainY);
                    p_bullet.i_Speed = BULLET_SPEED;
                    p_bullet.i_Direction = NORTH_WEST;

                    p_bullet = _getFirstInactiveSprite();
                    // Right bullet
                    p_bullet.i_ObjectType = OBJECT_BULLET;
                    loadStateForSprite(p_bullet, OBJECT_BULLET);
                    p_bullet.setMainPointXY(p_PlayerSprite.i_mainX + (p_PlayerSprite.i_width - p_PlayerSprite.i_width / 8) - (p_bullet.i_width>>1), p_PlayerSprite.i_mainY);
                    p_bullet.i_Speed = BULLET_SPEED;
                    p_bullet.i_Direction = NORTH_EAST;

                    p_bullet = _getFirstInactiveSprite();
                    // Left missle
                    p_bullet.i_ObjectType = OBJECT_MISSLE;
                    loadStateForSprite(p_bullet, OBJECT_MISSLE);
                    p_bullet.setMainPointXY(p_PlayerSprite.i_mainX  - (p_bullet.i_width>>1), p_PlayerSprite.i_mainY);
                    p_bullet.i_Speed = MISSLE_SPEED;
                    p_bullet.i_Direction = NORTH;
                    p_bullet = _getFirstInactiveSprite();

                    // Right missle
                    p_bullet.i_ObjectType = OBJECT_MISSLE;
                    loadStateForSprite(p_bullet, OBJECT_MISSLE);
                    p_bullet.setMainPointXY(p_PlayerSprite.i_mainX + p_PlayerSprite.i_width  - (p_bullet.i_width>>1), p_PlayerSprite.i_mainY);
                    p_bullet.i_Speed = MISSLE_SPEED;
                    p_bullet.i_Direction = NORTH;
                }
                    break;
                default:
                // case WEAPON_SINGLE_BULLET)
                {
                    p_bullet.i_ObjectType = OBJECT_BULLET;
                    loadStateForSprite(p_bullet, OBJECT_BULLET);
                    p_bullet.setMainPointXY(p_PlayerSprite.i_mainX + (p_PlayerSprite.i_width>>1) - (p_bullet.i_width>>1), p_PlayerSprite.i_mainY);
                    p_bullet.i_Speed = BULLET_SPEED;
                    p_bullet.i_Direction = NORTH;
                }
            }

            fire_time = horizont;
        }
    }

    private void launchMissle()
    {
        if (fire_time + player_missle_freq < horizont & p_PlayerSprite.i_ObjectType == OBJECT_PLAYER)
        {
            Sprite p_bullet = _getFirstInactiveSprite();

            p_bullet.i_ObjectType = OBJECT_MISSLE;
            p_bullet.setMainPointXY(p_PlayerSprite.i_mainX + (PLAYER_WIDTH / 2 << 8), p_PlayerSprite.i_mainY);
            p_bullet.i_Speed = MISSLE_SPEED;
            p_bullet.i_Direction = NORTH;
            loadStateForSprite(p_bullet, OBJECT_MISSLE);
            p_GameActionListener.gameAction(GAMEACTION_SND_PLAYERSHOT);
            fire_time = horizont;
        }
    }

    private void EnemyPancake(Sprite _object)
    {
            Sprite p_parachuter = _getFirstInactiveSprite();

            p_parachuter.i_ObjectType = OBJECT_PARACHUTER;
            loadStateForSprite(p_parachuter, OBJECT_PARACHUTER);
            p_parachuter.setMainPointXY(_object.i_mainX + (_object.i_width / 2) - (p_parachuter.i_width / 2), _object.i_mainY - p_parachuter.i_height );
            p_parachuter.i_Speed = PARACHUTER_SPEED;
            p_parachuter.i_Direction = SOUTH;
    }

    private void EnemyLaunchMissle(Sprite _object)
    {
            Sprite p_missle = _getFirstInactiveSprite();

            p_missle.i_ObjectType = OBJECT_E_MISSLE;
            loadStateForSprite(p_missle, OBJECT_E_MISSLE);
            p_missle.setMainPointXY(_object.i_mainX + (_object.i_width / 2) - (p_missle.i_width>>1), _object.i_mainY + _object.i_height - p_missle.i_height );
            p_missle.i_Speed = MISSLE_SPEED;
            p_missle.i_Direction = SOUTH;
    }

    private void EnemyLaunchBullet(Sprite _object)
    {
            Sprite p_bullet = _getFirstInactiveSprite();

            p_bullet.i_ObjectType = OBJECT_E_BULLET1;
            loadStateForSprite(p_bullet, OBJECT_E_BULLET1);
            p_bullet.setMainPointXY(_object.i_mainX + (_object.i_width / 2) - (p_bullet.i_width>>1), _object.i_mainY + _object.i_height - p_bullet.i_height );
            p_bullet.i_Speed = BULLET_SPEED;
            p_bullet.i_Direction = _object.i_Direction;
    }

    private void EnemyBossLaunchBullet(Sprite _object)
    {
            Sprite p_bullet = _getFirstInactiveSprite();
            boolean g_switch = (horizont & 0x08 ) == 0;;

            // Centre gun
            p_bullet.i_ObjectType = OBJECT_E_BULLET2;
            loadStateForSprite(p_bullet, OBJECT_E_BULLET2);
            p_bullet.setMainPointXY(_object.i_mainX + (_object.i_width / 2), _object.i_mainY + _object.i_height - p_bullet.i_height );
            p_bullet.i_Speed = BULLET_SPEED;
            p_bullet.i_Direction = SOUTH;

            p_bullet = _getFirstInactiveSprite();
            // Left gun1
            if (g_switch)
            {
            p_bullet.i_ObjectType = OBJECT_E_BULLET2;
            loadStateForSprite(p_bullet, OBJECT_E_BULLET2);
            p_bullet.setMainPointXY(_object.i_mainX + (_object.i_width / 2) - (_object.i_width / 8), _object.i_mainY + _object.i_height - p_bullet.i_height );
            p_bullet.i_Speed = BULLET_SPEED;
            p_bullet.i_Direction = SOUTH;
            }
            else
            {
            // p_bullet = _getFirstInactiveSprite();

            // Left gun2
            p_bullet.i_ObjectType = OBJECT_E_BULLET2;
            loadStateForSprite(p_bullet, OBJECT_E_BULLET2);
            p_bullet.setMainPointXY(_object.i_mainX + (_object.i_width / 8), _object.i_mainY + _object.i_height - p_bullet.i_height );
            p_bullet.i_Speed = BULLET_SPEED;
            p_bullet.i_Direction = SOUTH;
            }

            p_bullet = _getFirstInactiveSprite();
            // Right gun1
            if (g_switch)
            {
            p_bullet.i_ObjectType = OBJECT_E_BULLET2;
            loadStateForSprite(p_bullet, OBJECT_E_BULLET2);
            p_bullet.setMainPointXY(_object.i_mainX + (_object.i_width / 2) + (_object.i_width / 8), _object.i_mainY + _object.i_height - p_bullet.i_height );
            p_bullet.i_Speed = BULLET_SPEED;
            p_bullet.i_Direction = SOUTH;
            }
            else
            {
            // p_bullet = _getFirstInactiveSprite();
            // Right gun2
            p_bullet.i_ObjectType = OBJECT_E_BULLET2;
            loadStateForSprite(p_bullet, OBJECT_E_BULLET2);
            p_bullet.setMainPointXY(_object.i_mainX + _object.i_width - (_object.i_width / 8), _object.i_mainY + _object.i_height - p_bullet.i_height );
            p_bullet.i_Speed = BULLET_SPEED;
            p_bullet.i_Direction = SOUTH;
            }
    }


    private void ExplodeEnemy(Sprite _object)
    {
            int oldX = _object.i_mainX + _object.i_width / 2;
            int oldY = _object.i_mainY + _object.i_height / 2;
            if (_object.i_ObjectType == OBJECT_BOSS)
            {
                _object.i_ObjectType = OBJECT_BOSS_EXPLOSION;
                loadStateForSprite(_object, OBJECT_BOSS_EXPLOSION);
                p_GameActionListener.gameAction(GAMEACTION_SND_HUGEEXPLOSION);
                _object.setMainPointXY( oldX - (_object.i_width / 2), oldY - (_object.i_height / 2) );
                _object.i_Speed = EXPLOSION_SPEED;
                _object.i_Direction = SOUTH;
            }
            else
            {

  int dir = SOUTH;
                switch(_object.i_ObjectType)
                {
                    case OBJECT_AIR_PLANE:
                        _object.i_ObjectType = OBJECT_DEAD_PLANE;
                        break;
                    case OBJECT_AIR_BOMBER:
                        _object.i_ObjectType = OBJECT_DEAD_BOMBER;
                        break;
                    case OBJECT_AIR_FIGHTER:
                        _object.i_ObjectType = OBJECT_DEAD_FIGHTER;
                        break;
                    case OBJECT_AIR_SPY:
                        _object.i_ObjectType = OBJECT_DEAD_SPY;
                        dir = _object.i_Direction;
                        break;
                    case OBJECT_PLAYER:
                        _object.i_ObjectType = OBJECT_EXPLOSION;
                        dir = _object.i_Direction;

                        Sprite _obj = _getFirstInactiveSprite();
                        _obj.i_ObjectType = OBJECT_DEAD_PLAYER;
                        loadStateForSprite(_obj, OBJECT_DEAD_PLAYER);
                        _obj.setMainPointXY( oldX - (_object.i_width / 2), oldY - (_object.i_height / 2) );
                        _obj.i_Speed = EXPLOSION_SPEED;
                        _obj.i_Direction = dir;

                        break;
                    default:
                        _object.i_ObjectType = OBJECT_HI_EXPLOSION;
                        break;
                }
                loadStateForSprite(_object, _object.i_ObjectType);
                _object.setMainPointXY( oldX - (_object.i_width / 2), oldY - (_object.i_height / 2) );
                _object.i_Speed = EXPLOSION_SPEED;
                _object.i_Direction = dir;

                p_GameActionListener.gameAction(GAMEACTION_SND_SMALLEXPLOSION);

                if (_object.i_ObjectType != OBJECT_HI_EXPLOSION )
                {
                  _object = _getFirstInactiveSprite();

                  _object.i_ObjectType = OBJECT_EXPLOSION;
                  loadStateForSprite(_object, OBJECT_EXPLOSION);
                  _object.setMainPointXY( oldX - (_object.i_width / 2), oldY - (_object.i_height / 2) );
                  _object.i_Speed = EXPLOSION_SPEED;
                  _object.i_Direction = SOUTH;
                }

            }
    }

    private void KillParachuterEnemy(Sprite _object)
    {
            int oldX = _object.i_mainX + _object.i_width / 2;
            int oldY = _object.i_mainY + _object.i_height / 2;
            _object.i_ObjectType = OBJECT_DEADBODY;
            loadStateForSprite(_object, OBJECT_DEADBODY);
            p_GameActionListener.gameAction(GAMEACTION_SND_PARADEAD);
            _object.setMainPointXY( oldX - (_object.i_width / 2), oldY - (_object.i_height / 2) );
    }
    private int SelectWeapon(int weapon)
    {
        if (i_PlayerScore > EXTRA_AVAILABLE_SCORE) return WEAPON_EXTRA_BULLET;
        if (i_PlayerScore > QUANTA_AVAILABLE_SCORE) return WEAPON_QUANTA_BULLET;
        if (i_PlayerScore > QUADRA_AVAILABLE_SCORE) return WEAPON_QUADRA_BULLET;
        if (i_PlayerScore > TRIBLE_AVAILABLE_SCORE) return WEAPON_TRIBLE_BULLET;
        if (i_PlayerScore > HEALTH_AVAILABLE_SCORE) return HEALTH_BONUS;
        if (i_PlayerScore > DOUBLE_AVAILABLE_SCORE) return WEAPON_DOUBLE_BULLET;
        return WEAPON_SINGLE_BULLET;
    }

    private void generateCloud()
    {
            Sprite p_bonus = _getFirstInactiveSprite();

            int rnd2 = getRandomInt(4);
            if(getRandomInt(8) == 1)
            {
                    p_bonus.i_ObjectType = OBJECT_CLOUD;
                    loadStateForSprite(p_bonus, OBJECT_CLOUD);
                    p_bonus.setMainPointXY((i_ScreenWidth << 6) * (rnd2) - (p_bonus.i_width / 2), 0x0100 - p_bonus.i_height );
                    p_bonus.i_Speed = CLOUD_SPEED;
                    p_bonus.i_Direction = SOUTH;
            }
    }

    private void MakeRndBonus()
    {
            Sprite p_bonus = _getFirstInactiveSprite();

            int rnd = getRandomInt(8);
            int rnd2 = getRandomInt(4);

            switch(rnd)
            {
                case 7:
                    p_bonus.i_ObjectType = OBJECT_BONUS1;
                    loadStateForSprite(p_bonus, OBJECT_BONUS1);
                    p_GameActionListener.gameAction(GAMEACTION_SND_BIRDSCRY);
                    p_bonus.setMainPointXY((i_ScreenWidth - 1 << 8), (i_ScreenWidth << 6) * (rnd2) - (p_bonus.i_height / 2) );
                    p_bonus.i_Speed = BONUS1_SPEED;
                    p_bonus.i_Direction = SOUTH_WEST;
                    break;
                case 5:
                    p_bonus.i_ObjectType = OBJECT_BONUS2;
                    loadStateForSprite(p_bonus, OBJECT_BONUS1);
                    p_GameActionListener.gameAction(GAMEACTION_SND_BIRDSCRY);
                    p_bonus.setMainPointXY(0x0100 - p_bonus.i_width, (i_ScreenWidth << 6) * (rnd2) - (p_bonus.i_height / 2) );
                    p_bonus.i_Speed = BONUS1_SPEED;
                    p_bonus.i_Direction = SOUTH_EAST;
                    break;
                case 3:
                    if(!i_bossmode || i_bossStyle < 5)
                    {
                      p_bonus.i_ObjectType = OBJECT_BONUS3;
                      loadStateForSprite(p_bonus, OBJECT_BONUS1);
                      p_bonus.setMainPointXY((i_ScreenWidth - 1 << 8), (i_ScreenWidth << 6) * (rnd2) - (p_bonus.i_height / 2) );
                      p_bonus.i_Speed = BONUS1_SPEED;
                      p_bonus.i_Direction = NORTH_WEST;
                    }
                    break;
               case 1:
                    if(!i_bossmode || i_bossStyle < 5)
                    {
                      p_bonus.i_ObjectType = OBJECT_BONUS4;
                      loadStateForSprite(p_bonus, OBJECT_BONUS1);
                      p_bonus.setMainPointXY(0x0100 - p_bonus.i_width, (i_ScreenWidth << 6) * (rnd2) - (p_bonus.i_height / 2) );
                      p_bonus.i_Speed = BONUS1_SPEED;
                      p_bonus.i_Direction = NORTH_EAST;
                    }
                    break;
                case 6:
                case 2:
                    break;
//                case 4:
                default:
                    switch(getRandomInt(SelectWeapon(WEAPON_EXTRA_BULLET)))
                    {
                        case WEAPON_DOUBLE_BULLET:
                            p_bonus.i_ObjectType = OBJECT_BONUS6; // Double
                            break;
                        case HEALTH_BONUS:
                            p_bonus.i_ObjectType = OBJECT_BONUS11; // HEALTH
                            break;
                        case WEAPON_TRIBLE_BULLET:
                            p_bonus.i_ObjectType = OBJECT_BONUS7; // Trible
                            break;
                        case WEAPON_QUADRA_BULLET:
                            p_bonus.i_ObjectType = OBJECT_BONUS8; // Quadra
                            break;
                        case WEAPON_QUANTA_BULLET:
                            p_bonus.i_ObjectType = OBJECT_BONUS9; // Quanta
                            break;
                        case WEAPON_EXTRA_BULLET:
                            p_bonus.i_ObjectType = OBJECT_BONUS10; // Extra
                            break;
                        default:
                            p_bonus.i_ObjectType = OBJECT_BONUS5; // Single
                            break;
                    }
                    loadStateForSprite(p_bonus, p_bonus.i_ObjectType);
                    p_GameActionListener.gameAction(GAMEACTION_SND_HELYAPPEARS);
                    int x = p_bonus.i_width >> 2;
                    p_bonus.setMainPointXY(((i_ScreenWidth << 6) - x) * (rnd2), 0x0100 - p_bonus.i_height );
                    p_bonus.i_Speed = BONUSA_SPEED;
                    p_bonus.i_Direction = SOUTH;
                    break;
            }
    }

public GameletImpl(int _screenWidth, int _screenHeight, startup _gameActionListener, String _staticArrayResource)
    {
 super(_screenWidth, _screenHeight, _gameActionListener, _staticArrayResource);
        i8_Init_Player_X = (_screenWidth - PLAYER_WIDTH) << 7;
        i8_Init_Player_Y = (_screenHeight) << 8;

    }

    public String getGameID()
    {
        return "PlaneWAR";
    }

    public int getMaxSizeOfSavedGameBlock()
    {
        return 1200; //30 + MAX_LANDSCAPE * 15 + SPRITES_NUMBER * 15;
    }

    public void newGameSession(int _level)
    {
        super.initLevel(_level);

        ap_Sprites = new Sprite[SPRITES_NUMBER];
        ap_Landscape = new Sprite[MAX_LANDSCAPE];

        horizont = 0; // Reset game position
        landscape_time = 0;
        landscape_freq = 40;

        fire_time = 0;           // Horizont position when player launched missle or bullet
        fire_freq = 7;           // Current player fire frequency
        player_missle_freq = 17; // Current player missle frequency
        born_time = 0;           // Horizont position where was born last enemy
        born_freq = 50;          // Current enemy born frequency

        i_Weapon = WEAPON_SINGLE_BULLET;

        i_bossStyle = BOSS_STYLE_NULL;

        i_bossmode = false;
        i_boss_active = false;
        i_bossHealth = BOSS_HEALTH;


        for (int li = 0; li < SPRITES_NUMBER; li++) ap_Sprites[li] = new Sprite(li);
        for (int li = 0; li < MAX_LANDSCAPE; li++)  ap_Landscape[li] = new Sprite(li);

        int level = 0;

        switch (_level)
        {
            case GAMELEVEL_EASY:
                {
                    i_GameTimeDelay = GAMELEVEL_EASY_TIMEDELAY;
                    i_Attemptions = GAMELEVEL_EASY_ATTEMPTIONS;
                    i_playerHealthInit = GAMELEVEL_EASY_PLAYERHEALTH;

                }
                ;
                break;
            case GAMELEVEL_NORMAL:
                {
                    i_GameTimeDelay = GAMELEVEL_NORMAL_TIMEDELAY;
                    i_Attemptions = GAMELEVEL_NORMAL_ATTEMPTIONS;
                    i_playerHealthInit = GAMELEVEL_NORMAL_PLAYERHEALTH;
                    level = 1;
                }
                ;
                break;
            case GAMELEVEL_HARD:
                {
                    i_GameTimeDelay = GAMELEVEL_HARD_TIMEDELAY;
                    i_Attemptions = GAMELEVEL_HARD_ATTEMPTIONS;
                    i_playerHealthInit = GAMELEVEL_HARD_PLAYERHEALTH;
                    level = 2;
                }
                ;
                break;
        }

        SINGLE_AVAILABLE_SCORE = WEAPON_SCORES[ WEAPON_SINGLE_BULLET * 3 + level];
        DOUBLE_AVAILABLE_SCORE = WEAPON_SCORES[ WEAPON_DOUBLE_BULLET * 3 + level];
        HEALTH_AVAILABLE_SCORE = WEAPON_SCORES[ HEALTH_BONUS         * 3 + level];
        TRIBLE_AVAILABLE_SCORE = WEAPON_SCORES[ WEAPON_TRIBLE_BULLET * 3 + level];
        QUADRA_AVAILABLE_SCORE = WEAPON_SCORES[ WEAPON_QUADRA_BULLET * 3 + level];
        QUANTA_AVAILABLE_SCORE = WEAPON_SCORES[ WEAPON_QUANTA_BULLET * 3 + level];
        EXTRA_AVAILABLE_SCORE  = WEAPON_SCORES[ WEAPON_EXTRA_BULLET  * 3 + level];


        MinPlayerArea_X = (0 + PLAYER_AREA_BORDER) << 8;
        MaxPlayerArea_X = (i_ScreenWidth - PLAYER_AREA_BORDER - PLAYER_WIDTH) << 8;
        MinPlayerArea_Y = (0 + PLAYER_AREA_BORDER) << 8;
        MaxPlayerArea_Y = (i_ScreenHeight - PLAYER_AREA_BORDER - PLAYER_HEIGHT - BOTTOM_GAP) << 8;

        p_PlayerSprite = new Sprite(0);

        // Initing of the player
        p_PlayerSprite.i_ObjectType = OBJECT_PLAYER;
        p_PlayerSprite.setMainPointXY(i8_Init_Player_X, i8_Init_Player_Y);
        loadStateForSprite(p_PlayerSprite, OBJECT_PLAYER);
        i8_player_HorzSpeed = 0;
        i8_player_VertSpeed = 0;

        _deactivateAllSprites(false);

        resumeGameAfterPlayerLost();
        lg_immortality = false;
    }

    public void endGameSession()
    {
        if(ap_Sprites != null) for (int li = 0; li < SPRITES_NUMBER; li++) ap_Sprites[li] = null;
        ap_Sprites = null;
        if(ap_Landscape != null)for (int li = 0; li < MAX_LANDSCAPE; li++) ap_Landscape[li] = null;
        ap_Landscape = null;
        super.endGameSession();
    }

    public void resumeGameAfterPlayerLost()
    {
        i_PlayerKey = PLAYER_BUTTON_NONE;
        i_PlayerState = PLAYERSTATE_NORMAL;
        i_GameState = GAMESTATE_PLAYED;
        i_Weapon = WEAPON_SINGLE_BULLET;
        i_playerHealth = i_playerHealthInit;
        p_PlayerSprite.i_ObjectType = OBJECT_PLAYER;
        p_PlayerSprite.i_Direction = NORTH;
        p_PlayerSprite.setMainPointXY(i8_Init_Player_X, i8_Init_Player_Y);
        loadStateForSprite(p_PlayerSprite, OBJECT_PLAYER);
        lg_justStarted = true;
        lg_immortality = true;
        i_immortality_timeleft = IMMORTALITY_TIMEDELAY;
        p_GameActionListener.gameAction(GAMEACTION_SND_PLAYERAPPEARED);
    }

    public int getPlayerScore()
    {
        return (i_PlayerScore);
    }

    public void nextGameStep(Object _playermoveobject)
    {

        horizont++; // Increase game turn counter
        if ((horizont & 0x002F) == 0) {generateCloud();}

        boolean lg_animationCompleted = p_PlayerSprite.processAnimation();
        if (p_PlayerSprite.i_ObjectType != OBJECT_PLAYER)
        {
                  if (lg_animationCompleted)
                  {
                     p_PlayerSprite.lg_SpriteActive = false;

                     if (i_Attemptions <= 0)
                     {
                      if(i_immortality_timeleft-- <=0)
                      {
                        i_PlayerState = PLAYERSTATE_LOST;
                        i_GameState = GAMESTATE_OVER;
                      }
                     }
                      else
                         i_PlayerState = PLAYERSTATE_LOST;

                  }
        }
        else
        if (horizont > BOSS_STYLE_EIGHT * BOSS_DISTANCE + 30 && !i_bossmode)
        {
              // leading to top of screen
              int i_t_mainX = p_PlayerSprite.i_mainX; // i8_player_HorzSpeed = 0;
              int i_t_mainY = p_PlayerSprite.i_mainY; // i8_player_VertSpeed = 0;

              if(i_t_mainY+p_PlayerSprite.i_height>=MinPlayerArea_Y)
              {
                if (lg_justStarted && !lg_immortality)
                {
                 p_PlayerSprite.setMainPointXY(i_t_mainX, i_t_mainY-i8_player_VertSpeed);
                 i8_player_VertSpeed += I8_PLAYER_HORZSPEED_INCREASE>>1;
                }
                else
                {
                  int i8_StartPointX = i_ScreenWidth<<7;    // 1/2 of screen width
                  int i8_StartPointY = i_ScreenHeight*3<<6; // 3/4 of screen height

                  int dX = i8_StartPointX - i_t_mainX - (p_PlayerSprite.i_width>>1);
                  int dY = i8_StartPointY - i_t_mainY;

                  int abs_dX = Math.abs(dX);
                  int abs_dY = Math.abs(dY);

                  if((abs_dX >> 9) != 0 || (abs_dY >> 9) != 0)
                  {
                    i8_player_HorzSpeed = dX * I8_PLAYER_MAX_VERTSPEED / (abs_dY+1) % I8_PLAYER_MAX_HORZSPEED;
                    i8_player_VertSpeed = dY * I8_PLAYER_MAX_HORZSPEED / (abs_dX+1) % (I8_PLAYER_MAX_HORZSPEED<<1);
                    p_PlayerSprite.setMainPointXY(i_t_mainX + i8_player_HorzSpeed, i_t_mainY + i8_player_VertSpeed);
                  }
                  else
                  {
                    i8_player_VertSpeed =0;
                    i8_player_HorzSpeed =0;
                    lg_justStarted = true;
                    lg_immortality = false;
                  }
                }
              }
              else
              {
                i_GameState = GAMESTATE_OVER;
                i_PlayerState = PLAYERSTATE_WON;
              }
           generateLandscape();
           processEnemy();
           processLandscape();
           return;
        }
        else
         if (i_playerHealth <= 0)
         {
                i_Attemptions--;
                i8_player_HorzSpeed = 0;
                i8_player_VertSpeed = 0;
                ExplodeEnemy(p_PlayerSprite);
                p_GameActionListener.gameAction(GAMEACTION_SND_PLAYERDEAD);
                i_immortality_timeleft = GAMEOVER_TIMEDELAY;
         }

        // If Player is not dead then ...
        if (p_PlayerSprite.i_ObjectType == OBJECT_PLAYER)
        {
            if ((horizont & 0x007F) == 0) {MakeRndBonus();}
            if ((horizont & BOSS_DISTANCE) == 0 && i_bossmode == false)
            {
                  if(i_bossStyle < BOSS_STYLE_EIGHT)
                      i_bossStyle++;  //2047
                  i_bossmode = true;
                  p_GameActionListener.gameAction(GAMEACTION_SND_BOSSAPPEARS);
            }

             if (lg_justStarted)
             {
                p_PlayerSprite.setMainPointXY(p_PlayerSprite.i_mainX, p_PlayerSprite.i_mainY - I8_PLAYER_MAX_VERTSPEED);
                if(p_PlayerSprite.i_mainY < MaxPlayerArea_Y)
                {
                    lg_justStarted = false;
                }
             }
              else
                  {
                    if(lg_immortality)
                    {
                       i_immortality_timeleft--;
                       if(i_immortality_timeleft <= 0) lg_immortality = false;
                    }
                    processPlayer();
                  }
        }

        generateEnemy();
        generateLandscape();
        processEnemy();
        processLandscape();
    }

    private void processPlayer()
    {
            if ((i_PlayerKey & PLAYER_BUTTON_DOWN) != 0)
            {
                if (i8_player_VertSpeed < I8_PLAYER_MAX_VERTSPEED)
                {
                    i8_player_VertSpeed += I8_PLAYER_VERTSPEED_INCREASE;
                }
            }

            if ((i_PlayerKey & PLAYER_BUTTON_TOP) != 0)
            {
                if (i8_player_VertSpeed > (0 - I8_PLAYER_MAX_VERTSPEED) )
                {
                    i8_player_VertSpeed -= I8_PLAYER_VERTSPEED_DECREASE;
                }
            }

            if ((i_PlayerKey & PLAYER_BUTTON_RIGHT) != 0)
            {
                if (i8_player_HorzSpeed < I8_PLAYER_MAX_HORZSPEED)
                {
                    i8_player_HorzSpeed += I8_PLAYER_HORZSPEED_INCREASE;
                }
            }

            if ((i_PlayerKey & PLAYER_BUTTON_LEFT) != 0)
            {
                if (i8_player_HorzSpeed > (0 - I8_PLAYER_MAX_HORZSPEED) )
                {
                    i8_player_HorzSpeed -= I8_PLAYER_HORZSPEED_DECREASE;
                }
            }

            if ((i_PlayerKey & PLAYER_BUTTON_MISSLEFIRE) != 0)
            {
                launchMissle();
            }

            if ((i_PlayerKey & PLAYER_BUTTON_BULLETFIRE) != 0)
            {
                launchBullet();
            }
            int i_t_mainX = p_PlayerSprite.i_mainX + i8_player_HorzSpeed; // i8_player_HorzSpeed = 0;
            int i_t_mainY = p_PlayerSprite.i_mainY + i8_player_VertSpeed; // i8_player_VertSpeed = 0;


            if ((i_PlayerKey & (PLAYER_BUTTON_RIGHT | PLAYER_BUTTON_LEFT | PLAYER_BUTTON_TOP | PLAYER_BUTTON_DOWN)) == 0)
            {
                if (i8_player_HorzSpeed != 0)
                    {
                        if (i8_player_HorzSpeed > 0) {i8_player_HorzSpeed -= I8_PLAYER_HORZSPEED_DECREASE;}
                        else                         {i8_player_HorzSpeed += I8_PLAYER_HORZSPEED_DECREASE;}
                    }
                if (i8_player_VertSpeed != 0)
                    {
                        if (i8_player_VertSpeed > 0) {i8_player_VertSpeed -= I8_PLAYER_VERTSPEED_DECREASE;}
                        else                         {i8_player_VertSpeed += I8_PLAYER_VERTSPEED_DECREASE;}
                    }
            }

            if  (i_t_mainX < MinPlayerArea_X) { i_t_mainX = MinPlayerArea_X; i8_player_HorzSpeed = 0; }
            if  (i_t_mainX > MaxPlayerArea_X) { i_t_mainX = MaxPlayerArea_X; i8_player_HorzSpeed = 0; }
            if  (i_t_mainY < MinPlayerArea_Y) { i_t_mainY = MinPlayerArea_Y; i8_player_VertSpeed = 0; }
            if  (i_t_mainY > MaxPlayerArea_Y) { i_t_mainY = MaxPlayerArea_Y; i8_player_VertSpeed = 0; }

            p_PlayerSprite.setMainPointXY(i_t_mainX, i_t_mainY);
    }


    private void loadStateForSprite(Sprite _sprite, int _state)
       {
           int i_offset = _sprite.i_ObjectType * 10;
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

       //Sprite defenition array
       public static final short[] ai_SpriteDefinitionArray = new short[]
       {
           // Width, Height, OffX, OffY, OffWdth, OffHght, Frames, Delay, Animation type, Anchor
           //
           // PLAYER
           (PLAYER_WIDTH << 8) * SCALEFACTOR,   (PLAYER_HEIGHT << 8) * SCALEFACTOR, (PLAYER_OFS_X << 8) * SCALEFACTOR, 0x000 * SCALEFACTOR, (PLAYER_INT_W << 8) * SCALEFACTOR, (PLAYER_HEIGHT << 8) * SCALEFACTOR,    1, 1, Sprite.ANIMATION_FROZEN, Sprite.SPRITE_ALIGN_TOP | Sprite.SPRITE_ALIGN_LEFT,
           // MISSLE
           (MISSLE_W << 8) * SCALEFACTOR,       (MISSLE_H << 8) * SCALEFACTOR,      0x000 * SCALEFACTOR, 0x000 * SCALEFACTOR, (MISSLE_W << 8) * SCALEFACTOR,     (MISSLE_H << 8) * SCALEFACTOR,         1, 1, Sprite.ANIMATION_FROZEN, Sprite.SPRITE_ALIGN_TOP | Sprite.SPRITE_ALIGN_LEFT,
           // BULLET
           (BULLET_W << 8) * SCALEFACTOR,       (BULLET_H << 8) * SCALEFACTOR,      0x000 * SCALEFACTOR, 0x000 * SCALEFACTOR, (BULLET_W << 8) * SCALEFACTOR,     (BULLET_H << 8) * SCALEFACTOR,         1, 1, Sprite.ANIMATION_FROZEN, Sprite.SPRITE_ALIGN_TOP | Sprite.SPRITE_ALIGN_LEFT,
           // AIR_FIGHTER
           (AIR_FIGHTER_W << 8) * SCALEFACTOR,  (AIR_FIGHTER_H << 8) * SCALEFACTOR, 0x000 * SCALEFACTOR, 0x000 * SCALEFACTOR, (AIR_FIGHTER_W << 8) * SCALEFACTOR, (AIR_FIGHTER_H << 8) * SCALEFACTOR,   1, 1, Sprite.ANIMATION_FROZEN, Sprite.SPRITE_ALIGN_TOP | Sprite.SPRITE_ALIGN_LEFT,
           // AIR_BOMBER
           (AIR_BOMBER_W << 8) * SCALEFACTOR,   (AIR_BOMBER_H << 8) * SCALEFACTOR,  0x000 * SCALEFACTOR, 0x000 * SCALEFACTOR, (AIR_BOMBER_W << 8) * SCALEFACTOR,  (AIR_BOMBER_H << 8) * SCALEFACTOR,    1, 1, Sprite.ANIMATION_FROZEN, Sprite.SPRITE_ALIGN_TOP | Sprite.SPRITE_ALIGN_LEFT,
           // AIR_PLANE                                                                                                                                                                                 //5
           (AIR_PLANE_W << 8) * SCALEFACTOR,    (AIR_PLANE_H << 8) * SCALEFACTOR,   0x000 * SCALEFACTOR, 0x000 * SCALEFACTOR, (AIR_PLANE_W << 8) * SCALEFACTOR,   (AIR_PLANE_H << 8) * SCALEFACTOR,     1, 1, Sprite.ANIMATION_FROZEN, Sprite.SPRITE_ALIGN_TOP | Sprite.SPRITE_ALIGN_LEFT,
           // AIR_SPY
           (AIR_SPY_W << 8) * SCALEFACTOR,      (AIR_SPY_H << 8) * SCALEFACTOR,     0x000 * SCALEFACTOR, 0x000 * SCALEFACTOR, (AIR_SPY_W << 8) * SCALEFACTOR,     (AIR_SPY_H << 8) * SCALEFACTOR,       1, 1, Sprite.ANIMATION_FROZEN, Sprite.SPRITE_ALIGN_TOP | Sprite.SPRITE_ALIGN_LEFT,
           // E_BULLET1
           (E_BULLET1_W << 8) * SCALEFACTOR,    (E_BULLET1_H << 8) * SCALEFACTOR,   0x000 * SCALEFACTOR, 0x000 * SCALEFACTOR, (E_BULLET1_W << 8) * SCALEFACTOR,   (E_BULLET1_H << 8) * SCALEFACTOR,     1, 1, Sprite.ANIMATION_FROZEN, Sprite.SPRITE_ALIGN_TOP | Sprite.SPRITE_ALIGN_LEFT,
           // E_BULLET2
           (E_BULLET2_W << 8) * SCALEFACTOR,    (E_BULLET2_H << 8) * SCALEFACTOR,   0x000 * SCALEFACTOR, 0x000 * SCALEFACTOR, (E_BULLET2_W << 8) * SCALEFACTOR,   (E_BULLET2_H << 8) * SCALEFACTOR,     1, 1, Sprite.ANIMATION_FROZEN, Sprite.SPRITE_ALIGN_TOP | Sprite.SPRITE_ALIGN_LEFT,
           // E_MISSLE
           (E_MISSLE_W << 8) * SCALEFACTOR,     (E_MISSLE_H << 8) * SCALEFACTOR,    0x000 * SCALEFACTOR, 0x000 * SCALEFACTOR, (E_MISSLE_W << 8) * SCALEFACTOR,   (E_MISSLE_H << 8) * SCALEFACTOR,       1, 1, Sprite.ANIMATION_FROZEN, Sprite.SPRITE_ALIGN_TOP | Sprite.SPRITE_ALIGN_LEFT,
           // EXPLOSION                                                                                                                                                                                 //10
           (EXPLOSION_W << 8) * SCALEFACTOR,    (EXPLOSION_H << 8) * SCALEFACTOR,   0x000 * SCALEFACTOR, 0x000 * SCALEFACTOR, (EXPLOSION_W << 8) * SCALEFACTOR,  (EXPLOSION_H << 8) * SCALEFACTOR,      7, 1, Sprite.ANIMATION_FROZEN, Sprite.SPRITE_ALIGN_TOP | Sprite.SPRITE_ALIGN_LEFT,
           // BONUS1 BIRDS
           (BONUS1_W << 8) * SCALEFACTOR,       (BONUS1_H << 8) *  SCALEFACTOR,     0x000 * SCALEFACTOR, 0x000 * SCALEFACTOR, (BONUS1_W << 8) * SCALEFACTOR,     (BONUS1_H << 8) * SCALEFACTOR,         2, 2, Sprite.ANIMATION_CYCLIC, Sprite.SPRITE_ALIGN_TOP | Sprite.SPRITE_ALIGN_LEFT,
           // BONUS2 BIRDS
           (BONUS2_W << 8) * SCALEFACTOR,       (BONUS2_H << 8) *  SCALEFACTOR,     0x000 * SCALEFACTOR, 0x000 * SCALEFACTOR, (BONUS2_W << 8) * SCALEFACTOR,     (BONUS2_H << 8) * SCALEFACTOR,         2, 2, Sprite.ANIMATION_CYCLIC, Sprite.SPRITE_ALIGN_TOP | Sprite.SPRITE_ALIGN_LEFT,
           // BONUS3 BIRDS
           (BONUS3_W << 8) * SCALEFACTOR,       (BONUS3_H << 8) *  SCALEFACTOR,     0x000 * SCALEFACTOR, 0x000 * SCALEFACTOR, (BONUS3_W << 8) * SCALEFACTOR,     (BONUS3_H << 8) * SCALEFACTOR,         1, 1, Sprite.ANIMATION_FROZEN, Sprite.SPRITE_ALIGN_TOP | Sprite.SPRITE_ALIGN_LEFT,
           // BONUS4 BIRDS
           (BONUS4_W << 8) * SCALEFACTOR,       (BONUS4_H << 8) *  SCALEFACTOR,     0x000 * SCALEFACTOR, 0x000 * SCALEFACTOR, (BONUS4_W << 8) * SCALEFACTOR,     (BONUS4_H << 8) * SCALEFACTOR,         1, 1, Sprite.ANIMATION_FROZEN, Sprite.SPRITE_ALIGN_TOP | Sprite.SPRITE_ALIGN_LEFT,
           // BONUS5 WEAPON SINGLE                                                                                                                                                                      //15
           (BONUSA_W << 8) * SCALEFACTOR,       (BONUSA_H << 8) *  SCALEFACTOR,     (BONUSA_OFS_X << 8) * SCALEFACTOR, (BONUSA_OFS_Y << 8) * SCALEFACTOR, (BONUSA_INT_W << 8) * SCALEFACTOR,     (BONUSA_INT_H << 8) * SCALEFACTOR,         2, 1, Sprite.ANIMATION_CYCLIC, Sprite.SPRITE_ALIGN_TOP | Sprite.SPRITE_ALIGN_LEFT,
           // BONUS6 WEAPON DOUBLE
           (BONUSA_W << 8) * SCALEFACTOR,       (BONUSA_H << 8) *  SCALEFACTOR,     (BONUSA_OFS_X << 8) * SCALEFACTOR, (BONUSA_OFS_Y << 8) * SCALEFACTOR, (BONUSA_INT_W << 8) * SCALEFACTOR,     (BONUSA_INT_H << 8) * SCALEFACTOR,         2, 1, Sprite.ANIMATION_CYCLIC, Sprite.SPRITE_ALIGN_TOP | Sprite.SPRITE_ALIGN_LEFT,
           // BONUS7 WEAPON TRIBLE
           (BONUSA_W << 8) * SCALEFACTOR,       (BONUSA_H << 8) *  SCALEFACTOR,     (BONUSA_OFS_X << 8) * SCALEFACTOR, (BONUSA_OFS_Y << 8) * SCALEFACTOR, (BONUSA_INT_W << 8) * SCALEFACTOR,     (BONUSA_INT_H << 8) * SCALEFACTOR,         2, 1, Sprite.ANIMATION_CYCLIC, Sprite.SPRITE_ALIGN_TOP | Sprite.SPRITE_ALIGN_LEFT,
           // BONUS8 WEAPON QUADRA
           (BONUSA_W << 8) * SCALEFACTOR,       (BONUSA_H << 8) *  SCALEFACTOR,     (BONUSA_OFS_X << 8) * SCALEFACTOR, (BONUSA_OFS_Y << 8) * SCALEFACTOR, (BONUSA_INT_W << 8) * SCALEFACTOR,     (BONUSA_INT_H << 8) * SCALEFACTOR,         2, 1, Sprite.ANIMATION_CYCLIC, Sprite.SPRITE_ALIGN_TOP | Sprite.SPRITE_ALIGN_LEFT,
           // BONUS9 WEAPON QUANTA
           (BONUSA_W << 8) * SCALEFACTOR,       (BONUSA_H << 8) *  SCALEFACTOR,     (BONUSA_OFS_X << 8) * SCALEFACTOR, (BONUSA_OFS_Y << 8) * SCALEFACTOR, (BONUSA_INT_W << 8) * SCALEFACTOR,     (BONUSA_INT_H << 8) * SCALEFACTOR,         2, 1, Sprite.ANIMATION_CYCLIC, Sprite.SPRITE_ALIGN_TOP | Sprite.SPRITE_ALIGN_LEFT,
           // BONUS10 WEAPON EXTRA
           (BONUSA_W << 8) * SCALEFACTOR,       (BONUSA_H << 8) *  SCALEFACTOR,     (BONUSA_OFS_X << 8) * SCALEFACTOR, (BONUSA_OFS_Y << 8) * SCALEFACTOR, (BONUSA_INT_W << 8) * SCALEFACTOR,     (BONUSA_INT_H << 8) * SCALEFACTOR,         2, 1, Sprite.ANIMATION_CYCLIC, Sprite.SPRITE_ALIGN_TOP | Sprite.SPRITE_ALIGN_LEFT,
           // BONUS11 HEALTH
           (BONUSA_W << 8) * SCALEFACTOR,       (BONUSA_H << 8) *  SCALEFACTOR,     (BONUSA_OFS_X << 8) * SCALEFACTOR, (BONUSA_OFS_Y << 8) * SCALEFACTOR, (BONUSA_INT_W << 8) * SCALEFACTOR,     (BONUSA_INT_H << 8) * SCALEFACTOR,         2, 1, Sprite.ANIMATION_CYCLIC, Sprite.SPRITE_ALIGN_TOP | Sprite.SPRITE_ALIGN_LEFT,

           // PARACHUTER
           (PARACHUTER_W << 8) * SCALEFACTOR,   (PARACHUTER_H << 8) * SCALEFACTOR,  0x000 * SCALEFACTOR, 0x000 * SCALEFACTOR, (PARACHUTER_W << 8) * SCALEFACTOR, (PARACHUTER_H << 8) * SCALEFACTOR,     6, 2, Sprite.ANIMATION_FROZEN, Sprite.SPRITE_ALIGN_TOP | Sprite.SPRITE_ALIGN_LEFT,
           // DEADBODY PARACHUTER
           (DEADBODY_W << 8) * SCALEFACTOR,     (DEADBODY_H << 8) * SCALEFACTOR,    0x000 * SCALEFACTOR, 0x000 * SCALEFACTOR, (DEADBODY_W << 8) * SCALEFACTOR,   (DEADBODY_H << 8) * SCALEFACTOR,       8, 1, Sprite.ANIMATION_FROZEN, Sprite.SPRITE_ALIGN_TOP | Sprite.SPRITE_ALIGN_LEFT,
           // CLOUD
           (CLOUD_W << 8) * SCALEFACTOR,        (CLOUD_H << 8) * SCALEFACTOR,       0x000 * SCALEFACTOR, 0x000 * SCALEFACTOR, (CLOUD_W << 8) * SCALEFACTOR,      (CLOUD_H << 8) * SCALEFACTOR,          1, 1, Sprite.ANIMATION_FROZEN, Sprite.SPRITE_ALIGN_TOP | Sprite.SPRITE_ALIGN_LEFT,

           // LANDSCAPE OBJECTS
           // ISLAND1
           (ISLAND1_W << 8) * SCALEFACTOR,      (ISLAND1_H << 8) * SCALEFACTOR,     0x000 * SCALEFACTOR, 0x000 * SCALEFACTOR, (ISLAND1_W << 8) * SCALEFACTOR,    (ISLAND1_H << 8) * SCALEFACTOR,        1, 1, Sprite.ANIMATION_FROZEN, Sprite.SPRITE_ALIGN_TOP | Sprite.SPRITE_ALIGN_LEFT,
           (ISLAND2_W << 8) * SCALEFACTOR,      (ISLAND2_H << 8) * SCALEFACTOR,     0x000 * SCALEFACTOR, 0x000 * SCALEFACTOR, (ISLAND2_W << 8) * SCALEFACTOR,    (ISLAND2_H << 8) * SCALEFACTOR,        1, 1, Sprite.ANIMATION_FROZEN, Sprite.SPRITE_ALIGN_TOP | Sprite.SPRITE_ALIGN_LEFT,
           (ISLAND3_W << 8) * SCALEFACTOR,      (ISLAND3_H << 8) * SCALEFACTOR,     0x000 * SCALEFACTOR, 0x000 * SCALEFACTOR, (ISLAND3_W << 8) * SCALEFACTOR,    (ISLAND3_H << 8) * SCALEFACTOR,        1, 1, Sprite.ANIMATION_FROZEN, Sprite.SPRITE_ALIGN_TOP | Sprite.SPRITE_ALIGN_LEFT,
           (STONE1_W << 8) * SCALEFACTOR,       (STONE1_H << 8) * SCALEFACTOR,      0x000 * SCALEFACTOR, 0x000 * SCALEFACTOR, (STONE1_W << 8) * SCALEFACTOR,     (STONE1_H << 8) * SCALEFACTOR,         1, 1, Sprite.ANIMATION_FROZEN, Sprite.SPRITE_ALIGN_TOP | Sprite.SPRITE_ALIGN_LEFT,
           (STONE2_W << 8) * SCALEFACTOR,       (STONE2_H << 8) * SCALEFACTOR,      0x000 * SCALEFACTOR, 0x000 * SCALEFACTOR, (STONE2_W << 8) * SCALEFACTOR,     (STONE2_H << 8) * SCALEFACTOR,         1, 1, Sprite.ANIMATION_FROZEN, Sprite.SPRITE_ALIGN_TOP | Sprite.SPRITE_ALIGN_LEFT,
           (STONE3_W << 8) * SCALEFACTOR,       (STONE3_H << 8) * SCALEFACTOR,      0x000 * SCALEFACTOR, 0x000 * SCALEFACTOR, (STONE3_W << 8) * SCALEFACTOR,     (STONE3_H << 8) * SCALEFACTOR,         1, 1, Sprite.ANIMATION_FROZEN, Sprite.SPRITE_ALIGN_TOP | Sprite.SPRITE_ALIGN_LEFT,

           // BOSS
           (BOSS_W << 8) * SCALEFACTOR,         (BOSS_H << 8) * SCALEFACTOR,        0x000 * SCALEFACTOR, (BOSS_OFS_Y << 8) * SCALEFACTOR, (BOSS_W << 8) * SCALEFACTOR,       (BOSS_INT_H << 8) * SCALEFACTOR,           4, 1, Sprite.ANIMATION_CYCLIC, Sprite.SPRITE_ALIGN_TOP | Sprite.SPRITE_ALIGN_LEFT,
           // BOSS EXPLOSION
           (BOSS_EXPLOSION_W << 8) * SCALEFACTOR, (BOSS_EXPLOSION_H << 8) * SCALEFACTOR, 0x000 * SCALEFACTOR, 0x000 * SCALEFACTOR, (BOSS_EXPLOSION_W << 8) * SCALEFACTOR, (BOSS_EXPLOSION_H << 8) * SCALEFACTOR, 9, 2, Sprite.ANIMATION_FROZEN, Sprite.SPRITE_ALIGN_TOP | Sprite.SPRITE_ALIGN_LEFT,
           // AIR_PLANE
           (AIR_PLANE_W << 8) * SCALEFACTOR,    (AIR_PLANE_H << 8) * SCALEFACTOR,   0x000 * SCALEFACTOR, 0x000 * SCALEFACTOR, (AIR_PLANE_W << 8) * SCALEFACTOR,   (AIR_PLANE_H << 8) * SCALEFACTOR,     8, 2, Sprite.ANIMATION_FROZEN, Sprite.SPRITE_ALIGN_TOP | Sprite.SPRITE_ALIGN_LEFT,
           // AIR_BOMBER
           (AIR_BOMBER_W << 8) * SCALEFACTOR,   (AIR_BOMBER_H << 8) * SCALEFACTOR,  0x000 * SCALEFACTOR, 0x000 * SCALEFACTOR, (AIR_BOMBER_W << 8) * SCALEFACTOR,  (AIR_BOMBER_H << 8) * SCALEFACTOR,    8, 2, Sprite.ANIMATION_FROZEN, Sprite.SPRITE_ALIGN_TOP | Sprite.SPRITE_ALIGN_LEFT,
           // AIR_FIGHTER
           (AIR_FIGHTER_W << 8) * SCALEFACTOR,  (AIR_FIGHTER_H << 8) * SCALEFACTOR, 0x000 * SCALEFACTOR, 0x000 * SCALEFACTOR, (AIR_FIGHTER_W << 8) * SCALEFACTOR, (AIR_FIGHTER_H << 8) * SCALEFACTOR,   7, 2, Sprite.ANIMATION_FROZEN, Sprite.SPRITE_ALIGN_TOP | Sprite.SPRITE_ALIGN_LEFT,
           // AIR_SPY
           (AIR_SPY_W << 8) * SCALEFACTOR,      (AIR_SPY_H << 8) * SCALEFACTOR,     0x000 * SCALEFACTOR, 0x000 * SCALEFACTOR, (AIR_SPY_W << 8) * SCALEFACTOR,     (AIR_SPY_H << 8) * SCALEFACTOR,       6, 2, Sprite.ANIMATION_FROZEN, Sprite.SPRITE_ALIGN_TOP | Sprite.SPRITE_ALIGN_LEFT,
           // DEAD PLAYER
           (PLAYER_WIDTH << 8) * SCALEFACTOR,   (PLAYER_HEIGHT << 8) * SCALEFACTOR, 0x000 * SCALEFACTOR, 0x000 * SCALEFACTOR, (PLAYER_WIDTH << 8) * SCALEFACTOR, (PLAYER_HEIGHT << 8) * SCALEFACTOR,    8, 2, Sprite.ANIMATION_FROZEN, Sprite.SPRITE_ALIGN_TOP | Sprite.SPRITE_ALIGN_LEFT,
           // HIGH EXPLOSION                                                                                                                                                                                 //10
           (EXPLOSION_W << 8) * SCALEFACTOR,    (EXPLOSION_H << 8) * SCALEFACTOR,   0x000 * SCALEFACTOR, 0x000 * SCALEFACTOR, (EXPLOSION_W << 8) * SCALEFACTOR,  (EXPLOSION_H << 8) * SCALEFACTOR,      7, 1, Sprite.ANIMATION_FROZEN, Sprite.SPRITE_ALIGN_TOP | Sprite.SPRITE_ALIGN_LEFT,
           // BONUSxx - NO WEAPON
           (BONUSA_W << 8) * SCALEFACTOR,       (BONUSA_H << 8) *  SCALEFACTOR,     (BONUSA_OFS_X << 8) * SCALEFACTOR, (BONUSA_OFS_Y << 8) * SCALEFACTOR, (BONUSA_INT_W << 8) * SCALEFACTOR,     (BONUSA_INT_H << 8) * SCALEFACTOR,         2, 1, Sprite.ANIMATION_CYCLIC, Sprite.SPRITE_ALIGN_TOP | Sprite.SPRITE_ALIGN_LEFT,
       };

//////////////////////////////////////////////////////////////////////////////////////////////////

    public void readFromStream(DataInputStream _dataInputStream) throws IOException
    {
        horizont = _dataInputStream.readUnsignedShort();

        i_Attemptions = _dataInputStream.readByte();
        i_playerHealth = _dataInputStream.readShort();
        i_immortality_timeleft = _dataInputStream.readUnsignedShort();

        lg_immortality = _dataInputStream.readBoolean();
        lg_justStarted = _dataInputStream.readBoolean();

        i_Weapon = _dataInputStream.readByte();
        i_bossStyle = _dataInputStream.readUnsignedByte();
        i_bossHealth = _dataInputStream.readShort();
        i_bossmode = _dataInputStream.readBoolean();
        i_boss_active = _dataInputStream.readBoolean();
        i_bossInitHealth = BOSS_HEALTH + i_bossStyle*BOSS_HEALTH_INC;

        landscape_time = _dataInputStream.readUnsignedShort();
        landscape_freq = _dataInputStream.readUnsignedByte();
        fire_time = _dataInputStream.readUnsignedShort();
        fire_freq = _dataInputStream.readUnsignedByte();
        player_missle_freq = _dataInputStream.readUnsignedByte();
        born_time = _dataInputStream.readUnsignedShort();
        born_freq = _dataInputStream.readUnsignedByte();


        if(p_PlayerSprite == null) p_PlayerSprite = new Sprite(0);
        p_PlayerSprite.i_ObjectType = _dataInputStream.readUnsignedByte();
        loadStateForSprite(p_PlayerSprite,p_PlayerSprite.i_ObjectType);
        p_PlayerSprite.readSpriteFromStream(_dataInputStream);

        if(ap_Sprites == null) ap_Sprites = new Sprite[SPRITES_NUMBER];
        if(ap_Landscape == null) ap_Landscape = new Sprite[MAX_LANDSCAPE];

        Sprite sp;

        for(int li=0;li<SPRITES_NUMBER;li++)
        {
             sp = ap_Sprites[li];
             if(sp == null) ap_Sprites[li] = (sp = new Sprite(li));
             sp.i_ObjectType = _dataInputStream.readUnsignedByte();
             loadStateForSprite(sp,sp.i_ObjectType);
             sp.readSpriteFromStream(_dataInputStream);
        }
        for(int li=0;li<MAX_LANDSCAPE;li++)
        {
             sp = ap_Landscape[li];
             if(sp == null) ap_Landscape[li] = (sp = new Sprite(li));
             sp.i_ObjectType = _dataInputStream.readUnsignedByte();
             loadStateForSprite(sp,sp.i_ObjectType);
             sp.readSpriteFromStream(_dataInputStream);
        }
    }

    public void writeToStream(DataOutputStream _dataOutputStream) throws IOException
    {
        _dataOutputStream.writeShort(horizont);

        _dataOutputStream.writeByte(i_Attemptions);
        _dataOutputStream.writeShort(i_playerHealth);
        _dataOutputStream.writeShort(i_immortality_timeleft);

        _dataOutputStream.writeBoolean(lg_immortality);
        _dataOutputStream.writeBoolean(lg_justStarted);

        _dataOutputStream.writeByte(i_Weapon);
        _dataOutputStream.writeByte(i_bossStyle);
        _dataOutputStream.writeShort(i_bossHealth);
        _dataOutputStream.writeBoolean(i_bossmode);
        _dataOutputStream.writeBoolean(i_boss_active);

        _dataOutputStream.writeShort(landscape_time);
        _dataOutputStream.writeByte(landscape_freq);
        _dataOutputStream.writeShort(fire_time);
        _dataOutputStream.writeByte(fire_freq);
        _dataOutputStream.writeByte(player_missle_freq);
        _dataOutputStream.writeShort(born_time);
        _dataOutputStream.writeByte(born_freq);

        _dataOutputStream.writeByte(p_PlayerSprite.i_ObjectType);
        p_PlayerSprite.writeSpriteToStream(_dataOutputStream);

        for(int li=0;li<SPRITES_NUMBER;li++)
        {
             _dataOutputStream.writeByte(ap_Sprites[li].i_ObjectType);
             ap_Sprites[li].writeSpriteToStream(_dataOutputStream);
        }
        for(int li=0;li<MAX_LANDSCAPE;li++)
        {
             _dataOutputStream.writeByte(ap_Landscape[li].i_ObjectType);
             ap_Landscape[li].writeSpriteToStream(_dataOutputStream);
        }
    }
}
