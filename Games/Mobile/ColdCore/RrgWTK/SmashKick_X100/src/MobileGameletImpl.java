
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.DataInputStream;

public class MobileGameletImpl extends MobileGamelet
{
    private static final int BONUS_BALLS = 10;
    private static final int MAX_ATTEMPTIONS = 4;
    private static final int BORED_DELAY = 25;


    private static final int GRAVITY = 0x80;

    private static final int REFLECTION_RATE= 0x90;

    public static final int LAND_LINE = (110) << 8;

    private static final int INIT_PLAYER_Y = LAND_LINE - (15<<8);
    private static final int INIT_PLAYER_X = (40) << 8;

    public static final int GATEKICK_LEFT_X = (80) << 8;
    public static final int GATEKICK_RIGHT_X = (118) << 8;

    public static final int GATE_X = (118) << 8;
    public static final int GATE_Y = (128-34) << 8;

    public static final int BALL_X = (13) << 8;
    public static final int BALL_Y = LAND_LINE - ((10) << 8);

    public static final int BALL_SPEED = (1) << 8;

    public static final int BALL_VSPEED00 = (-3) << 8;
    public static final int BALL_VSPEED01 = (-5) << 8;
    public static final int BALL_VSPEED02 = (-8) << 8;
    public static final int BALL_VSPEED03 = (-10) << 8;
    public static final int BALL_HSPEED00 = (BALL_SPEED); 
    public static final int BALL_HSPEED01 = (BALL_SPEED);
    public static final int BALL_HSPEED02 = (BALL_SPEED);
    public static final int BALL_HSPEED03 = (BALL_SPEED);

    public static final int BALL_VSPEED1 = (-5) << 8;
    public static final int BALL_VSPEED2 = (-6) << 8;
    public static final int BALL_VSPEED3 = (-7) << 8;
    public static final int BALL_VSPEED4 = (-1) << 8;
    public static final int BALL_HSPEED1 = (BALL_SPEED);
    public static final int BALL_HSPEED2 = (BALL_SPEED);
    public static final int BALL_HSPEED3 = (BALL_SPEED);
    public static final int BALL_HSPEED4 = (4) << 8;

    public static final int BALL_X_OFFSET1 = (0) << 8;
    public static final int BALL_Y_OFFSET1 = (-25) << 8;
    public static final int BALL_X_OFFSET2 = (13) << 8;
    public static final int BALL_Y_OFFSET2 = (0) << 8;
    public static final int BALL_X_OFFSET3 = (-12) << 8;
    public static final int BALL_Y_OFFSET3 = (-7) << 8;
    public static final int BALL_X_OFFSET4 = (13) << 8;
    public static final int BALL_Y_OFFSET4 = (0) << 8;

    public static final int COACH_X = (8) << 8;
    public static final int COACH_Y = (INIT_PLAYER_Y);

    public static final int FOOTBALLER_MOVE_SPEED = 0x0300; 

    public static final int LEFT_MOVE_MARGIN = (28) << 8;
    public static final int RIGHT_MOVE_MARGIN = (28) << 8;

    private static final int LEVEL_EASY_TIMEDELAY = 100;

    private static final int LEVEL_EASY_LIVES = 4;

    private static final int LEVEL_EASY_BALLFREQ = 50;

    private static final int LEVEL_NORMAL_TIMEDELAY = 90;

    private static final int LEVEL_NORMAL_LIVES = 3;

    private static final int LEVEL_NORMAL_BALLFREQ = 40;

    private static final int LEVEL_HARD_TIMEDELAY = 80;

    private static final int LEVEL_HARD_LIVES = 2;

    private static final int LEVEL_HARD_BALLFREQ = 30;

    private static final int REMOVE_BALL_TIME = 10;

    private static final int SCORES_TOUCH1 = 5;
    private static final int SCORES_TOUCH2 = 10;
    private static final int SCORES_TOUCH3 = 15;
    private static final int SCORES_TOUCH4 = 10;
    private static final int SCORES_GOAL = 25;

