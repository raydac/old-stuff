package com.igormaznitsa.GameKit_FE652.Snake;

import com.igormaznitsa.GameAPI.PlayerMoveRecord;

public class Snake_PMR implements PlayerMoveRecord
{
    public static final int DIRECT_NONE = 0;
    public static final int DIRECT_UP = 1;
    public static final int DIRECT_DOWN = 2;
    public static final int DIRECT_LEFT = 4;
    public static final int DIRECT_RIGHT = 8;

    protected int _dir;

    public Snake_PMR(int dir)
    {
        _dir = dir;
    }

    public int getDirect()
    {
        return _dir;
    }

    public void setDirect(int dir)
    {
        _dir = dir;
    }

}
