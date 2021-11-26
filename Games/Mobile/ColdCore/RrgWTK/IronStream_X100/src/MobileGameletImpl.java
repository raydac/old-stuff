
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.DataInputStream;


public class MobileGameletImpl extends MobileGamelet
{
    public static final int RIGHT_MARGIN = (8 << 8);
    public static final int LEFT_MARGIN = (8 << 8);

    public static final int HOUSES_X1 = (20 << 8);
    public static final int HOUSES_Y1 = (28 << 8);
    public static final int HOUSES_X2 = (30 << 8);
    public static final int HOUSES_Y2 = (73 << 8);

    public static final int HOUSES_SIZE = (38 << 8);

    public static final int TANK_X1 = (HOUSES_X1 + HOUSES_SIZE * 0);
    public static final int TANK_X2 = (HOUSES_X1 + HOUSES_SIZE * 1);
    public static final int TANK_X3 = (HOUSES_X1 + HOUSES_SIZE * 2);

    public static final int TANK_GUN_OFFSET_X = (-1<<8);
    public static final int TANK_GUN_OFFSET_Y = (-17<<8);

    public static final int ENEMY_TANK_GUN_OFFSET_X = 0;
    public static final int ENEMY_TANK_GUN_OFFSET_Y = (12<<8);

    public static final int ENEMY_TANK_SMOKE_OFFSET_X = 0;
    public static final int ENEMY_TANK_SMOKE_OFFSET_Y = (-2<<8);

    public static final int TANK_SPEED = (2 << 8);
    public static final int SHELL_SPEED = (6 << 8);
    public static final int ENEMY_TANK_SPEED = (1 << 8);

    public static final int HOUSE_FIRE_TIMER = 200;
    public static final int DAMAGE_TIMER = 30;
    public static final int SMALL_DAMAGE_TIMER = 12;
    public static final int MAX_HIT_COUNTER = 2;

    public static final int STEP_GENERATION_MASK = 0x001F;


    private static int I8_SCREEN_WIDTH;

    private static int I8_SCREEN_HEIGHT;

    public MoveObject p_MoveObject;

    public static final int GAMEACTION_TANK_READY                   = 0;
    public static final int GAMEACTION_TANK_TURN_GUN                = 1;
    public static final int GAMEACTION_TANK_LEFT                    = 2;
    public static final int GAMEACTION_TANK_RIGHT                   = 3;
    public static final int GAMEACTION_TANK_STOP                    = 4;
    public static final int GAMEACTION_TANK_FIRE                    = 5;
    public static final int GAMEACTION_TANK_DEAD                    = 6;
    public static final int GAMEACTION_ATTEMPTION_LOST              = 7;

    public static final int GAMEACTION_ENEMY_TANK_COLLISION         = 8;
    public static final int GAMEACTION_ENEMY_TANK_TURN              = 9;
    public static final int GAMEACTION_ENEMY_TANK_FIRE              = 10;
    public static final int GAMEACTION_ENEMY_TANK_DEAD              = 11;
    public static final int GAMEACTION_ENEMY_TANK_GENERATED         = 12;
    public static final int GAMEACTION_BURN_HOUSE                   = 13;
    public static final int GAMEACTION_DEAD_HOUSE                   = 14;
    public static final int GAMEACTION_SHELL                        = 15;
    public static final int GAMEACTION_ENEMY_SHELL                  = 16;
    public static final int GAMEACTION_SHELL_EXPLOSION              = 17;
    public static final int GAMEACTION_EXPLOSION                    = 18;
    public static final int GAMEACTION_SMOKE                        = 19;

    public static final int SCORES_HIT = 5;
    public static final int SCORES_TANK = 10;
    public static final int SCORES_HOUSE = -50;

    private int i_timedelay;

    public int i_playerAttemptions;

    public int i_GameTimer;

    public int i_LastTime;
    public int i_GenerateDelay;

    public int i_LastFireTime;
    public int i_FireDelay;

    public int i_LastEnemyFireTime;
    public int i_EnemyFireDelay;

    public boolean b_WaveDirection;

    public static final int LEVEL_EASY = 0;

    private static final int LEVEL_EASY_ATTEMPTIONS = 4;

    private static final int LEVEL_EASY_GENERATION = 90;
    private static final int LEVEL_EASY_LIMIT_GENERATION = 75;
    private static final int LEVEL_EASY_FIREDELAY = 7;
    private static final int LEVEL_EASY_ENEMYFIREDELAY = 40;

    private static final int LEVEL_EASY_TIMEDELAY = 130;

    public static final int LEVEL_NORMAL = 1;

    private static final int LEVEL_NORMAL_ATTEMPTIONS = 3;

    private static final int LEVEL_NORMAL_GENERATION = 80;
    private static final int LEVEL_NORMAL_LIMIT_GENERATION = 70;
    private static final int LEVEL_NORMAL_FIREDELAY = 7;
    private static final int LEVEL_NORMAL_ENEMYFIREDELAY = 40;

    private static final int LEVEL_NORMAL_TIMEDELAY = 100;

    public static final int LEVEL_HARD = 2;

    private static final int LEVEL_HARD_ATTEMPTIONS = 2;

    private static final int LEVEL_HARD_GENERATION = 75;
    private static final int LEVEL_HARD_LIMIT_GENERATION = 60;
    private static final int LEVEL_HARD_FIREDELAY = 7;
    private static final int LEVEL_HARD_ENEMYFIREDELAY = 40;

    private static final int LEVEL_HARD_TIMEDELAY = 100;


    public Sprite pPlayerSprite;


    public static final int MAX_MAIN_SPRITES = 16;

    public Sprite[] ap_MainSprites;

    public static final int MAX_ADDITIONAL_SPRITES = 25;

    public Sprite[] ap_AddSprites;


    public static final int MAX_SPRITES_NUMBER = MAX_ADDITIONAL_SPRITES + MAX_MAIN_SPRITES;
    public Sprite[] ap_Sprites;

    private static final int SPRITEDATALENGTH = 10;

