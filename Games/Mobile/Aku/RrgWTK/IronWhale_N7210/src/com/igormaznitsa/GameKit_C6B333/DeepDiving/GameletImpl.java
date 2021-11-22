package com.igormaznitsa.GameKit_C6B333.DeepDiving;

import com.igormaznitsa.gameapi.Gamelet;
import com.igormaznitsa.gameapi.GameActionListener;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.DataOutputStream;

public class GameletImpl extends Gamelet
{
//==================SOUND ACTIONS================================
    // Звук набираемого воздуха
    public static final int GAMEACTION_SOUND_TAKEOXYGEN = 0;
    // Взрыв
    public static final int GAMEACTION_SOUND_EXPLOSION = 1;
    // Старт торпеды
    public static final int GAMEACTION_SOUND_TORPEDESTART = 2;
    // Старт ракеты
    public static final int GAMEACTION_SOUND_ROCKETSTART = 3;
    // Всплеск
    public static final int GAMEACTION_SOUND_SPLASH = 4;
    // Скрежет
    public static final int GAMEACTION_SOUND_GNASH = 5;
    // Бульканье
    public static final int GAMEACTION_SOUND_GURGLING = 6;

    // Изменение состояния мины
    public static final int GAMEACTION_MINE_STATE_CHANGED = 7;
//===============================================================

//=====================DRIVING KEYS==============================
    public static final int PLAYER_BUTTON_NONE = 0;
    public static final int PLAYER_BUTTON_LEFT = 1;
    public static final int PLAYER_BUTTON_RIGHT = 2;
    public static final int PLAYER_BUTTON_TOP = 4;
    public static final int PLAYER_BUTTON_DOWN = 8;
    public static final int PLAYER_BUTTON_TORPEDOFIRE = 16;
    public static final int PLAYER_BUTTON_ROCKETFIRE = 32;
//================================================================

    public static final int GAMELEVEL_EASY = 0;
    public static final int GAMELEVEL_NORMAL = 1;
    public static final int GAMELEVEL_HARD = 2;

    private static final int GAMELEVEL_EASY_TIMEDELAY = 100;
    private static final int GAMELEVEL_NORMAL_TIMEDELAY = 85;
    private static final int GAMELEVEL_HARD_TIMEDELAY = 70;

    private static final int GAMELEVEL_EASY_ATTEMPTIONS = 3;
    private static final int GAMELEVEL_NORMAL_ATTEMPTIONS = 4;
    private static final int GAMELEVEL_HARD_ATTEMPTIONS = 4;

    private static final int GAMELEVEL_EASY_SHIPFREQ = 3;
    private static final int GAMELEVEL_EASY_SHIPFIREFREQ = 100;
    private static final int GAMELEVEL_NORMAL_SHIPFREQ = 60;
    private static final int GAMELEVEL_NORMAL_SHIPFIREFREQ = 4;
    private static final int GAMELEVEL_HARD_SHIPFREQ = 4;
    private static final int GAMELEVEL_HARD_SHIPFIREFREQ = 30;

    private static final int GAMELEVEL_EASY_HELICOPFREQ = 3;
    private static final int GAMELEVEL_EASY_HELICOPFIREFREQ = 100;
    private static final int GAMELEVEL_NORMAL_HELICOPFREQ = 4;
    private static final int GAMELEVEL_NORMAL_HELICOPFIREFREQ = 60;
    private static final int GAMELEVEL_HARD_HELICOPFREQ = 4;
    private static final int GAMELEVEL_HARD_HELICOPFIREFREQ = 30;

    private static final int GAMELEVEL_EASY_ICEBERGFREQ = 3;
    private static final int GAMELEVEL_NORMAL_ICEBERGFREQ = 4;
    private static final int GAMELEVEL_HARD_ICEBERGFREQ = 4;

    private static final int GAMELEVEL_EASY_MINEFREQ = 3;
    private static final int GAMELEVEL_NORMAL_MINEFREQ = 4;
    private static final int GAMELEVEL_HARD_MINEFREQ = 4;

    public static final int I8_MAX_OXYGEN_VALUE = 100 << 8;
    private static final int I8_SPEED_OXYGEN_DECREASE = 0x30;
    private static final int I8_SPEED_OXYGEN_INCREASE = 0x100;

    private static final int I8_INERTIA_DAMPER = 0x15;

    private static final int I8_PLAYER_MAX_HORZSPEED = 0x300;
    private static final int I8_PLAYER_HORZSPEED_INCREASE = 0x35;
    private static final int I8_PLAYER_HORZSPEED_DECREASE = 0x35;

    private static final int I8_PLAYER_MAX_VERTSPEED = 0x200;
    private static final int I8_PLAYER_VERTSPEED_INCREASE = 0x25;
    private static final int I8_PLAYER_VERTSPEED_DECREASE = 0x25;

    public static final int INIT_PLAYER_HEALTH = 120;
    public static final int INIT_BOSS_HEALTH = 100;

    private static final int I8_CHOPPERLEFTHORZSPEED = 0x100;
    private static final int I8_CHOPPERRIGHTHORZSPEED = 0x300;
    private static final int I8_SURFACESHIPHORZSPEED = 0x100;
    private static final int I8_AIRCRAFTCARRIERHORZSPEED = 0x100;
    private static final int I8_BLEBSVERTSPEED = 0x100;
    private static final int I8_TORPEDOSPEED = 0x300;
    private static final int I8_ROCKETVSPEED = 0x300;
    private static final int I8_ROCKETHSPEED = 0x300;
    private static final int I8_SURFACEBOMBVSPEED = 0x100;
    private static final int I8_UNDERWATERBOMBVSPEED = 0x80;
    private static final int I8_MINEVERTSPEED = 0x90;

    private static final int I_ICEBERG_HEALTH = 100;
    private static final int I_BLEBFREQ = 250;
    private static final int I8_GENERATION_STEP = 0xA00;

//-------------------------------------------------------------------------------------------------------------
    public static final int I8_SEASURFACE_OFFSET = 0x4000; // sea-surface offset
    public static final int I_GROUND_OFFSET = 0;          // ground lift , it lift ground onto these points

    private static final int I8_HIGHCHOPPERBORDER = 0x300; // copter's tunnel
    private static final int I8_LOWCHOPPERBORDER = 0x2000; //
    private static final int I8_CHOPPERVERTSPEED = 0x70;
//-------------------------------------------------------------------------------------------------------------


    private static final int I_BOSS_FIRE_FREQ = 30;
    //========================================
    public static final int SPRITES_NUMBER = 30;

    private int i8_player_HorzSpeed;
    private int i8_player_VertSpeed;

    public int i8_player_OxygenValue;
    public int i_playerHealth;
    public int i8_bossStartPosition;
    public int i_Attemptions;

    public int i_PlayerKey;

    private static final int I_PLAYER_DEATHTICKS = 30;
    private int i_playerKilledCounter;
    public boolean lg_PlayerKilled;
    public boolean lg_BossKilled;
    private boolean lg_BossSeen;
    public boolean lg_bossDrivingMode;
    private int i_bossKilledCounter;

    private int i8_generationStep;
    private int i8_generationStep2;
    private int i_shipgenfreq;
    private int i_shipfirefreq;
    private int i_choppergenfreq;
    private int i_chopperfirefreq;
    private int i_iceberggenfreq;
    private int i_minegenfreq;

    private int i_indexSunkShip;
    private int i8_nextSunkShipX,i8_nextSunkShipY,i_nextSunkShipType;

    private int i8_torpede_speed;

    private static final int I8_INIT_PLAYER_X = 0x2000;
    private static final int I8_INIT_PLAYER_Y = I8_SEASURFACE_OFFSET-0x400;

    public int i_GameTimeDelay;
    public int i_BossHealth;

    //==================Unique sprites============================
    public Sprite p_PlayerSprite;
    public Sprite p_TorpedoSprite;
    public Sprite p_RocketSprite;

    public int i8_startViewScreen;
    public int i8_endViewScreen;
    private int i8_screenWidth;
    private int i_bossSpriteId;

    public Sprite[] ap_Sprites;

