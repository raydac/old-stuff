import java.applet.*;
import java.awt.*; 
import java.awt.image.*; 
import java.awt.event.*; 
import java.io.*;
import java.net.*;

public class tetris extends Applet implements Runnable 
{
	private Image [] frames = new Image[9];
	private Image FULLFRAME_IMAGE = null;
	private Image SCORES_IMAGE = null;
	private Image TIME_IMAGE = null;
	private Image STOP_IMAGE = null;
	
	private String error_resource=null;
	
	private Color background_color = null;
	private String images_placement="";
	private Image panel_image = null;
	private Graphics panel_graphics=null;
	private Font panel_font = new Font("Arial",Font.BOLD,12);
	private int panelimage_x,panelimage_y;
	
	private final int GAME_FIELD_WIDTH = 50;
	private final int GAME_FIELD_HEIGHT = 40;
	private final int GLASS_WIDTH = 15;
	private final int GLASS_HEIGHT = 35;

	private static final int x_offset_glb =5;
	private static final int y_offset_glb =5;
	
	private final int SCORE_BORDER = 35;
	private final int GLASS_X = (GAME_FIELD_WIDTH-(GAME_FIELD_WIDTH-SCORE_BORDER)-GLASS_WIDTH)/2;
	private final int GLASS_BOTTOM = GAME_FIELD_HEIGHT-3;

	private final int figure_start_x = 39;
	private final int figure_start_y = 6;
	
	private int APPLET_WIDTH = 500;
	private int APPLET_HEIGHT = 400;
	
	private int CELL_WIDTH = 0;
	private int CELL_HEIGHT = 0;

	private static final int C_W = 0; // White
	private static final int C_R = 1; // Red
	private static final int C_G = 2; // Green
	private static final int C_B = 3; // Blue
	private static final int C_C = 4; // Cyan
	private static final int C_Y = 5; // Yellow
	private static final int C_O = 6; // Orange
	private static final int C_J = 7; // Gray
	
	private static final int TETRIS_EMPTY=0;	    // Пустое пространство
	private static final int TETRIS_BLOCK=1;		// Блок
	private static final int TETRIS_GLASSWALL=2;		// Стенка стакана
	private static final int TETRIS_GLASSINSIDE=3;	// Пространство внутри стакана
	private static final int TETRIS_STARTPLACE=4;	// Стартовая площадка
	private static final int TETRIS_BORDER=5;		// Бордюр
	
	private boolean figure_drag = false; // флаг захвата фигуры
	
	private int block_x = 0; // Координаты фигуры
	private int block_y = 0;
	private int old_block_x = 0; // Старые координаты фигуры
	private int old_block_y = 0;

	private int [][] game_array = null;
	private Image hidden_image = null;
	private Graphics hidden_graphics = null;	

	private int figure_x_offset = 0;
	private int figure_y_offset = 0;
	private int figure_width = 0;
	private int figure_height = 0;
	private boolean stop_button = false;
	private MediaTracker mtr = new MediaTracker(this);
	
	private int game_mode = 0;
	private static final int GAME_LOAD = 4;
	private static final int GAME_ERRORLOAD = 5;
	private static final int GAME_FIRSTPAGE = 0;
	private static final int GAME_RUN = 1;
	private static final int GAME_STOPPAGE = 2;
	private static final int GAME_FINALPAGE = 3;

	private long start_time = 0;
	private long end_time = 0;
	private int game_scores = 0;

	private int text_xoffset_SCORES = 0;
	private int text_xoffset_PLAYTIME = 0;
	private int text_xoffset_SCORESNUM = 0;
	private int text_xoffset_PLAYTIMENUM = 0;
	private int text_xoffset_STOPGAME = 0;
	private int text_yoffset_STOPGAME = 0;
	
