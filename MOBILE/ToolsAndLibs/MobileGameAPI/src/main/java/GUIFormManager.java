import javax.microedition.lcdui.Font;
import javax.microedition.lcdui.Image;
import javax.microedition.lcdui.Graphics;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.Hashtable;

/**
 * Класс содержит менеджер форм.
 * 
 * @author Igor Maznitsa
 * @version 2.03
 */
public class GUIFormManager
{
    /**
     * Максимальный размер массива содержащего данные форм
     */
    private static final int MAXFORMARRAYLENGTH = 1024;

    /**
     * Максимальное количество кнопок на форме, используется при построении списка обхода кнопок
     */
    private static final int MAXBUTTONSONFORM = 16;

    /**
     * Максимальное количество строковых записей в пуле строк
     */
    private static final int MAXTEXTRESOURCES = 512;

    /**
     * Расширение имени графических файлов 
     */
    private static final String IMAGE_DEFAULTEXTENSION = "png";

    /**
     * Константа, показывающая состояние кнопки как нажатое
     */
    public static final int BUTTONSTATE_NORMAL = 0;

    /**
     * Константа, показывающая состояние кнопки как нормальное
     */
    public static final int BUTTONSTATE_PRESSED = 1;

    /**
     * Константа, показывающая состояние кнопки как выбранное
     */
    public static final int BUTTONSTATE_SELECTED = 2;

    /**
     * Константа, показывающая состояние кнопки как запрещенное
     */
    public static final int BUTTONSTATE_DISABLED = 3;

    /**
     * Таблица содержит данные о соответствии имен именованных компонентов и их смещениях в массиве форм
     */
    private static final Hashtable p_PointersTable = new Hashtable();

    /**
     * Текущий размер пула строк
     */
    private static int i_StringPoolLength;

    /**
     * Массив, содержащий пул строк
     */
    private static final String[] as_StringPool = new String[MAXTEXTRESOURCES];

    /**
     * Массив, содержащий кэш ресурсов
     */
    private static final Hashtable p_ResourceCache = new Hashtable();

    /**
     * Строковый буффер
     */
    private static final StringBuffer p_StringBuffer = new StringBuffer(32);

    /**
     * Массив данных форм
     */
    private static final int[] ai_FormsDataArray = new int[MAXFORMARRAYLENGTH];

    /**
     * Список смещений текущих кнопок на форме
     */
    private static final int[] ai_FormButtonsList = new int[MAXBUTTONSONFORM];

    /**
     * Смещение текущей выбранной формы
     */
    private static int i_SelectedFormOffset;

    /**
     * Имя текущей выбранной формы
     */
    public static String s_SelectedFormName;

    /**
     * Цвет текущей формы
     */
    private static int i_SelectedFormColor;

    /**
     * Задержка анимации элементов текущей формы
     */
    public static int i_SelectedFormAnimationDelay;

    /**
     * Количество кнопок на текущей форме
     */
    private static int i_SelectedFormButtonsNumber;

    /**
     * Индекс текущей выбранной кнопки
     */
    private static int i_SelectedButtonIndex;

    /**
     * Системный фонт
     */
    public static Font p_SystemFont;

    /**
     * Объект, используемый в кэше вместо null
     */
    private static final Object NULL = new Object();

    // константы объектов
    /**
     * Объект форма
     */
    private static final int OBJECT_FORM = 0;

    /**
     * Объект изображение на форме
     */
    private static final int OBJECT_IMAGE = 1;

    /**
     * Объект кнопка на форме
     */
    private static final int OBJECT_BUTTON = 2;

    /**
     * Объект залитая цветом прямоугольная область
     */
    private static final int OBJECT_FILLRECT = 3;

    /**
     * Объект прямоугольник
     */
    private static final int OBJECT_RECT = 4;

    /**
     * Объект область отображаемая пользователем
     */
    private static final int OBJECT_CUSTOMAREA = 5;

    /**
     * Объект текстовая метка, выводимая системным шрифтом
     */
    private static final int OBJECT_TEXTLABEL = 6;

    /**
     * Объект кэшируемая картинка
     */
    private static final int OBJECT_CACHEDIMAGE = 7;

    /**
     * Объект область кэшируемой картинки, выводимая как отдельное изображение
     */
    private static final int OBJECT_CACHEDIMAGEAREA = 8;

    /**
     * размер в ячейках области, занимаемой объектом формой
     */
    private static final int DATALEN_FORM = 7;

    /**
     * размер в ячейках области, занимаемой объектом кнопкой
     */
    private static final int DATALEN_BUTTON = 10;

    /**
     * размер в ячейках области, занимаемой объектом прямоугольником
     */
    private static final int DATALEN_RECT = 6;

    /**
     * размер в ячейках области, занимаемой объектом залитым прямоугольником
     */
    private static final int DATALEN_FILLRECT = 6;

    /**
     * размер в ячейках области, занимаемой объектом настраевоемой зоной
     */
    private static final int DATALEN_CUSTOMAREA = 6;

    /**
     * размер в ячейках области, занимаемой объектом изображением
     */
    private static final int DATALEN_IMAGE = 4;

    /**
     * размер в ячейках области, занимаемой объектом текстовой меткой
     */
    private static final int DATALEN_TEXTLABEL = 5;

    /**
     * размер в ячейках области, занимаемой объектом кешируемой картинкой
     */
    private static final int DATALEN_CACHEDIMAGE = 3;

    /**
     * размер в ячейках области, занимаемой объектом "область кешируемой картинки"
     */
    private static final int DATALEN_CACHEDIMAGEAREA = 9;

    /**
     * Чтение строки из потока, ограниченной символом '\r'
     * @param _inStream поток, из которого осуществляется чтение строки
     * @return считанную строку или null если конец потока
     * @throws Throwable порождается в случае проблемы при чтении
     */
    private static final String readString(InputStream _inStream) throws Throwable
    {
        final StringBuffer p_strBuf = p_StringBuffer;
        p_strBuf.setLength(0);
        boolean lg_endOfFile = false;
        boolean lg_worked = true;
        while (lg_worked)
        {
            int i_char = _inStream.read();
            switch (i_char)
            {
                case -1:
                {
                    lg_worked = false;
                    lg_endOfFile = true;
                }
                    ;
                    break;
                case '\r':
                    continue;
                case '\n':
                {
                    lg_worked = false;
                }
                    ;
                    break;
                default:
                {
                    p_strBuf.append((char) i_char);
                }
            }
        }

        String s_str = p_strBuf.toString();
        if (s_str.length() == 0 && lg_endOfFile) return null;
        return s_str;
    }

