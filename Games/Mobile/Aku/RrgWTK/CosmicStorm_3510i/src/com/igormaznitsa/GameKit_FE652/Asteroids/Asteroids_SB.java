package com.igormaznitsa.GameKit_FE652.Asteroids;

import com.igormaznitsa.GameAPI.GameStateRecord;
import com.igormaznitsa.GameAPI.PlayerBlock;
import com.igormaznitsa.GameAPI.StrategicBlock;
import com.igormaznitsa.GameAPI.GameActionListener;
import com.igormaznitsa.GameAPI.Utils.RndGenerator;

import java.io.*;

public class Asteroids_SB implements StrategicBlock
{
    public static final int LEVEL0 = 0;
    public static final int LEVEL0_ASTEROIDS = 50;
    public static final int LEVEL0_PERLINE = 3;

    public static final int LEVEL1 = 1;
    public static final int LEVEL1_ASTEROIDS = 50;
    public static final int LEVEL1_PERLINE = 8;

    public static final int LEVEL2 = 2;
    public static final int LEVEL2_ASTEROIDS = 50;
    public static final int LEVEL2_PERLINE = 10;

    public static final int ASTEROID_WIDTH = 50;
    public static final int ASTEROID_HEIGHT = 50;

    public static final int ABSFIELD_WIDTH = 300;
    public static final int ABSFIELD_HEIGHT = 300;
    public static final int ABSFIELD_RIGHTBORDER = ABSFIELD_WIDTH - ASTEROID_WIDTH;
    public static final int ABSFIELD_DOWNBORDER = ABSFIELD_HEIGHT - ASTEROID_HEIGHT;

    public static final int PLAYER_SCREEN_WIDTH = 101;
    public static final int PLAYER_SCREEN_HEIGHT = 64;

    public static final int PLAYER_CENTER_HORIZ = PLAYER_SCREEN_WIDTH >> 1;
    public static final int PLAYER_CENTER_VERT = PLAYER_SCREEN_HEIGHT >> 1;

    public static final int HIT_BORDER = 10;

    public static final int ASTEROID_STATE_NUMBER = 3;
    public static final int ASTEROID_INIT_Z = 100;
    public static final int PLANET_INIT_Z = 10000;

    public static final int PLANET_ABS_X = (ABSFIELD_WIDTH - ASTEROID_WIDTH) >> 1;
    public static final int PLANET_ABS_Y = (ABSFIELD_HEIGHT - ASTEROID_HEIGHT) >> 1;

    public static final int ASTEROIDS_SPEED = 5;
    public static final int MIN_Z_STEP = 10;

    public static final int PLAYER_HORIZ_STEP = 4;
    public static final int PLAYER_VERT_STEP = 4;

    public static final int PERSPECTIVE_COEFF = 6;
    public static final int PLANET_APPROACH_SPEED = 40;
    public static final int PLAYER_ATTEMPTION = 10;

    protected Asteroids_GSR _game_state = null;
    protected int _level = 0;
    protected PlayerBlock _player;

    protected int _harray_len = 0;
    protected int _max_z = 0;

    protected RndGenerator _rnd = new RndGenerator(System.currentTimeMillis());

    protected GameActionListener _action_listener = null;

    public static final int ACTION_HITSOUND = 0;
    public static final int ACTION_LOSTSOUND = 1;
    public static final int ACTION_WONSOUND = 2;
    public static final int ACTION_STARTSOUND = 3;

    public Asteroids_SB(GameActionListener actionListener)
    {
        _action_listener = actionListener;
    }

    public void saveGameState(OutputStream outputStream) throws IOException
    {
        System.gc();
        DataOutputStream dis = new DataOutputStream(outputStream);

        dis.writeByte(_game_state._cur_level);

        dis.writeShort(_game_state._last_hit_x);
        dis.writeShort(_game_state._last_hit_y);

        for(int li=0;li<_game_state._all_asteroids.length;li++)
        {
            Asteroid _aster = _game_state._all_asteroids[li];
            dis.writeShort(_aster._rx);
            dis.writeShort(_aster._ry);
            dis.writeShort(_aster._x);
            dis.writeShort(_aster._y);
            dis.writeShort(_aster._z);
            dis.writeByte(_aster._type);
        }

        dis.writeShort(_game_state.player_x);
        dis.writeShort(_game_state.player_y);

        dis.writeByte(_game_state._game_state) ;
        dis.writeShort(_game_state._planet_distance);

        dis.writeShort(_game_state._relative_planet_x);
        dis.writeShort(_game_state._relative_planet_y);

        dis.writeByte(_game_state._player_state) ;
        dis.writeByte(_game_state._player_attemption);

        dis.writeInt(_game_state._cur_scores);

        dis = null;
        System.gc();
    }

