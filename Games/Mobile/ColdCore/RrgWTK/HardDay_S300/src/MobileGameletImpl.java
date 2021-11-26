
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.DataInputStream;



public class MobileGameletImpl extends MobileGamelet
{




    public static final int GAME_X = (0 << 8);
    public static final int GAME_Y = (0 << 8);

    public static final int CELL_WIDTH  = (12 << 8);
    public static final int CELL_HEIGHT = (24 << 8);
    public static final int CELL_LEDGE = (4 << 8);

    public static final int INIT_PLAYER_DOOR = 3;

    public static final int HAMBURGER_DELAY = 100;
    public static final int LIFT_SPEED = 0x0100;
    public static final int MANAGER_SPEED = 0x0200;
    public static final int PLAYER_HSPEED = 0x0300;
    public static final int PLAYER_HI_HSPEED = 0x0600;
    public static final int PLAYER_VSPEED = 0x0200;


    public static final int MANAGER_STEPS = CELL_WIDTH / MANAGER_SPEED;

    private static final int MANAGER_STATE_DOWN = 12;
    private static final int MANAGER_STATE_UP = 14;


    private static final int PLAYER_ON_LIFT = 0x80;
    private static final int MANAGER_ON_LIFT = 0x40;

    private final static int I8_SCREEN_WIDTH = (128 << 8);
    private final static int I8_SCREEN_HEIGHT = (128 << 8);

    private final static int BOTTOM_FIRST_CENTER_DOOR = (I8_SCREEN_HEIGHT - 4*CELL_HEIGHT)/2 + 1*CELL_HEIGHT + GAME_Y - CELL_LEDGE;
    private final static int[] DOORS_LAYOUT = new int[]{
          I8_SCREEN_WIDTH/2 - CELL_WIDTH + GAME_X, (I8_SCREEN_HEIGHT - 4*CELL_HEIGHT)/2 + 0*CELL_HEIGHT + GAME_Y,
          I8_SCREEN_WIDTH/2 - CELL_WIDTH + GAME_X, (I8_SCREEN_HEIGHT - 4*CELL_HEIGHT)/2 + 1*CELL_HEIGHT + GAME_Y,
          I8_SCREEN_WIDTH/2 - CELL_WIDTH + GAME_X, (I8_SCREEN_HEIGHT - 4*CELL_HEIGHT)/2 + 2*CELL_HEIGHT + GAME_Y,
          I8_SCREEN_WIDTH/2 - CELL_WIDTH + GAME_X, (I8_SCREEN_HEIGHT - 4*CELL_HEIGHT)/2 + 3*CELL_HEIGHT + GAME_Y,
          I8_SCREEN_WIDTH/2 - 5*CELL_WIDTH + GAME_X, (I8_SCREEN_HEIGHT - 3*CELL_HEIGHT)/2 + 0*CELL_HEIGHT + GAME_Y,
          I8_SCREEN_WIDTH/2 - 5*CELL_WIDTH + GAME_X, (I8_SCREEN_HEIGHT - 3*CELL_HEIGHT)/2 + 1*CELL_HEIGHT + GAME_Y,
          I8_SCREEN_WIDTH/2 - 5*CELL_WIDTH + GAME_X, (I8_SCREEN_HEIGHT - 3*CELL_HEIGHT)/2 + 2*CELL_HEIGHT + GAME_Y,
          I8_SCREEN_WIDTH/2 + 3*CELL_WIDTH + GAME_X, (I8_SCREEN_HEIGHT - 3*CELL_HEIGHT)/2 + 0*CELL_HEIGHT + GAME_Y,
          I8_SCREEN_WIDTH/2 + 3*CELL_WIDTH + GAME_X, (I8_SCREEN_HEIGHT - 3*CELL_HEIGHT)/2 + 1*CELL_HEIGHT + GAME_Y,
          I8_SCREEN_WIDTH/2 + 3*CELL_WIDTH + GAME_X, (I8_SCREEN_HEIGHT - 3*CELL_HEIGHT)/2 + 2*CELL_HEIGHT + GAME_Y
    };
    private final static int[] LIFTS_LAYOUT = new int[]{
          I8_SCREEN_WIDTH/2 - 3*CELL_WIDTH  + GAME_X, 
          I8_SCREEN_WIDTH/2 - 2*CELL_WIDTH  + GAME_X, 
          I8_SCREEN_WIDTH/2 + 1*CELL_WIDTH  + GAME_X, 
          I8_SCREEN_WIDTH/2 + 2*CELL_WIDTH  + GAME_X  
    };


    public MoveObject p_MoveObject;

    public static final int GAMEACTION_COURIER_EAT_HAMBURGER   = 0;
    public static final int GAMEACTION_COURIER_LIFT         = 1;
    public static final int GAMEACTION_COURIER_LEAVE_LIFT   = 2;
    public static final int GAMEACTION_COURIER_DEAD         = 3;
    public static final int GAMEACTION_MANAGER              = 4;
    public static final int GAMEACTION_MANAGER_LIFT         = 5;
    public static final int GAMEACTION_MANAGER_LEAVE_LIFT   = 6;
    public static final int GAMEACTION_MANAGER_EAT_HAMBURGER   = 7;
    public static final int GAMEACTION_MANAGER_AT_OFFICE    = 8;
    public static final int GAMEACTION_BAD_MANAGER_MEETING  = 9;
    public static final int GAMEACTION_GOOD_MANAGER_MEETING = 10;
    public static final int GAMEACTION_SENDER_SENT          = 11;
    public static final int GAMEACTION_RECEIVER_OK          = 12;
    public static final int GAMEACTION_HAMBURGER               = 13;
    public static final int GAMEACTION_CLERKS_GENERATED     = 14;

    private int i_timedelay;

    public int i_playerAttemptions;

    public int i_GameTimer;

    public int i_LastTime;
    public int i_GenerateDelay;

