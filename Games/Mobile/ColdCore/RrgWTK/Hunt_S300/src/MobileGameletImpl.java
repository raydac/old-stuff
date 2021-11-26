
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.DataInputStream;

public class MobileGameletImpl extends MobileGamelet
{
    private static final int GUNSIGHT_VERTMIN_SPEED = 0x800;

    private static final int GUNSIGHT_VERTMAX_SPEED = 0x800;

    private static final int GUNSIGHT_HORZTMIN_SPEED = 0x800;

    private static final int GUNSIGHT_HORZMAX_SPEED = 0x800;

    private static final int GUNSIGHT_CHANGESPEED_DELAY = 5;

    private static final int LEVEL_EASY_TIMEDELAY = 150;

    private static final int LEVEL_EASY_STARTFREQ = 0xC800;

    private static final int LEVEL_EASY_FREQSPEED = 0x100;

    private static final int LEVEL_NORMAL_TIMEDELAY = 110;

    private static final int LEVEL_NORMAL_STARTFREQ = 0x9600;

    private static final int LEVEL_NORMAL_FREQSPEED = 0x100;

    private static final int LEVEL_HARD_TIMEDELAY = 80;

    private static final int LEVEL_HARD_STARTFREQ = 0x6400;

    private static final int LEVEL_HARD_FREQSPEED = 0x100;

    private static final int SCORES_FOX = 30;

    private static final int SCORES_WOLF = 10;

    private static final int SCORES_PIG = 20;

    private static final int SCORES_RABBIT = 40;

    private static final int SCORES_RAVEN = 5;

    private static final int INIT_BULLETS = 20;

    private static final int SKIP_ANIMAL_NUMBER = 10;

    private int i_gunsight_delay;

    private int i_timedelay;

    private int i_rndfreq;

    private int i_limitrndfreq;

    private int i_rndfreqspeed;

    public int i_bulletNumber;

    private static final int SPRITEDATALENGTH = 8;

    public static final int SPRITE_GUNBARREL = 0;

    public static final int SPRITE_GUNSIGHT = 0;

    public static final int SPRITE_GUNSIGHTFIRE = 1;

    public static final int SPRITE_RAVEN = 2;

    public static final int SPRITE_RAVENAFRAID = 3;

    public static final int SPRITE_RAVENKILLED = 4;

    public static final int SPRITE_FOX = 5;

    public static final int SPRITE_FOXAFRAID = 6;

    public static final int SPRITE_FOXKILLED = 7;

    public static final int SPRITE_PIG = 8;

    public static final int SPRITE_PIGAFRAID = 9;

    public static final int SPRITE_PIGKILLED = 10;

    public static final int SPRITE_RABBIT = 11;

    public static final int SPRITE_RABBITAFRAID = 12;

    public static final int SPRITE_RABBITKILLED = 13;

    public static final int SPRITE_WOLF = 14;

    public static final int SPRITE_WOLFAFRAID = 15;

    public static final int SPRITE_WOLFKILLED = 16;

    public static final int MAX_SPRITES = 8;

    private static final int MAX_PATHS = 8;

    private static final int PATH_RAVEN_1 = 0;

    private static final int PATH_RAVEN_2 = 7;

    private static final int PATH_PIG = 23;

    private static final int PATH_WOLF = 45;

    private static final int PATH_RABBIT = 67;

    private static final int PATH_FOX = 80;

    public Sprite p_gunSight;

    public Sprite p_gunBarrel;

    public Sprite[] ap_Sprites;

    public Sprite[] ap_firstPlaneObstacles;

    public Sprite[] ap_secondPlaneObstacles;

    public Sprite[] ap_thirdPlaneObstacles;

    private PathController[] ap_Paths;

    public MoveObject p_MoveObject;

    public Sprite p_currentUnderSightAnimal;

    private Sprite p_lastKilledAnimal;

    private int i_animalsCounter;

    private int i_animalsTotalKilled;

    private int i_skippedAnimalNumber;

    public static final int GAMEACTION_FOX_APPEARED = 0;

    public static final int GAMEACTION_FOX_AFRAID = 1;

    public static final int GAMEACTION_FOX_KILLED = 2;

    public static final int GAMEACTION_PIG_APPEARED = 3;

    public static final int GAMEACTION_PIG_AFRAID = 4;

