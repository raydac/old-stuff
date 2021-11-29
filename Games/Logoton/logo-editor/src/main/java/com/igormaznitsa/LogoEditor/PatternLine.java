// Author: Igor A. Maznitsa (rrg@forth.org.ru)
// (C) 2001 All Copyright by Igor A. Maznitsa
package com.igormaznitsa.LogoEditor;

import java.awt.*;
import java.awt.image.PixelGrabber;
import java.applet.*;
import java.io.*;
import java.awt.event.*; 

public class PatternLine extends Panel implements MouseListener,ActionListener
{
	public static final int PATTERN_NUMBER = 18;
    public static final int BUTTON_WIDTH = 16;
    public static final int BUTTON_HEIGHT = 20;
    public static final int ICON_WIDTH = 14;
    public static final int ICON_HEIGHT = 14;

    protected static final String ACTION_LEFT = "LEFT";
    protected static final String ACTION_RIGHT = "RIGHT";

    public static final String ACTION_SELECTED = "PATTERNSEL";

    protected Image [] _pattern_array;
    protected int [][] _pattern_int_array;
    protected RButton _left_button;
    protected RButton _right_button;
    protected Image _hidden_scroll_image;
    protected Applet _parent_applet;

    protected ActionListener _select_action_listener;
    protected int _selected_icon_index;
    protected int _first_icon_index;

    public void validate()
    {
        repaint();
    }

    public void mouseClicked(MouseEvent e)
    {
    }

	public void mousePressed(MouseEvent e)
    {
        int lx = e.getX();
        if ((lx<BUTTON_WIDTH)||(lx>(getSize().width-BUTTON_WIDTH))) return;
        lx -= BUTTON_WIDTH;
        int lndx = _first_icon_index + lx/ICON_WIDTH;
        if (lndx>_pattern_array.length) return;
        _selected_icon_index = lndx;
        updateHiddenImage();
        repaint();

        if (_select_action_listener != null)
        {
            _select_action_listener.actionPerformed(new ActionEvent(this,0,ACTION_SELECTED));
        }
    }
	public void mouseReleased(MouseEvent e){}
	public void mouseEntered(MouseEvent e){}
	public void mouseExited(MouseEvent e){}

    public void setActionListener(ActionListener lstnr)
    {
        _select_action_listener = lstnr;
    }

    public void setSelectIndex(int val)
    {
        _selected_icon_index = val;
        updateHiddenImage();
        repaint();
    }

    public int [] getSelectedImage()
    {
        if ((_selected_icon_index<0)||(_selected_icon_index>=_pattern_array.length)) return null;
        return _pattern_int_array[_selected_icon_index];
    }

    public synchronized void actionPerformed(ActionEvent e)
    {
        String act = e.getActionCommand();
        if (act.equals(ACTION_LEFT))
        {
            if (_left_button.isEnabled()) _first_icon_index--;
        }
        else
        if (act.equals(ACTION_RIGHT))
        {
            if (_right_button.isEnabled()) _first_icon_index++;
        }

        updateHiddenImage();
        repaint();
    }

    public Dimension getMaximumSize()
    {
        return new Dimension(2000,BUTTON_HEIGHT);
    }

    public Dimension getMinimumSize()
    {
        return new Dimension(BUTTON_WIDTH*2+ICON_WIDTH,BUTTON_HEIGHT);
    }

    public void setSize(Dimension dim)
    {
        setSize(dim.width,dim.height);
    }

    public void setSize(int w,int h)
    {
        super.setSize(w,h);
        _left_button.setBounds(0,0,10,10);
        _right_button.setBounds(w-BUTTON_WIDTH,0,10,10);

        int liw = w-(BUTTON_WIDTH<<1);
        _hidden_scroll_image = common.createImage(_parent_applet,liw,BUTTON_HEIGHT);
        updateHiddenImage();
    }

    protected synchronized void updateHiddenImage()
    {
        Graphics g = _hidden_scroll_image.getGraphics();
        g.setColor(getBackground());
        int lw = _hidden_scroll_image.getWidth(null);
        int lh = _hidden_scroll_image.getHeight(null);
        g.fillRect(0,0,lw-1,lh-1);
        g.setColor(Color.black);
        g.drawRect(0,0,lw-1,lh-1);

        int loff = (BUTTON_HEIGHT - ICON_HEIGHT)>>1;

        for(int li=_first_icon_index;li<_pattern_array.length;li++)
        {
            int lx = (li-_first_icon_index) * ICON_WIDTH;
            if (lx>=lw) break;

            if (li==_selected_icon_index)
            {
                g.setColor(getForeground());
                g.fillRect(lx,1,ICON_WIDTH,BUTTON_HEIGHT-2);
            }
            g.drawImage(_pattern_array[li],lx,loff,null);
        }

        if (_first_icon_index==0) _left_button.setEnabled(false); else _left_button.setEnabled(true);
        if ((_pattern_array.length-_first_icon_index)*ICON_WIDTH<lw) _right_button.setEnabled(false); else _right_button.setEnabled(true);
    }

    protected int [] decodePattern(Image pattern)
    {
        if (pattern==null) return null;
        int [] nimg = new int [ICON_WIDTH*ICON_HEIGHT];
        PixelGrabber pxg = new PixelGrabber(pattern,0,0,ICON_WIDTH,ICON_HEIGHT,nimg,0,ICON_WIDTH);
        try
        {
            pxg.grabPixels();
        }
        catch (InterruptedException e)
        {
            return null;
        }
        for(int li=0;li<nimg.length;li++) if ((nimg[li]&0xFF000000)!=0) nimg[li]=0xFFFFFF; else nimg[li] = 0x00;
        return nimg;
    }

    public PatternLine(Applet apl) throws IOException
    {
        super();
        _first_icon_index = 0;
        _selected_icon_index = -1;
        _parent_applet = apl;
        _pattern_array = new Image[PATTERN_NUMBER];
        _pattern_int_array = new int [PATTERN_NUMBER][];
        for(int li = 1 ;li <= PATTERN_NUMBER;li++)
        {
            _pattern_array [li-1] = common.loadImageResource(apl,"patterns/"+li+".gif");
            _pattern_int_array [li-1] = decodePattern(_pattern_array[li-1]);
        }

        _left_button = new RButton(apl,"Left",common.loadImageResource(apl,"scr_left.gif"),common.loadImageResource(apl,"scr_left_on.gif"),common.loadImageResource(apl,"scr_left_off.gif"),false,ACTION_LEFT);
        _right_button = new RButton(apl,"Right",common.loadImageResource(apl,"scr_right.gif"),common.loadImageResource(apl,"scr_right_on.gif"),common.loadImageResource(apl,"scr_right_off.gif"),false,ACTION_RIGHT);

        _left_button.setActionListener(this);
        _right_button.setActionListener(this);

        setLayout(null);

        add(_left_button);
        add(_right_button);

        setSize(ICON_WIDTH+BUTTON_WIDTH*2,BUTTON_HEIGHT);

        addMouseListener(this);
    }

    public void update(Graphics g)
    {
        paint(g);
    }

    public void paint(Graphics g)
    {
        _left_button.repaint();
        _right_button.repaint();
        g.drawImage(_hidden_scroll_image,BUTTON_WIDTH,0,null);
    }
}
