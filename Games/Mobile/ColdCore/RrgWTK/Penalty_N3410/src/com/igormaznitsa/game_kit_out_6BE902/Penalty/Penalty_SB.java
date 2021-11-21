package com.igormaznitsa.game_kit_out_6BE902.Penalty;

import com.igormaznitsa.gameapi.GameActionListener;
import com.igormaznitsa.gameapi.GamePlayerBlock;
import com.igormaznitsa.gameapi.StrategicBlock;
import com.igormaznitsa.gameapi.GameStateBlock;
import com.igormaznitsa.gameapi.util.RndGenerator;

public class Penalty_SB extends StrategicBlock
{
    public static final byte LEVEL0 = 0;
    public static final byte LEVEL0_DEVIATION = 0;

    public static final byte LEVEL1 = 1;
    public static final byte LEVEL1_DEVIATION = 3;

    public static final byte LEVEL2 = 2;
    public static final byte LEVEL2_DEVIATION = 5;

    public static final byte MAX_Z = 12;

    public static final byte MODE_GOALKEEPER = 0;
    public static final byte MODE_PLAYER = 1;

    protected static final byte SUBMODE_BEGIN = 0;
    protected static final byte SUBMODE_SELECTSIDE = 1;
    protected static final byte SUBMODE_SELECTCORNER = 2;
    protected static final byte SUBMODE_AIMOVE = 3;
    protected static final byte SUBMODE_BEAT = 4;

    protected static final byte SIDE_LEFT = 0;
    protected static final byte SIDE_CENTER = 1;
    protected static final byte SIDE_RIGHT = 2;

    protected static final byte CORNER_LEFTDOWN = 0;
    protected static final byte CORNER_LEFTCENTER = 1;
    protected static final byte CORNER_LEFTUP = 2;
    protected static final byte CORNER_CENTERDOWN = 3;
    protected static final byte CORNER_CENTERCENTER = 4;
    protected static final byte CORNER_CENTERUP = 5;
    protected static final byte CORNER_RIGHTDOWN = 6;
    protected static final byte CORNER_RIGHTCENTER = 7;
    protected static final byte CORNER_RIGHTUP = 8;

    public static final byte RESULT_GOAL = 0;
    public static final byte RESULT_NOHIT = 1;
    public static final byte RESULT_GATE = 2;
    public static final byte RESULT_GOALKEEPER = 3;

    public static final RndGenerator p_rnd = new RndGenerator(System.currentTimeMillis());
    protected Penalty_GSB p_gsb;
    protected AIPlayer p_aiplayer;

    protected int i_beatresult;

    public int getBeatResult()
    {
        return i_beatresult;
    }

    public int getPlayerScore()
    {
        return p_gsb.i_playerScore;
    }

    public void newGameSession(int level)
    {
        super.newGameSession(level);
        p_gsb = new Penalty_GSB(i_screenWidth, i_screenHeight);
        p_gameStateBlock = p_gsb;
        p_gsb.initLevel(level);
    }


    public PlayerObj getPlayerObj()
    {
        return p_gsb.p_playerobj;
    }

    public GoalkeeperObj getGoalkeeperObj()
    {
        return p_gsb.p_goalkeeperobj;
    }

    public Penalty_SB(int screenWidth, int screenHeight, GamePlayerBlock gamePlayerBlock, GamePlayerBlock aiPlayerBlock, GameActionListener gameActionListener)
    {
        super(screenWidth, screenHeight, gamePlayerBlock, aiPlayerBlock, gameActionListener);
        i_gameFlags = FLAG_LEVELS_SUPPORT | FLAG_SAVELOAD_SUPPORT | FLAG_SCORES_SUPPORT;
        p_aiplayer = new AIPlayer();
    }

    public Gate getGate()
    {
        return p_gsb.p_gate;
    }

    public Ball getBall()
    {
        return p_gsb.p_ball;
    }

