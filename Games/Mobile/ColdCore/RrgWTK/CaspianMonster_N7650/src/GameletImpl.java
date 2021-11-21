
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.DataInputStream;

public class GameletImpl extends Gamelet
{
    public static final int VIRTUALCELLWIDTH = 4;
    public static final int VIRTUALCELLHEIGHT = 4;

    // Game level definitions

    // The level number
    public static final int LEVEL0 = 0;
    // The timedelay for the level
    public static final int LEVEL0_TIMEDELAY = 80;
    // The level of activity of the artillery
    public static final int LEVEL0_ARTILLERYACTIVITY = 25;
    // The level of activity of the air bomers
    public static final int LEVEL0_AIRACTIVITY = 70;
    // The attemption number
    public static final int LEVEL0_ATTEMPTIONS = 4;

    public static final int LEVEL1 = 1;
    public static final int LEVEL1_TIMEDELAY = 80;
    public static final int LEVEL1_ARTILLERYACTIVITY = 15;
    public static final int LEVEL1_AIRACTIVITY = 60;
    public static final int LEVEL1_ATTEMPTIONS = 3;

    public static final int LEVEL2 = 2;
    public static final int LEVEL2_TIMEDELAY = 80;
    public static final int LEVEL2_ARTILLERYACTIVITY = 10;
    public static final int LEVEL2_AIRACTIVITY = 40;
    public static final int LEVEL2_ATTEMPTIONS = 2;

    // GAME ACTIONS
    //======================================
    public static final int GAMEACTION_SND_EARTHEXPLOSION = 0;
    public static final int GAMEACTION_SND_WATEREXPLOSION = 1;
    public static final int GAMEACTION_SND_PLAYERNURSLAUNCH = 2;
    public static final int GAMEACTION_SND_PLAYERSMARTLAUNCH = 3;
    public static final int GAMEACTION_SND_PLAYERFIRE = 4;
    public static final int GAMEACTION_SND_PLAYERHIT = 5;
    public static final int GAMEACTION_SND_PLAYERDEATH = 6;
    public static final int GAMEACTION_SND_PLAYERREADY = 7;
    public static final int GAMEACTION_SND_AIRBOMBERREADY = 8;
    public static final int GAMEACTION_SND_AIRBOMBERDROP = 9;
    public static final int GAMEACTION_SND_BRIDGEDESTROYED = 10;
    public static final int GAMEACTION_SND_SHIPDESTROYED = 11;
    public static final int GAMEACTION_SND_ENEMYHIT = 12;

    // Inside game variables
    //=============================
    private int i_ActivityAir;
    private int i_ActivityArtillery;

    public int i_playerPower;
    public int i_PlayerKey;
    public int i_nonKilledDelay;
    private boolean lg_playerEntering;
    private boolean lg_playerFinishing;
    private int i8_playerEnteringBorder;

    public int i8_backgroundOffsetStart,i8_backgroundOffsetEnd;

    private byte[][] abb_WayPartsArray;
    private byte[] ab_linkPartsArray;

    private int i_curInactiveSplashIndex;
    private int i_curSplashDelay;

    private int i8_lastLineAnalysed;

    private int i_autofiredelay;
    private int i_delayToSuperRocket;

    // Player driving keys
    //==============================
    public static final int PLAYER_BUTTON_NONE = 0;
    public static final int PLAYER_BUTTON_LEFT = 1;
    public static final int PLAYER_BUTTON_RIGHT = 2;
    public static final int PLAYER_BUTTON_UP = 4;
    public static final int PLAYER_BUTTON_DOWN = 8;
    public static final int PLAYER_BUTTON_FIREGUN = 16;
    public static final int PLAYER_BUTTON_FIREROCKET = 32;

    // Game constants
    //==================================
    public static final int INITPLAYERFORCE = 100;
    public static final int WATERMINEFORCE = 30;

    public static final int MAXIMUMACTIVEOBJECTSONSCREEN = 40;
    public static final int MAXIMUMENEMYSHELLS = 15;
    public static final int MAXIMUMSPLASHES = 10;
    public static final int MAXIMUMPLAYERSHELLS = 10;

    public static final int I8_BACKGROUNDSPEED = 0x200; //0x300
    public static final int I8_PLAYERENTERINGSPEED = I8_BACKGROUNDSPEED << 1;
    public static final int I8_PLAYERVERTSPEED = 0x150;
    public static final int I8_PLAYERHORZSPEED = 0x200;

    private static final int NONKILLEDDELAY = 30;
    private static final int SPLASHDELAY = 3;

    private static final int TANKSPEED = 0x100;
    private static final int BOMBERSPEED = I8_BACKGROUNDSPEED << 1;

    private static final int AUTOFIREDELAY = 5;

    private static final int USERSHELLPOWER = 1;
    private static final int TANKPOWER = 3;
    private static final int ARTILLERY1POWER = 2;
    private static final int ARTILLERY2POWER = 4;
    private static final int RADARPOWER = 2;
    private static final int AIRBOMBERPOWER = 3;
    private static final int LINEARSHIPPOWER = 5;
    private static final int WATERMINEPOWER = 1;

    private static final int PLAYERSHELLSPEED = I8_BACKGROUNDSPEED+0x300;
    private static final int ENEMYSHELLSPEED = 0x300;
    private static final int ENEMYSHELLPOWER = 6;
    private static final int ENEMYSHELLTTL = 43; // = 35 + 1/4
    private static final int INITPLAYERPOWER = 100;

    private static final int SMARTROCKETSPEED = 0x400;
    private static final int SMARTROCKETINERTION = 3;
    private static final int DELAYTOSUPERROCKET = 7;

    private static final int NURSROCKETSPEED = (I8_PLAYERVERTSPEED) << 2;

    private static final int USERSHELLTTL = ((208 * 2/3) << 8) / PLAYERSHELLSPEED ;
    private static final int NURSTTL = (((300) * 2/3) << 8) / NURSROCKETSPEED ;
                                   // 300px - long enough distance for crossing over a diagonal of the screen
/*

    private static final int [] ANGLEDEVIATION = {-65 *32/180 +1,
                                                  -44 *32/180 -1,
                                                  -25 *32/180,
                                                    0 *32/180,
                                                   25 *32/180,
                                                   44 *32/180 +1,
                                                   65 *32/180 -1};
*/
    /*                  Vx = Sine(a) * ObjectSpeed   Vy = CoSine(a) * ObjectSpeed */
    private final static int[][] NURS_VELOCITIES ={
                       {-213 * NURSROCKETSPEED >> 8, -142 * NURSROCKETSPEED >> 8},
                       {-181 * NURSROCKETSPEED >> 8, -181 * NURSROCKETSPEED >> 8},
                       { -98 * NURSROCKETSPEED >> 8, -237 * NURSROCKETSPEED >> 8},
                       {   0 * NURSROCKETSPEED >> 8, -256 * NURSROCKETSPEED >> 8},
                       {  98 * NURSROCKETSPEED >> 8, -237 * NURSROCKETSPEED >> 8},
                       { 181 * NURSROCKETSPEED >> 8, -181 * NURSROCKETSPEED >> 8},
                       { 213 * NURSROCKETSPEED >> 8, -142 * NURSROCKETSPEED >> 8}
                                                  };

    private final static int[][] SHELL_VELOCITIES ={
                       {-213 * PLAYERSHELLSPEED >> 8, -142 * PLAYERSHELLSPEED >> 8},
                       {-181 * PLAYERSHELLSPEED >> 8, -181 * PLAYERSHELLSPEED >> 8},
                       { -98 * PLAYERSHELLSPEED >> 8, -237 * PLAYERSHELLSPEED >> 8},
                       {   0 * PLAYERSHELLSPEED >> 8, -256 * PLAYERSHELLSPEED >> 8},
                       {  98 * PLAYERSHELLSPEED >> 8, -237 * PLAYERSHELLSPEED >> 8},
                       { 181 * PLAYERSHELLSPEED >> 8, -181 * PLAYERSHELLSPEED >> 8},
                       { 213 * PLAYERSHELLSPEED >> 8, -142 * PLAYERSHELLSPEED >> 8}
                                                  };









    //Game dynamic object constants
    //================================

    // PLAYER
    public static final int PLAYER_NORMAL = 0;
    public static final int PLAYER_NORMAL_SPRITES_NUM = 7;
    private static final short[][] FIREPOINTS = {{0x400,0x400},{0x700,0x200},{0xB00,0x0}
                                                ,{0xF00,0x0},{0x1300,0x0},{0x1700,0x200}
                                                ,{0x1B00,0x300}};

    public static final int PLAYER_EXPLOSION = 70;
    public static final int PLAYER_EXPLOSION_SPRITES_NUM = 4;

    // EARTH EXPLOSION
    public static final int EARTHEXPLOSION_NORMAL = 110;
    public static final int EARTHEXPLOSION_SPRITES_NUM = 1;

    // WATER EXPLOSION
    public static final int WATEREXPLOSION_NORMAL = 120;
    public static final int WATEREXPLOSION_SPRITES_NUM = 1;

    // SPLASH
    public static final int SPLASH_NORMAL = 130;
    public static final int SPLASH_SPRITES_NUM = 1;

//-------------------------ARTILLERY1------------------------------
    // ARTILLERY1
    public static final int ARTILLERY1_NORMAL = 140;
    public static final int ARTILLERY1_SPRITES_NUM = 1;

    // ARTILLERY1 GUN
    public static final int ARTILLERY1GUN_NORMAL = 150;
    public static final int ARTILLERY1GUN_SPRITES_NUM = 8;

    // ARTILLERY1 DESTROYED
    public static final int ARTILLERY1_DESTROYED = 230;
    public static final int ARTILLERY1_DESTROYED_SPRITES_NUM = 1;
//-------------------------ARTILLERY2------------------------------
    // ARTILLERY2
    public static final int ARTILLERY2_NORMAL = 240;
    public static final int ARTILLERY2_SPRITES_NUM = 1;

    // ARTILLERY2 GUN
    public static final int ARTILLERY2GUN_NORMAL = 250;
    public static final int ARTILLERY2GUN_SPRITES_NUM = 8;


    // ARTILLERY2 DESTROYED
    public static final int ARTILLERY2_DESTROYED = 330;
    public static final int ARTILLERY2_DESTROYED_SPRITES_NUM = 1;

    // TANKLEFT
    public static final int TANKLEFT_NORMAL = 340;
    public static final int TANKLEFT_NORMAL_SPRITES_NUM = 1;

    // TANKLEFTDESTROYED
    public static final int TANKLEFT_DESTROYED = 350;
    public static final int TANKLEFT_DESTROYED_SPRITES_NUM = 1;

    // TANKRIGHT
    public static final int TANKRIGHT_NORMAL = 360;
    public static final int TANKRIGHT_NORMAL_SPRITES_NUM = 1;

    // TANKRIGHT DESTROYED
    public static final int TANKRIGHT_DESTROYED = 370;
    public static final int TANKRIGHT_DESTROYED_SPRITES_NUM = 1;

    // TANK GUN
    public static final int TANKGUN_NORMAL = 380;
    public static final int TANKGUN_SPRITES_NUM = 8;

    // WATERMINE
    public static final int WATERMINE_NORMAL = 460;
    public static final int WATERMINE_SPRITES_NUM = 1;

    // RADAR
    public static final int RADAR_NORMAL = 470;
    public static final int RADAR_SPRITES_NUM = 1;

    // RADAR DESTROYED
    public static final int RADAR_DESTROYED = 480;
    public static final int RADAR_DESTROYED_SPRITES_NUM = 1;

    // BOMBER
    public static final int BOMBER_NORMAL = 490;
    public static final int BOMBER_SPRITES_NUM = 1;

    // BOMBER DROP
    public static final int BOMBER_DROP = 500;
    public static final int BOMBER_DROP_SPRITES_NUM = 1;

    // LINEARSHIP
    public static final int LINEARSHIP_NORMAL = 510;
    public static final int LINEARSHIP_NORMAL_SPRITES_NUM = 1;
    public static final int LINEARSHIP_DESTROYED = 520;
    public static final int LINEARSHIP_DESTROYED_SPRITES_NUM = 1;

    // PLAYER SHELL
    public static final int PLAYERSHELL_NORMAL = 530;
    public static final int PLAYERSHELL_NORMAL_SPRITES_NUM = 1;

    // PLAYER NURS ROCKET
    public static final int NURS_NORMAL = 540;
    public static final int NURS_NORMAL_SPRITES_NUM = 7;

    // INVISIBLE CORRIDOR
    public static final int INVISIBLECORRIDOR_NORMAL = 610;
    public static final int INVISIBLECORRIDOR_NORMAL_SPRITES_NUM = 1;

    // ENEMY SHELL
    public static final int ENEMYSHELL_NORMAL = 620;
    public static final int ENEMYSHELL_NORMAL_SPRITES_NUM = 1;

    private static final int I8_SPEEDCOEFF = I8_PLAYERHORZSPEED / (PLAYER_NORMAL_SPRITES_NUM >> 1);

    // PLAYER SMART ROCKET
    public static final int SMARTROCKET_NORMAL = 630;
    public static final int SMARTROCKET_NORMAL_SPRITES_NUM = 7;

    // SMALL EXPLOSION
    public static final int SMALLEXPLOSION_NORMAL = 710;
    public static final int SMALLEXPLOSION_SPRITES_NUM = 1;


