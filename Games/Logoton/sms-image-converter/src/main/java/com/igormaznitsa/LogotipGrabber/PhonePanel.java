/*
    Author : Igor A. Maznitsa
    EMail : rrg@forth.org.ru
    Date : 24.03.2002
*/
package com.igormaznitsa.LogotipGrabber;

import java.applet.Applet;
import java.awt.*;
import java.awt.event.ActionListener;
import java.io.IOException;

public class PhonePanel extends Panel
{
    protected PhonePreview _phonescreen;
    protected RScrollbar _optionbar;

    public void setPhoneImage(Image img)
    {
        _phonescreen.setImage(img);
    }

    public void fullRepaintPhoneScreen()
    {
        _phonescreen.paint(_phonescreen.getGraphics());
    }

    public void repaintPhoneScreen()
    {
        _phonescreen.repaint();
    }

    public PhonePanel(Applet apl, ActionListener adj) throws IOException
    {
        _phonescreen = new PhonePreview(apl,new Rectangle(27,56,101,64),false);
        _phonescreen.setBackground(getBackground());
        _optionbar = new RScrollbar(apl, -999, 999, 0, "CONTRAST");
        _optionbar.setBackground(getBackground());
        _optionbar.setActionListener(adj);

        BorderLayout bl = new BorderLayout();

        setLayout(bl);

        add("Center", _phonescreen);
        add("South", _optionbar);
    }
}