    private Sprite getFirstInactiveSprite()
    {
        for (int li = 0; li < SPRITES_NUMBER; li++)
        {
            if (!ap_Sprites[li].lg_SpriteActive) return ap_Sprites[li];
        }
        return null;
    }

    private void deactivateAllSprites(boolean _deactivateSunkShips)
    {
        for (int li = 0; li < SPRITES_NUMBER; li++)
        {
            if (_deactivateSunkShips)
                ap_Sprites[li].lg_SpriteActive = false;
            else
                switch (ap_Sprites[li].i_objectType)
                {
                    case Sprite.SPRITE_AIRCRAFTCARRIER:
                    case Sprite.SPRITE_SUNKSHIP1:
                    case Sprite.SPRITE_SUNKSHIP2:
                    case Sprite.SPRITE_SUNKSHIP3:
                    case Sprite.SPRITE_SUNKSHIP4:
                        continue;
                    default :
                        ap_Sprites[li].lg_SpriteActive = false;
                }
        }
    }

    public GameletImpl(int _screenWidth, int _screenHeight, GameActionListener _gameActionListener)
    {
        super(_screenWidth, _screenHeight, _gameActionListener);
        i8_screenWidth = _screenWidth << 8;
    }

    public String getGameID()
    {
        return "DeePDiViNG";
    }

    public int getMaxSizeOfSavedGameBlock()
    {
        return 1024;
    }

    private boolean checkBottomCollided(Sprite _sprite)
    {
        int i_screenY = ((_sprite.i8_ScreenY - I8_SEASURFACE_OFFSET) >> 8 ) + I_GROUND_OFFSET;
        int i_screenX = (_sprite.i8_ScreenX + _sprite.i8_col_offsetX) >>> 8;
        int i_endx = (_sprite.i8_col_width >>> 8) + i_screenX;

        if (i_screenY < 0) return false;

        long l_mask = _sprite.l_Mask >>> i_screenY;
        for (int lx = i_screenX; lx < i_endx; lx++)
        {
            long l_element = getLineElement(lx);
            if ((l_mask & l_element) != 0) return true;
        }
        return false;
    }

    public void newGameSession(int _level)
    {
        super.initLevel(_level);

        ap_Sprites = new Sprite[SPRITES_NUMBER];
        lg_BossKilled = false;
        lg_BossSeen = false;
        lg_bossDrivingMode = false;
        i_BossHealth = INIT_BOSS_HEALTH;

	i8_startViewScreen = 0;
        i8_endViewScreen = i8_startViewScreen + i8_screenWidth;


        for (int li = 0; li < SPRITES_NUMBER; li++) ap_Sprites[li] = new Sprite(li);

        switch (_level)
        {
            case GAMELEVEL_EASY:
                {
                    i_shipgenfreq = GAMELEVEL_EASY_SHIPFREQ;
                    i_shipfirefreq = GAMELEVEL_EASY_SHIPFIREFREQ;
                    i_choppergenfreq = GAMELEVEL_EASY_HELICOPFREQ;
                    i_chopperfirefreq = GAMELEVEL_EASY_HELICOPFIREFREQ;
                    i_minegenfreq = GAMELEVEL_EASY_MINEFREQ;
                    i_iceberggenfreq = GAMELEVEL_EASY_ICEBERGFREQ;
                    i_Attemptions = GAMELEVEL_EASY_ATTEMPTIONS;
                    i_GameTimeDelay = GAMELEVEL_EASY_TIMEDELAY;
                }
                ;
                break;
            case GAMELEVEL_NORMAL:
                {
                    i_shipgenfreq = GAMELEVEL_NORMAL_SHIPFREQ;
                    i_shipfirefreq = GAMELEVEL_NORMAL_SHIPFIREFREQ;
                    i_choppergenfreq = GAMELEVEL_NORMAL_HELICOPFREQ;
                    i_chopperfirefreq = GAMELEVEL_NORMAL_HELICOPFIREFREQ;
                    i_minegenfreq = GAMELEVEL_NORMAL_MINEFREQ;
                    i_iceberggenfreq = GAMELEVEL_NORMAL_ICEBERGFREQ;
                    i_Attemptions = GAMELEVEL_NORMAL_ATTEMPTIONS;
                    i_GameTimeDelay = GAMELEVEL_NORMAL_TIMEDELAY;
                }
                ;
                break;
            case GAMELEVEL_HARD:
                {
                    i_shipgenfreq = GAMELEVEL_HARD_SHIPFREQ;
                    i_shipfirefreq = GAMELEVEL_HARD_SHIPFIREFREQ;
                    i_choppergenfreq = GAMELEVEL_HARD_HELICOPFREQ;
                    i_chopperfirefreq = GAMELEVEL_HARD_HELICOPFIREFREQ;
                    i_minegenfreq = GAMELEVEL_HARD_MINEFREQ;
                    i_iceberggenfreq = GAMELEVEL_HARD_ICEBERGFREQ;
                    i_Attemptions = GAMELEVEL_HARD_ATTEMPTIONS;
                    i_GameTimeDelay = GAMELEVEL_HARD_TIMEDELAY;
                }
                ;
                break;
        }
        i_indexSunkShip = 0;
        p_PlayerSprite = new Sprite(0);
        p_TorpedoSprite = new Sprite(0);
        p_RocketSprite = new Sprite(0);
        p_PlayerSprite.setMainXY8(I8_INIT_PLAYER_X, I8_INIT_PLAYER_Y);
        nextSunkShip();
        resumeGameAfterPlayerLost();
    }

    private void nextSunkShip()
    {
        int i_indx = i_indexSunkShip * 3;
	if(Stage.ai8_SunkShipsXYT.length<i_indx+3) return;
        i8_nextSunkShipX = Stage.ai8_SunkShipsXYT[i_indx++];
        i8_nextSunkShipY = Stage.ai8_SunkShipsXYT[i_indx++];
	if(i8_nextSunkShipY>I8_SEASURFACE_OFFSET)i8_nextSunkShipY -= I_GROUND_OFFSET<<8;
        i_nextSunkShipType = Stage.ai8_SunkShipsXYT[i_indx];
        i_indexSunkShip++;
    }

    public void endGameSession()
    {
        for (int li = 0; li < SPRITES_NUMBER; li++) ap_Sprites[li] = null;
        ap_Sprites = null;
        super.endGameSession();
    }

    public void resumeGameAfterPlayerLost()
    {
        i_PlayerKey = PLAYER_BUTTON_NONE;
        deactivateAllSprites(false);
        i_PlayerState = PLAYERSTATE_NORMAL;
        i_GameState = GAMESTATE_PLAYED;
        p_PlayerSprite.loadSprite(Sprite.SPRITE_SUBMARINE);
        i8_generationStep = 0;
        p_PlayerSprite.setMainXY8(p_PlayerSprite.i8_mainX, I8_INIT_PLAYER_Y);
        i_playerHealth = INIT_PLAYER_HEALTH;
        i8_player_OxygenValue = I8_MAX_OXYGEN_VALUE;
        i_playerKilledCounter = 0;
        lg_PlayerKilled = false;

        if (lg_bossDrivingMode)
        {
            Sprite p_boss = ap_Sprites[i_bossSpriteId];
            p_boss.setMainXY8(i8_endViewScreen - p_boss.i8_width, p_boss.i8_mainY);
            p_PlayerSprite.setMainXY8(i8_startViewScreen, I8_INIT_PLAYER_Y);
        }
    }

