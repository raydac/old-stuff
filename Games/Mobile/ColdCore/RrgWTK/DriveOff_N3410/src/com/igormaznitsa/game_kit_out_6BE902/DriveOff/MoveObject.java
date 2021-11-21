package com.igormaznitsa.game_kit_out_6BE902.DriveOff;

public class MoveObject
{
    // автомобиль игрока
    public static final int TYPE_PLAYERCAR = 0;
    // машина тип 0
    public static final int TYPE_CAR0 = 1;
    // машина тип 1
    public static final int TYPE_CAR1 = 2;
    // машина тип 2
    public static final int TYPE_CAR2 = 3;
    // мотоциклист
    public static final int TYPE_MOTO = 4;
    // полицейский вертолет
    public static final int TYPE_HELYCOPTER = 5;
    // полицейская машина
    public static final int TYPE_POLICECAR = 6;
    // пешеход голосующий
    public static final int TYPE_TRAVELLER0 = 7;
    // пешеход идущий
    public static final int TYPE_TRAVELLER1 = 8;
    // граната игрока
    public static final int TYPE_GRENADE = 9;
    // ракета полиции
    public static final int TYPE_ROCKET = 10;
    // взрыв
    public static final int TYPE_EXPLODING  = 11;

    public static final int STATE_ALIVE = 0;
    public static final int STATE_KILLED = 1;

    protected int i_firedelay;

    private static final int [] ai_widthmask =
    {
      0x01,
      0x03,
      0x07,
      0x0F,
      0x1F,
      0x3F,
      0x7F,
      0xFF
    };

    public static final int [] ai_propertiesarray =
    {
        // width, height, live frames, live delay, livebackanimation, death frames, death delay, max damages, vertspeed, horizspeed
        // playercar
        7, 12, 1, 1, 0, 1, 3, 100, 3, 2,
        // car0
        10, 30, 1, 1, 0, 1, 3, 5, 1, 2,
        // car1
        10, 40, 1, 1, 0, 1, 3, 10, 2, 2,
        // car2
        7, 12, 1, 1, 0, 1, 3, 5, 5, 2,
        // motocycle
        5, 10, 1, 1, 0, 1, 3, 5, 4, 2,
        // cop helycopter
        18, 18, 3, 1, 0, 1, 10, 30, 3, 2,
        // police car
        7, 12, 3, 1, 0, 1, 3, 30, 5, 2,
        // traveller 0
        6, 5, 4, 1, 0, 3, 4, 0, 0, 1,
        // traveller 1
        6, 5, 3, 1, 1, 3, 4, 0, 0, 0,
        // player's grenade
        3, 3, 1, 0, 0, 0, 0, 5, 8, 0,
        // Copter's rocket
        3, 3, 1, 0, 0, 0, 0, 5, 2, 0,
        // Explode
        12, 12, 0, 0, 0, 3, 3, 1, 0, 0
    };

    protected int i_type;
    protected int i_state;
    protected int i_width;
    protected int i_height;
    protected int i_maxframes;
    protected int i_maxticks;
    protected int i_maxliveframes;
    protected int i_maxliveticks;
    protected int i_maxdieframes;
    protected int i_maxdieticks;
    protected int i_maxdamage;
    protected int i_vertspeed;
    protected int i_horzspeed;

    protected int i_hitoffx;
    protected int i_hitoffy;

    protected int i_mask;
    protected int i_curmask;

    protected int i_curline;
    protected int i_cellheight;

    protected boolean lg_backflag;
    protected boolean lg_backanimation;

    protected int i_curframe;
    protected int i_curtick;
    protected int i_curdamage;

    protected int i_scrx;
    protected int i_scry;

    protected int i_dirx;
    protected int i_diry;

    protected boolean lg_active;

    public boolean isActive()
     {
       return lg_active;
     }

    public int getCurrentDamage()
       {
           return i_curdamage;
       }

    public int getCurrentFrame()
    {
        return i_curframe;
    }

    public int getState()
    {
        return i_state;
    }

