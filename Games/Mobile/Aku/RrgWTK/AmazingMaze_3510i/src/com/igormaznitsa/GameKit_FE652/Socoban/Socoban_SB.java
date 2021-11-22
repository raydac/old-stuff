package com.igormaznitsa.GameKit_FE652.Socoban;


import com.igormaznitsa.GameAPI.GameStateRecord;
import com.igormaznitsa.GameAPI.PlayerBlock;
import com.igormaznitsa.GameAPI.StrategicBlock;

import java.io.*;


public class Socoban_SB implements StrategicBlock {
    protected Socoban_GSR _game_state;
    protected PlayerBlock _player_block = null;

    public void setAIBlock(PlayerBlock aiBlock) {
    }

    public void setPlayerBlock(PlayerBlock playerBlock) {
        _player_block = playerBlock;
    }

    public GameStateRecord getGameStateRecord() {
        return _game_state;
    }

    public void resumeGame() {
        _game_state.resumeGame();
    }

    public void loadGameState(InputStream inputStream) throws IOException   //ok
    {
        System.gc();
        DataInputStream dis = new DataInputStream(inputStream);

        int lab = dis.readByte();

	newGame(lab);
        //_game_state.initLabyrinth(lab);
        Runtime.getRuntime().gc();

        _game_state._counter_move = dis.readInt();
        _game_state._prestage_counter_move = dis.readInt();
        _game_state._player_position_x = dis.readByte();
        _game_state._player_position_y = dis.readByte();

        _game_state._player_old_position_x = dis.readByte();
        _game_state._player_old_position_y = dis.readByte();

        _game_state._player_place_position_xy = dis.readInt();
        _game_state._player_next_position_x = dis.readByte();
        _game_state._player_next_position_y = dis.readByte();
        _game_state._player_old_place_position_xy = dis.readInt();

        for (int li = 0; li < _game_state._current_labyrinth.length; li++) {
            _game_state._current_labyrinth[li] = dis.readByte();
        }

        dis = null;
        System.gc();
        if (_player_block != null) _player_block.initPlayer();
    }

    public void saveGameState(OutputStream outputStream) throws IOException       //ok
    {
        System.gc();
        DataOutputStream dis = new DataOutputStream(outputStream);

        dis.writeByte(_game_state._current_lab_num);
        dis.writeInt(_game_state._counter_move);
        dis.writeInt(_game_state._prestage_counter_move);

        dis.writeByte(_game_state._player_position_x);
        dis.writeByte(_game_state._player_position_y);
        dis.writeByte(_game_state._player_old_position_x);
        dis.writeByte(_game_state._player_old_position_y);
        dis.writeInt(_game_state._player_place_position_xy);
        dis.writeByte(_game_state._player_next_position_x);
        dis.writeByte(_game_state._player_next_position_y);
        dis.writeInt(_game_state._player_old_place_position_xy);

        for (int li = 0; li < _game_state._current_labyrinth.length; li++) {
            dis.writeByte(_game_state._current_labyrinth[li]);
        }

        dis = null;
        System.gc();
    }

    public boolean ifNextLevel() {
        boolean flag = true;
        for (int s = 0; s != _game_state._current_labyrinth.length; s++) {
            int Element = _game_state.getElementAt(s);
            if ((Element == Labyrinths.BOX) || (Element == Labyrinths.BOXS_PLACE)) {
                flag = false;
                break;
            }
        }
        return flag;
    }

    public void newGame(int level) {
        _game_state = new Socoban_GSR();
        _game_state._current_lab_num = level;
        resumeGame();
        if (_player_block != null) _player_block.initPlayer();
    }

