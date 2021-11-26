
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.DataInputStream;

public class MobileGameletImpl extends MobileGamelet
{



    private static final int STATE_NONE = 0;

    private static final int STATE_RAVEN_TOP = 1;

    private static final int STATE_RAVEN_DOWN = 2;

    private static final int STATE_CHEESE_FALLEN_LEFT = 3;

    private static final int STATE_CHEESE_FALLEN_RIGHT = 4;

    private static final int STATE_CHEESE_DROPPED_LEFT = 5;

    private static final int STATE_CHEESE_DROPPED_RIGHT = 6;

    public static final int STATE_CHEESE_CATCHED = 7;


    private static final int FOX_UP_DELAY = 20;

    public static final int LEVEL_EASY = 0;

    private static final int LEVEL_EASY_TIMEDELAY = 100;

    private static final int LEVEL_EASY_START_NEXT_RAVEN_DELAY = 30;

    private static final int LEVEL_EASY_END_NEXT_RAVEN_DELAY = 10;


    public static final int LEVEL_NORMAL = 1;

    private static final int LEVEL_NORMAL_TIMEDELAY = 80;

    private static final int LEVEL_NORNAL_START_NEXT_RAVEN_DELAY = 25;

    private static final int LEVEL_NORMAL_END_NEXT_RAVEN_DELAY = 10;

    public static final int LEVEL_HARD = 2;

    private static final int LEVEL_HARD_TIMEDELAY = 50;

    private static final int LEVEL_HARD_START_NEXT_RAVEN_DELAY = 20;

    private static final int LEVEL_HARD_END_NEXT_RAVEN_DELAY = 5;

    private int i_timedelay;

    private int i_levelStartNextRavenDelay;

    private int i_levelEndNextRavenDelay;

    private int i_generatedCheeses;

    private int i_catchenCheeses;

    private int i_nextRavenDelay;

    private int i_foxUpDelay;

    private int i_busyRavens;

    private int i_lastGeneratedRaven;

    public MoveObject p_MoveObject;

    private boolean lg_foxCatchingCheese;

    private static final int SPEED_CHANGE_SCORES_STEP = 50;

    private static final int REMOVE_FALLEN_CHEESES = 50;

    private static final int WINNER_CHEESES = 100;


    private static final int PATH_RAVEN_LEFTTOP = 0;

    private static final int PATH_RAVEN_RIGHTTOP = 7;

    private static final int PATH_RAVEN_LEFTDOWN = 14;

    private static final int PATH_RAVEN_RIGHTDOWN = 21;

    private static final int PATH_RAVEN_BACK_LEFTTOP = 28;

    private static final int PATH_RAVEN_BACK_RIGHTTOP = 35;

    private static final int PATH_RAVEN_BACK_LEFTDOWN = 42;

    private static final int PATH_RAVEN_BACK_RIGHTDOWN = 49;

    private static final int PATH_MOUSE_EMPTY_LEFT = 56;

    private static final int PATH_MOUSE_EMPTY_RIGHT = 63;

    private static final int PATH_MOUSE_CHEESE_LEFT = 70;

    private static final int PATH_MOUSE_CHEESE_RIGHT = 77;

    private static final int PATH_CHEESE_FALL_TOP_LEFT = 84;

    private static final int PATH_CHEESE_FALL_TOP_RIGHT = 91;

    private static final int PATH_CHEESE_FALL_DOWN_LEFT = 98;

    private static final int PATH_CHEESE_FALL_DOWN_RIGHT = 105;

    private static final int PATH_CHEESE_CATCHED_TOP_LEFT = 112;

    private static final int PATH_CHEESE_CATCHED_TOP_RIGHT = 119;

    private static final int PATH_CHEESE_CATCHED_DOWN_LEFT = 126;

    private static final int PATH_CHEESE_CATCHED_DOWN_RIGHT = 133;

    public static final int MAX_SPRITES = 10;

    private static final int MAX_PATHS = 10;

    private static final int PATH_CENTERX = 0;

    private static final int PATH_CENTERY = 0;

    private static final int FOX_DOWN_POINT = PATH_CENTERY + 102;

    public static final int GAMEACTION_RAVEN_CROAKING = 0;

    public static final int GAMEACTION_MOUSE_GENERATED = 1;

    public static final int GAMEACTION_MOUSE_HIDDEN = 2;

    public static final int GAMEACTION_CHEESE_TAKEN_BY_MOUSE = 3;

    public static final int GAMEACTION_CHEESE_TAKEN_BY_FOX = 4;

    public static final int GAMEACTION_CHEESE_FALLEN = 5;

    public static final int GAMEACTION_RAVEN_GENERATED = 6;

    public static final int GAMEACTION_RAVEN_HIDDEN = 7;

    public static final int GAMEACTION_ATTEMPTION_INCREASED = 8;

