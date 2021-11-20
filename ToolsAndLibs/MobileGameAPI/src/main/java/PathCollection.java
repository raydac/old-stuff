import java.io.DataInputStream;
import java.io.DataOutputStream;

/**
 * Класс описывает коллекцию путей
 * 
 * @author Igor A. Maznitsa (igor.maznitsa@raydac-research.com)
 * @version 2.08
 */
public class PathCollection
{
    /**
     * Переменная хранит смещение к текущему пути при итерации активных путей
     */
    private int i_lastIteratorPathOffset;

    /**
     * Глобальный коэффициент сжатия пути по горизонтали
     */
    public static int SCALE_WIDTH = 0x100;

    /**
     * Глобальный коэффициент сжатия пути по вертикали
     */
    public static int SCALE_HEIGHT = 0x100;

    /**
     * Флаг вертикального зеркалирования пути
     */
    public static final int MODIFY_FLIPVERT = 1;

    /**
     * Флаг горизонтального зеркалирования пути
     */
    public static final int MODIFY_FLIPHORZ = 2;

    /**
     * Тип пути нормальный, путь такого типа проигрывается от начала до конца и переходит в неактивный режим
     */
    public static final int PATHTYPE_NORMAL = 0;

    /**
     * Тип пути "маятниковый", путь такого типа бесконечно проигрывается от начала до конца и обратно
     */
    public static final int PATHTYPE_PENDULUM = 1;

    /**
     * Тип пути "зацикленный", путь такого типа бесконечно проигрывается от начала до конца. Пройдя последнюю точку, объект появляется в первой.
     */
    public static final int PATHTYPE_CYCLED = 2;

    /**
     * Тип пути "зацикленный сглаженный", путь такого типа бесконечно проигрывается от начала до конца. Пройдя последнюю точку, объект плавно перемещается в первую.
     */
    public static final int PATHTYPE_CYCLEDSMOOTH = 3;

    /**
     * Флаг уведомления геймлета при прохождении каждой точки пути
     */
    public static final int NOTIFY_EVERYPOINT = 0x1;

    /**
     * Флаг уведомления геймлета при окончании прохождения
     */
    public static final int NOTIFY_ENDPOINT = 0x2;

    /**
     * Маска типа пути
     */
    public static final int MASK_PATHTYPE = 0x3;

    /**
     * Сдвиг вправо типа пути
     */
    public static final int SHR_PATHTYPE = 0;

    /**
     * Маска флагов уведомлений
     */
    public static final int MASK_NOTIFYFLAGS = 0x3C;

    /**
     * Сдвиг вправо флагов уведомлений
     */
    public static final int SHR_NOTIFYFLAGS = 2;

    /**
     * Маска флагов модификации путей
     */
    public static final int MASK_PATHMODIFY = 0x1C0;

    /**
     * Сдвиг вправо флагов модификации путей
     */
    public static final int SHR_PATHMODIFY = 6;

    /**
     * Маска размера пути в точках
     */
    public static final int MASK_PATHLEN = 0x7FE00;

    /**
     * Сдвиг вправо размера пути в точках
     */
    public static final int SHR_PATHLEN = 9;

    /**
     * Маска индекса текущей точки пути
     */
    public static final int MASK_PATHCURPOINT = 0x1FF80000;

    /**
     * Смещение вправо индекса текущей точки пути
     */
    public static final int SHR_PATHCURPOINT = 19;

    /**
     * Маска флага обратного движения пути
     */
    public static final int MASK_BACKFLAG = 0x20000000;

    /**
     * Сдвиг вправо флага обратного движения пути
     */
    public static final int SHR_BACKFLAG = 29;

    /**
     * Маска флага постановки пути на паузу
     */
    public static final int MASK_PAUSED = 0x40000000;

    /**
     * Сдвиг вправо флага постановки пути на паузу
     */
    public static final int SHR_PAUSED = 30;

    /**
     * Маска флага активности пути
     */
    public static final int MASK_ACTIVE = 0x80000000;

    /**
     * Сдвиг вправо флага активности пути
     */
    public static final int SHR_ACTIVE = 31;

    /**
     * Маска индекса предыдущего пути в списке
     */
    private static final int MASK_PREVITEM = 0x0000FFFF;

    /**
     * Сдвиг вправо индекса предыдущего пути в списке
     */
    private static final int SHR_PREVITEM = 0;

    /**
     * Маска индекса следующего пути в списке
     */
    private static final int MASK_NEXTITEM = 0xFFFF0000;

    /**
     * Сдвиг вправо индекса следующего пути в списке
     */
    private static final int SHR_NEXTITEM = 16;

    /**
     * Маска индекса коллекции спрайтов с которой связан путь в массиве коллекций
     */
    private static final int MASK_SPRITECOLLECTION = 0xFF000000;

    /**
     * Сдвиг вправо индекса коллекции спрайтов с которой связан путь в массиве коллекций
     */
    private static final int SHR_SPRITECOLLECTION = 24;

    /**
     * Маска смещения спрайта в массиве коллекции спрайтов
     */
    private static final int MASK_SPRITEOFFSET = 0x00FFFFFF;

    /**
     * Сдвиг вправо смещения спрайта в массиве коллекции спрайтов
     */
    private static final int SHR_SPRITEOFFSET = 0;

    /**
     * Маска хранения опциональных данных пути, не более 12 бит
     */
    public static final int MASK_OPTIONALDATA = 0xFFF00000;

    /**
     * Сдвиг вправо маски хранения опциональных данных пути
     */
    public static final int SHR_OPTIONALDATA = 20;

    /**
     * Маска хранения первой инициализированной точки пути
     */
    public static final int MASK_INITPATHOFFSET = 0x000FFFFF;

    /**
     * Сдвиг вправо маски хранения смещения первой инициализированной точки пути
     */
    public static final int SHR_INITPATHOFFSET = 0;

    /**
     * Смещение поля, содержащего индексы следующего в списке пути и предыдущего. 0xFFFF, показывает что элемент граничный
     * PREVITEM, NEXTITEM
     */
    public static final int OFFSET_LISTINDEXES = 0;

    /**
     * Смещение поля, содержащего смещение пути в массиве путей и опциональные данные
     * OPTIONALDATA, INITPATHOFFSET
     */
    public static final int OFFSET_PATHOFFSETDATA = 1;

    /**
     * Смещение до поля специальных данных пути
     * поле содержит поля TYPE,NOTIFY,MODIFY,LENGTH,CURPOINT,BACK,PAUSED,ACTIVE
     */
    public static final int OFFSET_PATHSPECDATA = 2;

