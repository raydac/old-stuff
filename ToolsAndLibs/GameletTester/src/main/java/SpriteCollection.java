
import java.io.*;

/**
 * Класс, описывает коллекцию спрайтов и сервисные функции для работы с ней
 * (C) 2005-2006 Raydac Research Group Ltd.
 *
 * @author Игорь Мазница
 * @version 2.01
 */
public class SpriteCollection
{
    /**
     * Координата X верхнего левого края видимой области экрана
     */
    private static int i8_ScreenTopLeftX;

    /**
     * Координата Y верхнего левого края видимой области экрана
     */
    private static int i8_ScreenTopLeftY;

    /**
     * Координата X нижнего правого края видимой области экрана
     */
    private static int i8_ScreenBottomRightX;

    /**
     * Координата Y нижнего правого края видимой области экрана
     */
    private static int i8_ScreenBottomRightY;

    /**
     * Маска для получения номера кадра из общего поля
     */
    public static final int MASK_FRAMENUMBER = 0x3FFF;

    /**
     * Сдвиг вправо для получения номера кадра из общего поля
     */
    public static final int SHR_FRAMENUMBER = 0;

    /**
     * Маска для получения флага активности спрайта из общего поля
     */
    public static final int MASK_ACTIVE = 0x80000000;

    /**
     * Сдвиг вправо для получения флага активности спрайта из общего поля
     */
    public static final int SHR_ACTIVE = 31;

    /**
     * Маска для получения индекса предыдущего спрайта в списке
     */
    public static final int MASK_LISTPREVINDEX = 0xFFFF;

    /**
     * Сдвиг вправо для получения индекса предыдущего спрайта в списке 
     */
    public static final int SHR_LISTPREVINDEX = 0;

    /**
     * Маска для получения индекса следующего спрайта в списке
     */
    public static final int MASK_LISTNEXTINDEX = 0xFFFF0000;

    /**
     * Сдвиг вправо для получения индекса следующего спрайта в списке 
     */
    public static final int SHR_LISTNEXTINDEX = 16;

    /**
     * Маска для получения флага обратной анимации спрайта из общего поля
     */
    public static final int MASK_BACKANIMATION = 0x40000000;

    /**
     * Сдвиг вправо для получения флага обратной анимации спрайта из общего поля
     */
    public static final int SHR_BACKANIMATION = 30;

    /**
     * Маска для получения флага паузы спрайта из общего поля
     */
    public static final int MASK_PAUSED = 0x20000000;

    /**
     * Сдвиг вправо для получения флага паузы спрайта из общего поля
     */
    public static final int SHR_PAUSED = 29;

    /**
     * Маска для получения данных текущего состояния спрайта
     */
    private static final int MASK_CURSTATE = 0x000000FF;

    /**
     * Сдвиг вправо для получения данных текущего состояния спрайта
     */
    private static final int SHR_CURSTATE = 0;

    /**
     * Маска для получения данных текущего типа спрайта
     */
    private static final int MASK_CURTYPE = 0x0000FF00;

    /**
     * Сдвиг вправо для получения данных текущего типа спрайта
     */
    private static final int SHR_CURTYPE = 8;

    /**
     * Маска для получения данных следующего состояния спрайта
     */
    private static final int MASK_NEXTSTATE = 0x00FF0000;

    /**
     * Сдвиг вправо для получения данных следующего состояния спрайта
     */
    private static final int SHR_NEXTSTATE = 16;

    /**
     * Маска для получения данных следующего типа спрайта
     */
    private static final int MASK_NEXTTYPE = 0xFF000000;

    /**
     * Сдвиг вправо для получения данных следующего типа спрайта
     */
    private static final int SHR_NEXTTYPE = 24;

    /**
     * Маска для получения количества тиков до смены кадра спрайта из общего поля
     */
    private static final int MASK_NEXTFRAMEDELAY = 0x1FFFC000;

    /**
     * Смещение количества тиков до смены кадра спрайта в общем поле
     */
    private static final int SHR_NEXTFRAMEDELAY = 14;

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
    public static final int SPRITEDATA_LENGTH = 9;

    /**
     * Константа содержит количество байт, отводимое под сохранение одного спрайта
     */
    public static final int SPRITEDATA_SAVED_LENGTH = 26;

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
     * Смещение до ячейки, содержащей слуюебные данные связи спрайта в списке
     */
    private static final int OFFSET_LISTDATA = 0;

    /**
     * Смещение до ячейки, содержащей тип объекта, а так же тип и состояние объекта в которое он должен перейти при достижении конца итерации анимации
     * Формат:
     * nextType<<24 | nextState<<16 | currentType<<8 | currentState
     * комбинация nextType<<8 | nextState может содержать SPRITEBEHAVIOUR флаг
     */
    public static final int OFFSET_OBJECTTYPESTATE = 1; // [тип/состояние по окончании анимации]<<16 текущие тип и состояние

    /**
     * Смещение до ячейки, хранящей данные анимации спрайта и флаг его активности
     * Формат:
     * (sprite_active ? MASK_ACTIVE : 0x0)| (sprite_paused ? MASK_PAUSED : 0) | ((backanimation_flag ? MASK_BACKANIMATION  : 0x0) | (current_animation_counter<<14) | current_frame
     */
    public static final int OFFSET_ANIMATIONDATA = 2; //SPRITE_ACTIVE | PAUSED | BACKANIMATION |  NEXTFRAMECOUNTER<<15 | FRAME

    /**
     * Смещение до данных по анимации и размерам спрайта в массиве настроек анимации
     */
    public static final int OFFSET_STATICDATAOFFSET = 3;

    /**
     * Смещение до ячейки спрайта, хранящего экранную координату X спрайта
     */
    public static final int OFFSET_SCREENX = 4;

    /**
     * Смещение до ячейки спрайта, хранящего экранную координату Y спрайта
     */
    public static final int OFFSET_SCREENY = 5;

    /**
     * Смещение до ячейки спрайта, хранящего координату X спрайта
     */
    public static final int OFFSET_MAINX = 6;

    /**
     * Смещение до ячейки спрайта, хранящего координату Y спрайта
     */
    public static final int OFFSET_MAINY = 7;

    /**
     * Смещение до ячейки спрайта, хранящего опциональную информацию о спрайте
     */
    public static final int OFFSET_OPTIONALDATA = 8;

