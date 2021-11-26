
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.DataInputStream;

public class MobileGameletImpl extends MobileGamelet
{

    private static final int ACTION_DELAY = 15;
    private static final int DEAD_COUNT = 3;

    protected static final int ADDITIONAL_BANDITS = 2;
    protected static final int SHERIFF_BULLETS = 12;

    private static final int MOVE_SPEED = 0x500;


    private static final int MOVE_SPACE = 0x0400;

    private static final int LEFT_BORDER   = (0 + MOVE_SPACE);

    private static final int RIGHT_BORDER  = ((128<<8) - MOVE_SPACE);

    private static final int TOP_BORDER    = (0 + MOVE_SPACE);

    private static final int BOTTOM_BORDER = ((100<<8) - MOVE_SPACE);


    private static final int LEVEL_EASY_TIMEDELAY = 150;

    private static final int LEVEL_EASY_LIVES = 4;

    private static final int LEVEL_EASY_ENEMYFREQ = 10;


    protected static final int LEVEL_EASY_BANDITS = 2;

    private static final int LEVEL_NORMAL_TIMEDELAY = 110;

    private static final int LEVEL_NORMAL_LIVES = 3;

    private static final int LEVEL_NORMAL_ENEMYFREQ = 8;

    protected static final int LEVEL_NORMAL_BANDITS = 3;

    private static final int LEVEL_HARD_TIMEDELAY = 80;

    private static final int LEVEL_HARD_LIVES = 2;

    private static final int LEVEL_HARD_ENEMYFREQ = 6;

    protected static final int LEVEL_HARD_BANDITS = 4;

    private static final int SCORES_BANDIT = 10;
    private static final int SCORES_WITHOUT_MISSING = 50;

    private static final int SPRITEDATALENGTH = 8;

    public static final int SPRITE_SHERIFF_SIGHT                = 0;
    public static final int SPRITE_SHERIFF_FIRE                 = 1;
    public static final int SPRITE_SHERIFF_DEAD                 = 2;

    public static final int SPRITE_MAN1                         = 3;
    public static final int SPRITE_MAN1_FIRE                    = 4;
    public static final int SPRITE_MAN1_DEAD                    = 5;
    public static final int SPRITE_MAN2                         = 6;
    public static final int SPRITE_MAN2_FIRE                    = 7;
    public static final int SPRITE_MAN2_DEAD                    = 8;
    public static final int SPRITE_MAN3                         = 9;
    public static final int SPRITE_MAN3_FIRE                    = 10;
    public static final int SPRITE_MAN3_DEAD                    = 11;
    public static final int SPRITE_MAN4                         = 12;
    public static final int SPRITE_MAN4_FIRE                    = 13;
    public static final int SPRITE_MAN4_DEAD                    = 14;
    public static final int SPRITE_MAN5                         = 15;
    public static final int SPRITE_MAN5_FIRE                    = 16;
    public static final int SPRITE_MAN5_DEAD                    = 17;

    public static final int MAX_SPRITES = 11;

    private static final int MAX_PATHS = 11;

    private static final int PATH_MAN_11 = 0;
    private static final int PATH_MAN_1 = 13;
    private static final int PATH_MAN_2 = 26;
    private static final int PATH_MAN_3 = 39;
    private static final int PATH_MAN_4 = 52;
    private static final int PATH_MAN_9 = 65;
    private static final int PATH_MAN_5 = 78;
    private static final int PATH_MAN_6 = 91;
    private static final int PATH_MAN_7 = 104;
    private static final int PATH_MAN_8 = 117;
    private static final int PATH_MAN_10 = 130;

    public static final int GAMEACTION_SHERIFF_FIRE             = 0;
    public static final int GAMEACTION_SHERIFF_DEAD             = 1;
    public static final int GAMEACTION_NO_BULLETS               = 2;
    public static final int GAMEACTION_BANDIT_FIRE              = 3;
    public static final int GAMEACTION_BANDIT_DEAD              = 4;
    public static final int GAMEACTION_BANDIT_HIDE              = 5;
    public static final int GAMEACTION_KILL_MAN                 = 6;
    public static final int GAMEACTION_NEW_MAN                  = 7;
    public static final int GAMEACTION_NEW_BANDIT               = 8;
    public static final int GAMEACTION_CONGRATULATION           = 9;



    public int m_iPlayerAttemptions;

    private int i_timedelay;

    public int i_GameTimer;

    private int i_borndelay;
    private int i_lasttime;

    private int i_rndfreq;

    public boolean b_MistakeKill;

    public int i_Bandits;

    public int i_BanditsOnScreen;

    public int i_BanditsBand;

    public int i_SheriffBullets;


    public Sprite pPlayerSight;

    public Sprite[] ap_Sprites;

    private PathController[] ap_Paths;

    public MoveObject p_MoveObject;

    private int i8_gameScreenWidth;

    private int i8_gameScreenHeight;

