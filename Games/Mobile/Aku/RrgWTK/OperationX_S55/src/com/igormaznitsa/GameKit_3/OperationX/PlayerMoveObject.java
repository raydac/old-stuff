package com.igormaznitsa.GameKit_3.OperationX;

public class PlayerMoveObject
{
    public static final int BUTTON_NONE     = 0;
    public static final int BUTTON_LEFT     = 1;
    public static final int BUTTON_RIGHT    = 2;
    public static final int BUTTON_UP       = 3;
    public static final int BUTTON_DOWN     = 4;
    public static final int BUTTON_FIRE     = 5;

    public static final int BUTTON_RECHARGE     = 6;

    public int i_Button;

    public PlayerMoveObject()
    {
        i_Button = BUTTON_NONE;
    }
}
