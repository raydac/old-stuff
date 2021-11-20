package mtv.pillow;

import javax.microedition.lcdui.Image;
import javax.microedition.lcdui.Graphics;
import java.io.InputStream;
import java.io.DataInputStream;

/**
 * ����� ��������� ����, ��� ������ � ��������� ���������. ������������ �������� � �����������.
 * @version 1.01
 * @author Igor Maznitsa
 */
public class LangBlock
{
    /**
     * ������ �������� ����� �������� �������
     */
    public static byte [] ab_LanguageNames;
    /**
     * ���������� �������� ������ ������ ����� ����� � ������ ��������
     */
    public static int i_FirstLanguageIndex;
    /**
     * ���������� �������� ���������� ������, �������������� ������
     */
    public static int i_LanguageNumber;
    /**
     * ������ �������� �������������� �������� �������
     */
    public static String[] as_LanguageIDs;
    /**
     * ������ ��������x ������ �� ����� ���������� �������� �������
     */
    public static String[] as_LanguageResource;
    /**
     * ������ �������� ���������� ��������� �������
     */
    public static int i_CurrentLanguageIndex = -1;
    /**
     * ������ �������� ������������� ������� ��� ���������� �����
     */
    public static String s_PhoneNativeLanguage;
    /**
     * ������ �������� ������� ������ ����� ��� ���������� ��������� �������
     */
    public static byte [] ab_TextStringArray;
    /**
     * ������ �������� ������ �������� �� ����� ��� ���������� ��������� �������
     */
    public static int [] ai_TextStringOffsetsArray;
    /**
     * �������� �������� ����� �������� ��� ����������� ������
     */
    public static Image p_FontImage;
    /**
     * ���������� �������� �� ������ �������� �����
     */
    public static int i_FontImage_ColsNumber;
    /**
     * ������ ������ ������������ �������
     */
    public static int i_FontImage_CharWidth;
    /**
     * ������ ������ ������������ �������
     */
    public static int i_FontImage_CharHeight;

    /**
     *  ��������� ������, ������������ ��� ��������� �����. �������� ���������� �������� Y.
     */
    private static final int [] ai_numberDigitsArrayRow = new int [11];
    /**
     *  ��������� ������, ������������ ��� ��������� �����. �������� ���������� �������� X.
     */
    private static final int [] ai_numberDigitsArrayCol = new int [11];

    /**
     * ���������� ������������ �������� � CHARSET0
     */
    private static final int CHARSET0_SYMBOLS = 24;
    /**
     * ���������� ������������ �������� � CHARSET1
     */
    private static final int CHARSET1_SYMBOLS = 26;
    /**
     * ���������� ������������ �������� � CHARSET2
     */
    private static final int CHARSET2_SYMBOLS = 32;
    /**
     * ���������� ������������ �������� � CHARSET3
     */
    private static final int CHARSET3_SYMBOLS = 0;

    private static final int CHAR_MINUS_CODE = 17;
    private static final int CHAR_ZERO_CODE = 1;

    private static int i_Char_Minus_Row,i_Char_Minus_Col;

    /**
     * ������ �������� �������� ������� ������ ��������, �.�. ������������� ���������� ��������������� �������� ���������� �������
     */
    private static final int [] ARRAY_CHARSET_OFFSET = new int [] {0,CHARSET0_SYMBOLS,CHARSET0_SYMBOLS+CHARSET1_SYMBOLS,CHARSET0_SYMBOLS+CHARSET1_SYMBOLS+CHARSET2_SYMBOLS};

    /**
     * ������� ��������� ����� �������� ������ �� ����� � �������� ������
     * @param _loader �����, ����� ������� �������� ������ � �������
     * @param _resourcename ��� ����� �������
     * @throws Exception ���������� ���� ��������� ������ ������
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

        // ��������� ������ �������� ��� �����
        i_FirstLanguageIndex = i_stringNumber;
        i_stringNumber += i_LanguageNumber;
        ai_TextStringOffsetsArray = new int[i_stringNumber];
        ab_TextStringArray = ab_new_array;
        int i_offst = 0;
        for(int li=0;li<i_stringNumber;li++)
        {
            ai_TextStringOffsetsArray[li] = i_offst;
            int i_strLen = ab_TextStringArray[i_offst++] & 0xFF;
            i_offst+=i_strLen;
        }

        ab_TextStringArray = ab_new_array;
        ds.close();
        ds = null;
        is = null;

        Runtime.getRuntime().gc();
    }

    /**
     * ������� ������������� ����� ������ ���������� �����
     * @param _loader �����, ����� ������� �������� ������ � �������
     * @param _index ������ ����������� �����
     * @throws Exception ���������� ���� ��������� ������ � �������� ��������
     */
    protected static final void setLanguage(Class _loader,int _index) throws Exception
    {
        if (i_CurrentLanguageIndex == _index) return;
        changeResource(_loader,as_LanguageResource[_index]);
        i_CurrentLanguageIndex = _index;
    }

