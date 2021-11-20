//=========================================//
// "TORWART"
// Author : Igor A. Maznitsa
// Version: v1.00
//=========================================//

import java.awt.image.*; 
import java.awt.*;
import java.applet.*;
import java.net.*;
import java.io.*;

public class torwart extends Applet implements Runnable
{
	private String images_placement = null;
	private String error_resource = "";
	private Color background_color = null;
	private MediaTracker mtr =new MediaTracker(this); 
	private Font outfont = new Font("Arial",Font.BOLD,12);
	
	private Image hidden_image = null;
	private Graphics hidden_graphics = null;
	private int FIELD_WIDTH = 20;
	private int FIELD_HEIGHT = 40;
	private int MAX_FIELD_WIDTH = 45;
	private int MAX_FIELD_HEIGHT = 45;

	private final static int x_offset = 5;
	private final static int y_offset = 5;
	
	private Image [] frames = new Image[9];
	private Image SCORES_IMAGE = null;
	private Image BALLS_IMAGE = null;
	private Image STOP_IMAGE = null;
	private Image FULLFRAME_IMAGE = null;
	
	private int FIELD_X_OFFSET = 0;
	private int FIELD_Y_OFFSET = 0;
	
	private boolean FIELD_ORIENTATION = false;
	private int BITA_LENGTH = 3;
	private int APPLET_WIDTH = 0;
	private int APPLET_HEIGHT = 0;
	
	private int INFO_PANEL_WIDTH = 130;
	
	private int game_state; // состояние игры
	private static final int GAME_START = 0;
	private static final int GAME_RUN   = 1;
	private static final int GAME_STOP  = 2;
	private static final int GAME_LOADING  = 3;
	private static final int GAME_ERRORLOAD  = 4;
	
	private int KEY_DIRECT  = 0;
	
	private java.util.Random rnd = new java.util.Random(System.currentTimeMillis()); 
	private int [][] game_array = null;
	
	private static final int TORWART_FIELD = 0;
	private static final int TORWART_BALL  = 1;
	private static final int TORWART_WALL  = 2;
	private static final int TORWART_BITA  = 3;

	private Color FIELD_COLOR = new Color(0,128,0); 
	private Color BALL_COLOR  = Color.cyan;											
	private Color BITA_COLOR  = Color.orange;											
	private Color WALL_COLOR  = Color.red;											
	
	private int CELL_WIDTH = 0;
	private int CELL_HEIGHT = 0;

	private int bita_x = 0;	// Координаты биты
	private int bita_y = 0;
	private int old_bita_x = 0;
	private int old_bita_y = 0;

	private int mouse_x = 0;
	private int mouse_y = 0;
	
	private float ball_x = 0; // Координаты мяча
	private float ball_y = 0; 
	private float ball_step_x = 0; // Шаги меча по координатам
	private float ball_step_y = 0;

	private int ball_count = 0; // Количество пойманных мячей
	private int ball_counter = 0; // Количество оставшихся мячей
	private int ball_number = 100; // Общее количество мячей
	
	private final static int SPRITE_WIDTH = 15;
	private final static int SPRITE_HEIGHT = 15;
	
	private final static int C_W = Color.white.getRGB();  
	private final static int C_B = Color.black.getRGB();
	private final static int C_G = new Color(0,128,0).getRGB();
	private final static int C_A = Color.gray.getRGB();

	private Font panel_font = new Font("Dialog",Font.BOLD,12);
	
	// Button
	private Font button_font = new Font("Dialog",Font.BOLD,10);
	private	String button_text = "STOP GAME";
	private	int button_width =0;
	private	int button_height =0;
	private	int button_x =0;
	private	int button_y = 150;
	
