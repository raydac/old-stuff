package com.raydac_research.FormEditor.Misc;

import javax.swing.*;
import java.awt.*;

public class SplashForm extends Thread
{
    private static final int SPLASH_DELAY_SEC = 3;

    private JLabel SplashImage;

    private JDialog p_DialogFrame;
    private JPanel SplashPanel;

    public SplashForm(JFrame _frame)
    {
        p_DialogFrame = new JDialog(_frame,true);
        p_DialogFrame.setUndecorated(true);
        p_DialogFrame.setDefaultCloseOperation(JDialog.HIDE_ON_CLOSE);
        p_DialogFrame.setResizable(false);

        int i_ScreenWidth = _frame.getWidth();
        int i_ScreenHeight = _frame.getHeight();

        ImageIcon p_ImageIcon = Utilities.loadIconFromResource("splash.gif");

        SplashImage.setIcon(p_ImageIcon);

        p_DialogFrame.setContentPane(SplashPanel);
        p_DialogFrame.pack();

        int i_WindowWidth = p_DialogFrame.getWidth();
        int i_WindowHeight = p_DialogFrame.getHeight();

        int i_ScreenX = _frame.getX() + ((i_ScreenWidth - i_WindowWidth)>>1);
        int i_ScreenY = _frame.getY() + ((i_ScreenHeight - i_WindowHeight)>>1);

        p_DialogFrame.setLocation(i_ScreenX,i_ScreenY);

        start();
        p_DialogFrame.show();
   }

    protected void hideSplashWindow()
    {
        if (p_DialogFrame!=null)
        {
            p_DialogFrame.dispose();
            p_DialogFrame = null;
        }
    }

    public void run()
    {
        if (p_DialogFrame == null) return;

        long l_initTime  = System.currentTimeMillis();
        long l_delayMilliseconds = SPLASH_DELAY_SEC * 1000;

        do
        {
            try
            {
                sleep(300);
                p_DialogFrame.toFront();
            }
            catch (InterruptedException e)
            {
                return;
            }
        }
        while((System.currentTimeMillis()-l_initTime)<l_delayMilliseconds);
        hideSplashWindow();
   }

}