    public GuideSign[] getGuideArray()
    {
        return p_gsb.ap_guidesigns;
    }

    public int getMode()
    {
        return p_gsb.i_mode;
    }

    public int getSubMode()
    {
        return p_gsb.i_submode;
    }

    private boolean modeSelectCorner(int _button)
    {
        int i_x = (p_gsb.i_selected >> 4) & 0x0F;
        int i_y = p_gsb.i_selected & 0x0F;

        switch (_button)
        {
            case Penalty_PMR.BUTTON_UP:
                {
                    if (i_y > 0) i_y--;
                }
                ;
                break;
            case Penalty_PMR.BUTTON_DOWN:
                {
                    if (i_y < 2) i_y++;
                }
                ;
                break;
            case Penalty_PMR.BUTTON_LEFT:
                {
                    if (i_x > 0) i_x--;
                }
                ;
                break;
            case Penalty_PMR.BUTTON_RIGHT:
                {
                    if (i_x < 2) i_x++;
                }
                ;
                break;
            case Penalty_PMR.BUTTON_FIRE:
                {
                    GuideSign p_obj = p_gsb.ap_guidesigns[i_x + i_y * 3];

                    switch (p_gsb.i_selected)
                    {
                        case 0x00:
                            p_gsb.i_selected = CORNER_LEFTDOWN;
                            break;
                        case 0x01:
                            p_gsb.i_selected = CORNER_LEFTCENTER;
                            break;
                        case 0x02:
                            p_gsb.i_selected = CORNER_LEFTUP;
                            break;
                        case 0x10:
                            p_gsb.i_selected = CORNER_CENTERDOWN;
                            break;
                        case 0x11:
                            p_gsb.i_selected = CORNER_CENTERCENTER;
                            break;
                        case 0x12:
                            p_gsb.i_selected = CORNER_CENTERUP;
                            break;
                        case 0x20:
                            p_gsb.i_selected = CORNER_RIGHTDOWN;
                            break;
                        case 0x21:
                            p_gsb.i_selected = CORNER_RIGHTCENTER;
                            break;
                        case 0x22:
                            p_gsb.i_selected = CORNER_RIGHTUP;
                            break;
                    }

                    int i_deviation = p_gsb.i_deviation;

                    switch (p_gsb.i_playerside)
                    {
                        case SIDE_CENTER:
                            {
                                i_deviation = i_deviation * 2;
                            };break;
                        case SIDE_RIGHT:
                            {
                                switch (p_gsb.i_selected)
                                {
                                    case CORNER_RIGHTCENTER:
                                    case CORNER_RIGHTDOWN:
                                    case CORNER_RIGHTUP:
                                        {
                                            i_deviation = i_deviation * 2;
                                        }
                                        ;
                                        break;
                                }
                            }
                            ;
                            break;
                        case SIDE_LEFT:
                            {
                                switch (p_gsb.i_selected)
                                {
                                    case CORNER_LEFTCENTER:
                                    case CORNER_LEFTDOWN:
                                    case CORNER_LEFTUP:
                                        {
                                            i_deviation = i_deviation * 2;
                                        }
                                        ;
                                        break;
                                }
                            }
                            ;
                            break;
                    }


                    int i_gy = p_gsb.p_gate.getScrY() + p_gsb.p_gate.getScrHeight();

                    p_gsb.i_endballx = p_obj.getScrX() + (p_rnd.getInt(i_deviation * 2) - i_deviation);
                    p_gsb.i_endbally = p_obj.getScrY() + (p_rnd.getInt(i_deviation * 2) - i_deviation);

                    if (p_gsb.i_mode == MODE_PLAYER && (p_gsb.i_endbally + (Ball.ai_sizes[Ball.ai_sizes.length-1]>>1))>i_gy)
                    {
                        p_gsb.i_endbally = i_gy - (Ball.ai_sizes[Ball.ai_sizes.length-1]>>1)-1;
                    }

                    if (p_gsb.i_mode == MODE_PLAYER)
                    {
                        p_gsb.i_endballx += (GuideSign.SIGHT_FAR_WIDTH >> 1);
                        p_gsb.i_endbally += (GuideSign.SIGHT_FAR_HEIGHT >> 1);
                    }
                    else
                    {
                        p_gsb.i_endballx += (GuideSign.SIGHT_NEAR_WIDTH >> 1);
                        p_gsb.i_endbally += (GuideSign.SIGHT_NEAR_HEIGHT >> 1);
                    }

                    p_gsb.i_mx = p_gsb.i_startballx + ((p_gsb.i_endballx - p_gsb.i_startballx) >> 1);
                    p_gsb.i_my = p_gsb.i_startbally + ((p_gsb.i_endbally - p_gsb.i_startbally) >> 1);

                    p_gsb.i_my -= 8;

                    p_gsb.i8_xcoeff1 = ((p_gsb.i_mx - p_gsb.i_startballx) << 8) / (MAX_Z >> 1);
                    p_gsb.i8_ycoeff1 = ((p_gsb.i_my - p_gsb.i_startbally) << 8) / (MAX_Z >> 1);

                    p_gsb.i8_xcoeff2 = ((p_gsb.i_endballx - p_gsb.i_mx) << 8) / (MAX_Z >> 1);
                    p_gsb.i8_ycoeff2 = ((p_gsb.i_endbally - p_gsb.i_my) << 8) / (MAX_Z >> 1);

                    return true;
                }
        }
        p_gsb.i_selected = (i_x << 4) | i_y;
        p_gsb.selectGateSight(p_gsb.i_selected);
        return false;
    }