	private int [] BALL_SPRITE = { 
									C_G,C_G,C_G,C_G,C_G,C_A,C_B,C_B,C_B,C_A,C_G,C_G,C_G,C_G,C_G,
									C_G,C_G,C_G,C_A,C_W,C_W,C_A,C_B,C_A,C_W,C_W,C_W,C_G,C_G,C_G,
									C_G,C_G,C_B,C_W,C_W,C_W,C_W,C_W,C_W,C_W,C_W,C_W,C_B,C_G,C_G,
									C_G,C_B,C_B,C_W,C_W,C_W,C_W,C_W,C_W,C_W,C_W,C_W,C_B,C_B,C_G,
									C_G,C_B,C_A,C_W,C_W,C_W,C_W,C_W,C_W,C_W,C_W,C_W,C_B,C_B,C_G,
									C_A,C_A,C_W,C_W,C_W,C_W,C_W,C_A,C_W,C_W,C_W,C_W,C_W,C_W,C_A,
									C_W,C_W,C_W,C_W,C_W,C_W,C_A,C_B,C_B,C_W,C_W,C_W,C_W,C_W,C_W,
									C_W,C_W,C_W,C_W,C_W,C_A,C_B,C_B,C_B,C_A,C_W,C_W,C_W,C_W,C_W,
									C_W,C_W,C_W,C_W,C_W,C_A,C_B,C_B,C_B,C_A,C_W,C_W,C_W,C_W,C_W,
									C_A,C_W,C_W,C_W,C_W,C_W,C_B,C_B,C_B,C_W,C_W,C_W,C_W,C_W,C_W,
									C_G,C_W,C_W,C_W,C_W,C_W,C_W,C_W,C_W,C_W,C_W,C_W,C_W,C_W,C_A,
									C_G,C_A,C_A,C_A,C_W,C_W,C_W,C_W,C_W,C_W,C_W,C_A,C_A,C_A,C_G,
									C_G,C_G,C_B,C_B,C_W,C_W,C_W,C_W,C_W,C_W,C_A,C_B,C_B,C_G,C_G,
									C_G,C_G,C_G,C_B,C_A,C_W,C_W,C_W,C_W,C_W,C_A,C_B,C_G,C_G,C_G,
									C_G,C_G,C_G,C_G,C_G,C_G,C_W,C_W,C_W,C_W,C_W,C_G,C_G,C_G,C_G
								};

	
	private Image BALL_IMAGE  = createImage(new MemoryImageSource(SPRITE_WIDTH,SPRITE_HEIGHT,BALL_SPRITE,0,SPRITE_WIDTH));
	
	private void init_game_field()
	{
		if (FIELD_ORIENTATION)
		{
			// Горизонтальное положение
			for(int ly=0;ly<FIELD_HEIGHT;ly++)
				for(int lx=0;lx<FIELD_WIDTH;lx++)
				{
					if ((lx==0)||(ly==0)||(ly==(FIELD_HEIGHT-1))) game_array[lx][ly] = TORWART_WALL;
					else game_array[lx][ly] = TORWART_FIELD;
				}
			bita_x = 5;
			bita_y = (FIELD_HEIGHT - BITA_LENGTH)/2;
		}
		else
		{
			// Вертикальное положение
			for(int ly=0;ly<FIELD_HEIGHT;ly++)
				for(int lx=0;lx<FIELD_WIDTH;lx++)
				{
					if ((ly==(FIELD_HEIGHT-1))||(lx==0)||(lx==(FIELD_WIDTH-1))) game_array[lx][ly] = TORWART_WALL;
					else game_array[lx][ly] = TORWART_FIELD;
				}
			
			bita_y = FIELD_HEIGHT - 5;
			bita_x = (FIELD_WIDTH - BITA_LENGTH)/2;
		}
			old_bita_x = bita_x;
			old_bita_y = bita_y;
	}

	private void draw_field_state()
	{
		hidden_graphics.setColor(background_color);
		hidden_graphics.fillRect(0,0,APPLET_WIDTH,APPLET_HEIGHT);    

		hidden_graphics.setColor(FIELD_COLOR);
		hidden_graphics.fillRect(FIELD_X_OFFSET,FIELD_Y_OFFSET,CELL_WIDTH*FIELD_WIDTH,CELL_HEIGHT*FIELD_HEIGHT); 

		hidden_graphics.setColor(Color.red); 
		
		if (FIELD_ORIENTATION)
		{
			// Horizontal
			hidden_graphics.fillRect(FIELD_X_OFFSET,FIELD_Y_OFFSET,CELL_WIDTH,(FIELD_HEIGHT-1)*CELL_HEIGHT);
			hidden_graphics.fillRect(FIELD_X_OFFSET,FIELD_Y_OFFSET,CELL_WIDTH*FIELD_WIDTH,CELL_WIDTH);
			hidden_graphics.fillRect(FIELD_X_OFFSET,FIELD_Y_OFFSET+CELL_HEIGHT*(FIELD_HEIGHT-1),CELL_WIDTH*FIELD_WIDTH,CELL_WIDTH);
		}
		else
		{
			// Vertical
			hidden_graphics.fillRect(FIELD_X_OFFSET,FIELD_Y_OFFSET,CELL_WIDTH,CELL_HEIGHT*(FIELD_HEIGHT-1));
			hidden_graphics.fillRect(FIELD_X_OFFSET,FIELD_Y_OFFSET+CELL_HEIGHT*(FIELD_HEIGHT-1),CELL_WIDTH*FIELD_WIDTH,CELL_WIDTH);
			hidden_graphics.fillRect(FIELD_X_OFFSET+CELL_WIDTH*(FIELD_WIDTH-1),FIELD_Y_OFFSET,CELL_WIDTH,CELL_HEIGHT*(FIELD_HEIGHT-1));
		}
		
		DrawFrame(hidden_graphics,0,0,hidden_image.getWidth(this)-INFO_PANEL_WIDTH,hidden_image.getHeight(this));     
		
	}
	
