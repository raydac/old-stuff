/*
 * Author : Igor A. Maznitsa (igor.maznitsa@igormaznitsa.com)
 * Date: 12.09.2002
 * Time: 13:45:48
 */
package com.igormaznitsa.game_kit_E5D3F2.Assault;

import com.igormaznitsa.gameapi.GameActionListener;
import com.igormaznitsa.gameapi.GameStateBlock;
import com.igormaznitsa.gameapi.StrategicBlock;

public class Assault_SB extends StrategicBlock
{
    public static final int GAMEACTION_FIELDCHANGED = 0;

    public static final int FIELD_WIDTH = 17;
    public static final int FIELD_HEIGHT = 8;

    public static final int LEVEL0 = 0;
    public static final int LEVEL0_ATTEMPTIONS = 5;
    public static final int LEVEL0_SHOTFREQ = 5;
    public static final int LEVEL0_TIMEDELAY = 120;
    public static final int LEVEL0_PLAYERALT = 0;
    public static final int LEVEL0_ASSAULTERNUMBER = 80;

    public static final int LEVEL1 = 1;
    public static final int LEVEL1_ATTEMPTIONS = 4;
    public static final int LEVEL1_SHOTFREQ = 10;
    public static final int LEVEL1_TIMEDELAY = 120;
    public static final int LEVEL1_PLAYERALT = 1;
    public static final int LEVEL1_ASSAULTERNUMBER = 100;

    public static final int LEVEL2 = 2;
    public static final int LEVEL2_ATTEMPTIONS = 3;
    public static final int LEVEL2_SHOTFREQ = 15;
    public static final int LEVEL2_TIMEDELAY = 120;
    public static final int LEVEL2_PLAYERALT = 2;
    public static final int LEVEL2_ASSAULTERNUMBER = 120;

    public static final int VIRTUALCELL_WIDTH = 6;
    public static final int VIRTUALCELL_HEIGHT = 8;

    protected static final int NUMBER_MOVEOBJECTS = 20;

    private static final int ASSAULTER_HORZSPEED = 1;
    private static final int ASSAULTER_VERTSPEED = 2;
    private static final int PLAYER_HORZSPEED = 2;
    private static final int ARROWSPEED = 2;
    private static final int STONESPEED = 3;

    protected Assault_GSB p_gsb;

    public Assault_SB(int _screenWidth,int _screenHeight,GameActionListener _gameActionListener)
    {
        super(_screenWidth,_screenHeight,_gameActionListener);
    }

    public void newGameSession(int _level)
    {
        p_gsb = new Assault_GSB();
        p_gameStateBlock = p_gsb;
        p_gsb.initLevel(_level);
    }

    public void endGameSession()
    {
        super.endGameSession();
        p_gsb = null;
    }

    public void resumeGameAfterPlayerLost()
    {
        p_gsb.i_playerState = GameStateBlock.PLAYERSTATE_NORMAL;
        p_gsb.deactivateAllMovingObjects();
        p_gsb.initPlayer();
    }

    private void generateNewAssaulter()
    {
        if (p_gsb.i_curassaulternumber < (NUMBER_MOVEOBJECTS >> 1))
        {
            if (getRandomInt(20) == 10)
            {
                MovingObject p_obj = p_gsb.getInactiveMovingObject();
                if (p_obj == null) return;

                int i_cellx = 0;
                int i_state = MovingObject.STATE_RIGHT;

                if (getRandomInt(30) >= 15)
                {
                    i_cellx = 0;
                    i_state = MovingObject.STATE_RIGHT;
                }
                else
                {
                    i_cellx = FIELD_WIDTH - 1;
                    i_state = MovingObject.STATE_LEFT;
                }

                p_obj.activate(MovingObject.TYPE_ASSAULTER,i_state);
                p_gsb.initScrCoordsForMovingObject(p_obj,i_cellx,FIELD_HEIGHT - 1);
                p_gsb.i_assaulternumber--;
                p_gsb.i_curassaulternumber++;
            }
        }
    }

