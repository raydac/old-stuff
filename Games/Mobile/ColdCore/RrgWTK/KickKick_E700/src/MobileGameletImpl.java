
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.DataInputStream;

public class MobileGameletImpl extends MobileGamelet
{
    public static final int RIGHT_MARGIN = (48 << 8); 
    public static final int LEFT_MARGIN = (48 << 8);  
    public static final int BORDER = (8 << 8);
    public static final int GATE_SPACE = ((0x24 ) << 8);

    public static final int FOOTBALLER_X = (32 << 8);
    public static final int FOOTBALLER_Y1 = (40 << 8);
    public static final int FOOTBALLER_Y2 = (87 << 8);
    public static final int FOOTBALLER_SPACE = (32 << 8);

    public static final int FOOTBALLER_SPEED = (0x0200);

    public static final int FIRST_HSPEED = (0x0200);
    public static final int FIRST_VSPEED = (0x0300);
    public static final int SECOND_VSPEED = (0x0500);

    public static final int INIT_HSPEED = (FIRST_HSPEED);
    public static final int INIT_VSPEED = (FIRST_VSPEED);

    public static final int COLLISION_DELAY = 5;

    private final static int I8_SCREEN_WIDTH = 128 << 8;

    private final static int I8_SCREEN_HEIGHT = 128 << 8;



    private static final int FIELD_LEFT_BOUND = 0x0000 + BORDER +0x0300;
    private static final int FIELD_RIGHT_BOUND = I8_SCREEN_WIDTH - BORDER -0x0300;
    private static final int FIELD_TOP_BOUND = 0x0000 + BORDER +0x0300;
    private static final int FIELD_BOTTOM_BOUND = I8_SCREEN_HEIGHT - BORDER -0x0300;


    private static final int GATE_LEFT_BOUND  = I8_SCREEN_WIDTH / 2 - GATE_SPACE / 2;
    private static final int GATE_RIGHT_BOUND = I8_SCREEN_WIDTH / 2 + GATE_SPACE / 2;


    public MoveObject p_MoveObject;

    public static final int SCORES_GOAL = 30;
    public static final int SCORES_MINUS_WALL = -10;
    public static final int SCORES_MINUS_FOOTBALLER = -10;

    public static final int GAMEACTION_PLAYER_STRIKE        = 0;
    public static final int GAMEACTION_ENEMY_STRIKE         = 1;
    public static final int GAMEACTION_PLAYER_STRIKE_BALL   = 2;
    public static final int GAMEACTION_ENEMY_STRIKE_BALL    = 3;
    public static final int GAMEACTION_TOUCH_WALL           = 4;
    public static final int GAMEACTION_TOUCH_FOOTBALLER     = 5;
    public static final int GAMEACTION_PLAYER_GOAL          = 6;
    public static final int GAMEACTION_ENEMY_GOAL           = 7;

    private int i_timedelay;

    public int i_playerAttemptions;

    public int i_GameTimer;

    public int i_LastTime;
    public int i_GenerateDelay;

    public int i_HSpeed;
    public int i_VSpeed;

    public int i_Goal;
    public int i_Balls1;
    public int i_Balls2;

    public boolean b_TouchWall;

    public boolean b_TouchFootballer;

    public static final int LEVEL_EASY = 0;

    private static final int LEVEL_EASY_ATTEMPTIONS = 4;

    private static final int LEVEL_EASY_GENERATION = 50;

    private static final int LEVEL_EASY_TIMEDELAY = 90;

    public static final int LEVEL_NORMAL = 1;

    private static final int LEVEL_NORMAL_ATTEMPTIONS = 3;

    private static final int LEVEL_NORMAL_GENERATION = 40;

    private static final int LEVEL_NORMAL_TIMEDELAY = 80;

    public static final int LEVEL_HARD = 2;

    private static final int LEVEL_HARD_ATTEMPTIONS = 2;

    private static final int LEVEL_HARD_GENERATION = 30;

    private static final int LEVEL_HARD_TIMEDELAY = 70;


    public Sprite pPlayerSprite;

    public static final int MAX_SPRITES_NUMBER = 12;

    public Sprite[] ap_Sprites;

    private static final int SPRITEDATALENGTH = 10;

