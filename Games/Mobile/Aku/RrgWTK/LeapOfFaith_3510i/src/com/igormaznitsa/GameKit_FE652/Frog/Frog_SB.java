
package com.igormaznitsa.GameKit_FE652.Frog;

import com.igormaznitsa.GameAPI.GameStateRecord;
import com.igormaznitsa.GameAPI.PlayerBlock;
import com.igormaznitsa.GameAPI.StrategicBlock;
import com.igormaznitsa.GameAPI.Utils.RndGenerator;

import java.io.*;

public class Frog_SB implements StrategicBlock
{
    public static final int LEVEL0 = 0;
    public static final int LEVEL0_DELAY = 170;
    public static final int LEVEL1 = 1;
    public static final int LEVEL1_DELAY = 100;
    public static final int LEVEL2= 2;
    public static final int LEVEL2_DELAY = 70;

    public static final int FIELD_WIDTH = 101;
    public static final int FIELD_HEIGHT = 64;

    public static final int PLAYER_HOR_SPEED = 3;

    public static final int PLAYER_WIDTH = 5;
    public static final int PLAYER_HEIGHT = Car.CAR_HEIGHT;

    public static final int PLAYER_SENSE_VERT_OFFSET = 2;
    public static final int PLAYER_SENSE_HORIZ_OFFSET = 2;
    public static final int PLAYER_SENSE_WIDTH = 3;
    public static final int PLAYER_SENSE_HEIGHT = 3;

