import java.io.*;

/**
 * Класс, описывает коллекцию спрайтов и сервисные функции для работы с ней
 * (C) 2005 Raydac Research Group Ltd.
 *
 * @author Игорь Мазница
 * @version 1.04 (15 Sep 2005)
 */
public class SpriteCollection
{
    /**
     * Маска для получения номера кадра из общего поля
     */
    public static final int MASK_FRAMENUMBER = 0x7FFF;

    /**
     * Маска для получения флага активности спрайта из общего поля
     */
    private static final int MASK_SPRITEACTIVE = 0x80000000;

    /**
     * Маска для получения данных состояния спрайта
     */
    private static final int MASK_STATE = 0xFF;

    /**
     * Маска для получения данных типа спрайта
     */
    private static final int MASK_TYPE = 0xFF;

    /**
     * Маска для получения флага обратной анимации спрайта из общего поля
     */
    private static final int MASK_BACKANIMATION = 0x40000000;

    /**
     * Маска для получения количества тиков до смены кадра спрайта из общего поля
     */
    private static final int MASK_NEXTFRAMEDELAY = 0x3FFF8000;

    /**
     * Маска для получения типа-состояния спрайта
     */
    private static final int MASK_TYPESTATE = 0xFFFF;

    /**
     * Смещение количества тиков до смены кадра спрайта в общем поле
     */
    private static final int SHR_NEXTFRAMEDELAY = 15;

    /**
     * Смещение данных следующего типа-состояния в общем поле
     */
    private static final int SHR_NEXTTYPESTATE = 16;

    /**
     * Смещение данных типа спрайта
     */
    private static final int SHR_TYPE = 8;

    /**
     * Главная точка спрайта находится в центре
     */
    public static final int SPRITE_ALIGN_CENTER = 0;

    /**
     * Главная точка спрайта выровнена влево
     */
    public static final int SPRITE_ALIGN_LEFT = 1;

    /**
     * Главная точка спрайта выровнена вправо
     */
    public static final int SPRITE_ALIGN_RIGHT = 2;

    /**
     * Главная точка спрайта выровнена вверх
     */
    public static final int SPRITE_ALIGN_TOP = 0x10;

    /**
     * Главная точка спрайта выровнена вниз
     */
    public static final int SPRITE_ALIGN_DOWN = 0x20;

    /**
     * Флаг, показывающий что производится циклическая анимация
     */
    public static final int ANIMATION_CYCLIC = 0;

    /**
     * Флаг, показывающий что производится анимация с заморозкой последнего кадра
     */
    public static final int ANIMATION_FROZEN = 1;

    /**
     * Флаг, показывающий что производится маятниковая анимация
     */
    public static final int ANIMATION_PENDULUM = 2;

    /**
     * Константа содержит количество ячеек, отводимое под один спрайт в массиве
     */
    public static final int SPRITEDATA_LENGTH = 10;

    /**
     * Флаг, показывающий, что спрайт должен быть уничтожен при достижении конца текущей итерации анимации
     */
    public static final int SPRITEBEHAVIOUR_RELEASE = 0xFF02;

    /**
     * Флаг, показывающий, что должна быть вызвана функция, уведомляющая о достижении конца итерации анимации для заданного спрайта
     */
    public static final int SPRITEBEHAVIOUR_NOTIFY = 0xFF01;

    //-----------------------------СМЕЩЕНИЯ ДЛЯ ТАБЛИЦЫ СПРАЙТОВ--------------------------------------
    /**
     * Смещение до ячейки, содержащей указатель на первую ячейку предыдущего спрайта в списке, если -1 то это граничный спрайт
     */
    private static final int SPRITEDATAOFFSET_PREVLISTINDEX = 0;

    /**
     * Смещение до ячейки, содержащей указатель на первую ячейку последующего спрайта в списке, если -1 то это граничный спрайт
     */
    private static final int SPRITEDATAOFFSET_NEXTLISTINDEX = 1;


    /**
     * Смещение до ячейки, содержащей тип объекта, а так же тип и состояние объекта в которое он должен перейти при достижении конца итерации анимации
     * Формат:
     * nextType<<24 | nextState<<16 | currentType<<8 | currentState
     * комбинация nextType<<8 | nextState может содержать SPRITEBEHAVIOUR флаг
     */
    public static final int SPRITEDATAOFFSET_OBJECTTYPESTATE = 2; // [тип/состояние по окончании анимации]<<16 текущие тип и состояние

