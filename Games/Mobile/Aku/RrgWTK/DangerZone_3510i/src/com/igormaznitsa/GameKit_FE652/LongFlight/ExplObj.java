package com.igormaznitsa.GameKit_FE652.LongFlight;

public class ExplObj
{
    public static final int EXPLODE_FRAMES = 4;
    public static final int EXPLODE_FRAMEDELAY = 1;

    public int _x;
    public int _y;
    public int _frame;
    public int _tick;
    public boolean _explode;
    public boolean _active;

    public ExplObj()
    {
        init(0,0);
        _active = false;
    }

    public int getY()
    {
        return _y >> 8;
    }

    public void init(int x,int y)
    {
        _x = x;
        _y = y<<8;
        _frame = 0;
        _tick = 0;
        _explode = false;
        _active = true;
    }

    public boolean processAnimation()
    {
        if (!_active) return false;
        if (_explode)
        {
            _tick++;
            if (_tick>=EXPLODE_FRAMEDELAY)
            {
                _frame++;
                if (_frame >= EXPLODE_FRAMES)
                {
                    _active = false;
                    return true;
                }
            }
        }
        return
            false;
    }

}
