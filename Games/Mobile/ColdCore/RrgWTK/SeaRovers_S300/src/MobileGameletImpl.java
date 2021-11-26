
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.DataInputStream;


public class MobileGameletImpl extends MobileGamelet
{
    private static final int SHIP_X = (64 << 8);
    private static final int SHIP_Y = (-32 << 8);
    private static final int SHIP_X_STEP = (8 << 8);

    private static final int SHIP_X_DISTANCE = (48 << 8);

    private static final int SHIP_SPEED = (2 << 8);
    private static final int COCKBOAT_SPEED = (2 << 8);

    private static final int LEFT_MARGIN = (32 << 8);
    private static final int RIGHT_MARGIN = (32 << 8);

    private static final int MAX_HSPEED = (3 << 8);
    private static final int MIN_HSPEED = (1 << 8);
    private static final int INC_HSPEED = (0x0080);
    private static final int DEC_HSPEED = (0x0080);

    private static final int APP_VSPEED = (3 << 8);
    private static final int MAX_VSPEED = (4 << 8)-0x080;
    private static final int MIN_VSPEED = (2 << 8);
    private static final int INC_VSPEED = (0x0040);
    private static final int DEC_VSPEED = (0x0040);

    private static final int INIT_Y_OFFSET = (0x1900) +0x500;

    private static final int FORCE_BOAT       = 20;
    private static final int FORCE_TRADESHIP  = 25;
    private static final int FORCE_BATTLESHIP = 75;

    private static final int FIRE_TRADESHIP  = 15;
    private static final int FIRE_BATTLESHIP = 20;
    private static final int FIRE_CORSAR     = 25;

    public int i_CorsarHSpeed;
    public int i_CorsarVSpeed;

    public int i_LastTime;
    public int i_GenerateDelay;
    public int i_GenerateShipX;
    public int i_GenerateShipStep;
    public int i_wayLength;


    public static final int SCORES_FIRE = 5;
    public static final int SCORES_BOAT = 5;
    public static final int SCORES_BATTLESHIP = 50;
    public static final int SCORES_TRADESHIP_MIN = 10;
    public static final int SCORES_TRADESHIP_MAX = 30;

    public static final int HEALTH_BONUS = 20;

    private static int I8_SCREEN_WIDTH;

    private static int I8_SCREEN_HEIGHT;

    public MoveObject p_MoveObject;

    public static final int GAMEACTION_CORSAR_FIRE          = 0;
    public static final int GAMEACTION_CORSAR_DEAD          = 1;
    public static final int GAMEACTION_BATTLESHIP_FIRE      = 2;
    public static final int GAMEACTION_TRADESHIP_FIRE       = 3;
    public static final int GAMEACTION_BOAT_DEAD            = 4;
    public static final int GAMEACTION_BATTLESHIP_DEAD      = 5;
    public static final int GAMEACTION_TRADESHIP_DEAD       = 6;
    public static final int GAMEACTION_TRADESHIP_DAMAGED    = 7;
    public static final int GAMEACTION_COCKBOAT             = 8;
    public static final int GAMEACTION_COCKBOAT_RETURN      = 9;
    public static final int GAMEACTION_COCKBOAT_RETURNED    = 10;
    public static final int GAMEACTION_COCKBOAT_LOST        = 11;
    public static final int GAMEACTION_SHIP_COLLISION       = 12;
    public static final int GAMEACTION_SHIP_CRASHED         = 13;

    public boolean lg_FireLocation;

    private int i_timedelay;

    public int i_playerAttemptions;

    public int i_GameTimer;

    public static final int LEVEL_EASY = 0;

    private static final int LEVEL_EASY_ATTEMPTIONS = 4;

    private static final int LEVEL_EASY_FORCE = 120;

    private static final int LEVEL_EASY_TIMEDELAY = 110;

    private static final int LEVEL_EASY_GENERATION = 55;

    public static final int LEVEL_NORMAL = 1;

    private static final int LEVEL_NORMAL_ATTEMPTIONS = 3;

    private static final int LEVEL_NORMAL_FORCE = 100;

    private static final int LEVEL_NORMAL_TIMEDELAY = 90;

    private static final int LEVEL_NORMAL_GENERATION = 50;

    public static final int LEVEL_HARD = 2;

    private static final int LEVEL_HARD_ATTEMPTIONS = 2;

    private static final int LEVEL_HARD_FORCE = 80;

    private static final int LEVEL_HARD_TIMEDELAY = 70;

    private static final int LEVEL_HARD_GENERATION = 45;

    public static final int MAX_SPRITES_NUMBER = 12;

    public Sprite[] ap_Sprites;

    private static final int SPRITEDATALENGTH = 10;

    public Sprite pPlayerCorsar;

    public Sprite pBoat;

    public Sprite pIsland;

    public static final int SPRITE_CORSAR = 0;
    public static final int SPRITE_CORSAR_LEFT = 1;
    public static final int SPRITE_CORSAR_RIGHT = 2;
    public static final int SPRITE_CORSAR_LBACK = 3;
    public static final int SPRITE_CORSAR_RBACK = 4;
    public static final int SPRITE_CORSAR_FIRE_LEFT = 5;
    public static final int SPRITE_CORSAR_FIRE_RIGHT = 6;
    public static final int SPRITE_CORSAR_BEFORE_DEAD = 7;
    public static final int SPRITE_CORSAR_DEAD = 8;

    public static final int SPRITE_TRADESHIP = 9;
    public static final int SPRITE_TRADESHIP_FIRE_LEFT = 10;
    public static final int SPRITE_TRADESHIP_FIRE_RIGHT = 11;
    public static final int SPRITE_TRADESHIP_DAMAGED = 12;
    public static final int SPRITE_TRADESHIP_BEFORE_DEAD = 13;
    public static final int SPRITE_TRADESHIP_DEAD = 14;

