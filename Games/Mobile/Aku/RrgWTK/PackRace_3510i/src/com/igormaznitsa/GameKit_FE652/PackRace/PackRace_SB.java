package com.igormaznitsa.GameKit_FE652.PackRace;

import com.igormaznitsa.GameAPI.GameStateRecord;
import java.util.Random;
import java.io.*;

public class PackRace_SB extends Random
{

    public static final int DIRECT_NONE = 0;
    public static final int DIRECT_LEFT = 1;
    public static final int DIRECT_RIGHT = 2;
    public static final int DIRECT_UP = 3;
    public static final int DIRECT_DOWN = 4;

    public static int VIRTUAL_CELL_WIDTH = 10;
    public static int VIRTUAL_CELL_HEIGHT = 10;

    public static final int MAX_ATTEMPTIONS = 3;
    public static final int LEVEL0 = 0;
    public static final int LEVEL0_DELAY = 170;
    public static final int LEVEL1 = 1;
    public static final int LEVEL1_DELAY = 100;
    public static final int LEVEL2 = 2;
    public static final int LEVEL2_DELAY = 70;

    public static final int CREATURE_SPEED = 2;

    public static final int GUN_TICKS = 100;

    protected boolean _fieldchanged = false;
    protected int _changex = -1;
    protected int _changey = -1;

    protected PackRace_GSR _game_state = null;

    protected int _next_dir = 0;
    public int i_Button = DIRECT_NONE;

    public boolean isFieldChanged()
    {
        return _fieldchanged;
    }

    public int getChangeX()
    {
        return _changex;
    }

    public int getChangeY()
    {
        return _changey;
    }

    public boolean isGunMode()
    {
        return _game_state._isguninhend;
    }

