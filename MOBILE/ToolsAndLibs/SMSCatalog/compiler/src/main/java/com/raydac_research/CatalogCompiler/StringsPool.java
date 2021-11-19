package com.raydac_research.CatalogCompiler;

import java.util.Hashtable;
import java.util.Vector;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ByteArrayOutputStream;

public class StringsPool
{
    private static final char[][] ac_ReplacementChars = new char[][]
    {
        new char[]{'–', '-'},
        new char[]{'—', '-'},
        new char[]{'ё', 'е'},
        new char[]{'Ё', 'Е'},
        new char[]{'“', '"'},
        new char[]{'”', '"'},
        new char[]{'«', '"'},
        new char[]{'»', '"'},
        new char[]{0x2019, '\''}
    };

    public int getStringsNumber()
    {
        return p_Strings.size();
    }

    public static final String processString(String _value)
    {
        String p_value = _value;
        for (int li = 0; li < ac_ReplacementChars.length; li++)
        {
            char[] ac_pair = ac_ReplacementChars[li];
            _value = _value.replace(ac_pair[0], ac_pair[1]);
            if (!_value.equals(p_value))
            {
                System.out.println("Used correction rule [" + li + "]");
            }
            p_value = _value;

            _value = _value.replace("…","...");
        }
        return _value;
    }

    private class StringData
    {
        public String s_String;
        public int i_Index;
        public int i_DataOffset;

        public StringData(String _string,int _index)
        {
            s_String = _string;
            i_Index = _index;
        }

        public String toString()
        {
            return s_String+" ["+i_Index+"]";
        }

        public int hashCode()
        {
            return s_String.hashCode();
        }
    }

    private Hashtable p_StringsTable;
    private Vector p_Strings;

    public StringsPool()
    {
        p_StringsTable = new Hashtable();
        p_Strings = new Vector();
    }

    public int addString(String _string)
    {
        Object p_obj = p_StringsTable.get(_string);
        if (p_obj!=null)
        {
            return ((StringData)p_obj).i_Index;
        }
        StringData p_strData = new StringData(_string,p_Strings.size());
        p_Strings.add(p_strData);
        p_StringsTable.put(_string,p_strData);
        return p_strData.i_Index;
    }

    public int getOffsetForString(int _id)
    {
        return ((StringData) p_Strings.elementAt(_id)).i_DataOffset;
    }

    public byte [] toByteArray(boolean _saveAsBytes) throws IOException
    {
        ByteArrayOutputStream p_baos = new ByteArrayOutputStream(16000);
        DataOutputStream p_dos = new DataOutputStream(p_baos);

        int i_offset = 0;
        for(int li=0;li<p_Strings.size();li++)
        {
            StringData p_data = (StringData) p_Strings.elementAt(li);
            String s_data = processString(p_data.s_String);

            p_data.i_DataOffset = p_dos.size()-i_offset;


            //System.out.println("String: "+p_data.s_String+" ["+p_data.i_DataOffset+"]");


            if (_saveAsBytes)
            {
                byte [] ab_string = s_data.getBytes();

                p_dos.write(ab_string);
                p_dos.writeByte(0);
           }
            else
            {
                p_dos.writeUTF(s_data);
            }
        }
        p_dos.flush();
        p_dos.close();
        return p_baos.toByteArray();
    }
}
