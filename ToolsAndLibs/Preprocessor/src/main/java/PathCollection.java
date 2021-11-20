import java.io.DataOutputStream;
import java.io.DataInputStream;

public class PathCollection
{
    private static final int PATH_DATASIZE = 15;

    private static final int MASK_PATHACTIVE = 0x80000000;
    private static final int MASK_BACKWAY = 0x40000000;

    /**
     * Значение поведения пути при прохождении точек, по умолчанию
     */
    public static final int BEHAVIOUR_DEFAULT = 0;

    /**
     * Значение поведения пути при прохождении точек, деактивизировать спрайт по достижении конца пути
     */
    public static final int BEHAVIOUR_REMOVESPRITEATEND = 1;

    /**
     * Значение поведения пути при прохождении точек, послыать уведомление по окончании прохождения пути
     */
    private static final int BEHAVIOUR_NOTIFYATEND = 2;

    /**
     * Значение поведения пути при прохождении точек, послыать уведомление по прохождении каждой точки
     */
    private static final int BEHAVIOUR_NOTIFYATEVERYPOINT = 3;

    /**
     * Значение поведения пути при прохождении точек, послыать уведомление по прохождении каждой точки
     */
    private static final int BEHAVIOUR_REPEAT = 4;


    private static final int SHR_MODIFIERS = 30;
    private static final int MSK_MODIFIERS = 3;
    private static final int SHR_TYPE = 28;
    private static final int MSK_TYPE = 3;
    private static final int SHR_PATHLEN = 19;
    private static final int MSK_PATHLEN = 0x1FF;
    public static final int SHR_BEHAVIOUR = 16;
    private static final int MSK_BEHAVIOUR = 0x7;
    public static final int SHR_COLLECTION = 11;
    private static final int MSK_COLLECTION = 0x1F;
    private static final int MSK_CURPOINTINDEX = ~(MASK_BACKWAY | MASK_PATHACTIVE);
    private static final int MSK_OFFSET = 0xFFFF;
    private static final int SHR_OFFSET = 16;
    public static final int MSK_SPRITEINDEX = 0x7FF;

    /**
     * Смещение до ячейки, содержащей смещение предыдущего путти в списке
     */
    public static final int PATHDATAOFFSET_PREVPATH = 0;

    /**
     * Смещение до ячейки, содержащей смещение последующего путти в списке
     */
    public static final int PATHDATAOFFSET_NEXTPATH = 1;

    /**
     * Смещение до ячейки, содержащей флаги модификаторы, тип пути, количество точек пути, индекс коллекции, индекс спрайта
     * MODIFIERS<<30 (2b) | PATH_TYPE<<28 (2b) | path_len<<19 (9b) | behaviour_type<<16 (3b) | collection_index << 11 (5b) | sprite_index (11b)
     */
    public static final int PATHDATAOFFSET_MODIFIERS_PATHTYPE_PATHLEN_COLLECTION_SPRITE = 2;

    /**
     * Смещение до ячейки, содержащей смещение начальной точки пути и текущую точку пути
     * INITOFFSET<<16 | CURRENTOFFSET
     */
    public static final int PATHDATAOFFSET_INITOFFSET_CURRENTOFFSET = 3;

    /**
     * Смещение до ячейки, флаг завершенности работы пути и индекс текущей точки
     * (completed ? 0 : MASK_PATHACTIVE) | (backway ? MASK_BACKWAY : 0) | CURPOINTINDEX
     */
    public static final int PATHDATAOFFSET_ACTIVEFLAG_BACKWAY_CURPOINTINDEX = 4;

    /**
     * Смещение до ячейки, содержащей текущую координату X
     */
    public static final int PATHDATAOFFSET_CURRENTX = 5;

    /**
     * Смещение до ячейки, содержащей текущую координату Y
     */
    public static final int PATHDATAOFFSET_CURRENTY = 6;

    /**
     * Смещение до ячейки, содержащей текущую координату X центра пути
     */
    public static final int PATHDATAOFFSET_CENTERX = 7;

    /**
     * Смещение до ячейки, содержащей текущую координату Y центра пути
     */
    public static final int PATHDATAOFFSET_CENTERY = 8;

    /**
     * Смещение до ячейки, содержащей текущую координату X следующей точки
     */
    public static final int PATHDATAOFFSET_TARGETX = 9;

