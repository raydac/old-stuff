package com.igormaznitsa.GameKit_3.OperationX;

import com.igormaznitsa.gameapi.Gamelet;
import com.igormaznitsa.gameapi.GameActionListener;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.DataInputStream;

public class GameletImpl extends Gamelet
{
    private static final int I8_MIN_SIGHT_SPEED = 0x500;
    private static final int I8_MAX_SIGHT_SPEED = 0x0A00;
    private static final int SIGHT_SPEED_DELAY = 5;

    public static final int LEVEL_0 = 0;
    private static final int TIMEDELAY_0 = 120;
    public static final int HIT_CHANCE_0 = 75;

    public static final int LEVEL_1 = 1;
    private static final int TIMEDELAY_1 = 100;
    public static final int HIT_CHANCE_1 = 65;

    public static final int LEVEL_2 = 2;
    private static final int TIMEDELAY_2 = 80;
    public static final int HIT_CHANCE_2 = 50;

    private static final int I_SIGHT_BORDER = 1;

    private static final int KILL_DISTANCE_STEP = 3;
    private int[] ai_line_y = new int[KILL_DISTANCE_STEP];

    public int i_TimeDelay;
    public static final int I8_SCROLL_SPEED = 0x100;

    private static final int NUMBER_PLAYER_FLASHES = 10;
    private static final int NUMBER_ANIMATION_OBJECTS = 20;
    private static final int NUMBER_ENEMYSHOOT_OBJECTS = 20;

    private static final int I8_FALLSPEED = 0x300;
    private static final int I8_PERSPECTIVE_COEFF = 0x010;

    public static final int I8_GROUND_Y_OFFSET = 0x2A00;
    private static final int I8_VERTCOPTER_LIM_Y = I8_GROUND_Y_OFFSET - 0x1000;

    public static final int I_PLAYER_MAX_HEALT = 100;

    public int i8_stageWayLength;
    public int i8_startViewX;

    private int i_hitChance;

    public int i_lastHit_x;
    public int i_lastHit_y;

    private static final int DELAY_OF_HIT_ON_SCREEN = 10;
    private int i_hitDelayOnScreen;

    public AnimeObject[] ap_PlayersFlashes;
    public AnimeObject[] ap_AnimationObjects;
    public AnimeObject[] ap_EnemyShootFlushObjects;

    public PlayerGun p_PlayerGun;
    private int[] ai_CurrentWay;
    private int i_PlayerFlashIndex;
    private int i_moveDelay;

    public int i_currentPlayerHealth;
    private int i8_screenWidth;

    public GameletImpl(int _screenWidth, int _screenHeight, GameActionListener _gameActionListener)
    {
        super(_screenWidth, _screenHeight, _gameActionListener);

        int i_step = (i_ScreenHeight - (I8_GROUND_Y_OFFSET >> 8) - 15) / KILL_DISTANCE_STEP;
        for (int li = 0; li < KILL_DISTANCE_STEP; li++)
        {
            ai_line_y[li] = I8_GROUND_Y_OFFSET + ((5 + li * i_step) << 8);
        }

        i8_screenWidth = _screenWidth << 8;
        ap_PlayersFlashes = new AnimeObject[NUMBER_PLAYER_FLASHES];
        ap_AnimationObjects = new AnimeObject[NUMBER_ANIMATION_OBJECTS];
        ap_EnemyShootFlushObjects = new AnimeObject[NUMBER_ENEMYSHOOT_OBJECTS];

        for (int li = 0; li < NUMBER_ANIMATION_OBJECTS; li++) ap_AnimationObjects[li] = new AnimeObject(li);
        for (int li = 0; li < NUMBER_ENEMYSHOOT_OBJECTS; li++) ap_EnemyShootFlushObjects[li] = new AnimeObject(li);
        for (int li = 0; li < NUMBER_PLAYER_FLASHES; li++) ap_PlayersFlashes[li] = new AnimeObject(li);

        p_PlayerGun = new PlayerGun(_screenWidth, _screenHeight);

        deactivateAllObjects();
    }

    public void initStage(int _stage)
    {
        super.initStage(_stage);
        deactivateAllObjects();
        i8_startViewX = 0;
        ai_CurrentWay = Stages.ai_Stages[_stage];
        i8_stageWayLength = (ai_CurrentWay.length * Stages.BLOCK_WIDTH) << 8;
        i_currentPlayerHealth = I_PLAYER_MAX_HEALT;
        i_lastHit_x = -1;
        i_lastHit_y = -1;
        i_hitDelayOnScreen = 0;
    }

    private void deactivateAllObjects()
    {
        for (int li = 0; li < ap_AnimationObjects.length; li++) ap_AnimationObjects[li].lg_Active = false;
        for (int li = 0; li < ap_EnemyShootFlushObjects.length; li++) ap_EnemyShootFlushObjects[li].lg_Active = false;
        for (int li = 0; li < ap_PlayersFlashes.length; li++) ap_PlayersFlashes[li].lg_Active = false;
        p_PlayerGun.setState(PlayerGun.STATE_CHARGING);
    }

    private AnimeObject getFirstFreeAnimationObject()
    {
        for (int li = 0; li < NUMBER_ANIMATION_OBJECTS; li++)
        {
            if (ap_AnimationObjects[li].lg_Active) continue;
            return ap_AnimationObjects[li];
        }
        return null;
    }

    private int getXCoeffForLine(int _i8_y, int _i8_speed)
    {
        if (_i8_y <= I8_GROUND_Y_OFFSET) return 1;
        _i8_y = _i8_y - I8_GROUND_Y_OFFSET;
        return _i8_speed + ((_i8_y * I8_PERSPECTIVE_COEFF) >> 8);
    }

    private AnimeObject getFirstFreeFlashShotObject()
    {
        for (int li = 0; li < NUMBER_ENEMYSHOOT_OBJECTS; li++)
        {
            if (ap_EnemyShootFlushObjects[li].lg_Active) continue;
            return ap_EnemyShootFlushObjects[li];
        }
        return null;
    }

