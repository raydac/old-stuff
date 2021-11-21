package com.igormaznitsa.game_kit_out_6BE902.DriveOff;

import com.igormaznitsa.gameapi.GameActionListener;
import com.igormaznitsa.gameapi.GamePlayerBlock;
import com.igormaznitsa.gameapi.StrategicBlock;
import com.igormaznitsa.gameapi.GameStateBlock;
import com.igormaznitsa.gameapi.util.RndGenerator;

public class DriveOff_SB extends StrategicBlock
{
    public static final int GAMEACTION_POLICE = 0;
    public static final int GAMEACTION_PLAYERKILLED = 1;
    public static final int GAMEACTION_PLAYEROFFROAD = 2;
    public static final int GAMEACTION_PLAYERCOLLIDED = 3;
    public static final int GAMEACTION_EXPLODING = 4;

    public static final int LEVEL0 = 0;
    public static final int LEVEL0_TIMEDELAY = 100;
    public static final int LEVEL0_HELYCOPTERS = 2;
    public static final int LEVEL0_COPCARS = 5;
    public static final int LEVEL0_ATTEMPTIONS = 3;
    public static final int LEVEL0_MAXOBJECTS = 3;
    public static final int LEVEL0_TRACKS = 20;


    public static final int LEVEL1 = 1;
    public static final int LEVEL1_TIMEDELAY = 80;
    public static final int LEVEL1_HELYCOPTERS = 3;
    public static final int LEVEL1_COPCARS = 7;
    public static final int LEVEL1_ATTEMPTIONS = 3;
    public static final int LEVEL1_MAXOBJECTS = 4;
    public static final int LEVEL1_TRACKS = 20;

    public static final int LEVEL2 = 2;
    public static final int LEVEL2_TIMEDELAY = 60;
    public static final int LEVEL2_HELYCOPTERS = 4;
    public static final int LEVEL2_COPCARS = 8;
    public static final int LEVEL2_ATTEMPTIONS = 3;
    public static final int LEVEL2_MAXOBJECTS = 5;
    public static final int LEVEL2_TRACKS = 20;

    public static final int VIRTUALCELL_WIDTH = 4;
    public static final int VIRTUALCELL_HEIGHT = 4;

    public static final int ANIMATION_OBJECTS = 30;
    protected static final int PLAYEROBJECT_INDEX = 0;
    protected static final int BOARD_INDEX = 20;

    private static final int SENSE_LENGTH = 4;
    private static final int FIRE_TIMEDELAY_TICKS = 13;
    private static final int POLICE_FIRE_TIMEDELAY = 6;

    public int HIGH_BORDER = 0;
    public int SCREEN_LINES = 0;
    public static final int PLAYER_CELLLINES = 3;

    private RndGenerator p_rnd = new RndGenerator(System.currentTimeMillis());
    private static final int HIT_OFFSET = 3;
    private static final int OFFROAD_OFFSET = 2;

    private DriveOff_GSB p_gsb;

    private static final int[] ai_table =
            {
                MoveObject.TYPE_CAR0,
                MoveObject.TYPE_CAR1,
                MoveObject.TYPE_CAR2,
                MoveObject.TYPE_MOTO,
                MoveObject.TYPE_TRAVELLER0,
                MoveObject.TYPE_TRAVELLER1
            };

    public int getTimeDelay()
    {
        return p_gsb.i_timedelay;
    }

    public void newGameSession(int level)
    {
        super.newGameSession(level);
        p_gsb = new DriveOff_GSB();
        p_gameStateBlock = p_gsb;
        p_gameStateBlock.initLevel(level);
    }

    protected void convertObjectToExplodeing(MoveObject _obj)
    {
        int i_x = _obj.i_scrx;
        int i_y = _obj.i_scry;
        int i_w = _obj.i_width;
        int i_h = _obj.i_height;

        _obj.activate(MoveObject.TYPE_EXPLODING, 0, 0);
        int i_exw = _obj.i_width;
        int i_exh = _obj.i_height;

        int i_explnum = i_h / i_exh;
        i_x = i_x + ((i_w - i_exw) >> 1);
        i_y = i_y + ((i_h - i_exh) >> 1);
        for (int li = 0; li <= i_explnum; li++)
        {
            if (li != 0)
            {
                _obj = p_gsb.getInactiveMoveObject();
            }
            if (_obj == null) return;

            _obj.activate(MoveObject.TYPE_EXPLODING, i_x, i_y + i_exh * li);
            _obj.setState(MoveObject.STATE_KILLED);
        }
    }