    /**
     * Смещение до ячейки, содержащей текущую координату Y следующей точки
     */
    public static final int PATHDATAOFFSET_TARGETY = 10;

    /**
     * Смещение до ячейки, содержащей текущее приращение X
     */
    public static final int PATHDATAOFFSET_DELTAX = 11;

    /**
     * Смещение до ячейки, содержащей текущее приращение Y
     */
    public static final int PATHDATAOFFSET_DELTAY = 12;

    /**
     * Смещение до ячейки, содержащей текущее значение сжатия по оси X
     */
    public static final int PATHDATAOFFSET_LOCALSCALEWIDTH = 13;

    /**
     * Смещение до ячейки, содержащей текущее значение сжатия по оси Y
     */
    public static final int PATHDATAOFFSET_LOCALSCALEHEIGHT = 14;

    private short[] ash_pathArray;
    private SpriteCollection[] ap_spriteCollections;

    public static int SCALE_WIDTH = 0x100;
    public static int SCALE_HEIGHT = 0x100;

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
     * Обычныый тип, путь проходит до конца пути и работа контроллера прекращается
     */
    public static final int TYPE_NORMAL = 0;
    /**
     * Маятник, путь дойдя до последней точки, начинает двигаться в обратнок направлении
     */
    public static final int TYPE_PENDULUM = 1;
    /**
     * Зацикленный, путь дойдя до последней точки, переходин на первую
     */
    public static final int TYPE_CYCLED = 2;


    protected int[] ai_pathDataArray;

    protected int i_lastInactivePathOffset;
    protected int i_lastActivePathOffset;
    protected int i_PathsNumber;
    protected int i_PathCollectionID;

    private int i_lastIteratorPathOffset;

    public final void initIterator()
    {
        i_lastIteratorPathOffset = i_lastActivePathOffset;
    }

    public final int nextActivePathOffset()
    {
        int i_result = i_lastIteratorPathOffset;
        if (i_result >= 0)
        {
            i_lastIteratorPathOffset = ai_pathDataArray[i_result];
        }
        return i_result;
    }

    public PathCollection(int _pathCollectionID, int _pathsNumber, short[] _pathDataArray, SpriteCollection[] _spriteCollections)
    {
        i_PathCollectionID = _pathCollectionID;
        ash_pathArray = _pathDataArray;
        ap_spriteCollections = _spriteCollections;

        ai_pathDataArray = new int [_pathsNumber * PATH_DATASIZE];
        i_PathsNumber = _pathsNumber;
        i_lastActivePathOffset = -1;
        i_lastInactivePathOffset = -1;
        releaseAllPaths();
    }

    public final int getOffsetOfLastInactivePath()
    {
        return i_lastInactivePathOffset;
    }

    public final void releaseAllPaths()
    {
        final int[] ai_paths = ai_pathDataArray;
        i_lastActivePathOffset = -1;

        int i_currentPathOffset = 0;
        int i_index = i_PathsNumber;
        int i_prevOffset = -1;

        while (i_index != 0)
        {
            ai_paths[i_currentPathOffset] = i_prevOffset;
            if (i_index == 1)
                ai_paths[i_currentPathOffset + 1] = -1;
            else
                ai_paths[i_currentPathOffset + 1] = i_currentPathOffset + PATH_DATASIZE;

            ai_paths[i_currentPathOffset + PATHDATAOFFSET_ACTIVEFLAG_BACKWAY_CURPOINTINDEX] = 0;

            i_prevOffset = i_currentPathOffset;
            i_currentPathOffset += PATH_DATASIZE;
            i_index--;
        }

        i_lastInactivePathOffset = i_currentPathOffset - PATH_DATASIZE;
    }

