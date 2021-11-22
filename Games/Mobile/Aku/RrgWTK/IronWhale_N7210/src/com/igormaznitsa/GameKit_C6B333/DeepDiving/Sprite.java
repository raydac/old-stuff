package com.igormaznitsa.GameKit_C6B333.DeepDiving;

public class Sprite
{
    public static final byte SPRITE_SUBMARINE = 0;
    public static final byte SPRITE_SUFRACESHIP = 1;
    public static final byte SPRITE_CHOPPERLEFT = 2;
    public static final byte SPRITE_ICEBERG = 3;
    public static final byte SPRITE_MINE = 4;
    public static final byte SPRITE_SURFACEBOMB = 5;
    public static final byte SPRITE_UNDERWATERBOMB = 6;
    public static final byte SPRITE_TORPEDO = 7;
    public static final byte SPRITE_ROCKET = 8;
    public static final byte SPRITE_AIRCRAFTCARRIER = 9;
    public static final byte SPRITE_EXPLOSIONUNDERWATER = 10;
    public static final byte SPRITE_EXPLOSIONSURFACE = 11;
    public static final byte SPRITE_EXPLOSIONAIR = 12;
    public static final byte SPRITE_BLEBS = 13;
    public static final byte SPRITE_CHOPPERRIGHT = 14;

    public static final byte SPRITE_SUNKSHIP1 = 15;
    public static final byte SPRITE_SUNKSHIP2 = 16;
    public static final byte SPRITE_SUNKSHIP3 = 17;
    public static final byte SPRITE_SUNKSHIP4 = 18;

    // Степень погружения объекта
    int i8_MaxSurfacing;
    // Маска вертикальная маска объекта
    long l_Mask;
    // ТИп объекта отображаемого спрайтом
    public int i_objectType;
    // Настраиваемый параметр
    public int i_param0,i_param1;

    public int i_SpriteID;

    public void loadSprite(int _type)
    {
        i_objectType = _type;
        int i_offset = 11 * _type;

        int i_wdth = ai_AnimationArray[i_offset++];
        int i_hght = ai_AnimationArray[i_offset++];
        int i_sensex = ai_AnimationArray[i_offset++];
        int i_sensey = ai_AnimationArray[i_offset++];
        int i_sensew = ai_AnimationArray[i_offset++];
        int i_senseh = ai_AnimationArray[i_offset++];
        int i_frames = ai_AnimationArray[i_offset++];
        int i_delay = ai_AnimationArray[i_offset++];
        int i_animtype = ai_AnimationArray[i_offset++];
        i8_MaxSurfacing = ai_AnimationArray[i_offset++];
        l_Mask = ((long)ai_AnimationArray[i_offset]) << (32 + 16);

        int i_algn;
        switch (_type)
        {
            case SPRITE_EXPLOSIONAIR :
            case SPRITE_EXPLOSIONUNDERWATER :
            case SPRITE_ROCKET :
            case SPRITE_TORPEDO : i_algn = SPRITE_ALIGN_CENTER | SPRITE_ALIGN_CENTER; break;
            case SPRITE_MINE : i_algn = SPRITE_ALIGN_LEFT | SPRITE_ALIGN_DOWN; break;
            case SPRITE_EXPLOSIONSURFACE : i_algn = SPRITE_ALIGN_DOWN | SPRITE_ALIGN_CENTER; break;
            default : i_algn = SPRITE_ALIGN_TOP | SPRITE_ALIGN_LEFT;
        }

        setAnimation(i_animtype,i_algn,i_wdth,i_hght,i_frames,0,i_delay);
        setCollisionBounds(i_sensex,i_sensey,i_sensew,i_senseh);

        lg_SpriteActive = true;
    }

//==================================================================
    public static final byte ANIMATION_CYCLIC = 0;
    public static final byte ANIMATION_FROZEN = 1;
    public static final byte ANIMATION_PENDULUM = 2;
    public static final byte SPRITE_ALIGN_CENTER = 0;
    public static final byte SPRITE_ALIGN_LEFT = 1;
    public static final byte SPRITE_ALIGN_RIGHT = 2;
    public static final byte SPRITE_ALIGN_TOP = 0x10;
    public static final byte SPRITE_ALIGN_DOWN = 0x20;
    protected int i_animType;
    public int i8_width,i8_height;
    protected int i8_mainX, i8_mainY;
    protected int i_maxFrames;
    protected int i_maxTimeDelay;
    protected int i8_offsetX,i8_offsetY;
    protected boolean lg_backMove;
    protected int i8_col_offsetX;
    protected int i8_col_offsetY;
    protected int i8_col_width;
    protected int i8_col_height;
    public boolean lg_SpriteActive;
    public int i8_ScreenX,i8_ScreenY;
    public int i_Frame;
    public int i_Delay;
    public int i_Align;
//====================================================================
    public Sprite(int _id)
    {
        lg_SpriteActive = false;
        i_SpriteID = _id;
    }

