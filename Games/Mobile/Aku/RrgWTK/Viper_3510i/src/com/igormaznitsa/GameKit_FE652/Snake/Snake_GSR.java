package com.igormaznitsa.GameKit_FE652.Snake;

import com.igormaznitsa.GameAPI.GameStateRecord;

public class Snake_GSR implements GameStateRecord
{
    public static final int GAMESTATE_PLAYED = 0;
    public static final int GAMESTATE_OVER = 1;

    public static final int PLAYERSTATE_NORMAL = 0;
    public static final int PLAYERSTATE_LOST = 1;
    public static final int PLAYERSTATE_WON = 2;

    public static final int MAX_ATTEMPTIONS = 3;

    private static final int LAB_INIT_NUMBER = 0;

    protected int _game_state;
    protected int _player_state;
    protected int _player_head_position_x;
    protected int _player_head_position_y;
    protected int _player_tail_position_x;
    protected int _player_tail_position_y;

    protected int _player_prev_head_position_x;
    protected int _player_prev_head_position_y;
    protected int _player_prev_tail_position_x;
    protected int _player_prev_tail_position_y;

    protected int _mice_number;
    protected int _current_direction;
    protected int _current_tail_direction;
    protected int [] _cur_labyrinth;
    protected boolean _mice_is_eaten = false;
    protected int _current_scores = 0;
    protected int _level_scores = 0;
    protected int _current_timedelay = 0;
    protected int _cur_attemptions = 0;

    protected int _tail_prev_x;
    protected int _tail_prev_y;
    protected int _cur_lab_num;
    protected int _cur_mouse_counter;
    protected int _cur_level;

    public int getLevelDelay()
    {
        return _current_timedelay;
    }

    public Snake_GSR(int level)
    {
        _cur_attemptions = Snake_SB.MAX_ATTEMPTIONS;
        _cur_level = level;
        switch (level)
        {
            case Snake_SB.LEVEL0:
                {
                    _current_timedelay = Snake_SB.LEVEL0_TIMEDELAY;
                }
                ;
                break;
            case Snake_SB.LEVEL1:
                {
                    _current_timedelay = Snake_SB.LEVEL1_TIMEDELAY;
                }
                ;
                break;
            case Snake_SB.LEVEL2:
                {
                    _current_timedelay = Snake_SB.LEVEL2_TIMEDELAY;
                }
                ;
                break;
            case Snake_SB.LEVEL3:
                {
                    _current_timedelay = Snake_SB.LEVEL3_TIMEDELAY;
                }
                ;
                break;
        }

        _cur_labyrinth = new int[Labyrinths.FIELD_WIDTH * Labyrinths.FIELD_HEIGHT];

        _cur_lab_num = LAB_INIT_NUMBER;
        resumeGame();
    }

    public int getAttemptions()
    {
        return _cur_attemptions;
    }

    protected void resumeGame()
    {
        _game_state = GAMESTATE_PLAYED;
        _player_state = PLAYERSTATE_NORMAL;

        initLabyrinth(_cur_lab_num);
    }

    public int getTailPrevX()
    {
        return _tail_prev_x;
    }

    public int getTailPrevY()
    {
        return _tail_prev_y;
    }

    public int getHeadX()
    {
        return _player_head_position_x;
    }

    public int getHeadY()
    {
        return _player_head_position_y;
    }

    public int getPrevHeadX()
    {
        return _player_prev_head_position_x;
    }

    public int getPrevHeadY()
    {
        return _player_prev_head_position_y;
    }

    public int getTailX()
    {
        return _player_tail_position_x;
    }

    public int getTailY()
    {
        return _player_tail_position_y;
    }

    protected void initLabyrinth(int number)
    {
        _cur_lab_num = number;

	_cur_labyrinth = null;
	_cur_labyrinth = Labyrinths.getLabyrinth(number);

        // System.arraycopy(Labyrinths.LABYRINTH_ARRAY[number], 0, _cur_labyrinth, 0, _cur_labyrinth.length);

        int lx = 0;
        int ly = 0;
        _mice_number = 0;
        _cur_mouse_counter = 0;
        _level_scores = 0;

        for (int li = 0; li < _cur_labyrinth.length; li++)
        {
            int _bt = _cur_labyrinth[li];
            if (_bt == Labyrinths.ELE_MOUSE)
                _mice_number++;
            else
            {
                int _bl = _bt & 0xF0;
                switch (_bl)
                {
                    case Labyrinths.ELE_SNAKE_HEAD:
                        {
                            _player_head_position_x = lx;
                            _player_head_position_y = ly;
                            switch(_bt)
                            {
                                case Labyrinths.ELE_SNAKE_HEAD_DOWN  : _current_direction = Snake_PMR.DIRECT_DOWN; break;
                                case Labyrinths.ELE_SNAKE_HEAD_LEFT  : _current_direction = Snake_PMR.DIRECT_LEFT; break;
                                case Labyrinths.ELE_SNAKE_HEAD_RIGHT : _current_direction = Snake_PMR.DIRECT_RIGHT; break;
                                case Labyrinths.ELE_SNAKE_HEAD_UP    : _current_direction = Snake_PMR.DIRECT_UP; break;
                            }
                        }
                        ;
                        break;
                    case Labyrinths.ELE_SNAKE_TAIL:
                        {
                            _player_tail_position_x = lx;
                            _player_tail_position_y = ly;
                            switch(_bt)
                            {
                                case Labyrinths.ELE_SNAKE_TAIL_DOWN : _current_tail_direction = Snake_PMR.DIRECT_DOWN; break;
                                case Labyrinths.ELE_SNAKE_TAIL_LEFT: _current_tail_direction = Snake_PMR.DIRECT_LEFT; break;
                                case Labyrinths.ELE_SNAKE_TAIL_RIGHT: _current_tail_direction = Snake_PMR.DIRECT_RIGHT; break;
                                case Labyrinths.ELE_SNAKE_TAIL_UP   : _current_tail_direction = Snake_PMR.DIRECT_UP; break;
                            }
                        }
                        ;
                        break;
                }
            }

            lx++;
            if (lx == Labyrinths.FIELD_WIDTH)
            {
                ly++;
                lx = 0;
            }
        }

        _player_prev_head_position_x = _player_head_position_x;
        _player_prev_head_position_y = _player_head_position_y;
        _player_prev_tail_position_x = _player_tail_position_x;
        _player_prev_tail_position_y = _player_tail_position_y;
    }

    public int getElementAt(int x,int y)
    {
        x += y * Labyrinths.FIELD_WIDTH;
        if (x>=_cur_labyrinth.length) return Labyrinths.ELE_EMPTY;
        else
            return _cur_labyrinth[x];
    }

    public void setElementAt(int x,int y,int elem)
    {
        x += y * Labyrinths.FIELD_WIDTH;
        _cur_labyrinth[x] = (byte) elem;
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
        return _game_state;
    }

    public int getPlayerScores()
    {
        return _current_scores;
    }

    public int getPlayerState()
    {
        return _player_state;
    }

    public int getLevel()
    {
        return _cur_level;
    }

    public int getStage()
    {
        return _cur_lab_num;
    }
}
