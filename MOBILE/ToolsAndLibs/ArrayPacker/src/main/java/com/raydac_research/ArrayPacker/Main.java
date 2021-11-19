package com.raydac_research.ArrayPacker;

import com.igormaznitsa.Preprocessor.Formula.Value;
import com.igormaznitsa.Preprocessor.Formula.Operation;

import java.io.*;
import java.util.Vector;
import java.util.StringTokenizer;
import java.util.HashMap;
import java.awt.image.BufferedImage;

public class Main
{
    private static final void printBanner()
    {
        System.out.println("ArrayPacking utility");
        System.out.println("---------------------------------");
        System.out.println("v.1.5 (20-jul-2005)");
        System.out.println("Author : Igor Maznitsa");
        System.out.println("(C) 2005 Raydac Research Group Ltd.");
    }

    private static final void printHelp()
    {
        System.out.println();
        System.out.println("/m:[mar_array]      - to define the name of an encoded MAR file");
        System.out.println("/t:[tileset_img]    - to define the tileset image(GIF,TGA or JPG) (in the case the cells number for MAR will be defined as -1 automatically)");
        System.out.println("/cw:[cell_width]    - to define the width of a block in the tileset imagetileset image");
        System.out.println("/ch:[cell_height]   - to define the height of a block in the tileset imagetileset image");
        System.out.println("/cells:[cells_num]  - to define the image cells width number of an encoded MAR file (default 16)");
        System.out.println("/i:[text_array]     - to define the name of an encoded file");
        System.out.println("/o:[out_name]       - to define the name of the output file");
        System.out.println("/z                  - the tile image doesn't have zero tile");
        System.out.println("/p                  - to pack data");
    }

    private static final int ARRAY_BYTE = 0;
    private static final int ARRAY_CHAR = 1;
    private static final int ARRAY_SHORT = 2;
    private static final int ARRAY_INT = 3;
    private static final int ARRAY_LONG = 4;