    public int i_DeliveryTimer;

    public int i_InitDeliveryTimer;

    public int i_LastLiftTime;
    public int i_LiftDelay;

    public int i_LastManagerTime;
    public int i_ManagerDelay;

    public int i_DocumentID;
    public int i_Clerks;
    public int i_MaximumClerks;

    public int i_ManagerY;
    public int i_ManagerLevel;

    private boolean b_ManagerMeeting;
    private boolean b_ManagerDisable;

    private int i_playerSpeed;

    public static final int LEVEL_EASY = 0;

    private static final int LEVEL_EASY_ATTEMPTIONS = 4;

    private static final int LEVEL_EASY_GENERATION = 130; 
    private static final int LEVEL_EASY_MANAGER_DELAY = 50; 
    private static final int LEVEL_EASY_ENGAGE_LIFT_DELAY = 44;

    private static final int LEVEL_EASY_MAX_CLERK = 3; 

    private static final int LEVEL_EASY_DELIVERY = 250;

    private static final int LEVEL_EASY_TIMEDELAY = 100;

    public static final int LEVEL_NORMAL = 1;

    private static final int LEVEL_NORMAL_ATTEMPTIONS = 4;

    private static final int LEVEL_NORMAL_GENERATION = 130;
    private static final int LEVEL_NORMAL_MANAGER_DELAY = 45; 
    private static final int LEVEL_NORMAL_ENGAGE_LIFT_DELAY = 22;

    private static final int LEVEL_NORMAL_MAX_CLERK = 4;

    private static final int LEVEL_NORMAL_DELIVERY = 220;

    private static final int LEVEL_NORMAL_TIMEDELAY = 90;

    public static final int LEVEL_HARD = 2;

    private static final int LEVEL_HARD_ATTEMPTIONS = 2;

    private static final int LEVEL_HARD_GENERATION = 120;
    private static final int LEVEL_HARD_MANAGER_DELAY = 40;  
    private static final int LEVEL_HARD_ENGAGE_LIFT_DELAY = 11;

    private static final int LEVEL_HARD_MAX_CLERK = 6;

    private static final int LEVEL_HARD_DELIVERY = 200; 

    private static final int LEVEL_HARD_TIMEDELAY = 80;

    private static final int SCORES_DELIVERY = 10;
    private static final int SCORES_HAMBURGER = 30;
    private static final int SCORES_MANAGER = 20;

    public Sprite pPlayerSprite;

    public Sprite pHamburger;

    public Sprite pManager;

    public static final int MAX_DOORS = 3+4+3;
    public static final int LIFTS_PER_WELL = (I8_SCREEN_HEIGHT + CELL_HEIGHT - 1) / CELL_HEIGHT;
    public static final int MAX_LIFTS = 4 * LIFTS_PER_WELL;
    public Sprite[] ap_LiftSprites;
    public Sprite[] ap_ClerksSprites;

    public static final int MAX_SPRITES_NUMBER = MAX_DOORS*2 + MAX_LIFTS;

    public Sprite[] ap_Sprites;

    private static final int SPRITEDATALENGTH = 10;

    public static final int SPRITE_COURIER = 0;
    public static final int SPRITE_COURIER_LEFT = 1;
    public static final int SPRITE_COURIER_RIGHT = 2;
    public static final int SPRITE_COURIER_LIFT = 3;
    public static final int SPRITE_COURIER_DEAD = 4;

    public static final int SPRITE_SENDER_APPEAR = 5;
    public static final int SPRITE_SENDER = 6;
    public static final int SPRITE_SENDER_DISAPPEAR = 7;
    public static final int SPRITE_RECEIVER_APPEAR = 8;
    public static final int SPRITE_RECEIVER = 9;
    public static final int SPRITE_RECEIVER_DISAPPEAR = 10;
    public static final int SPRITE_HAMBURGER = 11;
    public static final int SPRITE_MANAGER_APPEAR = 12;
    public static final int SPRITE_MANAGER = 13;
    public static final int SPRITE_MANAGER_DISAPPEAR = 14;
    public static final int SPRITE_MANAGER_LEFT = 15;
    public static final int SPRITE_MANAGER_RIGHT = 16;
    public static final int SPRITE_MANAGER_LIFT = 17;

    public static final int SPRITE_FLOOR = 18;
    public static final int SPRITE_LIFT_UP = 19;
    public static final int SPRITE_LIFT_DOWN = 20;


