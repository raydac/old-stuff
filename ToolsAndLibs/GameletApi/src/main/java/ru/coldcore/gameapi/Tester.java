package ru.coldcore.gameapi;

import java.awt.BorderLayout;
import java.awt.Canvas;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;

import javax.swing.JFrame;

public class Tester extends JFrame implements Runnable
{
    public static final void drawCustomArea(Graphics _g,String _areaName,int _x,int _y,int _w,int _h)
    {
        
    }
    
    public static final void drawCursor(Graphics _g,int _x,int _y,int _buttonWidth,int _buttonHeight)
    {
        
    }
    
    public static final int getButtonState(String _button)
    {
        return 0;
    }
    
    private class displayClass extends Canvas
    {
        Image p_db = null;

        int i_width, i_height;

        public displayClass(final int _width, final int _height)
        {
            super();
            p_db = new BufferedImage(_width, _height, BufferedImage.TYPE_4BYTE_ABGR);
            setSize(_width, _height);
            i_width = _width;
            i_height = _height;
        }

        public void paint(final Graphics _gr)
        {
            if (p_db == null)
            {
                super.paint(_gr);
            }
            else
            {
                _gr.drawImage(p_db, 0, 0, null);
            }
        }
    }

    private displayClass p_display;

    public int i_width;
    public int i_height;

    public SpriteCollection[] ap_SpriteCollection;

    public PathCollection p_PathCollection;

    HexagonalView p_hexView;
    
    protected static final short[] ash_SpritesTable = new short[] {
    // Object CARD
            (short) 1,
            // Object CARD state FACE
            (short) 4,
            // Object CARD state BACK
            (short) 4,
            // Object CARD state TWOBACK
            (short) 15, (short) 15, (short) 20, (short) 0, (short) 0, (short) 15, (short) 20, (short) 1, (short) 1, (short) 1, (short) 7, (short) 10, (short) 19, (short) 22, (short) 0, (short) 0, (short) 19, (short) 22, (short) 1, (short) 1, (short) 1, (short) 9, (short) 11, };

    // ------------------Sprite constants-----------------
    public static final int SPRITE_OBJ_CARD = 0;

    public static final int SPRITE_STATE_CARD_FACE = 0;

    public static final int SPRITE_STATE_CARD_BACK = 1;

    public static final int SPRITE_STATE_CARD_TWOBACK = 2;

    // The array contains values for path controllers
    public static final short[] ash_Paths = new short[] {
    // PATH_pathNormal
            (short) 8, (short) 0, (short) 14, (short) 28, (short) 10, (short) 143, (short) 12, (short) 10, (short) 223, (short) 59, (short) 10, (short) 99, (short) 56, (short) 10, (short) 57, (short) 81, (short) 10, (short) 144, (short) 76, (short) 10, (short) 220, (short) 95, (short) 10, (short) 118, (short) 127,
            (short) 10, (short) 29, (short) 107, (short) 10,
            // PATH_pathCycled
            (short) 4, (short) 2, (short) 24, (short) 151, (short) 10, (short) 102, (short) 141, (short) 10, (short) 144, (short) 135, (short) 10, (short) 206, (short) 124, (short) 10, (short) 255, (short) 102, (short) 10,
            // PATH_pathCycledSmooth
            (short) 4, (short) 3, (short) 25, (short) 183, (short) 10, (short) 103, (short) 155, (short) 10, (short) 180, (short) 171, (short) 10, (short) 177, (short) 190, (short) 10, (short) 100, (short) 211, (short) 10,
            // PATH_pathPendulum
            (short) 5, (short) 1, (short) 243, (short) 193, (short) 10, (short) 287, (short) 161, (short) 10, (short) 266, (short) 119, (short) 10, (short) 282, (short) 76, (short) 10, (short) 251, (short) 45, (short) 10, (short) 274, (short) 22, (short) 10, };

    // PATH offsets
    private static final int PATH_pathNormal = 0;

    private static final int PATH_pathCycled = 29;

    private static final int PATH_pathCycledSmooth = 46;

    private static final int PATH_pathPendulum = 63;

    private void drawPath(Graphics g, int _x, int _y, short[] _pathArray, int _path, long _i8_coeffHorz, long _i8_coeffVert)
    {
        int i_lastX = 0;
        int i_lastY = 0;

        int i_points = _pathArray[_path++] + 1;
        int i_type = _pathArray[_path++];
        for (int li = 0; li < i_points; li++)
        {
            int i_x = _pathArray[_path++];
            int i_y = _pathArray[_path++];
            int i_steps = _pathArray[_path++];

            i_x = (int) (((_i8_coeffHorz) * i_x) >> 8);
            i_y = (int) (((_i8_coeffVert) * i_y) >> 8);

            if (li == 0)
            {
                g.drawLine(i_x, i_y, i_x, i_y);
            }
            else
            {
                g.drawLine(i_lastX, i_lastY, i_x, i_y);
            }

            i_lastX = i_x;
            i_lastY = i_y;
        }
    }