    private void linkToAnimeObjectOtherObject(AnimeObject _object, AnimeObject _linkedobj)
    {
        int[] ai_offsets = AnimeObject.aai_OffsetToShootFire[_object.i_Type];

        int i8_x = ai_offsets[0];
        int i8_y = ai_offsets[1];

        _linkedobj.setCenterXY_8(_object.i8_centerX + i8_x, _object.i8_centerY + i8_y);
        _object.p_linkObject = _linkedobj;
        _linkedobj.lg_LinkedObject = true;
    }

    private void generateTroopers()
    {
        if (getRandomInt(100) == 50)
        {
            //// System.out.println("Generated trooper");
            AnimeObject p_obj = getFirstFreeAnimationObject();
            if (p_obj == null) return;

            int i_type = getRandomInt(29) / 10;
            int i_t = i_type == 0 ? 1 : i_type;
            int i8_y = ai_line_y[i_t];
            p_obj.i_line_index = i_t;

            switch (i_type)
            {
                case 0:
                    i_type = AnimeObject.OBJECT_TROOPER0;
                    break;
                case 1:
                    i_type = AnimeObject.OBJECT_TROOPER1;
                    break;
                case 2:
                    i_type = AnimeObject.OBJECT_TROOPER2;
                    break;
            }

            p_obj.initObject(i_type);

            int i_direct = AnimeObject.STATE_LEFT;
            if (getRandomInt(100) > 50) i_direct = AnimeObject.STATE_RIGHT;

            p_obj.initState(i_direct);
            int i8_x = 0;
            if (i_direct == AnimeObject.STATE_LEFT)
            {
                i8_x = i8_startViewX + (i_ScreenWidth << 8) + p_obj.i8_halfWidth;
            }
            else
            {
                i8_x = i8_startViewX - p_obj.i8_halfWidth;
            }

            p_obj.initState(i_direct);


            p_obj.setCenterXY_8(i8_x, i8_y);
        }
    }

    private void generateHorzCopter()
    {
        if (getRandomInt(100) == 50)
        {
            // System.out.println("Generated horzcopter");
            AnimeObject p_obj = getFirstFreeAnimationObject();
            if (p_obj == null) return;
            p_obj.initObject(AnimeObject.OBJECT_HELYCOPTERHORZ);

            AnimeObject p_pilot = getFirstFreeAnimationObject();
            if (p_pilot != null)
            {
                p_pilot.initObject(AnimeObject.OBJECT_PILOTGUNNER);
            }

            if (p_obj == null || p_pilot == null)
            {
                if (p_obj != null) p_obj.lg_Active = false;
                if (p_pilot != null) p_obj.lg_Active = false;
                return;
            }


            int i_direct = AnimeObject.STATE_LEFT;
            if (getRandomInt(100) > 50) i_direct = AnimeObject.STATE_RIGHT;

            p_obj.initState(i_direct);
            int i8_x = 0;
            if (i_direct == AnimeObject.STATE_LEFT)
            {
                i8_x = i8_startViewX + (i_ScreenWidth << 8) + p_obj.i8_halfWidth;
            }
            else
            {
                i8_x = i8_startViewX - p_obj.i8_halfWidth;
            }

            p_obj.initState(i_direct);
            p_pilot.initState(i_direct);

            int i8_y = p_obj.i8_Height;
            p_obj.i_line_index = 1;
            p_pilot.i_line_index = 1;

            p_obj.setCenterXY_8(i8_x, i8_y);
            p_pilot.setCenterXY_8(i8_x, i8_y);
            p_pilot.lg_LinkedObject = true;
            p_obj.p_linkObject = p_pilot;
        }
    }

    private void generateTank()
    {
        if (getRandomInt(150) == 75)
        {
            // System.out.println("Generated tank");
            AnimeObject p_obj = getFirstFreeAnimationObject();
            if (p_obj == null) return;
            p_obj.initObject(AnimeObject.OBJECT_TANK);

            int i_direct = AnimeObject.STATE_LEFT;
            if (getRandomInt(100) > 50) i_direct = AnimeObject.STATE_RIGHT;

            p_obj.initState(i_direct);
            int i8_x = 0;
            if (i_direct == AnimeObject.STATE_LEFT)
            {
                i8_x = i8_startViewX + (i_ScreenWidth << 8) + p_obj.i8_halfWidth;
            }
            else
            {
                i8_x = i8_startViewX - p_obj.i8_halfWidth;
                // System.out.println("tank to right");
            }

            p_obj.initState(i_direct);

            int i8_y = ai_line_y[0];
            p_obj.i_line_index = 0;

            p_obj.setCenterXY_8(i8_x, i8_y);
        }
    }

    private void generateVertCopter()
    {
        if (getRandomInt(150) == 75)
        {
            // System.out.println("Generated");
            AnimeObject p_obj = getFirstFreeAnimationObject();
            if (p_obj == null) return;
            p_obj.initObject(AnimeObject.OBJECT_HELYCOPTERVERT);
            p_obj.initState(AnimeObject.STATE_LEFT);
            int i8_x = i8_startViewX + ((getRandomInt(i_ScreenWidth / p_obj.i_Width) * p_obj.i_Width) << 8) + p_obj.i8_halfWidth;
            int i8_y = 0 - p_obj.i8_halfHeight;
            p_obj.setCenterXY_8(i8_x, i8_y);
        }
    }

    private boolean linkToAnimeObjectShotFlashes(AnimeObject _object)
    {
        int[] ai_offsets = AnimeObject.aai_OffsetToShootFire[_object.i_Type];
        int i_indx = 0;
        for (int li = 0; li < ai_offsets.length; li += 2)
        {
            AnimeObject p_obj = getFirstFreeFlashShotObject();

            int i8_x = ai_offsets[li];
            int i8_y = ai_offsets[li + 1];

            if (p_obj == null) return false;
            p_obj.initObject(AnimeObject.OBJECT_SHOOTFIRE);
            p_obj.setCenterXY_8(_object.i8_centerX + i8_x, _object.i8_centerY + i8_y);
            _object.ap_shotFireArray[i_indx++] = p_obj;
        }
        return true;
    }

