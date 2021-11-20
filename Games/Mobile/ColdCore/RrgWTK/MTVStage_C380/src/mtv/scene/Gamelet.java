package mtv.scene;

import java.util.Random;
import java.io.*;

/**
 * ����� ��������� ����������� ������� ������.
 *
 * @author ����� �������
 * @version 4.1
 *          (�) 2003-2005 Raydac Reserach Group Ltd.
 */

public class Gamelet
{
    protected static final int ACTION_SOUND_BREAKED_FLOWERS = 0;
    protected static final int ACTION_SOUND_BREAKED_BOTTLE = 1;
    protected static final int ACTION_SOUND_BREAKED_CAN = 2;
    protected static final int ACTION_SOUND_BREAKED_EGG = 3;
    protected static final int ACTION_SOUND_BREAKED_TOMATO = 4;
    protected static final int ACTION_SOUND_ACTOR_HIT = 5;
    protected static final int ACTION_SOUND_ACTOR_THANKS = 6;
    protected static final int ACTION_SOUND_ACTOR_BOW = 7;
    protected static final int ACTION_SOUND_ACTOR_DEATH = 8;
    protected static final int ACTION_SOUND_GENERATE_EGG = 9;
    protected static final int ACTION_SOUND_GENERATE_FLOWERS = 10;
    protected static final int ACTION_SOUND_GENERATE_CAN = 11;
    protected static final int ACTION_SOUND_GENERATE_BOTTLE = 12;
    protected static final int ACTION_SOUND_GENERATE_TOMATO = 13;

    private static final int POINT_MID_STATE = 3;
    private static final int POINT_MAX_STATE = 5;
    private static final int POINT_END = 6;

    //============================================================================
    // The array contains values for path controllers
    public static final short [] ash_Paths= new short[] {
        //#if VENDOR=="MOTOROLA" && MODEL=="C380"
         // PATH_thing_path
         (short)116,(short)104,(short)6,(short)0,(short)1,(short)41,(short)8,(short)12,(short)23,(short)9,(short)34,(short)8,(short)10,(short)65,(short)4,(short)10,(short)92,(short)21,(short)9,(short)107,(short)53,(short)8,(short)116,(short)104,(short)10,
         //#endif
         //#if VENDOR=="MOTOROLA" && MODEL=="E398"
         // PATH_thing_path
         (short)147,(short)181,(short)6,(short)0,(short)1,(short)66,(short)8,(short)19,(short)29,(short)9,(short)44,(short)12,(short)10,(short)77,(short)8,(short)10,(short)123,(short)47,(short)9,(short)141,(short)104,(short)8,(short)147,(short)181,(short)10,
        //#endif
    };

    // PATH offsets
    private static final int PATH_thing_path = 2;

    /**
     * �������� ����� �������� ������
     */
    private static final int STEPTIMEDELAY = 80;

    /**
     * ��� ������� ������
     */
    public static final int KEY_NONE = 0;
    /**
     * ������ �������� �����
     */
    public static final int KEY_LEFT = 1;
    /**
     * ������ �������� ������
     */
    public static final int KEY_RIGHT = 2;

    /**
     * ��������� ���� "����� �����"
     */
    private static final int GSTATE_ENTERING = 0;
    /**
     * ��������� ���� "����������� �����"
     */
    private static final int GSTATE_MOVING = 1;
    /**
     * ��������� ���� "������� �����"
     */
    private static final int GSTATE_WIN = 2;
    /**
     * ��������� ���� "�������� �����"
     */
    private static final int GSTATE_LOST = 3;

    /**
     * ����� �� ����������� �������� ������ � �������������
     */
    private static final int TIMEFORWIN_EASY = 80000;

    /**
     * ����� �� ����������� ����������� ������ � �������������
     */
    private static final int TIMEFORWIN_NORMAL = 200000;

    /**
     * ����� �� ����������� �������� ������ � �������������
     */
    private static final int TIMEFORWIN_HARD = 320000;


    /**
     * ������������ ���������� ������������ ������� ��������
     */
    protected static final int MAX_SPRITES = 7;

    /**
     * ���������� ����� ������� ��������
     */
    private static final int START_POINTS_NUMBER = 4;

    /**
     * ���������� ����� "�������" ��������
     */
    private static final int DEST_POINTS_NUMBER = 8;

    /**
     * ����������� �������� ����� ���������� �������� � ������� �����
     */
    private static final int MINIMUM_GENERATION_DELAY = 10;

    /**
     * ������������ �������� ������� ������
     */
    protected static final int PLAYER_INIT_POWER = 100;

    /**
     * ���������� ������� ������ ��������� ���������
     */
    private static final int POW_TOMATO = 5;

    /**
     * ���������� ������� ������ ��������� �����
     */
    private static final int POW_EGG = 10;

    /**
     * ���������� ������� ������ ��������� ������
     */
    private static final int POW_CAN = PLAYER_INIT_POWER/3;

    /**
     * ���������� ������� ������ ��������� ��������
     */
    private static final int POW_BOTTLE = PLAYER_INIT_POWER/2;

    /**
     * ������� ���������� ��������� �������� � ������ �������� ������, ��� ������ �������� ��� ����
     */
    private static final int GENERATOR_EASY_START = 20;

    /**
     * ������� ���������� ��������� �������� � ������ ���������� ������, ��� ������ �������� ��� ����
     */
    private static final int GENERATOR_NORMAL_START = 12;

    /**
     * ������� ���������� ��������� �������� � ������ �������� ������, ��� ������ �������� ��� ����
     */
    private static final int GENERATOR_HARD_START = 8;

    /**
     * ������� ���������� ��������� �������� � ����� ������, ��� ������ �������� ��� ����
     */
    private static final int GENERATOR_END = 5;

    /**
     * ���������� Y ��������� ������ �� ������
     */
    //#if VENDOR=="MOTOROLA"
    //#if MODEL=="C380"
    private static final int PLAYER_Y = 70;
    private static final int PLAYER_STEP = 0x600;
    //#endif
    //#if MODEL=="E398"
    //$private static final int PLAYER_Y = 133;
    //$private static final int PLAYER_STEP = 0x800;
    //#endif
    //#endif

    /**
     * ������� ��������� ����
     */
    private static int i_CurrentGSTATE;

    /**
     * ������� ���������� ������ ���������� ������� ��������
     */
    private static int i8_currentGenerator;

    /**
     * ������� ��� ���������� ���������� ���������� �������
     */
    private static int i8_generatorStep;

    /**
     * ������� ����������� �������� ����� ���������� �������, ������� ��� ��������� �������� ����� ������ ��� ����� ������
     */
    private static int i_minGenerationDelay;

    /**
     * ���������� ��������� ������� ����
     */
    public static long l_FinalTime;

    /**
     * ���������� �������� ������� �� ������� ��� ���������� ���� �� �����
     */
    private static long l_pausedTimeDifferent;

    /**
     * ���������� �������� ������������ ���� �� ������� ������ � �������������
     */
    protected static int i_levelTime;

    /**
     * ������ ������
     */
    public static Sprite p_ActorSprite;

    /**
     * ������ �������� ��������� ��������
     */
    public static Sprite[] ap_Objects;

    /**
     * ������ ����� �������� ��������� ��������
     */
    public static PathController[] ap_Paths;

    /**
     * ���������� ������� ������
     */
    public static int i_PlayerPower;

    /**
     * ������� ��������� ������
     */
    private static int i_PlayerFlowers;


    private static final int SHELL_FLOWERS = 0;
    private static final int SHELL_CAN = 1;
    private static final int SHELL_BOTTLE = 2;
    private static final int SHELL_TOMATO = 3;
    private static final int SHELL_EGG = 4;