    public static final int SPRITE_BALL = 0;
    public static final int SPRITE_FOOTBALLER = 1;
    public static final int SPRITE_FOOTBALLER_STRIKE = 2;
    public static final int SPRITE_FOOTBALLER_AFTER_STRIKE = 3;
    public static final int SPRITE_FOOTBALLER_ENEMY = 4;
    public static final int SPRITE_FOOTBALLER_ENEMY_STRIKE = 5;
    public static final int SPRITE_GATE = 6;
    public static final int SPRITE_GATE_ENEMY = 7;

    private static final int [] ai_SpriteParameters = new int[]
    {
        0x0700, 0x0700, 0x0000, 0x0000, 0x0700, 0x0700, 1, 1, Sprite.SPRITE_ALIGN_CENTER, Sprite.ANIMATION_FROZEN,
        0x0B00, 0x0F00, 0x0000, 0x0000, 0x0B00, 0x0F00, 1, 1, Sprite.SPRITE_ALIGN_CENTER, Sprite.ANIMATION_FROZEN,
        0x0B00, 0x1300, 0x0000, 0x0000, 0x0B00, 0x1300, 5, 1, Sprite.SPRITE_ALIGN_CENTER, Sprite.ANIMATION_FROZEN,
        0x0B00, 0x1300, 0x0000, 0x0000, 0x0B00, 0x1300, 1, 1, Sprite.SPRITE_ALIGN_CENTER, Sprite.ANIMATION_FROZEN,
        0x0B00, 0x0F00, 0x0000, 0x0000, 0x0B00, 0x0F00, 1, 1, Sprite.SPRITE_ALIGN_CENTER, Sprite.ANIMATION_FROZEN,
        0x0B00, 0x1300, 0x0000, 0x0000, 0x0B00, 0x1300, 5, 1, Sprite.SPRITE_ALIGN_CENTER, Sprite.ANIMATION_FROZEN,
        0x2400, 0x0600, 0x0000, 0x0000, 0x2400, 0x0600, 1, 1, Sprite.SPRITE_ALIGN_CENTER, Sprite.ANIMATION_FROZEN,
        0x2400, 0x0600, 0x0000, 0x0000, 0x2400, 0x0600, 1, 1, Sprite.SPRITE_ALIGN_CENTER, Sprite.ANIMATION_FROZEN
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

                    case SPRITE_FOOTBALLER_STRIKE:
                        if((p_MoveObject.i_buttonState & MoveObject.BUTTON_FIRE) == 0)
                        {
                            activateSprite(p_Sprite, SPRITE_FOOTBALLER);
                        }
                         else
                            activateSprite(p_Sprite, SPRITE_FOOTBALLER_AFTER_STRIKE);
                        break;
                    case SPRITE_FOOTBALLER_AFTER_STRIKE:
                        if((p_MoveObject.i_buttonState & MoveObject.BUTTON_FIRE) == 0)
                        {
                            activateSprite(p_Sprite, SPRITE_FOOTBALLER);
                        }
                        break;
                    case SPRITE_FOOTBALLER_ENEMY_STRIKE:
                        activateSprite(p_Sprite, SPRITE_FOOTBALLER_ENEMY);
                        break;
                }
            }
            switch (p_Sprite.i_ObjectType)
            {
                case SPRITE_GATE:
                    if(p_Sprite.isCollided(pPlayerSprite))
                    {
                        int i_mx = pPlayerSprite.i_mainX;
                        int i_my = pPlayerSprite.i_mainY;
                        int i_gy = p_Sprite.i_mainY;
                        int i_r = pPlayerSprite.i_width;

                        {
                        }
                        if((i_Goal == 0))
                        {
                            i_Goal = 1;
                            i_Balls1++;
                            m_pAbstractGameActionListener.processGameAction(GAMEACTION_ENEMY_GOAL);
                        }
                    }
                    break;
                case SPRITE_GATE_ENEMY:
                    if(p_Sprite.isCollided(pPlayerSprite))
                    {
                        int i_mx = pPlayerSprite.i_mainX;
                        int i_my = pPlayerSprite.i_mainY;
                        int i_gy = p_Sprite.i_mainY;
                        int i_r = pPlayerSprite.i_width;

                        {
                        }

                        if((i_Goal == 0))
                        {
                            i_Goal = 2;
                            i_Balls2++;

                            int Score = SCORES_GOAL;

                            if(b_TouchWall) Score += SCORES_MINUS_WALL;
                            if(b_TouchFootballer) Score += SCORES_MINUS_FOOTBALLER;
                            addScore(Score);
                            m_pAbstractGameActionListener.processGameAction(GAMEACTION_PLAYER_GOAL);
                        }
                    }
                    break;
                case SPRITE_FOOTBALLER:
                case SPRITE_FOOTBALLER_ENEMY:
                case SPRITE_FOOTBALLER_STRIKE:
                case SPRITE_FOOTBALLER_AFTER_STRIKE:
                case SPRITE_FOOTBALLER_ENEMY_STRIKE:
                    if(p_Sprite.isCollided(pPlayerSprite))
                    {
                        if((i_LastTime + COLLISION_DELAY) < i_GameTimer)
                        {
                            i_LastTime = i_GameTimer;

                            if(p_Sprite.i_ObjectType == SPRITE_FOOTBALLER || p_Sprite.i_ObjectType == SPRITE_FOOTBALLER_STRIKE || p_Sprite.i_ObjectType == SPRITE_FOOTBALLER_AFTER_STRIKE)
                            {
                                b_TouchWall = false;
                                b_TouchFootballer = false;
                            }
                            else
                            {
                                b_TouchFootballer = true;
                            }

                            m_pAbstractGameActionListener.processGameAction(
                                  p_Sprite.i_ObjectType == SPRITE_FOOTBALLER_STRIKE
                                          ? GAMEACTION_PLAYER_STRIKE_BALL :
                                  p_Sprite.i_ObjectType == SPRITE_FOOTBALLER_ENEMY_STRIKE
                                          ? GAMEACTION_ENEMY_STRIKE_BALL :
                                             GAMEACTION_TOUCH_FOOTBALLER
                            );

                            int dx = p_Sprite.i_mainX - pPlayerSprite.i_mainX;
                            int dy = p_Sprite.i_mainY - pPlayerSprite.i_mainY;

                            int adx = Math.abs(dx);
                            int ady = Math.abs(dy);


                            if(dx < 0)
                            {
                                i_HSpeed = FIRST_HSPEED;
                            }
                            else
                            {
                                i_HSpeed = -FIRST_HSPEED;
                            }
                            if(dy < 0)
                            {
                                i_VSpeed = FIRST_VSPEED;
                            }
                            else
                            {
                                i_VSpeed = -FIRST_VSPEED;
                            }

                            switch(p_Sprite.i_ObjectType)
                            {
                                case SPRITE_FOOTBALLER_STRIKE:
                                case SPRITE_FOOTBALLER_ENEMY_STRIKE:
                                    if(i_VSpeed < 0)
                                    {
                                        i_VSpeed = -SECOND_VSPEED;
                                    }
                                    else
                                    {
                                         i_VSpeed = SECOND_VSPEED;
                                    }
                                    if(adx < pPlayerSprite.i_width / 2 )
                                    {
                                        i_HSpeed = 0;
                                    }
                                    break;
                                default:
                                    if(i_VSpeed > FIRST_VSPEED) i_VSpeed = FIRST_VSPEED;
                                    if(i_VSpeed < -FIRST_VSPEED) i_VSpeed = -FIRST_VSPEED;
                                    break;
                            }
                        }
                    }
                    break;
            }
        }
    }

    private boolean processBall()
    {

        if(i_Goal>0) return true;

        Sprite obj = pPlayerSprite;
        if(obj.lg_SpriteActive)
        {
            int i_x, i_y, i_r, i_dx, i_dy;
            obj.processAnimation();
            boolean touch = false;

            i_x = obj.i_mainX;
            i_y = obj.i_mainY;
            i_r = obj.i_width >> 1;

            if( obj.i_ScreenX >= GATE_LEFT_BOUND && obj.i_ScreenX+obj.i_height <= GATE_RIGHT_BOUND)
            {
               i_x += i_HSpeed;
               i_y += i_VSpeed;


               i_dx = i_x - GATE_LEFT_BOUND;
               if(i_dx > 0)
               {
                 i_dx = GATE_RIGHT_BOUND - i_x;
                 if(i_dx <= 0)
                 {
                   i_x = GATE_RIGHT_BOUND + i_dx;
                 }
               }
                else
                   i_x = GATE_LEFT_BOUND - i_dx;

               i_dy = i_y - 0;
               if(i_dy > 0)
               {
                 i_dy = I8_SCREEN_HEIGHT - i_y;
                 if(i_dy <= 0)
                 {
                    i_y = I8_SCREEN_HEIGHT + i_dy;
                 }
               }
                else
                   i_y = -i_dy;
            }
              else
                   {
                       i_x += i_HSpeed;
                       i_y += i_VSpeed;

                       i_dx = i_x - FIELD_LEFT_BOUND;
                       if(i_dx > 0)
                       {
                         i_dx = FIELD_RIGHT_BOUND - i_x;
                         if(i_dx <= 0)
                            i_x = FIELD_RIGHT_BOUND + i_dx;
                       }
                         else
                             i_x = FIELD_LEFT_BOUND - i_dx;

                       i_dy = i_y - FIELD_TOP_BOUND;
                       if(i_dy > 0)
                       {
                         i_dy = FIELD_BOTTOM_BOUND - i_y;
                         if(i_dy <= 0)
                            i_y = FIELD_BOTTOM_BOUND + i_dy;
                       }
                         else
                             i_y = FIELD_TOP_BOUND - i_dy;
                   }

            if (i_dx <= 0)
            {
                touch = true;
                i_HSpeed = -i_HSpeed;
            }

            if (i_dy <= 0)
            {
                touch = true;
                i_VSpeed = -i_VSpeed;
            }

            obj.setMainPointXY(i_x, i_y);


            if(touch)
            {
                m_pAbstractGameActionListener.processGameAction(GAMEACTION_TOUCH_WALL);
                b_TouchWall = true;
            }

        }

        return true;
    }











    private boolean processFootballer(int _buttonState, boolean bottom)
    {

        int CENTER_FOOTBALLER = 1;

        if(bottom) CENTER_FOOTBALLER = 1+3;

        int i_speed = FOOTBALLER_SPEED;

        int i_mx = ap_Sprites[CENTER_FOOTBALLER].i_mainX;
        int i_my = ap_Sprites[CENTER_FOOTBALLER].i_mainY;

        int i_w = ap_Sprites[CENTER_FOOTBALLER].i_width;
        int i_h = ap_Sprites[CENTER_FOOTBALLER].i_height;

        int state = ap_Sprites[CENTER_FOOTBALLER].i_ObjectType;

        if((state == SPRITE_FOOTBALLER) || (state == SPRITE_FOOTBALLER_ENEMY))
        {
            if ((_buttonState & MoveObject.BUTTON_LEFT) != 0)
            {
                if(i_mx > (0x0000 + LEFT_MARGIN))
                {
                    ap_Sprites[CENTER_FOOTBALLER-1].setMainPointXY(ap_Sprites[CENTER_FOOTBALLER-1].i_mainX -= i_speed, ap_Sprites[CENTER_FOOTBALLER-1].i_mainY);
                    ap_Sprites[CENTER_FOOTBALLER].setMainPointXY(ap_Sprites[CENTER_FOOTBALLER].i_mainX -= i_speed, ap_Sprites[CENTER_FOOTBALLER].i_mainY);
                    ap_Sprites[CENTER_FOOTBALLER+1].setMainPointXY(ap_Sprites[CENTER_FOOTBALLER+1].i_mainX -= i_speed, ap_Sprites[CENTER_FOOTBALLER+1].i_mainY);
                }
            }
            else if ((_buttonState & MoveObject.BUTTON_RIGHT) != 0)
            {
                if(i_mx < (I8_SCREEN_WIDTH - RIGHT_MARGIN))
                {
                    ap_Sprites[CENTER_FOOTBALLER-1].setMainPointXY(ap_Sprites[CENTER_FOOTBALLER-1].i_mainX += i_speed, ap_Sprites[CENTER_FOOTBALLER-1].i_mainY);
                    ap_Sprites[CENTER_FOOTBALLER].setMainPointXY(ap_Sprites[CENTER_FOOTBALLER].i_mainX += i_speed, ap_Sprites[CENTER_FOOTBALLER].i_mainY);
                    ap_Sprites[CENTER_FOOTBALLER+1].setMainPointXY(ap_Sprites[CENTER_FOOTBALLER+1].i_mainX += i_speed, ap_Sprites[CENTER_FOOTBALLER+1].i_mainY);
                }
            }
        }

        switch(state)
        {
            case SPRITE_FOOTBALLER:
                if ((_buttonState & MoveObject.BUTTON_FIRE) != 0)
                {
                    activateSprite(ap_Sprites[CENTER_FOOTBALLER-1], SPRITE_FOOTBALLER_STRIKE);
                    activateSprite(ap_Sprites[CENTER_FOOTBALLER], SPRITE_FOOTBALLER_STRIKE);
                    activateSprite(ap_Sprites[CENTER_FOOTBALLER+1], SPRITE_FOOTBALLER_STRIKE);
                    m_pAbstractGameActionListener.processGameAction(GAMEACTION_PLAYER_STRIKE);
                }
                break;
            case SPRITE_FOOTBALLER_ENEMY:
                if ((_buttonState & MoveObject.BUTTON_FIRE) != 0)
                {
                    activateSprite(ap_Sprites[CENTER_FOOTBALLER-1], SPRITE_FOOTBALLER_ENEMY_STRIKE);
                    activateSprite(ap_Sprites[CENTER_FOOTBALLER], SPRITE_FOOTBALLER_ENEMY_STRIKE);
                    activateSprite(ap_Sprites[CENTER_FOOTBALLER+1], SPRITE_FOOTBALLER_ENEMY_STRIKE);
                    m_pAbstractGameActionListener.processGameAction(GAMEACTION_ENEMY_STRIKE);
                }
                break;
        }
        return true;
    }

    private void initSprites()
    {
        int i = 0;
        int x = FOOTBALLER_X;
        int y = FOOTBALLER_Y1;

        while(i<3)
        {
            activateSprite(ap_Sprites[i], SPRITE_FOOTBALLER_ENEMY);
            ap_Sprites[i].setMainPointXY(x,y);
            x += FOOTBALLER_SPACE;
            i++;
        }

        x = FOOTBALLER_X;
        y = FOOTBALLER_Y2;

        while(i<6)
        {
            activateSprite(ap_Sprites[i], SPRITE_FOOTBALLER);
            ap_Sprites[i].setMainPointXY(x,y);
            x += FOOTBALLER_SPACE;
            i++;
        }
        activateSprite(ap_Sprites[i], SPRITE_GATE);
        ap_Sprites[i].setMainPointXY(I8_SCREEN_WIDTH / 2, I8_SCREEN_HEIGHT );
        i++;
        activateSprite(ap_Sprites[i], SPRITE_GATE_ENEMY);
        ap_Sprites[i].setMainPointXY(I8_SCREEN_WIDTH / 2, 0 );
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

        i_HSpeed = INIT_HSPEED;
        i_VSpeed = INIT_VSPEED;

        switch(getRandomInt(5))
        {
            default:
            case 0:
                i_HSpeed = INIT_HSPEED;
                i_VSpeed = INIT_VSPEED;
                break;
            case 1:
                i_HSpeed = -INIT_HSPEED;
                i_VSpeed = INIT_VSPEED;
                break;
            case 2:
                i_HSpeed = -INIT_HSPEED;
                i_VSpeed = -INIT_VSPEED;
                break;
            case 3:
                i_HSpeed = INIT_HSPEED;
                i_VSpeed = -INIT_VSPEED;
                break;
            case 4:
                i_HSpeed = 0;
                i_VSpeed = -INIT_VSPEED;
                break;
        }

        b_TouchWall = false;
        b_TouchFootballer = false;
        i_Goal = 0;
        i_Balls1 = 0;
        i_Balls2 = 0;

        activateSprite(pPlayerSprite, SPRITE_BALL);
        pPlayerSprite.setMainPointXY(I8_SCREEN_WIDTH / 2, I8_SCREEN_HEIGHT / 2);

        initSprites();

        return true;
    }

    public void resumeGameAfterPlayerLostOrPaused()
    {
        super.resumeGameAfterPlayerLostOrPaused();

        deinitState();
        initState();

        i_GameTimer = 0;
        i_LastTime = i_GameTimer;

        i_HSpeed = INIT_HSPEED;
        i_VSpeed = INIT_VSPEED;

        switch(getRandomInt(5))
        {
            default:
            case 0:
                i_HSpeed = INIT_HSPEED;
                i_VSpeed = INIT_VSPEED;
                break;
            case 1:
                i_HSpeed = -INIT_HSPEED;
                i_VSpeed = INIT_VSPEED;
                break;
            case 2:
                i_HSpeed = -INIT_HSPEED;
                i_VSpeed = -INIT_VSPEED;
                break;
            case 3:
                i_HSpeed = INIT_HSPEED;
                i_VSpeed = -INIT_VSPEED;
                break;
            case 4:
                i_HSpeed = 0;
                i_VSpeed = INIT_VSPEED;
                break;
            case 5:
                i_HSpeed = 0;
                i_VSpeed = -INIT_VSPEED;
                break;
        }

        b_TouchWall = false;
        b_TouchFootballer = false;
        i_Goal = 0;

        activateSprite(pPlayerSprite, SPRITE_BALL);
        pPlayerSprite.setMainPointXY(I8_SCREEN_WIDTH / 2, I8_SCREEN_HEIGHT / 2);

        initSprites();

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
        return "SOCCER";
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
        return (MAX_SPRITES_NUMBER+1)*(Sprite.DATASIZE_BYTES+1)+38;
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
        _outputStream.writeInt(i_HSpeed);
        _outputStream.writeInt(i_VSpeed);

        _outputStream.writeInt(i_Goal);
        _outputStream.writeInt(i_Balls1);
        _outputStream.writeInt(i_Balls2);

        _outputStream.writeBoolean(b_TouchWall);
        _outputStream.writeBoolean(b_TouchFootballer);

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
        i_HSpeed = _inputStream.readInt();
        i_VSpeed = _inputStream.readInt();

        i_Goal = _inputStream.readInt();
        i_Balls1 = _inputStream.readInt();
        i_Balls2 = _inputStream.readInt();
        b_TouchWall = _inputStream.readBoolean();
        b_TouchFootballer = _inputStream.readBoolean();
    }

    public int nextGameStep()
    {
        CAbstractPlayer p_player = m_pPlayerList[0];
        p_player.nextPlayerMove(this, p_MoveObject);


        int EnemyButton = AI_Thinking();

        processFootballer(EnemyButton, false);

        processFootballer(p_MoveObject.i_buttonState, true);

        processSprites();
        processBall();

        switch(i_Goal)
        {
            case 1:
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
                break;
            case 2:
                setGameState(GAMEWORLDSTATE_USERDEFINED);
                break;
            default:
                break;
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

    private int AI_Thinking()
    {
        boolean bottom = false;
        int button = 0;

        int CENTER_FOOTBALLER = 1;
        if(bottom) CENTER_FOOTBALLER = 1+3;

        int ball_x = pPlayerSprite.i_mainX;
        int ball_y = pPlayerSprite.i_mainY;

        int i_speed = FOOTBALLER_SPEED;

        int i_mx = ap_Sprites[CENTER_FOOTBALLER].i_mainX;
        int i_my = ap_Sprites[CENTER_FOOTBALLER].i_mainY;

        int i_w = ap_Sprites[CENTER_FOOTBALLER].i_width;
        int i_h = ap_Sprites[CENTER_FOOTBALLER].i_height;

        int i_r = i_w / 2;

        int state = ap_Sprites[CENTER_FOOTBALLER].i_ObjectType;

        int dx = i_mx - ball_x;
        int dy = i_my - ball_y;

        int adx = Math.abs(dx);
        int ady = Math.abs(dy);


        if(dx < 0)
        {
            if(adx > (FOOTBALLER_SPACE / 2))
            {
                button = MoveObject.BUTTON_LEFT;
            }
            else
            {
                button = MoveObject.BUTTON_RIGHT;
            }
        }
        else
        {
            if(adx > (FOOTBALLER_SPACE / 2))
            {
                button = MoveObject.BUTTON_RIGHT;
            }
            else
            {
                button = MoveObject.BUTTON_LEFT;
            }
        }
        if((ball_x > (i_mx - i_r)) && (ball_x < (i_mx + i_r))) button = MoveObject.BUTTON_NONE;
        if((ball_x > (i_mx - i_r - FOOTBALLER_SPACE)) && (ball_x < (i_mx + i_r - FOOTBALLER_SPACE))) button = MoveObject.BUTTON_NONE;
        if((ball_x > (i_mx - i_r + FOOTBALLER_SPACE)) && (ball_x < (i_mx + i_r + FOOTBALLER_SPACE))) button = MoveObject.BUTTON_NONE;

        if(getRandomInt(8) > 4)
        {
            if(dy < 0)
            {
                switch(button)
                {
                    case MoveObject.BUTTON_LEFT:
                        button = MoveObject.BUTTON_RIGHT;
                        break;
                    case MoveObject.BUTTON_RIGHT:
                        button = MoveObject.BUTTON_LEFT;
                        break;
                }
            }
        }


        if(dy < 0)
        {
            if((ady < i_w) && button == 0)
            {
                button = MoveObject.BUTTON_FIRE;
            }
        }

        return button;
    }

    public int getGameStepDelay()
    {
        return i_timedelay;
    }
}
