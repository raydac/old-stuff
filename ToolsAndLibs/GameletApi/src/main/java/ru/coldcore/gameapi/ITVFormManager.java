package ru.coldcore.gameapi;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Image;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.Hashtable;;

public class ITVFormManager
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
    private static final String IMAGE_EXTENSION = ".png";
    
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
     * Размер пула строк
     */
    private static int i_StringPoolLength;

    /**
     * Массив, содержащий пул строк
     */
    private static final String [] as_StringPool = new String[MAXTEXTRESOURCES];

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
    private static final int [] ai_FormsDataArray = new int[MAXFORMARRAYLENGTH];

    /**
     * Список смещений текущих кнопок на форме
     */
    private static final int [] ai_FormButtonsList = new int[MAXBUTTONSONFORM];

    /**
     * Смещение текущей выбранной формы
     */
    private static int i_SelectedFormOffset;

    /**
     * Цвет текущей формы
     */
    private static Color p_SelectedFormColor;
    
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
     * Метрика системного фонта
     */
    public static FontMetrics p_SystemFontMetrics;
    
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
     * размер в ячейках области, занимаемой объектом формой
     */
    private static final int DATALEN_FORM = 6;

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
    private static final int DATALEN_CACHEDIMAGE = 2;
    
    
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
                    p_strBuf.append((char)i_char);
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
        if (_string.startsWith("#"))
        {
            // hex color
            i_color = Integer.parseInt(_string.substring(1),16);
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
    private static final int parseStringToArray(String _string,String[] _array) throws Throwable 
    {
        final int MODE_NORMAL = 0;
        final int MODE_STRING = 1;
        final int MODE_SPECSYMBOL = 2;
        
        char [] ach_array = _string.toCharArray();
        final StringBuffer p_buff = p_StringBuffer;
        int i_arrayPointer = 0;
        final int i_arrLen = ach_array.length;
        p_buff.setLength(0);
        int i_strIndex = 0;
        
        for(int li=0;li<_array.length;li++) _array[li] = null;
        
        int i_curMode = MODE_NORMAL;
        
        while(i_arrayPointer<i_arrLen)
        {
            char ch_char = ach_array[i_arrayPointer++];
            switch(ch_char)
            {
                case '\"':
                {
                    switch(i_curMode)
                    {
                        case MODE_SPECSYMBOL:
                        {
                            p_buff.append(ch_char);
                            i_curMode = MODE_STRING;
                        };break;
                        case MODE_NORMAL :
                        {
                            i_curMode = MODE_STRING;
                        };break;
                        case MODE_STRING :
                        {
                            i_curMode = MODE_NORMAL; 
                        };break;
                    }
                };break;
                case '\\':
                {
                    switch(i_curMode)
                    {
                        case MODE_NORMAL :
                        {
                            p_buff.append(ch_char);
                        };break;
                        case MODE_STRING :
                        {
                            i_curMode = MODE_SPECSYMBOL;
                        };break;
                        case MODE_SPECSYMBOL :
                        {
                            p_buff.append(ch_char);
                            i_curMode = MODE_STRING;
                        };break;
                    }
                };break;
                case ',' :
                {
                    switch(i_curMode)
                    {
                        case MODE_NORMAL :
                        {
                            String s_newStr = p_buff.toString();
                            p_buff.setLength(0);
                            _array[i_strIndex++] = s_newStr;
                        };break;
                        case MODE_SPECSYMBOL :
                        case MODE_STRING :
                        {
                            p_buff.append(ch_char);
                        };break;
                    }
                };break;
                default:
                {
                    p_buff.append(ch_char);
                };break;
            }
        }

        if (p_buff.length()>0)
        {
            String s_newStr = p_buff.toString();
            p_buff.setLength(0);
            _array[i_strIndex++] = s_newStr;
        }
    
        switch(i_curMode)
        {
            case MODE_STRING : 
            case MODE_SPECSYMBOL : throw new Throwable(); 
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
    private static final int addObjectPointer(String _name,int _offset) throws Throwable
    {
        if (p_PointersTable.contains(_name)) throw new Throwable(_name);
        int i_index = i_StringPoolLength;
        as_StringPool[i_StringPoolLength++] = _name; 
        p_PointersTable.put(_name,new Integer(_offset));
        return i_index;
    }

    /**
     * Очистка кэша с вызовом GC
     */
    public static final void clearResourceCache()
    {
        synchronized (p_ResourceCache)
        {
            Enumeration p_enum = p_ResourceCache.keys();
            while(p_enum.hasMoreElements())
            {
                String s_key = (String) p_enum.nextElement();
                p_ResourceCache.put(s_key,NULL);
            }
        }
        Runtime.getRuntime().gc();
    }
    
    /**
     * Добавляет путь в пул строк 
     * @param _path путь
     * @return индекс строки в пуле строк
     */
    private static final int addCachePath(String _path)
    {
        if (_path==null || _path.length()==0) return -1;
        
        int i_index = 0;
        if (p_ResourceCache.contains(_path)) 
        {
            i_index = ((Integer)p_ResourceCache.get(_path)).intValue();
        }
        else
        {
            i_index = i_StringPoolLength;
            as_StringPool[i_StringPoolLength++] = _path; 
            p_ResourceCache.put(_path,new Integer(i_index));
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
        if (_nameIndex<0) return;
        String s_resourcename = as_StringPool[_nameIndex];
        if (!p_ResourceCache.get(s_resourcename).equals(NULL)) return; 
        String s_imgname = s_resourcename + IMAGE_EXTENSION;
        Image p_image = Utils.loadImageFromResource(s_imgname);
        if (p_image==null) throw new Throwable(s_imgname);
        p_ResourceCache.put(s_resourcename,p_image);
    }

    /**
     * Отрисовка кэшируемого изображения 
     * @param _g графическое устройство
     * @param _name имя картинки
     * @param _x координата X
     * @param _y координата Y
     */
    public static final void drawImageFromCache(Graphics _g,String _name,int _x,int _y)
    {
        Image p_image = (Image)p_ResourceCache.get(_name);
        _g.drawImage(p_image,_x,_y,null);
    }

    /**
     * Возвращает имя выбранной кнопки 
     * @return имя выбранной кнопки как строка
     */
    public static final String getNameOfSelectedButton()
    {
        if (i_SelectedButtonIndex<0) return null;
        return as_StringPool[ai_FormButtonsList[i_SelectedButtonIndex]];
    }
   
    /**
     * Перерисовывает область компонента заданного именем
     * @param _g контекст графического устройства
     * @param _name имя перерисовываемого компонента
     * @param _x смещение формы по X
     * @param _y смещение формы по Y
     * @throws Throwable порождается в случае исключения возникшего при обработке
     */
    public static final void redrawComponent(Graphics _g,String _name,int _x,int _y) throws Throwable
    {
        int i_pointer = ((Integer)p_PointersTable.get(_name)).intValue();
        final int i_origpointer = i_pointer; 
    
        int i_zx=0;
        int i_zy=0;
        int i_zw=0;
        int i_zh=0;
        int i_zx2=0;
        int i_zy2=0;
    
        int [] ai_arr = ai_FormsDataArray;
        
        switch(ai_arr[i_pointer++])
        {
            case OBJECT_FORM :
            {
                drawCurrentForm(_g,_x,_y);
            };break;
            case OBJECT_BUTTON :
            case OBJECT_CUSTOMAREA :
            {
                i_pointer++;
                i_zx = ai_arr[i_pointer++];
                i_zy = ai_arr[i_pointer++];
                i_zw = ai_arr[i_pointer++];
                i_zh = ai_arr[i_pointer++];
                i_zx2 = i_zx+i_zw;
                i_zy2 = i_zy+i_zh;
            };break;
        }
        
        _g.translate(_x,_y);
        
        _g.setClip(i_zx,i_zy,i_zw,i_zh);
        
        i_pointer = i_SelectedFormOffset;
        
        while(i_pointer<=i_origpointer)
        {
            int i_tmppointer = i_pointer+1;
            switch(ai_arr[i_pointer])
            {
                case OBJECT_FORM :
                {
                    i_pointer+=DATALEN_FORM;
                    int i_w = ai_arr[i_tmppointer++];
                    int i_h = ai_arr[i_tmppointer++];
                    int i_c = ai_arr[i_tmppointer++];
                    int i_opacity = ai_arr[i_tmppointer++];
                    
                    if (i_opacity!=0)
                    {
                        _g.setColor(p_SelectedFormColor);
                        _g.fillRect(i_zx,i_zy,i_zw,i_zh);
                    }
                    
                };break;
                case OBJECT_CUSTOMAREA :
                {
                    i_pointer+=DATALEN_CUSTOMAREA;

                    String s_areaName = as_StringPool[i_tmppointer++];
                    final int i_x = ai_arr[i_tmppointer++];
                    final int i_y = ai_arr[i_tmppointer++];
                    final int i_w = ai_arr[i_tmppointer++];
                    final int i_h = ai_arr[i_tmppointer++];
                    
                    final int i_x2 = i_x+i_w;
                    final int i_y2 = i_y+i_h;
                    
                    // проверка на пересеченность
                    if (!(i_x>i_zx2 || i_y>i_zy2 || i_zx>i_x2 || i_zy>i_y2))
                    {
                        Tester.drawCustomArea(_g,s_areaName,i_x,i_y,i_w,i_h);
                    }
                    
                };break;
                case OBJECT_FILLRECT :
                {
                    i_pointer+=DATALEN_FILLRECT;

                    final int i_x = ai_arr[i_tmppointer++];
                    final int i_y = ai_arr[i_tmppointer++];
                    final int i_w = ai_arr[i_tmppointer++];
                    final int i_h = ai_arr[i_tmppointer++];
                    final int i_c = ai_arr[i_tmppointer++];
                    
                    final int i_x2 = i_x+i_w;
                    final int i_y2 = i_y+i_h;
                    
                    // проверка на пересеченность
                    if (!(i_x>i_zx2 || i_y>i_zy2 || i_zx>i_x2 || i_zy>i_y2))
                    {
                        _g.setColor(new Color(i_c));
                        _g.fillRect(i_x,i_y,i_w,i_h);
                    }
                    
                };break;
                case OBJECT_CACHEDIMAGE :
                {
                    i_pointer+=DATALEN_CACHEDIMAGE;
                };break;
                case OBJECT_TEXTLABEL :
                {
                    i_pointer+=DATALEN_TEXTLABEL;

                    final int i_x = ai_arr[i_tmppointer++];
                    final int i_y = ai_arr[i_tmppointer++];
                    final int i_c = ai_arr[i_tmppointer++];

                    if (i_x<=i_zx2 && i_y<=i_zy2)
                    {
                        String s_string = as_StringPool[ai_arr[i_tmppointer++]];
                        _g.setColor(new Color(i_c));
                        _g.drawString(s_string,i_x,i_y+p_SystemFontMetrics.getMaxAscent());
                    }
                };break;
                case OBJECT_RECT :
                {
                    i_pointer+=DATALEN_RECT;
                    
                    final int i_x = ai_arr[i_tmppointer++];
                    final int i_y = ai_arr[i_tmppointer++];
                    final int i_w = ai_arr[i_tmppointer++];
                    final int i_h = ai_arr[i_tmppointer++];
                    final int i_c = ai_arr[i_tmppointer++];
                    
                    final int i_x2 = i_x+i_w;
                    final int i_y2 = i_y+i_h;
                    
                    // проверка на пересеченность
                    if (!(i_x>i_zx2 || i_y>i_zy2 || i_zx>i_x2 || i_zy>i_y2))
                    {
                        _g.setColor(new Color(i_c));
                        _g.drawRect(i_x,i_y,i_w,i_h);
                    }
                };break;
                case OBJECT_IMAGE :
                {
                    i_pointer+=DATALEN_IMAGE;

                    final int i_x = ai_arr[i_tmppointer++];
                    final int i_y = ai_arr[i_tmppointer++];
                    String s_resName = as_StringPool[ai_arr[i_tmppointer++]];
                    final Image p_img = (Image)p_ResourceCache.get(s_resName); 
                    
                    final int i_w = p_img.getWidth(null); 
                    final int i_h = p_img.getHeight(null);

                    final int i_x2 = i_x+i_w;
                    final int i_y2 = i_y+i_h;
                    
                    if (!(i_x>i_zx2 || i_y>i_zy2 || i_zx>i_x2 || i_zy>i_y2))
                    {
                        _g.drawImage(p_img,i_x,i_y,null);
                    }
                    
                };break;
            }
        }
        _g.translate(-_x,-_y);
    }
    
    /**
     * Отрисовка текущей формы на графическом устройстве в выбранных координатах
     * @param _g графическое устройство
     * @param _x координата X
     * @param _y координата Y
     */
    public static final void drawCurrentForm(Graphics _g,int _x,int _y)
    {
        boolean lg_drawLabels = p_SystemFont!=null; 
        if (lg_drawLabels)
        {
            if (p_SystemFontMetrics==null) p_SystemFontMetrics = _g.getFontMetrics(p_SystemFont);
            _g.setFont(p_SystemFont);
        }

        _g.translate(_x,_y);
        
        final int [] ai_formdata = ai_FormsDataArray;
        int i_tmpoffset = i_SelectedFormOffset;
        
        i_tmpoffset+=2;// пропускаем тип и имя
        int i_w = ai_formdata[i_tmpoffset++];
        int i_h = ai_formdata[i_tmpoffset++];
        int i_c = ai_formdata[i_tmpoffset++];
        int i_opaq = ai_formdata[i_tmpoffset++];
        
        _g.setClip(0,0,i_w,i_h);
        if (i_opaq==255)
        {
            _g.setColor(p_SelectedFormColor);
            _g.fillRect(0,0,i_w,i_h);
        }

        int i_buttonIndex = 0;
        
        while(i_tmpoffset<MAXFORMARRAYLENGTH)
        {
            int i_type = ai_formdata[i_tmpoffset++];
            switch(i_type)
            {
                case OBJECT_FORM :
                {
                    // достигли следующей формы, заканчиваем отрисовку
                    i_tmpoffset = MAXFORMARRAYLENGTH;
                };break;
                case OBJECT_IMAGE:
                {
                    int i_x = ai_formdata[i_tmpoffset++];
                    int i_y = ai_formdata[i_tmpoffset++];
                    String s_resName = as_StringPool[ai_formdata[i_tmpoffset++]];
                    _g.drawImage((Image)p_ResourceCache.get(s_resName),i_x,i_y,null);
                };break;
                case OBJECT_BUTTON :
                {
                    String s_name = as_StringPool[ai_formdata[i_tmpoffset++]];
                    int i_x = ai_formdata[i_tmpoffset++];
                    int i_y = ai_formdata[i_tmpoffset++];
                    i_w = ai_formdata[i_tmpoffset++];
                    i_h = ai_formdata[i_tmpoffset++];
                    int i_buttonState = Tester.getButtonState(s_name);
                    int i_normalImageIndex = ai_formdata[i_tmpoffset];
                    
                    if (i_buttonState>=0 && i_buttonState<=BUTTONSTATE_DISABLED)
                    {
                        int i_destImage = ai_formdata[i_tmpoffset+i_buttonState];
                        if (i_destImage<0) i_destImage = i_normalImageIndex;
                        String s_resName = as_StringPool[i_destImage];
                        Image p_img = (Image)p_ResourceCache.get(s_resName);
                        _g.drawImage(p_img,i_x,i_y,null);
                        if (i_buttonIndex == i_SelectedButtonIndex)
                        {
                            // отрисовка курсора
                            Tester.drawCursor(_g,i_x,i_y,i_w,i_h);
                        }
                    }
                    
                };break;
                case OBJECT_CACHEDIMAGE :
                {
                    i_tmpoffset += DATALEN_CACHEDIMAGE-1;
                };break;
                case OBJECT_CUSTOMAREA :
                {
                    String s_areaName = as_StringPool[i_tmpoffset++];
                    int i_x = ai_formdata[i_tmpoffset++];
                    int i_y = ai_formdata[i_tmpoffset++];
                    i_w = ai_formdata[i_tmpoffset++];
                    i_h = ai_formdata[i_tmpoffset++];
                    Tester.drawCustomArea(_g,s_areaName,i_x,i_y,i_w,i_h);
                };break;
                case OBJECT_FILLRECT :
                {
                    int i_x = ai_formdata[i_tmpoffset++];
                    int i_y = ai_formdata[i_tmpoffset++];
                    i_w = ai_formdata[i_tmpoffset++];
                    i_h = ai_formdata[i_tmpoffset++];
                    i_c = ai_formdata[i_tmpoffset++];
                    _g.setColor(new Color(i_c));
                    _g.fillRect(i_x,i_y,i_w,i_h);
                };break;
                case OBJECT_RECT:
                {
                    int i_x = ai_formdata[i_tmpoffset++];
                    int i_y = ai_formdata[i_tmpoffset++];
                    i_w = ai_formdata[i_tmpoffset++];
                    i_h = ai_formdata[i_tmpoffset++];
                    i_c = ai_formdata[i_tmpoffset++];
                    _g.setColor(new Color(i_c));
                    _g.drawRect(i_x,i_y,i_w,i_h);
                };break;
                case OBJECT_TEXTLABEL:
                {
                    if (lg_drawLabels)
                    {
                        int i_x = ai_formdata[i_tmpoffset++];
                        int i_y = ai_formdata[i_tmpoffset++];
                        i_c = ai_formdata[i_tmpoffset++];
                        String s_string = as_StringPool[ai_formdata[i_tmpoffset++]];
                        _g.setColor(new Color(i_c));
                        _g.drawString(s_string,i_x,i_y+p_SystemFontMetrics.getMaxAscent());
                    }
                };break;
            }
        }
        
        _g.translate(-_x,-_y);
    }

    /**
     * Переводит курсор на следующую незапрещенную к сипользованию кнопку
     */
    public static final void selectNextButton()
    {
        int i_cur = i_SelectedButtonIndex;
        while(true)
        {
            i_cur ++;
            if (i_cur>=i_SelectedFormButtonsNumber) i_cur = 0;
            if (i_cur==i_SelectedButtonIndex) break;
            int i_buttonOffset = ai_FormButtonsList[i_cur]+1;
            String s_name = as_StringPool[i_buttonOffset];
            if (Tester.getButtonState(s_name)!=BUTTONSTATE_DISABLED)
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
        while(true)
        {
            i_cur --;
            if (i_cur<0) i_cur = i_SelectedFormButtonsNumber-1;
            if (i_cur==i_SelectedButtonIndex) break;
            int i_buttonOffset = ai_FormButtonsList[i_cur]+1;
            String s_name = as_StringPool[i_buttonOffset];
            if (Tester.getButtonState(s_name)!=BUTTONSTATE_DISABLED)
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
        if (_name==null) i_SelectedButtonIndex = 0;
        for(int li=0;li<i_SelectedFormButtonsNumber;li++)
        {
            int i_offset = ai_FormButtonsList[li];
            String s_str = as_StringPool[ai_FormsDataArray[i_offset+1]];
            if (_name==s_str) 
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
    public static final void selectForm(String _name,boolean _clearCache) throws Throwable
    {
        if (_clearCache) clearResourceCache();
        int i_formOffset = ((Integer)p_PointersTable.get(_name)).intValue();
        
        i_SelectedFormOffset = i_formOffset;
        
        final int [] ai_formarray = ai_FormsDataArray;
        
        p_SelectedFormColor = new Color(ai_formarray[i_formOffset+4]);
        
        // производим загрузку данных формы в кэш
        i_formOffset += DATALEN_FORM; // смещаем указатель на первый элемент

        i_SelectedFormButtonsNumber = 0;
        
        while(i_formOffset<MAXFORMARRAYLENGTH)
        {
            int i_type = ai_formarray[i_formOffset];
            int i_tmpoffst = i_formOffset+1;
            switch(i_type)
            {
                case OBJECT_FORM :
                {
                    // достигли конца формы, выходим
                    i_formOffset = MAXFORMARRAYLENGTH;
                };break;
                case OBJECT_CUSTOMAREA :
                {
                    i_formOffset+=DATALEN_CUSTOMAREA;
                };break;
                case OBJECT_RECT :
                case OBJECT_FILLRECT :
                {
                    i_formOffset+=DATALEN_RECT;
                };break;
                case OBJECT_TEXTLABEL :
                {
                    i_formOffset+=DATALEN_TEXTLABEL;
                };break;
                case OBJECT_IMAGE :
                {
                    i_formOffset+=DATALEN_IMAGE;
                    // загружаем изображение
                    loadImageToCache(ai_formarray[i_tmpoffst+2]);
                };break;
                case OBJECT_CACHEDIMAGE :
                {
                    i_formOffset+=DATALEN_CACHEDIMAGE;

                    // загружаем изображение
                    loadImageToCache(ai_formarray[i_tmpoffst]);
                };break;
                case OBJECT_BUTTON :
                {
                    i_formOffset+=DATALEN_BUTTON;
                    // загружаем изображения и звук
                    i_tmpoffst+=3;
                    loadImageToCache(ai_formarray[i_tmpoffst++]);
                    loadImageToCache(ai_formarray[i_tmpoffst++]);
                    loadImageToCache(ai_formarray[i_tmpoffst++]);
                    loadImageToCache(ai_formarray[i_tmpoffst++]);
                    
                    ai_FormButtonsList[i_SelectedFormButtonsNumber++] = i_formOffset;
                };break;
                default: throw new Throwable();
            }
        }
    }
    
    /**
     * Инициализация менеджера
     * @param _resource идентификатор ресурса в котором находится  
     * @throws Throwable порождается в случае проблемы инициализации менеджера
     */
    public static final void init(String _resource) throws Throwable
    {
        InputStream p_inStream = Utils.getResourceAsStream(_resource);
        
        final String [] as_array = new String[16]; 
        final int [] ai_formarray = ai_FormsDataArray;

        i_StringPoolLength = 0;
        p_PointersTable.clear();
        p_ResourceCache.clear();
        
        int i_formarraypoiner = 0;
        
        for(int li=0;li<as_StringPool.length;li++) as_StringPool[li] = null;
        
        try
        {
            while (true)
            {
                String s_string = readString(p_inStream);
                if (s_string==null) break;
                s_string = s_string.trim();
                
                int i_len =parseStringToArray(s_string,as_array);
                if (i_len==0) continue;
                String s_firstElem = as_array[0];
                if (s_firstElem.equals("form"))
                {
                    // form,<name>,<width>,<height>,<color>,<opacity>
                    
                    int i_nameOffset = addObjectPointer(as_array[1],i_formarraypoiner);
                    ai_formarray[i_formarraypoiner++] = OBJECT_FORM;
                    ai_formarray[i_formarraypoiner++] = i_nameOffset;
                    int i_w = Integer.parseInt(as_array[2]);
                    int i_h = Integer.parseInt(as_array[3]);
                    int i_color = getColorFromString(as_array[4]);
                    int i_opacity = Integer.parseInt(as_array[5]);
                    
                    ai_formarray[i_formarraypoiner++] = i_w;
                    ai_formarray[i_formarraypoiner++] = i_h;
                    ai_formarray[i_formarraypoiner++] = i_color;
                    ai_formarray[i_formarraypoiner++] = i_opacity;
                }
                else
                if (s_firstElem.equals("button"))
                {
                    // button,<name>,<x>,<y>,<width>,<htight>,<normal_image_path>,<pressed_image_path>,<selected_image_path>,<disabled_image_path>
                    
                    int i_namePointer = addObjectPointer(as_array[1],i_formarraypoiner);
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
                        // cimage,<image_path>
                        ai_formarray[i_formarraypoiner++] = OBJECT_CACHEDIMAGE;
                        ai_formarray[i_formarraypoiner++] = addCachePath(as_array[1]);
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
                    
                    int i_nameIndex = addObjectPointer(as_array[1],i_formarraypoiner);

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
        finally
        {
            clearResourceCache();
            
            Utils.closeStream(p_inStream);
            p_inStream = null;
        }
    }
}
