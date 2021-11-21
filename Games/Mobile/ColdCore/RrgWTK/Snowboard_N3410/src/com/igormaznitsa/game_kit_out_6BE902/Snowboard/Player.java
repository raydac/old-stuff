package com.igormaznitsa.game_kit_out_6BE902.Snowboard;

public class Player
{
    public static final byte STATE_ROAD = 0;
    public static final byte STATE_JUMP = 1;
    public static final byte STATE_FALLEN = 2;

    public static final int MAXFRAMES_ROADSTATE = 7;
    public static final int MAXFRAMES_JUMPSTATE = 3;
    public static final int MAXFRAMES_FALLENSTATE = 3;

    private static final int MAXTICKS_ROADSTATE = 1;
    private static final int MAXTICKS_FALLENSTATE = 2;

    public int i_scrx;
    public int i_scry;

    public int i_state;
    public int i_frame;
    protected int i_maxticks;
    protected int i_tick;
    protected boolean lg_backmove;

    public Player()
    {
        setState(STATE_ROAD,0);
    }

    protected void setState(int _newstate,int _jumpticks)
    {
        i_frame = 0;
        lg_backmove = false;

        switch (_newstate)
        {
            case STATE_ROAD:
                {
                    i_frame = 3;
                    i_maxticks = MAXTICKS_ROADSTATE;
                }
                ;
                break;
            case STATE_FALLEN:
                {
                    i_maxticks = MAXTICKS_FALLENSTATE;
                }
                ;
                break;
            case STATE_JUMP:
                {
                    i_maxticks = _jumpticks;
                }
                ;
                break;
        }
        i_state = _newstate;
    }

    // Return true if the peak of an animation is presented
    protected boolean process(int _key)
    {
        boolean lg_framechanged = false;
        boolean lg_result = false;

        i_tick ++;
        if (i_tick>=i_maxticks)
        {
            i_tick = 0;
            lg_framechanged = true;
        }

        if (!(i_state == STATE_FALLEN || i_state == STATE_JUMP))
        {
            switch (_key)
            {
                case Snowboard_PMR.BUTTON_NONE:
                    {
                        if (i_frame<(MAXFRAMES_ROADSTATE>>1))
                        {
                            if (lg_framechanged)
                            {
                                i_frame++;
                            }
                        }
                        else
                        if (i_frame>(MAXFRAMES_ROADSTATE>>1))
                        {
                            if (lg_framechanged)
                            {
                                i_frame--;
                            }
                        }
                    }
                    ;
                    break;
                case Snowboard_PMR.BUTTON_LEFT:
                    {
                        if (i_frame>0)
                        {
                            if (lg_framechanged)
                            {
                                i_frame -- ;
                            }
                        }
                    }
                    ;
                    break;
                case Snowboard_PMR.BUTTON_RIGHT:
                    {
                        if (i_frame<(MAXFRAMES_ROADSTATE-1))
                        {
                            if (lg_framechanged)
                            {
                                i_frame ++;
                            }
                        }
                    }
                    ;
                    break;
            }
        }
        else
        {
            if (i_state == STATE_JUMP)
            {
                if (lg_framechanged)
                {
                    if (lg_backmove)
                    {
                        i_frame --;
                        if (i_frame<0)
                        {
                            i_frame = 0;
                        }
                    }
                    else
                    {
                        i_frame ++;
                        if (i_frame>=MAXFRAMES_JUMPSTATE)
                        {
                            i_frame --;
                            lg_backmove = true;
                        }
                    }
                }
            }
            else
            {
                if (lg_framechanged)
                {
                    i_frame ++;
                    if (i_frame >= MAXFRAMES_FALLENSTATE)
                    {
                        i_frame --;
                        lg_result = true;
                    }
                }
            }
        }
        return lg_result;
    }
}