    private static final short[] ash_Paths = new short[]
    {
        3, 0, 112,66,10,   106,66,5,     100,66,15,  112,66,

        3, 0,  10,42,10,    10,36,5,     10,30,15,   10,42,
        3, 0,  34,42,10,    34,36,5,     34,30,15,   34,42,
        3, 0, -16,65,10,    12,65,5,     12,65,15,  -16,65,
        3, 0,  34,70,10,    34,64,5,     34,59,15,   34,70,

        3, 0, 100,64,10,    95,64,5,     80,64,10,   40,64,

        3, 0,  86,20,10,    86,15,5,     86,10,15,   86,20,
        3, 0, 108,20,10,   108,15,5,    108,10,15,  108,20,
        3, 0,  86,40,10,    86,35,5,     86,31,15,   86,40,
        3, 0, 108,40,10,   108,35,5,    108,31,15,  108,40,

        3, 0,  43,36,10,    50,36,5,    64,36,5,   80,36
    };

    private static final int[] ai_SpriteParameters = new int[]
    {
        0x0800, 0x0800, 0x0200, 0x0200, 2, 1, Sprite.SPRITE_ALIGN_CENTER, Sprite.ANIMATION_CYCLIC,
        0x0800, 0x0800, 0x0400, 0x0400, 2, 1, Sprite.SPRITE_ALIGN_CENTER, Sprite.ANIMATION_FROZEN,
        0x1000, 0x1000, 0x0200, 0x0200, 2, 1, Sprite.SPRITE_ALIGN_CENTER, Sprite.ANIMATION_FROZEN,
        0x0C00, 0x0C00, 0x0E00, 0x1000, 2, 1, Sprite.SPRITE_ALIGN_CENTER, Sprite.ANIMATION_CYCLIC,
        0x0C00, 0x0C00, 0x0E00, 0x1000, 4, 1, Sprite.SPRITE_ALIGN_CENTER, Sprite.ANIMATION_FROZEN,
        0x0C00, 0x0C00, 0x0E00, 0x1000, 3, 2, Sprite.SPRITE_ALIGN_CENTER, Sprite.ANIMATION_FROZEN,
        0x0C00, 0x1800, 0x0C00, 0x1800, 2, 1, Sprite.SPRITE_ALIGN_CENTER, Sprite.ANIMATION_CYCLIC,
        0x0C00, 0x1800, 0x0C00, 0x1800, 4, 1, Sprite.SPRITE_ALIGN_CENTER, Sprite.ANIMATION_FROZEN,
        0x0C00, 0x1800, 0x0C00, 0x1800, 3, 2, Sprite.SPRITE_ALIGN_CENTER, Sprite.ANIMATION_FROZEN,
        0x0800, 0x0A00, 0x0A00, 0x1200, 2, 1, Sprite.SPRITE_ALIGN_CENTER, Sprite.ANIMATION_CYCLIC,
        0x0800, 0x0A00, 0x0A00, 0x1200, 4, 1, Sprite.SPRITE_ALIGN_CENTER, Sprite.ANIMATION_FROZEN,
        0x0800, 0x0A00, 0x0A00, 0x1200, 3, 2, Sprite.SPRITE_ALIGN_CENTER, Sprite.ANIMATION_FROZEN,
        0x0900, 0x1300, 0x0800, 0x1000, 4, 1, Sprite.SPRITE_ALIGN_CENTER, Sprite.ANIMATION_CYCLIC,
        0x0900, 0x1300, 0x0800, 0x1000, 4, 1, Sprite.SPRITE_ALIGN_CENTER, Sprite.ANIMATION_FROZEN,
        0x0900, 0x1300, 0x0800, 0x1000, 3, 2, Sprite.SPRITE_ALIGN_CENTER, Sprite.ANIMATION_FROZEN,
        0x0B00, 0x1800, 0x0A00, 0x1200, 4, 1, Sprite.SPRITE_ALIGN_CENTER, Sprite.ANIMATION_CYCLIC,
        0x0B00, 0x1800, 0x0A00, 0x1200, 4, 1, Sprite.SPRITE_ALIGN_CENTER, Sprite.ANIMATION_FROZEN,
        0x0B00, 0x1800, 0x0A00, 0x1200, 3, 2, Sprite.SPRITE_ALIGN_CENTER, Sprite.ANIMATION_FROZEN
    };

    public static final short ash_PlayField_01[] =
    {
        1, (110 << 8), (56 << 8), (22 << 8), (32 << 8),
        2, (88 << 8), (56 << 8), (38 << 8), (32 << 8),

        3, (0 << 8), (36 << 8), (54 << 8), (15 << 8),
        3, (17 << 8), (65 << 8), (32 << 8), (14 << 8),
        4, (0 << 8), (14 << 8), (54 << 8), (65 << 8),

        5, (75 << 8), (16 << 8), (44 << 8), (8 << 8),
        5, (75 << 8), (37 << 8), (44 << 8), (11 << 8),
        6, (75 << 8), (0 << 8), (44 << 8), (48 << 8),

        0
    };