    /**
     * Переводит строку в значение соответствующее цвету, может воспринимать HEX значения если указан в начале символ "#"
     * @param _string строка к конверсии
     * @return значение цвета
     * @throws Throwable
     */
    private static final int getColorFromString(String _string) throws Throwable
    {
        _string = _string.trim();
        int i_color;
        if (_string.charAt(0) == '#')
        {
            // hex color
            i_color = Integer.parseInt(_string.substring(1), 16);
        }
        else
        {
            // decimal
            i_color = Integer.parseInt(_string);
        }
        return i_color;
    }

    /**
     * Заполняет массив данными строки, разделенными символом ","
     * @param _string разбираемая строка
     * @param _array массив для заполнения данными
     * @return количество элементов, выделенных из строки
     * @throws Throwable 
     */
    private static final int parseStringToArray(String _string, String[] _array) throws Throwable
    {
        final int MODE_NORMAL = 0;
        final int MODE_STRING = 1;
        final int MODE_SPECSYMBOL = 2;

        char[] ach_array = _string.toCharArray();
        final StringBuffer p_buff = p_StringBuffer;
        int i_arrayPointer = 0;
        final int i_arrLen = ach_array.length;
        p_buff.setLength(0);
        int i_strIndex = 0;

        for (int li = 0; li < _array.length; li++)
            _array[li] = null;

        int i_curMode = MODE_NORMAL;

        while (i_arrayPointer < i_arrLen)
        {
            char ch_char = ach_array[i_arrayPointer++];
            switch (ch_char)
            {
                case '\"':
                {
                    switch (i_curMode)
                    {
                        case MODE_SPECSYMBOL:
                        {
                            p_buff.append(ch_char);
                            i_curMode = MODE_STRING;
                        }
                            ;
                            break;
                        case MODE_NORMAL:
                        {
                            i_curMode = MODE_STRING;
                        }
                            ;
                            break;
                        case MODE_STRING:
                        {
                            i_curMode = MODE_NORMAL;
                        }
                            ;
                            break;
                    }
                }
                    ;
                    break;
                case '\\':
                {
                    switch (i_curMode)
                    {
                        case MODE_NORMAL:
                        {
                            p_buff.append(ch_char);
                        }
                            ;
                            break;
                        case MODE_STRING:
                        {
                            i_curMode = MODE_SPECSYMBOL;
                        }
                            ;
                            break;
                        case MODE_SPECSYMBOL:
                        {
                            p_buff.append(ch_char);
                            i_curMode = MODE_STRING;
                        }
                            ;
                            break;
                    }
                }
                    ;
                    break;
                case ',':
                {
                    switch (i_curMode)
                    {
                        case MODE_NORMAL:
                        {
                            String s_newStr = p_buff.toString();
                            p_buff.setLength(0);
                            _array[i_strIndex++] = s_newStr;
                        }
                            ;
                            break;
                        case MODE_SPECSYMBOL:
                        case MODE_STRING:
                        {
                            p_buff.append(ch_char);
                        }
                            ;
                            break;
                    }
                }
                    ;
                    break;
                default:
                {
                    p_buff.append(ch_char);
                }
                    ;
                    break;
            }
        }

        if (p_buff.length() > 0)
        {
            String s_newStr = p_buff.toString();
            p_buff.setLength(0);
            _array[i_strIndex++] = s_newStr;
        }

        switch (i_curMode)
        {
            case MODE_STRING:
            case MODE_SPECSYMBOL:
                throw new Throwable();
        }

        return i_strIndex;
    }

    /**
     * Добавляет указатель на объект в таблицу указателей
     * @param _name имя объекта
     * @param _offset смещение объекта в массиве данных
     * @return индекс имени объекта в пуле строк
     * @throws Throwable порождается если была проблема при загрузке
     */
    private static final int addObjectPointer(String _name, int _offset) throws Throwable
    {
        if (p_PointersTable.contains(_name)) throw new Throwable(_name);
        int i_index = i_StringPoolLength;
        as_StringPool[i_StringPoolLength++] = _name;
        p_PointersTable.put(_name, new Integer(_offset));
        return i_index;
    }

    /**
     * Очистка кэша с вызовом GC
     * @param _removeAllResources флаг, если true то удаляются все ресурсы, иначе только те, которые не используются в текущей форме
     */
    public static final void clearResourceCache(boolean _removeAllResources)
    {
        synchronized (p_ResourceCache)
        {
            Object p_null = NULL;
            Hashtable p_resourcesOfCurrent = null;
            if (!_removeAllResources)
            {
                // строим список ресурсов используемых текущей формой
                p_resourcesOfCurrent = new Hashtable(16);

                int[] ai_arr = ai_FormsDataArray;
                String[] as_arr = as_StringPool;
                int i_pointer = i_SelectedFormOffset + DATALEN_FORM;
                while (i_pointer < MAXFORMARRAYLENGTH)
                {
                    int i_tmppntr = i_pointer;
                    switch (ai_arr[i_tmppntr])
                    {
                        case OBJECT_FORM:
                        {
                            i_pointer = MAXFORMARRAYLENGTH;
                        }
                            ;
                            break;
                        case OBJECT_BUTTON:
                        {
                            i_pointer += DATALEN_BUTTON;
                            i_tmppntr += 6;
                            for (int li = 0; li < 4; li++)
                            {
                                String s_resource = as_arr[ai_arr[i_tmppntr]];
                                p_resourcesOfCurrent.put(s_resource, p_null);
                                i_tmppntr++;
                            }
                        }
                            ;
                            break;
                        case OBJECT_CACHEDIMAGE:
                        {
                            i_pointer += DATALEN_CACHEDIMAGE;
                            String s_resource = as_arr[ai_arr[i_tmppntr + 2]];
                            p_resourcesOfCurrent.put(s_resource, p_null);
                        }
                            ;
                            break;
                        case OBJECT_CACHEDIMAGEAREA:
                        {
                            i_pointer += DATALEN_CACHEDIMAGEAREA;
                            String s_resource = as_arr[ai_arr[i_tmppntr + 2]];
                            p_resourcesOfCurrent.put(s_resource, p_null);
                        }
                            ;
                            break;
                        case OBJECT_IMAGE:
                        {
                            i_pointer += DATALEN_IMAGE;
                            String s_resource = as_arr[ai_arr[i_tmppntr + 3]];
                            p_resourcesOfCurrent.put(s_resource, p_null);
                        }
                            ;
                            break;
                        case OBJECT_CUSTOMAREA:
                        {
                            i_pointer += DATALEN_CUSTOMAREA;
                        }
                            ;
                            break;
                        case OBJECT_TEXTLABEL:
                        {
                            i_pointer += DATALEN_TEXTLABEL;
                        }
                            ;
                            break;
                        case OBJECT_RECT:
                        {
                            i_pointer += DATALEN_RECT;
                        }
                            ;
                            break;
                        case OBJECT_FILLRECT:
                        {
                            i_pointer += DATALEN_FILLRECT;
                        }
                            ;
                            break;
                    }
                }
            }

            Enumeration p_enum = p_ResourceCache.keys();
            Hashtable p_cache = p_ResourceCache;
            while (p_enum.hasMoreElements())
            {
                String s_key = (String) p_enum.nextElement();

                if (!_removeAllResources)
                {
                    if (p_resourcesOfCurrent.contains(s_key)) continue;

                }
                p_cache.put(s_key, p_null);
            }
            p_enum = null;
            if (p_resourcesOfCurrent != null)
            {
                p_resourcesOfCurrent.clear();
                p_resourcesOfCurrent = null;
            }
        }

        Runtime.getRuntime().gc();
        Thread.yield();
    }

