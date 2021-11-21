package com.igormaznitsa.Preprocessor;

import com.igormaznitsa.Preprocessor.Formula.Operation;
import com.igormaznitsa.Preprocessor.Formula.Value;
import com.igormaznitsa.Preprocessor.Formula.FormulaStack;
import com.igormaznitsa.WToolkit.classes.FileUtils;

import java.io.*;
import java.util.*;

/*
 * Copyright © 2002-2005 Igor A. Maznitsa (igor.maznitsa@igormaznitsa.com).  All rights reserved.
 * Software is confidential and copyrighted. Title to Software and all associated intellectual property rights
 * is retained by Igor A. Maznitsa. You may not make copies of Software, other than a single copy of Software for
 * archival purposes.  Unless enforcement is prohibited by applicable law, you may not modify, decompile, or reverse
 * engineer Software.  You acknowledge that Software is not designed, licensed or intended for use in the design,
 * construction, operation or maintenance of any nuclear facility. Igor A. Maznitsa disclaims any express or implied
 * warranty of fitness for such uses.
 *
 * This utility is a simple preprocessor for source files
 * Author : Igor A. Maznitsa
 * Version : 4.09b (15.06.2005)
*/

public class Preprocessor
{
    private static final void header()
    {
        System.out.println("CommentPreprocessor v4.91b (13 march 2005)");
        System.out.println("(C) 2003-2006 All Copyright by Igor A. Maznitsa (igor.maznitsa@igormaznitsa.com)");
        System.out.println("The latest version of the preprocessor is available on http://www.igormaznitsa.com\r\n");
    }

    private static final void help()
    {
        System.out.println("com.igormaznitsa.preprocessor.Preprocessor [@file_path] [/F:filter_list] [/N] [/I:in_dir] [/O:out_dir] [/P:name=value]");
        System.out.println("@file_path      - to download variable list from the file");
        System.out.println("/N              - the output directory will not be cleared before (default it will be cleared) ");
        System.out.println("/I:in_dir       - the name of the input directory (default \".\\\"");
        System.out.println("/O:out_dir      - the name of the output directory (default \"..\\preprocessed\")");
        System.out.println("/P:name=value   - the name and the value of a parameter, ATTENTION: If you want set a string value, you must use symbol '$' instead of '\"\'(\"Hello\"=$Hello$)");
        System.out.println("/R              - remove all comments from prprocessed sources");
        System.out.println("/CA             - copy all files, processing only files in the extension list");
        System.out.println("/F:filter_list  - set the extensions for preprocessed files (default /F:java,txt,html,htm)");
        System.out.println("/EF:filter_list - set the extensions for excluded files (default /EF:)");
        System.out.println();
        System.out.println("Preprocessor directives");
        System.out.println("-------------------------------------------------");
        System.out.println("//#action EXPR0,EXPR1...EXPRn   To generate an action for an outside preprocessor action listener");
        System.out.println("//#global NAME=EXPR             Definition a global variable");
        System.out.println("//#local  NAME=EXPR             Definition a local variable");
        System.out.println("//#define NAME                  Definition a local logical variable as TRUE (you can use it instead of //#local NAME=true)");
        System.out.println("//#if BOOLEAN_EXPR              The beginning of a #if..#else..#endif block");
        System.out.println("//#ifdefined VARIABLE_NAME      The beginning of a #ifdefine..#else..#endif block, TRUE if the variable is exists else FALSE");
        System.out.println("//#else                         Change the flag for current #if construction");
        System.out.println("//#endif                        To end current #if construction");
        System.out.println("//#include STRING_EXPR          Include the file from the path what is gotten from the expression");
        System.out.println("//#excludeif BOOLEAN_EXPR       Exclude the file from the preprocessor output list if the expression is true");
        System.out.println("//#exit                         Abort current file preprocessing");
        System.out.println("//#exitif BOOLEAN_EXPR          Abort current file preprocessing if the expression is true");
        System.out.println("//#while BOOLEAN_EXPR           Start of //#while..//#end construction");
        System.out.println("//#break                        To break //#while..//#end construction");
        System.out.println("//#continue                     To continue //#while..//#end construction");
        System.out.println("//#end                          End of //#while..//#end construction");
        System.out.println("//#//                           Comment the next string with \"//\" in the output stream");
        System.out.println("/*-*/                           Give up the tail of the string after the command");
        System.out.println("/*$EXPR$*/                      Insert the string view of the expression into the output stream");
        System.out.println("//#-                            Turn off the flag of string outputting");
        System.out.println("//#+                            Turn on the flag of string outputting");
        System.out.println("//$                             Output the tail of the string into the output stream without comments");
        System.out.println("//$$                            Output the tail of the string into the output stream without comments and without processing of /*$..$*/");
        System.out.println("//#assert                       Output the tail of the string onto the console");
        System.out.println("//#prefix+                      All strings after the directive will be added in the beginning of generated file");
        System.out.println("//#prefix-                      Turn off the prefix mode");
        System.out.println("//#postfix+                     All strings after the directive will be added in the end of generated file");
        System.out.println("//#postfix-                     Turn off the postfix mode");
        System.out.println("//#outdir  STRING_EXPR          The destination dir for the file");
        System.out.println("//#outname  STRING_EXPR         The destination name for the file");
        System.out.println("//#flush                        To write current text buffer states into the destination file and to clear them.");
        System.out.println("//#_if BOOLEAN_EXPR             The beginning of a #_if..#_else..#_endif block (works during finding of global variables at sources)");
        System.out.println("//#_else                        Change the flag for current #_if construction");
        System.out.println("//#_endif                       To end current #_if construction");
        System.out.println("-------------------------------------------------");
        System.out.println("Expression operators:\r\n");
        System.out.println("Arithmetic : *,/,+,%");
        System.out.println("Logical    : &&,||,!,^ (&&-AND,||-OR,!-NOT,^-XOR)");
        System.out.println("Comparation: ==,!=,<,>,>=,<=");
        System.out.println("Brakes     : (,)");
        System.out.println("-------------------------------------------------");
        System.out.println("Functions:\r\n");
        System.out.println("FLOAT|INTEGER   abs(FLOAT|INTEGER)              Return the absolute value of an int or float value.");
        System.out.println("INTEGER         round(FLOAT|INTEGER)            Return the closest int to the argument.");
        System.out.println("STRING          str2web(STRING)                 Convert a string to a web compatible string.");
        System.out.println("INTEGER         strlen(STRING)                  Return the length of a string");
        System.out.println("INTEGER         str2int(STRING)                 Convert a string value to integer");

        System.out.println("INTEGER xml_open(STRING)                        Open an xml document for the name as the argument, return the ID of the document");
        System.out.println("INTEGER xml_getDocumentElement(INTEGER)         Return the document element for an opened xml document");
        System.out.println("INTEGER xml_getElementsForName(INTEGER,STRING)  Return an elemens list");
        System.out.println("INTEGER xml_elementsNumber(INTEGER)             Return number of elements in the list");
        System.out.println("INTEGER xml_elementAt(INTEGER)                  Return element for index in the list");
        System.out.println("STRING  xml_elementName(INTEGER)                Return the name of element");
        System.out.println("INTEGER xml_getAttribute(INTEGER,STRING)        Return attribute value of an element");
        System.out.println("INTEGER xml_getElementText(STRING)              Return text value of an element");

        System.out.println("VALUE   $user_name(EXPR0,EXPR1..EXPRn)          User defined function, it returns variable (undefined type)");
        System.out.println("-------------------------------------------------");
        System.out.println("Data types:\r\n");
        System.out.println("BOOLEAN: true,false");
        System.out.println("INTEGER: 2374,0x56FE (signed 64 bit)");
        System.out.println("STRING : \"Hello World!\" (or $Hello World!$ for the command string)");
        System.out.println("FLOAT  : 0.745 (signed 32 bit)");
    }

