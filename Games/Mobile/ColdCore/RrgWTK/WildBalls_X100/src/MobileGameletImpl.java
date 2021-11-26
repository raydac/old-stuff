
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.DataInputStream;

public class MobileGameletImpl extends MobileGamelet
{
    public static final int INIT_ANGLE1 = 16; 
    public static final int INIT_ANGLE2 = 32; 
    public static final int INIT_ANGLE3 = 0;  
    public static final int MOVE_SPEED  = 0x380;
    public static final int MOVE_SPACE  = (12) << 8;


    public static final int BALL_SPEED  = 0x380;
    public static final int BALL_SPEED4 = (2) << 8;
    public static final int BALL_SPEED3 = (3) << 8;
    public static final int BALL_SPEED2 = (2) << 8;
    public static final int BALL_SPEED1 = (3) << 8;

    public static final int BALL_RADIUS  = 7; 

    public static final int BALL_Y4     = (50) << 8;
    public static final int BALL_Y3     = (60) << 8;
    public static final int BALL_Y2     = (60) << 8;
    public static final int BALL_Y1     = (70) << 8;

    public static final int SKITTLES_X = (10) << 8;
    public static final int SKITTLES_Y = (10) << 8;
    public static final int SKITTLE_SPACE = (26) << 8;

    public static final int FAIL_LINE = (40) << 8;
    public static final int STOP_LINE = (20+1) << 8;

    public static final int INFO_PANEL_HEIGHT = ((13) << 8) + 0x600;

    public static final int MAX_PLAYER_LIVES = 4;

    private static final int LEVEL_EASY_TIMEDELAY = 100;

    private static final int LEVEL_EASY_LIVES = 4;

    private static final int LEVEL_EASY_BALLFREQ = 10;

    private static final int LEVEL_NORMAL_TIMEDELAY = 90;

    private static final int LEVEL_NORMAL_LIVES = 3;

    private static final int LEVEL_NORMAL_BALLFREQ = 20;

    private static final int LEVEL_HARD_TIMEDELAY = 80;

    private static final int LEVEL_HARD_LIVES = 2;

    private static final int LEVEL_HARD_BALLFREQ = 30;

    private static final int SCORES_SKITTLE = 10;
    private static final int SCORES_SKITTLE_ALL = 100;

    private static final int SPRITEDATALENGTH = 8;


    public static final int SPRITE_BALL_APPEARING           = 0;
    public static final int SPRITE_BALL                     = 1;
    public static final int SPRITE_BALL_RUN                 = 2;
    public static final int SPRITE_BALL_CHANGED             = 3;
    public static final int SPRITE_BALL_DEAD                = 4;
    public static final int SPRITE_SKITTLE_APPEARING        = 5;
    public static final int SPRITE_SKITTLE                  = 6;
    public static final int SPRITE_SKITTLE_FALL             = 7;
    public static final int SPRITE_BAR                      = 8;

    public static final int MAX_SPRITES = 8;


    public static final int GAMEACTION_SKITTLE_HIT          = 0;
    public static final int GAMEACTION_SKITTLE_FALLEN       = 1;
    public static final int GAMEACTION_NEW_BALL_GENERATED   = 2;
    public static final int GAMEACTION_BALLS_HIT            = 3;
    public static final int GAMEACTION_RAISE_BAR            = 4;
    public static final int GAMEACTION_RUN_BALL             = 5;
    public static final int GAMEACTION_ALL_SKITTLE          = 6;
    public static final int GAMEACTION_BALL_CRASH           = 7;


    public int m_iPlayerAttemptions;

    private int i_timedelay;

    public int i_GameTimer;

    public int i_borndelay;
    public int i_lastball;


    public int m_nSkittles;


    public Sprite pPlayerBall;

    public Sprite pBall;

    public Sprite[] ap_Sprites;

    public MoveObject p_MoveObject;

    private int i8_gameScreenWidth;

    private int i8_gameScreenHeight;

    private int i8_initPlayerY;

    private int i8_initPlayerX;


