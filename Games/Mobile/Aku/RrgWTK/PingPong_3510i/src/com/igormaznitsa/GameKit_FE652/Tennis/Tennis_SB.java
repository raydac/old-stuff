package com.igormaznitsa.GameKit_FE652.Tennis;

import com.igormaznitsa.GameAPI.GameStateRecord;
import com.igormaznitsa.GameAPI.PlayerBlock;
import com.igormaznitsa.GameAPI.StrategicBlock;
import com.igormaznitsa.GameAPI.Utils.RndGenerator;

import java.io.*;

public class Tennis_SB implements StrategicBlock
{
    public static final int LEVEL0 = 0;
    public static final int LEVEL1 = 1;
    public static final int LEVEL2 = 2;

    public static final int TABLE_X = 5;
    public static final int TABLE_Y = 0;
    public static final int TABLE_WIDTH = 50;
    public static final int TABLE_LENGTH = 90;
    public static final int TABLE_CENTER = TABLE_WIDTH >> 1;
    public static final int GRID_X = TABLE_LENGTH >> 1;

    public static final int BALL_RACKET_DISTANCE = 8;
    public static final int RACKET_WIDTH = 7;
    public static final int RACKET_CENTER = RACKET_WIDTH >>1;
    public static final int PLAYER_X = 0;
    public static final int AI_X = (TABLE_X << 1) + TABLE_LENGTH;

    public static final int PLAYER_INIT_Y = (TABLE_WIDTH - RACKET_WIDTH) >> 1;
    public static final int AI_INIT_Y = (TABLE_WIDTH - RACKET_WIDTH) >> 1;

    public static final int RACKET_MOVE_SPEED = (RACKET_WIDTH>>1);

    public static final int BALL_HORIZ_SPEED = 3;

    public static final int BALL_WIDTH = 3;

    protected Tennis_GSR _game_state = null;
    protected PlayerBlock _player_block = null;
    protected PlayerBlock _ai_block = null;

    private static final int BALL_NORMAL = 0;
    private static final int BALL_LEFTOUTFIELD = 1;
    private static final int BALL_RIGHTOUTFIELD = 2;
    private static final int BALL_GRID = 3;

    public static final int BALL_MAX_Z = 30;

    protected  static final int BALL_SPEEDNORMAL_DZ_8 = 0 - (BALL_MAX_Z << 8)/((AI_X>>2)/BALL_HORIZ_SPEED);
    protected static final int BALL_SPEEDLOW_DZ_8 = 0 - (BALL_MAX_Z<<8)/((AI_X>>1)/BALL_HORIZ_SPEED);

    protected static RndGenerator _rnd = new RndGenerator(System.currentTimeMillis());

    public GameStateRecord getGameStateRecord()
    {
        return _game_state;
    }

    public void saveGameState(OutputStream outputStream) throws IOException
    {
        System.gc();
        DataOutputStream dos = new DataOutputStream(outputStream);

        dos.writeByte(_game_state._level);
        dos.writeByte(_game_state._ai_scores);
        dos.writeByte(_game_state._player_scores);
        dos.writeBoolean(_game_state._ball_lost);
        dos.writeByte(_game_state._local_score_counter);
        dos.writeBoolean(_game_state._player_serving);

        // Save player's racket state
        Racket rc = _game_state._player_racket;

        if (rc._attached_ball != null) dos.writeBoolean(true); else dos.writeBoolean(false);
        dos.writeByte(rc._state);
        dos.writeByte(rc._x);
        dos.writeByte(rc._y);

        // Save AI's racket state
        rc = _game_state._ai_racket;

        if (rc._attached_ball != null) dos.writeBoolean(true); else dos.writeBoolean(false);
        dos.writeByte(rc._state);
        dos.writeByte(rc._x);
        dos.writeByte(rc._y);

        // Save ball state
        dos.writeInt(_game_state._ball._dz_8);
        dos.writeByte(_game_state._ball._horiz_speed);
        dos.writeByte(_game_state._ball._state);
        dos.writeInt(_game_state._ball._vert_speed);
        dos.writeShort(_game_state._ball._x);
        dos.writeInt(_game_state._ball._y);
        dos.writeInt(_game_state._ball._z);

        dos = null;
        System.gc();
    }