    /**
     * Добавляет путь в пул строк 
     * @param _path путь
     * @return индекс строки в пуле строк
     */
    private static final int addCachePath(String _path)
    {
        if (_path == null || _path.length() == 0) return -1;

        int i_index = 0;
        if (p_ResourceCache.contains(_path))
        {
            i_index = ((Integer) p_ResourceCache.get(_path)).intValue();
        }
        else
        {
            i_index = i_StringPoolLength;

            as_StringPool[i_StringPoolLength++] = _path;
            p_ResourceCache.put(_path, new Integer(i_index));
        }
        return i_index;
    }

    /**
     * Загрузка изображения в кэш
     * @param _nameIndex индекс имени в пуле изображений
     * @throws Throwable порождается если была проблема при загрузке
     */
    private static final void loadImageToCache(int _nameIndex) throws Throwable
    {
        if (_nameIndex < 0) return;

        String s_resourcename = as_StringPool[_nameIndex];

        //System.out.println("Loading of resource "+s_resourcename);

        if (s_resourcename.charAt(0) == '@') return;

        if (!p_ResourceCache.get(s_resourcename).equals(NULL)) return;
        String s_imgname = s_resourcename;
        if (s_imgname.endsWith(".")) s_imgname += IMAGE_DEFAULTEXTENSION;
        Image p_image = Image.createImage(s_imgname);
        if (p_image == null) throw new Throwable(s_imgname);
        p_ResourceCache.put(s_resourcename, p_image);
    }

    /**
     * Отрисовка кэшируемого изображения 
     * @param _g графическое устройство
     * @param _name имя кэшируемой картинки
     * @param _x координата X
     * @param _y координата Y
     */
    public static final void drawImageFromCache(Graphics _g, String _name, int _x, int _y)
    {
        int i_pointer = ((Integer) p_PointersTable.get(_name)).intValue();
        String s_pathName = as_StringPool[ai_FormsDataArray[i_pointer + 2]];
        Image p_image = (Image) p_ResourceCache.get(s_pathName);
        _g.drawImage(p_image, _x, _y, 0);
    }

    /**
     * Отрисовка участка кэшируемого изображения 
     * @param _g графическое устройство
     * @param _name имя области
     * @param _x координата X
     * @param _y координата Y
     * @param _onlyIfVisibled флаг, показывающий что требуется отрисовывать зону только если она попадает в область отрисовки
     */
    public static final void drawAreaOfImageFromCache(Graphics _g, String _name, int _x, int _y, boolean _onlyIfVisibled)
    {
        int i_pointer = ((Integer) p_PointersTable.get(_name)).intValue() + 2;
        final int[] ai_arr = ai_FormsDataArray;
        String s_pathName = as_StringPool[ai_arr[i_pointer++]];

        int i_x = ai_arr[i_pointer++];
        int i_y = ai_arr[i_pointer++];
        int i_w = ai_arr[i_pointer++];
        int i_h = ai_arr[i_pointer++];
        _x += ai_arr[i_pointer++];
        _y += ai_arr[i_pointer];

        if (_onlyIfVisibled)
        {
            int i_rx = _g.getClipX();
            int i_ry = _g.getClipY();
            int i_rx2 = i_rx + _g.getClipWidth();
            int i_ry2 = i_ry + _g.getClipHeight();

            if (_x >= i_rx2 || _y >= i_ry2 || i_rx >= i_x + i_w || i_ry >= i_y + i_h) return;
        }

        Image p_image = (Image) p_ResourceCache.get(s_pathName);

        //#-
        System.out.println("zone arr " + i_x + " " + i_y + " " + i_w + " " + i_h + " xy " + _x + " " + _y);
        //#+

        _g.setClip(_x, _y, i_w, i_h);
        _g.drawImage(p_image, _x - i_x, _y - i_y, 0);
    }

    /**
     * Функция, позволяющая узнать имеются ли кнопки на форме.
     * @return true если присутствуют и false если не присутствуют
     */
    public static final boolean hasButtons()
    {
        return i_SelectedFormButtonsNumber > 0;
    }

