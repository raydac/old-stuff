
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.DataInputStream;

public class MobileGameletImpl extends MobileGamelet
{
    private static final int GUNSIGHT_VERTMIN_SPEED = 0x400;

    private static final int GUNSIGHT_VERTMAX_SPEED = 0x400;

    private static final int GUNSIGHT_HORZTMIN_SPEED = 0x400;

    private static final int GUNSIGHT_HORZMAX_SPEED = 0x400;

    private static final int HELICOPTER_FALL_SPEED = 0x0800;

    private static final int GUNSIGHT_CHANGESPEED_DELAY = 5;

    public static final int ENEMY_MAX_DISTANCE_FRAMES = 5;

    private static final int ENEMY_APPROACHING_DELAY = 20;



    private static final int LEVEL_EASY_TIMEDELAY = 120;

    private static final int LEVEL_EASY_LIVES = 4;

    private static final int LEVEL_EASY_ENEMYFREQ = 30;

    private static final int LEVEL_NORMAL_TIMEDELAY = 100;

    private static final int LEVEL_NORMAL_LIVES = 3;

    private static final int LEVEL_NORMAL_ENEMYFREQ = 20;

    private static final int LEVEL_HARD_TIMEDELAY = 80;

    private static final int LEVEL_HARD_LIVES = 2;

    private static final int LEVEL_HARD_ENEMYFREQ = 10;

    private static final int SCORES_HELICOPTER = 10;

    private static final int GROUND_LINE = (128 - 24) << 8;

    private static final int SPRITEDATALENGTH = 9;

    public static final int SPRITE_HELICOPTER_NORMAL            = 0;
    public static final int SPRITE_HELICOPTER_UP                = 1;
    public static final int SPRITE_HELICOPTER_DOWN              = 2;
    public static final int SPRITE_HELICOPTER_LEFT              = 3;
    public static final int SPRITE_HELICOPTER_RIGHT             = 4;
    public static final int SPRITE_HELICOPTER_FIRE              = 5;
    public static final int SPRITE_HELICOPTER_SHOOTED           = 6;
    public static final int SPRITE_HELICOPTER_DESTROYED         = 7;
    public static final int SPRITE_ENEMY_HELICOPTER             = 8;
    public static final int SPRITE_ENEMY_HELICOPTER_FIRE        = 9;
    public static final int SPRITE_ENEMY_HELICOPTER_SHOOTED     = 10;
    public static final int SPRITE_ENEMY_HELICOPTER_EXPLOSION   = 11;

    public static final int MAX_SPRITES = 8;

    private static final int MAX_PATHS = 8;

    private static final int PATH_HELICOPTER_1 = 16*0;
    private static final int PATH_HELICOPTER_2 = 16*1;
    private static final int PATH_HELICOPTER_3 = 16*2;
    private static final int PATH_HELICOPTER_4 = 16*3;
    private static final int PATH_HELICOPTER_5 = 16*4;
    private static final int PATH_HELICOPTER_6 = 16*5;
    private static final int PATH_HELICOPTER_7 = 16*6;
    private static final int PATH_HELICOPTER_8 = 16*7;

    public static final int ENEMY_FIRE_TIMEOUT = 50;
    public static final int ENEMY_FIRE_FREQUENCY = 32;

    public static final int FIRE_DELAY_MASK = 0x01;


    public static final int GAMEACTION_PLAYER_FIRE      = 0;

    public static final int GAMEACTION_PLAYER_SHOOTED   = 1;
    public static final int GAMEACTION_PLAYER_DESTROYED = 2;

    public static final int GAMEACTION_ENEMY_FIRE       = 3;

    public static final int GAMEACTION_ENEMY_SHOOTED    = 4;

    public static final int GAMEACTION_ENEMY_EXPLODED   = 5;




    private int i_gunsight_delay;

    public int m_iPlayerAttemptions;

    private int i_timedelay;

    public int i_GameTimer;

    private int i_borndelay = 20;
    private int i_lasttime;

    private int i_rndfreq;

    public boolean m_bFire;

    public int m_nFireCounter;


    public Sprite pPlayerSight;

    public Sprite[] ap_Sprites;

    private PathController[] ap_Paths;

    public MoveObject p_MoveObject;

    private int i8_gameScreenWidth;

    private int i8_gameScreenHeight;