    public void loadGameState(InputStream inputStream) throws IOException
    {
        System.gc();
        DataInputStream dos = new DataInputStream(inputStream);

        int lvl = dos.readByte();

        _game_state = null;
        System.gc();
        newGame(lvl);

        _game_state._ai_scores = dos.readByte();
        _game_state._player_scores = dos.readByte();
        _game_state._ball_lost = dos.readBoolean();
        _game_state._local_score_counter = dos.readByte();
        _game_state._player_serving = dos.readBoolean();

        // Load player's racket state
        Racket rc = _game_state._player_racket;

        if (dos.readBoolean())
        {
            rc.attachBall(_game_state._ball);
        }
        else
        {
            rc.attachBall(null);
        }

        rc._state = dos.readByte();
        rc._x = dos.readByte();
        rc._y = dos.readByte();

        // Load AI's racket state
        rc = _game_state._ai_racket;

        if (dos.readBoolean())
        {
            rc.attachBall(_game_state._ball);
        }
        else
        {
            rc.attachBall(null);
        }

        rc._state = dos.readByte();
        rc._x = dos.readByte();
        rc._y = dos.readByte();

        // Load ball state
        _game_state._ball._dz_8 = dos.readInt();
        _game_state._ball._horiz_speed = dos.readByte();
        _game_state._ball._state = dos.readByte();
        _game_state._ball._vert_speed = dos.readInt();
        _game_state._ball._x = dos.readShort();
        _game_state._ball._y = dos.readInt();
        _game_state._ball._z = dos.readInt();

        dos = null;
        System.gc();
    }

    public void newGame(int level)
    {
        _game_state = new Tennis_GSR(level, PLAYER_X, PLAYER_INIT_Y, AI_X, AI_INIT_Y);
        _ai_block = null;
        Tennis_AI _ai = new Tennis_AI(level);
        _ai_block = _ai;
        if (_player_block != null) _player_block.initPlayer();
        if (_ai_block != null) _ai_block.initPlayer();
    }

    public void nextGameStep()
    {
        if (_game_state._ball_lost)
        {
            _game_state._ai_racket.setY(AI_INIT_Y);
            _game_state._player_racket.setY(PLAYER_INIT_Y);

            if (_game_state._player_serving)
            {
                _game_state._player_racket.attachBall(_game_state._ball);
                _game_state._ball._vert_speed = 0;
            }
            else
            {
                _game_state._ai_racket.attachBall(_game_state._ball);
                _game_state._ball._vert_speed = 0;
            }
            _game_state._ball_lost = false;
            return;
        }

        _game_state._ball_lost = false;
        _game_state._player_scores_changed = false;

        if (_player_block == null) return;
        Tennis_PMR _pmr = (Tennis_PMR) _player_block.getPlayerMoveRecord(_game_state);

        processRacket(_pmr.getButton(), _game_state._player_racket);

        if (_ai_block != null)
        {
            _pmr = (Tennis_PMR) _ai_block.getPlayerMoveRecord(_game_state);
            processRacket(_pmr.getButton(), _game_state._ai_racket);
        }

        int br = processBall();
        switch (br)
        {
            case BALL_GRID:
            case BALL_LEFTOUTFIELD:
                {
                    _game_state._ai_scores++;
                    _game_state._local_score_counter++;
                    _game_state._ball_lost = true;
                }
                ;
                break;
            case BALL_RIGHTOUTFIELD:
                {
                    _game_state._player_scores++;
                    _game_state._local_score_counter++;
                    _game_state._ball_lost = true;
                    _game_state._player_scores_changed = true;
                }
                ;
                break;
        }

        if (br != BALL_NORMAL)
        {
            switch (_game_state._gamestate)
            {
                case Tennis_GSR.GAMESTATE_PLAYED:
                    {
                        if (_game_state._local_score_counter == 6)
                        {
                            _game_state._player_serving = !_game_state._player_serving;
                            _game_state._local_score_counter = 0;
                        }

                        if ((_game_state._ai_scores == 21) && (_game_state._player_scores < 20))
                        {
                            _game_state._playerstate = Tennis_GSR.PLAYERSTATE_LOST;
                            _game_state._aistate = Tennis_GSR.PLAYERSTATE_WON;
                            _game_state._gamestate = Tennis_GSR.GAMESTATE_OVER;
                        }
                        else if ((_game_state._ai_scores < 20) && (_game_state._player_scores == 21))
                        {
                            _game_state._playerstate = Tennis_GSR.PLAYERSTATE_WON;
                            _game_state._aistate = Tennis_GSR.PLAYERSTATE_LOST;
                            _game_state._gamestate = Tennis_GSR.GAMESTATE_OVER;
                        }
                        else if ((_game_state._ai_scores >= 20) && (_game_state._player_scores >= 20))
                        {
                            _game_state._gamestate = Tennis_GSR.GAMESTATE_EXTRA;
                        }
                    }
                    ;
                    break;
                case Tennis_GSR.GAMESTATE_EXTRA:
                    {
                        _game_state._player_serving = !_game_state._player_serving;
                        _game_state._local_score_counter = 0;

                        if (Math.abs(_game_state._ai_scores - _game_state._player_scores) >= 2)
                        {
                            if (_game_state._player_scores > _game_state._ai_scores)
                            {
                                _game_state._playerstate = Tennis_GSR.PLAYERSTATE_WON;
                                _game_state._aistate = Tennis_GSR.PLAYERSTATE_LOST;
                            }
                            else
                            {
                                _game_state._playerstate = Tennis_GSR.PLAYERSTATE_LOST;
                                _game_state._aistate = Tennis_GSR.PLAYERSTATE_WON;
                            }
                            _game_state._gamestate = Tennis_GSR.GAMESTATE_OVER;
                        }
                    }
                    ;
                    break;
            }

        }
    }

