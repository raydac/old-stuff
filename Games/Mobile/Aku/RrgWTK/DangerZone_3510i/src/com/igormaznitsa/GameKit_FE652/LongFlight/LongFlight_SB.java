package com.igormaznitsa.GameKit_FE652.LongFlight;

import com.igormaznitsa.GameAPI.GameStateRecord;
import com.igormaznitsa.GameAPI.Utils.RndGenerator;

import java.io.*;

public class LongFlight_SB
{
    public static final int BUTTON_NONE = 0;
    public static final int BUTTON_UP = 1;
    public static final int BUTTON_DOWN = 2;
    public static final int BUTTON_BOMB = 3;

    public static final int LEVEL0 = 0;
    public static final int LEVEL0_BOMBS = 30;
    public static final int LEVEL0_SHELLS = 10;
    public static final int LEVEL0_ROCKETS = 10;
    public static final int LEVEL0_ATTEMPTS = 4;

    public static final int LEVEL1 = 1;
    public static final int LEVEL1_BOMBS = 40;
    public static final int LEVEL1_SHELLS = 10;
    public static final int LEVEL1_ROCKETS = 10;
    public static final int LEVEL1_ATTEMPTS = 3;

    public static final int LEVEL2 = 2;
    public static final int LEVEL2_BOMBS = 50;
    public static final int LEVEL2_SHELLS = 10;
    public static final int LEVEL2_ROCKETS = 10;
    public static final int LEVEL2_ATTEMPTS = 3;

    public static final int SPRITE_WIDTH = 10;
    public static final int SPRITE_HEIGHT = 10;

    public static final int PLAYER_SPRITE_WIDTH = 20;
    public static final int PLAYER_SPRITE_HEIGHT = 10;

    public static final int PLAYER_OFF_X = 0;
    public static final int PLAYER_OFF_Y = 3;
    public static final int PLAYER_ZONE_WIDTH = PLAYER_SPRITE_WIDTH;
    public static final int PLAYER_ZONE_HEIGHT = 5;

    public static final int BOMB_SPRITE_WIDTH = 4;
    public static final int BOMB_SPRITE_HEIGHT = 2;

    public static final int SHELL_SPRITE_SIDE = 2;

    public static final int ROCKET_SPRITE_WIDTH = 10;
    public static final int ROCKET_SPRITE_HEIGHT = 4;

    public static final int SCREEN_WIDTH = 101;
    public static final int SCREEN_HEIGHT = 65;

    public static final int OBJECTS_ON_SCREEN = SCREEN_WIDTH / SPRITE_WIDTH;

    public static final int PLAYER_X_SPEED = 1;
    public static final int PLAYER_Y_SPEED = 3;

    public static final int SHELL_X_SPEED = PLAYER_X_SPEED + 1;
    public static final int SHELL_Y_SPEED_8 = 0x100;

    public static final int BOMB_X_SPEED_8 = 0;
    public static final int BOMB_Y_SPEED_8 = 0x100;

    public static final int ROCKET_X_SPEED = 4;
    public static final int ROCKET_Y_SPEED_8 = 0x050;

    public static final int PLAYER_X_POSITION = 2;
    public static final int PLAYER_X_OFFSET = SPRITE_WIDTH * PLAYER_X_POSITION;
    public static final int PLAYER_INIT_Y_OFFSET = SCREEN_HEIGHT - SPRITE_HEIGHT * 6;

    private static final int ROCKET_DISTANCE = 12;
    private static final int SHELL_DISTANCE0 = 3;
    private static final int SHELL_DISTANCE1 = 5;
    private static final int SHELL_DISTANCE2 = 7;

    protected static final int SHELL_FREQ_LEVEL0 = 4;
    protected static final int SHELL_FREQ_LEVEL1 = 3;
    protected static final int SHELL_FREQ_LEVEL2 = 2;

    private static final int NEXT_BOMB_DELAY = (SPRITE_WIDTH / PLAYER_X_SPEED)>>1;

    private static final int SCORES_ANTIAIRGUN = 1;
    private static final int SCORES_LAUNCHER = 2;
    private static final int SCORES_FACTORY = 5;
    private static final int SCORES_HOUSE = -1;
    private static final int SCORES_TREE = -1;

