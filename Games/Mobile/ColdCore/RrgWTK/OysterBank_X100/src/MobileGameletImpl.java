
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.DataInputStream;


public class MobileGameletImpl extends MobileGamelet
{
    public static final int RIGHT_MARGIN = (12 << 8);
    public static final int LEFT_MARGIN = (12 << 8);

    public static final int UNDERWATER_OFFSET_X = (0 << 8);
    public static final int UNDERWATER_OFFSET_Y = (12 << 8);

    public static final int SHELLS_X = (14 << 8);
    public static final int SHELLS_Y = (110 << 8);
    public static final int SHELL_SIZE = (20 << 8);

    public static final int WATER_LINE = ((42-12) << 8);
    public static final int INIT_PLAYER_Y = ((42-12) << 8);

    public static final int STOP_PLAYER_Y = (SHELLS_Y - 0x0600);

    public static final int SHARK_MARGIN_UP = (8 << 8);
    public static final int SHARK_MARGIN_DOWN = (40 << 8);

    public static final int MIN_SHELL_DELAY = 30;
    public static final int MAX_SHELL_DELAY = 60;

    public static final int SHARK_SPEED = (0x0180);
    public static final int BOAT_SPEED = (0x0100);
    public static final int DIVER_SPEED = (0x0100);
    public static final int DIVE_SPEED = (0x0100);

    public static final int BUBBLE_SPEED = (0x0400);

    private static final int PLAYER_BUBBLE = 128;

    private static int I8_SCREEN_WIDTH;

    private static int I8_SCREEN_HEIGHT;

    public MoveObject p_MoveObject;

    public static final int GAMEACTION_DIVER_GET_PEARL          = 0;
    public static final int GAMEACTION_DIVER_DEAD               = 1;
    public static final int GAMEACTION_SHELL_OPEN               = 2;
    public static final int GAMEACTION_SHELL_CLOSE              = 3;
    public static final int GAMEACTION_SHARK_ATTACK             = 4;
    public static final int GAMEACTION_SHARK_GENERATED          = 5;
    public static final int GAMEACTION_OXYGEN                   = 6;
    public static final int GAMEACTION_BUBBLE_GENERATED         = 7;
    public static final int GAMEACTION_SPLASH                   = 8;


    public static final int SCORES_PEARL = 5;


    private int i_timedelay;

    public int i_playerAttemptions;

    public int i_GameTimer;

    public int i_LastTime;
    public int i_GenerateDelay;

    public int i_LastSharkTime;
    public int i_SharkDelay;

    public int i_PearlCounter;

    public static final int LEVEL_EASY = 0;

    private static final int LEVEL_EASY_ATTEMPTIONS = 4;

    private static final int LEVEL_EASY_GENERATION = 20;
    private static final int LEVEL_EASY_SHARK = 300;
    private static final int LEVEL_EASY_OXYGEN = 300;

    private static final int LEVEL_EASY_TIMEDELAY = 130;

    public static final int LEVEL_NORMAL = 1;

    private static final int LEVEL_NORMAL_ATTEMPTIONS = 3;

    private static final int LEVEL_NORMAL_GENERATION = 40;
    private static final int LEVEL_NORMAL_SHARK = 200;

    private static final int LEVEL_NORMAL_OXYGEN = 250;

    private static final int LEVEL_NORMAL_TIMEDELAY = 90;

    public static final int LEVEL_HARD = 2;

    private static final int LEVEL_HARD_ATTEMPTIONS = 2;

    private static final int LEVEL_HARD_GENERATION = 50;
    private static final int LEVEL_HARD_SHARK = 200;
    private static final int LEVEL_HARD_OXYGEN = 200;

    private static final int LEVEL_HARD_TIMEDELAY = 80;


    public Sprite pPlayerSprite;

    public static final int MAX_SPRITES_NUMBER = 32;

    public Sprite[] ap_Sprites;

    private static final int SPRITEDATALENGTH = 10;

