package com.igormaznitsa.game_kit_E5D3F2.Zynaps;

import com.igormaznitsa.gameapi.GameStateBlock;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.DataInputStream;

public class Zynaps_GSB extends GameStateBlock
{
    public MovingObject[] ap_MovingObjects;
    public MovingObject[] ap_PlayerBullets;

    public MovingObject p_PlayerShield;
    public MovingObject p_PlayerShortWeapon;

    public int i_PlayerBulletType;
    public int i_Attemptions;
    public int i_TimeDelay;
    public Player p_Player;

    protected int i_playerbulletinactive;
    protected boolean lg_BossMode;

    protected int i_enemydelay;
    protected int i_shielddelay;
    protected int i_energyshielddelay;
    protected int i_firedelay;
    protected int i_maxenemydelay;
    public int i_playerpower;
    public int i_bosspower;

    public int i8_backgroundway;

    protected long[][] aal_blockarray = null;
    protected int[] ai_wayarray = null;

    public int i_NonDestroyableDelay;

    protected int i_fullcellsnumber;

    public void initLevel(int _level)
    {
        super.initLevel(_level);

        switch (_level)
        {
            case Zynaps_SB.LEVEL0:{
                i_TimeDelay = Zynaps_SB.LEVEL0_TIMEDELAY;
                i_maxenemydelay = Zynaps_SB.LEVEL0_ENEMYDELAY;
                };break;
            case Zynaps_SB.LEVEL1:
                {
                i_TimeDelay = Zynaps_SB.LEVEL1_TIMEDELAY;
                i_maxenemydelay = Zynaps_SB.LEVEL1_ENEMYDELAY;
                };
                break;
            case Zynaps_SB.LEVEL2:
                {
                i_TimeDelay = Zynaps_SB.LEVEL2_TIMEDELAY;
                i_maxenemydelay = Zynaps_SB.LEVEL2_ENEMYDELAY;
                };
                break;
        }

        ap_MovingObjects = new MovingObject[Zynaps_SB.MAX_MOVINGOBJECTS];
        ap_PlayerBullets = new MovingObject[Zynaps_SB.MAX_PLAYERBULLETS];
        p_PlayerShield = new MovingObject();
        p_PlayerShortWeapon = new MovingObject();

        p_PlayerShield.activate(MovingObject.TYPE_SHIELD,0,0);
        p_PlayerShortWeapon.activate(MovingObject.TYPE_ENERGYSHIELD,0,0);

        for (int li = 0; li < ap_MovingObjects.length; li++) ap_MovingObjects[li] = new MovingObject();
        for (int li = 0; li < ap_PlayerBullets.length; li++) ap_PlayerBullets[li] = new MovingObject();

        p_Player = new Player();
        deactivateMoveObjects(true);
        deactivatePlayerProperties();

        lg_BossMode = false;
        i_firedelay = 0;

        aal_blockarray = new long[Stages.BLOCK_NUMBER][];
        for (int li = 0; li < aal_blockarray.length; li++)
        {
            aal_blockarray[li] = Stages.getBlock(li);
        }

        ai_wayarray = Stages.getWay();
        i8_backgroundway = 0;
        i_NonDestroyableDelay = 0;

        i_Attemptions = Zynaps_SB.MAX_ATTEMPTIONS;

        i_fullcellsnumber = Stages.ai_wayarray.length * Stages.BLOCKLENGTH - 1;
    }

    public long getLineElement(int _num)
    {
        int i_block = _num / Stages.BLOCKLENGTH;
        int i_indx = _num % Stages.BLOCKLENGTH;

        return aal_blockarray[ai_wayarray[i_block]][i_indx];
    }

    protected void deactivateMoveObjects(boolean _removboss)
    {
        for (int li = 0; li < ap_MovingObjects.length; li++)
        {
            if(!ap_MovingObjects[li].lg_Active) continue;
            if (ap_MovingObjects[li].i_Type==MovingObject.TYPE_BOSS)
            {
                if (!_removboss)
                {
                    continue;
                }
            }
            ap_MovingObjects[li].lg_Active = false;
        }
    }

    protected void deactivatePlayerProperties()
    {
        for (int li = 0; li < ap_PlayerBullets.length; li++) ap_PlayerBullets[li].lg_Active = false;
        p_PlayerShield.lg_Active = false;
        p_PlayerShortWeapon.lg_Active = false;
        i_PlayerBulletType = MovingObject.TYPE_BULLET1;
        i_playerbulletinactive = Zynaps_SB.MAX_PLAYERBULLETS;
        i_shielddelay = 0;
        p_PlayerShield.lg_Active = false;
        p_PlayerShortWeapon.lg_Active = false;
        i_playerpower = Zynaps_SB.PLAYER_POWER;
        i_NonDestroyableDelay = Zynaps_SB.DELAY_NONDESTROYED;
    }

