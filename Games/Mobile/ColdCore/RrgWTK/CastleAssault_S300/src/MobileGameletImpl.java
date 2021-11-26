
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.DataInputStream;

public class MobileGameletImpl extends MobileGamelet
{
    public static final int CELL_WIDTH = (22) << 8;
    public static final int CELL_HEIGHT = (13) << 8;
    public static final int WALL_X = ((21) << 8) + CELL_WIDTH / 2;
    public static final int WALL_Y = ((33) << 8) + CELL_HEIGHT / 2;

    protected static final int CELL1_X = (WALL_X + CELL_WIDTH * 0);
    protected static final int CELL2_X = (WALL_X + CELL_WIDTH * 1);
    protected static final int CELL3_X = (WALL_X + CELL_WIDTH * 2);
    protected static final int CELL4_X = (WALL_X + CELL_WIDTH * 3);
    private static final int CELL4_Y = (WALL_Y + CELL_HEIGHT * 0);
    private static final int CELL3_Y = (WALL_Y + CELL_HEIGHT * 1);
    private static final int CELL2_Y = (WALL_Y + CELL_HEIGHT * 2);
    private static final int CELL1_Y = (WALL_Y + CELL_HEIGHT * 3);

    private static final int MOVE_SPEED = 0x0100;

    private static final int SUPPLIER_SPEED  = 0x0100;
    private static final int ENEMY_STONE_SPEED  = (-4) << 8;

    private static final int STONE_SPEED4  = (-9) << 8;
    private static final int STONE_SPEED3  = (-8) << 8;
    private static final int STONE_SPEED2  = (-7) << 8;
    private static final int STONE_SPEED1  = (-6) << 8;
    private static final int STONE_SPEED0  = (-5) << 8;

    protected static final int G_SPEED  = 0x0080;
    public static final int LAUNCH_SPEED_INCREMENT  = 0x0010;
    public static final int LAUNCH_INITIAL_SPEED  = (-5) << 8;
    public static final int LAUNCH_MAX_SPEED  = (-10) << 8;

    private static final int INIT_PLAYER_Y = (128-18) << 8;
    private static final int INIT_PLAYER_X = (CELL2_X);
    private static final int INIT_SUPPLIER_Y = (128-15) << 8;

    private static final int LEVEL_EASY_TIMEDELAY = 100;

    private static final int LEVEL_EASY_LIVES = 4;

    private static final int LEVEL_EASY_STONEFREQ = 50;
    private static final int LEVEL_EASY_ENSTONEFREQ = 100;

    private static final int LEVEL_NORMAL_TIMEDELAY = 90;

    private static final int LEVEL_NORMAL_LIVES = 3;

    private static final int LEVEL_NORMAL_STONEFREQ = 80; 
    private static final int LEVEL_NORMAL_ENSTONEFREQ = 50;

    private static final int LEVEL_HARD_TIMEDELAY = 80;

    private static final int LEVEL_HARD_LIVES = 2;

    private static final int LEVEL_HARD_STONEFREQ = 110; 
    private static final int LEVEL_HARD_ENSTONEFREQ = 25;

    private static final int SCORES_BLOCK = 10;

    private static final int SPRITEDATALENGTH = 8;

    public static final int SPRITE_CATAPULT                     = 0;
    public static final int SPRITE_CATAPULT_LEFT                = 1;
    public static final int SPRITE_CATAPULT_RIGHT               = 2;
    public static final int SPRITE_CATAPULT_READY               = 3;
    public static final int SPRITE_CATAPULT_FIRE                = 4;
    public static final int SPRITE_CATAPULT_AFTER_FIRE          = 5;
    public static final int SPRITE_CATAPULT_DESTROYED           = 6;

    public static final int SPRITE_STONESUPPLIER                = 7;
    public static final int SPRITE_STONESUPPLIER_DEAD           = 8;

    public static final int SPRITE_STONE                        = 9;

    public static final int SPRITE_ENEMY_STONE                  = 10;

    public static final int MAX_SPRITES = 8;

