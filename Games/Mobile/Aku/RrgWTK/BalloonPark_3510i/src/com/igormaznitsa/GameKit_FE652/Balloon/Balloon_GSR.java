package com.igormaznitsa.GameKit_FE652.Balloon;

import com.igormaznitsa.GameAPI.GameStateRecord;

public class Balloon_GSR implements GameStateRecord
{
    public static final int GAME_PLAYED = 0;
    public static final int GAME_OVER = 1;

    public static final int PLAYER_BURNINGOFF = 0;
    public static final int PLAYER_BURNINGON = 1;
    public static final int PLAYER_DEAD = 2;
    public static final int PLAYER_FINISHED = 3;
    public static final int PLAYER_GAS_IS_ALL = 4;

    protected int _level;
    protected int _flows_number;
    protected AirFlow[] _flow_array;

    protected int _player_x;
    protected int _player_y;
    protected int _player_state;
    protected int _player_speed;
    protected int _cur_gas_level;
    protected int _airflow_step;
    protected int _game_state;
    protected boolean _ground_present;

    protected int _player_scores = 0;

    public Balloon_GSR(int level, int _px, int _py)
    {
        _level = level;
        _player_x = _px;
        _player_y = _py;

        _player_state = PLAYER_BURNINGOFF;
        _player_speed = 0;
        _game_state = GAME_PLAYED;

        switch (_level)
        {
            case Balloon_SB.LEVEL0:
                {
                    _flows_number = Balloon_SB.LEVEL0_FLOWS;
                    _cur_gas_level = Balloon_SB.LEVEL0_GAS_LEVEL;
                    _ground_present = Balloon_SB.LEVEL0_GROUNDPRESENT;
                }
                ;
                break;
            case Balloon_SB.LEVEL1:
                {
                    _flows_number = Balloon_SB.LEVEL1_FLOWS;
                    _cur_gas_level = Balloon_SB.LEVEL1_GAS_LEVEL;
                    _ground_present = Balloon_SB.LEVEL1_GROUNDPRESENT;
                }
                ;
                break;
            case Balloon_SB.LEVEL2:
                {
                    _flows_number = Balloon_SB.LEVEL2_FLOWS;
                    _cur_gas_level = Balloon_SB.LEVEL2_GAS_LEVEL;
                    _ground_present = Balloon_SB.LEVEL2_GROUNDPRESENT;
                }
                ;
                break;
        }

        _flow_array = new AirFlow[_flows_number];

        _airflow_step = Balloon_SB.FLOWAREA_HEIGHT / _flows_number;

        boolean _flag = true;
        for (int li = 0; li < _flows_number; li++)
        {
            int _sp = Balloon_SB._rnd.getInt(Balloon_SB.FLOW_MAXSPEED + 1) + 1;
            if (_flag) _sp = -_sp;
            _flow_array[li] = new AirFlow(_airflow_step * li + Balloon_SB.BALLOON_SAIL_OFFSET, _airflow_step, Balloon_SB._rnd.getInt(Balloon_SB.FIELD_WIDTH), _sp);
            _flag = !_flag;
        }
    }

    public int getPlayerX()
    {
        return _player_x;
    }

    public int getPlayerY()
    {
        return _player_y;
    }

    public int getGasLevel()
    {
        return _cur_gas_level;
    }

    public AirFlow[] getFlowArray()
    {
        return _flow_array;
    }

    public int getAIScores()
    {
        return 0;
    }

    public int getAIState()
    {
        return 0;
    }

    public int getGameState()
    {
        return _game_state;
    }

//    public int getPlayerScores()
//    {
//        int scr = 0;
//        if (_player_speed < Balloon_SB.DEATH_SPEED)
//        {
//            scr = _cur_gas_level * 2 *(_level+1);
//        }
//        else
//        {
//            scr = (_cur_gas_level/30) / 8 / (_level+1);
//        }
//        return scr;
//    }

    public int getPlayerScores()
    {
        int scr = 0;
        switch (_level)
        {
            case Balloon_SB.LEVEL0:
                scr = Balloon_SB.LEVEL0_GAS_LEVEL;
                break;
            case Balloon_SB.LEVEL1:
                scr = Balloon_SB.LEVEL1_GAS_LEVEL;
                break;
            case Balloon_SB.LEVEL2:
                scr = Balloon_SB.LEVEL2_GAS_LEVEL;
                break;
        }

        switch (_player_state)
        {
            case PLAYER_DEAD:
            case PLAYER_GAS_IS_ALL:
                {
                    scr -= _cur_gas_level;
                    scr += _level * 1000;
                };break;
            case PLAYER_FINISHED:
                {
                scr -= _cur_gas_level;
                scr += _level * 1000 + 1500;
                };break;

        }
        return scr;
    }

    public int getPlayerState()
    {
        return _player_state;
    }

    public int getLevel()
    {
        return _level;
    }

    public int getStage()
    {
        return 0;
    }
}