    public static final int GAMEACTION_PIG_KILLED = 5;

    public static final int GAMEACTION_RABBIT_APPEARED = 6;

    public static final int GAMEACTION_RABBIT_AFRAID = 7;

    public static final int GAMEACTION_RABBIT_KILLED = 8;

    public static final int GAMEACTION_WOLF_APPEARED = 9;

    public static final int GAMEACTION_WOLF_AFRAID = 10;

    public static final int GAMEACTION_WOLF_KILLED = 11;

    public static final int GAMEACTION_RAVEN_APPEARED = 12;

    public static final int GAMEACTION_RAVEN_AFRAID = 13;

    public static final int GAMEACTION_RAVEN_KILLED = 14;

    public static final int GAMEACTION_PLAYER_FIRING = 15;

    public static final int GAMEACTION_PLAYER_FIRING_WITHOUT_BULLETS = 16;

    public static final int GAMEACTION_BULLET_REMOVED = 17;


    private int i8_gameScreenWidth;

    private int i8_gameScreenHeight;


    private static final short[] ash_Paths = new short[]
    {
        1, 0,  -15,15,50,   140,35,
        4, 0,  -15,35,20,   30,40,20,    60,35,20,   90,20,20,  150,35,
        6, 0,  38,140-(160-128),16,   48,130-(160-128),18,   57,122-(160-128),18,  66,118-(160-128),18, 78,116-(160-128),18, 88,116-(160-128),16, 100,116-(160-128),
        6, 0,  130,120-(160-128),6,   85,128-(160-128),8,    68,135-(160-128),8,   45,127-(160-128),8,  29,120-(160-128),8,  11,128-(160-128),8,  -7,133-(160-128),
        3, 0,  36,158-(160-128),10,   36,130-(160-128),14,   36,120-(160-128),5,   36,158-(160-128),
        1, 0, -14,144-(160-128),14,   30,144-(160-128)
    };


    private static final short[] ash_FirstPlaneObstacles = new short[]
    {
         23,136-(160-128),32,24,
         99,149-(160-128),29,11,
         119,138-(160-128),9,11
    };

    private static final short[] ash_SecondPlaneObstacles = new short[]
    {
        0,64-(160-128),18,96,
        18,136-(160-128),11,24,
        56,143-(160-128),8,17,
        64,150-(160-128),17,10,
        106,136-(160-128),19,11,
        121,106-(160-128),7,30
    };

    private static final short[] ash_ThirdPlaneObstacles = new short[]
    {
         96,108-(160-128),11,30
    };

    private static final int[] ai_SpriteParameters = new int[]
    {
        0x1C00, 0x1C00, 0x500, 0x500, 1, 0, Sprite.SPRITE_ALIGN_CENTER, Sprite.ANIMATION_FROZEN,
        0x1000, 0x1000, 0x500, 0x500, 2, 1, Sprite.SPRITE_ALIGN_CENTER, Sprite.ANIMATION_FROZEN,
        0x1500, 0x1500, 0x1500, 0x1500, 3, 1, Sprite.SPRITE_ALIGN_CENTER, Sprite.ANIMATION_CYCLIC,
        0x1500, 0x1500, 0x1500, 0x1500, 2, 1, Sprite.SPRITE_ALIGN_CENTER, Sprite.ANIMATION_CYCLIC,
        0x1500, 0x1500, 0x1500, 0x1500, 3, 1, Sprite.SPRITE_ALIGN_CENTER, Sprite.ANIMATION_FROZEN,
        0x1500, 0x1500, 0x1500, 0x1500, 3, 1, Sprite.SPRITE_ALIGN_CENTER, Sprite.ANIMATION_CYCLIC,
        0x1500, 0x1500, 0x1500, 0x1500, 2, 1, Sprite.SPRITE_ALIGN_CENTER, Sprite.ANIMATION_CYCLIC,
        0x1500, 0x1500, 0x1500, 0x1500, 3, 1, Sprite.SPRITE_ALIGN_CENTER, Sprite.ANIMATION_FROZEN,
        0x1200, 0x1000, 0x1200, 0x1000, 3, 1, Sprite.SPRITE_ALIGN_CENTER, Sprite.ANIMATION_CYCLIC,
        0x1200, 0x1000, 0x1200, 0x1000, 2, 1, Sprite.SPRITE_ALIGN_CENTER, Sprite.ANIMATION_CYCLIC,
        0x1200, 0x1000, 0x1200, 0x1000, 3, 1, Sprite.SPRITE_ALIGN_CENTER, Sprite.ANIMATION_FROZEN,
        0x1500, 0x1500, 0x1500, 0x1500, 1, 1, Sprite.SPRITE_ALIGN_CENTER, Sprite.ANIMATION_CYCLIC,
        0x1500, 0x1500, 0x1500, 0x1500, 2, 1, Sprite.SPRITE_ALIGN_CENTER, Sprite.ANIMATION_CYCLIC,
        0x1500, 0x1500, 0x1500, 0x1500, 3, 1, Sprite.SPRITE_ALIGN_CENTER, Sprite.ANIMATION_FROZEN,
        0x1500, 0x1500, 0x1500, 0x1500, 3, 1, Sprite.SPRITE_ALIGN_CENTER, Sprite.ANIMATION_CYCLIC,
        0x1500, 0x1500, 0x1500, 0x1500, 2, 1, Sprite.SPRITE_ALIGN_CENTER, Sprite.ANIMATION_CYCLIC,
        0x1500, 0x1500, 0x1500, 0x1500, 3, 1, Sprite.SPRITE_ALIGN_CENTER, Sprite.ANIMATION_FROZEN
    };

