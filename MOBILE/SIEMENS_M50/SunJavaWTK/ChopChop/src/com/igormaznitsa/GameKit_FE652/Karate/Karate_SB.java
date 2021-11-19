package com.igormaznitsa.GameKit_FE652.Karate;

import com.igormaznitsa.GameAPI.StrategicBlock;
import com.igormaznitsa.GameAPI.GameStateRecord;
import com.igormaznitsa.GameAPI.PlayerBlock;

import java.io.*;

public class Karate_SB implements StrategicBlock
{
    public static final int LEVEL0 = 0;
    public static final int LEVEL1 = 1;
    public static final int LEVEL2 = 2;

    public static final int FIELD_WIDTH = 101;
    public static final int FIELD_HEIGHT = 64;

    protected Karate_GSR _game_state = null;

    protected PlayerBlock _player_block = null;
    protected PlayerBlock _ai_block = null;

    public Karate_SB()
    {
    }

    public GameStateRecord getGameStateRecord()
    {
        return _game_state;
    }

    public void saveGameState(OutputStream outputStream) throws IOException
    {
        System.gc();
        DataOutputStream dos = new DataOutputStream(outputStream);

        dos.writeByte(_game_state._level);
        dos.writeByte(_game_state._stage);
        dos.writeInt(_game_state._scores);

        // Save player's state
        KaratekerLeft _pp = _game_state._player_karateker;

        dos.writeByte(_pp._cur_state);
        dos.writeShort(_pp._cur_life);
        dos.writeByte(_pp._cur_frame);
        dos.writeByte(_pp._cur_tick);
        dos.writeByte(_pp._cur_move_len);

        // Save opponent's state

        KaratekerRight _pr = _game_state._ai_karateker;

        dos.writeByte(_pr._cur_state);
        dos.writeShort(_pr._cur_life);
        dos.writeByte(_pr._cur_frame);
        dos.writeByte(_pr._cur_tick);
        dos.writeByte(_pr._cur_move_len);

        dos = null;
        System.gc();
    }

    public void loadGameState(InputStream inputStream) throws IOException
    {
        System.gc();
        DataInputStream dos = new DataInputStream(inputStream);

        int lvl =  dos.readByte();
        int stt = dos.readByte();

        _game_state = null;
        System.gc();
        _game_state = new Karate_GSR(lvl);
        _game_state.initStage(stt);

        _game_state._scores = dos.readInt();

        // Read player's state
        KaratekerLeft _pp = _game_state._player_karateker;

        int st = dos.readByte();
        _pp.setState(st);

        _pp._cur_life = dos.readShort();
        _pp._cur_frame = dos.readByte();
        _pp._cur_tick = dos.readByte();
        _pp._cur_move_len = dos.readByte();

        // Save opponent's state

        KaratekerRight _pr = _game_state._ai_karateker;

        st = dos.readByte();
        _pr.setState(st);

        _pr._cur_life = dos.readShort();
        _pr._cur_frame = dos.readByte();
        _pr._cur_tick = dos.readByte();
        _pr._cur_move_len = dos.readByte();

        dos = null;
        System.gc();
    }

    public void newGame(int level)
    {
        _game_state = new Karate_GSR(level);

        setAIBlock(_game_state._aiplayer);

        if (_player_block!=null) _player_block.initPlayer();
        if (_ai_block!=null) _ai_block.initPlayer();
    }

    protected boolean checkIntersect(int x1,int y1,int w1,int h1,int x2,int y2,int w2,int h2)
    {
        return !((x1 + w1 <= x2) || (y1 + h1 <= y2) || (x1 >= x2 + w2) || (y1 >= y2 + h2));
    }

