package com.raydac_research.FormEditor.Misc;

import javax.swing.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.*;

public class AboutForm implements ActionListener
{
    private JLabel ImageLabelForm;
    private JButton ButtonOk;
    private JTextArea TextArea;

    private JDialog p_Dlg;

    private static final String s_AboutText = "Mobile Project Builder\r\nv 4.5b (11 Nov 2005)\r\n©2005 Raydac Research Group Ltd.\r\nhttp://www.raydac-research.com";
    private JPanel MainPanel;

    public AboutForm(JFrame _frame)
    {
        ImageIcon p_Icon = Utilities.loadIconFromResource("felogo.gif");
        ImageLabelForm.setIcon(p_Icon);

        p_Dlg = new JDialog(_frame,"About",true);
        TextArea.setText(s_AboutText);

        MainPanel.setBackground(Color.white);
        p_Dlg.setContentPane(MainPanel);

        p_Dlg.setResizable(false);
        p_Dlg.pack();

        Utilities.toScreenCenter(p_Dlg);

        ButtonOk.addActionListener(this);

        p_Dlg.setVisible(true);
    }

    public void actionPerformed(ActionEvent e)
    {
        p_Dlg.setVisible(false);
    }
}