    private static final void processLocalDefiniting(String _str, HashMap _global, HashMap _local, PreprocessorActionListener _listener) throws IOException
    {
            int i_equ = _str.indexOf('=');
            if (i_equ < 0)
            {
                throw new IOException();
            }
            String s_name = _str.substring(0, i_equ).trim();
            String s_eval = _str.substring(i_equ + 1).trim();

            Value p_value = Operation.evaluateFormula(s_eval, _global, _local, _listener);

            if (p_value == null) throw new IOException("Error value");

            _local.put(s_name, p_value);
    }

    private static final void removeComments(File _file) throws IOException
    {
        FileReader p_fr = new FileReader(_file);

        BufferedReader p_bufreader = new BufferedReader(p_fr);

        ByteArrayOutputStream p_baos = new ByteArrayOutputStream((int) _file.length());

        PrintStream p_dos = new PrintStream(p_baos);

        boolean lg_streamComments = false;
        boolean lg_string = false;
        boolean lg_specsymbol = false;

        while (true)
        {
            String s_str = p_bufreader.readLine();
            if (s_str == null) break;

            if (s_str.trim().length() == 0)
            {
                p_dos.println();
            }

            int i_pos = 0;
            StringBuffer p_akkum = new StringBuffer();
            lg_specsymbol = false;
            while (i_pos < s_str.length())
            {
                String s_pair = null;
                if ((s_str.length() - i_pos) >= 2)
                    s_pair = s_str.substring(i_pos, i_pos + 2);
                else
                    s_pair = "" + s_str.charAt(i_pos);

                if (lg_streamComments)
                {
                    if (s_pair.equals("*/"))
                    {
                        lg_streamComments = false;
                        i_pos++;
                    }
                } else
                {
                    if (lg_string)
                    {
                        if (lg_specsymbol)
                        {
                            lg_specsymbol = false;
                        } else if (s_pair.charAt(0) == '\\')
                        {
                            lg_specsymbol = true;
                        } else if (s_pair.charAt(0) == '\"')
                        {
                            lg_string = false;
                        }
                        p_akkum.append(s_pair.charAt(0));
                    } else
                    {
                        if (s_pair.equals("//"))
                        {
                            break;
                        } else if (s_pair.equals("/*"))
                        {
                            lg_streamComments = true;
                            i_pos++;
                        } else if (s_pair.charAt(0) == '\"')
                        {
                            lg_string = true;
                            lg_specsymbol = false;
                            p_akkum.append('\"');
                        } else
                            p_akkum.append(s_pair.charAt(0));
                    }
                }
                i_pos++;
            }

            String s_sss = p_akkum.toString();
            if (s_sss.trim().length() != 0)
            {
                p_dos.println(s_sss);
            }
        }

        p_fr.close();

        byte [] ab_newFile = p_baos.toByteArray();
        FileOutputStream p_fos = new FileOutputStream(_file);
        p_fos.write(ab_newFile);
        p_fos.flush();
        p_fos.close();
        p_baos = null;
        p_fos = null;
    }

    private static final String processMacros(String _string, HashMap _global, HashMap _local, PreprocessorActionListener _listener) throws IOException
    {
        if (_string.startsWith("//$$")) return _string;

        int i_indx;
        while (true)
        {
            i_indx = _string.indexOf("/*$");

            if (i_indx >= 0)
            {
                String s_leftpart = _string.substring(0, i_indx);
                int i_begin = i_indx;
                i_indx = _string.indexOf("$*/", i_indx);
                if (i_indx >= 0)
                {
                    String s_strVal = _string.substring(i_begin + 3, i_indx);
                    String s_rightPart = _string.substring(i_indx + 3);

                    Value p_val = Operation.evaluateFormula(s_strVal, _global, _local, _listener);
                    if (p_val == null) throw new IOException("Error value");

                    _string = s_leftpart + p_val.toString() + s_rightPart;
                } else
                    break;
            } else
                break;
        }
        return _string;
    }


