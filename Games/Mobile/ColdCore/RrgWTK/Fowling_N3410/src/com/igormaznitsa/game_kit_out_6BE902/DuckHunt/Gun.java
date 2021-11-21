package com.igormaznitsa.game_kit_out_6BE902.DuckHunt;

public class Gun
{
    public static final int STATE_READY = 0;
    public static final int STATE_CHARGING = 1;
    public static final int STATE_FIRE = 2;

    public static final int GUN_TURN_FRAMES = 5;
    public static final int GUN_CHARGING_FRAMES = 7;
    public static final int GUN_CHARGING_TICKS = 2;
    public static final int GUN_FIRE_FRAMES = 3;
    public static final int GUN_FIRE_TICKS = 1;

    private int i8_screencoeff;
    private int i_scrcenterx;

    protected static final byte[] ab_chargingframes = new byte[]
    {
        0,
        1,
        2,
        3,
        4,
        1,
        0
    };

    protected static final byte[] ab_gunframeoffset = new byte[]
    {
        -38, 3,
        -28, 1,
        -13, 0,
        2, 1,
        2, 3,
    };

    protected static final byte[] ab_chargingoffset = new byte[]
    {
        -13, 0,
        -12, 4,
        -12, 16,
        -12, 16,
        -12, 16
    };

    protected static final byte[] ab_gunfireoffset = new byte[]
    {
        2, 1,
        1, 2,
        0, 3,
        -1, 2,
        -2, 1,
    };

    protected int i_state;
    protected int i_frame;
    protected int i_tick;

    protected int i8_sight_scrx;
    protected int i8_sight_scry;

    protected int i_gun_scrx;
    protected int i_gun_scry;

    private int i_scrwidth;
    private int i_scrheight;
    protected boolean lg_backmove;
    private int i8_vertconstant;
    private int i8_horzconstant;
    private int i_fireXoffst;
    private int i_fireYoffst;

    protected int i_firePhase;
    protected int i_bulletsingun;
    protected boolean lg_sightvisibled;
    protected int i_firingx;
    protected int i_firingy;

    public Gun(int _scrwidth, int _scrheight)
    {
        i_bulletsingun = 0;
        i_scrwidth = _scrwidth;
        i_scrheight = _scrheight;

        i8_vertconstant = (DuckHunt_SB.CHARGING_FRAME_HEIGHT << 8) / _scrheight;
        i8_horzconstant = (GUN_TURN_FRAMES << 8) / _scrwidth;

        i8_screencoeff = ((_scrwidth <<8)/3)/i_scrheight;

        i_scrcenterx = _scrwidth >> 1;
        sightToCenter();
    }

    public void setSightScrCoord(int _i8_scrx, int _i8_scry)
    {
        i8_sight_scrx = _i8_scrx;
        i8_sight_scry = _i8_scry;

        int i_scry = _i8_scry >>8;
        int i_scrx = _i8_scrx >>8;
        int i_dx = ((i_scrheight - i_scry)*i8_screencoeff)>>8;
        if (i_scrx>i_scrcenterx)
        {
            i_dx = i_scrwidth-i_dx-1;
            if (i_scrx > i_dx) i_scrx = i_dx;
        }
        else
        {
            if (i_scrx < i_dx) i_scrx = i_dx;
        }

        i_frame = (i8_horzconstant * i_scrx) >> 8;


        i_gun_scry = i_scrheight - DuckHunt_SB.CHARGING_FRAME_HEIGHT + ((i8_vertconstant * i_scry) >> 8) + ab_gunframeoffset[(i_frame << 1) + 1];
        i_gun_scrx = (i_scrwidth >> 1) + ab_gunframeoffset[i_frame << 1];
    }

