package com.igormaznitsa.Preprocessor.Formula;

import com.igormaznitsa.Preprocessor.PreprocessorActionListener;

import java.io.IOException;
import java.util.HashMap;

public class Operation
{
    private static final int OP_AND = 0;
    private static final int OP_OR = 1;
    private static final int OP_NOT = 2;
    private static final int OP_XOR = 3;
    private static final int OP_EQU = 4;
    private static final int OP_NEQU = 5;
    private static final int OP_LESS = 6;
    private static final int OP_MORE = 7;
    private static final int OP_LESSEQU = 8;
    private static final int OP_MOREEQU = 9;
    private static final int OP_ADD = 10;
    private static final int OP_SUB = 11;
    private static final int OP_DIV = 12;
    private static final int OP_MUL = 13;
    private static final int OP_MOD = 14;

    private static final int OP_LEFTBRACKET = 15;
    private static final int OP_RIGHTBRACKET = 16;

    private int i_OperationCode;
    private int i_Priority;
    private String s_String;

    private static final String[] as_unaryOperations = new String[]{"!"};

    private static final int PRIORITY_OPEN_BRACKET = 7;
    private static final int PRIORITY_CLOSE_BRACKET = 8;
    private static final int PRIORITY_FUNCTION = 5;
    private static final int PRIORITY_VALUE = 6;


    private static final Object[][] ap_operations = new Object[][]
    {
        new Object[]{new Integer(0), new String[]{"&&", "||", "^"}},
        new Object[]{new Integer(1), new String[]{"==", "!=", "<", ">", "<=", ">="}},
        new Object[]{new Integer(2), new String[]{"+", "-"}},
        new Object[]{new Integer(3), new String[]{"/", "*", "%"}},
        new Object[]{new Integer(4), new String[]{"!"}},
        new Object[]{new Integer(PRIORITY_OPEN_BRACKET), new String[]{"("}},
        new Object[]{new Integer(PRIORITY_CLOSE_BRACKET), new String[]{")"}}
    };

    private static boolean isUnary(String _str)
    {
        for (int li = 0; li < as_unaryOperations.length; li++)
        {
            if (_str.equals(as_unaryOperations[li])) return true;
        }
        return false;
    }

    public int getPriorityForOpeation(String _op)
    {
        for (int li = 0; li < ap_operations.length; li++)
        {
            int i_prior = ((Integer) ap_operations[li][0]).intValue();

            String[] as_ops = (String[]) ap_operations[li][1];
            for (int lx = 0; lx < as_ops.length; lx++)
            {
                if (as_ops[lx].equals(_op)) return i_prior;
            }
        }
        return -1;
    }

    public int getOpCodeForOperation(String _op) throws IOException
    {
        int i_op;
        if (_op.equals("&&"))
            i_op = OP_AND;
        else if (_op.equals("||"))
            i_op = OP_OR;
        else if (_op.equals("!"))
            i_op = OP_NOT;
        else if (_op.equals("^"))
            i_op = OP_XOR;
        else if (_op.equals("=="))
            i_op = OP_EQU;
        else if (_op.equals("!="))
            i_op = OP_NEQU;
        else if (_op.equals("<"))
            i_op = OP_LESS;
        else if (_op.equals(">"))
            i_op = OP_MORE;
        else if (_op.equals("<="))
            i_op = OP_LESSEQU;
        else if (_op.equals(">="))
            i_op = OP_MOREEQU;
        else if (_op.equals("+"))
            i_op = OP_ADD;
        else if (_op.equals("-"))
            i_op = OP_SUB;
        else if (_op.equals("/"))
            i_op = OP_DIV;
        else if (_op.equals("*"))
            i_op = OP_MUL;
        else if (_op.equals("%"))
            i_op = OP_MOD;
        else if (_op.equals("("))
            i_op = OP_LEFTBRACKET;
        else if (_op.equals(")"))
            i_op = OP_RIGHTBRACKET;
        else
            throw new IOException("Unknown operation [" + _op + "]");

        return i_op;
    }

    public Operation(String _str) throws IOException
    {
        i_OperationCode = getOpCodeForOperation(_str);
        i_Priority = getPriorityForOpeation(_str);
        s_String = _str;
    }

    public int getOperationCode()
    {
        return i_OperationCode;
    }

    public int getOperationPriority()
    {
        return i_Priority;
    }

    // ===================Boolean functions======================================
    // &&
    private static void operationAND(FormulaStack _stack, int _index) throws IOException
    {
        if (!_stack.isTwoPreviousItemsValues(_index)) throw new IOException("Operation \'&&\' needs two operands");

        Value _val0 = (Value) _stack.elementAt(_index - 2);
        Value _val1 = (Value) _stack.elementAt(_index - 1);

        _index -= 2;
        _stack.removeElementAt(_index);
        _stack.removeElementAt(_index);

        switch (_val0.getType())
        {
            case Value.TYPE_BOOLEAN:
                {
                    if (_val1.getType() == Value.TYPE_BOOLEAN)
                    {
                        boolean lg_result = ((Boolean) _val0.getValue()).booleanValue() && ((Boolean) _val1.getValue()).booleanValue();
                        _stack.setElementAt(new Value(Boolean.toString(lg_result)), _index);
                    }
                }
                ;
                break;
            case Value.TYPE_INT:
                {
                    if (_val1.getType() == Value.TYPE_INT)
                    {
                        long i_result = ((Long) _val0.getValue()).longValue() & ((Long) _val1.getValue()).longValue();
                        _stack.setElementAt(new Value(Long.toString(i_result)), _index);
                    }
                }
                ;
                break;
            default :
                throw new IOException("Operation && processes only the BOOLEAN or the INTEGER types");
        }
    }

