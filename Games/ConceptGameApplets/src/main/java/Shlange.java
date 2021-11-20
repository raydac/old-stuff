import java.applet.*;
import java.awt.*; 
import java.awt.image.*; 
import java.awt.event.*; 
import java.io.*;
import java.net.*;

public class Shlange extends Applet implements Runnable 
{
	private MediaTracker mtr = new MediaTracker(this);
	private static final int GAME_LOAD = -2;
	private static final int GAME_LOADERROR = -1;

	private String error_resource = null;
	private static final int GAME_START = 0;
	private static final int GAME_RUN   = 1;
	private static final int GAME_STOP  = 2;
	private static final int GAME_WAIT   = 3;
	private static final int GAME_PAUSE  = 4;
	private static final int GAME_ANIMATION  = 5;
	private boolean reset_flag = false;
	private boolean send_result = false;
	
	// Outside images
	private Point reset_point = null;
	private Image RESET_IMAGE = null;
	private Point arrow_point = null;
	private Image ARROW_IMAGE = null;
	private Image SCORE_IMAGE = null;
	private Image FULLFRAME_IMAGE = null;
	private Image TIME_IMAGE  = null;

	private Image [] frames = new Image[9];
	
	private int game_mode = GAME_START; 
	
	private Thread python_thread_game = null;

	private Image hidden_image = null;
	private Graphics hidden_graphics = null;

	private Image hidden_scores_image = null;
	private Graphics hidden_scores_graphics = null;

	private Image bottom_image = null;
	private Graphics bottom_image_graphics = null;
	
	private int [][] game_field_array = null;
	
	private final int WIDTH_GAME_FIELD = 40;
	private final int HEIGHT_GAME_FIELD = 30;

	// Смещение на теневом экране
	private int hidden_x_offset = 8;
	private int hidden_y_offset = 8;
	private final int HIDDEN_BUFFER_WIDTH = 400+hidden_x_offset*2;
	private final int HIDDEN_BUFFER_HEIGHT = 300+hidden_y_offset*2;
	
	private final int HIDDEN_SCORES_WIDTH = 120;
	private final int HIDDEN_SCORES_HEIGHT = HIDDEN_BUFFER_HEIGHT;

	private final int BOTTOM_HEIGHT = 50;
	
	private int CELL_WIDTH = 0;
	private int CELL_HEIGHT = 0;

	private int MOUSE_COUNT = 60;
	private int WALL_COUNT = 40;

	private int current_scores = 0;

	private long start_time = 0;
	private long end_time = 0;

	private final int PYTHON_EMPTY  = 0;
	private final int PYTHON_WALL   = 1;
	private final int PYTHON_PYTHON = 2;
	private final int PYTHON_HEAD   = 3;
	private final int PYTHON_BODY   = 4;
	private final int PYTHON_TAIL   = 5;
	private final int PYTHON_MOUSE  = 6;

	private final int DIRECTION_UP    = 0;
	private final int DIRECTION_DOWN  = 1;
	private final int DIRECTION_LEFT  = 2;
	private final int DIRECTION_RIGHT = 3;

	private final int SPECIAL_NONE    = 0;
	private final int SPECIAL_LU      = 1;
	private final int SPECIAL_RU      = 2;
	private final int SPECIAL_LD      = 3;
	private final int SPECIAL_RD      = 4;

	private final int GAMEOVER_NONE	  = 0;
	private final int GAMEOVER_BAD	  = 1;
	private final int GAMEOVER_OK	  = 2;
	private final int GAMEOVER_RESET  = 3;

	private int current_head_direction = DIRECTION_LEFT; 
	private int buffer_head_direction  = DIRECTION_LEFT; 
	private int old_head_direction     = DIRECTION_LEFT; 
	private int current_tail_direction = DIRECTION_LEFT; 
	private int head_xposition = 20;
	private int head_yposition = 20;
	private int tail_xposition = 23;
	private int tail_yposition = 20;

	private final int RED = Color.red.getRGB(); 
	private final int WHT = Color.white.getRGB(); 
	private final int GRN = Color.green.getRGB(); 
	private final int BLU = Color.blue.getRGB(); 
	private final int BCK = Color.black.getRGB()& 0x00FFFFFF;
	private final int YEL = Color.yellow.getRGB();
	private final int GAY = Color.gray.getRGB(); 

	private Color background_color = null;
	private java.util.Random rnd = null;
	private String images_placement = null;
	
	
	private final String []start_page= { "WWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWW",
										 "W                                      W",
										 "W                                      W",
										 "W                                      W",
										 "W                                      W",
										 "W                                      W",
										 "W                                      W",
										 "W                                      W",
										 "W                                      W",
										 "W  MMM                                 W",
										 "W M         M    M MMM                 W",
										 "W M         M    M    M                W",
										 "W  MMM   MM M MM M  MMM M M   MM   MM  W",
										 "W     M M   MM M M M  M MM M M  M M MM W",
										 "W     M M   M  M M M MM M  M M  M MM   W",
										 "W  MMM   MM M  M M MM M M  M  MMM  MMM W",
										 "W                               M      W",
										 "W                            MMM       W",
										 "W                                      W",
										 "W                                      W",
										 "W                                      W",
										 "W                                      W",
										 "W                                      W",
										 "W                                      W",
										 "W                                      W",
										 "W                                      W",
										 "W                                      W",
										 "W                                      W",
										 "W                                      W",
										 "WWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWW"};


	private final String []ok_end_page= {"WWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWW",
										 "W                                      W",
										 "W                                      W",
										 "W                                      W",
										 "W                                      W",
										 "W                                      W",
										 "W                                      W",
										 "W                                      W",
										 "W       M                              W",
										 "W  WWW  WW  W   W WWWW                 W",
										 "W W    W  W WW WW W M                  W",
										 "W W WW WWWW W W W WWW                  W",
										 "W W  W W  W W   W W  M                 W",
										 "W  WWW W  W W   W WWWW                 W",
										 "W                                      W",
										 "W                 MWW  W   W WWWW WWW  W",
										 "W                 W  W W   W W    W  W W",
										 "W                 W  W  W W  WWW  WM W W",
										 "W                 W  W  W W  W  M WWW  W",
										 "W                  WW    W   WWWW W  W W",
										 "W                                      W",
										 "W                                      W",
										 "W                                      W",
										 "W                                      W",
										 "W                                      W",
										 "W                                      W",
										 "W                                      W",
										 "W                                      W",
										 "W                                      W",
										 "WWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWW"};

	
	
	private final int [] IMAGE_WALL_AR= {RED,RED,RED,RED,BCK,RED,RED,RED,RED,RED,
									     RED,RED,RED,RED,BCK,RED,RED,RED,RED,RED,
									     RED,RED,RED,RED,BCK,RED,RED,RED,RED,RED,
									     RED,RED,RED,RED,BCK,RED,RED,RED,RED,RED,
									     BCK,BCK,BCK,BCK,BCK,BCK,BCK,BCK,BCK,BCK,
									     RED,RED,RED,RED,RED,RED,RED,BCK,RED,RED,
									     RED,RED,RED,RED,RED,RED,RED,BCK,RED,RED,
									     RED,RED,RED,RED,RED,RED,RED,BCK,RED,RED,
									     RED,RED,RED,RED,RED,RED,RED,BCK,RED,RED,
										 BCK,BCK,BCK,BCK,BCK,BCK,BCK,BCK,BCK,BCK};
	
	private Image IMAGE_WALL = createImage(new MemoryImageSource(10,10,IMAGE_WALL_AR,0,10));
	

	private final int [] IMAGE_MOUSE_AR={BCK,BCK,BCK,BCK,BCK,BCK,BCK,BCK,BCK,BCK,
										 BCK,BCK,BCK,BCK,BCK,BCK,BCK,BCK,BCK,BCK,
										 BCK,BCK,BCK,BCK,BCK,BCK,BCK,BCK,BCK,BCK,
										 BCK,BCK,BCK,BCK,BLU,BLU,BCK,WHT,BCK,BCK,
										 BCK,BCK,BCK,WHT,WHT,WHT,WHT,WHT,BCK,BCK,
										 BCK,BCK,WHT,WHT,WHT,WHT,WHT,RED,WHT,BCK,
										 WHT,BCK,WHT,WHT,WHT,WHT,GAY,WHT,WHT,RED,
										 BCK,WHT,WHT,GAY,GAY,WHT,GAY,GAY,WHT,WHT,
										 BCK,BCK,BCK,BCK,BCK,BCK,BCK,BCK,BCK,BCK,
										 BCK,BCK,BCK,BCK,BCK,BCK,BCK,BCK,BCK,BCK};
										 
