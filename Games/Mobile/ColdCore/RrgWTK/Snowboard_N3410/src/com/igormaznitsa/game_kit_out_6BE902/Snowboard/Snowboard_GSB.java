package com.igormaznitsa.game_kit_out_6BE902.Snowboard;

import com.igormaznitsa.gameapi.GameStateBlock;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class Snowboard_GSB extends GameStateBlock
{
    public int i_Attemptions = 0;
    protected int i_obstacleDelay = 0;
    public Player p_player;

    public Obstacle [] ap_obstacles = null;

    protected int i_wayposition;
    protected int i_jumpmode = 0;
    protected int i_curObstacleDelay = 0;
    protected int i_jumpheight = 0;
    protected int i_curjumpheight = 0;
    protected boolean lg_jumpdirectup = false;

    public int i_BorderPosition = 0;

    protected Obstacle getInactiveObstacle()
    {
        if(!ap_obstacles[i_BorderPosition].lg_isactive){
	   int i = i_BorderPosition;
	   if(++i_BorderPosition>=ap_obstacles.length)i_BorderPosition=0;
	   return ap_obstacles[i];
        }
        return null;
    }

    public void initLevel(int _level)
    {
        super.initLevel(_level);

        switch (_level)
        {
            case Snowboard_SB.LEVEL0:
                {
                    i_obstacleDelay = Snowboard_SB.LEVEL0_OBSTACLESDELAY;
                    i_Attemptions = Snowboard_SB.LEVEL0_ATTEMPTIONS;
                }
                ;
                break;
            case Snowboard_SB.LEVEL1:
                {
                    i_obstacleDelay = Snowboard_SB.LEVEL1_OBSTACLESDELAY;
                    i_Attemptions = Snowboard_SB.LEVEL1_ATTEMPTIONS;
                }
                ;
                break;
            case Snowboard_SB.LEVEL2:
                {
                    i_obstacleDelay = Snowboard_SB.LEVEL2_OBSTACLESDELAY;
                    i_Attemptions = Snowboard_SB.LEVEL2_ATTEMPTIONS;
                }
                ;
                break;
        }

        ap_obstacles = new Obstacle[Snowboard_SB.MAX_OBSTACLES];

        for(int li=0;li<Snowboard_SB.MAX_OBSTACLES;li++)
        {
            ap_obstacles [li] = new Obstacle();
        }

        p_player = new Player();
        i_wayposition = Snowboard_SB.WAY_LENGTH;
    }

    protected void deactivateAllObstacles()
    {
        for(int li=0;li<ap_obstacles.length;li++)
        {
            ap_obstacles[li].lg_isactive = false;
        }
        i_BorderPosition = 0;
    }

    public void readFromStream(DataInputStream _dataInputStream) throws IOException
    {
        i_Attemptions = _dataInputStream.readUnsignedByte();
        i_obstacleDelay =  _dataInputStream.readUnsignedShort();

        i_wayposition = _dataInputStream.readInt();
        i_jumpmode = _dataInputStream.readShort();
        i_curObstacleDelay = _dataInputStream.readShort();
        i_BorderPosition = _dataInputStream.readUnsignedByte();

        i_jumpheight =  _dataInputStream.readShort();
        i_curjumpheight =  _dataInputStream.readShort();
        lg_jumpdirectup =  _dataInputStream.readBoolean();

        // Reading Player data
        p_player.i_state =  _dataInputStream.readUnsignedByte();
        p_player.i_frame = _dataInputStream.readUnsignedByte();
        p_player.i_scrx = _dataInputStream.readUnsignedShort();
        p_player.i_scry = _dataInputStream.readUnsignedShort();
        p_player.i_tick = _dataInputStream.readUnsignedShort();
        p_player.i_maxticks = _dataInputStream.readUnsignedShort();
        p_player.lg_backmove = _dataInputStream.readBoolean();

        // Reading of obstacle data
        for(int li=0;li<ap_obstacles.length;li++)
        {
            Obstacle p_obj = ap_obstacles[li];

            p_obj.activate(_dataInputStream.readByte(),0);
            p_obj.lg_isactive = _dataInputStream.readBoolean();
            p_obj.i_frame = _dataInputStream.readUnsignedByte();
            p_obj.i_scrX = _dataInputStream.readShort();
            p_obj.i_scrY = _dataInputStream.readShort();
            p_obj.i_X = _dataInputStream.readShort();
            p_obj.i_Z = _dataInputStream.readShort();
        }
    }

    public int getPlayerScore()
    {
        int i8_waypercent = (((Snowboard_SB.WAY_LENGTH - i_wayposition)<<8)*100)/Snowboard_SB.WAY_LENGTH;
        return i_playerScore + (((i_gameLevel+1)*i8_waypercent)>>8);
    }

    public void writeToStream(DataOutputStream _dataOutputStream) throws IOException
    {
        _dataOutputStream.writeByte(i_Attemptions);
        _dataOutputStream.writeShort(i_obstacleDelay);

        _dataOutputStream.writeInt(i_wayposition);
        _dataOutputStream.writeShort(i_jumpmode);
        _dataOutputStream.writeShort(i_curObstacleDelay);
        _dataOutputStream.writeByte(i_BorderPosition);

        _dataOutputStream.writeShort(i_jumpheight);
        _dataOutputStream.writeShort(i_curjumpheight);
        _dataOutputStream.writeBoolean(lg_jumpdirectup);

        // Writing Player data
        _dataOutputStream.writeByte(p_player.i_state);
        _dataOutputStream.writeByte(p_player.i_frame);
        _dataOutputStream.writeShort(p_player.i_scrx);
        _dataOutputStream.writeShort(p_player.i_scry);
        _dataOutputStream.writeShort(p_player.i_tick);
        _dataOutputStream.writeShort(p_player.i_maxticks);
        _dataOutputStream.writeBoolean(p_player.lg_backmove);


        // Writing of obstacle data
        for(int li=0;li<ap_obstacles.length;li++)
        {
            Obstacle p_obj = ap_obstacles[li];
            _dataOutputStream.writeByte(p_obj.b_type);
            _dataOutputStream.writeBoolean(p_obj.lg_isactive);
            _dataOutputStream.writeByte(p_obj.i_frame);
            _dataOutputStream.writeShort(p_obj.i_scrX);
            _dataOutputStream.writeShort(p_obj.i_scrY);
            _dataOutputStream.writeShort(p_obj.i_X);
            _dataOutputStream.writeShort(p_obj.i_Z);
        }

    }
}