    /**
     * Возвращает упакованные значения границ компонента
     * @param _compo имя компонента
     * @return (short)x<<48 | (short)y<<32 | (short) width<<16 | (short) height
     */
    public static final long getBoundsOfComponent(String _compo)
    {
        //System.out.println("Get bounds for "+_compo);
    	int i_pointer = ((Integer) p_PointersTable.get(_compo)).intValue();
        int[] ai_arr = ai_FormsDataArray;
        int i_x = 0;
        int i_y = 0;
        int i_w = 0;
        int i_h = 0;
        switch (ai_arr[i_pointer++])
        {
            case OBJECT_FORM:
            {
                i_pointer++;
                i_w = ai_arr[i_pointer++];
                i_h = ai_arr[i_pointer];
            }
                ;
                break;
            case OBJECT_BUTTON:
            case OBJECT_CUSTOMAREA:
            {
                i_pointer++;
                i_x = ai_arr[i_pointer++];
                i_y = ai_arr[i_pointer++];
                i_w = ai_arr[i_pointer++];
                i_h = ai_arr[i_pointer];
            }
                ;
                break;
            case OBJECT_CACHEDIMAGEAREA:
            {
                i_pointer += 2;
                i_x = ai_arr[i_pointer++];
                i_y = ai_arr[i_pointer++];
                i_w = ai_arr[i_pointer++];
                i_h = ai_arr[i_pointer++];
                int i_xoff = ai_arr[i_pointer++];
                int i_yoff = ai_arr[i_pointer];
                i_x -= i_xoff;
                i_y -= i_yoff;
                i_w += i_xoff;
                i_h += i_yoff;
            }
                ;
                break;
        }

        long l_acc = ((long) ((short) i_h)) & 0xFFFFl;
        l_acc |= ((((long) ((short) i_w)) & 0xFFFFl) << 16);
        l_acc |= ((((long) ((short) i_y)) & 0xFFFFl) << 32);
        l_acc |= ((((long) ((short) i_x)) & 0xFFFFl) << 48);

        return l_acc;
    }

    /**
     * Возвращает имя выбранной кнопки 
     * @return имя выбранной кнопки как строка
     */
    public static final String getNameOfSelectedButton()
    {
        if (i_SelectedButtonIndex < 0) return null;
        return as_StringPool[ai_FormsDataArray[ai_FormButtonsList[i_SelectedButtonIndex] + 1]];
    }

    /**
     * Перерисовывает область компонента заданного именем
     * @param _g контекст графического устройства
     * @param _name имя перерисовываемого компонента
     * @param _x смещение формы по X
     * @param _y смещение формы по Y
     * @throws Throwable порождается в случае исключения возникшего при обработке
     */
    public static final void redrawComponent(Graphics _g, String _name, int _x, int _y) throws Throwable
    {
        int i_pointer = ((Integer) p_PointersTable.get(_name)).intValue();

        int i_zx = 0;
        int i_zy = 0;
        int i_zw = 0;
        int i_zh = 0;
        int i_zx2 = 0;
        int i_zy2 = 0;

        int[] ai_arr = ai_FormsDataArray;

        switch (ai_arr[i_pointer++])
        {
            case OBJECT_FORM:
            {
                drawCurrentForm(_g, _x, _y);
            }
                ;
                break;
            case OBJECT_CACHEDIMAGE:
            case OBJECT_CACHEDIMAGEAREA:
                throw new Throwable(_name);
            case OBJECT_BUTTON:
            case OBJECT_CUSTOMAREA:
            {
                i_pointer++;
                i_zx = ai_arr[i_pointer++];
                i_zy = ai_arr[i_pointer++];
                i_zw = ai_arr[i_pointer++];
                i_zh = ai_arr[i_pointer++];
                i_zx2 = i_zx + i_zw;
                i_zy2 = i_zy + i_zh;
            }
                ;
                break;
        }

        _g.translate(_x, _y);
        _g.setClip(i_zx, i_zy, i_zw, i_zh);
        i_pointer = i_SelectedFormOffset;

        i_pointer += DATALEN_FORM;

        int i_tmppointer = i_pointer + 4;
        int i_opacity = ai_arr[i_tmppointer];
        if (i_opacity != 0)
        {
            _g.setColor(i_SelectedFormColor);
            _g.fillRect(i_zx, i_zy, i_zw, i_zh);
        }

        int i_buttonIndex = 0;

        while (i_pointer < MAXFORMARRAYLENGTH)
        {
            i_tmppointer = i_pointer + 1;
            switch (ai_arr[i_pointer])
            {
                case OBJECT_FORM:
                {
                    i_pointer = MAXFORMARRAYLENGTH;
                }
                    ;
                    break;
                case OBJECT_BUTTON:
                {
                    i_pointer += DATALEN_BUTTON;

                    String s_name = as_StringPool[ai_arr[i_tmppointer++]];
                    int i_x = ai_arr[i_tmppointer++];
                    int i_y = ai_arr[i_tmppointer++];
                    int i_w = ai_arr[i_tmppointer++];
                    int i_h = ai_arr[i_tmppointer++];

                    int i_x2 = i_x + i_w;
                    int i_y2 = i_y + i_h;

                    if (!(i_x > i_zx2 || i_y > i_zy2 || i_zx > i_x2 || i_zy > i_y2))
                    {
                        boolean lg_selected = i_buttonIndex == i_SelectedButtonIndex;

                        int i_buttonState = startup.getButtonState(s_name, lg_selected);
                        int i_destImage = ai_arr[i_tmppointer + i_buttonState];

                        String s_resName = as_StringPool[i_destImage];

                        if (s_resName.charAt(0) == '@')
                        {
                            // ссылка на зону из кэшируемой картинки
                            s_resName = s_resName.substring(1);
                            drawAreaOfImageFromCache(_g, s_resName, i_x, i_y, true);
                            _g.setClip(0, 0, i_w, i_h);
                        }
                        else
                        {
                            Image p_img = (Image) p_ResourceCache.get(s_resName);
                            _g.drawImage(p_img, i_x, i_y, 0);
                        }

                        if (lg_selected)
                        {
                            // отрисовка курсора
                            startup.drawCursor(_g, i_x, i_y, i_w, i_h);
                        }
                    }
                    i_buttonIndex++;
                }
                    ;
                    break;
                case OBJECT_CUSTOMAREA:
                {
                    i_pointer += DATALEN_CUSTOMAREA;

                    String s_areaName = as_StringPool[ai_arr[i_tmppointer++]];
                    final int i_x = ai_arr[i_tmppointer++];
                    final int i_y = ai_arr[i_tmppointer++];
                    final int i_w = ai_arr[i_tmppointer++];
                    final int i_h = ai_arr[i_tmppointer++];

                    final int i_x2 = i_x + i_w;
                    final int i_y2 = i_y + i_h;

                    // проверка на пересеченность
                    if (!(i_x > i_zx2 || i_y > i_zy2 || i_zx > i_x2 || i_zy > i_y2))
                    {
                        startup.drawCustomArea(_g, s_areaName, i_x, i_y, i_w, i_h);
                        _g.setClip(i_zx, i_zy, i_zw, i_zh);
                    }
                }
                    ;
                    break;
                case OBJECT_FILLRECT:
                {
                    i_pointer += DATALEN_FILLRECT;

                    final int i_x = ai_arr[i_tmppointer++];
                    final int i_y = ai_arr[i_tmppointer++];
                    final int i_w = ai_arr[i_tmppointer++];
                    final int i_h = ai_arr[i_tmppointer++];
                    final int i_c = ai_arr[i_tmppointer++];

                    final int i_x2 = i_x + i_w;
                    final int i_y2 = i_y + i_h;

                    // проверка на пересеченность
                    if (!(i_x > i_zx2 || i_y > i_zy2 || i_zx > i_x2 || i_zy > i_y2))
                    {
                        _g.setColor(i_c);
                        _g.fillRect(i_x, i_y, i_w, i_h);
                    }

                }
                    ;
                    break;
                case OBJECT_CACHEDIMAGE:
                {
                    i_pointer += DATALEN_CACHEDIMAGE;
                }
                    ;
                    break;
                case OBJECT_CACHEDIMAGEAREA:
                {
                    i_pointer += DATALEN_CACHEDIMAGEAREA;
                }
                    ;
                    break;
                case OBJECT_TEXTLABEL:
                {
                    i_pointer += DATALEN_TEXTLABEL;

                    final int i_x = ai_arr[i_tmppointer++];
                    final int i_y = ai_arr[i_tmppointer++];
                    final int i_c = ai_arr[i_tmppointer++];

                    if (i_x <= i_zx2 && i_y <= i_zy2)
                    {
                        String s_string = as_StringPool[ai_arr[i_tmppointer++]];
                        _g.setColor(i_c);
                        _g.drawString(s_string, i_x, i_y,Graphics.TOP|Graphics.LEFT);
                    }
                }
                    ;
                    break;
                case OBJECT_RECT:
                {
                    i_pointer += DATALEN_RECT;

                    final int i_x = ai_arr[i_tmppointer++];
                    final int i_y = ai_arr[i_tmppointer++];
                    final int i_w = ai_arr[i_tmppointer++];
                    final int i_h = ai_arr[i_tmppointer++];
                    final int i_c = ai_arr[i_tmppointer++];

                    final int i_x2 = i_x + i_w;
                    final int i_y2 = i_y + i_h;

                    // проверка на пересеченность
                    if (!(i_x > i_zx2 || i_y > i_zy2 || i_zx > i_x2 || i_zy > i_y2))
                    {
                        _g.setColor(i_c);
                        _g.drawRect(i_x, i_y, i_w, i_h);
                    }
                }
                    ;
                    break;
                case OBJECT_IMAGE:
                {
                    i_pointer += DATALEN_IMAGE;

                    final int i_x = ai_arr[i_tmppointer++];
                    final int i_y = ai_arr[i_tmppointer++];
                    String s_resName = as_StringPool[ai_arr[i_tmppointer++]];

                    if (s_resName.charAt(0) == '@')
                    {
                        // ссылка на зону из кэшируемой картинки
                        s_resName = s_resName.substring(1);

                        drawAreaOfImageFromCache(_g, s_resName, i_x, i_y, true);
                        _g.setClip(i_zx, i_zy, i_zw, i_zh);
                    }
                    else
                    {
                        final Image p_img = (Image) p_ResourceCache.get(s_resName);

                        final int i_w = p_img.getWidth();
                        final int i_h = p_img.getHeight();

                        final int i_x2 = i_x + i_w;
                        final int i_y2 = i_y + i_h;

                        if (!(i_x > i_zx2 || i_y > i_zy2 || i_zx > i_x2 || i_zy > i_y2))
                        {
                            _g.drawImage(p_img, i_x, i_y, 0);
                        }
                    }
                }
                    ;
                    break;
            }
        }

        _g.translate(-_x, -_y);
    }

