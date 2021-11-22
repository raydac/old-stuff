package com.igormaznitsa.GameKit_C6B333.Squirrel;

import com.igormaznitsa.gameapi.Gamelet;
import com.igormaznitsa.gameapi.GameActionListener;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.DataOutputStream;

public class GameletImpl extends Gamelet
{
    public static final int PLAYER_BUTTON_NONE = 0;
    public static final int PLAYER_BUTTON_LEFT = 1;
    public static final int PLAYER_BUTTON_RIGHT = 2;
    public static final int PLAYER_BUTTON_UP = 3;
    public static final int PLAYER_BUTTON_DOWN = 4;
    public static final int PLAYER_BUTTON_FIRE = 5;
    public static final int PLAYER_BUTTON_JUMP = 6;
    public static final int PLAYER_BUTTON_JUMPLEFT = 7;
    public static final int PLAYER_BUTTON_JUMPRIGHT = 8;

    //========================================================================
    public static final int GAMEACTION_NUTSTATECHANGED = 0;
    //========================================================================

    public static final int GAMELEVEL_EASY = 0;
    public static final int GAMELEVEL_NORMAL = 1;
    public static final int GAMELEVEL_HARD = 2;

    public static final int TIMEDELAY_EASY = 140;
    public static final int TIMEDELAY_NORMAL = 120;
    public static final int TIMEDELAY_HARD = 100;

    public static final int ATTEMPTIONS_EASY = 4;
    public static final int ATTEMPTIONS_NORMAL = 3;
    public static final int ATTEMPTIONS_HARD = 3;

    public int i_PlayerKey;
    public int i_GameTimeDelay;

    public static final int VIRTUALCELL_WIDTH = 12;
    public static final int VIRTUALCELL_HEIGHT = 12;
    public static final int I8_VIRTUALCELL_WIDTH = VIRTUALCELL_WIDTH << 8;
    public static final int I8_VIRTUALCELL_HEIGHT = VIRTUALCELL_HEIGHT << 8;

    public Sprite p_Player;
    public Sprite p_DroppedNut;
    public Sprite p_HunterBullet;

    public byte[] ab_StageArray;
    public int i_StageWidth,i_StageHeigh;

    public int i_maxNutNumber;
    public int i_currentNutNumber;
    public int i_takenNutNumber;
    public int i_summaryNuts;

    public int i_MaxSpriteNum;

    public int i_Attemptions;

    public static final int MAXSPRITEOBJECTS = 60;
    public Sprite[] ap_ObjectSprites;

    private static final int MAXTIME_CATERPILLAR_REMOVED = 100;
    private static final int DROPPEDNUT_DECREMENT = 10;
    private static final int I8_DROP_VERTSPEED = 0x500;

    private static final int I8_PLAYERVERTSPEED = 0x300;
    private static final int I8_PLAYERHORZSPEED = 0x400;
    private static final int I8_BEESPEED = 0x150;
    private static final int I8_CATERPILLARSPEED = 0x130;
    private static final int I8_LOGGERSPEED = 0x100;
    private static final int I8_HUNTERSPEED = 0x100;
    private static final int I8_BEETLESPEED = 0x100;
    private static final int I8_BULLETSPEED = 0x300;


    public static final boolean  LG_INFINITE_LIFE = false;
    public static final int  I_PLAYERMAXHEALTH = 100;
    private static final int I_BEEMAXHEALTH = 10;
    private static final int I_HUNTERMAXHEALTH = 10;
    private static final int I_LOGGERMAXHEALTH = 10;
    private static final int I_OWLMAXHEALTH = 10;
    private static final int I_BEETLEMAXHEALTH = 10;
    private static final int I_CATERPILLARMAXHEALTH = 10;

    private static final int I8_PLAYERJUMP_X_RADIUS = 26<<8;
    private static final int I8_PLAYERJUMP_Y_RADIUS = 34<<8;
    private static final int I_PLAYERJUMPSPEED = 2;

    private static final int I_BEEDAMAGE = 5;
    private static final int I_LOGGERDAMAGE = 15;
    private static final int I_CATERPILLARDAMAGE = 10;
    private static final int I_OWLDAMAGE = 10;
    private static final int I_HUNTERBULLETDAMAGE = 40;
    private static final int I_HUNTERDAMAGE = 10;
    private static final int I_BEETLEDAMAGE = 15;

    private int i_playerJumpAngle;
    private int i8_playerjumppointX;
    private int i8_playerjumppointY;

    private boolean lg_collided_duringJump;

    public GameletImpl(int _screenWidth, int _screenHeight, GameActionListener _gameActionListener)
    {
        super(_screenWidth, _screenHeight, _gameActionListener);
    }

    public String getGameID()
    {
        return "NHUNTER";
    }

    private final void deinitAllSprites(boolean _nutstoo)
    {
        for (int li = 0; li < MAXSPRITEOBJECTS; li++)
        {
            Sprite p_spr = ap_ObjectSprites[li];
            switch (p_spr.i_objectType)
            {
                case Sprite.OBJECT_NUT:
                    {
                        if (_nutstoo)
                            p_spr.lg_SpriteActive = false;
                    }
                    ;
                    break;
                default :
                    p_spr.lg_SpriteActive = false;
            }
        }
        p_DroppedNut.lg_SpriteActive = false;
        p_HunterBullet.lg_SpriteActive = false;
    }

    public void resumeGameAfterPlayerLost()
    {
        i_PlayerState = PLAYERSTATE_NORMAL;
        i_GameState = GAMESTATE_PLAYED;
        initPlayer();
        reinitObjects(false);
    }

    public int getMaxSizeOfSavedGameBlock()
    {
        return 1200;
    }

    private void playerCollided(Sprite _sprite)
    {
        if (_sprite == null) return;
        int i_damage = 0;
        boolean lg_process = true;
        if (p_Player.i_objectState == Sprite.STATE_UNDERFIRE || p_Player.i_objectState == Sprite.STATE_DEATH) lg_process = false;

        if (_sprite.equals(p_HunterBullet))
        {
            i_damage = I_HUNTERBULLETDAMAGE;
            p_HunterBullet.lg_SpriteActive = false;
        }
        else
        {
            switch (_sprite.i_objectType)
            {
                case Sprite.OBJECT_BEE:
                    i_damage = I_BEEDAMAGE;
                    break;
                case Sprite.OBJECT_BEETLE:
                    i_damage = I_BEETLEDAMAGE;
                    break;
                case Sprite.OBJECT_CATERPILLAR:
                    i_damage = I_CATERPILLARDAMAGE;
                    break;
                case Sprite.OBJECT_HUNTER:
                    i_damage = I_HUNTERDAMAGE;
                    break;
                case Sprite.OBJECT_LOGGER:
                    i_damage = I_LOGGERDAMAGE;
                    break;
                case Sprite.OBJECT_OWL:
                    i_damage = I_OWLDAMAGE;
                    break;
                case Sprite.OBJECT_NUT:
                    {
                        i_currentNutNumber++;
                        i_takenNutNumber++;
                        _sprite.lg_SpriteActive = false;
                        if (p_GameActionListener != null) p_GameActionListener.gameAction(GAMEACTION_NUTSTATECHANGED, _sprite.i_SpriteID);
                        return;
                    }
            }
        }

        if (lg_process)
        {
            p_Player.i_ObjectHealth -= i_damage;
            if (p_Player.i_objectState == Sprite.STATE_BEATJUMPLEFT || p_Player.i_objectState == Sprite.STATE_BEATJUMPRIGHT)
            {
                lg_collided_duringJump = true;
            }
            else if (p_Player.i_ObjectHealth <= 0)
            {
                p_Player.i_ObjectHealth = 0;
		if(LG_INFINITE_LIFE) p_Player.i_ObjectHealth = I_PLAYERMAXHEALTH;
		    else p_Player.initState(Sprite.STATE_DEATH);
            }
            else
            {
                if (p_Player.i_objectState != Sprite.STATE_DROP)
                    p_Player.i_previousState = p_Player.i_objectState;
                p_Player.initState(Sprite.STATE_UNDERFIRE);
            }
        }
    }