    // return true if an assaulter is on player's level
    private boolean processMovingObject()
    {
        MovingObject[] ap_objects = p_gsb.ap_MovingObjects;
        for (int li = 0;li < ap_objects.length;li++)
        {
            MovingObject p_obj = ap_objects[li];

            if (!p_obj.lg_Active) continue;

            if (p_obj.process())
            {
                if (p_obj.i_State == MovingObject.STATE_EXPLODE)
                {
                    p_obj.lg_Active = false;
                    continue;
                }
            }

            if (p_obj.i_State == MovingObject.STATE_EXPLODE) continue;

            switch (p_obj.i_Type)
            {
                case MovingObject.TYPE_ARROW:
                    {
                        p_obj.i_scry -= ARROWSPEED;
                        if (p_obj.i_scry <= (0 - p_obj.i_height))
                        {
                            p_obj.lg_Active = false;
                            continue;
                        }
                    }
                    ;
                    break;
                case MovingObject.TYPE_ASSAULTER:
                    {
                        if (getRandomInt(1000) <= p_gsb.i_shotfreq)
                        {
                            MovingObject p_arrow = p_gsb.getInactiveMovingObject();
                            if (p_arrow != null)
                            {
                                p_arrow.activate(MovingObject.TYPE_ARROW,MovingObject.STATE_UP);
                                p_arrow.i_scrx = p_obj.i_scrx;
                                p_arrow.i_scry = p_obj.i_scry - p_arrow.i_height;
                            }
                        }

                        if (p_obj.i_State != MovingObject.STATE_DOWN) generateLadderAndDamForAssaulter(p_obj);

                        switch (p_obj.i_State)
                        {
                            case MovingObject.STATE_LEFT:
                                {
                                    if (p_obj.i_scrx - ASSAULTER_HORZSPEED < 0)
                                    {
                                        p_obj.setState(MovingObject.STATE_RIGHT,false);
                                    }
                                    else
                                    {
                                        p_obj.i_scrx -= ASSAULTER_HORZSPEED;
                                    }
                                }
                                ;
                                break;
                            case MovingObject.STATE_RIGHT:
                                {
                                    if (p_obj.i_scrx + p_obj.i_width + ASSAULTER_HORZSPEED >= i_screenWidth)
                                    {
                                        p_obj.setState(MovingObject.STATE_LEFT,false);
                                    }
                                    else
                                    {
                                        p_obj.i_scrx += ASSAULTER_HORZSPEED;
                                    }
                                }
                                ;
                                break;
                            case MovingObject.STATE_UP:
                                {
                                    p_obj.i_scry -= ASSAULTER_VERTSPEED;
                                    if ((p_obj.i_scry + (p_obj.i_height >> 1)) / VIRTUALCELL_HEIGHT == p_gsb.i_playeralt) return true;
                                }
                                ;
                                break;
                            case MovingObject.STATE_DOWN:
                                {
                                    p_obj.i_scry += ASSAULTER_VERTSPEED;
                                    int i_cellx = p_obj.i_scrx / VIRTUALCELL_WIDTH;
                                    int i_celly = (p_obj.i_scry + p_obj.i_height - 1) / VIRTUALCELL_HEIGHT;
                                    boolean lg_stop = false;
                                    if (i_celly < FIELD_HEIGHT)
                                    {
                                        if (Assault_GSB.getElement(i_cellx,i_celly) != Assault_GSB.CELL_NONE) lg_stop = true;
                                    }
                                    else
                                    {
                                        lg_stop = true;
                                        i_celly--;
                                    }

                                    if (lg_stop)
                                    {
                                        p_obj.i_scry = i_celly * VIRTUALCELL_HEIGHT;
                                        if (getRandomInt(40) >= 20)
                                        {
                                            p_obj.setState(MovingObject.STATE_LEFT,false);
                                        }
                                        else
                                        {
                                            p_obj.setState(MovingObject.STATE_RIGHT,false);
                                        }
                                    }
                                }
                                ;
                                break;
                        }
                    }
                    ;
                    break;
                case MovingObject.TYPE_STONE:
                    {
                        if (p_obj.i_State == MovingObject.STATE_DOWN)
                        {
                            p_obj.i_scry += STONESPEED;
                            if ((p_obj.i_scry + p_obj.i_height) >= FIELD_HEIGHT * VIRTUALCELL_HEIGHT)
                            {
                                p_obj.i_scry = (FIELD_HEIGHT * VIRTUALCELL_HEIGHT) - p_obj.i_height - 1;
                                p_obj.setState(MovingObject.STATE_EXPLODE,false);
                            }
                        }
                    }
                    ;
                    break;
            }
        }
        return false;
    }