    private static final int [] ai_SpriteParameters = new int[]
    {
        0x0C00, 0x0A00, -1, -1, 0x0800, 0x0A00, 1, 1, Sprite.SPRITE_ALIGN_CENTER, Sprite.ANIMATION_FROZEN,
        0x0C00, 0x0A00, -1, -1, 0x0800, 0x0A00, 3, 1, Sprite.SPRITE_ALIGN_CENTER, Sprite.ANIMATION_PENDULUM,
        0x0C00, 0x0A00, -1, -1, 0x0800, 0x0A00, 3, 1, Sprite.SPRITE_ALIGN_CENTER, Sprite.ANIMATION_PENDULUM,
        0x0C00, 0x0A00, -1, -1, 0x0C00, 0x0A00, 1, 1, Sprite.SPRITE_ALIGN_CENTER, Sprite.ANIMATION_FROZEN,
        0x0C00, 0x0A00, -1, -1, 0x0400, 0x0A00, 1, 1, Sprite.SPRITE_ALIGN_CENTER, Sprite.ANIMATION_FROZEN,
        0x0C00, 0x1000, -1, -1, 0x0C00, 0x1000, 2, 2, Sprite.SPRITE_ALIGN_CENTER, Sprite.ANIMATION_FROZEN,
        0x0C00, 0x1000, -1, -1, 0x0C00, 0x1000, 2, 2, Sprite.SPRITE_ALIGN_CENTER, Sprite.ANIMATION_CYCLIC,
        0x0C00, 0x1000, -1, -1, 0x0C00, 0x1000, 2, 2, Sprite.SPRITE_ALIGN_CENTER, Sprite.ANIMATION_FROZEN,
        0x0C00, 0x1000, -1, -1, 0x0C00, 0x1000, 2, 2, Sprite.SPRITE_ALIGN_CENTER, Sprite.ANIMATION_FROZEN,
        0x0C00, 0x1000, -1, -1, 0x0C00, 0x1000, 2, 2, Sprite.SPRITE_ALIGN_CENTER, Sprite.ANIMATION_CYCLIC,
        0x0C00, 0x1000, -1, -1, 0x0C00, 0x1000, 2, 2, Sprite.SPRITE_ALIGN_CENTER, Sprite.ANIMATION_FROZEN,
        0x1000, 0x1000, -1, -1, 0x0800, 0x1000, 1, 1, Sprite.SPRITE_ALIGN_CENTER, Sprite.ANIMATION_FROZEN,
        0x0C00, 0x0A00, -1, -1, 0x0800, 0x0A00, 2, 2, Sprite.SPRITE_ALIGN_CENTER, Sprite.ANIMATION_FROZEN,
        0x0C00, 0x0A00, -1, -1, 0x0800, 0x0A00, 1, 1, Sprite.SPRITE_ALIGN_CENTER, Sprite.ANIMATION_FROZEN,
        0x0C00, 0x0A00, -1, -1, 0x0800, 0x0A00, 2, 2, Sprite.SPRITE_ALIGN_CENTER, Sprite.ANIMATION_FROZEN,
        0x0C00, 0x0A00, -1, -1, 0x0800, 0x0A00, 3, 1, Sprite.SPRITE_ALIGN_CENTER, Sprite.ANIMATION_PENDULUM,
        0x0C00, 0x0A00, -1, -1, 0x0800, 0x0A00, 3, 1, Sprite.SPRITE_ALIGN_CENTER, Sprite.ANIMATION_PENDULUM,
        0x0C00, 0x0A00, -1, -1, 0x0800, 0x0A00, 1, 1, Sprite.SPRITE_ALIGN_CENTER, Sprite.ANIMATION_FROZEN,
        CELL_WIDTH*2, 0x0400, -1, -1, CELL_WIDTH*2 -0x400, 0x0400, 1, 1, Sprite.SPRITE_ALIGN_CENTER, Sprite.ANIMATION_FROZEN,
        0x0C00, 0x0400, -1, -1, 0x0A00, 0x0200, 1, 1, Sprite.SPRITE_ALIGN_CENTER, Sprite.ANIMATION_FROZEN,
        0x0C00, 0x0400, -1, -1, 0x0A00, 0x0200, 1, 1, Sprite.SPRITE_ALIGN_CENTER, Sprite.ANIMATION_FROZEN,
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


        if(i8_cx < 0) i8_cx = (i8_w - i8_aw) >> 1;
        if(i8_cy < 0) i8_cy = (i8_h - i8_ah) >> 1;
        _sprite.setCollisionBounds(i8_cx, i8_cy, i8_aw, i8_ah);


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

            switch (p_Sprite.i_ObjectType)
            {
                case SPRITE_FLOOR:
                    if(p_Sprite.isCollided(pPlayerSprite) )
                    {
                        int lsy = p_Sprite.i_mainY - p_Sprite.i_height / 2;
                        int psh = pPlayerSprite.i_height / 2 ;
                        pPlayerSprite.setMainPointXY(pPlayerSprite.i_mainX, lsy - psh);
                    }
                    if(p_Sprite.isCollided(pManager))
                    {
                        int lsy = p_Sprite.i_mainY - p_Sprite.i_height / 2;
                        int psh = pManager.i_height / 2 ;
                        pManager.setMainPointXY(pManager.i_mainX, lsy - psh);
                    }
                    break;
                case SPRITE_LIFT_UP:

                    p_Sprite.setMainPointXY(p_Sprite.i_mainX, p_Sprite.i_mainY - LIFT_SPEED);
                    if(p_Sprite.i_mainY <= GAME_Y)
                    {
                        p_Sprite.setMainPointXY(p_Sprite.i_mainX, LIFTS_PER_WELL*CELL_HEIGHT+GAME_Y);

                        if((i_LastLiftTime + i_LiftDelay) < i_GameTimer )
                        {
                            if(getRandomInt(2) == 1)                    
                            {
                                p_Sprite.i_ObjectState = 1;
                                i_LastLiftTime = i_GameTimer;
                            }
                            else
                            {
                                p_Sprite.i_ObjectState = 0;
                            }
                        }
                        else
                        {
                            p_Sprite.i_ObjectState = 0;
                        }
                    }
                    if(p_Sprite.isCollided(pPlayerSprite))
                    {
                        int lsy = p_Sprite.i_mainY - (p_Sprite.i_height >> 1);
                        int psh = pPlayerSprite.i_height >> 1 ;
                        pPlayerSprite.setMainPointXY(pPlayerSprite.i_mainX, lsy - psh);
                        p_Sprite.i_ObjectState |= PLAYER_ON_LIFT;
                    }
                    else
                    {
                        p_Sprite.i_ObjectState &= ~PLAYER_ON_LIFT;
                    }
                    if(p_Sprite.isCollided(pManager))
                    {
                        int lsy = p_Sprite.i_mainY - (p_Sprite.i_height >> 1);
                        int psh = pManager.i_height >> 1 ;
                        if(pManager.i_ObjectType != SPRITE_MANAGER_LIFT)
                        {
                            activateSprite(pManager, SPRITE_MANAGER_LIFT);
                            pManager.i_ObjectState = MANAGER_STATE_UP;
                            m_pAbstractGameActionListener.processGameAction(GAMEACTION_MANAGER_LIFT);
                        }
                        pManager.setMainPointXY(p_Sprite.i_mainX, lsy - psh);
                        p_Sprite.i_ObjectState |= MANAGER_ON_LIFT;
                    }
                    else
                    {
                        p_Sprite.i_ObjectState &= ~MANAGER_ON_LIFT;
                    }
                    break;
                case SPRITE_LIFT_DOWN:

                    p_Sprite.setMainPointXY(p_Sprite.i_mainX, p_Sprite.i_mainY + LIFT_SPEED);
                    if(p_Sprite.i_mainY >= LIFTS_PER_WELL*CELL_HEIGHT+GAME_Y )
                    {
                        p_Sprite.setMainPointXY(p_Sprite.i_mainX, GAME_Y);

                        if((i_LastLiftTime + i_LiftDelay) < i_GameTimer )
                        {
                            if(getRandomInt(2) == 1)              
                            {
                                p_Sprite.i_ObjectState = 1;
                                i_LastLiftTime = i_GameTimer;
                            }
                            else
                            {
                                p_Sprite.i_ObjectState = 0;
                            }
                        }
                        else
                        {
                            p_Sprite.i_ObjectState = 0;
                        }
                    }
                    if(p_Sprite.isCollided(pPlayerSprite))
                    {
                        int lsy = p_Sprite.i_mainY - p_Sprite.i_height / 2;
                        int psh = pPlayerSprite.i_height / 2 ;
                        pPlayerSprite.setMainPointXY(pPlayerSprite.i_mainX, lsy - psh);
                        p_Sprite.i_ObjectState |= PLAYER_ON_LIFT;
                    }
                    else
                    {
                        p_Sprite.i_ObjectState &= ~PLAYER_ON_LIFT;
                    }
                    if(p_Sprite.isCollided(pManager))
                    {
                        int lsy = p_Sprite.i_mainY - p_Sprite.i_height / 2;
                        int psh = pManager.i_height / 2 ;
                        if(pManager.i_ObjectType != SPRITE_MANAGER_LIFT)
                        {
                            activateSprite(pManager, SPRITE_MANAGER_LIFT);
                            pManager.i_ObjectState = MANAGER_STATE_DOWN;
                            m_pAbstractGameActionListener.processGameAction(GAMEACTION_MANAGER_LIFT);
                        }
                        pManager.setMainPointXY(p_Sprite.i_mainX, lsy - psh);
                        p_Sprite.i_ObjectState |= MANAGER_ON_LIFT;
                    }
                    else
                    {
                        p_Sprite.i_ObjectState &= ~MANAGER_ON_LIFT;
                    }
                    break;
                case SPRITE_SENDER_APPEAR:
                    if(p_Sprite.processAnimation())
                    {
                      activateSprite(p_Sprite, SPRITE_SENDER);
                    }
                    break;
                case SPRITE_SENDER:
                    p_Sprite.processAnimation();
                    if(i_DocumentID == 0)
                    {
                        if(p_Sprite.isCollided(pPlayerSprite) && (pPlayerSprite.i_ObjectType != SPRITE_COURIER_DEAD))
                        {
                            i_DeliveryTimer = i_InitDeliveryTimer;
                            m_pAbstractGameActionListener.processGameAction(GAMEACTION_SENDER_SENT);
                            i_DocumentID = p_Sprite.i_ObjectState;
                            activateSprite(p_Sprite, SPRITE_SENDER_DISAPPEAR);
                            i_Clerks--;
                        }
                    }
                    if(pHamburger.lg_SpriteActive && p_Sprite.isCollided(pHamburger))
                    {
                        deactivateSprite(pHamburger);
                        pHamburger.i_ObjectState = HAMBURGER_DELAY;
                    }
                    break;
                case SPRITE_SENDER_DISAPPEAR:
                    if(p_Sprite.processAnimation())
                    {
                           deactivateSprite(p_Sprite);
                    }
                    break;
                case SPRITE_RECEIVER_APPEAR:
                    if(p_Sprite.processAnimation())
                    {
                           activateSprite(p_Sprite, SPRITE_RECEIVER);
                    }
                    break;
                case SPRITE_RECEIVER:
                    p_Sprite.processAnimation();
                    if(i_DocumentID == p_Sprite.i_ObjectState)
                    {
                        if(p_Sprite.isCollided(pPlayerSprite))
                        {
                            i_LastTime = i_GameTimer + i_GenerateDelay / 8;
                            i_DeliveryTimer = i_InitDeliveryTimer;
                            m_pAbstractGameActionListener.processGameAction(GAMEACTION_RECEIVER_OK);
                            addScore(SCORES_DELIVERY);
                            i_DocumentID = 0;
                            activateSprite(p_Sprite, SPRITE_RECEIVER_DISAPPEAR);
                            i_Clerks--;
                        }
                    }
                    if(p_Sprite.isCollided(pHamburger) && pHamburger.lg_SpriteActive)
                    {
                        deactivateSprite(pHamburger);
                        pHamburger.i_ObjectState = HAMBURGER_DELAY;
                    }
                    break;
                case SPRITE_RECEIVER_DISAPPEAR:
                    if(p_Sprite.processAnimation())
                    {
                           deactivateSprite(p_Sprite);
                    }
                    break;
                default:
                    break;
            }
        }
    }

    private void processManager()
    {
        if(pManager.lg_SpriteActive)
        {
            if(pManager.processAnimation())
            {
                switch(pManager.i_ObjectType)
                {
                case SPRITE_MANAGER_APPEAR:
                    {
                        activateSprite(pManager, SPRITE_MANAGER_RIGHT);
                    }
                    break;
                case SPRITE_MANAGER_DISAPPEAR:
                    {
                           deactivateSprite(pManager);
                           i_LastManagerTime = i_GameTimer;
                           m_pAbstractGameActionListener.processGameAction(GAMEACTION_MANAGER_AT_OFFICE);
                    }
                    break;
                }
            }

            switch(pManager.i_ObjectType)
            {
                case SPRITE_MANAGER_APPEAR:  return;

                case SPRITE_MANAGER:
                    {
                        if(pManager.i_mainY < BOTTOM_FIRST_CENTER_DOOR)
                        {
                            if(canMoveRight(pManager))
                            {
                                activateSprite(pManager, SPRITE_MANAGER_RIGHT);
                                pManager.i_ObjectState = MANAGER_STEPS;
                            }
                        }
                        else
                        {
                           if(canMoveLeft(pManager))
                            {
                                activateSprite(pManager, SPRITE_MANAGER_LEFT);
                                pManager.i_ObjectState = MANAGER_STEPS;
                            }
                        }
                    }
                    break;
                case SPRITE_MANAGER_DISAPPEAR:  return;

                case SPRITE_MANAGER_RIGHT:
                    if(pManager.i_ObjectState > 0)
                    {
                        pManager.setMainPointXY(pManager.i_mainX + MANAGER_SPEED, pManager.i_mainY);
                        pManager.i_ObjectState--;
                    }
                    else
                    {
                        activateSprite(pManager, SPRITE_MANAGER);
                    }
                    break;
                case SPRITE_MANAGER_LEFT:
                    if(pManager.i_ObjectState > 0)
                    {
                        pManager.setMainPointXY(pManager.i_mainX - MANAGER_SPEED, pManager.i_mainY);
                        pManager.i_ObjectState--;
                    }
                    else
                    {
                        activateSprite(pManager, SPRITE_MANAGER);
                    }
                    break;
                case SPRITE_MANAGER_LIFT:
                    if(pManager.i_ObjectState == MANAGER_STATE_DOWN)
                    {
                        if(pManager.i_mainY >= i_ManagerY)
                        {
                            if(canMoveLeft(pManager))
                            {
                                activateSprite(pManager, SPRITE_MANAGER_LEFT);
                                pManager.i_ObjectState = 6;
                                pManager.setMainPointXY(pManager.i_mainX - 0x0C00, pManager.i_mainY);
                                m_pAbstractGameActionListener.processGameAction(GAMEACTION_MANAGER_LEAVE_LIFT);
                            }
                        }
                    }
                    if(pManager.i_ObjectState == MANAGER_STATE_UP)
                    {
                        if(pManager.i_mainY < BOTTOM_FIRST_CENTER_DOOR)
                        {
                            if(canMoveRight(pManager))
                            {
                                activateSprite(pManager, SPRITE_MANAGER_RIGHT);
                                pManager.i_ObjectState = MANAGER_STEPS;
                                pManager.setMainPointXY(pManager.i_mainX + 0x0C00, pManager.i_mainY);
                                m_pAbstractGameActionListener.processGameAction(GAMEACTION_MANAGER_LEAVE_LIFT);
                            }
                        }
                    }
                    break;
            }

              if(!b_ManagerDisable && pManager.isCollided(pPlayerSprite) && (pPlayerSprite.i_ObjectType != SPRITE_COURIER_DEAD) )
              {
                  if(i_DocumentID == 0)
                  {
                      activateSprite(pManager, SPRITE_MANAGER);
                      activateSprite(pPlayerSprite, SPRITE_COURIER_DEAD);
                      m_pAbstractGameActionListener.processGameAction(GAMEACTION_BAD_MANAGER_MEETING);
                      m_pAbstractGameActionListener.processGameAction(GAMEACTION_COURIER_DEAD);
                      b_ManagerDisable = true;
                  }
                  else
                  {
                      if(!b_ManagerMeeting)
                      {
                          addScore(SCORES_MANAGER);
                          b_ManagerMeeting = true;
                          m_pAbstractGameActionListener.processGameAction(GAMEACTION_GOOD_MANAGER_MEETING);
                      }
                  }
              }
              if( (pManager.i_mainY < BOTTOM_FIRST_CENTER_DOOR) && (pManager.i_mainX > I8_SCREEN_WIDTH / 2 - 0x0400) && (pManager.i_mainX < I8_SCREEN_WIDTH / 2) )
              {
                    if(b_ManagerDisable)
                    {
                        activateSprite(pManager, SPRITE_MANAGER);
                    }
                      else
                           {
                              activateSprite(pManager, SPRITE_MANAGER_DISAPPEAR);
                           }
              }
             pManager.setMainPointXY(pManager.i_mainX, pManager.i_mainY + 0x0200);
        }
        else
        {
            if((i_LastManagerTime + i_ManagerDelay) < i_GameTimer )
            {
                m_pAbstractGameActionListener.processGameAction(GAMEACTION_MANAGER);
                b_ManagerMeeting = false;
                b_ManagerDisable = false;
                activateSprite(pManager, SPRITE_MANAGER_APPEAR);
                pManager.i_ObjectState = 3;
                pManager.setMainPointXY(I8_SCREEN_WIDTH / 2, BOTTOM_FIRST_CENTER_DOOR - (pManager.i_height>>1));

                switch(i_ManagerLevel)
                {
                    case 1:
                        i_ManagerY = BOTTOM_FIRST_CENTER_DOOR + 0*CELL_HEIGHT;
                        break;
                    case 2:
                        i_ManagerY = BOTTOM_FIRST_CENTER_DOOR + 1*CELL_HEIGHT;
                        break;
                    case 3:
                        i_ManagerY = BOTTOM_FIRST_CENTER_DOOR + 2*CELL_HEIGHT;
                        break;
                    default:
                        i_ManagerLevel = 1;
                        i_ManagerY = BOTTOM_FIRST_CENTER_DOOR + 0*CELL_HEIGHT;
                        break;
                }
                i_ManagerLevel++;
                if(i_ManagerLevel > 3)
                {
                    i_ManagerLevel = 1;
                }

            }
        }
    }


    private void processBurger()
    {
        if(pHamburger.lg_SpriteActive)
        {
            if(pHamburger.processAnimation())
            {
                if(pHamburger.isCollided(pPlayerSprite))
                {
                    addScore(SCORES_HAMBURGER);
                    deactivateSprite(pHamburger);
                    pHamburger.i_ObjectState = HAMBURGER_DELAY;
                    m_pAbstractGameActionListener.processGameAction(GAMEACTION_COURIER_EAT_HAMBURGER);
                }
                if(pHamburger.isCollided(pManager) && pManager.lg_SpriteActive)
                {
                    deactivateSprite(pHamburger);
                    pHamburger.i_ObjectState = HAMBURGER_DELAY;
                    m_pAbstractGameActionListener.processGameAction(GAMEACTION_MANAGER_EAT_HAMBURGER);
                }
            }
        }
        else
        {
            if(pHamburger.i_ObjectState > 0)
            {
                pHamburger.i_ObjectState--;
            }
            else
            {
                int a = findDoor(getRandomInt(7));
                if(a>=0)
                {
                  activateSprite(pHamburger, SPRITE_HAMBURGER);
                  pHamburger.lg_SpriteActive = true;
                  attachSpriteToDoor(pHamburger,a,true);
                  m_pAbstractGameActionListener.processGameAction(GAMEACTION_HAMBURGER);
                }
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
                case SPRITE_COURIER_DEAD:
                    return false;
                default:
                    break;
            }
        }
        switch(pPlayerSprite.i_ObjectType)
        {
            case SPRITE_COURIER:
                if ((_buttonState & MoveObject.BUTTON_LEFT) != 0)
                {
                    if(canMoveLeft(pPlayerSprite))
                    {
                            activateSprite(pPlayerSprite, SPRITE_COURIER_LEFT);
                            i_playerSpeed = (pPlayerSprite.i_TTL == 0) ? PLAYER_HSPEED : PLAYER_HI_HSPEED;
                            pPlayerSprite.i_ObjectState = CELL_WIDTH / i_playerSpeed;
                    }
                }
                else if ((_buttonState & MoveObject.BUTTON_RIGHT) != 0)
                {
                    if(canMoveRight(pPlayerSprite))
                    {
                            activateSprite(pPlayerSprite, SPRITE_COURIER_RIGHT);
                            i_playerSpeed = (pPlayerSprite.i_TTL == 0) ? PLAYER_HSPEED : PLAYER_HI_HSPEED;
                            pPlayerSprite.i_ObjectState = CELL_WIDTH / i_playerSpeed;
                    }
                }
                break;
           case SPRITE_COURIER_LEFT:
                if(pPlayerSprite.i_ObjectState > 0)
                {
                    pPlayerSprite.setMainPointXY(i_mx - i_playerSpeed, i_my);
                    pPlayerSprite.i_ObjectState--;
                }
                else
                {


                    {
                         activateSprite(pPlayerSprite, SPRITE_COURIER);
                    }
                }
                break;
           case SPRITE_COURIER_RIGHT:
                if(pPlayerSprite.i_ObjectState > 0)
                {
                    pPlayerSprite.setMainPointXY(i_mx + i_playerSpeed, i_my);
                    pPlayerSprite.i_ObjectState--;
                }
                else
                {

                    {
                         activateSprite(pPlayerSprite, SPRITE_COURIER);
                    }
                }
                break;
        }

        if(i_DeliveryTimer > 0)
        {
            if(i_Clerks > 0)
            {
                i_DeliveryTimer--;
            }
        }
        else
        {
            if(pPlayerSprite.i_ObjectType != SPRITE_COURIER_DEAD)
            {
                activateSprite(pPlayerSprite, SPRITE_COURIER_DEAD);
                m_pAbstractGameActionListener.processGameAction(GAMEACTION_COURIER_DEAD);
            }
        }

        if(pPlayerSprite.i_ObjectType != SPRITE_COURIER_DEAD && (((i_y + i_h) >= (I8_SCREEN_HEIGHT - GAME_Y)) || ((i_y + i_h - CELL_HEIGHT/2) <= GAME_Y)) )
        {
            activateSprite(pPlayerSprite, SPRITE_COURIER_DEAD);
            m_pAbstractGameActionListener.processGameAction(GAMEACTION_COURIER_DEAD);
        }

        pPlayerSprite.setMainPointXY(pPlayerSprite.i_mainX, pPlayerSprite.i_mainY + PLAYER_VSPEED);
        return true;
    }

   int leftFloorColectionRanges[]=
   {
       0,-1, 
       0,0,  
       4,3,  
       MAX_DOORS*2 + 0*LIFTS_PER_WELL , LIFTS_PER_WELL,
       MAX_DOORS*2 + 1*LIFTS_PER_WELL , LIFTS_PER_WELL,
       0,0,  
       0,4,  
       MAX_DOORS*2 + 2*LIFTS_PER_WELL , LIFTS_PER_WELL,
       MAX_DOORS*2 + 3*LIFTS_PER_WELL , LIFTS_PER_WELL,
       0,0   
   };
   private boolean canMoveLeft(Sprite p_spr)
   {
     int section = (p_spr.i_mainX - (I8_SCREEN_WIDTH - CELL_WIDTH*10 >> 1)) / CELL_WIDTH;
     if(section >=10 || section <= 0) return false;

     Sprite p_floor;
     int offset = leftFloorColectionRanges[section<<1];
     int target = offset + leftFloorColectionRanges[(section<<1)+1];

     section = p_spr.i_ScreenY + p_spr.i_height;

     if(offset == target)
     {
        p_spr.i_TTL = 0;
        return true;
     }

     for(;offset < target; offset++)
     {
         p_floor = ap_Sprites[offset];
         if(p_floor.i_ObjectState == 0 && Math.abs(section - p_floor.i_ScreenY) <= 0x100)
         {
             p_spr.i_TTL = -1;
             return true;
         }
     }
     return false;
   }
   int rightFloorColectionRanges[]=
   {
       0,0,  
       MAX_DOORS*2 + 0*LIFTS_PER_WELL , LIFTS_PER_WELL,
       MAX_DOORS*2 + 1*LIFTS_PER_WELL , LIFTS_PER_WELL,
       0,4,  
       0,0,  
       MAX_DOORS*2 + 2*LIFTS_PER_WELL , LIFTS_PER_WELL,
       MAX_DOORS*2 + 3*LIFTS_PER_WELL , LIFTS_PER_WELL,
       4+3,3,
       0,0,  
       0,-1  
   };
   private boolean canMoveRight(Sprite p_spr)
   {
     int section = (p_spr.i_mainX - (I8_SCREEN_WIDTH - CELL_WIDTH*10 >> 1)) / CELL_WIDTH;
     if(section >=9 || section < 0) return false;

     Sprite p_floor;
     int offset = rightFloorColectionRanges[section<<1];
     int target = offset + rightFloorColectionRanges[(section<<1)+1];

     section = p_spr.i_ScreenY + p_spr.i_height;

     if(offset == target)
     {
         p_spr.i_TTL = 0;
         return true;
     }

     for(;offset < target; offset++)
     {
         p_floor = ap_Sprites[offset];
         if(p_floor.i_ObjectState == 0 && Math.abs(section - p_floor.i_ScreenY) <= 0x100)
         {
             p_spr.i_TTL = -1;
             return true;
         }
     }
     return false;
   }


    private void generateClerks()
    {
        if((i_LastTime + i_GenerateDelay) < i_GameTimer )
        {
            if(i_Clerks <= i_MaximumClerks)
            {
                i_LastTime = i_GameTimer;

                int sender = findDoor(1+getRandomInt(8));
                if(sender <= 0) return;

                Sprite p_src = ap_ClerksSprites[sender];
                activateSprite(p_src, SPRITE_SENDER_APPEAR);

                int receiver = findDoor(1+getRandomInt(8));

                if(receiver <= 0)
                {
                    deactivateSprite(p_src);
                    return;
                }

                int id = ((sender << 4) & 0xF0) | (receiver & 0x0F);

                p_src.setMainPointXY(ap_Sprites[sender].i_mainX,ap_Sprites[sender].i_ScreenY - p_src.i_height/2);
                p_src.i_ObjectState = id;
                i_Clerks++;

                Sprite p_dst = ap_ClerksSprites[receiver];
                activateSprite(p_dst, SPRITE_RECEIVER_APPEAR);
                p_dst.setMainPointXY(ap_Sprites[receiver].i_mainX,ap_Sprites[receiver].i_ScreenY - p_dst.i_height/2);
                p_dst.i_ObjectState = id;
                i_Clerks++;

                m_pAbstractGameActionListener.processGameAction(GAMEACTION_CLERKS_GENERATED);
            }
        }
    }

    private int findDoor(int n)
    {
        for(int i=n; i<MAX_DOORS; i++)
        {
            if(!ap_ClerksSprites[i].lg_SpriteActive)
            {
                return i;
            }
        }
        for(int i=0; i<=n; i++)
        {
            if(!ap_ClerksSprites[i].lg_SpriteActive)
            {
                return i;
            }
        }
        return -1;
    }


    private void attachSpriteToDoor(Sprite p_spr,int door, boolean lg_alignCenter)
    {
        Sprite p_door = ap_Sprites[door];
        int x = lg_alignCenter ? p_door.i_mainX : p_door.i_ScreenX + (p_spr.i_width>>1);
        int y = p_door.i_ScreenY + p_door.i_col_offsetY - (p_spr.i_height>>1);
        p_spr.setMainPointXY(x,y);
    }

    private boolean resetLevel()
    {
        i_GameTimer = 0;
        i_LastTime = i_GameTimer;
        i_LastLiftTime = i_GameTimer;
        i_LastManagerTime = i_GameTimer;
        i_DeliveryTimer = i_InitDeliveryTimer;
        i_playerSpeed = PLAYER_HSPEED;

        i_DocumentID = 0;
        i_Clerks = 0;

        i_ManagerLevel = 1;
        b_ManagerMeeting = false;
        b_ManagerDisable = false;

        activateSprite(pHamburger, SPRITE_HAMBURGER);
        deactivateSprite(pHamburger);
        pHamburger.i_ObjectState = 0;

        activateSprite(pManager, SPRITE_MANAGER);
        attachSpriteToDoor(pManager,0,true);
        deactivateSprite(pManager);

        activateSprite(pPlayerSprite, SPRITE_COURIER);
        attachSpriteToDoor(pPlayerSprite,INIT_PLAYER_DOOR,false);
        return true;
    }


