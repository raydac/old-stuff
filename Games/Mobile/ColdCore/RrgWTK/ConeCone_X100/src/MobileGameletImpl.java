
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.DataInputStream;


public class MobileGameletImpl extends MobileGamelet
{
    public static final int RIGHT_MARGIN = (16 << 8);
    public static final int LEFT_MARGIN = (16 << 8);

    public static final int CROW_LEFT_OFFSET_X = (16 << 8);
    public static final int CROW_RIGHT_OFFSET_X = (16 << 8);


    public static final int SQUIRREL_INITFALL_SPEED = -6<<8;
    public static final int SQUIRREL_GRAVITY = 0x300;

    public static final int HOLLOW1_X = (55 << 8);
    public static final int HOLLOW1_Y = (103 << 8);

    public static final int HOLLOW2_X = (55 << 8);
    public static final int HOLLOW2_Y = (48 << 8);

    public static final int HOLLOW3_X = (71 << 8);
    public static final int HOLLOW3_Y = (79 << 8);

    public static final int HOLLOW4_X = (71 << 8);
    public static final int HOLLOW4_Y = (23 << 8);

    public static final int CROW_LEFT_Y1 = (HOLLOW1_Y - CROW_LEFT_OFFSET_X);
    public static final int CROW_LEFT_Y2 = (HOLLOW2_Y - CROW_LEFT_OFFSET_X);
    public static final int CROW_RIGHT_Y3 = (HOLLOW3_Y - CROW_RIGHT_OFFSET_X);
    public static final int CROW_RIGHT_Y4 = (HOLLOW4_Y - CROW_RIGHT_OFFSET_X);


    public static final int CONE1_X = (20 << 8);   
    public static final int CONE1_Y = (68 << 8);

    public static final int CONE2_X = (34 << 8);
    public static final int CONE2_Y = (67 << 8);

    public static final int CONE3_X = (20 << 8);
    public static final int CONE3_Y = (122 << 8);

    public static final int CONE4_X = (36 << 8);
    public static final int CONE4_Y = (121 << 8);

    public static final int CONE5_X = (90 << 8);   
    public static final int CONE5_Y = (43 << 8);

    public static final int CONE6_X = (105 << 8);
    public static final int CONE6_Y = (42 << 8);

    public static final int CONE7_X = (92 << 8);
    public static final int CONE7_Y = (97 << 8);

    public static final int CONE8_X = (107 << 8);
    public static final int CONE8_Y = (96 << 8);

    public static final int INIT_PLAYER_X = (HOLLOW2_X);
    public static final int INIT_PLAYER_Y = (HOLLOW2_Y);

    public static final int INIT_CROW_FORCE = 2;

    public static final int MOVE_RND = 64;

    public static final int CONE_SPEED = (4 << 8);
    public static final int CROW_SPEED = (1 << 8);
    public static final int CROW_FLY_IN_SPEED = (1 << 8);
    public static final int CROW_FLY_AWAY_SPEED = (3 << 8);


    public static final int SQUIRREL_JUMP_SPEED = (4 << 8);
    public static final int SQUIRREL_JUMP_STEPS = 4;

    private static int I8_SCREEN_WIDTH;

    private static int I8_SCREEN_HEIGHT;

    public MoveObject p_MoveObject;

    public static final int GAMEACTION_SQUIRREL_JUMP                = 0;
    public static final int GAMEACTION_SQUIRREL_TELEPORTED          = 1;
    public static final int GAMEACTION_SQUIRREL_FIRE                = 2;
    public static final int GAMEACTION_SQUIRREL_DEAD                = 3;

    public static final int GAMEACTION_CROW_GENERATED               = 4;
    public static final int GAMEACTION_CROW_FLY_AWAY                = 5;
    public static final int GAMEACTION_CROW_ATTACK                  = 6;
    public static final int GAMEACTION_CROW_PAIN                    = 7;
    public static final int GAMEACTION_CROW_WIN                     = 8;
    public static final int GAMEACTION_CROW_WIN_CROAK               = 9;

    public static final int GAMEACTION_CONE_GENERATED               = 10;
    public static final int GAMEACTION_CONE_READY                   = 11;
    public static final int GAMEACTION_CONE_TAKEN                   = 12;
    public static final int GAMEACTION_CONE_FIRE_GENERATED          = 13;


    public static final int SCORES_FIRE_CONE = 5;
    public static final int SCORES_CROW = 20;

    private int i_timedelay;

    public int i_playerAttemptions;

    public int i_GameTimer;

    public int i_LastTime;
    public int i_GenerateDelay;

    public int i_LastConeTime;
    public int i_ConeDelay;

    public int i_ConeCounter;

    public int i_MaxCones;

    public static final int LEVEL_EASY = 0;

    private static final int LEVEL_EASY_ATTEMPTIONS = 4;

    private static final int LEVEL_EASY_GENERATION = 130;
    private static final int LEVEL_EASY_CONE_GENERATION = 40;
    private static final int LEVEL_EASY_CONES = 4;

    private static final int LEVEL_EASY_TIMEDELAY = 100;

    public static final int LEVEL_NORMAL = 1;

    private static final int LEVEL_NORMAL_ATTEMPTIONS = 3;

    private static final int LEVEL_NORMAL_GENERATION = 120;
    private static final int LEVEL_NORMAL_CONE_GENERATION = 40;
    private static final int LEVEL_NORMAL_CONES = 3;

    private static final int LEVEL_NORMAL_TIMEDELAY = 90;

    public static final int LEVEL_HARD = 2;

    private static final int LEVEL_HARD_ATTEMPTIONS = 2;

    private static final int LEVEL_HARD_GENERATION = 100;
    private static final int LEVEL_HARD_CONE_GENERATION = 35;
    private static final int LEVEL_HARD_CONES = 2;

    private static final int LEVEL_HARD_TIMEDELAY = 80;


    public Sprite pPlayerSprite;

