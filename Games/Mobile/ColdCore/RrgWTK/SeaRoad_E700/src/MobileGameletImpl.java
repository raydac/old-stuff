
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.DataInputStream;


public class MobileGameletImpl extends MobileGamelet
{
    public static final int RIGHT_MARGIN = (16 << 8);
    public static final int LEFT_MARGIN = (16 << 8);
    public static final int UP_MARGIN = (12 << 8);
    public static final int DOWN_MARGIN = (12 << 8);

    public static final int INIT_TIME_LIMIT = 200;

    public static final int INIT_ANGLE = 16; 

    public static final int MAX_ANGLE = 28; 
    public static final int MIN_ANGLE = 4; 

    public static final int STEP_ANGLE = 4; 

    public static final int INIT_PLAYER_SPEED = (2 << 8);
    public static final int INCREASE_SPEED = (0x0080);
    public static final int DECREASE_SPEED = (0x0080);
    public static final int MAX_PLAYER_SPEED = (2 << 8);
    public static final int MIN_PLAYER_SPEED = (1 << 8);

    public static final int SEA_SPEED = (2 << 8);

    public static final int SPEED_SOUTH_BOAT = (2 << 8);
    public static final int SPEED_SOUTH_TUGBOAT = (2 << 8);
    public static final int SPEED_SOUTH_SPEEDBOAT = (3 << 8);
    public static final int SPEED_SOUTH_BARGE = (2 << 8);
    public static final int SPEED_NORTH_BARGE = -(1 << 8);

    public static final int BOATS_X = (8 << 8);
    public static final int BOATS_Y = (0 << 8);

    public static final int BOAT_SIZE = (16 << 8);  

    public static final int GATE_SIZE = (32 << 8);

    public static final int BUOY_MOVE_SPEED = (1 << 8);


    public static final int SPLASHES_GENERATE_MASK = 1; 

    public static final int POSITION_MASK = 0x003F;

    public static final int STEP_WAVE_MASK = 0x001F;
    public static final int LIMIT_BOATFREQ = 17;    

    private static int I8_SCREEN_WIDTH;

    private static int I8_SCREEN_HEIGHT;

    public MoveObject p_MoveObject;

    public static final int GAMEACTION_BOAT_APPEARING           = 0;
    public static final int GAMEACTION_BOAT_READY               = 1;
    public static final int GAMEACTION_BOAT_DEAD                = 2;
    public static final int GAMEACTION_BUOY_HIT                 = 3;
    public static final int GAMEACTION_GATE_OK                  = 4;
    public static final int GAMEACTION_GATE_GENERATED           = 5;
    public static final int GAMEACTION_BOAT_GENERATED           = 6;
    public static final int GAMEACTION_SPLASH_GENERATED         = 7;


    private int i_timedelay;

    public int i_playerAttemptions;

    public int i_GameTimer;

    public int i_tripDistance;
    public int i_LastTime;
    public int i_GenerateDelay;

    public int i_PlayerSpeed;

    public int i_PlayerTimeLimit;

    public int i_LastGateX;
    public int i_Range;

    public static final int LEVEL_EASY = 0;

    private static final int LEVEL_EASY_ATTEMPTIONS = 4;

    private static final int LEVEL_EASY_GENERATION = 35;
    private static final int LEVEL_EASY_RANGE = 80;

    private static final int LEVEL_EASY_TIMEDELAY = 100;




    public static final int LEVEL_NORMAL = 1;

    private static final int LEVEL_NORMAL_ATTEMPTIONS = 3;

    private static final int LEVEL_NORMAL_GENERATION = 30;
    private static final int LEVEL_NORMAL_RANGE = 96;

    private static final int LEVEL_NORMAL_TIMEDELAY = 85;

    public static final int LEVEL_HARD = 2;

    private static final int LEVEL_HARD_ATTEMPTIONS = 2;

    private static final int LEVEL_HARD_GENERATION = 25;
    private static final int LEVEL_HARD_RANGE = 110;

    private static final int LEVEL_HARD_TIMEDELAY = 70;



    public Sprite pPlayerSprite;

    private static final int SPRITEDATALENGTH = 10;

