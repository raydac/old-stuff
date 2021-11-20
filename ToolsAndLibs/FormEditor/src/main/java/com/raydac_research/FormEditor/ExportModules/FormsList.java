package com.raydac_research.FormEditor.ExportModules;

import com.raydac_research.FormEditor.RrgFormComponents.FormContainer;

import java.util.Vector;

public class FormsList
{
    private Vector p_forms;

    public FormsList()
    {
        p_forms = new Vector();
    }

    public FormContainer getFirstForm()
    {
        if (p_forms.size()>0)
        {
            return (FormContainer) p_forms.firstElement();
        }
        else
            return null;
    }

    public void clear()
    {
        p_forms.removeAllElements();
    }

    public void add(FormContainer _form)
    {
        p_forms.add(_form);
    }

    public int getSize()
    {
        return p_forms.size();
    }

    public FormContainer getFormAt(int _index)
    {
        if (_index<0 || _index>=p_forms.size()) return null;
        return (FormContainer) p_forms.elementAt(_index);
    }
}