    public static final int GAMEACTION_ON_TARGET                = 0;
    public static final int GAMEACTION_OVER_TARGET              = 1;
    public static final int GAMEACTION_HIT_THE_WALL             = 2;
    public static final int GAMEACTION_CATAPULT_READY_TO_FIRE   = 3;
    public static final int GAMEACTION_CATAPULT_INCREASE_SPEED  = 4;
    public static final int GAMEACTION_CATAPULT_FIRE            = 5;
    public static final int GAMEACTION_CATAPULT_DEAD            = 6;
    public static final int GAMEACTION_SUPPLIER_GENERATED       = 7;
    public static final int GAMEACTION_SUPPLIER_DEAD            = 8;
    public static final int GAMEACTION_ENEMY_FIRE               = 9;
    public static final int GAMEACTION_ENEMY_ON_THE_LAND        = 10;



    public int m_iPlayerAttemptions;

    private int i_timedelay;

    public int i_GameTimer;

    private int i_borndelay;
    private int i_lastborn;

    private int i_stonedelay;
    private int i_laststone;

    public int i_FinishY;
    public int i_Speed;

    public static final int EMPTY_WALL     = 0;
    public static final int NORMAL_WALL    = 1;
    public static final int HEAD_WALL      = 2;
    public static final int DESTROYED_WALL = 3;

    private int m_nCells[][] =
    {
        { 0, NORMAL_WALL, NORMAL_WALL, NORMAL_WALL, HEAD_WALL },
        { 0, NORMAL_WALL, NORMAL_WALL, NORMAL_WALL, HEAD_WALL },
        { 0, NORMAL_WALL, NORMAL_WALL, NORMAL_WALL, HEAD_WALL },
        { 0, NORMAL_WALL, NORMAL_WALL, NORMAL_WALL, HEAD_WALL }
    };


    public Sprite pPlayerCatapult;

    public Sprite pStone;

    public Sprite pEnemyStone;

    public Sprite pStoneSupplier;

    public MoveObject p_MoveObject;

    private int i8_gameScreenWidth;

    private int i8_gameScreenHeight;

