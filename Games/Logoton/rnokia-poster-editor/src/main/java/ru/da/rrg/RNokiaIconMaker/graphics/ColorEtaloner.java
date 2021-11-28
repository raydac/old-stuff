package ru.da.rrg.RNokiaIconMaker.graphics;

import java.awt.*;
import java.awt.image.*;  
import java.applet.*;

public class ColorEtaloner
{
	public static int red = 0; 
	public static int black = 0;
	public static int magenta = 0;
	
	public ColorEtaloner(Applet apl)
	{
		Image img = Utilities.createImage(apl,3,1);
		Graphics gr = img.getGraphics();
		gr.setColor(Color.red);
		gr.drawLine(0,0,0,0);
		gr.setColor(Color.black);
		gr.drawLine(1,0,1,0);
		gr.setColor(Color.magenta);
		gr.drawLine(2,0,2,0);
		
		int [] imggrab = new int[3];  
		PixelGrabber pxg = new PixelGrabber(img,0,0,3,1,imggrab,0,3);
		try
		{
			pxg.grabPixels(); 
		}
		catch(InterruptedException exx)
		{
			return;
		}

		red   = imggrab[0]&0x00FFFFFF;
		black = imggrab[1]&0x00FFFFFF;
		magenta = imggrab[2]&0x00FFFFFF;
	}
	
}