    /**
     * Отрисовка текущей формы на графическом устройстве в выбранных координатах
     * @param _g графическое устройство
     * @param _x координата X
     * @param _y координата Y
     */
    public static final void drawCurrentForm(Graphics _g, int _x, int _y)
    {
        boolean lg_drawLabels = p_SystemFont != null;
        if (lg_drawLabels)
        {
            _g.setFont(p_SystemFont);
        }

        _g.translate(_x, _y);

        final int[] ai_formdata = ai_FormsDataArray;
        int i_offset = i_SelectedFormOffset;

        i_offset += 2;// пропускаем тип и имя
        final int i_formw = ai_formdata[i_offset++];
        final int i_formh = ai_formdata[i_offset++];
        int i_c = ai_formdata[i_offset++];
        int i_opaq = ai_formdata[i_offset++];
        // пропускаем анимационную задержку
        i_offset++;

        _g.setClip(0, 0, i_formw, i_formh);
        if (i_opaq == 255)
        {
            _g.setColor(i_SelectedFormColor);
            _g.fillRect(0, 0, i_formw, i_formh);
        }

        int i_buttonIndex = 0;

        while (i_offset < MAXFORMARRAYLENGTH)
        {
            int i_tmpoffset = i_offset;
            int i_type = ai_formdata[i_tmpoffset++];
            switch (i_type)
            {
                case OBJECT_FORM:
                {
                    // достигли следующей формы, заканчиваем отрисовку
                    i_offset = MAXFORMARRAYLENGTH;
                }
                    ;
                    break;
                case OBJECT_IMAGE:
                {
                    i_offset += DATALEN_IMAGE;
                    final int i_x = ai_formdata[i_tmpoffset++];
                    final int i_y = ai_formdata[i_tmpoffset++];
                    String s_resName = as_StringPool[ai_formdata[i_tmpoffset++]];

                    if (s_resName.charAt(0) == '@')
                    {
                        // ссылка на зону из кэшируемой картинки
                        s_resName = s_resName.substring(1);
                        drawAreaOfImageFromCache(_g, s_resName, i_x, i_y, false);
                        _g.setClip(0, 0, i_formw, i_formh);
                    }
                    else
                    {
                        _g.drawImage((Image) p_ResourceCache.get(s_resName), i_x, i_y, 0);
                    }
                }
                    ;
                    break;
                case OBJECT_BUTTON:
                {
                    i_offset += DATALEN_BUTTON;
                    String s_name = as_StringPool[ai_formdata[i_tmpoffset++]];
                    int i_x = ai_formdata[i_tmpoffset++];
                    int i_y = ai_formdata[i_tmpoffset++];
                    int i_w = ai_formdata[i_tmpoffset++];
                    int i_h = ai_formdata[i_tmpoffset++];

                    boolean lg_selected = i_buttonIndex == i_SelectedButtonIndex;

                    int i_buttonState = startup.getButtonState(s_name, lg_selected);

                    int i_destImage = ai_formdata[i_tmpoffset + i_buttonState];
                    String s_resName = as_StringPool[i_destImage];

                    if (s_resName.charAt(0) == '@')
                    {
                        // ссылка на зону из кэшируемой картинки
                        s_resName = s_resName.substring(1);
                        drawAreaOfImageFromCache(_g, s_resName, i_x, i_y, false);
                        _g.setClip(0, 0, i_w, i_h);
                    }
                    else
                    {
                        Image p_img = (Image) p_ResourceCache.get(s_resName);
                        _g.drawImage(p_img, i_x, i_y, 0);
                    }

                    if (lg_selected)
                    {
                        // отрисовка курсора
                        startup.drawCursor(_g, i_x, i_y, i_w, i_h);
                    }

                    i_buttonIndex++;
                }
                    ;
                    break;
                case OBJECT_CACHEDIMAGE:
                {
                    i_offset += DATALEN_CACHEDIMAGE;
                }
                    ;
                    break;
                case OBJECT_CACHEDIMAGEAREA:
                {
                    i_offset += DATALEN_CACHEDIMAGEAREA;
                }
                    ;
                    break;
                case OBJECT_CUSTOMAREA:
                {
                    i_offset += DATALEN_CUSTOMAREA;
                    String s_areaName = as_StringPool[ai_formdata[i_tmpoffset++]];
                    int i_x = ai_formdata[i_tmpoffset++];
                    int i_y = ai_formdata[i_tmpoffset++];
                    int i_w = ai_formdata[i_tmpoffset++];
                    int i_h = ai_formdata[i_tmpoffset++];
                    startup.drawCustomArea(_g, s_areaName, i_x, i_y, i_w, i_h);
                    _g.setClip(0, 0, i_formw, i_formh);
                }
                    ;
                    break;
                case OBJECT_FILLRECT:
                {
                    i_offset += DATALEN_FILLRECT;

                    int i_x = ai_formdata[i_tmpoffset++];
                    int i_y = ai_formdata[i_tmpoffset++];
                    int i_w = ai_formdata[i_tmpoffset++];
                    int i_h = ai_formdata[i_tmpoffset++];
                    i_c = ai_formdata[i_tmpoffset++];
                    _g.setColor(i_c);
                    _g.fillRect(i_x, i_y, i_w, i_h);
                }
                    ;
                    break;
                case OBJECT_RECT:
                {
                    i_offset += DATALEN_RECT;

                    int i_x = ai_formdata[i_tmpoffset++];
                    int i_y = ai_formdata[i_tmpoffset++];
                    int i_w = ai_formdata[i_tmpoffset++];
                    int i_h = ai_formdata[i_tmpoffset++];
                    i_c = ai_formdata[i_tmpoffset++];
                    _g.setColor(i_c);
                    _g.drawRect(i_x, i_y, i_w, i_h);
                }
                    ;
                    break;
                case OBJECT_TEXTLABEL:
                {
                    i_offset += DATALEN_TEXTLABEL;

                    if (lg_drawLabels)
                    {
                        int i_x = ai_formdata[i_tmpoffset++];
                        int i_y = ai_formdata[i_tmpoffset++];
                        i_c = ai_formdata[i_tmpoffset++];
                        String s_string = as_StringPool[ai_formdata[i_tmpoffset++]];
                        _g.setColor(i_c);
                        _g.drawString(s_string, i_x, i_y, Graphics.TOP | Graphics.LEFT);
                    }
                }
                    ;
                    break;
            }
        }

        _g.translate(-_x, -_y);
    }

