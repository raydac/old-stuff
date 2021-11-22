package com.igormaznitsa.GameKit_FE652.Asteroids;

import com.igormaznitsa.GameAPI.GameStateRecord;

public class Asteroids_GSR implements GameStateRecord
{
    public static final int GAMESTATE_PLAYED = 0;
    public static final int GAMESTATE_OVER = 1;

    public static final int PLAYERSTATE_NORMAL = 0;
    public static final int PLAYERSTATE_KILLED = 1;
    public static final int PLAYERSTATE_FINISHED = 2;
    public static final int PLAYERSTATE_HIT = 3;

    protected int _last_hit_x = 0;
    protected int _last_hit_y = 0;

    protected Asteroid[] _all_asteroids;
    protected Asteroid[] _visible_asteroids;
    protected Asteroid[] _empty_asteroids;

    protected int _arraysize;
    protected int _visiblecount;
    protected int _emptycount;

    protected int _max_asteroids_per_line;

    protected int player_x = 0;
    protected int player_y = 0;

    protected int _game_state;
    protected int _planet_distance;

    protected int _relative_planet_x = 0;
    protected int _relative_planet_y = 0;

    protected int _player_state = 0;
    protected int _player_attemption = 0;

    protected int _cur_level = 0;

    protected int _cur_scores = 0;

    public int getLastHitX()
    {
        return _last_hit_x;
    }

    public int getLastHitY()
    {
        return _last_hit_y;
    }

    public Asteroids_GSR(int level)
    {
        _cur_level = level;
        _cur_scores = 0;
        switch (level)
        {
            case Asteroids_SB.LEVEL0:
                {
                    _arraysize = Asteroids_SB.LEVEL0_ASTEROIDS;
                    _max_asteroids_per_line = Asteroids_SB.LEVEL0_PERLINE;
                }
                ;
                break;
            case Asteroids_SB.LEVEL1:
                {
                    _arraysize = Asteroids_SB.LEVEL1_ASTEROIDS;
                    _max_asteroids_per_line = Asteroids_SB.LEVEL1_PERLINE;
                }
                ;
                break;
            case Asteroids_SB.LEVEL2:
                {
                    _arraysize = Asteroids_SB.LEVEL2_ASTEROIDS;
                    _max_asteroids_per_line = Asteroids_SB.LEVEL2_PERLINE;
                }
                ;
                break;
        }

        _all_asteroids = new Asteroid[_arraysize];

        for (int li = 0; li < _all_asteroids.length; li++)
        {
            _all_asteroids[li] = new Asteroid();
        }

        _visible_asteroids = new Asteroid[_arraysize];
        _empty_asteroids = new Asteroid[_arraysize];

        _game_state = GAMESTATE_PLAYED;
        _planet_distance = Asteroids_SB.PLANET_INIT_Z;

        _player_attemption = Asteroids_SB.PLAYER_ATTEMPTION;
    }

    public Asteroid [] getVisibleArray()
    {
        return _visible_asteroids;
    }

    public int getPlanetRX()
    {
        return _relative_planet_x;
    }

    public int getPlayerAttemption()
    {
        return _player_attemption;
    }

    public int getPlanetRY()
    {
        return _relative_planet_y;
    }

    public int getPlanetDistance()
    {
        return _planet_distance;
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
        if (_planet_distance<=0)
        {
            scr =  (_cur_scores*(_cur_level+1))/10;
        }
        else
        {
            scr =  (_cur_scores*(_cur_level+1))/(_planet_distance/1000);
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

    public int getLevel()
    {
        return _cur_level;
    }

    public int getStage()
    {
        return 0;
    }
}