    private static final int[] ai_SpriteParameters = new int[]
    {
        0x1400, 0x1900, 0x1000, 0x1000, 1, 1, Sprite.SPRITE_ALIGN_CENTER, Sprite.ANIMATION_CYCLIC,
        0x1400, 0x1900, 0x1000, 0x1000, 3, 1, Sprite.SPRITE_ALIGN_CENTER, Sprite.ANIMATION_CYCLIC,
        0x1400, 0x1900, 0x1000, 0x1000, 3, 1, Sprite.SPRITE_ALIGN_CENTER, Sprite.ANIMATION_CYCLIC,
        0x1400, 0x1900, 0x1000, 0x1000, 2, 1, Sprite.SPRITE_ALIGN_CENTER, Sprite.ANIMATION_FROZEN,
        0x1400, 0x1900, 0x1000, 0x1000, 1, 1, Sprite.SPRITE_ALIGN_CENTER, Sprite.ANIMATION_FROZEN,
        0x1400, 0x1900, 0x1000, 0x1000, 3, 1, Sprite.SPRITE_ALIGN_CENTER, Sprite.ANIMATION_FROZEN,
        0x1400, 0x1900, 0x1000, 0x0200, 4, 1, Sprite.SPRITE_ALIGN_CENTER, Sprite.ANIMATION_FROZEN,
        0x1800, 0x1300, 0x1000, 0x1000, 3, 1, Sprite.SPRITE_ALIGN_CENTER, Sprite.ANIMATION_PENDULUM,
        0x1800, 0x1300, 0x1000, 0x1000, 4, 3, Sprite.SPRITE_ALIGN_CENTER, Sprite.ANIMATION_FROZEN,
        0x0A00, 0x0900, 0x0800, 0x0800, 99, 1, Sprite.SPRITE_ALIGN_CENTER, Sprite.ANIMATION_FROZEN,
        0x0A00, 0x0900, 0x0800, 0x0700, 3, 1, Sprite.SPRITE_ALIGN_CENTER, Sprite.ANIMATION_FROZEN
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


    public String getGameTextID()
    {
        return "CASTLEASSAULT";
    }


    public boolean initState()
    {
        pPlayerCatapult = new Sprite(0);

        initSpriteFromArray(pPlayerCatapult, SPRITE_CATAPULT);
        pPlayerCatapult.lg_SpriteActive = true;

        int i8_cx = INIT_PLAYER_X; 
        int i8_cy = INIT_PLAYER_Y;

        pPlayerCatapult.setMainPointXY(i8_cx, i8_cy);
        pPlayerCatapult.i_ObjectState = 1;

        pStone = new Sprite(1);
        initSpriteFromArray(pStone, SPRITE_STONE);
        pStone.lg_SpriteActive = false;

        pEnemyStone = new Sprite(2);
        initSpriteFromArray(pEnemyStone, SPRITE_ENEMY_STONE);
        pEnemyStone.lg_SpriteActive = false;

        pStoneSupplier = new Sprite(3);
        initSpriteFromArray(pStoneSupplier, SPRITE_STONESUPPLIER);
        pStoneSupplier.lg_SpriteActive = false;

        for(int wx=0;wx<4;wx++)
        {
            int wy = 0;

            setCell(wx, wy++, HEAD_WALL);
            setCell(wx, wy++, NORMAL_WALL);
            setCell(wx, wy++, NORMAL_WALL);
            setCell(wx, wy++, NORMAL_WALL);
            setTower(wx, 0); 
        }

        p_MoveObject = new MoveObject();

        return true;
    }

    private boolean setCell(int x, int y, int value)
        {
            if(x >= 0 && x < 4 && y >= 0 && y < 4)
            {
                m_nCells[x][4-y] = value;
                return true;
            }
            else
            {
                return false;
            }
        }


    public int getCell(int x, int y)
    {
        if(x >= 0 && x < 4 && y >= 0 && y < 4)
        {
            return (m_nCells[x][4-y]);
        }
        else
        {
            return -1;
        }
    }

    public int getTower(int x)
    {
        if(x >= 0 && x < 4)
        {
            return (m_nCells[x][0]);
        }
        else
        {
            return -1;
        }
    }

    public boolean setTower(int x, int value)
    {
        if(x >= 0 && x < 4)
        {
            m_nCells[x][0] = value;
            return true;
        }
        else
        {
            return false;
        }
    }


    private void processStone()
    {
        if(pStone.lg_SpriteActive)
        {
            pStone.processAnimation();
            pStone.setMainPointXY(pStone.i_mainX, pStone.i_mainY + pStone.i_ObjectState);
            pStone.i_ObjectState += G_SPEED;

            if(pStone.i_ObjectState > 0)
            {
                if(pStone.i_mainY >= i_FinishY)
                {
                    pStone.lg_SpriteActive = false;

                    if(i_FinishY >= CELL4_Y)
                    {
                        int wx = (pStone.i_mainX - WALL_X) / CELL_WIDTH;
                        int wy = (i_FinishY - WALL_Y) / CELL_HEIGHT;

                        int upper = getTower(wx);

                        if(upper == wy)
                        {
                            setCell(wx, wy, DESTROYED_WALL);
                            addScore(SCORES_BLOCK);
                            if(wy > 0)
                            {
                                setCell(wx, wy-1, EMPTY_WALL);
                            }
                            setTower(wx, ++upper);
                            m_pAbstractGameActionListener.processGameAction(GAMEACTION_ON_TARGET);
                        }
                        else
                        {
                            if(wy > upper)
                            {
                                m_pAbstractGameActionListener.processGameAction(GAMEACTION_HIT_THE_WALL);
                            }
                            else
                            {
                                m_pAbstractGameActionListener.processGameAction(GAMEACTION_OVER_TARGET);
                            }
                        }
                    }
                    else
                    {
                        m_pAbstractGameActionListener.processGameAction(GAMEACTION_OVER_TARGET);
                    }
                }
            }
        }
    }


    private boolean processCatapult(int _buttonState)
    {
        if (pPlayerCatapult.processAnimation())
        {
            switch(pPlayerCatapult.i_ObjectType)
            {
                case SPRITE_CATAPULT_DESTROYED:
                    return false;
                case SPRITE_CATAPULT_READY:
                    break;
                case SPRITE_CATAPULT_FIRE:
                    initSpriteFromArray(pPlayerCatapult, SPRITE_CATAPULT_AFTER_FIRE);

                    initSpriteFromArray(pStone, SPRITE_STONE);
                    pStone.setMainPointXY(pPlayerCatapult.i_mainX, pPlayerCatapult.i_mainY);
                    pStone.i_ObjectState = i_Speed;
                    pStone.lg_SpriteActive = true;

                    if(i_Speed < STONE_SPEED4)
                    {
                        i_FinishY = CELL4_Y - CELL_HEIGHT / 2;
                    }
                    else
                    {
                        if(i_Speed < STONE_SPEED3)
                        {
                            i_FinishY = CELL4_Y;
                        }
                        else
                        {
                            if(i_Speed < STONE_SPEED2)
                            {
                                i_FinishY = CELL3_Y;
                            }
                            else
                            {
                                if(i_Speed < STONE_SPEED1)
                                {
                                    i_FinishY = CELL2_Y;
                                }
                                else
                                {
                                        i_FinishY = CELL1_Y;
                                }
                            }
                        }
                    }
                    break;
                case SPRITE_CATAPULT_AFTER_FIRE:
                    initSpriteFromArray(pPlayerCatapult, SPRITE_CATAPULT);
                    break;
            } 
        }

        switch(pPlayerCatapult.i_ObjectType)
        {
            case SPRITE_CATAPULT_LEFT:
                pPlayerCatapult.setMainPointXY(pPlayerCatapult.i_mainX - MOVE_SPEED, pPlayerCatapult.i_mainY);
                switch(pPlayerCatapult.i_mainX)
                {
                    case CELL1_X:
                          pPlayerCatapult.i_ObjectState = 0;
                          initSpriteFromArray(pPlayerCatapult, SPRITE_CATAPULT);
                          break;
                    case CELL2_X:
                          pPlayerCatapult.i_ObjectState = 1;
                          initSpriteFromArray(pPlayerCatapult, SPRITE_CATAPULT);
                          break;
                    case CELL3_X:
                          pPlayerCatapult.i_ObjectState = 2;
                          initSpriteFromArray(pPlayerCatapult, SPRITE_CATAPULT);
                          break;
                }
                break;
            case SPRITE_CATAPULT_RIGHT:
                pPlayerCatapult.setMainPointXY(pPlayerCatapult.i_mainX + MOVE_SPEED, pPlayerCatapult.i_mainY);
                switch(pPlayerCatapult.i_mainX)
                {
                    case CELL2_X:
                          pPlayerCatapult.i_ObjectState = 1;
                          initSpriteFromArray(pPlayerCatapult, SPRITE_CATAPULT);
                          break;
                    case CELL3_X:
                          pPlayerCatapult.i_ObjectState = 2;
                          initSpriteFromArray(pPlayerCatapult, SPRITE_CATAPULT);
                          break;
                    case CELL4_X:
                          pPlayerCatapult.i_ObjectState = 3;
                          initSpriteFromArray(pPlayerCatapult, SPRITE_CATAPULT);
                          break;
                }
                break;
            case SPRITE_CATAPULT_READY:
                if ((_buttonState & MoveObject.BUTTON_FIRE) != 0 && !pStone.lg_SpriteActive)
                {
                    initSpriteFromArray(pPlayerCatapult, SPRITE_CATAPULT_FIRE);
                    m_pAbstractGameActionListener.processGameAction(GAMEACTION_CATAPULT_FIRE);
                }
                else
                {
                    m_pAbstractGameActionListener.processGameAction(GAMEACTION_CATAPULT_INCREASE_SPEED);
                    i_Speed -= LAUNCH_SPEED_INCREMENT;
                    if(i_Speed <= LAUNCH_MAX_SPEED)
                    {
                        initSpriteFromArray(pPlayerCatapult, SPRITE_CATAPULT_FIRE);
                        m_pAbstractGameActionListener.processGameAction(GAMEACTION_CATAPULT_FIRE);
                    }
               }
                break;
        }

        return true;
    }

    private void processMotionKeys(int _buttonState)
    {
        if (pPlayerCatapult.i_ObjectType == SPRITE_CATAPULT)
        {
            if ((_buttonState & MoveObject.BUTTON_LEFT) != 0)
            {
                if(pPlayerCatapult.i_ObjectState > 0)
                {
                    initSpriteFromArray(pPlayerCatapult, SPRITE_CATAPULT_LEFT);
                }
            }
            else if ((_buttonState & MoveObject.BUTTON_RIGHT) != 0)
            {
                if(pPlayerCatapult.i_ObjectState < 3)
                {
                    initSpriteFromArray(pPlayerCatapult, SPRITE_CATAPULT_RIGHT);
                }
            }

            if(pStoneSupplier.lg_SpriteActive && (pStoneSupplier.i_ObjectType == SPRITE_STONESUPPLIER))
            {
                if(pPlayerCatapult.isCollided(pStoneSupplier))
                {
                    initSpriteFromArray(pPlayerCatapult, SPRITE_CATAPULT_READY);
                    pStoneSupplier.lg_SpriteActive = false;
                    i_lastborn = i_GameTimer;
                    i_Speed = LAUNCH_INITIAL_SPEED;

                    m_pAbstractGameActionListener.processGameAction(GAMEACTION_CATAPULT_READY_TO_FIRE);
                }
            }
        }
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
                    i_borndelay = LEVEL_EASY_STONEFREQ;
                    i_stonedelay = LEVEL_EASY_ENSTONEFREQ;
                    m_iPlayerAttemptions = LEVEL_EASY_LIVES;
                }
                ;
                break;
            case GAMELEVEL_NORMAL:
                {
                    i_timedelay = LEVEL_NORMAL_TIMEDELAY;
                    i_borndelay = LEVEL_NORMAL_STONEFREQ;
                    i_stonedelay = LEVEL_NORMAL_ENSTONEFREQ;
                    m_iPlayerAttemptions = LEVEL_NORMAL_LIVES;
                }
                ;
                break;
            case GAMELEVEL_HARD:
                {
                    i_timedelay = LEVEL_HARD_TIMEDELAY;
                    i_borndelay = LEVEL_HARD_STONEFREQ;
                    i_stonedelay = LEVEL_HARD_ENSTONEFREQ;
                    m_iPlayerAttemptions = LEVEL_HARD_LIVES;
                }
                ;
                break;
        }

