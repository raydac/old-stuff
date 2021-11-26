
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.DataInputStream;


public class MobileGameletImpl extends MobileGamelet
{
    public static final int RIGHT_MARGIN = (16 << 8);
    public static final int LEFT_MARGIN = (16 << 8);

    private static final int CROCODILE_SPEED = 0x0200;
    private static final int CROCODILE_ATTACK_SPEED = 0x0355;
    private static final int ANIMAL_SPEED = 0x0100;
    private static final int HUNTER_SPEED = 0x0100;
    private static final int BULLET_SPEED = 0x1000;

    private static final int STAY_DELAY = 0x0005;
    private static final int HUNTER_STAY_DELAY = 0x0040; 
    private static final int DRINK_DELAY = 0x0015; 
    private static final int DEAD_DELAY = 0x0007;

    public static final int MAXIMUM_ALLOWED_MISSES = 20;

    private static final int MAX_HSPEED = (4 << 8);
    private static final int MIN_HSPEED = (4 << 8);
    private static final int INC_HSPEED = (0x0400); 
    private static final int DEC_HSPEED = (0x0400);
    private static final int HSPEED = (0x0200);
    private static final int MOVE_STEPS = 8;

    public static final int LANE_SIZE = (16 << 8);
    public static final int LANE_X = (8 << 8);
    public static final int LANE_X1 = LANE_X + (LANE_SIZE * 0);
    public static final int LANE_X2 = LANE_X + (LANE_SIZE * 1);
    public static final int LANE_X3 = LANE_X + (LANE_SIZE * 2);
    public static final int LANE_X4 = LANE_X + (LANE_SIZE * 3);
    public static final int LANE_X5 = LANE_X + (LANE_SIZE * 4);
    public static final int LANE_X6 = LANE_X + (LANE_SIZE * 5);
    public static final int LANE_X7 = LANE_X + (LANE_SIZE * 6);
    public static final int LANE_X8 = LANE_X + (LANE_SIZE * 7);

    public static final int SEA_LINE = ((52) << 8);
    public static final int HUNTING_SPACE = (16 << 8);

    public static final int BULLET_X_OFFSET = (0 << 8);
    public static final int BULLET_Y_OFFSET = (0 << 8);

    private static int I8_SCREEN_WIDTH;

    private static int I8_SCREEN_HEIGHT;

    public MoveObject p_MoveObject;

    public static final int GAMEACTION_CROCODILE_DIVE           = 0;
    public static final int GAMEACTION_CROCODILE_ATTACK         = 1;
    public static final int GAMEACTION_CROCODILE_BITE           = 2;
    public static final int GAMEACTION_CROCODILE_BACK           = 3;
    public static final int GAMEACTION_CROCODILE_DEAD           = 4;
    public static final int GAMEACTION_HUNTER                   = 5;
    public static final int GAMEACTION_HUNTER_STAY              = 6;
    public static final int GAMEACTION_HUNTER_FIRE              = 7;
    public static final int GAMEACTION_ANIMAL_GENERATED         = 8;
    public static final int GAMEACTION_ANIMAL_GULP              = 9;
    public static final int GAMEACTION_ANIMAL_DEAD              = 10;

    private static final int SCORES_ANIMAL_01 = 40;
    private static final int SCORES_ANIMAL_02 = 10;
    private static final int SCORES_ANIMAL_03 = 20;
    private static final int SCORES_ANIMAL_04 = 30;
    private static final int SCORES_ANIMAL_05 = 15;
    private static final int SCORES_ANIMAL_06 = 25;
    private static final int SCORES_ANIMAL_07 = 5;
    private static final int SCORES_ANIMAL_08 = 35;

    private int i_timedelay;

    public int i_playerAttemptions;

    public int i_GameTimer;

    public int i_LastTime;
    public int i_GenerateDelay;

    public int i_LastHunterTime;
    public int i_HunterDelay;

    public int i_HSpeed;

    public int i_MissCounter;


    public static final int LEVEL_EASY = 0;

    private static final int LEVEL_EASY_ATTEMPTIONS = 4;

    private static final int LEVEL_EASY_GENERATION = 25;
    private static final int LEVEL_EASY_HUNTER = 112;

    private static final int LEVEL_EASY_TIMEDELAY = 100;




    public static final int LEVEL_NORMAL = 1;

    private static final int LEVEL_NORMAL_ATTEMPTIONS = 3;

    private static final int LEVEL_NORMAL_GENERATION = 30;
    private static final int LEVEL_NORMAL_HUNTER = 97;

    private static final int LEVEL_NORMAL_TIMEDELAY = 90;




    public static final int LEVEL_HARD = 2;

    private static final int LEVEL_HARD_ATTEMPTIONS = 2;

    private static final int LEVEL_HARD_GENERATION = 35;
    private static final int LEVEL_HARD_HUNTER = 75;

    private static final int LEVEL_HARD_TIMEDELAY = 80;



    public Sprite pPlayerSprite;

    public static final int MAX_SPRITES_NUMBER = 12;

    public Sprite[] ap_Sprites;

    private static final int SPRITEDATALENGTH = 10;

