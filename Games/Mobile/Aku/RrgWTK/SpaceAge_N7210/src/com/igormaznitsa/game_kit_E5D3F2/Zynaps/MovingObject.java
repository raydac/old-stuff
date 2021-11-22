package com.igormaznitsa.game_kit_E5D3F2.Zynaps;

public class MovingObject
{
    public static final int TYPE_ENEMY0 = 0;
    public static final int TYPE_ENEMY1 = 1;
    public static final int TYPE_ENEMY2 = 2;
    public static final int TYPE_ENEMY3 = 3;
    public static final int TYPE_ENEMY4 = 4;
    public static final int TYPE_BOSS   = 5;
    public static final int TYPE_EXPLOSION = 6;
    public static final int TYPE_BULLET1 = 7;
    public static final int TYPE_BULLET2 = 8;
    public static final int TYPE_ENEMYBULLET = 9;
    public static final int TYPE_BOSSBULLET = 10;
    public static final int TYPE_SHIELD = 11;
    public static final int TYPE_WEAPON0 = 12;
    public static final int TYPE_WEAPON1 = 13;
    public static final int TYPE_WEAPON2 = 14;
    public static final int TYPE_WEAPON3 = 15;
    public static final int TYPE_ENERGYSHIELD = 16;

    public boolean lg_Active;
    public int i_Frame;
    public int i_Type;
    public int i_Width;
    public int i_Height;

    protected int i_maxDelay;
    protected int i_maxFrames;
    protected boolean lg_isbackmove;

    protected boolean lg_backmove;
    protected int i_delay;

    protected int i8_scrx;
    protected int i8_scry;

    protected int i_vx;
    protected int i_vy;

    protected int i_angle;
    protected int i_radius;

    public MovingObject()
    {
        lg_Active = false;
    }

    public static final int [] ai_animationarray =
    {
        // frames, delay, backanimation, width, height
        //ENEMY0
        1,100,0,11,10,
        //ENEMY1
        2,1,0,12,12,
        //ENEMY2
        1,100,0,12,12,
        //ENEMY3
        3,2,1,14,14,
        //ENEMY4
        3,1,1,12,12,
        //BOSS
        4,3,0,48,45,
        //EXPLOSION
        6,1,0,19,19,
        //BULLET1
        1,100,0,8,5,
        //BULLET2
        1,100,0,8,11,
        //ENEMYBULLET
        1,100,0,6,6,
        //BOSSBULLET
        3,1,1,13,7,
        //SHIELD
        2,1,0,5,5,
        //WEAPON0
        2,1,0,12,12,
        //WEAPON1
        2,1,0,12,12,
        //WEAPON2
        2,1,0,12,12,
        //WEAPON3
        2,1,0,12,12,
        //ENERGY SHIELD
        3,1,1,5,18
    };

    public int getScrX()
    {
        switch (i_Type)
        {
            case TYPE_ENEMY3 :
            case TYPE_SHIELD :
                return i_vx;
            default: return i8_scrx >> 8;
        }
    }

    public int getScrY()
    {
        switch (i_Type)
        {
            case TYPE_ENEMY1 :
            case TYPE_ENEMY2 :
            case TYPE_ENEMY3 :
            case TYPE_SHIELD :
                return i_vy;
            default: return i8_scry >> 8;
        }
    }

    protected void activate(int _type, int _x, int _y)
    {
        lg_Active = true;
        i_Type = _type;
        i8_scrx = _x << 8;
        i8_scry = _y << 8;

        int i_indx = _type * 5;

        i_maxFrames = ai_animationarray[i_indx++];
        i_maxDelay = ai_animationarray[i_indx++];
        if (ai_animationarray[i_indx++]!=0) lg_isbackmove = true; else lg_isbackmove = false;
        i_Width = ai_animationarray[i_indx++];
        i_Height = ai_animationarray[i_indx];

        i_vx = -100;
        i_vy = -100;

        i_Frame = 0;
        i_delay = 0;
        lg_backmove = false;
    }

    // return true if the peak of an animation
    protected boolean process()
    {
        i_delay ++;
        boolean lg_result = false;
        if (i_delay >= i_maxDelay)
        {
            i_delay = 0;

            if (lg_isbackmove)
            {
                if (lg_backmove)
                {
                    i_Frame --;
                    if (i_Frame < 0)
                    {
                        i_Frame = 1;
                        lg_backmove = false;
                    }
                }
                else
                {
                    i_Frame ++;
                    if (i_Frame >= i_maxFrames)
                    {
                        i_Frame -= 2;
                        lg_backmove = true;
                        lg_result = true;
                    }
                }
            }
            else
            {
                i_Frame ++;
                if (i_Frame>=i_maxFrames)
                {
                    i_Frame = 0;
                    lg_result = true;
                }
            }
        }
        return lg_result;
    }
}
