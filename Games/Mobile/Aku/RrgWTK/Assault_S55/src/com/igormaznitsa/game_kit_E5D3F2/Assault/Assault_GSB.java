package com.igormaznitsa.game_kit_E5D3F2.Assault;

import com.igormaznitsa.gameapi.GameStateBlock;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class Assault_GSB extends GameStateBlock
{
    public static final byte CELL_NONE = 0;
    public static final byte CELL_LADDER = 1;
    public static final byte CELL_DAM = 2;

    public static byte[] ab_GameField = new byte[Assault_SB.FIELD_WIDTH * Assault_SB.FIELD_HEIGHT];

    public MovingObject p_Player;
    public MovingObject[] ap_MovingObjects;
    public MovingObject p_Stone;

    public int i_TimeDelay;
    public int i_Attemptions;

    public int i_playeralt;
    protected int i_shotfreq;
    public int i_curassaulternumber;
    public int i_assaulternumber;

    public void initLevel(int _level)
    {
        super.initLevel(_level);

        switch (_level)
        {
            case Assault_SB.LEVEL0:
                {
                    i_TimeDelay = Assault_SB.LEVEL0_TIMEDELAY;
                    i_Attemptions = Assault_SB.LEVEL0_ATTEMPTIONS;
                    i_playeralt = Assault_SB.LEVEL0_PLAYERALT;
                    i_shotfreq = Assault_SB.LEVEL0_SHOTFREQ;
                    i_assaulternumber = Assault_SB.LEVEL0_ASSAULTERNUMBER;
                }
                ;
                break;
            case Assault_SB.LEVEL1:
                {
                    i_TimeDelay = Assault_SB.LEVEL1_TIMEDELAY;
                    i_Attemptions = Assault_SB.LEVEL1_ATTEMPTIONS;
                    i_playeralt = Assault_SB.LEVEL1_PLAYERALT;
                    i_shotfreq = Assault_SB.LEVEL1_SHOTFREQ;
                    i_assaulternumber = Assault_SB.LEVEL1_ASSAULTERNUMBER;
                }
                ;
                break;
            case Assault_SB.LEVEL2:
                {
                    i_TimeDelay = Assault_SB.LEVEL2_TIMEDELAY;
                    i_Attemptions = Assault_SB.LEVEL2_ATTEMPTIONS;
                    i_playeralt = Assault_SB.LEVEL2_PLAYERALT;
                    i_shotfreq = Assault_SB.LEVEL2_SHOTFREQ;
                    i_assaulternumber = Assault_SB.LEVEL2_ASSAULTERNUMBER;
                }
                ;
                break;
        }

        p_Player = new MovingObject();
        p_Stone = new MovingObject();

        ap_MovingObjects = new MovingObject[Assault_SB.NUMBER_MOVEOBJECTS];

        for (int li = 0;li < ap_MovingObjects.length;li++)
        {
            ap_MovingObjects[li] = new MovingObject();
        }
        i_curassaulternumber = 0;
        initGameArray();
        initPlayer();
        deactivateAllMovingObjects();
    }

    protected void initScrCoordsForMovingObject(MovingObject _object,int _cellx,int _celly)
    {
        _object.i_scrx = _cellx * Assault_SB.VIRTUALCELL_WIDTH;
        _object.i_scry = _celly * Assault_SB.VIRTUALCELL_HEIGHT;
    }

    public static final int getElement(int _cellx,int _celly)
    {
        return ab_GameField[_cellx + _celly * Assault_SB.FIELD_WIDTH];
    }

    public static final void setElement(int _cellx,int _celly,int _element)
    {
        ab_GameField[_cellx + _celly * Assault_SB.FIELD_WIDTH] = (byte)_element;
    }

    protected MovingObject getInactiveMovingObject()
    {
        for (int li = 0;li < ap_MovingObjects.length;li++)
        {
            MovingObject p_obj = ap_MovingObjects[li];
            if (!p_obj.lg_Active) return p_obj;
        }
        return null;
    }

    protected void deactivateAllMovingObjects()
    {
        for (int li = 0;li < ap_MovingObjects.length;li++)
        {
            ap_MovingObjects[li].lg_Active = false;
        }
        i_assaulternumber += i_curassaulternumber;
        i_curassaulternumber = 0;
    }

    protected void initPlayer()
    {
        p_Stone.lg_Active = false;
        p_Player.activate(MovingObject.TYPE_PLAYER,MovingObject.STATE_UP);
        initScrCoordsForMovingObject(p_Player,Assault_SB.FIELD_WIDTH >> 1,i_playeralt);
    }

    protected void initGameArray()
    {
        for (int li = 0;li < ab_GameField.length;li++)
        {
            ab_GameField[li] = CELL_NONE;
        }
    }

    public int getPlayerScore()
    {
        return i_playerScore * (i_gameLevel + 1);
    }

    public void readFromStream(DataInputStream _dataInputStream) throws IOException
    {
        i_Attemptions = _dataInputStream.readByte();
        i_curassaulternumber = _dataInputStream.readInt();
        i_assaulternumber = _dataInputStream.readInt();

        // Reading Player's data
        p_Player.activate(_dataInputStream.readByte(),_dataInputStream.readByte());
        p_Player.i_Frame = _dataInputStream.readByte();
        p_Player.i_delay = _dataInputStream.readByte();
        p_Player.i_scrx = _dataInputStream.readShort();
        p_Player.i_scry = _dataInputStream.readShort();
        p_Player.lg_Active = true;

        // Reading Stone's data
        p_Stone.activate(_dataInputStream.readByte(),_dataInputStream.readByte());
        p_Stone.lg_Active = _dataInputStream.readBoolean();

        p_Stone.i_Frame = _dataInputStream.readByte();
        p_Stone.i_delay = _dataInputStream.readByte();
        p_Stone.i_scrx = _dataInputStream.readShort();
        p_Stone.i_scry = _dataInputStream.readShort();

        // Reading moving objects
        for (int li = 0;li < ap_MovingObjects.length;li++)
        {
            MovingObject p_obj = ap_MovingObjects[li];

            p_obj.activate(_dataInputStream.readByte(),_dataInputStream.readByte());
            p_obj.lg_Active = _dataInputStream.readBoolean();

            p_obj.i_Frame = _dataInputStream.readByte();
            p_obj.i_delay = _dataInputStream.readByte();
            p_obj.i_scrx = _dataInputStream.readShort();
            p_obj.i_scry = _dataInputStream.readShort();
        }

        // Reading game field data
        for (int li = 0;li < ab_GameField.length;li++)
        {
            ab_GameField[li] = _dataInputStream.readByte();
        }
    }

    public void writeToStream(DataOutputStream _dataOutputStream) throws IOException
    {
        _dataOutputStream.writeByte(i_Attemptions);
        _dataOutputStream.writeInt(i_curassaulternumber);
        _dataOutputStream.writeInt(i_assaulternumber);

        // Writing Player's data
        MovingObject p_obj = p_Player;

        _dataOutputStream.writeByte(p_obj.i_Type);
        _dataOutputStream.writeByte(p_obj.i_State);

        _dataOutputStream.writeByte(p_obj.i_Frame);
        _dataOutputStream.writeByte(p_obj.i_delay);
        _dataOutputStream.writeShort(p_obj.i_scrx);
        _dataOutputStream.writeShort(p_obj.i_scry);

        // Writing Stone's data
        p_obj = p_Stone;

        _dataOutputStream.writeByte(p_obj.i_Type);
        _dataOutputStream.writeByte(p_obj.i_State);
        _dataOutputStream.writeBoolean(p_obj.lg_Active);

        _dataOutputStream.writeByte(p_obj.i_Frame);
        _dataOutputStream.writeByte(p_obj.i_delay);
        _dataOutputStream.writeShort(p_obj.i_scrx);
        _dataOutputStream.writeShort(p_obj.i_scry);

        // Writing moving objects
        for (int li = 0;li < ap_MovingObjects.length;li++)
        {
            p_obj = ap_MovingObjects[li];

            _dataOutputStream.writeByte(p_obj.i_Type);
            _dataOutputStream.writeByte(p_obj.i_State);
            _dataOutputStream.writeBoolean(p_obj.lg_Active);

            _dataOutputStream.writeByte(p_obj.i_Frame);
            _dataOutputStream.writeByte(p_obj.i_delay);
            _dataOutputStream.writeShort(p_obj.i_scrx);
            _dataOutputStream.writeShort(p_obj.i_scry);
        }

        // Writing game field data
        for (int li = 0;li < ab_GameField.length;li++)
        {
            _dataOutputStream.writeByte(ab_GameField[li]);
        }
    }
}
