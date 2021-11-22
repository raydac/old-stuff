package com.igormaznitsa.GameKit_FE652.Wall;

import com.igormaznitsa.GameAPI.StrategicBlock;
import com.igormaznitsa.GameAPI.GameStateRecord;
import com.igormaznitsa.GameAPI.PlayerBlock;
import com.igormaznitsa.GameAPI.GameActionListener;
import com.igormaznitsa.GameAPI.Utils.RndGenerator;

import java.io.*;

public class Wall_SB implements StrategicBlock
{
    public static final int LEVEL0 = 0;
    public static final int LEVEL0_DELAY = 160;

    public static final int LEVEL1 = 1;
    public static final int LEVEL1_DELAY = 100;

    public static final int LEVEL2 = 2;
    public static final int LEVEL2_DELAY = 70;

    public static final int CELL_WIDTH = 10;
    public static final int CELL_HEIGHT = 4;

    public static final int RACKET_NORMAL_WIDTH = 20;
    public static final int RACKET_LONG_WIDTH = 30;
    public static final int RACKET_HEIGHT = 4;

    public static final int BALLS_MAX_NUMBER = 3;

    public static final int BALL_HEIGHT = 4;
    public static final int BALL_WIDTH = BALL_HEIGHT;

    public static final int RACKET_SPEED = 5;
    public static final int SURPRISE_SPEED = 3;

    public static final int PLAYER_MAX_ATTEMPTIONS = 4;

    public static final int RACKETSTATE_NORMAL = 0;
    public static final int RACKETSTATE_LONG = 1;

    public static final int FIELD_WIDTH = CELL_WIDTH * Labyrinths.FIELD_WIDTH;
    public static final int FIELD_HEIGHT = CELL_HEIGHT * Labyrinths.FIELD_HEIGHT;

    protected Wall_GSR _game_state;
    protected int _attached_ball = -1;
    protected PlayerBlock _player_block = null;

    public int _lastball_x = 0;
    public int _lastball_y = 0;

    public final static int BRICK_REMOVED = 0;

    private final static int BALL_DEVIATION = 20;

    private RndGenerator _rnd = new RndGenerator(System.currentTimeMillis());

    protected GameActionListener _action_listener = null;

    public int getLastElementX()
    {
        return _lastball_x;
    }

    public int getLastElementY()
    {
        return _lastball_y;
    }

    public Wall_SB(GameActionListener listener)
    {
        _action_listener = listener;
    }

    public GameStateRecord getGameStateRecord()
    {
        return _game_state;
    }

    public void saveGameState(OutputStream outputStream) throws IOException
    {
        System.gc();
        DataOutputStream dos = new DataOutputStream(outputStream);

        dos.writeByte(_game_state._cur_level);
        dos.writeByte(_game_state._lab_num);
        dos.writeByte(_game_state._cur_attemptions);
        dos.writeInt(_game_state._summary_scores);
        dos.writeShort(_game_state._cur_scores);

        dos.writeByte(_game_state._cur_racket_state);
        dos.writeByte(_game_state._cur_racket_width);
        dos.writeByte(_game_state._racket_x);
        dos.writeByte(_game_state._racket_y);

        dos.writeBoolean(_game_state._gluemode);

        dos.writeByte(_attached_ball);

        // Saving surprises
        for(int li=0;li<_game_state._surprises.length;li++)
        {
            Surprise surp = _game_state._surprises[li];
            dos.writeBoolean(surp._active);
            dos.writeByte(surp._value);
            dos.writeByte(surp._x);
            dos.writeByte(surp._y);
        }

        // Saving current balls
        for(int li=0;li<_game_state._balls.length;li++)
        {
            Ball lb = _game_state._balls[li];
            dos.writeBoolean(lb._active);
            dos.writeByte(lb._delta_x );
            dos.writeByte(lb._delta_y);
            dos.writeBoolean(lb._moving);
            dos.writeShort(lb._x);
            dos.writeShort(lb._y);
        }

        // Saving current array state
        for(int li=0;li<_game_state._cur_array.length;li++)
        {
            dos.writeByte(_game_state._cur_array[li]);
        }

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

        _game_state = new Wall_GSR(lvl);
        _game_state.initLabyrinth(dos.readByte());

        _game_state._cur_attemptions = dos.readByte();
        _game_state._summary_scores = dos.readInt();
        _game_state._cur_scores = dos.readUnsignedShort();

        _game_state._cur_racket_state = dos.readByte();
        _game_state._cur_racket_width = dos.readByte();
        _game_state._racket_x = dos.readByte();
        _game_state._racket_y = dos.readByte();

        _game_state._gluemode = dos.readBoolean();

        _attached_ball = dos.readByte();

        // Load surprises
        for(int li=0;li<_game_state._surprises.length;li++)
        {
            Surprise surp = _game_state._surprises[li];
            surp._active =  dos.readBoolean();
            surp._value = dos.readByte();
            surp._x = dos.readByte();
            surp._y = dos.readByte();
        }

        // Load current balls
        for(int li=0;li<_game_state._balls.length;li++)
        {
            Ball lb = _game_state._balls[li];
            lb._active = dos.readBoolean();
            lb._delta_x = dos.readByte();
            lb._delta_y = dos.readByte();
            lb._moving = dos.readBoolean();
            lb._x = dos.readShort();
            lb._y = dos.readShort();
        }

        // Saving current array state
        for(int li=0;li<_game_state._cur_array.length;li++)
        {
            _game_state._cur_array [li] = dos.readByte()&0xFF;
        }

        dos = null;
        System.gc();

        if (_player_block!=null) _player_block.initPlayer();
    }

