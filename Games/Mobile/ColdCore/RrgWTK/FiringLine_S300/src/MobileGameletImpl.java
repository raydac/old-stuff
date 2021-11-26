
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.DataInputStream;


public class MobileGameletImpl extends MobileGamelet
{


    public static final int MOVE_SPACE = (8 << 8);

    public static final int MOVE_SPEED = 0x520;

    public static final int FIRE_DELAY = 2;

    public static final int DEAD_TIMER = 25;
    public static final int SLIP_VALUE = DEAD_TIMER-20;
    public static final int SLIP_SPEED = (0x0080);

    private static int I8_SCREEN_WIDTH;

    private static int I8_SCREEN_HEIGHT;

    public MoveObject p_MoveObject;


    public static final int GAMEACTION_CREATURE_GENERATED = 0;
    public static final int GAMEACTION_CREATURE_ATTACKING = 1;
    public static final int GAMEACTION_CREATURE_DEAD = 2;
    public static final int GAMEACTION_PLAYER_SHOT = 3;
    public static final int GAMEACTION_PLAYER_DEAD = 4;
    public static final int GAMEACTION_RED_ALERT = 5;


    private int i_timedelay;

    public int i_playerAttemptions;

    public int i_GameTimer;

    public int i_LastTime;
    public int i_GenerateDelay;

    public int i_MonsterCount;

    public int i_MaxMonsters;

    public static final int LEVEL_EASY = 0;

    private static final int LEVEL_EASY_ATTEMPTIONS = 4;

    private static final int LEVEL_EASY_GENERATION = 17;
    private static final int LEVEL_EASY_MONSTERS = 6;

    private static final int LEVEL_EASY_TIMEDELAY = 100;

    public static final int LEVEL_NORMAL = 1;

    private static final int LEVEL_NORMAL_ATTEMPTIONS = 3;

    private static final int LEVEL_NORMAL_GENERATION = 15;
    private static final int LEVEL_NORMAL_MONSTERS = 8;

    private static final int LEVEL_NORMAL_TIMEDELAY = 90;

    public static final int LEVEL_HARD = 2;

    private static final int LEVEL_HARD_ATTEMPTIONS = 2;

    private static final int LEVEL_HARD_GENERATION = 15;
    private static final int LEVEL_HARD_MONSTERS = 10;

    private static final int LEVEL_HARD_TIMEDELAY = 80;


    public Sprite pPlayerSprite;

    public static final int MAX_SPRITES_NUMBER = 16;

    public Sprite[] ap_Sprites;

    private static final int SPRITEDATALENGTH = 10;

    public static final int SPRITE_BLASTER = 0;
    public static final int SPRITE_BLASTER_FIRE = 1;
    public static final int SPRITE_BLASTER_DEAD = 2;

    public static final int SPRITE_ALIEN_UP = 3;
    public static final int SPRITE_ALIEN_DOWN = 4;
    public static final int SPRITE_ALIEN_LEFT = 5;
    public static final int SPRITE_ALIEN_RIGHT = 6;

    public static final int SPRITE_ALIEN_REMAINS_UP = 7;
    public static final int SPRITE_ALIEN_REMAINS_DOWN = 8;
    public static final int SPRITE_ALIEN_REMAINS_LEFT = 9;
    public static final int SPRITE_ALIEN_REMAINS_RIGHT = 10;

    public static final int SPRITE_ALIEN_ATTACK_UP = 11;
    public static final int SPRITE_ALIEN_ATTACK_DOWN = 12;
    public static final int SPRITE_ALIEN_ATTACK_LEFT = 13;
    public static final int SPRITE_ALIEN_ATTACK_RIGHT = 14;

