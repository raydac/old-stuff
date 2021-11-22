package com.igormaznitsa.game_kit_E5D3F2.RiverRescue;

import com.igormaznitsa.gameapi.GameStateBlock;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.DataInputStream;

public class RiverRescue_GSB extends GameStateBlock
{
    public static final int PLAYERSTATE_FUELISENDED = 10;

    public int i_TimeDelay;

    protected long[][] aal_blockarray = null;
    protected int[] ai_wayarray = null;

    public Player p_Player;
    public MovingObject[] ap_MovingObjects = null;

    public static final int BLOCKLENGTH = 96;
    public int i_FullWayLength;
    public int i_FullWayLengthScr;

    public int i8_fuelvalue;
    protected int i_maxfueldelay;
    protected int i_minminedelay;
    protected int i_fueldelay;
    protected int i_minedelay;

    protected static final long l_airminemask = 0xC;

    public void initLevel(int _level)
    {
        super.initLevel(_level);

        switch (_level)
        {
            case RiverRescue_SB.LEVEL0:
                {
                    i_TimeDelay = RiverRescue_SB.LEVEL0_TIMEDELAY;
                    i_maxfueldelay = RiverRescue_SB.LEVEL0_FUELDELAY;
                    i_minminedelay = RiverRescue_SB.LEVEL0_MINEMINDELAY;
                }
                ;
                break;
            case RiverRescue_SB.LEVEL1:
                {
                    i_TimeDelay = RiverRescue_SB.LEVEL1_TIMEDELAY;
                    i_maxfueldelay = RiverRescue_SB.LEVEL1_FUELDELAY;
                    i_minminedelay = RiverRescue_SB.LEVEL1_MINEMINDELAY;
                }
                ;
                break;
            case RiverRescue_SB.LEVEL2:
                {
                    i_TimeDelay = RiverRescue_SB.LEVEL2_TIMEDELAY;
                    i_maxfueldelay = RiverRescue_SB.LEVEL2_FUELDELAY;
                    i_minminedelay = RiverRescue_SB.LEVEL2_MINEMINDELAY;
                }
                ;
                break;
        }

        ap_MovingObjects = new MovingObject[RiverRescue_SB.MAXOBJECTS];
        for (int li = 0; li < ap_MovingObjects.length; li++)
        {
            ap_MovingObjects[li] = new MovingObject();
        }


        if( aal_blockarray==null || ai_wayarray==null){
/*
// old version within Stages.java

          aal_blockarray = new long[Stages.BLOCK_NUMBER][];
          for (int li = 0; li < aal_blockarray.length; li++)
          {
              aal_blockarray[li] = Stages.getBlock(li);
          }
          ai_wayarray = Stages.getWay();
*/
           Runtime.getRuntime().gc();

           try
           {
              DataInputStream ds = new DataInputStream(getClass().getResourceAsStream("/res/map"));

	      aal_blockarray = new long[ds.readUnsignedByte()][];
              for (int li = 0; li < aal_blockarray.length; li++)
              {
		   aal_blockarray[li] = new long[ds.readUnsignedByte()];
	           for(int j = 0;j<aal_blockarray[li].length;j++)
		       aal_blockarray[li][j] = ds.readLong();
              }
              ai_wayarray = new int[ds.readUnsignedByte()];
              for(int j = 0;j<ai_wayarray.length;j++)
	            ai_wayarray[j] = ds.readUnsignedByte();

           } catch(Exception e) {
	     i_gameState = GAMESTATE_OVER;
	     i_playerState = PLAYERSTATE_LOST;
	     e.printStackTrace();
	     return;
	   }
        }

        i_FullWayLength = ai_wayarray.length * BLOCKLENGTH;
        i_FullWayLengthScr = i_FullWayLength * RiverRescue_SB.VIRTUALCELL_HEIGHT;

        p_Player = new Player();
        p_Player.setCY(i_FullWayLengthScr - p_Player.i_height);
        initPlayer();

        i8_fuelvalue = RiverRescue_SB.I8_MAX_FUEL_VALUE;
        i_fueldelay = i_maxfueldelay;
        i_minedelay = i_minminedelay;
    }

    public int getPlayerScore()
    {
        return i_playerScore * (i_gameLevel+1);
    }

    protected void initPlayer()
    {
        p_Player.setFrame(3);

        int i_line = p_Player.getScrY()/ RiverRescue_SB.VIRTUALCELL_HEIGHT;

        p_Player.setCX(centeredMoveObjectOnRoad(i_line, 0));
    }

    protected void deactivateAllObjects()
    {
        for (int li = 0; li < ap_MovingObjects.length; li++)
        {
            ap_MovingObjects[li].lg_Active = false;
        }
    }

    protected MovingObject getFirstInactiveObject()
    {
        for (int li = 0; li < ap_MovingObjects.length; li++)
        {
            if (ap_MovingObjects[li].lg_Active) continue;
            return ap_MovingObjects[li];
        }
        return null;
    }

