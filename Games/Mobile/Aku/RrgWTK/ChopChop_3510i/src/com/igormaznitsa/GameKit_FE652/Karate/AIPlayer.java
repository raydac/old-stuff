package com.igormaznitsa.GameKit_FE652.Karate;

import com.igormaznitsa.GameAPI.PlayerBlock;
import com.igormaznitsa.GameAPI.PlayerMoveRecord;
import com.igormaznitsa.GameAPI.GameStateRecord;
import java.util.Random;

public class AIPlayer implements PlayerBlock
{
    public static final int LEVEL_MAX = 1;
    public static final int LEVEL_MID = 2;
    public static final int LEVEL_MIN = 3;

    protected Karate_PMR _move = null;

    protected int _level;
    protected int _prev_state;

    protected Random _rnd = new Random(System.currentTimeMillis());
    private int getInt(int limit)
    {
       limit++;
       limit = (int)(((long)Math.abs(_rnd.nextInt())*(long)limit)>>>31);
       return limit;
    }


/*    public void setDirect(int dir)
    {
        _move.setButton(dir);
    }*/

    public AIPlayer(int level)
    {
        _level = level;
        _prev_state = Karate_PMR.BUTTON_NONE;
    }

    public PlayerMoveRecord getPlayerMoveRecord(GameStateRecord gameStateRecord)
    {

        Karate_GSR _gsr = (Karate_GSR) gameStateRecord;

        int _my_x = _gsr._ai_karateker.getX();
        int _mystate = _gsr._ai_karateker._cur_state;
        int _enemy_x = _gsr._player_karateker.getX();
        int _enemy_state = _gsr._player_karateker._cur_state;

        int _my_life = _gsr._ai_karateker.getLifeStatus();
        int _enemy_life = _gsr._player_karateker.getLifeStatus();

        if (_mystate != Karateker.STATE_STAND)
        {
            _move.setButton(Karate_PMR.BUTTON_NONE);
        }
        else
        {
            if ((_my_x - _enemy_x) > (Karateker.FRAME_WIDTH / 3))
            {
                if (_my_life >= (_enemy_life/3))
                {
                    if (_enemy_state == Karateker.STATE_STAND )
                    {
                        _move.setButton(Karate_PMR.BUTTON_LEFT);
                    }
                    else
                    {
                        _move.setButton(Karate_PMR.BUTTON_NONE);
                    }
                }
                else
                {
                    _move.setButton(Karate_PMR.BUTTON_RIGHT);
                }
            }
            else
            {
                int kx = _my_x+Karateker.HOR_MOVE_SPEED+Karateker.FRAME_WIDTH;
                boolean _is_way = (kx<Karate_SB.FIELD_WIDTH);

                if ((_my_life <= (_enemy_life/3)) && _is_way)
                {
                    _move.setButton(Karate_PMR.BUTTON_RIGHT);
                }
                else
                {
                    if (getInt(3)==3)
                    {
                        if (_is_way)
                        {
                            _move.setButton(Karate_PMR.BUTTON_RIGHT);
                        }
                        else
                        {
                            _move.setButton(genBeat(_enemy_state,_enemy_life,_my_life));
                        }
                    }
                    else
                        _move.setButton(genBeat(_enemy_state,_enemy_life,_my_life));
                }
            }
        }
        return _move;
    }

    protected int genBeat(int enemy_state,int enemylife,int mylife)
    {
        int mstate = Karate_PMR.BUTTON_NONE;

        switch (enemy_state)
        {
            case Karateker.STATE_BLOCKDOWN:
                {
                    if (getInt(_level) == _level)
                    {
                        if (getInt(1) == 1)
                        {
                            mstate = Karate_PMR.BUTTON_PUNCHMID;
                        }
                        else
                        {
                            mstate = Karate_PMR.BUTTON_PUNCHTOP;
                        }
                    }
                    else
                    {
                        mstate = Karate_PMR.BUTTON_NONE;
                    }
                }
                ;
                break;
            case Karateker.STATE_BLOCKMID:
                {
                    if (getInt(_level) == _level)
                    {
                        if (getInt(1) == 1)
                        {
                            mstate = Karate_PMR.BUTTON_PUNCHTOP;
                        }
                        else
                        {
                            mstate = Karate_PMR.BUTTON_PUNCHDOWN;
                        }
                    }
                    else
                    {
                        mstate = Karate_PMR.BUTTON_NONE;
                    }
                }
                ;
                break;
            case Karateker.STATE_BLOCKTOP:
                {
                    if (getInt(_level) == _level)
                    {
                        if (getInt(1) == 1)
                        {
                            mstate = Karate_PMR.BUTTON_PUNCHMID;
                        }
                        else
                        {
                            mstate = Karate_PMR.BUTTON_PUNCHDOWN;
                        }
                    }
                    else
                    {
                        mstate = Karate_PMR.BUTTON_NONE;
                    }
                }
                ;
                break;
            case Karateker.STATE_PUNCHDOWN:
                {
                    if (mylife>(Karateker.INIT_LIFE_VALUE>>1))
                    {
                        mstate = getPunch();
                    }
                    else
                    if (getInt(_level) != _level)
                    {
                        if (getInt(1) == 1)
                        {
                            mstate = Karate_PMR.BUTTON_BLOCKMID;
                        }
                        else
                        {
                            mstate = Karate_PMR.BUTTON_BLOCKDOWN;
                        }
                    }
                    else
                    {
                        mstate = getPunch();
                    }
                }
                ;
                break;
            case Karateker.STATE_PUNCHMID:
                {
                    if (mylife>(Karateker.INIT_LIFE_VALUE>>1))
                    {
                        mstate = getPunch();
                    }
                    else
                    if (getInt(_level) != _level)
                    {
                        if (getInt(1) == 1)
                        {
                            mstate = Karate_PMR.BUTTON_BLOCKDOWN;
                        }
                        else
                        {
                            mstate = Karate_PMR.BUTTON_BLOCKMID;
                        }
                    }
                    else
                    {
                        mstate = getPunch();
                    }
                }
                ;
                break;
            case Karateker.STATE_PUNCHTOP:
                {
                    if (mylife>(Karateker.INIT_LIFE_VALUE>>1))
                    {
                        mstate = getPunch();
                    }
                    else
                    if (getInt(_level) != _level)
                    {
                        if (getInt(1) == 1)
                        {
                            mstate = Karate_PMR.BUTTON_BLOCKMID;
                        }
                        else
                        {
                            mstate = Karate_PMR.BUTTON_BLOCKTOP;
                        }
                    }
                    else
                    {
                        mstate = getPunch();
                    }
                }
                ;
                break;
            case Karateker.STATE_DIED : mstate = Karate_PMR.BUTTON_NONE; break;
            default :
                {
                    if (getInt(_level) == _level)
                    {
                        mstate = getPunch();
                    }
                    else
                    {
                        mstate = Karate_PMR.BUTTON_NONE;
                    }
                }
        }

        return mstate;
    }

    protected int getPunch()
    {
                  switch(getInt(2))
                    {
                        case 0: return Karate_PMR.BUTTON_PUNCHTOP;
                        case 1: return Karate_PMR.BUTTON_PUNCHMID;
                        case 2: return Karate_PMR.BUTTON_PUNCHDOWN;
                    }
        return Karate_PMR.BUTTON_PUNCHMID;
    }

    public void initPlayer()
    {
        _move = new Karate_PMR();
        _prev_state = Karate_PMR.BUTTON_NONE;
    }
}