    // Return true if the player is dead
    private boolean processPlayer()
    {
        switch (p_Player.i_objectState)
        {
            case Sprite.STATE_DEATH:
                {
                    return p_Player.processAnimation();
                }
            case Sprite.STATE_DROP:
                {
                    if (p_Player.processAnimation())
                    {
                        p_DroppedNut.initState(Sprite.STATE_DROP);
                        p_DroppedNut.setMainXY8(p_Player.i8_mainX, p_Player.i8_mainY);
                        p_DroppedNut.lg_SpriteActive = true;
                        p_Player.initState(p_Player.i_previousState);
                        i_takenNutNumber--;
                    }
                }
                ;
                break;
            case Sprite.STATE_UNDERFIRE:
                {
                    if (p_Player.processAnimation())
                    {
                        p_Player.initState(p_Player.i_previousState);
                    }
                }
                ;
                break;
            case Sprite.STATE_BEATJUMPRIGHT:
            case Sprite.STATE_BEATJUMPLEFT:
                {
                    boolean lg_ground = false;

                    if (i_playerJumpAngle < 0) // ju
                    {
                        // The player is down, rest in peace, squirrel. You will be living at our hearts
                        int i8_y = p_Player.i8_ScreenY + p_Player.i8_height + I8_DROP_VERTSPEED;

                        if (getCellFromCoordsI8(p_Player.i8_mainX, i8_y) == Stages.OBJECT_NONE)
                        {
                            p_Player.setMainXY8(p_Player.i8_mainX, p_Player.i8_mainY + I8_DROP_VERTSPEED);
                            p_Player.i_Frame = p_Player.i_maxFrames - 1;
                        }
                        else
                        {
                            lg_ground = true;
                        }
                    }
                    else
                    {
                        i_playerJumpAngle += I_PLAYERJUMPSPEED;
                            int i_angle;
                            int i8_newmx;
                            i_angle = i_playerJumpAngle;
                            if (p_Player.i_objectState == Sprite.STATE_BEATJUMPLEFT)
                            {
                                i8_newmx = i8_playerjumppointX + xCoSine(I8_PLAYERJUMP_X_RADIUS, i_angle);
                            }
                            else
                            {
                                i8_newmx = i8_playerjumppointX - xCoSine(I8_PLAYERJUMP_X_RADIUS, i_angle);
                            }
                            int i8_newmy = i8_playerjumppointY - xSine(I8_PLAYERJUMP_Y_RADIUS, i_angle);
                            p_Player.setMainXY8(i8_newmx, i8_newmy);

                            if (p_Player.i_objectState == Sprite.STATE_BEATJUMPLEFT)
                            {
 			        int obj = getCellFromCoordsI8(p_Player.i8_ScreenX, p_Player.i8_ScreenY)&0xf;
                                if (obj == Stages.OBJECT_EARTH )
                                {
                                    p_Player.setScreenXY8(alignXI8(p_Player.i8_ScreenX + I8_VIRTUALCELL_WIDTH), p_Player.i8_ScreenY);
                                    i_playerJumpAngle = -1;
                                    p_Player.i_Frame = p_Player.i_maxFrames - 1;
                                }
				else
				  if (i_playerJumpAngle>15)
				  {
				    int obj_dwn = getCellFromCoordsI8(p_Player.i8_ScreenX, p_Player.i8_ScreenY+p_Player.i8_height-1)&0xf;
				    if(obj_dwn == Stages.OBJECT_BRANCH || obj_dwn == Stages.OBJECT_TRUNK)
                                     if (obj != Stages.OBJECT_NONE )
                                     {
				        i_playerJumpAngle = -1;
                                        p_Player.setMainXY8(alignXI8(p_Player.i8_mainX), p_Player.i8_mainY);
				        p_Player.initState(Sprite.STATE_GOTOP);
                                     }
				     else
				     {
				        i_playerJumpAngle = -1;
                                        p_Player.setMainXY8(p_Player.i8_mainX, alignYI8(p_Player.i8_mainY));
				        p_Player.initState(Sprite.STATE_GOLEFT);
				     }
				}
                            }
                            else
                            {
			        int obj = getCellFromCoordsI8(p_Player.i8_ScreenX + p_Player.i8_width, p_Player.i8_ScreenY)&0xf;
                                if (obj == Stages.OBJECT_EARTH)
                                {
                                    p_Player.setScreenXY8(alignXI8(p_Player.i8_ScreenX + p_Player.i8_width) - p_Player.i8_width, p_Player.i8_ScreenY);
                                    p_Player.i_Frame = p_Player.i_maxFrames - 1;
                                    i_playerJumpAngle = -1;
                                }
				else
				  if (i_playerJumpAngle>15)
				  {
				    int obj_dwn = getCellFromCoordsI8(p_Player.i8_ScreenX + (p_Player.i8_width>>2), p_Player.i8_ScreenY+p_Player.i8_height-1)&0xf;
				    if(obj_dwn == Stages.OBJECT_BRANCH || obj_dwn == Stages.OBJECT_TRUNK)
                                     if (obj != Stages.OBJECT_NONE )
                                     {
				        i_playerJumpAngle = -1;
                                        p_Player.setMainXY8(alignXI8(p_Player.i8_mainX + (p_Player.i8_width>>2)), p_Player.i8_mainY);
				        p_Player.initState(Sprite.STATE_GOTOP);
                                     }
				     else
				     {
				        i_playerJumpAngle = -1;
                                        p_Player.setMainXY8(p_Player.i8_mainX + (p_Player.i8_width>>2), alignYI8(p_Player.i8_mainY));
				        p_Player.initState(Sprite.STATE_GORIGHT);
				     }
				}
                            }
                        if (i_playerJumpAngle >= 30)
                        {
                            i_playerJumpAngle = -1;
                        }
                    }

                    if (lg_ground)
                    {
                        if (p_Player.i_objectState == Sprite.STATE_BEATJUMPLEFT)
                        {
                            p_Player.initState(Sprite.STATE_GOLEFT);
                        }
                        else
                        {
                            p_Player.initState(Sprite.STATE_GORIGHT);
                        }
                        p_Player.setMainXY8(p_Player.i8_mainX, alignYI8(p_Player.i8_ScreenY + p_Player.i8_height) /*+ I8_VIRTUALCELL_HEIGHT - (p_Player.i8_height >> 1)*/);
                        if (lg_collided_duringJump)
                        {
                            if (p_Player.i_ObjectHealth == 0)
                            {
				if(LG_INFINITE_LIFE) p_Player.i_ObjectHealth = I_PLAYERMAXHEALTH;
				 else p_Player.initState(Sprite.STATE_DEATH);
                            }
                            else
                            {
                                p_Player.i_previousState = p_Player.i_objectState;
                                p_Player.initState(Sprite.STATE_UNDERFIRE);
                            }
                        }
                    }
                }
                ;
                break;
            case Sprite.STATE_APPEARANCE:
                {
                    if (p_Player.processAnimation())
                    {
                        p_Player.initState(Sprite.STATE_GORIGHT);
                    }
                }
                ;
                break;
            default :
                {
                    int i_playerState = p_Player.i_objectState;

                    int i8_scrX = p_Player.i8_ScreenX;
                    int i8_scrY = p_Player.i8_ScreenY;
                    int i8_wdth = p_Player.i8_width;
                    int i8_hght = p_Player.i8_height;
                    int i8_hghthlf = i8_hght >> 1;
                    int i8_mainX = p_Player.i8_mainX;
                    int i8_mainY = p_Player.i8_mainY;

                    if (getCellFromCoordsI8(i8_mainX, i8_scrY + i8_hght + 0x100) == Stages.OBJECT_NONE)
                    {
                        if (p_Player.i_objectState == Sprite.STATE_GOLEFT)
                        {
                            p_Player.initState(Sprite.STATE_BEATJUMPLEFT);
                        }
                        else
                        {
                            p_Player.initState(Sprite.STATE_BEATJUMPRIGHT);
                        }
                        p_Player.i_Frame = p_Player.i_maxFrames - 1;
                        i_playerJumpAngle = -1;
                        p_Player.i_param0 = 0;
                    }
                    else
                        switch (i_PlayerKey)
                        {
                            case PLAYER_BUTTON_JUMPLEFT:
                                {
                                    p_Player.initState(Sprite.STATE_BEATJUMPLEFT);
                                    i8_playerjumppointX = p_Player.i8_mainX - I8_PLAYERJUMP_X_RADIUS;
                                    i8_playerjumppointY = p_Player.i8_mainY;

                                    i_playerJumpAngle = 0;
                                    lg_collided_duringJump = false;
                                }
                                ;
                                break;
                            case PLAYER_BUTTON_JUMPRIGHT:
                                {
                                    p_Player.initState(Sprite.STATE_BEATJUMPRIGHT);
                                    i8_playerjumppointX = p_Player.i8_mainX + I8_PLAYERJUMP_X_RADIUS;
                                    i8_playerjumppointY = p_Player.i8_mainY;

                                    i_playerJumpAngle = 0;
                                    lg_collided_duringJump = false;
                                }
                                ;
                                break;

                            case PLAYER_BUTTON_JUMP:
                                {
                                    switch (p_Player.i_objectState)
                                    {
                                        case Sprite.STATE_GOLEFT:
                                            {
                                                p_Player.initState(Sprite.STATE_BEATJUMPLEFT);
                                                i8_playerjumppointX = p_Player.i8_mainX - I8_PLAYERJUMP_X_RADIUS;
                                                i8_playerjumppointY = p_Player.i8_mainY;
                                            }
                                            ;
                                            break;
                                        case Sprite.STATE_GORIGHT:
                                            {
                                                p_Player.initState(Sprite.STATE_BEATJUMPRIGHT);
                                                i8_playerjumppointX = p_Player.i8_mainX + I8_PLAYERJUMP_X_RADIUS;
                                                i8_playerjumppointY = p_Player.i8_mainY;
                                            }
                                            ;
                                            break;
                                    }
                                    i_playerJumpAngle = 0;
                                    lg_collided_duringJump = false;
                                }
                                ;
                                break;
                            case PLAYER_BUTTON_LEFT:
                                {
                                    i8_scrX -= I8_PLAYERHORZSPEED;
                                    int i_elem = getCellFromCoordsI8(i8_scrX, i8_mainY) &0xf;
                                    if (i_elem != Stages.OBJECT_EARTH && i_elem != Stages.OBJECT_BRANCH)
                                    {
                                        if (i_playerState == Sprite.STATE_GOLEFT)
                                        {
                                            p_Player.processAnimation();
                                            p_Player.setMainXY8(i8_mainX - I8_PLAYERHORZSPEED, alignYI8(i8_mainY) /*+ I8_VIRTUALCELL_HEIGHT - i8_hghthlf*/);
                                        }
                                        else
                                        {
					    if(i_playerState == Sprite.STATE_GODOWN) {
					      p_Player.initState(Sprite.STATE_GOLEFT);
                                              p_Player.setMainXY8(p_Player.i8_mainX , p_Player.i8_mainY + i8_hght - p_Player.i8_height);
					    } else
                                                p_Player.initState(Sprite.STATE_GOLEFT);
                                            p_Player.setMainXY8(p_Player.i8_mainX , alignYI8(p_Player.i8_mainY));
                                        }
                                    }
                                }
                                ;
                                break;
                            case PLAYER_BUTTON_RIGHT:
                                {
                                    i8_scrX += i8_wdth + I8_PLAYERHORZSPEED;
                                    int i_elem = getCellFromCoordsI8(i8_scrX, i8_mainY) & 0xf;

                                    if (i_elem != Stages.OBJECT_EARTH && i_elem != Stages.OBJECT_BRANCH)
                                    {
                                        if (i_playerState == Sprite.STATE_GORIGHT)
                                        {
                                            p_Player.processAnimation();
                                            p_Player.setMainXY8(i8_mainX + I8_PLAYERHORZSPEED, alignYI8(i8_mainY) /*+ I8_VIRTUALCELL_HEIGHT - i8_hghthlf*/);
                                        }
                                        else
                                        {
					    if(i_playerState == Sprite.STATE_GODOWN) {
					      p_Player.initState(Sprite.STATE_GORIGHT);
                                              p_Player.setMainXY8(p_Player.i8_mainX , p_Player.i8_mainY + i8_hght - p_Player.i8_height);
					    } else
					        p_Player.initState(Sprite.STATE_GORIGHT);
                                            p_Player.setMainXY8(p_Player.i8_mainX , alignYI8(p_Player.i8_mainY));
                                        }
                                    }
                                }
                                ;
                                break;
                            case PLAYER_BUTTON_UP:
                                {

                                    int i_elem;
				    if(i_playerState == Sprite.STATE_GORIGHT)
				       i_elem = getCellFromCoordsI8(i8_mainX + i8_hght, i8_scrY + i8_hght - I8_PLAYERVERTSPEED -1);
				     else
				      i_elem = getCellFromCoordsI8(i8_mainX, i8_scrY + i8_hght - I8_PLAYERVERTSPEED -1);
                                    if (i_elem != Stages.OBJECT_NONE)
                                    {
                                        if (i_playerState == Sprite.STATE_GOTOP)
                                        {
                                            p_Player.processAnimation();
                                            p_Player.setMainXY8(alignXI8(i8_mainX)/* + (I8_VIRTUALCELL_WIDTH >> 1)*/, i8_mainY - I8_PLAYERVERTSPEED);
                                        }
                                        else
                                        {
                                            p_Player.initState(Sprite.STATE_GOTOP);
                                            if (i_playerState == Sprite.STATE_GORIGHT)
                                            p_Player.setMainXY8(alignXI8(i8_mainX+i8_hght), i8_mainY);
                                        }
                                    }
				    else
                                        if (i_playerState == Sprite.STATE_GOTOP)
				        {
					   p_Player.initState(Sprite.STATE_GOLEFT);
                                           p_Player.setMainXY8(i8_mainX, alignYI8(i8_mainY + i8_hght - p_Player.i8_height));
				        }
                                }
                                ;
                                break;
                            case PLAYER_BUTTON_DOWN:
                                {
                                    i8_scrY = i8_scrY + i8_hght + I8_PLAYERVERTSPEED;
                                    int i_elem = getCellFromCoordsI8(i8_mainX, i8_scrY);
                                    if (i_elem != Stages.OBJECT_EARTH)
                                    {
                                        if (i_playerState == Sprite.STATE_GODOWN)
                                        {
                                            p_Player.processAnimation();
                                            p_Player.setMainXY8(alignXI8(i8_mainX) /*+ (I8_VIRTUALCELL_WIDTH >> 1)*/, i8_mainY + I8_PLAYERVERTSPEED);
                                        }
                                        else
                                        {
                                            p_Player.initState(Sprite.STATE_GODOWN);
                                        }
                                    }
                                }
                                ;
                                break;
                            case PLAYER_BUTTON_FIRE:
                                {
                                    if (!p_DroppedNut.lg_SpriteActive && i_takenNutNumber > 0)
                                    {
                                        p_Player.i_previousState = p_Player.i_objectState;
                                        p_Player.initState(Sprite.STATE_DROP);
                                    }
                                }
                                ;
                                break;
                        }
                }
        }
        return false;
    }

