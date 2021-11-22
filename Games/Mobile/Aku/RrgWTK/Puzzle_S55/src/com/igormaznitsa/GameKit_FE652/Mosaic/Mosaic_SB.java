package com.igormaznitsa.GameKit_FE652.Mosaic;

import com.igormaznitsa.GameAPI.StrategicBlock;
import com.igormaznitsa.GameAPI.GameStateRecord;
import com.igormaznitsa.GameAPI.PlayerBlock;
import com.igormaznitsa.GameAPI.GameActionListener;
import com.igormaznitsa.GameAPI.Utils.RndGenerator;

import java.io.*;

public class Mosaic_SB implements StrategicBlock
{
    /**
     * Grid is 3x3
     */
    public static final int LEVEL_0 = 0;
    /**
     * Grid is 4x4
     */
    public static final int LEVEL_1 = 1;
    /**
     * Grid is 5x5
     */
    public static final int LEVEL_2 = 2;
    /**
     * Mix number for LEVEL0
     */
    public static final int MIX_LEVEL0 = 50;
    /**
     * Mix number for LEVEL1
     */
    public static final int MIX_LEVEL1 = 100;
    /**
     * Mix number for LEVEL2
     */
    public static final int MIX_LEVEL2 = 150;

    protected Mosaic_GSR _game_state = null;
    protected PlayerBlock _player_block = null;
    public int _game_level;

    protected GameActionListener _action_listener = null;

    public static final int ACTION_STARTSOUND = 0;
    public static final int ACTION_MOVESOUND = 1;
    public static final int ACTION_FINALSOUND = 2;

    public Mosaic_SB(GameActionListener gameActionListener)
    {
        _action_listener = gameActionListener;
        _game_level = LEVEL_0;
    }

    public void newGame(int level)
    {
        initGameArray(level,false);
        if (_player_block!=null) _player_block.initPlayer();
        if (_action_listener!=null) _action_listener.actionEvent(ACTION_STARTSOUND);
    }

    public byte [] newDemoGame(int level)
    {
        byte [] demosq = initGameArray(level,true);
        if (_player_block!=null) _player_block.initPlayer();
        return demosq;
    }

    protected byte [] initGameArray(int level,boolean demo)
    {
        _game_state = null;
        _game_level = level;
        byte [] dmsq = null;
        System.gc();

        _game_state = new Mosaic_GSR(level);
        switch (level)
        {
            case LEVEL_0:
                    dmsq  = mixGameArray(MIX_LEVEL0,demo);
                ;
                break;
            case LEVEL_1:
                    dmsq  = mixGameArray(MIX_LEVEL1,demo);
                ;break;
            case LEVEL_2:
                    dmsq  = mixGameArray(MIX_LEVEL2,demo);
                ;break;
        }
        _game_state._game_state = Mosaic_GSR.GAME_PLAYED;
        return  dmsq;
    }

    protected byte [] mixGameArray(int mixcount,boolean demo)
    {
        if (_game_state == null) return null;

        byte [] demoarray = null;

        if (demo)
        {
            demoarray = new byte [mixcount];
        }

        RndGenerator _rndGenerator = new RndGenerator(System.currentTimeMillis());

        int[] arra = _game_state.getGameArray();
        for (int li = 0; li < arra.length - 1; li++) arra[li] = li;
        arra[arra.length - 1] = Mosaic_GSR.EMPTY_CELL;

        int lst_x = _game_state.getGameArrayWidth() - 1;
        int lst_y = _game_state.getGameArrayHeight() - 1;

        int px = lst_x ;
        int py = lst_y ;

        for (int li = 0; li < mixcount; li++)
        {
            int dx = 0;
            int dy = 0;
            int drct = _rndGenerator.getInt(3);

            while(true)
            {
                dx = lst_x;
                dy = lst_y;

                switch (drct)
                {
                    case Mosaic_PMR.DIRECT_DOWN:
                        dy++;
                        break;
                    case Mosaic_PMR.DIRECT_LEFT:
                        dx--;
                        break;
                    case Mosaic_PMR.DIRECT_RIGHT:
                        dx++;
                        break;
                    case Mosaic_PMR.DIRECT_TOP:
                        dy--;
                        break;
                }

                if (_game_state.getElementAt(dx, dy) != Mosaic_GSR.EMPTY_OUTARRAY)
                {
                    if ((px!=dx)||(py!=dy))
                    {
                        px = lst_x;
                        py = lst_y;
                        break;
                    }
                    else
                        drct++;
                }
                else
                    drct++;

                if (drct>3) drct = 0;
            }

            if (demo)
            {
                int dr = drct ^ 0x03;
                demoarray [mixcount-1-li] = (byte)dr;
            }
            int ele = _game_state.getElementAt(dx, dy);
            int ele2 = _game_state.getElementAt(lst_x, lst_y);
            _game_state.setElementAt(lst_x, lst_y, ele);
            _game_state.setElementAt(dx, dy, ele2);

            lst_x = dx;
            lst_y = dy;
        }
        _game_state._empty_x = lst_x;
        _game_state._empty_y = lst_y;
        _game_state._old_empty_x = lst_x;
        _game_state._old_empty_y = lst_y;

        return demoarray;
    }

