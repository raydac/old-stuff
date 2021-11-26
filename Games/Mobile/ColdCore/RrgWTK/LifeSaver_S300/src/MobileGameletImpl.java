
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.DataInputStream;

public class MobileGameletImpl extends MobileGamelet
{
    public static final int WINDOWS_X = (32-7) << 8;
    public static final int WINDOWS_Y = (22) << 8;
    public static final int WINDOW_SIZE = (15) << 8;
    public static final int WINDOW_SPACE = (5) << 8;

    private static final int WINDOW1_X = (WINDOWS_X + (WINDOW_SIZE + WINDOW_SPACE) * 0 );
    private static final int WINDOW2_X = (WINDOWS_X + (WINDOW_SIZE + WINDOW_SPACE) * 1 );
    private static final int WINDOW3_X = (WINDOWS_X + (WINDOW_SIZE + WINDOW_SPACE) * 2 );
    private static final int WINDOW4_X = (WINDOWS_X + (WINDOW_SIZE + WINDOW_SPACE) * 3 );
    private static final int WINDOW5_X = (WINDOWS_X + (WINDOW_SIZE + WINDOW_SPACE) * 4 );

    private static final int AMBULANCE_X = (-10) << 8;
    public static final int DEAD_LINE   = (128 - 8) << 8;

    private static final int MOVE_SPEED = 0x0400;
    private static final int FALL_SPEED = 0x0300;
    private static final int RUN_SPEED  = 0x0500;

    private static final int MAX_FIRE_LEVEL  = 1000;

    private static final int INIT_PLAYER_Y = (DEAD_LINE );
    private static final int INIT_PLAYER_X = (WINDOW1_X);

    public static final int STEP_VICTIMFREQ_MASK = 0x001F;
    public static final int LIMIT_VICTIMFREQ = 30;

    public static final int STATE_FLOOR_NORMAL = 0;
    public static final int STATE_FLOOR_FIRING = 1;
    public static final int STATE_FLOOR_BURNT = 2;


    private static final int LEVEL_EASY_TIMEDELAY = 100;

    private static final int LEVEL_EASY_LIVES = 4;

    private static final int LEVEL_EASY_VICTIMFREQ = 100;

    private static final int LEVEL_EASY_FIRE = MAX_FIRE_LEVEL / 5 * 1;

    private static final int LEVEL_NORMAL_TIMEDELAY = 90;

    private static final int LEVEL_NORMAL_LIVES = 3;

    private static final int LEVEL_NORMAL_VICTIMFREQ = 80;

    private static final int LEVEL_NORMAL_FIRE = MAX_FIRE_LEVEL / 5 * 2;

    private static final int LEVEL_HARD_TIMEDELAY = 80;

    private static final int LEVEL_HARD_LIVES = 2;

    private static final int LEVEL_HARD_VICTIMFREQ = 60;

    private static final int LEVEL_HARD_FIRE = MAX_FIRE_LEVEL / 5 * 3;

    private static final int SCORES_SAVE_VICTIM = 10;
    private static final int SCORES_FLOOR = 50;

    private static final int SPRITEDATALENGTH = 8;

    public static final int SPRITE_FIREMEN                      = 0;
    public static final int SPRITE_FIREMEN_LEFT                 = 1;
    public static final int SPRITE_FIREMEN_RIGHT                = 2;
    public static final int SPRITE_FIREMEN_PUT_OUT              = 3;
    public static final int SPRITE_VICTIM_HELP                  = 4;
    public static final int SPRITE_VICTIM_JUMP_OUT              = 5;
    public static final int SPRITE_VICTIM_LANDING               = 6;
    public static final int SPRITE_VICTIM_RUN                   = 7;
    public static final int SPRITE_VICTIM_DEAD                  = 8;

    public static final int MAX_SPRITES = 8;