    protected MovingObject getInactivePlayerBullet()
    {
        for (int li = 0; li < ap_PlayerBullets.length; li++)
        {
            if (ap_PlayerBullets[li].lg_Active) continue;
            return ap_PlayerBullets[li];
        }
        return null;
    }

    protected MovingObject getInactiveObject()
    {
        for (int li = 0; li < ap_MovingObjects.length; li++)
        {
            if (ap_MovingObjects[li].lg_Active) continue;
            return ap_MovingObjects[li];
        }
        return null;
    }

    public void writeToStream(DataOutputStream _dataOutputStream) throws IOException
    {
        _dataOutputStream.writeByte(i_PlayerBulletType);
        _dataOutputStream.writeByte(i_Attemptions);
        _dataOutputStream.writeByte(i_playerbulletinactive);
        _dataOutputStream.writeBoolean(lg_BossMode);

        _dataOutputStream.writeShort(i_enemydelay);
        _dataOutputStream.writeShort(i_shielddelay);
        _dataOutputStream.writeShort(i_energyshielddelay);
        _dataOutputStream.writeShort(i_firedelay);
        _dataOutputStream.writeShort(i_playerpower);
        _dataOutputStream.writeShort(i_bosspower);
        _dataOutputStream.writeInt(i8_backgroundway);
        _dataOutputStream.writeInt(i_NonDestroyableDelay);

        // Writing player's data

        _dataOutputStream.writeBoolean(p_Player.lg_Destroyed);
        _dataOutputStream.writeByte(p_Player.i_Frame);
        _dataOutputStream.writeByte(p_Player.i_tick);
        _dataOutputStream.writeInt(p_Player.i8_cx);
        _dataOutputStream.writeInt(p_Player.i8_cy);

        // moving objects

        MovingObject p_obj = null;

        for(int li=0;li<ap_MovingObjects.length;li++)
        {
            p_obj = ap_MovingObjects[li];

            _dataOutputStream.writeByte(p_obj.i_Type);
            _dataOutputStream.writeBoolean(p_obj.lg_Active);
            _dataOutputStream.writeByte(p_obj.i_Frame);
            _dataOutputStream.writeBoolean(p_obj.lg_backmove);

            _dataOutputStream.writeByte(p_obj.i_delay);
            _dataOutputStream.writeInt(p_obj.i8_scrx);
            _dataOutputStream.writeInt(p_obj.i8_scry);
            _dataOutputStream.writeInt(p_obj.i_vx);
            _dataOutputStream.writeInt(p_obj.i_vy);
            _dataOutputStream.writeInt(p_obj.i_angle);
            _dataOutputStream.writeInt(p_obj.i_radius);
        }

        for(int li=0;li<ap_PlayerBullets.length;li++)
        {
            p_obj = ap_PlayerBullets[li];

            _dataOutputStream.writeByte(p_obj.i_Type);
            _dataOutputStream.writeBoolean(p_obj.lg_Active);
            _dataOutputStream.writeByte(p_obj.i_Frame);
            _dataOutputStream.writeBoolean(p_obj.lg_backmove);

            _dataOutputStream.writeByte(p_obj.i_delay);
            _dataOutputStream.writeInt(p_obj.i8_scrx);
            _dataOutputStream.writeInt(p_obj.i8_scry);
            _dataOutputStream.writeInt(p_obj.i_vx);
            _dataOutputStream.writeInt(p_obj.i_vy);
        }


        p_obj = p_PlayerShield;

        _dataOutputStream.writeBoolean(p_obj.lg_Active);
        _dataOutputStream.writeByte(p_obj.i_Frame);
        _dataOutputStream.writeBoolean(p_obj.lg_backmove);

        _dataOutputStream.writeByte(p_obj.i_delay);
        _dataOutputStream.writeInt(p_obj.i8_scrx);
        _dataOutputStream.writeInt(p_obj.i8_scry);
        _dataOutputStream.writeInt(p_obj.i_vx);
        _dataOutputStream.writeInt(p_obj.i_vy);
        _dataOutputStream.writeInt(p_obj.i_angle);
        _dataOutputStream.writeInt(p_obj.i_radius);

        p_obj = p_PlayerShortWeapon;

        _dataOutputStream.writeBoolean(p_obj.lg_Active);
        _dataOutputStream.writeByte(p_obj.i_Frame);
        _dataOutputStream.writeBoolean(p_obj.lg_backmove);

        _dataOutputStream.writeByte(p_obj.i_delay);
        _dataOutputStream.writeInt(p_obj.i8_scrx);
        _dataOutputStream.writeInt(p_obj.i8_scry);
        _dataOutputStream.writeInt(p_obj.i_vx);
        _dataOutputStream.writeInt(p_obj.i_vy);
        _dataOutputStream.writeInt(p_obj.i_angle);
        _dataOutputStream.writeInt(p_obj.i_radius);
    }

