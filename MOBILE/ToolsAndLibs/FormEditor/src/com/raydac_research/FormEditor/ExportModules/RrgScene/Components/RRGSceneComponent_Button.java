package com.raydac_research.FormEditor.ExportModules.RrgScene.Components;

import com.raydac_research.FormEditor.ExportModules.RrgScene.Resources.RRGSceneResourceFont;
import com.raydac_research.FormEditor.ExportModules.RrgScene.Resources.RRGSceneResourceText;
import com.raydac_research.FormEditor.ExportModules.RrgScene.Resources.RRGSceneResourceImage;
import com.raydac_research.FormEditor.ExportModules.RrgScene.Resources.RRGSceneResourceSound;
import com.raydac_research.FormEditor.ExportModules.RrgScene.RrgScene;

import java.io.DataOutputStream;
import java.io.IOException;

public class RRGSceneComponent_Button extends RRGSceneComponent
{
    protected RRGSceneResourceFont p_textFont;
    protected RRGSceneResourceText p_text;

    protected RRGSceneResourceImage p_NormalImage;
    protected RRGSceneResourceImage p_SelectedImage;
    protected RRGSceneResourceImage p_DisabledImage;
    protected RRGSceneResourceImage p_PressedImage;

    protected RRGSceneResourceSound p_ClickSound;

    protected int i_textAlign;

    public int getTextAlign()
    {
        return i_textAlign;
    }

    public RRGSceneResourceFont getTextFont()
    {
        return p_textFont;
    }

    public RRGSceneResourceText getText()
    {
        return p_text;
    }

    public RRGSceneResourceImage getNormalImage()
    {
        return p_NormalImage;
    }

    public RRGSceneResourceImage getSelectedImage()
    {
        return p_SelectedImage;
    }

    public RRGSceneResourceImage getDisabledImage()
    {
        return p_DisabledImage;
    }

    public RRGSceneResourceImage getPressedImage()
    {
        return p_PressedImage;
    }

    public RRGSceneComponent_Button(String _id,int _componentId,int _x,int _y,int _width,int _height,int _textAlign,RRGSceneResourceImage _normalImage,RRGSceneResourceImage _selectedImage,RRGSceneResourceImage _disabledImage,RRGSceneResourceImage _pressedImage,RRGSceneResourceFont _textFont,RRGSceneResourceText _text,RRGSceneResourceSound _clickSound)
    {
        super(_componentId,_id,RrgScene.TYPE_COMPONENT_BUTTON,_x,_y,_width,_height);
        i_textAlign = _textAlign;
        p_textFont = _textFont;
        p_text = _text;
        p_NormalImage = _normalImage;
        p_SelectedImage = _selectedImage;
        p_DisabledImage = _disabledImage;
        p_PressedImage = _pressedImage;
        p_ClickSound = _clickSound;
    }

    public void saveComponentIntoStream(DataOutputStream _stream, boolean _saveChannel, boolean _saveResourceOffsets, boolean _isImageSection, boolean _isSoundSection, boolean _isTextSection, boolean _isFontSection, boolean _saveSounds, boolean _saveFonts, boolean _saveImages, boolean _saveTexts) throws IOException
    {
        super.saveComponentIntoStream(_stream, _saveChannel, _saveResourceOffsets, _isImageSection, _isSoundSection, _isTextSection, _isFontSection, _saveSounds, _saveFonts, _saveImages, _saveTexts);

        // Normal image
        if (_isImageSection)
        {
            if (p_NormalImage == null)
            {
                _stream.writeByte(0);
            }
            else
            {
                _stream.writeByte(p_NormalImage.getPoolID());

                if (_saveResourceOffsets && _saveImages)
                {
                    _stream.writeShort(p_NormalImage.getOffset());
                }
            }

            if (p_SelectedImage == null)
            {
                _stream.writeByte(0);
            }
            else
            {
                _stream.writeByte(p_SelectedImage.getPoolID());

                if (_saveResourceOffsets && _saveImages)
                {
                    _stream.writeShort(p_SelectedImage.getOffset());
                }
            }

            if (p_DisabledImage == null)
            {
                _stream.writeByte(0);
            }
            else
            {
                _stream.writeByte(p_DisabledImage.getPoolID());

                if (_saveResourceOffsets && _saveImages)
                {
                    _stream.writeShort(p_DisabledImage.getOffset());
                }
            }

            if (p_PressedImage == null)
            {
                _stream.writeByte(0);
            }
            else
            {
                _stream.writeByte(p_PressedImage.getPoolID());

                if (_saveResourceOffsets && _saveImages)
                {
                    _stream.writeShort(p_PressedImage.getOffset());
                }
            }
        }

        if (_isTextSection)
        {
            if (p_text == null)
            {
                _stream.writeByte(0);
            }
            else
            {
                _stream.writeByte(p_text.getPoolID());

                if (_saveResourceOffsets && _saveTexts)
                {
                    _stream.writeShort(p_text.getOffset());
                }

                _stream.writeByte(i_textAlign);

                if (_isFontSection)
                {
                    if (p_textFont == null)
                    {
                        _stream.writeByte(0);
                    }
                    else
                    {
                        _stream.writeByte(p_textFont.getPoolID());

                        if (_saveResourceOffsets && _saveFonts)
                        {
                            _stream.writeShort(p_textFont.getOffset());
                        }
                    }
                }
            }
        }

        if (_isSoundSection)
        {
            if (p_ClickSound == null)
            {
                _stream.writeByte(0);
            }
            else
            {
                _stream.writeByte(p_ClickSound.getPoolID());

                if (_saveResourceOffsets && _saveSounds)
                {
                    _stream.writeShort(p_ClickSound.getOffset());
                }
            }
        }
    }
}
