package com.igormaznitsa.GameKit_FE652.Rally;

import com.igormaznitsa.gameapi.GameStateRecord;
import com.igormaznitsa.gameapi.PlayerBlock;
import com.igormaznitsa.gameapi.StrategicBlock;
import com.igormaznitsa.gameapi.GameActionListener;
import com.igormaznitsa.gameapi.Utils.RndGenerator;

import java.io.*;

public class Rally_SB implements StrategicBlock
{
    public static final int ROAD_WIDTH = 96;
    public static final int ROAD_HEIGHT = 160;
    public static final int ROAD_LENGTH = 4000;

    public static final int LEVEL0 = 0;
    public static final int LEVEL1 = 1;
    public static final int LEVEL2 = 2;
    public static final int LEVEL_DEMO = 3;

    public static final int LEVEL0_OBSTACLE = 20;
    public static final int LEVEL0_MOVE_OBSTACLE = 3;
    public static final int LEVEL0_MOVING_TRIGGER = ROAD_LENGTH  - ROAD_LENGTH / 3;
    public static final int LEVEL0_SCORE_NORMAL = 2;
    public static final int LEVEL0_SCORE_MOVE = 2;
    public static final int LEVEL0_SCORE_ROAD = 2;

    public static final int LEVEL1_OBSTACLE = 30;
    public static final int LEVEL1_MOVE_OBSTACLE = 5;
    public static final int LEVEL1_MOVING_TRIGGER = ROAD_LENGTH-(ROAD_LENGTH / 4);
    public static final int LEVEL1_SCORE_NORMAL = 4;
    public static final int LEVEL1_SCORE_MOVE = 4;
    public static final int LEVEL1_SCORE_ROAD = 4;

    public static final int LEVEL2_OBSTACLE = 45;
    public static final int LEVEL2_MOVE_OBSTACLE = 8;
    public static final int LEVEL2_MOVING_TRIGGER = ROAD_LENGTH - (ROAD_LENGTH / 6);
    public static final int LEVEL2_SCORE_NORMAL = 5;
    public static final int LEVEL2_SCORE_MOVE = 5;
    public static final int LEVEL2_SCORE_ROAD = 5;

    public static final int USER_ATTEMPTION_NUMBER = 3;

    public static final int ROAD_SCROLL_SPEED = 4;
    public static final int PLAYER_SPEED = 2;
    public static final int MOVING_OBSTACLE_SPEED = -2;

    public static final int HORIZ_PLAYER_SPEED = 2;
    public static final int VERT_PLAYER_SPEED = 2;

    public static final int PLAYER_CELL_WIDTH = 13;
    public static final int PLAYER_CELL_HEIGHT = 22;

    public static final int OBSTACLE_CELL_WIDTH = 13;
    public static final int OBSTACLE_CELL_HEIGHT = 22;

    private static final int VIRTUAL_CELL_WIDTH = OBSTACLE_CELL_WIDTH + 3;
    protected static final int DOWN_MOVE_BORDER = ROAD_HEIGHT - PLAYER_CELL_HEIGHT;

    public static final int START_X_POSITION = (ROAD_WIDTH - PLAYER_CELL_WIDTH) >> 1;
    public static final int START_Y_POSITION = (ROAD_HEIGHT - PLAYER_CELL_HEIGHT) >> 1;

    public static final int OBSTACLES_PER_LINE = ROAD_WIDTH / VIRTUAL_CELL_WIDTH;

    public static final int TURNOFF_OBSTACLES_TRIGGER = ROAD_HEIGHT * 2;

    protected Rally_GSR _game_state;

    protected int _movearray_len;
    protected Obstacle[] _movearray;
    protected int _emptarray_len;
    protected Obstacle[] _emptarray;
    protected int _normalarray_len;
    protected Obstacle[] _normalarray;

    protected int _move_obstacles;

    protected RndGenerator _rnd;
    protected PlayerBlock _player;

    protected int _game_level;

    protected int _harray_len = 0;
    protected int _larray_len = 0;

    protected GameActionListener _action_listener = null;

    public static final int ACTION_SOUNDSTART = 0;
    public static final int ACTION_SOUNDLOST = 1;
    public static final int ACTION_SOUNDWIN = 2;
    public static final int ACTION_SOUNDCOLLIDE = 3;

    public Rally_SB(GameActionListener actionListener)
    {
        _action_listener = actionListener;
        _rnd = new RndGenerator(System.currentTimeMillis());
    }

