package com.igormaznitsa.game_kit_E5D3F2.Zynaps;

import com.igormaznitsa.gameapi.StrategicBlock;
import com.igormaznitsa.gameapi.GameStateBlock;

public class Zynaps_SB extends StrategicBlock
{
    public static final int VIRTUALCELL_WIDTH = 2;
    public static final int VIRTUALCELL_HEIGHT = 1;

    public static final int LEVEL0 = 0;
    public static final int LEVEL0_TIMEDELAY = 100;
    public static final int LEVEL0_ENEMYDELAY = 30;

    public static final int LEVEL1 = 1;
    public static final int LEVEL1_TIMEDELAY = 90;
    public static final int LEVEL1_ENEMYDELAY = 20;

    public static final int LEVEL2 = 2;
    public static final int LEVEL2_TIMEDELAY = 80;
    public static final int LEVEL2_ENEMYDELAY = 10;

    public static final int MAX_ATTEMPTIONS = 3;
    public static final int MAX_MOVINGOBJECTS = 30;
    public static final int MAX_PLAYERBULLETS = 10;

    private static final int I8_PLAYER_VERTSPEED = 0x300;

    private static final int I8_PLAYERBULLSPEED = 0x600;
    private static final int I8_ENEMYBULLSPEED = 0x400;

    public static final int I8_BACKGROUNDSPEED = 0x100;

    protected static final int SHIELD_RADIUS = 25;
    private static final int SHIELD_DELAY = 500;
    private static final int ENERGYSHIELD_DELAY = 500;
    protected static final int WAYLENGTH = 5000;

    private static final int FIRE_DELAY = 3;
    private static final int I8_BOSSSPEED = 0x200;

    public static final int BOSS_POWER = 50;
    public static final int PLAYER_POWER = 50;

    public static final int DELAY_NONDESTROYED = 40;

    public Zynaps_GSB p_Gsb;

    public int i_CellsPerScreen;

    private static final long L_SHIPMASK = 0x3FFFl;

    public void resumeGameAfterPlayerLost()
    {
        p_Gsb.i_playerState = GameStateBlock.PLAYERSTATE_NORMAL;
        p_Gsb.i_gameState = GameStateBlock.GAMESTATE_PLAYED;

        p_Gsb.p_Player.Init(0, i_screenHeight, p_Gsb.p_PlayerShield, p_Gsb.p_PlayerShortWeapon);
        p_Gsb.i_playerpower = PLAYER_POWER;

        p_Gsb.deactivateMoveObjects(false);
        p_Gsb.deactivatePlayerProperties();
        processPlayerShield();
    }

    public void newGameSession(int _level)
    {
        p_Gsb = new Zynaps_GSB();
        p_gameStateBlock = p_Gsb;
        p_Gsb.initLevel(_level);
        p_Gsb.p_Player.Init(0, i_screenHeight, p_Gsb.p_PlayerShield, p_Gsb.p_PlayerShortWeapon);
    }

    public void endGameSession()
    {
        super.endGameSession();
        p_Gsb = null;
    }

    // return true if intersected
    private boolean checkShipAndWalls()
    {
        Player p_player = p_Gsb.p_Player;

        int i_offst = p_player.getScrY() / VIRTUALCELL_HEIGHT;
        long l_mask = L_SHIPMASK << i_offst;
        int i_wdth = p_player.i_width / VIRTUALCELL_WIDTH;

        int i_cellstart = ((p_Gsb.i8_backgroundway >> 8) + p_player.getScrX()) / VIRTUALCELL_WIDTH;
        int i_endstart = i_cellstart + i_wdth;

        for (int li = i_cellstart; li <= i_endstart; li++)
        {
            long l_val = p_Gsb.getLineElement(li);
            if ((l_val & l_mask) != l_mask) return true;
        }

        return false;
    }