    private static short[] ash_paths = new short[]
    {
        1, 0, 2, 10, 10, 22, 10,

        1, 0, 124, 16, 10, 105, 16,

        1, 0, 2, 51, 10, 22, 51,

        1, 0, 124, 51, 10, 105, 51,


        1, 0, 22, 10, 5, 2, 10,

        1, 0, 105, 16, 5, 124, 16,

        1, 0, 22, 51, 5, 2, 51,

        1, 0, 105, 51, 5, 124, 51,


        1, 0, 4, 107, 3, 16, 107,

        1, 0, 120, 107, 3, 108, 107,

        1, 0, 16, 107, 6, 4, 107,

        1, 0, 108, 107, 6, 120, 107,


        1, 0, 26, 18, 4, 28, 104,

        1, 0, 100, 18, 4, 98, 104,

        1, 0, 26, 61, 4, 28, 104,

        1, 0, 96, 61, 4, 98, 104,


        1, 0, 30, 22, 3, 48, 69,

        1, 0, 93, 22, 3, 86, 69,

        1, 0, 31, 61, 3, 39, 100,

        1, 0, 90, 61, 3, 92, 100
    };

    public static final int SPRITE_RAVEN_WITH_CHEESE_LEFT = 0;

    public static final int SPRITE_RAVEN_WITH_CHEESE_RIGHT = 1;

    public static final int SPRITE_RAVEN_EMPTY_LEFT = 2;

    public static final int SPRITE_RAVEN_EMPTY_RIGHT = 3;

    public static final int SPRITE_RAVEN_LEFT_CROAKING = 4;

    public static final int SPRITE_RAVEN_RIGHT_CROAKING = 5;

    public static final int SPRITE_MOUSE_EMPTY_LEFT = 6;

    public static final int SPRITE_MOUSE_EMPTY_RIGHT = 7;

    public static final int SPRITE_MOUSE_WITH_CHEESE_LEFT = 8;

    public static final int SPRITE_MOUSE_WITH_CHEESE_RIGHT = 9;

    public static final int SPRITE_DROPPED_CHEESE = 10;

    public static final int SPRITE_FOX_DOWN_LEFT = 11;

    public static final int SPRITE_FOX_DOWN_RIGHT = 12;

    public static final int SPRITE_FOX_TOP_LEFT = 13;

    public static final int SPRITE_FOX_TOP_RIGHT = 14;

    public static final int SPRITE_FOX_DOWN_OPEN_LEFT = 15;

    public static final int SPRITE_FOX_DOWN_OPEN_RIGHT = 16;

    public static final int SPRITE_FOX_TOP_OPEN_LEFT = 17;

    public static final int SPRITE_FOX_TOP_OPEN_RIGHT = 18;

    private static final int[] ai_SpriteParameters = new int[]
    {
        0x1C00, 0x1C00, 1, 0, Sprite.SPRITE_ALIGN_CENTER, Sprite.ANIMATION_FROZEN,
        0x1C00, 0x1C00, 1, 0, Sprite.SPRITE_ALIGN_CENTER, Sprite.ANIMATION_FROZEN,
        0x1C00, 0x1C00, 1, 0, Sprite.SPRITE_ALIGN_CENTER, Sprite.ANIMATION_FROZEN,
        0x1C00, 0x1C00, 1, 0, Sprite.SPRITE_ALIGN_CENTER, Sprite.ANIMATION_FROZEN,
        0x1C00, 0x1C00, 1, 3, Sprite.SPRITE_ALIGN_CENTER, Sprite.ANIMATION_FROZEN,
        0x1C00, 0x1C00, 1, 3, Sprite.SPRITE_ALIGN_CENTER, Sprite.ANIMATION_FROZEN,
        0x1C00, 0x1200, 1, 0, Sprite.SPRITE_ALIGN_CENTER, Sprite.ANIMATION_FROZEN,
        0x1C00, 0x1200, 1, 0, Sprite.SPRITE_ALIGN_CENTER, Sprite.ANIMATION_FROZEN,
        0x1C00, 0x1200, 1, 0, Sprite.SPRITE_ALIGN_CENTER, Sprite.ANIMATION_FROZEN,
        0x1C00, 0x1200, 1, 0, Sprite.SPRITE_ALIGN_CENTER, Sprite.ANIMATION_FROZEN,
        0x1200, 0x0A00, 1, 0, Sprite.SPRITE_ALIGN_CENTER, Sprite.ANIMATION_FROZEN,
        0x3500, 0x1C00, 1, 0, Sprite.SPRITE_ALIGN_CENTER | Sprite.SPRITE_ALIGN_DOWN, Sprite.ANIMATION_FROZEN,
        0x3500, 0x1C00, 1, 0, Sprite.SPRITE_ALIGN_CENTER | Sprite.SPRITE_ALIGN_DOWN, Sprite.ANIMATION_FROZEN,
        0x3500, 0x2500, 1, 0, Sprite.SPRITE_ALIGN_CENTER | Sprite.SPRITE_ALIGN_DOWN, Sprite.ANIMATION_FROZEN,
        0x3500, 0x2500, 1, 0, Sprite.SPRITE_ALIGN_CENTER | Sprite.SPRITE_ALIGN_DOWN, Sprite.ANIMATION_FROZEN,
        0x3500, 0x1C00, 1, 0, Sprite.SPRITE_ALIGN_CENTER | Sprite.SPRITE_ALIGN_DOWN, Sprite.ANIMATION_FROZEN,
        0x3500, 0x1C00, 1, 0, Sprite.SPRITE_ALIGN_CENTER | Sprite.SPRITE_ALIGN_DOWN, Sprite.ANIMATION_FROZEN,
        0x3500, 0x2500, 1, 0, Sprite.SPRITE_ALIGN_CENTER | Sprite.SPRITE_ALIGN_DOWN, Sprite.ANIMATION_FROZEN,
        0x3500, 0x2500, 1, 0, Sprite.SPRITE_ALIGN_CENTER | Sprite.SPRITE_ALIGN_DOWN, Sprite.ANIMATION_FROZEN
    };