public boolean newGameSession(int _gameAreaWidth, int _gameAreaHeight, int _gameLevel, CAbstractPlayer[] _players,startup _listener, String _staticArrayResourceName)
    {
        super.newGameSession(_gameAreaWidth, _gameAreaHeight, _gameLevel, _players, _listener, _staticArrayResourceName);


        switch (_gameLevel)
        {
            case LEVEL_EASY:
                {
                    i_timedelay = LEVEL_EASY_TIMEDELAY;
                    i_playerAttemptions = LEVEL_EASY_ATTEMPTIONS;
                    i_GenerateDelay = LEVEL_EASY_GENERATION;
                    i_LiftDelay = LEVEL_EASY_ENGAGE_LIFT_DELAY;
                    i_ManagerDelay = LEVEL_EASY_MANAGER_DELAY;
                    i_InitDeliveryTimer = LEVEL_EASY_DELIVERY;
                    i_MaximumClerks = LEVEL_EASY_MAX_CLERK;
                }
                break;
            case LEVEL_NORMAL:
                {
                    i_timedelay = LEVEL_NORMAL_TIMEDELAY;
                    i_playerAttemptions = LEVEL_NORMAL_ATTEMPTIONS;
                    i_GenerateDelay = LEVEL_NORMAL_GENERATION;
                    i_LiftDelay = LEVEL_NORMAL_ENGAGE_LIFT_DELAY;
                    i_ManagerDelay = LEVEL_NORMAL_MANAGER_DELAY;
                    i_InitDeliveryTimer = LEVEL_NORMAL_DELIVERY;
                    i_MaximumClerks = LEVEL_NORMAL_MAX_CLERK;
                }
                break;
            case LEVEL_HARD:
                {
                    i_timedelay = LEVEL_HARD_TIMEDELAY;
                    i_playerAttemptions = LEVEL_HARD_ATTEMPTIONS;
                    i_GenerateDelay = LEVEL_HARD_GENERATION;
                    i_LiftDelay = LEVEL_HARD_ENGAGE_LIFT_DELAY;
                    i_ManagerDelay = LEVEL_HARD_MANAGER_DELAY;
                    i_InitDeliveryTimer = LEVEL_HARD_DELIVERY;
                    i_MaximumClerks = LEVEL_HARD_MAX_CLERK;
                }
                break;
        }

        resetLevel();

        return true;
    }

    public void resumeGameAfterPlayerLostOrPaused()
    {
        super.resumeGameAfterPlayerLostOrPaused();

        deinitState();
        initState();

        resetLevel();

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
        return "COURIER";
    }

    public boolean initState()
    {
        int li,n,i_x,i_y;
        ap_Sprites = new Sprite[MAX_SPRITES_NUMBER];
        ap_LiftSprites = new Sprite[MAX_LIFTS];
        ap_ClerksSprites = new Sprite[MAX_DOORS];
        for (li = 0; li < MAX_SPRITES_NUMBER; li++) ap_Sprites[li] = new Sprite(li);

        for(li = 0; li < MAX_DOORS; li++)
        {
           activateSprite(ap_Sprites[li], SPRITE_FLOOR);
           i_x = DOORS_LAYOUT[li<<1] + ap_Sprites[li].i_width/2;
           i_y = DOORS_LAYOUT[(li<<1)+1] + CELL_HEIGHT - ap_Sprites[li].i_height/2 ;
           ap_Sprites[li].setMainPointXY(i_x, i_y);
           ap_Sprites[li].i_ObjectState = 0;
           ap_ClerksSprites[li] = ap_Sprites[li+MAX_DOORS];
        }

        ap_LiftSprites = new Sprite[MAX_LIFTS];
        for(li = 0; li < MAX_LIFTS; li++)
        {
            n = li / LIFTS_PER_WELL;
            ap_LiftSprites[li] = ap_Sprites[li + MAX_DOORS*2];
            activateSprite(ap_LiftSprites[li], (n & 1) == 0 ? SPRITE_LIFT_DOWN : SPRITE_LIFT_UP);
            i_x = LIFTS_LAYOUT[n] + ap_LiftSprites[li].i_width/2;
            i_y = (li % LIFTS_PER_WELL) * CELL_HEIGHT + ap_LiftSprites[li].i_height/2;
            ap_LiftSprites[li].setMainPointXY(i_x, i_y);
            ap_Sprites[li].i_ObjectState = 0;
        }

        pPlayerSprite = new Sprite(-1);
        pHamburger = new Sprite(-2);
        pManager = ap_ClerksSprites[0];

        p_MoveObject = new MoveObject();

        return true;
    }

    public void deinitState()
    {
        ap_Sprites = null;
        ap_LiftSprites = null;
        ap_ClerksSprites = null;
        pPlayerSprite = null;
        pHamburger = null;
        pManager = null;

        Runtime.getRuntime().gc();
    }

    public int _getMaximumDataSize()
    {
        return (MAX_SPRITES_NUMBER+3)*(Sprite.DATASIZE_BYTES+1)+74;
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

        _outputStream.writeByte(pHamburger.i_ObjectType);
        pHamburger.writeSpriteToStream(_outputStream);


        _outputStream.writeInt(i_playerAttemptions);
        _outputStream.writeInt(i_GameTimer);
        _outputStream.writeInt(i_LastTime);
        _outputStream.writeInt(i_GenerateDelay);

        _outputStream.writeInt(i_LastLiftTime);
        _outputStream.writeInt(i_LiftDelay);

        _outputStream.writeInt(i_LastManagerTime);
        _outputStream.writeInt(i_ManagerDelay);

        _outputStream.writeInt(i_DocumentID);
        _outputStream.writeInt(i_Clerks);
        _outputStream.writeInt(i_DeliveryTimer);

        _outputStream.writeInt(i_ManagerY);
        _outputStream.writeInt(i_ManagerLevel);
        _outputStream.writeBoolean(b_ManagerMeeting);
        _outputStream.writeBoolean(b_ManagerDisable);

        _outputStream.writeShort(i_playerSpeed);
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
        activateSprite(pHamburger,i_type);
        pHamburger.readSpriteFromStream(_inputStream);


        pManager = ap_ClerksSprites[0];

        i_playerAttemptions = _inputStream.readInt();
        i_GameTimer = _inputStream.readInt();
        i_LastTime = _inputStream.readInt();
        i_GenerateDelay = _inputStream.readInt();

        i_LastLiftTime = _inputStream.readInt();
        i_LiftDelay = _inputStream.readInt();

        i_LastManagerTime = _inputStream.readInt();
        i_ManagerDelay = _inputStream.readInt();

        i_DocumentID = _inputStream.readInt();
        i_Clerks = _inputStream.readInt();
        i_DeliveryTimer = _inputStream.readInt();

        i_ManagerY = _inputStream.readInt();
        i_ManagerLevel = _inputStream.readInt();
        b_ManagerMeeting = _inputStream.readBoolean();
        b_ManagerDisable = _inputStream.readBoolean();

        i_playerSpeed = _inputStream.readShort();
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
        processManager();

        processSprites();
        processBurger();

        generateClerks();



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