    private static final int[] ai_SpriteParameters = new int[]
    {
        0x1000, 0x1000, 0x0E00, 0x0E00, 6, 2, Sprite.SPRITE_ALIGN_CENTER, Sprite.ANIMATION_FROZEN,
        0x1000, 0x1000, 0x0E00, 0x0E00, 2, 1, Sprite.SPRITE_ALIGN_CENTER, Sprite.ANIMATION_CYCLIC,
        0x1000, 0x1000, 0x0E00, 0x0E00, 2, 1, Sprite.SPRITE_ALIGN_CENTER, Sprite.ANIMATION_CYCLIC,
        0x1000, 0x1000, 0x1000, 0x1000, 2, 1, Sprite.SPRITE_ALIGN_CENTER, Sprite.ANIMATION_CYCLIC,
        0x1000, 0x1000, 0x1000, 0x1000, 4, 2, Sprite.SPRITE_ALIGN_CENTER, Sprite.ANIMATION_FROZEN,
        0x0800, 0x0800, 0x0800, 0x0800, 5, 1, Sprite.SPRITE_ALIGN_CENTER, Sprite.ANIMATION_CYCLIC,
        0x0800, 0x0800, 0x0800, 0x0800, 2, 1, Sprite.SPRITE_ALIGN_CENTER, Sprite.ANIMATION_CYCLIC,
        0x0800, 0x0800, 0x0800, 0x0800, 8, 1, Sprite.SPRITE_ALIGN_CENTER, Sprite.ANIMATION_FROZEN,
        0x7600, 0x0600, 0x7600, 0x0600, 3, 1, Sprite.SPRITE_ALIGN_CENTER, Sprite.ANIMATION_FROZEN
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
                        case SPRITE_SKITTLE_APPEARING:
                            initSpriteFromArray(ap_Sprites[li], SPRITE_SKITTLE);
                            break;
                        case SPRITE_SKITTLE_FALL:
                            m_pAbstractGameActionListener.processGameAction(GAMEACTION_SKITTLE_FALLEN);
                            ap_Sprites[li].lg_SpriteActive = false;
                            m_nSkittles++;

                            if(m_nSkittles % 5 == 0)
                            {
                               generateSkittles(true);
                               addScore(SCORES_SKITTLE_ALL);
                               m_pAbstractGameActionListener.processGameAction(GAMEACTION_ALL_SKITTLE);
                               if(m_iPlayerAttemptions < MAX_PLAYER_LIVES) m_iPlayerAttemptions++;
                            }
                            break;
                    }
                } 

                switch(ap_Sprites[li].i_ObjectType)
                {
                    case SPRITE_SKITTLE:
                        if(ap_Sprites[li].isCollided(pPlayerBall) ||
                           ap_Sprites[li].isCollided(pBall) )
                        {
                            initSpriteFromArray(ap_Sprites[li], SPRITE_SKITTLE_FALL);
                            m_pAbstractGameActionListener.processGameAction(GAMEACTION_SKITTLE_HIT);
                            addScore(SCORES_SKITTLE);
                        }
                        break;
                    case SPRITE_BAR:
                        int bottom = ap_Sprites[li].i_ScreenY+ap_Sprites[li].i_col_offsetY+ap_Sprites[li].i_col_height;
                        if(pPlayerBall.i_ScreenY <= bottom)
                        {
                            if(pPlayerBall.i_ObjectType != SPRITE_BALL_DEAD)
                            {
                                initSpriteFromArray(pPlayerBall, SPRITE_BALL_DEAD);
                                m_pAbstractGameActionListener.processGameAction(GAMEACTION_BALL_CRASH);
                                pPlayerBall.setMainPointXY(pPlayerBall.i_mainX,bottom + (pPlayerBall.i_width>>1));
                            }
                        }

                        break;
                }

            } 
        } 
    }

    public String getGameTextID()
    {
        return "WILDBALLS";
    }

    private Sprite getInactiveSprite()
    {
        for (int li = 0; li < MAX_SPRITES; li++)
        {
            if (!ap_Sprites[li].lg_SpriteActive) return ap_Sprites[li];
        }
        return null;
    }

    private void DeactiveSprites()
    {
        for (int li = 0; li < MAX_SPRITES; li++)
        {
            if (ap_Sprites[li].lg_SpriteActive) ap_Sprites[li].lg_SpriteActive = false;
        }
    }

    public boolean initState()
    {
        ap_Sprites = new Sprite[MAX_SPRITES];
        for (int li = 0; li < MAX_SPRITES; li++)
        {
            ap_Sprites[li] = new Sprite(li);
        }

        pPlayerBall = new Sprite(0);
        pBall = new Sprite(0);

        initSpriteFromArray(pPlayerBall, SPRITE_BALL);
        pPlayerBall.lg_SpriteActive = true;

        initSpriteFromArray(pBall, SPRITE_BALL_RUN);
        pBall.lg_SpriteActive = true;

        i8_initPlayerX = i8_gameScreenWidth >> 1;
        i8_initPlayerY = i8_gameScreenHeight - INFO_PANEL_HEIGHT - (pPlayerBall.i_width>>1);

        pPlayerBall.setMainPointXY(i8_initPlayerX, i8_initPlayerY);
        pPlayerBall.i_ObjectState = INIT_ANGLE1;
        pPlayerBall.i_Speed = BALL_SPEED;

        pBall.setMainPointXY((-30 << 8), (-30 << 8));
        pBall.i_ObjectState = INIT_ANGLE2;

        p_MoveObject = new MoveObject();

        return true;
    }

    private void processBall2()
    {
        if (pBall.processAnimation())
        {
        }

        switch(pBall.i_ObjectType)
        {
            case SPRITE_BALL_RUN:
                Sprite obj = pBall;

                if(obj.i_mainY < (0x0000 - obj.i_height) || obj.i_mainY > (i8_gameScreenHeight + obj.i_height) || obj.i_mainX < (0x0000 - obj.i_width) || obj.i_mainX > (i8_gameScreenWidth + obj.i_width ))
                {
                    if((pPlayerBall.i_ObjectType == SPRITE_BALL || pPlayerBall.i_ObjectType == SPRITE_BALL_RUN) && (i_GameTimer > (i_lastball + i_borndelay)) )
                    {
                        int ball = getRandomInt(40) / 10;
                        int ball_x = 0;
                        int ball_y = 0;

                        i_lastball = i_GameTimer;

                        switch(ball)
                        {
                            default:
                            case 0:
                                ball_x = i8_gameScreenWidth;
                                ball_y = BALL_Y2;
                                pBall.i_ObjectState = INIT_ANGLE2;
                                pBall.i_Speed = BALL_SPEED2;
                                break;
                            case 1:
                                ball_x = 0;
                                ball_y = BALL_Y1;
                                pBall.i_ObjectState = INIT_ANGLE3;
                                pBall.i_Speed = BALL_SPEED1;
                                break;
                            case 2:
                                ball_x = i8_gameScreenWidth;
                                ball_y = BALL_Y3;
                                pBall.i_ObjectState = INIT_ANGLE2;
                                pBall.i_Speed = BALL_SPEED3;
                                break;
                            case 3:
                                ball_x = 0;
                                ball_y = BALL_Y4;
                                pBall.i_ObjectState = INIT_ANGLE3;
                                pBall.i_Speed = BALL_SPEED4;
                                break;
                        }
                        pBall.i_spriteID = getRandomInt(3);
                        pBall.lg_SpriteActive = true;
                        pBall.setMainPointXY(ball_x, ball_y);
                        m_pAbstractGameActionListener.processGameAction(GAMEACTION_NEW_BALL_GENERATED);
                    }
                    else
                    {
                        obj.lg_SpriteActive = false;
                        return;
                    }
                }

                int x = obj.i_mainX;
                int y = obj.i_mainY;

                int speed = obj.i_Speed;

                int angle = obj.i_ObjectState;

                x += ( xCoSine(speed, angle) );
                y -= ( xSine(speed, angle) );

                obj.setMainPointXY(x,y);


                break;
        }
    }


    private boolean processBall(int _buttonState)
    {
        Sprite obj = pPlayerBall;

        int x = obj.i_mainX;
        int y = obj.i_mainY;

        int speed = obj.i_Speed;

        int angle = obj.i_ObjectState;

        x += ( xCoSine(speed, angle) );
        y -= ( xSine(speed, angle) );

        if (pPlayerBall.processAnimation())
        {
            switch(obj.i_ObjectType)
            {
               case SPRITE_BALL_DEAD:
                         {
                           obj.lg_SpriteActive = false;
                           return false;
                         }
               case SPRITE_BALL_APPEARING:
                         {
                           initSpriteFromArray(obj, SPRITE_BALL);
                         }
                         break;
            }
        }

        switch(pPlayerBall.i_ObjectType)
        {
            case SPRITE_BALL_CHANGED:
                obj.setMainPointXY(x,y);

                if(obj.i_mainY < (0x0000 - obj.i_height) || obj.i_mainY > (i8_gameScreenHeight + obj.i_height) || obj.i_mainX < (0x0000 - obj.i_width) || obj.i_mainX > (i8_gameScreenWidth + obj.i_width ))
                {
                    initSpriteFromArray(obj, SPRITE_BALL_APPEARING);
                    obj.setMainPointXY(i8_initPlayerX, i8_initPlayerY);
                    obj.i_ObjectState = INIT_ANGLE1;
                }
                break;
            case SPRITE_BALL_RUN:
                if(obj.i_mainY < (0x0000 - obj.i_height) || obj.i_mainY > (i8_gameScreenHeight + obj.i_height) || obj.i_mainX < (0x0000 - obj.i_width) || obj.i_mainX > (i8_gameScreenWidth + obj.i_width ))
                {
                    initSpriteFromArray(obj, SPRITE_BALL_APPEARING);
                    obj.setMainPointXY(i8_initPlayerX, i8_initPlayerY);
                    obj.i_ObjectState = INIT_ANGLE1;
                    break;
                }

                obj.setMainPointXY(x,y);

                if(obj.isCollided(pBall))
                {
                    int dx = ((obj.i_mainX - pBall.i_mainX) >> 8);
                    int dy = ((obj.i_mainY - pBall.i_mainY) >> 8);

                    int angle1 = 0; 
                    int angle2 = 0; 


                    if(pBall.i_ObjectState == INIT_ANGLE2)
                    {

                        if(dy > 0)
                        {

                            if(dx < 0)
                            {
                                angle1 = 12;
                            }
                            else
                            {
                                angle1 = -12;
                            }

                            dx = Math.abs(dx);
                            if(dx < BALL_RADIUS)
                            {
                                angle2 = -12; 
                            }
                            else
                            {
                                angle2 = -6;
                            }
                        }
                        else
                        {

                            if(dx < 0)
                            {
                                angle1 = 12;
                            }
                            else
                            {
                                angle1 = -12; 
                            }

                            dx = Math.abs(dx);
                            if(dx < BALL_RADIUS)
                            {
                                angle2 = 12; 
                            }
                            else
                            {
                                angle2 = 6;
                            }

                        }
                    }
                    else
                    {
                        if(dy > 0)
                        {

                            if(dx < 0)
                            {
                                angle1 = 12;
                            }
                            else
                            {
                                angle1 = -12;
                            }

                            dx = Math.abs(dx);
                            if(dx < BALL_RADIUS)
                            {
                                angle2 = 12; 
                            }
                            else
                            {
                                angle2 = 6;
                            }

                        }
                        else
                        {
                            if(dx < 0)
                            {
                                angle1 = 12; 
                            }
                            else
                            {
                                angle1 = -12;
                            }

                            dx = Math.abs(dx);
                            if(dx < BALL_RADIUS)
                            {
                                angle2 = -12; 
                            }
                            else
                            {
                                angle2 = -6;
                            }

                        }
                    }


                    obj.i_ObjectState += angle1;
                    pBall.i_ObjectState += angle2;

                    obj.i_ObjectState = obj.i_ObjectState & 63; 
                    pBall.i_ObjectState = pBall.i_ObjectState & 63; 


                    initSpriteFromArray(obj, SPRITE_BALL_CHANGED);
                    m_pAbstractGameActionListener.processGameAction(GAMEACTION_BALLS_HIT);

                    if(obj.i_Speed < pBall.i_Speed) obj.i_Speed = pBall.i_Speed;

                }

                if(obj.i_mainY < FAIL_LINE)
                {
                    generateBar();
                    m_pAbstractGameActionListener.processGameAction(GAMEACTION_RAISE_BAR);
                }

                break;
            case SPRITE_BALL:
            {
                int i_x = pPlayerBall.i_ScreenX;
                int i_y = pPlayerBall.i_ScreenY;

                int i_mx = pPlayerBall.i_mainX;
                int i_my = pPlayerBall.i_mainY;

                int i_w = pPlayerBall.i_width;
                int i_h = pPlayerBall.i_height;

                int i_spV, i_spH;

                if ((_buttonState & MoveObject.BUTTON_FIRE) != 0)
                {
                    initSpriteFromArray(pPlayerBall, SPRITE_BALL_RUN);
                    m_pAbstractGameActionListener.processGameAction(GAMEACTION_RUN_BALL);
                    obj.i_Speed = BALL_SPEED;
                }
                else
                if ((_buttonState & MoveObject.BUTTON_LEFT) != 0)
                {
                    i_mx -= MOVE_SPEED;
                    if(i_mx < (0 + MOVE_SPACE) )
                    {
                       i_mx = (0 + MOVE_SPACE);
                    }
                    pPlayerBall.setMainPointXY(i_mx, i_my);
                }
                else if ((_buttonState & MoveObject.BUTTON_RIGHT) != 0)
                {
                    i_mx += MOVE_SPEED;
                    if(i_mx > (i8_gameScreenWidth - MOVE_SPACE))
                    {
                      i_mx = (i8_gameScreenWidth - MOVE_SPACE);
                    }
                    pPlayerBall.setMainPointXY(i_mx, i_my);
                }
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
        i_lastball = i_GameTimer;
        m_nSkittles = 0;

        generateSkittles(false);

        return true;
    }

    public void resumeGameAfterPlayerLostOrPaused()
    {
        super.resumeGameAfterPlayerLostOrPaused();



        i_GameTimer = 0; 
        i_lastball = i_GameTimer;

        for (int li = 0; li < MAX_SPRITES; li++)
        {
            if (ap_Sprites[li].lg_SpriteActive && ap_Sprites[li].i_ObjectType== SPRITE_BAR)
            {
                ap_Sprites[li].lg_SpriteActive = false;
            }
        }
        if(pPlayerBall != null)
        {
          pPlayerBall.lg_SpriteActive = true;
          initSpriteFromArray(pPlayerBall, SPRITE_BALL_APPEARING);
          pPlayerBall.setMainPointXY(i8_initPlayerX, i8_initPlayerY);
          pPlayerBall.i_ObjectState = INIT_ANGLE1;
        }
    }

    public void deinitState()
    {
        ap_Sprites = null;
        pPlayerBall = null;
        pBall = null;
        p_MoveObject = null;

        Runtime.getRuntime().gc();
    }

    public int _getMaximumDataSize()
    {
        return MAX_SPRITES*(Sprite.DATASIZE_BYTES+1)+(Sprite.DATASIZE_BYTES+1) * 2 + 17;
    }

    public void _saveGameData(DataOutputStream _outputStream) throws IOException
    {
        _outputStream.writeByte(m_iPlayerAttemptions);
        _outputStream.writeInt(i_GameTimer);
        _outputStream.writeInt(i_borndelay);
        _outputStream.writeInt(i_lastball);
        _outputStream.writeInt(m_nSkittles);

        for(int li=0;li<MAX_SPRITES;li++)
        {
            _outputStream.writeByte(ap_Sprites[li].i_ObjectType);
            ap_Sprites[li].writeSpriteToStream(_outputStream);
        }

        _outputStream.writeByte(pPlayerBall.i_ObjectType);
        pPlayerBall.writeSpriteToStream(_outputStream);

        _outputStream.writeByte(pBall.i_ObjectType);
        pBall.writeSpriteToStream(_outputStream);
    }

    public void _loadGameData(DataInputStream _inputStream) throws IOException
    {
        m_iPlayerAttemptions = _inputStream.readByte();
        i_GameTimer = _inputStream.readInt();
        i_borndelay = _inputStream.readInt();
        i_lastball = _inputStream.readInt();
        m_nSkittles = _inputStream.readInt();

        for(int li=0;li<MAX_SPRITES;li++)
        {
            int i_type = _inputStream.readByte();
            initSpriteFromArray(ap_Sprites[li],i_type);
            ap_Sprites[li].readSpriteFromStream(_inputStream);
        }

        initSpriteFromArray(pPlayerBall,_inputStream.readUnsignedByte());
        pPlayerBall.readSpriteFromStream(_inputStream);

        initSpriteFromArray(pBall,_inputStream.readUnsignedByte());
        pBall.readSpriteFromStream(_inputStream);
    }

    private void generateSkittles(boolean lg_appearing)
    {
        int n = 5;
        int y = SKITTLES_Y;
        int x = SKITTLES_X;

        DeactiveSprites();

        while(n > 0)  
        {
            Sprite p_emptySprite = getInactiveSprite();
            if (p_emptySprite != null)
            {
                if(lg_appearing)
                {
                    initSpriteFromArray(p_emptySprite, SPRITE_SKITTLE_APPEARING);
                }
                  else
                        {
                            initSpriteFromArray(p_emptySprite, SPRITE_SKITTLE);
                        }
                p_emptySprite.lg_SpriteActive = true;
                p_emptySprite.setMainPointXY(x, y);

            }
            n--;
            x += SKITTLE_SPACE;
        }
    }

    private void generateBar()
    {
        int y = STOP_LINE;
        int x = i8_gameScreenWidth / 2;

        Sprite p_emptySprite = null;

        for (int li = 0; li < MAX_SPRITES; li++)
        {
            if (!ap_Sprites[li].lg_SpriteActive)
                p_emptySprite = ap_Sprites[li];
              else
                    if (ap_Sprites[li].i_ObjectType== SPRITE_BAR)
                         return;
        }

        if (p_emptySprite != null)
        {
            initSpriteFromArray(p_emptySprite, SPRITE_BAR);
            p_emptySprite.lg_SpriteActive = true;
            p_emptySprite.setMainPointXY(x, y);
        }
    }


    public int nextGameStep()
    {
        CAbstractPlayer p_player = m_pPlayerList[0];
        p_player.nextPlayerMove(this, p_MoveObject);




        if (processBall(p_MoveObject.i_buttonState) && processBall(p_MoveObject.i_buttonState))
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
        processBall2();
        processBall2();

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