    private static final void processFile(FileReference _fileRef, HashMap _global, HashMap _local, File _outdir, PrintStream _infoStream, boolean _removeComments, PreprocessorActionListener _listener) throws IOException
    {
        _local.clear();
        String s_strLastIfFileName = null;
        String s_strLastWhileFileName = null;
        int i_lastIfStringNumber = 0;
        int i_lastWhileStringNumber = 0;

        BufferedReader p_bufreader = new BufferedReader(new FileReader(_fileRef.p_srcFile));

        // Считываем весь файл в память
        Vector p_StringVector = new Vector(5000);
        while (true)
        {
            String s_str = p_bufreader.readLine();
            p_StringVector.add(s_str);
            if (s_str == null) break;
        }
        p_bufreader.close();
        p_bufreader = null;

        boolean lg_outenabled = true;
        boolean lg_ifenabled = true;
        boolean lg_commentNextString = false;
        boolean lg_noContinueCommand = true;
        boolean lg_noBreakCommand = true;

        int i_stringCounter = 0;
        int i_ifcounter = 0;
        int i_whileCounter = 0;
        int i_activeWhileCounter = 0;
        int i_activeif = 0;
        String s_fileName = _fileRef.p_srcFile.getCanonicalPath();

        Stack p_includeReferences = new Stack();
        boolean lg_endProcessing = false;
        File p_currentFile = _fileRef.p_srcFile;

        ByteArrayOutputStream p_baos = new ByteArrayOutputStream(64000);
        ByteArrayOutputStream p_prefix = new ByteArrayOutputStream(1024);
        ByteArrayOutputStream p_postfix = new ByteArrayOutputStream(1024);

        PrintStream p_maindos = new PrintStream(p_baos);
        PrintStream p_dosprefix = new PrintStream(p_prefix);
        PrintStream p_dospostfix = new PrintStream(p_postfix);

        PrintStream p_curdos = p_maindos;

        Stack p_fileNamesStack = new Stack();
        p_fileNamesStack.add(s_fileName);

        Stack p_stackWhileIndexes = new Stack();

        try
        {
            while (true)
            {
                String s_str = (String) p_StringVector.elementAt(i_stringCounter++);

                if (lg_endProcessing) s_str = null;

                if (s_str == null)
                {
                    if (p_includeReferences.size() > 0)
                    {
                        IncludeReference p_inRef = (IncludeReference) p_includeReferences.pop();
                        p_currentFile = p_inRef.p_File;
                        p_StringVector = p_inRef.p_stringsArray;
                        i_stringCounter = p_inRef.i_stringCounter;
                        lg_endProcessing = false;
                        p_fileNamesStack.pop();
                        s_fileName = (String) p_fileNamesStack.lastElement();
                        continue;
                    } else
                        break;
                }

                String s_originalString = s_str;
                s_str = s_str.trim();

                int i_spaces = s_originalString.indexOf(s_str);

                boolean lg_enableProcess = lg_noBreakCommand && lg_ifenabled && lg_noContinueCommand;

                if (lg_enableProcess) s_str = processMacros(s_str, _global, _local, _listener);

                if (s_str.startsWith("//#_if")
                        || s_str.startsWith("//#_else")
                        || s_str.startsWith("//#_endif")
                        || s_str.startsWith("//#global")
                        || s_str.startsWith("//#exclude"))
                    continue;
                else if (lg_enableProcess && s_str.startsWith("//#local"))
                {
                    // Processing of a local definition
                    s_str = s_str.substring(8).trim();
                    processLocalDefiniting(s_str, _global, _local, _listener);
                }
                else if (lg_enableProcess && s_str.startsWith("//#define"))
                {
                    // Processing of a local definition
                    s_str = s_str.substring(9).trim();
                    Value p_value = new Value("true");
                    _local.put(s_str, p_value);
                }
                else if (lg_enableProcess && s_str.startsWith("//#exitif"))
                {
                    // To end processing the file processing immediatly if the value is true
                    s_str = s_str.substring(9).trim();
                    Value p_value = Operation.evaluateFormula(s_str, _global, _local, _listener);
                    if (p_value == null || p_value.getType() != Value.TYPE_BOOLEAN)
                        throw new IOException("You don't have a boolean result in the #endif instruction");
                    if (((Boolean) p_value.getValue()).booleanValue())
                    {
                        lg_endProcessing = true;
                        continue;
                    }
                }
                else
                if (lg_enableProcess && s_str.startsWith("//#exit"))
                {
                    // To end processing the file immediatly
                    lg_endProcessing = true;
                    continue;
                } else if (s_str.startsWith("//#continue"))
                {
                    if (i_whileCounter == 0) throw new IOException("You have #continue without #when");
                    if (lg_enableProcess && i_whileCounter == i_activeWhileCounter)
                    {
                        lg_noContinueCommand = false;
                    }
                } else if (s_str.startsWith("//#break"))
                {
                    if (i_whileCounter == 0) throw new IOException("You have #break without #when");

                    if (lg_enableProcess && i_whileCounter == i_activeWhileCounter)
                    {
                        lg_noBreakCommand = false;
                    }
                } else if (s_str.startsWith("//#while"))
                {
                    // To end processing the file immediatly
                    if (lg_enableProcess)
                    {
                        s_str = s_str.substring(8).trim();
                        Value p_value = Operation.evaluateFormula(s_str, _global, _local, _listener);
                        if (p_value == null || p_value.getType() != Value.TYPE_BOOLEAN)
                            throw new IOException("You don't have a boolean result in the #while instruction");
                        if (i_whileCounter == 0)
                        {
                            s_strLastWhileFileName = s_fileName;
                            i_lastWhileStringNumber = i_stringCounter;
                        }
                        i_whileCounter++;
                        i_activeWhileCounter = i_whileCounter;

                        if (((Boolean) p_value.getValue()).booleanValue())
                        {
                            lg_noBreakCommand = true;
                        } else
                        {
                            lg_noBreakCommand = false;
                        }
                    } else
                        i_whileCounter++;

                    p_stackWhileIndexes.push(new Integer(i_stringCounter - 1));
                } else if (lg_enableProcess && s_str.startsWith("//#prefix+"))
                {
                    p_curdos = p_dosprefix;
                    continue;
                } else if (lg_enableProcess && s_str.startsWith("//#prefix-"))
                {
                    p_curdos = p_maindos;
                    continue;
                } else if (lg_enableProcess && s_str.startsWith("//#postfix+"))
                {
                    p_curdos = p_dospostfix;
                    continue;
                } else if (lg_enableProcess && s_str.startsWith("//#postfix-"))
                {
                    p_curdos = p_maindos;
                    continue;
                } else if (s_str.startsWith("//#ifdefined"))
                {
                    // Processing #ifdefine instruction
                    if (lg_enableProcess)
                    {
                        s_str = s_str.substring(12).trim();

                        if (s_str.length() == 0)
                            throw new IOException("You have not defined any variable in a //#ifdefined deirective");

                        boolean lg_defined = false;

                        if (_local.containsKey(s_str) || _global.containsKey(s_str)) lg_defined = true;

                        if (i_ifcounter == 0)
                        {
                            s_strLastIfFileName = s_fileName;
                            i_lastIfStringNumber = i_stringCounter;
                        }
                        i_ifcounter++;
                        i_activeif = i_ifcounter;

                        if (lg_defined)
                        {
                            lg_ifenabled = true;
                        } else
                        {
                            lg_ifenabled = false;
                        }
                    } else
                        i_ifcounter++;
                } else if (s_str.startsWith("//#if"))
                {
                    // Processing #if instruction
                    if (lg_enableProcess)
                    {
                        s_str = s_str.substring(5).trim();
                        Value p_value = Operation.evaluateFormula(s_str, _global, _local, _listener);
                        if (p_value == null || p_value.getType() != Value.TYPE_BOOLEAN)
                            throw new IOException("You don't have a boolean result in the #if instruction");
                        if (i_ifcounter == 0)
                        {
                            s_strLastIfFileName = s_fileName;
                            i_lastIfStringNumber = i_stringCounter;
                        }
                        i_ifcounter++;
                        i_activeif = i_ifcounter;

                        if (((Boolean) p_value.getValue()).booleanValue())
                        {
                            lg_ifenabled = true;
                        } else
                        {
                            lg_ifenabled = false;
                        }
                    } else
                        i_ifcounter++;
                } else if (s_str.startsWith("//#else"))
                {
                    if (i_ifcounter == 0) throw new IOException("You have got an #else instruction without #if");

                    if (i_ifcounter == i_activeif)
                    {
                        lg_ifenabled = !lg_ifenabled;
                    }
                }
                else if (s_str.startsWith("//#outdir"))
                {
                    if (lg_enableProcess)
                    {
                        try
                        {
                            s_str = s_str.substring(9).trim();
                            Value p_value = Operation.evaluateFormula(s_str, _global, _local, _listener);

                            if (p_value == null || p_value.getType() != Value.TYPE_STRING) throw new IOException("non string expression");
                            s_str = (String) p_value.getValue();
                            _fileRef.s_destDir = s_str;
                        }
                        catch (IOException e)
                        {
                            throw new IOException("You have the error in the #outdir instruction in the file " + _fileRef.p_srcFile.getCanonicalPath() + " line: " + i_stringCounter + " [" + e.getMessage() + ']');
                        }
                    }
                } else if (s_str.startsWith("//#outname"))
                {
                    if (lg_enableProcess)
                    {
                        try
                        {
                            s_str = s_str.substring(10).trim();
                            Value p_value = Operation.evaluateFormula(s_str, _global, _local, _listener);

                            if (p_value == null || p_value.getType() != Value.TYPE_STRING) throw new IOException("non string expression");
                            s_str = (String) p_value.getValue();
                            _fileRef.s_destName = s_str;
                        }
                        catch (IOException e)
                        {
                            throw new IOException("You have the error in the #outname instruction in the file " + _fileRef.p_srcFile.getCanonicalPath() + " line: " + i_stringCounter + " [" + e.getMessage() + ']');
                        }
                    }
                } else if (s_str.startsWith("//#flush"))
                {
                    if (lg_enableProcess)
                    {
                        try
                        {
                            p_maindos.flush();
                            p_maindos.close();

                            p_dospostfix.flush();
                            p_dospostfix.close();

                            p_dosprefix.flush();
                            p_dosprefix.close();


                            if (p_prefix.size() != 0 || p_postfix.size() != 0 || p_baos.size() != 0)
                            {

                                String s_fname = _fileRef.getDestFileName();
                                File p_outfile = new File(_outdir, s_fname);
                                p_outfile.getParentFile().mkdirs();
                                FileOutputStream p_fos = new FileOutputStream(p_outfile);

                                if (p_prefix.size() != 0)
                                {
                                    p_fos.write(p_prefix.toByteArray());
                                    p_fos.flush();
                                }

                                p_fos.write(p_baos.toByteArray());
                                p_fos.flush();

                                if (p_postfix.size() != 0)
                                {
                                    p_fos.write(p_postfix.toByteArray());
                                    p_fos.flush();
                                }

                                p_fos.close();
                                p_fos = null;

                                p_baos = new ByteArrayOutputStream(64000);
                                p_prefix = new ByteArrayOutputStream(1024);
                                p_postfix = new ByteArrayOutputStream(1024);

                                p_maindos = new PrintStream(p_baos);
                                p_dosprefix = new PrintStream(p_prefix);
                                p_dospostfix = new PrintStream(p_postfix);

                                p_curdos = p_maindos;
                            }
                        }
                        catch (IOException e)
                        {
                            throw new IOException("You have the error in the #outname instruction in the file " + _fileRef.p_srcFile.getCanonicalPath() + " line: " + i_stringCounter + " [" + e.getMessage() + ']');
                        }
                    }
                } else if (s_str.startsWith("//#endif"))
                {
                    if (i_ifcounter == 0) throw new IOException("You have got an #endif instruction without #if");

                    if (i_ifcounter == i_activeif)
                    {
                        i_ifcounter--;
                        i_activeif--;
                        lg_ifenabled = true;
                    } else
                    {
                        i_ifcounter--;
                    }
                } else if (s_str.startsWith("//#end"))
                {
                    if (i_whileCounter == 0) throw new IOException("You have got an #end instruction without #while");

                    int i_lastWhileIndex = ((Integer) p_stackWhileIndexes.pop()).intValue();

                    if (i_whileCounter == i_activeWhileCounter)
                    {
                        i_whileCounter--;
                        i_activeWhileCounter--;

                        if (lg_noBreakCommand)
                        {
                            i_stringCounter = i_lastWhileIndex;
                        }

                        lg_noContinueCommand = true;
                        lg_noBreakCommand = true;
                    } else
                    {
                        i_whileCounter--;
                    }
                } else if (lg_enableProcess && lg_outenabled && s_str.startsWith("//#action"))
                {
                    // Вызов внешнего обработчика, если есть
                    if (_listener != null)
                    {
                        s_str = s_str.substring(9).trim();
                        FormulaStack p_stack = Operation.convertStringToFormulaStack(s_str, _global, _local, _listener);
                        Operation.sortFormulaStack(p_stack);
                        Operation.calculateFormulaStack(p_stack, true, _listener);

                        Value [] ap_results = new Value[p_stack.size()];
                        for (int li = 0; li < p_stack.size(); li++)
                        {
                            Object p_obj = p_stack.elementAt(li);
                            if (!(p_obj instanceof Value)) throw new IOException("Error arguments list \'" + s_str + "\'");
                            ap_results[li] = (Value) p_obj;
                        }

                        if (!_listener.processAction(ap_results,_fileRef.s_destDir,_fileRef.s_destName, p_maindos, p_dosprefix, p_dospostfix, _infoStream))
                        {
                            throw new IOException("There is an error during an action processing [" + s_str + "]");
                        }
                    }
                } else if (lg_enableProcess && lg_outenabled && s_str.startsWith("//#include"))
                {
                    // include a file with the path to the place (with processing)
                    s_str = s_str.substring(10).trim();
                    Value p_value = Operation.evaluateFormula(s_str, _global, _local, _listener);

                    if (p_value == null || p_value.getType() != Value.TYPE_STRING)
                        throw new IOException("You don't have a string result in the #include instruction");

                    IncludeReference p_inRef = new IncludeReference(p_currentFile, p_StringVector, i_stringCounter, s_fileName);
                    p_includeReferences.push(p_inRef);
                    String s_fName = (String) p_value.getValue();
                    try
                    {
                        File p_inclFile = null;
                        if (s_fName.startsWith("."))
                            p_inclFile = new File(_fileRef.p_srcFile.getParent(), s_fName);
                        else
                            p_inclFile = new File(s_fName);

                        p_currentFile = p_inclFile;

                        p_bufreader = new BufferedReader(new FileReader(p_inclFile));
                        p_StringVector = new Vector(5000);
                        while (true)
                        {
                            String s_s = p_bufreader.readLine();
                            p_StringVector.add(s_s);
                            if (s_s == null) break;
                        }
                        p_bufreader.close();
                        p_bufreader = null;
                    }
                    catch (FileNotFoundException e)
                    {
                        throw new IOException("You have got the bad file pointer in the #include instruction [" + s_fName + "]");
                    }
                    p_fileNamesStack.push(s_fileName);
                    s_fileName = s_fName;
                    i_stringCounter = 0;
                } else if (s_str.startsWith("//$$") && lg_enableProcess && lg_outenabled)
                {
                    // Output the tail of the string to the output stream without comments and macroses
                    s_str = s_str.substring(4);
                    p_curdos.println(s_str);
                } else if (s_str.startsWith("//$") && lg_enableProcess && lg_outenabled )
                {
                    // Output the tail of the string to the output stream without comments
                    s_str = s_str.substring(3);
                    p_curdos.println(s_str);
                } else if (lg_enableProcess && lg_outenabled && s_str.startsWith("//#assert"))
                {
                    // Out a message to the output stream
                    s_str = s_str.substring(9).trim();
                    _infoStream.println("ASSERT MESSAGE: " + s_str);
                } else if (lg_enableProcess && s_str.startsWith("//#+"))
                {
                    // Turn on outputing to the output stream
                    lg_outenabled = true;
                } else if (lg_enableProcess && s_str.startsWith("//#-"))
                {
                    // Turn off outputing to the output stream
                    lg_outenabled = false;
                } else if (lg_enableProcess && s_str.startsWith("//#//"))
                {
                    // To comment next string
                    lg_commentNextString = true;
                } else if (lg_enableProcess)
                {
                    // Just string :)
                    if (lg_outenabled)
                    {
                        if (!s_str.startsWith("//#"))
                        {
                            int i_indx;

                            i_indx = s_str.indexOf("/*-*/");
                            if (i_indx >= 0) s_str = s_str.substring(0, i_indx);

                            if (lg_commentNextString)
                            {
                                p_curdos.print("// ");
                                lg_commentNextString = false;
                            }
                            for (int li = 0; li < i_spaces; li++)
                                p_curdos.print(" ");
                            p_curdos.println(s_str);
                        } else
                        {
                            throw new IOException("An unsupported preprocessor directive has been found " + s_str);
                        }
                    }
                }
            }
        }
        catch (IOException e)
        {
            throw new IOException(e.getMessage() + " file: " + s_fileName + " str: " + i_stringCounter);
        }

        if (i_ifcounter > 0) throw new IOException("You have an unclosed #if construction [" + s_strLastIfFileName + ':' + i_lastIfStringNumber + ']');
        if (i_whileCounter > 0) throw new IOException("You have an unclosed #while construction [" + s_strLastWhileFileName + ':' + i_lastWhileStringNumber + ']');


        p_maindos.flush();
        p_maindos.close();

        p_dospostfix.flush();
        p_dospostfix.close();

        p_dosprefix.flush();
        p_dosprefix.close();


        if (p_prefix.size() != 0 || p_postfix.size() != 0 || p_baos.size() != 0)
        {

            File p_outfile = new File(_outdir, _fileRef.getDestFileName());
            p_outfile.getParentFile().mkdirs();
            FileOutputStream p_fos = new FileOutputStream(p_outfile);

            if (p_prefix.size() != 0)
            {
                p_fos.write(p_prefix.toByteArray());
                p_fos.flush();
            }

            p_fos.write(p_baos.toByteArray());
            p_fos.flush();

            if (p_postfix.size() != 0)
            {
                p_fos.write(p_postfix.toByteArray());
                p_fos.flush();
            }

            p_fos.close();
            p_fos = null;

            if (_removeComments)
            {
                removeComments(p_outfile);
            }
        }
    }