    public static final int SPRITE_CROCODILE = 0;
    public static final int SPRITE_CROCODILE_LEFT = 1;
    public static final int SPRITE_CROCODILE_RIGHT = 2;
    public static final int SPRITE_CROCODILE_BACK = 5;
    public static final int SPRITE_CROCODILE_BITE = 6;
    public static final int SPRITE_CROCODILE_KEEP_ANIMAL = 7;
    public static final int SPRITE_CROCODILE_SURFACE = 8;
    public static final int SPRITE_CROCODILE_DIVE = 9;
    public static final int SPRITE_CROCODILE_DEAD = 10;

    public static final int SPRITE_ANIMAL_01 = 11;
    public static final int SPRITE_ANIMAL_02 = 12;
    public static final int SPRITE_ANIMAL_03 = 13;
    public static final int SPRITE_ANIMAL_04 = 14;
    public static final int SPRITE_ANIMAL_05 = 15;
    public static final int SPRITE_ANIMAL_06 = 16;
    public static final int SPRITE_ANIMAL_07 = 17;
    public static final int SPRITE_ANIMAL_08 = 18;

    public static final int SPRITE_HUNTER = 19;
    public static final int SPRITE_HUNTER_STAY = 20;
    public static final int SPRITE_HUNTER_BACK = 21;
    public static final int SPRITE_HUNTER_FIRE = 22;

    public static final int SPRITE_BULLET = 23;

    public static final int STATE_ANIMAL_MOVE = 1;
    public static final int STATE_ANIMAL_STAY = 2;
    public static final int STATE_ANIMAL_DRINK = 3;
    public static final int STATE_ANIMAL_BACK = 4;
    public static final int STATE_ANIMAL_BITTEN = 5;

    private static final int [] ai_SpriteParameters = new int[]
    {
        0x1000, 0x3000, 0x0000, 0x0000, 0x0800, 0x3000, 3, 2, Sprite.SPRITE_ALIGN_CENTER, Sprite.ANIMATION_PENDULUM,
        0x2800, 0x2000, 0x0000, 0x0000, 0x1A00, 0x2000, 3, 2, Sprite.SPRITE_ALIGN_CENTER, Sprite.ANIMATION_PENDULUM,
        0x2800, 0x2000, 0x0000, 0x0000, 0x1A00, 0x2000, 3, 2, Sprite.SPRITE_ALIGN_CENTER, Sprite.ANIMATION_PENDULUM,
        0x2800, 0x2000, 0x0000, 0x0000, 0x1A00, 0x2000, 2, 2, Sprite.SPRITE_ALIGN_CENTER, Sprite.ANIMATION_PENDULUM,
        0x2800, 0x2000, 0x0000, 0x0000, 0x1A00, 0x2000, 2, 2, Sprite.SPRITE_ALIGN_CENTER, Sprite.ANIMATION_CYCLIC,
        0x1000, 0x3000, 0x0000, 0x0000, 0x0800, 0x3000, 3, 2, Sprite.SPRITE_ALIGN_CENTER, Sprite.ANIMATION_CYCLIC,
        0x1000, 0x3000, 0x0000, 0x0000, 0x0800, 0x3000, 2, 3, Sprite.SPRITE_ALIGN_CENTER, Sprite.ANIMATION_FROZEN,
        0x1000, 0x3000, 0x0000, 0x0000, 0x0800, 0x3000, 2, 3, Sprite.SPRITE_ALIGN_CENTER, Sprite.ANIMATION_FROZEN,
        0x1000, 0x3000, 0x0000, 0x0000, 0x0800, 0x3000, 2, 3, Sprite.SPRITE_ALIGN_CENTER, Sprite.ANIMATION_FROZEN,
        0x1000, 0x3000, 0x0000, 0x0000, 0x0800, 0x3000, 3, 2, Sprite.SPRITE_ALIGN_CENTER, Sprite.ANIMATION_FROZEN,
        0x1000, 0x3000, 0x0000, 0x0000, 0x1000, 0x0400, 3, 3, Sprite.SPRITE_ALIGN_CENTER, Sprite.ANIMATION_FROZEN,
        0x1000, 0x1000, 0x0000, 0x0000, 0x0400, 0x1200, 1, 1, Sprite.SPRITE_ALIGN_CENTER, Sprite.ANIMATION_FROZEN,
        0x1000, 0x1000, 0x0000, 0x0000, 0x0400, 0x1200, 1, 1, Sprite.SPRITE_ALIGN_CENTER, Sprite.ANIMATION_FROZEN,
        0x1000, 0x1000, 0x0000, 0x0000, 0x0400, 0x1200, 1, 1, Sprite.SPRITE_ALIGN_CENTER, Sprite.ANIMATION_FROZEN,
        0x1000, 0x1000, 0x0000, 0x0000, 0x0400, 0x1200, 1, 1, Sprite.SPRITE_ALIGN_CENTER, Sprite.ANIMATION_FROZEN,
        0x1000, 0x1000, 0x0000, 0x0000, 0x0400, 0x1200, 1, 1, Sprite.SPRITE_ALIGN_CENTER, Sprite.ANIMATION_FROZEN,
        0x1000, 0x1000, 0x0000, 0x0000, 0x0400, 0x1200, 1, 1, Sprite.SPRITE_ALIGN_CENTER, Sprite.ANIMATION_FROZEN,
        0x1000, 0x1000, 0x0000, 0x0000, 0x0400, 0x1200, 1, 1, Sprite.SPRITE_ALIGN_CENTER, Sprite.ANIMATION_FROZEN,
        0x1000, 0x1000, 0x0000, 0x0000, 0x0400, 0x1200, 1, 1, Sprite.SPRITE_ALIGN_CENTER, Sprite.ANIMATION_FROZEN,
        0x0C00, 0x1000, 0x0000, 0x0000, 0x0C00, 0x1000, 2, 2, Sprite.SPRITE_ALIGN_CENTER, Sprite.ANIMATION_CYCLIC,
        0x0C00, 0x1000, 0x0000, 0x0000, 0x0C00, 0x1000, 1, 1, Sprite.SPRITE_ALIGN_CENTER, Sprite.ANIMATION_FROZEN,
        0x0C00, 0x1000, 0x0000, 0x0000, 0x0C00, 0x1000, 2, 2, Sprite.SPRITE_ALIGN_CENTER, Sprite.ANIMATION_CYCLIC,
        0x0C00, 0x1000, 0x0000, 0x0000, 0x0C00, 0x1000, 1, 1, Sprite.SPRITE_ALIGN_CENTER, Sprite.ANIMATION_FROZEN,
        0x0200, 0x0200, 0x0000, 0x0000, 0x0200, 0x0200, 1, 1, Sprite.SPRITE_ALIGN_CENTER, Sprite.ANIMATION_FROZEN,
    };


