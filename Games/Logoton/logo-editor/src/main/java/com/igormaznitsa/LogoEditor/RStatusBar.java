/*
    Author : Igor A. Maznitsa
    EMail : rrg@forth.org.ru
    Date : 24.03.2002
*/
package com.igormaznitsa.LogoEditor;

import java.awt.*;

public class RStatusBar extends Canvas
{
    protected Color _foreground;
    protected Color _background;
    protected Color _bordercolor;
    protected int _min_value;
    protected int _max_value;
    protected int _cur_value;
    protected int _height;
    protected float _offsetvalue;

    public RStatusBar(Color foreground,Color background,Color border,int minvalue,int maxvalue,int startvalue,int hght)
    {
        _foreground = foreground;
        _background = background;
        _height = hght;
        _bordercolor = border;
        _min_value = minvalue;
        _max_value = maxvalue;
        _cur_value = startvalue;

        _offsetvalue = 0;
    }

    public void validate()
    {
        update(getGraphics());
    }

    public void setForegroundColor(Color clr)
    {
        _foreground = clr;
    }

    public void setBackgroundColor(Color clr)
    {
        _background = clr;
    }

    public Dimension getMinimumSize()
    {
        return new Dimension(10,_height);
    }

    public Dimension getPreferredSize()
    {
        return new Dimension(100,_height);
    }

    public Dimension getMaximumSize()
    {
        return new Dimension(1000000,_height);
    }

    public void setBorderColor(Color color)
    {
        _bordercolor = color;
    }

    public void setSize(Dimension dim)
    {
        setSize(dim.width,dim.height);
    }

    public void setSize(int width,int height)
    {
        recalculateOffset(width);
        super.setSize(width,_height);
    }

    public void setBounds(int x,int y,int w,int h)
    {
        recalculateOffset(w);
        super.setBounds(x,y,w,_height);
    }

    public void setBounds(Rectangle rect)
    {
        setBounds(rect.x,rect.y,rect.width,rect.height);
    }

    protected void recalculateOffset(int newwidth)
    {
        _offsetvalue =  (float)(newwidth-2) / (float)(_max_value-_min_value);
    }

    public void update(Graphics g)
    {
        paint(g);
    }

    public void setValue(int value)
    {
        if (value<_min_value) value = _min_value;
        if (value>_max_value) value = _max_value;

        _cur_value = value;
        repaint();
    }

    public void paint(Graphics g)
    {
        g.setColor(_bordercolor);
        g.draw3DRect(0,0,getSize().width-1,_height-1,false);
        g.setColor(_background);
        g.fillRect(1,1,getSize().width-2,_height-2);
        g.setColor(_foreground);
        int lll = (int)((float)(_cur_value -_min_value)*_offsetvalue);
        g.fill3DRect(1,1,lll,_height-2,true);

        g.setColor(Color.yellow);
    }

}