    TileRender p_TileManager;
    
    public static final byte [] ab_hexArray = new byte[]{1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16};
    
    public Tester()
    {
        super();

        ITVFormManager.p_SystemFont = new Font("",Font.PLAIN,28);
        
        Utils.p_This = this.getClass();
        
        p_TileManager = new TileRender();
        p_TileManager.init(128,128,8,12);

        p_TileManager.SCREEN_WIDTH = 500;
        p_TileManager.SCREEN_HEIGHT = 500;
    
        p_hexView = new HexagonalView(22,22,true);
        
        
        try
        {
            ITVFormManager.init("forms.txt");
            
            Image p_image = Utils.loadImageFromResource("font.png");
//            byte [] ab_array = (byte[])Utils.loadArray(this.getClass(),"/ru/coldcore/gameapi/tile1.bin");
  
            
            p_TileManager.setTileSetImage(p_image,null);
            p_TileManager.setSringAsTileArray("Огромная комната, вмещающая в себе второуездный  класс  училища,  носит характер казенщины, выражающей полное  отсутствие  домовитости  и  приюта.Стены с промерзшими насквозь  углами  грязны  -  в  чернобурых  полосах  и пятнах, в плесени и ржавчине; потолок подперт деревянными столбами, потому что он давно погнулся и без подпорок грозил падением; пол в  зимнее  время посыпался песком либо опилками: иначе на нем была бы  постоянная  грязь  и слякоть от снегу, приносимого учениками на  сапогах  с  улицы.  От  задней стены идут _парты_ (учебные столы); у передней стены, между окнами, стол и стул для учителя; вправо от него - черная учебная доска; влево, в  углу  у дверей, на табурете - ведро воды для жаждущих; в  противоположном  углу  - печка; между печкой и дверями вешалка, на спицах которой висит  целый  ряд тряпичный: шинели, шубы, халаты, накидки разного рода,  все  перешитое  из матерних капотов и отцовских  подрясников,  -  нагольное,  крытое  сукном, шерстяное и тиковое; на всем этом виднеются клочья ваты и дыры, и много  в том  месте  злачнем  и  прохладном  паразитов,   поедающих,   тело   плохо кормленного бурсака. В пять окон, с пузырчатыми и  зеленоватыми  стеклами, пробивается мало свету. Вонь и копоть в классе; воздух  мозглый,  какой-то прогорклый, сырой и холодный. ");
        }
        catch(Throwable _ex)
        {
            _ex.printStackTrace();
            return;
        }
        
        p_TileManager.setPositionXY(0,0);
        
        ap_SpriteCollection = new SpriteCollection[1];
        ap_SpriteCollection[0] = new SpriteCollection(0, 4, ash_SpritesTable);

        p_PathCollection = new PathCollection(0, 4, ap_SpriteCollection, ash_Paths);

        int i_lastSprite = ap_SpriteCollection[0].i_lastInactiveSpriteOffset;
        ap_SpriteCollection[0].activateOne(i_lastSprite, 0, 0);
        int i_lastPath = p_PathCollection.i_lastInactivePathOffset;
        p_PathCollection.activateOne(i_lastPath, 0, 0, 0, 0x170, 0x80, 0, i_lastSprite, PATH_pathNormal, 0, 0, 0);

        i_lastSprite = ap_SpriteCollection[0].i_lastInactiveSpriteOffset;
        ap_SpriteCollection[0].activateOne(i_lastSprite, 0, 0);
        i_lastPath = p_PathCollection.i_lastInactivePathOffset;
        p_PathCollection.activateOne(i_lastPath, 0, 0, 0, 0x100, 0x100, 0, i_lastSprite, PATH_pathCycled, 0, 0, 0);

        i_lastSprite = ap_SpriteCollection[0].i_lastInactiveSpriteOffset;
        ap_SpriteCollection[0].activateOne(i_lastSprite, 0, 0);
        i_lastPath = p_PathCollection.i_lastInactivePathOffset;
        p_PathCollection.activateOne(i_lastPath, 0, 0, 0, 0x100, 0x100, 0, i_lastSprite, PATH_pathCycledSmooth, 0, 0, 0);

        i_lastSprite = ap_SpriteCollection[0].i_lastInactiveSpriteOffset;
        ap_SpriteCollection[0].activateOne(i_lastSprite, 0, 0);
        i_lastPath = p_PathCollection.i_lastInactivePathOffset;
        p_PathCollection.activateOne(i_lastPath, 0, 0, 0, 0x100, 0x100, 0, i_lastSprite, PATH_pathPendulum, 0, 0, 0);

        i_width = 500;
        i_height = 500;

        p_display = new displayClass(i_width, i_height);

        getContentPane().setLayout(new BorderLayout(0, 0));
        getContentPane().add(p_display, BorderLayout.CENTER);

        this.pack();

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);