    // ||
    private static void operationOR(FormulaStack _stack, int _index) throws IOException
    {
        if (!_stack.isTwoPreviousItemsValues(_index)) throw new IOException("Operation \'||\' needs two operands");

        Value _val0 = (Value) _stack.elementAt(_index - 2);
        Value _val1 = (Value) _stack.elementAt(_index - 1);

        _index -= 2;
        _stack.removeElementAt(_index);
        _stack.removeElementAt(_index);

        switch (_val0.getType())
        {
            case Value.TYPE_BOOLEAN:
                {
                    if (_val1.getType() == Value.TYPE_BOOLEAN)
                    {
                        boolean lg_result = ((Boolean) _val0.getValue()).booleanValue() || ((Boolean) _val1.getValue()).booleanValue();
                        _stack.setElementAt(new Value(Boolean.toString(lg_result)), _index);
                    }
                }
                ;
                break;
            case Value.TYPE_INT:
                {
                    if (_val1.getType() == Value.TYPE_INT)
                    {
                        long i_result = ((Long) _val0.getValue()).longValue() | ((Long) _val1.getValue()).longValue();
                        _stack.setElementAt(new Value(Long.toString(i_result)), _index);
                    }
                }
                ;
                break;
            default :
                throw new IOException("Operation || processes only the BOOLEAN or the INTEGER types");
        }
    }

    // ^
    private static void operationXOR(FormulaStack _stack, int _index) throws IOException
    {
        if (!_stack.isTwoPreviousItemsValues(_index)) throw new IOException("Operation \'^\' needs two operands");

        Value _val0 = (Value) _stack.elementAt(_index - 2);
        Value _val1 = (Value) _stack.elementAt(_index - 1);

        _index -= 2;
        _stack.removeElementAt(_index);
        _stack.removeElementAt(_index);

        switch (_val0.getType())
        {
            case Value.TYPE_BOOLEAN:
                {
                    if (_val1.getType() == Value.TYPE_BOOLEAN)
                    {
                        boolean lg_result = ((Boolean) _val0.getValue()).booleanValue() ^ ((Boolean) _val1.getValue()).booleanValue();
                        _stack.setElementAt(new Value(Boolean.toString(lg_result)), _index);
                    }
                }
                ;
                break;
            case Value.TYPE_INT:
                {
                    if (_val1.getType() == Value.TYPE_INT)
                    {
                        long i_result = ((Long) _val0.getValue()).longValue() ^ ((Long) _val1.getValue()).longValue();
                        _stack.setElementAt(new Value(Long.toString(i_result)), _index);
                    }
                }
                ;
                break;
            default :
                throw new IOException("Operation \'^\' processes only the BOOLEAN or the INTEGER types");
        }
    }

    // !
    private static void operationNOT(FormulaStack _stack, int _index) throws IOException
    {
        if (!_stack.isOnePreviousItemValue(_index)) throw new IOException("Operation ! needs an operand");

        Value _val0 = (Value) _stack.elementAt(_index - 1);
        _index--;
        _stack.removeElementAt(_index);

        switch (_val0.getType())
        {
            case Value.TYPE_BOOLEAN:
                {
                    boolean lg_result = !((Boolean) _val0.getValue()).booleanValue();
                    _stack.setElementAt(new Value(Boolean.toString(lg_result)), _index);
                }
                ;
                break;
            case Value.TYPE_INT:
                {
                    long i_result = 0xFFFFFFFF ^ ((Long) _val0.getValue()).longValue();
                    _stack.setElementAt(new Value(Long.toString(i_result)), _index);
                }
                ;
                break;
            default :
                throw new IOException("Operation ! processes only the BOOLEAN or the INTEGER types");
        }
    }
// ==========================================================================

// ===================Compare functions======================================
    //==
    private static void operationEQU(FormulaStack _stack, int _index) throws IOException
    {
        if (!_stack.isTwoPreviousItemsValues(_index)) throw new IOException("Operation \'==\' needs two operands");

        Value _val0 = (Value) _stack.elementAt(_index - 2);
        Value _val1 = (Value) _stack.elementAt(_index - 1);

        _index -= 2;
        _stack.removeElementAt(_index);
        _stack.removeElementAt(_index);


        if (_val0.getType() != _val1.getType()) throw new IOException("Incompatible types in \"==\" operation");

        switch (_val0.getType())
        {
            case Value.TYPE_BOOLEAN:
                {
                    boolean lg_result = ((Boolean) _val0.getValue()).booleanValue() == ((Boolean) _val1.getValue()).booleanValue();
                    _stack.setElementAt(new Value(Boolean.toString(lg_result)), _index);
                }
                ;
                break;
            case Value.TYPE_FLOAT:
                {
                    boolean lg_result = ((Float) _val0.getValue()).floatValue() == ((Float) _val1.getValue()).floatValue();
                    _stack.setElementAt(new Value(Boolean.toString(lg_result)), _index);
                }
                ;
                break;
            case Value.TYPE_INT:
                {
                    boolean lg_result = ((Long) _val0.getValue()).longValue() == ((Long) _val1.getValue()).longValue();
                    _stack.setElementAt(new Value(Boolean.toString(lg_result)), _index);
                }
                ;
                break;
            case Value.TYPE_STRING:
                {
                    boolean lg_result = ((String) _val0.getValue()).equals((String) _val1.getValue());
                    _stack.setElementAt(new Value(Boolean.toString(lg_result)), _index);
                }
                ;
                break;
        }
    }

