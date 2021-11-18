package com.igormaznitsa.GameKit_FE652.Karate;

public abstract class Karateker
{
    // Sizes
    public static final int FRAME_WIDTH = 50;
    public static final int FRAME_HEIGHT = 41;

    // Move speed
    public static final int HOR_MOVE_SPEED = 10;

    // States
    public static final int STATE_STAND = 0;
    public static final int STATE_MOVELEFT = 1;
    public static final int STATE_MOVERIGHT = 2;

    public static final int STATE_BLOCKTOP = 3;
    public static final int STATE_BLOCKMID = 4;
    public static final int STATE_BLOCKDOWN = 5;

    public static final int STATE_PUNCHTOP = 6;
    public static final int STATE_PUNCHMID = 7;
    public static final int STATE_PUNCHDOWN = 8;

    public static final int STATE_DIED = 9;

    // Animation
    public static final int ANIMATION_STAND_FRAMES = 3;
    public static final int ANIMATION_STAND_DELAY = 6;
    public static final boolean ANIMATION_STAND_ISBACK = true;

    public static final int ANIMATION_MOVELEFT_FRAMES = 5;
    public static final int ANIMATION_MOVELEFT_DELAY = HOR_MOVE_SPEED / ANIMATION_MOVELEFT_FRAMES;
    public static final boolean ANIMATION_MOVELEFT_ISBACK = false;

    public static final int ANIMATION_MOVERIGHT_FRAMES = 5;
    public static final int ANIMATION_MOVERIGHT_DELAY = HOR_MOVE_SPEED / ANIMATION_MOVERIGHT_FRAMES;
    public static final boolean ANIMATION_MOVERIGHT_ISBACK = false;

    public static final int ANIMATION_BLOCKTOP_FRAMES = 5;
    public static final int ANIMATION_BLOCKTOP_DELAY = 1;
    public static final boolean ANIMATION_BLOCKTOP_ISBACK = true;

    public static final int ANIMATION_BLOCKMID_FRAMES = 6;
    public static final int ANIMATION_BLOCKMID_DELAY = 1;
    public static final boolean ANIMATION_BLOCKMID_ISBACK = true;

    public static final int ANIMATION_BLOCKDOWN_FRAMES = 6;
    public static final int ANIMATION_BLOCKDOWN_DELAY = 1;
    public static final boolean ANIMATION_BLOCKDOWN_ISBACK = true;

    public static final int ANIMATION_PUNCHTOP_FRAMES = 5;
    public static final int ANIMATION_PUNCHTOP_DELAY = 2;
    public static final boolean ANIMATION_PUNCHTOP_ISBACK = true;

    public static final int ANIMATION_PUNCHMID_FRAMES = 5;
    public static final int ANIMATION_PUNCHMID_DELAY = 2;
    public static final boolean ANIMATION_PUNCHMID_ISBACK = true;

    public static final int ANIMATION_PUNCHDOWN_FRAMES = 4;
    public static final int ANIMATION_PUNCHDOWN_DELAY = 2;
    public static final boolean ANIMATION_PUNCHDOWN_ISBACK = true;

    public static final int ANIMATION_DIED_FRAMES = 3;
    public static final int ANIMATION_DIED_DELAY = 6;
    public static final boolean ANIMATION_DIED_ISBACK = true;

    public static final int KICKBLOCK_DELAY = 2;

    // Sense zones
    public static final int SENSE_STAND = 0;
    public static final int SENSE_MOVELEFT = 1;
    public static final int SENSE_MOVERIGHT = 2;
    public static final int SENSE_BLOCKTOP = 3;
    public static final int SENSE_BLOCKMID = 4;
    public static final int SENSE_BLOCKDOWN = 5;
    public static final int SENSE_PUNCHTOP = 6;
    public static final int SENSE_PUNCHMID = 7;
    public static final int SENSE_PUNCHDOWN = 8;

    // Breaking zones
    public static final int BREAKING_STAND = 0;
    public static final int BREAKING_MOVELEFT = 1;
    public static final int BREAKING_MOVERIGHT = 2;
    public static final int BREAKING_BLOCKTOP = 3;
    public static final int BREAKING_BLOCKMID = 4;
    public static final int BREAKING_BLOCKDOWN = 5;
    public static final int BREAKING_PUNCHTOP = 6;
    public static final int BREAKING_PUNCHMID = 7;
    public static final int BREAKING_PUNCHDOWN = 8;