    // Sprite table
    private int[] ai_SpriteTable = new int[]
    {
        // width, height, AreaXOffset, AreaYOffset, AreaWidth, AreaHeight, Frames, Delay, Animation type, Anchor

        //PLAYER NORMAL
        // FRAME0
        0x1F00, 0x1B00, 0x0700, 0x0500, 0x1000, 0x1000, 1, 3, Sprite.ANIMATION_FROZEN, Sprite.SPRITE_ALIGN_CENTER,
        // FRAME1
        0x1F00, 0x1B00, 0x0700, 0x0500, 0x1000, 0x1000, 1, 3, Sprite.ANIMATION_FROZEN, Sprite.SPRITE_ALIGN_CENTER,
        // FRAME2
        0x1F00, 0x1B00, 0x0700, 0x0500, 0x1000, 0x1000, 1, 3, Sprite.ANIMATION_FROZEN, Sprite.SPRITE_ALIGN_CENTER,
        // FRAME3
        0x1F00, 0x1B00, 0x0700, 0x0500, 0x1000, 0x1000, 1, 3, Sprite.ANIMATION_FROZEN, Sprite.SPRITE_ALIGN_CENTER,
        // FRAME4
        0x1F00, 0x1B00, 0x0700, 0x0500, 0x1000, 0x1000, 1, 3, Sprite.ANIMATION_FROZEN, Sprite.SPRITE_ALIGN_CENTER,
        // FRAME5
        0x1F00, 0x1B00, 0x0700, 0x0500, 0x1000, 0x1000, 1, 3, Sprite.ANIMATION_FROZEN, Sprite.SPRITE_ALIGN_CENTER,
        // FRAME6
        0x1F00, 0x1B00, 0x0700, 0x0500, 0x1000, 0x1000, 1, 3, Sprite.ANIMATION_FROZEN, Sprite.SPRITE_ALIGN_CENTER,

        // PLAYER EXPLOSION
        // FRAME0
        0x2200, 0x2200, 0, 0, 0x2200, 0x2200, 1, 3, Sprite.ANIMATION_FROZEN, Sprite.SPRITE_ALIGN_CENTER,
        // FRAME1
        0x2200, 0x2200, 0, 0, 0x2200, 0x2200, 1, 3, Sprite.ANIMATION_FROZEN, Sprite.SPRITE_ALIGN_CENTER,
        // FRAME2
        0x2200, 0x2200, 0, 0, 0x2200, 0x2200, 1, 3, Sprite.ANIMATION_FROZEN, Sprite.SPRITE_ALIGN_CENTER,
        // FRAME3
        0x2200, 0x2200, 0, 0, 0x2000, 0x2200, 1, 3, Sprite.ANIMATION_FROZEN, Sprite.SPRITE_ALIGN_CENTER,

        // EARTH EXPLOSION
        // FRAME0
        0x1800, 0x1800, 0, 0, 0x1800, 0x1800, 7, 1, Sprite.ANIMATION_FROZEN, Sprite.SPRITE_ALIGN_CENTER,

        // WATER EXPLOSION
        // FRAME0
        0x1800, 0x1800, 0, 0, 0x1800, 0x1800, 11, 1, Sprite.ANIMATION_FROZEN, Sprite.SPRITE_ALIGN_CENTER,

        // SPLASH
        // FRAME0
        0x1400, 0x1000, 0, 0, 0x1400, 0x1000, 4, 5, Sprite.ANIMATION_FROZEN, Sprite.SPRITE_ALIGN_CENTER,

        // ARTILLERY1
        // FRAME0
        0x1600, 0x1600, 0x400, 0x400, 0x0E00, 0x0E00, 1, 1, Sprite.ANIMATION_FROZEN, Sprite.SPRITE_ALIGN_CENTER,

        // ARTILLERY1 GUN
        // FRAME0
        0x1600, 0x1600, 0x800, 0x800, 0x600, 0x600, 1, 2, Sprite.ANIMATION_FROZEN, Sprite.SPRITE_ALIGN_CENTER,
        // FRAME1
        0x1600, 0x1600, 0x800, 0x800, 0x600, 0x600, 1, 2, Sprite.ANIMATION_FROZEN, Sprite.SPRITE_ALIGN_CENTER,
        // FRAME2
        0x1600, 0x1600, 0x800, 0x800, 0x600, 0x600, 1, 2, Sprite.ANIMATION_FROZEN, Sprite.SPRITE_ALIGN_CENTER,
        // FRAME3
        0x1600, 0x1600, 0x800, 0x800, 0x600, 0x600, 1, 2, Sprite.ANIMATION_FROZEN, Sprite.SPRITE_ALIGN_CENTER,
        // FRAME4
        0x1600, 0x1600, 0x800, 0x800, 0x600, 0x600, 1, 2, Sprite.ANIMATION_FROZEN, Sprite.SPRITE_ALIGN_CENTER,
        // FRAME5
        0x1600, 0x1600, 0x800, 0x800, 0x600, 0x600, 1, 2, Sprite.ANIMATION_FROZEN, Sprite.SPRITE_ALIGN_CENTER,
        // FRAME6
        0x1600, 0x1600, 0x800, 0x800, 0x600, 0x600, 1, 2, Sprite.ANIMATION_FROZEN, Sprite.SPRITE_ALIGN_CENTER,
        // FRAME7
        0x1600, 0x1600, 0x800, 0x800, 0x600, 0x600, 1, 2, Sprite.ANIMATION_FROZEN, Sprite.SPRITE_ALIGN_CENTER,

        // ARTILLERY1 DESTROYED
        // FRAME0
        0x0A00, 0x0B00, 0, 0, 0x0A00, 0x0B00, 1, 1, Sprite.ANIMATION_FROZEN, Sprite.SPRITE_ALIGN_CENTER,

        // ARTILLERY2
        // FRAME0
        0x1900, 0x1900, 0x500, 0x500, 0xF00, 0xF00, 1, 1, Sprite.ANIMATION_FROZEN, Sprite.SPRITE_ALIGN_CENTER,

        // ARTILLERY2 GUN
        // FRAME0
        0x1900, 0x1900, 0x800, 0x800, 0x900, 0x900, 1, 2, Sprite.ANIMATION_FROZEN, Sprite.SPRITE_ALIGN_CENTER,
        // FRAME1
        0x1900, 0x1900, 0x800, 0x800, 0x900, 0x900, 1, 2, Sprite.ANIMATION_FROZEN, Sprite.SPRITE_ALIGN_CENTER,
        // FRAME2
        0x1900, 0x1900, 0x800, 0x800, 0x900, 0x900, 1, 2, Sprite.ANIMATION_FROZEN, Sprite.SPRITE_ALIGN_CENTER,
        // FRAME3
        0x1900, 0x1900, 0x800, 0x800, 0x900, 0x900, 1, 2, Sprite.ANIMATION_FROZEN, Sprite.SPRITE_ALIGN_CENTER,
        // FRAME4
        0x1900, 0x1900, 0x800, 0x800, 0x900, 0x900, 1, 2, Sprite.ANIMATION_FROZEN, Sprite.SPRITE_ALIGN_CENTER,
        // FRAME5
        0x1900, 0x1900, 0x800, 0x800, 0x900, 0x900, 1, 2, Sprite.ANIMATION_FROZEN, Sprite.SPRITE_ALIGN_CENTER,
        // FRAME6
        0x1900, 0x1900, 0x800, 0x800, 0x900, 0x900, 1, 2, Sprite.ANIMATION_FROZEN, Sprite.SPRITE_ALIGN_CENTER,
        // FRAME7
        0x1900, 0x1900, 0x800, 0x800, 0x900, 0x900, 1, 2, Sprite.ANIMATION_FROZEN, Sprite.SPRITE_ALIGN_CENTER,


        // ARTILLERY2 DESTROYED
        // FRAME0
        0x0D00, 0x0D00, 0, 0, 0x0D00, 0x0D00, 1, 1, Sprite.ANIMATION_FROZEN, Sprite.SPRITE_ALIGN_CENTER,

        // TANKLEFT
        // FRAME0
        0x1900, 0x1900, 0x400, 0x600, 0x1200, 0x0D00, 1, 1, Sprite.ANIMATION_FROZEN, Sprite.SPRITE_ALIGN_CENTER,

        // TANKLEFT DESTROYED
        // FRAME0
        0x1000, 0x0E00, 0, 0, 0x1000, 0x0E00, 1, 1, Sprite.ANIMATION_FROZEN, Sprite.SPRITE_ALIGN_CENTER,

        // TANKRIGHT
        // FRAME0
        0x1900, 0x1900, 0x400, 0x600, 0x1200, 0x0D00, 1, 1, Sprite.ANIMATION_FROZEN, Sprite.SPRITE_ALIGN_CENTER,

        // TANKRIGHT DESTROYED
        // FRAME0
        0x1000, 0x0E00, 0, 0, 0x1000, 0x0E00, 1, 1, Sprite.ANIMATION_FROZEN, Sprite.SPRITE_ALIGN_CENTER,

        // TANK GUN
        // FRAME0
        0x1900, 0x1900, 0x800, 0x800, 0x900, 0x900, 1, 2, Sprite.ANIMATION_FROZEN, Sprite.SPRITE_ALIGN_CENTER,
        // FRAME1
        0x1900, 0x1900, 0x800, 0x800, 0x900, 0x900, 1, 2, Sprite.ANIMATION_FROZEN, Sprite.SPRITE_ALIGN_CENTER,
        // FRAME2
        0x1900, 0x1900, 0x800, 0x800, 0x900, 0x900, 1, 2, Sprite.ANIMATION_FROZEN, Sprite.SPRITE_ALIGN_CENTER,
        // FRAME3
        0x1900, 0x1900, 0x800, 0x800, 0x900, 0x900, 1, 2, Sprite.ANIMATION_FROZEN, Sprite.SPRITE_ALIGN_CENTER,
        // FRAME4
        0x1900, 0x1900, 0x800, 0x800, 0x900, 0x900, 1, 2, Sprite.ANIMATION_FROZEN, Sprite.SPRITE_ALIGN_CENTER,
        // FRAME5
        0x1900, 0x1900, 0x800, 0x800, 0x900, 0x900, 1, 2, Sprite.ANIMATION_FROZEN, Sprite.SPRITE_ALIGN_CENTER,
        // FRAME6
        0x1900, 0x1900, 0x800, 0x800, 0x900, 0x900, 1, 2, Sprite.ANIMATION_FROZEN, Sprite.SPRITE_ALIGN_CENTER,
        // FRAME7
        0x1900, 0x1900, 0x800, 0x800, 0x900, 0x900, 1, 2, Sprite.ANIMATION_FROZEN, Sprite.SPRITE_ALIGN_CENTER,

        // WATERMINE
        // FRAME0
        0xA00, 0xA00, 0x100, 0x100, 0x800, 0x800, 4, 2, Sprite.ANIMATION_PENDULUM, Sprite.SPRITE_ALIGN_CENTER,

        // RADAR
        // FRAME0
        0x1000, 0x1000, 0, 0, 0x1000, 0x1000, 8, 2, Sprite.ANIMATION_CYCLIC, Sprite.SPRITE_ALIGN_CENTER,

        // RADAR DESTROYED
        // FRAME0
        0x1000, 0x1000, 0, 0, 0x1000, 0x1000, 1, 1, Sprite.ANIMATION_FROZEN, Sprite.SPRITE_ALIGN_CENTER,

        // BOMBER
        // FRAME0
        0x1500, 0x1A00, 0, 0, 0x1500, 0x1A00, 1, 1, Sprite.ANIMATION_FROZEN, Sprite.SPRITE_ALIGN_CENTER,

        // BOMBER DROP
        // FRAME0
        0x1500, 0x1A00, 0, 0, 0x1500, 0x1A00, 5, 2, Sprite.ANIMATION_FROZEN, Sprite.SPRITE_ALIGN_CENTER,

        // LINEAR SHIP
        // FRAME0
        0x1700, 0x3B00, 0x500, 0x600, 0x0D00, 0x2f00, 1, 5, Sprite.ANIMATION_FROZEN, Sprite.SPRITE_ALIGN_CENTER,

        // LINEAR SHIP DESTROYED
        // FRAME0
        0x1700, 0x3B00, 0x500, 0x600, 0x0D00, 0x2f00, 4, 4, Sprite.ANIMATION_FROZEN, Sprite.SPRITE_ALIGN_CENTER,

        // PLAYER GUN SHELL
        0x300, 0x300, 0, 0, 0x300, 0x300, 1, 3, Sprite.ANIMATION_FROZEN, Sprite.SPRITE_ALIGN_CENTER,

        // PLAYER NURS ROCKET
        // FRAME 0
        0x700, 0x600, 0x000, 0, 0x500, 0x600, 1, 2, Sprite.ANIMATION_FROZEN, Sprite.SPRITE_ALIGN_CENTER,
        // FRAME 1
        0x700, 0x600, 0x000, 0, 0x500, 0x600, 1, 2, Sprite.ANIMATION_FROZEN, Sprite.SPRITE_ALIGN_CENTER,
        // FRAME 2
        0x700, 0x600, 0x000, 0, 0x500, 0x600, 1, 2, Sprite.ANIMATION_FROZEN, Sprite.SPRITE_ALIGN_CENTER,
        // FRAME 3
        0x700, 0x600, 0x000, 0, 0x500, 0x600, 1, 2, Sprite.ANIMATION_FROZEN, Sprite.SPRITE_ALIGN_CENTER,
        // FRAME 4
        0x700, 0x600, 0x000, 0, 0x500, 0x600, 1, 2, Sprite.ANIMATION_FROZEN, Sprite.SPRITE_ALIGN_CENTER,
        // FRAME 5
        0x700, 0x600, 0x000, 0, 0x500, 0x600, 1, 2, Sprite.ANIMATION_FROZEN, Sprite.SPRITE_ALIGN_CENTER,
        // FRAME 6
        0x700, 0x600, 0x000, 0, 0x500, 0x600, 1, 2, Sprite.ANIMATION_FROZEN, Sprite.SPRITE_ALIGN_CENTER,

        // INVISIBLE CORRIDOR
        // FRAME 0
        //(VIRTUALCELLWIDTH * 10) << 8, (VIRTUALCELLHEIGHT * 10) << 8, 0, 0, (VIRTUALCELLWIDTH * 10) << 8, (VIRTUALCELLHEIGHT * 10) << 8, 4, 4, Sprite.ANIMATION_FROZEN, Sprite.SPRITE_ALIGN_CENTER,
        0x1E00, 0x1E00, 0x0E00, 0, 0x0400, 0x1E00, 4, 3, Sprite.ANIMATION_FROZEN, Sprite.SPRITE_ALIGN_CENTER,

        // ENEMY SHELL
        // FRAME 0
        0x300, 0x300, 0, 0, 0x300, 0x300, 1, 3, Sprite.ANIMATION_CYCLIC, Sprite.SPRITE_ALIGN_CENTER,

        // SMART ROCKET
        // FRAME 0
        0xB00, 0xB00, 0x300, 0x300, 0x500, 0x500, 1, 3, Sprite.ANIMATION_CYCLIC, Sprite.SPRITE_ALIGN_CENTER,
        // FRAME 1
        0xB00, 0xB00, 0x300, 0x300, 0x500, 0x500, 1, 3, Sprite.ANIMATION_CYCLIC, Sprite.SPRITE_ALIGN_CENTER,
        // FRAME 2
        0xB00, 0xB00, 0x300, 0x300, 0x500, 0x500, 1, 3, Sprite.ANIMATION_CYCLIC, Sprite.SPRITE_ALIGN_CENTER,
        // FRAME 3
        0xB00, 0xB00, 0x300, 0x300, 0x500, 0x500, 1, 3, Sprite.ANIMATION_CYCLIC, Sprite.SPRITE_ALIGN_CENTER,
        // FRAME 4
        0xB00, 0xB00, 0x300, 0x300, 0x500, 0x500, 1, 3, Sprite.ANIMATION_CYCLIC, Sprite.SPRITE_ALIGN_CENTER,
        // FRAME 5
        0xB00, 0xB00, 0x300, 0x300, 0x500, 0x500, 1, 3, Sprite.ANIMATION_CYCLIC, Sprite.SPRITE_ALIGN_CENTER,
        // FRAME 6
        0xB00, 0xB00, 0x300, 0x300, 0x500, 0x500, 1, 3, Sprite.ANIMATION_CYCLIC, Sprite.SPRITE_ALIGN_CENTER,
        // FRAME 7
        0xB00, 0xB00, 0x300, 0x300, 0x500, 0x500, 1, 3, Sprite.ANIMATION_CYCLIC, Sprite.SPRITE_ALIGN_CENTER,

        // SMALL EXPLOSION
        0xD00, 0xC00, 0, 0, 0xD00, 0xC00, 4, 2, Sprite.ANIMATION_FROZEN, Sprite.SPRITE_ALIGN_CENTER
    };