    protected boolean isPlayerOutField()
    {
        if ((_game_state.getPlayerX() < 0) || ((_game_state.getPlayerX() + PLAYER_CELL_WIDTH) >= ROAD_WIDTH)) return true;
        return false;
    }

    protected Obstacle isCollision(Obstacle obst)
    {
        if (obst == null)
        {
            // Check player's collisions
            int lx0 = (_game_state._player_x / VIRTUAL_CELL_WIDTH) * VIRTUAL_CELL_WIDTH;
            int lx1 = ((_game_state._player_x + Rally_SB.PLAYER_CELL_WIDTH) / VIRTUAL_CELL_WIDTH) * VIRTUAL_CELL_WIDTH;

            // Check for firing and normal obstacles
            for (int li = 0; li < _normalarray_len; li++)
            {
                Obstacle _ob = _normalarray[li];
                if ((_ob._x == lx0) || (_ob._x == lx1))
                {
                    int ly = Math.abs(_game_state._player_y - _ob._y);
                    int lx = Math.abs(_game_state._player_x - _ob._x);
                    if ((ly < OBSTACLE_CELL_HEIGHT) && (lx < OBSTACLE_CELL_WIDTH)) return _ob;
                }
            }

            // Check for firing and normal obstacles
            for (int li = 0; li < _movearray_len; li++)
            {
                Obstacle _ob = _movearray[li];
                if ((_ob._x == lx0) || (_ob._x == lx1))
                {
                    int ly = Math.abs(_game_state._player_y - _ob._y);
                    int lx = Math.abs(_game_state._player_x - _ob._x);
                    if ((ly < OBSTACLE_CELL_HEIGHT) && (lx < OBSTACLE_CELL_WIDTH)) return _ob;
                }
            }

            return null;
        }
        else
        {
            // Check moving obstacles collisions
            int lx = obst._x;
            int ly = obst._y;
            for (int li = 0; li < _normalarray_len; li++)
            {
                Obstacle _ob = _normalarray[li];
                if (_ob._x == lx)
                {
                    int lly = _ob._y - ly;
                    if (Math.abs(lly) <= OBSTACLE_CELL_HEIGHT) return _ob;
                }
            }
        }
        return null;
    }

    protected void clearAllArrays()
    {
        _emptarray_len = 0;
        _movearray_len = 0;
        _normalarray_len = 0;

        Obstacle[] _obst = _game_state.getObstacleArray();
        for (int li = 0; li < _obst.length; li++)
        {
            _obst[li]._type = Obstacle.NONE;
        }
    }

    protected void fillObsArrays()
    {
        // Creating of the list of top and empty obstacles
        _emptarray_len = 0;
        _movearray_len = 0;
        _harray_len = 0;
        _larray_len = 0;
        _normalarray_len = 0;

        Obstacle[] _arr = _game_state.getObstacleArray();
        for (int li = 0; li < _arr.length; li++)
        {
            Obstacle _obj = _arr[li];

            switch (_obj._type)
            {
                case Obstacle.NONE:
                    {
                        _emptarray[_emptarray_len] = _obj;
                        _emptarray_len++;
                    }
                    ;
                    break;
                case Obstacle.MOVING_CAR:
                    {
                        if (_obj._y >= (ROAD_HEIGHT -OBSTACLE_CELL_HEIGHT))
                        {
                            _larray_len++;
                        }
                        _movearray[_movearray_len] = _obj;
                        _movearray_len++;
                    }
                    ;
                    break;
                default :
                    {
                        if (_obj._y < OBSTACLE_CELL_HEIGHT )
                        {
                            _harray_len++;
                        }
                        _normalarray[_normalarray_len] = _obj;
                        _normalarray_len++;
                    }
            }
        }
    }

    public void resumeGame()
    {
        clearAllArrays();
        _game_state._player_x = Rally_SB.START_X_POSITION;
        _game_state._player_y = Rally_SB.START_Y_POSITION;
        _game_state._player_state = Rally_GSR.PLAYER_NORMAL;
    }

