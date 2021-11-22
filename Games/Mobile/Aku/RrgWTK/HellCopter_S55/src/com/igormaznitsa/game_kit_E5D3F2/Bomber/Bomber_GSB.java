package com.igormaznitsa.game_kit_E5D3F2.Bomber;

import com.igormaznitsa.gameapi.GameStateBlock;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.DataInputStream;

public class Bomber_GSB extends GameStateBlock
{
    protected int i_bomb_counter;
    protected int i_attemptions;
    protected int i_timedelay;
    protected int i_init_bomb_number;

    protected AnimationObject[] ap_exploded_objects;
    protected BombObject[] ap_bomb_objects;

    protected byte[] ab_stage_array = null;

    protected int i_player_x;
    protected int i_player_y;
    protected int i_playeranimationstate;
    protected int i_playertick;
    protected int i_playerframe;

    protected int i_blocksnumber;
    protected int i_tickbeforenextbomb;

    public void writeToStream(DataOutputStream dataOutputStream) throws IOException
    {
        dataOutputStream.writeShort(i_bomb_counter);
        dataOutputStream.writeByte(i_attemptions);
        dataOutputStream.writeShort(i_player_x);
        dataOutputStream.writeShort(i_player_y);
        dataOutputStream.writeByte(i_playeranimationstate);
        dataOutputStream.writeByte(i_playertick);
        dataOutputStream.writeByte(i_playerframe);
        dataOutputStream.writeShort(i_blocksnumber);
        dataOutputStream.writeShort(i_tickbeforenextbomb);

        for (int li = 0;li < ap_exploded_objects.length;li++)
        {
            AnimationObject p_obj = ap_exploded_objects[li];
            dataOutputStream.writeBoolean(p_obj.lg_is_active);
            if (p_obj.isActive())
            {
                dataOutputStream.writeByte(p_obj.i_frame);
                dataOutputStream.writeByte(p_obj.i_scrx);
                dataOutputStream.writeShort(p_obj.i8_scry);
                dataOutputStream.writeByte(p_obj.i_tick);
                dataOutputStream.writeByte(p_obj.i_type);
            }
        }

        for (int li = 0;li < ap_bomb_objects.length;li++)
        {
            BombObject p_obj = ap_bomb_objects[li];
            dataOutputStream.writeBoolean(p_obj.lg_is_active);
            if (p_obj.isActive())
            {
                dataOutputStream.writeByte(p_obj.i_destroyed);
                dataOutputStream.writeByte(p_obj.i_scrx);
                dataOutputStream.writeByte(p_obj.i_scry);
            }
        }

        for (int li = 0;li < ab_stage_array.length;li++)
        {
            dataOutputStream.writeByte(ab_stage_array[li]);
        }
    }

    public void readFromStream(DataInputStream dataInputStream) throws IOException
    {
        i_bomb_counter = dataInputStream.readUnsignedShort();
        i_attemptions = dataInputStream.readUnsignedByte();
        i_player_x = dataInputStream.readUnsignedShort();
        i_player_y = dataInputStream.readUnsignedShort();
        i_playeranimationstate = dataInputStream.readUnsignedByte();
        i_playertick = dataInputStream.readUnsignedByte();
        i_playerframe = dataInputStream.readUnsignedByte();
        i_blocksnumber = dataInputStream.readUnsignedShort();
        i_tickbeforenextbomb = dataInputStream.readUnsignedShort();

        for (int li = 0;li < ap_exploded_objects.length;li++)
        {
            AnimationObject p_obj = ap_exploded_objects[li];
            p_obj.lg_is_active = dataInputStream.readBoolean();
            if (p_obj.isActive())
            {
                p_obj.i_frame = dataInputStream.readUnsignedByte();
                p_obj.i_scrx = dataInputStream.readUnsignedByte();
                p_obj.i8_scry = dataInputStream.readShort();
                p_obj.i_tick = dataInputStream.readUnsignedByte();
                p_obj.i_type = dataInputStream.readUnsignedByte();
            }
        }

        for (int li = 0;li < ap_bomb_objects.length;li++)
        {
            BombObject p_obj = ap_bomb_objects[li];
            p_obj.lg_is_active = dataInputStream.readBoolean();
            if (p_obj.isActive())
            {
                p_obj.i_destroyed = dataInputStream.readUnsignedByte();
                p_obj.i_scrx = dataInputStream.readUnsignedByte();
                p_obj.i_scry = dataInputStream.readUnsignedByte();
            }
        }

        for (int li = 0;li < ab_stage_array.length;li++)
        {
            ab_stage_array[li] = dataInputStream.readByte();
        }
    }