    private static final int SPRITE_ARGS_NUMBER = 6;


    public static final int MAX_ATTEMPTIONS = 3;


    public int i_PlayerAttemptions;

    public Sprite[] ap_Sprites;

    private PathController[] ap_Paths;

    public Sprite p_foxSprite;

    private void activateSprite(Sprite _sprite, int _actorIndex)
    {
        _sprite.i_ObjectType = _actorIndex;
        _sprite.i_ObjectState = STATE_NONE;
        _actorIndex *= SPRITE_ARGS_NUMBER;
        int i_w = ai_SpriteParameters[_actorIndex++];
        int i_h = ai_SpriteParameters[_actorIndex++];
        int i_f = ai_SpriteParameters[_actorIndex++];
        int i_fd = ai_SpriteParameters[_actorIndex++];
        int i_mp = ai_SpriteParameters[_actorIndex++];
        int i_an = ai_SpriteParameters[_actorIndex++];

        _sprite.setAnimation(i_an, i_mp, i_w, i_h, i_f, 0, i_fd);
        _sprite.lg_SpriteActive = true;
    }

public boolean newGameSession(int _gameAreaWidth, int _gameAreaHeight, int _gameLevel, CAbstractPlayer[] _players, startup _listener, String _staticArrayResourceName)
    {
        super.newGameSession(_gameAreaWidth, _gameAreaHeight, _gameLevel, _players, _listener, _staticArrayResourceName);

        switch (_gameLevel)
        {
            case LEVEL_EASY:
                {
                    i_timedelay = LEVEL_EASY_TIMEDELAY;
                    i_levelStartNextRavenDelay = LEVEL_EASY_START_NEXT_RAVEN_DELAY;
                    i_levelEndNextRavenDelay = LEVEL_EASY_END_NEXT_RAVEN_DELAY;
                }
                ;
                break;
            case LEVEL_NORMAL:
                {
                    i_timedelay = LEVEL_NORMAL_TIMEDELAY;
                    i_levelStartNextRavenDelay = LEVEL_NORNAL_START_NEXT_RAVEN_DELAY;
                    i_levelEndNextRavenDelay = LEVEL_NORMAL_END_NEXT_RAVEN_DELAY;
                }
                ;
                break;
            case LEVEL_HARD:
                {
                    i_timedelay = LEVEL_HARD_TIMEDELAY;
                    i_levelStartNextRavenDelay = LEVEL_HARD_START_NEXT_RAVEN_DELAY;
                    i_levelEndNextRavenDelay = LEVEL_HARD_END_NEXT_RAVEN_DELAY;
                }
                ;
                break;
        }

        i_nextRavenDelay = calculateNewRavenDelay();

        return true;
    }

    public String getGameTextID()
    {
        return "RAVENCHEESE";
    }

    public boolean initState()
    {
        p_foxSprite = new Sprite(0);
        ap_Sprites = new Sprite[MAX_SPRITES];
        ap_Paths = new PathController[MAX_PATHS];

        for (int li = 0; li < MAX_SPRITES; li++) ap_Sprites[li] = new Sprite(li);
        for (int li = 0; li < MAX_PATHS; li++) ap_Paths[li] = new PathController();

        i_generatedCheeses = 0;

        activateSprite(p_foxSprite, SPRITE_FOX_DOWN_LEFT);
        p_foxSprite.setMainPointXY(m_iGameAreaWidth << 7, FOX_DOWN_POINT << 8);

        p_MoveObject = new MoveObject();

        i_PlayerAttemptions = MAX_ATTEMPTIONS;

        i_lastGeneratedRaven = 0;
        i_catchenCheeses = 0;
        i_busyRavens = 0;

        return true;
    }

    public void deinitState()
    {
        ap_Paths = null;
        ap_Sprites = null;
        p_foxSprite = null;
        p_MoveObject = null;

        Runtime.getRuntime().gc();
    }

    public int _getMaximumDataSize()
    {
        return (MAX_SPRITES+1)*(Sprite.DATASIZE_BYTES+1)+MAX_PATHS*PathController.DATASIZE_BYTES+7*4+1;
    }


    public void _saveGameData(DataOutputStream _outputStream) throws IOException
    {
        for(int li=0;li<MAX_SPRITES;li++)
        {
            _outputStream.writeByte(ap_Sprites[li].i_ObjectType);
            ap_Sprites[li].writeSpriteToStream(_outputStream);
        }

        for(int li=0;li<MAX_PATHS;li++)
        {
            ap_Paths[li].writePathToStream(_outputStream);
        }

        _outputStream.writeByte(p_foxSprite.i_ObjectType);
        p_foxSprite.writeSpriteToStream(_outputStream);

        _outputStream.writeInt(i_busyRavens);
        _outputStream.writeInt(i_catchenCheeses);
        _outputStream.writeInt(i_foxUpDelay);
        _outputStream.writeInt(i_generatedCheeses);
        _outputStream.writeInt(i_lastGeneratedRaven);
        _outputStream.writeInt(i_nextRavenDelay);
        _outputStream.writeInt(i_PlayerAttemptions);
        _outputStream.writeBoolean(lg_foxCatchingCheese);
    }

