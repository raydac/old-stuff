package com.igormaznitsa.game_kit_E5D3F2.RiverRescue;

import com.igormaznitsa.gameapi.StrategicBlock;
import com.igormaznitsa.gameapi.GameActionListener;

public class RiverRescue_SB extends StrategicBlock
{
    public static final int LEVEL0 = 0;
    public static final int LEVEL0_TIMEDELAY = 120;
    public static final int LEVEL0_FUELDELAY = 60;
    public static final int LEVEL0_MINEMINDELAY = 40;

    public static final int LEVEL1 = 1;
    public static final int LEVEL1_TIMEDELAY = 100;
    public static final int LEVEL1_FUELDELAY = 80;
    public static final int LEVEL1_MINEMINDELAY = 30;

    public static final int LEVEL2 = 2;
    public static final int LEVEL2_TIMEDELAY = 80;
    public static final int LEVEL2_FUELDELAY = 100;
    public static final int LEVEL2_MINEMINDELAY = 20;

    public static final int MAXOBJECTS = 20;

    public static final int VIRTUALCELL_HEIGHT = 8;
    public static final int VIRTUALCELL_WIDTH = 3;

    protected static final int PLAYERSPEED = 2;
    protected static int i_VirtualScreenWidth;

    public static final int I8_MAX_FUEL_VALUE = 0x5000;
    private static final int I8_DECR_FUEL_SPEED = 0x20;

    private static final int PLAYER_HORZSPEED = 4;
    private static final int I8_SPEEDCOEFF = (PLAYER_HORZSPEED << 8) / ((Player.MAXFRAMES + 1) >> 1);

    public int I_GENERATEBORDEROFFSET;

    public RiverRescue_GSB p_Gsb;

    private long[] al_bitencodetable;

    private void fillBitEncodeTable()
    {
        al_bitencodetable = new long[15];

        long l_mask = 0x1l;

        for (int li = 0; li < al_bitencodetable.length; li++)
        {
            al_bitencodetable[li] = l_mask;
            l_mask <<= 1;
            l_mask |= 0x1l;
        }
    }

    public RiverRescue_SB(int _screenWidth, int _screenHeight, GameActionListener _gameActionListener)
    {
        super(_screenWidth, _screenHeight, _gameActionListener);
        i_VirtualScreenWidth = 64 * VIRTUALCELL_WIDTH;
        I_GENERATEBORDEROFFSET = _screenHeight >> 1;
        fillBitEncodeTable();
    }

    public void newGameSession(int _level)
    {
        p_Gsb = new RiverRescue_GSB();
        p_gameStateBlock = p_Gsb;

        p_Gsb.initLevel(_level);
    }

    public void nextGameStep(Object _playermoveobject)
    {
        RiverRescue_PMR p_pmr = (RiverRescue_PMR) _playermoveobject;

        if (processPlayer(p_pmr.i_Value))
        {
            p_Gsb.p_Player.destroyBoat();
        }

        p_Gsb.i8_fuelvalue -= I8_DECR_FUEL_SPEED;
        if (p_Gsb.i8_fuelvalue <= 0)
        {
            p_Gsb.i8_fuelvalue = 0;

            p_Gsb.i_playerState = RiverRescue_GSB.PLAYERSTATE_FUELISENDED;
            p_Gsb.i_gameState = RiverRescue_GSB.GAMESTATE_OVER;
            return;
        }

        processObject();
        generateObjects();
    }

    private int generateRandomPositionOfObject(int _line, int _width)
    {
        int i_indx = _line;
        long l_elem = p_Gsb.getLineElement(i_indx);

        int i_wdth = 0;

        int i_cw = (_width + VIRTUALCELL_WIDTH - 1) / VIRTUALCELL_WIDTH;
        long l_lmask = al_bitencodetable[i_cw];

        int i_attempt = 0;
        while (true)
        {
            if (i_attempt == 10)
            {
                i_wdth = p_Gsb.centeredMoveObjectOnRoad(_line, _width);
            }
            else
            {
                i_wdth = getRandomInt(63 - i_cw) + 1;
                long l_nm = l_lmask << i_wdth;
                if ((l_nm & l_elem) == l_nm)
                {
                    i_wdth = i_wdth * VIRTUALCELL_WIDTH;
                    break;
                }
            }
            i_attempt++;
        }

        return i_wdth;
    }