    private static final short[] ash_Paths = new short[]
    {

        3, 0,  -10,40,40,   120,40,30,   10,60,20,    148,80,00,   00,00,
        3, 0,  -10,50,40,  120,30,30,   40,30,15,   110,-10,00,   00,00,
        3, 0,  138,70,30,  40,60,20,   20,40,15,   -10,-10,00,   00,00,
        4, 0,  -10,80,40,  120,50,25,   80,40,20,   110,20,15,   50,-10,
        4, 0,  -10,80,30,  30,60,20,   10,40,25,   100,70,15,   138,30,
        3, 0,  30,-10,30,  30,20,30,   60,40,20,   -10,60,00,   00,00,
        4, 0,  120,-10,40,  120,20,30, 80,20,20,   40,20,15,   10,-10,
        3, 0,  138,90,40,  90,60,20,   90,50,30,   138,30,00,   00,00

    };

    private static final int[] ai_SpriteParameters = new int[]
    {
        0x1C00, 0x3C00, 0x0200, 0x0300, 0x0300, 3, 1, Sprite.SPRITE_ALIGN_CENTER, Sprite.ANIMATION_CYCLIC,
        0x1C00, 0x3C00, 0x0200, 0x0300, 0x0300, 3, 1, Sprite.SPRITE_ALIGN_CENTER, Sprite.ANIMATION_CYCLIC,
        0x1C00, 0x3C00, 0x0200, 0x0300, 0x0300, 3, 1, Sprite.SPRITE_ALIGN_CENTER, Sprite.ANIMATION_CYCLIC,
        0x1C00, 0x3C00, 0x0200, 0x0300, 0x0300, 3, 1, Sprite.SPRITE_ALIGN_CENTER, Sprite.ANIMATION_CYCLIC,
        0x1C00, 0x3C00, 0x0200, 0x0300, 0x0300, 3, 1, Sprite.SPRITE_ALIGN_CENTER, Sprite.ANIMATION_CYCLIC,
        0x1C00, 0x3C00, 0x0200, 0x0300, 0x0300, 3, 1, Sprite.SPRITE_ALIGN_CENTER, Sprite.ANIMATION_CYCLIC,
        0x1C00, 0x3C00, 0x0200, 0x0300, 0x0300, 3, 1, Sprite.SPRITE_ALIGN_CENTER, Sprite.ANIMATION_FROZEN,
        0x1C000, 0x2000, -1, 0x1C00, 0x1C00, 20, 1, Sprite.SPRITE_ALIGN_CENTER, Sprite.ANIMATION_FROZEN,
        0x2000, 0x2000, -1, 0x1000, 0x1000, 4, 1, Sprite.SPRITE_ALIGN_CENTER, Sprite.ANIMATION_CYCLIC,
        0x2000, 0x2000, -1, 0x1000, 0x1000, 4, 1, Sprite.SPRITE_ALIGN_CENTER, Sprite.ANIMATION_CYCLIC,
        0x2000, 0x2000, -1, 0x1000, 0x1000, 4, 1, Sprite.SPRITE_ALIGN_CENTER, Sprite.ANIMATION_CYCLIC,
        0x2400, 0x2400, -1, 0x2000, 0x2000, 8, 1, Sprite.SPRITE_ALIGN_CENTER, Sprite.ANIMATION_CYCLIC
    };


    private void initSpriteFromArray(Sprite _sprite, int _actorIndex)
    {
        _sprite.i_ObjectType = _actorIndex;
        _actorIndex *= SPRITEDATALENGTH;
        int i_w = ai_SpriteParameters[_actorIndex++];
        int i_h = ai_SpriteParameters[_actorIndex++];

        int i_y = ai_SpriteParameters[_actorIndex++];

        int i_aw = ai_SpriteParameters[_actorIndex++];
        int i_ah = ai_SpriteParameters[_actorIndex++];
        int i_f = ai_SpriteParameters[_actorIndex++];
        int i_fd = ai_SpriteParameters[_actorIndex++];
        int i_mp = ai_SpriteParameters[_actorIndex++];
        int i_an = ai_SpriteParameters[_actorIndex++];

        _sprite.setAnimation(i_an, i_mp, i_w, i_h, i_f, 0, i_fd);
        _sprite.setCollisionBounds((i_w - i_aw) >> 1, (i_y < 0) ? ((i_h - i_ah) >> 1) : i_y, i_aw, i_ah);
    }


