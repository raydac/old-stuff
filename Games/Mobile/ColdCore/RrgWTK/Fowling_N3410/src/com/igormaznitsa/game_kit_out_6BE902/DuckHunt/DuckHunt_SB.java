package com.igormaznitsa.game_kit_out_6BE902.DuckHunt;

//import com.igormaznitsa.gameapi.GameActionListener;
import com.igormaznitsa.midp.GameCanvas;//.igormaznitsa.midp.PhoneModels.Nokia.GameCanvas;
import java.util.Random;

import java.io.*;

public class DuckHunt_SB
{

    public static final int BUTTON_NONE = 0;
    public static final int BUTTON_DOWN = 1;
    public static final int BUTTON_UP = 2;
    public static final int BUTTON_LEFT = 3;
    public static final int BUTTON_RIGHT = 4;
    public static final int BUTTON_FIRE = 5;

    public int i_value;


    public static final int GAMEACTION_SOUND_KRYA_KRYA = 0;
    public static final int GAMEACTION_SOUND_SHOOT = 1;
    public static final int GAMEACTION_SOUND_HIT = 2;
    public static final int GAMEACTION_SOUND_NOHIT = 3;

    protected static final int CHARGING_FRAME_HEIGHT = 40;

    protected static final int SIGHT_FRAME_WIDTH = 7;
    protected static final int SIGHT_FRAME_HEIGHT = 7;

    protected static final int DEFEATZONE_OFFSETX = 2;
    protected static final int DEFEATZONE_OFFSETY = 2;
    protected static final int DEFEATZONE_WIDTH = SIGHT_FRAME_WIDTH - (DEFEATZONE_OFFSETX << 1);
    protected static final int DEFEATZONE_HEIGHT = SIGHT_FRAME_HEIGHT - (DEFEATZONE_OFFSETY << 1);

    protected static final int GUN_MOVE_MINSPEED = 0x300;
    protected static final int GUN_MOVE_MAXSPEED = 0x600;
    protected static final int GUN_MOVE_SENSELEVEL_TICKS = 5;

    protected static final int LEVEL0 = 0;
    protected static final int LEVEL0_DUCKSPEED_I8 = 0x400;
    protected static final int LEVEL0_BULLETS = 40;
    protected static final int LEVEL0_TIME = 250000;

    protected static final int LEVEL1 = 1;
    protected static final int LEVEL1_DUCKSPEED_I8 = 0x600;
    protected static final int LEVEL1_BULLETS = 30;
    protected static final int LEVEL1_TIME = 180000;

    protected static final int LEVEL2 = 2;
    protected static final int LEVEL2_DUCKSPEED_I8 = 0x700;

    protected static final int LEVEL2_BULLETS = 20;
    protected static final int LEVEL2_TIME = 100000;

    protected static final int TIMEDELAY = 100;

    protected static final int MAX_OBJECT_DISTANCE = 60;
    protected static final int OBJECT_X_COEFF_I8 = 0x40;
    protected static final int OBJECT_Y_COEFF_I8 = 0x70;
    protected static final int OBJECT_Z_COEFF_I8 = 0x300;

    public static final int MAX_ANIMATION_OBJECTS = 30;
    protected static final int MAX_X_DIFFERENT = 40;
    protected static final int CANE_SPEED_I8 = 0x10;

    private static final int SCORE_NUMBER = 5;

    private Score[] ap_score;
    private DuckHunt_GSB p_gsb;

    private int i_activescores = 0;

    private int i_movetick = 0;
    protected int i_maxindex;

    private int[] ai_duck_killed_y = new int[MoveObject.DUCK_DISTANCE_STEP];
    private int DUCK_STEP_DISTANCE = MAX_OBJECT_DISTANCE / MoveObject.DUCK_DISTANCE_STEP;

    public int getTimeDelay()
    {
        return TIMEDELAY;
    }

    protected static Random p_rndgenerator = new Random(System.currentTimeMillis());
    public static int getInt(int limit)
    {
       limit++;
       limit = (int)(((long)Math.abs(p_rndgenerator.nextInt())*(long)limit)>>>31);
       return limit;
    }

