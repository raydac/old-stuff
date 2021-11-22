package com.igormaznitsa.GameKit_FE652.Seabattle;

import com.igormaznitsa.GameAPI.GameStateRecord;

public class Seabattle_GSR implements GameStateRecord
{
    public static final int GAMESTATE_PLAYED = 0;
    public static final int GAMESTATE_OVER = 1;

    public static final int PLAYERSTATE_NORMAL = 0;
    public static final int PLAYERSTATE_WON = 1;
    public static final int PLAYERSTATE_LOST = 2;

    protected SBLogic _player;
    protected SBLogic _ai;

    protected int _player_state = 0;
    protected int _ai_state = 0;

    protected int _game_state;
    protected boolean _ai_is_moving;

    protected int _last_opponent_x;
    protected int _last_opponent_y;

    public boolean isPlayerMoving()
    {
        return !_ai_is_moving;
    }

    public SBLogic getPlayerLogic()
    {
        return _player;
    }

    public SBLogic getAILogic()
    {
        return _ai;
    }

    public Seabattle_GSR()
    {
        _last_opponent_x = -1;
        _last_opponent_y = -1;

        _player = new SBLogic();
        _ai = new SBLogic();
        _player.init();
        _ai.init();
        _game_state = GAMESTATE_PLAYED;
        _player_state = PLAYERSTATE_NORMAL;
        _ai_state = PLAYERSTATE_NORMAL;

        if (SBLogic._rnd.getInt(10)>5)
            _ai_is_moving = true;
        else
            _ai_is_moving = false;
    }

    public int getLastOpponentX()
    {
        return _last_opponent_x;
    }

    public int getLastOpponentY()
    {
        return _last_opponent_y;
    }

    public int getGameState()
    {
        return _game_state;
    }

    public int getPlayerScores()
    {
        return 0;
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
        return _ai_state;
    }

    public int getLevel()
    {
        return 0;
    }

    public int getStage()
    {
        return 0;
    }
}
