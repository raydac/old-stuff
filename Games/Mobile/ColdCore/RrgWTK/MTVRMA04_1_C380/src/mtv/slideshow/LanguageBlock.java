package mtv.slideshow;

import java.io.IOException;
import java.io.InputStream;
import java.io.DataInputStream;

/**
 * Класс описывает блок, ответственный зазагрузку языкового модуля и его смену
 *
 * @author Igor Maznitsa
 * @version 1.00
 * @since 25 sep 2004
 */
public class LanguageBlock
{
    /**
     * Массив содержит список языков, поддерживаемых модулем
     */
    protected String[] LB_as_LanguageNames;

    /**
     * Массив содержит список идентификаторов языков, поддерживаемых модулем
     */
    protected String[] LB_as_LanguageIDs;

    /**
     * Массив содержит список ресурсов содержащих тексты для языковых модулей
     */
    protected String[] LB_as_LanguageResource;

    /**
     * Переменная показывает индекс текущего выбранного языка, если -1 то язык используемый по умолчанию
     */
    protected int LB_i_CurrentLanguageIndex = -1;

    /**
     * Переменная содержит строковое представление языка, который используется мобильным устройством
     */
    protected String LB_s_PhoneNativeLanguage;

    /**
     * Массив содержит строки текста,загруженные из выбранного языкового модуля
     */
    protected String[] LB_as_TextStringArray;

    /**
     * Функция производит загрузку текста из ресурса с заданным именем
     *
     * @param _resourcename имя загружаемого ресурса
     * @throws java.io.IOException исключение, если загрузка произведена с ошибкой
     */
    private void LB_changeResource(String _resourcename) throws IOException
    {
        Runtime.getRuntime().gc();

        InputStream is = getClass().getResourceAsStream(_resourcename);
        DataInputStream ds = new DataInputStream(is);
        int i_num = ds.readUnsignedByte();
        String[] as_new_array = new String[i_num];

        for (int li = 0; li < i_num; li++)
        {
            as_new_array[li] = ds.readUTF();
            if (as_new_array[li].length() == 0)
                as_new_array[li] = null;
            else
                as_new_array[li] = as_new_array[li].replace('~', '\n');
            Runtime.getRuntime().gc();
        }
        LB_as_TextStringArray = null;
        LB_as_TextStringArray = as_new_array;
        ds.close();
        ds = null;
        is = null;
        Runtime.getRuntime().gc();
    }

    /**
     * Функция устанавливает новый язык для модуля и производит загрузку текста из ассоциированного ресурса
     *
     * @param _index индекс языка
     * @throws java.io.IOException исключение генерируется если произошла ошибка в процессе смены языка
     */
    protected void LB_setLanguage(int _index) throws IOException
    {
        if (LB_i_CurrentLanguageIndex == _index || _index >= LB_as_LanguageNames.length || _index < 0) return;
        LB_changeResource(LB_as_LanguageResource[_index]);
        LB_i_CurrentLanguageIndex = _index;
    }

    /**
     * Возвращает индекс для текстового идентификатора языка
     *
     * @param _lang_id идентификатор языка
     * @return индекс данного языка в массиве
     */
    protected int LB_getIndexForID(String _lang_id)
    {
        _lang_id = _lang_id.trim().toLowerCase();
        for (int li = 0; li < LB_as_LanguageIDs.length; li++)
        {
            if (_lang_id.equals(LB_as_LanguageIDs[li])) return li;
        }
        return -1;
    }

    /**
     * Инициализация модуля, если передается -1 как идентификатор, то производится следующее:
     * 1) Выясняется используемый в устройстве язык
     * 2) Если он поддерживается, то выставляется он
     * 3) Если его нет, то выставляется язык с индексом 0 (обычно английский)
     *
     * @param _language_list Имя ресурса, содержащего языковую информацию
     * @param _language_id   Идентификатор языка, который будет выставлен после загрузки модуля или -1 если требуется выставить язык по умолчанию
     * @throws java.io.IOException исключение порождается в случае ошибки при инициализации
     */
    protected int LB_initLanguageBlock(String _language_list, int _language_id) throws IOException
    {
        LB_s_PhoneNativeLanguage = System.getProperty("microedition.locale").trim().toLowerCase().substring(0, 2);
        Runtime.getRuntime().gc();

        InputStream is = getClass().getResourceAsStream(_language_list);
        DataInputStream ds = new DataInputStream(is);
        int i_num = ds.readUnsignedByte();
        LB_as_LanguageNames = new String[i_num];
        LB_as_LanguageIDs = new String[i_num];
        LB_as_LanguageResource = new String[i_num];

        for (int li = 0; li < i_num; li++)
        {
            LB_as_LanguageNames[li] = ds.readUTF();
            LB_as_LanguageIDs[li] = ds.readUTF();
            LB_as_LanguageResource[li] = ds.readUTF();
            Runtime.getRuntime().gc();
        }
        ds.close();
        ds = null;
        is = null;

        if (_language_id < 0)
        {
            _language_id = LB_getIndexForID(LB_s_PhoneNativeLanguage);
        }
        if (_language_id >= 0) LB_setLanguage(_language_id); else LB_setLanguage(0);
        Runtime.getRuntime().gc();
        return LB_i_CurrentLanguageIndex;
    }
}