    public static final int INIT_LIFE_VALUE = 100;

    protected int _cur_state;
    protected int _cur_frame;
    protected int _cur_tick;
    protected int _cur_life;
    protected boolean _is_back_phase;

    protected int _cur_maxframes;
    protected int _cur_maxtick;
    protected int _cur_move_len;

    protected int _xpos;
    protected int _ypos;

    protected boolean _back_phase;
    protected boolean _kickblock_phase;

    protected SenseBreakingZone[][] _sensezones;
    protected SenseBreakingZone[] _cur_sensezone;

    protected SenseBreakingZone[][] _breakingzones;
    protected SenseBreakingZone[] _cur_breakingzone;

    public Karateker(int startx, int starty,SenseBreakingZone[][] sensezones,SenseBreakingZone[][] breakingzones)
    {
        _sensezones = sensezones;
        _breakingzones = breakingzones;
        init(startx,starty);
    }

    public int getLifeStatus()
    {
        return _cur_life;
    }

    public int getX()
    {
        return _xpos;
    }

    public int getY()
    {
        return _ypos;
    }

    protected SenseBreakingZone [] getSenseZone()
    {
        return _cur_sensezone;
    }

    protected SenseBreakingZone [] getBreakingZone()
    {
        return _cur_breakingzone;
    }

    // Process of anmation phases... return true if it is max phase, but it is working for states where back phase flag is true
    public boolean processAnimation()
    {
        if (_kickblock_phase)
        {
            _cur_tick ++;
            if (_cur_tick >= KICKBLOCK_DELAY) _kickblock_phase = false;
                return true;
        }

        if (_cur_state == STATE_MOVELEFT)
        {
            _xpos --;
            _cur_move_len++;
            if (_cur_move_len >= HOR_MOVE_SPEED)
                setState(STATE_STAND);
        }
        else
        if (_cur_state == STATE_MOVERIGHT)
        {
            _xpos ++;
            _cur_move_len ++;
            if (_cur_move_len >= HOR_MOVE_SPEED) setState(STATE_STAND);
        }

        _cur_tick ++;
        if (_cur_tick >= _cur_maxtick)
        {
            _cur_tick  = 0;
            if (_is_back_phase && _back_phase)
            {
                _cur_frame --;
                if (_cur_frame <0)
                {
                    setState(STATE_STAND);
                }
            }
            else
            {
                _cur_frame ++;
                if (_cur_frame >= _cur_maxframes)
                {
                    _cur_frame = _cur_maxframes-1;
                    if (_is_back_phase)
                    {
                        _back_phase = true;
                        if ((_cur_state != STATE_STAND) && (_cur_state != STATE_MOVELEFT) && (_cur_state != STATE_MOVERIGHT))
                        {
                            _kickblock_phase = true;
                            return true;
                        }
                    }
                    else
                        setState(STATE_STAND);
                }
            }
        }
        return false;
    }

    public int getState()
    {
        return _cur_state;
    }

    public int getFrame()
    {
        return _cur_frame;
    }

