package com.igormaznitsa.game_kit_E5D3F2.Bomber;

public class AnimationObject
{
    public static final int OBJECT_EXPLODE = 1;

    public static final int ANIMATION_FRAMES = 4;
    public static final int ANIMATION_TICKS = 3;

    protected int i_frame;
    protected int i_tick;

    protected int i_scrx;
    protected int i8_scry;

    protected int i_type;

    protected boolean lg_is_active;

    public AnimationObject()
    {
        i_type = OBJECT_EXPLODE;
        i_scrx = 0;
        i8_scry = 0;
        i_frame = 0;
        i_tick = 0;
        lg_is_active = false;
    }

    public int getType()
    {
        return i_type;
    }

    public void initObj(int _x,int _y,int _type)
    {
        i_type = _type;
        i_scrx = _x;
        i8_scry = _y << 8;
        i_frame = 0;
        i_tick = 0;
        lg_is_active = true;
    }

    public int getScreenX()
    {
        return i_scrx;
    }

    public int getScreenY()
    {
        return i8_scry >> 8;
    }

    public int getFrameNumber()
    {
        return i_frame;
    }

    public boolean isActive()
    {
        return lg_is_active;
    }

    /**
     * @return true if animation is done and the object is becoming inactive
     */
    public boolean processAnimation()
    {
        if (!lg_is_active) return true;

        i_tick++;
        if (i_tick >= ANIMATION_TICKS)
        {
            i_frame++;
            i_tick = 0;
            if (i_frame >= ANIMATION_FRAMES)
            {
                lg_is_active = false;
                return true;
            }
        }
        return false;
    }
}