	public void init()
	{
		game_state = GAME_LOADING;

		String var_html = null;
		images_placement = getParameter("IMAGES");
		if (images_placement==null)
		{
			images_placement = "";
		}

		var_html = getParameter("ORIENTATION");
		if (var_html!=null)
		{
			var_html = var_html.trim();
			if (var_html.equalsIgnoreCase("h")) FIELD_ORIENTATION=true; else FIELD_ORIENTATION=false;  
		}
		else
		{
			FIELD_ORIENTATION = true;
		}
		
		var_html = getParameter("FIELDHEIGHT");
		if (var_html!=null)
		{
			try
			{
				FIELD_HEIGHT = Math.abs(new Integer(var_html).intValue()); 
				if ((FIELD_HEIGHT>44)||(FIELD_HEIGHT<10))FIELD_HEIGHT = 40; 
			}
			catch (NumberFormatException e)
			{
				FIELD_HEIGHT = 40;	
			}
		}
		else
		{
			FIELD_HEIGHT = 40;
		}
		
		var_html = getParameter("FIELDWIDTH");
		if (var_html!=null)
		{
			try
			{
				FIELD_WIDTH = Math.abs(new Integer(var_html).intValue()); 
				if ((FIELD_WIDTH>44)||(FIELD_WIDTH<10)) FIELD_WIDTH = 40; 
			}
			catch (NumberFormatException e)
			{
				FIELD_WIDTH = 40;	
			}
		}
		else
		{
			FIELD_WIDTH = 40;
		}
		
		var_html = getParameter("BALLS");
		if (var_html!=null)
		{
			try
			{
				ball_number = Math.abs(new Integer(var_html).intValue()); 
				if (ball_number>1000) ball_number=100;
			}
			catch (NumberFormatException e)
			{
				ball_number = 100;	
			}
		}
		else
		{
			ball_number = 100;
		}
		
		var_html = getParameter("BACKGROUND");
		if (var_html!=null)
		{
			try
			{
				background_color = stringToColor(var_html);
			}
			catch (Exception e)
			{
				background_color = stringToColor("0A0063"); 
			}
		}
		else
		{
			background_color = Color.black; 
		}
		
		
		APPLET_WIDTH = this.getSize().width;
		APPLET_HEIGHT = this.getSize().height;
		hidden_image = createImage(APPLET_WIDTH,APPLET_HEIGHT); 
		hidden_graphics = hidden_image.getGraphics(); 
		
		CELL_WIDTH = ((APPLET_WIDTH-x_offset*2)-INFO_PANEL_WIDTH)/ MAX_FIELD_WIDTH;
		CELL_HEIGHT = (APPLET_HEIGHT-y_offset*2)/ MAX_FIELD_HEIGHT;
		
		FIELD_X_OFFSET = CELL_WIDTH*((MAX_FIELD_WIDTH - FIELD_WIDTH)/2)+x_offset;
		FIELD_Y_OFFSET = CELL_HEIGHT*((MAX_FIELD_HEIGHT - FIELD_HEIGHT)/2)+y_offset;
		
		game_array = new int[FIELD_WIDTH][FIELD_HEIGHT];  
		game_state = GAME_START;
		
		hidden_graphics.setFont(button_font);
		button_width = hidden_graphics.getFontMetrics().stringWidth(button_text);
		button_height = hidden_graphics.getFontMetrics().getAscent();
		button_x = (INFO_PANEL_WIDTH-button_width)/2;
		
		Thread th = new Thread(this);
		th.start(); 
	}
	
