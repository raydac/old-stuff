package com.igormaznitsa.GameKit_3.OperationX;

public class AnimeObject
{
    public static final int STATE_APPEARANCE = 0;
    public static final int STATE_LEFT = 1;
    public static final int STATE_RIGHT = 2;
    public static final int STATE_DEATH = 3;
    public static final int STATE_FIRE = 4;
    public static final int STATE_HIDE = 5;

    public static final int OBJECT_PILOTGUNNER = 0;
    // Far trooper
    public static final int OBJECT_TROOPER0 = 1;
    // Midle trooper
    public static final int OBJECT_TROOPER1 = 2;
    // Near trooper
    public static final int OBJECT_TROOPER2 = 3;

    // The helycopter flies horizontally
    public static final int OBJECT_HELYCOPTERHORZ = 4;
    // The helycopter flies vertically
    public static final int OBJECT_HELYCOPTERVERT = 5;
    // The tank
    public static final int OBJECT_TANK = 6;

    // Ammo
    public static final int OBJECT_AMMO = 7;

    // Explosion
    public static final int OBJECT_EXPLOSION = 8;
    // A fountain from a shoot
    public static final int OBJECT_FOUNTAIN = 9;
    // A flash from a shoot
    public static final int OBJECT_SHOOTFIRE = 10;
    // A fountain from a player's shoot
    public static final int OBJECT_PLAYERSHOOT = 11;

    private static final int ANIMATION_NORMAL = 0;
    private static final int ANIMATION_BACK = 1;
    private static final int ANIMATION_FREEZE = 2;

    // The linking object
    public AnimeObject p_linkObject;
    public boolean lg_LinkedObject;
    // The array of shot flashes
    public AnimeObject[] ap_shotFireArray = new AnimeObject[2];

    protected int i8_HorzSpeed;
    public int i_line_index;

    protected int i_indexArray;

    protected static final int[][] aai_OffsetToShootFire = new int[][]
    {
        // Machin gunner
        new int[]{0,0},
        // Far trooper
        new int[]{0,0},
        // Midle trooper
        new int[]{0,0},
        // Near trooper
        new int[]{0,0},
        // The helycopter flies horizontally
        null,
        // The helycopter flies vertically
        new int[]{0-0x500,0,0x500,0},
        // The tank
        new int[]{0,0},
        // Ammo
        null,
        // Explosion
        null,
        // A fountain from a shoot
        null,
        // A flash from a shoot
        null,
        // A fountain from a player's shoot
        null
    };

