package com.igormaznitsa.j2me_wtk;

import com.raydac_research.FormEditor.Misc.Utilities;

import javax.swing.table.TableModel;
import javax.swing.event.TableModelListener;
import javax.swing.event.TableModelEvent;
import javax.swing.*;
import java.util.Properties;
import java.util.Vector;
import java.io.IOException;

public class PropertiesTableModel implements TableModel
{
    private TableModelListener p_listener;

    private ProjectInfo p_ProjectInfo;

    public PropertiesTableModel()
    {
        setProjectInfo(null);
    }

    public void updated()
    {
        if (p_listener != null) p_listener.tableChanged(new TableModelEvent(this));
    }
    
    public int getColumnCount()
    {
        return 2;
    }

    public void setProjectInfo(ProjectInfo _projectInfo)
    {
        p_ProjectInfo = _projectInfo;
        if (p_listener!=null) p_listener.tableChanged(new TableModelEvent(this));
    }

    public int getRowCount()
    {
        if (p_ProjectInfo == null) return 0;
        Vector p_names = p_ProjectInfo.p_propertiesNames;
        return p_names.size();
    }

    public boolean isCellEditable(int rowIndex, int columnIndex)
    {
        if (columnIndex == 0)
            return false;
        else
            return true;
    }

    public Class getColumnClass(int columnIndex)
    {
        return String.class;
    }

    public Object getValueAt(int rowIndex, int columnIndex)
    {
        if (p_ProjectInfo == null) return null;
        Properties p_prop = p_ProjectInfo.getProperties();
        Vector p_names = p_ProjectInfo.p_propertiesNames;
        if (columnIndex>=p_names.size()) return null;
        if (columnIndex==0)
        {
            return (String) p_names.elementAt(rowIndex);
        }
        else
        {
            String s_val = p_prop.getProperty((String)p_names.elementAt(rowIndex));
            return s_val;
        }
    }

    public void setValueAt(Object aValue, int rowIndex, int columnIndex)
    {
        if (columnIndex == 1)
        {
            if (!(aValue instanceof String)) return;
            Properties p_prop = p_ProjectInfo.getProperties();
            Vector p_names = p_ProjectInfo.p_propertiesNames;
            String s_key = (String)p_names.elementAt(rowIndex);
            p_prop.setProperty(s_key,(String)aValue);
            p_ProjectInfo.updateMIDPCLDCVersions();

            try
            {
                p_ProjectInfo.saveJDDFile(p_ProjectInfo.p_JDDFile);
            }
            catch (IOException e)
            {
                Utilities.showErrorDialog(null,"Save error","I can't save JAD file.");
            }
        }
    }

    public String getColumnName(int columnIndex)
    {
        if (columnIndex == 0) return "Name"; else return "Value";
    }

    public void addTableModelListener(TableModelListener l)
    {
        p_listener = l;
    }

    public void removeTableModelListener(TableModelListener l)
    {
        if (l.equals(p_listener)) p_listener = null;
    }
}