    //!=
    private static void operationNotEQU(FormulaStack _stack, int _index) throws IOException
    {
        if (!_stack.isTwoPreviousItemsValues(_index)) throw new IOException("Operation \'!=\' needs two operands");

        Value _val0 = (Value) _stack.elementAt(_index - 2);
        Value _val1 = (Value) _stack.elementAt(_index - 1);

        _index -= 2;
        _stack.removeElementAt(_index);
        _stack.removeElementAt(_index);

        if (_val0.getType() != _val1.getType()) throw new IOException("Incompatible types in \"!=\" operation");

        switch (_val0.getType())
        {
            case Value.TYPE_BOOLEAN:
                {
                    boolean lg_result = ((Boolean) _val0.getValue()).booleanValue() != ((Boolean) _val1.getValue()).booleanValue();
                    _stack.setElementAt(new Value(Boolean.toString(lg_result)), _index);
                }
                ;
                break;
            case Value.TYPE_FLOAT:
                {
                    boolean lg_result = ((Float) _val0.getValue()).floatValue() != ((Float) _val1.getValue()).floatValue();
                    _stack.setElementAt(new Value(Boolean.toString(lg_result)), _index);
                }
                ;
                break;
            case Value.TYPE_INT:
                {
                    boolean lg_result = ((Long) _val0.getValue()).longValue() != ((Long) _val1.getValue()).longValue();
                    _stack.setElementAt(new Value(Boolean.toString(lg_result)), _index);
                }
                ;
                break;
            case Value.TYPE_STRING:
                {
                    boolean lg_result = !(((String) _val0.getValue()).equals((String) _val1.getValue()));
                    _stack.setElementAt(new Value(Boolean.toString(lg_result)), _index);
                }
                ;
                break;
        }
    }

    //<
    private static void operationLess(FormulaStack _stack, int _index) throws IOException
    {
        if (!_stack.isTwoPreviousItemsValues(_index)) throw new IOException("Operation \'<\' needs two operands");

        Value _val0 = (Value) _stack.elementAt(_index - 2);
        Value _val1 = (Value) _stack.elementAt(_index - 1);

        _index -= 2;
        _stack.removeElementAt(_index);
        _stack.removeElementAt(_index);

        if (_val0.getType() != _val1.getType()) throw new IOException("Incompatible types in \"<\" operation");

        switch (_val0.getType())
        {
            case Value.TYPE_BOOLEAN:
                {
                    throw new IOException("Operation \"<\" doesn't work with BOOLEAN types");
                }
            case Value.TYPE_FLOAT:
                {
                    boolean lg_result = ((Float) _val0.getValue()).floatValue() < ((Float) _val1.getValue()).floatValue();
                    _stack.setElementAt(new Value(Boolean.toString(lg_result)), _index);
                }
                ;
                break;
            case Value.TYPE_INT:
                {
                    boolean lg_result = ((Long) _val0.getValue()).longValue() < ((Long) _val1.getValue()).longValue();
                    _stack.setElementAt(new Value(Boolean.toString(lg_result)), _index);
                }
                ;
                break;
            case Value.TYPE_STRING:
                {
                    boolean lg_result = ((String) _val0.getValue()).length() < ((String) _val1.getValue()).length();
                    _stack.setElementAt(new Value(Boolean.toString(lg_result)), _index);
                }
                ;
                break;
        }
    }

    //<=
    private static void operationLessEQU(FormulaStack _stack, int _index) throws IOException
    {
        if (!_stack.isTwoPreviousItemsValues(_index)) throw new IOException("Operation \'<=\' needs two operands");

        Value _val0 = (Value) _stack.elementAt(_index - 2);
        Value _val1 = (Value) _stack.elementAt(_index - 1);

        _index -= 2;
        _stack.removeElementAt(_index);
        _stack.removeElementAt(_index);

        if (_val0.getType() != _val1.getType()) throw new IOException("Incompatible types in \"<=\" operation");

        switch (_val0.getType())
        {
            case Value.TYPE_BOOLEAN:
                {
                    throw new IOException("Operation \"<=\" doesn't work with BOOLEAN types");
                }
            case Value.TYPE_FLOAT:
                {
                    boolean lg_result = ((Float) _val0.getValue()).floatValue() <= ((Float) _val1.getValue()).floatValue();
                    _stack.setElementAt(new Value(Boolean.toString(lg_result)), _index);
                }
                ;
                break;
            case Value.TYPE_INT:
                {
                    boolean lg_result = ((Long) _val0.getValue()).longValue() <= ((Long) _val1.getValue()).longValue();
                    _stack.setElementAt(new Value(Boolean.toString(lg_result)), _index);
                }
                ;
                break;
            case Value.TYPE_STRING:
                {
                    boolean lg_result = ((String) _val0.getValue()).length() <= ((String) _val1.getValue()).length();
                    _stack.setElementAt(new Value(Boolean.toString(lg_result)), _index);
                }
                ;
                break;
        }
    }

    //>
    private static void operationMore(FormulaStack _stack, int _index) throws IOException
    {
        if (!_stack.isTwoPreviousItemsValues(_index)) throw new IOException("Operation \'>\' needs two operands");

        Value _val0 = (Value) _stack.elementAt(_index - 2);
        Value _val1 = (Value) _stack.elementAt(_index - 1);

        _index -= 2;
        _stack.removeElementAt(_index);
        _stack.removeElementAt(_index);

        if (_val0.getType() != _val1.getType()) throw new IOException("Incompatible types in \">\" operation");

        switch (_val0.getType())
        {
            case Value.TYPE_BOOLEAN:
                {
                    throw new IOException("Operation \">\" doesn't work with BOOLEAN types");
                }
            case Value.TYPE_FLOAT:
                {
                    boolean lg_result = ((Float) _val0.getValue()).floatValue() > ((Float) _val1.getValue()).floatValue();
                    _stack.setElementAt(new Value(Boolean.toString(lg_result)), _index);
                }
                ;
                break;
            case Value.TYPE_INT:
                {
                    boolean lg_result = ((Long) _val0.getValue()).longValue() > ((Long) _val1.getValue()).longValue();
                    _stack.setElementAt(new Value(Boolean.toString(lg_result)), _index);
                }
                ;
                break;
            case Value.TYPE_STRING:
                {
                    boolean lg_result = ((String) _val0.getValue()).length() > ((String) _val1.getValue()).length();
                    _stack.setElementAt(new Value(Boolean.toString(lg_result)), _index);
                }
                ;
                break;
        }

    }

