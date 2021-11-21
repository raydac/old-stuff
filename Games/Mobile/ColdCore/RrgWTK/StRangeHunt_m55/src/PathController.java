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
 * @version 1.7
 * @since 04 nov 2005
 */
public final class PathController
{
    //#global PATH_LOCAL_SCALE=true

    public static final int NOTIFY_EVERYPOINT = 0x10;
    public static final int NOTIFY_ENDPOINT = 0x20;

    public short[] ash_pathArray;// Указатель на массив
    private int i_initOffset;//Инициализирующее значения смещения, указывает на первую точку пути
    private int i_currentOffset;//Смещение до текущей точки
    private SpriteCollection [] ap_spriteCollection;
    public int i_spriteCollectionID;// Прилинкованный спрайт
    public int i_spriteOffset;// Прилинкованный спрайт
    private int i_length;// Размер пути в точках
    private int i_curPointIndex;// Номер текущей точки
    protected int i8_dx;// Приращение координаты X
    protected int i8_dy;// Приращение координаты Y
    private int i8_targetX;// Координата X целевой точки
    private int i8_targetY;// Координата Y целевой точки
    private int i_modify;// флаги модификации путевых точек
    private int i8_curX;// Текущая координата X
    private int i8_curY;// Текущая координата Y
    private int i_type;// Тип пути
    public boolean lg_completed; // Флаг, показывающий, что прохождения пути завершено

    public int i_PathControllerID; // Идентификатор контроллера

    //#if PATH_LOCAL_SCALE
    private int i8_localScaleWidth = 0x100;
    private int i8_localScaleHeight = 0x100;
    //#endif

    protected static int SCALE_WIDTH = 0x100;
    protected static int SCALE_HEIGHT = 0x100;

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

    /**
     * Изменять только по точкам
     */
    public static final int MODIFY_MOVEPOINTS = 4;

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
     * Зацикленный, спрайт дойдя до последней точки, переходит на первую
     */
    public static final int TYPE_CYCLED = 2;