    private boolean selectSide(int _button)
    {
        switch (_button)
        {
            case Penalty_PMR.BUTTON_LEFT:
                {
                    switch (p_gsb.i_selected)
                    {
                        case SIDE_LEFT:
                            p_gsb.i_selected = SIDE_RIGHT;
                            break;
                        case SIDE_CENTER:
                            p_gsb.i_selected = SIDE_LEFT;
                            break;
                        case SIDE_RIGHT:
                            p_gsb.i_selected = SIDE_CENTER;
                            break;
                    }
                }
                ;
                break;
            case Penalty_PMR.BUTTON_RIGHT:
                {
                    switch (p_gsb.i_selected)
                    {
                        case SIDE_LEFT:
                            p_gsb.i_selected = SIDE_CENTER;
                            break;
                        case SIDE_CENTER:
                            p_gsb.i_selected = SIDE_RIGHT;
                            break;
                        case SIDE_RIGHT:
                            p_gsb.i_selected = SIDE_LEFT;
                            break;
                    }
                }
                ;
                break;
            case Penalty_PMR.BUTTON_FIRE:
                {
                    p_gsb.i_startballx = p_gsb.p_ball.i_x;
                    p_gsb.i_startbally = p_gsb.p_ball.i_y;

                    return true;
                }
        }
        p_gsb.i_playerside = p_gsb.i_selected;

        p_gsb.selectSideArrow(p_gsb.i_selected);
        return false;
    }

    public int getAttemptions()
    {
        return p_gsb.i_attemptions >> 1;
    }