    private LongFlight_GSR _game_state = null;
    public int i_Button = BUTTON_NONE;

    private int bomb_ticks = 0;
    private int _active_bomb = 0;

    private RndGenerator _rnd = new RndGenerator(System.currentTimeMillis());

    public void saveGameState(OutputStream outputStream) throws IOException
    {
       System.gc();
       DataOutputStream dos = new DataOutputStream(outputStream);

       dos.writeByte(_game_state._cur_level);
       dos.writeByte(_game_state._cur_stage);
       dos.writeByte(_game_state._cur_bombs);
       dos.writeByte(_game_state._cur_factory);
       dos.writeShort(_game_state._player_pos_y);
       dos.writeInt(_game_state._player_abs_pos_x);
       dos.writeShort(_game_state._player_scores);
       dos.writeByte(_game_state._attemptions);

        for(int li=0;li<_game_state._way.length;li++)
        {
            dos.writeByte(_game_state._way[li]);
        }

        for(int li=0;li<_game_state._rockets.length;li++)
        {
            ExplObj obj = _game_state._rockets[li];
            dos.writeBoolean(obj._active);
            dos.writeBoolean(obj._explode);
            dos.writeByte(obj._frame);
            dos.writeByte(obj._tick);
            dos.writeShort(obj._x);
            dos.writeShort(obj._y);
        }

        for(int li=0;li<_game_state._shells.length;li++)
        {
            ExplObj obj = _game_state._shells[li];
            dos.writeBoolean(obj._active);
            dos.writeBoolean(obj._explode);
            dos.writeByte(obj._frame);
            dos.writeByte(obj._tick);
            dos.writeShort(obj._x);
            dos.writeShort(obj._y);
        }
       dos = null;
       System.gc();
    }

    public void loadGameState(InputStream inputStream) throws IOException
    {
        System.gc();
        DataInputStream dos = new DataInputStream(inputStream);

        int lvl = dos.readByte();
        int stage = dos.readByte();

        _game_state = null;
        System.gc();
        _game_state = new LongFlight_GSR(lvl,stage);

        _game_state._cur_bombs = dos.readByte();
        _game_state._cur_factory = dos.readByte();
        _game_state._player_pos_y = dos.readShort();
        _game_state._player_abs_pos_x = dos.readInt();
        _game_state._player_scores = dos.readShort();
        _game_state._attemptions = dos.readByte();

         for(int li=0;li<_game_state._way.length;li++)
         {
             _game_state._way [li] = dos.readByte();
         }

         for(int li=0;li<_game_state._rockets.length;li++)
         {
             ExplObj obj = _game_state._rockets[li];
             obj._active =  dos.readBoolean();
             obj._explode = dos.readBoolean();
             obj._frame = dos.readByte();
             obj._tick = dos.readByte();
             obj._x =  dos.readShort();
             obj._y = dos.readShort();
         }

        for(int li=0;li<_game_state._shells.length;li++)
        {
            ExplObj obj = _game_state._shells[li];
            obj._active =  dos.readBoolean();
            obj._explode = dos.readBoolean();
            obj._frame = dos.readByte();
            obj._tick = dos.readByte();
            obj._x =  dos.readShort();
            obj._y = dos.readShort();
        }
        dos = null;
        Runtime.getRuntime().gc();
    }

    public void initStage(int stage)
    {
        _game_state._game_state=LongFlight_GSR.GAMESTATE_PLAYED;
        _game_state._player_state = LongFlight_GSR.PLAYERSTATE_NORMAL;
        _game_state.initWayArray(stage);
        _game_state.initAmmo();
    }

    public void newGame(int level)
    {
        _game_state = new LongFlight_GSR(level,0);
    }