    protected void generateCopter()
    {
        MoveObject p_player = p_gsb.p_player;
        MoveObject p_obj = p_gsb.getInactiveMoveObject();
        if (p_obj == null) return;

        int i_miny = p_player.i_curline - BOARD_INDEX;

        if (i_miny < 0) i_miny += p_gsb.i_waylength;

        int i_scry = i_miny;
        i_scry = i_scry * VIRTUALCELL_HEIGHT;

        p_obj.activate(MoveObject.TYPE_HELYCOPTER, 0, i_scry);
        if (p_rnd.getInt(10) > 5)
        {
            p_obj.setX(32 * VIRTUALCELL_WIDTH - p_obj.i_width);
        }
        else
        {
            p_obj.setX(0);
        }
    }

    protected void generatePolicecar()
    {
        MoveObject p_player = p_gsb.p_player;
        MoveObject p_obj = p_gsb.getInactiveMoveObject();
        if (p_obj == null) return;

        int i_miny = p_player.i_curline - BOARD_INDEX;

        if (i_miny < 0) i_miny += p_gsb.i_waylength;

        int i_scry = i_miny;
        i_scry = i_scry * VIRTUALCELL_HEIGHT;

        p_obj.activate(MoveObject.TYPE_POLICECAR, 0, i_scry);
        switch (p_rnd.getInt(29) / 10)
        {
            case 0:
                p_gsb.centeredMoveObjectOnRoad(p_obj);
                break;
            case 1:
                p_gsb.leftMoveObjectOnRoad(p_obj);
                break;
            case 2:
                p_gsb.rightMoveObjectOnRoad(p_obj);
                break;
        }
        if (p_gameActionListener!=null) p_gameActionListener.gameAction(GAMEACTION_POLICE);
    }

    protected void generateNewCar()
    {
        MoveObject p_player = p_gsb.p_player;
        MoveObject p_obj = p_gsb.getInactiveMoveObject();
        if (p_obj == null) return;

        int i_maxy = p_player.i_curline + BOARD_INDEX;
        int i_miny = p_player.i_curline - BOARD_INDEX;

        if (i_maxy >= p_gsb.i_waylength) i_maxy -= p_gsb.i_waylength;
        if (i_miny < 0) i_miny += p_gsb.i_waylength;

        int i_scry = i_maxy;
        if (p_rnd.getInt(10) > 5) i_scry = i_miny;
        i_scry = i_scry * VIRTUALCELL_HEIGHT;

        int i_tpe = p_rnd.getInt(ai_table.length - 1);
        i_tpe = ai_table[i_tpe];

        p_obj.activate(i_tpe, 0, i_scry);
        switch (p_obj.i_type)
        {
            case MoveObject.TYPE_TRAVELLER1:
            case MoveObject.TYPE_TRAVELLER0:
                p_gsb.leftMoveObjectOnRoad(p_obj);
                break;
            default:
                p_gsb.centeredMoveObjectOnRoad(p_obj);
        }
        p_gsb.i_curobjects++;
    }