    public void newGame(int level)
    {
        _game_state = new Wall_GSR(level);
        initBallAndRacket();

        if (_player_block != null) _player_block.initPlayer();
    }

    protected void initBallAndRacket()
    {
        _game_state._gluemode = false;
        _game_state.setRacketState(Wall_SB.RACKETSTATE_NORMAL);

        _game_state._racket_x = (FIELD_WIDTH - _game_state._cur_racket_width) >> 1;
        _game_state._racket_y = FIELD_HEIGHT - RACKET_HEIGHT;

        Ball[] _balls = _game_state._balls;
        for (int li = 0; li < _balls.length; li++)
        {
            _balls[li]._active = false;
            _balls[li]._moving = false;
        }

        Ball _nball = _balls[0];
        _nball.initDelta();
        _nball._active = true;
        _nball._x = _game_state._racket_x + ((_game_state._cur_racket_width - BALL_WIDTH) >> 1);
        _nball._y = _game_state._racket_y - BALL_HEIGHT;
        _attached_ball = 0;
    }

    public void resumeGame()
    {
        _game_state._playerstate = Wall_GSR.PLAYERSTATE_NORMAL;
        initBallAndRacket();
    }

    public void nextStage(int stage)
    {
        _game_state._summary_scores += _game_state._cur_scores;
        resumeGame();
        _game_state.initLabyrinth(stage);
    }