    private void deactivatePathForSpriteID(int _spriteID)
    {
         for (int li = 0; li < MAX_PATHS; li++)
        {
            if (ap_Paths[li].isCompleted()) continue;
            if (ap_Paths[li].p_sprite.i_spriteID == _spriteID)
            {
                ap_Paths[li].deactivate();
                break;
            }
        }
    }

    private void processSprites()
    {
        Sprite sprite;
        for (int li = 0; li < MAX_SPRITES; li++)
        {
            if ((sprite = ap_Sprites[li]).lg_SpriteActive)
            {
                    switch (sprite.i_ObjectType)
                    {
                        case SPRITE_ENEMY_HELICOPTER_SHOOTED:
                              {
                                  if (sprite.processAnimation())
                                  {
                                     deactivatePathForSpriteID(sprite.i_spriteID);
                                     initSpriteFromArray(sprite, SPRITE_ENEMY_HELICOPTER_EXPLOSION);
                                     m_pAbstractGameActionListener.processGameAction(GAMEACTION_ENEMY_EXPLODED);

                                     CAbstractPlayer p_player = m_pPlayerList[0];
                                     p_player.setPlayerMoveGameScores(SCORES_HELICOPTER, false);
                                  }
                              }
                              continue;
                        case SPRITE_ENEMY_HELICOPTER_EXPLOSION:
                              {
                                  if (sprite.processAnimation())
                                  {
                                    sprite.lg_SpriteActive = false;
                                  }
                              }
                              continue;
                        case SPRITE_ENEMY_HELICOPTER_FIRE:
                              {
                                  if (sprite.processAnimation())
                                  {
                                      if(m_nFireCounter == 0)
                                      {
                                          initSpriteFromArray(sprite, SPRITE_ENEMY_HELICOPTER);
                                      }
                                      else
                                      {
                                          if((i_GameTimer & FIRE_DELAY_MASK) == 0)
                                          {
                                              m_pAbstractGameActionListener.processGameAction(GAMEACTION_ENEMY_FIRE);
                                          }
                                      }
                                  }
                              }
                              break;
                        case SPRITE_ENEMY_HELICOPTER:
                            {
                                  if (sprite.processAnimation())
                                  {
                                      if(getRandomInt(ENEMY_FIRE_FREQUENCY) == (ENEMY_FIRE_FREQUENCY / 2) )
                                      {
                                          if(m_nFireCounter == 0)
                                          {
                                              initSpriteFromArray(sprite, SPRITE_ENEMY_HELICOPTER_FIRE);
                                              m_nFireCounter = ENEMY_FIRE_TIMEOUT;
                                              m_pAbstractGameActionListener.processGameAction(GAMEACTION_ENEMY_FIRE);
                                          }
                                      }
                                  }
                            }
                            break;
                    } 

                if(--sprite.i_TTL < 0)
                {
                    sprite.i_TTL = ENEMY_APPROACHING_DELAY;
                    if(sprite.i_ObjectState > 0) sprite.i_ObjectState--;
                }

                if(sprite.isCollided(pPlayerSight) )
                {
                    if(m_bFire)
                    {
                        if(sprite.i_ObjectType == SPRITE_ENEMY_HELICOPTER_FIRE)
                        {
                            m_nFireCounter = 0;
                        }
                        initSpriteFromArray(sprite, SPRITE_ENEMY_HELICOPTER_SHOOTED);
                        m_pAbstractGameActionListener.processGameAction(GAMEACTION_ENEMY_SHOOTED);
                    }
                }
            } 
        } 
    }

    private void processPaths()
    {
        for (int li = 0; li < MAX_PATHS; li++)
        {
            if (ap_Paths[li].isCompleted()) continue;
            int i_spr = ap_Paths[li].p_sprite.i_ObjectType;

            boolean lg_moved = true;

            switch (i_spr)
            {
                case -1:
                    lg_moved = false;
            }

            if (lg_moved)
            {
                ap_Paths[li].processStep();
                if (ap_Paths[li].isCompleted())
                {
                    if(ap_Paths[li].p_sprite.i_ObjectType == SPRITE_ENEMY_HELICOPTER_FIRE)
                    {
                        m_nFireCounter = 0;
                    }
                    ap_Paths[li].p_sprite.lg_SpriteActive = false;
                }
            }
        }
    }