    /**
     * Зацикленный, спрайт дойдя до последней точки и плавно переходин на первую
     */
    public static final int TYPE_CYCLED_SMOOTH = 3;

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
        return i_curPointIndex;
    }

    /**
     * количество байт, требуемое для записи объекта
     */
    //#if PATH_LOCAL_SCALE
    public static final int DATASIZE_BYTES = 57;
    //#else
    //$public static final int DATASIZE_BYTES = 49;
    //#endif

    /**
     * Записываем состояние контроллера в поток
     * @param _stream поток
     * @throws java.io.IOException исключение, генерируется при проблемах записи
     */
    public final void writePathToStream(DataOutputStream _stream) throws IOException
    {
        _stream.writeInt(i_PathControllerID);
        _stream.writeInt(i8_dx);
        _stream.writeInt(i8_dy);
        _stream.writeInt(i8_targetX);
        _stream.writeInt(i8_targetY);
        _stream.writeInt(i8_curX);
        _stream.writeInt(i8_curY);
        _stream.writeInt(i8_centerX);
        _stream.writeInt(i8_centerY);

        _stream.writeShort(i_initOffset);
        _stream.writeShort(i_currentOffset);
        _stream.writeShort(i_spriteOffset);
        _stream.writeShort(i_length);
        _stream.writeShort(i_curPointIndex);

        _stream.writeByte(i_spriteCollectionID);

        int i_pack = (lg_completed ? 0x80 :0) | (lg_back ? 0x40:0) | i_modify;
        _stream.writeByte(i_pack);
        _stream.writeByte(i_type);

        //#if PATH_LOCAL_SCALE
        _stream.writeInt(i8_localScaleWidth);
        _stream.writeInt(i8_localScaleHeight);
        //#endif
    }

    /**
     * Загружаем контроллер пути из потока
     * @throws java.io.IOException исключение, генерируется при проблемах при чтении данных
     */
    public void readPathFromStream(DataInputStream _stream, SpriteCollection [] _spriteCollections, short [] _pathArray) throws IOException
    {
        ap_spriteCollection = _spriteCollections;
        ash_pathArray = _pathArray;

        i_PathControllerID = _stream.readInt();
        i8_dx = _stream.readInt();
        i8_dy = _stream.readInt();
        i8_targetX = _stream.readInt();
        i8_targetY = _stream.readInt();
        i8_curX = _stream.readInt();
        i8_curY = _stream.readInt();
        i8_centerX = _stream.readInt();
        i8_centerY = _stream.readInt();

        i_initOffset = _stream.readUnsignedShort();
        i_currentOffset = _stream.readUnsignedShort();
        i_spriteOffset = _stream.readUnsignedShort();
        i_length = _stream.readUnsignedShort();
        i_curPointIndex = _stream.readUnsignedShort();

        i_spriteCollectionID = _stream.readUnsignedByte();
        int i_pack = _stream.readUnsignedByte();
        i_type = _stream.readUnsignedByte();

        lg_completed = (i_pack & 0x80)!=0;
        lg_back = (i_pack & 0x40)!=0;
        i_modify = i_pack & 0x3;

        //#if PATH_LOCAL_SCALE
        i8_localScaleWidth = _stream.readInt();
        i8_localScaleHeight = _stream.readInt();
        //#endif
    }

    /**
     * Расчитываем новые характеристики для перехода к следующей точке
     * после обработки функции позиция на шагах для текущего отрезка, в случае нормального режима
     */
    private void calculateDifferents(boolean _stepsNormal,int _steps)
    {
        short [] as_array = ash_pathArray;
        int i_steps=_steps;
        int i_curOffset = i_currentOffset;

        if (_stepsNormal)i_steps = as_array[i_curOffset++];

        int i8_tX = as_array[i_curOffset++]*SCALE_WIDTH;
        int i8_tY = as_array[i_curOffset++]*SCALE_HEIGHT;

        //#if PATH_LOCAL_SCALE
        i8_tX = (int)(((long)i8_tX*(long)i8_localScaleWidth+0x7F)>>8);
        i8_tY = (int)(((long)i8_tY*(long)i8_localScaleHeight+0x7F)>>8);
        //#endif

        int i8_cX = i8_curX;
        int i8_cY = i8_curY;

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
     * @param _pathID идентификатор пути
     * @param _centerX X точки отсчета в формате I8
     * @param _centerY Y точки отсчета в формате I8
     * @param _localScaleW локальный коэффициент горизонтального масштабирования в формате I8
     * @param _localScaleH локальный коэффициент вертикального масштабирования в формате I8
     * @param _spriteCollections массив спрайт коллекций
     * @param _spriteCollectionID идентификатор коллекции спрафтов (индекс в массиве коллекций)
     * @param _spriteOffset смещение спрайта
     * @param _pathArray массив параметров пути
     * @param _offset смещение до параметров заданого пути в массиве
     * @param _initPathPoint начальная точка пути, 0 - первая
     * @param _pathLength количество точек к обработке, 0 - все
     * @param _modify флаги модификации пути
     */
    public final void initPath(int _pathID,int _centerX,int _centerY,int _localScaleW,int _localScaleH,SpriteCollection [] _spriteCollections,int _spriteCollectionID,int _spriteOffset, short[] _pathArray, int _offset,int _initPathPoint, int _pathLength, int _modify)
    {
        i_PathControllerID = _pathID;

        //#if PATH_LOCAL_SCALE
        i8_localScaleWidth = _localScaleW;
        i8_localScaleHeight = _localScaleH;
        //#endif

        i8_centerX = _centerX;
        i8_centerY = _centerY;
        i_modify = _modify;
        i_initOffset = _offset;

        ap_spriteCollection = _spriteCollections;
        i_spriteCollectionID = _spriteCollectionID;
        i_spriteOffset = _spriteOffset;

        ash_pathArray = _pathArray;
        i_length = ash_pathArray[i_initOffset++];
        if (_pathLength>0)
            i_length = _pathLength;
        else
            if (_initPathPoint>0)
                i_length -= _initPathPoint;
        short w_acc = ash_pathArray[i_initOffset++];
        i_initOffset += _initPathPoint * POINT_DATA_SIZE;
        resetPath();
        calculateDifferents(true,0);
        i_type = w_acc;
        lg_back = false;

        _spriteCollections[_spriteCollectionID].setMainPointXY(_spriteOffset,i8_centerX+i8_curX, i8_centerY+i8_curY);
    }

    /**
     * Сбросить контроллер в начальное состояние, спрайт перемещается в начальную точку
     * устанавливает текущую точку как текущую, выставляя номер текущей точки в 0
     * после отработки данной функции, указатель ставится на скорость пути на отрезке
     */
    public final void resetPath()
    {
        i_curPointIndex = 0;
        lg_back = false;
        lg_completed = false;

        int i_inOffset = i_initOffset;

        int i8_cX = ash_pathArray[i_inOffset]*SCALE_WIDTH;
        int i8_cY = ash_pathArray[i_inOffset+1]*SCALE_HEIGHT;

        //#if PATH_LOCAL_SCALE
        i8_cX = (int)(((long)i8_cX*(long)i8_localScaleWidth+0x7F)>>8);
        i8_cY = (int)(((long)i8_cY*(long)i8_localScaleHeight+0x7F)>>8);
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

    public void setNotifyFlags(final int _flags)
    {
        i_type = _flags | (i_type & 0xF);
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

        if ((i_modify & MODIFY_MOVEPOINTS)==0)
            ap_spriteCollection[i_spriteCollectionID].setMainPointXY(i_spriteOffset,i8_centerX+i8_curX, i8_centerY+i8_curY);

        int i_len = i_length;

        if (i8_curX == i8_targetX && i8_curY == i8_targetY)
        {
            if ((i_modify & MODIFY_MOVEPOINTS)!=0)
                ap_spriteCollection[i_spriteCollectionID].setMainPointXY(i_spriteOffset,i8_centerX+i8_curX, i8_centerY+i8_curY);

            switch (i_type & 0xF)
            {
                case TYPE_NORMAL:
                    {
                        i_curPointIndex++;
                        if (i_curPointIndex == i_len)
                            lg_completed = true;
                        else
                            calculateDifferents(true,0);
                    }
                    ;
                    break;
                case TYPE_PENDULUM:
                    {
                        boolean lg_forward = true;

                        if (lg_back)
                        {
                            if (i_curPointIndex == 0)
                            {
                                lg_back = false;
                            }
                            else
                            {
                                i_curPointIndex --;
                                i_currentOffset -= (POINT_DATA_SIZE*2-1);
                                lg_forward = false;
                            }
                        }
                        else
                        {
                            i_curPointIndex ++;
                            if (i_curPointIndex == i_len)
                            {
                                lg_back = true;
                                i_currentOffset -= (POINT_DATA_SIZE*2-1);
                                i_curPointIndex --;
                                lg_forward = false;
                            }
                        }

                        if (lg_forward)
                        {
                            calculateDifferents(true,0);
                        }
                        else
                        {
                            int i_steps = ash_pathArray[i_currentOffset+(POINT_DATA_SIZE-1)];
                            calculateDifferents(false,i_steps);
                        }
                    }
                    ;
                    break;
                case TYPE_CYCLED:
                    {
                        i_curPointIndex ++;
                        if (i_curPointIndex == i_len)
                        {
                            resetPath();
                        }
                        calculateDifferents(true,0);
                    };break;
                case TYPE_CYCLED_SMOOTH:
                    {
                        i_curPointIndex++;
                        if (i_curPointIndex == i_len)
                        {
                            int i_steps = ash_pathArray[i_currentOffset];
                            i_currentOffset = i_initOffset;
                            calculateDifferents(false,i_steps);
                            i_curPointIndex = -1;
                        }
                        else
                        {
                            calculateDifferents(true,0);
                        }
                    };break;
            }
            if ((i_type & NOTIFY_EVERYPOINT)!=0) Gamelet.notifyPathPointPassed(this);
            if (lg_completed && ((i_type & NOTIFY_ENDPOINT)!=0)) Gamelet.notifyPathCompleted(this);

            return true;
        }
        return false;
    }

    /**
     * Деактивизировать спрайт связанный с путем
     */
    public final void realiseSprite()
    {
        SpriteCollection p_collection = ap_spriteCollection[i_spriteCollectionID];
        p_collection.releaseSprite(i_spriteOffset);
    }

    /**
     * Расчитываем коэффициенты, для сжатия - растяжения пути
     *
     * @param _width  ширина затребованного прямоугольника, в который должен вписаться путь
     * @param _height высота затребованного прямоугольника, в который должен вписаться путь
     * @return пакованные коэффициенты старшие 32 бита это коэфф. по X, младшие 32 бита это коэфф. по Y
     */
    public static long calculateLocalPathCoeff(int _width, int _height, int _pathOffset,short [] _pathArray)
    {
        _pathOffset -= 2;
        int i_w = ((int) ((_pathArray[_pathOffset++] * SCALE_WIDTH) + 0x7F) >> 8);
        int i_h = ((int) ((_pathArray[_pathOffset++] * SCALE_HEIGHT) + 0x7F) >> 8);

        if (i_w == 0) i_w = 1;
        if (i_h == 0) i_h = 1;

        // Рассчитываем коэффициенты
        long l_coeffW = ((long) _width << 8) / (long) i_w;
        long l_coeffH = ((long) _height << 8) / (long) i_h;

        return (l_coeffW << 32) | l_coeffH;
    }

}
