/*
    Author : Igor A. Maznitsa
    EMail : rrg@forth.org.ru
    Date : 24.03.2002
*/
package com.igormaznitsa.LogotipGrabber;

import java.applet.Applet;
import java.awt.*;
import java.awt.event.*;

public class Pict extends Canvas implements MouseListener, MouseMotionListener
{
    protected Image _selected_part;
    protected int _part_x = -1;
    protected int _part_y = -1;
    protected int _part_width = 0;
    protected int _part_height = 0;

    protected Image _img;
    protected Dimension dm;

    protected Image buffer_image;
    protected int _buffer_x;
    protected int _buffer_y;

    protected Applet _apl;

    protected IconObject _state;
    protected int _rectwidth = 0;
    protected int _rectheight = 0;

    protected ActionListener _lstnr;

    public void validate()
    {
        update(getGraphics());
    }

    public void setActionListener(ActionListener ls)
    {
        _lstnr = ls;
    }

    public Image getGrabbedPart()
    {
        return _selected_part;
    }

    public void setImage(Image image)
    {
        _selected_part = null;
        _part_x = -1;
        _part_y = -1;
        _part_width = 0;
        _part_height = 0;
        _img = image;
         dm = new Dimension(_img.getWidth(null), _img.getHeight(null));
        setSize(dm);
    }

    public Pict(Applet apl, Image img, IconObject initialstate)
    {
        super();

        _apl = apl;
        _img = img;
        _buffer_x = -1;
        _buffer_y = -1;
        buffer_image = null;
        dm = new Dimension(_img.getWidth(null), _img.getHeight(null));

        setCursor(new Cursor(Cursor.CROSSHAIR_CURSOR));
        setState(initialstate);

        addMouseListener(this);
        addMouseMotionListener(this);
    }

    protected synchronized void getBackground(int x, int y)
    {
        _buffer_x = x;
        _buffer_y = y;

        Graphics gr = buffer_image.getGraphics();
        gr.setColor(getBackground());
        gr.fillRect(0, 0, buffer_image.getWidth(null), buffer_image.getHeight(null));
        gr.drawImage(_img, -x, -y, null);
    }

    protected synchronized void repairBackground()
    {
        Graphics gr = getGraphics();
        if (_buffer_x >= 0)
        {
            gr.drawImage(buffer_image, _buffer_x, _buffer_y, null);
        }
        _buffer_x = -1;
        _buffer_y = -1;
    }

    public void setState(IconObject newstate)
    {
        repairBackground();

        _state = newstate;

        _rectwidth = _state.getWidth();
        _rectheight = _state.getHeight();
        buffer_image = _apl.createImage(_rectwidth, _rectheight);
    }

    public Dimension getMaximumSize()
    {
        return dm;
    }

    public Dimension getMinimumSize()
    {
        return dm;
    }

    public void paint(Graphics g)
    {
        g.drawImage(_img, 0, 0, null);
        drawSelectArea(g);

        if (this.mouseEvent != null) {
            g.setColor(Color.blue);
            int lwdth = _rectwidth;
            int lhght = _rectheight;
            int lx = mouseEvent.getX();
            int ly = mouseEvent.getY();

            if ((lx + lwdth) > dm.width) lwdth = dm.width - lx;
            if ((ly + lhght) > dm.height) lhght = dm.height - ly;

            if ((lwdth > 0) && (lhght > 0)) g.drawRect(mouseEvent.getX(), mouseEvent.getY(), lwdth - 1, lhght - 1);
        }
    }

    public void mouseClicked(MouseEvent evt)
    {
        this.mouseEvent = evt;
        repaint();
    }

    public void mouseEntered(MouseEvent evt)
    {
        this.mouseEvent = evt;
        repaint();
    }

    public void mouseExited(MouseEvent evt)
    {
        this.mouseEvent = evt;
        repaint();
    }

    public void mousePressed(MouseEvent evt)
    {
        int lwdth = dm.width - evt.getX();
        int lhght = dm.height - evt.getY();
        if ((lwdth > 0) && (lhght > 0)) {
            repaintGrabArea();
            grabPart(evt.getX(), evt.getY());

            if (_lstnr != null) _lstnr.actionPerformed(new ActionEvent(this, 0, "imageselect"));
        }
        this.mouseEvent = evt;
        repaint();
    }

    public void mouseReleased(MouseEvent evt)
    {

    }

    public void mouseDragged(MouseEvent evt)
    {
        mouseMoved(evt);
    }

    protected synchronized void grabPart(int x, int y)
    {
        synchronized (buffer_image)
        {
            _selected_part = _apl.createImage(buffer_image.getWidth(null), buffer_image.getHeight(null));
            _part_x = x;
            _part_y = y;
            _part_width = buffer_image.getWidth(null);
            _part_height = buffer_image.getHeight(null);

            if ((x + _part_width) >= dm.width) _part_width = dm.width - x;
            if ((y + _part_height) >= dm.height) _part_height = dm.height - y;

            _selected_part.getGraphics().drawImage(buffer_image, 0, 0, null);
        }
    }

    protected void drawSelectArea(Graphics g)
    {
        if (_part_x >= 0)
        {
            g.setColor(Color.green);
            g.drawRect(_part_x, _part_y, _part_width - 1, _part_height - 1);
        }
    }

    protected void repaintGrabArea()
    {
        if (_part_x >= 0)
        {
            getGraphics().drawImage(_selected_part, _part_x, _part_y, null);
        }

        _part_x = -1;
        _part_y = -1;
    }

    private MouseEvent mouseEvent = null;

    public void mouseMoved(MouseEvent evt)
    {
        repairBackground();
        getBackground(evt.getX(), evt.getY());
        mouseEvent = evt;
        repaint();
    }


}
