import java.awt.*;
import java.applet.*;
import java.util.*;

public class SternTangram extends Applet implements Runnable 
{
	private	Random rn = new Random(System.currentTimeMillis());

	class GameObject
	{
		private int turn = 0; // угол поворота элемента
		private Polygon main_shape = null; // точки определяющие полигон
		private Polygon rot_shape = null; // повернутый полигон
		
		private Rectangle circumscribe_rectangle = null;

		public int rotate_angle = 0;
		public int x = 0;
		public int y = 0;

		private boolean main_figure = false; // Флаг заполняемой фигуры
		
		private Color color = Color.red; // Цвет полигона 
	
		public void SetRotateAngle (int angle)
		{
			this.rotate_angle = angle;
			Polygon lp = new Polygon();
			for (int li=0;li<main_shape.npoints;li++)
			{
				int lx = (int)((main_shape.xpoints[li]*Math.cos(Math.PI*angle/180))-(main_shape.ypoints[li]*Math.sin(Math.PI*angle/180)));
				int ly = (int)((main_shape.xpoints[li]*Math.sin(Math.PI*angle/180))+(main_shape.ypoints[li]*Math.cos(Math.PI*angle/180)));
				
				lp.addPoint(lx,ly); 
			}
			
			main_shape = lp;
			
			circumscribe_rectangle = getRectangle(); 
		}

		public GameObject(Polygon pl,boolean mn)
		{
			main_shape = pl;
			SetRotateAngle(0);
			main_figure = mn;
		}

		public GameObject(Polygon pl)
		{
			main_shape = pl;
			SetRotateAngle((int)((35*rn.nextDouble()))*10);
		}

		public void SetXYPosition(int x,int y)
		{
			this.x = x;
			this.y = y;
			
			Polygon lp = new Polygon(); 
			for(int li=0;li<main_shape.npoints;li++) lp.addPoint(main_shape.xpoints[li]+x,main_shape.ypoints[li]+y);			
			rot_shape = lp;
		}
		
		public Rectangle getCRectangle()
		{
			return circumscribe_rectangle;	
		}
		
		public void setColor(Color c)
		{
			this.color = c;
		}

		// Возвращает описывающий прямоугольник для повернутой фигуры
		private Rectangle getRectangle()
		{
			int lminx=0;
			int lminy=0;
			int lmaxx=0;
			int lmaxy=0;
			for(int li=0;li<main_shape.npoints;li++)
			{
				if (lminx>main_shape.xpoints[li]) lminx = main_shape.xpoints[li];
				if (lmaxx<main_shape.xpoints[li]) lmaxx = main_shape.xpoints[li];
				if (lminy>main_shape.ypoints[li]) lminy = main_shape.ypoints[li];
				if (lmaxy<main_shape.ypoints[li]) lmaxy = main_shape.ypoints[li];
			}
			
			return new Rectangle(lminx,lminy,lmaxx-lminx,lmaxy-lminy);
 		}
		
		public void DrawPolygon (Graphics g)
		{
			g.setColor(color); 
			g.fillPolygon(rot_shape); 
			Color hid_color = new Color(color.getRed()/3,color.getGreen()/3,color.getBlue()/3);
			Color fl_color = new Color(color.getRed()/2,color.getGreen()/2,color.getBlue()/2);
			int lb = rot_shape.npoints/2; 
			int lx1 = rot_shape.xpoints[0];
			int ly1 = rot_shape.ypoints[0];
			int lx2 = 0;
			int ly2 = 0;
			
			if (main_figure)
			{
				g.setColor(Color.white);
				g.drawPolygon(rot_shape);
				
				g.setColor(Color.yellow);  
				for(int li=0;li<rot_shape.npoints;li++)
				{
					g.drawOval(rot_shape.xpoints[li]-3,rot_shape.ypoints[li]-3,6,6);
				}
			}
			else
			{
				g.setColor(Color.darkGray);
				g.drawPolygon(rot_shape); 
			}
		}
	
	}

	private long time_start = 0; // Стартовое время
	private long time_end = 0;   // Время конца игры
	
	private GameObject main_figure = null; // Заполняемая фигура
	private Image hidden_image = null;
	private Graphics hidden_graphics = null;

	private static final int table_width = 250; // Ширина стола с фигурами
	private static final int control_height = 30; // Высота контрольной панели
	
	private int center_field_x = 0; // Центр игрового стола
	private int center_field_y = 0; // Центр игрового стола
	private Vector game_objects = null; // Массив игровых фигур
	