        i_GameTimer = 0; 
        i_lastborn = i_GameTimer;
        i_laststone = i_GameTimer;
        i_Speed = LAUNCH_INITIAL_SPEED;

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

        i_GameTimer = 0; 
        i_lastborn = i_GameTimer;
        i_laststone = i_GameTimer;
        i_Speed = LAUNCH_INITIAL_SPEED;

        initSpriteFromArray(pPlayerCatapult, SPRITE_CATAPULT);
        pPlayerCatapult.lg_SpriteActive = true;

        int i8_cx = INIT_PLAYER_X; 
        int i8_cy = INIT_PLAYER_Y;

        pPlayerCatapult.setMainPointXY(i8_cx, i8_cy);
        pPlayerCatapult.i_ObjectState = 1; 

        pStoneSupplier.lg_SpriteActive = false;
    }

    public void deinitState()
    {
        pPlayerCatapult = null;
        pStone = null;
        pEnemyStone = null;
        pStoneSupplier = null;

        p_MoveObject = null;

        Runtime.getRuntime().gc();
    }

    public int _getMaximumDataSize()
    {
        return  (Sprite.DATASIZE_BYTES+1) * 4 + 29 + 20 * 4;
    }

    public void _saveGameData(DataOutputStream _outputStream) throws IOException
    {
        _outputStream.writeByte(m_iPlayerAttemptions);
        _outputStream.writeInt(i_GameTimer);
        _outputStream.writeInt(i_lastborn);
        _outputStream.writeInt(i_borndelay);
        _outputStream.writeInt(i_laststone);
        _outputStream.writeInt(i_stonedelay);
        _outputStream.writeInt(i_FinishY);
        _outputStream.writeInt(i_Speed);

        for(int i=0; i<4; i++)
        {
            _outputStream.writeInt( getCell(0, i) );
            _outputStream.writeInt( getCell(1, i) );
            _outputStream.writeInt( getCell(2, i) );
            _outputStream.writeInt( getCell(3, i) );
            _outputStream.writeInt( getTower(i) );
        }

        _outputStream.writeByte(pStone.i_ObjectType);
        pStone.writeSpriteToStream(_outputStream);

        _outputStream.writeByte(pEnemyStone.i_ObjectType);
        pEnemyStone.writeSpriteToStream(_outputStream);

        _outputStream.writeByte(pStoneSupplier.i_ObjectType);
        pStoneSupplier.writeSpriteToStream(_outputStream);

        _outputStream.writeByte(pPlayerCatapult.i_ObjectType);
        pPlayerCatapult.writeSpriteToStream(_outputStream);
    }

    public void _loadGameData(DataInputStream _inputStream) throws IOException
    {
        m_iPlayerAttemptions = _inputStream.readByte();
        i_GameTimer = _inputStream.readInt();
        i_lastborn = _inputStream.readInt();
        i_borndelay = _inputStream.readInt();
        i_laststone = _inputStream.readInt();
        i_stonedelay = _inputStream.readInt();
        i_FinishY = _inputStream.readInt();
        i_Speed = _inputStream.readInt();

        for(int i=0; i<4; i++)
        {
            setCell(0, i, _inputStream.readInt());
            setCell(1, i, _inputStream.readInt());
            setCell(2, i, _inputStream.readInt());
            setCell(3, i, _inputStream.readInt());
            setTower(i, _inputStream.readInt());
        }

        initSpriteFromArray(pStone,_inputStream.readUnsignedByte());
        pStone.readSpriteFromStream(_inputStream);

        initSpriteFromArray(pEnemyStone,_inputStream.readUnsignedByte());
        pEnemyStone.readSpriteFromStream(_inputStream);

        initSpriteFromArray(pStoneSupplier,_inputStream.readUnsignedByte());
        pStoneSupplier.readSpriteFromStream(_inputStream);

        initSpriteFromArray(pPlayerCatapult,_inputStream.readUnsignedByte());
        pPlayerCatapult.readSpriteFromStream(_inputStream);
    }

    private void processSupplier()
    {
        if(!pStoneSupplier.lg_SpriteActive)
        {
            if((i_lastborn + i_borndelay) < i_GameTimer && pPlayerCatapult.i_ObjectType != SPRITE_CATAPULT_READY)
            {
                i_lastborn = i_GameTimer;

                initSpriteFromArray(pStoneSupplier, SPRITE_STONESUPPLIER);
                pStoneSupplier.lg_SpriteActive = true;

                int x = i8_gameScreenWidth + pStoneSupplier.i_width;
                int y = INIT_SUPPLIER_Y;

                int speed = -SUPPLIER_SPEED;

                if(getRandomInt(16) > 8)
                {
                    speed = SUPPLIER_SPEED;
                    x = 0 - pStoneSupplier.i_width;
                }

                pStoneSupplier.i_ObjectState = speed;
                pStoneSupplier.setMainPointXY(x, y);
                m_pAbstractGameActionListener.processGameAction(GAMEACTION_SUPPLIER_GENERATED);
            }
        }
        else
        {
            if(pStoneSupplier.processAnimation())
            {
                if(pStoneSupplier.i_ObjectType == SPRITE_STONESUPPLIER_DEAD)
                {
                    pStoneSupplier.lg_SpriteActive = false;
                }
            }
            if(pStoneSupplier.i_ObjectType == SPRITE_STONESUPPLIER)
            {
                pStoneSupplier.setMainPointXY(pStoneSupplier.i_mainX + pStoneSupplier.i_ObjectState, pStoneSupplier.i_mainY);


                if(pPlayerCatapult.lg_SpriteActive)
                {
                  if(pPlayerCatapult.i_mainX < pStoneSupplier.i_mainX)
                  {
                     pStoneSupplier.i_ObjectState = -SUPPLIER_SPEED;
                  }
                    else
                         {
                            pStoneSupplier.i_ObjectState = SUPPLIER_SPEED;
                         }
                }
            }
        }
    }

    private void processEnemyStone()
    {
        if(!pEnemyStone.lg_SpriteActive)
        {
            if((i_laststone + i_stonedelay) < i_GameTimer )
            {
                i_laststone = i_GameTimer;

                initSpriteFromArray(pEnemyStone, SPRITE_ENEMY_STONE);
                pEnemyStone.lg_SpriteActive = true;

                int x = getRandomInt(8) / 2;
                int y = CELL4_Y;

                switch(x)
                {
                    default:
                    case 0:
                        x = CELL4_X;
                        break;
                    case 1:
                        x = CELL3_X;
                        break;
                    case 2:
                        x = CELL2_X;
                        break;
                    case 3:
                        x = CELL1_X;
                        break;
                }

                pEnemyStone.setMainPointXY(x, y);
                pEnemyStone.i_ObjectState = ENEMY_STONE_SPEED;
                m_pAbstractGameActionListener.processGameAction(GAMEACTION_ENEMY_FIRE);
            }
        }
        else
        {
            pEnemyStone.processAnimation();
            pEnemyStone.setMainPointXY(pEnemyStone.i_mainX, pEnemyStone.i_mainY + pEnemyStone.i_ObjectState);
            pEnemyStone.i_ObjectState += G_SPEED;

            if(pEnemyStone.i_mainY > i8_gameScreenHeight)
            {
                pEnemyStone.lg_SpriteActive = false;
                m_pAbstractGameActionListener.processGameAction(GAMEACTION_ENEMY_ON_THE_LAND);
            }
            else
            {
                if(pEnemyStone.isCollided(pPlayerCatapult))
                {
                    initSpriteFromArray(pPlayerCatapult, SPRITE_CATAPULT_DESTROYED);
                    pEnemyStone.lg_SpriteActive = false;
                    m_pAbstractGameActionListener.processGameAction(GAMEACTION_CATAPULT_DEAD);
                }
                if(pStoneSupplier.lg_SpriteActive)
                {
                    if(pEnemyStone.isCollided(pStoneSupplier))
                    {
                        initSpriteFromArray(pStoneSupplier, SPRITE_STONESUPPLIER_DEAD);
                        pEnemyStone.lg_SpriteActive = false;
                        m_pAbstractGameActionListener.processGameAction(GAMEACTION_SUPPLIER_DEAD);
                    }
                }
            }
        }
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

    public int nextGameStep()
    {
        CAbstractPlayer p_player = m_pPlayerList[0];
        p_player.nextPlayerMove(this, p_MoveObject);


        if (processCatapult(p_MoveObject.i_buttonState) )
        {
        }
        else
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

        processStone();
        processSupplier();
        processEnemyStone();
        processMotionKeys(p_MoveObject.i_buttonState);

        int w1 = getTower(0);
        int w2 = getTower(1);
        int w3 = getTower(2);
        int w4 = getTower(3);

        if( ((w1 << w2) == (4 << 4) ||
             (w2 << w3) == (4 << 4) ||
             (w3 << w4) == (4 << 4))
          )
        {
            if (m_pWinningList==null)
            {
               m_pWinningList = m_pPlayerList;
            }
            setGameState(GAMEWORLDSTATE_GAMEOVER);
        }

        i_GameTimer++;

        return m_iGameState;
    }

    public int getGameStepDelay()
    {
        return i_timedelay;
    }
}
