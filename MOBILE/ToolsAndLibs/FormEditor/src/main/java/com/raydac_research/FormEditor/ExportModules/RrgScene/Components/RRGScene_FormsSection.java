package com.raydac_research.FormEditor.ExportModules.RrgScene.Components;

import java.util.Vector;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class RRGScene_FormsSection
{
    Vector p_forms;

    public RRGScene_FormsSection()
    {
        p_forms = new Vector();
    }

    public int getSize()
    {
        return p_forms.size();
    }

    public void removeAll()
    {
        p_forms.removeAllElements();
    }

    public void addForm(RRGScene_Form _form)
    {
        p_forms.add(_form);
    }

    public RRGScene_Form getForm(int _index)
    {
        if (_index<0 || _index>=p_forms.size()) return null;
        return (RRGScene_Form) p_forms.elementAt(_index);
    }

    public byte [] toByteArray(boolean _saveChannel,boolean _saveResourcesOffset,boolean _isImageSection,boolean _isFontSection,boolean _isTextSection,boolean _isSoundsSection,boolean _saveImages,boolean _saveFonts,boolean _saveSounds,boolean _saveTexts) throws IOException
    {
        ByteArrayOutputStream p_baos = new ByteArrayOutputStream(32000);
        DataOutputStream p_daos = new DataOutputStream(p_baos);

        p_daos.writeByte(getSize());

        for(int li=0;li<getSize();li++)
        {
            RRGScene_Form p_form = getForm(li);
            p_form.toStream(p_daos,_saveChannel,_saveResourcesOffset,_isImageSection,_isFontSection,_isTextSection,_isSoundsSection,_saveImages,_saveFonts,_saveSounds,_saveTexts);
        }

        p_daos.flush();
        p_daos.close();

        return p_baos.toByteArray();
    }

}
