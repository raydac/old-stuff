package com.igormaznitsa.GameKit_FE652.Karate;

import com.igormaznitsa.GameAPI.GameStateRecord;

public class Karate_GSR implements GameStateRecord
{
    public static final int PLAYERSTATE_NORMAL = 0;
    public static final int PLAYERSTATE_WON = 1;
    public static final int PLAYERSTATE_LOST = 2;

    public static final int GAMESTATE_PLAYED = 0;
    public static final int GAMESTATE_OVER = 1;

    protected int _player_state;
    protected int _ai_state;
    protected int _game_state;
    protected int _scores;

    protected int _level;
    protected int _stage;

    protected KaratekerLeft _player_karateker;
    protected KaratekerRight _ai_karateker;

    protected AIPlayer _aiplayer = null;

    public Karate_GSR(int level)
    {
        _level = level;
        _stage = 0;
        _scores = 0;
        _game_state = GAMESTATE_PLAYED;
        _player_state = PLAYERSTATE_NORMAL;
        _ai_state = PLAYERSTATE_NORMAL;

        int lvl = 0;

        switch (level)
        {
            case Karate_SB.LEVEL0 : lvl = AIPlayer.LEVEL_MIN ; break;
            case Karate_SB.LEVEL1 : lvl = AIPlayer.LEVEL_MID ; break;
            case Karate_SB.LEVEL2 : lvl = AIPlayer.LEVEL_MAX ; break;
        }

        _aiplayer = new AIPlayer(lvl);

        _player_karateker = new KaratekerLeft(0,Karate_SB.FIELD_HEIGHT-Karateker.FRAME_HEIGHT);
        _ai_karateker = new KaratekerRight(Karate_SB.FIELD_WIDTH-Karateker.FRAME_WIDTH,Karate_SB.FIELD_HEIGHT-Karateker.FRAME_HEIGHT);
    }

    protected void initStage(int stage)
    {
        _stage = stage;

        _player_state = PLAYERSTATE_NORMAL;
        _ai_state = PLAYERSTATE_NORMAL;
        _game_state = GAMESTATE_PLAYED;

        int curlife = _player_karateker.getLifeStatus();

        _player_karateker.init(0,Karate_SB.FIELD_HEIGHT-Karateker.FRAME_HEIGHT);
        _player_karateker._cur_life = curlife;

        _ai_karateker.init(Karate_SB.FIELD_WIDTH-Karateker.FRAME_WIDTH,Karate_SB.FIELD_HEIGHT-Karateker.FRAME_HEIGHT);
    }

    public int getStage()
    {
        return _stage;
    }

    public int getPlayerLifeState()
    {
        return _player_karateker.getLifeStatus();
    }

    public int getAILifeState()
    {
        return _ai_karateker.getLifeStatus();
    }

    public Karateker getPlayerPerson()
    {
        return _player_karateker;
    }

    public Karateker getAIPerson()
    {
        return _ai_karateker;
    }

    public int getAIScores()
    {
        return 0;
    }

    public int getAIState()
    {
        return _ai_state;
    }

    public int getGameState()
    {
        return _game_state;
    }

    public int getPlayerScores()
    {
        return _scores;
    }

    public int getPlayerState()
    {
        return _player_state;
    }

    public int getLevel()
    {
        return _level;
    }
}