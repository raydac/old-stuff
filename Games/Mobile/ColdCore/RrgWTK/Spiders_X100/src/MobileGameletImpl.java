
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.DataInputStream;

public class MobileGameletImpl extends MobileGamelet
{
    public static final int MIN_SPIDER_DELAY = 10;
    public static final int MAX_SPIDER_DELAY = 40;

    public static final int SPIDER_UP_SPEED = (0x0100);

    public static final int SPIDERS_X = (20 << 8);
    public static final int SPIDERS_Y = (16 << 8);
    public static final int SPIDER_SPACE = (18 << 8);

    public static final int STOP_LINE = (73 << 8);

    private static final int SPIDER1_X = (SPIDERS_X + SPIDER_SPACE * 0);
    private static final int SPIDER2_X = (SPIDERS_X + SPIDER_SPACE * 1);
    private static final int SPIDER3_X = (SPIDERS_X + SPIDER_SPACE * 2);
    private static final int SPIDER4_X = (SPIDERS_X + SPIDER_SPACE * 3);
    private static final int SPIDER5_X = (SPIDERS_X + SPIDER_SPACE * 4);
    private static final int SPIDER6_X = (SPIDERS_X + SPIDER_SPACE * 5);

    private static final int MOVE_SPEED = 0x0200;

    private static final int INIT_PLAYER_Y = (95 << 8);
    private static final int INIT_PLAYER_X = (SPIDER1_X); 
    private static final int INIT_POSITION = 0; 

    public static final int INIT_ENTRANCE_X1 = (10 << 8);
    public static final int INIT_ENTRANCE_X2 = (118 << 8);
    public static final int INIT_ENTRANCE_Y = (96 << 8);

    private static final int LEVEL_EASY_TIMEDELAY = 100;

    private static final int LEVEL_EASY_LIVES = 4;

    private static final int LEVEL_EASY_SPIDER_SPEED = (0x0150);  

    private static final int LEVEL_NORMAL_TIMEDELAY = 90;

    private static final int LEVEL_NORMAL_LIVES = 3;

    private static final int LEVEL_NORMAL_SPIDER_SPEED = (0x0180);

    private static final int LEVEL_HARD_TIMEDELAY = 80;

    private static final int LEVEL_HARD_LIVES = 2;

    private static final int LEVEL_HARD_SPIDER_SPEED = (0x0200);

    private static final int SCORES_TWIG = 10;

    private static final int SPRITEDATALENGTH = 8;

    public static final int SPRITE_ANT                      = 0;
    public static final int SPRITE_ANT_LEFT                 = 1;
    public static final int SPRITE_ANT_RIGHT                = 2;
    public static final int SPRITE_ANT_UNLOAD               = 3;
    public static final int SPRITE_ANT_LOAD                 = 4;
    public static final int SPRITE_ANT_DEAD                 = 5;
    public static final int SPRITE_ANT_LOADER               = 6;
    public static final int SPRITE_ANT_UNLOADER             = 7;
    public static final int SPRITE_SPIDER                   = 8;
    public static final int SPRITE_SPIDER_UP                = 9;
    public static final int SPRITE_SPIDER_DOWN              = 10;
    public static final int SPRITE_SPIDER_CATCH             = 11;

    public static final int MAX_SPRITES = 8;

    public static final int GAMEACTION_ANT_LOAD             = 0;
    public static final int GAMEACTION_ANT_UNLOAD           = 1;
    public static final int GAMEACTION_ANT_UNLOADED         = 2;
    public static final int GAMEACTION_SPIDER_CATCH         = 3;

    public int i_playerAttemptions;

    private int i_timedelay;

    public int i_GameTimer;

    public int i_borndelay;
    private int i_lastjump;

    public boolean b_AntTwig;

    private int i_VSpeed;


    public Sprite pPlayerAnt;

    public Sprite pAnt;

    public Sprite[] ap_Sprites;

    public MoveObject p_MoveObject;

    private int i8_gameScreenWidth;

    private int i8_gameScreenHeight;

