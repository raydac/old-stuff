package com.igormaznitsa.game_kit_out_6BE902.DuckHunt;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.DataOutputStream;

public class DuckHunt_GSB
{
    public static final int GAMESTATE_PLAYED = 0;
    public static final int GAMESTATE_OVER = 1;
    public static final int PLAYERSTATE_NORMAL = 0;
    public static final int PLAYERSTATE_WON = 1;
    public static final int PLAYERSTATE_LOST = 2;

    public int i_gameLevel;
    public int i_gameStage;
    public int i_playerState;
    public int i_gameState;
    public int i_playerScore;


    /**
     * Initing of a game stage
     */
    public void initStage(int stage)
    {
        i_gameStage = stage;
//        i_aiState = PLAYERSTATE_NORMAL;
        i_playerState = PLAYERSTATE_NORMAL;
        i_gameState = GAMESTATE_PLAYED;
    }

    /**
     * Get current game state
     * @return the current game state as int value
     */
    public int getGameState()
    {
        return i_gameState;
    }


    /**
     * Get current player's state
     * @return the current player's state as int value
     */
    public int getPlayerState()
    {
        return i_playerState;
    }


    /**
     * Get current game level
     * @return the current game level as int value
     */
    public int getLevel()
    {
        return i_gameLevel;
    }

    /**
     * Get current game stage
     * @return the current game stage as int value
     */
    public int getStage()
    {
        return i_gameStage;
    }

    protected Gun p_gun;
    protected MoveObject[] ap_move_objects;
    protected int i8_currentduckspeed;
    protected int i_bullets;
    protected int i_hits;
    protected int i_shots;
    protected int i_duckscounter;
    protected int i_initbullets;
    protected long l_endtime;
    protected boolean lg_iskilled;
    protected int i_pause_sec = 0;

    private int i_scrheight;

    public void initLevel(int level)
    {
//        i_aiState = PLAYERSTATE_NORMAL;
        i_playerState = PLAYERSTATE_NORMAL;
        i_gameState = GAMESTATE_PLAYED;
        i_gameLevel = level;
        i_playerScore = 0;
 //       i_aiScore = 0;

        switch (level)
        {
            case DuckHunt_SB.LEVEL0:
                {
                    i8_currentduckspeed = DuckHunt_SB.LEVEL0_DUCKSPEED_I8;
                    i_bullets = DuckHunt_SB.LEVEL0_BULLETS;
                    l_endtime = System.currentTimeMillis() + DuckHunt_SB.LEVEL0_TIME;
                }
                ;
                break;
            case DuckHunt_SB.LEVEL1:
                {
                    i8_currentduckspeed = DuckHunt_SB.LEVEL1_DUCKSPEED_I8;
                    i_bullets = DuckHunt_SB.LEVEL1_BULLETS;
                    l_endtime = System.currentTimeMillis() + DuckHunt_SB.LEVEL1_TIME;
                }
                ;
                break;
            case DuckHunt_SB.LEVEL2:
                {
                    i8_currentduckspeed = DuckHunt_SB.LEVEL2_DUCKSPEED_I8;
                    i_bullets = DuckHunt_SB.LEVEL2_BULLETS;
                    l_endtime = System.currentTimeMillis() + DuckHunt_SB.LEVEL2_TIME;
                }
                ;
                break;
        }
        i_duckscounter = 0;
        deactivateAllMoveObjects();
        for(int li=0;li<(DuckHunt_SB.MAX_ANIMATION_OBJECTS>>1);li++)
        {
            ap_move_objects[li].activate(DuckHunt_SB.getInt(DuckHunt_SB.MAX_X_DIFFERENT << 1) - DuckHunt_SB.MAX_X_DIFFERENT, i_scrheight >> 1, MoveObject.OBJECT_CANE, DuckHunt_SB.getInt(DuckHunt_SB.MAX_OBJECT_DISTANCE>>3)<<3, true);
        }
        i_initbullets = i_bullets;
        i_hits = 0;
        i_shots = 0;
    }