    public void nextGameStep(Object _playermoveobject)
    {
        if (p_Gsb.i_NonDestroyableDelay > 0) p_Gsb.i_NonDestroyableDelay--;
        if (!p_Gsb.lg_BossMode) p_Gsb.i8_backgroundway += I8_BACKGROUNDSPEED;

        int i_curcell = (p_Gsb.i8_backgroundway / Zynaps_SB.VIRTUALCELL_WIDTH) >> 8;

        if (!p_Gsb.lg_BossMode && i_curcell + i_CellsPerScreen >= p_Gsb.i_fullcellsnumber)
        {
            MovingObject p_obj = p_Gsb.getInactiveObject();
            if (p_obj == null)
            {
                p_obj = p_Gsb.ap_MovingObjects[0];
            }

            p_obj.activate(MovingObject.TYPE_BOSS, 0, 0);
            p_obj.i8_scrx = i_screenWidth << 8;
            p_obj.i8_scry = ((i_screenHeight >> 1) - (p_obj.i_Height >> 1)) << 8;

            p_Gsb.lg_BossMode = true;
            p_Gsb.i_bosspower = BOSS_POWER;
        }

        if (p_Gsb.i_firedelay > 0) p_Gsb.i_firedelay--;

        Zynaps_PMR p_pmr = (Zynaps_PMR) _playermoveobject;

        Player p_player = p_Gsb.p_Player;

        int i8_px = p_player.i8_cx;
        int i8_py = p_player.i8_cy;

        if (p_player.lg_Destroyed)
        {
            if (p_player.process(Zynaps_PMR.BUTTON_NONE))
            {
                p_Gsb.i_Attemptions--;
                p_Gsb.i_playerState = GameStateBlock.PLAYERSTATE_LOST;
                if (p_Gsb.i_Attemptions <= 0)
                {
                    p_Gsb.i_gameState = GameStateBlock.GAMESTATE_OVER;
                }
            }
        }
        else
        {
            switch (p_pmr.i_Value)
            {
                case Zynaps_PMR.BUTTON_LEFT:
                    {
                        int i8_nx = i8_px - I8_PLAYER_VERTSPEED;
                        if (i8_nx >= 0)
                        {
                            p_player.setCX(i8_nx);
                        }
                    }
                    ;
                    break;
                case Zynaps_PMR.BUTTON_RIGHT:
                    {
                        int i8_nx = i8_px + I8_PLAYER_VERTSPEED;
                        if (((i8_nx >> 8) + p_player.i_width) < i_screenWidth)
                        {
                            p_player.setCX(i8_nx);
                        }
                    }
                    ;
                    break;
                case Zynaps_PMR.BUTTON_UP:
                    {
                        int i8_ny = i8_py - I8_PLAYER_VERTSPEED;
                        if (i8_ny >= 0)
                        {
                            p_player.setCY(i8_ny);
                        }
                    }
                    ;
                    break;
                case Zynaps_PMR.BUTTON_DOWN:
                    {
                        int i8_ny = i8_py + I8_PLAYER_VERTSPEED;
                        if (((i8_ny >> 8) + p_player.i_height) <= i_screenHeight)
                        {
                            p_player.setCY(i8_ny);
                        }
                    }
                    ;
                    break;
                case Zynaps_PMR.BUTTON_FIRE:
                    {
                        if (p_Gsb.i_playerbulletinactive != 0 && p_Gsb.i_firedelay == 0)
                        {
                            MovingObject p_bull = p_Gsb.getInactivePlayerBullet();
                            if (p_bull != null)
                            {
                                p_bull.activate(p_Gsb.i_PlayerBulletType, 0, 0);
                                int i_x = i8_px + (p_player.i_width << 8);
                                p_bull.i8_scrx = i_x;
                                p_bull.i8_scry = p_player.i8_cy + ((p_player.i_height >> 1) << 8) - ((p_bull.i_Height >> 1) << 8);
                                p_Gsb.i_playerbulletinactive--;

                                p_Gsb.i_firedelay = FIRE_DELAY;
                            }
                        }
                    }
                    ;
                    break;
            }

            p_player.process(p_pmr.i_Value);
        }

        processPlayerShield();
        processPlayerBullets();
        processMovingObjects();

        if (!p_player.lg_Destroyed)
        {
            if (p_Gsb.i_NonDestroyableDelay == 0)
            {
                if (checkShipAndWalls() || p_Gsb.i_playerpower == 0)
                {
                    p_Gsb.p_PlayerShield.lg_Active = false;
                    p_Gsb.p_PlayerShortWeapon.lg_Active = false;
                    p_player.destroyBoat();
                }
            }
        }

        if (p_Gsb.i_bosspower == 0 && p_Gsb.lg_BossMode)
        {
            p_Gsb.i_playerState = GameStateBlock.PLAYERSTATE_WON;
            p_Gsb.i_gameState = GameStateBlock.GAMESTATE_OVER;
            return;
        }

        generateMovingObject();
    }