    public final void releasePath(int _pathOffset)
    {
        int i_lastInactivePthOffset = i_lastInactivePathOffset;
        final int[] ai_pda = ai_pathDataArray;

        // Убираем путь из списка активных
        int i_prevActivePathOffset = ai_pda[_pathOffset];
        int i_nextActivePathOffset = ai_pda[_pathOffset + 1];
        if (i_prevActivePathOffset >= 0)
        {
            ai_pda[i_prevActivePathOffset + 1] = i_nextActivePathOffset;
        }
        if (i_nextActivePathOffset >= 0)
        {
            ai_pda[i_nextActivePathOffset] = i_prevActivePathOffset;
        }
        if (_pathOffset == i_lastActivePathOffset)
        {
            i_lastActivePathOffset = i_prevActivePathOffset;
        }

        // Вставляем путь в список неактивных
        ai_pda[_pathOffset] = i_lastInactivePthOffset;
        ai_pda[_pathOffset + 1] = -1;
        if (i_lastInactivePthOffset >= 0)
        {
            ai_pda[i_lastInactivePthOffset + 1] = _pathOffset;
        }
        i_lastInactivePathOffset = _pathOffset;

        // сбрасываем флаг активности пути
        ai_pda[_pathOffset + PATHDATAOFFSET_ACTIVEFLAG_BACKWAY_CURPOINTINDEX] = 0;
    }

    public final int getSpriteCollectionIndexAndSpriteIndex(int _pathOffset)
    {
        return ai_pathDataArray[_pathOffset + PATHDATAOFFSET_MODIFIERS_PATHTYPE_PATHLEN_COLLECTION_SPRITE] & (MSK_COLLECTION | MSK_SPRITEINDEX);
    }

    public final void activatePath(int _pathOffset, int _offsetPathData, int _modify, int _behaviour, int _centerX, int _centerY, int _localScaleW, int _localScaleH, int _spriteCollectionID, int _spriteIndex, int _initPathPoint, int _pathLength)
    {
        final int[] ai_pda = ai_pathDataArray;
        final short[] ash_ppda = ash_pathArray;

        // Проверяем, активен ли уже спрайт, если нет то активизируем, иначе пропускаем активизацию
        if ((ai_pda[_pathOffset + PATHDATAOFFSET_ACTIVEFLAG_BACKWAY_CURPOINTINDEX] & MASK_PATHACTIVE) == 0)
        {
            int i_lastActivePthOffset = i_lastActivePathOffset;

            // Убираем путь из списка неактивных и вставляем в список активных
            int i_prevInactivePathOffset = ai_pda[_pathOffset];
            int i_nextInactivePathOffset = ai_pda[_pathOffset + 1];
            if (i_prevInactivePathOffset >= 0)
            {
                ai_pda[i_prevInactivePathOffset + 1] = i_nextInactivePathOffset;
            }
            if (i_nextInactivePathOffset >= 0)
            {
                ai_pda[i_nextInactivePathOffset] = i_prevInactivePathOffset;
            }
            if (_pathOffset == i_lastInactivePathOffset)
            {
                i_lastInactivePathOffset = i_prevInactivePathOffset;
            }

            // Вставляем путь в список активных
            ai_pda[_pathOffset] = i_lastActivePthOffset;
            ai_pda[_pathOffset + 1] = -1;
            if (i_lastActivePthOffset >= 0)
            {
                ai_pda[i_lastActivePthOffset + 1] = _pathOffset;
            }
            i_lastActivePathOffset = _pathOffset;
        }

        int i_initOffset = _offsetPathData;

        int i_length = ash_ppda[i_initOffset++];
        if (_pathLength > 0)
            i_length = _pathLength;
        else if (_initPathPoint > 0)
            i_length -= _initPathPoint;

        int i_pathType = ash_ppda[i_initOffset++] & 0xF;
        i_initOffset += _initPathPoint * POINT_DATA_SIZE;

        int i_modifiers_type_length_collection_sprite = (_modify << SHR_MODIFIERS) | (i_pathType << SHR_TYPE) | (i_length << SHR_PATHLEN) | (_behaviour << SHR_BEHAVIOUR) | (_spriteCollectionID << SHR_COLLECTION) | _spriteIndex;
        ai_pda[_pathOffset + PATHDATAOFFSET_MODIFIERS_PATHTYPE_PATHLEN_COLLECTION_SPRITE] = i_modifiers_type_length_collection_sprite;
        ai_pda[_pathOffset + PATHDATAOFFSET_INITOFFSET_CURRENTOFFSET] = i_initOffset << 16;

        int i_dataOffset = _pathOffset + PATHDATAOFFSET_LOCALSCALEWIDTH;
        ai_pda[i_dataOffset++] = _localScaleW;
        ai_pda[i_dataOffset] = _localScaleH;

        i_dataOffset = _pathOffset + PATHDATAOFFSET_CENTERX;
        ai_pda[i_dataOffset++] = _centerX;
        ai_pda[i_dataOffset] = _centerY;

        resetPath(_pathOffset);
    }