    private static final int SPRITEDATALENGTH = 8;

    public static final int SPRITE_FOOTBALLER               = 0;
    public static final int SPRITE_FOOTBALLER_LEFT          = 1;
    public static final int SPRITE_FOOTBALLER_RIGHT         = 2;
    public static final int SPRITE_FOOTBALLER_HEAD_KICK     = 3;
    public static final int SPRITE_FOOTBALLER_OVERHEAD_KICK = 4;
    public static final int SPRITE_FOOTBALLER_RIGHT_KICK    = 5;
    public static final int SPRITE_FOOTBALLER_GATE_KICK     = 6;
    public static final int SPRITE_FOOTBALLER_HAPPY         = 7;
    public static final int SPRITE_FOOTBALLER_SAD           = 8;
    public static final int SPRITE_FOOTBALLER_BORED         = 9;

    public static final int SPRITE_BALL                     = 10;
    public static final int SPRITE_COACH                    = 11;
    public static final int SPRITE_COACH_KICK               = 12;
    public static final int SPRITE_COACH_AFTER_KICK         = 13;
    public static final int SPRITE_GATE                     = 14;

    public static final int MAX_SPRITES = 8;

    public static final int GAMEACTION_FOOTBALLER_KICK      = 0;
    public static final int GAMEACTION_FOOTBALLER_KICKED    = 1;
    public static final int GAMEACTION_FOOTBALLER_HAPPY     = 2;
    public static final int GAMEACTION_FOOTBALLER_SAD       = 3;
    public static final int GAMEACTION_FOOTBALLER_BORED     = 4;
    public static final int GAMEACTION_COACH_KICK           = 5;
    public static final int GAMEACTION_COACH_KICKED         = 6;
    public static final int GAMEACTION_BALL_STOPED          = 7;
    public static final int GAMEACTION_ADD_BONUS_LIVE       = 8;


    public int m_iPlayerAttemptions;

    private int i_timedelay;

    public int i_GameTimer;

    private int i_borndelay;
    public  int i_countdown;

    public int i_BallCounter;

    private int i_BallHSpeed = 0;
    private int i_BallVSpeed = 0;
    private boolean b_Goal;
    private boolean b_Landed;




    public Sprite pPlayerFootballer;

    public Sprite pCoach;

    public Sprite pBall;

    public Sprite pGate;

    public MoveObject p_MoveObject;

    private int i8_gameScreenWidth;

    private int i8_gameScreenHeight;

    private static final int[] ai_SpriteParameters = new int[]
    {
        0x0E00, 0x1D00, 0x0E00, 0x1D00, 1, 1, Sprite.SPRITE_ALIGN_CENTER, Sprite.ANIMATION_CYCLIC,
        0x1100, 0x1F00, 0x1100, 0x0F00, 4, 1, Sprite.SPRITE_ALIGN_CENTER, Sprite.ANIMATION_CYCLIC,
        0x1100, 0x1F00, 0x1100, 0x0F00, 4, 1, Sprite.SPRITE_ALIGN_CENTER, Sprite.ANIMATION_CYCLIC,
        0x1200, 0x2700, 0x1200, 0x2700, 2, 2, Sprite.SPRITE_ALIGN_CENTER, Sprite.ANIMATION_FROZEN,
        0x1C00, 0x1900, 0x1C00, 0x1900, 3, 2, Sprite.SPRITE_ALIGN_CENTER, Sprite.ANIMATION_FROZEN,
        0x1C00, 0x1C00, 0x1C00, 0x1C00, 3, 2, Sprite.SPRITE_ALIGN_CENTER, Sprite.ANIMATION_FROZEN,
        0x1C00, 0x1C00, 0x1C00, 0x1C00, 2, 2, Sprite.SPRITE_ALIGN_CENTER, Sprite.ANIMATION_FROZEN,
        0x0E00, 0x1D00, 0x0E00, 0x1D00, 10, 1, Sprite.SPRITE_ALIGN_CENTER, Sprite.ANIMATION_FROZEN,
        0x1000, 0x1B00, 0x1000, 0x1B00, 2, 2, Sprite.SPRITE_ALIGN_CENTER, Sprite.ANIMATION_FROZEN,
        0x0E00, 0x1D00, 0x0E00, 0x1D00, 3, 2, Sprite.SPRITE_ALIGN_CENTER, Sprite.ANIMATION_FROZEN,
        0x0600, 0x0600, 0x0600, 0x0600, 4, 1, Sprite.SPRITE_ALIGN_CENTER, Sprite.ANIMATION_CYCLIC,
        0x1700, 0x1C00, 0x1700, 0x1C00, 1, 1, Sprite.SPRITE_ALIGN_CENTER, Sprite.ANIMATION_CYCLIC,
        0x1700, 0x1C00, 0x1700, 0x1C00, 2, 2, Sprite.SPRITE_ALIGN_CENTER, Sprite.ANIMATION_FROZEN,
        0x1700, 0x1C00, 0x1700, 0x1C00, 3, 2, Sprite.SPRITE_ALIGN_CENTER, Sprite.ANIMATION_FROZEN,
        0x0800, 0x2000, 0x0800, 0x2000, 4, 1, Sprite.SPRITE_ALIGN_CENTER, Sprite.ANIMATION_CYCLIC
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
        _sprite.setCollisionBounds((i_w - i_aw) >> 1, i_h - i_ah, i_aw, i_ah);
    }

