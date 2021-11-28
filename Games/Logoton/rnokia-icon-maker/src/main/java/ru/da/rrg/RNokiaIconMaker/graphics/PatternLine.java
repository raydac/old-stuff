// Author: Igor A. Maznitsa (rrg@forth.org.ru)
// (C) 2001 All Copyright by Igor A. Maznitsa
package ru.da.rrg.RNokiaIconMaker.graphics;

import java.awt.*;  
import java.applet.*; 
import ru.da.rrg.RNokiaIconMaker.*;   
import java.util.*;
import java.io.*;
import java.awt.event.*; 

public class PatternLine extends Canvas implements RComponent,MouseListener 
{
	protected int component_id;
	protected Image [] patt_list = null;
	protected int icon_height = 14;
	protected int icon_width = 14;
	protected Image hidden_image = null;
	protected int firstviewindx=0;
	protected int selectindx=-1;
	protected int hoffst=0;
	protected Applet papplet = null;
	
	public void mouseClicked(MouseEvent e){}
	public void mousePressed(MouseEvent e)
	{
 		pressPattern(e.getX()); 
		Container cnt = getParent();
		if (cnt!=null)
		{
			if (cnt instanceof RActionListener)
			{
				RActionEvent ae = new RActionEvent(this,component_id); 
				((RActionListener)cnt).actionPerformed(ae);
			}
		}
	}

	public void mouseReleased(MouseEvent e){}
	public void mouseEntered(MouseEvent e){}
	public void mouseExited(MouseEvent e){}
	
	public void unselect()
	{
		setSelect(-1);	
	}
	
	public void setSelect(int index)
	{
		selectindx = index;
		update(getGraphics());
	}
	
	public Image getCurrentPattern()
	{
		if (selectindx<0) return null;
		return patt_list[selectindx];
	}

	public void stepLeft()
	{
		if (isLeftOffscreenImages())
		{
			firstviewindx--;
			update(getGraphics());
		}
	}

	public void stepRight()
	{
		if (isRightOffscreenImages())
		{
			firstviewindx++;
			update(getGraphics());
		}
	}
	
	public boolean isRightOffscreenImages()
	{
		int lk = getBounds().width/icon_width;   
		if ((patt_list.length-firstviewindx)>lk) return true; else return false;
	}

	public boolean isLeftOffscreenImages()
	{
		if (firstviewindx==0) return false; else return true;
	}
	
	public void paint(Graphics g)
	{
		if ((g==null)||(hidden_image==null)) return;
		g.drawImage(hidden_image,0,0,null);
	}

	public void hupdate()
	{
		if (hidden_image==null) return;
		Graphics hidden_graphics = hidden_image.getGraphics();
		Rectangle comp_rect = getBounds();
		hidden_graphics.setColor(Color.white);
		
		hidden_graphics.fillRect(0,0,comp_rect.width,comp_rect.height);

		hidden_graphics.setColor(Color.orange.brighter());  
		
		int lxx = 0;
		for(int li=firstviewindx;li<patt_list.length;li++)
		{
			Image jjj = patt_list[li];
			if (li==selectindx)
			{
				hidden_graphics.fillRect(lxx,2,14,16);
			}
			hidden_graphics.drawImage(jjj,lxx,hoffst,null); 
			lxx+=icon_width; 
			if (lxx>=comp_rect.width) break;
		}
	}
	
	public void update(Graphics g)
	{
		hupdate();
		paint(g);
	}
	
	public void pressPattern(int x)
	{
		x = x/icon_width;
		if ((x+firstviewindx)>=patt_list.length) 
			selectindx=-1;
		else
			selectindx=x+firstviewindx;
		update(getGraphics());
	}
	
	public int getID()
	{
		return component_id;
	}
	
	public void setBounds(Rectangle rct)
	{
		hidden_image = papplet.createImage(rct.width,rct.height);
		hoffst = (rct.height-icon_height)>>1;
		super.setBounds(rct); 
		update(getGraphics()); 
	}
	
	public void setBounds(int x,int y,int w,int h)
	{
		hidden_image = papplet.createImage(w,h);
		hoffst = (h-icon_height)>>1;
		super.setBounds(x,y,w,h); 
		update(getGraphics()); 
	}
	
	public PatternLine(Applet apl,int id)
	{
		super();
		component_id = id;
		papplet = apl;
		hoffst = 0;
		patt_list = new Image[18];
		for(int li=1;li<=patt_list.length;li++)
		{
			loadPattern(Integer.toString(li),li-1);
		}
		hupdate();
		addMouseListener(this); 
	}

	public boolean loadPattern(String name,int index)
	{
		Image imm = null;
		try
		{
			imm = Utilities.loadImageResource(papplet,"patterns/"+name);
		}
		catch(IOException exx)
		{
			return false;	
		}
		patt_list[index] = imm;
		return true;
	}
	
}
