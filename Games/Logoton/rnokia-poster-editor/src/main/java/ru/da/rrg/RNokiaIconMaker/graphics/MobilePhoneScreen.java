// Author: Igor A. Maznitsa (rrg@forth.org.ru)
// (C) 2001 All Copyright by Igor A. Maznitsa
package ru.da.rrg.RNokiaIconMaker.graphics;

import java.awt.*; 
import java.applet.*; 
import java.awt.image.*; 
import java.awt.event.*; 

public class MobilePhoneScreen extends Canvas implements RComponent,MouseListener,MouseMotionListener  
{
	protected Image pattern = null;
	protected boolean transact_state = false; // Flag of transaction state
	protected int comp_x = 0; // X coord of top left corner of the component
	protected int comp_y = 0; // Y coord of top left corner of the component
	protected int scr_width = 100; // Width of the phone screen
	protected int scr_height = 100; // Height of the phone screen
	protected Color [] phone_colors = null; // Array of supported phone color
	protected Color select_color = Color.blue; // Color of selected rectangle
	protected Image hiddenimage = null; // Offscreen image
	protected Image undoimage = null; // Undo image
	protected Image redoimage = null; // Undo image
	protected Image transactimage = null; // Offscreen transact image
	protected Image bufferimage = null; //Offscreen buffer image
	protected Graphics hiddengraphics = null; // Offscreen graphics
	protected Graphics transactgraphics = null; // Offscreen transact graphics
	protected int zoom_factor = 4 ; // Zoom factor
	protected int current_pencolor_index = 1; // Current color index of the pen color
	protected int [] grabbuffer = null;
	protected int [] patternbuffer = null;
	protected int component_id;
	protected boolean having_undo = false;
	protected boolean having_redo = false;
	protected boolean edit_start = false;
	protected Applet prnt_applet = null;
	protected RFont cur_font = null; 
	protected boolean constructor_work = true;
	protected RMPSListener lstnr = null;
	protected Image grid_image = null;
	protected boolean transact_leftbutton = false;
	
	protected Rectangle select_rectangle = null;
	
	public boolean isUndo(){ return having_undo; }
	public boolean isRedo(){ return having_redo; }
	
	public void setFont(RFont fnt){cur_font=fnt;} 
	public void setPenColor(int index){ current_pencolor_index = index; }
	public int getWidth(){ return scr_width;}
	public int getHeight(){ return scr_height;}
	public int getColorDepth(){ return phone_colors.length;}
	public Color getColorByIndex(int index){ return phone_colors[index];}
	public int getZoomFactor(){ return zoom_factor;}
	public boolean getTransactState(){ return transact_state;}
	public void setMPSListener (RMPSListener list){lstnr = list;}
	
	public void mousePressed(MouseEvent e)
	{
		if (lstnr!=null) 
		  	if ((e.getModifiers()&e.BUTTON1_MASK)==0)
				lstnr.mps_mouseDown(true,e.getX(),e.getY()); 
			else
				lstnr.mps_mouseDown(false,e.getX(),e.getY()); 
	}

	public void mouseExited(MouseEvent e){}
	public void mouseEntered(MouseEvent e){}
	public void mouseClicked(MouseEvent e){}
	public void mouseMoved(MouseEvent e){}
	
	public void unselect(){}
	
	public void mouseReleased(MouseEvent e)
	{
		if (lstnr!=null) lstnr.mps_mouseUp(e.getX(),e.getY()); 
	}
	
	public void mouseDragged(MouseEvent e)
	{
		if (lstnr!=null) lstnr.mps_mouseDrag(e.getX(),e.getY()); 
	}
	
	public Rectangle getSelectRectangle()
	{
		return select_rectangle; 
	}
	
	public void setSelectRectangle(Rectangle rct)
	{
		select_rectangle = rct;	
	}
	
	public Image getScreenImage()
	{
		return hiddenimage;
	}
	
	public void setPattern(Image ptrn)
	{
		pattern = ptrn;	
		if (pattern!=null)
		{
			patternbuffer = new int [pattern.getWidth(null)*pattern.getHeight(null)];
			PixelGrabber pxg = new PixelGrabber(pattern,0,0,pattern.getWidth(null),pattern.getHeight(null),patternbuffer,0,pattern.getWidth(null));
			try
			{
				pxg.grabPixels(); 
			}
			catch(InterruptedException exx)
			{
				return;	
			}
		}
	}

