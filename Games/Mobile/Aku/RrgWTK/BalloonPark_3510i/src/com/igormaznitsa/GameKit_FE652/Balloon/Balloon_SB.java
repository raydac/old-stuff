package com.igormaznitsa.GameKit_FE652.Balloon;

import com.igormaznitsa.GameAPI.GameStateRecord;
import com.igormaznitsa.GameAPI.PlayerBlock;
import com.igormaznitsa.GameAPI.StrategicBlock;
import com.igormaznitsa.GameAPI.GameActionListener;
import com.igormaznitsa.GameAPI.Utils.RndGenerator;

import java.io.*;

public class Balloon_SB implements StrategicBlock
{
    public static final int MAX_GAS_LEVEL = 10000;

    public static final int LEVEL0 = 0;
    public static final int LEVEL0_FLOWS = 5;
    public static final int LEVEL0_GAS_LEVEL = 1000;
    public static final boolean LEVEL0_GROUNDPRESENT = true;

    public static final int LEVEL1 = 1;
    public static final int LEVEL1_FLOWS = 7;
    public static final int LEVEL1_GAS_LEVEL = 900;
    public static final boolean LEVEL1_GROUNDPRESENT = false;

    public static final int LEVEL2 = 2;
    public static final int LEVEL2_FLOWS = 9;
    public static final int LEVEL2_GAS_LEVEL = 600;
    public static final boolean LEVEL2_GROUNDPRESENT = false;

    public static final int FIELD_WIDTH = 101;
    public static final int FIELD_HEIGHT = 62;

    public static final int FLOWMARKER_WIDTH = 15;
    public static final int FLOWMARKER_HEIGHT = 5;

    public static final int PLACE_WIDTH = 14;
    public static final int PLACE_HEIGHT = 4;
    public static final int PLACE_X = 45;
    public static final int PLACE_Y = FIELD_HEIGHT - PLACE_HEIGHT;
    public static final int BALLOON_HEIGHT = 15;
    public static final int BALLOON_SAIL_OFFSET = BALLOON_HEIGHT >> 1;
    public static final int BALLOON_TOP_WIDTH = 12;
    public static final int BALLOON_BOTTOM_WIDTH = 4;
    public static final int BALLOON_BOTTOM_OFFSET = 4;

    public static final int FLOW_MAXSPEED = 3;

    public static final int FLOWAREA_HEIGHT = FIELD_HEIGHT - PLACE_HEIGHT - BALLOON_HEIGHT - BALLOON_SAIL_OFFSET;

    private static final int SPEED_INCREASING_8 = 0x00000020;
    private static final int SPEED_DECREASING_8 = 0x00000010;

    protected Balloon_GSR _game_state = null;
    protected static RndGenerator _rnd = new RndGenerator(System.currentTimeMillis());
    protected PlayerBlock _player_block = null;
    protected static final int DEATH_SPEED = -(0x1A0);
    protected int _flow_height = 0;

    protected GameActionListener _action_listener = null;

    public static final int ACTION_SOUNDSTARTGAME = 0;
    public static final int ACTION_SOUNDBURNERON = 1;
    public static final int ACTION_SOUNDBURNEROFF = 2;
    public static final int ACTION_SOUNDLOST = 3;
    public static final int ACTION_SOUNDWIN = 4;

    public Balloon_SB(GameActionListener gameActionListener)
    {
        _action_listener = gameActionListener;
    }

    public GameStateRecord getGameStateRecord()
    {
        return _game_state;
    }

    public void init()
    {
    }

    public void saveGameState(OutputStream outputStream) throws IOException
    {
        System.gc();
        DataOutputStream dos = new DataOutputStream(outputStream);

        dos.writeByte(_game_state._level);
        dos.writeShort(_game_state._player_x);
        dos.writeShort(_game_state._player_y);
        dos.writeInt(_game_state._player_speed);
        dos.writeShort(_game_state._cur_gas_level);

        dos = null;
        System.gc();
    }

    public void loadGameState(InputStream inputStream) throws IOException
    {
        System.gc();
        DataInputStream dos = new DataInputStream(inputStream);

        int lvl = dos.readByte();
        int px = dos.readShort();
        int py = dos.readShort();

        _game_state = null;
        System.gc();

        _game_state = new Balloon_GSR(lvl,px,py);
        _game_state._player_speed = dos.readInt();
        _game_state._cur_gas_level =  dos.readShort();

        dos = null;
        System.gc();

        if (_player_block!=null) _player_block.initPlayer();
    }

    public void newGame(int level)
    {
        int lx = 0;
        int ly = 0;
        _game_state = new Balloon_GSR(level, lx, ly);

        if (_player_block != null) _player_block.initPlayer();

        if (_action_listener!=null) _action_listener.actionEvent(ACTION_SOUNDSTARTGAME);
    }