    public void loadGameState(DataInputStream inputStream) throws IOException
    {
        Runtime.getRuntime().gc();

        DataInputStream dataInputStream = new DataInputStream(inputStream);

        int  _level = dataInputStream.readUnsignedByte();

        newGameSession(_level);

        int _stage = dataInputStream.readUnsignedByte();
        p_gameStateBlock.initStage(_stage);

        p_gameStateBlock.i_playerScore = dataInputStream.readInt();
//        p_gameStateBlock.i_aiScore = dataInputStream.readInt();
        p_gameStateBlock.i_playerState = dataInputStream.readUnsignedByte();
//        p_gameStateBlock.i_aiState = dataInputStream.readByte();

        p_gameStateBlock.readFromStream(dataInputStream);

        dataInputStream = null;
        Runtime.getRuntime().gc();
        deactivateAllScore();
    }

    public DuckHunt_SB(int screenWidth, int screenHeight, GameCanvas gameActionListener)
    {
        i_screenWidth = screenWidth;
        i_screenHeight = screenHeight;
        p_gamePlayerBlock = gameActionListener;
        p_gameActionListener = gameActionListener;
        p_gameStateBlock = null;

        i_gameFlags = FLAG_LEVELS_SUPPORT | FLAG_SAVELOAD_SUPPORT | FLAG_SCORES_SUPPORT;

        ap_score = new Score[SCORE_NUMBER];
        for (int li = 0; li < SCORE_NUMBER; li++)
        {
            ap_score[li] = new Score();
        }

        int i_step = ((i_screenHeight >> 1) - 15) / MoveObject.DUCK_DISTANCE_STEP;
        for (int li = 0; li < MoveObject.DUCK_DISTANCE_STEP; li++)
        {
            ai_duck_killed_y[li] = ((i_screenHeight >> 1) + 5 + li * i_step) << 8;
        }
    }

    public int getTimeTillEnd()
    {
        return (int)((p_gsb.l_endtime - System.currentTimeMillis()) / 1000);
    }

    private void deactivateAllScore()
    {
        for (int li = 0; li < SCORE_NUMBER; li++) ap_score[li].lg_active = false;
        i_activescores = 0;
    }

    private Score getInactiveScore()
    {
        for (int li = 0; li < ap_score.length; li++) if (!ap_score[li].lg_active) return ap_score[li];
        return null;
    }

    private void processScore()
    {
        for (int li = 0; li < ap_score.length; li++)
        {
            Score p_obj = ap_score[li];
            if (p_obj.lg_active) if (p_obj.process()) i_activescores--;
        }
    }

    public void newGameSession(int i)
    {
        i_value = BUTTON_NONE;
        p_gsb = new DuckHunt_GSB(i_screenWidth, i_screenHeight);
        p_gameStateBlock = p_gsb;
        p_gsb.initLevel(i);
        deactivateAllScore();
    }

    private void generateNewObject()
    {
        if (getInt(100) > 50)
        {
            MoveObject p_obj = p_gsb.getFirstInactiveMoveObject();
            if (p_obj != null)
            {
                int i_type = getInt(100);
                if (i_type < 20)
                {
                    p_obj.activate(getInt(MAX_X_DIFFERENT << 1) - MAX_X_DIFFERENT, i_screenHeight >> 1, MoveObject.OBJECT_CANE, 0, true);
                }
                else if (i_type > 97)
                {
                    i_type = getInt(10);
                    int i_dist = getInt(100);
                    if (i_dist <= 5)
                    {
                        i_dist = 0;
                    }
                    else if (i_dist >= 75)
                    {
                        i_dist = DUCK_STEP_DISTANCE;
                    }
                    else
                    {
                        i_dist = DUCK_STEP_DISTANCE << 1;
                    }

                    int i_y = getInt((i_screenHeight >> 1) - 5);
                    if (i_type < 5)
                    {
                        p_obj.activate(0, 0, MoveObject.OBJECT_DUCK_FLYING_LEFT, i_dist, true);
                        p_obj.setXY(i_screenWidth, i_y);
                    }
                    else
                    {
                        p_obj.activate(0, 0, MoveObject.OBJECT_DUCK_FLYING_RIGHT, i_dist, true);
                        p_obj.setXY(0 - p_obj.i_frame_sizeY, i_y);
                    }
                    p_gsb.i_duckscounter ++;
                }
            }
        }
    }

