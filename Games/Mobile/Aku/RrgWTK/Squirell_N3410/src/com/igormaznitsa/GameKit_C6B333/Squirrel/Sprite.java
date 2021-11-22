package com.igormaznitsa.GameKit_C6B333.Squirrel;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.DataInputStream;

public class Sprite
{
    public static final int OBJECT_SQUIRREL = 0;
    public static final int OBJECT_LOGGER = 1;
    public static final int OBJECT_OWL = 2;
    public static final int OBJECT_NUT = 3;
    public static final int OBJECT_CATERPILLAR = 4;
    public static final int OBJECT_BEE = 5;
    public static final int OBJECT_BEETLE = 6;
    public static final int OBJECT_HUNTER = 7;
    public static final int OBJECT_BULLET = 8;

    // Идти влево
    public static final int STATE_GOLEFT = 0;
    // Идти вправо
    public static final int STATE_GORIGHT = 1;
    // лезть вверх
    public static final int STATE_GOTOP = 2;
    // лезть вниз
    public static final int STATE_GODOWN = 3;
    // смерть
    public static final int STATE_DEATH = 4;
    // бросок
    public static final int STATE_DROP = 5;
    // удар/прыжок влево
    public static final int STATE_BEATJUMPLEFT = 6;
    // удар/прыжок вправо
    public static final int STATE_BEATJUMPRIGHT = 7;
    // проявление
    public static final int STATE_APPEARANCE = 8;
    // попадание в объект
    public static final int STATE_UNDERFIRE = 9;

    public int i_SpriteID;
    public int i_objectType;
    public int i_objectState;
    public int i_param0,i_param1;
    protected int i_initcellX, i_initcellY;
    public int i_previousState;
    public int i_ObjectHealth;


// commented by Sandy
//
    private final static int COORD_SHIFT = 3;
//
// Purpose: convert fix.point to short value
//
// Description:
// shifting on several bits saving us a 4 bytes per every sprite at storage,
// 4 little bits contains a low part of fixed point
// This decision does some precision loss, but it isn't so much necessary
// Calculation:
//      MaxShortValue is +32768
//      number shifting of bits = Log2(
//                                      Ceil(
//                                             VIRTUALCELL_WIDTH * Max( STAGE_WIDTH,STAGE_HEIGHT ) * 256 / MaxShortValue
//                                           )
//                                    )
// Current value: Log2(Ceil( 16*60*256/32768 )) = Log2(Ceil(7,5)) = 3
//

    public void loadSprite(int _type,int _state)
    {
        i_objectType = _type;
        initState(_state);
        lg_SpriteActive = true;
    }

    protected void writeSpriteToOutputStream(DataOutputStream _dos) throws IOException
    {
        _dos.writeByte(i_objectType);
        _dos.writeByte(i_objectState);
        _dos.writeShort(i8_mainX>>COORD_SHIFT);  // * see comment at definition of COORD_SHIFT
        _dos.writeShort(i8_mainY>>COORD_SHIFT);
        _dos.writeByte(i_previousState);
        _dos.writeByte(i_param0);      // can be -1..+1
        _dos.writeShort(i_param1);     // see a caterpillar properties
        _dos.writeByte(i_Frame);
        _dos.writeByte(i_Delay);
        _dos.writeBoolean(lg_backMove);
        _dos.writeShort(i_ObjectHealth);
        _dos.writeByte(i_initcellX);
	_dos.writeByte(i_initcellY);
        _dos.writeBoolean(lg_SpriteActive);
    }

    protected void loadSpriteFromStream(DataInputStream _dis) throws IOException
    {
        int i_t = _dis.readUnsignedByte();
        int i_s = _dis.readUnsignedByte();
        i8_mainX = ((int)_dis.readShort())<<COORD_SHIFT; // * see comment at definition of COORD_SHIFT
        i8_mainY = ((int)_dis.readShort())<<COORD_SHIFT;
        loadSprite(i_t,i_s);
        i_previousState = _dis.readUnsignedByte();
        i_param0 = _dis.readByte();            // can be -1..+1
        i_param1 = _dis.readShort();           // see a caterpillar properties
        i_Frame = _dis.readUnsignedByte();
        i_Delay = _dis.readUnsignedByte();
        lg_backMove = _dis.readBoolean();
        i_ObjectHealth = _dis.readShort();
        i_initcellX = _dis.readUnsignedByte();
        i_initcellY = _dis.readUnsignedByte();
        lg_SpriteActive = _dis.readBoolean();
    }