    private static final void fillGlobalVariables(HashMap _global, HashSet _fileset, PrintStream _outStream, PreprocessorActionListener _listener) throws IOException
    {
        int i_stringLine = 0;
        String s_fileName = null;

        try
        {
            Iterator p_iter = _fileset.iterator();
            int i_ifcounter = 0;
            int i_activeif = 0;

            String s_strLastIfFileName = null;
            int i_lastIfStringNumber = 0;

            while (p_iter.hasNext())
            {
                FileReference p_fr = (FileReference) p_iter.next();
                s_fileName = p_fr.p_srcFile.getCanonicalPath();
                if (p_fr.lg_copyOnly) continue;
                BufferedReader p_bufreader = new BufferedReader(new FileReader(p_fr.p_srcFile));
                boolean lg_ifenabled = true;
                i_stringLine = 0;

                while (true)
                {
                    String s_str = p_bufreader.readLine();
                    if (s_str == null) break;
                    i_stringLine++;

                    s_str = s_str.trim();

                    if (s_str.startsWith("//#_if"))
                    {
                        // Processing #ifg instruction
                        if (lg_ifenabled)
                        {
                            s_str = s_str.substring(6).trim();
                            Value p_value = Operation.evaluateFormula(s_str, _global, null, _listener);
                            if (p_value == null || p_value.getType() != Value.TYPE_BOOLEAN)
                                throw new IOException("You don't have a boolean result in the #_if instruction");
                            if (i_ifcounter == 0)
                            {
                                s_strLastIfFileName = s_fileName;
                                i_lastIfStringNumber = i_stringLine;
                            }
                            i_ifcounter++;
                            i_activeif = i_ifcounter;

                            if (((Boolean) p_value.getValue()).booleanValue())
                            {
                                lg_ifenabled = true;
                            } else
                            {
                                lg_ifenabled = false;
                            }
                        } else
                            i_ifcounter++;
                    } else if (s_str.startsWith("//#_else"))
                    {
                        if (i_ifcounter == 0) throw new IOException("You have got an #_else instruction without #_if");

                        if (i_ifcounter == i_activeif)
                        {
                            lg_ifenabled = !lg_ifenabled;
                        }
                    } else if (s_str.startsWith("//#_endif"))
                    {
                        if (i_ifcounter == 0) throw new IOException("You have got an #_endif instruction without #_if");

                        if (i_ifcounter == i_activeif)
                        {
                            i_ifcounter--;
                            i_activeif--;
                            lg_ifenabled = true;
                        } else
                        {
                            i_ifcounter--;
                        }
                    }
                    else if (s_str.startsWith("//#global"))
                    {
                        if (!lg_ifenabled) continue;
                        try
                        {
                            s_str = s_str.substring(9).trim();
                            int i_equ = s_str.indexOf('=');
                            if (i_equ < 0)
                            {
                                throw new IOException();
                            }
                            String s_name = s_str.substring(0, i_equ).trim();
                            String s_eval = s_str.substring(i_equ + 1).trim();

                            if (_global.containsKey(s_name)) throw new IOException("You have duplicated the global variable " + s_name);

                            Value p_value = Operation.evaluateFormula(s_eval, _global, null, _listener);
                            if (p_value == null) throw new IOException("Error value");
                            _global.put(s_name, p_value);

                            _outStream.println("\'" + s_name + "\' = \'" + p_value + "\'");
                        }
                        catch (IOException e)
                        {
                            throw new IOException("Global definition error in " + p_fr.p_srcFile.getCanonicalPath() + " line: " + i_stringLine + " [" + e.getMessage() + "]");
                        }
                    }
                }
                p_bufreader.close();

                if (i_ifcounter > 0) throw new IOException("You have an unclosed #ifg construction [" + s_strLastIfFileName + ":" + i_lastIfStringNumber + "]");
            }
        }
        catch (Exception _ex)
        {
            throw new IOException(s_fileName + ":" + i_stringLine + " " + _ex.getMessage());
        }
    }