    /**
     * Смещение до ячейки, хранящей данные анимации спрайта и флаг его активности
     * Формат:
     * (sprite_active ? MASK_SPRITEACTIVE : 0x0)| ((backanimation_flag ? MASK_BACKANIMATION  : 0x0) | (current_animation_counter<<15) | current_frame
     */
    public static final int SPRITEDATAOFFSET_ANIMATIONDATA = 3; //SPRITE_ACTIVE | BACKANIMATION |  NEXTFRAMECOUNTER<<15 | FRAME

    /**
     * Смещение до данных по анимации и размерам спрайта в массиве настроек анимации
     */
    public static final int SPRITEDATAOFFSET_STATICDATAOFFSET = 4;

    /**
     * Смещение до ячейки спрайта, хранящего экранную координату X спрайта
     */
    public static final int SPRITEDATAOFFSET_SCREENX = 5;

    /**
     * Смещение до ячейки спрайта, хранящего экранную координату Y спрайта
     */
    public static final int SPRITEDATAOFFSET_SCREENY = 6;

    /**
     * Смещение до ячейки спрайта, хранящего координату X спрайта
     */
    public static final int SPRITEDATAOFFSET_MAINX = 7;

    /**
     * Смещение до ячейки спрайта, хранящего координату Y спрайта
     */
    public static final int SPRITEDATAOFFSET_MAINY = 8;

    /**
     * Смещение до ячейки спрайта, хранящего опциональную информацию о спрайте
     */
    public static final int SPRITEDATAOFFSET_OPTIONALDATA = 9;


    //-----------------------------СМЕЩЕНИЯ ДЛЯ ТАБЛИЦЫ АНИМАЦИЙ--------------------------------------
    /**
     * Смещение в массиве данных анимации до ячейки хранящей значение ширины объекта
     */
    private static final int ANIMATIONTABLEEOFFSET_WIDTH = 0;

    /**
     * Смещение в массиве данных анимации до ячейки хранящей значение высоты объекта
     */
    private static final int ANIMATIONTABLEEOFFSET_HEIGHT = 1;

    /**
     * Смещение в массиве данных анимации до ячейки хранящей значение смещения горячей зоны по оси X от верхнего левого края объекта
     */
    private static final int ANIMATIONTABLEEOFFSET_HOTZONEOFFSETX = 2;

    /**
     * Смещение в массиве данных анимации до ячейки хранящей значение смещения горячей зоны по оси Y от верхнего левого края объекта
     */
    private static final int ANIMATIONTABLEEOFFSET_HOTZONEOFFSETY = 3;

    /**
     * Смещение в массиве данных анимации до ячейки хранящей значение ширины горячей зоны объекта
     */
    private static final int ANIMATIONTABLEEOFFSET_HOTZONEWIDTH = 4;

    /**
     * Смещение в массиве данных анимации до ячейки хранящей значение высоты горячей зоны объекта
     */
    private static final int ANIMATIONTABLEEOFFSET_HOTZONEHEIGHT = 5;

    /**
     * Смещение в массиве данных анимации до ячейки хранящей значение количества кадров анимации объекта
     */
    private static final int ANIMATIONTABLEEOFFSET_FRAMES = 6;

    /**
     * Смещение в массиве данных анимации до ячейки хранящей значение задержки между кадрами при анимации объекта
     */
    private static final int ANIMATIONTABLEEOFFSET_ANIMATIONDELAY = 7;

    /**
     * Смещение в массиве данных анимации до ячейки хранящей значение типа анимации  объекта
     */
    private static final int ANIMATIONTABLEEOFFSET_ANIMATIONTYPE = 8;

    /**
     * Смещение в массиве данных анимации до ячейки хранящей смещение от главной точки до верхнего левого края по оси X
     */
    private static final int ANIMATIONTABLEEOFFSET_OFFSETTOMAINX = 9;

    /**
     * Смещение в массиве данных анимации до ячейки хранящей смещение от главной точки до верхнего левого края по оси Y
     */
    private static final int ANIMATIONTABLEEOFFSET_OFFSETTOMAINY = 10;

    /**
     * Переменная хранит указатель на массив с данными анимации
     */
    private short[] ash_spriteAnimationDataArray;

    /**
     * Переменная хранит количество спрайтов в коллекции
     */
    private int i_SpritesNumber;

    /**
     * Переменная хранит смещение к началу последнего неактивного спрайта
     */
    public int i_lastInactiveSpriteOffset;

    /**
     * Переменная хранит смещение к началу последнего активного спрайта
     */
    public int i_lastActiveSpriteOffset;