    public static final int[] ai_AnimationValues = new int[]
    {
        // width, height, frames, delay, animation type, lifeenergy, speed
        // PILOT GUNNER

        // STATE_APPEARANCE
        15, 20, 1, 2, ANIMATION_FREEZE, 0,0x150,
        // STATE_LEFT
        15, 20, 1, 2, ANIMATION_NORMAL, 0,0x150,
        // STATE_RIGHT
        15, 20, 1, 2, ANIMATION_NORMAL, 0,0x150,
        // STATE_DEATH
        15, 20, 3, 2, ANIMATION_FREEZE, 0,0,
        // STATE_FIRE
        15, 20, 1, 2, ANIMATION_NORMAL, 0,0x150,
        // STATE_HIDE
        15, 15, 1, 2, ANIMATION_FREEZE, 0,0x500,/* The speed of fall */

        // TROOPER 0

        // STATE_APPEARANCE
        15, 20, 1, 2, ANIMATION_FREEZE, 0,0x150,
        // STATE_LEFT
        15, 20, 4, 2, ANIMATION_NORMAL, 0,0x050,
        // STATE_RIGHT
        15, 20, 4, 2, ANIMATION_NORMAL, 0,0x200,
        // STATE_DEATH
        15, 20, 3, 2, ANIMATION_FREEZE, 0,0,
        // STATE_FIRE
        15, 20, 1, 2, ANIMATION_NORMAL, 0,0,
        // STATE_HIDE
        0, 0, 0, 0, ANIMATION_FREEZE, 0,0x150,

        // TROOPER 1

        // STATE_APPEARANCE
        15, 20, 1, 2, ANIMATION_FREEZE, 0,0x150,
        // STATE_LEFT
        15, 20, 4, 2, ANIMATION_NORMAL, 0,0x050,
        // STATE_RIGHT
        15, 20, 4, 2, ANIMATION_NORMAL, 0,0x200,
        // STATE_DEATH
        15, 20, 3, 2, ANIMATION_FREEZE, 0,0,
        // STATE_FIRE
        15, 20, 1, 2, ANIMATION_NORMAL, 0,0,
        // STATE_HIDE
        15, 15, 0, 2, ANIMATION_FREEZE, 0,0x150,

        // TROOPER 2

        // STATE_APPEARANCE
        25, 30, 1, 1, ANIMATION_FREEZE, 0,0x150,
        // STATE_LEFT
        25, 30, 4, 1, ANIMATION_NORMAL, 0,0x010,
        // STATE_RIGHT
        25, 30, 4, 1, ANIMATION_NORMAL, 0,0x300,
        // STATE_DEATH
        25, 30, 3, 1, ANIMATION_FREEZE, 0,0,
        // STATE_FIRE
        25, 30, 1, 1,ANIMATION_NORMAL, 0,0,
        // STATE_HIDE
        25, 30, 0, 1,ANIMATION_FREEZE, 0,0x150,

        // COPTER HORZ

        // STATE_APPEARANCE
        29, 15, 1, 1, ANIMATION_FREEZE, 0,0x300,
        // STATE_LEFT
        29, 15, 2, 1, ANIMATION_NORMAL, 0,0x300,
        // STATE_RIGHT
        29, 15, 2,1, ANIMATION_NORMAL, 0,0x300,
        // STATE_DEATH
        29, 15, 0, 1, ANIMATION_FREEZE, 0,0x300,
        // STATE_FIRE
        29, 15, 2, 1, ANIMATION_NORMAL, 0,0x300,
        // STATE_HIDE
        0, 0, 0, 0, ANIMATION_FREEZE, 0,0x300,

        // COPTER VERT

        // STATE_APPEARANCE
        0, 0, 0, 0, ANIMATION_FREEZE, 0,0,
        // STATE_LEFT
        25, 16, 2, 1, ANIMATION_NORMAL, 0,0x150,
        // STATE_RIGHT
        25, 16, 2, 1, ANIMATION_NORMAL, 0,0x150,
        // STATE_DEATH
        25, 16, 0, 1, ANIMATION_FREEZE, 0,0x150,
        // STATE_FIRE
        25, 16, 2, 1, ANIMATION_NORMAL, 0,0x150,
        // STATE_HIDE
        0, 0, 0, 0, ANIMATION_FREEZE, 0,0x150,

        // TANK

        // STATE_APPEARANCE
        20, 12, 2, 1, ANIMATION_FREEZE, 0,0,
        // STATE_LEFT
        20, 12, 2, 1, ANIMATION_NORMAL, 0,0x200,
        // STATE_RIGHT
        20, 12, 2, 1, ANIMATION_NORMAL, 0,0x200,
        // STATE_DEATH
        20, 12, 0, 1, ANIMATION_FREEZE, 0,0,
        // STATE_FIRE
        20, 12, 2, 1, ANIMATION_NORMAL, 0,0x200,
        // STATE_HIDE
        0, 0, 0, 0, ANIMATION_FREEZE, 0,0,

        // AMMO

        // STATE_APPEARANCE
        0, 0, 0, 0, ANIMATION_FREEZE, 0,0,
        // STATE_LEFT
        0, 0, 0, 0, ANIMATION_NORMAL, 0,0,
        // STATE_RIGHT
        0, 0, 0, 0, ANIMATION_NORMAL, 0,0,
        // STATE_DEATH
        0, 0, 0, 0, ANIMATION_NORMAL, 0,0,
        // STATE_FIRE
        0, 0, 0, 0, ANIMATION_NORMAL, 0,0,
        // STATE_HIDE
        0, 0, 0, 0, ANIMATION_NORMAL, 0,0,

        // EXPLOSION

        // STATE_APPEARANCE
        17, 12, 5, 1, ANIMATION_NORMAL, 0,0,
        // STATE_LEFT
        0, 0, 0, 0, ANIMATION_NORMAL, 0,0,
        // STATE_RIGHT
        0, 0, 0, 0, ANIMATION_NORMAL, 0,0,
        // STATE_DEATH
        0, 0, 0, 0, ANIMATION_NORMAL, 0,0,
        // STATE_FIRE
        0, 0, 0, 0, ANIMATION_NORMAL, 0,0,
        // STATE_HIDE
        0, 0, 0, 0, ANIMATION_NORMAL, 0,0,

        // FOUNTAIN

        // STATE_APPEARANCE
        4, 4, 3, 0, ANIMATION_NORMAL, 0,0,
        // STATE_LEFT
        0, 0, 0, 0, ANIMATION_NORMAL, 0,0,
        // STATE_RIGHT
        0, 0, 0, 0, ANIMATION_NORMAL, 0,0,
        // STATE_DEATH
        0, 0, 0, 0, ANIMATION_NORMAL, 0,0,
        // STATE_FIRE
        0, 0, 0, 0, ANIMATION_NORMAL, 0,0,
        // STATE_HIDE
        0, 0, 0, 0, ANIMATION_NORMAL, 0,0,

        // SHOOTFIRE

        // STATE_APPEARANCE
        4, 4, 3, 1, ANIMATION_FREEZE, 0,0,
        // STATE_LEFT
        0, 0, 0, 0, ANIMATION_NORMAL, 0,0,
        // STATE_RIGHT
        0, 0, 0, 0, ANIMATION_NORMAL, 0,0,
        // STATE_DEATH
        0, 0, 0, 0, ANIMATION_FREEZE, 0,0,
        // STATE_FIRE
        0, 0, 0, 0, ANIMATION_NORMAL, 0,0,
        // STATE_HIDE
        0, 0, 0, 0, ANIMATION_FREEZE, 0,0,

        // PLAYERSHOOT

        // STATE_APPEARANCE
        4, 4, 3, 1, ANIMATION_FREEZE, 0,0,
        // STATE_LEFT
        0, 0, 0, 0, ANIMATION_NORMAL, 0,0,
        // STATE_RIGHT
        0, 0, 0, 0, ANIMATION_NORMAL, 0,0,
        // STATE_DEATH
        0, 0, 0, 0, ANIMATION_FREEZE, 0,0,
        // STATE_FIRE
        0, 0, 0, 0, ANIMATION_NORMAL, 0,0,
        // STATE_HIDE
        0, 0, 0, 0, ANIMATION_FREEZE, 0,0

    };