    private static final int MAX_LOCATIONS = 8;

    private boolean ab_Places[] = new boolean[]
    {
        true, true, true, true, true, true, true, true
    };

    private void resetLocation()
    {
        for(int i=0; i<MAX_LOCATIONS; i++)
        {
            ab_Places[i] = true;
        }
    }

    private int findLocation(int n)
    {
        for(int i=n; i<MAX_LOCATIONS; i++)
        {
            if(ab_Places[i])
            {
                return i;
            }
        }
        for(int i=0; i<=n; i++)
        {
            if(ab_Places[i])
            {
                return i;
            }
        }
        return -1;
    }

    private void unlockLocation(int i)
    {
        if(i >= 0 && i < MAX_LOCATIONS) ab_Places[i] = true;
    }

    private void lockLocation(int i)
    {
        if(i >= 0 && i < MAX_LOCATIONS) ab_Places[i] = false;
    }

    private int calculateScores(int _scores)
    {
        return _scores*(m_iGameDifficultLevel+1);
    }

    private void deactivateSprite(Sprite _sprite)
    {
        _sprite.lg_SpriteActive = false;
    }


    private void activateSprite(Sprite _sprite, int _actorIndex)
    {
        _sprite.i_ObjectType = _actorIndex;
        _actorIndex *= SPRITEDATALENGTH;
        int i8_w = (int)((long)ai_SpriteParameters[_actorIndex++]);
        int i8_h = (int)((long)ai_SpriteParameters[_actorIndex++]);

        int i8_cx = (int)((long)ai_SpriteParameters[_actorIndex++]);
        int i8_cy = (int)((long)ai_SpriteParameters[_actorIndex++]);
        int i8_aw = (int)((long)ai_SpriteParameters[_actorIndex++]);
        int i8_ah = (int)((long)ai_SpriteParameters[_actorIndex++]);

        int i_f = ai_SpriteParameters[_actorIndex++];
        int i_fd = ai_SpriteParameters[_actorIndex++];
        int i_mp = ai_SpriteParameters[_actorIndex++];
        int i_an = ai_SpriteParameters[_actorIndex++];

        _sprite.setAnimation(i_an, i_mp, i8_w, i8_h, i_f, 0, i_fd);

        _sprite.setCollisionBounds((i8_w - i8_aw) >> 1, (i8_h - i8_ah) >> 1, i8_aw, i8_ah);

        _sprite.lg_SpriteActive = true;
    }

    private Sprite getInactiveSprite()
    {
        for (int li = 0; li < MAX_SPRITES_NUMBER; li++)
        {
            if (!ap_Sprites[li].lg_SpriteActive) return ap_Sprites[li];
        }
        return null;
    }

