
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.DataInputStream;

public class PathController
{
    public short[] as_pathArray;
    private int i_initOffset;
    private int i_currentOffset;
    public Sprite p_sprite;
    private int i_length;
    private int i_curLength;
    private int i8_dx;
    private int i8_dy;
    private int i8_targetX;
    private int i8_targetY;
    private int i_modify;
    private int i8_curX;
    private int i8_curY;
    private int i_type;
    private boolean lg_completed; 

    public int i8_centerX;
    public int i8_centerY;

    protected boolean lg_back; 

    public static final int MODIFY_NONE = 0;
    public static final int MODIFY_FLIPVERT = 1;
    public static final int MODIFY_FLIPHORZ = 2;

    private static final int POINT_DATA_SIZE = 3; 


    public static final int TYPE_NORMAL = 0;
    public static final int TYPE_PENDULUM = 1;
    public static final int TYPE_CYCLED = 2;

    public PathController()
    {
        lg_completed = true;
    }

    public void deactivate()
    {
        lg_completed = true;
    }

    public final int getType()
    {
        return i_type;
    }

    public final void setType(int _value)
    {
        i_type = _value;
    }

    public final int getCurrentPointIndex()
    {
        return i_curLength;
    }

    public static final int DATASIZE_BYTES = 44;

    public final void writePathToStream(DataOutputStream _stream) throws IOException
    {
        if(p_sprite == null)
            _stream.writeShort(-1);
        else
            _stream.writeShort(p_sprite.i_spriteID);

        _stream.writeShort(i_initOffset);
        _stream.writeShort(i_currentOffset);

        _stream.writeInt(i8_centerX);
        _stream.writeInt(i8_centerY);

        _stream.writeByte(i_length);
        _stream.writeByte(i_curLength);
        _stream.writeInt(i8_dx);
        _stream.writeInt(i8_dy);
        _stream.writeInt(i8_targetX);
        _stream.writeInt(i8_targetY);
        _stream.writeByte(i_modify);
        _stream.writeInt(i8_curX);
        _stream.writeInt(i8_curY);
        _stream.writeByte(i_type);
        _stream.writeBoolean(lg_back);
        _stream.writeBoolean(lg_completed);
    }

    public void readPathFromStream(DataInputStream _stream,Sprite [] _sprites) throws IOException
    {
        int i_sprIndex = _stream.readShort();
        if (i_sprIndex<0)
            p_sprite = null;
        else
            p_sprite = _sprites[i_sprIndex];

        i_initOffset = _stream.readUnsignedShort();
        i_currentOffset = _stream.readUnsignedShort();

        i8_centerX = _stream.readInt();
        i8_centerY = _stream.readInt();

        i_length = _stream.readUnsignedByte();
        i_curLength = _stream.readUnsignedByte();
        i8_dx = _stream.readInt();
        i8_dy = _stream.readInt();
        i8_targetX = _stream.readInt();
        i8_targetY = _stream.readInt();
        i_modify = _stream.readByte();
        i8_curX = _stream.readInt();
        i8_curY = _stream.readInt();
        i_type = _stream.readByte();
        lg_back = _stream.readBoolean();
        lg_completed = _stream.readBoolean();
    }

    private final void calculateDifferents(boolean _speedPrev)
    {
        int i_steps=0;
        if (_speedPrev)i_steps = as_pathArray[i_currentOffset++];
        i8_targetX = as_pathArray[i_currentOffset++]<<8;
        i8_targetY = as_pathArray[i_currentOffset++]<<8;
        if (!_speedPrev)i_steps = as_pathArray[i_currentOffset++];

        if ((i_modify & MODIFY_FLIPHORZ) != 0)
        {
            i8_targetX = 0 - i8_targetX;
        }

        if ((i_modify & MODIFY_FLIPVERT) != 0)
        {
            i8_targetY = 0 - i8_targetY;
        }

        i8_dx = (i8_targetX - i8_curX) / i_steps;
        i8_dy = (i8_targetY - i8_curY) / i_steps;

        i8_targetX = i8_curX + i8_dx * i_steps;
        i8_targetY = i8_curY + i8_dy * i_steps;
    }

    public final void initPath(int _centerX,int _centerY,Sprite _sprite, short[] _pathArray, int _offset,int _initPathPoint, int _pathLength, int _modify)
    {
        i8_centerX = _centerX;
        i8_centerY = _centerY;
        i_modify = _modify;
        i_initOffset = _offset;

        p_sprite = _sprite;
        as_pathArray = _pathArray;
        i_length = as_pathArray[i_initOffset++];
        if (_pathLength>0)
            i_length = _pathLength;
        else
            if (_initPathPoint>0)
                i_length -= _initPathPoint;
        short w_acc = as_pathArray[i_initOffset++];
        i_initOffset = i_initOffset + _initPathPoint * POINT_DATA_SIZE;
        resetPath();
        calculateDifferents(true);
        i_type = w_acc & 0xF;
        lg_back = false;

        p_sprite.setMainPointXY(i8_centerX+i8_curX, i8_centerY+i8_curY);
    }

    public final void resetPath()
    {
        i_curLength = 0;
        lg_back = false;
        lg_completed = false;
        i8_curX = as_pathArray[i_initOffset]<<8;
        i8_curY = as_pathArray[i_initOffset+1]<<8;
        i_currentOffset = i_initOffset+(POINT_DATA_SIZE-1);
    }

    public final boolean isCompleted()
    {
        return lg_completed;
    }

    public final boolean processStep()
    {
        if (lg_completed) return false;

        i8_curX += i8_dx;
        i8_curY += i8_dy;

        p_sprite.setMainPointXY(i8_centerX+i8_curX, i8_centerY+i8_curY);

        if (i8_curX == i8_targetX && i8_curY == i8_targetY)
        {
            switch (i_type)
            {
                case TYPE_NORMAL:
                    {
                        i_curLength++;
                        if (i_curLength == i_length)
                            lg_completed = true;
                        else
                            calculateDifferents(true);
                    }
                    ;
                    break;
                case TYPE_PENDULUM:
                    {
                        boolean lg_p = true;
                        if (lg_back)
                        {
                            if (i_curLength == 0)
                            {
                                lg_back = false;
                                i_currentOffset --;
                                lg_p = true;
                            }
                            else
                            {
                                i_curLength --;
                                i_currentOffset -= (POINT_DATA_SIZE*2);
                                lg_p = false;
                            }
                        }
                        else
                        {
                            i_curLength ++;
                            if (i_curLength == i_length)
                            {
                                lg_back = true;
                                i_currentOffset -= (POINT_DATA_SIZE*2-1);
                                i_curLength --;
                                lg_p = false;
                            }
                        }
                       calculateDifferents(lg_p);
                    }
                    ;
                    break;
                case TYPE_CYCLED:
                    {
                        i_curLength ++;
                        if (i_curLength == i_length)
                        {
                            resetPath();
                        }
                        calculateDifferents(true);
                    }
            }
            return true;
        }
        return false;
    }
}