    /**
     * Переменная хранит массив, содержащий параметры спрайтов
     */
    public int[] ai_spriteDataArray;

    /**
     * Переменная хранит идентификатор коллекции
     */
    public int i_CollectionID;

    /**
     * Переменная хранит смещение к текущему спрайту при итерации активных спрайтов
     */
    private int i_lastIteratorSpriteOffset;

    /**
     * Инициализация итератора активных спрайтов, после вызова этой функции можно начать перебор при помощи nextActiveSpriteOffset()
     */
    public final void initIterator()
    {
        i_lastIteratorSpriteOffset = i_lastActiveSpriteOffset;
    }

    /**
     * Получение следующего значения от итератора активных спрайтов
     *
     * @return смещение в массиве спрайтов или -1 если нет больше активных спрайтов
     */
    public final int nextActiveSpriteOffset()
    {
        int i_result = i_lastIteratorSpriteOffset;
        if (i_result >= 0)
        {
            i_lastIteratorSpriteOffset = ai_spriteDataArray[i_result];
        }
        return i_result;
    }

    /**
     * Конструктор коллекции спрайтов
     *
     * @param _collectionID    уникальный идентификатор коллекции
     * @param _spriteNumber    количество спрайтов в коллекции
     * @param _spriteDataArray указатель на массив, содержащий данные по анимации объектов
     */
    public SpriteCollection(int _collectionID, int _spriteNumber, short[] _spriteDataArray)
    {
        i_CollectionID = _collectionID;
        ash_spriteAnimationDataArray = _spriteDataArray;
        i_SpritesNumber = _spriteNumber;

        ai_spriteDataArray = new int[SPRITEDATA_LENGTH * _spriteNumber];

        i_lastActiveSpriteOffset = -1;
        i_lastInactiveSpriteOffset = 0;

        releaseAllSprites();
    }

    /**
     * Деактивизация всех активных спрайтов
     */
    public final void releaseAllSprites()
    {
        final int[] ai_sprites = ai_spriteDataArray;
        i_lastActiveSpriteOffset = -1;

        int i_currentSpriteOffset = 0;
        int i_index = i_SpritesNumber;
        int i_prevOffset = -1;

        while (i_index != 0)
        {
            ai_sprites[i_currentSpriteOffset] = i_prevOffset;
            if (i_index == 1)
                ai_sprites[i_currentSpriteOffset + 1] = -1;
            else
                ai_sprites[i_currentSpriteOffset + 1] = i_currentSpriteOffset + SPRITEDATA_LENGTH;

            ai_sprites[i_currentSpriteOffset + SPRITEDATAOFFSET_ANIMATIONDATA] = 0;

            i_prevOffset = i_currentSpriteOffset;
            i_currentSpriteOffset += SPRITEDATA_LENGTH;
            i_index--;
        }

        i_lastInactiveSpriteOffset = i_currentSpriteOffset-SPRITEDATA_LENGTH;
    }

    /**
     * Получить смещение последнего неактивного спрайта
     *
     * @return смещение к спрайту или -1 если нет неактивных спрайтов
     */
    public final int getOffsetOfLastInactiveSprite()
    {
        return i_lastInactiveSpriteOffset;
    }

    /**
     * Деактивизировать спрайт по заданному смещению
     *
     * @param _spriteOffset смещение в массиве данных спрайта
     */
    public final void releaseSprite(int _spriteOffset)
    {
        int i_lastInactiveSprOffset = i_lastInactiveSpriteOffset;
        final int[] ai_sda = ai_spriteDataArray;

        // Убираем спрайт из списка активных
        int i_prevActiveSpriteOffset = ai_sda[_spriteOffset];
        int i_nextActiveSpriteOffset = ai_sda[_spriteOffset + 1];
        if (i_prevActiveSpriteOffset >= 0)
        {
            ai_sda[i_prevActiveSpriteOffset + 1] = i_nextActiveSpriteOffset;
        }
        if (i_nextActiveSpriteOffset >= 0)
        {
            ai_sda[i_nextActiveSpriteOffset] = i_prevActiveSpriteOffset;
        }
        if (_spriteOffset == i_lastActiveSpriteOffset)
        {
            i_lastActiveSpriteOffset = i_prevActiveSpriteOffset;
        }

        // Вставляем спрайт в список неактивных
        ai_sda[_spriteOffset] = i_lastInactiveSprOffset;
        ai_sda[_spriteOffset + 1] = -1;
        if (i_lastInactiveSprOffset >= 0)
        {
            ai_sda[i_lastInactiveSprOffset + 1] = _spriteOffset;
        }
        i_lastInactiveSpriteOffset = _spriteOffset;

        // сбрасываем флаг активности спрайта
        ai_sda[_spriteOffset + SPRITEDATAOFFSET_ANIMATIONDATA] = 0;
    }