    public int i_Type;
    public int i_State;
    public int i_Width;
    public int i_Height;
    public int i8_Speed;
    public int i_Energy;
    public int i_Frame;

    public int i8_ScreenX;
    public int i8_ScreenY;

    public boolean lg_Active;

    protected int i8_centerX,i8_centerY;
    private int i_MaxFrames;
    private int i_MaxDelay;
    protected boolean lg_back;
    protected int i_delay;

    private byte b_animationType;
    protected int i8_halfWidth;
    protected int i8_halfHeight;

    protected int i8_Width,i8_Height;
    public int i_ObjectDirection;

    public AnimeObject(int _indx)
    {
        i_indexArray = _indx;
        lg_Active = false;
    }

    public void initObject(int _type)
    {
        i_State = -1;
        i_ObjectDirection = -1;
        i_Type = _type;
        lg_Active = true;
        initState(STATE_APPEARANCE);
        ap_shotFireArray[0] = null;
        ap_shotFireArray[1] = null;
        lg_LinkedObject = false;
        p_linkObject = null;
    }

    protected void setObjectDirection(int _direction)
    {
        if (_direction == i_ObjectDirection) return;
        initState(_direction);
        i_ObjectDirection = _direction;
    }

    public void initState(int _state)
    {
        if (i_State == _state)
        {
            i_Frame = 0;
            i_delay = i_MaxDelay;
            return;
        }

        if (_state == STATE_LEFT || _state == STATE_RIGHT) i_ObjectDirection = _state;

        i_State = _state;

        int i_offst = (42 * i_Type) + 7 * _state;

        i_Width = ai_AnimationValues[i_offst++];
        i8_Width = i_Width << 8;
        i_Height = ai_AnimationValues[i_offst++];
        i8_Height = i_Height << 8;
        i_MaxFrames = ai_AnimationValues[i_offst++];
        i_MaxDelay = ai_AnimationValues[i_offst++];

        b_animationType = (byte) ai_AnimationValues[i_offst++];

        int i_energy = ai_AnimationValues[i_offst++];

        i8_Speed = ai_AnimationValues[i_offst++];

        if (_state == 0) i_Energy = i_energy;

        i_Frame = 0;
        i_delay = i_MaxDelay;
        i8_halfWidth = (i_Width >>> 1) << 8;
        i8_halfHeight = (i_Height >>> 1) << 8;

        lg_back = false;

        setCenterXY_8(i8_centerX, i8_centerY);
    }

