package com.igormaznitsa.GameKit_FE652.ShipsDuel;

import com.igormaznitsa.GameAPI.PlayerMoveRecord;

public class ShipsDuel_PMR implements PlayerMoveRecord
{
    public static final int DIRECT_NONE = 0;
    public static final int DIRECT_POWER_LESS = 1;
    public static final int DIRECT_POWER_MORE = 2;
    public static final int DIRECT_ANGLE_LEFT = 4;
    public static final int DIRECT_ANGLE_RIGHT = 8;
    public static final int DIRECT_FIRE = 16;

    protected int _direct;

    public void setDirect(int direct)
    {
        _direct = direct;
    }

    public int getDirect()
    {
        return _direct;
    }

    public ShipsDuel_PMR(int direct)
    {
        _direct = direct;
    }
}
