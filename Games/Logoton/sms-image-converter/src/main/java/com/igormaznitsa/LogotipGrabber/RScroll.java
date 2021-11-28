/*
    Author : Igor A. Maznitsa
    EMail : rrg@forth.org.ru
    Date : 24.03.2002
*/
package com.igormaznitsa.LogotipGrabber;

import java.applet.Applet;
import java.awt.*;
import java.awt.event.ActionListener;

public class RScroll extends ScrollPane
{
    protected Pict _pct;

    public void setImage(Image img)
    {
        _pct.setImage(img);
        doLayout();
        _pct.repaint();
    }

    public Image getSelectImage()
    {
        return _pct.getGrabbedPart();
    }

    public void setActionListener(ActionListener lstnr)
    {
        _pct.setActionListener(lstnr);
    }

    public void setMode(IconObject mode)
    {
        _pct.setState(mode);
    }

    public RScroll(Applet apl, Image img,IconObject [] iarr)
    {
        super(ScrollPane.SCROLLBARS_AS_NEEDED);
        _pct = new Pict(apl, img,  iarr[0]);
        add(_pct, null, 0);
        doLayout();
    }
}