	private String []start_screen={ "WWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWW",
									"W                                                W",
									"W                                                W",
									"W                                                W",
									"W                                                W",
									"W  TTTTTTT EEEEEEE TTTTTTT RRRRR    I    SSSSS   W",
									"W     T    E          T    R    R       S     S  W",
									"W     T    E          T    R    R IIIII S        W",
									"W     T    EEEEEE     T    RRRRR    I    SSSSS   W",
									"W     T    E          T    R  R     I         S  W",
									"W     T    E          T    R   R    I   S     S  W",
									"W     T    EEEEEEE    T    R    R IIIII  SSSSS   W",
									"W                                                W",
									"W                                                W",
									"W                                                W",
									"W                                                W",
									"W                                                W",
									"W                                                W",
									"W                                                W",
									"W                                                W",
									"W                                                W",
									"W                                                W",
									"W                                                W",
									"W                                                W",
									"W                                                W",
									"W                                                W",
									"W                                                W",
									"W                                                W",
									"W                                                W",
									"W                                                W",
									"W                                                W",
									"W                                                W",
									"W                                                W",
									"W                                                W",
									"W                                                W",
									"W                                                W",
									"W                                                W",
									"W                                                W",
									"W                                                W",
									"WWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWW"
								   };

	
	private String []final_screen= {"WWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWW",
									"W                                                W",
									"W                                                W",
									"W                                                W",
									"W                                                W",
									"W                                                W",
									"W                                                W",
									"W                                                W",
									"W                                                W",
									"W       GGGGGG   AAA   M     M EEEEE             W",
									"W      G        A   A  MM   MM E                 W",
									"W      G       A     A M M M M EEEE              W",
									"W      G   GGG AAAAAAA M  M  M E                 W",
									"W      G     G A     A M     M E                 W",
									"W       GGGGGG A     A M     M EEEEE             W",
									"W                                                W",
									"W               OOOOO  V     V EEEEE RRRRR       W",
									"W              O     O V     V E     R    R      W",
									"W              O     O V     V EEEE  R    R      W",
									"W              O     O V     V E     RRRRR       W",
									"W              O     O  V   V  E     R  R        W",
									"W               OOOOO    VVV   EEEEE R   RR      W",
									"W                                                W",
									"W                                                W",
									"W                                                W",
									"W                                                W",
									"W                                                W",
									"W                                                W",
									"W                                                W",
									"W                                                W",
									"W                                                W",
									"W                                                W",
									"W                                                W",
									"W                                                W",
									"W                                                W",
									"W                                                W",
									"W                                                W",
									"W                                                W",
									"W                                                W",
									"WWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWW"
								   };
	
	
//  Матрицы фигур
	
	private final int [] [] figure_1 = {
										{ -1, -1, -1, -1, -1, -1},
										{ -1, -1, -1, -1, -1, -1},
										{ -1, -1,C_B,C_B, -1, -1},
										{ -1, -1, -1,C_B, -1, -1},
										{ -1, -1, -1, -1, -1, -1},
										{ -1, -1, -1, -1, -1, -1},
										};
	
	private final int [] [] figure_2 = {
										{ -1, -1, -1, -1, -1, -1},
										{ -1, -1, -1, -1, -1, -1},
										{ -1, -1,C_C,C_C, -1, -1},
										{ -1, -1,C_C, -1, -1, -1},
										{ -1, -1, -1, -1, -1, -1},
										{ -1, -1, -1, -1, -1, -1},
										};
	
	private final int [] [] figure_3 = {
										{ -1, -1, -1, -1, -1, -1},
										{ -1, -1, -1, -1, -1, -1},
										{ -1, -1, -1,C_G, -1, -1},
										{ -1, -1,C_G,C_G, -1, -1},
										{ -1, -1, -1, -1, -1, -1},
										{ -1, -1, -1, -1, -1, -1},
										};

	private final int [] [] figure_4 = {
										{ -1, -1, -1, -1, -1, -1},
										{ -1, -1, -1, -1, -1, -1},
										{ -1, -1, -1,C_Y,C_Y, -1},
										{ -1, -1,C_Y,C_Y, -1, -1},
										{ -1, -1, -1, -1, -1, -1},
										{ -1, -1, -1, -1, -1, -1},
										};
	
	private final int [] [] figure_5 = {
										{ -1, -1, -1, -1, -1, -1},
										{ -1, -1, -1, -1, -1, -1},
										{ -1, -1,C_W,C_W, -1, -1},
										{ -1, -1, -1,C_W,C_W, -1},
										{ -1, -1, -1, -1, -1, -1},
										{ -1, -1, -1, -1, -1, -1},
										};

	private final int [] [] figure_6 = {
										{ -1, -1, -1, -1, -1, -1},
										{ -1, -1, -1, -1, -1, -1},
										{ -1, -1, -1,C_O, -1, -1},
										{ -1, -1, -1,C_O, -1, -1},
										{ -1, -1, -1, -1, -1, -1},
										{ -1, -1, -1, -1, -1, -1},
										};
	
	private final int [] [] figure_7 = {
										{ -1, -1, -1, -1, -1, -1},
										{ -1, -1, -1,C_G, -1, -1},
										{ -1, -1, -1,C_G, -1, -1},
										{ -1, -1, -1,C_G, -1, -1},
										{ -1, -1, -1, -1, -1, -1},
										{ -1, -1, -1, -1, -1, -1},
										};
	
	private final int [] [] figure_8 = {
										{ -1, -1, -1, -1, -1, -1},
										{ -1, -1, -1, -1, -1, -1},
										{ -1, -1,C_W,C_W, -1, -1},
										{ -1, -1, -1, -1, -1, -1},
										{ -1, -1, -1, -1, -1, -1},
										{ -1, -1, -1, -1, -1, -1},
										};

	private final int [] [] figure_9 = {
										{ -1, -1, -1, -1, -1, -1},
										{ -1, -1, -1, -1, -1, -1},
										{ -1, -1,C_Y,C_Y,C_Y, -1},
										{ -1, -1, -1, -1, -1, -1},
										{ -1, -1, -1, -1, -1, -1},
										{ -1, -1, -1, -1, -1, -1},
										};

