package com.igormaznitsa.GameKit_FE652.Socoban;

import com.igormaznitsa.GameAPI.GameStateRecord;

public class Socoban_GSR implements GameStateRecord
{

    public static final int GAMESTATE_PLAYED = 0;
    public static final int GAMESTATE_OVER = 1;

    public static final int PLAYSTATE_NORMAL = 0;
    public static final int PLAYSTATE_WON = 1;
    public static final int PLAYSTATE_BREAK = 2;

    private static final int LAB_INIT_NUMBER = 0;

    protected int _game_state;
    protected int _player_state;
    protected int _player_position_x;
    protected int _player_position_y;
    protected int _player_old_position_x;
    protected int _player_old_position_y;
    protected int _player_place_position_xy;
    protected int _player_next_position_x;
    protected int _player_next_position_y;
    protected int _player_old_place_position_xy;

    protected byte[] _current_labyrinth;

    // protected int _current_direction;
    protected int _current_width;
    protected int _current_heigth;
    public int _counter_move;
    public int _prestage_counter_move;
    protected int _current_lab_num;

    public Socoban_GSR()   //конструктор
    {
        _counter_move = 0;
        _prestage_counter_move = 0;
        _current_lab_num = LAB_INIT_NUMBER;
        resumeGame();
    }

    protected void resumeGame()
    {
        _game_state = GAMESTATE_PLAYED;
        _player_state = PLAYSTATE_NORMAL;
        initLabyrinth(_current_lab_num);
    }

    public int getLabirinthNumber()
    {
        return _current_lab_num;
    }

    public int getPosition_X()       // get X coordinate
    {
        return _player_position_x;
    }

    public int getPosition_Y()       // get y coordinate
    {
        return _player_position_y;
    }

    public int getOldPosition_X()   // get X old coordinate
    {
        return _player_old_position_x;
    }

    public int getOldPosition_Y()  // get y old coordinate
    {
        return _player_old_position_y;
    }

    public int getNextPosition_X()   // get X old coordinate
    {
        return _player_next_position_x;
    }

    public int getNextPosition_Y()  // get y old coordinate
    {
        return _player_next_position_y;
    }

    public int getPlaceOnPosition()   // get place (_X,_Y)
    {
        return _player_place_position_xy;
    }

    public int getPlaceOnNextPosition()   // get place (_X,_Y)
    {
        return _player_old_place_position_xy;
    }

    public int getFields_WIDTH(int num)   // get width game's field
    {
        return Labyrinths.FIELD_WIDTH[num];
    }

    public int getFields_HEIGHT(int num)   // get height game's field
    {
        return Labyrinths.FIELD_HEIGHT[num];
    }

    public int getAIScores()       // ok
    {
        return 0;
    }

    public int getPlayerState()    // ok
    {
        return _player_state;
    }

    public int getAIState()       // ok
    {
        return 0;
    }

    public int getElementAt(int x, int y)    // ok
    {
        x += y * _current_width;
        if (x >= _current_labyrinth.length)
            return Labyrinths.IS_EMPTY;
        else
            return _current_labyrinth[x];
    }

    public int getElementAt(int x)    // ok
    {
        if (x >= _current_labyrinth.length)
            return Labyrinths.IS_EMPTY;
        else
            return _current_labyrinth[x];

    }

    public boolean setElementAt(int x, int y, int element)       // ok
    {
        x += y * _current_width;
        if (x >= _current_labyrinth.length)
            return false;
        _current_labyrinth[x] = (byte) element;
        return true;
    }

    public boolean setElementAt(int x, int element)       // ok
    {
        if (x >= _current_labyrinth.length)
            return false;
        _current_labyrinth[x] = (byte) element;
        return true;
    }

    public int getGameState()
    {
        return _game_state;
    }

    public int getPlayerScores()
    {
        return _counter_move;
    }

    protected void initLabyrinth(int number)
    {
        _current_lab_num = number;
        _current_width = Labyrinths.FIELD_WIDTH[number];
        _current_heigth = Labyrinths.FIELD_HEIGHT[number];
        _current_labyrinth = Labyrinths.getLabyrinth(number);
//        _current_labyrinth = new byte[_current_width * _current_heigth];
//        System.arraycopy(Labyrinths.LABYRINTH_ARRAY[number], 0, _current_labyrinth, 0, _current_labyrinth.length);
        int lx = 0;
        int ly = 0;
        for (int li = 0; li < _current_labyrinth.length; li++)
        {
            int _bt = _current_labyrinth[li];
            if (_bt == Labyrinths.GAMER)
            {
                _player_position_x = lx;
                _player_position_y = ly;
                _current_labyrinth[li] = 0;
            }
            lx++;
            if (lx == _current_width)
            {
                ly++;
                lx = 0;
            }
        }
        _player_old_position_x = _player_position_x;
        _player_old_position_y = _player_position_y;
        _player_next_position_x = _player_position_x;
        _player_next_position_y = _player_position_y;
        _player_place_position_xy = Labyrinths.IS_EMPTY;
        _player_old_place_position_xy = _player_place_position_xy;
    }

    public int getLevel()
    {
        return 0;
    }

    public int getStage()
    {
        return _current_lab_num;
    }

}