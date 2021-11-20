import java.util.*;  
import java.awt.*;
import java.awt.image.*;  
import java.applet.*;

public class klad extends Applet implements Runnable 
{

	private final static int LAB_WIDTH = 28;
	private final static int LAB_HEIGHT = 16;

	private int you_x = 0;
	private int you_y = 0;
	private int enemy_x = 0;
	private int enemy_y = 0;
	
	private int [][] current_labyrinth = new int[LAB_WIDTH][LAB_HEIGHT];
	
	final static byte [] lab0 = 
	{
		2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,
        2,1,5,1,5,1,5,1,1,1,1,16,1,1,1,5,1,1,5,1,5,1,1,1,1,1,1,2,
        2,1,2,2,2,2,2,2,3,2,1,1,1,3,2,2,2,2,2,2,2,2,2,1,1,1,1,2,
        2,1,1,1,1,1,1,1,3,1,1,1,1,3,1,1,1,1,1,1,1,1,1,1,1,1,1,2,
        2,1,1,1,6,1,1,1,3,1,1,1,1,3,1,1,1,5,1,1,1,5,1,1,1,1,1,2,
        2,1,3,2,2,2,2,2,2,2,1,1,2,2,2,2,2,2,2,2,2,2,2,2,3,1,1,2,
        2,1,3,1,30,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,3,1,1,2,
        2,1,3,1,1,1,1,1,1,1,1,7,1,1,1,1,1,1,1,1,1,1,1,1,3,1,1,2,
        2,1,3,1,1,1,3,2,2,2,2,2,2,2,2,2,2,2,2,1,1,1,1,1,3,1,1,2,
        2,1,3,1,1,1,3,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,3,1,1,2,
        2,1,3,1,1,5,3,5,1,1,1,1,1,1,1,1,5,1,5,1,1,5,1,1,3,1,1,2,
        2,2,2,2,2,2,2,2,2,1,1,1,1,1,1,1,2,2,2,2,2,2,2,2,2,1,1,2,
        2,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,2,
        2,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,2,
        2,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,2,
		2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2 
	};

	final static byte [] lab1 = 
	{
		2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,
        2,1,1,1,5,5,1,1,30,1,1,1,1,1,1,1,1,5,5,1,1,1,1,1,1,5,1,2,
        2,1,3,2,2,2,3,1,1,3,2,2,2,3,1,1,3,2,2,2,3,1,1,3,2,2,2,2,
        2,1,3,1,1,1,3,1,1,3,1,1,1,3,1,1,3,1,1,1,3,1,1,3,1,1,1,2,
        2,5,3,1,1,1,3,5,5,3,1,1,1,3,5,5,3,1,1,1,3,5,1,3,1,1,1,2,
        2,2,2,3,1,3,2,2,2,2,3,1,3,2,2,2,2,3,1,3,2,2,2,2,3,1,1,2,
        2,1,1,3,1,3,1,1,1,1,3,1,3,1,1,1,1,3,1,3,16,1,1,1,3,1,1,2,
        2,1,7,3,5,3,1,1,1,1,3,5,3,1,1,1,1,3,1,3,1,1,1,1,3,1,1,2,
        2,1,1,3,2,2,3,1,1,3,2,2,2,3,1,1,3,2,2,2,3,1,1,3,2,2,2,2,
        2,1,1,3,1,1,3,1,1,3,1,1,1,3,1,1,3,1,1,1,3,1,1,3,1,1,1,2,
        2,1,1,3,1,1,3,5,5,3,1,1,1,3,5,5,3,1,1,1,3,5,5,3,1,1,1,2,
        2,5,1,3,1,3,2,2,2,2,1,1,1,2,2,2,2,1,1,1,2,2,2,2,3,1,1,2,
        2,2,2,2,1,3,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,3,1,1,2,
        2,1,1,1,1,3,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,3,1,1,2,
        2,6,1,1,1,3,1,1,1,1,1,1,1,1,1,5,1,1,1,5,1,1,1,1,3,1,1,2,
        2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2
	};