    public String getGameTextID()
    {
        return "HELICOPTERS";
    }

    private PathController getInactivePath()
    {
        for (int li = 0; li < MAX_PATHS; li++)
        {
            if (ap_Paths[li].isCompleted()) return ap_Paths[li];
        }
        return null;
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
        ap_Paths = new PathController[MAX_PATHS];
        for (int li = 0; li < MAX_PATHS; li++)
        {
            ap_Paths[li] = new PathController();
        }

        pPlayerSight = new Sprite(0);

        initSpriteFromArray(pPlayerSight, SPRITE_HELICOPTER_NORMAL);
        pPlayerSight.lg_SpriteActive = true;

        int i8_cx = i8_gameScreenWidth >> 1;
        int i8_cy = i8_gameScreenHeight >> 1;

        pPlayerSight.setMainPointXY(i8_cx, i8_cy);

        p_MoveObject = new MoveObject();
        i_gunsight_delay = GUNSIGHT_CHANGESPEED_DELAY;

        return true;
    }

    private boolean processHelicopter(int _buttonState)
    {
        if (pPlayerSight.processAnimation())
        {
            switch(pPlayerSight.i_ObjectType)
            {
                case SPRITE_HELICOPTER_NORMAL:
                case SPRITE_HELICOPTER_SHOOTED:
                    break;
                case SPRITE_HELICOPTER_DESTROYED:
                    return false;
                case SPRITE_HELICOPTER_FIRE:
                case SPRITE_HELICOPTER_UP:
                case SPRITE_HELICOPTER_DOWN:
                case SPRITE_HELICOPTER_LEFT:
                case SPRITE_HELICOPTER_RIGHT:
                    if(_buttonState == MoveObject.BUTTON_NONE)
                    {
                        initSpriteFromArray(pPlayerSight, SPRITE_HELICOPTER_NORMAL);
                    }
                    break;
            }
        }

        if (pPlayerSight.i_ObjectType == SPRITE_HELICOPTER_DESTROYED) return true;
        if (pPlayerSight.i_ObjectType == SPRITE_HELICOPTER_SHOOTED)
        {
            int newY = pPlayerSight.i_mainY + HELICOPTER_FALL_SPEED;

            if (newY >= GROUND_LINE)
            {
                newY = GROUND_LINE;
                initSpriteFromArray(pPlayerSight, SPRITE_HELICOPTER_DESTROYED);
                m_pAbstractGameActionListener.processGameAction(GAMEACTION_PLAYER_DESTROYED);
            }

            pPlayerSight.setMainPointXY(pPlayerSight.i_mainX, newY);
        }
        else
        if ((_buttonState & MoveObject.BUTTON_FIRE) != 0)
        {
            if(pPlayerSight.i_ObjectType != SPRITE_HELICOPTER_FIRE)
            {
                initSpriteFromArray(pPlayerSight, SPRITE_HELICOPTER_FIRE);
                m_pAbstractGameActionListener.processGameAction(GAMEACTION_PLAYER_FIRE);
            }
            if((i_GameTimer & FIRE_DELAY_MASK) == 0)
            {
                m_pAbstractGameActionListener.processGameAction(GAMEACTION_PLAYER_FIRE);
            }
        }
        else
        {
            int i_x = pPlayerSight.i_ScreenX;
            int i_y = pPlayerSight.i_ScreenY;

            int i_mx = pPlayerSight.i_mainX;
            int i_my = pPlayerSight.i_mainY;

            int i_w = pPlayerSight.i_width;
            int i_h = pPlayerSight.i_height;

            int i_spV, i_spH;

            if (i_gunsight_delay == 0)
            {
                i_spH = GUNSIGHT_HORZMAX_SPEED;
                i_spV = GUNSIGHT_VERTMAX_SPEED;
            }
            else
            {
                i_spH = GUNSIGHT_HORZTMIN_SPEED;
                i_spV = GUNSIGHT_VERTMIN_SPEED;
            }

            int state =  SPRITE_HELICOPTER_NORMAL;

            if ((_buttonState & MoveObject.BUTTON_LEFT) != 0)
            {
                i_mx = i_x - i_spH < 0 ? (i_w >> 1) : i_mx - i_spH;
                if(i_x - i_spH > 0)
                {
                    state = SPRITE_HELICOPTER_LEFT;
                }
                if(pPlayerSight.i_ObjectType != state)
                {
                       initSpriteFromArray(pPlayerSight, state);
                }
            }
            else if ((_buttonState & MoveObject.BUTTON_RIGHT) != 0)
            {
                i_mx = i_x + i_w + i_spH >= i8_gameScreenWidth ? i8_gameScreenWidth - (i_w >> 1) : i_mx + i_spH;
                if((i_x + i_w + i_spH) < i8_gameScreenWidth)
                {
                    state = SPRITE_HELICOPTER_RIGHT;
                }
                if(pPlayerSight.i_ObjectType != state)
                {
                       initSpriteFromArray(pPlayerSight, state);
                }
            }

            if ((_buttonState & MoveObject.BUTTON_UP) != 0)
            {
                i_my = i_y - i_spV < 0 ? (i_h >> 1) : i_my - i_spV;
                if((i_y - i_spV) > 0)
                {
                    state = SPRITE_HELICOPTER_UP;
                }
                if(pPlayerSight.i_ObjectType != state)
                {
                       initSpriteFromArray(pPlayerSight, state);
                }
            }
            else if ((_buttonState & MoveObject.BUTTON_DOWN) != 0)
            {
                i_my = i_y + i_h + i_spV >= i8_gameScreenHeight ? i8_gameScreenHeight - (i_h >> 1) : i_my + i_spV;
                if((i_y + i_h + i_spV) < i8_gameScreenHeight)
                {
                    state = SPRITE_HELICOPTER_DOWN;
                }
                if(pPlayerSight.i_ObjectType != state)
                {
                       initSpriteFromArray(pPlayerSight, state);
                }
            }
            pPlayerSight.setMainPointXY(i_mx, i_my);
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
                    i_borndelay = LEVEL_EASY_ENEMYFREQ;
                    m_iPlayerAttemptions = LEVEL_EASY_LIVES;
                }
                ;
                break;
            case GAMELEVEL_NORMAL:
                {
                    i_timedelay = LEVEL_NORMAL_TIMEDELAY;
                    i_borndelay = LEVEL_NORMAL_ENEMYFREQ;
                    m_iPlayerAttemptions = LEVEL_NORMAL_LIVES;
                }
                ;
                break;
            case GAMELEVEL_HARD:
                {
                    i_timedelay = LEVEL_HARD_TIMEDELAY;
                    i_borndelay = LEVEL_HARD_ENEMYFREQ;
                    m_iPlayerAttemptions = LEVEL_HARD_LIVES;
                }
                ;
                break;
        }