	public void setImage(RImage img)
	{
		Color col0 = phone_colors[0];
		Color col1 = phone_colors[1];
		int [] imarr = img.getImageArray(); 
		for(int lx=0;lx<img.getWidth();lx++)
		{
			for (int ly=0;ly<img.getHeight();ly++)
			{
				int lcol = imarr[lx+ly*img.getWidth()];
				if (lcol!=0) 
				{
					hiddengraphics.setColor(col1);  
				}
				else
				{
					hiddengraphics.setColor(col0);  
				}
				hiddengraphics.drawLine(lx,ly,lx,ly);  
			}
		}
	}
	
	public void startTransaction(boolean mode)
	{
		if (transact_state) return;
		clearTransactScreen();
		transact_leftbutton = mode;
		if (transact_leftbutton) transactgraphics.setColor(Color.red); else transactgraphics.setColor(Color.magenta);
		transact_state=true; 
	}
	
	public void commitTransaction()
	{
		if (!transact_state) return;
		transact_state = false;

		PixelGrabber lgrab = new PixelGrabber(transactimage,0,0,scr_width,scr_height,grabbuffer,0,scr_width);
		try
		{
			lgrab.grabPixels(); 
		}
		catch(InterruptedException exx)
		{
			return;	
		}
	
		edit_start = true;
		fill_undo(); 

		Color set_color = getColorByIndex(1);
		Color reset_color = getColorByIndex(0);
		
		int trc_set = ColorEtaloner.red; 
		int trc_reset = ColorEtaloner.magenta; 
		
		for (int lx=0;lx<scr_width;lx++)
		{
			for (int ly=0;ly<scr_height;ly++)
			{
				int lll = grabbuffer[lx+ly*scr_width];
				
				if (Utilities.checkColor(lll,trc_set)) 
				{	
					hiddengraphics.setColor(set_color);
					hiddengraphics.drawLine(lx,ly,lx,ly); 
				}
				else
				if (Utilities.checkColor(lll,trc_reset))
				{	
					hiddengraphics.setColor(reset_color);
					hiddengraphics.drawLine(lx,ly,lx,ly); 
				}
				
			}
		}
		lgrab = null;
		clearTransactScreen(); 
	}
	
	public void rollbackTransaction()
	{
		clearTransactScreen(); 
		transact_state = false;
	}
	
	protected void changeZoomFactor(int newzoom)
	{
		zoom_factor = newzoom;
		int new_wdth = scr_width*zoom_factor;
		int new_hght = scr_height*zoom_factor;
		bufferimage = Utilities.createImage(prnt_applet,new_wdth,new_hght);
		setBounds(getBounds().x,getBounds().y,scr_width*zoom_factor,scr_height*zoom_factor);
	}

	public void setBounds(int x,int y,int w,int h)
	{
		super.setBounds(x,y,w,h);
	}

	public void setBounds(Rectangle rect)
	{
		super.setBounds(rect);
	}
	
	public int getID()
	{
		return component_id;
	}
	
	public MobilePhoneScreen(Applet prntappl,Image gridimg,int phonewidth, int phoneheight, Color [] colors,int zoom,int id)
	{
		super();
		grid_image = gridimg;
		prnt_applet = prntappl; 
		component_id  = id;
		scr_width = phonewidth;
		scr_height = phoneheight;
		
		phone_colors = colors;
		undoimage = Utilities.createImage(prnt_applet,scr_width,scr_height);
		redoimage = Utilities.createImage(prnt_applet,scr_width,scr_height);
		hiddenimage = Utilities.createImage(prnt_applet,scr_width,scr_height);
		hiddengraphics = hiddenimage.getGraphics(); 
		transactimage = Utilities.createImage(prnt_applet,scr_width,scr_height);
		transactgraphics = transactimage.getGraphics(); 
		grabbuffer = new int[scr_width*scr_height];
		changeZoomFactor(zoom);
		clearScreen();
		clearTransactScreen(); 
		constructor_work = false;
		this.addMouseListener(this);
		this.addMouseMotionListener(this);
	}

	public void clearTransactScreen()
	{
		transactgraphics.drawImage(hiddenimage,0,0,this);
	}
	
