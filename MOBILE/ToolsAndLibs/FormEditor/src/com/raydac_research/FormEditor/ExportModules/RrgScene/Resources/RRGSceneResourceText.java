package com.raydac_research.FormEditor.ExportModules.RrgScene.Resources;

public class RRGSceneResourceText extends RRGSceneResource
{
    protected String s_text;

    public String getText()
    {
        return s_text;
    }

    public RRGSceneResourceText(String _id,String _string)
    {
        super(_id,RESOURCE_TEXT,0);
        s_text = _string;
    }
}
