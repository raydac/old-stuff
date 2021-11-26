
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.DataInputStream;


public class MobileGameletImpl extends MobileGamelet
{
    public static final int TARAKAN_FAST_SPEED = (8 << 8);
    public static final int TARAKAN_SPEED = (4 << 8);
    public static final int TARAKAN_SLOW_SPEED = (2 << 8);

    public static final int TARAKAN_DISABLE_TIMER = 30;
    public static final int TARAKAN_SPEED_TIMER = 200;
    public static final int TARAKAN_SKIN_TIMER = 200;

    private static int I8_SCREEN_WIDTH;

    private static int I8_SCREEN_HEIGHT;

    public MoveObject p_MoveObject;

    public static final int SCORES_SAUSAGE = 20;
    public static final int SCORES_JELLY = -10;
    public static final int SCORE_FOR_TIME = -1;

    public static final int GAMEACTION_TARAKAN_WIN              = 0;
    public static final int GAMEACTION_TARAKAN_PARALIZED         = 1;
    public static final int GAMEACTION_TARAKAN_RELIEF          = 2;
    public static final int GAMEACTION_TARAKAN_RESTORE_SPEED    = 3;
    public static final int GAMEACTION_TARAKAN_CHANGE_SKIN_BACK = 4;
    public static final int GAMEACTION_TARAKAN_EAT_SAUSAGE      = 5;
    public static final int GAMEACTION_TARAKAN_EAT_FISH_SKELETON        = 6;
    public static final int GAMEACTION_TARAKAN_EAT_MUSHROOM     = 7;
    public static final int GAMEACTION_TARAKAN_EAT_BREAD_CRUST  = 8;
    public static final int GAMEACTION_TARAKAN_EAT_APPLE_BIT    = 9;
    public static final int GAMEACTION_TARAKAN_EAT_JELLY        = 10;

    public static final int CELL_SIZE  = 32;

    public static final int LEFT_WALL       = 0x01; 
    public static final int UP_WALL         = 0x02; 
    public static final int RIGHT_WALL      = 0x04; 
    public static final int DOWN_WALL       = 0x08; 
    public static final int FINISH_POSITION = 0x20; 
    public static final int START_POSITION  = 0x40; 


   protected byte ab_initGameArray[][];

    public short as_GameArray[][]= new short[16][16];
    public int getCell(int x, int y)
    {
         return as_GameArray[y & 0x0F][x & 0x0F];
    }

    public void putTile(int x, int y, int tile)
    {
         x &= 0xf;
         y &= 0xf;

         as_GameArray[y][x] = (short) (as_GameArray[y][x] & 0xff | ( tile << 8 ));
    }

    public int getTile(int x, int y)
    {
         return as_GameArray[y & 0xf][x & 0xf] >> 8;
    }


    public void changeCell(int x, int y, int v)
    {
            x &= 0x0F;
            y &= 0x0F;

            as_GameArray[y][x] = (short)(as_GameArray[y][x] & ~v);
    }

    public int i_player_bx;
    public int i_player_by;

    public int i_finish_bx;
    public int i_finish_by;

    public int i_Speed;

    public boolean b_Skin;

    public int i_FlipMode;

    public int i_DisableTimer;
    public int i_SpeedTimer;
    public int i_SkinTimer;

    private int i_timedelay;

    private int i_ticksPerSeconds;

    private int i_pressedKey;

    public int i_playerAttemptions;

    public int i_GameTimer;

    public int i_LastTime;
    public int i_GenerateDelay;

    public static final int LEVEL_EASY = 0;

    private static final int LEVEL_EASY_ATTEMPTIONS = 4;

    private static final int LEVEL_EASY_GENERATION = 50;
    private static final int LEVEL_EASY_INITIAL_SCORE = 150;
    private static final int LEVEL_EASY_BONUS_SAUSAGE = 12;
    private static final int LEVEL_EASY_BONUS_CHEESE = 4;
    private static final int LEVEL_EASY_BONUS_SPOON = 6;
    private static final int LEVEL_EASY_BONUS_FISH_SKELETON = 8;
    private static final int LEVEL_EASY_BONUS_KISSEL = 5;
    private static final int LEVEL_EASY_BONUS_BRED_CRUST = 3;
    private static final int LEVEL_EASY_BONUS_POISON = 6;
    private static final int LEVEL_EASY_BONUS_SWEET = 4;

    private static final int LEVEL_EASY_TIMEDELAY = 100;

    public static final int LEVEL_NORMAL = 1;

    private static final int LEVEL_NORMAL_ATTEMPTIONS = 3;