    /**
     * Функция возвращает упакованное значение ширины и высоты фрейма спрайта, переведенные в нормальные величины
     *
     * @param _spriteOffset смещение спрайта в массиве
     * @return возвращает упакованное значение (width << 16) | height
     */
    public final int getSpriteWidthHeight(int _spriteOffset)
    {
        final int[] ai_spriteData = ai_spriteDataArray;
        final short[] ash_spriteAnimationData = ash_spriteAnimationDataArray;
        int i_dataOffset = ai_spriteData[_spriteOffset + SPRITEDATAOFFSET_STATICDATAOFFSET] + ANIMATIONTABLEEOFFSET_WIDTH;
        int i_w = ash_spriteAnimationData[i_dataOffset++];
        int i_h = ash_spriteAnimationData[i_dataOffset];

        return (i_w << 16) | i_h;
    }

    /**
     * Функция возвращает упакованное значение экранных координат спрайта, переведенные в нормальные величины
     *
     * @param _spriteOffset смещение спрайта в массиве
     * @return возвращает упакованное значение (X << 32) | Y
     */
    public final long getSpriteScreenXY(int _spriteOffset)
    {
        final int[] ai_spriteData = ai_spriteDataArray;
        int i_dataOffset = _spriteOffset + SPRITEDATAOFFSET_SCREENX;
        int i_x = (ai_spriteData[i_dataOffset++] + 0x7F) >> 8;
        int i_y = (ai_spriteData[i_dataOffset] + 0x7F) >> 8;

        return ((long) i_x << 32) | (((long)i_y)&0xFFFFFFFFl);
    }

    /**
     * Функция возвращает упакованное значение координаты ширины и высоты фрейма спрайта, переведенные в нормальные величины
     *
     * @param _spriteOffset смещение спрайта в массиве
     * @return (xoffst << 48) | (yoffst << 32) | (width << 16) | (height)
     */
    public final long getSpriteHotspotCoordinates(int _spriteOffset)
    {
        final int[] ai_spriteData = ai_spriteDataArray;
        final short[] ash_spriteAnimationData = ash_spriteAnimationDataArray;
        int i_dataOffset = ai_spriteData[_spriteOffset + SPRITEDATAOFFSET_STATICDATAOFFSET] + ANIMATIONTABLEEOFFSET_HOTZONEOFFSETX;
        int i_x = ash_spriteAnimationData[i_dataOffset++];
        int i_y = ash_spriteAnimationData[i_dataOffset++];
        int i_w = ash_spriteAnimationData[i_dataOffset++];
        int i_h = ash_spriteAnimationData[i_dataOffset];

        return ((long) i_x << 48) | ((long) i_y << 32) | ((long) i_w << 16) | (long) i_h;
    }

