
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.DataInputStream;

public class MobileGameletImpl extends MobileGamelet
{
    public static final int TURTLE_X = (22 << 8);
    public static final int TURTLE_Y = (73 << 8);

    public static final int TURTLE_SIZE = (20 << 8);

    public static final int TURTLE_X_1 = (TURTLE_X + TURTLE_SIZE * 0);
    public static final int TURTLE_X_2 = (TURTLE_X + TURTLE_SIZE * 1);
    public static final int TURTLE_X_3 = (TURTLE_X + TURTLE_SIZE * 2);
    public static final int TURTLE_X_4 = (TURTLE_X + TURTLE_SIZE * 3);
    public static final int TURTLE_X_5 = (TURTLE_X + TURTLE_SIZE * 4);

    public static final int TURTLE_DIVE_Y = (TURTLE_Y + (16 << 8));

    public static final int PLAYER_X = (TURTLE_X_1);
    public static final int PLAYER_Y = (TURTLE_Y - 0x0C00); 

    public static final int LOADER_X = (TURTLE_X_1 - 0x1000);
    public static final int LOADER_Y = (PLAYER_Y);

    public static final int UNLOADER_X = (TURTLE_X_5 + 0x1000);
    public static final int UNLOADER_Y = (PLAYER_Y);

    public static final int FISH_SPEED = (0x0080);
    public static final int TURTLE_SPEED = (0x0100);
    public static final int HSPEED = (0x0240);
    public static final int VSPEED = -(0x0200);

    public static final int DEAD_SPEED = (2 << 8);

    public static final int EAT_DISTANCE = (16 << 8);

    private static int I8_SCREEN_WIDTH;

    private static int I8_SCREEN_HEIGHT;

    public MoveObject p_MoveObject;

    public static final int GAMEACTION_JUMP                 = 0;
    public static final int GAMEACTION_JUMPED               = 1;
    public static final int GAMEACTION_LOAD                 = 2;
    public static final int GAMEACTION_UNLOAD               = 3;

    public static final int GAMEACTION_DEAD                 = 4;
    public static final int GAMEACTION_TURTLE_TOUCH         = 5;
    public static final int GAMEACTION_TURTLE_ON_THE_WATER  = 6;
    public static final int GAMEACTION_EAT_FISH             = 7;
    public static final int GAMEACTION_FISH_GENERATED       = 8;

    private int i_timedelay;

    public int i_playerAttemptions;

    public int i_GameTimer;

    public int i_LastTime;
    public int i_GenerateDelay;

    public int i_VSpeed;

    public static final int LEVEL_EASY = 0;

    private static final int LEVEL_EASY_ATTEMPTIONS = 4;

    private static final int LEVEL_EASY_GENERATION = 20;

    private static final int LEVEL_EASY_TIMEDELAY = 130;

    public static final int LEVEL_NORMAL = 1;

    private static final int LEVEL_NORMAL_ATTEMPTIONS = 3;

    private static final int LEVEL_NORMAL_GENERATION = 15;

    private static final int LEVEL_NORMAL_TIMEDELAY = 100;

    public static final int LEVEL_HARD = 2;

    private static final int LEVEL_HARD_ATTEMPTIONS = 2;

    private static final int LEVEL_HARD_GENERATION = 10;

    private static final int LEVEL_HARD_TIMEDELAY = 70;

    private static final int SCORES_CARRY = 10;

    public static final int MAX_SPRITES_NUMBER = 12;

    public Sprite pPlayerSprite;

    public Sprite pHelper;

    public Sprite[] ap_Sprites;

    private static final int SPRITEDATALENGTH = 10;

    public static final int SPRITE_CARRIER = 0;

    public static final int SPRITE_CARRIER_JUMPLEFT = 1;

    public static final int SPRITE_CARRIER_JUMPRIGHT = 2;

    public static final int SPRITE_CARRIER_LOAD = 3;

    public static final int SPRITE_CARRIER_UNLOAD = 4;

    public static final int SPRITE_CARRIER_DEAD = 5;

    public static final int SPRITE_LOADER = 6;

    public static final int SPRITE_UNLOADER = 7;

    public static final int SPRITE_TURTLE = 8;

    public static final int SPRITE_TURTLE_TOUCHED = 9;

    public static final int SPRITE_TURTLE_DIVE = 10;

    public static final int SPRITE_FISH = 11;


