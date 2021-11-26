
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.DataInputStream;
import java.io.InputStream;


public class MobileGameletImpl extends MobileGamelet
{
    public static final int RIGHT_MARGIN = (24 << 8);
    public static final int LEFT_MARGIN = (24 << 8);

    public static final int FINISH_LINE = (8192 << 8);

    public static final int INIT_PLAYER_Y = (100 << 8);

    public static final int PLAYER_OFFSET = (128 - 100);

    private static final int HSPEED = (0x0300);      

    public static final int MAX_VSPEED = (0x1000);
    public static final int MIN_VSPEED = (0 << 8);
    public static final int INC_VSPEED = (0x0200); 
    public static final int DEC_VSPEED = (0x0200);


    public static final int SAFE_ZONE       = 0x0900; 
    public static final int SMART_FACTOR    = 1;

    public static final int REPAIR_TIMER = 10;
    public static final int WAIT_TIMER = 20;

    public static final int ROAD_WIDTH = (72 << 8);

    public static final int RACE_WIDTH = (192 << 8);

    public static final int CAMERA_WIDTH = (128 << 8);

    public static final int SPEED_CAR1 = (10 << 8); 
    public static final int SPEED_CAR2 = (12 << 8); 
    public static final int SPEED_CAR3 = (14 << 8); 

    public static final int FAR_FAR_AWAY = (96 << 8);

    private static int I8_SCREEN_WIDTH;

    private static int I8_SCREEN_HEIGHT;

    public MoveObject p_MoveObject;

    public static final int SCORES_COLLISION = -50;
    public static final int SCORES_RACING    = 1;

    public static final int GAMEACTION_PLAYER_CAR_SPEED_UP      = 0;
    public static final int GAMEACTION_PLAYER_CAR_HIT           = 1;
    public static final int GAMEACTION_PLAYER_CAR_STOP          = 2;
    public static final int GAMEACTION_PLAYER_CAR_BRAKE         = 3;
    public static final int GAMEACTION_PLAYER_CAR_READY         = 4;
    public static final int GAMEACTION_CAR_BREAK                = 5;
    public static final int GAMEACTION_CAR_COLLISION            = 6;
    public static final int GAMEACTION_EXPLOSION                = 7;

    public static final int GAMEACTION_TIME_OUT                 = 8;
    public static final int GAMEACTION_GOOD_TIME                = 9;
    public static final int GAMEACTION_BAD_TIME                 = 10;
    public static final int GAMEACTION_START_SESSION            = 11;



    private int i_timedelay;

    public int i_playerAttemptions;

    public int i_GameTimer;

    public int i_Horizont;

    public int i_HSpeed;

    public int i_CameraX;
    public int i_CameraL;
    public int i_CameraR;

    public int i_RaceTimer;

    public int i_LastTime;
    public int i_GenerateDelay;
    public int i_LastStoneTime;
    public int i_GenerateStoneDelay;

    private static final int STONE_DELAY = 7;

    public static final int LEVEL_EASY = 0;

    private static final int LEVEL_EASY_ATTEMPTIONS = 4;

    private static final int LEVEL_EASY_GENERATION = 15;
    private static final int LEVEL_EASY_RACETIME = 720;

    private static final int LEVEL_EASY_TIMEDELAY = 80;

    public static final int LEVEL_NORMAL = 1;

    private static final int LEVEL_NORMAL_ATTEMPTIONS = 3;

    private static final int LEVEL_NORMAL_GENERATION = 12;
    private static final int LEVEL_NORMAL_RACETIME = 640;

    private static final int LEVEL_NORMAL_TIMEDELAY = 80;

    public static final int LEVEL_HARD = 2;

    private static final int LEVEL_HARD_ATTEMPTIONS = 2;

    private static final int LEVEL_HARD_GENERATION = 10;
    private static final int LEVEL_HARD_RACETIME = 560;

    private static final int LEVEL_HARD_TIMEDELAY = 80;


    public Sprite pPlayerSprite;

    public static final int MAX_SPRITES_NUMBER = 32;

    public static final int MAX_STONES = 4;

    public Sprite[] ap_Sprites;

    private static final int SPRITEDATALENGTH = 10;

