// Author: Igor A. Maznitsa (rrg@forth.org.ru)
// (C) 2001 All Copyright by Igor A. Maznitsa

package ru.da.rrg.RNokiaIconMaker;

import ru.da.rrg.RNokiaIconMaker.graphics.*;    
import java.applet.*;  
import java.awt.*;
import java.io.*;
import java.awt.event.*;   

public class EditorForm extends RPanel implements RActionListener,RMPSListener,RBackDrawListener   
{
	public MobilePhoneScreen mscreen = null;
	protected PatternLine mpattern = null;
	protected RButton undo_button = null;
	protected RButton redo_button = null;
	protected RButton leftstamp_button = null;
	protected RButton rightstamp_button = null;
	protected Image scr_grid = null;
	protected boolean drag_selectarea = false;
	protected Rectangle rectt = new Rectangle(372,14,72,14); 
	protected RBackDraw previewpr = null;
	
	final static int GB_NONE = -1; // None
	final static int GB_HAND = 0; // Hand drawing
	final static int GB_LINE = 1;// Line drawing
	final static int GB_FILLRECT = 2;// Fill rectangle
	final static int GB_RECT = 3;// Rectangle
	final static int GB_ELLIPSE = 4;// Ellipse 
	final static int GB_FILLELLIPSE = 5;// Fill ellipse
	final static int GB_INVERT = 6;// Invertation of the image
	final static int GB_MIRROR = 7;// Mirror the image
	final static int GB_FLIP = 8;// Flip the image
	final static int GB_TEXT = 9;// Text drawing
	final static int GB_UNDO = 10;// Undo
	final static int GB_REDO = 11;// Redo
	final static int GB_CLEAR = 12;// Clear the screen
	final static int GB_RSCROLL = 13;// Right scroll of the screen
	final static int GB_LSCROLL = 14;// Left scroll of the screen
	final static int GB_USCROLL = 15;// Up scroll of the screen
	final static int GB_DSCROLL = 16;// Down scroll of the screen
	final static int GB_LSTAMP = 17;// Left scrool of the stamp line
	final static int GB_RSTAMP = 18;// Right scroll of the stamp line
	final static int GB_BASE = 19;// Base button
	final static int GB_EDITOR = 21;// Screen editor for the mobile picture
	final static int GB_PATTERNLINE = 22;// Pattern line
	final static int GB_AREA = 23;// Area operation
	final static int GB_PREVIEW = 24;// Preview area
	
	int current_select = GB_NONE;
	Component cur_compo = null;
	
	int strt_x = 0;
	int strt_y = 0;

	String curStr = "";

	public void setString(String str)
	{
		curStr =  str;
	}
	
	public void text_button()
	{
		IconMaker icm = (IconMaker)getParent();
		icm.changeState(icm.PS_TEXTSELECT);   
	}

	public void base_button()
	{
		IconMaker icm = (IconMaker)getParent();
		icm.changeState(icm.PS_BASEWORK);   
	}
	
	public void mps_mouseDown(boolean left_button,int x,int y)
	{
		if (left_button) mscreen.setPenColor(0); else  mscreen.setPenColor(1);

		strt_x = mscreen.translateX(x);
		strt_y = mscreen.translateY(y);

		if (mscreen.getSelectRectangle()!=null)
		{
			if (mscreen.getSelectRectangle().contains(strt_x,strt_y))
			{
				mscreen.copySelectToPattern(); 
				drag_selectarea = true;	
			}
		}
		
		mscreen.startTransaction(!left_button);
		mps_mouseDrag(x,y); 
	}
	
	public void mps_mouseUp(int x,int y)
	{
		if (mscreen.getTransactState())
		{
			mscreen.commitTransaction(); 
			
			drag_selectarea = false;
			
			updateScreen(); 
			checkStateSpecial();
		}
	}
	