    /**
     * ������� ���� ������ ��� ��������� ��������� ��������������
     * @param _lang_id �������� �������������
     * @return ������ ��� -1 ���� �� ������
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
     * ������� ������������ ������ ���� � ������ ��������� �������� ���������� ����������� ����������� ��������, ��� ����� ������� ��������� �� ���
     * @param _strings ������ ����, ���������� ������
     */
    private static final void encodeStringArrayToTiles(byte [] _strings)
    {
        int i_len = _strings.length;
        int i_index = 0;

        int [] ai_charsets = ARRAY_CHARSET_OFFSET;

        while(i_index<i_len)
        {
            int i_strLen = _strings[i_index++]&0xFF;
            while(i_strLen>0)
            {
                int i_char = _strings[i_index]&0xFF;
                int i_charset = i_char >>> 6;
                int i_code = i_char & 0x3F;
                int i_offset = ai_charsets[i_charset]+i_code;

                int i_col = i_offset % i_FontImage_ColsNumber;
                int i_row = i_offset / i_FontImage_ColsNumber;

                i_char = (i_col << 4)&i_row;
                _strings[i_index++] = (byte)i_char;
                i_strLen--;
            }
        }
    }

    /**
     * ������������� ����� �������� ��������
     * @param _loader �����, ����������� �������� ������ � �������
     * @param _fontImage ��������, ���������� ����������� ��������
     * @param _charWidth ������ ������������ ������� � ��������
     * @param _charHeight ������ ������������ ������� � ��������
     * @param _charCols ���������� �������� � ����� ������ �������� �����
     * @param _language_list ��� �������, ����������� ������ ������
     * @param _language_id ������������� ������������� �����, -1 �� ���������
     * @throws Exception ����������� ���� ������ � �������� ��������
     */
    public static final int initLanguageBlock(Class _loader,Image _fontImage,int _charWidth,int _charHeight,int _charCols,String _language_list,int _language_id) throws Exception
    {
        p_FontImage = _fontImage;
        i_FontImage_CharWidth = _charWidth;
        i_FontImage_CharHeight = _charHeight;
        i_FontImage_ColsNumber = _charCols;

        s_PhoneNativeLanguage = System.getProperty("microedition.locale").trim().toLowerCase().substring(0,2);
        Runtime.getRuntime().gc();

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

        // ��������� �������� �� ������
        i_Char_Minus_Col = CHAR_MINUS_CODE % i_FontImage_ColsNumber;
        i_Char_Minus_Row = CHAR_MINUS_CODE % i_FontImage_ColsNumber;

        Runtime.getRuntime().gc();

        ds.close();
        ds = null;
        is = null;

        if (_language_id < 0)
        {
            _language_id = getIndexForID(s_PhoneNativeLanguage);
            if (_language_id < 0) _language_id = 0;
        }
        setLanguage(_loader,_language_id);

        Runtime.getRuntime().gc();
        return _language_id;
    }

    //---------------------------���� ����������� ��������� �����--------------------------------------
    /**
     * ������ �������� ������ �������� ��� ������������� ����������� �������� �����
     */
    public static final String [] CHARSETS = new String []
    {
      "~ 0123456789:.,!?+-/\'\"()",
      "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz",
      "����������������������������������������������������������������"
    };

