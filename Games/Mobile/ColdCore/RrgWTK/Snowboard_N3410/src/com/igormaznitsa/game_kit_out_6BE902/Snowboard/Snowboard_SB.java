package com.igormaznitsa.game_kit_out_6BE902.Snowboard;

import com.igormaznitsa.gameapi.GameActionListener;
import com.igormaznitsa.gameapi.StrategicBlock;
import com.igormaznitsa.gameapi.GameStateBlock;

import java.io.DataInputStream;
import java.io.IOException;

public class Snowboard_SB extends StrategicBlock
{
    public static final int GAMEACTION_JUMP = 0;

    public static final int WAY_LENGTH = 5000;

    public static final int LEVEL0 = 0;
    public static final int LEVEL0_OBSTACLESDELAY = 10;
    public static final int LEVEL0_ATTEMPTIONS = 4;

    public static final int LEVEL1 = 1;
    public static final int LEVEL1_OBSTACLESDELAY = 6;
    public static final int LEVEL1_ATTEMPTIONS = 4;

    public static final int LEVEL2 = 2;
    public static final int LEVEL2_OBSTACLESDELAY = 3;
    public static final int LEVEL2_ATTEMPTIONS = 4;

    public static final int MAX_OBSTACLES = 20;

    public static final int PLAYER_FRAMEWIDTH = 37;
    public static final int PLAYER_FRAMEHEIGHT = 37;

    protected static final int I_OBSTACLESTARTZ = 100;
    protected static final int I_OBSTACLEZSPEED = 1;

    private static final byte PLAYER_HORZSPEED = 4;
    private static final byte PLAYER_VERTSPEED = 2;
    public static final int I_OBSTACLE_STARTY = 20;
    private static final int I8_SPEEDCOEFF = (PLAYER_HORZSPEED << 8) / (Player.MAXFRAMES_ROADSTATE >> 1);
    private static final int I8_OBSTACLEZFRAME = (I_OBSTACLESTARTZ << 8) / Obstacle.MAXFRAMES;

    private static final int I_MAXHEIGHTOFJUMP = 27;
    private static final int I_MINHEIGHTOFJUMP = 9;
    private static final int I_MAXJUMPCENTER = 10;
    private static final int I8_JUMPCOEFF = ((I_MAXHEIGHTOFJUMP - I_MINHEIGHTOFJUMP) << 8) / I_MAXJUMPCENTER;

    private static final int PLAYER_SENSEXOFFST = 10;

    public Snowboard_GSB p_gsb;

    private int i_centerScreenX;

    private static final int STATE_NONE = 0;
    private static final int STATE_JUMPING = 1;
    private static final int STATE_FALLEN = 2;

    private Score p_score;

    public Snowboard_SB(int screenWidth,int screenHeight,GameActionListener gameActionListener)
    {
        super(screenWidth,screenHeight,gameActionListener);
        i_centerScreenX = screenWidth >> 1;
        p_score = new Score();
    }

    public void resumeGame()
    {
        p_gsb.deactivateAllObstacles();
        initPlayerPosition();
        p_gsb.i_playerState = GameStateBlock.PLAYERSTATE_NORMAL;
        p_score.lg_active = false;
    }

    private void initPlayerPosition()
    {
        p_gsb.p_player.i_scrx = (i_screenWidth - PLAYER_FRAMEWIDTH) >> 1;
        p_gsb.p_player.i_scry = i_screenHeight - PLAYER_FRAMEHEIGHT - 1;
        p_gsb.p_player.setState(Player.STATE_ROAD,0);
    }

    public void loadGameState(DataInputStream _dataInputStream) throws IOException
    {
        super.loadGameState(_dataInputStream);
        p_score.lg_active = false;
    }

