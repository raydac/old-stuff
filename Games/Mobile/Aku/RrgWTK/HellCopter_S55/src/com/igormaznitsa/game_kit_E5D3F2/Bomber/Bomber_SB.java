package com.igormaznitsa.game_kit_E5D3F2.Bomber;

import com.igormaznitsa.gameapi.StrategicBlock;
import com.igormaznitsa.gameapi.GameActionListener;

public class Bomber_SB extends StrategicBlock
{
    public static final int GAMEACTION_ERASEBLOCK = 0;

    public static final int LEVEL0 = 0;
    public static final int LEVEL0_DELAY = 150;//180;
    public static final int LEVEL0_BOMB = 10;

    public static final int LEVEL1 = 1;
    public static final int LEVEL1_DELAY = 90;//110;
    public static final int LEVEL1_BOMB = 5;

    public static final int LEVEL2 = 2;
    public static final int LEVEL2_DELAY = 60;
    public static final int LEVEL2_BOMB = 0;

    protected static final int BOMB_DESTROYING_DEPTH = 3;

    public static final int MAX_USER_ATTEMPTIONS = 4;

    public static final int VIRTUAL_CELL_WIDTH = 10;
    public static final int VIRTUAL_CELL_HEIGHT = 4;
    public static final int PLAYER_SPRITE_WIDTH = 15;
    public static final int PLAYER_SPRITE_HEIGHT = 7;
    public static final int PLAYER_DIRECTION_LEFT = 0;
    public static final int PLAYER_DIRECTION_RIGHT = 1;
    public static final int PLAYER_ANIMATION_FRAMES = 4;
    public static final int PLAYER_ANIMATION_TICKS = 2;

    public static final int PLAYER_ANIMATION_STATE_LEFT = 0;
    public static final int PLAYER_ANIMATION_STATE_RIGHT = 1;
    public static final int PLAYER_ANIMATION_STATE_TURNLEFT = 2;
    public static final int PLAYER_ANIMATION_STATE_TURNRIGHT = 3;
    public static final int PLAYER_ANIMATION_STATE_BLASTING = 4;

    public static final int DELAY_FOR_NEXT_BOMB = 10;

    public static final int PLAYER_VERT_SPEED = VIRTUAL_CELL_HEIGHT;
    public static final int PLAYER_HORIZ_SPEED = 2;

    protected static final int PLAYER_INIT_Y = 0;
    protected static final int PLAYER_INIT_X = 0;
    protected static final int PLAYER_INIT_STATE = PLAYER_ANIMATION_STATE_RIGHT;

    protected static final int BOMB_FRAME_WIDTH = 3;
    protected static final int BOMB_FRAME_HEIGHT = 3;
    protected static final int BOMB_SPEED = 3;

    protected static final int MAX_EPLODING_OBJECTS = (Stages.STAGE_WIDTH * Stages.STAGE_HEIGHT) >> 1;
    protected static final int MAX_BOMB_OBJECTS = 10;

    protected int i_lastbombindex;
    protected int i_lastanimindex;

    public boolean isBombPresent()
    {
        if (((Bomber_GSB)p_gameStateBlock).i_tickbeforenextbomb == 0) return true; else return false;
    }

    public int getBombCounter()
    {
        return ((Bomber_GSB)p_gameStateBlock).i_bomb_counter;
    }

    public int getLastBombIndex()
    {
        return i_lastbombindex;
    }

    public int getLastAnimationObjectIndex()
    {
        return i_lastanimindex;
    }

    public int getPlayerX()
    {
        return ((Bomber_GSB)p_gameStateBlock).i_player_x;
    }

    public int getPlayerY()
    {
        return ((Bomber_GSB)p_gameStateBlock).i_player_y;
    }

    public int getPlayerAnimationFrame()
    {
        return ((Bomber_GSB)p_gameStateBlock).i_playerframe;
    }

    public int getPlayerAnimationState()
    {
        return ((Bomber_GSB)p_gameStateBlock).i_playeranimationstate;
    }


    public void initStage(int i)
    {
        p_gameStateBlock.initStage(i);
    }

