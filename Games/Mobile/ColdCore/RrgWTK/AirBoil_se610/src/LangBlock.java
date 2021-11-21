import javax.microedition.lcdui.Image;
import javax.microedition.lcdui.Graphics;
import java.io.InputStream;
import java.io.DataInputStream;

/**
 * Класс описывает блок, для работы с языковыми ресурсами. Осуществляет загрузку и отображение.
 * @version 2.7
 * @author Igor Maznitsa
 */
public class LangBlock
{
    /**
     * Массив содержит имена языковых модулей
     */
    public static byte [] ab_LanguageNames;
    /**
     * Переменная содержит первый индекс имени языка в списке смещений
     */
    public static int i_FirstLanguageIndex;
    /**
     * Переменная содержит количество языков, поддерживаемых блоком
     */
    public static int i_LanguageNumber;
    /**
     * Массив содержит идентификаторы языковых модулей
     */
    public static String[] as_LanguageIDs;
    /**
     * Массив текстовыx ссылок на файлы содержащие языковые ресурсы
     */
    public static String[] as_LanguageResource;
    /**
     * Индекс текущего выбранного языкового ресурса
     */
    public static int i_CurrentLanguageIndex = -1;
    /**
     * Строка содержит идентификатор родного для устройства языка
     */
    public static String s_PhoneNativeLanguage;
    /**
     * Массив содержит текущий массив строк для выбранного языкового ресурса
     */
    public static byte [] ab_TextStringArray;
    /**
     * Массив содержит массив смещений до строк для выбранного языкового ресурса
     */
    public static int [] ai_TextStringOffsetsArray;
    /**
     * Картинка содержит набор символов для отображения текста
     */
    public static Image p_FontImage;
    /**
     * Ширина одного графического символа
     */
    public static int i_FontImage_CharWidth;
    /**
     * Высота одного графического символа
     */
    public static int i_FontImage_CharHeight;

    /**
     * Количество используемых символов в CHARSET0
     */
    protected static final int CHARSET0_SYMBOLS = 24;
    /**
     * Количество используемых символов в CHARSET1
     */
    protected static final int CHARSET1_SYMBOLS = 52;
    /**
     * Количество используемых символов в CHARSET2
     */
    protected static final int CHARSET2_SYMBOLS = 64;
    /**
     * Количество используемых символов в CHARSET3
     */
    private static final int CHARSET3_SYMBOLS = 0;

    private static final int CHAR_MINUS_CODE = 18;
    private static final int CHAR_ZERO_CODE = 2;

    private static int i_Char_Minus_Row,i_Char_Minus_Col;

    private static final int GRAPHICS_ANCHOR = Graphics.TOP|Graphics.LEFT;

    /**
     * Массив содержит смещение каждого набора символов, т.е. суммированное количество задействованных символов предыдущих наборов
     */
    private static final int [] ARRAY_CHARSET_OFFSET = new int [] {0,CHARSET0_SYMBOLS,CHARSET0_SYMBOLS+CHARSET1_SYMBOLS,CHARSET0_SYMBOLS+CHARSET1_SYMBOLS+CHARSET2_SYMBOLS};

    /**
     * Функция загружает новый языковой ресурс из файла с заданным именем
     * @param _loader класс, через который получаем доступ к ресурсу
     * @param _resourcename имя файла ресурса
     * @throws Exception исключение если произошла ошибка чтения
     */
    private static final void changeResource(Class _loader,String _resourcename) throws Exception
    {
        Runtime.getRuntime().gc();

        InputStream is = _loader.getClass().getResourceAsStream(_resourcename);
        DataInputStream ds = new DataInputStream(is);
        int i_stringNumber = ds.readUnsignedByte();
        int i_len = ds.readUnsignedShort();
        byte [] ab_langNames = ab_LanguageNames;
        byte[] ab_new_array = new byte[i_len+ab_langNames.length];
        ds.read(ab_new_array,0,ab_new_array.length);
        System.arraycopy(ab_langNames,0,ab_new_array,i_len,ab_langNames.length);
        if (p_FontImage != null) encodeStringArrayToTiles(ab_new_array);

        // Заполняем массив смещений для строк
        i_FirstLanguageIndex = i_stringNumber;
        i_stringNumber += i_LanguageNumber;
        ai_TextStringOffsetsArray = new int[i_stringNumber];

        int i_offst = 0;
        for(int li=0;li<i_stringNumber;li++)
        {
            ai_TextStringOffsetsArray[li] = i_offst;
            int i_strLen = (ab_new_array[i_offst++] & 0xFF)+1;
            i_offst+=i_strLen;
        }

        ab_TextStringArray = ab_new_array;
        ds.close();
        ds = null;
        is = null;

        Runtime.getRuntime().gc();
    }