	private final int [] [] figure_10 = {
										{ -1, -1, -1, -1, -1, -1},
										{ -1, -1, -1, -1, -1, -1},
										{ -1, -1,C_R, -1, -1, -1},
										{ -1, -1,C_R,C_R, -1, -1},
										{ -1, -1, -1, -1, -1, -1},
										{ -1, -1, -1, -1, -1, -1},
										};

	private final int [] [] figure_11 = {
										{ -1, -1, -1, -1, -1, -1},
										{ -1, -1,C_O, -1, -1, -1},
										{ -1, -1,C_O,C_O, -1, -1},
										{ -1, -1, -1,C_O, -1, -1},
										{ -1, -1, -1, -1, -1, -1},
										{ -1, -1, -1, -1, -1, -1},
										};

	private final int [] [] figure_12 = {
										{ -1, -1, -1, -1, -1, -1},
										{ -1, -1, -1,C_G, -1, -1},
										{ -1, -1,C_G,C_G, -1, -1},
										{ -1, -1,C_G, -1, -1, -1},
										{ -1, -1, -1, -1, -1, -1},
										{ -1, -1, -1, -1, -1, -1},
										};

	private final Object [] figure_array = {figure_1,figure_2,figure_3,figure_4,figure_5,
											figure_6,figure_7,figure_8,figure_9,figure_10,
											figure_11,figure_12};
	
	private int [][] buffer_array = new int [6][6];// Массив для хранения содержимого игрового массива под перемещаемой фигурой

	private int [][] current_figure = null; // Указатель на массив с матрицей текущей фигуры
	
	private Color decode_color(int color)
	{
		switch (color)
		{
			case C_W : return Color.white; 
			case C_R : return Color.red;
			case C_G : return Color.green;
			case C_B : return Color.blue;
			case C_C : return Color.cyan;
			case C_Y : return Color.yellow;
		    case C_O : return Color.orange;
			case C_J : return Color.gray;
			default : return Color.black;
		}
	}
	
	private int get_cell_color(int x,int y)
	{
		return (game_array[x][y]>>8); 	
	}

	private int get_cell_element(int x,int y)
	{
		return (game_array[x][y]&0xFF);	
	}
	
	private void set_cell (int x,int y,int element,int color)
	{
		game_array[x][y] = element | (color << 8);
		Color cl = decode_color(color);
		
		switch (element)
		{
			case TETRIS_BLOCK : drawCell(x,y,cl); break;
			case TETRIS_EMPTY : drawEmpty(x,y); break;
			case TETRIS_GLASSINSIDE : drawGlassInside(x,y,cl); break;
			case TETRIS_GLASSWALL : drawGlassWall(x,y,cl);break; 
			case TETRIS_STARTPLACE : drawGlassInside(x,y,cl); break;
			case TETRIS_BORDER : drawCell(x,y,cl); break;
		}
		
	}

	private void draw_figure_ground_area()
	{
		for(int lx=0;lx<6;lx++)
			for(int ly=0;ly<6;ly++)
			{
				set_cell(figure_start_x+lx,figure_start_y+ly,TETRIS_STARTPLACE,C_J);
			}
	}
	
	private void draw_figure (int x,int y,boolean repair)
	{
		if (repair) set_array_from_buffer(old_block_x,old_block_y);   
		get_array_to_buffer(x,y);
		for (int ly=0;ly<6;ly++)
			for (int lx=0;lx<6;lx++)
			{
				if (current_figure[ly][lx]>=0)
				{
					if (((x+lx)>=GAME_FIELD_WIDTH)||((y+ly)>=GAME_FIELD_HEIGHT)) continue;
					if (get_cell_element(x+lx,y+ly)!=TETRIS_BORDER)	set_cell(x+lx,y+ly,TETRIS_BLOCK,current_figure[ly][lx]);
				}
			}
		old_block_x = x;
		old_block_y = y;
		
		// Обрисовываем стартовое место
		hidden_graphics.setColor(Color.gray);
		hidden_graphics.drawRect(figure_start_x*CELL_WIDTH+x_offset_glb,figure_start_y*CELL_HEIGHT+y_offset_glb,6*CELL_WIDTH,6*CELL_HEIGHT);
	}
	
	private void drawGlassWall(int x,int y,Color cl)
	{
		x = CELL_WIDTH*x+x_offset_glb;
		y = CELL_HEIGHT*y+y_offset_glb;
		hidden_graphics.setColor(cl);  
		hidden_graphics.fillRect(x,y,CELL_WIDTH,CELL_HEIGHT);    
	}

	private void drawGlassInside(int x,int y,Color cl)
	{
		x = CELL_WIDTH*x+x_offset_glb;
		y = CELL_HEIGHT*y+y_offset_glb;
		hidden_graphics.setColor(background_color); 
		hidden_graphics.fillRect(x,y,CELL_WIDTH,CELL_HEIGHT);    
		hidden_graphics.setColor(cl);
		hidden_graphics.drawRect(x,y,CELL_WIDTH,CELL_HEIGHT);    
	}
	