    private int checkDestination(int fld)
    {
        int field = 0;

        int i = 0;

        int i_sX;
        int i_sY;
        int i_sw;
        int i_sh;

        int i_dX = pPlayerSight.i_ScreenX+ pPlayerSight.i_col_offsetX;
        int i_dY = pPlayerSight.i_ScreenY+ pPlayerSight.i_col_offsetY;
        int i_dw = pPlayerSight.i_col_width;
        int i_dh = pPlayerSight.i_col_height;

        while(true)
        {
            field = ash_PlayField_01[i++];

            if(field == 0) break;
            if(field > fld)
            {
                i+=4;
            }
            else
            {
                i_sX = ash_PlayField_01[i++];
                i_sY = ash_PlayField_01[i++];
                i_sw = ash_PlayField_01[i++];
                i_sh = ash_PlayField_01[i++];

                if (i_sX+i_sw < i_dX || i_sY+i_sh < i_dY || i_dX+i_dw < i_sX || i_dY+i_dh < i_sY)
                {
                }
                else
                {
                    return field;
                }
            }
        }
        return 0;
    }

    private void initSpriteFromArray(Sprite _sprite, int _actorIndex)
    {
        _sprite.i_ObjectType = _actorIndex;
        _actorIndex *= SPRITEDATALENGTH;
        int i_w = ai_SpriteParameters[_actorIndex++];
        int i_h = ai_SpriteParameters[_actorIndex++];
        int i_aw = ai_SpriteParameters[_actorIndex++];
        int i_ah = ai_SpriteParameters[_actorIndex++];
        int i_f = ai_SpriteParameters[_actorIndex++];
        int i_fd = ai_SpriteParameters[_actorIndex++];
        int i_mp = ai_SpriteParameters[_actorIndex++];
        int i_an = ai_SpriteParameters[_actorIndex++];

        _sprite.setAnimation(i_an, i_mp, i_w, i_h, i_f, 0, i_fd);
        _sprite.setCollisionBounds((i_w - i_aw) >> 1, (i_h - i_ah) >> 1, i_aw, i_ah);
    }

    private void deactivatePathForSpriteID(int _spriteID)
    {
        for (int li = 0; li < MAX_PATHS; li++)
        {
            if (ap_Paths[li].isCompleted()) continue;
            if (ap_Paths[li].p_sprite.i_spriteID == _spriteID)
            {
                ap_Paths[li].deactivate();
                break;
            }
        }
    }

