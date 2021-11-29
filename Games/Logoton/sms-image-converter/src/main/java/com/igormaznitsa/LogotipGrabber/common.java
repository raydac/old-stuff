/*
    Author : Igor A. Maznitsa
    EMail : rrg@forth.org.ru
    Date : 24.03.2002
*/
package com.igormaznitsa.LogotipGrabber;

import java.applet.Applet;
import java.awt.*;
import java.io.IOException;
import java.io.InputStream;

public class common
{
    public static Image loadImageResource(Applet cmp, String name) throws IOException
    {
        String resource = "/res/" + name;
        InputStream imgStream = cmp.getClass().getResourceAsStream(resource);
        if (imgStream == null) throw new IOException("Can't load resource image: " + resource);
        Toolkit tk = Toolkit.getDefaultToolkit();
        Image img = null;

        byte imageBytes[] = new byte[imgStream.available()];
        imgStream.read(imageBytes);
        img = tk.createImage(imageBytes);

        MediaTracker trcker = new MediaTracker(cmp);

        trcker.addImage(img, 0);
        try
        {
            trcker.waitForID(0);
        }
        catch (InterruptedException exx)
        {
            return null;
        }

        trcker.removeImage(img);

        return img;
    }
}