    public void nextGameStep()
    {
        if (bomb_ticks != 0)
        {
            bomb_ticks++;
            if (bomb_ticks > NEXT_BOMB_DELAY) bomb_ticks = 0;
        }

        boolean death = false;

        if (checkCrash(LongFlight_SB.PLAYER_X_OFFSET+PLAYER_OFF_X, _game_state._player_pos_y+PLAYER_OFF_Y, LongFlight_SB.PLAYER_ZONE_WIDTH, LongFlight_SB.PLAYER_ZONE_HEIGHT))
        {
            _game_state._deathstate = LongFlight_GSR.DEATHSTATE_CRASH;
            death = true;
        }

        ExplObj obj = checkShells();
        if (obj != null)
        {
            _game_state._deathstate = LongFlight_GSR.DEATHSTATE_BATTERED;
            death = true;
        }

        obj = checkRockets();
        if (obj != null)
        {
            _game_state._deathstate = LongFlight_GSR.DEATHSTATE_EXPLOSED;
            death = true;
        }

        if (death)
        {
            _game_state._attemptions--;
            if (_game_state._attemptions < 0)
            {
                _game_state._game_state = LongFlight_GSR.GAMESTATE_OVER;
            }
            _game_state._player_state = LongFlight_GSR.PLAYERSTATE_KILLED;
            return;
        }


        _game_state._player_abs_pos_x += LongFlight_SB.PLAYER_X_SPEED;

        if ((_game_state._player_abs_pos_x / LongFlight_SB.SPRITE_WIDTH) >= _game_state._way.length) _game_state._player_abs_pos_x -= LongFlight_SB.SPRITE_WIDTH * _game_state._way.length;

        switch (i_Button)
        {
            case LongFlight_SB.BUTTON_NONE:
                {
                }
                ;
                break;
            case LongFlight_SB.BUTTON_DOWN:
                {
                    _game_state._player_pos_y += PLAYER_Y_SPEED;
                }
                ;
                break;
            case LongFlight_SB.BUTTON_UP:
                {
                    if (_game_state._player_pos_y > PLAYER_INIT_Y_OFFSET)
                    {
                        _game_state._player_pos_y -= PLAYER_Y_SPEED;
                    }
                }
                ;
                break;
            case LongFlight_SB.BUTTON_BOMB:
                {
                    generateBomb();
                }
                ;
                break;
        }

        processBombs();
        processShells();
        processRockets();

        int pos = _game_state._player_abs_pos_x / SPRITE_WIDTH;
        boolean ton = false;
        if ((_game_state._player_abs_pos_x % SPRITE_WIDTH) == 0) ton = true;

        if (ton)
        {
            if ((_game_state.getElementAtPos(pos + ROCKET_DISTANCE) & 0xF0) == Way.OBJ_LAUNCER)
            {
                generateRocket();
            }

            int el = _game_state.getElementAtPos(pos + SHELL_DISTANCE0);
            if ((el & 0xF0) == Way.OBJ_ANTIAIRGUN)
            {
                generateShell((el & 0x0F) + 1,SHELL_DISTANCE0+1);
            }
            el = _game_state.getElementAtPos(pos + SHELL_DISTANCE1);
            if ((el & 0xF0) == Way.OBJ_ANTIAIRGUN)
            {
                generateShell((el & 0x0F) + 1,SHELL_DISTANCE1+1);
            }
            el = _game_state.getElementAtPos(pos + SHELL_DISTANCE2);
            if ((el & 0xF0) == Way.OBJ_ANTIAIRGUN)
            {
                generateShell((el & 0x0F) + 1,SHELL_DISTANCE2+1);
            }
        }

        if (_game_state._cur_factory == _game_state._max_factory)
        {
            _game_state._player_state = LongFlight_GSR.PLAYERSTATE_WON;
            _game_state._game_state = LongFlight_GSR.GAMESTATE_OVER;
        }
        else if ((_active_bomb == 0) && (_game_state._cur_bombs == 0))
        {
            _game_state._attemptions--;
            if (_game_state._attemptions < 0)
            {
                _game_state._game_state = LongFlight_GSR.GAMESTATE_OVER;
            }
            _game_state._player_state = LongFlight_GSR.PLAYERSTATE_OUTOFAMMO;
        }
    }

    private int getAltInIndex(int ele)
    {
        int hght = LongFlight_SB.SCREEN_HEIGHT - ((ele & 0x0F) * SPRITE_HEIGHT);
        return hght;
    }

    public int getElementAtScrXY(int x, int y)
    {
        int el1 = _game_state.getElementAtPos(getXAtScrCoord(x));
        int h = getAltInIndex(el1);
        switch (el1 & 0xF0)
        {
            case Way.OBJ_NONE:
            case Way.OBJ_CRATER:
                ;
                break;
            default :
                h -= SPRITE_HEIGHT;
        }
        if (h <= y) return el1; else return -1;
    }