    private void processSprites()
    {
        for (int li = 0; li < MAX_SPRITES_NUMBER; li++)
        {
            Sprite p_Sprite = ap_Sprites[li];
            if (!p_Sprite.lg_SpriteActive) continue;
            if (p_Sprite.processAnimation())
            {
                switch (p_Sprite.i_ObjectType)
                {
                    case SPRITE_HUNTER_FIRE:
                        activateSprite(p_Sprite, SPRITE_HUNTER_BACK);
                        break;
                }
            }
            switch (p_Sprite.i_ObjectType)
            {
                case SPRITE_BULLET:
                    p_Sprite.setMainPointXY(p_Sprite.i_mainX, p_Sprite.i_mainY + BULLET_SPEED);
                    {

                        if(pPlayerSprite.i_mainY - (pPlayerSprite.i_height>>2) < I8_SCREEN_HEIGHT)
                        {
                          if(p_Sprite.isCollided(pPlayerSprite) && pPlayerSprite.i_ObjectType != SPRITE_CROCODILE_DIVE)
                          {
                              m_pAbstractGameActionListener.processGameAction(GAMEACTION_CROCODILE_DEAD);
                              activateSprite(pPlayerSprite, SPRITE_CROCODILE_DEAD);
                              deactivateSprite(p_Sprite);
                          }
                        }
                        if((p_Sprite.i_mainY + p_Sprite.i_height / 2) > I8_SCREEN_HEIGHT)
                        {
                            deactivateSprite(p_Sprite);
                        }
                    }
                    break;
                case SPRITE_HUNTER:
                    if((p_Sprite.i_mainY + p_Sprite.i_height / 2) >= SEA_LINE)
                    {
                        m_pAbstractGameActionListener.processGameAction(GAMEACTION_HUNTER_STAY);
                        activateSprite(p_Sprite, SPRITE_HUNTER_STAY);
                        p_Sprite.i_ObjectDelay = HUNTER_STAY_DELAY;
                    }
                    else
                    {
                        p_Sprite.setMainPointXY(p_Sprite.i_mainX, p_Sprite.i_mainY + HUNTER_SPEED);
                    }
                    break;
                case SPRITE_HUNTER_STAY:
                    if(p_Sprite.i_ObjectDelay > 0)
                    {
                        p_Sprite.i_ObjectDelay--;
                    }
                    else
                    {
                        activateSprite(p_Sprite, SPRITE_HUNTER_BACK);
                    }
                    if( (pPlayerSprite.i_mainX > (p_Sprite.i_mainX - p_Sprite.i_width / 2)) && (pPlayerSprite.i_mainX < (p_Sprite.i_mainX + p_Sprite.i_width / 2)) )
                    {
                        generateBullet(p_Sprite);
                        activateSprite(p_Sprite, SPRITE_HUNTER_FIRE);
                    }
                    break;
                case SPRITE_HUNTER_BACK:
                    if((p_Sprite.i_mainY + p_Sprite.i_height / 2) < 0)
                    {
                        switch(p_Sprite.i_mainX)
                        {
                            case LANE_X1:
                                unlockLocation(0);
                                break;
                            case LANE_X2:
                                unlockLocation(1);
                                break;
                            case LANE_X3:
                                unlockLocation(2);
                                break;
                            case LANE_X4:
                                unlockLocation(3);
                                break;
                            case LANE_X5:
                                unlockLocation(4);
                                break;
                            case LANE_X6:
                                unlockLocation(5);
                                break;
                            case LANE_X7:
                                unlockLocation(6);
                                break;
                            case LANE_X8:
                                unlockLocation(7);
                                break;
                        }
                        deactivateSprite(p_Sprite);
                    }
                    else
                    {
                        p_Sprite.setMainPointXY(p_Sprite.i_mainX, p_Sprite.i_mainY - HUNTER_SPEED);
                    }
                    break;
                case SPRITE_ANIMAL_01:
                case SPRITE_ANIMAL_02:
                case SPRITE_ANIMAL_03:
                case SPRITE_ANIMAL_04:
                case SPRITE_ANIMAL_05:
                case SPRITE_ANIMAL_06:
                case SPRITE_ANIMAL_07:
                case SPRITE_ANIMAL_08:
                    switch(p_Sprite.i_ObjectState)
                    {
                        case STATE_ANIMAL_MOVE:
                            if((p_Sprite.i_mainY + p_Sprite.i_height / 2) >= SEA_LINE)
                            {
                                p_Sprite.i_ObjectState = STATE_ANIMAL_STAY;
                                p_Sprite.i_ObjectDelay = STAY_DELAY;
                            }
                            else
                            {
                                p_Sprite.setMainPointXY(p_Sprite.i_mainX, p_Sprite.i_mainY + ANIMAL_SPEED);
                            }
                            break;
                        case STATE_ANIMAL_STAY:
                            if(p_Sprite.i_ObjectDelay > 0)
                            {
                                p_Sprite.i_ObjectDelay--;
                            }
                            else
                            {
                                p_Sprite.i_ObjectState = STATE_ANIMAL_DRINK;
                                p_Sprite.i_ObjectDelay = DRINK_DELAY;
                                m_pAbstractGameActionListener.processGameAction(GAMEACTION_ANIMAL_GULP);

                            }
                            if(p_Sprite.isCollided(pPlayerSprite))
                            {
                                if(pPlayerSprite.i_ObjectType != SPRITE_CROCODILE)
                                {
                                    p_Sprite.i_ObjectDelay = 0;
                                    p_Sprite.i_ObjectState = STATE_ANIMAL_BACK;
                                }
                            }
                            break;
                        case STATE_ANIMAL_DRINK:
                            if(p_Sprite.i_ObjectDelay > 0)
                            {
                                p_Sprite.i_ObjectDelay--;
                            }
                            else
                            {
                                p_Sprite.i_ObjectState = STATE_ANIMAL_BACK;
                            }
                            if(p_Sprite.isCollided(pPlayerSprite))
                            {
                                if(pPlayerSprite.i_ObjectType == SPRITE_CROCODILE)
                                {
                                    m_pAbstractGameActionListener.processGameAction(GAMEACTION_ANIMAL_DEAD);
                                    p_Sprite.i_ObjectState = STATE_ANIMAL_BITTEN;
                                    p_Sprite.i_ObjectDelay = DEAD_DELAY;
                                    m_pAbstractGameActionListener.processGameAction(GAMEACTION_CROCODILE_ATTACK);
                                    activateSprite(pPlayerSprite, SPRITE_CROCODILE_BITE);
                                    switch(p_Sprite.i_ObjectType)
                                    {
                                        case SPRITE_ANIMAL_01:
                                            addScore(SCORES_ANIMAL_01);
                                            break;
                                        case SPRITE_ANIMAL_02:
                                            addScore(SCORES_ANIMAL_02);
                                            break;
                                        case SPRITE_ANIMAL_03:
                                            addScore(SCORES_ANIMAL_03);
                                            break;
                                        case SPRITE_ANIMAL_04:
                                            addScore(SCORES_ANIMAL_04);
                                            break;
                                        case SPRITE_ANIMAL_05:
                                            addScore(SCORES_ANIMAL_05);
                                            break;
                                        case SPRITE_ANIMAL_06:
                                            addScore(SCORES_ANIMAL_06);
                                            break;
                                        case SPRITE_ANIMAL_07:
                                            addScore(SCORES_ANIMAL_07);
                                            break;
                                        case SPRITE_ANIMAL_08:
                                            addScore(SCORES_ANIMAL_08);
                                            break;
                                    }
                                }
                            }
                            break;
                        case STATE_ANIMAL_BITTEN:
                            if(p_Sprite.i_ObjectDelay > 0)
                            {
                                p_Sprite.i_ObjectDelay--;
                            }
                            else
                            {
                                switch(p_Sprite.i_mainX)
                                {
                                    case LANE_X1:
                                        unlockLocation(0);
                                        break;
                                    case LANE_X2:
                                        unlockLocation(1);
                                        break;
                                    case LANE_X3:
                                        unlockLocation(2);
                                        break;
                                    case LANE_X4:
                                        unlockLocation(3);
                                        break;
                                    case LANE_X5:
                                        unlockLocation(4);
                                        break;
                                    case LANE_X6:
                                        unlockLocation(5);
                                        break;
                                    case LANE_X7:
                                        unlockLocation(6);
                                        break;
                                    case LANE_X8:
                                        unlockLocation(7);
                                        break;
                                }
                                deactivateSprite(p_Sprite);
                            }
                            break;
                        case STATE_ANIMAL_BACK:
                            if((p_Sprite.i_mainY + p_Sprite.i_height / 2) < 0)
                            {
                                switch(p_Sprite.i_mainX)
                                {
                                    case LANE_X1:
                                        unlockLocation(0);
                                        break;
                                    case LANE_X2:
                                        unlockLocation(1);
                                        break;

                                    case LANE_X3:
                                        unlockLocation(2);
                                        break;
                                    case LANE_X4:
                                        unlockLocation(3);
                                        break;
                                    case LANE_X5:
                                        unlockLocation(4);
                                        break;
                                    case LANE_X6:
                                        unlockLocation(5);
                                        break;
                                    case LANE_X7:
                                        unlockLocation(6);
                                        break;
                                    case LANE_X8:
                                        unlockLocation(7);
                                        break;
                                }
                                deactivateSprite(p_Sprite);
                            }
                            else
                            {
                                p_Sprite.setMainPointXY(p_Sprite.i_mainX, p_Sprite.i_mainY - ANIMAL_SPEED);
                            }
                            break;
                    }
                    break;
                default:
                    break;
            }
        }
    }