    public final void processAllActivePaths()
    {
        int i_lastActive = i_lastActivePathOffset;
        final int[] ai_sda = ai_pathDataArray;

        while (true)
        {
            if (i_lastActive < 0) break;
            final int i_curOffset = i_lastActive;
            i_lastActive = ai_sda[i_lastActive];
            processStep(i_curOffset);
        }
    }

    public final void saveDataToStream(DataOutputStream _outStream) throws Exception
    {
        _outStream.writeInt(i_lastActivePathOffset);
        _outStream.writeInt(i_lastInactivePathOffset);

        int i_index = (i_PathsNumber * PATH_DATASIZE) - 1;
        final int [] ai_arr = ai_pathDataArray;
        while (i_index >= 0)
        {
            _outStream.writeInt(ai_arr[i_index--]);
        }
    }

    public final void loadDataFromStream(DataInputStream _outStream) throws Exception
    {
        i_lastActivePathOffset = _outStream.readInt();
        i_lastInactivePathOffset = _outStream.readInt();

        int i_index = (i_PathsNumber * PATH_DATASIZE) - 1;
        final int [] ai_arr = ai_pathDataArray;
        while (i_index >= 0)
        {
            ai_arr[i_index--] = _outStream.readInt();
        }
    }

    public static final int getDataSize(int _pathsNumber)
    {
        return 8 + (4 * PATH_DATASIZE * _pathsNumber);
    }