	private void drawEmpty(int x,int y)
	{
		x = CELL_WIDTH*x+x_offset_glb;
		y = CELL_HEIGHT*y+y_offset_glb;
		hidden_graphics.setColor(background_color); 
		hidden_graphics.fillRect(x,y,CELL_WIDTH,CELL_HEIGHT);    
	}
	
	private void drawCell(int x,int y,Color c)
	{
		x = CELL_WIDTH*x+x_offset_glb;
		y = CELL_HEIGHT*y+y_offset_glb;
		hidden_graphics.setColor(c); 
		hidden_graphics.fill3DRect(x,y,CELL_WIDTH,CELL_HEIGHT,true);    
	}
	
	private void clearviewgamefield(Color cl)
	{
		hidden_graphics.setColor(cl); 
		hidden_graphics.fillRect(0,0,APPLET_WIDTH+x_offset_glb*2,APPLET_HEIGHT+y_offset_glb*2);    	
	}
	
	private void update_panel()
	{
		panel_graphics.setColor(background_color);  
		panel_graphics.setFont(panel_font); 
		panel_graphics.fillRect(0,0,panel_image.getWidth(this),panel_image.getHeight(this));   
		panel_graphics.setColor(Color.white);  		
		
		// Кнопка SCORES
		panel_graphics.drawImage(SCORES_IMAGE,(panel_image.getWidth(this)-SCORES_IMAGE.getWidth(this))/2,0,this);   

		// Количество очков
		String ls = Integer.toString(game_scores); 
		ls = ((String)("000000")).substring(1,7-ls.length())+ls;  
		panel_graphics.drawString(ls,(panel_image.getWidth(this)-panel_graphics.getFontMetrics().stringWidth(ls))/2,60-(40-panel_graphics.getFontMetrics().getHeight())/2);
		
		// Кнопка TIME
		panel_graphics.drawImage(TIME_IMAGE,(panel_image.getWidth(this)-TIME_IMAGE.getWidth(this))/2,60,this);   

		// Время игры
		long inter = System.currentTimeMillis()-start_time;  
			
		java.util.Date ld = new java.util.Date(inter);
		String mn = Integer.toString(ld.getMinutes());
		String sc = Integer.toString(ld.getSeconds());
			
		if (mn.length()==1) mn="0"+mn;
		if (sc.length()==1) sc="0"+sc;
			
		ls = mn+":"+sc;
		panel_graphics.drawString(ls,(panel_image.getWidth(this)-panel_graphics.getFontMetrics().stringWidth(ls))/2,120-(40-panel_graphics.getFontMetrics().getHeight())/2);
		
		// Кнопка STOP
		panel_graphics.drawImage(STOP_IMAGE,(panel_image.getWidth(this)-STOP_IMAGE.getWidth(this))/2,120,this);   
		
		hidden_graphics.drawImage(panel_image,panelimage_x,panelimage_y,this); 
		
	}
	
	private void full_repaint()
	{
		clearviewgamefield(background_color);
		for (int lx=0;lx<GAME_FIELD_WIDTH;lx++)
			for(int ly=0;ly<GAME_FIELD_HEIGHT;ly++)
			{
				int le = get_cell_element(lx,ly);
				int lc = get_cell_color(lx,ly);
				set_cell(lx,ly,le,lc);
			}
			DrawFrame(hidden_graphics,0,0,hidden_image.getWidth(this),hidden_image.getHeight(this));
			repaint();
	}
	
	private synchronized void update_hidden()
	{
		// Отрисовка кнопки STOP GAME и времени игры если начата игра
		if (game_mode==GAME_RUN)
		{
			draw_figure(block_x,block_y,true);
			update_panel();  
			if (stop_button) game_mode=GAME_FINALPAGE;
		}
	}

	// Захват содержимого игрового массива в буффер
	private synchronized void get_array_to_buffer(int x,int y)
	{
		for (int lx=0;lx<6;lx++)
			for(int ly=0;ly<6;ly++)
			{
				if (((x+lx)>=1)&&((x+lx)<(GAME_FIELD_WIDTH-1)))
				{
					if (((y+ly)>=1)&&((y+ly)<(GAME_FIELD_HEIGHT-1)))
					{
						if (current_figure[ly][lx]>=0)
						{
							buffer_array[ly][lx] = game_array[lx+x][ly+y]; 
						}
						else buffer_array[ly][lx] = -1;
					}
					else
					{
						buffer_array[ly][lx] = -1;
					}
				}
				else
				{
					buffer_array[ly][lx] = -1;
				}
			}
	}
	 