	public void clearScreen()
	{
		edit_start = true;
		fill_undo();
		hiddengraphics.setColor(phone_colors[0]);
		hiddengraphics.fillRect(0,0,scr_width,scr_height);
	}

	public int translateX(int x)
	{
		return x/zoom_factor;	
	}
	
	public int translateY(int y)
	{
		return y/zoom_factor;	
	}
	
	//==================Graphical transact functions===========================

	public void copySelectToPattern()
	{
		pattern = Utilities.createImage(this,select_rectangle.width,select_rectangle.height);    
		patternbuffer = new int [select_rectangle.width*select_rectangle.height]; 
		PixelGrabber pxg = new PixelGrabber(hiddenimage,0,0,scr_width,scr_height,grabbuffer,0,scr_width);
		try
		{
			pxg.grabPixels(); 
		}
		catch(InterruptedException exx)
		{
			return;	
		}

		for(int lx=0;lx<select_rectangle.width;lx++)
		{
			for(int ly=0;ly<select_rectangle.height;ly++)
			{
				int llx = lx+select_rectangle.x; 
				int lly = ly+select_rectangle.y;
				int px = grabbuffer[llx+lly*scr_width];
				if (Utilities.checkColor(px,ColorEtaloner.black))
				{
					patternbuffer [lx+ly*select_rectangle.width] = ColorEtaloner.black;   
				}
				else
				{				   
					patternbuffer [lx+ly*select_rectangle.width] = 0x00FFFFFF;
				}
			}
		}
	}
	
	public void drawLine(int x1,int y1,int x2,int y2)
	{
		if (!transact_state) return;
		clearTransactScreen(); 
		transactgraphics.drawLine(x1,y1,x2,y2);
	}

	public void drawPattern(int x1,int y1,boolean trans)
	{
		if (!transact_state) return;
		if (pattern==null) return; 
		int lw=pattern.getWidth(null);
		int lh=pattern.getHeight(null);
		int offx = lw>>1;
		int offy = lh>>1;
		int lclr = ColorEtaloner.black;  
		
		clearTransactScreen();
		
		for(int lx=0;lx<lw;lx++)
		{
			for(int ly=0;ly<lh;ly++)
			{
				int lox = x1+lx-offx;
				int loy = y1+ly-offy;

				if (Utilities.checkColor(lclr,patternbuffer[ly*lw+lx]))
				{
					if (transact_leftbutton) transactgraphics.setColor(Color.red); else transactgraphics.setColor(Color.magenta);
					
					transactgraphics.drawLine(lox,loy,lox,loy);  	
				}
				else
				{
					if (!trans)
					{
						if (transact_leftbutton) transactgraphics.setColor(Color.magenta); else transactgraphics.setColor(Color.red);
						transactgraphics.drawLine(lox,loy,lox,loy);
					}
				}
			}
		}
	}
	
	public void drawText(int x1,int y1,String str)
	{
		if (!transact_state) return;
		if (cur_font==null) return; 
		clearTransactScreen();
		int lxoff = cur_font.getStringWidth(str)>>1;
		int lyoff = cur_font.CHAR_MAX_HEIGHT>>1; 
		cur_font.drawString(transactgraphics,str,x1-lxoff,y1-lyoff);
	}
	
	public void drawPixel(int x1,int y1)
	{
		if (!transact_state) return;
		transactgraphics.drawLine(x1,y1,x1,y1);
	}
	
	public void fillRectangle(int x1,int y1,int w,int h)
	{
		if (!transact_state) return;
		clearTransactScreen(); 
		transactgraphics.fillRect(x1,y1,w,h);	
	}
	
	public void drawRectangle(int x1,int y1,int w,int h)
	{
		if (!transact_state) return;
		clearTransactScreen(); 
		transactgraphics.drawRect(x1,y1,w,h);
	}
	
	public void fillEllipse(int x1,int y1,int w,int h)
	{
		if (!transact_state) return;
		clearTransactScreen(); 
		transactgraphics.fillOval(x1,y1,w,h);
	}
	
	public void drawEllipse(int x1,int y1,int w,int h)
	{
		if (!transact_state) return;
		clearTransactScreen(); 
		transactgraphics.drawOval(x1,y1,w,h);
	}
	//================================================================
	protected void to_undo()
	{
		undoimage.getGraphics().drawImage(hiddenimage,0,0,this);
		having_undo=true;
	}

