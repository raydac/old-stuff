package com.raydac_research.FormEditor.ExportModules.RrgScene.Components;

import com.raydac_research.FormEditor.ExportModules.RrgScene.Resources.RRGSceneResourceImage;
import com.raydac_research.FormEditor.ExportModules.RrgScene.RrgScene;

import java.io.DataOutputStream;
import java.io.IOException;

public class RRGSceneComponent_Image extends RRGSceneComponent
{
    protected RRGSceneResourceImage p_image;
    protected int i_scale;
    protected int i_modifiers;

    public static final int MODIFIER_NONE = 0;
    public static final int MODIFIER_FLIPV = 1;
    public static final int MODIFIER_FLIPH = 2;

    public RRGSceneResourceImage getImage()
    {
        return p_image;
    }

    public int getScale()
    {
        return i_scale;
    }

    public RRGSceneComponent_Image(String _id,int _componentId,RRGSceneResourceImage _image,int _x,int _y,int _width,int _height,int _scale,int _modifiers)
    {
        super(_componentId,_id, RrgScene.TYPE_COMPONENT_IMAGE,_x,_y,_width,_height);
        p_image = _image;
        i_scale = _scale;
        i_modifiers = _modifiers;
    }

    public void saveComponentIntoStream(DataOutputStream _stream, boolean _saveChannel, boolean _saveResourceOffsets, boolean _isImageSection, boolean _isSoundSection, boolean _isTextSection, boolean _isFontSection, boolean _saveSounds, boolean _saveFonts, boolean _saveImages, boolean _saveTexts) throws IOException
    {
        super.saveComponentIntoStream(_stream, _saveChannel, _saveResourceOffsets, _isImageSection, _isSoundSection, _isTextSection, _isFontSection, _saveSounds, _saveFonts, _saveImages, _saveTexts);

        if (_isImageSection)
        {
            _stream.writeByte(p_image.getPoolID());
            _stream.writeByte(i_scale);
            _stream.writeByte(i_modifiers);

            if (_saveResourceOffsets && _saveImages)
            {
                _stream.writeShort(p_image.getOffset());
            }
        }
    }
}