    private static final void saveBIN(DataOutputStream _dos,Object _array,boolean _pack) throws IOException
    {
        DataOutputStream p_dos = _dos;

        int i_byteSize = 1;

        int i_length = 0;
        int i_type = 0;

        if (_array instanceof byte[])
        {
            i_type = ARRAY_BYTE;
            i_length = ((byte[])_array).length;
            i_byteSize = 1;
        }
        else
        if (_array instanceof char[])
        {
            i_type = ARRAY_CHAR;
            i_length = ((char[])_array).length;

            char [] ach_arr = (char[])_array;
            for(int li=0;li<ach_arr.length;li++)
            {
                long l_val = (long)ach_arr[li] & 0xFFFFl;
                int i_bsize = 1;
                if (l_val>0xFFl) i_bsize = 2;
                if (i_bsize>i_byteSize) i_byteSize = i_bsize;
            }
        }
        else
        if (_array instanceof short[])
        {
            i_type = ARRAY_SHORT;
            i_length = ((short[])_array).length;

            short [] ash_arr = (short[])_array;
            for(int li=0;li<ash_arr.length;li++)
            {
                long l_val = (long)ash_arr[li] & 0xFFFFl;

                System.out.print(l_val+",");

                int i_bsize = 1;
                if (l_val>0x7F) i_bsize = 2;
                if (i_bsize>i_byteSize) i_byteSize = i_bsize;
            }
        }
        else
        if (_array instanceof int[])
        {
            i_type = ARRAY_INT;
            i_length = ((int[])_array).length;

            int [] ai_arr = (int[])_array;
            for(int li=0;li<ai_arr.length;li++)
            {
                long l_val = (long)ai_arr[li] & 0xFFFFFFFFl;
                int i_bsize = 1;
                if (l_val>0xFFl) i_bsize = 2;
                if (l_val>0xFFFFl) i_bsize = 4;
                if (i_bsize>i_byteSize) i_byteSize = i_bsize;
            }
        }
        else
        if (_array instanceof long[])
        {
            i_type = ARRAY_LONG;
            i_length = ((long[])_array).length;

            long [] al_arr = (long[])_array;
            for(int li=0;li<al_arr.length;li++)
            {
                int i_bsize = 1;
                long l_val = (long)al_arr[li];
                if (l_val>0xFFl) i_bsize = 2;
                if (l_val>0xFFFFl) i_bsize = 4;
                if (l_val<0) i_bsize= 8;

                if (i_bsize>i_byteSize) i_byteSize = i_bsize;
            }
        }

        // Тип
        p_dos.writeByte(i_type);

        // Размер
        p_dos.writeShort(i_length);

        // Размер данных
        p_dos.writeByte(i_byteSize);

        System.out.println("Type:"+i_type);
        System.out.println("Length:"+i_length);
        System.out.println("ByteSize:"+i_byteSize);

        // Флаг паковки
        if (_pack)
        {
            //Пакуем
            p_dos.writeByte(1);
            long  l_acc = -1;
            boolean lg_accNegative = true;
            int i_len = 0;
            for(int li=0;li<i_length;li++)
            {
                long l_data = 0;
                switch(i_type)
                {
                        case ARRAY_BYTE :
                        {
                            l_data = ((byte[])_array)[li] & 0xFFl;
                        };break;
                        case ARRAY_CHAR :
                        {
                            l_data = ((char[])_array)[li] & 0xFFFFl;
                        };break;
                        case ARRAY_INT :
                        {
                            l_data = ((int[])_array)[li] & 0xFFFFFFFFl;
                        };break;
                        case ARRAY_SHORT :
                        {
                            l_data = ((short[])_array)[li] & 0xFFFFl;
                        };break;
                        case ARRAY_LONG :
                        {
                            l_data = ((long[])_array)[li];
                        };break;
                }

                if (lg_accNegative)
                {
                    lg_accNegative = false;
                    l_acc = l_data;
                    i_len = 0;
                }
                else
                {
                    if (l_acc==l_data)
                    {
                        i_len++;
                        if (i_len == 255)
                        {
                            p_dos.writeByte(255);

                            switch(i_byteSize)
                            {
                                case 1  :
                                    {
                                        p_dos.writeByte((int)l_acc);
                                    };break;
                                case 2 :
                                    {
                                        p_dos.writeShort((int)l_acc);
                                    };break;
                                case 4 :
                                    {
                                        p_dos.writeInt((int)l_acc);
                                    };break;
                                case 8 :
                                    {
                                        p_dos.writeLong((long)l_acc);
                                    };break;
                            }
                            i_len = 0;
                        }
                    }
                    else
                    {
                        p_dos.writeByte(i_len);
                        switch(i_byteSize)
                        {
                            case 1  :
                                {
                                    p_dos.writeByte((int)l_acc);
                                };break;
                            case 2 :
                                {
                                    p_dos.writeShort((int)l_acc);
                                };break;
                            case 4 :
                                {
                                    p_dos.writeInt((int)l_acc);
                                };break;
                            case 8 :
                                {
                                    p_dos.writeLong((long)l_acc);
                                };break;
                        }

                        l_acc = l_data;
                        i_len = 0;
                    }
                }
            }
            p_dos.writeByte(i_len);
            switch(i_byteSize)
            {
                case 1  :
                    {
                        p_dos.writeByte((int)l_acc);
                    };break;
                case 2 :
                    {
                        p_dos.writeShort((int)l_acc);
                    };break;
                case 4 :
                    {
                        p_dos.writeInt((int)l_acc);
                    };break;
                case 8 :
                    {
                        p_dos.writeLong((long)l_acc);
                    };break;
            }
        }
        else
        {
            //Непакованный
            p_dos.writeByte(0);

            switch(i_type)
            {
                case ARRAY_BYTE :
                    {
                        byte [] ab_arr = (byte []) _array;
                        for(int li=0;li<ab_arr.length;li++)
                        {
                            p_dos.writeByte(ab_arr[li]);
                        }
                    };break;
                case ARRAY_CHAR :
                    {
                        char[] ach_arr = (char []) _array;
                        for(int li=0;li<ach_arr.length;li++)
                        {
                            switch(i_byteSize)
                            {
                                    case 1 : p_dos.writeByte(ach_arr[li]);break;
                                    case 2 : p_dos.writeShort(ach_arr[li]);break;
                            }
                        }
                    };break;
                case ARRAY_SHORT :
                    {
                        short [] ash_arr = (short []) _array;
                        for(int li=0;li<ash_arr.length;li++)
                        {
                            switch(i_byteSize)
                            {
                                    case 1 : p_dos.writeByte(ash_arr[li]);break;
                                    case 2 : p_dos.writeShort(ash_arr[li]);break;
                            }
                        }
                    };break;
                case ARRAY_INT :
                    {
                        int [] ai_arr = (int []) _array;
                        for(int li=0;li<ai_arr.length;li++)
                        {
                            switch(i_byteSize)
                            {
                                    case 1 : p_dos.writeByte(ai_arr[li]);break;
                                    case 2 : p_dos.writeShort(ai_arr[li]);break;
                                    case 4 : p_dos.writeInt(ai_arr[li]);break;
                            }
                        }
                    };break;
                case ARRAY_LONG :
                    {
                        long [] al_arr = (long []) _array;
                        for(int li=0;li<al_arr.length;li++)
                        {
                        switch(i_byteSize)
                        {
                                case 1 : p_dos.writeByte((byte)al_arr[li]);break;
                                case 2 : p_dos.writeShort((short)al_arr[li]);break;
                                case 4 : p_dos.writeInt((int)al_arr[li]);break;
                                case 8 : p_dos.writeLong(al_arr[li]);break;
                        }
                        }
                    };break;
            }
        }

        p_dos.flush();
        p_dos.close();
    }