    public final void processStep(int _pathOffset)
    {
        int[] ai_dataArr = ai_pathDataArray;

        int i_dataOffset = _pathOffset + PATHDATAOFFSET_MODIFIERS_PATHTYPE_PATHLEN_COLLECTION_SPRITE;
        int i_modif_type_len_col_spr = ai_dataArr[i_dataOffset++];
        int i_initoffst_currentoffset = ai_dataArr[i_dataOffset++];
        int i_activeflag_backway_curpointindex = ai_dataArr[i_dataOffset++];
        int i8_curX = ai_dataArr[i_dataOffset++];
        int i8_curY = ai_dataArr[i_dataOffset++];
        int i8_centerX = ai_dataArr[i_dataOffset++];
        int i8_centerY = ai_dataArr[i_dataOffset++];
        int i8_targetX = ai_dataArr[i_dataOffset++];
        int i8_targetY = ai_dataArr[i_dataOffset++];
        int i8_dx = ai_dataArr[i_dataOffset++];
        int i8_dy = ai_dataArr[i_dataOffset];

        boolean lg_back = (i_activeflag_backway_curpointindex & MASK_BACKWAY) != 0;
        int i_curPointIndex = i_activeflag_backway_curpointindex & MSK_CURPOINTINDEX;
        int i_type = (i_modif_type_len_col_spr >>> SHR_TYPE) & MSK_TYPE;
        int i_length = (i_modif_type_len_col_spr >>> SHR_PATHLEN) & MSK_PATHLEN;
        int i_currentOffset = i_initoffst_currentoffset & MSK_OFFSET;

        int i_spriteCollection = (i_modif_type_len_col_spr >>> SHR_COLLECTION) & MSK_COLLECTION;
        int i_spriteOffset = (i_modif_type_len_col_spr & MSK_SPRITEINDEX) * SpriteCollection.SPRITEDATA_LENGTH;
        SpriteCollection p_collection = ap_spriteCollections[i_spriteCollection];

        boolean lg_released = false;
        if ((i_activeflag_backway_curpointindex & MASK_PATHACTIVE) !=0)
        {
        int i_len = i_length;

        i8_curX += i8_dx;
        i8_curY += i8_dy;

        i_dataOffset = _pathOffset + PATHDATAOFFSET_CURRENTX;
        ai_dataArr[i_dataOffset++] = i8_curX;
        ai_dataArr[i_dataOffset] = i8_curY;

        if (i8_curX == i8_targetX && i8_curY == i8_targetY)
        {
            int i_behaviour = (i_modif_type_len_col_spr >> SHR_BEHAVIOUR) & MSK_BEHAVIOUR;

            switch (i_type)
            {
                case TYPE_NORMAL:
                {
                    i_curPointIndex++;
                    i_activeflag_backway_curpointindex = (i_activeflag_backway_curpointindex & ~MSK_CURPOINTINDEX) | i_curPointIndex;
                    ai_dataArr[_pathOffset + PATHDATAOFFSET_ACTIVEFLAG_BACKWAY_CURPOINTINDEX] = i_activeflag_backway_curpointindex;
                    if (i_curPointIndex == i_len)
                    {
                        switch(i_behaviour)
                        {
                            case BEHAVIOUR_NOTIFYATEND :
                            {
                                ai_dataArr[_pathOffset+PATHDATAOFFSET_ACTIVEFLAG_BACKWAY_CURPOINTINDEX] &= ~MASK_PATHACTIVE;
                                Gamelet.notifyPathCompleted(i_PathCollectionID,_pathOffset,i_spriteCollection,i_spriteOffset);
                            };break;
                            case BEHAVIOUR_REMOVESPRITEATEND :
                            {
                               p_collection.releaseSprite(i_spriteOffset);
                                releasePath(_pathOffset);
                                lg_released = true;
                            };break;
                            case BEHAVIOUR_REPEAT :
                            {
                               resetPath(_pathOffset);
                            };break;
                        }
                    } else
                        calculateDifferents(_pathOffset, true);
                }
                    ;
                    break;
                case TYPE_PENDULUM:
                {
                    boolean lg_p = true;
                    if (lg_back)
                    {
                        if (i_curPointIndex == 0)
                        {
                            i_activeflag_backway_curpointindex = i_activeflag_backway_curpointindex & (~MASK_BACKWAY);
                            ai_dataArr[_pathOffset + PATHDATAOFFSET_ACTIVEFLAG_BACKWAY_CURPOINTINDEX] = i_activeflag_backway_curpointindex;

                            i_currentOffset--;
                            i_initoffst_currentoffset = (i_initoffst_currentoffset & ~MSK_OFFSET) | i_currentOffset;
                            ai_dataArr[_pathOffset + PATHDATAOFFSET_INITOFFSET_CURRENTOFFSET] = i_initoffst_currentoffset;

                            lg_p = true;
                        } else
                        {
                            i_curPointIndex--;
                            i_activeflag_backway_curpointindex = (i_activeflag_backway_curpointindex & ~MSK_CURPOINTINDEX) | i_curPointIndex;
                            ai_dataArr[_pathOffset + PATHDATAOFFSET_ACTIVEFLAG_BACKWAY_CURPOINTINDEX] = i_activeflag_backway_curpointindex;
                            i_currentOffset -= (POINT_DATA_SIZE << 1);
                            i_initoffst_currentoffset = (i_initoffst_currentoffset & ~MSK_OFFSET) | i_currentOffset;
                            ai_dataArr[_pathOffset + PATHDATAOFFSET_INITOFFSET_CURRENTOFFSET] = i_initoffst_currentoffset;

                            lg_p = false;
                        }
                    } else
                    {
                        i_curPointIndex++;
                        if (i_curPointIndex == i_len)
                        {
                            i_activeflag_backway_curpointindex |= MASK_BACKWAY;
                            ai_dataArr[_pathOffset + PATHDATAOFFSET_ACTIVEFLAG_BACKWAY_CURPOINTINDEX] = i_activeflag_backway_curpointindex;

                            i_currentOffset -= ((POINT_DATA_SIZE << 1) - 1);
                            i_initoffst_currentoffset = (i_initoffst_currentoffset & ~MSK_OFFSET) | i_currentOffset;
                            ai_dataArr[_pathOffset + PATHDATAOFFSET_INITOFFSET_CURRENTOFFSET] = i_initoffst_currentoffset;

                            i_curPointIndex--;
                            lg_p = false;
                        }
                        i_activeflag_backway_curpointindex = (i_activeflag_backway_curpointindex & ~MSK_CURPOINTINDEX) | i_curPointIndex;
                        ai_dataArr[_pathOffset + PATHDATAOFFSET_ACTIVEFLAG_BACKWAY_CURPOINTINDEX] = i_activeflag_backway_curpointindex;
                    }
                    calculateDifferents(_pathOffset, lg_p);
                }
                    ;
                    break;
                case TYPE_CYCLED:
                {
                    i_curPointIndex++;
                    i_activeflag_backway_curpointindex = (i_activeflag_backway_curpointindex & ~MSK_CURPOINTINDEX) | i_curPointIndex;
                    ai_dataArr[_pathOffset + PATHDATAOFFSET_ACTIVEFLAG_BACKWAY_CURPOINTINDEX] = i_activeflag_backway_curpointindex;
                    if (i_curPointIndex == i_len)
                    {
                        resetPath(_pathOffset);
                    }
                    else
                        calculateDifferents(_pathOffset, true);
                }
                    ;
                    break;
            }

            if (i_behaviour == BEHAVIOUR_NOTIFYATEVERYPOINT && !lg_released)
            {
                    Gamelet.notifyPathPointPassed(i_PathCollectionID, _pathOffset, i_curPointIndex, i_spriteCollection, i_spriteOffset);
            }
        }
        }

        if (!lg_released)
        {
            p_collection.setMainPointXY(i_spriteOffset, i8_centerX + i8_curX, i8_centerY + i8_curY);
        }
    }