	public void init()
	{
		Thread thr = new Thread(this);
		
		hidden_image = createImage(this.getSize().width,this.getSize().height);
		hidden_graphics = hidden_image.getGraphics();  
		hidden_graphics.setFont(new Font("Dialog",Font.BOLD,12)); 
		game_objects = new Vector(); 
		
		// Задаем заполняемую фигуру
		Polygon m_figure = null;
		m_figure = new Polygon();
		m_figure.addPoint(-106,-29); //1
		m_figure.addPoint(-56,-29); //2
		m_figure.addPoint(-25,-29);  //3
		m_figure.addPoint(0,-106);   //4
		m_figure.addPoint(25,-29);   //5
		m_figure.addPoint(56,-29);   //6
		m_figure.addPoint(106,-29);   //7
		m_figure.addPoint(40,29);   //8
		m_figure.addPoint(56,106);   //9
		m_figure.addPoint(25,79);   //10
		m_figure.addPoint(0,58);   //11
		m_figure.addPoint(-25,79);   //12
		m_figure.addPoint(-56,106);   //13
		m_figure.addPoint(-40,29);   //14
		
		main_figure = new GameObject(m_figure,true);
		main_figure.setColor(Color.black); 

		// Задаем заполняющие фигуры

		// 3 левых фигуры
		
		GameObject lgb = null;		
		m_figure = new Polygon();
		m_figure.addPoint(-35,-35);
		m_figure.addPoint(-35,35);
		m_figure.addPoint(35,35);
		m_figure.addPoint(35,-35);
		lgb = new GameObject(m_figure);
		lgb.setColor(Color.green);
		lgb.SetXYPosition((int)(rn.nextDouble()*200+table_width+20),(int)(rn.nextDouble()*200+20));
		game_objects.addElement(lgb); 

		m_figure = new Polygon();
		m_figure.addPoint(-15,-15);
		m_figure.addPoint(-15,15);
		m_figure.addPoint(15,15);
		m_figure.addPoint(15,-15);
		lgb = new GameObject(m_figure);
		lgb.setColor(Color.blue);
		lgb.SetXYPosition((int)(rn.nextDouble()*200+table_width+20),(int)(rn.nextDouble()*200+20));
		game_objects.addElement(lgb); 

		m_figure = new Polygon();
		m_figure.addPoint(-25,-25);
		m_figure.addPoint(-25,25);
		m_figure.addPoint(25,25);
		m_figure.addPoint(25,-25);
		lgb = new GameObject(m_figure);
		lgb.setColor(Color.red);
		lgb.SetXYPosition((int)(rn.nextDouble()*200+table_width+20),(int)(rn.nextDouble()*200+20));
		game_objects.addElement(lgb); 
		
		
		// Фигура 1 в 2-х экземплярах
		m_figure = new Polygon();
		m_figure.addPoint(-25,38);
		m_figure.addPoint(0,-38);
		m_figure.addPoint(25,38);
		lgb = new GameObject(m_figure);
		lgb.setColor(Color.yellow);
		lgb.SetXYPosition((int)(rn.nextDouble()*200+table_width+20),(int)(rn.nextDouble()*200+20));
		game_objects.addElement(lgb); 

		m_figure = new Polygon();
		m_figure.addPoint(-25,38);
		m_figure.addPoint(0,-38);
		m_figure.addPoint(25,38);
		lgb = new GameObject(m_figure);
		lgb.setColor(Color.cyan);
		lgb.SetXYPosition((int)(rn.nextDouble()*200+table_width+20),(int)(rn.nextDouble()*200+20));
		game_objects.addElement(lgb); 

		
		// Фигура 2 в 4-х экземплярах
		m_figure = new Polygon();
		m_figure.addPoint(-40,15);
		m_figure.addPoint(0,-15);
		m_figure.addPoint(40,15);
		lgb = new GameObject(m_figure);
		lgb.setColor(Color.orange);
		lgb.SetXYPosition((int)(rn.nextDouble()*200+table_width+20),(int)(rn.nextDouble()*200+20));
		game_objects.addElement(lgb); 

		m_figure = new Polygon();
		m_figure.addPoint(-40,15);
		m_figure.addPoint(0,-15);
		m_figure.addPoint(40,15);
		lgb = new GameObject(m_figure);
		lgb.setColor(Color.red);
		lgb.SetXYPosition((int)(rn.nextDouble()*200+table_width+20),(int)(rn.nextDouble()*200+20));
		game_objects.addElement(lgb); 

		m_figure = new Polygon();
		m_figure.addPoint(-40,15);
		m_figure.addPoint(0,-15);
		m_figure.addPoint(40,15);
		lgb = new GameObject(m_figure);
		lgb.setColor(Color.green);
		lgb.SetXYPosition((int)(rn.nextDouble()*200+table_width+20),(int)(rn.nextDouble()*200+20));
		game_objects.addElement(lgb); 

		m_figure = new Polygon();
		m_figure.addPoint(-40,15);
		m_figure.addPoint(0,-15);
		m_figure.addPoint(40,15);
		lgb = new GameObject(m_figure);
		lgb.setColor(Color.magenta);
		lgb.SetXYPosition((int)(rn.nextDouble()*200+table_width+20),(int)(rn.nextDouble()*200+20));
		game_objects.addElement(lgb); 

		// Фигура 3 в 2-х экземплярах
		m_figure = new Polygon();
		m_figure.addPoint(-50,15);
		m_figure.addPoint(-40,-15);
		m_figure.addPoint(40,-15);
		m_figure.addPoint(50,15);
		lgb = new GameObject(m_figure);
		lgb.setColor(Color.pink);
		lgb.SetXYPosition((int)(rn.nextDouble()*200+table_width+20),(int)(rn.nextDouble()*200+20));
		game_objects.addElement(lgb); 

		m_figure = new Polygon();
		m_figure.addPoint(-50,15);
		m_figure.addPoint(-40,-15);
		m_figure.addPoint(40,-15);
		m_figure.addPoint(50,15);
		lgb = new GameObject(m_figure);
		lgb.setColor(Color.white);
		lgb.SetXYPosition((int)(rn.nextDouble()*200+table_width+20),(int)(rn.nextDouble()*200+20));
		game_objects.addElement(lgb); 
		
		
		// Расчет центра игрового стола
		center_field_x = (this.getSize().width-table_width)/2;
		center_field_y = (this.getSize().height-control_height) /2;
		
		repaint();

		time_start = System.currentTimeMillis(); 
		
		thr.start(); 
	}
	
