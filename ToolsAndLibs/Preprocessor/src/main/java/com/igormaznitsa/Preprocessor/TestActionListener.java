package com.igormaznitsa.Preprocessor;

import com.igormaznitsa.Preprocessor.Formula.Value;

import java.io.File;
import java.io.PrintStream;
import java.io.IOException;

public class TestActionListener implements PreprocessorActionListener
{
    public boolean processAction(Value[] _actionParameters, String _dir,String _name, PrintStream _mainOutputStream, PrintStream _prefixOutputStream, PrintStream _postfixOutputStream, PrintStream _infoStream) throws IOException
    {
        return false;
    }

    public Value processUserFunction(String _functionName, Value[] _arguments) throws IOException
    {
        if (_functionName.equals("user"))
        {
            System.out.println("Call user function");
            for(int li=0;li<_arguments.length;li++)
            {
                System.out.println(_arguments[li]);
            }
            return new Value("44");
        }
        else
        if (_functionName.equals("user2"))
        {
            System.out.println("Call user2 function");
            for(int li=0;li<_arguments.length;li++)
            {
                System.out.println(_arguments[li]);
            }
            return new Value("122");
        }
        return null;
    }

    public int getArgumentsNumberForUserFunction(String _name) throws IOException
    {
        if (_name.equals("user"))
        {
            System.out.println("Ask arguments number");
            return 3;
        }
        else
        if (_name.equals("user2"))
        {
            System.out.println("Ask arguments number");
            return 2;
        }
        return -1;
    }
}