    public void newGameSession(int i)
    {
        p_gameStateBlock = new Bomber_GSB();
        ((Bomber_GSB)p_gameStateBlock).initLevel(i);
    }

    public void nextGameStep(Object _playermoveobject)
    {
        Bomber_PMR p_pmr = (Bomber_PMR)_playermoveobject;
        Bomber_GSB p_gsb = (Bomber_GSB)p_gameStateBlock;

        if (p_gsb.i_tickbeforenextbomb > 0)
        {
            p_gsb.i_tickbeforenextbomb--;
        }

        boolean lg_proc = processPlayer();

        if (p_gsb.i_playeranimationstate == PLAYER_ANIMATION_STATE_BLASTING)
        {
            if (lg_proc)
            {
                p_gsb.i_playerState = Bomber_GSB.PLAYERSTATE_LOST;
                p_gsb.i_attemptions--;
                if (p_gsb.i_attemptions <= 0)
                {
                    p_gsb.i_gameState = Bomber_GSB.GAMESTATE_OVER;
                }
            }
        }
        else
        {
            if (checkPlayer())
            {
                p_gsb.setPlayerAnimationState(PLAYER_ANIMATION_STATE_BLASTING);
            }
            else if (p_pmr.i_value == Bomber_PMR.BUTTON_DROP_BOMB)
            {
                if (p_gsb.i_tickbeforenextbomb == 0)
                {
                    if (p_gsb.i_bomb_counter > 0)
                    {
                        BombObject p_obj = p_gsb.getFirstInactiveBomb();
                        if (p_obj != null)
                        {
                            p_gsb.i_bomb_counter--;
                            p_obj.init(p_gsb.i_player_x + ((PLAYER_SPRITE_WIDTH - BOMB_FRAME_WIDTH) >> 1),p_gsb.i_player_y + PLAYER_SPRITE_HEIGHT);
                            p_gsb.i_tickbeforenextbomb = DELAY_FOR_NEXT_BOMB;
                        }
                    }
                }
            }
        }
        processBomb();
        processAnimationBlocks();

        if ((p_gsb.i_blocksnumber == 0) && (p_gsb.i_playeranimationstate != PLAYER_ANIMATION_STATE_BLASTING))
        {
            p_gsb.i_playerState = Bomber_GSB.PLAYERSTATE_WON;
        }
        else if ((p_gsb.i_bomb_counter == 0) && (i_lastbombindex < 0) && (p_gsb.i_playeranimationstate != PLAYER_ANIMATION_STATE_BLASTING))
        {
            p_gsb.setPlayerAnimationState(PLAYER_ANIMATION_STATE_BLASTING);
            p_gsb.i_attemptions = 1;
        }
    }

    public void resumeGameAfterPlayerLost()
    {
        Bomber_GSB p_gsb = (Bomber_GSB)p_gameStateBlock;
        p_gsb.i_player_x = PLAYER_INIT_X;
        p_gsb.i_player_y = PLAYER_INIT_Y;
        p_gsb.i_playeranimationstate = PLAYER_INIT_STATE;
        p_gsb.i_playerState = Bomber_GSB.PLAYERSTATE_NORMAL;
        p_gsb.deactivateAllBombObjects();
        p_gsb.deactivateAllExplodedObjects();
    }

    public AnimationObject[] getExplodingAray()
    {
        return ((Bomber_GSB)p_gameStateBlock).ap_exploded_objects;
    }

    private void processAnimationBlocks()
    {
        Bomber_GSB p_gsb = (Bomber_GSB)p_gameStateBlock;
        AnimationObject[] ap_anim = p_gsb.ap_exploded_objects;
        i_lastanimindex = -1;
        for (int li = 0;li < ap_anim.length;li++)
        {
            AnimationObject p_obj = ap_anim[li];
            if (!p_obj.lg_is_active) continue;
            p_obj.i8_scry += 0x80;
            if (p_obj.processAnimation())
            {
                p_obj.lg_is_active = false;
            }
            else
                i_lastanimindex = li;
        }
    }

