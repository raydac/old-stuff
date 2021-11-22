package com.igormaznitsa.GameKit_FE652.Balloon;

import com.igormaznitsa.GameAPI.PlayerMoveRecord;

public class Balloon_PMR implements PlayerMoveRecord
{
    public static final int BURNER_ON=1;
    public static final int BURNER_OFF=0;

    protected int _state;

    public Balloon_PMR(int state)
    {
        _state = state;
    }

    public void setState(int state)
    {
        _state = state;
    }

    public int getState()
    {
        return _state;
    }

}