    public void loadGameState(InputStream inputStream) throws IOException
    {
        System.gc();
        DataInputStream dis = new DataInputStream(inputStream);

        int lvl = dis.readByte();

        _game_state = null;
        System.gc();
        _game_state = new Asteroids_GSR(lvl);

        _game_state._last_hit_x = dis.readShort();
        _game_state._last_hit_y = dis.readShort();

        for(int li=0;li<_game_state._all_asteroids.length;li++)
        {
            Asteroid _aster = _game_state._all_asteroids[li];
            _aster._rx =  dis.readShort();
            _aster._ry =  dis.readShort();
            _aster._x =  dis.readShort();
            _aster._y =  dis.readShort();
            _aster._z =  dis.readShort();
            _aster._type =  dis.readByte();
        }

        _game_state.player_x =  dis.readShort();
        _game_state.player_y =  dis.readShort();

        _game_state._game_state =  dis.readByte();
        _game_state._planet_distance  = dis.readShort();

        _game_state._relative_planet_x = dis.readShort();
        _game_state._relative_planet_y = dis.readShort();

        _game_state._player_state = dis.readByte();
        _game_state._player_attemption = dis.readByte();

        _game_state._cur_scores = dis.readInt();

        dis = null;
        System.gc();

        if (_player != null) _player.initPlayer();
    }

    public void newGame(int level)
    {
        _level = level;
        _game_state = new Asteroids_GSR(level);
        if (_player != null)
        {
            _player.initPlayer();
        }
        if (_action_listener!=null) _action_listener.actionEvent(ACTION_STARTSOUND) ;
    }

