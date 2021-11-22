package com.igormaznitsa.GameKit_FE652.Tennis;

public class Ball
{
    public static final int BALL_SPEEDNORMAL = 0;
    public static final int BALL_SPEEDLOW = 1;

    protected int _dz_8;
    protected int _z;
    protected int _x;
    protected int _y;
    public int _state;

    protected int _horiz_speed = Tennis_SB.BALL_HORIZ_SPEED;
    protected int _vert_speed = 0;
    protected boolean is_center_crossed = false;

    public int getX()
    {
        return _x;
    }

    public int getY()
    {
        return (_y>>16);
    }

    public int getZ()
    {
        return _z>>8;
    }

    public Ball(int x,int y,int z)
    {
        _dz_8 = 0;
        _x = x;
        _y = y;
        _z = z;
        _state = BALL_SPEEDNORMAL;
    }
}