    private void generateObject()
    {
        if (!lg_BossSeen)
        {
            if (i8_endViewScreen >= Stage.I8_BOSS_START_X)
            {
                Sprite p_spr = getFirstInactiveSprite();
                if (p_spr != null)
                {
                    p_spr.loadSprite(Sprite.SPRITE_AIRCRAFTCARRIER);
                    p_spr.setMainXY8(i8_endViewScreen, I8_SEASURFACE_OFFSET - p_spr.i8_MaxSurfacing);
                    p_spr.i_param0 = 0;
                    lg_BossSeen = true;
                    i_bossSpriteId = p_spr.i_SpriteID;
                }
            }
        }

        if (i8_endViewScreen >= i8_nextSunkShipX)
        {
            Sprite p_spt = getFirstInactiveSprite();
            if (p_spt != null)
            {
                p_spt.loadSprite(i_nextSunkShipType);
                p_spt.setMainXY8(i8_nextSunkShipX, i8_nextSunkShipY);
                nextSunkShip();
            }
        }

        boolean lg_generate = (i8_generationStep >= I8_GENERATION_STEP);
        if (lg_generate) i8_generationStep = 0;

        if (getRandomInt(1) == 1)
        {
            switch (getRandomInt(399) / 100)
            {
                case 0:
                    {
                        if (!lg_BossSeen && i8_generationStep2 >= I8_GENERATION_STEP)
                        {
                            // Ship
                            if (getRandomInt(i_shipgenfreq) == (i_shipgenfreq >> 1))
                            {
                                Sprite p_spr = getFirstInactiveSprite();
                                if (p_spr == null) return;
                                p_spr.loadSprite(Sprite.SPRITE_SUFRACESHIP);
                                int i8_y = I8_SEASURFACE_OFFSET - p_spr.i8_MaxSurfacing;
                                int i8_x = i8_endViewScreen + 0x100;
                                p_spr.setMainXY8(i8_x, i8_y);
                            }
                            i8_generationStep2 = 0;
                        }
                    }
                    ;
                    break;
                case 1:
                    {
                        if (i8_generationStep2 >= I8_GENERATION_STEP)
                        {
                            // Chopper
                            if (getRandomInt(i_choppergenfreq) == (i_choppergenfreq >> 1))
                            {
                                Sprite p_spr = getFirstInactiveSprite();
                                if (p_spr == null) return;

                                int i8_y = I8_HIGHCHOPPERBORDER + getRandomInt(I8_LOWCHOPPERBORDER - I8_HIGHCHOPPERBORDER);
                                int i8_x;

                                if (getRandomInt(100) >= 50)
                                {
                                    // Left Chopper
                                    p_spr.loadSprite(Sprite.SPRITE_CHOPPERLEFT);
                                    i8_x = i8_endViewScreen + 0x100;
                                }
                                else
                                {
                                    // Right Chopper
                                    p_spr.loadSprite(Sprite.SPRITE_CHOPPERRIGHT);
                                    i8_x = i8_startViewScreen - p_spr.i8_width-0x100;
                                }
                                p_spr.setMainXY8(i8_x, i8_y);
                            }
                            i8_generationStep2 = 0;
                        }
                    }
                    ;
                    break;
                case 2:
                    {
                        if (lg_BossSeen || !lg_generate) return;
                        // Iceberg
                        if (getRandomInt(i_iceberggenfreq) == (i_iceberggenfreq >> 1))
                        {
                            Sprite p_spr = getFirstInactiveSprite();
                            if (p_spr == null) return;

                            p_spr = getFirstInactiveSprite();
                            if (p_spr == null) return;
                            p_spr.loadSprite(Sprite.SPRITE_ICEBERG);
			    p_spr.i_param0 = I_ICEBERG_HEALTH;
                            int i8_y = I8_SEASURFACE_OFFSET - p_spr.i8_MaxSurfacing;
                            int i8_x = i8_endViewScreen + 0x100;
                            p_spr.setMainXY8(i8_x, i8_y);
                        }
                    }
                    ;
                    break;
                default:
                    {

                        if (getRandomInt(I_BLEBFREQ) == (I_BLEBFREQ >> 1))
                        {
                            Sprite p_spr = getFirstInactiveSprite();
                            if (p_spr == null) return;
                            p_spr.loadSprite(Sprite.SPRITE_BLEBS);
                            p_spr.setMainXY8(i8_endViewScreen, I8_SEASURFACE_OFFSET + (getDepthAtWayPoint((i8_endViewScreen >>> 8) + 1)<<8));
                        }

                        if (lg_BossSeen || !lg_generate) return;
                        // Mine
                        if (getRandomInt(i_minegenfreq) == (i_minegenfreq >> 1)) //
                        {
                            Sprite p_spr = getFirstInactiveSprite();
                            if (p_spr == null) return;

                            int i_depth = getDepthAtWayPoint((i8_endViewScreen >>> 8) + 1);
                            i_depth = (64 - i_depth) << 8;
                            p_spr.loadSprite(Sprite.SPRITE_MINE);
                            if (p_spr.i8_height >= (i_depth << 1))
                            {
                                p_spr.lg_SpriteActive = false;
                            }
                            else
                            {
                                int i8_diapason = i_depth - p_spr.i8_height;
                                int i8_y = getRandomInt(i8_diapason / 0x300) * 0x300;
				if(i8_y<p_spr.i8_height) i8_y = p_spr.i8_height;
                                p_spr.i_param1 = (i_depth - i8_y) >>> 8;
                                p_spr.i_param0 = 0;
                                p_spr.setMainXY8(i8_endViewScreen + 0x100, I8_SEASURFACE_OFFSET + i8_y);
                            }
                        }
                    }
            }
        }
    }

    private void convertObjectToExplosion(Sprite _sprite, int _explType)
    {
        int i_explNumber = 0;
        switch (_sprite.i_objectType)
        {
            case Sprite.SPRITE_SUBMARINE:
                {
                    i_explNumber = 5;
                }
                ;
                break;
            case Sprite.SPRITE_AIRCRAFTCARRIER:
                {
                    i_explNumber = 8;
                }
                ;
                break;
            case Sprite.SPRITE_ICEBERG:
                {
                    i_explNumber = 3;
                }
                ;
                break;
            case Sprite.SPRITE_SUFRACESHIP:
                {
                    i_explNumber = 2;
                }
                ;
                break;
            case Sprite.SPRITE_SUNKSHIP1:
                {
                    i_explNumber = 3;
                }
                ;
                break;
            case Sprite.SPRITE_SUNKSHIP2:
                {
                    i_explNumber = 3;
                }
                ;
                break;
            case Sprite.SPRITE_SUNKSHIP3:
                {
                    i_explNumber = 3;
                }
                ;
                break;
            case Sprite.SPRITE_SUNKSHIP4:
                {
                    i_explNumber = 3;
                }
                ;
                break;
            case Sprite.SPRITE_CHOPPERLEFT:
            case Sprite.SPRITE_CHOPPERRIGHT:
                {
                    i_explNumber = 1;
                }
                ;
                break;
            case Sprite.SPRITE_MINE:
                {
                    i_explNumber = 1;
                }
                ;
                break;
            case Sprite.SPRITE_ROCKET:
            case Sprite.SPRITE_TORPEDO:
            case Sprite.SPRITE_UNDERWATERBOMB:
                {
                    i_explNumber = 1;
                }
                ;
                break;
            case Sprite.SPRITE_SURFACEBOMB:
                {
                    i_explNumber = 1;
                }
                ;
                break;
        }

        int i8_stx = _sprite.i8_ScreenX;
        int i8_sty = _sprite.i8_ScreenY;
        int i8_wdth = _sprite.i8_width;
        int i8_height = _sprite.i8_height;

        for (int li = 0; li < i_explNumber; li++)
        {
            Sprite p_spr = getFirstInactiveSprite();
            if (p_spr == null) break;
            int i_x = i8_stx + (getRandomInt(i8_wdth / 0x300) * 0x300);
            int i_y = 0;
            if (_explType == Sprite.SPRITE_EXPLOSIONSURFACE)
            {
                i_y = I8_SEASURFACE_OFFSET;
            }
            else
            {
                i_y = i8_sty + (getRandomInt(i8_height / 0x300) * 0x300);
            }

            p_spr.loadSprite(_explType);
            p_spr.setMainXY8(i_x, i_y);
        }

        if (p_GameActionListener!=null) p_GameActionListener.gameAction(GAMEACTION_SOUND_EXPLOSION);
    }

    private void processTorpedo()
    {
        if (!p_TorpedoSprite.lg_SpriteActive) return;
        if (checkBottomCollided(p_TorpedoSprite))
        {
            convertObjectToExplosion(p_TorpedoSprite, Sprite.SPRITE_EXPLOSIONUNDERWATER);
            p_TorpedoSprite.lg_SpriteActive = false;
        }
        else
        {
            p_TorpedoSprite.setMainXY8(p_TorpedoSprite.i8_mainX + i8_torpede_speed, p_TorpedoSprite.i8_mainY);
            if (p_TorpedoSprite.i8_ScreenX > i8_endViewScreen)
            {
                p_TorpedoSprite.lg_SpriteActive = false;
            }
        }
    }