    private boolean processPlayer(int _buttonState)
    {
        int i_x = pPlayerSprite.i_ScreenX;
        int i_y = pPlayerSprite.i_ScreenY;

        int i_mx = pPlayerSprite.i_mainX;
        int i_my = pPlayerSprite.i_mainY;

        int i_w = pPlayerSprite.i_width;
        int i_h = pPlayerSprite.i_height;

        if(pPlayerSprite.processAnimation())
        {
            switch(pPlayerSprite.i_ObjectType)
            {
                case SPRITE_CROCODILE_SURFACE:
                    activateSprite(pPlayerSprite, SPRITE_CROCODILE_DIVE);
                    m_pAbstractGameActionListener.processGameAction(GAMEACTION_CROCODILE_DIVE);
                    break;
                case SPRITE_CROCODILE_DIVE:
                    activateSprite(pPlayerSprite, SPRITE_CROCODILE);
                    generateCrocodile();
                    break;
                case SPRITE_CROCODILE_BITE:
                    activateSprite(pPlayerSprite, SPRITE_CROCODILE_KEEP_ANIMAL);
                    m_pAbstractGameActionListener.processGameAction(GAMEACTION_CROCODILE_BITE);
                    break;
                case SPRITE_CROCODILE_KEEP_ANIMAL:
                    activateSprite(pPlayerSprite, SPRITE_CROCODILE_BACK);
                    m_pAbstractGameActionListener.processGameAction(GAMEACTION_CROCODILE_BACK);
                    break;
                case SPRITE_CROCODILE_DEAD:
                    return false;
                default:
                    break;
            }
        }

        i_mx = pPlayerSprite.i_mainX;
        i_my = pPlayerSprite.i_mainY;

        switch(pPlayerSprite.i_ObjectType)
        {
            case SPRITE_CROCODILE_BITE:
                i_my -= CROCODILE_ATTACK_SPEED;
                pPlayerSprite.setMainPointXY(i_mx, i_my);
                break;
            case SPRITE_CROCODILE_BACK:
                i_my += CROCODILE_SPEED;
                pPlayerSprite.setMainPointXY(i_mx, i_my);
                if((i_my - i_h / 2) > SEA_LINE)
                {
                    activateSprite(pPlayerSprite, SPRITE_CROCODILE_DIVE);
                }
                break;
            case SPRITE_CROCODILE:
                if((i_my - i_h / 2) > (SEA_LINE + HUNTING_SPACE) )
                {
                    if ((_buttonState & MoveObject.BUTTON_LEFT) != 0)
                    {
                        if(i_mx > (0x0000 + LEFT_MARGIN))
                        {
                            activateSprite(pPlayerSprite, SPRITE_CROCODILE_LEFT);
                            pPlayerSprite.i_ObjectState = MOVE_STEPS;
                            i_HSpeed = -HSPEED;
                        }
                    }
                    else if ((_buttonState & MoveObject.BUTTON_RIGHT) != 0)
                    {
                        if(i_mx < (I8_SCREEN_WIDTH - RIGHT_MARGIN))
                        {
                            activateSprite(pPlayerSprite, SPRITE_CROCODILE_RIGHT);
                            pPlayerSprite.i_ObjectState = MOVE_STEPS;
                            i_HSpeed = HSPEED;
                        }
                    }
                }
                i_my -= CROCODILE_SPEED;
                pPlayerSprite.setMainPointXY(i_mx, i_my);
                if((i_my - i_h / 2) < SEA_LINE)
                {

                   if(--i_MissCounter <= 0)
                   {
                              i_MissCounter = 0;
                              activateSprite(pPlayerSprite, SPRITE_CROCODILE_DEAD);
                              m_pAbstractGameActionListener.processGameAction(GAMEACTION_CROCODILE_DEAD);
                   }
                     else
                           {
                                activateSprite(pPlayerSprite, SPRITE_CROCODILE_SURFACE);
                           }
                }
                break;
            case SPRITE_CROCODILE_LEFT:
                i_mx += i_HSpeed;
                i_my -= CROCODILE_SPEED>>1;
                pPlayerSprite.setMainPointXY(i_mx, i_my);
                pPlayerSprite.i_ObjectState--;

                if(pPlayerSprite.i_ObjectState == 0)
                {
                    if (  ((_buttonState & MoveObject.BUTTON_LEFT) != 0) && ((i_my - i_h / 2) > (SEA_LINE + HUNTING_SPACE) ) && (i_mx > (0x0000 + LEFT_MARGIN))  )
                    {
                        pPlayerSprite.i_ObjectState = MOVE_STEPS;
                    }
                    else
                    {
                        activateSprite(pPlayerSprite, SPRITE_CROCODILE);
                    }
                }
                break;
            case SPRITE_CROCODILE_RIGHT:
                i_mx += i_HSpeed;
                i_my -= CROCODILE_SPEED>>1;
                pPlayerSprite.setMainPointXY(i_mx, i_my);
                pPlayerSprite.i_ObjectState--;

                if(pPlayerSprite.i_ObjectState == 0)
                {
                    if (  ((_buttonState & MoveObject.BUTTON_RIGHT) != 0) && ((i_my - i_h / 2) > (SEA_LINE + HUNTING_SPACE) ) && (i_mx < (I8_SCREEN_WIDTH - RIGHT_MARGIN)) )
                    {
                        pPlayerSprite.i_ObjectState = MOVE_STEPS;
                    }
                    else
                    {
                        activateSprite(pPlayerSprite, SPRITE_CROCODILE);
                    }
                }
                break;

        }
        return true;
    }

