
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.DataInputStream;

public class MobileGameletImpl extends MobileGamelet
{
    private static final int FISHROD_X0 = 35 << 8; 
    private static final int FISHROD_Y0 = 44 << 8; 
    private static final int FISHROD_ANGLE = 5;
    private static final int FISHROD_MAX_ANGLE = 10; 
    private static final int FISHROD_MIN_ANGLE = 1; 
    private static final int FISHROD_LENGTH = 64 << 8;

    private static final int FISHROD_SIZE = 80 << 8;
    private static final int FISHROD_SIZE_MIN = 80 << 8; 
    private static final int FISHROD_SIZE_MAX = (146 + 6) << 8; 

    private static final int FISH_LINE_SPEED = 0x0140;
    private static final int CATCH_SPEED = 4 << 8;
    private static final int HOOK_FALL_SPEED = 4 << 8;

    private static final int LEVEL_EASY_TIMEDELAY = 120;

    private static final int LEVEL_EASY_LIVES = 4;

    private static final int LEVEL_EASY_ENEMYFREQ = 20;

    private static final int LEVEL_NORMAL_TIMEDELAY = 100;

    private static final int LEVEL_NORMAL_LIVES = 3;

    private static final int LEVEL_NORMAL_ENEMYFREQ = 30;

    private static final int LEVEL_HARD_TIMEDELAY = 80;

    private static final int LEVEL_HARD_LIVES = 2;

    private static final int LEVEL_HARD_ENEMYFREQ = 40;

    private static final int SCORES_FISH1 = 10;
    private static final int SCORES_FISH2 = 20;
    private static final int SCORES_FISH3 = 30;
    private static final int SCORES_FISH4 = 40;
    private static final int SCORES_FISH5 = 50;
    private static final int SCORES_FISH6 = 60;

    private static final int SPRITEDATALENGTH = 10;

    public static final int SPRITE_HOOK                         = 0;
    public static final int SPRITE_HOOK_FALL                    = 1;
    public static final int SPRITE_HOOK_CATCH                   = 2;
    public static final int SPRITE_HOOK_CATCH_1                 = 3;
    public static final int SPRITE_HOOK_CATCH_2                 = 4;
    public static final int SPRITE_HOOK_CATCH_3                 = 5;
    public static final int SPRITE_HOOK_CATCH_4                 = 6;
    public static final int SPRITE_HOOK_CATCH_5                 = 7;
    public static final int SPRITE_HOOK_CATCH_6                 = 8;

    public static final int SPRITE_FISH_1                       = 9;
    public static final int SPRITE_FISH_2                       = 10;
    public static final int SPRITE_FISH_3                       = 11;
    public static final int SPRITE_FISH_4                       = 12;
    public static final int SPRITE_FISH_5                       = 13;
    public static final int SPRITE_FISH_6                       = 14;
    public static final int SPRITE_ENEMY_FISH_1                 = 15;
    public static final int SPRITE_ENEMY_FISH_2                 = 16;
    public static final int SPRITE_ENEMY_FISH_1_ATTACK          = 17;
    public static final int SPRITE_ENEMY_FISH_2_ATTACK          = 18;

    public static final int MAX_SPRITES = 32;

    private static final int MAX_PATHS = 8;

    private static final int PATH_FISH_1        = 13*0;
    private static final int PATH_FISH_2        = 13*1;
    private static final int PATH_FISH_3        = 13*2;
    private static final int PATH_FISH_4        = 13*3;
    private static final int PATH_FISH_5        = 13*4;
    private static final int PATH_FISH_6        = 13*5;
    private static final int PATH_FISH_7        = 13*6;
    private static final int PATH_FISH_8        = 13*7;
    private static final int PATH_FISH_9        = 13*8;
    private static final int PATH_FISH_10       = 13*9;
    private static final int PATH_FISH_11       = 13*10;
    private static final int PATH_FISH_12       = 13*11;
    private static final int PATH_ENEMY_FISH_1  = 13*12;
    private static final int PATH_ENEMY_FISH_2  = 13*13;


    public static final int GAMEACTION_TRY_CATCH_FISH       = 0;
    public static final int GAMEACTION_CATCH_FISH           = 1;

    public static final int GAMEACTION_CAUGHT_FISH          = 2;

    public static final int GAMEACTION_ENEMY_FISH           = 3;

