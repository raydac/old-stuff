
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.DataInputStream;

public class MobileGameletImpl extends MobileGamelet
{

    public static final int APE_CATCHED = 0x80;
    public static final int TREE_X = (16 << 8);
    public static final int TREE_Y = (48 << 8);

    public static final int HELICOPTER_Y = (20 << 8);
    public static final int HELICOPTER_SPEED = (0x0300);

    public static final int TREE_SIZE = (32 << 8);

    public static final int TREE_X_1 = (TREE_X + TREE_SIZE * 0);
    public static final int TREE_X_2 = (TREE_X + TREE_SIZE * 1);
    public static final int TREE_X_3 = (TREE_X + TREE_SIZE * 2);
    public static final int TREE_X_4 = (TREE_X + TREE_SIZE * 3);

    public static final int COCONUT_OFFSET_X = 6;
    public static final int COCONUT_OFFSET_Y = 2;

    public static final int PLAYER_X = (TREE_X_2 + 0x0080);
    public static final int PLAYER_Y = (TREE_Y - 0x0C00); 

    public static final int CLIMB_SPEED = (0x0100);
    public static final int CLIMB_FALL_SPEED = (0x0400);

    public static final int STAND_SIZE = (0x0600);

    public static final int COCONUT_SPEED = (0x0600);

    public static final int APE_SPEED = (0x0250);

    public static final int HSPEED = (0x0390); 
    public static final int VSPEED = -(0x0400);
    public static final int VCHANGE = (0x0100);

    private static int I8_SCREEN_WIDTH;

    private static int I8_SCREEN_HEIGHT;

    public MoveObject p_MoveObject;

    public static final int GAMEACTION_JUMP                 = 0;
    public static final int GAMEACTION_JUMPED               = 1;
    public static final int GAMEACTION_PICK                 = 2;
    public static final int GAMEACTION_FIRE                 = 3;
    public static final int GAMEACTION_FIRED                = 4;
    public static final int GAMEACTION_DEAD                 = 5;
    public static final int GAMEACTION_TREE_TOUCH           = 6;
    public static final int GAMEACTION_CLIMBER_STAND        = 7;
    public static final int GAMEACTION_CLIMBER_HIT          = 8;
    public static final int GAMEACTION_CLIMBER_GENERATED    = 9;
    public static final int GAMEACTION_HELICOPTER_GENERATED = 10;

    private int i_timedelay;

    public int i_playerAttemptions;

    public int i_GameTimer;

    public int i_LastTime;
    public int i_LastHelicopterTime;
    public int i_GenerateDelay;
    public int i_HelicopterDelay;

    public int i_VSpeed;

    public int i_CoconutX;



    public static final int LEVEL_EASY = 0;

    private static final int LEVEL_EASY_ATTEMPTIONS = 4;

    private static final int LEVEL_EASY_GENERATION = 53;
    private static final int LEVEL_EASY_HELICOPTER = 300;

    private static final int LEVEL_EASY_TIMEDELAY = 100;




    public static final int LEVEL_NORMAL = 1;

    private static final int LEVEL_NORMAL_ATTEMPTIONS = 3;

    private static final int LEVEL_NORMAL_GENERATION = 48;
    private static final int LEVEL_NORMAL_HELICOPTER = 280;

    private static final int LEVEL_NORMAL_TIMEDELAY = 90;

    public static final int LEVEL_HARD = 2;

    private static final int LEVEL_HARD_ATTEMPTIONS = 2;

    private static final int LEVEL_HARD_GENERATION = 40;
    private static final int LEVEL_HARD_HELICOPTER = 250;

    private static final int LEVEL_HARD_TIMEDELAY = 80;

    private static final int SCORES_KILL = 10;

    public static final int MAX_SPRITES_NUMBER = 12;

    public Sprite pPlayerSprite;

    public Sprite pCoconut;

    public Sprite pHelicopter;

    public Sprite[] ap_Sprites;