    private static final int SPRITEGUNBARRELDATALENGTH = 2;

    private static final int SPRITEGUNBARELLNUMBER = 7;

    private static final int LOWLEVELOFBARREL = 0x500;

    private static final int MAXBARRELSIZE = 0x5000;

    private static int i8_multiplierOfGunX = 0;

    private static int i8_multiplierOfGunY = 0;

    private static final int[] ai_SpriteGunParameters = new int []
    {
        0x4000, 0x5000,
        0x3000, 0x5000,
        0x2000, 0x5000,
        0x1000, 0x5000,
        0x2000, 0x5000,
        0x3000, 0x5000,
        0x4000, 0x5000
    };

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

    private void initGunSpriteFromArray(int _state)
    {
        p_gunBarrel.i_ObjectType = SPRITE_GUNBARREL;
        _state *= SPRITEGUNBARRELDATALENGTH;
        int i_w = ai_SpriteGunParameters[_state++];
        int i_h = ai_SpriteGunParameters[_state];

        p_gunBarrel.setAnimation(Sprite.ANIMATION_FROZEN, Sprite.SPRITE_ALIGN_TOP | Sprite.SPRITE_ALIGN_CENTER, i_w, i_h, 1, 0, 1);
        p_gunBarrel.lg_SpriteActive = true;
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

    private int checkSightForObstacle()
    {
        for (int li = 0; li < ap_firstPlaneObstacles.length; li++)
        {
            if (p_gunSight.isCollided(ap_firstPlaneObstacles[li])) return 0;
        }
        for (int li = 0; li < ap_secondPlaneObstacles.length; li++)
        {
            if (p_gunSight.isCollided(ap_secondPlaneObstacles[li])) return 1;
        }
        for (int li = 0; li < ap_thirdPlaneObstacles.length; li++)
        {
            if (p_gunSight.isCollided(ap_thirdPlaneObstacles[li])) return 2;
        }
        return -1;
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
                        case SPRITE_FOXKILLED:
                        case SPRITE_PIGKILLED:
                        case SPRITE_WOLFKILLED:
                        case SPRITE_RABBITKILLED:
                        case SPRITE_RAVENKILLED:
                             {
                                ap_Sprites[li].lg_SpriteActive = false;
                            }
                    }
                }
            }
        }
    }

    private void processGunBarrel()
    {
        int i_sx = p_gunSight.i_mainX;
        int i_sy = p_gunSight.i_mainY;

        int i_cadr = (i8_multiplierOfGunX * i_sx)>>16;

        i_sy = (i8_gameScreenHeight - MAXBARRELSIZE)+((i_sy*i8_multiplierOfGunY)>>8);

        initGunSpriteFromArray(i_cadr);
        p_gunBarrel.setMainPointXY(p_gunBarrel.i_mainX,i_sy);
        p_gunBarrel.i_ObjectState = i_cadr;
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
                case SPRITE_FOXAFRAID:
                case SPRITE_PIGAFRAID:
                case SPRITE_WOLFAFRAID:
                case SPRITE_RABBITAFRAID:
                    lg_moved = false;
            }

            if (lg_moved)
            {
                ap_Paths[li].processStep();
                if (ap_Paths[li].isCompleted())
                {
                    ap_Paths[li].p_sprite.lg_SpriteActive = false;
                    if (i_skippedAnimalNumber > 0)
                    {
                        i_skippedAnimalNumber--;
                    }
                    else
                    {
                        i_skippedAnimalNumber = SKIP_ANIMAL_NUMBER;
                        if (i_bulletNumber > 0) i_bulletNumber--;
                    }
                }
            }
        }
    }

    public String getGameTextID()
    {
        return "HUNT";
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

        p_gunSight = new Sprite(0);
        initSpriteFromArray(p_gunSight, SPRITE_GUNSIGHT);
        p_gunSight.lg_SpriteActive = true;

        int i8_cx = i8_gameScreenWidth >> 1;
        int i8_cy = i8_gameScreenHeight >> 1;

        p_gunSight.setMainPointXY(i8_cx, i8_cy);
        i_bulletNumber = INIT_BULLETS;
        p_MoveObject = new MoveObject();
        i_gunsight_delay = GUNSIGHT_CHANGESPEED_DELAY;

        i_animalsCounter = 0;

        short[] ash_plane = ash_FirstPlaneObstacles;
        int i_size = ash_plane.length / 4;
        int i_indx = 0;

        ap_firstPlaneObstacles = new Sprite[i_size];

        for (int li = 0; li < i_size; li++)
        {
            int i_x = ash_plane[i_indx++]<<8;
            int i_y = ash_plane[i_indx++]<<8;
            int i_w = ash_plane[i_indx++]<<8;
            int i_h = ash_plane[i_indx++]<<8;

            Sprite p_newSprite = new Sprite(0);
            p_newSprite.setAnimation(Sprite.ANIMATION_FROZEN, Sprite.SPRITE_ALIGN_LEFT | Sprite.SPRITE_ALIGN_TOP, i_w, i_h, 1, 0, 1);
            p_newSprite.setMainPointXY(i_x, i_y);
            p_newSprite.setCollisionBounds(0,0,i_w, i_h);

            ap_firstPlaneObstacles[li] = p_newSprite;
        }

        ash_plane = ash_SecondPlaneObstacles;
        i_size = ash_plane.length / 4;
        i_indx = 0;

        ap_secondPlaneObstacles = new Sprite[i_size];

        for (int li = 0; li < i_size; li++)
        {
            int i_x = ash_plane[i_indx++]<<8;
            int i_y = ash_plane[i_indx++]<<8;
            int i_w = ash_plane[i_indx++]<<8;
            int i_h = ash_plane[i_indx++]<<8;

            Sprite p_newSprite = new Sprite(0);
            p_newSprite.setAnimation(Sprite.ANIMATION_FROZEN, Sprite.SPRITE_ALIGN_LEFT | Sprite.SPRITE_ALIGN_TOP, i_w, i_h, 1, 0, 1);
            p_newSprite.setMainPointXY(i_x, i_y);
            p_newSprite.setCollisionBounds(0,0,i_w, i_h);

            ap_secondPlaneObstacles[li] = p_newSprite;
        }

        ash_plane = ash_ThirdPlaneObstacles;
        i_size = ash_plane.length / 4;
        i_indx = 0;

        ap_thirdPlaneObstacles = new Sprite[i_size];

        for (int li = 0; li < i_size; li++)
        {
            int i_x = ash_plane[i_indx++]<<8;
            int i_y = ash_plane[i_indx++]<<8;
            int i_w = ash_plane[i_indx++]<<8;
            int i_h = ash_plane[i_indx++]<<8;

            Sprite p_newSprite = new Sprite(0);
            p_newSprite.setAnimation(Sprite.ANIMATION_FROZEN, Sprite.SPRITE_ALIGN_LEFT | Sprite.SPRITE_ALIGN_TOP, i_w, i_h, 1, 0, 1);
            p_newSprite.setMainPointXY(i_x, i_y);
            p_newSprite.setCollisionBounds(0,0,i_w, i_h);

            ap_thirdPlaneObstacles[li] = p_newSprite;
        }

        i8_multiplierOfGunX = (SPRITEGUNBARELLNUMBER<<16) / i8_gameScreenWidth;
        i8_multiplierOfGunY = ((MAXBARRELSIZE - LOWLEVELOFBARREL)<<8) / i8_gameScreenHeight;


        p_gunBarrel = new Sprite(SPRITE_GUNBARREL);
        p_gunBarrel.setMainPointXY(i8_gameScreenWidth>>1,0);

        return true;
    }

    private void checkUnderSightAnimal()
    {
        for (int li = 0; li < MAX_SPRITES; li++)
        {
            Sprite p_sprite = ap_Sprites[li];
            if (p_sprite.lg_SpriteActive)
            {
                if (p_gunSight.isCollided(p_sprite))
                {
                    if (!p_sprite.equals(p_currentUnderSightAnimal))
                    {
                        if (p_currentUnderSightAnimal != null) break;

                        int i_obstaclePlan = checkSightForObstacle();

                        p_currentUnderSightAnimal = p_sprite;

                        switch (p_currentUnderSightAnimal.i_ObjectType)
                        {
                            case SPRITE_FOX:
                                {
                                    if (i_obstaclePlan == 0) continue;
                                    initSpriteFromArray(p_currentUnderSightAnimal, SPRITE_FOXAFRAID);
                                    m_pAbstractGameActionListener.processGameAction(GAMEACTION_FOX_AFRAID);
                                }
                                ;
                                break;
                            case SPRITE_PIG:
                                {
                                    if (i_obstaclePlan >= 0) continue;
                                    initSpriteFromArray(p_currentUnderSightAnimal, SPRITE_PIGAFRAID);
                                    m_pAbstractGameActionListener.processGameAction(GAMEACTION_PIG_AFRAID);
                                }
                                ;
                                break;
                            case SPRITE_RABBIT:
                                {
                                    if (i_obstaclePlan == 0) continue;
                                    initSpriteFromArray(p_currentUnderSightAnimal, SPRITE_RABBITAFRAID);
                                    m_pAbstractGameActionListener.processGameAction(GAMEACTION_RABBIT_AFRAID);
                                }
                                ;
                                break;
                            case SPRITE_RAVEN:
                                {
                                    initSpriteFromArray(p_currentUnderSightAnimal, SPRITE_RAVENAFRAID);
                                    m_pAbstractGameActionListener.processGameAction(GAMEACTION_RAVEN_AFRAID);
                                }
                                ;
                                break;
                            case SPRITE_WOLF:
                                {
                                    if (i_obstaclePlan == 1 || i_obstaclePlan == 0) continue;
                                    initSpriteFromArray(p_currentUnderSightAnimal, SPRITE_WOLFAFRAID);
                                    m_pAbstractGameActionListener.processGameAction(GAMEACTION_WOLF_AFRAID);
                                }
                                ;
                                break;
                        }
                    }
                    return;
                }
            }
        }

        if (p_currentUnderSightAnimal != null)
        {
            switch (p_currentUnderSightAnimal.i_ObjectType)
            {
                case SPRITE_FOXAFRAID:
                    {
                        initSpriteFromArray(p_currentUnderSightAnimal, SPRITE_FOX);
                    }
                    ;
                    break;
                case SPRITE_PIGAFRAID:
                    {
                        initSpriteFromArray(p_currentUnderSightAnimal, SPRITE_PIG);
                    }
                    ;
                    break;
                case SPRITE_RABBITAFRAID:
                    {
                        initSpriteFromArray(p_currentUnderSightAnimal, SPRITE_RABBIT);
                    }
                    ;
                    break;
                case SPRITE_RAVENAFRAID:
                    {
                        initSpriteFromArray(p_currentUnderSightAnimal, SPRITE_RAVEN);
                    }
                    ;
                    break;
                case SPRITE_WOLFAFRAID:
                    {
                        initSpriteFromArray(p_currentUnderSightAnimal, SPRITE_WOLF);
                    }
                    ;
                    break;
            }
        }
        p_currentUnderSightAnimal = null;
    }

    private boolean processGunSight(int _buttonState)
    {
        boolean lg_firing = false;

        if (p_gunSight.processAnimation() && p_gunSight.i_ObjectType == SPRITE_GUNSIGHTFIRE)
        {
            initSpriteFromArray(p_gunSight, SPRITE_GUNSIGHT);
        }

        if (p_gunSight.i_ObjectType == SPRITE_GUNSIGHT)
        {
            int i_x = p_gunSight.i_ScreenX;
            int i_y = p_gunSight.i_ScreenY;

            int i_mx = p_gunSight.i_mainX;
            int i_my = p_gunSight.i_mainY;

            int i_w = p_gunSight.i_width;
            int i_h = p_gunSight.i_height;

            int i_spV, i_spH;

            if (i_gunsight_delay == 0)
            {
                i_spH = GUNSIGHT_HORZMAX_SPEED;
                i_spV = GUNSIGHT_VERTMAX_SPEED;
            }
            else
            {
                i_spH = GUNSIGHT_HORZTMIN_SPEED;
                i_spV = GUNSIGHT_VERTMIN_SPEED;
            }

            if ((_buttonState & MoveObject.BUTTON_LEFT) != 0)
            {
                i_mx = i_x - i_spH < 0 ? (i_w >> 1) : i_mx - i_spH;
            }
            else if ((_buttonState & MoveObject.BUTTON_RIGHT) != 0)
            {
                i_mx = i_x + i_w + i_spH >= i8_gameScreenWidth ? i8_gameScreenWidth - (i_w >> 1) : i_mx + i_spH;
            }

            if ((_buttonState & MoveObject.BUTTON_UP) != 0)
            {
                i_my = i_y - i_spV < 0 ? (i_h >> 1) : i_my - i_spV;
            }
            else if ((_buttonState & MoveObject.BUTTON_DOWN) != 0)
            {
                i_my = i_y + i_h + i_spV >= i8_gameScreenHeight ? i8_gameScreenHeight - (i_h >> 1) : i_my + i_spV;
            }

            if ((_buttonState & MoveObject.BUTTON_FIRE) != 0)
            {
                if (i_bulletNumber == 0)
                {
                    m_pAbstractGameActionListener.processGameAction(GAMEACTION_PLAYER_FIRING_WITHOUT_BULLETS);
                }
                else
                {
                    i_bulletNumber--;
                    initSpriteFromArray(p_gunSight, SPRITE_GUNSIGHTFIRE);
                    i_gunsight_delay = GUNSIGHT_CHANGESPEED_DELAY;
                    lg_firing = true;
                    i_skippedAnimalNumber = SKIP_ANIMAL_NUMBER;
                    m_pAbstractGameActionListener.processGameAction(GAMEACTION_PLAYER_FIRING);
                }
            }
            else
            {
                if (_buttonState == MoveObject.BUTTON_NONE)
                    i_gunsight_delay = GUNSIGHT_CHANGESPEED_DELAY;
                else if (i_gunsight_delay > 0) i_gunsight_delay--;
            }

            p_gunSight.setMainPointXY(i_mx, i_my);
        }
        return lg_firing;
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
                    i_rndfreq = LEVEL_EASY_STARTFREQ;
                    i_rndfreqspeed = LEVEL_EASY_FREQSPEED;
                    i_limitrndfreq = i_rndfreq >> 1;
                }
                ;
                break;
            case GAMELEVEL_NORMAL:
                {
                    i_timedelay = LEVEL_NORMAL_TIMEDELAY;
                    i_rndfreq = LEVEL_NORMAL_STARTFREQ;
                    i_rndfreqspeed = LEVEL_NORMAL_FREQSPEED;
                    i_limitrndfreq = i_rndfreq >> 1;
                }
                ;
                break;
            case GAMELEVEL_HARD:
                {
                    i_timedelay = LEVEL_HARD_TIMEDELAY;
                    i_rndfreq = LEVEL_HARD_STARTFREQ;
                    i_rndfreqspeed = LEVEL_HARD_FREQSPEED;
                    i_limitrndfreq = i_rndfreq >> 1;
                }
                ;
                break;
        }

        return true;
    }

    public void deinitState()
    {
        ap_Sprites = null;
        p_gunSight = null;
        ap_Paths = null;
        p_MoveObject = null;
        p_lastKilledAnimal = null;
        p_currentUnderSightAnimal = null;
        p_gunBarrel = null;

        ap_firstPlaneObstacles = null;
        ap_secondPlaneObstacles = null;
        ap_thirdPlaneObstacles = null;

        Runtime.getRuntime().gc();
    }

    public int _getMaximumDataSize()
    {
        return MAX_PATHS*PathController.DATASIZE_BYTES+MAX_SPRITES*(Sprite.DATASIZE_BYTES+1)+6+Sprite.DATASIZE_BYTES+1;
    }

    public void _saveGameData(DataOutputStream _outputStream) throws IOException
    {
        for(int li=0;li<MAX_SPRITES;li++)
        {
            _outputStream.writeByte(ap_Sprites[li].i_ObjectType);
            ap_Sprites[li].writeSpriteToStream(_outputStream);
        }

        for(int li=0;li<MAX_PATHS;li++) ap_Paths[li].writePathToStream(_outputStream);

        _outputStream.writeInt(i_rndfreq);
        _outputStream.writeShort(i_bulletNumber);

        _outputStream.writeByte(p_gunSight.i_ObjectType);
        p_gunSight.writeSpriteToStream(_outputStream);
    }

    public void _loadGameData(DataInputStream _inputStream) throws IOException
    {
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
        i_bulletNumber = _inputStream.readShort();

        initSpriteFromArray(p_gunSight,_inputStream.readUnsignedByte());
        p_gunSight.readSpriteFromStream(_inputStream);
        processGunBarrel();
    }

    private boolean isAnimalPresented(int _animalType)
    {
        for (int li = 0; li < MAX_SPRITES; li++)
        {
            Sprite p_curSprite = ap_Sprites[li];
            if (!p_curSprite.lg_SpriteActive) continue;
            switch (_animalType)
            {
                case SPRITE_FOX:
                    {
                        switch (p_curSprite.i_ObjectType)
                        {
                            case SPRITE_FOX:
                            case SPRITE_FOXAFRAID:
                            case SPRITE_FOXKILLED:
                                return true;
                        }
                    }
                    ;
                    break;
                case SPRITE_RABBIT:
                    {
                        switch (p_curSprite.i_ObjectType)
                        {
                            case SPRITE_RABBIT:
                            case SPRITE_RABBITAFRAID:
                            case SPRITE_RABBITKILLED:
                                return true;
                        }
                    }
                    ;
                    break;
                case SPRITE_PIG:
                    {
                        switch (p_curSprite.i_ObjectType)
                        {
                            case SPRITE_PIG:
                            case SPRITE_PIGAFRAID:
                            case SPRITE_PIGKILLED:
                                return true;
                        }
                    }
                    ;
                    break;
                case SPRITE_RAVEN:
                    {
                        switch (p_curSprite.i_ObjectType)
                        {
                            case SPRITE_RAVEN:
                            case SPRITE_RAVENAFRAID:
                            case SPRITE_RAVENKILLED:
                                return true;
                        }
                    }
                    ;
                    break;
            }
        }
        return false;
    }

    private void generateNewAnimal()
    {
        if (getRandomInt(i_rndfreq >> 8) == (i_rndfreq >> 9))
        {
            Sprite p_emptySprite = getInactiveSprite();
            PathController p_inactivePath = getInactivePath();
            if (p_emptySprite != null && p_inactivePath != null)
            {
                int i_type = getRandomInt(100) / 20;

                int i_path = 0;
                while (true)
                {
                    switch (i_type)
                    {
                        case 0:
                            {
                                if (isAnimalPresented(SPRITE_RAVEN))
                                {
                                    i_type = 1;
                                    continue;
                                }
                                else
                                {
                                    i_path = getRandomInt(100) > 50 ? PATH_RAVEN_1 : PATH_RAVEN_2;
                                    i_type = SPRITE_RAVEN;
                                }
                            }
                            ;
                            break;
                        case 1:
                            {
                                if (isAnimalPresented(SPRITE_FOX))
                                {
                                    i_type = 3;
                                    continue;
                                }
                                else
                                {
                                    i_type = SPRITE_FOX;
                                    i_path = PATH_FOX;
                                }
                            }
                            ;
                            break;
                        case 2:
                            {
                                if (isAnimalPresented(SPRITE_RABBIT))
                                {
                                    i_type = 4;
                                    continue;
                                }
                                else
                                {
                                    i_path = PATH_RABBIT;
                                    i_type = SPRITE_RABBIT;
                                }
                            }
                            ;
                            break;
                        case 3:
                            {
                                i_path = PATH_PIG;
                                i_type = SPRITE_PIG;
                            }
                            ;
                            break;
                        case 4:
                            {
                                i_path = PATH_WOLF;
                                i_type = SPRITE_WOLF;
                            }
                            ;
                            break;
                        default:
                            {
                                 i_type = getRandomInt(100) / 20;
                            }
                            ;
                            continue;
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

        if (i_rndfreq > i_limitrndfreq) i_rndfreq -= i_rndfreqspeed;

        generateNewAnimal();

        processPaths();
        if (p_gunSight.i_ObjectType == SPRITE_GUNSIGHT)
        {
            checkUnderSightAnimal();
            if (processGunSight(p_MoveObject.i_buttonState) && p_currentUnderSightAnimal != null)
            {
                int i_scores = killUnderSightAnimal();
                p_player.setPlayerMoveGameScores(i_scores, false);
            }
        }
        else
        {
            p_currentUnderSightAnimal = null;
            processGunSight(0);
        }

        p_MoveObject.i_buttonState &= ~MoveObject.BUTTON_FIRE;
        ((Player)p_player).i_buttonState &= ~MoveObject.BUTTON_FIRE;

        processSprites();
        generateNewAnimal();
        processGunBarrel();

        if (i_bulletNumber == 0)
        {
            if ((p_lastKilledAnimal != null && !p_lastKilledAnimal.lg_SpriteActive)
                 || p_lastKilledAnimal == null)
            {
                 if (i_animalsTotalKilled > (i_animalsCounter >> 1)
                     && p_player.m_iPlayerGameScores > 0)
                 {
                      m_pWinningList = m_pPlayerList;
                 }
                    else
                         {
                             m_pWinningList = null;
                         }
                 setGameState(GAMEWORLDSTATE_GAMEOVER);
            }
        }

        return m_iGameState;
    }

    private int killUnderSightAnimal()
    {
        int i_score = 0;
        i_animalsTotalKilled++;
        p_lastKilledAnimal = p_currentUnderSightAnimal;
        deactivatePathForSpriteID(p_currentUnderSightAnimal.i_spriteID);
        switch (p_currentUnderSightAnimal.i_ObjectType)
        {
            case SPRITE_FOXAFRAID:
                {
                    initSpriteFromArray(p_currentUnderSightAnimal, SPRITE_FOXKILLED);
                    i_score = SCORES_FOX;
                    m_pAbstractGameActionListener.processGameAction(GAMEACTION_FOX_KILLED);
                }
                ;
                break;
            case SPRITE_PIGAFRAID:
                {
                    initSpriteFromArray(p_currentUnderSightAnimal, SPRITE_PIGKILLED);
                    i_score = SCORES_PIG;
                    m_pAbstractGameActionListener.processGameAction(GAMEACTION_PIG_KILLED);
                }
                ;
                break;
            case SPRITE_RABBITAFRAID:
                {
                    initSpriteFromArray(p_currentUnderSightAnimal, SPRITE_RABBITKILLED);
                    i_score = SCORES_RABBIT;
                    m_pAbstractGameActionListener.processGameAction(GAMEACTION_RABBIT_KILLED);
                }
                ;
                break;
            case SPRITE_WOLFAFRAID:
                {
                    initSpriteFromArray(p_currentUnderSightAnimal, SPRITE_WOLFKILLED);
                    i_score = SCORES_WOLF;
                    m_pAbstractGameActionListener.processGameAction(GAMEACTION_WOLF_KILLED);
                }
                ;
                break;
            case SPRITE_RAVENAFRAID:
                {
                    initSpriteFromArray(p_currentUnderSightAnimal, SPRITE_RAVENKILLED);
                    i_score = SCORES_RAVEN;
                    m_pAbstractGameActionListener.processGameAction(GAMEACTION_RAVEN_KILLED);
                }
                ;
                break;
        }
        p_currentUnderSightAnimal = null;
        return i_score;
    }

    public int getGameStepDelay()
    {
        return i_timedelay;
    }
}
