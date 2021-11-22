package com.igormaznitsa.GameKit_FE652.Tennis;

import com.igormaznitsa.GameAPI.GameStateRecord;
import com.igormaznitsa.GameAPI.PlayerBlock;
import com.igormaznitsa.GameAPI.PlayerMoveRecord;
import com.igormaznitsa.GameAPI.Utils.RndGenerator;

public class Tennis_AI implements PlayerBlock
{
    protected Tennis_PMR _pmr;
    protected RndGenerator _rnd = new RndGenerator(System.currentTimeMillis());
    protected int _level = 0;
    protected int _prev = Tennis_PMR.BUTTON_NONE;

    public Tennis_AI(int level)
    {
        _level = level;
        _prev = Tennis_PMR.BUTTON_NONE;
    }

    public PlayerMoveRecord getPlayerMoveRecord(GameStateRecord gameStateRecord)
    {
        Tennis_GSR _gsr = (Tennis_GSR) gameStateRecord;
        Ball _ball = _gsr._ball;

        int _y = _gsr._ai_racket._y;
        int _own_x = _gsr._ai_racket._x;
        int rst = Tennis_PMR.BUTTON_NONE;

        if (_gsr._ai_racket._attached_ball != null)
        {
            if (_rnd.getInt(15) == 7)
            {
                switch (_rnd.getInt(3))
                {
                    case 0:
                        rst = Tennis_PMR.BUTTON_BEAT;
                        break;
                    case 1:
                        rst = Tennis_PMR.BUTTON_BEATLEFT;
                        break;
                    default:
                        rst = Tennis_PMR.BUTTON_BEATRIGHT;
                        break;
                }
                _gsr._ai_racket._attached_ball = null;
            }
            else
            {
                switch (_rnd.getInt(10))
                {
                    case 1:
                        {
                            rst = Tennis_PMR.BUTTON_LEFT;
                            _prev = Tennis_PMR.BUTTON_LEFT;
                        }
                        ;
                        break;
                    case 9:
                        {
                            rst = Tennis_PMR.BUTTON_RIGHT;
                            _prev = Tennis_PMR.BUTTON_RIGHT;
                        }
                        ;
                        break;
                    default:
                        rst = _prev;
                }
            }
        }
        else
        {
            int dr =0;


            if ((_ball.getY()<(_y+3))&&(_ball.getY()+Tennis_SB.BALL_WIDTH)<=(_y+3)) dr = -1;
                else
            if ((_ball.getY()>=(_y+Tennis_SB.RACKET_WIDTH-3))&&(_ball.getY()+Tennis_SB.BALL_WIDTH)>(_y+Tennis_SB.RACKET_WIDTH-3)) dr = 1;

            boolean reakt = false;
            switch (_level)
            {
                case Tennis_SB.LEVEL0:
                    {
                        if (_rnd.getInt(20)>8) reakt = true;
                    }
                    ;
                    break;
                case Tennis_SB.LEVEL1:
                    {
                        if (_rnd.getInt(20)>5) reakt = true;
                    }
                    ;
                    break;
                case Tennis_SB.LEVEL2:
                    {
                        if (_rnd.getInt(20)>2) reakt = true;
                    }
                    ;
                    break;
            }

            if (reakt)
            {
                switch(dr)
                {
                    case 0 : rst = Tennis_PMR.BUTTON_NONE;break;
                    case -1 : rst = Tennis_PMR.BUTTON_LEFT;break;
                    case 1 :rst = Tennis_PMR.BUTTON_RIGHT;;break;
                }
            }
            else
               rst = Tennis_PMR.BUTTON_NONE;

            if ((_own_x - _ball.getX()) < (Tennis_SB.BALL_HORIZ_SPEED << 1))
            {
                int ll = _rnd.getInt(10);
                if (ll<5) rst = Tennis_PMR.BUTTON_BEATLEFT;
                else
                if (ll>5) rst = Tennis_PMR.BUTTON_BEATRIGHT;
                else
                rst = Tennis_PMR.BUTTON_BEAT;
            }
        }
        _pmr._button = rst;

        return _pmr;
    }

    public void initPlayer()
    {
        _pmr = new Tennis_PMR();
        _prev = Tennis_PMR.BUTTON_NONE;
    }
}