    public static final short [] ai_AnimationArray = new short[]
    {
        // Width, Height, Sense X, Sense Y, Sense width, Sense height, Frame, Animation delay, AnimationType, Max Surfacing, Mask
        //SPRITE_SUBMARINE
        30,14,3,3,26,10,2,1,ANIMATION_CYCLIC,0x500,(short)0xFFC0,
        //SPRITE_SUFRACESHIP
        30,15,0,0,30,15,2,1,ANIMATION_CYCLIC,0xC00,(short)0xFFC0,
        //SPRITE_CHOPPERLEFT
        29,15,0,0,29,15,4,1,ANIMATION_CYCLIC,3,(short)0xFFC0,
        //SPRITE_ICEBERG
        25,19,0,0,25,19,1,1,ANIMATION_CYCLIC,0x800,(short)0xFFC0,
        //SPRITE_MINE
        10, 10,0,0,10,10,1,1,ANIMATION_CYCLIC,0x200,(short)0xF000,
        //SPRITE_SURFACEBOMB
        9,13,0,0,9,13,1,1,ANIMATION_CYCLIC,0xA00,(short)0xF800,
        //SPRITE_UNDERWATERBOMB
        7,7,0,0,7,7,1,1,ANIMATION_CYCLIC,3,(short)0xF800,
        //SPRITE_TORPEDO
        10,5,0,0,10,5,1,1,ANIMATION_CYCLIC,3,(short)0xF800,
        //SPRITE_ROCKET
        9,9,0,0,9,9,1,1,ANIMATION_CYCLIC,3,(short)0xFFC0,
        //SPRITE_AIRCRAFTCARRIER
        41,19,0,0,41,19,2,1,ANIMATION_CYCLIC,0xF00,(short)0xFFC0,
        //SPRITE_EXPLOSIONUNDERWATER
        16,16,0,0,16,16,7,3,ANIMATION_CYCLIC,3,(short)0xF000,
        //SPRITE_EXPLOSIONSURFACE
        20,30,0,0,20,30,6,3,ANIMATION_CYCLIC,3,(short)0xF000,
        //SPRITE_EXPLOSIONAIR
        26,27,0,0,26,27,5,2,ANIMATION_CYCLIC,3,(short)0xF000,
        //SPRITE_BLEBS
        7,10,0,0,7,10,4,2,ANIMATION_CYCLIC,3,(short)0xF000,
        //SPRITE_CHOPPERRIGHT
        29,15,0,0,29,15,4,1,ANIMATION_CYCLIC,3,(short)0xFFC0,
        //SPRITE_SUNKSHIP1
        10,12,0,0,10,12,4,2,ANIMATION_CYCLIC,3,(short)0xF000,
        //SPRITE_SUNKSHIP2
        32,20,0,0,32,20,1,1,ANIMATION_CYCLIC,3,(short)0xF000,
        //SPRITE_SUNKSHIP3
        32,16,0,0,32,16,1,1,ANIMATION_CYCLIC,3,(short)0xF000,
        //SPRITE_SUNKSHIP4
        32,22,0,0,32,22,1,1,ANIMATION_CYCLIC,3,(short)0xF000
    };