    private void generateCrocodile()
    {
        int x = 0;
        int y = I8_SCREEN_HEIGHT + pPlayerSprite.i_height * 2 - 0x0100;

        switch(getRandomInt(7))
        {
            case 0:
                x = LANE_X1;
                break;
            case 1:
                x = LANE_X2;
                break;
            case 2:
                x = LANE_X3;
                break;
            default:
            case 3:
                x = LANE_X4;
                break;
            case 4:
                x = LANE_X5;
                break;
            case 5:
                x = LANE_X6;
                break;
            case 6:
                x = LANE_X7;
                break;
            case 7:
                x = LANE_X8;
                break;
        }
        pPlayerSprite.setMainPointXY(x,y);
    }

    private void generateBullet(Sprite hunter)
    {
        Sprite p_emptySprite = getInactiveSprite();
        if (p_emptySprite != null)
        {
            int y = hunter.i_mainY + BULLET_Y_OFFSET;
            int x = hunter.i_mainX + BULLET_X_OFFSET;
            int type = SPRITE_BULLET;

            activateSprite(p_emptySprite, type);
            p_emptySprite.lg_SpriteActive = true;
            p_emptySprite.setMainPointXY(x, y);
            m_pAbstractGameActionListener.processGameAction(GAMEACTION_HUNTER_FIRE);
        }
    }