    private final void calculateDifferents(final int _pathOffset, final boolean _speedPrev)
    {
        final int [] ai_pathsArray = ai_pathDataArray;
        final short [] ash_pathDataArray = ash_pathArray;
        int i_steps = 0;
        int i_idOffset = ai_pathsArray[_pathOffset + PATHDATAOFFSET_INITOFFSET_CURRENTOFFSET];
        int i_curOffset = i_idOffset & MSK_OFFSET;

        if (_speedPrev)
            i_steps = ash_pathDataArray[i_curOffset++];

        int i8_tX = ash_pathDataArray[i_curOffset++] * SCALE_WIDTH;
        int i8_tY = ash_pathDataArray[i_curOffset++] * SCALE_HEIGHT;

        int i_dataOffset = _pathOffset + PATHDATAOFFSET_LOCALSCALEWIDTH;
        int i8_localScaleWidth = ai_pathsArray[i_dataOffset++];
        int i8_localScaleHeight = ai_pathsArray[i_dataOffset];

        i8_tX = (int) (((long) i8_tX * (long) i8_localScaleWidth + 0x7F) >> 8);
        i8_tY = (int) (((long) i8_tY * (long) i8_localScaleHeight + 0x7F) >> 8);

        i_dataOffset = _pathOffset + PATHDATAOFFSET_CURRENTX;
        int i8_cX = ai_pathsArray[i_dataOffset++];
        int i8_cY = ai_pathsArray[i_dataOffset];

        if (!_speedPrev) i_steps = ash_pathDataArray[i_curOffset++];

        i_dataOffset = _pathOffset + PATHDATAOFFSET_MODIFIERS_PATHTYPE_PATHLEN_COLLECTION_SPRITE;
        int i_mod = (ai_pathsArray[i_dataOffset++] >>> SHR_MODIFIERS) & MSK_MODIFIERS;

        ai_pathsArray[i_dataOffset] = (i_idOffset & ~MSK_OFFSET) | i_curOffset;

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

        i_dataOffset = _pathOffset + PATHDATAOFFSET_TARGETX;
        ai_pathsArray[i_dataOffset++] = i8_tX;
        ai_pathsArray[i_dataOffset++] = i8_tY;
        ai_pathsArray[i_dataOffset++] = i8_ddx;
        ai_pathsArray[i_dataOffset] = i8_ddy;
    }