    public String getGameTextID()
    {
        return "SMASHINGKICK";
    }

    public boolean initState()
    {
        pPlayerFootballer = new Sprite(0);

        initSpriteFromArray(pPlayerFootballer, SPRITE_FOOTBALLER);
        pPlayerFootballer.lg_SpriteActive = true;

        int i8_cx = INIT_PLAYER_X;
        int i8_cy = LAND_LINE - (pPlayerFootballer.i_height >> 1);

        pPlayerFootballer.setMainPointXY(i8_cx, i8_cy);
        pPlayerFootballer.i_ObjectState = 0;

        pCoach = new Sprite(1);
        initSpriteFromArray(pCoach, SPRITE_COACH);
        pCoach.lg_SpriteActive = true;
        pCoach.setMainPointXY(COACH_X, COACH_Y);

        pBall = new Sprite(2);
        initSpriteFromArray(pBall, SPRITE_BALL);
        pBall.lg_SpriteActive = false;
        pBall.setMainPointXY(COACH_X, COACH_Y);

        pGate = new Sprite(3);
        initSpriteFromArray(pGate, SPRITE_GATE);
        pGate.lg_SpriteActive = true;
        pGate.setMainPointXY(GATE_X, GATE_Y);

        p_MoveObject = new MoveObject();

        return true;
    }


