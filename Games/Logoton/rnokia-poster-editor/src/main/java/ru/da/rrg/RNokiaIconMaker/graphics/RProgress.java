package ru.da.rrg.RNokiaIconMaker.graphics;

import java.awt.*; 

public class RProgress extends Canvas 
{
	protected int lprogress = 0;
	
	public RProgress()
	{
		lprogress = 0;
	}
	
	public void setProgress(int new_prgr)
	{
		if (new_prgr>100) new_prgr=100;
		if (new_prgr<0) new_prgr=0;
		lprogress = new_prgr;
		repaint();
	}
	
	public void update(Graphics g)
	{
		paint(g);	
	}
	
	public void paint(Graphics g)
	{
		if (g==null) return;
		int lwdth = getBounds().width;
		int lsz = (((lwdth-2)*lprogress)/100);
		g.setColor(getBackground()); 
		g.fillRect(0,0,lwdth,getBounds().height);
		g.draw3DRect(0,0,lwdth,getBounds().height,false);
		g.setColor(getForeground());
		g.fillRect(1,1,lsz,getBounds().height-1);
	}
	
}
