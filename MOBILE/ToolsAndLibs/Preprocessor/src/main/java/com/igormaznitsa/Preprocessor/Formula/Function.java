package com.igormaznitsa.Preprocessor.Formula;

import com.igormaznitsa.Preprocessor.PreprocessorActionListener;

import java.io.IOException;

public class Function
{
    private static final String FUNCTION_ABS = "abs";
    private static final String FUNCTION_ROUND = "round";
    private static final String FUNCTION_STR2WEB = "str2web";
    private static final String FUNCTION_STRLEN = "strlen";
    private static final String FUNCTION_STR2INT = "str2int";

    private static final String FUNCTION_XML_OPEN = "xml_open";
    private static final String FUNCTION_XML_GETDOCUMENTELEMENT = "xml_getdocumentelement";
    private static final String FUNCTION_XML_GETELEMENTNAME = "xml_getelementname";
    private static final String FUNCTION_XML_GETELEMENTSFORNAME = "xml_getelementsforname";
    private static final String FUNCTION_XML_ELEMENTSNUMBER = "xml_elementsnumber";
    private static final String FUNCTION_XML_ELEMENTAT = "xml_elementat";
    private static final String FUNCTION_XML_GETATTRIBUTE = "xml_getattribute";
    private static final String FUNCTION_XML_GETELEMENTTEXT = "xml_getelementtext";

    private String s_name;
    public int i_argsNumber;
    public boolean lg_UserFunction;
    public String s_userFunctionName;

    public Function(String _name) throws IOException
    {
         if (!isFunction(_name)) throw new IOException("Unknown function name "+_name);
        s_name = _name;
        i_argsNumber = 1;

        if (_name.startsWith("$"))
        {
            lg_UserFunction = true;
            s_userFunctionName = _name.substring(1);
            i_argsNumber = -1;
        }
    }

    private static final String [] as_Functions = new String[]
    {
       FUNCTION_ABS,
       FUNCTION_ROUND,
       FUNCTION_STR2WEB,
       FUNCTION_STRLEN,
       FUNCTION_STR2INT,
       FUNCTION_XML_GETATTRIBUTE,
       FUNCTION_XML_ELEMENTAT,
       FUNCTION_XML_GETDOCUMENTELEMENT,
       FUNCTION_XML_GETELEMENTNAME,
       FUNCTION_XML_GETELEMENTSFORNAME,
       FUNCTION_XML_ELEMENTSNUMBER,
       FUNCTION_XML_GETELEMENTTEXT,
       FUNCTION_XML_OPEN,
    };

    public static boolean isFunction(String _name)
    {
        if (_name.startsWith("$")) return true;
        for(int li=0;li<as_Functions.length;li++)
        {
            if (as_Functions[li].equals(_name)) return true;
        }
        return false;
    }

    public void process(FormulaStack _stack,PreprocessorActionListener _actionListener,int _index) throws IOException
    {
        if (lg_UserFunction)
        {
            if (_actionListener == null) throw new IOException("You must defined an action listener for user defined functions [\""+s_name+"\"]");
            processDEF(_stack,_index,_actionListener);
        }
        else
        {
            if (s_name.equals(FUNCTION_ABS)) processABS(_stack,_index);
            else
            if (s_name.equals(FUNCTION_ROUND)) processROUND(_stack,_index);
            else
            if (s_name.equals(FUNCTION_STR2INT)) processSTR2INT(_stack,_index);
            else
            if (s_name.equals(FUNCTION_STR2WEB)) processSTR2WEB(_stack,_index);
            else
            if (s_name.equals(FUNCTION_STRLEN)) processSTRLEN(_stack,_index);
            else
            if (s_name.equals(FUNCTION_XML_GETATTRIBUTE)) XMLpackage.processXML_GETATTRIBUTE(_stack,_index);
            else
            if (s_name.equals(FUNCTION_XML_ELEMENTAT)) XMLpackage.processXML_ELEMENTAT(_stack,_index);
            else
            if (s_name.equals(FUNCTION_XML_OPEN)) XMLpackage.processXML_OPEN(_stack,_index);
            else
            if (s_name.equals(FUNCTION_XML_GETELEMENTNAME)) XMLpackage.processXML_GETELEMENTNAME(_stack,_index);
            else
            if (s_name.equals(FUNCTION_XML_GETDOCUMENTELEMENT)) XMLpackage.processXML_GETDOCUMENTELEMENT(_stack,_index);
            else
            if (s_name.equals(FUNCTION_XML_GETELEMENTSFORNAME)) XMLpackage.processXML_GETELEMENTSFORNAME(_stack,_index);
            else
            if (s_name.equals(FUNCTION_XML_ELEMENTSNUMBER)) XMLpackage.processXML_ELEMENTSNUMBER(_stack,_index);
            else
            if (s_name.equals(FUNCTION_XML_GETELEMENTTEXT)) XMLpackage.processXML_GETELEMENTTEXT(_stack,_index);
        }
    }

    private void processDEF(FormulaStack _stack,int _index,PreprocessorActionListener _actionListener) throws IOException
    {
        Value [] ap_values = new Value[i_argsNumber];

        int i_arg = i_argsNumber;
        while(i_arg>0)
        {
            try
            {
                if (_stack.size()==0) throw new Exception();
                Object p_obj = _stack.elementAt(_index-1);
                _index--;
                _stack.removeElementAt(_index);

                if (p_obj instanceof Value)
                {
                    ap_values[i_arg-1] = (Value)p_obj;
                }
                else
                    throw new Exception();
            }
            catch(Exception _ex)
            {
                throw new IOException("You have wrong arguments number for \""+s_name+"\" function, must be "+i_argsNumber);
            }

            i_arg--;
        }

        Value p_value = _actionListener.processUserFunction(s_userFunctionName,ap_values);
        if (p_value == null) throw new IOException("User defined function \""+s_userFunctionName+"\" has returned NULL");

        _stack.setElementAt(p_value,_index);
    }

