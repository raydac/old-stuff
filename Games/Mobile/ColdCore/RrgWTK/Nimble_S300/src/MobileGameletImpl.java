
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.DataInputStream;

public class MobileGameletImpl extends MobileGamelet
{
    public static final int MAX_SUITCASES = 32;

    public static final int SUITCASE_SPEED = (1 << 8);
    public static final int SUITCASE_SPACE = (22 << 8);

    private static int I8_SCREEN_WIDTH;

    private static int I8_SCREEN_HEIGHT;

    private static final int MOVE_SPACE = (25 << 8);

    public MoveObject p_MoveObject;

    public static final int GAMEACTION_SUITCASE_GET             = 0;
    public static final int GAMEACTION_SUITCASE_RIGHT           = 1;
    public static final int GAMEACTION_SUITCASE_WRONG           = 2;
    public static final int GAMEACTION_SUITCASE_GENERATED       = 3;
    public static final int GAMEACTION_SUITCASE_DISAPPEAR       = 4;
    public static final int GAMEACTION_PLAYER_LOST              = 5;


    private int i_timedelay;

    public int i_playerAttemptions;

    public int i_GameTimer;

    public int i_LastTime;
    public int i_GenerateDelay;

    public int i_CurrentSuitcase;

    public int i_SuitcaseCounter;

    public static final int LEVEL_EASY = 0;

    private static final int LEVEL_EASY_ATTEMPTIONS = 4;

    private static final int LEVEL_EASY_GENERATION = 8;

    private static final int LEVEL_EASY_TIMEDELAY = 130;

    public static final int LEVEL_NORMAL = 1;

    private static final int LEVEL_NORMAL_ATTEMPTIONS = 3;

    private static final int LEVEL_NORMAL_GENERATION = 7;

    private static final int LEVEL_NORMAL_TIMEDELAY = 100;

    public static final int LEVEL_HARD = 2;

    private static final int LEVEL_HARD_ATTEMPTIONS = 2;

    private static final int LEVEL_HARD_GENERATION = 6;

    private static final int LEVEL_HARD_TIMEDELAY = 70;

    public static final int SCORES_SUITCASE = 10;


    public Sprite pPlayerSprite;

    public static final int MAX_SPRITES_NUMBER = 32;

    public Sprite[] ap_Sprites;

    private static final int SPRITEDATALENGTH = 10;

    public static final int SPRITE_CARRIER = 0;
    public static final int SPRITE_CARRIER_LEFT = 1;
    public static final int SPRITE_CARRIER_UP = 2;
    public static final int SPRITE_CARRIER_RIGHT = 3;
    public static final int SPRITE_CARRIER_DOWN = 4;
    public static final int SPRITE_CARRIER_DEAD = 5;

    public static final int SPRITE_SUITCASE1 = 6;
    public static final int SPRITE_SUITCASE2 = 7;
    public static final int SPRITE_SUITCASE3 = 8;
    public static final int SPRITE_SUITCASE4 = 9;
    public static final int SPRITE_SUITCASE_EMPTY = 10;


