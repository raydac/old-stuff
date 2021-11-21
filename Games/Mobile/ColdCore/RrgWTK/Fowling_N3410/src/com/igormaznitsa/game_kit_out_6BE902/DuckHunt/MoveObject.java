package com.igormaznitsa.game_kit_out_6BE902.DuckHunt;

public class MoveObject
{
    public static final int OBJECT_DUCK_TAKEN_OFF_LEFT = 0;
    public static final int OBJECT_DUCK_TAKEN_OFF_RIGHT = 1;
    public static final int OBJECT_DUCK_FLYING_LEFT = 2;
    public static final int OBJECT_DUCK_FLYING_RIGHT = 3;
    public static final int OBJECT_CANE = 4;
    public static final int OBJECT_BURST = 5;

    public static final int STATE_ALIVE = 0;
    public static final int STATE_KILLED = 1;

    public static final int ANIMATION_FRAMES = 3;
    public static final int ANIMATION_TICKS = 1;

    public static final int CANE_DISTANCE_STEP = 10;
    public static final int DUCK_DISTANCE_STEP = 3;
    public static final int BURST_DISTANCE_STEP = 3;

    private static final int[] ai_duckdied_frame_sizes =
            {
                8, 8,
                10, 10,
                15, 15
            };

    private static final int[] ai_duckvert_frame_sizes =
            {
                8, 8,
                10, 10,
                15, 14
            };

    private static final int[] ai_duckhorz_frame_sizes =
            {
                8, 5,
                12, 7,
                18, 9
            };

    private static final int[] ai_burst_frame_sizes =
            {
                10, 5,
                17, 10,
                25, 15
            };

    private static final int[] ai_cane_frame_sizes =
            {
                5, 5,
                7, 7,
                9, 9,
                11, 11,
                13, 13,
                15, 15,
                17, 17,
                19, 19,
                21, 21,
                23, 23
            };

    protected static final int[] ai8_duck_speed_constant =
            {
                0x030,
                0x070,
                0x100
            };

    protected int i8_x;
    protected int i8_y;

    protected int i_scrx;
    protected int i_scry;
    protected int i8_distance;
    protected int i_distanceframe;
    protected int i_type;
    protected int i_state;
    protected int i_frame_sizeX;
    protected int i_frame_sizeY;

    protected int i_frame;
    protected int i_tick;
    protected boolean lg_backanimation;
    protected boolean lg_backmove;
    protected boolean lg_active;

    protected int[] ai_frame_size_array;

    protected static int i_scrcenterx;
    protected static int i_scrcentery;

    public MoveObject(int _scrwidth, int _scrheight)
    {
        lg_active = false;
        i_scrcenterx = _scrwidth >> 1;
        i_scrcentery = _scrheight >> 1;
    }

    protected int getDuckSpeedConstant()
    {
        return ai8_duck_speed_constant[i_distanceframe];
    }

    public void setXY(int _x, int _y)
    {
        i8_x = _x << 8;
        i8_y = _y << 8;
    }

    public int getType()
    {
        return i_type;
    }

    public int getFrame()
    {
        return i_frame;
    }

    public int getState()
    {
        return i_state;
    }

    public int getDistanceFrame()
    {
        return i_distanceframe;
    }

    protected void convertDuckToBurst()
    {
        int i8_framecenterx = (i8_x + (i_frame_sizeX << 7));
        int i8_framey = i8_y + ((i_frame_sizeY - ai_burst_frame_sizes[(i_distanceframe << 1) + 1]) << 8);


        i8_distance = (((i8_framey >> 8) - i_scrcentery) << 16) / DuckHunt_SB.OBJECT_Y_COEFF_I8;
        i8_x = ((((i8_framecenterx >> 8) - i_scrcenterx) << 16) / i8_distance / DuckHunt_SB.OBJECT_X_COEFF_I8) << 8;

        setType(OBJECT_BURST);
        setDistance(i8_distance);
        i_state = STATE_ALIVE;
    }

