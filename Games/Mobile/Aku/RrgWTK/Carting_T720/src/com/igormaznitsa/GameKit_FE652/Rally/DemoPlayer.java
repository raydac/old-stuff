package com.igormaznitsa.GameKit_FE652.Rally;

import com.igormaznitsa.gameapi.PlayerBlock;
import com.igormaznitsa.gameapi.PlayerMoveRecord;
import com.igormaznitsa.gameapi.GameStateRecord;

public class DemoPlayer implements PlayerBlock
{
    protected Rally_PMR player_record;
    protected int direct;

    public DemoPlayer()
    {

    }

    public PlayerMoveRecord getPlayerMoveRecord(GameStateRecord gameStateRecord)
    {
        Rally_GSR _gs = (Rally_GSR) gameStateRecord;
        int lx = _gs.getPlayerX();
        int ly = _gs.getPlayerY();

        int ndir = direct;

        if ((lx-Rally_SB.HORIZ_PLAYER_SPEED)<=0)
        {
            ndir &= ~Rally_PMR.DIRECT_LEFT;
            ndir |= Rally_PMR.DIRECT_RIGHT;
        }
        else
        if ((lx+Rally_SB.HORIZ_PLAYER_SPEED+Rally_SB.PLAYER_CELL_WIDTH)>=Rally_SB.ROAD_WIDTH)
        {
            ndir &= ~Rally_PMR.DIRECT_RIGHT;
            ndir |= Rally_PMR.DIRECT_LEFT;
        }

        if ((ly-Rally_SB.VERT_PLAYER_SPEED)<=0)
        {
            ndir &= ~Rally_PMR.DIRECT_UP;
            ndir |= Rally_PMR.DIRECT_DOWN;
        }
        else
        if ((ly+Rally_SB.VERT_PLAYER_SPEED)>=Rally_SB.DOWN_MOVE_BORDER)
        {
            ndir &= ~Rally_PMR.DIRECT_DOWN;
            ndir |= Rally_PMR.DIRECT_UP;
        }

        if (ndir!=direct) direct = ndir;

        player_record.setDirect(direct);
        return player_record;
    }

    public void initPlayer()
    {
        direct = Rally_PMR.DIRECT_LEFT|Rally_PMR.DIRECT_UP;
        player_record = new Rally_PMR(direct);
    }
}