    /**
     * Смещение до поля, содержащего кординату X точки отсчета координат пути
     */
    public static final int OFFSET_MAINX_I8 = 3;

    /**
     * Смещение до поля, содержащего кординату Y точки отсчета координат пути
     */
    public static final int OFFSET_MAINY_I8 = 4;

    /**
     * Смещение до поля, содержащего текущую кординату X спрайта на пути
     */
    public static final int OFFSET_CURX_I8 = 5;

    /**
     * Смещение до поля, содержащего текущую кординату Y спрайта на пути
     */
    public static final int OFFSET_CURY_I8 = 6;

    /**
     * Смещение до поля, содержащего целевую кординату X спрайта на пути
     */
    public static final int OFFSET_DESTX_I8 = 7;

    /**
     * Смещение до поля, содержащего целевую кординату Y спрайта на пути
     */
    public static final int OFFSET_DESTY_I8 = 8;

    /**
     * Смещение до поля, содержащего текущее приращение координаты X
     */
    public static final int OFFSET_DELTAX_I8 = 9;

    /**
     * Смещение до поля, содержащего текущее приращение координаты Y
     */
    public static final int OFFSET_DELTAY_I8 = 10;

    /**
     * Смещение до поля, содержащего данные о коллекции и спрайте, связанных с путем
     * COLLECTION, SPRITEOFFSET
     */
    public static final int OFFSET_SPRITEDATA = 11;

    /**
     * Смещение до поля, содержащего данные о локальном коэффициенте сжатия по X
     */
    public static final int OFFSET_LOCALSCALEWIDTH_I8 = 12;

    /**
     * Смещение до поля, содержащего данные о локальном коэффициенте сжатия по Y
     */
    public static final int OFFSET_LOCALSCALEHEIGHT_I8 = 13;

    /**
     * Указатель на массив, содержащий базовые данные путей
     */
    private short[] ash_pathStaticDataArray;

    /**
     * Массив, содержащий коллекции спрайтов
     */
    private SpriteCollection[] ap_SpriteCollections;

    /**
     * Массив, содержащий данные текущих активных путей
     */
    public int[] ai_PathDynamicDataArray;

    /**
     * Идентификатор коллекции
     */
    public int i_CollectionID;

    /**
     * Размер одного описателя пути в массиве
     */
    public static final int PATHDATACELLSNUMBER = 14;

    /**
     * Смещение последнего активного пути в списке
     */
    public int i_lastActivePathOffset;

    /**
     * Смещение последнего неактивного пути в списке
     */
    public int i_lastInactivePathOffset;

    /**
     * Количество путей в коллекции
     */
    public int i_PathsNumber;

    /**
     * Конструктор
     * @param _collectionID идентификатор коллекции
     * @param _pathsNumber количество путей в коллекции
     * @param _spriteCollections массив коллекций спрайтов
     * @param _pathArray массив статических данных путей
     */
    public PathCollection(int _collectionID, int _pathsNumber, SpriteCollection[] _spriteCollections, short[] _pathArray)
    {
        i_PathsNumber = _pathsNumber;
        i_CollectionID = _collectionID;
        ash_pathStaticDataArray = _pathArray;
        ap_SpriteCollections = _spriteCollections;
        ai_PathDynamicDataArray = new int[PATHDATACELLSNUMBER * _pathsNumber];
        releaseAll();
    }

    /**
     * Проверка на то, пересекла ли текущая точка пути некую линию на текущем шаге и возврат координаты пересечения 
     * @param _offset смещение данных пути в массиве
     * @param _i8coord координата по X или Y, что зависит от параметра _vert
     * @param _vert true если требуется получить координату Y пересечения с вертикальной линией и X если требуется получить координату X пересечения с горизонтальной линией 
     * @return если присутствует пересчение то координата в формате I8 и если отсутствует, то 0x7FFFFFFF 
     */
    public final int getIntersectionCoord(int _offset, int _i8coord, boolean _vert)
    {
        final int NULL = 0x7FFFFFFF;

        final int[] ai_patharray = ai_PathDynamicDataArray;
        final short[] ash_patharray = ash_pathStaticDataArray;

        _offset += OFFSET_PATHOFFSETDATA;

        int i_pathOffsetData = (ai_patharray[_offset++] & MASK_INITPATHOFFSET) >>> SHR_INITPATHOFFSET;
        int i_pathSpecial = ai_patharray[_offset++];
        int i8_mainx = ai_patharray[_offset++];
        int i8_mainy = ai_patharray[_offset++];
        int i8_curx = i8_mainx + ai_patharray[_offset++];
        int i8_cury = i8_mainy + ai_patharray[_offset++];
        int i8_destx = i8_mainx+ai_patharray[_offset++];
        int i8_desty = i8_mainy+ai_patharray[_offset++];
        int i8_deltax = ai_patharray[_offset++];
        int i8_deltay = ai_patharray[_offset];

        // если неактивен то возвращаем флаг непересечения
        if ((i_pathSpecial & MASK_ACTIVE) == 0)
            return NULL;

        // выявляем активную точку и её смещение в статических данных
        int i_curPoint = (i_pathSpecial & MASK_PATHCURPOINT) >>> SHR_PATHCURPOINT;
        _offset = i_pathOffsetData + ((i_curPoint << 1) + i_curPoint);

        // вычисляем стартовую точку
        int i8_startx = i8_mainx+(((int) ash_patharray[_offset++]) << 8);
        int i8_starty = i8_mainy+(((int) ash_patharray[_offset++]) << 8);

        
        
        // точка уже прошла какое то расстояние, смещаем на шаг назад
        int i8_prevx = (i8_curx - i8_deltax);
        int i8_prevy = (i8_cury - i8_deltay);

        // вычисляем угловой коэффициент
        int i_diffx = i8_destx - i8_startx;
        int i_diffy = i8_desty - i8_starty;

        // проверяем на достижимость
        if (_vert && i_diffx == 0)  return NULL;
        if (!_vert && i_diffy == 0) return NULL;

        if (i_diffx == 0)
            i_diffx = 1;
        int i8_coeff = ((i_diffy) << 8) / (i_diffx);

        if (_vert)
        {
            // проверка на пересеченность с вертикальной линией

            // проверяем на частный случай, когда отрезок не пересекается
            if ((i8_curx > _i8coord && i8_prevx > _i8coord) || (i8_curx < _i8coord && i8_prevx < _i8coord))
                return NULL;

            // вычисляем координату Y пересечения
            return i8_starty + (((_i8coord - i8_startx) * i8_coeff) >> 8);
        }
        else
        {
            // проверка на пересеченность с горизонтальной линией

            // проверяем на частный случай, когда отрезок не пересекается
            if ((i8_cury > _i8coord && i8_prevy > _i8coord) || (i8_cury < _i8coord && i8_prevy < _i8coord))
                return NULL;

            // вычисляем координату X пересечения
            if (i8_coeff == 0) i8_coeff = 1;
            return i8_startx + (((_i8coord-i8_starty) << 8) / i8_coeff);
        }
    }