    public void setState(int state)
    {
        if ((_cur_state != STATE_STAND) && (state!=STATE_STAND)) return;

        switch (state)
        {
            case STATE_STAND:
                {
                    _cur_sensezone = _sensezones[SENSE_STAND];
                    _cur_breakingzone = _breakingzones[BREAKING_STAND];
                    _is_back_phase = ANIMATION_STAND_ISBACK;
                    _cur_maxframes = ANIMATION_STAND_FRAMES;
                    _cur_maxtick = ANIMATION_STAND_DELAY;
                }
                ;
                break;
            case STATE_MOVELEFT:
                {
                    _cur_sensezone = _sensezones[SENSE_MOVELEFT];
                    _cur_breakingzone = _breakingzones[SENSE_MOVELEFT];
                    _is_back_phase = ANIMATION_MOVELEFT_ISBACK;
                    _cur_maxframes = ANIMATION_MOVELEFT_FRAMES;
                    _cur_maxtick = ANIMATION_MOVELEFT_DELAY;
                    _cur_move_len = 0;
                }
                ;
                break;
            case STATE_MOVERIGHT:
                {
                    _cur_sensezone = _sensezones[SENSE_MOVERIGHT];
                    _cur_breakingzone = _breakingzones[BREAKING_MOVERIGHT];
                    _is_back_phase = ANIMATION_MOVERIGHT_ISBACK;
                    _cur_maxframes = ANIMATION_MOVERIGHT_FRAMES;
                    _cur_maxtick = ANIMATION_MOVERIGHT_DELAY;
                    _cur_move_len = 0;
                }
                ;
                break;

            case STATE_BLOCKTOP:
                {
                    _cur_sensezone = _sensezones[SENSE_BLOCKTOP];
                    _cur_breakingzone = _breakingzones[BREAKING_BLOCKTOP];
                    _cur_maxframes = ANIMATION_BLOCKTOP_FRAMES;
                    _is_back_phase = ANIMATION_BLOCKTOP_ISBACK;
                    _cur_maxtick = ANIMATION_BLOCKTOP_DELAY;
                }
                ;
                break;
            case STATE_BLOCKMID:
                {
                    _cur_sensezone = _sensezones[SENSE_BLOCKMID];
                    _cur_breakingzone = _breakingzones[BREAKING_BLOCKMID];
                    _cur_maxframes = ANIMATION_BLOCKMID_FRAMES;
                    _is_back_phase = ANIMATION_BLOCKMID_ISBACK;
                    _cur_maxtick = ANIMATION_BLOCKMID_DELAY;
                }
                ;
                break;
            case STATE_BLOCKDOWN:
                {
                    _cur_sensezone = _sensezones[SENSE_BLOCKDOWN];
                    _cur_breakingzone = _breakingzones[BREAKING_BLOCKDOWN];
                    _is_back_phase = ANIMATION_BLOCKDOWN_ISBACK;
                    _cur_maxframes = ANIMATION_BLOCKDOWN_FRAMES;
                    _cur_maxtick = ANIMATION_BLOCKDOWN_DELAY;
                }
                ;
                break;
            case STATE_PUNCHTOP:
                {
                    _cur_sensezone = _sensezones[SENSE_PUNCHTOP];
                    _cur_breakingzone = _breakingzones[BREAKING_PUNCHTOP];
                    _is_back_phase = ANIMATION_PUNCHTOP_ISBACK;
                    _cur_maxframes = ANIMATION_PUNCHTOP_FRAMES;
                    _cur_maxtick = ANIMATION_PUNCHTOP_DELAY;
                }
                ;
                break;
            case STATE_PUNCHMID:
                {
                    _cur_sensezone = _sensezones[SENSE_PUNCHMID];
                    _cur_breakingzone = _breakingzones[BREAKING_PUNCHMID];
                    _is_back_phase = ANIMATION_PUNCHMID_ISBACK;
                    _cur_maxframes = ANIMATION_PUNCHMID_FRAMES;
                    _cur_maxtick = ANIMATION_PUNCHMID_DELAY;
                }
                ;
                break;

            case STATE_PUNCHDOWN:
                {
                    _cur_sensezone = _sensezones[SENSE_PUNCHDOWN];
                    _cur_breakingzone = _breakingzones[BREAKING_PUNCHDOWN];
                    _is_back_phase = ANIMATION_PUNCHDOWN_ISBACK;
                    _cur_maxframes = ANIMATION_PUNCHDOWN_FRAMES;
                    _cur_maxtick = ANIMATION_PUNCHDOWN_DELAY;
                }
                ;
                break;
            case STATE_DIED:
                {
                    _is_back_phase = ANIMATION_DIED_ISBACK;
                    _cur_maxframes = ANIMATION_DIED_FRAMES;
                    _cur_maxtick = ANIMATION_DIED_DELAY;
                }
                ;
                break;
        }

        _cur_state = state;
        _cur_tick = 0;
        _cur_frame = 0;
        _back_phase = false;
        _kickblock_phase = false;
    }

    public void init(int x, int y)
    {
        _xpos = x;
        _ypos = y;
        _cur_life = INIT_LIFE_VALUE;
        setState(STATE_STAND);
    }

}