    private void processShotFireArray(AnimeObject _obj, int _i8_newX, int _i8_newY)
    {
        int i8_dx = _i8_newX - _obj.i8_centerX;
        int i8_dy = _i8_newY - _obj.i8_centerY;


        int i_count = 0;
        if (_obj.ap_shotFireArray[0] != null)
        {
            if (_obj.ap_shotFireArray[0].lg_Active)
            {
                AnimeObject _ao = _obj.ap_shotFireArray[0];
                _ao.setCenterXY_8(i8_dx + _ao.i8_centerX, i8_dy + _ao.i8_centerY);
                i_count++;
            }
        }
        if (_obj.ap_shotFireArray[1] != null)
        {
            if (_obj.ap_shotFireArray[1].lg_Active)
            {
                i_count++;
                AnimeObject _ao = _obj.ap_shotFireArray[1];
                _ao.setCenterXY_8(i8_dx + _ao.i8_centerX, i8_dy + _ao.i8_centerY);
            }
        }

        // Calculating of hit
        boolean lg_htd = false;
        for (int li = 0; li < i_count; li++)
        {
            if (getRandomInt(i_hitChance) == (i_hitChance >> 1))
            {
                lg_htd = true;
                break;
            }
        }

        if (lg_htd)
        {
            i_lastHit_x = getRandomInt(i_ScreenWidth - 10) + 5;
            i_lastHit_y = getRandomInt(i_ScreenHeight - 10) + 5;
            i_hitDelayOnScreen = DELAY_OF_HIT_ON_SCREEN;
            i_currentPlayerHealth--;
            if (i_currentPlayerHealth < 0) i_currentPlayerHealth = 0;
        }

        // Moving of player's flashes
        for (int li = 0; li < NUMBER_PLAYER_FLASHES; li++)
        {
            AnimeObject p_obj = ap_PlayersFlashes[li];
            if (p_obj.lg_Active && p_obj.p_linkObject != null)
            {
                if (p_obj.p_linkObject.equals(_obj))
                {
                    p_obj.setCenterXY_8(i8_dx + p_obj.i8_centerX, i8_dy + p_obj.i8_centerY);
                }
            }
        }

        if (_obj.p_linkObject != null)
        {
            processShotFireArray(_obj.p_linkObject, _i8_newX, _i8_newY);
            _obj.p_linkObject.setCenterXY_8(_i8_newX, _i8_newY);
        }
    }

    private void processFireFlashes()
    {
        for (int li = 0; li < NUMBER_ENEMYSHOOT_OBJECTS; li++)
        {
            AnimeObject p_obj = ap_EnemyShootFlushObjects[li];
            if (p_obj.lg_Active) p_obj.processAnimation();
        }
    }

    private void deactivateLinkObjects(AnimeObject _obj, boolean _removelink)
    {
        if (_obj.p_linkObject != null && _removelink)
        {
            _obj.p_linkObject.lg_Active = false;
            _obj.p_linkObject.lg_LinkedObject = false;
            _obj.p_linkObject = null;
        }

        if (_obj.ap_shotFireArray[0] != null)
        {
            _obj.ap_shotFireArray[0].lg_Active = false;
            _obj.ap_shotFireArray[0] = null;
        }

        if (_obj.ap_shotFireArray[1] != null)
        {
            _obj.ap_shotFireArray[1].lg_Active = false;
            _obj.ap_shotFireArray[1] = null;
        }
    }

    private void processFiredObject(AnimeObject _firedobject)
    {
        if (_firedobject == null) return;
        if (_firedobject.i_State == AnimeObject.STATE_DEATH) return;

        if (--_firedobject.i_Energy <= 0)
        {
            _firedobject.i_Energy = 0;
            switch (_firedobject.i_Type)
            {
                case AnimeObject.OBJECT_PILOTGUNNER:
                    {
                        if (_firedobject.i_State != AnimeObject.STATE_HIDE)
                        {
                            deactivateLinkObjects(_firedobject, false);
                            i_PlayerScore += 5;
                            _firedobject.lg_LinkedObject = false;
                            _firedobject.initState(AnimeObject.STATE_HIDE);
                        }
                    }
                    ;
                    break;
                case AnimeObject.OBJECT_HELYCOPTERHORZ:
                case AnimeObject.OBJECT_HELYCOPTERVERT:
                case AnimeObject.OBJECT_TANK:
                case AnimeObject.OBJECT_TROOPER0:
                case AnimeObject.OBJECT_TROOPER1:
                case AnimeObject.OBJECT_TROOPER2:
                    {
                        deactivateLinkObjects(_firedobject, true);
                        _firedobject.initState(AnimeObject.STATE_DEATH);
                    }
                    ;
                    break;
            }
        }
    }