    public void initState(int _state)
    {
        i_objectState = _state;
        int i_offset = i_objectType * 100 +10 * i_objectState;

        int i_wdth = ai_AnimationArray[i_offset++];
        int i_hght = ai_AnimationArray[i_offset++];
        int i_sensex = ai_AnimationArray[i_offset++];
        int i_sensey = ai_AnimationArray[i_offset++];
        int i_sensew = ai_AnimationArray[i_offset++];
        int i_senseh = ai_AnimationArray[i_offset++];
        int i_frames = ai_AnimationArray[i_offset++];
        int i_delay = ai_AnimationArray[i_offset++];
        int i_animtype = ai_AnimationArray[i_offset++];
        int i_aligntype = ai_AnimationArray[i_offset];

        setAnimation(i_animtype,i_aligntype,i_wdth,i_hght,i_frames,0,i_delay);
        setCollisionBounds(i_sensex,i_sensey,i_sensew,i_senseh);
        setMainXY8(i8_mainX,i8_mainY);

    }

//==================================================================
    public static final int ANIMATION_CYCLIC = 0;
    public static final int ANIMATION_FROZEN = 1;
    public static final int ANIMATION_PENDULUM = 2;
    public static final int SPRITE_ALIGN_CENTER = 0;
    public static final int SPRITE_ALIGN_LEFT = 1;
    public static final int SPRITE_ALIGN_RIGHT = 2;
    public static final int SPRITE_ALIGN_TOP = 0x10;
    public static final int SPRITE_ALIGN_DOWN = 0x20;
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
//====================================================================
    public Sprite(int _id)
    {
        lg_SpriteActive = false;
        i_SpriteID = _id;
    }