        // ==============

        setTitle("Test");
        setIgnoreRepaint(true);

        
        try
        {
        ImageManager.init(true);
        }
        catch(Throwable _thr)
        {
            _thr.printStackTrace();
            System.exit(1);
        }
        
        new Thread(this).start();
    }

    DrawOptimizer p_opti = new DrawOptimizer(16);
    
    public void run()
    {
        System.out.println("Runned");

        int i_x = 0;
        int i_y = 0;
        
        final int STEPS = 30;
        
        int i_ticker = STEPS;
        
        int i_xs = 4;
        int i_ys = 4 ;

        int i_val = 0;
        int i_pos = 0;
     
        DrawOptimizer.GRAPHICSAREA_WIDTH = i_width;
        DrawOptimizer.GRAPHICSAREA_HEIGHT = i_height;
        
        while (true)
        {
            try
            {
                Thread.sleep(50);
            }
            catch (Throwable _ex)
            {
                return;
            }

            //p_PathCollection.processActive();

            p_TileManager.setPositionXY(i_x,i_y);
            
            //p_TileManager.setTileValue(i_val,0);
            

            i_x+=i_xs;
            i_y+=i_ys;
            
            i_val++;
            i_pos++;
            
            i_ticker--;
            if (i_ticker==0)
            {
                i_val=0;
                i_pos=0;
                
                i_xs = 0 - i_xs;
                i_ys = 0 - i_ys;
                i_ticker = STEPS;
            }
            
            p_opti.reset();
            
            final boolean lg_opt = false;
            
            p_opti.addRectangleArea(10,56,30,2,lg_opt);
            p_opti.addRectangleArea(50,56,10,10,lg_opt);
            
            p_opti.pack();
            
            updateDBuffer();
        }
    }
    
    public final static void main(String[] args)
    {
        final Tester p_tstr = new Tester();
        p_tstr.setVisible(true);
    }

    public void drawZones(Graphics _g,int _x,int _y,DrawOptimizer _opti)
    {
        _g.setClip(0,0,i_width,i_height);
        
        int i_areasNumber = _opti.i_StackPointer;
        if (i_areasNumber==0) return;
        int i_areaPointer = 0;

        while(i_areasNumber!=0)
        {
            int i_ax = _opti.AREA_ARRAY[i_areaPointer++];
            int i_ay = _opti.AREA_ARRAY[i_areaPointer++];
            int i_aw = _opti.AREA_ARRAY[i_areaPointer++];
            int i_ah = _opti.AREA_ARRAY[i_areaPointer++];

            i_areaPointer+=4;
            
            _g.setColor(Color.BLUE);
            _g.drawRect(i_ax+_x,i_ay+_y,i_aw,i_ah);
            
            i_areasNumber--;
        }
    }
    
    public void updateDBuffer()
    {
        final Graphics g = p_display.p_db.getGraphics();
        ImageManager.p_DestinationGraphics = g;
        
        g.setColor(Color.white);
        g.setClip(0,0,i_width,i_height);

        g.fillRect(0, 0, i_width, i_height);

        try
        {
            ITVFormManager.selectForm("mainform",false);
            ITVFormManager.drawCurrentForm(g,40,40);
            
            ITVFormManager.selectForm("forma",false);
            ITVFormManager.drawCurrentForm(g,90,90);
            
            
        }
        catch(Throwable _thr)
        {
            _thr.printStackTrace();
        }
        

/*
        p_TileManager.drawHiddenBuffer(g,10,10);
        
        p_TileManager.directFillRegions(g,200,10,p_opti);// directPaint(g,200,10,100,100);

        drawZones(g,200,10,p_opti);
        drawZones(g,10,10,p_opti);
        
        g.setClip(0,0,i_width,i_height);
        //g.drawImage(p_TileManager.p_HiddenBuffer,10,10,null);
        g.setColor(Color.black);
        g.drawRect(10,10,p_TileManager.i_OutAreaWidth,p_TileManager.i_OutAreaHeight);
        
        try
        {
        ImageManager.drawImage(0,30,30);
        }
        catch(Throwable _thr)
        {
            _thr.printStackTrace();
        }
 */
        g.setClip(0,0,500,500);
        g.setColor(Color.GREEN);
        
        p_hexView.drawArray(g,0,0,ab_hexArray,4);
        
        p_display.paint(p_display.getGraphics());
    }

    public static final void renderCell(Graphics _g,int _x,int _y,int _value)
    {
        _g.drawString(""+_value,_x+3,_y+12);
    }
    
}