    /**
     * Функция устанавливаеь новый индекс выбранного языка
     * @param _loader класс, через который получаем доступ к ресурсу
     * @param _index индекс выбираемого языка
     * @throws Exception исключение если произошла ошибка в процессе загрузки
     */
    protected static final void setLanguage(Class _loader,int _index) throws Exception
    {
        if (i_CurrentLanguageIndex == _index) return;
        changeResource(_loader,as_LanguageResource[_index]);
        i_CurrentLanguageIndex = _index;
    }

    /**
     * Функция ищет индекс для заданного языкового идентификатора
     * @param _lang_id языковой идентификатор
     * @return индекс или -1 если не найден
     */
    private static final int getIndexForID(String _lang_id)
    {
        _lang_id = _lang_id.trim().toLowerCase();
        String [] as_str = as_LanguageIDs;
        int i_len = as_str.length;
        for (int li = 0;li < i_len;li++)
        {
            if (_lang_id.equals(as_str[li])) return li;
        }
        return -1;
    }

    /**
     * Функция перекодирует массив байт с учетом состояния картинки содержащей графическое отображение символов, для более быстрой навигации по ней
     * @param _strings массив байт, содержащих строки
     */
    private static final void encodeStringArrayToTiles(byte [] _strings)
    {
        int i_len = _strings.length;
        int i_index = 0;

        final int [] ai_charsets = ARRAY_CHARSET_OFFSET;

        while(i_index<i_len)
        {
            int i_strLen = (_strings[i_index++]&0xFF)+1;
            while(i_strLen>0)
            {
                int i_char = _strings[i_index]&0xFF;
                int i_charset = i_char >>> 6;
                int i_code = i_char & 0x3F;

                int i_pos = i_code + ai_charsets[i_charset];

                i_char = i_pos;
                _strings[i_index++] = (byte)i_char;
                i_strLen--;
            }
        }

        //#if SHOWSYS
            System.out.println("Lang res.encoded");
        //#endif
    }

    /**
     * Функция перекодирует строку байт с учетом состояния картинки содержащей графическое отображение символов, для более быстрой навигации по ней
     * @param _string массив байт, содержащий строку
     */
    protected static final byte[] encodeStringToTiles(byte [] _string,int _offset)
    {
            int i_index = _offset;
            final int [] ai_charsets = ARRAY_CHARSET_OFFSET;
            int i_strLen = (_string[i_index++]&0xFF)+1;
            byte [] ab_newStr = new byte[i_strLen+1];
            ab_newStr[0] = (byte)(i_strLen-1);
            int i_nsIndex = 1;
            while(i_strLen>0)
            {
                int i_char = _string[i_index++]&0xFF;
                int i_charset = i_char >>> 6;
                int i_code = i_char & 0x3F;

                int i_pos = i_code + ai_charsets[i_charset];

                i_char = i_pos;
                ab_newStr[i_nsIndex++] = (byte)i_char;
                i_strLen--;
            }
            return ab_newStr;
    }

    /**
     * Инициализация блока языковых ресурсов
     * @param _loader класс, позволяющий получить доступ к ресурсу
     * @param _fontImage картинка, содержащая изображения символов
     * @param _charWidth ширина графического символа в пикселях
     * @param _charHeight высота графического символа в пикселях
     * @param _language_list имя ресурса, содержащего список языков
     * @param _language_id идентификатор выставляемого языка, -1 по умолчанию
     * @throws Exception порождается если ошибка в процессе загрузки
     */
    public static final int initLanguageBlock(Class _loader,Image _fontImage,int _charWidth,int _charHeight,String _language_list,int _language_id) throws Exception
    {
        //#if SHOWSYS
        int i_errCode = 0;
        try
        {
        //#endif

        p_FontImage = _fontImage;
        i_FontImage_CharWidth = _charWidth;
        i_FontImage_CharHeight = _charHeight;

        String s_locale = System.getProperty("microedition.locale");
        if (s_locale == null)
            s_PhoneNativeLanguage = "en";
        else
            s_PhoneNativeLanguage = s_locale.trim().toLowerCase().substring(0,2);

        Runtime.getRuntime().gc();

        //#if SHOWSYS
        i_errCode++;
        //#endif

        InputStream is = _loader.getClass().getResourceAsStream(_language_list);
        DataInputStream ds = new DataInputStream(is);
        int i_num = ds.readUnsignedByte();
        int i_langNamesLen = ds.readUnsignedShort();
        ab_LanguageNames = new byte[i_langNamesLen];
        as_LanguageIDs = new String[i_num];
        i_LanguageNumber = i_num;
        as_LanguageResource = new String[i_num];

        ds.read(ab_LanguageNames);

        for (int li = 0;li < i_num;li++)
        {
            as_LanguageIDs[li] = ds.readUTF();
            as_LanguageResource[li] = ds.readUTF();
        }

        // Вычисляем смещение до минуса
        i_Char_Minus_Col = CHAR_MINUS_CODE  & 0xF;
        i_Char_Minus_Row = CHAR_MINUS_CODE  >>> 4;

        Runtime.getRuntime().gc();

        ds.close();
        ds = null;
        is = null;

        //#if SHOWSYS
        i_errCode++;
        //#endif

        if (_language_id < 0)
        {
            //#if SHOWSYS
            i_errCode=9889;
            //#endif
            _language_id = getIndexForID(s_PhoneNativeLanguage);
            if (_language_id < 0) _language_id = 0;
        }

        //#if SHOWSYS
        i_errCode=9823;
        //#endif
        setLanguage(_loader,_language_id);

        Runtime.getRuntime().gc();
        return _language_id;

        //#if SHOWSYS
        }
        catch(Exception _ex)
        {
            throw new Exception("lb:"+i_errCode);
        }
        //#endif
    }