	private boolean computate_ball_coord()
	{
		float lball_x = ball_x+ball_step_x;
		float lball_y = ball_y+ball_step_y;

		if (FIELD_ORIENTATION)
		{
			if (game_array[(int)(lball_x/CELL_WIDTH)][(int)(ball_y/CELL_HEIGHT)]==TORWART_WALL)
			{
				ball_step_x = 0-ball_step_x;
				lball_x = ball_x+ball_step_x;
			}
		
			if ((lball_y>0)&&(game_array[(int)(ball_x/CELL_WIDTH)][(int)(lball_y/CELL_HEIGHT)+1]==TORWART_WALL))
			{
				ball_step_y = 0-ball_step_y;
				lball_y = ball_y+ball_step_y;
			}
			else
			{
				if (game_array[(int)(ball_x/CELL_WIDTH)][(int)(lball_y/CELL_HEIGHT)]==TORWART_WALL)
				{
					ball_step_y = 0-ball_step_y;
					lball_y = ball_y+ball_step_y;
				}
			}
		}
		else
		{
			if (game_array[(int)(lball_x/CELL_WIDTH)][(int)(ball_y/CELL_HEIGHT)]==TORWART_WALL)
			{
				ball_step_x = 0-ball_step_x;
				lball_x = ball_x+ball_step_x;
			}
		
			if ((lball_x>0)&&(game_array[(int)(lball_x/CELL_WIDTH)+1][(int)(ball_y/CELL_HEIGHT)]==TORWART_WALL))
			{
				ball_step_x = 0-ball_step_x;
				lball_x = ball_x+ball_step_x;
			}
			else
			{
				if (game_array[(int)(lball_x/CELL_WIDTH)][(int)(ball_y/CELL_HEIGHT)]==TORWART_WALL)
				{
					ball_step_x = 0-ball_step_x;
					lball_x = ball_x+ball_step_x;
				}
			}
		}
		
		ball_x = lball_x;
		ball_y = lball_y; 
		
		if (game_array[(int)(ball_x/CELL_WIDTH)][(int)(ball_y/CELL_HEIGHT)]==TORWART_BITA)
		{
			ball_count++; 
			return true;
		}

		return false;
	}
	
	private void generate_new_ball()
	{
		if (FIELD_ORIENTATION)
		{
			// Горизонтальная игра	
			ball_x = (FIELD_WIDTH-1)*CELL_WIDTH;
			ball_y = (Math.round((rnd.nextDouble()*(FIELD_HEIGHT-3)+1)))*CELL_HEIGHT;
			ball_step_x = rnd.nextFloat()*(-6)-1;
			ball_step_y = rnd.nextFloat()*10-5;
		}
		else
		{
			// Вертикальная игра	
			ball_y = 0;
			ball_x = (rnd.nextFloat()*(FIELD_WIDTH-3)+1)*CELL_WIDTH;
			ball_step_x = rnd.nextFloat()*10-5;
			ball_step_y = rnd.nextFloat()*(6)+1;
		}
	}
	
	public void paint(Graphics g)
	{
		g.drawImage(hidden_image,0,0,this); 
	}
	
	private void draw_info_panel()
	{
		hidden_graphics.setColor(background_color);
		
		int stx = APPLET_WIDTH-INFO_PANEL_WIDTH;
		int sty = 5;
		hidden_graphics.fillRect(stx,0,INFO_PANEL_WIDTH,APPLET_HEIGHT);  
		hidden_graphics.setColor(Color.white);
		hidden_graphics.setFont(outfont);

		// Кнопка "SCORES"
		hidden_graphics.drawImage(SCORES_IMAGE,stx+(INFO_PANEL_WIDTH-SCORES_IMAGE.getWidth(this))/2,sty,this);
		String lballs = Integer.toString(ball_count);
		hidden_graphics.drawString(lballs,(INFO_PANEL_WIDTH-hidden_graphics.getFontMetrics().stringWidth(lballs))/2+stx,(50-hidden_graphics.getFontMetrics().getHeight())/2+30+sty);  

		sty += 70;
		// Кнопка "BALLS"
		hidden_graphics.drawImage(BALLS_IMAGE,stx+(INFO_PANEL_WIDTH-BALLS_IMAGE.getWidth(this))/2,sty,this);
		// Количество оставшихся мячей
		lballs = Integer.toString(ball_counter);
		hidden_graphics.drawString(lballs,(INFO_PANEL_WIDTH-hidden_graphics.getFontMetrics().stringWidth(lballs))/2+stx,(50-hidden_graphics.getFontMetrics().getHeight())/2+30+sty);  
		
		sty += 70;
		// Кнопка "STOP"
		hidden_graphics.drawImage(STOP_IMAGE,stx+(INFO_PANEL_WIDTH-BALLS_IMAGE.getWidth(this))/2,sty,this);
		
	}
	