    public void _loadGameData(DataInputStream _inputStream) throws IOException
    {
        for(int li=0;li<MAX_SPRITES;li++)
        {
            int i_type = _inputStream.readByte();
            activateSprite(ap_Sprites[li],i_type);
            ap_Sprites[li].readSpriteFromStream(_inputStream);
        }

        for(int li=0;li<MAX_PATHS;li++)
        {
            ap_Paths[li].readPathFromStream(_inputStream,ap_Sprites);
        }

        int i_type = _inputStream.readByte();
        activateSprite(p_foxSprite,i_type);
        p_foxSprite.readSpriteFromStream(_inputStream);

        i_busyRavens = _inputStream.readInt();
        i_catchenCheeses = _inputStream.readInt();
        i_foxUpDelay = _inputStream.readInt();
        i_generatedCheeses = _inputStream.readInt();
        i_lastGeneratedRaven = _inputStream.readInt();
        i_nextRavenDelay = _inputStream.readInt();
        i_PlayerAttemptions = _inputStream.readInt();
        lg_foxCatchingCheese = _inputStream.readBoolean();
    }

    private PathController getInactivePath()
    {
        for (int li = 0; li < MAX_PATHS; li++)
        {
            if (ap_Paths[li].isCompleted()) return ap_Paths[li];
        }
        return null;
    }

    private Sprite getFirstInactiveSprite()
    {
        for (int li = 0; li < MAX_SPRITES; li++)
        {
            if (ap_Sprites[li].lg_SpriteActive) continue;
            return ap_Sprites[li];
        }
        return null;
    }

    private void processSprites()
    {
        for (int li = 0; li < MAX_SPRITES; li++)
        {
            Sprite p_curSprite = ap_Sprites[li];
            if (!p_curSprite.lg_SpriteActive) continue;
            if (p_curSprite.processAnimation())
            {
                switch (p_curSprite.i_ObjectType)
                {
                    case SPRITE_RAVEN_RIGHT_CROAKING:
                        {
                            PathController p_path = getInactivePath();
                            int i_path;

                            if (p_curSprite.i_ObjectState == STATE_RAVEN_TOP)
                            {
                                i_path = PATH_RAVEN_BACK_RIGHTTOP;
                            }
                            else
                            {
                                i_path = PATH_RAVEN_BACK_RIGHTDOWN;
                            }

                            int i_t = p_curSprite.i_ObjectState;
                            activateSprite(p_curSprite, SPRITE_RAVEN_EMPTY_RIGHT);
                            p_curSprite.i_ObjectState = i_t;
                            p_path.initPath(PATH_CENTERX, PATH_CENTERY, p_curSprite, ash_paths, i_path, 0, 0, PathController.MODIFY_NONE);
                        }
                        ;
                        break;
                    case SPRITE_RAVEN_LEFT_CROAKING:
                        {
                            PathController p_path = getInactivePath();
                            int i_path;

                            if (p_curSprite.i_ObjectState == STATE_RAVEN_TOP)
                            {
                                i_path = PATH_RAVEN_BACK_LEFTTOP;
                            }
                            else
                            {
                                i_path = PATH_RAVEN_BACK_LEFTDOWN;
                            }

                            int i_t = p_curSprite.i_ObjectState;
                            activateSprite(p_curSprite, SPRITE_RAVEN_EMPTY_LEFT);
                            p_curSprite.i_ObjectState = i_t;
                            p_path.initPath(PATH_CENTERX, PATH_CENTERY, p_curSprite, ash_paths, i_path, 0, 0, PathController.MODIFY_NONE);
                        }
                        ;
                        break;
                }
            }
        }
    }

    private void deactivateObject(int _state)
    {
        for (int li = 0; li < MAX_SPRITES; li++)
        {
            Sprite p_spr = ap_Sprites[li];
            if (p_spr.lg_SpriteActive)
            {
                if (p_spr.i_ObjectState == _state)
                {
                    p_spr.lg_SpriteActive = false;
                    return;
                }
            }
        }
    }