    private void processSprites()
    {
        for (int li = 0; li < MAX_SPRITES; li++)
        {
            if (ap_Sprites[li].lg_SpriteActive)
            {
                if (ap_Sprites[li].processAnimation())
                {
                    switch (ap_Sprites[li].i_ObjectType)
                    {
                        case SPRITE_MAN1_DEAD:
                        case SPRITE_MAN2_DEAD:
                        case SPRITE_MAN3_DEAD:
                        case SPRITE_MAN4_DEAD:
                        case SPRITE_MAN5_DEAD:
                            deactivatePathForSpriteID(ap_Sprites[li].i_spriteID);
                            ap_Sprites[li].lg_SpriteActive = false;
                            ap_Sprites[li].b_Staying = false;
                            if(ap_Sprites[li].i_ActionCount > 0)
                            {
                                i_BanditsOnScreen--;
                            }
                            break;
                        case SPRITE_MAN1_FIRE:
                            initSpriteFromArray(ap_Sprites[li], SPRITE_MAN1);
                            if(ap_Sprites[li].i_ActionCount > DEAD_COUNT)
                            {
                                initSpriteFromArray(pPlayerSight, SPRITE_SHERIFF_DEAD);
                                m_pAbstractGameActionListener.processGameAction(GAMEACTION_SHERIFF_DEAD);
                            }
                            break;
                        case SPRITE_MAN2_FIRE:
                            initSpriteFromArray(ap_Sprites[li], SPRITE_MAN2);
                            if(ap_Sprites[li].i_ActionCount > DEAD_COUNT)
                            {
                                initSpriteFromArray(pPlayerSight, SPRITE_SHERIFF_DEAD);
                                m_pAbstractGameActionListener.processGameAction(GAMEACTION_SHERIFF_DEAD);
                            }
                            break;
                        case SPRITE_MAN3_FIRE:
                            initSpriteFromArray(ap_Sprites[li], SPRITE_MAN3);
                            if(ap_Sprites[li].i_ActionCount > DEAD_COUNT)
                            {
                                initSpriteFromArray(pPlayerSight, SPRITE_SHERIFF_DEAD);
                                m_pAbstractGameActionListener.processGameAction(GAMEACTION_SHERIFF_DEAD);
                            }
                            break;
                        case SPRITE_MAN4_FIRE:
                            initSpriteFromArray(ap_Sprites[li], SPRITE_MAN4);
                            if(ap_Sprites[li].i_ActionCount > DEAD_COUNT)
                            {
                                initSpriteFromArray(pPlayerSight, SPRITE_SHERIFF_DEAD);
                                m_pAbstractGameActionListener.processGameAction(GAMEACTION_SHERIFF_DEAD);
                            }
                            break;
                        case SPRITE_MAN5_FIRE:
                            initSpriteFromArray(ap_Sprites[li], SPRITE_MAN5);
                            if(ap_Sprites[li].i_ActionCount > DEAD_COUNT)
                            {
                                initSpriteFromArray(pPlayerSight, SPRITE_SHERIFF_DEAD);
                                m_pAbstractGameActionListener.processGameAction(GAMEACTION_SHERIFF_DEAD);
                            }
                            break;
                    } 
                } 


                if(ap_Sprites[li].b_Staying)
                {
                    if (ap_Sprites[li].i_ActionDelay > 0)
                    {
                        ap_Sprites[li].i_ActionDelay--;
                    }
                    else
                    {

                        if(ap_Sprites[li].i_ActionCount > 0 && getRandomInt(8) > 2)
                        {
                            ap_Sprites[li].i_ActionDelay = ACTION_DELAY;
                            ap_Sprites[li].i_ActionCount++;

                            {
                                switch (ap_Sprites[li].i_ObjectType)
                                {
                                    case SPRITE_MAN1:
                                        initSpriteFromArray(ap_Sprites[li], SPRITE_MAN1_FIRE);
                                        ap_Sprites[li].i_ActionDelay = ACTION_DELAY;
                                        m_pAbstractGameActionListener.processGameAction(GAMEACTION_BANDIT_FIRE);
                                        break;
                                    case SPRITE_MAN2:
                                        initSpriteFromArray(ap_Sprites[li], SPRITE_MAN2_FIRE);
                                        ap_Sprites[li].i_ActionDelay = ACTION_DELAY;
                                        m_pAbstractGameActionListener.processGameAction(GAMEACTION_BANDIT_FIRE);
                                        break;
                                    case SPRITE_MAN3:
                                        initSpriteFromArray(ap_Sprites[li], SPRITE_MAN3_FIRE);
                                        ap_Sprites[li].i_ActionDelay = ACTION_DELAY;
                                        m_pAbstractGameActionListener.processGameAction(GAMEACTION_BANDIT_FIRE);
                                        break;
                                    case SPRITE_MAN4:
                                        initSpriteFromArray(ap_Sprites[li], SPRITE_MAN4_FIRE);
                                        ap_Sprites[li].i_ActionDelay = ACTION_DELAY;
                                        m_pAbstractGameActionListener.processGameAction(GAMEACTION_BANDIT_FIRE);
                                        break;
                                    case SPRITE_MAN5:
                                        initSpriteFromArray(ap_Sprites[li], SPRITE_MAN5_FIRE);
                                        ap_Sprites[li].i_ActionDelay = ACTION_DELAY;
                                        m_pAbstractGameActionListener.processGameAction(GAMEACTION_BANDIT_FIRE);
                                        break;
                                }
                            }
                        }
                        else
                        {
                            ap_Sprites[li].b_Staying = false;
                        }
                    }
                }
            } 
        } 
    }

    private void processPaths()
    {
        for (int li = 0; li < MAX_PATHS; li++)
        {
            if (ap_Paths[li].isCompleted()) continue;
            int i_spr = ap_Paths[li].p_sprite.i_ObjectType;

            switch (i_spr)
            {
                case SPRITE_MAN1_DEAD:
                case SPRITE_MAN2_DEAD:
                case SPRITE_MAN3_DEAD:
                case SPRITE_MAN4_DEAD:
                case SPRITE_MAN5_DEAD:
                    ap_Paths[li].p_sprite.b_Staying = true;
                    break;
            }

            if (!ap_Paths[li].p_sprite.b_Staying)
            {
                if(ap_Paths[li].processStep())
                {
                    if(ap_Paths[li].getCurrentPointIndex() == 2)
                    {
                        switch(ap_Paths[li].p_sprite.i_ObjectType)
                        {
                            case SPRITE_MAN1:
                            case SPRITE_MAN2:
                            case SPRITE_MAN3:
                            case SPRITE_MAN4:
                            case SPRITE_MAN5:
                                ap_Paths[li].p_sprite.b_Staying = true;
                                break;
                        }
                    }
                }

                if (ap_Paths[li].isCompleted())
                {
                    if(ap_Paths[li].p_sprite.i_ActionCount > 0)
                    {
                        i_BanditsOnScreen--;
                        m_pAbstractGameActionListener.processGameAction(GAMEACTION_BANDIT_HIDE);
                    }
                    ap_Paths[li].p_sprite.lg_SpriteActive = false;
                }
            }
        }
    }

    public String getGameTextID()
    {
        return "SHERIFF";
    }

