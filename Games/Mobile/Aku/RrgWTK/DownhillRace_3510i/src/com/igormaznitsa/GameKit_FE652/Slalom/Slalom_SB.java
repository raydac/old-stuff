package com.igormaznitsa.GameKit_FE652.Slalom;

import com.igormaznitsa.GameAPI.GameStateRecord;
import com.igormaznitsa.GameAPI.PlayerBlock;
import com.igormaznitsa.GameAPI.StrategicBlock;
import com.igormaznitsa.GameAPI.GameActionListener;
import com.igormaznitsa.GameAPI.Utils.RndGenerator;

import java.io.*;

public class Slalom_SB implements StrategicBlock
{
    public static final int ROAD_WIDTH = 101;
    public static final int ROAD_HEIGHT = 64;
    public static final int ROAD_LENGTH = 3000;

    public static final int LEVEL0 = 0;
    public static final int LEVEL1 = 1;
    public static final int LEVEL2 = 2;
    public static final int LEVEL_DEMO = 3;

    public static final int LEVEL0_OBSTACLE = 20;
    public static final int LEVEL0_MOVE_OBSTACLE = 3;
    public static final int LEVEL0_MOVING_TRIGGER = ROAD_LENGTH - (ROAD_LENGTH / 3);
    public static final int LEVEL0_SCORE_NORMAL = 2;
    public static final int LEVEL0_SCORE_MOVE = 2;
    public static final int LEVEL0_SCORE_ROAD = 2;

    public static final int LEVEL1_OBSTACLE = 40;
    public static final int LEVEL1_MOVE_OBSTACLE = 8;
    public static final int LEVEL1_MOVING_TRIGGER = ROAD_LENGTH - (ROAD_LENGTH /4);
    public static final int LEVEL1_SCORE_NORMAL = 4;
    public static final int LEVEL1_SCORE_MOVE = 4;
    public static final int LEVEL1_SCORE_ROAD = 4;

    public static final int LEVEL2_OBSTACLE = 55;
    public static final int LEVEL2_MOVE_OBSTACLE = 10;
    public static final int LEVEL2_MOVING_TRIGGER = ROAD_LENGTH -  (ROAD_LENGTH / 6);
    public static final int LEVEL2_SCORE_NORMAL = 5;
    public static final int LEVEL2_SCORE_MOVE = 5;
    public static final int LEVEL2_SCORE_ROAD = 5;

    public static final int USER_ATTEMPTION_NUMBER = 3;

    public static final int ROAD_SCROLL_SPEED = -4;
    public static final int PLAYER_SPEED = -2;
    public static final int MOVING_OBSTACLE_SPEED = 2;

    public static final int HORIZ_PLAYER_SPEED = 2;

    public static final int PLAYER_IMG_WIDTH = 15;
    public static final int PLAYER_IMG_HEIGHT = 15;

    public static final int PLAYER_OFF_X = 4;
    public static final int PLAYER_OFF_Y = 6;
    public static final int PLAYER_CELL_WIDTH = 7;
    public static final int PLAYER_CELL_HEIGHT = 7;

    public static final int OBSTACLE_IMG_WIDTH = 15;
    public static final int OBSTACLE_IMG_HEIGHT = 15;

    public static final int OBSTACLE_OFF_X = 4;
    public static final int OBSTACLE_OFF_Y = 7;
    public static final int OBSTACLE_CELL_WIDTH = 7;
    public static final int OBSTACLE_CELL_HEIGHT = 7;

    private static final int VIRTUAL_CELL_WIDTH = OBSTACLE_IMG_WIDTH;
    protected static final int DOWN_BORDER = ROAD_HEIGHT - (PLAYER_IMG_HEIGHT + (PLAYER_IMG_HEIGHT >>1));

    public static final int START_X_POSITION = (ROAD_WIDTH - PLAYER_IMG_WIDTH) >> 1;
    public static final int START_Y_POSITION = (ROAD_HEIGHT - PLAYER_IMG_HEIGHT) >> 1;;
    private static final int PLAYER_RIGHT_BORDER = ROAD_WIDTH - PLAYER_IMG_WIDTH;

    public static final int OBSTACLES_PER_LINE = (ROAD_WIDTH+VIRTUAL_CELL_WIDTH-1) / VIRTUAL_CELL_WIDTH;
    public static final int TURNOFF_OBSTACLES_TRIGGER = ROAD_HEIGHT * 2;

    protected Slalom_GSR _game_state;

    protected int _skierarray_len;
    protected Obstacle[] _skierrray;
    protected int _emptarray_len;
    protected Obstacle[] _emptarray;
    protected int _flagarray_len;
    protected Obstacle[] _flagrray;