    private boolean processPaths()
    {
        boolean lg_foxHasTakenCheese = false;
        for (int li = 0; li < MAX_PATHS; li++)
        {
            PathController p_curPathController = ap_Paths[li];
            if (p_curPathController.isCompleted()) continue;
            if (p_curPathController.processStep())
            {
                Sprite p_spr = p_curPathController.p_sprite;
                int i_sprType = p_spr.i_ObjectType;

                switch (i_sprType)
                {
                    case SPRITE_RAVEN_EMPTY_LEFT:
                    case SPRITE_RAVEN_EMPTY_RIGHT:
                        {
                            int i_val = convertRavenPosition(p_spr);
                            p_spr.lg_SpriteActive = false;
                            i_busyRavens &= ~i_val;
                            m_pAbstractGameActionListener.processGameAction(GAMEACTION_RAVEN_HIDDEN);
                        }
                        ;
                        break;
                    case SPRITE_RAVEN_WITH_CHEESE_LEFT:
                        {
                            if (p_foxSprite.i_ObjectType == SPRITE_FOX_DOWN_LEFT && p_spr.i_ObjectState == STATE_RAVEN_DOWN)
                            {
                                activateSprite(p_foxSprite, SPRITE_FOX_DOWN_OPEN_LEFT);
                                lg_foxCatchingCheese = true;

                                int i_t = p_spr.i_ObjectState;
                                activateSprite(p_spr, SPRITE_RAVEN_EMPTY_LEFT);
                                p_spr.i_ObjectState = i_t;
                                p_curPathController.initPath(PATH_CENTERX, PATH_CENTERY, p_spr, ash_paths, PATH_RAVEN_BACK_LEFTDOWN, 0, 0, PathController.MODIFY_NONE);


                                Sprite p_cheese = getFirstInactiveSprite();
                                PathController p_contr = getInactivePath();
                                activateSprite(p_cheese, SPRITE_DROPPED_CHEESE);
                                p_cheese.i_ObjectState = STATE_CHEESE_CATCHED;
                                p_contr.initPath(PATH_CENTERX, PATH_CENTERY, p_cheese, ash_paths, PATH_CHEESE_CATCHED_DOWN_LEFT, 0, 0, PathController.MODIFY_NONE);
                            }
                            else if (p_foxSprite.i_ObjectType == SPRITE_FOX_TOP_LEFT && p_spr.i_ObjectState == STATE_RAVEN_TOP)
                            {
                                activateSprite(p_foxSprite, SPRITE_FOX_TOP_OPEN_LEFT);
                                lg_foxCatchingCheese = true;

                                int i_t = p_spr.i_ObjectState;
                                activateSprite(p_spr, SPRITE_RAVEN_EMPTY_LEFT);
                                p_spr.i_ObjectState = i_t;
                                p_curPathController.initPath(PATH_CENTERX, PATH_CENTERY, p_spr, ash_paths, PATH_RAVEN_BACK_LEFTTOP, 0, 0, PathController.MODIFY_NONE);


                                Sprite p_cheese = getFirstInactiveSprite();
                                PathController p_contr = getInactivePath();
                                activateSprite(p_cheese, SPRITE_DROPPED_CHEESE);
                                p_cheese.i_ObjectState = STATE_CHEESE_CATCHED;
                                p_contr.initPath(PATH_CENTERX, PATH_CENTERY, p_cheese, ash_paths, PATH_CHEESE_CATCHED_TOP_LEFT, 0, 0, PathController.MODIFY_NONE);
                            }
                            else
                            {
                                int i_path;

                                if (p_spr.i_ObjectState == STATE_RAVEN_TOP)
                                    i_path = PATH_CHEESE_FALL_TOP_LEFT;
                                else
                                    i_path = PATH_CHEESE_FALL_DOWN_LEFT;

                                int i_t = p_spr.i_ObjectState;
                                activateSprite(p_spr, SPRITE_RAVEN_LEFT_CROAKING);
                                p_spr.i_ObjectState = i_t;

                                Sprite p_cheese = getFirstInactiveSprite();
                                activateSprite(p_cheese, SPRITE_DROPPED_CHEESE);
                                p_cheese.i_ObjectState = STATE_CHEESE_DROPPED_LEFT;
                                p_curPathController.initPath(PATH_CENTERX, PATH_CENTERY, p_cheese, ash_paths, i_path, 0, 0, PathController.MODIFY_NONE);
                                m_pAbstractGameActionListener.processGameAction(GAMEACTION_RAVEN_CROAKING);
                            }
                        }
                        ;
                        break;
                    case SPRITE_RAVEN_WITH_CHEESE_RIGHT:
                        {
                            if (p_foxSprite.i_ObjectType == SPRITE_FOX_DOWN_RIGHT && p_spr.i_ObjectState == STATE_RAVEN_DOWN)
                            {
                                activateSprite(p_foxSprite, SPRITE_FOX_DOWN_OPEN_RIGHT);
                                lg_foxCatchingCheese = true;

                                int i_t = p_spr.i_ObjectState;
                                activateSprite(p_spr, SPRITE_RAVEN_EMPTY_RIGHT);
                                p_spr.i_ObjectState = i_t;
                                p_curPathController.initPath(PATH_CENTERX, PATH_CENTERY, p_spr, ash_paths, PATH_RAVEN_BACK_RIGHTDOWN, 0, 0, PathController.MODIFY_NONE);


                                Sprite p_cheese = getFirstInactiveSprite();
                                PathController p_contr = getInactivePath();
                                activateSprite(p_cheese, SPRITE_DROPPED_CHEESE);
                                p_cheese.i_ObjectState = STATE_CHEESE_CATCHED;
                                p_contr.initPath(PATH_CENTERX, PATH_CENTERY, p_cheese, ash_paths, PATH_CHEESE_CATCHED_DOWN_RIGHT, 0, 0, PathController.MODIFY_NONE);
                            }
                            else if (p_foxSprite.i_ObjectType == SPRITE_FOX_TOP_RIGHT && p_spr.i_ObjectState == STATE_RAVEN_TOP)
                            {
                                activateSprite(p_foxSprite, SPRITE_FOX_TOP_OPEN_RIGHT);
                                lg_foxCatchingCheese = true;

                                int i_t = p_spr.i_ObjectState;
                                activateSprite(p_spr, SPRITE_RAVEN_EMPTY_RIGHT);
                                p_spr.i_ObjectState = i_t;
                                p_curPathController.initPath(PATH_CENTERX, PATH_CENTERY, p_spr, ash_paths, PATH_RAVEN_BACK_RIGHTTOP, 0, 0, PathController.MODIFY_NONE);


                                Sprite p_cheese = getFirstInactiveSprite();
                                PathController p_contr = getInactivePath();
                                activateSprite(p_cheese, SPRITE_DROPPED_CHEESE);
                                p_cheese.i_ObjectState = STATE_CHEESE_CATCHED;
                                p_contr.initPath(PATH_CENTERX, PATH_CENTERY, p_cheese, ash_paths, PATH_CHEESE_CATCHED_TOP_RIGHT, 0, 0, PathController.MODIFY_NONE);
                            }
                            else
                            {
                                int i_path;

                                if (p_spr.i_ObjectState == STATE_RAVEN_TOP)
                                    i_path = PATH_CHEESE_FALL_TOP_RIGHT;
                                else
                                    i_path = PATH_CHEESE_FALL_DOWN_RIGHT;

                                int i_t = p_spr.i_ObjectState;
                                activateSprite(p_spr, SPRITE_RAVEN_RIGHT_CROAKING);
                                p_spr.i_ObjectState = i_t;

                                Sprite p_cheese = getFirstInactiveSprite();
                                activateSprite(p_cheese, SPRITE_DROPPED_CHEESE);
                                p_cheese.i_ObjectState = STATE_CHEESE_DROPPED_RIGHT;
                                p_curPathController.initPath(PATH_CENTERX, PATH_CENTERY, p_cheese, ash_paths, i_path, 0, 0, PathController.MODIFY_NONE);
                                m_pAbstractGameActionListener.processGameAction(GAMEACTION_RAVEN_CROAKING);
                            }
                        }
                        ;
                        break;
                    case SPRITE_MOUSE_EMPTY_LEFT:
                        {
                            deactivateObject(STATE_CHEESE_FALLEN_LEFT);
                            activateSprite(p_spr, SPRITE_MOUSE_WITH_CHEESE_LEFT);
                            p_curPathController.initPath(PATH_CENTERX, PATH_CENTERY, p_spr, ash_paths, PATH_MOUSE_CHEESE_LEFT, 0, 0, PathController.MODIFY_NONE);
                            m_pAbstractGameActionListener.processGameAction(GAMEACTION_CHEESE_TAKEN_BY_MOUSE);
                        }
                        ;
                        break;
                    case SPRITE_MOUSE_EMPTY_RIGHT:
                        {
                            deactivateObject(STATE_CHEESE_FALLEN_RIGHT);
                            activateSprite(p_spr, SPRITE_MOUSE_WITH_CHEESE_RIGHT);
                            p_curPathController.initPath(PATH_CENTERX, PATH_CENTERY, p_spr, ash_paths, PATH_MOUSE_CHEESE_RIGHT, 0, 0, PathController.MODIFY_NONE);
                            m_pAbstractGameActionListener.processGameAction(GAMEACTION_CHEESE_TAKEN_BY_MOUSE);
                        }
                        ;
                        break;
                    case SPRITE_MOUSE_WITH_CHEESE_LEFT:
                    case SPRITE_MOUSE_WITH_CHEESE_RIGHT:
                        {
                            p_spr.lg_SpriteActive = false;
                            if (i_PlayerAttemptions>0) i_PlayerAttemptions -- ;
                            m_pAbstractGameActionListener.processGameAction(GAMEACTION_MOUSE_HIDDEN);
                        }
                        ;
                        break;
                    case SPRITE_DROPPED_CHEESE:
                        {
                            switch (p_spr.i_ObjectState)
                            {
                                case STATE_CHEESE_CATCHED:
                                    {
                                        p_spr.lg_SpriteActive = false;
                                        switch(p_foxSprite.i_ObjectType)
                                        {
                                            case SPRITE_FOX_DOWN_OPEN_LEFT :
                                                {
                                                    activateSprite(p_foxSprite,SPRITE_FOX_DOWN_LEFT);
                                                };break;
                                            case SPRITE_FOX_DOWN_OPEN_RIGHT :
                                                {
                                                    activateSprite(p_foxSprite,SPRITE_FOX_DOWN_RIGHT);
                                                };break;
                                            case SPRITE_FOX_TOP_OPEN_LEFT :
                                                {
                                                    activateSprite(p_foxSprite,SPRITE_FOX_TOP_LEFT);
                                                };break;
                                            case SPRITE_FOX_TOP_OPEN_RIGHT :
                                                {
                                                    activateSprite(p_foxSprite,SPRITE_FOX_TOP_RIGHT);
                                                };break;
                                        }
                                        lg_foxHasTakenCheese = true;

                                        i_catchenCheeses ++;

                                        if (i_catchenCheeses % REMOVE_FALLEN_CHEESES == 0)
                                        {
                                          m_pAbstractGameActionListener.processGameAction(GAMEACTION_ATTEMPTION_INCREASED);
                                          i_PlayerAttemptions = MAX_ATTEMPTIONS;
                                        }
                                        else
                                        {
                                            m_pAbstractGameActionListener.processGameAction(GAMEACTION_CHEESE_TAKEN_BY_FOX);
                                        }
                                        lg_foxCatchingCheese = false;



                                    };break;
                                case STATE_CHEESE_DROPPED_LEFT:
                                    {
                                        m_pAbstractGameActionListener.processGameAction(GAMEACTION_CHEESE_FALLEN);
                                        p_spr.i_ObjectState = STATE_CHEESE_FALLEN_LEFT;
                                        createMouse(true);
                                        m_pAbstractGameActionListener.processGameAction(GAMEACTION_MOUSE_GENERATED);
                                    }
                                    ;
                                    break;
                                case STATE_CHEESE_DROPPED_RIGHT:
                                    {
                                        m_pAbstractGameActionListener.processGameAction(GAMEACTION_CHEESE_FALLEN);
                                        p_spr.i_ObjectState = STATE_CHEESE_FALLEN_RIGHT;
                                        createMouse(false);
                                        m_pAbstractGameActionListener.processGameAction(GAMEACTION_MOUSE_GENERATED);
                                    }
                                    ;
                                    break;
                            }
                        }
                        ;
                        break;
                }
            }
        }
        return lg_foxHasTakenCheese;
    }

