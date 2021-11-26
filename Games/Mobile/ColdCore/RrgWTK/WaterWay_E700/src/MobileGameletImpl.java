
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.DataInputStream;

public class MobileGameletImpl extends MobileGamelet
{
    public static final int DROPS_X = (20 << 8);
    public static final int DROPS_Y = (0 << 8);
    public static final int DROP_SPACE = (18 << 8);

    public static final int STOP_LINE = (135 << 8);

    public static final int STEP_DROPFREQ_MASK = 0x001F;
    public static final int LIMIT_DROPFREQ = 15; 

    private static final int DROP1_X = (DROPS_X + DROP_SPACE * 0);
    private static final int DROP2_X = (DROPS_X + DROP_SPACE * 1);
    private static final int DROP3_X = (DROPS_X + DROP_SPACE * 2);
    private static final int DROP4_X = (DROPS_X + DROP_SPACE * 3);
    private static final int DROP5_X = (DROPS_X + DROP_SPACE * 4);
    private static final int DROP6_X = (DROPS_X + DROP_SPACE * 5);

    private static final int MOVE_SPEED = 0x0200;
    private static final int FALL_SPEED = 0x0500;  

    private static final int INIT_PLAYER_Y = (STOP_LINE - 0x2500 / 2);
    private static final int INIT_PLAYER_X = (DROP3_X); 
    private static final int INIT_POSITION = 2; 

    public static final int MIN_ENTRANCE_DELAY = 20;
    public static final int MAX_ENTRANCE_DELAY = 50;  

    public static final int INIT_ENTRANCE_X1 = (10 << 8);
    public static final int INIT_ENTRANCE_X2 = (118 << 8);
    public static final int INIT_ENTRANCE_Y = STOP_LINE-(27/2<<8);

    private static final int LEVEL_EASY_TIMEDELAY = 80; 

    private static final int LEVEL_EASY_LIVES = 4;

    private static final int LEVEL_EASY_DROPFREQ = 35; 

    private static final int LEVEL_EASY_DRYING_DELAY = (20); 




    private static final int LEVEL_NORMAL_TIMEDELAY = 80;

    private static final int LEVEL_NORMAL_LIVES = 3;

    private static final int LEVEL_NORMAL_DROPFREQ = 30;

    private static final int LEVEL_NORMAL_DRYING_DELAY = (25);



    private static final int LEVEL_HARD_TIMEDELAY = 80;

    private static final int LEVEL_HARD_LIVES = 2;

    private static final int LEVEL_HARD_DROPFREQ = 25;

    private static final int LEVEL_HARD_DRYING_DELAY = (30);




    private static final int SCORES_DROP = 10;

    private static final int SPRITEDATALENGTH = 8;

    public static final int SPRITE_ANT                      = 0;
    public static final int SPRITE_ANT_LEFT                 = 1;
    public static final int SPRITE_ANT_RIGHT                = 2;
    public static final int SPRITE_ANT_COLLECTED_DROP       = 3;
    public static final int SPRITE_ANT_COLLECTED_DROP_LEFT  = 4;
    public static final int SPRITE_ANT_COLLECTED_DROP_RIGHT = 5;
    public static final int SPRITE_ANT_POUR_OUT_LEFT        = 6;
    public static final int SPRITE_ANT_POUR_OUT_RIGHT       = 7;
    public static final int SPRITE_ANT_DEAD                 = 8;
    public static final int SPRITE_ANT_ENTRANCE             = 9;
    public static final int SPRITE_DROP                     = 10;
    public static final int SPRITE_DROP_PUDDLET             = 11;
    public static final int SPRITE_DROP_PUDDLET_DRYING      = 12;

    public static final int MAX_SPRITES = 10;

    public static final int GAMEACTION_ANT_CATCH_DROP       = 0;
    public static final int GAMEACTION_ANT_POUR_OUT         = 1;
    public static final int GAMEACTION_ANT_POURED_OUT       = 2;
    public static final int GAMEACTION_ANT_DEAD             = 3;
    public static final int GAMEACTION_ANT_FLOWER           = 4;
    public static final int GAMEACTION_DROP_GENERATED       = 5;
    public static final int GAMEACTION_DROP_ON_THE_LAND     = 6;
    public static final int GAMEACTION_DROP_INTO_THE_PUDDLE = 7;

