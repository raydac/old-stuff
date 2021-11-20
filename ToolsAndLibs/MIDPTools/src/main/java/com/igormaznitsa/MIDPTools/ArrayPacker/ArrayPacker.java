package com.igormaznitsa.MIDPTools.ArrayPacker;

import java.io.*;
import java.util.Vector;
import java.util.StringTokenizer;

public class ArrayPacker
{
    private static final int ARRAY_NONE = 0;
    private static final int ARRAY_BYTE = 1;
    private static final int ARRAY_SHORT = 2;
    private static final int ARRAY_INT = 3;
    private static final int ARRAY_LONG = 4;

    private static final void outHelp()
    {
        System.out.println("Command line:");
        System.out.println("ArrayPacker arraytxt [outfile]");
    }

    private static final void writeArray(Vector _vector, DataOutputStream _dos, int _arrayType) throws IOException
    {
        if (_vector.size() == 0) return;

        _dos.writeShort(_vector.size());

        System.out.print("Write ");

        switch(_arrayType)
        {
            case ARRAY_BYTE: System.out.print("BYTE");break;
            case ARRAY_SHORT: System.out.print("SHORT");break;
            case ARRAY_INT: System.out.print("INT");break;
            case ARRAY_LONG: System.out.print("LONG");break;
        }

        System.out.println(" array length="+_vector.size());

        for (int li = 0; li < _vector.size(); li++)
        {
            long l_long = ((Long) _vector.get(li)).longValue();
            switch (_arrayType)
            {
                case ARRAY_BYTE:
                    {
                       _dos.writeByte((int)l_long);
                    }
                    ;
                    break;
                case ARRAY_SHORT:
                    {
                        _dos.writeShort((int)l_long);
                    }
                    ;
                    break;
                case ARRAY_INT:
                    {
                        _dos.writeInt((int)l_long);
                    }
                    ;
                    break;
                case ARRAY_LONG:
                    {
                        _dos.writeLong(l_long);
                    }
                    ;
                    break;
            }
        }
        _vector.removeAllElements();
        _dos.flush();
    }

    public static final void main(String[] _args)
    {

        if (_args.length == 0)
        {
            outHelp();
            System.exit(0);
        }

        String s_arrayFileName = _args[0];

        String s_outFileName = "arrays.bin";

        if (_args.length > 1)
        {
            s_outFileName = _args[1];
        }

        DataInputStream p_dis = null;
        DataOutputStream p_dos = null;

        try
        {
            p_dis = new DataInputStream(new FileInputStream(s_arrayFileName));
            p_dos = new DataOutputStream(new FileOutputStream(s_outFileName));

            Vector p_arrayVector = new Vector();

            int i_arrayType = ARRAY_NONE;

            while (true)
            {
                String s_line = p_dis.readLine();
                if (s_line == null) break;

                s_line = s_line.trim().toLowerCase();

                if (s_line.startsWith("$"))
                {
                    if (p_arrayVector.size() != 0)
                    {
                        writeArray(p_arrayVector,p_dos,i_arrayType);
                    }

                    if (s_line.startsWith("$byte"))
                    {
                        i_arrayType = ARRAY_BYTE;
                    }
                    else
                    if (s_line.startsWith("$short"))
                    {
                        i_arrayType = ARRAY_SHORT;
                    }
                    else
                    if (s_line.startsWith("$int"))
                    {
                        i_arrayType = ARRAY_INT;
                    }
                    else
                    if (s_line.startsWith("$long"))
                    {
                        i_arrayType = ARRAY_LONG;
                    }
                    else
                    {
                        throw new IOException("Unsupported array type ["+s_line+"]");
                    }
                }
                else
                {
                    if (i_arrayType == ARRAY_NONE) throw new IOException("You have not declared array type");

                    StringTokenizer p_StrTkn = new StringTokenizer(s_line,",");
                    while(p_StrTkn.hasMoreTokens())
                    {
                        String s_num = p_StrTkn.nextToken().trim();
                        Long p_longEl = new Long(s_num);
                        p_arrayVector.add(p_longEl);
                    }
                }
            }
            if (p_arrayVector.size()!=0)
            {
                writeArray(p_arrayVector,p_dos,i_arrayType);
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
            System.exit(0);
        }
        finally
        {
            try
            {
                p_dis.close();
                p_dos.close();
            }
            catch(Exception _ex)
            {
            }
        }
        System.exit(1);
    }
}
