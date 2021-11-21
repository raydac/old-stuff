package com.igormaznitsa.game_kit_out_6BE902.DriveOff;

import com.igormaznitsa.gameapi.GameStateBlock;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class DriveOff_GSB extends GameStateBlock
{
    protected int[] ai_stagearray = null;

    protected MoveObject[] ap_moveobjects;
    protected int i_attemption;
    protected int i_copcars;
    protected int i_copters;
    protected int i_timedelay;
    protected int i_waylength;
    protected int i_maxobjects;
    protected int i_curobjects;
    protected int i_bombdelay;

    protected int i_initpolice;
    protected int i_initcopters;

    protected int i_helicoptertimedelay;
    protected int i_policetimedelay;

    protected int i_curcopterdelay;
    protected int i_curpolicedelay;

    protected int i_tracknumber;

    MoveObject p_player;

    public void readFromStream(DataInputStream dataInputStream) throws IOException
    {
        i_attemption = dataInputStream.readUnsignedByte();
        i_copcars =  dataInputStream.readUnsignedByte();
        i_copters = dataInputStream.readUnsignedByte();
        i_curobjects =  dataInputStream.readUnsignedByte();
        i_bombdelay = dataInputStream.readUnsignedByte();

        i_curcopterdelay = dataInputStream.readUnsignedShort();
        i_curpolicedelay = dataInputStream.readUnsignedShort();

        for(int li=0;li<ap_moveobjects.length;li++)
        {
            MoveObject p_obj = ap_moveobjects[li];
            boolean lg_active = dataInputStream.readBoolean();
            if (lg_active)
            {
                int i_type = dataInputStream.readUnsignedByte();
                int i_x = dataInputStream.readShort();
                int i_y = dataInputStream.readShort();
                p_obj.activate(i_type,i_x,i_y);
                p_obj.i_state = dataInputStream.readUnsignedByte();
                p_obj.i_curdamage = dataInputStream.readUnsignedByte();

                p_obj.i_dirx = dataInputStream.readByte();
                p_obj.i_diry = dataInputStream.readByte();
            }
            else
            {
                p_obj.lg_active = false;
            }
        }
    }

    public void writeToStream(DataOutputStream dataOutputStream) throws IOException
    {
        dataOutputStream.writeByte(i_attemption);
        dataOutputStream.writeByte(i_copcars);
        dataOutputStream.writeByte(i_copters);
        dataOutputStream.writeByte(i_curobjects);
        dataOutputStream.writeByte(i_bombdelay);

        dataOutputStream.writeShort(i_curcopterdelay);
        dataOutputStream.writeShort(i_curpolicedelay);

        for(int li=0;li<ap_moveobjects.length;li++)
        {
            MoveObject p_obj = ap_moveobjects[li];
            dataOutputStream.writeBoolean(p_obj.lg_active);
            if (p_obj.lg_active)
            {
                dataOutputStream.writeByte(p_obj.i_type);
                dataOutputStream.writeShort(p_obj.i_scrx);
                dataOutputStream.writeShort(p_obj.i_scry);

                dataOutputStream.writeByte(p_obj.i_state);
                dataOutputStream.writeByte(p_obj.i_curdamage);

                dataOutputStream.writeByte(p_obj.i_dirx);
                dataOutputStream.writeByte(p_obj.i_diry);
            }
        }
    }

    public int getWayLength()
    {
        return i_waylength;
    }

    public int getAttemptions()
    {
      return i_attemption;
    }

    public void initStage(int stage)
    {
        super.initStage(stage);

        i_copcars = i_initpolice+stage;
        i_copters = i_initcopters+stage;

        ai_stagearray = Stages.getStage(stage);
        i_waylength = ai_stagearray.length;
        p_player.activate(MoveObject.TYPE_PLAYERCAR, 0, 0);
        centeredMoveObjectOnRoad(p_player);
        i_curobjects = 0;
        i_curcopterdelay = 0;
        i_curpolicedelay = 0;

        i_helicoptertimedelay = (i_waylength * i_tracknumber)/i_copters;
        i_policetimedelay = (i_waylength * i_tracknumber)/i_copcars;
        deactivateAllMoveObjectsExcPlayer();
    }

    public void initLevel(int level)
    {
        super.initLevel(level);

        switch (level)
        {
            case DriveOff_SB.LEVEL0:
                {
                    i_attemption = DriveOff_SB.LEVEL0_ATTEMPTIONS;
                    i_initpolice = DriveOff_SB.LEVEL0_COPCARS;
                    i_initcopters = DriveOff_SB.LEVEL0_HELYCOPTERS;
                    i_timedelay = DriveOff_SB.LEVEL0_TIMEDELAY;
                    i_maxobjects = DriveOff_SB.LEVEL0_MAXOBJECTS;
                    i_tracknumber = DriveOff_SB.LEVEL0_TRACKS;
                }
                ;
                break;
            case DriveOff_SB.LEVEL1:
                {
                    i_attemption = DriveOff_SB.LEVEL1_ATTEMPTIONS;
                    i_initpolice = DriveOff_SB.LEVEL1_COPCARS;
                    i_initcopters = DriveOff_SB.LEVEL1_HELYCOPTERS;
                    i_timedelay = DriveOff_SB.LEVEL1_TIMEDELAY;
                    i_maxobjects = DriveOff_SB.LEVEL1_MAXOBJECTS;
                    i_tracknumber = DriveOff_SB.LEVEL1_TRACKS;
                }
                ;
                break;
            case DriveOff_SB.LEVEL2:
                {
                    i_attemption = DriveOff_SB.LEVEL2_ATTEMPTIONS;
                    i_initpolice = DriveOff_SB.LEVEL2_COPCARS;
                    i_initcopters = DriveOff_SB.LEVEL2_HELYCOPTERS;
                    i_timedelay = DriveOff_SB.LEVEL2_TIMEDELAY;
                    i_maxobjects = DriveOff_SB.LEVEL2_MAXOBJECTS;
                    i_tracknumber = DriveOff_SB.LEVEL2_TRACKS;
                }
                ;
                break;
        }

        ap_moveobjects = null;
        System.gc();
        ap_moveobjects = new MoveObject[DriveOff_SB.ANIMATION_OBJECTS];
        for (int li = 0; li < ap_moveobjects.length; li++)
        {
            ap_moveobjects[li] = new MoveObject();
        }
        p_player = ap_moveobjects[DriveOff_SB.PLAYEROBJECT_INDEX];
    }

    protected MoveObject getInactiveMoveObject()
    {
        for (int li = 0; li < ap_moveobjects.length; li++)
        {
            if (!ap_moveobjects[li].lg_active) return ap_moveobjects[li];
        }
        return null;
    }

    public MoveObject[] getMoveObjectArray()
    {
        return ap_moveobjects;
    }

    public int getPlayerScore()
    {
        return i_playerScore * (i_gameLevel + 1) + (i_gameStage+1)*50;
    }

    protected void rightMoveObjectOnRoad(MoveObject _obj)
    {
        int i_indx = _obj.getCurrentLine();
        int i_elem = getLineElement(i_indx);

        int i_start = -1;

        int i_lmask = 0x80000000;
        for (int li = 31; li >= 0; li--)
        {
            if (((i_elem & i_lmask) != 0))
            {
                if (i_start < 0)
                {
                    i_start = li;
                    break;
                }
            }
            i_lmask >>>= 1;
        }

        int i_offx = i_start * DriveOff_SB.VIRTUALCELL_WIDTH;
        _obj.setX(i_offx - _obj.i_width);
    }

    protected void leftMoveObjectOnRoad(MoveObject _obj)
    {
        int i_indx = _obj.getCurrentLine();
        int i_elem = getLineElement(i_indx);

        int i_start = -1;

        int i_lmask = 0x01;
        for (int li = 0; li < 32; li++)
        {
            if (((i_elem & i_lmask) != 0))
            {
                if (i_start < 0)
                {
                    i_start = li;
                    break;
                }
            }
            i_lmask <<= 1;
        }

        int i_offx = i_start * DriveOff_SB.VIRTUALCELL_WIDTH;
        _obj.setX(i_offx);
    }

    protected void centeredMoveObjectOnRoad(MoveObject _obj)
    {
        int i_indx = _obj.getCurrentLine();
        int i_elem = getLineElement(i_indx);

        int i_start = -1;
        int i_wdth = 0;

        int i_lmask = 0x01;
        for (int li = 0; li < 32; li++)
        {
            if (((i_elem & i_lmask) != 0))
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
            i_lmask <<= 1;
        }

        int i_offx = i_start * DriveOff_SB.VIRTUALCELL_WIDTH;
        i_wdth = i_wdth * DriveOff_SB.VIRTUALCELL_WIDTH;
        _obj.setX(i_offx + ((i_wdth - _obj.i_width) >> 1));
    }

    protected void deactivateAllMoveObjectsExcPlayer()
    {
        for (int li = 0; li < ap_moveobjects.length; li++)
        {
            if (li == DriveOff_SB.PLAYEROBJECT_INDEX) continue;
            ap_moveobjects[li].lg_active = false;
        }
        i_curobjects = 0;
    }

    protected void deactivateAllMoveObjectsExcPlayerAndPolice()
    {
        for (int li = 0; li < ap_moveobjects.length; li++)
        {
            switch(ap_moveobjects[li].i_type)
            {
                case MoveObject.TYPE_PLAYERCAR :
                case MoveObject.TYPE_POLICECAR :
                case MoveObject.TYPE_HELYCOPTER : continue;
                default :
                        ap_moveobjects[li].lg_active = false;
            }
        }
        i_curobjects = 0;
    }

    public int getLineElement(int _num)
    {
        if (_num < 0)
            _num += i_waylength;
        else if (_num >= i_waylength)
            _num -= (i_waylength - 1);
        return ai_stagearray[_num];
    }

}
