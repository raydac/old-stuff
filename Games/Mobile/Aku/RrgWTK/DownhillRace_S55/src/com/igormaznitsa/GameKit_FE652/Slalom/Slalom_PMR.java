package com.igormaznitsa.GameKit_FE652.Slalom;

import com.igormaznitsa.GameAPI.PlayerMoveRecord;

public class Slalom_PMR implements PlayerMoveRecord
{
    public static final int DIRECT_NONE = 0;
    public static final int DIRECT_LEFT = 1;
    public static final int DIRECT_RIGHT = 2;

    protected int _direct;

    public void setDirect(int direct)
    {
        _direct = direct;
    }

    public int getDirect()
    {
        return _direct;
    }

    public Slalom_PMR(int direct)
    {
        _direct = direct;
    }
}