	private Image IMAGE_MOUSE = createImage(new MemoryImageSource(10,10,IMAGE_MOUSE_AR,0,10));



	private final int [] IMAGE_BODYLD_AR={ BCK,BCK,BCK,BCK,BCK,BCK,BCK,BCK,BCK,BCK,
										   GRN,GRN,GRN,GRN,GRN,GRN,GRN,BCK,BCK,BCK,
										   GRN,GRN,GRN,GRN,GRN,GRN,GRN,GRN,BCK,BCK,
										   GRN,GRN,GRN,GRN,GRN,GRN,GRN,GRN,GRN,BCK,
										   GRN,GRN,GRN,GRN,GRN,GRN,GRN,GRN,GRN,BCK,
										   GRN,GRN,GRN,GRN,GRN,GRN,GRN,GRN,GRN,BCK,
										   GRN,GRN,GRN,GRN,GRN,GRN,GRN,GRN,GRN,BCK,
										   GRN,GRN,GRN,GRN,GRN,GRN,GRN,GRN,GRN,BCK,
										   GRN,GRN,GRN,GRN,GRN,GRN,GRN,GRN,GRN,BCK,
										   BCK,GRN,GRN,GRN,GRN,GRN,GRN,GRN,GRN,BCK};

	private final int [] IMAGE_BODYRD_AR={ BCK,BCK,BCK,BCK,BCK,BCK,BCK,BCK,BCK,BCK,
										   BCK,BCK,BCK,GRN,GRN,GRN,GRN,GRN,GRN,GRN,
										   BCK,BCK,GRN,GRN,GRN,GRN,GRN,GRN,GRN,GRN,
										   BCK,GRN,GRN,GRN,GRN,GRN,GRN,GRN,GRN,GRN,
										   BCK,GRN,GRN,GRN,GRN,GRN,GRN,GRN,GRN,GRN,
										   BCK,GRN,GRN,GRN,GRN,GRN,GRN,GRN,GRN,GRN,
										   BCK,GRN,GRN,GRN,GRN,GRN,GRN,GRN,GRN,GRN,
										   BCK,GRN,GRN,GRN,GRN,GRN,GRN,GRN,GRN,GRN,
										   BCK,GRN,GRN,GRN,GRN,GRN,GRN,GRN,GRN,GRN,
										   BCK,GRN,GRN,GRN,GRN,GRN,GRN,GRN,GRN,BCK};
	
	private final int [] IMAGE_BODYLU_AR={ BCK,GRN,GRN,GRN,GRN,GRN,GRN,GRN,GRN,BCK,
										   GRN,GRN,GRN,GRN,GRN,GRN,GRN,GRN,GRN,BCK,
										   GRN,GRN,GRN,GRN,GRN,GRN,GRN,GRN,GRN,BCK,
										   GRN,GRN,GRN,GRN,GRN,GRN,GRN,GRN,GRN,BCK,
										   GRN,GRN,GRN,GRN,GRN,GRN,GRN,GRN,GRN,BCK,
										   GRN,GRN,GRN,GRN,GRN,GRN,GRN,GRN,GRN,BCK,
										   GRN,GRN,GRN,GRN,GRN,GRN,GRN,GRN,GRN,BCK,
										   GRN,GRN,GRN,GRN,GRN,GRN,GRN,GRN,BCK,BCK,
										   GRN,GRN,GRN,GRN,GRN,GRN,GRN,BCK,BCK,BCK,
										   BCK,BCK,BCK,BCK,BCK,BCK,BCK,BCK,BCK,BCK};

	private final int [] IMAGE_BODYRU_AR={ BCK,GRN,GRN,GRN,GRN,GRN,GRN,GRN,GRN,BCK,
										   BCK,GRN,GRN,GRN,GRN,GRN,GRN,GRN,GRN,GRN,
										   BCK,GRN,GRN,GRN,GRN,GRN,GRN,GRN,GRN,GRN,
										   BCK,GRN,GRN,GRN,GRN,GRN,GRN,GRN,GRN,GRN,
										   BCK,GRN,GRN,GRN,GRN,GRN,GRN,GRN,GRN,GRN,
										   BCK,GRN,GRN,GRN,GRN,GRN,GRN,GRN,GRN,GRN,
										   BCK,GRN,GRN,GRN,GRN,GRN,GRN,GRN,GRN,GRN,
										   BCK,BCK,GRN,GRN,GRN,GRN,GRN,GRN,GRN,GRN,
										   BCK,BCK,BCK,GRN,GRN,GRN,GRN,GRN,GRN,GRN,
										   BCK,BCK,BCK,BCK,BCK,BCK,BCK,BCK,BCK,BCK};

	
	
	private final int [] IMAGE_BODYHRZ_AR={ BCK,BCK,BCK,BCK,BCK,BCK,BCK,BCK,BCK,BCK,
										   GRN,GRN,GRN,GRN,GRN,GRN,GRN,GRN,GRN,GRN,
										   GRN,GRN,GRN,GRN,GRN,GRN,GRN,GRN,GRN,GRN,
										   GRN,GRN,GRN,GRN,GRN,GRN,GRN,GRN,GRN,GRN,
										   GRN,GRN,GRN,GRN,GRN,GRN,GRN,GRN,GRN,GRN,
										   GRN,GRN,GRN,GRN,GRN,GRN,GRN,GRN,GRN,GRN,
										   GRN,GRN,GRN,GRN,GRN,GRN,GRN,GRN,GRN,GRN,
										   GRN,GRN,GRN,GRN,GRN,GRN,GRN,GRN,GRN,GRN,
										   GRN,GRN,GRN,GRN,GRN,GRN,GRN,GRN,GRN,GRN,
										   BCK,BCK,BCK,BCK,BCK,BCK,BCK,BCK,BCK,BCK};

	private final int [] IMAGE_BODYVERT_AR={ BCK,GRN,GRN,GRN,GRN,GRN,GRN,GRN,GRN,BCK,
										   BCK,GRN,GRN,GRN,GRN,GRN,GRN,GRN,GRN,BCK,
										   BCK,GRN,GRN,GRN,GRN,GRN,GRN,GRN,GRN,BCK,
										   BCK,GRN,GRN,GRN,GRN,GRN,GRN,GRN,GRN,BCK,
										   BCK,GRN,GRN,GRN,GRN,GRN,GRN,GRN,GRN,BCK,
										   BCK,GRN,GRN,GRN,GRN,GRN,GRN,GRN,GRN,BCK,
										   BCK,GRN,GRN,GRN,GRN,GRN,GRN,GRN,GRN,BCK,
										   BCK,GRN,GRN,GRN,GRN,GRN,GRN,GRN,GRN,BCK,
										   BCK,GRN,GRN,GRN,GRN,GRN,GRN,GRN,GRN,BCK,
										   BCK,GRN,GRN,GRN,GRN,GRN,GRN,GRN,GRN,BCK};
	
	private Image IMAGE_BODYRD = createImage(new MemoryImageSource(10,10,IMAGE_BODYRD_AR,0,10));
	private Image IMAGE_BODYLD = createImage(new MemoryImageSource(10,10,IMAGE_BODYLD_AR,0,10));
	private Image IMAGE_BODYRU = createImage(new MemoryImageSource(10,10,IMAGE_BODYRU_AR,0,10));
	private Image IMAGE_BODYLU = createImage(new MemoryImageSource(10,10,IMAGE_BODYLU_AR,0,10));
	
	private Image IMAGE_BODYHRZ = createImage(new MemoryImageSource(10,10,IMAGE_BODYHRZ_AR,0,10));
	private Image IMAGE_BODYVERT = createImage(new MemoryImageSource(10,10,IMAGE_BODYVERT_AR,0,10));
	
