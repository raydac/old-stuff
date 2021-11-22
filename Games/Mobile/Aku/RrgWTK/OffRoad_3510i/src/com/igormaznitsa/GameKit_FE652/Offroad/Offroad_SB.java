package com.igormaznitsa.GameKit_FE652.Offroad;

import com.igormaznitsa.GameAPI.GameStateRecord;
import com.igormaznitsa.GameAPI.GameActionListener;
import java.util.Random;


import java.io.*;

public class Offroad_SB extends Random
{
    public static final int MOVE_NONE = 0;
    public static final int MOVE_LEFT = 1;
    public static final int MOVE_RIGHT = 2;

    public static final int LEVEL0 = 0;
    public static final int LEVEL0_OBSTACLES = 15;
    public static final int LEVEL0_PERLINE = 3;

    public static final int LEVEL1 = 1;
    public static final int LEVEL1_OBSTACLES = 20;
    public static final int LEVEL1_PERLINE = 8;

    public static final int LEVEL2 = 2;
    public static final int LEVEL2_OBSTACLES = 25;
    public static final int LEVEL2_PERLINE = 10;

    public static final int OBSTACLE_WIDTH = 40;
    public static final int OBSTACLE_HEIGHT = 80;

    public static final int PLAYER_SCREEN_WIDTH = 101;
    public static final int PLAYER_SCREEN_HEIGHT = 64;

    public static final int ABSFIELD_WIDTH = 300;
    public static final int ABSFIELD_HEIGHT = PLAYER_SCREEN_HEIGHT;
    public static final int ABSFIELD_RIGHTBORDER = ABSFIELD_WIDTH - OBSTACLE_WIDTH;

    public static final int PLAYER_CENTER_HORIZ = PLAYER_SCREEN_WIDTH >> 1;
    public static final int PLAYER_CENTER_VERT = PLAYER_SCREEN_HEIGHT >> 1;

    public static final int HIT_BORDER = 20;

    public static final int OBSTACLE_INIT_Z = 100;
    public static final int CITY_INIT_Z = 5000;
    public static final int CITY_WIDTH = 50;
    public static final int CITY_HEIGHT = PLAYER_SCREEN_HEIGHT>>1;

    public static final int CITY_ABS_X = (ABSFIELD_WIDTH - CITY_WIDTH) >> 1;
    public static final int CITY_REL_Y = (PLAYER_CENTER_VERT - CITY_HEIGHT);// >> 1;

    public static final int OBSTACLE_SPEED = 5;
    public static final int MIN_Z_STEP = 30;

    public static final int PLAYER_HORIZ_STEP = 4;

    public static final int PERSPECTIVE_COEFF = 20;
    public static final int CITY_APPROACH_SPEED = 40;
    public static final int PLAYER_ATTEMPTION = 10;

    public int i_Button;

    protected Offroad_GSR _game_state = null;
    protected int _level = 0;

    protected int _lastTreeX = 0;
    protected int _lastTreeY = 0;

    protected int _max_z = 0;

    protected GameActionListener _action_listener = null;

    public static final int ACTION_SOUNDSTART = 0;
    public static final int ACTION_SOUNDHIT = 1;
    public static final int ACTION_SOUNDWIN = 2;
    public static final int ACTION_SOUNDLOST = 3;

    public Offroad_SB(GameActionListener gameActionListener)
    {
        super(System.currentTimeMillis());
        _action_listener = gameActionListener;
    }

    public int getInt(int limit)
    {
       limit++;
       limit = (int)(((long)Math.abs(nextInt())*(long)limit)>>>31);
       return limit;
    }


