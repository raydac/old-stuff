package com.igormaznitsa.GameKit_FE652.Tennis;

import com.igormaznitsa.GameAPI.PlayerMoveRecord;

public class Tennis_PMR implements PlayerMoveRecord
{
    public static final int BUTTON_NONE = 0;
    public static final int BUTTON_LEFT = 1;
    public static final int BUTTON_RIGHT = 2;
    public static final int BUTTON_BEAT = 3;
    public static final int BUTTON_BEATLEFT = 4;
    public static final int BUTTON_BEATRIGHT = 5;

    protected int _button = BUTTON_NONE;

    public Tennis_PMR()
    {
    }

    public void setButton(int button)
    {
        _button = button;
    }

    public int getButton()
    {
        return _button;
    }
}