	public void mps_mouseDrag(int x,int y)
	{
		if (mscreen.getTransactState())
		{
			int lx = x;
			int ly = y;
			lx = mscreen.translateX(x);
			ly = mscreen.translateY(y);
			switch (current_select)
			{
				case EditorForm.GB_TEXT : {
								mscreen.drawText(lx,ly,curStr);
						  };break;
				case EditorForm.GB_PATTERNLINE :{
								mscreen.drawPattern(lx,ly,true);
							};break;
				case EditorForm.GB_LINE :{
								mscreen.drawLine(strt_x,strt_y,lx,ly);   
							  };break;
				case EditorForm.GB_HAND :{
								mscreen.drawPixel(lx,ly);   
							  };break;
				case EditorForm.GB_FILLRECT :{
									  int lxx = Math.min(strt_x,lx);
									  int lyy = Math.min(strt_y,ly);
									  mscreen.fillRectangle(lxx,lyy,Math.abs(lx-strt_x),Math.abs(ly-strt_y));
								  };break;
				case EditorForm.GB_RECT  :{
									  int lxx = Math.min(strt_x,lx);
									  int lyy = Math.min(strt_y,ly);
									  mscreen.drawRectangle(lxx,lyy,Math.abs(lx-strt_x),Math.abs(ly-strt_y));
								  };break;
				case EditorForm.GB_ELLIPSE :{
									  int lxx = Math.min(strt_x,lx);
									  int lyy = Math.min(strt_y,ly);
									  mscreen.drawEllipse(lxx,lyy,Math.abs(lx-strt_x),Math.abs(ly-strt_y));
								  };break;
				case EditorForm.GB_FILLELLIPSE  :{
									  int lxx = Math.min(strt_x,lx);
									  int lyy = Math.min(strt_y,ly);
									  mscreen.fillEllipse(lxx,lyy,Math.abs(lx-strt_x),Math.abs(ly-strt_y));
								  };break;
				case EditorForm.GB_AREA : {
											  if (drag_selectarea)
											  {
												mscreen.drawPattern(lx,ly,false);
											  }
											  else
											 {
												  if (lx<0) lx = 0;
												  if (ly<0) ly = 0;
												  int lxx = Math.min(strt_x,lx);
												  int lyy = Math.min(strt_y,ly);
												  if (lxx<0) lxx = 0;
												  if (lyy<0) lyy = 0;
												  Rectangle rec = new Rectangle(lxx,lyy,Math.abs(lx-strt_x),Math.abs(ly-strt_y));
												  mscreen.setSelectRectangle(rec); 
												  drag_selectarea = false;
											  }
										  };break;
				case EditorForm.GB_NONE : {
									};break;
			}
			updateScreen();
		}
	}
	
	public void checkStateSpecial()
	{
		if (mscreen.isUndo())
		{
			undo_button.setEnabled(true);
		}
		else
		{
			undo_button.setEnabled(false);
		}

		if (mscreen.isRedo())
		{
			redo_button.setEnabled(true);
		}
		else
		{
			redo_button.setEnabled(false);
		}

		if (mpattern.isLeftOffscreenImages())
		{
			leftstamp_button.setEnabled(true);
		}
		else
		{
			leftstamp_button.setEnabled(false);   	
		}
		
		if (mpattern.isRightOffscreenImages())
		{
			rightstamp_button.setEnabled(true);
		}
		else
		{
			rightstamp_button.setEnabled(false);   	
		}
		undo_button.update(undo_button.getGraphics());
		redo_button.update(redo_button.getGraphics());
		leftstamp_button.update(leftstamp_button.getGraphics());
		rightstamp_button.update(rightstamp_button.getGraphics()); 
	}
	
	public void loadBMPImage(byte [] img) throws IOException
	{
		try
		{
			BMPImage imgc = new BMPImage(img);
			mscreen.setImage(imgc);  
		}
		catch(IOException exx)
		{
			mscreen.clearScreen();  	
			throw new IOException(); 
		}
	}

	public void loadOTBImage(byte [] img) throws IOException
	{
		try
		{
			OTBImage imgc = new OTBImage(img);
			mscreen.setImage(imgc);  
		}
		catch(IOException exx)
		{
			mscreen.clearScreen();  	
			throw new IOException(); 
		}
	}

	public void loadGIFImage(byte [] img) throws IOException
	{
		try
		{
			GIFImage imgc = new GIFImage(this,img);
			mscreen.setImage(imgc);  
		}
		catch(IOException exx)
		{
			mscreen.clearScreen();  	
			throw new IOException(); 
		}
	}
	