	final static byte [] lab2 = 
	{
		2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,
        2,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,2,
        2,16,5,5,5,1,1,5,1,1,1,1,1,1,5,5,1,1,1,1,1,1,1,1,1,1,1,2,
        2,3,2,2,2,3,2,2,2,3,1,6,1,3,2,2,2,3,1,1,1,3,2,2,2,2,2,2,
        2,3,1,1,1,3,1,1,1,2,2,2,2,2,1,1,1,3,1,1,1,3,1,1,1,1,1,2,
        2,3,1,1,3,3,1,1,1,1,1,2,1,1,1,1,1,3,1,1,1,3,1,1,1,1,30,2,
        2,2,2,3,2,3,1,1,1,1,1,1,1,1,1,1,1,3,2,2,2,2,2,3,1,1,1,2,
        2,1,1,3,1,2,5,1,1,1,1,1,1,1,5,1,3,3,1,1,1,1,1,3,1,1,1,2,
        2,1,1,3,1,1,2,5,1,1,1,1,1,5,3,3,3,3,1,1,1,1,1,3,1,1,1,2,
        2,1,1,3,1,1,1,2,5,1,1,1,5,3,3,2,3,3,1,1,1,1,1,3,1,1,1,2,
        2,1,1,3,1,1,1,1,3,5,5,1,3,3,2,1,3,3,1,1,1,3,2,2,2,2,2,2,
        2,1,1,2,2,2,2,2,3,2,3,1,3,2,1,1,3,1,1,1,1,3,1,1,1,1,1,2,
        2,1,1,1,1,1,1,1,1,1,3,7,3,1,1,1,3,1,1,1,1,3,1,1,1,1,1,2,
        2,1,1,1,1,1,1,1,1,1,2,2,2,1,1,1,2,2,2,2,2,2,2,1,1,1,1,2,
        2,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,2,
        2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2
	};
	
	final static byte [] lab3 = 
	{
		2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,
        2,1,16,1,1,1,5,5,5,5,5,5,5,5,1,1,1,5,5,1,1,1,1,1,5,1,30,2,
        2,1,2,3,2,2,2,2,2,2,3,2,2,2,2,2,2,3,2,2,2,2,3,2,2,2,1,2,
        2,1,5,3,1,1,5,5,1,1,3,1,1,5,1,1,1,3,1,5,5,1,3,1,1,1,1,2,
        2,1,2,2,2,2,3,2,2,2,2,2,2,2,3,2,2,2,2,2,2,2,2,2,2,3,1,2,
        2,1,5,1,1,1,3,1,5,1,5,1,5,1,3,1,1,5,1,1,5,1,1,5,1,3,1,2,
        2,1,3,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,3,2,2,2,2,2,2,2,1,2,
        2,1,3,1,5,1,1,1,1,1,5,5,5,5,5,1,1,1,3,1,5,1,1,1,1,5,1,2,
        2,1,2,2,2,2,2,3,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,3,2,2,1,2,
        2,1,1,5,5,1,1,3,1,1,1,1,1,1,5,1,5,1,5,1,1,1,1,3,1,1,1,2,
        2,1,2,3,2,2,2,2,2,2,2,2,2,2,3,2,2,2,2,2,2,2,2,2,2,2,1,2,
        2,1,5,3,1,1,1,1,5,1,1,1,1,1,3,1,5,5,5,1,1,7,1,1,1,6,1,2,
        2,1,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,3,2,1,2,
        2,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,3,1,1,2,
        2,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,2,2,2,2,
        2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2
	};
	
	final static byte [] lab4 = 
	{
		2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,
        2,30,1,1,1,1,1,1,1,1,1,1,3,3,5,3,5,1,5,1,5,1,1,16,1,1,1,2,
        2,1,1,1,1,3,3,3,3,3,3,3,3,3,2,3,2,3,2,3,2,3,3,3,3,3,3,2,
        2,1,1,1,1,3,1,1,1,3,1,1,2,3,2,3,2,3,2,3,2,1,3,1,1,1,3,2,
        2,1,1,1,1,3,1,1,1,3,1,1,2,2,2,2,2,2,2,2,2,1,3,1,1,1,3,2,
        2,1,1,1,1,3,3,3,3,3,1,1,1,2,2,2,2,2,2,2,1,1,3,1,1,1,3,2,
        2,1,1,1,1,3,1,1,1,3,1,1,1,2,2,2,1,2,2,2,1,1,3,1,1,1,3,2,
        2,1,1,1,1,3,1,1,1,3,1,1,1,2,2,2,1,2,2,2,3,3,3,3,3,3,3,2,
        2,1,1,1,1,3,1,1,1,3,1,1,1,2,2,2,2,2,2,2,3,2,3,2,3,2,3,2,
        2,1,1,1,1,3,1,1,1,3,1,1,1,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,
        2,1,1,1,1,3,3,3,3,3,1,1,1,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,
        2,1,1,1,1,3,1,1,1,3,1,1,1,2,2,2,1,2,2,1,2,2,1,2,2,1,2,2,
        2,6,7,5,5,3,1,1,1,3,1,1,1,2,2,2,1,2,2,1,2,2,1,2,2,1,2,2,
        2,2,2,2,2,3,1,1,1,3,1,1,1,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,
        2,2,2,2,2,3,4,4,4,4,4,4,4,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,
        2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2
	};
	
