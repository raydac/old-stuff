package com.igormaznitsa.GameKit_FE652.Wall;

import com.igormaznitsa.GameAPI.PlayerMoveRecord;

public class Wall_PMR implements PlayerMoveRecord
{
    public static final int BUTTON_NONE = 0;
    public static final int BUTTON_LEFT = 1;
    public static final int BUTTON_RIGHT = 2;
    public static final int BUTTON_UPTHROW = 3;

    protected int _button;

    public Wall_PMR (int button)
    {
        _button = button;
    }

    public int getButtonValue()
    {
        return _button;
    }

    public void setButtonValue(int value)
    {
        _button = value;
    }

}