    public static final int MAX_SPRITES_NUMBER = 18;

    public Sprite[] ap_Sprites;

    private static final int SPRITEDATALENGTH = 10;

    public static final int SPRITE_SQUIRREL_LEFT = 0;
    public static final int SPRITE_SQUIRREL_RIGHT = 1;
    public static final int SPRITE_SQUIRREL_JUMP_LEFT = 2;
    public static final int SPRITE_SQUIRREL_JUMP_RIGHT = 3;
    public static final int SPRITE_SQUIRREL_TAKE_LEFT = 4;
    public static final int SPRITE_SQUIRREL_TAKE_RIGHT = 5;
    public static final int SPRITE_SQUIRREL_FIRE_LEFT = 6;
    public static final int SPRITE_SQUIRREL_FIRE_RIGHT = 7;
    public static final int SPRITE_SQUIRREL_DEAD = 8;
    public static final int SPRITE_SQUIRREL_FALL = 9;
    public static final int SPRITE_CONE = 10;
    public static final int SPRITE_CONE_APPEAR = 11;
    public static final int SPRITE_CONE_FIRE = 12;
    public static final int SPRITE_CROW_LEFT = 13;
    public static final int SPRITE_CROW_LEFT_MOVE = 14;
    public static final int SPRITE_CROW_LEFT_FLY_IN = 15;
    public static final int SPRITE_CROW_LEFT_ATTACK = 16;
    public static final int SPRITE_CROW_LEFT_PAIN = 17;
    public static final int SPRITE_CROW_LEFT_FLY = 18;
    public static final int SPRITE_CROW_RIGHT = 19;
    public static final int SPRITE_CROW_RIGHT_MOVE = 20;
    public static final int SPRITE_CROW_RIGHT_FLY_IN = 21;
    public static final int SPRITE_CROW_RIGHT_ATTACK = 22;
    public static final int SPRITE_CROW_RIGHT_PAIN = 23;
    public static final int SPRITE_CROW_RIGHT_FLY = 24;
    public static final int SPRITE_CROW_WIN = 25;
    public static final int SPRITE_HOLLOW = 26;


    private static final int [] ai_SpriteParameters = new int[]
    {
        0x1000, 0x1000, -0x001, 0x0000, 0x1000, 0x1000, 1, 1, Sprite.SPRITE_ALIGN_CENTER, Sprite.ANIMATION_FROZEN,
        0x1000, 0x1000, -0x001, 0x0000, 0x1000, 0x1000, 1, 1, Sprite.SPRITE_ALIGN_CENTER, Sprite.ANIMATION_FROZEN,
        0x1000, 0x1000, -0x001, 0x0000, 0x1000, 0x1000, 2, 3, Sprite.SPRITE_ALIGN_CENTER, Sprite.ANIMATION_CYCLIC,
        0x1000, 0x1000, -0x001, 0x0000, 0x1000, 0x1000, 2, 3, Sprite.SPRITE_ALIGN_CENTER, Sprite.ANIMATION_CYCLIC,
        0x1000, 0x1000, -0x001, 0x0000, 0x1000, 0x1000, 3, 2, Sprite.SPRITE_ALIGN_CENTER, Sprite.ANIMATION_FROZEN,
        0x1000, 0x1000, -0x001, 0x0000, 0x1000, 0x1000, 3, 2, Sprite.SPRITE_ALIGN_CENTER, Sprite.ANIMATION_FROZEN,
        0x1000, 0x1000, -0x001, 0x0000, 0x1000, 0x1000, 2, 3, Sprite.SPRITE_ALIGN_CENTER, Sprite.ANIMATION_FROZEN,
        0x1000, 0x1000, -0x001, 0x0000, 0x1000, 0x1000, 2, 3, Sprite.SPRITE_ALIGN_CENTER, Sprite.ANIMATION_FROZEN,
        0x1000, 0x1000, -0x001, 0x0000, 0x1000, 0x1000, 2, 3, Sprite.SPRITE_ALIGN_CENTER, Sprite.ANIMATION_FROZEN,
        0x1000, 0x1000, -0x001, 0x0000, 0x1000, 0x1000, 5, 5, Sprite.SPRITE_ALIGN_CENTER, Sprite.ANIMATION_FROZEN,
        0x0600, 0x1000, -0x001, 0x0000, 0x0400, 0x1A00, 1, 1, Sprite.SPRITE_ALIGN_CENTER, Sprite.ANIMATION_FROZEN,
        0x0600, 0x0800, -0x001, 0x0000, 0x0400, 0x0800, 4, 3, Sprite.SPRITE_ALIGN_CENTER, Sprite.ANIMATION_FROZEN,
        0x0600, 0x0600, -0x001, 0x0000, 0x0600, 0x0600, 8, 1, Sprite.SPRITE_ALIGN_CENTER, Sprite.ANIMATION_CYCLIC,
        0x1000, 0x1000, -0x001, 0x0000, 0x0A00, 0x1000, 1, 1, Sprite.SPRITE_ALIGN_CENTER, Sprite.ANIMATION_FROZEN,
        0x1000, 0x1000, -0x001, 0x0000, 0x0A00, 0x1000, 2, 2, Sprite.SPRITE_ALIGN_CENTER, Sprite.ANIMATION_FROZEN,
        0x1000, 0x1000, -0x001, 0x0000, 0x1000, 0x1000, 1, 1, Sprite.SPRITE_ALIGN_CENTER, Sprite.ANIMATION_FROZEN,
        0x1000, 0x1000, -0x001, 0x0000, 0x1000, 0x1000, 4, 1, Sprite.SPRITE_ALIGN_CENTER, Sprite.ANIMATION_FROZEN,
        0x1000, 0x1000, -0x001, 0x0000, 0x1000, 0x1000, 2, 2, Sprite.SPRITE_ALIGN_CENTER, Sprite.ANIMATION_FROZEN,
        0x1000, 0x1000, -0x001, 0x0000, 0x1000, 0x1000, 2, 1, Sprite.SPRITE_ALIGN_CENTER, Sprite.ANIMATION_FROZEN,
        0x1000, 0x1000, -0x001, 0x0000, 0x0A00, 0x1000, 1, 1, Sprite.SPRITE_ALIGN_CENTER, Sprite.ANIMATION_FROZEN,
        0x1000, 0x1000, -0x001, 0x0000, 0x0A00, 0x1000, 2, 2, Sprite.SPRITE_ALIGN_CENTER, Sprite.ANIMATION_FROZEN,
        0x1000, 0x1000, -0x001, 0x0000, 0x1000, 0x1000, 1, 1, Sprite.SPRITE_ALIGN_CENTER, Sprite.ANIMATION_FROZEN,
        0x1000, 0x1000, -0x001, 0x0000, 0x1000, 0x1000, 4, 1, Sprite.SPRITE_ALIGN_CENTER, Sprite.ANIMATION_FROZEN,
        0x1000, 0x1000, -0x001, 0x0000, 0x1000, 0x1000, 2, 2, Sprite.SPRITE_ALIGN_CENTER, Sprite.ANIMATION_FROZEN,
        0x1000, 0x1000, -0x001, 0x0000, 0x1000, 0x1000, 2, 1, Sprite.SPRITE_ALIGN_CENTER, Sprite.ANIMATION_FROZEN,
        0x1000, 0x1000, -0x001, 0x0000, 0x1000, 0x1000, 4, 5, Sprite.SPRITE_ALIGN_CENTER, Sprite.ANIMATION_FROZEN,
        0x1000, 0x1000, -0x001, 0x0000, 0x0A00, 0x0E00, 1, 1, Sprite.SPRITE_ALIGN_CENTER, Sprite.ANIMATION_FROZEN,
    };


