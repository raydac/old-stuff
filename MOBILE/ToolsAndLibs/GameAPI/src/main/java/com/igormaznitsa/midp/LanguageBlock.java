package com.igormaznitsa.midp;

import java.io.*;

/**
 * This class implements language block
 */
public class LanguageBlock
{
    private String[] as_language_names;
    private String[] as_language_id;
    private String[] as_language_resource;
    private int i_currentindex = -1;
    private String s_phonenativelanguage;
    private String[] as_str_array;

    /**
     * This function loads texts from a text resource file
     * @param _resourcename Name of file contains text resources
     * @throws IOException
     */
    public void changeResource(String _resourcename) throws IOException
    {
        Runtime.getRuntime().gc();

        InputStream is = getClass().getResourceAsStream(_resourcename);
        DataInputStream ds = new DataInputStream(is);
        int i_num = ds.readUnsignedByte();
        String[] as_new_array = new String[i_num];

        for (int li = 0;li < i_num;li++)
        {
            as_new_array[li] = ds.readUTF();
            if (as_new_array[li].length() == 0)
                as_new_array[li] = null;
            else
                as_new_array[li] = as_new_array[li].replace('~','\n');
            Runtime.getRuntime().gc();
        }
        as_str_array = null;
        as_str_array = as_new_array;
        ds.close();
        ds = null;
        is = null;
        Runtime.getRuntime().gc();
    }

    /**
     * This function returns the array contains text strings
     * @return string array
     */
    public String[] getStringArray()
    {
        return as_str_array;
    }

    /**
     * Get list of language names
     * @return string array
     */
    public String[] getLanguageNames()
    {
        return as_language_names;
    }

    /**
     * Get list of language id
     * @return string array
     */
    public String[] getLanguageId()
    {
        return as_language_id;
    }

    /**
     * Get list of language resource files
     * @return string array
     */
    public String[] getLanguageResources()
    {
        return as_language_resource;
    }

    /**
     * Set new language as current
     * @param _index index of new language
     * @throws IOException throws if any error in downloading time
     */
    public void setLanguage(int _index) throws IOException
    {
        if (i_currentindex == _index || _index >= as_language_names.length || _index < 0) return;
        changeResource(as_language_resource[_index]);
        i_currentindex = _index;
    }

    /**
     * Get the index of current language
     * @return
     */
    public int getCurrentIndex()
    {
        return i_currentindex;
    }

    /**
     * Return index for id of a language
     * @param _lang_id
     * @return
     */
    public int getIndexForID(String _lang_id)
    {
        _lang_id = _lang_id.trim().toLowerCase();
        for (int li = 0;li < as_language_id.length;li++)
        {
            if (_lang_id.equals(as_language_id[li])) return li;
        }
        return -1;
    }

    /**
     * Constructor
     * @param _language_list Name of the file contains language information
     * @param _language_id Id for language what will be set after downloading or -1 if needs default language
     * @throws IOException
     */
    public LanguageBlock(String _language_list,int _language_id) throws IOException
    {
        s_phonenativelanguage = System.getProperty("microedition.locale").trim().toLowerCase().substring(0,2);
        Runtime.getRuntime().gc();

        InputStream is = getClass().getResourceAsStream(_language_list);
        DataInputStream ds = new DataInputStream(is);
        int i_num = ds.readUnsignedByte();
        as_language_names = new String[i_num];
        as_language_id = new String[i_num];
        as_language_resource = new String[i_num];

        for (int li = 0;li < i_num;li++)
        {
            as_language_names[li] = ds.readUTF();
            as_language_id[li] = ds.readUTF();
            as_language_resource[li] = ds.readUTF();
            Runtime.getRuntime().gc();
        }
        ds.close();
        ds = null;
        is = null;

        if (_language_id < 0)
        {
            _language_id = getIndexForID(s_phonenativelanguage);
        }
        if (_language_id >= 0) setLanguage(_language_id); else setLanguage(0);

        Runtime.getRuntime().gc();
    }
}
