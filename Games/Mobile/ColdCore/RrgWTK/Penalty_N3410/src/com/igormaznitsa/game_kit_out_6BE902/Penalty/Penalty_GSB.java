package com.igormaznitsa.game_kit_out_6BE902.Penalty;

import com.igormaznitsa.gameapi.GameStateBlock;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class Penalty_GSB extends GameStateBlock
{
    protected int i_mode;
    protected int i_submode;
    protected int i_penaltycounter;
    protected int i_selected;

    protected Ball p_ball;
    protected Gate p_gate;
    protected GuideSign[] ap_guidesigns;

    protected int i_scrw;
    protected int i_scrh;

    private int i_gatex_goalkeeper;
    private int i_gatey_goalkeeper;
    private int i_gatex_player;
    private int i_gatey_player;

    private int i_ballx_goalkeeper;
    private int i_bally_goalkeeper;
    private int i_ballx_player;
    private int i_bally_player;

    protected int i8_xcoeff1;
    protected int i8_ycoeff1;

    protected int i8_xcoeff2;
    protected int i8_ycoeff2;

    protected int i_mx;
    protected int i_my;

    protected int i_deviation;

    protected PlayerObj p_playerobj;
    protected GoalkeeperObj p_goalkeeperobj;

    protected int i_tour;
    protected int i_attemptions;

//==================================
    protected int i_playerside;
    protected int i_playercorner;
    protected int i_playerforce;

    protected int i_opponentside;
    protected int i_opponentcorner;
    protected int i_opponentforce;

    protected int i_startballx;
    protected int i_startbally;
    protected int i_endballx;
    protected int i_endbally;

    public void readFromStream(DataInputStream dataInputStream) throws IOException
    {
        setMode(dataInputStream.readUnsignedByte());
        setSubmode(dataInputStream.readUnsignedByte());

        i_opponentcorner = dataInputStream.readUnsignedByte();
        i_opponentside = dataInputStream.readUnsignedByte();
        i_playercorner = dataInputStream.readUnsignedByte();
        i_playerside = dataInputStream.readUnsignedByte();

        i_penaltycounter = dataInputStream.readUnsignedByte();
        i_selected = dataInputStream.readInt();

        i_tour = dataInputStream.readUnsignedByte();
        i_attemptions = dataInputStream.readUnsignedByte();

        p_ball.setXY(dataInputStream.readShort(),dataInputStream.readShort());
        p_ball.setZ(dataInputStream.readByte());


        for(int li=0;li<9;li++)
        {
            GuideSign p_sign = ap_guidesigns[li];
             p_sign.i_type =  dataInputStream.readByte();
            p_sign.i_x = dataInputStream.readShort();
            p_sign.i_y = dataInputStream.readShort();
            p_sign.lg_active = dataInputStream.readBoolean();
            p_sign.lg_selected = dataInputStream.readBoolean();
        }

        i8_xcoeff1 = dataInputStream.readInt();
        i8_ycoeff1 = dataInputStream.readInt();
        i8_xcoeff2 = dataInputStream.readInt();
        i8_ycoeff2 = dataInputStream.readInt();
        i_mx = dataInputStream.readShort();
        i_my = dataInputStream.readShort();

        // Writing player
        p_playerobj.i_frame = dataInputStream.readUnsignedByte();
        p_playerobj.i_tick = dataInputStream.readUnsignedByte();
        p_playerobj.i_scrx = dataInputStream.readShort();
        p_playerobj.i_scry = dataInputStream.readShort();

        p_playerobj.lg_active = dataInputStream.readBoolean();
        p_playerobj.lg_beated = dataInputStream.readBoolean();

        // Writing goalkeeper
        p_goalkeeperobj.setState(dataInputStream.readByte());
        p_goalkeeperobj.setStateForZ(p_ball.i_z,i_mode);
        p_goalkeeperobj.i8_xcoeff1 = dataInputStream.readInt();
        p_goalkeeperobj.i8_ycoeff1 = dataInputStream.readInt();
        p_goalkeeperobj.i8_xcoeff2 = dataInputStream.readInt();
        p_goalkeeperobj.i8_ycoeff2 = dataInputStream.readInt();
        p_goalkeeperobj.i_endx = dataInputStream.readShort();
        p_goalkeeperobj.i_endy = dataInputStream.readShort();
        p_goalkeeperobj.i_midx = dataInputStream.readShort();
        p_goalkeeperobj.i_midy = dataInputStream.readShort();
        p_goalkeeperobj.i_startx = dataInputStream.readShort();
        p_goalkeeperobj.i_starty = dataInputStream.readShort();

        p_goalkeeperobj.i_x = dataInputStream.readShort();
        p_goalkeeperobj.i_y = dataInputStream.readShort();
    }

    public void writeToStream(DataOutputStream dataOutputStream) throws IOException
    {
        dataOutputStream.writeByte(i_mode);
        dataOutputStream.writeByte(i_submode);

        dataOutputStream.writeByte(i_opponentcorner);
        dataOutputStream.writeByte(i_opponentside);
        dataOutputStream.writeByte(i_playercorner);
        dataOutputStream.writeByte(i_playerside);

        dataOutputStream.writeByte(i_penaltycounter);
        dataOutputStream.writeInt(i_selected);

        dataOutputStream.writeByte(i_tour);
        dataOutputStream.writeByte(i_attemptions);

        dataOutputStream.writeShort(p_ball.i_x);
        dataOutputStream.writeShort(p_ball.i_y);
        dataOutputStream.writeByte(p_ball.i_z);


        for(int li=0;li<9;li++)
        {
            GuideSign p_sign = ap_guidesigns[li];
            dataOutputStream.writeByte(p_sign.i_type);
            dataOutputStream.writeShort(p_sign.i_x);
            dataOutputStream.writeShort(p_sign.i_y);
            dataOutputStream.writeBoolean(p_sign.lg_active);
            dataOutputStream.writeBoolean(p_sign.lg_selected);
        }

        dataOutputStream.writeInt(i8_xcoeff1);
        dataOutputStream.writeInt(i8_ycoeff1);
        dataOutputStream.writeInt(i8_xcoeff2);
        dataOutputStream.writeInt(i8_ycoeff2);
        dataOutputStream.writeShort(i_mx);
        dataOutputStream.writeShort(i_my);

        // Writing player
        dataOutputStream.writeByte(p_playerobj.i_frame);
        dataOutputStream.writeByte(p_playerobj.i_tick);
        dataOutputStream.writeShort(p_playerobj.i_scrx);
        dataOutputStream.writeShort(p_playerobj.i_scry);

        dataOutputStream.writeBoolean(p_playerobj.lg_active);
        dataOutputStream.writeBoolean(p_playerobj.lg_beated);

        // Writing goalkeeper
        dataOutputStream.writeByte(p_goalkeeperobj.i_nextstate);
        dataOutputStream.writeInt(p_goalkeeperobj.i8_xcoeff1);
        dataOutputStream.writeInt(p_goalkeeperobj.i8_ycoeff1);
        dataOutputStream.writeInt(p_goalkeeperobj.i8_xcoeff2);
        dataOutputStream.writeInt(p_goalkeeperobj.i8_ycoeff2);
        dataOutputStream.writeShort(p_goalkeeperobj.i_endx);
        dataOutputStream.writeShort(p_goalkeeperobj.i_endy);
        dataOutputStream.writeShort(p_goalkeeperobj.i_midx);
        dataOutputStream.writeShort(p_goalkeeperobj.i_midy);
        dataOutputStream.writeShort(p_goalkeeperobj.i_startx);
        dataOutputStream.writeShort(p_goalkeeperobj.i_starty);

        dataOutputStream.writeShort(p_goalkeeperobj.i_x);
        dataOutputStream.writeShort(p_goalkeeperobj.i_y);
   }

    public int getPlayerScore()
    {
        int i_diff = i_playerScore - i_aiScore;
        if (i_diff<=0 )
        {
            return 0;
        }
        else
        {
            return (i_diff * (i_gameLevel+1) * 100);
        }
    }

    public Penalty_GSB(int _scrwdth, int _scrhght)
    {
        i_scrh = _scrhght;
        i_scrw = _scrwdth;
        p_ball = new Ball();
        p_gate = new Gate();
        ap_guidesigns = new GuideSign[9];
        for (int li = 0; li < 9; li++)
        {
            ap_guidesigns[li] = new GuideSign();
        }

        i_gatex_goalkeeper = (_scrwdth - Gate.GATEWIDTH_NEAR) >> 1;
          i_gatey_goalkeeper = _scrhght - Gate.GATEHEIGHT_NEAR - 3;
          i_gatex_player = (_scrwdth - Gate.GATEWIDTH_FAR) >> 1;
          i_gatey_player = 25;

          i_ballx_goalkeeper = _scrwdth >> 1;
          i_ballx_player = _scrwdth >> 1;
          i_bally_goalkeeper = 40 - (Ball.ai_sizes[Ball.ai_sizes.length - 1]);
          i_bally_player = _scrhght - (GuideSign.SIDEARROW_HEIGHT >> 1) - (Ball.ai_sizes[1] >> 1);
          p_playerobj = new PlayerObj();
          p_goalkeeperobj = new GoalkeeperObj();

    }

    protected void deactivateAllSigns()
    {
        for (int li = 0; li < 9; li++)
        {
            ap_guidesigns[li].lg_active = false;
        }
    }

    public void initLevel(int level)
    {
        super.initLevel(level);
        setMode(Penalty_SB.p_rnd.getInt(10) > 5 ? Penalty_SB.MODE_GOALKEEPER : Penalty_SB.MODE_PLAYER);
        //setMode(Penalty_SB.MODE_PLAYER);

        switch (level)
        {
            case Penalty_SB.LEVEL0:
                i_deviation = Penalty_SB.LEVEL0_DEVIATION;
                break;
            case Penalty_SB.LEVEL1:
                i_deviation = Penalty_SB.LEVEL1_DEVIATION;
                break;
            case Penalty_SB.LEVEL2:
                i_deviation = Penalty_SB.LEVEL2_DEVIATION;
                break;
        }
    }

    protected void selectSideArrow(int _value)
    {
        for (int li = 0; li < 9; li++)
        {
            if (ap_guidesigns[li].i_type == _value)
                ap_guidesigns[li].lg_selected = true;
            else
                ap_guidesigns[li].lg_selected = false;
        }
    }

    protected void selectGateSight(int _value)
    {
        int i_x = (_value >> 4) & 0x0F;
        int i_y = _value & 0x0F;
        int i_indx = i_x + i_y * 3;
        for (int li = 0; li < 9; li++)
        {
            if (li == i_indx)
                ap_guidesigns[li].lg_selected = true;
            else
                ap_guidesigns[li].lg_selected = false;
        }
    }

    private void fillSideArrows()
    {
        deactivateAllSigns();
        ap_guidesigns[Penalty_SB.SIDE_LEFT].activate(GuideSign.TYPE_SIDEARROWLEFT, p_ball.getScrX() - GuideSign.SIDEARROW_WIDTH - 1, p_ball.getScrY() + ((p_ball.getScrHeight() - GuideSign.SIDEARROW_HEIGHT) >> 1));
        ap_guidesigns[Penalty_SB.SIDE_CENTER].activate(GuideSign.TYPE_SIDEARROWCENTER, p_ball.getScrX() + ((p_ball.getScrWidth() - GuideSign.SIDEARROW_WIDTH) >> 1), i_scrh - GuideSign.SIDEARROW_HEIGHT);
        ap_guidesigns[Penalty_SB.SIDE_RIGHT].activate(GuideSign.TYPE_SIDEARROWRIGHT, p_ball.getScrX() + p_ball.getScrWidth() + 1, p_ball.getScrY() + ((p_ball.getScrHeight() - GuideSign.SIDEARROW_HEIGHT) >> 1));
    }

    private void fillGateSight(int _sghtwdth, int _sghthght)
    {
        int i_gatex = p_gate.i_scrx;
        int i_gatey = p_gate.i_scry;
        int i_stepx = p_gate.i_width / 3;
        int i_stepy = p_gate.i_height / 3;

        int i_offstx = 0;
        int i_offsty = 0;
        for (int li = 0; li < 9; li++)
        {
            GuideSign p_obj = ap_guidesigns[li];
            int i_x = li % 3;
            int i_y = li / 3;

            switch (i_x)
            {
                case 0:
                    i_offstx = 0;
                    break;
                case 1:
                    i_offstx = (i_stepx - _sghtwdth) >> 1;
                    break;
                case 2:
                    i_offstx = i_stepx - _sghtwdth;
                    break;
            }

            switch (i_y)
            {
                case 0:
                    i_offsty = 0;
                    break;
                case 1:
                    i_offsty = (i_stepy - _sghthght) >> 1;
                    break;
                case 2:
                    i_offsty = i_stepy - _sghthght;
                    break;
            }

            p_obj.activate(GuideSign.TYPE_GATESIGHT, i_gatex + i_x * i_stepx + i_offstx, i_gatey + i_y * i_stepy + i_offsty);
        }
    }

    public void setSubmode(int _submode)
    {
        i_submode = _submode;
        deactivateAllSigns();

        switch (i_mode)
        {
            case Penalty_SB.MODE_GOALKEEPER:
                {
                    switch (i_submode)
                    {
                        case Penalty_SB.SUBMODE_SELECTCORNER:
                            {
                                i_selected = 0x11;
                                fillGateSight(GuideSign.SIGHT_NEAR_WIDTH, GuideSign.SIGHT_NEAR_HEIGHT);
                                selectGateSight(i_selected);
                            }
                            ;
                            break;
                        case Penalty_SB.SUBMODE_BEAT:
                            {
                                i_playerforce = i_selected;
                            }
                            ;
                            break;
                    }
                }
                ;
                break;
            case Penalty_SB.MODE_PLAYER:
                {
                    switch (i_submode)
                    {
                        case Penalty_SB.SUBMODE_SELECTCORNER:
                            {
                                i_playerside = i_selected;
                                i_selected = 0x11;
                                fillGateSight(GuideSign.SIGHT_FAR_WIDTH, GuideSign.SIGHT_FAR_HEIGHT);
                                selectGateSight(i_selected);
                            }
                            ;
                            break;
                        case Penalty_SB.SUBMODE_SELECTSIDE:
                            {
                                fillSideArrows();
                                i_selected = Penalty_SB.SIDE_CENTER;
                                selectSideArrow(i_selected);
                            }
                            ;
                            break;
                    }
                }
                ;
                break;
        }
    }

    public void setMode(int _mode)
    {
        i_mode = _mode;
        switch (i_mode)
        {
            case Penalty_SB.MODE_GOALKEEPER:
                {
                    p_ball.initBall(i_ballx_goalkeeper, i_bally_goalkeeper, Penalty_SB.MAX_Z);
                    p_gate.initGate(i_gatex_goalkeeper, i_gatey_goalkeeper, Gate.GATEMODE_NEAR);
                    p_goalkeeperobj.initMode(_mode,p_gate.getScrX()+(p_gate.i_width>>1),p_gate.getScrY()+p_gate.i_height);
                }
                ;
                break;
            case Penalty_SB.MODE_PLAYER:
                {
                    p_ball.initBall(i_ballx_player, i_bally_player, 0);
                    p_gate.initGate(i_gatex_player, i_gatey_player, Gate.GATEMODE_FAR);
                    p_goalkeeperobj.initMode(_mode,p_gate.getScrX()+(p_gate.i_width>>1),p_gate.getScrY()+p_gate.i_height);
                }
                ;
                break;
        }
        setSubmode(Penalty_SB.SUBMODE_BEGIN);
        p_goalkeeperobj.setState(GoalkeeperObj.STATE_STAND);
        p_goalkeeperobj.setStateForZ(p_ball.i_z,_mode);
    }

}