    public void readFromStream(DataInputStream dataInputStream) throws IOException
    {
        i_duckscounter = dataInputStream.readInt();
        //dataOutputStream.writeBoolean(lg_iskilled);
        lg_iskilled = dataInputStream.readBoolean();
        //dataOutputStream.writeByte(i_hits);
        i_hits = dataInputStream.readUnsignedByte();
        //dataOutputStream.writeByte(i_shots);
        i_shots = dataInputStream.readUnsignedByte();
        //dataOutputStream.writeByte(i_bullets);
        i_bullets = dataInputStream.readUnsignedByte();
        //dataOutputStream.writeShort((int)((l_endtime - System.currentTimeMillis())/1000));
        i_pause_sec = dataInputStream.readUnsignedShort();

        //dataOutputStream.writeByte(p_gun.i_state);
        int i_state = dataInputStream.readByte();
        p_gun.setState(i_state);

        //dataOutputStream.writeBoolean(p_gun.lg_backmove);
        p_gun.lg_backmove = dataInputStream.readBoolean();

        //dataOutputStream.writeInt(p_gun.i8_sight_scrx);
        int i8_sight_scrx = dataInputStream.readInt();
        //dataOutputStream.writeInt(p_gun.i8_sight_scry);
        int i8_sight_scry = dataInputStream.readInt();
        p_gun.setSightScrCoord(i8_sight_scrx,i8_sight_scry);

        //dataOutputStream.writeByte(p_gun.i_bulletsingun);
        p_gun.i_bulletsingun = dataInputStream.readUnsignedByte();
        //dataOutputStream.writeByte(p_gun.i_firePhase);
        p_gun.i_firePhase = dataInputStream.readUnsignedByte();
        //dataOutputStream.writeShort(p_gun.i_firingx);
        p_gun.i_firingx = dataInputStream.readUnsignedShort();
        //dataOutputStream.writeShort(p_gun.i_firingy);
        p_gun.i_firingy = dataInputStream.readUnsignedShort();
        //dataOutputStream.writeBoolean(p_gun.lg_sightvisibled);
        p_gun.lg_sightvisibled = dataInputStream.readBoolean();
        //dataOutputStream.writeByte(p_gun.i_frame);
        p_gun.i_frame = dataInputStream.readUnsignedByte();
        //dataOutputStream.writeByte(p_gun.i_tick);      //25
        p_gun.i_tick = dataInputStream.readUnsignedByte();

        for(int li=0;li<ap_move_objects.length;li++)
        {
            MoveObject p_move_object = ap_move_objects[li];
            p_move_object.lg_active = dataInputStream.readBoolean();
            if (p_move_object.lg_active)
            {
                p_move_object.i8_x = dataInputStream.readInt();
                p_move_object.i8_y = dataInputStream.readInt();

                p_move_object.i_scrx = dataInputStream.readShort();
                p_move_object.i_scry = dataInputStream.readShort();
                p_move_object.i8_distance = dataInputStream.readInt();
                p_move_object.setDistance(p_move_object.i8_distance);
                p_move_object.i_distanceframe = dataInputStream.readUnsignedByte();
                p_move_object.setType(dataInputStream.readUnsignedByte());
                p_move_object.i_state = dataInputStream.readByte();

                p_move_object.i_frame = dataInputStream.readUnsignedByte();
                p_move_object.i_tick =  dataInputStream.readUnsignedByte();
                p_move_object.lg_backanimation = dataInputStream.readBoolean();
                p_move_object.lg_backmove = dataInputStream.readBoolean();

                p_move_object.i_scrcenterx = dataInputStream.readInt();
                p_move_object.i_scrcentery = dataInputStream.readInt();
            }
        }
    }

    public void writeToStream(DataOutputStream dataOutputStream) throws IOException
    {
        dataOutputStream.writeInt(i_duckscounter);
        dataOutputStream.writeBoolean(lg_iskilled);
        dataOutputStream.writeByte(i_hits);
        dataOutputStream.writeByte(i_shots);
        dataOutputStream.writeByte(i_bullets);
        dataOutputStream.writeShort(i_pause_sec);

        dataOutputStream.writeByte(p_gun.i_state);
        dataOutputStream.writeBoolean(p_gun.lg_backmove);
        dataOutputStream.writeInt(p_gun.i8_sight_scrx);
        dataOutputStream.writeInt(p_gun.i8_sight_scry);
        dataOutputStream.writeByte(p_gun.i_bulletsingun);
        dataOutputStream.writeByte(p_gun.i_firePhase);
        dataOutputStream.writeShort(p_gun.i_firingx);
        dataOutputStream.writeShort(p_gun.i_firingy);
        dataOutputStream.writeBoolean(p_gun.lg_sightvisibled);
        dataOutputStream.writeByte(p_gun.i_frame);
        dataOutputStream.writeByte(p_gun.i_tick);      //25

        for(int li=0;li<ap_move_objects.length;li++)
        {
            MoveObject p_move_object = ap_move_objects[li];
            dataOutputStream.writeBoolean(p_move_object.lg_active);
            if (p_move_object.lg_active)
            {
                dataOutputStream.writeInt(p_move_object.i8_x);
                dataOutputStream.writeInt(p_move_object.i8_y);

                dataOutputStream.writeShort(p_move_object.i_scrx);
                dataOutputStream.writeShort(p_move_object.i_scry);
                dataOutputStream.writeInt(p_move_object.i8_distance);
                dataOutputStream.writeByte(p_move_object.i_distanceframe);
                dataOutputStream.writeByte(p_move_object.i_type);
                dataOutputStream.writeByte(p_move_object.i_state);

                dataOutputStream.writeByte(p_move_object.i_frame);
                dataOutputStream.writeByte(p_move_object.i_tick);
                dataOutputStream.writeBoolean(p_move_object.lg_backanimation);
                dataOutputStream.writeBoolean(p_move_object.lg_backmove);

                dataOutputStream.writeInt(p_move_object.i_scrcenterx);
                dataOutputStream.writeInt(p_move_object.i_scrcentery);     //31
            }
        }
    }

    public MoveObject[] getMoveObjects()
    {
        return ap_move_objects;
    }

    public int getCurrentBullets()
    {
        return i_bullets;
    }

    protected void deactivateAllMoveObjects()
    {
        for (int li = 0; li < DuckHunt_SB.MAX_ANIMATION_OBJECTS; li++) ap_move_objects[li].lg_active = false;
    }

    protected MoveObject getFirstInactiveMoveObject()
    {
        for (int li = 0; li < DuckHunt_SB.MAX_ANIMATION_OBJECTS; li++)
        {
            if (!ap_move_objects[li].lg_active) return ap_move_objects[li];
        }
        return null;
    }

    public DuckHunt_GSB(int _width, int _height)
    {
        p_gun = new Gun(_width, _height);

        i_scrheight = _height;

        ap_move_objects = new MoveObject[DuckHunt_SB.MAX_ANIMATION_OBJECTS];

        for (int li = 0; li < DuckHunt_SB.MAX_ANIMATION_OBJECTS; li++)
        {
            ap_move_objects[li] = new MoveObject(_width, _height);
        }

    }

    public int getPlayerScore()
    {
        return i_playerScore * (i_gameLevel+1);
    }

    public Gun getGunObject()
    {
        return p_gun;
    }


/*
    public int getAIScore() {  return i_aiScore; }
    public int getAIState() {  return i_aiState; }
    public int i_aiState;
    public int i_aiScore;

*/
}
