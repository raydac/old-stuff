package mtv.scene;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.DataInputStream;

/**
 * ����� ��������� ���������� ���� ��� ��������� �������.
 *
 * ������ �������� ���� � ������� ���������
 * ����������_�����, ���_����,{x,y,step_number}...
 *
 * @author ����� �������
 * @version 1.2
 * @since 27 okt 2004
 */
public class PathController
{
    public short[] as_pathArray;// ��������� �� ������
    private int i_initOffset;//���������������� �������� ��������, ��������� �� ������ ����� ����
    private int i_currentOffset;//�������� �� ������� �����
    public Sprite p_sprite;// �������������� ������
    private int i_length;// ������ ���� � ������
    private int i_curLength;// ����� ������� �����
    private int i8_dx;// ���������� ���������� X
    private int i8_dy;// ���������� ���������� Y
    private int i8_targetX;// ���������� X ������� �����
    private int i8_targetY;// ���������� Y ������� �����
    private int i_modify;// ����� ����������� ������� �����
    private int i8_curX;// ������� ���������� X
    private int i8_curY;// ������� ���������� Y
    private int i_type;// ��� ����
    private boolean lg_completed; // ����, ������������, ��� ����������� ���� ���������

    //#global PATH_LOCAL_SCALE=true

    //#if PATH_LOCAL_SCALE
    private int i8_localScaleWidth = 0x100;
    private int i8_localScaleHeight = 0x100;
    //#endif

    protected static long SCALE_WIDTH = 0x100;
    protected static long SCALE_HEIGHT = 0x100;

    /**
     * ���������� X ����������� ����� ����, ������������� ����� 8 ��������
     */
    public int i8_centerX;
    /**
     * ���������� Y ����������� ����� ����, ������������� ����� 8 ��������
     */
    public int i8_centerY;

    protected boolean lg_back; //����, ����������, ��� �������������� �������� ���

    /**
     * ��� �����������
     */
    public static final int MODIFY_NONE = 0;
    /**
     * ����������� ������������ ��� Y
     */
    public static final int MODIFY_FLIPVERT = 1;
    /**
     * ����������� ������������ ��� X
     */
    public static final int MODIFY_FLIPHORZ = 2;

    private static final int POINT_DATA_SIZE = 3; // ������ ������, ��������� �� ���� ����� ���� � �������


    /**
     * �������� ���, ������ �������� �� ����� ���� � ������ ����������� ������������
     */
    public static final int TYPE_NORMAL = 0;
    /**
     * �������, ������ ����� �� ��������� �����, �������� ��������� � �������� �����������
     */
    public static final int TYPE_PENDULUM = 1;
    /**
     * �����������, ������ ����� �� ��������� �����, ��������� �� ������
     */
    public static final int TYPE_CYCLED = 2;

    public PathController()
    {
        lg_completed = true;
    }

    /**
     * ��������� ����, �������� ��� � ����������� ���������
     */
    public void deactivate()
    {
        lg_completed = true;
    }

    /**
     * ���������� ��� �����������
     * @return ���
     */
    public final int getType()
    {
        return i_type;
    }

    /**
     * ��������� ������ ������������� ��� ����
     * @param _value ����� ��������
     */
    public final void setType(int _value)
    {
        i_type = _value;
    }

    /**
     * ���������� ������ ������� ����� ����
     * @return ������ ������� �����, (0 - ������)
     */
    public final int getCurrentPointIndex()
    {
        return i_curLength;
    }

    /**
     * ���������� ����, ��������� ��� ������ �������
     */
    //#if PATH_LOCAL_SCALE
    public static final int DATASIZE_BYTES = 52;
    //#else
    //$public static final int DATASIZE_BYTES = 44;
    //#endif
    
    /**
     * ���������� ��������� ����������� � �����
     * @param _stream �����
     * @throws IOException ����������, ������������ ��� ��������� ������
     */
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

        //#if PATH_LOCAL_SCALE
        _stream.writeInt(i8_localScaleWidth);
        _stream.writeInt(i8_localScaleHeight);
        //#endif

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

