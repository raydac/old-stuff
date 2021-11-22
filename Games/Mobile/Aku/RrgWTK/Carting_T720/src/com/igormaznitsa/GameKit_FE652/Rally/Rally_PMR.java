package com.igormaznitsa.GameKit_FE652.Rally;

import com.igormaznitsa.gameapi.PlayerMoveRecord;

public class Rally_PMR implements PlayerMoveRecord
{
    public static final int DIRECT_NONE = 0;
    public static final int DIRECT_LEFT = 1;
    public static final int DIRECT_RIGHT = 2;
    public static final int DIRECT_UP = 4;
    public static final int DIRECT_DOWN = 8;

    protected int _direct;

    public void setDirect(int direct)
    {
        _direct = direct;
    }

    public int getDirect()
    {
        return _direct;
    }

    public Rally_PMR(int direct)
    {
        _direct = direct;
    }
}