    private void processRocket()
    {
        if (!p_RocketSprite.lg_SpriteActive) return;
        if (checkBottomCollided(p_RocketSprite))
        {
            convertObjectToExplosion(p_RocketSprite, Sprite.SPRITE_EXPLOSIONUNDERWATER);
            p_RocketSprite.lg_SpriteActive = false;
        }
        else
        {
            p_RocketSprite.setMainXY8(p_RocketSprite.i8_mainX + I8_ROCKETHSPEED, p_RocketSprite.i8_mainY - I8_ROCKETVSPEED);
            if (p_RocketSprite.i8_ScreenX > i8_endViewScreen)
            {
                p_RocketSprite.lg_SpriteActive = false;
            }
            else if (p_RocketSprite.i8_ScreenY + p_RocketSprite.i8_height < 0)
            {
                p_RocketSprite.lg_SpriteActive = false;
            }
        }
    }

    private Sprite processSprites()
    {
        Sprite p_collided = null;
        Sprite p_torpedoCollided = null;
        Sprite p_rocketCollided = null;

        int i8_px = p_PlayerSprite.i8_ScreenX;
        int i8_pw = p_PlayerSprite.i8_width;
        int i8_ex = i8_px + i8_pw;

        boolean boat_is_seen = false;
        if (p_PlayerSprite.i8_ScreenY < I8_SEASURFACE_OFFSET)
            boat_is_seen = true;

        for (int li = 0; li < SPRITES_NUMBER; li++)
        {
            Sprite p_spr = ap_Sprites[li];
            if (p_spr.lg_SpriteActive)
            {
                boolean lg_finalFrame = p_spr.processAnimation();
                int i8_x = p_spr.i8_mainX;
                int i8_y = p_spr.i8_mainY;
                int i8_w = p_spr.i8_width;
                int i8_h = p_spr.i8_height;

                boolean over_boat = true;


                if (p_spr.i8_ScreenX + i8_w < i8_px || i8_ex < p_spr.i8_ScreenX) over_boat = false;


                switch (p_spr.i_objectType)
                {
                    case Sprite.SPRITE_EXPLOSIONAIR:
                        {
                            p_spr.setMainXY8(p_spr.i8_mainX, p_spr.i8_mainY + 0x60);
                        }
                    case Sprite.SPRITE_EXPLOSIONSURFACE:
                    case Sprite.SPRITE_EXPLOSIONUNDERWATER:
                        {
                            if (lg_finalFrame)
                            {
                                p_spr.lg_SpriteActive = false;
                                continue;
                            }
                        }
                        ;
                        break;
                    case Sprite.SPRITE_AIRCRAFTCARRIER:
                        {
                            if (lg_BossKilled)
                            {
                                i8_y += 0x60;
                            }
                            else
                            {
                                if (p_spr.i8_ScreenX + i8_w < i8_endViewScreen)
                                {
                                    lg_bossDrivingMode = true;
                                }

                                if (lg_bossDrivingMode)
                                {
                                    int i_par = p_spr.i_param0;
                                    if (boat_is_seen)
                                    {
                                        if (p_PlayerSprite.i8_mainX < i8_x)
                                        {
                                            i_par = 0;
                                        }
                                        else
                                        {
                                            i_par = -1;
                                        }
                                        p_spr.i_param0 = i_par;
                                    }

                                    if (i_par == 0)
                                    {
                                        // To left
                                        i8_x -= I8_AIRCRAFTCARRIERHORZSPEED;
                                        if (i8_x <= i8_startViewScreen)
                                        {
                                            i8_x = i8_startViewScreen;
                                            p_spr.i_param0 = -1;
                                        }
                                    }
                                    else
                                    {
                                        // To right
                                        i8_x += I8_AIRCRAFTCARRIERHORZSPEED;
                                        if (i8_x + i8_w >= i8_endViewScreen)
                                        {
                                            i8_x = i8_endViewScreen - i8_w;
                                            p_spr.i_param0 = 0;
                                        }
                                    }

                                    p_spr.setMainXY8(i8_x, i8_y);

                                    int i_fire = I_BOSS_FIRE_FREQ;

                                    if (over_boat) i_fire >>>= 1;

                                    if (getRandomInt(i_fire) == (i_fire >>> 1))
                                    {
                                        Sprite p_bomb = getFirstInactiveSprite();
                                        if (p_bomb != null)
                                        {
                                            int i8_cx = 0;
                                            switch (getRandomInt(29) / 10)
                                            {
                                                case 0: // Center
                                                    {
                                                        i8_cx = i8_x + (i8_w >>> 1);
                                                    }
                                                    ;
                                                    break;
                                                case 1: // Left
                                                    {
                                                        i8_cx = i8_x;
                                                    }
                                                    ;
                                                    break;
                                                case 2: // Right
                                                    {
                                                        i8_cx = i8_x + i8_w;
                                                    }
                                                    ;
                                                    break;
                                            }
                                            p_bomb.loadSprite(Sprite.SPRITE_UNDERWATERBOMB);
                                            p_bomb.setMainXY8(i8_cx, I8_SEASURFACE_OFFSET + 0x20);
                                        }
                                    }
                                }
                                else
                                {
                                    i8_x -= (I8_AIRCRAFTCARRIERHORZSPEED<<1);
                                    p_spr.setMainXY8(i8_x, p_spr.i8_mainY);
                                }
                            }
                        }
                        ;
                        break;
                    case Sprite.SPRITE_BLEBS:
                        {
                            i8_y -= I8_BLEBSVERTSPEED;
                            if (i8_y <= I8_SEASURFACE_OFFSET)
                            {
                                p_spr.lg_SpriteActive = false;
                                continue;
                            }
                            p_spr.setMainXY8(i8_x, i8_y);
                            continue;
                        }
                    case Sprite.SPRITE_CHOPPERLEFT:
                        {
                            int i8_scr = p_spr.i8_ScreenY;
                            if (p_spr.i_param0 == 0)
                            {
                                if (i8_scr > I8_HIGHCHOPPERBORDER)
                                {
                                    i8_y -= I8_CHOPPERVERTSPEED;
                                }
                                else
                                {
                                    p_spr.i_param0 = -1;
                                }
                            }
                            else
                            {
                                if (i8_scr + i8_h < I8_LOWCHOPPERBORDER)
                                {
                                    i8_y += I8_CHOPPERVERTSPEED;
                                }
                                else
                                {
                                    p_spr.i_param0 = 0;
                                }
                            }
                            i8_x -= I8_CHOPPERLEFTHORZSPEED;

                            p_spr.setMainXY8(i8_x, i8_y);

                            if ((p_spr.i8_ScreenX + i8_w) < i8_startViewScreen)
                            {
                                p_spr.lg_SpriteActive = false;
                                continue;
                            }

                            int i_fire = i_chopperfirefreq;
                            if (over_boat) i_fire >>>= 1;
                            if (over_boat && boat_is_seen) i_fire >>>= 1;

                            if (getRandomInt(i_fire) == (i_fire >>> 1))
                            {
                                Sprite p_Spr = getFirstInactiveSprite();
                                if (p_Spr != null)
                                {
                                    p_Spr.loadSprite(Sprite.SPRITE_SURFACEBOMB);
                                    p_Spr.setMainXY8(i8_x + (i8_w >>> 1), i8_y + i8_h);
                                }
                            }
                        }
                        ;
                        break;
                    case Sprite.SPRITE_CHOPPERRIGHT:
                        {
                            int i8_scr = p_spr.i8_ScreenY;
                            if (p_spr.i_param0 == 0)
                            {
                                if (i8_scr + i8_h < I8_LOWCHOPPERBORDER)
                                {
                                    i8_y += I8_CHOPPERVERTSPEED;
                                }
                                else
                                {
                                    p_spr.i_param0 = -1;
                                }
                            }
                            else
                            {
                                if (i8_scr > I8_HIGHCHOPPERBORDER)
                                {
                                    i8_y -= I8_CHOPPERVERTSPEED;
                                }
                                else
                                {
                                    p_spr.i_param0 = 0;
                                }
                            }

                            i8_x += I8_CHOPPERRIGHTHORZSPEED;
                            p_spr.setMainXY8(i8_x, i8_y);

                            if (p_spr.i8_ScreenX > i8_endViewScreen)
                            {
                                p_spr.lg_SpriteActive = false;
                                continue;
                            }

                            int i_fire = i_chopperfirefreq;
                            if (over_boat) i_fire >>>= 1;
                            if (over_boat && boat_is_seen) i_fire >>>= 1;

                            if (getRandomInt(i_fire) == (i_fire >>> 1))
                            {
                                Sprite p_Spr = getFirstInactiveSprite();
                                if (p_Spr != null)
                                {
                                    p_Spr.loadSprite(Sprite.SPRITE_SURFACEBOMB);
                                    p_Spr.setMainXY8(i8_x + (i8_w >>> 1), i8_y + i8_h);
                                }
                            }

                        }
                        ;
                        break;
                    case Sprite.SPRITE_ICEBERG:
                        {
                            p_spr.setMainXY8(i8_x, i8_y);
                        }
                        ;
                        break;
                    case Sprite.SPRITE_MINE:
                        {
                            if (p_spr.i_param0 == 0)
                            {
                                if (getRandomInt(100) == 80) p_spr.i_param0 = -1;
                                if (p_GameActionListener != null)
                                    p_GameActionListener.gameAction(GAMEACTION_MINE_STATE_CHANGED, p_spr.i_SpriteID);
                            }
                            else
                            {
                                i8_y -= I8_MINEVERTSPEED;
                                p_spr.setMainXY8(i8_x, i8_y);

                                if ((p_spr.i8_ScreenY + p_spr.i8_MaxSurfacing) < I8_SEASURFACE_OFFSET)
                                {
                                    i8_y = I8_SEASURFACE_OFFSET + p_spr.i8_height - p_spr.i8_MaxSurfacing;
                                    p_spr.setMainXY8(i8_x, i8_y);
                                }
                            }

                            if (getRandomInt(500) == 400)
                            {
                                p_spr.lg_SpriteActive = false;
                                if (p_spr.i8_ScreenY < I8_SEASURFACE_OFFSET)
                                    convertObjectToExplosion(p_spr, Sprite.SPRITE_EXPLOSIONSURFACE);
                                else
                                    convertObjectToExplosion(p_spr, Sprite.SPRITE_EXPLOSIONUNDERWATER);
                                if (p_GameActionListener != null)
                                    p_GameActionListener.gameAction(GAMEACTION_MINE_STATE_CHANGED, p_spr.i_SpriteID);
                            }
                        }
                        ;
                        break;
                    case Sprite.SPRITE_SUNKSHIP1:
                    case Sprite.SPRITE_SUNKSHIP2:
                    case Sprite.SPRITE_SUNKSHIP3:
                    case Sprite.SPRITE_SUNKSHIP4:
                        {
                            if ((p_spr.i8_ScreenX + i8_w) < i8_startViewScreen)
                            {
                                p_spr.lg_SpriteActive = false;
                                continue;
                            }
                        }
                        ;
                        break;
                    case Sprite.SPRITE_SURFACEBOMB:
                        {
                            i8_y += I8_SURFACEBOMBVSPEED;
                            if (i8_y >= I8_SEASURFACE_OFFSET)
                            {
                                p_spr.loadSprite(Sprite.SPRITE_UNDERWATERBOMB);
                                if (p_GameActionListener!=null) p_GameActionListener.gameAction(GAMEACTION_SOUND_SPLASH);
                            }
                            p_spr.setMainXY8(i8_x, i8_y);
                        }
                        ;
                        break;
                    case Sprite.SPRITE_UNDERWATERBOMB:
                        {
                            i8_y += I8_UNDERWATERBOMBVSPEED;
                            if (getRandomInt(30) == 25)
                            {
                                p_spr.lg_SpriteActive = false;
                                convertObjectToExplosion(p_spr, Sprite.SPRITE_EXPLOSIONUNDERWATER);
                            }
                            else
                                p_spr.setMainXY8(i8_x, i8_y);
                        }
                        ;
                        break;
                    case Sprite.SPRITE_SUFRACESHIP:
                        {
                            i8_x -= I8_SURFACESHIPHORZSPEED;
                            p_spr.setMainXY8(i8_x, i8_y);

                            if ((p_spr.i8_ScreenX + i8_w) < i8_startViewScreen)
                            {
                                p_spr.lg_SpriteActive = false;
                                continue;
                            }

                            int i_fire = i_shipfirefreq;
                            if (over_boat) i_fire >>>= 1;

                            if (getRandomInt(i_fire) == (i_fire >>> 1))
                            {
                                Sprite p_Spr = getFirstInactiveSprite();
                                if (p_Spr != null)
                                {
                                    p_Spr.loadSprite(Sprite.SPRITE_UNDERWATERBOMB);
                                    p_Spr.setMainXY8(i8_x + (i8_w >>> 1), i8_y + i8_h);
                                    if (p_GameActionListener!=null) p_GameActionListener.gameAction(GAMEACTION_SOUND_SPLASH);
                                }
                            }
                        }
                        ;
                        break;
                }

                if (p_TorpedoSprite.lg_SpriteActive && p_torpedoCollided == null)
                {
                    if (p_spr.isCollided(p_TorpedoSprite))
                    {
                        p_torpedoCollided = p_spr;
                    }
                }

                if (p_RocketSprite.lg_SpriteActive && p_rocketCollided == null)
                {
                    if (p_spr.isCollided(p_RocketSprite))
                    {
                        p_rocketCollided = p_spr;
                    }
                }

                if (p_collided == null && p_PlayerSprite.isCollided(p_spr))
                {
                    p_collided = p_spr;
                }
            }
        }

        if (p_rocketCollided != null)
        {
            switch (p_rocketCollided.i_objectType)
            {
                case Sprite.SPRITE_SUNKSHIP1:
                case Sprite.SPRITE_SUNKSHIP2:
                case Sprite.SPRITE_SUNKSHIP3:
                case Sprite.SPRITE_SUNKSHIP4:
                    {
                        convertObjectToExplosion(p_RocketSprite, Sprite.SPRITE_EXPLOSIONUNDERWATER);
                        p_RocketSprite.lg_SpriteActive = false;
                    };break;
                case Sprite.SPRITE_AIRCRAFTCARRIER:
                    {
                        convertObjectToExplosion(p_RocketSprite, Sprite.SPRITE_EXPLOSIONSURFACE);
                        i_BossHealth -= 5;
                        p_RocketSprite.lg_SpriteActive = false;
                    }
                    ;
                    break;
                case Sprite.SPRITE_EXPLOSIONSURFACE:
                case Sprite.SPRITE_EXPLOSIONAIR:
                case Sprite.SPRITE_EXPLOSIONUNDERWATER:
                    ;
                    break;
                case Sprite.SPRITE_SUFRACESHIP:
                    {
                        convertObjectToExplosion(p_rocketCollided, Sprite.SPRITE_EXPLOSIONSURFACE);
                        p_rocketCollided.lg_SpriteActive = false;
                        p_RocketSprite.lg_SpriteActive = false;
                        i_PlayerScore += 20;
                    }
                    ;
                    break;
                case Sprite.SPRITE_ICEBERG:
                    {
		        p_rocketCollided.i_param0 -= 4;

			if(p_rocketCollided.i_param0<0){

                           convertObjectToExplosion(p_rocketCollided, Sprite.SPRITE_EXPLOSIONSURFACE);
                           p_rocketCollided.lg_SpriteActive = false;
                           i_PlayerScore += 50;
			}
			  else
                          if (p_RocketSprite.i8_ScreenX < I8_SEASURFACE_OFFSET)
                               convertObjectToExplosion(p_RocketSprite, Sprite.SPRITE_EXPLOSIONSURFACE);
                          else
                               convertObjectToExplosion(p_RocketSprite, Sprite.SPRITE_EXPLOSIONUNDERWATER);
                        p_RocketSprite.lg_SpriteActive = false;
                    }
                    ;
                    break;
                case Sprite.SPRITE_CHOPPERLEFT:
                case Sprite.SPRITE_CHOPPERRIGHT:
                case Sprite.SPRITE_SURFACEBOMB:
                    {
                        convertObjectToExplosion(p_rocketCollided, Sprite.SPRITE_EXPLOSIONAIR);
                        p_rocketCollided.lg_SpriteActive = false;
                        p_RocketSprite.lg_SpriteActive = false;
                        i_PlayerScore += 50;
                    }
                    ;
                    break;
                default :
                    {
                        convertObjectToExplosion(p_rocketCollided, Sprite.SPRITE_EXPLOSIONUNDERWATER);
                        p_rocketCollided.lg_SpriteActive = false;
                        p_RocketSprite.lg_SpriteActive = false;
                        i_PlayerScore += 20;
                    }
            }
        }

        if (p_torpedoCollided != null)
        {
            switch (p_torpedoCollided.i_objectType)
            {
                case Sprite.SPRITE_SUNKSHIP1: // прощай кустик
                    {
                        convertObjectToExplosion(p_torpedoCollided, Sprite.SPRITE_EXPLOSIONUNDERWATER);
                        p_torpedoCollided.lg_SpriteActive = false;
                        p_TorpedoSprite.lg_SpriteActive = false;
                    };break;

                case Sprite.SPRITE_SUNKSHIP2:
                case Sprite.SPRITE_SUNKSHIP3:
                case Sprite.SPRITE_SUNKSHIP4:
                    {
                        convertObjectToExplosion(p_TorpedoSprite, Sprite.SPRITE_EXPLOSIONUNDERWATER);
                        p_TorpedoSprite.lg_SpriteActive = false;
                    };break;
                case Sprite.SPRITE_AIRCRAFTCARRIER:
                    {
                        convertObjectToExplosion(p_TorpedoSprite, Sprite.SPRITE_EXPLOSIONSURFACE);
                        i_BossHealth -= 10;
                        p_TorpedoSprite.lg_SpriteActive = false;
                    }
                    ;
                    break;
                case Sprite.SPRITE_EXPLOSIONSURFACE:
                case Sprite.SPRITE_EXPLOSIONAIR:
                case Sprite.SPRITE_EXPLOSIONUNDERWATER:
                    ;
                    break;
                case Sprite.SPRITE_SUFRACESHIP:
                    {
                        convertObjectToExplosion(p_torpedoCollided, Sprite.SPRITE_EXPLOSIONSURFACE);
                        p_torpedoCollided.lg_SpriteActive = false;
                        p_TorpedoSprite.lg_SpriteActive = false;
                        i_PlayerScore += 20;
                    }
                    ;
                    break;
                case Sprite.SPRITE_ICEBERG:
                    {
		        p_torpedoCollided.i_param0 -= 10;
			if(p_torpedoCollided.i_param0<0){
                           convertObjectToExplosion(p_torpedoCollided, Sprite.SPRITE_EXPLOSIONSURFACE);
                           p_torpedoCollided.lg_SpriteActive = false;
                           i_PlayerScore += 50;
			}
			  else
                             convertObjectToExplosion(p_TorpedoSprite, Sprite.SPRITE_EXPLOSIONUNDERWATER);
                        p_TorpedoSprite.lg_SpriteActive = false;
                    }
                    ;
                    break;
                default :
                    {
                        convertObjectToExplosion(p_torpedoCollided, Sprite.SPRITE_EXPLOSIONUNDERWATER);
                        p_torpedoCollided.lg_SpriteActive = false;
                        p_TorpedoSprite.lg_SpriteActive = false;
                        i_PlayerScore += 10;
                    }
            }
        }
        return p_collided;
    }