	private void draw_start_page()
	{
		hidden_graphics.setColor(background_color); 
		hidden_graphics.fillRect(0,0,APPLET_WIDTH,APPLET_HEIGHT);
		DrawFrame(hidden_graphics,0,0,hidden_image.getWidth(this),hidden_image.getHeight(this));
		hidden_graphics.setColor(Color.orange); 
		hidden_graphics.setFont(new Font("Arial",Font.BOLD,75));
		int ly = (APPLET_HEIGHT-hidden_graphics.getFontMetrics().getHeight())/2;
		hidden_graphics.drawString("TORWART",(APPLET_WIDTH-hidden_graphics.getFontMetrics().stringWidth("TORWART"))/2,ly);
		hidden_graphics.setFont(new Font("Arial",Font.BOLD,14));
		hidden_graphics.setColor(Color.cyan);
		ly += (hidden_graphics.getFontMetrics().getHeight()*2);
		hidden_graphics.drawString("PRESS ANY KEY FOR START",(APPLET_WIDTH-hidden_graphics.getFontMetrics().stringWidth("PRESS ANY KEY FOR START"))/2,ly);
		
	}
	
	private void draw_stop_page()
	{
		hidden_graphics.setColor(background_color); 
		hidden_graphics.fillRect(0,0,APPLET_WIDTH,APPLET_HEIGHT);
		DrawFrame(hidden_graphics,0,0,hidden_image.getWidth(this),hidden_image.getHeight(this));

		hidden_graphics.setColor(Color.yellow); 
		hidden_graphics.setFont(new Font("Arial",Font.BOLD,20));
		int ly = (APPLET_HEIGHT-hidden_graphics.getFontMetrics().getHeight())/2;
		// Выводим оющее количество мячей и количество пойманных мячей
		hidden_graphics.setColor(Color.orange); 
		hidden_graphics.drawString("GAME OVER",(APPLET_WIDTH-hidden_graphics.getFontMetrics().stringWidth("GAME OVER"))/2,ly);
		hidden_graphics.setColor(Color.cyan); 
		ly += (hidden_graphics.getFontMetrics().getHeight()*2);
		String lstr = "CAUGHT "+Integer.toString(ball_count)+" BALLS from "+Integer.toString(ball_number);
		hidden_graphics.drawString(lstr,(APPLET_WIDTH-hidden_graphics.getFontMetrics().stringWidth(lstr))/2,ly);
		
		ly += (hidden_graphics.getFontMetrics().getHeight());
		
		hidden_graphics.setFont(new Font("Arial",Font.PLAIN,12));
		if (this.SendData(Integer.toString(ball_count)))
		{
			hidden_graphics.setColor(Color.white);
			lstr = "The game data are saved";
		}
		else
		{
			hidden_graphics.setColor(Color.red);
			lstr = "I can not save the game data!!!";
		}
		hidden_graphics.drawString(lstr,x_offset+(APPLET_WIDTH-hidden_graphics.getFontMetrics().stringWidth(lstr))/2,y_offset+ly);

		
	}
	
	private void draw_load_page()
	{
		hidden_graphics.setColor(Color.blue);
		hidden_graphics.setFont(new Font("Arial",Font.BOLD,12));
		hidden_graphics.fillRect(0,0,hidden_image.getWidth(this),hidden_image.getHeight(this));
		hidden_graphics.setColor(Color.yellow);
		hidden_graphics.drawString("Loading Images. Please Wait...",5,hidden_graphics.getFontMetrics().getHeight()+5);   
	}

	private void draw_errorload_page()
	{
		hidden_graphics.setColor(Color.black);
		hidden_graphics.setFont(new Font("Arial",Font.BOLD,12));
		hidden_graphics.fillRect(0,0,hidden_image.getWidth(this),hidden_image.getHeight(this));
		hidden_graphics.setColor(Color.red);
		hidden_graphics.drawString("Error of load the image \""+error_resource+"\"" ,5,hidden_graphics.getFontMetrics().getHeight()+5);   
	}
	
	public void update(Graphics g)
	{
		switch (game_state)
		{
			case GAME_LOADING :	draw_load_page(); break;
			case GAME_ERRORLOAD :	draw_errorload_page(); break;
			case GAME_START : draw_start_page(); break;
//			case GAME_RUN   : draw_info_panel(); break;
			case GAME_STOP  : draw_stop_page(); break;
		}
		paint(g);
	}

	private boolean ball_is_end()
	{
		if (FIELD_ORIENTATION)
		{
			// Для горизонтальной
			if ((int)(ball_x/CELL_WIDTH)<2)	return true;
		}
		else
		{
			// Для вертикальной
			if (((int)(ball_y/CELL_HEIGHT))>(FIELD_HEIGHT-4)) return true;
		}
		return false;
	}

