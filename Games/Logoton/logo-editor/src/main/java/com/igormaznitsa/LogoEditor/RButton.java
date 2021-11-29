/*
    Author : Igor A. Maznitsa
    EMail : rrg@forth.org.ru
    Date : 24.03.2002
*/
package com.igormaznitsa.LogoEditor;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.applet.Applet;

public class RButton extends Canvas implements MouseListener
{
    protected Applet apl;
    protected Image img_up = null;
    protected Image img_dwn = null;
    protected Image img_dsb = null;
    protected String hint = "";

    protected boolean state = false;
    protected boolean isradio = false;

    protected ActionListener lstnr;
    protected String action;
    protected Dimension _dim = null;

    protected boolean _is_enabled = true;

    public String getActionCommand()
    {
        return action;
    }

    public void validate()
    {
        update(getGraphics());
    }

    public void setActionListener(ActionListener lst)
    {
        lstnr = lst;
    }

    public void setEnabled(boolean state)
    {
        super.setEnabled(state);
        repaint();
    }

    public void unselect()
    {
        changeState(false);
    }

    public void select()
    {
        changeState(true);
    }

    public Dimension getMaximumSize()
    {
        return _dim;
    }

    public Dimension getMinimumSize()
    {
        return _dim;
    }

    public Dimension getPrefferedSize()
    {
        return _dim;
    }

    public void setBounds(Rectangle rct)
    {
        setBounds(rct.x,rct.y,rct.width,rct.height);
    }

    public void setBounds(int x,int y,int w,int h)
    {
        super.setBounds(x,y,_dim.width,_dim.height);
    }

    public void changeState(boolean newstate)
    {
        state = newstate;
        update(getGraphics());
    }

    public void mouseClicked(MouseEvent e)
    {
    }

    public void mousePressed(MouseEvent e)
    {
        if (isradio & state)  return;
        state = !state;
        update(getGraphics());
        if (!isradio)
        {
            try
            {
                Thread.sleep(200);
            }
            catch (InterruptedException exx)
            {
                return;
            }
            state = !state;
            update(getGraphics());
        }

        if (lstnr != null)
        {
            lstnr.actionPerformed(new ActionEvent(this, 0, action));
        }
    }

    public void mouseReleased(MouseEvent e)
    {
    }

    public void mouseEntered(MouseEvent e)
    {
        apl.showStatus(hint);
    }

    public void mouseExited(MouseEvent e)
    {
        apl.showStatus("");
    }

    public boolean getState()
    {
        return state;
    }

    public RButton(Applet apl,String hint,Image iup, Image idwn, Image idsb, boolean radio, String actionid)
    {
        this.apl = apl;
        this.hint = hint;
        int _w = 0;
        int _h = 0;

        img_up = iup;

        _h = img_up.getHeight(null);
        _w = img_up.getWidth(null);

        img_dwn = idwn;

        if (img_dwn != null)
        {
            _h = Math.max(_h, img_dwn.getHeight(null));
            _w = Math.max(_w, img_dwn.getWidth(null));
        }

        img_dsb = idsb;

        if (img_dsb != null)
        {
            _h = Math.max(_h, img_dsb.getHeight(null));
            _w = Math.max(_w, img_dsb.getWidth(null));
        }

        _dim = new Dimension(_w, _h);

        setBounds(0, 0, _w, _h);


        isradio = radio;
        action = actionid;
        addMouseListener(this);
    }

    public void paint(Graphics g)
    {
        if (!isVisible()) return;
        if (g == null) return;
        if (img_up == null) return;

        if (!isEnabled())
        {
            if (img_dsb == null)
            {
                g.drawImage(img_up, 0, 0, this);
            }
            else
                g.drawImage(img_dsb, 0, 0, this);
            return;
        }

        if (getState())
        {
            if (img_dwn != null)
            {
                g.drawImage(img_dwn, 0, 0, this);
            }
            else
                g.drawImage(img_up, 0, 0, this);
        }
        else
        {
            g.drawImage(img_up, 0, 0, this);
        }
    }

    public void update(Graphics g)
    {
        paint(g);
    }
}