    private PathController getInactivePath()
    {
        for (int li = 0; li < MAX_PATHS; li++)
        {
            if (ap_Paths[li].isCompleted()) return ap_Paths[li];
        }
        return null;
    }

    private Sprite getInactiveSprite()
    {
        for (int li = 0; li < MAX_SPRITES; li++)
        {
            if (!ap_Sprites[li].lg_SpriteActive) return ap_Sprites[li];
        }
        return null;
    }

    public boolean initState()
    {
        ap_Sprites = new Sprite[MAX_SPRITES];
        for (int li = 0; li < MAX_SPRITES; li++)
        {
            ap_Sprites[li] = new Sprite(li);
        }
        ap_Paths = new PathController[MAX_PATHS];
        for (int li = 0; li < MAX_PATHS; li++)
        {
            ap_Paths[li] = new PathController();
        }

        pPlayerSight = new Sprite(0);

        initSpriteFromArray(pPlayerSight, SPRITE_SHERIFF_SIGHT);
        pPlayerSight.lg_SpriteActive = true;

        int i8_cx = i8_gameScreenWidth >> 1;
        int i8_cy = i8_gameScreenHeight >> 1;

        pPlayerSight.setMainPointXY(i8_cx, i8_cy);

        p_MoveObject = new MoveObject();

        return true;
    }

