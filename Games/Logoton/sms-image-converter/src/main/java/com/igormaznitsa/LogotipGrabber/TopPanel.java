/*
    Author : Igor A. Maznitsa
    EMail : rrg@forth.org.ru
    Date : 24.03.2002
*/
package com.igormaznitsa.LogotipGrabber;

import java.applet.Applet;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.ItemListener;
import java.io.IOException;

public class TopPanel extends Panel
{
    protected RButton _save;
    protected RButton _increase;
    protected RButton _decrease;

    protected Choice _modelist;

    public void updateButton(RButton btn)
    {
        Component[] cmp = getComponents();

        for (int li = 0; li < cmp.length; li++)
        {
            if (cmp[li] instanceof RButton)
            {
                RButton rrr = (RButton) cmp[li];
                if (rrr.equals(btn)) rrr.select(); else rrr.unselect();
            }
        }
    }

    public void setZoomIncEnable(boolean flag)
    {
        if (_increase.isEnabled()==flag) return;
        _increase.setEnabled(flag);
        _increase.repaint();
    }

    public void setZoomDecEnable(boolean flag)
    {
        if (_decrease.isEnabled()==flag) return;
        _decrease.setEnabled(flag);
        _decrease.repaint();
    }

    public TopPanel(Applet apl, ActionListener lstnr,ItemListener ilstnr,IconObject [] _modes) throws IOException
    {
        setBackground(ConverterPanel._backgroundColor);
        setForeground(ConverterPanel._backgroundColor);

        _modelist = new Choice();
        _modelist.addItemListener(ilstnr);
        _modelist.setForeground(Color.black);
        _modelist.setBackground(Color.white);

        for(int li=0;li<_modes.length;li++ )
        {
            _modelist.add(_modes[li].getName());
        }

        _save    = new RButton(apl,"Save",common.loadImageResource(apl, "saveup.gif"),common.loadImageResource(apl, "savedwn.gif"), null, false, "savepart");
        _increase= new RButton(apl,"Increase",common.loadImageResource(apl, "incup.gif"), common.loadImageResource(apl, "incdwn.gif"), common.loadImageResource(apl, "incdsbl.gif"), false, "increase");
        _decrease= new RButton(apl,"Decrease",common.loadImageResource(apl, "decup.gif"), common.loadImageResource(apl, "decdwn.gif"),common.loadImageResource(apl, "decdsbl.gif"), false, "decrease");

        _save.setActionListener(lstnr);
        _increase.setActionListener(lstnr);
        _decrease.setActionListener(lstnr);

        setLayout(new FlowLayout(FlowLayout.LEFT));

        add(_save);
        add(_modelist);
        add(_increase);
        add(_decrease);
    }
}
