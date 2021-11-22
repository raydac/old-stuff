package com.igormaznitsa.GameKit_FE652.Offroad;

import com.igormaznitsa.GameAPI.GameStateRecord;

public class Offroad_GSR implements GameStateRecord
{
    public static final int GAMESTATE_PLAYED = 0;
    public static final int GAMESTATE_OVER = 1;

    public static final int PLAYERSTATE_NORMAL = 0;
    public static final int PLAYERSTATE_KILLED = 1;
    public static final int PLAYERSTATE_FINISHED = 2;

    protected Obstacle[] _all_obstacles;
    protected Obstacle[] _visible_obstacles;
    protected Obstacle[] _empty_obstacles;

    protected int _arraysize;
    protected int _visiblecount;
    protected int _emptycount;

    protected int _max_obstacles_per_line;

    protected int player_x = 0;
    protected int player_y = 0;

    protected int _game_state;
    protected int _city_distance;

    protected int _relative_city_x = 0;
    protected int _relative_city_y = 0;

    protected int _player_state = 0;
    protected int _player_attemption = 0;

    protected int _cur_level = 0;
    protected int _cur_scores = 0;


    public int getLevel()
    {
        return _cur_level;
    }

    public Offroad_GSR(int level)
    {
        _cur_scores = 0;
        _cur_level = level;
        switch (level)
        {
            case Offroad_SB.LEVEL0:
                {
                    _arraysize = Offroad_SB.LEVEL0_OBSTACLES;
                    _max_obstacles_per_line = Offroad_SB.LEVEL0_PERLINE;
                }
                ;
                break;
            case Offroad_SB.LEVEL1:
                {
                    _arraysize = Offroad_SB.LEVEL1_OBSTACLES;
                    _max_obstacles_per_line = Offroad_SB.LEVEL1_PERLINE;
                }
                ;
                break;
            case Offroad_SB.LEVEL2:
                {
                    _arraysize = Offroad_SB.LEVEL2_OBSTACLES;
                    _max_obstacles_per_line = Offroad_SB.LEVEL2_PERLINE;
                }
                ;
                break;
        }

        _all_obstacles = new Obstacle[_arraysize];

        for (int li = 0; li < _all_obstacles.length; li++)
        {
            _all_obstacles[li] = new Obstacle();
        }

        _visible_obstacles = new Obstacle[_arraysize];
        _empty_obstacles = new Obstacle[_arraysize];

        _game_state = GAMESTATE_PLAYED;
        _city_distance = Offroad_SB.CITY_INIT_Z;

        _player_attemption = Offroad_SB.PLAYER_ATTEMPTION;
    }

    public Obstacle [] getVisibleArray()
    {
        return _visible_obstacles;
    }

    public int getCityRX()
    {
        return _relative_city_x;
    }

    public int getPlayerAttemption()
    {
        return _player_attemption;
    }

    public int getCityRY()
    {
        return Offroad_SB.CITY_REL_Y;
    }

    public int getCityDistance()
    {
        return _city_distance;
    }

    public int getVisibleCounter()
    {
        return _visiblecount;
    }

    public int getGameState()
    {
        return _game_state;
    }

    public int getPlayerScores()
    {
        int scr = 0;
        if (_city_distance<=0)
        {
            scr =  (_cur_scores*(_cur_level+1))/10;
        }
        else
        {
            scr =  (_cur_scores*(_cur_level+1))/(_city_distance/1000);
        }
        return scr;
    }

    public int getAIScores()
    {
        return 0;
    }

    public int getPlayerState()
    {
        return _player_state;
    }

    public int getAIState()
    {
        return 0;
    }

    public int getStage()
    {
        return 0;
    }
}
