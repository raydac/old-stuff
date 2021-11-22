package com.igormaznitsa.game_kit_E5D3F2.RiverRescue;

public class Player
{
    public static final int MAXFRAMES = 7;
    public static final int EXPLOSIVEFRAMES = 5;
    private static final int MAXTICKS_ROADSTATE = 3;
    private static final int MAXTICKS_EXPLOSIVE = 5;

    private static final int SPLASH_DELAY = 2;

    public int i_Frame;
    protected int i_maxticks;
    protected int i_tick;

    protected int i_width;
    public int i_height;

    protected int i_splashcounter;
    protected int i_splashdelay;

    public int i_cx;
    public int i_cy;

    private int i_xoffst;
    private int i_yoffst;

    public boolean lg_Destroyed;
    public static final int MAXSPLASHES = 10;
    public Splash[] ap_Splashes;

    protected static final int[] ai_sizes =
            {
                // width, height, xoffst, yoffst
                15, 13, -3, 1,
                14, 14, -2, 0,
                12, 15, -1, 0,
                9,  15,  0, 0,
                12, 15, -1, 0,
                14, 14, -2, 0,
                15, 13, -3, 1
            };

    protected static final int[] ai_explosesizes =
            {
                // width, height, xoffst, yoffst
                45, 36, -19, -6,
                45, 34, -19, -5,
                45, 36, -19, -6,
                45, 36, -19, -6,
                45, 36, -19, -6
            };

    public Player()
    {
        setFrame(3);
        i_maxticks = MAXTICKS_ROADSTATE;

        ap_Splashes = new Splash[MAXSPLASHES];

        for (int li = 0; li < ap_Splashes.length; li++)
        {
            ap_Splashes[li] = new Splash();
        }
    }

    public int getScrX()
    {
        return i_cx + i_xoffst;
    }

    public int getScrY()
    {
        return i_cy  + i_yoffst;
    }

    protected void setCX(int _cx)
    {
        i_cx = _cx;
    }

    protected void setCY(int _cy)
    {
        i_cy = _cy;
    }

    protected void initSplashes()
    {
        for (int li = 0; li < ap_Splashes.length; li++)
        {
            ap_Splashes[li].lg_Active = false;
        }

        i_splashcounter = 0;
        i_splashdelay = SPLASH_DELAY;
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
        else
        {
            i_width = ai_sizes[i_indx++];
            i_height = ai_sizes[i_indx++];
            i_xoffst = ai_sizes[i_indx++];
            i_yoffst = ai_sizes[i_indx];
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
            process();
            return lg_result;
        }

        boolean lg_framechanged = false;
        i_tick++;
        if (i_tick >= i_maxticks)
        {
            i_tick = 0;
            lg_framechanged = true;
        }

        switch (_key)
        {
            case RiverRescue_PMR.BUTTON_NONE:
                {
                    if (i_Frame < (MAXFRAMES >> 1))
                    {
                        if (lg_framechanged)
                        {
                            setFrame(i_Frame + 1);
                        }
                    }
                    else if (i_Frame > (MAXFRAMES >> 1))
                    {
                        if (lg_framechanged)
                        {
                            setFrame(i_Frame - 1);
                        }
                    }
                }
                ;
                break;
            case RiverRescue_PMR.BUTTON_LEFT:
                {
                    if (i_Frame > 0)
                    {
                        if (lg_framechanged)
                        {
                            setFrame(i_Frame - 1);
                        }
                    }
                }
                ;
                break;
            case RiverRescue_PMR.BUTTON_RIGHT:
                {
                    if (i_Frame < (MAXFRAMES - 1))
                    {
                        if (lg_framechanged)
                        {
                            setFrame(i_Frame + 1);
                        }
                    }
                }
                ;
                break;
        }
        process();
        return false;
    }

    private void process()
    {
        // Processing of the splashs
        for (int li = 0; li < ap_Splashes.length; li++)
        {
            if (ap_Splashes[li].lg_Active) ap_Splashes[li].process();
        }

        if (i_splashdelay <= 0)
        {
            if (lg_Destroyed) return;
            i_splashdelay = SPLASH_DELAY;
            ap_Splashes[i_splashcounter++].activate(getScrX()+(i_width>>1), getScrY() + (i_height >> 1));
            if (i_splashcounter >= MAXSPLASHES) i_splashcounter = 0;
        }
        else
            i_splashdelay--;
    }
}
