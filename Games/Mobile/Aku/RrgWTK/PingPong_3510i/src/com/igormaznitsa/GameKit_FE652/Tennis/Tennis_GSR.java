package com.igormaznitsa.GameKit_FE652.Tennis;

import com.igormaznitsa.GameAPI.GameStateRecord;

public class Tennis_GSR implements GameStateRecord
{
    public static final int PLAYERSTATE_NORMAL = 0;
    public static final int PLAYERSTATE_WON = 1;
    public static final int PLAYERSTATE_LOST = 2;

    public static final int GAMESTATE_PLAYED = 0;
    public static final int GAMESTATE_EXTRA = 1;
    public static final int GAMESTATE_OVER = 2;

    protected int _level;
    protected int _playerstate;
    protected int _aistate;
    protected int _gamestate;

    protected Racket _player_racket;
    protected Racket _ai_racket;
    protected Ball _ball;

    protected boolean _player_serving = true;
    protected boolean _ball_lost = false;

    protected int _player_scores = 0;
    protected int _ai_scores = 0;

    protected boolean _player_scores_changed = false;

    protected int _local_score_counter = 0;

    public boolean isPlayerScoresChanged()
    {
        return _player_scores_changed;
    }

    public Ball getBall()
    {
        return _ball;
    }

    public Racket getPlayerRacket()
    {
        return _player_racket;
    }

    public Racket getAIRacket()
    {
        return _ai_racket;
    }

    public boolean isBallLost()
    {
        return _ball_lost;
    }

    public Tennis_GSR(int level,int plx,int ply,int aix,int aiy)
    {
        _player_racket = new Racket(plx,ply,Racket.RACKET_DOWN,Racket.RACKETPOS_LEFT);
        _ai_racket = new Racket(aix,aiy,Racket.RACKET_DOWN,Racket.RACKETPOS_RIGHT);
        _ai_racket._state = Racket.RACKET_UP;

        _ball = new Ball(0,0,0);
        if (_player_serving)
        {
            _player_racket.attachBall(_ball);
        }
        else
        {
            _ai_racket.attachBall(_ball);
        }

        _level = level;
    }

    public int getAIScores()
    {
        return _ai_scores;
    }

    public int getAIState()
    {
        return _aistate;
    }

    public int getGameState()
    {
        return _gamestate;
    }

    public int getPlayerScores()
    {
        return _player_scores;
    }

    public int getPlayerState()
    {
        return _playerstate;
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
