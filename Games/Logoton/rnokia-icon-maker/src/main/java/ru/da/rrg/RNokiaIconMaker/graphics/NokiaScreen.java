// Author: Igor A. Maznitsa (rrg@forth.org.ru)
// (C) 2001 All Copyright by Igor A. Maznitsa
package ru.da.rrg.RNokiaIconMaker.graphics;

import java.awt.*; 
import java.io.*; 
import java.applet.*;

public class NokiaScreen extends Canvas
{
	static Image img;
	Image txtimg = null;
	Font cfnt = null;
	FontMetrics cfm = null;
	String cstr = "";
	Applet prnt = null;
	RFont rfnt = null;
	Image primage = null;
	
	public NokiaScreen(Applet prntappl) throws IOException
	{
		prnt = prntappl; 
		if (img==null) img = Utilities.loadImageResource(prntappl,"screen");
		txtimg = prntappl.createImage(72,14);
		ref();
	}

	public void setDFont(RFont fnt)
	{
		rfnt = fnt;
	}
	
	public void setDString(String str)
	{
		cstr = str;
		ref();
	}
	
	public void setPreviewImage(Image pr)
	{
		primage = pr;
	}
	
	protected void ref()
	{
		Graphics gr = txtimg.getGraphics();
		gr.setColor(Color.green);
		gr.fillRect(0,0,72,14);
		if (rfnt==null) return; 
		gr.setFont(cfnt);
		gr.setColor(Color.black);  
		int lw = rfnt.getStringWidth(cstr)>>1; 
		rfnt.drawString(gr,cstr,36-lw,0);  
	}
	
	public void update(Graphics g)
	{
		ref();
		paint(g);	
	}
	
	public void paint(Graphics g)
	{
		if (g==null) return;
		g.drawImage(img,0,0,null); 
		if (primage!=null)
		{
			g.drawImage(primage,20,50,this);
		}
		else
		{
			g.drawImage(txtimg,20,50,this);
		}
	}
	
}
