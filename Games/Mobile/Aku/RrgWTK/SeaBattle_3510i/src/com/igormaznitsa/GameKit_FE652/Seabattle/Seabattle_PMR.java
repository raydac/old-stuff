package com.igormaznitsa.GameKit_FE652.Seabattle;

import com.igormaznitsa.GameAPI.PlayerMoveRecord;

public class Seabattle_PMR implements PlayerMoveRecord
{
    protected int _x;
    protected int _y;

    public Seabattle_PMR(int x,int y)
    {
        _x = x;
        _y = y;
    }

    public int getX()
    {
        return _x;
    }

    public int getY()
    {
        return _y;
    }

    public void setXY(int x,int y)
    {
        _x = x;
        _y = y;
    }

}