	protected void to_redo()
	{
		redoimage.getGraphics().drawImage(hiddenimage,0,0,this);
		having_redo=true;
	}

	protected void from_undo()
	{
		hiddengraphics.drawImage(undoimage,0,0,this);
		having_undo = false;		
	}

	protected void from_redo()
	{
		hiddengraphics.drawImage(redoimage,0,0,this);
		having_redo = false;		
	}
	
	protected void fill_undo()
	{
		if (constructor_work) return;
		if (!edit_start) return;
		to_undo();
		having_redo=false; 
	}
	
	public void undoImage()
	{
		if (!having_undo) return;
		to_redo(); 
		from_undo();
	}

	public void redoImage()
	{
		if (!having_redo) return;
		to_undo(); 
		from_redo(); 
	}

	//========================Image manipulate functions==============
	public void InvertImage()
	{
		edit_start = true;
		PixelGrabber lgrab = new PixelGrabber(hiddenimage,0,0,scr_width,scr_height,grabbuffer,0,scr_width);
		try
		{
			lgrab.grabPixels(); 
		}
		catch(InterruptedException exx)
		{
			return;	
		}
		fill_undo(); 
		int lclr = ColorEtaloner.black;//  phone_colors[1].getRGB();
		for (int lx=0;lx<scr_width;lx++)
		{
			for(int ly=0;ly<scr_height;ly++)
			{
				switch(phone_colors.length)
				{
					case 2: {
								int lcolor = grabbuffer[lx+ly*scr_width];
								if (Utilities.checkColor(lcolor,lclr))
								{
									hiddengraphics.setColor(phone_colors[0]);
								}
								else
								{
									hiddengraphics.setColor(phone_colors[1]);
								}
								hiddengraphics.drawLine(lx,ly,lx,ly);
							};break;
				}
			}
		}
		lgrab = null;
	}
	
	public void MirrorImage()
	{
		edit_start = true;
		PixelGrabber lgrab = new PixelGrabber(hiddenimage,0,0,scr_width,scr_height,grabbuffer,0,scr_width);
		try
		{
			lgrab.grabPixels(); 
		}
		catch(InterruptedException exx)
		{
			return;	
		}
		fill_undo(); 
		int llx=scr_width-1;
		for (int lx=0;lx<scr_width;lx++)
		{
			for(int ly=0;ly<scr_height;ly++)
			{
				hiddengraphics.setColor(new Color(grabbuffer[lx+ly*scr_width]));
				hiddengraphics.drawLine(llx,ly,llx,ly);
			}
			llx--;
		}
		lgrab = null;
	}
	
	public void FlipImage()
	{
		edit_start = true;
		PixelGrabber lgrab = new PixelGrabber(hiddenimage,0,0,scr_width,scr_height,grabbuffer,0,scr_width);
		try
		{
			lgrab.grabPixels(); 
		}
		catch(InterruptedException exx)
		{
			return;	
		}
		fill_undo(); 
		int lly=scr_height-1;
		for(int ly=0;ly<scr_height;ly++)
		{
			for (int lx=0;lx<scr_width;lx++)
			{
				hiddengraphics.setColor(new Color(grabbuffer[lx+ly*scr_width]));
				hiddengraphics.drawLine(lx,lly,lx,lly);
			}
			lly--;
		}
		lgrab = null;
	}

	public void ScrollUp()
	{
		edit_start = true;
		PixelGrabber lgrab = new PixelGrabber(hiddenimage,0,0,scr_width,scr_height,grabbuffer,0,scr_width);

		try
		{
			lgrab.grabPixels(); 
		}
		catch(InterruptedException exx)
		{
			return;	
		}
		fill_undo(); 
		for(int ly=0;ly<(scr_height-1);ly++)
		{
			for (int lx=0;lx<scr_width;lx++)
			{
				hiddengraphics.setColor(new Color(grabbuffer[lx+(ly+1)*scr_width]));
				hiddengraphics.drawLine(lx,ly,lx,ly);
			}
		}
		hiddengraphics.setColor(phone_colors[0]); 
		hiddengraphics.drawLine(0,scr_height-1,scr_width-1,scr_height-1);    
		lgrab = null;
	}

