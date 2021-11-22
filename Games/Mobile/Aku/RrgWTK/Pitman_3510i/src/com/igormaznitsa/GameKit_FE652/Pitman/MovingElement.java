package com.igormaznitsa.GameKit_FE652.Pitman;

public class MovingElement
{
    protected int _cur_x;
    protected int _cur_y;
    protected int _prev_x;
    protected int _prev_y;
    protected byte _type;

    public int getCurX() { return _cur_x;}
    public int getCurY() { return _cur_y;}
    public int getPrevX() { return _prev_x;}
    public int getPrevY() { return _prev_y;}
    public int getType() { return _type;}

    public MovingElement()
    {
        _cur_x = 0;
        _cur_y = 0;
        _prev_x = 0;
        _prev_y = 0;
        _type = 0;
    }
}