    public MoveObject()
    {
        lg_active = false;
    }

    public int getType()
    {
        return i_type;
    }

    public void setX(int _x)
    {
        i_scrx = _x;
        _x = _x / DriveOff_SB.VIRTUALCELL_WIDTH;
        i_curmask = i_mask << _x;
    }

    public void setY(int _y)
    {
        i_scry = _y;
        i_curline = _y / DriveOff_SB.VIRTUALCELL_HEIGHT;
    }

    public int getCurrentLine()
    {
        return i_curline;
    }

    public int getX()
    {
        return i_scrx;
    }

    public int getY()
    {
        return i_scry;
    }

    public int getWidth()
    {
        return i_width;
    }

    public int getHeight()
    {
        return i_height;
    }

    protected void activate(int _type,int _scrx,int _scry)
    {
        i_hitoffx = 0;
        i_hitoffy = 0;
        i_type = _type;
        lg_active = true;
        int i_indx = _type *10;
        i_width = ai_propertiesarray[i_indx++];
        i_height = ai_propertiesarray[i_indx++];
        i_cellheight = i_height / DriveOff_SB.VIRTUALCELL_HEIGHT;
        if (i_height % DriveOff_SB.VIRTUALCELL_HEIGHT !=0) i_cellheight++;
        i_maxliveframes = ai_propertiesarray[i_indx++];
        i_maxliveticks = ai_propertiesarray[i_indx++];
        if (ai_propertiesarray[i_indx++]!=0)
            lg_backanimation = true;
        else
            lg_backanimation = false;
        i_maxdieframes = ai_propertiesarray[i_indx++];
        i_maxdieticks = ai_propertiesarray[i_indx++];
        i_maxdamage = ai_propertiesarray[i_indx++];
        i_vertspeed = ai_propertiesarray[i_indx++];
        i_horzspeed = ai_propertiesarray[i_indx++];
        setState(STATE_ALIVE);

        int i_maskwdth = i_width / DriveOff_SB.VIRTUALCELL_WIDTH;
        if (i_width % DriveOff_SB.VIRTUALCELL_WIDTH !=0 ) i_maskwdth++;

        i_mask = ai_widthmask[i_maskwdth];
        setX(_scrx);
        setY(_scry);
    }

    protected boolean processDamage(int _incvalue)
    {
        if (i_state==STATE_ALIVE)
        {
            i_curdamage += _incvalue;
            if (i_curdamage>=i_maxdamage)
            {
                setState(STATE_KILLED);
                return true;
            }
        }
        return false;
    }

    protected void setState(int _state)
    {
        switch(_state)
        {
            case STATE_ALIVE : i_maxframes = i_maxliveframes; i_maxticks = i_maxliveticks; i_curdamage = 0; break;
            case STATE_KILLED : i_maxframes = i_maxdieframes; i_maxticks = i_maxdieticks; break;
        }
        i_curframe = 0;
        i_curtick = 0;
        i_state = _state;
        lg_backflag = false;
    }

    protected boolean processObject()
    {
        if (i_hitoffx>0) i_hitoffx--;
        else
        if (i_hitoffx <0) i_hitoffx++;
        if (i_hitoffy>0) i_hitoffy--;
        else
        if (i_hitoffy <0) i_hitoffy++;

        i_curtick ++;
        if (i_curtick>=i_maxticks)
        {
            i_curtick=0;
            if (lg_backanimation && i_state!=STATE_KILLED)
            {
                if (lg_backflag)
                {
                    i_curframe --;
                    if (i_curframe<=0) lg_backflag = false;
                }
                else
                {
                    i_curframe ++;
                    if (i_curframe>=i_maxframes)
                    {
                        i_curframe --;
                        lg_backflag = true;
                    }
                }
            }
            else
            {
                i_curframe++;
                if (i_curframe>=i_maxframes)
                {
                    i_curframe = 0;
                    if (i_state==STATE_KILLED) lg_active = false;
                }
            }
        }
        if (lg_active) return false; else return true;
    }

}