	private final int [] IMAGE_HEADL_AR={ BCK,BCK,BCK,BCK,GRN,GRN,GRN,GRN,BCK,BCK,
									 	  BCK,BCK,BCK,GRN,GRN,GRN,GRN,GRN,GRN,GRN,
										  BCK,BCK,GRN,GRN,BCK,GRN,GRN,GRN,GRN,GRN,
										  BCK,GRN,GRN,GRN,BCK,GRN,GRN,GRN,GRN,GRN,
										  BCK,GRN,GRN,GRN,GRN,GRN,GRN,GRN,GRN,GRN,
										  BCK,GRN,GRN,GRN,GRN,GRN,GRN,GRN,GRN,GRN,
										  BCK,GRN,GRN,GRN,BCK,GRN,GRN,GRN,GRN,GRN,
										  BCK,BCK,GRN,GRN,BCK,GRN,GRN,GRN,GRN,GRN,
										  BCK,BCK,BCK,GRN,GRN,GRN,GRN,GRN,GRN,GRN,
										  BCK,BCK,BCK,BCK,GRN,GRN,GRN,GRN,BCK,BCK};

	
	private final int [] IMAGE_HEADR_AR={ BCK,BCK,GRN,GRN,GRN,GRN,BCK,BCK,BCK,BCK,
									 	  GRN,GRN,GRN,GRN,GRN,GRN,GRN,BCK,BCK,BCK,
										  GRN,GRN,GRN,GRN,GRN,BCK,GRN,GRN,BCK,BCK,
										  GRN,GRN,GRN,GRN,GRN,BCK,GRN,GRN,GRN,BCK,
										  GRN,GRN,GRN,GRN,GRN,GRN,GRN,GRN,GRN,BCK,
										  GRN,GRN,GRN,GRN,GRN,GRN,GRN,GRN,GRN,BCK,
										  GRN,GRN,GRN,GRN,GRN,BCK,GRN,GRN,GRN,BCK,
										  GRN,GRN,GRN,GRN,GRN,BCK,GRN,GRN,BCK,BCK,
										  GRN,GRN,GRN,GRN,GRN,GRN,GRN,BCK,BCK,BCK,
										  BCK,BCK,GRN,GRN,GRN,GRN,BCK,BCK,BCK,BCK};

	private final int [] IMAGE_HEADU_AR={ BCK,BCK,BCK,BCK,BCK,BCK,BCK,BCK,BCK,BCK,
									 	  BCK,BCK,BCK,GRN,GRN,GRN,GRN,BCK,BCK,BCK,
										  BCK,BCK,GRN,GRN,GRN,GRN,GRN,GRN,BCK,BCK,
										  BCK,GRN,GRN,GRN,GRN,GRN,GRN,GRN,GRN,BCK,
										  GRN,GRN,BCK,BCK,GRN,GRN,BCK,BCK,GRN,GRN,
										  GRN,GRN,GRN,GRN,GRN,GRN,GRN,GRN,GRN,GRN,
										  GRN,GRN,GRN,GRN,GRN,GRN,GRN,GRN,GRN,GRN,
										  GRN,GRN,GRN,GRN,GRN,GRN,GRN,GRN,GRN,GRN,
										  BCK,GRN,GRN,GRN,GRN,GRN,GRN,GRN,GRN,BCK,
										  BCK,GRN,GRN,GRN,GRN,GRN,GRN,GRN,GRN,BCK};


	private final int [] IMAGE_HEADD_AR={ BCK,GRN,GRN,GRN,GRN,GRN,GRN,GRN,GRN,BCK,
										  BCK,GRN,GRN,GRN,GRN,GRN,GRN,GRN,GRN,BCK,
										  GRN,GRN,GRN,GRN,GRN,GRN,GRN,GRN,GRN,GRN,
										  GRN,GRN,GRN,GRN,GRN,GRN,GRN,GRN,GRN,GRN,
										  GRN,GRN,GRN,GRN,GRN,GRN,GRN,GRN,GRN,GRN,
										  GRN,GRN,BCK,BCK,GRN,GRN,BCK,BCK,GRN,GRN,
										  BCK,GRN,GRN,GRN,GRN,GRN,GRN,GRN,GRN,BCK,
										  BCK,BCK,GRN,GRN,GRN,GRN,GRN,GRN,BCK,BCK,
									 	  BCK,BCK,BCK,GRN,GRN,GRN,GRN,BCK,BCK,BCK,
										  BCK,BCK,BCK,BCK,BCK,BCK,BCK,BCK,BCK,BCK};
	
	private Image IMAGE_HEAD_L = createImage(new MemoryImageSource(10,10,IMAGE_HEADL_AR,0,10));
	private Image IMAGE_HEAD_R = createImage(new MemoryImageSource(10,10,IMAGE_HEADR_AR,0,10));
	private Image IMAGE_HEAD_U = createImage(new MemoryImageSource(10,10,IMAGE_HEADU_AR,0,10));
	private Image IMAGE_HEAD_D = createImage(new MemoryImageSource(10,10,IMAGE_HEADD_AR,0,10));

	private final int [] IMAGE_TAILL_AR={BCK,BCK,BCK,BCK,BCK,BCK,BCK,BCK,BCK,BCK,
										 GRN,GRN,GRN,BCK,BCK,BCK,BCK,BCK,BCK,BCK,
										 GRN,GRN,GRN,GRN,GRN,GRN,BCK,BCK,BCK,BCK,
										 GRN,GRN,GRN,GRN,GRN,GRN,GRN,GRN,BCK,BCK,
										 GRN,GRN,GRN,GRN,GRN,GRN,GRN,GRN,GRN,BCK,
										 GRN,GRN,GRN,GRN,GRN,GRN,GRN,GRN,GRN,BCK,
										 GRN,GRN,GRN,GRN,GRN,GRN,GRN,GRN,BCK,BCK,
										 GRN,GRN,GRN,GRN,GRN,GRN,BCK,BCK,BCK,BCK,
										 GRN,GRN,GRN,BCK,BCK,BCK,BCK,BCK,BCK,BCK,
										 BCK,BCK,BCK,BCK,BCK,BCK,BCK,BCK,BCK,BCK};
										 
	private final int [] IMAGE_TAILR_AR={BCK,BCK,BCK,BCK,BCK,BCK,BCK,BCK,BCK,BCK,
										 BCK,BCK,BCK,BCK,BCK,BCK,BCK,GRN,GRN,GRN,
										 BCK,BCK,BCK,BCK,GRN,GRN,GRN,GRN,GRN,GRN,
										 BCK,BCK,GRN,GRN,GRN,GRN,GRN,GRN,GRN,GRN,
										 BCK,GRN,GRN,GRN,GRN,GRN,GRN,GRN,GRN,GRN,
										 BCK,GRN,GRN,GRN,GRN,GRN,GRN,GRN,GRN,GRN,
										 BCK,BCK,GRN,GRN,GRN,GRN,GRN,GRN,GRN,GRN,
										 BCK,BCK,BCK,BCK,GRN,GRN,GRN,GRN,GRN,GRN,
										 BCK,BCK,BCK,BCK,BCK,BCK,BCK,GRN,GRN,GRN,
										 BCK,BCK,BCK,BCK,BCK,BCK,BCK,BCK,BCK,BCK};
	
	private final int [] IMAGE_TAILU_AR={BCK,GRN,GRN,GRN,GRN,GRN,GRN,GRN,GRN,BCK,
										 BCK,GRN,GRN,GRN,GRN,GRN,GRN,GRN,GRN,BCK,
										 BCK,GRN,GRN,GRN,GRN,GRN,GRN,GRN,GRN,BCK,
										 BCK,BCK,GRN,GRN,GRN,GRN,GRN,GRN,BCK,BCK,
										 BCK,BCK,GRN,GRN,GRN,GRN,GRN,GRN,BCK,BCK,
										 BCK,BCK,GRN,GRN,GRN,GRN,GRN,GRN,BCK,BCK,
										 BCK,BCK,BCK,GRN,GRN,GRN,GRN,BCK,BCK,BCK,
										 BCK,BCK,BCK,GRN,GRN,GRN,GRN,BCK,BCK,BCK,
										 BCK,BCK,BCK,BCK,GRN,GRN,BCK,BCK,BCK,BCK,
										 BCK,BCK,BCK,BCK,BCK,BCK,BCK,BCK,BCK,BCK};
	