    private void generateDuckFromCane(MoveObject _cane)
    {
        int i_distance = ((_cane.i8_distance >> 8) / DUCK_STEP_DISTANCE) * DUCK_STEP_DISTANCE;

        MoveObject p_obj = p_gsb.getFirstInactiveMoveObject();
        if (p_obj == null) return;

        int i_type = MoveObject.OBJECT_DUCK_TAKEN_OFF_LEFT;
        if (_cane.getScrX() < (i_screenWidth >> 1))
        {
            i_type = MoveObject.OBJECT_DUCK_TAKEN_OFF_RIGHT;
        }
        p_obj.activate(_cane.getScrX(), _cane.getScrY(), i_type, i_distance << 8, true);
        p_gsb.i_duckscounter++;
        if (p_gameActionListener != null) p_gameActionListener.gameAction(GAMEACTION_SOUND_KRYA_KRYA);
    }

    protected int getMaxActiveIndex()
    {
        return i_maxindex;
    }

    private void checkShot()
    {
        MoveObject[] ap_moveobjects = p_gsb.ap_move_objects;
        Gun p_gun = p_gsb.p_gun;
        boolean lg_shoted = false;
        if (!p_gun.lg_backmove)
        {
            for (int li = 0; li < MAX_ANIMATION_OBJECTS; li++)
            {
                MoveObject p_obj = ap_moveobjects[li];
                if (!p_obj.lg_active) continue;
                if (p_obj.i_state == MoveObject.STATE_KILLED) continue;
                if (p_obj.i_type != MoveObject.OBJECT_BURST && p_obj.i_type != MoveObject.OBJECT_CANE)
                {
                    switch (p_obj.i_distanceframe)
                    {
                        case 0:
                            {
                                if (p_gun.i_firePhase == 2)
                                    if (p_gun.checkShot(p_obj.getScrX(), p_obj.getScrY(), p_obj.i_frame_sizeX, p_obj.i_frame_sizeY))
                                    {
                                        lg_shoted = true;
                                        p_obj.setState(MoveObject.STATE_KILLED, false);
                                    }
                            }
                            ;
                            break;
                        case 1:
                            {
                                if (p_gun.i_firePhase == 1)
                                    if (p_gun.checkShot(p_obj.getScrX(), p_obj.getScrY(), p_obj.i_frame_sizeX, p_obj.i_frame_sizeY))
                                    {
                                        lg_shoted = true;
                                        p_obj.setState(MoveObject.STATE_KILLED, false);
                                    }
                            }
                            ;
                            break;
                        case 2:
                            {
                                if (p_gun.i_firePhase == 0)
                                    if (p_gun.checkShot(p_obj.getScrX(), p_obj.getScrY(), p_obj.i_frame_sizeX, p_obj.i_frame_sizeY))
                                    {
                                        lg_shoted = true;
                                        p_obj.setState(MoveObject.STATE_KILLED, false);
                                    }
                            }
                            ;
                            break;
                    }
                    if (lg_shoted)
                    {
                        p_gsb.lg_iskilled = true;
                        p_gsb.i_hits++;

                        Score p_score = getInactiveScore();
                        i_activescores ++;
                        int i_scoretype = Score.SCORE_100;

                        if (p_obj.i_type == MoveObject.OBJECT_DUCK_FLYING_LEFT || p_obj.i_type == MoveObject.OBJECT_DUCK_FLYING_RIGHT)
                        {
                            switch (p_obj.i_distanceframe)
                            {
                                case 0:
                                    {
                                        i_scoretype = Score.SCORE_100;
                                        p_gsb.i_playerScore += 100;
                                    }
                                    ;
                                    break;
                                case 1:
                                    {
                                        i_scoretype = Score.SCORE_300;
                                        p_gsb.i_playerScore += 300;
                                    }
                                    ;
                                    break;
                                case 2:
                                    {
                                        i_scoretype = Score.SCORE_500;
                                        p_gsb.i_playerScore += 500;
                                    }
                                    ;
                                    break;
                            }
                        }
                        else
                        {
                            switch (p_obj.i_distanceframe)
                            {
                                case 0:
                                    i_scoretype = Score.SCORE_200;
                                    p_gsb.i_playerScore += 200;
                                    break;
                                case 1:
                                    i_scoretype = Score.SCORE_400;
                                    p_gsb.i_playerScore += 400;
                                    break;
                                case 2:
                                    i_scoretype = Score.SCORE_600;
                                    p_gsb.i_playerScore += 600;
                                    break;
                            }
                        }

                        if (p_score != null)
                        {
                            p_score.activation(p_obj.getScrX() + ((p_obj.i_frame_sizeX - p_score.ai_widths[2]) >> 1), p_obj.getScrY(), i_scoretype);
                        }
                        break;
                    }
                }
            }
        }
        if (lg_shoted)
        {
            if (p_gameActionListener != null) p_gameActionListener.gameAction(GAMEACTION_SOUND_HIT);
        }
    }