    protected void processBallAndRocket(Racket racket)
    {
        int bcx = _game_state._ball.getX()+1;
        int bcy = _game_state._ball.getY()+1;

        int endx = Tennis_SB.PLAYER_X;

        if (bcx< GRID_X) endx = Tennis_SB.AI_X;

        int dx = Math.abs(endx - bcx) / Tennis_SB.BALL_HORIZ_SPEED;

        _game_state._ball.is_center_crossed = false;

        _game_state._ball._horiz_speed = 0 - _game_state._ball._horiz_speed;

        int desty = 0;


        _game_state._ball._z = BALL_MAX_Z << 8;

        int rc = racket._y + RACKET_CENTER;

        int dhy = rc-_rnd.getInt(TABLE_WIDTH>>1)-25;
        int ddy = rc+_rnd.getInt(TABLE_WIDTH>>1)+25;

        switch (racket._state)
        {
            case Racket.RACKET_DOWN:
                {
                    _game_state._ball._vert_speed  = 0;
                    _game_state._ball._state = Ball.BALL_SPEEDLOW;
                    _game_state._ball._dz_8 = Tennis_SB.BALL_SPEEDLOW_DZ_8;
                    return;
                }
                
            case Racket.RACKET_UP:
                {
                    _game_state._ball._vert_speed  = 0;
                    _game_state._ball._dz_8 = Tennis_SB.BALL_SPEEDNORMAL_DZ_8;
                    return;
                }
                
            case Racket.RACKET_RIGHT:
                {
                    if (racket._type == Racket.RACKETPOS_LEFT)
                    {
                        desty = ddy;
                    }
                    else
                    {
                        desty = dhy;
                    }
                    _game_state._ball._dz_8 = Tennis_SB.BALL_SPEEDNORMAL_DZ_8;
                }
                ;
                break;
            case Racket.RACKET_LEFT:
                {
                    if (racket._type == Racket.RACKETPOS_LEFT)
                    {
                        desty = dhy;
                    }
                    else
                    {
                        desty = ddy;
                    }
                    _game_state._ball._dz_8 = Tennis_SB.BALL_SPEEDNORMAL_DZ_8;
                }
                ;
                break;
        }
        if (desty<RACKET_CENTER) desty = RACKET_CENTER;
            else
        if (desty>(TABLE_WIDTH-RACKET_CENTER)) desty = TABLE_WIDTH - RACKET_CENTER;

        _game_state._ball._vert_speed  = ((desty - bcy) << 16) / dx;
        return ;
    }

    protected boolean IsPointInRacket(Racket rck,int y)
    {
        if ((y >= rck._y) && ((y-rck._y) < RACKET_WIDTH)) return true; else return false;
    }