    private static final int[] ai_SpriteParameters = new int[]
    {
        0x1000, 0x2000, 0x0400, 0x2000, 2, 1, Sprite.SPRITE_ALIGN_CENTER, Sprite.ANIMATION_CYCLIC,
        0x1000, 0x2000, 0x0400, 0x2000, 3, 1, Sprite.SPRITE_ALIGN_CENTER, Sprite.ANIMATION_PENDULUM,
        0x1000, 0x2000, 0x0400, 0x2000, 3, 1, Sprite.SPRITE_ALIGN_CENTER, Sprite.ANIMATION_PENDULUM,
        0x1000, 0x2000, 0x0400, 0x0400, 3, 1, Sprite.SPRITE_ALIGN_CENTER, Sprite.ANIMATION_FROZEN,
        0x1000, 0x2000, 0x0400, 0x0400, 3, 1, Sprite.SPRITE_ALIGN_CENTER, Sprite.ANIMATION_FROZEN,
        0x1000, 0x2000, 0x0400, 0x0400, 5, 2, Sprite.SPRITE_ALIGN_CENTER, Sprite.ANIMATION_FROZEN,
        0x0A00, 0x0F00, 0x0A00, 0x0F00, 1, 1, Sprite.SPRITE_ALIGN_CENTER, Sprite.ANIMATION_CYCLIC,
        0x0A00, 0x0F00, 0x0A00, 0x0F00, 1, 1, Sprite.SPRITE_ALIGN_CENTER, Sprite.ANIMATION_CYCLIC,
        0x1000, 0x1800, 0x0400, 0x1800, 2, 1, Sprite.SPRITE_ALIGN_CENTER, Sprite.ANIMATION_CYCLIC,
        0x1000, 0x1800, 0x0400, 0x1800, 2, 1, Sprite.SPRITE_ALIGN_CENTER, Sprite.ANIMATION_CYCLIC,
        0x1000, 0x1800, 0x0400, 0x1800, 2, 1, Sprite.SPRITE_ALIGN_CENTER, Sprite.ANIMATION_CYCLIC,
        0x1000, 0x1800, 0x0400, 0x1800, 2, 1, Sprite.SPRITE_ALIGN_CENTER, Sprite.ANIMATION_CYCLIC
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

    private void processSprites()
    {
        for (int li = 0; li < MAX_SPRITES; li++)
        {
            Sprite p_Sprite = ap_Sprites[li];
            if (p_Sprite.lg_SpriteActive)
            {
                if (p_Sprite.processAnimation())
                {
                    switch(p_Sprite.i_ObjectType)
                    {
                        case SPRITE_SPIDER_CATCH:
                            break;
                    }
                } 

                switch(p_Sprite.i_ObjectType)
                {
                    case SPRITE_SPIDER:
                        if(p_Sprite.i_ObjectState > 0)
                        {
                            p_Sprite.i_ObjectState--;
                        }
                        else
                        {
                            int delay = MIN_SPIDER_DELAY + getRandomInt(MAX_SPIDER_DELAY - MIN_SPIDER_DELAY);
                            switch(getRandomInt(30)/10)
                            {
                                case 0:
                                    initSpriteFromArray(p_Sprite, SPRITE_SPIDER);
                                    break;
                                case 1:
                                    initSpriteFromArray(p_Sprite, SPRITE_SPIDER_DOWN);
                                    break;
                                default:
                                case 2:

                                    if(p_Sprite.i_mainY < SPIDERS_Y)
                                    {
                                      initSpriteFromArray(p_Sprite, SPRITE_SPIDER_UP);
                                    }
                                    break;
                            }
                            p_Sprite.i_ObjectState = delay;
                            if(p_Sprite.i_mainY >= STOP_LINE)
                            {
                                initSpriteFromArray(p_Sprite, SPRITE_SPIDER_UP);
                            }
                        }
                        break;
                    case SPRITE_SPIDER_UP:
                        if(p_Sprite.i_ObjectState > 0)
                        {
                            p_Sprite.setMainPointXY(p_Sprite.i_mainX, p_Sprite.i_mainY - SPIDER_UP_SPEED);
                            p_Sprite.i_ObjectState--;
                            if(p_Sprite.i_mainY < SPIDERS_Y)
                            {
                                p_Sprite.setMainPointXY(p_Sprite.i_mainX, SPIDERS_Y);
                            }
                        }
                        else
                        {
                            initSpriteFromArray(p_Sprite, SPRITE_SPIDER);
                        }
                        break;
                    case SPRITE_SPIDER_DOWN:
                        if(p_Sprite.i_ObjectState > 0)
                        {
                            p_Sprite.setMainPointXY(p_Sprite.i_mainX, p_Sprite.i_mainY + i_VSpeed);
                            p_Sprite.i_ObjectState--;
                            if(p_Sprite.i_mainY > STOP_LINE)
                            {
                                p_Sprite.setMainPointXY(p_Sprite.i_mainX, STOP_LINE);
                            }
                        }
                        else
                        {
                            initSpriteFromArray(p_Sprite, SPRITE_SPIDER);
                        }
                        break;
                }

                if(p_Sprite.isCollided(pPlayerAnt) && pPlayerAnt.i_ObjectType != SPRITE_ANT_DEAD)
                {
                    m_pAbstractGameActionListener.processGameAction(GAMEACTION_SPIDER_CATCH);
                    initSpriteFromArray(p_Sprite, SPRITE_SPIDER_CATCH);
                    initSpriteFromArray(pPlayerAnt, SPRITE_ANT_DEAD);
                    pPlayerAnt.setMainPointXY(p_Sprite.i_mainX, pPlayerAnt.i_mainY);
                }


            } 
        } 
    }

    public String getGameTextID()
    {
        return "SPIDERS";
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

        pPlayerAnt = new Sprite(0);
        pAnt = new Sprite(-1);

        initSpriteFromArray(pPlayerAnt, SPRITE_ANT);
        pPlayerAnt.lg_SpriteActive = true;

        pPlayerAnt.setMainPointXY(INIT_PLAYER_X, INIT_PLAYER_Y);
        pPlayerAnt.i_ObjectState = INIT_POSITION; 

        initSpriteFromArray(pAnt, SPRITE_ANT_LOADER);
        pAnt.lg_SpriteActive = true;
        pAnt.setMainPointXY(INIT_ENTRANCE_X2, INIT_ENTRANCE_Y);

        b_AntTwig = false;

        p_MoveObject = new MoveObject();

        return true;
    }

    private boolean processAnt(int _buttonState)
    {
        if (pPlayerAnt.processAnimation())
        {
            switch(pPlayerAnt.i_ObjectType)
            {
                case SPRITE_ANT_DEAD:
                    return false;
                case SPRITE_ANT_UNLOAD:
                    addScore(SCORES_TWIG);
                    initSpriteFromArray(pPlayerAnt, SPRITE_ANT);
                    m_pAbstractGameActionListener.processGameAction(GAMEACTION_ANT_UNLOADED);
                    break;
                case SPRITE_ANT_LOAD:
                    initSpriteFromArray(pPlayerAnt, SPRITE_ANT);
                    break;
            }
        }
        if(pAnt.lg_SpriteActive && pPlayerAnt.i_ObjectType != SPRITE_ANT_DEAD)
        {
            pAnt.processAnimation();

            if(b_AntTwig)
            {
                if((pPlayerAnt.i_mainX == SPIDER1_X) && (pAnt.i_mainX <= SPIDER1_X))
                {
                    initSpriteFromArray(pPlayerAnt, SPRITE_ANT_UNLOAD);
                    m_pAbstractGameActionListener.processGameAction(GAMEACTION_ANT_UNLOAD);
                    b_AntTwig = false;
                    initSpriteFromArray(pAnt, SPRITE_ANT_LOADER);
                    pAnt.setMainPointXY(INIT_ENTRANCE_X2, INIT_ENTRANCE_Y);
                }
            }
            else
            {
                if((pPlayerAnt.i_mainX == SPIDER6_X) && (pAnt.i_mainX >= SPIDER6_X))
                {
                    initSpriteFromArray(pPlayerAnt, SPRITE_ANT_LOAD);
                    m_pAbstractGameActionListener.processGameAction(GAMEACTION_ANT_LOAD);
                    b_AntTwig = true;
                    initSpriteFromArray(pAnt, SPRITE_ANT_UNLOADER);
                    pAnt.setMainPointXY(INIT_ENTRANCE_X1, INIT_ENTRANCE_Y);
                }
            }
        }

        int i_mx = pPlayerAnt.i_mainX;
        int i_my = pPlayerAnt.i_mainY;

        int i_w = pPlayerAnt.i_width / 2;
        int i_h = pPlayerAnt.i_height / 2;



        switch(pPlayerAnt.i_ObjectType)
        {
            case SPRITE_ANT_LEFT:
                pPlayerAnt.setMainPointXY(pPlayerAnt.i_mainX - MOVE_SPEED, pPlayerAnt.i_mainY);
                switch(pPlayerAnt.i_mainX)
                {
                    case SPIDER1_X:
                        pPlayerAnt.i_ObjectState = 0;
                        initSpriteFromArray(pPlayerAnt, SPRITE_ANT);
                        break;
                    case SPIDER2_X:
                        pPlayerAnt.i_ObjectState = 1;
                        if ((_buttonState & MoveObject.BUTTON_LEFT) == 0)
                        {
                            initSpriteFromArray(pPlayerAnt, SPRITE_ANT);
                        }
                        break;
                    case SPIDER3_X:
                        pPlayerAnt.i_ObjectState = 2;
                        if ((_buttonState & MoveObject.BUTTON_LEFT) == 0)
                        {
                            initSpriteFromArray(pPlayerAnt, SPRITE_ANT);
                        }
                        break;
                    case SPIDER4_X:
                        pPlayerAnt.i_ObjectState = 3;
                        if ((_buttonState & MoveObject.BUTTON_LEFT) == 0)
                        {
                            initSpriteFromArray(pPlayerAnt, SPRITE_ANT);
                        }
                        break;
                    case SPIDER5_X:
                        pPlayerAnt.i_ObjectState = 4;
                        if ((_buttonState & MoveObject.BUTTON_LEFT) == 0)
                        {
                            initSpriteFromArray(pPlayerAnt, SPRITE_ANT);
                        }
                        break;
                }
                break;
            case SPRITE_ANT_RIGHT:
                pPlayerAnt.setMainPointXY(pPlayerAnt.i_mainX + MOVE_SPEED, pPlayerAnt.i_mainY);
                switch(pPlayerAnt.i_mainX)
                {
                    case SPIDER2_X:
                        pPlayerAnt.i_ObjectState = 1;
                        if ((_buttonState & MoveObject.BUTTON_RIGHT) == 0)
                        {
                            initSpriteFromArray(pPlayerAnt, SPRITE_ANT);
                        }
                        break;
                    case SPIDER3_X:
                        pPlayerAnt.i_ObjectState = 2;
                        if ((_buttonState & MoveObject.BUTTON_RIGHT) == 0)
                        {
                            initSpriteFromArray(pPlayerAnt, SPRITE_ANT);
                        }
                        break;
                    case SPIDER4_X:
                        pPlayerAnt.i_ObjectState = 3;
                        if ((_buttonState & MoveObject.BUTTON_RIGHT) == 0)
                        {
                            initSpriteFromArray(pPlayerAnt, SPRITE_ANT);
                        }
                        break;
                    case SPIDER5_X:
                        pPlayerAnt.i_ObjectState = 4;
                        if ((_buttonState & MoveObject.BUTTON_RIGHT) == 0)
                        {
                            initSpriteFromArray(pPlayerAnt, SPRITE_ANT);
                        }
                        break;
                    case SPIDER6_X:
                        pPlayerAnt.i_ObjectState = 5;
                        initSpriteFromArray(pPlayerAnt, SPRITE_ANT);
                        break;

                }
                break;
            case SPRITE_ANT:
            {
                if ((_buttonState & MoveObject.BUTTON_LEFT) != 0)
                {
                    if(pPlayerAnt.i_ObjectState > 0)
                    {
                        initSpriteFromArray(pPlayerAnt, SPRITE_ANT_LEFT);
                    }
                    else
                    {
                        initSpriteFromArray(pPlayerAnt, SPRITE_ANT);
                    }
                }
                else if ((_buttonState & MoveObject.BUTTON_RIGHT) != 0)
                {
                    if(pPlayerAnt.i_ObjectState < 5)
                    {
                        initSpriteFromArray(pPlayerAnt, SPRITE_ANT_RIGHT);
                    }
                    else
                    {
                        initSpriteFromArray(pPlayerAnt, SPRITE_ANT);
                    }
                }

                if ((_buttonState & MoveObject.BUTTON_UP) != 0)
                {
                    int a = 0;
                }

                pPlayerAnt.setMainPointXY(i_mx, i_my);
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
                    i_VSpeed = LEVEL_EASY_SPIDER_SPEED;
                    i_playerAttemptions = LEVEL_EASY_LIVES;
                }
                break;
            case GAMELEVEL_NORMAL:
                {
                    i_timedelay = LEVEL_NORMAL_TIMEDELAY;
                    i_VSpeed = LEVEL_NORMAL_SPIDER_SPEED;
                    i_playerAttemptions = LEVEL_NORMAL_LIVES;
                }
                break;
            case GAMELEVEL_HARD:
                {
                    i_timedelay = LEVEL_HARD_TIMEDELAY;
                    i_VSpeed = LEVEL_HARD_SPIDER_SPEED;
                    i_playerAttemptions = LEVEL_HARD_LIVES;
                }
                break;
        }

        i_GameTimer = 0; 
        i_lastjump = i_GameTimer;
        generateSpiders();

        return true;
    }

    public void resumeGameAfterPlayerLostOrPaused()
    {
        super.resumeGameAfterPlayerLostOrPaused();

        deinitState();
        initState();

        i_GameTimer = 0; 
        i_lastjump = i_GameTimer;
        generateSpiders();
    }

    public void deinitState()
    {
        ap_Sprites = null;
        pPlayerAnt = null;
        pAnt = null;
        p_MoveObject = null;

        Runtime.getRuntime().gc();
    }

    public int _getMaximumDataSize()
    {
        return (2+MAX_SPRITES)*(Sprite.DATASIZE_BYTES+1) + 18;
    }

    public void _saveGameData(DataOutputStream _outputStream) throws IOException
    {
        _outputStream.writeByte(i_playerAttemptions);
        _outputStream.writeInt(i_GameTimer);
        _outputStream.writeInt(i_lastjump);
        _outputStream.writeInt(i_borndelay);
        _outputStream.writeInt(i_VSpeed);
        _outputStream.writeBoolean(b_AntTwig);

        for(int li=0;li<MAX_SPRITES;li++)
        {
            _outputStream.writeByte(ap_Sprites[li].i_ObjectType);
            ap_Sprites[li].writeSpriteToStream(_outputStream);
        }

        _outputStream.writeByte(pPlayerAnt.i_ObjectType);
        pPlayerAnt.writeSpriteToStream(_outputStream);
        _outputStream.writeByte(pAnt.i_ObjectType);
        pAnt.writeSpriteToStream(_outputStream);
    }

    public void _loadGameData(DataInputStream _inputStream) throws IOException
    {
        i_playerAttemptions = _inputStream.readByte();
        i_GameTimer = _inputStream.readInt();
        i_lastjump = _inputStream.readInt();
        i_borndelay = _inputStream.readInt();
        i_VSpeed = _inputStream.readInt();
        b_AntTwig = _inputStream.readBoolean();

        for(int li=0;li<MAX_SPRITES;li++)
        {
            int i_type = _inputStream.readByte();
            initSpriteFromArray(ap_Sprites[li],i_type);
            ap_Sprites[li].readSpriteFromStream(_inputStream);
        }

        initSpriteFromArray(pPlayerAnt,_inputStream.readUnsignedByte());
        pPlayerAnt.readSpriteFromStream(_inputStream);

        initSpriteFromArray(pAnt,_inputStream.readUnsignedByte());
        pAnt.readSpriteFromStream(_inputStream);
    }

    private void generateSpiders()
    {
        int y = SPIDERS_Y;
        int x = SPIDERS_X;

        for(int i=0; i<6; i++)
        {
            Sprite p_emptySprite = getInactiveSprite();
            if (p_emptySprite != null)
            {
                initSpriteFromArray(p_emptySprite, SPRITE_SPIDER);
                p_emptySprite.lg_SpriteActive = true;
                p_emptySprite.setMainPointXY(x, y);
                p_emptySprite.i_ObjectState = 0;
            }
            x += SPIDER_SPACE;
        }
    }

    public int nextGameStep()
    {
        CAbstractPlayer p_player = m_pPlayerList[0];
        p_player.nextPlayerMove(this, p_MoveObject);

        if (!processAnt(p_MoveObject.i_buttonState) )
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