    private void generateHunter()
    {
        if((i_LastHunterTime + i_HunterDelay) < i_GameTimer )
        {
            Sprite p_emptySprite = getInactiveSprite();
            if (p_emptySprite != null)
            {
                int y = 0; 
                int x = I8_SCREEN_WIDTH / 2;
                int type = SPRITE_HUNTER;

                int a = findLocation(getRandomInt(7));
                if(a < 0) return;

                i_LastHunterTime = i_GameTimer;
                lockLocation(a);

                switch(a)
                {
                    case 0:
                        x = LANE_X1;
                        break;
                    case 1:
                        x = LANE_X2;
                        break;
                    case 2:
                        x = LANE_X3;
                        break;
                    default:
                    case 3:
                        x = LANE_X4;
                        break;
                    case 4:
                        x = LANE_X5;
                        break;
                    case 5:
                        x = LANE_X6;
                        break;
                    case 6:
                        x = LANE_X7;
                        break;
                    case 7:
                        x = LANE_X8;
                        break;
                }

                activateSprite(p_emptySprite, type);
                p_emptySprite.lg_SpriteActive = true;
                p_emptySprite.setMainPointXY(x, 0 - p_emptySprite.i_height / 2);
                m_pAbstractGameActionListener.processGameAction(GAMEACTION_HUNTER);
            }
        }
    }


    private void generateSprite()
    {
        if((i_LastTime + i_GenerateDelay) < i_GameTimer )
        {
            i_LastTime = i_GameTimer;
            Sprite p_emptySprite = getInactiveSprite();
            if (p_emptySprite != null)
            {
                int y = 0; 
                int x = I8_SCREEN_WIDTH / 2;
                int type = SPRITE_ANIMAL_01;

                switch(getRandomInt(7))
                {
                    default:
                    case 0:
                        type = SPRITE_ANIMAL_01;
                        break;
                    case 1:
                        type = SPRITE_ANIMAL_02;
                        break;
                    case 2:
                        type = SPRITE_ANIMAL_03;
                        break;
                    case 3:
                        type = SPRITE_ANIMAL_04;
                        break;
                    case 4:
                        type = SPRITE_ANIMAL_05;
                        break;
                    case 5:
                        type = SPRITE_ANIMAL_06;
                        break;
                    case 6:
                        type = SPRITE_ANIMAL_07;
                        break;
                    case 7:
                        type = SPRITE_ANIMAL_08;
                        break;
                }

                int a = findLocation(getRandomInt(7));
                if(a < 0) return;

                lockLocation(a);

                switch(a)
                {
                    case 0:
                        x = LANE_X1;
                        break;
                    case 1:
                        x = LANE_X2;
                        break;
                    case 2:
                        x = LANE_X3;
                        break;
                    default:
                    case 3:
                        x = LANE_X4;
                        break;
                    case 4:
                        x = LANE_X5;
                        break;
                    case 5:
                        x = LANE_X6;
                        break;
                    case 6:
                        x = LANE_X7;
                        break;
                    case 7:
                        x = LANE_X8;
                        break;
                }

                activateSprite(p_emptySprite, type);
                p_emptySprite.lg_SpriteActive = true;
                p_emptySprite.setMainPointXY(x, 0 - p_emptySprite.i_height / 2);
                p_emptySprite.i_ObjectState = STATE_ANIMAL_MOVE;
                m_pAbstractGameActionListener.processGameAction(GAMEACTION_ANIMAL_GENERATED);
            }
        }
    }


public boolean newGameSession(int _gameAreaWidth, int _gameAreaHeight, int _gameLevel, CAbstractPlayer[] _players,startup _listener, String _staticArrayResourceName)
    {
        super.newGameSession(_gameAreaWidth, _gameAreaHeight, _gameLevel, _players, _listener, _staticArrayResourceName);

        I8_SCREEN_WIDTH = _gameAreaWidth<<8;
        I8_SCREEN_HEIGHT = _gameAreaHeight<<8;

        switch (_gameLevel)
        {
            case LEVEL_EASY:
                {
                    i_timedelay = LEVEL_EASY_TIMEDELAY;
                    i_playerAttemptions = LEVEL_EASY_ATTEMPTIONS;
                    i_GenerateDelay = LEVEL_EASY_GENERATION;
                    i_HunterDelay = LEVEL_EASY_HUNTER;
                }
                break;
            case LEVEL_NORMAL:
                {
                    i_timedelay = LEVEL_NORMAL_TIMEDELAY;
                    i_playerAttemptions = LEVEL_NORMAL_ATTEMPTIONS;
                    i_GenerateDelay = LEVEL_NORMAL_GENERATION;
                    i_HunterDelay = LEVEL_NORMAL_HUNTER;
                }
                break;
            case LEVEL_HARD:
                {
                    i_timedelay = LEVEL_HARD_TIMEDELAY;
                    i_playerAttemptions = LEVEL_HARD_ATTEMPTIONS;
                    i_GenerateDelay = LEVEL_HARD_GENERATION;
                    i_HunterDelay = LEVEL_HARD_HUNTER;
                }
                break;
        }

        i_GameTimer = 0;
        i_LastTime = i_GameTimer;
        i_LastHunterTime = i_GameTimer;
        i_HSpeed = 0;
        i_MissCounter = MAXIMUM_ALLOWED_MISSES;

        resetLocation();

        activateSprite(pPlayerSprite, SPRITE_CROCODILE);
        generateCrocodile();

        return true;
    }