    private void processObjects()
    {
        int i_maxactiveindex = -1;

        MoveObject[] ap_moveobjects = p_gsb.ap_move_objects;

        int i8_currentduckspeed = p_gsb.i8_currentduckspeed;
        for (int li = 0; li < MAX_ANIMATION_OBJECTS; li++)
        {
            MoveObject p_obj = ap_moveobjects[li];
            int i_objdistance = p_obj.i8_distance;
            int i_type = p_obj.i_type;

            if (p_obj.lg_active)
            {
                if (p_obj.processAnimation() && i_type == MoveObject.OBJECT_BURST)
                {
                    p_obj.lg_active = false;
                    continue;
                }

                if (p_obj.i_state == MoveObject.STATE_KILLED)
                {
                    int i8_differ = (p_obj.getDuckSpeedConstant() * p_gsb.i8_currentduckspeed) >> 7;
                    int i8_y = p_obj.i8_y + i8_differ;
                    if (i8_y >= (ai_duck_killed_y[p_obj.i_distanceframe]))
                    {
                        p_obj.i8_y = ai_duck_killed_y[p_obj.i_distanceframe];
                        p_obj.convertDuckToBurst();
                    }
                    else
                        p_obj.i8_y = i8_y;
                }
                else
                    switch (i_type)
                    {
                        case MoveObject.OBJECT_BURST:
                        case MoveObject.OBJECT_CANE:
                            {
                                int i_newdistance = i_objdistance + ((i_objdistance * OBJECT_Z_COEFF_I8) >> 16) + CANE_SPEED_I8;
                                int i_x = p_obj.getScrX();
                                if (((i_newdistance >> 8) >= MAX_OBJECT_DISTANCE) || (i_x + p_obj.i_frame_sizeX < 0) || (i_x > i_screenWidth))
                                {
                                    p_obj.lg_active = false;
                                }
                                else
                                {
                                    p_obj.setDistance(i_newdistance);
                                    i_objdistance = p_obj.i8_distance;
                                    i_maxactiveindex = li;

                                    if (i_type == MoveObject.OBJECT_CANE)
                                        if (getInt(100) == 50 && (i_newdistance >> 8) > 20) generateDuckFromCane(p_obj);
                                }
                            }
                            ;
                            break;
                        case MoveObject.OBJECT_DUCK_FLYING_LEFT:
                            {
                                int i8_differ = (p_obj.getDuckSpeedConstant() * p_gsb.i8_currentduckspeed) >> 8;
                                int i8_x = p_obj.i8_x - i8_differ;
                                if ((i8_x >> 8) < (0 - p_obj.i_frame_sizeY))
                                {
                                    p_obj.lg_active = false;
                                }
                                else
                                {
                                    p_obj.i8_x = i8_x;
                                }
                            }
                            ;
                            break;
                        case MoveObject.OBJECT_DUCK_FLYING_RIGHT:
                            {
                                int i8_differ = (p_obj.getDuckSpeedConstant() * i8_currentduckspeed) >> 8;
                                int i8_x = p_obj.i8_x + i8_differ;
                                if ((i8_x >> 8) >= i_screenWidth)
                                {
                                    p_obj.lg_active = false;
                                }
                                else
                                {
                                    p_obj.i8_x = i8_x;
                                }
                            }
                            ;
                            break;
                        case MoveObject.OBJECT_DUCK_TAKEN_OFF_LEFT:
                            {
                                int i8_differ = (p_obj.getDuckSpeedConstant() * i8_currentduckspeed) >> 8;
                                int i8_x = p_obj.i8_x - i8_differ;
                                int i8_y = p_obj.i8_y - i8_differ;
                                if (((i8_y >> 8) + p_obj.i_frame_sizeY < 0) || ((i8_x >> 8) + p_obj.i_frame_sizeX < 0))
                                {
                                    p_obj.lg_active = false;
                                }
                                else
                                {
                                    p_obj.i8_x = i8_x;
                                    p_obj.i8_y = i8_y;
                                }
                            }
                            ;
                            break;
                        case MoveObject.OBJECT_DUCK_TAKEN_OFF_RIGHT:
                            {
                                int i8_differ = (p_obj.getDuckSpeedConstant() * i8_currentduckspeed) >> 8;
                                int i8_x = p_obj.i8_x + i8_differ;
                                int i8_y = p_obj.i8_y - i8_differ;
                                if (((i8_y >> 8) + p_obj.i_frame_sizeY < 0) || ((i8_x >> 8) >= i_screenWidth))
                                {
                                    p_obj.lg_active = false;
                                }
                                else
                                {
                                    p_obj.i8_x = i8_x;
                                    p_obj.i8_y = i8_y;
                                }
                            }
                            ;
                            break;
                    }
            }
        }
        i_maxindex = i_maxactiveindex;
    }