	private void update_hidden_image()
	{
		hidden_graphics.setColor(Color.black);
		hidden_graphics.fillRect(0,0,this.getSize().width-table_width,this.getSize().height-control_height);

		for(int lk=0;lk<3;lk++)
		{
			hidden_graphics.setColor(Color.darkGray);
			hidden_graphics.fill3DRect(this.getSize().width-table_width+lk,lk,table_width-(lk*2),this.getSize().height-(lk*2)-control_height,true);

			hidden_graphics.fill3DRect(lk,this.getSize().height-control_height+lk,this.getSize().width-(lk*2),control_height-(lk*2),true);
	
		}

		// Отрисовка времени и кнопки конца игры
		hidden_graphics.setColor(Color.green);
		hidden_graphics.drawString("PLAY TIME :",50,this.getSize().height-control_height+20);
		
		long ltim = System.currentTimeMillis();
		
		Date ldat = new Date(ltim-this.time_start);
		String lmin = Integer.toString(ldat.getMinutes());
		String lsec = Integer.toString(ldat.getSeconds());
		
		if (lmin.length()==1) lmin="0"+lmin;
		if (lsec.length()==1) lsec="0"+lsec;
		hidden_graphics.drawString(lmin+":"+lsec,130,this.getSize().height-control_height+20);
		
		ldat = null;
		
		hidden_graphics.setColor(Color.orange);
		hidden_graphics.fill3DRect(this.getSize().width/2-30,this.getSize().height-control_height+5,60,control_height-10,true);

		hidden_graphics.setColor(Color.black);
		hidden_graphics.drawString("STOP",this.getSize().width/2-17,this.getSize().height-control_height+20);

		// Отрисовка главной фигуры
		main_figure.SetXYPosition(center_field_x,center_field_y);
		main_figure.DrawPolygon(hidden_graphics);  
		
		// Отрисовка игровых объектов
		for(int li=0;li<game_objects.size();li++)
		{
			GameObject lgb = (GameObject)game_objects.elementAt(li); 
			lgb.DrawPolygon(hidden_graphics);  
		}
		
	}
	
	public void run()
	{
		while(true)
		{
			repaint();
			try
			{
				Thread.sleep(800);
			}
			catch(InterruptedException e){ break; }
		}
	}
	
	public void paint(Graphics g)
	{
		g.drawImage(hidden_image,0,0,this);  
	}
	
	public void update(Graphics g)
	{
		update_hidden_image(); 
		paint(g);	
	}
	
}