    public static final int SPRITE_BATTLESHIP = 15;
    public static final int SPRITE_BATTLESHIP_FIRE_LEFT = 16;
    public static final int SPRITE_BATTLESHIP_FIRE_RIGHT = 17;
    public static final int SPRITE_BATTLESHIP_BEFORE_DEAD = 18;
    public static final int SPRITE_BATTLESHIP_DEAD = 19;

    public static final int SPRITE_COKCBOAT_LEFT = 20;
    public static final int SPRITE_COKCBOAT_RIGHT = 21;

    public static final int SPRITE_BOAT             = 22;
    public static final int SPRITE_BOAT_BEFORE_DEAD = 23;
    public static final int SPRITE_BOAT_DEAD        = 24;
    public static final int SPRITE_ISLAND           = 25;
    public static final int SPRITE_ROCK             = 26;
    public static final int SPRITE_STONE            = 27;


    private static final int [] ai_SpriteParameters = new int[]
    {
        0x1000, 0x2C00, 0x0000, 0x0000, 0x1000, 0x2C00, 3, 1, Sprite.SPRITE_ALIGN_CENTER, Sprite.ANIMATION_FROZEN,
        0x1400, 0x2C00, 0x0000, 0x0000, 0x1000, 0x1C00, 2, 3, Sprite.SPRITE_ALIGN_CENTER, Sprite.ANIMATION_FROZEN,
        0x1400, 0x2C00, 0x0000, 0x0000, 0x1000, 0x1C00, 2, 3, Sprite.SPRITE_ALIGN_CENTER, Sprite.ANIMATION_FROZEN,
        0x1200, 0x2C00, 0x0000, 0x0000, 0x1000, 0x1C00, 5, 1, Sprite.SPRITE_ALIGN_CENTER, Sprite.ANIMATION_FROZEN,
        0x1200, 0x2C00, 0x0000, 0x0000, 0x1000, 0x1C00, 5, 1, Sprite.SPRITE_ALIGN_CENTER, Sprite.ANIMATION_FROZEN,
        0x1000, 0x2C00, 0x0000, 0x0000, 0x1000, 0x2C00, 3, 2, Sprite.SPRITE_ALIGN_CENTER, Sprite.ANIMATION_FROZEN,
        0x1000, 0x2C00, 0x0000, 0x0000, 0x1000, 0x2C00, 3, 2, Sprite.SPRITE_ALIGN_CENTER, Sprite.ANIMATION_FROZEN,
        0x1000, 0x2C00, 0x0000, 0x0000, 0x0300, 0x0300, 5, 1, Sprite.SPRITE_ALIGN_CENTER, Sprite.ANIMATION_FROZEN,
        0x1000, 0x2C00, 0x0000, 0x0000, 0x0300, 0x0300, 5, 2, Sprite.SPRITE_ALIGN_CENTER, Sprite.ANIMATION_FROZEN,

        0x1000, 0x2800, 0x0000, 0x0000, 0x1000, 0x2800, 1, 1, Sprite.SPRITE_ALIGN_CENTER, Sprite.ANIMATION_FROZEN,
        0x1000, 0x2800, 0x0000, 0x0000, 0x1000, 0x2800, 4, 2, Sprite.SPRITE_ALIGN_CENTER, Sprite.ANIMATION_FROZEN,
        0x1000, 0x2800, 0x0000, 0x0000, 0x1000, 0x2800, 4, 2, Sprite.SPRITE_ALIGN_CENTER, Sprite.ANIMATION_FROZEN,
        0x1000, 0x2800, 0x0000, 0x0000, 0x1000, 0x2800, 1, 1, Sprite.SPRITE_ALIGN_CENTER, Sprite.ANIMATION_FROZEN,
        0x1000, 0x2800, 0x0000, 0x0000, 0x1000, 0x2800, 5, 1, Sprite.SPRITE_ALIGN_CENTER, Sprite.ANIMATION_FROZEN,
        0x1000, 0x2800, 0x0000, 0x0000, 0x1000, 0x2800, 5, 2, Sprite.SPRITE_ALIGN_CENTER, Sprite.ANIMATION_FROZEN,


        0x1000, 0x3000, 0x0000, 0x0000, 0x1000, 0x3000, 1, 1, Sprite.SPRITE_ALIGN_CENTER, Sprite.ANIMATION_FROZEN,
        0x1000, 0x3000, 0x0000, 0x0000, 0x1000, 0x3000, 4, 2, Sprite.SPRITE_ALIGN_CENTER, Sprite.ANIMATION_FROZEN,
        0x1000, 0x3000, 0x0000, 0x0000, 0x1000, 0x3000, 4, 2, Sprite.SPRITE_ALIGN_CENTER, Sprite.ANIMATION_FROZEN,
        0x1000, 0x3000, 0x0000, 0x0000, 0x1000, 0x3000, 5, 1, Sprite.SPRITE_ALIGN_CENTER, Sprite.ANIMATION_FROZEN,
        0x1000, 0x3000, 0x0000, 0x0000, 0x1000, 0x3000, 5, 2, Sprite.SPRITE_ALIGN_CENTER, Sprite.ANIMATION_FROZEN,

        0x1000, 0x0800, 0x0000, 0x0000, 0x0800, 0x0400, 3, 1, Sprite.SPRITE_ALIGN_CENTER, Sprite.ANIMATION_CYCLIC,
        0x1000, 0x0800, 0x0000, 0x0000, 0x0800, 0x0400, 3, 1, Sprite.SPRITE_ALIGN_CENTER, Sprite.ANIMATION_CYCLIC,

        0x1000, 0x1400, 0x0000, 0x0000, 0x0C00, 0x1400, 1, 1, Sprite.SPRITE_ALIGN_CENTER, Sprite.ANIMATION_FROZEN,
        0x1000, 0x1400, 0x0000, 0x0000, 0x0C00, 0x1400, 5, 1, Sprite.SPRITE_ALIGN_CENTER, Sprite.ANIMATION_FROZEN,
        0x1000, 0x1400, 0x0000, 0x0000, 0x0C00, 0x1400, 5, 2, Sprite.SPRITE_ALIGN_CENTER, Sprite.ANIMATION_FROZEN,

        0x2000, 0x1400, 0x0000, 0x0000, 0x1C00, 0x1400, 1, 1, Sprite.SPRITE_ALIGN_CENTER, Sprite.ANIMATION_FROZEN,
        0x0800, 0x0800, 0x0000, 0x0000, 0x0800, 0x0800, 1, 1, Sprite.SPRITE_ALIGN_CENTER, Sprite.ANIMATION_FROZEN,
        0x0800, 0x0800, 0x0000, 0x0000, 0x0800, 0x0800, 1, 1, Sprite.SPRITE_ALIGN_CENTER, Sprite.ANIMATION_FROZEN,
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

        lg_FireLocation = false;


        for (int li = 0; li < MAX_SPRITES_NUMBER; li++)
        {
            Sprite p_Sprite = ap_Sprites[li];
            if (!p_Sprite.lg_SpriteActive) continue;
            if (p_Sprite.processAnimation())
            {
                switch (p_Sprite.i_ObjectType)
                {
                    case SPRITE_BOAT_BEFORE_DEAD:
                        activateSprite(p_Sprite, SPRITE_BOAT_DEAD);
                        m_pAbstractGameActionListener.processGameAction(GAMEACTION_BOAT_DEAD);
                        break;
                    case SPRITE_TRADESHIP_BEFORE_DEAD:
                        activateSprite(p_Sprite, SPRITE_TRADESHIP_DEAD);
                        m_pAbstractGameActionListener.processGameAction(GAMEACTION_TRADESHIP_DEAD);
                        break;
                    case SPRITE_BATTLESHIP_BEFORE_DEAD:
                        activateSprite(p_Sprite, SPRITE_BATTLESHIP_DEAD);
                        m_pAbstractGameActionListener.processGameAction(GAMEACTION_BATTLESHIP_DEAD);
                        break;

                    case SPRITE_BOAT_DEAD:
                    case SPRITE_TRADESHIP_DEAD:
                    case SPRITE_BATTLESHIP_DEAD:
                        deactivateSprite(p_Sprite);
                        break;
                    case SPRITE_BATTLESHIP_FIRE_LEFT:
                    case SPRITE_BATTLESHIP_FIRE_RIGHT:
                        activateSprite(p_Sprite, SPRITE_BATTLESHIP);
                        break;
                    case SPRITE_TRADESHIP_FIRE_LEFT:
                    case SPRITE_TRADESHIP_FIRE_RIGHT:
                        activateSprite(p_Sprite, SPRITE_TRADESHIP);
                        break;
                }
            }
            switch (p_Sprite.i_ObjectType)
            {
                default:
                    p_Sprite.setMainPointXY(p_Sprite.i_mainX, p_Sprite.i_mainY + i_CorsarVSpeed);
                    break;
                case SPRITE_BOAT:
                    p_Sprite.setMainPointXY(p_Sprite.i_mainX, p_Sprite.i_mainY + SHIP_SPEED + i_CorsarVSpeed);
                    if(p_Sprite.i_Force <= 0)
                    {
                        activateSprite(p_Sprite, SPRITE_BOAT_BEFORE_DEAD);
                    }
                    break;
                case SPRITE_BATTLESHIP_FIRE_LEFT:
                case SPRITE_BATTLESHIP_FIRE_RIGHT:
                case SPRITE_BATTLESHIP:
                    p_Sprite.setMainPointXY(p_Sprite.i_mainX, p_Sprite.i_mainY + SHIP_SPEED + i_CorsarVSpeed);
                    if(p_Sprite.i_Force <= 0)
                    {
                        activateSprite(p_Sprite, SPRITE_BATTLESHIP_BEFORE_DEAD);
                        m_pAbstractGameActionListener.processGameAction(GAMEACTION_BATTLESHIP_DEAD);
                    }
                    break;
                case SPRITE_TRADESHIP:
                    p_Sprite.setMainPointXY(p_Sprite.i_mainX, p_Sprite.i_mainY + SHIP_SPEED + i_CorsarVSpeed);
                    if(p_Sprite.i_Force == 0)
                    {
                        activateSprite(p_Sprite, SPRITE_TRADESHIP_DAMAGED);
                        m_pAbstractGameActionListener.processGameAction(GAMEACTION_TRADESHIP_DAMAGED);
                    }
                    else if(p_Sprite.i_Force < 0)
                    {
                        activateSprite(p_Sprite, SPRITE_TRADESHIP_DEAD);
                        m_pAbstractGameActionListener.processGameAction(GAMEACTION_TRADESHIP_DEAD);
                    }
                    break;
                case SPRITE_TRADESHIP_DAMAGED:

                    p_Sprite.setMainPointXY(p_Sprite.i_mainX, p_Sprite.i_mainY + i_CorsarVSpeed);

                    if(!pBoat.lg_SpriteActive)
                    {
                        if(i_CorsarVSpeed == 0 && pPlayerCorsar.i_ObjectType == SPRITE_CORSAR)
                        {
                            int i_y = Math.max(pPlayerCorsar.i_ScreenY,p_Sprite.i_ScreenY);
                            int i_yf = Math.min(pPlayerCorsar.i_ScreenY + pPlayerCorsar.i_height, p_Sprite.i_ScreenY + p_Sprite.i_height);

                            if(pIsland.lg_SpriteActive)
                            {

                               int i_x = Math.max(p_Sprite.i_ScreenX, pPlayerCorsar.i_ScreenX);
                               int i_xf = Math.min(p_Sprite.i_ScreenX + p_Sprite.i_width, pPlayerCorsar.i_ScreenX + pPlayerCorsar.i_width);

                               if(pIsland.i_ScreenX < i_xf && pIsland.i_ScreenX+pIsland.i_width > i_x &&
                                  pIsland.i_ScreenY < i_yf && pIsland.i_ScreenY+pIsland.i_height > i_y)
                               {
                                    int up_intersection = pIsland.i_ScreenY - i_y;
                                    int dn_intersection = i_yf - pIsland.i_ScreenY - pIsland.i_height;

                                    if(up_intersection > dn_intersection)
                                    {
                                       i_yf = i_y;
                                       i_y = pIsland.i_ScreenY;
                                    }
                                      else
                                           {
                                              i_y = i_yf;
                                              i_yf = pIsland.i_ScreenY - pIsland.i_height;
                                           }
                               }
                            }

                            if(i_yf >= i_y)
                            {
                                 i_y += (i_yf - i_y) / 2;

                                 if(pPlayerCorsar.i_mainX < p_Sprite.i_mainX)
                                 {
                                     activateSprite(pBoat, SPRITE_COKCBOAT_RIGHT);
                                     pBoat.setMainPointXY(pPlayerCorsar.i_ScreenX + pPlayerCorsar.i_width , i_y);
                                 }
                                 else
                                 {
                                     activateSprite(pBoat, SPRITE_COKCBOAT_LEFT);
                                     pBoat.setMainPointXY(pPlayerCorsar.i_ScreenX, i_y);
                                 }
                                 pBoat.i_ObjectState = 0;
                                 m_pAbstractGameActionListener.processGameAction(GAMEACTION_COCKBOAT);

                            }
                              else
                                    {
                                        activateSprite(p_Sprite, SPRITE_TRADESHIP_DEAD);
                                        m_pAbstractGameActionListener.processGameAction(GAMEACTION_TRADESHIP_DEAD);
                                    }
                        }

                    }
                    else
                    {
                        {
                            boolean isBack = false;

                            if(pBoat.i_ObjectType == SPRITE_COKCBOAT_RIGHT)
                            {
                                if(p_Sprite.i_ScreenX <= pBoat.i_mainX)
                                {
                                    activateSprite(pBoat, SPRITE_COKCBOAT_LEFT);
                                    isBack = true;
                                }
                            }
                            else
                                if(p_Sprite.i_ScreenX + p_Sprite.i_width >= pBoat.i_mainX)
                                {
                                    activateSprite(pBoat, SPRITE_COKCBOAT_RIGHT);
                                    isBack = true;
                                }
                            if(isBack)
                            {
                                m_pAbstractGameActionListener.processGameAction(GAMEACTION_COCKBOAT_RETURN);

                                activateSprite(p_Sprite, SPRITE_TRADESHIP_DEAD);
                                m_pAbstractGameActionListener.processGameAction(GAMEACTION_TRADESHIP_DEAD);

                                int score = SCORES_TRADESHIP_MIN + getRandomInt(SCORES_TRADESHIP_MAX - SCORES_TRADESHIP_MIN);
                                pBoat.i_ObjectState = score;
                            }
                        }
                    }
                    break;
            }

            if((p_Sprite.i_mainY - p_Sprite.i_height / 2) > I8_SCREEN_HEIGHT)
            {
                deactivateSprite(p_Sprite);
            }
            else
            if(p_Sprite.isCollided(pPlayerCorsar))
            {
                if(pPlayerCorsar.i_ObjectType < SPRITE_CORSAR_BEFORE_DEAD)
                {
                  activateSprite(pPlayerCorsar, SPRITE_CORSAR_BEFORE_DEAD);
                }
                p_Sprite.i_Force = -1;
                i_CorsarHSpeed = 0;
                i_CorsarVSpeed = 0;
                m_pAbstractGameActionListener.processGameAction(GAMEACTION_SHIP_COLLISION);
            }
            else
            if(p_Sprite.i_Force > 0)
            {
                int cx = pPlayerCorsar.i_mainX;
                int sx = p_Sprite.i_mainX;

                int dist = cx - sx;

                if(pIsland.lg_SpriteActive && p_Sprite.isCollided(pIsland))
                {
                    p_Sprite.i_Force = -1;
                    m_pAbstractGameActionListener.processGameAction(GAMEACTION_SHIP_CRASHED);
                }
                else
                if(Math.abs(dist) <= SHIP_X_DISTANCE)
                {
                    if((pPlayerCorsar.i_mainY - pPlayerCorsar.i_height / 2) < p_Sprite.i_mainY)
                    {
                        lg_FireLocation = pPlayerCorsar.i_ObjectType < SPRITE_CORSAR_BEFORE_DEAD;

                        if (pPlayerCorsar.i_ObjectType == SPRITE_CORSAR)
                        {
                            if ((p_MoveObject.i_buttonState & MoveObject.BUTTON_FIRE) != 0)
                            {
                                if(cx < sx)
                                {
                                    activateSprite(pPlayerCorsar, SPRITE_CORSAR_FIRE_RIGHT);
                                }
                                else
                                {
                                    activateSprite(pPlayerCorsar, SPRITE_CORSAR_FIRE_LEFT);
                                }
                                p_Sprite.i_Force -= FIRE_CORSAR;
                                m_pAbstractGameActionListener.processGameAction(GAMEACTION_CORSAR_FIRE);
                                addScore(SCORES_FIRE);
                            }
                        }

                        switch(p_Sprite.i_ObjectType)
                        {
                            case SPRITE_BOAT:
                                if(p_Sprite.i_Force <= 0)
                                {
                                    addScore(SCORES_BOAT);
                                }
                                break;
                            case SPRITE_BATTLESHIP_FIRE_LEFT:
                            case SPRITE_BATTLESHIP_FIRE_RIGHT:
                                if(p_Sprite.i_Force <= 0)
                                {
                                    addScore(SCORES_BATTLESHIP);
                                }
                                break;
                            case SPRITE_BATTLESHIP:
                                if(p_Sprite.i_Force <= 0)
                                {
                                    addScore(SCORES_BATTLESHIP);
                                }
                                else
                                   if(pPlayerCorsar.i_ObjectType < SPRITE_CORSAR_BEFORE_DEAD)
                                   {
                                       if(sx < cx)
                                       {
                                           activateSprite(p_Sprite, SPRITE_BATTLESHIP_FIRE_RIGHT);
                                       }
                                       else
                                       {
                                           activateSprite(p_Sprite, SPRITE_BATTLESHIP_FIRE_LEFT);
                                       }
                                       pPlayerCorsar.i_Force -= FIRE_BATTLESHIP;
                                       m_pAbstractGameActionListener.processGameAction(GAMEACTION_BATTLESHIP_FIRE);
                                   }
                                break;
                            case SPRITE_TRADESHIP:
                                if(p_Sprite.i_Force < p_Sprite.i_MaxForce && getRandomInt(8) > 3 && pPlayerCorsar.i_ObjectType < SPRITE_CORSAR_BEFORE_DEAD)
                                {
                                    if(sx < cx)
                                    {
                                        activateSprite(p_Sprite, SPRITE_TRADESHIP_FIRE_RIGHT);
                                    }
                                    else
                                    {
                                        activateSprite(p_Sprite, SPRITE_TRADESHIP_FIRE_LEFT);
                                    }
                                    m_pAbstractGameActionListener.processGameAction(GAMEACTION_TRADESHIP_FIRE);
                                    pPlayerCorsar.i_Force -= FIRE_TRADESHIP;
                                }
                                break;
                        }
                    }
                }
            }
        }
    }

