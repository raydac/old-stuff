package com.raydac_research.FormEditor.ExportModules.RrgScene.Components;

import com.raydac_research.FormEditor.RrgFormComponents.FormContainer;

import java.io.DataOutputStream;
import java.io.IOException;

public class RRGSceneComponent
{
    protected int i_Type;
    protected int i_X;
    protected int i_Y;
    protected int i_Width;
    protected int i_Height;
    protected int i_channel;
    protected int i_componentID;
    protected String s_ID;

    public void setComponentID(int _componentID)
    {
        i_componentID = _componentID;
    }

    public int getComponentID()
    {
        return i_componentID;
    }

    public int getChannel()
    {
        return i_channel;
    }

    public int getType()
    {
        return i_Type;
    }

    public int getX()
    {
        return i_X;
    }

    public int getY()
    {
        return i_Y;
    }

    public int getWidth()
    {
        return i_Width;
    }

    public int getHeight()
    {
        return i_Height;
    }

    public String getStringID()
    {
        return s_ID;
    }

    public RRGSceneComponent(int _componentID,String _id,int _type,int _x,int _y,int _width,int _height)
    {
        s_ID = _id;
        i_Type = _type;
        i_X = _x;
        i_Y = _y;
        i_Width = _width;
        i_Height = _height;
        i_componentID = _componentID;
    }

    public void saveComponentIntoStream(DataOutputStream _stream,  boolean _saveChannel,boolean _saveResourceOffsets,boolean _isImageSection,boolean _isSoundSection,boolean _isTextSection,boolean _isFontSection,boolean _saveSounds,boolean _saveFonts,boolean _saveImages,boolean _saveTexts) throws IOException
    {
        _stream.writeByte(i_Type);
        _stream.writeByte(i_componentID);

        if (_saveChannel)
        {
            _stream.writeByte(i_channel);
        }

        _stream.writeShort(i_X);
        _stream.writeShort(i_Y);
        _stream.writeShort(i_Width);
        _stream.writeShort(i_Height);

        _stream.flush();
    }
}