    private boolean processSheriff(int _buttonState)
    {
        if (pPlayerSight.processAnimation())
        {
            switch(pPlayerSight.i_ObjectType)
            {
                case SPRITE_SHERIFF_SIGHT:
                    break;
                case SPRITE_SHERIFF_DEAD:
                    return false;
                case SPRITE_SHERIFF_FIRE:
                    initSpriteFromArray(pPlayerSight, SPRITE_SHERIFF_SIGHT);

                    for (int li = 0; li < MAX_SPRITES; li++)
                    {
                        if (ap_Sprites[li].lg_SpriteActive)
                        {
                            if(ap_Sprites[li].isCollided(pPlayerSight) )
                            {
                                if(checkDestination(ap_Sprites[li].i_ObjectState) == 0)
                                {
                                    switch(ap_Sprites[li].i_ObjectType)
                                    {
                                        case SPRITE_MAN1:
                                        case SPRITE_MAN1_FIRE:
                                            initSpriteFromArray(ap_Sprites[li], SPRITE_MAN1_DEAD);
                                            if(ap_Sprites[li].i_ActionCount > 0)
                                            {
                                                i_Bandits--;
                                                addScore(SCORES_BANDIT);
                                                m_pAbstractGameActionListener.processGameAction(GAMEACTION_BANDIT_DEAD);
                                            }
                                            else
                                            {
                                                b_MistakeKill = true;
                                                m_pAbstractGameActionListener.processGameAction(GAMEACTION_KILL_MAN);
                                            }
                                            break;
                                        case SPRITE_MAN2:
                                        case SPRITE_MAN2_FIRE:
                                            initSpriteFromArray(ap_Sprites[li], SPRITE_MAN2_DEAD);
                                            if(ap_Sprites[li].i_ActionCount > 0)
                                            {
                                                i_Bandits--;
                                                addScore(SCORES_BANDIT);
                                                m_pAbstractGameActionListener.processGameAction(GAMEACTION_BANDIT_DEAD);
                                            }
                                            else
                                            {
                                                b_MistakeKill = true;
                                                m_pAbstractGameActionListener.processGameAction(GAMEACTION_KILL_MAN);
                                            }
                                            break;
                                        case SPRITE_MAN3:
                                        case SPRITE_MAN3_FIRE:
                                            initSpriteFromArray(ap_Sprites[li], SPRITE_MAN3_DEAD);
                                            if(ap_Sprites[li].i_ActionCount > 0)
                                            {
                                                i_Bandits--;
                                                addScore(SCORES_BANDIT);
                                                m_pAbstractGameActionListener.processGameAction(GAMEACTION_BANDIT_DEAD);
                                            }
                                            else
                                            {
                                                b_MistakeKill = true;
                                                m_pAbstractGameActionListener.processGameAction(GAMEACTION_KILL_MAN);
                                            }
                                            break;
                                        case SPRITE_MAN4:
                                        case SPRITE_MAN4_FIRE:
                                            initSpriteFromArray(ap_Sprites[li], SPRITE_MAN4_DEAD);
                                            if(ap_Sprites[li].i_ActionCount > 0)
                                            {
                                                i_Bandits--;
                                                addScore(SCORES_BANDIT);
                                                m_pAbstractGameActionListener.processGameAction(GAMEACTION_BANDIT_DEAD);
                                            }
                                            else
                                            {
                                                b_MistakeKill = true;
                                                m_pAbstractGameActionListener.processGameAction(GAMEACTION_KILL_MAN);
                                            }

                                            break;
                                        case SPRITE_MAN5:
                                        case SPRITE_MAN5_FIRE:
                                            initSpriteFromArray(ap_Sprites[li], SPRITE_MAN5_DEAD);
                                            if(ap_Sprites[li].i_ActionCount > 0)
                                            {
                                                i_Bandits--;
                                                addScore(SCORES_BANDIT);
                                                m_pAbstractGameActionListener.processGameAction(GAMEACTION_BANDIT_DEAD);
                                            }
                                            else
                                            {
                                                b_MistakeKill = true;
                                                m_pAbstractGameActionListener.processGameAction(GAMEACTION_KILL_MAN);
                                            }
                                            break;
                                    }
                                }
                            }
                        } 
                    } 
                    break;
            }
        }

        if(pPlayerSight.i_ObjectType == SPRITE_SHERIFF_SIGHT)
        {

            int i_mx = pPlayerSight.i_mainX;
            int i_my = pPlayerSight.i_mainY;


            if ((_buttonState & MoveObject.BUTTON_LEFT) != 0)
            {
                i_mx -= MOVE_SPEED;
                if( i_mx < LEFT_BORDER) i_mx = LEFT_BORDER;
            }
            else if ((_buttonState & MoveObject.BUTTON_RIGHT) != 0)
            {
                i_mx += MOVE_SPEED;
                if( i_mx > RIGHT_BORDER) i_mx = RIGHT_BORDER;
            }
            if ((_buttonState & MoveObject.BUTTON_UP) != 0)
            {
                i_my -= MOVE_SPEED;
                if( i_my < TOP_BORDER) i_my = TOP_BORDER;
            }
            else if ((_buttonState & MoveObject.BUTTON_DOWN) != 0)
            {
                i_my += MOVE_SPEED;
                if( i_my > BOTTOM_BORDER) i_my = BOTTOM_BORDER;
            }

            if ((_buttonState & MoveObject.BUTTON_FIRE) != 0)
            {
                if(i_SheriffBullets > 0)
                {
                    initSpriteFromArray(pPlayerSight, SPRITE_SHERIFF_FIRE);
                    m_pAbstractGameActionListener.processGameAction(GAMEACTION_SHERIFF_FIRE);
                    i_SheriffBullets--;
                }
                else
                {
                    m_pAbstractGameActionListener.processGameAction(GAMEACTION_NO_BULLETS);
                }
            }

            pPlayerSight.setMainPointXY(i_mx, i_my);

        }
        return true;
    }


public boolean newGameSession(int _gameAreaWidth, int _gameAreaHeight, int _gameLevel, CAbstractPlayer[] _players, startup _listener, String _staticArrayResourceName)
    {
        i8_gameScreenWidth = _gameAreaWidth<<8;
        i8_gameScreenHeight = _gameAreaHeight<<8;

        if (!super.newGameSession(_gameAreaWidth, _gameAreaHeight, _gameLevel, _players, _listener, _staticArrayResourceName)) return false;

        switch (_gameLevel)
        {
            case GAMELEVEL_EASY:
                {
                    i_timedelay = LEVEL_EASY_TIMEDELAY;
                    i_borndelay = LEVEL_EASY_ENEMYFREQ;
                    m_iPlayerAttemptions = LEVEL_EASY_LIVES;
                    i_Bandits = LEVEL_EASY_BANDITS;
                }
                ;
                break;
            case GAMELEVEL_NORMAL:
                {
                    i_timedelay = LEVEL_NORMAL_TIMEDELAY;
                    i_borndelay = LEVEL_NORMAL_ENEMYFREQ;
                    m_iPlayerAttemptions = LEVEL_NORMAL_LIVES;
                    i_Bandits = LEVEL_NORMAL_BANDITS;
                }
                ;
                break;
            case GAMELEVEL_HARD:
                {
                    i_timedelay = LEVEL_HARD_TIMEDELAY;
                    i_borndelay = LEVEL_HARD_ENEMYFREQ;
                    m_iPlayerAttemptions = LEVEL_HARD_LIVES;
                    i_Bandits = LEVEL_HARD_BANDITS;
                }
                ;
                break;
        }

        i_Bandits = 0;

        i_GameTimer = 0; 
        i_lasttime = i_GameTimer;
        i_BanditsOnScreen = 0;
        i_SheriffBullets = SHERIFF_BULLETS;
        i_BanditsBand = i_Bandits;
        b_MistakeKill = false;


        return true;
    }

    public void resumeGameAfterPlayerLostOrPaused()
    {
        super.resumeGameAfterPlayerLostOrPaused();

        deinitState();
        initState();

        i_GameTimer = 0; 
        i_lasttime = i_GameTimer;
        i_BanditsOnScreen = 0;
        b_MistakeKill = false;
        if(i_SheriffBullets < (SHERIFF_BULLETS >> 1)) i_SheriffBullets = (SHERIFF_BULLETS >> 1);

    }