    private boolean processCorsar(int _buttonState)
    {
        int i_x = pPlayerCorsar.i_ScreenX;
        int i_y = pPlayerCorsar.i_ScreenY;

        int i_mx = pPlayerCorsar.i_mainX;
        int i_my = pPlayerCorsar.i_mainY;

        int i_w = pPlayerCorsar.i_width;
        int i_h = pPlayerCorsar.i_height;

        if(pBoat.lg_SpriteActive)
        {
            pBoat.processAnimation();
            switch(pBoat.i_ObjectType)
            {
                case SPRITE_COKCBOAT_RIGHT:
                    pBoat.setMainPointXY(pBoat.i_mainX + COCKBOAT_SPEED, pBoat.i_mainY + i_CorsarVSpeed);
                    break;
                case SPRITE_COKCBOAT_LEFT:
                    pBoat.setMainPointXY(pBoat.i_mainX - COCKBOAT_SPEED, pBoat.i_mainY + i_CorsarVSpeed);
                    break;
            }
            if(pBoat.isCollided(pPlayerCorsar) && pPlayerCorsar.i_ObjectType < SPRITE_CORSAR_BEFORE_DEAD)
            {
                if(pBoat.i_ObjectState != 0)
                {
                    pPlayerCorsar.i_Force += HEALTH_BONUS;

                    if(pPlayerCorsar.i_Force > pPlayerCorsar.i_MaxForce)
                    {
                       pPlayerCorsar.i_Force = pPlayerCorsar.i_MaxForce;
                    }

                    addScore(pBoat.i_ObjectState);
                    deactivateSprite(pBoat);
                    m_pAbstractGameActionListener.processGameAction(GAMEACTION_COCKBOAT_RETURNED);
                }
            }

            if( ((pBoat.i_mainY - pBoat.i_height / 2) > I8_SCREEN_HEIGHT) || ((pBoat.i_mainX + pBoat.i_width / 2) < 0) || ((pBoat.i_mainX - pBoat.i_width / 2) > I8_SCREEN_WIDTH)  )
            {
                pBoat.i_ObjectState = 0;
                deactivateSprite(pBoat);
                m_pAbstractGameActionListener.processGameAction(GAMEACTION_COCKBOAT_LOST);
            }
        }

        if(getGameWorldState() != GAMEWORLDSTATE_PLAYED) return true;

        if(pPlayerCorsar.processAnimation())
        {
            switch(pPlayerCorsar.i_ObjectType)
            {
                case SPRITE_CORSAR:
                    break;
                case SPRITE_CORSAR_FIRE_LEFT:
                case SPRITE_CORSAR_FIRE_RIGHT:
                    activateSprite(pPlayerCorsar, SPRITE_CORSAR);
                    break;
                case SPRITE_CORSAR_RIGHT:
                    if(i_mx < (I8_SCREEN_WIDTH - RIGHT_MARGIN))
                    {
                        if ((_buttonState & MoveObject.BUTTON_RIGHT) == 0)
                        {
                            activateSprite(pPlayerCorsar, SPRITE_CORSAR_RBACK);
                        }
                    }
                    else
                    {
                        activateSprite(pPlayerCorsar, SPRITE_CORSAR_RBACK);
                    }
                    break;
                case SPRITE_CORSAR_LEFT:
                    if(i_mx > (0x0000 + LEFT_MARGIN))
                    {
                        if ((_buttonState & MoveObject.BUTTON_LEFT) == 0)
                        {
                            activateSprite(pPlayerCorsar, SPRITE_CORSAR_LBACK);
                        }
                    }
                    else
                    {
                        activateSprite(pPlayerCorsar, SPRITE_CORSAR_LBACK);
                    }
                    break;

                case SPRITE_CORSAR_BEFORE_DEAD:
                    i_CorsarHSpeed = 0;
                    i_CorsarVSpeed = 0;
                    activateSprite(pPlayerCorsar, SPRITE_CORSAR_DEAD);
                    m_pAbstractGameActionListener.processGameAction(GAMEACTION_CORSAR_DEAD);
                    break;
                case SPRITE_CORSAR_DEAD:
                    return false;
                case SPRITE_CORSAR_LBACK:
                case SPRITE_CORSAR_RBACK:
                    activateSprite(pPlayerCorsar, SPRITE_CORSAR);
                    i_CorsarHSpeed = 0;
                    break;
            }
        }


        if(pPlayerCorsar.i_mainY > I8_SCREEN_HEIGHT - INIT_Y_OFFSET)
        {
            pPlayerCorsar.setMainPointXY(pPlayerCorsar.i_mainX, pPlayerCorsar.i_mainY - i_CorsarVSpeed);
            if(pPlayerCorsar.i_mainY < I8_SCREEN_HEIGHT - INIT_Y_OFFSET)
            {
                 pPlayerCorsar.setMainPointXY(pPlayerCorsar.i_mainX, I8_SCREEN_HEIGHT - INIT_Y_OFFSET);
            }
        }
        else
        switch(pPlayerCorsar.i_ObjectType)
        {
            case SPRITE_CORSAR:
                if(i_CorsarVSpeed > 0 && pPlayerCorsar.i_Frame == pPlayerCorsar.i_maxFrames-1)
                {
                    if ((_buttonState & MoveObject.BUTTON_LEFT) != 0)
                    {
                        if(i_mx > (0x0000 + LEFT_MARGIN))
                        {
                            activateSprite(pPlayerCorsar, SPRITE_CORSAR_LEFT);
                            i_CorsarHSpeed = -MIN_HSPEED;
                        }
                    }
                    else if ((_buttonState & MoveObject.BUTTON_RIGHT) != 0)
                    {
                        if(i_mx < (I8_SCREEN_WIDTH - RIGHT_MARGIN))
                        {
                            activateSprite(pPlayerCorsar, SPRITE_CORSAR_RIGHT);
                            i_CorsarHSpeed = MIN_HSPEED;
                        }
                    }
                }

                if ((_buttonState & MoveObject.BUTTON_UP) != 0)
                {
                    if((i_CorsarVSpeed + INC_VSPEED) > MAX_VSPEED )
                    {
                        i_CorsarVSpeed = MAX_VSPEED;
                    }
                    else
                    {
                        i_CorsarVSpeed += INC_VSPEED;
                    }
                }
                else if ((_buttonState & MoveObject.BUTTON_DOWN) != 0)
                {
                    if((i_CorsarVSpeed - DEC_VSPEED) < 0 )
                    {
                        i_CorsarVSpeed = 0;
                    }
                    else
                    {
                        i_CorsarVSpeed -= DEC_VSPEED;
                    }
                }
                else
                {
                    if(i_CorsarVSpeed != MIN_VSPEED && i_CorsarVSpeed != 0)
                    {
                        if(i_CorsarVSpeed > MIN_VSPEED)
                        {
                            i_CorsarVSpeed -= DEC_VSPEED;
                            if(i_CorsarVSpeed < MIN_VSPEED) i_CorsarVSpeed = MIN_VSPEED;
                        }
                        else
                        {
                            i_CorsarVSpeed += INC_VSPEED;
                            if(i_CorsarVSpeed > MIN_VSPEED) i_CorsarVSpeed = MIN_VSPEED;
                        }
                    }
                }
                break;
            case SPRITE_CORSAR_FIRE_LEFT:
            case SPRITE_CORSAR_FIRE_RIGHT:
                break;
            case SPRITE_CORSAR_LEFT:
                i_mx += i_CorsarHSpeed;
                pPlayerCorsar.setMainPointXY(i_mx, i_my);
                if((i_CorsarHSpeed - INC_HSPEED) < -MAX_HSPEED )
                {
                    i_CorsarHSpeed = -MAX_HSPEED;
                }
                else
                {
                    i_CorsarHSpeed -= INC_HSPEED;
                }
                break;
            case SPRITE_CORSAR_RIGHT:
                i_mx += i_CorsarHSpeed;
                pPlayerCorsar.setMainPointXY(i_mx, i_my);
                if((i_CorsarHSpeed + INC_HSPEED) > MAX_HSPEED )
                {
                    i_CorsarHSpeed = MAX_HSPEED;
                }
                else
                {
                    i_CorsarHSpeed += INC_HSPEED;
                }
                break;
            case SPRITE_CORSAR_RBACK:
                i_mx += i_CorsarHSpeed;
                pPlayerCorsar.setMainPointXY(i_mx, i_my);
                if((i_CorsarHSpeed - DEC_HSPEED) < 0 )
                {
                    i_CorsarHSpeed = 0;
                }
                else
                {
                    i_CorsarHSpeed -= DEC_HSPEED;
                }
                break;
            case SPRITE_CORSAR_LBACK:
                i_mx += i_CorsarHSpeed;
                pPlayerCorsar.setMainPointXY(i_mx, i_my);
                if((i_CorsarHSpeed + DEC_HSPEED) > 0 )
                {
                    i_CorsarHSpeed = 0;
                }
                else
                {
                    i_CorsarHSpeed += DEC_HSPEED;
                }
                break;
        }

        i_wayLength = (i_wayLength + i_CorsarVSpeed) & 0xFFFFFF;

        if(pIsland.lg_SpriteActive)
        {
             pIsland.setMainPointXY(pIsland.i_mainX, pIsland.i_mainY + i_CorsarVSpeed);

             if(pIsland.i_mainY - (pIsland.i_height >> 1) > I8_SCREEN_HEIGHT)
             {
                 deactivateSprite(pIsland);
             }

        }

        if(pIsland.lg_SpriteActive && pPlayerCorsar.i_Force > 0 && pPlayerCorsar.isCollided(pIsland))
        {
            pPlayerCorsar.setMainPointXY(pPlayerCorsar.i_mainX - i_CorsarHSpeed, pPlayerCorsar.i_mainY + i_CorsarVSpeed);
            pPlayerCorsar.i_Force = -1;
            i_CorsarHSpeed = 0;
            i_CorsarVSpeed = 0;
            activateSprite(pPlayerCorsar, SPRITE_CORSAR_BEFORE_DEAD);
            m_pAbstractGameActionListener.processGameAction(GAMEACTION_SHIP_CRASHED);
        }


        if(pPlayerCorsar.i_Force <= 0 && pPlayerCorsar.i_ObjectType < SPRITE_CORSAR_BEFORE_DEAD)
        {
            pPlayerCorsar.i_Force = 0;
            i_CorsarHSpeed >>= 1;
            i_CorsarVSpeed >>= 1;
            activateSprite(pPlayerCorsar, SPRITE_CORSAR_BEFORE_DEAD);
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
                int y = SHIP_Y + (i_wayLength & 0xFF);
                int x = i_GenerateShipX;
                int type = SPRITE_BOAT;
                int force = 0;

                switch(getRandomInt(4))
                {
                    case 0:
                        type = SPRITE_TRADESHIP;
                        force = FORCE_TRADESHIP;
                        break;
                    case 1:
                        type = SPRITE_BATTLESHIP;
                        force = FORCE_BATTLESHIP;
                        break;
                    default:
                    case 2:

                        type = SPRITE_BOAT;
                        force = FORCE_BOAT;


                        if(!pIsland.lg_SpriteActive && i_CorsarVSpeed >= 0x0100)
                        {
                            switch(getGameDifficultLevel())
                            {
                                case LEVEL_EASY:                
                                    if(getRandomInt(4) > 2)
                                    {
                                       p_emptySprite = pIsland;
                                       type = SPRITE_ISLAND;
                                    }
                                    break;
                                case LEVEL_NORMAL:
                                    switch(getRandomInt(5))    
                                    {
                                       case 0:
                                       case 1:
                                               p_emptySprite = pIsland;
                                               type = SPRITE_ISLAND;
                                               break;
                                       case 2:
                                               p_emptySprite = pIsland;
                                               type = SPRITE_ROCK;
                                               break;
                                    }
                                    break;
                                case LEVEL_HARD:
                                    switch(getRandomInt(4))    
                                    {
                                       case 0:
                                       case 1:
                                               p_emptySprite = pIsland;
                                               type = SPRITE_ISLAND;
                                               break;
                                       case 2:
                                               p_emptySprite = pIsland;
                                               type = SPRITE_ROCK;
                                               break;
                                       case 3:
                                               p_emptySprite = pIsland;
                                               type = SPRITE_STONE;
                                               break;
                                    }
                                    break;
                            }
                        }
                        break;
                }

                activateSprite(p_emptySprite, type);


                if(pIsland == p_emptySprite)
                {
                    p_emptySprite.setMainPointXY(x, y);
                }
                  else

                    while(true)
                    {
                        p_emptySprite.setMainPointXY(x, y);

                        i_GenerateShipX += i_GenerateShipStep;
                        if(i_GenerateShipX > (I8_SCREEN_WIDTH - RIGHT_MARGIN))
                        {
                            i_GenerateShipX = (I8_SCREEN_WIDTH - RIGHT_MARGIN);
                            i_GenerateShipStep = -i_GenerateShipStep;
                        }
                        if(i_GenerateShipX < (0x0000 + LEFT_MARGIN))
                        {
                            i_GenerateShipX = (0x0000 + LEFT_MARGIN);
                            i_GenerateShipStep = -i_GenerateShipStep;
                        }
                        if(!pIsland.lg_SpriteActive ||
                           p_emptySprite.i_ScreenX + p_emptySprite.i_width < pIsland.i_ScreenX ||
                           p_emptySprite.i_ScreenX > (pIsland.i_ScreenX + pIsland.i_width))
                           {
                              break;
                           }
                        x = i_GenerateShipX;
                    }

                p_emptySprite.lg_SpriteActive = true;
                p_emptySprite.i_Force = force;
                p_emptySprite.i_MaxForce = force;


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
                    pPlayerCorsar.i_Force = LEVEL_EASY_FORCE;
                }
                ;
                break;
            case LEVEL_NORMAL:
                {
                    i_timedelay = LEVEL_NORMAL_TIMEDELAY;
                    i_playerAttemptions = LEVEL_NORMAL_ATTEMPTIONS;
                    i_GenerateDelay = LEVEL_NORMAL_GENERATION;
                    pPlayerCorsar.i_Force = LEVEL_NORMAL_FORCE;
                }
                ;
                break;
            case LEVEL_HARD:
                {
                    i_timedelay = LEVEL_HARD_TIMEDELAY;
                    i_playerAttemptions = LEVEL_HARD_ATTEMPTIONS;
                    i_GenerateDelay = LEVEL_HARD_GENERATION;
                    pPlayerCorsar.i_Force = LEVEL_HARD_FORCE;
                }
                ;
                break;
        }