	private final int [] IMAGE_TAILD_AR={BCK,BCK,BCK,BCK,BCK,BCK,BCK,BCK,BCK,BCK,
										 BCK,BCK,BCK,BCK,GRN,GRN,BCK,BCK,BCK,BCK,
										 BCK,BCK,BCK,GRN,GRN,GRN,GRN,BCK,BCK,BCK,
										 BCK,BCK,BCK,GRN,GRN,GRN,GRN,BCK,BCK,BCK,
										 BCK,BCK,GRN,GRN,GRN,GRN,GRN,GRN,BCK,BCK,
										 BCK,BCK,GRN,GRN,GRN,GRN,GRN,GRN,BCK,BCK,
										 BCK,BCK,GRN,GRN,GRN,GRN,GRN,GRN,BCK,BCK,
										 BCK,GRN,GRN,GRN,GRN,GRN,GRN,GRN,GRN,BCK,
										 BCK,GRN,GRN,GRN,GRN,GRN,GRN,GRN,GRN,BCK,
										 BCK,GRN,GRN,GRN,GRN,GRN,GRN,GRN,GRN,BCK};

	
	private Image IMAGE_TAIL_L = createImage(new MemoryImageSource(10,10,IMAGE_TAILL_AR,0,10));
	private Image IMAGE_TAIL_R = createImage(new MemoryImageSource(10,10,IMAGE_TAILR_AR,0,10));
	private Image IMAGE_TAIL_U = createImage(new MemoryImageSource(10,10,IMAGE_TAILU_AR,0,10));
	private Image IMAGE_TAIL_D = createImage(new MemoryImageSource(10,10,IMAGE_TAILD_AR,0,10));
	
	private void out_start_paper()
	{
		end_time = 0;
		start_time = 0;
		current_scores = 0;
		char [] current_str = null;
		int mouse_counter = 0; 
		for(int ly=0;ly<HEIGHT_GAME_FIELD;ly++)
		{
			current_str = start_page[ly].toCharArray();
			for(int lx=0;lx<WIDTH_GAME_FIELD;lx++)
			{
				if (current_str[lx]=='M') mouse_counter++;
			}
		}
		rnd = new java.util.Random(System.currentTimeMillis()); 
		
		int [] mouse_x = new int[mouse_counter]; 
		int [] mouse_y = new int[mouse_counter];
		int [] mouse_step = new int[mouse_counter];
		
		mouse_counter=0; 
		for(int ly=0;ly<HEIGHT_GAME_FIELD;ly++)
		{
			current_str = start_page[ly].toCharArray();
			for(int lx=0;lx<WIDTH_GAME_FIELD;lx++)
			{
				switch(current_str[lx])
				{
					case 'W' : 	setElement(lx,ly,PYTHON_WALL);break;  
					case ' ' : 	setElement(lx,ly,PYTHON_EMPTY);break;
					case 'M' : 	{
									mouse_x[mouse_counter] = lx; 
									mouse_y[mouse_counter] = ly;
									mouse_step[mouse_counter] =(int)((6*rnd.nextDouble())-3); 
									if (mouse_step[mouse_counter]==0) mouse_step[mouse_counter]=(-2);
									mouse_counter++;
									setElement(lx,ly,PYTHON_MOUSE);break;
								}
				}
			}
		}

		UpdateFullBuffer();

		// Выводим надпись
		hidden_graphics.setColor(Color.yellow);
		String prigl = "Press \"ENTER\" for start";
		hidden_graphics.drawString(prigl,((WIDTH_GAME_FIELD*CELL_WIDTH)-hidden_graphics.getFontMetrics().stringWidth(prigl))/2,200);
		
		repaint(); 

		while(game_mode!=GAME_WAIT) 
		{
			try
			{
				Thread.sleep(300);
			}
			catch (InterruptedException e)
			{ return; }
		}
			

		//Стираем надпись
		hidden_graphics.setColor(background_color);
		hidden_graphics.fillRect(((WIDTH_GAME_FIELD*CELL_WIDTH)-hidden_graphics.getFontMetrics().stringWidth(prigl))/2,200-hidden_graphics.getFontMetrics().getHeight(),hidden_graphics.getFontMetrics().stringWidth(prigl),hidden_graphics.getFontMetrics().getHeight());
		
		head_xposition = 20;
		head_yposition = HEIGHT_GAME_FIELD-1;
		tail_xposition = 20;
		tail_yposition = HEIGHT_GAME_FIELD+5;
		current_head_direction = DIRECTION_UP; 
		buffer_head_direction  = DIRECTION_UP; 
		current_tail_direction = DIRECTION_UP; 
		
		while(head_yposition>(-10)) 
		{
			if ((head_yposition>0)&&(head_yposition<(HEIGHT_GAME_FIELD-1)))
			{
				setElement(head_xposition,head_yposition,PYTHON_BODY);
				setSpecial(head_xposition,head_yposition,SPECIAL_NONE);
				setDirection(head_xposition,head_yposition,DIRECTION_UP);
			}
			head_yposition--;	
			
			if ((head_yposition>0)&&(head_yposition<(HEIGHT_GAME_FIELD-1)))
			{
				setElement(head_xposition,head_yposition,PYTHON_HEAD);
			}
			
			if ((tail_yposition>0)&&(tail_yposition<(HEIGHT_GAME_FIELD-1)))
			{
				setElement(tail_xposition,tail_yposition,PYTHON_EMPTY);
			}
			tail_yposition--;	
			
			if ((tail_yposition>0)&&(tail_yposition<(HEIGHT_GAME_FIELD-1)))
			{
				setElement(tail_xposition,tail_yposition,PYTHON_TAIL);
			}
			
			for(int lc=0;lc<mouse_step.length;lc++)
			{
				if (mouse_y[lc]>0)
				{
					setElement(mouse_x[lc],mouse_y[lc],PYTHON_EMPTY);
					mouse_y[lc]=mouse_y[lc]-1;
					if (mouse_y[lc]==0)mouse_y[lc]=(-1);
					else
					{
						mouse_x[lc]=mouse_x[lc]+mouse_step[lc];
						if ((mouse_x[lc]<=0)||(mouse_x[lc]>=(WIDTH_GAME_FIELD-1)))
						{
							mouse_y[lc]=(-1);	
						}
						else
						{
							setElement(mouse_x[lc],mouse_y[lc],PYTHON_MOUSE);
						}
					}
					if (mouse_y[lc]<0) mouse_counter--;  
				}
			}
			repaint(); 
			try
			{
				Thread.sleep(100); 
			}
			catch(InterruptedException e){}
		}
	}

	private void clear_field()
	{
		for (int lx=0;lx<WIDTH_GAME_FIELD;lx++)
			for (int ly=0;ly<HEIGHT_GAME_FIELD;ly++)
			{
				if ((lx==0)||(lx==(WIDTH_GAME_FIELD-1))||(ly==0)||(ly==(HEIGHT_GAME_FIELD-1))) setElement(lx,ly,PYTHON_WALL); else setElement(lx,ly,PYTHON_EMPTY);
			}
	}

	private void bad_end_of_game()
	{
		char []  current_str = null;
		for(int ly=0;ly<HEIGHT_GAME_FIELD;ly++)
		{
			current_str = ok_end_page[ly].toCharArray();
			for(int lx=0;lx<WIDTH_GAME_FIELD;lx++)
			{
				switch(current_str[lx])
				{
					case 'W' : 	setElement(lx,ly,PYTHON_WALL);break;  
					case ' ' : 	setElement(lx,ly,PYTHON_EMPTY);break;
					case 'M' : 	{
									setElement(lx,ly,PYTHON_MOUSE);break;
								}
				}
			}
		}
		
		for (int lx=0;lx<WIDTH_GAME_FIELD;lx++)
			for (int ly=0;ly<HEIGHT_GAME_FIELD;ly++)
			{
				if ((lx==0)||(lx==(WIDTH_GAME_FIELD-1))||(ly==0)||(ly>22)) setElement(lx,ly,PYTHON_WALL); 
			}
		head_xposition = (-3);
		head_yposition = 22;
		tail_xposition = (-8);
		tail_yposition = 22;
		current_head_direction = DIRECTION_UP;
		buffer_head_direction  = DIRECTION_UP; 
		current_tail_direction = DIRECTION_DOWN;
		
		UpdateFullBuffer();
		try
		{
			while(tail_xposition<(WIDTH_GAME_FIELD+5))
			{
				if ((head_xposition>0)&&(head_xposition<(WIDTH_GAME_FIELD-1))) 
				{	
					setElement(head_xposition,head_yposition-1,PYTHON_BODY);
					setSpecial(head_xposition,head_yposition-1,SPECIAL_NONE);
					setDirection(head_xposition,head_yposition-1,DIRECTION_RIGHT);	
				}
				head_xposition++;
				if ((head_xposition>0)&&(head_xposition<(WIDTH_GAME_FIELD-1))) 
				{	
					setElement(head_xposition,head_yposition-1,PYTHON_HEAD);
					setElement(head_xposition,head_yposition,PYTHON_MOUSE);
				}
				tail_xposition++;
				if ((tail_xposition>0)&&(tail_xposition<(WIDTH_GAME_FIELD-1)))
				{	
					if (tail_xposition<(WIDTH_GAME_FIELD-2))
					{
						setElement(tail_xposition+1,tail_yposition-1,PYTHON_TAIL);	
					}
					setElement(tail_xposition,tail_yposition,PYTHON_EMPTY);
					setElement(tail_xposition,tail_yposition-1,PYTHON_EMPTY);
				}
				this.repaint();
				Thread.sleep(60); 
			}

		}
		catch(InterruptedException e){}

		game_mode = GAME_STOP;
		while(game_mode == GAME_STOP) 
		{
			try
			{
				Thread.sleep(300);
			}
			catch (InterruptedException e)
			{ return; }
		}
	}
	