    /**
     * Переводит курсор на следующую незапрещенную к сипользованию кнопку
     */
    public static final void selectNextButton()
    {
        int i_cur = i_SelectedButtonIndex;

        final int[] ai_buttonList = ai_FormButtonsList;
        final int[] ai_arr = ai_FormsDataArray;
        final String[] as_arr = as_StringPool;

        while (true)
        {
            i_cur++;
            if (i_cur >= i_SelectedFormButtonsNumber) i_cur = 0;
            if (i_cur == i_SelectedButtonIndex) break;
            int i_buttonOffset = ai_buttonList[i_cur];
            String s_name = as_arr[ai_arr[i_buttonOffset + 1]];

            if (startup.getButtonState(s_name, false) != BUTTONSTATE_DISABLED)
            {
                i_SelectedButtonIndex = i_cur;
                break;
            }
        }
    }

    /**
     * Переводит курсор на предыдущую незапрещенную к сипользованию кнопку
     */
    public static final void selectPrevButton()
    {
        int i_cur = i_SelectedButtonIndex;

        final int[] ai_buttonList = ai_FormButtonsList;
        final int[] ai_arr = ai_FormsDataArray;
        final String[] as_arr = as_StringPool;

        while (true)
        {
            i_cur--;
            if (i_cur < 0) i_cur = i_SelectedFormButtonsNumber - 1;

            if (i_cur == i_SelectedButtonIndex) break;

            int i_buttonOffset = ai_buttonList[i_cur];
            String s_name = as_arr[ai_arr[i_buttonOffset + 1]];
            if (startup.getButtonState(s_name, false) != BUTTONSTATE_DISABLED)
            {
                i_SelectedButtonIndex = i_cur;
                break;
            }
        }
    }

    /**
     * Фокусирует курсор на кнопку с заданным именем
     * @param _name имя кнопки
     */
    public static final void focusToButton(String _name)
    {
        if (_name == null) i_SelectedButtonIndex = 0;
        for (int li = 0; li < i_SelectedFormButtonsNumber; li++)
        {
            int i_offset = ai_FormButtonsList[li];
            String s_str = as_StringPool[ai_FormsDataArray[i_offset + 1]];
            if (_name == s_str)
            {
                i_SelectedButtonIndex = li;
                break;
            }
        }
    }

