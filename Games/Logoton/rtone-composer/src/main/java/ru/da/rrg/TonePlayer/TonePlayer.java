// Author: Igor A. Maznitsa (rrg@forth.org.ru)
// (C) 2001 All Copyright by Igor A. Maznitsa
package ru.da.rrg.TonePlayer;

import java.awt.*;
import java.applet.*;
import ru.da.rrg.musicengine.*;    
import java.io.*;
import java.util.*; 

public class TonePlayer extends Applet
{
	MusicList ml=null;
	MusicPlayer mplr = null;
	PhoneScreen phscr = null;
	String fname = null;
	String imagename = null;
	String wavefile = null;
	String trans = null;
	String tonefile = null;
	float tra = 1f;	
	String err = null;
	Rectangle screenrct=null;
	Point strcoord = null;
	Font titlefnt = null;
	Color titleclr = null;
	
	public void start()
	{
		System.out.println("Start");
		
		if (err!=null)
		{
			showStatus(err);
			return;
		}
		repaint();
		phscr.setText(ml.getName()); 
		mplr.setMusicList(ml);
		mplr.play(); 
	}
	
	public void stop()
	{
		System.out.println("Stop");
		mplr.stop(); 	
	}

	public void paint(Graphics g)
	{
		if (err!=null)
		{
			g.drawString(err,5,20);	
			showStatus(err);
			return;
		}
		paintComponents(g); 	
	}
	
	public void init() 
	{
		try
		{
			fname = getParameter("cfgfile"); 
			tonefile = getParameter("tonefile"); 
			if ((fname==null)||(tonefile==null)) throw new IOException("Parameter not found");
			tonefile=tonefile.trim().toLowerCase(); 
			
			byte [] cfgfile = Util.getURLResourceAsByteArray(this,fname);
			Properties prp = new Properties();
			prp.load(new ByteArrayInputStream(cfgfile));  
			
			imagename = prp.getProperty("image");
			wavefile = prp.getProperty("wave");
			trans = prp.getProperty("trans");
			String scrrect= prp.getProperty("screen");
			String titlecoord= prp.getProperty("titlecoord");
			String fontstr= prp.getProperty("font");
			String fontcolor= prp.getProperty("titlecolor");
			
			if ((fontcolor==null)||(fontstr==null)||(scrrect==null)||(titlecoord==null)||(wavefile==null)||(trans==null)) throw new IOException("Error config file");  

			try
			{
				StringTokenizer tkn = new StringTokenizer(fontstr,",");
				String fnme = tkn.nextToken();
				int styl = Integer.parseInt(tkn.nextToken());
				int sz = Integer.parseInt(tkn.nextToken());
				titlefnt = new Font(fnme,styl,sz);
			}
			catch(NumberFormatException ex){ throw new IOException("Error format of screen coordinates");}
			catch(NoSuchElementException ex){throw new IOException("Error screen coordinates");}
			
			try
			{
				StringTokenizer tkn = new StringTokenizer(fontcolor,",");
				int r = Integer.parseInt(tkn.nextToken());
				int g = Integer.parseInt(tkn.nextToken());
				int b = Integer.parseInt(tkn.nextToken());
				titleclr = new Color(r,g,b); 
			}
			catch(NumberFormatException ex){ throw new IOException("Error format of color values");}
			catch(NoSuchElementException ex){throw new IOException("Error color values");}
			
			try
			{
				StringTokenizer tkn = new StringTokenizer(scrrect,",");
				int x = Integer.parseInt(tkn.nextToken());
				int y = Integer.parseInt(tkn.nextToken());
				int w = Integer.parseInt(tkn.nextToken());
				int h = Integer.parseInt(tkn.nextToken());
				screenrct = new Rectangle(x,y,w,h); 
			}
			catch(NumberFormatException ex){ throw new IOException("Error format of screen coordinates");}
			catch(NoSuchElementException ex){throw new IOException("Error screen coordinates");}

			try
			{
				StringTokenizer tkn = new StringTokenizer(titlecoord,",");
				int x = Integer.parseInt(tkn.nextToken());
				int y = Integer.parseInt(tkn.nextToken());
				strcoord = new Point(x,y); 
			}
			catch(NumberFormatException ex){ throw new IOException("Error format of title coordinates");}
			catch(NoSuchElementException ex){throw new IOException("Error title coordinates");}
			
			try
			{
				tra = Float.valueOf(trans).floatValue(); 
			}
			catch(NumberFormatException ex)
			{ tra =1f;}
			
			this.setLayout(null); 
			byte [] ott = Util.getURLResourceAsByteArray(this,tonefile);
			byte [] ptrn = Util.getURLResourceAsByteArray(this,wavefile);
			Image img = null;
			if (imagename!=null) img = Util.loadURLImageResource(this,imagename);
			phscr = new PhoneScreen(this,img,titleclr,titlefnt,screenrct,strcoord);   
			if (img!=null) 
				phscr.setBounds(new Rectangle(0,0,img.getWidth(this),img.getHeight(this)));
			else 
				phscr.setBounds(new Rectangle(0,0,this.getSize().width,this.getSize().height));	
			add(phscr); 
			
			mplr = new MusicPlayer(ptrn,tra);
			ml = new MusicList();

			//ml.loadFromTextOTT(ott);
			
			
			if (tonefile.endsWith("ott")) ml.loadFromOTT(ott); 
			else
			if (tonefile.endsWith("rtx")) ml.loadFromRTT(ott); 
			else
				throw new IOException("Unsupported file extension"); 
			
		}
		catch(IOException ex)
		{
			err = "IOError:"+ex.getMessage(); 
			System.out.println(err);
		}
	}


}
