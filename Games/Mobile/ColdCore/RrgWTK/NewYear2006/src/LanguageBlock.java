import java.io.IOException;
import java.io.InputStream;
import java.io.DataInputStream;

// Внимание, блок содержит специфичный для приложения код и не может быть использован где либо без переработки
// транслит делается автоматически

public class LanguageBlock
{
    protected String[] LB_as_LanguageNames;
    protected String[] LB_as_LanguageIDs;
    protected String[] LB_as_LanguageResource;
    protected int LB_i_CurrentLanguageIndex = -1;
    protected String LB_s_PhoneNativeLanguage;
    protected String[] LB_as_TextStringArray;

    private void LB_changeResource(String _resourcename) throws IOException
    {
        boolean lg_convertToTranslit = false;
        if (_resourcename.equals("/translit.bin"))
        {
            _resourcename = "/russian.bin";
            lg_convertToTranslit = true;
        }

        Runtime.getRuntime().gc();

        InputStream is = getClass().getResourceAsStream(_resourcename);
        DataInputStream ds = new DataInputStream(is);

        int i_unicodeCharOffset = ds.readUnsignedShort();

        int i_num = ds.readUnsignedByte();
        String[] as_new_array = new String[i_num];

        StringBuffer p_strBuf = new StringBuffer(128);
        if (i_unicodeCharOffset!=0)
        {
            for (int li = 0;li < i_num;li++)
            {
                p_strBuf.delete(0,p_strBuf.length());

                while(true)
                {
                    int i_char = ds.readUnsignedByte();
                    if (i_char==0) break;
                    if (i_char >= 0x80) i_char += i_unicodeCharOffset;
                    p_strBuf.append((char)i_char);
                }

                as_new_array[li] = p_strBuf.toString();
                if (as_new_array[li].length() == 0)
                    as_new_array[li] = null;
                else
                    as_new_array[li] = as_new_array[li].replace('~','\n');
                Runtime.getRuntime().gc();
            }
        }
        else
        for (int li = 0;li < i_num;li++)
        {
            as_new_array[li] = ds.readUTF();
            if (as_new_array[li].length() == 0)
                as_new_array[li] = null;
            else
                as_new_array[li] = as_new_array[li].replace('~','\n');
            Runtime.getRuntime().gc();
        }

        if (lg_convertToTranslit)
        {
            for(int li=0;li<as_new_array.length;li++)
            {
                String s_str = as_new_array[li];
                if (s_str!=null)
                {
                    s_str = SMSCatalog.russian2translit(s_str);
                    as_new_array[li] = s_str;
                    s_str = null;
                }
            }
        }
        LB_as_TextStringArray = null;
        LB_as_TextStringArray = as_new_array;
        ds.close();
        ds = null;
        is = null;
        Runtime.getRuntime().gc();
    }

    protected void LB_setLanguage(int _index) throws IOException
    {
        if (LB_i_CurrentLanguageIndex == _index || _index >= LB_as_LanguageNames.length || _index < 0) return;
        LB_changeResource(LB_as_LanguageResource[_index]);
        LB_i_CurrentLanguageIndex = _index;
    }

    protected int LB_getIndexForID(String _lang_id)
    {
        _lang_id = _lang_id.trim().toLowerCase();
        for (int li = 0;li < LB_as_LanguageIDs.length;li++)
        {
            if (_lang_id.equals(LB_as_LanguageIDs[li])) return li;
        }
        return -1;
    }

    protected void LB_initLanguageBlock(String _language_list,int _language_id) throws IOException
    {
        String s_locale = System.getProperty("microedition.locale");
        if (s_locale == null)
            LB_s_PhoneNativeLanguage = "ru";
        else
            LB_s_PhoneNativeLanguage = s_locale.trim().toLowerCase().substring(0,2);


        Runtime.getRuntime().gc();

        InputStream is = getClass().getResourceAsStream(_language_list);
        DataInputStream ds = new DataInputStream(is);

        int i_num = ds.readUnsignedByte();

        LB_as_LanguageNames = new String[i_num];
        LB_as_LanguageIDs = new String[i_num];
        LB_as_LanguageResource = new String[i_num];

        for (int li = 0;li < i_num;li++)
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
    }
}