    private void processPlayerShield()
    {
        if (p_Gsb.i_shielddelay == 0)
            p_Gsb.p_PlayerShield.lg_Active = false;
        else
            p_Gsb.i_shielddelay--;

        if (p_Gsb.i_energyshielddelay == 0)
            p_Gsb.p_PlayerShortWeapon.lg_Active = false;
        else
            p_Gsb.i_energyshielddelay--;

        MovingObject p_shield = p_Gsb.p_PlayerShield;
        MovingObject p_def = p_Gsb.p_PlayerShortWeapon;

        p_shield.i_angle = (p_shield.i_angle + 3) & 63;
        Player p_player = p_Gsb.p_Player;

        int i_pcx = p_player.i8_cx + ((p_player.i_width >> 1) << 8);
        int i_pcy = p_player.i8_cy + ((p_player.i_height >> 1) << 8);

        p_shield.i8_scrx = i_pcx;
        p_shield.i8_scry = i_pcy;

        p_shield.i_vx = (i_pcx + xSineFloat(SHIELD_RADIUS, p_shield.i_angle)) >> 8;
        p_shield.i_vy = (i_pcy + xCoSineFloat(SHIELD_RADIUS, p_shield.i_angle)) >> 8;

        p_def.i8_scrx = i_pcx + (SHIELD_RADIUS << 8);
        p_def.i8_scry = i_pcy - ((p_def.i_Height >> 1) << 8);
        p_shield.process();
        p_def.process();
    }

    private void generateMovingObject()
    {
        if (p_Gsb.i_enemydelay == 0 && !p_Gsb.lg_BossMode)
        {
            int i_type = getRandomInt(1000) / 100;

            switch (i_type)
            {
                case 0:
                    {
                        i_type = MovingObject.TYPE_ENEMY0;
                    }
                    ;
                    break;
                case 1:
                    {
                        i_type = MovingObject.TYPE_ENEMY1;
                    }
                    ;
                    break;
                case 2:
                    {
                        i_type = MovingObject.TYPE_ENEMY2;
                    }
                    ;
                    break;
                case 3:
                    {
                        i_type = MovingObject.TYPE_ENEMY3;
                    }
                    ;
                    break;
                case 4:
                case 5:
                    {
                        i_type = MovingObject.TYPE_ENEMY4;
                    }
                    ;
                    break;
                default :
                    {
                        i_type = -1;
                    }
            }

            if (i_type >= 0)
            {
                MovingObject p_obj = p_Gsb.getInactiveObject();
                if (p_obj == null) return;

                p_obj.activate(i_type, 0, 0);
                p_obj.i_radius = 15;

                int i_oy = getRandomInt((i_screenHeight / p_obj.i_Height)-1) * p_obj.i_Height;

                switch(i_type)
                {
                    case MovingObject.TYPE_ENEMY1 :  p_obj.i_angle = 16;break;
                    case MovingObject.TYPE_ENEMY2 :  p_obj.i_angle = 48;break;
                    default :
                        p_obj.i_angle = 0;
                }


                p_obj.i8_scrx = (i_screenWidth << 8);
                p_obj.i8_scry = i_oy << 8;
                p_Gsb.i_enemydelay = p_Gsb.i_maxenemydelay;
            }
        }
        else
        {
            if (p_Gsb.i_enemydelay > 0) p_Gsb.i_enemydelay--;
        }
    }

    private void convertObjectToOtherType(MovingObject _obj, int _type)
    {
        int i_x = _obj.getScrX();
        int i_y = _obj.getScrY();
        int i_w = _obj.i_Width;
        int i_h = _obj.i_Height;

        _obj.activate(_type, 0, 0);
        i_x = i_x + (i_w >> 1) - (_obj.i_Width >> 1);
        i_y = i_y + (i_h >> 1) - (_obj.i_Height >> 1);

        _obj.i8_scrx = i_x << 8;
        _obj.i8_scry = i_y << 8;
    }