    protected int checkResult()
    {
        Ball p_ball = p_gsb.p_ball;
        Gate p_gate = p_gsb.p_gate;
        GoalkeeperObj p_gl = p_gsb.p_goalkeeperobj;

        // Checking the ball and the goalkeeper
        if (!((p_ball.getScrX() + p_ball.getScrWidth() <= p_gl.getScrX()) || (p_ball.getScrY() + p_ball.getScrHeight() <= p_gl.getScrY()) || (p_ball.getScrX() >= p_gl.getScrX() + p_gl.getScrWidth()) || (p_ball.getScrY() >= p_gl.getScrY() + p_gl.getScrHeight()))) return RESULT_GOALKEEPER;

        // Checking the ball and the gate
        if (!((p_ball.getScrX() + p_ball.getScrWidth() <= p_gate.getScrX()) || (p_ball.getScrY() + p_ball.getScrHeight() <= p_gate.getScrY()) || (p_ball.getScrX() >= p_gate.getScrX() + p_gate.getScrWidth()) || (p_ball.getScrY() >= p_gate.getScrY() + p_gate.getScrHeight())))
        {
            // Check weights and the ball

            // Left weight
            if (p_ball.getScrX() < p_gate.getScrX() && (p_ball.getScrX() + p_ball.getScrWidth()) > p_gate.getScrX()) return RESULT_GATE;
            // Top weight
            if (p_ball.getScrY() < p_gate.getScrY() && (p_ball.getScrY() + p_ball.getScrHeight()) > p_gate.getScrY()) return RESULT_GATE;
            // Right weight
            if (p_ball.getScrX() < (p_gate.getScrX() + p_gate.getScrWidth()) && (p_ball.getScrX() + p_ball.getScrWidth()) > (p_gate.getScrX() + p_gate.getScrWidth())) return RESULT_GATE;
            return RESULT_GOAL;
        }
        return RESULT_NOHIT;
    }

    private int cornerToPackedCoords(int _corner)
    {
        int i_x = 0;
        int i_y = 0;
        int i_state = 0;

        switch (_corner)
        {
            case CORNER_CENTERCENTER:
                {
                    i_x = 1;
                    i_y = 1;
                    i_state = GoalkeeperObj.STATE_STAND;
                }
                ;
                break;
            case CORNER_CENTERDOWN:
                {
                    i_x = 1;
                    i_y = 0;
                    i_state = GoalkeeperObj.STATE_JUMPTOP;
                }
                ;
                break;
            case CORNER_CENTERUP:
                {
                    i_x = 1;
                    i_y = 2;
                    i_state = GoalkeeperObj.STATE_JUMPDOWN;
                }
                ;
                break;
            case CORNER_LEFTCENTER:
                {
                    i_x = 0;
                    i_y = 1;
                    i_state = GoalkeeperObj.STATE_JUMPLEFT;
                }
                ;
                break;
            case CORNER_LEFTDOWN:
                {
                    i_x = 0;
                    i_y = 0;
                    i_state = GoalkeeperObj.STATE_JUMPLEFT;
                }
                ;
                break;
            case CORNER_LEFTUP:
                {
                    i_x = 0;
                    i_y = 2;
                    i_state = GoalkeeperObj.STATE_JUMPLEFT;
                }
                ;
                break;
            case CORNER_RIGHTCENTER:
                {
                    i_x = 2;
                    i_y = 1;
                    i_state = GoalkeeperObj.STATE_JUMPRIGHT;
                }
                ;
                break;
            case CORNER_RIGHTDOWN:
                {
                    i_x = 2;
                    i_y = 0;
                    i_state = GoalkeeperObj.STATE_JUMPRIGHT;
                }
                ;
                break;
            case CORNER_RIGHTUP:
                {
                    i_x = 2;
                    i_y = 2;
                    i_state = GoalkeeperObj.STATE_JUMPRIGHT;
                }
                ;
                break;
        }
        return (i_x << 4) | i_y | (i_state << 8);
    }