    private static final int [] ai_SpriteParameters = new int[]
    {
        0x1400, 0x1400, 0x0000, 0x0000, 0x1400, 0x1400, 3, 1, Sprite.SPRITE_ALIGN_CENTER, Sprite.ANIMATION_FROZEN,
        0x1400, 0x1400, 0x0000, 0x0000, 0x1400, 0x1000, 3, 1, Sprite.SPRITE_ALIGN_CENTER, Sprite.ANIMATION_FROZEN,
        0x1400, 0x1400, 0x0000, 0x0000, 0x1000, 0x1400, 3, 1, Sprite.SPRITE_ALIGN_CENTER, Sprite.ANIMATION_FROZEN,
        0x1400, 0x1400, 0x0000, 0x0000, 0x1400, 0x1000, 3, 1, Sprite.SPRITE_ALIGN_CENTER, Sprite.ANIMATION_FROZEN,
        0x1400, 0x1400, 0x0000, 0x0000, 0x1000, 0x1400, 3, 1, Sprite.SPRITE_ALIGN_CENTER, Sprite.ANIMATION_FROZEN,
        0x1400, 0x1400, 0x0000, 0x0000, 0x1400, 0x0400, 5, 1, Sprite.SPRITE_ALIGN_CENTER, Sprite.ANIMATION_FROZEN,
        0x0D00, 0x0D00, 0x0000, 0x0000, 0x0D00, 0x0D00, 5, 1, Sprite.SPRITE_ALIGN_CENTER, Sprite.ANIMATION_FROZEN,
        0x0D00, 0x0D00, 0x0000, 0x0000, 0x0D00, 0x0D00, 5, 1, Sprite.SPRITE_ALIGN_CENTER, Sprite.ANIMATION_FROZEN,
        0x0D00, 0x0D00, 0x0000, 0x0000, 0x0D00, 0x0D00, 5, 1, Sprite.SPRITE_ALIGN_CENTER, Sprite.ANIMATION_FROZEN,
        0x0D00, 0x0D00, 0x0000, 0x0000, 0x0D00, 0x0D00, 5, 1, Sprite.SPRITE_ALIGN_CENTER, Sprite.ANIMATION_FROZEN,
        0x0A00, 0x0A00, 0x0000, 0x0000, 0x0A00, 0x0A00, 5, 1, Sprite.SPRITE_ALIGN_CENTER, Sprite.ANIMATION_FROZEN
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
        for (int li = 0; li < MAX_SPRITES_NUMBER; li++)
        {
            Sprite p_Sprite = ap_Sprites[li];
            if (!p_Sprite.lg_SpriteActive) continue;
            if (p_Sprite.processAnimation())
            {
                switch (p_Sprite.i_ObjectType)
                {
                    case -1:
                        break;
                }
            }

            int k = p_Sprite.i_ObjectState;

            switch(pPlayerSprite.i_ObjectType)
            {
                case SPRITE_CARRIER_LEFT:
                    if((k == 0) && (p_Sprite.i_mainX > 0))
                    {
                        if(p_Sprite.i_mainY == pPlayerSprite.i_mainY)
                        {
                            int suitcase = pPlayerSprite.i_ObjectState;

                            switch(suitcase)
                            {
                                case SPRITE_SUITCASE1:
                                    pPlayerSprite.i_ObjectState = p_Sprite.i_ObjectType;
                                    activateSprite(p_Sprite, SPRITE_SUITCASE1);
                                    addScore(SCORES_SUITCASE);
                                    m_pAbstractGameActionListener.processGameAction(GAMEACTION_SUITCASE_RIGHT);
                                    break;
                                case SPRITE_SUITCASE_EMPTY:
                                    pPlayerSprite.i_ObjectState = p_Sprite.i_ObjectType;
                                    activateSprite(p_Sprite, SPRITE_SUITCASE_EMPTY);
                                    if(pPlayerSprite.i_ObjectState != SPRITE_SUITCASE_EMPTY)
                                    {
                                       m_pAbstractGameActionListener.processGameAction(GAMEACTION_SUITCASE_GET);
                                    }
                                    break;
                                default:
                                    if(p_Sprite.i_ObjectType == SPRITE_SUITCASE_EMPTY)
                                    {
                                        activateSprite(p_Sprite, pPlayerSprite.i_ObjectState);
                                        pPlayerSprite.i_ObjectState = SPRITE_SUITCASE_EMPTY;
                                        m_pAbstractGameActionListener.processGameAction(GAMEACTION_SUITCASE_WRONG);
                                    }
                                    break;
                            }
                        }
                    }
                    break;
                case SPRITE_CARRIER_RIGHT:
                    if((k == 1) && (p_Sprite.i_mainX < I8_SCREEN_WIDTH))
                    {
                        if(p_Sprite.i_mainY == pPlayerSprite.i_mainY)
                        {
                            int suitcase = pPlayerSprite.i_ObjectState;

                            switch(suitcase)
                            {
                                case SPRITE_SUITCASE2:
                                    pPlayerSprite.i_ObjectState = p_Sprite.i_ObjectType;
                                    activateSprite(p_Sprite, SPRITE_SUITCASE2);
                                    addScore(SCORES_SUITCASE);
                                    m_pAbstractGameActionListener.processGameAction(GAMEACTION_SUITCASE_RIGHT);
                                    break;
                                case SPRITE_SUITCASE_EMPTY:
                                    pPlayerSprite.i_ObjectState = p_Sprite.i_ObjectType;
                                    activateSprite(p_Sprite, SPRITE_SUITCASE_EMPTY);
                                    if(pPlayerSprite.i_ObjectState != SPRITE_SUITCASE_EMPTY)
                                    {
                                       m_pAbstractGameActionListener.processGameAction(GAMEACTION_SUITCASE_GET);
                                    }
                                    break;
                                default:
                                    if(p_Sprite.i_ObjectType == SPRITE_SUITCASE_EMPTY)
                                    {
                                        activateSprite(p_Sprite, pPlayerSprite.i_ObjectState);
                                        pPlayerSprite.i_ObjectState = SPRITE_SUITCASE_EMPTY;
                                        m_pAbstractGameActionListener.processGameAction(GAMEACTION_SUITCASE_WRONG);
                                    }
                                    break;
                            }
                        }
                    }
                    break;
                    case SPRITE_CARRIER_UP:
                    if((k == 2) && (p_Sprite.i_mainY > 0))
                    {
                        if(p_Sprite.i_mainX == pPlayerSprite.i_mainX)
                        {
                            int suitcase = pPlayerSprite.i_ObjectState;

                            switch(suitcase)
                            {
                                case SPRITE_SUITCASE3:
                                    pPlayerSprite.i_ObjectState = p_Sprite.i_ObjectType;
                                    activateSprite(p_Sprite, SPRITE_SUITCASE3);
                                    addScore(SCORES_SUITCASE);
                                    m_pAbstractGameActionListener.processGameAction(GAMEACTION_SUITCASE_RIGHT);
                                    break;
                                case SPRITE_SUITCASE_EMPTY:
                                    pPlayerSprite.i_ObjectState = p_Sprite.i_ObjectType;
                                    activateSprite(p_Sprite, SPRITE_SUITCASE_EMPTY);
                                    if(pPlayerSprite.i_ObjectState != SPRITE_SUITCASE_EMPTY)
                                    {
                                       m_pAbstractGameActionListener.processGameAction(GAMEACTION_SUITCASE_GET);
                                    }
                                    break;
                                default:
                                    if(p_Sprite.i_ObjectType == SPRITE_SUITCASE_EMPTY)
                                    {
                                        activateSprite(p_Sprite, pPlayerSprite.i_ObjectState);
                                        pPlayerSprite.i_ObjectState = SPRITE_SUITCASE_EMPTY;
                                        m_pAbstractGameActionListener.processGameAction(GAMEACTION_SUITCASE_WRONG);
                                    }
                                    break;
                            }
                        }
                    }
                    break;
                    case SPRITE_CARRIER_DOWN:
                    if((k == 3) && (p_Sprite.i_mainY < I8_SCREEN_HEIGHT))
                    {
                        if(p_Sprite.i_mainX == pPlayerSprite.i_mainX)
                        {
                            int suitcase = pPlayerSprite.i_ObjectState;

                            switch(suitcase)
                            {
                                case SPRITE_SUITCASE4:
                                    pPlayerSprite.i_ObjectState = p_Sprite.i_ObjectType;
                                    activateSprite(p_Sprite, SPRITE_SUITCASE4);
                                    addScore(SCORES_SUITCASE);
                                    m_pAbstractGameActionListener.processGameAction(GAMEACTION_SUITCASE_RIGHT);
                                    break;
                                case SPRITE_SUITCASE_EMPTY:
                                    pPlayerSprite.i_ObjectState = p_Sprite.i_ObjectType;
                                    activateSprite(p_Sprite, SPRITE_SUITCASE_EMPTY);
                                    if(pPlayerSprite.i_ObjectState != SPRITE_SUITCASE_EMPTY)
                                    {
                                       m_pAbstractGameActionListener.processGameAction(GAMEACTION_SUITCASE_GET);
                                    }
                                    break;
                                default:
                                    if(p_Sprite.i_ObjectType == SPRITE_SUITCASE_EMPTY)
                                    {
                                        activateSprite(p_Sprite, pPlayerSprite.i_ObjectState);
                                        pPlayerSprite.i_ObjectState = SPRITE_SUITCASE_EMPTY;
                                        m_pAbstractGameActionListener.processGameAction(GAMEACTION_SUITCASE_WRONG);
                                    }
                                    break;
                            }
                        }
                    }
                    break;
            }

            boolean hidden = false;

            int lm = 0 - p_Sprite.i_width / 2;
            int rm = I8_SCREEN_WIDTH + p_Sprite.i_width / 2;
            int tm = 0 - p_Sprite.i_height / 2;
            int bm = I8_SCREEN_HEIGHT + p_Sprite.i_height / 2;

            int sx = p_Sprite.i_mainX;
            int sy = p_Sprite.i_mainY;

            if(sx < lm || sx > rm || sy < tm || sy > bm )
            {
                if(sx == pPlayerSprite.i_mainX || sy == pPlayerSprite.i_mainY)
                {
                    hidden = true;
                }
            }

            switch (p_Sprite.i_ObjectType)
            {
                case SPRITE_SUITCASE_EMPTY:
                    if((i_LastTime + i_GenerateDelay) < i_GameTimer )
                    {
                        if(hidden)
                        {
                            i_LastTime = i_GameTimer;

                            if(i_CurrentSuitcase < SPRITE_SUITCASE4)
                            {
                                i_CurrentSuitcase++;
                            }
                            else
                            {
                                i_CurrentSuitcase = SPRITE_SUITCASE1;
                            }
                            activateSprite(p_Sprite, i_CurrentSuitcase);
                            m_pAbstractGameActionListener.processGameAction(GAMEACTION_SUITCASE_GENERATED);
                            i_SuitcaseCounter++;
                            hidden = false;
                        }
                    }
                case SPRITE_SUITCASE1:
                case SPRITE_SUITCASE2:
                case SPRITE_SUITCASE3:
                case SPRITE_SUITCASE4:
                    if(hidden)
                    {
                        if(k == 0 && p_Sprite.i_ObjectType == SPRITE_SUITCASE1)
                        {
                            activateSprite(p_Sprite, SPRITE_SUITCASE_EMPTY);
                            m_pAbstractGameActionListener.processGameAction(GAMEACTION_SUITCASE_DISAPPEAR);
                            i_SuitcaseCounter--;
                        }
                        if(k == 1 && p_Sprite.i_ObjectType == SPRITE_SUITCASE2)
                        {
                            activateSprite(p_Sprite, SPRITE_SUITCASE_EMPTY);
                            m_pAbstractGameActionListener.processGameAction(GAMEACTION_SUITCASE_DISAPPEAR);
                            i_SuitcaseCounter--;
                        }
                        if(k == 2 && p_Sprite.i_ObjectType == SPRITE_SUITCASE3)
                        {
                            activateSprite(p_Sprite, SPRITE_SUITCASE_EMPTY);
                            m_pAbstractGameActionListener.processGameAction(GAMEACTION_SUITCASE_DISAPPEAR);
                            i_SuitcaseCounter--;
                        }
                        if(k == 3 && p_Sprite.i_ObjectType == SPRITE_SUITCASE4)
                        {
                            activateSprite(p_Sprite, SPRITE_SUITCASE_EMPTY);
                            m_pAbstractGameActionListener.processGameAction(GAMEACTION_SUITCASE_DISAPPEAR);
                            i_SuitcaseCounter--;
                        }
                    }
                default:
                    int x = p_Sprite.i_mainX;
                    int y = p_Sprite.i_mainY;

                    int CenterX = 64 << 8;
                    int CenterY = I8_SCREEN_HEIGHT / 2;

                    int Top = (32 << 8);
                    int Left = (CenterX) - SUITCASE_SPACE / 2;
                    int Right = (CenterX) + SUITCASE_SPACE / 2;
                    int Bottom = (96 << 8);

                    switch(p_Sprite.i_ObjectState)
                    {
                        case 0:
                            CenterX = 0;
                            CenterY = I8_SCREEN_HEIGHT / 2;

                            Top = (CenterY - 0x2000);
                            Left = (CenterX) - SUITCASE_SPACE / 2;
                            Right = (CenterX) + SUITCASE_SPACE / 2;
                            Bottom = (CenterY + 0x2000);
                            break;
                        case 1:
                            CenterX = I8_SCREEN_WIDTH - 0x0100;
                            CenterY = I8_SCREEN_HEIGHT / 2;

                            Top = (CenterY - 0x2000);
                            Left = (CenterX) - SUITCASE_SPACE / 2;
                            Right = (CenterX) + SUITCASE_SPACE / 2;
                            Bottom = (CenterY + 0x2000);
                            break;
                        case 2:
                            CenterX = I8_SCREEN_WIDTH / 2;
                            CenterY = 0;

                            Top = (CenterY) - SUITCASE_SPACE / 2;
                            Left = (CenterX - 0x2000);
                            Right = (CenterX + 0x2000);
                            Bottom = (CenterY) + SUITCASE_SPACE / 2;
                            break;
                        case 3:
                            CenterX = I8_SCREEN_WIDTH / 2;
                            CenterY = I8_SCREEN_HEIGHT - 0x0100;

                            Top = (CenterY) - SUITCASE_SPACE / 2;
                            Left = (CenterX - 0x2000);
                            Right = (CenterX + 0x2000);
                            Bottom = (CenterY) + SUITCASE_SPACE / 2;
                            break;
                    }

                    if(y > Top && y < Bottom)
                    {
                        if(x > CenterX)
                        {
                            y += SUITCASE_SPEED;
                        }
                        else
                        {
                            y -= SUITCASE_SPEED;
                        }
                    }
                    else
                    {
                        if(y < CenterY)
                        {
                            if(x < Right)
                            {
                                x += SUITCASE_SPEED;
                            }
                            else
                            {
                                y += SUITCASE_SPEED;
                            }
                        }
                        else
                        {
                            if(x > Left)
                            {
                                x -= SUITCASE_SPEED;
                            }
                            else
                            {
                                y -= SUITCASE_SPEED;
                            }
                        }
                    }
                    p_Sprite.setMainPointXY(x,y);
                    break;
            }
        }
    }

