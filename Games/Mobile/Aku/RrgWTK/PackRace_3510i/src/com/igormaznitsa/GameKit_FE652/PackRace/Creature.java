package com.igormaznitsa.GameKit_FE652.PackRace;

public class Creature
{
    public static final int STATE_LEFT = 0;
    public static final int STATE_RIGHT = 1;
    public static final int STATE_UP = 2;
    public static final int STATE_DOWN = 3;
    public static final int STATE_APPEARANCE = 4;

    public static final int FRAME_APPEARANCE = 4;
    public static final int FRAME_MOVE = 2;
    public static final int ANIME_TICKS = 5;
    public static final int APPEARANCE_TICKS = 4;

    protected int _x;
    protected int _y;
    protected int _init_x;
    protected int _init_y;
    protected int _tick;
    protected int _frame;
    protected boolean _isappearance;
    protected int _cur_state;
    protected int _last_direct;
    protected int _prelast_direct;

    public int getX()
    {
        return _x;
    }

    public int getY()
    {
        return _y;
    }

    public int getState()
    {
        return _cur_state;
    }

    public int getFrame()
    {
        return _frame;
    }

    public Creature(int inix,int iniy,boolean isappearance)
    {
        _isappearance = isappearance;
        _init_x = inix;
        _init_y = iniy;
    }

    public void init()
    {
        _last_direct = PackRace_SB.DIRECT_NONE;
        _prelast_direct = PackRace_SB.DIRECT_NONE;
        _x = _init_x;
        _y = _init_y;
        _frame = 0;
        _tick = 0;
        if (_isappearance)
            _cur_state = STATE_APPEARANCE;
        else
            _cur_state = STATE_LEFT;
    }
}