    private boolean isPlayerWon()
    {
        if (p_gsb.i_shots < 15) return false;
        if (p_gsb.i_hits >= (p_gsb.i_shots>>1)) return true; else return false;
    }

    public void nextGameStep()
    {
        p_gamePlayerBlock.getPlayerMoveRecord();
        int i_button = i_value;
        int i8_movespeed = GUN_MOVE_MINSPEED;
        if (i_movetick >= GUN_MOVE_SENSELEVEL_TICKS) i8_movespeed = GUN_MOVE_MAXSPEED;

        Gun p_gun = p_gsb.p_gun;
        int i8_x = p_gun.i8_sight_scrx;
        int i8_y = p_gun.i8_sight_scry;

        if (System.currentTimeMillis() > p_gsb.l_endtime)
        {
            if (isPlayerWon()) p_gsb.i_playerState = DuckHunt_GSB.PLAYERSTATE_WON; else p_gsb.i_playerState = DuckHunt_GSB.PLAYERSTATE_LOST;
            p_gsb.i_gameState = DuckHunt_GSB.GAMESTATE_OVER;
            return;
        }

        if (p_gun.i_bulletsingun == 0 && p_gun.i_state == Gun.STATE_READY)
        {
            if (p_gsb.i_bullets == 0)
            {
                if (isPlayerWon()) p_gsb.i_playerState = DuckHunt_GSB.PLAYERSTATE_WON; else p_gsb.i_playerState = DuckHunt_GSB.PLAYERSTATE_LOST;
                p_gsb.i_gameState = DuckHunt_GSB.GAMESTATE_OVER;
                return;
            }
            else
            {
                p_gun.setState(Gun.STATE_CHARGING);
                p_gsb.i_bullets -= 2;
            }
        }
        else
        if (p_gun.i_state == Gun.STATE_READY)
        {
            switch (i_button)
            {
                case BUTTON_NONE:
                    i_movetick = 0;
                    break;
                case BUTTON_DOWN:
                    {
                        i8_y += i8_movespeed;
                        if (((i8_y >> 8) + SIGHT_FRAME_HEIGHT) < i_screenHeight)
                        {
                            p_gun.setSightScrCoord(i8_x, i8_y);
                            i_movetick++;
                        }
                    }
                    ;
                    break;
                case BUTTON_UP:
                    {
                        i8_y -= i8_movespeed;
                        if ((i8_y >> 8) >= 0)
                        {
                            p_gun.setSightScrCoord(i8_x, i8_y);
                            i_movetick++;
                        }
                    }
                    ;
                    break;
                case BUTTON_LEFT:
                    {
                        i8_x -= i8_movespeed;
                        if ((i8_x >> 8) >= 0)
                        {
                            p_gun.setSightScrCoord(i8_x, i8_y);
                            i_movetick++;
                        }
                    }
                    ;
                    break;
                case BUTTON_RIGHT:
                    {
                        i8_x += i8_movespeed;
                        if (((i8_x >> 8) + SIGHT_FRAME_WIDTH) < i_screenWidth)
                        {
                            p_gun.setSightScrCoord(i8_x, i8_y);
                            i_movetick++;
                        }
                    }
                    ;
                    break;
                case BUTTON_FIRE:
                    {
                        i_movetick = 0;
                        if (p_gsb.p_gun.i_state == Gun.STATE_READY)
                        {
                            p_gsb.p_gun.setState(Gun.STATE_FIRE);
                            p_gsb.i_shots ++;
                            if (p_gameActionListener != null) p_gameActionListener.gameAction(GAMEACTION_SOUND_SHOOT);
                        }
                    }
                    ;
                    break;
            }
        }

        generateNewObject();
        processObjects();
        if (i_activescores!=0) processScore();

        if (p_gsb.p_gun.i_state == Gun.STATE_FIRE && !p_gsb.lg_iskilled) checkShot();
        boolean lg_isfiring = false;
        if (p_gun.i_state == Gun.STATE_FIRE) lg_isfiring = true;
        if (p_gsb.p_gun.processGun())
        {
            if (lg_isfiring && p_gun.i_state == Gun.STATE_READY)
            {
                if (!p_gsb.lg_iskilled)
                    if (p_gameActionListener!=null) p_gameActionListener.gameAction(GAMEACTION_SOUND_NOHIT);
            }
            p_gsb.lg_iskilled = false;
        }
    }

