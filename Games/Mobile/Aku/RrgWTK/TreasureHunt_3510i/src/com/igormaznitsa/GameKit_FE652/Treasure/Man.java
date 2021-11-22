package com.igormaznitsa.GameKit_FE652.Treasure;

public class Man
{
    public static final int APPEARANCE_STATE = 0x00;
    public static final int MOVE_LEFT_STATE = 0x10;
    public static final int MOVE_RIGHT_STATE = 0x20;
    public static final int MOVE_UPDOWN_STATE = 0x30;

    protected int _x;
    protected int _y;

    public int _init_x;
    public int _init_y;

    public int _cur_state;
    public int _ticks;

    protected static final int APPEARANCE_TICK_DELAY = 3;
    protected static final int MOVE_TICK_DELAY =1 ;

    public static final int APPEARANCE_FRAMES = 6;
    public static final int MOVE_FRAMES = 4;

    public void init()
    {
        _x = (_init_x<<8) * Treasure_SB.VIRTUAL_CELL_WIDTH;
        _y = (_init_y<<8) * Treasure_SB.VIRTUAL_CELL_HEIGHT;
        _ticks = 0;
        _cur_state = APPEARANCE_STATE;
    }

    public int getX()
    {
        return _x>>8;
    }

    public int getY()
    {
        return _y>>8;
    }

    public Man(int initx,int inity)
    {
         _init_x = initx;
        _init_y = inity;
        init();
    }
}