    private void generateLadderAndDamForAssaulter(MovingObject _assaulter)
    {
        int i_cellx = (_assaulter.i_scrx + (_assaulter.i_width >> 1)) / VIRTUALCELL_WIDTH;
        int i_celly = (_assaulter.i_scry + (_assaulter.i_height >> 1)) / VIRTUALCELL_HEIGHT;

        int i_elem = Assault_GSB.getElement(i_cellx,i_celly);

        if (i_elem == Assault_GSB.CELL_NONE && i_cellx > 0 && i_cellx < (FIELD_WIDTH - 1))
        {
            int i_rnd = getRandomInt(2000);
            if (i_rnd < 50)
            {
                if (i_rnd > 40)
                {
                    int i_ly = i_celly + 1;
                    if (i_ly < FIELD_HEIGHT)
                    {
                        if (Assault_GSB.getElement(i_cellx,i_ly) == Assault_GSB.CELL_DAM)
                        {
                            Assault_GSB.setElement(i_cellx,i_celly,Assault_GSB.CELL_DAM);
                            if (p_gameActionListener != null) p_gameActionListener.gameAction(GAMEACTION_FIELDCHANGED,i_cellx,i_celly);
                        }
                    }
                    else
                    {
                        Assault_GSB.setElement(i_cellx,i_celly,Assault_GSB.CELL_DAM);
                        if (p_gameActionListener != null) p_gameActionListener.gameAction(GAMEACTION_FIELDCHANGED,i_cellx,i_celly);
                    }
                }
                else
                {
                    Assault_GSB.setElement(i_cellx,i_celly,Assault_GSB.CELL_LADDER);
                    if (p_gameActionListener != null) p_gameActionListener.gameAction(GAMEACTION_FIELDCHANGED,i_cellx,i_celly);
                }
            }
        }

        // Changing of the direction of the assaulter
        i_celly = (_assaulter.i_scry + _assaulter.i_height - 1) / VIRTUALCELL_HEIGHT;

        int i_celly2 = i_celly + 1;

        i_elem = Assault_GSB.getElement(i_cellx,i_celly);
        switch (i_elem)
        {
            case Assault_GSB.CELL_LADDER:
                {
                    _assaulter.i_scrx = i_cellx * VIRTUALCELL_WIDTH;
                    if (_assaulter.i_State != MovingObject.STATE_UP)
                        if (getRandomInt(100) >= 50)
                            _assaulter.setState(MovingObject.STATE_UP,false);
                        else if (getRandomInt(100) >= 90)
                            if (getRandomInt(100) >= 50)
                                _assaulter.setState(MovingObject.STATE_LEFT,false);
                            else
                                _assaulter.setState(MovingObject.STATE_RIGHT,false);
                }
                ;
                break;
            case Assault_GSB.CELL_NONE:
                {
                    _assaulter.i_scry = i_celly * VIRTUALCELL_HEIGHT;

                    if (_assaulter.i_State == MovingObject.STATE_UP)
                    {
                        if (getRandomInt(30) >= 15) _assaulter.setState(MovingObject.STATE_LEFT,false);
                    }
                    if (i_celly2 < FIELD_HEIGHT)
                    {
                        if (Assault_GSB.getElement(i_cellx,i_celly2) == Assault_GSB.CELL_NONE)
                        {
                            _assaulter.i_scrx = i_cellx * VIRTUALCELL_WIDTH;
                            _assaulter.setState(MovingObject.STATE_DOWN,false);
                        }
                    }
                }
                ;
                break;
        }
    }