	public void run()
	{
		repaint();
		try
		{
			FULLFRAME_IMAGE = this.getImage(new URL(getCodeBase(),images_placement+"fullframe.gif"));
			SCORES_IMAGE = this.getImage(new URL(getCodeBase(),images_placement+"scores.gif"));
			BALLS_IMAGE = this.getImage(new URL(getCodeBase(),images_placement+"balls.gif"));
			STOP_IMAGE = this.getImage(new URL(getCodeBase(),images_placement+"stop.gif"));
		}
		catch (Exception ee) { return; }
		
		mtr.addImage(FULLFRAME_IMAGE ,0);
		mtr.addImage(SCORES_IMAGE ,1);
		mtr.addImage(BALLS_IMAGE ,2);
		mtr.addImage(STOP_IMAGE ,3);
		
		error_resource = images_placement; 
		try
		{
			mtr.waitForID(0);
			if (mtr.isErrorAny()) 
			{
				error_resource += "fullframe.gif";
				throw new InterruptedException(); 
			}
			mtr.waitForID(1);
			if (mtr.isErrorAny()) 
			{
				error_resource += "scores.gif";
				throw new InterruptedException(); 
			}
			mtr.waitForID(2);
			if (mtr.isErrorAny()) 
			{
				error_resource += "balls.gif";
				throw new InterruptedException(); 
			}
			mtr.waitForID(3);
			if (mtr.isErrorAny()) 
			{
				error_resource += "stop.gif";
				throw new InterruptedException(); 
			}
		}
		catch (InterruptedException e)
		{ 
			game_state=GAME_ERRORLOAD; 
			repaint(); 
			return;
		}
		
		
		// Нарезка бордюра
		int imageCount=0;
		int s1=0;
		int framesize[]={17,14,17};
        for(int i = 0; i < 3; i++)
        {
             int s2=0;
             for (int j = 0; j<3; j++)           
             {
                 frames[i*3+j] = CropImage(FULLFRAME_IMAGE,s1,s2,framesize[i],framesize[j]);
                 imageCount++;
                 s2+=framesize[j];
             }
             s1+=framesize[i];
         }
		
		
		this.requestFocus(); 
		while(true)
		{
			game_state = GAME_START;
			repaint();
			while(game_state==GAME_START) 
			{	
				try
				{
					Thread.sleep(300);
				}
				catch(InterruptedException e){ return; }
			}
		
			hidden_graphics.setFont(panel_font);
			button_y = 40+hidden_graphics.getFontMetrics().getHeight()*6;
			init_game_field();
			draw_field_state();
			repaint();
			
			boolean flag_first_ball=true;
			ball_counter = ball_number; 
			ball_count = 0; // Количество пойманных мячей

			while((ball_counter>0)&&(game_state==GAME_RUN))
			{
				if (!flag_first_ball) erase_ball();
				generate_new_ball();			
				ball_counter--;
				draw_info_panel();
				
				flag_first_ball = false;
				while((!ball_is_end())&&(game_state==GAME_RUN))
				{
					erase_ball();
					if (computate_ball_coord()) 
					{	
						draw_ball();
						erase_bita();
						compute_bita_coord(); 
						draw_bita();
						repaint();
						break;
					}
					draw_ball();
					erase_bita();
					compute_bita_coord(); 
					draw_bita();
					repaint();
					try
					{
						Thread.sleep(30); 
					}
					catch(InterruptedException e){ return; }
				}
			}
			game_state = GAME_STOP;
			repaint();
			while (game_state==GAME_STOP) 
			{	
				try
				{
					Thread.sleep(300);  
				}
				catch(InterruptedException e) {}
			}
		}
	}

	private void draw_bita()
	{
		old_bita_x = bita_x;
		old_bita_y = bita_y; 

		if (FIELD_ORIENTATION)
		{
			// Horizon
			hidden_graphics.setColor(BITA_COLOR);
			hidden_graphics.fill3DRect(FIELD_X_OFFSET+bita_x*CELL_WIDTH,FIELD_Y_OFFSET+bita_y*CELL_HEIGHT,CELL_WIDTH,CELL_HEIGHT*BITA_LENGTH,true);
			for (int ly=(bita_y-1);ly<(bita_y+BITA_LENGTH);ly++) 
				if ((ly>0)&&(ly<(FIELD_HEIGHT-1))) game_array[bita_x][ly]=TORWART_BITA;  
		}
		else
		{
			// Vert
			hidden_graphics.setColor(BITA_COLOR);
			hidden_graphics.fill3DRect(FIELD_X_OFFSET+bita_x*CELL_WIDTH,FIELD_Y_OFFSET+bita_y*CELL_HEIGHT,BITA_LENGTH*CELL_WIDTH,CELL_HEIGHT,true);
			for (int lx=(bita_x-1);lx<(bita_x+BITA_LENGTH);lx++) 
				if ((lx>0)&&(lx<(FIELD_WIDTH-1))) game_array[lx][bita_y]=TORWART_BITA;  
		}
	}
	