    public static final int SPRITE_DIVER = 0;
    public static final int SPRITE_DIVER_UP = 1;
    public static final int SPRITE_DIVER_LEFT = 2;
    public static final int SPRITE_DIVER_RIGHT = 3;
    public static final int SPRITE_DIVER_TURN_UP = 4;
    public static final int SPRITE_DIVER_TURN_LEFT = 5;
    public static final int SPRITE_DIVER_TURN_RIGHT = 6;
    public static final int SPRITE_DIVER_TAKE_PEARL_LEFT = 7;
    public static final int SPRITE_DIVER_TAKE_PEARL_RIGHT = 8;
    public static final int SPRITE_DIVER_DEAD = 9;
    public static final int SPRITE_SHELL_OPEN = 10;
    public static final int SPRITE_SHELL_CLOSE = 11;
    public static final int SPRITE_PEARL_BOAT_WEST = 12;
    public static final int SPRITE_PEARL_BOAT_EAST = 13;
    public static final int SPRITE_PEARL_BOAT_SOUTH = 14;
    public static final int SPRITE_SHARK_LEFT = 15;
    public static final int SPRITE_SHARK_RIGHT = 16;
    public static final int SPRITE_SHARK_ATTACK_LEFT = 17;
    public static final int SPRITE_SHARK_ATTACK_RIGHT = 18;
    public static final int SPRITE_OXYGEN_BUBBLE = 19;


