
import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseListener;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;

public class TestSprites extends JFrame implements KeyListener, MouseListener, Runnable
{
    static final int MODEL_M50 = 0;
    static final int MODEL_3410 = 1;
    static final int MODEL_7210 = 2;
    static final int MODEL_7650 = 3;
    static final int MODEL_S300 = 4;
    static final int MODEL_X100 = 5;
    static final int MODEL_E700 = 6;
    static final int MODEL_D410 = 7;
    static final int MODEL_CUSTOM = 8;


    // The array contains values for path controllers
    public static final short[] ash_Paths = new short[]{
                        // PATH_path_pendulum
                        (short) 3, (short) 1, (short) 54, (short) 186, (short) 10, (short) 148, (short) 180, (short) 10, (short) 212, (short) 179, (short) 3, (short) 321, (short) 184, (short) 10,
                        // PATH_path_cyclic
                        (short) 5, (short) 3, (short) 52, (short) 119, (short) 10, (short) 71, (short) 132, (short) 10, (short) 128, (short) 139, (short) 10, (short) 229, (short) 110, (short) 10, (short) 163, (short) 77, (short) 10, (short) 85, (short) 80, (short) 10,
                        // PATH_path_normal
                        (short) 14, (short) 0, (short) 38, (short) 37, (short) 10, (short) 86, (short) 50, (short) 10, (short) 132, (short) 36, (short) 10, (short) 171, (short) 47, (short) 10, (short) 202, (short) 37, (short) 10, (short) 234, (short) 46, (short) 10, (short) 265, (short) 34, (short) 10, (short) 301, (short) 46, (short) 10, (short) 275, (short) 9, (short) 10, (short) 236, (short) 19, (short) 10, (short) 196, (short) 12, (short) 10, (short) 162, (short) 20, (short) 10, (short) 122, (short) 14, (short) 10, (short) 87, (short) 20, (short) 10, (short) 57, (short) 13, (short) 10,
                };

    // PATH offsets
    private static final int PATH_path_pendulum = 0;
    private static final int PATH_path_cyclic = 14;
    private static final int PATH_path_normal = 34;


    // Main point coords
    private static final int PATH_path_pendulum_MP_X = -1;
    private static final int PATH_path_pendulum_MP_Y = 0;
    private static final int PATH_path_cyclic_MP_X = 0;
    private static final int PATH_path_cyclic_MP_Y = 0;
    private static final int PATH_path_normal_MP_X = 1;
    private static final int PATH_path_normal_MP_Y = 1;


    byte[] ab_lastGameRecord = null;

    int i_width
    ,
    i_height;

    Object p_synchroObject = new Object();

    private class displayClass extends Canvas
    {
        Image p_db = null;
        int i_width
        ,
        i_height;

        public displayClass(int _width, int _height)
        {
            super();
            p_db = new BufferedImage(_width, _height, BufferedImage.TYPE_4BYTE_ABGR);
            setSize(_width, _height);
            i_width = _width;
            i_height = _height;
        }

        public void paint(Graphics _gr)
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
    private int i_timer;

    private int i_PlayerButtons;

    public void initPlayer()
    {
        i_PlayerButtons = 0;
    }

    public TestSprites(int _type)
    {
        super();

        int i_model = _type;
        i_width = 320;
        i_height = 240;

        p_display = new displayClass(i_width, i_height);

        getContentPane().setLayout(new BorderLayout(0, 0));
        getContentPane().add(p_display, BorderLayout.CENTER);

        this.pack();

        p_display.addKeyListener(this);
        p_display.addMouseListener(this);

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);

        //==============
        i_timer = 0;

        setIgnoreRepaint(true);

        new Thread(this).start();
    }


    public static final int OBJECT_TYPE1 = 0;

    public static final int OBJECT_STATE1 = 0;
    public static final int OBJECT_STATE2 = 1;
    public static final int OBJECT_STATE3 = 2;


    // массив анимации
    int[] ai_spriteAnimationArray = new int []
            {
                        // object_type1
                        1,
                        // state1
                        4,
                        // state2
                        15,
                        // state3
                        26,


                        // width, height, hotzoneoffsetx, hotzoneoffsety, hotzonewidth, hotzoneheight, frames, animation_delay, animation_type, main_offsetx, main_offsety
                        //data state1
                        0x3000, 0x3000, 0x1000, 0x1000, 0x1000, 0x1000, 10, 10, SpriteCollection.ANIMATION_CYCLIC, 0, 0,
                        //data state2
                        0x3000, 0x3000, 0x1000, 0x1000, 0x1000, 0x1000, 10, 10, SpriteCollection.ANIMATION_PENDULUM, 0, 0,
                        //data state3
                        0x3000, 0x3000, 0x1000, 0x1000, 0x1000, 0x1000, 10, 10, SpriteCollection.ANIMATION_FROZEN, 0, 0,
                };

    SpriteCollection p_sprCollection;
    PathController p_path;

    public static final void main(String[] _args)
    {


        TestSprites p_tstr = new TestSprites(MODEL_CUSTOM);
        p_tstr.show();
    }

