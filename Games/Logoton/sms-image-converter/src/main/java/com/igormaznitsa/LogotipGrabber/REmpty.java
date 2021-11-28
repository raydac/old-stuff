/*
    Author : Igor A. Maznitsa
    EMail : rrg@forth.org.ru
    Date : 24.03.2002
*/
package com.igormaznitsa.LogotipGrabber;

import java.awt.*;

public class REmpty extends Canvas
{
    public void validate()
    {
        update(getGraphics());
    }

    public void update(Graphics g)
    {
    }

    public void paint(Graphics g)
    {
    }

    public void repaint()
    {
    }

    protected Dimension dm;

    public Dimension getMaxmumSize()
    {
        return dm;
    }

    public Dimension getMinimumSize()
    {
        return dm;
    }

    public Dimension getPrefferedSize()
    {
        return dm;
    }

    public void setBounds(Rectangle rect)
    {
        setBounds(rect.x, rect.y, dm.width, dm.height);
    }

    public void setBounds(int x, int y, int w, int h)
    {
        super.setBounds(1, 1, dm.width, dm.height);
    }

    public void setSize(Dimension dm)
    {
    }

    public void setSize(int w, int h)
    {
    }

    public REmpty(int w, int h)
    {
        super();
        dm = new Dimension(w, h);
        super.setSize(w, h);
    }
}