    public void nextGameStep()
    {
        if (p_gsb.i_bombdelay > 0) p_gsb.i_bombdelay--;
        DriveOff_PMR p_pmr = (DriveOff_PMR) p_gamePlayerBlock.getPlayerMoveRecord(p_gameStateBlock);
        MoveObject p_player = p_gsb.p_player;

        processMoveObjects();

        if (p_player.i_state == MoveObject.STATE_KILLED)
        {
            p_gsb.i_attemption--;
            p_gsb.i_playerState = GameStateBlock.PLAYERSTATE_LOST;
            if (p_gsb.i_attemption == 0)
            {
                p_gsb.i_gameState = GameStateBlock.GAMESTATE_OVER;
            }
            if (p_gameActionListener != null) p_gameActionListener.gameAction(GAMEACTION_PLAYERKILLED);
            return;
        }
        else
        {
            if (p_gsb.i_copters <= 0 && p_gsb.i_copcars <= 0)
            {
                p_gsb.i_playerState = GameStateBlock.PLAYERSTATE_WON;
                return;
            }
        }

        if (p_player.i_state == MoveObject.STATE_ALIVE)
        {
            int i_mask = p_player.i_curmask;
            switch (p_pmr.i_value)
            {
                case DriveOff_PMR.BUTTON_FIREDOWN:
                    {
                        if (p_gsb.i_bombdelay == 0)
                        {
                            MoveObject p_obj = p_gsb.getInactiveMoveObject();

                            p_gsb.i_bombdelay = FIRE_TIMEDELAY_TICKS;

                            p_obj.activate(MoveObject.TYPE_GRENADE, 0, 0);
                            int i_wdth = p_obj.i_width;

                            p_obj.setX(p_player.getX() + ((p_player.i_width - i_wdth) >> 1));
                            p_obj.setY(p_player.getY() - 1);

                            p_obj.i_vertspeed = p_player.i_vertspeed - p_obj.i_vertspeed;
                        }
                    }
                    ;
                    break;
                case DriveOff_PMR.BUTTON_FIREUP:
                    {
                        if (p_gsb.i_bombdelay == 0)
                        {
                            MoveObject p_obj = p_gsb.getInactiveMoveObject();

                            p_gsb.i_bombdelay = FIRE_TIMEDELAY_TICKS;

                            p_obj.activate(MoveObject.TYPE_GRENADE, 0, 0);
                            int i_wdth = p_obj.i_width;

                            p_obj.setX(p_player.getX() + ((p_player.i_width - i_wdth) >> 1));
                            p_obj.setY(p_player.getY() + p_player.i_height);

                            p_obj.i_vertspeed = p_player.i_vertspeed + p_obj.i_vertspeed;
                        }
                    }
                    ;
                    break;
                case DriveOff_PMR.BUTTON_LEFT:
                    {
//                    System.out.println(Integer.toBinaryString(i_mask));
                        if ((i_mask & 0x1) == 0) p_player.setX(p_player.i_scrx - p_player.i_horzspeed + p_player.i_hitoffx);
                    }
                    ;
                    break;
                case DriveOff_PMR.BUTTON_RIGHT:
                    {
                        if ((i_mask & 0x80000000) == 0) p_player.setX(p_player.i_scrx + p_player.i_horzspeed + p_player.i_hitoffx);
                    }
                    ;
                    break;
            }
        }

        p_gsb.i_curpolicedelay++;
        p_gsb.i_curcopterdelay++;

        if (p_gsb.i_tracknumber == 0)
        {
            p_gsb.i_curpolicedelay = p_gsb.i_policetimedelay;
            p_gsb.i_curcopterdelay = p_gsb.i_helicoptertimedelay;
        }

        if (p_player.i_curline >= DriveOff_SB.BOARD_INDEX && p_player.i_curline < (p_gsb.i_waylength - DriveOff_SB.BOARD_INDEX))
        {
            if (p_gsb.i_curobjects < p_gsb.i_maxobjects && p_gsb.i_tracknumber != 0) generateNewCar();

            if (p_gsb.i_curcopterdelay >= p_gsb.i_helicoptertimedelay)
            {
                p_gsb.i_curcopterdelay = 0;
                if (p_gsb.i_copters > 0 ) generateCopter();
            }

            if (p_gsb.i_curpolicedelay >= p_gsb.i_policetimedelay)
            {
                p_gsb.i_curpolicedelay = 0;
                if (p_gsb.i_copcars >0 ) generatePolicecar();
            }
        }
    }

