package com.igormaznitsa.GameKit_FE652.Karate;

import com.igormaznitsa.GameAPI.PlayerMoveRecord;

public class Karate_PMR implements PlayerMoveRecord
{
    public static final int BUTTON_NONE = 0;
    public static final int BUTTON_LEFT = 1;
    public static final int BUTTON_RIGHT = 2;
    public static final int BUTTON_BLOCKTOP = 3;
    public static final int BUTTON_BLOCKMID = 4;
    public static final int BUTTON_BLOCKDOWN = 5;
    public static final int BUTTON_PUNCHTOP = 6;
    public static final int BUTTON_PUNCHMID = 7;
    public static final int BUTTON_PUNCHDOWN = 8;

    protected int _button;

    public Karate_PMR()
    {
        _button = BUTTON_NONE;
    }

    public int getButton()
    {
        return _button;
    }

    public void setButton(int button)
    {
        _button = button;
    }
}