    //=================================================
    public void keyTyped(KeyEvent e)
    {
    }

    public void keyPressed(KeyEvent e)
    {
        synchronized (p_synchroObject)
        {

            int k = e.getKeyCode();

            int i_state = i_PlayerButtons;


            i_PlayerButtons = i_state;
        }
    }

    public void keyReleased(KeyEvent e)
    {
    }

    public void mouseClicked(MouseEvent e)
    {
    }

    public void mousePressed(MouseEvent e)
    {
    }

    public void mouseReleased(MouseEvent e)
    {
    }

    public void mouseEntered(MouseEvent e)
    {
    }

    public void mouseExited(MouseEvent e)
    {
    }
    //=================================================

    public void run()
    {
        p_sprCollection = new SpriteCollection(0, 15, ai_spriteAnimationArray);

        p_path = new PathController();

        //        int i_spr = p_sprCollection.i_lastInactiveSpriteOffset;
        //       p_sprCollection.activateSprite(OBJECT_TYPE1,OBJECT_STATE1,i_spr);

        //        int i_path = p_pathCollection.i_lastInactivePathOffset;
        //       p_pathCollection.activatePath(i_path,PATH_path_cyclic, PathCollection.MODIFY_NONE,PathCollection.BEHAVIOUR_DEFAULT,0,0,0x100,0x100,0,i_spr/10,0,0);


        int i_spr = p_sprCollection.i_lastInactiveSpriteOffset;
        p_sprCollection.activateSprite(OBJECT_TYPE1, OBJECT_STATE2, i_spr);

        p_path.initPath(0, 0, 0x100, 0x100, new SpriteCollection[]{p_sprCollection}, 0, i_spr, ash_Paths, PATH_path_cyclic, 0, 0, 0);

        //int        i_spr = p_sprCollection.i_lastInactiveSpriteOffset;
        //        p_sprCollection.activateSprite(OBJECT_TYPE1,OBJECT_STATE3,i_spr);
        // int       i_path = p_pathCollection.i_lastInactivePathOffset;
        //        p_pathCollection.activatePath(i_path,PATH_path_pendulum, PathCollection.MODIFY_NONE,PathCollection.BEHAVIOUR_DEFAULT,0,0,0x100,0x100,0,i_spr/10,0,0);

        while (true)
        {

            p_sprCollection.processAnimationForActiveSprites();
            p_path.processStep();

            updateDBuffer();

            //p_pathCollection.moveCenterPointForPath(i_path,0x100,0x100);

            try
            {
                Thread.sleep(50);
            }
            catch (InterruptedException e)
            {
                return;
            }
        }
    }

    public void updateDBuffer()
    {
        Graphics g = p_display.p_db.getGraphics();

        g.setColor(Color.white);
        g.fillRect(0, 0, i_width, i_height);

        g.setColor(Color.red);
        drawPath(g, 0, 0, ash_Paths, PATH_path_normal, 0x100, 0x100);
        drawPath(g, 0, 0, ash_Paths, PATH_path_cyclic, 0x100, 0x100);
        drawPath(g, 0, 0, ash_Paths, PATH_path_pendulum, 0x100, 0x100);

        p_sprCollection.initIterator();
        while (true)
        {
            int _offset = p_sprCollection.nextActiveSpriteOffset();
            if (_offset < 0) break;

            long l_xy = p_sprCollection.getSpriteScreenXY(_offset);
            int i_x = (int) (l_xy >>> 32);
            int i_y = (int) l_xy;

            int i_w = p_sprCollection.getSpriteWidthHeight(_offset);
            int i_h = i_w & 0xFFFF;
            i_w >>>= 16;

            long l_hotspot = p_sprCollection.getSpriteHotspotCoordinates(_offset);
            int i_hx = (int) (l_hotspot >>> 48) + i_x;
            int i_hy = ((int) (l_hotspot >>> 32) + i_y) & 0xFFFF;
            int i_hw = ((int) (l_hotspot >>> 16)) & 0xFFFF;
            int i_hh = ((int) (l_hotspot)) & 0xFFFF;

            int i_frameNumber = p_sprCollection.ai_spriteDataArray[_offset + SpriteCollection.SPRITEDATAOFFSET_ANIMATIONDATA] & SpriteCollection.MASK_FRAMENUMBER;

            g.setColor(Color.gray);
            g.fillRect(i_x, i_y, i_w, i_h);

            g.setColor(Color.black);
            g.drawRect(i_hx, i_hy, i_hw, i_hh);

            g.drawString("" + i_frameNumber, i_x, i_y);
        }

        p_display.paint(p_display.getGraphics());
    }

    private void drawPath(Graphics g, int _x, int _y, short[] _pathArray, int _path, long _i8_coeffHorz, long _i8_coeffVert)
    {
        int i_lastX = 0;
        int i_lastY = 0;

        int i_points = _pathArray[_path++];
        int i_type = _pathArray[_path++];
        for (int li = 0; li <= i_points; li++)
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

}
