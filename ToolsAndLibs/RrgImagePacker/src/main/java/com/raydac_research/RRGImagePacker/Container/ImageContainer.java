package com.raydac_research.RRGImagePacker.Container;

import javax.swing.tree.TreePath;
import java.util.Vector;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.DataInputStream;

public class ImageContainer
{
    protected String s_Name;
    protected Vector p_images;

    public ImageContainer(String _name)
    {
        p_images = new Vector();
        s_Name = _name;
    }

    public ImageCnt getImageForName(String _name)
    {
        synchronized (p_images)
        {
            for (int li = 0; li < p_images.size(); li++)
            {
                ImageCnt p_cnt = (ImageCnt) p_images.elementAt(li);
                if (_name.equals(p_cnt.getName())) return p_cnt;
            }
            return null;
        }
    }

    public void writeDataToStream(DataOutputStream _stream) throws IOException
    {
        synchronized (p_images)
        {
            // Количество изображений
            _stream.writeInt(p_images.size());
            for (int li = 0; li < p_images.size(); li++)
            {
                ImageCnt p_img = (ImageCnt) p_images.elementAt(li);
                p_img.saveToStream(_stream);
            }
        }
    }

    public void loadDataFromStream(PaletteContainer _palettes, DataInputStream _stream) throws IOException
    {
        synchronized (p_images)
        {
            // Количество изображений
            p_images.removeAllElements();
            int i_length = _stream.readInt();
            for (int li = 0; li < i_length; li++)
            {
                ImageCnt p_img = new ImageCnt();
                p_img.loadFromStream(this, _palettes, _stream);
                p_images.add(p_img);
            }
        }
    }

    public int getIndex(Object _obj)
    {
        synchronized (p_images)
        {
            for(int li=0;li<p_images.size();li++)
            {
                if (p_images.elementAt(li).equals(_obj)) return li;
            }
            return -1;
        }
    }

    public ImageCnt getImageAt(int _index)
    {
        synchronized (p_images)
        {
            if (_index < 0 || _index >= p_images.size())
            {
                System.out.println("Asked index "+_index);
                return null;
            }
            return (ImageCnt) p_images.elementAt(_index);
        }
    }

    public int getSize()
    {
        synchronized (p_images)
        {
            return p_images.size();
        }
    }

    public ImageCnt addImage()
    {
        synchronized (p_images)
        {
            ImageCnt p_newImg = new ImageCnt();
            p_images.add(p_newImg);
            return p_newImg;
        }
    }

    public void upImage(ImageCnt _image)
    {
        synchronized (p_images)
        {
            int i_index = getIndex(_image);
            if (i_index <= 0) return;

            Object p_prev = p_images.elementAt(i_index - 1);
            p_images.setElementAt(_image, i_index - 1);
            p_images.setElementAt(p_prev, i_index);
        }
    }

    public void downImage(ImageCnt _image)
    {
        synchronized (p_images)
        {
            int i_index = getIndex(_image);
            if (i_index >= (p_images.size() - 1)|| i_index<0) return;

            Object p_prev = p_images.elementAt(i_index + 1);
            p_images.setElementAt(_image, i_index + 1);
            p_images.setElementAt(p_prev, i_index);
        }
    }

    public String toString()
    {
        return s_Name;
    }
}