    /**
     * Выбор рабочей формы, 
     * @param _name имя формы
     * @param _clearCache флаг предварительной очистки кэша ресурсов
     * @throws Throwable
     */
    public static final void selectForm(String _name, boolean _clearCache) throws Throwable
    {
        int i_formOffset = ((Integer) p_PointersTable.get(_name)).intValue();

        i_SelectedFormOffset = i_formOffset;
        if (_clearCache) clearResourceCache(false);

        final int[] ai_formarray = ai_FormsDataArray;
        s_SelectedFormName = as_StringPool[ai_formarray[i_formOffset + 1]];
        i_SelectedFormColor = ai_formarray[i_formOffset + 4];
        i_SelectedFormAnimationDelay = ai_formarray[i_formOffset + 6];

        // производим загрузку данных формы в кэш
        i_formOffset += DATALEN_FORM; // смещаем указатель на первый элемент

        i_SelectedFormButtonsNumber = 0;

        int i_selectedButtonIndex = -1;

        while (i_formOffset < MAXFORMARRAYLENGTH)
        {
            int i_type = ai_formarray[i_formOffset];
            int i_tmpoffst = i_formOffset + 1;

            switch (i_type)
            {
                case OBJECT_FORM:
                {
                    // достигли конца формы, выходим
                    i_formOffset = MAXFORMARRAYLENGTH;
                }
                    ;
                    break;
                case OBJECT_CUSTOMAREA:
                {
                    i_formOffset += DATALEN_CUSTOMAREA;
                }
                    ;
                    break;
                case OBJECT_RECT:
                case OBJECT_FILLRECT:
                {
                    i_formOffset += DATALEN_RECT;
                }
                    ;
                    break;
                case OBJECT_TEXTLABEL:
                {
                    i_formOffset += DATALEN_TEXTLABEL;
                }
                    ;
                    break;
                case OBJECT_IMAGE:
                {
                    i_formOffset += DATALEN_IMAGE;
                    // загружаем изображение
                    loadImageToCache(ai_formarray[i_tmpoffst + 2]);
                }
                    ;
                    break;
                case OBJECT_CACHEDIMAGE:
                {
                    i_formOffset += DATALEN_CACHEDIMAGE;

                    // загружаем изображение
                    loadImageToCache(ai_formarray[i_tmpoffst + 1]);
                }
                    ;
                    break;
                case OBJECT_CACHEDIMAGEAREA:
                {
                    i_formOffset += DATALEN_CACHEDIMAGEAREA;

                    // загружаем изображение
                    loadImageToCache(ai_formarray[i_tmpoffst + 1]);
                }
                    ;
                    break;
                case OBJECT_BUTTON:
                {
                    // загружаем изображения
                    String s_str = as_StringPool[ai_formarray[i_tmpoffst]];
                    i_tmpoffst += 5;

                    loadImageToCache(ai_formarray[i_tmpoffst++]);
                    loadImageToCache(ai_formarray[i_tmpoffst++]);
                    loadImageToCache(ai_formarray[i_tmpoffst++]);
                    loadImageToCache(ai_formarray[i_tmpoffst++]);

                    // проверяем разрешенная ли эта кнопка
                    if (i_selectedButtonIndex < 0)
                    {
                        if (startup.getButtonState(s_str, false) != BUTTONSTATE_DISABLED)
                        {
                            i_selectedButtonIndex = i_SelectedFormButtonsNumber;
                        }
                    }

                    ai_FormButtonsList[i_SelectedFormButtonsNumber++] = i_formOffset;
                    i_formOffset += DATALEN_BUTTON;
                }
                    ;
                    break;
                default:
                    throw new Throwable();
            }
        }
        i_SelectedButtonIndex = i_selectedButtonIndex;
    }

