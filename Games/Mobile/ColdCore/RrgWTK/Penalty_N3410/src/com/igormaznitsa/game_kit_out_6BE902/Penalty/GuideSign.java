package com.igormaznitsa.game_kit_out_6BE902.Penalty;

public class GuideSign
{
    public static final byte TYPE_SIDEARROWLEFT = 0;
    public static final byte TYPE_SIDEARROWCENTER = 1;
    public static final byte TYPE_SIDEARROWRIGHT = 2;
    public static final byte TYPE_GATESIGHT= 3;

    public static final byte SIDEARROW_WIDTH = 17;
    public static final byte SIDEARROW_HEIGHT = 9;

    public static final byte SIGHT_NEAR_WIDTH = 20;
    public static final byte SIGHT_NEAR_HEIGHT = 6;

    public static final byte SIGHT_FAR_WIDTH = 16;
    public static final byte SIGHT_FAR_HEIGHT = 5;

    protected int i_type;
    protected int i_x;
    protected int i_y;
    protected boolean lg_selected;
    protected boolean lg_active;

    public GuideSign()
    {
        lg_active = false;
    }

    public void activate(int _type, int _x, int _y)
    {
        lg_active = true;
        lg_selected = false;
        i_type = _type;

        i_x = _x;
        i_y = _y;
    }

    public int getScrX()
    {
        return i_x;
    }

    public int getScrY()
    {
        return i_y;
    }

    public int getType()
    {
        return i_type;
    }

    public boolean isActive()
    {
        return lg_active;
    }

    public boolean isSelected()
    {
        return lg_selected;
    }

}