    /**
     * Обработка всех активных путей, не поставленных на паузу
     */
    public final void processActive()
    {
        int i_pathOffset = i_lastActivePathOffset;
        if (i_pathOffset < 0)
            return;

        final int[] ai_dynPathArray = ai_PathDynamicDataArray;

        while (true)
        {
            final int i_nextPath = ((ai_dynPathArray[i_pathOffset + OFFSET_LISTINDEXES]) & MASK_PREVITEM) >>> SHR_PREVITEM;
            int i_offset = i_pathOffset + OFFSET_PATHSPECDATA;

            int i_pathSpecial = ai_dynPathArray[i_offset++];

            // проверяем не стоит ли путь на паузе и если стоит то не отрабатываем
            if ((i_pathSpecial & MASK_PAUSED) == 0)
            {
                final int i8_mainX = ai_dynPathArray[i_offset++];
                final int i8_mainY = ai_dynPathArray[i_offset++];
                int i8_curX = ai_dynPathArray[i_offset++];
                int i8_curY = ai_dynPathArray[i_offset++];
                int i8_destX = ai_dynPathArray[i_offset++];
                int i8_destY = ai_dynPathArray[i_offset++];
                int i8_deltaX = ai_dynPathArray[i_offset++];
                int i8_deltaY = ai_dynPathArray[i_offset++];

                SpriteCollection p_SpriteCollection;
                int i_spriteOffset;

                {
                    int i_spriteCollectionIndex = ai_dynPathArray[i_offset++];
                    i_spriteOffset = (i_spriteCollectionIndex & MASK_SPRITEOFFSET) >>> SHR_SPRITEOFFSET;
                    p_SpriteCollection = ap_SpriteCollections[(i_spriteCollectionIndex & MASK_SPRITECOLLECTION) >>> SHR_SPRITECOLLECTION];
                }

                i8_curX += i8_deltaX;
                i8_curY += i8_deltaY;

                p_SpriteCollection.setMainPointXY(i_spriteOffset, i8_mainX + i8_curX, i8_mainY + i8_curY);

                i_offset = i_pathOffset + OFFSET_CURX_I8;
                ai_dynPathArray[i_offset] = i8_curX;
                i_offset++;
                ai_dynPathArray[i_offset] = i8_curY;

                if (i8_curX == i8_destX && i8_curY == i8_destY)
                {
                    final int i_pointsLen = (i_pathSpecial & MASK_PATHLEN) >>> SHR_PATHLEN;
                    int i_curPoint = (i_pathSpecial & MASK_PATHCURPOINT) >>> SHR_PATHCURPOINT;

                    // путь не стоит на паузе
                    final int i_pathType = (i_pathSpecial & MASK_PATHTYPE) >>> SHR_PATHTYPE;

                    boolean lg_completed = false;
                    final int i_notifyFlags = (i_pathSpecial & MASK_NOTIFYFLAGS) >>> SHR_NOTIFYFLAGS;

                    switch (i_pathType)
                    {
                        case PATHTYPE_NORMAL:
                        {
                            i_curPoint++;
                            if (i_curPoint == i_pointsLen)
                            {
                                // путь закончен
                                lg_completed = true;
                            }
                            else
                            {
                                // рассчитываем следующую точку
                                ai_dynPathArray[i_pathOffset + OFFSET_PATHSPECDATA] = (i_pathSpecial & ~MASK_PATHCURPOINT) | (i_curPoint << SHR_PATHCURPOINT);
                                _calculateDelta(i_pathOffset, true);
                            }
                        }
                            ;
                            break;
                        case PATHTYPE_PENDULUM:
                        {
                            boolean lg_forward = true;

                            if ((i_pathSpecial & MASK_BACKFLAG) != 0)
                            {
                                i_curPoint--;
                                if (i_curPoint == 0)
                                {
                                    i_pathSpecial = i_pathSpecial & ~MASK_BACKFLAG;
                                }
                                else
                                {
                                    lg_forward = false;
                                }
                                i_pathSpecial = (i_pathSpecial & ~MASK_PATHCURPOINT) | (i_curPoint << SHR_PATHCURPOINT);
                                ai_dynPathArray[i_pathOffset + OFFSET_PATHSPECDATA] = i_pathSpecial;
                            }
                            else
                            {
                                i_curPoint++;
                                if (i_curPoint == i_pointsLen)
                                {
                                    i_pathSpecial = (i_pathSpecial & ~MASK_PATHCURPOINT) | (i_curPoint << SHR_PATHCURPOINT);
                                    ai_dynPathArray[i_pathOffset + OFFSET_PATHSPECDATA] = i_pathSpecial | MASK_BACKFLAG;
                                    lg_forward = false;
                                }
                                else
                                {
                                    // рассчитываем следующую точку
                                    ai_dynPathArray[i_pathOffset + OFFSET_PATHSPECDATA] = (i_pathSpecial & ~MASK_PATHCURPOINT) | (i_curPoint << SHR_PATHCURPOINT);
                                }
                            }

                            if (lg_forward)
                            {
                                _calculateDelta(i_pathOffset, true);
                            }
                            else
                            {
                                _calculateDelta(i_pathOffset, false);
                            }
                        }
                            ;
                            break;
                        case PATHTYPE_CYCLED:
                        {
                            i_curPoint++;
                            if (i_curPoint == i_pointsLen)
                            {
                                lg_completed = true;
                                resetOne(i_pathOffset);
                            }
                            else
                            {
                                i_pathSpecial = (i_pathSpecial & ~MASK_PATHCURPOINT) | (i_curPoint << SHR_PATHCURPOINT);
                                ai_dynPathArray[i_pathOffset + OFFSET_PATHSPECDATA] = i_pathSpecial;
                                _calculateDelta(i_pathOffset, true);
                            }
                        }
                            ;
                            break;
                        case PATHTYPE_CYCLEDSMOOTH:
                        {
                            i_curPoint++;
                            if (i_curPoint > i_pointsLen)
                            {
                                i_curPoint = 0;
                                lg_completed = true;
                                ai_dynPathArray[i_pathOffset + OFFSET_PATHSPECDATA] = (i_pathSpecial & ~MASK_PATHCURPOINT) | (i_curPoint << SHR_PATHCURPOINT);
                            }
                            else
                            {
                                i_pathSpecial = (i_pathSpecial & ~MASK_PATHCURPOINT) | (i_curPoint << SHR_PATHCURPOINT);
                                ai_dynPathArray[i_pathOffset + OFFSET_PATHSPECDATA] = i_pathSpecial;
                            }
                            _calculateDelta(i_pathOffset, true);
                        }
                            ;
                            break;
                    }

                    if ((i_notifyFlags & NOTIFY_EVERYPOINT) != 0)
                        Gamelet.notifyPathPointPassed(this, i_pathOffset);

                    if (lg_completed)
                    {
                        if (i_pathType == PATHTYPE_NORMAL)
                        {
                            // деактивируем путь
                            _moveActivePathToInactiveList(i_pathOffset);
                        }

                        if ((i_notifyFlags & NOTIFY_ENDPOINT) != 0)
                        {
                            Gamelet.notifyPathCompleted(this, i_pathOffset);
                        }
                    }

                }

            }

            if (i_nextPath == 0xFFFF)
                break;
            i_pathOffset = i_nextPath;
        }
    }

