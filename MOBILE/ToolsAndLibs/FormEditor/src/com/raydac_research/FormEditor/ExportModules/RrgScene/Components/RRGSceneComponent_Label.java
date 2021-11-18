package com.raydac_research.FormEditor.ExportModules.RrgScene.Components;

import com.raydac_research.FormEditor.ExportModules.RrgScene.Resources.RRGSceneResourceText;
import com.raydac_research.FormEditor.ExportModules.RrgScene.Resources.RRGSceneResourceFont;
import com.raydac_research.FormEditor.ExportModules.RrgScene.Resources.RrgResourceSection;
import com.raydac_research.FormEditor.ExportModules.RrgScene.RrgScene;

import java.awt.*;
import java.io.DataOutputStream;
import java.io.IOException;

public class RRGSceneComponent_Label extends RRGSceneComponent
{
    protected RRGSceneResourceText p_Resource_text;
    protected RRGSceneResourceFont p_font;
    protected int i_Color;

    public int getColor()
    {
        return i_Color;
    }

    public RRGSceneResourceText getText()
    {
        return p_Resource_text;
    }

    public RRGSceneResourceFont getFont()
    {
        return p_font;
    }

    public RRGSceneComponent_Label(String _id,int _componentId,int _x,int _y,int _width,int _height, RRGSceneResourceText _Resource_text,RRGSceneResourceFont _font,Color _color)
    {
        super(_componentId,_id,RrgScene.TYPE_COMPONENT_LABEL,_x,_y,_width,_height);
        p_Resource_text = _Resource_text;
        p_font = _font;
        i_Color = _color.getRGB() & 0xFFFFFF;
    }

    public void saveComponentIntoStream(DataOutputStream _stream, boolean _saveChannel, boolean _saveResourceOffsets, boolean _isImageSection, boolean _isSoundSection, boolean _isTextSection, boolean _isFontSection, boolean _saveSounds, boolean _saveFonts, boolean _saveImages, boolean _saveTexts) throws IOException
    {
        super.saveComponentIntoStream(_stream, _saveChannel, _saveResourceOffsets, _isImageSection, _isSoundSection, _isTextSection, _isFontSection, _saveSounds, _saveFonts, _saveImages, _saveTexts);

        RrgResourceSection._write3bytes(i_Color,_stream);

        if (_isTextSection)
        {
            _stream.writeByte(p_Resource_text.getPoolID());

            if (_saveResourceOffsets && _saveTexts)
            {
                _stream.writeShort(p_Resource_text.getOffset());
            }
        }

        if (_isFontSection)
        {
            _stream.writeByte(p_font.getPoolID());

            if (_saveResourceOffsets && _saveFonts)
            {
                _stream.writeShort(p_font.getOffset());
            }
        }
    }
}