    /**
     * Сбросить контроллер в начальное состояние
     */
    public final void resetPath(final int _pathOffset)
    {
        final int[] ai_pathArr = ai_pathDataArray;
        final short[] ash_pathDataArr = ash_pathArray;

        int i_dataOffset = _pathOffset + PATHDATAOFFSET_ACTIVEFLAG_BACKWAY_CURPOINTINDEX;
        ai_pathArr[i_dataOffset] = MASK_PATHACTIVE;

        final int i_initOffset = ai_pathDataArray[_pathOffset + PATHDATAOFFSET_INITOFFSET_CURRENTOFFSET] >>> SHR_OFFSET;
        int i_inOffset = i_initOffset;

        int i8_cX = ash_pathDataArr[i_inOffset] * SCALE_WIDTH;
        int i8_cY = ash_pathDataArr[i_inOffset + 1] * SCALE_HEIGHT;

        i_dataOffset = _pathOffset + PATHDATAOFFSET_LOCALSCALEWIDTH;
        int i_localScaleWidth = ai_pathArr[i_dataOffset++];
        int i_localScaleHeight = ai_pathArr[i_dataOffset];

        int i_currentOffset = i_initOffset + (POINT_DATA_SIZE - 1);
        ai_pathArr[_pathOffset + PATHDATAOFFSET_INITOFFSET_CURRENTOFFSET] = (i_initOffset << SHR_OFFSET) | i_currentOffset;

        i8_cX = (int) (((long) i8_cX * (long) i_localScaleWidth + 0x7F) >> 8);
        i8_cY = (int) (((long) i8_cY * (long) i_localScaleHeight + 0x7F) >> 8);

        i_dataOffset = _pathOffset + PATHDATAOFFSET_CURRENTX;
        ai_pathArr[i_dataOffset++] = i8_cX;
        ai_pathArr[i_dataOffset++] = i8_cY;
        int i_centerX = ai_pathArr[i_dataOffset++];
        int i_centerY = ai_pathArr[i_dataOffset];

        int i_sprData = ai_pathArr[_pathOffset + PATHDATAOFFSET_MODIFIERS_PATHTYPE_PATHLEN_COLLECTION_SPRITE];
        int i_sprCollection = (i_sprData >>> SHR_COLLECTION) & MSK_COLLECTION;
        int i_spriteOffset = (i_sprData & MSK_SPRITEINDEX) * SpriteCollection.SPRITEDATA_LENGTH;

        SpriteCollection p_sprCollection = ap_spriteCollections[i_sprCollection];
        p_sprCollection.setMainPointXY(i_spriteOffset, i_centerX + i8_cX, i_centerY + i8_cY);

        calculateDifferents(_pathOffset,true);
    }

    public final void setCenterPointForPath(int _pathOffset,int _centerX,int _centerY)
    {
        final int [] ai_arr = ai_pathDataArray;
        int i_dataOffset = _pathOffset + PATHDATAOFFSET_CURRENTX;
        int i8_curX = ai_arr[i_dataOffset++];
        int i8_curY = ai_arr[i_dataOffset++];
        ai_arr[i_dataOffset++] = _centerX;
        ai_arr[i_dataOffset] = _centerY;

        int i_sprite = ai_arr[_pathOffset+PATHDATAOFFSET_MODIFIERS_PATHTYPE_PATHLEN_COLLECTION_SPRITE];
        int i_collection = (i_sprite >>> SHR_COLLECTION) & MSK_COLLECTION;
        i_sprite = i_sprite & MSK_SPRITEINDEX * SpriteCollection.SPRITEDATA_LENGTH;

        SpriteCollection p_sprCollection = ap_spriteCollections[i_collection];
        p_sprCollection.setMainPointXY(i_sprite, _centerX + i8_curX, _centerY + i8_curY);
    }

    public final void moveCenterPointForPath(int _pathOffset,int _deltaX,int _deltaY)
    {
        final int [] ai_arr = ai_pathDataArray;
        int i_dataOffset = _pathOffset + PATHDATAOFFSET_CURRENTX;
        int i8_curX = ai_arr[i_dataOffset++];
        int i8_curY = ai_arr[i_dataOffset++];

        int i_cX = ai_arr[i_dataOffset]+_deltaX;
        int i_cY = ai_arr[i_dataOffset+1]+_deltaY;

        ai_arr[i_dataOffset++] = i_cX;
        ai_arr[i_dataOffset] = i_cY;

        int i_sprite = ai_arr[_pathOffset+PATHDATAOFFSET_MODIFIERS_PATHTYPE_PATHLEN_COLLECTION_SPRITE];
        int i_collection = (i_sprite >>> SHR_COLLECTION) & MSK_COLLECTION;
        i_sprite = i_sprite & MSK_SPRITEINDEX * SpriteCollection.SPRITEDATA_LENGTH;

        SpriteCollection p_sprCollection = ap_spriteCollections[i_collection];
        p_sprCollection.setMainPointXY(i_sprite, i_cX + i8_curX, i_cY + i8_curY);
    }
}
