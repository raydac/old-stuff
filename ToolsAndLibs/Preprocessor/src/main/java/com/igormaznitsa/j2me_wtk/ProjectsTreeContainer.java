package com.igormaznitsa.j2me_wtk;

import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;
import javax.swing.event.TreeModelListener;
import javax.swing.event.TreeModelEvent;
import java.util.Vector;
import java.io.File;
import java.io.IOException;

public class ProjectsTreeContainer implements TreeModel
{
    public Vector p_projects;
    public TreeModelListener p_treeListener;
    public File p_MainDirectory;

    public ProjectsTreeContainer()
    {
        p_projects = new Vector();
    }

    public void addTreeModelListener(TreeModelListener l)
    {
         p_treeListener = l;
    }

    public Object getChild(Object parent, int index)
    {
        if (this.equals(parent))
        {
            if (index>=0 && index<p_projects.size())
            {
                return p_projects.elementAt(index);
            }
            return null;
        }
        return null;
    }

    public int getChildCount(Object parent)
    {
        if (parent == null) return 0;
        if (parent.equals(this))
        {
            return p_projects.size();
        }
        return 0;
    }

    public int getIndexOfChild(Object parent, Object child)
    {
        if (parent.equals(this))
        {
            return p_projects.indexOf(child);
        }
        return -1;
    }

    public Object getRoot()
    {
        return this;
    }

    public boolean isLeaf(Object node)
    {
        if (node.equals(this)) return false;
        return true;
    }

    public void removeTreeModelListener(TreeModelListener l)
    {
        if (l.equals(p_treeListener))
        {
            p_treeListener = null;
        }
    }

    public String toString()
    {
        if (p_MainDirectory!=null) return p_MainDirectory.getAbsolutePath();
        return "<Not selected>";
    }

    public void valueForPathChanged(TreePath path, Object newValue)
    {

    }

    public String setMainDirectory(File _file)
    {
        p_MainDirectory = _file;
        p_projects.clear();

        StringBuffer p_strBuff = new StringBuffer(1024);

        if (_file!=null)
        {
            File [] ap_files = _file.listFiles();
            for(int li=0;li<ap_files.length;li++)
            {
                File p_f = ap_files[li];
                if (!p_f.isDirectory()) continue;

                try
                {
                    ProjectInfo p_info = new ProjectInfo(p_f);
                    p_projects.add(p_info);
                    p_strBuff.append(p_f.getName()+" has been added");
                }
                catch (IOException e)
                {
                    p_strBuff.append("!!! "+p_f.getName()+" is not a project ["+e.getMessage()+"]");
                }
                p_strBuff.append("\r\n");
            }
            sort(p_projects);
        }
        if (p_treeListener!=null) p_treeListener.treeStructureChanged(new TreeModelEvent(this,new Object[]{this}));
        return p_strBuff.toString();
    }

    private static void sort(Vector projects) {
        boolean changed;
        do {
            changed = false;
            for(int i=0;i<projects.size()-2;i++) {
                ProjectInfo a = (ProjectInfo) projects.get(i);
                ProjectInfo b = (ProjectInfo) projects.get(i+1);
                if (a.compare(b) > 0) {
                    projects.set(i, b);
                    projects.set(i+1, a);
                    changed = true;
                    break;
                }
            }
        } while (changed);
    }

}