	private void ok_game_end()
	{
		char []  current_str = null;
		for(int ly=0;ly<HEIGHT_GAME_FIELD;ly++)
		{
			current_str = ok_end_page[ly].toCharArray();
			for(int lx=0;lx<WIDTH_GAME_FIELD;lx++)
			{
				switch(current_str[lx])
				{
					case 'W' : 	setElement(lx,ly,PYTHON_WALL);break;  
					case ' ' : 	setElement(lx,ly,PYTHON_EMPTY);break;
					case 'M' : 	{
									setElement(lx,ly,PYTHON_MOUSE);break;
								}
				}
			}
		}
		game_mode = GAME_ANIMATION;		
		UpdateFullBuffer();
		try
		{
			Thread.sleep(300);
		}
		catch (InterruptedException e){}
		game_mode=GAME_STOP;
		while(game_mode==GAME_STOP)
		{
			try
			{
				Thread.sleep(300);
			}
			catch (InterruptedException e)
			{ return; }
		}
	}
	
	private void run_page_init()
	{
		current_head_direction = DIRECTION_LEFT; 
		buffer_head_direction  = DIRECTION_LEFT; 
		old_head_direction     = DIRECTION_LEFT; 
		current_tail_direction = DIRECTION_LEFT; 
		
		head_xposition = 20;
		head_yposition = 20;
		tail_xposition = 23;
		tail_yposition = 20;

		for (int lx=0;lx<WIDTH_GAME_FIELD;lx++)
			for (int ly=0;ly<HEIGHT_GAME_FIELD;ly++)
			{
				if ((lx==0)||(lx==(WIDTH_GAME_FIELD-1))||(ly==0)||(ly==(HEIGHT_GAME_FIELD-1))) setElement(lx,ly,PYTHON_WALL); else setElement(lx,ly,PYTHON_EMPTY);
			}

		setElement(head_xposition,head_yposition,PYTHON_HEAD);

		for(int lx=head_xposition+1;lx<head_xposition+4;lx++)
		{
			setElement(lx,head_yposition,PYTHON_BODY); 
			setDirection(lx,head_yposition,DIRECTION_LEFT); 
		}
		tail_xposition = head_xposition+4;
		tail_yposition = head_yposition;
		
		setElement(tail_xposition,tail_yposition,PYTHON_TAIL); 
		
		for (int lc=0;lc<WALL_COUNT;lc++)
		{
			int lx = (int)((WIDTH_GAME_FIELD-1)*rnd.nextDouble());
			int ly = (int)((HEIGHT_GAME_FIELD-1)*rnd.nextDouble());
			while (game_field_array[lx][ly]!=PYTHON_EMPTY)
			{
				lx = (int)((WIDTH_GAME_FIELD-1)*rnd.nextDouble());
				ly = (int)((HEIGHT_GAME_FIELD-1)*rnd.nextDouble());
			}
			setElement(lx,ly,PYTHON_WALL); 
		}
		
		for (int lc=0;lc<MOUSE_COUNT;lc++)
		{
			int lx = (int)((WIDTH_GAME_FIELD-1)*rnd.nextDouble());
			int ly = (int)((HEIGHT_GAME_FIELD-1)*rnd.nextDouble());
			while (game_field_array[lx][ly]!=PYTHON_EMPTY)
			{
				lx = (int)((WIDTH_GAME_FIELD-1)*rnd.nextDouble());
				ly = (int)((HEIGHT_GAME_FIELD-1)*rnd.nextDouble());
			}
			setElement(lx,ly,PYTHON_MOUSE); 
		}
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
	
	public void init()
	{
		try
		{
			hidden_image = this.createImage(HIDDEN_BUFFER_WIDTH,HIDDEN_BUFFER_HEIGHT);
			hidden_graphics = hidden_image.getGraphics(); 
	
			hidden_scores_image = this.createImage(HIDDEN_SCORES_WIDTH,HIDDEN_SCORES_HEIGHT);
			hidden_scores_graphics = hidden_scores_image.getGraphics(); 

			bottom_image = this.createImage(HIDDEN_BUFFER_WIDTH+HIDDEN_SCORES_WIDTH,BOTTOM_HEIGHT);
			bottom_image_graphics = bottom_image.getGraphics();  
		}
		catch (Exception e)
		{
			System.out.println("Cannot create surface ["+e.getMessage()+"]");
			return;	
		}

		game_mode = GAME_LOAD; 

		python_thread_game = new Thread(this); 
		python_thread_game.start(); 
	}
	
	public synchronized void update(Graphics g)
	{
		if (game_mode != GAME_LOAD)
		{
			update_hidden_scores_buffer(); 
			if (game_mode==GAME_ANIMATION)
			{
				hidden_graphics.setFont(new Font("Dialog",Font.PLAIN,12));
				String lstr = null;
				if (send_result)
				{
					hidden_graphics.setColor(Color.white);
					lstr = "The game data are successfully saved";						 
				}
				else
				{
					hidden_graphics.setColor(Color.red);
					lstr = "I can not save the game data!!!";						 
				}
				hidden_graphics.drawString(lstr,hidden_x_offset+(WIDTH_GAME_FIELD*CELL_WIDTH-hidden_graphics.getFontMetrics().stringWidth(lstr))/2,hidden_y_offset+4*CELL_HEIGHT);
			}
		}
		paint(g);
	}
	
	public void paint (Graphics g)
	{
		switch(game_mode) 
		{		
		case GAME_LOADERROR :
		  {
			 g.setColor(Color.black);
			 g.fill3DRect(0,0,this.getSize().width,this.getSize().height,true);
			 g.setColor(Color.red);  
			 g.setFont(new Font("Arial",Font.BOLD,20));
			 g.drawString(" Error of load image\""+error_resource+"\"",0,g.getFontMetrics().getHeight()+3);
		  }; break;
		case GAME_LOAD :
		  {
			 g.setColor(Color.blue);
			 g.fill3DRect(0,0,this.getSize().width,this.getSize().height,true);
			 g.setColor(Color.yellow);  
			 g.setFont(new Font("Arial",Font.BOLD,12));
			 g.drawString(" Loading Images. Please Wait....",0,g.getFontMetrics().getHeight()+3);
		  }; break;
		default :
			{
			  g.drawImage(hidden_image,0,0,this);
			  g.drawImage(hidden_scores_image,HIDDEN_BUFFER_WIDTH,0,this);  
			  g.drawImage(bottom_image,0,HIDDEN_BUFFER_HEIGHT,this);  
			}
		}
	} 

	private int getDirection(int x,int y)
	{
		int lcb = game_field_array[x][y];
		return (lcb >> 4)&0xF;
	}

	private void setSpecial(int x,int y,int spec)
	{
		game_field_array[x][y] = (game_field_array[x][y] & 0xFF)|((spec & 0xF)<<8);
		setElement(x,y,getElement(x,y));
	}
	
	private int getSpecial(int x,int y)
	{
		return (game_field_array[x][y] >> 8) & 0xF;
	}
	
	private void setDirection(int x,int y,int dir)
	{
		game_field_array[x][y] = (game_field_array[x][y] & 0xF0F) | ((dir & 0xF)<<4);
		setElement(x,y,getElement(x,y));
	}

	private void setElement(int x,int y,int element)
	{
		game_field_array[x][y] = (game_field_array[x][y] & 0xFF0) |( element & 0xF);
		element = element & 0xF;
		draw_empty(x,y);
		switch (element)
		{
			case PYTHON_EMPTY :	{
									draw_empty(x,y); 
								}; break;
			case PYTHON_WALL  : {
									draw_wall(x,y);
								}; break;
			case PYTHON_MOUSE : {
									draw_mouse(x,y); 
								}; break;
			case PYTHON_HEAD  : {
									draw_head(x,y);
								}; break;
			case PYTHON_TAIL  : {
									draw_tail(x,y);
								}; break;
			case PYTHON_BODY  : {
									draw_body(x,y); 
								}; break;
		}
	}

	private int getElement(int x,int y)
	{
		return game_field_array[x][y] & 0xF;
	}
	
	private void draw_empty(int x,int y)
	{
		hidden_graphics.setColor(background_color);  
		hidden_graphics.fillRect(hidden_x_offset+x*CELL_WIDTH,hidden_y_offset+y*CELL_HEIGHT,CELL_WIDTH,CELL_HEIGHT); 
	}
	
	private void draw_mouse(int x,int y)
	{
		hidden_graphics.drawImage(IMAGE_MOUSE,hidden_x_offset+x*CELL_WIDTH,hidden_y_offset+y*CELL_HEIGHT,this);   
	}
	
	private void draw_wall(int x,int y)
	{
		hidden_graphics.drawImage(IMAGE_WALL,hidden_x_offset+x*CELL_WIDTH,hidden_y_offset+y*CELL_HEIGHT,this);   
	}

	private void draw_head(int x,int y)
	{
		switch(current_head_direction)
		{
			case DIRECTION_DOWN :	hidden_graphics.drawImage(IMAGE_HEAD_D,hidden_x_offset+x*CELL_WIDTH,hidden_y_offset+y*CELL_HEIGHT,this);break;
			case DIRECTION_UP   :	hidden_graphics.drawImage(IMAGE_HEAD_U,hidden_x_offset+x*CELL_WIDTH,hidden_y_offset+y*CELL_HEIGHT,this);break;
			case DIRECTION_LEFT :	hidden_graphics.drawImage(IMAGE_HEAD_L,hidden_x_offset+x*CELL_WIDTH,hidden_y_offset+y*CELL_HEIGHT,this);break;
			case DIRECTION_RIGHT:	hidden_graphics.drawImage(IMAGE_HEAD_R,hidden_x_offset+x*CELL_WIDTH,hidden_y_offset+y*CELL_HEIGHT,this);break;
		}
	}
	
	private void draw_tail(int x,int y)
	{
		switch(current_tail_direction)
		{
			case DIRECTION_DOWN :	hidden_graphics.drawImage(IMAGE_TAIL_D,hidden_x_offset+x*CELL_WIDTH,hidden_y_offset+y*CELL_HEIGHT,this);break;
			case DIRECTION_UP   :	hidden_graphics.drawImage(IMAGE_TAIL_U,hidden_x_offset+x*CELL_WIDTH,hidden_y_offset+y*CELL_HEIGHT,this);break;
			case DIRECTION_LEFT :	hidden_graphics.drawImage(IMAGE_TAIL_L,hidden_x_offset+x*CELL_WIDTH,hidden_y_offset+y*CELL_HEIGHT,this);break;
			case DIRECTION_RIGHT:	hidden_graphics.drawImage(IMAGE_TAIL_R,hidden_x_offset+x*CELL_WIDTH,hidden_y_offset+y*CELL_HEIGHT,this);break;
		}
	}

	private void draw_body(int x,int y)
	{
		int ldir = getDirection(x,y);
		
		switch (getSpecial(x,y))
		{
			case SPECIAL_NONE : {
									switch(ldir)
									{
										case DIRECTION_DOWN :	hidden_graphics.drawImage(IMAGE_BODYVERT,hidden_x_offset+x*CELL_WIDTH,hidden_y_offset+y*CELL_HEIGHT,this);break;
										case DIRECTION_UP   :	hidden_graphics.drawImage(IMAGE_BODYVERT,hidden_x_offset+x*CELL_WIDTH,hidden_y_offset+y*CELL_HEIGHT,this);break;
										case DIRECTION_LEFT :	hidden_graphics.drawImage(IMAGE_BODYHRZ,hidden_x_offset+x*CELL_WIDTH,hidden_y_offset+y*CELL_HEIGHT,this);break;
										case DIRECTION_RIGHT:	hidden_graphics.drawImage(IMAGE_BODYHRZ,hidden_x_offset+x*CELL_WIDTH,hidden_y_offset+y*CELL_HEIGHT,this);break;
									}
								};break;
			case SPECIAL_LD   : {
									hidden_graphics.drawImage(IMAGE_BODYLD,hidden_x_offset+x*CELL_WIDTH,hidden_y_offset+y*CELL_HEIGHT,this);
								};break;
			case SPECIAL_RD   : {
									hidden_graphics.drawImage(IMAGE_BODYRD,hidden_x_offset+x*CELL_WIDTH,hidden_y_offset+y*CELL_HEIGHT,this);
								};break;
			case SPECIAL_LU   : {
									hidden_graphics.drawImage(IMAGE_BODYLU,hidden_x_offset+x*CELL_WIDTH,hidden_y_offset+y*CELL_HEIGHT,this);
								};break;
			case SPECIAL_RU   : {
									hidden_graphics.drawImage(IMAGE_BODYRU,hidden_x_offset+x*CELL_WIDTH,hidden_y_offset+y*CELL_HEIGHT,this);
								};break;
		
		}
	}

	private void update_hidden_scores_buffer()
	{
		hidden_scores_graphics.setColor(background_color);
		hidden_scores_graphics.fillRect(0,0,HIDDEN_SCORES_WIDTH,HIDDEN_SCORES_HEIGHT);
		
		int ly = 20;
		// Выводим рисунок со стрелками 
		hidden_scores_graphics.drawImage(ARROW_IMAGE,arrow_point.x,arrow_point.y,this);    
		ly+=ARROW_IMAGE.getHeight(this)+20;
		
		String ltstr = null;
		
		hidden_scores_graphics.setColor(Color.white);
		if (game_mode==GAME_RUN)
		{
			long razn = System.currentTimeMillis()-start_time;
			java.util.Date dt = new java.util.Date(razn);
			String minutes = String.valueOf(dt.getMinutes());
			if (minutes.length()==1) minutes = "0"+minutes; 
			String seconds = String.valueOf(dt.getSeconds());
			if (seconds.length()==1) seconds = "0"+seconds; 
			ltstr = minutes+":"+seconds; 
			dt = null;
		}
		else
		{
//			if ((game_mode==GAME_STOP)||(game_mode==GAME_WAIT)) 
//			{
			if (start_time!=0)
			{
				long razn = end_time-start_time;
				java.util.Date dt = new java.util.Date(razn);
				String minutes = String.valueOf(dt.getMinutes());
				if (minutes.length()==1) minutes = "0"+minutes; 
				String seconds = String.valueOf(dt.getSeconds());
				if (seconds.length()==1) seconds = "0"+seconds; 
				ltstr = minutes+":"+seconds;
				dt = null;
			}
			else
			{
				long razn = end_time;
				java.util.Date dt = new java.util.Date(razn);
				String minutes = String.valueOf(dt.getMinutes());
				if (minutes.length()==1) minutes = "0"+minutes; 
				String seconds = String.valueOf(dt.getSeconds());
				if (seconds.length()==1) seconds = "0"+seconds; 
				ltstr = minutes+":"+seconds;
				dt = null;
			}
//			else
//			ltstr = "00:00"; 
		}

		// Выводим кнопку TIME
		hidden_scores_graphics.drawImage(TIME_IMAGE,(hidden_scores_image.getWidth(this)-TIME_IMAGE.getWidth(this))/2,ly,this);    
		ly +=TIME_IMAGE.getHeight(this)+25;
		int llx = (hidden_scores_image.getWidth(this)-hidden_scores_graphics.getFontMetrics().stringWidth(ltstr))/2;   
		int lly = (25-hidden_scores_graphics.getFontMetrics().getHeight())/2;   
		hidden_scores_graphics.drawString(ltstr,llx,ly-lly-2);  
		
		// Выводим кнопку SCORE
		ltstr = Integer.toString(current_scores);	
		hidden_scores_graphics.drawImage(SCORE_IMAGE,(hidden_scores_image.getWidth(this)-SCORE_IMAGE.getWidth(this))/2,ly,this);    
		ly +=SCORE_IMAGE.getHeight(this)+20;
		llx = (hidden_scores_image.getWidth(this)-hidden_scores_graphics.getFontMetrics().stringWidth(ltstr))/2;   
		lly = (25-hidden_scores_graphics.getFontMetrics().getHeight())/2;   
		hidden_scores_graphics.drawString(ltstr,llx,ly-lly-2);  
		
	}
	
	private void UpdateFullBuffer()
	{
		hidden_graphics.setColor(this.background_color);
		hidden_graphics.fillRect(0,0,HIDDEN_BUFFER_WIDTH,HIDDEN_BUFFER_HEIGHT);
		
		DrawFrame(hidden_graphics,0,0,hidden_image.getWidth(this),hidden_image.getHeight(this));
		for (int lx=0;lx<WIDTH_GAME_FIELD;lx++)
			for (int ly=0;ly<HEIGHT_GAME_FIELD;ly++)
			{
				switch (getElement(lx,ly))
				{
					case PYTHON_EMPTY :	{
											draw_empty(lx,ly); 
										}; break;
					case PYTHON_WALL  : {
											draw_wall(lx,ly);
										}; break;
					case PYTHON_MOUSE : {
											draw_mouse(lx,ly); 
										}; break;
					case PYTHON_HEAD  : {
											draw_head(lx,ly);
										}; break;
					case PYTHON_TAIL  : {
											draw_tail(lx,ly);
										}; break;
					case PYTHON_BODY  : {
											draw_body(lx,ly); 
										}; break;
				}
			}
		
	  DrawFrame(hidden_graphics,0,0,hidden_image.getWidth(this),hidden_image.getHeight(this));
	  repaint();
	  
	  try
	  {
		Thread.sleep(100);   
	  }
	  catch(InterruptedException ee)
	  {}
	}
	
	private int game_proceed()
	{
		if (reset_flag) return GAMEOVER_RESET;
		
		old_head_direction = current_head_direction; 
		current_head_direction = buffer_head_direction;

		int old_headxposition = head_xposition; 
		int old_headyposition = head_yposition;
		setElement(head_xposition,head_yposition,PYTHON_BODY); 
		
		switch (current_head_direction)
		{
			case DIRECTION_DOWN :{ 
									if (getElement(head_xposition,head_yposition+1)!=PYTHON_WALL)
									{
										head_yposition++;
									}
									else
									{
										if ((getElement(head_xposition+1,head_yposition)!=PYTHON_WALL)&&(getElement(head_xposition+1,head_yposition)!=PYTHON_BODY))   
										{
											current_head_direction = DIRECTION_RIGHT;
											buffer_head_direction = DIRECTION_RIGHT;
											head_xposition++;
										}
										else
										{
											if ((getElement(head_xposition-1,head_yposition)!=PYTHON_WALL)&&(getElement(head_xposition-1,head_yposition)!=PYTHON_BODY))   										
											{
												current_head_direction = DIRECTION_LEFT;
												buffer_head_direction = DIRECTION_LEFT;
												head_xposition--;
											}
											else
											{
												return GAMEOVER_BAD; 
											}
										}
									}
								 };break;
			case DIRECTION_UP   :{
									if (getElement(head_xposition,head_yposition-1)!=PYTHON_WALL)
									{
										head_yposition--;
									}
									else
									{
										if ((getElement(head_xposition-1,head_yposition)!=PYTHON_WALL)&&(getElement(head_xposition-1,head_yposition)!=PYTHON_BODY))   
										{
											current_head_direction = DIRECTION_LEFT;
											buffer_head_direction = DIRECTION_LEFT;
											head_xposition--;
										}
										else
										{
											if ((getElement(head_xposition+1,head_yposition)!=PYTHON_WALL)&&(getElement(head_xposition+1,head_yposition)!=PYTHON_BODY))   										
											{
												current_head_direction = DIRECTION_RIGHT;
												buffer_head_direction = DIRECTION_RIGHT;
												head_xposition++;	
											}
											else
											{
												return GAMEOVER_BAD; 
											}
										}
									}
								 };break;
			case DIRECTION_LEFT:{
									if (getElement(head_xposition-1,head_yposition)!=PYTHON_WALL)
									{
										head_xposition--;
									}
									else
									{
										if ((getElement(head_xposition,head_yposition-1)!=PYTHON_WALL)&&(getElement(head_xposition,head_yposition-1)!=PYTHON_BODY))   
										{
											current_head_direction = DIRECTION_UP;
											buffer_head_direction = DIRECTION_UP;
											head_yposition--;
										}
										else
										{
											if ((getElement(head_xposition,head_yposition+1)!=PYTHON_WALL)&&(getElement(head_xposition,head_yposition+1)!=PYTHON_BODY))   										
											{
												current_head_direction = DIRECTION_DOWN;
												buffer_head_direction = DIRECTION_DOWN;
												head_yposition++; 
											}
											else
											{
												return GAMEOVER_BAD; 	
											}
										}
									}
								 };break;
			case DIRECTION_RIGHT :{
									if (getElement(head_xposition+1,head_yposition)!=PYTHON_WALL)
									{
										head_xposition++;
									}
									else
									{
										if ((getElement(head_xposition,head_yposition-1)!=PYTHON_WALL)&&(getElement(head_xposition,head_yposition-1)!=PYTHON_BODY))   
										{
											current_head_direction = DIRECTION_UP;
											buffer_head_direction = DIRECTION_UP;
											head_yposition--; 
										}
										else
										{
											if ((getElement(head_xposition,head_yposition+1)!=PYTHON_WALL)&&(getElement(head_xposition,head_yposition+1)!=PYTHON_BODY))   										
											{
												current_head_direction = DIRECTION_DOWN;
												buffer_head_direction = DIRECTION_DOWN;
												head_yposition++; 
											}
											else
											{
												return GAMEOVER_BAD; 	
											}
										}
									}
								 };break;
		}
		setDirection(old_headxposition,old_headyposition,current_head_direction); 

		
		if (old_head_direction!=current_head_direction)
		{
			switch(old_head_direction)
			{
				case DIRECTION_DOWN :{
										 switch(current_head_direction)
										 {
											case DIRECTION_LEFT : setSpecial(old_headxposition,old_headyposition,SPECIAL_LU); break;
											case DIRECTION_RIGHT: setSpecial(old_headxposition,old_headyposition,SPECIAL_RU); break;
										 }
									 };break;
				case DIRECTION_LEFT :{
										 switch(current_head_direction)
										 {
											case DIRECTION_DOWN : setSpecial(old_headxposition,old_headyposition,SPECIAL_RD); break;
											case DIRECTION_UP :	  setSpecial(old_headxposition,old_headyposition,SPECIAL_RU); break;
										 }
									 };break;
				case DIRECTION_RIGHT:{
										 switch(current_head_direction)
										 {
											case DIRECTION_DOWN : setSpecial(old_headxposition,old_headyposition,SPECIAL_LD); break;
											case DIRECTION_UP :   setSpecial(old_headxposition,old_headyposition,SPECIAL_LU); break;
										 }
									 };break;
				case DIRECTION_UP   :{
										 switch(current_head_direction)
										 {
											case DIRECTION_LEFT : setSpecial(old_headxposition,old_headyposition,SPECIAL_LD); break;
											case DIRECTION_RIGHT : setSpecial(old_headxposition,old_headyposition,SPECIAL_RD); break;
										 }
									 };break;
			}
		}
		else
		{
			setSpecial(old_headxposition,old_headyposition,SPECIAL_NONE);   	
		}
		
		old_head_direction = current_head_direction; 
		
		if (getElement(head_xposition,head_yposition)==PYTHON_BODY) 
			return GAMEOVER_BAD;
		
		if (getElement(head_xposition,head_yposition)!=PYTHON_MOUSE)
		{
			setElement(tail_xposition,tail_yposition,PYTHON_EMPTY); 
			switch (current_tail_direction)
			{
				case DIRECTION_DOWN : {
										tail_yposition++; 
									  };break;
				case DIRECTION_LEFT : {
										tail_xposition--; 
									  };break;
				case DIRECTION_RIGHT: {
										tail_xposition++; 
									  };break;
				case DIRECTION_UP   : {
										tail_yposition--; 
									  };break;
			}
			current_tail_direction = getDirection(tail_xposition,tail_yposition);   
		}
		else
		{
			current_scores++; 	
			if (current_scores==MOUSE_COUNT) return GAMEOVER_OK;   
		}
		if ((tail_xposition==head_xposition)&&(tail_yposition==head_yposition)) 
			return GAMEOVER_BAD; 
		setElement(tail_xposition,tail_yposition,PYTHON_TAIL);
		setElement(head_xposition,head_yposition,PYTHON_HEAD);
		return GAMEOVER_NONE; 
	}
	
	public boolean gotFocus( Event evt, Object what ) 
	{
		if (game_mode==GAME_PAUSE) game_mode = GAME_RUN;
		return super.gotFocus(evt,what);
	}
	
	public boolean lostFocus(Event evt,Object what)
	{
		if (game_mode==GAME_RUN) game_mode=GAME_PAUSE;  	
		return super.lostFocus(evt,what);
	}
	
	public void run()
	{
		repaint(); 

		// Загружаем переменные
		String var_html = null;

		var_html = getParameter("MOUSES");
		if (var_html!=null)
		{
			try
			{
				MOUSE_COUNT = Math.abs(new Integer(var_html).intValue()); 
				if (MOUSE_COUNT>80) MOUSE_COUNT=80;
			}
			catch (Exception e)
			{ 
				MOUSE_COUNT = 80;
			}
		}

		var_html = getParameter("WALLS");
		if (var_html!=null)
		{
			try
			{
				WALL_COUNT = new Integer(var_html).intValue(); 
				if (WALL_COUNT>50) WALL_COUNT=50;
			}
			catch (Exception e)
			{
				WALL_COUNT=50;
			}
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
			background_color = stringToColor("0A0063"); 
		}

		images_placement = getParameter("IMAGES");
		if (images_placement==null)
		{
			images_placement = "";
		}

		game_field_array = new int[WIDTH_GAME_FIELD][HEIGHT_GAME_FIELD];
		CELL_WIDTH = HIDDEN_BUFFER_WIDTH / WIDTH_GAME_FIELD;
		CELL_HEIGHT = HIDDEN_BUFFER_HEIGHT / HEIGHT_GAME_FIELD;
		
		bottom_image_graphics.setColor(background_color);
		bottom_image_graphics.fillRect(0,0,bottom_image.getWidth(this),bottom_image.getHeight(this)); 
		
		// Loading of outside images
		try
		{
			RESET_IMAGE = this.getImage(new URL(getCodeBase(),images_placement+"restart.gif"));
			ARROW_IMAGE = this.getImage(new URL(getCodeBase(),images_placement+"buttons.gif"));
			SCORE_IMAGE = this.getImage(new URL(getCodeBase(),images_placement+"score.gif"));
			TIME_IMAGE  = this.getImage(new URL(getCodeBase(),images_placement+"time.gif"));
			FULLFRAME_IMAGE = this.getImage(new URL(getCodeBase(),images_placement+"fullframe.gif"));
		}
		catch (Exception ee) { return; }
		mtr.addImage(RESET_IMAGE,0);
		mtr.addImage(ARROW_IMAGE,1);
		mtr.addImage(SCORE_IMAGE,2);
		mtr.addImage(TIME_IMAGE ,3);
		mtr.addImage(FULLFRAME_IMAGE ,4);
		
		error_resource = images_placement;
	    try
		{
			mtr.waitForID(0);
			if (mtr.isErrorID(0)) 
			{	
				error_resource += "restart.gif";
				throw new InterruptedException();
			}
			mtr.waitForID(1);
			if (mtr.isErrorID(1)) 
			{	
				error_resource += "buttons.gif";
				throw new InterruptedException();
			}
			mtr.waitForID(2);
			if (mtr.isErrorID(2)) 
			{	
				error_resource += "score.gif";
				throw new InterruptedException();
			}
			mtr.waitForID(3);
			if (mtr.isErrorID(3)) 
			{	
				error_resource += "time.gif";
				throw new InterruptedException();
			}
			mtr.waitForID(4);
			if (mtr.isErrorID(4)) 
			{	
				error_resource += "fullframe.gif";
				throw new InterruptedException();
			}
		} 
		catch (InterruptedException e) 
		{
			game_mode = GAME_LOADERROR;
		}

		if (game_mode == GAME_LOADERROR)
		{
			paint(this.getGraphics());  
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

		// Вычисляем смещения для кнопок
		arrow_point = new Point((hidden_scores_image.getWidth(this)-ARROW_IMAGE.getWidth(this))/2,20);
		reset_point = new Point((hidden_image.getWidth(this)-RESET_IMAGE.getWidth(this))/2,(bottom_image.getHeight(this)-RESET_IMAGE.getHeight(this))/2); 		
		
		// Заполняем нижний раздел
		bottom_image_graphics.drawImage(RESET_IMAGE,reset_point.x,reset_point.y,this);  
		hidden_scores_graphics.setFont(new Font("Dialog",Font.BOLD,12)); 
		hidden_graphics.setFont(new Font("Dialog",Font.BOLD,12)); 
		
		this.requestFocus(); 
		
		// Игровой цикл
		while (true)
		{
			start_time=0;
			game_mode = GAME_START;
			out_start_paper();
			run_page_init(); 
			current_scores = 0;
			UpdateFullBuffer();
			game_mode=GAME_RUN;
			start_time = System.currentTimeMillis(); 
			int result = 0;
			reset_flag = false;
			while(true)
			{
				while (game_mode==GAME_PAUSE) 
				{
					try
					{
						Thread.sleep(300);
					}
					catch (InterruptedException e)
					{ return; }
				}
				result = game_proceed();
				repaint();
				if (result!=GAMEOVER_NONE) break; 
				try
				{
					Thread.sleep(100); 
				}
				catch (Exception e) { break; }
			}

			if (result == GAMEOVER_RESET) continue; 
			
			game_mode = GAME_ANIMATION; 
			end_time = System.currentTimeMillis(); 

			send_result = SendData(Integer.toString(current_scores));
			if (result==GAMEOVER_BAD) bad_end_of_game();
			else ok_game_end();
		}
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
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return false;
		}
        return true;
	}
	
	public boolean mouseDown (Event e,int x, int y)
	{
		// Проверка попадания на клавишу RESTART
		if (y>HIDDEN_BUFFER_HEIGHT)
		{
			x -= reset_point.x;
			y -= (HIDDEN_BUFFER_HEIGHT+reset_point.y);
			if ((x>=0)&&(x<RESET_IMAGE.getWidth(this)))
			{
				switch (game_mode)
				{
					case GAME_STOP : {
										 game_mode = GAME_START;
									 };break;
					case GAME_RUN : {
										if ((y>=0)&&(y<RESET_IMAGE.getHeight(this)))
										{
											reset_flag = true;
										}
									}; break;
				}
			}
			return true;
		}
		
		// Проверка на попадание в зону управления
		if (x>HIDDEN_BUFFER_WIDTH)
		{
			x -= (HIDDEN_BUFFER_WIDTH+arrow_point.x); 
			if ((x>=0)&&(x<ARROW_IMAGE.getWidth(this)))
			{
				y -= arrow_point.y;
				if ((y>=0)&&(y<ARROW_IMAGE.getHeight(this)))
				{
					int loffx = ARROW_IMAGE.getWidth(this)/3;
					int loffy = ARROW_IMAGE.getHeight(this)/3;
					
					x = x/loffx;
					y = y/loffy;
					
					if ((x==1)&&(y==0)) game_key_proceed(Event.UP);
					else
					if ((x==0)&&(y==1)) game_key_proceed(Event.LEFT);
					else
					if ((x==1)&&(y==2)) game_key_proceed(Event.DOWN);
					else
					if ((x==2)&&(y==1)) game_key_proceed(Event.RIGHT);
				}
			}
		}
		return true;
	}

	private void game_key_proceed(int key)
	{
			switch (key)
			{
				case Event.UP : {
										if (current_head_direction!=DIRECTION_DOWN) 
										buffer_head_direction = DIRECTION_UP; 
								}; break;
				case Event.DOWN : {
										if (current_head_direction!=DIRECTION_UP)
										buffer_head_direction = DIRECTION_DOWN;  
								}; break;
				case Event.LEFT : {
										if (current_head_direction!=DIRECTION_RIGHT)
										buffer_head_direction = DIRECTION_LEFT;
								  }; break;
				case Event.RIGHT: {
										if (current_head_direction!=DIRECTION_LEFT)
										buffer_head_direction = DIRECTION_RIGHT;  
									}; break;
			}
	}
	
	public boolean keyDown(Event evt,int key)
	{
		if (game_mode==GAME_WAIT) return true; 

		switch (game_mode)
		{
			case GAME_START : {
								 switch (key)
								 {
									case Event.ENTER :  game_mode = GAME_WAIT ;break;
								 }
							  };break;
			case GAME_RUN	: {
									game_key_proceed(key);  
							  };break;
			case GAME_STOP  : {
								 game_mode = GAME_START;
							  };break;
		}
		return true;
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

}