    public static final int GAMEACTION_DROP_DRYING          = 8;



    public int i_playerAttemptions;

    private int i_timedelay;

    public int i_GameTimer;

    public int i_borndelay;
    private int i_lastjump;

    public boolean b_AntDrop;


    public Sprite pPlayerAnt;

    public Sprite pAnt;

    public Sprite[] ap_Sprites;

    public MoveObject p_MoveObject;

    private int i8_gameScreenWidth;

    private int i8_gameScreenHeight;

    private static final int[] ai_SpriteParameters = new int[]
    {
        0x1000, 0x2400, 0x1000, 0x2000, 1, 1, Sprite.SPRITE_ALIGN_CENTER, Sprite.ANIMATION_CYCLIC,
        0x1000, 0x2400, 0x0C00, 0x2000, 2, 1, Sprite.SPRITE_ALIGN_CENTER, Sprite.ANIMATION_CYCLIC,
        0x1000, 0x2400, 0x0C00, 0x2000, 2, 1, Sprite.SPRITE_ALIGN_CENTER, Sprite.ANIMATION_CYCLIC,
        0x1000, 0x2400, 0x1000, 0x2000, 1, 1, Sprite.SPRITE_ALIGN_CENTER, Sprite.ANIMATION_FROZEN,
        0x1000, 0x2400, 0x1000, 0x2000, 1, 1, Sprite.SPRITE_ALIGN_CENTER, Sprite.ANIMATION_FROZEN,
        0x1000, 0x2400, 0x1000, 0x2000, 1, 1, Sprite.SPRITE_ALIGN_CENTER, Sprite.ANIMATION_FROZEN,
        0x1000, 0x2400, 0x1000, 0x2000, 6, 1, Sprite.SPRITE_ALIGN_CENTER, Sprite.ANIMATION_FROZEN,
        0x1000, 0x2400, 0x1000, 0x2000, 6, 1, Sprite.SPRITE_ALIGN_CENTER, Sprite.ANIMATION_FROZEN,
        0x1000, 0x2400, 0x1000, 0x2400, 3, 2, Sprite.SPRITE_ALIGN_CENTER, Sprite.ANIMATION_FROZEN,
        0x1600, 0x1B00, 0x0A00, 0x1B00, 1, 1, Sprite.SPRITE_ALIGN_CENTER, Sprite.ANIMATION_FROZEN,
        0x0400, 0x0400, 0x0400, 0x0400, 1, 1, Sprite.SPRITE_ALIGN_CENTER, Sprite.ANIMATION_CYCLIC,
        0x0800, 0x0400, 0x0800, 0x0400, 5, 1, Sprite.SPRITE_ALIGN_CENTER, Sprite.ANIMATION_FROZEN,
        0x0800, 0x0400, 0x0800, 0x0400, 5, 2, Sprite.SPRITE_ALIGN_CENTER, Sprite.ANIMATION_FROZEN
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
                        case SPRITE_DROP_PUDDLET_DRYING:
                            ap_Sprites[li].lg_SpriteActive = false;
                            break;
                    }
                } 

                switch(ap_Sprites[li].i_ObjectType)
                {
                    case SPRITE_DROP:

                        ap_Sprites[li].setMainPointXY(ap_Sprites[li].i_mainX, ap_Sprites[li].i_mainY + FALL_SPEED);

                        if(ap_Sprites[li].i_mainY >= STOP_LINE)
                        {

                            boolean fallen_into_puddle = false;
                            int i_x = ap_Sprites[li].i_mainX;
                            Sprite p_spr;
                            for (int i = 0; i < MAX_SPRITES; i++)
                            {
                                if ((p_spr = ap_Sprites[i]).lg_SpriteActive
                                && p_spr.i_mainX == i_x && p_spr.i_ObjectType != SPRITE_DROP)
                                {
                                   fallen_into_puddle = true;
                                }
                            }

                            initSpriteFromArray(ap_Sprites[li], SPRITE_DROP_PUDDLET);

                            ap_Sprites[li].setMainPointXY(ap_Sprites[li].i_mainX, STOP_LINE);

                            int dry_delay = LEVEL_EASY_DRYING_DELAY;

                            switch(getGameDifficultLevel())
                            {
                                case GAMELEVEL_EASY:
                                    dry_delay = LEVEL_EASY_DRYING_DELAY;
                                    break;
                                case GAMELEVEL_NORMAL:
                                    dry_delay = LEVEL_NORMAL_DRYING_DELAY;
                                    break;
                                case GAMELEVEL_HARD:
                                    dry_delay = LEVEL_HARD_DRYING_DELAY;
                                    break;
                            }

                            ap_Sprites[li].i_ObjectState = dry_delay;


                            m_pAbstractGameActionListener.processGameAction(
                                fallen_into_puddle ? GAMEACTION_DROP_INTO_THE_PUDDLE :
                                           GAMEACTION_DROP_ON_THE_LAND );
                        }


                       if(ap_Sprites[li].isCollided(pPlayerAnt))
                       {
                             ap_Sprites[li].lg_SpriteActive = false;

                             if(b_AntDrop)
                             {
                                 if(pPlayerAnt.i_ObjectType != SPRITE_ANT_DEAD)
                                 {
                                      initSpriteFromArray(pPlayerAnt, SPRITE_ANT_DEAD);
                                      m_pAbstractGameActionListener.processGameAction(GAMEACTION_ANT_DEAD);
                                 }
                             }
                               else
                                     {

                                           b_AntDrop = true;
                                           switch(pPlayerAnt.i_ObjectType)
                                           {
                                               case SPRITE_ANT:
                                                   {
                                                        initSpriteFromArray(pPlayerAnt, SPRITE_ANT_COLLECTED_DROP);
                                                        m_pAbstractGameActionListener.processGameAction(GAMEACTION_ANT_CATCH_DROP);
                                                   }
                                                   break;
                                               case SPRITE_ANT_LEFT:
                                                  {
                                                        initSpriteFromArray(pPlayerAnt, SPRITE_ANT_COLLECTED_DROP_LEFT);
                                                        m_pAbstractGameActionListener.processGameAction(GAMEACTION_ANT_CATCH_DROP);
                                                   }
                                                   break;
                                                case SPRITE_ANT_RIGHT:
                                                  {
                                                        initSpriteFromArray(pPlayerAnt, SPRITE_ANT_COLLECTED_DROP_RIGHT);
                                                        m_pAbstractGameActionListener.processGameAction(GAMEACTION_ANT_CATCH_DROP);
                                                   }
                                                   break;
                                                case SPRITE_ANT_POUR_OUT_LEFT:
                                                case SPRITE_ANT_POUR_OUT_RIGHT:
                                                   if(ap_Sprites[li].isCollided(pPlayerAnt))
                                                   {
                                                       initSpriteFromArray(pPlayerAnt, SPRITE_ANT_DEAD);
                                                       m_pAbstractGameActionListener.processGameAction(GAMEACTION_ANT_DEAD);
                                                   }
                                                   break;
                                           }
                                     }
                        }
                        break;
                    case SPRITE_DROP_PUDDLET:
                        if(ap_Sprites[li].i_ObjectState > 0)
                        {
                            ap_Sprites[li].i_ObjectState--;
                        }
                        else
                        {
                            initSpriteFromArray(ap_Sprites[li], SPRITE_DROP_PUDDLET_DRYING);
                            m_pAbstractGameActionListener.processGameAction(GAMEACTION_DROP_DRYING);
                        }
                        break;
                }
            } 
        } 
    }

    public String getGameTextID()
    {
        return "RAINANT";
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

        initSpriteFromArray(pAnt, SPRITE_ANT_ENTRANCE);
        pAnt.lg_SpriteActive = true;
        pAnt.setMainPointXY(INIT_ENTRANCE_X1, INIT_ENTRANCE_Y);

        b_AntDrop = false;

        p_MoveObject = new MoveObject();

        return true;
    }

    private boolean processAnt(int _buttonState)
    {
        if (pPlayerAnt.processAnimation())
        {
            switch(pPlayerAnt.i_ObjectType)
            {
                case SPRITE_ANT_COLLECTED_DROP:
                    initSpriteFromArray(pPlayerAnt, SPRITE_ANT);
                    break;
                case SPRITE_ANT_COLLECTED_DROP_LEFT:
                    initSpriteFromArray(pPlayerAnt, SPRITE_ANT_LEFT);
                    break;
                case SPRITE_ANT_COLLECTED_DROP_RIGHT:
                    initSpriteFromArray(pPlayerAnt, SPRITE_ANT_RIGHT);
                    break;
                case SPRITE_ANT_DEAD:
                    return false;
                case SPRITE_ANT_POUR_OUT_LEFT:
                case SPRITE_ANT_POUR_OUT_RIGHT:
                    initSpriteFromArray(pPlayerAnt, SPRITE_ANT);
                    pAnt.i_ObjectState = MIN_ENTRANCE_DELAY + getRandomInt(MAX_ENTRANCE_DELAY - MIN_ENTRANCE_DELAY);
                    pAnt.lg_SpriteActive = false;

                    addScore(SCORES_DROP);
                    m_pAbstractGameActionListener.processGameAction(GAMEACTION_ANT_POURED_OUT);
                    break;
            }
        }
        if(pAnt.lg_SpriteActive)
        {
            pAnt.processAnimation();

            if(b_AntDrop)
            {
                if((pPlayerAnt.i_mainX == DROP1_X) && (pAnt.i_mainX <= DROP1_X))
                {
                    initSpriteFromArray(pPlayerAnt, SPRITE_ANT_POUR_OUT_LEFT);
                    m_pAbstractGameActionListener.processGameAction(GAMEACTION_ANT_POUR_OUT);
                    b_AntDrop = false;
                }
                if((pPlayerAnt.i_mainX == DROP6_X) && (pAnt.i_mainX >= DROP6_X))
                {
                    initSpriteFromArray(pPlayerAnt, SPRITE_ANT_POUR_OUT_RIGHT);
                    m_pAbstractGameActionListener.processGameAction(GAMEACTION_ANT_POUR_OUT);
                    b_AntDrop = false;
                }
            }
        }
        else
        {
            if(pAnt.i_ObjectState > 0)
            {
                pAnt.i_ObjectState--;
            }
            else
            {
                pAnt.i_ObjectState = 0; 
                if(getRandomInt(8) > 4)
                {
                    pAnt.setMainPointXY(INIT_ENTRANCE_X1, INIT_ENTRANCE_Y);
                }
                else
                {
                    pAnt.setMainPointXY(INIT_ENTRANCE_X2, INIT_ENTRANCE_Y);
                }
                pAnt.lg_SpriteActive = true;
                m_pAbstractGameActionListener.processGameAction(GAMEACTION_ANT_FLOWER);
            }
        }

        int i_mx = pPlayerAnt.i_mainX;
        int i_my = pPlayerAnt.i_mainY;

        int i_w = pPlayerAnt.i_width / 2;
        int i_h = pPlayerAnt.i_height / 2;

        boolean can_left = true;
        boolean can_right = true;

        for (int li = 0; li < MAX_SPRITES; li++)
        {
            if (ap_Sprites[li].lg_SpriteActive)
            {
                int i_sx = ap_Sprites[li].i_mainX;
                int i_sy = ap_Sprites[li].i_mainY;
                int i_dx = Math.abs(i_sx - i_mx);

                if( (i_sy > (i_my - i_h)) && (i_dx < (DROP_SPACE * 2)) )
                {
                    if(i_sx < (i_mx - i_w))
                    {
                        can_left = false;
                    }
                    else if((i_sx > (i_mx + i_w)))
                    {
                        can_right = false;
                    }
                }
            }
        }

        switch(pPlayerAnt.i_ObjectType)
        {
            case SPRITE_ANT_LEFT:
                pPlayerAnt.setMainPointXY(pPlayerAnt.i_mainX - MOVE_SPEED, pPlayerAnt.i_mainY);
                switch(pPlayerAnt.i_mainX)
                {
                    case DROP1_X:
                        pPlayerAnt.i_ObjectState = 0;
                        initSpriteFromArray(pPlayerAnt, SPRITE_ANT);
                        break;
                    case DROP2_X:
                        pPlayerAnt.i_ObjectState = 1;
                        if(can_left)
                        {
                            if ((_buttonState & MoveObject.BUTTON_LEFT) == 0)
                            {
                                initSpriteFromArray(pPlayerAnt, SPRITE_ANT);
                            }
                        }
                        else
                        {
                            initSpriteFromArray(pPlayerAnt, SPRITE_ANT);
                        }
                        break;
                    case DROP3_X:
                        pPlayerAnt.i_ObjectState = 2;
                        if(can_left)
                        {
                            if ((_buttonState & MoveObject.BUTTON_LEFT) == 0)
                            {
                                initSpriteFromArray(pPlayerAnt, SPRITE_ANT);
                            }
                        }
                        else
                        {
                            initSpriteFromArray(pPlayerAnt, SPRITE_ANT);
                        }
                        break;
                    case DROP4_X:
                        pPlayerAnt.i_ObjectState = 3;
                        if(can_left)
                        {
                            if ((_buttonState & MoveObject.BUTTON_LEFT) == 0)
                            {
                                initSpriteFromArray(pPlayerAnt, SPRITE_ANT);
                            }
                        }
                        else
                        {
                            initSpriteFromArray(pPlayerAnt, SPRITE_ANT);
                        }
                        break;
                    case DROP5_X:
                        pPlayerAnt.i_ObjectState = 4;
                        if(can_left)
                        {
                            if ((_buttonState & MoveObject.BUTTON_LEFT) == 0)
                            {
                                initSpriteFromArray(pPlayerAnt, SPRITE_ANT);
                            }
                        }
                        else
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
                    case DROP2_X:
                        pPlayerAnt.i_ObjectState = 1;
                        if(can_right)
                        {
                            if ((_buttonState & MoveObject.BUTTON_RIGHT) == 0)
                            {
                                initSpriteFromArray(pPlayerAnt, SPRITE_ANT);
                            }
                        }
                        else
                        {
                            initSpriteFromArray(pPlayerAnt, SPRITE_ANT);
                        }
                        break;
                    case DROP3_X:
                        pPlayerAnt.i_ObjectState = 2;
                        if(can_right)
                        {
                            if ((_buttonState & MoveObject.BUTTON_RIGHT) == 0)
                            {
                                initSpriteFromArray(pPlayerAnt, SPRITE_ANT);
                            }
                        }
                        else
                        {
                            initSpriteFromArray(pPlayerAnt, SPRITE_ANT);
                        }
                        break;
                    case DROP4_X:
                        pPlayerAnt.i_ObjectState = 3;
                        if(can_right)
                        {
                            if ((_buttonState & MoveObject.BUTTON_RIGHT) == 0)
                            {
                                initSpriteFromArray(pPlayerAnt, SPRITE_ANT);
                            }
                        }
                        else
                        {
                            initSpriteFromArray(pPlayerAnt, SPRITE_ANT);
                        }
                        break;
                    case DROP5_X:
                        pPlayerAnt.i_ObjectState = 4;
                        if(can_right)
                        {
                            if ((_buttonState & MoveObject.BUTTON_RIGHT) == 0)
                            {
                                initSpriteFromArray(pPlayerAnt, SPRITE_ANT);
                            }
                        }
                        else
                        {
                            initSpriteFromArray(pPlayerAnt, SPRITE_ANT);
                        }
                        break;
                    case DROP6_X:
                        pPlayerAnt.i_ObjectState = 5;
                        initSpriteFromArray(pPlayerAnt, SPRITE_ANT);
                        break;

                }
                break;
            case SPRITE_ANT:
            {
                if ((_buttonState & MoveObject.BUTTON_LEFT) != 0)
                {
                    if(pPlayerAnt.i_ObjectState > 0 && can_left)
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
                    if(pPlayerAnt.i_ObjectState < 5 && can_right)
                    {
                        initSpriteFromArray(pPlayerAnt, SPRITE_ANT_RIGHT);
                    }
                    else
                    {
                        initSpriteFromArray(pPlayerAnt, SPRITE_ANT);
                    }
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
                    i_borndelay = LEVEL_EASY_DROPFREQ;
                    i_playerAttemptions = LEVEL_EASY_LIVES;
                }
                break;
            case GAMELEVEL_NORMAL:
                {
                    i_timedelay = LEVEL_NORMAL_TIMEDELAY;
                    i_borndelay = LEVEL_NORMAL_DROPFREQ;
                    i_playerAttemptions = LEVEL_NORMAL_LIVES;
                }
                break;
            case GAMELEVEL_HARD:
                {
                    i_timedelay = LEVEL_HARD_TIMEDELAY;
                    i_borndelay = LEVEL_HARD_DROPFREQ;
                    i_playerAttemptions = LEVEL_HARD_LIVES;
                }
                break;
        }

        i_GameTimer = 0; 
        i_lastjump = i_GameTimer;

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
                i_borndelay = LEVEL_EASY_DROPFREQ;
                break;
            case GAMELEVEL_NORMAL:
                i_borndelay = LEVEL_NORMAL_DROPFREQ;
                break;
            case GAMELEVEL_HARD:
                i_borndelay = LEVEL_HARD_DROPFREQ;
                break;
        }

        i_GameTimer = 0; 
        i_lastjump = i_GameTimer;
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
        return (2+MAX_SPRITES)*(Sprite.DATASIZE_BYTES+1) + 14;
    }

    public void _saveGameData(DataOutputStream _outputStream) throws IOException
    {
        _outputStream.writeByte(i_playerAttemptions);
        _outputStream.writeInt(i_GameTimer);
        _outputStream.writeInt(i_lastjump);
        _outputStream.writeInt(i_borndelay);
        _outputStream.writeBoolean(b_AntDrop);

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
        b_AntDrop = _inputStream.readBoolean();

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

    private void generateNewDrop()
    {
        if((i_lastjump + i_borndelay) < i_GameTimer )
        {
            i_lastjump = i_GameTimer;
            Sprite p_emptySprite = getInactiveSprite();
            if (p_emptySprite != null)
            {
                int y = DROPS_Y;
                int x = DROPS_X;

                switch(getRandomInt(6))
                {
                    case 0:
                        x = DROP1_X;
                        break;
                    case 1:
                        x = DROP2_X;
                        break;
                    default:
                    case 2:
                        x = DROP3_X;
                        break;
                    case 3:
                        x = DROP4_X;
                        break;
                    case 4:
                        x = DROP5_X;
                        break;
                    case 5:
                        x = DROP6_X;
                        break;
                }

                initSpriteFromArray(p_emptySprite, SPRITE_DROP);
                p_emptySprite.lg_SpriteActive = true;
                p_emptySprite.setMainPointXY(x, y);
                m_pAbstractGameActionListener.processGameAction(GAMEACTION_DROP_GENERATED);
            }
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
        generateNewDrop();

        i_GameTimer++;

        if((i_GameTimer & STEP_DROPFREQ_MASK) == 0)
        {
            if(i_borndelay > LIMIT_DROPFREQ )
            {
                i_borndelay--;
            }
            else
            {
                switch(getGameDifficultLevel())
                {
                    case GAMELEVEL_EASY:
                        i_borndelay = LEVEL_EASY_DROPFREQ;
                        break;
                    case GAMELEVEL_NORMAL:
                        i_borndelay = LEVEL_NORMAL_DROPFREQ;
                        break;
                    case GAMELEVEL_HARD:
                        i_borndelay = LEVEL_HARD_DROPFREQ;
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