    public Sprite p_PlayerSprite;
    public Sprite[] ap_ActiveObjects;
    public Sprite[] ap_Splashes;

    public Sprite[] ap_Bombers;
    public Sprite[] ap_UserGunShells;
    public Sprite[] ap_EnemyShells;
    public Sprite p_UserRocket;

    // Smartrocket's warhead variables
    protected Sprite p_WarheadTargetSprite;
    private int i_WarheadTargetSpriteType;
    private int i_WarheadTargetSpriteWidth,i_WarheadTargetSpriteHeight;
    private int i_inertion;

public GameletImpl(int _screenWidth, int _screenHeight, startup _gameActionListener, String _staticArrayResource)
    {
        super(_screenWidth, _screenHeight, _gameActionListener, _staticArrayResource);
    }

    public void initStage(int _stage)
    {
        super.initStage(_stage);

        abb_WayPartsArray = Stages.getPartArrayForStage(_stage);
        ab_linkPartsArray = Stages.getLinkPartArrayForStage(_stage);
        i_nonKilledDelay = 0;

        int i8_summLength = ((Stages.PARTCELLHEIGHT * VIRTUALCELLHEIGHT) * ab_linkPartsArray.length) << 8;
        int i8_screenHeight = (i_ScreenHeight << 8);

        i8_backgroundOffsetStart = i8_summLength - i8_screenHeight;
        i8_backgroundOffsetEnd = i8_summLength;

        i8_playerEnteringBorder = i8_backgroundOffsetEnd;

        lg_playerEntering = false;
        lg_playerFinishing = false;

        i8_lastLineAnalysed = -1;

        _initPlayerCoordsWithEnteringFlag();

        i_nonKilledDelay = 0;

        _deactivateAllPlayerSplashes();
        _deactivateAllGameObjects();
        i_PlayerKey = PLAYER_BUTTON_NONE;
    }



    private void _rocketToExplosion(Sprite p_sprite)
    {
         int i_mX = p_sprite.i_mainX;
         int i_mY = p_sprite.i_mainY;
         p_sprite.lg_SpriteActive = false;

         Sprite p_explosion = _getFirstInactiveSprite();

         if (p_explosion != null)
         {
            int i_type = EARTHEXPLOSION_NORMAL;

            /*

            if( checkSurface )
            {
                switch (_getWayElementForXY(i_mX, i_mY))
                {
                    case Stages.ELEMENT_WATER:
                    case Stages.ELEMENT_WATERMINE:
                        {
                            i_type = WATEREXPLOSION_NORMAL;
                            p_GameActionListener.gameAction(GAMEACTION_SND_WATEREXPLOSION);
                        }
                        ;
                        break;
                    default:
                        {
                            p_GameActionListener.gameAction(GAMEACTION_SND_EARTHEXPLOSION);
                        }
                }
            }
            else

            */

            {
               p_GameActionListener.gameAction(GAMEACTION_SND_EARTHEXPLOSION);
            }

            _initSpriteFromSpriteArray(p_explosion, i_type, 0);
            p_explosion.setMainPointXY(i_mX, i_mY);

         }
    }


    private void _processPlayerRocket()
    {
        if (p_UserRocket.lg_SpriteActive)
        {
            // Checking the collisions with the active objects
            for (int lo = 0; lo < MAXIMUMACTIVEOBJECTSONSCREEN; lo++)
            {
                Sprite p_object = ap_ActiveObjects[lo];

                if (!p_object.lg_SpriteActive || p_object.lg_linkedSprite) continue;

                if (!p_object.isCollided(p_UserRocket)) continue;

                switch (p_object.i_ObjectType)
                {
                    case TANKLEFT_DESTROYED:
                    case TANKRIGHT_DESTROYED:
                    case ARTILLERY1_DESTROYED:
                    case ARTILLERY2_DESTROYED:
                    case EARTHEXPLOSION_NORMAL:
                    case WATEREXPLOSION_NORMAL:
                    case SMALLEXPLOSION_NORMAL:
                    case RADAR_DESTROYED:
                    case WATERMINE_NORMAL:
                    case LINEARSHIP_DESTROYED:
                    case INVISIBLECORRIDOR_NORMAL:
                        continue;
                    default :
                        {
                            _explosionOfObject(p_object);
                        }
                }

                _rocketToExplosion(p_UserRocket);

                return;
            }

            if (p_UserRocket.i_ObjectType == NURS_NORMAL)
            {
                p_UserRocket.i_TTL--;
                if (p_UserRocket.i_TTL <= 0)
                {
                    p_UserRocket.lg_SpriteActive = false;

                    //  _rocketToExplosion(p_UserRocket, true);
                      //* no need for explosion

                    return;
                }

                int i_dx = p_UserRocket.i_deltaX;
                int i_dy = p_UserRocket.i_deltaY;
                int i_mainX = p_UserRocket.i_mainX + i_dx;
                int i_mainY = p_UserRocket.i_mainY - I8_BACKGROUNDSPEED + i_dy;

                if (i_dx < 0)
                {
                    if (i_mainX < 0)
                    {
                        p_UserRocket.lg_SpriteActive = false;
                        return;
                    }
                }
                else
                {
                    if (i_mainX >= ((Stages.PARTCELLWIDTH * VIRTUALCELLWIDTH) << 8))
                    {
                        p_UserRocket.lg_SpriteActive = false;
                        return;
                    }
                }

                if (i_mainY <= i8_backgroundOffsetStart)
                {
                    p_UserRocket.lg_SpriteActive = false;
                    return;
                }

                // Checking for breakable beach
                if (_getWayElementForXY(i_mainX, i_mainY) == Stages.ELEMENT_BREAKABLEBEACH && !_isSpriteOnTheCorridor(p_UserRocket))
                {
                    p_UserRocket.lg_SpriteActive = false;
                    Sprite p_corridor = _getFirstInactiveSprite();
                    Sprite p_obj;

                    // skips other corridors
                    int target = 0;
                    while (ap_ActiveObjects[target].lg_SpriteActive &&
                           ap_ActiveObjects[target].i_ObjectType == INVISIBLECORRIDOR_NORMAL)
                              target ++;

                    // move chain
                    if(p_corridor.i_spriteID != target)
                    {
                      int source = p_corridor.i_spriteID;
                      for (int i = source - 1; i>=target; i--)
                      {
                        p_obj = ap_ActiveObjects[i];
                        p_obj.i_spriteID = i+1;
                        ap_ActiveObjects[i+1] = p_obj;
                      }
                      p_corridor.i_spriteID = target;
                      ap_ActiveObjects[target] = p_corridor;

                      // correct linked sprites
                      for (int li = 0; li < MAXIMUMACTIVEOBJECTSONSCREEN; li++)
                      {
                         p_obj = ap_ActiveObjects[li];
                         if(p_obj.i_linkedSprite >= target && p_obj.i_linkedSprite < source)
                              p_obj.i_linkedSprite++;
                      }
                    }

                    int i_dh = ((Stages.PARTCELLHEIGHT * VIRTUALCELLHEIGHT) << 8);   // get segment size
                    int i_Y = i_mainY - i_mainY % i_dh + (i_dh >> 1);                // centering 'em

                    _initSpriteFromSpriteArray(p_corridor, INVISIBLECORRIDOR_NORMAL, 0);
                    p_corridor.setMainPointXY(p_UserRocket.i_mainX, i_Y);

                    Sprite p_explosion = _getFirstInactiveSprite();

                    if (p_explosion != null)
                    {
                      _initSpriteFromSpriteArray(p_explosion, EARTHEXPLOSION_NORMAL, 0);
                      p_explosion.setMainPointXY(p_UserRocket.i_mainX, p_UserRocket.i_mainY);
                      p_GameActionListener.gameAction(GAMEACTION_SND_EARTHEXPLOSION);
                    }

                    p_corridor.lg_SpriteActive = false;

                    for (int li = 0; li < MAXIMUMACTIVEOBJECTSONSCREEN; li++)
                    {
                        p_obj = ap_ActiveObjects[li];
                        if (p_obj.lg_SpriteActive)
                        {
                            if (p_corridor.isCollided(p_obj))
                            {
                                switch (p_obj.i_ObjectType)
                                {
                                    case ARTILLERY1_NORMAL:
                                    case ARTILLERY2_NORMAL:
                                        {
                                            _explosionOfObject(p_obj);
                                        }
                                        ;
                                        break;
                                    case TANKLEFT_NORMAL:
                                    case TANKRIGHT_NORMAL:
                                        {
                                            _explosionOfObject(p_obj);
                                            p_obj.lg_SpriteActive = false;
                                        }
                                        ;
                                        break;
                                    case TANKLEFT_DESTROYED:
                                    case TANKRIGHT_DESTROYED:
                                        {
                                            _initSpriteFromSpriteArray(p_obj, EARTHEXPLOSION_NORMAL, 0);
                                            p_GameActionListener.gameAction(GAMEACTION_SND_EARTHEXPLOSION);
                                        }
                                        ;
                                        break;
                                    case WATERMINE_NORMAL:
                                        {
                                            _initSpriteFromSpriteArray(p_obj, WATEREXPLOSION_NORMAL, 0);
                                            p_GameActionListener.gameAction(GAMEACTION_SND_WATEREXPLOSION);

                                        }
                                        ;
                                        break;
                                    case RADAR_NORMAL:
                                        {
                                            _initSpriteFromSpriteArray(p_obj, RADAR_DESTROYED, 0);
                                        }
                                }
                            }
                        }
                    }
                    p_corridor.lg_SpriteActive = true;
                    p_UserRocket.lg_SpriteActive = false;

                    p_GameActionListener.gameAction(GAMEACTION_SND_BRIDGEDESTROYED);

                    return;
                }
                else
                    p_UserRocket.setMainPointXY(i_mainX, i_mainY);
            }
            else
            {
                // the AI rocket
                i_inertion--;
                int i_newRocketState = p_UserRocket.i_ObjectState;
                if (i_inertion <= 0)
                {
                    i_newRocketState = _calculateNewStateForSmartRocket();
                    i_inertion = SMARTROCKETINERTION;
                }

                boolean lg_destroyRocket = false;

                if (!p_WarheadTargetSprite.lg_SpriteActive || p_WarheadTargetSprite.i_ObjectType != i_WarheadTargetSpriteType)
                {
                    lg_destroyRocket = true;
                }
                else
                {
                    if (i_newRocketState != p_UserRocket.i_ObjectState)
                    {
                        _initSpriteFromSpriteArray(p_UserRocket, SMARTROCKET_NORMAL, i_newRocketState);
                        _setDeltaToSmartRocket();
                    }

                    p_UserRocket.setMainPointXY(p_UserRocket.i_deltaX + p_UserRocket.i_mainX, p_UserRocket.i_deltaY + p_UserRocket.i_mainY);

                    if (p_WarheadTargetSprite.isCollided(p_UserRocket))
                    {
                        _explosionOfObject(p_WarheadTargetSprite);
                        lg_destroyRocket = true;
                    }
                    else if (p_WarheadTargetSprite.i_ScreenY > i8_backgroundOffsetEnd)
                    {
                        lg_destroyRocket = true;
                    }
                }

                if (lg_destroyRocket)
                {
                    Sprite p_Explosion = _getFirstInactiveSprite();
                    if (p_Explosion != null)
                    {
                        _initSpriteFromSpriteArray(p_Explosion, EARTHEXPLOSION_NORMAL, 0);
                        p_GameActionListener.gameAction(GAMEACTION_SND_EARTHEXPLOSION);
                        p_Explosion.setMainPointXY(p_UserRocket.i_mainX, p_UserRocket.i_mainY);
                    }
                    p_UserRocket.lg_SpriteActive = false;
                }
            }
        }
    }