    private AnimeObject processAnimeObjects()
    {
        AnimeObject p_firedObject = null;

        boolean lg_player_fire = p_PlayerGun.i_state == PlayerGun.STATE_FIRE ? true : false;
        int i8_sightX = p_PlayerGun.i8_sight_scrx + i8_startViewX + (I_SIGHT_BORDER << 8);
        int i8_sightY = p_PlayerGun.i8_sight_scry + (I_SIGHT_BORDER << 8);
        int i8_sightWidth = (PlayerGun.SIGHT_FRAME_WIDTH - (I_SIGHT_BORDER << 1)) << 8;
        int i8_sightHeight = (PlayerGun.SIGHT_FRAME_HEIGHT - (I_SIGHT_BORDER << 1)) << 8;


        for (int li = 0; li < NUMBER_ANIMATION_OBJECTS; li++)
        {
            AnimeObject p_obj = ap_AnimationObjects[li];
            int i_state,i8_centerX = 0,i8_centerY = 0,i8_lineY;

            if (p_obj.lg_Active)
            {
                // Checking with the sight
                if (lg_player_fire)
                {
                    int i_oldy = -1;
                    if (p_firedObject != null)
                    {
                        i_oldy = p_firedObject.i_line_index;
                    }

                    if (p_obj.i_line_index > i_oldy)
                    {
                        boolean lg_linkedFired = false;
                        if (p_obj.p_linkObject != null)
                        {
                            AnimeObject p_lobj = p_obj.p_linkObject;
                            if (p_lobj.lg_LinkedObject)
                            {
                                if (!((p_lobj.i8_ScreenX + p_lobj.i8_Width <= i8_sightX) || (p_lobj.i8_ScreenY + p_lobj.i8_Height <= i8_sightY) || (p_lobj.i8_ScreenX >= i8_sightX + i8_sightWidth) || (p_lobj.i8_ScreenY >= i8_sightY + i8_sightHeight)))
                                {
                                    p_firedObject = p_lobj;
                                    lg_linkedFired = true;
                                }
                            }
                        }

                        if (!lg_linkedFired)
                            if (!((p_obj.i8_ScreenX + p_obj.i8_Width <= i8_sightX) || (p_obj.i8_ScreenY + p_obj.i8_Height <= i8_sightY) || (p_obj.i8_ScreenX >= i8_sightX + i8_sightWidth) || (p_obj.i8_ScreenY >= i8_sightY + i8_sightHeight))) p_firedObject = p_obj;
                    }
                }

                boolean lg_result = p_obj.processAnimation();

                i_state = p_obj.i_State;
                i8_centerX = p_obj.i8_centerX;
                i8_centerY = p_obj.i8_centerY;
                i8_lineY = ai_line_y[p_obj.i_line_index];
                int i8_width = p_obj.i8_Width;
                int i8_height = p_obj.i8_Height;
                int i8_speed = p_obj.i8_Speed;
                int i_screenX = (p_obj.i8_centerX - i8_startViewX) >> 8;

                boolean lg_fireEnable = true;
                if (i_screenX < 0 || i_screenX + p_obj.i_Width > i_ScreenWidth) lg_fireEnable = false;

                switch (p_obj.i_Type)
                {
                    case AnimeObject.OBJECT_AMMO:
                        {
                        }
                        ;
                        break;
                    case AnimeObject.OBJECT_FOUNTAIN:
                    case AnimeObject.OBJECT_EXPLOSION:
                        {
                            if (lg_result)
                                p_obj.lg_Active = false;
                            else
                                i8_centerX += I8_SCROLL_SPEED + getXCoeffForLine(i8_lineY, I8_SCROLL_SPEED);
                        }
                        ;
                        break;
                    case AnimeObject.OBJECT_HELYCOPTERHORZ:
                        {
                            if (p_obj.p_linkObject != null)
                            {
                                if (!p_obj.p_linkObject.lg_LinkedObject) p_obj.p_linkObject = null;
                            }

                            switch (i_state)
                            {
                                case AnimeObject.STATE_DEATH:
                                    {
                                        if (lg_result)
                                        {
                                            p_obj.initObject(AnimeObject.OBJECT_EXPLOSION);
                                            p_obj.setCenterXY_8(p_obj.i8_centerX, p_obj.i8_centerY);
                                        }
                                        else
                                        {
                                            i8_centerX += getXCoeffForLine(i8_lineY, I8_SCROLL_SPEED) + I8_SCROLL_SPEED;
                                        }
                                    }
                                    ;
                                    break;
                                case AnimeObject.STATE_FIRE:
                                    {
                                        i8_centerX += I8_SCROLL_SPEED + getXCoeffForLine(i8_lineY, I8_SCROLL_SPEED);
                                        if (getRandomInt(20) == 10 || p_obj.p_linkObject == null)
                                        {
                                            if (getRandomInt(100) > 50)
                                            {
                                                p_obj.initState(AnimeObject.STATE_LEFT);
                                                if (p_obj.p_linkObject != null)
                                                {
                                                    p_obj.p_linkObject.initState(AnimeObject.STATE_LEFT);
                                                    deactivateLinkObjects(p_obj.p_linkObject, false);
                                                }
                                            }
                                            else
                                            {
                                                p_obj.initState(AnimeObject.STATE_RIGHT);
                                                if (p_obj.p_linkObject != null)
                                                {
                                                    p_obj.p_linkObject.initState(AnimeObject.STATE_RIGHT);
                                                    deactivateLinkObjects(p_obj.p_linkObject, false);
                                                }
                                            }
                                        }
                                    }
                                    ;
                                    break;
                                default:
                                    {
                                        if (p_obj.i_ObjectDirection == AnimeObject.STATE_LEFT)
                                        {
                                            i8_centerX -= 0;//getXCoeffForLine(i8_centerY,p_obj.i8_Speed);
                                            if (i8_centerX + i8_width < i8_startViewX)
                                            {
                                                deactivateLinkObjects(p_obj, true);
                                                p_obj.lg_Active = false;
                                                continue;
                                            }
                                        }
                                        else
                                        {
                                            i8_centerX += +(I8_SCROLL_SPEED << 1) + getXCoeffForLine(i8_lineY, p_obj.i8_Speed);

                                            if (i8_centerX - i8_width > (i8_startViewX + (i_ScreenWidth << 8)))
                                            {
                                                deactivateLinkObjects(p_obj, true);
                                                p_obj.lg_Active = false;
                                                continue;
                                            }
                                        }

                                        if (i8_centerX > i8_startViewX + i8_width && i8_centerX + i8_width < (i8_startViewX + (i_ScreenWidth << 8)) && p_obj.p_linkObject != null)
                                        {
                                            if (getRandomInt(10) == 5 && p_obj.p_linkObject != null)
                                            {
                                                p_obj.initState(AnimeObject.STATE_FIRE);
                                                p_obj.p_linkObject.initState(AnimeObject.STATE_FIRE);

                                                linkToAnimeObjectShotFlashes(p_obj.p_linkObject);
                                            }
                                        }
                                    }
                            }
                        }
                        ;
                        break;
                    case AnimeObject.OBJECT_HELYCOPTERVERT:
                        {
                            i8_centerX += getXCoeffForLine(i8_lineY, I8_SCROLL_SPEED) + I8_SCROLL_SPEED;
                            switch (i_state)
                            {
                                case AnimeObject.STATE_FIRE:
                                    {

                                        if (getRandomInt(50) == 20)
                                        {
                                            deactivateLinkObjects(p_obj, true);
                                            p_obj.initState(AnimeObject.STATE_RIGHT);
                                        }

                                    }
                                    ;
                                    break;
                                case AnimeObject.STATE_LEFT:
                                    {
                                        i8_centerY += i8_speed;
                                        if (i8_centerY >= I8_VERTCOPTER_LIM_Y)
                                        {
                                            i8_centerY = I8_VERTCOPTER_LIM_Y;
                                            p_obj.initState(AnimeObject.STATE_FIRE);
                                            this.linkToAnimeObjectShotFlashes(p_obj);
                                        }
                                    }
                                    ;
                                    break;
                                case AnimeObject.STATE_RIGHT:
                                    {
                                        i8_centerY -= i8_speed;
                                        if ((i8_centerY + i8_height) < 0) p_obj.lg_Active = false;
                                    }
                                    ;
                                    break;
                                case AnimeObject.STATE_DEATH:
                                    {
                                        if (lg_result)
                                        {
                                            p_obj.initObject(AnimeObject.OBJECT_EXPLOSION);
                                            p_obj.setCenterXY_8(p_obj.i8_centerX, p_obj.i8_centerY);
                                        }
                                    }
                                    ;
                                    break;
                                case AnimeObject.STATE_HIDE:
                                    {
                                        i8_centerY += I8_FALLSPEED;
                                        if (i8_centerY > i8_lineY)
                                        {
                                            i8_centerY = i8_lineY;
                                            p_obj.initState(AnimeObject.STATE_DEATH);
                                        }
                                    }
                                    ;
                                    break;
                            }
                        }
                        ;
                        break;
                    case AnimeObject.OBJECT_PILOTGUNNER:
                        {
                            switch (i_state)
                            {
                                case AnimeObject.STATE_DEATH:
                                    {
                                        if (lg_result)
                                        {
                                            p_obj.lg_Active = false;
                                        }
                                    }
                                    ;
                                    break;
                                case AnimeObject.STATE_HIDE:
                                    {
                                        i8_centerX += I8_SCROLL_SPEED + getXCoeffForLine(i8_lineY, I8_SCROLL_SPEED);
                                        i8_centerY += i8_speed;
                                        if (i8_centerY > i8_lineY)
                                        {
                                            i8_centerY = i8_lineY;
                                            p_obj.initState(AnimeObject.STATE_DEATH);
                                        }
                                    }
                                    ;
                                    break;
                            }
                        }
                        ;
                        break;
                    case AnimeObject.OBJECT_TANK:
                        {
                            switch (i_state)
                            {
                                case AnimeObject.STATE_DEATH:
                                    {
                                        if (lg_result)
                                        {
                                            p_obj.initObject(AnimeObject.OBJECT_EXPLOSION);
                                            p_obj.setCenterXY_8(p_obj.i8_centerX, p_obj.i8_centerY);
                                        }
                                    }
                                    ;
                                    break;
                                case AnimeObject.STATE_FIRE:
                                    {
                                        i8_centerX += I8_SCROLL_SPEED + getXCoeffForLine(i8_lineY, I8_SCROLL_SPEED);
                                        if (getRandomInt(30) == 20)
                                        {
                                            if (getRandomInt(100) > 50)
                                                p_obj.initState(AnimeObject.STATE_LEFT);
                                            else
                                                p_obj.initState(AnimeObject.STATE_RIGHT);
                                            deactivateLinkObjects(p_obj, true);
                                        }
                                    }
                                    ;
                                    break;
                                default:
                                    {
                                        if (p_obj.i_ObjectDirection == AnimeObject.STATE_LEFT)
                                        {
                                            i8_centerX -= 0;//getXCoeffForLine(i8_centerY,p_obj.i8_Speed);
                                            if (i8_centerX + i8_width < i8_startViewX)
                                            {
                                                deactivateLinkObjects(p_obj, true);
                                                p_obj.lg_Active = false;
                                                continue;
                                            }
                                        }
                                        else
                                        {
                                            i8_centerX += +(I8_SCROLL_SPEED << 1) + getXCoeffForLine(i8_lineY, p_obj.i8_Speed);

                                            if (i8_centerX - i8_width > (i8_startViewX + (i_ScreenWidth << 8)))
                                            {
                                                deactivateLinkObjects(p_obj, true);
                                                p_obj.lg_Active = false;
                                                continue;
                                            }
                                        }

                                        if (lg_fireEnable)
                                        {
                                            if (getRandomInt(20) == 10)
                                            {
                                                p_obj.initState(AnimeObject.STATE_FIRE);
                                                linkToAnimeObjectShotFlashes(p_obj);
                                            }
                                        }
                                    }
                            }
                        }
                        ;
                        break;
                    case AnimeObject.OBJECT_TROOPER1:
                    case AnimeObject.OBJECT_TROOPER2:
                    case AnimeObject.OBJECT_TROOPER0:
                        {
                            switch (i_state)
                            {
                                case AnimeObject.STATE_DEATH:
                                    {
                                        if (lg_result)
                                        {
                                            deactivateLinkObjects(p_obj, true);
                                            p_obj.lg_Active = false;
                                        }
                                    }
                                    ;
                                    break;
                                case AnimeObject.STATE_FIRE:
                                    {
                                        i8_centerX += getXCoeffForLine(i8_lineY, I8_SCROLL_SPEED);
                                        if (getRandomInt(100) == 50)
                                        {
                                            if (getRandomInt(100) > 50)
                                                p_obj.initState(AnimeObject.STATE_LEFT);
                                            else
                                                p_obj.initState(AnimeObject.STATE_RIGHT);
                                            deactivateLinkObjects(p_obj, true);
                                        }
                                    }
                                    ;
                                    break;
                                default:
                                    {
                                        if (p_obj.i_ObjectDirection == AnimeObject.STATE_LEFT)
                                        {
                                            i8_centerX -= getXCoeffForLine(i8_centerY, p_obj.i8_Speed);
                                            if (i8_centerX + i8_width < i8_startViewX)
                                            {
                                                deactivateLinkObjects(p_obj, true);
                                                p_obj.lg_Active = false;

                                                // System.out.println("Left removed");

                                                continue;
                                            }
                                        }
                                        else
                                        {
                                            i8_centerX += (I8_SCROLL_SPEED << 1) + getXCoeffForLine(i8_lineY, p_obj.i8_Speed);

                                            if (i8_centerX - i8_width > (i8_startViewX + (i_ScreenWidth << 8)))
                                            {
                                                deactivateLinkObjects(p_obj, true);
                                                p_obj.lg_Active = false;

                                                // System.out.println("Right removed");
                                                continue;
                                            }
                                        }

                                        if (lg_fireEnable)
                                        {
                                            if (getRandomInt(10) == 5)
                                            {
                                                p_obj.initState(AnimeObject.STATE_FIRE);
                                                linkToAnimeObjectShotFlashes(p_obj);
                                            }
                                        }
                                    }
                            }
                        }
                        ;
                        break;
                }
                if (p_obj.lg_Active)
                {
                    i8_centerX -= getXCoeffForLine(i8_lineY, I8_SCROLL_SPEED);
                }
            }

            if (p_obj.lg_Active)
            {
                if (!p_obj.lg_LinkedObject)
                {
                    processShotFireArray(p_obj, i8_centerX, i8_centerY);
                    p_obj.setCenterXY_8(i8_centerX, i8_centerY);
                }
            }
        }

        if (p_firedObject != null)
        {
            if (!p_firedObject.lg_Active)
                p_firedObject = null;
            else if (p_firedObject.i_Type == AnimeObject.OBJECT_EXPLOSION) p_firedObject = null;
        }

        return p_firedObject;
    }

