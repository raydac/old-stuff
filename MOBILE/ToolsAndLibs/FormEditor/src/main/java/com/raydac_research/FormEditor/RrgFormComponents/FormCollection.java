package com.raydac_research.FormEditor.RrgFormComponents;

import com.raydac_research.FormEditor.RrgFormResources.AbstractRrgResource;
import com.raydac_research.FormEditor.RrgFormResources.ResourceContainer;

import java.util.Vector;
import java.io.PrintStream;
import java.io.IOException;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;
import javax.swing.event.TreeModelListener;
import javax.swing.event.TreeModelEvent;

public class FormCollection implements TreeModel,TreeModelListener
{
    protected Vector p_forms;
    protected FormContainer p_selectedForm;
    protected TreeModelListener p_treeModeListener;

    public FormContainer getSelectedForm()
    {
        return p_selectedForm;
    }

    public void treeNodesChanged(TreeModelEvent e)
    {
        if (e.getSource().equals(p_selectedForm))
        {
            p_treeModeListener.treeNodesChanged(e);
        }
    }

    public void treeNodesInserted(TreeModelEvent e)
    {
        if (e.getSource().equals(p_selectedForm))
        {
            p_treeModeListener.treeNodesInserted(e);
        }
    }

    public void treeNodesRemoved(TreeModelEvent e)
    {
        if (e.getSource().equals(p_selectedForm))
        {
            p_treeModeListener.treeNodesRemoved(e);
        }
    }

    public void treeStructureChanged(TreeModelEvent e)
    {
        if (e.getSource().equals(p_selectedForm))
        {
            p_treeModeListener.treeStructureChanged(e);
        }
    }

    public void copy(FormCollection _object)
    {
        p_forms.removeAllElements();
        for(int li=0;li<_object.getFormsNumber();li++)
        {
            FormContainer p_newForm = new FormContainer("Untitled",10,10);
            p_newForm.copyFrom(_object.getFormAt(li));
            p_newForm.addTreeModelListener(this);
            p_forms.add(p_newForm);
        }
        selectForm(getFirstForm());
    }

    public void changeStateForComponents(int _state)
    {
        synchronized(p_forms)
        {
            for(int li=0;li<getFormsNumber();li++)
            {
                FormContainer p_container = getFormAt(li);
                p_container.changeStateForComponents(_state);
            }
        }
    }

    public void removeAllForms(FormContainer _newForm)
    {
        p_forms.removeAllElements();
        p_forms.add(_newForm);
        selectForm(_newForm);
    }

    public void selectForm(FormContainer _container)
    {
        if (p_forms.contains(_container))
        {
            p_selectedForm = _container;
            if (p_treeModeListener != null)
            {
                p_treeModeListener.treeStructureChanged(new TreeModelEvent(this,new Object[]{this}));
            }
        }
    }

    public FormCollection()
    {
        p_forms = new Vector();
    }

    public int getFormsNumber()
    {
        return p_forms.size();
    }

    public void addForm(FormContainer _form)
    {
        _form.addTreeModelListener(this);
        p_forms.add(_form);
    }

    public void removeForm(FormContainer _form)
    {
        p_forms.remove(_form);
    }

    public Vector getListOfComponentsUseResource(AbstractRrgResource _resource)
    {
        synchronized(p_forms)
        {
            Vector p_data = new Vector();
            for(int li=0;li<getFormsNumber();li++)
            {
                FormContainer p_container = getFormAt(li);
                Vector p_components = p_container.getListOfComponentsUseResource(_resource);
                p_data.addAll(p_components);
            }

            return p_data;
        }
    }

    public void saveAsXML(PrintStream _printStream)
    {
        synchronized(p_forms)
        {
            for(int li=0;li<getFormsNumber();li++)
            {
                FormContainer p_form = getFormAt(li);
                p_form._saveAsXML(_printStream);
            }
        }
    }

    public void loadFromXML(Element _element,ResourceContainer _container) throws IOException
    {
        synchronized(p_forms)
        {
            p_forms.removeAllElements();
            NodeList p_formList = _element.getElementsByTagName(FormContainer.XML_FORM_TAG);

            for(int li=0;li<p_formList.getLength();li++)
            {
                Element p_ele = (Element)p_formList.item(li);
                if (p_ele.getParentNode().equals(_element))
                {
                    FormContainer p_newForm = new FormContainer("s",5,5);
                    p_newForm._loadFromXML(p_ele,_container);
                    p_forms.add(p_newForm);
                }
            }
        }
    }

    public FormContainer getFirstForm()
    {
        if (p_forms.size()>0)
        {
            return getFormAt(0);
        }
        else
            return null;
    }

    public FormContainer getFormAt(int _index)
    {
        if (_index<0 || _index>=p_forms.size())
        {
            return null;
        }
        else
        {
            return (FormContainer) p_forms.elementAt(_index);
        }
    }

    public void addTreeModelListener(TreeModelListener lst)
    {
        p_treeModeListener = lst;
    }

    public Object getChild(Object parent, int index)
    {
        return getSelectedForm().getChild(parent,index);
    }

    public int getChildCount(Object parent)
    {
        return getSelectedForm().getChildCount(parent);
    }

    public int getIndexOfChild(Object parent, Object child)
    {
        return getSelectedForm().getIndexOfChild(parent, child);
    }

    public Object getRoot()
    {
        return getSelectedForm().getRoot();
    }

    public boolean isLeaf(Object node)
    {
        return getSelectedForm().isLeaf(node);
    }

    public void removeTreeModelListener(TreeModelListener l)
    {
        p_treeModeListener = null;
    }

    public void valueForPathChanged(TreePath path, Object newValue)
    {
        getSelectedForm().valueForPathChanged(path,newValue);
    }
}
