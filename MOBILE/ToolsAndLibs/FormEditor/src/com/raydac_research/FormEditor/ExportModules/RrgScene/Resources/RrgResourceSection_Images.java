package com.raydac_research.FormEditor.ExportModules.RrgScene.Resources;

import com.raydac_research.PNGWriter.PNGEncoder;
import com.raydac_research.FormEditor.ExportModules.RrgScene.RrgScene;

import java.io.IOException;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.util.Enumeration;
import java.util.Iterator;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;

public class RrgResourceSection_Images extends RrgResourceSection
{
    public RrgResourceSection_Images()
    {
        super();
    }

    public void addResource(RRGSceneResource _resource)
    {
        synchronized(p_resources)
        {
            RRGSceneResourceImage p_resource = (RRGSceneResourceImage) _resource;
            p_resource.setUsedFlag(false);

            // Ищем похожую картинку в списке и если есть то ассоциируем со ссылкой на неё
            Iterator p_iter = p_resources.values().iterator();


            RRGSceneResourceImage p_forLink = p_resource;
            while(p_iter.hasNext())
            {
                RRGSceneResourceImage p_resImage = (RRGSceneResourceImage) p_iter.next();

                if (p_resImage.isLink()) continue;

                if (isImageEquals(p_resImage.getImage(),p_resource.getImage()))
                {
                    p_forLink = p_resImage;
                    p_resource.setLinkStatus(true);
                    break;
                }
            }

            p_resources.put(p_resource.s_ID,p_forLink);
        }
    }

    protected boolean isImageEquals(BufferedImage _orig,BufferedImage _etal)
    {
        if (_orig.getWidth()!=_etal.getWidth()) return false;
        if (_orig.getHeight()!=_etal.getHeight()) return false;

        int[] ai_OrigImageBuffer = ((DataBufferInt) _orig.getRaster().getDataBuffer()).getData();
        int[] ai_EtalImageBuffer = ((DataBufferInt) _etal.getRaster().getDataBuffer()).getData();

        if (ai_EtalImageBuffer.length!=ai_OrigImageBuffer.length) return false;
        int i_len = ai_EtalImageBuffer.length;
        for(int li=0;li<i_len;li++)
        {
            if (ai_EtalImageBuffer[li]!=ai_OrigImageBuffer[li]) return false;
        }
        return true;
    }

    public byte[] saveResourceSection(boolean _writeType) throws IOException
    {
        Enumeration p_elements = p_resources.elements();

        ByteArrayOutputStream p_baos = new ByteArrayOutputStream(10000);
        DataOutputStream p_daos = new DataOutputStream(p_baos);

        int i_poolID = 1;

        int i_number = getUsedResourcesCount();

        p_daos.write(i_number);
        
        while(p_elements.hasMoreElements())
        {
            RRGSceneResourceImage p_image = (RRGSceneResourceImage) p_elements.nextElement();

            if (p_image.isUsed())
            {
                p_image.setPoolID(i_poolID++);
                p_image.setOffset(p_daos.size());

                p_daos.writeByte(p_image.getPoolID());

                if (_writeType)
                {
                    p_daos.writeByte(p_image.getFormatType());
                }

                byte [] ab_imageArray =  convertImageResourceToImage(p_image.getFormatType(),p_image.getImage());

/*
                FileOutputStream p_fos = new FileOutputStream("d:/"+p_image.getID()+".png");
                p_fos.write(ab_imageArray);
                p_fos.flush();
                p_fos.close();
*/
                
                int i_length = ab_imageArray.length;

                RrgScene.writeVLQ(p_daos,i_length);

                p_daos.write(ab_imageArray);
                p_daos.flush();

                ab_imageArray = null;
            }
        }

        p_daos.flush();
        p_daos.close();

        return p_baos.toByteArray();
    }

    protected byte[] convertImageResourceToImage(int _imageType, BufferedImage _image) throws IOException
    {
        ByteArrayOutputStream p_baos = new ByteArrayOutputStream();

        switch (_imageType)
        {
            case RrgScene.TYPE_IMAGE_PNG:
                {
                    PNGEncoder p_pngEncoder = new PNGEncoder(_image, p_baos, null, false);
                    p_pngEncoder.setCompressionLevel(9);
                    p_pngEncoder.setAlpha(true);
                    p_pngEncoder.encodeImage();
                }
                ;
                break;
            default :
                throw new IOException("Unsupported packing format");
        }

        return p_baos.toByteArray();
    }



}