    private void processCoach()
    {
        if(pCoach.processAnimation())
        {
            switch(pCoach.i_ObjectType)
            {
                case SPRITE_COACH:
                    if (b_Landed || b_Goal)
                    {
                        if(i_countdown == 0)
                        {
                            i_countdown = i_borndelay;
                            if(b_Goal)
                            {
                                i_BallCounter++;
                                b_Goal = false;
                                addScore(SCORES_GOAL);
                                initSpriteFromArray(pPlayerFootballer, SPRITE_FOOTBALLER_HAPPY);
                                pPlayerFootballer.setMainPointXY(pPlayerFootballer.i_mainX, LAND_LINE - (pPlayerFootballer.i_height >> 1));
                                m_pAbstractGameActionListener.processGameAction(GAMEACTION_FOOTBALLER_HAPPY);
                            }
                            else
                            {

                            }
                        }
                        else
                        {
                            i_countdown--;
                            if(i_countdown <= 0)
                            {
                                if(i_borndelay > 1) i_borndelay--;
                                initSpriteFromArray(pCoach, SPRITE_COACH_KICK);
                                m_pAbstractGameActionListener.processGameAction(GAMEACTION_COACH_KICK);
                            }
                              else
                                  if(i_countdown <= REMOVE_BALL_TIME && m_iGameState == GAMEWORLDSTATE_PLAYED)
                                  {
                                      pBall.lg_SpriteActive = false;
                                  }
                        }
                    }
                    break;
                case SPRITE_COACH_KICK:
                    switch(getRandomInt(40) / 10)
                    {
                        case 0:
                            i_BallVSpeed = BALL_VSPEED00;
                            i_BallHSpeed = BALL_HSPEED00;
                            break;
                        default:
                        case 1:
                            i_BallVSpeed = BALL_VSPEED01;
                            i_BallHSpeed = BALL_HSPEED01;
                            break;
                        case 2:
                            i_BallVSpeed = BALL_VSPEED02;
                            i_BallHSpeed = BALL_HSPEED02;
                            break;
                        case 3:
                            i_BallVSpeed = BALL_VSPEED03;
                            i_BallHSpeed = BALL_HSPEED03;
                            break;
                    }
                    pBall.setMainPointXY(BALL_X, BALL_Y);
                    pBall.lg_SpriteActive = true;
                    m_pAbstractGameActionListener.processGameAction(GAMEACTION_COACH_KICKED);

                    initSpriteFromArray(pCoach, SPRITE_COACH_AFTER_KICK);

                    b_Goal = false;
                    b_Landed = false;
                    break;
                case SPRITE_COACH_AFTER_KICK:
                    initSpriteFromArray(pCoach, SPRITE_COACH);
                    break;
            }
        }
    }


