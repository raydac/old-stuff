import java.io.IOException;
import java.io.InputStream;
import java.io.DataInputStream;

// Внимание, блок содержит специфичный для приложения код и не может быть использован где либо без переработки
// транслит делается автоматически

public class LanguageBlock
{
    protected String[] as_LanguageNames;
    protected String[] as_LanguageIDs;
    protected String[] as_LanguageResource;
    protected int i_CurrentLanguageIndex = -1;
    protected String s_PhoneNativeLanguage;
    protected String[] as_TextStringArray;

    private String[] as_strArrTmp;

    private void changeResource(String _resourcename) throws IOException
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
        as_strArrTmp = new String[i_num];

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

                as_strArrTmp[li] = p_strBuf.toString();
                if (as_strArrTmp[li].length() == 0)
                    as_strArrTmp[li] = null;
                else
                    as_strArrTmp[li] = as_strArrTmp[li].replace('~','\n');
                Runtime.getRuntime().gc();
            }
        }
        else
        for (int li = 0;li < i_num;li++)
        {
            as_strArrTmp[li] = ds.readUTF();
            if (as_strArrTmp[li].length() == 0)
                as_strArrTmp[li] = null;
            else
                as_strArrTmp[li] = as_strArrTmp[li].replace('~','\n');
            Runtime.getRuntime().gc();
        }

        if (lg_convertToTranslit)
        {
            for(int li=0;li<as_strArrTmp.length;li++)
            {
                String s_str = as_strArrTmp[li];
                if (s_str!=null)
                {
                    s_str = SMSCatalog.russian2translit(s_str);
                    as_strArrTmp[li] = s_str;
                    s_str = null;
                }
            }
        }
        as_TextStringArray = null;
        as_TextStringArray = as_strArrTmp;
        as_strArrTmp = null;
        ds.close();
        ds = null;
        is = null;
        Runtime.getRuntime().gc();
    }

    protected void setLanguage(int _index) throws IOException
    {
        if (i_CurrentLanguageIndex == _index || _index >= as_LanguageNames.length || _index < 0) return;
        changeResource(as_LanguageResource[_index]);
        i_CurrentLanguageIndex = _index;
    }

    protected int getIndexForID(String _lang_id)
    {
        _lang_id = _lang_id.trim().toLowerCase();
        for (int li = 0;li < as_LanguageIDs.length;li++)
        {
            if (_lang_id.equals(as_LanguageIDs[li])) return li;
        }
        return -1;
    }

    protected void initLanguageBlock(String _language_list,int _language_id) throws IOException
    {
        String s_locale = System.getProperty("microedition.locale");
        if (s_locale == null)
            s_PhoneNativeLanguage = "ru";
        else
            s_PhoneNativeLanguage = s_locale.trim().toLowerCase().substring(0,2);


        Runtime.getRuntime().gc();

        InputStream is = getClass().getResourceAsStream(_language_list);
        DataInputStream ds = new DataInputStream(is);

        int i_num = ds.readUnsignedByte();

        as_LanguageNames = new String[i_num];
        as_LanguageIDs = new String[i_num];
        as_LanguageResource = new String[i_num];

        for (int li = 0;li < i_num;li++)
        {
            as_LanguageNames[li] = ds.readUTF();
            as_LanguageIDs[li] = ds.readUTF();
            as_LanguageResource[li] = ds.readUTF();
            Runtime.getRuntime().gc();
        }
        ds.close();
        ds = null;
        is = null;

        if (_language_id < 0)
        {
            _language_id = getIndexForID(s_PhoneNativeLanguage);
        }
        if (_language_id >= 0) setLanguage(_language_id); else setLanguage(0);

        Runtime.getRuntime().gc();
    }
}