    public static final int GAMEACTION_ENEMY_FISH_ATTACK    = 4;

    public static final int GAMEACTION_HOOK_ON_THE_FLOOR    = 5;




    public int m_iPlayerAttemptions;

    private int i_timedelay;

    public int i_GameTimer;

    private int i_borndelay;
    private int i_lasttime;

    private int i_rndfreq;

    public boolean m_bEnemyFish;

    public int m_nFishCounter;

    public int m_FishrodSize;

    public int m_FishrodAngle;

    public int m_FishrodX0;
    public int m_FishrodY0;

    public int m_FishrodX1;
    public int m_FishrodY1;


    public Sprite pPlayerHook;

    public Sprite[] ap_Sprites;

    private PathController[] ap_Paths;

    public MoveObject p_MoveObject;

    private int i8_gameScreenWidth;

    private int i8_gameScreenHeight;


    private static final short[] ash_Paths = new short[]
    {
        3, 0,  -20,80,20,    32,85,30,    96,90,20,    142,80,
        3, 0,  -20,80,40,    32,90,20,    96,95,25,    142,80,
        3, 0,  138,80,40,    96,90,25,    32,80,25,    -20,80,
        3, 0,  138,80,40,    96,80,30,    32,90,25,    -20,80,
        3, 0,  -20,100,40,   32,90,30,    96,110,25,   142,100,
        3, 0,  -20,100,40,   32,110,20,   96,90,25,    142,100,
        3, 0,  138,100,40,   96,110,30,   32,90,25,    -20,100,
        3, 0,  138,100,40,   96,90,30,    32,110,20,   -20,100,
        3, 0,  -20,120,40,   32,110,30,   96,130,25,   142,120,
        3, 0,  -20,120,40,   32,120,30,   96,110,25,   142,120,
        3, 0,  138,120,40,   96,120,30,   32,110,25,   -20,120,
        3, 0,  138,120,40,   96,110,30,   32,120,25,   -20,110,
        3, 0,  -35,82,20,    32,82,20,    96,82,20,    152,82,
        3, 0,  148,82,20,    96,82,20,    32,82,20,    -35,82
    };

    private static final int[] ai_SpriteParameters = new int[]
    {
        0x0900, 0x0E00, 0x0600, 0x0600, 0, 0x0600, 2, 1, Sprite.SPRITE_ALIGN_CENTER, Sprite.ANIMATION_CYCLIC,
        0x0900, 0x0E00, 0x0600, 0x0600, 0, 0x0600, 2, 1, Sprite.SPRITE_ALIGN_CENTER, Sprite.ANIMATION_CYCLIC,
        0x0900, 0x0E00, 0x0600, 0x0600, 0, 0x0600, 2, 1, Sprite.SPRITE_ALIGN_CENTER, Sprite.ANIMATION_CYCLIC,
        0x0800, 0x1000, 0x0800, 0x0600, 0, 0x0600, 4, 2, Sprite.SPRITE_ALIGN_CENTER, Sprite.ANIMATION_FROZEN,
        0x0800, 0x1000, 0x0800, 0x0600, 0, 0x0600, 4, 2, Sprite.SPRITE_ALIGN_CENTER, Sprite.ANIMATION_FROZEN,
        0x0800, 0x1000, 0x0800, 0x0600, 0, 0x0600, 4, 2, Sprite.SPRITE_ALIGN_CENTER, Sprite.ANIMATION_FROZEN,
        0x0800, 0x1000, 0x0800, 0x0600, 0, 0x0600, 4, 2, Sprite.SPRITE_ALIGN_CENTER, Sprite.ANIMATION_FROZEN,
        0x1800, 0x1E00, 0x0800, 0x0600, 0, 0x0600, 4, 2, Sprite.SPRITE_ALIGN_CENTER, Sprite.ANIMATION_FROZEN,
        0x0800, 0x1000, 0x0800, 0x0600, 0, 0x0600, 4, 2, Sprite.SPRITE_ALIGN_CENTER, Sprite.ANIMATION_FROZEN,

        0x1800, 0x0F00, 0x0E00, 0x0300, 0x0800, -1, 2, 2, Sprite.SPRITE_ALIGN_CENTER, Sprite.ANIMATION_CYCLIC,
        0x1900, 0x0E00, 0x0E00, 0x0300, 0x0200, -1, 2, 2, Sprite.SPRITE_ALIGN_CENTER, Sprite.ANIMATION_CYCLIC,
        0x1D00, 0x1000, 0x1000, 0x0300, 0x0B00, -1, 2, 2, Sprite.SPRITE_ALIGN_CENTER, Sprite.ANIMATION_CYCLIC,
        0x1D00, 0x0F00, 0x1000, 0x0300, 0x0200, -1, 2, 2, Sprite.SPRITE_ALIGN_CENTER, Sprite.ANIMATION_CYCLIC,
        0x2300, 0x1100, 0x1100, 0x0300, 0x1000, -1, 2, 2, Sprite.SPRITE_ALIGN_CENTER, Sprite.ANIMATION_CYCLIC,
        0x2300, 0x1100, 0x1100, 0x0300, 0x0100, -1, 2, 2, Sprite.SPRITE_ALIGN_CENTER, Sprite.ANIMATION_CYCLIC,

        0x3700, 0x1C00, 0x1000, 0x1000, 0x2200, -1, 2, 2, Sprite.SPRITE_ALIGN_CENTER, Sprite.ANIMATION_CYCLIC,
        0x3400, 0x1C00, 0x1200, 0x1400, 0x0300, -1, 2, 2, Sprite.SPRITE_ALIGN_CENTER, Sprite.ANIMATION_CYCLIC,
        0x3700, 0x1C00, 0x1000, 0x1000, 0x2200, -1, 2, 1, Sprite.SPRITE_ALIGN_CENTER, Sprite.ANIMATION_FROZEN,
        0x3400, 0x1C00, 0x1200, 0x1400, 0x0300, -1, 2, 1, Sprite.SPRITE_ALIGN_CENTER, Sprite.ANIMATION_FROZEN
    };