    public static final int SPRITE_CAR = 0;
    public static final int SPRITE_CAR_DEAD = 1;
    public static final int SPRITE_CAR_1 = 2;
    public static final int SPRITE_CAR_2 = 3;
    public static final int SPRITE_CAR_3 = 4;
    public static final int SPRITE_EXPLOSION = 5;
    public static final int SPRITE_STONE_SMALL = 6;
    public static final int SPRITE_STONE_MEDIUM = 7;
    public static final int SPRITE_STONE_LARGE = 8;


    private static final int [] ai_SpriteParameters = new int[]
    {
        0x0E00, 0x1F00, -0x001, 0x0000, 0x0E00, 0x1F00, 1, 1, Sprite.SPRITE_ALIGN_CENTER, Sprite.ANIMATION_CYCLIC,
        0x0E00, 0x1F00, -0x001, 0x0000, 0x0E00, 0x1F00, 4, 4, Sprite.SPRITE_ALIGN_CENTER, Sprite.ANIMATION_FROZEN,
        0x0E00, 0x1F00, -0x001, 0x0000, 0x0E00, 0x1F00, 1, 1, Sprite.SPRITE_ALIGN_CENTER, Sprite.ANIMATION_CYCLIC,
        0x0E00, 0x1F00, -0x001, 0x0000, 0x0E00, 0x1F00, 1, 1, Sprite.SPRITE_ALIGN_CENTER, Sprite.ANIMATION_CYCLIC,
        0x0E00, 0x1F00, -0x001, 0x0000, 0x0E00, 0x1F00, 1, 1, Sprite.SPRITE_ALIGN_CENTER, Sprite.ANIMATION_CYCLIC,
        0x2000, 0x2000, -0x001, 0x0000, 0x2000, 0x2000, 5, 1, Sprite.SPRITE_ALIGN_CENTER, Sprite.ANIMATION_FROZEN,
        0x0700, 0x0600, -0x001, 0x0000, 0x0200, 0x0200, 1, 1, Sprite.SPRITE_ALIGN_CENTER, Sprite.ANIMATION_FROZEN,
        0x0900, 0x0900, -0x001, 0x0000, 0x0200, 0x0200, 1, 1, Sprite.SPRITE_ALIGN_CENTER, Sprite.ANIMATION_FROZEN,
        0x0C00, 0x0A00, -0x001, 0x0000, 0x0300, 0x0300, 1, 1, Sprite.SPRITE_ALIGN_CENTER, Sprite.ANIMATION_FROZEN,
    };


    public static final byte[] ab_Road = new byte[4096];

    public int getRoad(int index)
    {
        index >>= 1;
        return (ab_Road[(index & 0xFFF)]) << 8;
    }

    public static final byte[] ab_LeftViews = new byte[128];

    public static final byte[] ab_RightViews = new byte[128];

    public static final int MAX_VIEWS = 16;
    public static final int MAX_VIEW_ARRAY = 128;

    public int getLeftView(int index)
    {
        return ab_LeftViews[index & (MAX_VIEW_ARRAY-1)];
    }

    public int getRightView(int index)
    {
        return ab_RightViews[index & (MAX_VIEW_ARRAY-1)];
    }

    public void RegenerateViews()
    {
        ab_LeftViews[0] = 14;
        ab_RightViews[0] = 15;

        for(int i = 1; i < MAX_VIEW_ARRAY; i++)
        {
            int a = getRandomInt(6);
            int b = getRandomInt(6)+7;
            ab_LeftViews[i] = (byte)a;
            ab_RightViews[i] = (byte)b;
        }
    }