    private static final void processExcludeif(HashMap _global, HashSet _fileset, PrintStream _outStream, PreprocessorActionListener _listener) throws IOException
    {
        int i_stringLine = 0;
        String s_fileName = null;

        try
        {
            Iterator p_iter = _fileset.iterator();
            int i_ifcounter = 0;
            int i_activeif = 0;

            String s_strLastIfFileName = null;
            int i_lastIfStringNumber = 0;

            while (p_iter.hasNext())
            {
                FileReference p_fr = (FileReference) p_iter.next();
                s_fileName = p_fr.p_srcFile.getCanonicalPath();
                if (p_fr.lg_copyOnly) continue;
                BufferedReader p_bufreader = new BufferedReader(new FileReader(p_fr.p_srcFile));
                boolean lg_ifenabled = true;
                i_stringLine = 0;

                while (true)
                {
                    String s_str = p_bufreader.readLine();
                    if (s_str == null) break;
                    i_stringLine++;

                    s_str = s_str.trim();

                    if (s_str.startsWith("//#_if"))
                    {
                        // Processing #ifg instruction
                        if (lg_ifenabled)
                        {
                            s_str = s_str.substring(6).trim();
                            Value p_value = Operation.evaluateFormula(s_str, _global, null, _listener);
                            if (p_value == null || p_value.getType() != Value.TYPE_BOOLEAN)
                                throw new IOException("You don't have a boolean result in the #_if instruction");
                            if (i_ifcounter == 0)
                            {
                                s_strLastIfFileName = s_fileName;
                                i_lastIfStringNumber = i_stringLine;
                            }
                            i_ifcounter++;
                            i_activeif = i_ifcounter;

                            if (((Boolean) p_value.getValue()).booleanValue())
                            {
                                lg_ifenabled = true;
                            } else
                            {
                                lg_ifenabled = false;
                            }
                        } else
                            i_ifcounter++;
                    } else if (s_str.startsWith("//#_else"))
                    {
                        if (i_ifcounter == 0) throw new IOException("You have got an #_else instruction without #_if");

                        if (i_ifcounter == i_activeif)
                        {
                            lg_ifenabled = !lg_ifenabled;
                        }
                    } else if (s_str.startsWith("//#_endif"))
                    {
                        if (i_ifcounter == 0) throw new IOException("You have got an #_endif instruction without #_if");

                        if (i_ifcounter == i_activeif)
                        {
                            i_ifcounter--;
                            i_activeif--;
                            lg_ifenabled = true;
                        } else
                        {
                            i_ifcounter--;
                        }
                    }
                    else
                    if (s_str.startsWith("//#excludeif"))
                    {
                        if (lg_ifenabled)
                        {
                            try
                            {
                                s_str = s_str.substring(12).trim();
                                Value p_value = Operation.evaluateFormula(s_str, _global, null, _listener);

                                if (p_value == null || p_value.getType() != Value.TYPE_BOOLEAN) throw new IOException("non boolean expression");
                                if (((Boolean) p_value.getValue()).booleanValue())
                                {
                                    p_fr.lg_excluded = true;
                                }
                            }
                            catch (IOException e)
                            {
                                throw new IOException("You have the error in the #excludeif instruction in the file " + p_fr.p_srcFile.getCanonicalPath() + " line: " + i_stringLine+ " [" + e.getMessage() + "]");
                            }
                        }
                    }
                }
                p_bufreader.close();

                if (i_ifcounter > 0) throw new IOException("You have an unclosed #ifg construction [" + s_strLastIfFileName + ":" + i_lastIfStringNumber + "]");
            }
        }
        catch (Exception _ex)
        {
            throw new IOException(s_fileName + ":" + i_stringLine + " " + _ex.getMessage());
        }
    }

