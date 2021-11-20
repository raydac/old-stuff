package com.igormaznitsa.game_kit_E5D3F2.Zynaps;

public class Player
{
    public static final int MAXFRAMES = 7;
    public static final int EXPLOSIVEFRAMES = 5;
    private static final int MAXTICKS_ROADSTATE = 1;
    private static final int MAXTICKS_EXPLOSIVE = 1;

    protected static final int SPRITE_WIDTH = 14;
    protected static final int SPRITE_HEIGHT = 14;

    public int i_Frame;
    protected int i_maxticks;
    protected int i_tick;

    protected int i_width;
    protected int i_height;

    protected int i8_cx;
    protected int i8_cy;

    protected int i_xoffst;
    protected int i_yoffst;

    public boolean lg_Destroyed;

    protected static final int[] ai_explosesizes = new int []
            {
                // width, height, xoffst, yoffst
                11, 11, 1, 1,
                17, 17, -1, -1,
                26, 27, -6, -6,
                26, 27, -6, -6,
                26, 27, -6, -6
            };

    protected void Init(int _centerx,int _scrheight,MovingObject _shield,MovingObject _short)
    {
        i_xoffst = 0;
        i_yoffst = 0;

        i_width = SPRITE_WIDTH;
        i_height = SPRITE_HEIGHT;

        lg_Destroyed = false;
        setFrame(3);
        i_maxticks = MAXTICKS_ROADSTATE;
        setCX(_centerx<<8);
        setCY(((_scrheight>>1)-(i_height>>1))<<8);

        _shield.i_radius = Zynaps_SB.SHIELD_RADIUS;
        _shield.i_angle = 0;

        _shield.i8_scrx = i8_cx + ((i_width>>1)<<8);
        _shield.i8_scry = i8_cy + ((i_height>>1)<<8);
    }

    public Player()
    {
        setFrame(3);
        i_maxticks = MAXTICKS_ROADSTATE;
        i_width = SPRITE_WIDTH;
        i_height = SPRITE_HEIGHT;
    }

    public int getScrX()
    {
        return (i8_cx>>8)+i_xoffst;
    }

    public int getScrY()
    {
        return (i8_cy>>8)+i_yoffst;
    }

    protected void setCX(int _i8_cx)
    {
        i8_cx = _i8_cx;
    }

    protected void setCY(int _i8_cy)
    {
        i8_cy = _i8_cy;
    }

    protected void setFrame(int _frame)
    {
        i_Frame = _frame;

        int i_indx = _frame << 2;

        if (lg_Destroyed)
        {
            i_width = ai_explosesizes[i_indx++];
            i_height = ai_explosesizes[i_indx++];
            i_xoffst = ai_explosesizes[i_indx++];
            i_yoffst = ai_explosesizes[i_indx];
        }
    }

    protected void destroyBoat()
    {
        lg_Destroyed = true;
        setFrame(0);
        i_tick = 0;
        i_maxticks = MAXTICKS_EXPLOSIVE;
    }

    // Return true if the peak of an animation is presented
    protected boolean process(int _key)
    {
        boolean lg_result = false;

        if (lg_Destroyed)
        {
            i_tick++;
            if (i_tick >= i_maxticks)
            {
                i_tick = 0;

                int i_int = i_Frame + 1;
                if (i_int >= EXPLOSIVEFRAMES)
                {
                    lg_result = true;
                }
                else
                {
                    setFrame(i_int);
                }
            }
            return lg_result;
        }

        return false;
    }

}