    private static final int [] ai_SpriteParameters = new int[]
    {
        0x0600, 0x0600, -0x001, 0x0000, 0x0200, 0x0200, 1, 1, Sprite.SPRITE_ALIGN_CENTER, Sprite.ANIMATION_CYCLIC,
        0x1000, 0x1D00, 0x0000, 0x0000, 0x0A00, 0x1900, 2, 1, Sprite.SPRITE_ALIGN_CENTER, Sprite.ANIMATION_CYCLIC,
        0x1A00, 0x1C00, 0x0400, 0x0200, 0x0A00, 0x1800, 2, 1, Sprite.SPRITE_ALIGN_CENTER, Sprite.ANIMATION_CYCLIC,
        0x1A00, 0x1C00, 0x0C00, 0x0200, 0x0A00, 0x1800, 2, 1, Sprite.SPRITE_ALIGN_CENTER, Sprite.ANIMATION_CYCLIC,
        0x0E00, 0x1400, -0x0001, 0x0000, 0x1000, 0x1000, 1, 1, Sprite.SPRITE_ALIGN_CENTER, Sprite.ANIMATION_FROZEN,
        0x0E00, 0x1400, -0x0001, 0x0000, 0x0E00, 0x1200, 1, 1, Sprite.SPRITE_ALIGN_CENTER, Sprite.ANIMATION_FROZEN,
        0x0E00, 0x1400, -0x0001, 0x0000, 0x0E00, 0x1200, 1, 1, Sprite.SPRITE_ALIGN_CENTER, Sprite.ANIMATION_FROZEN,
        0x1A00, 0x1C00, 0x0400, 0x0200, 0x0C00, 0x1800, 2, 1, Sprite.SPRITE_ALIGN_CENTER, Sprite.ANIMATION_FROZEN,
        0x1A00, 0x1C00, 0x0A00, 0x0200, 0x0C00, 0x1800, 2, 1, Sprite.SPRITE_ALIGN_CENTER, Sprite.ANIMATION_FROZEN,
        0x1500, 0x2100, -0x0001, 0x0000, 0x1500, 0x2100, 26, 1, Sprite.SPRITE_ALIGN_CENTER, Sprite.ANIMATION_FROZEN,
        0x1100, 0x1000, 0x0400, 0x0600, 0x0400, 0x0600, 3, 1, Sprite.SPRITE_ALIGN_CENTER, Sprite.ANIMATION_FROZEN,
        0x1100, 0x1000, 0x0400, 0x0600, 0x0400, 0x0600, 1, 1, Sprite.SPRITE_ALIGN_CENTER, Sprite.ANIMATION_FROZEN,
        0x3700, 0x1300, -0x0001, 0x0000, 0x2400, 0x0800, 1, 1, Sprite.SPRITE_ALIGN_CENTER, Sprite.ANIMATION_CYCLIC,
        0x3700, 0x1300, -0x0001, 0x0000, 0x2400, 0x0800, 1, 1, Sprite.SPRITE_ALIGN_CENTER, Sprite.ANIMATION_CYCLIC,
        0x3700, 0x1300, -0x0001, 0x0000, 0x1800, 0x0800, 1, 1, Sprite.SPRITE_ALIGN_CENTER, Sprite.ANIMATION_CYCLIC,
        0x2F00, 0x1A00,  0x0300, 0x0700, 0x0C00, 0x0C00, 4, 2, Sprite.SPRITE_ALIGN_CENTER, Sprite.ANIMATION_CYCLIC,
        0x2F00, 0x1A00,  0x1F00, 0x0700, 0x0C00, 0x0C00, 4, 2, Sprite.SPRITE_ALIGN_CENTER, Sprite.ANIMATION_CYCLIC,
        0x2C00, 0x1200, -0x0001, 0x0000, 0x2C00, 0x1200, 1, 1, Sprite.SPRITE_ALIGN_CENTER, Sprite.ANIMATION_FROZEN,
        0x2C00, 0x1200, -0x0001, 0x0000, 0x2C00, 0x1200, 1, 1, Sprite.SPRITE_ALIGN_CENTER, Sprite.ANIMATION_FROZEN,
        0x0300, 0x0300, -0x0001, 0x0000, 0x0300, 0x0300, 1, 4, Sprite.SPRITE_ALIGN_CENTER, Sprite.ANIMATION_FROZEN
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
                    case SPRITE_PEARL_BOAT_SOUTH:
                        activateSprite(p_Sprite, p_Sprite.i_ObjectState);
                        break;
                    case SPRITE_SHARK_ATTACK_LEFT:
                        activateSprite(p_Sprite, SPRITE_SHARK_LEFT);
                        break;
                    case SPRITE_SHARK_ATTACK_RIGHT:
                        activateSprite(p_Sprite, SPRITE_SHARK_RIGHT);
                        break;
                }
            }
            switch (p_Sprite.i_ObjectType)
            {
                case SPRITE_SHELL_CLOSE:
                    if(p_Sprite.i_ObjectState > 0)
                    {
                        p_Sprite.i_ObjectState--;
                    }
                    else
                    {
                        if((i_LastTime + i_GenerateDelay) < i_GameTimer )
                        {
                            i_LastTime = i_GameTimer;
                            m_pAbstractGameActionListener.processGameAction(GAMEACTION_SHELL_OPEN);
                            activateSprite(p_Sprite, SPRITE_SHELL_OPEN);
                            p_Sprite.i_ObjectState = MIN_SHELL_DELAY + getRandomInt(MAX_SHELL_DELAY - MIN_SHELL_DELAY);
                            generateBubbles(p_Sprite, 1 + getRandomInt(2));
                        }
                    }
                    break;
                case SPRITE_SHELL_OPEN:
                    if(p_Sprite.i_ObjectState > 0)
                    {
                        p_Sprite.i_ObjectState--;

                        switch(pPlayerSprite.i_ObjectType)
                        {
                            case SPRITE_DIVER_LEFT:
                                if(p_Sprite.isCollided(pPlayerSprite))
                                {
                                    activateSprite(pPlayerSprite, SPRITE_DIVER_TAKE_PEARL_LEFT);
                                }
                                break;
                            case SPRITE_DIVER_RIGHT:
                                if(p_Sprite.isCollided(pPlayerSprite))
                                {
                                    activateSprite(pPlayerSprite, SPRITE_DIVER_TAKE_PEARL_RIGHT);
                                }
                                break;
                            case SPRITE_DIVER_TAKE_PEARL_LEFT:
                                if(p_Sprite.isCollided(pPlayerSprite))
                                {
                                    activateSprite(p_Sprite, SPRITE_SHELL_CLOSE);
                                    m_pAbstractGameActionListener.processGameAction(GAMEACTION_SHELL_CLOSE);
                                    p_Sprite.i_ObjectState = MIN_SHELL_DELAY + getRandomInt(MAX_SHELL_DELAY - MIN_SHELL_DELAY);
                                    addScore(SCORES_PEARL + i_PearlCounter * 2);
                                    i_PearlCounter++;
                                    m_pAbstractGameActionListener.processGameAction(GAMEACTION_DIVER_GET_PEARL);
                                }
                                break;
                            case SPRITE_DIVER_TAKE_PEARL_RIGHT:
                                if(p_Sprite.isCollided(pPlayerSprite))
                                {
                                    activateSprite(p_Sprite, SPRITE_SHELL_CLOSE);
                                    m_pAbstractGameActionListener.processGameAction(GAMEACTION_SHELL_CLOSE);
                                    p_Sprite.i_ObjectState = MIN_SHELL_DELAY + getRandomInt(MAX_SHELL_DELAY - MIN_SHELL_DELAY);
                                    addScore(SCORES_PEARL + i_PearlCounter * 2);
                                    i_PearlCounter++;
                                    m_pAbstractGameActionListener.processGameAction(GAMEACTION_DIVER_GET_PEARL);
                                }
                                break;
                        }

                    }
                    else
                    {
                        m_pAbstractGameActionListener.processGameAction(GAMEACTION_SHELL_CLOSE);
                        activateSprite(p_Sprite, SPRITE_SHELL_CLOSE);
                        p_Sprite.i_ObjectState = MIN_SHELL_DELAY + getRandomInt(MAX_SHELL_DELAY - MIN_SHELL_DELAY);
                    }
                    break;
                case SPRITE_PEARL_BOAT_WEST:
                    p_Sprite.setMainPointXY(p_Sprite.i_mainX - BOAT_SPEED, p_Sprite.i_mainY);
                    if(p_Sprite.i_mainX < LEFT_MARGIN)
                    {
                        activateSprite(p_Sprite, SPRITE_PEARL_BOAT_SOUTH);
                        p_Sprite.i_ObjectState = SPRITE_PEARL_BOAT_EAST;
                    }
                    switch(pPlayerSprite.i_ObjectType)
                    {
                        case SPRITE_DIVER:
                            pPlayerSprite.setMainPointXY(p_Sprite.i_mainX, p_Sprite.i_mainY);
                            break;
                        case SPRITE_DIVER_UP:
                            if(p_Sprite.isCollided(pPlayerSprite))
                            {
                                activateSprite(pPlayerSprite, SPRITE_DIVER);
                                pPlayerSprite.setMainPointXY(p_Sprite.i_mainX, p_Sprite.i_mainY);
                                m_pAbstractGameActionListener.processGameAction(GAMEACTION_SPLASH);
                            }
                            break;
                    }
                    break;
                case SPRITE_PEARL_BOAT_EAST:
                    p_Sprite.setMainPointXY(p_Sprite.i_mainX + BOAT_SPEED, p_Sprite.i_mainY);
                    if((p_Sprite.i_mainX) > (I8_SCREEN_WIDTH - LEFT_MARGIN))
                    {
                        activateSprite(p_Sprite, SPRITE_PEARL_BOAT_SOUTH);
                        p_Sprite.i_ObjectState = SPRITE_PEARL_BOAT_WEST;
                    }
                    switch(pPlayerSprite.i_ObjectType)
                    {
                        case SPRITE_DIVER:
                            pPlayerSprite.setMainPointXY(p_Sprite.i_mainX, p_Sprite.i_mainY);
                            break;
                        case SPRITE_DIVER_UP:
                            if(p_Sprite.isCollided(pPlayerSprite))
                            {
                                activateSprite(pPlayerSprite, SPRITE_DIVER);
                                pPlayerSprite.setMainPointXY(p_Sprite.i_mainX, p_Sprite.i_mainY);
                                m_pAbstractGameActionListener.processGameAction(GAMEACTION_SPLASH);
                            }
                            break;
                    }
                    break;
                case SPRITE_SHARK_LEFT:
                    p_Sprite.setMainPointXY(p_Sprite.i_mainX - SHARK_SPEED, p_Sprite.i_mainY);
                    if(pPlayerSprite.i_ObjectType != SPRITE_DIVER_DEAD)
                    {
                        if(p_Sprite.isCollided(pPlayerSprite))
                        {
                            m_pAbstractGameActionListener.processGameAction(GAMEACTION_SHARK_ATTACK);
                            activateSprite(p_Sprite, SPRITE_SHARK_ATTACK_LEFT);
                            activateSprite(pPlayerSprite, SPRITE_DIVER_DEAD);
                            m_pAbstractGameActionListener.processGameAction(GAMEACTION_DIVER_DEAD);
                        }
                    }
                    if((p_Sprite.i_mainX + p_Sprite.i_width / 2) < 0) deactivateSprite(p_Sprite);
                    break;
                case SPRITE_SHARK_RIGHT:
                    p_Sprite.setMainPointXY(p_Sprite.i_mainX + SHARK_SPEED, p_Sprite.i_mainY);
                    if(pPlayerSprite.i_ObjectType != SPRITE_DIVER_DEAD)
                    {
                        if(p_Sprite.isCollided(pPlayerSprite))
                        {
                            m_pAbstractGameActionListener.processGameAction(GAMEACTION_SHARK_ATTACK);
                            activateSprite(p_Sprite, SPRITE_SHARK_ATTACK_RIGHT);
                            activateSprite(pPlayerSprite, SPRITE_DIVER_DEAD);
                            m_pAbstractGameActionListener.processGameAction(GAMEACTION_DIVER_DEAD);
                        }
                    }
                    if((p_Sprite.i_mainX - p_Sprite.i_width / 2) > I8_SCREEN_WIDTH) deactivateSprite(p_Sprite);
                    break;
                case SPRITE_OXYGEN_BUBBLE:
                    p_Sprite.setMainPointXY(p_Sprite.i_mainX, p_Sprite.i_mainY - p_Sprite.i_ObjectState);
                    if((p_Sprite.i_mainY - p_Sprite.i_height) < WATER_LINE) deactivateSprite(p_Sprite);
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
                case SPRITE_DIVER_TAKE_PEARL_LEFT:
                case SPRITE_DIVER_TAKE_PEARL_RIGHT:
                    activateSprite(pPlayerSprite, SPRITE_DIVER_TURN_UP);
                    break;
                case SPRITE_DIVER_TURN_UP:
                    activateSprite(pPlayerSprite, SPRITE_DIVER_UP);
                    break;
                case SPRITE_DIVER_TURN_LEFT:
                    activateSprite(pPlayerSprite, SPRITE_DIVER_LEFT);
                    break;
                case SPRITE_DIVER_TURN_RIGHT:
                    activateSprite(pPlayerSprite, SPRITE_DIVER_RIGHT);
                    break;
                case SPRITE_DIVER_DEAD:
                    return false;
            }
        }

        int state = pPlayerSprite.i_ObjectType;

        if(state == SPRITE_DIVER_UP || state == SPRITE_DIVER_RIGHT)
        {
            if ((_buttonState & MoveObject.BUTTON_LEFT) != 0)
            {
                if(i_mx > (0x0000 + LEFT_MARGIN))
                {
                    activateSprite(pPlayerSprite, SPRITE_DIVER_TURN_LEFT);
                }
            }
        }
        if(state == SPRITE_DIVER_LEFT || state == SPRITE_DIVER_UP)
        {
            if ((_buttonState & MoveObject.BUTTON_RIGHT) != 0)
            {
                if(i_mx < (I8_SCREEN_WIDTH - RIGHT_MARGIN))
                {
                    activateSprite(pPlayerSprite, SPRITE_DIVER_TURN_RIGHT);
                }
            }
        }
        if(state == SPRITE_DIVER_UP)
        {
            if ((_buttonState & MoveObject.BUTTON_DOWN) != 0)
            {
                if(getRandomInt(16) < 8)
                {
                    if(i_mx > (0x0000 + LEFT_MARGIN))
                    {
                        activateSprite(pPlayerSprite, SPRITE_DIVER_TURN_LEFT);
                    }
                    else
                    {
                        activateSprite(pPlayerSprite, SPRITE_DIVER_TURN_RIGHT);
                    }
                }
                else
                {
                    if(i_mx < (I8_SCREEN_WIDTH - RIGHT_MARGIN))
                    {
                        activateSprite(pPlayerSprite, SPRITE_DIVER_TURN_RIGHT);
                    }
                    else
                    {
                        activateSprite(pPlayerSprite, SPRITE_DIVER_TURN_LEFT);
                    }
                }
            }
        }

        if(state == SPRITE_DIVER_LEFT || state == SPRITE_DIVER_RIGHT)
        {
            if ((_buttonState & MoveObject.BUTTON_UP) != 0)
            {
                if(i_my > (WATER_LINE))
                {
                    activateSprite(pPlayerSprite, SPRITE_DIVER_TURN_UP);
                }
            }
        }

        switch(pPlayerSprite.i_ObjectType)
        {
            case SPRITE_DIVER:
                i_PearlCounter = 0;
                if ((_buttonState & MoveObject.BUTTON_DOWN) != 0)
                {
                    if(getRandomInt(16) < 8)
                    {
                        activateSprite(pPlayerSprite, SPRITE_DIVER_TURN_LEFT);
                        pPlayerSprite.setMainPointXY(pPlayerSprite.i_mainX + UNDERWATER_OFFSET_X, pPlayerSprite.i_mainY + UNDERWATER_OFFSET_Y);
                    }
                    else
                    {
                        activateSprite(pPlayerSprite, SPRITE_DIVER_TURN_RIGHT);
                        pPlayerSprite.setMainPointXY(pPlayerSprite.i_mainX + UNDERWATER_OFFSET_X, pPlayerSprite.i_mainY + UNDERWATER_OFFSET_Y);
                    }
                    m_pAbstractGameActionListener.processGameAction(GAMEACTION_SPLASH);
                }
                else
                {
                        boolean oxygen = true;
                        pPlayerSprite.i_ObjectState += 3;
                        switch(getGameDifficultLevel())
                        {
                            case LEVEL_EASY:
                                if(pPlayerSprite.i_ObjectState > LEVEL_EASY_OXYGEN)
                                {
                                    pPlayerSprite.i_ObjectState = LEVEL_EASY_OXYGEN;
                                    oxygen = false;
                                }
                                break;
                            case LEVEL_NORMAL:
                                if(pPlayerSprite.i_ObjectState > LEVEL_NORMAL_OXYGEN)
                                {
                                    pPlayerSprite.i_ObjectState = LEVEL_NORMAL_OXYGEN;
                                    oxygen = false;
                                }
                                break;
                            case LEVEL_HARD:
                                if(pPlayerSprite.i_ObjectState > LEVEL_HARD_OXYGEN)
                                {
                                    pPlayerSprite.i_ObjectState = LEVEL_HARD_OXYGEN;
                                    oxygen = false;
                                }
                                break;
                        }
                        if(oxygen) m_pAbstractGameActionListener.processGameAction(GAMEACTION_OXYGEN);
                }
                break;
            case SPRITE_DIVER_UP:
                if(i_my > (WATER_LINE + UNDERWATER_OFFSET_Y))
                {
                    pPlayerSprite.setMainPointXY(pPlayerSprite.i_mainX, pPlayerSprite.i_mainY - DIVE_SPEED);
                }
                else
                {
                }
                break;
            case SPRITE_DIVER_LEFT:
                if(i_my > STOP_PLAYER_Y || (i_mx < (0x0000 + LEFT_MARGIN)) )
                {
                    activateSprite(pPlayerSprite, SPRITE_DIVER_TURN_UP);
                }
                else
                {
                    pPlayerSprite.setMainPointXY(pPlayerSprite.i_mainX - DIVER_SPEED, pPlayerSprite.i_mainY + DIVE_SPEED);
                    if(getRandomInt(PLAYER_BUBBLE) == (PLAYER_BUBBLE / 2))
                    {
                    }
                }
                break;
            case SPRITE_DIVER_RIGHT:
                if(i_my > STOP_PLAYER_Y || (i_mx > (I8_SCREEN_WIDTH - RIGHT_MARGIN)) )
                {
                    activateSprite(pPlayerSprite, SPRITE_DIVER_TURN_UP);
                }
                else
                {
                    pPlayerSprite.setMainPointXY(pPlayerSprite.i_mainX + DIVER_SPEED, pPlayerSprite.i_mainY + DIVE_SPEED);
                    if(getRandomInt(PLAYER_BUBBLE) == (PLAYER_BUBBLE / 2))
                    {
                    }
                }
                break;
        }
        if(i_my > (WATER_LINE + UNDERWATER_OFFSET_Y) && pPlayerSprite.i_ObjectType != SPRITE_DIVER_DEAD)
        {
            if(pPlayerSprite.i_ObjectState > 0)
            {
                pPlayerSprite.i_ObjectState--;
            }
            else
            {
                activateSprite(pPlayerSprite, SPRITE_DIVER_DEAD);
                m_pAbstractGameActionListener.processGameAction(GAMEACTION_DIVER_DEAD);
            }
        }
        return true;
    }

    private void generateShark()
    {
        if((i_LastSharkTime + i_SharkDelay) < i_GameTimer )
        {
            i_LastSharkTime = i_GameTimer;
            Sprite p_emptySprite = getInactiveSprite();
            if (p_emptySprite != null)
            {
                int y = WATER_LINE + SHARK_MARGIN_UP + getRandomInt(I8_SCREEN_HEIGHT - WATER_LINE - SHARK_MARGIN_DOWN);
                int x = I8_SCREEN_WIDTH;
                int type = SPRITE_SHARK_ATTACK_LEFT;

                activateSprite(p_emptySprite, type);

                if(getRandomInt(8) < 4)
                {
                    type = SPRITE_SHARK_LEFT;
                    x = I8_SCREEN_WIDTH + p_emptySprite.i_width / 2;
                }
                else
                {
                    type = SPRITE_SHARK_RIGHT;
                    x = 0x0000 - p_emptySprite.i_width / 2;

                }

                activateSprite(p_emptySprite, type);
                p_emptySprite.lg_SpriteActive = true;
                p_emptySprite.setMainPointXY(x, y);
                m_pAbstractGameActionListener.processGameAction(GAMEACTION_SHARK_GENERATED);
            }
        }
    }

    private void generateBoat()
    {
        Sprite p_emptySprite = getInactiveSprite();
        if (p_emptySprite != null)
        {
            int y = WATER_LINE;
            int x = I8_SCREEN_WIDTH - RIGHT_MARGIN;
            int type = SPRITE_PEARL_BOAT_WEST;

            activateSprite(p_emptySprite, type);
            p_emptySprite.lg_SpriteActive = true;
            p_emptySprite.setMainPointXY(x, y);
        }
    }

    private void generateShells()
    {
        int y = SHELLS_Y;
        int x = SHELLS_X;
        int type = SPRITE_SHELL_CLOSE;

        for(int i=0; i<6; i++)
        {
            Sprite p_emptySprite = getInactiveSprite();
            if (p_emptySprite != null)
            {
                activateSprite(p_emptySprite, type);
                p_emptySprite.lg_SpriteActive = true;
                p_emptySprite.setMainPointXY(x, y);
                p_emptySprite.i_ObjectState = MIN_SHELL_DELAY + getRandomInt(MAX_SHELL_DELAY - MIN_SHELL_DELAY);
            }
            x += SHELL_SIZE;
        }
    }


    private void generateBubbles(Sprite obj, int count)
    {
        int x = obj.i_mainX;
        int y = obj.i_mainY;
        int type = SPRITE_OXYGEN_BUBBLE;
        int speed = BUBBLE_SPEED;


        for(int i=0;i<count;i++)
        {
            Sprite p_emptySprite = getInactiveSprite();
            if (p_emptySprite != null)
            {
                activateSprite(p_emptySprite, type);
                p_emptySprite.lg_SpriteActive = true;
                p_emptySprite.setMainPointXY(x, y);
                p_emptySprite.i_ObjectState = speed;
                m_pAbstractGameActionListener.processGameAction(GAMEACTION_BUBBLE_GENERATED);
            }
            speed /= 2;
        }
    }