    public static final int SPRITE_BOAT = 0;
    public static final int SPRITE_BOAT_APPEAR = 1;
    public static final int SPRITE_BOAT_DEAD = 2;
    public static final int SPRITE_SOUTH_BOAT = 3;
    public static final int SPRITE_SOUTH_BARGE = 4;
    public static final int SPRITE_NORTH_BARGE = 5;
    public static final int SPRITE_SOUTH_TUGBOAT = 6;
    public static final int SPRITE_SOUTH_SPEEDBOAT = 7;
    public static final int SPRITE_BUOY_LEFT = 8;
    public static final int SPRITE_BUOY_RIGHT = 9;
    public static final int SPRITE_BUOY_CENTER = 10;
    public static final int SPRITE_SPLASH = 11;

    private static final int [] ai_SpriteParameters = new int[]
    {
        0x1800, 0x1800, -0x001, 0x0000, 0x0A00, 0x0A00, 1, 1, Sprite.SPRITE_ALIGN_CENTER, Sprite.ANIMATION_CYCLIC,
        0x1800, 0x1800, -0x001, 0x0000, 0x0A00, 0x1800, 1, 1, Sprite.SPRITE_ALIGN_CENTER, Sprite.ANIMATION_CYCLIC,
        0x1800, 0x1800, -0x001, 0x0000, 0x0000, 0x0000, 8, 2, Sprite.SPRITE_ALIGN_CENTER, Sprite.ANIMATION_FROZEN,
        0x0C00, 0x1800, -0x001, 0x0000, 0x1000, 0x1A00, 2, 1, Sprite.SPRITE_ALIGN_CENTER, Sprite.ANIMATION_CYCLIC,
        0x0E00, 0x2800, -0x001, 0x0000, 0x1200, 0x2A00, 2, 1, Sprite.SPRITE_ALIGN_CENTER, Sprite.ANIMATION_CYCLIC,
        0x0E00, 0x2800, -0x001, 0x0000, 0x1200, 0x2A00, 2, 2, Sprite.SPRITE_ALIGN_CENTER, Sprite.ANIMATION_CYCLIC,
        0x0E00, 0x1A00, -0x001, 0x0000, 0x1200, 0x1C00, 2, 1, Sprite.SPRITE_ALIGN_CENTER, Sprite.ANIMATION_CYCLIC,
        0x0C00, 0x2000, -0x001, 0x0000, 0x1000, 0x2200, 2, 1, Sprite.SPRITE_ALIGN_CENTER, Sprite.ANIMATION_CYCLIC,
        0x0400, 0x0400, -0x001, 0x0000, 0x0600, 0x0600, 8, 4, Sprite.SPRITE_ALIGN_CENTER, Sprite.ANIMATION_CYCLIC,
        0x0400, 0x0400, -0x001, 0x0000, 0x0600, 0x0600, 8, 4, Sprite.SPRITE_ALIGN_CENTER, Sprite.ANIMATION_CYCLIC,
        0x1800, 0x0200, -0x001, 0x0000, 0x1800, 0x0200, 1, 1, Sprite.SPRITE_ALIGN_CENTER, Sprite.ANIMATION_CYCLIC,
        0x1000, 0x1000, -0x001, 0x0000, 0x1000, 0x1000, 4, 3, Sprite.SPRITE_ALIGN_CENTER, Sprite.ANIMATION_FROZEN,
    };


    public static final int MAX_LOCATIONS = 8;

    public static final int MAX_SPLASHES = ai_SpriteParameters[SPRITE_SPLASH*SPRITEDATALENGTH + 6] * ai_SpriteParameters[SPRITE_SPLASH*SPRITEDATALENGTH + 7] / (SPLASHES_GENERATE_MASK +1);

    public static final int MAX_GATES = (160 + POSITION_MASK) / (POSITION_MASK + 1);

    public static final int MAX_GATES_SPRITES = MAX_GATES * 3;

    public static final int MAX_SPRITES_NUMBER = MAX_LOCATIONS + MAX_SPLASHES + MAX_GATES_SPRITES;

    public Sprite[] ap_Sprites;


