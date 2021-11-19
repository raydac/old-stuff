package com.raydac_research.FormEditor.ExportModules.RrgScene.Resources;

public class RRGSceneResourceSound extends RRGSceneResource
{
    protected byte[] ab_soundData;
    protected int i_soundTimeLength;

    public byte [] getSoundData()
    {
        return ab_soundData;
    }

    public int getTimeLength()
    {
        return i_soundTimeLength;
    }

    public RRGSceneResourceSound(String _id,int _soundType,byte[] _soundData,int _soundTimeLength)
    {
        super(_id,RESOURCE_SOUND,_soundType);
        ab_soundData = _soundData;
        i_soundTimeLength = _soundTimeLength;
    }
}