    public boolean newGameStage(int _stage)
    {
        if(super.newGameStage(_stage))
        {
            resumeGameAfterPlayerLostOrPaused();

            switch(getGameDifficultLevel())
            {
                case GAMELEVEL_EASY:
                    i_Bandits = LEVEL_EASY_BANDITS;
                    break;
                case GAMELEVEL_NORMAL:
                    i_Bandits = LEVEL_NORMAL_BANDITS;
                    break;
                case GAMELEVEL_HARD:
                    i_Bandits = LEVEL_HARD_BANDITS;
                    break;
            }
            i_Bandits += getRandomInt(ADDITIONAL_BANDITS);
            i_BanditsBand = i_Bandits;
            i_SheriffBullets = SHERIFF_BULLETS;

            return true;
        }
        return false;
    }



    public void deinitState()
    {
        ap_Sprites = null;
        pPlayerSight = null;
        ap_Paths = null;
        p_MoveObject = null;

        Runtime.getRuntime().gc();
    }

    public int _getMaximumDataSize()
    {
        return MAX_PATHS*PathController.DATASIZE_BYTES+MAX_SPRITES*(Sprite.DATASIZE_BYTES+1)+6+Sprite.DATASIZE_BYTES+1 + 16;
    }

    public void _saveGameData(DataOutputStream _outputStream) throws IOException
    {
        _outputStream.writeByte(m_iPlayerAttemptions);
        _outputStream.writeInt(i_GameTimer);
        _outputStream.writeInt(i_lasttime);
        _outputStream.writeInt(i_borndelay);
        _outputStream.writeByte(i_Bandits);
        _outputStream.writeByte(i_BanditsOnScreen);
        _outputStream.writeByte(i_BanditsBand);
        _outputStream.writeByte(i_SheriffBullets);
        _outputStream.writeBoolean(b_MistakeKill);

        for(int li=0;li<MAX_SPRITES;li++)
        {
            _outputStream.writeByte(ap_Sprites[li].i_ObjectType);
            ap_Sprites[li].writeSpriteToStream(_outputStream);
        }

        for(int li=0;li<MAX_PATHS;li++) ap_Paths[li].writePathToStream(_outputStream);

        _outputStream.writeInt(i_rndfreq);

        _outputStream.writeByte(pPlayerSight.i_ObjectType);
        pPlayerSight.writeSpriteToStream(_outputStream);
    }

    public void _loadGameData(DataInputStream _inputStream) throws IOException
    {
        m_iPlayerAttemptions = _inputStream.readByte();
        i_GameTimer = _inputStream.readInt();
        i_lasttime = _inputStream.readInt();
        i_borndelay = _inputStream.readInt();
        i_Bandits = _inputStream.readByte();
        i_BanditsOnScreen = _inputStream.readByte();
        i_BanditsBand = _inputStream.readByte();
        i_SheriffBullets = _inputStream.readByte();
        b_MistakeKill = _inputStream.readBoolean();

        for(int li=0;li<MAX_SPRITES;li++)
        {
            int i_type = _inputStream.readByte();
            initSpriteFromArray(ap_Sprites[li],i_type);
            ap_Sprites[li].readSpriteFromStream(_inputStream);
        }

        for(int li=0;li<MAX_PATHS;li++)
        {
            ap_Paths[li].readPathFromStream(_inputStream,ap_Sprites);
            ap_Paths[li].as_pathArray = ash_Paths;
        }

        i_rndfreq = _inputStream.readInt();

        initSpriteFromArray(pPlayerSight,_inputStream.readUnsignedByte());
        pPlayerSight.readSpriteFromStream(_inputStream);
    }