	// Восстановление игрового массива из буффера
	private synchronized void set_array_from_buffer(int x,int y)
	{
		int lcell = 0;
		for (int lx=0;lx<6;lx++)
			for(int ly=0;ly<6;ly++)
			{
				if (((x+lx)>=0)&&((x+lx)<GAME_FIELD_WIDTH))
					if (((y+ly)>=0)&&((y+ly)<GAME_FIELD_HEIGHT))
					{
						if (buffer_array[ly][lx]>=0)
						{
							lcell = buffer_array[ly][lx];
							set_cell(lx+x,ly+y,lcell&0xFF,lcell>>8);
						}
					}
			}
	}
	
	private void init_game_array()
	{
		// Очистка поля
		for (int lx=0;lx<GAME_FIELD_WIDTH;lx++)
			for(int ly=0;ly<GAME_FIELD_HEIGHT;ly++)
			{
				set_cell(lx,ly,TETRIS_EMPTY,C_W);   	
			}
		
		// Заносим координаты стакана
		for (int ly=GLASS_BOTTOM;ly>(GLASS_BOTTOM-GLASS_HEIGHT);ly--)
			for(int lx=GLASS_X;lx<(GLASS_X+GLASS_WIDTH);lx++)
			{
				if (ly==(GLASS_BOTTOM)) set_cell(lx,ly,TETRIS_GLASSWALL,C_B);
				else
				{
					if ((lx==GLASS_X)||(lx==(GLASS_X+GLASS_WIDTH-1))) set_cell(lx,ly,TETRIS_GLASSWALL,C_B);
					else
						set_cell(lx,ly,TETRIS_GLASSINSIDE,C_B);  	
				}
			}
		
		// Границы игрового экрана
		for (int lx=0;lx<GAME_FIELD_WIDTH;lx++)
			for(int ly=0;ly<GAME_FIELD_HEIGHT;ly++)
			{
				if ((lx==0)||(lx==(GAME_FIELD_WIDTH-1))||(lx==SCORE_BORDER)) set_cell(lx,ly,TETRIS_BORDER,C_R);
				else
				 if ((ly==0)||(ly==(GAME_FIELD_HEIGHT-1))) set_cell(lx,ly,TETRIS_BORDER,C_R);
			}
		
		// Площадка
		draw_figure_ground_area();
	}
	
	private int get_scores_from_glass()
	{
		int scores = 0;
		for(int ly=(GLASS_BOTTOM-1);ly>(GLASS_BOTTOM-GLASS_HEIGHT);ly--)
		{	
			boolean lfl = true;
			for(int lx=(GLASS_X+1);lx<(GLASS_X+GLASS_WIDTH-1);lx++)
			{
				if (get_cell_element(lx,ly)!=TETRIS_BLOCK)
				{
					lfl = false;
					break;
				}
			}
			if (lfl)
			{
				scores++;	
			}
		}
		
		return scores;
	}
	
	private void set_new_figure(int number)
	{
		current_figure = (int [][])figure_array [number];
		
		block_x = figure_start_x;
		block_y = figure_start_y; 
		draw_figure_ground_area();
		get_array_to_buffer(block_x,block_y);   
		
		
		figure_x_offset = -1;
		figure_y_offset = -1;
		figure_width    = 0;
		figure_height   = -1;

		int lx,ly;
		
		for(lx=0;lx<6;lx++)
		{
			boolean lflag = false;
			for(ly=0;ly<6;ly++)
			{
				if (current_figure[ly][lx]>=0)
				{
					if (figure_x_offset<0) figure_x_offset = lx;
					if (figure_y_offset<0) figure_y_offset = ly;
					lflag = true;
					break;
				}
			}
			
			if (lflag)
			{
				figure_width++;  	
			}
			
		}

		for(ly=0;ly<6;ly++)
		{
			boolean lflag =false;
			for(lx=0;lx<6;lx++)
			{
				if (current_figure[ly][lx]>=0) { lflag=true; break; }
			}
			if (lflag)
			{
				if (figure_height<0) figure_height=1; else figure_height++;
			}
		}
		
	}
	
	public void init()
	{
		game_mode = GAME_LOAD;
		game_array = new int[GAME_FIELD_WIDTH][GAME_FIELD_HEIGHT];   
		
		hidden_image = createImage(APPLET_WIDTH+x_offset_glb*2,APPLET_HEIGHT+y_offset_glb*2);
		hidden_graphics = hidden_image.getGraphics();  
		
		CELL_HEIGHT = APPLET_HEIGHT  / GAME_FIELD_HEIGHT;  
		CELL_WIDTH  = APPLET_WIDTH   / GAME_FIELD_WIDTH;  

   		panel_image = createImage(13*CELL_WIDTH,150);
		panel_graphics = panel_image.getGraphics(); 
		
		Thread thr = new Thread(this); 	
	
		panelimage_x = x_offset_glb+36*CELL_WIDTH;
		panelimage_y = y_offset_glb+20*CELL_HEIGHT;
		
		thr.start();
	}

