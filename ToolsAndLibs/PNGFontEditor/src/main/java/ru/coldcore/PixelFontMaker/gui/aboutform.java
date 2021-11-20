package ru.coldcore.PixelFontMaker.gui;

import ru.coldcore.PixelFontMaker.Main;
import ru.coldcore.PixelFontMaker.Utils;

import javax.swing.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class aboutform extends JDialog
{
    private JPanel contentPane;
    private JButton buttonOK;
    private JTextArea p_TextArea;
    private JLabel p_IconLabel;

    public aboutform()
    {
        setTitle("About");
        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(buttonOK);

        String s_aboutStr = Main.NAME+"\r\n"+Main.VERSION+"\r\n---------\r\nThe utility can make image based font files from TTF fonts.\r\n(C)2006 RRG Ltd.(http://www.coldcore.ru)";

        p_TextArea.setText(s_aboutStr);

        pack();

        Utils.toScreenCenter(this);

        buttonOK.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                onOK();
            }
        });
    }

    private void onOK()
    {
        dispose();
    }
}
