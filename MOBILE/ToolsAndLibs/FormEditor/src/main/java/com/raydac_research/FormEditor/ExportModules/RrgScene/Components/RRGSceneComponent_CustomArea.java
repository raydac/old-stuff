package com.raydac_research.FormEditor.ExportModules.RrgScene.Components;

import com.raydac_research.FormEditor.ExportModules.RrgScene.RrgScene;

import java.io.DataOutputStream;
import java.io.IOException;

public class RRGSceneComponent_CustomArea  extends RRGSceneComponent
{
    protected int i_areaValue;

    public RRGSceneComponent_CustomArea(String _id,int _componentID, int _x, int _y, int _width, int _height,int _areaValue)
    {
        super(_componentID, _id, RrgScene.TYPE_COMPONENT_CUSTOMAREA, _x, _y, _width, _height);
        i_areaValue = _areaValue;
    }

    public void saveComponentIntoStream(DataOutputStream _stream, boolean _saveChannel, boolean _saveResourceOffsets, boolean _isImageSection, boolean _isSoundSection, boolean _isTextSection, boolean _isFontSection, boolean _saveSounds, boolean _saveFonts, boolean _saveImages, boolean _saveTexts) throws IOException
    {
        super.saveComponentIntoStream(_stream, _saveChannel, _saveResourceOffsets, _isImageSection, _isSoundSection, _isTextSection, _isFontSection, _saveSounds, _saveFonts, _saveImages, _saveTexts);
        _stream.writeInt(i_areaValue);
    }

}