	final static byte [] lab5 = 
	{
		2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,
        2,1,1,1,1,1,1,1,1,5,5,5,5,5,5,5,5,1,1,1,1,1,1,1,1,30,1,2,
        2,1,1,1,1,1,1,3,2,2,2,2,3,3,2,2,2,2,3,1,1,1,1,1,1,1,1,2,
        2,16,1,1,1,1,1,3,1,1,1,1,3,3,1,1,1,1,3,1,1,1,1,1,1,1,1,2,
        2,1,1,1,1,1,1,3,1,1,1,1,3,3,1,1,1,1,3,1,1,1,1,1,1,1,1,2,
        2,1,5,1,5,5,1,3,1,1,1,1,3,3,1,1,1,1,3,1,5,5,1,5,1,5,1,2,
        2,2,2,2,2,2,2,2,1,1,1,1,3,3,1,1,1,1,2,2,2,2,2,2,2,2,2,2,
        2,1,1,1,1,1,1,1,1,6,5,5,3,3,5,5,1,1,1,1,1,1,1,1,1,1,1,2,
        2,1,1,1,1,1,3,2,2,2,2,2,2,2,2,2,2,2,2,3,1,1,1,1,1,1,1,2,
        2,1,5,5,5,1,3,1,1,1,1,1,1,1,1,1,1,1,1,3,1,5,5,5,5,1,1,2,
        2,1,2,2,2,2,2,2,2,3,1,1,1,1,1,1,3,2,2,2,2,2,2,2,2,2,1,2,
        2,1,1,1,1,1,1,1,1,3,1,1,1,1,1,1,3,1,1,1,1,1,1,1,1,1,1,2,
        2,1,5,5,5,1,5,7,3,2,2,2,2,2,2,2,2,3,1,1,5,1,5,5,5,1,1,2,
        2,2,2,2,2,2,2,2,2,1,1,1,1,1,1,1,1,2,2,2,2,2,2,2,2,2,2,2,
        2,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,2,
        2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2
	};

	final static byte [] lab6 = 
	{
		2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,
        2,16,1,1,1,1,1,1,5,1,5,1,5,1,5,1,5,1,1,1,1,1,1,1,1,1,30,2,
        2,1,3,2,2,3,1,3,2,2,2,2,2,2,2,2,2,2,3,1,3,2,2,3,1,1,1,2,
        2,1,3,1,2,2,2,3,5,1,1,1,1,1,1,1,1,5,3,2,2,2,1,3,1,1,1,2,
        2,5,3,1,1,1,2,2,2,3,5,1,1,1,1,5,3,2,2,2,1,1,1,3,5,3,3,2,
        2,3,3,5,1,1,1,1,2,2,2,3,7,1,3,2,2,2,1,1,1,1,5,3,3,3,3,2,
        2,3,3,3,3,5,1,1,1,1,2,2,2,2,2,2,1,1,1,1,5,3,3,3,3,3,3,2,
        2,3,3,3,3,3,3,5,1,1,1,1,2,2,1,1,1,1,5,3,3,3,3,3,3,3,3,2,
        2,3,3,3,3,3,3,3,3,5,1,1,1,1,1,1,5,3,3,3,3,3,3,3,3,3,3,2,
        2,3,3,3,3,3,3,3,3,2,2,2,2,2,2,2,2,3,3,3,3,3,3,3,3,1,1,2,
        2,3,3,3,3,3,3,3,3,1,5,5,1,6,5,5,1,3,3,3,3,3,3,1,1,1,1,2,
        2,1,1,3,3,3,3,3,3,2,2,2,2,2,2,2,2,3,3,3,3,1,1,1,1,1,1,2,
        2,1,1,1,1,1,1,3,3,1,1,1,2,2,1,1,1,3,3,1,1,1,1,1,1,1,1,2,
        2,1,1,1,1,1,1,1,1,1,1,1,2,2,1,1,1,1,1,1,1,1,1,1,1,1,1,2,
        2,4,4,4,4,4,4,4,4,4,4,4,2,2,4,4,4,4,4,4,4,4,4,4,4,4,4,2,
        2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2
	};
	