    private static final String [] preprocessingCommandLine(String [] _args)
    {
        String [] as_new = new String[_args.length];
        for (int li = 0; li < _args.length; li++)
        {
            as_new[li] = _args[li].replace('$', '\"');
        }
        return as_new;
    }

    private static final void processConfigurationFile(String _fileName, HashMap _global, boolean _verbose, PrintStream _outStream, PreprocessorActionListener _listener) throws IOException
    {
        File p_file = new File(_fileName);

        if (!p_file.exists() || p_file.isDirectory()) throw new IOException("I have not found the file " + p_file.getPath());

        BufferedReader p_bufreader = new BufferedReader(new FileReader(p_file));

        int i_stringCounter = 0;

        while (true)
        {
            String s_str = p_bufreader.readLine();
            if (s_str == null) break;
            i_stringCounter++;

            s_str = s_str.trim();

            if (s_str.length() == 0) continue;
            if (s_str.startsWith("#")) continue;

            String s_name = null;
            String s_value = null;

            int i_equindx = s_str.indexOf('=');
            if (i_equindx < 0)
            {
                throw new IOException("Error parameter format in " + p_file.getPath() + " line:" + i_stringCounter);
            }

            s_name = s_str.substring(0, i_equindx);
            s_value = s_str.substring(i_equindx + 1);

            Value p_value = null;
            try
            {
                s_value = s_value.trim();
                if (s_value.startsWith("@"))
                {
                    // This is a file
                    s_value = s_value.substring(1).trim();
                    p_value = Operation.evaluateFormula(s_value, _global, null, _listener);
                    if (p_value == null || p_value.getType() != Value.TYPE_STRING)
                    {
                        throw new IOException("You have not a string value in " + p_file.getPath() + " line:" + i_stringCounter);
                    }
                    s_value = (String) p_value.getValue();

                    File p_openedFile = new File(s_value);
                    if (p_openedFile.exists() && !p_openedFile.isDirectory())
                    {
                        BufferedReader p_openedFileBuf = new BufferedReader(new FileReader(p_openedFile));
                        StringBuffer p_strBuffer = new StringBuffer();
                        int i_iCntr = 0;
                        while (true)
                        {
                            String s_newStr = p_openedFileBuf.readLine();
                            if (s_newStr == null) break;
                            i_iCntr++;
                            try
                            {
                                s_newStr = processMacros(s_newStr, _global, null, _listener);
                            }
                            catch (IOException _op)
                            {
                                throw new IOException("Error during macros processing in " + p_openedFile.getPath() + " line:" + i_iCntr + " [" + _op.getMessage() + "]");
                            }
                            if (p_strBuffer.length() != 0) p_strBuffer.append("\r\n");
                            p_strBuffer.append(s_newStr);
                        }
                        p_openedFileBuf.close();
                        p_value = new Value("\"" + p_strBuffer + "\"");
                    } else
                    {
                        throw new IOException("I can not find the " + p_openedFile.getPath());
                    }
                } else
                {
                    // This is a string
                    p_value = Operation.evaluateFormula(s_value, _global, null, _listener);
                }

                if (p_value == null) throw new IOException("Error value");
            }
            catch (IOException e)
            {
                throw new IOException("An error value for the global variable \'" + s_name + "\' [" + e.getMessage() + "] in " + p_file.getPath() + " line:" + i_stringCounter);
            }

            if (_global.containsKey(s_name))
            {
                throw new IOException("Duplicated name of the name of the parameter [" + s_name + "] in " + p_file.getPath() + " line:" + i_stringCounter);
            }

            if (_verbose)
            {
                _outStream.println("The global variable \'" + s_name + "\'=" + p_value.toString() + " has been added from " + p_file.getPath() + " file");
            }
            _global.put(s_name, p_value);
        }
        p_bufreader.close();
        p_file = null;
    }