    public int getXAtScrCoord(int x)
    {
        int dx = x - LongFlight_SB.PLAYER_X_OFFSET;
        int posx = ((_game_state._player_abs_pos_x + dx) / LongFlight_SB.SPRITE_WIDTH) - 1;
        if (posx >= _game_state._way.length) posx -= _game_state._way.length;
        return posx;
    }

    private int getScrXFromIndex(int indx)
    {
        int posabs = ((indx + 1) * SPRITE_WIDTH) - _game_state._player_abs_pos_x + LongFlight_SB.PLAYER_X_OFFSET;
        return posabs;
    }

    private ExplObj getInactiveBomb()
    {
        for (int li = 0; li < _game_state._bombs.length; li++)
        {
            if (!_game_state._bombs[li]._active) return _game_state._bombs[li];
        }
        return null;
    }

    private ExplObj getInactiveShell()
    {
        for (int li = 0; li < _game_state._shells.length; li++)
        {
            if (!_game_state._shells[li]._active) return _game_state._shells[li];
        }
        return null;
    }

    private ExplObj getInactiveRocket()
    {
        for (int li = 0; li < _game_state._rockets.length; li++)
        {
            if (!_game_state._rockets[li]._active) return _game_state._rockets[li];
        }
        return null;
    }

    private void generateBomb()
    {
        if ((bomb_ticks != 0) || (_game_state._cur_bombs == 0)) return;
        ExplObj obj = getInactiveBomb();
        if (obj == null) return;

        obj.init(LongFlight_SB.PLAYER_X_OFFSET + ((LongFlight_SB.PLAYER_SPRITE_WIDTH - LongFlight_SB.BOMB_SPRITE_WIDTH) >> 1), _game_state._player_pos_y + PLAYER_SPRITE_HEIGHT);
        bomb_ticks = 1;
        _game_state._cur_bombs--;
    }

    private void generateShell(int alt,int distance)
    {
        if (_rnd.getInt(_game_state._cur_shellfreq)!=(_game_state._cur_shellfreq>>1)) return;

        ExplObj obj = getInactiveShell();
        if (obj == null) return;

        obj.init(LongFlight_SB.PLAYER_X_OFFSET + distance * LongFlight_SB.SPRITE_WIDTH, LongFlight_SB.SCREEN_HEIGHT - alt * LongFlight_SB.SPRITE_HEIGHT);
    }

    private void generateRocket()
    {
        ExplObj obj = getInactiveRocket();
        if (obj == null) return;

        obj.init(LongFlight_SB.ROCKET_DISTANCE * LongFlight_SB.SPRITE_WIDTH, LongFlight_SB.PLAYER_INIT_Y_OFFSET);
    }

    private void processBombs()
    {
        ExplObj[] bombs = _game_state._bombs;

        _active_bomb = 0;
        for (int li = 0; li < bombs.length; li++)
        {
            ExplObj bmb = bombs[li];
            if (bmb._active)
            {
                _active_bomb++;
                if (bmb._explode)
                {
                    bmb.processAnimation();
                    bmb._x -= PLAYER_X_SPEED;
                    if (bmb._x < (0 - BOMB_SPRITE_WIDTH)) bmb._active = false;
                }
                else
                {
                    bmb._y += BOMB_Y_SPEED_8;

                    if (bmb.getY() >= SCREEN_HEIGHT)
                    {
                        bmb._active = false;
                    }

                    int getx = getXAtScrCoord(bmb._x);
                    int ele = _game_state.getElementAtPos(getx);
                    int hght = getAltInIndex(ele);

                    int hght2 =hght;

                    if ((ele&0xF0)!=Way.OBJ_NONE)
                    {
                        hght2 -= SPRITE_HEIGHT;
                    }



















































                    if (hght2 <= bmb.getY())
                    {
                        hght -= SPRITE_HEIGHT;
                        bmb._x = getScrXFromIndex(getx);
                        bmb._y = hght << 8;
                        bmb._explode = true;
                        switch (ele & 0xF0)
                        {
                            case Way.OBJ_LEFTSLANT:
                            case Way.OBJ_RIGHTSLANT:
                            {
                              bmb._y -= ((SPRITE_HEIGHT>>1)<<8);
                            };break;
                            case Way.OBJ_FACTORY:
                                _game_state._cur_factory++;
                            default :
                                _game_state.setElementAtPos(getx, (_game_state.getElementAtPos(getx) & 0x0F) | Way.OBJ_CRATER);
                        }

                        switch (ele & 0xF0)
                        {
                            case Way.OBJ_ANTIAIRGUN : _game_state._player_scores += SCORES_ANTIAIRGUN;break;
                            case Way.OBJ_LAUNCER : _game_state._player_scores += SCORES_LAUNCHER;break;
                            case Way.OBJ_FACTORY : _game_state._player_scores += SCORES_FACTORY;break;
                            case Way.OBJ_HOUSE : _game_state._player_scores += SCORES_HOUSE;break;
                            case Way.OBJ_TREE : _game_state._player_scores += SCORES_TREE ;break;
                        }
                    }
                }
            }
        }
    }