    public int getPlayerScore()
    {
        return (((i8_startViewScreen>>>9) + i_PlayerScore * (i_GameLevel+1))>>>1);
    }

    private void ActivateRocket()
    {
        p_RocketSprite.loadSprite(Sprite.SPRITE_ROCKET);
        p_RocketSprite.setMainXY8(p_PlayerSprite.i8_ScreenX + p_PlayerSprite.i8_width, p_PlayerSprite.i8_ScreenY);
        if (p_GameActionListener!=null) p_GameActionListener.gameAction(GAMEACTION_SOUND_ROCKETSTART);
    }

    private void ActivateTorpedo()
    {
        p_TorpedoSprite.loadSprite(Sprite.SPRITE_TORPEDO);
        p_TorpedoSprite.setMainXY8(p_PlayerSprite.i8_ScreenX + p_PlayerSprite.i8_width, p_PlayerSprite.i8_ScreenY + ((p_PlayerSprite.i8_height + p_PlayerSprite.i8_col_offsetY) >>> 1));
	i8_torpede_speed = I8_TORPEDOSPEED +i8_player_HorzSpeed;
        if (p_GameActionListener!=null) p_GameActionListener.gameAction(GAMEACTION_SOUND_TORPEDESTART);
    }

    public void nextGameStep(int _playermoveobject)
    {
        i_PlayerKey = _playermoveobject;
        Sprite p_collidedSprite = processSprites();
        processRocket();
        processTorpedo();
        if (!lg_BossSeen)
            generateObject();

        if (!lg_PlayerKilled && checkBottomCollided(p_PlayerSprite))
        {
            convertObjectToExplosion(p_PlayerSprite, Sprite.SPRITE_EXPLOSIONUNDERWATER);
            lg_PlayerKilled = true;
        }

        if (lg_BossKilled)
        {
            if (i_bossKilledCounter == I_PLAYER_DEATHTICKS)
            {
                i_GameState = GAMESTATE_OVER;
                i_PlayerState = PLAYERSTATE_WON;
                i_PlayerScore += 1000;
                return;
            }
            else
                i_bossKilledCounter++;
        }

        if (lg_PlayerKilled)
        {
            i8_player_HorzSpeed = 0;
            i8_player_VertSpeed = 0x10;
            if (i_playerKilledCounter == I_PLAYER_DEATHTICKS)
            {
                i_Attemptions--;
                if (i_Attemptions == 0)
                {
                    i_GameState = GAMESTATE_OVER;
                }
                i_PlayerState = PLAYERSTATE_LOST;
            }
            else
                i_playerKilledCounter++;
        }
        else
        {
            if ((i_PlayerKey & PLAYER_BUTTON_DOWN) != 0)
            {
                if (i8_player_VertSpeed > (0 - I8_PLAYER_MAX_VERTSPEED))
                {
                    i8_player_VertSpeed += I8_PLAYER_VERTSPEED_DECREASE;
                }
            }
	     else
              if ((i_PlayerKey & PLAYER_BUTTON_TOP) != 0)
              {
                  if (i8_player_VertSpeed < I8_PLAYER_MAX_VERTSPEED)
                  {
                      i8_player_VertSpeed -= I8_PLAYER_VERTSPEED_INCREASE;
                  }
              }
	        else
	        {
		   int dx = Math.min( Math.abs(i8_player_VertSpeed)>>1, I8_INERTIA_DAMPER);
	           if (i8_player_VertSpeed > 0)
		            i8_player_VertSpeed -= dx;
		       else
		            i8_player_VertSpeed += dx;
                }



            if ((i_PlayerKey & PLAYER_BUTTON_RIGHT) != 0)
            {
                if (i8_player_HorzSpeed < I8_PLAYER_MAX_HORZSPEED)
                {
                    i8_player_HorzSpeed += I8_PLAYER_HORZSPEED_INCREASE;
                }
            }
	     else
              if ((i_PlayerKey & PLAYER_BUTTON_LEFT) != 0)
              {
                i8_player_HorzSpeed -= I8_PLAYER_HORZSPEED_DECREASE;
                if (lg_bossDrivingMode)
                {
                    if (i8_player_HorzSpeed < (0 - I8_PLAYER_MAX_HORZSPEED))
                        i8_player_HorzSpeed = 0 - I8_PLAYER_MAX_HORZSPEED;
                }
                else
                {
                    if (i8_player_HorzSpeed < 0) i8_player_HorzSpeed = 0;
                }
              }
	        else
	        {
		   int dx = Math.min( Math.abs(i8_player_HorzSpeed)>>1, I8_INERTIA_DAMPER);
	           if (i8_player_HorzSpeed > 0)
		            i8_player_HorzSpeed -= dx;
		       else
		            i8_player_HorzSpeed += dx;
                }




            if ((i_PlayerKey & PLAYER_BUTTON_TORPEDOFIRE) != 0)
            {
                if (!p_TorpedoSprite.lg_SpriteActive)
                {
                    ActivateTorpedo();
                }
            }

            if ((i_PlayerKey & PLAYER_BUTTON_ROCKETFIRE) != 0)
            {
                if (!p_RocketSprite.lg_SpriteActive)
                {
                    ActivateRocket();
                }
            }
        }

        int i8_mainX = p_PlayerSprite.i8_mainX + i8_player_HorzSpeed;
        int i8_mainY = p_PlayerSprite.i8_mainY + i8_player_VertSpeed;

        i8_generationStep += (i8_player_HorzSpeed >>> 1);
        i8_generationStep2 += 0x80;

        if (i8_mainY < (I8_SEASURFACE_OFFSET - p_PlayerSprite.i8_MaxSurfacing))
        {
            i8_mainY = I8_SEASURFACE_OFFSET - p_PlayerSprite.i8_MaxSurfacing;
            i8_player_VertSpeed = 0;
        }

        p_PlayerSprite.setMainXY8(i8_mainX, i8_mainY);

        if (p_PlayerSprite.i8_ScreenY < I8_SEASURFACE_OFFSET)
        {
            i8_player_OxygenValue += I8_SPEED_OXYGEN_INCREASE;
            if (i8_player_OxygenValue > I8_MAX_OXYGEN_VALUE)
                i8_player_OxygenValue = I8_MAX_OXYGEN_VALUE;
            else
                if (p_GameActionListener!=null) p_GameActionListener.gameAction(GAMEACTION_SOUND_TAKEOXYGEN);
        }
        else
        {
            i8_player_OxygenValue -= I8_SPEED_OXYGEN_DECREASE;

            if (i8_player_OxygenValue <= 0)
            {
                i8_player_OxygenValue = 0;
                lg_PlayerKilled = true;
            }
        }

        if (lg_bossDrivingMode)
        {
            int i8_plx = p_PlayerSprite.i8_ScreenX;
            int i8_ply = p_PlayerSprite.i8_mainY;

            if (i8_plx < i8_startViewScreen)
            {
                p_PlayerSprite.setMainXY8(i8_startViewScreen, i8_ply);
                i8_player_HorzSpeed = 0;
            }
            else if (i8_plx + p_PlayerSprite.i8_width >= i8_endViewScreen)
            {
                p_PlayerSprite.setMainXY8(i8_endViewScreen - p_PlayerSprite.i8_width - 0x100, i8_ply);
                i8_player_HorzSpeed = 0;
            }
        }
        else
        {
            i8_startViewScreen += i8_player_HorzSpeed;
            i8_endViewScreen = i8_startViewScreen + i8_screenWidth;
        }

        if (p_collidedSprite != null)
        {
            processPlayerCollide(p_collidedSprite);
        }

        if (!lg_PlayerKilled && i_playerHealth <= 0)
        {
            i_playerHealth = 0;
            convertObjectToExplosion(p_PlayerSprite, Sprite.SPRITE_EXPLOSIONUNDERWATER);
            lg_PlayerKilled = true;
        }
        else if (!lg_BossKilled && i_BossHealth <= 0)
        {
            i_BossHealth = 0;
            lg_BossKilled = true;
            convertObjectToExplosion(ap_Sprites[i_bossSpriteId], Sprite.SPRITE_EXPLOSIONSURFACE);
        }
    }