    private void processMovingObjects()
    {
        Player p_player = p_Gsb.p_Player;
        MovingObject p_roundshield = p_Gsb.p_PlayerShield;
        MovingObject p_shortshield = p_Gsb.p_PlayerShortWeapon;

        int i_px = p_player.getScrX();
        int i_py = p_player.getScrY();
        int i_pw = p_player.i_width;
        int i_ph = p_player.i_height;
        int i_pwx = i_px + i_pw;
        int i_pwy = i_py + i_ph;

        boolean lg_shieldactive = p_roundshield.lg_Active;
        int i_shx = p_roundshield.getScrX();
        int i_shy = p_roundshield.getScrY();
        int i_shw = p_roundshield.i_Width;
        int i_shh = p_roundshield.i_Height;
        int i_shwx = i_shx + i_shw;
        int i_shwy = i_shy + i_shh;

        boolean lg_shortactive = p_shortshield.lg_Active;
        int i_shx2 = p_shortshield.getScrX();
        int i_shy2 = p_shortshield.getScrY();
        int i_shw2 = p_shortshield.i_Width;
        int i_shh2 = p_shortshield.i_Height;
        int i_shwx2 = i_shx2 + i_shw2;
        int i_shwy2 = i_shy2 + i_shh2;

        MovingObject[] ap_playerbulltes = p_Gsb.ap_PlayerBullets;


        int i_cy = i_py + (i_ph>>1);

        for (int li = 0; li < MAX_MOVINGOBJECTS; li++)
        {
            boolean lg_iscollided = false;
            MovingObject p_obj = p_Gsb.ap_MovingObjects[li];
            if (p_obj.lg_Active)
            {
                int i_ox = p_obj.getScrX();
                int i_oy = p_obj.getScrY();
                int i_ow = p_obj.i_Width;
                int i_oh = p_obj.i_Height;
                int i_oxw = i_ox + i_ow;
                int i_oyh = i_oy + i_oh;

                // checking of collision with the player and his shields
                boolean lg_sheldcollided = false;

                if (p_obj.i_Type == MovingObject.TYPE_EXPLOSION)
                {
                    if (!p_Gsb.lg_BossMode)
                    {
                        p_obj.i8_scry += (I8_PLAYER_VERTSPEED >> 2);
                        p_obj.i8_scrx -= (I8_PLAYER_VERTSPEED >> 1);
                    }
                    if (p_obj.process()) p_obj.lg_Active = false;
                    continue;
                }

                p_obj.process();

                if (!((i_oxw <= i_px) || (i_oyh <= i_py) || (i_ox >= i_pwx) || (i_oy >= i_pwy))) lg_iscollided = true;

                if (p_obj.i_Type != MovingObject.TYPE_BOSS)
                {
                    if (lg_shieldactive)
                        if (!((i_oxw <= i_shx) || (i_oyh <= i_shy) || (i_ox >= i_shwx) || (i_oy >= i_shwy))) lg_sheldcollided = true;

                    if (lg_shortactive && !lg_sheldcollided)
                        if (!((i_oxw <= i_shx2) || (i_oyh <= i_shy2) || (i_ox >= i_shwx2) || (i_oy >= i_shwy2))) lg_sheldcollided = true;
                }

                // Checking for player's bullets
                for (int lb = 0; lb < ap_playerbulltes.length; lb++)
                {
                    MovingObject p_bull = ap_playerbulltes[lb];

                    if (p_bull.lg_Active)
                    {
                        int i_bx = p_bull.getScrX();
                        int i_by = p_bull.getScrY();

                        int i_bw = p_bull.i_Width;
                        int i_bh = p_bull.i_Height;

                        if (!((i_oxw <= i_bx) || (i_oyh <= i_by) || (i_ox >= i_bw + i_bx) || (i_oy >= i_bh + i_by)))
                        {
                            if (p_Gsb.lg_BossMode && p_obj.i_Type == MovingObject.TYPE_BOSS)
                            {
                                if (p_Gsb.i_PlayerBulletType == MovingObject.TYPE_BULLET2)
                                    p_Gsb.i_bosspower -= 2;
                                else
                                    p_Gsb.i_bosspower -= 1;

                                MovingObject p_expl = p_Gsb.getInactiveObject();
                                if (p_expl != null)
                                {
                                    int i_x = p_bull.getScrX();
                                    int i_y = p_bull.getScrY();
                                    int i_w = p_bull.i_Width;
                                    int i_h = p_bull.i_Height;

                                    p_expl.activate(MovingObject.TYPE_EXPLOSION, 0, 0);
                                    i_x = i_x + (i_w >> 1) - (p_bull.i_Width >> 1);
                                    i_y = i_y + (i_h >> 1) - (p_bull.i_Height >> 1);

                                    p_expl.i8_scrx = i_x << 8;
                                    p_expl.i8_scry = i_y << 8;
                                    p_bull.lg_Active = false;

                                }

                                p_Gsb.i_playerScore += 15;

                                p_Gsb.i_playerbulletinactive++;
                            }
                            else
                            {
                                lg_sheldcollided = true;
                                if (p_bull.i_Type == MovingObject.TYPE_BULLET1 || p_obj.i_Type == MovingObject.TYPE_ENEMY3)
                                {
                                    p_bull.lg_Active = false;
                                    p_Gsb.i_playerbulletinactive++;
                                }
                                break;
                            }
                        }
                    }
                }

                switch (p_obj.i_Type)
                {
                    case MovingObject.TYPE_BOSS:
                        {
                            if ((p_obj.i8_scrx >> 8) > (i_screenWidth - p_obj.i_Width))
                            {
                                p_obj.i8_scrx -= (I8_PLAYER_VERTSPEED >> 2);
                                p_obj.i_angle = -1;
                                p_obj.i_radius = 1;
                            }
                            else
                            {
                                if (p_obj.i_angle < 0)
                                {
                                    p_obj.i8_scry -= I8_BOSSSPEED;
                                    if (p_obj.i8_scry < 0)
                                    {
                                        p_obj.i8_scry = 0;
                                        p_obj.i_angle = 1;
                                    }
                                }
                                else
                                {
                                    p_obj.i8_scry += I8_BOSSSPEED;
                                    if ((p_obj.i8_scry >> 8) + p_obj.i_Height >= i_screenHeight)
                                    {
                                        p_obj.i8_scry = (i_screenHeight - p_obj.i_Height - 1) << 8;
                                        p_obj.i_angle = -1;
                                    }
                                }

                                if (p_obj.i_radius < 0)
                                {
                                    p_obj.i8_scrx += I8_BOSSSPEED;
                                    if ((p_obj.i8_scrx >> 8) + p_obj.i_Width >= i_screenWidth)
                                    {
                                        p_obj.i8_scrx = (i_screenWidth - p_obj.i_Width) << 8;
                                        p_obj.i_radius = 1;
                                    }
                                }
                                else
                                {
                                    p_obj.i8_scrx -= I8_BOSSSPEED;
                                    if ((p_obj.i8_scrx >> 8) <= (i_screenWidth >> 1))
                                    {
                                        p_obj.i8_scrx = (i_screenWidth >> 1) << 8;
                                        p_obj.i_radius = -1;
                                    }
                                }

                                if (getRandomInt(50) == 25)
                                {
                                    p_obj.i_radius = 0 - p_obj.i_radius;
                                }

                                if (getRandomInt(50) == 25)
                                {
                                    p_obj.i_angle = 0 - p_obj.i_angle;
                                }

                                // Block of firing from three points of the boss
                                int i_cpy = (i_py + i_ph >> 1);

                                // Firing from first point
                                if (getRandomInt(15) == 10)
                                {
                                    MovingObject p_mo = p_Gsb.getInactiveObject();
                                    if (p_mo != null)
                                    {
                                        p_mo.activate(MovingObject.TYPE_BOSSBULLET, 0, 0);
                                        int i_bpx = p_obj.i8_scrx + 0x500 - ((p_mo.i_Width >> 1) << 8);
                                        int i_bpy = p_obj.i8_scry + 0x500 - ((p_mo.i_Height >> 1) << 8);

                                        p_mo.i8_scrx = i_bpx;
                                        p_mo.i8_scry = i_bpy;

                                        if (i_cpy < (i_bpy >> 8))
                                        {
                                            p_mo.i_radius = 0 - (I8_ENEMYBULLSPEED >> 1);
                                        }
                                        else
                                        {
                                            if (getRandomInt(10) > 3)
                                            {
                                                p_mo.i_radius = 0;
                                            }
                                            else
                                            {
                                                p_mo.i_radius = I8_ENEMYBULLSPEED >> 1;
                                            }
                                        }
                                    }
                                }
                                // Firing from second point
                                if (getRandomInt(20) == 15)
                                {
                                    MovingObject p_mo = p_Gsb.getInactiveObject();
                                    if (p_mo != null)
                                    {
                                        p_mo.activate(MovingObject.TYPE_BOSSBULLET, 0, 0);
                                        int i_bpx = p_obj.i8_scrx + 0x500 - ((p_mo.i_Width >> 1) << 8);
                                        int i_bpy = p_obj.i8_scry + 0x1900 - ((p_mo.i_Height >> 1) << 8);

                                        p_mo.i8_scrx = i_bpx;
                                        p_mo.i8_scry = i_bpy;

                                        if (i_cpy < (i_bpy >> 8))
                                        {
                                            p_mo.i_radius = 0 - (I8_ENEMYBULLSPEED >> 1);
                                        }
                                        else
                                        {
                                            if (getRandomInt(10) > 3)
                                            {
                                                p_mo.i_radius = 0;
                                            }
                                            else
                                            {
                                                p_mo.i_radius = (I8_ENEMYBULLSPEED >> 1);
                                            }
                                        }
                                    }
                                }

                                // Firing from third point
                                if (getRandomInt(15) == 10)
                                {
                                    MovingObject p_mo = p_Gsb.getInactiveObject();
                                    if (p_mo != null)
                                    {
                                        p_mo.activate(MovingObject.TYPE_BOSSBULLET, 0, 0);
                                        int i_bpx = p_obj.i8_scrx + 0x500 - ((p_mo.i_Width >> 1) << 8);
                                        int i_bpy = p_obj.i8_scry + 0x2d00 - ((p_mo.i_Height >> 1) << 8);

                                        p_mo.i8_scrx = i_bpx;
                                        p_mo.i8_scry = i_bpy;

                                        if (i_cpy < (i_bpx >> 8))
                                        {
                                            p_mo.i_radius = 0 - (I8_ENEMYBULLSPEED >> 1);
                                        }
                                        else
                                        {
                                            if (getRandomInt(10) > 3)
                                            {
                                                p_mo.i_radius = 0;
                                            }
                                            else
                                            {
                                                p_mo.i_radius = (I8_ENEMYBULLSPEED >> 1);
                                            }
                                        }
                                    }
                                }

                                if (lg_iscollided)
                                {
                                    p_Gsb.i_playerpower -= PLAYER_POWER;
                                    continue;
                                }
                            }
                        }
                        ;
                        break;
                    case MovingObject.TYPE_BOSSBULLET:
                        {
                            if (lg_sheldcollided)
                            {
                                p_Gsb.i_playerScore += 10;
                                convertObjectToOtherType(p_obj, MovingObject.TYPE_EXPLOSION);
                                continue;
                            }

                            p_obj.i8_scrx -= I8_ENEMYBULLSPEED;
                            p_obj.i8_scry += p_obj.i_radius;

                            if ((p_obj.i8_scrx >> 8) > i_screenWidth || (p_obj.i8_scrx >> 8) + p_obj.i_Width < 0 || (p_obj.i8_scry >> 8) <= 0 || (p_obj.i8_scry >> 8) >= i_screenHeight) p_obj.lg_Active = false;

                            if (lg_iscollided)
                            {
                                p_Gsb.i_playerpower -= 10;
                                convertObjectToOtherType(p_obj, MovingObject.TYPE_EXPLOSION);
                                continue;
                            }
                        }
                        ;
                        break;
                    case MovingObject.TYPE_ENEMY0:
                        {
                            if (lg_sheldcollided)
                            {
                                p_Gsb.i_playerScore += 2;
                                convertObjectToOtherType(p_obj, MovingObject.TYPE_EXPLOSION);
                                continue;
                            }

                            p_obj.i8_scrx -= (I8_PLAYER_VERTSPEED << 1);

                            if (i_px < i_ox)
                            {
                                int i_diff = i_oy+(i_oh>>1)-i_cy;

                                if (i_diff>3) p_obj.i8_scry -= (I8_PLAYER_VERTSPEED >> 2);
                                else
                                if (i_diff<(-3)) p_obj.i8_scry += (I8_PLAYER_VERTSPEED >> 2);
                            }

                            if (lg_iscollided)
                            {
                                p_Gsb.i_playerpower -= 3;
                                convertObjectToOtherType(p_obj, MovingObject.TYPE_EXPLOSION);
                                continue;
                            }
                        }
                        ;
                        break;
                    case MovingObject.TYPE_ENEMY1:
                        {
                            // Sine
                            if (lg_sheldcollided)
                            {
                                p_Gsb.i_playerScore += 4;
                                convertObjectToOtherType(p_obj, MovingObject.TYPE_EXPLOSION);
                                continue;
                            }

                            p_obj.i_angle = (p_obj.i_angle + 2) & 63;

                            p_obj.i8_scrx -= (I8_PLAYER_VERTSPEED >> 1);
                            p_obj.i_vy = (i_screenHeight>>1)-(i_oh>>1)+xSine((i_screenHeight-i_oh)>>1, p_obj.i_angle);

                            if (lg_iscollided)
                            {
                                p_Gsb.i_playerpower -= 8;
                                convertObjectToOtherType(p_obj, MovingObject.TYPE_EXPLOSION);
                                continue;
                            }
                        }
                        ;
                        break;
                    case MovingObject.TYPE_ENEMY2:
                        {
                            // CoSine
                            if (lg_sheldcollided)
                            {
                                p_Gsb.i_playerScore += 6;
                                convertObjectToOtherType(p_obj, MovingObject.TYPE_EXPLOSION);
                                continue;
                            }

                            p_obj.i_angle = (p_obj.i_angle + 2) & 63;

                            p_obj.i8_scrx -= (I8_PLAYER_VERTSPEED >> 1);
                            p_obj.i_vy = (i_screenHeight>>1)-(i_oh>>1) +xCoSine((i_screenHeight-i_oh)>>1, p_obj.i_angle);

                            if (lg_iscollided)
                            {
                                p_Gsb.i_playerpower -= 5;
                                convertObjectToOtherType(p_obj, MovingObject.TYPE_EXPLOSION);
                                continue;
                            }
                        }
                        ;
                        break;
                    case MovingObject.TYPE_ENEMY3:
                        {
                            // Round
                            if (lg_sheldcollided)
                            {
                                p_Gsb.i_playerScore += 8;

                                int i_tt = MovingObject.TYPE_EXPLOSION;
                                switch (getRandomInt(499) / 100)
                                {
                                    case 0:
                                        i_tt = MovingObject.TYPE_EXPLOSION;
                                        break;
                                    case 1:
                                        i_tt = MovingObject.TYPE_WEAPON0;
                                        break;
                                    case 2:
                                        i_tt = MovingObject.TYPE_WEAPON1;
                                        break;
                                    case 3:
                                        i_tt = MovingObject.TYPE_WEAPON2;
                                        break;
                                    case 4:
                                        i_tt = MovingObject.TYPE_WEAPON3;
                                        break;
                                }

                                convertObjectToOtherType(p_obj, i_tt);
                                continue;
                            }

                            p_obj.i_angle = (p_obj.i_angle + 3) & 63;

                            p_obj.i8_scrx -= (I8_PLAYER_VERTSPEED >> 1);

                            p_obj.i_vy = (p_obj.i8_scry + xSineFloat(p_obj.i_radius, p_obj.i_angle)) >> 8;
                            p_obj.i_vx = (p_obj.i8_scrx + xCoSineFloat(p_obj.i_radius, p_obj.i_angle)) >> 8;

                            if (lg_iscollided)
                            {
                                p_Gsb.i_playerpower -= 1;
                                convertObjectToOtherType(p_obj, MovingObject.TYPE_EXPLOSION);
                                continue;
                            }
                        }
                        ;
                        break;
                    case MovingObject.TYPE_ENEMY4:
                        {
                            if (lg_sheldcollided)
                            {
                                p_Gsb.i_playerScore += 8;
                                convertObjectToOtherType(p_obj, MovingObject.TYPE_EXPLOSION);
                                continue;
                            }
                            p_obj.i8_scrx -= I8_PLAYER_VERTSPEED;

                            if (getRandomInt(20) == 10)
                            {
                                MovingObject p_eb = p_Gsb.getInactiveObject();
                                if (p_eb != null)
                                {
                                    int i_dy = 0;
                                    int i_dx = 0;
                                    if (i_px > i_ox)
                                    {
                                        i_dx = I8_ENEMYBULLSPEED;
                                    }
                                    else
                                    if (i_px <i_ox)
                                    {
                                        i_dx = 0 - I8_ENEMYBULLSPEED;
                                    }

                                    if (i_oy > i_py)
                                    {
                                        i_dy = 0 - I8_ENEMYBULLSPEED;
                                    }
                                    else
                                    {
                                        i_dy = I8_ENEMYBULLSPEED;
                                    }
                                    p_eb.activate(MovingObject.TYPE_ENEMYBULLET, 0, 0);
                                    p_eb.i8_scrx = p_obj.i8_scrx;
                                    p_eb.i8_scry = p_obj.i8_scry;
                                    p_eb.i_radius = i_dx- (I8_PLAYER_VERTSPEED >> 1);
                                    p_eb.i_angle = i_dy;
                                }
                            }

                            if (lg_iscollided)
                            {
                                p_Gsb.i_playerpower -= 5;
                                convertObjectToOtherType(p_obj, MovingObject.TYPE_EXPLOSION);
                                continue;
                            }
                        }
                        ;
                        break;
                    case MovingObject.TYPE_ENEMYBULLET:
                        {
                            if (lg_sheldcollided)
                            {
                                p_Gsb.i_playerScore += 8;
                                p_obj.lg_Active = false;
                                continue;
                            }

                            if (lg_iscollided)
                            {
                                p_Gsb.i_playerpower -= 3;
                                convertObjectToOtherType(p_obj, MovingObject.TYPE_EXPLOSION);
                                continue;
                            }

                            p_obj.i8_scry += p_obj.i_angle;
                            p_obj.i8_scrx += p_obj.i_radius;

                            if ((p_obj.i8_scrx >> 8) > i_screenWidth || (p_obj.i8_scrx >> 8) + p_obj.i_Width < 0 || (p_obj.i8_scry >> 8) <= 0 || (p_obj.i8_scry >> 8) >= i_screenHeight) p_obj.lg_Active = false;
                        }
                        ;
                        break;
                    case MovingObject.TYPE_WEAPON0:
                        {
                            if (lg_sheldcollided)
                            {
                                convertObjectToOtherType(p_obj, MovingObject.TYPE_EXPLOSION);
                                continue;
                            }

                            if (lg_iscollided)
                            {
                                lg_iscollided = false;
                                p_Gsb.i_PlayerBulletType = MovingObject.TYPE_BULLET1;
                                p_obj.lg_Active = false;
                            }
                            else
                                p_obj.i8_scrx -= (I8_PLAYER_VERTSPEED >> 1);
                        }
                        ;
                        break;
                    case MovingObject.TYPE_WEAPON1:
                        {
                            if (lg_sheldcollided)
                            {
                                p_Gsb.i_playerScore += 8;
                                convertObjectToOtherType(p_obj, MovingObject.TYPE_EXPLOSION);
                                continue;
                            }

                            if (lg_iscollided)
                            {
                                lg_iscollided = false;
                                p_Gsb.i_PlayerBulletType = MovingObject.TYPE_BULLET2;
                                p_obj.lg_Active = false;
                            }
                            else
                                p_obj.i8_scrx -= (I8_PLAYER_VERTSPEED >> 1);

                        }
                        ;
                        break;
                    case MovingObject.TYPE_WEAPON2:
                        {
                            if (lg_sheldcollided)
                            {
                                lg_iscollided = false;
                                p_Gsb.i_playerScore += 8;
                                convertObjectToOtherType(p_obj, MovingObject.TYPE_EXPLOSION);
                                continue;
                            }

                            if (lg_iscollided)
                            {
                                lg_iscollided = false;
                                p_Gsb.p_PlayerShield.lg_Active = true;
                                p_Gsb.i_shielddelay = SHIELD_DELAY;
                                p_obj.lg_Active = false;
                            }
                            else
                                p_obj.i8_scrx -= (I8_PLAYER_VERTSPEED >> 1);
                        }
                        ;
                        break;
                    case MovingObject.TYPE_WEAPON3:
                        {
                            if (lg_sheldcollided)
                            {
                                lg_iscollided = false;
                                p_Gsb.i_playerScore += 8;
                                convertObjectToOtherType(p_obj, MovingObject.TYPE_EXPLOSION);
                                continue;
                            }

                            if (lg_iscollided)
                            {
                                lg_iscollided = false;
                                p_Gsb.p_PlayerShortWeapon.lg_Active = true;
                                p_Gsb.i_energyshielddelay = ENERGYSHIELD_DELAY;
                                p_obj.lg_Active = false;
                            }
                            else
                                p_obj.i8_scrx -= (I8_PLAYER_VERTSPEED >> 1);
                        }
                        ;
                        break;
                }

                if ((p_obj.getScrX() + i_ow) < 0) p_obj.lg_Active = false;
            }
        }

        if (p_Gsb.i_playerpower < 0) p_Gsb.i_playerpower = 0;
    }

    private void processPlayerBullets()
    {
        for (int li = 0; li < MAX_PLAYERBULLETS; li++)
        {
            MovingObject p_bull = p_Gsb.ap_PlayerBullets[li];
            if (p_bull.lg_Active)
            {
                p_bull.i8_scrx += I8_PLAYERBULLSPEED;
                if ((p_bull.i8_scrx >> 8) >= i_screenWidth)
                {
                    p_bull.lg_Active = false;
                    p_Gsb.i_playerbulletinactive++;
                }
            }
        }
    }

    public String getGameID()
    {
        return "ZZYNAPS";
    }

    public int getMaxSizeOfSavedGameBlock()
    {
        return 1200;
    }

    public Zynaps_SB(int _screenWidth, int _screenHeight, com.GameArea _gameActionListener)
    {
        super(_screenWidth, _screenHeight, _gameActionListener);
        i_CellsPerScreen = (_screenWidth + VIRTUALCELL_WIDTH - 1) / VIRTUALCELL_WIDTH;
    }
}