    /**
     * ��������� "��������", ������������ ������������� ��������� � ������ �������.
     * @return ������������� ��������� � ������ �������
     */
    private static final int processShells()
    {
        int i_collided = -1;

        for (int li = 0; li < MAX_SPRITES; li++)
        {
            Sprite p_spr = ap_Objects[li];
            if (p_spr.lg_SpriteActive)
            {
                boolean lg_anim = p_spr.processAnimation();

                PathController p_path = ap_Paths[li];
                boolean lg_path = false;
                int i_pathPoint = p_path.getCurrentPointIndex();
                if (!p_path.isCompleted())
                {
                    lg_path = p_path.processStep();
                    i_pathPoint = p_path.getCurrentPointIndex();
                }

                switch (p_spr.i_ObjectState)
                {
                case SHELL_BOTTLE:
                     {
                         if (lg_anim && p_spr.i_ObjectType == SPRITE_BOTTLE_BREAKED)
                         {
                             p_spr.lg_SpriteActive = false;
                         }
                         else if (p_spr.i_ObjectType == SPRITE_BOTTLE_MID)
                         {
                             // �������� �� ������������ � �������
                             if (p_ActorSprite.isCollided(p_spr))
                             {
                                 if (i_collided < 0) i_collided = p_spr.i_ObjectState;
                                 p_spr.lg_SpriteActive = false;
                             }
                         }

                         if (p_spr.lg_SpriteActive && lg_path)
                         {
                             switch (i_pathPoint)
                             {
                             case POINT_MID_STATE:
                                  {
                                      activateSprite(p_spr, SPRITE_BOTTLE_MID);
                                  }
                             ;
                                  break;
                             case POINT_MAX_STATE:
                                  {
                                      activateSprite(p_spr, SPRITE_BOTTLE_MAX);
                                  }
                             ;
                                  break;
                             case POINT_END:
                                  {
                                      activateSprite(p_spr, SPRITE_BOTTLE_BREAKED);
                                      p_actionListener.processGameAction(ACTION_SOUND_BREAKED_BOTTLE);
                                  }
                             ;
                                  break;
                             }
                         }
                     }
                ;
                     break;
                case SHELL_CAN:
                     {
                         if (lg_anim && p_spr.i_ObjectType == SPRITE_CAN_BREAKED)
                         {
                             p_spr.lg_SpriteActive = false;
                         }
                         else if (p_spr.i_ObjectType == SPRITE_CAN_MID)
                         {
                             // �������� �� ������������ � �������
                             if (p_ActorSprite.isCollided(p_spr))
                             {
                                 if (i_collided < 0) i_collided = p_spr.i_ObjectState;
                                 p_spr.lg_SpriteActive = false;
                             }
                         }

                         if (p_spr.lg_SpriteActive && lg_path)
                         {
                             switch (i_pathPoint)
                             {
                             case POINT_MID_STATE:
                                  {
                                      activateSprite(p_spr, SPRITE_CAN_MID);
                                  }
                             ;
                                  break;
                             case POINT_MAX_STATE:
                                  {
                                      activateSprite(p_spr, SPRITE_CAN_MAX);
                                  }
                             ;
                                  break;
                             case POINT_END:
                                  {
                                      activateSprite(p_spr, SPRITE_CAN_BREAKED);
                                      p_actionListener.processGameAction(ACTION_SOUND_BREAKED_CAN);
                                  }
                             ;
                                  break;
                             }
                         }
                     }
                ;
                     break;
                case SHELL_EGG:
                     {
                         if (lg_anim && p_spr.i_ObjectType == SPRITE_EGG_BREAKED)
                         {
                             p_spr.lg_SpriteActive = false;
                         }
                         else if (p_spr.i_ObjectType == SPRITE_EGG_MID)
                         {
                             // �������� �� ������������ � �������
                             if (p_ActorSprite.isCollided(p_spr))
                             {
                                 if (i_collided < 0) i_collided = p_spr.i_ObjectState;
                                 p_spr.lg_SpriteActive = false;
                             }
                         }

                         if (p_spr.lg_SpriteActive && lg_path)
                         {
                             switch (i_pathPoint)
                             {
                             case POINT_MID_STATE:
                                  {
                                      activateSprite(p_spr, SPRITE_EGG_MID);
                                  }
                             ;
                                  break;
                             case POINT_MAX_STATE:
                                  {
                                      activateSprite(p_spr, SPRITE_EGG_MAX);
                                  }
                             ;
                                  break;
                             case POINT_END:
                                  {
                                      activateSprite(p_spr, SPRITE_EGG_BREAKED);
                                      p_actionListener.processGameAction(ACTION_SOUND_BREAKED_EGG);
                                  }
                             ;
                                  break;
                             }
                         }

                     }
                ;
                     break;
                case SHELL_FLOWERS:
                     {
                         if (lg_anim && p_spr.i_ObjectType == SPRITE_FLOWERS_BREAKED)
                         {
                             p_spr.lg_SpriteActive = false;
                         }
                         else if (p_spr.i_ObjectType == SPRITE_FLOWERS_MID)
                         {
                             // �������� �� ������������ � �������
                             if (p_ActorSprite.isCollided(p_spr))
                             {
                                 if (i_collided < 0) i_collided = p_spr.i_ObjectState;
                                 p_spr.lg_SpriteActive = false;
                             }
                         }

                         if (p_spr.lg_SpriteActive && lg_path)
                         {
                             switch (i_pathPoint)
                             {
                             case POINT_MID_STATE:
                                  {
                                      activateSprite(p_spr, SPRITE_FLOWERS_MID);
                                  }
                             ;
                                  break;
                             case POINT_MAX_STATE:
                                  {
                                      activateSprite(p_spr, SPRITE_FLOWERS_MAX);
                                  }
                             ;
                                  break;
                             case POINT_END:
                                  {
                                      activateSprite(p_spr, SPRITE_FLOWERS_BREAKED);
                                      p_actionListener.processGameAction(ACTION_SOUND_BREAKED_FLOWERS);
                                  }
                             ;
                                  break;
                             }
                         }

                     }
                ;
                     break;
                case SHELL_TOMATO:
                     {
                         if (lg_path && i_pathPoint == POINT_END)
                         {
                             activateSprite(p_spr, SPRITE_TOMATO_BREAKED);
                         }
                         else if (lg_anim && p_spr.i_ObjectType == SPRITE_TOMATO_BREAKED)
                         {
                             p_spr.lg_SpriteActive = false;
                         }
                         else if (p_spr.i_ObjectType == SPRITE_TOMATO_MID)
                         {
                             // �������� �� ������������ � �������
                             if (p_ActorSprite.isCollided(p_spr))
                             {
                                 if (i_collided < 0) i_collided = p_spr.i_ObjectState;
                                 p_spr.lg_SpriteActive = false;
                             }
                         }

                         if (p_spr.lg_SpriteActive && lg_path)
                         {
                             switch (i_pathPoint)
                             {
                             case POINT_MID_STATE:
                                  {
                                      activateSprite(p_spr, SPRITE_TOMATO_MID);
                                  }
                             ;
                                  break;
                             case POINT_MAX_STATE:
                                  {
                                      activateSprite(p_spr, SPRITE_TOMATO_MAX);
                                  }
                             ;
                                  break;
                             case POINT_END:
                                  {
                                      activateSprite(p_spr, SPRITE_TOMATO_BREAKED);
                                      p_actionListener.processGameAction(ACTION_SOUND_BREAKED_TOMATO);
                                  }
                             ;
                                  break;
                             }
                         }

                     }
                ;
                     break;
                }
            }
        }

        return i_collided;
    }

    /**
     * ����� ������� ����������� �������
     *
     * @return null ���� ��� ��� ������ ���� ������
     */
    private static Sprite getFirstInactiveSprite(Sprite[] _array)
    {
        int i_array_len = _array.length;
        for (int li = 0; li < i_array_len; li++)
        {
            if (!_array[li].lg_SpriteActive) return _array[li];
        }
        return null;
    }

