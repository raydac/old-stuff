
package com.igormaznitsa.GameKit_FE652.Treasure;

import com.igormaznitsa.GameAPI.GameStateRecord;

import java.io.*;

public class Treasure_SB
{

    public static final int DIRECT_NONE = 0;
    public static final int DIRECT_UP = 1;
    public static final int DIRECT_DOWN = 2;
    public static final int DIRECT_LEFT = 3;
    public static final int DIRECT_RIGHT = 4;


    public static final int LEVEL0 = 0;
    public static final int LEVEL0_TIMEDELAY = 120;

    public static final int LEVEL1 = 1;
    public static final int LEVEL1_TIMEDELAY = 100;

    public static final int LEVEL2 = 2;
    public static final int LEVEL2_TIMEDELAY = 70;

    public static final int VIRTUAL_CELL_WIDTH = 10;
    public static final int VIRTUAL_CELL_HEIGHT = 10;

    protected Treasure_GSR _game_state = null;

    public static final int PLAYER_SPEED = 0x200;
    public static final int ENEMY_SPEED = 0x150;

    public int i_Button = DIRECT_NONE;
    protected boolean _is_changed = false;
    protected int _changed_x = 0;
    protected int _changed_y = 0;
    protected boolean _door_state_changed = false;
    protected boolean _key_state_changed = false;

    public boolean isDoorChanged()
    {
        return _door_state_changed;
    }

    public boolean isKeyChanged()
    {
        return _key_state_changed;
    }

    public int getChangedX()
    {
        return _changed_x;
    }

    public int getChangedY()
    {
        return _changed_y;
    }

    public boolean isChanged()
    {
        return _is_changed;
    }

    public Treasure_SB()
    {
    }

    public void saveGameState(OutputStream outputStream) throws IOException
    {
        System.gc();
        DataOutputStream dos = new DataOutputStream(outputStream);

        dos.writeByte(_game_state._level);
        dos.writeByte(_game_state._cur_lab_number);
        dos.writeByte(_game_state._cur_attempt);
        dos.writeInt(_game_state._summary_scores);
        dos.writeShort(_game_state._curscores);

        dos.writeByte(_game_state._player._cur_state);
        dos.writeByte(_game_state._player._ticks);
        dos.writeShort((_game_state._player._x)>>8);
        dos.writeShort((_game_state._player._y)>>8);

        for (int li=0;li<_game_state._opponents_number;li++)
        {
            Man _mn = _game_state._opponents[li];
            dos.writeByte(_mn._cur_state);
            dos.writeByte(_mn._ticks);
            dos.writeShort(_mn.getX());
            dos.writeShort(_mn.getY());
        }

        for(int li=0;li<_game_state._cur_labyrinth.length;li++)
        {
            dos.writeByte(_game_state._cur_labyrinth[li]);
        }

        dos = null;
        System.gc();
    }

    public void loadGameState(InputStream inputStream) throws IOException
    {
        System.gc();
        DataInputStream dos = new DataInputStream(inputStream);

        int lvl = dos.readByte();
        _game_state = null;
        System.gc();
        _game_state = new Treasure_GSR(lvl);
        _game_state.initLabyrinth(dos.readUnsignedByte());

        _game_state._cur_attempt = dos.readByte();
        _game_state._summary_scores = dos.readInt();

        _game_state._curscores = dos.readUnsignedShort();

        _game_state._player._cur_state = dos.readUnsignedByte();
        _game_state._player._ticks = dos.readByte();
        _game_state._player._x = dos.readShort()<<8;
        _game_state._player._y = dos.readShort()<<8;

        for (int li=0;li<_game_state._opponents_number;li++)
        {
            Man _mn = _game_state._opponents[li];
            _mn._cur_state = dos.readUnsignedByte();
            _mn._ticks = dos.readByte();
            _mn._x = dos.readShort()<<8;
            _mn._y = dos.readShort()<<8;
        }

        for(int li=0;li<_game_state._cur_labyrinth.length;li++)
        {
            _game_state._cur_labyrinth[li] = dos.readByte();
        }

        dos = null;
        Runtime.getRuntime().gc();
    }

    public void newGame(int level)
    {
        _game_state = new Treasure_GSR(level);
    }

    public void resumeGame()
    {
        _game_state._playerstate = Treasure_GSR.PLAYERSTATE_NORMAL;
        _game_state._player.init();
        initAllKillers();
    }

    public void initStage(int stage)
    {
        _game_state._playerstate = Treasure_GSR.PLAYERSTATE_NORMAL;
        _game_state.initLabyrinth(stage);
        _is_changed = false;
        _door_state_changed = false;
        _key_state_changed = false;
    }

