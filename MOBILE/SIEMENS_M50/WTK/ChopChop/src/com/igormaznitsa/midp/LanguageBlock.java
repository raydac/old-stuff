package com.igormaznitsa.midp;

import java.io.*;
import com.igormaznitsa.GameAPI.LoadListener;

/**
 * This class implements language block
 */
public class LanguageBlock
{
    private String [] as_language_names;
    private String [] as_language_id;
    private String [] as_language_resource;
    private TextBlock p_textblock;
    private int i_currentindex;
    private String s_phonenativelanguage;

    /**
     * Get list of language names
     * @return string array
     */
    public String [] getLanguageNames()
    {
        return as_language_names;
    }

    /**
     * Get list of language id
     * @return string array
     */
    public String [] getLanguageId()
    {
        return as_language_id;
    }

    /**
     * Get list of language resource files
     * @return string array
     */
    public String [] getLanguageResources()
    {
        return as_language_resource;
    }

    /**
     * Set new language as current
     * @param _index index of new language
     * @param _loadListener pointer to a load listener
     * @throws IOException throws if any error in downloading time
     */
    public void setLanguage(int _index,LoadListener _loadListener) throws IOException
    {
        if (p_textblock==null)
        {
            p_textblock = new TextBlock(as_language_resource[_index],_loadListener);
        }
        else
        {
            if (i_currentindex==_index || _index>=as_language_names.length || _index<0) return;
            p_textblock.changeResource(as_language_resource[_index],_loadListener);
        }
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
     * Return pointer to the string array
     * @return string array
     */
    public String [] getCurrentStringArray()
    {
        return p_textblock.getStringArray();
    }

    /**
     * Return index for id of a language
     * @param _lang_id
     * @return
     */
    public int getIndexForID(String _lang_id)
    {
        _lang_id = _lang_id.trim().toLowerCase();
        for(int li=0;li<as_language_id.length;li++)
        {
            if (_lang_id.equals(as_language_id[li])) return li;
        }
        return -1;
    }

    /**
     * Constructor
     * @param _language_list Name of the file contains language information
     * @param _language_id Id for language what will be set after downloading or -1 if needs default language
     * @param _loadListener link toa a load listener
     * @throws IOException
     */
    public LanguageBlock(String _language_list,int _language_id,LoadListener _loadListener) throws IOException
    {
        s_phonenativelanguage = System.getProperty("microedition.locale").trim().toLowerCase();
        Runtime.getRuntime().gc();

        InputStream is = getClass().getResourceAsStream(_language_list);
        DataInputStream ds = new DataInputStream(is);
        int i_num = ds.readUnsignedByte();
        as_language_names = new String[i_num];
        as_language_id = new String[i_num];
        as_language_resource = new String[i_num];

        for(int li=0;li<i_num;li++)
        {
            as_language_names[li] = ds.readUTF();
            as_language_id[li] = ds.readUTF();
            as_language_resource[li] = ds.readUTF();
	    if (_loadListener != null) _loadListener.nextItemLoaded(1);
            Runtime.getRuntime().gc();
        }
        ds.close();
        ds = null;
        is = null;

        if (_language_id<0)
        {
            _language_id = getIndexForID(s_phonenativelanguage);
        }
        if (_language_id>=0) setLanguage(_language_id,_loadListener); else setLanguage(0,_loadListener);

        Runtime.getRuntime().gc();
    }
}