    public void loadGameState(InputStream inputStream) throws IOException
    {
        System.gc();
        DataInputStream dos = new DataInputStream(inputStream);

        // Reading game level
        int lvl = dos.readByte();
        initGameArray(lvl,false);

        // Saving state of the game array
        int[] arr = _game_state.getGameArray();
        for (int li = 0; li < arr.length; li++)
        {
            arr[li] = dos.readUnsignedByte();
        }

        _game_state._empty_x = dos.readUnsignedByte();
        _game_state._empty_y = dos.readUnsignedByte();
        _game_state._old_empty_x = dos.readUnsignedByte();
        _game_state._old_empty_y = dos.readUnsignedByte();
        _game_state._moving_counter = dos.readUnsignedShort();

        dos = null;
        System.gc();

        if (_player_block!=null) _player_block.initPlayer();
    }

    public void saveGameState(OutputStream outputStream) throws IOException
    {
        System.gc();
        DataOutputStream dos = new DataOutputStream(outputStream);

        // Writing game level
        dos.writeByte(_game_level);
        // Saving state of the game array
        int[] arr = _game_state.getGameArray();
        for (int li = 0; li < arr.length; li++)
        {
            dos.writeByte(arr[li]);
        }

        dos.writeByte(_game_state._empty_x);
        dos.writeByte(_game_state._empty_y);
        dos.writeByte(_game_state._old_empty_x);
        dos.writeByte(_game_state._old_empty_y);
        dos.writeShort(_game_state._moving_counter);

        dos = null;
        System.gc();
    }

    public void nextGameStep()
    {
        if (_player_block == null) return;
        Mosaic_PMR _mpr = (Mosaic_PMR) _player_block.getPlayerMoveRecord(_game_state);

        int _direct = _mpr.getDirect();

        int nx = _game_state.getEmptyCellX();
        int ny = _game_state.getEmptyCellY();

        switch(_direct)
        {
            case Mosaic_PMR.DIRECT_NONE :
                {
                    _game_state._old_empty_x = _game_state._empty_x;
                    _game_state._old_empty_y = _game_state._empty_y;
                    return;
                };
            case Mosaic_PMR.DIRECT_DOWN :
                {
                    ny++;
                } ; break;
            case Mosaic_PMR.DIRECT_LEFT :
                {
                    nx--;
                } ; break;
            case Mosaic_PMR.DIRECT_RIGHT:
                {
                    nx++;
                } ; break;
            case Mosaic_PMR.DIRECT_TOP  :
                {
                    ny--;
                } ; break;
        }
        _game_state._moving_counter ++;
        if (_action_listener!=null) _action_listener.actionEvent(ACTION_MOVESOUND);

        int nele = _game_state.getElementAt(nx,ny);
        if(nele==Mosaic_GSR.EMPTY_OUTARRAY)
        {
            _game_state._old_empty_x = _game_state._empty_x;
            _game_state._old_empty_y = _game_state._empty_y;
            return;
        }

        int nele2 = _game_state.getElementAt(_game_state.getEmptyCellX(),_game_state.getEmptyCellY());

        _game_state.setElementAt(nx,ny,nele2);
        _game_state.setElementAt(_game_state.getEmptyCellX(),_game_state.getEmptyCellY(),nele);

        _game_state._old_empty_x = _game_state._empty_x;
        _game_state._old_empty_y = _game_state._empty_y;
        _game_state._empty_x = nx;
        _game_state._empty_y = ny;

        if (isGameOver())
        {
            if (_action_listener!=null) _action_listener.actionEvent(ACTION_FINALSOUND);
            _game_state._game_state = Mosaic_GSR.GAME_OVER;
        }
    }

    protected boolean isGameOver()
    {
        int[] arr = _game_state.getGameArray();
        for (int li = 0; li < arr.length - 1; li++)
        {
            if (arr[li] != li) return false;
        }
        return true;
    }

    public GameStateRecord getGameStateRecord()
    {
        return _game_state;
    }

    public void setPlayerBlock(PlayerBlock playerBlock)
    {
        _player_block = playerBlock;
    }

    public void setAIBlock(PlayerBlock aiBlock)
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