    public boolean checkShot(int _scrx, int _scry, int _width, int _height)
    {
        return !((_scrx + _width <= i_firingx) || (_scry + _height <= i_firingy) || (_scrx >= i_firingx + DuckHunt_SB.DEFEATZONE_WIDTH) || (_scry >= i_firingy + DuckHunt_SB.DEFEATZONE_HEIGHT));
    }

    public void setState(int _newstate)
    {
        i_state = _newstate;
        i_tick = 0;
        lg_backmove = false;

        switch (_newstate)
        {
            case STATE_FIRE:
                {
                    i_bulletsingun--;
                    i_fireXoffst = ab_gunfireoffset[i_frame << 1];
                    i_fireYoffst = ab_gunfireoffset[(i_frame << 1) + 1];
                    i_firingx = (i8_sight_scrx >> 8) + DuckHunt_SB.DEFEATZONE_OFFSETX;
                    i_firingy = (i8_sight_scry >> 8) + DuckHunt_SB.DEFEATZONE_OFFSETY;
                    i_firePhase = 0;
                }
                ;
                break;
            case STATE_CHARGING:
                {
                    i_bulletsingun = 2;
                    i_frame = 0;
                }
                ;
                break;
        }
    }

    public int getSightX()
    {
        return i8_sight_scrx >> 8;
    }

    public int getSightY()
    {
        return i8_sight_scry >> 8;
    }

    public int getGunX()
    {
        switch (i_state)
        {
            case STATE_CHARGING:
                return (i_scrwidth >> 1) + ab_chargingoffset[getFrame() << 1];
            case STATE_FIRE:
                return i_gun_scrx + i_fireXoffst * (i_firePhase + 1);
            default :
                return i_gun_scrx;
        }
    }

    public int getGunY()
    {
        switch (i_state)
        {
            case STATE_CHARGING:
                return i_scrheight - DuckHunt_SB.CHARGING_FRAME_HEIGHT + ab_chargingoffset[(getFrame() << 1) + 1];
            case STATE_FIRE:
                return i_gun_scry + i_fireYoffst * (i_firePhase + 1);
            default :
                return i_gun_scry;
        }
    }

    public int getFrame()
    {
        switch (i_state)
        {
            case STATE_CHARGING:
                return ab_chargingframes[i_frame];
            default :
                return i_frame;
        }
    }

    public int getState()
    {
        return i_state;
    }

    private void sightToCenter()
    {
        setSightScrCoord((i_scrwidth >> 1) - (DuckHunt_SB.SIGHT_FRAME_WIDTH >> 1) << 8, ((i_scrheight >> 1) - DuckHunt_SB.SIGHT_FRAME_HEIGHT) << 8);
    }

    // return true when is ready
    public boolean processGun()
    {
        i_tick++;
        switch (i_state)
        {
            case STATE_CHARGING:
                {
                    lg_sightvisibled = false;
                    if (i_tick >= GUN_CHARGING_TICKS)
                    {
                        i_tick = 0;
                        i_frame++;
                        if (i_frame >= GUN_CHARGING_FRAMES)
                        {
                            setState(STATE_READY);
                            sightToCenter();
                            return true;
                        }
                    }
                }
                ;
                break;
            case STATE_FIRE:
                {
                    lg_sightvisibled = false;
                    if (i_tick >= GUN_FIRE_TICKS)
                    {
                        i_tick = 0;

                        if (lg_backmove)
                        {
                            i_firePhase--;
                            if (i_firePhase <= 0)
                            {
                                setState(STATE_READY);
                                return true;
                            }
                        }
                        else
                        {
                            i_firePhase++;
                            if (i_firePhase >= GUN_FIRE_FRAMES)
                            {
                                i_firePhase--;
                                lg_backmove = true;
                            }
                        }
                    }
                }
                ;
                break;
            case STATE_READY:
                {
                    lg_sightvisibled = true;
                    i_tick = 0;
                    return true;
                }
        }
        return false;
    }

    public boolean isSightVisibled()
    {
        return lg_sightvisibled;
    }

}