    private static final void generateShell()
    {
        int i_type = getRandomInt(49999) / 10000;

        Sprite p_emptySprite = getFirstInactiveSprite(ap_Objects);
        if (p_emptySprite == null) return;

        PathController p_path = ap_Paths[p_emptySprite.i_spriteID];

        int i_objType = -1;
        switch (i_type)
        {
        case SHELL_FLOWERS:
                {
             i_objType = SPRITE_FLOWERS_MIN;
                    p_actionListener.processGameAction(ACTION_SOUND_GENERATE_FLOWERS);
                };
             break;
        case SHELL_CAN:
                {
             i_objType = SPRITE_CAN_MIN;
                    p_actionListener.processGameAction(ACTION_SOUND_GENERATE_CAN);
                };
             break;
        case SHELL_BOTTLE:
                {
             i_objType = SPRITE_BOTTLE_MIN;
                    p_actionListener.processGameAction(ACTION_SOUND_GENERATE_BOTTLE);
                };
             break;
        case SHELL_TOMATO:
                {
             i_objType = SPRITE_TOMATO_MIN;
                    p_actionListener.processGameAction(ACTION_SOUND_GENERATE_TOMATO);
                };
             break;
        case SHELL_EGG:
                {
             i_objType = SPRITE_EGG_MIN;
                    p_actionListener.processGameAction(ACTION_SOUND_GENERATE_EGG);
                };
             break;
        }

        activateSprite(p_emptySprite, i_objType);
        p_emptySprite.i_ObjectState = i_type;

        int i_startPoint = (getRandomInt(START_POINTS_NUMBER * 10000 + 9999) / 10000) + 1;
        int i_endPoint = (getRandomInt(DEST_POINTS_NUMBER * 10000 + 9999) / 10000) + 1;

        int i_xStart = i_startPoint * (((i_GameAreaWidth << 8) / (START_POINTS_NUMBER + 2)) >> 8);
        int i_xEnd = i_endPoint * (((i_GameAreaWidth << 8) / (DEST_POINTS_NUMBER + 2)) >> 8);

        int i_pathModifier = PathController.MODIFY_NONE;

        if (i_xStart > i_xEnd)
        {
            i_pathModifier = PathController.MODIFY_FLIPHORZ;
        }
        int i_widthPath = Math.abs(i_xEnd - i_xStart);

        int i_xcoeff = (int) (calculateLocalPathCoeff(i_widthPath, 128, PATH_thing_path) >>> 32);

        p_path.initPath(i_xStart << 8, 0, i_xcoeff, 0x100, p_emptySprite, ash_Paths, PATH_thing_path, 0, 0, i_pathModifier);
    }

    /**
     * ����������� ������������, ��� ������ - ���������� ����
     *
     * @param _width  ������ �������������� ��������������, � ������� ������ ��������� ����
     * @param _height ������ �������������� ��������������, � ������� ������ ��������� ����
     * @return ���������� ������������ ������� 32 ���� ��� �����. �� X, ������� 32 ���� ��� �����. �� Y
     */
    private static long calculateLocalPathCoeff(int _width, int _height, int _pathOffset)
    {
        _pathOffset -= 2;
        int i_w = (int) ash_Paths[_pathOffset++];
        int i_h = (int) ash_Paths[_pathOffset++];

        if (i_w == 0) i_w = 1;
        if (i_h == 0) i_h = 1;

        // ������������ ������������
        long l_coeffW = ((long) _width << 8) / (long) i_w;
        long l_coeffH = ((long) _height << 8) / (long) i_h;

        return (l_coeffW << 32) | l_coeffH;
    }


    /**
     * ����� ����� �� �����
     */
    public static final int SPRITE_ACTOR_STAND = 0;

    /**
     * ����� ��������� �����
     */
    public static final int SPRITE_ACTOR_MOVELEFT = 1;

    /**
     * ����� ��������� ������
     */
    public static final int SPRITE_ACTOR_MOVERIGHT = 2;

    /**
     * ����� �������������� �� ��������� ������ � �������� ������
     */
    public static final int SPRITE_ACTOR_STAND2MOVERIGHT = 3;

    /**
     * ����� �������������� �� ��������� �������� ������ � ������
     */
    public static final int SPRITE_ACTOR_MOVERIGHT2STAND = 4;

    /**
     * ����� �������������� �� ��������� �������� ����� � ������
     */
    public static final int SPRITE_ACTOR_MOVELEFT2STAND = 5;

    /**
     * ����� �������������� �� ��������� ������ � �������� �����
     */
    public static final int SPRITE_ACTOR_STAND2MOVELEFT = 6;

    /**
     * ����� ���������
     */
    public static final int SPRITE_ACTOR_BOW = 7;

    /**
     * ����� ������
     */
    public static final int SPRITE_ACTOR_DEATH = 8;

    /**
     * ����� ���������� �� �����
     */
    public static final int SPRITE_ACTOR_THANKS = 9;

    /**
     * � ������ ������
     */
    public static final int SPRITE_ACTOR_HIT = 10;

    public static final int SPRITE_FLOWERS_MIN = 11;
    public static final int SPRITE_FLOWERS_MID = 12;
    public static final int SPRITE_FLOWERS_MAX = 13;

    public static final int SPRITE_EGG_MIN = 14;
    public static final int SPRITE_EGG_MID = 15;
    public static final int SPRITE_EGG_MAX = 16;

    public static final int SPRITE_TOMATO_MIN = 17;
    public static final int SPRITE_TOMATO_MID = 18;
    public static final int SPRITE_TOMATO_MAX = 19;

    public static final int SPRITE_BOTTLE_MIN = 20;
    public static final int SPRITE_BOTTLE_MID = 21;
    public static final int SPRITE_BOTTLE_MAX = 22;

    public static final int SPRITE_CAN_MIN = 23;
    public static final int SPRITE_CAN_MID = 24;
    public static final int SPRITE_CAN_MAX = 25;

    public static final int SPRITE_EGG_BREAKED = 26;
    public static final int SPRITE_TOMATO_BREAKED = 27;
    public static final int SPRITE_BOTTLE_BREAKED = 28;
    public static final int SPRITE_CAN_BREAKED = 29;
    public static final int SPRITE_FLOWERS_BREAKED = 30;

    private static final void setActorState(int _state)
    {
        activateSprite(p_ActorSprite, _state);
    }

    private static final boolean processActor()
    {
        boolean lg_process = p_ActorSprite.processAnimation();

        switch (p_ActorSprite.i_ObjectType)
        {
        case SPRITE_ACTOR_STAND:
             {

             }
        ;
             break;
        case SPRITE_ACTOR_MOVELEFT2STAND:
        case SPRITE_ACTOR_MOVERIGHT2STAND:
             {
                 if (lg_process) setActorState(SPRITE_ACTOR_STAND);
             }
        ;
             break;
        case SPRITE_ACTOR_STAND2MOVELEFT:
             {
                 if (lg_process) setActorState(SPRITE_ACTOR_MOVELEFT);
             }
        ;
             break;
        case SPRITE_ACTOR_STAND2MOVERIGHT:
             {
                 if (lg_process) setActorState(SPRITE_ACTOR_MOVERIGHT);
             }
        ;
             break;
        case SPRITE_ACTOR_MOVERIGHT:
             {
                 int i8_x = p_ActorSprite.i_mainX;
                 int i_step = PLAYER_STEP;
                 i8_x += i_step;

                 int i8_rb = i8_x + (p_ActorSprite.i_width >> 1);

                 int i8_scrW = i_GameAreaWidth << 8;
                 if (i_CurrentGSTATE != GSTATE_WIN && i8_rb >= i8_scrW)
                 {
                     i8_x = i8_scrW - (p_ActorSprite.i_width >> 1);
                     setActorState(SPRITE_ACTOR_MOVERIGHT2STAND);
                 }

                 p_ActorSprite.setMainPointXY(i8_x, p_ActorSprite.i_mainY);
             }
        ;
             break;
        case SPRITE_ACTOR_MOVELEFT:
             {
                 int i8_x = p_ActorSprite.i_mainX;

                 int i_step = PLAYER_STEP;


                 i8_x -= i_step;

                 int i8_rb = i8_x - (p_ActorSprite.i_width >> 1);

                 if (i8_rb < 0)
                 {
                     i8_x = p_ActorSprite.i_width >> 1;
                     setActorState(SPRITE_ACTOR_MOVELEFT2STAND);
                 }

                 p_ActorSprite.setMainPointXY(i8_x, p_ActorSprite.i_mainY);
             }
        ;
             break;
        case SPRITE_ACTOR_BOW:
             {
                 if (lg_process) setActorState(SPRITE_ACTOR_STAND2MOVERIGHT);
             }
        ;
             break;
        case SPRITE_ACTOR_DEATH:
             {
                 if (lg_process) i_CurrentGSTATE = GSTATE_LOST;
             }
        ;
             break;
        case SPRITE_ACTOR_HIT:
             {
                 if (lg_process) setActorState(SPRITE_ACTOR_STAND);
             }
        ;
             break;
        case SPRITE_ACTOR_THANKS:
             {
                 if (lg_process && p_ActorSprite.lg_backMove) setActorState(SPRITE_ACTOR_STAND);
             }
        ;
             break;
        }
        return lg_process;
    }

