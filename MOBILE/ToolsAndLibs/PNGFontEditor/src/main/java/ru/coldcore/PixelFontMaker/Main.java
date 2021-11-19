package ru.coldcore.PixelFontMaker;

import ru.coldcore.PixelFontMaker.gui.MainGUIForm;

import javax.swing.*;
import java.awt.*;

public class Main extends JDialog implements Runnable
{
    public Main()
    {
        super();

        setUndecorated(true);
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        setResizable(false);


        JPanel p_panel = new JPanel(new BorderLayout(0,0));
        JLabel p_label = new JLabel(Utils.loadIconFromResource("splash.gif"));
        p_panel.add(p_label,BorderLayout.CENTER);
        setContentPane(p_panel);
        pack();
        Utils.toScreenCenter(this);
        setAlwaysOnTop(true);
        setVisible(true);
        new Thread(this).start();
    }

    public void run()
    {
        int i_counter = 100;
        while(i_counter>0)
        {
            try
            {
            Thread.sleep(30);
                toFront();
            }
             catch(Throwable _t) {
                 break;
             }
                Thread.yield();
            i_counter--;
        }

        setVisible(false);
        dispose();
    }

    public static final String NAME = "Raydac Pixel Font Maker ";
    public static final String VERSION = "v 1.00";

    public static final void main(String [] _args)
    {
        new Main();
        MainGUIForm p_forrr = new MainGUIForm();
        p_forrr.setVisible(true);
    }
}