    private Sprite _findObjectForRocket()
    {
        int i_plX = p_PlayerSprite.i_ScreenX + FIREPOINTS[p_PlayerSprite.i_ObjectState][0];
        int i_plY = p_PlayerSprite.i_ScreenY + FIREPOINTS[p_PlayerSprite.i_ObjectState][1];

        int i_distance = 0x7FFFFFFF;
        Sprite p_nearestSprite = null;

        for (int li = 0; li < MAXIMUMACTIVEOBJECTSONSCREEN; li++)
        {
            Sprite p_spr = ap_ActiveObjects[li];
            if (p_spr.lg_SpriteActive)
            {
                switch (p_spr.i_ObjectType)
                {
                    case TANKLEFT_NORMAL:
                    case TANKRIGHT_NORMAL:
                    case ARTILLERY1_NORMAL:
                    case ARTILLERY2_NORMAL:
                        {
                            int i_mX = p_spr.i_mainX;
                            int i_mY = p_spr.i_mainY;

                            int i_newDistance = (Math.abs(i_plX - i_mX) + Math.abs(i_plY - i_mY)) >> 1;

                            if (i_mY > i_plY) i_newDistance <<= 1;

                            if (i_distance > i_newDistance)
                            {
                                i_distance = i_newDistance;
                                p_nearestSprite = p_spr;
                            }
                        }
                        ;
                        break;
                }
            }
        }

        return p_nearestSprite;
    }

    private boolean _isSpriteOnTheCorridor(Sprite _spr)
    {
        for (int li = 0; li < MAXIMUMACTIVEOBJECTSONSCREEN; li++)
        {
            Sprite p_spr = ap_ActiveObjects[li];
            if (p_spr.lg_SpriteActive)
            {
                if (p_spr.i_ObjectType == INVISIBLECORRIDOR_NORMAL)
                {
                    if (p_spr.isCollided(_spr)) return true;
                }
            }
        }
        return false;
    }

    private void _processPlayerShells()
    {
        for (int li = 0; li < MAXIMUMPLAYERSHELLS; li++)
        {
            Sprite p_shell = ap_UserGunShells[li];

            if (!p_shell.lg_SpriteActive) continue;

            p_shell.i_TTL--;
            if (p_shell.i_TTL <= 0)
            {
                p_shell.lg_SpriteActive = false;
                continue;
            }

            int i_dx = p_shell.i_deltaX;
            int i_dy = p_shell.i_deltaY;
            int i_mainX = p_shell.i_mainX + i_dx;
            int i_mainY = p_shell.i_mainY - I8_BACKGROUNDSPEED + i_dy;

            if (i_dx < 0)
            {
                if (i_mainX < 0)
                {
                    p_shell.lg_SpriteActive = false;
                    continue;
                }
            }
            else
            {
                if (i_mainX >= ((Stages.PARTCELLWIDTH * VIRTUALCELLWIDTH) << 8))
                {
                    p_shell.lg_SpriteActive = false;
                    continue;
                }
            }

            if (i_mainY <= i8_backgroundOffsetStart)
            {
                p_shell.lg_SpriteActive = false;
                continue;
            }

            p_shell.processAnimation();
            p_shell.setMainPointXY(i_mainX, i_mainY);

            // Checking the collisions with the active objects
            for (int lo = 0; lo < MAXIMUMACTIVEOBJECTSONSCREEN; lo++)
            {
                Sprite p_object = ap_ActiveObjects[lo];

                if (!p_object.lg_SpriteActive || p_object.lg_linkedSprite) continue;

                if (!p_object.isCollided(p_shell)) continue;

                switch (p_object.i_ObjectType)
                {
                    case TANKLEFT_DESTROYED:
                    case TANKRIGHT_DESTROYED:
                    case ARTILLERY1_DESTROYED:
                    case ARTILLERY2_DESTROYED:
                    case EARTHEXPLOSION_NORMAL:
                    case WATEREXPLOSION_NORMAL:
                    case SMALLEXPLOSION_NORMAL:
                    case RADAR_DESTROYED:
                    case LINEARSHIP_DESTROYED:
                    case INVISIBLECORRIDOR_NORMAL:
                        continue;
                    default :
                        {
                            p_object.i_deltaX -= USERSHELLPOWER;
                            if (p_object.i_deltaX <= 0)
                            {
                                _explosionOfObject(p_object);
                            }
                            else
                              {
                                  int i_mX = p_shell.i_mainX;
                                  int i_mY = p_shell.i_mainY;

                                  Sprite p_explosion = _getFirstInactiveSprite();

                                  if (p_explosion != null)
                                  {
                                    _initSpriteFromSpriteArray(p_explosion, SMALLEXPLOSION_NORMAL, 0);
                                    p_explosion.setMainPointXY(i_mX, i_mY);
                                  }

                                  p_GameActionListener.gameAction(GAMEACTION_SND_PLAYERHIT);
                              }
                        }
                }
                p_shell.lg_SpriteActive = false;

            }

            // Checking for bomber hit
            for (int lb = 0; lb < 3; lb++)
            {
                Sprite p_bomber = ap_Bombers[lb];
                if (p_bomber.lg_SpriteActive)
                {
                    if (p_bomber.i_ObjectType == BOMBER_DROP) continue;

                    if (p_bomber.isCollided(p_shell))
                    {
                        p_bomber.i_deltaX -= USERSHELLPOWER;

                        int i_mX = p_shell.i_mainX;
                        int i_mY = p_shell.i_mainY;

                        Sprite p_explosion = _getFirstInactiveSprite();

                        if (p_explosion != null)
                        {
                            _initSpriteFromSpriteArray(p_explosion, SMALLEXPLOSION_NORMAL, 0);
                            p_explosion.setMainPointXY(i_mX, i_mY);
                        }

                        p_shell.lg_SpriteActive = false;
                        break;
                    }
                }
            }
        }
    }

    private void _explosionOfObject(Sprite _object)
    {
        switch (_object.i_ObjectType)
        {
            case TANKLEFT_NORMAL:
                {
                    Sprite p_linked = ap_ActiveObjects[_object.i_linkedSprite];
                    _initSpriteFromSpriteArray(p_linked, EARTHEXPLOSION_NORMAL, 0);
                    p_GameActionListener.gameAction(GAMEACTION_SND_EARTHEXPLOSION);
                    _initSpriteFromSpriteArray(_object, TANKLEFT_DESTROYED, 0);
                    i_PlayerScore += 10;
                }
                ;
                break;
            case TANKRIGHT_NORMAL:
                {
                    Sprite p_linked = ap_ActiveObjects[_object.i_linkedSprite];
                    _initSpriteFromSpriteArray(p_linked, EARTHEXPLOSION_NORMAL, 0);
                    p_GameActionListener.gameAction(GAMEACTION_SND_EARTHEXPLOSION);
                    _initSpriteFromSpriteArray(_object, TANKRIGHT_DESTROYED, 0);
                    i_PlayerScore += 10;
                }
                ;
                break;
            case ARTILLERY1_NORMAL:
                {
                    Sprite p_linked = ap_ActiveObjects[_object.i_linkedSprite];
                    _initSpriteFromSpriteArray(p_linked, EARTHEXPLOSION_NORMAL, 0);
                    p_GameActionListener.gameAction(GAMEACTION_SND_EARTHEXPLOSION);
                    _initSpriteFromSpriteArray(_object, ARTILLERY1_DESTROYED, 0);
                    i_PlayerScore += 5;
                }
                ;
                break;
            case ARTILLERY2_NORMAL:
                {
                    Sprite p_linked = ap_ActiveObjects[_object.i_linkedSprite];
                    _initSpriteFromSpriteArray(p_linked, EARTHEXPLOSION_NORMAL, 0);
                    p_GameActionListener.gameAction(GAMEACTION_SND_EARTHEXPLOSION);
                    _initSpriteFromSpriteArray(_object, ARTILLERY2_DESTROYED, 0);
                    i_PlayerScore += 10;
                }
                ;
                break;
            case RADAR_NORMAL:
                {
                    Sprite p_expl = _getFirstInactiveSprite();
                    _initSpriteFromSpriteArray(_object, RADAR_DESTROYED, 0);
                    _initSpriteFromSpriteArray(p_expl, EARTHEXPLOSION_NORMAL, 0);
                    p_GameActionListener.gameAction(GAMEACTION_SND_EARTHEXPLOSION);
                    p_expl.setMainPointXY(_object.i_mainX, _object.i_mainY);
                    i_PlayerScore += 2;
                }
                ;
                break;
            case LINEARSHIP_NORMAL:
                {
                    _initSpriteFromSpriteArray(_object, LINEARSHIP_DESTROYED, 0);
                    p_GameActionListener.gameAction(GAMEACTION_SND_SHIPDESTROYED);
                    i_PlayerScore += 20;
                }
                ;
                break;
            case WATERMINE_NORMAL:
                {
                    _initSpriteFromSpriteArray(_object, WATEREXPLOSION_NORMAL, 0);
                    p_GameActionListener.gameAction(GAMEACTION_SND_WATEREXPLOSION);
                    i_PlayerScore += 5;
                }
                ;
                break;
            case BOMBER_NORMAL:
                {
                    _initSpriteFromSpriteArray(_object, BOMBER_DROP, 0);
                    p_GameActionListener.gameAction(GAMEACTION_SND_AIRBOMBERDROP);
                    i_PlayerScore += 15;
                }
                ;
                break;
        }
    }

    private void _deactivateAllGameObjects()
    {
        for (int li = 0; li < MAXIMUMACTIVEOBJECTSONSCREEN; li++) ap_ActiveObjects[li].lg_SpriteActive = false;
        for (int li = 0; li < MAXIMUMENEMYSHELLS; li++) ap_EnemyShells[li].lg_SpriteActive = false;
        for (int li = 0; li < 3; li++) ap_Bombers[li].lg_SpriteActive = false;
        for (int li = 0; li < MAXIMUMPLAYERSHELLS; li++) ap_UserGunShells[li].lg_SpriteActive = false;
    }

    private Sprite _getFirstInactiveUserShell()
    {
        for (int li = 0; li < MAXIMUMPLAYERSHELLS; li++)
        {
            if (!ap_UserGunShells[li].lg_SpriteActive) return ap_UserGunShells[li];
        }
        return null;
    }

    private Sprite _getFirstInactiveSprite()
    {
        for (int li = 0; li < MAXIMUMACTIVEOBJECTSONSCREEN; li++)
        {
            if (!ap_ActiveObjects[li].lg_SpriteActive) return ap_ActiveObjects[li];
        }
        return null;
    }

    private Sprite _getFirstInactiveEnemyShell()
    {
        for (int li = 0; li < MAXIMUMENEMYSHELLS; li++)
        {
            if (!ap_EnemyShells[li].lg_SpriteActive) return ap_EnemyShells[li];
        }
        return null;
    }

    private void _processEnemyShells()
    {
        boolean lg_playerNormal = p_PlayerSprite.i_ObjectType == PLAYER_NORMAL;
        for (int li = 0; li < MAXIMUMENEMYSHELLS; li++)
        {
            Sprite p_shell = ap_EnemyShells[li];

            if (p_shell.lg_SpriteActive)
            {
                int i_dX = p_shell.i_deltaX;
                int i_dY = p_shell.i_deltaY;
                int i_mX = p_shell.i_mainX + i_dX;
                int i_mY = p_shell.i_mainY + i_dY;

                int i_TTL = p_shell.i_TTL - 1;

                if (i_TTL <= 0)
                {
                    p_shell.lg_SpriteActive = false;
                    continue;
                }
                else
                    p_shell.i_TTL = i_TTL;

                if (i_dX < 0)
                {
                    if (i_mX < 0)
                    {
                        p_shell.lg_SpriteActive = false;
                        continue;
                    }
                }
                else if (i_dX > 0)
                {
                    if (i_mX >= ((Stages.PARTCELLWIDTH * VIRTUALCELLWIDTH) << 8))
                    {
                        p_shell.lg_SpriteActive = false;
                        continue;
                    }
                }

                if (i_dY < 0)
                {
                    if (i_mY < i8_backgroundOffsetStart)
                    {
                        p_shell.lg_SpriteActive = false;
                        continue;
                    }
                }
                else if (i_dY > 0)
                {
                    if (i_mY < i8_backgroundOffsetEnd)
                    {
                        p_shell.lg_SpriteActive = false;
                        continue;
                    }
                }

                p_shell.setMainPointXY(i_mX, i_mY);

                if (lg_playerNormal && p_PlayerSprite.isCollided(p_shell))
                {
                    Sprite p_expl = _getFirstInactiveSprite();
                    _initSpriteFromSpriteArray(p_expl, SMALLEXPLOSION_NORMAL, 0);
                    p_expl.setMainPointXY(p_shell.i_mainX, p_shell.i_mainY);

                    i_playerPower -= ENEMYSHELLPOWER;
                    p_shell.lg_SpriteActive = false;
                    p_GameActionListener.gameAction(GAMEACTION_SND_ENEMYHIT);

                }
                else
                {
                    p_shell.processAnimation();
                }
            }
        }
    }

    private void _setDeltaToEnemyShell(Sprite _shell, int _state)
    {
        int i_state = _state;

        int i_dX = 0;
        int i_dY = 0;

        switch (i_state)
        {
            case 0:
                {
                    i_dX = 0;
                    i_dY = 0 - ENEMYSHELLSPEED - (I8_BACKGROUNDSPEED>>1);
                }
                ;
                break;
            case 1:
                {
                    i_dX = ENEMYSHELLSPEED;
                    i_dY = 0 - ENEMYSHELLSPEED - (I8_BACKGROUNDSPEED>>1);
                }
                ;
                break;
            case 2:
                {
                    i_dX = ENEMYSHELLSPEED;
                    i_dY = 0;
                }
                ;
                break;
            case 3:
                {
                    i_dX = ENEMYSHELLSPEED;
                    i_dY = ENEMYSHELLSPEED;
                }
                ;
                break;
            case 4:
                {
                    i_dX = 0;
                    i_dY = ENEMYSHELLSPEED;
                }
                ;
                break;
            case 5:
                {
                    i_dX = 0 - ENEMYSHELLSPEED;
                    i_dY = ENEMYSHELLSPEED;
                }
                ;
                break;
            case 6:
                {
                    i_dX = 0 - ENEMYSHELLSPEED;
                    i_dY = 0;
                }
                ;
                break;
            case 7:
                {
                    i_dX = 0 - ENEMYSHELLSPEED;
                    i_dY = 0 - ENEMYSHELLSPEED - (I8_BACKGROUNDSPEED>>1);
                }
                ;
                break;
        }

        _shell.i_deltaX = i_dX;
        _shell.i_deltaY = i_dY;
        _shell.i_TTL = ENEMYSHELLTTL;
    }

