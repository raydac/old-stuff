package mtv.paparazzo;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.DataInputStream;

/**
 * Класс описывает контроллер пути для заданного спрайта.
 *
 * Формат хранения пути в массиве следующий
 * количество_точек, тип_пути,{x,y,step_number}...
 *
 * @author Игорь Мазница
 * @version 1.2
 * @since 27 okt 2004
 */
public class PathController
{
    public short[] as_pathArray;// Указатель на массив
    private int i_initOffset;//Инициализирующее значения смещения, указывает на первую точку пути
    private int i_currentOffset;//Смещение до текущей точки
    public Sprite p_sprite;// Прилинкованный спрайт
    private int i_length;// Размер пути в точках
    private int i_curLength;// Номер текущей точки
    private int i8_dx;// Приращение координаты X
    private int i8_dy;// Приращение координаты Y
    private int i8_targetX;// Координата X целевой точки
    private int i8_targetY;// Координата Y целевой точки
    private int i_modify;// флаги модификации путевых точек
    private int i8_curX;// Текущая координата X
    private int i8_curY;// Текущая координата Y
    private int i_type;// Тип пути
    private boolean lg_completed; // Флаг, показывающий, что прохождения пути завершено

    //#global PATH_LOCAL_SCALE=true

    //#if PATH_LOCAL_SCALE
    private int i8_localScaleWidth = 0x100;
    private int i8_localScaleHeight = 0x100;
    //#endif

    protected static long SCALE_WIDTH = 0x100;
    protected static long SCALE_HEIGHT = 0x100;

    /**
     * Координата X центральной точки пути, фиксированная точка 8 разрядов
     */
    public int i8_centerX;
    /**
     * Координата Y центральной точки пути, фиксированная точка 8 разрядов
     */
    public int i8_centerY;

    protected boolean lg_back; //Флаг, показывает, что осуществляется обратный ход

    /**
     * Нет модификации
     */
    public static final int MODIFY_NONE = 0;
    /**
     * Отзеркалить относительно оси Y
     */
    public static final int MODIFY_FLIPVERT = 1;
    /**
     * Отзеркалить относительно оси X
     */
    public static final int MODIFY_FLIPHORZ = 2;

    private static final int POINT_DATA_SIZE = 3; // Размер данных, отводимых на одну точку пути в массиве


    /**
     * Обычныый тип, спрайт проходит до конца пути и работа контроллера прекращается
     */
    public static final int TYPE_NORMAL = 0;
    /**
     * Маятник, спрайт дойдя до последней точки, начинает двигаться в обратнок направлении
     */
    public static final int TYPE_PENDULUM = 1;
    /**
     * Зацикленный, спрайт дойдя до последней точки, переходин на первую
     */
    public static final int TYPE_CYCLED = 2;

    public PathController()
    {
        lg_completed = true;
    }

    /**
     * Отключает путь, переводя его в завершенное состояние
     */
    public void deactivate()
    {
        lg_completed = true;
    }

    /**
     * Возвращает тип контроллера
     * @return тип
     */
    public final int getType()
    {
        return i_type;
    }

    /**
     * Позволяет задать принудительно тип пути
     * @param _value новое значение
     */
    public final void setType(int _value)
    {
        i_type = _value;
    }

    /**
     * Возвращает индекс текущей точки пути
     * @return индекс текущей точки, (0 - первая)
     */
    public final int getCurrentPointIndex()
    {
        return i_curLength;
    }

    /**
     * количество байт, требуемое для записи объекта
     */
    //#if PATH_LOCAL_SCALE
    public static final int DATASIZE_BYTES = 52;
    //#else
    //$public static final int DATASIZE_BYTES = 44;
    //#endif
    
    /**
     * Записываем состояние контроллера в поток
     * @param _stream поток
     * @throws IOException исключение, генерируется при проблемах записи
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
     * Загружаем контроллер пути из потока
     * @param _stream поток
     * @param _sprites массив содержащий спрайты
     * @throws IOException исключение, генерируется при проблемах при чтении данных
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
     * Расчитываем новые характеристики для перехода к следующей точке
     * @param _speedPrev если TRUE, то берем количество шагов на участке как первое значение в массиве, иначе как последние
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
     * Инициализация контроллера пути
     * @param _centerX координата X центральной точки отсчета, фиксированная запятая 8 разрядов
     * @param _centerY координата Y центральной точки отсчета, фиксированная запятая 8 разрядов
     * @param _sprite  спрайт, прикрепленный к данному контроллеру
     * @param _pathArray массив, содержащий описание пути
     * @param _offset смещение в массиве до начала описания пути для данного контроллера
     * @param _initPathPoint индекс начальной точки в пути (первая точка имеет индекс 0)
     * @param _pathLength размер пути, если 0 или меньше, то берется из значения в массиве, если больше нуля, то используется как количество точек в пути
     * @param _modify флаги модификации пути MODIFY_NONE, MODIFY_FLIPVERT, MODIFY_FLIPHORZ. Модификация производится относительно точки 0,0
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
     * Сбросить контроллер в начальное состояние, спрайт перемещается в начальную точку       sdfdsfsdf
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
     * Возвращает показатель пройденности пути
     * @return TRUE если путь пройден, FALSE если в процессе
     */
    public final boolean isCompleted()
    {
        return lg_completed;
    }

    /**
     * Отработка одного шага пути
     * @return возвращает TRUE при смене очередной целевой точки пути
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