    private int alignYI8(int _i8y)
    {
        return (_i8y / I8_VIRTUALCELL_HEIGHT) * I8_VIRTUALCELL_HEIGHT;
    }

    private int alignXI8(int _i8x)
    {
        return (_i8x / I8_VIRTUALCELL_WIDTH) * I8_VIRTUALCELL_WIDTH;
    }

    public int getPlayerScore()
    {
        int i_score = ((i_PlayerScore * (i_GameLevel+1))>>1)+100*i_Attemptions+3*i_summaryNuts;
        return i_score;
    }

    private void initPlayer()
    {
        p_Player.i_ObjectHealth = I_PLAYERMAXHEALTH;
        int i8_mainX = (p_Player.i_initcellX * VIRTUALCELL_WIDTH) << 8;
        int i8_mainY = ((p_Player.i_initcellY * VIRTUALCELL_HEIGHT)/* + VIRTUALCELL_HEIGHT - 1*/) << 8;
        p_Player.loadSprite(Sprite.OBJECT_SQUIRREL, Sprite.STATE_APPEARANCE);
        p_Player.setMainXY8(i8_mainX, i8_mainY);
        i_PlayerKey = PLAYER_BUTTON_NONE;
    }

    private void reinitObjects(boolean _nutstoo)
    {
        p_HunterBullet.lg_SpriteActive = false;
        p_DroppedNut.lg_SpriteActive = false;

        for (int li = 0; li < i_MaxSpriteNum; li++)
        {
            int i_maxhealth = 0;
            Sprite p_spr = ap_ObjectSprites[li];
            switch (p_spr.i_objectType)
            {
                case Sprite.OBJECT_BEE:
                    {
                        i_maxhealth = I_BEEMAXHEALTH;

                        if (getRandomInt(100) > 50)
                            p_spr.i_param0 = 0;
                        else
                            p_spr.i_param0 = -1;

                        if (getRandomInt(100) > 50)
                            p_spr.i_param1 = 0;
                        else
                            p_spr.i_param1 = -1;

                        if (p_spr.i_param0 == 0)
                        {
                            p_spr.initState(Sprite.STATE_GOLEFT);
                        }
                        else
                        {
                            p_spr.initState(Sprite.STATE_GORIGHT);
                        }
                    }
                    ;
                    break;
                case Sprite.OBJECT_BEETLE:
                    {
                        i_maxhealth = I_BEETLEMAXHEALTH;
                    }
                    ;
                    break;
                case Sprite.OBJECT_CATERPILLAR:
                    {
                        i_maxhealth = I_CATERPILLARMAXHEALTH;
                    }
                    ;
                    break;
                case Sprite.OBJECT_LOGGER:
                    {
                        i_maxhealth = I_LOGGERMAXHEALTH;
                    }
                    ;
                    break;
                case Sprite.OBJECT_NUT:
                    {
                        if (_nutstoo)
                            i_maxhealth = 0;
                        else
                            continue;
                    }
                    ;
                    break;
                case Sprite.OBJECT_OWL:
                    {
                        i_maxhealth = I_OWLMAXHEALTH;
                    }
                    ;
                    break;
                case Sprite.OBJECT_HUNTER:
                    {
                        i_maxhealth = I_HUNTERMAXHEALTH;
                    }
                    ;
                    break;
            }

            int i8_mainX = alignXI8((p_spr.i_initcellX * VIRTUALCELL_WIDTH) << 8);
            int i8_mainY = alignYI8(((p_spr.i_initcellY * VIRTUALCELL_HEIGHT)) << 8);
//            p_spr.setScreenXY8(i8_mainX, i8_mainY + I8_VIRTUALCELL_HEIGHT - p_spr.i8_height);
            p_spr.setMainXY8(i8_mainX, i8_mainY /*+ I8_VIRTUALCELL_HEIGHT - p_spr.i8_height*/);
            p_spr.i_ObjectHealth = i_maxhealth;
        }
    }

