package com.GameKit_3.SantaClaus;

public class AnimeObject
{
    public static final byte STATE_APPEARANCE = 0;
    public static final byte STATE_LEFT = 1;
    public static final byte STATE_RIGHT = 2;
    public static final byte STATE_JUMP = 3;
    public static final byte STATE_COWER = 4;
    public static final byte STATE_DEATH = 5;
    public static final byte STATE_FIRE = 6;

    public static final byte OBJECT_PLAYER = 0;
    public static final byte OBJECT_GHOST = 1;
    public static final byte OBJECT_FALLINGSTAR = 2;
    public static final byte OBJECT_FALLEDSTAR = 3;
    public static final byte OBJECT_CAT = 4;
    public static final byte OBJECT_SNOWBAL = 5;
    public static final byte OBJECT_SMOKE = 6;
    public static final byte OBJECT_UFO = 7;
    public static final byte OBJECT_WITCH = 8;
    public static final byte OBJECT_ICECREAM = 9;
    public static final byte OBJECT_MOON = 10;
    public static final byte OBJECT_FIRE = 11;

    private static final byte ANIMATION_NORMAL = 0;
    private static final byte ANIMATION_BACK = 1;
    private static final byte ANIMATION_FREEZE = 2;

    public static final short[] ai_AnimationValues = new short[]
     {
         // width, height, frames, delay, animation type, speed, lifeenergy, left offset, right offset
         //OBJECT_PLAYER

         //Appearance
         21, 24, 3, 2, ANIMATION_FREEZE, 0, 5,0,0,
         //Left
         21, 24, 6, 1, ANIMATION_NORMAL, 0, 5,5,5,
         //Right
         21, 24, 6, 1, ANIMATION_NORMAL, 0, 5,5,5,
         //Jump
         26, 24, 3, 1, ANIMATION_FREEZE, 0, 5,7,7,
         //Cower
         26, 24, 2, 3, ANIMATION_BACK, 0, 5,7,7,
         //Death
         28, 24, 3, 3, ANIMATION_FREEZE, 0, 5,8,8,
         //Fire
         28, 24, 3, 1, ANIMATION_NORMAL, 0, 5,8,8,

         //OBJECT_GHOST

         //Appearance
         21, 25, 5, 2,ANIMATION_NORMAL, 0, 5,3,2,
         //Left
         21, 25, 2, 2,ANIMATION_NORMAL, 0, 5,3,2,
         //Right
         21, 25, 2, 2,ANIMATION_NORMAL, 0, 5,3,2,
         //Jump
         0, 0, 0, 0, 0, 0, 5,0,0,
         //Cower
         0, 0, 0, 0, 0, 0, 5,0,0,
         //Death
         32, 32, 5, 2,ANIMATION_NORMAL, 0, 5,3,2,
         //Fire
         0, 0, 0, 0, 0, 0, 5,0,0,

         //OBJECT_FALLINGSTAR

         //Appearance
         7, 7, 2, 1, ANIMATION_BACK, 0, 5,0,0,
         //Left
         0, 0, 0, 0, 0, 0, 5,0,0,
         //Right
         0, 0, 0, 0, 0, 0, 5,0,0,
         //Jump
         0, 0, 0, 0, 0, 0, 5,0,0,
         //Cower
         0, 0, 0, 0, 0, 0, 5,0,0,
         //Death
         0, 0, 0, 0, 0, 0, 5,0,0,
         //Fire
         0, 0, 0, 0, 0, 0, 5,0,0,

         //OBJECT_FALLEDSTAR

         //Appearance
         11, 10, 1, 1, ANIMATION_BACK, 0, 5,0,0,
         //Left
         0, 0, 0, 0, 0, 0, 5,0,0,
         //Right
         0, 0, 0, 0, 0, 0, 5,0,0,
         //Jump
         0, 0, 0, 0, 0, 0, 5,0,0,
         //Cower
         0, 0, 0, 0, 0, 0, 5,0,0,
         //Death
         0, 0, 0, 0, 0, 0, 5,0,0,
         //Fire
         0, 0, 0, 0, 0, 0, 5,0,0,

         //OBJECT_CAT

         //Appearance
         0, 0, 0, 0, 0, 0, 5,0,0,
         //Left
         14, 12, 3, 2, ANIMATION_NORMAL, 0x150, 2,1,4,
         //Right
         14, 12, 3, 2, ANIMATION_NORMAL, 0x150, 2,4,1,
         //Jump
         12, 15, 2, 2, ANIMATION_BACK, 0x200, 5,0,0,
         //Cower
         12, 15, 2, 2, ANIMATION_NORMAL, 0x200, 5,0,0,
         //Death
         14, 12, 2, 2, ANIMATION_NORMAL, 0x200, 5,0,0,
         //Fire
         19, 12, 2, 2, ANIMATION_BACK, 0, 5,0,0,

         //OBJECT_SNOWBAL

         //Appearance
         7, 7, 1, 1, ANIMATION_FREEZE, 0x470, 1,0,0,
         //Left
         10, 10, 3, 1, ANIMATION_NORMAL, 0, 5,0,0,
         //Right
         0, 0, 0, 0, 0, 0, 5,0,0,
         //Jump
         0, 0, 0, 0, 0, 0, 5,0,0,
         //Cower
         0, 0, 0, 0, 0, 0, 5,0,0,
         //Death
         0, 0, 0, 0, 0, 0, 5,0,0,
         //Fire
         0, 0, 0, 0, 0, 0, 5,0,0,

         //OBJECT_SMOKE

          //Appearance
          12, 5, 2, 1, ANIMATION_NORMAL, 2, 1,0,0,
          //Left
          17, 9, 3, 1, ANIMATION_NORMAL, 2, 1,0,0,
          //Right
          17, 9, 3, 1, ANIMATION_NORMAL, 2, 1,0,0,
          //Jump
          0, 0, 0, 0, 0, 0, 5,0,0,
          //Cower
          0, 0, 0, 0, 0, 0, 5,0,0,
          //Death
          17, 9, 3, 2, ANIMATION_NORMAL, 1, 1,0,0,
          //Fire
          0, 0, 0, 0, 0, 0, 5,0,0,

         //OBJECT_UFO

         //Appearance
         0, 0, 0, 0, 0, 0, 5,0,0,
         //Left
         26, 16, 2, 2, ANIMATION_NORMAL, 0x400, 5,0,0,
         //Right
         26, 16, 2, 2, ANIMATION_NORMAL, 0x400, 5,0,0,
         //Jump
         0, 0, 0, 0, 0, 0, 5,0,0,
         //Cower
         0, 0, 0, 0, 0, 0, 5,0,0,
         //Death
         26, 20, 3, 1, ANIMATION_FREEZE, 0x400, 5,0,0,
         //Fire
         0, 0, 0, 0, 0, 0, 5,0,0,

         //OBJECT_WITCH

         //Appearance
         0, 0, 0, 0, 0, 0, 5,0,0,
         //Left
         26, 21, 2, 2, ANIMATION_NORMAL, 0x300, 5,0,0,
         //Right
         26, 21, 2, 2, ANIMATION_NORMAL, 0x300, 5,0,0,
         //Jump
         0, 0, 0, 0, 0, 0, 5,0,0,
         //Cower
         0, 0, 0, 0, 0, 0, 5,0,0,
         //Death
         26, 21, 1, 1, ANIMATION_FREEZE, 0x300, 5,0,0,
         //Fire
         0, 0, 0, 0, 0, 0, 5,0,0,

         //OBJECT_ICECREAM

         //Appearance
         //Left
         10, 10, 3, 1, ANIMATION_BACK, 0, 5,0,0,
         0, 0, 0, 0, 0, 0, 5,0,0,
         //Right
         0, 0, 0, 0, 0, 0, 5,0,0,
         //Jump
         0, 0, 0, 0, 0, 0, 5,0,0,
         //Cower
         0, 0, 0, 0, 0, 0, 5,0,0,
         //Death
         0, 0, 0, 0, 0, 0, 5,0,0,
         //Fire
         0, 0, 0, 0, 0, 0, 5,0,0,

         //OBJECT_MOON

          //Appearance
          22, 43, 0, 0, ANIMATION_FREEZE, 0, 5,8,0,
          //Left
          22, 43, 0, 0, ANIMATION_FREEZE, 0, 5,8,0,
          //Right
          22, 43, 0, 0, ANIMATION_FREEZE, 0, 5,8,0,
          //Jump
          0, 0, 0, 0, 0, 0, 5,0,0,
          //Cower
          0, 0, 0, 0, 0, 0, 5,0,0,
          //Death
          0, 0, 0, 0, 0, 0, 5,0,0,
          //Fire
          22, 43, 2, 2, ANIMATION_NORMAL, 0, 5,0,0,

         //OBJECT_FIRE

          //Appearance
          10, 10, 2, 1, ANIMATION_NORMAL, 0x200, 15,3,3,
          //Left
          0, 0, 0, 0, 0, 0, 5,0,0,
          //Right
          0, 0, 0, 0, 0, 0, 5,0,0,
          //Jump
          0, 0, 0, 0, 0, 0, 5,0,0,
          //Cower
          0, 0, 0, 0, 0, 0, 5,0,0,
          //Death
          0, 0, 0, 0, 0, 0, 5,0,0,
          //Fire
          10, 10, 2, 1, ANIMATION_NORMAL, 0, 2,3,3
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

    protected int i_leftOffset,i_rightOffset;
    protected int i8_leftOffset,i8_rightOffset;

    public int i_ObjectDirection;

    protected int i_Param0,i_Param1,i_Param2;
    protected long l_mask;

    protected boolean checkCollide(AnimeObject _object)
    {
        int i_x = _object.i8_ScreenX + _object.i8_leftOffset;
        int i_y = _object.i8_ScreenY;
        int i_w = _object.i8_Width - _object.i8_leftOffset - _object.i8_rightOffset;
        int i_h = _object.i8_Height;

        int i_cx = i8_ScreenX+i8_leftOffset;
        int i_cy = i8_ScreenY;
        int i_cw = i8_Width - i8_leftOffset - i8_rightOffset;
        int i_ch = i8_Height;

        if (!((i_x + i_w <= i_cx) || (i_y + i_h <= i_cy) || (i_x >= i_cw + i_cx) || (i_y >= i_ch + i_cy)))
            return true;
        else
            return false;
    }

    public AnimeObject()
    {
        lg_Active = false;
    }

    public void initObject(int _type)
    {
        i_State = -1;
        i_ObjectDirection = -1;
        i_Type = _type;
        lg_Active = true;
        initState(STATE_APPEARANCE);
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

        int i_offst = (63 * i_Type) + 9 * _state;

        i_Width = ai_AnimationValues[i_offst++];
        l_mask = GameletImpl.al_maskarray[i_Width];
        i8_Width = i_Width << 8;
        i_Height = ai_AnimationValues[i_offst++];
        i8_Height = i_Height << 8;
        i_MaxFrames = ai_AnimationValues[i_offst++];
        i_MaxDelay = ai_AnimationValues[i_offst++];

        b_animationType = (byte) ai_AnimationValues[i_offst++];

        i8_Speed = ai_AnimationValues[i_offst++];

        int i_energy = ai_AnimationValues[i_offst++];
        if (_state == 0) i_Energy = i_energy;

        if (i_ObjectDirection == STATE_RIGHT)
        {
            i_leftOffset = ai_AnimationValues[i_offst++];
            i8_leftOffset = i_leftOffset<<8;

            i_rightOffset = ai_AnimationValues[i_offst++];
            i8_rightOffset = i_rightOffset<<8;
        }
        else
        {
            i_rightOffset = ai_AnimationValues[i_offst++];
            i8_rightOffset = i_rightOffset<<8;

            i_leftOffset = ai_AnimationValues[i_offst++];
            i8_leftOffset = i_leftOffset<<8;
        }

        i_Frame = 0;
        i_delay = i_MaxDelay;
        i8_halfWidth = (i_Width << 8) >> 1;
        i8_halfHeight = (i_Height << 8) >> 1;

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
                };break;
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
