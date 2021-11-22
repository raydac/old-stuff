package com.igormaznitsa.GameKit_FE652.Snake;

import com.igormaznitsa.GameAPI.GameStateRecord;
import com.igormaznitsa.GameAPI.PlayerBlock;
import com.igormaznitsa.GameAPI.StrategicBlock;

import java.io.*;

public class Snake_SB implements StrategicBlock
{
    protected Snake_GSR _game_state;

    public static final int LEVEL0 = 0;
    public static final int LEVEL0_TIMEDELAY = 200;

    public static final int LEVEL1 = 1;
    public static final int LEVEL1_TIMEDELAY = 170;

    public static final int LEVEL2 = 2;
    public static final int LEVEL2_TIMEDELAY = 100;

    public static final int LEVEL3 = 3;
    public static final int LEVEL3_TIMEDELAY = 70;

    public static final int PLAYER_STEP = 2;
    public static final int MAX_ATTEMPTIONS = 3;

    protected PlayerBlock _player_block = null;

    public GameStateRecord getGameStateRecord()
    {
        return _game_state;
    }

    public void saveGameState(OutputStream outputStream) throws IOException
    {
        System.gc();

        DataOutputStream dos = new DataOutputStream(outputStream);

        dos.writeByte(_game_state._cur_level);
        dos.writeByte(_game_state._cur_lab_num);
        dos.writeByte(_game_state._cur_attemptions);
        dos.writeInt(_game_state._current_scores);

        dos.writeShort(_game_state._cur_mouse_counter);
        dos.writeByte(_game_state._current_direction);
        dos.writeByte(_game_state._current_tail_direction);
        dos.writeInt(_game_state._level_scores);
        dos.writeBoolean(_game_state._mice_is_eaten);
        dos.writeShort(_game_state._mice_number);
        dos.writeByte(_game_state._player_head_position_x);
        dos.writeByte(_game_state._player_head_position_y);
        dos.writeByte(_game_state._player_prev_head_position_x);
        dos.writeByte(_game_state._player_prev_head_position_y);
        dos.writeByte(_game_state._player_prev_tail_position_x);
        dos.writeByte(_game_state._player_prev_tail_position_y);
        dos.writeByte(_game_state._player_tail_position_x);
        dos.writeByte(_game_state._player_tail_position_y);
        dos.writeByte(_game_state._tail_prev_x);
        dos.writeByte(_game_state._tail_prev_y);

        for(int li=0;li<_game_state._cur_labyrinth.length;li++)
        {
            dos.writeByte(_game_state._cur_labyrinth[li]);
        }

        dos = null;
        System.gc();
    }

    public void initStage(int stage)
    {
        _game_state._game_state = Snake_GSR.GAMESTATE_PLAYED;
        _game_state._player_state = Snake_GSR.PLAYERSTATE_NORMAL;
        _game_state.initLabyrinth(stage);
    }

    public void loadGameState(InputStream inputStream) throws IOException
    {
        System.gc();

        DataInputStream dos = new DataInputStream(inputStream);

        int lvl = dos.readUnsignedByte() ;
        _game_state = null;
        System.gc();
        _game_state = new Snake_GSR(lvl);
        _game_state.initLabyrinth(dos.readByte());
        _game_state._cur_attemptions = dos.readByte();
        _game_state._current_scores = dos.readInt();

        _game_state._cur_mouse_counter=dos.readShort();
        _game_state._current_direction = dos.readByte();
        _game_state._current_tail_direction = dos.readByte();
        _game_state._level_scores = dos.readInt();
        _game_state._mice_is_eaten = dos.readBoolean();
        _game_state._mice_number = dos.readShort();
        _game_state._player_head_position_x = dos.readByte();
        _game_state._player_head_position_y = dos.readByte();
        _game_state._player_prev_head_position_x = dos.readByte();
        _game_state._player_prev_head_position_y = dos.readByte();
        _game_state._player_prev_tail_position_x = dos.readByte();
        _game_state._player_prev_tail_position_y = dos.readByte();
        _game_state._player_tail_position_x = dos.readByte();
        _game_state._player_tail_position_y = dos.readByte();
        _game_state._tail_prev_x = dos.readByte();
        _game_state._tail_prev_y = dos.readByte();

        for(int li=0;li<_game_state._cur_labyrinth.length;li++)
        {
            _game_state._cur_labyrinth[li] = dos.readByte();
        }

        dos = null;
        System.gc();

        if(_player_block!=null) _player_block.initPlayer();
    }

    public void newGame(int level)
    {
        _game_state = new Snake_GSR(level);

        if (_player_block != null) _player_block.initPlayer();
    }

