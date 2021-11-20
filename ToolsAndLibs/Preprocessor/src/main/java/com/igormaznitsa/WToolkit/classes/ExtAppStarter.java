package com.igormaznitsa.WToolkit.classes;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class ExtAppStarter
{
    public static final ExtAppStructure startApp(StringArray _stringArray,String [] _vars) throws IOException
    {
        Process process = null;
        if (_vars == null)
            process = Runtime.getRuntime().exec(_stringArray.toStringArray());
        else
            process = Runtime.getRuntime().exec(_stringArray.toStringArray(),_vars);

        ByteArrayOutputStream p_baos_out = new ByteArrayOutputStream();
        ByteArrayOutputStream p_baos_err = new ByteArrayOutputStream();

        StreamCopier p_outStreamCopier = new StreamCopier(process.getInputStream(), p_baos_out);
        StreamCopier p_errStreamCopier = new StreamCopier(process.getErrorStream(), p_baos_err);
        int i_state = 0;
        (new Thread(p_outStreamCopier)).start();
        (new Thread(p_errStreamCopier)).start();
        try
        {
            Thread.sleep(100);
            i_state = process.waitFor();
            Thread.sleep(100);
        }
        catch (InterruptedException interruptedexception)
        {
            return null;
        }

        return new ExtAppStructure(i_state,p_baos_out.toByteArray(),p_baos_err.toByteArray());
    }
}
