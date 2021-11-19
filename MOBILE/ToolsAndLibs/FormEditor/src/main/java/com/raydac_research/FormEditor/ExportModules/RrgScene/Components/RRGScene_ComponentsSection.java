package com.raydac_research.FormEditor.ExportModules.RrgScene.Components;

import java.util.Vector;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ByteArrayOutputStream;

public class RRGScene_ComponentsSection
{
    protected Vector p_components;

    public RRGScene_ComponentsSection()
    {
        p_components = new Vector();
    }

    public int getSize()
    {
        return p_components.size();
    }

    public RRGSceneComponent getComponentAt(int _index)
    {
        if (_index<0 || _index>=p_components.size()) return null;
        return (RRGSceneComponent) p_components.elementAt(_index);
    }

    public void addComponent(RRGSceneComponent _component)
    {
        p_components.addElement(_component);
    }

    public void toStream (DataOutputStream _outStream,boolean _saveChannel, boolean _saveResourcesOffset,boolean _isImageSection,boolean _isFontsSection,boolean _isTextsSection,boolean _isSoundsSection,boolean _saveImages,boolean _saveFonts,boolean _saveSounds,boolean _saveTexts) throws IOException
    {
        _outStream.writeByte(p_components.size());

        for(int li=0;li<p_components.size();li++)
        {
            RRGSceneComponent p_component = (RRGSceneComponent) p_components.elementAt(li);
            p_component.saveComponentIntoStream(_outStream,_saveChannel,_saveResourcesOffset,_isImageSection,_isSoundsSection,_isTextsSection,_isFontsSection,_saveSounds,_saveFonts,_saveImages,_saveTexts);
        }

        _outStream.flush();
    }
}