    public void initStage(int _stage)
    {
        super.initStage(_stage);
        deinitAllSprites(true);
        ab_StageArray = getStageArray(_stage);
        i_currentNutNumber = 0;
        i_maxNutNumber = 0;
        i_takenNutNumber = 0;

        i_MaxSpriteNum = 0;
        for (int loffst = 0; loffst < ab_StageArray.length; loffst++)
        {
            int i_object = (ab_StageArray[loffst])&0xf;
            int i_x = loffst % i_StageWidth;
            int i_y = loffst / i_StageWidth;
            boolean lg_dynamicobject = true;
            switch (i_object)
            {
                case Stages.OBJECT_PLAYER:
                    {
                        ab_StageArray[loffst] = Stages.OBJECT_NONE;
                        p_Player.i_initcellX = i_x;
                        p_Player.i_initcellY = i_y;
                        initPlayer();
                        continue;
                    }
                case Stages.OBJECT_NONE:
                case Stages.OBJECT_EARTH:
                case Stages.OBJECT_BRANCH:
                case Stages.OBJECT_TRUNK:
                    {
                        lg_dynamicobject = false;
                    }
                    ;
                    break;
                default:
                    {
		       if(i_MaxSpriteNum < MAXSPRITEOBJECTS)
		       {
                         Sprite p_sprite = ap_ObjectSprites[i_MaxSpriteNum++];
                         switch (i_object)
                         {
                            case Stages.OBJECT_BEE:
                                p_sprite.loadSprite(Sprite.OBJECT_BEE, Sprite.STATE_APPEARANCE);
                                break;
                            case Stages.OBJECT_BEETLE:
                                p_sprite.loadSprite(Sprite.OBJECT_BEETLE, Sprite.STATE_APPEARANCE);
                                break;
                            case Stages.OBJECT_CATERPILLAR:
                                p_sprite.loadSprite(Sprite.OBJECT_CATERPILLAR, Sprite.STATE_APPEARANCE);
                                break;
                            case Stages.OBJECT_HUNTER:
                                p_sprite.loadSprite(Sprite.OBJECT_HUNTER, Sprite.STATE_APPEARANCE);
                                break;
                            case Stages.OBJECT_LOGGER:
                                {
                                    p_sprite.loadSprite(Sprite.OBJECT_LOGGER, Sprite.STATE_APPEARANCE);
                                }
                                ;
                                break;
                            case Stages.OBJECT_NUT:
                                {
                                    p_sprite.loadSprite(Sprite.OBJECT_NUT, Sprite.STATE_APPEARANCE);
                                    i_maxNutNumber++;
                                }
                                ;
                                break;
                            case Stages.OBJECT_OWL:
                                p_sprite.loadSprite(Sprite.OBJECT_OWL, Sprite.STATE_APPEARANCE);
                                break;
                         }
                         p_sprite.i_initcellX = i_x;
                         p_sprite.i_initcellY = i_y;
		       }
                    }
            }
            if (lg_dynamicobject)
            {
                ab_StageArray[loffst] = Stages.OBJECT_NONE;
            }
        }
        initPlayer();
        reinitObjects(true);
    }