    public PackRace_SB()
    {
       super(System.currentTimeMillis());
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

        dos.writeByte(_game_state._level);
        dos.writeInt(_game_state._summary_scores);
        dos.writeByte(_game_state._lab_num);
        dos.writeByte(_game_state._attempts);
        dos.writeShort(_game_state._cur_rice_number);

        dos.writeByte(_game_state._creature._cur_state);
        dos.writeByte(_game_state._creature._frame);
        dos.writeByte(_game_state._creature._last_direct);
        dos.writeByte(_game_state._creature._prelast_direct);
        dos.writeByte(_game_state._creature._tick);
        dos.writeShort(_game_state._creature._x);
        dos.writeShort(_game_state._creature._y);

        dos.writeBoolean(_game_state._isguninhend);
        dos.writeShort(_game_state._gun_ticks);

        for(int li=0;li<_game_state._guard.length;li++)
        {
            Creature _crt = _game_state._guard[li];

            dos.writeByte(_crt._cur_state);
            dos.writeByte(_crt._frame);
            dos.writeByte(_crt._last_direct);
            dos.writeByte(_crt._prelast_direct);
            dos.writeByte(_crt._tick);
            dos.writeShort(_crt._x);
            dos.writeShort(_crt._y);
        }

        for(int li=0;li<_game_state._cur_lab.length;li++)
        {
            dos.writeByte(_game_state._cur_lab[li]);
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
        _game_state = new PackRace_GSR(lvl);
        _game_state._summary_scores = dos.readInt();
        _game_state.loadLabyrinth(dos.readByte());
        _game_state._attempts = dos.readByte();
        _game_state._cur_rice_number = dos.readUnsignedShort();

        _game_state._creature._cur_state = dos.readByte();
        _game_state._creature._frame = dos.readByte();
        _game_state._creature._last_direct = dos.readByte();
        _game_state._creature._prelast_direct = dos.readByte();
        _game_state._creature._tick = dos.readByte();
        _game_state._creature._x = dos.readShort();
        _game_state._creature._y = dos.readShort();

        _game_state._isguninhend = dos.readBoolean();
        _game_state._gun_ticks = dos.readShort();

        for(int li=0;li<_game_state._guard.length;li++)
        {
            Creature _crt = _game_state._guard[li];

            _crt._cur_state = dos.readByte();
            _crt._frame = dos.readByte();
            _crt._last_direct = dos.readByte();
            _crt._prelast_direct = dos.readByte();
            _crt._tick = dos.readByte();
            _crt._x = dos.readShort();
            _crt._y = dos.readShort();
        }

        for(int li=0;li<_game_state._cur_lab.length;li++)
        {
            _game_state._cur_lab[li] = dos.readUnsignedByte();
        }

        dos = null;
        Runtime.getRuntime().gc();
    }

    public void resumeGame()
    {
        _game_state.resumeGame();
    }

    public void initStage(int stage)
    {
        _game_state._summary_scores += _game_state._cur_level_scores;
        _game_state.loadLabyrinth(stage);
    }

    public void newGame(int level)
    {
        _game_state = new PackRace_GSR(level);
        _next_dir = DIRECT_NONE;
    }

    protected int invertDir(int dir)
    {
        switch (dir)
        {
            case DIRECT_LEFT:
                return DIRECT_RIGHT;
            case DIRECT_DOWN:
                return DIRECT_UP;
            case DIRECT_RIGHT:
                return DIRECT_LEFT;
            case DIRECT_UP:
                return DIRECT_DOWN;
        }
        return DIRECT_NONE;
    }

    public void nextGameStep()
    {
        if (_game_state._isguninhend)
        {
            _game_state._gun_ticks++;
            if (_game_state._gun_ticks >= GUN_TICKS)
            {
                _game_state._isguninhend = false;
                _game_state._gun_ticks = 0;
            }
        }

        _fieldchanged = false;


        if (i_Button != DIRECT_NONE)
        {
            _next_dir = i_Button;
        }

        processCreature(_game_state._creature, _next_dir);

        int lx = (_game_state._creature._x + (VIRTUAL_CELL_WIDTH >> 1)) / VIRTUAL_CELL_WIDTH;
        int ly = (_game_state._creature._y + (VIRTUAL_CELL_HEIGHT >> 1)) / VIRTUAL_CELL_HEIGHT;

        switch (_game_state.getElementAt(lx, ly))
        {
            case Labyrints.ELE_RICE:
                {
                    _fieldchanged = true;
                    _changex = lx;
                    _changey = ly;
                    _game_state._cur_rice_number++;
                    _game_state._cur_level_scores++;
                    _game_state.setElementAt(lx, ly, Labyrints.ELE_EMPTY);
                }
                ;
                break;
            case Labyrints.ELE_GUN:
                {
                    _fieldchanged = true;
                    _changex = lx;
                    _changey = ly;
                    _game_state._isguninhend = true;
                    _game_state._gun_ticks = 0;
                    _game_state.setElementAt(lx, ly, Labyrints.ELE_EMPTY);
                }
                ;
                break;
        }


        if (_game_state._cur_max_rice == _game_state._cur_rice_number)
        {
            _game_state._player_state = PackRace_GSR.PLAYERSTATE_WON;
        }

        if (processGuard())
        {
            if (isGunMode())
            {
                _game_state._cur_level_scores += 10;
            }
            else
            {
                _game_state._attempts--;
                _game_state._player_state = PackRace_GSR.PLAYERSTATE_KILLED;
                if (_game_state._attempts == 0)
                {
                    _game_state._summary_scores += _game_state._cur_level_scores;
                    _game_state._game_state = PackRace_GSR.GAMESTATE_OVER;
                }
            }
        }
    }

    protected int whereCreatureIsVisible(int x, int y)
    {
        int lx = _game_state._creature._x / VIRTUAL_CELL_WIDTH;
        int ly = _game_state._creature._y / VIRTUAL_CELL_HEIGHT;

        int dx = x;
        int dy = y - 1;

        // Check up
        while (true)
        {
            if (_game_state.getElementAt(dx, dy) == Labyrints.ELE_WALL) break;

            if ((lx == dx) && (ly == dy)) return DIRECT_UP;
            dy--;
        }

        dy = y + 1;

        // Check down
        while (true)
        {
            if (_game_state.getElementAt(dx, dy) == Labyrints.ELE_WALL) break;

            if ((lx == dx) && (ly == dy))
                return DIRECT_DOWN;
            dy++;
        }

        dx = x - 1;
        dy = y;

        // Check left
        while (true)
        {
            if (_game_state.getElementAt(dx, dy) == Labyrints.ELE_WALL) break;

            if ((lx == dx) && (ly == dy))
                return DIRECT_LEFT;
            dx--;
        }

        dx = x + 1;

        // Check right
        while (true)
        {
            if (_game_state.getElementAt(dx, dy) == Labyrints.ELE_WALL) break;

            if ((lx == dx) && (ly == dy))
                return DIRECT_RIGHT;
            dx++;
        }

        return DIRECT_NONE;
    }

    protected boolean isGuardianinTheCell(int x, int y)
    {
        for (int li = 0; li < _game_state._guard.length; li++)
        {
            int lx = _game_state._guard[li]._x / VIRTUAL_CELL_WIDTH;
            int ly = _game_state._guard[li]._y / VIRTUAL_CELL_HEIGHT;
            if ((x == lx) && (y == ly)) return true;
        }
        return false;
    }

    protected boolean processGuard()
    {
        boolean rslt = false;

        for (int li = 0; li < _game_state._guard.length; li++)
        {
            Creature crt = _game_state._guard[li];

            int lx = crt._x / VIRTUAL_CELL_WIDTH;
            int ly = crt._y / VIRTUAL_CELL_HEIGHT;

            int dir = whereCreatureIsVisible(lx, ly);


            if (dir == DIRECT_NONE)
            {
                if (crt._last_direct == DIRECT_NONE)
                {
                    int newdr = getInt(3) + 1;
                    boolean fl = true;
                    for (int lii = 0; lii < 3; lii++)
                    {
                        newdr++;
                        if (newdr > 4) newdr = 1;

                        switch (newdr)
                        {
                            case DIRECT_DOWN:
                                {
                                    if (_game_state.getElementAt(lx, ly + 1) == Labyrints.ELE_WALL) continue;
                                }
                                ;
                                break;
                            case DIRECT_LEFT:
                                {
                                    if (_game_state.getElementAt(lx - 1, ly) == Labyrints.ELE_WALL) continue;
                                }
                                ;
                                break;
                            case DIRECT_RIGHT:
                                {
                                    if (_game_state.getElementAt(lx + 1, ly) == Labyrints.ELE_WALL) continue;
                                }
                                ;
                                break;
                            case DIRECT_UP:
                                {
                                    if (_game_state.getElementAt(lx, ly - 1) == Labyrints.ELE_WALL) continue;
                                }
                                ;
                                break;
                        }
                        if (newdr == crt._prelast_direct) continue;
                        fl = false;
                        break;
                    }

                    if (fl)
                    {
                        dir = crt._prelast_direct;
                    }
                    else
                    {
                        dir = newdr;
                    }
                }
                else
                {
                    dir = crt._last_direct;
                }
            }
            else
            {
                if (isGunMode())
                {
                    crt._prelast_direct = dir;
                    int newdr = getInt(3) + 1;
                    boolean fl = true;

                    for (int lii = 0; lii < 3; lii++)
                    {
                        newdr++;
                        if (newdr > 4) newdr = 1;

                        switch (newdr)
                        {
                            case DIRECT_DOWN:
                                {
                                    if (_game_state.getElementAt(lx, ly + 1) == Labyrints.ELE_WALL) continue;
                                }
                                ;
                                break;
                            case DIRECT_LEFT:
                                {
                                    if (_game_state.getElementAt(lx - 1, ly) == Labyrints.ELE_WALL) continue;
                                }
                                ;
                                break;
                            case DIRECT_RIGHT:
                                {
                                    if (_game_state.getElementAt(lx + 1, ly) == Labyrints.ELE_WALL) continue;
                                }
                                ;
                                break;
                            case DIRECT_UP:
                                {
                                    if (_game_state.getElementAt(lx, ly - 1) == Labyrints.ELE_WALL) continue;
                                }
                                ;
                                break;
                        }
                        if (newdr == crt._prelast_direct) continue;
                        fl = false;
                        break;
                    }

                    if (fl)
                    {
                        dir = crt._prelast_direct;
                    }
                    else
                    {
                        dir = newdr;
                    }
                }
            }

            processCreature(crt, dir);

            int xx = _game_state._creature._x;
            int yy = _game_state._creature._y;

            // Checking of the creature
            if (!((crt._x + VIRTUAL_CELL_WIDTH <= xx) || (crt._y + VIRTUAL_CELL_HEIGHT <= yy) || (crt._x >= xx + VIRTUAL_CELL_WIDTH) || (crt._y >= yy + VIRTUAL_CELL_HEIGHT)))
            {
                if (_game_state._isguninhend)
                {
                    crt.init();
                }
                rslt = true;
            }
        }
        return rslt;
    }

    protected void processCreature(Creature item, int direct)
    {
        if (item._cur_state == Creature.STATE_APPEARANCE)
        {
            item._tick++;
            if (item._tick >= Creature.APPEARANCE_TICKS)
            {
                item._tick = 0;
                item._frame++;
                if (item._frame >= Creature.FRAME_APPEARANCE)
                {
                    item._frame = 0;
                    item._cur_state = Creature.STATE_RIGHT;
                }
                else
                    return;
            }
            else
                return;
        }

        int x = item.getX();
        int y = item.getY();

        if (((x % VIRTUAL_CELL_WIDTH) != 0) || (y % VIRTUAL_CELL_HEIGHT != 0))
        {
            direct = item._last_direct;
        }
        else
        {
            int lx = x / VIRTUAL_CELL_WIDTH;
            int ly = y / VIRTUAL_CELL_HEIGHT;

            if (direct == DIRECT_NONE) direct = item._last_direct;

            switch (direct)
            {
                case DIRECT_DOWN:
                    {
                        if (_game_state.getElementAt(lx, ly + 1) == Labyrints.ELE_WALL) direct = DIRECT_NONE;
                        item._cur_state = Creature.STATE_DOWN;
                    }
                    ;
                    break;
                case DIRECT_LEFT:
                    {
                        if (_game_state.getElementAt(lx - 1, ly) == Labyrints.ELE_WALL) direct = DIRECT_NONE;
                        item._cur_state = Creature.STATE_LEFT;
                    }
                    ;
                    break;
                case DIRECT_RIGHT:
                    {
                        if (_game_state.getElementAt(lx + 1, ly) == Labyrints.ELE_WALL) direct = DIRECT_NONE;
                        item._cur_state = Creature.STATE_RIGHT;
                    }
                    ;
                    break;
                case DIRECT_UP:
                    {
                        if (_game_state.getElementAt(lx, ly - 1) == Labyrints.ELE_WALL) direct = DIRECT_NONE;
                        item._cur_state = Creature.STATE_UP;
                    }
                    ;
                    break;
            }
            item._prelast_direct = item._last_direct;
            item._last_direct = direct;

            item._frame = 0;
            item._tick = 0;
        }


        switch (direct)
        {
            case DIRECT_DOWN:
                {
                    item._y += CREATURE_SPEED;
                }
                ;
                break;
            case DIRECT_LEFT:
                {
                    item._x -= CREATURE_SPEED;
                }
                ;
                break;
            case DIRECT_RIGHT:
                {
                    item._x += CREATURE_SPEED;

                }
                ;
                break;
            case DIRECT_UP:
                {
                    item._y -= CREATURE_SPEED;
                }
                ;
                break;
        }

        item._tick++;
        if (item._tick >= Creature.ANIME_TICKS)
        {
            item._tick = 0;
            item._frame++;
            if (item._frame >= Creature.FRAME_MOVE)
            {
                item._frame = 0;
            }
        }
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