    public void nextGameStep()
    {
        if (_player_block == null) return;

        Balloon_PMR _player_move = (Balloon_PMR) _player_block.getPlayerMoveRecord(_game_state);
        switch (_player_move.getState())
        {
            case Balloon_PMR.BURNER_OFF:
                {
                    _game_state._player_speed -= SPEED_DECREASING_8;
                    _game_state._player_state = Balloon_GSR.PLAYER_BURNINGOFF;
                    if (_action_listener!=null) _action_listener.actionEvent(ACTION_SOUNDBURNEROFF);
                }
                ;
                break;
            case Balloon_PMR.BURNER_ON:
                {
                    if (_game_state._cur_gas_level > 0)
                    {
                        _game_state._player_speed += SPEED_INCREASING_8;
                        _game_state._cur_gas_level--;
                        _game_state._player_state = Balloon_GSR.PLAYER_BURNINGON;
                        if (_action_listener!=null) _action_listener.actionEvent(ACTION_SOUNDBURNERON);
                    }
                    else
                    {
                        _game_state._player_speed -= SPEED_DECREASING_8;
                        _game_state._player_state = Balloon_GSR.PLAYER_BURNINGOFF;
                        if (_action_listener!=null) _action_listener.actionEvent(ACTION_SOUNDBURNEROFF);
                    }
                }
                ;
                break;
        }

        int dx = _game_state._player_x;
        int dy = _game_state._player_y;

        // Calculating of the number of an air flow where the balloon is at present
        int ln = dy / _game_state._airflow_step;
        AirFlow[] _af = _game_state.getFlowArray();
        if (ln < _af.length)
        {
            dx += _af[ln]._speed;
            if (dx >= (FIELD_WIDTH - BALLOON_TOP_WIDTH)) dx = FIELD_WIDTH - BALLOON_TOP_WIDTH;
            if (dx < 0) dx = 0;
        }
        dy = ((dy << 8) - _game_state._player_speed) >> 8;

        if (dy < 0)
        {
            dy = 0;
            _game_state._player_speed = 0;
        }

        if (((dx + BALLOON_BOTTOM_OFFSET) >= PLACE_X) && ((dx + BALLOON_BOTTOM_OFFSET) <= ((PLACE_X + PLACE_WIDTH) - BALLOON_BOTTOM_WIDTH)))
        {
            // the Balloon over the place
            if (dy >= (FIELD_HEIGHT - BALLOON_HEIGHT - PLACE_HEIGHT))
            {
                dy = FIELD_HEIGHT - BALLOON_HEIGHT - PLACE_HEIGHT;
                if (_game_state._player_speed < DEATH_SPEED)
                {
                    _game_state._player_state = Balloon_GSR.PLAYER_DEAD;
                    if (_action_listener!=null) _action_listener.actionEvent(ACTION_SOUNDLOST);
                }
                else
                {
                    _game_state._player_state = Balloon_GSR.PLAYER_FINISHED;
                    if (_action_listener!=null) _action_listener.actionEvent(ACTION_SOUNDWIN);
                }
                _game_state._game_state = Balloon_GSR.GAME_OVER;
            }
        }
        else
        {
            // the balloon is not over the place
            if (dy >= (FIELD_HEIGHT - BALLOON_HEIGHT))
            {
                dy = FIELD_HEIGHT - BALLOON_HEIGHT;
                if ((_game_state._player_speed < DEATH_SPEED) || (!_game_state._ground_present))
                {
                    _game_state._player_state = Balloon_GSR.PLAYER_DEAD;
                    _game_state._game_state = Balloon_GSR.GAME_OVER;
                    if (_action_listener!=null) _action_listener.actionEvent(ACTION_SOUNDLOST);
                }
                else if (_game_state._cur_gas_level == 0)
                {
                    _game_state._player_state = Balloon_GSR.PLAYER_GAS_IS_ALL;
                    _game_state._game_state = Balloon_GSR.GAME_OVER;
                    if (_action_listener!=null) _action_listener.actionEvent(ACTION_SOUNDBURNEROFF);
                }

                _game_state._player_speed = 0;
            }
        }

        _game_state._player_x = dx;
        _game_state._player_y = dy;

        processAirFlows();
    }

    protected void processAirFlows()
    {
        AirFlow[] _arr = _game_state.getFlowArray();
        for (int li = 0; li < _arr.length; li++)
        {
            AirFlow af = _arr[li];
            af._markerx += af._speed;
            if (af._markerx <= (0 - Balloon_SB.FLOWMARKER_WIDTH))
            {
                af._markerx = Balloon_SB.FIELD_WIDTH;
            }
            else if (af._markerx >= Balloon_SB.FIELD_WIDTH)
            {
                af._markerx = 0 - Balloon_SB.FLOWMARKER_WIDTH;
            }
        }
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