    private void processPlayerCollide(Sprite _collideobject)
    {
        switch (_collideobject.i_objectType)
        {
            case Sprite.SPRITE_AIRCRAFTCARRIER:
                {
                    i_playerHealth = 0;
                }
                ;
                break;
            case Sprite.SPRITE_EXPLOSIONSURFACE:
                {
//                    i_playerHealth -= 1;
                }
                ;
                break;
            case Sprite.SPRITE_EXPLOSIONUNDERWATER:
                {
                    i_playerHealth -= 5;
                }
                ;
                break;
            case Sprite.SPRITE_SURFACEBOMB:
                {
                    convertObjectToExplosion(_collideobject, Sprite.SPRITE_EXPLOSIONSURFACE);
                }
            case Sprite.SPRITE_ICEBERG:
                {
                    i_playerHealth -= 20;
                }
                ;
                break;
            case Sprite.SPRITE_MINE:
                {
                    convertObjectToExplosion(_collideobject, Sprite.SPRITE_EXPLOSIONUNDERWATER);
                    i_playerHealth = 0;
		    _collideobject.lg_SpriteActive = false;
                }
                ;
                break;
            case Sprite.SPRITE_SUFRACESHIP:
                {
                    i_playerHealth -= 20;
                }
                ;
                break;
            case Sprite.SPRITE_SUNKSHIP1:
            case Sprite.SPRITE_SUNKSHIP2:
            case Sprite.SPRITE_SUNKSHIP3:
            case Sprite.SPRITE_SUNKSHIP4:
            case Sprite.SPRITE_UNDERWATERBOMB:
                {
                    if (i8_player_HorzSpeed != 0)
                    {
                        if (p_GameActionListener!=null) p_GameActionListener.gameAction(GAMEACTION_SOUND_GNASH);
                        i_playerHealth -= 3;

                        if (getRandomInt(10) == 5)
                        {
                            Sprite p_bul = getFirstInactiveSprite();
                            if (p_bul != null)
                            {
                                int i8_px = p_PlayerSprite.i8_ScreenX;
                                int i8_py = p_PlayerSprite.i8_ScreenY;
                                int i8_pw = i8_px + p_PlayerSprite.i8_width;

                                int i8_cx = _collideobject.i8_ScreenX;
                                int i8_cy = _collideobject.i8_ScreenY;
                                int i8_cw = i8_cx + _collideobject.i8_width;

                                int i8_sy = i8_py < i8_cy ? i8_py : i8_cy;
                                int i8_sw = i8_pw < i8_cw ? i8_pw : i8_cw;

                                p_bul.loadSprite(Sprite.SPRITE_BLEBS);
                                p_bul.setMainXY8(i8_sw, i8_sy);
                                if (p_GameActionListener!=null) p_GameActionListener.gameAction(GAMEACTION_SOUND_GURGLING);
                            }
                        }
                    }
                }
                ;
                break;
        }
    }

