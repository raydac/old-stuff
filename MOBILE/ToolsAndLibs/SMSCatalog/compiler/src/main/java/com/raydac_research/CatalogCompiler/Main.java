package com.raydac_research.CatalogCompiler;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;

public class Main
{
    public static void main(String[] _args)
    {
        String s_fileName = _args[0];
        FileInputStream p_fis = null;
        FileOutputStream p_fos = null;
        FileOutputStream p_flags = null;
        try
        {
            p_fis = new FileInputStream(s_fileName);
            CatalogContainer p_compiler = new CatalogContainer(p_fis);

            p_fos = new FileOutputStream("cat.bin");

            p_flags = new FileOutputStream("catalogflags.txt");
            PrintStream p_prstr =new PrintStream(p_flags);

            p_prstr.println("//#excludeif true");

            p_compiler.saveToStream(p_fos,p_prstr);
            p_prstr.flush();
            p_fos.flush();
        }
        catch (IOException e)
        {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        finally
        {
            try
            {
                if (p_flags!=null) p_flags.close();
            }
            catch (Throwable e)
            {
            }
            try
            {
                if (p_fis!=null) p_fis.close();
            }
            catch (Throwable e)
            {
            }
            try
            {
                if (p_fos!=null) p_fos.close();
            }
            catch (Throwable e)
            {
            }
        }
    }
 }
