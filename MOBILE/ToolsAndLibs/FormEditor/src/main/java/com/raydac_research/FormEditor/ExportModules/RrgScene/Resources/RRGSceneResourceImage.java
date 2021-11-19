package com.raydac_research.FormEditor.ExportModules.RrgScene.Resources;

import java.awt.image.BufferedImage;

public class RRGSceneResourceImage extends RRGSceneResource
{
    protected BufferedImage p_Image;

    public RRGSceneResourceImage(String _id,int _imageType,BufferedImage _image)
    {
        super(_id,RESOURCE_IMAGE,_imageType);

        p_Image = _image;
    }

    public BufferedImage getImage()
    {
       return p_Image;
    }
}
