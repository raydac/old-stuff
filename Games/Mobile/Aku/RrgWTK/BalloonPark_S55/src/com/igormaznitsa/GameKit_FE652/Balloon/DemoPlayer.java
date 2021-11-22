package com.igormaznitsa.GameKit_FE652.Balloon;

import com.igormaznitsa.GameAPI.PlayerBlock;
import com.igormaznitsa.GameAPI.PlayerMoveRecord;
import com.igormaznitsa.GameAPI.GameStateRecord;

public class DemoPlayer implements PlayerBlock
{
    protected Balloon_PMR _pmr = null;
    protected int _state = 0;

    public DemoPlayer()
    {
        _pmr = new Balloon_PMR(Balloon_PMR.BURNER_OFF);
    }

    public PlayerMoveRecord getPlayerMoveRecord(GameStateRecord gameStateRecord)
    {
        int rrr  = Balloon_SB._rnd.getInt(10);
        if (rrr==5)
        {
            if (_state==Balloon_PMR.BURNER_OFF)
                _state = Balloon_PMR.BURNER_ON;
            else
                _state = Balloon_PMR.BURNER_OFF;
        }
        _pmr.setState(_state);

        return _pmr;
    }

    public void initPlayer()
    {
        _pmr.setState(Balloon_PMR.BURNER_OFF);
        _state = Balloon_PMR.BURNER_OFF;
    }
}