    private void processBall()
    {
        pBall.processAnimation();
        pGate.processAnimation();

        pBall.setMainPointXY(pBall.i_mainX + i_BallHSpeed, pBall.i_mainY + i_BallVSpeed);

        if(pBall.i_ScreenY + pBall.i_height >= LAND_LINE)
        {
            if(!b_Landed && !b_Goal && pPlayerFootballer.i_ObjectType != SPRITE_FOOTBALLER_SAD)
            {
                initSpriteFromArray(pPlayerFootballer, SPRITE_FOOTBALLER_SAD);
                pPlayerFootballer.setMainPointXY(pPlayerFootballer.i_mainX, LAND_LINE - (pPlayerFootballer.i_height >> 1));
                m_pAbstractGameActionListener.processGameAction(GAMEACTION_FOOTBALLER_SAD);
            }

            b_Landed = true;


            i_BallHSpeed >>=1;

            i_BallVSpeed = (GRAVITY-i_BallVSpeed) >> 1;
            if(i_BallVSpeed > -GRAVITY) i_BallVSpeed = -GRAVITY;
              else
               m_pAbstractGameActionListener.processGameAction(GAMEACTION_BALL_STOPED);


            pBall.setMainPointXY(pBall.i_mainX, LAND_LINE - (pBall.i_height>>1));

        }

        i_BallVSpeed += GRAVITY;


        if (!b_Landed)
        {
            if(pBall.isCollided(pGate))
            {
               b_Goal = true;
            }

            if(pBall.isCollided(pPlayerFootballer))
            {
                if(pBall.lg_SpriteActive)
                {
                    if(pPlayerFootballer.i_mainX > GATEKICK_LEFT_X && pPlayerFootballer.i_mainX < GATEKICK_RIGHT_X)
                    {
                        switch(pPlayerFootballer.i_ObjectType)
                        {
                            case SPRITE_FOOTBALLER_BORED:
                            case SPRITE_FOOTBALLER:
                            case SPRITE_FOOTBALLER_RIGHT:
                            case SPRITE_FOOTBALLER_LEFT:
                                initSpriteFromArray(pPlayerFootballer, SPRITE_FOOTBALLER_GATE_KICK);
                                i_BallHSpeed = BALL_HSPEED4;
                                i_BallVSpeed = BALL_VSPEED4;
                                pPlayerFootballer.setMainPointXY(pPlayerFootballer.i_mainX, LAND_LINE - (pPlayerFootballer.i_height >> 1));
                                pBall.setMainPointXY(pPlayerFootballer.i_mainX + BALL_X_OFFSET4, pPlayerFootballer.i_mainY + BALL_Y_OFFSET4);
                                addScore(SCORES_TOUCH4);
                                m_pAbstractGameActionListener.processGameAction(GAMEACTION_FOOTBALLER_KICK);
                                pBall.lg_SpriteActive = false;
                                break;
                        }
                    }
                    else
                    {
                        switch(pPlayerFootballer.i_ObjectType)
                        {
                            case SPRITE_FOOTBALLER:
                            case SPRITE_FOOTBALLER_BORED:

                                if(pBall.i_ScreenY < pPlayerFootballer.i_ScreenY + pBall.i_height)
                                {
                                    initSpriteFromArray(pPlayerFootballer, SPRITE_FOOTBALLER_HEAD_KICK);
                                    i_BallVSpeed = BALL_VSPEED1;
                                    i_BallHSpeed = BALL_HSPEED1;
                                    pPlayerFootballer.setMainPointXY(pPlayerFootballer.i_mainX, LAND_LINE - (pPlayerFootballer.i_height >> 1));
                                    pBall.setMainPointXY(pPlayerFootballer.i_mainX + BALL_X_OFFSET1, pPlayerFootballer.i_mainY + BALL_Y_OFFSET1);
                                    addScore(SCORES_TOUCH1);
                                    m_pAbstractGameActionListener.processGameAction(GAMEACTION_FOOTBALLER_KICK);
                                }
                                  else
                                       {
                                         if(pBall.i_mainX < pPlayerFootballer.i_mainX - (pBall.i_width>>1))
                                         {
                                              initSpriteFromArray(pPlayerFootballer, SPRITE_FOOTBALLER_OVERHEAD_KICK);
                                              i_BallVSpeed = BALL_VSPEED3;
                                              i_BallHSpeed = BALL_HSPEED3;
                                              pPlayerFootballer.setMainPointXY(pPlayerFootballer.i_mainX, LAND_LINE - (pPlayerFootballer.i_height >> 1));
                                              pBall.setMainPointXY(pPlayerFootballer.i_mainX + BALL_X_OFFSET3, pPlayerFootballer.i_mainY + BALL_Y_OFFSET3);
                                              addScore(SCORES_TOUCH3);
                                              m_pAbstractGameActionListener.processGameAction(GAMEACTION_FOOTBALLER_KICK);
                                         }
                                           else
                                                {
                                                    initSpriteFromArray(pPlayerFootballer, SPRITE_FOOTBALLER_RIGHT_KICK);
                                                    i_BallVSpeed = BALL_VSPEED2;
                                                    i_BallHSpeed = BALL_HSPEED2;
                                                    pPlayerFootballer.setMainPointXY(pPlayerFootballer.i_mainX, LAND_LINE - (pPlayerFootballer.i_height >> 1));
                                                    pBall.setMainPointXY(pPlayerFootballer.i_mainX + BALL_X_OFFSET2, pPlayerFootballer.i_mainY + BALL_Y_OFFSET2);
                                                    addScore(SCORES_TOUCH2);
                                                    m_pAbstractGameActionListener.processGameAction(GAMEACTION_FOOTBALLER_KICK);
                                                }
                                       }
                                break;

                            case SPRITE_FOOTBALLER_LEFT:

                                initSpriteFromArray(pPlayerFootballer, SPRITE_FOOTBALLER_OVERHEAD_KICK);
                                i_BallVSpeed = BALL_VSPEED3;
                                i_BallHSpeed = BALL_HSPEED3;
                                pPlayerFootballer.setMainPointXY(pPlayerFootballer.i_mainX, LAND_LINE - (pPlayerFootballer.i_height >> 1));
                                pBall.setMainPointXY(pPlayerFootballer.i_mainX + BALL_X_OFFSET3, pPlayerFootballer.i_mainY + BALL_Y_OFFSET3);
                                addScore(SCORES_TOUCH3);
                                m_pAbstractGameActionListener.processGameAction(GAMEACTION_FOOTBALLER_KICK);
                                break;

                            case SPRITE_FOOTBALLER_RIGHT:

                                initSpriteFromArray(pPlayerFootballer, SPRITE_FOOTBALLER_RIGHT_KICK);
                                i_BallVSpeed = BALL_VSPEED2;
                                i_BallHSpeed = BALL_HSPEED2;
                                pPlayerFootballer.setMainPointXY(pPlayerFootballer.i_mainX, LAND_LINE - (pPlayerFootballer.i_height >> 1));
                                pBall.setMainPointXY(pPlayerFootballer.i_mainX + BALL_X_OFFSET2, pPlayerFootballer.i_mainY + BALL_Y_OFFSET2);
                                addScore(SCORES_TOUCH2);
                                m_pAbstractGameActionListener.processGameAction(GAMEACTION_FOOTBALLER_KICK);
                                break;
                        }
                    }
                }
                else
                {
                    pBall.lg_SpriteActive = true;
                }
            }
        }
    }