    private void _setDeltaToSmartRocket()
    {
        int i_state = p_UserRocket.i_ObjectState;

        int i_dX = 0;
        int i_dY = 0;

        switch (i_state)
        {
            case 0:
                {
                    i_dX = 0;
                    i_dY = 0 - SMARTROCKETSPEED - I8_BACKGROUNDSPEED;
                }
                ;
                break;
            case 1:
                {
                    i_dX = SMARTROCKETSPEED;
                    i_dY = 0 - SMARTROCKETSPEED - I8_BACKGROUNDSPEED;
                }
                ;
                break;
            case 2:
                {
                    i_dX = SMARTROCKETSPEED;
                    i_dY = 0;
                }
                ;
                break;
            case 3:
                {
                    i_dX = SMARTROCKETSPEED;
                    i_dY = SMARTROCKETSPEED;
                }
                ;
                break;
            case 4:
                {
                    i_dX = 0;
                    i_dY = SMARTROCKETSPEED;
                }
                ;
                break;
            case 5:
                {
                    i_dX = 0 - SMARTROCKETSPEED;
                    i_dY = SMARTROCKETSPEED;
                }
                ;
                break;
            case 6:
                {
                    i_dX = 0 - SMARTROCKETSPEED;
                    i_dY = 0;
                }
                ;
                break;
            case 7:
                {
                    i_dX = 0 - SMARTROCKETSPEED;
                    i_dY = 0 - SMARTROCKETSPEED - I8_BACKGROUNDSPEED;
                }
                ;
                break;
        }

        p_UserRocket.i_deltaX = i_dX;
        p_UserRocket.i_deltaY = i_dY;
    }


    private void _initPlayerCoordsWithEnteringFlag()
    {
        _initSpriteFromSpriteArray(p_PlayerSprite, PLAYER_NORMAL, (PLAYER_NORMAL_SPRITES_NUM >> 1));
        _deactivateAllPlayerSplashes();
        lg_playerEntering = true;
        i_nonKilledDelay = NONKILLEDDELAY;
        i_playerPower = INITPLAYERPOWER;
        i8_playerEnteringBorder = i8_backgroundOffsetEnd - (p_PlayerSprite.i_height); // <<1

        int i8_y = i8_backgroundOffsetEnd + p_PlayerSprite.i_height;

        int i8_x = 0;

        if (getRandomInt(100) > 50)
        {
            i8_x = _getCenterCoordinateOfWaterLeft(i8_playerEnteringBorder);
        }
        else
        {
            i8_x = _getCenterCoordinateOfWaterRight(i8_playerEnteringBorder);
        }

        p_PlayerSprite.setMainPointXY(i8_x, i8_y);

        p_GameActionListener.gameAction(GAMEACTION_SND_PLAYERREADY);
    }

    // Return the sprite what is collided with the player
    private Sprite _processSprites()
    {
        Sprite p_collidedObj = null;
        int i8_playerCX = p_PlayerSprite.i_mainX;
        int i8_playerCY = p_PlayerSprite.i_mainY;

        for (int li = 0; li < GameletImpl.MAXIMUMACTIVEOBJECTSONSCREEN; li++)
        {
            Sprite p_sprite = ap_ActiveObjects[li];

            if (!p_sprite.lg_SpriteActive) continue;

            boolean lg_animationCompleted = p_sprite.processAnimation();

            boolean lg_firing = false;

            if (p_PlayerSprite.i_ObjectType == PLAYER_NORMAL)
            {
                switch (p_sprite.i_ObjectType)
                {
                    case LINEARSHIP_DESTROYED:
                    case LINEARSHIP_NORMAL:
                    case WATERMINE_NORMAL:
                        {
                            if (p_sprite.isCollided(p_PlayerSprite))
                            {
                                p_collidedObj = p_sprite;
                            }
                        }
                        ;
                        break;
                }
            }

            int i_sprMX = p_sprite.i_mainX;
            int i_sprMY = p_sprite.i_mainY;

            int i_stateSpr = -1;

            switch (p_sprite.i_ObjectType)
            {
                case TANKLEFT_NORMAL:
                    {
                        if (_getWayElementForXY(i_sprMX, i_sprMY) == Stages.ELEMENT_WATER)
                        {
                            _initSpriteFromSpriteArray(p_sprite, EARTHEXPLOSION_NORMAL, 0);
                        }
                        else
                        {
                            int i_sense = p_sprite.i_ScreenX - (VIRTUALCELLWIDTH << 8) - TANKSPEED;
                            if (i_sense <= 0) continue;
                            switch (_getWayElementForXY(i_sense, i_sprMY))
                            {
                                case Stages.ELEMENT_LEFTTANK:
                                case Stages.ELEMENT_NONBREAKABLEBEACH:
                                case Stages.ELEMENT_BREAKABLEBEACH:
                                    {
                                        Sprite p_GunSprite = ap_ActiveObjects[p_sprite.i_linkedSprite];
                                        i_sprMX -= TANKSPEED;
                                        p_GunSprite.setMainPointXY(i_sprMX, i_sprMY);
                                        p_sprite.setMainPointXY(i_sprMX, i_sprMY);
                                    }
                            }

                            if (_isSpriteOnTheCorridor(p_sprite))
                            {
                                ap_ActiveObjects[p_sprite.i_linkedSprite].lg_SpriteActive = false;
                                _initSpriteFromSpriteArray(p_sprite, EARTHEXPLOSION_NORMAL, 0);
                            }
                        }
                    }
                    ;
                    break;
                case TANKRIGHT_NORMAL:
                    {
                        if (_getWayElementForXY(i_sprMX, i_sprMY) == Stages.ELEMENT_WATER)
                        {
                            _initSpriteFromSpriteArray(p_sprite, EARTHEXPLOSION_NORMAL, 0);
                        }
                        else
                        {
                            int i_sense = p_sprite.i_ScreenX + p_sprite.i_width + (VIRTUALCELLWIDTH << 8) + TANKSPEED;
                            if (i_sense >= ((Stages.PARTCELLWIDTH * VIRTUALCELLWIDTH) << 8)) continue;

                            switch (_getWayElementForXY(i_sense, i_sprMY))
                            {
                                case Stages.ELEMENT_RIGHTTANK:
                                case Stages.ELEMENT_NONBREAKABLEBEACH:
                                case Stages.ELEMENT_BREAKABLEBEACH:
                                    {
                                        Sprite p_GunSprite = ap_ActiveObjects[p_sprite.i_linkedSprite];
                                        i_sprMX += TANKSPEED;
                                        p_GunSprite.setMainPointXY(i_sprMX, i_sprMY);
                                        p_sprite.setMainPointXY(i_sprMX, i_sprMY);
                                    }
                            }

                            if (_isSpriteOnTheCorridor(p_sprite))
                            {
                                ap_ActiveObjects[p_sprite.i_linkedSprite].lg_SpriteActive = false;
                                _initSpriteFromSpriteArray(p_sprite, EARTHEXPLOSION_NORMAL, 0);
                            }
                        }
                    }
                    ;
                    break;
                case LINEARSHIP_NORMAL:
                    {
                        int i_startY = p_sprite.i_ScreenY + p_sprite.i_col_offsetY;
                        int i_stepY = p_sprite.i_col_height / 3;
                        int i_mainX = p_sprite.i_mainX;

                        for (int lg = 0; lg < 3; lg++)
                        {
                            i_stateSpr = _calculateGunStateToPlayer(i_mainX, i_startY, i_mainX - 0x200, 0x400, i8_playerCX, i8_playerCY);

                            if (getRandomInt(i_ActivityArtillery) == (i_ActivityArtillery >> 1))
                            {
                                Sprite p_EnemyShell = _getFirstInactiveEnemyShell();
                                if (p_EnemyShell != null)
                                {
                                    _initSpriteFromSpriteArray(p_EnemyShell, ENEMYSHELL_NORMAL, 0);
                                    p_EnemyShell.setMainPointXY(i_mainX, i_startY);
                                    _setDeltaToEnemyShell(p_EnemyShell, i_stateSpr);
                                }
                            }
                            i_startY += i_stepY;
                        }
                    }
                    ;
                    break;
                case LINEARSHIP_DESTROYED:
                    {
                        if (lg_animationCompleted)
                        {
                            p_sprite.lg_SpriteActive = false;
                            continue;
                        }
                    }
                    ;
                    break;
                case TANKGUN_NORMAL:
                case ARTILLERY1GUN_NORMAL:
                case ARTILLERY2GUN_NORMAL:
                    {
                        lg_firing = true;
                        int i_newState = _calculateGunStateToPlayer(i_sprMX, i_sprMY, p_sprite.i_ScreenX, p_sprite.i_width, i8_playerCX, i8_playerCY);
                        if (p_sprite.i_ObjectState != i_newState)
                        {
                            _initSpriteFromSpriteArray(p_sprite, p_sprite.i_ObjectType, i_newState);
                        }

                        i_stateSpr = i_newState;
                    }
                    ;
                    break;
                case EARTHEXPLOSION_NORMAL:
                case WATEREXPLOSION_NORMAL:
                case SMALLEXPLOSION_NORMAL:
                    {
                        if (lg_animationCompleted)
                            p_sprite.lg_SpriteActive = false;
                    }
                    ;
                    break;
            }

            if (lg_firing)
            {
                if (getRandomInt(i_ActivityArtillery) == (i_ActivityArtillery >> 1))
                {
                    Sprite p_EnemyShell = _getFirstInactiveEnemyShell();
                    if (p_EnemyShell != null)
                    {
                        _initSpriteFromSpriteArray(p_EnemyShell, ENEMYSHELL_NORMAL, 0);
                        p_EnemyShell.setMainPointXY(i_sprMX, i_sprMY);
                        _setDeltaToEnemyShell(p_EnemyShell, i_stateSpr);
                    }
                }
            }

            // Checking the visibility of the sprite
            if (!p_sprite.lg_linkedSprite && p_sprite.i_ScreenY > i8_backgroundOffsetEnd)
            {
                if (p_sprite.i_linkedSprite >= 0)
                {
                    ap_ActiveObjects[p_sprite.i_linkedSprite].lg_SpriteActive = false;
                }
                p_sprite.lg_SpriteActive = false;
            }
        }

        // Processing the splashes
        for (int li = 0; li < ap_Splashes.length; li++)
        {
            Sprite p_splash = ap_Splashes[li];
            if (!p_splash.lg_SpriteActive) continue;
            if (p_splash.processAnimation()) p_splash.lg_SpriteActive = false;
        }

        return p_collidedObj;
    }

    // return true if the player is dead
    private boolean _processPlayer(Sprite _collidedSprite)
    {
        if (i_nonKilledDelay > 0)
        {
              i_playerPower = INITPLAYERPOWER;
              return false;
        }
        if (!lg_playerFinishing && _collidedSprite != null)
            switch (_collidedSprite.i_ObjectType)
            {
                case WATERMINE_NORMAL:
                    {
                        i_playerPower -= WATERMINEFORCE;
                        _initSpriteFromSpriteArray(_collidedSprite, WATEREXPLOSION_NORMAL, 0);
                        p_GameActionListener.gameAction(GAMEACTION_SND_WATEREXPLOSION);
                    }
                    ;
                    break;
                case LINEARSHIP_NORMAL:
                    {
                        _initSpriteFromSpriteArray(_collidedSprite, LINEARSHIP_DESTROYED, 0);
                    }
                case LINEARSHIP_DESTROYED:
                    {
                        i_playerPower = 0;
                    }
                    ;
                    break;
            }

        if (i_playerPower <= 0)
            return true;
        else
            return false;
    }

    public void newGameSession(int _level)
    {
        initLevel(_level);

        switch (_level)
        {
            case LEVEL0:
                {
                    i_Attemptions = LEVEL0_ATTEMPTIONS;
                    i_GameTimeDelay = LEVEL0_TIMEDELAY;
                    i_ActivityArtillery = LEVEL0_ARTILLERYACTIVITY;
                    i_ActivityAir = LEVEL0_AIRACTIVITY;
                }
                ;
                break;
            case LEVEL1:
                {
                    i_Attemptions = LEVEL1_ATTEMPTIONS;
                    i_GameTimeDelay = LEVEL1_TIMEDELAY;
                    i_ActivityArtillery = LEVEL1_ARTILLERYACTIVITY;
                    i_ActivityAir = LEVEL0_AIRACTIVITY;
                }
                ;
                break;
            case LEVEL2:
                {
                    i_Attemptions = LEVEL2_ATTEMPTIONS;
                    i_GameTimeDelay = LEVEL2_TIMEDELAY;
                    i_ActivityArtillery = LEVEL2_ARTILLERYACTIVITY;
                    i_ActivityAir = LEVEL2_AIRACTIVITY;
                }
                ;
                break;
        }

        p_PlayerSprite = new Sprite(0);
        ap_ActiveObjects = new Sprite[MAXIMUMACTIVEOBJECTSONSCREEN];
        for (int li = 0; li < ap_ActiveObjects.length; li++)
        {
            ap_ActiveObjects[li] = new Sprite(li);
        }

        ap_Splashes = new Sprite[MAXIMUMSPLASHES];
        for (int li = 0; li < ap_Splashes.length; li++)
        {
            ap_Splashes[li] = new Sprite(li);
        }

        ap_Bombers = new Sprite[3];
        for (int li = 0; li < ap_Bombers.length; li++)
        {
            ap_Bombers[li] = new Sprite(li);
        }

        ap_EnemyShells = new Sprite[MAXIMUMENEMYSHELLS];
        for (int li = 0; li < ap_EnemyShells.length; li++)
        {
            ap_EnemyShells[li] = new Sprite(li);
        }

        ap_UserGunShells = new Sprite[MAXIMUMPLAYERSHELLS];
        for (int li = 0; li < ap_UserGunShells.length; li++)
        {
            ap_UserGunShells[li] = new Sprite(li);
        }

        p_UserRocket = new Sprite(0);
    }

    public void endGameSession()
    {
        super.endGameSession();
        p_PlayerSprite = null;
        if(ap_ActiveObjects != null)
          for (int li = 0; li < ap_ActiveObjects.length; li++)
          {
              ap_ActiveObjects[li] = null;
          }
        Runtime.getRuntime().gc();
    }

