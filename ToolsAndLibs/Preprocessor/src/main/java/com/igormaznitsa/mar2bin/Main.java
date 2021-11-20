package com.igormaznitsa.mar2bin;

import java.io.*;

public class Main
{
    private static final byte [] loadMAR (File _file) throws IOException
    {
        byte[] ab_Array = new byte[(int)_file.length()/2];

        FileInputStream p_fis = new FileInputStream(_file);
        DataInputStream p_instr = new DataInputStream(p_fis);

        for (int li = 0; li < ab_Array.length; li++)
        {
            int i_b0 = p_instr.readUnsignedByte();
            int i_b1 = p_instr.readUnsignedByte();
            int i_data = ((i_b1 << 8) | i_b0) / 32;
            ab_Array[li] = (byte) i_data;
        }

        int i_tileWidth = 16;

        for (int li = 0; li < ab_Array.length; li++)
        {
            int i_val = ab_Array[li] & 0xFF;
            int i_x = i_val % i_tileWidth;
            int i_y = i_val / i_tileWidth;
            ab_Array[li] = (byte) ((i_x << 4) | i_y);
        }

        p_instr.close();
        p_instr = null;
        Runtime.getRuntime().gc();

        return ab_Array;
    }

    private static final void saveBIN(File _file,byte [] _array,boolean _pack) throws IOException
    {
        FileOutputStream p_fos = new FileOutputStream(_file);
        DataOutputStream p_dos = new DataOutputStream(p_fos);

        // Размер
        p_dos.writeShort(_array.length);

        // Флаг паковки
        if (_pack)
        {
            //Пакуем
            p_dos.writeByte(1);
            int i_acc = -1;
            int i_len = 0;
            for(int li=0;li<_array.length;li++)
            {
                if (i_acc<0)
                {
                    i_acc = _array[li]&0xFF;
                    i_len = 0;
                }
                else
                {
                    int i_val = _array[li]&0xFF;
                    if (i_acc==i_val)
                    {
                        i_len++;
                        if (i_len == 255)
                        {
                            p_dos.writeByte(255);
                            p_dos.writeByte(i_acc);
                            i_len = 0;
                        }
                    }
                    else
                    {
                        p_dos.writeByte(i_len);
                        p_dos.writeByte(i_acc);

                        i_acc = i_val;
                        i_len = 0;
                    }
                }
            }
            p_dos.writeByte(i_len);
            p_dos.writeByte(i_acc);
        }
        else
        {
            //Непакованный
            p_dos.writeByte(0);
            p_dos.write(_array);
        }

        p_dos.flush();
        p_dos.close();
    }


    public static final void main(String [] _args)
    {
        String s_srcFile = null;
        String s_dstFile = null;
        boolean lg_packData = false;
        s_srcFile = _args[0];
        for(int li=0;li<_args.length;li++)
        {
            String s_str = _args[li];
            if (s_str.startsWith("/s:"))
            {
                s_srcFile = s_str.substring(3);
            }
            else
            if (s_str.startsWith("/d:"))
            {
                s_dstFile = s_str.substring(3);
            }
            else
            if (s_str.equals("/p"))
            {
                lg_packData = true;
            }
        }

        if (s_srcFile == null)
        {
            System.err.println("You must define source file");
            return;
        }
        if (s_dstFile == null)
        {
            if (s_srcFile.toUpperCase().endsWith(".MAR"))
            {
                s_dstFile = s_srcFile.substring(0,s_srcFile.length()-4)+".bin";
            }
            else
                s_dstFile = s_srcFile+".bin";
        }

        File p_file = new File(s_srcFile);
        if (!p_file.exists() || p_file.isDirectory())
        {
            System.err.println("I can't find source file ["+s_srcFile+"]");
            return;
        }

        try
        {
            byte [] ab_arr = loadMAR(p_file);
            File p_outFile = new File(s_dstFile);
            saveBIN(p_outFile,ab_arr,lg_packData);
        }
        catch (IOException e)
        {
            System.err.println("Error ["+e.getMessage()+"]");
        }
    }

}