    private static final int [] ai_SpriteParameters = new int[]
    {
        0x0C00, 0x1100, 0x0000, 0x0000, 0x0C00, 0x0F00, 1, 1, Sprite.SPRITE_ALIGN_CENTER, Sprite.ANIMATION_FROZEN,
        0x0C00, 0x1100, 0x0000, 0x0000, 0x0C00, 0x1500, 2, 1, Sprite.SPRITE_ALIGN_CENTER, Sprite.ANIMATION_FROZEN,
        0x0C00, 0x1100, 0x0000, 0x0000, 0x0C00, 0x1500, 2, 1, Sprite.SPRITE_ALIGN_CENTER, Sprite.ANIMATION_FROZEN,
        0x0C00, 0x1100, 0x0000, 0x0000, 0x0C00, 0x0900, 3, 2, Sprite.SPRITE_ALIGN_CENTER, Sprite.ANIMATION_FROZEN,
        0x0C00, 0x1100, 0x0000, 0x0000, 0x0C00, 0x0900, 3, 2, Sprite.SPRITE_ALIGN_CENTER, Sprite.ANIMATION_FROZEN,
        0x0C00, 0x1100, 0x0000, 0x0000, 0x0C00, 0x0200, 5, 2, Sprite.SPRITE_ALIGN_CENTER, Sprite.ANIMATION_FROZEN,
        0x0A00, 0x0F00, 0x0000, 0x0000, 0x0A00, 0x0F00, 3, 2, Sprite.SPRITE_ALIGN_CENTER, Sprite.ANIMATION_FROZEN,
        0x0A00, 0x0F00, 0x0000, 0x0000, 0x0A00, 0x0F00, 3, 2, Sprite.SPRITE_ALIGN_CENTER, Sprite.ANIMATION_FROZEN,
        0x0E00, 0x0700, 0x0000, 0x0000, 0x0E00, 0x0700, 1, 1, Sprite.SPRITE_ALIGN_CENTER, Sprite.ANIMATION_FROZEN,
        0x0E00, 0x0700, 0x0000, 0x0000, 0x0E00, 0x0700, 1, 1, Sprite.SPRITE_ALIGN_CENTER, Sprite.ANIMATION_FROZEN,
        0x0E00, 0x0700, 0x0000, 0x0000, 0x0E00, 0x0700, 2, 1, Sprite.SPRITE_ALIGN_CENTER, Sprite.ANIMATION_FROZEN,
        0x0600, 0x0300, 0x0000, 0x0000, 0x0400, 0x0300, 1, 1, Sprite.SPRITE_ALIGN_CENTER, Sprite.ANIMATION_FROZEN,
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
                    case SPRITE_TURTLE_TOUCHED:
                        activateSprite(p_Sprite, SPRITE_TURTLE);
                        p_Sprite.setMainPointXY(p_Sprite.i_mainX, p_Sprite.i_mainY - 0x0100);
                        pPlayerSprite.setMainPointXY(pPlayerSprite.i_mainX, pPlayerSprite.i_mainY - 0x0100);
                        break;
                }
            }
            switch (p_Sprite.i_ObjectType)
            {
                case SPRITE_FISH:
                    p_Sprite.setMainPointXY(p_Sprite.i_mainX, p_Sprite.i_mainY - FISH_SPEED);
                    for (int ni = 0; ni < MAX_SPRITES_NUMBER; ni++)
                    {
                        Sprite p_Spr = ap_Sprites[ni];
                        if (!p_Spr.lg_SpriteActive) continue;
                        switch (p_Spr.i_ObjectType)
                        {
                            case SPRITE_TURTLE_DIVE:
                                if(p_Sprite.isCollided(p_Spr))
                                {
                                    deactivateSprite(p_Sprite);
                                    p_Spr.i_ObjectState = SPRITE_TURTLE;
                                    m_pAbstractGameActionListener.processGameAction(GAMEACTION_EAT_FISH);
                                }
                                else
                                {
                                    if((p_Sprite.i_mainX == p_Spr.i_mainX) && ((p_Sprite.i_mainY < (p_Spr.i_mainY + EAT_DISTANCE))) )
                                    {
                                        p_Spr.i_ObjectState = SPRITE_TURTLE_DIVE;
                                    }
                                }
                                break;
                            case SPRITE_TURTLE:
                                if(p_Sprite.isCollided(p_Spr))
                                {
                                    deactivateSprite(p_Sprite);
                                    p_Spr.i_ObjectState = SPRITE_TURTLE;
                                }
                                else
                                {
                                    if((p_Sprite.i_mainX == p_Spr.i_mainX) && ((p_Sprite.i_mainY < (p_Spr.i_mainY + EAT_DISTANCE))) )
                                    {
                                        activateSprite(p_Spr, SPRITE_TURTLE_DIVE);
                                        p_Spr.i_ObjectState = SPRITE_TURTLE_DIVE;
                                        if( (pPlayerSprite.i_mainX > (p_Spr.i_mainX - p_Spr.i_width / 2)) && (pPlayerSprite.i_mainX < (p_Spr.i_mainX + p_Spr.i_width / 2))  )
                                        {
                                            switch(pPlayerSprite.i_ObjectType)
                                            {
                                                case SPRITE_CARRIER_DEAD:
                                                    break;
                                                default:
                                                    if(pPlayerSprite.i_mainY >= PLAYER_Y)
                                                    {
                                                        activateSprite(pPlayerSprite, SPRITE_CARRIER_DEAD);
                                                        m_pAbstractGameActionListener.processGameAction(GAMEACTION_DEAD);
                                                    }
                                                    break;
                                            }
                                        }
                                    }
                                }
                                break;
                        }
                    }
                    break;
                case SPRITE_TURTLE:
                    if(p_Sprite.isCollided(pPlayerSprite) && i_VSpeed > 0)
                    {
                        m_pAbstractGameActionListener.processGameAction(GAMEACTION_TURTLE_TOUCH);
                        activateSprite(p_Sprite, SPRITE_TURTLE_TOUCHED);
                        p_Sprite.setMainPointXY(p_Sprite.i_mainX, p_Sprite.i_mainY + 0x0100);
                        pPlayerSprite.setMainPointXY(pPlayerSprite.i_mainX, pPlayerSprite.i_mainY + 0x0100);
                    }
                    break;
                case SPRITE_TURTLE_DIVE:
                    if(p_Sprite.i_ObjectState == SPRITE_TURTLE_DIVE)
                    {
                        if(p_Sprite.i_mainY < TURTLE_DIVE_Y)
                        {
                            p_Sprite.setMainPointXY(p_Sprite.i_mainX, p_Sprite.i_mainY + TURTLE_SPEED);
                        }
                    }
                    else
                    {
                        if(p_Sprite.i_mainY > TURTLE_Y)
                        {
                            p_Sprite.setMainPointXY(p_Sprite.i_mainX, p_Sprite.i_mainY - TURTLE_SPEED);
                        }
                        else
                        {
                            activateSprite(p_Sprite, SPRITE_TURTLE);
                            m_pAbstractGameActionListener.processGameAction(GAMEACTION_TURTLE_ON_THE_WATER);
                        }
                    }
                    break;
                case SPRITE_TURTLE_TOUCHED:
                    break;
            }
        }
    }

    private void processHelper()
    {
        if(pHelper.lg_SpriteActive)
        {
            if(pHelper.processAnimation())
            {
                deactivateSprite(pHelper);
            }
        }
        else
        {
            if(pPlayerSprite.i_ObjectState == 0)
            {
                if(pPlayerSprite.i_mainX <= TURTLE_X_1)
                {
                    if(pPlayerSprite.i_ObjectType == SPRITE_CARRIER)
                    {
                        activateSprite(pHelper, SPRITE_LOADER);
                        pHelper.setMainPointXY(LOADER_X, LOADER_Y);
                        activateSprite(pPlayerSprite, SPRITE_CARRIER_LOAD);
                        pPlayerSprite.i_ObjectState = 1;
                        m_pAbstractGameActionListener.processGameAction(GAMEACTION_LOAD);
                    }
                }
            }
            else
            {
                if(pPlayerSprite.i_mainX >= TURTLE_X_5)
                {
                    if(pPlayerSprite.i_ObjectType == SPRITE_CARRIER)
                    {
                        activateSprite(pHelper, SPRITE_UNLOADER);
                        pHelper.setMainPointXY(UNLOADER_X, UNLOADER_Y);
                        activateSprite(pPlayerSprite, SPRITE_CARRIER_UNLOAD);
                        pPlayerSprite.i_ObjectState = 0;
                        addScore(SCORES_CARRY);
                        m_pAbstractGameActionListener.processGameAction(GAMEACTION_UNLOAD);
                    }
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
                case SPRITE_CARRIER_LOAD:
                    activateSprite(pPlayerSprite, SPRITE_CARRIER);
                    break;
                case SPRITE_CARRIER_UNLOAD:
                    activateSprite(pPlayerSprite, SPRITE_CARRIER);
                    break;
                case SPRITE_CARRIER_DEAD:
                    return false;
            }
        }
        switch(pPlayerSprite.i_ObjectType)
        {
            case SPRITE_CARRIER:
                if(pPlayerSprite.i_mainY >= PLAYER_Y)
                {
                    boolean death = true;

                    for (int li = 0; li < MAX_SPRITES_NUMBER; li++)
                    {
                        Sprite p_Sprite = ap_Sprites[li];
                        if (!p_Sprite.lg_SpriteActive) continue;
                        switch (p_Sprite.i_ObjectType)
                        {
                            case SPRITE_TURTLE:
                            case SPRITE_TURTLE_TOUCHED:
                                if( (pPlayerSprite.i_mainX > (p_Sprite.i_mainX - p_Sprite.i_width / 2)) && (pPlayerSprite.i_mainX < (p_Sprite.i_mainX + p_Sprite.i_width / 2))  )
                                {
                                    death = false;
                                }
                                break;
                        }
                        if(!death) break;
                    }

                    if(death)
                    {
                        activateSprite(pPlayerSprite, SPRITE_CARRIER_DEAD);
                        m_pAbstractGameActionListener.processGameAction(GAMEACTION_DEAD);
                        return true;
                    }
                }
                if ((_buttonState & MoveObject.BUTTON_LEFT) != 0)
                {
                    if(i_mx > (TURTLE_X_1))
                    {
                        m_pAbstractGameActionListener.processGameAction(GAMEACTION_JUMP);
                        activateSprite(pPlayerSprite, SPRITE_CARRIER_JUMPLEFT);
                        i_VSpeed = VSPEED;
                    }
                }
                else if ((_buttonState & MoveObject.BUTTON_RIGHT) != 0)
                {
                    if(i_mx < (TURTLE_X_5))
                    {
                        m_pAbstractGameActionListener.processGameAction(GAMEACTION_JUMP);
                        activateSprite(pPlayerSprite, SPRITE_CARRIER_JUMPRIGHT);
                        i_VSpeed = VSPEED;
                    }
                }
                pPlayerSprite.setMainPointXY(i_mx, i_my);
                break;
            case SPRITE_CARRIER_JUMPLEFT:
                pPlayerSprite.setMainPointXY(i_mx - HSPEED, i_my + i_VSpeed);
                i_VSpeed += 0x080;
                if(pPlayerSprite.i_mainY >= PLAYER_Y)
                {
                    boolean death = true;

                    for (int li = 0; li < MAX_SPRITES_NUMBER; li++)
                    {
                        Sprite p_Sprite = ap_Sprites[li];
                        if (!p_Sprite.lg_SpriteActive) continue;
                        switch (p_Sprite.i_ObjectType)
                        {
                            case SPRITE_TURTLE:
                            case SPRITE_TURTLE_TOUCHED:
                                if( (pPlayerSprite.i_mainX > (p_Sprite.i_mainX - p_Sprite.i_width / 2)) && (pPlayerSprite.i_mainX < (p_Sprite.i_mainX + p_Sprite.i_width / 2))  )
                                {
                                    death = false;
                                }
                                break;
                        }
                    }

                    if(death)
                    {
                        activateSprite(pPlayerSprite, SPRITE_CARRIER_DEAD);
                        m_pAbstractGameActionListener.processGameAction(GAMEACTION_DEAD);
                    }
                    else
                    {
                        activateSprite(pPlayerSprite, SPRITE_CARRIER);
                        m_pAbstractGameActionListener.processGameAction(GAMEACTION_JUMPED);
                    }
                }
                break;
            case SPRITE_CARRIER_JUMPRIGHT:
                pPlayerSprite.setMainPointXY(i_mx + HSPEED, i_my + i_VSpeed);
                i_VSpeed += 0x080;
                if(pPlayerSprite.i_mainY >= PLAYER_Y)
                {
                    boolean death = true;

                    for (int li = 0; li < MAX_SPRITES_NUMBER; li++)
                    {
                        Sprite p_Sprite = ap_Sprites[li];
                        if (!p_Sprite.lg_SpriteActive) continue;
                        switch (p_Sprite.i_ObjectType)
                        {
                            case SPRITE_TURTLE:
                            case SPRITE_TURTLE_TOUCHED:
                                if( (pPlayerSprite.i_mainX > (p_Sprite.i_mainX - p_Sprite.i_width / 2)) && (pPlayerSprite.i_mainX < (p_Sprite.i_mainX + p_Sprite.i_width / 2))  )
                                {
                                    death = false;
                                }
                                break;
                        }
                    }

                    if(death)
                    {
                        activateSprite(pPlayerSprite, SPRITE_CARRIER_DEAD);
                        m_pAbstractGameActionListener.processGameAction(GAMEACTION_DEAD);
                    }
                    else
                    {
                        activateSprite(pPlayerSprite, SPRITE_CARRIER);
                        m_pAbstractGameActionListener.processGameAction(GAMEACTION_JUMPED);
                    }
                }
                break;
            case SPRITE_CARRIER_DEAD:
                pPlayerSprite.setMainPointXY(i_mx, i_my + DEAD_SPEED);
                break;
        }
        return true;
    }



    private void generateSprite()
    {
        if((i_LastTime + i_GenerateDelay) < i_GameTimer )
        {
            i_LastTime = i_GameTimer;
            Sprite p_emptySprite = getInactiveSprite();
            if (p_emptySprite != null)
            {
                int y = I8_SCREEN_HEIGHT;
                int x = I8_SCREEN_WIDTH / 2;
                int type = SPRITE_FISH;

                switch(getRandomInt(5))
                {
                    case 0:
                        x = TURTLE_X_1;
                        break;
                    case 1:
                        x = TURTLE_X_2;
                        break;
                    default:
                    case 2:
                        x = TURTLE_X_3;
                        break;
                    case 3:
                        x = TURTLE_X_4;
                        break;
                    case 4:
                        x = TURTLE_X_5;
                        break;
                }

                activateSprite(p_emptySprite, type);
                p_emptySprite.lg_SpriteActive = true;
                p_emptySprite.setMainPointXY(x, y);
                m_pAbstractGameActionListener.processGameAction(GAMEACTION_FISH_GENERATED);
            }
        }
    }


    private void generateTurtles()
    {
        int y = TURTLE_Y;
        int x = TURTLE_X;
        int type = SPRITE_TURTLE;

        for(int i=0; i<5; i++)
        {
            Sprite p_emptySprite = getInactiveSprite();
            if (p_emptySprite != null)
            {
                switch(i)
                {
                    case 0:
                        x = TURTLE_X_1;
                        break;
                    case 1:
                        x = TURTLE_X_2;
                        break;
                    case 2:
                        x = TURTLE_X_3;
                        break;
                    case 3:
                        x = TURTLE_X_4;
                        break;
                    case 4:
                        x = TURTLE_X_5;
                        break;
                }

                activateSprite(p_emptySprite, type);
                p_emptySprite.lg_SpriteActive = true;
                p_emptySprite.setMainPointXY(x, y);
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

        activateSprite(pPlayerSprite, SPRITE_CARRIER);
        pPlayerSprite.setMainPointXY(PLAYER_X, PLAYER_Y);

        generateTurtles();

        return true;
    }

    public void resumeGameAfterPlayerLostOrPaused()
    {
        super.resumeGameAfterPlayerLostOrPaused();

        deinitState();
        initState();

        i_GameTimer = 0;
        i_LastTime = i_GameTimer;

        activateSprite(pPlayerSprite, SPRITE_CARRIER);
        pPlayerSprite.setMainPointXY(PLAYER_X, PLAYER_Y);

        generateTurtles();

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
        return "CARRIER";
    }

    public boolean initState()
    {
        ap_Sprites = new Sprite[MAX_SPRITES_NUMBER];
        for (int li = 0; li < MAX_SPRITES_NUMBER; li++) ap_Sprites[li] = new Sprite(li);


        pPlayerSprite = new Sprite(-1);
        pHelper = new Sprite(-2);

        p_MoveObject = new MoveObject();

        return true;
    }

    public void deinitState()
    {

        ap_Sprites = null;
        pPlayerSprite = null;
        pHelper = null;

        Runtime.getRuntime().gc();
    }

    public int _getMaximumDataSize()
    {
        return (MAX_SPRITES_NUMBER+2)*(Sprite.DATASIZE_BYTES+1)+20;
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

        _outputStream.writeByte(pHelper.i_ObjectType);
        pHelper.writeSpriteToStream(_outputStream);

        _outputStream.writeInt(i_playerAttemptions);
        _outputStream.writeInt(i_GameTimer);
        _outputStream.writeInt(i_LastTime);
        _outputStream.writeInt(i_GenerateDelay);
        _outputStream.writeInt(i_VSpeed);

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
        activateSprite(pHelper,i_type);
        pHelper.readSpriteFromStream(_inputStream);

        i_playerAttemptions = _inputStream.readInt();
        i_GameTimer = _inputStream.readInt();
        i_LastTime = _inputStream.readInt();
        i_GenerateDelay = _inputStream.readInt();
        i_VSpeed = _inputStream.readInt();
    }

    public int nextGameStep()
    {
        CAbstractPlayer p_player = m_pPlayerList[0];
        p_player.nextPlayerMove(this, p_MoveObject);

        processSprites();
        processHelper();

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

        generateSprite();



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