	private void erase_bita()
	{
		if (FIELD_ORIENTATION)
		{
			hidden_graphics.setColor(FIELD_COLOR);
			hidden_graphics.fillRect(FIELD_X_OFFSET+old_bita_x*CELL_WIDTH,FIELD_Y_OFFSET+old_bita_y*CELL_HEIGHT,CELL_WIDTH,BITA_LENGTH*CELL_HEIGHT);
			for (int ly=(old_bita_y-1);ly<(old_bita_y+BITA_LENGTH);ly++) 
				if ((ly>0)&&(ly<(FIELD_HEIGHT-1))) game_array[old_bita_x][ly]=TORWART_FIELD;  
		}
		else
		{
			hidden_graphics.setColor(FIELD_COLOR);
			hidden_graphics.fillRect(FIELD_X_OFFSET+old_bita_x*CELL_WIDTH,FIELD_Y_OFFSET+old_bita_y*CELL_HEIGHT,BITA_LENGTH*CELL_WIDTH,CELL_WIDTH);
			for (int lx=(old_bita_x-1);lx<(old_bita_x+BITA_LENGTH);lx++) 
				if ((lx>0)&&(lx<(FIELD_WIDTH-1))) game_array[lx][old_bita_y]=TORWART_FIELD;  
		}
	}
	
	private void erase_ball()
	{
		hidden_graphics.setColor(FIELD_COLOR);  
		hidden_graphics.fillRect(FIELD_X_OFFSET+((int)ball_x),FIELD_Y_OFFSET+((int)ball_y),CELL_WIDTH,CELL_HEIGHT);
	}

	private void draw_ball()
	{
		hidden_graphics.setColor(BALL_COLOR);
		hidden_graphics.drawImage(BALL_IMAGE,FIELD_X_OFFSET+((int)ball_x),FIELD_Y_OFFSET+((int)ball_y),CELL_WIDTH,CELL_HEIGHT,this);
	}
	
	public boolean keyUp(Event e,int key)
	{
		KEY_DIRECT = 0;
		return true;
	}
	
	public boolean keyDown( Event e, int key ) 
	{
		if (game_state == GAME_STOP)
		{
			game_state = GAME_START;
			return true;
		}
		
		if (game_state == GAME_RUN)
		{
			if (key==e.ESCAPE)
			{
				game_state = GAME_STOP;
				return true;	
			}
		}
		
		if (game_state== GAME_START)
		{
			game_state = GAME_RUN;
			return true;
		}
		
		if (FIELD_ORIENTATION)
		{
			// Horizontal
			switch (key)
			{
				case Event.UP : KEY_DIRECT = Event.UP;  break;
				case Event.DOWN: KEY_DIRECT = Event.DOWN;  break;							  
				default : KEY_DIRECT = 0;
			}
		}
		else
		{
			// Vertical
			switch (key)
			{
				case Event.LEFT : KEY_DIRECT = Event.LEFT;  break;
				case Event.RIGHT   : KEY_DIRECT = Event.RIGHT;  break;							  
				default : KEY_DIRECT = 0;
			}
		}
		return true;
	}

	private void compute_bita_coord()
	{
		if (FIELD_ORIENTATION)
		{
			//	Horizontal
			if (KEY_DIRECT!=0)
			{
				switch (KEY_DIRECT)
				{
					case Event.UP : if (bita_y>1) bita_y--; break;
					case Event.DOWN : if ((bita_y+BITA_LENGTH+1)<FIELD_HEIGHT) bita_y++; break;									
				}
				mouse_y = bita_y;
				mouse_x = bita_x;
			}
			else
			{
				if (mouse_y==bita_y) return;
				if (mouse_y<bita_y) 
				{
					if (bita_y>1) bita_y--;
				}
				else
				{
					if ((bita_y+BITA_LENGTH+1)<FIELD_HEIGHT) bita_y++;
				}
			}
		}
		else
		{
			//  Vertical	
			if (KEY_DIRECT!=0)
			{
				switch (KEY_DIRECT)
				{
					case Event.LEFT : if (bita_x>1) bita_x--; break;
					case Event.RIGHT : if ((bita_x+BITA_LENGTH+1)<FIELD_WIDTH) bita_x++; break;									
				}
				mouse_y = bita_y;
				mouse_x = bita_x;
			}
			else
			{
				if (mouse_x==bita_x) return ;
				if (mouse_x<bita_x) 
				{
					if (bita_x>1) bita_x--;
				}
				else
				{
					if ((bita_x+BITA_LENGTH+1)<FIELD_WIDTH) bita_x++;
				}
			}
		}
	}
	
