package com.igormaznitsa.GameKit_FE652.Wall;

public class Surprise
{
    public int _x;
    public int _y;
    public int _value;
    public boolean _active;

    public void init(int x,int y,int value)
    {
        _x = x *Wall_SB.CELL_WIDTH;
        _y = y *Wall_SB.CELL_HEIGHT;
        _value = value;
        _active = true;
    }

    public Surprise()
    {
        _active = false;
        _x = 0;
        _y = 0;
        _value = 0;
    }
}