    private Sprite findLocation(int n)
    {
        for(int i=n; i<MAX_LOCATIONS; i++)
        {
            if(!ap_Sprites[i].lg_SpriteActive)
            {
                return ap_Sprites[i];
            }
        }
        for(int i=0; i<=n; i++)
        {
            if(!ap_Sprites[i].lg_SpriteActive)
            {
                return ap_Sprites[i];
            }
        }
        return null;
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
        for (int li = MAX_LOCATIONS + MAX_SPLASHES; li < MAX_SPRITES_NUMBER; li++)
        {
            if (!ap_Sprites[li].lg_SpriteActive) return ap_Sprites[li];
        }
        return null;
    }
    private Sprite getInactiveSplash()
    {
        for (int li = MAX_LOCATIONS; li < MAX_LOCATIONS + MAX_SPLASHES; li++)
        {
            if (!ap_Sprites[li].lg_SpriteActive) return ap_Sprites[li];
        }
        return null;
    }

    private void processSprites()
    {

        i_tripDistance = (i_tripDistance + SEA_SPEED) & ((64<<8)-1);

        for (int li = 0; li < MAX_SPRITES_NUMBER; li++)
        {
            Sprite p_Sprite = ap_Sprites[li];
            if (!p_Sprite.lg_SpriteActive) continue;
            if (p_Sprite.processAnimation())
            {
                switch (p_Sprite.i_ObjectType)
                {
                    case SPRITE_SPLASH:
                        deactivateSprite(p_Sprite);
                        break;
                }
            }
            switch (p_Sprite.i_ObjectType)
            {
                case SPRITE_SOUTH_BOAT:
                case SPRITE_SOUTH_TUGBOAT:
                case SPRITE_SOUTH_SPEEDBOAT:
                case SPRITE_SOUTH_BARGE:
                case SPRITE_NORTH_BARGE:
                    p_Sprite.setMainPointXY(p_Sprite.i_mainX, p_Sprite.i_mainY + p_Sprite.i_ObjectState + SEA_SPEED);

                    if(p_Sprite.isCollided(pPlayerSprite) && pPlayerSprite.i_ObjectType == SPRITE_BOAT)
                    {
                        activateSprite(pPlayerSprite, SPRITE_BOAT_DEAD);
                        m_pAbstractGameActionListener.processGameAction(GAMEACTION_BOAT_DEAD);
                    }

                    if((p_Sprite.i_mainY - p_Sprite.i_height) > I8_SCREEN_HEIGHT)
                    {
                        deactivateSprite(p_Sprite);
                    }
                    break;
                case SPRITE_BUOY_LEFT:
                case SPRITE_BUOY_RIGHT:
                    boolean b_moveback = true;

                    for (int idx = 0; idx < MAX_LOCATIONS; idx++)
                    {
                        Sprite p_Boat = ap_Sprites[idx];
                        if (!p_Boat.lg_SpriteActive) continue;
                        switch (p_Boat.i_ObjectType)
                        {
                            case SPRITE_SOUTH_BOAT:
                            case SPRITE_NORTH_BARGE:
                            case SPRITE_SOUTH_BARGE:
                            case SPRITE_SOUTH_TUGBOAT:
                            case SPRITE_SOUTH_SPEEDBOAT:
                                if(p_Sprite.isCollided(p_Boat))
                                {
                                    if(p_Sprite.i_mainX < p_Boat.i_mainX)
                                    {
                                        p_Sprite.setMainPointXY(p_Sprite.i_mainX - BUOY_MOVE_SPEED, p_Sprite.i_mainY);
                                    }
                                    else
                                    {
                                        p_Sprite.setMainPointXY(p_Sprite.i_mainX + BUOY_MOVE_SPEED, p_Sprite.i_mainY);
                                    }
                                    b_moveback = false;
                                }
                                break;
                        }
                    }
                    if(p_Sprite.isCollided(pPlayerSprite))
                    {
                        if(p_Sprite.i_mainX < pPlayerSprite.i_mainX)
                        {
                            p_Sprite.setMainPointXY(p_Sprite.i_mainX - BUOY_MOVE_SPEED, p_Sprite.i_mainY);
                        }
                        else
                        {
                            p_Sprite.setMainPointXY(p_Sprite.i_mainX + BUOY_MOVE_SPEED, p_Sprite.i_mainY);
                        }
                        b_moveback = false;
                    }

                    if(b_moveback)
                    {
                        int destination = (p_Sprite.i_ObjectState - GATE_SIZE / 2);
                        if(p_Sprite.i_ObjectType == SPRITE_BUOY_RIGHT)
                        {
                            destination = (p_Sprite.i_ObjectState + GATE_SIZE / 2);
                        }

                        if( p_Sprite.i_mainX != destination )
                        {
                            if(p_Sprite.i_mainX > destination)
                            {
                                p_Sprite.setMainPointXY(p_Sprite.i_mainX - BUOY_MOVE_SPEED, p_Sprite.i_mainY);
                            }
                            else
                            {
                                p_Sprite.setMainPointXY(p_Sprite.i_mainX + BUOY_MOVE_SPEED, p_Sprite.i_mainY);
                            }
                        }
                    }
                    else
                    {
                        m_pAbstractGameActionListener.processGameAction(GAMEACTION_BUOY_HIT);
                    }

                    p_Sprite.setMainPointXY(p_Sprite.i_mainX, p_Sprite.i_mainY + SEA_SPEED);
                    if((p_Sprite.i_mainY - p_Sprite.i_height) > I8_SCREEN_HEIGHT)
                    {
                        deactivateSprite(p_Sprite);
                    }
                    break;
                case SPRITE_BUOY_CENTER:
                    if(p_Sprite.isCollided(pPlayerSprite) && pPlayerSprite.i_ObjectType != SPRITE_BOAT_DEAD)
                    {
                        int gate_y = p_Sprite.i_mainY;
                        int left_x = p_Sprite.i_mainX;
                        int right_x = p_Sprite.i_mainX;

                        for (int idx = MAX_LOCATIONS+MAX_SPLASHES; idx < MAX_SPRITES_NUMBER; idx++)
                        {
                            Sprite p_obj = ap_Sprites[idx];
                            if (!p_obj.lg_SpriteActive) continue;
                            switch (p_obj.i_ObjectType)
                            {
                                case SPRITE_BUOY_LEFT:
                                    if( (p_obj.i_mainY - p_obj.i_height * 2) < gate_y && gate_y < (p_obj.i_mainY + p_obj.i_height * 2) )
                                    {
                                        left_x = p_obj.i_mainX;
                                    }
                                    break;
                                case SPRITE_BUOY_RIGHT:
                                    if( (p_obj.i_mainY - p_obj.i_height * 2) < gate_y && gate_y < (p_obj.i_mainY + p_obj.i_height * 2) )
                                    {
                                        right_x = p_obj.i_mainX;
                                    }
                                    break;
                            }
                        }
                        if(pPlayerSprite.i_mainX > left_x && pPlayerSprite.i_mainX < right_x)
                        {
                            m_pAbstractGameActionListener.processGameAction(GAMEACTION_GATE_OK);
                            deactivateSprite(p_Sprite);
                            addScore(i_PlayerTimeLimit >> 3);  
                            i_PlayerTimeLimit = INIT_TIME_LIMIT;
                            break;
                        }
                    }

                    p_Sprite.setMainPointXY(p_Sprite.i_mainX, p_Sprite.i_mainY + SEA_SPEED);
                    if((p_Sprite.i_mainY - p_Sprite.i_height) > I8_SCREEN_HEIGHT)
                    {
                        deactivateSprite(p_Sprite);
                    }
                    break;
                case SPRITE_SPLASH:
                    p_Sprite.setMainPointXY(p_Sprite.i_mainX, p_Sprite.i_mainY + p_Sprite.i_ObjectState);
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
                case SPRITE_BOAT_DEAD:
                    return false;
            }
        }
        switch(pPlayerSprite.i_ObjectType)
        {
            case SPRITE_BOAT:
                int speed = i_PlayerSpeed;
                int angle = pPlayerSprite.i_ObjectState & 63;

                i_mx += ( xCoSine(speed + SEA_SPEED, angle) );


                if ((_buttonState & MoveObject.BUTTON_LEFT) != 0)
                {
                    if(i_mx > (0x0000 + LEFT_MARGIN))
                    {
                        pPlayerSprite.i_ObjectState += STEP_ANGLE;
                        if(pPlayerSprite.i_ObjectState > MAX_ANGLE)
                        {
                            pPlayerSprite.i_ObjectState = MAX_ANGLE;
                        }
                    }
                }
                else if ((_buttonState & MoveObject.BUTTON_RIGHT) != 0)
                {
                    if(i_mx < (I8_SCREEN_WIDTH - RIGHT_MARGIN))
                    {
                        pPlayerSprite.i_ObjectState -= STEP_ANGLE;
                        if(pPlayerSprite.i_ObjectState < MIN_ANGLE)
                        {
                            pPlayerSprite.i_ObjectState = MIN_ANGLE;
                        }
                    }
                }

                if ((_buttonState & MoveObject.BUTTON_UP) != 0)
                {
                    i_my -= ( xSine(speed, angle) );
                    if(i_my > (0x0000 + UP_MARGIN))
                    {
                        i_PlayerSpeed += INCREASE_SPEED;
                        if(i_PlayerSpeed >= MAX_PLAYER_SPEED)
                        {
                            i_PlayerSpeed = MAX_PLAYER_SPEED;
                        }
                    }
                    else
                    {
                        i_my = pPlayerSprite.i_mainY;
                    }
                }
                else
                {
                    i_my = pPlayerSprite.i_mainY;

                    if ((_buttonState & MoveObject.BUTTON_DOWN) != 0)
                    {
                        i_my -= ( xSine(-speed, angle) );
                        if(i_my < (I8_SCREEN_HEIGHT - DOWN_MARGIN))
                        {
                            i_PlayerSpeed -= DECREASE_SPEED;
                            if(i_PlayerSpeed <= MIN_PLAYER_SPEED)
                            {
                                i_PlayerSpeed = MIN_PLAYER_SPEED;
                            }
                        }
                        else
                        {
                            i_my = pPlayerSprite.i_mainY;
                        }
                    }
                    else
                    {
                        i_my = pPlayerSprite.i_mainY;
                    }
                }



                if( (i_my < (0x0000 + UP_MARGIN)) || (i_my > (I8_SCREEN_HEIGHT - DOWN_MARGIN)) )
                {
                    i_my = pPlayerSprite.i_mainY;
                }
                if(i_mx < (0x0000 + LEFT_MARGIN))
                {
                    if(pPlayerSprite.i_ObjectState > INIT_ANGLE)
                    {
                        pPlayerSprite.i_ObjectState -= STEP_ANGLE;
                    }
                }
                if(i_mx > (I8_SCREEN_WIDTH - RIGHT_MARGIN))
                {
                    if(pPlayerSprite.i_ObjectState < INIT_ANGLE)
                    {
                        pPlayerSprite.i_ObjectState += STEP_ANGLE;
                    }
                }


                pPlayerSprite.setMainPointXY(i_mx, i_my);
                break;
            case SPRITE_BOAT_APPEAR:
                if(i_my < I8_SCREEN_HEIGHT + (pPlayerSprite.i_height >> 1))
                {
                    m_pAbstractGameActionListener.processGameAction(GAMEACTION_BOAT_APPEARING);
                }

                pPlayerSprite.setMainPointXY(i_mx, i_my - i_PlayerSpeed);

                if(i_my < (I8_SCREEN_HEIGHT - DOWN_MARGIN))
                {
                    activateSprite(pPlayerSprite, SPRITE_BOAT);
                    m_pAbstractGameActionListener.processGameAction(GAMEACTION_BOAT_READY);
                }
                break;
        }

        generateSplashes(pPlayerSprite);

        if(i_PlayerTimeLimit > 0)
        {
            i_PlayerTimeLimit--;
        }
        else
        {
            if(pPlayerSprite.i_ObjectType != SPRITE_BOAT_DEAD)
            {
                activateSprite(pPlayerSprite, SPRITE_BOAT_DEAD);
                m_pAbstractGameActionListener.processGameAction(GAMEACTION_BOAT_DEAD);
            }
        }
        return true;
    }