    //---------------------------Блок отображения текстовых строк--------------------------------------
    /**
     * Массив содержит наборы символов для декодирования упакованных байтовых строк
     */
    public static final String [] CHARSETS = new String []
    {
      "~ 0123456789:.,!?+-/\'\"()",
      "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz",
      "АБВГДЕЖЗИЙКЛМНОПРСТУФХЦЧШЩЪЫЬЭЮЯабвгдежзийклмнопрстуфхцчшщъыьэюя"
    };

    /**
     * Функция переводит упакованную строку в формат нормальной строки
     */
    public static final String decodeString(byte [] _array,int _offset)
    {
        //TODO
        int i_length = (_array[_offset++] & 0xFF)+1;
        StringBuffer p_buffer = new StringBuffer(i_length);
        while(i_length>0)
        {
            int i_char = _array[_offset++] & 0xFF;

            //#if VENDOR=="SAMSUNG"
            // Переводим позицию тайтла в код символа
            //$int i_set = 0;
            //$int i_pos = 0;
            //$if (i_char<CHARSET0_SYMBOLS)
            //${
            //$    // Набор0
            //$    i_pos = i_char;
            //$}
            //$else
            //$if (i_char<(CHARSET0_SYMBOLS+CHARSET1_SYMBOLS))
            //${
            //$    // Набор1
            //$    i_set=1;
            //$    i_pos = i_char - CHARSET0_SYMBOLS;
            //$}
            //$else
            //$if (i_char<(CHARSET0_SYMBOLS+CHARSET1_SYMBOLS+CHARSET2_SYMBOLS))
            //${
            //$    // Набор2
            //$    i_set=2;
            //$    i_pos = i_char - (CHARSET0_SYMBOLS + CHARSET1_SYMBOLS);
            //$}
            //$else
            //${
            //$    // Набор3
            //$    i_set=3;
            //$    i_pos = i_char - (CHARSET0_SYMBOLS + CHARSET1_SYMBOLS + CHARSET2_SYMBOLS);
            //$}

            //$final String s_charset = CHARSETS[i_set];
            //$p_buffer.append(s_charset.charAt(i_pos));

            //#else

            int i_charset = i_char >>> 6;
            int i_position = i_char & 0x3F;
            final String s_charset = CHARSETS[i_charset];
            p_buffer.append(s_charset.charAt(i_position));

            //#endif

            i_length--;
        }
        return p_buffer.toString();
    }

    public static final String getStringForIndex(int _stringIndex)
    {
        return decodeString(ab_TextStringArray,ai_TextStringOffsetsArray[_stringIndex]);
    }
    //---------------------------Блок отображения строк при помощи картинки----------------------------
    //#if VENDOR=="SAMSUNG"

    private static final int CHAR_INTERVAL = 1;

    /**
     * Возвращает ширину строки в пикселях
     * @param _stringIndex индекс строки
     * @return ширина строки в пикселях
     */
    public static final int getStringWidth(int _stringIndex)
    {
        int i_strOffset = ai_TextStringOffsetsArray[_stringIndex];
        int i_stringLen = (ab_TextStringArray[i_strOffset]&0xFF)+1;
        return i_stringLen*(i_FontImage_CharWidth+CHAR_INTERVAL);
    }

    /**
     * Отрисовывает строку
     * @param _index индекс строки
     * @param _g канвас
     * @param _x X координата верхнего левого края
     * @param _y Y координата верхнего левого края
     */
    protected static final void drawStringForIndex(int _index,Graphics _g,int _x,int _y)
    {
        drawString(ai_TextStringOffsetsArray[_index],_g,_x,_y);
    }