	public void resumeGameAfterPlayerLost()
	{
	    p_gsb.i_curpolicedelay = 0;
        p_gsb.i_curcopterdelay = 0;
        p_gsb.deactivateAllMoveObjectsExcPlayerAndPolice();
        int i_indx = p_gsb.p_player.i_scry;
        p_gsb.p_player.activate(MoveObject.TYPE_PLAYERCAR, 0, i_indx);
        p_gsb.centeredMoveObjectOnRoad(p_gsb.p_player);
        p_gsb.i_playerState = GameStateBlock.PLAYERSTATE_NORMAL;
        p_gsb.i_bombdelay = 0;
	}

    public boolean isGrenadeReady()
    {
        return ((DriveOff_GSB) p_gameStateBlock).i_bombdelay == 0;
    }

    private void processMoveObjects()
    {
        MoveObject[] ap_mo = p_gsb.ap_moveobjects;
        MoveObject p_player = p_gsb.p_player;
        boolean lg_explode = false;
        int i_waylength = p_gsb.i_waylength;

        for (int li = 0; li < ap_mo.length; li++)
        {
            MoveObject p_obj = ap_mo[li];
            int i_objtype = p_obj.i_type;
            int i_curline = p_obj.i_curline;
            int i_scrx = p_obj.i_scrx;
            int i_scry = p_obj.i_scry;
            int i_cellheight = p_obj.i_cellheight;

            if (p_obj.lg_active)
            {
                if (p_obj.processObject())
                {
                    if (i_objtype<=MoveObject.TYPE_TRAVELLER1)
                    {
                        lg_explode = true;
                        switch (i_objtype)
                        {
                            case MoveObject.TYPE_HELYCOPTER:
                                p_gsb.i_copters--;
                                break;
                            case MoveObject.TYPE_POLICECAR:
                                p_gsb.i_copcars--;
                                break;
                            case MoveObject.TYPE_TRAVELLER0:
                            case MoveObject.TYPE_TRAVELLER1:
                                {
                                    p_gsb.i_curobjects--;
                                    lg_explode = false;
                                };break;
                            default :
                                {
                                    p_gsb.i_curobjects--;
                                }
                        }
                        if (lg_explode) convertObjectToExplodeing(p_obj);
                    }
                    else
                    if (i_objtype<=MoveObject.TYPE_ROCKET)
                    {
                        convertObjectToExplodeing(p_obj);
                        lg_explode = true;
                    }
                    continue;
                }

                if (p_obj.i_state == MoveObject.STATE_ALIVE)
                {
                    p_obj.setX(i_scrx + p_obj.i_hitoffx);
                    p_obj.setY(i_scry + p_obj.i_vertspeed + p_obj.i_hitoffy);

                    i_scrx = p_obj.i_scrx;
                    i_scry = p_obj.i_scry;
                    i_curline = p_obj.i_curline;

                    if (i_objtype == MoveObject.TYPE_ROCKET)
                    {
                        if (p_obj.i_vertspeed > p_player.i_vertspeed)
                        {
                            if (i_curline > (i_curline + 3)) p_obj.setState(MoveObject.STATE_KILLED);
                        }
                        else
                        {
                            if (i_curline < (i_curline - p_player.i_cellheight - 3)) p_obj.setState(MoveObject.STATE_KILLED);
                        }
                    }

                }

                if ((i_curline - i_cellheight) >= i_waylength)
                {
                    int liy = i_scry - i_waylength * VIRTUALCELL_HEIGHT;
                    p_obj.setY(liy);
                    i_curline = p_obj.i_curline;
                    i_scry = liy;
                }

                // Processing of the move object
                if (i_objtype <= MoveObject.TYPE_MOTO)
                {
                    if (checkDriveObjectBorder(p_obj) && p_obj.i_type == MoveObject.TYPE_PLAYERCAR)
                    {
                        if (p_gameActionListener != null) p_gameActionListener.gameAction(GAMEACTION_PLAYEROFFROAD);
                    }
                }
                else if (i_objtype == MoveObject.TYPE_TRAVELLER0)
                {
                    if (p_obj.i_state == MoveObject.STATE_ALIVE)
                    {
                        p_obj.setX(i_scrx + p_obj.i_horzspeed);
                        i_scrx = p_obj.i_scrx;
                    }
                }
                else if (i_objtype == MoveObject.TYPE_POLICECAR)
                {
                    p_obj.i_dirx = 0;
                    p_obj.i_diry = 1;

                    if (p_obj.i_firedelay>0) p_obj.i_firedelay--;

                    if (!checkDriveObjectBorder(p_obj))
                    {
                        int i_playerline = p_player.i_curline - HIGH_BORDER + 1;
                        int i_cur = p_obj.i_curline;
                        int i_diff = i_playerline - i_cur;
                        if (i_diff != 0)
                        {
                            if (i_playerline > i_cur)
                            {
                                p_obj.i_dirx = 0;
                                p_obj.i_diry = 1;
                            }
                            else if (i_playerline < i_cur)
                            {
                                p_obj.i_dirx = 0;
                                p_obj.i_diry = 0;
                            }
                            else
                            {
                                p_obj.i_dirx = 0;
                                p_obj.i_diry = 0;
                            }

                            if (i_diff == 1)
                                p_obj.i_vertspeed = p_player.i_vertspeed + (1 * p_obj.i_diry);
                            else
                            {
                                if (i_diff < 0 && (0 - i_diff) > (i_waylength >> 1))
                                {
                                    p_obj.i_vertspeed = p_player.i_vertspeed;
                                    p_obj.i_diry = 1;
                                }
                                else
                                    p_obj.i_vertspeed = p_player.i_vertspeed + 1;
                            }

                            if (p_player.i_hitoffx != 0) p_obj.i_diry = 1;
                        }
                        else
                        {
                            p_obj.i_vertspeed = p_player.i_vertspeed;
                            if ((p_player.i_curmask & p_obj.i_curmask) != 0)
                            {
                                if (p_obj.i_firedelay==0)
                                if (p_rnd.getInt(30) == 15)
                                {
                                    MoveObject p_rocket = p_gsb.getInactiveMoveObject();
                                    if (p_rocket != null)
                                    {
                                        p_obj.i_firedelay = POLICE_FIRE_TIMEDELAY;
                                        p_rocket.activate(MoveObject.TYPE_ROCKET, 0, 0);
                                        p_rocket.setX(p_obj.i_scrx + ((p_obj.i_width - p_rocket.i_width) >> 1));
                                        p_rocket.setY(p_obj.i_scry);
                                        p_rocket.i_vertspeed = p_player.i_vertspeed + p_rocket.i_vertspeed;
                                    }
                                }
                            }
                            else
                            {
                                int i_elem = p_gsb.getLineElement(p_obj.i_curline + p_obj.i_cellheight + SENSE_LENGTH);
                                if (p_player.i_scrx < p_obj.i_scrx)
                                {
                                    if ((i_elem & (p_obj.i_curmask >>> 1)) == (p_obj.i_curmask >>> 1))
                                    {
                                        p_obj.i_dirx = -1;
                                    }
                                }
                                else if (p_player.i_scrx > p_obj.i_scrx)
                                {
                                    if ((i_elem & (p_obj.i_curmask << 1)) == (p_obj.i_curmask << 1))
                                    {
                                        p_obj.i_dirx = 1;
                                    }
                                }
                            }
                        }
                        p_obj.setX(p_obj.i_scrx + (p_obj.i_dirx * p_obj.i_horzspeed));
                        p_obj.i_vertspeed = p_obj.i_diry * p_obj.i_vertspeed;
                    }
                }
                else if (i_objtype == MoveObject.TYPE_HELYCOPTER)
                {
                    if (p_obj.i_firedelay>0) p_obj.i_firedelay--;

                    p_obj.i_diry = 1;
                    int i_playerline = p_player.i_curline + HIGH_BORDER;
                    int i_diff = i_playerline - i_curline;
                    if (i_diff != 0)
                    {
                        if (i_playerline > i_curline)
                        {
                            p_obj.i_dirx = 0;
                            p_obj.i_diry = 1;
                        }
                        else if (i_playerline < i_curline)
                        {
                            p_obj.i_dirx = 0;
                            p_obj.i_diry = -1;
                        }
                        else
                        {
                            p_obj.i_dirx = 0;
                            p_obj.i_diry = 0;
                        }
                        if (i_diff == 1)
                            p_obj.i_vertspeed = p_player.i_vertspeed + (1 * p_obj.i_diry);
                        else
                            p_obj.i_vertspeed = p_player.i_vertspeed << 1;

                        //if (p_player.i_hitoffx != 0) p_obj.i_diry = 1;

                    }
                    else
                    {
                        p_obj.i_diry = 1;

                        if ((p_obj.i_curmask & 0x80000000) == 0x80000000)
                        {
                            p_obj.i_dirx = -1;
                        }
                        else if ((p_obj.i_curmask & 0x1) == 0x1)
                        {
                            p_obj.i_dirx = 1;
                        }
                        else
                        {
                            if (p_obj.i_dirx == 0) p_obj.i_dirx = 1;
                        }
                        p_obj.i_vertspeed = p_player.i_vertspeed;


                        if (p_obj.i_firedelay==0)
                        if ((p_player.i_curmask & p_obj.i_curmask) != 0 && p_rnd.getInt(10) == 5)
                        {
                            MoveObject p_rocket = p_gsb.getInactiveMoveObject();
                            if (p_rocket != null)
                            {
                                p_rocket.activate(MoveObject.TYPE_ROCKET, 0, 0);
                                p_rocket.setX(i_scrx + ((p_obj.i_width - p_rocket.i_width) >> 1));
                                p_rocket.setY(i_scry);
                                p_rocket.i_vertspeed = 0 - p_rocket.i_vertspeed;
                            }
                        }
                    }

                    p_obj.setX(i_scrx + (p_obj.i_dirx * p_obj.i_horzspeed));
                    p_obj.i_vertspeed = p_obj.i_diry * p_obj.i_vertspeed;
                }

                int i_diffy = Math.abs(p_player.i_curline - i_curline);
                if (i_diffy >= (BOARD_INDEX << 1))
                {
                    i_diffy = (i_waylength - Math.max(p_player.i_curline, i_curline)) + Math.min(p_player.i_curline, i_curline);
                }
                else
                {
                    i_diffy = Math.abs(p_player.i_curline - i_curline);
                }

                if (i_diffy > BOARD_INDEX)
                {
                        if (i_objtype <= MoveObject.TYPE_MOTO)
                        {
                            p_obj.lg_active = false;
                            p_gsb.i_curobjects--;
                        }
                        else
                        if (i_objtype<=MoveObject.TYPE_POLICECAR)
                        {
                        }
                        else
                        if (i_objtype<=MoveObject.TYPE_TRAVELLER1)
                        {
                            p_obj.lg_active = false;
                            p_gsb.i_curobjects--;
                        }
                        else
                        if (i_objtype<=MoveObject.TYPE_EXPLODING)
                        {
                            p_obj.lg_active = false;
                        }
                }
                else
                {
                    if (p_obj.i_state == MoveObject.STATE_ALIVE)
                        switch (i_objtype)
                        {
                            case MoveObject.TYPE_ROCKET:
                            case MoveObject.TYPE_GRENADE:
                                break;
                            default :
                                if (checkObjectCollision(li) && i_objtype == MoveObject.TYPE_PLAYERCAR)
                                    if (p_gameActionListener != null) p_gameActionListener.gameAction(GAMEACTION_PLAYERCOLLIDED);
                        }

                }
            }
        }

        if (lg_explode) if (p_gameActionListener != null) p_gameActionListener.gameAction(GAMEACTION_EXPLODING);
    }