    public void resumeGameAfterPlayerLost()
    {
        super.resumeGameAfterPlayerLost();
        i_PlayerState = PLAYERSTATE_NORMAL;
        i_GameState = GAMESTATE_PLAYED;

        _initPlayerCoordsWithEnteringFlag();
        _deactivateAllPlayerSplashes();
    }


    public int _getWayElementForXY(int _i8x, int _i8y)
    {
        _i8y >>= 8;
        _i8x >>= 8;
        int i_row = _i8y / VIRTUALCELLHEIGHT;
        int i_col = _i8x / VIRTUALCELLWIDTH;

        int i_blockIndex = ab_linkPartsArray[i_row / Stages.PARTCELLHEIGHT];
        int i_blockIndexOffset = (i_row % Stages.PARTCELLHEIGHT) * (Stages.PARTCELLWIDTH >>> 1) + (i_col >> 1);

        int i_elem = abb_WayPartsArray[i_blockIndex][i_blockIndexOffset] & 0xFF;
        if ((i_col & 0x1) == 0) i_elem >>>= 4; else i_elem &= 0xF;

        return i_elem;
    }
/*

//speed optimization
    public int _getWayElementForXY(int _x, int _y)
    {
        _y >>=10;
        _x >>=10;

        return ( abb_WayPartsArray[ ab_linkPartsArray[ _y >> 2 ] ]
                                  [ (_y & 3) * 22 + (_x >> 1)    ]
                 >>> (
                       (_x & 0x1) << 3
                     )
               ) & 0xF;

    }
*/

    private boolean _checkPlayerForCollisionWithBeach()
    {
        // Checking for an invisible corridor
        if (_isSpriteOnTheCorridor(p_PlayerSprite)) return false;

        int i8_LeftTopX = p_PlayerSprite.i_ScreenX + p_PlayerSprite.i_col_offsetX;
        int i8_LeftTopY = p_PlayerSprite.i_ScreenY + p_PlayerSprite.i_col_offsetY;

        int i8_stepY = p_PlayerSprite.i_col_height / 3;
        int i8_stepX = p_PlayerSprite.i_col_width / 3;

        int i8_pY = i8_LeftTopY;

        for (int ly = 0; ly < 3; ly++)
        {
            int i8_pX = i8_LeftTopX;
            for (int lx = 0; lx < 3; lx++)
            {
                int i_elem = _getWayElementForXY(i8_pX, i8_pY);

                if (i_elem == Stages.ELEMENT_BREAKABLEBEACH || i_elem == Stages.ELEMENT_NONBREAKABLEBEACH)
                     return true;

                i8_pX += i8_stepX;
            }
            i8_pY += i8_stepY;
        }
        return false;
    }

    public void writeToStream(DataOutputStream _dataOutputStream) throws IOException
    {
        _dataOutputStream.writeInt(i_playerPower);
        _dataOutputStream.writeByte(i_Attemptions);
        _dataOutputStream.writeInt(i_nonKilledDelay);
        _dataOutputStream.writeBoolean(lg_playerEntering);
        _dataOutputStream.writeBoolean(lg_playerFinishing);

        _dataOutputStream.writeInt(i8_playerEnteringBorder);
        _dataOutputStream.writeInt(i8_backgroundOffsetStart);
        _dataOutputStream.writeInt(i8_backgroundOffsetEnd);
        _dataOutputStream.writeInt(i_curInactiveSplashIndex);
        _dataOutputStream.writeInt(i_curSplashDelay);
        _dataOutputStream.writeInt(i8_lastLineAnalysed);
        _dataOutputStream.writeInt(i_autofiredelay);
        _dataOutputStream.writeInt(i_delayToSuperRocket);

        writeSprite(_dataOutputStream, p_PlayerSprite);
        for(int li = 0; li<ap_ActiveObjects.length; li++)
             writeSprite(_dataOutputStream, ap_ActiveObjects[li]);

        for(int li = 0; li<ap_Splashes.length; li++)
             writeSprite(_dataOutputStream, ap_Splashes[li]);

        for(int li = 0; li<ap_Bombers.length; li++)
             writeSprite(_dataOutputStream, ap_Bombers[li]);

        for(int li = 0; li<ap_UserGunShells.length; li++)
             writeSprite(_dataOutputStream, ap_UserGunShells[li]);

        for(int li = 0; li<ap_EnemyShells.length; li++)
             writeSprite(_dataOutputStream, ap_EnemyShells[li]);

        writeSprite(_dataOutputStream, p_UserRocket);

    // Smartrocket's warhead variables
        if(p_WarheadTargetSprite!=null)
        {
          _dataOutputStream.writeBoolean(true);
          _dataOutputStream.writeShort(p_WarheadTargetSprite.i_spriteID);
          writeSprite(_dataOutputStream, p_WarheadTargetSprite);
          _dataOutputStream.writeInt(i_WarheadTargetSpriteType);
          _dataOutputStream.writeInt(i_WarheadTargetSpriteWidth);
          _dataOutputStream.writeInt(i_WarheadTargetSpriteHeight);
          _dataOutputStream.writeInt(i_inertion);
        }
         else
              _dataOutputStream.writeBoolean(false);
    }

    public void readFromStream(DataInputStream _dataInputStream) throws IOException
    {
        i_playerPower = _dataInputStream.readInt();
        i_Attemptions = _dataInputStream.readByte();
        i_nonKilledDelay = _dataInputStream.readInt();
        lg_playerEntering = _dataInputStream.readBoolean();
        lg_playerFinishing = _dataInputStream.readBoolean();

        i8_playerEnteringBorder = _dataInputStream.readInt();
        i8_backgroundOffsetStart = _dataInputStream.readInt();
        i8_backgroundOffsetEnd = _dataInputStream.readInt();
        i_curInactiveSplashIndex = _dataInputStream.readInt();
        i_curSplashDelay = _dataInputStream.readInt();
        i8_lastLineAnalysed = _dataInputStream.readInt();
        i_autofiredelay = _dataInputStream.readInt();
        i_delayToSuperRocket = _dataInputStream.readInt();

        readSprite(_dataInputStream, p_PlayerSprite);

        for(int li = 0; li<ap_ActiveObjects.length; li++)
             readSprite(_dataInputStream, ap_ActiveObjects[li]);

        for(int li = 0; li<ap_Splashes.length; li++)
             readSprite(_dataInputStream, ap_Splashes[li]);

        for(int li = 0; li<ap_Bombers.length; li++)
             readSprite(_dataInputStream, ap_Bombers[li]);

        for(int li = 0; li<ap_UserGunShells.length; li++)
             readSprite(_dataInputStream, ap_UserGunShells[li]);

        for(int li = 0; li<ap_EnemyShells.length; li++)
             readSprite(_dataInputStream, ap_EnemyShells[li]);

        readSprite(_dataInputStream, p_UserRocket);

    // Smartrocket's warhead variables
        if(_dataInputStream.readBoolean())
        {
		   int i_sprID = _dataInputStream.readUnsignedShort();
		   p_WarheadTargetSprite = ap_ActiveObjects[i_sprID];
			
           readSprite(_dataInputStream, p_WarheadTargetSprite);

           i_WarheadTargetSpriteType = _dataInputStream.readInt();
           i_WarheadTargetSpriteWidth = _dataInputStream.readInt();
           i_WarheadTargetSpriteHeight = _dataInputStream.readInt();
           i_inertion = _dataInputStream.readInt();
        }
    }

    private int _calculateNewStateForSmartRocket()
    {
        int i8_gx = p_UserRocket.i_mainX;
        int i8_gy = p_UserRocket.i_mainY;
        int i8_dstx = p_WarheadTargetSprite.i_mainX;
        int i8_dsty = p_WarheadTargetSprite.i_mainY;

        int d_x = i8_gx - i8_dstx;
        int d_y = i8_gy - i8_dsty;

        int i_oldState = p_UserRocket.i_ObjectState;
        int i_neededState = 0;

        if (Math.abs(d_x) <= (i_WarheadTargetSpriteWidth >>> 1))
        {
            // 0,4
            if (d_y > 0)
            {
                i_neededState = 0;
            }
            else
            {
                i_neededState = 4;
            }
        }
        else if (d_x < 0)
        {
            // 1,2,3
            if (Math.abs(d_y) <= (i_WarheadTargetSpriteHeight >>> 1))
            {
                i_neededState = 2;
            }
            else if (d_y > 0)
                i_neededState = 1;
            else
                i_neededState = 3;
        }
        else
        {
            // 5,6,7
            if (Math.abs(d_y) <= (i_WarheadTargetSpriteHeight >>> 1))
            {
                i_neededState = 6;
            }
            else if (d_y > 0)
                i_neededState = 7;
            else
                i_neededState = 5;
        }

        if (i_neededState == i_oldState) return i_oldState;

        int i_diffState = (i_neededState - i_oldState);

        if (i_diffState < 0)
        {
            if (Math.abs(i_diffState) <= 4)
                return (i_oldState - 1) & 0x7;
            else
                return (i_oldState + 1) & 0x7;
        }
        else
        {
            if (Math.abs(i_diffState) <= 4)
                return (i_oldState + 1) & 0x7;
            else
                return (i_oldState - 1) & 0x7;
        }

/*
        int i_diffState = (i_oldState + 4) & 0x7;

        if (i_diffState > i_neededState)
            return (i_oldState + 1) & 0x7;
        else
            return (i_oldState - 1) & 0x7;
*/

    }

    private int _calculateGunStateToPlayer(int _mX, int _mY, int _scrX, int _Wdth, int _x, int _y)
    {
        int i8_gx = _mX;
        int i8_gy = _mY;
        int i8_gsx = _scrX - i8_gx;
        int i8_gex = (i8_gsx + _Wdth) - i8_gx;

        _x = _x - i8_gx;
        _y = _y - i8_gy;

        // Checking of the dead zones
        if (_x < i8_gsx || _x > i8_gex)
        {
            // This is a sector
            if (_y < 0)
            {
                // 7,0,1 sectors
                if (_x < 0)
                {
                    // 0,7
                    if (_y < _x)
                        return 0;
                    else
                        return 7;
                }
                else
                {
                    // 0,1
                    if (_y < (0 - _x))
                        return 0;
                    else
                        return 1;
                }
            }
            else
            {
                // 6,5,3,2 sectors
                if (_x > 0)
                {
                    // 3,2
                    if (_x > _y)
                        return 2;
                    else
                        return 3;
                }
                else
                {
                    // 5,6
                    if ((0 - _x) > _y)
                        return 6;
                    else
                        return 5;
                }
            }
        }
        else
        {
            // This is a dead zone
            if (_y < 0)
                return 0;
            else
                return 4;
        }
    }

    public void nextGameStep(Object _playermoveobject)
    {
        _processPlayer(_processSprites());

        _processAirEnemy();
        _processPlayerShells();
        _processPlayerRocket();
        _processEnemyShells();

        if (i8_backgroundOffsetStart > 0)
        {
            if (p_PlayerSprite.i_ObjectType == PLAYER_NORMAL)
            {
                i8_backgroundOffsetStart -= I8_BACKGROUNDSPEED;
                i8_backgroundOffsetEnd -= I8_BACKGROUNDSPEED;
                i8_playerEnteringBorder -= I8_BACKGROUNDSPEED;
            }

            if (i8_backgroundOffsetStart < 0)
            {
                i8_backgroundOffsetEnd = i8_backgroundOffsetEnd - i8_backgroundOffsetStart;
                i8_playerEnteringBorder = i8_playerEnteringBorder - i8_backgroundOffsetStart;
                i8_backgroundOffsetStart = 0;
            }
            else
            {
                int i_anLine = i8_backgroundOffsetStart - 0x3000;
                if (i_anLine >= 0) _analyseLineForObjects(i_anLine);
            }
        }

        // Processing offset of all active objects
        // The player is driven
        if (p_PlayerSprite.i_ObjectType != PLAYER_EXPLOSION)
        {
            p_PlayerSprite.setMainPointXY(p_PlayerSprite.i_mainX, p_PlayerSprite.i_mainY - I8_BACKGROUNDSPEED);
        }

        if (p_PlayerSprite.i_ObjectType == PLAYER_NORMAL)
        {
            _generateAirEnemy();

            // The player is entering
            if (lg_playerEntering)
            {
                int i8_playerMainY = p_PlayerSprite.i_mainY - I8_PLAYERENTERINGSPEED;
                p_PlayerSprite.setMainPointXY(p_PlayerSprite.i_mainX, i8_playerMainY);
                if (i8_playerMainY <= i8_playerEnteringBorder) lg_playerEntering = false;
            }

            if (p_PlayerSprite.i_ScreenY <= 0) lg_playerFinishing = true;

            if (!lg_playerFinishing && ((!lg_playerEntering && i_nonKilledDelay == 0 && _checkPlayerForCollisionWithBeach()) || i_playerPower <= 0))
            {
                _initSpriteFromSpriteArray(p_PlayerSprite, PLAYER_EXPLOSION, 0);
                 Sprite p_explosion = _getFirstInactiveSprite();

                 if (p_explosion != null)
                 {
                   _initSpriteFromSpriteArray(p_explosion, EARTHEXPLOSION_NORMAL, 0);
                   p_explosion.setMainPointXY(p_PlayerSprite.i_mainX, p_PlayerSprite.i_mainY);
                 }

                p_GameActionListener.gameAction(GAMEACTION_SND_PLAYERDEATH);
            }
            else
            {
                if (i_nonKilledDelay > 0)
                {
                     i_nonKilledDelay--;
                     i_playerPower = INITPLAYERPOWER;
                }

                int i_k = i_PlayerKey;

                int i8_mx = p_PlayerSprite.i_mainX;
                int i8_my = p_PlayerSprite.i_mainY;
                int i8_sx = p_PlayerSprite.i_ScreenX;
                int i8_sy = p_PlayerSprite.i_ScreenY;

                if (!lg_playerEntering)
                {
                    if ((i_k & PLAYER_BUTTON_UP) != 0)
                    {
                        // processing player upward
                        if (i8_sy - I8_PLAYERVERTSPEED > i8_backgroundOffsetStart) i8_my -= I8_PLAYERVERTSPEED;
                    }
                    else if ((i_k & PLAYER_BUTTON_DOWN) != 0)
                    {
                        // processing player downward
                        if (i8_sy + p_PlayerSprite.i_height + I8_PLAYERVERTSPEED < i8_backgroundOffsetEnd) i8_my += I8_PLAYERVERTSPEED;
                    }
                }

                if (lg_playerFinishing)
                {
                    if (p_PlayerSprite.i_ScreenY + p_PlayerSprite.i_height < i8_backgroundOffsetStart)
                    {
                        i_PlayerState = PLAYERSTATE_WON;
                        i_GameState = GAMESTATE_OVER;
                    }
                }

                if ((i_k & PLAYER_BUTTON_RIGHT) != 0)
                {
                    if (p_PlayerSprite.i_ObjectState < (PLAYER_NORMAL_SPRITES_NUM - 1))
                    {
                        _initSpriteFromSpriteArray(p_PlayerSprite, PLAYER_NORMAL, p_PlayerSprite.i_ObjectState + 1);
                    }
                }
                else if ((i_k & PLAYER_BUTTON_LEFT) != 0)
                {
                    if (p_PlayerSprite.i_ObjectState > 0)
                    {
                        _initSpriteFromSpriteArray(p_PlayerSprite, PLAYER_NORMAL, p_PlayerSprite.i_ObjectState - 1);
                    }
                }

                if (i_autofiredelay > 0) i_autofiredelay--;

                if ((i_k & PLAYER_BUTTON_FIREGUN) != 0)
                {
                    if (i_autofiredelay <= 0)
                    {
                        // processing firegun
                        _generateUserShell();

                        i_autofiredelay = AUTOFIREDELAY;
                    }
                }
                else
                {
                   // i_autofiredelay = 0;
                }

                if ((i_k & PLAYER_BUTTON_FIREROCKET) != 0)
                {
                    // processing rocket launch
                    i_delayToSuperRocket++;
                }
                else
                {
                    if (!p_UserRocket.lg_SpriteActive)
                    {
                        if (i_delayToSuperRocket != 0)
                        {
                            if (i_delayToSuperRocket >= DELAYTOSUPERROCKET)
                            {
                                // Generation the super rocket
                                _generateUserRocket(true);
                            }
                            else
                            {
                                // Generation NURS
                                _generateUserRocket(false);
                            }
                        }
                    }
                    i_delayToSuperRocket = 0;
                }

                // Calculating player coordinates
                int i8_different = (p_PlayerSprite.i_ObjectState - (PLAYER_NORMAL_SPRITES_NUM >>> 1)) * I8_SPEEDCOEFF;

                if (i8_different < 0)
                {
                    if (i8_sx + i8_different < 0) i8_different = 0;
                }
                else
                {
                    if (i8_sx + p_PlayerSprite.i_width + i8_different > ((Stages.PARTCELLWIDTH * VIRTUALCELLWIDTH) << 8)) i8_different = 0;
                }

                i8_mx += i8_different;

                p_PlayerSprite.setMainPointXY(i8_mx, i8_my);

                i_curSplashDelay--;
                if (i_curSplashDelay <= 0 && !lg_playerFinishing)
                {
                    _activateNewSplash();
                    i_curSplashDelay = SPLASHDELAY;
                }
            }
        }
        else
        {
            if (p_PlayerSprite.processAnimation())
            {
                int i_newstate = p_PlayerSprite.i_ObjectState + 1;
                if (i_newstate >= PLAYER_EXPLOSION_SPRITES_NUM)
                {
                    i_Attemptions--;
                    if (i_Attemptions == 0)
                    {
                        i_PlayerState = PLAYERSTATE_LOST;
                        i_GameState = GAMESTATE_OVER;
                    }
                    else
                    {
                        _initPlayerCoordsWithEnteringFlag();
                    }
                }
                else
                {
                    _initSpriteFromSpriteArray(p_PlayerSprite, PLAYER_EXPLOSION, i_newstate);
                }
            }
        }
    }