	final static byte [] lab7 = 
	{
		2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,
        2,1,1,5,5,5,5,5,5,1,5,1,1,1,1,5,1,1,1,1,30,5,5,1,1,1,1,2,
        2,3,2,2,2,2,2,2,3,2,2,3,1,3,2,2,3,2,2,2,2,2,2,3,3,3,3,2,
        2,3,2,2,1,1,3,3,3,2,2,2,3,3,2,2,3,2,2,2,2,2,2,2,1,1,3,2,
        2,3,2,2,7,1,3,3,3,2,2,2,2,3,2,2,3,1,2,2,1,1,1,1,2,1,3,2,
        2,3,2,2,2,2,3,3,3,2,2,1,2,2,2,2,3,5,2,2,1,1,1,1,2,1,3,2,
        2,3,2,2,2,2,3,3,3,2,2,1,1,2,2,2,3,3,2,2,1,1,1,1,2,1,3,2,
        2,3,2,2,1,1,3,3,1,2,2,1,1,1,2,2,3,5,2,2,1,1,1,1,2,1,3,2,
        2,3,2,2,1,1,3,3,1,2,2,1,1,1,2,2,3,2,2,2,2,2,2,2,1,1,3,2,
        2,3,2,2,2,2,2,2,1,2,2,1,1,1,2,2,3,2,2,2,2,2,2,1,1,1,3,2,
        2,3,1,1,1,5,5,5,1,1,6,1,5,5,5,1,3,1,1,1,1,1,5,5,5,1,3,2,
        2,2,3,2,2,2,2,2,2,2,2,2,2,2,2,2,3,1,1,1,3,2,2,2,2,2,2,2,
        2,1,3,16,5,5,5,5,5,5,5,5,5,1,1,1,3,1,1,1,3,1,1,1,1,1,1,2,
        2,1,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,1,1,1,1,1,1,2,
        2,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,2,
        2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2
	};
	
	final static int KLAD_EMPTY = 1;
	final static int KLAD_WALL = 2;
	final static int KLAD_YOU  = 16;
	final static int KLAD_WATER  = 4;
	final static int KLAD_LADDER  = 3;
	final static int KLAD_TREASURE = 5;
	final static int KLAD_ENEMY  = 30;
	final static int KLAD_EXIT  = 6;
	final static int KLAD_KEY = 7;
	
	public final static int LABYRINTH_COUNT = 8;

	private final Object [] labyrinth_list = { lab0,lab1,lab2,lab3,lab4,lab5,lab6,lab7 }; 

// Спрайты
	private static final int SPRITE_WIDTH = 8;
	private static final int SPRITE_HEIGHT = 8;
	
	private final static int C_R = Color.red.getRGB();
	private final static int C_B = Color.black.getRGB();
	private final static int C_Y = Color.yellow.getRGB();
	private final static int C_U = Color.blue.getRGB();
	private final static int C_C = Color.cyan.getRGB();
	private final static int C_G = Color.green.getRGB();
	private final static int C_W = Color.white.getRGB();
	private final static int C_O = Color.orange.getRGB();
	
	private final int [] A_SPRITE_EXIT = {
															C_B,C_B,C_B,C_B,C_B,C_B,C_B,C_B,
															C_B,C_U,C_U,C_U,C_U,C_U,C_B,C_B,
															C_U,C_U,C_Y,C_U,C_Y,C_U,C_U,C_B,
															C_U,C_Y,C_Y,C_U,C_Y,C_Y,C_U,C_B,
															C_U,C_Y,C_B,C_U,C_B,C_Y,C_U,C_B,
															C_U,C_Y,C_Y,C_U,C_Y,C_Y,C_U,C_B,
															C_U,C_Y,C_Y,C_U,C_Y,C_Y,C_U,C_B,
															C_U,C_U,C_U,C_U,C_U,C_U,C_U,C_B
														  };