    public void nextGameStep(Object _move)
    {
        Snowboard_PMR p_pmr = (Snowboard_PMR)_move;
        Player p_player = p_gsb.p_player;

        if (p_gsb.i_curObstacleDelay > 0)
            p_gsb.i_curObstacleDelay--;
        else
            if (p_player.i_state != Player.STATE_FALLEN) generateNewObstacle();

        boolean lg_ispeak = p_player.process(p_pmr.i_value);

        switch (p_player.i_state)
        {
            case Player.STATE_FALLEN:
                {
                    if (lg_ispeak)
                    {
                        p_gsb.i_Attemptions--;
                        p_gsb.i_playerState = GameStateBlock.PLAYERSTATE_LOST;
                        if (p_gsb.i_Attemptions == 0) p_gsb.i_gameState = GameStateBlock.GAMESTATE_OVER;
                        p_score.lg_active = false;
                    }
                }
                ;
                break;
            case Player.STATE_JUMP:
                {
                    if (p_gsb.lg_jumpdirectup)
                    {
                        p_gsb.i_curjumpheight += PLAYER_VERTSPEED;
                        if (p_gsb.i_curjumpheight >= p_gsb.i_jumpheight)
                        {
                            p_gsb.i_curjumpheight = p_gsb.i_jumpheight;
                            p_score.activation(p_player.i_scrx + (PLAYER_FRAMEWIDTH >> 1),p_player.i_scry,5-p_gsb.i_jumpmode);
                            p_gsb.lg_jumpdirectup = false;

                            p_gsb.i_playerScore += (100 + p_score.i_type*50);
                            if(p_gameActionListener!=null) p_gameActionListener.gameAction(GAMEACTION_JUMP);
                        }
                    }
                    else
                    {
                        p_gsb.i_curjumpheight -= PLAYER_VERTSPEED;
                        if (p_gsb.i_curjumpheight <= 0)
                        {
                            p_gsb.i_curjumpheight = 0;
                            p_player.setState(Player.STATE_ROAD,0);
                        }
                    }
                    p_player.i_scry = i_screenHeight - PLAYER_FRAMEHEIGHT - p_gsb.i_curjumpheight - 1;

                    p_gsb.i_wayposition--;
                }
                ;
            default :
                {
                    if (p_player.i_state != Player.STATE_JUMP)
                    {
                        int i_different = ((p_player.i_frame - 3) * I8_SPEEDCOEFF) >> 8;

                        if (i_different < 0)
                        {
                            if (p_player.i_scrx > 0 && (p_player.i_scrx + i_different >= 0)) p_player.i_scrx += i_different;
                        }
                        else if (i_different > 0)
                        {
                            if (p_player.i_scrx < (i_screenWidth - PLAYER_FRAMEWIDTH) && (p_player.i_scrx + i_different + PLAYER_FRAMEWIDTH < i_screenWidth)) p_player.i_scrx += i_different;
                        }
                    }
                    switch (processObstacles(p_player))
                    {
                        case STATE_FALLEN:
                            {
                                p_player.setState(Player.STATE_FALLEN,0);
                            }
                            ;
                            break;
                        case STATE_JUMPING:
                            {
                                int i_ticks = (((p_gsb.i_jumpheight << 8) / Player.MAXFRAMES_JUMPSTATE) / PLAYER_VERTSPEED) >> 8;
                                p_player.setState(Player.STATE_JUMP,i_ticks);
                                p_gsb.lg_jumpdirectup = true;
                            }
                            ;
                        default :
                            {
                                p_gsb.i_wayposition--;
                            }
                            ;
                            break;
                    }
                }

                if (p_gsb.i_wayposition<=0)
                {
                    if (p_gsb.i_playerState == GameStateBlock.PLAYERSTATE_NORMAL)
                    {
                        p_gsb.i_gameState = GameStateBlock.GAMESTATE_OVER;
                        p_gsb.i_playerState = GameStateBlock.PLAYERSTATE_WON;
                        p_score.lg_active = false;
                    }
                }
        }

        if (p_score.lg_active)
        {
            p_score.process();
        }
    }

    private void generateNewObstacle()
    {
        int i_type = getRandomInt(10);
        if (i_type == 5)
        {
            i_type = getRandomInt(30);

            if (i_type < 10)
            {
                i_type = Obstacle.OBSTACLE_FIR;
            }
            else if (i_type < 20)
            {
                i_type = Obstacle.OBSTACLE_HILL;
            }
            else if (i_type <= 30)
            {
                i_type = Obstacle.OBSTACLE_STONE;
            }

            Obstacle p_obst = p_gsb.getInactiveObstacle();
            if (p_obst == null) return;

            int i_x = getRandomInt(15) * 6 - 45;

            p_obst.activate((byte)i_type,i_x);
            p_gsb.i_curObstacleDelay = p_gsb.i_obstacleDelay;
        }
    }

