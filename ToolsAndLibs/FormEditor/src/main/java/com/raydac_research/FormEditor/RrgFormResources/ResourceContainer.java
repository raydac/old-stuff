package com.raydac_research.FormEditor.RrgFormResources;

import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;
import javax.swing.event.TreeModelListener;
import javax.swing.event.TreeModelEvent;
import java.util.Vector;

public class ResourceContainer implements TreeModel
{
    public static final int MAX_COMPONENT_NUMBER = 1024;

    protected AbstractRrgResource [] ap_storage;
    protected int i_size;

    protected Object p_RootObject;
    protected TreeModelListener p_listener;


    public TreePath getTreePath(AbstractRrgResource _component)
    {
        return  new TreePath(new Object[]{p_RootObject,_component});
    }


    public int getSize()
    {
        return i_size;
    }

    public void isUpdated()
    {
        if (p_listener != null) p_listener.treeStructureChanged(new TreeModelEvent(this,new TreePath(new Object[]{p_RootObject})));
    }

    public void copy(ResourceContainer _container)
    {
        for(int li=0;li<ap_storage.length;li++)
        {
            ap_storage[li] = _container.ap_storage[li];
        }
        i_size = _container.i_size;

        if (p_listener != null) p_listener.treeStructureChanged(new TreeModelEvent(this,new TreePath(new Object[]{p_RootObject})));
    }

    public Vector getVectorForType(int _type)
    {
        Vector p_newVector = new Vector();
        for(int li=0;li<i_size;li++)
        {
            if (ap_storage[li].getType() == _type)
            {
                p_newVector.add(ap_storage[li]);
            }
        }
        return p_newVector;
    }

    public AbstractRrgResource [] getComponentArray()
    {
        return ap_storage;
    }

    public ResourceContainer()
    {
        p_listener = null;
        p_RootObject = new Object();
        ap_storage = new AbstractRrgResource[MAX_COMPONENT_NUMBER];
        i_size = 0;
     }

     public int getIndexForComponent(AbstractRrgResource _component)
     {
         for(int li=0;li<i_size;li++)
         {
             if (ap_storage[li].equals(_component)) return li;
         }
         return -1;
     }

     public void removeResourceForID(String _id)
     {
         for(int li=0;li<i_size;li++)
         {
             if (ap_storage[li].getResourceID().equals(_id))
             {
                 removeResourceForIndex(li);
             }
         }
     }

     public AbstractRrgResource getResourceForIndex(int _index)
     {
         if (_index<0 || _index>=i_size) return null;
         return ap_storage[_index];
     }

     public AbstractRrgResource getResourceForID(String _id)
     {
         for(int li=0;li<i_size;li++)
         {
             if (ap_storage[li].getResourceID().equals(_id)) return ap_storage[li];
         }
         return null;
     }

     public void removeAll()
     {
         for(int li=0;li<i_size;li++) ap_storage[li] = null;
         i_size = 0;

          if (p_listener != null) p_listener.treeStructureChanged(new TreeModelEvent(this,new TreePath(new Object[]{p_RootObject})));
     }

     public void removeResourceForIndex(int _index)
     {
         if (_index<0 || _index>=i_size) return;

         upList(_index);

         if (p_listener != null) p_listener.treeStructureChanged(new TreeModelEvent(this,new TreePath(new Object[]{p_RootObject})));
     }

     public void insertResource(int _index,AbstractRrgResource _component)
     {
          downList(_index);
          ap_storage[_index] = _component;

          if (p_listener != null) p_listener.treeStructureChanged(new TreeModelEvent(this,new TreePath(new Object[]{p_RootObject})));
     }

     public void moveResourceUp(int _index)
     {
         if (_index<=0 || _index>=i_size) return;
         AbstractRrgResource p_compo = ap_storage[_index-1];
         ap_storage[_index-1] = ap_storage[_index];
         ap_storage[_index] = p_compo;

          if (p_listener != null) p_listener.treeStructureChanged(new TreeModelEvent(this,new TreePath(new Object[]{p_RootObject})));
     }

    public boolean containsID(String _id)
    {
        if (_id == null) return false;
        for(int li=0;li<i_size;li++)
        {
            if (ap_storage[li].getResourceID().equals(_id)) return true;
        }
        return false;
    }

    public void addResource(AbstractRrgResource _resource)
    {
        if (_resource == null) return;
        if (!containsID(_resource.getResourceID()))
        {
            ap_storage[i_size] = _resource;
            i_size++;
            if (p_listener != null) p_listener.treeStructureChanged(new TreeModelEvent(this,new TreePath(new Object[]{p_RootObject})));
        }
    }

     public void moveComponentDown(int _index)
     {
         if (_index<0 || _index>=(i_size-1)) return;
         AbstractRrgResource p_compo = ap_storage[_index+1];
         ap_storage[_index+1] = ap_storage[_index];
         ap_storage[_index] = p_compo;

          if (p_listener != null) p_listener.treeStructureChanged(new TreeModelEvent(this,new TreePath(new Object[]{p_RootObject})));
     }

     protected void upList(int _startIndex)
     {
         for(int li=_startIndex;li<i_size;li++)
         {
             ap_storage[li] = ap_storage[li+1];
         }
         i_size --;
     }

    protected void downList(int _startIndex)
    {
        for(int li=i_size;li>=_startIndex;li--)
        {
            ap_storage[li] = ap_storage[li-1];
        }
        i_size ++;
    }

    public void addTreeModelListener(TreeModelListener l)
    {
       p_listener = l;
    }

    public Object getChild(Object parent, int index)
    {
        if (p_RootObject.equals(parent))
        {
            return ap_storage[index];
        }
        return null;
    }

    public int getChildCount(Object parent)
    {
        if (parent.equals(p_RootObject))
        {
            return i_size;
        }
        return 0;
    }

    public int getIndexOfChild(Object parent, Object child)
    {
        if (parent == null || child == null) return -1;
        if (p_RootObject.equals(parent))
        {
            return getIndexForComponent((AbstractRrgResource) child);
        }
        return -1;
    }

    public Object getRoot()
    {
        return p_RootObject;
    }

    public boolean isLeaf(Object node)
    {
        if (p_RootObject.equals(node)) return false;
        return true;
    }

    public void removeTreeModelListener(TreeModelListener l)
    {
        p_listener = null;
    }

    public void valueForPathChanged(TreePath path, Object newValue)
    {

    }
}