    private void createMouse(boolean _left)
    {
        Sprite p_spr = getFirstInactiveSprite();
        PathController p_path = getInactivePath();

        int i_path;
        int i_spr;

        if (_left)
        {
            i_spr = SPRITE_MOUSE_EMPTY_LEFT;
            i_path = PATH_MOUSE_EMPTY_LEFT;
        }
        else
        {
            i_spr = SPRITE_MOUSE_EMPTY_RIGHT;
            i_path = PATH_MOUSE_EMPTY_RIGHT;
        }

        activateSprite(p_spr, i_spr);
        p_path.initPath(PATH_CENTERX, PATH_CENTERY, p_spr, ash_paths, i_path, 0, 0, PathController.MODIFY_NONE);
    }

    private int convertRavenPosition(Sprite _ravenSprite)
    {
        switch (_ravenSprite.i_ObjectState)
        {
            case STATE_RAVEN_TOP:
                {
                    switch (_ravenSprite.i_ObjectType)
                    {
                        case SPRITE_RAVEN_LEFT_CROAKING:
                        case SPRITE_RAVEN_EMPTY_LEFT:
                        case SPRITE_RAVEN_WITH_CHEESE_LEFT:
                            {
                                return 0xFF;
                            }
                        case SPRITE_RAVEN_RIGHT_CROAKING:
                        case SPRITE_RAVEN_EMPTY_RIGHT:
                        case SPRITE_RAVEN_WITH_CHEESE_RIGHT:
                            {
                                return 0xFF00;
                            }
                    }
                }
                ;
                break;
            case STATE_RAVEN_DOWN:
                {
                    switch (_ravenSprite.i_ObjectType)
                    {
                        case SPRITE_RAVEN_LEFT_CROAKING:
                        case SPRITE_RAVEN_EMPTY_LEFT:
                        case SPRITE_RAVEN_WITH_CHEESE_LEFT:
                            {
                                return 0xFF0000;
                            }
                        case SPRITE_RAVEN_RIGHT_CROAKING:
                        case SPRITE_RAVEN_EMPTY_RIGHT:
                        case SPRITE_RAVEN_WITH_CHEESE_RIGHT:
                            {
                                return 0xFF000000;
                            }
                    }
                }
                ;
                break;
        }
        return 0;
    }