    private void processPlayerFlashes()
    {
        for (int li = 0; li < NUMBER_PLAYER_FLASHES; li++)
        {
            AnimeObject p_obj = ap_PlayersFlashes[li];
            if (p_obj.lg_Active)
            {
                if (p_obj.processAnimation())
                {
                    p_obj.lg_Active = false;
                    continue;
                }

                if (p_obj.p_linkObject == null)
                {
                    p_obj.i8_centerX -= getXCoeffForLine(p_obj.i8_centerY, p_obj.i8_Speed);
                    p_obj.updateXY();
                }
                else
                {
                    if (!p_obj.p_linkObject.lg_Active)
                    {
                        p_obj.lg_Active = false;
                        continue;
                    }
                    else
                    {
                        p_obj.i8_centerX += getXCoeffForLine(p_obj.i8_centerY, p_obj.i8_Speed);
                        p_obj.updateXY();
                    }
                }
            }
        }
    }

    private void addPlayerFlashes(AnimeObject _firedObject, int _i8_x, int _i8_y)
    {
        if (_i8_y < I8_GROUND_Y_OFFSET && _firedObject == null) return;

        AnimeObject p_obj = ap_PlayersFlashes[i_PlayerFlashIndex++];

        p_obj.initObject(AnimeObject.OBJECT_PLAYERSHOOT);
        p_obj.setCenterXY_8(_i8_x + i8_startViewX, _i8_y);
        p_obj.p_linkObject = _firedObject;
        p_obj.lg_Active = true;

        if (i_PlayerFlashIndex == NUMBER_PLAYER_FLASHES) i_PlayerFlashIndex = 0;
    }

