package com.igormaznitsa.j2me_wtk.forms;

import com.raydac_research.FormEditor.Misc.Utilities;

import javax.swing.*;
import java.awt.event.*;
import java.util.Properties;

public class NewVarDialog extends JDialog
        {
    private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;
    private JTextField p_Field_Name;
    private JTextField p_Field_Value;

    private Properties p_List;
    private boolean lg_result;
    private String s_Name;
    private String s_Value;

    public NewVarDialog(JFrame _parent)
    {
        super(_parent);
        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(buttonOK);

        buttonOK.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                onOK();
    }
        });

        buttonCancel.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                onCancel();
    }
        });

// call onCancel() when cross is clicked
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter()
        {
            public void windowClosing(WindowEvent e)
            {
                onCancel();
    }
        });

// call onCancel() on ESCAPE
        contentPane.registerKeyboardAction(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                onCancel();
    }
        }, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);

        setResizable(false);
}

    public boolean showDialog(String _title,boolean _enableChangeName,String _name,String _value,Properties _list)
    {
        Utilities.toScreenCenter(this);

        setTitle(_title);

        p_Field_Name.setEnabled(_enableChangeName);
        p_Field_Name.setText(_name);
        p_Field_Value.setText(_value);

        p_List = _list;

        pack();
        setVisible(true);

        return lg_result;
    }

    public String getName()
    {
        return p_Field_Name.getText();
    }

    public String getValue()
    {
        return p_Field_Value.getText();
    }

    private void onOK()
    {
// add your code here
        if (p_Field_Name.isEnabled())
        {
            // Проверяем на уникальность
            String s_name = p_Field_Name.getText().trim();
            p_Field_Name.setText(s_name);
            if (p_List.getProperty(s_name)!=null)
            {
                Utilities.showErrorDialog(this,"Duplicated key","You have entered a duplicated key!");
                return;
            }
        }

        s_Name = p_Field_Name.getText().trim();
        s_Value = p_Field_Value.getText().trim();

        lg_result = true;
        dispose();
}

    private void onCancel()
    {
// add your code here if necessary
        s_Name = null;
        s_Value = null;
        p_List = null;
        lg_result = false;
        dispose();
    }
}