    public void nextGameStep(Object _playermoveobject)
    {
        Assault_PMR p_pmr = (Assault_PMR)_playermoveobject;

        MovingObject p_player = p_gsb.p_Player;

        boolean lg_playerresult = p_player.process();

        if (lg_playerresult)
        {
            switch (p_player.i_State)
            {
                case MovingObject.STATE_DOWN:
                    {
                        if (p_gsb.p_Stone.lg_Active)
                        {
                            p_player.setState(MovingObject.STATE_DOWN,true);
                        }
                        else
                        {
                            p_gsb.p_Stone.activate(MovingObject.TYPE_STONE,MovingObject.STATE_DOWN);
                            p_gsb.p_Stone.i_scrx = p_player.i_scrx;
                            p_gsb.p_Stone.i_scry = p_player.i_scry + 5;
                        }
                    }
                    ;
                    break;
                case MovingObject.STATE_EXPLODE:
                    {
                        p_gsb.i_playerState = GameStateBlock.PLAYERSTATE_LOST;
                        p_gsb.i_Attemptions--;
                        if (p_gsb.i_Attemptions == 0) p_gsb.i_gameState = GameStateBlock.GAMESTATE_OVER;
                        return;
                    }
            }
        }

        if (p_player.i_State != MovingObject.STATE_DOWN && p_player.i_State != MovingObject.STATE_EXPLODE)
        {
            switch (p_pmr.i_Value)
            {
                case Assault_PMR.BUTTON_FIRE:
                    {
                        p_player.setState(MovingObject.STATE_DOWN,false);
                    }
                    ;
                    break;
                case Assault_PMR.BUTTON_LEFT:
                    {
                        if (p_player.i_scrx - PLAYER_HORZSPEED >= 0)
                        {
                            p_player.i_scrx -= PLAYER_HORZSPEED;
                            p_player.setState(MovingObject.STATE_LEFT,false);
                        }
                        else
                        {
                            p_player.setState(MovingObject.STATE_UP,true);
                        }
                    }
                    ;
                    break;
                case Assault_PMR.BUTTON_RIGHT:
                    {
                        if (p_player.i_scrx + PLAYER_HORZSPEED + p_player.i_width < i_screenWidth)
                        {
                            p_player.i_scrx += PLAYER_HORZSPEED;
                            p_player.setState(MovingObject.STATE_RIGHT,false);
                        }
                        else
                        {
                            p_player.setState(MovingObject.STATE_UP,true);
                        }
                    }
                    ;
                    break;
                case Assault_PMR.BUTTON_NONE:
                    p_player.setState(MovingObject.STATE_UP,true);
                    break;
            }
        }

        if (processMovingObject())
        {
            p_gsb.i_playerState = GameStateBlock.PLAYERSTATE_LOST;
            p_gsb.i_gameState = GameStateBlock.GAMESTATE_OVER;
        }

        if (p_gsb.i_assaulternumber > 0) generateNewAssaulter();

        if (checkArrows())
        {
            p_player.setState(MovingObject.STATE_EXPLODE,false);
        }

        if (p_gsb.p_Stone.lg_Active)
        {
            if (p_gsb.p_Stone.process() && p_gsb.p_Stone.i_State == MovingObject.STATE_EXPLODE)
            {
                p_gsb.p_Stone.lg_Active = false;
                p_player.setState(MovingObject.STATE_UP,false);
            }
        }

        if (p_gsb.p_Stone.lg_Active)
        {
            lg_playerresult = p_gsb.p_Stone.process();
            if (lg_playerresult && p_gsb.p_Stone.i_State == MovingObject.STATE_EXPLODE)
            {
                p_gsb.p_Stone.lg_Active = false;
                p_gsb.p_Player.setState(MovingObject.STATE_UP,false);
            }
            else if (p_gsb.p_Stone.i_State == MovingObject.STATE_DOWN)
            {
                checkStone();
            }
        }
    }

