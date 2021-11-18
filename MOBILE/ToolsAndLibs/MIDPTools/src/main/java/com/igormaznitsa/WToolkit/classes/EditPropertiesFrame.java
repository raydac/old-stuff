package com.igormaznitsa.WToolkit.classes;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.util.Properties;
import java.util.Vector;
import java.util.Enumeration;

public class EditPropertiesFrame extends JDialog implements ActionListener
{
    JTable p_table_Params;

    JButton p_Ok,p_Cancel,p_New,p_Remove;

    TableDataModel p_dataModel;

    static final int BUTTON_OK = 0;
    static final int BUTTON_CANCEL = 1;

    protected int i_result = BUTTON_CANCEL;

    class TableDataModel extends AbstractTableModel
    {
        Vector p_keyVector;
        Vector p_valueVector;

        boolean lg_edited = false;

        public String getColumnName(int column)
        {
            if (column == 0) return "Parameter"; else return "Value";
        }

        public TableDataModel()
        {
            p_keyVector = new Vector();
            p_valueVector = new Vector();
        }

        public boolean isCellEditable(int rowIndex, int columnIndex)
        {
            if (columnIndex == 0) return false;
            return true;
        }

        public int getColumnCount()
        {
            return 2;
        }

        public void removeKey(String key)
        {
            int i_indx = p_keyVector.lastIndexOf(key);
            if (i_indx >= 0)
            {
                p_keyVector.remove(i_indx);
                p_valueVector.remove(i_indx);
                this.fireTableDataChanged();
            }
        }

        public void addString(String key, String value)
        {
            int i_indx = p_keyVector.lastIndexOf(key);
            if (i_indx >= 0)
            {
                p_valueVector.setElementAt(value, i_indx);
                this.fireTableRowsUpdated(i_indx, i_indx);
            }
            else
            {
                p_keyVector.add(key);
                p_valueVector.add(value);
                this.fireTableDataChanged();
            }
        }

        public void removeRow(int _index)
        {
            if (_index < p_keyVector.size() && _index >= 0 && p_keyVector.size() > 0)
            {
                p_keyVector.removeElementAt(_index);
                p_valueVector.removeElementAt(_index);
                fireTableDataChanged();
            }
        }

        public int getRowCount()
        {
            return p_keyVector.size();
        }

        public void setValueAt(Object aValue, int rowIndex, int columnIndex)
        {
            if (rowIndex >= 0 && rowIndex < p_keyVector.size() && p_keyVector.size() > 0)
            {
                if (columnIndex == 1)
                {
                    if (!p_valueVector.elementAt(rowIndex).equals(aValue))
                    {
                        lg_edited = true;
                        p_valueVector.setElementAt(aValue, rowIndex);
                    }
                }
            }
        }

        public Object getValueAt(int rowIndex, int columnIndex)
        {
            if (columnIndex == 0)
            {
                return p_keyVector.elementAt(rowIndex);
            }
            else
            {
                return p_valueVector.elementAt(rowIndex);
            }
        }
    }

    public EditPropertiesFrame(Frame _owner, String _project, Properties _properties)
    {
        super(_owner, true);
        setTitle(_project);
        setSize(300, 300);

        Dimension p_screenSize = Toolkit.getDefaultToolkit().getScreenSize();

        setLocation(((int)p_screenSize.getWidth()-300)>>1,((int)p_screenSize.getHeight()-300)>>1);

        //setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);

        getContentPane().setLayout(new BorderLayout(2, 2));

        JPanel p_buttonspanel = new JPanel(new FlowLayout(FlowLayout.CENTER));

        p_Ok = new JButton("Ok");
        p_Ok.addActionListener(this);
        p_Cancel = new JButton("Cancel");
        p_Cancel.addActionListener(this);
        p_New = new JButton("New");
        p_New.addActionListener(this);
        p_Remove = new JButton("Remove");
        p_Remove.addActionListener(this);
        p_buttonspanel.add(p_Ok);
        p_buttonspanel.add(p_Cancel);
        p_buttonspanel.add(p_New);
        p_buttonspanel.add(p_Remove);

        getContentPane().add(p_buttonspanel, BorderLayout.SOUTH);

        p_dataModel = new TableDataModel();

        Enumeration p_keys = _properties.keys();
        while (p_keys.hasMoreElements())
        {
            String s_key = (String) p_keys.nextElement();
                String s_value = _properties.getProperty(s_key);
                if (s_value != null)
                    p_dataModel.addString(s_key, s_value);
        }


        p_table_Params = new JTable(p_dataModel);

        getContentPane().add(new JScrollPane(p_table_Params), BorderLayout.CENTER);
    }

    public Properties showDialog()
    {
        show();

        switch (i_result)
        {
            case BUTTON_OK:
                {
                    Properties p_prop = new Properties();
                    for(int li=0;li<p_dataModel.p_keyVector.size();li++)
                    {
                        String s_key = (String)p_dataModel.p_keyVector.elementAt(li);
                        String s_value = (String)p_dataModel.p_valueVector.elementAt(li);
                        p_prop.setProperty(s_key,s_value);
                    }
                    return p_prop;
                }
            case BUTTON_CANCEL:
                {
                    return null;
                }
        }
        return null;
    }

    public void actionPerformed(ActionEvent e)
    {
        if (e.getSource().equals(p_Ok))
        {
            i_result = BUTTON_OK;

            hide();
        }
        else if (e.getSource().equals(p_Cancel))
        {
            i_result = BUTTON_CANCEL;

            hide();
        }
        else if (e.getSource().equals(p_New))
        {
            NameParameterDialog p_NewParameterDialog = new NameParameterDialog(this);
            String s_string = p_NewParameterDialog.inputString();
            if (s_string != null)
            {
                s_string = s_string.trim();
                if (p_dataModel.p_keyVector.contains(s_string))
                {
                    JOptionPane.showMessageDialog(this,"I can't create the "+s_string+" parameter because it is exist already!","New parameter error!",JOptionPane.ERROR_MESSAGE);
                    return;
                }
                p_dataModel.addString(s_string,"");
            }
        }
            else if (e.getSource().equals(p_Remove))
            {
                int i_selectedRow = p_table_Params.getSelectedRow();
                if (i_selectedRow>=0)
                {
                    if (JOptionPane.OK_OPTION  == JOptionPane.showConfirmDialog(this,"Do you really want to remove "+p_dataModel.getValueAt(i_selectedRow,0)+"?","Remove parameter?",JOptionPane.YES_NO_OPTION))
                    {
                        p_dataModel.removeRow(i_selectedRow);
                    }
                }
            }

    }
}
