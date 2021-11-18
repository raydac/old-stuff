package com.igormaznitsa.WToolkit.classes;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.util.HashSet;
import java.util.Iterator;

public class NameParameterDialog extends JDialog implements ActionListener
{
    JButton p_buttonOk,p_buttonCancel;
    JComboBox p_comboBox;
    String s_result;

    public NameParameterDialog(Dialog _owner)
    {
        super(_owner,"Create new parameter",true);
        getContentPane().setLayout(new BorderLayout(2,2));

        s_result = null;

        p_buttonOk = new JButton("Ok");
        p_buttonOk.addActionListener(this);
        p_buttonCancel = new JButton("Cancel");
        p_buttonCancel.addActionListener(this);

        HashSet p_set = new HashSet(32);
        for(int li=0;li<Panel.as_JADvalues.length;li++) p_set.add(Panel.as_JADvalues[li]);
        for(int li=0;li<Panel.as_ManifValues.length;li++) p_set.add(Panel.as_ManifValues[li]);

        p_comboBox = new JComboBox();
        p_comboBox.setEditable(true);
        Iterator p_iter = p_set.iterator();
        while(p_iter.hasNext())
            p_comboBox.addItem(p_iter.next());

        JPanel p_buttonPanel = new JPanel();
        p_buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
        p_buttonPanel.add(p_buttonOk);
        p_buttonPanel.add(p_buttonCancel);

        getContentPane().add(p_buttonPanel,BorderLayout.SOUTH);
        getContentPane().add(p_comboBox,BorderLayout.CENTER);

        pack();
        Dimension p_screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        setLocation(((int)p_screenSize.getWidth()-getWidth())>>1,((int)p_screenSize.getHeight()-getHeight())>>1);
    }

    public String inputString()
    {
        show();
        return s_result;
    }

    public void actionPerformed(ActionEvent e)
    {
        if (e.getSource().equals(p_buttonOk))
        {
            s_result = (String)p_comboBox.getSelectedItem();
        }
        else
        if (e.getSource().equals(p_buttonCancel))
        {
            s_result = null;
        }
        hide();
    }

}
