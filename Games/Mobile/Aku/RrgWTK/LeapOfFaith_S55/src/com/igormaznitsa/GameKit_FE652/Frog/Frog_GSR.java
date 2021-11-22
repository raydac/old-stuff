package com.igormaznitsa.GameKit_FE652.Frog;

import com.igormaznitsa.GameAPI.GameStateRecord;

public class Frog_GSR implements GameStateRecord
{
    public static final int PLAYERSTATE_NORMAL = 0;
    public static final int PLAYERSTATE_WON = 1;
    public static final int PLAYERSTATE_LOST = 2;

    public static final int GAMESTATE_PLAYED = 0;
    public static final int GAMESTATE_OVER = 1;

    public static final int FROG_MOVE_FRAMES = 3;
    public static final int FROG_MOVE_FRAMEDELAY = 1;

    public static final int FROG_JUMP_FRAMES = 5;
    public static final int FROG_JUMP_LENGTH = Car.CAR_HEIGHT;
    public static final int FROG_JUMP_FRAMEDELAY = FROG_JUMP_LENGTH / FROG_JUMP_FRAMES;

    public static final int PLAYER_VIEW_LEFT = 0;
    public static final int PLAYER_VIEW_RIGHT = 1;
    public static final int PLAYER_VIEW_UP = 2;
    public static final int PLAYER_VIEW_DOWN = 3;
    public static final int PLAYER_VIEW_JUMPDOWN = 4;
    public static final int PLAYER_VIEW_JUMPUP = 5;

    protected int _sublevel;
    protected int _level;

    protected int _delay;

    protected int _initplayer_x;
    protected int _initplayer_y;

    protected int _player_x;
    protected int _player_y;

    protected int _player_endy;
    protected Flow [] _flowarray;

    protected int _gamestate;
    protected int _playerstate;

    protected int _scores = 0;

    protected int _curattempt;

    protected int _playerviewstate;
    protected int _playertick;
    protected int _playerframe;
    protected int _jumplen;
    protected int _viewcarnumber;
    protected Car[] _viewcar;

    public int getPlayerViewState()
    {
        return _playerviewstate;
    }

    public int getPlayerFrame()
    {
        return _playerframe;
    }

    public Frog_GSR(int level)
    {
        _scores = 0;
        _gamestate = GAMESTATE_PLAYED;
        _playerstate = PLAYERSTATE_NORMAL;

        _curattempt = Frog_SB.MAX_USER_ATTEMPTION;

        _viewcar = new Car[50];
        _sublevel = 0;
        _level = level;

        switch(level)
        {
            case Frog_SB.LEVEL0 : _delay = Frog_SB.LEVEL0_DELAY; break;
            case Frog_SB.LEVEL1 : _delay = Frog_SB.LEVEL1_DELAY; break;
            case Frog_SB.LEVEL2 : _delay = Frog_SB.LEVEL2_DELAY; break;
        }
   }

    public int getTimeDelay()
    {
        return _delay;
    }

    public int getAttemptionNumber()
    {
        return _curattempt;
    }

    public Car [] getViewCarArray()
    {
        return _viewcar;
    }

    public int getViewCarNumber()
    {
        return _viewcarnumber;
    }

    public int getPlayerX()
    {
        return _player_x;
    }

    public int getPlayerY()
    {
        return _player_y;
    }

    protected  void setPlayerViewState(int state)
    {
        _playerviewstate = state;
        _playertick = 0;
        _playerframe = 0;
        _jumplen = 0;
    }

    public void setSublevel (int num)
    {
        _playerstate = PLAYERSTATE_NORMAL;
        int [] _sublevelarr = Frog_SB.SUBLEVELS_FLOWMAP [num];
        _sublevel = num;

        _initplayer_x = _sublevelarr[0];
        _initplayer_y = _sublevelarr[1];
        _player_endy = _sublevelarr[2];

        _player_x = _initplayer_x;
        _player_y = _initplayer_y;

        _flowarray = null;
        _flowarray = new Flow [(_sublevelarr.length-3)/5];

        int loff = 3;
        for(int li=0;li<_flowarray.length;li++)
        {
            int _ftype = _sublevelarr[loff++];
            int _fy = _sublevelarr[loff++];
            int _fd = _sublevelarr[loff++];
            int _ff = _sublevelarr[loff++];
            int _fsp = _sublevelarr[loff++];

            Flow _newflow = new Flow(_fy,_fd,_ftype,_fsp,_ff);
            _flowarray[li] = _newflow;
        }
        setPlayerViewState(PLAYER_VIEW_UP);
    }

    public int getSublevel()
    {
        return _sublevel;
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
        return _gamestate;
    }

    public int getPlayerScores()
    {
        return _scores*(_level+1);
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
        return _sublevel;
    }
}
