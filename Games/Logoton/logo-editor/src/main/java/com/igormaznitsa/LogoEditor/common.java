/*
    Author : Igor A. Maznitsa
    EMail : rrg@forth.org.ru
    Date : 24.03.2002
*/
package com.igormaznitsa.LogoEditor;

import java.applet.Applet;
import java.awt.*;
import java.io.IOException;
import java.io.InputStream;

public class common
{
    public static Image fillImage(Applet apl,Image source,int destwidth,int destheight)
    {
        MediaTracker trck = new MediaTracker(apl);
        Image _nimg = source.getScaledInstance(destwidth,destheight,Image.SCALE_FAST);

        trck.addImage(_nimg,0);
        try
        {
            trck.waitForID(0);
        }
        catch(InterruptedException ex)
        {
            return null;
        }

        return _nimg;
    }

    public static Image createImage(Applet apl, int width, int height)
    {
        MediaTracker trck = new MediaTracker(apl);
        Image new_image = apl.createImage(width,height);
        trck.addImage(new_image,0);
        try
        {
            trck.waitForID(0);
        }
        catch(InterruptedException ex)
        {
            return null;
        }

        return  new_image;
    }

    public static Image loadImageResource(Applet cmp, String name) throws IOException
    {
        name = "/res/" + name;
        InputStream imgStream = cmp.getClass().getResourceAsStream(name);

        if (imgStream == null) throw new IOException(name);
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