    //-----------------------------СМЕЩЕНИЯ ДЛЯ ТАБЛИЦЫ АНИМАЦИЙ--------------------------------------
    /**
     * Смещение в массиве данных анимации до ячейки хранящей значение ширины объекта
     */
    public static final int ANIMATIONTABLEEOFFSET_WIDTH = 0;

    /**
     * Смещение в массиве данных анимации до ячейки хранящей значение высоты объекта
     */
    public static final int ANIMATIONTABLEEOFFSET_HEIGHT = 1;

    /**
     * Смещение в массиве данных анимации до ячейки хранящей значение смещения горячей зоны по оси X от верхнего левого края объекта
     */
    public static final int ANIMATIONTABLEEOFFSET_HOTZONEOFFSETX = 2;

    /**
     * Смещение в массиве данных анимации до ячейки хранящей значение смещения горячей зоны по оси Y от верхнего левого края объекта
     */
    public static final int ANIMATIONTABLEEOFFSET_HOTZONEOFFSETY = 3;

    /**
     * Смещение в массиве данных анимации до ячейки хранящей значение ширины горячей зоны объекта
     */
    public static final int ANIMATIONTABLEEOFFSET_HOTZONEWIDTH = 4;

    /**
     * Смещение в массиве данных анимации до ячейки хранящей значение высоты горячей зоны объекта
     */
    public static final int ANIMATIONTABLEEOFFSET_HOTZONEHEIGHT = 5;

    /**
     * Смещение в массиве данных анимации до ячейки хранящей значение количества кадров анимации объекта
     */
    public static final int ANIMATIONTABLEEOFFSET_FRAMES = 6;

    /**
     * Смещение в массиве данных анимации до ячейки хранящей значение задержки между кадрами при анимации объекта
     */
    public static final int ANIMATIONTABLEEOFFSET_ANIMATIONDELAY = 7;

    /**
     * Смещение в массиве данных анимации до ячейки хранящей значение типа анимации  объекта
     */
    public static final int ANIMATIONTABLEEOFFSET_ANIMATIONTYPE = 8;

    /**
     * Смещение в массиве данных анимации до ячейки хранящей смещение от главной точки до верхнего левого края по оси X
     */
    public static final int ANIMATIONTABLEEOFFSET_OFFSETTOMAINX = 9;

    /**
     * Смещение в массиве данных анимации до ячейки хранящей смещение от главной точки до верхнего левого края по оси Y
     */
    public static final int ANIMATIONTABLEEOFFSET_OFFSETTOMAINY = 10;