        pPlayerCorsar.i_MaxForce = pPlayerCorsar.i_Force;

        i_wayLength = 0;
        i_GameTimer = 0;
        i_LastTime = i_GameTimer;

        activateSprite(pPlayerCorsar, SPRITE_CORSAR);
        pPlayerCorsar.setMainPointXY(I8_SCREEN_WIDTH / 2, I8_SCREEN_HEIGHT + (pPlayerCorsar.i_height>>1));

        i_CorsarHSpeed = 0;
        i_CorsarVSpeed = APP_VSPEED;

        i_GenerateShipX = SHIP_X;
        i_GenerateShipStep = SHIP_X_STEP;

        activateSprite(pBoat, SPRITE_COKCBOAT_RIGHT);
        deactivateSprite(pBoat);

        deactivateSprite(pIsland);

        return true;
    }

    public void resumeGameAfterPlayerLostOrPaused()
    {
        super.resumeGameAfterPlayerLostOrPaused();

        deinitState();
        initState();

        i_GameTimer = 0;
        i_LastTime = i_GameTimer;

        activateSprite(pPlayerCorsar, SPRITE_CORSAR);
        pPlayerCorsar.setMainPointXY(I8_SCREEN_WIDTH / 2, I8_SCREEN_HEIGHT + (pPlayerCorsar.i_height>>1));

        i_CorsarHSpeed = 0;
        i_CorsarVSpeed = APP_VSPEED;

        activateSprite(pBoat, SPRITE_COKCBOAT_RIGHT);
        deactivateSprite(pBoat);

        deactivateSprite(pIsland);

        switch(getGameDifficultLevel())
        {
            case LEVEL_EASY:
                pPlayerCorsar.i_Force = LEVEL_EASY_FORCE;
                break;
            case LEVEL_NORMAL:
                pPlayerCorsar.i_Force = LEVEL_NORMAL_FORCE;
                break;
            case LEVEL_HARD:
                pPlayerCorsar.i_Force = LEVEL_HARD_FORCE;
                break;
        }
        pPlayerCorsar.i_MaxForce = pPlayerCorsar.i_Force;

    }

    public String getGameTextID()
    {
        return "CORSARS";
    }

    public boolean initState()
    {
        ap_Sprites = new Sprite[MAX_SPRITES_NUMBER];
        for (int li = 0; li < MAX_SPRITES_NUMBER; li++) ap_Sprites[li] = new Sprite(li);

        pPlayerCorsar = new Sprite(-1);
        pBoat = new Sprite(-2);

        pIsland = new Sprite(-3);

        p_MoveObject = new MoveObject();

        return true;
    }

    public void deinitState()
    {
        ap_Sprites = null;
        pPlayerCorsar = null;
        pBoat = null;

        pIsland = null;

        Runtime.getRuntime().gc();
    }

    public int _getMaximumDataSize()
    {
        return (MAX_SPRITES_NUMBER+3)*(Sprite.DATASIZE_BYTES+1)+32;
    }

    public void _saveGameData(DataOutputStream _outputStream) throws IOException
    {
        for(int li=0;li<MAX_SPRITES_NUMBER;li++)
        {
            _outputStream.writeByte(ap_Sprites[li].i_ObjectType);
            ap_Sprites[li].writeSpriteToStream(_outputStream);
        }
        _outputStream.writeByte(pPlayerCorsar.i_ObjectType);
        pPlayerCorsar.writeSpriteToStream(_outputStream);

        _outputStream.writeByte(pBoat.i_ObjectType);
        pBoat.writeSpriteToStream(_outputStream);

        _outputStream.writeByte(pIsland.i_ObjectType);
        pIsland.writeSpriteToStream(_outputStream);

        _outputStream.writeInt(i_GameTimer);
        _outputStream.writeInt(i_playerAttemptions);
        _outputStream.writeInt(i_LastTime);
        _outputStream.writeInt(i_GenerateDelay);
        _outputStream.writeInt(i_GenerateShipX);
        _outputStream.writeInt(i_GenerateShipStep);
        _outputStream.writeInt(i_CorsarHSpeed);
        _outputStream.writeInt(i_CorsarVSpeed);
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
        activateSprite(pPlayerCorsar,i_type);
        pPlayerCorsar.readSpriteFromStream(_inputStream);

        i_type = _inputStream.readByte();
        activateSprite(pBoat,i_type);
        pBoat.readSpriteFromStream(_inputStream);

        i_type = _inputStream.readByte();
        activateSprite(pIsland,i_type);
        pIsland.readSpriteFromStream(_inputStream);

        i_GameTimer = _inputStream.readInt();
        i_playerAttemptions = _inputStream.readInt();
        i_LastTime = _inputStream.readInt();
        i_GenerateDelay = _inputStream.readInt();
        i_GenerateShipX = _inputStream.readInt();
        i_GenerateShipStep = _inputStream.readInt();
        i_CorsarHSpeed = _inputStream.readInt();
        i_CorsarVSpeed = _inputStream.readInt();

    }

    public int nextGameStep()
    {
        CAbstractPlayer p_player = m_pPlayerList[0];
        p_player.nextPlayerMove(this, p_MoveObject);

        processSprites();

        if(!processCorsar(p_MoveObject.i_buttonState))
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