    //>=
    private static void operationMoreEQU(FormulaStack _stack, int _index) throws IOException
    {
        if (!_stack.isTwoPreviousItemsValues(_index)) throw new IOException("Operation \'>=\' needs two operands");

        Value _val0 = (Value) _stack.elementAt(_index - 2);
        Value _val1 = (Value) _stack.elementAt(_index - 1);

        _index -= 2;
        _stack.removeElementAt(_index);
        _stack.removeElementAt(_index);

        if (_val0.getType() != _val1.getType()) throw new IOException("Incompatible types in \">=\" operation");

        switch (_val0.getType())
        {
            case Value.TYPE_BOOLEAN:
                {
                    throw new IOException("Operation \">=\" doesn't work with BOOLEAN types");
                }
            case Value.TYPE_FLOAT:
                {
                    boolean lg_result = ((Float) _val0.getValue()).floatValue() >= ((Float) _val1.getValue()).floatValue();
                    _stack.setElementAt(new Value(Boolean.toString(lg_result)), _index);
                }
                ;
                break;
            case Value.TYPE_INT:
                {
                    boolean lg_result = ((Long) _val0.getValue()).longValue() >= ((Long) _val1.getValue()).longValue();
                    _stack.setElementAt(new Value(Boolean.toString(lg_result)), _index);
                }
                ;
                break;
            case Value.TYPE_STRING:
                {
                    boolean lg_result = ((String) _val0.getValue()).length() >= ((String) _val1.getValue()).length();
                    _stack.setElementAt(new Value(Boolean.toString(lg_result)), _index);
                }
                ;
                break;
        }

    }
// ==========================================================================

// ===================Arithmetic functions======================================
    //+
    private static void operationADD(FormulaStack _stack, int _index) throws IOException
    {
        if (!_stack.isTwoPreviousItemsValues(_index)) throw new IOException("Operation \'+\' needs two operands");

        Value _val0 = (Value) _stack.elementAt(_index - 2);
        Value _val1 = (Value) _stack.elementAt(_index - 1);

        _index -= 2;
        _stack.removeElementAt(_index);
        _stack.removeElementAt(_index);

        if (_val0.getType() == Value.TYPE_STRING || _val1.getType() == Value.TYPE_STRING)
        {
            String s_val0 = "", s_val1 = "";
            if (_val0.getType() != Value.TYPE_STRING)
            {
                switch (_val0.getType())
                {
                    case Value.TYPE_BOOLEAN:
                        {
                            s_val0 = "" + ((Boolean) _val0.getValue()).booleanValue();
                        }
                        ;
                        break;
                    case Value.TYPE_INT:
                        {
                            s_val0 = "" + ((Long) _val0.getValue()).longValue();
                        }
                        ;
                        break;
                    case Value.TYPE_FLOAT:
                        {
                            s_val0 = "" + ((Float) _val0.getValue()).floatValue();
                        }
                        ;
                        break;
                }
            }
            else
            {
                s_val0 = (String) _val0.getValue();
            }

            if (_val1.getType() != Value.TYPE_STRING)
            {
                switch (_val1.getType())
                {
                    case Value.TYPE_BOOLEAN:
                        {
                            s_val1 = "" + ((Boolean) _val1.getValue()).booleanValue();
                        }
                        ;
                        break;
                    case Value.TYPE_INT:
                        {
                            s_val1 = "" + ((Long) _val1.getValue()).longValue();
                        }
                        ;
                        break;
                    case Value.TYPE_FLOAT:
                        {
                            s_val1 = "" + ((Float) _val1.getValue()).floatValue();
                        }
                        ;
                        break;
                }
            }
            else
            {
                s_val1 = (String) _val1.getValue();
            }

            s_val0 = s_val0.concat(s_val1);
            _stack.setElementAt(new Value("\"" + s_val0 + "\""), _index);
        }
        else
        {
            switch (_val0.getType())
            {
                case Value.TYPE_BOOLEAN:
                    {
                        throw new IOException("Operation \"+\" doesn't work with the BOOLEAN type and any other exclude STRING");
                    }

                case Value.TYPE_FLOAT:
                    {
                        float f_result;
                        if (_val0.getType() == _val1.getType())
                        {
                            f_result = ((Float) _val0.getValue()).floatValue() + ((Float) _val1.getValue()).floatValue();
                        }
                        else
                        {
                            f_result = ((Float) _val0.getValue()).floatValue() + ((Long) _val1.getValue()).longValue();
                        }
                        _stack.setElementAt(new Value(Float.toString(f_result)), _index);
                    }
                    ;
                    break;
                case Value.TYPE_INT:
                    {
                        if (_val0.getType() == _val1.getType())
                        {
                            long i_result = ((Long) _val0.getValue()).longValue() + ((Long) _val1.getValue()).longValue();
                            _stack.setElementAt(new Value(Long.toString(i_result)), _index);
                        }
                        else
                        {
                            float f_result = ((Long) _val0.getValue()).longValue() + ((Float) _val1.getValue()).floatValue();
                            _stack.setElementAt(new Value(Float.toString(f_result)), _index);
                        }
                    }
                    ;
                    break;
            }
        }
    }