	private void init_array_for_start_page()
	{
		stop_button = false;
		hidden_graphics.setFont(new Font("Dialog",Font.BOLD,18)); 
		String lstr=null;
		for(int ly=0;ly<GAME_FIELD_HEIGHT;ly++)
		{
			lstr = start_screen[ly]; 
			for(int lx=0;lx<lstr.length();lx++)
			{
				switch(lstr.charAt(lx))
				{
					case 'W' : 	{
									set_cell(lx,ly,TETRIS_GLASSWALL,C_R);	
								};break;
					case 'T' :  {
									set_cell(lx,ly,TETRIS_BLOCK,C_O);
								};break;
					case ' ' :  {
									set_cell(lx,ly,TETRIS_EMPTY,C_B);  
								};break;
					case 'E' :  {
									set_cell(lx,ly,TETRIS_BLOCK,C_G);
								};break;
					case 'R' :  {
									set_cell(lx,ly,TETRIS_BLOCK,C_R);
								};break;
					case 'I' :  {
									set_cell(lx,ly,TETRIS_BLOCK,C_Y);
								};break;
					case 'S' :  {
									set_cell(lx,ly,TETRIS_BLOCK,C_W);
								};break;
					
				}
			}
		}
	}

	private void init_array_for_final_page()
	{
		stop_button = false;
		String lstr=null;
		for(int ly=0;ly<GAME_FIELD_HEIGHT;ly++)
		{
			lstr = final_screen[ly]; 
			for(int lx=0;lx<lstr.length();lx++)
			{
				switch(lstr.charAt(lx))
				{
					case 'G' : 	{
									set_cell(lx,ly,TETRIS_BLOCK,C_O);	
								};break;
					case 'A' :  {
									set_cell(lx,ly,TETRIS_BLOCK,C_O);
								};break;
					case ' ' :  {
									set_cell(lx,ly,TETRIS_EMPTY,C_B);  
								};break;
					case 'M' :  {
									set_cell(lx,ly,TETRIS_BLOCK,C_G);
								};break;
					case 'E' :  {
									set_cell(lx,ly,TETRIS_BLOCK,C_R);
								};break;
					case 'O' :  {
									set_cell(lx,ly,TETRIS_BLOCK,C_Y);
								};break;
					case 'V' :  {
									set_cell(lx,ly,TETRIS_BLOCK,C_W);
								};break;
					case 'R' :  {
									set_cell(lx,ly,TETRIS_BLOCK,C_Y);
								};break;
					
				}
			}
		}
		
			hidden_graphics.setFont(new Font("Courier",Font.BOLD,20));  
			hidden_graphics.setColor(Color.white);
			hidden_graphics.drawString("Your scores    : "+Integer.toString(game_scores),15*CELL_WIDTH,30*CELL_HEIGHT);
			long inter = System.currentTimeMillis()-start_time;  
			java.util.Date ld = new java.util.Date(inter);
			String mn = Integer.toString(ld.getMinutes());
			String sc = Integer.toString(ld.getSeconds());
			
			if (mn.length()==1) mn="0"+mn;
			if (sc.length()==1) sc="0"+sc;
			
			hidden_graphics.drawString("Your play time : "+mn+":"+sc,15*CELL_WIDTH,30*CELL_HEIGHT+hidden_graphics.getFontMetrics().getHeight()+3);
			DrawFrame(hidden_graphics,0,0,hidden_image.getWidth(this),hidden_image.getHeight(this));
	}
	
	/**
	* Converts a string formatted as "rrggbb" to an awt.Color object
	*/
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
	