	public void actionPerformed(RActionEvent ev)
	{
		if (ev.getComponent().getID()!=current_select)
		{
			if (cur_compo!=null)
			{
				if (cur_compo instanceof RComponent)
				{
					((RComponent)cur_compo).unselect(); 
				}
			}
		}

		switch(ev.getID())
		{
			case GB_HAND :{
							  current_select = GB_HAND;
							  cur_compo = (Component)ev.getComponent(); 
					  };break;
			case GB_LINE:{
							 current_select = GB_LINE;
							 cur_compo = (Component)ev.getComponent(); 
					  };break;
			case GB_FILLRECT:{
							 current_select = GB_FILLRECT;
							 cur_compo = (Component)ev.getComponent(); 
					  };break;
			case GB_RECT:{
							 current_select = GB_RECT;
 							  cur_compo = (Component)ev.getComponent(); 
					  };break;
			case GB_ELLIPSE:{
							current_select = GB_ELLIPSE;
						  cur_compo = (Component)ev.getComponent(); 
					  };break;
			case GB_FILLELLIPSE:{
							current_select = GB_FILLELLIPSE;
						    cur_compo = (Component)ev.getComponent(); 
					  };break;
			case GB_AREA:{
						current_select = GB_AREA;
						cur_compo = (Component)ev.getComponent(); 
					  };break;
			case GB_INVERT:{
							mscreen.InvertImage(); 
						    cur_compo = null; 
					  };break;
			case GB_MIRROR:{
							mscreen.MirrorImage(); 
						    cur_compo = null; 
					  };break;
			case GB_FLIP:{
							 mscreen.FlipImage();
							 cur_compo = null; 
					  };break;
			case GB_TEXT:{
						current_select = GB_TEXT;
						cur_compo = (Component)ev.getComponent(); 
						((RButton)cur_compo).select();
						mscreen.setSelectRectangle(null);
						text_button(); 
					  };break;
			case GB_UNDO:{
							current_select = GB_NONE;
							 mscreen.undoImage();
							 cur_compo = null; 
					  };break;
			case GB_REDO:{
							 current_select = GB_NONE;
							 mscreen.redoImage();
							 cur_compo = null; 
					  };break;
			case GB_CLEAR:{
							 current_select = GB_NONE;
							 mscreen.clearScreen();
							 cur_compo = null; 
					  };break;
			case GB_RSCROLL:{
							 current_select = GB_NONE;
							 mscreen.ScrollRight();
							 cur_compo = null; 
					  };break;
			case GB_LSCROLL:{
							 current_select = GB_NONE;
							 mscreen.ScrollLeft();
							 cur_compo = null; 
							};break;
			case GB_USCROLL:{
							current_select = GB_NONE;
							 mscreen.ScrollUp();
							 cur_compo = null; 
					  };break;
			case GB_DSCROLL:{
							current_select = GB_NONE;
							 mscreen.ScrollDown();
							 cur_compo = null; 
					  };break;
			case GB_LSTAMP:{
						current_select = GB_NONE;
						mpattern.stepLeft();
						cur_compo = null; 
					  };break;
			case GB_RSTAMP:{
						current_select = GB_NONE;
						mpattern.stepRight();					
						cur_compo = null; 
						 };break;
			case GB_BASE:{
							current_select = GB_NONE;
							cur_compo = null; 
							base_button();
						 };break;
			case GB_PATTERNLINE:{
							if (mpattern.getCurrentPattern()==null) 
							{
								current_select = GB_NONE;  
								cur_compo = null;
							}
							else
							{
								mscreen.setPattern(mpattern.getCurrentPattern());  
								current_select = GB_PATTERNLINE;  	
								cur_compo = (Component)ev.getComponent(); 
							}
					  };break;
			default : {
							current_select = GB_NONE;
							cur_compo = null;
					  }
		}

		if (cur_compo!=null)
		{
			if (cur_compo instanceof RButton)
			{
				RButton rc = (RButton) cur_compo;
				if (!rc.getState())
				{
					current_select = GB_NONE;	
					cur_compo = null;
				}
			}
		}

		mscreen.setSelectRectangle(null);  
		
		this.checkStateSpecial(); 
	
		updateScreen(); 
	}
	
	public byte [] getImageAsBMP() throws IOException 
	{
		return BMPImage.encodeImage(mscreen.getScreenImage()); 
	}

