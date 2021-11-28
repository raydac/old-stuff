// Author: Igor A. Maznitsa (rrg@forth.org.ru)
// (C) 2001 All Copyright by Igor A. Maznitsa
package ru.da.rrg.RNokiaIconMaker;

import java.awt.*;
import java.applet.*;
import java.awt.event.*; 
import java.util.*; 
import java.io.*;
import ru.da.rrg.RNokiaIconMaker.graphics.*; 
import ru.da.rrg.RNokiaIconMaker.*;

public class IconMaker extends Applet implements Runnable 
{
	Image bckgrnd = null;
	EditorForm editor_form = null;
	TextSelectForm tsf = null;
	DBPanel dbp = null;
	String errormsg = null;	
	boolean isalive = true;
	Thread newthread = null;
	Frame mainframe = null;
	String init_imagename = null;
	String sendcmnd = null;
	ColorEtaloner etl = null;

	public final static int [] iarr = {0x20286874,0x74703A2F,0x2F777777,0x2E666F72,0x74682E6F,0x72672E72,0x752F7E72,0x72672920};
	
	public final static int PS_NONE = -1;
	public final static int PS_EDITOR = 0; // Editor mode
	public final static int PS_TEXTSELECT = 1; // Text select mode
	public final static int PS_BASEWORK = 2; // Work with base
	public final static int PS_SENDING = 3; // Sending of the logo
		
	int current_mode = PS_NONE; 
	
	int strt_x = 0;
	int strt_y = 0;

	public void changeState(int newstate)
	{
		current_mode = newstate;
		removeAll(); 

		editor_form.mscreen.setFont(tsf.getSelectedFont());
		editor_form.setString(tsf.getTextString());
		
		switch (current_mode)
		{
			case PS_EDITOR : 
							{
								add(editor_form);
								editor_form.onActivate();
							};break;
			case PS_TEXTSELECT : {
								add(tsf);
								tsf.onActivate(); 
								 };break;				 
			case PS_BASEWORK  : {
									add(dbp);
									dbp.setPreviewImage(editor_form.mscreen.getScreenImage());
									dbp.setImageName(init_imagename);
									dbp.setState(DBPanel.REQUEST_WINDOW);   
									dbp.onActivate(); 
								 };break;				 
		}
		repaint(this.getGraphics());  
	}
	
	public void init()
	{
		etl = new ColorEtaloner(this);

		init_imagename = getParameter("image"); 
		sendcmnd = getParameter("sendcmnd"); 
		String okurl = getParameter("okpage"); 		
		java.net.URL urlok = null;

		try
		{
			if (okurl!=null)
			{
				urlok = new java.net.URL(getDocumentBase(),okurl);
			}
		}
		catch(java.net.MalformedURLException exp)
		{
			urlok = null;
		}
		
		this.setLayout(null); 
		setBounds(0,0,463,164);
		try
		{
			editor_form = new EditorForm(this);
			tsf = new TextSelectForm(this); 
			dbp = new DBPanel(this,editor_form,sendcmnd,urlok); 
		}
		catch(IOException exx)
		{
			errormsg =exx.getMessage();
			return;
		}
		
		newthread = new Thread(this);
		newthread.start();
	}

	public void run()
	{

		try
		{
			if (init_imagename!=null)
			{
				editor_form.loadBMPImage(Utilities.getURLResourceAsByteArray(this,init_imagename));
			}
			
		}
		catch(IOException ecc)
		{
			showStatus("Can't load "+init_imagename+" file"); 
		}
		
		changeState(PS_EDITOR);  
		
		repaint(this.getGraphics());   
		
		while(isalive)
		{
			try
			{
				Thread.sleep(500);	
			}
			catch(InterruptedException exx)
			{
				break;
			}
		}
	}
	
	public void start(){}
	public void stop()
	{
		isalive = false;
	}
	public void resize(int w,int h){}
	public void resize(Dimension dim){}
	
	public Frame getParentFrame(){return mainframe;} 
	
	public void repaint(Graphics g)
	{
		paint(g);	
	}
	
	public void update(Graphics g)
	{
		paint(g);	
	}
	
	public void paint(Graphics g)
	{
		if (!isVisible()) return;
		switch(current_mode)
		{
			case PS_EDITOR :{
								if (editor_form!=null) editor_form.paint(g);  
							};break;
			case PS_TEXTSELECT :{
									if (tsf!=null) tsf.paint(g); 	
								};break;
			case PS_BASEWORK  :{
									if (dbp!=null) dbp.paint(g);
								};break;
		}
	}
	
}
