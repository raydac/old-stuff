package com.igormaznitsa.GameKit_FE652.Pitman;

//import com.igormaznitsa.GameAPI.Pitman_GSR;
import com.GameArea;
//import com.igormaznitsa.GameAPI.PlayerBlock;
//import java.io.OutputStream;
//import java.io.IOException;
//import java.io.InputStream;

import java.io.*;

public class Pitman_SB
{
    public static final int MOVE_NONE = 0;
    public static final int MOVE_UP = 1;
    public static final int MOVE_RIGHT = 2;
    public static final int MOVE_DOWN = 3;
    public static final int MOVE_LEFT = 4;


    public static final int LEVEL0 = 0;
    public static final int LEVEL0_TIMEDELAY = 170;
    public static final int LEVEL1 = 1;
    public static final int LEVEL1_TIMEDELAY = 120;
    public static final int LEVEL2 = 2;
    public static final int LEVEL2_TIMEDELAY = 70;

    public static final int VIRTUAL_CELL_WIDTH = 12;
    public static final int VIRTUAL_CELL_HEIGHT = 12;

    public int i_Button;

    protected Pitman_GSR _game_state;

    public int getMovingStoneNumber()
    {
        return _game_state._moving_stone_counter;
    }

    public MovingElement[] getStonesArray()
    {
        return _game_state._stones;
    }

    public void loadGameState(InputStream inputStream) throws IOException
    {
        _game_state = null;
        System.gc();
        DataInputStream dais = new DataInputStream(inputStream);

        int level = dais.readUnsignedByte();
        int stage = dais.readUnsignedByte();

        newGame(level);
        initStage(stage);

        _game_state._level_scores = dais.readUnsignedShort();
        _game_state._max_diamods =  dais.readUnsignedShort();
        _game_state._player_index = dais.readInt();
        _game_state._player_scores = dais.readInt();

        for(int li=0;li<_game_state._lab.length;li++)
        {
            _game_state._lab[li] = dais.readByte();
        }

        for(int li=0;li<_game_state.creatures.length;li++)
        {
            MovingCreature crt = _game_state.creatures[li];
            crt._cellx = dais.readByte();
            crt._celly = dais.readByte();
            crt._frame = dais.readByte();
            crt._state = dais.readByte();
            crt._ticks = dais.readByte();
            crt._type = dais.readByte();
            crt._x = dais.readUnsignedShort();
            crt._y = dais.readUnsignedShort();
        }

        dais = null;
        Runtime.getRuntime().gc();
    }

    public void saveGameState(OutputStream outputStream) throws IOException
    {
        System.gc();
        DataOutputStream daos = new DataOutputStream(outputStream);

        daos.writeByte(_game_state._level);
        daos.writeByte(_game_state._stage);

        daos.writeShort(_game_state._level_scores);
        daos.writeShort(_game_state._max_diamods);
        daos.writeInt(_game_state._player_index);
        daos.writeInt(_game_state._player_scores);

        for(int li=0;li<_game_state._lab.length;li++)
        {
            daos.writeByte(_game_state._lab[li]);
        }

        for(int li=0;li<_game_state.creatures.length;li++)
        {
            MovingCreature crt = _game_state.creatures[li];
            daos.writeByte(crt._cellx);
            daos.writeByte(crt._celly);
            daos.writeByte(crt._frame);
            daos.writeByte(crt._state);
            daos.writeByte(crt._ticks);
            daos.writeByte(crt._type);
            daos.writeShort(crt._x);
            daos.writeShort(crt._y);
        }

        daos = null;
        System.gc();
    }

    public void newGame(int level)
    {
        _game_state = new Pitman_GSR(level);
    }

    public void initStage(int stage)
    {
        _game_state.initStage(stage);
    }

