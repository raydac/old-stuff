package com.raydac_research.RRGImagePacker.Utils;

import javax.swing.*;
import java.awt.*;

public class WaitDialog extends JDialog implements Runnable
{
    private Panel p_Panel;
    private Label p_Label;

    public WaitDialog()
    {
        super();
        setResizable(false);
        setTitle("Waiting for an operation...");
        setModal(true);
        setUndecorated(true);

        p_Panel = new Panel();
        p_Label = new Label("Please wait until the end of current operation...");

        p_Panel.setLayout(new BorderLayout(20,20));
        p_Panel.add(p_Label,BorderLayout.CENTER);

        p_Panel.add(new Label("     "),BorderLayout.WEST);
        p_Panel.add(new Label("     "),BorderLayout.SOUTH);
        p_Panel.add(new Label("     "),BorderLayout.NORTH);
        p_Panel.add(new Label("     "),BorderLayout.EAST);

        setContentPane(p_Panel);
        pack();

    }

    public void show()
    {
        Thread p_thr = new Thread(this);
        p_thr.setPriority(Thread.NORM_PRIORITY);
        p_thr.start();
    }

    public void run()
    {
        pack();
        Utilities.toScreenCenter(this);
        super.show();
    }
}