    private void generateRaven()
    {
         if (i_busyRavens == 0xFFFFFFFF) return;

        Sprite p_spr = getFirstInactiveSprite();
        PathController p_path = getInactivePath();

        int i_type = 0;
        int i_state = 0;
        int i_path = 0;

        boolean lg_enable = true;
        if ((getRandomInt(100) > 50) && (i_busyRavens & 0xFF) == 0 && i_lastGeneratedRaven != 0xFF)
        {
            i_type = SPRITE_RAVEN_WITH_CHEESE_LEFT;
            i_state = STATE_RAVEN_TOP;
            i_path = PATH_RAVEN_LEFTTOP;
            i_busyRavens |= 0xFF;
            i_lastGeneratedRaven = 0xFF;
        }
        else if ((getRandomInt(100) > 50) && (i_busyRavens & 0xFF00) == 0 && i_lastGeneratedRaven != 0xFF00)
        {
            i_type = SPRITE_RAVEN_WITH_CHEESE_RIGHT;
            i_state = STATE_RAVEN_TOP;
            i_path = PATH_RAVEN_RIGHTTOP;
            i_busyRavens |= 0xFF00;
            i_lastGeneratedRaven = 0xFF00;
        }
        else if ((getRandomInt(100) > 50) && (i_busyRavens & 0xFF0000) == 0 && i_lastGeneratedRaven != 0xFF0000)
        {
            i_type = SPRITE_RAVEN_WITH_CHEESE_LEFT;
            i_state = STATE_RAVEN_DOWN;
            i_path = PATH_RAVEN_LEFTDOWN;

            i_busyRavens |= 0xFF0000;
            i_lastGeneratedRaven = 0xFF0000;
        }
        else if ((getRandomInt(100) > 50) && (i_busyRavens & 0xFF000000) == 0 && i_lastGeneratedRaven != 0xFF000000)
        {
            i_type = SPRITE_RAVEN_WITH_CHEESE_RIGHT;
            i_state = STATE_RAVEN_DOWN;
            i_path = PATH_RAVEN_RIGHTDOWN;

            i_busyRavens |= 0xFF000000;
            i_lastGeneratedRaven = 0xFF000000;
        }
        else
            lg_enable = false;

        if (lg_enable)
        {
            activateSprite(p_spr, i_type);
            p_spr.i_ObjectState = i_state;

            p_path.initPath(PATH_CENTERX, PATH_CENTERY, p_spr, ash_paths, i_path, 0, 0, PathController.MODIFY_NONE);
            i_generatedCheeses ++;
            m_pAbstractGameActionListener.processGameAction(GAMEACTION_RAVEN_GENERATED);
            i_nextRavenDelay = calculateNewRavenDelay();
        }
        else
            i_nextRavenDelay = 0;
    }