    public void nextGameStep()
    {
        int bt = i_Button;

        _game_state._new_player_dir = bt;

        if (fillMovingArray())
        {
            _game_state._game_state = Pitman_GSR.GAMESTATE_OVER;
            _game_state._player_state = Pitman_GSR.PLAYERSTATE_LOST;
            _game_state._player_scores += _game_state._level_scores;
        }
        else if (processMovingCreatures())
        {
            _game_state._game_state = Pitman_GSR.GAMESTATE_OVER;
            _game_state._player_state = Pitman_GSR.PLAYERSTATE_LOST;
            _game_state._player_scores += _game_state._level_scores;
        }
        else if (_game_state._max_diamods == _game_state._level_scores)
        {
            _game_state._player_state = Pitman_GSR.PLAYERSTATE_WON;
            _game_state._player_scores += _game_state._level_scores;
        }
    }

    protected void deactivateCreature(int cx, int cy)
    {
        MovingCreature[] crt = _game_state.creatures;
        for (int li = 0; li < crt.length; li++)
        {
            MovingCreature cr = crt[li];
            if (cr._type == MovingCreature.CREATURE_INACTIVE) continue;
            if (cr._cellx == cx && cr._celly == cy)
            {
                cr._type = MovingCreature.CREATURE_INACTIVE;
                break;
            }
        }
    }