    /**
     * To start preprocessing with parameters
     *
     * @param _args      an array of command string arguments
     * @param _outStream an information output stream
     * @param _errStream an error output stream
     * @param _verbose   the flag of detailed reporting during preprocessing
     * @param _listener  an preprocessing action listener  (of //#action directive)
     * @return 0 if success and 1 if error
     * @throws IOException
     */
    public static final int process(String[] _args, PrintStream _outStream, PrintStream _errStream, boolean _verbose, PreprocessorActionListener _listener) throws IOException
    {
        _args = preprocessingCommandLine(_args);

        boolean lg_removeComments = false;
        String s_indir = "./";
        String s_outdir = "../preprocessed";
        String s_fileFilter = "java,txt,htm,html";
        String s_exclFileFilter = "xml";
        boolean lg_copyAll = false;

        boolean lg_clearOutputDirectory = true;

        HashMap p_global = new HashMap(64);

        if (_args.length > 0)
        {
            for (int li = 0; li < _args.length; li++)
            {
                String s_curstr = _args[li].toUpperCase();
                if (s_curstr.startsWith("@"))
                {
                    String s_fileName = _args[li].substring(1);
                    try
                    {
                        processConfigurationFile(s_fileName, p_global, _verbose, _outStream, _listener);
                    }
                    catch (IOException _exx)
                    {
                        _errStream.println(_exx.getMessage());
                        return 1;
                    }
                }
                else
                if (s_curstr.equals("/CA"))
                {
                    lg_copyAll = true;
                }
                else
                if (s_curstr.startsWith("/F:"))
                {
                    s_fileFilter = _args[li].substring(3);
                }
                else
                if (s_curstr.startsWith("/EF:"))
                {
                    s_exclFileFilter = _args[li].substring(4);
                }
                else
                if (s_curstr.startsWith("/I:"))
                {
                    s_indir = _args[li].substring(3);
                } else if (s_curstr.startsWith("/N"))
                {
                    lg_clearOutputDirectory = false;
                } else if (s_curstr.startsWith("/R"))
                {
                    lg_removeComments = true;
                } else if (s_curstr.startsWith("/H") || s_curstr.startsWith("-H") || s_curstr.startsWith("-?") || s_curstr.startsWith("/?"))
                {
                    help();
                    return 0;
                } else if (s_curstr.startsWith("/O:"))
                {
                    s_outdir = _args[li].substring(3);
                } else if (s_curstr.startsWith("/P:"))
                {
                    String s_name = null;
                    String s_value = null;
                    s_curstr = _args[li].substring(3);
                    int i_equindx = s_curstr.indexOf('=');
                    if (i_equindx < 0)
                    {
                        _errStream.println("Error format of the parameter [" + s_curstr + "]");
                        return 1;
                    }

                    s_name = s_curstr.substring(0, i_equindx);
                    s_value = s_curstr.substring(i_equindx + 1);

                    Value p_value = null;
                    try
                    {
                        p_value = Operation.evaluateFormula(s_value, p_global, null, _listener);
                        if (p_value == null) throw new IOException("Error value");
                    }
                    catch (IOException e)
                    {
                        _errStream.println("An error value for the global variable \'" + s_name + "\' [" + e.getMessage() + "]");
                        return 1;
                    }

                    if (p_global.containsKey(s_name))
                    {
                        _errStream.println("Duplicated name of the name of the parameter [" + s_name + "]");
                        return 1;
                    }

                    if (_verbose)
                    {
                        _outStream.println("The global variable \'" + s_name + "\'=" + p_value.toString() + " has been added from the command string");
                    }
                    p_global.put(s_name, p_value);
                } else
                {
                    help();
                    return 1;
                }
            }
        }

        File p_file = new File(s_outdir);
        if (lg_clearOutputDirectory) FileUtils.deleteDir(p_file);



        // Processing the extension list
        _outStream.println();
        s_fileFilter = s_fileFilter.trim();
        if (s_fileFilter.length() == 0)
        {
            _errStream.println("There are not any defined file extension for preprocessing");
            return 1;
        }

        StringTokenizer p_tokenizer = new StringTokenizer(s_fileFilter, ",");
        int i_numberFilters = p_tokenizer.countTokens();
        String[] as_fileExtension = null;
        if (i_numberFilters == 1)
        {
            as_fileExtension = new String[1];
            as_fileExtension[0] = s_fileFilter.trim();
        } else
        {
            as_fileExtension = new String[i_numberFilters];
            int i_indx = 0;
            while (p_tokenizer.hasMoreTokens())
            {
                String s_extension = "." + p_tokenizer.nextToken().trim();
                as_fileExtension[i_indx++] = s_extension;
                _outStream.println("Extension " + s_extension + " has been added");
            }
        }

        // Processing excluded the extension list
        _outStream.println();
        s_exclFileFilter = s_exclFileFilter.trim();

        p_tokenizer = new StringTokenizer(s_exclFileFilter, ",");
        i_numberFilters = p_tokenizer.countTokens();
        String[] as_fileExclExtension = null;
        if (i_numberFilters == 0)
        {
            as_fileExclExtension = new String[0];
        }
        else
        if (i_numberFilters == 1)
        {
            as_fileExclExtension = new String[1];
            as_fileExclExtension[0] = s_exclFileFilter.trim();
        } else
        {
            as_fileExclExtension = new String[i_numberFilters];
            int i_indx = 0;
            while (p_tokenizer.hasMoreTokens())
            {
                String s_extension = "." + p_tokenizer.nextToken().trim();
                as_fileExclExtension[i_indx++] = s_extension;
                _outStream.println("Excluded extension " + s_extension + " has been added");
            }
        }

        // Ordering of input directory names
        HashSet p_indir = new HashSet();
        p_tokenizer = new StringTokenizer(s_indir, File.pathSeparator);
        while (p_tokenizer.hasMoreTokens())
        {
            String s_dir = p_tokenizer.nextToken().trim();
            if (s_dir.length() > 0)
            {
                p_indir.add(s_dir);
            }
        }

        Iterator p_iter = p_indir.iterator();
        HashSet p_filelist = new HashSet(64);

        p_file = null;
        while (p_iter.hasNext())
        {
            s_indir = (String) p_iter.next();
            p_file = new File(s_indir);
            String s_absolutePath = null;
            try
            {
                s_absolutePath = p_file.getCanonicalPath();
            }
            catch (IOException e)
            {
                _errStream.println("Inside error #1");
                return 1;
            }

            if (p_file.exists())
            {
                if (!p_file.isDirectory())
                {
                    _errStream.println("An input directory is not found [" + p_file.getAbsolutePath() + "]");
                    return 1;
                }
                try
                {
                    s_indir = p_file.getCanonicalPath();
                    File[] p_tfilelist = sourceFileList(p_file, as_fileExtension,as_fileExclExtension,lg_copyAll);
                    for (int li = 0; li < p_tfilelist.length; li++)
                    {
                        File p_curFile = p_tfilelist[li];
                        boolean lg_extInList = false;
                        String s_fileName = p_curFile.getName();
                        for(int ld=0;ld<as_fileExtension.length;ld++)
                        {
                            if (endsWithIgnoreCase(s_fileName,as_fileExtension[ld]))
                                {
                                    lg_extInList = true;
                                    break;
                                }
                        }
                        p_filelist.add(new FileReference(p_curFile, p_curFile.getCanonicalPath().substring(s_absolutePath.length()),!lg_extInList));
                    }
                }
                catch (IOException e)
                {
                    _errStream.println("Fatal error");
                    return 1;
                }
            }
        }

        // Filling global variables
        try
        {
            if (_verbose)
            {
                _outStream.println("Scanning for global variables\r\n--------------");
            }
            fillGlobalVariables(p_global, p_filelist, _outStream, _listener);
            processExcludeif(p_global, p_filelist, _outStream, _listener);
        }
        catch (IOException e)
        {
            _errStream.println("Error in the global variable filling time [" + e.getMessage() + "]");
            return 1;
        }

        if (_verbose)
        {
            _outStream.println("\r\nPreprocessed files\r\n--------------");
        }
        // Processing files from the list
        File p_outDirectory = new File(s_outdir);

        if (!p_outDirectory.exists() || !p_outDirectory.isDirectory())
        {
            if (!p_outDirectory.mkdirs())
            {
                _errStream.println("I can't create a directory for the output path [" + p_outDirectory.getAbsolutePath() + "]");
                return 1;
            }
        }

        p_iter = p_filelist.iterator();
        HashMap p_local = new HashMap(64);

        byte [] ab_binBuffer = new byte[65535];

        while (p_iter.hasNext())
        {
            p_local.clear();
            FileReference p_fref = (FileReference) p_iter.next();
            if (p_fref.lg_excluded) continue;

            if (_verbose)
            {
                _outStream.println(p_fref.p_srcFile.getName());
            }

            try
            {
                if (p_fref.lg_copyOnly)
                {
                    FileInputStream p_fis = new FileInputStream(p_fref.p_srcFile);
                    File p_outfile = new File(p_outDirectory, p_fref.getDestFileName());
                    p_outfile.getParentFile().mkdirs();
                    FileOutputStream p_fos = new FileOutputStream(p_outfile);
                    while(true)
                    {
                        int i_len = p_fis.read(ab_binBuffer);
                        if (i_len<=0) break;
                        p_fos.write(ab_binBuffer,0,i_len);
                        if (i_len<ab_binBuffer.length) break;
                    }
                    p_fos.close();
                    p_fis.close();
                    p_fis = null;
                    p_fos = null;
                }
                else
                    processFile(p_fref, p_global, p_local, p_outDirectory, _outStream, lg_removeComments, _listener);
            }
            catch (IOException e)
            {
                _errStream.println("Error during the \'" + p_fref.p_srcFile.getCanonicalPath() + "\' file processing time [" + e.getMessage() + "]");
                return 1;
            }
        }

        return 0;
    }