    public static final int SPRITE_TANK = 0;
    public static final int SPRITE_TANK_APPEAR = 1;
    public static final int SPRITE_TANK_TURN_GUN = 2;
    public static final int SPRITE_TANK_LEFT = 3;
    public static final int SPRITE_TANK_RIGHT = 4;
    public static final int SPRITE_TANK_FIRE = 5;
    public static final int SPRITE_TANK_DEAD = 6;
    public static final int SPRITE_ATTEMPTION_LOST = 7;
    public static final int SPRITE_ENEMY_TANK = 8;
    public static final int SPRITE_ENEMY_TANK_LEFT = 9;
    public static final int SPRITE_ENEMY_TANK_RIGHT = 10;
    public static final int SPRITE_ENEMY_TANK_TURN_LEFT = 11;
    public static final int SPRITE_ENEMY_TANK_TURN_RIGHT = 12;
    public static final int SPRITE_ENEMY_TANK_BACK_LEFT = 13;
    public static final int SPRITE_ENEMY_TANK_BACK_RIGHT = 14;
    public static final int SPRITE_ENEMY_TANK_FIRE = 15;
    public static final int SPRITE_ENEMY_TANK_DEAD = 16;
    public static final int SPRITE_ENEMY_TANK_RUINS = 17;
    public static final int SPRITE_TANK_SHELL = 18;
    public static final int SPRITE_ENEMY_TANK_SHELL = 19;
    public static final int SPRITE_EXPLOSION = 20;
    public static final int SPRITE_SHELL_EXPLOSION = 21;
    public static final int SPRITE_HOUSE = 22;
    public static final int SPRITE_HOUSE_BARELY_DAMAGED = 23;
    public static final int SPRITE_HOUSE_DAMAGED = 24;
    public static final int SPRITE_HOUSE_RUINS = 25;
    public static final int SPRITE_SMOKE = 26;

    private static final int [] ai_SpriteParameters = new int[]
    {
        0x1000, 0x0800, -0x001, 0x0000, 0x1000, 0x0A00, 2, 1, Sprite.SPRITE_ALIGN_CENTER, Sprite.ANIMATION_CYCLIC,
        0x1000, 0x0800, -0x001, 0x0000, 0x1000, 0x0A00, 2, 1, Sprite.SPRITE_ALIGN_CENTER, Sprite.ANIMATION_CYCLIC,
        0x1000, 0x0800, -0x001, 0x0000, 0x1000, 0x0A00, 5, 1, Sprite.SPRITE_ALIGN_CENTER, Sprite.ANIMATION_FROZEN,
        0x1000, 0x0800, -0x001, 0x0000, 0x1000, 0x0A00, 2, 1, Sprite.SPRITE_ALIGN_CENTER, Sprite.ANIMATION_CYCLIC,
        0x1000, 0x0800, -0x001, 0x0000, 0x1000, 0x0A00, 2, 1, Sprite.SPRITE_ALIGN_CENTER, Sprite.ANIMATION_CYCLIC,
        0x1000, 0x0800, -0x001, 0x0000, 0x1000, 0x0A00, 2, 1, Sprite.SPRITE_ALIGN_CENTER, Sprite.ANIMATION_FROZEN,
        0x1000, 0x0800, -0x001, 0x0000, 0x1000, 0x0A00, 5, 2, Sprite.SPRITE_ALIGN_CENTER, Sprite.ANIMATION_FROZEN,
        0x1000, 0x0800, -0x001, 0x0000, 0x1000, 0x0A00, 5, 2, Sprite.SPRITE_ALIGN_CENTER, Sprite.ANIMATION_FROZEN,

        0x0A00, 0x1000, -0x001, 0x0000, 0x0800, 0x1400, 2, 1, Sprite.SPRITE_ALIGN_CENTER, Sprite.ANIMATION_CYCLIC,
        0x1000, 0x0800, -0x001, 0x0000, 0x1000, 0x1400, 2, 1, Sprite.SPRITE_ALIGN_CENTER, Sprite.ANIMATION_CYCLIC,
        0x1000, 0x0800, -0x001, 0x0000, 0x1000, 0x1400, 2, 1, Sprite.SPRITE_ALIGN_CENTER, Sprite.ANIMATION_CYCLIC,
        0x1000, 0x1000, -0x001, 0x0000, 0x0800, 0x1400, 3, 2, Sprite.SPRITE_ALIGN_CENTER, Sprite.ANIMATION_FROZEN,
        0x1000, 0x1000, -0x001, 0x0000, 0x0800, 0x1400, 3, 2, Sprite.SPRITE_ALIGN_CENTER, Sprite.ANIMATION_FROZEN,
        0x1000, 0x1000, -0x001, 0x0000, 0x0800, 0x1400, 3, 2, Sprite.SPRITE_ALIGN_CENTER, Sprite.ANIMATION_FROZEN,
        0x1000, 0x1000, -0x001, 0x0000, 0x0800, 0x1400, 3, 2, Sprite.SPRITE_ALIGN_CENTER, Sprite.ANIMATION_FROZEN,
        0x0A00, 0x1000, -0x001, 0x0000, 0x0800, 0x1400, 2, 1, Sprite.SPRITE_ALIGN_CENTER, Sprite.ANIMATION_FROZEN,
        0x0C00, 0x0C00, -0x001, 0x0000, 0x0C00, 0x0C00, 5, 2, Sprite.SPRITE_ALIGN_CENTER, Sprite.ANIMATION_FROZEN,
        0x0C00, 0x0C00, -0x001, 0x0000, 0x0C00, 0x0C00, 4, 4, Sprite.SPRITE_ALIGN_CENTER, Sprite.ANIMATION_FROZEN,

        0x0200, 0x0300, -0x001, 0x0000, 0x0200, 0x0300, 1, 1, Sprite.SPRITE_ALIGN_CENTER, Sprite.ANIMATION_FROZEN,
        0x0200, 0x0300, -0x001, 0x0000, 0x0200, 0x0300, 1, 1, Sprite.SPRITE_ALIGN_CENTER, Sprite.ANIMATION_FROZEN,
        0x1200, 0x1200, -0x001, 0x0000, 0x1200, 0x1200, 5, 2, Sprite.SPRITE_ALIGN_CENTER, Sprite.ANIMATION_FROZEN,
        0x0600, 0x0600, -0x001, 0x0000, 0x0600, 0x0600, 4, 2, Sprite.SPRITE_ALIGN_CENTER, Sprite.ANIMATION_FROZEN,
        0x1300, 0x1300, -0x001, 0x0000, 0x1300, 0x1300, 1, 1, Sprite.SPRITE_ALIGN_CENTER, Sprite.ANIMATION_CYCLIC,
        0x1300, 0x1300, -0x001, 0x0000, 0x1300, 0x1300, 5, 2, Sprite.SPRITE_ALIGN_CENTER, Sprite.ANIMATION_CYCLIC,
        0x1300, 0x1300, -0x001, 0x0000, 0x1300, 0x1300, 5, 2, Sprite.SPRITE_ALIGN_CENTER, Sprite.ANIMATION_CYCLIC,
        0x1300, 0x1300, -0x001, 0x0000, 0x0E00, 0x1300, 1, 1, Sprite.SPRITE_ALIGN_CENTER, Sprite.ANIMATION_CYCLIC,
        0x0A00, 0x0A00, -0x001, 0x0000, 0x0A00, 0x0A00, 4, 3, Sprite.SPRITE_ALIGN_CENTER, Sprite.ANIMATION_FROZEN
    };

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

