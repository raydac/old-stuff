package com.igormaznitsa.WToolkit;

import com.igormaznitsa.WToolkit.classes.Panel;

import java.io.File;

public class WToolkit
{
    public static final void printHelp()
    {
        System.out.println("Command line : ClassPath Preverifier Jar Javac");
    }

    public static void main(String [] _args) throws Exception
    {
        String s_classPath = null;
        String s_preverifier = null;
        String s_jar = null;
        String s_javac = null;
        if (_args.length == 4)
        {
            s_classPath = _args[0];
            s_preverifier = _args[1];
            s_jar = _args[2];
            s_javac = _args[3];
        }
        else
        {
            printHelp();
            System.exit(1);
        }

        new Panel(s_classPath,s_preverifier,s_jar,s_javac);
    }
}