    protected boolean fillMovingArray()
    {
        byte[] bte = _game_state._lab;
        _game_state._moving_stone_counter = 0;
        for (int li = (bte.length -1); li >= 0; li--)
        {
            switch (bte[li])
            {
                case Labyrinths.ELEMENT_STONE:
                case Labyrinths.ELEMENT_DIAMOND:
                    {
                        int x = li % Labyrinths.LAB_WIDTH;
                        int y = li / Labyrinths.LAB_WIDTH;
                        byte ell = _game_state.getElementAt(x, y);
                        byte el = _game_state.getElementAt(x, y + 1);
                        switch (el)
                        {
                            case Labyrinths.ELEMENT_DIAMOND :
                            case Labyrinths.ELEMENT_STONE :{
                                if ((_game_state.getElementAt(x+1,y)==Labyrinths.ELEMENT_EMPTY)&&(_game_state.getElementAt(x+1,y+1)==Labyrinths.ELEMENT_EMPTY))
                                {
                                    addMoveElement(ell,x,y,x+1,y);
                                    _game_state.setElementAt(x, y, Labyrinths.ELEMENT_EMPTY);
                                    _game_state.setElementAt(x+1, y, ell);
                                }
                                else
                                if ((_game_state.getElementAt(x-1,y)==Labyrinths.ELEMENT_EMPTY)&&(_game_state.getElementAt(x-1,y+1)==Labyrinths.ELEMENT_EMPTY))
                                {
                                    addMoveElement(ell,x,y,x-1,y);
                                    _game_state.setElementAt(x,y, Labyrinths.ELEMENT_EMPTY);
                                    _game_state.setElementAt(x-1,y, ell);
                                }
                            };break;
                            case Labyrinths.ELEMENT_FIREFLY:
                                {
                                    _game_state.setElementAt(x,y,Labyrinths.ELEMENT_EMPTY);
                                    addMoveElement(Labyrinths.ELEMENT_EMPTY,x,y,x,y);
                                    _game_state._player_scores +=10;
                                    deactivateCreature(x, y + 1);
                                    boolean playerdied = false;
                                    for (int ly = y; ly <= y + 2; ly++)
                                    {
                                        for (int lx = x - 1; lx <= x + 1; lx++)
                                        {
                                            switch (_game_state.getElementAt(lx, ly))
                                            {
                                                case Labyrinths.ELEMENT_PLAYER: playerdied = true;
                                                case Labyrinths.ELEMENT_BUTTERFLY:
                                                case Labyrinths.ELEMENT_FIREFLY:
                                                    {
                                                        deactivateCreature(lx, ly);
                                                        _game_state.setElementAt(lx, ly, Labyrinths.ELEMENT_INVISIBLE);
                                                        _game_state.getInactiveCreature().setCXCYS(MovingCreature.ELEMENT_EXPLOSIVE, lx, ly);
                                                    }
                                                    ;
                                                    break;
                                                case Labyrinths.ELEMENT_DIAMOND:
                                                    {
                                                        _game_state.setElementAt(lx, ly, Labyrinths.ELEMENT_INVISIBLE);
                                                        _game_state.getInactiveCreature().setCXCYS(MovingCreature.ELEMENT_EXPLOSIVE, lx, ly);
                                                        _game_state._max_diamods--;
                                                    }
                                                    ;
                                                    break;
                                                case Labyrinths.ELEMENT_INVISIBLE:
                                                case Labyrinths.ELEMENT_TITANIUMWALL:
                                                    {
                                                    }
                                                    ;
                                                    break;
                                                default :
                                                    {
                                                        _game_state.setElementAt(lx, ly, Labyrinths.ELEMENT_INVISIBLE);
                                                        _game_state.getInactiveCreature().setCXCYS(MovingCreature.ELEMENT_EXPLOSIVE, lx, ly);
                                                    }
                                            }
                                        }
                                    }
                                    if (playerdied) return true;
                                }
                                ;
                                break;
                            case Labyrinths.ELEMENT_BUTTERFLY:
                                {
                                    _game_state.setElementAt(x,y,Labyrinths.ELEMENT_EMPTY);
                                    addMoveElement(Labyrinths.ELEMENT_EMPTY,x,y,x,y);
                                    _game_state._player_scores +=20;
                                    deactivateCreature(x, y + 1);
                                    for (int ly = y; ly <= y+2; ly++)
                                    {
                                        for (int lx = x - 1; lx <= x + 1; lx++)
                                        {
                                            switch (_game_state.getElementAt(lx, ly))
                                            {
                                                case Labyrinths.ELEMENT_PLAYER:
                                                    {
                                                        _game_state._max_diamods++;
                                                        _game_state._level_scores++;
                                                    }
                                                    ;
                                                    break;
                                                case Labyrinths.ELEMENT_BUTTERFLY:
                                                case Labyrinths.ELEMENT_FIREFLY:
                                                    {
                                                        deactivateCreature(lx, ly);
                                                        _game_state.setElementAt(lx, ly, Labyrinths.ELEMENT_INVISIBLE);
                                                        _game_state.getInactiveCreature().setCXCYS(MovingCreature.ELEMENT_GROWCRYSTAL, lx, ly);
                                                        _game_state._max_diamods++;
                                                    }
                                                    ;
                                                    break;

                                                case Labyrinths.ELEMENT_DIAMOND:
                                                case Labyrinths.ELEMENT_INVISIBLE:
                                                case Labyrinths.ELEMENT_TITANIUMWALL:
                                                    {
                                                    }
                                                    ;
                                                    break;
                                                default :
                                                    {
                                                        _game_state.setElementAt(lx, ly, Labyrinths.ELEMENT_INVISIBLE);
                                                        _game_state.getInactiveCreature().setCXCYS(MovingCreature.ELEMENT_GROWCRYSTAL, lx, ly);
                                                        _game_state._max_diamods++;
                                                    }
                                            }
                                        }
                                    }
                                }
                                ;
                                break;
                            case Labyrinths.ELEMENT_EMPTY:
                                {
                                    addMoveElement(ell,x,y,x,y+1);
                                    _game_state.setElementAt(x, y, Labyrinths.ELEMENT_EMPTY);
                                    _game_state.setElementAt(x, y + 1, ell);

                                    switch (_game_state.getElementAt(x, y + 2))
                                    {
                                        case Labyrinths.ELEMENT_PLAYER:
                                            {
                                                return true;
                                            }
                                    }
                                }
                                ;
                                break;
                        }
                    }
            }
        }
        return false;
    }

    protected int isPlayerNearly(int cx, int cy)
    {
        if (_game_state.getElementAt(cx - 1, cy) == Labyrinths.ELEMENT_PLAYER) return MovingCreature.STATE_LEFT;
        if (_game_state.getElementAt(cx, cy - 1) == Labyrinths.ELEMENT_PLAYER) return MovingCreature.STATE_TOP;
        if (_game_state.getElementAt(cx + 1, cy) == Labyrinths.ELEMENT_PLAYER) return MovingCreature.STATE_RIGHT;
        if (_game_state.getElementAt(cx, cy + 1) == Labyrinths.ELEMENT_PLAYER) return MovingCreature.STATE_DOWN;
        return MovingCreature.STATE_STAND;
    }

