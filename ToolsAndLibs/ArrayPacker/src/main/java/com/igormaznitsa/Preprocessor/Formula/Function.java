package com.igormaznitsa.Preprocessor.Formula;

import java.io.IOException;

public class Function
{
    private String s_name;

    public Function(String _name) throws IOException
    {
         if (!isFunction(_name)) throw new IOException("Unknown function name "+_name);
        s_name = _name;
    }

    private static final String [] as_Functions = new String[]
    {
       "abs",
       "round"
    };

    public static boolean isFunction(String _name)
    {
        for(int li=0;li<as_Functions.length;li++)
        {
            if (as_Functions[li].equals(_name)) return true;
        }
        return false;
    }

    public void process(FormulaStack _stack,int _index) throws IOException
    {
        if (s_name.equals("abs")) processABS(_stack,_index);
        else
        if (s_name.equals("round")) processROUND(_stack,_index);
        else
            throw new IOException("Unknow function "+s_name);
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
                    float f_result = Math.abs(((Float) _val0.getValue()).intValue());
                    _stack.setElementAt(new Value(Float.toString(f_result)),_index);
                };break;
            default :
                throw new IOException("Function ABS processes only the INTEGER or the FLOAT types");
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
                    int i_result = Math.round(((Float) _val0.getValue()).intValue());
                    _stack.setElementAt(new Value(Integer.toString(i_result)),_index);
                };break;
            default :
                throw new IOException("Function ROUND processes only the INTEGER or the FLOAT types");
        }

    }

    public String toString()
    {
        return s_name;
    }

}
