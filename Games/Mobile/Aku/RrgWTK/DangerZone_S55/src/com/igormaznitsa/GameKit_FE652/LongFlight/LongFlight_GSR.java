package com.igormaznitsa.GameKit_FE652.LongFlight;

import com.igormaznitsa.GameAPI.GameStateRecord;

public class LongFlight_GSR implements GameStateRecord
{
    public static final int PLAYERSTATE_NORMAL = 0;
    public static final int PLAYERSTATE_KILLED = 1;
    public static final int PLAYERSTATE_WON = 2;
    public static final int PLAYERSTATE_OUTOFAMMO = 3;

    public static final int DEATHSTATE_CRASH = 0;
    public static final int DEATHSTATE_BATTERED = 1;
    public static final int DEATHSTATE_EXPLOSED = 2;

    public static final int GAMESTATE_PLAYED = 0;
    public static final int GAMESTATE_OVER = 1;

    protected ExplObj[] _bombs = null;
    protected ExplObj[] _shells = null;
    protected ExplObj[] _rockets = null;

    protected byte[] _way;
    protected int _max_factory = 0;
    protected int _attemptions;

    protected int _player_state;
    protected int _game_state;
    protected int _player_scores;
    protected int _deathstate = 0;
    protected int _cur_factory = 0;

    protected int _player_pos_y = 0;
    protected int _player_abs_pos_x = 0;
    protected int _init_ammo = 0;
    protected int _cur_bombs = 0;
    protected int _cur_level = 0;
    protected int _cur_shellfreq = 0;

    protected int _cur_stage = 0;

    public int getBombsNumber()
    {
        return _cur_bombs;
    }

    public int getPlayerAbsX()
    {
        return _player_abs_pos_x;
    }

    public int getPlayerY()
    {
        return _player_pos_y;
    }

    protected void initWayArray(int waynum)
    {
        _player_abs_pos_x = 0;
        _cur_stage = waynum;
        _way = null;
        System.gc();
        _way = Way.getWay(waynum);
        _max_factory = 0;
        _cur_factory = 0;
        for(int li=0;li<_way.length;li++)
        {
            if ((_way[li]&0xF0)==Way.OBJ_FACTORY) _max_factory++;
        }
    }

    public int getElementAtPos(int x)
    {
        if (x>=_way.length)
        {
            x = x - _way.length;
        }
        else
        if (x<0)
        {
            x += _way.length;
        }

        return _way[x];
    }

    public void setElementAtPos(int x,int ele)
    {
        if (x>=_way.length)
        {
            x = x - _way.length;
        }
        else
        if (x<0)
        {
            x += _way.length;
        }

        _way[x] = (byte)((_way[x] & 0x0F) | ele);
    }

    protected void initAmmo()
    {
        _cur_bombs = _init_ammo;

        for(int li=0;li<_bombs.length;li++)
        {
            _bombs [li]._active = false;
        }
        for(int li=0;li<_rockets.length;li++)
        {
            _rockets [li]._active = false;
        }

        for(int li=0;li<_shells.length;li++)
        {
            _shells [li]._active = false;
        }
    }

    public ExplObj [] getBombArray()
    {
        return _bombs;
    }

    public ExplObj [] getShellArray()
    {
        return _shells;
    }

    public ExplObj [] getRocketArray()
    {
        return _rockets;
    }

    public LongFlight_GSR(int level,int stage)
    {
        _cur_level = level;
        _player_pos_y = LongFlight_SB.PLAYER_INIT_Y_OFFSET;
        _game_state = GAMESTATE_PLAYED;
        _player_state = PLAYERSTATE_NORMAL;
        _player_scores = 0;
        int rckts = 0;
        int shells = 0;

        switch (level)
        {
            case LongFlight_SB.LEVEL0:
                {
                    rckts = LongFlight_SB.LEVEL0_ROCKETS;
                    _init_ammo = LongFlight_SB.LEVEL0_BOMBS;
                    shells = LongFlight_SB.LEVEL0_SHELLS;
                    _attemptions = LongFlight_SB.LEVEL0_ATTEMPTS;
                    _cur_shellfreq = LongFlight_SB.SHELL_FREQ_LEVEL0;
                }
                ;
                break;
            case LongFlight_SB.LEVEL1:
                {
                    rckts = LongFlight_SB.LEVEL1_ROCKETS;
                    _init_ammo = LongFlight_SB.LEVEL1_BOMBS;
                    shells = LongFlight_SB.LEVEL1_SHELLS;
                    _attemptions = LongFlight_SB.LEVEL1_ATTEMPTS;
                    _cur_shellfreq = LongFlight_SB.SHELL_FREQ_LEVEL1;
                }
                ;
                break;
            case LongFlight_SB.LEVEL2:
                {
                    rckts = LongFlight_SB.LEVEL2_ROCKETS;
                    _init_ammo = LongFlight_SB.LEVEL2_BOMBS;
                    shells = LongFlight_SB.LEVEL2_SHELLS;
                    _attemptions = LongFlight_SB.LEVEL2_ATTEMPTS;
                    _cur_shellfreq = LongFlight_SB.SHELL_FREQ_LEVEL2;
                }
                ;
                break;
        }

        _bombs = new ExplObj[_init_ammo];
        for(int li=0;li<_bombs.length;li++)
        {
            _bombs [li]= new ExplObj();
        }

        _rockets = new ExplObj[rckts];
        for(int li=0;li<_rockets.length;li++)
        {
            _rockets [li]= new ExplObj();
        }

        _shells = new ExplObj[shells];
        for(int li=0;li<_shells.length;li++)
        {
            _shells [li]= new ExplObj();
        }

        initWayArray(stage);
        initAmmo();
    }

    protected void resumeGame()
    {
       _player_pos_y = LongFlight_SB.PLAYER_INIT_Y_OFFSET;
        _player_state = PLAYERSTATE_NORMAL;
        initAmmo();
    }

    public int getDeathState()
    {
        return _deathstate;
    }

    public int getAttemptions()
    {
        return _attemptions;
    }

    public int getGameState()
    {
        return _game_state;
    }

    public int getPlayerScores()
    {
        return _player_scores;
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
        return _cur_stage;
    }
}