    public static final int CROW_WIN_CROAC_FRAME = ai_SpriteParameters[SPRITEDATALENGTH * SPRITE_CROW_WIN + 6] - 2;

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
        if(n < 0 || n >= MAX_LOCATIONS) return -1;

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

    private static final int MAX_BRANCHES = 4;

    private boolean ab_Branches[] = new boolean[]
    {
        true, true, true, true
    };

    private void resetBranches()
    {
        for(int i=0; i<MAX_BRANCHES; i++)
        {
            ab_Branches[i] = true;
        }
    }

    private int findBranch(int n)
    {
        if(n < 0 || n >= MAX_BRANCHES) return -1;

        for(int i=n; i<MAX_BRANCHES; i++)
        {
            if(ab_Branches[i])
            {
                return i;
            }
        }
        for(int i=0; i<=n; i++)
        {
            if(ab_Branches[i])
            {
                return i;
            }
        }
        return -1;
    }

    private void unlockBranch(int i)
    {
        if(i >= 0 && i < MAX_BRANCHES) ab_Branches[i] = true;
    }

    private void lockBranch(int i)
    {
        if(i >= 0 && i < MAX_BRANCHES) ab_Branches[i] = false;
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


        if(i8_cx < 0 || i8_cy < 0)
        {
            _sprite.setCollisionBounds((i8_w - i8_aw) >> 1, (i8_h - i8_ah) >> 1, i8_aw, i8_ah);
        }
        else
        {
            _sprite.setCollisionBounds(i8_cx, i8_cy, i8_aw, i8_ah);
        }

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
                    case SPRITE_CROW_LEFT_ATTACK:
                        activateSprite(p_Sprite, SPRITE_CROW_LEFT);
                        if(pPlayerSprite.i_ObjectType < SPRITE_SQUIRREL_DEAD)
                        {
                            activateSprite(pPlayerSprite, SPRITE_SQUIRREL_DEAD);
                        }
                        break;
                    case SPRITE_CROW_RIGHT_ATTACK:
                        activateSprite(p_Sprite, SPRITE_CROW_RIGHT);
                        if(pPlayerSprite.i_ObjectType < SPRITE_SQUIRREL_DEAD)
                        {
                            activateSprite(pPlayerSprite, SPRITE_SQUIRREL_DEAD);
                        }
                        break;
                    case SPRITE_CROW_WIN:
                        if(pPlayerSprite.i_ObjectType < SPRITE_SQUIRREL_DEAD)
                        {
                            activateSprite(pPlayerSprite, SPRITE_SQUIRREL_DEAD);
                        }
                        break;
                    case SPRITE_CROW_LEFT_MOVE:
                        activateSprite(p_Sprite, SPRITE_CROW_LEFT);
                        break;
                    case SPRITE_CROW_RIGHT_MOVE:
                        activateSprite(p_Sprite, SPRITE_CROW_RIGHT);
                        break;
                    case SPRITE_CROW_LEFT_PAIN:
                        addScore(SCORES_FIRE_CONE);
                        p_Sprite.i_ObjectState--;
                        if(p_Sprite.i_ObjectState > 0)
                        {
                            activateSprite(p_Sprite, SPRITE_CROW_LEFT);
                        }
                        else
                        {
                            switch(p_Sprite.i_mainY)
                            {
                                case HOLLOW1_Y:
                                    unlockBranch(0);
                                    break;
                                case HOLLOW2_Y:
                                    unlockBranch(1);
                                    break;
                            }
                            activateSprite(p_Sprite, SPRITE_CROW_LEFT_FLY);
                            addScore(SCORES_CROW);
                            m_pAbstractGameActionListener.processGameAction(GAMEACTION_CROW_FLY_AWAY);
                        }
                        break;
                    case SPRITE_CROW_RIGHT_PAIN:
                        addScore(SCORES_FIRE_CONE);
                        p_Sprite.i_ObjectState--;
                        if(p_Sprite.i_ObjectState > 0)
                        {
                            activateSprite(p_Sprite, SPRITE_CROW_RIGHT);
                        }
                        else
                        {
                            switch(p_Sprite.i_mainY)
                            {
                                case HOLLOW3_Y:
                                    unlockBranch(2);
                                    break;
                                case HOLLOW4_Y:
                                    unlockBranch(3);
                                    break;
                            }
                            activateSprite(p_Sprite, SPRITE_CROW_RIGHT_FLY);
                            addScore(SCORES_CROW);
                            m_pAbstractGameActionListener.processGameAction(GAMEACTION_CROW_FLY_AWAY);
                        }
                        break;
                    case SPRITE_CONE_APPEAR:
                        activateSprite(p_Sprite, SPRITE_CONE);
                        m_pAbstractGameActionListener.processGameAction(GAMEACTION_CONE_READY);
                        break;
                }
            }
            switch (p_Sprite.i_ObjectType)
            {
                case SPRITE_CROW_LEFT:
                    if(getRandomInt(MOVE_RND) == (MOVE_RND / 2))
                    {
                        activateSprite(p_Sprite, SPRITE_CROW_LEFT_MOVE);
                    }
                    else
                    {
                        for (int ix = 0; ix < MAX_SPRITES_NUMBER; ix++)
                        {
                            Sprite p_hollow = ap_Sprites[ix];
                            if (!p_hollow.lg_SpriteActive) continue;
                            if(p_hollow.i_ObjectType == SPRITE_HOLLOW)
                            {
                                if(p_Sprite.isCollided(p_hollow))
                                {
                                    m_pAbstractGameActionListener.processGameAction(GAMEACTION_CROW_WIN);
                                    activateSprite(p_Sprite, SPRITE_CROW_WIN);
                                    p_Sprite.setMainPointXY(p_hollow.i_mainX, p_Sprite.i_mainY);
                                    break;
                                }
                            }
                        }
                        if(p_Sprite.isCollided(pPlayerSprite) && pPlayerSprite.i_ObjectType < SPRITE_SQUIRREL_DEAD)
                        {
                            activateSprite(p_Sprite, SPRITE_CROW_LEFT_ATTACK);
                            m_pAbstractGameActionListener.processGameAction(GAMEACTION_CROW_ATTACK);
                        }
                    }
                    break;
                case SPRITE_CROW_RIGHT:
                    if(getRandomInt(MOVE_RND) == (MOVE_RND / 2))
                    {
                        activateSprite(p_Sprite, SPRITE_CROW_RIGHT_MOVE);
                    }
                    else
                    {
                        for (int ix = 0; ix < MAX_SPRITES_NUMBER; ix++)
                        {
                            Sprite p_hollow = ap_Sprites[ix];
                            if (!p_hollow.lg_SpriteActive) continue;
                            if(p_hollow.i_ObjectType == SPRITE_HOLLOW)
                            {
                                if(p_Sprite.isCollided(p_hollow))
                                {
                                    m_pAbstractGameActionListener.processGameAction(GAMEACTION_CROW_WIN);
                                    activateSprite(p_Sprite, SPRITE_CROW_WIN);
                                    p_Sprite.setMainPointXY(p_hollow.i_mainX, p_Sprite.i_mainY);
                                    break;
                                }
                            }
                        }
                        if(p_Sprite.isCollided(pPlayerSprite) && pPlayerSprite.i_ObjectType < SPRITE_SQUIRREL_DEAD)
                        {
                            activateSprite(p_Sprite, SPRITE_CROW_RIGHT_ATTACK);
                            m_pAbstractGameActionListener.processGameAction(GAMEACTION_CROW_ATTACK);
                        }
                    }
                    break;
                case SPRITE_CROW_LEFT_MOVE:
                    p_Sprite.setMainPointXY(p_Sprite.i_mainX + CROW_SPEED, p_Sprite.i_mainY);
                    break;
                case SPRITE_CROW_RIGHT_MOVE:
                    p_Sprite.setMainPointXY(p_Sprite.i_mainX - CROW_SPEED, p_Sprite.i_mainY);
                    break;
                case SPRITE_CROW_LEFT_FLY:
                    if( ((p_Sprite.i_mainX + p_Sprite.i_width / 2) < 0x0000) || ((p_Sprite.i_mainX - p_Sprite.i_width / 2) > I8_SCREEN_WIDTH) || ((p_Sprite.i_mainY + p_Sprite.i_height / 2) < 0x0000) || (p_Sprite.i_mainY - p_Sprite.i_height > I8_SCREEN_HEIGHT) )
                    {
                        deactivateSprite(p_Sprite);
                    }
                    else
                    {
                        p_Sprite.setMainPointXY(p_Sprite.i_mainX + CROW_FLY_AWAY_SPEED, p_Sprite.i_mainY + CROW_FLY_AWAY_SPEED);
                    }
                    break;
                case SPRITE_CROW_RIGHT_FLY:
                    if( ((p_Sprite.i_mainX + p_Sprite.i_width / 2) < 0x0000) || ((p_Sprite.i_mainX - p_Sprite.i_width / 2) > I8_SCREEN_WIDTH) || ((p_Sprite.i_mainY + p_Sprite.i_height / 2) < 0x0000) || (p_Sprite.i_mainY - p_Sprite.i_height > I8_SCREEN_HEIGHT) )
                    {
                        deactivateSprite(p_Sprite);
                    }
                    else
                    {
                        p_Sprite.setMainPointXY(p_Sprite.i_mainX - CROW_FLY_AWAY_SPEED, p_Sprite.i_mainY + CROW_FLY_AWAY_SPEED);
                    }
                    break;
                case SPRITE_CROW_RIGHT_FLY_IN:
                    if(p_Sprite.i_mainY >= p_Sprite.i_ObjectState)
                    {
                        activateSprite(p_Sprite, SPRITE_CROW_RIGHT);
                        p_Sprite.i_ObjectState = INIT_CROW_FORCE;
                    }
                    else
                    {
                        p_Sprite.setMainPointXY(p_Sprite.i_mainX - CROW_FLY_IN_SPEED, p_Sprite.i_mainY + CROW_FLY_IN_SPEED);
                    }
                    break;
                case SPRITE_CROW_LEFT_FLY_IN:
                    if(p_Sprite.i_mainY >= p_Sprite.i_ObjectState)
                    {
                        activateSprite(p_Sprite, SPRITE_CROW_LEFT);
                        p_Sprite.i_ObjectState = INIT_CROW_FORCE;
                    }
                    else
                    {
                        p_Sprite.setMainPointXY(p_Sprite.i_mainX + CROW_FLY_IN_SPEED, p_Sprite.i_mainY + CROW_FLY_IN_SPEED);
                    }
                    break;
                case SPRITE_CROW_WIN:
                    if(p_Sprite.i_Frame == CROW_WIN_CROAC_FRAME && p_Sprite.i_Delay == 0)
                    {
                         m_pAbstractGameActionListener.processGameAction(GAMEACTION_CROW_WIN_CROAK);
                    }
                    break;

                case SPRITE_CONE_FIRE:
                    p_Sprite.setMainPointXY(p_Sprite.i_mainX + p_Sprite.i_ObjectState, p_Sprite.i_mainY);
                    if(((p_Sprite.i_mainX + p_Sprite.i_width / 2) < 0x0000) || ((p_Sprite.i_mainX - p_Sprite.i_width / 2) > I8_SCREEN_WIDTH) )
                    {
                        deactivateSprite(p_Sprite);
                    }
                    else
                    {
                        boolean stop = false;
                        for (int idx = 0; idx < MAX_SPRITES_NUMBER; idx++)
                        {
                            Sprite p_Crow = ap_Sprites[idx];
                            if (!p_Crow.lg_SpriteActive) continue;
                            switch (p_Crow.i_ObjectType)
                            {
                                case SPRITE_CROW_LEFT:
                                case SPRITE_CROW_LEFT_MOVE:
                                    if(p_Sprite.isCollided(p_Crow))
                                    {
                                        m_pAbstractGameActionListener.processGameAction(GAMEACTION_CROW_PAIN);
                                        activateSprite(p_Crow, SPRITE_CROW_LEFT_PAIN);
                                        deactivateSprite(p_Sprite);
                                        stop = true;
                                    }
                                    break;
                                case SPRITE_CROW_RIGHT:
                                case SPRITE_CROW_RIGHT_MOVE:
                                    if(p_Sprite.isCollided(p_Crow))
                                    {
                                        m_pAbstractGameActionListener.processGameAction(GAMEACTION_CROW_PAIN);
                                        activateSprite(p_Crow, SPRITE_CROW_RIGHT_PAIN);
                                        deactivateSprite(p_Sprite);
                                        stop = true;
                                    }
                                    break;
                            }
                            if(stop) break;
                        }
                    }
                    break;
                case SPRITE_CONE:
                    if(i_ConeCounter < i_MaxCones)
                    {
                        if(pPlayerSprite.isCollided(p_Sprite))
                        {
                            switch(pPlayerSprite.i_ObjectType)
                            {
                                case SPRITE_SQUIRREL_LEFT:
                                    activateSprite(pPlayerSprite, SPRITE_SQUIRREL_TAKE_LEFT);
                                    break;
                                case SPRITE_SQUIRREL_RIGHT:
                                    activateSprite(pPlayerSprite, SPRITE_SQUIRREL_TAKE_RIGHT);
                                    break;
                                case SPRITE_SQUIRREL_TAKE_LEFT:
                                    if(pPlayerSprite.isAnimationCompleted())
                                    {
                                        m_pAbstractGameActionListener.processGameAction(GAMEACTION_CONE_TAKEN);
                                        activateSprite(pPlayerSprite, SPRITE_SQUIRREL_LEFT);
                                        unlockLocation(p_Sprite.i_ObjectState);
                                        deactivateSprite(p_Sprite);
                                        i_ConeCounter++;
                                        i_LastConeTime = i_GameTimer;
                                    }
                                    break;
                                case SPRITE_SQUIRREL_TAKE_RIGHT:
                                    if(pPlayerSprite.isAnimationCompleted())
                                    {
                                        m_pAbstractGameActionListener.processGameAction(GAMEACTION_CONE_TAKEN);
                                        activateSprite(pPlayerSprite, SPRITE_SQUIRREL_RIGHT);
                                        unlockLocation(p_Sprite.i_ObjectState);
                                        deactivateSprite(p_Sprite);
                                        i_ConeCounter++;
                                        i_LastConeTime = i_GameTimer;
                                    }
                                    break;
                            }
                        }
                    }
                    break;
                case SPRITE_HOLLOW:
                    switch(p_Sprite.i_ObjectState)
                    {
                        case 1:
                            if(p_Sprite.isCollided(pPlayerSprite) && p_Sprite.i_mainX < pPlayerSprite.i_mainX)
                            {
                                m_pAbstractGameActionListener.processGameAction(GAMEACTION_SQUIRREL_TELEPORTED);
                                activateSprite(pPlayerSprite, SPRITE_SQUIRREL_JUMP_LEFT);
                                pPlayerSprite.setMainPointXY(HOLLOW2_X, HOLLOW2_Y);
                                pPlayerSprite.i_ObjectState = SQUIRREL_JUMP_STEPS;
                            }
                            break;
                        case 2:
                            if(p_Sprite.isCollided(pPlayerSprite) && p_Sprite.i_mainX < pPlayerSprite.i_mainX)
                            {
                                m_pAbstractGameActionListener.processGameAction(GAMEACTION_SQUIRREL_TELEPORTED);
                                activateSprite(pPlayerSprite, SPRITE_SQUIRREL_JUMP_RIGHT);
                                pPlayerSprite.setMainPointXY(HOLLOW3_X, HOLLOW3_Y);
                                pPlayerSprite.i_ObjectState = SQUIRREL_JUMP_STEPS;
                            }
                            break;
                        case 3:
                            if(p_Sprite.isCollided(pPlayerSprite) && p_Sprite.i_mainX > pPlayerSprite.i_mainX)
                            {
                                m_pAbstractGameActionListener.processGameAction(GAMEACTION_SQUIRREL_TELEPORTED);
                                activateSprite(pPlayerSprite, SPRITE_SQUIRREL_JUMP_RIGHT);
                                pPlayerSprite.setMainPointXY(HOLLOW4_X, HOLLOW4_Y);
                                pPlayerSprite.i_ObjectState = SQUIRREL_JUMP_STEPS;
                            }
                            break;
                        case 4:
                            if(p_Sprite.isCollided(pPlayerSprite) && p_Sprite.i_mainX > pPlayerSprite.i_mainX)
                            {
                                m_pAbstractGameActionListener.processGameAction(GAMEACTION_SQUIRREL_TELEPORTED);
                                activateSprite(pPlayerSprite, SPRITE_SQUIRREL_JUMP_LEFT);
                                pPlayerSprite.setMainPointXY(HOLLOW1_X, HOLLOW1_Y);
                                pPlayerSprite.i_ObjectState = SQUIRREL_JUMP_STEPS;
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
                 case SPRITE_SQUIRREL_FIRE_LEFT:
                     activateSprite(pPlayerSprite, SPRITE_SQUIRREL_LEFT);
                     break;
                 case SPRITE_SQUIRREL_FIRE_RIGHT:
                     activateSprite(pPlayerSprite, SPRITE_SQUIRREL_RIGHT);
                     break;
                 case SPRITE_SQUIRREL_DEAD:
                     m_pAbstractGameActionListener.processGameAction(GAMEACTION_SQUIRREL_DEAD);
                     activateSprite(pPlayerSprite, SPRITE_SQUIRREL_FALL);
                     pPlayerSprite.i_ObjectState = SQUIRREL_INITFALL_SPEED;
                     break;
                 case SPRITE_SQUIRREL_FALL:
                     return false;
             }
        }


        switch(pPlayerSprite.i_ObjectType)
        {
            case SPRITE_SQUIRREL_JUMP_LEFT:
                if(pPlayerSprite.i_ObjectState <= 0)
                {
                     Sprite p_Sprite;
                     int li;
                     for (li = 0; li < MAX_SPRITES_NUMBER; li++)
                     {
                         p_Sprite = ap_Sprites[li];
                         if (p_Sprite.lg_SpriteActive && p_Sprite.isCollided(pPlayerSprite)
                             && p_Sprite.i_ObjectType == SPRITE_HOLLOW)
                         {
                               activateSprite(pPlayerSprite, SPRITE_SQUIRREL_JUMP_LEFT);
                               pPlayerSprite.i_ObjectState = SQUIRREL_JUMP_STEPS;
                               break;
                         }
                     }
                     if(li == MAX_SPRITES_NUMBER)
                     {
                          activateSprite(pPlayerSprite, SPRITE_SQUIRREL_LEFT);
                     }
                }
                break;
            case SPRITE_SQUIRREL_JUMP_RIGHT:
                if(pPlayerSprite.i_ObjectState <= 0)
                {
                     Sprite p_Sprite;
                     int li;
                     for (li = 0; li < MAX_SPRITES_NUMBER; li++)
                     {
                         p_Sprite = ap_Sprites[li];
                         if (p_Sprite.lg_SpriteActive && p_Sprite.isCollided(pPlayerSprite)
                             && p_Sprite.i_ObjectType == SPRITE_HOLLOW)
                         {
                               activateSprite(pPlayerSprite, SPRITE_SQUIRREL_JUMP_RIGHT);
                               pPlayerSprite.i_ObjectState = SQUIRREL_JUMP_STEPS;
                               break;
                         }
                     }
                     if(li == MAX_SPRITES_NUMBER)
                     {
                          activateSprite(pPlayerSprite, SPRITE_SQUIRREL_RIGHT);
                     }
                }
                break;
        }

        switch(pPlayerSprite.i_ObjectType)
        {
            case SPRITE_SQUIRREL_LEFT:
                if ((_buttonState & MoveObject.BUTTON_LEFT) != 0)
                {
                    if(i_mx > (0x0000 + LEFT_MARGIN))
                    {
                        m_pAbstractGameActionListener.processGameAction(GAMEACTION_SQUIRREL_JUMP);
                        activateSprite(pPlayerSprite, SPRITE_SQUIRREL_JUMP_LEFT);
                        pPlayerSprite.i_ObjectState = SQUIRREL_JUMP_STEPS;
                    }
                }
                else if ((_buttonState & MoveObject.BUTTON_RIGHT) != 0)
                {
                    if(i_mx <= (I8_SCREEN_WIDTH - RIGHT_MARGIN))
                    {
                        activateSprite(pPlayerSprite, SPRITE_SQUIRREL_RIGHT);
                    }
                }
                if((_buttonState & MoveObject.BUTTON_FIRE) != 0)
                {
                    if(i_ConeCounter > 0)
                    {
                        m_pAbstractGameActionListener.processGameAction(GAMEACTION_SQUIRREL_FIRE);
                        activateSprite(pPlayerSprite, SPRITE_SQUIRREL_FIRE_LEFT);
                        i_ConeCounter--;
                    }
                }
                break;
            case SPRITE_SQUIRREL_RIGHT:
                if ((_buttonState & MoveObject.BUTTON_LEFT) != 0)
                {
                    if(i_mx > (0x0000 + LEFT_MARGIN))
                    {
                        activateSprite(pPlayerSprite, SPRITE_SQUIRREL_LEFT);
                    }
                }
                else if ((_buttonState & MoveObject.BUTTON_RIGHT) != 0)
                {
                    if(i_mx <= (I8_SCREEN_WIDTH - RIGHT_MARGIN))
                    {
                        m_pAbstractGameActionListener.processGameAction(GAMEACTION_SQUIRREL_JUMP);
                        activateSprite(pPlayerSprite, SPRITE_SQUIRREL_JUMP_RIGHT);
                        pPlayerSprite.i_ObjectState = SQUIRREL_JUMP_STEPS;
                    }
                }
                if((_buttonState & MoveObject.BUTTON_FIRE) != 0)
                {
                    if(i_ConeCounter > 0)
                    {
                        m_pAbstractGameActionListener.processGameAction(GAMEACTION_SQUIRREL_FIRE);
                        activateSprite(pPlayerSprite, SPRITE_SQUIRREL_FIRE_RIGHT);
                        i_ConeCounter--;
                    }
                }
                break;
            case SPRITE_SQUIRREL_JUMP_LEFT:
                {
                    pPlayerSprite.i_ObjectState--;
                    pPlayerSprite.setMainPointXY(i_mx - SQUIRREL_JUMP_SPEED, i_my);
                }
                break;
            case SPRITE_SQUIRREL_JUMP_RIGHT:
                {
                    pPlayerSprite.i_ObjectState--;
                    pPlayerSprite.setMainPointXY(i_mx + SQUIRREL_JUMP_SPEED, i_my);
                }
                break;
            case SPRITE_SQUIRREL_FIRE_LEFT:
                if(pPlayerSprite.isAnimationCompleted() && pPlayerSprite.i_Delay==0)
                {
                    generateFireCone(pPlayerSprite, -CONE_SPEED);
                    activateSprite(pPlayerSprite, SPRITE_SQUIRREL_LEFT);
                }
                break;
            case SPRITE_SQUIRREL_FIRE_RIGHT:
                if(pPlayerSprite.isAnimationCompleted() && pPlayerSprite.i_Delay==0)
                {
                    generateFireCone(pPlayerSprite, CONE_SPEED);
                    activateSprite(pPlayerSprite, SPRITE_SQUIRREL_RIGHT);
                }
                break;

            case SPRITE_SQUIRREL_DEAD:
                break;

            case SPRITE_SQUIRREL_FALL:
                {
                    pPlayerSprite.setMainPointXY(i_mx, i_my + pPlayerSprite.i_ObjectState);
                    pPlayerSprite.i_ObjectState += SQUIRREL_GRAVITY;
                    if(pPlayerSprite.i_ScreenY >= I8_SCREEN_HEIGHT)
                    {
                        return false;
                    }
                }
                break;

        }
        return true;
    }



    private void generateHollows()
    {
        int y = I8_SCREEN_HEIGHT / 2;
        int x = I8_SCREEN_WIDTH / 2;
        int type = SPRITE_HOLLOW;

        for(int i=0;i<4;i++)
        {
            Sprite p_emptySprite = getInactiveSprite();
            if (p_emptySprite != null)
            {
                switch(i)
                {
                    case 0:
                        x = HOLLOW1_X;
                        y = HOLLOW1_Y;
                        break;
                    case 1:
                        x = HOLLOW2_X;
                        y = HOLLOW2_Y;
                        break;
                    case 2:
                        x = HOLLOW3_X;
                        y = HOLLOW3_Y;
                        break;
                    case 3:
                        x = HOLLOW4_X;
                        y = HOLLOW4_Y;
                        break;
                }

                activateSprite(p_emptySprite, type);
                p_emptySprite.lg_SpriteActive = true;
                p_emptySprite.setMainPointXY(x, y);
                p_emptySprite.i_ObjectState = i+1;
            }
        }
    }

    private void generateFireCone(Sprite obj, int speed)
    {
        Sprite p_emptySprite = getInactiveSprite();
        if (p_emptySprite != null)
        {
            int x = obj.i_mainX;
            int y = obj.i_ScreenY;
            int type = SPRITE_CONE_FIRE;

            activateSprite(p_emptySprite, type);
            p_emptySprite.lg_SpriteActive = true;
            p_emptySprite.i_ObjectState = speed;

            if(speed < 0)
            {
                x -= obj.i_width / 2 + p_emptySprite.i_width;
            }
            else
            {
                x += obj.i_width / 2 + p_emptySprite.i_width;
            }

            p_emptySprite.setMainPointXY(x, y);
            m_pAbstractGameActionListener.processGameAction(GAMEACTION_CONE_FIRE_GENERATED);

        }
    }



    private void generateCones()
    {
        if((i_LastConeTime + i_ConeDelay) < i_GameTimer )
        {
            i_LastConeTime = i_GameTimer;
            Sprite p_emptySprite = getInactiveSprite();
            if (p_emptySprite != null)
            {
                int y = I8_SCREEN_HEIGHT / 2;
                int x = I8_SCREEN_WIDTH / 2;
                int type = SPRITE_CONE_APPEAR;

                int a = findLocation(getRandomInt(7));

                if (a < 0)
                {
                    return;
                }
                else
                {
                    lockLocation(a);
                }

                switch(a)
                {
                    case 0:
                        x = CONE1_X;
                        y = CONE1_Y;
                        break;
                    case 1:
                        x = CONE2_X;
                        y = CONE2_Y;
                        break;
                    case 2:
                        x = CONE3_X;
                        y = CONE3_Y;
                        break;
                    case 3:
                        x = CONE4_X;
                        y = CONE4_Y;
                        break;
                    case 4:
                        x = CONE5_X;
                        y = CONE5_Y;
                        break;
                    case 5:
                        x = CONE6_X;
                        y = CONE6_Y;
                        break;
                    case 6:
                        x = CONE7_X;
                        y = CONE7_Y;
                        break;
                    case 7:
                        x = CONE8_X;
                        y = CONE8_Y;
                        break;
                }

                activateSprite(p_emptySprite, type);
                p_emptySprite.lg_SpriteActive = true;
                p_emptySprite.setMainPointXY(x, y);
                p_emptySprite.i_ObjectState = a;
                m_pAbstractGameActionListener.processGameAction(GAMEACTION_CONE_GENERATED);

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
                int land = 0;
                int y = I8_SCREEN_HEIGHT / 2;
                int x = I8_SCREEN_WIDTH / 2;
                int type = SPRITE_CROW_LEFT_FLY_IN;

                int a = findBranch(getRandomInt(3));

                if (a < 0)
                {
                    return;
                }
                else
                {
                    lockBranch(a);
                }

                switch(a)
                {
                    case 0:
                        type = SPRITE_CROW_LEFT_FLY_IN;
                        x = 0x0000 - CROW_LEFT_OFFSET_X;
                        y = CROW_LEFT_Y1;
                        land = HOLLOW1_Y;
                        break;
                    case 1:
                        type = SPRITE_CROW_LEFT_FLY_IN;
                        x = 0x0000 - CROW_LEFT_OFFSET_X;
                        y = CROW_LEFT_Y2;
                        land = HOLLOW2_Y;
                        break;
                    case 2:
                        type = SPRITE_CROW_RIGHT_FLY_IN;
                        x = I8_SCREEN_WIDTH + CROW_RIGHT_OFFSET_X;
                        y = CROW_RIGHT_Y3;
                        land = HOLLOW3_Y;
                        break;
                    default:
                    case 3:
                        type = SPRITE_CROW_RIGHT_FLY_IN;
                        x = I8_SCREEN_WIDTH + CROW_RIGHT_OFFSET_X;
                        y = CROW_RIGHT_Y4;
                        land = HOLLOW4_Y;
                        break;
                }

                activateSprite(p_emptySprite, type);
                p_emptySprite.lg_SpriteActive = true;
                p_emptySprite.setMainPointXY(x, y);
                p_emptySprite.i_ObjectState = land;
                m_pAbstractGameActionListener.processGameAction(GAMEACTION_CROW_GENERATED);

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
                    i_ConeDelay = LEVEL_EASY_CONE_GENERATION;
                    i_MaxCones = LEVEL_EASY_CONES;
                }
                break;
            case LEVEL_NORMAL:
                {
                    i_timedelay = LEVEL_NORMAL_TIMEDELAY;
                    i_playerAttemptions = LEVEL_NORMAL_ATTEMPTIONS;
                    i_GenerateDelay = LEVEL_NORMAL_GENERATION;
                    i_ConeDelay = LEVEL_NORMAL_CONE_GENERATION;
                    i_MaxCones = LEVEL_NORMAL_CONES;
                }
                break;
            case LEVEL_HARD:
                {
                    i_timedelay = LEVEL_HARD_TIMEDELAY;
                    i_playerAttemptions = LEVEL_HARD_ATTEMPTIONS;
                    i_GenerateDelay = LEVEL_HARD_GENERATION;
                    i_ConeDelay = LEVEL_HARD_CONE_GENERATION;
                    i_MaxCones = LEVEL_HARD_CONES;
                }
                break;
        }

        i_GameTimer = 0;
        i_LastTime = i_GameTimer;
        i_LastConeTime = i_GameTimer;

        i_ConeCounter = 0;

        activateSprite(pPlayerSprite, SPRITE_SQUIRREL_JUMP_LEFT);
        pPlayerSprite.setMainPointXY(INIT_PLAYER_X, INIT_PLAYER_Y);
        pPlayerSprite.i_ObjectState = SQUIRREL_JUMP_STEPS;

        resetLocation();
        resetBranches();
        generateHollows();

        return true;
    }

    public void resumeGameAfterPlayerLostOrPaused()
    {
        super.resumeGameAfterPlayerLostOrPaused();

        deinitState();
        initState();

        i_GameTimer = 0;
        i_LastTime = i_GameTimer;
        i_LastConeTime = i_GameTimer;

        i_ConeCounter = 0;

        activateSprite(pPlayerSprite, SPRITE_SQUIRREL_JUMP_LEFT);
        pPlayerSprite.setMainPointXY(INIT_PLAYER_X, INIT_PLAYER_Y);
        pPlayerSprite.i_ObjectState = SQUIRREL_JUMP_STEPS;

        resetLocation();
        resetBranches();
        generateHollows();

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
        return "SQUIRREL";
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
        return (MAX_SPRITES_NUMBER+1)*(Sprite.DATASIZE_BYTES+1)+32+MAX_BRANCHES+MAX_LOCATIONS;
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

        _outputStream.writeInt(i_playerAttemptions);
        _outputStream.writeInt(i_GameTimer);
        _outputStream.writeInt(i_LastTime);
        _outputStream.writeInt(i_GenerateDelay);
        _outputStream.writeInt(i_LastConeTime);
        _outputStream.writeInt(i_ConeDelay);
        _outputStream.writeInt(i_ConeCounter);
        _outputStream.writeInt(i_MaxCones);

        for(int i=0; i<MAX_BRANCHES; i++)
        {
            _outputStream.writeBoolean(ab_Branches[i]);
        }
        for(int i=0; i<MAX_LOCATIONS; i++)
        {
            _outputStream.writeBoolean(ab_Places[i]);
        }
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

        i_playerAttemptions = _inputStream.readInt();
        i_GameTimer = _inputStream.readInt();
        i_LastTime = _inputStream.readInt();
        i_GenerateDelay = _inputStream.readInt();
        i_LastConeTime = _inputStream.readInt();
        i_ConeDelay = _inputStream.readInt();
        i_ConeCounter = _inputStream.readInt();
        i_MaxCones = _inputStream.readInt();

        for(int i=0; i<MAX_BRANCHES; i++)
        {
            ab_Branches[i] = _inputStream.readBoolean();
        }
        for(int i=0; i<MAX_LOCATIONS; i++)
        {
            ab_Places[i] = _inputStream.readBoolean();
        }
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

        generateCones();
        generateSprite();

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