	public void ScrollDown()
	{
		edit_start = true;
		PixelGrabber lgrab = new PixelGrabber(hiddenimage,0,0,scr_width,scr_height,grabbuffer,0,scr_width);
		try
		{
			lgrab.grabPixels(); 
		}
		catch(InterruptedException exx)
		{
			return;	
		}
		fill_undo(); 
		for(int ly=(scr_height-1);ly>0;ly--)
		{
			for (int lx=0;lx<scr_width;lx++)
			{
				hiddengraphics.setColor(new Color(grabbuffer[lx+(ly-1)*scr_width]));
				hiddengraphics.drawLine(lx,ly,lx,ly);
			}
		}
		hiddengraphics.setColor(phone_colors[0]); 
		hiddengraphics.drawLine(0,0,scr_width-1,0);    
		lgrab = null;
	}
	
	public void ScrollLeft()
	{
		edit_start = true;
		PixelGrabber lgrab = new PixelGrabber(hiddenimage,0,0,scr_width,scr_height,grabbuffer,0,scr_width);
		try
		{
			lgrab.grabPixels(); 
		}
		catch(InterruptedException exx)
		{
			return;	
		}
		fill_undo(); 
		for (int lx=(scr_width-2);lx>=0;lx--)
		{
			for(int ly=0;ly<scr_height;ly++)
			{
				hiddengraphics.setColor(new Color(grabbuffer[lx+1+ly*scr_width]));
				hiddengraphics.drawLine(lx,ly,lx,ly);
			}
		}
		hiddengraphics.setColor(phone_colors[0]); 
		hiddengraphics.drawLine(scr_width-1,0,scr_width-1,scr_height-1);    
		lgrab = null;
	}

	public void ScrollRight()
	{
		edit_start = true;
		PixelGrabber lgrab = new PixelGrabber(hiddenimage,0,0,scr_width,scr_height,grabbuffer,0,scr_width);
		try
		{
			lgrab.grabPixels(); 
		}
		catch(InterruptedException exx)
		{
			return;	
		}

		fill_undo(); 
		for (int lx=1;lx<scr_width;lx++)
		{
			for(int ly=0;ly<scr_height;ly++)
			{
				hiddengraphics.setColor(new Color(grabbuffer[lx-1+ly*scr_width]));
				hiddengraphics.drawLine(lx,ly,lx,ly);
			}
		}
		hiddengraphics.setColor(phone_colors[0]); 
		hiddengraphics.drawLine(0,0,0,scr_height-1);    
		lgrab = null;
	}
	//================================================================

	public void update(Graphics g){ paint(g);}
	
	public void paint(Graphics g)
	{
		if (g==null) return;
		
		if (grid_image == null)
		{
			if (transact_state) 
			{
				g.drawImage(transactimage,0,0,getBounds().width,getBounds().height,this);
			}
			else
			{
				g.drawImage(hiddenimage,0,0,getBounds().width,getBounds().height,this);
			}
		}
		else
		{
			Graphics grph = bufferimage.getGraphics(); 
			if (transact_state) 
			{
				grph.drawImage(transactimage,0,0,getBounds().width,getBounds().height,this);
			}
			else
			{
				grph.drawImage(hiddenimage,0,0,getBounds().width,getBounds().height,this);
			}
			grph.drawImage(grid_image,0,0,this);  
			
			if (select_rectangle!=null)
			{
				grph.setColor(select_color);
				
				select_rectangle.x = Math.max(0,select_rectangle.x);
				select_rectangle.y = Math.max(0,select_rectangle.y);

				if ((select_rectangle.width+select_rectangle.x)>scr_width) select_rectangle.width = scr_width-select_rectangle.x; 
				if ((select_rectangle.height+select_rectangle.y)>scr_height)select_rectangle.height = scr_height-select_rectangle.y; 
				
				int lx = select_rectangle.x*zoom_factor;
				int ly = select_rectangle.y*zoom_factor;
				
				int lw = select_rectangle.width*zoom_factor;
				int lh = select_rectangle.height*zoom_factor;
				
				grph.drawRect(lx,ly,lw,lh);  
				grph.drawRect(lx+1,ly+1,lw-2,lh-2);  
			}
			
			g.drawImage(bufferimage,0,0,this);
		}
	}
	
	public void preview(Graphics g,int x,int y)
	{
		g.drawImage(hiddenimage,x,y,this);
	}
	
}