    public void nextGameStep()
    {
        if (_player_block == null) return;
        Wall_PMR _move = (Wall_PMR) _player_block.getPlayerMoveRecord(_game_state);

        int _button = _move.getButtonValue();

        int dx = _game_state._racket_x;

        switch (_button)
        {
            case Wall_PMR.BUTTON_LEFT:
                {
                    dx -= RACKET_SPEED;
                    if (dx < 0) dx = 0;
                }
                ;
                break;
            case Wall_PMR.BUTTON_RIGHT:
                {
                    dx += RACKET_SPEED;
                    if ((dx + _game_state._cur_racket_width) >= FIELD_WIDTH) dx = FIELD_WIDTH - _game_state._cur_racket_width;
                }
                ;
                break;
            case Wall_PMR.BUTTON_UPTHROW:
                {
                    if (_attached_ball >= 0)
                    {
                        _game_state._balls[_attached_ball]._moving = true;
                        _attached_ball = -1;
                    }
                }
                ;
                break;
        }

        if (_attached_ball >=0)
        {
            int delta = dx - _game_state._racket_x;
            _game_state._balls[_attached_ball]._x += delta;
        }

        _game_state._racket_x = dx;

        // Processing of balls
        Ball[] _balls = _game_state.getBallsArray();
        for (int li = 0; li < _balls.length; li++)
        {
            Ball _aball = _balls[li];

            if ((_aball._active) && (_aball._moving))
            {
                _aball._x += _aball._delta_x;
                _aball._y += _aball._delta_y;

                // Check border
                if (_aball._x <= 0)
                {
                    _aball._x = 0;
                    _aball._delta_x = 0 - _aball._delta_x;
                }
                else if ((_aball._x + BALL_WIDTH) >= FIELD_WIDTH)
                {
                    _aball._x = FIELD_WIDTH - BALL_WIDTH - 1;
                    _aball._delta_x = 0 - _aball._delta_x;
                }

                if (_aball._y <= 0)
                {
                    _aball._y = 0;
                    _aball._delta_y = 0 - _aball._delta_y;
                }
                else if ((_aball._y + BALL_HEIGHT) >= FIELD_HEIGHT)
                {
                    _aball._active = false;
                    if (_game_state.getNoFreeBall() == null)
                    {
                        _game_state._cur_attemptions--;
                        clearSurpriseArray();
                        _game_state._playerstate = Wall_GSR.PLAYERSTATE_LOST;
                        if (_game_state._cur_attemptions <= 0)
                        {
                            _game_state._summary_scores+=_game_state._cur_scores;
                            _game_state._gamestate = Wall_GSR.GAMESTATE_OVER;
                        }
                        return;
                    }
                    else
                        continue;
                }

                // Check cells
                int side_x = 0;
                int side_y = 0;

                if (_aball._delta_x <= 0)
                {
                    side_x = _aball._x;
                    side_x = _aball._x;
                }
                else
                {
                    side_x = _aball._x + BALL_WIDTH;
                }

                if (_aball._delta_y <= 0)
                    side_y = _aball._y;
                else
                    side_y = _aball._y + BALL_HEIGHT;


                // Check the racket
                if (Math.abs(side_y - _game_state._racket_y) < Ball.MAX_DELTA)
                {
                    if ((side_x >= _game_state._racket_x) && (side_x < (_game_state._racket_x + _game_state._cur_racket_width)))
                    {
                        _aball._y = _game_state._racket_y - BALL_HEIGHT;
                        _aball._delta_y = 0 - _aball._delta_y;
                        switch (_button)
                        {
                            case Wall_PMR.BUTTON_LEFT:
                                _aball._delta_x--;
                                break;
                            case Wall_PMR.BUTTON_RIGHT:
                                _aball._delta_x++;
                                break;
                        }

                        if (_aball._delta_x < (0 - Ball.MAX_DELTA))
                            _aball._delta_x = 0 - Ball.MAX_DELTA;
                        else if (_aball._delta_x > Ball.MAX_DELTA) _aball._delta_x = Ball.MAX_DELTA;


                        if (_game_state._gluemode && (_attached_ball <0))
                        {
                            _attached_ball = li;
                            _game_state._balls[_attached_ball]._moving = false;
                        }
                    }
                }
                else
                {
                    // Check cells
                    int el = ballProcessing(_aball);

                    switch (el)
                    {
                        case Labyrinths.ELE_ELE1:
                            {
                                _game_state._cur_scores += Labyrinths.ELE_ELE1_SC;
                                _game_state.setElementAt(_lastball_x, _lastball_y, Labyrinths.ELE_EMPTY);
                                if (_action_listener!=null) _action_listener.actionEvent(BRICK_REMOVED);
                            }
                            ;
                            break;
                        case Labyrinths.ELE_ELE2:
                            {
                                _game_state._cur_scores += Labyrinths.ELE_ELE2_SC;
                                _game_state.setElementAt(_lastball_x, _lastball_y, Labyrinths.ELE_ELE1);
                                if (_action_listener!=null) _action_listener.actionEvent(BRICK_REMOVED);
                            }
                            ;
                            break;
                        case Labyrinths.ELE_ELE3:
                            {
                                _game_state._cur_scores += Labyrinths.ELE_ELE3_SC;
                                _game_state.setElementAt(_lastball_x, _lastball_y, Labyrinths.ELE_ELE2);
                                if (_action_listener!=null) _action_listener.actionEvent(BRICK_REMOVED);
                            }
                            ;
                            break;
                        case Labyrinths.ELE_SURP_BALLS:
                            {
                                _game_state._cur_scores += Labyrinths.ELE_SURP_BALLS_SC;
                                _game_state.setElementAt(_lastball_x, _lastball_y, Labyrinths.ELE_EMPTY);
                                Surprise _sur = _game_state.getFreeSurprise();
                                _sur.init(_lastball_x, _lastball_y, el);
                                if (_action_listener!=null) _action_listener.actionEvent(BRICK_REMOVED);
                            }
                            ;
                            break;
                        case Labyrinths.ELE_SURP_WIDTH:
                            {
                                _game_state._cur_scores += Labyrinths.ELE_SURP_WIDTH_SC;
                                _game_state.setElementAt(_lastball_x, _lastball_y, Labyrinths.ELE_EMPTY);
                                Surprise _sur = _game_state.getFreeSurprise();
                                _sur.init(_lastball_x, _lastball_y, el);
                                if (_action_listener!=null) _action_listener.actionEvent(BRICK_REMOVED);
                            }
                            ;
                            break;
                        case Labyrinths.ELE_SURP_RACKET:
                            {
                                _game_state._cur_scores += Labyrinths.ELE_SURP_RACKET_SC;
                                _game_state.setElementAt(_lastball_x, _lastball_y, Labyrinths.ELE_EMPTY);
                                Surprise _sur = _game_state.getFreeSurprise();
                                _sur.init(_lastball_x, _lastball_y, el);
                                if (_action_listener!=null) _action_listener.actionEvent(BRICK_REMOVED);
                            }
                            ;
                            break;
                        case Labyrinths.ELE_SURP_GLUE:
                            {
                                _game_state._cur_scores += Labyrinths.ELE_SURP_GLUE_SC;
                                _game_state.setElementAt(_lastball_x, _lastball_y, Labyrinths.ELE_EMPTY);
                                Surprise _sur = _game_state.getFreeSurprise();
                                _sur.init(_lastball_x, _lastball_y, el);
                                if (_action_listener!=null) _action_listener.actionEvent(BRICK_REMOVED);
                            }
                            ;
                            break;
                    }
                }
            }
        }

        // Processing of surprises
        processSurprises();

        if (_game_state._scores_max == _game_state._cur_scores)
        {
            _game_state._playerstate = Wall_GSR.PLAYERSTATE_WON;
        }
    }

