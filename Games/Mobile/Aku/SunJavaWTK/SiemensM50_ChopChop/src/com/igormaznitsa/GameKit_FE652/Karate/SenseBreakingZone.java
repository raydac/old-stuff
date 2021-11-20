package com.igormaznitsa.GameKit_FE652.Karate;

public class SenseBreakingZone
{
    protected int _x;
    protected int _y;
    protected int _width;
    protected int _height;
    protected int _sensetivity;

    public int getSensetivity()
    {
        return _sensetivity;
    }

    public SenseBreakingZone(int x,int y, int width,int height,int sensetivity)
    {
        _x = x;
        _y = y;
        _width = width;
        _height = height;
        _sensetivity = sensetivity;
    }

}