    public void nextGameStep()
    {
        if (_player_block == null) return;
        Karate_PMR _move = (Karate_PMR) _player_block.getPlayerMoveRecord(_game_state);

        boolean _max_phase1 = processKarateker(_move.getButton(), _game_state._player_karateker);

        if ((_game_state._player_karateker._cur_state == Karateker.STATE_DIED)&&_max_phase1)
        {
            _game_state._player_state = Karate_GSR.PLAYERSTATE_LOST;
            _game_state._ai_state = Karate_GSR.PLAYERSTATE_WON;
            _game_state._game_state= Karate_GSR.GAMESTATE_OVER;
            return;
        }

        if (_ai_block == null) return;
        _move = (Karate_PMR) _ai_block.getPlayerMoveRecord(_game_state);

        boolean _max_phase2 = processKarateker(_move.getButton(), _game_state._ai_karateker);

        if ((_game_state._ai_karateker._cur_state == Karateker.STATE_DIED)&&_max_phase2)
        {
            _game_state._player_state = Karate_GSR.PLAYERSTATE_WON;
            _game_state._ai_state = Karate_GSR.PLAYERSTATE_LOST;
            return;
        }

        if (_max_phase1)
        {
            SenseBreakingZone[] _beat_zone = _game_state.getPlayerPerson().getBreakingZone();
            SenseBreakingZone[] _sense_zone = _game_state.getAIPerson().getSenseZone();

            int xoff = _game_state._player_karateker.getX();
            int axoff = _game_state._ai_karateker.getX();

            int _damage = 0;

            for (int li = 0; li < _beat_zone.length; li++)
            {
                SenseBreakingZone _cur_beat = _beat_zone[li];
                int bx = _cur_beat._x +xoff;

                for (int lii = 0; lii < _sense_zone.length; lii++)
                {
                    SenseBreakingZone _cur_sense = _sense_zone[lii];
                    if (checkIntersect(bx,_cur_beat._y,_cur_beat._width,_cur_beat._height,axoff+_cur_sense._x,_cur_sense._y,_cur_sense._width,_cur_sense._height))
                        _damage = _damage + ((_cur_beat._sensetivity * _cur_sense._sensetivity)>>1);
                }
            }

            if (_damage>0) _game_state._scores ++;
            _game_state._ai_karateker._cur_life -= _damage;
            if (_game_state._ai_karateker._cur_life<0) _game_state._ai_karateker._cur_life=0;
        }

        if (_max_phase2)
        {
            SenseBreakingZone[] _beat_zone = _game_state.getAIPerson().getBreakingZone();
            SenseBreakingZone[] _sense_zone = _game_state.getPlayerPerson().getSenseZone();

            int axoff = _game_state._player_karateker.getX();
            int xoff = _game_state._ai_karateker.getX();

            int _damage = 0;

            for (int li = 0; li < _beat_zone.length; li++)
            {
                SenseBreakingZone _cur_beat = _beat_zone[li];
                int bx = _cur_beat._x +xoff;

                for (int lii = 0; lii < _sense_zone.length; lii++)
                {
                    SenseBreakingZone _cur_sense = _sense_zone[lii];
                    if (checkIntersect(bx,_cur_beat._y,_cur_beat._width,_cur_beat._height,axoff+_cur_sense._x,_cur_sense._y,_cur_sense._width,_cur_sense._height))
                        _damage = _damage + ((_cur_beat._sensetivity * _cur_sense._sensetivity)>>1);
                }
            }

            _game_state._player_karateker._cur_life -= _damage;
            if (_game_state._player_karateker._cur_life<0) _game_state._player_karateker._cur_life=0;
        }

    }

    protected boolean processKarateker(int button, Karateker karateker)
    {
        if (karateker._cur_state == Karateker.STATE_STAND)
        {
            if (karateker._cur_life == 0)
            {
                karateker.setState(Karateker.STATE_DIED);
                return false;
            }

            switch (button)
            {
                case Karate_PMR.BUTTON_NONE:
                    ;
                    break;
                case Karate_PMR.BUTTON_LEFT:
                    {
                        int dx = karateker.getX();
                        dx -= Karateker.HOR_MOVE_SPEED;

                        int cdx = dx + (Karateker.FRAME_WIDTH >> 1);

                        if (karateker.equals(_game_state._player_karateker))
                        {
                            // Left karateker
                            if (dx >= 0)
                            {
                                karateker.setState(Karateker.STATE_MOVELEFT);
                            }
                        }
                        else
                        {
                            // Right karateker
                            if ((cdx - _game_state._player_karateker._xpos-(Karateker.FRAME_WIDTH>>1))>(Karateker.FRAME_WIDTH>>3))
                            {
                                karateker.setState(Karateker.STATE_MOVELEFT);
                            }
                        }
                    }
                    ;
                    break;
                case Karate_PMR.BUTTON_RIGHT:
                    {
                        int dx = karateker.getX();
                        dx += Karateker.HOR_MOVE_SPEED;
                        int cdx = dx + (Karateker.FRAME_WIDTH >> 1);

                        if (karateker.equals(_game_state._player_karateker))
                        {
                            // Left karateker
                            if ((_game_state._ai_karateker._xpos+(Karateker.FRAME_WIDTH>>1)) - cdx >(Karateker.FRAME_WIDTH>>3))
                            {
                                karateker.setState(Karateker.STATE_MOVERIGHT);
                            }
                        }
                        else
                        {
                            // Right karateker
                            if (dx+Karateker.FRAME_WIDTH < FIELD_WIDTH)
                            {
                                karateker.setState(Karateker.STATE_MOVERIGHT);
                            }
                        }
                    }
                    ;
                    break;
                case Karate_PMR.BUTTON_BLOCKTOP:
                    {
                        karateker.setState(Karateker.STATE_BLOCKTOP);
                    }
                    ;
                    break;
                case Karate_PMR.BUTTON_BLOCKMID:
                    {
                        karateker.setState(Karateker.STATE_BLOCKMID);
                    }
                    ;
                    break;
                case Karate_PMR.BUTTON_BLOCKDOWN:
                    {
                        karateker.setState(Karateker.STATE_BLOCKDOWN);
                    }
                    ;
                    break;
                case Karate_PMR.BUTTON_PUNCHTOP:
                    {
                        karateker.setState(Karateker.STATE_PUNCHTOP);
                    }
                    ;
                    break;
                case Karate_PMR.BUTTON_PUNCHMID:
                    {
                        karateker.setState(Karateker.STATE_PUNCHMID);
                    }
                    ;
                    break;
                case Karate_PMR.BUTTON_PUNCHDOWN:
                    {
                        karateker.setState(Karateker.STATE_PUNCHDOWN);
                    }
                    ;
                    break;
            }
        }
        return karateker.processAnimation();
    }

    public void initStage(int num)
    {
        _game_state.initStage(num);
    }

    public void setAIBlock(PlayerBlock aiBlock)
    {
        _ai_block = aiBlock;
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
    public int getMaxSizeOfSavedGameBlock()
    {
        return 800;
    }

}
