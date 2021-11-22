package com.igormaznitsa.GameKit_FE652.Wall;

import com.igormaznitsa.GameAPI.Utils.RndGenerator;

public class Ball
{
    public static final int MAX_DELTA = 3;

    protected static RndGenerator _rnd = new RndGenerator(System.currentTimeMillis());

    public int _x;
    public int _y;
    public boolean _moving;
    public boolean _active;

    public int _delta_x;
    public int _delta_y;

    public void init(int x,int y,int dx,int dy)
    {
        _moving = true;
        _active = true;
        _delta_x = dx;
        _delta_y = dy;
        _x = x;
        _y = y;
    }

    public void initDelta()
    {
        _delta_x = 0-MAX_DELTA+1;
        _delta_y = 0-MAX_DELTA;
    }

    public Ball(int x,int y,boolean moving)
    {
        _x = x;
        _y = y;
        _moving = moving;
        _active = false;
        initDelta();
    }
}