    private void generateGate()
    {
        if((i_GameTimer & POSITION_MASK) == 0)
        {
            int y = -0x1000;
            int x = BOATS_X + getRandomInt(7) * BOAT_SIZE;

            int offset = ((0 - (i_Range / 2) + getRandomInt(i_Range)) << 8);

            x = i_LastGateX + offset;

            if(x > (I8_SCREEN_WIDTH - RIGHT_MARGIN))
            {
                x = i_LastGateX - offset;
            }
            if(x < (0x0000 + LEFT_MARGIN))
            {
                x = i_LastGateX - offset;
            }

            if(x < GATE_SIZE / 2)
            {
                x = GATE_SIZE / 2;
            }
            if(x > (I8_SCREEN_WIDTH - GATE_SIZE / 2))
            {
                x = I8_SCREEN_WIDTH - GATE_SIZE / 2;
            }


            i_LastGateX = x;

            Sprite p_emptySprite = getInactiveSprite();
            if (p_emptySprite != null)
            {
                activateSprite(p_emptySprite, SPRITE_BUOY_LEFT);
                p_emptySprite.lg_SpriteActive = true;
                p_emptySprite.setMainPointXY(x - GATE_SIZE / 2, y);
                p_emptySprite.i_ObjectState = x;
            }
            p_emptySprite = getInactiveSprite();
            if (p_emptySprite != null)
            {
                activateSprite(p_emptySprite, SPRITE_BUOY_RIGHT);
                p_emptySprite.lg_SpriteActive = true;
                p_emptySprite.setMainPointXY(x + GATE_SIZE / 2, y);
                p_emptySprite.i_ObjectState = x;
            }
            p_emptySprite = getInactiveSprite();
            if (p_emptySprite != null)
            {
                activateSprite(p_emptySprite, SPRITE_BUOY_CENTER);
                p_emptySprite.lg_SpriteActive = true;
                p_emptySprite.setMainPointXY(x, y);
                p_emptySprite.i_ObjectState = x;
            }
            m_pAbstractGameActionListener.processGameAction(GAMEACTION_GATE_GENERATED);
        }
    }