    /**
     * ��������� ���������� ���� �� ������
     * @param _stream �����
     * @param _sprites ������ ���������� �������
     * @throws IOException ����������, ������������ ��� ��������� ��� ������ ������
     */
    public void readPathFromStream(DataInputStream _stream,Sprite [] _sprites,short [] _pathArray) throws IOException
    {
        as_pathArray = _pathArray;

        int i_sprIndex = _stream.readShort();
        if (i_sprIndex<0)
            p_sprite = null;
        else
            p_sprite = _sprites[i_sprIndex];

        i_initOffset = _stream.readUnsignedShort();
        i_currentOffset = _stream.readUnsignedShort();

        i8_centerX = _stream.readInt();
        i8_centerY = _stream.readInt();

        //#if PATH_LOCAL_SCALE
        i8_localScaleWidth = _stream.readInt();
        i8_localScaleHeight = _stream.readInt();
        //#endif

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

    /**
     * ����������� ����� �������������� ��� �������� � ��������� �����
     * @param _speedPrev ���� TRUE, �� ����� ���������� ����� �� ������� ��� ������ �������� � �������, ����� ��� ���������
     */
    private final void calculateDifferents(boolean _speedPrev)
    {
        short [] as_array = as_pathArray;
        int i_steps=0;
        int i_curOffset = i_currentOffset;

        if (_speedPrev)i_steps = as_array[i_curOffset++];

        int i8_tX = (int)(((long)as_array[i_curOffset++]<<8)*SCALE_WIDTH)>>8;
        int i8_tY = (int)(((long)as_array[i_curOffset++]<<8)*SCALE_HEIGHT)>>8;

        //#if PATH_LOCAL_SCALE
        i8_tX = (int)(((long)i8_tX*(long)i8_localScaleWidth)>>8);
        i8_tY = (int)(((long)i8_tY*(long)i8_localScaleHeight)>>8);
        //#endif

        int i8_cX = i8_curX;
        int i8_cY = i8_curY;

        if (!_speedPrev)i_steps = as_array[i_curOffset++];

        i_currentOffset = i_curOffset;

        int i_mod = i_modify;

        if ((i_mod & MODIFY_FLIPHORZ) != 0)
        {
            i8_tX = 0 - i8_tX;
        }

        if ((i_mod & MODIFY_FLIPVERT) != 0)
        {
            i8_tY = 0 - i8_tY;
        }

        int i8_ddx = (i8_tX - i8_cX) / i_steps;
        int i8_ddy = (i8_tY - i8_cY) / i_steps;

        i8_tX = i8_cX + i8_ddx * i_steps;
        i8_tY = i8_cY + i8_ddy * i_steps;

        i8_dx = i8_ddx;
        i8_dy = i8_ddy;

        i8_targetX = i8_tX;
        i8_targetY = i8_tY;
    }

    /**
     * ������������� ����������� ����
     * @param _centerX ���������� X ����������� ����� �������, ������������� ������� 8 ��������
     * @param _centerY ���������� Y ����������� ����� �������, ������������� ������� 8 ��������
     * @param _sprite  ������, ������������� � ������� �����������
     * @param _pathArray ������, ���������� �������� ����
     * @param _offset �������� � ������� �� ������ �������� ���� ��� ������� �����������
     * @param _initPathPoint ������ ��������� ����� � ���� (������ ����� ����� ������ 0)
     * @param _pathLength ������ ����, ���� 0 ��� ������, �� ������� �� �������� � �������, ���� ������ ����, �� ������������ ��� ���������� ����� � ����
     * @param _modify ����� ����������� ���� MODIFY_NONE, MODIFY_FLIPVERT, MODIFY_FLIPHORZ. ����������� ������������ ������������ ����� 0,0
     */
    //#if PATH_LOCAL_SCALE
    public final void initPath(int _centerX,int _centerY,int _localScaleW,int _localScaleH,Sprite _sprite, short[] _pathArray, int _offset,int _initPathPoint, int _pathLength, int _modify)
    //#else
    //$public final void initPath(int _centerX,int _centerY,Sprite _sprite, short[] _pathArray, int _offset,int _initPathPoint, int _pathLength, int _modify)
    //#endif
    {
        //#if PATH_LOCAL_SCALE
        i8_localScaleWidth = _localScaleW;
        i8_localScaleHeight = _localScaleH;
        //#endif

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
        i_initOffset += _initPathPoint * POINT_DATA_SIZE;
        resetPath();
        calculateDifferents(true);
        i_type = w_acc & 0xF;
        lg_back = false;

        p_sprite.setMainPointXY(i8_centerX+i8_curX, i8_centerY+i8_curY);
    }

    /**
     * �������� ���������� � ��������� ���������, ������ ������������ � ��������� �����       sdfdsfsdf
     */
    public final void resetPath()
    {
        i_curLength = 0;
        lg_back = false;
        lg_completed = false;

        int i_inOffset = i_initOffset;

        int i8_cX = (int)((long)as_pathArray[i_inOffset]*SCALE_WIDTH);
        int i8_cY = (int)((long)as_pathArray[i_inOffset+1]*SCALE_HEIGHT);

        //#if PATH_LOCAL_SCALE
        i8_cX = (int)(((long)i8_cX*(long)i8_localScaleWidth)>>8);
        i8_cY = (int)(((long)i8_cY*(long)i8_localScaleHeight)>>8);
        //#endif

        i8_curX = i8_cX;
        i8_curY = i8_cY;

        i_currentOffset = i_initOffset+(POINT_DATA_SIZE-1);
    }

    /**
     * ���������� ���������� ������������ ����
     * @return TRUE ���� ���� �������, FALSE ���� � ��������
     */
    public final boolean isCompleted()
    {
        return lg_completed;
    }

    /**
     * ��������� ������ ���� ����
     * @return ���������� TRUE ��� ����� ��������� ������� ����� ����
     */
    public final boolean processStep()
    {
        if (lg_completed) return false;

        i8_curX += i8_dx;
        i8_curY += i8_dy;

        p_sprite.setMainPointXY(i8_centerX+i8_curX, i8_centerY+i8_curY);

        int i_len = i_length;

        if (i8_curX == i8_targetX && i8_curY == i8_targetY)
        {
            switch (i_type)
            {
                case TYPE_NORMAL:
                    {
                        i_curLength++;
                        if (i_curLength == i_len)
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
                            if (i_curLength == i_len)
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
                        if (i_curLength == i_len)
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
