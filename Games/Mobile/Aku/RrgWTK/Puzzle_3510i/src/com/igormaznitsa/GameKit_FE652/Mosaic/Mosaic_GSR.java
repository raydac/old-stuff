package com.igormaznitsa.GameKit_FE652.Mosaic;

import com.igormaznitsa.GameAPI.GameStateRecord;

/**
 * Game state object for the mosaic game
 */
public class Mosaic_GSR implements GameStateRecord
{
    /** Game is played state */
    public static final int GAME_PLAYED = 0;
    /** Game is over state */
    public static final int GAME_OVER = 1;

    /** Value of game array cell state for an empty cell */
    public static final int EMPTY_CELL = 0xFF;
    /** Value of game array cell state for an outarray cell */
    public static final int EMPTY_OUTARRAY = 0x2F;

    protected int _game_array_width;
    protected int _game_array_height;
    protected int[] _game_array;

    protected int _game_state;

    protected int _empty_x;
    protected int _empty_y;

    protected int _old_empty_x;
    protected int _old_empty_y;

    protected int _moving_counter;
    protected int _min_movings_number;

    protected int _level = 0;

    public Mosaic_GSR(int level)
    {
        _level = level;
        _game_state = GAME_PLAYED;
        int minmovenum = 0;
        int height = 0;
        int width = 0;

        switch (level)
        {
            case Mosaic_SB.LEVEL_0:
                {
                    minmovenum = Mosaic_SB.MIX_LEVEL0;
                    height = 3;
                    width = 3;
                }
                ;
                break;
            case Mosaic_SB.LEVEL_1:
                {
                    minmovenum = Mosaic_SB.MIX_LEVEL1;
                    height = 4;
                    width = 4;
                }
                ;
                break;
            case Mosaic_SB.LEVEL_2:
                {
                    minmovenum = Mosaic_SB.MIX_LEVEL2;
                    height = 5;
                    width = 5;
                }
                ;
                break;
        }


        _min_movings_number = minmovenum;

        _game_array = new int[width * height];
        _game_array_height = height;
        _game_array_width = width;

        _empty_x = -1;
        _empty_y = -1;
        _old_empty_x = -1;
        _old_empty_y = -1;

        _moving_counter = 0;
    }

    public int getEmptyCellX()
    {
        return _empty_x;
    }

    public int getEmptyCellY()
    {
        return _empty_y;
    }

    public int getGameArrayWidth()
    {
        return _game_array_width;
    }

    public int getGameArrayHeight()
    {
        return _game_array_height;
    }

    public int getOldEmptyX()
    {
        return _old_empty_x;
    }

    public int getOldEmptyY()
    {
        return _old_empty_y;
    }

    public int getElementAt(int x, int y)
    {
        if ((x < 0) || (x >= _game_array_width)) return Mosaic_GSR.EMPTY_OUTARRAY;
        if ((y < 0) || (y >= _game_array_height)) return Mosaic_GSR.EMPTY_OUTARRAY;
        return _game_array[x + y * _game_array_width];
    }

    public void setElementAt(int x, int y, int element)
    {
        _game_array[x + y * _game_array_width] = element;
    }

    public int[] getGameArray()
    {
        return _game_array;
    }

    public int getGameState()
    {
        return _game_state;
    }

    public int getPlayerScores()
    {
                                                //WARNING - thу (+10) value committed for minimal score border
        int max = _min_movings_number * 4 +10;
        int li = _moving_counter - _min_movings_number;

        if (li>=0)
            li = max - li * 2;
        else
            li = max - (_moving_counter+_min_movings_number)*2;

        if (li < 0) li = 10;
        return li;
    }

    public int getAIScores()
    {
        return 0;
    }

    public int getPlayerState()
    {
        return 0;
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
        return 0;
    }
}