public boolean newGameSession(int _gameAreaWidth, int _gameAreaHeight, int _gameLevel, CAbstractPlayer[] _players,startup _listener, String _staticArrayResourceName)
    {
        super.newGameSession(_gameAreaWidth, _gameAreaHeight, _gameLevel, _players, _listener, _staticArrayResourceName);

        I8_SCREEN_WIDTH = _gameAreaWidth<<8;
        I8_SCREEN_HEIGHT = 128 << 8; 

        switch (_gameLevel)
        {
            case LEVEL_EASY:
                {
                    i_timedelay = LEVEL_EASY_TIMEDELAY;
                    i_playerAttemptions = LEVEL_EASY_ATTEMPTIONS;
                    i_GenerateDelay = LEVEL_EASY_GENERATION;
                    i_SharkDelay = LEVEL_EASY_SHARK;
                }
                break;
            case LEVEL_NORMAL:
                {
                    i_timedelay = LEVEL_NORMAL_TIMEDELAY;
                    i_playerAttemptions = LEVEL_NORMAL_ATTEMPTIONS;
                    i_GenerateDelay = LEVEL_NORMAL_GENERATION;
                    i_SharkDelay = LEVEL_NORMAL_SHARK;
                }
                break;
            case LEVEL_HARD:
                {
                    i_timedelay = LEVEL_HARD_TIMEDELAY;
                    i_playerAttemptions = LEVEL_HARD_ATTEMPTIONS;
                    i_GenerateDelay = LEVEL_HARD_GENERATION;
                    i_SharkDelay = LEVEL_HARD_SHARK;
                }
                break;
        }

        i_GameTimer = 0;
        i_LastTime = i_GameTimer;
        i_LastSharkTime = i_GameTimer;

        i_PearlCounter = 0;

        activateSprite(pPlayerSprite, SPRITE_DIVER);
        pPlayerSprite.setMainPointXY(I8_SCREEN_WIDTH / 2, INIT_PLAYER_Y);

        switch(getGameDifficultLevel())
        {
            case LEVEL_EASY:
                pPlayerSprite.i_ObjectState = LEVEL_EASY_OXYGEN;
                break;
            case LEVEL_NORMAL:
                pPlayerSprite.i_ObjectState = LEVEL_NORMAL_OXYGEN;
                break;
            case LEVEL_HARD:
                pPlayerSprite.i_ObjectState = LEVEL_HARD_OXYGEN;
                break;
        }

        generateBoat();
        generateShells();

        return true;
    }

    public void resumeGameAfterPlayerLostOrPaused()
    {
        super.resumeGameAfterPlayerLostOrPaused();

        deinitState();
        initState();

        i_GameTimer = 0;
        i_LastTime = i_GameTimer;
        i_LastSharkTime = i_GameTimer;

        activateSprite(pPlayerSprite, SPRITE_DIVER);
        pPlayerSprite.setMainPointXY(I8_SCREEN_WIDTH, INIT_PLAYER_Y);

        generateBoat();
        generateShells();

        switch(getGameDifficultLevel())
        {
            case LEVEL_EASY:
                pPlayerSprite.i_ObjectState = LEVEL_EASY_OXYGEN;
                break;
            case LEVEL_NORMAL:
                pPlayerSprite.i_ObjectState = LEVEL_NORMAL_OXYGEN;
                break;
            case LEVEL_HARD:
                pPlayerSprite.i_ObjectState = LEVEL_HARD_OXYGEN;
                break;
        }

    }

    public String getGameTextID()
    {
        return "PEARLDIVER";
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
        return (MAX_SPRITES_NUMBER+1)*(Sprite.DATASIZE_BYTES+1)+28;
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
        _outputStream.writeInt(i_LastSharkTime);
        _outputStream.writeInt(i_SharkDelay);
        _outputStream.writeInt(i_PearlCounter);
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
        i_LastSharkTime = _inputStream.readInt();
        i_SharkDelay = _inputStream.readInt();
        i_PearlCounter = _inputStream.readInt();
    }

    public int nextGameStep()
    {
        CAbstractPlayer p_player = m_pPlayerList[0];
        p_player.nextPlayerMove(this, p_MoveObject);

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

        processSprites();
        generateShark();

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

