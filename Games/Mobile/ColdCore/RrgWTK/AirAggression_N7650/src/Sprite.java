import java.io.DataInputStream;
import java.io.IOException;
import java.io.DataOutputStream;

/*
    Class   :   Sprite
    Version :   1.0
    Author  :   Igor Maznitsa (igor.maznitsa@igormaznitsa.com)
*/

/*
    Preprocessor variable list:

    SPRITE_COLLISION - ("NONE"|"PARTLY"|"FULL") a sprite has collision detect mechanism
    SPRITE_ANIM_TYPE_CYCLIC - a sprite has possibility for the cyclic animation
    SPRITE_ANIM_TYPE_FROZEN - a sprite has possibility for the frozen animation
    SPRITE_ANIM_TYPE_PENDULUM - a sprite has possibility for the pendulum animation

    SPRITE_ALIGN_CENTER - the align of a sprite to center relative to the main point
    SPRITE_ALIGN_LEFT - the align of a sprite to left relative to the main point
    SPRITE_ALIGN_RIGHT - the align of a sprite to right relative to the main point
    SPRITE_ALIGN_TOP - the align of a sprite to top relative to the main point
    SPRITE_ALIGN_DOWN - the align of a sprite to down relative to the main point
*/


public class Sprite
{
    public static final int ANIMATION_CYCLIC = 0;
    public static final int ANIMATION_FROZEN = 1;
    public static final int ANIMATION_PENDULUM = 2;

    public static final int SPRITE_ALIGN_CENTER = 0;
    public static final int SPRITE_ALIGN_LEFT = 1;
    public static final int SPRITE_ALIGN_RIGHT = 2;
    public static final int SPRITE_ALIGN_TOP = 0x10;
    public static final int SPRITE_ALIGN_DOWN = 0x20;

    // The kind of animation
    protected int i_animType;
    // Width and height of the animation
    protected int i_width,i_height;
    // Main X and Y coords
    protected int i_mainX, i_mainY;
    // Max Frame number
    protected int i_maxFrames;
    // Time delay
    protected int i_maxTimeDelay;
    // Offset relative to the main point
    protected int i_offsetX,i_offsetY;
    protected boolean lg_backMove;// Flag of back animation

    public int i_col_offsetX;
    public int i_col_offsetY;
    public int i_col_width;
    public int i_col_height;

    public boolean lg_SpriteActive;
    public int i_ScreenX,i_ScreenY;
    public int i_Frame;
    public int i_Delay;

    public int i_LastDirection;
    public int i_JumpAngle;
    public int i_JumpMainX;
    public int i_JumpMainY;
    //
    public int i_Speed;     // Sprite object speed
    public int i_Direction; // Sprite object direction
    //

    public int i_initMainX,i_initMainY;
    public int i_ObjectType;
    public int i_ObjectState;
    public boolean lg_SpriteInvisible;

    protected int i_keySet;

    protected int i_spriteID;

    public Sprite(int _id)
    {
        lg_SpriteActive = false;
        i_spriteID = _id;
    }

    public void setCollisionBounds(int _offX,int _offY,int _width,int _height)
    {
        i_col_offsetX = _offX;
        i_col_offsetY = _offY;
        i_col_width = _width;
        i_col_height = _height;
    }

    public void setAnimation(int _animationType, int _alignType, int _frameWidth, int _frameHeight, int _maxFrames, int _initFrame, int _timeDelay)
    {
        i_animType = _animationType;
        i_width = _frameWidth;
        i_height = _frameHeight;
        i_maxFrames = _maxFrames;
        i_maxTimeDelay = _timeDelay;


        i_Delay = 0;
        i_Frame = _initFrame;

        lg_backMove = false;

        switch (_alignType & 0x0F)
        {
            case SPRITE_ALIGN_CENTER:
                {
                    i_offsetX = 0 - (i_width>>1);
                }
                ;
                break;
            case SPRITE_ALIGN_RIGHT:
                {
                    i_offsetX = 0 - i_width;
                }
                ;
                break;
            case SPRITE_ALIGN_LEFT:
                {
                    i_offsetX = 0;
                }
                ;
                break;
        }

        switch (_alignType & 0xF0)
        {
            case SPRITE_ALIGN_CENTER:
                {
                    i_offsetY = 0 - (i_height>>1);
                }
                ;
                break;
            case SPRITE_ALIGN_TOP:
                {
                    i_offsetY = 0;
                }
                ;
                break;
            case SPRITE_ALIGN_DOWN:
                {
                    i_offsetY = 0 - i_height;
                }
                ;
                break;
        }
        setMainPointXY(i_mainX,i_mainY);
    }

    public boolean isCollided(Sprite _sprite)
    {
 int i_sX = i_ScreenX+i_col_offsetX;
 int i_sY = i_ScreenY+i_col_offsetY;
 int i_sw = i_col_width;
 int i_sh = i_col_height;

 int i_dX = _sprite.i_ScreenX+_sprite.i_col_offsetX;
 int i_dY = _sprite.i_ScreenY+_sprite.i_col_offsetY;
 int i_dw = _sprite.i_col_width;
 int i_dh = _sprite.i_col_height;

 if (i_sX+i_sw < i_dX || i_sY+i_sh < i_dY || i_dX+i_dw < i_sX || i_dY+i_dh < i_sY)
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

///////
// (!) WARNING: check the list of stored variables more carefully


    public void readSpriteFromStream(DataInputStream _dis) throws IOException
    {
        i_ObjectType = _dis.readUnsignedByte();
        lg_SpriteActive = _dis.readBoolean();
//        lg_SpriteInvisible = _dis.readBoolean();

        int i_mnX = _dis.readInt();
        int i_mnY = _dis.readInt();

        setMainPointXY(i_mnX,i_mnY);

//        i_JumpAngle = dis.readUnsignedByte();
//        i_JumpMainX = _dis.readInt();
//        i_JumpMainY = _dis.readInt();

        i_Frame = _dis.readUnsignedByte();
        i_Delay = _dis.readUnsignedByte();

//        i_LastDirection = _dis.readUnsignedByte();

//        i_initMainX = _dis.readInt();
//        i_initMainY = _dis.readInt();

        i_Speed = _dis.readShort();;
        i_Direction = _dis.readUnsignedByte();


//        i_keySet = _dis.readUnsignedByte();

        lg_backMove = _dis.readBoolean();
    }

    public void writeSpriteToStream(DataOutputStream _dis) throws IOException
    {
        _dis.writeByte(i_ObjectType);

//        i_ObjectState = i_ObjectType;

        _dis.writeBoolean(lg_SpriteActive);
//        _dis.writeBoolean(lg_SpriteInvisible);

        _dis.writeInt(i_mainX);
        _dis.writeInt(i_mainY);
//        _dis.writeByte(i_JumpAngle);
//        _dis.writeInt(i_JumpMainX);
//        _dis.writeInt(i_JumpMainY);

        _dis.writeByte(i_Frame);
        _dis.writeByte(i_Delay);

//        _dis.writeByte(i_LastDirection);

//        _dis.writeInt(i_initMainX);
//        _dis.writeInt(i_initMainY);

        _dis.writeShort(i_Speed);
        _dis.writeByte(i_Direction);

//        _dis.writeByte(i_keySet);

        _dis.writeBoolean(lg_backMove);
    }

    public void setMainPointXY(int _newX, int _newY)
    {
        i_mainX = _newX;
        i_mainY = _newY;
        i_ScreenX = i_mainX + i_offsetX;
        i_ScreenY = i_mainY + i_offsetY;
    }
}