    private static final int LEVEL_NORMAL_GENERATION = 40;
    private static final int LEVEL_NORMAL_INITIAL_SCORE = 100;
    private static final int LEVEL_NORMAL_BONUS_SAUSAGE = 10;
    private static final int LEVEL_NORMAL_BONUS_CHEESE = 5;
    private static final int LEVEL_NORMAL_BONUS_SPOON = 6;
    private static final int LEVEL_NORMAL_BONUS_FISH_SKELETON = 7;
    private static final int LEVEL_NORMAL_BONUS_KISSEL = 5;
    private static final int LEVEL_NORMAL_BONUS_BRED_CRUST = 4;
    private static final int LEVEL_NORMAL_BONUS_POISON = 4;
    private static final int LEVEL_NORMAL_BONUS_SWEET = 5;

    private static final int LEVEL_NORMAL_TIMEDELAY = 90;

    public static final int LEVEL_HARD = 2;

    private static final int LEVEL_HARD_ATTEMPTIONS = 2;

    private static final int LEVEL_HARD_GENERATION = 30;
    private static final int LEVEL_HARD_INITIAL_SCORE = 50;
    private static final int LEVEL_HARD_BONUS_SAUSAGE = 8;
    private static final int LEVEL_HARD_BONUS_CHEESE = 6;
    private static final int LEVEL_HARD_BONUS_SPOON = 4;
    private static final int LEVEL_HARD_BONUS_FISH_SKELETON = 10;
    private static final int LEVEL_HARD_BONUS_KISSEL = 4;
    private static final int LEVEL_HARD_BONUS_BRED_CRUST = 6;
    private static final int LEVEL_HARD_BONUS_POISON = 4;
    private static final int LEVEL_HARD_BONUS_SWEET = 4;

    private static final int LEVEL_HARD_TIMEDELAY = 80;


    public Sprite pPlayerSprite;

    public static final int MAX_SPRITES_NUMBER = 4;

    public Sprite[] ap_Sprites;

    private static final int SPRITEDATALENGTH = 8;

    public static final int SPRITE_TARAKAN_LEFT = 0;
    public static final int SPRITE_TARAKAN_UP = 1;
    public static final int SPRITE_TARAKAN_RIGHT = 2;
    public static final int SPRITE_TARAKAN_DOWN = 3;
    public static final int SPRITE_TARAKAN_TURN_LEFT = 4;
    public static final int SPRITE_TARAKAN_TURN_UP = 5;
    public static final int SPRITE_TARAKAN_TURN_RIGHT = 6;
    public static final int SPRITE_TARAKAN_TURN_DOWN = 7;
    public static final int SPRITE_TARAKAN_DISABLED = 8;
    public static final int SPRITE_TARAKAN_DEAD = 9;

    public static final int SPRITE_SCORE = 10;

    public static final int OBJECT_SAUSAGE = 11;
    public static final int OBJECT_MUSHROOM = 12;
    public static final int OBJECT_APPLE_BIT = 13;
    public static final int OBJECT_JELLY = 14;
    public static final int OBJECT_BREAD_CRUST = 15;
    public static final int OBJECT_POISON = 16;
    public static final int OBJECT_FISH_SKELETON = 17;
    public static final int OBJECT_START = 18;
    public static final int OBJECT_FINISH = 19;


