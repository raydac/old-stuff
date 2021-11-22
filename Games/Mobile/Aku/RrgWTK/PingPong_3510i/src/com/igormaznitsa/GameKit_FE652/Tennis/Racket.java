package com.igormaznitsa.GameKit_FE652.Tennis;

public class Racket
{
    public static final int RACKET_DOWN = 0;
    public static final int RACKET_UP = 1;
    public static final int RACKET_LEFT = 2;
    public static final int RACKET_RIGHT = 3;

    public static final int RACKETPOS_LEFT = 0;
    public static final int RACKETPOS_RIGHT = 1;

    protected int _type;
    protected int _x;
    protected int _y;
    protected int _state;
    protected Ball _attached_ball;

    public int getX()
    {
        return _x;
    }

    public int getY()
    {
        return _y;
    }

    public void setY(int value)
    {
        _y = value;
        if (_attached_ball!=null)
        {
            _attached_ball._y = (_y + ((Tennis_SB.RACKET_WIDTH-Tennis_SB.BALL_WIDTH)>>1))<<16;
        }
    }

    public int getState()
    {
        return _state;
    }

    public void attachBall(Ball ball)
    {
        if (ball==null)
        {
            _attached_ball = null;
            return;
        }
        _attached_ball = ball;
        _attached_ball._y = (_y + ((Tennis_SB.RACKET_WIDTH-Tennis_SB.BALL_WIDTH)>>1))<<16;
        _attached_ball._z = Tennis_SB.BALL_MAX_Z<<8;
        _attached_ball._dz_8 = Tennis_SB.BALL_SPEEDNORMAL_DZ_8;
        _attached_ball._state = Ball.BALL_SPEEDNORMAL;

        switch (_type)
        {
            case RACKETPOS_LEFT:
                {
                    _attached_ball._x = _x + Tennis_SB.BALL_RACKET_DISTANCE;
                    if (_attached_ball._horiz_speed <0)
                    {
                        _attached_ball._horiz_speed = 0 - _attached_ball._horiz_speed;
                    }
                }
                ;
                break;
            case RACKETPOS_RIGHT:
                {
                    _attached_ball._x = _x - Tennis_SB.BALL_RACKET_DISTANCE;
                    if (_attached_ball._horiz_speed >0)
                    {
                        _attached_ball._horiz_speed = 0 - _attached_ball._horiz_speed;
                    }
                }
                ;
                break;
        }

        ball.is_center_crossed = false;
    }

    public Racket(int x, int y, int state, int type)
    {
        _type = type;
        _x = x;
        _y = y;
        _state = state;
        _attached_ball = null;
    }
}