    /**
     * Отрисовка символа в заданных координатах
     * @param _g канвас
     * @param _char символ (в старших 4-х битах строка, в младших 4-х битах столбец картинки с отсчетом от 0)
     * @param _x координата X вывода
     * @param _y координата Y вывода
     */
    protected static final void drawChar(Graphics _g,int _char,int _x,int _y)
    {
        final int i_col = _char&0xF;
        final int i_row = _char>>>4;

        final int i_charW = i_FontImage_CharWidth;
        final int i_charH = i_FontImage_CharHeight;

        final int i_xoffset = i_col * i_charW;
        final int i_yoffset = i_row * i_charH;

        _g.setClip(_x,_y,i_charW,i_charH);
        _g.drawImage(p_FontImage,_x-i_xoffset,_y-i_yoffset,GRAPHICS_ANCHOR);
    }

    /**
     * Отрисовывает строку с заданным смещением в массиве строк
     * @param _stringOffset смещение в массиве
     * @param _g канвас
     * @param _x X координата верхнего левого края
     * @param _y Y координата верхнего левого края
     */
    public static final void drawString(int _stringOffset,Graphics _g,int _x,int _y)
    {
        int i_strOffset = _stringOffset;
        final byte [] ab_array = ab_TextStringArray;
        int i_stringLen = (ab_array[i_strOffset++]&0xFF)+1;
        int i_outX = _x;
        final int i_outY = _y;
        final int i_charWidth = i_FontImage_CharWidth;
        final int i_charHeight = i_FontImage_CharHeight;
        final Image p_fontImg = p_FontImage;
        while(i_stringLen>0)
        {
            final int i_char = ab_array[i_strOffset++] & 0xFF;
            final int i_col = i_char&0xF;
            final int i_row = i_char>>>4;

            final int i_xoffset = i_col * i_charWidth;
            final int i_yoffset = i_row * i_charHeight;

            _g.setClip(i_outX,i_outY,i_charWidth,i_charHeight);
            _g.drawImage(p_fontImg,i_outX-i_xoffset,i_outY-i_yoffset,GRAPHICS_ANCHOR);

            i_outX += i_charWidth+CHAR_INTERVAL;
            i_stringLen--;
        }
    }

    private static final int NUMBER_LEN = 11;

    /**
     *  Служебный массив, используется при отрисовке числа. Содержит координаты смещения Y.
     */
    private static final int [] ai_numberDigitsArrayRow = new int [NUMBER_LEN];
    /**
     *  Служебный массив, используется при отрисовке числа. Содержит координаты смещения X.
     */
    private static final int [] ai_numberDigitsArrayCol = new int [NUMBER_LEN];

    protected static final void drawInteger(int _value,Graphics _g,int _x,int _y)
    {
        final int [] ai_digRow = ai_numberDigitsArrayRow;
        final int [] ai_digCol = ai_numberDigitsArrayCol;

        int i_absVal = Math.abs(_value);

        int i_charWidth = i_FontImage_CharWidth;
        int i_charHeight = i_FontImage_CharHeight;

        int i_indx = NUMBER_LEN;

        if (i_absVal == 0)
        {
            i_indx--;
            ai_digCol[i_indx] = (CHAR_ZERO_CODE & 0xF)*i_charWidth;
            ai_digRow[i_indx] = (CHAR_ZERO_CODE >>> 4)*i_charHeight;
        }
        else
        while(i_absVal!=0)
        {
            i_indx--;
            int i_val = i_absVal % 10;
            i_absVal /= 10;
            i_val += CHAR_ZERO_CODE;
            ai_digCol[i_indx] = (i_val & 0xF)*i_charWidth;
            ai_digRow[i_indx] = (i_val >>> 4)*i_charHeight;
        }

        if (_value<0)
        {
            i_indx--;
            ai_digRow[i_indx] = i_Char_Minus_Row*i_charHeight;
            ai_digCol[i_indx] = i_Char_Minus_Col*i_charWidth;
        }

        int i_outX = _x;
        int i_outY = _y;
        Image p_fontImg = p_FontImage;
        for(int li=i_indx;li<NUMBER_LEN;li++)
        {
            final int i_xoffset = ai_digCol[li];
            final int i_yoffset = ai_digRow[li];

            _g.setClip(i_outX,i_outY,i_charWidth,i_charHeight);
            _g.drawImage(p_fontImg,i_outX-i_xoffset,i_outY-i_yoffset,GRAPHICS_ANCHOR);

            i_outX += i_charWidth+1;
        }
    }

    //#endif

}
