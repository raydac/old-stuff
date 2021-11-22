package com.igormaznitsa.GameKit_FE652.Wall;

import com.igormaznitsa.GameAPI.GameStateRecord;

public class Wall_GSR implements GameStateRecord
{
    public static final int GAMESTATE_PLAYED = 0;
    public static final int GAMESTATE_OVER = 1;

    public static final int PLAYERSTATE_NORMAL = 0;
    public static final int PLAYERSTATE_LOST = 1;
    public static final int PLAYERSTATE_WON = 2;

    protected int _cur_delay;
    protected int _cur_level;
    protected int _lab_num;

    protected int _scores_max;
    protected int _cur_scores;

    protected int _gamestate;
    protected int _playerstate;

    protected int _cur_racket_width;
    protected int _cur_racket_state;

    protected int _cur_attemptions;

    protected int[] _cur_array;
    protected Ball[] _balls;
    protected Surprise[] _surprises;

    protected int _racket_x=0;
    protected int _racket_y=0;

    protected int _summary_scores = 0;

    protected boolean _gluemode = false;

    public Wall_GSR(int level)
    {
        int delay = 0;
        switch (level)
        {
            case Wall_SB.LEVEL0:
                {
                    delay = Wall_SB.LEVEL0_DELAY;
                }
                ;
                break;
            case Wall_SB.LEVEL1:
                {
                    delay = Wall_SB.LEVEL1_DELAY;
                }
                ;
                break;
            case Wall_SB.LEVEL2:
                {
                    delay = Wall_SB.LEVEL2_DELAY;
                }
                ;
                break;
        }

        _cur_attemptions = Wall_SB.PLAYER_MAX_ATTEMPTIONS;
        _cur_delay = delay;
        _cur_level = level;
        _gamestate = GAMESTATE_PLAYED;
        _playerstate = PLAYERSTATE_NORMAL;

        _summary_scores = 0;

        initLabyrinth(0);

        _balls = new Ball[Wall_SB.BALLS_MAX_NUMBER];
        for (int li = 0; li < _balls.length; li++)
        {
            _balls[li] = new Ball(0, 0, false);
        }
    }

    public int getLevelDelay()
    {
        return _cur_delay;
    }

    public int getAttemptions()
    {
        return _cur_attemptions;
    }

    public Surprise [] getSurpriseArray()
    {
        return _surprises;
    }

    protected Surprise getFreeSurprise()
    {
        for (int li = 0; li < _surprises.length; li++)
        {
            if (!_surprises[li]._active) return _surprises[li];
        }
        return null;
    }


    protected Ball getFreeBall()
    {
        for (int li = 0; li < _balls.length; li++)
        {
            if (!_balls[li]._active) return _balls[li];
        }
        return null;
    }

    protected Ball getNoFreeBall()
    {
        for (int li = 0; li < _balls.length; li++)
        {
            if (_balls[li]._active) return _balls[li];
        }
        return null;
    }

    protected void setRacketState(int state)
    {
        _cur_racket_state = state;
        if (state == Wall_SB.RACKETSTATE_NORMAL)
        {
            _cur_racket_width = Wall_SB.RACKET_NORMAL_WIDTH;
        }
        else if (state == Wall_SB.RACKETSTATE_LONG)
        {
            _cur_racket_width = Wall_SB.RACKET_LONG_WIDTH;
        }

        if ((_racket_x+_cur_racket_width)>=Wall_SB.FIELD_WIDTH)
        {
            _racket_x = Wall_SB.FIELD_WIDTH - 1 - _cur_racket_width;
        }

    }

    public int getRacketState()
    {
      return _cur_racket_state;
    }

    protected void initLabyrinth(int num)
    {
        _lab_num = num;
        _cur_array = null;
	_cur_array = Labyrinths.getLabyrinth(num);
	//new int[Labyrinths.FIELD_WIDTH * Labyrinths.FIELD_HEIGHT];
        //System.arraycopy(Labyrinths.LABYRINTH_ARRAY[num], 0, _cur_array, 0, _cur_array.length);

        int _surp = 0;

        _scores_max = 0;
        _cur_scores = 0;

        for (int li = 0; li < _cur_array.length; li++)
        {
            int ele = _cur_array[li];

            switch (ele)
            {
                case Labyrinths.ELE_ELE1:
                    _scores_max += Labyrinths.ELE_ELE1_SC;
                    break;
                case Labyrinths.ELE_ELE2:
                    _scores_max += Labyrinths.ELE_ELE2_SC+Labyrinths.ELE_ELE1_SC;
                    break;
                case Labyrinths.ELE_ELE3:
                    _scores_max += Labyrinths.ELE_ELE3_SC+Labyrinths.ELE_ELE2_SC+Labyrinths.ELE_ELE1_SC;
                    break;
                case Labyrinths.ELE_SURP_BALLS:
                    {
                    _scores_max += Labyrinths.ELE_SURP_BALLS_SC;
                    _surp ++;
                    };break;
                case Labyrinths.ELE_SURP_WIDTH:
                    {
                        _scores_max += Labyrinths.ELE_SURP_WIDTH_SC;
                        _surp ++;
                    };
                    break;
                case Labyrinths.ELE_SURP_RACKET:
                    {
                        _scores_max += Labyrinths.ELE_SURP_RACKET_SC;
                        _surp ++;
                    };
                    break;
                case Labyrinths.ELE_SURP_GLUE:
                    {
                        _scores_max += Labyrinths.ELE_SURP_GLUE_SC;
                        _surp ++;
                    };
                    break;
            }
        }
        _surprises = new Surprise[_surp];
        for(int li=0;li<_surp;li++) _surprises [li] = new Surprise();
    }

    public Ball [] getBallsArray()
    {
        return _balls;
    }

    public int getRacketX()
    {
        return _racket_x;
    }

    public int getRacketY()
    {
        return _racket_y;
    }

    public void setElementAt(int x, int y, int elem)
    {
        _cur_array[x + y * Labyrinths.FIELD_WIDTH] = (byte) elem;
    }

    public int getElementAt(int x, int y)
    {
        return _cur_array[x + y * Labyrinths.FIELD_WIDTH];
    }

    public int getElementAtAbs(int x, int y)
    {
        return _cur_array[(x / Wall_SB.CELL_WIDTH) + (y / Wall_SB.CELL_HEIGHT) * Labyrinths.FIELD_WIDTH];
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
        return _summary_scores;
    }

    public int getLevelScores()
    {
        return _cur_scores;
    }

    public int getPlayerState()
    {
        return _playerstate;
    }

    public int getLevel()
    {
        return _cur_level;
    }

    public int getStage()
    {
        return _lab_num;
    }
}
