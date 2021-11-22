package com.igormaznitsa.GameKit_FE652.Asteroids;

import com.igormaznitsa.GameAPI.PlayerMoveRecord;

public class Asteroids_PMR implements PlayerMoveRecord
{
    public static final int MOVE_NONE = 0;
    public static final int MOVE_UP = 1;
    public static final int MOVE_LEFT = 2;
    public static final int MOVE_DOWN = 4;
    public static final int MOVE_RIGHT = 8;

    protected int _direct;

    public Asteroids_PMR()
    {
        _direct = MOVE_NONE;
    }

    public void setDirect(int direct)
    {
        _direct = direct;
    }

    public int getDirect()
    {
        return _direct;
    }

}