    private void initGun()
    {
        p_PlayerGun.sightToCenter();
        p_PlayerGun.setState(PlayerGun.STATE_CHARGING);
    }

    public void newGameSession(int _level)
    {
        initLevel(_level);

        switch (_level)
        {
            case LEVEL_0:
                {
                    i_TimeDelay = TIMEDELAY_0;
                    i_hitChance = HIT_CHANCE_0;
                }
                ;
                break;
            case LEVEL_1:
                {
                    i_TimeDelay = TIMEDELAY_1;
                    i_hitChance = HIT_CHANCE_1;
                }
                ;
                break;
            case LEVEL_2:
                {
                    i_TimeDelay = TIMEDELAY_2;
                    i_hitChance = HIT_CHANCE_2;
                }
                ;
                break;
        }
        initGun();
    }

    private void processSight(int _button, AnimeObject _firedobj)
    {
        if (p_PlayerGun.i_state == PlayerGun.STATE_CHARGING) return;

        int i8_screenX = p_PlayerGun.i8_sight_scrx;
        int i8_screenY = p_PlayerGun.i8_sight_scry;

        int i8_movespeed = I8_MIN_SIGHT_SPEED;

        if (_button == PlayerMoveObject.BUTTON_NONE || _button == PlayerMoveObject.BUTTON_FIRE)
        {
            i_moveDelay = SIGHT_SPEED_DELAY;
        }
        else
        {
            i_moveDelay--;
            if (i_moveDelay <= 0)
            {
                i_moveDelay = 0;
                i8_movespeed = I8_MAX_SIGHT_SPEED;
            }
        }

        switch (_button)
        {
            case PlayerMoveObject.BUTTON_RECHARGE:
                {
                    p_PlayerGun.setState(PlayerGun.STATE_CHARGING);
                }
                ;
                break;
            case PlayerMoveObject.BUTTON_NONE:
                {

                }
                ;
                break;
            case PlayerMoveObject.BUTTON_LEFT:
                {

                    i8_screenX -= i8_movespeed;
                }
                ;
                break;
            case PlayerMoveObject.BUTTON_RIGHT:
                {
                    i8_screenX += i8_movespeed;
                }
                ;
                break;
            case PlayerMoveObject.BUTTON_UP:
                {
                    i8_screenY -= i8_movespeed;
                }
                ;
                break;
            case PlayerMoveObject.BUTTON_DOWN:
                {
                    i8_screenY += i8_movespeed;
                }
                ;
                break;
            case PlayerMoveObject.BUTTON_FIRE:
                {
                    if (p_PlayerGun.i_bulletsingun > 0 && p_PlayerGun.i_state != PlayerGun.STATE_CHARGING)
                    {
                        addPlayerFlashes(_firedobj, i8_screenX + (PlayerGun.SIGHT_FRAME_WIDTH >> 1), i8_screenY + (PlayerGun.SIGHT_FRAME_HEIGHT >> 1));
                        int i_dx = (getRandomInt(4) - 2) << 8;
                        int i_dy = (getRandomInt(4) - 2) << 8;
                        i8_screenX += i_dx;
                        i8_screenY += i_dy;
                        if (p_PlayerGun.i_state == PlayerGun.STATE_READY) p_PlayerGun.setState(PlayerGun.STATE_FIRE);
                        p_PlayerGun.i_bulletsingun--;
                    }
                }
                ;
                break;
        }

        if (i8_screenX < 0) i8_screenX = 0;
        if (i8_screenY < 0) i8_screenY = 0;

        int i8_rghtX = i8_screenX + (PlayerGun.SIGHT_FRAME_WIDTH << 8);
        int i8_dwnY = i8_screenY + (PlayerGun.SIGHT_FRAME_HEIGHT << 8);

        if (i8_rghtX >= (i_ScreenWidth << 8)) i8_screenX = (i_ScreenWidth << 8) - (PlayerGun.SIGHT_FRAME_WIDTH << 8);
        if (i8_dwnY >= (i_ScreenHeight << 8)) i8_screenY = (i_ScreenHeight << 8) - (PlayerGun.SIGHT_FRAME_HEIGHT << 8);

        p_PlayerGun.setSightScrCoord(i8_screenX, i8_screenY);
    }

