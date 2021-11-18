package com.igormaznitsa.WToolkit.classes;

import java.util.Vector;

public class StringArray extends Vector
{
    public StringArray()
    {
        super();
    }

    public void append(String [] _strings)
    {
        for(int li=0;li<_strings.length;li++)
        {
            add(_strings[li]);
        }
    }

    public String[] toStringArray()
    {
        String as_arr[] = new String[size()];

        for(int li=0;li<size();li++)
        {
            as_arr[li] = (String)elementAt(li);
        }

        return as_arr;
    }

    public String toString(String _startStr, String _delim, String _endStr, boolean _commas)
    {
        StringBuffer stringbuffer = new StringBuffer();
        stringbuffer.append(_startStr);
        for(int i = 0; i < size(); i++)
        {
            if(_commas)
                stringbuffer.append('"');
                stringbuffer.append((String)elementAt(i));
            if(_commas)
                stringbuffer.append('"');
            if(i != elementCount - 1)
                stringbuffer.append(_delim);
        }

        stringbuffer.append(_endStr);
        return stringbuffer.toString();
    }


}

