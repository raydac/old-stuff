
import java.io.DataInputStream;
import java.io.IOException;
import java.io.DataOutputStream;






public class Sprite
{
    public static final int OBJECT_GUNSIGHT = 0;
    public static final int OBJECT_GUNSIGHTFIRE = 0;

    public static final int ANIMATION_CYCLIC = 0;
    public static final int ANIMATION_FROZEN = 1;
    public static final int ANIMATION_PENDULUM = 2;

    public static final int SPRITE_ALIGN_CENTER = 0;
    public static final int SPRITE_ALIGN_LEFT = 1;
    public static final int SPRITE_ALIGN_RIGHT = 2;
    public static final int SPRITE_ALIGN_TOP = 0x10;
    public static final int SPRITE_ALIGN_DOWN = 0x20;

    protected int i_animType;
    protected int i_width,i_height;
    protected int i_mainX, i_mainY;
    protected int i_maxFrames;
    protected int i_maxTimeDelay;
    protected int i_offsetX,i_offsetY;
    protected boolean lg_backMove;


    public boolean lg_SpriteActive;
    public int i_ScreenX,i_ScreenY;
    public int i_Frame;
    public int i_Delay;

    public int i_ObjectType;
    public int i_ObjectState;
    public boolean lg_SpriteInvisible;

    protected int i_spriteID;

    protected int i_linkedSprite;
    protected boolean lg_linkedSprite;

    protected int i_TTL;
    protected int i_deltaX;
    protected int i_deltaY;

    public Sprite(int _id)
    {
        lg_SpriteActive = false;
        i_spriteID = _id;
        i_linkedSprite = -1;
        lg_linkedSprite = false;
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

    public static final int DATASIZE_BYTES = 18;

    public void writeSpriteToStream(DataOutputStream _dis) throws IOException
    {
        _dis.writeBoolean(lg_SpriteActive);
        _dis.writeBoolean(lg_SpriteInvisible);

        _dis.writeInt(i_mainX);
        _dis.writeInt(i_mainY);

        _dis.writeByte(i_Frame);
        _dis.writeByte(i_Delay);

        _dis.writeBoolean(lg_backMove);

        _dis.writeInt(i_ObjectState);

        _dis.writeBoolean(lg_SpriteInvisible);
    }

    public void setMainPointXY(int _newX, int _newY)
    {
        i_mainX = _newX;
        i_mainY = _newY;
        i_ScreenX = i_mainX + i_offsetX;
        i_ScreenY = i_mainY + i_offsetY;
    }
}