    /**
     * Активизировать спрайт с заданным смещением в массиве
     *
     * @param _objectType   тип объекта
     * @param _objectState  тип состояния
     * @param _spriteOffset смещение до данных спрайта
     */
    public final void activateSprite(int _objectType, int _objectState, final int _spriteOffset)
    {
        int i_spriteOffset = _spriteOffset;
        final int[] ai_sda = ai_spriteDataArray;
        final short [] ash_ssda = ash_spriteAnimationDataArray;

        // Проверяем, активен ли уже спрайт, если нет то активизируем, иначе пропускаем активизацию
        int i_offsetSpriteDataAnimation = _spriteOffset + SPRITEDATAOFFSET_ANIMATIONDATA;
        if ((ai_sda[i_offsetSpriteDataAnimation] & MASK_SPRITEACTIVE) == 0)
        {
            int i_lastActiveSprOffset = i_lastActiveSpriteOffset;

            // Убираем спрайт из списка неактивных и вставляем в список активных
            int i_prevInactiveSpriteOffset = ai_sda[_spriteOffset];
            int i_nextInactiveSpriteOffset = ai_sda[_spriteOffset + 1];
            if (i_prevInactiveSpriteOffset >= 0)
            {
                ai_sda[i_prevInactiveSpriteOffset + 1] = i_nextInactiveSpriteOffset;
            }
            if (i_nextInactiveSpriteOffset >= 0)
            {
                ai_sda[i_nextInactiveSpriteOffset] = i_prevInactiveSpriteOffset;
            }
            if (_spriteOffset == i_lastInactiveSpriteOffset)
            {
                i_lastInactiveSpriteOffset = i_prevInactiveSpriteOffset;
            }

            // Вставляем спрайт в список активных
            ai_sda[_spriteOffset] = i_lastActiveSprOffset;
            ai_sda[_spriteOffset + 1] = -1;
            if (i_lastActiveSprOffset >= 0)
            {
                ai_sda[i_lastActiveSprOffset + 1] = _spriteOffset;
            }
            i_lastActiveSpriteOffset = _spriteOffset;
        }

        // переводим указатель на Тип и Состояние
        i_spriteOffset += SPRITEDATAOFFSET_OBJECTTYPESTATE;

        // поле STATICDATAOFFSET
        // Находим ссылку на смещения для состояний объекта
        int i_staticDataOffset = ash_ssda[(ash_ssda[_objectType]) + _objectState];
        int i_maxAnimationCounter = ash_ssda[i_staticDataOffset + ANIMATIONTABLEEOFFSET_ANIMATIONDELAY];

        // Выставляем тип и состояние
        int i_typeState = (_objectType << SHR_TYPE) | _objectState;
        ai_sda[i_spriteOffset++] = (i_typeState << SHR_NEXTTYPESTATE) | (i_typeState);
        // Данные анимации и флаг активности
        ai_sda[i_spriteOffset++] = MASK_SPRITEACTIVE | (i_maxAnimationCounter << SHR_NEXTFRAMEDELAY);
        // Ссылка на статические данные
        ai_sda[i_spriteOffset++] = i_staticDataOffset;

        // Новые смещения для экранных кординат
        i_spriteOffset++;
        i_spriteOffset++;
        int i_curMainX = ai_sda[i_spriteOffset++];
        int i_curMainY = ai_sda[i_spriteOffset];
        setMainPointXY(_spriteOffset, i_curMainX, i_curMainY);
    }

    /**
     * Обрабатываем анимацию для всех активных спрайтов
     */
    public final void processAnimationForActiveSprites()
    {
        int i_lastActive = i_lastActiveSpriteOffset;
        final int[] ai_sda = ai_spriteDataArray;
        final short[] ash_ssda = ash_spriteAnimationDataArray;

        while (true)
        {
            if (i_lastActive < 0) break;
            final int i_curOffset = i_lastActive;
            i_lastActive = ai_sda[i_lastActive];

            // Обработка анимации
            int i_animationData = ai_sda[i_curOffset + SPRITEDATAOFFSET_ANIMATIONDATA];
            int i_nextFrameCounter = (i_animationData >>> SHR_NEXTFRAMEDELAY) & ~MASK_NEXTFRAMEDELAY;
            if (i_nextFrameCounter == 0)
            {
                // Отработка анимации
                int i_staticDataOffset = ai_sda[i_curOffset + SPRITEDATAOFFSET_STATICDATAOFFSET];
                int i_curObjectStateType = ai_sda[i_curOffset + SPRITEDATAOFFSET_OBJECTTYPESTATE];
                int i_nextObjectStateType = i_curObjectStateType >>> SHR_NEXTTYPESTATE;
                i_curObjectStateType = i_curObjectStateType & MASK_TYPESTATE;
                int i_animationType = ash_ssda[i_staticDataOffset + ANIMATIONTABLEEOFFSET_ANIMATIONTYPE];
                int i_maxFrames = ash_ssda[i_staticDataOffset + ANIMATIONTABLEEOFFSET_FRAMES];
                int i_animationDelay = ash_ssda[i_staticDataOffset + ANIMATIONTABLEEOFFSET_ANIMATIONDELAY];

                boolean lg_backMove = (i_animationData & MASK_BACKANIMATION) != 0;
                int i_currentFrame = i_animationData & MASK_FRAMENUMBER;

                i_nextFrameCounter = i_animationDelay;
                boolean lg_iterationFinished = false;

                switch (i_animationType)
                {
                    case ANIMATION_CYCLIC:
                    {
                        i_currentFrame++;
                        if (i_currentFrame >= i_maxFrames)
                        {
                            i_currentFrame = 0;
                            lg_iterationFinished = true;
                        }
                    }
                        ;
                        break;
                    case ANIMATION_FROZEN:
                    {
                        i_currentFrame++;
                        if (i_currentFrame >= i_maxFrames)
                        {
                            i_currentFrame = i_maxFrames - 1;
                            lg_iterationFinished = true;
                        }
                    }
                        ;
                        break;
                    case ANIMATION_PENDULUM:
                    {
                        if (lg_backMove)
                        {
                            i_currentFrame--;
                            if (i_currentFrame == 0)
                            {
                                lg_backMove = false;
                                lg_iterationFinished = true;
                            }
                        } else
                        {
                            i_currentFrame++;
                            if (i_currentFrame == i_maxFrames - 1)
                            {
                                lg_backMove = true;
                                lg_iterationFinished = true;
                            }
                        }
                    }
                        ;
                        break;
                }

                i_animationData = MASK_SPRITEACTIVE | (lg_backMove ? MASK_BACKANIMATION : 0x0) | (i_nextFrameCounter << SHR_NEXTFRAMEDELAY) | i_currentFrame;
                ai_sda[i_curOffset + SPRITEDATAOFFSET_ANIMATIONDATA] = i_animationData;

                // Проверка на изменение состояния объекта
                if ((i_nextObjectStateType != i_curObjectStateType) && lg_iterationFinished)
                {
                    if ((i_nextObjectStateType >>> SHR_TYPE) == 0xFF)
                    {
                        i_nextObjectStateType &= MASK_STATE;

                        if ((i_nextObjectStateType & (SPRITEBEHAVIOUR_NOTIFY & MASK_STATE))!=0)
                        {
                            // Уведомляем о прошествии итерации
                            Gamelet.notifySpriteAnimationCompleted(i_CollectionID, i_curOffset);
                        }

                        if ((i_nextObjectStateType & (SPRITEBEHAVIOUR_RELEASE & MASK_STATE))!=0)
                        {
                            // Удаляем объект
                            releaseSprite(i_curOffset);
                        }
                    } else
                    {
                        // Переводим в новое состояние и тип
                        int i_newSpriteType = i_nextObjectStateType >>> SHR_TYPE;
                        int i_newSpriteState = i_nextObjectStateType & MASK_STATE;
                        activateSprite(i_newSpriteType, i_newSpriteState, i_curOffset);
                    }
                }
            } else
            {
                i_nextFrameCounter--;
                i_animationData = (i_animationData & ~MASK_NEXTFRAMEDELAY) | (i_nextFrameCounter << SHR_NEXTFRAMEDELAY);
                ai_sda[i_curOffset + SPRITEDATAOFFSET_ANIMATIONDATA] = i_animationData;
            }
        }
    }