    private static final int SPRITEDATALENGTH = 10;

    public static final int SPRITE_APE = 0;

    public static final int SPRITE_APE_JUMPLEFT = 1;

    public static final int SPRITE_APE_JUMPRIGHT = 2;

    public static final int SPRITE_APE_DOWN = 3;

    public static final int SPRITE_APE_UP = 4;

    public static final int SPRITE_APE_DEAD = 5;

    public static final int SPRITE_APE_PICK = 6;

    public static final int SPRITE_APE_FIRE = 7;

    public static final int SPRITE_TREE = 8;

    public static final int SPRITE_TREE_TOUCHED = 9;

    public static final int SPRITE_COCONUT = 10;

    public static final int SPRITE_CLIMBER = 11;

    public static final int SPRITE_CLIMBER_STAND = 12;


    public static final int SPRITE_CLIMBER_FALL = 13;

    public static final int SPRITE_HELICOPTER = 14;


    private static final int [] ai_SpriteParameters = new int[]
    {
        0x0C00, 0x1100, 0x0000, 0x0000, 0x0C00, 0x0F00, 1, 1, Sprite.SPRITE_ALIGN_CENTER, Sprite.ANIMATION_FROZEN,
        0x0C00, 0x1100, 0x0000, 0x0000, 0x0C00, 0x1500, 2, 1, Sprite.SPRITE_ALIGN_CENTER, Sprite.ANIMATION_FROZEN,
        0x0C00, 0x1100, 0x0000, 0x0000, 0x0C00, 0x1500, 2, 1, Sprite.SPRITE_ALIGN_CENTER, Sprite.ANIMATION_FROZEN,
        0x0C00, 0x1100, 0x0000, 0x0000, 0x0C00, 0x0F00, 3, 2, Sprite.SPRITE_ALIGN_CENTER, Sprite.ANIMATION_FROZEN,
        0x0C00, 0x1100, 0x0000, 0x0000, 0x0C00, 0x0F00, 3, 2, Sprite.SPRITE_ALIGN_CENTER, Sprite.ANIMATION_FROZEN,
        0x0C00, 0x1100, 0x0000, 0x0000, 0x0400, 0x0F00, 3, 2, Sprite.SPRITE_ALIGN_CENTER, Sprite.ANIMATION_FROZEN,
        0x0C00, 0x1100, 0x0000, 0x0000, 0x0C00, 0x0F00, 1, 1, Sprite.SPRITE_ALIGN_CENTER, Sprite.ANIMATION_FROZEN,
        0x0C00, 0x1100, 0x0000, 0x0000, 0x0C00, 0x0F00, 2, 1, Sprite.SPRITE_ALIGN_CENTER, Sprite.ANIMATION_FROZEN,
        0x1800, 0x0F00, 0x0000, 0x0000, 0x1800, 0x0F00, 1, 1, Sprite.SPRITE_ALIGN_CENTER, Sprite.ANIMATION_FROZEN,
        0x1800, 0x0F00, 0x0000, 0x0000, 0x1800, 0x0F00, 1, 1, Sprite.SPRITE_ALIGN_CENTER, Sprite.ANIMATION_FROZEN,
        0x0800, 0x0800, 0x0000, 0x0000, 0x0800, 0x0800, 1, 1, Sprite.SPRITE_ALIGN_CENTER, Sprite.ANIMATION_FROZEN,
        0x0C00, 0x1000, 0x0000, 0x0000, 0x0C00, 0x1000, 2, 3, Sprite.SPRITE_ALIGN_CENTER, Sprite.ANIMATION_CYCLIC,
        0x0C00, 0x1000, 0x0000, 0x0000, 0x0C00, 0x1000, 1, 1, Sprite.SPRITE_ALIGN_CENTER, Sprite.ANIMATION_FROZEN,
        0x1200, 0x1000, 0x0000, 0x0000, 0x0C00, 0x1000, 3, 1, Sprite.SPRITE_ALIGN_CENTER, Sprite.ANIMATION_FROZEN,
        0x1800, 0x1000, 0x0000, 0x0000, 0x1100, 0x1200, 2, 1, Sprite.SPRITE_ALIGN_CENTER, Sprite.ANIMATION_CYCLIC,
    };