    // Flow map... format [playerstartpos x,playerstartpos y,playerendposy,
    //[flowtype,flow y,flow direction,flow frequency, flow speed]]
     public static final int[][] SUBLEVELS_FLOWMAP =
            {// 1 путь
                {(FIELD_WIDTH - PLAYER_WIDTH) >> 1, Car.CAR_HEIGHT * 5, PLAYER_HEIGHT,
                 Flow.TYPE_SINGLE, Car.CAR_HEIGHT * 1, Car.DIRECT_LEFT, 3, 1,
                 Flow.TYPE_SINGLE, Car.CAR_HEIGHT * 2, Car.DIRECT_RIGHT, 3, 2,
                 Flow.TYPE_SINGLE, Car.CAR_HEIGHT * 3, Car.DIRECT_LEFT, 2, 1,
                 Flow.TYPE_SINGLE, Car.CAR_HEIGHT * 4, Car.DIRECT_RIGHT, 3, 1              // ok
                }, // 2 путь
                {
                    (FIELD_WIDTH - PLAYER_WIDTH) >> 1, Car.CAR_HEIGHT * 5, PLAYER_HEIGHT,
                    Flow.TYPE_SINGLE, Car.CAR_HEIGHT * 1, Car.DIRECT_RIGHT, 3, 2,
                    Flow.TYPE_SINGLE, Car.CAR_HEIGHT * 2, Car.DIRECT_LEFT, 2, 1,
                    Flow.TYPE_STREAM, Car.CAR_HEIGHT * 3, Car.DIRECT_RIGHT, 8, 1,
                    Flow.TYPE_SINGLE, Car.CAR_HEIGHT * 4, Car.DIRECT_LEFT, 3, 2
                }, // 3 путь
                {
                    (FIELD_WIDTH - PLAYER_WIDTH) >> 1, Car.CAR_HEIGHT * 5, PLAYER_HEIGHT,
                    Flow.TYPE_STREAM, Car.CAR_HEIGHT * 1, Car.DIRECT_RIGHT, 7, 1,
                    Flow.TYPE_SINGLE, Car.CAR_HEIGHT * 2, Car.DIRECT_RIGHT, 2, 1,
                    Flow.TYPE_STREAM, Car.CAR_HEIGHT * 3, Car.DIRECT_RIGHT, 8, 2,
                    Flow.TYPE_SINGLE, Car.CAR_HEIGHT * 4, Car.DIRECT_RIGHT, 3, 2
                }, // 4 путь
                {
                    (FIELD_WIDTH - PLAYER_WIDTH) >> 1, Car.CAR_HEIGHT * 5, PLAYER_HEIGHT,
                    Flow.TYPE_SINGLE, Car.CAR_HEIGHT * 1, Car.DIRECT_LEFT, 2, 1,
                    Flow.TYPE_STREAM, Car.CAR_HEIGHT * 2, Car.DIRECT_LEFT, 4, 1,
                    Flow.TYPE_SINGLE, Car.CAR_HEIGHT * 3, Car.DIRECT_LEFT, 6, 1,
                    Flow.TYPE_STREAM, Car.CAR_HEIGHT * 4, Car.DIRECT_LEFT, 6, 1
                }, // 5 путь
                {
                    (FIELD_WIDTH - PLAYER_WIDTH) >> 1, Car.CAR_HEIGHT * 5, PLAYER_HEIGHT,
                    Flow.TYPE_STREAM, Car.CAR_HEIGHT * 1, Car.DIRECT_LEFT, 4, 2,
                    Flow.TYPE_SINGLE, Car.CAR_HEIGHT * 2, Car.DIRECT_RIGHT, 3, 2,
                    Flow.TYPE_SINGLE, Car.CAR_HEIGHT * 3, Car.DIRECT_RIGHT, 2, 1,
                    Flow.TYPE_STREAM, Car.CAR_HEIGHT * 4, Car.DIRECT_LEFT, 5, 2
                }, // 6 путь
                {
                    (FIELD_WIDTH - PLAYER_WIDTH) >> 1, Car.CAR_HEIGHT * 5, PLAYER_HEIGHT,
                    Flow.TYPE_STREAM, Car.CAR_HEIGHT * 1, Car.DIRECT_LEFT, 4, 2,
                    Flow.TYPE_SINGLE, Car.CAR_HEIGHT * 2, Car.DIRECT_RIGHT, 1, 1,
                    Flow.TYPE_STREAM, Car.CAR_HEIGHT * 3, Car.DIRECT_LEFT, 1, 1,
                    Flow.TYPE_STREAM, Car.CAR_HEIGHT * 4, Car.DIRECT_RIGHT, 3, 1
                }, // 7 путь
                {
                    (FIELD_WIDTH - PLAYER_WIDTH) >> 1, Car.CAR_HEIGHT * 5, PLAYER_HEIGHT,
                    Flow.TYPE_STREAM, Car.CAR_HEIGHT * 1, Car.DIRECT_RIGHT, 7, 1,
                    Flow.TYPE_SINGLE, Car.CAR_HEIGHT * 2, Car.DIRECT_LEFT, 2, 1,
                    Flow.TYPE_STREAM, Car.CAR_HEIGHT * 3, Car.DIRECT_RIGHT, 6, 1,
                    Flow.TYPE_SINGLE, Car.CAR_HEIGHT * 4, Car.DIRECT_LEFT, 1, 2
                }, // 8 путь
                {
                    (FIELD_WIDTH - PLAYER_WIDTH) >> 1, Car.CAR_HEIGHT * 5, PLAYER_HEIGHT,
                    Flow.TYPE_SINGLE, Car.CAR_HEIGHT * 1, Car.DIRECT_LEFT, 2, 2,
                    Flow.TYPE_STREAM, Car.CAR_HEIGHT * 2, Car.DIRECT_LEFT, 2, 2,
                    Flow.TYPE_SINGLE, Car.CAR_HEIGHT * 3, Car.DIRECT_RIGHT, 3, 2,
                    Flow.TYPE_STREAM, Car.CAR_HEIGHT * 4, Car.DIRECT_LEFT, 5, 1,
                }, // 9 путь
                {
                    (FIELD_WIDTH - PLAYER_WIDTH) >> 1, Car.CAR_HEIGHT * 5, PLAYER_HEIGHT,
                    Flow.TYPE_STREAM, Car.CAR_HEIGHT * 1, Car.DIRECT_LEFT, 2, 1,
                    Flow.TYPE_STREAM, Car.CAR_HEIGHT * 2, Car.DIRECT_LEFT, 3, 1,
                    Flow.TYPE_STREAM, Car.CAR_HEIGHT * 3, Car.DIRECT_RIGHT, 2, 1,
                    Flow.TYPE_STREAM, Car.CAR_HEIGHT * 4, Car.DIRECT_LEFT, 1, 1
                }, // 10 путь
                {
                    (FIELD_WIDTH - PLAYER_WIDTH) >> 1, Car.CAR_HEIGHT * 5, PLAYER_HEIGHT,
                    Flow.TYPE_STREAM, Car.CAR_HEIGHT * 1, Car.DIRECT_LEFT, 5, 2,
                    Flow.TYPE_SINGLE, Car.CAR_HEIGHT * 2, Car.DIRECT_LEFT, 4, 2,
                    Flow.TYPE_SINGLE, Car.CAR_HEIGHT * 3, Car.DIRECT_RIGHT, 4, 2,
                    Flow.TYPE_STREAM, Car.CAR_HEIGHT * 4, Car.DIRECT_RIGHT, 5, 1
                }
            };

    public static final int MAX_USER_ATTEMPTION = 3;

    protected static RndGenerator _rnd = new RndGenerator(System.currentTimeMillis());
    protected Frog_GSR _game_state = null;
    protected PlayerBlock _player_block = null;

    public Frog_SB()
    {
    }