    public static final int GAMEACTION_FIREMEN_PUT_OUT      = 0;
    public static final int GAMEACTION_FIREMEN_RESCUE       = 1;
    public static final int GAMEACTION_VICTIM_GENERATED     = 2;
    public static final int GAMEACTION_VICTIM_HELP          = 3;
    public static final int GAMEACTION_VICTIM_JUMP          = 4;
    public static final int GAMEACTION_VICTIM_DEAD          = 5;
    public static final int GAMEACTION_VICTIM_LANDING       = 6;
    public static final int GAMEACTION_VICTIM_RUN           = 7;
    public static final int GAMEACTION_VICTIM_SAVED         = 8;
    public static final int GAMEACTION_FLOOR_BURNING        = 9;


    public int m_iPlayerAttemptions;

    private int i_timedelay;

    public int i_GameTimer;

    public int i_borndelay;
    private int i_lastjump;

    public int m_nFireLevel;

    public int m_nFloor_States[] =
    {
        STATE_FLOOR_NORMAL,
        STATE_FLOOR_NORMAL,
        STATE_FLOOR_NORMAL,
        STATE_FLOOR_NORMAL,
        STATE_FLOOR_NORMAL
    };


    public Sprite pPlayerFiremen;

    public Sprite[] ap_Sprites;

    public MoveObject p_MoveObject;

    private int i8_gameScreenWidth;

    private int i8_gameScreenHeight;