    private boolean processFootballer(int _buttonState)
    {
        if (pPlayerFootballer.processAnimation())
        {
            switch(pPlayerFootballer.i_ObjectType)
            {
                case SPRITE_FOOTBALLER_RIGHT_KICK:
                case SPRITE_FOOTBALLER_HEAD_KICK:
                case SPRITE_FOOTBALLER_OVERHEAD_KICK:
                case SPRITE_FOOTBALLER_GATE_KICK:
                    initSpriteFromArray(pPlayerFootballer, SPRITE_FOOTBALLER);
                    pPlayerFootballer.setMainPointXY(pPlayerFootballer.i_mainX, LAND_LINE - (pPlayerFootballer.i_height >> 1));
                    m_pAbstractGameActionListener.processGameAction(GAMEACTION_FOOTBALLER_KICKED);
                    pBall.lg_SpriteActive = true;
                    break;
                case SPRITE_FOOTBALLER_HAPPY:
                    if((i_BallCounter % BONUS_BALLS) == 0)
                    {
                        if(m_iPlayerAttemptions < MAX_ATTEMPTIONS)
                        {
                            m_iPlayerAttemptions++;
                            m_pAbstractGameActionListener.processGameAction(GAMEACTION_ADD_BONUS_LIVE);
                        }
                    }
                case SPRITE_FOOTBALLER_BORED:
                    initSpriteFromArray(pPlayerFootballer, SPRITE_FOOTBALLER);
                    pPlayerFootballer.setMainPointXY(pPlayerFootballer.i_mainX, LAND_LINE - (pPlayerFootballer.i_height >> 1));
                    i_GameTimer = 0;
                    break;
                case SPRITE_FOOTBALLER_SAD:
                    pPlayerFootballer.setMainPointXY(pPlayerFootballer.i_mainX, LAND_LINE - (pPlayerFootballer.i_height >> 1));
                    return false;
            }
        }

        int i_x = pPlayerFootballer.i_ScreenX;
        int i_y = pPlayerFootballer.i_ScreenY;

        int i_mx = pPlayerFootballer.i_mainX;
        int i_my = pPlayerFootballer.i_mainY;

        int i_w = pPlayerFootballer.i_width;
        int i_h = pPlayerFootballer.i_height;

        switch(pPlayerFootballer.i_ObjectType)
        {
            case SPRITE_FOOTBALLER:
                if(i_GameTimer > BORED_DELAY)
                {
                    i_GameTimer = 0;
                    initSpriteFromArray(pPlayerFootballer, SPRITE_FOOTBALLER_BORED);
                    pPlayerFootballer.setMainPointXY(pPlayerFootballer.i_mainX, LAND_LINE - (pPlayerFootballer.i_height >> 1));
                    m_pAbstractGameActionListener.processGameAction(GAMEACTION_FOOTBALLER_BORED);
                }
            case SPRITE_FOOTBALLER_BORED:
                if ((_buttonState & MoveObject.BUTTON_LEFT) != 0)
                {
                    if(i_mx > LEFT_MOVE_MARGIN)
                    {
                        initSpriteFromArray(pPlayerFootballer, SPRITE_FOOTBALLER_LEFT);
                    }
                    else
                    {
                        initSpriteFromArray(pPlayerFootballer, SPRITE_FOOTBALLER);
                        i_GameTimer = 0;
                    }
                    pPlayerFootballer.setMainPointXY(pPlayerFootballer.i_mainX, LAND_LINE - (pPlayerFootballer.i_height >> 1));
                }
                else if ((_buttonState & MoveObject.BUTTON_RIGHT) != 0)
                {

                    if(i_mx < (i8_gameScreenWidth - RIGHT_MOVE_MARGIN))
                    {
                        initSpriteFromArray(pPlayerFootballer, SPRITE_FOOTBALLER_RIGHT);
                    }
                    else
                    {
                        initSpriteFromArray(pPlayerFootballer, SPRITE_FOOTBALLER);
                        i_GameTimer = 0;
                    }
                    pPlayerFootballer.setMainPointXY(pPlayerFootballer.i_mainX, LAND_LINE - (pPlayerFootballer.i_height >> 1));
                }
                break;
            case SPRITE_FOOTBALLER_LEFT:
                i_GameTimer = 0;
                i_mx -= FOOTBALLER_MOVE_SPEED;
                if(i_mx < LEFT_MOVE_MARGIN)
                {
                    initSpriteFromArray(pPlayerFootballer, SPRITE_FOOTBALLER);
                    pPlayerFootballer.setMainPointXY(LEFT_MOVE_MARGIN, LAND_LINE - (pPlayerFootballer.i_height >> 1));
                }
                else
                {
                    pPlayerFootballer.setMainPointXY(i_mx, i_my);
                }
                if ((_buttonState & MoveObject.BUTTON_LEFT) == 0)
                {
                    initSpriteFromArray(pPlayerFootballer, SPRITE_FOOTBALLER);
                    pPlayerFootballer.setMainPointXY(pPlayerFootballer.i_mainX, LAND_LINE - (pPlayerFootballer.i_height >> 1));
                }
                break;
            case SPRITE_FOOTBALLER_RIGHT:
                i_GameTimer = 0;
                i_mx += FOOTBALLER_MOVE_SPEED;
                if(i_mx > (i8_gameScreenWidth - RIGHT_MOVE_MARGIN))
                {
                    initSpriteFromArray(pPlayerFootballer, SPRITE_FOOTBALLER);
                    pPlayerFootballer.setMainPointXY((i8_gameScreenWidth - RIGHT_MOVE_MARGIN), LAND_LINE - (pPlayerFootballer.i_height >> 1));
                }
                else
                {
                    pPlayerFootballer.setMainPointXY(i_mx, i_my);
                }
                if ((_buttonState & MoveObject.BUTTON_RIGHT) == 0)
                {
                    initSpriteFromArray(pPlayerFootballer, SPRITE_FOOTBALLER);
                    pPlayerFootballer.setMainPointXY(pPlayerFootballer.i_mainX, LAND_LINE - (pPlayerFootballer.i_height >> 1));
                }
                break;
        }

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
                    i_borndelay = LEVEL_EASY_BALLFREQ;
                    m_iPlayerAttemptions = LEVEL_EASY_LIVES;
                }
                ;
                break;
            case GAMELEVEL_NORMAL:
                {
                    i_timedelay = LEVEL_NORMAL_TIMEDELAY;
                    i_borndelay = LEVEL_NORMAL_BALLFREQ;
                    m_iPlayerAttemptions = LEVEL_NORMAL_LIVES;
                }
                ;
                break;
            case GAMELEVEL_HARD:
                {
                    i_timedelay = LEVEL_HARD_TIMEDELAY;
                    i_borndelay = LEVEL_HARD_BALLFREQ;
                    m_iPlayerAttemptions = LEVEL_HARD_LIVES;
                }
                ;
                break;
        }

        i_GameTimer = 0; 
        i_countdown = 0;
        i_BallCounter = 0;
        b_Goal = false;
        b_Landed = true;
        i_BallHSpeed = 0;
        i_BallVSpeed = 0;

        return true;
    }

    public void resumeGameAfterPlayerLostOrPaused()
    {
        super.resumeGameAfterPlayerLostOrPaused();


        switch(getGameDifficultLevel())
        {
            case GAMELEVEL_EASY:
                break;
            case GAMELEVEL_NORMAL:
                break;
            case GAMELEVEL_HARD:
                break;
        }

        initSpriteFromArray(pPlayerFootballer, SPRITE_FOOTBALLER);
        pPlayerFootballer.setMainPointXY(pPlayerFootballer.i_mainX, LAND_LINE - (pPlayerFootballer.i_height >> 1));
        pPlayerFootballer.lg_SpriteActive = true;


        i_GameTimer = 0; 
        i_countdown = 0;
        b_Goal = false;
        b_Landed = true;
        i_BallHSpeed = 0;
        i_BallVSpeed = 0;
        pBall.lg_SpriteActive = false;
    }

    public void deinitState()
    {
        pPlayerFootballer = null;
        pCoach = null;
        pBall = null;
        pGate = null;
        p_MoveObject = null;

        Runtime.getRuntime().gc();
    }

    public int _getMaximumDataSize()
    {
        return  (Sprite.DATASIZE_BYTES+1) * 4 + 27;
    }

    public void _saveGameData(DataOutputStream _outputStream) throws IOException
    {
        _outputStream.writeByte(m_iPlayerAttemptions);
        _outputStream.writeInt(i_GameTimer);
        _outputStream.writeInt(i_countdown);
        _outputStream.writeInt(i_borndelay);
        _outputStream.writeInt(i_BallCounter);
        _outputStream.writeInt(i_BallHSpeed);
        _outputStream.writeInt(i_BallVSpeed);
        _outputStream.writeBoolean(b_Goal);
        _outputStream.writeBoolean(b_Landed);


        _outputStream.writeByte(pPlayerFootballer.i_ObjectType);
        pPlayerFootballer.writeSpriteToStream(_outputStream);

        _outputStream.writeByte(pCoach.i_ObjectType);
        pCoach.writeSpriteToStream(_outputStream);

        _outputStream.writeByte(pBall.i_ObjectType);
        pBall.writeSpriteToStream(_outputStream);

        _outputStream.writeByte(pGate.i_ObjectType);
        pGate.writeSpriteToStream(_outputStream);
    }

    public void _loadGameData(DataInputStream _inputStream) throws IOException
    {
        m_iPlayerAttemptions = _inputStream.readByte();
        i_GameTimer = _inputStream.readInt();
        i_countdown = _inputStream.readInt();
        i_borndelay = _inputStream.readInt();
        i_BallCounter = _inputStream.readInt();
        i_BallHSpeed = _inputStream.readInt();
        i_BallVSpeed = _inputStream.readInt();
        b_Goal = _inputStream.readBoolean();
        b_Landed = _inputStream.readBoolean();


        initSpriteFromArray(pPlayerFootballer,_inputStream.readUnsignedByte());
        pPlayerFootballer.readSpriteFromStream(_inputStream);

        initSpriteFromArray(pCoach,_inputStream.readUnsignedByte());
        pCoach.readSpriteFromStream(_inputStream);

        initSpriteFromArray(pBall,_inputStream.readUnsignedByte());
        pBall.readSpriteFromStream(_inputStream);

        initSpriteFromArray(pGate,_inputStream.readUnsignedByte());
        pGate.readSpriteFromStream(_inputStream);
    }

    public int nextGameStep()
    {
        CAbstractPlayer p_player = m_pPlayerList[0];
        p_player.nextPlayerMove(this, p_MoveObject);


        if(m_iGameState == GAMEWORLDSTATE_GAMEOVER || m_iGameState == GAMEWORLDSTATE_PLAYERLOST)
        {
            processBall();
            return m_iGameState;
        }

        if (processFootballer(p_MoveObject.i_buttonState) )
        {
        }
        else
        {
            m_iPlayerAttemptions--;

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

        processCoach();
        processBall();

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