    public GameStateRecord getGameStateRecord()
    {
        return _game_state;
    }

    public void nextStage(int stage)
    {
        _game_state.setSublevel(stage);
    }

    public void saveGameState(OutputStream outputStream) throws IOException
    {
       System.gc();
       DataOutputStream dos = new DataOutputStream(outputStream);

       dos.writeByte(_game_state._level);
       dos.writeByte(_game_state._sublevel);
       dos.writeByte(_game_state._curattempt);
       dos.writeInt(_game_state._scores);

       dos.writeByte(_game_state._player_x);
       dos.writeByte(_game_state._player_y);
       dos.writeByte(_game_state._playerframe);
       dos.writeByte(_game_state._playertick);
       dos.writeByte(_game_state._playerviewstate);

       for(int li=0;li<_game_state._flowarray.length;li++)
       {
           Flow _flow = _game_state._flowarray[li];
           for(int lli=0;lli<_flow._car_array.length;lli++)
           {
               Car _cr = _flow._car_array[lli];
               dos.writeByte(_cr._direction);
               dos.writeByte(_cr._frame);
               dos.writeByte(_cr._state);
               dos.writeByte(_cr._tick);
               dos.writeByte(_cr._type);
               dos.writeByte(_cr._x);
               dos.writeByte(_cr._y);
           }
       }

       dos = null;
       System.gc();
    }

    public void loadGameState(InputStream inputStream) throws IOException
    {
        System.gc();
        DataInputStream dos = new DataInputStream(inputStream);

        int lvl = dos.readByte();
        int stg = dos.readByte();
        _game_state = null;
        System.gc();

        _game_state = new Frog_GSR(lvl);
        _game_state.setSublevel(stg);
        _game_state._curattempt = dos.readByte();
        _game_state._scores = dos.readInt();

        _game_state._player_x = dos.readByte();
        _game_state._player_y = dos.readByte();
        _game_state._playerframe = dos.readByte();
        _game_state._playertick = dos.readByte();
        _game_state._playerviewstate = dos.readByte();

        for(int li=0;li<_game_state._flowarray.length;li++)
        {
            Flow _flow = _game_state._flowarray[li];
            for(int lli=0;lli<_flow._car_array.length;lli++)
            {
                Car _cr = _flow._car_array[lli];
                _cr._direction = dos.readByte();
                _cr._frame = dos.readByte();
                _cr._state = dos.readByte();
                _cr._tick = dos.readByte();
                _cr._type = dos.readByte();
                _cr._x = dos.readByte();
                _cr._y = dos.readByte();
            }
        }

        dos = null;
        System.gc();
        if (_player_block!=null) _player_block.initPlayer();
    }

    public void newGame(int level)
    {
        _game_state = new Frog_GSR(level);

        _game_state.setSublevel(0);

        if (_player_block != null) _player_block.initPlayer();

    }

    public synchronized void nextGameStep()
    {
        if (_player_block == null) return;

        Frog_PMR _pmr = (Frog_PMR) _player_block.getPlayerMoveRecord(_game_state);

        playermove(_pmr.getButton());
        carmove();

        if (isPlayerKilled())
        {
            _game_state._curattempt--;
            _game_state._playerstate = Frog_GSR.PLAYERSTATE_LOST;
            if (_game_state._curattempt == 0)
            {
                _game_state._gamestate = Frog_GSR.GAMESTATE_OVER;
            }
            return;
        }

        if (isPlayerWon())
        {
            _game_state._scores+=50;
            _game_state._playerstate = Frog_GSR.PLAYERSTATE_WON;
        }
    }

    private boolean isPlayerWon()
    {
        int ly = _game_state._player_y + PLAYER_SENSE_VERT_OFFSET + PLAYER_SENSE_HEIGHT;
        if (ly < _game_state._player_endy) return true; else return false;
    }

    public void resumeGame()
    {
        initStage(_game_state._sublevel);
        if(_player_block!=null) _player_block.initPlayer();
    }

    public void initStage(int stage)
    {
        _game_state._gamestate = Frog_GSR.GAMESTATE_PLAYED;
        _game_state._playerstate = Frog_GSR.PLAYERSTATE_NORMAL;
        _game_state.setSublevel(stage);
    }

    protected void carmove()
    {
        Flow[] _flowarr = _game_state._flowarray;
        int indx = 0;
        for (int li = 0; li < _flowarr.length; li++)
        {
            Flow _fl = _flowarr[li];
            indx = _fl.processFlow(_game_state._viewcar, indx);
        }
        _game_state._viewcarnumber = indx;
    }