    private void generateSprite()
    {
        if((i_LastTime + i_GenerateDelay) < i_GameTimer )
        {
            i_LastTime = i_GameTimer;
            Sprite p_emptySprite = findLocation(getRandomInt(7));
            if (p_emptySprite != null)
            {
                int y = BOATS_Y;
                int x = BOATS_X + p_emptySprite.i_spriteID * BOAT_SIZE;
                int type = SPRITE_SOUTH_BOAT;
                int speed = 0;

                switch(getRandomInt(5))
                {
                    default:
                    case 0:
                        type = SPRITE_SOUTH_BOAT;
                        speed = SPEED_SOUTH_BOAT;
                        break;
                    case 1:
                        type = SPRITE_SOUTH_BARGE;
                        speed = SPEED_SOUTH_BARGE;
                        break;
                    case 2:
                        type = SPRITE_NORTH_BARGE;
                        speed = SPEED_NORTH_BARGE;
                        break;
                    case 3:
                        type = SPRITE_SOUTH_TUGBOAT;
                        speed = SPEED_SOUTH_TUGBOAT;
                        break;
                    case 4:
                        type = SPRITE_SOUTH_SPEEDBOAT;
                        speed = SPEED_SOUTH_SPEEDBOAT;
                        break;
                }

                activateSprite(p_emptySprite, type);
                p_emptySprite.lg_SpriteActive = true;
                p_emptySprite.setMainPointXY(x, y - p_emptySprite.i_height);
                p_emptySprite.i_ObjectState = speed;
                m_pAbstractGameActionListener.processGameAction(GAMEACTION_BOAT_GENERATED);
            }
        }
    }