    public String getGameID()
    {
        return "CMONST";
    }

    public int getMaxSizeOfSavedGameBlock()
    {
        return 2048;
    }

    private final int _getCenterCoordinateOfWaterLeft(int _i8y)
    {
        _i8y >>= 8;
        int i_row = _i8y / VIRTUALCELLHEIGHT;

        int i_blockIndex = ab_linkPartsArray[i_row / Stages.PARTCELLHEIGHT];
        int i_blockIndexOffset = (i_row % Stages.PARTCELLHEIGHT) * (Stages.PARTCELLWIDTH >>> 1);

        int i_start = -1;
        int i_end = -1;

        byte[] ab_block = abb_WayPartsArray[i_blockIndex];

        for (int li = 0; li < Stages.PARTCELLWIDTH; li++)
        {
            if (i_end >= 0) break;
            int i_elem = ab_block[i_blockIndexOffset + (li >>> 1)] & 0xFF;
            if ((li & 0x1) == 0) i_elem >>>= 4; else i_elem &= 0xF;
            switch (i_elem)
            {
                case Stages.ELEMENT_BREAKABLEBEACH:
                case Stages.ELEMENT_WATER:
                case Stages.ELEMENT_WATERMINE:
                    {
                        if (i_start < 0) i_start = li;
                    }
                    ;
                    break;
                default:
                    {
                        if (i_start >= 0)
                        {
                            i_end = li;
                        }
                    }
            }
        }


        if (i_start < 0) i_start = 0;
        if (i_end < 0) i_end = Stages.PARTCELLWIDTH;

        int i8_wdthOfSpace = ((i_end - i_start) * VIRTUALCELLWIDTH) << 8;
        int i8_start = (i_start * VIRTUALCELLWIDTH) << 8;

        return i8_start + (i8_wdthOfSpace >>> 1);
    }

    private final int _getCenterCoordinateOfWaterRight(int _i8y)
    {
        _i8y >>= 8;
        int i_row = _i8y / VIRTUALCELLHEIGHT;

        int i_blockIndex = ab_linkPartsArray[i_row / Stages.PARTCELLHEIGHT];
        int i_blockIndexOffset = (i_row % Stages.PARTCELLHEIGHT) * (Stages.PARTCELLWIDTH >>> 1);

        int i_start = -1;
        int i_end = -1;

        byte[] ab_block = abb_WayPartsArray[i_blockIndex];

        for (int li = Stages.PARTCELLWIDTH - 1; li >= 0; li--)
        {
            if (i_end >= 0) break;
            int i_elem = ab_block[i_blockIndexOffset + (li >>> 1)] & 0xFF;
            if ((li & 0x1) == 0) i_elem >>>= 4; else i_elem &= 0xF;
            switch (i_elem)
            {
                case Stages.ELEMENT_BREAKABLEBEACH:
                case Stages.ELEMENT_WATER:
                case Stages.ELEMENT_WATERMINE:
                    {
                        if (i_start < 0) i_start = li;
                    }
                    ;
                    break;
                default:
                    {
                        if (i_start >= 0)
                        {
                            i_end = li;
                        }
                    }
            }
        }

        if (i_start < 0) i_start = Stages.PARTCELLWIDTH;
        if (i_end < 0) i_end = 0;

        int i8_wdthOfSpace = ((i_start - i_end) * VIRTUALCELLWIDTH) << 8;
        int i8_start = (i_end * VIRTUALCELLWIDTH) << 8;

        return i8_start + (i8_wdthOfSpace >>> 1);
    }

    private final void _deactivateAllPlayerSplashes()
    {
        for (int li = 0; li < ap_Splashes.length; li++) ap_Splashes[li].lg_SpriteActive = false;
        i_curSplashDelay = 0;
        i_curInactiveSplashIndex = 0;
        i_delayToSuperRocket = 0;
        i_autofiredelay = 0;
    }

    private final void _activateNewSplash()
    {
        Sprite p_splash1 = ap_Splashes[i_curInactiveSplashIndex++];
        Sprite p_splash2 = ap_Splashes[i_curInactiveSplashIndex++];
        if (i_curInactiveSplashIndex == MAXIMUMSPLASHES) i_curInactiveSplashIndex = 0;

        _initSpriteFromSpriteArray(p_splash1, SPLASH_NORMAL, 0);
        _initSpriteFromSpriteArray(p_splash2, SPLASH_NORMAL, 0);

        int i8_different = (p_PlayerSprite.i_ObjectState - (PLAYER_NORMAL_SPRITES_NUM >>> 1)) * I8_SPEEDCOEFF;

        p_splash1.setMainPointXY(p_PlayerSprite.i_mainX - 0x700, p_PlayerSprite.i_mainY - i8_different);
        p_splash2.setMainPointXY(p_PlayerSprite.i_mainX + 0x700, p_PlayerSprite.i_mainY + i8_different);
    }

    private void _generateUserRocket(boolean _superRocket)
    {
        int i_mX = p_PlayerSprite.i_ScreenX + FIREPOINTS[p_PlayerSprite.i_ObjectState][0];
        int i_mY = p_PlayerSprite.i_ScreenY + FIREPOINTS[p_PlayerSprite.i_ObjectState][1];

        if (_superRocket)
        {
            // Generation the super rocket
            i_inertion = SMARTROCKETINERTION;
            p_WarheadTargetSprite = _findObjectForRocket();
            if (p_WarheadTargetSprite == null) return;

            i_WarheadTargetSpriteHeight = p_WarheadTargetSprite.i_col_height;
            i_WarheadTargetSpriteWidth = p_WarheadTargetSprite.i_col_width;
            i_WarheadTargetSpriteType = p_WarheadTargetSprite.i_ObjectType;

            _initSpriteFromSpriteArray(p_UserRocket, SMARTROCKET_NORMAL, 0);
            _setDeltaToSmartRocket();

            p_UserRocket.setMainPointXY(i_mX , i_mY);

            p_GameActionListener.gameAction(GAMEACTION_SND_PLAYERSMARTLAUNCH);
        }
        else
        {
            // Generation the NURS rocket
            int i_userState = p_PlayerSprite.i_ObjectState;

            _initSpriteFromSpriteArray(p_UserRocket, NURS_NORMAL, i_userState);

// precalculated version
            p_UserRocket.i_deltaX = NURS_VELOCITIES[p_PlayerSprite.i_ObjectState][0];
            p_UserRocket.i_deltaY = NURS_VELOCITIES[p_PlayerSprite.i_ObjectState][1];

            p_UserRocket.i_TTL = NURSTTL;

            p_UserRocket.setMainPointXY(i_mX, i_mY);

            p_GameActionListener.gameAction(GAMEACTION_SND_PLAYERNURSLAUNCH);
        }
    }

    private void _generateUserShell()
    {
        Sprite p_freeShell = _getFirstInactiveUserShell();
        if (p_freeShell == null) return;

        int i_mX = p_PlayerSprite.i_ScreenX + FIREPOINTS[p_PlayerSprite.i_ObjectState][0];
        int i_mY = p_PlayerSprite.i_ScreenY + FIREPOINTS[p_PlayerSprite.i_ObjectState][1];


        _initSpriteFromSpriteArray(p_freeShell, PLAYERSHELL_NORMAL, 0);
        p_freeShell.setMainPointXY(i_mX, i_mY);

        p_freeShell.i_deltaX = SHELL_VELOCITIES[p_PlayerSprite.i_ObjectState][0];
        p_freeShell.i_deltaY = SHELL_VELOCITIES[p_PlayerSprite.i_ObjectState][1];

        p_freeShell.i_TTL = USERSHELLTTL;

        p_GameActionListener.gameAction(GAMEACTION_SND_PLAYERFIRE);
    }


    //return true if the waterline is clear
    private boolean _checkTheWaterInTheLine(int _i8lineYCoords)
    {
        _i8lineYCoords = (_i8lineYCoords >> 8) / VIRTUALCELLHEIGHT;

        int i_blockIndex = ab_linkPartsArray[_i8lineYCoords / Stages.PARTCELLHEIGHT];
        int i_blockIndexOffset = (_i8lineYCoords % Stages.PARTCELLHEIGHT) * (Stages.PARTCELLWIDTH >>> 1);
        byte[] ab_block = abb_WayPartsArray[i_blockIndex];

        for (int li = 0; li < Stages.PARTCELLWIDTH; li++)
        {
            int i_elem = ab_block[i_blockIndexOffset + (li >>> 1)] & 0xFF;
            if ((li & 0x1) == 0) i_elem >>>= 4; else i_elem &= 0xF;

            if (i_elem == Stages.ELEMENT_BREAKABLEBEACH || i_elem == Stages.ELEMENT_WATERMINE) return false;
        }
        return true;
    }

