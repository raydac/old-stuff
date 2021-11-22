package com.igormaznitsa.game_kit_E5D3F2.RiverRescue;

public class Splash
{
    public static final int FRAME_NUMBER = 6;

    public static final int [] ai_sizes = new int []
    {
        // width, height, delay
        13,5,2,
        13,6,3,
        18,10,4,
        20,10,5,
        21,10,6,
        20,5,7
    };

    public boolean lg_Active;
    protected int i_cx;
    protected int i_cy;
    protected int i_maxDelay;
    protected int i_delay;

    public int i_X;
    public int i_Y;

    public int i_Width;
    public int i_Height;

    public int i_Frame;

    public Splash()
    {
        lg_Active = false;
    }

    protected void setFrame(int _frame)
    {
        i_Frame = _frame;
        int i_indx = _frame * 3;
        i_Width = ai_sizes[i_indx++];
        i_Height = ai_sizes[i_indx++];
        i_maxDelay = ai_sizes[i_indx];

        i_X = i_cx - (i_Width>>1);
        i_Y = i_cy - (i_Height>>1);

        i_delay = 0;
    }

    protected void process()
    {
        i_delay ++;
        if (i_delay>=i_maxDelay)
        {
            if (i_Frame<(FRAME_NUMBER-1))
            {
                setFrame(i_Frame+1);
            }
            else
            {
                lg_Active = false;
            }
        }
    }

    protected void activate(int _x, int _y)
    {
        lg_Active = true;
        i_cx = _x;
        i_cy = _y;
        setFrame(0);
    }
}