    private static final void setGState(int _state)
    {
        i_CurrentGSTATE = _state;
        switch (_state)
        {
        case GSTATE_ENTERING:
             {
                 setActorState(SPRITE_ACTOR_MOVELEFT);
                 int i8_initX = (i_GameAreaWidth << 8) + (p_ActorSprite.i_width >> 1);
                 p_ActorSprite.setMainPointXY(i8_initX, p_ActorSprite.i_mainY);
             }
        ;
             break;
        case GSTATE_LOST:
             {
                 setActorState(SPRITE_ACTOR_DEATH);
             }
        ;
             break;
        case GSTATE_WIN:
             {
                 switch (p_ActorSprite.i_ObjectType)
                 {
                 case SPRITE_ACTOR_MOVELEFT:
                      {
                          setActorState(SPRITE_ACTOR_MOVELEFT2STAND);
                      }
                 ;
                      break;
                 case SPRITE_ACTOR_MOVERIGHT:
                      {
                          setActorState(SPRITE_ACTOR_MOVERIGHT2STAND);
                      }
                 ;
                      break;
                 }

             }
        ;
             break;
        case GSTATE_MOVING:
             {
             }
        ;
             break;
        }
    }

    private static final void processGame(int _key)
    {
        boolean lg_process = processActor();

        i8_currentGenerator -= i8_generatorStep;

        if (i_minGenerationDelay > 0) i_minGenerationDelay--;

        int i_collided = processShells();

        switch (i_CurrentGSTATE)
        {
        case GSTATE_ENTERING:
             {
                 if (p_ActorSprite.i_mainX <= (i_GameAreaWidth << 7))
                 {
                     setGState(GSTATE_MOVING);
                 }
             }
        ;
             break;
        case GSTATE_MOVING:
             {
                 if (i_minGenerationDelay == 0)
                 {
                     int i_generator = i8_currentGenerator >> 8;

                     if (getRandomInt(i_generator) == (i_generator >>> 1))
                     {
                         generateShell();
                         i_minGenerationDelay = MINIMUM_GENERATION_DELAY;
                     }
                 }

                 if (i_collided >= 0)
                 {
                     if (p_ActorSprite.i_ObjectType == SPRITE_ACTOR_HIT)
                     {

                     }
                     else
                     {
                         switch (i_collided)
                         {
                         case SHELL_BOTTLE:
                              {
                                  i_PlayerPower -= POW_BOTTLE;
                                  p_actionListener.processGameAction(ACTION_SOUND_ACTOR_HIT);
                              }
                         ;
                              break;
                         case SHELL_CAN:
                              {
                                  i_PlayerPower -= POW_CAN;
                              }
                         ;
                              break;
                         case SHELL_EGG:
                              {
                                  i_PlayerPower -= POW_EGG;
                              }
                         ;
                              break;
                         case SHELL_FLOWERS:
                              {
                                  if (p_ActorSprite.i_ObjectType != SPRITE_ACTOR_THANKS)
                                  {
                                      activateSprite(p_ActorSprite, SPRITE_ACTOR_THANKS);
                                      i_PlayerFlowers += 20;
                                      p_actionListener.processGameAction(ACTION_SOUND_ACTOR_THANKS);
                                  }
                              }
                         ;
                              break;
                         case SHELL_TOMATO:
                              {
                                  i_PlayerPower -= POW_TOMATO;
                              }
                         ;
                              break;
                         }

                         if (i_PlayerPower <= 0)
                         {
                             i_PlayerPower = 0;
                             activateSprite(p_ActorSprite, SPRITE_ACTOR_DEATH);
                             p_actionListener.processGameAction(ACTION_SOUND_ACTOR_DEATH);
                             setGState(GSTATE_LOST);
                         }
                         else
                         {
                             if (i_collided != SHELL_FLOWERS)
                             {
                                 activateSprite(p_ActorSprite, SPRITE_ACTOR_HIT);
                                 p_actionListener.processGameAction(ACTION_SOUND_ACTOR_HIT);
                             }
                         }
                     }
                 }
                 else
                 {
                     if (l_FinalTime <= System.currentTimeMillis())
                     {
                         setActorState(SPRITE_ACTOR_BOW);
                         p_actionListener.processGameAction(ACTION_SOUND_ACTOR_BOW);
                         setGState(GSTATE_WIN);
                     }
                     else if (_key == KEY_NONE)
                     {
                         switch (p_ActorSprite.i_ObjectType)
                         {
                         case SPRITE_ACTOR_MOVELEFT:
                              {
                                  setActorState(SPRITE_ACTOR_MOVELEFT2STAND);
                              }
                         ;
                              break;
                         case SPRITE_ACTOR_MOVERIGHT:
                              {
                                  setActorState(SPRITE_ACTOR_MOVERIGHT2STAND);
                              }
                         ;
                              break;
                         }
                     }
                     else
                     {
                         if ((_key & KEY_LEFT) != 0 && p_ActorSprite.i_ScreenX>=0x200)
                         {
                             // ������ ������� �����
                             switch (p_ActorSprite.i_ObjectType)
                             {
                             case SPRITE_ACTOR_STAND:
                                  {
                                      setActorState(SPRITE_ACTOR_STAND2MOVELEFT);
                                  }
                             ;
                                  break;
                             }
                         }
                         else
                         if ((_key & KEY_RIGHT) != 0 && p_ActorSprite.i_ScreenX+p_ActorSprite.i_width<=((i_GameAreaWidth-2)<<8))
                         {
                             // ������ ������� ������
                             switch (p_ActorSprite.i_ObjectType)
                             {
                             case SPRITE_ACTOR_STAND:
                                  {
                                      setActorState(SPRITE_ACTOR_STAND2MOVERIGHT);
                                  }
                             ;
                                  break;
                             }
                         }
                     }
                 }
             }
        ;
             break;
        case GSTATE_WIN:
             {
                 if (p_ActorSprite.i_ObjectType == SPRITE_ACTOR_STAND)
                 {
                     setActorState(SPRITE_ACTOR_STAND2MOVERIGHT);
                 }
                 else if (p_ActorSprite.i_mainX - (p_ActorSprite.i_width >> 1) >= (i_GameAreaWidth << 8))
                 {
                     i_PlayerState = PLAYER_WIN;
                 }
             }
        ;
             break;
        case GSTATE_LOST:
             {
                 if (lg_process) i_PlayerState = PLAYER_LOST;
             }
        ;
             break;
        }
    }