    /**
     * Переменная хранит указатель на массив с данными анимации
     */
    protected short[] ash_spriteAnimationDataArray;

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
            final int i_listData = ai_spriteDataArray[i_result + OFFSET_LISTDATA];
            int i_prev = (i_listData & MASK_LISTPREVINDEX) >>> SHR_LISTPREVINDEX;
            if (i_prev == 0xFFFF) i_prev = -1;
            i_lastIteratorSpriteOffset = i_prev;
        }
        return i_result;
    }

    /**
     * Конструктор коллекции спрайтов
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

        releaseAll();
    }

    /**
     * Деактивизация всех активных спрайтов
     */
    public final void releaseAll()
    {
        final int[] ai_sprites = ai_spriteDataArray;
        i_lastActiveSpriteOffset = -1;

        int i_currentSpriteOffset = 0;
        final int i_index = i_SpritesNumber;

        // Перебираем последовательно все спрайты от последнего до первого, деактивизируя их и выставляя их показатели списка
        for (int li = 0; li < i_index; li++)
        {
            int i_packed = 0;

            if (li == 0)
            {
                i_packed = 0xFFFF << SHR_LISTPREVINDEX;
            }
            else
            {
                i_packed = (i_currentSpriteOffset - SPRITEDATA_LENGTH) << SHR_LISTPREVINDEX;
            }

            if (li == (i_index - 1))
            {
                i_packed |= (0xFFFF << SHR_LISTNEXTINDEX);
            }
            else
            {
                i_packed |= ((i_currentSpriteOffset + SPRITEDATA_LENGTH) << SHR_LISTNEXTINDEX);
            }

            ai_sprites[i_currentSpriteOffset] = i_packed;

            i_currentSpriteOffset += SPRITEDATA_LENGTH;
        }

        i_lastInactiveSpriteOffset = (i_SpritesNumber - 1) * SPRITEDATA_LENGTH;
    }

    /**
     * Перенести неактивный спрайт из списка неактивных в список активных
     * ВНИМАНИЕ! Не осуществляется проверка активности спрайта
     * @param _spriteOffset смещение спрайта
     */
    private final void _moveInactiveSpriteToActiveList(int _spriteOffset)
    {
        final int[] ai_dynSpriteArray = ai_spriteDataArray;

        final int i_curSpriteListIndexOffset = _spriteOffset + OFFSET_LISTDATA;
        int i_offsetListData = ai_dynSpriteArray[i_curSpriteListIndexOffset];

        // "склеиваем" список неактивных
        int i_prevItem = (i_offsetListData & MASK_LISTPREVINDEX) >>> SHR_LISTPREVINDEX;
        int i_nextItem = (i_offsetListData & MASK_LISTNEXTINDEX) >>> SHR_LISTNEXTINDEX;

        if (i_prevItem != 0xFFFF)
        {
            final int i_acc = i_prevItem + OFFSET_LISTDATA;
            int i_old = ai_dynSpriteArray[i_acc];
            i_old &= ~MASK_LISTNEXTINDEX;
            i_old |= (i_nextItem << SHR_LISTNEXTINDEX);
            ai_dynSpriteArray[i_acc] = i_old;
        }

        if (i_nextItem != 0xFFFF)
        {
            final int i_acc = i_nextItem + OFFSET_LISTDATA;
            int i_old = ai_dynSpriteArray[i_acc];
            i_old &= ~MASK_LISTPREVINDEX;
            i_old |= (i_prevItem << SHR_LISTPREVINDEX);
            ai_dynSpriteArray[i_acc] = i_old;
        }

        // выставляем индекс последнего активного пути если данный путь был последним
        if (_spriteOffset == i_lastInactiveSpriteOffset)
        {
            if (i_prevItem == 0xFFFF)
            {
                if (i_nextItem == 0xFFFF)
                    i_lastInactiveSpriteOffset = -1;
                else
                    i_lastInactiveSpriteOffset = i_nextItem;
            }
            else
            {
                i_lastInactiveSpriteOffset = i_prevItem;
            }
        }

        // подключаем путь к списку активных
        if (i_lastActiveSpriteOffset < 0)
        {
            // этот путь будет первым в списке
            i_lastActiveSpriteOffset = _spriteOffset;

            // и хвост и голова граничные
            ai_dynSpriteArray[i_curSpriteListIndexOffset] = 0xFFFFFFFF;
        }
        else
        {
            // прописываем у предыдущего пути ссылку на текущий
            int i_lastinSpriteOffset = i_lastActiveSpriteOffset;
            int i_listData = ai_dynSpriteArray[i_lastinSpriteOffset + OFFSET_LISTDATA];

            i_listData &= ~MASK_LISTNEXTINDEX;
            i_listData |= (_spriteOffset << SHR_LISTNEXTINDEX);

            ai_dynSpriteArray[i_lastinSpriteOffset + OFFSET_LISTDATA] = i_listData;

            // прописываем у текущего ссылку на предыдущий и флаг отсутствия следующего
            i_listData = (i_lastinSpriteOffset << SHR_LISTPREVINDEX) | (0xFFFF << SHR_LISTNEXTINDEX);
            ai_dynSpriteArray[i_curSpriteListIndexOffset] = i_listData;
            i_lastActiveSpriteOffset = _spriteOffset;
        }

        // устанавливаем флаг активности
        ai_dynSpriteArray[_spriteOffset + OFFSET_ANIMATIONDATA] |= MASK_ACTIVE;
    }

    /**
     * Перенести активный спрайт из списка активных в список неактивных
     * ВНИМАНИЕ! Не осуществляется проверка активности спрайта
     * @param _spriteOffset смещение индекса спрайта в массиве
     */
    private final void _moveActiveSpriteToInactiveList(int _spriteOffset)
    {
        final int[] ai_dynSpriteArray = ai_spriteDataArray;

        final int i_curSpriteOffsetList = _spriteOffset + OFFSET_LISTDATA;
        int i_offsetListData = ai_dynSpriteArray[i_curSpriteOffsetList];

        // "склеиваем" список активных 
        int i_prevItem = (i_offsetListData & MASK_LISTPREVINDEX) >>> SHR_LISTPREVINDEX;
        int i_nextItem = (i_offsetListData & MASK_LISTNEXTINDEX) >>> SHR_LISTNEXTINDEX;

        if (i_prevItem != 0xFFFF)
        {
            final int i_acc = i_prevItem + OFFSET_LISTDATA;
            int i_old = ai_dynSpriteArray[i_acc];
            i_old &= ~MASK_LISTNEXTINDEX;
            i_old |= (i_nextItem << SHR_LISTNEXTINDEX);
            ai_dynSpriteArray[i_acc] = i_old;
        }

        if (i_nextItem != 0xFFFF)
        {
            final int i_acc = i_nextItem + OFFSET_LISTDATA;
            int i_old = ai_dynSpriteArray[i_acc];
            i_old &= ~MASK_LISTPREVINDEX;
            i_old |= (i_prevItem << SHR_LISTPREVINDEX);
            ai_dynSpriteArray[i_acc] = i_old;
        }

        // выставляем индекс последнего активного спрайта если данный спрайт был последним
        if (_spriteOffset == i_lastActiveSpriteOffset)
        {
            if (i_prevItem == 0xFFFF)
            {
                if (i_nextItem == 0xFFFF)
                    i_lastActiveSpriteOffset = -1;
                else
                    i_lastActiveSpriteOffset = i_nextItem;
            }
            else
            {
                i_lastActiveSpriteOffset = i_prevItem;
            }
        }

        // подключаем путь к списку неактивных
        if (i_lastInactiveSpriteOffset < 0)
        {
            // этот путь будет первым в списке
            i_lastInactiveSpriteOffset = _spriteOffset;

            // и хвост и голова граничные
            ai_dynSpriteArray[i_curSpriteOffsetList] = 0xFFFFFFFF;
        }
        else
        {
            // прописываем у предыдущего пути ссылку на текущий
            int i_lastinSpriteOffset = i_lastInactiveSpriteOffset;
            int i_listData = ai_dynSpriteArray[i_lastinSpriteOffset + OFFSET_LISTDATA];

            i_listData &= ~MASK_LISTNEXTINDEX;
            i_listData |= (_spriteOffset << SHR_LISTNEXTINDEX);

            ai_dynSpriteArray[i_lastinSpriteOffset + OFFSET_LISTDATA] = i_listData;

            // прописываем у текущего ссылку на предыдущий и флаг отсутствия следующего
            i_listData = (i_lastinSpriteOffset << SHR_LISTPREVINDEX) | (0xFFFF << SHR_LISTNEXTINDEX);
            ai_dynSpriteArray[i_curSpriteOffsetList] = i_listData;
            i_lastInactiveSpriteOffset = _spriteOffset;
        }

        // сбрасываем флаг активности паузы и обратного движения по пути
        ai_dynSpriteArray[_spriteOffset + OFFSET_ANIMATIONDATA] &= ~(MASK_ACTIVE | MASK_PAUSED | MASK_BACKANIMATION);
    }

    /**
     * Деактивизировать спрайт по заданному смещению
     *
     * @param _spriteOffset смещение в массиве данных спрайта
     */
    public final void releaseOne(int _spriteOffset)
    {
        int i_sprAnim = ai_spriteDataArray[_spriteOffset + OFFSET_ANIMATIONDATA];
        if ((i_sprAnim & MASK_ACTIVE) != 0)
        {
            _moveActiveSpriteToInactiveList(_spriteOffset);
        }
    }

    /**
     * Функция возвращает упакованное значение ширины и высоты фрейма спрайта, переведенные в нормальные величины
     *
     * @param _spriteOffset смещение спрайта в массиве
     * @return возвращает упакованное значение (width << 16) | height
     */
    public final int getWidthHeight(int _spriteOffset)
    {
        final int[] ai_spriteData = ai_spriteDataArray;
        final short[] ash_spriteAnimationData = ash_spriteAnimationDataArray;
        int i_dataOffset = ai_spriteData[_spriteOffset + OFFSET_STATICDATAOFFSET] + ANIMATIONTABLEEOFFSET_WIDTH;
        int i_w = ash_spriteAnimationData[i_dataOffset++];
        int i_h = ash_spriteAnimationData[i_dataOffset];

        return (i_w << 16) | i_h;
    }

    /**
     * Функция возвращает упакованное значение ширины и высоты горячей зоны спрайта, переведенные в нормальные величины (не I8)
     *
     * @param _spriteOffset смещение спрайта в массиве
     * @return возвращает упакованное значение (hotzonewidth << 16) | hotzoneheight
     */
    public final int getHotzoneWidthHeight(int _spriteOffset)
    {
        final int[] ai_spriteData = ai_spriteDataArray;
        final short[] ash_spriteAnimationData = ash_spriteAnimationDataArray;
        int i_dataOffset = ai_spriteData[_spriteOffset + OFFSET_STATICDATAOFFSET] + ANIMATIONTABLEEOFFSET_HOTZONEWIDTH;
        int i_w = ash_spriteAnimationData[i_dataOffset++];
        int i_h = ash_spriteAnimationData[i_dataOffset];

        return (i_w << 16) | i_h;
    }

    /**
     * Функция возвращает упакованное значение смещения горячей зоны спрайта, переведенные в нормальные величины (не I8)
     *
     * @param _spriteOffset смещение спрайта в массиве
     * @return возвращает упакованное значение (hotzoneXoffset << 16) | hotzoneYoffset
     */
    public final int getHotzoneXY(int _spriteOffset)
    {
        final int[] ai_spriteData = ai_spriteDataArray;
        final short[] ash_spriteAnimationData = ash_spriteAnimationDataArray;
        int i_dataOffset = ai_spriteData[_spriteOffset + OFFSET_STATICDATAOFFSET] + ANIMATIONTABLEEOFFSET_HOTZONEOFFSETX;
        int i_x = ash_spriteAnimationData[i_dataOffset++];
        int i_y = ash_spriteAnimationData[i_dataOffset];

        return (i_x << 16) | (i_y & 0xFFFF);
    }

    /**
     * Функция возвращает упакованное значение экранных координат спрайта, переведенные в нормальные величины
     *
     * @param _spriteOffset смещение спрайта в массиве
     * @return возвращает упакованное значение (X << 16) | Y
     */
    public final int getScreenXY(int _spriteOffset)
    {
        final int[] ai_spriteData = ai_spriteDataArray;
        int i_dataOffset = _spriteOffset + OFFSET_SCREENX;
        int i_x = (ai_spriteData[i_dataOffset++] + 0x7F) >> 8;
        int i_y = (ai_spriteData[i_dataOffset] + 0x7F) >> 8;

        return (i_x << 16) | (i_y & 0xFFFF);
    }

    /**
     * Функция возвращает координаты и размеры горячей зоны спрайта упакованные в long 
     *
     * @param _spriteOffset смещение спрайта в массиве
     * @return (xoffst << 48) | (yoffst << 32) | (width << 16) | (height)
     */
    public final long getHotspotCoordinates(int _spriteOffset)
    {
        final int[] ai_spriteData = ai_spriteDataArray;
        final short[] ash_spriteAnimationData = ash_spriteAnimationDataArray;
        int i_dataOffset = ai_spriteData[_spriteOffset + OFFSET_STATICDATAOFFSET] + ANIMATIONTABLEEOFFSET_HOTZONEOFFSETX;
        int i_x = ash_spriteAnimationData[i_dataOffset++];
        int i_y = ash_spriteAnimationData[i_dataOffset++];
        int i_w = ash_spriteAnimationData[i_dataOffset++];
        int i_h = ash_spriteAnimationData[i_dataOffset];

        return ((long) i_x << 48) | ((long) i_y << 32) | ((long) i_w << 16) | (long) i_h;
    }

    /**
     * Функция возвращает упакованное значение номера текущего кадра и количества кадров
     *
     * @param _spriteOffset смещение спрайта в массиве
     * @return (framenum << 16) | (cur_frame)
     */
    public final int getFrameFullInfo(int _spriteOffset)
    {
        final int[] ai_spriteData = ai_spriteDataArray;
        final short[] ash_spriteAnimationData = ash_spriteAnimationDataArray;

        int i_dataOffset = ai_spriteData[_spriteOffset + OFFSET_STATICDATAOFFSET];
        int i_frames = ash_spriteAnimationData[i_dataOffset + ANIMATIONTABLEEOFFSET_FRAMES];
        int i_curFrame = (ai_spriteData[_spriteOffset + OFFSET_ANIMATIONDATA] & MASK_FRAMENUMBER) >>> SHR_FRAMENUMBER;
        return (i_frames << 16) | i_curFrame;
    }

    /**
     * Функция возвращает номер текущего кадра
     * @param _spriteOffset смещение данных спрайта
     * @return номер 
     */
    public final int getFrameNumber(int _spriteOffset)
    {
        return (ai_spriteDataArray[_spriteOffset + OFFSET_ANIMATIONDATA] & MASK_FRAMENUMBER) >>> SHR_FRAMENUMBER;
    }

    /**
     * Устанавливает номер кадра анимации для заданного спрайта
     * @param _spriteOffset смещение данных спрайта
     * @param _frame номер спрайта
     */
    public final void setFrameNumber(int _spriteOffset, int _frame)
    {
        final int[] ai_spriteData = ai_spriteDataArray;
        _frame = (_frame << SHR_FRAMENUMBER) & MASK_FRAMENUMBER;
        int i_offset = _spriteOffset + OFFSET_ANIMATIONDATA;
        int i_data = (ai_spriteData[i_offset] & ~MASK_FRAMENUMBER) | _frame;
        ai_spriteData[i_offset] = i_data;
    }

    /**
     * Активизировать спрайт с заданным смещением в массиве
     * 
     * @param _spriteOffset смещение до данных спрайта
     * @param _objectType   тип объекта
     * @param _objectState  тип состояния
     */
    public final void activateOne(final int _spriteOffset, int _objectType, int _objectState)
    {
        int i_spriteOffset = _spriteOffset;
        final int[] ai_sda = ai_spriteDataArray;
        final short[] ash_ssda = ash_spriteAnimationDataArray;

        // Проверяем, активен ли уже спрайт, если нет то активизируем, иначе пропускаем активизацию
        int i_offsetSpriteDataAnimation = _spriteOffset + OFFSET_ANIMATIONDATA;
        if ((ai_sda[i_offsetSpriteDataAnimation] & MASK_ACTIVE) == 0)
        {
            _moveInactiveSpriteToActiveList(_spriteOffset);
        }

        // переводим указатель на Тип и Состояние
        i_spriteOffset += OFFSET_OBJECTTYPESTATE;

        // поле STATICDATAOFFSET
        // Находим ссылку на смещения для состояний объекта
        int i_staticDataOffset = ash_ssda[(ash_ssda[_objectType]) + _objectState];
        int i_maxAnimationCounter = ash_ssda[i_staticDataOffset + ANIMATIONTABLEEOFFSET_ANIMATIONDELAY];

        // Выставляем тип и состояние
        int i_typeState = (_objectType << SHR_CURTYPE) | (_objectState << SHR_CURSTATE);

        // Следующее сосотояние спрайта указываем таким же
        ai_sda[i_spriteOffset++] = (i_typeState << SHR_NEXTSTATE) | (i_typeState);
        // Данные анимации и флаг активности
        ai_sda[i_spriteOffset++] = MASK_ACTIVE | (i_maxAnimationCounter << SHR_NEXTFRAMEDELAY);
        // Индекс даных из статического массива спрайтов
        ai_sda[i_spriteOffset++] = i_staticDataOffset;

        // Новые смещения для экранных кординат
        i_spriteOffset++;
        i_spriteOffset++;
        int i_curMainX = ai_sda[i_spriteOffset++];
        int i_curMainY = ai_sda[i_spriteOffset];
        setMainPointXY(_spriteOffset, i_curMainX, i_curMainY);
    }

    /**
     * Обрабатываем анимацию для всех активных спрайтов не стоящих на паузе
     */
    public final void processActive()
    {
        int i_lastActive = i_lastActiveSpriteOffset;
        final int[] ai_sda = ai_spriteDataArray;
        final short[] ash_ssda = ash_spriteAnimationDataArray;

        while (true)
        {
            if (i_lastActive < 0)
                break;
            final int i_curOffset = i_lastActive;
            i_lastActive = ai_sda[i_lastActive];

            // Обработка анимации
            int i_animationData = ai_sda[i_curOffset + OFFSET_ANIMATIONDATA];

            // Не обрабатываем спрайты на паузе
            if ((i_animationData & MASK_PAUSED) != 0)
                continue;

            int i_nextFrameCounter = (i_animationData & MASK_NEXTFRAMEDELAY) >>> SHR_NEXTFRAMEDELAY;
            if (i_nextFrameCounter == 0)
            {
                // Отработка анимации
                int i_staticDataOffset = ai_sda[i_curOffset + OFFSET_STATICDATAOFFSET];
                int i_curObjectStateType = ai_sda[i_curOffset + OFFSET_OBJECTTYPESTATE];
                int i_nextObjectStateType = i_curObjectStateType >>> SHR_NEXTSTATE;
                i_curObjectStateType = i_curObjectStateType & (MASK_CURSTATE | MASK_CURTYPE);
                int i_animationType = ash_ssda[i_staticDataOffset + ANIMATIONTABLEEOFFSET_ANIMATIONTYPE];
                int i_maxFrames = ash_ssda[i_staticDataOffset + ANIMATIONTABLEEOFFSET_FRAMES];
                int i_animationDelay = ash_ssda[i_staticDataOffset + ANIMATIONTABLEEOFFSET_ANIMATIONDELAY];

                boolean lg_backMove = (i_animationData & MASK_BACKANIMATION) != 0;
                int i_currentFrame = (i_animationData & MASK_FRAMENUMBER) >>> SHR_FRAMENUMBER;

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
                        }
                        else
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

                i_animationData = MASK_ACTIVE | (lg_backMove ? MASK_BACKANIMATION : 0x0) | (i_nextFrameCounter << SHR_NEXTFRAMEDELAY) | (i_currentFrame << SHR_FRAMENUMBER);
                ai_sda[i_curOffset + OFFSET_ANIMATIONDATA] = i_animationData;

                // Проверка на изменение состояния объекта
                if ((i_nextObjectStateType != i_curObjectStateType) && lg_iterationFinished)
                {
                    if ((i_nextObjectStateType >>> SHR_CURTYPE) == 0xFF)
                    {
                        i_nextObjectStateType &= MASK_CURSTATE;

                        if ((i_nextObjectStateType & (SPRITEBEHAVIOUR_NOTIFY & MASK_CURSTATE)) != 0)
                        {
                            // Уведомляем о прошествии итерации
                            Gamelet.notifySpriteAnimationCompleted(this, i_curOffset);
                        }

                        if ((i_nextObjectStateType & (SPRITEBEHAVIOUR_RELEASE & MASK_CURSTATE)) != 0)
                        {
                            // Удаляем объект
                            _moveActiveSpriteToInactiveList(i_curOffset);
                        }
                    }
                    else
                    {
                        // Переводим в новое состояние и тип
                        int i_newSpriteType = i_nextObjectStateType >>> SHR_CURTYPE;
                        int i_newSpriteState = i_nextObjectStateType & MASK_CURSTATE;
                        activateOne(i_newSpriteType, i_newSpriteState, i_curOffset);
                    }
                }
            }
            else
            {
                i_nextFrameCounter--;
                i_animationData = (i_animationData & ~MASK_NEXTFRAMEDELAY) | (i_nextFrameCounter << SHR_NEXTFRAMEDELAY);
                ai_sda[i_curOffset + OFFSET_ANIMATIONDATA] = i_animationData;
            }
        }
    }

    /**
     * Проверяет активен ли спрайт с заданным смещением
     *
     * @param _spriteOffset смещение к спрайту в массиве
     * @return true если спрайт активен, иначе false
     */
    public final boolean isActive(int _spriteOffset)
    {
        return (ai_spriteDataArray[_spriteOffset + OFFSET_ANIMATIONDATA] & MASK_ACTIVE) != 0;
    }

    /**
     * Возвращает смещение заданного индексом спрайта в массиве данных
     * @param _index индекс спрайта
     * @return смещение спрайта в массиве
     */
    public static final int getOffsetForSpriteWithIndex(int _index)
    {
        return SPRITEDATA_LENGTH * _index;
    }

    /**
     * Присваивает значения следующих типа и состояния спрайта, которые будут присвоены ему по окончании текущей анимационной итерации
     *
     * @param _spriteOffset  смещение спрайта в массиве
     * @param _nextTypeState тип и состояние в формате (Тип<<8)|Состояние, так же может быть из разряба SPRITEBEHAVIOUR
     */
    public final void setNextTypeState(int _spriteOffset, int _nextTypeState)
    {
        int i_dataOffset = _spriteOffset + OFFSET_OBJECTTYPESTATE;
        int i_old = ai_spriteDataArray[i_dataOffset];
        i_old = (i_old & (MASK_CURTYPE | MASK_CURSTATE)) | (_nextTypeState << SHR_NEXTSTATE);
        ai_spriteDataArray[i_dataOffset] = i_old;
    }

    /**
     * Запись состояния спрайтов в поток
     *
     * @param _outStream поток для записи состояния
     * @throws Throwable исключение в случае ошибки записи
     */
    public final void saveToStream(DataOutputStream _outStream) throws Throwable
    {
        int i_sprNumber = (i_SpritesNumber * SPRITEDATA_LENGTH);
        int[] ai_dataArray = ai_spriteDataArray;

        _outStream.writeInt(i_lastIteratorSpriteOffset);
        _outStream.writeInt(i_lastActiveSpriteOffset);
        _outStream.writeInt(i_lastInactiveSpriteOffset);

        int i_offset = 0;

        while (i_offset < i_sprNumber)
        {
            _outStream.writeInt(ai_dataArray[i_offset++]); // list data

            _outStream.writeInt(ai_dataArray[i_offset++]); // object state
            _outStream.writeInt(ai_dataArray[i_offset++]); // animation data

            _outStream.writeShort(ai_dataArray[i_offset++]); // static data offset

            i_offset++; // screen x
            i_offset++; // screen y

            _outStream.writeInt(ai_dataArray[i_offset++]); // main x
            _outStream.writeInt(ai_dataArray[i_offset++]); // main y

            _outStream.writeInt(ai_dataArray[i_offset++]); // optional data
        }
        _outStream.flush();
    }

    /**
     * Загрузка состояния спрайтов из потока
     *
     * @param _inStream поток для чтения состояния
     * @throws Throwable исключение в случае ошибки чтения
     */
    public final void loadFromStream(DataInputStream _inStream) throws Throwable
    {
        int i_sprNumber = (i_SpritesNumber * SPRITEDATA_LENGTH);
        final int[] ai_dataArray = ai_spriteDataArray;

        i_lastIteratorSpriteOffset = _inStream.readInt();
        i_lastActiveSpriteOffset = _inStream.readInt();
        i_lastInactiveSpriteOffset = _inStream.readInt();

        int i_offset = 0;

        while (i_offset < i_sprNumber)
        {
            int i_sprOffset = i_offset;
            ai_dataArray[i_offset] = _inStream.readInt(); // list data
            i_offset++;

            ai_dataArray[i_offset] = _inStream.readInt(); // object state
            i_offset++;

            int i_anim = _inStream.readInt();
            ai_dataArray[i_offset] = i_anim; // animation data
            i_offset++;

            ai_dataArray[i_offset] = _inStream.readUnsignedShort(); // static data offset
            i_offset++;

            i_offset++; // screen x
            i_offset++; // screen y

            int i_mx = _inStream.readInt();
            i_offset++;
            int i_my = _inStream.readInt();
            i_offset++;

            ai_dataArray[i_offset] = _inStream.readInt(); // optional data
            i_offset++;

            if ((i_anim & MASK_ACTIVE) != 0)
            {
                setMainPointXY(i_sprOffset, i_mx, i_my);
            }
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
        return 12 + SPRITEDATA_SAVED_LENGTH * _spritesNumber;
    }

    /**
     * Выровнять у двух спрайтов из одного набора главные точки
     * @param _sourceSpriteOffset смещение у выравниваемого спрайта
     * @param _destSpriteOffset смещение у целевого спрайта
     */
    public final void alignMainPoint(int _sourceSpriteOffset, int _destSpriteOffset)
    {
        final int[] ai_sda = ai_spriteDataArray;
        int i_off = _destSpriteOffset + OFFSET_MAINX;
        int i_mainX = ai_sda[i_off++];
        int i_mainY = ai_sda[i_off];
        setMainPointXY(_sourceSpriteOffset, i_mainX, i_mainY);
    }

    /**
     * Клонировать спрайт с заданным смещением
     *
     * @param _spriteOffset смещение до области спрайта, подлежащего клонированию
     * @return смещение до нового спрайта-клона или -1 если не было свободных спрайтов
     */
    public final void cloneActive(int _srcSpriteOffset, int _dstSpriteOffset)
    {
        if (!isActive(_dstSpriteOffset))
        {
            // Путь в который будет произведено копирование данных не активен
            _moveInactiveSpriteToActiveList(_dstSpriteOffset);
        }

        System.arraycopy(ai_spriteDataArray, _srcSpriteOffset, ai_spriteDataArray, _dstSpriteOffset, SPRITEDATA_LENGTH);
    }

    /**
     * Функция позволяет проверить стоит ли спрайт на паузе
     * @param _spriteOffset смещение данных проверяемого спрайта
     * @return true если спрайт на паузе и false если спрайт не на паузе или неактивен
     */
    public final boolean isPaused(int _spriteOffset)
    {
        int i_data = ai_spriteDataArray[_spriteOffset + OFFSET_ANIMATIONDATA];
        if ((i_data & MASK_ACTIVE) != 0)
        {
            return (i_data & MASK_PAUSED) != 0;
        }
        return false;
    }

    /**
     * Выставляет флаг паузы анимации спрайта
     * @param _spriteOffset смещение до данных спрайта
     * @param _pause true если поставить на паузу и false если снять с паузы
     */
    public void setPause(int _spriteOffset, boolean _pause)
    {
        if (_pause)
        {
            ai_spriteDataArray[_spriteOffset + OFFSET_ANIMATIONDATA] |= MASK_PAUSED;
        }
        else
        {
            ai_spriteDataArray[_spriteOffset + OFFSET_ANIMATIONDATA] &= ~MASK_PAUSED;
        }
    }

    /**
     * Возвращает тип спрайта по смещению
     * @param _spriteOffset смещение спрайта
     * @return тип спрайта
     */
    public final int getType(int _spriteOffset)
    {
        return (ai_spriteDataArray[_spriteOffset + OFFSET_OBJECTTYPESTATE] & MASK_CURTYPE) >>> SHR_CURTYPE;
    }

    /**
     * Возвращает состояние спрайта по смещению
     * @param _spriteOffset смещение спрайта
     * @return состояние спрайта
     */
    public final int getState(int _spriteOffset)
    {
        return ai_spriteDataArray[_spriteOffset + OFFSET_OBJECTTYPESTATE] & 0xFF;
    }

    /**
     * Найти спрайт, пересекающийся с заданным спрайтом, заданной коллекции
     *
     * @param _spriteOffset     смещение до проверяемого спрайта в заданной коллекции
     * @param _spriteCollection заданная коллекция спрайтов
     * @param _initSpriteOffset смещение спрайта, с предыдущего которыму надо продолжать проверку (проверка идет по списку с конца), если -1 то проверка идет с самого последнего активного спрайта
     * @return смещение до первого пересекающегося спрайта или -1 если таковой не найден
     */
    public final int findCollidedSprite(int _spriteOffset, SpriteCollection _spriteCollection, int _initSpriteOffset)
    {
        boolean lg_ignoreThisOffset = _spriteCollection.i_CollectionID == i_CollectionID;

        int[] ai_dataArray = _spriteCollection.ai_spriteDataArray;

        short[] ash_staticData = ash_spriteAnimationDataArray;
        int i_indexOffset = _spriteOffset + OFFSET_STATICDATAOFFSET;
        int i_dataOffset = ai_dataArray[i_indexOffset++];
        int i_ScrX = ai_dataArray[i_indexOffset++];
        int i_ScrY = ai_dataArray[i_indexOffset];

        i_indexOffset = i_dataOffset + ANIMATIONTABLEEOFFSET_HOTZONEOFFSETX;
        int i_mainHotXoff = i_ScrX + ((int) ash_staticData[i_indexOffset++] << 8);
        int i_mainHotYoff = i_ScrY + ((int) ash_staticData[i_indexOffset++] << 8);
        int i_mainHotW = i_mainHotXoff + ((int) ash_staticData[i_indexOffset++] << 8);
        int i_mainHotH = i_mainHotYoff + ((int) ash_staticData[i_indexOffset] << 8);

        ai_dataArray = ai_spriteDataArray;

        int i_lastActive;

        if (_initSpriteOffset < 0)
            i_lastActive = i_lastActiveSpriteOffset;
        else
        {
            i_lastActive = ai_dataArray[_initSpriteOffset];
        }

        while (true)
        {
            if (i_lastActive < 0)
                break;

            if (lg_ignoreThisOffset && i_lastActive == _spriteOffset)
            {
                i_lastActive = ai_dataArray[i_lastActive];
                continue;
            }

            i_indexOffset = i_lastActive + OFFSET_STATICDATAOFFSET;
            i_dataOffset = ai_dataArray[i_indexOffset++];
            i_ScrX = ai_dataArray[i_indexOffset++];
            i_ScrY = ai_dataArray[i_indexOffset];
            i_dataOffset = i_dataOffset + ANIMATIONTABLEEOFFSET_HOTZONEOFFSETX;

            int i_cHotXoff = i_ScrX + ((int) ash_staticData[i_dataOffset++] << 8);
            int i_cHotYoff = i_ScrY + ((int) ash_staticData[i_dataOffset++] << 8);
            int i_cHotW = i_cHotXoff + ((int) ash_staticData[i_dataOffset++] << 8);
            int i_cHotH = i_cHotYoff + ((int) ash_staticData[i_dataOffset] << 8);

            if (i_mainHotW < i_cHotXoff || i_mainHotH < i_cHotYoff || i_cHotW < i_mainHotXoff || i_cHotH < i_mainHotYoff)
            {
                i_lastActive = ai_dataArray[i_lastActive];
            }
            else
            {
                return i_lastActive;
            }
        }
        return -1;
    }

    /**
     * Возвращает опциональные данные для спрайта
     * @param _spriteOffset смещение спрайта
     * @return значение
     */
    public final int getOptionalData(int _spriteOffset)
    {
        return ai_spriteDataArray[_spriteOffset + OFFSET_OPTIONALDATA];
    }

    /**
     * Записывает опциональные данные спрайта
     * @param _spriteOffset смещение спрайта
     * @param _value значение
     */
    public final void setOptionalData(int _spriteOffset, int _value)
    {
        ai_spriteDataArray[_spriteOffset + OFFSET_OPTIONALDATA] = _value;
    }

    /**
     * Сместить координаты главной точки для спрайта с заданным смещением
     *
     * @param _spriteOffset смещение спрайта в таблице
     * @param _deltaX       смещение по оси X главной точки
     * @param _deltaY       смещение по оси Y главной точки
     */
    public final void moveMainPointXY(int _spriteOffset, int _i8deltaX, int _i8deltaY)
    {
        final int[] ai_sda = ai_spriteDataArray;
        final short[] ash_ssda = ash_spriteAnimationDataArray;

        int i_staticDataOffset = ai_sda[_spriteOffset + OFFSET_STATICDATAOFFSET];
        i_staticDataOffset = i_staticDataOffset + ANIMATIONTABLEEOFFSET_OFFSETTOMAINX;
        int i_offsetX = (0 - ash_ssda[i_staticDataOffset++]) << 8;
        int i_offsetY = (0 - ash_ssda[i_staticDataOffset]) << 8;

        i_staticDataOffset = _spriteOffset + OFFSET_MAINX;
        int i_mainX = ai_sda[i_staticDataOffset++] + _i8deltaX;
        int i_mainY = ai_sda[i_staticDataOffset] + _i8deltaY;

        i_staticDataOffset = _spriteOffset + OFFSET_SCREENX;
        ai_sda[i_staticDataOffset++] = i_mainX + i_offsetX;
        ai_sda[i_staticDataOffset++] = i_mainY + i_offsetY;

        ai_sda[i_staticDataOffset++] = i_mainX;
        ai_sda[i_staticDataOffset++] = i_mainY;
    }

    /**
     * Выставить координаты главной точки для спрайта с заданным смещением
     *
     * @param _spriteOffset смещение спрайта в таблице
     * @param _i8newMainX     новое значение X главной точки
     * @param _i8newMainY     новое значение Y главной точки
     */
    public final void setMainPointXY(int _spriteOffset, int _i8newMainX, int _i8newMainY)
    {
        final int[] ai_sda = ai_spriteDataArray;
        final short[] ash_ssda = ash_spriteAnimationDataArray;

        int i_staticDataOffset = ai_sda[_spriteOffset + OFFSET_STATICDATAOFFSET];
        i_staticDataOffset = i_staticDataOffset + ANIMATIONTABLEEOFFSET_OFFSETTOMAINX;
        int i_offsetX = 0 - (ash_ssda[i_staticDataOffset++] << 8);
        int i_offsetY = 0 - (ash_ssda[i_staticDataOffset] << 8);

        i_staticDataOffset = _spriteOffset + OFFSET_SCREENX;
        ai_sda[i_staticDataOffset++] = _i8newMainX + i_offsetX;
        ai_sda[i_staticDataOffset++] = _i8newMainY + i_offsetY;

        ai_sda[i_staticDataOffset++] = _i8newMainX;
        ai_sda[i_staticDataOffset++] = _i8newMainY;
    }

    /**
     * Устанавливает координаты видимой экранной зоны
     * @param _x координата X верхнего левого края (в пикселях)
     * @param _y координата Y верхнего левого края (в пикселях)
     * @param _width ширина зоны (в пикселях)
     * @param _height высота зоны (в пикселях)
     */
    public static final void setVisibleScreenAreaCoords(int _x, int _y, int _width, int _height)
    {
        i8_ScreenTopLeftX = _x << 8;
        i8_ScreenTopLeftY = _y << 8;
        i8_ScreenBottomRightX = (_x + (_width - 1)) << 8;
        i8_ScreenBottomRightY = (_y + (_height - 1)) << 8;
    }

    /**
     * Проверяет спрайт на видимость в экранной плоскости
     * @param _spriteOffset смещение спрайта
     * @return true если спрайт невидимый и false если видимый
     */
    public final boolean isNoVisibled(int _spriteOffset)
    {
        final int[] ai_spriteData = ai_spriteDataArray;
        final short[] ash_spriteAnimationData = ash_spriteAnimationDataArray;

        int i_doff = _spriteOffset + OFFSET_STATICDATAOFFSET;

        int i_dataOffset = ai_spriteData[i_doff] + ANIMATIONTABLEEOFFSET_WIDTH;
        int i8_w = (ash_spriteAnimationData[i_dataOffset++]) << 8;
        int i8_h = (ash_spriteAnimationData[i_dataOffset]) << 8;

        //i_dataOffset = _spriteOffset + SPRITEDATAOFFSET_SCREENX;
        i_doff++;
        int i8_x = ai_spriteData[i_doff++];
        int i8_y = ai_spriteData[i_doff];

        int i8_x2 = i8_x + i8_w;
        int i8_y2 = i8_y + i8_h;

        final int i8_scrLTX = i8_ScreenTopLeftX;
        final int i8_scrLTY = i8_ScreenTopLeftY;

        final int i8_scrRBX = i8_ScreenBottomRightX;
        final int i8_scrRBY = i8_ScreenBottomRightY;

        return (i8_x2 < i8_scrLTX || i8_y2 < i8_scrLTY || i8_x > i8_scrRBX || i8_y > i8_scrRBY);
    }

    /**
     * Выравниваем спрайт по границам области
     * @param _spriteOffset смещение спрайта
     * @param _i8x1 координата X верхней левой точки границы
     * @param _i8y1 координата Y верхней левой точки границы
     * @param _i8x2 координата X нижней правой точки границы
     * @param _i8y2 координата Y нижней правой точки границы
     * @param _screenCoords выравнивание экранных координат если true и main точки если false
     * @return true если было выравнивание, иначе false
     */
    public final boolean alignToArea(int _spriteOffset, int _i8x1, int _i8y1, int _i8x2, int _i8y2, boolean _screenCoords)
    {
        final int[] ai_sda = ai_spriteDataArray;
        final short[] ash_ssda = ash_spriteAnimationDataArray;

        int i_deltaX = 0;
        int i_deltaY = 0;

        if (_screenCoords)
        {
            // Экранные координаты
            int i_off = _spriteOffset + OFFSET_SCREENX;
            int i_sx = ai_sda[i_off++];
            int i_sy = ai_sda[i_off];

            int i_staticDataOffset = ai_sda[_spriteOffset + OFFSET_STATICDATAOFFSET] + ANIMATIONTABLEEOFFSET_WIDTH;
            int i_w = ash_ssda[i_staticDataOffset++] << 8;
            int i_h = ash_ssda[i_staticDataOffset] << 8;

            if (i_sx < _i8x1)
            {
                i_deltaX = _i8x1 - i_sx;
            }
            else if (i_sx + i_w > _i8x2)
            {
                i_deltaX = _i8x2 - (i_sx + i_w);
            }

            if (i_sy < _i8y1)
            {
                i_deltaY = _i8y1 - i_sy;
            }
            else if (i_sy + i_h > _i8y2)
            {
                i_deltaY = _i8y2 - (i_sy + i_h);
            }
        }
        else
        {
            // Координаты main point
            int i_off = _spriteOffset + OFFSET_MAINX;
            int i_mx = ai_sda[i_off++];
            int i_my = ai_sda[i_off];

            if (i_mx < _i8x1)
            {
                i_deltaX = _i8x1 - i_mx;
            }
            else if (i_mx > _i8x2)
            {
                i_deltaX = _i8x2 - i_mx;
            }

            if (i_my < _i8y1)
            {
                i_deltaY = _i8y1 - i_my;
            }
            else if (i_my > _i8y2)
            {
                i_deltaY = _i8y2 - i_my;
            }
        }

        if ((i_deltaX | i_deltaY) != 0)
        {
            moveMainPointXY(_spriteOffset, i_deltaX, i_deltaY);
            return true;
        }
        return false;
    }
}