    public void initLevel(int i)
    {
        super.initLevel(i);
        switch (i)
        {
            case Bomber_SB.LEVEL0:
                {
                    i_init_bomb_number = Bomber_SB.LEVEL0_BOMB;
                    i_timedelay = Bomber_SB.LEVEL0_DELAY;
                }
                ;
                break;
            case Bomber_SB.LEVEL1:
                {
                    i_init_bomb_number = Bomber_SB.LEVEL1_BOMB;
                    i_timedelay = Bomber_SB.LEVEL1_DELAY;
                }
                ;
                break;
            case Bomber_SB.LEVEL2:
                {
                    i_init_bomb_number = Bomber_SB.LEVEL2_BOMB;
                    i_timedelay = Bomber_SB.LEVEL2_DELAY;
                }
                ;
                break;
        }

        ap_exploded_objects = new AnimationObject[Bomber_SB.MAX_EPLODING_OBJECTS];
        for (int li = 0;li < ap_exploded_objects.length;li++)
        {
            ap_exploded_objects[li] = new AnimationObject();
        }

        ap_bomb_objects = new BombObject[Bomber_SB.MAX_BOMB_OBJECTS];
        for (int li = 0;li < ap_bomb_objects.length;li++)
        {
            ap_bomb_objects[li] = new BombObject();
        }

        i_attemptions = Bomber_SB.MAX_USER_ATTEMPTIONS;
    }

    protected void deactivateAllExplodedObjects()
    {
        for (int li = 0;li < ap_exploded_objects.length;li++)
        {
            ap_exploded_objects[li].lg_is_active = false;
        }
    }

    protected void deactivateAllBombObjects()
    {
        for (int li = 0;li < ap_bomb_objects.length;li++)
        {
            ap_bomb_objects[li].lg_is_active = false;
        }
    }

    protected AnimationObject getFirstInactive()
    {
        for (int li = 0;li < ap_exploded_objects.length;li++)
        {
            if (!ap_exploded_objects[li].lg_is_active) return ap_exploded_objects[li];
        }
        System.err.println("I can't find any empty object");
        return null;
    }

    protected BombObject getFirstInactiveBomb()
    {
        for (int li = 0;li < ap_bomb_objects.length;li++)
        {
            if (!ap_bomb_objects[li].lg_is_active) return ap_bomb_objects[li];
        }
        return null;
    }

    public void initStage(int i)
    {
        super.initStage(i);
        i_player_x = Bomber_SB.PLAYER_INIT_X;
        i_player_y = Bomber_SB.PLAYER_INIT_Y;
        i_playeranimationstate = Bomber_SB.PLAYER_INIT_STATE;

        deactivateAllExplodedObjects();
        deactivateAllBombObjects();
        ab_stage_array = Stages.getStage(i);
	i_bomb_counter = 0;
        i_blocksnumber = 0;
        i_tickbeforenextbomb = 0;

	for (int x=0;x<Stages.STAGE_WIDTH;x++)
	{
	  int vert = 0;
          for (int y = 0;y < Stages.STAGE_HEIGHT;y++)
            if (getElementAt(x,y) == Stages.ELE_HOUSE) vert++;
	  i_blocksnumber += vert;
	  i_bomb_counter += ((vert + Bomber_SB.BOMB_DESTROYING_DEPTH) / (Bomber_SB.BOMB_DESTROYING_DEPTH+1));
	}

        i_bomb_counter += i_init_bomb_number;
    }

    public int getPlayerScore()
    {
        return super.getPlayerScore() * (i_gameLevel + 1);
    }

    public byte getElementAt(int _x,int _y)
    {
        return ab_stage_array[_x + _y * Stages.STAGE_WIDTH];
    }

    protected void setElementAt(int _x,int _y,byte _elem)
    {
        ab_stage_array[_x + _y * Stages.STAGE_WIDTH] = _elem;
    }

    protected void setPlayerAnimationState(int _newstate)
    {
        i_playeranimationstate = _newstate;
        i_playerframe = 0;
        i_playertick = 0;
    }

}
