package com.igormaznitsa.game_kit_out_6BE902.Penalty;

public class PlayerObj
{
    public static final byte FRAME_WIDTH = 8;
    public static final byte FRAME_HEIGHT = 12;

    public static final byte FRAMES = 4;
    public static final byte DELAY = 3;

    protected int i_frame;
    protected int i_tick;
    protected int i_scrx;
    protected int i_scry;

    protected boolean lg_active;
    protected boolean lg_beated;

    public PlayerObj()
    {
        i_frame = 0;
        i_tick = 0;
        lg_active = false;
        lg_beated = false;
    }

    public int getScrX()
    {
        return i_scrx;
    }

    public int getScrY()
    {
        return i_scry;
    }

    public int getFrame()
    {
        return i_frame;
    }

    public void initPlayer(int _x,int _y)
    {
        i_scrx = _x;
        i_scry = _y;
        i_frame = 0;
        i_tick = 0;
        lg_active = false;
        lg_beated = false;
    }

    public void activate()
    {
        lg_active = true;
    }

    public boolean processPlayer()
    {
        if (lg_active)
        {
                i_tick++;
                if (i_tick>=DELAY)
                {
                    i_tick = 0;
                    i_frame++;
                    if (i_frame>=FRAMES)
                    {
                       i_frame = 0;
                        lg_beated = true;
                        lg_active = false;
                        return true;
                    }
                }
        }
        else
        {
            i_frame = 0;
            i_tick = 0;
        }
        return false;
    }


}