    private void generateObjects()
    {
        if (p_Gsb.i_minedelay > 0) p_Gsb.i_minedelay--;

        int i_ply = p_Gsb.p_Player.getScrY() - I_GENERATEBORDEROFFSET-5;
        int i_ply2 = i_ply / VIRTUALCELL_HEIGHT;

        // Generate of fuel
        if (p_Gsb.i_fueldelay == 0)
        {
            MovingObject p_obj = p_Gsb.getFirstInactiveObject();

            if (p_obj != null)
            {
                p_obj.activate(MovingObject.OBJECT_FUEL, 0, i_ply);
                int i_w = p_obj.i_width;

                int i_x = 0;

                switch (getRandomInt(29) / 10)
                {
                    case 0:
                        {
                            i_x = p_Gsb.centeredMoveObjectOnRoad(i_ply2, i_w);
                        }
                        ;
                        break;
                    case 1:
                        {
                            i_x = p_Gsb.leftMoveObjectOnRoad(i_ply2, i_w);
                        }
                        ;
                        break;
                    case 2:
                        {
                            i_x = p_Gsb.rightMoveObjectOnRoad(i_ply2, i_w);
                        }
                        ;
                        break;
                }

                p_obj.i_x = i_x;
                p_Gsb.i_fueldelay = p_Gsb.i_maxfueldelay;
            }
        }
        else
        {
            p_Gsb.i_fueldelay--;

            switch (getRandomInt(30))
            {
                case 15:
                    {
                        if (p_Gsb.i_minedelay == 0)
                        {
                            MovingObject p_obj = p_Gsb.getFirstInactiveObject();
                            if (p_obj != null)
                            {
                                p_obj.activate(MovingObject.OBJECT_MINE, 0, i_ply);
                                p_obj.i_x = generateRandomPositionOfObject(i_ply2, p_obj.i_width);
                                p_Gsb.i_minedelay = p_Gsb.i_minminedelay;
                            }
                        }
                    }
                    ;
                    break;
                case 29:
                    {
                        // Air generation
                        int i_rnd = getRandomInt(100);
                        if (i_rnd > 80)
                        {
                            MovingObject p_mobj = p_Gsb.getFirstInactiveObject();
                            if (p_mobj != null)
                            {
                                if (i_rnd < 95)
                                {
                                    // Generation of an airplane
                                    i_ply = p_Gsb.p_Player.getScrY() + I_GENERATEBORDEROFFSET + 5 - (PLAYERSPEED << 1);

                                    p_mobj.activate(MovingObject.OBJECT_AIRPLANEVERT, 0, i_ply);

                                    int i_cw = (p_mobj.i_width + VIRTUALCELL_WIDTH - 1) / VIRTUALCELL_WIDTH;

                                    int i_w = getRandomInt(63 - i_cw);
                                    p_mobj.i_x = i_w * VIRTUALCELL_WIDTH;
                                }
                                else
                                {
                                    // Generation of a helicopter
                                    i_ply = p_Gsb.p_Player.getScrY() + I_GENERATEBORDEROFFSET - (PLAYERSPEED << 1);

                                    p_mobj.activate(MovingObject.OBJECT_HELICOPTERHORZ, 0, i_ply);
                                    p_mobj.i_x = 0 - p_mobj.i_width;

                                    p_mobj.i_y = p_Gsb.p_Player.getScrY() - I_GENERATEBORDEROFFSET;
                                }
                            }
                        }
                    }
                    ;
                    break;
                case 10:
                    {
                        MovingObject p_obj = p_Gsb.getFirstInactiveObject();
                        if (p_obj != null)
                        {
                            p_obj.activate(MovingObject.OBJECT_DROWNINGMAN, 0, i_ply);
                            p_obj.i_x = generateRandomPositionOfObject(i_ply2, p_obj.i_width);
                        }
                    }
                    ;
                    break;
            }
        }
    }

