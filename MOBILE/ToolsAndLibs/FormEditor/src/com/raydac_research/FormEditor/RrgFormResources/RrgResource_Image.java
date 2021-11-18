package com.raydac_research.FormEditor.RrgFormResources;

import com.raydac_research.TGA.TGAImageReader;
import com.raydac_research.FormEditor.Misc.Utilities;

import javax.imageio.stream.MemoryCacheImageInputStream;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class RrgResource_Image extends AbstractRrgResource
{
    protected int i_width;
    protected int i_height;
    protected BufferedImage p_image;
    protected File p_imageFile;

    protected int i_width_trans;
    protected int i_height_trans;
    protected BufferedImage p_image_trans;
    protected File p_imageFile_trans;

    protected static final String XML_IMAGE = "image";
    protected static final String XML_FILE = "file";

    protected static TGAImageReader pTgaReader = new TGAImageReader();
    protected static MediaTracker pMediaTracker = new MediaTracker(new Button());

    public boolean refreshResource() throws IOException
    {
        setImageFile(p_imageFile);
        return true;
    }

    public void startTransaction()
    {
        super.startTransaction();

        i_width_trans = i_width;
        i_height_trans = i_height;

        p_image_trans = p_image;
        p_imageFile_trans = p_imageFile;
    }

    public void rollbackTransaction()
    {
        super.rollbackTransaction();

        i_width = i_width_trans;
        i_height = i_height_trans;

        p_image = p_image_trans;
        p_imageFile = p_imageFile_trans;
    }

    public RrgResource_Image(String _id,File _imageFile) throws IOException
    {
        super(_id,TYPE_IMAGE);
        setImageFile(_imageFile);
    }

    public BufferedImage getImage()
    {
        return p_image;
    }

    public File getImageFile()
    {
        return p_imageFile;
    }

    public static BufferedImage convertFileToImage(File _imageFile) throws IOException
    {
        byte [] ab_byte = new byte[(int)_imageFile.length()];
        FileInputStream p_fis = new FileInputStream(_imageFile);
        p_fis.read(ab_byte);
        p_fis.close();
        return convertFileToImage(ab_byte);
    }

    public static BufferedImage convertFileToImage(byte [] _imageFile) throws IOException
    {
        // May be  it is GIF or JPEG?
        BufferedImage pResultImage = null;
        Image pImage = Toolkit.getDefaultToolkit().createImage(_imageFile);

        pMediaTracker.addImage(pImage,1);

        try
        {
            pMediaTracker.waitForAll();
        }
        catch (InterruptedException e)
        {
            return null;
        }

        if (pMediaTracker.isErrorAny())
        {
           if ((pMediaTracker.statusID(1,true) & MediaTracker.ERRORED)!=0)
           {
               pMediaTracker.removeImage(pImage);
               pImage = null;
           }
        }
        else
        {
            pMediaTracker.removeImage(pImage);
        }


        if (pImage != null)
        {
            pResultImage = new BufferedImage(pImage.getWidth(null),pImage.getHeight(null),BufferedImage.TYPE_INT_ARGB);
            pResultImage.getGraphics().drawImage(pImage,0,0,null);
            pImage = null;
        }
        else
        {
            // May be that file is a TGA file?
            try
            {
                ByteArrayInputStream pFis = new ByteArrayInputStream(_imageFile);
                pResultImage = pTgaReader.read(new MemoryCacheImageInputStream(pFis));
            }
            catch(IOException ex)
            {
                pResultImage = null;
            }
        }

        if (pResultImage == null)
        {
            throw new IOException("Unsupported image format, you must use GIF,JPEG or TGA format");
        }

        return pResultImage;
    }

    public void setImageFile(File _imageFile) throws IOException
    {
        p_image = null;
        p_imageFile = null;

        if (_imageFile == null) return;

        p_image = convertFileToImage(_imageFile);
        i_width = p_image.getWidth(null);
        i_height = p_image.getHeight(null);
        p_imageFile = _imageFile;
    }

    public int getWidth()
    {
        return i_width;
    }

    public int getHeight()
    {
        return i_height;
    }

    public void _saveAsXML(PrintStream _stream,File _file, boolean _relative)
    {
        if (_stream == null) return;
        String s_str = "<"+XML_RESOURCE_INFO+">";
        _stream.println(s_str);
        String s_file = p_imageFile == null ? "" : _relative ? Utilities.calcRelativePath(_file,p_imageFile) : p_imageFile.getAbsolutePath();
        s_str="<"+XML_IMAGE+" "+XML_FILE+"=\""+s_file+"\"/>";
        _stream.println(s_str);
        _stream.println("</"+XML_RESOURCE_INFO+">");
    }

    public void _loadFromXML(File _file,Element _element) throws IOException
    {
        NodeList p_images =  _element.getElementsByTagName(XML_IMAGE);
        if (p_images.getLength() == 0) throw new IOException("<"+XML_IMAGE+"> format error");
        Element p_image = (Element)p_images.item(0);
        String s_fileName = p_image.getAttribute(XML_FILE);

        File p_imageFile = Utilities.isAbsolutePath(s_fileName) ?  new File(s_fileName) : new File(_file,s_fileName);
        setImageFile(p_imageFile);
    }
}
