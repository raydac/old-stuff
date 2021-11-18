package com.igormaznitsa.WToolkit.classes;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;

public class StreamCopier implements Runnable
{
    private final InputStream in;
    private final OutputStream out;

    public StreamCopier(InputStream inputstream, OutputStream outputstream)
    {
        in = inputstream;
        out = new BufferedOutputStream(outputstream);
    }

    public synchronized void run()
    {
        while (true)
        {
            try
            {
                int i = in.read();
                switch (i)
                {
                    case -1:
                        return;
                    case '\n':
                    case '\r':
                        {
                            out.write(i);
                            out.flush();
                        }
                        ;
                        break;
                    default:
                        out.write(i);
                }
            }
            catch (IOException ioexception)
            {
                return;
            }
        }
    }
}