    public void nextGameStep()
    {
        Penalty_PMR p_pmr = (Penalty_PMR) p_gamePlayerBlock.getPlayerMoveRecord(p_gameStateBlock);
        int i_button = p_pmr.i_value;
        p_pmr.i_value = Penalty_PMR.BUTTON_NONE;


        if (i_button == Penalty_PMR.BUTTON_NONE && !(p_gsb.i_submode == SUBMODE_BEGIN || p_gsb.i_submode == SUBMODE_AIMOVE || p_gsb.i_submode == SUBMODE_BEAT)) return;

        switch (p_gsb.i_mode)
        {
            case MODE_GOALKEEPER:
                {
                    switch (p_gsb.i_submode)
                    {
                        case SUBMODE_BEGIN:
                            {
                                p_gsb.setSubmode(SUBMODE_AIMOVE);
                            }
                            ;
                            break;
                        case SUBMODE_AIMOVE:
                            {
                                p_gsb.i_startballx = p_gsb.p_ball.i_x;
                                p_gsb.i_startbally = p_gsb.p_ball.i_y;

                                p_gsb.i_opponentside = p_aiplayer.getAIPlayerMove(MODE_GOALKEEPER, SUBMODE_SELECTSIDE, 0);
                                p_gsb.i_opponentcorner = p_aiplayer.getAIPlayerMove(MODE_GOALKEEPER, SUBMODE_SELECTCORNER, 0);
                                p_gsb.setSubmode(SUBMODE_SELECTCORNER);

                                int i_x = 0;
                                switch (p_gsb.i_opponentside)
                                {
                                    case SIDE_LEFT:
                                        {
                                            i_x = p_gsb.p_ball.i_x - PlayerObj.FRAME_WIDTH;
                                        }
                                        ;
                                        break;
                                    case SIDE_CENTER:
                                        {
                                            i_x = p_gsb.p_ball.i_x - (PlayerObj.FRAME_WIDTH >> 1);
                                        }
                                        ;
                                        break;
                                    case SIDE_RIGHT:
                                        {
                                            i_x = p_gsb.p_ball.i_x;
                                        }
                                        ;
                                        break;
                                }

                                p_gsb.p_playerobj.initPlayer(i_x, p_gsb.p_ball.getScrY() + p_gsb.p_ball.getScrHeight() - PlayerObj.FRAME_HEIGHT);
                            }
                            ;
                            break;
                        case SUBMODE_SELECTCORNER:
                            {
                                if (modeSelectCorner(i_button))
                                {
                                    p_gsb.i_playercorner = p_gsb.i_selected;
                                    int i_packsxy = cornerToPackedCoords(p_gsb.i_playercorner);
                                    int i_x = (i_packsxy >>> 4) & 0x0F;
                                    int i_y = i_packsxy & 0x0F;
                                    int i_state = i_packsxy >>> 8;

                                    GuideSign p_obj = p_gsb.ap_guidesigns[i_x + i_y * 3];


                                    GoalkeeperObj p_gl = p_gsb.p_goalkeeperobj;

                                    p_gl.setState(i_state);
                                    p_gl.setStateForZ(p_gsb.p_ball.i_z, p_gsb.i_mode);

                                    p_gl.i_startx = p_gl.i_x;
                                    p_gl.i_starty = p_gl.i_y;

                                    if (i_state == GoalkeeperObj.STATE_STAND)
                                    {
                                        p_gl.i_midx = p_gl.i_startx;
                                        p_gl.i_midy = p_gl.i_starty;
                                        p_gl.i_endx = p_gl.i_midx;
                                        p_gl.i_endy = p_gl.i_midy;
                                    }
                                    else
                                    {
                                        p_gl.i_midx = p_obj.i_x + (GuideSign.SIGHT_NEAR_WIDTH >> 1);
                                        p_gl.i_midy = p_obj.i_y + (GuideSign.SIGHT_NEAR_HEIGHT >> 1);

                                        p_gl.i_endx = p_gl.i_midx + ((p_gl.i_midx - p_gl.i_startx) >> 1);
                                        p_gl.i_endy = p_gsb.p_gate.getScrY() + p_gsb.p_gate.i_height;
                                    }


                                    p_gl.i8_xcoeff1 = ((p_gl.i_startx - p_gl.i_midx) << 8) / MAX_Z;
                                    p_gl.i8_ycoeff1 = ((p_gl.i_starty - p_gl.i_midy) << 8) / MAX_Z;

                                    p_gl.i8_xcoeff2 = ((p_gl.i_endx - p_gl.i_midx) << 8) / MAX_Z;
                                    p_gl.i8_ycoeff2 = ((p_gl.i_endy - p_gl.i_midy) << 8) / MAX_Z;


                                    int i_packedSXY = cornerToPackedCoords(p_gsb.i_opponentcorner);
                                    i_x = (i_packedSXY >>> 4) & 0x0F;
                                    i_y = i_packedSXY & 0x0F;

                                    p_obj = p_gsb.ap_guidesigns[i_x + i_y * 3];

                                    p_gsb.i_endballx = p_obj.getScrX();
                                    p_gsb.i_endbally = p_obj.getScrY() + Math.max(GuideSign.SIGHT_NEAR_HEIGHT >> 1, Ball.ai_sizes[1] >> 1) + 1;

                                    p_gsb.i_endballx += (GuideSign.SIGHT_NEAR_WIDTH >> 1);
                                    p_gsb.i_endbally += (GuideSign.SIGHT_NEAR_HEIGHT >> 1);

                                    int i_a = p_gsb.i_endballx;
                                    p_gsb.i_endballx = p_gsb.i_startballx;
                                    p_gsb.i_startballx = i_a;

                                    i_a = p_gsb.i_endbally;
                                    p_gsb.i_endbally = p_gsb.i_startbally;
                                    p_gsb.i_startbally = i_a;

                                    p_gsb.i_mx = p_gsb.i_startballx + ((p_gsb.i_endballx - p_gsb.i_startballx) >> 1);
                                    p_gsb.i_my = p_gsb.i_startbally + ((p_gsb.i_endbally - p_gsb.i_startbally) >> 1);

                                    p_gsb.i_my -= 8;

                                    p_gsb.i8_xcoeff1 = ((p_gsb.i_mx - p_gsb.i_startballx) << 8) / (MAX_Z >> 1);
                                    p_gsb.i8_ycoeff1 = ((p_gsb.i_my - p_gsb.i_startbally) << 8) / (MAX_Z >> 1);

                                    p_gsb.i8_xcoeff2 = ((p_gsb.i_endballx - p_gsb.i_mx) << 8) / (MAX_Z >> 1);
                                    p_gsb.i8_ycoeff2 = ((p_gsb.i_endbally - p_gsb.i_my) << 8) / (MAX_Z >> 1);

                                    p_gsb.setSubmode(SUBMODE_BEAT);
                                    p_gsb.p_playerobj.activate();
                                }
                            }
                            ;
                            break;
                        case SUBMODE_BEAT:
                            {
                                p_gsb.p_playerobj.processPlayer();

                                if (!p_gsb.p_playerobj.lg_beated) break;

                                int i_z = p_gsb.p_ball.i_z;

                                i_z--;
                                if (i_z < 0)
                                {
                                    i_beatresult = checkResult();
                                    processResults();
                                    return;
                                }
                                else
                                {
                                    p_gsb.p_ball.setZ(i_z);
                                    int i_x = 0;
                                    int i_y = 0;
                                    if (i_z < (MAX_Z >> 1))
                                    {
                                        i_x = p_gsb.i_startballx + ((i_z * p_gsb.i8_xcoeff1) >> 8);
                                        i_y = p_gsb.i_startbally + ((i_z * p_gsb.i8_ycoeff1) >> 8);
                                    }
                                    else
                                    {
                                        int i_zz = i_z - (MAX_Z >> 1);
                                        i_x = p_gsb.i_mx + ((i_zz * p_gsb.i8_xcoeff2) >> 8);
                                        i_y = p_gsb.i_my + ((i_zz * p_gsb.i8_ycoeff2) >> 8);
                                    }

                                    p_gsb.p_ball.setXY(i_x, i_y);

                                    GoalkeeperObj p_gk = p_gsb.p_goalkeeperobj;

                                    i_x = p_gk.i_midx + ((i_z * p_gk.i8_xcoeff1) >> 8);
                                    i_y = p_gk.i_midy + ((i_z * p_gk.i8_ycoeff1) >> 8);

                                    p_gk.setXY(i_x, i_y);
                                    p_gk.setStateForZ(i_z, p_gsb.i_mode);
                                }
                            }
                            ;
                            break;
                    }
                }
                ;
                break;
            case MODE_PLAYER:
                {
                    switch (p_gsb.i_submode)
                    {
                        case SUBMODE_BEGIN:
                            {
                                p_gsb.setSubmode(SUBMODE_SELECTSIDE);
                            }
                            ;
                            break;
                        case SUBMODE_AIMOVE:
                            {
                                p_gsb.i_opponentcorner = p_aiplayer.getAIPlayerMove(MODE_PLAYER, SUBMODE_SELECTCORNER, p_gsb.i_playerside);
                                p_gsb.setSubmode(SUBMODE_BEAT);

                                int i_packsxy = cornerToPackedCoords(p_gsb.i_opponentcorner);

                                int i_state = i_packsxy >>> 8;
                                int i_x = (i_packsxy >>> 4) & 0x0f;
                                int i_y = i_packsxy & 0x0F;

                                GuideSign p_obj = p_gsb.ap_guidesigns[i_x + i_y * 3];

                                GoalkeeperObj p_gl = p_gsb.p_goalkeeperobj;
                                p_gl.setState(i_state);
                                p_gl.setStateForZ(p_gsb.p_ball.i_z, p_gsb.i_mode);

                                p_gl.i_startx = p_gl.i_x;
                                p_gl.i_starty = p_gl.i_y;

                                if (i_state == GoalkeeperObj.STATE_STAND)
                                {
                                    p_gl.i_midx = p_gl.i_startx;
                                    p_gl.i_midy = p_gl.i_starty;
                                    p_gl.i_endx = p_gl.i_midx;
                                    p_gl.i_endy = p_gl.i_midy;
                                }
                                else
                                {
                                    p_gl.i_midx = p_obj.i_x + (GuideSign.SIGHT_FAR_WIDTH >> 1);
                                    p_gl.i_midy = p_obj.i_y + (GuideSign.SIGHT_FAR_HEIGHT >> 1);

                                    p_gl.i_endx = p_gl.i_midx + ((p_gl.i_midx - p_gl.i_startx) >> 1);
                                    p_gl.i_endy = p_gsb.p_gate.getScrY() + p_gsb.p_gate.i_height;
                                }
                                p_gl.i8_xcoeff1 = ((p_gl.i_midx - p_gl.i_startx) << 8) / MAX_Z;
                                p_gl.i8_ycoeff1 = ((p_gl.i_midy - p_gl.i_starty) << 8) / MAX_Z;

                                p_gl.i8_xcoeff2 = ((p_gl.i_endx - p_gl.i_midx) << 8) / MAX_Z;
                                p_gl.i8_ycoeff2 = ((p_gl.i_endy - p_gl.i_midy) << 8) / MAX_Z;

                            }
                            ;
                            break;
                        case SUBMODE_SELECTSIDE:
                            {
                                if (selectSide(i_button))
                                {
                                    p_gsb.setSubmode(SUBMODE_SELECTCORNER);
                                }
                            }
                            ;
                            break;
                        case SUBMODE_SELECTCORNER:
                            {
                                if (modeSelectCorner(i_button))
                                {
                                    p_gsb.setSubmode(SUBMODE_AIMOVE);
                                }
                            }
                            ;
                            break;
                        case SUBMODE_BEAT:
                            {
                                int i_z = p_gsb.p_ball.i_z;

                                i_z++;
                                if (i_z > MAX_Z)
                                {
                                    i_beatresult = checkResult();
                                    processResults();
                                    return;
                                }
                                else
                                {
                                    p_gsb.p_ball.setZ(i_z);
                                    int i_x = 0;
                                    int i_y = 0;
                                    if (i_z < (MAX_Z >> 1))
                                    {
                                        i_x = p_gsb.i_startballx + ((i_z * p_gsb.i8_xcoeff1) >> 8);
                                        i_y = p_gsb.i_startbally + ((i_z * p_gsb.i8_ycoeff1) >> 8);
                                    }
                                    else
                                    {
                                        int i_zz = i_z - (MAX_Z >> 1);
                                        i_x = p_gsb.i_mx + ((i_zz * p_gsb.i8_xcoeff2) >> 8);
                                        i_y = p_gsb.i_my + ((i_zz * p_gsb.i8_ycoeff2) >> 8);
                                    }

                                    p_gsb.p_ball.setXY(i_x, i_y);

                                    GoalkeeperObj p_gk = p_gsb.p_goalkeeperobj;

                                    i_x = p_gk.i_startx + ((i_z * p_gk.i8_xcoeff1) >> 8);
                                    i_y = p_gk.i_starty + ((i_z * p_gk.i8_ycoeff1) >> 8);
                                    p_gk.setStateForZ(i_z, p_gsb.i_mode);
                                    p_gk.setXY(i_x, i_y);
                                }
                            }
                            ;
                            break;
                    }
                }
                ;
                break;
        }

    }

