// Author: Igor A. Maznitsa (rrg@forth.org.ru)
// (C) 2001 All Copyright by Igor A. Maznitsa
package ru.da.rrg.RNokiaIconMaker.graphics;

import java.awt.*; 
import java.applet.*; 
import java.io.*;

public class RPanel extends Container
{
	protected Applet prnt = null;
	protected Image bckg = null;
	
	public Applet getParentApplet(){return prnt; }

	public RPanel(Applet appl,String imgname) throws IOException
	{
		super();	
		bckg = Utilities.loadImageResource(appl,imgname); 
		prnt = appl;
	}

	public void onActivate(){}
	
	public void update(Graphics g)
	{
		paint(g);	
	}
	
	public synchronized void paint(Graphics g)
	{
		if (!isVisible())return;
		if (g==null) return;
		if (bckg!=null) g.drawImage(bckg,0,0,this); 
		Component [] compo = getComponents();
        for(int li=0;li<compo.length;li++)
        {
            compo[li].repaint();
        }
	}
	
}