    /**
     * Расчитываем новые характеристики для перехода к следующей точке пути
     * ВНИМАНИЕ! Не осуществляется проверка активности пути
     * @param _pathOffset смещение пути
     * @param _forward флаг, true показывает что путь идет в последовательно прямом направлении, от младших точек к старшим, если false то идет назад
     */
    private void _calculateDelta(int _pathOffset, boolean _forward)
    {
        final short[] ash_statPathArrayarray = ash_pathStaticDataArray;
        final int[] ai_dynPathArrayarray = ai_PathDynamicDataArray;

        int i_offset = _pathOffset + OFFSET_PATHOFFSETDATA;
        int i_initOffset = (ai_dynPathArrayarray[i_offset++] & MASK_INITPATHOFFSET) >>> SHR_INITPATHOFFSET;

        int i_specData = ai_dynPathArrayarray[i_offset++];
        int i_modifyFlags = (i_specData & MASK_PATHMODIFY) >>> SHR_PATHMODIFY;

        int i_curPoint = (i_specData & MASK_PATHCURPOINT) >>> SHR_PATHCURPOINT;
        int i_pointsNumber = (i_specData & MASK_PATHLEN) >>> SHR_PATHLEN;

        int i_curOffset, i_steps;

        if (_forward)
        {
            // идем в прямом направлении
            i_curOffset = i_initOffset + ((i_curPoint << 1) + i_curPoint) + 2;
            i_steps = ash_statPathArrayarray[i_curOffset++];
            if (i_curPoint == i_pointsNumber)
            {
                // последняя точка, в качестве целевых координат надо брать координаты первой точки
                i_curOffset = i_initOffset;
            }
        }
        else
        {
            // идем в обратном направлении
            if (i_curPoint == 0)
            {
                // первая точка, так что смещаем оффсет на последнюю
                i_curOffset = i_initOffset + ((i_pointsNumber << 1) + i_pointsNumber) - 1;
                i_steps = ash_statPathArrayarray[i_curOffset--];
                i_curOffset--;
            }
            else
            {
                i_curOffset = i_initOffset + (i_curPoint << 1) + i_curPoint - 1;
                i_steps = ash_statPathArrayarray[i_curOffset--];
                i_curOffset--;
            }
        }

        int i8_destX = ash_statPathArrayarray[i_curOffset++] * SCALE_WIDTH;
        int i8_destY = ash_statPathArrayarray[i_curOffset++] * SCALE_HEIGHT;

        i_offset = _pathOffset + OFFSET_LOCALSCALEWIDTH_I8;
        i8_destX = (int) (((long) i8_destX * (long) ai_dynPathArrayarray[i_offset++] + 0x7F) >> 8);
        i8_destY = (int) (((long) i8_destY * (long) ai_dynPathArrayarray[i_offset] + 0x7F) >> 8);

        i_offset = _pathOffset + OFFSET_CURX_I8;
        int i8_curX = ai_dynPathArrayarray[i_offset++];
        int i8_curY = ai_dynPathArrayarray[i_offset];

        if ((i_modifyFlags & MODIFY_FLIPHORZ) != 0)
        {
            i8_destX = -i8_destX;
        }

        if ((i_modifyFlags & MODIFY_FLIPVERT) != 0)
        {
            i8_destY = -i8_destY;
        }

        int i8_deltaX = (i8_destX - i8_curX) / i_steps;
        int i8_deltaY = (i8_destY - i8_curY) / i_steps;

        i8_destX = i8_curX + i8_deltaX * i_steps;
        i8_destY = i8_curY + i8_deltaY * i_steps;

        i_offset = _pathOffset + OFFSET_DESTX_I8;
        ai_dynPathArrayarray[i_offset] = i8_destX;
        i_offset++;
        ai_dynPathArrayarray[i_offset] = i8_destY;
        i_offset++;
        ai_dynPathArrayarray[i_offset] = i8_deltaX;
        i_offset++;
        ai_dynPathArrayarray[i_offset] = i8_deltaY;
    }

    /**
     * Функция возвращает размер блока в байтах, требуемый для записи заданного количества путей
     * @param _pathsNumber 
     * @return
     */
    public static final int getDataSize(int _pathsNumber)
    {
        return PATHDATACELLSNUMBER * _pathsNumber + 12;
    }

    /**
     * Запись данных путей в поток 
     * @param _outStream поток для записи
     * @throws Throwable порождается если произошла ошибка записи данных
     */
    public final void saveToStream(DataOutputStream _outStream) throws Throwable
    {
        _outStream.writeInt(i_lastIteratorPathOffset);
        _outStream.writeInt(i_lastActivePathOffset);
        _outStream.writeInt(i_lastInactivePathOffset);
        final int LENGTH = PATHDATACELLSNUMBER * i_PathsNumber;
        final int[] ai_arr = ai_PathDynamicDataArray;
        for (int li = 0; li < LENGTH; li++)
            _outStream.writeInt(ai_arr[li]);
        _outStream.flush();
    }