    /**
     * ������ ���������� ������� ��������
     */
    private static final int[] ai_SpriteParameters = new int[]
            {
                //#if VENDOR=="MOTOROLA" && MODEL=="C380"
                        // Width, Height,ColOffsetX, ColOffsetY, ColWidth, ColHeight, Frames, FrameDelay, Main point, Animation
                        //SPRITE_ACTOR_STAND
                        0x3500, 0x4500, 0x1500, 0x700, 0xB00, 0x1E00, 1, 1, Sprite.SPRITE_ALIGN_CENTER, Sprite.ANIMATION_FROZEN,
                        //SPRITE_ACTOR_MOVELEFT
                        0x3500, 0x4500, 0x1500, 0x700, 0xB00, 0x1E00, 8, 1, Sprite.SPRITE_ALIGN_CENTER, Sprite.ANIMATION_CYCLIC,
                        //SPRITE_ACTOR_MOVERIGHT
                        0x3500, 0x4500, 0x1500, 0x700, 0xB00, 0x1E00, 8, 1, Sprite.SPRITE_ALIGN_CENTER, Sprite.ANIMATION_CYCLIC,
                        //SPRITE_ACTOR_STAND2MOVERIGHT
                        0x3500, 0x4500, 0x1500, 0x700, 0xB00, 0x1E00, 2, 1, Sprite.SPRITE_ALIGN_CENTER, Sprite.ANIMATION_FROZEN,
                        //
                        0x3500, 0x4500, 0x1500, 0x700, 0xB00, 0x1E00, 2, 1, Sprite.SPRITE_ALIGN_CENTER, Sprite.ANIMATION_FROZEN,
                        //SPRITE_ACTOR_MOVELEFT2STAND
                        0x3500, 0x4500, 0x1500, 0x700, 0xB00, 0x1E00, 2, 1, Sprite.SPRITE_ALIGN_CENTER, Sprite.ANIMATION_FROZEN,
                        //SPRITE_ACTOR_STAND2MOVELEFT
                        0x3500, 0x4500, 0x1500, 0x700, 0xB00, 0x1E00, 2, 1, Sprite.SPRITE_ALIGN_CENTER, Sprite.ANIMATION_FROZEN,
                        //SPRITE_ACTOR_BOW
                        0x3500, 0x4500, 0x1500, 0x700, 0xB00, 0x1E00, 6, 4, Sprite.SPRITE_ALIGN_CENTER, Sprite.ANIMATION_FROZEN,
                        //SPRITE_ACTOR_DEATH
                        0x3500, 0x4500, 0x1500, 0x700, 0xB00, 0x1E00, 6, 1, Sprite.SPRITE_ALIGN_CENTER, Sprite.ANIMATION_FROZEN,
                        //SPRITE_ACTOR_THANKS
                        0x3500, 0x4500, 0x1500, 0x700, 0xB00, 0x1E00, 5, 2, Sprite.SPRITE_ALIGN_CENTER, Sprite.ANIMATION_PENDULUM,
                        //SPRITE_ACTOR_HIT
                        0x3500, 0x4500, 0x1500, 0x700, 0xB00, 0x1E00, 1, 2, Sprite.SPRITE_ALIGN_CENTER, Sprite.ANIMATION_FROZEN,

                        //SPRITE_FLOWERS_MIN
                        0xA00, 0xA00, 0x0, 0x0, 0xA00, 0xA00, 10, 1, Sprite.SPRITE_ALIGN_CENTER, Sprite.ANIMATION_CYCLIC,
                        //SPRITE_FLOWERS_MID
                        0xE00, 0xF00, 0x200, 0x200, 0xE00, 0xF00, 10, 1, Sprite.SPRITE_ALIGN_CENTER, Sprite.ANIMATION_CYCLIC,
                        //SPRITE_FLOWERS_MAX
                        0x1300, 0x1400, 0x0, 0x0, 0x1300, 0x1400, 10, 1, Sprite.SPRITE_ALIGN_CENTER, Sprite.ANIMATION_CYCLIC,

                        //SPRITE_EGG_MIN
                        0xB00, 0x900, 0x0, 0x0, 0xB00, 0x900, 3, 1, Sprite.SPRITE_ALIGN_CENTER, Sprite.ANIMATION_CYCLIC,
                        //SPRITE_EGG_MID
                        0x1000, 0xE00, 0x500, 0x400, 0x600, 0x600, 3, 1, Sprite.SPRITE_ALIGN_CENTER, Sprite.ANIMATION_CYCLIC,
                        //SPRITE_EGG_MAX
                        0x1500, 0x1200, 0x0, 0x0, 0x1500, 0x1200, 9, 1, Sprite.SPRITE_ALIGN_CENTER, Sprite.ANIMATION_CYCLIC,

                        //SPRITE_TOMATO_MIN
                        0x800, 0x700, 0x0, 0x0, 0x800, 0x700, 3, 1, Sprite.SPRITE_ALIGN_CENTER, Sprite.ANIMATION_CYCLIC,
                        //SPRITE_TOMATO_MID
                        0xC00, 0xB00, 0x300, 0x300, 0x600, 0x600, 3, 1, Sprite.SPRITE_ALIGN_CENTER, Sprite.ANIMATION_CYCLIC,
                        //SPRITE_TOMATO_MAX
                        0x1000, 0xE00, 0x0, 0x0, 0x1000, 0xE00, 7, 1, Sprite.SPRITE_ALIGN_CENTER, Sprite.ANIMATION_CYCLIC,

                        //SPRITE_BOTTLE_MIN
                        0xF00, 0x1100, 0x0, 0x0, 0xF00, 0x1100, 10, 1, Sprite.SPRITE_ALIGN_CENTER, Sprite.ANIMATION_CYCLIC,
                        //SPRITE_BOTTLE_MID
                        0x1600, 0x1900, 0x700, 0x800, 0x800, 0x800, 10, 1, Sprite.SPRITE_ALIGN_CENTER, Sprite.ANIMATION_CYCLIC,
                        //SPRITE_BOTTLE_MAX
                        0x1D00, 0x2100, 0x0, 0x0, 0x1D00, 0x2100, 10, 1, Sprite.SPRITE_ALIGN_CENTER, Sprite.ANIMATION_CYCLIC,

                        //SPRITE_CAN_MIN
                        0x700, 0x700, 0x0, 0x0, 0x700, 0x700, 5, 1, Sprite.SPRITE_ALIGN_CENTER, Sprite.ANIMATION_CYCLIC,
                        //SPRITE_CAN_MID
                        0xB00, 0xB00, 0x200, 0x200, 0x600, 0x600, 6, 1, Sprite.SPRITE_ALIGN_CENTER, Sprite.ANIMATION_CYCLIC,
                        //SPRITE_CAN_MAX
                        0xE00, 0xE00, 0x0, 0x0, 0xE00, 0xE00, 5, 1, Sprite.SPRITE_ALIGN_CENTER, Sprite.ANIMATION_CYCLIC,

                        //SPRITE_EGG_BREAKED
                        0x1500, 0x1200, 0x0, 0x0, 0x2000, 0x4000, 1, 10, Sprite.SPRITE_ALIGN_CENTER, Sprite.ANIMATION_FROZEN,
                        //SPRITE_TOMATO_BREAKED
                        0x1000, 0xE00, 0x0, 0x0, 0x2000, 0x4000, 1, 10, Sprite.SPRITE_ALIGN_CENTER, Sprite.ANIMATION_FROZEN,
                        //SPRITE_BOTTLE_BREAKED
                        0x1D00, 0x2100, 0x0, 0x0, 0x2000, 0x4000, 1, 10, Sprite.SPRITE_ALIGN_CENTER, Sprite.ANIMATION_FROZEN,
                        //SPRITE_CAN_BREAKED
                        0xE00, 0xE00, 0x0, 0x0, 0x2000, 0x4000, 1, 10, Sprite.SPRITE_ALIGN_CENTER, Sprite.ANIMATION_FROZEN,
                        //SPRITE_FLOWERS_BREAKED
                        0x1300, 0x1400, 0x0, 0x0, 0x2000, 0x4000, 1, 10, Sprite.SPRITE_ALIGN_CENTER, Sprite.ANIMATION_FROZEN,
                //#endif
                //#if VENDOR=="MOTOROLA" && MODEL=="E398"
                        // Width, Height,ColOffsetX, ColOffsetY, ColWidth, ColHeight, Frames, FrameDelay, Main point, Animation
                        //SPRITE_ACTOR_STAND
                        256*86, 256*102, 256*35, 256*11, 256*21, 256*44, 1, 1, Sprite.SPRITE_ALIGN_CENTER, Sprite.ANIMATION_FROZEN,
                        //SPRITE_ACTOR_MOVELEFT
                        256*86, 256*102, 256*35, 256*11, 256*21, 256*44, 8, 1, Sprite.SPRITE_ALIGN_CENTER, Sprite.ANIMATION_CYCLIC,
                        //SPRITE_ACTOR_MOVERIGHT
                        256*86, 256*102, 256*35, 256*11, 256*21, 256*44, 8, 1, Sprite.SPRITE_ALIGN_CENTER, Sprite.ANIMATION_CYCLIC,
                        //SPRITE_ACTOR_STAND2MOVERIGHT
                        256*86, 256*102, 256*35, 256*11, 256*21, 256*44, 2, 1, Sprite.SPRITE_ALIGN_CENTER, Sprite.ANIMATION_FROZEN,
                        //SPRITE_ACTOR_MOVERIGHT2STAND
                        256*86, 256*102, 256*35, 256*11, 256*21, 256*44, 2, 1, Sprite.SPRITE_ALIGN_CENTER, Sprite.ANIMATION_FROZEN,
                        //SPRITE_ACTOR_MOVELEFT2STAND
                        256*86, 256*102, 256*35, 256*11, 256*21, 256*44, 2, 1, Sprite.SPRITE_ALIGN_CENTER, Sprite.ANIMATION_FROZEN,
                        //SPRITE_ACTOR_STAND2MOVELEFT
                        256*86, 256*102, 256*35, 256*11, 256*21, 256*44, 2, 1, Sprite.SPRITE_ALIGN_CENTER, Sprite.ANIMATION_FROZEN,
                        //SPRITE_ACTOR_BOW
                        256*86, 256*102, 256*35, 256*11, 256*21, 256*44, 6, 4, Sprite.SPRITE_ALIGN_CENTER, Sprite.ANIMATION_FROZEN,
                        //SPRITE_ACTOR_DEATH
                        256*86, 256*102, 256*35, 256*11, 256*21, 256*44, 6, 1, Sprite.SPRITE_ALIGN_CENTER, Sprite.ANIMATION_FROZEN,
                        //SPRITE_ACTOR_THANKS
                        256*86, 256*102, 256*35, 256*11, 256*21, 256*44, 5, 2, Sprite.SPRITE_ALIGN_CENTER, Sprite.ANIMATION_PENDULUM,
                        //SPRITE_ACTOR_HIT
                        256*86, 256*102, 256*35, 256*11, 256*21, 256*44, 1, 2, Sprite.SPRITE_ALIGN_CENTER, Sprite.ANIMATION_FROZEN,

                        //SPRITE_FLOWERS_MIN
                        256*14, 256*15, 0x0, 0x0, 256*14, 256*15, 10, 1, Sprite.SPRITE_ALIGN_CENTER, Sprite.ANIMATION_CYCLIC,
                        //SPRITE_FLOWERS_MID
                        256*19, 256*20, 0x0, 0x0, 256*19, 256*20, 10, 1, Sprite.SPRITE_ALIGN_CENTER, Sprite.ANIMATION_CYCLIC,
                        //SPRITE_FLOWERS_MAX
                        256*22, 256*23, 0x0, 0x0, 256*22, 256*23, 10, 1, Sprite.SPRITE_ALIGN_CENTER, Sprite.ANIMATION_CYCLIC,

                        //SPRITE_EGG_MIN
                        256*16, 256*14, 256*6, 256*5, 256*16, 256*14, 3, 1, Sprite.SPRITE_ALIGN_CENTER, Sprite.ANIMATION_CYCLIC,
                        //SPRITE_EGG_MID
                        256*21, 256*18, 256*6, 256*5, 256*9, 256*8, 3, 1, Sprite.SPRITE_ALIGN_CENTER, Sprite.ANIMATION_CYCLIC,
                        //SPRITE_EGG_MAX
                        256*23, 256*20, 256*6, 256*5, 256*23, 256*20, 9, 1, Sprite.SPRITE_ALIGN_CENTER, Sprite.ANIMATION_CYCLIC,

                        //SPRITE_TOMATO_MIN
                        256*12, 256*11, 256*4, 256*3, 256*8, 256*8, 3, 1, Sprite.SPRITE_ALIGN_CENTER, Sprite.ANIMATION_CYCLIC,
                        //SPRITE_TOMATO_MID
                        256*16, 256*14, 256*4, 256*3, 256*8, 256*8, 3, 1, Sprite.SPRITE_ALIGN_CENTER, Sprite.ANIMATION_CYCLIC,
                        //SPRITE_TOMATO_MAX
                        256*18, 256*16, 256*4, 256*3, 256*8, 256*8, 7, 1, Sprite.SPRITE_ALIGN_CENTER, Sprite.ANIMATION_CYCLIC,

                        //SPRITE_BOTTLE_MIN
                        256*22, 256*25, 256*11, 256*12, 256*10, 256*10, 10, 1, Sprite.SPRITE_ALIGN_CENTER, Sprite.ANIMATION_CYCLIC,
                        //SPRITE_BOTTLE_MID
                        256*29, 256*33, 256*11, 256*12, 256*10, 256*10, 10, 1, Sprite.SPRITE_ALIGN_CENTER, Sprite.ANIMATION_CYCLIC,
                        //SPRITE_BOTTLE_MAX
                        256*31, 256*35, 256*11, 256*12, 256*10, 256*10, 10, 1, Sprite.SPRITE_ALIGN_CENTER, Sprite.ANIMATION_CYCLIC,

                        //SPRITE_CAN_MIN
                        256*11, 256*11, 256*4,256*2, 256*6, 256*6, 5, 1, Sprite.SPRITE_ALIGN_CENTER, Sprite.ANIMATION_CYCLIC,
                        //SPRITE_CAN_MID
                        256*14, 256*14, 256*4,256*2, 256*6, 256*6, 6, 1, Sprite.SPRITE_ALIGN_CENTER, Sprite.ANIMATION_CYCLIC,
                        //SPRITE_CAN_MAX
                        256*20, 256*20, 256*4,256*2, 256*6, 256*6, 5, 1, Sprite.SPRITE_ALIGN_CENTER, Sprite.ANIMATION_CYCLIC,

                        //SPRITE_EGG_BREAKED
                        256*23, 256*20, 0x0, 0x0, 0x1000, 0x1000, 1, 10, Sprite.SPRITE_ALIGN_CENTER, Sprite.ANIMATION_FROZEN,
                        //SPRITE_TOMATO_BREAKED
                        256*18, 256*16, 0x0, 0x0, 0x1000, 0x1000, 1, 10, Sprite.SPRITE_ALIGN_CENTER, Sprite.ANIMATION_FROZEN,
                        //SPRITE_BOTTLE_BREAKED
                        256*31, 256*35, 0x0, 0x0, 0x1000, 0x1000, 1, 10, Sprite.SPRITE_ALIGN_CENTER, Sprite.ANIMATION_FROZEN,
                        //SPRITE_CAN_BREAKED
                        256*20, 256*20, 0x0, 0x0, 0x1000, 0x1000, 1, 10, Sprite.SPRITE_ALIGN_CENTER, Sprite.ANIMATION_FROZEN,
                        //SPRITE_FLOWERS_BREAKED
                        256*22, 256*23, 0x0, 0x0, 0x1000, 0x1000, 1, 10, Sprite.SPRITE_ALIGN_CENTER, Sprite.ANIMATION_FROZEN,
                //#endif
                };