	public byte [] getImageAsOTB() throws IOException 
	{
		return OTBImage.encodeImage(mscreen.getScreenImage()); 
	}

	public byte [] getImageAsGIF() throws IOException 
	{
            Image gm = mscreen.getScreenImage();
            gm = Utilities.changecolor(gm,Color.white);
            return GIFImage.encodeImage(mscreen.getScreenImage(),false,Color.white); 
	}
	
	public EditorForm(IconMaker appl) throws IOException 
	{
		super(appl,"all_alfa");	
		scr_grid = Utilities.loadImageResource(getParentApplet(),"netscreen");  

		int ll = 0;
		for(int li=0;li<IconMaker.iarr.length;li++) ll += IconMaker.iarr[li];
		if (ll!=-1059287553) throw new IOException("Fatal error");
		
		RButton rrb = null; 
		Image imageup = null;
		Image imagedwn = null;
		Image imagedsbl = null;
		
		imageup = Utilities.loadImageResource(appl,"draw");
		imagedwn = Utilities.loadImageResource(appl,"draw_on");
		rrb = new RButton(imageup,imagedwn,null,true,GB_HAND);   
		rrb.setBounds(13,11,24,20); 
		add(rrb);
		
		imageup = Utilities.loadImageResource(appl,"line");
		imagedwn = Utilities.loadImageResource(appl,"line_on");
		rrb = new RButton(imageup,imagedwn,null,true,GB_LINE);   
		rrb.setBounds(39,11,24,20);
		add(rrb);

		imageup = Utilities.loadImageResource(appl,"sq");
		imagedwn = Utilities.loadImageResource(appl,"sq_on");
		rrb = new RButton(imageup,imagedwn,null,true,GB_FILLRECT);   
		rrb.setBounds(65,11,24,20);
		add(rrb);

		imageup = Utilities.loadImageResource(appl,"rec");
		imagedwn = Utilities.loadImageResource(appl,"rec_on");
		rrb = new RButton(imageup,imagedwn,null,true,GB_RECT);   
		rrb.setBounds(91,11,24,20);
		add(rrb);

		imageup = Utilities.loadImageResource(appl,"cercle");
		imagedwn = Utilities.loadImageResource(appl,"cercle_on");
		rrb = new RButton(imageup,imagedwn,null,true,GB_ELLIPSE);   
		rrb.setBounds(117,11,24,20);
		add(rrb);

		imageup = Utilities.loadImageResource(appl,"round");
		imagedwn = Utilities.loadImageResource(appl,"round_on");
		rrb = new RButton(imageup,imagedwn,null,true,GB_FILLELLIPSE);   
		rrb.setBounds(143,11,24,20);
		add(rrb);
			
		imageup = Utilities.loadImageResource(appl,"inv");
		imagedwn = Utilities.loadImageResource(appl,"inv_on");
		rrb = new RButton(imageup,imagedwn,null,false,GB_INVERT);   
		rrb.setBounds(169,11,24,20);
		add(rrb);

		imageup = Utilities.loadImageResource(appl,"gor");
		imagedwn = Utilities.loadImageResource(appl,"gor_on");
		rrb = new RButton(imageup,imagedwn,null,false,GB_MIRROR);   
		rrb.setBounds(195,11,24,20);
		add(rrb);

		imageup = Utilities.loadImageResource(appl,"vert");
		imagedwn = Utilities.loadImageResource(appl,"vert_on");
		rrb = new RButton(imageup,imagedwn,null,false,GB_FLIP);   
		rrb.setBounds(221,11,24,20);
		add(rrb);

		imageup = Utilities.loadImageResource(appl,"text");
		imagedwn = Utilities.loadImageResource(appl,"text_on");
		rrb = new RButton(imageup,imagedwn,null,true,GB_TEXT);   
		rrb.setBounds(247,11,24,20);
		add(rrb);

		imageup = Utilities.loadImageResource(appl,"undo");
		imagedwn = Utilities.loadImageResource(appl,"undo_on");
		imagedsbl = Utilities.loadImageResource(appl,"undo_off");
		rrb = new RButton(imageup,imagedwn,imagedsbl,false,GB_UNDO);   
		rrb.setBounds(273,11,24,20);
		undo_button = rrb;
		add(rrb);

		imageup = Utilities.loadImageResource(appl,"redo");
		imagedwn = Utilities.loadImageResource(appl,"redo_on");
		imagedsbl = Utilities.loadImageResource(appl,"redo_off");
		rrb = new RButton(imageup,imagedwn,imagedsbl,false,GB_REDO);
		rrb.setBounds(299,11,24,20);
		redo_button = rrb;
		add(rrb);

		imageup = Utilities.loadImageResource(appl,"del");
		imagedwn = Utilities.loadImageResource(appl,"del_on");
		rrb = new RButton(imageup,imagedwn,null,false,GB_CLEAR);   
		rrb.setBounds(325,11,24,20);
		add(rrb);

		imageup = Utilities.loadImageResource(appl,"left");
		imagedwn = Utilities.loadImageResource(appl,"left_on");
		rrb = new RButton(imageup,imagedwn,null,false,GB_LSCROLL);   
		rrb.setBounds(13,133,19,20); 
		add(rrb);

		imageup = Utilities.loadImageResource(appl,"right");
		imagedwn = Utilities.loadImageResource(appl,"right_on");
		rrb = new RButton(imageup,imagedwn,null,false,GB_RSCROLL);   
		rrb.setBounds(34,133,19,20); 
		add(rrb);
		
		imageup = Utilities.loadImageResource(appl,"up");
		imagedwn = Utilities.loadImageResource(appl,"up_on");
		rrb = new RButton(imageup,imagedwn,null,false,GB_USCROLL);   
		rrb.setBounds(55,133,19,20); 
		add(rrb);
		
		imageup = Utilities.loadImageResource(appl,"down");
		imagedwn = Utilities.loadImageResource(appl,"down_on");
		rrb = new RButton(imageup,imagedwn,null,false,GB_DSCROLL);   
		rrb.setBounds(76,133,19,20); 
		add(rrb);

		imageup = Utilities.loadImageResource(appl,"area");
		imagedwn = Utilities.loadImageResource(appl,"area_on");
		rrb = new RButton(imageup,imagedwn,null,true,GB_AREA);   
		rrb.setBounds(97,133,24,20); 
		add(rrb);
		
		imageup = Utilities.loadImageResource(appl,"scr_left");
		imagedwn = Utilities.loadImageResource(appl,"scr_left_on");
		imagedsbl = Utilities.loadImageResource(appl,"scr_left_off");
		rrb = new RButton(imageup,imagedwn,imagedsbl,false,GB_LSTAMP);   
		leftstamp_button = rrb;
		rrb.setBounds(123,133,16,20);
		add(rrb);

		imageup = Utilities.loadImageResource(appl,"scr_right");
		imagedwn = Utilities.loadImageResource(appl,"scr_right_on");
		imagedsbl = Utilities.loadImageResource(appl,"scr_right_off");
		rrb = new RButton(imageup,imagedwn,imagedsbl,false,GB_RSTAMP);   
		rightstamp_button = rrb;
		rrb.setBounds(330,133,16,20);
		add(rrb);

		imageup = Utilities.loadImageResource(appl,"save");
		rrb = new RButton(imageup,null,null,false,GB_BASE);   
		rrb.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		rrb.setBounds(349,133,101,20);
		add(rrb);

		Color [] phone_color = new Color[]{new Color(158,214,250),Color.black};   

		mscreen = new MobilePhoneScreen(getParentApplet(),scr_grid,72,14,phone_color,6,GB_EDITOR);
		mscreen.setBounds(15,40,432,84);
		mscreen.setMPSListener(this); 
		add(mscreen);
		mscreen.setCursor(Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));   

		previewpr = new RBackDraw(GB_PREVIEW,this);
		previewpr.setBounds(372,14,72,14);
		add(previewpr);
		
		mpattern = new PatternLine(getParentApplet(),GB_PATTERNLINE);
		mpattern.setBounds(139,133,190,20);
		add(mpattern);
		mpattern.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));   
	}

	public void updateScreen()
	{
		mscreen.repaint();
		previewpr.update(previewpr.getGraphics());
	}

	public void backdrawPaint(int id,Graphics g)
	{
		switch(id)
		{
			case GB_PREVIEW : mscreen.preview(g,0,0);break;
		}
	}
	
	public void paint(Graphics g)
	{
		super.paint(g);
	}

}