    public void nextGameStep()
    {

        // Process player
        Man _player = _game_state._player;
        if (processMan(_player, i_Button, PLAYER_SPEED))
        {
            _game_state._cur_attempt--;
            _game_state._playerstate = Treasure_GSR.PLAYERSTATE_LOST;
            if (_game_state._cur_attempt == 0)
            {
                _game_state._gamestate = Treasure_GSR.GAMESTATE_OVER;
            }
            return;
        }

        // Process killers
        Man[] _killers = _game_state._opponents;
        int _kn = _game_state._opponents_number;
        for (int li = 0; li < _kn; li++)
        {
            Man akiller = _killers[li];
            int direct = DIRECT_NONE;

            int dx = _player.getX() - akiller.getX();
            int dy = _player.getY() - akiller.getY();

            int akx = (akiller.getX() + (VIRTUAL_CELL_WIDTH >> 1)) / VIRTUAL_CELL_WIDTH;
            int aky = (akiller.getY() + (VIRTUAL_CELL_HEIGHT >> 1)) / VIRTUAL_CELL_HEIGHT;

            if ((dy > 0) && (dy>=(VIRTUAL_CELL_HEIGHT>>1))&&(_game_state.getElementAt(akx, aky + 1) == Labyrinths.ELE_STAIRS))
            {
                direct = DIRECT_DOWN;
            }
            else if ((dy < 0) && (dy<=(0-(VIRTUAL_CELL_HEIGHT>>1))) && (_game_state.getElementAt(akx, aky) == Labyrinths.ELE_STAIRS))
            {
                direct = DIRECT_UP;
            }
            else if (direct == DIRECT_NONE)
            {
                if (dx > 0)
                {
                    direct = DIRECT_RIGHT;
                }
                else if (dx < 0)
                {
                    direct = DIRECT_LEFT;
                }
            }

            if (processMan(akiller, direct, ENEMY_SPEED))
            {
                akiller.init();
            }
        }

        // Check killers and player
        for (int li = 0; li < _kn; li++)
        {
            Man kl = _killers[li];
            int ldx = Math.abs((kl.getX() + (VIRTUAL_CELL_WIDTH >> 1) - (_player.getX() + (VIRTUAL_CELL_WIDTH >> 1))));
            int ldy = Math.abs((kl.getY() + (VIRTUAL_CELL_HEIGHT >> 1) - (_player.getY() + (VIRTUAL_CELL_HEIGHT >> 1))));
            if (ldx <= (VIRTUAL_CELL_WIDTH >> 1) && ldy <= (VIRTUAL_CELL_HEIGHT >> 1))
            {
                _game_state._cur_attempt--;
                _game_state._playerstate = Treasure_GSR.PLAYERSTATE_LOST;

                if (_game_state._cur_attempt == 0)
                {
                    _game_state._gamestate = Treasure_GSR.GAMESTATE_OVER;
                    _game_state.setSummaryScores(_game_state._summary_scores + _game_state._curscores);
                }
                return;
            }
        }

        // Check treasure, key and door
        int lx = (_player.getX()+(VIRTUAL_CELL_WIDTH>>1)) / VIRTUAL_CELL_WIDTH;
        int ly = (_player.getY()+(VIRTUAL_CELL_HEIGHT>>1)) / VIRTUAL_CELL_WIDTH;

        int el = _game_state.getElementAt(lx, ly);

        _is_changed = false;
        _key_state_changed = false;
        _door_state_changed = false;

        switch (el)
        {
            case Labyrinths.ELE_TREASURE:
                {
                    _is_changed = true;
                    _changed_x = lx;
                    _changed_y = ly;
                    _game_state._curscores++;
                    _game_state.setElementAt(lx, ly, Labyrinths.ELE_EMPTY);

                    if (_game_state._maxscores == _game_state._curscores)
                    {
                        _key_state_changed = true;
                        _game_state.setElementAt(_game_state._keyx, _game_state._keyy, Labyrinths.ELE_KEY);
                    }
                }
                ;
                break;
            case Labyrinths.ELE_KEY:
                {
                    _is_changed = true;
                    _changed_x = lx;
                    _changed_y = ly;
                    _game_state.setElementAt(lx, ly, Labyrinths.ELE_EMPTY);

                    _key_state_changed = true;
                    _door_state_changed = true;
                    _game_state.setElementAt(_game_state._doorx, _game_state._doory, Labyrinths.ELE_DOOR);
                }
                ;
                break;
            case Labyrinths.ELE_DOOR:
                {
                    _game_state.setSummaryScores(_game_state._summary_scores + _game_state._curscores);
                    _game_state._playerstate = Treasure_GSR.PLAYERSTATE_WON;
                }
                ;
                break;
        }
    }

    protected void initAllKillers()
    {
        Man[] _killers = _game_state._opponents;
        int _kn = _game_state._opponents_number;
        for (int li = 0; li < _kn; li++)
        {
            _killers[li].init();
        }
    }