    /**
     * ���������� �����, ����������� ���� ������ � �������
     */
    private static final int SPRITEDATALENGTH = 10;

    /**
     * ������� ������������ �������� ������, �������� ������� �� ������� ��������
     *
     * @param _sprite     �������������� ������
     * @param _actorIndex ������ ����������� ������
     */
    private static void activateSprite(Sprite _sprite, int _actorIndex)
    {
        _sprite.i_ObjectType = _actorIndex;
        _actorIndex *= SPRITEDATALENGTH;

        int[] ai_sprParameters = ai_SpriteParameters;

        int i8_w = ai_sprParameters[_actorIndex++];
        int i8_h = ai_sprParameters[_actorIndex++];

        int i8_cx = ai_sprParameters[_actorIndex++];
        int i8_cy = ai_sprParameters[_actorIndex++];
        int i8_aw = ai_sprParameters[_actorIndex++];
        int i8_ah = ai_sprParameters[_actorIndex++];

        int i_f = ai_sprParameters[_actorIndex++];
        int i_fd = ai_sprParameters[_actorIndex++];
        int i_mp = ai_sprParameters[_actorIndex++];
        int i_an = ai_sprParameters[_actorIndex++];

        _sprite.setAnimation(i_an, i_mp, i8_w, i8_h, i_f, 0, i_fd);
        _sprite.setCollisionBounds(i8_cx, i8_cy, i8_aw, i8_ah);

        _sprite.lg_SpriteActive = true;
    }

    //============================================================================
    /**
     * ��������� ���������� ������� ��������� ������� ���������
     */
    public interface GameActionListener
    {
        /**
         * ��������� �������� �������, � ����� �������� ���������� � �������� ������������ ���������
         */
        public int processGameAction(int _arg);
    }

    /**
     * ��������� ��������� �����
     */
    private static final Random p_RNDGenerator = new Random(System.currentTimeMillis());

