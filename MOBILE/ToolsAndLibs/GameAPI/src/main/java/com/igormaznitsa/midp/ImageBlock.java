package com.igormaznitsa.midp;

import javax.microedition.lcdui.Image;
import java.io.IOException;
import java.io.InputStream;
import java.io.DataInputStream;

/*
 * Copyright (C) 2002 Igor A. Maznitsa (igor.maznitsa@igormaznitsa.com).  All rights reserved.
 * Software is confidential and copyrighted. Title to Software and all associated intellectual property rights
 * is retained by Igor A. Maznitsa. You may not make copies of Software, other than a single copy of Software for
 * archival purposes.  Unless enforcement is prohibited by applicable law, you may not modify, decompile, or reverse
 * engineer Software.  You acknowledge that Software is not designed, licensed or intended for use in the design,
 * construction, operation or maintenance of any nuclear facility. Igor A. Maznitsa disclaims any express or implied
 * warranty of fitness for such uses.
 */
public class ImageBlock
{
    private Image[] _image_array;

    /**
     * Return the image for the id. If image with the id is not exists then return null
     * @param id
     * @return image as javax.microedition.lcdui.Image
     */
    public Image getImageForID(int id,boolean removeimage)
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
     * @throws IOException exception which throws when any problem in load or create time of an image
     */
    public ImageBlock(String resource_name,PhoneStub _phonestub) throws IOException
    {
        Runtime.getRuntime().gc();
        InputStream dis = getClass().getResourceAsStream(resource_name);

        DataInputStream ds = new DataInputStream(dis);
        // Reading of image number
        int num = ds.readUnsignedByte();
        _image_array = new Image[num];

        try
        {
            for (int li = 0;li < num;li++)
            {
                int i_imageindx = ds.readUnsignedByte();
                _image_array[i_imageindx] = _phonestub.getImageFromInputStream(ds);
                if (_image_array[li] == null) throw new IOException("I can't create image, indx[" + li + "]");
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