    /**
     * ������� ��������� ����������� ������ � ������ ���������� ������
     */
    public static final String decodeString(byte [] _array,int _offset)
    {
        int i_length = _array[_offset++] & 0xFF;
        StringBuffer p_buffer = new StringBuffer(i_length);
        while(i_length>0)
        {
            int i_char = _array[_offset++] & 0xFF;
            int i_charset = i_char >>> 6;
            int i_position = i_char & 0x3F;
            final String s_charset = CHARSETS[i_charset];
            p_buffer.append(s_charset.charAt(i_position));
            i_length--;
        }
        return p_buffer.toString();
    }

    public static final String getStringForIndex(int _stringIndex)
    {
        return decodeString(ab_TextStringArray,ai_TextStringOffsetsArray[_stringIndex]);
    }
    //---------------------------���� ����������� ����� ��� ������ ��������----------------------------
    /**
     * ���������� ������ ������ � ��������
     * @param _stringIndex ������ ������
     * @return ������ ������ � ��������
     */
    public static final int getStringWidth(int _stringIndex)
    {
        int i_strOffset = ai_TextStringOffsetsArray[_stringIndex];
        int i_stringLen = ab_TextStringArray[i_strOffset];
        return i_stringLen*i_FontImage_CharWidth;
    }

    /**
     * ������������ ������
     * @param _index ������ ������
     * @param _g ������
     * @param _x X ���������� �������� ������ ����
     * @param _y Y ���������� �������� ������ ����
     */
    public static final void drawStringForIndex(int _index,Graphics _g,int _x,int _y)
    {
        drawString(ai_TextStringOffsetsArray[_index],_g,_x,_y);
    }

    /**
     * ������������ ������ � �������� ��������� � ������� �����
     * @param _stringOffset �������� � �������
     * @param _g ������
     * @param _x X ���������� �������� ������ ����
     * @param _y Y ���������� �������� ������ ����
     */
    public static final void drawString(int _stringOffset,Graphics _g,int _x,int _y)
    {
        int i_strOffset = _stringOffset;
        byte [] ab_array = ab_TextStringArray;
        int i_stringLen = ab_array[i_strOffset++];
        int i_outX = _x;
        int i_outY = _y;
        int i_charWidth = i_FontImage_CharWidth;
        int i_charHeight = i_FontImage_CharHeight;
        Image p_fontImg = p_FontImage;
        while(i_stringLen!=0)
        {
            int i_char = ab_array[i_strOffset++] & 0xFF;
            int i_col = i_char>>>4;
            int i_row = i_char&0xF;

            int i_xoffset = i_col * i_charWidth;
            int i_yoffset = i_row * i_charHeight;

            _g.setClip(i_outX,i_outY,i_charWidth,i_charHeight);
            _g.drawImage(p_fontImg,i_outX-i_xoffset,i_outY-i_yoffset,0);

            i_outX += i_charWidth;
            i_stringLen--;
        }
    }

    public static final void drawInteger(int _value,Graphics _g,int _x,int _y)
    {
        boolean lg_minus = _value <0 ? true : false;
        int [] ai_digRow = ai_numberDigitsArrayRow;
        int [] ai_digCol = ai_numberDigitsArrayCol;
        int i_len = 0;
        if (lg_minus)
        {
            i_len = 1;
            ai_digRow[0] = i_Char_Minus_Row;
            ai_digCol[0] = i_Char_Minus_Col;
        }

        int i_absVal = Math.abs(_value);
        int i_mul = 1;
        int i_colsNum = i_FontImage_ColsNumber;

        int i_charWidth = i_FontImage_CharWidth;
        int i_charHeight = i_FontImage_CharHeight;
        while(i_mul<i_absVal)
        {
            int i_m = i_mul * 10;
            int i_val = i_absVal % i_m;
            i_val = (i_val / (i_m/10))+CHAR_ZERO_CODE;
            ai_digCol[i_len] = (i_val % i_colsNum)*i_charWidth;
            ai_digRow[i_len++] = (i_val / i_colsNum)*i_charHeight;
        }

        int i_outX = _x;
        int i_outY = _y;
        Image p_fontImg = p_FontImage;
        for(int li=0;li<i_len;li++)
        {
            int i_xoffset = ai_digCol[li];
            int i_yoffset = ai_digRow[li];

            _g.setClip(i_outX,i_outY,i_charWidth,i_charHeight);
            _g.drawImage(p_fontImg,i_outX-i_xoffset,i_outY-i_yoffset,0);

            i_outX += i_charWidth;
        }
    }

}