    protected void generateNewObstacles()
    {
        // Generate normal obstactles
        if (_harray_len == 0)
        {
            int lo = 0;
            boolean ust = false;
            for (int li = 0; li < OBSTACLES_PER_LINE; li++)
            {
                if (lo == _emptarray_len) break;
                if (ust)
                {
                    ust = false;
                    continue;
                }
                int lid = _rnd.getInt(20);
                if (lid == 10)
                {
                    _emptarray[lo]._type = Obstacle.NORMAL_CAR;
                    _emptarray[lo]._x = li * VIRTUAL_CELL_WIDTH;
                    _emptarray[lo]._y = 0 - OBSTACLE_CELL_HEIGHT;
                    _emptarray[lo]._speed = PLAYER_SPEED;
                    lo++;
                    ust = true;
                }
            }
        }
        // Generate moving obstactles
        if (_move_obstacles == _game_state._max_moving_obstacles) return;

        if (_game_state.getCurrentRoadPosition() < _game_state._move_trigger)
        {
            if (_larray_len == 0)
            {

                Obstacle lemptyo = _emptarray[0];
                int lx = _rnd.getInt(30);
                if (lx == 15)
                {
                    lx = _rnd.getInt(OBSTACLES_PER_LINE - 1);
                    lemptyo._type = Obstacle.MOVING_CAR;
                    lemptyo._x = lx * VIRTUAL_CELL_WIDTH;
                    lemptyo._y = ROAD_HEIGHT;
                    lemptyo._speed = MOVING_OBSTACLE_SPEED;
                    _move_obstacles++;
                }
            }
        }
    }

    protected void processObstacles()
    {
        Obstacle[] arr = _game_state.getObstacleArray();
        for (int li = 0; li < arr.length; li++)
        {
            Obstacle obs = arr[li];
            switch (obs._type)
            {
                case Obstacle.FIRING_CAR:
                    ;
                case Obstacle.FIRING_MOVED_CAR:
                    ;
                case Obstacle.NORMAL_CAR:
                    {
                        obs._y += obs._speed;
                        if (obs._y >= ROAD_HEIGHT)
                        {
                            obs._type = Obstacle.NONE;
                            _game_state.normal_counter++;
                        }
                    }
                    ;
                    break;
                case Obstacle.MOVING_CAR:
                    {
                        obs._y += obs._speed;
                        Obstacle _col = isCollision(obs);
                        if (_col != null)
                        {
                            _col._speed = ROAD_SCROLL_SPEED;
			    if(_col._type==Obstacle.NORMAL_CAR) _col._type = Obstacle.FIRING_CAR;
			       else _col._type = Obstacle.FIRING_MOVED_CAR;
                            obs._speed = ROAD_SCROLL_SPEED;
			    if(obs._type==Obstacle.NORMAL_CAR) obs._type = Obstacle.FIRING_CAR;
			       else obs._type = Obstacle.FIRING_MOVED_CAR;
                            _game_state.move_counter++;
                            _move_obstacles--;
                        }
                        else
                        {
                            if (obs._y <= (-OBSTACLE_CELL_HEIGHT))
                            {
                                obs._type = Obstacle.NONE;
                                _move_obstacles--;
                                _game_state.move_counter++;
                            }
                        }
                    }
                    ;
                    break;
            }
        }
    }

    public void newGame(int level)
    {
        _game_state = new Rally_GSR(level);
        _game_level = level;
        _movearray = new Obstacle[_game_state.getObstacleArray().length];
        _emptarray = new Obstacle[_game_state.getObstacleArray().length];
        _normalarray = new Obstacle[_game_state.getObstacleArray().length];

        if (_player != null) _player.initPlayer();

        if (_action_listener!=null) _action_listener.actionEvent(ACTION_SOUNDSTART);
    }

    public void saveGameState(OutputStream outputStream) throws IOException
    {
        System.gc();
        DataOutputStream dis = new DataOutputStream(outputStream);

        // Saving the game level value
        dis.writeByte(_game_level);

        // Saving obstacles from stream
        Obstacle[] arra = _game_state.getObstacleArray();
        for (int li = 0; li < arra.length; li++)
        {
            dis.writeByte(arra[li]._x);
            dis.writeByte(arra[li]._y);
            dis.writeByte(arra[li]._type);
            dis.writeByte(arra[li]._speed);
        }

        // Saving of current number of player's attemptions
        dis.writeByte(_game_state._current_user_attemption);

        // Saving of current player's state
        dis.writeByte(_game_state._player_state);

        // Saving of current player's X coordinate
        dis.writeByte(_game_state._player_x);

        // Saving of current player's Y coordinate
        dis.writeByte(_game_state._player_y);

        // Saving of current player's road position
        dis.writeInt(_game_state._current_road_position);

        // Loading of current game state
        dis.writeByte(_game_state._game_state);
        dis = null;
        System.gc();
    }

