package com.igormaznitsa.game_kit_out_6BE902.DriveOff;

import com.igormaznitsa.gameapi.PlayerMoveRecord;

public class DriveOff_PMR implements PlayerMoveRecord
{
    public static final int BUTTON_NONE = 0;
    public static final int BUTTON_LEFT = 1;
    public static final int BUTTON_RIGHT = 2;
    public static final int BUTTON_FIREUP = 3;
    public static final int BUTTON_FIREDOWN = 4;

    protected int i_value;

    public DriveOff_PMR()
    {
        i_value = BUTTON_NONE;
    }

    public int getValue()
    {
        return i_value;
    }

    public void setValue(int _value)
    {
        i_value = _value;
    }

}
