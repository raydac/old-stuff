package com.raydac_research.FormEditor.ExportModules.RrgScene.Resources;

public class RRGSceneResourceFont extends RRGSceneResource
{
    protected byte [] ab_fontArray;

    public byte[] getFontData()
    {
        return ab_fontArray;
    }

    public RRGSceneResourceFont(String _id,int _fontType, byte [] _fontData)
    {
        super(_id,RESOURCE_FONT,_fontType);
        ab_fontArray = _fontData;
    }
}
