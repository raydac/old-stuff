package com.igormaznitsa.WToolkit.classes;

import java.io.*;

public class FileUtils
{
    
    public static boolean deleteDir(File _removedDir)
    {
        deleteDirIml(_removedDir);
        boolean flag = false;
        try
        {
            _removedDir.delete();
            flag = _removedDir.isDirectory();
        }
        catch(SecurityException securityexception)
        {
            securityexception.printStackTrace();
        }
        return !flag;
    }

    private static void deleteDirIml(File file)
    {
        if(file.isDirectory())
        {
            String as[] = file.list();
            for(int i = 0; i < as.length; i++)
            {
                File file1 = new File(file, as[i]);
                if(file1.isDirectory())
                {
                    deleteDir(file1);
                    file1.delete();
                } else
                {
                    file1.delete();
                }
            }

        } else
        {
            file.delete();
        }
    }

    public static final boolean clearDirectory(File file)
    {
        if(file.isDirectory())
        {
            String as[] = file.list();
            for(int i = 0; i < as.length; i++)
            {
                File file1 = new File(file, as[i]);
                if(file1.isDirectory())
                {
                    if (!clearDirectory(file1)) return false;
                    if (!file1.delete()) return false;
                } else
                {
                    if (!file1.delete()) return false;
                }
            }
            return true;
        }
        return false;
    }

    public static void copyFile(File _src, File _dest)throws IOException
    {
        FileInputStream fileinputstream = null;
        FileOutputStream fileoutputstream = null;
        try
        {
            byte abyte0[] = new byte[1024];
            fileinputstream = new FileInputStream(_src);
            fileoutputstream = new FileOutputStream(_dest);
            int i;
            while((i = fileinputstream.read(abyte0)) > -1)
                fileoutputstream.write(abyte0, 0, i);
        }
        catch(FileNotFoundException filenotfoundexception)
        {
            throw new IOException("File Not Found: " + filenotfoundexception);
        }
        finally
        {
            if(fileinputstream != null) fileinputstream.close();
            if(fileoutputstream != null) fileoutputstream.close();
        }
    }
}