    public void newGameSession(int _level)
    {
        super.initLevel(_level);
        switch (_level)
        {
            case GAMELEVEL_EASY:
                {
                    i_GameTimeDelay = TIMEDELAY_EASY;
                    i_Attemptions = ATTEMPTIONS_EASY;
                }
                ;
                break;
            case GAMELEVEL_NORMAL:
                {
                    i_GameTimeDelay = TIMEDELAY_NORMAL;
                    i_Attemptions = ATTEMPTIONS_NORMAL;
                }
                ;
                break;
            case GAMELEVEL_HARD:
                {
                    i_GameTimeDelay = TIMEDELAY_HARD;
                    i_Attemptions = ATTEMPTIONS_HARD;
                }
                ;
                break;
        }

        ap_ObjectSprites = new Sprite[MAXSPRITEOBJECTS];
        p_Player = new Sprite(0);

        p_DroppedNut = new Sprite(0);
        p_DroppedNut.loadSprite(Sprite.OBJECT_NUT, Sprite.STATE_DROP);
        p_DroppedNut.lg_SpriteActive = false;

        p_HunterBullet = new Sprite(0);
        p_HunterBullet.loadSprite(Sprite.OBJECT_BULLET, Sprite.STATE_DROP);
        p_HunterBullet.lg_SpriteActive = false;

        for (int li = 0; li < MAXSPRITEOBJECTS; li++) ap_ObjectSprites[li] = new Sprite(li);
        i_summaryNuts = 0;
    }

    private int getCellFromCoords(int _x, int _y)
    {
        if (_x >= i_StageWidth || _x < 0 || _y >= i_StageHeigh) return Stages.OBJECT_EARTH;
        if (_y < 0) return Stages.OBJECT_NONE;
        return ab_StageArray[_x + _y * i_StageWidth];
    }

    private int getCellFromCoordsI8(int _i8x, int _i8y)
    {
        if (_i8x < 0) return Stages.OBJECT_EARTH;
        return getCellFromCoords(_i8x / I8_VIRTUALCELL_WIDTH, _i8y / I8_VIRTUALCELL_HEIGHT);
    }

