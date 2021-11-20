package ru.coldcore.png;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextArea;
import javax.swing.JWindow;

public class aboutform extends JDialog implements ActionListener
{
    private static final String ABOUT = ' '+main.APPNAME+'\n'+' '+main.VERSION+'\n'+" Author: Igor A. Maznitsa\n E-Mail: igor.maznitsa@raydac-research.com\n\n ©2002-2006 RRG Ltd.\n http://www.raydac-research.com \n COLD CORE® is a registered trademark \n --------------\n The utility helps you to convert either GIF,PNG,JPG or TGA image (or an\n image list) into hi-packed PNG format. So it makes PNG images compatible\n with features of color schemes of Samsung and LG mobile phones.";
    
    public aboutform(JFrame _owner)
    {
        super(_owner,"About..",true);
        Container p_container = getContentPane();
        p_container.setBackground(Color.white);
        
        p_container.setLayout(new BorderLayout());
        p_container.add(new JLabel(Utilities.loadIconFromResource("about.gif")),BorderLayout.WEST);
        JTextArea p_area = new JTextArea(ABOUT);
        p_area.setEditable(false);
        p_area.setOpaque(false);
        p_area.setFont(new Font("",Font.BOLD,10));
        p_container.add(p_area,BorderLayout.CENTER);
        
        JButton p_button = new JButton("Close");
        p_button.addActionListener(this);
        
        p_container.add(p_button,BorderLayout.SOUTH);
        
        pack();
        
        Utilities.toScreenCenter(this);
    }

    public void actionPerformed(ActionEvent e)
    {
        setVisible(false);        
    }
}