    protected void clearSurpriseArray()
    {
        for(int li=0;li<_game_state._surprises.length;li++)
        {
            _game_state._surprises[li]._active = false;
        }
    }

    protected boolean isPointInsideBrick(int bx, int by, int x, int y)
    {
        int xx = x - bx;
        int yy = y - by;
        if ((xx > 0) && (xx < CELL_WIDTH) && (yy > 0) && (yy < CELL_HEIGHT)) return true; else return false;
    }

    protected int ballProcessing(Ball ball)
    {
        int res = Labyrinths.ELE_EMPTY;

        if (ball._delta_y > 0)
        {
            int cx = ball._x + (BALL_WIDTH >> 1);
            int cy = ball._y + BALL_HEIGHT;
            if (_game_state.getElementAtAbs(cx, cy) != Labyrinths.ELE_EMPTY)
            {
                _lastball_x = cx / CELL_WIDTH;
                _lastball_y = cy / CELL_HEIGHT;
                res = _game_state.getElementAt(_lastball_x, _lastball_y);
                ball._delta_y = 0 - ball._delta_y;

                if (_rnd.getInt(BALL_DEVIATION)==(BALL_DEVIATION>>1))
                {
                    ball._delta_y ++;
                }
            }
        }
        else
        {
            int cx = ball._x + (BALL_WIDTH >> 1);
            int cy = ball._y;
            if (_game_state.getElementAtAbs(cx, cy) != Labyrinths.ELE_EMPTY)
            {
                _lastball_x = cx / CELL_WIDTH;
                _lastball_y = cy / CELL_HEIGHT;
                res = _game_state.getElementAt(_lastball_x, _lastball_y);
                ball._delta_y = 0 - ball._delta_y;
            }

            if (_rnd.getInt(BALL_DEVIATION)==(BALL_DEVIATION>>1))
            {
                ball._delta_y --;
            }

        }

        if (ball._delta_x < 0)
        {
            int cx = ball._x;
            int cy = ball._y + (BALL_HEIGHT >> 1);
            if (_game_state.getElementAtAbs(cx, cy) != Labyrinths.ELE_EMPTY)
            {
                ball._delta_x = 0 - ball._delta_x;
                if (_rnd.getInt(BALL_DEVIATION)==(BALL_DEVIATION>>1))
                {
                    ball._delta_x ++;
                }

                _lastball_x = cx / CELL_WIDTH;
                _lastball_y = cy / CELL_HEIGHT;
                res = _game_state.getElementAt(_lastball_x, _lastball_y);
                return res;
            }
        }
        else
        {
            int cx = ball._x + BALL_WIDTH;
            int cy = ball._y + (BALL_WIDTH >> 1);
            if (_game_state.getElementAtAbs(cx, cy) != Labyrinths.ELE_EMPTY)
            {
                _lastball_x = cx / CELL_WIDTH;
                _lastball_y = cy / CELL_HEIGHT;
                res = _game_state.getElementAt(_lastball_x, _lastball_y);

                ball._delta_x = 0 - ball._delta_x;
                if (_rnd.getInt(BALL_DEVIATION)==(BALL_DEVIATION>>1))
                {
                    ball._delta_x --;
                }
            }
        }

        if(ball._delta_x >Ball.MAX_DELTA) ball._delta_x=Ball.MAX_DELTA;
        else
        if(ball._delta_x <(0-Ball.MAX_DELTA)) ball._delta_x=0-Ball.MAX_DELTA;

        if(ball._delta_y >Ball.MAX_DELTA) ball._delta_y=Ball.MAX_DELTA;
        else
        if(ball._delta_y <(0-Ball.MAX_DELTA)) ball._delta_y=0-Ball.MAX_DELTA;

        return res;
    }

