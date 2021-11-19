package com.igormaznitsa.Preprocessor.Formula;

import java.io.IOException;

public class Value
{
    public static final int TYPE_STRING = 0;
    public static final int TYPE_BOOLEAN = 1;
    public static final int TYPE_INT = 2;
    public static final int TYPE_FLOAT = 3;
    public static final int TYPE_UNKNOW = 4;

    private Object p_Value;
    private int i_Type;

    public int getType()
    {
        return i_Type;
    }

    public Object getValue()
    {
        return p_Value;
    }

    public Value(String _value) throws IOException
    {
        i_Type = getType(_value);
        p_Value = getValue(_value, i_Type);
        if (p_Value == null) throw new IOException();
    }

    public static final Object getValue(String _value, int _type)
    {
        try
        {
            switch (_type)
            {
                case TYPE_STRING:
                    {
                        return _value.substring(1, _value.length() - 1);
                    }
                case TYPE_BOOLEAN:
                    {
                        _value = _value.toLowerCase();
                        if (_value.equals("true"))
                        {
                            return new Boolean(true);
                        }
                        else
                        {
                            return new Boolean(false);
                        }
                    }
                case TYPE_INT:
                    {
                        long l_value = -1;
                        if (_value.startsWith("0x"))
                        {
                            // HEX value
                            _value = _value.substring(2);
                            l_value = Long.parseLong(_value, 16);
                        }
                        else
                        {
                            // Decimal value
                            l_value = Long.parseLong(_value, 10);
                        }
                        return new Long(l_value);
                    }
                case TYPE_FLOAT:
                    {
                        float f_value = Float.parseFloat(_value);
                        return new Float(f_value);
                    }
                default :
                    return null;
            }
        }
        catch (NumberFormatException e)
        {
            return null;
        }
    }

    public static final int getType(String _value)
    {
        float f_value;
        long l_value;
        if (_value.toLowerCase().equals("true") || _value.toLowerCase().equals("false"))
        // Boolean
            return TYPE_BOOLEAN;
        else if (_value.startsWith("\"") && _value.endsWith("\""))
        // String value
            return TYPE_STRING;
        else
        {
            try
            {
                if (_value.indexOf('.') >= 0)
                {
                    // Float
                    f_value = Float.parseFloat(_value);
                    return TYPE_FLOAT;
                }
                else
                {
                    // Integer
                    if (_value.startsWith("0x"))
                    {
                        // HEX value
                        _value = _value.substring(2);
                        l_value = Long.parseLong(_value, 16);
                    }
                    else
                    {
                        // Decimal value
                        l_value = Long.parseLong(_value, 10);
                    }
                    return TYPE_INT;
                }
            }
            catch (NumberFormatException e)
            {
                return TYPE_UNKNOW;
            }
        }
    }

    public String toStringDetail()
    {
        switch (i_Type)
        {
            case TYPE_BOOLEAN:
                {
                    return "Boolean : " + ((Boolean) p_Value).booleanValue();
                }
            case TYPE_INT:
                {
                    return "Integer : " + ((Long) p_Value).longValue();
                }
            case TYPE_UNKNOW:
                {
                    return "Unknown : -";
                }
            case TYPE_FLOAT:
                {
                    return "Float : " + ((Float) p_Value).floatValue();
                }
            case TYPE_STRING:
                {
                    return "String : " + (String) p_Value;
                }
        }
        return "!!! ERROR , UNSUPPORTED TYPE [" + i_Type + "]";
    }

    public String toString()
    {
        switch (i_Type)
        {
            case TYPE_BOOLEAN:
                {
                    return "" + ((Boolean) p_Value).booleanValue();
                }
            case TYPE_INT:
                {
                    return "" + ((Long) p_Value).longValue();
                }
            case TYPE_UNKNOW:
                {
                    return "";
                }
            case TYPE_FLOAT:
                {
                    return "" + ((Float) p_Value).floatValue();
                }
            case TYPE_STRING:
                {
                    return "" + (String) p_Value;
                }
        }
        return "!!! ERROR , UNSUPPORTED TYPE [" + i_Type + "]";
    }
}