	public void run()
	{
		String var_html = null;

		images_placement = getParameter("IMAGES");
		if (images_placement==null)
		{
			images_placement = "";
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
		repaint();
		try
		{
			FULLFRAME_IMAGE = this.getImage(new URL(getCodeBase(),images_placement+"fullframe.gif"));
			SCORES_IMAGE = this.getImage(new URL(getCodeBase(),images_placement+"scores.gif"));
			TIME_IMAGE = this.getImage(new URL(getCodeBase(),images_placement+"time.gif"));
			STOP_IMAGE = this.getImage(new URL(getCodeBase(),images_placement+"stop.gif"));
		}
		catch (Exception ee) { return; }
		
		mtr.addImage(FULLFRAME_IMAGE ,0);
		mtr.addImage(SCORES_IMAGE ,1);
		mtr.addImage(TIME_IMAGE ,2);
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
				error_resource += "time.gif";
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
			game_mode=GAME_ERRORLOAD; 
			repaint(); 
			return;
		}
			
		game_mode = GAME_FIRSTPAGE; 
			
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
			game_mode = GAME_FIRSTPAGE; 
			init_array_for_start_page();
			full_repaint();
			while(game_mode!=GAME_RUN) 
			{
				try
				{
					Thread.sleep(300);
				}
				catch (InterruptedException e)
				{ return; }
			}
			init_game_array();
		
			java.util.Random rnd = new java.util.Random(); 
		
			set_new_figure((int)(rnd.nextDouble()*12));

			block_x = figure_start_x;
			block_y = figure_start_y;
			old_block_x = block_x;
			old_block_y = block_y;

			start_time = System.currentTimeMillis(); 
			
			get_array_to_buffer(old_block_x,old_block_y); 
			full_repaint();
			while(game_mode==GAME_RUN)
			{	
				repaint();
				try
				{
					Thread.sleep(200); 
				}
				catch(InterruptedException e){ break; }
			}
			end_time = System.currentTimeMillis(); 
			init_array_for_final_page();
			repaint();
//			hidden_graphics.setFont(new Font("Dialog",Font.BOLD,12)); 
			if (SendData(Integer.toString(this.game_scores)))
			{
				hidden_graphics.setColor(Color.green);
				String priglashenie = "The game data are saved";
				hidden_graphics.drawString(priglashenie,x_offset_glb+(GAME_FIELD_WIDTH*CELL_WIDTH-hidden_graphics.getFontMetrics().stringWidth(priglashenie))/2,y_offset_glb+CELL_HEIGHT*25); 
			}
			else
			{
				hidden_graphics.setColor(Color.red);
				String priglashenie = "I can not save the game data!!!";
				hidden_graphics.drawString(priglashenie,x_offset_glb+(GAME_FIELD_WIDTH*CELL_WIDTH-hidden_graphics.getFontMetrics().stringWidth(priglashenie))/2,y_offset_glb+CELL_HEIGHT*25);
			}
			
			this.getGraphics().drawImage(hidden_image,0,0,this);
			
			while(game_mode!=GAME_STOPPAGE) 
			{
				try
				{
					Thread.sleep(300);
				}
				catch (InterruptedException e)
				{ 
					break; 
				}
			}
		}
	}

	private Dimension getMouseCoord(int x,int y)
	{
		x -= x_offset_glb;
		if (x<0) x=0;
		y -= y_offset_glb;
		if (y<0) y=0;
		return new Dimension(x/CELL_WIDTH,y/CELL_HEIGHT);   	
	}
	
	private void fill_buffer_null()
	{
		for(int lx=0;lx<6;lx++)
			for(int ly=0;ly<6;ly++)
			{
				buffer_array[lx][ly]=-1; 	
			}
	}
	
	private boolean test_full_glass()
	{
		boolean bl = false;
		for(int lx=GLASS_X;lx<(GLASS_X+GLASS_WIDTH);lx++)
			if (get_cell_element(lx,GLASS_BOTTOM-GLASS_HEIGHT)==TETRIS_BLOCK)
			{
				bl = true; break;	
			}
		return bl;
	}
	
	private boolean figure_place(int x, int y)
	{
		if (x<(GLASS_X-figure_x_offset+1)) return false;
		if ((x+figure_width+figure_x_offset)>(GLASS_X+GLASS_WIDTH-1)) return false;
		for(int lx=0;lx<6;lx++)
			for(int ly=0;ly<6;ly++)
			{
				if (current_figure[ly][lx]>=0)
				{
					if ((get_cell_element(x+lx,y+ly)!=TETRIS_GLASSINSIDE)&&(get_cell_element(x+lx,y+ly)!=TETRIS_EMPTY)) return false;  	
				}
			}
		return true;
	}
	
	private void set_figure_into_glass(int x,int y)
	{
		while(true)
		{
			boolean fl=false;
			
			for (int lx=0;lx<6;lx++)
			{
				for (int ly=0;ly<6;ly++)
				{
					if (current_figure[ly][lx]>=0)
					{
						if (current_figure[ly+1][lx]<0)
						{
							if ((get_cell_element(x+lx,y+ly+1)!=TETRIS_GLASSINSIDE)&&(get_cell_element(x+lx,y+ly+1)!=TETRIS_EMPTY)) 
							{
								fl = true;
								break;
							}
						}
					}
				}
				if (fl) break;
			}
			if (!fl) y++; else break;
		}	
			int llx=x;
			int lly=y;
			for(int lx=0;lx<6;lx++)
				for(int ly=0;ly<6;ly++)
				{
					if (current_figure[ly][lx]>=0)
					{
						set_cell(llx+lx,lly+ly,TETRIS_BLOCK,current_figure[ly][lx]); 
					}
				}
		repaint_space_in_glass();	
	}

	private void repaint_space_in_glass()
	{
		for(int lx=0;lx<GLASS_WIDTH;lx++)
		{
			for(int ly=0;ly<GLASS_HEIGHT;ly++)
			{
				int el = get_cell_element(GLASS_X+lx,GLASS_BOTTOM-ly);
				int cl = get_cell_color(GLASS_X+lx,GLASS_BOTTOM-ly); 
				set_cell(GLASS_X+lx,GLASS_BOTTOM-ly,el,cl);
			}
		}
	}
	
	public synchronized boolean mouseDown( Event e, int x, int y )
	{
		if (game_mode==GAME_FINALPAGE)
		{
			game_mode = GAME_STOPPAGE;
			return true;	
		}
			
		if (game_mode==GAME_FIRSTPAGE)
		{
			game_mode = GAME_RUN;
			return true;	
		}
		
		if (game_mode!=GAME_RUN) return true;

		if (figure_drag) return true; 
		Dimension ld = getMouseCoord(x,y);
		if (ld.width>SCORE_BORDER) 
		{
			if ((ld.width>=figure_start_x)&&(ld.width<(figure_start_x+6)))
			if ((ld.height>=figure_start_y)&&(ld.height<(figure_start_y+6)))
			{
				if (get_cell_element(ld.width,ld.height)==TETRIS_BLOCK)
				{
					figure_drag = true;
					block_x = ld.width-figure_x_offset; 
					block_y = ld.height-figure_y_offset;
				}
			}
			else
			{
				// Проверка на нажатие кнопки стоп
				x -= panelimage_x;
				y -= panelimage_y; 
				if ((x<=0) || (y<=0)) return true;
				if ((x>panel_image.getWidth(this))||(y>panel_image.getHeight(this))) return true; 
				
				if ((y>=120) && (y<=(120+STOP_IMAGE.getHeight(this))))
				{
					stop_button = true;
				}
			}
			repaint();
		}
		return true;
	}

	public synchronized boolean keyDown( Event e, int key ) 
	{
		switch (game_mode)
		{
			case GAME_FIRSTPAGE : game_mode=GAME_RUN; break;
			case GAME_RUN		: if (key==Event.ESCAPE) game_mode=GAME_FINALPAGE; break;
			case GAME_FINALPAGE : game_mode=GAME_STOPPAGE; break;
		}
		return true;
	}
	
	public synchronized boolean mouseDrag(Event e, int x, int y )
	{
		if (game_mode!=GAME_RUN) return true;  
		
		if (figure_drag)
		{
			Dimension ld = getMouseCoord(x,y);
			if ((ld.width>=1)&&(ld.width<(GAME_FIELD_WIDTH-1)))
			if ((ld.height>=1)&&(ld.height<(GAME_FIELD_HEIGHT-1)))
			{
				block_x = ld.width-figure_x_offset;
				block_y = ld.height-figure_y_offset;
			}
			repaint();
		}
		return true;
	}
	
	public synchronized boolean mouseUp(Event evt, int x, int y )
	{
		if (game_mode!=GAME_RUN) return true;
		if (figure_drag)
		{	
			int lx = old_block_x;
			int ly = old_block_y; 
			figure_drag = false;
			set_array_from_buffer(lx,ly);

			if ((block_x<SCORE_BORDER)&&(figure_place(lx,ly)))
			{
				set_figure_into_glass(lx,ly);
				if (test_full_glass())
				{
					game_mode = GAME_FINALPAGE; 	
				}
												
				game_scores = get_scores_from_glass(); 
													
				java.util.Random rnd = new java.util.Random();
				set_new_figure((int)(rnd.nextDouble()*12));
			}
													
			block_x = figure_start_x;
			block_y = figure_start_y;
			
			
			draw_figure(block_x,block_y,false);
			repaint();															
		}
		return true;
	}

	public void paint(Graphics g)
	{
		switch (game_mode)
		{
			case GAME_LOAD : {
								g.setColor(Color.blue);  
								g.fillRect(0,0,hidden_image.getWidth(this),hidden_image.getHeight(this));
								g.setFont(new Font("Arial",Font.BOLD,12));
								g.setColor(Color.yellow);
								g.drawString("Loading Images. Please Wait...",10,g.getFontMetrics().getHeight()+5); 
							 };break;
			case GAME_ERRORLOAD : {
									g.setColor(Color.black);
									g.fillRect(0,0,hidden_image.getWidth(this),hidden_image.getHeight(this));
									g.setFont(new Font("Arial",Font.BOLD,12));
									g.setColor(Color.red);
									g.drawString("Error of loading a image",10,g.getFontMetrics().getHeight()+5); 
									g.drawString(error_resource,10,g.getFontMetrics().getHeight()*2+5); 
								  }; break;
			default :	g.drawImage(hidden_image,0,0,this);  	
		} 
	}
	
	public synchronized void update(Graphics g)
	{
		if ((game_mode==GAME_LOAD)||(game_mode==GAME_ERRORLOAD)) 
		{
			paint(g);
			return;
		}
		update_hidden();

		if (game_mode==GAME_FIRSTPAGE)
		{
			hidden_graphics.setColor(Color.green);
			String priglashenie = "Press any key for start";
			hidden_graphics.drawString(priglashenie,(GAME_FIELD_WIDTH*CELL_WIDTH-hidden_graphics.getFontMetrics().stringWidth(priglashenie))/2,CELL_HEIGHT*17);
		}
		paint(g); 	
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
	
}