    public void nextGameStep(Object _playermoveobject)
    {
        if (i_lastHit_x >= 0)
        {
            i_hitDelayOnScreen--;
            if (i_hitDelayOnScreen == 0)
            {
                i_lastHit_x = -1;
                i_lastHit_y = -1;
            }
        }

        p_PlayerGun.processGun();

        i8_startViewX += I8_SCROLL_SPEED;

        PlayerMoveObject p_mobj = (PlayerMoveObject) _playermoveobject;


        if (p_PlayerGun.i_bulletsingun == 0 && p_PlayerGun.i_state == PlayerGun.STATE_READY)
        {
            p_PlayerGun.setState(PlayerGun.STATE_CHARGING);
        }

        AnimeObject p_fired = processAnimeObjects();
        processSight(p_mobj.i_Button, p_fired);
        processFiredObject(p_fired);

        processFireFlashes();
        processPlayerFlashes();
        generateVertCopter();
        generateTank();
        generateTroopers();
        generateHorzCopter();

        if (i_currentPlayerHealth == 0)
        {
            i_GameState = GAMESTATE_OVER;
            i_PlayerState = PLAYERSTATE_LOST;
        }
        else if (i8_startViewX + i8_screenWidth >= i8_stageWayLength)
        {
            i_GameState = GAMESTATE_OVER;
            i_PlayerState = PLAYERSTATE_WON;
        }
    }

    public int getBlockNumberForXPosition(int _x)
    {
        int i_block = _x / Stages.BLOCK_WIDTH;
        return ai_CurrentWay[i_block];
    }

    public int getScreenXForBlockPosition(int _x)
    {
/*
        int i_blocknum = _x / Stages.BLOCK_WIDTH;
        return (i_blocknum * Stages.BLOCK_WIDTH) - i8_startViewX;
*/
        return _x - _x % Stages.BLOCK_WIDTH - (i8_startViewX >> 8);
    }

    public String getGameID()
    {
        return "OPerX";
    }

    public int getMaxSizeOfSavedGameBlock()
    {
        return 1024;
    }