    private int calculateNewRavenDelay()
    {
        int i_num = SPEED_CHANGE_SCORES_STEP - (i_generatedCheeses % SPEED_CHANGE_SCORES_STEP);

        int i8_del = ((i_levelStartNextRavenDelay - i_levelEndNextRavenDelay)<<8)/SPEED_CHANGE_SCORES_STEP;

        i_num = (i8_del * i_num)>>8;

        return i_num+i_levelEndNextRavenDelay;
    }

    private void processFox(int _move)
    {
        if (!lg_foxCatchingCheese)
        {
            boolean lg_nonchanged = true;
            if (i_foxUpDelay > 0)
            {
                i_foxUpDelay--;
                if (i_foxUpDelay == 0)
                {
                    switch (p_foxSprite.i_ObjectType)
                    {
                        case SPRITE_FOX_TOP_LEFT:
                            {
                                activateSprite(p_foxSprite, SPRITE_FOX_DOWN_LEFT);
                                lg_nonchanged = false;
                            }
                            ;
                            break;
                        case SPRITE_FOX_TOP_RIGHT:
                            {
                                activateSprite(p_foxSprite, SPRITE_FOX_DOWN_RIGHT);
                                lg_nonchanged = false;
                            }
                            ;
                            break;
                    }
                }
            }

            if (!lg_nonchanged) return;

            if ((_move & MoveObject.BUTTON_LEFT) != 0)
            {
                switch (p_foxSprite.i_ObjectType)
                {
                    case SPRITE_FOX_DOWN_RIGHT:
                        {
                            activateSprite(p_foxSprite, SPRITE_FOX_DOWN_LEFT);
                        }
                        ;
                        break;
                    case SPRITE_FOX_TOP_RIGHT:
                        {
                            activateSprite(p_foxSprite, SPRITE_FOX_TOP_LEFT);
                            i_foxUpDelay = FOX_UP_DELAY;
                        }
                        ;
                        break;
                }
            }
            else if ((_move & MoveObject.BUTTON_RIGHT) != 0)
            {
                switch (p_foxSprite.i_ObjectType)
                {
                    case SPRITE_FOX_DOWN_LEFT:
                        {
                            activateSprite(p_foxSprite, SPRITE_FOX_DOWN_RIGHT);
                        }
                        ;
                        break;
                    case SPRITE_FOX_TOP_LEFT:
                        {
                            activateSprite(p_foxSprite, SPRITE_FOX_TOP_RIGHT);
                            i_foxUpDelay = FOX_UP_DELAY;
                        }
                        ;
                        break;
                }
            }
            if ((_move & MoveObject.BUTTON_UP) != 0)
            {

                i_foxUpDelay = FOX_UP_DELAY;

                switch (p_foxSprite.i_ObjectType)
                {
                    case SPRITE_FOX_DOWN_LEFT:
                        {
                            activateSprite(p_foxSprite, SPRITE_FOX_TOP_LEFT);
                        }
                        ;
                        break;
                    case SPRITE_FOX_DOWN_RIGHT:
                        {
                            activateSprite(p_foxSprite, SPRITE_FOX_TOP_RIGHT);
                        }
                        ;
                        break;
                }
            }
            else if ((_move & MoveObject.BUTTON_DOWN) != 0)
            {

                i_foxUpDelay = 0;

                switch (p_foxSprite.i_ObjectType)
                {
                    case SPRITE_FOX_TOP_LEFT:
                        {
                            activateSprite(p_foxSprite, SPRITE_FOX_DOWN_LEFT);
                        }
                        ;
                        break;
                    case SPRITE_FOX_TOP_RIGHT:
                        {
                            activateSprite(p_foxSprite, SPRITE_FOX_DOWN_RIGHT);
                        }
                        ;
                        break;
                }
            }
        }
    }

    public int nextGameStep()
    {
        CAbstractPlayer p_player = m_pPlayerList[0];
        p_player.nextPlayerMove(this, p_MoveObject);

        processFox(p_MoveObject.i_buttonState);
        processSprites();
        if (processPaths())
        {
            p_player.setPlayerMoveGameScores(1,false);
        }

        if (i_nextRavenDelay > 0)
        {
            i_nextRavenDelay--;
        }
        else
        {
            generateRaven();
        }

        if (i_PlayerAttemptions <= 0)
        {
          if(i_catchenCheeses >= WINNER_CHEESES)

                 {
                      m_pWinningList = m_pPlayerList;
                 }
                    else
                         {
                             m_pWinningList = null;
                         }

            setGameState(GAMEWORLDSTATE_GAMEOVER);

        }

        return m_iGameState;
    }

    public int getGameStepDelay()
    {
        return i_timedelay;
    }
}