    private static final int MAX_TREES = 4;

    public boolean Trees[] = new boolean[]
    {
        true, true, true, true
    };

    private void ResetTrees()
    {
        for(int i=0; i<MAX_TREES; i++)
        {
            Trees[i] = true;
        }
    }

    private int GetTree(int index)
    {
        for(int i=index; i<MAX_TREES; i++)
        {
            if(Trees[i]) return i;
        }
        for(int i=0; i<index; i++)
        {
            if(Trees[i]) return i;
        }
        return -1;
    }

    private void SetTree(int i)
    {
        if(i >= 0 && i < MAX_TREES)
        {
            Trees[i] = false;
        }
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
                    case SPRITE_TREE_TOUCHED:
                        activateSprite(p_Sprite, SPRITE_TREE);
                        p_Sprite.setMainPointXY(p_Sprite.i_mainX, p_Sprite.i_mainY - 0x0100);
                        pPlayerSprite.setMainPointXY(pPlayerSprite.i_mainX, pPlayerSprite.i_mainY - 0x0100);
                        break;
                }
            }
            switch (p_Sprite.i_ObjectType)
            {
                case SPRITE_CLIMBER:
                    if(p_Sprite.i_Frame == 0)
                    {
                        p_Sprite.setMainPointXY(p_Sprite.i_mainX, p_Sprite.i_mainY - CLIMB_SPEED);
                    }
                    if(pCoconut.isCollided(p_Sprite) && pCoconut.lg_SpriteActive)
                    {
                        deactivateSprite(pCoconut);
                        activateSprite(p_Sprite, SPRITE_CLIMBER_FALL);
                        m_pAbstractGameActionListener.processGameAction(GAMEACTION_CLIMBER_HIT);
                        addScore(SCORES_KILL);
                    }
                    else
                    {
                        if(pPlayerSprite.isCollided(p_Sprite))
                        {
                            switch(pPlayerSprite.i_ObjectType)
                            {
                                case SPRITE_APE_JUMPLEFT:
                                case SPRITE_APE_JUMPRIGHT:
                                case SPRITE_APE_DEAD:
                                    break;
                                default:
                                    deactivateSprite(p_Sprite);
                                    activateSprite(pPlayerSprite, SPRITE_APE_DEAD);
                                    m_pAbstractGameActionListener.processGameAction(GAMEACTION_DEAD);
                                    break;
                            }
                        }
                        else
                        {
                            if(p_Sprite.i_mainY < TREE_Y)
                            {
                                m_pAbstractGameActionListener.processGameAction(GAMEACTION_CLIMBER_STAND);
                                activateSprite(p_Sprite, SPRITE_CLIMBER_STAND);
                                p_Sprite.setMainPointXY(p_Sprite.i_mainX, p_Sprite.i_mainY - STAND_SIZE);
                                SetTree( (p_Sprite.i_mainX - TREE_X) / TREE_SIZE );

                                for (int idx = 0; idx < MAX_SPRITES_NUMBER; idx++)
                                {
                                    Sprite p_Man = ap_Sprites[idx];
                                    if (!p_Man.lg_SpriteActive) continue;
                                    if (p_Man.i_ObjectType != SPRITE_CLIMBER) continue;
                                    if (p_Man.i_mainX == p_Sprite.i_mainX)
                                    {
                                        activateSprite(p_Man, SPRITE_CLIMBER_FALL);
                                    }
                                }
                            }
                        }
                    }
                    break;
                case SPRITE_CLIMBER_STAND:
                    if(pPlayerSprite.isCollided(p_Sprite))
                    {
                        switch(pPlayerSprite.i_ObjectType)
                        {
                            case SPRITE_APE_JUMPLEFT:
                            case SPRITE_APE_JUMPRIGHT:
                            case SPRITE_APE_DEAD:
                                break;
                            default:
                                deactivateSprite(p_Sprite);
                                activateSprite(pPlayerSprite, SPRITE_APE_DEAD);
                                m_pAbstractGameActionListener.processGameAction(GAMEACTION_DEAD);
                                break;
                        }
                    }
                    break;
                case SPRITE_CLIMBER_FALL:
                    p_Sprite.setMainPointXY(p_Sprite.i_mainX, p_Sprite.i_mainY + CLIMB_FALL_SPEED);
                    if((p_Sprite.i_mainY - p_Sprite.i_height / 2) > I8_SCREEN_HEIGHT )
                    {
                        deactivateSprite(p_Sprite);
                    }
                    break;
                case SPRITE_TREE:
                    if(p_Sprite.isCollided(pPlayerSprite))
                    {
                        m_pAbstractGameActionListener.processGameAction(GAMEACTION_TREE_TOUCH);
                        activateSprite(p_Sprite, SPRITE_TREE_TOUCHED);
                        p_Sprite.setMainPointXY(p_Sprite.i_mainX, p_Sprite.i_mainY + 0x0100);
                        pPlayerSprite.setMainPointXY(pPlayerSprite.i_mainX, pPlayerSprite.i_mainY + 0x0100);
                    }
                    break;
                case SPRITE_TREE_TOUCHED:
                    break;
            }
        }
    }

    private void processHelicopter()
    {
        if(pHelicopter.lg_SpriteActive)
        {
            pHelicopter.processAnimation();

            pHelicopter.setMainPointXY(pHelicopter.i_mainX + pHelicopter.i_ObjectState, pHelicopter.i_mainY);
            if(pPlayerSprite.isCollided(pHelicopter))
            {
                switch(pPlayerSprite.i_ObjectType)
                {
                    case SPRITE_APE_JUMPLEFT:
                    case SPRITE_APE_JUMPRIGHT:
                        pPlayerSprite.i_ObjectState |= APE_CATCHED;
                        m_pAbstractGameActionListener.processGameAction(GAMEACTION_DEAD);
                        break;
                    case SPRITE_APE_DEAD:
                        break;
                    default:
                        activateSprite(pPlayerSprite, SPRITE_APE_DEAD);
                        m_pAbstractGameActionListener.processGameAction(GAMEACTION_DEAD);
                        break;
                }
            }
            if((pHelicopter.i_mainX < -pHelicopter.i_width) || (pHelicopter.i_mainX > (I8_SCREEN_WIDTH + pHelicopter.i_width)) )
            {
                deactivateSprite(pHelicopter);
                i_LastHelicopterTime = i_GameTimer;
            }
        }
        else
        {
            if((i_LastHelicopterTime + i_HelicopterDelay) < i_GameTimer )
            {
                int y = HELICOPTER_Y;
                int x = I8_SCREEN_WIDTH;
                int speed = HELICOPTER_SPEED;

                activateSprite(pHelicopter, SPRITE_HELICOPTER);
                pHelicopter.lg_SpriteActive = true;
                if(getRandomInt(16) < 8)
                {
                    x = I8_SCREEN_WIDTH + pHelicopter.i_width;
                    speed = -speed;
                }
                else
                {
                    x = 0x0000 - pHelicopter.i_width;
                }

                pHelicopter.setMainPointXY(x, y);
                pHelicopter.i_ObjectState = speed;
                m_pAbstractGameActionListener.processGameAction(GAMEACTION_HELICOPTER_GENERATED);
            }
        }
    }

    private void processCoconut()
    {
        if(pCoconut.lg_SpriteActive)
        {
            if(pCoconut.processAnimation())
            {
            }
            pCoconut.setMainPointXY(pCoconut.i_mainX, pCoconut.i_mainY + COCONUT_SPEED);
            if((pCoconut.i_mainY - pCoconut.i_height / 2) > I8_SCREEN_HEIGHT )
            {
                deactivateSprite(pCoconut);
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
                case SPRITE_APE_PICK:
                    pPlayerSprite.i_ObjectState = 1;
                    generateCoconut();
                    activateSprite(pPlayerSprite, SPRITE_APE);
                    break;
                case SPRITE_APE_FIRE:
                    activateSprite(pCoconut, SPRITE_COCONUT);
                    pCoconut.setMainPointXY(i_mx, i_my - i_h / 2);
                    pPlayerSprite.i_ObjectState = 0;
                    activateSprite(pPlayerSprite, SPRITE_APE);
                    m_pAbstractGameActionListener.processGameAction(GAMEACTION_FIRED);
                    break;
                case SPRITE_APE_DEAD:
                    return false;
            }
        }
        switch(pPlayerSprite.i_ObjectType)
        {
            case SPRITE_APE:
                if(i_my == PLAYER_Y)
                {
                    if ((_buttonState & MoveObject.BUTTON_LEFT) != 0)
                    {
                        if(i_mx > (TREE_X_1+0x0100))
                        {
                            m_pAbstractGameActionListener.processGameAction(GAMEACTION_JUMP);
                            activateSprite(pPlayerSprite, SPRITE_APE_JUMPLEFT);
                            i_VSpeed = VSPEED;
                        }
                    }
                    else if ((_buttonState & MoveObject.BUTTON_RIGHT) != 0)
                    {
                        if(i_mx < (TREE_X_4))
                        {
                            m_pAbstractGameActionListener.processGameAction(GAMEACTION_JUMP);
                            activateSprite(pPlayerSprite, SPRITE_APE_JUMPRIGHT);
                            i_VSpeed = VSPEED;
                        }
                    }
                    if ((_buttonState & MoveObject.BUTTON_DOWN) != 0)
                    {
                        if(true)
                        {
                            activateSprite(pPlayerSprite, SPRITE_APE_DOWN);
                        }
                    }
                    if ((_buttonState & MoveObject.BUTTON_FIRE) != 0)
                    {
                        if(pPlayerSprite.i_ObjectState != 0)
                        {
                            activateSprite(pPlayerSprite, SPRITE_APE_FIRE);
                            m_pAbstractGameActionListener.processGameAction(GAMEACTION_FIRE);
                        }
                    }
                }
                else if ((_buttonState & MoveObject.BUTTON_UP) != 0)
                {
                    activateSprite(pPlayerSprite, SPRITE_APE_UP);
                }
                break;
            case SPRITE_APE_UP:
                if(i_my > PLAYER_Y)
                {
                    pPlayerSprite.setMainPointXY(i_mx, i_my - APE_SPEED);
                }
                else
                {
                    activateSprite(pPlayerSprite, SPRITE_APE);
                }
                break;
            case SPRITE_APE_DOWN:
                if(i_my < TREE_Y)
                {
                    pPlayerSprite.setMainPointXY(i_mx, i_my + APE_SPEED);
                }
                else
                {
                    if((i_mx >> 8) == (i_CoconutX >> 8) && (pPlayerSprite.i_ObjectState == 0))
                    {
                        activateSprite(pPlayerSprite, SPRITE_APE_PICK);
                        m_pAbstractGameActionListener.processGameAction(GAMEACTION_PICK);
                    }
                    else
                    {
                        activateSprite(pPlayerSprite, SPRITE_APE);
                    }
                }
                break;
            case SPRITE_APE_JUMPLEFT:
                pPlayerSprite.setMainPointXY(i_mx - HSPEED, i_my + i_VSpeed);
                i_VSpeed += VCHANGE;
                if(pPlayerSprite.i_mainY >= PLAYER_Y)
                {
                    if(pPlayerSprite.i_ObjectState >= APE_CATCHED)
                    {
                       activateSprite(pPlayerSprite, SPRITE_APE_DEAD);
                    }
                     else
                         {
                            activateSprite(pPlayerSprite, SPRITE_APE);
                            m_pAbstractGameActionListener.processGameAction(GAMEACTION_JUMPED);
                         }
                }
                break;
            case SPRITE_APE_JUMPRIGHT:
                pPlayerSprite.setMainPointXY(i_mx + HSPEED, i_my + i_VSpeed);
                i_VSpeed += VCHANGE;
                if(pPlayerSprite.i_mainY >= PLAYER_Y)
                {
                    if(pPlayerSprite.i_ObjectState >= APE_CATCHED)
                    {
                       activateSprite(pPlayerSprite, SPRITE_APE_DEAD);
                    }
                     else
                         {
                            activateSprite(pPlayerSprite, SPRITE_APE);
                            m_pAbstractGameActionListener.processGameAction(GAMEACTION_JUMPED);
                         }
                }
                break;
            case SPRITE_APE_DEAD:
                break;
        }
        return true;
    }


    private void generateSprite()
    {
        if((i_LastTime + i_GenerateDelay) < i_GameTimer )
        {
            i_LastTime = i_GameTimer;
            Sprite p_emptySprite = getInactiveSprite();
            if (p_emptySprite != null)
            {
                int y = I8_SCREEN_HEIGHT;
                int x = I8_SCREEN_WIDTH / 2;
                int type = SPRITE_CLIMBER;

                int a = GetTree(getRandomInt(4));
                if (a < 0) return;

                switch(a)
                {
                    case 0:
                        x = TREE_X_1;
                        break;
                    case 1:
                        x = TREE_X_2;
                        break;
                    default:
                    case 2:
                        x = TREE_X_3;
                        break;
                    case 3:
                        x = TREE_X_4;
                        break;
                }

                activateSprite(p_emptySprite, type);
                p_emptySprite.lg_SpriteActive = true;
                p_emptySprite.setMainPointXY(x, y + p_emptySprite.i_height / 2);
                m_pAbstractGameActionListener.processGameAction(GAMEACTION_CLIMBER_GENERATED);
            }
        }
    }


    private void generateTrees()
    {
        int y = TREE_Y;
        int x = TREE_X;
        int type = SPRITE_TREE;

        ResetTrees();

        for(int i=0; i<4; i++)
        {
            Sprite p_emptySprite = getInactiveSprite();
            if (p_emptySprite != null)
            {
                switch(i)
                {
                    case 0:
                        x = TREE_X_1;
                        break;
                    case 1:
                        x = TREE_X_2;
                        break;
                    case 2:
                        x = TREE_X_3;
                        break;
                    case 3:
                        x = TREE_X_4;
                        break;
                }

                activateSprite(p_emptySprite, type);
                p_emptySprite.lg_SpriteActive = true;
                p_emptySprite.setMainPointXY(x, y);
            }
        }
        generateCoconut();
    }

    private void generateCoconut()
    {
        switch(getRandomInt(3))
        {
            case 0:
                i_CoconutX = TREE_X_1;
                break;
            case 1:
                i_CoconutX = TREE_X_2;
                break;
            case 2:
                i_CoconutX = TREE_X_3;
                break;
            case 3:
                i_CoconutX = TREE_X_4;
                break;
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
                    i_HelicopterDelay = LEVEL_EASY_HELICOPTER;
                }
                break;
            case LEVEL_NORMAL:
                {
                    i_timedelay = LEVEL_NORMAL_TIMEDELAY;
                    i_playerAttemptions = LEVEL_NORMAL_ATTEMPTIONS;
                    i_GenerateDelay = LEVEL_NORMAL_GENERATION;
                    i_HelicopterDelay = LEVEL_NORMAL_HELICOPTER;
                }
                break;
            case LEVEL_HARD:
                {
                    i_timedelay = LEVEL_HARD_TIMEDELAY;
                    i_playerAttemptions = LEVEL_HARD_ATTEMPTIONS;
                    i_GenerateDelay = LEVEL_HARD_GENERATION;
                    i_HelicopterDelay = LEVEL_HARD_HELICOPTER;
                }
                break;
        }

        i_GameTimer = 0;
        i_LastTime = i_GameTimer;
        i_LastHelicopterTime = i_GameTimer;

        activateSprite(pPlayerSprite, SPRITE_APE);
        pPlayerSprite.setMainPointXY(PLAYER_X, PLAYER_Y - 0x0100);
        pPlayerSprite.i_ObjectState = 0;

        generateTrees();

        return true;
    }

    public void resumeGameAfterPlayerLostOrPaused()
    {
        super.resumeGameAfterPlayerLostOrPaused();

        deinitState();
        initState();

        i_GameTimer = 0;
        i_LastTime = i_GameTimer;
        i_LastHelicopterTime = i_GameTimer;

        activateSprite(pPlayerSprite, SPRITE_APE);
        pPlayerSprite.setMainPointXY(PLAYER_X, PLAYER_Y - 0x0100);
        pPlayerSprite.i_ObjectState = 0;

        generateTrees();

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
        return "APE";
    }

    public boolean initState()
    {
        ap_Sprites = new Sprite[MAX_SPRITES_NUMBER];
        for (int li = 0; li < MAX_SPRITES_NUMBER; li++) ap_Sprites[li] = new Sprite(li);


        pPlayerSprite = new Sprite(-1);
        pCoconut = new Sprite(-2);
        pHelicopter = new Sprite(-3);

        p_MoveObject = new MoveObject();

        return true;
    }

    public void deinitState()
    {

        ap_Sprites = null;
        pPlayerSprite = null;
        pCoconut = null;
        pHelicopter = null;

        Runtime.getRuntime().gc();
    }

    public int _getMaximumDataSize()
    {
        return (MAX_SPRITES_NUMBER+3)*(Sprite.DATASIZE_BYTES+1)+36;
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

        _outputStream.writeByte(pCoconut.i_ObjectType);
        pCoconut.writeSpriteToStream(_outputStream);

        _outputStream.writeByte(pHelicopter.i_ObjectType);
        pHelicopter.writeSpriteToStream(_outputStream);

        for(int i=0; i<MAX_TREES; i++)
        {
            _outputStream.writeBoolean(Trees[i]);
        }

        _outputStream.writeInt(i_playerAttemptions);
        _outputStream.writeInt(i_GameTimer);
        _outputStream.writeInt(i_LastTime);
        _outputStream.writeInt(i_GenerateDelay);
        _outputStream.writeInt(i_LastHelicopterTime);
        _outputStream.writeInt(i_HelicopterDelay);
        _outputStream.writeInt(i_VSpeed);
        _outputStream.writeInt(i_CoconutX);

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

        i_type = _inputStream.readByte();
        activateSprite(pCoconut,i_type);
        pCoconut.readSpriteFromStream(_inputStream);

        i_type = _inputStream.readByte();
        activateSprite(pHelicopter,i_type);
        pHelicopter.readSpriteFromStream(_inputStream);

        for(int i=0; i<MAX_TREES; i++)
        {
            Trees[i] = _inputStream.readBoolean();
        }

        i_playerAttemptions = _inputStream.readInt();
        i_GameTimer = _inputStream.readInt();
        i_LastTime = _inputStream.readInt();
        i_GenerateDelay = _inputStream.readInt();
        i_LastHelicopterTime = _inputStream.readInt();
        i_HelicopterDelay = _inputStream.readInt();
        i_VSpeed = _inputStream.readInt();
        i_CoconutX = _inputStream.readInt();
    }

    public int nextGameStep()
    {
        CAbstractPlayer p_player = m_pPlayerList[0];
        p_player.nextPlayerMove(this, p_MoveObject);

        processSprites();
        processCoconut();
        processHelicopter();

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