    protected boolean checkDriveObjectBorder(MoveObject _obj)
    {
        int i_mask = _obj.i_curmask;
        boolean lg_driven = false;
        boolean lg_offstenable = true;
        if (_obj.i_type == MoveObject.TYPE_PLAYERCAR)
        {
            if ((i_mask & 0x1) == 1 || (i_mask & 0x80000000) == 0x80000000)
            {
                _obj.i_hitoffx = 0;
                lg_offstenable = false;
            }
        }
        else
        {
            int i_elem = p_gsb.getLineElement(_obj.i_curline + _obj.i_cellheight + SENSE_LENGTH);
            int i_objhorzspeed = _obj.i_horzspeed;
            int i_diffsub = _obj.i_scrx - i_objhorzspeed;
            int i_diffadd = _obj.i_scrx + i_objhorzspeed;

            int i_imask = i_mask << 1;
            int i_dmask = i_mask >>> 1;

            if ((i_elem & i_mask) == i_mask)
            {
                if ((i_elem & i_imask) == i_imask)
                {
                    if ((i_elem & i_dmask) != i_dmask)
                    {
                        _obj.setX(i_diffadd);
                        lg_driven = true;
                    }
                }
                else
                {
                    _obj.setX(i_diffsub);
                    lg_driven = true;
                }
            }
            else
            {
                if ((i_elem & i_imask) == i_imask)
                {
                    _obj.setX(i_diffadd);
                    lg_driven = true;
                }
                else
                {
                    if ((i_elem & i_dmask) == i_dmask)
                    {
                        _obj.setX(i_diffsub);
                        lg_driven = true;
                    }
                    else
                    {
                        i_elem &= 0x7FFFFFFF;
                        i_mask &= 0x7FFFFFFF;

                        if (i_elem < i_mask)
                        {
                            _obj.setX(i_diffsub);
                        }
                        else
                        {
                            _obj.setX(i_diffadd);
                        }
                        lg_driven = true;
                    }
                }
            }
        }

        int i_elem = p_gsb.getLineElement(_obj.i_curline);
        int i_belem = p_gsb.getLineElement(_obj.i_curline + _obj.i_cellheight - 1);
        if ((i_elem & i_mask) != i_mask || (i_belem & i_mask) != i_mask)
        {
            if (_obj.i_type != MoveObject.TYPE_POLICECAR && _obj.i_type != MoveObject.TYPE_PLAYERCAR) _obj.processDamage(1);
            _obj.i_hitoffy = 0 - OFFROAD_OFFSET;

            if (lg_offstenable)
            {
                if (p_rnd.getInt(10) > 5)
                    _obj.i_hitoffx = 0 - DriveOff_SB.OFFROAD_OFFSET;
                else
                    _obj.i_hitoffx = DriveOff_SB.OFFROAD_OFFSET;
            }
            else
                _obj.i_hitoffx = 0;
            return true;
        }

        if (_obj.i_type == MoveObject.TYPE_POLICECAR)
        {
            return lg_driven;
        }
        return false;
    }