    // Return true if player was killed in this moving
    protected boolean processMovingCreatures()
    {
        MovingCreature[] _cr = _game_state.creatures;
        for (int li = 0; li < _cr.length; li++)
        {
            MovingCreature crt = _cr[li];
            int dx = crt._x % VIRTUAL_CELL_WIDTH;
            int dy = crt._y % VIRTUAL_CELL_HEIGHT;

            if ((dx != 0) || (dy != 0))
            {
                crt.processAnimation();
                switch (crt._state)
                {
                    case MovingCreature.STATE_DOWN:
                        crt._y += MovingCreature.SPEED;
                        break;
                    case MovingCreature.STATE_LEFT:
                        crt._x -= MovingCreature.SPEED;
                        break;
                    case MovingCreature.STATE_RIGHT:
                        crt._x += MovingCreature.SPEED;
                        break;
                    case MovingCreature.STATE_TOP:
                        crt._y -= MovingCreature.SPEED;
                        break;
                }
            }
            else
            {
                int ex = crt._x / VIRTUAL_CELL_WIDTH;
                int ey = crt._y / VIRTUAL_CELL_HEIGHT;
                int nx = ex;
                int ny = ey;
                switch (crt._type)
                {
                    case MovingCreature.ELEMENT_GROWCRYSTAL:
                        {
                            if (crt.processAnimation())
                            {
                                crt._type = MovingCreature.CREATURE_INACTIVE;
                                int cx = crt._cellx;
                                int cy = crt._celly;
                                _game_state.setElementAt(cx, cy, Labyrinths.ELEMENT_DIAMOND);
                                addMoveElement(Labyrinths.ELEMENT_DIAMOND,cx,cy,cx,cy);
                            }
                        }
                        ;
                        break;
                    case MovingCreature.ELEMENT_EXPLOSIVE:
                        {
                            if (crt.processAnimation())
                            {
                                crt._type = MovingCreature.CREATURE_INACTIVE;
                                int cx = crt._cellx;
                                int cy = crt._celly;
                                _game_state.setElementAt(cx, cy, Labyrinths.ELEMENT_EMPTY);
                                addMoveElement(Labyrinths.ELEMENT_EMPTY,cx,cy,cx,cy);
                            }
                        }
                        ;
                        break;
                    case MovingCreature.CREATURE_BUTTERFLY:
                        {
                            crt.processAnimation();
                            if (isPlayerNearly(ex, ey) != MovingCreature.STATE_STAND) return true;
                            switch (crt._state)
                            {
                                case MovingCreature.STATE_DOWN:
                                    if (_game_state.getElementAt(ex, ey + 1) == Labyrinths.ELEMENT_EMPTY)
                                    {
                                        crt._y += MovingCreature.SPEED;
                                        ny++;
                                    }
                                    else
                                    {
                                        crt.setState(MovingCreature.STATE_LEFT);
                                    }
                                    ;
                                    break;
                                case MovingCreature.STATE_LEFT:
                                    if (_game_state.getElementAt(ex - 1, ey) == Labyrinths.ELEMENT_EMPTY)
                                    {
                                        crt._x -= MovingCreature.SPEED;
                                        nx--;
                                    }
                                    else
                                    {
                                        crt.setState(MovingCreature.STATE_TOP);
                                    }
                                    ;
                                    break;
                                case MovingCreature.STATE_RIGHT:
                                    if (_game_state.getElementAt(ex + 1, ey) == Labyrinths.ELEMENT_EMPTY)
                                    {
                                        crt._x += MovingCreature.SPEED;
                                        nx++;
                                    }
                                    else
                                    {
                                        crt.setState(MovingCreature.STATE_DOWN);
                                    }
                                    ;
                                    break;
                                case MovingCreature.STATE_TOP:
                                    if (_game_state.getElementAt(ex, ey - 1) == Labyrinths.ELEMENT_EMPTY)
                                    {
                                        crt._y -= MovingCreature.SPEED;
                                        ny--;
                                    }
                                    else
                                    {
                                        crt.setState(MovingCreature.STATE_RIGHT);
                                    }
                                    ;
                                    break;
                                case MovingCreature.STATE_STAND:
                                    crt.setState(MovingCreature.STATE_TOP);
                                    break;
                            }
                        }
                        ;
                        break;
                    case MovingCreature.CREATURE_FIREFLY:
                        {
                            crt.processAnimation();
                            if (isPlayerNearly(ex, ey) != MovingCreature.STATE_STAND) return true;
                            switch (crt._state)
                            {
                                case MovingCreature.STATE_DOWN:
                                    if (_game_state.getElementAt(ex, ey + 1) == Labyrinths.ELEMENT_EMPTY)
                                    {
                                        crt._y += MovingCreature.SPEED;
                                        ny++;
                                    }
                                    else
                                    {
                                        crt.setState(MovingCreature.STATE_RIGHT);
                                    }
                                    ;
                                    break;
                                case MovingCreature.STATE_LEFT:
                                    if (_game_state.getElementAt(ex - 1, ey) == Labyrinths.ELEMENT_EMPTY)
                                    {
                                        crt._x -= MovingCreature.SPEED;
                                        nx--;
                                    }
                                    else
                                    {
                                        crt.setState(MovingCreature.STATE_DOWN);
                                    }
                                    ;
                                    break;
                                case MovingCreature.STATE_RIGHT:
                                    if (_game_state.getElementAt(ex + 1, ey) == Labyrinths.ELEMENT_EMPTY)
                                    {
                                        crt._x += MovingCreature.SPEED;
                                        nx++;
                                    }
                                    else
                                    {
                                        crt.setState(MovingCreature.STATE_TOP);
                                    }
                                    ;
                                    break;
                                case MovingCreature.STATE_TOP:
                                    if (_game_state.getElementAt(ex, ey - 1) == Labyrinths.ELEMENT_EMPTY)
                                    {
                                        crt._y -= MovingCreature.SPEED;
                                        ny--;
                                    }
                                    else
                                    {
                                        crt.setState(MovingCreature.STATE_LEFT);
                                    }
                                    ;
                                    break;
                                case MovingCreature.STATE_STAND:
                                    crt.setState(MovingCreature.STATE_DOWN);
                                    break;
                            }
                        }
                        ;
                        break;
                    case MovingCreature.CREATURE_PLAYER:
                        {
                            crt.processAnimation();
                            switch (_game_state._new_player_dir)
                            {
                                case MOVE_UP:
                                    {
                                        boolean fl = false;
                                        switch (_game_state.getElementAt(ex, ey - 1))
                                        {
                                            case Labyrinths.ELEMENT_EMPTY:
                                            case Labyrinths.ELEMENT_DIRT:
                                                {
                                                    fl = true;
                                                }
                                                ;
                                                break;
                                            case Labyrinths.ELEMENT_DIAMOND:
                                                {
                                                    fl = true;
                                                    _game_state._level_scores++;
                                                }
                                                ;
                                                break;
                                            default :
                                                crt.setState(MovingCreature.STATE_STAND);
                                        }
                                        if (fl)
                                        {
                                            ny--;
                                            crt.setState(MovingCreature.STATE_TOP);
                                            crt._y -= MovingCreature.SPEED;
                                        }
                                    }
                                    ;
                                    break;
                                case MOVE_DOWN:
                                    {
                                        boolean fl = false;
                                        switch (_game_state.getElementAt(ex, ey + 1))
                                        {
                                            case Labyrinths.ELEMENT_EMPTY:
                                            case Labyrinths.ELEMENT_DIRT:
                                                {
                                                    fl = true;
                                                }
                                                ;
                                                break;
                                            case Labyrinths.ELEMENT_DIAMOND:
                                                {
                                                    fl = true;
                                                    _game_state._level_scores++;
                                                }
                                                ;
                                                break;
                                            default :
                                                crt.setState(MovingCreature.STATE_STAND);
                                        }
                                        if (fl)
                                        {
                                            ny++;
                                            crt.setState(MovingCreature.STATE_DOWN);
                                            crt._y += MovingCreature.SPEED;
                                        }
                                    }
                                    ;
                                    break;
                                case MOVE_LEFT:
                                    {
                                        boolean fl = false;
                                        switch (_game_state.getElementAt(ex - 1, ey))
                                        {
                                            case Labyrinths.ELEMENT_EMPTY:
                                            case Labyrinths.ELEMENT_DIRT:
                                                {
                                                    fl = true;
                                                }
                                                ;
                                                break;
                                            case Labyrinths.ELEMENT_DIAMOND:
                                                {
                                                    fl = true;
                                                    _game_state._level_scores++;
                                                }
                                                ;
                                                break;
                                            case Labyrinths.ELEMENT_STONE:
                                                {
                                                    if (_game_state.getElementAt(ex - 2, ey) == Labyrinths.ELEMENT_EMPTY)
                                                    {
                                                        _game_state.setElementAt(ex - 2, ey, Labyrinths.ELEMENT_STONE);
                                                        _game_state.setElementAt(ex - 1, ey, Labyrinths.ELEMENT_EMPTY);

                                                        addMoveElement(Labyrinths.ELEMENT_STONE,ex-1,ey,ex-2,ey);

                                                        fl = true;
                                                    }
                                                }
                                                ;
                                                break;
                                            default :
                                                crt.setState(MovingCreature.STATE_STAND);
                                        }
                                        if (fl)
                                        {
                                            nx--;
                                            crt.setState(MovingCreature.STATE_LEFT);
                                            crt._x -= MovingCreature.SPEED;
                                        }
                                    }
                                    ;
                                    break;
                                case MOVE_RIGHT:
                                    {
                                        boolean fl = false;
                                        switch (_game_state.getElementAt(ex + 1, ey))
                                        {
                                            case Labyrinths.ELEMENT_EMPTY:
                                            case Labyrinths.ELEMENT_DIRT:
                                                {
                                                    fl = true;
                                                }
                                                ;
                                                break;
                                            case Labyrinths.ELEMENT_STONE:
                                                {
                                                    if (_game_state.getElementAt(ex + 2, ey) == Labyrinths.ELEMENT_EMPTY)
                                                    {
                                                        _game_state.setElementAt(ex + 2, ey, Labyrinths.ELEMENT_STONE);
                                                        _game_state.setElementAt(ex + 1, ey, Labyrinths.ELEMENT_EMPTY);

                                                        addMoveElement(Labyrinths.ELEMENT_STONE,ex+1,ey,ex+2,ey);
                                                        fl = true;
                                                    }
                                                }
                                                ;
                                                break;
                                            case Labyrinths.ELEMENT_DIAMOND:
                                                {
                                                    fl = true;
                                                    _game_state._level_scores++;
                                                }
                                                ;
                                                break;
                                            default :
                                                crt.setState(MovingCreature.STATE_STAND);
                                        }
                                        if (fl)
                                        {
                                            nx++;
                                            crt.setState(MovingCreature.STATE_RIGHT);
                                            crt._x += MovingCreature.SPEED;
                                        }
                                    }
                                    ;
                                    break;
                                case MOVE_NONE:
                                    {
                                        if (crt._state != MovingCreature.STATE_STAND) crt.setState(MovingCreature.STATE_STAND);
                                    }
                                    ;
                                    break;
                            }
                        }
                        ;
                        break;
                }

                byte el = _game_state.getElementAt(ex, ey);
                _game_state.setElementAt(ex, ey, Labyrinths.ELEMENT_EMPTY);
                _game_state.setElementAt(nx, ny, el);
                crt._cellx = nx;
                crt._celly = ny;
            }
        }
        return false;
    }

    protected void addMoveElement(byte type,int sx,int sy,int ex,int ey)
    {
        MovingElement _el =  _game_state._stones[_game_state._moving_stone_counter++];
        _el._type = type;
        _el._cur_x = ex;
        _el._cur_y = ey;
        _el._prev_x = sx;
        _el._prev_y = sy;
    }

    public Pitman_GSR getPitman_GSR()
    {
        return _game_state;
    }

    public boolean isLoadSaveSupporting()
    {
        return true;
    }
}
