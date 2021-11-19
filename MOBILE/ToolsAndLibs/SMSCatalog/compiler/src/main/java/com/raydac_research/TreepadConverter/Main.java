package com.raydac_research.TreepadConverter;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.FileOutputStream;
import java.io.PrintStream;

public class Main
{
    public static void main(String [] _args)
    {
        HJTDocument p_doc = null;
        FileInputStream p_fis = null;
        PrintStream p_fos = null;
        try
        {
            String s_fileName= _args[0];
            p_fis = new FileInputStream(s_fileName);
            p_doc = new HJTDocument(p_fis);
            p_fis.close();
            p_fis = null;

            System.out.println("Document has been loaded");
            System.out.println("Nodes number = "+p_doc.getNodesNumber());

            p_fos = new PrintStream(new FileOutputStream(s_fileName+".xml"));

            HJT2SMSCatalog.convert(p_fos,p_doc);
        }
        catch (Throwable e)
        {
            e.printStackTrace();
            System.exit(1);
        }
        finally
        {
            try
            {
                if (p_fis!=null) p_fis.close();
            }
            catch (IOException e)
            {
            }
            p_fis = null;
            if (p_fos!=null) p_fos.close();
        }
    }
}