    private void generateSplashes(Sprite obj)
    {
        if( pPlayerSprite.i_ObjectType == SPRITE_BOAT_DEAD || (i_GameTimer & SPLASHES_GENERATE_MASK) != 0)  return;


        int x = obj.i_mainX;
        int y = obj.i_mainY + 0x400;
        int type = SPRITE_SPLASH;
        int speed = SEA_SPEED;

        {
            Sprite p_emptySprite = getInactiveSplash();
            if (p_emptySprite != null)
            {
                x = obj.i_mainX;
                activateSprite(p_emptySprite, type);
                p_emptySprite.lg_SpriteActive = true;
                p_emptySprite.setMainPointXY(x, y);
                p_emptySprite.i_ObjectState = speed;
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
                    i_Range = LEVEL_EASY_RANGE;
                }
                break;
            case LEVEL_NORMAL:
                {
                    i_timedelay = LEVEL_NORMAL_TIMEDELAY;
                    i_playerAttemptions = LEVEL_NORMAL_ATTEMPTIONS;
                    i_GenerateDelay = LEVEL_NORMAL_GENERATION;
                    i_Range = LEVEL_NORMAL_RANGE;
                }
                break;
            case LEVEL_HARD:
                {
                    i_timedelay = LEVEL_HARD_TIMEDELAY;
                    i_playerAttemptions = LEVEL_HARD_ATTEMPTIONS;
                    i_GenerateDelay = LEVEL_HARD_GENERATION;
                    i_Range = LEVEL_HARD_RANGE;
                }
                break;
        }

        i_GameTimer = 0;
        i_LastTime = i_GameTimer;

        i_LastGateX = I8_SCREEN_WIDTH / 2;

        i_PlayerSpeed = INIT_PLAYER_SPEED;
        i_PlayerTimeLimit = INIT_TIME_LIMIT;

        activateSprite(pPlayerSprite, SPRITE_BOAT_APPEAR);
        pPlayerSprite.setMainPointXY(I8_SCREEN_WIDTH / 2, I8_SCREEN_HEIGHT + pPlayerSprite.i_height / 2);
        pPlayerSprite.i_ObjectState = INIT_ANGLE;

        return true;
    }