    public void nextGameStep()
    {
        if (_player_block == null) return;

        Snake_PMR _record = (Snake_PMR) _player_block.getPlayerMoveRecord(_game_state);

        _game_state._mice_is_eaten = false;

        boolean okfl = true;

        int new_dir = _record.getDirect();

        int dx = 0;
        int dy = 0;

        while (okfl)
        {
            int lx = _game_state._player_head_position_x;
            int ly = _game_state._player_head_position_y;
            dx = lx;
            dy = ly;

            if ((new_dir == Snake_PMR.DIRECT_DOWN) && (_game_state._current_direction == Snake_PMR.DIRECT_UP))
                new_dir = Snake_PMR.DIRECT_NONE;
            else if ((new_dir == Snake_PMR.DIRECT_LEFT) && (_game_state._current_direction == Snake_PMR.DIRECT_RIGHT))
                new_dir = Snake_PMR.DIRECT_NONE;
            else if ((new_dir == Snake_PMR.DIRECT_UP) && (_game_state._current_direction == Snake_PMR.DIRECT_DOWN))
                new_dir = Snake_PMR.DIRECT_NONE;
            else if ((new_dir == Snake_PMR.DIRECT_RIGHT) && (_game_state._current_direction == Snake_PMR.DIRECT_LEFT)) new_dir = Snake_PMR.DIRECT_NONE;

            switch (new_dir)
            {
                case Snake_PMR.DIRECT_DOWN:
                    dy++;
                    break;
                case Snake_PMR.DIRECT_LEFT:
                    dx--;
                    break;
                case Snake_PMR.DIRECT_RIGHT:
                    dx++;
                    break;
                case Snake_PMR.DIRECT_UP:
                    dy--;
                    break;
                default :
                    new_dir = _game_state._current_direction;
            }

            int _db = _game_state.getElementAt(dx, dy);

            switch (_db)
            {
                case Labyrinths.ELE_GROUND:
                    {
                        okfl = false;
                        continue;
                    }
                    ;
                case Labyrinths.ELE_MOUSE:
                    {
                        _game_state._mice_is_eaten = true;
                        _game_state._level_scores++;
                        _game_state._cur_mouse_counter ++;

                        if (_game_state._cur_mouse_counter == _game_state._mice_number)
                        {
                            _game_state._player_state = Snake_GSR.PLAYERSTATE_WON;
                            _game_state._current_scores += _game_state._level_scores;
                            return;
                        }

                        okfl = false;
                        continue;
                    }
                    ;
                case Labyrinths.ELE_WALL:
                    {
                        if (new_dir != _game_state._current_direction)
                        {
                            new_dir = _game_state._current_direction;
                            continue;
                        }

                        switch (new_dir)
                        {
                            case Snake_PMR.DIRECT_UP:
                            case Snake_PMR.DIRECT_DOWN:
                                {
                                    if ((_game_state.getElementAt(lx - 1, ly) == Labyrinths.ELE_MOUSE) || (_game_state.getElementAt(lx - 1, ly) == Labyrinths.ELE_GROUND))
                                    {
                                        new_dir = Snake_PMR.DIRECT_LEFT;
                                    }
                                    else if ((_game_state.getElementAt(lx + 1, ly) == Labyrinths.ELE_MOUSE) || (_game_state.getElementAt(lx + 1, ly) == Labyrinths.ELE_GROUND))
                                    {
                                        new_dir = Snake_PMR.DIRECT_RIGHT;
                                    }
                                    else
                                    {
                                        _game_state._cur_attemptions --;
                                        if (_game_state._cur_attemptions <0)
                                        {
                                            _game_state._game_state = Snake_GSR.GAMESTATE_OVER;
                                        }
                                        _game_state._player_state = Snake_GSR.PLAYERSTATE_LOST;
                                        okfl=false;
                                    }
                                    continue;
                                }
                                ;
                            case Snake_PMR.DIRECT_LEFT:
                            case Snake_PMR.DIRECT_RIGHT:
                                {
                                    if ((_game_state.getElementAt(lx, ly - 1) == Labyrinths.ELE_MOUSE) || (_game_state.getElementAt(lx, ly - 1) == Labyrinths.ELE_GROUND))
                                    {
                                        new_dir = Snake_PMR.DIRECT_UP;
                                    }
                                    else if ((_game_state.getElementAt(lx, ly + 1) == Labyrinths.ELE_MOUSE) || (_game_state.getElementAt(lx, ly + 1) == Labyrinths.ELE_GROUND))
                                    {
                                        new_dir = Snake_PMR.DIRECT_DOWN;
                                    }
                                    else
                                    {
                                        _game_state._cur_attemptions --;
                                        if (_game_state._cur_attemptions <0)
                                        {
                                            _game_state._game_state = Snake_GSR.GAMESTATE_OVER;
                                        }
                                        _game_state._player_state = Snake_GSR.PLAYERSTATE_LOST;
                                        okfl = false;
                                    }
                                    continue;
                                }
                                ;
                        }
                    }
                    ;
                    break;
                default :
                    {
                        switch (_db & 0xF0)
                        {
                            case Labyrinths.ELE_SNAKE_TAIL:
                                {
                                    if (_game_state._mice_is_eaten) break;
                                }
                                ;
                            case Labyrinths.ELE_SNAKE_BODY:
                                {
                                    _game_state._cur_attemptions --;
                                    if (_game_state._cur_attemptions <0)
                                    {
                                        _game_state._game_state = Snake_GSR.GAMESTATE_OVER;
                                    }
                                        _game_state._player_state = Snake_GSR.PLAYERSTATE_LOST;
                                    return;
                                }
                                ;
                        }
                    }
            }
        }

        switch (_game_state._current_direction)
        {
            case Snake_PMR.DIRECT_DOWN:
                {
                    int lnd = 0;
                    switch (new_dir)
                    {
                        case Snake_PMR.DIRECT_DOWN: lnd = Labyrinths.ELE_SNAKE_BODY_DOWN;break;
                        case Snake_PMR.DIRECT_LEFT: lnd = Labyrinths.ELE_SNAKE_BODY_UP_LEFT;break;
                        case Snake_PMR.DIRECT_RIGHT: lnd = Labyrinths.ELE_SNAKE_BODY_UP_RIGHT;break;
                    }
                    _game_state.setElementAt(_game_state._player_head_position_x, _game_state._player_head_position_y, lnd);
                }
                ;
                break;
            case Snake_PMR.DIRECT_UP:
                {
                    int lnd=0;
                    switch (new_dir)
                    {
                        case Snake_PMR.DIRECT_UP: lnd = Labyrinths.ELE_SNAKE_BODY_UP;break;
                        case Snake_PMR.DIRECT_LEFT: lnd = Labyrinths.ELE_SNAKE_BODY_DOWN_LEFT;break;
                        case Snake_PMR.DIRECT_RIGHT: lnd = Labyrinths.ELE_SNAKE_BODY_DOWN_RIGHT;break;
                    }
                    _game_state.setElementAt(_game_state._player_head_position_x, _game_state._player_head_position_y, lnd);
                }
                ;
                break;
            case Snake_PMR.DIRECT_LEFT:
                {
                    int lnd = 0;
                    switch (new_dir)
                    {
                        case Snake_PMR.DIRECT_DOWN:lnd = Labyrinths.ELE_SNAKE_BODY_DOWN_RIGHT;break;
                        case Snake_PMR.DIRECT_UP: lnd = Labyrinths.ELE_SNAKE_BODY_UP_RIGHT;break;
                        case Snake_PMR.DIRECT_LEFT: lnd = Labyrinths.ELE_SNAKE_BODY_LEFT;break;
                    }
                    _game_state.setElementAt(_game_state._player_head_position_x, _game_state._player_head_position_y, lnd);
                }
                ;
                break;
            case Snake_PMR.DIRECT_RIGHT:
                {
                    int lnd = 0;
                    switch (new_dir)
                    {
                        case Snake_PMR.DIRECT_DOWN: lnd = Labyrinths.ELE_SNAKE_BODY_DOWN_LEFT;break;
                        case Snake_PMR.DIRECT_UP: lnd = Labyrinths.ELE_SNAKE_BODY_UP_LEFT;break;
                        case Snake_PMR.DIRECT_RIGHT:lnd = Labyrinths.ELE_SNAKE_BODY_RIGHT;break;
                    }
                    _game_state.setElementAt(_game_state._player_head_position_x, _game_state._player_head_position_y, lnd);
                }
                ;
                break;
        }

        switch (new_dir)
        {
            case Snake_PMR.DIRECT_DOWN : _game_state.setElementAt(dx, dy, Labyrinths.ELE_SNAKE_HEAD_DOWN);break;
            case Snake_PMR.DIRECT_LEFT : _game_state.setElementAt(dx, dy, Labyrinths.ELE_SNAKE_HEAD_LEFT);break;
            case Snake_PMR.DIRECT_RIGHT: _game_state.setElementAt(dx, dy, Labyrinths.ELE_SNAKE_HEAD_RIGHT);break;
            case Snake_PMR.DIRECT_UP : _game_state.setElementAt(dx, dy, Labyrinths.ELE_SNAKE_HEAD_UP);break;
        }


        _game_state._player_prev_head_position_x = _game_state._player_head_position_x;
        _game_state._player_prev_head_position_y = _game_state._player_head_position_y;

        _game_state._player_head_position_x = dx;
        _game_state._player_head_position_y = dy;

        _game_state._current_direction = new_dir;

        // Moving of the snake's tail
        if (!_game_state._mice_is_eaten)
        {
            dx = _game_state._player_tail_position_x;
            dy = _game_state._player_tail_position_y;

            switch(_game_state._current_tail_direction)
            {
                case Snake_PMR.DIRECT_DOWN :  dy++; break;
                case Snake_PMR.DIRECT_UP :  dy--;break;
                case Snake_PMR.DIRECT_LEFT :  dx--; break;
                case Snake_PMR.DIRECT_RIGHT :  dx++; break;
            }

            int elem = _game_state.getElementAt(dx,dy);
            int _tdir = 0;

            switch(elem)
            {
                case Labyrinths.ELE_SNAKE_BODY_LEFT :
                {
                    _game_state._current_tail_direction = Snake_PMR.DIRECT_LEFT;
                    _tdir = Labyrinths.ELE_SNAKE_TAIL_LEFT;
                };break;
                case Labyrinths.ELE_SNAKE_BODY_RIGHT :
                {
                    _game_state._current_tail_direction = Snake_PMR.DIRECT_RIGHT;
                    _tdir = Labyrinths.ELE_SNAKE_TAIL_RIGHT;
                };break;
                case Labyrinths.ELE_SNAKE_BODY_DOWN :
                {
                    _game_state._current_tail_direction = Snake_PMR.DIRECT_DOWN;
                    _tdir = Labyrinths.ELE_SNAKE_TAIL_DOWN;
                };break;
                case Labyrinths.ELE_SNAKE_BODY_UP :
                    {
                        _game_state._current_tail_direction = Snake_PMR.DIRECT_UP;
                        _tdir = Labyrinths.ELE_SNAKE_TAIL_UP;
                    };break;
                case Labyrinths.ELE_SNAKE_BODY_DOWN_LEFT :
                    {
                         if (_game_state._current_tail_direction == Snake_PMR.DIRECT_UP)
                         {
                             _game_state._current_tail_direction = Snake_PMR.DIRECT_LEFT;
                             _tdir = Labyrinths.ELE_SNAKE_TAIL_LEFT;
                         }
                         else
                         {
                             _game_state._current_tail_direction = Snake_PMR.DIRECT_DOWN;
                             _tdir = Labyrinths.ELE_SNAKE_TAIL_DOWN;
                         }
                    };break;
                case Labyrinths.ELE_SNAKE_BODY_DOWN_RIGHT:
                    {
                        if (_game_state._current_tail_direction == Snake_PMR.DIRECT_UP)
                        {
                            _game_state._current_tail_direction = Snake_PMR.DIRECT_RIGHT;
                            _tdir = Labyrinths.ELE_SNAKE_TAIL_RIGHT;
                        }
                        else
                        {
                            _game_state._current_tail_direction = Snake_PMR.DIRECT_DOWN;
                            _tdir = Labyrinths.ELE_SNAKE_TAIL_DOWN;
                        }
                    };break;

                case Labyrinths.ELE_SNAKE_BODY_UP_LEFT :
                    {
                        if (_game_state._current_tail_direction == Snake_PMR.DIRECT_DOWN)
                        {
                            _game_state._current_tail_direction = Snake_PMR.DIRECT_LEFT;
                            _tdir = Labyrinths.ELE_SNAKE_TAIL_LEFT;
                        }
                        else
                        {
                            _game_state._current_tail_direction = Snake_PMR.DIRECT_UP;
                            _tdir = Labyrinths.ELE_SNAKE_TAIL_UP;
                        }
                    };break;
                case Labyrinths.ELE_SNAKE_BODY_UP_RIGHT :
                    {
                        if (_game_state._current_tail_direction == Snake_PMR.DIRECT_DOWN)
                        {
                            _game_state._current_tail_direction = Snake_PMR.DIRECT_RIGHT;
                            _tdir = Labyrinths.ELE_SNAKE_TAIL_RIGHT;
                        }
                        else
                        {
                            _game_state._current_tail_direction = Snake_PMR.DIRECT_UP;
                            _tdir = Labyrinths.ELE_SNAKE_TAIL_UP;
                        }
                    };break;
                default : System.err.println("Unknown value "+elem);
            }

            _game_state.setElementAt(_game_state._player_tail_position_x,_game_state._player_tail_position_y,Labyrinths.ELE_GROUND);
            _game_state.setElementAt(dx,dy,_tdir);

            _game_state._tail_prev_x = _game_state._player_tail_position_x;
            _game_state._tail_prev_y = _game_state._player_tail_position_y;

            _game_state._player_tail_position_x = dx;
            _game_state._player_tail_position_y = dy;
        }

    }

    public void resumeGame()
    {
        if (_player_block!=null) _player_block.initPlayer();
        _game_state.resumeGame();
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