    public int getDepthAtWayPoint(int _x)
    {
        long l_element = getLineElement(_x);
        for (int li = 0; li < 64; li++)
        {
            if (l_element == 0) return (li + I_GROUND_OFFSET)&63;
            l_element >>>= 1;
        }
        return 64;
    }

    public long getLineElement(int _num)
    {
        _num >>= 1;                                  // because map of ground is doubled
        int i_block = _num / Stage.BLOCKLENGTH;
        int i_indx = _num % Stage.BLOCKLENGTH;
        return Stage.alal_Blocks[Stage.ai_Stage[i_block]][i_indx];
    }

    public void readFromStream(DataInputStream _dataInputStream) throws IOException
    {
        for(int li=0;li<SPRITES_NUMBER;li++)
        {
            Sprite p_sprite = ap_Sprites[li];
            p_sprite.loadSprite(_dataInputStream.readUnsignedByte());
            p_sprite.setMainXY8(_dataInputStream.readInt(),_dataInputStream.readInt());
            p_sprite.i_param0 = _dataInputStream.readInt();
            p_sprite.i_param1 = _dataInputStream.readInt();
            p_sprite.lg_backMove = _dataInputStream.readBoolean();
            p_sprite.i_Frame = _dataInputStream.readUnsignedByte();
            p_sprite.i_Delay = _dataInputStream.readUnsignedByte();
            p_sprite.lg_SpriteActive = _dataInputStream.readBoolean();
        }

        // Load the player sprite
        Sprite p_sprite = p_PlayerSprite;
        p_sprite.loadSprite(_dataInputStream.readUnsignedByte());
        p_sprite.setMainXY8(_dataInputStream.readInt(),_dataInputStream.readInt());
        p_sprite.i_param0 = _dataInputStream.readInt();
        p_sprite.i_param1 = _dataInputStream.readInt();
        p_sprite.lg_backMove = _dataInputStream.readBoolean();
        p_sprite.i_Frame = _dataInputStream.readUnsignedByte();
        p_sprite.i_Delay = _dataInputStream.readUnsignedByte();
        p_sprite.lg_SpriteActive = _dataInputStream.readBoolean();

        // Load the torpedo sprite
        p_sprite = p_TorpedoSprite;
        p_sprite.loadSprite(_dataInputStream.readUnsignedByte());
        p_sprite.setMainXY8(_dataInputStream.readInt(),_dataInputStream.readInt());
        p_sprite.i_param0 = _dataInputStream.readInt();
        p_sprite.i_param1 = _dataInputStream.readInt();
        p_sprite.lg_backMove = _dataInputStream.readBoolean();
        p_sprite.i_Frame = _dataInputStream.readUnsignedByte();
        p_sprite.i_Delay = _dataInputStream.readUnsignedByte();
        p_sprite.lg_SpriteActive = _dataInputStream.readBoolean();
	i8_torpede_speed = _dataInputStream.readInt();

        // Load the rocket sprite
        p_sprite = p_RocketSprite;
        p_sprite.loadSprite(_dataInputStream.readUnsignedByte());
        p_sprite.setMainXY8(_dataInputStream.readInt(),_dataInputStream.readInt());
        p_sprite.i_param0 = _dataInputStream.readInt();
        p_sprite.i_param1 = _dataInputStream.readInt();
        p_sprite.lg_backMove = _dataInputStream.readBoolean();
        p_sprite.i_Frame = _dataInputStream.readUnsignedByte();
        p_sprite.i_Delay = _dataInputStream.readUnsignedByte();
        p_sprite.lg_SpriteActive = _dataInputStream.readBoolean();

        lg_bossDrivingMode = _dataInputStream.readBoolean();
        lg_BossKilled = _dataInputStream.readBoolean();
        lg_BossSeen = _dataInputStream.readBoolean();
        lg_PlayerKilled = _dataInputStream.readBoolean();

        i_Attemptions = _dataInputStream.readUnsignedByte();
        i_BossHealth = _dataInputStream.readInt();
        i_bossKilledCounter = _dataInputStream.readInt();
        i_bossSpriteId = _dataInputStream.readUnsignedByte();
        i_indexSunkShip = _dataInputStream.readUnsignedByte();
        i_nextSunkShipType = _dataInputStream.readUnsignedByte();
        i_playerHealth = _dataInputStream.readInt();
        i_playerKilledCounter = _dataInputStream.readInt();
        i8_bossStartPosition =  _dataInputStream.readInt();
        i8_endViewScreen = _dataInputStream.readInt();
        i8_startViewScreen = _dataInputStream.readInt();
        i8_generationStep = _dataInputStream.readInt();
        i8_generationStep2 = _dataInputStream.readInt();
        i8_nextSunkShipX =  _dataInputStream.readInt();
        i8_nextSunkShipY = _dataInputStream.readInt();
        i8_player_HorzSpeed = _dataInputStream.readInt();
        i8_player_VertSpeed = _dataInputStream.readInt();
        i8_player_OxygenValue = _dataInputStream.readInt();
    }