    protected void playermove(int button)
    {
        if (_game_state._playerviewstate == Frog_GSR.PLAYER_VIEW_JUMPDOWN || _game_state._playerviewstate == Frog_GSR.PLAYER_VIEW_JUMPUP)
        {
            _game_state._playertick++;
            _game_state._jumplen++;

            if (_game_state._playerviewstate == Frog_GSR.PLAYER_VIEW_JUMPDOWN)
                _game_state._player_y++;
            else
                _game_state._player_y--;

            if (_game_state._playertick >= Frog_GSR.FROG_JUMP_FRAMEDELAY)
            {
                _game_state._playerframe++;
                _game_state._playertick = 0;
                if (_game_state._jumplen == Frog_GSR.FROG_JUMP_LENGTH)
                {
                    if (_game_state._playerviewstate == Frog_GSR.PLAYER_VIEW_JUMPDOWN)
                        _game_state._playerviewstate = Frog_GSR.PLAYER_VIEW_DOWN;
                    else
                        _game_state._playerviewstate = Frog_GSR.PLAYER_VIEW_UP;

                    _game_state._playertick = 0;
                    _game_state._playerframe = 0;
                    _game_state._jumplen = 0;
                }
            }
        }
        else
        {
            switch (button)
            {
                case Frog_PMR.BUTTON_NONE:
                    ;
                    break;
                case Frog_PMR.BUTTON_LEFT:
                    {
                        int dx = _game_state._player_x;
                        dx -= PLAYER_HOR_SPEED;
                        if (dx < 0) return;
                        if (_game_state._playerviewstate != Frog_GSR.PLAYER_VIEW_LEFT)
                        {
                            _game_state.setPlayerViewState(Frog_GSR.PLAYER_VIEW_LEFT);
                        }
                        else
                        {
                            _game_state._player_x = dx;
                            animaHorPlayerStep();
                        }
                    }
                    ;
                    break;
                case Frog_PMR.BUTTON_RIGHT:
                    {
                        int dx = _game_state._player_x;
                        dx += PLAYER_HOR_SPEED;
                        if ((dx + PLAYER_WIDTH) >= FIELD_WIDTH) return;
                        if (_game_state._playerviewstate != Frog_GSR.PLAYER_VIEW_RIGHT)
                        {
                            _game_state.setPlayerViewState(Frog_GSR.PLAYER_VIEW_RIGHT);
                        }
                        else
                        {
                            _game_state._player_x = dx;
                            animaHorPlayerStep();
                        }
                    }
                    ;
                    break;
                case Frog_PMR.BUTTON_UP:
                    {
                        if (_game_state._player_y == 0) return;
                        if (_game_state._playerviewstate != Frog_GSR.PLAYER_VIEW_UP)
                            _game_state.setPlayerViewState(Frog_GSR.PLAYER_VIEW_UP);
                        else
                            _game_state.setPlayerViewState(Frog_GSR.PLAYER_VIEW_JUMPUP);
                        _game_state._scores ++;
                    }
                    ;
                    break;
                case Frog_PMR.BUTTON_DOWN:
                    {
                        if (_game_state._player_y == _game_state._initplayer_y) return;
                        if (_game_state._playerviewstate != Frog_GSR.PLAYER_VIEW_DOWN)
                            _game_state.setPlayerViewState(Frog_GSR.PLAYER_VIEW_DOWN);
                        else
                            _game_state.setPlayerViewState(Frog_GSR.PLAYER_VIEW_JUMPDOWN);
                        _game_state._scores --;
                    }
                    ;
                    break;
            }
        }
    }

    protected void animaHorPlayerStep()
    {
        _game_state._playertick++;
        if (_game_state._playertick >= Frog_GSR.FROG_MOVE_FRAMEDELAY)
        {
            _game_state._playertick = 0;
            _game_state._playerframe++;
            if (_game_state._playerframe >= Frog_GSR.FROG_MOVE_FRAMES)
            {
                _game_state._playerframe = 0;
            }
        }
    }

    private boolean isPlayerKilled()
    {
        int x1 = _game_state._player_x + PLAYER_SENSE_HORIZ_OFFSET;
        int y1 = _game_state._player_y + PLAYER_SENSE_VERT_OFFSET;

        Car[] _viscar = _game_state._viewcar;
        int _cnt = _game_state._viewcarnumber;

        for (int li = 0; li < _cnt; li++)
        {
            Car _c = _viscar[li];
            if (!((x1 + PLAYER_SENSE_WIDTH <= _c._x) || (y1 + PLAYER_SENSE_HEIGHT <= _c._y) || (x1 >= _c._x + Car.CAR_WIDTH) || (y1 >= _c._y + Car.CAR_HEIGHT))) return true;
        }
        return false;
    }

    public void setAIBlock(PlayerBlock aiBlock)
    {
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
