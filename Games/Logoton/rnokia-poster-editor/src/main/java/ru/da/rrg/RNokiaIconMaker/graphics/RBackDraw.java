package ru.da.rrg.RNokiaIconMaker.graphics;

import java.awt.*;

public class RBackDraw extends Canvas
{
	protected int id;
	protected RBackDrawListener lstnr;
	
	public int getBackDawID(){ return id;}

	public RBackDraw(int id,RBackDrawListener listener)
	{
		super();
		this.id = id;	
		this.lstnr = listener;
	}
	
	public void paint(Graphics g)
	{
		if (lstnr!=null)
			lstnr.backdrawPaint(id,g);
	}
}