    private static boolean lg_pack;

    private static final Object loadTextArray(File _file) throws IOException
    {
        HashMap p_variables = new HashMap();
        HashMap p_globalvariables = new HashMap();

        BufferedReader p_reader = new BufferedReader(new FileReader(_file));
        int i_type = ARRAY_INT;
        long l_mask = 0xFFFFFFFF;
        boolean lg_packing = false;
        Vector p_values = new Vector(32000);
        int i_line = 0;
        while(true)
        {
           String s_str = p_reader.readLine();
           i_line ++;
           if (s_str == null) break;
           s_str = s_str.trim().toUpperCase();
           if (s_str.startsWith("//")||s_str.startsWith("#")|| s_str.length() == 0) continue;

           if (s_str.equals("$BYTE"))
           {
               i_type = ARRAY_BYTE;
               l_mask = 0xFFl;
           }
           else
           if (s_str.equals("$CHAR"))
           {
               i_type = ARRAY_CHAR;
               l_mask = 0xFFFFl;
           }
           else
           if (s_str.equals("$SHORT"))
           {
               i_type = ARRAY_SHORT;
               l_mask = 0xFFFFl;
           }
           else
           if (s_str.equals("$INT"))
           {
               i_type = ARRAY_INT;
               l_mask = 0xFFFFFFFFl;
           }
           else
           if (s_str.equals("$LONG"))
           {
               i_type = ARRAY_LONG;
               l_mask = 0xFFFFFFFFFFFFFFFFl;
           }
           else
           if (s_str.equals("$PACK"))
           {
               lg_packing = true;
           }
           else
           if (s_str.startsWith("$VAR"))
           {
               StringTokenizer p_strTknz = new StringTokenizer(s_str.substring(4).trim().toLowerCase(),"=");
               String s_name = p_strTknz.nextToken().trim();
               String s_eval = p_strTknz.nextToken().trim();
               if (p_variables.containsKey(s_name))
               {
                   System.err.println("You have duplicated varibale name \'"+s_name+"\' in line "+i_line);
                   System.exit(1);
               }
               Value p_value = null;
               try
               {
                   p_value = Operation.evaluateFormula(s_eval,p_globalvariables,p_variables);
               }
               catch (IOException e)
               {
                   System.err.print(e.getMessage()+" in line "+i_line);
                   System.exit(1);
               }
               p_variables.put(s_name,p_value);
           }
           else
           {
               StringTokenizer p_tokenizer = new StringTokenizer(s_str,",");
               while(p_tokenizer.hasMoreTokens())
               {
                   String s_tkn = p_tokenizer.nextToken();
                   s_tkn = s_tkn.trim().toLowerCase();
                   if (s_tkn.length() == 0) continue;
                   if (s_tkn.startsWith("("))
                   {
                       if (s_tkn.startsWith("(byte)"))
                       {
                           s_tkn = s_tkn.substring(6).trim();
                       }
                       else
                       if (s_tkn.startsWith("(char)"))
                       {
                           s_tkn = s_tkn.substring(6).trim();
                       }
                       else
                       if (s_tkn.startsWith("(long)"))
                       {
                           s_tkn = s_tkn.substring(6).trim();
                       }
                       else
                       if (s_tkn.startsWith("(int)"))
                       {
                           s_tkn = s_tkn.substring(5).trim();
                       }
                       else
                       if (s_tkn.startsWith("(short)"))
                       {
                           s_tkn = s_tkn.substring(7).trim();
                       }

                       Value p_value = Operation.evaluateFormula(s_tkn,p_globalvariables,p_variables);
                       if (p_value.getType() != Value.TYPE_INT) throw new IOException("You must have only integer data type ["+i_line+"]");
                       long l_res = ((Long)p_value.getValue()).longValue();
                       p_values.add(new Long(l_res));
                   }
                   else
                   {
                       Value p_value = Operation.evaluateFormula(s_tkn,p_globalvariables,p_variables);
                       if (p_value.getType() != Value.TYPE_INT) throw new IOException("You must have only integer data type ["+i_line+"]");
                       long l_res = ((Long)p_value.getValue()).longValue();
                       p_values.add(new Long(l_res));
                   }
               }
           }
        }

        // Формируем массив
        byte [] ab_byteArray = null;
        char [] ach_charArray = null;
        short [] ash_charArray = null;
        int [] ai_intArray = null;
        long [] al_longArray = null;

        int i_length = p_values.size();
        switch(i_type)
        {
                case ARRAY_BYTE :
                {
                   ab_byteArray = new byte[i_length];
                };break;
                case ARRAY_CHAR :
                {
                   ach_charArray = new char[i_length];
                };break;
                case ARRAY_INT :
                {
                   ai_intArray = new int[i_length];
                };break;
                case ARRAY_SHORT :
                {
                   ash_charArray = new short[i_length];
                };break;
                case ARRAY_LONG :
                {
                   al_longArray = new long[i_length];
                };break;
        }

        for(int li=0;li<p_values.size();li++)
        {
            long l_val = ((Long) p_values.elementAt(li)).longValue() & l_mask;

            switch(i_type)
            {
                    case ARRAY_BYTE :
                    {
                       ab_byteArray[li] = (byte) l_val;
                    };break;
                    case ARRAY_CHAR :
                    {
                       ach_charArray[li] = (char) l_val;
                    };break;
                    case ARRAY_INT :
                    {
                       ai_intArray[li] = (int)l_val;
                    };break;
                    case ARRAY_SHORT :
                    {
                       ash_charArray[li] = (short)l_val;
                    };break;
                    case ARRAY_LONG :
                    {
                       al_longArray[li] = l_val;
                    };break;
            }
        }

        lg_pack |= lg_packing;

        switch(i_type)
        {
                case ARRAY_BYTE :
                {
                   return ab_byteArray;
                }
                case ARRAY_CHAR :
                {
                   return ach_charArray;
                }
                case ARRAY_INT :
                {
                   return ai_intArray;
                }
                case ARRAY_SHORT :
                {
                   return ash_charArray;
                }
                case ARRAY_LONG :
                {
                   return al_longArray;
                }
        }

        return null;
    }


    
    private static final byte [] loadMAR (File _file,boolean _withoutZeroTile,int _imageCellsNumber) throws IOException
    {
        byte[] ab_Array = new byte[(int)_file.length()/2];

        FileInputStream p_fis = new FileInputStream(_file);
        DataInputStream p_instr = new DataInputStream(p_fis);

        for (int li = 0; li < ab_Array.length; li++)
        {
            int i_b0 = p_instr.readUnsignedByte();
            int i_b1 = p_instr.readUnsignedByte();
            int i_data = ((i_b1 << 8) | i_b0) / 32;
            if (_withoutZeroTile) i_data--;
            ab_Array[li] = (byte) i_data;
        }

        int i_tileWidth = _imageCellsNumber;

        for (int li = 0; li < ab_Array.length; li++)
        {
            int i_val = ab_Array[li] & 0xFF;
            int i_x = i_val % i_tileWidth;
            int i_y = i_val / i_tileWidth;

            switch(_imageCellsNumber)
            {
               case -1: ab_Array[li] = ab_Array[li];break;
               case 16: ab_Array[li] = (byte) ((i_x << 4) | (i_y & 0xF));break;
               case 8: ab_Array[li] = (byte) ((i_x << 5) | (i_y & 0x1F));break;
               case 4: ab_Array[li] = (byte) ((i_x << 6) | (i_y & 0x3F));break;
               case 2: ab_Array[li] = (byte) ((i_x << 7) | (i_y & 0x7F));break;
               case 1: ab_Array[li] = (byte) i_y;break;
                default : throw new IOException("Unsupported cells width number value");
            }
        }

        p_instr.close();
        p_instr = null;
        Runtime.getRuntime().gc();

        return ab_Array;
    }