    /**
     * Загрузка данных путей из потока 
     * @param _inStream поток для загрузки
     * @throws Throwable порождается если произошла ошибка загрузки данных
     */
    public final void loadFromStream(DataInputStream _inStream) throws Throwable
    {
        i_lastIteratorPathOffset = _inStream.readInt();
        i_lastActivePathOffset = _inStream.readInt();
        i_lastInactivePathOffset = _inStream.readInt();
        final int LENGTH = PATHDATACELLSNUMBER * i_PathsNumber;
        final int[] ai_arr = ai_PathDynamicDataArray;
        for (int li = 0; li < LENGTH; li++)
            ai_arr[li] = _inStream.readInt();
    }

    /**
     * Инициализация итератора активных путей, после вызова этой функции можно начать перебор при помощи nextActivePathOffset()
     */
    public final void initIterator()
    {
        i_lastIteratorPathOffset = i_lastActivePathOffset;
    }

    /**
     * Возвращает количество активных путей
     * @return количество активных путей
     */
    public final int getActiveNumber()
    {
        int i_lastPath = i_lastActivePathOffset;
        if (i_lastPath < 0)
            return 0;

        final int[] ai_arr = ai_PathDynamicDataArray;

        int i_num = 0;

        while (true)
        {
            i_num++;
            int i_prev = (ai_arr[i_lastPath] & MASK_PREVITEM) >>> SHR_PREVITEM;
            if (i_prev == 0xFFFF)
                break;
            i_lastPath = i_prev;
        }

        return i_num;
    }

    /**
     * Возвращает количество неактивных путей
     * @return количество неактивных путей
     */
    public final int getInactiveNumber()
    {
        int i_lastPath = i_lastInactivePathOffset;
        if (i_lastPath < 0)
            return 0;

        final int[] ai_arr = ai_PathDynamicDataArray;

        int i_num = 0;

        while (true)
        {
            i_num++;
            int i_listIndex = ai_arr[i_lastPath];
            int i_prev = (i_listIndex & MASK_PREVITEM) >>> SHR_PREVITEM;
            if (i_prev == 0xFFFF)
                break;
            i_lastPath = i_prev;
        }

        return i_num;
    }

    /**
     * Получение следующего значения от итератора активных путей
     * @return смещение в массиве спрайтов или -1 если нет больше активных спрайтов
     */
    public final int nextActiveOffset()
    {
        int i_result = i_lastIteratorPathOffset;
        if (i_result >= 0)
        {
            final int i_indexList = ai_PathDynamicDataArray[i_result + OFFSET_LISTINDEXES];
            int i_prev = (i_indexList & MASK_PREVITEM) >>> SHR_PREVITEM;
            if (i_prev == 0xFFFF)
                i_prev = -1;
            i_lastIteratorPathOffset = i_prev;
        }
        return i_result;
    }

    /**
     * Принудительный переход активного пути в состояние отработки первой точки
     * ВНИМАНИЕ! Не осуществляется проверка активности пути
     * @param _pathOffset смещение обрабатываемого пути
     */
    public final void resetOne(final int _pathOffset)
    {
        final int[] ai_dynPathArray = ai_PathDynamicDataArray;
        final short[] ash_staticPathArray = ash_pathStaticDataArray;

        int i_offset = _pathOffset + OFFSET_PATHOFFSETDATA;

        int i_staticPathOffset = (ai_dynPathArray[i_offset++] & MASK_INITPATHOFFSET) >>> SHR_INITPATHOFFSET;
        int i_accum = ai_dynPathArray[i_offset];

        // сбрасываем флаг паузы и обратного пути
        // так же выставляем текущую точку в 0
        i_accum &= (~(MASK_PAUSED | MASK_BACKFLAG | MASK_PATHCURPOINT));
        ai_dynPathArray[i_offset] = i_accum;

        // берем координаты точки
        i_offset = i_staticPathOffset;
        int i8_pointX = ash_staticPathArray[i_offset++] * SCALE_WIDTH;
        int i8_pointY = ash_staticPathArray[i_offset] * SCALE_HEIGHT;

        // применяем локальные коэффициенты пути
        i_offset = _pathOffset + OFFSET_LOCALSCALEWIDTH_I8;
        i8_pointX = (int) (((long) i8_pointX * (long) ai_dynPathArray[i_offset++] + 0x7F) >> 8);
        i8_pointY = (int) (((long) i8_pointY * (long) ai_dynPathArray[i_offset] + 0x7F) >> 8);

        // записываем координаты точки на пути
        i_offset = _pathOffset + OFFSET_CURX_I8;
        ai_dynPathArray[i_offset] = i8_pointX;
        i_offset++;
        ai_dynPathArray[i_offset] = i8_pointY;

        // получаем координаты точки отсчета
        i_offset = _pathOffset + OFFSET_MAINX_I8;
        final int i8_mainX = ai_dynPathArray[i_offset++];
        final int i8_mainY = ai_dynPathArray[i_offset];

        // Выставляем координаты связанного спрайта в стартовую точку 
        int i_sprite = ai_dynPathArray[_pathOffset + OFFSET_SPRITEDATA];
        SpriteCollection p_spriteCollection = ap_SpriteCollections[(i_sprite & MASK_SPRITECOLLECTION) >>> SHR_SPRITECOLLECTION];
        i_sprite = (i_sprite & MASK_SPRITEOFFSET) >>> SHR_SPRITEOFFSET;

        p_spriteCollection.setMainPointXY(i_sprite, i8_mainX + i8_pointX, i8_mainY + i8_pointY);

        // рассчитываем дельту
        _calculateDelta(_pathOffset, true);
    }