    private void processShells()
    {
        ExplObj[] shells = _game_state._shells;

        for (int li = 0; li < shells.length; li++)
        {
            ExplObj bmb = shells[li];
            if (bmb._active)
            {
                bmb._y -= SHELL_Y_SPEED_8;
                bmb._x -= SHELL_X_SPEED;

                if (bmb.getY() < 0)
                {
                    bmb._active = false;
                }
            }
        }
    }

    private void processRockets()
    {
        ExplObj[] rockets = _game_state._rockets;

        for (int li = 0; li < rockets.length; li++)
        {
            ExplObj obj = rockets[li];
            if (!obj._active) continue;
            if (_game_state._player_pos_y < obj.getY())
            {
                obj._y -= ROCKET_Y_SPEED_8;
            }
            else if (_game_state._player_pos_y > obj.getY())
            {
                obj._y += ROCKET_Y_SPEED_8;
            }
            obj._x -= ROCKET_X_SPEED;
            if (obj._x < (0 - ROCKET_SPRITE_WIDTH)) obj._active = false;
        }
    }

    public void resumeGame()
    {
        _game_state.resumeGame();
    }

    protected ExplObj checkShells()
    {
        ExplObj[] shells = _game_state._shells;

        int plx = LongFlight_SB.PLAYER_X_OFFSET + PLAYER_OFF_X;
        int ply = _game_state._player_pos_y + PLAYER_OFF_Y;

        for (int li = 0; li < shells.length; li++)
        {
            ExplObj obj = shells[li];
            if (!obj._active) continue;


            if (!((plx + LongFlight_SB.PLAYER_ZONE_WIDTH <= obj._x)
                    || (ply + LongFlight_SB.PLAYER_ZONE_HEIGHT <= obj.getY())
                    || (plx >= obj._x + LongFlight_SB.SHELL_SPRITE_SIDE)
                    || (ply >= obj.getY() + LongFlight_SB.SHELL_SPRITE_SIDE)))
                return obj;

        }
        return null;
    }

    protected ExplObj checkRockets()
    {
        ExplObj[] shells = _game_state._rockets;

        int plx = LongFlight_SB.PLAYER_X_OFFSET + PLAYER_OFF_X;
        int ply = _game_state._player_pos_y + PLAYER_OFF_Y;

        for (int li = 0; li < shells.length; li++)
        {
            ExplObj obj = shells[li];
            if (!obj._active) continue;

            if (!((plx + LongFlight_SB.PLAYER_ZONE_WIDTH <= obj._x)
                    || (ply + LongFlight_SB.PLAYER_ZONE_HEIGHT <= obj.getY())
                    || (plx >= obj._x + LongFlight_SB.ROCKET_SPRITE_WIDTH)
                    || (ply >= obj.getY() + LongFlight_SB.ROCKET_SPRITE_HEIGHT)))
                return obj;
        }
        return null;
    }

    protected boolean checkCrash(int x, int y, int w, int h)
    {
        int el1 = getElementAtScrXY(x, y + h);
        int el2 = getElementAtScrXY(x + w, y + h);

        if ((el1 >= 0) || (el2 >= 0)) return true; else return false;
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