	private Image SPRITE_EXIT  = createImage(new MemoryImageSource(SPRITE_WIDTH,SPRITE_HEIGHT,A_SPRITE_EXIT,0,SPRITE_WIDTH));
	
	private final int [] A_SPRITE_KEY = {
															C_B,C_B,C_B,C_B,C_B,C_B,C_B,C_B,
															C_B,C_B,C_B,C_B,C_B,C_B,C_B,C_B,
															C_B,C_B,C_B,C_B,C_B,C_B,C_B,C_B,
															C_B,C_B,C_Y,C_B,C_B,C_B,C_B,C_B,
															C_B,C_Y,C_B,C_Y,C_Y,C_Y,C_Y,C_B,
															C_B,C_B,C_Y,C_B,C_B,C_B,C_Y,C_B,
															C_B,C_B,C_B,C_B,C_B,C_B,C_B,C_B,
															C_B,C_B,C_B,C_B,C_B,C_B,C_B,C_B
														  };

	private Image SPRITE_KEY  = createImage(new MemoryImageSource(SPRITE_WIDTH,SPRITE_HEIGHT,A_SPRITE_KEY,0,SPRITE_WIDTH));
	
	private final int [] A_SPRITE_WALL = {
																C_R,C_R,C_R,C_B,C_R,C_R,C_R,C_R,
																C_R,C_R,C_R,C_B,C_R,C_R,C_R,C_R,
																C_R,C_R,C_R,C_B,C_R,C_R,C_R,C_R,
																C_B,C_B,C_B,C_B,C_B,C_B,C_B,C_B,
																C_R,C_B,C_R,C_R,C_R,C_R,C_R,C_R,
																C_R,C_B,C_R,C_R,C_R,C_R,C_R,C_R,
																C_R,C_B,C_R,C_R,C_R,C_R,C_R,C_R,
																C_B,C_B,C_B,C_B,C_B,C_B,C_B,C_B
															};

	private Image SPRITE_WALL = createImage(new MemoryImageSource(SPRITE_WIDTH,SPRITE_HEIGHT,A_SPRITE_WALL,0,SPRITE_WIDTH));	
	
	private final int [] A_SPRITE_LADDER = {
																C_B,C_Y,C_B,C_B,C_B,C_B,C_Y,C_B,
																C_B,C_Y,C_Y,C_Y,C_Y,C_Y,C_Y,C_B,
																C_B,C_Y,C_B,C_B,C_B,C_B,C_Y,C_B,
																C_B,C_Y,C_B,C_B,C_B,C_B,C_Y,C_B,
																C_B,C_Y,C_B,C_B,C_B,C_B,C_Y,C_B,
																C_B,C_Y,C_Y,C_Y,C_Y,C_Y,C_Y,C_B,
																C_B,C_Y,C_B,C_B,C_B,C_B,C_Y,C_B,
																C_B,C_Y,C_B,C_B,C_B,C_B,C_Y,C_B
															};

	private Image SPRITE_LADDER  = createImage(new MemoryImageSource(SPRITE_WIDTH,SPRITE_HEIGHT,A_SPRITE_LADDER,0,SPRITE_WIDTH));
	
	private final int [] A_SPRITE_TREASURE = {
																C_B,C_B,C_B,C_B,C_B,C_B,C_B,C_B,
																C_B,C_B,C_B,C_B,C_B,C_B,C_B,C_B,
																C_B,C_B,C_B,C_R,C_R,C_R,C_R,C_B,
																C_B,C_B,C_R,C_Y,C_Y,C_R,C_R,C_B,
																C_B,C_R,C_R,C_R,C_R,C_Y,C_R,C_B,
																C_B,C_R,C_Y,C_Y,C_R,C_Y,C_R,C_B,
																C_B,C_R,C_Y,C_Y,C_R,C_C,C_B,C_B,
																C_B,C_R,C_R,C_R,C_R,C_B,C_B,C_B
															};
	
	private Image SPRITE_TREASURE  = createImage(new MemoryImageSource(SPRITE_WIDTH,SPRITE_HEIGHT,A_SPRITE_TREASURE,0,SPRITE_WIDTH));
	