    private static final int [] ai_SpriteParameters = new int[]
    {
        0x0800, 0x0800, -0x001, 0x0000, 0x0400, 0x0400, 1, 1, Sprite.SPRITE_ALIGN_CENTER, Sprite.ANIMATION_FROZEN,
        0x0800, 0x0800, -0x001, 0x0000, 0x0800, 0x0800, 1, 3, Sprite.SPRITE_ALIGN_CENTER, Sprite.ANIMATION_FROZEN,
        0x0800, 0x0800, -0x001, 0x0000, 0x0800, 0x0200, 7, 1, Sprite.SPRITE_ALIGN_CENTER, Sprite.ANIMATION_FROZEN,
        0x0C00, 0x0C00, -0x001, 0x0000, 0x0C00, 0x0C00, 2, 1, Sprite.SPRITE_ALIGN_CENTER, Sprite.ANIMATION_CYCLIC,
        0x0C00, 0x0C00, -0x001, 0x0000, 0x0C00, 0x0C00, 2, 1, Sprite.SPRITE_ALIGN_CENTER, Sprite.ANIMATION_CYCLIC,
        0x0C00, 0x0C00, -0x001, 0x0000, 0x0C00, 0x0C00, 2, 1, Sprite.SPRITE_ALIGN_CENTER, Sprite.ANIMATION_CYCLIC,
        0x0C00, 0x0C00, -0x001, 0x0000, 0x0C00, 0x0C00, 2, 1, Sprite.SPRITE_ALIGN_CENTER, Sprite.ANIMATION_CYCLIC,
        0x0C00, 0x0C00, -0x001, 0x0000, 0x0C00, 0x0C00, 7, 2, Sprite.SPRITE_ALIGN_CENTER, Sprite.ANIMATION_FROZEN,
        0x0C00, 0x0C00, -0x001, 0x0000, 0x0C00, 0x0C00, 7, 2, Sprite.SPRITE_ALIGN_CENTER, Sprite.ANIMATION_FROZEN,
        0x0C00, 0x0C00, -0x001, 0x0000, 0x0C00, 0x0C00, 7, 2, Sprite.SPRITE_ALIGN_CENTER, Sprite.ANIMATION_FROZEN,
        0x0C00, 0x0C00, -0x001, 0x0000, 0x0C00, 0x0C00, 7, 2, Sprite.SPRITE_ALIGN_CENTER, Sprite.ANIMATION_FROZEN,
        0x0C00, 0x0C00, -0x001, 0x0000, 0x0C00, 0x0C00, 3, 1, Sprite.SPRITE_ALIGN_CENTER, Sprite.ANIMATION_FROZEN,
        0x0C00, 0x0C00, -0x001, 0x0000, 0x0C00, 0x0C00, 3, 1, Sprite.SPRITE_ALIGN_CENTER, Sprite.ANIMATION_FROZEN,
        0x0C00, 0x0C00, -0x001, 0x0000, 0x0C00, 0x0C00, 3, 1, Sprite.SPRITE_ALIGN_CENTER, Sprite.ANIMATION_FROZEN,
        0x0C00, 0x0C00, -0x001, 0x0000, 0x0C00, 0x0C00, 3, 1, Sprite.SPRITE_ALIGN_CENTER, Sprite.ANIMATION_FROZEN,
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
                    case SPRITE_ALIEN_ATTACK_UP:
                    case SPRITE_ALIEN_ATTACK_DOWN:
                    case SPRITE_ALIEN_ATTACK_LEFT:
                    case SPRITE_ALIEN_ATTACK_RIGHT:
                        if(pPlayerSprite.i_ObjectType != SPRITE_BLASTER_DEAD)
                        {
                            activateSprite(pPlayerSprite, SPRITE_BLASTER_DEAD);
                            m_pAbstractGameActionListener.processGameAction(GAMEACTION_PLAYER_DEAD);
                        }
                        break;
                }
            }
            switch (p_Sprite.i_ObjectType)
            {
                case SPRITE_ALIEN_UP:
                    if(pPlayerSprite.i_ObjectType == SPRITE_BLASTER_FIRE)
                    {
                        if(p_Sprite.isCollided(pPlayerSprite))
                        {
                            activateSprite(p_Sprite, SPRITE_ALIEN_REMAINS_UP);
                            deactivatePathForSpriteID(p_Sprite.i_spriteID);
                            p_Sprite.i_ObjectState = DEAD_TIMER;
                            addScore((p_Sprite.i_mainZ >> 8) / 5);
                            i_MonsterCount--;
                        }
                    }
                    p_Sprite.i_mainZ = p_Sprite.i_mainY;
                    break;
                case SPRITE_ALIEN_DOWN:
                    if(pPlayerSprite.i_ObjectType == SPRITE_BLASTER_FIRE)
                    {
                        if(p_Sprite.isCollided(pPlayerSprite))
                        {
                            activateSprite(p_Sprite, SPRITE_ALIEN_REMAINS_DOWN);
                            deactivatePathForSpriteID(p_Sprite.i_spriteID);
                            p_Sprite.i_ObjectState = DEAD_TIMER;
                            addScore((p_Sprite.i_mainZ >> 8) / 5);
                            i_MonsterCount--;
                        }
                    }
                    p_Sprite.i_mainZ = I8_SCREEN_HEIGHT - p_Sprite.i_mainY;
                    break;
                case SPRITE_ALIEN_LEFT:
                    if(pPlayerSprite.i_ObjectType == SPRITE_BLASTER_FIRE)
                    {
                        if(p_Sprite.isCollided(pPlayerSprite))
                        {
                            activateSprite(p_Sprite, SPRITE_ALIEN_REMAINS_LEFT);
                            deactivatePathForSpriteID(p_Sprite.i_spriteID);
                            p_Sprite.i_ObjectState = DEAD_TIMER;
                            addScore((p_Sprite.i_mainZ >> 8) / 5);
                            i_MonsterCount--;
                        }
                    }
                    p_Sprite.i_mainZ = p_Sprite.i_mainX;
                    break;
                case SPRITE_ALIEN_RIGHT:
                    if(pPlayerSprite.i_ObjectType == SPRITE_BLASTER_FIRE)
                    {
                        if(p_Sprite.isCollided(pPlayerSprite))
                        {
                            activateSprite(p_Sprite, SPRITE_ALIEN_REMAINS_RIGHT);
                            deactivatePathForSpriteID(p_Sprite.i_spriteID);
                            p_Sprite.i_ObjectState = DEAD_TIMER;
                            addScore((p_Sprite.i_mainZ >> 8) / 5);
                            i_MonsterCount--;
                        }
                    }
                    p_Sprite.i_mainZ = I8_SCREEN_WIDTH - p_Sprite.i_mainX;
                    break;
                case SPRITE_ALIEN_REMAINS_UP:
                case SPRITE_ALIEN_REMAINS_DOWN:
                    if(p_Sprite.i_ObjectState == DEAD_TIMER)
                    {
                         m_pAbstractGameActionListener.processGameAction(GAMEACTION_CREATURE_DEAD);
                    }
                    if(p_Sprite.i_ObjectState > 0)
                    {
                        p_Sprite.i_ObjectState--;
                    }
                    else
                    {
                        deactivateSprite(p_Sprite);
                    }
                    break;
                case SPRITE_ALIEN_REMAINS_LEFT:
                case SPRITE_ALIEN_REMAINS_RIGHT:
                    if(p_Sprite.i_Frame == 0 && p_Sprite.i_Delay == 1)
                    {
                         m_pAbstractGameActionListener.processGameAction(GAMEACTION_CREATURE_DEAD);
                    }
                    if(p_Sprite.i_ObjectState > 0)
                    {
                        p_Sprite.i_ObjectState--;
                        if(p_Sprite.i_ObjectState > SLIP_VALUE)
                        {
                            p_Sprite.setMainPointXY(p_Sprite.i_mainX, p_Sprite.i_mainY + SLIP_SPEED);
                        }
                    }
                    else
                    {
                        deactivateSprite(p_Sprite);
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
                case SPRITE_BLASTER_FIRE:
                    activateSprite(pPlayerSprite, SPRITE_BLASTER);
                    break;
                case SPRITE_BLASTER_DEAD:
                    return false;
            }
        }
        switch(pPlayerSprite.i_ObjectType)
        {
            case SPRITE_BLASTER:
                if ((_buttonState & MoveObject.BUTTON_LEFT) != 0)
                {
                    i_mx -= MOVE_SPEED;
                }
                else if ((_buttonState & MoveObject.BUTTON_RIGHT) != 0)
                {
                    i_mx += MOVE_SPEED;
                }
                if ((_buttonState & MoveObject.BUTTON_UP) != 0)
                {
                    i_my -= MOVE_SPEED;
                }
                else if ((_buttonState & MoveObject.BUTTON_DOWN) != 0)
                {
                    i_my += MOVE_SPEED;
                }

                if(pPlayerSprite.i_ObjectState > 0)
                {
                    pPlayerSprite.i_ObjectState--;
                }
                else
                {
                    if ((_buttonState & MoveObject.BUTTON_FIRE) != 0)
                    {
                        activateSprite(pPlayerSprite, SPRITE_BLASTER_FIRE);
                        pPlayerSprite.i_ObjectState = FIRE_DELAY;
                        m_pAbstractGameActionListener.processGameAction(GAMEACTION_PLAYER_SHOT);
                    }
                }


                if( (i_mx > (0 + MOVE_SPACE) && i_mx < (I8_SCREEN_WIDTH - MOVE_SPACE)) && (i_my > (0 + MOVE_SPACE) && i_my < (I8_SCREEN_HEIGHT - MOVE_SPACE)) )
                {
                    pPlayerSprite.setMainPointXY(i_mx, i_my);
                }
                break;
        }
        return true;
    }

    private static final int MAX_PATHS_NUMBER = 16;

    private PathController[] ap_Paths;



     private static final short [] ash_Paths= new short[] {
          7,0,70,50,10,62,47,10,65,44,10,52,38,10,60,29,10,72,28,10,82,19,10,93,8,10,
          9,0,63,51,10,54,48,10,69,44,10,53,39,10,67,37,10,80,33,10,48,30,10,74,23,10,41,19,10,26,8,10,
          7,0,68,51,10,78,45,10,59,44,10,74,36,10,48,33,10,40,23,10,55,17,10,77,8,10,
          8,0,61,51,10,74,43,10,53,41,10,64,35,10,82,30,10,44,28,10,69,23,10,86,19,10,57,9,10,
          8,0,55,78,10,65,81,10,73,88,10,56,90,10,75,95,10,47,95,10,41,104,10,51,108,10,65,120,10,
          8,0,69,78,10,78,82,10,69,86,10,56,90,10,79,92,10,51,98,10,82,104,10,60,109,10,36,121,10,
          8,0,53,78,10,63,81,10,57,86,10,74,87,10,66,94,10,47,95,10,76,101,10,46,109,10,83,119,10,
          8,0,67,78,10,77,82,10,60,85,10,75,90,10,85,99,10,57,99,10,45,109,10,71,110,10,93,121,10,
          7,0,51,58,10,46,49,10,45,69,10,37,52,10,34,71,10,25,78,10,21,57,10,8,48,10,
          7,0,51,73,10,47,62,10,44,75,10,36,48,10,35,73,10,25,52,10,24,80,10,8,92,10,
          7,0,51,76,10,45,63,10,41,76,10,38,65,10,34,48,10,25,44,10,24,84,10,9,75,10,
          7,0,51,53,10,45,61,10,41,64,10,34,49,10,34,70,10,25,82,10,20,59,10,8,40,10,
          8,0,78,54,10,83,65,10,85,74,10,90,58,10,94,74,10,100,54,10,104,81,10,109,63,10,118,36,10,
          7,0,78,75,10,83,64,10,88,57,10,95,79,10,95,50,10,104,46,10,108,74,10,120,91,10,
          6,0,78,65,10,86,75,10,89,52,10,95,79,10,101,56,10,104,85,10,120,58,10,
          9,0,78,58,10,83,68,10,85,55,10,88,73,10,94,49,10,98,77,10,101,56,10,109,82,10,109,46,10,121,46,10
     };

     private static final int PATH_1  = 0;
     private static final int PATH_2  = 26;
     private static final int PATH_3  = 58;
     private static final int PATH_4  = 84;
     private static final int PATH_5  = 113;
     private static final int PATH_6  = 142;
     private static final int PATH_7  = 171;
     private static final int PATH_8  = 200;
     private static final int PATH_9  = 229;
     private static final int PATH_10 = 255;
     private static final int PATH_11 = 281;
     private static final int PATH_12 = 307;
     private static final int PATH_13 = 333;
     private static final int PATH_14 = 362;
     private static final int PATH_15 = 388;
     private static final int PATH_16 = 411;



    private PathController getInactivePath()
    {
        for (int li = 0; li < MAX_PATHS_NUMBER; li++)
        {
            if (ap_Paths[li].isCompleted()) return ap_Paths[li];
        }
        return null;
    }

    private void deactivatePathForSpriteID(int _spriteID)
    {
         for (int li = 0; li < MAX_PATHS_NUMBER; li++)
        {
            if (ap_Paths[li].isCompleted()) continue;
            if (ap_Paths[li].p_sprite.i_spriteID == _spriteID)
            {
                ap_Paths[li].deactivate();
                break;
            }
        }
    }

    private boolean processPaths()
    {
        for (int li = 0; li < MAX_PATHS_NUMBER; li++)
        {
            PathController p_curPathController = ap_Paths[li];
            if (p_curPathController.isCompleted()) continue;
            if (p_curPathController.processStep())
            {
                Sprite p_spr = p_curPathController.p_sprite;
            }
            if (p_curPathController.isCompleted())
            {
                switch(ap_Paths[li].p_sprite.i_ObjectType)
                {
                    case SPRITE_ALIEN_UP:
                        activateSprite(ap_Paths[li].p_sprite, SPRITE_ALIEN_ATTACK_UP);
                        break;
                    case SPRITE_ALIEN_DOWN:
                        activateSprite(ap_Paths[li].p_sprite, SPRITE_ALIEN_ATTACK_DOWN);
                        break;
                    case SPRITE_ALIEN_LEFT:
                        activateSprite(ap_Paths[li].p_sprite, SPRITE_ALIEN_ATTACK_LEFT);
                        break;
                    case SPRITE_ALIEN_RIGHT:
                        activateSprite(ap_Paths[li].p_sprite, SPRITE_ALIEN_ATTACK_RIGHT);
                        break;
                }
                m_pAbstractGameActionListener.processGameAction(GAMEACTION_CREATURE_ATTACKING);


            }
        }
        return true;
    }


    private void generateSprite()
    {
        if((i_LastTime + i_GenerateDelay) < i_GameTimer )
        {
            if(i_MonsterCount < i_MaxMonsters)
            {
                i_LastTime = i_GameTimer;
                Sprite p_emptySprite = getInactiveSprite();
                PathController p_inactivePath = getInactivePath();

                if (p_emptySprite != null && p_inactivePath != null)
                {
                    int y = I8_SCREEN_HEIGHT / 2;
                    int x = I8_SCREEN_WIDTH / 2;
                    int type = 0;
                    int path = PATH_1;

                    path = getRandomInt(15);
                    type = path / 4;

                    switch(path)
                    {
                        case 0:
                            type = SPRITE_ALIEN_UP;
                            path = PATH_1;
                            break;
                        case 1:
                            type = SPRITE_ALIEN_UP;
                            path = PATH_2;
                            break;
                        case 2:
                            type = SPRITE_ALIEN_UP;
                            path = PATH_3;
                            break;
                        case 3:
                            type = SPRITE_ALIEN_UP;
                            path = PATH_4;
                            break;
                        case 4:
                            type = SPRITE_ALIEN_DOWN;
                            path = PATH_5;
                            break;
                        case 5:
                            type = SPRITE_ALIEN_DOWN;
                            path = PATH_6;
                            break;
                        case 6:
                            type = SPRITE_ALIEN_DOWN;
                            path = PATH_7;
                            break;
                        case 7:
                            type = SPRITE_ALIEN_DOWN;
                            path = PATH_8;
                            break;
                        default:
                        case 8:
                            type = SPRITE_ALIEN_LEFT;
                            path = PATH_9;
                            break;
                        case 9:
                            type = SPRITE_ALIEN_LEFT;
                            path = PATH_10;
                            break;
                        case 10:
                            type = SPRITE_ALIEN_LEFT;
                            path = PATH_11;
                            break;
                        case 11:
                            type = SPRITE_ALIEN_LEFT;
                            path = PATH_12;
                            break;
                        case 12:
                            type = SPRITE_ALIEN_RIGHT;
                            path = PATH_13;
                            break;
                        case 13:
                            type = SPRITE_ALIEN_RIGHT;
                            path = PATH_14;
                            break;
                        case 14:
                            type = SPRITE_ALIEN_RIGHT;
                            path = PATH_15;
                            break;
                        case 15:
                            type = SPRITE_ALIEN_RIGHT;
                            path = PATH_16;
                            break;
                    }

                    i_MonsterCount++;
                    activateSprite(p_emptySprite, type);
                    p_emptySprite.lg_SpriteActive = true;
                    p_inactivePath.initPath(0, 0, p_emptySprite, ash_Paths, path, 0, 0, PathController.MODIFY_NONE);
                    p_emptySprite.i_mainZ = 64<<8; 

                    m_pAbstractGameActionListener.processGameAction(GAMEACTION_CREATURE_GENERATED);
                }
            }
        }
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
                    i_MaxMonsters = LEVEL_EASY_MONSTERS;
                }
                break;
            case LEVEL_NORMAL:
                {
                    i_timedelay = LEVEL_NORMAL_TIMEDELAY;
                    i_playerAttemptions = LEVEL_NORMAL_ATTEMPTIONS;
                    i_GenerateDelay = LEVEL_NORMAL_GENERATION;
                    i_MaxMonsters = LEVEL_NORMAL_MONSTERS;
                }
                break;
            case LEVEL_HARD:
                {
                    i_timedelay = LEVEL_HARD_TIMEDELAY;
                    i_playerAttemptions = LEVEL_HARD_ATTEMPTIONS;
                    i_GenerateDelay = LEVEL_HARD_GENERATION;
                    i_MaxMonsters = LEVEL_HARD_MONSTERS;
                }
                break;
        }

        i_GameTimer = 0;
        i_LastTime = i_GameTimer;

        i_MonsterCount = 0;

        activateSprite(pPlayerSprite, SPRITE_BLASTER);
        pPlayerSprite.setMainPointXY(I8_SCREEN_WIDTH / 2, I8_SCREEN_HEIGHT / 2);

        return true;
    }

    public void resumeGameAfterPlayerLostOrPaused()
    {
        super.resumeGameAfterPlayerLostOrPaused();

        deinitState();
        initState();

        i_GameTimer = 0;
        i_LastTime = i_GameTimer;

        i_MonsterCount = 0;

        activateSprite(pPlayerSprite, SPRITE_BLASTER);
        pPlayerSprite.setMainPointXY(I8_SCREEN_WIDTH / 2, I8_SCREEN_HEIGHT / 2);

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
        return "TUNNEL";
    }

    public boolean initState()
    {
        ap_Sprites = new Sprite[MAX_SPRITES_NUMBER];
        for (int li = 0; li < MAX_SPRITES_NUMBER; li++) ap_Sprites[li] = new Sprite(li);

        ap_Paths = new PathController[MAX_PATHS_NUMBER];
        for (int li = 0; li < MAX_PATHS_NUMBER; li++) ap_Paths[li] = new PathController();

        pPlayerSprite = new Sprite(-1);

        p_MoveObject = new MoveObject();

        return true;
    }

    public void deinitState()
    {
        ap_Paths = null;

        ap_Sprites = null;
        pPlayerSprite = null;

        Runtime.getRuntime().gc();
    }

    public int _getMaximumDataSize()
    {
        return (MAX_SPRITES_NUMBER+1)*(Sprite.DATASIZE_BYTES+1)+MAX_PATHS_NUMBER*PathController.DATASIZE_BYTES+24;
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

        for(int li=0;li<MAX_PATHS_NUMBER;li++)
        {
            ap_Paths[li].writePathToStream(_outputStream);
        }

        _outputStream.writeInt(i_playerAttemptions);
        _outputStream.writeInt(i_GameTimer);
        _outputStream.writeInt(i_LastTime);
        _outputStream.writeInt(i_GenerateDelay);
        _outputStream.writeInt(i_MonsterCount);
        _outputStream.writeInt(i_MaxMonsters);

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


        for(int li=0;li<MAX_PATHS_NUMBER;li++)
        {
            ap_Paths[li].readPathFromStream(_inputStream,ap_Sprites);
            ap_Paths[li].as_pathArray = ash_Paths;

        }

        i_playerAttemptions = _inputStream.readInt();
        i_GameTimer = _inputStream.readInt();
        i_LastTime = _inputStream.readInt();
        i_GenerateDelay = _inputStream.readInt();

        i_MonsterCount = _inputStream.readInt();
        i_MaxMonsters = _inputStream.readInt();
    }

    public int nextGameStep()
    {
        CAbstractPlayer p_player = m_pPlayerList[0];
        p_player.nextPlayerMove(this, p_MoveObject);

        processSprites();
        processPaths();

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


        if(i_GameTimer == 0)
        {
            m_pAbstractGameActionListener.processGameAction(GAMEACTION_RED_ALERT);
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