    public Score[] getScoreArray()
    {
        return ap_score;
    }

    public String getGameID()
    {
        return "DUCKHUNT";
    }

    public int getMaxSizeOfSavedGameBlock()
    {
        return 34 * MAX_ANIMATION_OBJECTS + 100;
    }

    public void pauseGame()
    {
        if (p_gsb!=null) p_gsb.i_pause_sec = (int)((p_gsb.l_endtime - System.currentTimeMillis())/1000);
    }

    public void resumeGame()
    {
        if (p_gsb!=null)
        {
            if (p_gsb.i_pause_sec>0) p_gsb.l_endtime = System.currentTimeMillis() + p_gsb.i_pause_sec*1000+500;
            p_gsb.i_pause_sec = 0;
        }
    }

    public static final int FLAG_EMPTY = 0x00;
    public static final int FLAG_SAVELOAD_SUPPORT = 0x01;
    public static final int FLAG_SCORES_SUPPORT = 0x02;
    public static final int FLAG_LEVELS_SUPPORT = 0x04;
    public static final int FLAG_STAGES_SUPPORT = 0x08;
    public DuckHunt_GSB p_gameStateBlock;
    public int i_gameFlags;
    public int i_screenWidth;
    public int i_screenHeight;
    public GameCanvas p_gamePlayerBlock;
    public GameCanvas p_gameActionListener;


    /**
     * Save current game state to an output stream
     * @param outputStream
     * @throws java.io.IOException
     */
    public void saveGameState(OutputStream outputStream) throws IOException
    {
        System.gc();
        DataOutputStream dataOutputStream = new DataOutputStream(outputStream);

        dataOutputStream.writeByte(p_gameStateBlock.i_gameLevel);
        dataOutputStream.writeByte(p_gameStateBlock.i_gameStage);
        dataOutputStream.writeInt(p_gameStateBlock.i_playerScore);
//        dataOutputStream.writeInt(p_gameStateBlock.i_aiScore);
        dataOutputStream.writeByte(p_gameStateBlock.i_playerState);
//        dataOutputStream.writeByte(p_gameStateBlock.i_aiState);
        dataOutputStream.flush();

        p_gameStateBlock.writeToStream(dataOutputStream);

        dataOutputStream = null;

        System.gc();
    }


    /**
     * Get current game state record
     * @return
     */
    public DuckHunt_GSB getGameStateBlock()
    {
        return p_gameStateBlock;
    }

    /**
     * Get a game flag
     * @param flag tested flag
     * @return a game flag as a boolean value... if it is true, flag is set else flag is clear
     */
    public boolean getGameFlag(int flag)
    {
        return (i_gameFlags & flag)!=0;
    }
}