    protected boolean generateNewBall()
    {
        Ball _nb = _game_state.getFreeBall();
        if (_nb == null) return false;
        Ball _nbb = _game_state.getNoFreeBall();
        _nb.init(_nbb._x, _nbb._y, 0 - _nbb._delta_x, _nbb._delta_y);
        return true;
    }

    protected void processSurprises()
    {
        Surprise[] _surp = _game_state.getSurpriseArray();
        int _prx = _game_state._racket_x;
        int _pry = _game_state._racket_y;
        for (int li = 0; li < _surp.length; li++)
        {
            Surprise _s = _surp[li];
            if (_s._active)
            {
                _s._y += SURPRISE_SPEED;

                if (_s._y >= FIELD_HEIGHT)
                {
                    _s._active = false;
                    continue;
                }

                int ddy = Math.abs(_pry - _s._y);
                if ((_s._x <= (_prx + _game_state._cur_racket_width) && ((_s._x + CELL_WIDTH) >= _prx)) && (ddy <= CELL_HEIGHT))
                {
                    switch (_s._value)
                    {
                        case Labyrinths.ELE_SURP_BALLS:
                            {
                                if (!generateNewBall())
                                {
                                    _game_state._cur_attemptions++;
                                }
                            }
                            ;
                            break;
                        case Labyrinths.ELE_SURP_RACKET:
                            {
                                _game_state._cur_attemptions++;
                            }
                            ;
                            break;
                        case Labyrinths.ELE_SURP_WIDTH:
                            {
                                _game_state.setRacketState(RACKETSTATE_LONG);
                            }
                            ;
                            break;
                        case Labyrinths.ELE_SURP_GLUE:
                            {
                                _game_state._gluemode = true;
                            }
                            ;
                            break;
                    }

                    _s._active = false;
                }
            }
        }
    }

    public void setAIBlock(PlayerBlock aiBlock)
    {
    }

    public void setPlayerBlock(PlayerBlock playerBlock)
    {
        _player_block = playerBlock;
    }

    public void unload()
    {
    }

    public boolean isLoadSaveSupporting()
    {
        return true;
    }
}
