package com.GameKit_6.Frog;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.DataInputStream;

public class Sprites
{
    public static final int OBJECT_PLAYER   = 0;
    public static final int OBJECT_BIRD     = 1;


    public static final int STATE_LEFT      = 0;
    public static final int STATE_RIGHT     = 1;
    public static final int STATE_UP        = 2;
    public static final int STATE_DOWN      = 3;
    public static final int STATE_DEATH     = 4;
    public static final int STATE_JUMPLEFT  = 5;
    public static final int STATE_JUMPRIGHT = 6;
    public static final int STATE_JUMPUP    = 7;
    public static final int STATE_JUMPDOWN  = 8;

    public int i_objectType;
    public int i_objectState;
    public int i_previousState;
    public int i_align;


    public Sprites(int _type,int _state)
    {
        i_objectType = _type;
        initState(_state);
        lg_SpriteActive = true;
    }


    public void initState(int _state)
    {
        i_objectState = _state;
	lg_SpriteActive = true;
        int i_offset = i_objectType * 6*9  +  6 * i_objectState;

	i8_moveX = 0; i8_moveY = 0;
        int i_wdth = ai_AnimationArray[i_offset++];
        int i_hght = ai_AnimationArray[i_offset++];
        int i_frames = ai_AnimationArray[i_offset++];
        int i_delay = ai_AnimationArray[i_offset++];
        int i_animtype = ai_AnimationArray[i_offset++];
        int i_aligntype = ai_AnimationArray[i_offset];

        setAnimation(i_animtype,i_aligntype,i_wdth,i_hght,i_frames,0,i_delay);
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
    public int i8_mainX, i8_mainY;
    public int i_cellX, i_cellY;
    protected int i_maxFrames;
    protected int i_maxTimeDelay;
    protected int i8_offsetX,i8_offsetY;
    protected int i8_moveX,i8_moveY;
    public boolean lg_backMove;
    public boolean lg_SpriteActive;
    public int i8_ScreenX,i8_ScreenY;
    public int i_Frame;
    public int i_Delay;
//====================================================================

    public static final int [] ai_AnimationArray = new int[]
    {
        // Width, Height, Frame, Animation delay, AnimationType, Sprite align
	// Player
        //STATE_LEFT      = 0;
        12,10,1,0,ANIMATION_FROZEN,SPRITE_ALIGN_DOWN | SPRITE_ALIGN_CENTER,
        //STATE_RIGHT     = 1;
        12,10,1,0,ANIMATION_FROZEN,SPRITE_ALIGN_DOWN | SPRITE_ALIGN_CENTER,
        //STATE_UP        = 2;
        11,10,1,0,ANIMATION_FROZEN,SPRITE_ALIGN_DOWN | SPRITE_ALIGN_CENTER,
        //STATE_DOWN      = 3;
        11,10,1,0,ANIMATION_FROZEN,SPRITE_ALIGN_DOWN | SPRITE_ALIGN_CENTER,
        //STATE_DEATH     = 8;
        15,15,0,0,ANIMATION_PENDULUM,SPRITE_ALIGN_DOWN | SPRITE_ALIGN_CENTER,
        //STATE_JUMPLEFT  = 4;
        20,15,3,1,ANIMATION_FROZEN,SPRITE_ALIGN_DOWN | SPRITE_ALIGN_CENTER,
        //STATE_JUMPRIGHT = 5;
        20,15,3,1,ANIMATION_FROZEN,SPRITE_ALIGN_DOWN | SPRITE_ALIGN_CENTER,
        //STATE_JUMPUP    = 6;
        12,20,3,1,ANIMATION_FROZEN,SPRITE_ALIGN_DOWN | SPRITE_ALIGN_CENTER,
        //STATE_JUMPDOWN  = 7;
        12,20,3,1,ANIMATION_FROZEN,SPRITE_ALIGN_DOWN | SPRITE_ALIGN_CENTER,

	// Bird
        1,1,0,0,ANIMATION_PENDULUM,SPRITE_ALIGN_DOWN | SPRITE_ALIGN_CENTER,
        //STATE_RIGHT     = 1;
        1,1,1,0,ANIMATION_PENDULUM,SPRITE_ALIGN_DOWN | SPRITE_ALIGN_CENTER,
        //STATE_UP        = 2;
        1,1,1,0,ANIMATION_PENDULUM,SPRITE_ALIGN_DOWN | SPRITE_ALIGN_CENTER,
        //STATE_DOWN      = 3;
        1,1,1,0,ANIMATION_PENDULUM,SPRITE_ALIGN_DOWN | SPRITE_ALIGN_CENTER,
        //STATE_JUMPLEFT  = 4;
        1,1,1,0,ANIMATION_PENDULUM,SPRITE_ALIGN_DOWN | SPRITE_ALIGN_CENTER,
        //STATE_JUMPRIGHT = 5;
        1,1,1,0,ANIMATION_PENDULUM,SPRITE_ALIGN_DOWN | SPRITE_ALIGN_CENTER,
        //STATE_JUMPUP    = 6;
        1,1,1,0,ANIMATION_PENDULUM,SPRITE_ALIGN_DOWN | SPRITE_ALIGN_CENTER,
        //STATE_JUMPDOWN  = 7;
        1,1,1,0,ANIMATION_PENDULUM,SPRITE_ALIGN_DOWN | SPRITE_ALIGN_CENTER,
        //STATE_DEATH     = 8;
        1,1,1,0,ANIMATION_PENDULUM,SPRITE_ALIGN_DOWN | SPRITE_ALIGN_CENTER,

    };

    public void setAnimation(int _animationType, int _alignType, int _frameWidth, int _frameHeight, int _maxFrames, int _initFrame, int _timeDelay)
    {
        i_animType = _animationType;
        i8_width = _frameWidth<<8;
        i8_height = _frameHeight<<8;
        i_maxFrames = _maxFrames;
        i_maxTimeDelay = _timeDelay;

        i_Delay = 0;
        i_Frame = _initFrame;

        lg_backMove = false;

        switch (_alignType & 0x0F)
        {
            case SPRITE_ALIGN_CENTER:
                {
                    i8_offsetX = i8_width >> 1;
                }
                ;
                break;
            case SPRITE_ALIGN_RIGHT:
                {
                    i8_offsetX = i8_width;
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
                    i8_offsetY = i8_height>>1;
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
                    i8_offsetY = i8_height ;
                }
                ;
                break;
        }
    }


    public boolean processAnimation()
    {
	if(i8_moveX!=0 || i8_moveY!=0)
	{
	   setMainXY8(i8_mainX+i8_moveX,i8_mainY+i8_moveY);
	}


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

    public void setMainXY8(int _newX8, int _newY8)
    {
        i8_mainX = _newX8;
        i8_mainY = _newY8;
        i8_ScreenX = i8_mainX + i8_offsetX;
        i8_ScreenY = i8_mainY + i8_offsetY;
    }

    public void setMotionDist(int _newX, int _newY)
    {
      int div = i_maxTimeDelay;
      if(div<=0)div=1;
      div *= i_maxFrames;

      if(div!=0) {
        i8_moveX = (_newX<<8)/div;
        i8_moveY = (_newY<<8)/div;
      } else {
        i8_moveX = 0;
        i8_moveY = 0;
      }

    }



    public void setMainXY(int _newX, int _newY)
    {
        setMainXY8(_newX << 8, _newY << 8);
    }


    public void setCellXY(int _X, int _Y)
    {
       i_cellX = _X;
       i_cellY = _Y;
    }

    protected void writeSpriteToOutputStream(DataOutputStream _dos) throws IOException
    {
        _dos.writeByte(i_objectType);
        _dos.writeByte(i_objectState);
        _dos.writeByte(i_cellX);
        _dos.writeByte(i_cellY);
        _dos.writeShort(i8_mainX);
        _dos.writeShort(i8_mainY);
        _dos.writeShort(i8_moveX);
        _dos.writeShort(i8_moveY);
        _dos.writeByte(i_previousState);
        _dos.writeByte(i_Frame);
        _dos.writeByte(i_Delay);
        _dos.writeBoolean(lg_backMove);
        _dos.writeBoolean(lg_SpriteActive);
    }

    protected void loadSpriteFromStream(DataInputStream _dis) throws IOException
    {
        i_objectType = _dis.readUnsignedByte();
        int i_s = _dis.readUnsignedByte();
        i_cellX = _dis.readUnsignedByte();
        i_cellY = _dis.readUnsignedByte();
        i8_mainX = ((int)_dis.readShort());
        i8_mainY = ((int)_dis.readShort());
        initState(i_s);
        i8_moveX = ((int)_dis.readShort());
        i8_moveY = ((int)_dis.readShort());
        i_previousState = _dis.readUnsignedByte();
        i_Frame = _dis.readUnsignedByte();
        i_Delay = _dis.readUnsignedByte();
        lg_backMove = _dis.readBoolean();
        lg_SpriteActive = _dis.readBoolean();
    }







}
