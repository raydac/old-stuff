package ru.da.rrg.RNokiaIconMaker.graphics;

import java.awt.*;

public class RLabel extends Canvas
{
	String strr;

	public String getText(){return strr;}
	public RLabel(String str){ strr = str; }

    public void validate()
    {
        repaint();
    }

	public void paint(Graphics g)
	{
		g.setColor(getBackground());
		g.fillRect(0,0,getBounds().width,getBounds().height);  
		g.setColor(getForeground());
		g.setFont(getFont());
		g.drawString(strr,0,getBounds().height-((getBounds().height-getFontMetrics(getFont()).getHeight())>>1));  
	}
	
}
