package com.igormaznitsa.game_kit_out_6BE902.Penalty;

import com.igormaznitsa.gameapi.PlayerMoveRecord;

public class Penalty_PMR implements PlayerMoveRecord
{
    public static final byte BUTTON_NONE = 0;
    public static final byte BUTTON_UP = 1;
    public static final byte BUTTON_LEFT = 2;
    public static final byte BUTTON_RIGHT = 3;
    public static final byte BUTTON_DOWN = 5;
    public static final byte BUTTON_FIRE = 6;

    protected int i_value;

    public Penalty_PMR()
    {
        i_value = BUTTON_NONE;
    }

    public void setValue(int _value)
    {
        i_value = _value;
    }

    public int getValue()
    {
        return i_value;
    }
}
