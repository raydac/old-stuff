package com.igormaznitsa.game_kit_out_6BE902.DuckHunt;

public class Score
{
    public static final int SCORE_100 = 0;
    public static final int SCORE_200 = 1;
    public static final int SCORE_300 = 2;
    public static final int SCORE_400 = 3;
    public static final int SCORE_500 = 4;
    public static final int SCORE_600 = 5;
    public static final int SCORE_700 = 6;

    public static final int HEIGHT = 5;

    protected int [] ai_widths = new int []
    {
        12,
        12,
        12,
        12,
        12,
        12,
        12
    };

    public static final int LIVE_TICKS = 10;
    public static final int VERT_SPEED_I8 = 0x70;

    protected int i_type;
    protected int i_ticks;
    protected boolean lg_active;
    protected int i_scrx;
    protected int i8_scry;
    protected int i_width;

    public Score()
    {
        lg_active = false;
    }

    public int getScrX()
    {
        return i_scrx;
    }

    public int getScrY()
    {
        return i8_scry>>8;
    }

    public int getType()
    {
        return i_type;
    }

    public boolean isActive()
    {
        return lg_active;
    }

    public void activation(int _x,int _y,int _type)
    {
        lg_active = true;
        i_scrx = _x;
        i8_scry = _y<<8;
        i_type = _type;
        i_ticks = 0;
        i_width = ai_widths[i_type];
    }

    public boolean process()
    {
        i8_scry -= VERT_SPEED_I8;
        i_ticks ++;
        if (i_ticks>=LIVE_TICKS)
        {
            lg_active = false;
            return true;
        }
        return false;
    }
}