    private void checkStone()
    {
        MovingObject[] ap_objects = p_gsb.ap_MovingObjects;
        MovingObject p_stone = p_gsb.p_Stone;

        p_stone.i_scry += STONESPEED;
        if (p_stone.i_scry + p_stone.i_height >= i_screenHeight)
        {
            p_stone.i_scry = i_screenHeight - p_stone.i_height - 1;
            p_stone.setState(MovingObject.STATE_EXPLODE,false);
            return;
        }

        int i_plx = p_stone.i_scrx;
        int i_ply = p_stone.i_scry;
        int i_plw = p_stone.i_width;
        int i_plh = p_stone.i_height;

        int i_cx = (i_plx + (p_stone.i_width >> 1)) / VIRTUALCELL_WIDTH;
        int i_cy = (i_ply + (p_stone.i_height >> 1)) / VIRTUALCELL_HEIGHT;

        if (Assault_GSB.getElement(i_cx,i_cy) == Assault_GSB.CELL_LADDER)
        {
            Assault_GSB.setElement(i_cx,i_cy,Assault_GSB.CELL_NONE);
            if (p_gameActionListener != null) p_gameActionListener.gameAction(GAMEACTION_FIELDCHANGED,i_cx,i_cy);
            p_gsb.i_playerScore += 2;
            p_stone.i_scrx = i_cx * VIRTUALCELL_WIDTH;
            p_stone.i_scry = i_cy * VIRTUALCELL_HEIGHT;
            p_stone.setState(MovingObject.STATE_EXPLODE,false);
            return;
        }

        for (int li = 0;li < ap_objects.length;li++)
        {
            MovingObject p_obj = ap_objects[li];

            if (p_obj.lg_Active)
            {
                if (p_obj.i_Type == MovingObject.TYPE_ASSAULTER && p_obj.i_State != MovingObject.STATE_EXPLODE)
                {
                    int i_ax = p_obj.i_scrx;
                    int i_ay = p_obj.i_scry;
                    int i_w = p_obj.i_width;
                    int i_h = p_obj.i_height;

                    if (!((i_plx + i_plw <= i_ax) || (i_ply + i_plh <= i_ay) || (i_plx >= i_ax + i_w) || (i_ply >= i_ay + i_h)))
                    {
                        p_gsb.i_playerScore += 10;
                        p_obj.setState(MovingObject.STATE_EXPLODE,false);
                        p_gsb.i_curassaulternumber--;
                        p_gsb.p_Stone.lg_Active = false;
                        p_gsb.p_Player.setState(MovingObject.STATE_UP,false);

                        if (p_gsb.i_assaulternumber == 0 && p_gsb.i_curassaulternumber == 0)
                        {
                            p_gsb.i_gameState = GameStateBlock.GAMESTATE_OVER;
                            p_gsb.i_playerState = GameStateBlock.PLAYERSTATE_WON;
                        }

                        break;
                    }
                }
            }
        }
    }

    private boolean checkArrows()
    {
        MovingObject[] ap_objects = p_gsb.ap_MovingObjects;

        int i_plx = p_gsb.p_Player.i_scrx;
        int i_ply = p_gsb.p_Player.i_scry;
        int i_plw = p_gsb.p_Player.i_width;
        int i_plh = p_gsb.p_Player.i_height;

        for (int li = 0;li < ap_objects.length;li++)
        {
            MovingObject p_obj = ap_objects[li];

            if (p_obj.lg_Active)
            {
                if (p_obj.i_Type == MovingObject.TYPE_ARROW)
                {
                    int i_ax = p_obj.i_scrx;
                    int i_ay = p_obj.i_scry;
                    int i_w = p_obj.i_width;
                    int i_h = p_obj.i_height;

                    if (!((i_plx + i_plw <= i_ax) || (i_ply + i_plh <= i_ay) || (i_plx >= i_ax + i_w) || (i_ply >= i_ay + i_h))) return true;
                }
            }
        }
        return false;
    }

    public String getGameID()
    {
        return "ASSAULT";
    }

    public int getMaxSizeOfSavedGameBlock()
    {
        return 10 * (NUMBER_MOVEOBJECTS + 2) + FIELD_WIDTH * FIELD_HEIGHT + 10;
    }
}