    private int calculateScores(int _scores)
    {
        return 0;

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
        for (int li = MAX_STONES; li < MAX_SPRITES_NUMBER; li++)
        {
            if (!ap_Sprites[li].lg_SpriteActive) return ap_Sprites[li];
        }
        return null;
    }
    private Sprite getInactiveStone()
    {
        for (int li = 0; li < MAX_STONES; li++)
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
                    case SPRITE_EXPLOSION:
                        deactivateSprite(p_Sprite);
                        break;
                }
            }
            switch (p_Sprite.i_ObjectType)
            {
                case SPRITE_CAR_1:
                case SPRITE_CAR_2:
                case SPRITE_CAR_3:
                    if(p_Sprite.i_Timer > 0)
                    {
                        p_Sprite.i_Timer--;
                        if(p_Sprite.isCollided(pPlayerSprite))
                        {
                            ExplodeCar(p_Sprite);
                            pPlayerSprite.i_Speed = 0;
                            pPlayerSprite.i_Timer = REPAIR_TIMER;
                            addScore(SCORES_COLLISION);
                            m_pAbstractGameActionListener.processGameAction(GAMEACTION_PLAYER_CAR_HIT);
                        }
                    }
                    else
                    {
                        Car_AI(p_Sprite);
                    }
                    int objectX = p_Sprite.i_mainX;

                    if ((p_Sprite.i_ObjectState > pPlayerSprite.i_ObjectState - pPlayerSprite.i_height * SMART_FACTOR) && (p_Sprite.i_ObjectState - p_Sprite.i_height * SMART_FACTOR  < pPlayerSprite.i_ObjectState))
                    {
                        if ((objectX > pPlayerSprite.i_mainX + pPlayerSprite.i_width + SAFE_ZONE) || (objectX + p_Sprite.i_width + SAFE_ZONE < pPlayerSprite.i_mainX))
                        {
                            if ((i_GameTimer & 0x0F) == 0)
                            {
                                if (p_Sprite.i_RoadX < p_Sprite.i_width)
                                {
                                    p_Sprite.i_RoadX += 0x0280;
                                }
                                if (p_Sprite.i_RoadX > (ROAD_WIDTH - p_Sprite.i_width))
                                {
                                    p_Sprite.i_RoadX -= 0x0280;
                                }
                            }
                        }
                        else
                        {
                            if((objectX < pPlayerSprite.i_mainX) && (objectX + p_Sprite.i_width + SAFE_ZONE > pPlayerSprite.i_mainX))
                            {
                                if (p_Sprite.i_RoadX > 0 + p_Sprite.i_width / 2 - SAFE_ZONE)
                                {
                                    p_Sprite.i_RoadX -= 0x0280;
                                }
                            }

                            if((objectX  > pPlayerSprite.i_mainX) && (objectX < pPlayerSprite.i_mainX + pPlayerSprite.i_width + SAFE_ZONE))
                            {
                                if (p_Sprite.i_RoadX < (ROAD_WIDTH) - p_Sprite.i_width / 2 + SAFE_ZONE)
                                {
                                    p_Sprite.i_RoadX += 0x0280;
                                }
                            }
                        }
                    }
                    p_Sprite.i_ObjectState += p_Sprite.i_Speed;

                    int speed = SPEED_CAR1;
                    switch (p_Sprite.i_ObjectType)
                    {
                        default:
                        case SPRITE_CAR_1: speed = SPEED_CAR1;break;
                        case SPRITE_CAR_2: speed = SPEED_CAR2;break;
                        case SPRITE_CAR_3: speed = SPEED_CAR3;break;
                    }

                    int leftX = getRoad(p_Sprite.i_ObjectState >> 8);
                    int rightX = leftX + ROAD_WIDTH;



                    if ((p_Sprite.i_mainX < leftX) || (p_Sprite.i_mainX > rightX))
                    {
                        leftX = (speed >> 2);
                        if (p_Sprite.i_Speed > leftX)
                        {
                            p_Sprite.i_Speed -= DEC_VSPEED * 2;
                            if (p_Sprite.i_Speed < leftX) p_Sprite.i_Speed = leftX;
                        }
                    }
                      else
                           {
                               p_Sprite.i_Speed = Math.min(p_Sprite.i_Speed + INC_VSPEED, speed);
                           }


                    p_Sprite.setMainPointXY((getRoad(p_Sprite.i_ObjectState >> 8) + p_Sprite.i_RoadX), (i_Horizont) - (p_Sprite.i_ObjectState) );
                    if(p_Sprite.i_ObjectState > FINISH_LINE && (pPlayerSprite.i_ObjectState < I8_SCREEN_HEIGHT) )
                    {
                        p_Sprite.i_ObjectState -= FINISH_LINE;
                    }
                    else if(p_Sprite.i_ObjectState > (FINISH_LINE - I8_SCREEN_HEIGHT) && (pPlayerSprite.i_ObjectState < I8_SCREEN_HEIGHT) )
                    {
                        p_Sprite.i_ObjectState -= FINISH_LINE;
                    }
                    p_Sprite.setMainPointXY((getRoad(p_Sprite.i_ObjectState >> 8) + p_Sprite.i_RoadX), (i_Horizont) - (p_Sprite.i_ObjectState) );


                        if(p_Sprite.i_ObjectState > (i_Horizont + FAR_FAR_AWAY) || p_Sprite.i_ObjectState < (i_Horizont - I8_SCREEN_HEIGHT - FAR_FAR_AWAY) )
                        {
                            deactivateSprite(p_Sprite);
                        }
                    break;

                case SPRITE_EXPLOSION:
                case SPRITE_STONE_SMALL:
                case SPRITE_STONE_MEDIUM:
                case SPRITE_STONE_LARGE:
                    p_Sprite.setMainPointXY(p_Sprite.i_mainX, (i_Horizont) - (p_Sprite.i_ObjectState) );

                    if(p_Sprite.i_ObjectType > SPRITE_STONE_SMALL && p_Sprite.isCollided(pPlayerSprite))
                    {
                            deactivateSprite(p_Sprite);
                            pPlayerSprite.i_Speed = 0;
                            pPlayerSprite.i_Timer = REPAIR_TIMER;
                            m_pAbstractGameActionListener.processGameAction(GAMEACTION_PLAYER_CAR_HIT);
                    }

                    if(p_Sprite.i_ObjectState > FINISH_LINE && (pPlayerSprite.i_ObjectState < I8_SCREEN_HEIGHT) )
                    {
                        p_Sprite.i_ObjectState -= FINISH_LINE;
                    }
                    else if(p_Sprite.i_ObjectState > (FINISH_LINE - I8_SCREEN_HEIGHT) && (pPlayerSprite.i_ObjectState < I8_SCREEN_HEIGHT) )
                    {
                        p_Sprite.i_ObjectState -= FINISH_LINE;
                    }

                    if(p_Sprite.i_ObjectState < (i_Horizont - I8_SCREEN_HEIGHT) )
                    {
                         deactivateSprite(p_Sprite);
                    }
                    break;
                default:
                    break;
            }
        }
    }

    public void Car_AI(Sprite car)
    {
        CheckCar(car, pPlayerSprite);
        if(car.isCollided(pPlayerSprite))
        {
            ExplodeCar(car);
            pPlayerSprite.i_Speed = 0;
            pPlayerSprite.i_Timer = REPAIR_TIMER;
            addScore(SCORES_COLLISION);
            m_pAbstractGameActionListener.processGameAction(GAMEACTION_PLAYER_CAR_HIT);
        }
        else
        {
            for (int ix = 0; ix < MAX_SPRITES_NUMBER; ix++)
            {
                if(ap_Sprites[ix].lg_SpriteActive && (car != ap_Sprites[ix]))
                {
                    CheckCar(car, ap_Sprites[ix]);
                    if(car.isCollided(ap_Sprites[ix]))
                    {
                        switch(ap_Sprites[ix].i_ObjectType)
                        {
                            case SPRITE_CAR_1:
                            case SPRITE_CAR_2:
                            case SPRITE_CAR_3:
                                ExplodeCar(ap_Sprites[ix]);
                                m_pAbstractGameActionListener.processGameAction(GAMEACTION_CAR_COLLISION);
                                break;
                            case SPRITE_STONE_MEDIUM:
                                deactivateSprite(ap_Sprites[ix]);
                                m_pAbstractGameActionListener.processGameAction(GAMEACTION_CAR_COLLISION);
                                break;
                            case SPRITE_STONE_LARGE:
                                ExplodeCar(car);
                                m_pAbstractGameActionListener.processGameAction(GAMEACTION_CAR_COLLISION);
                                break;

                        }
                    }
                }
            }
        }
    }

    public void CheckCar(Sprite car, Sprite obj)
    {
        if (obj.lg_SpriteActive && (car != obj))
        {
            switch (obj.i_ObjectType)
            {
                case SPRITE_CAR:
                case SPRITE_CAR_1:
                case SPRITE_CAR_2:
                case SPRITE_CAR_3:
                    if (car.i_Speed > obj.i_Speed)
                    {
                        if ((car.i_ObjectState < obj.i_ObjectState) && (car.i_ObjectState > obj.i_ObjectState - obj.i_height * 2))
                        {
                            if (car.i_RoadX > obj.i_RoadX)
                            {
                                if (obj.i_RoadX + obj.i_width + SAFE_ZONE > car.i_RoadX)
                                {
                                    if (car.i_RoadX < (ROAD_WIDTH) - car.i_width + SAFE_ZONE)
                                    {
                                        car.i_RoadX += 0x0280;
                                        car.i_Speed = 15<<8;

                                    }
                                    else
                                    {
                                        car.i_Speed = 10<<8;
                                        car.i_Timer = WAIT_TIMER;
                                        m_pAbstractGameActionListener.processGameAction(GAMEACTION_CAR_BREAK);
                                    }
                                }
                            }
                            else
                            {
                                if (car.i_RoadX + car.i_width + SAFE_ZONE > obj.i_RoadX)
                                {
                                    if (car.i_RoadX > 0 - SAFE_ZONE)
                                    {
                                        car.i_RoadX -= 0x0280;
                                        car.i_Speed = 15<<8;
                                    }
                                    else
                                    {
                                        car.i_Speed = 10<<8;
                                        car.i_Timer = WAIT_TIMER;
                                        m_pAbstractGameActionListener.processGameAction(GAMEACTION_CAR_BREAK);
                                    }
                                }
                            }
                        }
                    }
                    break;
            }
        }
    }

    public void ExplodeCar(Sprite car)
    {
        activateSprite(car, SPRITE_EXPLOSION);
        m_pAbstractGameActionListener.processGameAction(GAMEACTION_EXPLOSION);
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
                case SPRITE_CAR_DEAD:
                    return false;
            }
        }
        switch(pPlayerSprite.i_ObjectType)
        {
            case SPRITE_CAR:
                if(pPlayerSprite.i_Timer > 0)
                {
                    pPlayerSprite.i_Timer--;
                    if(pPlayerSprite.i_Timer == 0)
                    {
                        m_pAbstractGameActionListener.processGameAction(GAMEACTION_PLAYER_CAR_READY);
                    }
                }
                else
                {
                    if(pPlayerSprite.i_Speed > 0)
                    {
                        if ((_buttonState & MoveObject.BUTTON_LEFT) != 0)
                        {
                            if(i_mx > (0x0000 + LEFT_MARGIN))
                            {
                                i_mx -= HSPEED;
                            }
                        }
                        else if ((_buttonState & MoveObject.BUTTON_RIGHT) != 0)
                        {
                            if(i_mx < (RACE_WIDTH - RIGHT_MARGIN))
                            {
                                i_mx += HSPEED;
                            }
                        }
                    }

                    if ((_buttonState & MoveObject.BUTTON_UP) != 0)
                    {
                        if((pPlayerSprite.i_Speed + INC_VSPEED) > MAX_VSPEED )
                        {
                            pPlayerSprite.i_Speed = MAX_VSPEED;
                        }
                        else
                        {
                            pPlayerSprite.i_Speed += INC_VSPEED;
                            m_pAbstractGameActionListener.processGameAction(GAMEACTION_PLAYER_CAR_SPEED_UP);
                        }
                    }
                    else if ((_buttonState & MoveObject.BUTTON_DOWN) != 0)
                    {
                        if((pPlayerSprite.i_Speed - DEC_VSPEED) < 0 )
                        {
                            pPlayerSprite.i_Speed = 0;
                            m_pAbstractGameActionListener.processGameAction(GAMEACTION_PLAYER_CAR_STOP);
                        }
                        else
                        {
                            pPlayerSprite.i_Speed -= DEC_VSPEED;
                            m_pAbstractGameActionListener.processGameAction(GAMEACTION_PLAYER_CAR_BRAKE);
                        }
                    }
                }
                    int leftX = getRoad(pPlayerSprite.i_ObjectState >> 8); 
                    int rightX = leftX + ROAD_WIDTH; 

                    if ((i_mx < leftX) || (i_mx > rightX))
                    {
                        if (pPlayerSprite.i_Speed > MAX_VSPEED / 4)
                        {
                            pPlayerSprite.i_Speed -= DEC_VSPEED * 2;
                            if (pPlayerSprite.i_Speed < MAX_VSPEED / 4) pPlayerSprite.i_Speed = MAX_VSPEED / 4;
                        }
                    }

                {
                   addScore(SCORES_RACING * ((pPlayerSprite.i_ObjectState & 0x1FFF) + pPlayerSprite.i_Speed) >> (5+8));
                }

                pPlayerSprite.i_ObjectState += pPlayerSprite.i_Speed;
                pPlayerSprite.setMainPointXY(i_mx, i_my);
                pPlayerSprite.i_RoadX = i_mx - getRoad(pPlayerSprite.i_ObjectState >> 8);

                i_Horizont = pPlayerSprite.i_ObjectState + pPlayerSprite.i_mainY;

                i_CameraX = pPlayerSprite.i_mainX;
                if((i_CameraX - CAMERA_WIDTH / 2) < (0x0000) ) i_CameraX = ((0x0000) + CAMERA_WIDTH / 2);
                if((i_CameraX + CAMERA_WIDTH / 2) > (192 << 8) ) i_CameraX = ((192 << 8) - CAMERA_WIDTH / 2);
                i_CameraL = i_CameraX - CAMERA_WIDTH / 2;
                i_CameraR = i_CameraX + CAMERA_WIDTH / 2;
                break;
            case SPRITE_CAR_DEAD:
                if (pPlayerSprite.i_Speed > MAX_VSPEED / 4)
                {
                    pPlayerSprite.i_Speed -= DEC_VSPEED * 2;
                }
               if (pPlayerSprite.i_Speed < MAX_VSPEED / 4) pPlayerSprite.i_Speed = 0;

                pPlayerSprite.i_ObjectState += pPlayerSprite.i_Speed;
                pPlayerSprite.setMainPointXY(i_mx, i_my);
                pPlayerSprite.i_RoadX = i_mx - getRoad(pPlayerSprite.i_ObjectState >> 8);

                i_Horizont = pPlayerSprite.i_ObjectState + pPlayerSprite.i_mainY;
                break;
        }
        return true;
    }


    private void generateStone()
    {
        if((i_LastStoneTime + i_GenerateStoneDelay) < i_GameTimer )
        {
            i_LastStoneTime = i_GameTimer;

            Sprite p_emptySprite;
            {
                p_emptySprite = getInactiveStone();
                if (p_emptySprite != null)
                {
                    int x, y = 0 ,range,type,halfwidth;

                    switch(getRandomInt(6) / 2)
                    {
                        default:
                        case 0:  type = SPRITE_STONE_SMALL; break;
                        case 1:  type = SPRITE_STONE_MEDIUM; break;
                        case 2:  type = SPRITE_STONE_LARGE; break;
                    }

                    activateSprite(p_emptySprite, type);
                    p_emptySprite.lg_SpriteActive = true;

                    p_emptySprite.i_Speed = 0;
                    p_emptySprite.i_Timer = 0;
                    p_emptySprite.i_ObjectState = i_Horizont + p_emptySprite.i_height;

                    halfwidth = p_emptySprite.i_width>>1;

                    if ((getRandomInt(4) & 0x01) == 0) 
                    {
                        x = 0x1000 + halfwidth;
                        range = getRoad(p_emptySprite.i_ObjectState >> 8) - x - p_emptySprite.i_width;
                    }
                    else
                    {
                        x = getRoad(p_emptySprite.i_ObjectState >> 8) + ROAD_WIDTH + p_emptySprite.i_width;
                        range = RACE_WIDTH - x - halfwidth -0x1000;
                    }

                    if(range > 0)
                    {
                         x += getRandomInt(range);
                         p_emptySprite.i_RoadX = x;
                         p_emptySprite.setMainPointXY(x, y);
                         return;
                    }
                    p_emptySprite.lg_SpriteActive = false;
                }
            }
        }
    }


    private void generateSprite()
    {
        if((i_LastTime + i_GenerateDelay) < i_GameTimer )
        {
            i_LastTime = i_GameTimer;

            Sprite p_emptySprite;

            p_emptySprite = getInactiveSprite();
            if (p_emptySprite != null)
            {
                int y = 0;
                int x = 0;
                int type = SPRITE_CAR_1;
                int RoadX = ROAD_WIDTH / 2;
                int speed = SPEED_CAR1;

                switch(getRandomInt(6) / 2)
                {
                    default:
                    case 0:
                        type = SPRITE_CAR_1;
                        speed = SPEED_CAR1;
                        break;
                    case 1:
                        type = SPRITE_CAR_2;
                        speed = SPEED_CAR2;
                        break;
                    case 2:
                        type = SPRITE_CAR_3;
                        speed = SPEED_CAR3;
                        break;
                }

                activateSprite(p_emptySprite, type);
                p_emptySprite.lg_SpriteActive = true;

                RoadX = p_emptySprite.i_width / 2 + (getRandomInt((ROAD_WIDTH - p_emptySprite.i_width) >> 8) << 8);

                p_emptySprite.i_RoadX = RoadX;
                p_emptySprite.i_Speed = speed;
                p_emptySprite.i_Timer = 0;
                if ((getRandomInt(4) & 0x01) == 0)
                {
                    p_emptySprite.i_ObjectState = i_Horizont + p_emptySprite.i_height + (getRandomInt(4) << 10);
                }
                else
                {
                    p_emptySprite.i_ObjectState = (i_Horizont - I8_SCREEN_HEIGHT) - p_emptySprite.i_height - (getRandomInt(4) << 10);
                }


                p_emptySprite.setMainPointXY(x, y);
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
                    i_RaceTimer = LEVEL_EASY_RACETIME;
                }
                break;
            case LEVEL_NORMAL:
                {
                    i_timedelay = LEVEL_NORMAL_TIMEDELAY;
                    i_playerAttemptions = LEVEL_NORMAL_ATTEMPTIONS;
                    i_GenerateDelay = LEVEL_NORMAL_GENERATION;
                    i_RaceTimer = LEVEL_NORMAL_RACETIME;
                }
                break;
            case LEVEL_HARD:
                {
                    i_timedelay = LEVEL_HARD_TIMEDELAY;
                    i_playerAttemptions = LEVEL_HARD_ATTEMPTIONS;
                    i_GenerateDelay = LEVEL_HARD_GENERATION;
                    i_RaceTimer = LEVEL_HARD_RACETIME;
                }
                break;
        }

        i_GameTimer = 0;
        i_LastTime = i_GameTimer;
        i_LastStoneTime = i_GameTimer;
        i_GenerateStoneDelay = STONE_DELAY;

        activateSprite(pPlayerSprite, SPRITE_CAR);
        pPlayerSprite.setMainPointXY(I8_SCREEN_WIDTH / 2, INIT_PLAYER_Y);
        pPlayerSprite.i_ObjectState = 0;
        pPlayerSprite.i_Timer = 0;
        pPlayerSprite.i_Speed = 0;
        pPlayerSprite.i_RoadX = ROAD_WIDTH / 2;

        RegenerateViews();

        i_Horizont = pPlayerSprite.i_ObjectState + pPlayerSprite.i_mainY;

        i_CameraX = pPlayerSprite.i_mainX;
        if((i_CameraX - CAMERA_WIDTH / 2) < (0x0000) ) i_CameraX = ((0x0000) + CAMERA_WIDTH / 2);
        if((i_CameraX + CAMERA_WIDTH / 2) > (192 << 8) ) i_CameraX = ((192 << 8) - CAMERA_WIDTH / 2);
        i_CameraL = i_CameraX - CAMERA_WIDTH / 2;
        i_CameraR = i_CameraX + CAMERA_WIDTH / 2;


        try
        {

            Runtime.getRuntime().gc();

InputStream file = getClass().getResourceAsStream("/res/road_n.bin");

            int a = file.read();
            a = file.read();

            for(int i = 4095; i>=0;i--)
            {
                ab_Road[i] = (byte)file.read();
            }

            file.close();

        }
        catch(Exception e)
        {

        }


        return true;
    }

    public void resumeGameAfterPlayerLostOrPaused()
    {
        super.resumeGameAfterPlayerLostOrPaused();

        deinitState();
        initState();

        i_GameTimer = 0;
        i_LastTime = i_GameTimer;
        i_LastStoneTime = i_GameTimer;

        activateSprite(pPlayerSprite, SPRITE_CAR);
        pPlayerSprite.setMainPointXY(I8_SCREEN_WIDTH / 2, INIT_PLAYER_Y);

        pPlayerSprite.i_Timer = 0;

        i_Horizont = pPlayerSprite.i_ObjectState + pPlayerSprite.i_mainY;

        i_CameraX = pPlayerSprite.i_mainX;
        if((i_CameraX - CAMERA_WIDTH / 2) < (0x0000) ) i_CameraX = ((0x0000) + CAMERA_WIDTH / 2);
        if((i_CameraX + CAMERA_WIDTH / 2) > (192 << 8) ) i_CameraX = ((192 << 8) - CAMERA_WIDTH / 2);
        i_CameraL = i_CameraX - CAMERA_WIDTH / 2;
        i_CameraR = i_CameraX + CAMERA_WIDTH / 2;

        switch(getGameDifficultLevel())
        {
            case LEVEL_EASY:
                i_RaceTimer = LEVEL_EASY_RACETIME;
                break;
            case LEVEL_NORMAL:
                i_RaceTimer = LEVEL_NORMAL_RACETIME;
                break;
            case LEVEL_HARD:
                i_RaceTimer = LEVEL_HARD_RACETIME;
                break;
        }

    }

    public String getGameTextID()
    {
        return "MOBILECARTING";
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
        return (MAX_SPRITES_NUMBER+1)*(Sprite.DATASIZE_BYTES+1)+44+(MAX_VIEW_ARRAY * 2);
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

        for(int i = 0; i < MAX_VIEW_ARRAY; i++)
        {
            _outputStream.writeByte(ab_LeftViews[i]);
            _outputStream.writeByte(ab_RightViews[i]);
        }

        _outputStream.writeInt(i_playerAttemptions);
        _outputStream.writeInt(i_GameTimer);
        _outputStream.writeInt(i_LastTime);
        _outputStream.writeInt(i_GenerateDelay);
        _outputStream.writeInt(i_LastStoneTime);

        _outputStream.writeInt(i_RaceTimer);
        _outputStream.writeInt(i_Horizont);
        _outputStream.writeInt(i_HSpeed);
        _outputStream.writeInt(i_CameraX);
        _outputStream.writeInt(i_CameraL);
        _outputStream.writeInt(i_CameraR);

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

        for(int i = 0; i < MAX_VIEW_ARRAY; i++)
        {
            ab_LeftViews[i] = _inputStream.readByte();
            ab_RightViews[i] = _inputStream.readByte();
        }

        i_playerAttemptions = _inputStream.readInt();
        i_GameTimer = _inputStream.readInt();
        i_LastTime = _inputStream.readInt();
        i_GenerateDelay = _inputStream.readInt();
        i_LastStoneTime = _inputStream.readInt();

        i_RaceTimer = _inputStream.readInt();
        i_Horizont = _inputStream.readInt();
        i_HSpeed = _inputStream.readInt();
        i_CameraX = _inputStream.readInt();
        i_CameraL = _inputStream.readInt();
        i_CameraR = _inputStream.readInt();
    }

    public int nextGameStep()
    {
        if(m_iGameState != GAMEWORLDSTATE_PLAYED)
        {
           processSprites();
           return m_iGameState;
        }

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

        generateSprite();
        generateStone();
        processSprites();

        i_GameTimer++;

        if(pPlayerSprite.i_ObjectState >= FINISH_LINE)
        {
            pPlayerSprite.i_ObjectState -= FINISH_LINE;
            i_Horizont = pPlayerSprite.i_ObjectState + pPlayerSprite.i_mainY;

            if(i_RaceTimer > 0)
            {
                addScore(i_RaceTimer);
                m_pAbstractGameActionListener.processGameAction(GAMEACTION_GOOD_TIME);

                switch(getGameDifficultLevel())
                {
                    case LEVEL_EASY:
                        i_RaceTimer = LEVEL_EASY_RACETIME;
                        break;
                    case LEVEL_NORMAL:
                        i_RaceTimer = LEVEL_NORMAL_RACETIME;
                        break;
                    case LEVEL_HARD:
                        i_RaceTimer = LEVEL_HARD_RACETIME;
                        break;
                }
            }
            else
            {
                if(pPlayerSprite.i_ObjectType != SPRITE_CAR_DEAD)
                {
                    activateSprite(pPlayerSprite, SPRITE_CAR_DEAD);
                    m_pAbstractGameActionListener.processGameAction(GAMEACTION_BAD_TIME);
                }
            }

            RegenerateViews();
        }


        if(i_RaceTimer > 0)
        {
            i_RaceTimer--;
            if(i_RaceTimer == 0)
            {
                m_pAbstractGameActionListener.processGameAction(GAMEACTION_TIME_OUT);
            }
        }
        else
        {
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