    public void setDistance(int _newdistance)
    {
        i8_distance = _newdistance;
        switch (i_type)
        {
            case OBJECT_DUCK_FLYING_LEFT:
            case OBJECT_DUCK_FLYING_RIGHT:
            case OBJECT_DUCK_TAKEN_OFF_LEFT:
            case OBJECT_DUCK_TAKEN_OFF_RIGHT:
                {
                    i_distanceframe = (_newdistance >> 8) / (DuckHunt_SB.MAX_OBJECT_DISTANCE / DUCK_DISTANCE_STEP);
                    if (i_distanceframe >= DUCK_DISTANCE_STEP) i_distanceframe = DUCK_DISTANCE_STEP - 1;
                }
                ;
                break;
            case OBJECT_CANE:
                {
                    i_distanceframe = (_newdistance >> 8) / (DuckHunt_SB.MAX_OBJECT_DISTANCE / CANE_DISTANCE_STEP);
                    i_scrx = i_scrcenterx + (((i8_x >> 8) * DuckHunt_SB.OBJECT_X_COEFF_I8 * i8_distance) >> 16);
                    i_scry = i_scrcentery + ((i8_distance * DuckHunt_SB.OBJECT_Y_COEFF_I8) >> 16);
                    if (i_distanceframe >= CANE_DISTANCE_STEP) i_distanceframe = CANE_DISTANCE_STEP - 1;
                }
                ;
                break;
            case OBJECT_BURST:
                {
                    i_distanceframe = (_newdistance >> 8) / (DuckHunt_SB.MAX_OBJECT_DISTANCE / BURST_DISTANCE_STEP);
                    i_scrx = i_scrcenterx + (((i8_x >> 8) * DuckHunt_SB.OBJECT_X_COEFF_I8 * i8_distance) >> 16);
                    i_scry = i_scrcentery + ((i8_distance * DuckHunt_SB.OBJECT_Y_COEFF_I8) >> 16);
                    if (i_distanceframe >= BURST_DISTANCE_STEP) i_distanceframe = BURST_DISTANCE_STEP - 1;
                }
                ;
                break;
        }
        i_frame_sizeX = ai_frame_size_array[i_distanceframe << 1];
        i_frame_sizeY = ai_frame_size_array[(i_distanceframe << 1) + 1];
    }

    public int getScrX()
    {
        if (i_type == OBJECT_BURST || i_type == OBJECT_CANE)
            return i_scrx - (i_frame_sizeX >> 1);
        else
            return i8_x >> 8;
    }

    public int getScrY()
    {
        if (i_type == OBJECT_CANE)
            return i_scry - (i_frame_sizeY >> 1);
        else
            return i8_y >> 8;
    }

    public boolean isActive()
    {
        return lg_active;
    }

    public void setType(int _type)
    {
        i_type = _type;
        switch (i_type)
        {
            case OBJECT_DUCK_FLYING_LEFT:
            case OBJECT_DUCK_FLYING_RIGHT:
                {
                    ai_frame_size_array = ai_duckhorz_frame_sizes;
                }
                ;
                break;
            case OBJECT_DUCK_TAKEN_OFF_LEFT:
            case OBJECT_DUCK_TAKEN_OFF_RIGHT:
                {
                    ai_frame_size_array = ai_duckvert_frame_sizes;
                }
                ;
                break;
            case OBJECT_BURST:
                {
                    ai_frame_size_array = ai_burst_frame_sizes;
                }
                ;
                break;
            case OBJECT_CANE:
                {
                    ai_frame_size_array = ai_cane_frame_sizes;
                }
                ;
                break;
        }
        i_frame = 0;
        i_tick = 0;
    }

    public void activate(int _x, int _y, int _type, int _distance, boolean _backanimation)
    {
        i8_x = _x << 8;
        i8_y = _y << 8;

        setType(_type);

        lg_active = true;
        i8_distance = _distance << 8;
        setState(STATE_ALIVE, _backanimation);
        setDistance(i8_distance);
    }

    public void setState(int _state, boolean _backanimation)
    {
        i_frame = 0;
        i_tick = 0;
        i_state = _state;
        lg_backanimation = _backanimation;
        lg_backmove = false;

        if (_state == STATE_KILLED)
        {
            i_frame_sizeX = ai_duckdied_frame_sizes[i_distanceframe << 1];
            i_frame_sizeY = ai_duckdied_frame_sizes[(i_distanceframe << 1) + 1];
        }
    }

    public boolean processAnimation()
    {
        i_tick++;
        if (i_tick >= ANIMATION_TICKS)
        {
            i_tick = 0;
            if (lg_backanimation)
            {
                if (lg_backmove)
                {
                    i_frame--;
                    if (i_frame == 0) lg_backmove = false;
                }
                else
                {
                    i_frame++;
                    if (i_frame >= ANIMATION_FRAMES)
                    {
                        i_frame--;
                        lg_backmove = true;
                    }
                }
                return false;
            }
            else
            {
                i_frame++;
                if (i_frame >= ANIMATION_FRAMES)
                {
                    i_frame = 0;
                    if (i_type == OBJECT_BURST) lg_active = false;
                    return true;
                }
                else
                    return false;
            }
        }
        return false;
    }

}