    protected boolean processMan(Man man, int direct, int spd)
    {
        if ((man._cur_state&0xF0) == Man.APPEARANCE_STATE)
        {
            man._ticks++;
            if (man._ticks >= Man.APPEARANCE_TICK_DELAY)
            {
                man._ticks = 0;
                int lcs = man._cur_state & 0x0F;
                lcs++;
                if (lcs >= Man.APPEARANCE_FRAMES)
                {
                    man._cur_state = Man.MOVE_UPDOWN_STATE;
                    man._ticks = 0;
                    return false;
                }
                else
                {
                    man._cur_state = Man.APPEARANCE_STATE | lcs;
                }
            }
            return false;
        }

        int dx = man._x;
        int dy = man._y;
        int ddx = man.getX();
        int ddy = man.getY();
        int ldx = (ddx + (VIRTUAL_CELL_WIDTH >> 1)) / VIRTUAL_CELL_WIDTH;
        int ldy = ddy / VIRTUAL_CELL_HEIGHT;

        int uel = _game_state.getElementAt(ldx, ldy + 1);
        int uel2 = _game_state.getElementAt(ldx, ldy);

        ldy = (ddy + (VIRTUAL_CELL_HEIGHT >> 1)) / VIRTUAL_CELL_HEIGHT;

        boolean _anim = true;

        if (uel2 == Labyrinths.ELE_WATER) return true;
        if ((uel2 != Labyrinths.ELE_STAIRS) && (uel != Labyrinths.ELE_WALL) && (uel != Labyrinths.ELE_STAIRS))
        {
            man._y += spd;
            man._x = (ldx * VIRTUAL_CELL_WIDTH) << 8;

            if (man._cur_state != Man.MOVE_UPDOWN_STATE)
            {
                man._cur_state = Man.MOVE_UPDOWN_STATE;
                man._ticks = 0;
            }
        }
        else
        {
            int _new_dir = man._cur_state & 0xF0;

            switch (direct)
            {
                case DIRECT_DOWN:
                    {
                        dx = (ldx * VIRTUAL_CELL_WIDTH) << 8;

                        dy += spd;
                        ddy = dy>>8;

                        ldy = ddy / VIRTUAL_CELL_HEIGHT;

                        if (_game_state.getElementAt(ldx, ldy + 1) == Labyrinths.ELE_WALL)
                        {
                            dy = (ldy * VIRTUAL_CELL_HEIGHT) << 8;
                            _anim = false;
                        }

                        _new_dir = Man.MOVE_UPDOWN_STATE;
                    }
                    ;
                    break;
                case DIRECT_UP:
                    {
                        if ((_game_state.getElementAt(ldx, (ddy + VIRTUAL_CELL_HEIGHT - 1) / VIRTUAL_CELL_HEIGHT) != Labyrinths.ELE_STAIRS)
                        && (_game_state.getElementAt(ldx, (ddy - 1) / VIRTUAL_CELL_HEIGHT) != Labyrinths.ELE_STAIRS)) break;

                        dx = (ldx * VIRTUAL_CELL_WIDTH) << 8;
                        dy -= spd;
                        ddy = dy >>8;

                        ldy = (ddy + VIRTUAL_CELL_HEIGHT - 1) / VIRTUAL_CELL_HEIGHT;

                        if (_game_state.getElementAt(ldx, ldy - 1) == Labyrinths.ELE_WALL)
                        {
                            dy = (ldy * VIRTUAL_CELL_HEIGHT) << 8;
                            _anim = false;
                        }

                        _new_dir = Man.MOVE_UPDOWN_STATE;
                    }
                    ;
                    break;
                case DIRECT_LEFT:
                    {
                        dy = (ldy * VIRTUAL_CELL_HEIGHT) << 8;
                        dx -= spd;

                        ddx = dx >> 8;

                        ldx = ddx / VIRTUAL_CELL_WIDTH;

                        if (_game_state.getElementAt(ldx, ldy) == Labyrinths.ELE_WALL)
                        {
                            dx = ((ldx + 1) * VIRTUAL_CELL_WIDTH) << 8;
                            _anim = false;
                        }
                        _new_dir = Man.MOVE_LEFT_STATE;
                    }
                    ;
                    break;
                case DIRECT_RIGHT:
                    {
                        dy = (ldy * VIRTUAL_CELL_HEIGHT) << 8;
                        dx += spd;

                        ddx = dx >> 8;

                        ldx = (ddx + VIRTUAL_CELL_WIDTH) / VIRTUAL_CELL_WIDTH;

                        if (_game_state.getElementAt(ldx, ldy) == Labyrinths.ELE_WALL)
                        {
                            dx = ((ldx - 1) * VIRTUAL_CELL_WIDTH) << 8;
                            _anim = false;
                        }
                        _new_dir = Man.MOVE_RIGHT_STATE;
                    }
                    ;
                    break;
            }

            if (direct != DIRECT_NONE)
            {
                man._ticks++;

                if (((man._cur_state & 0xF0) == _new_dir) && _anim)
                {
                    // Direction is not changed
                    if (man._ticks >= Man.MOVE_TICK_DELAY)
                    {
                        man._ticks = 0;
                        int st = man._cur_state & 0x0F;
                        st++;
                        if (st >= Man.MOVE_FRAMES) st = 0;
                        man._cur_state = st | _new_dir;
                    }
                }
                else
                {
                    // Direction is changed
                    man._cur_state = _new_dir;
                    man._ticks = 0;
                }
            }
            man._x = dx;
            man._y = dy;
        }
        return false;
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