        i_GameTimer = 0; 
        i_lasttime = i_GameTimer;
        m_nFireCounter = 0;
        m_bFire = false;

        return true;
    }

    public void resumeGameAfterPlayerLostOrPaused()
    {
        super.resumeGameAfterPlayerLostOrPaused();

        deinitState();
        initState();

        i_GameTimer = 0; 
        i_lasttime = i_GameTimer;
        m_nFireCounter = 0;
        m_bFire = false;
    }

    public void deinitState()
    {
        ap_Sprites = null;
        pPlayerSight = null;
        ap_Paths = null;
        p_MoveObject = null;

        Runtime.getRuntime().gc();
    }

    public int _getMaximumDataSize()
    {
        return MAX_PATHS*PathController.DATASIZE_BYTES+MAX_SPRITES*(Sprite.DATASIZE_BYTES+1)+6+Sprite.DATASIZE_BYTES+1 + 6;
    }

    public void _saveGameData(DataOutputStream _outputStream) throws IOException
    {
        _outputStream.writeByte(m_iPlayerAttemptions);
        _outputStream.writeByte(i_GameTimer);
        _outputStream.writeByte(i_lasttime);
        _outputStream.writeByte(i_borndelay);
        _outputStream.writeByte(m_nFireCounter);

        for(int li=0;li<MAX_SPRITES;li++)
        {
            _outputStream.writeByte(ap_Sprites[li].i_ObjectType);
            ap_Sprites[li].writeSpriteToStream(_outputStream);
        }

        for(int li=0;li<MAX_PATHS;li++) ap_Paths[li].writePathToStream(_outputStream);

        _outputStream.writeInt(i_rndfreq);

        _outputStream.writeByte(pPlayerSight.i_ObjectType);
        pPlayerSight.writeSpriteToStream(_outputStream);
    }

    public void _loadGameData(DataInputStream _inputStream) throws IOException
    {
        m_iPlayerAttemptions = _inputStream.readByte();
        i_GameTimer = _inputStream.readByte();
        i_lasttime = _inputStream.readByte();
        i_borndelay = _inputStream.readByte();
        m_nFireCounter = _inputStream.readByte();

        for(int li=0;li<MAX_SPRITES;li++)
        {
            int i_type = _inputStream.readByte();
            initSpriteFromArray(ap_Sprites[li],i_type);
            ap_Sprites[li].readSpriteFromStream(_inputStream);
        }

        for(int li=0;li<MAX_PATHS;li++)
        {
            ap_Paths[li].readPathFromStream(_inputStream,ap_Sprites);
            ap_Paths[li].as_pathArray = ash_Paths;
        }

        i_rndfreq = _inputStream.readInt();

        initSpriteFromArray(pPlayerSight,_inputStream.readUnsignedByte());
        pPlayerSight.readSpriteFromStream(_inputStream);
    }

    private void generateNewHelicopter()
    {
        if((i_lasttime + i_borndelay) < i_GameTimer )
        {
            i_lasttime = i_GameTimer;
            Sprite p_emptySprite = getInactiveSprite();
            PathController p_inactivePath = getInactivePath();
            if (p_emptySprite != null && p_inactivePath != null)
            {
                int i_type = getRandomInt(80) / 10;

                int i_path = 0;


                    switch (i_type)
                    {
                        default:
                        case 0:
                            {
                                i_path = PATH_HELICOPTER_1;
                                i_type = SPRITE_ENEMY_HELICOPTER;
                            }
                            break;
                        case 1:
                            {
                                i_path = PATH_HELICOPTER_2;
                                i_type = SPRITE_ENEMY_HELICOPTER;
                            }
                            break;
                        case 2:
                            {
                                i_path = PATH_HELICOPTER_3;
                                i_type = SPRITE_ENEMY_HELICOPTER;
                            }
                            break;
                        case 3:
                            {
                                i_path = PATH_HELICOPTER_4;
                                i_type = SPRITE_ENEMY_HELICOPTER;
                            }
                            break;
                        case 4:
                            {
                                i_path = PATH_HELICOPTER_5;
                                i_type = SPRITE_ENEMY_HELICOPTER;
                            }
                            break;
                       case 5:
                            {
                                i_path = PATH_HELICOPTER_6;
                                i_type = SPRITE_ENEMY_HELICOPTER;
                            }
                            break;
                       case 6:
                            {
                                i_path = PATH_HELICOPTER_7;
                                i_type = SPRITE_ENEMY_HELICOPTER;
                            }
                            break;
                       case 7:
                            {
                                i_path = PATH_HELICOPTER_8;
                                i_type = SPRITE_ENEMY_HELICOPTER;
                            }
                            break;
                    }

                initSpriteFromArray(p_emptySprite, i_type);
                p_inactivePath.initPath(0, 0, p_emptySprite, ash_Paths, i_path, 0, 0, PathController.MODIFY_NONE);
                p_emptySprite.i_ObjectState = ENEMY_MAX_DISTANCE_FRAMES-1;
                p_emptySprite.i_TTL = ENEMY_APPROACHING_DELAY;
                p_emptySprite.lg_SpriteActive = true;
            }
        }
    }

    public int nextGameStep()
    {
        CAbstractPlayer p_player = m_pPlayerList[0];
        p_player.nextPlayerMove(this, p_MoveObject);

        m_bFire = ((p_MoveObject.i_buttonState & MoveObject.BUTTON_FIRE) == MoveObject.BUTTON_FIRE);

        processPaths();

        if (processHelicopter(p_MoveObject.i_buttonState) )
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


        processSprites();
        generateNewHelicopter();

        if(m_nFireCounter > 0)
        {
            m_nFireCounter--;
            if(m_nFireCounter == 0)
            {
                initSpriteFromArray(pPlayerSight, SPRITE_HELICOPTER_SHOOTED);
                m_pAbstractGameActionListener.processGameAction(GAMEACTION_PLAYER_SHOOTED);
            }
        }

        i_GameTimer++;

        return m_iGameState;
    }

    public int getGameStepDelay()
    {
        return i_timedelay;
    }


}