    public void nextGameStep() {
        int place_xy, place_next_xy;
        if (_player_block == null) return;
        Socoban_PMR Soc = (Socoban_PMR) _player_block.getPlayerMoveRecord(_game_state);
        int _direct = Soc.getDirect();

        switch (_direct) {
            case Socoban_PMR.DIRECT_NONE:
                return;
            case Socoban_PMR.DIRECT_BACK:
                {
                    int napravlenie = _game_state._player_position_x - _game_state._player_old_position_x;
                    int temp = 0;
                    if (napravlenie != 0) {
                        temp = _game_state.getElementAt(_game_state._player_position_x + napravlenie,
                                _game_state._player_position_y);

                        _game_state._player_old_position_y = _game_state._player_position_y;
                        switch (temp) {
                            case Labyrinths.WALL:
                            case Labyrinths.BOXS_PLACE:
                            case Labyrinths.IS_EMPTY:
                                _game_state.setElementAt(_game_state._player_position_x,
                                        _game_state._player_position_y,
                                        _game_state._player_place_position_xy);
                                _game_state._player_place_position_xy = _game_state.getElementAt(_game_state._player_old_position_x,
                                        _game_state._player_old_position_y);
                                _game_state._player_position_x -= napravlenie;
                                _game_state._player_old_position_x = _game_state._player_position_x;
                                break;
                            case Labyrinths.BOX:
                                switch (_game_state._player_place_position_xy) {
                                    case Labyrinths.IS_EMPTY:
                                    case Labyrinths.BOXS_PLACE:
                                        _game_state.setElementAt(_game_state._player_position_x,
                                                _game_state._player_position_y,
                                                _game_state._player_place_position_xy);
                                        _game_state._player_place_position_xy = _game_state.getElementAt(_game_state._player_old_position_x,
                                                _game_state._player_old_position_y);
                                        _game_state._player_position_x -= napravlenie;
                                        _game_state._player_old_position_x = _game_state._player_position_x;
                                        return;
                                }
                                _game_state.setElementAt(_game_state._player_position_x + napravlenie,
                                        _game_state._player_position_y,
                                        Labyrinths.IS_EMPTY);
                                if (_game_state.getElementAt(_game_state._player_position_x,
                                        _game_state._player_position_y) == Labyrinths.BOXS_PLACE) {
                                    _game_state.setElementAt(_game_state._player_position_x, // + napravlenie,
                                            _game_state._player_position_y,
                                            Labyrinths.BOX_ON_PLACE);

                                    _game_state._player_position_x -= napravlenie;
                                    _game_state._player_place_position_xy = _game_state.getElementAt(_game_state._player_position_x,
                                            _game_state._player_position_y); //Labyrinths.BOX_ON_PLACE;
                                    _game_state._player_old_position_x = _game_state._player_position_x;
                                } else {
                                    _game_state.setElementAt(_game_state._player_position_x, //+ napravlenie,
                                            _game_state._player_position_y,
                                            Labyrinths.BOX);
                                    _game_state._player_place_position_xy = _game_state.getElementAt(_game_state._player_position_x, //+ napravlenie,
                                            _game_state._player_position_y);          //Labyrinths.BOX;
                                    _game_state._player_position_x -= napravlenie;
                                    _game_state._player_place_position_xy = _game_state.getElementAt(_game_state._player_position_x, //+ napravlenie,
                                            _game_state._player_position_y);          //Labyrinths.BOX;
                                    _game_state._player_old_position_x = _game_state._player_position_x;
                                }
                                break;
                            case Labyrinths.BOX_ON_PLACE:
                                switch (_game_state._player_place_position_xy) {
                                    case Labyrinths.IS_EMPTY:
                                    case Labyrinths.BOXS_PLACE:
                                        _game_state.setElementAt(_game_state._player_position_x,
                                                _game_state._player_position_y,
                                                _game_state._player_place_position_xy);
                                        _game_state._player_place_position_xy = _game_state.getElementAt(_game_state._player_old_position_x,
                                                _game_state._player_old_position_y);
                                        _game_state._player_position_x -= napravlenie;
                                        _game_state._player_old_position_x = _game_state._player_position_x;
                                        return;
                                }
                                _game_state.setElementAt(_game_state._player_position_x + napravlenie,
                                        _game_state._player_position_y,
                                        Labyrinths.BOXS_PLACE);
                                if (_game_state.getElementAt(_game_state._player_position_x,
                                        _game_state._player_position_y) == Labyrinths.BOXS_PLACE) {
                                    _game_state.setElementAt(_game_state._player_position_x, // + napravlenie,
                                            _game_state._player_position_y,
                                            Labyrinths.BOX_ON_PLACE);
                                    _game_state._player_position_x -= napravlenie;
                                    _game_state._player_place_position_xy = _game_state.getElementAt(_game_state._player_position_x,
                                            _game_state._player_position_y); //Labyrinths.BOX_ON_PLACE;

                                    _game_state._player_old_position_x = _game_state._player_position_x;
                                } else {
                                    _game_state.setElementAt(_game_state._player_position_x, // + napravlenie,
                                            _game_state._player_position_y,
                                            Labyrinths.BOX);
                                    _game_state._player_position_x -= napravlenie;
                                    _game_state._player_place_position_xy = _game_state.getElementAt(_game_state._player_position_x,
                                            _game_state._player_position_y); //Labyrinths.BOX_ON_PLACE;

                                    _game_state._player_old_position_x = _game_state._player_position_x;
                                }

                                break;
                        }
                    }
                    if (napravlenie == 0) {
                        napravlenie = _game_state._player_position_y - _game_state._player_old_position_y;
                        temp = _game_state.getElementAt(_game_state._player_position_x,
                                _game_state._player_position_y + napravlenie);
                        _game_state._player_old_position_x = _game_state._player_position_x;
                        switch (temp) {
                            case Labyrinths.WALL:
                            case Labyrinths.BOXS_PLACE:
                            case Labyrinths.IS_EMPTY:
                                _game_state.setElementAt(_game_state._player_position_x,
                                        _game_state._player_position_y,
                                        _game_state._player_place_position_xy);
                                _game_state._player_place_position_xy = _game_state.getElementAt(_game_state._player_old_position_x,
                                        _game_state._player_old_position_y);
                                _game_state._player_position_y -= napravlenie;
                                _game_state._player_old_position_y = _game_state._player_position_y;
                                break;
                            case Labyrinths.BOX:
                                switch (_game_state._player_place_position_xy) {
                                    case Labyrinths.IS_EMPTY:
                                    case Labyrinths.BOXS_PLACE:
                                        _game_state.setElementAt(_game_state._player_position_x,
                                                _game_state._player_position_y,
                                                _game_state._player_place_position_xy);
                                        _game_state._player_place_position_xy = _game_state.getElementAt(_game_state._player_old_position_x,
                                                _game_state._player_old_position_y);
                                        _game_state._player_position_y -= napravlenie;
                                        _game_state._player_old_position_y = _game_state._player_position_y;
                                        return;
                                }

                                _game_state.setElementAt(_game_state._player_position_x,
                                        _game_state._player_position_y + napravlenie,
                                        Labyrinths.IS_EMPTY);
                                if (_game_state.getElementAt(_game_state._player_position_x,
                                        _game_state._player_position_y) == Labyrinths.BOXS_PLACE) {
                                    _game_state.setElementAt(_game_state._player_position_x,
                                            _game_state._player_position_y,
                                            Labyrinths.BOX_ON_PLACE);
                                    _game_state._player_place_position_xy = Labyrinths.BOX_ON_PLACE;
                                    _game_state._player_position_y -= napravlenie;
                                    _game_state._player_old_position_y = _game_state._player_position_y;
                                } else {
                                    _game_state.setElementAt(_game_state._player_position_x,
                                            _game_state._player_position_y,
                                            Labyrinths.BOX);
                                    _game_state._player_place_position_xy = Labyrinths.BOX;
                                    _game_state._player_position_y -= napravlenie;
                                    _game_state._player_old_position_y = _game_state._player_position_y;
                                }
                                break;
                            case Labyrinths.BOX_ON_PLACE:
                                switch (_game_state._player_place_position_xy) {
                                    case Labyrinths.IS_EMPTY:
                                    case Labyrinths.BOXS_PLACE:
                                        _game_state.setElementAt(_game_state._player_position_x,
                                                _game_state._player_position_y,
                                                _game_state._player_place_position_xy);
                                        _game_state._player_place_position_xy = _game_state.getElementAt(_game_state._player_old_position_x,
                                                _game_state._player_old_position_y);
                                        _game_state._player_position_y -= napravlenie;
                                        _game_state._player_old_position_y = _game_state._player_position_y;
                                        return;
                                }

                                _game_state.setElementAt(_game_state._player_position_x,
                                        _game_state._player_position_y + napravlenie,
                                        Labyrinths.BOXS_PLACE);
                                if (_game_state.getElementAt(_game_state._player_position_x,
                                        _game_state._player_position_y) == Labyrinths.BOXS_PLACE) {
                                    _game_state.setElementAt(_game_state._player_position_x,
                                            _game_state._player_position_y,
                                            Labyrinths.BOX_ON_PLACE);
                                    _game_state._player_place_position_xy = Labyrinths.BOX_ON_PLACE;
                                    _game_state._player_position_y -= napravlenie;
                                    _game_state._player_old_position_y = _game_state._player_position_y;
                                } else {
                                    _game_state.setElementAt(_game_state._player_position_x,
                                            _game_state._player_position_y,
                                            Labyrinths.BOX);
                                    _game_state._player_place_position_xy = Labyrinths.BOX;
                                    _game_state._player_position_y -= napravlenie;
                                    _game_state._player_old_position_y = _game_state._player_position_y;
                                }
                                break;
                        }
                    }
                    break;
                }
            case Socoban_PMR.DIRECT_LEFT:
                {
                    _game_state._player_old_position_y = _game_state._player_position_y;
                    _game_state._player_position_x -= 1;
                    _game_state._player_next_position_x = _game_state._player_position_x - 1;
                    place_xy = _game_state.getElementAt(_game_state._player_position_x, _game_state._player_position_y);
                    try {
                        place_next_xy = _game_state.getElementAt(_game_state._player_next_position_x,
                                _game_state._player_position_y);
                    } catch (ArrayIndexOutOfBoundsException tt) {
                        place_next_xy = place_xy;
                    }

                    if (_game_state._player_place_position_xy == Labyrinths.BOX_ON_PLACE)
                        _game_state._player_place_position_xy = Labyrinths.BOXS_PLACE;
                    if (_game_state._player_place_position_xy == Labyrinths.BOX)
                        _game_state._player_place_position_xy = Labyrinths.IS_EMPTY;


                    switch (place_xy) {
                        case Labyrinths.IS_EMPTY:
                        case Labyrinths.BOXS_PLACE:
                            _game_state._player_old_position_x = _game_state._player_position_x + 1;
                            _game_state.setElementAt(_game_state._player_old_position_x,
                                    _game_state._player_position_y,
                                    _game_state._player_place_position_xy);
                            _game_state._player_place_position_xy = place_xy;
                            _game_state._counter_move++;   ///-----------------------------------------------------------
                            break;
                        case Labyrinths.WALL:
                            _game_state._player_position_x += 1;
                            _game_state._player_old_position_x = _game_state._player_position_x;
                            _game_state._player_old_position_y = _game_state._player_position_y;
                            break;
                        case Labyrinths.BOX:
                        case Labyrinths.BOX_ON_PLACE:
                            if ((place_next_xy == Labyrinths.BOX) || (place_next_xy == Labyrinths.BOX_ON_PLACE)
                                    || (place_next_xy == Labyrinths.WALL)) {
                                _game_state._player_position_x += 1;
                                _game_state._player_old_position_x = _game_state._player_position_x;
                                _game_state._player_old_position_y = _game_state._player_position_y;
                            } else {
                                _game_state._counter_move++;   ///-----------------------------------------------------------
                                _game_state._player_old_position_x = _game_state._player_position_x + 1;
                                if (_game_state._player_place_position_xy == Labyrinths.BOX)
                                    _game_state._player_place_position_xy = Labyrinths.IS_EMPTY;
                                if (_game_state._player_place_position_xy == Labyrinths.BOX_ON_PLACE)
                                    _game_state._player_place_position_xy = Labyrinths.BOXS_PLACE;
                                _game_state.setElementAt(_game_state._player_old_position_x,
                                        _game_state._player_old_position_y,
                                        _game_state._player_place_position_xy);
                                _game_state._player_place_position_xy = place_xy;
                                if (_game_state._player_place_position_xy == Labyrinths.BOX) {
                                    if (place_next_xy == Labyrinths.BOXS_PLACE)
                                        _game_state.setElementAt(_game_state._player_next_position_x,
                                                _game_state._player_position_y,
                                                Labyrinths.BOX_ON_PLACE);
                                    else
                                        _game_state.setElementAt(_game_state._player_next_position_x,
                                                _game_state._player_position_y,
                                                Labyrinths.BOX);
                                    _game_state.setElementAt(_game_state._player_position_x,
                                            _game_state._player_position_y,
                                            Labyrinths.IS_EMPTY);
                                } else {
                                    if (place_next_xy == Labyrinths.BOXS_PLACE)
                                        _game_state.setElementAt(_game_state._player_next_position_x,
                                                _game_state._player_position_y,
                                                Labyrinths.BOX_ON_PLACE);
                                    else
                                        _game_state.setElementAt(_game_state._player_next_position_x,
                                                _game_state._player_position_y,
                                                Labyrinths.BOX);
                                    _game_state.setElementAt(_game_state._player_position_x,
                                            _game_state._player_position_y,
                                            Labyrinths.BOXS_PLACE);
                                }
                                _game_state._player_old_place_position_xy = place_xy;// _game_state._player_place_position_xy;
                            }

                    }
                    break;
                }
            case Socoban_PMR.DIRECT_RIGHT:
                {
                    _game_state._player_old_position_y = _game_state._player_position_y;
                    _game_state._player_position_x += 1;
                    _game_state._player_next_position_x = _game_state._player_position_x + 1;
                    place_xy = _game_state.getElementAt(_game_state._player_position_x, _game_state._player_position_y);
                    try {
                        place_next_xy = _game_state.getElementAt(_game_state._player_next_position_x,
                                _game_state._player_position_y);
                    } catch (ArrayIndexOutOfBoundsException tt) {
                        place_next_xy = place_xy;
                    }
                    if (_game_state._player_place_position_xy == Labyrinths.BOX_ON_PLACE)
                        _game_state._player_place_position_xy = Labyrinths.BOXS_PLACE;
                    if (_game_state._player_place_position_xy == Labyrinths.BOX)
                        _game_state._player_place_position_xy = Labyrinths.IS_EMPTY;

                    switch (place_xy) {
                        case Labyrinths.IS_EMPTY:
                        case Labyrinths.BOXS_PLACE:
                            _game_state._player_old_position_x = _game_state._player_position_x - 1;
                            _game_state.setElementAt(_game_state._player_old_position_x,
                                    _game_state._player_position_y,
                                    _game_state._player_place_position_xy);
                            _game_state._player_place_position_xy = place_xy;
                            _game_state._counter_move++;   ///-----------------------------------------------------------
                            break;
                        case Labyrinths.WALL:
                            _game_state._player_position_x -= 1;
                            _game_state._player_old_position_x = _game_state._player_position_x;
                            _game_state._player_old_position_y = _game_state._player_position_y;
                            break;
                        case Labyrinths.BOX:
                        case Labyrinths.BOX_ON_PLACE:
                            if ((place_next_xy == Labyrinths.BOX) || (place_next_xy == Labyrinths.BOX_ON_PLACE)
                                    || (place_next_xy == Labyrinths.WALL)) {
                                _game_state._player_position_x -= 1;
                                _game_state._player_old_position_x = _game_state._player_position_x;
                                _game_state._player_old_position_y = _game_state._player_position_y;
                            } else {
                                _game_state._counter_move++;   ///-----------------------------------------------------------
                                _game_state._player_old_position_x = _game_state._player_position_x - 1;
                                if (_game_state._player_place_position_xy == Labyrinths.BOX)
                                    _game_state._player_place_position_xy = Labyrinths.IS_EMPTY;

                                if (_game_state._player_place_position_xy == Labyrinths.BOX_ON_PLACE)
                                    _game_state._player_place_position_xy = Labyrinths.BOXS_PLACE;
                                _game_state.setElementAt(_game_state._player_old_position_x,
                                        _game_state._player_position_y,
                                        _game_state._player_place_position_xy);
                                _game_state._player_place_position_xy = place_xy;
                                if (_game_state._player_place_position_xy == Labyrinths.BOX) {
                                    if (place_next_xy == Labyrinths.BOXS_PLACE)
                                        _game_state.setElementAt(_game_state._player_next_position_x,
                                                _game_state._player_position_y,
                                                Labyrinths.BOX_ON_PLACE);
                                    else
                                        _game_state.setElementAt(_game_state._player_next_position_x,
                                                _game_state._player_position_y,
                                                Labyrinths.BOX);
                                    _game_state.setElementAt(_game_state._player_position_x,
                                            _game_state._player_position_y,
                                            Labyrinths.IS_EMPTY);
                                } else {
                                    if (place_next_xy == Labyrinths.BOXS_PLACE)
                                        _game_state.setElementAt(_game_state._player_next_position_x,
                                                _game_state._player_position_y,
                                                Labyrinths.BOX_ON_PLACE);
                                    else
                                        _game_state.setElementAt(_game_state._player_next_position_x,
                                                _game_state._player_position_y,
                                                Labyrinths.BOX);
                                    _game_state.setElementAt(_game_state._player_position_x,
                                            _game_state._player_position_y,
                                            Labyrinths.BOXS_PLACE);
                                }
                                _game_state._player_old_place_position_xy = place_xy;// _game_state._player_place_position_xy;
                            }

                    }
                    break;
                }
            case Socoban_PMR.DIRECT_DOWN:
                {
                    _game_state._player_old_position_x = _game_state._player_position_x;
                    _game_state._player_position_y += 1;
                    _game_state._player_next_position_y = _game_state._player_position_y + 1;
                    place_xy = _game_state.getElementAt(_game_state._player_position_x, _game_state._player_position_y);
                    try {
                        place_next_xy = _game_state.getElementAt(_game_state._player_position_x,
                                _game_state._player_next_position_y);
                    } catch (ArrayIndexOutOfBoundsException tt) {
                        place_next_xy = place_xy;
                    }
                    if (_game_state._player_place_position_xy == Labyrinths.BOX_ON_PLACE)
                        _game_state._player_place_position_xy = Labyrinths.BOXS_PLACE;
                    if (_game_state._player_place_position_xy == Labyrinths.BOX)
                        _game_state._player_place_position_xy = Labyrinths.IS_EMPTY;


                    switch (place_xy) {
                        case Labyrinths.IS_EMPTY:
                        case Labyrinths.BOXS_PLACE:
                            _game_state._player_old_position_y = _game_state._player_position_y - 1;
                            _game_state.setElementAt(_game_state._player_position_x,
                                    _game_state._player_old_position_y,
                                    _game_state._player_place_position_xy);
                            _game_state._player_place_position_xy = place_xy;
                            _game_state._counter_move++;   ///-----------------------------------------------------------
                            break;
                        case Labyrinths.WALL:
                            _game_state._player_position_y -= 1;
                            _game_state._player_old_position_y = _game_state._player_position_y;
                            _game_state._player_old_position_x = _game_state._player_position_x;
                            break;
                        case Labyrinths.BOX:
                        case Labyrinths.BOX_ON_PLACE:
                            if ((place_next_xy == Labyrinths.BOX) || (place_next_xy == Labyrinths.BOX_ON_PLACE)
                                    || (place_next_xy == Labyrinths.WALL)) {
                                _game_state._player_position_y -= 1;
                                _game_state._player_old_position_y = _game_state._player_position_y;
                                _game_state._player_old_position_x = _game_state._player_position_x;
                            } else {
                                _game_state._counter_move++;   ///-----------------------------------------------------------
                                _game_state._player_old_position_y = _game_state._player_position_y - 1;
                                if (_game_state._player_place_position_xy == Labyrinths.BOX)
                                    _game_state._player_place_position_xy = Labyrinths.IS_EMPTY;
                                if (_game_state._player_place_position_xy == Labyrinths.BOX_ON_PLACE)
                                    _game_state._player_place_position_xy = Labyrinths.BOXS_PLACE;
                                _game_state.setElementAt(_game_state._player_position_x,
                                        _game_state._player_old_position_y,
                                        _game_state._player_place_position_xy);
                                _game_state._player_place_position_xy = place_xy;
                                if (_game_state._player_place_position_xy == Labyrinths.BOX) {
                                    if (place_next_xy == Labyrinths.BOXS_PLACE)
                                        _game_state.setElementAt(_game_state._player_position_x,
                                                _game_state._player_next_position_y,
                                                Labyrinths.BOX_ON_PLACE);
                                    else
                                        _game_state.setElementAt(_game_state._player_position_x,
                                                _game_state._player_next_position_y,
                                                Labyrinths.BOX);
                                    _game_state.setElementAt(_game_state._player_position_x,
                                            _game_state._player_position_y,
                                            Labyrinths.IS_EMPTY);
                                } else {
                                    if (place_next_xy == Labyrinths.BOXS_PLACE)
                                        _game_state.setElementAt(_game_state._player_position_x,
                                                _game_state._player_next_position_y,
                                                Labyrinths.BOX_ON_PLACE);
                                    else
                                        _game_state.setElementAt(_game_state._player_position_x,
                                                _game_state._player_next_position_y,
                                                Labyrinths.BOX);
                                    _game_state.setElementAt(_game_state._player_position_x,
                                            _game_state._player_position_y,
                                            Labyrinths.BOXS_PLACE);
                                }
                                _game_state._player_old_place_position_xy = place_xy;// _game_state._player_place_position_xy;

                            }

                    }
                    break;
                }
            case Socoban_PMR.DIRECT_UP:
                {
                    _game_state._player_old_position_x = _game_state._player_position_x;
                    _game_state._player_position_y -= 1;
                    _game_state._player_next_position_y = _game_state._player_position_y - 1;
                    place_xy = _game_state.getElementAt(_game_state._player_position_x, _game_state._player_position_y);
                    try {
                        place_next_xy = _game_state.getElementAt(_game_state._player_position_x,
                                _game_state._player_next_position_y);
                    } catch (ArrayIndexOutOfBoundsException tt) {
                        place_next_xy = place_xy;
                    }
                    if (_game_state._player_place_position_xy == Labyrinths.BOX_ON_PLACE)
                        _game_state._player_place_position_xy = Labyrinths.BOXS_PLACE;
                    if (_game_state._player_place_position_xy == Labyrinths.BOX)
                        _game_state._player_place_position_xy = Labyrinths.IS_EMPTY;


                    switch (place_xy) {
                        case Labyrinths.IS_EMPTY:
                        case Labyrinths.BOXS_PLACE:
                            _game_state._player_old_position_y = _game_state._player_position_y + 1;
                            _game_state.setElementAt(_game_state._player_position_x,
                                    _game_state._player_old_position_y,
                                    _game_state._player_place_position_xy);
                            _game_state._player_place_position_xy = place_xy;
                            _game_state._counter_move++;   ///-----------------------------------------------------------
                            break;
                        case Labyrinths.WALL:
                            _game_state._player_position_y += 1;
                            _game_state._player_old_position_y = _game_state._player_position_y;
                            _game_state._player_old_position_x = _game_state._player_position_x;
                            break;
                        case Labyrinths.BOX:
                        case Labyrinths.BOX_ON_PLACE:
                            if ((place_next_xy == Labyrinths.BOX) || (place_next_xy == Labyrinths.BOX_ON_PLACE)
                                    || (place_next_xy == Labyrinths.WALL)) {
                                _game_state._player_position_y += 1;
                                _game_state._player_old_position_y = _game_state._player_position_y;
                                _game_state._player_old_position_x = _game_state._player_position_x;
                            } else {
                                _game_state._counter_move++;   ///-----------------------------------------------------------
                                _game_state._player_old_position_y = _game_state._player_position_y + 1;
                                if (_game_state._player_place_position_xy == Labyrinths.BOX)
                                    _game_state._player_place_position_xy = Labyrinths.IS_EMPTY;
                                if (_game_state._player_place_position_xy == Labyrinths.BOX_ON_PLACE)
                                    _game_state._player_place_position_xy = Labyrinths.BOXS_PLACE;
                                _game_state.setElementAt(_game_state._player_position_x,
                                        _game_state._player_old_position_y,
                                        _game_state._player_place_position_xy);
                                _game_state._player_place_position_xy = place_xy;
                                if (_game_state._player_place_position_xy == Labyrinths.BOX) {
                                    if (place_next_xy == Labyrinths.BOXS_PLACE)
                                        _game_state.setElementAt(_game_state._player_position_x,
                                                _game_state._player_next_position_y,
                                                Labyrinths.BOX_ON_PLACE);
                                    else
                                        _game_state.setElementAt(_game_state._player_position_x,
                                                _game_state._player_next_position_y,
                                                Labyrinths.BOX);
                                    _game_state.setElementAt(_game_state._player_position_x,
                                            _game_state._player_position_y,
                                            Labyrinths.IS_EMPTY);
                                } else {
                                    if (place_next_xy == Labyrinths.BOXS_PLACE)
                                        _game_state.setElementAt(_game_state._player_position_x,
                                                _game_state._player_next_position_y,
                                                Labyrinths.BOX_ON_PLACE);
                                    else
                                        _game_state.setElementAt(_game_state._player_position_x,
                                                _game_state._player_next_position_y,
                                                Labyrinths.BOX);
                                    _game_state.setElementAt(_game_state._player_position_x,
                                            _game_state._player_position_y,
                                            Labyrinths.BOXS_PLACE);
                                }
                                _game_state._player_old_place_position_xy = place_xy;
                            }
                    }
                    break;
                }
           }
   if (ifNextLevel()) {
               _game_state._player_state = Socoban_GSR.PLAYSTATE_WON;
               // _game_state.        ;
               return;
        }

    }

    public boolean isLoadSaveSupporting() {
        return true;
    }
}