    private Sprite getInactiveAddSprite()
    {
        for (int li = 0; li < MAX_ADDITIONAL_SPRITES; li++)
        {
            if (!ap_AddSprites[li].lg_SpriteActive) return ap_AddSprites[li];
        }
        return null;
    }
    private Sprite getInactiveMainSprite()
    {
        for (int li = 0; li < MAX_MAIN_SPRITES; li++)
        {
            if (!ap_MainSprites[li].lg_SpriteActive) return ap_MainSprites[li];
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
                    case SPRITE_ENEMY_TANK_FIRE:
                        activateSprite(p_Sprite, SPRITE_ENEMY_TANK);
                        break;
                    case SPRITE_ENEMY_TANK_TURN_LEFT:
                        if(!newShapeCollideTest(p_Sprite,SPRITE_ENEMY_TANK_LEFT))
                        {
                          activateSprite(p_Sprite, SPRITE_ENEMY_TANK_LEFT);
                        }
                        break;
                    case SPRITE_ENEMY_TANK_TURN_RIGHT:
                        if(!newShapeCollideTest(p_Sprite,SPRITE_ENEMY_TANK_RIGHT))
                        {
                            activateSprite(p_Sprite, SPRITE_ENEMY_TANK_RIGHT);
                        }
                        break;
                    case SPRITE_ENEMY_TANK_BACK_LEFT:
                    case SPRITE_ENEMY_TANK_BACK_RIGHT:
                        activateSprite(p_Sprite, SPRITE_ENEMY_TANK);
                        break;
                    case SPRITE_SHELL_EXPLOSION:
                    case SPRITE_EXPLOSION:
                        deactivateSprite(p_Sprite);
                        break;
                    case SPRITE_ENEMY_TANK_DEAD:
                        activateSprite(p_Sprite, SPRITE_ENEMY_TANK_RUINS);
                        break;
                    case SPRITE_ENEMY_TANK_RUINS:
                        deactivateSprite(p_Sprite);
                        break;
                    case SPRITE_HOUSE_BARELY_DAMAGED:
                        activateSprite(p_Sprite, SPRITE_HOUSE_DAMAGED);
                        m_pAbstractGameActionListener.processGameAction(GAMEACTION_BURN_HOUSE);
                        p_Sprite.i_ObjectTimer = HOUSE_FIRE_TIMER;
                        addScore(SCORES_HOUSE);
                        break;
                    case SPRITE_SMOKE:
                        deactivateSprite(p_Sprite);
                        break;
                }
            }
            switch (p_Sprite.i_ObjectType)
            {
                case SPRITE_ENEMY_TANK_RIGHT:
                    {
                        if(p_Sprite.i_ObjectTimer > 0)
                        {
                            p_Sprite.i_ObjectTimer--;
                            if((p_Sprite.i_ObjectTimer & 1) == 0)
                            generateSmoke(p_Sprite);
                        }
                        else
                        {
                            p_Sprite.i_ObjectState = 0;
                            boolean b_not_collision = true;

                            p_Sprite.setMainPointXY(p_Sprite.i_mainX - ENEMY_TANK_SPEED, p_Sprite.i_mainY);

                            for(int idx = 0; idx < MAX_MAIN_SPRITES; idx++)
                            {
                                Sprite p_House = ap_MainSprites[idx];
                                if(!p_House.lg_SpriteActive || p_House == p_Sprite) continue;
                                switch(p_House.i_ObjectType)
                                {
                                    case SPRITE_HOUSE:
                                    case SPRITE_HOUSE_BARELY_DAMAGED:
                                    case SPRITE_HOUSE_DAMAGED:
                                    case SPRITE_HOUSE_RUINS:
                                        if(p_House.isCollided(p_Sprite))
                                        {
                                            b_not_collision = false;
                                            break;
                                        }
                                        break;

                                    case SPRITE_ENEMY_TANK:
                                    case SPRITE_ENEMY_TANK_LEFT:
                                    case SPRITE_ENEMY_TANK_RIGHT:
                                    case SPRITE_ENEMY_TANK_TURN_LEFT:
                                    case SPRITE_ENEMY_TANK_TURN_RIGHT:
                                    case SPRITE_ENEMY_TANK_BACK_LEFT:
                                    case SPRITE_ENEMY_TANK_BACK_RIGHT:
                                    case SPRITE_ENEMY_TANK_FIRE:
                                        if(p_House.isCollided(p_Sprite))
                                        {
                                            p_Sprite.i_ObjectState = 0;
                                            p_Sprite.i_ObjectTimer = SMALL_DAMAGE_TIMER;
                                            p_Sprite.setMainPointXY(p_Sprite.i_mainX + ENEMY_TANK_SPEED, p_Sprite.i_mainY);
                                            m_pAbstractGameActionListener.processGameAction(GAMEACTION_ENEMY_TANK_COLLISION);

                                            if(p_House.i_ObjectTimer > 0)
                                            {
                                                p_House.i_ObjectTimer = 0;
                                            }
                                        }
                                        break;
                                }
                            }
                            if(b_not_collision)
                            {
                                activateSprite(p_Sprite, SPRITE_ENEMY_TANK_BACK_RIGHT);
                                m_pAbstractGameActionListener.processGameAction(GAMEACTION_ENEMY_TANK_TURN);
                            }
                        }
                    }
                    break;
                case SPRITE_ENEMY_TANK_LEFT:
                    {
                        if(p_Sprite.i_ObjectTimer > 0)
                        {
                            p_Sprite.i_ObjectTimer--;
                            if((p_Sprite.i_ObjectTimer & 1) == 0)
                            generateSmoke(p_Sprite);
                        }
                        else
                        {
                            p_Sprite.i_ObjectState = 0;
                            boolean b_not_collision = true;

                            p_Sprite.setMainPointXY(p_Sprite.i_mainX + ENEMY_TANK_SPEED, p_Sprite.i_mainY);

                            for(int idx = 0; idx < MAX_MAIN_SPRITES; idx++)
                            {
                                Sprite p_House = ap_MainSprites[idx];
                                if(!p_House.lg_SpriteActive || p_House == p_Sprite) continue;
                                switch(p_House.i_ObjectType)
                                {
                                    case SPRITE_HOUSE:
                                    case SPRITE_HOUSE_BARELY_DAMAGED:
                                    case SPRITE_HOUSE_DAMAGED:
                                    case SPRITE_HOUSE_RUINS:
                                        if(p_House.isCollided(p_Sprite))
                                        {
                                            b_not_collision = false;
                                            break;
                                        }
                                        break;
                                    case SPRITE_ENEMY_TANK:
                                    case SPRITE_ENEMY_TANK_LEFT:
                                    case SPRITE_ENEMY_TANK_RIGHT:
                                    case SPRITE_ENEMY_TANK_TURN_LEFT:
                                    case SPRITE_ENEMY_TANK_TURN_RIGHT:
                                    case SPRITE_ENEMY_TANK_BACK_LEFT:
                                    case SPRITE_ENEMY_TANK_BACK_RIGHT:
                                    case SPRITE_ENEMY_TANK_FIRE:
                                        if(p_House.isCollided(p_Sprite))
                                        {
                                            p_Sprite.i_ObjectTimer = SMALL_DAMAGE_TIMER;
                                            p_Sprite.i_ObjectState = 0;
                                            p_Sprite.setMainPointXY(p_Sprite.i_mainX - ENEMY_TANK_SPEED, p_Sprite.i_mainY);
                                            m_pAbstractGameActionListener.processGameAction(GAMEACTION_ENEMY_TANK_COLLISION);

                                            if(p_House.i_ObjectTimer > 0)
                                            {
                                                p_House.i_ObjectTimer = 0;
                                            }
                                        }
                                        break;
                                }
                            }
                            if(b_not_collision)
                            {
                                activateSprite(p_Sprite, SPRITE_ENEMY_TANK_BACK_LEFT);
                                m_pAbstractGameActionListener.processGameAction(GAMEACTION_ENEMY_TANK_TURN);
                            }
                        }
                    }
                    break;
                case SPRITE_ENEMY_TANK:
                    if(p_Sprite.i_ObjectTimer > 0)
                    {
                        p_Sprite.i_ObjectTimer--;
                            if((p_Sprite.i_ObjectTimer & 1) == 0)
                        generateSmoke(p_Sprite);
                        if((i_LastEnemyFireTime + i_EnemyFireDelay) < i_GameTimer )
                        {
                            i_LastEnemyFireTime = i_GameTimer;
                            generateEnemyShell(p_Sprite);
                            activateSprite(p_Sprite, SPRITE_ENEMY_TANK_FIRE);
                            m_pAbstractGameActionListener.processGameAction(GAMEACTION_ENEMY_TANK_FIRE);
                        }
                    }
                    else
                    {
                        p_Sprite.i_ObjectState = 0;
                        p_Sprite.setMainPointXY(p_Sprite.i_mainX, p_Sprite.i_mainY + ENEMY_TANK_SPEED);

                        for(int idx = 0; idx < MAX_MAIN_SPRITES; idx++)
                        {
                            Sprite p_House = ap_MainSprites[idx];
                                if(!p_House.lg_SpriteActive || p_House == p_Sprite) continue;
                            switch(p_House.i_ObjectType)
                            {
                                case SPRITE_HOUSE:
                                case SPRITE_HOUSE_BARELY_DAMAGED:
                                case SPRITE_HOUSE_DAMAGED:
                                case SPRITE_HOUSE_RUINS:
                                    if(p_House.isCollided(p_Sprite))
                                    {
                                        if(getRandomInt(16) < 8)
                                        {
                                            activateSprite(p_Sprite, SPRITE_ENEMY_TANK_TURN_LEFT);
                                            m_pAbstractGameActionListener.processGameAction(GAMEACTION_ENEMY_TANK_TURN);
                                            break;
                                        }
                                        else
                                        {
                                            activateSprite(p_Sprite, SPRITE_ENEMY_TANK_TURN_RIGHT);
                                            m_pAbstractGameActionListener.processGameAction(GAMEACTION_ENEMY_TANK_TURN);
                                            break;
                                        }
                                    }
                                    break;
                                case SPRITE_ENEMY_TANK:
                                case SPRITE_ENEMY_TANK_LEFT:
                                case SPRITE_ENEMY_TANK_RIGHT:
                                case SPRITE_ENEMY_TANK_TURN_LEFT:
                                case SPRITE_ENEMY_TANK_TURN_RIGHT:
                                case SPRITE_ENEMY_TANK_BACK_LEFT:
                                case SPRITE_ENEMY_TANK_BACK_RIGHT:
                                case SPRITE_ENEMY_TANK_FIRE:
                                    if(p_House.isCollided(p_Sprite))
                                    {
                                        p_Sprite.i_ObjectState = 0;
                                        p_Sprite.i_ObjectTimer = SMALL_DAMAGE_TIMER;
                                        p_Sprite.setMainPointXY(p_Sprite.i_mainX, p_Sprite.i_mainY - ENEMY_TANK_SPEED);
                                        m_pAbstractGameActionListener.processGameAction(GAMEACTION_ENEMY_TANK_COLLISION);
                                        if(p_House.i_ObjectTimer > 0)
                                        {
                                            p_House.i_ObjectTimer = 0;
                                        }
                                    }
                                    break;
                            }
                        }

                        if(p_Sprite.i_ObjectType == SPRITE_ENEMY_TANK)
                        {
                            if((i_LastEnemyFireTime + i_EnemyFireDelay) < i_GameTimer )
                            {
                                i_LastEnemyFireTime = i_GameTimer;
                                generateEnemyShell(p_Sprite);
                                activateSprite(p_Sprite, SPRITE_ENEMY_TANK_FIRE);
                                m_pAbstractGameActionListener.processGameAction(GAMEACTION_ENEMY_TANK_FIRE);
                            }
                        }

                    }

                    if((p_Sprite.i_mainY + p_Sprite.i_height / 2) > I8_SCREEN_HEIGHT)
                    {
                        if(pPlayerSprite.i_ObjectType != SPRITE_TANK_DEAD && pPlayerSprite.i_ObjectType != SPRITE_ATTEMPTION_LOST)
                        {
                            activateSprite(pPlayerSprite, SPRITE_ATTEMPTION_LOST);
                            m_pAbstractGameActionListener.processGameAction(GAMEACTION_ATTEMPTION_LOST);
                        }
                    }
                    if(p_Sprite.isCollided(pPlayerSprite))
                    {
                        if(pPlayerSprite.i_ObjectType != SPRITE_TANK_DEAD)
                        {
                            addScore(SCORES_TANK);

                            generateExplosion(p_Sprite);
                            activateSprite(p_Sprite, SPRITE_ENEMY_TANK_DEAD);
                            m_pAbstractGameActionListener.processGameAction(GAMEACTION_ENEMY_TANK_DEAD);

                            generateExplosion(pPlayerSprite);
                            activateSprite(pPlayerSprite, SPRITE_TANK_DEAD);
                            m_pAbstractGameActionListener.processGameAction(GAMEACTION_TANK_DEAD);
                        }
                    }
                    break;
                case SPRITE_TANK_SHELL:
                    p_Sprite.setMainPointXY(p_Sprite.i_mainX, p_Sprite.i_mainY - SHELL_SPEED);

                    for(int j = 0; j < MAX_MAIN_SPRITES; j++)
                    {
                        Sprite p_obj = ap_MainSprites[j];
                        if(!p_obj.lg_SpriteActive) continue;
                        switch(p_obj.i_ObjectType)
                        {
                            case SPRITE_HOUSE:
                                if(p_obj.isCollided(p_Sprite))
                                {
                                    activateSprite(p_Sprite, SPRITE_SHELL_EXPLOSION);
                                    m_pAbstractGameActionListener.processGameAction(GAMEACTION_SHELL_EXPLOSION);
                                    activateSprite(p_obj, SPRITE_HOUSE_BARELY_DAMAGED);
                                    break;
                                }
                                break;
                            case SPRITE_HOUSE_BARELY_DAMAGED:
                            case SPRITE_HOUSE_DAMAGED:
                            case SPRITE_HOUSE_RUINS:
                                if(p_obj.isCollided(p_Sprite))
                                {
                                    activateSprite(p_Sprite, SPRITE_SHELL_EXPLOSION);
                                    m_pAbstractGameActionListener.processGameAction(GAMEACTION_SHELL_EXPLOSION);
                                    break;
                                }
                                break;
                            case SPRITE_ENEMY_TANK:
                            case SPRITE_ENEMY_TANK_TURN_LEFT:
                            case SPRITE_ENEMY_TANK_TURN_RIGHT:
                            case SPRITE_ENEMY_TANK_BACK_LEFT:
                            case SPRITE_ENEMY_TANK_BACK_RIGHT:
                            case SPRITE_ENEMY_TANK_FIRE:
                            case SPRITE_ENEMY_TANK_LEFT:
                            case SPRITE_ENEMY_TANK_RIGHT:
                                if(p_obj.isCollided(p_Sprite))
                                {
                                    if(p_obj.i_ObjectType != SPRITE_ENEMY_TANK_TURN_LEFT &&
                                       p_obj.i_ObjectType != SPRITE_ENEMY_TANK_TURN_RIGHT)
                                    {
                                        p_Sprite.setMainPointXY(p_Sprite.i_mainX, p_Sprite.i_mainY - 0x300);
                                    }

                                    p_obj.i_ObjectState++;
                                    p_obj.i_ObjectTimer = DAMAGE_TIMER;
                                    m_pAbstractGameActionListener.processGameAction(GAMEACTION_SHELL_EXPLOSION);
                                    activateSprite(p_Sprite, SPRITE_SHELL_EXPLOSION);
                                    addScore(SCORES_HIT);

                                    if(p_obj.i_ObjectState > MAX_HIT_COUNTER)
                                    {
                                        addScore(SCORES_TANK);
                                        activateSprite(p_obj, SPRITE_ENEMY_TANK_DEAD);
                                        m_pAbstractGameActionListener.processGameAction(GAMEACTION_ENEMY_TANK_DEAD);
                                        generateExplosion(p_obj);
                                    }
                                    else
                                    {
                                        if(p_obj.i_ObjectState > (MAX_HIT_COUNTER-1))
                                        {
                                            if(getRandomInt(16) < 8)
                                            {
                                                addScore(SCORES_TANK);
                                                activateSprite(p_obj, SPRITE_ENEMY_TANK_DEAD);
                                                m_pAbstractGameActionListener.processGameAction(GAMEACTION_ENEMY_TANK_DEAD);
                                                generateExplosion(p_obj);
                                            }
                                        }
                                    }
                                }
                                break;
                        }
                    }

                    if((p_Sprite.i_mainY + p_Sprite.i_height) < 0)
                    {
                        deactivateSprite(p_Sprite);
                    }
                    break;
                case SPRITE_ENEMY_TANK_SHELL:
                    p_Sprite.setMainPointXY(p_Sprite.i_mainX, p_Sprite.i_mainY + SHELL_SPEED);
                    if(p_Sprite.isCollided(pPlayerSprite) && pPlayerSprite.i_ObjectType != SPRITE_TANK_DEAD)
                    {
                        activateSprite(p_Sprite, SPRITE_SHELL_EXPLOSION);
                        m_pAbstractGameActionListener.processGameAction(GAMEACTION_SHELL_EXPLOSION);
                        activateSprite(pPlayerSprite, SPRITE_TANK_DEAD);
                        m_pAbstractGameActionListener.processGameAction(GAMEACTION_TANK_DEAD);
                        generateExplosion(pPlayerSprite);
                    }

                    if((p_Sprite.i_mainY - p_Sprite.i_height) > I8_SCREEN_HEIGHT)
                    {
                        deactivateSprite(p_Sprite);
                    }
                    break;

                case SPRITE_HOUSE_DAMAGED:
                    if(p_Sprite.i_ObjectTimer > 0)
                    {
                        p_Sprite.i_ObjectTimer--;
                    }
                    else
                    {
                        activateSprite(p_Sprite, SPRITE_HOUSE_RUINS);
                        m_pAbstractGameActionListener.processGameAction(GAMEACTION_DEAD_HOUSE);
                    }
                    break;
                case SPRITE_SMOKE:
                    p_Sprite.setMainPointXY(p_Sprite.i_mainX - 128 + getRandomInt(256), p_Sprite.i_mainY - 128 + getRandomInt(256));
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
                case SPRITE_TANK_TURN_GUN:
                    activateSprite(pPlayerSprite, SPRITE_TANK);
                    m_pAbstractGameActionListener.processGameAction(GAMEACTION_TANK_READY);
                    break;
                case SPRITE_TANK_FIRE:
                    activateSprite(pPlayerSprite, SPRITE_TANK);
                    break;
                case SPRITE_TANK_DEAD:
                    return false;
                case SPRITE_ATTEMPTION_LOST:
                    return false;
            }
        }
        switch(pPlayerSprite.i_ObjectType)
        {
            case SPRITE_TANK:
                if ((_buttonState & MoveObject.BUTTON_LEFT) != 0)
                {
                    if(i_mx > (0x0000 + LEFT_MARGIN))
                    {
                        activateSprite(pPlayerSprite, SPRITE_TANK_LEFT);
                        m_pAbstractGameActionListener.processGameAction(GAMEACTION_TANK_LEFT);
                    }
                }
                else if ((_buttonState & MoveObject.BUTTON_RIGHT) != 0)
                {
                    if(i_mx < (I8_SCREEN_WIDTH - RIGHT_MARGIN))
                    {
                        activateSprite(pPlayerSprite, SPRITE_TANK_RIGHT);
                        m_pAbstractGameActionListener.processGameAction(GAMEACTION_TANK_RIGHT);
                    }
                }
                if ((_buttonState & MoveObject.BUTTON_FIRE) != 0)
                {
                    if((i_LastFireTime + i_FireDelay) < i_GameTimer )
                    {
                        i_LastFireTime = i_GameTimer;
                        generateShell(pPlayerSprite);
                        activateSprite(pPlayerSprite, SPRITE_TANK_FIRE);
                        m_pAbstractGameActionListener.processGameAction(GAMEACTION_TANK_FIRE);

                    }
                }
                break;
            case SPRITE_TANK_LEFT:
                if ((_buttonState & MoveObject.BUTTON_LEFT) != 0)
                {
                    if(i_mx > (0x0000 + LEFT_MARGIN))
                    {
                        i_mx -= TANK_SPEED;
                        pPlayerSprite.setMainPointXY(i_mx, i_my);
                    }
                    else
                    {
                        activateSprite(pPlayerSprite, SPRITE_TANK);
                        m_pAbstractGameActionListener.processGameAction(GAMEACTION_TANK_STOP);
                    }
                }
                else
                {
                    activateSprite(pPlayerSprite, SPRITE_TANK);
                    m_pAbstractGameActionListener.processGameAction(GAMEACTION_TANK_STOP);
                }
                break;
            case SPRITE_TANK_RIGHT:
                if ((_buttonState & MoveObject.BUTTON_RIGHT) != 0)
                {
                    if(i_mx < (I8_SCREEN_WIDTH - RIGHT_MARGIN))
                    {
                        i_mx += TANK_SPEED;
                        pPlayerSprite.setMainPointXY(i_mx, i_my);
                    }
                    else
                    {
                        activateSprite(pPlayerSprite, SPRITE_TANK);
                        m_pAbstractGameActionListener.processGameAction(GAMEACTION_TANK_STOP);
                    }
                }
                else
                {
                    activateSprite(pPlayerSprite, SPRITE_TANK);
                    m_pAbstractGameActionListener.processGameAction(GAMEACTION_TANK_STOP);
                }
                break;
            case SPRITE_TANK_APPEAR:
                if(i_mx > (I8_SCREEN_WIDTH / 4 * 3))
                {
                    pPlayerSprite.setMainPointXY(i_mx - TANK_SPEED, i_my);
                }
                else
                {
                    activateSprite(pPlayerSprite, SPRITE_TANK_TURN_GUN);
                    m_pAbstractGameActionListener.processGameAction(GAMEACTION_TANK_TURN_GUN);
                }
                break;
        }
        return true;
    }

    private void generateSmoke(Sprite obj)
    {
        if(obj.i_ObjectTimer <= SMALL_DAMAGE_TIMER) return;

        if(true) 
        {
            Sprite p_emptySprite = getInactiveAddSprite();
            if (p_emptySprite != null)
            {
                int x = obj.i_mainX + ENEMY_TANK_SMOKE_OFFSET_X;
                int y = obj.i_mainY + ENEMY_TANK_SMOKE_OFFSET_Y;
                int type = SPRITE_SMOKE;

                activateSprite(p_emptySprite, type);
                p_emptySprite.lg_SpriteActive = true;
                p_emptySprite.setMainPointXY(x, y);
                m_pAbstractGameActionListener.processGameAction(GAMEACTION_SMOKE);
            }
        }
    }



    private void generateExplosion(Sprite obj)
    {
        Sprite p_emptySprite = getInactiveAddSprite();
        if (p_emptySprite != null)
        {
            int x = obj.i_mainX;
            int y = obj.i_mainY;
            int type = SPRITE_EXPLOSION;

            activateSprite(p_emptySprite, type);
            p_emptySprite.lg_SpriteActive = true;
            p_emptySprite.setMainPointXY(x, y);
            m_pAbstractGameActionListener.processGameAction(GAMEACTION_EXPLOSION);
        }
    }

    private void generateShell(Sprite obj)
    {
        Sprite p_emptySprite = getInactiveAddSprite();
        if (p_emptySprite != null)
        {
            int x = obj.i_mainX + TANK_GUN_OFFSET_X;
            int y = obj.i_mainY + TANK_GUN_OFFSET_Y;
            int type = SPRITE_TANK_SHELL;

            activateSprite(p_emptySprite, type);
            p_emptySprite.lg_SpriteActive = true;
            p_emptySprite.setMainPointXY(x, y);
            m_pAbstractGameActionListener.processGameAction(GAMEACTION_SHELL);
        }
    }

    private void generateEnemyShell(Sprite obj)
    {
        Sprite p_emptySprite = getInactiveAddSprite();
        if (p_emptySprite != null)
        {
            int x = obj.i_mainX + ENEMY_TANK_GUN_OFFSET_X;
            int y = obj.i_mainY + ENEMY_TANK_GUN_OFFSET_Y;
            int type = SPRITE_ENEMY_TANK_SHELL;

            activateSprite(p_emptySprite, type);
            p_emptySprite.lg_SpriteActive = true;
            p_emptySprite.setMainPointXY(x, y);
            m_pAbstractGameActionListener.processGameAction(GAMEACTION_ENEMY_SHELL);
        }
    }

    private void generateHouses()
    {
        int y = HOUSES_Y1;
        int x = HOUSES_X1;
        int type = SPRITE_HOUSE;

        for(int i=0;i<6;i++)
        {
            if(i == 3)
            {
                y = HOUSES_Y2;
                x = HOUSES_X2;
            }
            Sprite p_emptySprite = getInactiveMainSprite();
            if (p_emptySprite != null)
            {
                activateSprite(p_emptySprite, type);
                p_emptySprite.lg_SpriteActive = true;
                p_emptySprite.setMainPointXY(x, y);
            }
            x += HOUSES_SIZE;
        }
    }




    private void generateSprite()
    {
        if((i_LastTime + i_GenerateDelay) < i_GameTimer )
        {
            i_LastTime = i_GameTimer;
            Sprite p_emptySprite = getInactiveMainSprite();

            if (p_emptySprite != null)
            {
                int y = 0;
                int x = 0;
                int type = SPRITE_ENEMY_TANK;

                int [] positions = {TANK_X1,TANK_X2,TANK_X3};
                int mark = positions.length;
                Sprite p_object;

                int ofs = type * SPRITEDATALENGTH;
                int border = y + ai_SpriteParameters[ofs + 1];
                int width = ai_SpriteParameters[ofs] >> 1;

                for(int idx = 0; idx < MAX_MAIN_SPRITES; idx++)
                {
                    p_object = ap_MainSprites[idx];
                    if(p_object.lg_SpriteActive && p_object.i_ScreenY < border)
                    {
                         for(ofs = 0; ofs < mark; ofs++)
                         {
                            if(p_object.i_ScreenX >= positions[ofs]-width &&
                               p_object.i_ScreenX+p_object.i_height <= positions[ofs]+width)
                            {
                                   positions[ofs] = positions[--mark];
                                   if(mark == 0) return;
                            }
                         }
                    }
                }

                x = positions[getRandomInt(mark-1)];

                activateSprite(p_emptySprite, type);
                p_emptySprite.setMainPointXY(x, y - p_emptySprite.i_height / 2);
                p_emptySprite.i_ObjectTimer = 0;
                p_emptySprite.i_ObjectState = 0;
                p_emptySprite.lg_SpriteActive = true;

                m_pAbstractGameActionListener.processGameAction(GAMEACTION_ENEMY_TANK_GENERATED);
            }
        }
    }

    private boolean newShapeCollideTest(Sprite p_Sprite,int type)
    {
        Sprite p_emptySprite = new Sprite(0);
        Sprite p_obj;

        activateSprite(p_emptySprite, type);
        p_emptySprite.setMainPointXY(p_Sprite.i_mainX,p_Sprite.i_mainY);

        for(int idx = 0; idx < MAX_MAIN_SPRITES; idx++)
        {
            p_obj = ap_MainSprites[idx];
            if(p_obj != p_Sprite && p_obj.lg_SpriteActive && p_obj.isCollided(p_emptySprite))
            {
                        switch(p_obj.i_ObjectType)
                        {
                            case SPRITE_ENEMY_TANK:
                            case SPRITE_ENEMY_TANK_TURN_LEFT:
                            case SPRITE_ENEMY_TANK_TURN_RIGHT:
                            case SPRITE_ENEMY_TANK_BACK_LEFT:
                            case SPRITE_ENEMY_TANK_BACK_RIGHT:
                            case SPRITE_ENEMY_TANK_FIRE:
                            case SPRITE_ENEMY_TANK_LEFT:
                            case SPRITE_ENEMY_TANK_RIGHT:
                                                      return true;
                        }
            }
        }
        return false;
    }