    public void readFromStream(DataInputStream _dataInputStream) throws IOException
    {
        i_PlayerBulletType = _dataInputStream.readByte();
        i_Attemptions = _dataInputStream.readByte();
        i_playerbulletinactive = _dataInputStream.readByte();
        lg_BossMode = _dataInputStream.readBoolean();

        i_enemydelay = _dataInputStream.readShort();
        i_shielddelay = _dataInputStream.readShort();
        i_energyshielddelay = _dataInputStream.readShort();
        i_firedelay = _dataInputStream.readShort();
        i_playerpower =  _dataInputStream.readShort();
        i_bosspower = _dataInputStream.readShort();
        i8_backgroundway = _dataInputStream.readInt();
        i_NonDestroyableDelay = _dataInputStream.readInt();

        // Writing player's data

        p_Player.lg_Destroyed = _dataInputStream.readBoolean();
        p_Player.setFrame(_dataInputStream.readByte());
        p_Player.i_tick = _dataInputStream.readByte();
        p_Player.i8_cx = _dataInputStream.readInt();
        p_Player.i8_cy = _dataInputStream.readInt();

        // moving objects

        MovingObject p_obj = null;
        for(int li=0;li<ap_MovingObjects.length;li++)
        {
            p_obj = ap_MovingObjects[li];

            p_obj.activate(_dataInputStream.readByte(),0,0);
            p_obj.lg_Active = _dataInputStream.readBoolean();
            p_obj.i_Frame = _dataInputStream.readByte();
            p_obj.lg_backmove = _dataInputStream.readBoolean();

            p_obj.i_delay = _dataInputStream.readByte();
            p_obj.i8_scrx = _dataInputStream.readInt();
            p_obj.i8_scry = _dataInputStream.readInt();
            p_obj.i_vx = _dataInputStream.readInt();
            p_obj.i_vy = _dataInputStream.readInt();
            p_obj.i_angle = _dataInputStream.readInt();
            p_obj.i_radius = _dataInputStream.readInt();
        }

        for(int li=0;li<ap_PlayerBullets.length;li++)
        {
            p_obj = ap_PlayerBullets[li];

            p_obj.activate(_dataInputStream.readByte(),0,0);
            p_obj.lg_Active = _dataInputStream.readBoolean();
            p_obj.i_Frame = _dataInputStream.readByte();
            p_obj.lg_backmove = _dataInputStream.readBoolean();

            p_obj.i_delay =_dataInputStream.readByte();
            p_obj.i8_scrx = _dataInputStream.readInt();
            p_obj.i8_scry = _dataInputStream.readInt();
            p_obj.i_vx = _dataInputStream.readInt();
            p_obj.i_vy =_dataInputStream.readInt();
        }

        p_obj = p_PlayerShield;

        p_obj.lg_Active = _dataInputStream.readBoolean();
        p_obj.i_Frame = _dataInputStream.readByte();
        p_obj.lg_backmove = _dataInputStream.readBoolean();

        p_obj.i_delay = _dataInputStream.readByte();
        p_obj.i8_scrx = _dataInputStream.readInt();
        p_obj.i8_scry = _dataInputStream.readInt();
        p_obj.i_vx = _dataInputStream.readInt();
        p_obj.i_vy = _dataInputStream.readInt();
        p_obj.i_angle = _dataInputStream.readInt();
        p_obj.i_radius = _dataInputStream.readInt();


        p_obj = p_PlayerShortWeapon;

        p_obj.lg_Active = _dataInputStream.readBoolean();
        p_obj.i_Frame = _dataInputStream.readByte();
        p_obj.lg_backmove = _dataInputStream.readBoolean();

        p_obj.i_delay = _dataInputStream.readByte();
        p_obj.i8_scrx = _dataInputStream.readInt();
        p_obj.i8_scry = _dataInputStream.readInt();
        p_obj.i_vx = _dataInputStream.readInt();
        p_obj.i_vy = _dataInputStream.readInt();
        p_obj.i_angle = _dataInputStream.readInt();
        p_obj.i_radius = _dataInputStream.readInt();
    }
}