    public void saveGameState(OutputStream outputStream) throws IOException
    {
        System.gc();
        DataOutputStream dos = new DataOutputStream(outputStream);

        dos.writeByte(_game_state._cur_level);

        dos.writeShort(_game_state.player_x);
        dos.writeShort(_game_state.player_y);
        dos.writeShort(_game_state._city_distance);
        dos.writeShort(_game_state._relative_city_x);
        dos.writeShort(_game_state._relative_city_y);
        dos.writeShort(_game_state._player_attemption);
        dos.writeInt(_game_state._cur_scores);

        for (int li=0;li<_game_state._all_obstacles.length;li++)
        {
            Obstacle obst = _game_state._all_obstacles[li];

            dos.writeShort(obst._rx);
            dos.writeShort(obst._ry);
            dos.writeByte(obst._type);
            dos.writeShort(obst._x);
            dos.writeShort(obst._z);
        }

        dos = null;
        System.gc();
    }

    public void loadGameState(InputStream inputStream) throws IOException
    {
        System.gc();

        DataInputStream dos = new DataInputStream(inputStream);

        int lvl =  dos.readByte();

        _game_state = null;
        System.gc();
        _game_state = new Offroad_GSR(lvl);

        _game_state.player_x = dos.readShort();
        _game_state.player_y = dos.readShort();
        _game_state._city_distance = dos.readShort();
        _game_state._relative_city_x = dos.readShort();
        _game_state._relative_city_y = dos.readShort();
        _game_state._player_attemption = dos.readShort();
        _game_state._cur_level = dos.readInt();

        for (int li=0;li<_game_state._all_obstacles.length;li++)
        {
            Obstacle obst = _game_state._all_obstacles[li];

            obst._rx = dos.readShort();
            obst._ry = dos.readShort();
            obst._type = dos.readUnsignedByte();
            obst._x = dos.readShort();
            obst._z = dos.readShort();
        }

        dos = null;
        System.gc();
    }

    public void newGame(int level)
    {
        _level = level;
        _game_state = new Offroad_GSR(level);

        if (_action_listener!=null) _action_listener.actionEvent(ACTION_SOUNDSTART);
    }

    public int getLastTreeX()
    {
        return _lastTreeX;
    }

    public int getLastTreeY()
    {
        return _lastTreeY;
    }

    protected synchronized void processObstacles()
    {
        Obstacle[] _empt = _game_state._empty_obstacles;
        int emptcntr = 0;
        Obstacle[] _aster = _game_state._all_obstacles;
        int vsbl = 0;
        Obstacle[] vaster = _game_state._visible_obstacles;

        int lpcx = _game_state.player_x + PLAYER_CENTER_HORIZ;
        int lpdx = (_game_state.player_x + PLAYER_SCREEN_WIDTH) - ABSFIELD_WIDTH;

        _max_z = 0;

        boolean _attdec = false;

        for (int li = 0; li < _aster.length; li++)
        {
            Obstacle _astra = _aster[li];
            if (_astra._type == Obstacle.TYPE_PRESENT)
            {
                int lx = _astra._x;
                int ly = 0-Offroad_SB.OBSTACLE_HEIGHT;

                if (lx <= lpdx)
                {
                    lx += ABSFIELD_WIDTH;
                }

                lx = lpcx - lx;

                // Perspective processing
                //==============================
                int pz = (Offroad_SB.OBSTACLE_INIT_Z - _astra._z);
                int lp_x = (pz * lx) / PERSPECTIVE_COEFF;
                int lp_y = pz / 3;
                //==============================
                lx += lp_x;
                ly += lp_y;

                _astra._rx = lx;
                _astra._ry = ly;

                boolean _v = false;

                if (!((lx + OBSTACLE_WIDTH <= (0 - PLAYER_CENTER_HORIZ)) || (lx >= PLAYER_CENTER_HORIZ) )) _v = true;

                _astra._z -= OBSTACLE_SPEED;
                if (_astra._z <= 0)
                {
                    _astra._type = Obstacle.TYPE_NONE;
                    _game_state._cur_scores ++;
                    if (_v)
                    {
                        if (!((lx + OBSTACLE_WIDTH <= (0 - PLAYER_CENTER_HORIZ - HIT_BORDER)) || (lx >= (PLAYER_CENTER_HORIZ - HIT_BORDER)) ))
                        {
                            if (!_attdec)
                            {
                                _game_state._player_attemption--;
                                _lastTreeX = _astra._rx;
                                _lastTreeY = _astra._ry;
                                if (_action_listener!=null) _action_listener.actionEvent(ACTION_SOUNDHIT);
                            }
                            _attdec = true;
                        }
                    }
                    _empt[emptcntr] = _astra;
                    emptcntr++;

                    continue;

                }
                else
                    if (_astra._z > _max_z) _max_z = _astra._z;

                if (_v)
                {
                    vaster[vsbl] = _astra;
                    vsbl++;
                }
            }
            else
            {
                _empt[emptcntr] = _astra;
                emptcntr++;
            }
        }
        _game_state._visiblecount = vsbl;
        _game_state._emptycount = emptcntr;

        // Calculating of the planet coordinates
        int lx = CITY_ABS_X;

        if (lx <= lpdx)
        {
            lx += ABSFIELD_WIDTH;
        }

        lx = lpcx - lx;

        _game_state._relative_city_x = lx;

        // Calculating of speed of approach to the planet

        int ldx = CITY_APPROACH_SPEED;

        int deltax = ((ABSFIELD_WIDTH >> 1) - lx) >> 4;
        if (deltax == 0)
            ldx = 0;
        else
            ldx = ldx / deltax;

        _game_state._city_distance -= ldx;
    }