    /**
     * Проверяет активен ли спрайт с заданным смещением
     *
     * @param _spriteOffset смещение к спрайту в массиве
     * @return true если спрайт активен, иначе false
     */
    public final boolean isSpriteActive(int _spriteOffset)
    {
        return (ai_spriteDataArray[_spriteOffset + SPRITEDATAOFFSET_ANIMATIONDATA] & MASK_SPRITEACTIVE) != 0;
    }

    /**
     * Присваивает значения следующих типа и состояния спрайта, которые будут присвоены ему по окончании текущей анимационной итерации
     *
     * @param _spriteOffset  смещение спрайта в массиве
     * @param _nextTypeState тип и состояние в формате (Тип<<8)|Состояние, так же может быть из разряба SPRITEBEHAVIOUR
     */
    public final void setSpriteNextTypeState(int _spriteOffset, int _nextTypeState)
    {
        int i_dataOffset = _spriteOffset + SPRITEDATAOFFSET_OBJECTTYPESTATE;
        int i_old = ai_spriteDataArray[i_dataOffset];
        i_old = (i_old & MASK_TYPESTATE) | (_nextTypeState<<SHR_NEXTTYPESTATE);
        ai_spriteDataArray[i_dataOffset] = i_old;
    }

     /**
     * Запись состояния спрайтов в поток
     *
     * @param _outStream поток для записи состояния
     * @throws Exception исключение в случае ошибки записи
     */
    public final void saveToStream(DataOutputStream _outStream) throws Exception
    {
        int i_sprNumber = (i_SpritesNumber * SPRITEDATA_LENGTH) - 1;
        int[] ai_dataArray = ai_spriteDataArray;

        _outStream.writeInt(i_lastActiveSpriteOffset);
        _outStream.writeInt(i_lastInactiveSpriteOffset);

        while (i_sprNumber >= 0)
        {
            int i_data = ai_dataArray[i_sprNumber--];
            _outStream.writeInt(i_data);
        }
        _outStream.flush();
    }