    public static final void main(String[] _args)
    {

        //String s_formula = "3+$user(1,2)";
        //try
        //{
        //    TestActionListener p_Testlistener = new TestActionListener();


        //    FormulaStack p_stack = Operation.convertStringToFormulaStack(s_formula,null,null,p_Testlistener);
        //    boolean lg_delims = Operation.sortFormulaStack(p_stack);
        //    p_stack.printFormulaStack();


        //    Value p_result = Operation.calculateFormulaStack(p_stack,lg_delims,p_Testlistener);
        //    System.out.println("result = "+p_result);
        //}
        //catch (IOException e)
        //{
        //    e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        //}
        //System.exit(1);

        header();

        PrintStream p_outWriter = System.out;
        PrintStream p_errWriter = System.err;

        try
        {
            int i_flag = process(_args, p_outWriter, p_errWriter, true, null);

            System.exit(i_flag);
        }
        catch (IOException e)
        {
            e.printStackTrace();
            System.exit(1);
        }
    }

//======================================================================

    private static File[] sourceFileList(File _srcdir, String[] _extenions,String [] _exclExtensions,boolean _copyAll)
    {
        ArrayList p_list = new ArrayList();
        fillListWithFilenames(_srcdir, p_list, _extenions,_exclExtensions,_copyAll);
        return (File[]) p_list.toArray(new File[0]);
    }

    private static void fillListWithFilenames(File _file, ArrayList _filelist, String[] _extensions,String[] _exclExtensions,boolean _copyall)
    {
        if (!_file.exists()) return;
        if (!_file.isDirectory()) return;
        File[] p_fileList = _file.listFiles();
        for (int li = 0; li < p_fileList.length; li++)
        {
            File p_file = p_fileList[li];
            if (p_file.isDirectory())
            {
                fillListWithFilenames(p_file, _filelist, _extensions,_exclExtensions,_copyall);
            } else
            {
                if (p_file.isFile())
                {
                    boolean lg_excluded = false;
                    for (int le = 0; le < _exclExtensions.length; le++)
                    {
                        if (endsWithIgnoreCase(p_file.getName(),_exclExtensions[le]))
                        {
                            lg_excluded = true;
                            break;
                        }
                    }

                    if (lg_excluded) continue;
                    for (int le = 0; le < _extensions.length; le++)
                    {
                        if (_copyall)
                        {
                           _filelist.add(p_file);
                           break;
                        }
                        else
                        if (endsWithIgnoreCase(p_file.getName(), _extensions[le]))
                        {
                            _filelist.add(p_file);
                            break;
                        }
                    }
                }
            }
        }
    }

    private static boolean endsWithIgnoreCase(String s, String s1)
    {
        int i = s1.length();
        return s.regionMatches(true, s.length() - i, s1, 0, i);
    }
}