    //-
    private static void operationSUB(FormulaStack _stack, int _index) throws IOException
    {
        Value _val0;
        Value _val1;

        if (!_stack.isTwoPreviousItemsValues(_index))
        {
            if (_stack.isOnePreviousItemValue(_index))
            {
                _val0 = new Value("0");
                _val1 = (Value) _stack.elementAt(_index - 1);
                _index = _index - 1;
                _stack.removeElementAt(_index);
            }
            else
            {
                throw new IOException("Operation \'-\' needs two operands");
            }
        }
        else
        {

            if (!_stack.isTwoPreviousItemsValues(_index)) throw new IOException("Operation \'-\' needs two operands");

            _val0 = (Value) _stack.elementAt(_index - 2);
            _val1 = (Value) _stack.elementAt(_index - 1);

            _index = _index - 2;
            _stack.removeElementAt(_index);
            _stack.removeElementAt(_index);
        }

        if (_val0.getType() == Value.TYPE_STRING || _val1.getType() == Value.TYPE_STRING)
        {
            throw new IOException("You can't use \"-\" operation with the STRING type");
        }
        else
        {
            switch (_val0.getType())
            {
                case Value.TYPE_BOOLEAN:
                    {
                        throw new IOException("Operation \"-\" doesn't work with the BOOLEAN type and any other exclude STRING");
                    }

                case Value.TYPE_FLOAT:
                    {
                        float f_result;
                        if (_val0.getType() == _val1.getType())
                        {
                            f_result = ((Float) _val0.getValue()).floatValue() - ((Float) _val1.getValue()).floatValue();
                        }
                        else
                        {
                            f_result = ((Float) _val0.getValue()).floatValue() - ((Long) _val1.getValue()).intValue();
                        }
                        _stack.setElementAt(new Value(Float.toString(f_result)), _index);
                    }
                    ;
                    break;
                case Value.TYPE_INT:
                    {
                        if (_val0.getType() == _val1.getType())
                        {
                            long i_result = ((Long) _val0.getValue()).longValue() - ((Long) _val1.getValue()).longValue();
                            _stack.setElementAt(new Value(Long.toString(i_result)), _index);
                        }
                        else
                        {

                            float f_result = ((Long) _val0.getValue()).longValue() - ((Float) _val1.getValue()).floatValue();
                            _stack.setElementAt(new Value(Float.toString(f_result)), _index);

                        }
                    }
                    ;
                    break;
            }
        }
    }

    // /
    private static void operationDIV(FormulaStack _stack, int _index) throws IOException
    {
        if (!_stack.isTwoPreviousItemsValues(_index)) throw new IOException("Operation \'\\\' needs two operands");

        Value _val0 = (Value) _stack.elementAt(_index - 2);
        Value _val1 = (Value) _stack.elementAt(_index - 1);

        _index = _index - 2;
        _stack.removeElementAt(_index);
        _stack.removeElementAt(_index);

        if (_val0.getType() == Value.TYPE_STRING || _val1.getType() == Value.TYPE_STRING)
        {
            throw new IOException("You can't use \"/\" operation with the STRING type");
        }
        else
        {
            switch (_val0.getType())
            {
                case Value.TYPE_BOOLEAN:
                    {
                        throw new IOException("Operation \"/\" doesn't work with the BOOLEAN type and any other exclude STRING");
                    }

                case Value.TYPE_FLOAT:
                    {
                        float f_result;
                        if (_val0.getType() == _val1.getType())
                        {
                            f_result = ((Float) _val0.getValue()).floatValue() / ((Float) _val1.getValue()).floatValue();
                        }
                        else
                        {
                            f_result = ((Float) _val0.getValue()).floatValue() / ((Long) _val1.getValue()).longValue();
                        }
                        _stack.setElementAt(new Value(Float.toString(f_result)), _index);
                    }
                    ;
                    break;

                case Value.TYPE_INT:
                    {
                        if (_val0.getType() == _val1.getType())
                        {
                            long i_result = ((Long) _val0.getValue()).longValue() / ((Long) _val1.getValue()).longValue();
                            _stack.setElementAt(new Value(Long.toString(i_result)), _index);
                        }
                        else
                        {

                            float f_result = ((Long) _val0.getValue()).longValue() / ((Float) _val1.getValue()).floatValue();
                            _stack.setElementAt(new Value(Float.toString(f_result)), _index);
                        }
                    }
                    ;
                    break;
            }
        }
    }

    // %
    private static void operationMOD(FormulaStack _stack, int _index) throws IOException
    {
        if (!_stack.isTwoPreviousItemsValues(_index)) throw new IOException("Operation \'%\' needs two operands");

        Value _val0 = (Value) _stack.elementAt(_index - 2);
        Value _val1 = (Value) _stack.elementAt(_index - 1);

        _index = _index - 2;
        _stack.removeElementAt(_index);
        _stack.removeElementAt(_index);

        if (_val0 == null || _val1 == null) throw new IOException("Operation \'%\' needs two operands");
        if (_val0.getType() == Value.TYPE_STRING || _val1.getType() == Value.TYPE_STRING)
        {
            throw new IOException("You can't use \"%\" operation with the STRING type");
        }
        else
        {
            switch (_val0.getType())
            {
                case Value.TYPE_BOOLEAN:
                    {
                        throw new IOException("Operation \"%\" doesn't work with the BOOLEAN type and any other exclude STRING");
                    }

                case Value.TYPE_FLOAT:
                    {
                        float f_result;
                        if (_val0.getType() == _val1.getType())
                        {
                            f_result = ((Float) _val0.getValue()).floatValue() % ((Float) _val1.getValue()).floatValue();
                        }
                        else
                        {
                            f_result = ((Float) _val0.getValue()).floatValue() % ((Long) _val1.getValue()).longValue();
                        }
                        _stack.setElementAt(new Value(Float.toString(f_result)), _index);
                    }
                    ;
                    break;

                case Value.TYPE_INT:
                    {
                        if (_val0.getType() == _val1.getType())
                        {
                            long i_result = ((Long) _val0.getValue()).longValue() % ((Long) _val1.getValue()).longValue();
                            _stack.setElementAt(new Value(Long.toString(i_result)), _index);
                        }
                        else
                        {
                            float f_result = ((Long) _val0.getValue()).longValue() % ((Float) _val1.getValue()).floatValue();
                            _stack.setElementAt(new Value(Float.toString(f_result)), _index);
                        }
                    }
                    ;
                    break;
            }
        }
    }