    protected int processBall()
    {
        if ((_game_state._player_racket._attached_ball != null) || (_game_state._ai_racket._attached_ball != null)) return BALL_NORMAL;
        Ball _ball = _game_state._ball;

        int dx = _ball._x;
        int dy = _ball._y;

        dx += _ball._horiz_speed;
        dy += _ball._vert_speed;

        _ball._z += _ball._dz_8;
        if ((_ball._z<0)&&(_ball._dz_8<0))
        {
            _ball._z=0;
            _ball._dz_8 = 0 - _ball._dz_8;
        }
        else
        if ((_ball._z>=(BALL_MAX_Z<<8))&&(_ball._dz_8>0))
        {
            _ball._z = BALL_MAX_Z<<8;
            _ball._dz_8 = 0 -_ball._dz_8;
        }

        int ddy = dy >> 16;

        if (!_ball.is_center_crossed)
        {
            if (_ball._horiz_speed < 0)
            {
                if (dx <= (GRID_X + TABLE_X))
                {
                    if (_ball._state == Ball.BALL_SPEEDLOW)
                    {
                        return BALL_GRID;
                    }
                    _ball.is_center_crossed = true;
                }
            }
            else
            {
                if (dx >= (GRID_X + TABLE_X))
                {
                    if (_ball._state == Ball.BALL_SPEEDLOW)
                    {
                        return BALL_GRID;
                    }
                    _ball.is_center_crossed = true;
                }
            }
        }

        if (dx < Tennis_SB.PLAYER_X)
        {
            if (IsPointInRacket(_game_state._player_racket,ddy)||IsPointInRacket(_game_state._player_racket,ddy+BALL_WIDTH))
            {
                _ball._x = _game_state._player_racket._x;
                _ball._y = dy;
                processBallAndRocket(_game_state._player_racket) ;
            }
            else
            {
                return BALL_LEFTOUTFIELD;
            }
        }
        else if (dx > Tennis_SB.AI_X)
        {
            if (IsPointInRacket(_game_state._ai_racket,ddy)||IsPointInRacket(_game_state._ai_racket,ddy+BALL_WIDTH))
            {
                 _ball._x = _game_state._ai_racket._x;
                _ball._y = dy;
                processBallAndRocket(_game_state._ai_racket) ;
            }
            else
            {
                return  BALL_RIGHTOUTFIELD;
            }
        }
        else
        {
            _ball._x = dx;
            _ball._y = dy;
        }
        return BALL_NORMAL;
    }

    protected void processRacket(int button, Racket racket)
    {
        racket._state = Racket.RACKET_DOWN;

        int dy = racket._y;

        switch (button)
        {
            case Tennis_PMR.BUTTON_NONE:
                ;
                break;
            case Tennis_PMR.BUTTON_LEFT:
                {
                    dy -= RACKET_MOVE_SPEED;
                    if (dy<0) dy = 0;
                }
                ;
                break;
            case Tennis_PMR.BUTTON_RIGHT:
                {
                    dy += RACKET_MOVE_SPEED;
                    if ((dy+RACKET_WIDTH)>=TABLE_WIDTH) dy = TABLE_WIDTH-RACKET_WIDTH-1;
                }
                ;
                break;
            case Tennis_PMR.BUTTON_BEAT:
                {
                    racket._state = Racket.RACKET_UP;
                    if (racket._attached_ball!=null)
                    {
                        racket._attached_ball = null;
                        processBallAndRocket(racket);
                    }
                }
                ;
                break;
            case Tennis_PMR.BUTTON_BEATLEFT:
                {
                    racket._state = Racket.RACKET_LEFT;
                    if (racket._attached_ball!=null)
                    {
                        racket._attached_ball = null;
                        processBallAndRocket(racket);
                    }
                }
                ;
                break;
            case Tennis_PMR.BUTTON_BEATRIGHT:
                {
                    racket._state = Racket.RACKET_RIGHT;
                    if (racket._attached_ball!=null)
                    {
                        racket._attached_ball = null;
                        processBallAndRocket(racket);
                    }
                }
                ;
                break;
        }

        racket.setY(dy);

        return;
    }

    public void setAIBlock(PlayerBlock aiBlock)
    {
        _ai_block = aiBlock;
    }

    public void setPlayerBlock(PlayerBlock playerBlock)
    {
        _player_block = playerBlock;
    }

    public boolean isLoadSaveSupporting()
    {
        return true;
    }
}