    /**
     * Инициализация пути
     * @param _pathOffset смещение в массиве данных инициализируемого пути
     * @param _optionalData опциональные данные (0..0xFFF) 
     * @param _centerX координата X начальной точки координат (формат I8)
     * @param _centerY координата Y начальной точки координат (формат I8)
     * @param _localScaleW координата X начальной точки координат (формат I8)
     * @param _localScaleH координата Y начальной точки координат (формат I8)
     * @param _spriteCollectionIndex индекс в массиве коллекций спрайтов, коллекции содержащей связанный спрайт 
     * @param _spriteOffset смещение в массиве коллекции связанного с путем спрайта 
     * @param _offset смещение начала данных пути в статическом массиве путей
     * @param _initPathPoint индекс первой стартовой точки пути (0 - первая точка)
     * @param _pathLength размер количество точек к обработке пути (0 - все точки)
     * @param _modify флаги модифкации пути
     */
    public final void activateOne(final int _pathOffset, int _optionalData, int _centerX, int _centerY, int _localScaleW, int _localScaleH, int _spriteCollectionIndex, int _spriteOffset, int _offset, int _initPathPoint, int _pathLength, int _modify)
    {
        final int[] ai_dynArray = ai_PathDynamicDataArray;
        final short[] ash_pathArray = ash_pathStaticDataArray;

        int i_staticOffset = _offset;
        int i_pathLen = ash_pathArray[i_staticOffset++];
        int i_pathType = ash_pathArray[i_staticOffset++];

        if (_pathLength > 0)
        {
            i_pathLen = _pathLength;

            if (_initPathPoint > 0)
            {
                i_staticOffset += (_initPathPoint << 1) + _initPathPoint;
            }
        }
        else if (_initPathPoint > 0)
        {
            i_pathLen -= _initPathPoint;
            i_staticOffset += (_initPathPoint << 1) + _initPathPoint;
        }

        // проверяем не активизирован ли путь уже
        int i_pathData = ai_PathDynamicDataArray[_pathOffset + OFFSET_PATHSPECDATA];
        if ((i_pathData & MASK_ACTIVE) == 0)
        {
            // переводим путь в активные
            _moveInactivePathToActiveList(_pathOffset);
        }

        // упаковываем значение из байта в 6 битное представление
        i_pathType = (((i_pathType >>> 4) << SHR_NOTIFYFLAGS) & MASK_NOTIFYFLAGS) | ((i_pathType << SHR_PATHTYPE) & MASK_PATHTYPE);

        i_pathData = MASK_ACTIVE | ((_modify << SHR_PATHMODIFY) & MASK_PATHMODIFY) | i_pathType | ((i_pathLen << SHR_PATHLEN) & MASK_PATHLEN);

        int i_curPathOffset = _pathOffset + 1;

        // данные смещения и опциональные данные
        ai_dynArray[i_curPathOffset] = ((_optionalData << SHR_OPTIONALDATA) & MASK_OPTIONALDATA) | ((i_staticOffset << SHR_INITPATHOFFSET) & MASK_INITPATHOFFSET);
        i_curPathOffset++;

        // служебные данные
        ai_dynArray[i_curPathOffset] = i_pathData;
        i_curPathOffset++;

        // Данные точки отсчета координат
        ai_dynArray[i_curPathOffset] = _centerX;
        i_curPathOffset++;
        ai_dynArray[i_curPathOffset] = _centerY;
        i_curPathOffset++;

        i_curPathOffset = _pathOffset + OFFSET_SPRITEDATA;

        // данные спрайта
        ai_dynArray[i_curPathOffset] = ((_spriteCollectionIndex << SHR_SPRITECOLLECTION) & MASK_SPRITECOLLECTION) | ((_spriteOffset << SHR_SPRITEOFFSET) & MASK_SPRITEOFFSET);
        i_curPathOffset++;

        // данные локальных коэффициентов пережатия пути
        ai_dynArray[i_curPathOffset] = _localScaleW;
        i_curPathOffset++;
        ai_dynArray[i_curPathOffset] = _localScaleH;

        resetOne(_pathOffset);
    }

    /**
     * Функция возвращает опциональные данные для пути
     * @param _pathOffset смещение пути
     * @return целочисленное положительное значение из поля опциональных данных в диапазоне (0..0xFFF)
     */
    public final int getOptionalData(int _pathOffset)
    {
        return ((ai_PathDynamicDataArray[_pathOffset + OFFSET_PATHOFFSETDATA] & MASK_OPTIONALDATA) >>> SHR_OPTIONALDATA);
    }

    /**
     * Функция задает для заданного пути опциональные данные
     * @param _pathOffset смещение пути
     * @param _data опциональные данные в интервале 0x0..0xFFF;
     */
    public final void setOptionalData(int _pathOffset, int _data)
    {
        _pathOffset += OFFSET_PATHOFFSETDATA;
        int i_data = ai_PathDynamicDataArray[_pathOffset];

        i_data &= ~MASK_OPTIONALDATA;
        i_data |= ((_data << SHR_OPTIONALDATA) & MASK_OPTIONALDATA);

        ai_PathDynamicDataArray[_pathOffset] = i_data;
    }

    /**
     * Функция возвращает упакованное значение номера коллекции спрайта и смещение спрайта
     * @param _pathOffset смещение данных пути
     * @return упакованное значени SPRITE_COLLECTION<<<24 | SPRITE_OFFSET
     */
    public final int getSpriteInfo(int _pathOffset)
    {
        return ai_PathDynamicDataArray[_pathOffset + OFFSET_SPRITEDATA];
    }

    /**
     * Функция позволяет задать новое значение прилинкованного спрайта
     * @param _pathOffset смещение данных пути
     * @return упакованное значени SPRITE_COLLECTION<<<24 | SPRITE_OFFSET
     */
    public final void setSpriteInfo(int _pathOffset, int _collectionIndex, int _spriteOffset)
    {
        final int[] ai_arr = ai_PathDynamicDataArray;
        ai_arr[_pathOffset + OFFSET_SPRITEDATA] = ((_collectionIndex << SHR_SPRITECOLLECTION) & MASK_SPRITECOLLECTION) | ((_spriteOffset << SHR_SPRITEOFFSET) & MASK_SPRITEOFFSET);
        _pathOffset += OFFSET_MAINX_I8;
        int i8_mainX = ai_arr[_pathOffset];
        _pathOffset++;
        int i8_mainY = ai_arr[_pathOffset];
        _pathOffset++;
        int i8_curX = ai_arr[_pathOffset];
        _pathOffset++;
        int i8_curY = ai_arr[_pathOffset];
        _pathOffset++;
        ap_SpriteCollections[_collectionIndex].setMainPointXY(_spriteOffset, i8_mainX + i8_curX, i8_mainY + i8_curY);
    }

