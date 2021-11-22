package com.igormaznitsa.GameKit_FE652.Frog;

public class Car
{
    public static final int MAX_TYPE = 5;
    public static final int CAR_TYPE1 = 0;
    public static final int CAR_TYPE2 = 1;
    public static final int CAR_TYPE3 = 2;
    public static final int CAR_TYPE4 = 3;
    public static final int CAR_TYPE5 = 4;
    public static final int CAR_TYPE6 = 5;


    public static final int CAR_NULL  = -1;

    public static final int CAR_MOVE_FRAMES = 3;
    public static final int CAR_MOVE_FRAME_DELAY = 1;

    public static final int DIRECT_LEFT = 0;
    public static final int DIRECT_RIGHT = 1;

    public static final int STATE_INACTIVE = 0;
    public static final int STATE_ACTIVE = 1;

    public static final int CAR_WIDTH = 20;
    public static final int CAR_HEIGHT = 11;

    protected int _x;
    protected int _y;

    protected int _direction;
    protected int _state;

    protected int _tick;
    protected int _frame;
    protected int _type;

    public int getType()
    {
        return _type;
    }

    public void nextStep(int _speed)
    {
        _x += _speed;
        if (_speed>0)
        {
            if (_x>=Frog_SB.FIELD_WIDTH)
            {
                _state = STATE_INACTIVE;
            }
        }
        else
        {
            if (_x<=(0-CAR_WIDTH))
            {
                _state = STATE_INACTIVE;
            }
            return;
        }

        _tick++;
        if (_tick>=CAR_MOVE_FRAME_DELAY)
        {
            _frame++;
            if (_frame>=CAR_MOVE_FRAMES) _frame = 0;
            _tick = 0;
        }
    }

    public int getFrame()
    {
        return _frame;
    }

    public int getX()
    {
        return _x;
    }

    public int getY()
    {
        return _y;
    }

    public int getDirection()
    {
        return _direction;
    }

    public int getState()
    {
        return _state;
    }

    public Car(int x,int y,int direct)
    {
        _direction = direct;
        _x = x;
        _y = y;
        _state = STATE_INACTIVE;
        _type = Frog_SB._rnd.getInt(Car.MAX_TYPE);
    }
}