    private static final int[] ai_SpriteParameters = new int[]
    {
        0x2000, 0x0A00, 0x0800, 0x0200, 1, 1, Sprite.SPRITE_ALIGN_CENTER, Sprite.ANIMATION_CYCLIC,
        0x2000, 0x0A00, 0x2000, 0x0A00, 2, 1, Sprite.SPRITE_ALIGN_CENTER, Sprite.ANIMATION_CYCLIC,
        0x2000, 0x0A00, 0x2000, 0x0A00, 2, 1, Sprite.SPRITE_ALIGN_CENTER, Sprite.ANIMATION_CYCLIC,
        0x2000, 0x0A00, 0x2000, 0x0A00, 4, 1, Sprite.SPRITE_ALIGN_CENTER, Sprite.ANIMATION_CYCLIC,
        0x0F00, 0x0F00, 0x0F00, 0x0F00, 4, 3, Sprite.SPRITE_ALIGN_CENTER, Sprite.ANIMATION_FROZEN,
        0x0F00, 0x0F00, 0x0800, 0x0200, 4, 1, Sprite.SPRITE_ALIGN_CENTER, Sprite.ANIMATION_FROZEN,
        0x2000, 0x0A00, 0x2000, 0x0A00, 4, 1, Sprite.SPRITE_ALIGN_CENTER, Sprite.ANIMATION_FROZEN,
        0x0F00, 0x0F00, 0x0800, 0x0200, 2, 1, Sprite.SPRITE_ALIGN_CENTER, Sprite.ANIMATION_CYCLIC,
        0x0F00, 0x0F00, 0x0F00, 0x0F00, 4, 1, Sprite.SPRITE_ALIGN_CENTER, Sprite.ANIMATION_FROZEN
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
            if (ap_Sprites[li].lg_SpriteActive)
            {
                if (ap_Sprites[li].processAnimation())
                {
                    switch(ap_Sprites[li].i_ObjectType)
                    {
                        case SPRITE_VICTIM_DEAD:
                            ap_Sprites[li].lg_SpriteActive = false;
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
                            break;
                        case SPRITE_VICTIM_LANDING:
                            initSpriteFromArray(ap_Sprites[li], SPRITE_VICTIM_RUN);
                            m_pAbstractGameActionListener.processGameAction(GAMEACTION_VICTIM_RUN);
                            break;
                        case SPRITE_VICTIM_HELP:
                            initSpriteFromArray(ap_Sprites[li], SPRITE_VICTIM_JUMP_OUT);
                            m_pAbstractGameActionListener.processGameAction(GAMEACTION_VICTIM_JUMP);
                            break;
                    }
                } 

                if (ap_Sprites[li].i_ObjectType == SPRITE_VICTIM_JUMP_OUT)
                {
                    if(ap_Sprites[li].i_mainY >= DEAD_LINE)
                    {
                        initSpriteFromArray(ap_Sprites[li], SPRITE_VICTIM_DEAD);
                        m_pAbstractGameActionListener.processGameAction(GAMEACTION_VICTIM_DEAD);
                    }
                    else
                    {
                        ap_Sprites[li].setMainPointXY(ap_Sprites[li].i_mainX, ap_Sprites[li].i_mainY + FALL_SPEED);
                    }

                    if( (pPlayerFiremen.i_ObjectType == SPRITE_FIREMEN) && ap_Sprites[li].isCollided(pPlayerFiremen) )
                    {
                        initSpriteFromArray(ap_Sprites[li], SPRITE_VICTIM_LANDING);
                        m_pAbstractGameActionListener.processGameAction(GAMEACTION_VICTIM_LANDING);

                        addScore(SCORES_SAVE_VICTIM);
                    }
                }

                if (ap_Sprites[li].i_ObjectType == SPRITE_VICTIM_RUN)
                {
                    ap_Sprites[li].setMainPointXY(ap_Sprites[li].i_mainX - RUN_SPEED, ap_Sprites[li].i_mainY);
                    if(ap_Sprites[li].i_mainX < AMBULANCE_X)
                    {
                        m_pAbstractGameActionListener.processGameAction(GAMEACTION_VICTIM_SAVED);
                        ap_Sprites[li].lg_SpriteActive = false;
                    }
                }
            } 
        } 
    }

    public String getGameTextID()
    {
        return "FIREFIGHTERS";
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

        pPlayerFiremen = new Sprite(0);

        initSpriteFromArray(pPlayerFiremen, SPRITE_FIREMEN);
        pPlayerFiremen.lg_SpriteActive = true;

        int i8_cx = INIT_PLAYER_X; 
        int i8_cy = INIT_PLAYER_Y; 

        pPlayerFiremen.setMainPointXY(i8_cx, i8_cy);
        pPlayerFiremen.i_ObjectState = 0;

        p_MoveObject = new MoveObject();

        return true;
    }

    private boolean processFiremen(int _buttonState)
    {
        if (pPlayerFiremen.processAnimation())
        {
        }

        switch(pPlayerFiremen.i_ObjectType)
        {
            case SPRITE_FIREMEN_LEFT:
                pPlayerFiremen.setMainPointXY(pPlayerFiremen.i_mainX - MOVE_SPEED, pPlayerFiremen.i_mainY);
                switch(pPlayerFiremen.i_mainX)
                {
                    case WINDOW1_X:
                        pPlayerFiremen.i_ObjectState = 0;
                        initSpriteFromArray(pPlayerFiremen, SPRITE_FIREMEN);
                        break;
                    case WINDOW2_X:
                        pPlayerFiremen.i_ObjectState = 1;
                        initSpriteFromArray(pPlayerFiremen, SPRITE_FIREMEN);
                        break;
                    case WINDOW3_X:
                        pPlayerFiremen.i_ObjectState = 2;
                        initSpriteFromArray(pPlayerFiremen, SPRITE_FIREMEN);
                        break;
                    case WINDOW4_X:
                        pPlayerFiremen.i_ObjectState = 3;
                        initSpriteFromArray(pPlayerFiremen, SPRITE_FIREMEN);
                        m_pAbstractGameActionListener.processGameAction(GAMEACTION_FIREMEN_RESCUE);
                        break;
                }
                break;
            case SPRITE_FIREMEN_RIGHT:
                pPlayerFiremen.setMainPointXY(pPlayerFiremen.i_mainX + MOVE_SPEED, pPlayerFiremen.i_mainY);
                switch(pPlayerFiremen.i_mainX)
                {
                    case WINDOW5_X:
                        pPlayerFiremen.i_ObjectState = 4;
                        initSpriteFromArray(pPlayerFiremen, SPRITE_FIREMEN_PUT_OUT);
                        m_pAbstractGameActionListener.processGameAction(GAMEACTION_FIREMEN_PUT_OUT);
                        break;
                    case WINDOW2_X:
                        pPlayerFiremen.i_ObjectState = 1;
                        initSpriteFromArray(pPlayerFiremen, SPRITE_FIREMEN);
                        break;
                    case WINDOW3_X:
                        pPlayerFiremen.i_ObjectState = 2;
                        initSpriteFromArray(pPlayerFiremen, SPRITE_FIREMEN);
                        break;
                    case WINDOW4_X:
                        pPlayerFiremen.i_ObjectState = 3;
                        initSpriteFromArray(pPlayerFiremen, SPRITE_FIREMEN);
                        break;
                }
                break;
            case SPRITE_FIREMEN_PUT_OUT:
                if ((_buttonState & MoveObject.BUTTON_LEFT) != 0)
                {
                    if(pPlayerFiremen.i_ObjectState > 0)
                    {
                        initSpriteFromArray(pPlayerFiremen, SPRITE_FIREMEN_LEFT);
                    }
                }
                break;
        }
        if(pPlayerFiremen.i_ObjectType == SPRITE_FIREMEN)
        {
                int i_x = pPlayerFiremen.i_ScreenX;
                int i_y = pPlayerFiremen.i_ScreenY;

                int i_mx = pPlayerFiremen.i_mainX;
                int i_my = pPlayerFiremen.i_mainY;

                int i_w = pPlayerFiremen.i_width;
                int i_h = pPlayerFiremen.i_height;

                int i_spV, i_spH;

                if ((_buttonState & MoveObject.BUTTON_LEFT) != 0)
                {
                    if(pPlayerFiremen.i_ObjectState > 0)
                    {
                        initSpriteFromArray(pPlayerFiremen, SPRITE_FIREMEN_LEFT);
                    }
                    else
                    {
                        initSpriteFromArray(pPlayerFiremen, SPRITE_FIREMEN);
                    }
                }
                else if ((_buttonState & MoveObject.BUTTON_RIGHT) != 0)
                {
                    if(pPlayerFiremen.i_ObjectState < 4)
                    {
                        initSpriteFromArray(pPlayerFiremen, SPRITE_FIREMEN_RIGHT);
                    }
                    else
                    {
                    }
                }

                pPlayerFiremen.setMainPointXY(i_mx, i_my);
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
                    m_nFireLevel = LEVEL_EASY_FIRE;
                    i_borndelay = LEVEL_EASY_VICTIMFREQ;
                    m_iPlayerAttemptions = LEVEL_EASY_LIVES;
                }
                ;
                break;
            case GAMELEVEL_NORMAL:
                {
                    i_timedelay = LEVEL_NORMAL_TIMEDELAY;
                    m_nFireLevel = LEVEL_NORMAL_FIRE;
                    i_borndelay = LEVEL_NORMAL_VICTIMFREQ;
                    m_iPlayerAttemptions = LEVEL_NORMAL_LIVES;
                }
                ;
                break;
            case GAMELEVEL_HARD:
                {
                    i_timedelay = LEVEL_HARD_TIMEDELAY;
                    m_nFireLevel = LEVEL_HARD_FIRE;
                    i_borndelay = LEVEL_HARD_VICTIMFREQ;
                    m_iPlayerAttemptions = LEVEL_HARD_LIVES;
                }
                ;
                break;
        }

        i_GameTimer = 0; 
        i_lastjump = i_GameTimer;
        for(int f=0;f<5;f++) m_nFloor_States[f] = STATE_FLOOR_NORMAL;

        return true;
    }

    public void resumeGameAfterPlayerLostOrPaused()
    {
        super.resumeGameAfterPlayerLostOrPaused();

        deinitState();
        initState();

        switch(getGameDifficultLevel())
        {
            case GAMELEVEL_EASY:
                m_nFireLevel = LEVEL_EASY_FIRE;
                i_borndelay = LEVEL_EASY_VICTIMFREQ;
                break;
            case GAMELEVEL_NORMAL:
                m_nFireLevel = LEVEL_NORMAL_FIRE;
                i_borndelay = LEVEL_NORMAL_VICTIMFREQ;
                break;
            case GAMELEVEL_HARD:
                m_nFireLevel = LEVEL_HARD_FIRE;
                i_borndelay = LEVEL_HARD_VICTIMFREQ;
                break;
        }

        i_GameTimer = 0; 
        i_lastjump = i_GameTimer;
        for(int f=0;f<5;f++) m_nFloor_States[f] = STATE_FLOOR_NORMAL;
    }

    public void deinitState()
    {
        ap_Sprites = null;
        pPlayerFiremen = null;
        p_MoveObject = null;

        Runtime.getRuntime().gc();
    }

    public int _getMaximumDataSize()
    {
        return MAX_SPRITES*(Sprite.DATASIZE_BYTES+1)+6+Sprite.DATASIZE_BYTES+1 + 6;
    }

    public void _saveGameData(DataOutputStream _outputStream) throws IOException
    {
        _outputStream.writeByte(m_iPlayerAttemptions);
        _outputStream.writeInt(i_GameTimer);
        _outputStream.writeInt(i_lastjump);
        _outputStream.writeInt(i_borndelay);
        _outputStream.writeInt(m_nFireLevel);

        for(int f=0;f<5;f++) _outputStream.writeByte(m_nFloor_States[f]);

        for(int li=0;li<MAX_SPRITES;li++)
        {
            _outputStream.writeByte(ap_Sprites[li].i_ObjectType);
            ap_Sprites[li].writeSpriteToStream(_outputStream);
        }

        _outputStream.writeByte(pPlayerFiremen.i_ObjectType);
        pPlayerFiremen.writeSpriteToStream(_outputStream);
    }

    public void _loadGameData(DataInputStream _inputStream) throws IOException
    {
        m_iPlayerAttemptions = _inputStream.readByte();
        i_GameTimer = _inputStream.readInt();
        i_lastjump = _inputStream.readInt();
        i_borndelay = _inputStream.readInt();
        m_nFireLevel = _inputStream.readInt();

        for(int f=0;f<5;f++) m_nFloor_States[f] = _inputStream.readByte();

        for(int li=0;li<MAX_SPRITES;li++)
        {
            int i_type = _inputStream.readByte();
            initSpriteFromArray(ap_Sprites[li],i_type);
            ap_Sprites[li].readSpriteFromStream(_inputStream);
        }

        initSpriteFromArray(pPlayerFiremen,_inputStream.readUnsignedByte());
        pPlayerFiremen.readSpriteFromStream(_inputStream);
    }

    private void generateNewVictim()
    {
        if((i_lastjump + i_borndelay) < i_GameTimer )
        {
            i_lastjump = i_GameTimer;
            Sprite p_emptySprite = getInactiveSprite();
            if (p_emptySprite != null)
            {
                int y = WINDOWS_Y;
                int x = WINDOWS_X;

                switch(getRandomInt(4))
                {
                    case 0:
                        x = WINDOW1_X;
                        break;
                    case 1:
                        x = WINDOW2_X;
                        break;
                    default:
                    case 2:
                        x = WINDOW3_X;
                        break;
                    case 3:
                        x = WINDOW4_X;
                        break;
                }

                initSpriteFromArray(p_emptySprite, SPRITE_VICTIM_HELP);
                p_emptySprite.lg_SpriteActive = true;
                p_emptySprite.setMainPointXY(x, y);
                m_pAbstractGameActionListener.processGameAction(GAMEACTION_VICTIM_GENERATED);

            }
        }
    }


    public int nextGameStep()
    {
        CAbstractPlayer p_player = m_pPlayerList[0];
        p_player.nextPlayerMove(this, p_MoveObject);

        if( (pPlayerFiremen.i_ObjectState == 4) && (m_nFireLevel > ( MAX_FIRE_LEVEL / 5 * 1 + 1) ) )
        {
            m_nFireLevel--;
            switch(m_nFireLevel)
            {
                case (MAX_FIRE_LEVEL / 5 * 5):
                    m_nFireLevel = (MAX_FIRE_LEVEL / 5 * 4) + (MAX_FIRE_LEVEL / 10);
                    addScore(SCORES_FLOOR);
                    m_nFloor_States[0] = STATE_FLOOR_BURNT;
                    break;
                case (MAX_FIRE_LEVEL / 5 * 4):
                    m_nFireLevel = (MAX_FIRE_LEVEL / 5 * 3) + (MAX_FIRE_LEVEL / 10);
                    addScore(SCORES_FLOOR);
                    m_nFloor_States[1] = STATE_FLOOR_BURNT;
                    break;
                case (MAX_FIRE_LEVEL / 5 * 3):
                    m_nFireLevel = (MAX_FIRE_LEVEL / 5 * 2) + (MAX_FIRE_LEVEL / 10);
                    addScore(SCORES_FLOOR);
                    m_nFloor_States[2] = STATE_FLOOR_BURNT;
                    break;
                case (MAX_FIRE_LEVEL / 5 * 2):
                    m_nFireLevel = (MAX_FIRE_LEVEL / 5 * 1) + (MAX_FIRE_LEVEL / 10);
                    addScore(SCORES_FLOOR);
                    m_nFloor_States[3] = STATE_FLOOR_BURNT;
                    break;
                case (MAX_FIRE_LEVEL / 5 * 1):
                    m_nFireLevel = (MAX_FIRE_LEVEL / 5 * 0) + (MAX_FIRE_LEVEL / 10);
                    addScore(SCORES_FLOOR);
                    m_nFloor_States[4] = STATE_FLOOR_BURNT;
                    break;
            }
        }
        else
        {
            if(m_nFireLevel > MAX_FIRE_LEVEL + 2)
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
            else
            {
                m_nFireLevel++;
            }
        }

        if (m_nFireLevel > (MAX_FIRE_LEVEL / 5 * 5) && m_nFloor_States[0] != STATE_FLOOR_FIRING)
        {
            m_nFloor_States[0] = STATE_FLOOR_FIRING;
            m_pAbstractGameActionListener.processGameAction(GAMEACTION_FLOOR_BURNING);
        }
        if (m_nFireLevel > (MAX_FIRE_LEVEL / 5 * 4) && m_nFloor_States[1] != STATE_FLOOR_FIRING)
        {
            m_nFloor_States[1] = STATE_FLOOR_FIRING;
            m_pAbstractGameActionListener.processGameAction(GAMEACTION_FLOOR_BURNING);
        }
        if (m_nFireLevel > (MAX_FIRE_LEVEL / 5 * 3) && m_nFloor_States[2] != STATE_FLOOR_FIRING)
        {
            m_nFloor_States[2] = STATE_FLOOR_FIRING;
            m_pAbstractGameActionListener.processGameAction(GAMEACTION_FLOOR_BURNING);
        }
        if (m_nFireLevel > (MAX_FIRE_LEVEL / 5 * 2) && m_nFloor_States[3] != STATE_FLOOR_FIRING)
        {
            m_nFloor_States[3] = STATE_FLOOR_FIRING;
            m_pAbstractGameActionListener.processGameAction(GAMEACTION_FLOOR_BURNING);
        }
        if (m_nFireLevel > (MAX_FIRE_LEVEL / 5 * 1) && m_nFloor_States[4] != STATE_FLOOR_FIRING)
        {
            m_nFloor_States[4] = STATE_FLOOR_FIRING;
            m_pAbstractGameActionListener.processGameAction(GAMEACTION_FLOOR_BURNING);
        }





        if (processFiremen(p_MoveObject.i_buttonState) )
        {
        }
        else
        {

        }

        processSprites();
        generateNewVictim();


        i_GameTimer++;

        if((i_GameTimer & STEP_VICTIMFREQ_MASK) == 0)
        {
            if(i_borndelay > LIMIT_VICTIMFREQ )
            {
                i_borndelay--;
            }
            else
            {
                switch(getGameDifficultLevel())
                {
                    case GAMELEVEL_EASY:
                        i_borndelay = LEVEL_EASY_VICTIMFREQ;
                        break;
                    case GAMELEVEL_NORMAL:
                        i_borndelay = LEVEL_NORMAL_VICTIMFREQ;
                        break;
                    case GAMELEVEL_HARD:
                        i_borndelay = LEVEL_HARD_VICTIMFREQ;
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