    public void writeToStream(DataOutputStream _dataOutputStream) throws IOException
    {
        for(int li=0;li<SPRITES_NUMBER;li++)
        {
            Sprite p_sprite = ap_Sprites[li];
            _dataOutputStream.writeByte(p_sprite.i_objectType);
            _dataOutputStream.writeInt(p_sprite.i8_mainX);
            _dataOutputStream.writeInt(p_sprite.i8_mainY);
            _dataOutputStream.writeInt(p_sprite.i_param0);
            _dataOutputStream.writeInt(p_sprite.i_param1);
            _dataOutputStream.writeBoolean(p_sprite.lg_backMove);
            _dataOutputStream.writeByte(p_sprite.i_Frame);
            _dataOutputStream.writeByte(p_sprite.i_Delay);
            _dataOutputStream.writeBoolean(p_sprite.lg_SpriteActive);
        }

        // Writing the player frame
        Sprite p_sprite = p_PlayerSprite;
        _dataOutputStream.writeByte(p_sprite.i_objectType);
        _dataOutputStream.writeInt(p_sprite.i8_mainX);
        _dataOutputStream.writeInt(p_sprite.i8_mainY);
        _dataOutputStream.writeInt(p_sprite.i_param0);
        _dataOutputStream.writeInt(p_sprite.i_param1);
        _dataOutputStream.writeBoolean(p_sprite.lg_backMove);
        _dataOutputStream.writeByte(p_sprite.i_Frame);
        _dataOutputStream.writeByte(p_sprite.i_Delay);
        _dataOutputStream.writeBoolean(p_sprite.lg_SpriteActive);

        // Writing the torpedo frame
        p_sprite = p_TorpedoSprite;
        _dataOutputStream.writeByte(p_sprite.i_objectType);
        _dataOutputStream.writeInt(p_sprite.i8_mainX);
        _dataOutputStream.writeInt(p_sprite.i8_mainY);
        _dataOutputStream.writeInt(p_sprite.i_param0);
        _dataOutputStream.writeInt(p_sprite.i_param1);
        _dataOutputStream.writeBoolean(p_sprite.lg_backMove);
        _dataOutputStream.writeByte(p_sprite.i_Frame);
        _dataOutputStream.writeByte(p_sprite.i_Delay);
        _dataOutputStream.writeBoolean(p_sprite.lg_SpriteActive);
        _dataOutputStream.writeInt(i8_torpede_speed);

        // Writing the rocket frame
        p_sprite = p_RocketSprite;
        _dataOutputStream.writeByte(p_sprite.i_objectType);
        _dataOutputStream.writeInt(p_sprite.i8_mainX);
        _dataOutputStream.writeInt(p_sprite.i8_mainY);
        _dataOutputStream.writeInt(p_sprite.i_param0);
        _dataOutputStream.writeInt(p_sprite.i_param1);
        _dataOutputStream.writeBoolean(p_sprite.lg_backMove);
        _dataOutputStream.writeByte(p_sprite.i_Frame);
        _dataOutputStream.writeByte(p_sprite.i_Delay);
        _dataOutputStream.writeBoolean(p_sprite.lg_SpriteActive);

        _dataOutputStream.writeBoolean(lg_bossDrivingMode);
        _dataOutputStream.writeBoolean(lg_BossKilled);
        _dataOutputStream.writeBoolean(lg_BossSeen);
        _dataOutputStream.writeBoolean(lg_PlayerKilled);

        _dataOutputStream.writeByte(i_Attemptions);
        _dataOutputStream.writeInt(i_BossHealth);
        _dataOutputStream.writeInt(i_bossKilledCounter);
        _dataOutputStream.writeByte(i_bossSpriteId);
        _dataOutputStream.writeByte(i_indexSunkShip);
        _dataOutputStream.writeByte(i_nextSunkShipType);
        _dataOutputStream.writeInt(i_playerHealth);
        _dataOutputStream.writeInt(i_playerKilledCounter);
        _dataOutputStream.writeInt(i8_bossStartPosition);
        _dataOutputStream.writeInt(i8_endViewScreen);
        _dataOutputStream.writeInt(i8_startViewScreen);
        _dataOutputStream.writeInt(i8_generationStep);
        _dataOutputStream.writeInt(i8_generationStep2);
        _dataOutputStream.writeInt(i8_nextSunkShipX);
        _dataOutputStream.writeInt(i8_nextSunkShipY);
        _dataOutputStream.writeInt(i8_player_HorzSpeed);
        _dataOutputStream.writeInt(i8_player_VertSpeed);
        _dataOutputStream.writeInt(i8_player_OxygenValue);
    }
}