    protected int _skier_obstacles;
    protected int _current_player_y;

    protected RndGenerator _rnd;
    protected PlayerBlock _player;

    protected int _game_level;
    protected int _harray_len = 0;
    protected int _larray_len = 0;

    protected GameActionListener _action_listener = null;

    public static final int ACTION_SOUNDSTART = 0;
    public static final int ACTION_SOUNDHIT = 1;
    public static final int ACTION_SOUNDWIN = 2;
    public static final int ACTION_SOUNDLOST = 3;

    public Slalom_SB(GameActionListener actionListener)
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
            int dx = _game_state._player_x + PLAYER_OFF_X;
            int dy = _game_state._player_y + PLAYER_OFF_Y;

            // Check player's collisions
            int lx0 = (dx/ VIRTUAL_CELL_WIDTH) * VIRTUAL_CELL_WIDTH;
            int lx1 = ((dx+PLAYER_CELL_WIDTH + Slalom_SB.PLAYER_CELL_WIDTH) / VIRTUAL_CELL_WIDTH) * VIRTUAL_CELL_WIDTH;

            // Check for trees
            for (int li = 0; li < _flagarray_len; li++)
            {
                Obstacle _ob = _flagrray[li];
                if ((_ob._x == lx0) || (_ob._x == lx1))
                {
                    int ly = Math.abs(dy - (_ob._y+OBSTACLE_OFF_Y));
                    int lx = Math.abs(dx - (_ob._x+OBSTACLE_OFF_X));
                    if ((ly <= OBSTACLE_CELL_HEIGHT) && (lx <= OBSTACLE_CELL_WIDTH)) return _ob;
                }
            }

            // Check for other skiers
            for (int li = 0; li < _skierarray_len; li++)
            {
                Obstacle _ob = _skierrray[li];
                if ((_ob._x == lx0) || (_ob._x == lx1))
                {
                    int ly = Math.abs(dy - (_ob._y+OBSTACLE_OFF_Y));
                    int lx = Math.abs(dx - (_ob._x+OBSTACLE_OFF_X));
                    if ((ly < OBSTACLE_CELL_HEIGHT) && (lx < OBSTACLE_CELL_WIDTH)) return _ob;
                }
            }

