package com.igormaznitsa.WToolkit.classes;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class NewProjectDialog extends JDialog implements ActionListener
{
    private final JTextField projectNameInput = new JTextField(40);
    private final JTextField classNameInput = new JTextField(40);
    private final JButton p_Ok = new JButton("Ok");
    private final JButton p_Cancel = new JButton("Cancel");

    boolean lg_cancel = true;

    String s_projectName,s_mainClassName;

    private void addPair(Box box, String s, JComponent jcomponent)
    {
        jcomponent.setMaximumSize(jcomponent.getPreferredSize());
        JLabel jlabel = new JLabel(s);
        jlabel.setLabelFor(jcomponent);
        Box box1 = new Box(0);
        box1.add(jlabel);
        box1.add(Box.createGlue());
        box1.add(Box.createHorizontalStrut(5));
        box1.add(jcomponent);
        box.add(box1);
        box.add(Box.createGlue());
    }

    public boolean showDialog()
    {
        lg_cancel = true;
        show();

        if (lg_cancel) return false;
        return true;
    }

    public NewProjectDialog(JFrame _frame)
    {
        super(_frame, "Create new project", true);

        Box box = new Box(1);
        addPair(box, "Enter project name", projectNameInput);
        addPair(box, "Main class name", classNameInput);

        getContentPane().add("Center", box);
        JPanel p_buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        p_buttonPanel.add(p_Ok);
        p_Ok.addActionListener(this);
        p_buttonPanel.add(p_Cancel);
        p_Cancel.addActionListener(this);
        getContentPane().add("South",p_buttonPanel);

        pack();
        Dimension p_screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        setLocation(((int) p_screenSize.getWidth() - getWidth()) >> 1, ((int) p_screenSize.getHeight() - getHeight()) >> 1);
        s_mainClassName = null;
        s_projectName = null;
    }

    public void actionPerformed(ActionEvent e)
    {
        if (e.getSource().equals(p_Ok))
        {
            lg_cancel = false;
            s_mainClassName = classNameInput.getText().trim();
            s_projectName = projectNameInput.getText().trim();
            s_projectName = s_projectName.replace('/','_');
            s_projectName = s_projectName.replace('\\','_');
            s_projectName = s_projectName.replace('.','_');
        }
        else if (e.getSource().equals(p_Cancel))
        {

        }
        hide();
    }
}
