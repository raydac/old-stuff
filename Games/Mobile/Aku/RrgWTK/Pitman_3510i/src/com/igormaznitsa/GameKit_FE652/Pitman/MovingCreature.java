package com.igormaznitsa.GameKit_FE652.Pitman;

public class MovingCreature
{
    public static final int CREATURE_PLAYER = 0;
    public static final int CREATURE_BUTTERFLY = 1;
    public static final int CREATURE_FIREFLY = 2;
    public static final int CREATURE_INACTIVE = 3;

    public static final int ELEMENT_EXPLOSIVE = 4;
    public static final int ELEMENT_GROWCRYSTAL = 5;

    public static final int STATE_LEFT = 0;
    public static final int STATE_RIGHT = 1;
    public static final int STATE_TOP = 2;
    public static final int STATE_DOWN = 3;
    public static final int STATE_STAND = 4;

    public static final int ANIMATION_FRAMES = 3;
    public static final int ANIMATION_DELAY = 2;

    protected int _state=0;
    protected int _ticks=0;
    protected int _frame=0;
    protected int _type=0;

    protected int _x = 0;
    protected int _y = 0;

    protected int _cellx = 0;
    protected int _celly = 0;

    public static final int SPEED = 4;

    public int getCellX()
    {
        return _cellx;
    }

    public int getCellY()
    {
        return _celly;
    }

    public int getX()
    {
        return _x;
    }

    public int getY()
    {
        return _y;
    }

    public int getType()
    {
        return _type;
    }

    public MovingCreature(int type,int x,int y)
    {
        _type = type;
        _x = x * Pitman_SB.VIRTUAL_CELL_WIDTH;
        _y = y * Pitman_SB.VIRTUAL_CELL_HEIGHT;;
    }

    protected void setState(int state)
    {
        if (_state!=state)
        {
            _state = state;
            _ticks = 0;
            _frame = 0;
        }
    }

    public void  setCXCYS(int type,int cx,int cy)
    {
        _type = type;
        setState(STATE_STAND);
        _x = cx*Pitman_SB.VIRTUAL_CELL_WIDTH;
        _y = cy*Pitman_SB.VIRTUAL_CELL_HEIGHT;
        _cellx = cx;
        _celly = cy;
    }

    public int getState()
    {
        return _state;
    }

    public int getFrame()
    {
        return _frame;
    }

    // return true if animation is completed
    protected boolean processAnimation()
    {
        _ticks++;
        if (_ticks>=ANIMATION_DELAY)
        {
            _ticks = 0;
            _frame++;
            if (_frame>=ANIMATION_FRAMES)
            {
                _frame = 0;
                return true;
            }
        }
        return false;
    }

}