    public static final int [] ai_AnimationArray = new int[]
    {
        // Width, Height, Sense X, Sense Y, Sense width, Sense height, Frame, Animation delay, AnimationType, Sprite align
        //OBJECT_SQUIRREL = 0;
        //STATE_GOLEFT = 0;
        16,10,0,0,16,10,4,1,ANIMATION_CYCLIC,SPRITE_ALIGN_DOWN | SPRITE_ALIGN_RIGHT,
        //STATE_GORIGHT = 1;
        16,10,0,0,16,10,4,1,ANIMATION_CYCLIC,SPRITE_ALIGN_DOWN | SPRITE_ALIGN_LEFT,
        //STATE_GOTOP = 2;
        10,16,0,0,10,16,4,1,ANIMATION_CYCLIC,SPRITE_ALIGN_DOWN | SPRITE_ALIGN_CENTER,
        //STATE_GODOWN = 3;
        10,16,0,0,10,16,4,1,ANIMATION_CYCLIC,SPRITE_ALIGN_TOP | SPRITE_ALIGN_CENTER,
        //STATE_DEATH = 4;
        16,10,0,0,16,10,4,1,ANIMATION_FROZEN,SPRITE_ALIGN_DOWN | SPRITE_ALIGN_CENTER,
        //STATE_DROP = 5;
        10,16,0,0,10,16,1,1,ANIMATION_FROZEN,SPRITE_ALIGN_DOWN | SPRITE_ALIGN_CENTER,
        //STATE_BEATJUMPLEFT = 6;
        16,10,0,0,16,10,2,1,ANIMATION_FROZEN,SPRITE_ALIGN_DOWN | SPRITE_ALIGN_RIGHT,
        //STATE_BEATJUMPRIGHT = 7;
        16,10,0,0,16,10,2,1,ANIMATION_FROZEN,SPRITE_ALIGN_DOWN | SPRITE_ALIGN_LEFT,
        //STATE_APPEARANCE = 8;
        11,11,0,0,11,11,4,3,ANIMATION_FROZEN,SPRITE_ALIGN_CENTER | SPRITE_ALIGN_CENTER,
        //STATE_UNDERFIRE = 9;
        16,10,0,0,16,10,1,3,ANIMATION_CYCLIC,SPRITE_ALIGN_DOWN | SPRITE_ALIGN_CENTER,

        //OBJECT_LOGGER = 1;
        //STATE_GOLEFT = 0;
        10,15,0,0,10,15,3,3,ANIMATION_CYCLIC,SPRITE_ALIGN_DOWN | SPRITE_ALIGN_CENTER,
        //STATE_GORIGHT = 1;
        10,15,0,0,10,15,3,3,ANIMATION_CYCLIC,SPRITE_ALIGN_DOWN | SPRITE_ALIGN_CENTER,
        //STATE_GOTOP = 2;
        0,0,0,0,0,0,0,0,0,0,
        //STATE_GODOWN = 3;
        0,0,0,0,0,0,0,0,0,0,
        //STATE_DEATH = 4;
        15,15,0,0,15,15,3,3,ANIMATION_FROZEN,SPRITE_ALIGN_DOWN | SPRITE_ALIGN_CENTER,
        //STATE_DROP = 5;
        0,0,0,0,0,0,0,0,0,0,
        //STATE_BEATJUMPLEFT = 6;
        20,15,0,0,20,15,3,3,ANIMATION_PENDULUM,SPRITE_ALIGN_DOWN | SPRITE_ALIGN_CENTER,
        //STATE_BEATJUMPRIGHT = 7;
        20,15,0,0,20,15,3,3,ANIMATION_PENDULUM,SPRITE_ALIGN_DOWN | SPRITE_ALIGN_CENTER,
        //STATE_APPEARANCE = 8;
        11,11,0,0,11,11,3,4,ANIMATION_FROZEN,SPRITE_ALIGN_DOWN | SPRITE_ALIGN_CENTER,
        //STATE_UNDERFIRE = 9;
        11,11,0,0,11,11,3,3,ANIMATION_FROZEN,SPRITE_ALIGN_DOWN | SPRITE_ALIGN_CENTER,

        //OBJECT_OWL = 2;
        //STATE_GOLEFT = 0;
        10,13,0,0,10,13,1,1,ANIMATION_FROZEN,SPRITE_ALIGN_DOWN | SPRITE_ALIGN_CENTER,
        //STATE_GORIGHT = 1;
        10,13,0,0,10,13,1,1,ANIMATION_FROZEN,SPRITE_ALIGN_DOWN | SPRITE_ALIGN_CENTER,
        //STATE_GOTOP = 2;
        10,13,0,0,10,13,1,1,ANIMATION_FROZEN,SPRITE_ALIGN_DOWN | SPRITE_ALIGN_CENTER,
        //STATE_GODOWN = 3;
        10,13,0,0,10,13,1,1,ANIMATION_FROZEN,SPRITE_ALIGN_DOWN | SPRITE_ALIGN_CENTER,
        //STATE_DEATH = 4;
        10,13,0,0,10,13,2,2,ANIMATION_FROZEN,SPRITE_ALIGN_DOWN | SPRITE_ALIGN_CENTER,
        //STATE_DROP = 5;
        10,13,0,0,10,13,1,1,ANIMATION_FROZEN,SPRITE_ALIGN_DOWN | SPRITE_ALIGN_CENTER,
        //STATE_BEATJUMPLEFT = 6;
        10,13,0,0,10,13,1,4,ANIMATION_PENDULUM,SPRITE_ALIGN_DOWN | SPRITE_ALIGN_CENTER,
        //STATE_BEATJUMPRIGHT = 7;
        10,13,0,0,10,13,1,4,ANIMATION_PENDULUM,SPRITE_ALIGN_DOWN | SPRITE_ALIGN_CENTER,
        //STATE_APPEARANCE = 8;
        11,11,0,0,11,11,4,2,ANIMATION_FROZEN,SPRITE_ALIGN_DOWN | SPRITE_ALIGN_CENTER,
        //STATE_UNDERFIRE = 9;
        10,13,0,0,10,13,1,1,ANIMATION_FROZEN,SPRITE_ALIGN_DOWN | SPRITE_ALIGN_CENTER,

        //OBJECT_NUT = 3;
        //STATE_GOLEFT = 0;
        7,7,0,0,7,7,1,1,ANIMATION_CYCLIC,SPRITE_ALIGN_DOWN | SPRITE_ALIGN_CENTER,
        //STATE_GORIGHT = 1;
        7,7,0,0,7,7,1,1,ANIMATION_CYCLIC,SPRITE_ALIGN_DOWN | SPRITE_ALIGN_CENTER,
        //STATE_GOTOP = 2;
        7,7,0,0,7,7,1,1,ANIMATION_CYCLIC,SPRITE_ALIGN_DOWN | SPRITE_ALIGN_CENTER,
        //STATE_GODOWN = 3;
        7,7,0,0,7,7,1,1,ANIMATION_CYCLIC,SPRITE_ALIGN_DOWN | SPRITE_ALIGN_CENTER,
        //STATE_DEATH = 4;
        7,7,0,0,7,7,1,1,ANIMATION_FROZEN,SPRITE_ALIGN_DOWN | SPRITE_ALIGN_CENTER,
        //STATE_DROP = 5;
        7,7,0,0,7,7,1,1,ANIMATION_CYCLIC,SPRITE_ALIGN_DOWN | SPRITE_ALIGN_CENTER,
        //STATE_BEATJUMPLEFT = 6;
        0,0,0,0,0,0,0,0,0,0,
        //STATE_BEATJUMPRIGHT = 7;
        0,0,0,0,0,0,0,0,0,0,
        //STATE_APPEARANCE = 8;
        7,7,0,0,7,7,4,1,ANIMATION_FROZEN,SPRITE_ALIGN_DOWN | SPRITE_ALIGN_CENTER,
        //STATE_UNDERFIRE = 9;
        0,0,0,0,0,0,0,0,0,0,

        //OBJECT_CATERPILLAR = 4;
        //STATE_GOLEFT = 0;
        12,10,0,0,12,10,3,3,ANIMATION_CYCLIC,SPRITE_ALIGN_DOWN | SPRITE_ALIGN_CENTER,
        //STATE_GORIGHT = 1;
        12,10,0,0,12,10,3,3,ANIMATION_CYCLIC,SPRITE_ALIGN_DOWN | SPRITE_ALIGN_CENTER,
        //STATE_GOTOP = 2;
        0,0,0,0,0,0,0,0,0,0,
        //STATE_GODOWN = 3;
        0,0,0,0,0,0,0,0,0,0,
        //STATE_DEATH = 4;
        12,10,0,0,12,10,1,1,ANIMATION_CYCLIC,SPRITE_ALIGN_DOWN | SPRITE_ALIGN_CENTER,
        //STATE_DROP = 5;
        0,0,0,0,0,0,0,0,0,0,
        //STATE_BEATJUMPLEFT = 6;
        12,10,0,0,12,10,1,4,ANIMATION_PENDULUM,SPRITE_ALIGN_DOWN | SPRITE_ALIGN_CENTER,
        //STATE_BEATJUMPRIGHT = 7;
        12,10,0,0,12,10,1,4,ANIMATION_PENDULUM,SPRITE_ALIGN_DOWN | SPRITE_ALIGN_CENTER,
        //STATE_APPEARANCE = 8;
        11,11,0,0,11,11,4,1,ANIMATION_FROZEN,SPRITE_ALIGN_DOWN | SPRITE_ALIGN_CENTER,
        //STATE_UNDERFIRE = 9;
        0,0,0,0,0,0,0,0,0,0,

        //OBJECT_BEE = 5;
        //STATE_GOLEFT = 0;
        12,10,0,0,12,10,1,1,ANIMATION_CYCLIC,SPRITE_ALIGN_DOWN | SPRITE_ALIGN_LEFT,
        //STATE_GORIGHT = 1;
        12,10,0,0,12,10,1,1,ANIMATION_CYCLIC,SPRITE_ALIGN_DOWN | SPRITE_ALIGN_LEFT,
        //STATE_GOTOP = 2;
        0,0,0,0,0,0,0,0,0,0,
        //STATE_GODOWN = 3;
        0,0,0,0,0,0,0,0,0,0,
        //STATE_DEATH = 4;
        12,10,0,0,12,10,1,1,ANIMATION_CYCLIC,SPRITE_ALIGN_DOWN | SPRITE_ALIGN_LEFT,
        //STATE_DROP = 5;
        0,0,0,0,0,0,0,0,0,0,
        //STATE_BEATJUMPLEFT = 6;
        12,10,0,0,12,10,1,4,ANIMATION_PENDULUM,SPRITE_ALIGN_DOWN | SPRITE_ALIGN_LEFT,
        //STATE_BEATJUMPRIGHT = 7;
        12,10,0,0,12,10,1,4,ANIMATION_PENDULUM,SPRITE_ALIGN_DOWN | SPRITE_ALIGN_LEFT,
        //STATE_APPEARANCE = 8;
        11,11,0,0,11,11,4,1,ANIMATION_CYCLIC,SPRITE_ALIGN_DOWN | SPRITE_ALIGN_LEFT,
        //STATE_UNDERFIRE = 9;
        0,0,0,0,0,0,0,0,0,0,

        //OBJECT_BEETLE = 6;
        //STATE_GOLEFT = 0;
        12,10,0,0,12,10,1,1,ANIMATION_CYCLIC,SPRITE_ALIGN_DOWN | SPRITE_ALIGN_LEFT,
        //STATE_GORIGHT = 1;
        12,10,0,0,12,10,1,1,ANIMATION_CYCLIC,SPRITE_ALIGN_DOWN | SPRITE_ALIGN_LEFT,
        //STATE_GOTOP = 2;
        10,12,0,0,10,12,1,1,ANIMATION_CYCLIC,SPRITE_ALIGN_DOWN | SPRITE_ALIGN_LEFT,
        //STATE_GODOWN = 3;
        10,12,0,0,10,12,1,1,ANIMATION_CYCLIC,SPRITE_ALIGN_DOWN | SPRITE_ALIGN_LEFT,
        //STATE_DEATH = 4;
        12,10,0,0,12,10,1,1,ANIMATION_CYCLIC,SPRITE_ALIGN_DOWN | SPRITE_ALIGN_LEFT,
        //STATE_DROP = 5;
        0,0,0,0,0,0,0,0,0,0,
        //STATE_BEATJUMPLEFT = 6;
        12,10,0,0,12,10,1,4,ANIMATION_PENDULUM,SPRITE_ALIGN_DOWN | SPRITE_ALIGN_LEFT,
        //STATE_BEATJUMPRIGHT = 7;
        12,10,0,0,12,10,1,4,ANIMATION_PENDULUM,SPRITE_ALIGN_DOWN | SPRITE_ALIGN_LEFT,
        //STATE_APPEARANCE = 8;
        11,11,0,0,11,11,4,2,ANIMATION_CYCLIC,SPRITE_ALIGN_DOWN | SPRITE_ALIGN_LEFT,
        //STATE_UNDERFIRE = 9;
        0,0,0,0,0,0,0,0,0,0,

        //OBJECT_HUNTER = 7;
        //STATE_GOLEFT = 0;
        16,16,0,0,16,16,1,1,ANIMATION_CYCLIC,SPRITE_ALIGN_DOWN | SPRITE_ALIGN_CENTER,
        //STATE_GORIGHT = 1;
        16,16,0,0,16,16,1,1,ANIMATION_CYCLIC,SPRITE_ALIGN_DOWN | SPRITE_ALIGN_CENTER,
        //STATE_GOTOP = 2;
        0,0,0,0,0,0,0,0,0,0,
        //STATE_GODOWN = 3;
        0,0,0,0,0,0,0,0,0,0,
        //STATE_DEATH = 4;
        16,16,0,0,16,16,1,1,ANIMATION_FROZEN,SPRITE_ALIGN_DOWN | SPRITE_ALIGN_CENTER,
        //STATE_DROP = 5;
        16,16,0,0,16,16,1,1,ANIMATION_FROZEN,SPRITE_ALIGN_DOWN | SPRITE_ALIGN_CENTER,
        //STATE_BEATJUMPLEFT = 6;
        16,16,0,0,16,16,1,4,ANIMATION_PENDULUM,SPRITE_ALIGN_DOWN | SPRITE_ALIGN_CENTER,
        //STATE_BEATJUMPRIGHT = 7;
        16,16,0,0,16,16,1,4,ANIMATION_PENDULUM,SPRITE_ALIGN_DOWN | SPRITE_ALIGN_CENTER,
        //STATE_APPEARANCE = 8;
        11,11,0,0,11,11,4,3,ANIMATION_FROZEN,SPRITE_ALIGN_DOWN | SPRITE_ALIGN_CENTER,
        //STATE_UNDERFIRE = 9;
        16,16,0,0,16,16,1,1,ANIMATION_FROZEN,SPRITE_ALIGN_DOWN | SPRITE_ALIGN_CENTER,

        //OBJECT_BULLET = 8;
        //STATE_GOLEFT = 0;
        4,5,0,0,4,5,1,1,ANIMATION_CYCLIC,SPRITE_ALIGN_CENTER | SPRITE_ALIGN_CENTER,
        //STATE_GORIGHT = 1;
        4,5,0,0,4,5,1,1,ANIMATION_CYCLIC,SPRITE_ALIGN_CENTER | SPRITE_ALIGN_CENTER,
        //STATE_GOTOP = 2;
        0,0,0,0,0,0,0,0,0,0,
        //STATE_GODOWN = 3;
        0,0,0,0,0,0,0,0,0,0,
        //STATE_DEATH = 4;
        4,5,0,0,4,5,1,1,ANIMATION_CYCLIC,SPRITE_ALIGN_CENTER | SPRITE_ALIGN_CENTER,
        //STATE_DROP = 5;
        4,5,0,0,4,5,1,1,ANIMATION_CYCLIC,SPRITE_ALIGN_CENTER | SPRITE_ALIGN_CENTER,
        //STATE_BEATJUMPLEFT = 6;
        0,0,0,0,0,0,0,0,0,0,
        //STATE_BEATJUMPRIGHT = 7;
        0,0,0,0,0,0,0,0,0,0,
        //STATE_APPEARANCE = 8;
        4,5,0,0,4,5,1,1,ANIMATION_CYCLIC,SPRITE_ALIGN_CENTER | SPRITE_ALIGN_CENTER,
        //STATE_UNDERFIRE = 9;
        4,5,0,0,4,5,1,1,ANIMATION_CYCLIC,SPRITE_ALIGN_CENTER | SPRITE_ALIGN_CENTER
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

        switch (_alignType & 0x0F)
        {
            case SPRITE_ALIGN_CENTER:
                {
                    i8_offsetX = (GameletImpl.I8_VIRTUALCELL_WIDTH - i8_width) >> 1;
                }
                ;
                break;
            case SPRITE_ALIGN_RIGHT:
                {
                    i8_offsetX = GameletImpl.I8_VIRTUALCELL_WIDTH - i8_width;
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
                    i8_offsetY = (GameletImpl.I8_VIRTUALCELL_HEIGHT - i8_height)>>1;
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
                    i8_offsetY = GameletImpl.I8_VIRTUALCELL_HEIGHT - i8_height ;
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
                        if (i_Frame<0)
                        {
                            lg_backMove = false;
			    i_Frame = 0;
                            return true;
                        }
                        else
                            return false;
                    }
                    else
                    {
                        i_Frame ++;
                        if (i_Frame>=i_maxFrames-1)
                        {
                            lg_backMove = true;
			    i_Frame=i_maxFrames-1;
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

    public void setScreenXY8(int _scrX8,int _scrY8)
    {
        i8_ScreenX = _scrX8;
        i8_ScreenY = _scrY8;
        i8_mainX = i8_ScreenX - i8_offsetX;
        i8_mainY = i8_ScreenY - i8_offsetY;
    }

    public void setMainXY8(int _newX8, int _newY8)
    {
        i8_mainX = _newX8;
        i8_mainY = _newY8;
        i8_ScreenX = i8_mainX + i8_offsetX;
        i8_ScreenY = i8_mainY + i8_offsetY;
    }
}