    private static final void printArray(int [] _array)
    {
        System.out.print("Array ["+_array.length+"]=");
        for(int li=0;li<_array.length;li++)
        {
            System.out.print("0x"+Integer.toHexString(_array[li]));
            System.out.print(',');
        }
        System.out.println();
    }


    public static final void main(String [] _args)
    {
/*
        int [] ai_testArr = new int [] {0x243f6a88, 0x85a308d3, 0x13198a2e, 0x03707344, 0xa4093822, 0x299f31d0};

        ByteArrayOutputStream p_baos = new ByteArrayOutputStream();
        DataOutputStream p_daos = new DataOutputStream(p_baos);

        printArray(ai_testArr);

        try
        {
            saveBIN(p_daos,ai_testArr,false);
            ai_testArr = (int[])testloadArray(p_baos.toByteArray());
            printArray(ai_testArr);
        }
        catch (Exception e)
        {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }

        System.exit(1);
*/
        printBanner();

        String s_inFile = null;
        String s_outFile = null;
        String s_marFile = null;

        String s_tilesetImage  = null;
        String s_tilesetBlockWidth  = "-1";
        String s_tilesetBlockHeight  = "-1";

        lg_pack = false;
        boolean lg_withoutZero = false;

        int i_imageCellNumber = 16;

        for(int li=0;li<_args.length;li++)
        {
            String s_arg = _args[li];
            if (s_arg.equals("/h") || s_arg.equals("-?") || s_arg.equals("-h") || s_arg.equals("/?"))
            {
                printHelp();
                System.exit(0);
            }
            else
            if (s_arg.startsWith("/p"))
            {
                lg_pack = true;
            }
            else
            if (s_arg.startsWith("/m:"))
            {
                s_marFile = s_arg.substring(3);
            }
            else
            if (s_arg.startsWith("/i:"))
            {
                s_inFile = s_arg.substring(3);
            }
            else
            if (s_arg.equals("/z"))
            {
                lg_withoutZero = true;
            }
            else
            if (s_arg.startsWith("/o:"))
            {
                s_outFile = s_arg.substring(3);
            }
            else
            if (s_arg.startsWith("/t:"))
            {
                s_tilesetImage = s_arg.substring(3);
            }
            else
            if (s_arg.startsWith("/cw:"))
            {
                s_tilesetBlockWidth = s_arg.substring(4);
            }
            else
            if (s_arg.startsWith("/ch:"))
            {
                s_tilesetBlockHeight = s_arg.substring(4);
            }
            else
            if (s_arg.startsWith("/cells:"))
            {
                String s_cellsNum = s_arg.substring(7).trim();
                try
                {
                    i_imageCellNumber = Integer.parseInt(s_cellsNum);
                    switch(i_imageCellNumber)
                    {
                        case 16 :
                        case 8 :
                        case 4 :
                        case 2 :
                        case 1 : {};break;
                        default: throw new NumberFormatException();
                    }
                }
                catch (NumberFormatException e)
                {
                    System.err.println("Error image cells number, you must use 16,8,4,2,1");
                }
            }
            else
            {
                printHelp();
                System.exit(1);
            }
        }

        BufferedImage p_tilesetImage = null;

        if (s_tilesetImage!=null)
        {
            File p_file = new File(s_tilesetImage);
            if (!p_file.exists() || p_file.isDirectory())
            {
                System.err.println("Can't find the tileset image ["+s_tilesetImage+"]");
                System.exit(1);
            }

            try
            {
                p_tilesetImage = TileSetPacker.convertFileToImage(p_file);
            }
            catch (IOException e)
            {
                System.err.println("Can't load the tileset image ["+s_tilesetImage+"]");
                System.exit(1);
            }
            i_imageCellNumber = -1;

            int i_cw = -1;
            int i_ch = -1;

            try
            {
                i_cw = Integer.parseInt(s_tilesetBlockWidth);
                i_ch = Integer.parseInt(s_tilesetBlockHeight);
            }
            catch (NumberFormatException e)
            {
                System.err.println("ill-defined value for tileset image block width or height");
                System.exit(1);
            }

            byte [] ab_tilesetArray = null;
            try
            {
                ab_tilesetArray = TileSetPacker.packTileSet(p_tilesetImage,i_cw,i_ch);
            }
            catch (Exception e)
            {
                System.err.println("can't parse the tileset image ["+e.getMessage()+"]");
                System.exit(1);
            }

            String s_strTilesetFileName = "tileset";
            if (s_marFile!=null) s_strTilesetFileName = s_marFile;
            try
            {
                TileSetPacker.saveBinFile(new File(s_strTilesetFileName+".tls"),ab_tilesetArray);
            }
            catch (IOException e)
            {
                System.err.println("can't save the tileset block ["+s_strTilesetFileName+".tls]");
                System.exit(1);
            }
        }

        if (s_inFile == null && s_marFile == null)
        {
            System.err.println("You have not defined input file");
            System.exit(1);
        }

        Object p_array = null;

        if (s_marFile!=null)
        {
            File p_file = new File(s_marFile);
            try
            {
                p_array = loadMAR(p_file,lg_withoutZero,i_imageCellNumber);
            }
            catch (IOException e)
            {
                System.err.println("Error during reading of MAR file ["+e.getMessage()+"]");
                System.exit(1);
            }
            System.out.println("MAR file ["+p_file.getAbsolutePath()+"] has been loaded");
        }
        else
        {
            File p_file = new File(s_inFile);
            try
            {
                p_array = loadTextArray(p_file);
            }
            catch (IOException e)
            {
                System.err.println("Error during reading of array file ["+e.getMessage()+"]");
                System.exit(1);
            }
            System.out.println("Array file ["+p_file.getAbsolutePath()+"] has been loaded");
        }

        if (s_outFile == null)
        {
            if (s_marFile != null)
            {
                s_outFile = s_marFile+".bin";
            }
            else
            {
                s_outFile = s_inFile+".bin";
            }
        }

        File p_outfile = new File(s_outFile);
        try
        {
            FileOutputStream p_fos = new FileOutputStream(p_outfile);
            DataOutputStream p_dos = new DataOutputStream(p_fos);
            saveBIN(p_dos,p_array,lg_pack);
            p_dos.flush();
            p_dos.close();
            p_fos = null;
        }
        catch (IOException e)
        {
            System.err.println("Error during writing of ["+p_outfile.getAbsolutePath()+"]");
            System.exit(1);
        }
        System.out.println("Array has been saved as "+p_outfile.getAbsolutePath());
    }