    /**
     * Инициализация менеджера
     * @param _resource идентификатор ресурса в котором находится  
     * @throws Throwable порождается в случае проблемы инициализации менеджера
     */
    public static final void init(String _resource) throws Throwable
    {
        InputStream p_inStream = startup.p_This.getClass().getResourceAsStream(_resource);

        final String[] as_array = new String[16];
        final int[] ai_formarray = ai_FormsDataArray;

        i_StringPoolLength = 0;
        p_PointersTable.clear();
        p_ResourceCache.clear();

        int i_formarraypoiner = 0;

        for (int li = 0; li < as_StringPool.length; li++)
            as_StringPool[li] = null;

        String s_string = null;
        try
        {
            while (true)
            {
                s_string = readString(p_inStream);
                if (s_string == null) break;
                s_string = s_string.trim();
                if (s_string.startsWith("//")) continue;

                int i_len = parseStringToArray(s_string, as_array);
                if (i_len == 0) continue;
                String s_firstElem = as_array[0];
                if (s_firstElem.equals("form"))
                {
                    // form,<name>,<width>,<height>,<color>,<opacity>,<animationdelay>

                    int i_nameOffset = addObjectPointer(as_array[1], i_formarraypoiner);
                    ai_formarray[i_formarraypoiner++] = OBJECT_FORM;
                    ai_formarray[i_formarraypoiner++] = i_nameOffset;
                    int i_w = Integer.parseInt(as_array[2]);
                    int i_h = Integer.parseInt(as_array[3]);
                    int i_color = getColorFromString(as_array[4]);
                    int i_opacity = Integer.parseInt(as_array[5]);
                    int i_animationdelay = Integer.parseInt(as_array[6]);

                    ai_formarray[i_formarraypoiner++] = i_w;
                    ai_formarray[i_formarraypoiner++] = i_h;
                    ai_formarray[i_formarraypoiner++] = i_color;
                    ai_formarray[i_formarraypoiner++] = i_opacity;
                    ai_formarray[i_formarraypoiner++] = i_animationdelay;
                }
                else
                    if (s_firstElem.equals("button"))
                    {
                        // button,<name>,<x>,<y>,<width>,<htight>,<normal_image_path>,<pressed_image_path>,<selected_image_path>,<disabled_image_path>

                        int i_namePointer = addObjectPointer(as_array[1], i_formarraypoiner);
                        ai_formarray[i_formarraypoiner++] = OBJECT_BUTTON;
                        ai_formarray[i_formarraypoiner++] = i_namePointer;

                        int i_x = Integer.parseInt(as_array[2]);
                        int i_y = Integer.parseInt(as_array[3]);
                        int i_w = Integer.parseInt(as_array[4]);
                        int i_h = Integer.parseInt(as_array[5]);

                        ai_formarray[i_formarraypoiner++] = i_x;
                        ai_formarray[i_formarraypoiner++] = i_y;
                        ai_formarray[i_formarraypoiner++] = i_w;
                        ai_formarray[i_formarraypoiner++] = i_h;

                        ai_formarray[i_formarraypoiner++] = addCachePath(as_array[6]);
                        ai_formarray[i_formarraypoiner++] = addCachePath(as_array[7]);
                        ai_formarray[i_formarraypoiner++] = addCachePath(as_array[8]);
                        ai_formarray[i_formarraypoiner++] = addCachePath(as_array[9]);
                    }
                    else
                        if (s_firstElem.equals("image"))
                        {
                            // image,<x>,<y>,<image_path>
                            int i_x = Integer.parseInt(as_array[1]);
                            int i_y = Integer.parseInt(as_array[2]);

                            ai_formarray[i_formarraypoiner++] = OBJECT_IMAGE;

                            ai_formarray[i_formarraypoiner++] = i_x;
                            ai_formarray[i_formarraypoiner++] = i_y;
                            ai_formarray[i_formarraypoiner++] = addCachePath(as_array[3]);
                        }
                        else
                            if (s_firstElem.equals("cimage"))
                            {
                                // cimage,<image_name>,<image_path>
                                int i_namePointer = addObjectPointer(as_array[1], i_formarraypoiner);
                                ai_formarray[i_formarraypoiner++] = OBJECT_CACHEDIMAGE;
                                ai_formarray[i_formarraypoiner++] = i_namePointer;
                                ai_formarray[i_formarraypoiner++] = addCachePath(as_array[2]);
                            }
                            else
                                if (s_firstElem.equals("imgarea"))
                                {
                                    // imgarea,<areaname>,<image_name>,<x>,<y>,<width>,<height>,<xoffset>,<yoffset>
                                    int i_namePointer = addObjectPointer(as_array[1], i_formarraypoiner);
                                    ai_formarray[i_formarraypoiner++] = OBJECT_CACHEDIMAGEAREA;
                                    ai_formarray[i_formarraypoiner++] = i_namePointer;

                                    // берем ссылку на изображение и извлекаем оттуда ссылку на путь
                                    ai_formarray[i_formarraypoiner++] = addCachePath(as_array[2]);

                                    int i_x = Integer.parseInt(as_array[3]);
                                    int i_y = Integer.parseInt(as_array[4]);
                                    int i_w = Integer.parseInt(as_array[5]);
                                    int i_h = Integer.parseInt(as_array[6]);
                                    int i_xoff = Integer.parseInt(as_array[7]);
                                    int i_yoff = Integer.parseInt(as_array[8]);

                                    ai_formarray[i_formarraypoiner++] = i_x;
                                    ai_formarray[i_formarraypoiner++] = i_y;
                                    ai_formarray[i_formarraypoiner++] = i_w;
                                    ai_formarray[i_formarraypoiner++] = i_h;
                                    ai_formarray[i_formarraypoiner++] = i_xoff;
                                    ai_formarray[i_formarraypoiner++] = i_yoff;
                                }
                                else
                                    if (s_firstElem.equals("label"))
                                    {
                                        // label,<x>,<y>,<color>,"text"

                                        int i_x = Integer.parseInt(as_array[1]);
                                        int i_y = Integer.parseInt(as_array[2]);
                                        int i_c = getColorFromString(as_array[3]);

                                        ai_formarray[i_formarraypoiner++] = OBJECT_TEXTLABEL;

                                        ai_formarray[i_formarraypoiner++] = i_x;
                                        ai_formarray[i_formarraypoiner++] = i_y;
                                        ai_formarray[i_formarraypoiner++] = i_c;

                                        ai_formarray[i_formarraypoiner++] = i_StringPoolLength;
                                        as_StringPool[i_StringPoolLength++] = as_array[4];
                                    }
                                    else
                                        if (s_firstElem.equals("frect"))
                                        {
                                            // frect,<x>,<y>,<width>,<height>,<color>
                                            ai_formarray[i_formarraypoiner++] = OBJECT_FILLRECT;

                                            int i_x = Integer.parseInt(as_array[1]);
                                            int i_y = Integer.parseInt(as_array[2]);
                                            int i_w = Integer.parseInt(as_array[3]);
                                            int i_h = Integer.parseInt(as_array[4]);
                                            int i_c = getColorFromString(as_array[5]);

                                            ai_formarray[i_formarraypoiner++] = i_x;
                                            ai_formarray[i_formarraypoiner++] = i_y;
                                            ai_formarray[i_formarraypoiner++] = i_w;
                                            ai_formarray[i_formarraypoiner++] = i_h;
                                            ai_formarray[i_formarraypoiner++] = i_c;
                                        }
                                        else
                                            if (s_firstElem.equals("rect"))
                                            {
                                                // rect,<x>,<y>,<width>,<height>,<color>
                                                ai_formarray[i_formarraypoiner++] = OBJECT_RECT;

                                                int i_x = Integer.parseInt(as_array[1]);
                                                int i_y = Integer.parseInt(as_array[2]);
                                                int i_w = Integer.parseInt(as_array[3]);
                                                int i_h = Integer.parseInt(as_array[4]);
                                                int i_c = getColorFromString(as_array[5]);

                                                ai_formarray[i_formarraypoiner++] = i_x;
                                                ai_formarray[i_formarraypoiner++] = i_y;
                                                ai_formarray[i_formarraypoiner++] = i_w;
                                                ai_formarray[i_formarraypoiner++] = i_h;
                                                ai_formarray[i_formarraypoiner++] = i_c;
                                            }
                                            else
                                                if (s_firstElem.equals("carea"))
                                                {
                                                    // carea,<name>,<x>,<y>,<width>,<height>

                                                    int i_nameIndex = addObjectPointer(as_array[1], i_formarraypoiner);

                                                    ai_formarray[i_formarraypoiner++] = OBJECT_CUSTOMAREA;

                                                    int i_x = Integer.parseInt(as_array[2]);
                                                    int i_y = Integer.parseInt(as_array[3]);
                                                    int i_w = Integer.parseInt(as_array[4]);
                                                    int i_h = Integer.parseInt(as_array[5]);

                                                    ai_formarray[i_formarraypoiner++] = i_nameIndex;
                                                    ai_formarray[i_formarraypoiner++] = i_x;
                                                    ai_formarray[i_formarraypoiner++] = i_y;
                                                    ai_formarray[i_formarraypoiner++] = i_w;
                                                    ai_formarray[i_formarraypoiner++] = i_h;
                                                }
            }
        }
        catch (Throwable _thr)
        {
            throw new Throwable("ErrStr: " + s_string);
        }
        finally
        {
            clearResourceCache(true);
            p_inStream = null;
        }
    }
}