    private Sprite processObjects()
    {
        Sprite p_playercollided = null;
        Sprite p_nutcollided = null;

        if (p_DroppedNut.lg_SpriteActive)
        {
            int i8_scrY = p_DroppedNut.i8_ScreenY + p_DroppedNut.i8_height;
            int i8_mainX = p_DroppedNut.i8_mainX;
            if (getCellFromCoordsI8(i8_mainX, i8_scrY + I8_DROP_VERTSPEED) == Stages.OBJECT_EARTH)
            {
                p_DroppedNut.lg_SpriteActive = false;
            }
            else
            {
                p_DroppedNut.setMainXY8(i8_mainX, p_DroppedNut.i8_mainY + I8_DROP_VERTSPEED);
                p_DroppedNut.processAnimation();
            }
        }

        if (p_HunterBullet.lg_SpriteActive)
        {
            if (p_Player.isCollided(p_HunterBullet))
            {
                p_playercollided = p_HunterBullet;
                p_HunterBullet.lg_SpriteActive = false;
            }
            else
            {
                if (p_HunterBullet.i8_ScreenY + p_HunterBullet.i8_height - I8_BULLETSPEED < p_Player.i8_ScreenY)
                {
                    p_HunterBullet.lg_SpriteActive = false;
                }
                else
                {
                    p_HunterBullet.setMainXY8(p_HunterBullet.i8_mainX, p_HunterBullet.i8_mainY - I8_BULLETSPEED);
                    p_HunterBullet.processAnimation();
                }
            }
        }

        for (int li = 0; li < MAXSPRITEOBJECTS; li++)
        {
            Sprite p_spr = ap_ObjectSprites[li];
            if (!p_spr.lg_SpriteActive && p_spr.i_objectType == Sprite.OBJECT_CATERPILLAR)
            {
                if (p_spr.i_param1 <= 0)
                {
                    p_spr.lg_SpriteActive = true;
                    p_spr.initState(Sprite.STATE_APPEARANCE);
                }
                else
                    p_spr.i_param1--;
            }
            else
            {
                if (!p_spr.lg_SpriteActive) continue;

                boolean lg_finalAnime = p_spr.processAnimation();

                switch (p_spr.i_objectState)
                {
                    case Sprite.STATE_BEATJUMPLEFT:
                    case Sprite.STATE_BEATJUMPRIGHT:
                        {
                            if (lg_finalAnime)
                            {
                                if (p_spr.lg_backMove)
                                {
                                    if (p_playercollided == null && p_spr.isCollided(p_Player))
                                        p_playercollided = p_spr;
                                }
                                else
                                {
                                    p_spr.initState(p_spr.i_previousState);
                                }
                            }
                        }
                        ;
                        break;
                    case Sprite.STATE_UNDERFIRE:
                        {
                            if (lg_finalAnime)
                            {
                                p_spr.initState(p_spr.i_previousState);
                            }
                        }
                        ;
                        break;
                    case Sprite.STATE_DEATH:
                        {
                            if (lg_finalAnime)
                            {
                                p_spr.lg_SpriteActive = false;
                                switch (p_spr.i_objectType)
                                {
                                    case Sprite.OBJECT_BEE:
                                        {
                                            i_PlayerScore += 10;
                                        }
                                        ;
                                        break;
                                    case Sprite.OBJECT_BEETLE:
                                        {
                                            i_PlayerScore += 10;
                                        }
                                        ;
                                        break;
                                    case Sprite.OBJECT_CATERPILLAR:
                                        {
                                            p_spr.i_param1 = getRandomInt(MAXTIME_CATERPILLAR_REMOVED / 10) * 10;
                                            i_PlayerScore += 5;
                                        }
                                        ;
                                        break;
                                    case Sprite.OBJECT_HUNTER:
                                        {
                                            i_PlayerScore += 20;
                                        }
                                        ;
                                        break;
                                    case Sprite.OBJECT_LOGGER:
                                        {
                                            i_PlayerScore += 20;
                                        }
                                        ;
                                        break;
                                    case Sprite.OBJECT_OWL:
                                        {
                                            i_PlayerScore += 15;
                                        }
                                        ;
                                        break;
                                }
                            }
                            continue;
                        }
                    case Sprite.STATE_APPEARANCE:
                        {
                            if (lg_finalAnime)
                            {
                                if (getRandomInt(300) >= 150)
                                {
                                    p_spr.initState(Sprite.STATE_GOLEFT);
                                }
                                else
                                {
                                    p_spr.initState(Sprite.STATE_GORIGHT);
                                }
                            }
                        }
                        ;
                        break;
                    default :
                        {
                            int i8_mainX = p_spr.i8_mainX;
                            int i8_mainY = p_spr.i8_mainY;
                            int i8_scrX = p_spr.i8_ScreenX;
                            int i8_scrY = p_spr.i8_ScreenY;
                            int i_param0 = p_spr.i_param0;
                            int i_param1 = p_spr.i_param1;
                            int i8_wdth = p_spr.i8_width;
                            int i8_height = p_spr.i8_height;

                            switch (p_spr.i_objectType)
                            {
                                case Sprite.OBJECT_BEE:
                                    {
                                        if (i_param0 == 0)
                                        {
                                            // Flying left
                                            if (i_param1 == 0)
                                            {
                                                // Flying down
                                                if (getCellFromCoordsI8(i8_scrX, i8_scrY + i8_height) == Stages.OBJECT_EARTH)
                                                {
                                                    p_spr.i_param1 = -1;
                                                }
                                                else
                                                {
                                                    i8_mainY += I8_BEESPEED;
                                                }
                                            }
                                            else
                                            {
                                                // Flying up
                                                if (i8_scrY <= 0)
                                                {
                                                    p_spr.i_param1 = 0;
                                                }
                                                else
                                                {
                                                    i8_mainY -= I8_BEESPEED;
                                                }
                                            }

                                            if (getCellFromCoordsI8(i8_scrX, i8_scrY) == Stages.OBJECT_EARTH)
                                            {
                                                p_spr.i_param0 = -1;
                                                p_spr.initState(Sprite.STATE_GORIGHT);
                                            }
                                            else
                                            {
                                                i8_mainX -= I8_BEESPEED;
                                            }
                                        }
                                        else
                                        {
                                            // Flying right
                                            if (i_param1 == 0)
                                            {
                                                // Flying down
                                                if (getCellFromCoordsI8(i8_scrX + i8_wdth, i8_scrY + i8_height) == Stages.OBJECT_EARTH)
                                                {
                                                    p_spr.i_param1 = -1;
                                                }
                                                else
                                                {
                                                    i8_mainY += I8_BEESPEED;
                                                }
                                            }
                                            else
                                            {
                                                // Flying up
                                                if (i8_scrY <= 0)
                                                {
                                                    p_spr.i_param1 = 0;
                                                }
                                                else
                                                {
                                                    i8_mainY -= I8_BEESPEED;
                                                }
                                            }

                                            if (getCellFromCoordsI8(i8_scrX + i8_wdth, i8_scrY) == Stages.OBJECT_EARTH)
                                            {
                                                p_spr.i_param0 = 0;
                                                p_spr.initState(Sprite.STATE_GOLEFT);
                                            }
                                            else
                                            {
                                                i8_mainX += I8_BEESPEED;
                                            }
                                        }
                                        p_spr.setMainXY8(i8_mainX, i8_mainY);
                                    }
                                    ;
                                    break;
                                case Sprite.OBJECT_BEETLE:
                                    {
                                        switch (p_spr.i_objectState)
                                        {
                                            case Sprite.STATE_GODOWN:
                                                {
                                                    i8_scrY += I8_BEETLESPEED;
                                                    if (getCellFromCoordsI8(i8_mainX, i8_scrY + i8_height + I8_BEETLESPEED) != Stages.OBJECT_EARTH)
                                                    {
                                                        p_spr.setMainXY8(i8_mainX, i8_mainY + I8_BEETLESPEED);
                                                    }
                                                    else
                                                    {
                                                        switch (getRandomInt(299) / 100)
                                                        {
                                                            case 0:
                                                                p_spr.initState(Sprite.STATE_GOLEFT);
                                                                break;
                                                            case 1:
                                                                p_spr.initState(Sprite.STATE_GORIGHT);
                                                                break;
                                                            case 2:
                                                                p_spr.initState(Sprite.STATE_GOTOP);
                                                                break;
                                                        }
                                                    }
                                                }
                                                ;
                                                break;
                                            case Sprite.STATE_GOTOP:
                                                {
                                                    i8_scrY -= I8_BEETLESPEED;
                                                    if (getCellFromCoordsI8(i8_mainX, i8_scrY - 0x100) != Stages.OBJECT_NONE)
                                                    {
                                                        p_spr.setMainXY8(i8_mainX, i8_mainY - I8_BEETLESPEED);
                                                    }
                                                    else
                                                    {
                                                        switch (getRandomInt(299) / 100)
                                                        {
                                                            case 0:
                                                                p_spr.initState(Sprite.STATE_GOLEFT);
                                                                break;
                                                            case 1:
                                                                p_spr.initState(Sprite.STATE_GORIGHT);
                                                                break;
                                                            case 2:
                                                                p_spr.initState(Sprite.STATE_GODOWN);
                                                                break;
                                                        }
                                                    }
                                                }
                                                ;
                                                break;
                                            case Sprite.STATE_GOLEFT:
                                                {
                                                    i8_scrX -= I8_BEETLESPEED;

                                                    boolean lg_changed = false;

                                                    if ((getCellFromCoordsI8(i8_mainX, i8_mainY - 0x100)&0xf) == Stages.OBJECT_TRUNK)
                                                    {
                                                        switch (getRandomInt(499) / 100)
                                                        {
                                                            case 0:
                                                                {
                                                                    p_spr.initState(Sprite.STATE_GOTOP);
                                                                    p_spr.setMainXY8(alignXI8(i8_mainX), i8_mainY);
                                                                    lg_changed = true;
                                                                }
                                                                ;
                                                                break;
                                                            case 1:
                                                                {
                                                                    p_spr.initState(Sprite.STATE_GODOWN);
                                                                    p_spr.setMainXY8(alignXI8(i8_mainX), i8_mainY);
                                                                    lg_changed = true;
                                                                }
                                                                ;
                                                                break;
                                                        }
                                                    }

                                                    if (!lg_changed)
                                                    {
                                                        if (getCellFromCoordsI8(i8_scrX, i8_scrY + i8_height + (I8_VIRTUALCELL_HEIGHT >>> 1)) == Stages.OBJECT_NONE)
                                                        {
                                                            p_spr.initState(Sprite.STATE_GORIGHT);
                                                        }
                                                        else
                                                        {
                                                            p_spr.setMainXY8(i8_mainX - I8_BEETLESPEED, i8_mainY);
                                                        }
                                                    }
                                                }
                                                ;
                                                break;
                                            case Sprite.STATE_GORIGHT:
                                                {
                                                    boolean lg_changed = false;

                                                    if ((getCellFromCoordsI8(i8_mainX, i8_mainY - 0x100) & 0x0f) == Stages.OBJECT_TRUNK)
                                                    {
                                                        switch (getRandomInt(499) / 100)
                                                        {
                                                            case 0:
                                                                {
                                                                    p_spr.initState(Sprite.STATE_GOTOP);
                                                                    p_spr.setMainXY8(alignXI8(i8_mainX), i8_mainY);
                                                                    lg_changed = true;
                                                                }
                                                                ;
                                                                break;
                                                            case 1:
                                                                {
                                                                    p_spr.initState(Sprite.STATE_GODOWN);
                                                                    p_spr.setMainXY8(alignXI8(i8_mainX), i8_mainY);
                                                                    lg_changed = true;
                                                                }
                                                                ;
                                                                break;
                                                        }
                                                    }

                                                    if (!lg_changed)
                                                    {
                                                        i8_scrX += I8_BEETLESPEED;
                                                        if (getCellFromCoordsI8(i8_scrX + i8_wdth, i8_scrY + i8_height + (I8_VIRTUALCELL_HEIGHT >>> 1)) == Stages.OBJECT_NONE)
                                                        {
                                                            p_spr.initState(Sprite.STATE_GOLEFT);
                                                        }
                                                        else
                                                        {
                                                            p_spr.setMainXY8(i8_mainX + I8_BEETLESPEED, i8_mainY);
                                                        }
                                                    }
                                                }
                                                ;
                                                break;
                                        }
                                    }
                                    ;
                                    break;
                                case Sprite.OBJECT_CATERPILLAR:
                                    {
                                        if (p_spr.i_objectState == Sprite.STATE_GOLEFT)
                                        {
                                            i8_scrX -= I8_CATERPILLARSPEED;
                                            if (getCellFromCoordsI8(i8_scrX, i8_scrY + (i8_height >> 1)) != Stages.OBJECT_NONE || getCellFromCoordsI8(i8_scrX, i8_scrY + i8_height + (I8_VIRTUALCELL_HEIGHT >>> 1)) == Stages.OBJECT_NONE)
                                            {
                                                p_spr.initState(Sprite.STATE_GORIGHT);
                                            }
                                            else
                                            {
                                                p_spr.setMainXY8(i8_mainX - I8_CATERPILLARSPEED, i8_mainY);
                                            }
                                        }
                                        else
                                        {
                                            i8_scrX += I8_CATERPILLARSPEED;
                                            if (getCellFromCoordsI8(i8_scrX + i8_wdth, i8_scrY + (i8_height >> 1)) != Stages.OBJECT_NONE || getCellFromCoordsI8(i8_scrX + i8_wdth, i8_scrY + i8_height + (I8_VIRTUALCELL_HEIGHT >>> 1)) == Stages.OBJECT_NONE)
                                            {
                                                p_spr.initState(Sprite.STATE_GOLEFT);
                                            }
                                            else
                                            {
                                                p_spr.setMainXY8(i8_mainX + I8_CATERPILLARSPEED, i8_mainY);
                                            }
                                        }
                                    }
                                    ;
                                    break;
                                case Sprite.OBJECT_HUNTER:
                                    {
                                        boolean lg_shoot = false;

                                        if (p_spr.i_objectState == Sprite.STATE_DROP)
                                        {
                                            if (lg_finalAnime)
                                            {
                                                if (!p_HunterBullet.lg_SpriteActive)
                                                {
                                                    p_HunterBullet.initState(Sprite.STATE_DROP);
                                                    p_HunterBullet.setMainXY8(i8_mainX, i8_scrY);
                                                    p_HunterBullet.lg_SpriteActive = true;
                                                }

                                                if (getRandomInt(100) > 50)
                                                {
                                                    p_spr.initState(Sprite.STATE_GOLEFT);
                                                }
                                                else
                                                {
                                                    p_spr.initState(Sprite.STATE_GORIGHT);
                                                }
                                            }

                                            lg_shoot = true;
                                        }
                                        else if (p_Player.i8_mainY < i8_mainY)
                                            if (!(p_Player.i8_ScreenX + p_Player.i8_width < i8_scrX || p_Player.i8_ScreenX > i8_scrX + i8_wdth) && !p_HunterBullet.lg_SpriteActive)
                                            {
                                                if (getRandomInt(15) == 7)
                                                {
						    p_spr.i_previousState = p_spr.i_objectState;
                                                    p_spr.initState(Sprite.STATE_DROP);
                                                    lg_shoot = true;
                                                }
                                            }

                                        if (!lg_shoot)
                                        {
                                            if (p_spr.i_objectState == Sprite.STATE_GOLEFT)
                                            {
                                                i8_scrX -= I8_HUNTERSPEED;
                                                if (getCellFromCoordsI8(i8_scrX, i8_scrY + (i8_height >> 1)) != Stages.OBJECT_NONE || getCellFromCoordsI8(i8_scrX, i8_scrY + i8_height + (I8_VIRTUALCELL_HEIGHT >>> 1)) == Stages.OBJECT_NONE)
                                                {
                                                    p_spr.initState(Sprite.STATE_GORIGHT);
                                                }
                                                else
                                                {
                                                    p_spr.setMainXY8(i8_mainX - I8_HUNTERSPEED, i8_mainY);
                                                }
                                            }
                                            else
                                            {
                                                i8_scrX += I8_HUNTERSPEED;
                                                if (getCellFromCoordsI8(i8_scrX + i8_wdth, i8_scrY + (i8_height >> 1)) != Stages.OBJECT_NONE || getCellFromCoordsI8(i8_scrX + i8_wdth, i8_scrY + i8_height + (I8_VIRTUALCELL_HEIGHT >>> 1)) == Stages.OBJECT_NONE)
                                                {
                                                    p_spr.initState(Sprite.STATE_GOLEFT);
                                                }
                                                else
                                                {
                                                    p_spr.setMainXY8(i8_mainX + I8_HUNTERSPEED, i8_mainY);
                                                }
                                            }
                                        }
                                    }
                                    ;
                                    break;
                                case Sprite.OBJECT_LOGGER:
                                    {
                                        if (p_spr.i_objectState == Sprite.STATE_GOLEFT)
                                        {
                                            i8_scrX -= I8_LOGGERSPEED;
                                            if (getCellFromCoordsI8(i8_scrX, i8_scrY + (i8_height >> 1)) != Stages.OBJECT_NONE || getCellFromCoordsI8(i8_scrX, i8_scrY + i8_height + (I8_VIRTUALCELL_HEIGHT >>> 1)) == Stages.OBJECT_NONE)
                                            {
                                                p_spr.initState(Sprite.STATE_GORIGHT);
                                            }
                                            else
                                            {
                                                p_spr.setMainXY8(i8_mainX - I8_LOGGERSPEED, i8_mainY);
                                            }
                                        }
                                        else
                                        {
                                            i8_scrX += I8_LOGGERSPEED;
                                            if (getCellFromCoordsI8(i8_scrX + i8_wdth, i8_scrY + (i8_height >> 1)) != Stages.OBJECT_NONE || getCellFromCoordsI8(i8_scrX + i8_wdth, i8_scrY + i8_height + (I8_VIRTUALCELL_HEIGHT >>> 1)) == Stages.OBJECT_NONE)
                                            {
                                                p_spr.initState(Sprite.STATE_GOLEFT);
                                            }
                                            else
                                            {
                                                p_spr.setMainXY8(i8_mainX + I8_LOGGERSPEED, i8_mainY);
                                            }
                                        }
                                    }
                                    ;
                                    break;
                                case Sprite.OBJECT_NUT:
                                    {
                                    }
                                    ;
                                    break;
                                case Sprite.OBJECT_OWL:
                                    {
                                        if (p_spr.i_objectState == Sprite.STATE_GOLEFT)
                                        {
                                            if (getRandomInt(100) == 50)
                                            {
                                                p_spr.initState(Sprite.STATE_GORIGHT);
                                            }
                                        }
                                        else
                                        {
                                            if (lg_finalAnime)
                                            {
                                                p_spr.initState(Sprite.STATE_GOLEFT);
                                            }
                                        }
                                    }
                                    ;
                                    break;
                            }

                            if (p_DroppedNut.lg_SpriteActive && p_DroppedNut.i_objectState == Sprite.STATE_DROP)
                            {
                                if (p_nutcollided == null && p_spr.i_objectType != Sprite.OBJECT_NUT)
                                {
                                    if (p_spr.isCollided(p_DroppedNut)) p_nutcollided = p_spr;
                                }
                            }

                            if (p_playercollided == null)
                            {
                                if (p_Player.isCollided(p_spr))
                                {
                                    if (p_spr.i_objectType == Sprite.OBJECT_NUT)
                                        p_playercollided = p_spr;
                                    else
                                    {
                                        p_spr.i_previousState = p_spr.i_objectState;
                                        if (p_spr.i8_mainX < p_Player.i8_mainX)
                                            p_spr.initState(Sprite.STATE_BEATJUMPRIGHT);
                                        else
                                            p_spr.initState(Sprite.STATE_BEATJUMPLEFT);
                                    }
                                }
                            }
                        }
                }
            }
        }

        if (p_nutcollided != null)
        {
            p_DroppedNut.initState(Sprite.STATE_DEATH);

            int i_health = p_nutcollided.i_ObjectHealth - DROPPEDNUT_DECREMENT;
            if (i_health <= 0)
            {
                p_nutcollided.i_ObjectHealth = 0;
                p_nutcollided.initState(Sprite.STATE_DEATH);
            }
        }

        return p_playercollided;
    }