    public void resumeGameAfterPlayerLostOrPaused()
    {
        super.resumeGameAfterPlayerLostOrPaused();

        deinitState();
        initState();

        i_GameTimer = 0;
        i_LastTime = i_GameTimer;

        i_PlayerSpeed = INIT_PLAYER_SPEED;
        i_PlayerTimeLimit = INIT_TIME_LIMIT;

        activateSprite(pPlayerSprite, SPRITE_BOAT_APPEAR);
        pPlayerSprite.setMainPointXY(I8_SCREEN_WIDTH / 2, I8_SCREEN_HEIGHT + pPlayerSprite.i_height / 2);
        pPlayerSprite.i_ObjectState = INIT_ANGLE;

        switch(getGameDifficultLevel())
        {
            case GAMELEVEL_EASY:
                i_GenerateDelay = LEVEL_EASY_GENERATION;
                break;
            case GAMELEVEL_NORMAL:
                i_GenerateDelay = LEVEL_NORMAL_GENERATION;
                break;
            case GAMELEVEL_HARD:
                i_GenerateDelay = LEVEL_HARD_GENERATION;
                break;
        }

    }

    public String getGameTextID()
    {
        return "WATERRACE";
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

        _outputStream.writeInt(i_playerAttemptions);
        _outputStream.writeInt(i_GameTimer);
        _outputStream.writeInt(i_LastTime);
        _outputStream.writeInt(i_GenerateDelay);

        _outputStream.writeInt(i_PlayerSpeed);
        _outputStream.writeInt(i_PlayerTimeLimit);
        _outputStream.writeInt(i_LastGateX);
        _outputStream.writeInt(i_Range);
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

        i_PlayerSpeed = _inputStream.readInt();
        i_PlayerTimeLimit = _inputStream.readInt();
        i_LastGateX = _inputStream.readInt();
        i_Range = _inputStream.readInt();
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
        generateGate();

        i_GameTimer++;

        if((i_GameTimer & STEP_WAVE_MASK) == 0)
        {
            if(i_GenerateDelay > LIMIT_BOATFREQ )
            {
                i_GenerateDelay--;
            }
            else
            {
                switch(getGameDifficultLevel())
                {
                    case GAMELEVEL_EASY:
                        i_GenerateDelay = LEVEL_EASY_GENERATION;
                        break;
                    case GAMELEVEL_NORMAL:
                        i_GenerateDelay = LEVEL_NORMAL_GENERATION;
                        break;
                    case GAMELEVEL_HARD:
                        i_GenerateDelay = LEVEL_HARD_GENERATION;
                        break;
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
    }

    public int getGameStepDelay()
    {
        return i_timedelay;
    }
}
