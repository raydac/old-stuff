// Author: Igor A. Maznitsa (rrg@forth.org.ru)
// (C) 2001 All Copyright by Igor A. Maznitsa
package ru.da.rrg.RNokiaIconMaker.graphics;

import java.awt.*;
import java.awt.event.*; 

public class RButton extends Canvas implements RComponent,MouseListener 
{
	protected Image img_up = null;
	protected Image img_dwn = null;
	protected Image img_dsb = null;
	protected int action = -1;
	
	protected boolean state = false;
	protected boolean isradio = false;
	
	public int getID(){ return action;} 
	
	public void unselect()
	{
		changeState(false);
	}

	public void select()
	{
		changeState(true);
	}
	
	public void changeState(boolean newstate)
	{
		state = newstate;
		update(getGraphics());
	}
	
	public void mouseClicked(MouseEvent e){}
	public void mousePressed(MouseEvent e)
	{
		state = !state;
		update(getGraphics());
		if (!isradio)
		{
			try
			{
				Thread.sleep(200); 
			}
			catch(InterruptedException exx){return;}
			state = !state;
			update(getGraphics());
		}
		
		Container cnt = getParent();
		if (cnt!=null)
		{
			if (cnt instanceof RActionListener)
			{
				RActionEvent ae = new RActionEvent(this,action);  
				((RActionListener)cnt).actionPerformed(ae);
			}
		}
	}
	public void mouseReleased(MouseEvent e){}
	public void mouseEntered(MouseEvent e){}
	public void mouseExited(MouseEvent e){}
	
	public boolean getState()
	{
		return state;	
	}
	
	public RButton(Image iup,Image idwn,Image idsb,boolean radio,int id)
	{
		img_up = iup;
		img_dwn = idwn;
		img_dsb = idsb;
		isradio = radio;
		action = id;
		addMouseListener(this); 
	}
	
	public void paint(Graphics g)
	{	
		if (!isVisible())return;
		if (g==null) return;
		if (img_up==null) return; 
		
		if (!isEnabled())
		{
			if (img_dsb==null)
			{
				g.drawImage(img_up,0,0,this);
			}
			else
				g.drawImage(img_dsb,0,0,this);
			return;
		}

		if (getState())
		{
			if (img_dwn!=null)
			{
				g.drawImage(img_dwn,0,0,this);  
			}
			else
				g.drawImage(img_up,0,0,this);  
		}
		else
		{
			g.drawImage(img_up,0,0,this);  
		}
	}
	
	public void update(Graphics g)
	{
		paint(g);	
	}
}
