package com.igormaznitsa.GameKit_FE652.Treasure;

import com.igormaznitsa.GameAPI.GameStateRecord;


public class Treasure_GSR implements GameStateRecord
{
    public static final int GAMESTATE_PLAYED = 0;
    public static final int GAMESTATE_OVER = 1;

    public static final int PLAYERSTATE_NORMAL = 0;
    public static final int PLAYERSTATE_LOST = 1;
    public static final int PLAYERSTATE_WON = 2;

    public static final int MAX_ATTEMPTIONS = 3;

    protected byte [] _cur_labyrinth;

    protected Man [] _opponents = new Man[3];
    protected int _opponents_number = 0;
    public Man _player;

    protected int _doorx;
    protected int _doory;
    protected int _keyx;
    protected int _keyy;

    protected int _timedelay;

    protected int _maxscores;
    protected int _curscores;

    protected int _playerstate;
    protected int _gamestate;

    protected int _cur_attempt;
    protected int _cur_lab_number;

    protected int _summary_scores;
    protected int _level = 0;


    public int getAttemptions()
    {
        return _cur_attempt;
    }

    public int getDoorX()
    {
        return _doorx;
    }

    public int getDoorY()
    {
        return _doory;
    }

    public int getKeyX()
    {
        return _keyx;
    }

    public int getKeyY()
    {
        return _keyy;
    }

    public int getGameTimedelay()
    {
        return _timedelay;
    }

    public Treasure_GSR(int level)
    {
        _level = level;

        switch (level)
        {
            case Treasure_SB.LEVEL0:
                {
                    _timedelay = Treasure_SB.LEVEL0_TIMEDELAY;
                }
                ;
                break;
            case Treasure_SB.LEVEL1:
                {
                    _timedelay = Treasure_SB.LEVEL1_TIMEDELAY;
                }
                ;
                break;
            default :
                    _timedelay = Treasure_SB.LEVEL2_TIMEDELAY;
        }

        _summary_scores = 0;
    }

    public void setSummaryScores(int scores)
    {
        _summary_scores = scores;
    }

    public int getElementAt(int x,int y)
    {
        x = x+y*Labyrinths.FIELD_WIDTH;
        return _cur_labyrinth[x];
    }

    public void setElementAt(int x,int y,int ele)
    {
        x = x+y*Labyrinths.FIELD_WIDTH;
        _cur_labyrinth[x]=(byte)ele;
    }

    public int getCurrentLabyrinthNumber()
    {
        return _cur_lab_number;
    }

    protected void initLabyrinth(int num)
    {
        _cur_lab_number = num;

        _cur_attempt = MAX_ATTEMPTIONS;

        _cur_labyrinth = null;
        System.gc();

        _cur_labyrinth = Labyrinths.getLabyrinth(num);

        int ly = 0;
        int lx = 0;
        _opponents_number = 0;
        _curscores = 0;
        _maxscores = 0;
        for (int li =0;li<_cur_labyrinth.length;li++)
        {
            switch(_cur_labyrinth[li])
            {
                case Labyrinths.ELE_PLAYER_START :
                    {
                      _player = new Man(lx,ly);
                      _cur_labyrinth[li] = Labyrinths.ELE_EMPTY;
                    } ; break;
                case Labyrinths.ELE_ENEMY_STARTPOS :
                    {
                        _opponents [_opponents_number] = new Man(lx,ly);
                        _opponents_number++;
                        _cur_labyrinth[li] = Labyrinths.ELE_EMPTY;
                    };break;
                case Labyrinths.ELE_KEY :
                    {
                        _keyx = lx;
                        _keyy = ly;
                        _cur_labyrinth[li] = Labyrinths.ELE_EMPTY;
                    };break;
                case Labyrinths.ELE_DOOR :
                    {
                        _doorx = lx;
                        _doory = ly;
                        _cur_labyrinth[li] = Labyrinths.ELE_EMPTY;
                    };break;
                case Labyrinths.ELE_TREASURE :
                    {
                        _maxscores++;
                    }
            }

            lx++;
            if (lx == Labyrinths.FIELD_WIDTH)
            {
                lx = 0;
                ly++;
            }
        }
    }

    public Man [] getOpponentArray()
    {
        return _opponents;
    }

    public int getOpponentArraySize()
    {
        return _opponents_number;
    }

    public int getGameState()
    {
        return _gamestate;
    }

    public int getPlayerScores()
    {
        return _summary_scores;
    }

    public int getAIScores()
    {
        return 0;
    }

    public int getPlayerState()
    {
        return _playerstate;
    }

    public int getAIState()
    {
        return 0;
    }

    public int getLevel()
    {
        return _level;
    }

    public int getStage()
    {
        return _cur_lab_number;
    }
}