    private static final int [] ai_SpriteParameters = new int[]
    {
        0x1000, 0x0A00, 0x1000, 0x0A00, 2, 1, Sprite.SPRITE_ALIGN_CENTER, Sprite.ANIMATION_CYCLIC,
        0x0A00, 0x1000, 0x0A00, 0x1000, 2, 1, Sprite.SPRITE_ALIGN_CENTER, Sprite.ANIMATION_CYCLIC,
        0x1000, 0x0A00, 0x1000, 0x0A00, 2, 1, Sprite.SPRITE_ALIGN_CENTER, Sprite.ANIMATION_CYCLIC,
        0x0A00, 0x1000, 0x0A00, 0x1000, 2, 1, Sprite.SPRITE_ALIGN_CENTER, Sprite.ANIMATION_CYCLIC,
        0x1000, 0x1000, 0x1000, 0x1000, 1, 1, Sprite.SPRITE_ALIGN_CENTER, Sprite.ANIMATION_FROZEN,
        0x1000, 0x1000, 0x1000, 0x1000, 1, 1, Sprite.SPRITE_ALIGN_CENTER, Sprite.ANIMATION_FROZEN,
        0x1000, 0x1000, 0x1000, 0x1000, 1, 1, Sprite.SPRITE_ALIGN_CENTER, Sprite.ANIMATION_FROZEN,
        0x1000, 0x1000, 0x1000, 0x1000, 1, 1, Sprite.SPRITE_ALIGN_CENTER, Sprite.ANIMATION_FROZEN,
        0x1000, 0x1000, 0x1000, 0x1000, 7, 1, Sprite.SPRITE_ALIGN_CENTER, Sprite.ANIMATION_CYCLIC,
        0x1000, 0x1000, 0x1000, 0x1000, 5, 1, Sprite.SPRITE_ALIGN_CENTER, Sprite.ANIMATION_FROZEN,
        0x2400, 0x2400, 0x2400, 0x2400, 5, 2, Sprite.SPRITE_ALIGN_CENTER, Sprite.ANIMATION_FROZEN,
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
        boolean b_check_x = false;
        boolean b_check_y = false;

        Sprite p_Sprite;

        int xc = (pPlayerSprite.i_mainX >> 8) & 0x1ff;
        int yc = (pPlayerSprite.i_mainY >> 8) & 0x1ff;

        int i_tile = getTile(i_player_bx ,i_player_by);

        if( xc == (i_player_bx * CELL_SIZE + CELL_SIZE / 2))
        {
            b_check_x = true;
        }
        if( yc == (i_player_by * CELL_SIZE + CELL_SIZE / 2))
        {
            b_check_y = true;
        }

        for (int li = 0; li < MAX_SPRITES_NUMBER; li++)
        {
            p_Sprite = ap_Sprites[li];
            if (!p_Sprite.lg_SpriteActive) continue;
            if (p_Sprite.processAnimation())
            {
                switch (p_Sprite.i_ObjectType)
                {
                    case SPRITE_SCORE:
                        deactivateSprite(p_Sprite);
                        break;
                }
            }
        }

        if(i_tile > 0 && b_check_x && b_check_y)
        {
            switch (i_tile)
            {
                case OBJECT_POISON:
                    {
                        i_DisableTimer = TARAKAN_DISABLE_TIMER;
                        pPlayerSprite.i_ObjectState = pPlayerSprite.i_ObjectType;
                        activateSprite(pPlayerSprite, SPRITE_TARAKAN_DISABLED);
                        putTile(i_player_bx ,i_player_by, 0);
                        m_pAbstractGameActionListener.processGameAction(GAMEACTION_TARAKAN_PARALIZED);
                    }
                    break;

                case OBJECT_FISH_SKELETON:
                    {
                        b_Skin = true;
                        i_SkinTimer = TARAKAN_SKIN_TIMER;
                        putTile(i_player_bx ,i_player_by, 0);
                        m_pAbstractGameActionListener.processGameAction(GAMEACTION_TARAKAN_EAT_FISH_SKELETON);
                    }
                    break;
                case OBJECT_MUSHROOM:
                    {
                        {
                            i_Speed = TARAKAN_SLOW_SPEED;
                            i_SpeedTimer = TARAKAN_SPEED_TIMER;
                            putTile(i_player_bx ,i_player_by, 0);
                            m_pAbstractGameActionListener.processGameAction(GAMEACTION_TARAKAN_EAT_MUSHROOM);
                        }
                    }
                    break;
                case OBJECT_BREAD_CRUST:
                    {
                        {
                            i_Speed = TARAKAN_FAST_SPEED;
                            i_SpeedTimer = TARAKAN_SPEED_TIMER;
                            putTile(i_player_bx ,i_player_by, 0);
                            m_pAbstractGameActionListener.processGameAction(GAMEACTION_TARAKAN_EAT_BREAD_CRUST);
                        }
                    }
                    break;
                case OBJECT_APPLE_BIT:
                    {
                        xc = i_player_bx;
                        yc = i_player_by;
                        int cell = getCell(xc, yc);

                        if(true) 
                        {
                            switch(cell & (UP_WALL | DOWN_WALL | LEFT_WALL | RIGHT_WALL))
                            {
                                case UP_WALL:
                                    changeCell(xc, yc, UP_WALL);
                                    changeCell(xc, yc-1, DOWN_WALL);
                                    break;
                                case DOWN_WALL:
                                    changeCell(xc, yc, DOWN_WALL);
                                    changeCell(xc, yc+1, UP_WALL);
                                    break;
                                case LEFT_WALL:
                                    changeCell(xc, yc, LEFT_WALL);
                                    changeCell(xc-1, yc, RIGHT_WALL);
                                    break;
                                case RIGHT_WALL:
                                    changeCell(xc, yc, RIGHT_WALL);
                                    changeCell(xc+1, yc, LEFT_WALL);
                                    break;
                                case (LEFT_WALL | RIGHT_WALL | DOWN_WALL):
                                case (LEFT_WALL | RIGHT_WALL | UP_WALL):
                                case (LEFT_WALL | RIGHT_WALL):
                                    if((getRandomInt(32) & 1) == 0)
                                    {
                                        changeCell(xc, yc, RIGHT_WALL);
                                        changeCell(xc+1, yc, LEFT_WALL);
                                    }
                                    else
                                    {
                                        changeCell(xc, yc, LEFT_WALL);
                                        changeCell(xc-1, yc, RIGHT_WALL);
                                    }
                                    break;
                                case (UP_WALL | DOWN_WALL | RIGHT_WALL):
                                case (UP_WALL | DOWN_WALL | LEFT_WALL):
                                case (UP_WALL | DOWN_WALL):
                                    if((getRandomInt(32) & 1) == 0)
                                    {
                                        changeCell(xc, yc, UP_WALL);
                                        changeCell(xc, yc-1, DOWN_WALL);
                                    }
                                    else
                                    {
                                        changeCell(xc, yc, DOWN_WALL);
                                        changeCell(xc, yc+1, UP_WALL);
                                    }
                                    break;
                                case (UP_WALL | RIGHT_WALL):
                                    if((getRandomInt(32) & 1) == 0)
                                    {
                                        changeCell(xc, yc, RIGHT_WALL);
                                        changeCell(xc+1, yc, LEFT_WALL);
                                    }
                                    else
                                    {
                                        changeCell(xc, yc, UP_WALL);
                                        changeCell(xc, yc-1, DOWN_WALL);
                                    }
                                    break;
                                case (DOWN_WALL | RIGHT_WALL):
                                    if((getRandomInt(32) & 1) == 0)
                                    {
                                        changeCell(xc, yc, RIGHT_WALL);
                                        changeCell(xc+1, yc, LEFT_WALL);
                                    }
                                    else
                                    {
                                        changeCell(xc, yc, DOWN_WALL);
                                        changeCell(xc, yc+1, UP_WALL);
                                    }
                                    break;
                                case (LEFT_WALL | DOWN_WALL):
                                    if((getRandomInt(32) & 1) == 0)
                                    {
                                        changeCell(xc, yc, DOWN_WALL);
                                        changeCell(xc, yc+1, UP_WALL);
                                    }
                                    else
                                    {
                                        changeCell(xc, yc, LEFT_WALL);
                                        changeCell(xc-1, yc, RIGHT_WALL);
                                    }
                                    break;
                                case (LEFT_WALL | UP_WALL):
                                    if((getRandomInt(32) & 1) == 0)
                                    {
                                        changeCell(xc, yc, UP_WALL);
                                        changeCell(xc, yc-1, DOWN_WALL);
                                    }
                                    else
                                    {
                                        changeCell(xc, yc, LEFT_WALL);
                                        changeCell(xc-1, yc, RIGHT_WALL);
                                    }
                                    break;
                            }
                            putTile(i_player_bx ,i_player_by, 0);
                        }
                        m_pAbstractGameActionListener.processGameAction(GAMEACTION_TARAKAN_EAT_APPLE_BIT);
                    }
                    break;
                case OBJECT_SAUSAGE:
                    p_Sprite = getInactiveSprite();
                    if (p_Sprite != null)
                    {
                        activateSprite(p_Sprite, SPRITE_SCORE);
                        p_Sprite.setMainPointXY(pPlayerSprite.i_mainX,pPlayerSprite.i_mainY);
                        p_Sprite.i_ObjectState = addScore(SCORES_SAUSAGE);
                        putTile(i_player_bx ,i_player_by, 0);
                        m_pAbstractGameActionListener.processGameAction(GAMEACTION_TARAKAN_EAT_SAUSAGE);
                    }
                    break;
                case OBJECT_JELLY:
                    p_Sprite = getInactiveSprite();
                    if (p_Sprite != null)
                    {
                        activateSprite(p_Sprite, SPRITE_SCORE);
                        p_Sprite.setMainPointXY(pPlayerSprite.i_mainX,pPlayerSprite.i_mainY);
                        p_Sprite.i_ObjectState = addScore(SCORES_JELLY);
                        putTile(i_player_bx ,i_player_by, 0);
                        m_pAbstractGameActionListener.processGameAction(GAMEACTION_TARAKAN_EAT_JELLY);
                    }
                    break;
                default:
                    break;
            }

            m_pAbstractGameActionListener.processGameAction(pPlayerSprite.i_mainX >> (8+5),pPlayerSprite.i_mainY  >> (8+5));
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

        int i_pixel_x = (i_mx >> 8) & 0x1ff;
        int i_pixel_y = (i_my >> 8) & 0x1ff;

        i_player_bx = (i_pixel_x) / CELL_SIZE;
        i_player_by = (i_pixel_y) / CELL_SIZE;

        boolean check_x = false;
        boolean check_y = false;

        if(i_pixel_x == (i_player_bx * CELL_SIZE + CELL_SIZE / 2))
        {
            check_x = true;
        }
        if(i_pixel_y == (i_player_by * CELL_SIZE + CELL_SIZE / 2))
        {
            check_y = true;
        }

        int cell = getCell(i_player_bx, i_player_by);

        if(pPlayerSprite.processAnimation())
        {
            switch(pPlayerSprite.i_ObjectType)
            {
                case SPRITE_TARAKAN_TURN_UP:
                    activateSprite(pPlayerSprite, SPRITE_TARAKAN_UP);
                    break;
                case SPRITE_TARAKAN_TURN_DOWN:
                    activateSprite(pPlayerSprite, SPRITE_TARAKAN_DOWN);
                    break;
                case SPRITE_TARAKAN_TURN_LEFT:
                    activateSprite(pPlayerSprite, SPRITE_TARAKAN_LEFT);
                    break;
                case SPRITE_TARAKAN_TURN_RIGHT:
                    activateSprite(pPlayerSprite, SPRITE_TARAKAN_RIGHT);
                    break;
                case SPRITE_TARAKAN_DEAD:
                    return false;
            }
        }

        switch(pPlayerSprite.i_ObjectType)
        {
            case SPRITE_TARAKAN_UP:
            case SPRITE_TARAKAN_DOWN:
            case SPRITE_TARAKAN_LEFT:
            case SPRITE_TARAKAN_RIGHT:
                if(check_y)
                {
                    if ((_buttonState & MoveObject.BUTTON_LEFT) != 0)
                    {
                        if((cell & LEFT_WALL) == 0)
                        {
                            TurnTo(SPRITE_TARAKAN_LEFT);
                            i_pressedKey &= ~MoveObject.BUTTON_LEFT;
                        }
                    }
                    else if ((_buttonState & MoveObject.BUTTON_RIGHT) != 0)
                    {
                        if((cell & RIGHT_WALL) == 0)
                        {
                            TurnTo(SPRITE_TARAKAN_RIGHT);
                            i_pressedKey &= ~MoveObject.BUTTON_RIGHT;
                        }
                    }
                }

                if(check_x)
                {
                    if ((_buttonState & MoveObject.BUTTON_UP) != 0)
                    {
                        if((cell & UP_WALL) == 0)
                        {
                            TurnTo(SPRITE_TARAKAN_UP);
                            i_pressedKey &= ~MoveObject.BUTTON_UP;
                        }
                    }
                    else if ((_buttonState & MoveObject.BUTTON_DOWN) != 0)
                    {
                        if((cell & DOWN_WALL) == 0)
                        {
                            TurnTo(SPRITE_TARAKAN_DOWN);
                            i_pressedKey &= ~MoveObject.BUTTON_DOWN;
                        }
                    }
                }
                break;
            default:
                break;
        }
        if(i_SpeedTimer > 0)
        {
            i_SpeedTimer--;
        }
        else
        {
            if(check_x && check_y)
            {
                if(i_Speed != TARAKAN_SPEED)
                {
                    m_pAbstractGameActionListener.processGameAction(GAMEACTION_TARAKAN_RESTORE_SPEED);
                }
                i_Speed = TARAKAN_SPEED;
            }
        }

        switch(pPlayerSprite.i_ObjectType)
        {
            case SPRITE_TARAKAN_DISABLED:
                if(i_DisableTimer > 0)
                {
                    i_DisableTimer--;
                }
                else
                {
                    activateSprite(pPlayerSprite, pPlayerSprite.i_ObjectState);
                    m_pAbstractGameActionListener.processGameAction(GAMEACTION_TARAKAN_RELIEF);
                }
                break;
            case SPRITE_TARAKAN_LEFT:
                if(check_x)
                {
                    if((cell & LEFT_WALL) == 0)
                    {
                        i_mx -= i_Speed;
                    }
                    else
                    {
                        switch(cell & (UP_WALL | DOWN_WALL))
                        {
                            default:
                            case (0):
                            case (UP_WALL | DOWN_WALL):
                                TurnTo(SPRITE_TARAKAN_UP, SPRITE_TARAKAN_DOWN);
                                break;
                            case (UP_WALL):
                                TurnTo(SPRITE_TARAKAN_DOWN);
                                break;
                            case (DOWN_WALL):
                                TurnTo(SPRITE_TARAKAN_UP);
                                break;
                        }
                    }
                }
                else
                {
                    i_mx -= i_Speed;
                }
                break;
            case SPRITE_TARAKAN_RIGHT:
                if(check_x)
                {
                    if((cell & RIGHT_WALL) == 0)
                    {
                        i_mx += i_Speed;
                    }
                    else
                    {
                        switch(cell & (UP_WALL | DOWN_WALL))
                        {
                            default:
                            case (0):
                            case (UP_WALL | DOWN_WALL):
                                TurnTo(SPRITE_TARAKAN_UP, SPRITE_TARAKAN_DOWN);
                                break;
                            case (UP_WALL):
                                TurnTo(SPRITE_TARAKAN_DOWN);
                                break;
                            case (DOWN_WALL):
                                TurnTo(SPRITE_TARAKAN_UP);
                                break;
                        }
                    }
                }
                else
                {
                    i_mx += i_Speed;
                }
                break;
            case SPRITE_TARAKAN_UP:
                if(check_y)
                {
                    if((cell & UP_WALL) == 0)
                    {
                        i_my -= i_Speed;
                    }
                    else
                    {
                        switch(cell & (LEFT_WALL | RIGHT_WALL))
                        {
                            default:
                            case (0):
                            case (LEFT_WALL | RIGHT_WALL):
                                TurnTo(SPRITE_TARAKAN_LEFT, SPRITE_TARAKAN_RIGHT);
                                break;
                            case (LEFT_WALL):
                                TurnTo(SPRITE_TARAKAN_RIGHT);
                                break;
                            case (RIGHT_WALL):
                                TurnTo(SPRITE_TARAKAN_LEFT);
                                break;
                        }
                    }
                }
                else
                {
                    i_my -= i_Speed;
                }
                break;
            case SPRITE_TARAKAN_DOWN:
                if(check_y)
                {
                    if((cell & DOWN_WALL) == 0)
                    {
                        i_my += i_Speed;
                    }
                    else
                    {
                        switch(cell & (LEFT_WALL | RIGHT_WALL))
                        {
                            default:
                            case (0):
                            case (LEFT_WALL | RIGHT_WALL):
                                TurnTo(SPRITE_TARAKAN_LEFT, SPRITE_TARAKAN_RIGHT);
                                break;
                            case (LEFT_WALL):
                                TurnTo(SPRITE_TARAKAN_RIGHT);
                                break;
                            case (RIGHT_WALL):
                                TurnTo(SPRITE_TARAKAN_LEFT);
                                break;
                        }
                    }
                }
                else
                {
                    i_my += i_Speed;
                }
                break;
        }

        pPlayerSprite.setMainPointXY(i_mx, i_my);

        if(i_SkinTimer > 0)
        {
            i_SkinTimer--;
        }
        else
        {
            if(b_Skin) m_pAbstractGameActionListener.processGameAction(GAMEACTION_TARAKAN_CHANGE_SKIN_BACK);
            b_Skin = false;
        }
        return true;
    }

    private void TurnTo(int target1, int target2)
    {
        if( (getRandomInt(32) & 1) == 0 )
        {
            TurnTo(target1);
        }
        else
        {
            TurnTo(target2);
        }
    }


    private void TurnTo(int target)
    {
        pPlayerSprite.i_ObjectState = pPlayerSprite.i_ObjectType;

        if(pPlayerSprite.i_ObjectType == target) return;

        switch(target)
        {
            case SPRITE_TARAKAN_LEFT:
                activateSprite(pPlayerSprite, SPRITE_TARAKAN_TURN_LEFT);
                break;
            case SPRITE_TARAKAN_RIGHT:
                activateSprite(pPlayerSprite, SPRITE_TARAKAN_TURN_RIGHT);
                break;
            case SPRITE_TARAKAN_UP:
                activateSprite(pPlayerSprite, SPRITE_TARAKAN_TURN_UP);
                break;
            case SPRITE_TARAKAN_DOWN:
                activateSprite(pPlayerSprite, SPRITE_TARAKAN_TURN_DOWN);
                break;
        }
    }


    private void resetLevel(int mode)
    {
        int tmp;
        for(int row=0; row<16; row++)
        {
            for(int col=0; col<16; col++)
            {
                switch(mode)
                {
                    default:
                    case 0:
                        as_GameArray[row][col] = ab_initGameArray[row][col];
                        break;
                    case 1:
                        tmp = ab_initGameArray[row][col];
                        tmp = (tmp & ~0xA) | ((tmp & 2) << 2) | ((tmp & 8) >> 2);
                        as_GameArray[15-row][col] = (byte)tmp;
                        break;
                    case 2:
                        tmp = ab_initGameArray[row][col];
                        tmp = (tmp & ~0x5) | ((tmp & 1) << 2) | ((tmp & 4) >> 2);
                        as_GameArray[row][15-col] = (byte)tmp;
                        break;
                    case 3:
                        tmp = ab_initGameArray[row][col];
                        tmp = (tmp & ~0xF) | ((tmp & 3) << 2) | ((tmp & 12) >> 2);
                        as_GameArray[15-row][15-col] = (byte)tmp;
                        break;
                }

            }
        }
        i_FlipMode = mode;
    }

    private static final int LEVEL_SIZE = 16 * 16 * 2;

    private void saveLevel(DataOutputStream stream) throws IOException
    {
        for(int row=0; row<16; row++)
        {
            for(int col=0; col<16; col++)
            {
                stream.writeShort(as_GameArray[row][col]);
            }
        }
    }

    public void loadLevel(DataInputStream stream) throws IOException
    {
        for(int row=0; row<16; row++)
        {
            for(int col=0; col<16; col++)
            {
                as_GameArray[row][col] = stream.readShort();
            }
        }
    }


    private int getRandomBonusNumber(int n)
    {
        for(int i = n; i < 8; i++)
        {
            if(Bonuses[i] > 0) return i;
        }
        for(int i = 0; i < n; i++)
        {
            if(Bonuses[i] > 0) return i;
        }
        return -1;
    }

    private static int Bonuses[] = new int[]
    {
        0,0,0,0,0,0,0,0
    };

    private void generateLevel()
    {
        int BonusCounter = 0; 
        int BonusIndex = 1; 
        int CheckIndex = BonusIndex; 
        int CellIndex = 0; 
        int BonusStep = 2; 
        int i = 0;

        int s_index = 0;
        int f_index = 0;

        int Starts[] = new int[]
        {
            0,0,
            0,0,
            0,0,
            0,0,
            0,0,
        };

        int Finishs[] = new int[]
        {
            0,0,
            0,0,
            0,0,
            0,0,
            0,0,
        };

        switch(getGameDifficultLevel())
        {
            default:
            case GAMELEVEL_EASY:
                Bonuses[i] = LEVEL_EASY_BONUS_SAUSAGE;
                BonusCounter += Bonuses[i];
                i++;
                Bonuses[i] = LEVEL_EASY_BONUS_CHEESE;
                BonusCounter += Bonuses[i];
                i++;
                Bonuses[i] = LEVEL_EASY_BONUS_SPOON;
                BonusCounter += Bonuses[i];
                i++;
                Bonuses[i] = LEVEL_EASY_BONUS_FISH_SKELETON;
                BonusCounter += Bonuses[i];
                i++;
                Bonuses[i] = LEVEL_EASY_BONUS_KISSEL;
                BonusCounter += Bonuses[i];
                i++;
                Bonuses[i] = LEVEL_EASY_BONUS_BRED_CRUST;
                BonusCounter += Bonuses[i];
                i++;
                Bonuses[i] = LEVEL_EASY_BONUS_SWEET;
                BonusCounter += Bonuses[i];
                break;
            case GAMELEVEL_NORMAL:
                Bonuses[i] = LEVEL_NORMAL_BONUS_SAUSAGE;
                BonusCounter += Bonuses[i];
                i++;
                Bonuses[i] = LEVEL_NORMAL_BONUS_CHEESE;
                BonusCounter += Bonuses[i];
                i++;
                Bonuses[i] = LEVEL_NORMAL_BONUS_SPOON;
                BonusCounter += Bonuses[i];
                i++;
                Bonuses[i] = LEVEL_NORMAL_BONUS_FISH_SKELETON;
                BonusCounter += Bonuses[i];
                i++;
                Bonuses[i] = LEVEL_NORMAL_BONUS_KISSEL;
                BonusCounter += Bonuses[i];
                i++;
                Bonuses[i] = LEVEL_NORMAL_BONUS_BRED_CRUST;
                BonusCounter += Bonuses[i];
                i++;
                Bonuses[i] = LEVEL_NORMAL_BONUS_SWEET;
                BonusCounter += Bonuses[i];
                break;
            case GAMELEVEL_HARD:
                Bonuses[i] = LEVEL_HARD_BONUS_SAUSAGE;
                BonusCounter += Bonuses[i];
                i++;
                Bonuses[i] = LEVEL_HARD_BONUS_CHEESE;
                BonusCounter += Bonuses[i];
                i++;
                Bonuses[i] = LEVEL_HARD_BONUS_SPOON;
                BonusCounter += Bonuses[i];
                i++;
                Bonuses[i] = LEVEL_HARD_BONUS_FISH_SKELETON;
                BonusCounter += Bonuses[i];
                i++;
                Bonuses[i] = LEVEL_HARD_BONUS_KISSEL;
                BonusCounter += Bonuses[i];
                i++;
                Bonuses[i] = LEVEL_HARD_BONUS_BRED_CRUST;
                BonusCounter += Bonuses[i];
                i++;
                Bonuses[i] = LEVEL_HARD_BONUS_SWEET;
                BonusCounter += Bonuses[i];
                break;
        }

        BonusStep = (256-CheckIndex) / BonusCounter;

        for(int yc=0; yc < 16; yc++)
        {
            for(int xc=0; xc < 16; xc++)
            {
                int x = (xc * CELL_SIZE + CELL_SIZE / 2) << 8;
                int y = (yc * CELL_SIZE + CELL_SIZE / 2) << 8;

                switch(getCell(xc, yc) & 0xF0)
                {
                    default:
                        break;
                    case START_POSITION:
                        Starts[s_index++] = xc;
                        Starts[s_index++] = yc;
                        break;
                    case FINISH_POSITION:
                        Finishs[f_index++] = xc;
                        Finishs[f_index++] = yc;
                        break;
                }
                if((CellIndex == BonusIndex) && (BonusCounter > 0))
                {
                    Sprite p_emptySprite = getInactiveSprite();
                    if (p_emptySprite != null)
                    {
                        int type = 0;
                        int number = 0;

                        number = getRandomBonusNumber(getRandomInt(6)); 
                        if(number >= 0)
                        {
                            switch(number)
                            {
                                default:
                                case 0:
                                    type = OBJECT_SAUSAGE;
                                    break;
                                case 1:
                                    type = OBJECT_MUSHROOM;
                                    break;
                                case 2:
                                    type = OBJECT_APPLE_BIT;
                                    break;
                                case 3:
                                    type = OBJECT_JELLY;
                                    break;
                                case 4:
                                    type = OBJECT_BREAD_CRUST;
                                    break;
                                case 5:
                                    type = OBJECT_POISON;
                                    break;
                                case 6:
                                    type = OBJECT_FISH_SKELETON;
                                    break;
                            }
                        }
                        else
                        {
                            type = -1;
                        }

                        BonusCounter--;
                        if(type >= 0)
                        {
                            Bonuses[number]--;


                            putTile(xc,yc,type);
                        }


                        BonusIndex += (BonusStep - getRandomInt(BonusStep-1));

                        CheckIndex += BonusStep;
                        if( (CheckIndex - BonusIndex) > 16 )
                        {
                            BonusIndex = CheckIndex;
                        }
                    }
                }

                CellIndex++;
            }
        }

        i = getRandomInt(4) * 2;

        int x = (Starts[i++] * CELL_SIZE + CELL_SIZE / 2) << 8;
        int y = (Starts[i++] * CELL_SIZE + CELL_SIZE / 2) << 8;

        pPlayerSprite.setMainPointXY(x, y);

        putTile(x >> (8+5),y >> (8+5), 0);                

        i = getRandomInt(4) * 2;
        i_finish_bx = (Finishs[i++]);
        i_finish_by = (Finishs[i++]);

        putTile(i_finish_bx,i_finish_by,OBJECT_FINISH);   










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
                    addScore(LEVEL_EASY_INITIAL_SCORE);
                }
                break;
            case LEVEL_NORMAL:
                {
                    i_timedelay = LEVEL_NORMAL_TIMEDELAY;
                    i_playerAttemptions = LEVEL_NORMAL_ATTEMPTIONS;
                    i_GenerateDelay = LEVEL_NORMAL_GENERATION;
                    addScore(LEVEL_NORMAL_INITIAL_SCORE);
                }
                break;
            case LEVEL_HARD:
                {
                    i_timedelay = LEVEL_HARD_TIMEDELAY;
                    i_playerAttemptions = LEVEL_HARD_ATTEMPTIONS;
                    i_GenerateDelay = LEVEL_HARD_GENERATION;
                    addScore(LEVEL_HARD_INITIAL_SCORE);
                }
                break;
        }