    public void setCollisionBounds(int _offX,int _offY,int _width,int _height)
    {
        i8_col_offsetX = _offX<<8;
        i8_col_offsetY = _offY<<8;
        i8_col_width = _width<<8;
        i8_col_height = _height<<8;
    }

    public void setAnimation(int _animationType, int _alignType, int _frameWidth, int _frameHeight, int _maxFrames, int _initFrame, int _timeDelay)
    {
        i_animType = _animationType;
        i8_width = _frameWidth<<8;
        i8_height = _frameHeight<<8;
        i_maxFrames = _maxFrames;
        i_maxTimeDelay = _timeDelay;

        i8_col_width = i8_width;
        i8_col_height = i8_height;

        i_Delay = 0;
        i_Frame = _initFrame;

        lg_backMove = false;

	i_Align = _alignType;

        switch (_alignType & 0x0F)
        {
            case SPRITE_ALIGN_CENTER:
                {
                    i8_offsetX = 0 - (i8_width >>> 1);
                }
                ;
                break;
            case SPRITE_ALIGN_RIGHT:
                {
                    i8_offsetX = 0 - i8_width;
                }
                ;
                break;
            case SPRITE_ALIGN_LEFT:
                {
                    i8_offsetX = 0;
                }
                ;
                break;
        }

        switch (_alignType & 0xF0)
        {
            case SPRITE_ALIGN_CENTER:
                {
                    i8_offsetY = 0 - (i8_height>>>1);
                }
                ;
                break;
            case SPRITE_ALIGN_TOP:
                {
                    i8_offsetY = 0;
                }
                ;
                break;
            case SPRITE_ALIGN_DOWN:
                {
                    i8_offsetY = 0 - i8_height ;
                }
                ;
                break;
        }
    }

    public boolean isCollided(Sprite _sprite)
    {
         int i_sX = i8_ScreenX+i8_col_offsetX;
         int i_sY = i8_ScreenY+i8_col_offsetY;
         int i_sw = i8_col_width;
         int i_sh = i8_col_height;

         int i_dX = _sprite.i8_ScreenX+_sprite.i8_col_offsetX;
         int i_dY = _sprite.i8_ScreenY+_sprite.i8_col_offsetY;
         int i_dw = _sprite.i8_col_width;
         int i_dh = _sprite.i8_col_height;

         if ((i_sX+i_sw) < i_dX || i_sX > (i_dX+i_dw)  || (i_sY+i_sh) < i_dY || i_sY >(i_dY+i_dh))
             return false;
         else
             return true;
    }

    public boolean processAnimation()
    {
        i_Delay ++;
        if (i_Delay<i_maxTimeDelay) return false;
        i_Delay = 0;

        switch (i_animType)
        {
            case ANIMATION_CYCLIC:
                {
                    i_Frame ++;
                    if (i_Frame>=i_maxFrames)
                    {
                        i_Frame = 0;
                        return true;
                    }
                    else
                    {
                        return false;
                    }
                }
            case ANIMATION_FROZEN:
                {
                    i_Frame ++;
                    if (i_Frame>=i_maxFrames)
                    {
                        i_Frame = i_maxFrames-1;
                        return true;
                    }
                    else
                    {
                        return false;
                    }
                }
            case ANIMATION_PENDULUM:
                {
                    if (lg_backMove)
                    {
                        i_Frame --;
                        if (i_Frame==0)
                        {
                            lg_backMove = false;
                            return true;
                        }
                        else
                            return false;
                    }
                    else
                    {
                        i_Frame ++;
                        if (i_Frame==i_maxFrames-1)
                        {
                            lg_backMove = true;
                            return true;
                        }
                        else
                            return false;
                    }
                }
        }
        return false;
    }

    public void setMainXY(int _newX, int _newY)
    {
        setMainXY8(_newX << 8, _newY << 8);
    }

    public void setMainXY8(int _newX8, int _newY8)
    {
        i8_mainX = _newX8;
        i8_mainY = _newY8;
        i8_ScreenX = i8_mainX + i8_offsetX;
        i8_ScreenY = i8_mainY + i8_offsetY;
    }
}