    private void generateNewMan()
    {
        if((i_lasttime + i_borndelay) < i_GameTimer )
        {
            i_lasttime = i_GameTimer;

            Sprite p_emptySprite = null;
            PathController p_inactivePath = getInactivePath();

            int i = getRandomInt(100) / 10;
            int begin_index = i;
            boolean bFinding = true;

            while(bFinding)
            {
                if(i >= MAX_SPRITES) i = 0;

                if (!ap_Sprites[i].lg_SpriteActive)
                {
                    p_emptySprite = ap_Sprites[i];
                    bFinding = false;
                }
                else
                {
                    i++;
                    if(i >= MAX_SPRITES) i = 0;
                    if(i != begin_index)
                    {
                        p_emptySprite = null;
                        bFinding = false;
                    }
                }
            }

            if (p_inactivePath != null && p_emptySprite != null)
            {
                int i_type = SPRITE_MAN1;
                int i_path = 0;
                int i_playfield = 0;
                int i_location_index = 0;

                p_emptySprite.lg_SpriteActive = true;

                switch(i)
                {
                    case -1:
                        p_emptySprite.lg_SpriteActive = false;
                        break;
                    case 0:
                        i_type = SPRITE_MAN1;
                        i_path = PATH_MAN_11;
                        i_location_index = 10;
                        i_playfield = 1;
                        break;
                    case 1:
                        i_type = SPRITE_MAN1;
                        i_path = PATH_MAN_1;
                        i_location_index = 0;
                        i_playfield = 3;
                        break;
                    case 2:
                        i_type = SPRITE_MAN1;
                        i_path = PATH_MAN_2;
                        i_location_index = 1;
                        i_playfield = 3;
                        break;
                    case 3:
                        i_type = SPRITE_MAN2;
                        i_path = PATH_MAN_3;
                        i_location_index = 2;
                        i_playfield = 3;
                        break;
                    case 4:
                        i_type = SPRITE_MAN1;
                        i_path = PATH_MAN_4;
                        i_location_index = 3;
                        i_playfield = 3;
                        break;
                    case 5:
                        i_type = SPRITE_MAN5;
                        i_path = PATH_MAN_9;
                        i_location_index = 8;
                        i_playfield = 4;
                        break;
                    case 6:
                        i_type = SPRITE_MAN3;
                        i_path = PATH_MAN_5;
                        i_location_index = 4;
                        i_playfield = 5;
                        break;
                    case 7:
                        i_type = SPRITE_MAN3;
                        i_path = PATH_MAN_6;
                        i_location_index = 5;
                        i_playfield = 5;
                        break;
                    case 8:
                        i_type = SPRITE_MAN3;
                        i_path = PATH_MAN_7;
                        i_location_index = 6;
                        i_playfield = 5;
                        break;
                    case 9:
                        i_type = SPRITE_MAN3;
                        i_path = PATH_MAN_8;
                        i_location_index = 7;
                        i_playfield = 5;
                        break;
                    case 10:
                        i_type = SPRITE_MAN4;
                        i_path = PATH_MAN_10;
                        i_location_index = 9;
                        i_playfield = 6;
                        break;
                }
                p_emptySprite.i_ObjectState = i_playfield;
                p_emptySprite.i_ActionDelay = ACTION_DELAY;
                p_emptySprite.i_TTL = i_location_index;


                if((i_Bandits > i_BanditsOnScreen) && (getRandomInt(2) == 1))
                {
                    p_emptySprite.i_ActionCount = 1;
                    i_BanditsOnScreen++;
                    m_pAbstractGameActionListener.processGameAction(GAMEACTION_NEW_BANDIT);
                }
                else
                {
                    p_emptySprite.i_ActionCount = 0;
                    m_pAbstractGameActionListener.processGameAction(GAMEACTION_NEW_MAN);
                }

                initSpriteFromArray(p_emptySprite, i_type);
                p_inactivePath.initPath(0, 0, p_emptySprite, ash_Paths, i_path, 0, 0, PathController.MODIFY_NONE);
            }
        }
    }

    public int nextGameStep()
    {
        CAbstractPlayer p_player = m_pPlayerList[0];
        p_player.nextPlayerMove(this, p_MoveObject);

        processPaths();

        if (processSheriff(p_MoveObject.i_buttonState) )
        {
        }
        else
        {
            m_iPlayerAttemptions --;

            if (m_iPlayerAttemptions == 0)
            {
               m_pWinningList = null;
               setGameState(GAMEWORLDSTATE_GAMEOVER);
            }
            else
            {
               setGameState(GAMEWORLDSTATE_PLAYERLOST);
            }
        }

        p_MoveObject.i_buttonState &= ~MoveObject.BUTTON_FIRE;
        ((Player)p_player).i_buttonState &= ~MoveObject.BUTTON_FIRE;

        processSprites();
        generateNewMan();

        if(i_Bandits == 0 && i_BanditsOnScreen == 0)
        {
            if((SHERIFF_BULLETS - i_SheriffBullets) == i_BanditsBand)
            {
                addScore(SCORES_WITHOUT_MISSING);
            }

            m_pAbstractGameActionListener.processGameAction(GAMEACTION_CONGRATULATION);
            m_pWinningList = m_pPlayerList;
            setGameState(GAMEWORLDSTATE_GAMEOVER);
            for (int li = 0; li < MAX_SPRITES; li++)
            {
                ap_Sprites[li].lg_SpriteActive = false;
            }


        }

        if(b_MistakeKill && pPlayerSight.i_ObjectType != SPRITE_SHERIFF_FIRE)
        {
            m_iPlayerAttemptions --;

            if (m_iPlayerAttemptions == 0)
            {
               m_pWinningList = null;
               setGameState(GAMEWORLDSTATE_GAMEOVER);
            }
            else
            {
               setGameState(GAMEWORLDSTATE_PLAYERLOST);
            }
            b_MistakeKill = false;
        }

        i_GameTimer++;

        return m_iGameState;
    }

    public void addScore(int score)
    {
        switch(getGameDifficultLevel())
        {
            case GAMELEVEL_EASY:
                break;
            case GAMELEVEL_NORMAL:
                score = score * 2;
                break;
            case GAMELEVEL_HARD:
                score = score * 3;
                break;
        }

        CAbstractPlayer p_player = m_pPlayerList[0];
        p_player.setPlayerMoveGameScores(score, false);
    }

    public int getGameStepDelay()
    {
        return i_timedelay;
    }
}