    /**
     * ���������, ������������ ��� ����� � ����
     */
    public static final int PLAYER_PLAYING = 0;

    /**
     * ���������, ������������ ��� ����� �������
     */
    public static final int PLAYER_WIN = 1;

    /**
     * ���������, ������������ ��� ����� ��������
     */
    public static final int PLAYER_LOST = 2;

    /**
     * ���������, ������������ ���� � �������������������� ��������� ��� ������������������
     */
    public static final int STATE_UNKNON = 0;

    /**
     * ���������, ������������ ���� � ������������������ ���������
     */
    public static final int STATE_INITED = 1;

    /**
     * ���������, ������������ ���� � ���������� ���������
     */
    public static final int STATE_STARTED = 2;

    /**
     * ���������, ������������ ���� � ���������������� ���������
     */
    public static final int STATE_PAUSED = 3;

    /**
     * ���������, ������������ ���� � ����������� ���������
     */
    public static final int STATE_OVER = 4;

    /**
     * ���������, ������������ ��� ������� ���� "�������"
     */
    public static final int GAMELEVEL_EASY = 0;

    /**
     * ���������, ������������ ��� ������� ���� "����������"
     */
    public static final int GAMELEVEL_NORMAL = 1;

    /**
     * ���������, ������������ ��� ������� ���� "�������"
     */
    public static final int GAMELEVEL_HARD = 2;

    /**
     * ���������� �������� ��������� ����
     */
    public static int i_GameState = STATE_UNKNON;

    /**
     * ���������� �������� ���������� ��������� ����
     */
    public static int i_PrevGameState = STATE_UNKNON;

    /**
     * ���������� �������� ������� ������� ����
     */
    public static int i_GameLevel;

    /**
     * ���������� �������� ������� ���� ����
     */
    public static int i_GameStage;

    /**
     * ���������� �������� ��������� �� ��������� ������� �������
     */
    private static GameActionListener p_actionListener;

    /**
     * ���������� �������� ���������� �������� �� ������ ������� ����
     */
    protected static int i_GameAreaWidth;

    /**
     * ���������� �������� ���������� �������� �� ������ ������� ����
     */
    protected static int i_GameAreaHeight;

    /**
     * ���������� �������� ��������� ������
     */
    public static int i_PlayerState;

    /**
     * ���������� �������� �������� � ������������� ����� �������� ������.
     */
    public static int i_GameStepDelay;

    /**
     * ���������� �������� ���������� ������� ������� ���������
     */
    public static int i_PlayerAttemptions;

    /**
     * ������� ���������� �� ������������� �����, �� � ������ ����� �������� � ������ ���������. ��������� ���� � ��������� INITED..
     *
     * @return true ���� ������������� ������ �������, ����� false.
     */
    public static final boolean init()
    {
        if (i_GameState != STATE_UNKNON) return false;
        p_actionListener = null;
        i_GameAreaHeight = -1;
        i_GameAreaWidth = -1;

        //------------�������� ���� ��� �����--------------------

        //--------------------------------------------------

        setState(STATE_INITED);
        return true;
    }

    /**
     * ������� ���������� �� ��������������� �����, ����� � ������ ����� �������� � ������ ���������
     */
    public static final void release()
    {
        if (i_GameState == STATE_UNKNON) return;

        //------------�������� ���� ��� �����--------------------

        //--------------------------------------------------

        i_GameAreaHeight = -1;
        i_GameAreaWidth = -1;
        p_actionListener = null;

        setState(STATE_UNKNON);
    }

    /**
     * ������� ������������� ��������� ����.
     *
     * @param _state ����� ��������� ����.
     */
    private static final void setState(int _state)
    {
        i_PrevGameState = i_GameState;
        i_GameState = _state;
    }

    /**
     * ������� ���������� � ���������� ��������������� �������� �������� � �������� ������� (������������).
     *
     * @param _limit ������ ������������� ��������� �������� (������������)
     * @return ��������������� ��������������� �������� � �������� �������.
     */
    private static final int getRandomInt(int _limit)
    {
        _limit++;
        _limit = (int) (((long) Math.abs(p_RNDGenerator.nextInt()) * (long) _limit) >>> 31);
        return _limit;
    }

    /**
     * ������������� ����� ������� ������
     *
     * @param _gameAreaWidth  �������� ������ ������� ���� � ��������
     * @param _gameAreaHeight �������� ������ ������� ���� � ��������
     * @param _gameLevel      ��������� ������� ������
     * @param _actionListener ��������� ������� �������
     * @return true ���� ������������� ������� ������ ������ �������, ����� false.
     */
    public static final boolean initNewGame(final int _gameAreaWidth, final int _gameAreaHeight, final int _gameLevel, final GameActionListener _actionListener)
    {
        if (i_GameState != STATE_INITED) return false;
        p_actionListener = _actionListener;
        i_GameAreaHeight = _gameAreaHeight;
        i_GameAreaWidth = _gameAreaWidth;
        i_GameLevel = _gameLevel;
        initPlayerForGame(true);
        //------------�������� ���� ��� �����--------------------
        i_CurrentGSTATE = GSTATE_ENTERING;
        i_GameStepDelay = STEPTIMEDELAY;

        p_ActorSprite = new Sprite(0);
        p_ActorSprite.setMainPointXY(0, PLAYER_Y<<8);

        i_PlayerFlowers = 0;

        ap_Objects = new Sprite[MAX_SPRITES];
        ap_Paths = new PathController[MAX_SPRITES];
        for (int li = 0; li < MAX_SPRITES; li++)
        {
            ap_Paths[li] = new PathController();
            ap_Objects[li] = new Sprite(li);
        }

        setGState(GSTATE_ENTERING);

        i_minGenerationDelay = 0;

        int i_generatorStart = 0;
        switch (_gameLevel)
        {
        case GAMELEVEL_EASY:
             {
                 i_levelTime = TIMEFORWIN_EASY;
                 i_generatorStart = GENERATOR_EASY_START;
             }
        ;
             break;
        case GAMELEVEL_NORMAL:
             {
                 i_levelTime = TIMEFORWIN_NORMAL;
                 i_generatorStart = GENERATOR_NORMAL_START;
             }
        ;
             break;
        case GAMELEVEL_HARD:
             {
                 i_levelTime = TIMEFORWIN_HARD;
                 i_generatorStart = GENERATOR_HARD_START;
             }
        ;
             break;
        }

        l_FinalTime = System.currentTimeMillis() + i_levelTime;

        i_PlayerPower = PLAYER_INIT_POWER;

        i8_currentGenerator = i_generatorStart << 8;
        i8_generatorStep = ((i_generatorStart - GENERATOR_END) << 8) / (i_levelTime / i_GameStepDelay);

        //--------------------------------------------------

        setState(STATE_STARTED);
        return true;
    }

    /**
     * ��������������� ������ ������
     */
    public static final void releaseGame()
    {
        if (i_GameState == STATE_INITED || i_GameState == STATE_UNKNON) return;
        p_actionListener = null;
        i_GameAreaHeight = -1;
        i_GameAreaWidth = -1;

        //------------�������� ���� ��� �����--------------------
        ap_Objects = null;
        ap_Paths = null;
        p_ActorSprite = null;
        //--------------------------------------------------

        setState(STATE_INITED);
    }

    /**
     * ���������� �������� �������� �� �����.
     */
    public static final void pauseGame()
    {
        if (i_GameState == STATE_STARTED)
        {
            setState(STATE_PAUSED);
            //------------�������� ���� ��� �����--------------------
            l_pausedTimeDifferent = l_FinalTime - System.currentTimeMillis();
            //--------------------------------------------------
        }
        else
            return;
    }

    /**
     * ����������� �������� �������� ����� ������ ������ ��� ����� ���������� �� �����
     */
    public static final void resumeGameAfterPauseOrPlayerLost()
    {
        if (i_GameState == STATE_STARTED)
        {
            initPlayerForGame(false);
            //------------�������� ���� ��� �����-----------------------------
            //------��� �������������� ��� ������� ����� ������ ������--------

            //----------------------------------------------------------------
        }
        else if (i_GameState == STATE_PAUSED)
        {
            setState(STATE_STARTED);
            //------------�������� ���� ��� �����--------------------
            //------��� �������������� ��� ������ � �����--------
            l_FinalTime = l_pausedTimeDifferent + System.currentTimeMillis();
            //--------------------------------------------------
        }
        else
            return;
    }