    protected boolean checkObjectCollision(int _index)
    {
        MoveObject[] ap_mo = p_gsb.ap_moveobjects;
        MoveObject p_curobj = ap_mo[_index];

        int i_curobjtype = p_curobj.i_type;

        boolean lg_collided = false;
        int i_x = p_curobj.i_scrx;
        int i_xw = i_x + p_curobj.i_width;

        int i_y = p_curobj.i_scry;
        int i_yh = i_y + p_curobj.i_height;

        int i_curobjmask = p_curobj.i_curmask;

        for (int li = 0; li < ap_mo.length; li++)
        {
            if (_index == li) continue;
            MoveObject p_obj = ap_mo[li];
            int i_otype = p_obj.i_type;

            if (i_curobjtype == MoveObject.TYPE_HELYCOPTER && !(i_otype == MoveObject.TYPE_GRENADE || i_otype == MoveObject.TYPE_ROCKET))
                continue;
            else if (i_otype == MoveObject.TYPE_HELYCOPTER) continue;

            if (p_obj.lg_active)
            {
                if ((i_curobjmask & p_obj.i_curmask) == 0) continue;

                int i_oscrx = p_obj.i_scrx;
                int i_oscry = p_obj.i_scry;

                if (!((i_oscrx + p_obj.i_width <= i_x) || (i_oscry + p_obj.i_height <= i_y) || (i_oscrx >= i_xw) || (i_oscry >= i_yh)))
                {
                    lg_collided = true;
                    switch (i_otype)
                    {
                        case MoveObject.TYPE_ROCKET:
                            {
                                if (i_curobjtype == MoveObject.TYPE_POLICECAR || i_curobjtype == MoveObject.TYPE_HELYCOPTER) continue;
                                if (p_obj.i_state == MoveObject.STATE_KILLED) continue;
                                p_obj.setState(MoveObject.STATE_KILLED);
                                if (p_curobj.processDamage(p_obj.i_maxdamage))
                                {
                                    p_curobj.setState(MoveObject.STATE_KILLED);
                                    return lg_collided;
                                }
                            }
                            ;
                            break;
                        case MoveObject.TYPE_GRENADE:
                            {
                                if (i_curobjtype == MoveObject.TYPE_PLAYERCAR)
                                {
                                    lg_collided = false;
                                    continue;
                                }
                                if (p_obj.i_state == MoveObject.STATE_KILLED) continue;
                                p_obj.setState(MoveObject.STATE_KILLED);
                                if (p_curobj.processDamage(p_obj.i_maxdamage))
                                {
                                    switch (i_otype)
                                    {
                                        case MoveObject.TYPE_HELYCOPTER:
                                            p_gsb.i_playerScore += 10;
                                            break;
                                        case MoveObject.TYPE_POLICECAR:
                                            p_gsb.i_playerScore += 20;
                                            break;
                                        default :
                                            p_gsb.i_playerScore++;
                                    }
                                    p_curobj.setState(MoveObject.STATE_KILLED);
                                    return lg_collided;
                                }
                            }
                            ;
                            break;
                        default :
                            {
                                if (i_curobjtype == MoveObject.TYPE_HELYCOPTER) continue;
                                boolean lg_damage = false;
                                if (i_curobjtype != MoveObject.TYPE_POLICECAR)
                                    lg_damage = p_curobj.processDamage(1);

                                if (lg_damage)
                                {
                                    p_curobj.setState(MoveObject.STATE_KILLED);
                                    return lg_collided;
                                }
                                else
                                {
                                    if (p_obj.i_state == MoveObject.STATE_ALIVE)
                                    {
                                        int i_sgnx = 1;
                                        int i_sgny = 1;
                                        if (i_oscrx > i_x) i_sgnx = -1;
                                        if (i_oscry > i_y) i_sgny = -1;

                                        int i_hfx = HIT_OFFSET * i_sgnx;
                                        int i_hfy = HIT_OFFSET * i_sgny;

                                        p_curobj.i_hitoffx = i_hfx;
                                        p_curobj.i_hitoffy = i_hfy;
                                        p_obj.i_hitoffx = i_hfx * (-1);
                                        p_obj.i_hitoffy = i_hfy * (-1);
                                    }
                                }
                            }
                    }
                }
            }
        }
        return lg_collided;
    }

    public void initStage(int _stage)
    {
        p_gsb.initStage(_stage);
    }

    public MoveObject getPlayer()
    {
        return ((DriveOff_GSB) p_gameStateBlock).p_player;
    }

    public DriveOff_SB(int screenWidth, int screenHeight, GamePlayerBlock gamePlayerBlock, GamePlayerBlock aiPlayerBlock, GameActionListener gameActionListener)
    {
        super(screenWidth, screenHeight, gamePlayerBlock, aiPlayerBlock, gameActionListener);

        i_gameFlags = FLAG_LEVELS_SUPPORT | FLAG_SAVELOAD_SUPPORT | FLAG_SCORES_SUPPORT | FLAG_STAGES_SUPPORT;

        SCREEN_LINES = screenHeight / VIRTUALCELL_HEIGHT;
        if ((screenHeight % VIRTUALCELL_HEIGHT) != 0) SCREEN_LINES++;
        HIGH_BORDER = ((SCREEN_LINES - PLAYER_CELLLINES) >> 1) + 1;
    }

    public String getGameID()
    {
        return "DRIVEOFF";
    }

    public int getMaxSizeOfSavedGameBlock()
    {
        return 10 + ANIMATION_OBJECTS * 10;
    }
}