    public void resumeSession()
    {
        p_gsb.i_playerState = GameStateBlock.PLAYERSTATE_NORMAL;
        if (p_gsb.i_mode == MODE_PLAYER)
        {
            p_gsb.setMode(MODE_GOALKEEPER);
        }
        else
        {
            p_gsb.setMode(MODE_PLAYER);
        }
    }

    private void processResults()
    {
        if (i_beatresult == RESULT_GOAL)
        {
            if (p_gsb.i_mode == MODE_GOALKEEPER)
            {
                p_gsb.i_aiScore++;
                p_gsb.i_playerState = GameStateBlock.PLAYERSTATE_LOST;
                p_gsb.i_aiState = GameStateBlock.PLAYERSTATE_WON;
            }
            else
            {
                p_gsb.i_playerScore++;
                p_gsb.i_playerState = GameStateBlock.PLAYERSTATE_WON;
                p_gsb.i_aiState = GameStateBlock.PLAYERSTATE_LOST;
            }
        }
        else
        {
            if (p_gsb.i_mode == MODE_GOALKEEPER)
            {
                p_gsb.i_playerState = GameStateBlock.PLAYERSTATE_WON;
                p_gsb.i_aiState = GameStateBlock.PLAYERSTATE_LOST;
            }
            else
            {
                p_gsb.i_playerState = GameStateBlock.PLAYERSTATE_LOST;
                p_gsb.i_aiState = GameStateBlock.PLAYERSTATE_WON;
            }
        }

        p_gsb.i_attemptions++;
        if (p_gsb.i_attemptions == 10)
        {
            p_gsb.i_attemptions = 0;
            p_gsb.i_tour++;
            if (p_gsb.i_playerScore > p_gsb.i_aiScore)
            {
                p_gsb.i_playerState = GameStateBlock.PLAYERSTATE_WON;
                p_gsb.i_aiState = GameStateBlock.PLAYERSTATE_LOST;
                p_gsb.i_gameState = GameStateBlock.GAMESTATE_OVER;
            }
            else if (p_gsb.i_playerScore < p_gsb.i_aiScore)
            {
                p_gsb.i_playerState = GameStateBlock.PLAYERSTATE_LOST;
                p_gsb.i_aiState = GameStateBlock.PLAYERSTATE_WON;
                p_gsb.i_gameState = GameStateBlock.GAMESTATE_OVER;
            }
        }
    }

    public String getGameID()
    {
        return "PENALTY";
    }

    public int getMaxSizeOfSavedGameBlock()
    {
        return 256;
    }
}