    // Обработка функции ABS
    private static void processABS(FormulaStack _stack,int _index) throws IOException
    {
        if (!_stack.isOnePreviousItemValue(_index)) throw new IOException("Operation ABS needs an operand");

        Value _val0 = (Value)_stack.elementAt(_index-1);
        _index--;
        _stack.removeElementAt(_index);

        switch (_val0.getType())
        {
            case Value.TYPE_INT:
                {
                    long l_result = Math.abs(((Long) _val0.getValue()).longValue());
                    _stack.setElementAt(new Value(Long.toString(l_result)),_index);
                };break;
            case Value.TYPE_FLOAT:
                {
                    float f_result = Math.abs(((Float) _val0.getValue()).floatValue());
                    _stack.setElementAt(new Value(Float.toString(f_result)),_index);
                };break;
            default :
                throw new IOException("Function ABS processes only the INTEGER or the FLOAT types");
        }

    }

    // Обработка функции STRLEN
    private static void processSTRLEN(FormulaStack _stack,int _index) throws IOException
    {
        if (!_stack.isOnePreviousItemValue(_index)) throw new IOException("Operation STRLEN needs an operand");

        Value _val0 = (Value)_stack.elementAt(_index-1);
        _index--;
        _stack.removeElementAt(_index);

        switch (_val0.getType())
        {
            case Value.TYPE_STRING:
                {
                    String s_result = (String) _val0.getValue();
                    long l_len = s_result.length();
                    _stack.setElementAt(new Value(Long.toString(l_len)),_index);
                };break;
            default :
                throw new IOException("Function STRLEN processes only the STRING types");
        }

    }

    // Обработка функции STR2WEB
    private static void processSTR2WEB(FormulaStack _stack,int _index) throws IOException
    {
        if (!_stack.isOnePreviousItemValue(_index)) throw new IOException("Operation STR2WEB needs an operand");

        Value p_val = (Value)_stack.elementAt(_index-1);
        _stack.removeElementAt(_index);

        switch (p_val.getType())
        {
            case Value.TYPE_STRING:
                {
                    String s_result = (String) p_val.getValue();

                    StringBuffer p_strBuffer = new StringBuffer(s_result.length()<<1);

                    int i_strLen = s_result.length();
                    for(int li=0;li<i_strLen;li++)
                    {
                        char ch_char = s_result.charAt(li);

                        switch(ch_char)
                        {
                                case '&' : p_strBuffer.append("&amp;");break;
                                case ' ' : p_strBuffer.append("&nbsp;");break;
                                case '<' : p_strBuffer.append("&lt;");break;
                                case '>' : p_strBuffer.append("&gt;");break;
                                case '\"': p_strBuffer.append("&quot;");break;
                                case '€': p_strBuffer.append("&euro;");break;
                                case '©': p_strBuffer.append("&copy;");break;
                                case '¤': p_strBuffer.append("&curren;");break;
                                case '«': p_strBuffer.append("&laquo;");break;
                                case '»': p_strBuffer.append("&raquo;");break;
                                case '®': p_strBuffer.append("&reg;");break;
                                case '§': p_strBuffer.append("&sect;");break;
                                case '™': p_strBuffer.append("&trade;");break;
                                default:
                                {
                                    p_strBuffer.append(ch_char);
                                }
                        }
                    }


                    p_val.setValue(p_strBuffer.toString());
                };break;
            default :
                throw new IOException("Function STR2WEB processes only the STRING type");
        }

    }
    // Обработка функции STR2INT
    private static void processSTR2INT(FormulaStack _stack,int _index) throws IOException
    {
        if (!_stack.isOnePreviousItemValue(_index)) throw new IOException("Operation STR2INT needs an operand");

        Value _val0 = (Value)_stack.elementAt(_index-1);
        _index--;
        _stack.removeElementAt(_index);

        switch (_val0.getType())
        {
            case Value.TYPE_STRING:
                {
                    String s_result = (String) _val0.getValue();

                    long l_value = 0;

                    try
                    {
                        l_value = Long.parseLong(s_result);
                    } catch (NumberFormatException e)
                    {
                        throw new IOException("I can't convert value ["+s_result+']');
                    }
                   _stack.setElementAt(new Value(new Long(l_value)),_index);
                };break;
            default :
                throw new IOException("Function STR2INT processes only the STRING type");
        }

    }

   // Обработка функции ROUND
    private static void processROUND(FormulaStack _stack,int _index) throws IOException
    {
        if (!_stack.isOnePreviousItemValue(_index)) throw new IOException("Operation ROUND needs an operand");

        Value _val0 = (Value)_stack.elementAt(_index-1);
        _index--;
        _stack.removeElementAt(_index);

        switch (_val0.getType())
        {
            case Value.TYPE_INT:
                {
                    _stack.setElementAt(_val0,_index);
                };break;
            case Value.TYPE_FLOAT:
                {
                    long l_result = Math.round(((Float) _val0.getValue()).longValue());
                    _stack.setElementAt(new Value(Long.toString(l_result)),_index);
                };break;
            default :
                throw new IOException("Function ROUND processes only the INTEGER or the FLOAT types");
        }

    }

    public String toString()
    {
        return "FUNCTION \'"+s_name+"\'";
    }
}
