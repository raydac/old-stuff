// Author: Igor A. Maznitsa (rrg@forth.org.ru)
// (C) 2001 All Copyright by Igor A. Maznitsa
package ru.da.rrg.musicengine;

import java.awt.*;
import java.applet.*; 

public class PhoneScreen extends Canvas
{
	Image img;
	Color clr;
	Font fntm;
	FontMetrics fmtr;
	Applet aplt;
	String str="";
	Rectangle scrcoord;
	Point strcoord;
	
	public PhoneScreen(Applet apl,Image bckgnd,Color fntcolor,Font fnt,Rectangle screen,Point str)
	{
		strcoord = str;
		scrcoord = screen;
		img = bckgnd;
		clr = fntcolor;
		aplt = apl;
		fntm = fnt;
		fmtr = Toolkit.getDefaultToolkit().getFontMetrics(fntm);  
	}
	
	public void setText(String st)
	{
		str = st;	
		repaint();
	}
	
	public void update(Graphics g)
	{
		paint(g);	
	}
	
	public void paint(Graphics g)
	{
		if (g==null) return;
		if (img!=null) 
			g.drawImage(img,0,0,this);
		else
		{
			g.setColor(Color.white);
			g.fillRect(0,0,aplt.getSize().width,aplt.getSize().height);    
		}
		g.setClip(scrcoord.x,scrcoord.y,scrcoord.width,scrcoord.height);       
		g.setColor(clr);
		g.setFont(fntm); 
		
		int lxx = (scrcoord.width - fmtr.stringWidth(str))>>1;  
		
		g.drawString(str,lxx,strcoord.y);
	}
	
}