	public boolean mouseDown(Event e,int x,int y)
	{
		if (game_state == GAME_STOP)
		{
			game_state = GAME_START;
			return true;
		}
		
		if (game_state== GAME_START)
		{
			game_state = GAME_RUN;
			return true;
		}
		
		if (game_state == GAME_RUN)
		{
			int b_off = APPLET_WIDTH-INFO_PANEL_WIDTH+(INFO_PANEL_WIDTH-STOP_IMAGE.getWidth(this))/2;
			int lx = x - b_off;
			int ly = y;

			if ((lx>=0)&&(lx<=STOP_IMAGE.getWidth(this))&&(ly>=145)&&(ly<=(145+STOP_IMAGE.getHeight(this))))
			{
				paint(this.getGraphics()); 
				game_state = GAME_STOP;
			}
		}
		return true;
	}
	
	public boolean mouseMove(Event e, int x, int y )
	{
		mouse_x = (x-FIELD_X_OFFSET) / CELL_WIDTH;
		mouse_y = (y-FIELD_Y_OFFSET) / CELL_HEIGHT;
		
		return true;
	}

	boolean SendData(String Score)
	{
	try
	 {
	  String username = getParameter("USER");
	  URL uhost = getCodeBase();
	  String host = uhost.getProtocol()+"://"+uhost.getHost();
	  if(uhost.getPort()!=-1) host+=":"+uhost.getPort();
          URL u = new URL(host+"/"+getParameter("DATABASE")+"/SetScore?OpenAgent=&GameType="+getParameter("GAMETYPE")+"&User="+username+"&Score="+Score);
          InputStream is = u.openStream();
          BufferedReader di = new BufferedReader(new InputStreamReader(is));
          String str="";
          int t;
          while((t=di.read())!=-1)
          {
           str += ""+String.valueOf((char)t);
           if(str.indexOf("$$ERROR$$")!=-1){System.out.println("Error");return false;}
          }
          System.out.println(str);
          di.close();
         }catch (Exception e){e.printStackTrace();return false;}
         return true;
	}

	private Color stringToColor(String paramValue)
	{
		int red;
		int green;
		int blue;

		red = (Integer.decode("0x" + paramValue.substring(0,2))).intValue();
		green = (Integer.decode("0x" + paramValue.substring(2,4))).intValue();
		blue = (Integer.decode("0x" + paramValue.substring(4,6))).intValue();

		return new Color(red,green,blue);
	}
    private Image CropImage(Image image, int x, int y, int w, int h)
    {
       Image img=null; 
       try
       {
         CropImageFilter cropimagefilter = new CropImageFilter(x,y,w,h);
		 img = this.createImage(new FilteredImageSource(image.getSource(), cropimagefilter));
		 mtr.addImage(img,0);
		 mtr.waitForID(0);
		 cropimagefilter = null;
	   }
       catch(Exception _ex)
       {
          _ex.printStackTrace();
          System.out.println("Exception occured: "+_ex);
       }
      
       return img;
    }
	
   private final void DrawFrame(Graphics g,int x,int y,int w,int h)
    {
	    g.drawImage(frames[3],x+17,y,(w-34>0)?(w-34):0,17,this);     //up
        g.drawImage(frames[5],x+17,y+h-17,(w-34>0)?(w-34):0,17,this);//down
        g.drawImage(frames[1],x,y+17,17,(h-34>0)?(h-34):0,this);     //left
        g.drawImage(frames[7],x+w-17,y+17,17,(h-34>0)?(h-34):0,this);  //right
        g.drawImage(frames[0],x,y,this);            //ul
        g.drawImage(frames[6],x+w-17,y,this);       //ur
        g.drawImage(frames[2],x,y+h-17,this);       //dl
        g.drawImage(frames[8],x+w-17,y+h-17,this);  //dr
    }

	
}
