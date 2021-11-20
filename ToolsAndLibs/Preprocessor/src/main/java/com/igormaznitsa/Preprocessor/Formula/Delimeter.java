package com.igormaznitsa.Preprocessor.Formula;

public class Delimeter
{
    private String s_delimeter = null;

    public Delimeter(String _delimeters)
    {
        s_delimeter = _delimeters;
    }

    public static boolean isDelimeter(Delimeter [] _delimeters,String _value)
    {
        if (_delimeters == null) return false;

        for(int li=0;li<_delimeters.length;li++) if (_delimeters[li].equals(_value)) return true;

        return false;
    }

    public static Delimeter getDelimeterForValue(Delimeter [] _delimeters,String _value)
    {
        if (_delimeters == null) return null;

        for(int li=0;li<_delimeters.length;li++) if (_delimeters[li].equals(_value)) return _delimeters[li];

        return null;
    }

    public boolean equals(Object _obj)
    {
        if (_obj == null|| s_delimeter == null) return false;
        if (_obj instanceof String)
        {
            String s_obj = (String) _obj;
            return s_obj.equals(s_delimeter);
        }
        else
        if (_obj instanceof Delimeter)
        {
            Delimeter p_delim = (Delimeter) _obj;
            return p_delim.s_delimeter.equals(s_delimeter);
        }
        return false;
    }

    public String toString()
    {
        return "DELIMETER \'"+s_delimeter+"\'";
    }

}
