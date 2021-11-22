package com.igormaznitsa.game_kit_E5D3F2.RiverRescue;

public class MovingObject
{
    public int i_y;
    public int i_x;

    public int i_Type;
    public int i_Frame;

    protected int i_width;
    protected int i_height;
    protected int i_delay;
    protected int i_maxFrame;
    protected int i_maxDelay;
    protected boolean lg_isBackMove;
    protected boolean lg_backMove;

    public static final int OBJECT_DROWNINGMAN = 0;
    public static final int OBJECT_FUEL = 1;
    public static final int OBJECT_MINE = 2;
    public static final int OBJECT_EXPLOSION = 3;
    public static final int OBJECT_AIRPLANEVERT = 4;
    public static final int OBJECT_HELICOPTERHORZ = 5;

    public boolean lg_Active;

    protected boolean lg_bombdrop;

    public static final int [] ai_animations =
    {
      // frame number, delay, is backmoving, width, height

      // DROWING MAN
        3, 2, 1, 7, 4,
      // FUEL
        2, 2, 1, 6, 4,
      // MINE
        4, 3, 1, 5, 5,
      // EXPLOSION
        6, 1, 0, 19, 19,
      // AIRPLANE VERT
        4, 1, 0, 22, 15,
      // HELICOPTER RIGHT
        3, 1, 0, 21, 25
    };

    public MovingObject()
    {
        lg_Active = false;
    }

    protected void activate(int _type,int _x,int _y)
    {
        lg_Active = true;
        lg_bombdrop = false;

        i_Type = _type;
        int i_indx = _type * 5;

        i_x = _x;
        i_y = _y;

        i_maxFrame = ai_animations[i_indx++];
        i_maxDelay = ai_animations[i_indx++];
        if (ai_animations[i_indx++] != 0)
            lg_isBackMove = true;
        else
            lg_isBackMove = false;
        i_width = ai_animations[i_indx++];
        i_height = ai_animations[i_indx++];

        i_Frame = 0;
        i_delay = 0;
        lg_backMove = false;
    }

    // Return true if peak of animation
    protected boolean process()
    {
        i_delay ++;
        boolean lg_result = false;
        if (i_delay >= i_maxDelay)
        {
            i_delay = 0;

            if (lg_isBackMove)
            {
                if (lg_backMove)
                {
                    i_Frame --;
                    if (i_Frame < 0)
                    {
                        i_Frame = 1;
                        lg_backMove = false;
                    }
                }
                else
                {
                    i_Frame ++;
                    if (i_Frame >= i_maxFrame)
                    {
                        i_Frame -= 2;
                        lg_backMove = true;
                        lg_result = true;
                    }
                }
            }
            else
            {
                i_Frame ++;
                if (i_Frame>=i_maxFrame)
                {
                    i_Frame = 0;
                    lg_result = true;
                }
            }
        }
        return lg_result;
    }

}