    /**
     * Загрузка состояния спрайтов из потока
     *
     * @param _inStream поток для чтения состояния
     * @throws Exception исключение в случае ошибки чтения
     */
    public final void loadFromStream(DataInputStream _inStream) throws Exception
    {
        int i_sprNumber = (i_SpritesNumber * SPRITEDATA_LENGTH) - 1;
        final int[] ai_dataArray = ai_spriteDataArray;

        i_lastActiveSpriteOffset = _inStream.readInt();
        i_lastInactiveSpriteOffset = _inStream.readInt();

        while (i_sprNumber >= 0)
        {
            ai_dataArray[i_sprNumber--] = _inStream.readInt();
        }
    }

    /**
     * Возвращает размер блока данных в байтах, требуемых для хранения данных состояния спрайтов в заданном количестве
     *
     * @param _spritesNumber количество спрайтов
     * @return размер области в байтах
     */
    public static final int getDataSize(int _spritesNumber)
    {
        return 8 + (4 * SPRITEDATA_LENGTH * _spritesNumber);
    }

    /**
     * Клонировать спрайт с заданным смещением
     *
     * @param _clonedSpriteOffset смещение до области спрайта, подлежащего клонированию
     * @return смещение до нового спрайта-клона или -1 если не было свободных спрайтов
     */
    public final int cloneSprite(int _clonedSpriteOffset)
    {
        final int[] ai_sda = ai_spriteDataArray;

        int i_newSpriteOffset = i_lastInactiveSpriteOffset;
        if (i_newSpriteOffset < 0) return -1;

        // Убираем спрайт из списка неактивных и вставляем в список активных
        int i_prevInactiveSpriteOffset = ai_sda[i_newSpriteOffset];
        int i_nextInactiveSpriteOffset = ai_sda[i_newSpriteOffset + 1];
        if (i_prevInactiveSpriteOffset >= 0)
        {
            ai_sda[i_prevInactiveSpriteOffset + 1] = i_nextInactiveSpriteOffset;
        }
        if (i_nextInactiveSpriteOffset >= 0)
        {
            ai_sda[i_nextInactiveSpriteOffset] = i_prevInactiveSpriteOffset;
        }
        i_lastInactiveSpriteOffset = i_prevInactiveSpriteOffset;

        // Вставляем спрайт в список активных
        int i_lastActiveSprOffset = i_lastActiveSpriteOffset;
        ai_sda[i_newSpriteOffset] = i_lastActiveSprOffset;
        ai_sda[i_newSpriteOffset + 1] = -1;
        if (i_lastActiveSprOffset >= 0)
        {
            ai_sda[i_lastActiveSprOffset + 1] = i_newSpriteOffset;
        }
        i_lastActiveSpriteOffset = i_newSpriteOffset;

        System.arraycopy(ai_sda, _clonedSpriteOffset + 2, ai_sda, i_newSpriteOffset + 2, SPRITEDATA_LENGTH - 2);

        return i_newSpriteOffset;
    }

    /**
     * Найти спрайт, пересекающийся с заданным спрайтом, заданной коллекции
     *
     * @param _spriteOffset     смещение до проверяемого спрайта в заданной коллекции
     * @param _spriteCollection заданная коллекция спрайтов
     * @return смещение до пересекающегося спрайта или -1 если таковой не найден
     */
    public final int findCollidedSprite(int _spriteOffset, SpriteCollection _spriteCollection)
    {
        boolean lg_ignoreThisOffset = _spriteCollection.i_CollectionID == i_CollectionID;

        int[] ai_dataArray = _spriteCollection.ai_spriteDataArray;
        short[] ash_staticData = _spriteCollection.ash_spriteAnimationDataArray;
        int i_indexOffset = _spriteOffset + SPRITEDATAOFFSET_STATICDATAOFFSET;
        int i_dataOffset = ai_dataArray[i_indexOffset++];
        int i_ScrX = ai_dataArray[i_indexOffset++];
        int i_ScrY = ai_dataArray[i_indexOffset];

        i_indexOffset = i_dataOffset + ANIMATIONTABLEEOFFSET_HOTZONEOFFSETX;
        int i_mainHotXoff = i_ScrX + ((int)ash_staticData[i_indexOffset++]<<8);
        int i_mainHotYoff = i_ScrY + ((int)ash_staticData[i_indexOffset++]<<8);
        int i_mainHotW = i_mainHotXoff + ((int)ash_staticData[i_indexOffset++]<<8);
        int i_mainHotH = i_mainHotYoff + ((int)ash_staticData[i_indexOffset]<<8);

        ash_staticData = ash_spriteAnimationDataArray;

        ai_dataArray = ai_spriteDataArray;
        int i_lastActive = i_lastActiveSpriteOffset;
        while (true)
        {
            if (i_lastActive < 0) break;

            if (lg_ignoreThisOffset && i_lastActive == _spriteOffset)
            {
                i_lastActive = ai_dataArray[i_lastActive];
                continue;
            }

            i_indexOffset = i_lastActive + SPRITEDATAOFFSET_STATICDATAOFFSET;
            i_dataOffset = ai_dataArray[i_indexOffset++];
            i_ScrX = ai_dataArray[i_indexOffset++];
            i_ScrY = ai_dataArray[i_indexOffset];
            i_dataOffset = i_dataOffset + ANIMATIONTABLEEOFFSET_HOTZONEOFFSETX;

            int i_cHotXoff = i_ScrX + ((int)ash_staticData[i_dataOffset++]<<8);
            int i_cHotYoff = i_ScrY + ((int)ash_staticData[i_dataOffset++]<<8);
            int i_cHotW = i_cHotXoff + ((int)ash_staticData[i_dataOffset++]<<8);
            int i_cHotH = i_cHotYoff + ((int)ash_staticData[i_dataOffset++]<<8);

            if (i_mainHotW < i_cHotXoff || i_mainHotH < i_cHotYoff || i_cHotW < i_mainHotXoff || i_cHotH < i_mainHotYoff)
            {
                i_lastActive = ai_dataArray[i_lastActive];
            } else
            {
                return i_lastActive;
            }
        }
        return -1;
    }

