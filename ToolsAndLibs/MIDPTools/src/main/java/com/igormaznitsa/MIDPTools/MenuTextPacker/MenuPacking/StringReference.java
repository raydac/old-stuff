package com.igormaznitsa.MIDPTools.MenuTextPacker.MenuPacking;

import java.util.HashMap;
import java.io.IOException;

public class StringReference
{
    private static final char[][] ac_ReplacementChars = new char[][]
    {
        new char[]{'–', '-'},
        new char[]{'—', '-'},
        new char[]{'ё', 'е'},
        new char[]{'Ё', 'Е'},
        new char[]{0x2019, '\''}
    };

    public String s_id;
    public HashMap p_value;
    public int i_number;

    public StringReference(String _id, int _number)
    {
        s_id = _id;
        p_value = new HashMap();
        i_number = _number;
    }

    public boolean containsID(String _languageID)
    {
        if (p_value.containsKey(_languageID)) return true; else return false;
    }

    public void addValue(String _languageID, String _value) throws IOException
    {
        if (_value != null)
        {
            String s_oldvalue = _value;
            String s_pvalue = _value;
            for (int li = 0; li < ac_ReplacementChars.length; li++)
            {
                char[] ac_pair = ac_ReplacementChars[li];
                _value = _value.replace(ac_pair[0], ac_pair[1]);
                if (!_value.equals(s_pvalue))
                {
                    System.out.println("Used correction rule [" + li + "]");
                }
                s_pvalue = _value;
            }
            if (!_value.equals(s_oldvalue)) System.out.println("I have corrected a few mistakes in the string [" + _languageID + "," + s_id + "]");
        }
                                
        if (containsID(_languageID)) throw new IOException("Duplicated language ID [" + s_id + "]");
        p_value.put(_languageID, _value);
    }

    public String getValue(String _langId)
    {
        return (String) p_value.get(_langId);
    }

    public int hashCode()
    {
        return s_id.hashCode();
    }
}