    protected void updateXY()
    {
        i8_ScreenX = i8_centerX - i8_halfWidth;
        i8_ScreenY = i8_centerY - i8_halfHeight;
    }

    protected void setCenterXY_8(int _i8_x, int _i8_y)
    {
        i8_centerX = _i8_x;
        i8_centerY = _i8_y;

        i8_ScreenX = i8_centerX - i8_halfWidth;
        i8_ScreenY = i8_centerY - i8_halfHeight;
    }

    protected void setCenterXY(int _x, int _y)
    {
        i8_centerX = _x << 8;
        i8_centerY = _y << 8;

        i8_ScreenX = i8_centerX - i8_halfWidth;
        i8_ScreenY = i8_centerY - i8_halfHeight;
    }

    public boolean processAnimation()
    {
        switch (b_animationType)
        {
            case ANIMATION_NORMAL:
                {
                    i_delay--;
                    if (i_delay == 0)
                    {
                        i_delay = i_MaxDelay;
                        i_Frame++;
                        if (i_Frame == i_MaxFrames)
                        {
                            i_Frame = 0;
                            return true;
                        }
                    }
                }
                ;
                break;
            case ANIMATION_BACK:
                {
                    i_delay--;
                    if (i_delay == 0)
                    {
                        i_delay = i_MaxDelay;
                        if (lg_back)
                        {
                            if (i_Frame == 0)
                            {
                                i_Frame++;
                                lg_back = false;
                                return true;
                            }
                            else
                            {
                                i_Frame--;
                            }
                        }
                        else
                        {
                            i_Frame++;
                            if (i_Frame == i_MaxFrames)
                            {
                                i_Frame = i_MaxFrames - 2;
                                lg_back = true;
                            }
                        }
                    }
                }
                ;
                break;
            case ANIMATION_FREEZE:
                {
                    if (i_Frame == (i_MaxFrames - 1)) return true;
                    i_delay--;
                    if (i_delay == 0)
                    {
                        i_delay = i_MaxDelay;
                        i_Frame++;
                        if (i_Frame >= i_MaxFrames)
                        {
                            i_Frame = i_MaxFrames - 1;
                            return true;
                        }
                    }
                }
                ;
                break;
        }

        return false;
    }
}