            return null;
        }
        else
        {
            // Check moving obstacles collisions
            int lx = obst._x;
            int ly = obst._y+OBSTACLE_OFF_Y;
            for (int li = 0; li < _flagarray_len; li++)
            {
                Obstacle _ob = _flagrray[li];
                if (_ob._x == lx)
                {
                    int lly = (_ob._y+OBSTACLE_OFF_Y) - ly;
                    if (Math.abs(lly) <= OBSTACLE_CELL_HEIGHT) return _ob;
                }
            }
        }
        return null;
    }

    protected void clearAllArrays()
    {
        _emptarray_len = 0;
        _skierarray_len = 0;
        _flagarray_len = 0;

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
        _skierarray_len = 0;
        _harray_len = 0;
        _larray_len = 0;
        _flagarray_len = 0;

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
                case Obstacle.DROPPED_SKIER:
                    ;
                case Obstacle.NORMAL_SKIER:
                    {
                        if (_obj._y < (OBSTACLE_IMG_HEIGHT << 1))
                        {
                            _larray_len++;
                        }
                        _skierrray[_skierarray_len] = _obj;
                        _skierarray_len++;
                    }
                    ;
                    break;
                default :
                    {
                        if (_obj._y >= DOWN_BORDER)
                        {
                            _harray_len++;
                        }
                        _flagrray[_flagarray_len] = _obj;
                        _flagarray_len++;
                    }
            }

        }
    }

    public void resumeGame()
    {
        clearAllArrays();
        _game_state._player_x = Slalom_SB.START_X_POSITION;
        _game_state._player_y = Slalom_SB.START_Y_POSITION;
        _game_state._player_state = Slalom_GSR.PLAYER_NORMAL;
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
                int lid = _rnd.getInt(8);
                if (lid == 4)
                {
                    _emptarray[lo]._type = Obstacle.FLAG;
                    _emptarray[lo]._x = li * VIRTUAL_CELL_WIDTH;
                    _emptarray[lo]._y = ROAD_HEIGHT;
                    _emptarray[lo]._speed = PLAYER_SPEED;
                    lo++;
                    ust = true;
                }
            }
        }
        // Generate moving obstactles
        if (_skier_obstacles == _game_state._max_skiers) return;

        if (_game_state.getCurrentRoadPosition() < _game_state._move_trigger)
        {
            if (_larray_len == 0)
            {
                Obstacle lemptyo = _emptarray[0];
                int lx = _rnd.getInt(20);
                if (lx == 15)
                {
                    lx = _rnd.getInt(OBSTACLES_PER_LINE - 1);
                    lemptyo._type = Obstacle.NORMAL_SKIER;
                    lemptyo._x = lx * VIRTUAL_CELL_WIDTH;
                    lemptyo._y = -OBSTACLE_IMG_HEIGHT;
                    lemptyo._speed = MOVING_OBSTACLE_SPEED;
                    _skier_obstacles++;
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
                case Obstacle.SKIER_ON_FLAG:
                    ;
                case Obstacle.FLAG:
                    {
                        obs._y += obs._speed;
                        if (obs._y <= -OBSTACLE_IMG_HEIGHT)
                        {
                            obs._type = Obstacle.NONE;
                            _game_state.normal_counter++;
                        }
                    }
                    ;
                    break;
                case Obstacle.DROPPED_SKIER:
                    ;
                case Obstacle.NORMAL_SKIER:
                    {
                        obs._y += obs._speed;
                        Obstacle _col = isCollision(obs);
                        if (_col != null)
                        {
                            int lk = _rnd.getInt(6);
                            if (lk != 3)
                            {
                                _col._type = Obstacle.SKIER_ON_FLAG;
                                _col._speed = PLAYER_SPEED;
                                obs._type = Obstacle.NONE;
                                _game_state.move_counter++;
                                _skier_obstacles--;
                            }
                            else
                            {
                                obs._type = Obstacle.DROPPED_SKIER;
                                _col._type = Obstacle.NONE;
                                _game_state.move_counter++;
                            }
                        }
                        else
                        {
                            if (obs._y >= ROAD_HEIGHT)
                            {
                                obs._type = Obstacle.NONE;
                                _skier_obstacles--;
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
        _game_state = new Slalom_GSR(level);
        _game_level = level;
        _skierrray = new Obstacle[_game_state.getObstacleArray().length];
        _emptarray = new Obstacle[_game_state.getObstacleArray().length];
        _flagrray = new Obstacle[_game_state.getObstacleArray().length];

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
        _game_state._current_user_attemption = dis.readUnsignedByte();

        // Loading of current player's state
        _game_state._player_state = dis.readUnsignedByte();

        // Loading of current player's X coordinate
        _game_state._player_x = dis.readUnsignedByte();

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
        Slalom_PMR _record = (Slalom_PMR) _player.getPlayerMoveRecord(_game_state);

        switch (_record.getDirect())
        {
            case Slalom_PMR.DIRECT_LEFT:
                {
                    _game_state._player_x -= HORIZ_PLAYER_SPEED;
                    _game_state._player_state = Slalom_GSR.PLAYER_LEFT;
                    if (_game_state._player_x < 0)
                    {
                        _game_state._player_state = Slalom_GSR.PLAYER_NORMAL;
                        _game_state._player_x = 0;
                    }
                }
                ;
                break;
            case Slalom_PMR.DIRECT_RIGHT:
                {
                    _game_state._player_x += HORIZ_PLAYER_SPEED;
                    _game_state._player_state = Slalom_GSR.PLAYER_RIGHT;
                    if (_game_state._player_x > Slalom_SB.PLAYER_RIGHT_BORDER)
                    {
                        _game_state._player_state = Slalom_GSR.PLAYER_NORMAL;
                        _game_state._player_x = Slalom_SB.PLAYER_RIGHT_BORDER;
                    }
                };break;
        }

        if (_game_level == LEVEL_DEMO) return;

        processObstacles();

        fillObsArrays();

        Obstacle _colobj = isCollision(null);
        if (_colobj != null)
        {
            _game_state._player_state = Slalom_GSR.PLAYER_COLLIDED;
            _game_state._current_user_attemption--;
            if (_game_state._current_user_attemption < 0) {
            _game_state._game_state = Slalom_GSR.GAMESTATE_OVER;
            if (_action_listener!=null) _action_listener.actionEvent(ACTION_SOUNDLOST);
            }
            if (_action_listener!=null) _action_listener.actionEvent(ACTION_SOUNDHIT);
            return;
        }

        _game_state._current_road_position--;

        if (_game_state._current_road_position <= 0)
        {
            _game_state._player_state = Slalom_GSR.PLAYER_FINISHED;
            _game_state._game_state = Slalom_GSR.GAMESTATE_OVER;
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
    public String getGameID()
    {
        return "Slalom";
    }

    public int getMaxSizeOfSavedGameBlock()
    {
        return 800;
    }

}