    private int processObstacles(Player _player)
    {
        Obstacle[] ap_obstacles = p_gsb.ap_obstacles;

        int i_result = STATE_NONE;

        int i_plx = _player.i_scrx + PLAYER_SENSEXOFFST;
        int i_plwdt = PLAYER_FRAMEWIDTH - PLAYER_SENSEXOFFST * 2;
        int i_ply = _player.i_scry;
        int i_plhgt = PLAYER_FRAMEHEIGHT;

        for (int li = 0;li < ap_obstacles.length;li++)
        {
            Obstacle p_obj = ap_obstacles[li];
            if (!p_obj.lg_isactive) continue;

            int i_coef = ((I_OBSTACLESTARTZ - p_obj.i_Z) * 0x5) >> 8;
            p_obj.i_Z -= 2 + i_coef;

            if (p_obj.i_Z <= 0)
            {
                int i_w = p_obj.ab_cursizearray[p_obj.i_frame << 1];
                int i_h = p_obj.ab_cursizearray[(p_obj.i_frame << 1) + 1];
                int i_scrx = p_obj.i_scrX;
                int i_scry = p_obj.i_scrY;

                // Check player and this obstacle
                if (!((i_plx + i_plwdt <= i_scrx) || (i_ply + i_plhgt <= i_scry) || (i_plx >= i_scrx + i_w) || (i_ply >= i_scry + i_h)))
                {
                    switch (p_obj.b_type)
                    {
                        case Obstacle.OBSTACLE_STONE:
                        case Obstacle.OBSTACLE_FIR:
                            {
                                i_result = STATE_FALLEN;
                            }
                            ;
                            break;
                        case Obstacle.OBSTACLE_HILL:
                            {
                                if (_player.i_state == Player.STATE_ROAD)
                                {
                                    int i_diff = Math.abs(_player.i_scrx + (PLAYER_FRAMEWIDTH >> 1) - (i_scrx + (i_w >> 1)));
                                    p_gsb.i_jumpmode = i_diff >> 1;
                                    if (i_diff <= I_MAXJUMPCENTER)
                                    {
                                        i_diff = I_MAXJUMPCENTER - i_diff;
                                        p_gsb.i_jumpheight = ((i_diff * I8_JUMPCOEFF) >> 8) + I_MINHEIGHTOFJUMP;
                                        i_result = STATE_JUMPING;
                                    }
                                }
                            }
                            ;
                            break;
                    }
                }

                p_obj.lg_isactive = false;
                continue;
            }

            p_obj.i_frame = (p_obj.i_Z << 8) / I8_OBSTACLEZFRAME;
            if (p_obj.i_frame >= Obstacle.MAXFRAMES) p_obj.i_frame = Obstacle.MAXFRAMES - 1;

            int i_zx = (I_OBSTACLESTARTZ + 50) - p_obj.i_Z;
            int i_zy = I_OBSTACLESTARTZ - p_obj.i_Z;

            p_obj.i_scrX = i_centerScreenX + ((p_obj.i_X * (0x2 * i_zx)) >> 8);
            p_obj.i_scrY = I_OBSTACLE_STARTY + (((((i_screenHeight - I_OBSTACLE_STARTY) << 8) / I_OBSTACLESTARTZ) * i_zy) >> 8);
        }

        return i_result;
    }

    public Score getScoreObject()
    {
        return p_score;
    }

    public String getGameID()
    {
        return "SBOARD";
    }

    public void newGameSession(int level)
    {
        p_gsb = new Snowboard_GSB();
        p_gameStateBlock = p_gsb;
        p_gsb.initLevel(level);

        initPlayerPosition();
    }

    public void endGameSession()
    {
        super.endGameSession();
        p_gsb = null;
    }

    public int getMaxSizeOfSavedGameBlock()
    {
        return 512;
    }
}