    private void processBomb()
    {
        Bomber_GSB p_gsb = (Bomber_GSB)p_gameStateBlock;
        BombObject[] ap_bomb = p_gsb.ap_bomb_objects;
        i_lastbombindex = -1;
        for (int li = 0;li < ap_bomb.length;li++)
        {
            BombObject p_obj = ap_bomb[li];
            if (!p_obj.lg_is_active) continue;

            p_obj.i_scry += BOMB_SPEED;
            int i_bx = p_obj.i_scrx;
            int i_by = p_obj.i_scry;

            int i_dx = (i_bx + (BOMB_FRAME_WIDTH >> 1)) / VIRTUAL_CELL_WIDTH;
            int i_dy = (i_by + (BOMB_FRAME_HEIGHT >> 1)) / VIRTUAL_CELL_HEIGHT;

            if (i_dy >= Stages.STAGE_HEIGHT)
            {
                p_obj.lg_is_active = false;
            }
            else if (p_gsb.getElementAt(i_dx,i_dy) != Stages.ELE_EMPTY)
            {
                p_gsb.setElementAt(i_dx,i_dy,Stages.ELE_EMPTY);
                if (p_gameActionListener != null) p_gameActionListener.gameAction(GAMEACTION_ERASEBLOCK,i_dx,i_dy);
                p_obj.i_destroyed++;
                p_gsb.i_blocksnumber--;
                p_gsb.i_playerScore += 2;
                if (p_obj.i_destroyed > BOMB_DESTROYING_DEPTH)
                {
                    p_obj.lg_is_active = false;
                }
                AnimationObject obj = p_gsb.getFirstInactive();
                obj.initObj(i_dx * VIRTUAL_CELL_WIDTH,i_dy * VIRTUAL_CELL_HEIGHT,AnimationObject.OBJECT_EXPLODE);
            }

            if (p_obj.lg_is_active) i_lastbombindex = li;
        }
    }

    private boolean checkPlayer()
    {
        Bomber_GSB p_gsb = (Bomber_GSB)p_gameStateBlock;
        int i_dx0 = p_gsb.i_player_x / VIRTUAL_CELL_WIDTH;
        int i_dy0 = (p_gsb.i_player_y + PLAYER_SPRITE_HEIGHT - 1) / VIRTUAL_CELL_HEIGHT;
        int i_dx1 = (p_gsb.i_player_x + PLAYER_SPRITE_WIDTH - 1) / VIRTUAL_CELL_WIDTH;
        int i_dy1 = i_dy0;
        if (p_gsb.getElementAt(i_dx0,i_dy0) != Stages.ELE_EMPTY || p_gsb.getElementAt(i_dx1,i_dy1) != Stages.ELE_EMPTY) return true;
        return false;
    }