    public void writeToStream(DataOutputStream _dataOutputStream) throws IOException
    {
        _dataOutputStream.writeInt(i8_startViewX);
        _dataOutputStream.writeInt(i_PlayerFlashIndex);
        _dataOutputStream.writeInt(i_moveDelay);

        for (int li = 0; li < ap_AnimationObjects.length; li++)
        {
            AnimeObject p_obj = ap_AnimationObjects[li];

            _dataOutputStream.writeBoolean(p_obj.lg_Active);
            _dataOutputStream.writeByte(p_obj.i_Type);
            _dataOutputStream.writeByte(p_obj.i_State);
            _dataOutputStream.writeInt(p_obj.i8_Speed);

            _dataOutputStream.writeInt(p_obj.i8_centerX);
            _dataOutputStream.writeInt(p_obj.i8_centerY);

            _dataOutputStream.writeByte(p_obj.i_Frame);
            _dataOutputStream.writeByte(p_obj.i_Energy);

            _dataOutputStream.writeBoolean(p_obj.lg_back);
            _dataOutputStream.writeByte(p_obj.i_delay);

            _dataOutputStream.writeByte(p_obj.i_ObjectDirection);

            _dataOutputStream.writeBoolean(p_obj.lg_LinkedObject);
            _dataOutputStream.writeInt(p_obj.i8_HorzSpeed);
            _dataOutputStream.writeByte(p_obj.i_line_index);

            if (p_obj.p_linkObject == null)
                _dataOutputStream.writeShort(-1);
            else
                _dataOutputStream.writeShort(p_obj.p_linkObject.i_indexArray);

            // The array of shot flashes
            if (p_obj.ap_shotFireArray[0] == null)
                _dataOutputStream.writeShort(-1);
            else
                _dataOutputStream.writeShort(p_obj.ap_shotFireArray[0].i_indexArray);

            if (p_obj.ap_shotFireArray[1] == null)
                _dataOutputStream.writeShort(-1);
            else
                _dataOutputStream.writeShort(p_obj.ap_shotFireArray[0].i_indexArray);
        }

        for (int li = 0; li < ap_EnemyShootFlushObjects.length; li++)
        {
            AnimeObject p_obj = ap_EnemyShootFlushObjects[li];

            _dataOutputStream.writeBoolean(p_obj.lg_Active);
            _dataOutputStream.writeByte(p_obj.i_Type);
            _dataOutputStream.writeByte(p_obj.i_State);
            _dataOutputStream.writeInt(p_obj.i8_Speed);

            _dataOutputStream.writeInt(p_obj.i8_centerX);
            _dataOutputStream.writeInt(p_obj.i8_centerY);

            _dataOutputStream.writeByte(p_obj.i_Frame);
            _dataOutputStream.writeByte(p_obj.i_delay);
        }

        // Writing Gun data
        _dataOutputStream.writeByte(p_PlayerGun.i_state);
        _dataOutputStream.writeByte(p_PlayerGun.i_frame);
        _dataOutputStream.writeByte(p_PlayerGun.i_tick);

        _dataOutputStream.writeInt(p_PlayerGun.i8_sight_scrx);
        _dataOutputStream.writeInt(p_PlayerGun.i8_sight_scry);

        _dataOutputStream.writeShort(p_PlayerGun.i_gun_scrx);
        _dataOutputStream.writeShort(p_PlayerGun.i_gun_scry);

        _dataOutputStream.writeBoolean(p_PlayerGun.lg_backmove);
        _dataOutputStream.writeByte(p_PlayerGun.i_firePhase);

        _dataOutputStream.writeByte(p_PlayerGun.i_bulletsingun);
        _dataOutputStream.writeBoolean(p_PlayerGun.lg_sightvisibled);

        _dataOutputStream.writeShort(p_PlayerGun.i_firingx);
        _dataOutputStream.writeShort(p_PlayerGun.i_firingy);
    }

    public void readFromStream(DataInputStream _dataInputStream) throws IOException
    {
        i8_startViewX = _dataInputStream.readInt();
        i_PlayerFlashIndex = _dataInputStream.readInt();
        i_moveDelay = _dataInputStream.readInt();

        for (int li = 0; li < ap_AnimationObjects.length; li++)
        {
            AnimeObject p_obj = ap_AnimationObjects[li];

            boolean lg_active = _dataInputStream.readBoolean();

            int i_Type = _dataInputStream.readByte();
            int i_State = _dataInputStream.readByte();

            if (lg_active)
            {
                p_obj.initObject(i_Type);
                p_obj.initState(i_State);
            }

            p_obj.i8_Speed = _dataInputStream.readInt();
            p_obj.i8_centerX = _dataInputStream.readInt();
            p_obj.i8_centerY = _dataInputStream.readInt();

            p_obj.i_Frame = _dataInputStream.readByte();
            p_obj.i_Energy = _dataInputStream.readByte();

            p_obj.lg_back = _dataInputStream.readBoolean();
            p_obj.i_delay = _dataInputStream.readByte();

            p_obj.setObjectDirection(_dataInputStream.readByte());

            p_obj.lg_LinkedObject = _dataInputStream.readBoolean();
            p_obj.i8_HorzSpeed = _dataInputStream.readInt();
            p_obj.i_line_index = _dataInputStream.readByte();

            int i_linkObjindx = _dataInputStream.readShort();

            if (i_linkObjindx < 0)
                p_obj.p_linkObject = null;
            else
                p_obj.p_linkObject = ap_AnimationObjects[i_linkObjindx];

            // The array of shot flashes
            i_linkObjindx = _dataInputStream.readShort();
            if (i_linkObjindx < 0)
                p_obj.ap_shotFireArray[0] = null;
            else
                p_obj.ap_shotFireArray[0] = ap_EnemyShootFlushObjects[i_linkObjindx];

            i_linkObjindx = _dataInputStream.readShort();
            if (i_linkObjindx < 0)
                p_obj.ap_shotFireArray[1] = null;
            else
                p_obj.ap_shotFireArray[1] = ap_EnemyShootFlushObjects[i_linkObjindx];
        }

        for (int li = 0; li < ap_EnemyShootFlushObjects.length; li++)
        {
            AnimeObject p_obj = ap_EnemyShootFlushObjects[li];

            boolean lg_active = _dataInputStream.readBoolean();
            int i_type = _dataInputStream.readByte();
            int i_state = _dataInputStream.readByte();

            if (lg_active)
            {
                p_obj.initObject(i_type);
                p_obj.initState(i_state);
            }

            p_obj.i8_Speed = _dataInputStream.readInt();

            p_obj.i8_centerX = _dataInputStream.readInt();
            p_obj.i8_centerY = _dataInputStream.readInt();

            p_obj.i_Frame = _dataInputStream.readByte();
            p_obj.i_delay = _dataInputStream.readByte();
        }

        // Writing Gun data
        p_PlayerGun.i_state = _dataInputStream.readByte();
        p_PlayerGun.i_frame = _dataInputStream.readByte();
        p_PlayerGun.i_tick = _dataInputStream.readByte();

        p_PlayerGun.i8_sight_scrx = _dataInputStream.readInt();
        p_PlayerGun.i8_sight_scry = _dataInputStream.readInt();

        p_PlayerGun.i_gun_scrx = _dataInputStream.readShort();
        p_PlayerGun.i_gun_scry = _dataInputStream.readShort();

        p_PlayerGun.lg_backmove = _dataInputStream.readBoolean();
        p_PlayerGun.i_firePhase = _dataInputStream.readByte();

        p_PlayerGun.i_bulletsingun = _dataInputStream.readByte();
        p_PlayerGun.lg_sightvisibled = _dataInputStream.readBoolean();

        p_PlayerGun.i_firingx = _dataInputStream.readShort();
        p_PlayerGun.i_firingy = _dataInputStream.readShort();
    }
}