public boolean newGameSession(int _gameAreaWidth, int _gameAreaHeight, int _gameLevel, CAbstractPlayer[] _players,startup _listener, String _staticArrayResourceName)
    {
        super.newGameSession(_gameAreaWidth, _gameAreaHeight, _gameLevel, _players, _listener, _staticArrayResourceName);

        I8_SCREEN_WIDTH = _gameAreaWidth<<8;
        I8_SCREEN_HEIGHT = 128<<8;

        switch (_gameLevel)
        {
            case LEVEL_EASY:
                {
                    i_timedelay = LEVEL_EASY_TIMEDELAY;
                    i_playerAttemptions = LEVEL_EASY_ATTEMPTIONS;
                    i_GenerateDelay = LEVEL_EASY_GENERATION;
                    i_FireDelay = LEVEL_EASY_FIREDELAY;
                    i_EnemyFireDelay = LEVEL_EASY_ENEMYFIREDELAY;
                }
                break;
            case LEVEL_NORMAL:
                {
                    i_timedelay = LEVEL_NORMAL_TIMEDELAY;
                    i_playerAttemptions = LEVEL_NORMAL_ATTEMPTIONS;
                    i_GenerateDelay = LEVEL_NORMAL_GENERATION;
                    i_FireDelay = LEVEL_NORMAL_FIREDELAY;
                    i_EnemyFireDelay = LEVEL_NORMAL_ENEMYFIREDELAY;
                }
                break;
            case LEVEL_HARD:
                {
                    i_timedelay = LEVEL_HARD_TIMEDELAY;
                    i_playerAttemptions = LEVEL_HARD_ATTEMPTIONS;
                    i_GenerateDelay = LEVEL_HARD_GENERATION;
                    i_FireDelay = LEVEL_HARD_FIREDELAY;
                    i_EnemyFireDelay = LEVEL_HARD_ENEMYFIREDELAY;
                }
                break;
        }

        i_GameTimer = 0;
        i_LastTime = i_GameTimer;
        i_LastFireTime = i_GameTimer + LEVEL_HARD_FIREDELAY;
        i_LastEnemyFireTime = i_GameTimer;
        b_WaveDirection = true;

        activateSprite(pPlayerSprite, SPRITE_TANK_APPEAR);
        pPlayerSprite.setMainPointXY(I8_SCREEN_WIDTH + pPlayerSprite.i_width / 2 , I8_SCREEN_HEIGHT - pPlayerSprite.i_height);

        generateHouses();

        return true;
    }

    public void resumeGameAfterPlayerLostOrPaused()
    {
        super.resumeGameAfterPlayerLostOrPaused();

        deinitState();
        initState();

        i_GameTimer = 0;
        i_LastTime = i_GameTimer;
        i_LastFireTime = i_GameTimer + LEVEL_HARD_FIREDELAY;
        i_LastEnemyFireTime = i_GameTimer;
        b_WaveDirection = true;

        activateSprite(pPlayerSprite, SPRITE_TANK_APPEAR);
        pPlayerSprite.setMainPointXY(I8_SCREEN_WIDTH + pPlayerSprite.i_width / 2 , I8_SCREEN_HEIGHT - pPlayerSprite.i_height);

        generateHouses();

        switch(getGameDifficultLevel())
        {
            case LEVEL_EASY:
                i_GenerateDelay = LEVEL_EASY_GENERATION;
                break;
            case LEVEL_NORMAL:
                i_GenerateDelay = LEVEL_NORMAL_GENERATION;
                break;
            case LEVEL_HARD:
                i_GenerateDelay = LEVEL_HARD_GENERATION;
                break;
        }

    }

    public String getGameTextID()
    {
        return "TANKATTACK";
    }

    public boolean initState()
    {
        ap_Sprites = new Sprite[MAX_SPRITES_NUMBER];
        ap_MainSprites = new Sprite[MAX_MAIN_SPRITES];
        ap_AddSprites = new Sprite[MAX_ADDITIONAL_SPRITES];
        for (int li = 0; li < MAX_SPRITES_NUMBER; li++)
        {
              Sprite p_Sprite = new Sprite(li);
              ap_Sprites[li] = p_Sprite;
              if(li < MAX_MAIN_SPRITES)
              {
                  ap_MainSprites[li] = p_Sprite;
              }
                 else
                      {
                          ap_AddSprites[li - MAX_MAIN_SPRITES] = p_Sprite;
                      }
        }

        pPlayerSprite = new Sprite(-1);

        p_MoveObject = new MoveObject();

        return true;
    }

    public void deinitState()
    {

        ap_Sprites = null;
        ap_MainSprites = null;
        ap_AddSprites = null;
        pPlayerSprite = null;

        Runtime.getRuntime().gc();
    }

    public int _getMaximumDataSize()
    {
        return (MAX_SPRITES_NUMBER+1)*(Sprite.DATASIZE_BYTES+1)+33;
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

        _outputStream.writeInt(i_LastFireTime);
        _outputStream.writeInt(i_FireDelay);

        _outputStream.writeInt(i_LastEnemyFireTime);
        _outputStream.writeInt(i_EnemyFireDelay);

        _outputStream.writeBoolean(b_WaveDirection);
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

        i_LastFireTime = _inputStream.readInt();
        i_FireDelay = _inputStream.readInt();

        i_LastEnemyFireTime = _inputStream.readInt();
        i_EnemyFireDelay = _inputStream.readInt();

        b_WaveDirection = _inputStream.readBoolean();
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

        i_GameTimer++;

        int minimal = LEVEL_EASY_LIMIT_GENERATION;
        int maximal = LEVEL_EASY_GENERATION;

        switch(getGameDifficultLevel())
        {
            case GAMELEVEL_EASY:
                minimal = LEVEL_EASY_LIMIT_GENERATION;
                maximal = LEVEL_EASY_GENERATION;
                break;
            case GAMELEVEL_NORMAL:
                minimal = LEVEL_NORMAL_LIMIT_GENERATION;
                maximal = LEVEL_NORMAL_GENERATION;
                break;
            case GAMELEVEL_HARD:
                minimal = LEVEL_HARD_LIMIT_GENERATION;
                maximal = LEVEL_HARD_GENERATION;
                break;
        }

        if((i_GameTimer & STEP_GENERATION_MASK) == 0)
        {
            if(b_WaveDirection)
            {
                if(i_GenerateDelay > minimal)
                {
                    i_GenerateDelay--;
                }
                else
                {
                    b_WaveDirection = !b_WaveDirection;
                }
            }
            else
            {
                if(i_GenerateDelay < maximal)
                {
                    i_GenerateDelay++;
                }
                else
                {
                    b_WaveDirection = !b_WaveDirection;
                }
            }
        }
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

        if(p_player.m_iPlayerGameScores < 0) p_player.setPlayerMoveGameScores(0, true);
    }

    public int getGameStepDelay()
    {
        return i_timedelay;
    }
}