    // *
    private static void operationMUL(FormulaStack _stack, int _index) throws IOException
    {
        if (!_stack.isTwoPreviousItemsValues(_index)) throw new IOException("Operation \'*\' needs two operands");

        Value _val0 = (Value) _stack.elementAt(_index - 2);
        Value _val1 = (Value) _stack.elementAt(_index - 1);

        _index = _index - 2;
        _stack.removeElementAt(_index);
        _stack.removeElementAt(_index);

        if (_val0 == null || _val1 == null) throw new IOException("Operation \'*\' needs two operands");

        if (_val0.getType() == Value.TYPE_STRING || _val1.getType() == Value.TYPE_STRING)
        {
            throw new IOException("You can't use \"*\" operation with the STRING type");
        }
        else
        {
            switch (_val0.getType())
            {
                case Value.TYPE_BOOLEAN:
                    {
                        throw new IOException("Operation \"*\" doesn't work with the BOOLEAN type and any other exclude STRING");
                    }

                case Value.TYPE_FLOAT:
                    {
                        float f_result;
                        if (_val0.getType() == _val1.getType())
                        {
                            f_result = ((Float) _val0.getValue()).floatValue() * ((Float) _val1.getValue()).floatValue();
                        }
                        else
                        {
                            f_result = ((Float) _val0.getValue()).floatValue() * ((Long) _val1.getValue()).longValue();
                        }
                        _stack.setElementAt(new Value(Float.toString(f_result)), _index);
                    }
                    ;
                    break;

                case Value.TYPE_INT:
                    {
                        if (_val0.getType() == _val1.getType())
                        {
                            long i_result = ((Long) _val0.getValue()).longValue() * ((Long) _val1.getValue()).longValue();
                            _stack.setElementAt(new Value(Long.toString(i_result)), _index);
                        }
                        else
                        {
                            float f_result = ((Long) _val0.getValue()).longValue() * ((Float) _val1.getValue()).floatValue();
                            _stack.setElementAt(new Value(Float.toString(f_result)), _index);
                        }
                    }
                    ;
                    break;
            }
        }
    }
//===========================================================================================

    public static final String getOperationToken(String _string, int _position)
    {
        String s_spaces = "";
        while (_position < _string.length())
        {
            if (_string.charAt(_position) == ' ')
            {
                s_spaces += ' ';
                _position++;
                continue;
            }
            break;
        }

        if (_position + 1 < _string.length())
        {
            // Checking  for long operations
            String s_str = _string.substring(_position, _position + 2);
            for (int li = 0; li < ap_operations.length; li++)
            {
                String[] as_ops = (String[]) ap_operations[li][1];
                for (int ls = 0; ls < as_ops.length; ls++)
                {
                    String s_op = as_ops[ls];
                    if (s_op.length() == 1) continue;
                    if (s_op.equals(s_str)) return s_spaces + s_str;
                }
            }
        }
        //Проверка на одноаргументную операцию
        String s_str = "" + _string.charAt(_position);
        for (int li = 0; li < ap_operations.length; li++)
        {
            String[] as_ops = (String[]) ap_operations[li][1];
            for (int ls = 0; ls < as_ops.length; ls++)
            {
                String s_op = as_ops[ls];
                if (s_op.length() != 1) continue;
                if (s_op.equals(s_str)) return s_spaces + s_str;
            }
        }
        return null;
    }

    private static final String getNumberOrVariable(String _string, int _pos) throws IOException
    {
        final int TYPE_NONE = 0;
        final int TYPE_INTEGER = 1;
        final int TYPE_STRING = 2;
        final int TYPE_VARIABLE = 3;
        final int TYPE_HEXINTEGER = 4;
        final int TYPE_FUNCTION = 5;

        int i_type = TYPE_NONE;

        String s_ak = "";

        boolean lg_specchar = false;
        int i_stringLength = _string.length();
        int i_specStr = 0;

        while (_pos < i_stringLength)
        {
            char c_char = _string.charAt(_pos++);

            switch (i_type)
            {
                case TYPE_NONE:
                    {
                        switch (c_char)
                        {
                            case ' ':
                                s_ak += c_char;
                                break;
                            case '\"':
                                {
                                    s_ak += c_char;
                                    i_type = TYPE_STRING;
                                }
                                ;
                                break;
                            default :
                                {
                                    if ((c_char >= '0' && c_char <= '9') || c_char == '.')
                                    {
                                        s_ak += c_char;
                                        i_type = TYPE_INTEGER;
                                    }
                                    else if (c_char == '$')
                                    {
                                        s_ak += c_char;
                                        i_type = TYPE_FUNCTION;
                                    }
                                    else if ((c_char >= 'a' && c_char <= 'z') || (c_char >= 'A' && c_char <= 'Z') || c_char == '_')
                                    {
                                        s_ak += c_char;
                                        i_type = TYPE_VARIABLE;
                                    }
                                    else
                                        return ""+c_char;
                                }
                        }
                    }
                    ;
                    break;
                case TYPE_INTEGER:
                    {
                        if (c_char == 'x')
                        {
                            s_ak += c_char;
                            i_type = TYPE_HEXINTEGER;
                        }
                        else if (c_char == '.' || (c_char >= '0' && c_char <= '9'))
                        {
                            s_ak += c_char;
                        }
                        else
                            return s_ak;
                    }
                    ;
                    break;
                case TYPE_STRING:
                    {
                        if (lg_specchar)
                        {
                            switch (c_char)
                            {
                                case '\"':
                                    {
                                        s_ak += '\"';
                                    }
                                    ;
                                    break;
                                case 'n':
                                    {
                                        s_ak += '\n';
                                    }
                                    ;
                                    break;
                                case 'r':
                                    {
                                        s_ak += '\r';
                                    }
                                    ;
                                    break;
                                case '\\':
                                    {
                                        s_ak += '\\';
                                    }
                                    ;
                                    break;
                                default :
                                    throw new IOException("Unknown special char");
                            }
                            i_specStr++;
                            lg_specchar = false;
                        }
                        else if (c_char == '\"')
                        {
                            s_ak += c_char;
                            if (i_specStr != 0)
                            {
                                while (i_specStr > 0)
                                {
                                    s_ak += ' ';
                                    i_specStr--;
                                }
                            }
                            return s_ak;
                        }
                        else if (c_char == '\\')
                        {
                            lg_specchar = true;
                        }
                        else
                        {
                            s_ak += c_char;
                        }
                    }
                    ;
                    break;
                case TYPE_VARIABLE:
                    {
                        if ((c_char >= 'a' && c_char <= 'z') || (c_char >= 'A' && c_char <= 'Z') || c_char == '_' || (c_char >= '0' && c_char <= '9'))
                        {
                            s_ak += c_char;
                        }
                        else
                            return s_ak;
                    }
                    ;
                    break;
                case TYPE_FUNCTION:
                    {
                        if ((c_char >= 'a' && c_char <= 'z') || (c_char >= 'A' && c_char <= 'Z') || c_char == '_' || (c_char >= '0' && c_char <= '9'))
                        {
                            s_ak += c_char;
                        }
                        else
                            return s_ak;
                    }
                    ;
                    break;
                case TYPE_HEXINTEGER:
                    {
                        if ((c_char >= 'a' && c_char <= 'f') || (c_char >= 'A' && c_char <= 'F') || (c_char >= '0' && c_char <= '9'))
                        {
                            s_ak += c_char;
                        }
                        else
                            return s_ak;
                    }
                    ;
                    break;
            }
        }
        if (i_type == TYPE_STRING) throw new IOException("You have not closed string value");
        return s_ak;
    }