    public void nextGameStep(int _playermoveobject)
    {
        i_PlayerKey = _playermoveobject;
        if (processPlayer())
        {
            i_Attemptions--;
            i_PlayerState = PLAYERSTATE_LOST;
            if (i_Attemptions == 0)
            {
                i_GameState = GAMESTATE_OVER;
                i_summaryNuts += i_takenNutNumber;
            }
            return;
        }
        else
        {
            if (i_currentNutNumber == i_maxNutNumber)
            {
                i_GameState = GAMESTATE_OVER;
                i_PlayerState = PLAYERSTATE_WON;
                i_summaryNuts += i_takenNutNumber;
                return;
            }
        }

        Sprite p_playercollided = processObjects();
        playerCollided(p_playercollided);
    }

    public void readFromStream(DataInputStream _dataInputStream) throws IOException
    {
        i_Attemptions = _dataInputStream.readUnsignedByte();
        i_currentNutNumber = _dataInputStream.readShort();
        i_maxNutNumber = _dataInputStream.readShort();
        i_takenNutNumber = _dataInputStream.readShort();
        i_MaxSpriteNum = _dataInputStream.readShort();
        i_playerJumpAngle = _dataInputStream.readUnsignedByte();
        i_summaryNuts = _dataInputStream.readShort();
        i8_playerjumppointX = _dataInputStream.readInt();
        i8_playerjumppointY = _dataInputStream.readInt();
        lg_collided_duringJump = _dataInputStream.readBoolean();

        // Writing objects data
        Sprite p_spr;
        for (int li = 0; li < MAXSPRITEOBJECTS; li++)
        {
            p_spr = ap_ObjectSprites[li];
            p_spr.loadSpriteFromStream(_dataInputStream);
        }
        // Writing player data
        p_Player.loadSpriteFromStream(_dataInputStream);
        // Writing bullet data
        p_HunterBullet.loadSpriteFromStream(_dataInputStream);
        // Writing dropped nut data
        p_DroppedNut.loadSpriteFromStream(_dataInputStream);
    }