	private final int [] A_SPRITE_WATER = {
																C_B,C_B,C_B,C_B,C_B,C_B,C_B,C_B,
																C_B,C_B,C_B,C_B,C_B,C_B,C_B,C_B,
																C_B,C_B,C_U,C_B,C_B,C_B,C_U,C_B,
																C_B,C_U,C_U,C_U,C_B,C_U,C_U,C_U,
																C_U,C_U,C_U,C_U,C_U,C_U,C_U,C_U,
																C_U,C_W,C_U,C_U,C_U,C_W,C_U,C_U,
																C_U,C_U,C_U,C_U,C_U,C_U,C_U,C_U,
																C_U,C_U,C_U,C_W,C_U,C_U,C_U,C_W
															};
	
	private Image SPRITE_WATER  = createImage(new MemoryImageSource(SPRITE_WIDTH,SPRITE_HEIGHT,A_SPRITE_WATER,0,SPRITE_WIDTH));
	
	private Image hidden_image = null;
	private Graphics hidden_graphics = null;
	
	private int APPLET_WIDTH = 0;
	private int APPLET_HEIGHT = 0;
	
	private void fill_current_labyrinth(int number)
	{
		for(int ly=0;ly<LAB_HEIGHT;ly++)
			for(int lx=0;lx<LAB_WIDTH;lx++)
			{
				current_labyrinth[lx][ly] = ((byte[])(labyrinth_list[number]))[ly*LAB_WIDTH+lx]; 
			}
	}
	
	private void update_hidden_image()
	{
		hidden_graphics.setColor(Color.black); 
		hidden_graphics.fillRect(0,0,APPLET_WIDTH,APPLET_HEIGHT);  

		for(int ly=0;ly<LAB_HEIGHT;ly++)
			for(int lx=0;lx<LAB_WIDTH;lx++)
			{
				switch (current_labyrinth[lx][ly])
				{
					case KLAD_EMPTY :		; break;
					case KLAD_EXIT	  :	{	
														hidden_graphics.drawImage(SPRITE_EXIT,lx*SPRITE_WIDTH,ly*SPRITE_HEIGHT,this);   
													}; break;
					case KLAD_KEY :		{	
														hidden_graphics.drawImage(SPRITE_KEY,lx*SPRITE_WIDTH,ly*SPRITE_HEIGHT,this);
													}; break;
					case KLAD_LADDER :{   
														hidden_graphics.drawImage(SPRITE_LADDER,lx*SPRITE_WIDTH,ly*SPRITE_HEIGHT,this);   
													}; break;
					case KLAD_TREASURE  :	{
												hidden_graphics.drawImage(SPRITE_TREASURE,lx*SPRITE_WIDTH,ly*SPRITE_HEIGHT,this);   
												}; break;
					case KLAD_WALL :{		
													hidden_graphics.drawImage(SPRITE_WALL,lx*SPRITE_WIDTH,ly*SPRITE_HEIGHT,this);   
												}; break;
					case KLAD_WATER :{	
													hidden_graphics.drawImage(SPRITE_WATER,lx*SPRITE_WIDTH,ly*SPRITE_HEIGHT,this);   
												}; break;
				}
			}
	}
	
	public void init()
	{
		super.requestFocus(); 
		
		this.resize(448,300);
		APPLET_WIDTH = 448;  
		APPLET_HEIGHT = 256; 

		hidden_image = createImage(LAB_WIDTH*SPRITE_WIDTH,LAB_HEIGHT*SPRITE_HEIGHT);
		hidden_graphics = hidden_image.getGraphics(); 
		fill_current_labyrinth(0); 
		repaint();
	}

	public void paint(Graphics g)
	{
		Graphics lgr = this.getGraphics(); 
		lgr.setColor(Color.darkGray);
		lgr.fillRect(0,256,APPLET_WIDTH,44);   
		for(int li=0;li<3;li++) lgr.draw3DRect(li,256+li,APPLET_WIDTH-li*2,44-li*2,true);   
		this.getGraphics().drawImage(hidden_image,0,0, APPLET_WIDTH,APPLET_HEIGHT,this );   
	}
	
	public void update(Graphics g)
	{
		update_hidden_image();
		paint(g);
	}
	
	public void run()
	{
		
	}
}
