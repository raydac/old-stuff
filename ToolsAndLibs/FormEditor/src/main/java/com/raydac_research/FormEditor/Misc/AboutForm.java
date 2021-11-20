package com.raydac_research.FormEditor.Misc;

import javax.swing.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.*;

public class AboutForm extends Thread implements ActionListener
{
    private JLabel ImageLabelForm;
    private JButton ButtonOk;
    private JTextArea TextArea;

    private JDialog p_MainFrame;

    private static final String s_AboutText = "RRG Form Editor\r\nv 4.6b(22 Sep 2005)\r\n©2004-2005 Raydac Research Group Ltd.\r\nhttp://www.raydac-research.com";
    private JPanel MainPanel;

    public AboutForm(JFrame _frame)
    {
        ImageIcon p_Icon = Utilities.loadIconFromResource("felogo.gif");
        ImageLabelForm.setIcon(p_Icon);

        p_MainFrame = new JDialog(_frame,"About",true);
        TextArea.setText(s_AboutText);

        MainPanel.setBackground(Color.white);
        p_MainFrame.setContentPane(MainPanel);

        p_MainFrame.setResizable(false);
        p_MainFrame.pack();

        int i_WindowWidth = p_MainFrame.getWidth();
        int i_WindowHeight = p_MainFrame.getHeight();

        int i_ScreenWidth = Toolkit.getDefaultToolkit().getScreenSize().width;
        int i_ScreenHeight = Toolkit.getDefaultToolkit().getScreenSize().height;

        int i_ScreenX = (i_ScreenWidth - i_WindowWidth)>>1;
        int i_ScreenY = (i_ScreenHeight - i_WindowHeight)>>1;

        p_MainFrame.setDefaultCloseOperation(JDialog.HIDE_ON_CLOSE);
        p_MainFrame.setLocation(i_ScreenX,i_ScreenY);

        ButtonOk.addActionListener(this);

        run();
    }

    public void run()
    {
        p_MainFrame.setVisible(true);
        p_MainFrame.dispose();
    }

    public void actionPerformed(ActionEvent e)
    {
        p_MainFrame.dispose();
    }
}