    /**
     * ���������� ���������� ����� ������ �� ��������� ������� ������
     *
     * @return �������� ���������� ����� ������.
     */
    public static final int getPlayerScore()
    {
        //------------�������� ���� ��� �����--------------------
        int i_score = (i_GameLevel+1)*i_PlayerFlowers+((i_GameLevel+1)*i_PlayerPower);
        //--------------------------------------------------
        return i_score;
    }

    /**
     * ������������� ������ ����� ������ ��� ��� ������������� ������� ������
     *
     * @param _initGame ���� true �� ������������� ����, ���� false �� ����� ������ ������
     */
    private static final void initPlayerForGame(boolean _initGame)
    {
        //------------�������� ���� ��� �����--------------------

        //--------------------------------------------------
        i_PlayerState = PLAYER_PLAYING;
    }

    /**
     * ������������� �������� ������
     *
     * @param _stage ID �������� ������
     * @return true ���� ������������� ������ ������ ����� false
     */
    public static final boolean initGameStage(int _stage)
    {
        //------------�������� ���� ��� �����--------------------

        //--------------------------------------------------
        setState(STATE_STARTED);
        i_GameStage = _stage;
        initPlayerForGame(false);
        return true;
    }

    /**
     * �������� �������� ��������� �� ������� ����
     *
     * @param _data ������ ����, ����������� ���������
     * @throws Exception ���� ��������� ������ ��� �������� ��������� ��� ���� ���������� � ��������� ������������� � ���������.
     */
    public static final void loadGameStateFromByteArray(final byte[] _data, GameActionListener _actionListener) throws Exception
    {
        if (i_GameState != STATE_INITED) throw new Exception();
        ByteArrayInputStream p_arrayInputStream = new ByteArrayInputStream(_data);
        DataInputStream p_inputStream = new DataInputStream(p_arrayInputStream);
        int i_gameLevel = p_inputStream.readUnsignedByte();
        int i_gameStage = p_inputStream.readUnsignedByte();
        int i_gameScreenWidth = p_inputStream.readUnsignedShort();
        int i_gameScreenHeight = p_inputStream.readUnsignedShort();
        if (!initNewGame(i_gameScreenWidth, i_gameScreenHeight, i_gameLevel, _actionListener)) throw new Exception();
        if (!initGameStage(i_gameStage)) throw new Exception();
        i_PlayerAttemptions = p_inputStream.readInt();
        //------------�������� ���� ��� �����--------------------
        for(int li=0;li<MAX_SPRITES;li++)
        {
            int i_type = p_inputStream.readUnsignedByte();
            Sprite p_spr = ap_Objects[li];
            activateSprite(p_spr,i_type);
            p_spr.readSpriteFromStream(p_inputStream);
        }
        for(int li=0;li<MAX_SPRITES;li++)
        {
            PathController p_spr = ap_Paths[li];
            p_spr.readPathFromStream(p_inputStream,ap_Objects,ash_Paths);
        }

        int i_state = p_inputStream.readUnsignedByte();
        activateSprite(p_ActorSprite,i_state);
        p_ActorSprite.readSpriteFromStream(p_inputStream);

        i_CurrentGSTATE = p_inputStream.readInt();
        i_minGenerationDelay = p_inputStream.readInt();
        i_PlayerFlowers = p_inputStream.readInt();
        i8_currentGenerator = p_inputStream.readInt();
        l_pausedTimeDifferent = p_inputStream.readLong();
        i_PlayerPower = p_inputStream.readInt();
        //--------------------------------------------------
        p_inputStream.close();
        p_arrayInputStream = null;
        p_inputStream = null;
        Runtime.getRuntime().gc();
    }

    /**
     * ������� ��������� ���� ������, ����������� ������� ������� ���������
     *
     * @return �������� ������, ���������� ���������� ��������� �������� ��������
     * @throws Exception ���� ���� ��� ����� ��������� � ������������� ���������, ��������� ������ ���������� ��� �������������� ������ ������������� �������
     */
    public static final byte[] saveGameStateToByteArray() throws Exception
    {
        if ((i_GameState != STATE_STARTED || i_GameState != STATE_PAUSED) && i_PlayerState != PLAYER_PLAYING) throw new Exception();
        Runtime.getRuntime().gc();
        ByteArrayOutputStream p_arrayOutputStream = new ByteArrayOutputStream(getGameStateDataBlockSize());
        DataOutputStream p_outputStream = new DataOutputStream(p_arrayOutputStream);
        p_outputStream.writeByte(i_GameLevel);
        p_outputStream.writeByte(i_GameStage);
        p_outputStream.writeShort(i_GameAreaWidth);
        p_outputStream.writeShort(i_GameAreaHeight);
        p_outputStream.writeInt(i_PlayerAttemptions);
        //------------�������� ���� ��� �����--------------------
        for(int li=0;li<MAX_SPRITES;li++)
        {
            Sprite p_spr = ap_Objects[li];
            p_outputStream.writeByte(p_spr.i_ObjectType);
            p_spr.writeSpriteToStream(p_outputStream);
        }
        for(int li=0;li<MAX_SPRITES;li++)
        {
            PathController p_spr = ap_Paths[li];
            p_spr.writePathToStream(p_outputStream);
        }

        p_outputStream.writeByte(p_ActorSprite.i_ObjectType);
        p_ActorSprite.writeSpriteToStream(p_outputStream);

        p_outputStream.writeInt(i_CurrentGSTATE);
        p_outputStream.writeInt(i_minGenerationDelay);
        p_outputStream.writeInt(i_PlayerFlowers);
        p_outputStream.writeInt(i8_currentGenerator);
        p_outputStream.writeLong(l_pausedTimeDifferent);
        p_outputStream.writeInt(i_PlayerPower);
        //--------------------------------------------------
        p_outputStream.close();
        p_outputStream = null;
        byte[] ab_result = p_arrayOutputStream.toByteArray();
        p_arrayOutputStream = null;
        if (ab_result.length != getGameStateDataBlockSize()) throw new Exception();
        Runtime.getRuntime().gc();
        return ab_result;
    }

    /**
     * ������� ���������� ������, ��������� ��� ���������� ����� ������� ������.
     *
     * @return ��������� ������ ����� ������.
     */
    public static final int getGameStateDataBlockSize()
    {
        int MINIMUM_SIZE = 10;

        MINIMUM_SIZE += ((PathController.DATASIZE_BYTES+Sprite.DATASIZE_BYTES+1)*MAX_SPRITES);
        MINIMUM_SIZE += Sprite.DATASIZE_BYTES+1;

        //i_CurrentGSTATE;
        MINIMUM_SIZE += 4;
        //i_minGenerationDelay;
        MINIMUM_SIZE += 4;
        //i_PlayerFlowers;
        MINIMUM_SIZE += 4;
        //i8_currentGenerator;
        MINIMUM_SIZE += 4;
        //l_pausedTimeDifferent;
        MINIMUM_SIZE += 8;
        //i_PlayerPower
        MINIMUM_SIZE += 4;

        return MINIMUM_SIZE;
    }

    /**
     * ���������� ��������� ������������� ����
     *
     * @return ������, ���������������� ����.
     */
    public static final String getID()
    {
        return "987432";
    }

    /**
     * ������� ������������ ������� ���
     *
     * @param _keyStateFlags ����� ���������� �������.
     * @return ������ ���� ����� ��������� ������� ���������
     */
    public static final int nextGameStep(int _keyStateFlags)
    {
        //------------�������� ���� ��� �����--------------------
        processGame(_keyStateFlags);
        if (i_PlayerState != PLAYER_PLAYING)
        {
            setState(STATE_OVER);
        }
        //--------------------------------------------------
        return i_GameState;
    }

    /**
     * ����, ������������ ��� ���� ������������ ���������� �������� ������ (����)
     */
    public static final int FLAG_SUPPORTRESTART = 1;
    /**
     * ����, ������������ ��� ���� ������������ ������� ������ (����)
     */
    public static final int FLAG_STAGESUPPORT = 2;

    public static final int getSupportedModes()
    {
        return 0;
    }
}
