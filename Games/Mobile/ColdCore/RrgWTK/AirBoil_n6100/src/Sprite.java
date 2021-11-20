import java.io.DataInputStream;
import java.io.IOException;
import java.io.DataOutputStream;

/*
    Class   :   Sprite
    Version :   1.1
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

//#global SPRITE_COLLISION="PARTLY"

//#global SPRITE_ANIM_TYPE_FROZEN=true
//#global SPRITE_ANIM_TYPE_CYCLIC=true
//#global SPRITE_ANIM_TYPE_PENDULUM=true
//#global SPRITE_ALIGN_CENTER=true
//#global SPRITE_ALIGN_LEFT=true
//#global SPRITE_ALIGN_RIGHT=true
//#global SPRITE_ALIGN_TOP=true
//#global SPRITE_ALIGN_DOWN=true

public class Sprite
{
    public static final int OBJECT_GUNSIGHT = 0;
    public static final int OBJECT_GUNSIGHTFIRE = 0;

//#if SPRITE_ANIM_TYPE_CYCLIC
    public static final int ANIMATION_CYCLIC = 0;
//#endif
//#if SPRITE_ANIM_TYPE_FROZEN
    public static final int ANIMATION_FROZEN = 1;
//#endif
//#if SPRITE_ANIM_TYPE_PENDULUM
    public static final int ANIMATION_PENDULUM = 2;
//#endif

//#if SPRITE_ALIGN_CENTER
    public static final int SPRITE_ALIGN_CENTER = 0;
//#endif
//#if SPRITE_ALIGN_LEFT
    public static final int SPRITE_ALIGN_LEFT = 1;
//#endif
//#if SPRITE_ALIGN_RIGHT
    public static final int SPRITE_ALIGN_RIGHT = 2;
//#endif
//#if SPRITE_ALIGN_TOP
    public static final int SPRITE_ALIGN_TOP = 0x10;
//#endif
//#if SPRITE_ALIGN_DOWN
    public static final int SPRITE_ALIGN_DOWN = 0x20;
//#endif

    public static final int COL_RIGHT = 0;
    public static final int COL_LEFT = 1;
    public static final int COL_UP = 2;
    public static final int COL_DOWN = 3;

    // The kind of animation
    protected int i_animType;
    // Width and height of the animation
    protected int i8_width,i8_height;
    // Main X and Y coords
    protected int i8_mainX, i8_mainY;
    // Max Frame number
    protected int i_maxFrames;
    // Time delay
    protected int i_maxTimeDelay;
    // Offset relative to the main point
    protected int i8_offsetX,i8_offsetY;
//#if SPRITE_ANIM_TYPE_PENDULUM
    protected boolean lg_backMove;// Flag of back animation
//#endif

//#if SPRITE_COLLISION=="PARTLY"
    public int i8_col_offsetX;
    public int i8_col_offsetY;
    public int i8_col_width;
    public int i8_col_height;
//#endif

    public boolean lg_SpriteActive;
    public int i8_ScreenX,i8_ScreenY;
    public int i_Frame;
    public int i_Delay;

    public int i_ObjectType;
    public int i_ObjectState;
    public boolean lg_SpriteInvisible;

    protected int i_spriteID;

    public Sprite(int _id)
    {
        lg_SpriteActive = false;
        i_spriteID = _id;
    }

//#if SPRITE_COLLISION=="PARTLY"
    public void setCollisionBounds(int i8_offX,int i8_offY,int i8_width,int i8_height)
    {
        i8_col_offsetX = i8_offX;
        i8_col_offsetY = i8_offY;
        i8_col_width = i8_width;
        i8_col_height = i8_height;
    }
//#endif

    public void setAnimation(int _animationType, int _alignType, int i8_frameWidth, int i8_frameHeight, int _maxFrames, int _initFrame, int _timeDelay)
    {
        i_animType = _animationType;
        i8_width = i8_frameWidth;
        i8_height = i8_frameHeight;
        i_maxFrames = _maxFrames;
        i_maxTimeDelay = _timeDelay;

//#-
        i8_col_width = i8_width;
        i8_col_height = i8_height;
//#+

        i_Delay = 0;
        i_Frame = _initFrame;

//#if SPRITE_ANIM_TYPE_PENDULUM
        lg_backMove = false;
//#endif

        switch (_alignType & 0x0F)
        {
//#if SPRITE_ALIGN_CENTER
            case SPRITE_ALIGN_CENTER:
                {
                    i8_offsetX = 0 - (i8_width>>1);
                }
                ;
                break;
//#endif
//#if SPRITE_ALIGN_RIGHT
            case SPRITE_ALIGN_RIGHT:
                {
                    i8_offsetX = 0 - i8_width;
                }
                ;
                break;
//#endif
//#if SPRITE_ALIGN_LEFT
            case SPRITE_ALIGN_LEFT:
                {
                    i8_offsetX = 0;
                }
                ;
                break;
//#endif
        }

        switch (_alignType & 0xF0)
        {
//#if SPRITE_ALIGN_CENTER
            case SPRITE_ALIGN_CENTER:
                {
                    i8_offsetY = 0 - (i8_height>>1);
                }
                ;
                break;
//#endif
//#if SPRITE_ALIGN_TOP
            case SPRITE_ALIGN_TOP:
                {
                    i8_offsetY = 0;
                }
                ;
                break;
//#endif
//#if SPRITE_ALIGN_DOWN
            case SPRITE_ALIGN_DOWN:
                {
                    i8_offsetY = 0 - i8_height;
                }
                ;
                break;
//#endif
        }

        i8_offsetX = (i8_offsetX+0x7f)&0xFFFFFF00;
        i8_offsetY = (i8_offsetY+0x7f)&0xFFFFFF00;

        setMainPointXY(i8_mainX,i8_mainY);
    }

//#if SPRITE_COLLISION!="NONE"
    public boolean isCollided(int i8_line, int colType)
    {
//#if SPRITE_COLLISION=="PARTLY"
//$ int i8_sX = i8_ScreenX+i8_col_offsetX;
//$ int i8_sY = i8_ScreenY+i8_col_offsetY;
//$ int i8_sw = i8_col_width;
//$ int i8_sh = i8_col_height;
//#else
//#if SPRITE_COLLISION=="FULL"
//$ int i8_sX = i8_ScreenX;
//$ int i8_sY = i8_ScreenY;
//$ int i8_sw = i8_width;
//$ int i8_sh = i8_height;
//#endif
//#endif

//#-
        final int i8_sX = i8_ScreenX+i8_col_offsetX;
        final int i8_sY = i8_ScreenY+i8_col_offsetY;
        final int i8_sw = i8_col_width;
        final int i8_sh = i8_col_height;

//#+
        switch (colType) {
            case COL_RIGHT :
                return i8_sX + i8_sw >= i8_line;
            case COL_LEFT :
                return i8_sX <= i8_line;
            case COL_UP :
                return i8_sY <= i8_line;
            case COL_DOWN :
                return i8_sY + i8_sh >= i8_line;
        }
        return false;
    }
//#endif

//#if SPRITE_COLLISION!="NONE"
    public boolean isCollided(Sprite _sprite)
    {
//#if SPRITE_COLLISION=="PARTLY"
//$ int i8_sX = i8_ScreenX+i8_col_offsetX;
//$ int i8_sY = i8_ScreenY+i8_col_offsetY;
//$ int i8_sw = i8_col_width;
//$ int i8_sh = i8_col_height;

//$ int i8_dX = _sprite.i8_ScreenX+_sprite.i8_col_offsetX;
//$ int i8_dY = _sprite.i8_ScreenY+_sprite.i8_col_offsetY;
//$ int i8_dw = _sprite.i8_col_width;
//$ int i8_dh = _sprite.i8_col_height;

//#else
//#if SPRITE_COLLISION=="FULL"
//$ int i8_sX = i8_ScreenX;
//$ int i8_sY = i8_ScreenY;
//$ int i8_sw = i8_width;
//$ int i8_sh = i8_height;

//$ int i8_dX = _sprite.i8_ScreenX;
//$ int i8_dY = _sprite.i8_ScreenY;
//$ int i8_dw = _sprite.i8_width;
//$ int i8_dh = _sprite.i8_height;

//#endif
//#endif

//#-
         final int i8_sX = i8_ScreenX+i8_col_offsetX;
         final int i8_sY = i8_ScreenY+i8_col_offsetY;
         final int i8_sw = i8_col_width;
         final int i8_sh = i8_col_height;

         final int i8_dX = _sprite.i8_ScreenX+_sprite.i8_col_offsetX;
         final int i8_dY = _sprite.i8_ScreenY+_sprite.i8_col_offsetY;
         final int i8_dw = _sprite.i8_col_width;
         final int i8_dh = _sprite.i8_col_height;
//#+

        return !(i8_sX + i8_sw < i8_dX || i8_sY + i8_sh < i8_dY || i8_dX + i8_dw < i8_sX || i8_dY + i8_dh < i8_sY);
    }
//#endif

    public boolean processAnimation()
    {
        i_Delay ++;
        if (i_Delay<i_maxTimeDelay) return false;
        i_Delay = 0;

        switch (i_animType)
        {
//#if SPRITE_ANIM_TYPE_CYCLIC
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
//#endif
//#if SPRITE_ANIM_TYPE_FROZEN
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
//#endif
//#if SPRITE_ANIM_TYPE_PENDULUM
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
//#endif
        }
        return false;
    }

    public void readSpriteFromStream(DataInputStream _dis) throws IOException
    {
        lg_SpriteActive = _dis.readBoolean();
        lg_SpriteInvisible = _dis.readBoolean();

        int i_mnX = _dis.readInt();
        int i_mnY = _dis.readInt();

        setMainPointXY(i_mnX,i_mnY);

        i_Frame = _dis.readUnsignedByte();
        i_Delay = _dis.readUnsignedByte();

        lg_backMove = _dis.readBoolean();

        i_ObjectState = _dis.readInt();

        lg_SpriteInvisible = _dis.readBoolean();
    }

    /**
     * количество байт, требуемое для записи объекта
     */
    public static final int DATASIZE_BYTES = 18;

    public void writeSpriteToStream(DataOutputStream _dis) throws IOException
    {
        _dis.writeBoolean(lg_SpriteActive);
        _dis.writeBoolean(lg_SpriteInvisible);

        _dis.writeInt(i8_mainX);
        _dis.writeInt(i8_mainY);

        _dis.writeByte(i_Frame);
        _dis.writeByte(i_Delay);

        _dis.writeBoolean(lg_backMove);

        _dis.writeInt(i_ObjectState);

        _dis.writeBoolean(lg_SpriteInvisible);
    }

    public void setMainPointXY(int i8_newX, int i8_newY)
    {
        i8_mainX = i8_newX;
        i8_mainY = i8_newY;
        i8_ScreenX = i8_mainX + i8_offsetX;
        i8_ScreenY = i8_mainY + i8_offsetY;
    }

    public void moveMainPointXY(int i8_newX, int i8_newY)
    {
        i8_mainX += i8_newX;
        i8_mainY += i8_newY;
        i8_ScreenX = i8_mainX + i8_offsetX;
        i8_ScreenY = i8_mainY + i8_offsetY;
    }
}