        i_GameTimer = 0;
        i_LastTime = i_GameTimer;
        i_ticksPerSeconds = 1000/i_timedelay;

        i_Speed = TARAKAN_SPEED;
        b_Skin = false;

        i_DisableTimer = 0;
        i_SpeedTimer = 0;
        i_SkinTimer = 0;
        i_pressedKey = 0;

        activateSprite(pPlayerSprite, SPRITE_TARAKAN_LEFT);
        pPlayerSprite.setMainPointXY(I8_SCREEN_WIDTH / 2, I8_SCREEN_HEIGHT / 2);

resetLevel(getRandomInt(3));
generateLevel();

        return true;
    }

    public void resumeGameAfterPlayerLostOrPaused()
    {
        super.resumeGameAfterPlayerLostOrPaused();


        i_pressedKey = 0;
        i_Speed = TARAKAN_SPEED;

        activateSprite(pPlayerSprite, SPRITE_TARAKAN_LEFT);

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
        return "TARAKAN";
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
        return (MAX_SPRITES_NUMBER+1)*(Sprite.DATASIZE_BYTES+1)+49+4 + LEVEL_SIZE;
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

        _outputStream.writeInt(i_player_bx);
        _outputStream.writeInt(i_player_by);
        _outputStream.writeInt(i_finish_bx);
        _outputStream.writeInt(i_finish_by);

        _outputStream.writeInt(i_Speed);
        _outputStream.writeInt(i_DisableTimer);
        _outputStream.writeInt(i_SpeedTimer);
        _outputStream.writeInt(i_SkinTimer);
        _outputStream.writeInt(i_FlipMode);
        _outputStream.writeBoolean(b_Skin);

        saveLevel(_outputStream);
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

        i_player_bx = _inputStream.readInt();
        i_player_by = _inputStream.readInt();
        i_finish_bx = _inputStream.readInt();
        i_finish_by = _inputStream.readInt();

        i_Speed = _inputStream.readInt();
        i_DisableTimer = _inputStream.readInt();
        i_SpeedTimer = _inputStream.readInt();
        i_SkinTimer = _inputStream.readInt();
        i_FlipMode = _inputStream.readInt();
        b_Skin = _inputStream.readBoolean();

        loadLevel(_inputStream);
    }

    public int nextGameStep()
    {
        CAbstractPlayer p_player = m_pPlayerList[0];
        p_player.nextPlayerMove(this, p_MoveObject);

        if(p_MoveObject.i_buttonState!=0) i_pressedKey = p_MoveObject.i_buttonState;

        if(!processPlayer(i_pressedKey))
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

        if(i_player_bx == i_finish_bx && i_player_by == i_finish_by)
        {
            if (m_pWinningList==null)
            {
               m_pWinningList = m_pPlayerList;
            }
            setGameState(GAMEWORLDSTATE_GAMEOVER);
            m_pAbstractGameActionListener.processGameAction(GAMEACTION_TARAKAN_WIN);
        }

        i_GameTimer++;

        if( i_GameTimer % i_ticksPerSeconds == 0)  addScore(SCORE_FOR_TIME);

        return m_iGameState;
    }

    public int addScore(int score)
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

        return score;
    }

    public int getGameStepDelay()
    {
        return i_timedelay;
    }
}