    private boolean processPlayer(int _buttonState)
    {

        if(pPlayerSprite.i_ObjectType == SPRITE_CARRIER_DEAD)
        {
            return !pPlayerSprite.processAnimation();
        }
         else
              {
                 pPlayerSprite.processAnimation();
              }

        switch(pPlayerSprite.i_ObjectType)
        {
            case SPRITE_CARRIER:
                if ((_buttonState & MoveObject.BUTTON_LEFT) != 0)
                {
                    activateSprite(pPlayerSprite, SPRITE_CARRIER_LEFT);
                    pPlayerSprite.setMainPointXY( (I8_SCREEN_WIDTH / 2 - MOVE_SPACE), (I8_SCREEN_HEIGHT / 2) );
                }
                else if ((_buttonState & MoveObject.BUTTON_RIGHT) != 0)
                {
                    activateSprite(pPlayerSprite, SPRITE_CARRIER_RIGHT);
                    pPlayerSprite.setMainPointXY( (I8_SCREEN_WIDTH / 2 + MOVE_SPACE), (I8_SCREEN_HEIGHT / 2) );
                }
                if ((_buttonState & MoveObject.BUTTON_UP) != 0)
                {
                    activateSprite(pPlayerSprite, SPRITE_CARRIER_UP);
                    pPlayerSprite.setMainPointXY( (I8_SCREEN_WIDTH / 2), (I8_SCREEN_HEIGHT / 2 - MOVE_SPACE) );
                }
                else if ((_buttonState & MoveObject.BUTTON_DOWN) != 0)
                {
                    activateSprite(pPlayerSprite, SPRITE_CARRIER_DOWN);
                    pPlayerSprite.setMainPointXY( (I8_SCREEN_WIDTH / 2), (I8_SCREEN_HEIGHT / 2 + MOVE_SPACE) );
                }
                break;
            case SPRITE_CARRIER_LEFT:
                if ((_buttonState & MoveObject.BUTTON_LEFT) == 0)
                {
                    activateSprite(pPlayerSprite, SPRITE_CARRIER);
                    pPlayerSprite.setMainPointXY( (I8_SCREEN_WIDTH / 2), (I8_SCREEN_HEIGHT / 2) );
                }
                break;
            case SPRITE_CARRIER_UP:
                if ((_buttonState & MoveObject.BUTTON_UP) == 0)
                {
                    activateSprite(pPlayerSprite, SPRITE_CARRIER);
                    pPlayerSprite.setMainPointXY( (I8_SCREEN_WIDTH / 2), (I8_SCREEN_HEIGHT / 2) );
                }
                break;
            case SPRITE_CARRIER_RIGHT:
                if ((_buttonState & MoveObject.BUTTON_RIGHT) == 0)
                {
                    activateSprite(pPlayerSprite, SPRITE_CARRIER);
                    pPlayerSprite.setMainPointXY( (I8_SCREEN_WIDTH / 2), (I8_SCREEN_HEIGHT / 2) );
                }
                break;
            case SPRITE_CARRIER_DOWN:
                if ((_buttonState & MoveObject.BUTTON_DOWN) == 0)
                {
                    activateSprite(pPlayerSprite, SPRITE_CARRIER);
                    pPlayerSprite.setMainPointXY( (I8_SCREEN_WIDTH / 2), (I8_SCREEN_HEIGHT / 2) );
                }
                break;
        }
        if(i_SuitcaseCounter >= MAX_SUITCASES)
        {
            activateSprite(pPlayerSprite, SPRITE_CARRIER_DEAD);
            pPlayerSprite.setMainPointXY( (I8_SCREEN_WIDTH / 2), (I8_SCREEN_HEIGHT / 2) );
            m_pAbstractGameActionListener.processGameAction(GAMEACTION_PLAYER_LOST);
        }

        return true;
    }