    private boolean processPlayer(int _key)
    {
        Player p_player = p_Gsb.p_Player;

        boolean lg_peak = p_player.process(_key);

        boolean lg_result = false;

        int i_different = 0;

        if (!p_player.lg_Destroyed)
        {
            i_different = ((p_player.i_Frame - 3) * I8_SPEEDCOEFF) >> 8;
            p_player.setCY(p_player.i_cy - PLAYERSPEED);
        }
        else
        {
            if (lg_peak)
            {
                p_Gsb.i_playerState = RiverRescue_GSB.PLAYERSTATE_LOST;
                p_Gsb.i_gameState = RiverRescue_GSB.GAMESTATE_OVER;
            }
            return false;
        }

        p_player.setCX(p_player.i_cx + i_different);

        // Check player and beach intersection
        long l_elem = p_Gsb.getLineElement(p_player.getScrY() / VIRTUALCELL_HEIGHT);
        long l_mask = al_bitencodetable[p_player.i_width / VIRTUALCELL_WIDTH];
        int i_offst = p_player.getScrX() / VIRTUALCELL_WIDTH;
        l_mask <<= i_offst;

        if ((l_elem & l_mask) != l_mask) lg_result = true;
        return lg_result;
    }

    private boolean processObject()
    {
        MovingObject[] ap_objects = p_Gsb.ap_MovingObjects;

        int i_y = p_Gsb.p_Player.getScrY() + I_GENERATEBORDEROFFSET + p_Gsb.p_Player.i_height;
        int i_hy = p_Gsb.p_Player.getScrY() - I_GENERATEBORDEROFFSET;

        int i_px = p_Gsb.p_Player.getScrX();
        int i_py = p_Gsb.p_Player.getScrY();
        int i_pxw = i_px + p_Gsb.p_Player.i_width;
        int i_pxh = i_py + p_Gsb.p_Player.i_height;

        boolean lg_destroyed = p_Gsb.p_Player.lg_Destroyed;

        for (int li = 0; li < ap_objects.length; li++)
        {
            MovingObject p_obj = ap_objects[li];

            if (!p_obj.lg_Active) continue;

            boolean lg_peak = p_obj.process();

            int i_ox = p_obj.i_x;
            int i_oy = p_obj.i_y;
            int i_wx = i_ox + p_obj.i_width;
            int i_wy = i_oy + p_obj.i_height;

            boolean lg_intersect = false;
            if (!((i_wx <= i_px) || (i_wy <= i_py) || (i_ox >= i_pxw) || (i_oy >= i_pxh))) lg_intersect = true;

            switch (p_obj.i_Type)
            {
                case MovingObject.OBJECT_DROWNINGMAN:
                    {
                        if (lg_intersect)
                        {
                            p_obj.lg_Active = false;
                            p_Gsb.i_playerScore += 5;
                        }
                    }
                    ;
                    break;
                case MovingObject.OBJECT_EXPLOSION:
                    {
                        if (lg_peak) p_obj.lg_Active = false;
                    }
                    ;
                    break;
                case MovingObject.OBJECT_FUEL:
                    {
                        if (lg_intersect)
                        {
                            p_obj.lg_Active = false;
                            p_Gsb.i8_fuelvalue = I8_MAX_FUEL_VALUE;
                            p_Gsb.i_playerScore ++;
                        }
                    }
                    ;
                    break;
                case MovingObject.OBJECT_MINE:
                    {
                        if (lg_intersect && !lg_destroyed)
                        {
                            convertObjectToExplosive(p_obj);
                            p_Gsb.p_Player.destroyBoat();
                        }
                    }
                    ;
                    break;
                case MovingObject.OBJECT_HELICOPTERHORZ:
                    {
                        p_obj.i_y -= PLAYERSPEED;

                        p_obj.i_x++;
                        if (p_obj.i_x >= i_VirtualScreenWidth)
                        {
                            p_obj.lg_Active = false;
                        }
                        else
                        if (Math.abs(p_obj.i_x - i_px) <= (VIRTUALCELL_WIDTH<<1))
                        if (!p_obj.lg_bombdrop)
                        {
                            int i_cell = p_obj.i_x / VIRTUALCELL_WIDTH;
                            long l_mask = RiverRescue_GSB.l_airminemask << i_cell;
                            int i_celly = (p_obj.i_y + (p_obj.i_height >> 1)) / VIRTUALCELL_HEIGHT;
                            if (i_celly >= 0 && i_cell >= 3)
                            {
                                long i_el = p_Gsb.getLineElement(i_celly);
                                if ((l_mask & i_el) == l_mask)
                                {
                                    MovingObject p_newmine = p_Gsb.getFirstInactiveObject();
                                    if (p_newmine != null)
                                    {
                                        int i_ax = p_obj.i_x + (p_obj.i_width >> 1);
                                        int i_ay = p_obj.i_y + (p_obj.i_height >> 1);

                                        p_newmine.activate(MovingObject.OBJECT_FUEL, 0, 0);

                                        p_newmine.i_x = i_ax - (p_newmine.i_width >> 1);
                                        p_newmine.i_y = i_ay - (p_newmine.i_height >> 1);

                                        p_obj.lg_bombdrop = true;
                                    }
                                }
                            }
                        }
                    }
                    ;
                    break;
                case MovingObject.OBJECT_AIRPLANEVERT:
                    {
                        p_obj.i_y -= (PLAYERSPEED << 1);

                        if (!p_obj.lg_bombdrop)
                        {
                            if ((p_obj.i_x - i_px) > VIRTUALCELL_WIDTH)
                            {
                                p_obj.i_x--;
                            }
                            else if ((i_px - p_obj.i_x) > VIRTUALCELL_WIDTH)
                            {
                                p_obj.i_x++;
                            }

                            if ((i_py - p_obj.i_y) >= (I_GENERATEBORDEROFFSET >> 1))
                            {
                                int i_cell = p_obj.i_x / VIRTUALCELL_WIDTH;
                                long l_mask = RiverRescue_GSB.l_airminemask << i_cell;
                                int i_celly = (p_obj.i_y + (p_obj.i_height >> 1)) / VIRTUALCELL_HEIGHT;
                                if (i_celly >= 0)
                                {
                                    long i_el = p_Gsb.getLineElement(i_celly);

                                    if ((l_mask & i_el) == l_mask)
                                    {
                                        MovingObject p_newmine = p_Gsb.getFirstInactiveObject();
                                        if (p_newmine != null)
                                        {
                                            int i_ax = p_obj.i_x + (p_obj.i_width >> 1);
                                            int i_ay = p_obj.i_y + (p_obj.i_height >> 1);

                                            p_newmine.activate(MovingObject.OBJECT_MINE, 0, 0);

                                            p_newmine.i_x = i_ax - (p_newmine.i_width >> 1);
                                            p_newmine.i_y = i_ay - (p_newmine.i_height >> 1);

                                            p_obj.lg_bombdrop = true;
                                        }
                                    }
                                }
                            }
                        }
                    }
                    ;
                    break;
            }

            if (p_obj.i_y > i_y || (p_obj.i_y + p_obj.i_height) <= i_hy) p_obj.lg_Active = false;
        }
        return false;
    }

    private void convertObjectToExplosive(MovingObject _obj)
    {
        int i_oow = _obj.i_width;
        int i_ooh = _obj.i_height;
        int i_oox = _obj.i_x;
        int i_ooy = _obj.i_y;

        _obj.activate(MovingObject.OBJECT_EXPLOSION, 0, 0);

        i_oox = i_oox - ((_obj.i_width - i_oow) >> 1);
        i_ooy = i_ooy - ((_obj.i_height - i_ooh) >> 1);

        _obj.i_x = i_oox;
        _obj.i_y = i_ooy;
    }

    public String getGameID()
    {
        return "RIVERR";
    }

    public int getMaxSizeOfSavedGameBlock()
    {
        return 1024;
    }
}
