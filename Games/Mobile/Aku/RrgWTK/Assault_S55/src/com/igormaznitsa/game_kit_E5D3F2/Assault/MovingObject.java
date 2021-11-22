package com.igormaznitsa.game_kit_E5D3F2.Assault;

public class MovingObject
{
    public static final int TYPE_ASSAULTER = 0;
    public static final int TYPE_STONE = 1;
    public static final int TYPE_ARROW = 2;
    public static final int TYPE_PLAYER = 3;

    public static final int STATE_LEFT = 0;
    public static final int STATE_RIGHT = 1;
    public static final int STATE_UP = 2;
    public static final int STATE_DOWN = 3;
    public static final int STATE_EXPLODE = 4;

    public int i_Frame;
    public boolean lg_Active;
    public int i_Type;
    public int i_State;

    protected int i_delay;
    protected int i_scrx;
    protected int i_scry;

    protected int i_maxframes;
    protected int i_maxdelay;
    protected int i_width;
    protected int i_height;
    protected int i_xoffst;
    protected int i_yoffst;

    protected boolean lg_onlyfinal;

    private boolean lg_animationcycled;

    public static final int[] ai_frames =
            {
                // frames,delay,width,height,x_offst,y_offst, animation cycled
                //Assaulter
                // LEFT
                4,1,6,8,0,0,1,
                // RIGHT
                4,1,6,8,0,0,1,
                // UP
                4,1,7,8,-1,0,1,
                // DOWN
                4,1,10,8,-1,0,1,
                // EXPLODE
                4,1,11,8,0,0,0,

                //Stone
                // LEFT
                1,1,6,5,0,0,1,
                // RIGHT
                1,1,6,5,0,0,1,
                // UP
                1,1,6,5,0,0,1,
                // DOWN
                1,1,6,5,0,0,1,
                // EXPLODE
                4,1,9,8,-2,-3,0,

                //Arrow
                // LEFT
                1,1,3,8,0,0,1,
                // RIGHT
                1,1,3,8,0,0,1,
                // UP
                1,1,3,8,0,0,1,
                // DOWN
                1,1,3,8,0,0,1,
                // EXPLODE
                1,1,3,8,0,0,0,

                //Player
                // LEFT
                4,1,7,8,0,0,1,
                // RIGHT
                4,1,7,8,0,0,1,
                // UP
                1,1,8,8,0,0,0,
                // DOWN
                3,1,8,8,0,0,0,
                // EXPLODE
                4,1,12,8,0,0,0
            };

    public MovingObject()
    {
        lg_Active = false;
    }

    public int getScrX()
    {
        return i_scrx + i_xoffst;
    }

    public int getScrY()
    {
        return i_scry + i_yoffst;
    }

    protected void activate(int _type,int _state)
    {
        lg_Active = true;
        i_Type = _type;
        i_State = -1;
        setState(_state,false);
    }

    protected void setState(int _state,boolean _onlyfinal)
    {
        if (i_State == _state) return;
        lg_onlyfinal = _onlyfinal;
        i_State = _state;
        int i_indx = i_Type * 35 + _state * 7;
        i_Frame = 0;
        i_maxframes = ai_frames[i_indx++];
        i_maxdelay = ai_frames[i_indx++];
        i_width = ai_frames[i_indx++];
        i_height = ai_frames[i_indx++];
        i_xoffst = ai_frames[i_indx++];
        i_yoffst = ai_frames[i_indx++];
        if (ai_frames[i_indx++] == 0)
            lg_animationcycled = false;
        else
            lg_animationcycled = true;
    }

    protected boolean process()
    {
        boolean lg_result = false;
        if (lg_onlyfinal)
        {
            i_Frame = i_maxframes - 1;
            return true;
        }
        else
        {
            i_delay++;
            if (i_delay >= i_maxdelay)
            {
                i_delay = 0;
                i_Frame++;
                if (i_Frame >= i_maxframes)
                {
                    if (lg_animationcycled)
                    {
                        i_Frame = 0;
                    }
                    else
                    {
                        i_Frame = i_maxframes - 1;
                        lg_result = true;
                    }
                }
            }
        }
        return lg_result;
    }

}