    private static final Object testloadArray(byte [] _data) throws Exception
    {
        final int ARRAY_BYTE = 0;
        final int ARRAY_CHAR = 1;
        final int ARRAY_SHORT = 2;
        final int ARRAY_INT = 3;
        final int ARRAY_LONG = 4;

        ByteArrayInputStream p_insTr = new ByteArrayInputStream(_data);

        DataInputStream p_instr = new DataInputStream(p_insTr);

        byte[] ab_byteArr = null;
        char[] ach_charArr = null;
        short[] ash_shortArr = null;
        int[] ai_intArr = null;
        long[] al_longArr = null;

        int i_type = p_instr.readUnsignedByte();
        int i_length = p_instr.readUnsignedShort();
        int i_byteSize = p_instr.readUnsignedByte();

        //System.out.println("type "+i_type);
        //System.out.println("len "+i_length);
        //System.out.println("bsize "+i_byteSize);

        switch (i_type)
        {
            case ARRAY_BYTE:
                {
                    ab_byteArr = new byte[i_length];
                }
        ;
                break;
            case ARRAY_CHAR:
                {
                    ach_charArr = new char[i_length];
                }
        ;
                break;
            case ARRAY_SHORT:
                {
                    ash_shortArr = new short[i_length];
                }
        ;
                break;
            case ARRAY_INT:
                {
                    ai_intArr = new int[i_length];
                }
        ;
                break;
            case ARRAY_LONG:
                {
                    al_longArr = new long[i_length];
                }
        ;
                break;
        }

        if (p_instr.readUnsignedByte() == 1)
        {
            // Пакованные данные
            int i_index = 0;
            while (i_index < i_length)
            {
                int i_len = p_instr.readUnsignedByte();
                long l_val = 0;
                switch (i_byteSize)
                {
                    case 1:
                        {
                            l_val = p_instr.readByte();
                        }
                ;
                        break;
                    case 2:
                        {
                            l_val = p_instr.readShort();
                        }
                ;
                        break;
                    case 4:
                        {
                            l_val = p_instr.readInt();
                        }
                ;
                        break;
                    case 8:
                        {
                            l_val = p_instr.readLong();
                        }
                ;
                        break;
                }

                int li = 0;

                while (i_len>=0)
                {
                    switch (i_type)
                    {
                        case ARRAY_BYTE:
                            {
                                ab_byteArr [i_index] = (byte) l_val;
                            }
                    ;
                            break;
                        case ARRAY_CHAR:
                            {
                                ach_charArr [i_index] = (char) l_val;
                            }
                    ;
                            break;
                        case ARRAY_SHORT:
                            {
                                ash_shortArr [i_index] = (short) l_val;
                            }
                    ;
                            break;
                        case ARRAY_INT:
                            {
                                ai_intArr [i_index] = (int) l_val;
                            }
                    ;
                            break;
                        case ARRAY_LONG:
                            {
                                al_longArr [i_index] = l_val;
                            }
                    ;
                            break;
                    }

                    i_index++;
                    i_len--;
                }
            }
        }
        else
        {
            int i_index = 0;
            while (i_index < i_length)
            {
                long l_val = 0;
                switch (i_byteSize)
                {
                    case 1:
                        {
                            l_val = p_instr.readByte();
                        }
                ;
                        break;
                    case 2:
                        {
                            l_val = p_instr.readShort();
                        }
                ;
                        break;
                    case 4:
                        {
                            l_val = p_instr.readInt();
                        }
                ;
                        break;
                    case 8:
                        {
                            l_val = p_instr.readLong();
                        }
                ;
                        break;
                }


                    switch (i_type)
                    {
                        case ARRAY_BYTE:
                            {
                                ab_byteArr [i_index] = (byte) l_val;
                            }
                    ;
                            break;
                        case ARRAY_CHAR:
                            {
                                ach_charArr [i_index] = (char) l_val;
                            }
                    ;
                            break;
                        case ARRAY_SHORT:
                            {
                                ash_shortArr [i_index] = (short) l_val;
                            }
                    ;
                            break;
                        case ARRAY_INT:
                            {
                                ai_intArr [i_index] = (int) l_val;
                            }
                    ;
                            break;
                        case ARRAY_LONG:
                            {
                                al_longArr [i_index] = l_val;
                            }
                    ;
                            break;
                    }

                    i_index++;
            }
        }

        switch (i_type)
        {
            case ARRAY_BYTE:
                {
                    return ab_byteArr;
                }
            case ARRAY_CHAR:
                {
                    return ach_charArr;
                }
            case ARRAY_SHORT:
                {
                    return ash_shortArr;
                }
            case ARRAY_INT:
                {
                    return ai_intArr;
                }
            case ARRAY_LONG:
                {
                    return al_longArr;
                }
        }

        return null;
    }

}