    private void _generateAirEnemy()
    {
        if (getRandomInt(i_ActivityAir) == (i_ActivityAir >> 1))
        {
            if (ap_Bombers[0].lg_SpriteActive || ap_Bombers[1].lg_SpriteActive || ap_Bombers[2].lg_SpriteActive) return;

            int i_bomberwdth = 0;

            switch (getRandomInt(4))
            {
                case 0:
                    {
                        _initSpriteFromSpriteArray(ap_Bombers[0], BOMBER_NORMAL, 0);
                        i_bomberwdth = ap_Bombers[0].i_width;
                        ap_Bombers[0].setMainPointXY(i_bomberwdth + (i_bomberwdth >>> 1), 0);
                    }
                    ;
                    break;
                case 1:
                    {
                        _initSpriteFromSpriteArray(ap_Bombers[0], BOMBER_NORMAL, 0);
                        _initSpriteFromSpriteArray(ap_Bombers[2], BOMBER_NORMAL, 0);
                        i_bomberwdth = ap_Bombers[0].i_width;
                        int i_bomberheight = ap_Bombers[0].i_height;
                        ap_Bombers[0].setMainPointXY(i_bomberwdth + (i_bomberwdth >>> 1), 0);
                        ap_Bombers[2].setMainPointXY((i_bomberwdth << 1) + (i_bomberwdth >>> 1), i_bomberheight + (i_bomberheight >> 1));
                    }
                    ;
                    break;
                case 2:
                    {
                        _initSpriteFromSpriteArray(ap_Bombers[0], BOMBER_NORMAL, 0);
                        _initSpriteFromSpriteArray(ap_Bombers[1], BOMBER_NORMAL, 0);
                        i_bomberwdth = ap_Bombers[0].i_width;
                        int i_bomberheight = ap_Bombers[0].i_height;
                        ap_Bombers[0].setMainPointXY(i_bomberwdth + (i_bomberwdth >>> 1), 0);
                        ap_Bombers[1].setMainPointXY((i_bomberwdth >>> 1), i_bomberheight + (i_bomberheight >> 1));
                    }
                    ;
                    break;
                case 3:
                    {
                        _initSpriteFromSpriteArray(ap_Bombers[1], BOMBER_NORMAL, 0);
                        _initSpriteFromSpriteArray(ap_Bombers[2], BOMBER_NORMAL, 0);
                        i_bomberwdth = ap_Bombers[1].i_width;
                        int i_bomberheight = ap_Bombers[2].i_height;
                        ap_Bombers[1].setMainPointXY((i_bomberwdth >>> 1), i_bomberheight + (i_bomberheight >> 1));
                        ap_Bombers[2].setMainPointXY((i_bomberwdth << 1) + (i_bomberwdth >>> 1), i_bomberheight + (i_bomberheight >> 1));
                    }
                    ;
                    break;
                case 4:
                    {
                        _initSpriteFromSpriteArray(ap_Bombers[0], BOMBER_NORMAL, 0);
                        _initSpriteFromSpriteArray(ap_Bombers[1], BOMBER_NORMAL, 0);
                        _initSpriteFromSpriteArray(ap_Bombers[2], BOMBER_NORMAL, 0);
                        i_bomberwdth = ap_Bombers[0].i_width;
                        int i_bomberheight = ap_Bombers[0].i_height;
                        ap_Bombers[0].setMainPointXY(i_bomberwdth + (i_bomberwdth >>> 1), 0);
                        ap_Bombers[1].setMainPointXY((i_bomberwdth >>> 1), i_bomberheight + (i_bomberheight >> 1));
                        ap_Bombers[2].setMainPointXY((i_bomberwdth << 1) + (i_bomberwdth >>> 1), i_bomberheight + (i_bomberheight >> 1));
                    }
                    ;
                    break;
            }

            i_bomberwdth = i_bomberwdth * 3;

            int i_maxBlocks = ((Stages.PARTCELLWIDTH * VIRTUALCELLWIDTH) << 8) / i_bomberwdth;
            int i_OffsetX = ((i_maxBlocks * i_bomberwdth) >>> 1);

            int i_xoffset = i_OffsetX + ((getRandomInt(i_maxBlocks) * i_bomberwdth) << 8);
            int i_yoffset = i8_backgroundOffsetEnd;

            for (int li = 0; li < ap_Bombers.length; li++)
            {
                if (ap_Bombers[li].lg_SpriteActive)
                {
                    ap_Bombers[li].i_deltaX = AIRBOMBERPOWER;
                    ap_Bombers[li].setMainPointXY(ap_Bombers[li].i_mainX + i_xoffset, ap_Bombers[li].i_mainY + i_yoffset);
                }
            }

            p_GameActionListener.gameAction(GAMEACTION_SND_AIRBOMBERREADY);
        }
    }

    private void _processAirEnemy()
    {
        int i_playerMX = p_PlayerSprite.i_mainX;
        int i_playerMY = p_PlayerSprite.i_mainY;

        boolean lg_playerAlive = p_PlayerSprite.i_ObjectType == PLAYER_NORMAL;

        for (int li = 0; li < 3; li++)
        {
            Sprite p_spr = ap_Bombers[li];
            if (p_spr.lg_SpriteActive)
            {
                int i_mainy = p_spr.i_mainY;
                i_mainy -= BOMBERSPEED;
                p_spr.setMainPointXY(p_spr.i_mainX, i_mainy);

                if (p_spr.i_ScreenY + p_spr.i_height < i8_backgroundOffsetStart)
                {
                    p_spr.lg_SpriteActive = false;
                    continue;
                }

                boolean lg_anime = p_spr.processAnimation();
                if (p_spr.i_ObjectType == BOMBER_DROP)
                {
                    if (lg_anime)
                    {
                        if (p_spr.i_mainY >= 0)
                        {
                            p_spr.lg_SpriteActive = false;
                            Sprite p_expl = _getFirstInactiveSprite();
                            if (p_expl != null)
                            {
                                switch (_getWayElementForXY(p_spr.i_mainX, p_spr.i_mainY))
                                {
                                    case Stages.ELEMENT_WATER:
                                    case Stages.ELEMENT_WATERMINE:
                                        {
                                            _initSpriteFromSpriteArray(p_expl, WATEREXPLOSION_NORMAL, 0);
                                            p_GameActionListener.gameAction(GAMEACTION_SND_WATEREXPLOSION);
                                        }
                                        ;
                                        break;
                                    default:
                                        {
                                            _initSpriteFromSpriteArray(p_expl, EARTHEXPLOSION_NORMAL, 0);
                                            p_GameActionListener.gameAction(GAMEACTION_SND_EARTHEXPLOSION);
                                        }
                                }
                                p_expl.setMainPointXY(p_spr.i_mainX, p_spr.i_mainY);
                            }
                        }
                        p_spr.lg_SpriteActive = false;
                    }
                }
                else
                {
                    if (p_spr.i_deltaX <= 0)
                    {
                        _initSpriteFromSpriteArray(p_spr, BOMBER_DROP, 0);
                        p_GameActionListener.gameAction(GAMEACTION_SND_AIRBOMBERDROP);
                    }
                    else
                    {
                        if (lg_playerAlive)
                        {
                            if (getRandomInt(i_ActivityArtillery) == (i_ActivityArtillery >> 1))
                            {
                                Sprite p_EnemyShell = _getFirstInactiveEnemyShell();
                                if (p_EnemyShell != null)
                                {
                                    _initSpriteFromSpriteArray(p_EnemyShell, ENEMYSHELL_NORMAL, 0);
                                    p_EnemyShell.setMainPointXY(p_spr.i_mainX, p_spr.i_mainY);
                                    int i_stateSpr = _calculateGunStateToPlayer(p_spr.i_mainX, p_spr.i_mainY, p_spr.i_ScreenX, p_spr.i_width, i_playerMX, i_playerMY);
                                    _setDeltaToEnemyShell(p_EnemyShell, i_stateSpr);
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private final void _analyseLineForObjects(int _i8lineYCoords)
    {
        _i8lineYCoords = (_i8lineYCoords >> 8) / VIRTUALCELLHEIGHT;

        if (_i8lineYCoords == i8_lastLineAnalysed) return;

        int i_blockIndex = ab_linkPartsArray[_i8lineYCoords / Stages.PARTCELLHEIGHT];
        int i_blockIndexOffset = (_i8lineYCoords % Stages.PARTCELLHEIGHT) * (Stages.PARTCELLWIDTH >>> 1);
        byte[] ab_block = abb_WayPartsArray[i_blockIndex];

        for (int li = 0; li < Stages.PARTCELLWIDTH; li++)
        {
            int i_elem = ab_block[i_blockIndexOffset + (li >>> 1)] & 0xFF;
            if ((li & 0x1) == 0) i_elem >>>= 4; else i_elem &= 0xF;

            int i_indexMainSprite = -1;
            int i_indexLinkedSprite = -1;

            int i_LinkedSpriteInitState = 0;

            int i_mainPower = 0;
            boolean lg_randomFrame = false;

            switch (i_elem)
            {
                case Stages.ELEMENT_ARTILLERY1:
                    {
                        i_indexMainSprite = ARTILLERY1_NORMAL;
                        i_indexLinkedSprite = ARTILLERY1GUN_NORMAL;

                        i_LinkedSpriteInitState = getRandomInt(7);
                        i_mainPower = ARTILLERY1POWER;
                    }
                    ;
                    break;
                case Stages.ELEMENT_ARTILLERY2:
                    {
                        i_indexMainSprite = ARTILLERY2_NORMAL;
                        i_indexLinkedSprite = ARTILLERY2GUN_NORMAL;

                        i_LinkedSpriteInitState = getRandomInt(7);
                        i_mainPower = ARTILLERY2POWER;
                    }
                    ;
                    break;
                case Stages.ELEMENT_LEFTTANK:
                    {
                        i_indexMainSprite = TANKLEFT_NORMAL;
                        i_indexLinkedSprite = TANKGUN_NORMAL;

                        i_LinkedSpriteInitState = getRandomInt(7);
                        i_mainPower = TANKPOWER;
                    }
                    ;
                    break;
                case Stages.ELEMENT_RIGHTTANK:
                    {
                        i_indexMainSprite = TANKRIGHT_NORMAL;
                        i_indexLinkedSprite = TANKGUN_NORMAL;

                        i_LinkedSpriteInitState = getRandomInt(7);
                        i_mainPower = TANKPOWER;
                    }
                    ;
                    break;
                case Stages.ELEMENT_WATERMINE:
                    {
                        i_indexMainSprite = WATERMINE_NORMAL;
                        i_mainPower = WATERMINEPOWER;
                        lg_randomFrame = true;
                    }
                    ;
                    break;
                case Stages.ELEMENT_RADAR:
                    {
                        i_indexMainSprite = RADAR_NORMAL;
                        i_mainPower = RADARPOWER;
                        lg_randomFrame = true;
                    }
                    ;
                    break;
                case Stages.ELEMENT_LINEARSHEEP:
                    {
                        i_indexMainSprite = LINEARSHIP_NORMAL;
                        i_mainPower = LINEARSHIPPOWER;
                    }
                    ;
                    break;
                default:
                    continue;
            }

            Sprite p_sprMain = _getFirstInactiveSprite();
            Sprite p_linkedSprite = null;

            if (i_indexLinkedSprite >= 0)
            {
                p_sprMain.lg_SpriteActive = true;
                p_linkedSprite = _getFirstInactiveSprite();
                if (p_linkedSprite.i_spriteID < p_sprMain.i_spriteID)
                {
                    Sprite p_tmp = p_sprMain;
                    p_sprMain = p_linkedSprite;
                    p_linkedSprite = p_tmp;
                }
                _initSpriteFromSpriteArray(p_linkedSprite, i_indexLinkedSprite, i_LinkedSpriteInitState);
                p_linkedSprite.lg_linkedSprite = true;
            }

            _initSpriteFromSpriteArray(p_sprMain, i_indexMainSprite, 0);
            if(lg_randomFrame)
            {
               p_sprMain.i_Frame = getRandomInt(Math.max(p_sprMain.i_maxFrames-1,0));
            }
            p_sprMain.i_deltaX = i_mainPower;

 //!!!
// NEW OBJECT GENERATES ON CENTER OF CELL

            int i_mainY = ((_i8lineYCoords * VIRTUALCELLHEIGHT) + (VIRTUALCELLHEIGHT >>> 1)) << 8;
            int i_mainX = (li * VIRTUALCELLWIDTH) + (VIRTUALCELLWIDTH >>> 1) << 8;

/*
// NEW OBJECT GENERATES ON BOTTOM-LEFT CORNER OF CELL
            int i_mainY = ((_i8lineYCoords * VIRTUALCELLHEIGHT + VIRTUALCELLHEIGHT) << 8)
                          - p_sprMain.i_height - p_sprMain.i_offsetY;
            int i_mainX = (li * VIRTUALCELLWIDTH << 8)
                          - p_sprMain.i_offsetX;

*/

            p_sprMain.setMainPointXY(i_mainX, i_mainY);
            if (p_linkedSprite != null)
            {
                _initSpriteFromSpriteArray(p_linkedSprite, i_indexLinkedSprite, i_LinkedSpriteInitState);
                p_linkedSprite.lg_linkedSprite = true;
                p_sprMain.i_linkedSprite = p_linkedSprite.i_spriteID;
                p_linkedSprite.setMainPointXY(i_mainX, i_mainY);
            }
        }
        i8_lastLineAnalysed = _i8lineYCoords;
    }
    private final void readSprite(DataInputStream _dis, Sprite _sprite) throws IOException
    {
        _initSpriteFromSpriteArray(_sprite, _dis.readShort(), _dis.readByte());
        _sprite.readSpriteFromStream(_dis);
    }

    private final void writeSprite(DataOutputStream _dos, Sprite _sprite) throws IOException
    {
       _dos.writeShort(_sprite.i_ObjectType);
       _dos.writeByte(_sprite.i_ObjectState);
       _sprite.writeSpriteToStream(_dos);
    }

    private final void _initSpriteFromSpriteArray(Sprite _sprite, int _type, int _state)
    {
        boolean lg_oldtype = _sprite.i_ObjectType == _type;

        _sprite.i_ObjectType = _type;
        _sprite.i_ObjectState = _state;
        int i_index = _type + _state * 10;

        int i_width = ai_SpriteTable[i_index++];
        int i_height = ai_SpriteTable[i_index++];
        int i_areaXOffset = ai_SpriteTable[i_index++];
        int i_areaYOffset = ai_SpriteTable[i_index++];
        int i_areaWidth = ai_SpriteTable[i_index++];
        int i_areaHeight = ai_SpriteTable[i_index++];
        int i_frames = ai_SpriteTable[i_index++];
        int i_delay = ai_SpriteTable[i_index++];
        int i_animetype = ai_SpriteTable[i_index++];
        int i_anchor = ai_SpriteTable[i_index];

        if (!_sprite.lg_SpriteActive)
        {
            _sprite.lg_linkedSprite = false;
            _sprite.i_linkedSprite = -1;
            _sprite.lg_SpriteActive = true;
        }
        else
        {
            if (!lg_oldtype)
            {
                _sprite.lg_linkedSprite = false;
                _sprite.i_linkedSprite = -1;
            }
        }

        _sprite.setAnimation(i_animetype, i_anchor, i_width, i_height, i_frames, 0, i_delay);
        _sprite.setCollisionBounds(i_areaXOffset, i_areaYOffset, i_areaWidth, i_areaHeight);
    }

    public int getPlayerScore()
    {
        int i_stageScores = (i_GameStage * 40);
        int i_scr = i_PlayerScore + i_stageScores;

        switch (i_GameLevel)
        {
            case LEVEL0:
                {
                    i_scr = (i_scr<<1)-(i_scr>>1);
                }
                ;
                break;
            case LEVEL1:
                {
                    i_scr = (i_scr<<1);
                }
                ;
                break;
            case LEVEL2:
                {
                    i_scr = (i_scr<<1)+(i_scr>>1);
                }
                ;
                break;
        }
        return i_scr;
    }
}