    private void generateSuitcases()
    {
        int y = (I8_SCREEN_HEIGHT / 2 - 0x2000); 
        int x = (64 << 8); 
        int aw = 0;
        int ah = 0;

        int type = SPRITE_SUITCASE_EMPTY;

        for(int k=0; k<4; k++)
        {
            switch(k)
            {
                case 0:
                    y = (I8_SCREEN_HEIGHT / 2 - 0x2000);
                    x = (0x0000) + SUITCASE_SPACE / 2;
                    ah = SUITCASE_SPACE;
                    aw = 0;
                    break;
                case 1:
                    y = (I8_SCREEN_HEIGHT / 2 - 0x2000);
                    x = (I8_SCREEN_WIDTH - 0x0100) + SUITCASE_SPACE / 2;
                    ah = SUITCASE_SPACE;
                    aw = 0;
                    break;
                case 2:
                    y = (0x0000) + SUITCASE_SPACE / 2;;
                    x = (I8_SCREEN_WIDTH / 2 - 0x2000);
                    aw = SUITCASE_SPACE;
                    ah = 0;
                    break;
                case 3:
                    y = (I8_SCREEN_HEIGHT - 0x0100) + SUITCASE_SPACE / 2;
                    x = (I8_SCREEN_WIDTH / 2 - 0x2000);
                    aw = SUITCASE_SPACE;
                    ah = 0;
                    break;
            }

            for(int i=0; i<4; i++)
            {
                Sprite p_emptySprite = getInactiveSprite();
                if (p_emptySprite != null)
                {
                    activateSprite(p_emptySprite, type);
                    p_emptySprite.lg_SpriteActive = true;
                    p_emptySprite.setMainPointXY(x, y);
                    p_emptySprite.i_ObjectState = k;
                    y += ah;
                    x += aw;
                }
            }

            x -= SUITCASE_SPACE;
            y -= SUITCASE_SPACE;

            for(int i=4; i<8; i++)
            {
                Sprite p_emptySprite = getInactiveSprite();
                if (p_emptySprite != null)
                {
                    activateSprite(p_emptySprite, type);
                    p_emptySprite.lg_SpriteActive = true;
                    p_emptySprite.setMainPointXY(x, y);
                    p_emptySprite.i_ObjectState = k;
                    y -= ah;
                    x -= aw;
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
                }
                break;
            case LEVEL_NORMAL:
                {
                    i_timedelay = LEVEL_NORMAL_TIMEDELAY;
                    i_playerAttemptions = LEVEL_NORMAL_ATTEMPTIONS;
                    i_GenerateDelay = LEVEL_NORMAL_GENERATION;
                }
                break;
            case LEVEL_HARD:
                {
                    i_timedelay = LEVEL_HARD_TIMEDELAY;
                    i_playerAttemptions = LEVEL_HARD_ATTEMPTIONS;
                    i_GenerateDelay = LEVEL_HARD_GENERATION;
                }
                break;
        }

        i_GameTimer = 0;
        i_LastTime = i_GameTimer;
        i_CurrentSuitcase = SPRITE_SUITCASE1;
        i_SuitcaseCounter = 0;

        activateSprite(pPlayerSprite, SPRITE_CARRIER);
        pPlayerSprite.setMainPointXY(I8_SCREEN_WIDTH / 2, I8_SCREEN_HEIGHT / 2);
        pPlayerSprite.i_ObjectState = SPRITE_SUITCASE_EMPTY;

        generateSuitcases();

        return true;
    }

    public void resumeGameAfterPlayerLostOrPaused()
    {
        super.resumeGameAfterPlayerLostOrPaused();

        deinitState();
        initState();

        i_GameTimer = 0;
        i_LastTime = i_GameTimer;
        i_CurrentSuitcase = SPRITE_SUITCASE1;

        i_SuitcaseCounter = 0;

        activateSprite(pPlayerSprite, SPRITE_CARRIER);
        pPlayerSprite.setMainPointXY(I8_SCREEN_WIDTH / 2, I8_SCREEN_HEIGHT / 2);
        pPlayerSprite.i_ObjectState = SPRITE_SUITCASE_EMPTY;

        generateSuitcases();

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
        return "BAGGAGE";
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
        return (MAX_SPRITES_NUMBER+1)*(Sprite.DATASIZE_BYTES+1)+24;
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
        _outputStream.writeInt(i_CurrentSuitcase);
        _outputStream.writeInt(i_SuitcaseCounter);
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
        i_CurrentSuitcase = _inputStream.readInt();
        i_SuitcaseCounter = _inputStream.readInt();;
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
