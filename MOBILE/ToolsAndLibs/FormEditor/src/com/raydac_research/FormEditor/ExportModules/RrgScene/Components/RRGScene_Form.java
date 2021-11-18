package com.raydac_research.FormEditor.ExportModules.RrgScene.Components;

import com.raydac_research.FormEditor.ExportModules.RrgScene.Resources.RrgResourceSection;

import java.awt.*;
import java.io.DataOutputStream;
import java.io.IOException;

public class RRGScene_Form
{
    protected int i_id;
    protected int i_Width;
    protected int i_Height;

    protected int i_Color_Background;
    protected int i_Color_NormalText;
    protected int i_Color_SelectedText;
    protected int i_Color_PressedText;
    protected int i_Color_DisabledText;

    protected int i_Channel;
    protected String s_FormID;
    protected RRGScene_ComponentsSection p_components;

    public RRGScene_ComponentsSection getComponentsSection()
    {
        return p_components;
    }

    public String getFormID()
    {
        return s_FormID;
    }

    public int getID()
    {
        return i_id;
    }

    public RRGScene_Form(String _strId,int _id,int _width,int _height,int _channel,Color _background,Color _normalText,Color _selectedText,Color _pressedText,Color _disabledText)
    {
        p_components = new RRGScene_ComponentsSection();

        s_FormID = _strId;
        i_id = _id;
        i_Width = _width;
        i_Height = _height;

        i_Channel = _channel;

        i_Color_Background  = _background.getRGB()  & 0xFFFFFF;
        i_Color_NormalText  = _normalText.getRGB()  & 0xFFFFFF;
        i_Color_SelectedText= _selectedText.getRGB()& 0xFFFFFF;
        i_Color_PressedText = _pressedText.getRGB() & 0xFFFFFF;
        i_Color_DisabledText= _disabledText.getRGB()& 0xFFFFFF;
    }

    public void toStream(DataOutputStream _outStream, boolean _saveChannel,boolean _saveResourcesOffset,boolean _isImageSection,boolean _isFontSection,boolean _isTextSection,boolean _isSoundsSection,boolean _saveImages,boolean _saveFonts,boolean _saveSounds,boolean _saveTexts) throws IOException
    {
        _outStream.writeByte(i_id);

        if (_saveChannel)
        {
            _outStream.writeByte(i_Channel);
        }

        _outStream.writeShort(i_Width);
        _outStream.writeShort(i_Height);

        RrgResourceSection._write3bytes(i_Color_Background,_outStream);
        RrgResourceSection._write3bytes(i_Color_NormalText,_outStream);
        RrgResourceSection._write3bytes(i_Color_SelectedText,_outStream);
        RrgResourceSection._write3bytes(i_Color_PressedText,_outStream);
        RrgResourceSection._write3bytes(i_Color_DisabledText,_outStream);

        p_components.toStream(_outStream,_saveChannel,_saveResourcesOffset,_isImageSection,_isFontSection,_isTextSection,_isSoundsSection,_saveImages,_saveFonts,_saveSounds,_saveTexts);

        _outStream.flush();
    }
}