    /**
     * Перенести неактивный путь из списка неактивных в список активных
     * ВНИМАНИЕ! Не осуществляется проверка активности пути
     * @param _pathOffset смещение пути
     */
    private final void _moveInactivePathToActiveList(int _pathOffset)
    {
        final int[] ai_dynPathArray = ai_PathDynamicDataArray;

        final int i_curPathListIndexOffset = _pathOffset + OFFSET_LISTINDEXES;
        int i_offsetListData = ai_dynPathArray[i_curPathListIndexOffset];

        // "склеиваем" список неактивных
        int i_prevItem = (i_offsetListData & MASK_PREVITEM) >>> SHR_PREVITEM;
        int i_nextItem = (i_offsetListData & MASK_NEXTITEM) >>> SHR_NEXTITEM;

        if (i_prevItem != 0xFFFF)
        {
            final int i_acc = i_prevItem + OFFSET_LISTINDEXES;
            int i_old = ai_dynPathArray[i_acc];
            i_old &= ~MASK_NEXTITEM;
            i_old |= (i_nextItem << SHR_NEXTITEM);
            ai_dynPathArray[i_acc] = i_old;
        }

        if (i_nextItem != 0xFFFF)
        {
            final int i_acc = i_nextItem + OFFSET_LISTINDEXES;
            int i_old = ai_dynPathArray[i_acc];
            i_old &= ~MASK_PREVITEM;
            i_old |= (i_prevItem << SHR_PREVITEM);
            ai_dynPathArray[i_acc] = i_old;
        }

        // выставляем индекс последнего активного пути если данный путь был последним
        if (_pathOffset == i_lastInactivePathOffset)
        {
            if (i_prevItem == 0xFFFF)
            {
                if (i_nextItem == 0xFFFF)
                    i_lastInactivePathOffset = -1;
                else
                    i_lastInactivePathOffset = i_nextItem;
            }
            else
            {
                i_lastInactivePathOffset = i_prevItem;
            }
        }

        // подключаем путь к списку активных
        if (i_lastActivePathOffset < 0)
        {
            // этот путь будет первым в списке
            i_lastActivePathOffset = _pathOffset;

            // и хвост и голова граничные
            ai_dynPathArray[i_curPathListIndexOffset] = 0xFFFFFFFF;
        }
        else
        {
            // прописываем у предыдущего пути ссылку на текущий
            int i_lastinPathOffset = i_lastActivePathOffset;
            int i_listData = ai_dynPathArray[i_lastinPathOffset + OFFSET_LISTINDEXES];

            i_listData &= ~MASK_NEXTITEM;
            i_listData |= (_pathOffset << SHR_NEXTITEM);

            ai_dynPathArray[i_lastinPathOffset + OFFSET_LISTINDEXES] = i_listData;

            // прописываем у текущего ссылку на предыдущий и флаг отсутствия следующего
            i_listData = (i_lastinPathOffset << SHR_PREVITEM) | (0xFFFF << SHR_NEXTITEM);
            ai_dynPathArray[i_curPathListIndexOffset] = i_listData;
            i_lastActivePathOffset = _pathOffset;
        }

        // устанавливаем флаг активности
        ai_dynPathArray[_pathOffset + OFFSET_PATHSPECDATA] |= MASK_ACTIVE;
    }

    /**
     * Перенести активный путь из списка активных в список неактивных
     * ВНИМАНИЕ! Не осуществляется проверка активности пути 
     * @param _pathOffset смещение индекса пути в массиве
     */
    private final void _moveActivePathToInactiveList(int _pathOffset)
    {
        final int[] ai_dynPathArray = ai_PathDynamicDataArray;

        final int i_curPathOffsetList = _pathOffset + OFFSET_LISTINDEXES;
        int i_offsetListData = ai_dynPathArray[i_curPathOffsetList];

        // "склеиваем" список активных 
        int i_prevItem = (i_offsetListData & MASK_PREVITEM) >>> SHR_PREVITEM;
        int i_nextItem = (i_offsetListData & MASK_NEXTITEM) >>> SHR_NEXTITEM;

        if (i_prevItem != 0xFFFF)
        {
            final int i_acc = i_prevItem + OFFSET_LISTINDEXES;
            int i_old = ai_dynPathArray[i_acc];
            i_old &= ~MASK_NEXTITEM;
            i_old |= (i_nextItem << SHR_NEXTITEM);
            ai_dynPathArray[i_acc] = i_old;
        }

        if (i_nextItem != 0xFFFF)
        {
            final int i_acc = i_nextItem + OFFSET_LISTINDEXES;
            int i_old = ai_dynPathArray[i_acc];
            i_old &= ~MASK_PREVITEM;
            i_old |= (i_prevItem << SHR_PREVITEM);
            ai_dynPathArray[i_acc] = i_old;
        }

        // выставляем индекс последнего активного пути если данный путь был последним
        if (_pathOffset == i_lastActivePathOffset)
        {
            if (i_prevItem == 0xFFFF)
            {
                if (i_nextItem == 0xFFFF)
                    i_lastActivePathOffset = -1;
                else
                    i_lastActivePathOffset = i_nextItem;
            }
            else
            {
                i_lastActivePathOffset = i_prevItem;
            }
        }

        // подключаем путь к списку неактивных
        if (i_lastInactivePathOffset < 0)
        {
            // этот путь будет первым в списке
            i_lastInactivePathOffset = _pathOffset;

            // и хвост и голова граничные
            ai_dynPathArray[i_curPathOffsetList] = 0xFFFFFFFF;
        }
        else
        {
            // прописываем у предыдущего пути ссылку на текущий
            int i_lastinPathOffset = i_lastInactivePathOffset;
            int i_listData = ai_dynPathArray[i_lastinPathOffset + OFFSET_LISTINDEXES];

            i_listData &= ~MASK_NEXTITEM;
            i_listData |= (_pathOffset << SHR_NEXTITEM);

            ai_dynPathArray[i_lastinPathOffset + OFFSET_LISTINDEXES] = i_listData;

            // прописываем у текущего ссылку на предыдущий и флаг отсутствия следующего
            i_listData = (i_lastinPathOffset << SHR_PREVITEM) | (0xFFFF << SHR_NEXTITEM);
            ai_dynPathArray[i_curPathOffsetList] = i_listData;
            i_lastInactivePathOffset = _pathOffset;
        }

        // сбрасываем флаг активности паузы и обратного движения по пути
        ai_dynPathArray[_pathOffset + OFFSET_PATHSPECDATA] &= ~(MASK_ACTIVE | MASK_PAUSED | MASK_BACKFLAG);
    }

    /**
     * Изготовление копии активного пути
     * @param _srcPath копируемый путь
     * @param _dstPath путь, в который будут скопированы данные
     */
    public final void cloneActive(int _srcPath, int _dstPath)
    {
        if (!isActive(_dstPath))
        {
            // Путь в который будет произведено копирование данных не активен
            _moveInactivePathToActiveList(_dstPath);
        }

        System.arraycopy(ai_PathDynamicDataArray, _srcPath, ai_PathDynamicDataArray, _dstPath, PATHDATACELLSNUMBER);
    }

    /**
     * Проверка пути на активность
     * @param _pathOffset смещение начал данных пути
     * @return true если путь активен и false если неактивен
     */
    public final boolean isActive(int _pathOffset)
    {
        return (ai_PathDynamicDataArray[_pathOffset + OFFSET_PATHSPECDATA] & MASK_ACTIVE) != 0;
    }

