package com.igormaznitsa.GameKit_FE652.Frog;

import com.igormaznitsa.GameAPI.PlayerMoveRecord;

public class Frog_PMR implements PlayerMoveRecord
{
    public static final int BUTTON_NONE  = 0;
    public static final int BUTTON_LEFT  = 1;
    public static final int BUTTON_RIGHT = 2;
    public static final int BUTTON_UP= 3;
    public static final int BUTTON_DOWN= 4;

    protected int _button;

    public void setButton(int button)
    {
        _button = button;
    }

    public int getButton()
    {
        return _button;
    }

    public Frog_PMR()
    {
        _button = BUTTON_NONE;
    }
}