    /**
     * Выставить координаты главной точки для спрайта с заданным смещением
     *
     * @param _spriteOffset смещение спрайта в таблице
     * @param _newMainX     новое значение X главной точки
     * @param _newMainY     новое значение Y главной точки
     */
    public final void setMainPointXY(int _spriteOffset, int _newMainX, int _newMainY)
    {
        final int[] ai_sda = ai_spriteDataArray;
        final short[] ash_ssda = ash_spriteAnimationDataArray;

        int i_staticDataOffset = ai_sda[_spriteOffset + SPRITEDATAOFFSET_STATICDATAOFFSET];
        i_staticDataOffset = i_staticDataOffset + ANIMATIONTABLEEOFFSET_OFFSETTOMAINX;
        int i_offsetX = 0-(ash_ssda[i_staticDataOffset++]<<8);
        int i_offsetY = 0-(ash_ssda[i_staticDataOffset]<<8);

        i_staticDataOffset = _spriteOffset + SPRITEDATAOFFSET_SCREENX;
        ai_sda[i_staticDataOffset++] = _newMainX + i_offsetX;
        ai_sda[i_staticDataOffset++] = _newMainY + i_offsetY;

        ai_sda[i_staticDataOffset++] = _newMainX;
        ai_sda[i_staticDataOffset++] = _newMainY;
    }

    /**
     * Сместить координаты главной точки для спрайта с заданным смещением
     *
     * @param _spriteOffset смещение спрайта в таблице
     * @param _deltaX       смещение по оси X главной точки
     * @param _deltaY       смещение по оси Y главной точки
     */
    public final void moveMainPointXY(int _spriteOffset, int _deltaX, int _deltaY)
    {
        final int[] ai_sda = ai_spriteDataArray;
        final short[] ash_ssda = ash_spriteAnimationDataArray;

        int i_staticDataOffset = ai_sda[_spriteOffset + SPRITEDATAOFFSET_STATICDATAOFFSET];
        i_staticDataOffset = i_staticDataOffset + ANIMATIONTABLEEOFFSET_OFFSETTOMAINX;
        int i_offsetX = (0-ash_ssda[i_staticDataOffset++])<<8;
        int i_offsetY = (0-ash_ssda[i_staticDataOffset])<<8;

        i_staticDataOffset = _spriteOffset + SPRITEDATAOFFSET_MAINX;
        int i_mainX = ai_sda[i_staticDataOffset++] + _deltaX;
        int i_mainY = ai_sda[i_staticDataOffset] + _deltaY;

        i_staticDataOffset = _spriteOffset + SPRITEDATAOFFSET_SCREENX;
        ai_sda[i_staticDataOffset++] = i_mainX + i_offsetX;
        ai_sda[i_staticDataOffset++] = i_mainY + i_offsetY;

        ai_sda[i_staticDataOffset++] = i_mainX;
        ai_sda[i_staticDataOffset++] = i_mainY;
    }
}