    public static final Delimeter [] DELIMETER_COMMA = new Delimeter[] {new Delimeter(",")};

    public static final FormulaStack convertStringToFormulaStack(String _string, HashMap _globalvariables, HashMap _localvariables, PreprocessorActionListener _listener) throws IOException
    {
        FormulaStack p_stack = new FormulaStack();
        final Delimeter [] _delimeters = DELIMETER_COMMA;

        int i_pos = 0;
        while (i_pos < _string.length())
        {
            String s_ar = getOperationToken(_string, i_pos);
            if (s_ar != null)
            {
                i_pos += s_ar.length();
                s_ar = s_ar.trim();

                p_stack.add(new Operation(s_ar));
                continue;
            }

            s_ar = getNumberOrVariable(_string, i_pos);
            if (s_ar.length() != 0)
            {
                i_pos += s_ar.length();
                s_ar = s_ar.trim();

                // Проверяем, не делиметер ли
                if (Delimeter.isDelimeter(_delimeters,s_ar))
                {
                    p_stack.add(Delimeter.getDelimeterForValue(_delimeters,s_ar));
                }
                else
                // Проверяем, не функция ли
                if (Function.isFunction(s_ar.toLowerCase()))
                {
                    Function p_function = new Function(s_ar.toLowerCase());
                    if (p_function.lg_UserFunction)
                    {
                        if (_listener == null) throw new IOException("You have an user function \""+s_ar+"\" but don't have defined an action listener");
                        int i_args = _listener.getArgumentsNumberForUserFunction(p_function.s_userFunctionName);
                        if (i_args<0) throw new IOException("Unknown user function \""+s_ar+"\"");
                        p_function.i_argsNumber = i_args;
                    }
                    p_stack.add(p_function);
                }
                else
                {
                    Value p_val = null;
                    if (_localvariables != null)
                        p_val = (Value) _localvariables.get(s_ar);
                    if (p_val == null)
                    {
                        if (_globalvariables != null)
                            p_val = (Value) _globalvariables.get(s_ar);
                    }

                    if (p_val != null)
                    {
                        p_stack.add(p_val);
                    }
                    else
                    {
                        try
                        {
                            p_stack.add(new Value(s_ar));
                        }
                        catch (IOException e)
                        {
                            throw new IOException("Unsupported value or function \'" + s_ar + "\'");
                        }
                    }
                }
            }
        }

        return p_stack;
    }

    /**
     *
     * @param _stack
     * @return
     * @throws IOException
     */
    public static final boolean sortFormulaStack(FormulaStack _stack) throws IOException
    {
        boolean lg_result = false;

        // Сортировка по приоритетам операций
        for (int li = 0; li < _stack.size() - 1; li++)
        {
            Object p_obj = _stack.elementAt(li);

            int i_prioritet = getPriorityForObject(p_obj);
            int i_bracketNumber = 0;
            boolean lg_unary = false;

            if (p_obj instanceof Delimeter)
            {
                lg_unary = false;
                continue;
            }
            else
            if (p_obj instanceof Value)
            {
                lg_unary = false;
                continue;
            }
            else if (p_obj instanceof Operation)
            {
                lg_unary = isUnary(((Operation) p_obj).s_String);
                if (i_prioritet == PRIORITY_OPEN_BRACKET || i_prioritet == PRIORITY_CLOSE_BRACKET) continue;
            }
            else if (p_obj instanceof Function)
            {
                lg_unary = true;
            }

            int i_lioff = 0;

            for (int lx = li + 1; lx < _stack.size(); lx++)
            {
                Object p_nobj = _stack.elementAt(lx);
                int i_priorityOfNewObj = getPriorityForObject(p_nobj);

                if (i_priorityOfNewObj == PRIORITY_OPEN_BRACKET)
                {
                    i_bracketNumber++;
                }
                else
                if (i_priorityOfNewObj == PRIORITY_CLOSE_BRACKET)
                {
                    if (i_bracketNumber == 0)
                    {
                        break;
                    }
                    else
                        i_bracketNumber--;
                }

                if (i_bracketNumber == 0)
                {
                    if (i_priorityOfNewObj > i_prioritet)
                    {
                        _stack.swap(lx, lx - 1);
                        i_lioff = -1;
                        if (lg_unary) break;
                    }
                    else
                        break;
                }
                else
                    _stack.swap(lx, lx - 1);
            }
            li += i_lioff;

            if (i_bracketNumber != 0) throw new IOException("There is not closed blacket");
        }

        // Удаляем все скобки и разделители
        int li = 0;
        while (li < _stack.size())
        {
            Object p_obj = _stack.elementAt(li);
            if (p_obj instanceof Delimeter)
            {
                lg_result = true;
                _stack.removeElementAt(li);
            }
            else
            if (p_obj instanceof Operation)
            {
                if (((Operation) p_obj).i_Priority == PRIORITY_OPEN_BRACKET || ((Operation) p_obj).i_Priority == PRIORITY_CLOSE_BRACKET)
                    _stack.removeElementAt(li);
                else
                    li++;
            }
            else
                li++;
        }

        return lg_result;
    }