    public void loadGameState(InputStream inputStream) throws IOException
    {
        System.gc();
        DataInputStream dis = new DataInputStream(inputStream);

        // Load the game level value
        _game_level = dis.readByte();
        newGame(_game_level);
        // Loading obstacles from stream
        Obstacle[] arra = _game_state.getObstacleArray();
        for (int li = 0; li < arra.length; li++)
        {
            arra[li]._x = dis.readUnsignedByte();
            arra[li]._y = dis.readUnsignedByte();
            arra[li]._type = dis.readUnsignedByte();
            arra[li]._speed = dis.readByte();
        }

        // Loading of current number of player's attemptions
        _game_state._current_user_attemption = dis.readByte();

        // Loading of current player's state
        _game_state._player_state = dis.readByte();

        // Loading of current player's X coordinate
        _game_state._player_x = dis.readByte();

        // Loading of current player's Y coordinate
        _game_state._player_y = dis.readByte();

        // Loading of current player's road position
        _game_state._current_road_position = dis.readInt();

        // Loading of current game state
        _game_state._game_state = dis.readByte();

        fillObsArrays();
        dis = null;
        System.gc();
    }

    public void nextGameStep()
    {
        if (_player == null) return;
        Rally_PMR _record = (Rally_PMR) _player.getPlayerMoveRecord(_game_state);

        int ldir = _record.getDirect();
        if ((ldir & Rally_PMR.DIRECT_LEFT) != 0)
            _game_state._player_x -= HORIZ_PLAYER_SPEED;
        else if ((ldir & Rally_PMR.DIRECT_RIGHT) != 0) _game_state._player_x += HORIZ_PLAYER_SPEED;

        if ((ldir & Rally_PMR.DIRECT_UP) != 0)
        {
            int ly = _game_state._player_y;
            ly -= VERT_PLAYER_SPEED;
            if (ly < 0) _game_state._player_y = 0; else _game_state._player_y = ly;
        }
        else if ((ldir & Rally_PMR.DIRECT_DOWN) != 0)
        {
            int ly = _game_state._player_y;
            ly += VERT_PLAYER_SPEED;
            if (ly > DOWN_MOVE_BORDER) _game_state._player_y = DOWN_MOVE_BORDER; else _game_state._player_y = ly;
        }

        if (_game_level == LEVEL_DEMO) return;

        processObstacles();

        fillObsArrays();

        if (isPlayerOutField())
        {
            _game_state._player_state = Rally_GSR.PLAYER_OUTFIELD;
            _game_state._current_user_attemption--;
            if (_game_state._current_user_attemption < 0)
            {
                _game_state._game_state = Rally_GSR.GAMESTATE_OVER;
                if (_action_listener!=null) _action_listener.actionEvent(ACTION_SOUNDLOST);
            }
            else
            {
                if (_action_listener!=null) _action_listener.actionEvent(ACTION_SOUNDCOLLIDE);
            }
            return;
        }

        Obstacle _colobj = isCollision(null);
        if (_colobj != null)
        {
            _game_state._player_state = Rally_GSR.PLAYER_COLLIDED;
            _game_state._current_user_attemption--;
            if (_game_state._current_user_attemption < 0) _game_state._game_state = Rally_GSR.GAMESTATE_OVER;
            if (_action_listener!=null) _action_listener.actionEvent(ACTION_SOUNDCOLLIDE);
            return;
        }

        _game_state._current_road_position--;

        if (_game_state._current_road_position <= 0)
        {
            _game_state._player_state = Rally_GSR.PLAYER_FINISHED;
            _game_state._game_state = Rally_GSR.GAMESTATE_OVER;
            if (_action_listener!=null) _action_listener.actionEvent(ACTION_SOUNDWIN);
        }

        if (_game_state._current_road_position > TURNOFF_OBSTACLES_TRIGGER) generateNewObstacles();
    }

    public GameStateRecord getGameStateRecord()
    {
        return _game_state;
    }

    public void setPlayerBlock(PlayerBlock playerBlock)
    {
        _player = playerBlock;
    }

    public void setAIBlock(PlayerBlock aiBlock)
    {
    }

    public boolean isLoadSaveSupporting()
    {
        return true;
    }
    public int getMaxSizeOfSavedGameBlock()
    {
        return 800;
    }
}