    public long getLineElement(int _num)
    {
        int i_block = _num / BLOCKLENGTH;
        int i_indx = _num % BLOCKLENGTH;

        return aal_blockarray[ai_wayarray[i_block]][i_indx];
    }

    protected int rightMoveObjectOnRoad(int _line, int _width)
    {
        int i_indx = _line;
        long l_elem = getLineElement(i_indx);

        int i_start = -1;

        long l_lmask = 0x8000000000000000l;
        for (int li = 63; li >= 0; li--)
        {
            if (((l_elem & l_lmask) != 0))
            {
                if (i_start < 0)
                {
                    i_start = li;
                    break;
                }
            }
            l_lmask >>>= 1;
        }

        int i_offx = i_start * RiverRescue_SB.VIRTUALCELL_WIDTH;
        return i_offx - _width;
    }

    protected int leftMoveObjectOnRoad(int _line, int _width)
    {
        int i_indx = _line;
        long l_elem = getLineElement(i_indx);

        int i_start = -1;

        long l_lmask = 0x01;
        for (int li = 0; li < 64; li++)
        {
            if (((l_elem & l_lmask) != 0))
            {
                if (i_start < 0)
                {
                    i_start = li;
                    break;
                }
            }
            l_lmask <<= 1;
        }

        int i_offx = i_start * RiverRescue_SB.VIRTUALCELL_WIDTH;
        return i_offx;
    }

    protected int centeredMoveObjectOnRoad(int _line, int _width)
    {
        int i_indx = _line;
        long l_elem = getLineElement(i_indx);

        int i_start = -1;
        int i_wdth = 0;

        long l_lmask = 0x01;
        for (int li = 0; li < 64; li++)
        {
            if (((l_elem & l_lmask) != 0))
            {
                if (i_start < 0)
                {
                    i_start = li;
                    i_wdth++;
                }
                else
                    i_wdth++;
            }
            else
            {
                if (i_start >= 0) break;
            }
            l_lmask <<= 1;
        }

        int i_offx = i_start * RiverRescue_SB.VIRTUALCELL_WIDTH;
        i_wdth = i_wdth * RiverRescue_SB.VIRTUALCELL_WIDTH;
        return i_offx + ((i_wdth - _width) >> 1);
    }

    public void writeToStream(DataOutputStream _dataOutputStream) throws IOException
    {
        _dataOutputStream.writeInt(i8_fuelvalue);
        _dataOutputStream.writeInt(i_fueldelay);
        _dataOutputStream.writeInt(i_minedelay);

        _dataOutputStream.writeBoolean(p_Player.lg_Destroyed);
        _dataOutputStream.writeByte(p_Player.i_Frame);
        _dataOutputStream.writeByte(p_Player.i_tick);

        _dataOutputStream.writeInt(p_Player.i_cx);
        _dataOutputStream.writeInt(p_Player.i_cy);

        for(int li=0;li<ap_MovingObjects.length;li++)
        {
            MovingObject p_obj = ap_MovingObjects[li];

            _dataOutputStream.writeByte(p_obj.i_Type);
            _dataOutputStream.writeByte(p_obj.i_Frame);


            _dataOutputStream.writeByte(p_obj.i_delay);
            _dataOutputStream.writeBoolean(p_obj.lg_backMove);

            _dataOutputStream.writeBoolean(p_obj.lg_Active);
            _dataOutputStream.writeBoolean(p_obj.lg_bombdrop);

            _dataOutputStream.writeInt(p_obj.i_y);
            _dataOutputStream.writeInt(p_obj.i_x);
        }
    }

    public void readFromStream(DataInputStream _dataInputStream) throws IOException
    {
        i8_fuelvalue = _dataInputStream.readInt();
        i_fueldelay = _dataInputStream.readInt();
        i_minedelay = _dataInputStream.readInt();

        p_Player.initSplashes();
        p_Player.lg_Destroyed =_dataInputStream.readBoolean();
        p_Player.setFrame(_dataInputStream.readByte());
        p_Player.i_tick = _dataInputStream.readByte();

        p_Player.setCX(_dataInputStream.readInt());
        p_Player.setCY(_dataInputStream.readInt());

        for(int li=0;li<ap_MovingObjects.length;li++)
        {
            MovingObject p_obj = ap_MovingObjects[li];

            p_obj.activate(_dataInputStream.readByte(),0,0);
            p_obj.i_Frame =_dataInputStream.readByte();

            p_obj.i_delay = _dataInputStream.readByte();
            p_obj.lg_backMove =  _dataInputStream.readBoolean();

            p_obj.lg_Active = _dataInputStream.readBoolean();
            p_obj.lg_bombdrop = _dataInputStream.readBoolean();

            p_obj.i_y =_dataInputStream.readInt();
            p_obj.i_x = _dataInputStream.readInt();
        }
    }
}