    public void resumeGameAfterPlayerLostOrPaused()
    {
        super.resumeGameAfterPlayerLostOrPaused();

        deinitState();
        initState();

        i_GameTimer = 0;
        i_LastTime = i_GameTimer;
        i_LastHunterTime = i_GameTimer;
        i_MissCounter = MAXIMUM_ALLOWED_MISSES;
        i_HSpeed = 0;

        resetLocation();

        activateSprite(pPlayerSprite, SPRITE_CROCODILE);
        generateCrocodile();

        switch(getGameDifficultLevel())
        {
            case LEVEL_EASY:
                break;
            case LEVEL_NORMAL:
                break;
            case LEVEL_HARD:
                break;
        }

    }

    public String getGameTextID()
    {
        return "CROCODILE";
    }

    public boolean initState()
    {
        ap_Sprites = new Sprite[MAX_SPRITES_NUMBER];
        for (int li = 0; li < MAX_SPRITES_NUMBER; li++) ap_Sprites[li] = new Sprite(li);


        pPlayerSprite = new Sprite(-1);

        p_MoveObject = new MoveObject();

        return true;
    }

    public void deinitState()
    {

        ap_Sprites = null;
        pPlayerSprite = null;

        Runtime.getRuntime().gc();
    }

    public int _getMaximumDataSize()
    {
        return (MAX_SPRITES_NUMBER+1)*(Sprite.DATASIZE_BYTES+1)+40;
    }

    public void _saveGameData(DataOutputStream _outputStream) throws IOException
    {
        for(int li=0;li<MAX_SPRITES_NUMBER;li++)
        {
            _outputStream.writeByte(ap_Sprites[li].i_ObjectType);
            ap_Sprites[li].writeSpriteToStream(_outputStream);
        }

        _outputStream.writeByte(pPlayerSprite.i_ObjectType);
        pPlayerSprite.writeSpriteToStream(_outputStream);

        for(int i=0; i<MAX_LOCATIONS; i++)
        {
            _outputStream.writeBoolean(ab_Places[i]);
        }

        _outputStream.writeInt(i_playerAttemptions);
        _outputStream.writeInt(i_GameTimer);
        _outputStream.writeInt(i_LastTime);
        _outputStream.writeInt(i_GenerateDelay);
        _outputStream.writeInt(i_LastHunterTime);
        _outputStream.writeInt(i_HunterDelay);
        _outputStream.writeInt(i_HSpeed);
        _outputStream.writeInt(i_MissCounter);

    }

    public void _loadGameData(DataInputStream _inputStream) throws IOException
    {
        int i_type;
        for(int li=0;li<MAX_SPRITES_NUMBER;li++)
        {
            i_type = _inputStream.readByte();
            activateSprite(ap_Sprites[li],i_type);
            ap_Sprites[li].readSpriteFromStream(_inputStream);
        }

        i_type = _inputStream.readByte();
        activateSprite(pPlayerSprite,i_type);
        pPlayerSprite.readSpriteFromStream(_inputStream);

        for(int i=0; i<MAX_LOCATIONS; i++)
        {
            ab_Places[i] = _inputStream.readBoolean();
        }

        i_playerAttemptions = _inputStream.readInt();
        i_GameTimer = _inputStream.readInt();
        i_LastTime = _inputStream.readInt();
        i_GenerateDelay = _inputStream.readInt();
        i_LastHunterTime = _inputStream.readInt();
        i_HunterDelay = _inputStream.readInt();
        i_HSpeed = _inputStream.readInt();
        i_MissCounter = _inputStream.readInt();
    }

    public int nextGameStep()
    {
        CAbstractPlayer p_player = m_pPlayerList[0];
        p_player.nextPlayerMove(this, p_MoveObject);

        processSprites();

        if(!processPlayer(p_MoveObject.i_buttonState))
        {
            i_playerAttemptions --;

            if (i_playerAttemptions == 0)
            {
               m_pWinningList = null;
               setGameState(GAMEWORLDSTATE_GAMEOVER);
            }
            else
            {
               setGameState(GAMEWORLDSTATE_PLAYERLOST);
            }
        }

        generateSprite();
        generateHunter();

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
