package com.igormaznitsa.LogoEditor;

import java.awt.*;
import java.applet.Applet;
import java.io.IOException;

public class PhonePreview extends Canvas
{
    public static Color COLOR_FOREGROUND = Color.black;
    public static Color COLOR_BACKGROUND = new Color(103,207,103);

    protected static Image _backimage = null;
    protected Image _img;
    protected Dimension _dim;
    protected Rectangle _recta;
    protected boolean _update;

    public Dimension getMaximumSize()
    {
        return _dim;
    }

    public Dimension getMinimumSize()
    {
        return _dim;
    }

    public PhonePreview(Applet apl,Rectangle _r,boolean _u) throws IOException
    {
        super();
        _update = _u;
        _recta = _r;
        if (_backimage==null) _backimage = common.loadImageResource(apl, "screen.gif");
        _dim = new Dimension(_backimage.getWidth(null), _backimage.getHeight(null));
        super.setSize(_dim);
    }

    public void setImage(Image img)
    {
        _img = img;
        repaint();
    }

    public void update(Graphics g)
    {
        if (_update) paint(g); else drawScreen(g);
    }

    protected void drawScreen(Graphics g)
    {
        if (_img != null)
        {
            int lw = _img.getWidth(null);
            int lh = _img.getHeight(null);

            int lxoff = (_recta.width-lw)>>1;
            int lyoff = (_recta.height-lh)>>1;
            g.setClip(_recta.x,_recta.y,_recta.width,_recta.height);
            g.drawImage(_img, _recta.x+lxoff, _recta.y+lyoff, null);
            g.setClip(0,0,1000,1000);
        }
    }

    public void paint(Graphics g)
    {
        g.drawImage(_backimage, 0, 0, null);
        drawScreen(g);
    }

}
