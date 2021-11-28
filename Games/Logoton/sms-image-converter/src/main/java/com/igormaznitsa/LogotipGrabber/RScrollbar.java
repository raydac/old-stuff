/*
    Author : Igor A. Maznitsa
    EMail : rrg@forth.org.ru
    Date : 24.03.2002
*/
package com.igormaznitsa.LogotipGrabber;

import java.applet.Applet;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;

public class RScrollbar extends Canvas implements MouseMotionListener, MouseListener
{
    protected int _widthscrollbar;
    protected int _heightscrollbar;
    protected int _widthcursor;
    protected int _heightcursor;
    protected int _count_start;
    protected ActionListener _adj;

    protected Rectangle _scrollbarrect;
    protected Dimension _dm;

    protected Image _scrollbarimage;
    protected Image _cursorimage;

    protected int startvalue;
    protected int endvalue;
    protected int currentvalue;

    protected float _stepoffset;
    protected String _command;


    public void validate()
    {
        update(getGraphics());
    }

    public void mouseClicked(MouseEvent evt)
    {
    }

    public void mouseEntered(MouseEvent evt)
    {
    }

    public void mouseExited(MouseEvent evt)
    {
    }

    public void mousePressed(MouseEvent evt)
    {
        if (checkCoord(evt.getX(), evt.getY()))
        {
            int lx = evt.getX() - _scrollbarrect.x;

            if (lx < _count_start)
                setValue(startvalue);
            else if (lx > (_widthscrollbar - _count_start))
                setValue(endvalue);
            else
            {
                setValue((int) ((float) lx / _stepoffset) + startvalue);
            }
        }

        if (_adj != null)
        {
            _adj.actionPerformed(new ActionEvent(this, currentvalue, _command));
        }
    }

    public void mouseReleased(MouseEvent evt)
    {
    }

    public void mouseDragged(MouseEvent evt)
    {
        mousePressed(evt);
    }

    public void mouseMoved(MouseEvent evt)
    {
    }

    protected boolean checkCoord(int x, int y)
    {
        return _scrollbarrect.contains(x, y);
    }

    public Dimension getMaximumSize()
    {
        return null;
    }

    public Dimension getMinimumSize()
    {
        return _dm;
    }

    public Dimension getPrefferedSize()
    {
        return _dm;
    }

    public void setBounds(int x, int y, int w, int h)
    {
        int lx = (w - _widthscrollbar) >> 1;
        int ly = (h - _heightscrollbar) >> 1;

        _scrollbarrect = new Rectangle(lx, ly, _widthscrollbar, _heightscrollbar);

        super.setBounds(x, y, w, h);
    }

    public void setBounds(Rectangle rect)
    {
        setBounds(rect.x, rect.y, rect.width, rect.height);
    }

    public void setSize(int w, int h)
    {
        super.setSize(w, h);
    }

    public void setSize(Dimension dm)
    {
        setSize(dm.width, dm.height);
    }

    public void update(Graphics g)
    {
        paint(g);
    }

    public void paint(Graphics g)
    {
        draw(g);
    }

    protected void draw(Graphics g)
    {
        int scrx = _scrollbarrect.x;
        int scry = _scrollbarrect.y;

        g.drawImage(_scrollbarimage, scrx, scry, null);
        int lll = currentvalue - startvalue;
        int polz = (int) ((float) lll * _stepoffset);
        g.drawImage(_cursorimage, polz + _count_start + scrx, scry, null);
    }

    public void setValue(int val)
    {
        if (val > endvalue)
            val = endvalue;
        else if (val < startvalue) val = startvalue;
        currentvalue = val;
        repaint();
    }

    public void setActionListener(ActionListener lstnr)
    {
        _adj = lstnr;
    }

    public RScrollbar(Applet apl, int startvalue, int endvalue, int currentvalue, String command) throws IOException
    {
        super();

        _adj = null;
        _command = command;

        this.startvalue = startvalue;
        this.endvalue = endvalue;
        this.currentvalue = currentvalue;

        _scrollbarimage = common.loadImageResource(apl, "scrollbar.gif");
        _cursorimage = common.loadImageResource(apl, "cursor.gif");

        _widthscrollbar = _scrollbarimage.getWidth(null);
        _heightscrollbar = _scrollbarimage.getHeight(null);

        _dm = new Dimension(_widthscrollbar, _heightscrollbar);

        _widthcursor = _cursorimage.getWidth(null);
        _heightcursor = _cursorimage.getHeight(null);

        _count_start = _widthcursor >> 1;

        _stepoffset = (float) (_widthscrollbar - (_count_start << 1)) / (float) (endvalue - startvalue);

        setValue(currentvalue);

        _scrollbarrect = new Rectangle(0, 0, _widthscrollbar, _heightscrollbar);

        setBounds(_scrollbarrect);

        addMouseListener(this);
        addMouseMotionListener(this);
    }
}