    protected synchronized void processAsteroids()
    {
        Asteroid[] _empt = _game_state._empty_asteroids;
        int emptcntr = 0;
        Asteroid[] _aster = _game_state._all_asteroids;
        int vsbl = 0;
        Asteroid[] vaster = _game_state._visible_asteroids;

        int lpcx = _game_state.player_x + PLAYER_CENTER_HORIZ;
        int lpcy = _game_state.player_y + PLAYER_CENTER_VERT;

        int lpdx = (_game_state.player_x + PLAYER_SCREEN_WIDTH) - ABSFIELD_WIDTH;
        int lpdy = (_game_state.player_y + PLAYER_SCREEN_HEIGHT) - ABSFIELD_HEIGHT;

        _max_z = 0;

        boolean _attdec = false;

        for (int li = 0; li < _aster.length; li++)
        {
            Asteroid _astra = _aster[li];
            if (_astra._type == Asteroid.TYPE_PRESENT)
            {
                int lx = _astra._x;
                int ly = _astra._y;

                if (lx <= lpdx)
                {
                    lx += ABSFIELD_WIDTH;
                }

                if (ly <= lpdy)
                {
                    ly += ABSFIELD_HEIGHT;
                }

                lx = lpcx - lx;
                ly = lpcy - ly;

                // Perspective processing
                //==============================
                int pz = (Asteroids_SB.ASTEROID_INIT_Z - _astra._z);

                int lp_x = (pz * lx) >> PERSPECTIVE_COEFF;
                int lp_y = (pz * ly) >> PERSPECTIVE_COEFF;

                //==============================
                lx += lp_x;
                ly += lp_y;

                _astra._rx = lx;
                _astra._ry = ly;

                boolean _v = false;

                if (!((lx + ASTEROID_WIDTH <= (0 - PLAYER_CENTER_HORIZ)) || (ly + ASTEROID_HEIGHT <= (0 - PLAYER_CENTER_VERT)) || (lx >= PLAYER_CENTER_HORIZ) || (ly >= PLAYER_CENTER_VERT))) _v = true;

                _astra._z -= ASTEROIDS_SPEED;
                if (_astra._z <= 0)
                {
                    _astra._type = Asteroid.TYPE_NONE;

                    if (_v)
                    {
                        if (!((lx + ASTEROID_WIDTH <= (0 - PLAYER_CENTER_HORIZ - HIT_BORDER)) || (ly + ASTEROID_HEIGHT <= (0 - PLAYER_CENTER_VERT - HIT_BORDER)) || (lx >= (PLAYER_CENTER_HORIZ - HIT_BORDER)) || (ly >= (PLAYER_CENTER_VERT - HIT_BORDER))))
                        {
                            if (!_attdec)
                            {
                                _game_state._player_attemption--;
                                _game_state._last_hit_x = _astra._rx;
                                _game_state._last_hit_y = _astra._ry;
                                _game_state._player_state = Asteroids_GSR.PLAYERSTATE_HIT;
                                if (_action_listener!=null) _action_listener.actionEvent(ACTION_HITSOUND);
                            }
                            _attdec = true;
                        }
                    }
                    _empt[emptcntr] = _astra;
                    emptcntr++;
                    _game_state._cur_scores ++;

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
        int lx = PLANET_ABS_X;
        int ly = PLANET_ABS_Y;

        if (lx <= lpdx)
        {
            lx += ABSFIELD_WIDTH;
        }

        if (ly <= lpdy)
        {
            ly += ABSFIELD_HEIGHT;
        }

        lx = lpcx - lx;
        ly = lpcy - ly;

        _game_state._relative_planet_x = lx;
        _game_state._relative_planet_y = ly;

        // Calculating of speed of approach to the planet

        int ldx = PLANET_APPROACH_SPEED;
        int ldy = PLANET_APPROACH_SPEED;

        int deltax = ((ABSFIELD_WIDTH >> 1) - lx) >> 4;
        if (deltax == 0)
            ldx = 0;
        else
            ldx = ldx / deltax;

        int deltay = ((ABSFIELD_HEIGHT >> 1) - ly) >> 4;
        if (deltay == 0)
            ldy = 0;
        else
            ldy = ldy / deltay;

        ldx = (ldx + ldy) >> 1;

        _game_state._planet_distance -= ldx;
    }

    protected void generateNewAsteroids()
    {
        // Generate
        if ((ASTEROID_INIT_Z - _max_z) > MIN_Z_STEP)
        {
            int lm = 0;
            Asteroid[] _ea = _game_state._empty_asteroids;
            for (int li = 0; li < _game_state._max_asteroids_per_line; li++)
            {
                if (lm >= _game_state._emptycount) break;
                Asteroid _astra = _ea[li];
                _astra._x = _rnd.getInt(ABSFIELD_RIGHTBORDER);
                _astra._y = _rnd.getInt(ABSFIELD_DOWNBORDER);
                _astra._z = ASTEROID_INIT_Z;
                _astra._type = Asteroid.TYPE_PRESENT;
                lm++;
                if (lm == _game_state._max_asteroids_per_line) break;
            }
        }
    }

    public void nextGameStep()
    {
        if (_player == null) return;

        Asteroids_PMR _pmr = (Asteroids_PMR) _player.getPlayerMoveRecord(_game_state);

        int _dir = _pmr.getDirect();

        _game_state._player_state = Asteroids_GSR.PLAYERSTATE_NORMAL;

        if ((_dir & Asteroids_PMR.MOVE_RIGHT) != 0)
            _game_state.player_x += PLAYER_HORIZ_STEP;
        else if ((_dir & Asteroids_PMR.MOVE_LEFT) != 0) _game_state.player_x -= PLAYER_HORIZ_STEP;

        if ((_dir & Asteroids_PMR.MOVE_DOWN) != 0)
            _game_state.player_y += PLAYER_VERT_STEP;
        else if ((_dir & Asteroids_PMR.MOVE_UP) != 0) _game_state.player_y -= PLAYER_VERT_STEP;

        if (_game_state.player_x >= ABSFIELD_WIDTH)
            _game_state.player_x = _game_state.player_x - ABSFIELD_WIDTH;
        else if (_game_state.player_x < 0) _game_state.player_x = _game_state.player_x + ABSFIELD_WIDTH;

        if (_game_state.player_y >= ABSFIELD_HEIGHT)
            _game_state.player_y = _game_state.player_y - ABSFIELD_HEIGHT;
        else if (_game_state.player_y < 0) _game_state.player_y = _game_state.player_y + ABSFIELD_HEIGHT;

        processAsteroids();

        if (_game_state._planet_distance <= 0)
        {
            _game_state._game_state = Asteroids_GSR.GAMESTATE_OVER;
            _game_state._player_state = Asteroids_GSR.PLAYERSTATE_FINISHED;

            if (_action_listener!=null) _action_listener.actionEvent(ACTION_WONSOUND);
            return;
        }

        if (_game_state._player_attemption <= 0)
        {
            _game_state._game_state = Asteroids_GSR.GAMESTATE_OVER;
            _game_state._player_state = Asteroids_GSR.PLAYERSTATE_KILLED;
            if (_action_listener!=null) _action_listener.actionEvent(ACTION_LOSTSOUND);
            return;
        }

        generateNewAsteroids();
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
}