    public void writeToStream(DataOutputStream _dataOutputStream) throws IOException
    {
        _dataOutputStream.writeByte(i_Attemptions);
        _dataOutputStream.writeShort(i_currentNutNumber);
        _dataOutputStream.writeShort(i_maxNutNumber);
        _dataOutputStream.writeShort(i_takenNutNumber);
        _dataOutputStream.writeShort(i_MaxSpriteNum);
        _dataOutputStream.writeByte(i_playerJumpAngle);
        _dataOutputStream.writeShort(i_summaryNuts);
        _dataOutputStream.writeInt(i8_playerjumppointX);
        _dataOutputStream.writeInt(i8_playerjumppointY);
        _dataOutputStream.writeBoolean(lg_collided_duringJump);

        // Writing objects data
        Sprite p_spr;
        for (int li = 0; li < MAXSPRITEOBJECTS; li++)
        {
            p_spr = ap_ObjectSprites[li];
            p_spr.writeSpriteToOutputStream(_dataOutputStream);
        }
        // Writing player data
        p_Player.writeSpriteToOutputStream(_dataOutputStream);
        // Writing bullet data
        p_HunterBullet.writeSpriteToOutputStream(_dataOutputStream);
        // Writing dropped nut data
        p_DroppedNut.writeSpriteToOutputStream(_dataOutputStream);
    }

    private byte[] getStageArray(int _stage) {
        byte [] ab_newarray;
        try
        {
           DataInputStream ds = new DataInputStream(getClass().getResourceAsStream("/res/map"+_stage));

           // Reading of image number
           i_StageWidth = ds.readUnsignedByte();
           i_StageHeigh = ds.readUnsignedByte();
	   ab_newarray = new byte[i_StageWidth*i_StageHeigh];
	   ds.read(ab_newarray);
	   return ab_newarray;

        } catch(Exception e) {
            i_StageWidth = 1;
            i_StageHeigh = 1;
	    ab_newarray = new byte[1];
	    ab_newarray[0] = Stages.OBJECT_PLAYER;
        }
        return ab_newarray;
    }

}