    /**
     * Проверка пути на постановку на паузу
     * @param _pathOffset смещение данных пути
     * @return true если путь стоит на паузе, false если путь неактивен или не стоит на паузе
     */
    public final boolean isPaused(int _pathOffset)
    {
        final int i_spec = ai_PathDynamicDataArray[_pathOffset + OFFSET_PATHSPECDATA];
        if ((i_spec & MASK_ACTIVE) != 0)
        {
            return (i_spec & MASK_PAUSED) != 0;
        }
        else
            return false;
    }

    /**
     * Реализация связанного с путем спрайта
     * ВНИМАНИЕ! Не производится проверка активности пути
     * @param _pathOffset смещение пути
     */
    public final void releaseLinkedSprite(int _pathOffset)
    {
        int i_sprieData = ai_PathDynamicDataArray[_pathOffset + OFFSET_SPRITEDATA];

        int i_collectuionID = (i_sprieData & MASK_SPRITECOLLECTION) >>> SHR_SPRITECOLLECTION;
        int i_spriteOffset = (i_sprieData & MASK_SPRITEOFFSET) >>> SHR_SPRITEOFFSET;

        ap_SpriteCollections[i_collectuionID].releaseOne(i_spriteOffset);
    }

    /**
     * Деактивизация активного пути и перевод его в неактивный список
     * @param _pathOffset смещение данных пути
     */
    public final void releaseOne(int _pathOffset)
    {
        int i_pathSpec = ai_PathDynamicDataArray[_pathOffset + OFFSET_PATHSPECDATA];
        if ((i_pathSpec & MASK_ACTIVE) != 0)
        {
            _moveActivePathToInactiveList(_pathOffset);
        }
    }

    /**
     * Деактивизация пути, имеющего в качестве прилинкованного заданный спрайт из заданной коллекции
     * @param _collectionID идентификатор коллекции
     * @param _spriteOffset смещение спрайта в массиве 
     * @return true если спрайт был найден и деактивизирован или false если спрайт не бл найден
     */
    public final boolean releasePathForSprite(int _collectionID, int _spriteOffset)
    {
        int i_result = i_lastActivePathOffset;
        int [] ai_pathArray = ai_PathDynamicDataArray;

        final int i_spritedata = ((_collectionID << SHR_SPRITECOLLECTION) & MASK_SPRITECOLLECTION) | ((_spriteOffset << SHR_SPRITEOFFSET) & MASK_SPRITEOFFSET); 
        
        while(true)
        {
            if (i_result >= 0)
            {
                final int i_indexList = ai_pathArray[i_result + OFFSET_LISTINDEXES];
                
                if (ai_pathArray[i_result+OFFSET_SPRITEDATA] == i_spritedata)
                {
                    _moveActivePathToInactiveList(i_result);
                    return true;
                }

                i_result  = (i_indexList & MASK_PREVITEM) >>> SHR_PREVITEM;
                if (i_result == 0xFFFF) i_result = -1;
            }
            else
                break;
        }
        return false;
    }
    
    /**
     * Возвращает смещение заданного индексом пути в массиве данных
     * @param _index индекс пути
     * @return смещение пути в массиве
     */
    public static final int getOffsetForPathWithIndex(int _index)
    {
        return PATHDATACELLSNUMBER * _index;
    }

    /**
     * Установка или снятие паузы у пути
     * ВНИМАНИЕ! Не производится проверки на активность пути
     * @param _pathOffset смещение данных пути
     * @param _pause флаг паузы пути
     */
    public final void setPause(int _pathOffset, boolean _pause)
    {
        if (_pause)
        {
            ai_PathDynamicDataArray[_pathOffset + OFFSET_PATHSPECDATA] |= MASK_PAUSED;
        }
        else
        {
            ai_PathDynamicDataArray[_pathOffset + OFFSET_PATHSPECDATA] &= ~MASK_PAUSED;
        }
    }

    /**
     * Сдвиг центра координат для заданного пути
     * ВНИМАНИЕ! Не производится проверки на активность пути
     * @param _pathOffset смещение пути
     * @param _i8deltaX смещение по оси X (формат I8)
     * @param _i8deltaY смещение по оси Y (формат I8)
     */
    public final void moveCoordinatCenter(int _pathOffset, int _i8deltaX, int _i8deltaY)
    {
        final int[] ai_dynArray = ai_PathDynamicDataArray;

        int i_offset = _pathOffset + OFFSET_MAINX_I8;
        int i8_mX = ai_dynArray[i_offset] + _i8deltaX;
        ai_dynArray[i_offset] = i8_mX;
        i_offset++;
        int i8_mY = ai_dynArray[i_offset] + _i8deltaY;
        ai_dynArray[i_offset] = i8_mY;
        i_offset++;
        i8_mX += ai_dynArray[i_offset++];
        i8_mY += ai_dynArray[i_offset++];

        i_offset = ai_dynArray[_pathOffset + OFFSET_SPRITEDATA];
        ap_SpriteCollections[(i_offset & MASK_SPRITECOLLECTION) >>> SHR_SPRITECOLLECTION].setMainPointXY((i_offset & MASK_SPRITEOFFSET) >>> SHR_SPRITEOFFSET, i8_mX, i8_mY);
    }

    /**
     * Деактивизация всех активных путей
     */
    public final void releaseAll()
    {
        final int[] ai_paths = ai_PathDynamicDataArray;
        i_lastActivePathOffset = -1;

        int i_currentPathOffset = 0;
        final int i_index = i_PathsNumber;

        // Перебираем последовательно все пути от последнего до первого, деактивизируя их и выставляя их показатели списка
        for (int li = 0; li < i_index; li++)
        {
            int i_packed = 0;

            if (li == 0)
            {
                i_packed = 0xFFFF << SHR_PREVITEM;
            }
            else
            {
                i_packed = (i_currentPathOffset - PATHDATACELLSNUMBER) << SHR_PREVITEM;
            }

            if (li == (i_index - 1))
            {
                i_packed |= (0xFFFF << SHR_NEXTITEM);
            }
            else
            {
                i_packed |= ((i_currentPathOffset + PATHDATACELLSNUMBER) << SHR_NEXTITEM);
            }

            ai_paths[i_currentPathOffset] = i_packed;

            i_currentPathOffset += PATHDATACELLSNUMBER;
        }

        i_lastInactivePathOffset = (i_PathsNumber - 1) * PATHDATACELLSNUMBER;
    }

}