    private void initSpriteFromArray(Sprite _sprite, int _actorIndex)
    {
        _sprite.i_ObjectType = _actorIndex;
        _actorIndex *= SPRITEDATALENGTH;
        int i_w = ai_SpriteParameters[_actorIndex++];
        int i_h = ai_SpriteParameters[_actorIndex++];
        int i_aw = ai_SpriteParameters[_actorIndex++];
        int i_ah = ai_SpriteParameters[_actorIndex++];
        int i_dx = ai_SpriteParameters[_actorIndex++];
        int i_dy = ai_SpriteParameters[_actorIndex++];
        int i_f = ai_SpriteParameters[_actorIndex++];
        int i_fd = ai_SpriteParameters[_actorIndex++];
        int i_mp = ai_SpriteParameters[_actorIndex++];
        int i_an = ai_SpriteParameters[_actorIndex++];

        _sprite.setAnimation(i_an, i_mp, i_w, i_h, i_f, 0, i_fd);
        _sprite.setCollisionBounds(i_dx < 0 ? (i_w - i_aw) >> 1 : i_dx, i_dy < 0 ? (i_h - i_ah) >> 1 : i_dy, i_aw, i_ah);
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
                        case SPRITE_ENEMY_FISH_1_ATTACK:
                            {
                               initSpriteFromArray(ap_Sprites[li], SPRITE_ENEMY_FISH_1);
                               if(pPlayerHook.i_ObjectType == SPRITE_HOOK ||
                                  pPlayerHook.i_ObjectType == SPRITE_HOOK_CATCH)
                                  {
                                    initSpriteFromArray(pPlayerHook, SPRITE_HOOK_FALL);
                                  }
                               m_pAbstractGameActionListener.processGameAction(GAMEACTION_ENEMY_FISH_ATTACK);
                            }
                            break;
                        case SPRITE_ENEMY_FISH_2_ATTACK:
                            {
                               initSpriteFromArray(ap_Sprites[li], SPRITE_ENEMY_FISH_2);
                               if(pPlayerHook.i_ObjectType == SPRITE_HOOK ||
                                  pPlayerHook.i_ObjectType == SPRITE_HOOK_CATCH)
                                  {
                                    initSpriteFromArray(pPlayerHook, SPRITE_HOOK_FALL);
                                  }
                               m_pAbstractGameActionListener.processGameAction(GAMEACTION_ENEMY_FISH_ATTACK);
                            }
                            break;
                    } 
                } 

                Sprite pEnemyFish = ap_Sprites[li];

                if(pEnemyFish.i_ObjectType == SPRITE_ENEMY_FISH_1 ||
                   pEnemyFish.i_ObjectType == SPRITE_ENEMY_FISH_2)
                {
                    if(pPlayerHook.i_ObjectType == SPRITE_HOOK ||
                       pPlayerHook.i_ObjectType == SPRITE_HOOK_CATCH)
                    {

                        int fish_x = pEnemyFish.i_ScreenX+pEnemyFish.i_col_offsetX;
                        int fish_y = pEnemyFish.i_ScreenY+pEnemyFish.i_col_offsetY;
                        int fish_x2 = fish_x + pEnemyFish.i_col_width;

                        int hook_x = pPlayerHook.i_ScreenX+pPlayerHook.i_col_offsetX;
                        int hook_y = pPlayerHook.i_ScreenY+pPlayerHook.i_col_offsetY;
                        int hook_x2 = hook_x + pPlayerHook.i_col_width;


                        if(fish_x < hook_x2 && fish_x2 > hook_x)
                        {
                            if(fish_y < hook_y)
                            {
                                m_pAbstractGameActionListener.processGameAction(GAMEACTION_ENEMY_FISH_ATTACK);
                                if(pEnemyFish.i_ObjectType == SPRITE_ENEMY_FISH_1)
                                {
                                    initSpriteFromArray(pEnemyFish, SPRITE_ENEMY_FISH_1_ATTACK);
                                }
                                else
                                {
                                    initSpriteFromArray(pEnemyFish, SPRITE_ENEMY_FISH_2_ATTACK);
                                }
                            }
                        }
                    }
                }

                if(ap_Sprites[li].isCollided(pPlayerHook) )
                {
                    if(pPlayerHook.i_ObjectType == SPRITE_HOOK_CATCH)
                    {
                        CAbstractPlayer p_player = m_pPlayerList[0];
                        switch(ap_Sprites[li].i_ObjectType)
                        {
                            case SPRITE_FISH_1:
                                initSpriteFromArray(pPlayerHook, SPRITE_HOOK_CATCH_1);
                                deactivatePathForSpriteID(ap_Sprites[li].i_spriteID);
                                ap_Sprites[li].lg_SpriteActive = false;
                                break;
                            case SPRITE_FISH_2:
                                initSpriteFromArray(pPlayerHook, SPRITE_HOOK_CATCH_2);
                                deactivatePathForSpriteID(ap_Sprites[li].i_spriteID);
                                ap_Sprites[li].lg_SpriteActive = false;
                                break;
                            case SPRITE_FISH_3:
                                initSpriteFromArray(pPlayerHook, SPRITE_HOOK_CATCH_3);
                                deactivatePathForSpriteID(ap_Sprites[li].i_spriteID);
                                ap_Sprites[li].lg_SpriteActive = false;
                                break;
                            case SPRITE_FISH_4:
                                initSpriteFromArray(pPlayerHook, SPRITE_HOOK_CATCH_4);
                                deactivatePathForSpriteID(ap_Sprites[li].i_spriteID);
                                ap_Sprites[li].lg_SpriteActive = false;
                                break;
                            case SPRITE_FISH_5:
                                initSpriteFromArray(pPlayerHook, SPRITE_HOOK_CATCH_5);
                                deactivatePathForSpriteID(ap_Sprites[li].i_spriteID);
                                ap_Sprites[li].lg_SpriteActive = false;
                                break;
                            case SPRITE_FISH_6:
                                initSpriteFromArray(pPlayerHook, SPRITE_HOOK_CATCH_6);
                                deactivatePathForSpriteID(ap_Sprites[li].i_spriteID);
                                ap_Sprites[li].lg_SpriteActive = false;
                                break;
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

            boolean lg_moved = true;

            switch (i_spr)
            {
                case -1:
                    lg_moved = false;
            }

            if (lg_moved)
            {
                ap_Paths[li].processStep();
                if (ap_Paths[li].isCompleted())
                {
                    if(ap_Paths[li].p_sprite.i_ObjectType == SPRITE_ENEMY_FISH_1 ||
                       ap_Paths[li].p_sprite.i_ObjectType == SPRITE_ENEMY_FISH_2)
                    {
                        m_bEnemyFish = false;
                    }

                    ap_Paths[li].p_sprite.lg_SpriteActive = false;
                }
            }
        }
    }

    public String getGameTextID()
    {
        return "FISHER";
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

        pPlayerHook = new Sprite(0);

        initSpriteFromArray(pPlayerHook, SPRITE_HOOK);
        pPlayerHook.lg_SpriteActive = true;

        int i8_cx = i8_gameScreenWidth >> 1;
        int i8_cy = i8_gameScreenHeight >> 1;

        pPlayerHook.setMainPointXY(i8_cx, i8_cy);

        p_MoveObject = new MoveObject();

        return true;
    }

    private boolean processFisher(int _buttonState)
    {
        if (pPlayerHook.processAnimation())
        {
            switch(pPlayerHook.i_ObjectType)
            {
                case SPRITE_HOOK_CATCH:
                    break;
            }
        }

        if (pPlayerHook.i_ObjectType == SPRITE_HOOK)
        {
            if((_buttonState & MoveObject.BUTTON_LEFT) != 0)
            {
                if(m_FishrodAngle < FISHROD_MAX_ANGLE) m_FishrodAngle++;
            }
            if((_buttonState & MoveObject.BUTTON_RIGHT) != 0)
            {
                if(m_FishrodAngle > FISHROD_MIN_ANGLE) m_FishrodAngle--;
            }
            if((_buttonState & MoveObject.BUTTON_DOWN) != 0)
            {
                if(m_FishrodSize < FISHROD_SIZE_MAX) m_FishrodSize += FISH_LINE_SPEED;
            }
            if((_buttonState & MoveObject.BUTTON_UP) != 0)
            {
                if(m_FishrodSize > FISHROD_SIZE_MIN) m_FishrodSize -= FISH_LINE_SPEED;
            }
            if((_buttonState & MoveObject.BUTTON_FIRE) != 0)
            {
                m_pAbstractGameActionListener.processGameAction(GAMEACTION_TRY_CATCH_FISH);
                initSpriteFromArray(pPlayerHook, SPRITE_HOOK_CATCH);
            }
        }
        else
        {
            if(pPlayerHook.i_ObjectType != SPRITE_HOOK_FALL)
            {
                if((m_FishrodSize - CATCH_SPEED) > FISHROD_SIZE_MIN) m_FishrodSize -= CATCH_SPEED; else m_FishrodSize = FISHROD_SIZE_MIN;
                if(m_FishrodAngle < FISHROD_MAX_ANGLE) m_FishrodAngle++;

                if(m_FishrodSize == FISHROD_SIZE_MIN && m_FishrodAngle == FISHROD_MAX_ANGLE)
                {
                    if(pPlayerHook.i_ObjectType != SPRITE_HOOK_CATCH)
                    {
                         switch(pPlayerHook.i_ObjectType)
                         {
                             case SPRITE_HOOK_CATCH_1: addScore(SCORES_FISH1); break;
                             case SPRITE_HOOK_CATCH_2: addScore(SCORES_FISH2); break;
                             case SPRITE_HOOK_CATCH_3: addScore(SCORES_FISH3); break;
                             case SPRITE_HOOK_CATCH_4: addScore(SCORES_FISH4); break;
                             case SPRITE_HOOK_CATCH_5: addScore(SCORES_FISH5); break;
                             case SPRITE_HOOK_CATCH_6: addScore(SCORES_FISH6); break;
                         }
                        m_pAbstractGameActionListener.processGameAction(GAMEACTION_CAUGHT_FISH);
                        m_nFishCounter++;
                    }
                    initSpriteFromArray(pPlayerHook, SPRITE_HOOK);
                }
            }
        }

        int hook_x = pPlayerHook.i_mainX;
        int hook_y = pPlayerHook.i_mainY;

        if(pPlayerHook.i_ObjectType == SPRITE_HOOK_FALL)
        {
            hook_y += HOOK_FALL_SPEED;
            if (hook_y > i8_gameScreenHeight)
            {
                if (hook_y > (i8_gameScreenHeight - HOOK_FALL_SPEED))
                {
                    m_pAbstractGameActionListener.processGameAction(GAMEACTION_HOOK_ON_THE_FLOOR);
                }
                return false;
            }
        }
        else
        {




            m_FishrodX1 = m_FishrodX0 + xCoSine(FISHROD_LENGTH, m_FishrodAngle);
            m_FishrodY1 = m_FishrodY0 - xSine(FISHROD_LENGTH, m_FishrodAngle);

            hook_x = m_FishrodX1;
            hook_y = m_FishrodY1 + m_FishrodSize - FISHROD_LENGTH;
        }

        pPlayerHook.setMainPointXY(hook_x, hook_y);

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
                }
                ;
                break;
            case GAMELEVEL_NORMAL:
                {
                    i_timedelay = LEVEL_NORMAL_TIMEDELAY;
                    i_borndelay = LEVEL_NORMAL_ENEMYFREQ;
                    m_iPlayerAttemptions = LEVEL_NORMAL_LIVES;
                }
                ;
                break;
            case GAMELEVEL_HARD:
                {
                    i_timedelay = LEVEL_HARD_TIMEDELAY;
                    i_borndelay = LEVEL_HARD_ENEMYFREQ;
                    m_iPlayerAttemptions = LEVEL_HARD_LIVES;
                }
                ;
                break;
        }

        i_GameTimer = 0; 
        i_lasttime = i_GameTimer;
        m_nFishCounter = 0;
        m_bEnemyFish = false;

        m_FishrodX0     = FISHROD_X0;
        m_FishrodY0     = FISHROD_Y0;
        m_FishrodAngle  = FISHROD_ANGLE;
        m_FishrodSize   = FISHROD_SIZE;

        m_FishrodX1 = m_FishrodX0 + xCoSine(FISHROD_LENGTH, FISHROD_ANGLE);
        m_FishrodY1 = m_FishrodY0 - xSine(FISHROD_LENGTH, FISHROD_ANGLE);
        pPlayerHook.setMainPointXY(m_FishrodX1, m_FishrodY1 + m_FishrodSize - FISHROD_LENGTH);


        return true;
    }

    public void resumeGameAfterPlayerLostOrPaused()
    {
        super.resumeGameAfterPlayerLostOrPaused();

        deinitState();
        initState();

        i_GameTimer = 0; 
        i_lasttime = i_GameTimer;
        m_bEnemyFish = false;

        m_FishrodX0     = FISHROD_X0;
        m_FishrodY0     = FISHROD_Y0;
        m_FishrodAngle  = FISHROD_ANGLE;
        m_FishrodSize   = FISHROD_SIZE;

        m_FishrodX1 = m_FishrodX0 + xCoSine(FISHROD_LENGTH, FISHROD_ANGLE);
        m_FishrodY1 = m_FishrodY0 - xSine(FISHROD_LENGTH, FISHROD_ANGLE);
        pPlayerHook.setMainPointXY(m_FishrodX1, m_FishrodY1 + m_FishrodSize - FISHROD_LENGTH);
    }

    public void deinitState()
    {
        ap_Sprites = null;
        pPlayerHook = null;
        ap_Paths = null;
        p_MoveObject = null;

        Runtime.getRuntime().gc();
    }

    public int _getMaximumDataSize()
    {
        return MAX_PATHS*PathController.DATASIZE_BYTES + MAX_SPRITES*(Sprite.DATASIZE_BYTES+1)+Sprite.DATASIZE_BYTES+1 + 38;
    }

    public void _saveGameData(DataOutputStream _outputStream) throws IOException
    {
        _outputStream.writeByte(m_iPlayerAttemptions);
        _outputStream.writeInt(i_GameTimer);
        _outputStream.writeInt(i_lasttime);
        _outputStream.writeInt(i_borndelay);
        _outputStream.writeInt(m_nFishCounter);
        _outputStream.writeBoolean(m_bEnemyFish);

        for(int li=0;li<MAX_SPRITES;li++)
        {
            _outputStream.writeByte(ap_Sprites[li].i_ObjectType);
            ap_Sprites[li].writeSpriteToStream(_outputStream);
        }

        for(int li=0;li<MAX_PATHS;li++) ap_Paths[li].writePathToStream(_outputStream);

        _outputStream.writeInt(i_rndfreq);

        _outputStream.writeByte(pPlayerHook.i_ObjectType);
        pPlayerHook.writeSpriteToStream(_outputStream);

        _outputStream.writeInt(m_FishrodX0);
        _outputStream.writeInt(m_FishrodY0);
        _outputStream.writeInt(m_FishrodAngle);
        _outputStream.writeInt(m_FishrodSize);
    }

    public void _loadGameData(DataInputStream _inputStream) throws IOException
    {
        m_iPlayerAttemptions = _inputStream.readByte();
        i_GameTimer = _inputStream.readInt();
        i_lasttime = _inputStream.readInt();
        i_borndelay = _inputStream.readInt();
        m_nFishCounter = _inputStream.readInt();
        m_bEnemyFish = _inputStream.readBoolean();

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

        initSpriteFromArray(pPlayerHook,_inputStream.readUnsignedByte());
        pPlayerHook.readSpriteFromStream(_inputStream);

        m_FishrodX0    = _inputStream.readInt();
        m_FishrodY0    = _inputStream.readInt();
        m_FishrodAngle = _inputStream.readInt();
        m_FishrodSize  = _inputStream.readInt();

        m_FishrodX1 = m_FishrodX0 + xCoSine(FISHROD_LENGTH, m_FishrodAngle);
        m_FishrodY1 = m_FishrodY0 - xSine(FISHROD_LENGTH, m_FishrodAngle);
    }

    private void generateNewFish()
    {
        if((i_lasttime + i_borndelay) < i_GameTimer )
        {
            i_lasttime = i_GameTimer;
            Sprite p_emptySprite = getInactiveSprite();
            PathController p_inactivePath = getInactivePath();
            if (p_emptySprite != null && p_inactivePath != null)
            {
                int i_type = getRandomInt(115) / 10;

                if(!m_bEnemyFish)
                {
                    if(getRandomInt(8) < 4)
                    {
                        i_type = 12;
                    }
                    else
                    {
                        i_type = 13;
                    }
                }

                int i_path = 0;

                    switch (i_type)
                    {
                        default:
                        case 0:
                            {
                                i_path = PATH_FISH_1;
                                i_type = SPRITE_FISH_1;
                            }
                            break;
                        case 1:
                            {
                                i_path = PATH_FISH_2;
                                i_type = SPRITE_FISH_1;
                            }
                            break;
                        case 2:
                            {
                                i_path = PATH_FISH_3;
                                i_type = SPRITE_FISH_2;
                            }
                            break;
                        case 3:
                            {
                                i_path = PATH_FISH_4;
                                i_type = SPRITE_FISH_2;
                            }
                            break;
                        case 4:
                            {
                                i_path = PATH_FISH_5;
                                i_type = SPRITE_FISH_3;
                            }
                            break;
                        case 5:
                            {
                                i_path = PATH_FISH_6;
                                i_type = SPRITE_FISH_3;
                            }
                            break;
                        case 6:
                            {
                                i_path = PATH_FISH_7;
                                i_type = SPRITE_FISH_4;
                            }
                            break;
                        case 7:
                            {
                                i_path = PATH_FISH_8;
                                i_type = SPRITE_FISH_4;
                            }
                            break;
                        case 8:
                            {
                                i_path = PATH_FISH_9;
                                i_type = SPRITE_FISH_5;
                            }
                            break;
                        case 9:
                            {
                                i_path = PATH_FISH_10;
                                i_type = SPRITE_FISH_5;
                            }
                            break;
                        case 10:
                            {
                                i_path = PATH_FISH_11;
                                i_type = SPRITE_FISH_6;
                            }
                            break;
                        case 11:
                            {
                                i_path = PATH_FISH_12;
                                i_type = SPRITE_FISH_6;
                            }
                            break;
                        case 12:
                            {
                                i_path = PATH_ENEMY_FISH_1;
                                i_type = SPRITE_ENEMY_FISH_1;
                                m_bEnemyFish = true;
                                m_pAbstractGameActionListener.processGameAction(GAMEACTION_ENEMY_FISH);
                            }
                            break;
                         case 13:
                            {
                                i_path = PATH_ENEMY_FISH_2;
                                i_type = SPRITE_ENEMY_FISH_2;
                                m_bEnemyFish = true;
                                m_pAbstractGameActionListener.processGameAction(GAMEACTION_ENEMY_FISH);
                            }
                            break;
                    }

                initSpriteFromArray(p_emptySprite, i_type);
                p_inactivePath.initPath(0, 0, p_emptySprite, ash_Paths, i_path, 0, 0, PathController.MODIFY_NONE);
                p_emptySprite.lg_SpriteActive = true;
            }
        }
    }

    public int nextGameStep()
    {
        CAbstractPlayer p_player = m_pPlayerList[0];
        p_player.nextPlayerMove(this, p_MoveObject);

        processPaths();

        if (processFisher(p_MoveObject.i_buttonState) )
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
        generateNewFish();

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
