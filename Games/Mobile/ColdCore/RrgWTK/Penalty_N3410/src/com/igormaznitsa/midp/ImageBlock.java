package com.igormaznitsa.midp;

import com.igormaznitsa.gameapi.LoadListener;

import javax.microedition.lcdui.Image;
import java.io.IOException;
import java.io.InputStream;
import java.io.DataInputStream;

public class ImageBlock
{
    private Image[] _image_array;

    /**
     * Return the image for the id. If image with the id is not exists then return null
     * @param id
     * @return image as javax.microedition.lcdui.Image
     */
    public Image getImageForID(int id, boolean removeimage)
    {
        Image _img = _image_array[id];
        if (removeimage)
        {
            _image_array[id] = null;
            Runtime.getRuntime().gc();
        }
        return _img;
    }

    /**
     * Constructor
     * @param resource_name File name of resource contains PNG packed images
     * @param loadListener link to load listener object
     * @throws IOException exception which throws when any problem in load or create time of an image
     */
    public ImageBlock(String resource_name, LoadListener loadListener,PhoneStub _phonestub) throws IOException
    {
        Runtime.getRuntime().gc();
        InputStream dis = getClass().getResourceAsStream(resource_name);

        DataInputStream ds = new DataInputStream(dis);
        // Reading of image number
        int num = ds.readUnsignedByte();
        _image_array = new Image[num];

        try
        {
            for (int li = 0; li < num; li++)
            {
                _image_array[li] = _phonestub.getImageFromInputStream(ds);
                if (_image_array[li] == null) throw new IOException("I can't create image, indx[" + li + "]");
                if (loadListener != null) loadListener.nextItemLoaded(1);
                Runtime.getRuntime().gc();
            }
        }
        finally
        {
            if (dis != null)
            {
                try
                {
                    dis.close();
                }
                catch (IOException e)
                {
                }
                dis = null;
            }
            ds = null;
            Runtime.getRuntime().gc();
        }
    }
}