    private static final int getPriorityForObject(Object _obj)
    {
        if (_obj instanceof Value)
        {
            return PRIORITY_VALUE;
        }
        else if (_obj instanceof Operation)
        {
            return ((Operation) _obj).i_Priority;

        }
        else if (_obj instanceof Function)
        {
            return PRIORITY_FUNCTION;
        }

        return -1;
    }


    public static final Value calculateFormulaStack(FormulaStack _stack,boolean _delimetersPresented,PreprocessorActionListener _actionListener) throws IOException
    {
        int i_indx = 0;
        while (_stack.size() != 1)
        {
            if (_stack.size()==i_indx)
                throw new IOException("Error formula");

           Object p_obj = _stack.elementAt(i_indx);
            if (p_obj instanceof Value)
            {
                i_indx++;
            }
            else
            if (p_obj instanceof Function)
            {
                Function p_func = (Function) p_obj;
                p_func.process(_stack, _actionListener, i_indx);
                i_indx -= p_func.i_argsNumber;
            }
            else if (p_obj instanceof Operation)
            {
                Operation p_oper = (Operation) p_obj;

                switch (p_oper.i_OperationCode)
                {
                    case OP_ADD:
                        {
                            operationADD(_stack, i_indx);
                            i_indx = 0;
                        }
                        ;
                        break;
                    case OP_AND:
                        {
                            operationAND(_stack, i_indx);
                            i_indx = 0;
                        }
                        ;
                        break;
                    case OP_DIV:
                        {
                            operationDIV(_stack, i_indx);
                            i_indx = 0;
                        }
                        ;
                        break;
                    case OP_EQU:
                        {
                            operationEQU(_stack, i_indx);
                            i_indx = 0;
                        }
                        ;
                        break;
                    case OP_LESS:
                        {
                            operationLess(_stack, i_indx);
                            i_indx = 0;
                        }
                        ;
                        break;
                    case OP_LESSEQU:
                        {
                            operationLessEQU(_stack, i_indx);
                            i_indx = 0;
                        }
                        ;
                        break;
                    case OP_MOD:
                        {
                            operationMOD(_stack, i_indx);
                            i_indx = 0;
                        }
                        ;
                        break;
                    case OP_MORE:
                        {
                            operationMore(_stack, i_indx);
                            i_indx = 0;
                        }
                        ;
                        break;
                    case OP_MOREEQU:
                        {
                            operationMoreEQU(_stack, i_indx);
                            i_indx = 0;
                        }
                        ;
                        break;
                    case OP_MUL:
                        {
                            operationMUL(_stack, i_indx);
                            i_indx = 0;
                        }
                        ;
                        break;
                    case OP_NEQU:
                        {
                            operationNotEQU(_stack, i_indx);
                            i_indx = 0;
                        }
                        ;
                        break;
                    case OP_NOT:
                        {
                            operationNOT(_stack, i_indx);
                            i_indx = 0;
                        }
                        ;
                        break;
                    case OP_OR:
                        {
                            operationOR(_stack, i_indx);
                            i_indx = 0;
                        }
                        ;
                        break;
                    case OP_SUB:
                        {
                            operationSUB(_stack, i_indx);
                            i_indx = 0;
                        }
                        ;
                        break;
                    case OP_XOR:
                        {
                            operationXOR(_stack, i_indx);
                            i_indx = 0;
                        }
                        ;
                        break;
                }
            }
        }
        if (!_delimetersPresented && _stack.size() > 1) throw new IOException("There is an operand without an operation");
        return _delimetersPresented ? _stack.size()>1 ? null : (Value) _stack.elementAt(0) : (Value) _stack.elementAt(0);
    }

    public static final Value evaluateFormula(String _string, HashMap _globalvariables, HashMap _localvariables, PreprocessorActionListener _actionListener) throws IOException
    {
        FormulaStack p_stack = convertStringToFormulaStack(_string, _globalvariables, _localvariables,_actionListener);

//        p_stack.printFormulaStack();
//        System.out.println("-------------");
         boolean lg_delimeters = sortFormulaStack(p_stack);

//        p_stack.printFormulaStack();

        return calculateFormulaStack(p_stack,lg_delimeters,_actionListener);
    }

    public static final Value evaluateFormula(FormulaStack _formula,PreprocessorActionListener _actionListener) throws IOException
    {
        boolean lg_delimeters = sortFormulaStack(_formula);

        return calculateFormulaStack(_formula,lg_delimeters,_actionListener);
    }

    public String toString()
    {
        return s_String;
    }
}