    // Return true if animation of current player state is completed else return false
    private boolean processPlayer()
    {
        Bomber_GSB p_gsb = (Bomber_GSB)p_gameStateBlock;

        boolean lg_result = false;
        int i_dx = p_gsb.i_player_x;
        int i_dy = p_gsb.i_player_y;

        switch (p_gsb.i_playeranimationstate)
        {
            case PLAYER_ANIMATION_STATE_BLASTING:
                {
                    // Processing of player animation
                    p_gsb.i_playertick++;
                    if (p_gsb.i_playertick >= PLAYER_ANIMATION_TICKS)
                    {
                        p_gsb.i_playertick = 0;
                        p_gsb.i_playerframe++;
                        if (p_gsb.i_playerframe >= PLAYER_ANIMATION_FRAMES)
                        {
                            p_gsb.i_playerframe = 0;
                            lg_result = true;
                        }
                    }
                }
                ;
                break;
            case PLAYER_ANIMATION_STATE_LEFT:
                {
                    if (p_gsb.i_playertick == 0xFF)
                    {
                        i_dy++;
                        int i_ost = i_dy % PLAYER_VERT_SPEED;
                        if (i_ost == 0)
                        {
                            p_gsb.i_playertick = 0;
                        }
                    }
                    else
                    {
                        // Processing of player animation
                        p_gsb.i_playertick++;
                        if (p_gsb.i_playertick >= PLAYER_ANIMATION_TICKS)
                        {
                            p_gsb.i_playertick = 0;
                            p_gsb.i_playerframe++;
                            if (p_gsb.i_playerframe >= PLAYER_ANIMATION_FRAMES)
                            {
                                p_gsb.i_playerframe = 0;
                                lg_result = true;
                            }
                        }

                        i_dx -= PLAYER_HORIZ_SPEED;
                        if (i_dx <= 0)
                        {
                            i_dx = 0;
                            p_gsb.setPlayerAnimationState(PLAYER_ANIMATION_STATE_TURNRIGHT);
                        }
                    }
                }
                ;
                break;
            case PLAYER_ANIMATION_STATE_RIGHT:
                {
                    if (p_gsb.i_playertick == 0xFF)
                    {
                        i_dy++;
                        int i_ost = i_dy % PLAYER_VERT_SPEED;
                        if (i_ost == 0)
                        {
                            p_gsb.i_playertick = 0;
                        }
                    }
                    else
                    {
                        // Processing of player animation
                        p_gsb.i_playertick++;
                        if (p_gsb.i_playertick >= PLAYER_ANIMATION_TICKS)
                        {
                            p_gsb.i_playertick = 0;
                            p_gsb.i_playerframe++;
                            if (p_gsb.i_playerframe >= PLAYER_ANIMATION_FRAMES)
                            {
                                p_gsb.i_playerframe = 0;
                                lg_result = true;
                            }
                        }

                        i_dx += PLAYER_HORIZ_SPEED;
                        if (i_dx + PLAYER_SPRITE_WIDTH >= i_screenWidth)
                        {
                            i_dx = i_screenWidth - PLAYER_SPRITE_WIDTH;
                            p_gsb.setPlayerAnimationState(PLAYER_ANIMATION_STATE_TURNLEFT);
                        }
                    }
                }
                ;
                break;
            case PLAYER_ANIMATION_STATE_TURNRIGHT:
            case PLAYER_ANIMATION_STATE_TURNLEFT:
                {
                    i_dy++;
                    int i_ost = i_dy % PLAYER_VERT_SPEED;

                    if (p_gsb.i_playertick != 0xFF)
                    {
                        // Processing of player animation
                        p_gsb.i_playertick++;
                        if (p_gsb.i_playertick >= (PLAYER_VERT_SPEED / PLAYER_ANIMATION_FRAMES))
                        {
                            p_gsb.i_playertick = 0;
                            p_gsb.i_playerframe++;
                            if (p_gsb.i_playerframe >= PLAYER_ANIMATION_FRAMES)
                            {
                                p_gsb.i_playerframe = 0;
                                lg_result = true;
                                if (p_gsb.i_playeranimationstate == PLAYER_ANIMATION_STATE_TURNLEFT)
                                {
                                    p_gsb.setPlayerAnimationState(PLAYER_ANIMATION_STATE_LEFT);
                                }
                                else
                                {
                                    p_gsb.setPlayerAnimationState(PLAYER_ANIMATION_STATE_RIGHT);
                                }
                                p_gsb.i_playertick = 0xFF;
                            }
                        }
                    }

                    if (i_ost == 0)
                    {
                        p_gsb.i_playertick = 0;
                    }
                }
                ;
                break;
        }
        p_gsb.i_player_x = i_dx;
        p_gsb.i_player_y = i_dy;

        return lg_result;
    }

    public int getTimeDelay()
    {
        return ((Bomber_GSB)p_gameStateBlock).i_timedelay;
    }

    public int getAttemptions()
    {
        return ((Bomber_GSB)p_gameStateBlock).i_attemptions;
    }

    public BombObject[] getBombArray()
    {
        return ((Bomber_GSB)p_gameStateBlock).ap_bomb_objects;
    }

    public Bomber_SB(int screenWidth,int screenHeight,GameActionListener gameActionListener)
    {
        super(Stages.STAGE_WIDTH * VIRTUAL_CELL_WIDTH,screenHeight,gameActionListener);
        //#define FLAG_LEVELS_SUPPORT true
        //#define FLAG_SAVELOAD_SUPPORT true
        //#define FLAG_SCORES_SUPPORT true
        //#define FLAG_STAGES_SUPPORT true
    }

    public String getGameID()
    {
        return "BOMBER";
    }

    public int getMaxSizeOfSavedGameBlock()
    {
        return 1024;
    }
}
