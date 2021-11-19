package com.raydac_research.FormEditor.ExportModules.RrgScene.Resources;

public class RRGSceneResource
{
    public static final int RESOURCE_IMAGE  = 0;
    public static final int RESOURCE_TEXT   = 1;
    public static final int RESOURCE_FONT   = 2;
    public static final int RESOURCE_SOUND  = 3;

    protected String s_ID;
    protected int i_Type;

    protected int i_poolId;

    protected int i_Offset;
    protected int i_FormatType;

    protected boolean lg_used;
    protected boolean lg_link;

    public String getID()
    {
        return s_ID;
    }

    public int getPoolID()
    {
        return i_poolId;
    }

    public void setPoolID(int _id)
    {
        i_poolId = _id;
    }

    public int getFormatType()
    {
        return i_FormatType;
    }

    public void setFormatType(int _formatType)
    {
        i_FormatType = _formatType;
    }

    public int getOffset()
    {
        return i_Offset;
    }

    public void setOffset(int _offset)
    {
        i_Offset = _offset;
    }

    public int getType()
    {
        return i_Type;
    }

    public boolean isLink()
    {
        return lg_link;
    }

    public void setLinkStatus(boolean _status)
    {
        lg_link = _status;
    }

    public boolean isUsed()
    {
        return lg_used;
    }

    public void setUsedFlag(boolean _flag)
    {
        lg_used = _flag;
    }

    public RRGSceneResource(String _id,int _type,int _formatType)
    {
        i_poolId = 0;
        i_Type = _type;
        s_ID = _id;
        i_FormatType = _formatType;
        lg_used = false;
        lg_link = false;
    }

    public int hashCode()
    {
        return s_ID.hashCode();
    }
}