    protected void generateNewObstacles()
    {
        // Generate
        if ((OBSTACLE_INIT_Z - _max_z) > MIN_Z_STEP)
        {
            int lm = 0;
            Obstacle[] _ea = _game_state._empty_obstacles;
            for (int li = 0; li < _game_state._max_obstacles_per_line; li++)
            {
                if (lm >= _game_state._emptycount) break;
                Obstacle _astra = _ea[li];
                _astra._x = getInt(ABSFIELD_RIGHTBORDER);
                _astra._z = OBSTACLE_INIT_Z;
                _astra._type = Obstacle.TYPE_PRESENT;
                lm++;
                if (lm == _game_state._max_obstacles_per_line) break;
            }
        }
    }

    public void nextGameStep()
    {
        int _dir = i_Button;

        if ((_dir & MOVE_RIGHT) != 0)
            _game_state.player_x += PLAYER_HORIZ_STEP;
        else if ((_dir & MOVE_LEFT) != 0) _game_state.player_x -= PLAYER_HORIZ_STEP;

        if (_game_state.player_x >= ABSFIELD_WIDTH)
            _game_state.player_x = _game_state.player_x - ABSFIELD_WIDTH;
        else if (_game_state.player_x < 0) _game_state.player_x = _game_state.player_x + ABSFIELD_WIDTH;

        if (_game_state.player_y >= ABSFIELD_HEIGHT)
            _game_state.player_y = _game_state.player_y - ABSFIELD_HEIGHT;
        else if (_game_state.player_y < 0) _game_state.player_y = _game_state.player_y + ABSFIELD_HEIGHT;

        processObstacles();

        if (_game_state._city_distance <= 0)
        {
            _game_state._game_state = Offroad_GSR.GAMESTATE_OVER;
            _game_state._player_state = Offroad_GSR.PLAYERSTATE_FINISHED;
            if (_action_listener!=null) _action_listener.actionEvent(ACTION_SOUNDWIN);
            return;
        }

        if (_game_state._player_attemption <= 0)
        {
            _game_state._game_state = Offroad_GSR.GAMESTATE_OVER;
            _game_state._player_state = Offroad_GSR.PLAYERSTATE_KILLED;
            if (_action_listener!=null) _action_listener.actionEvent(ACTION_SOUNDLOST);
            return;
        }

        generateNewObstacles();
    }

    public GameStateRecord getGameStateRecord()
    {
        return _game_state;
    }


    public boolean isLoadSaveSupporting()
    {
        return true;
    }
}
