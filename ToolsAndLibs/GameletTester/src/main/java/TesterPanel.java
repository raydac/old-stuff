import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.KeyboardFocusManager;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;

public class TesterPanel extends Component implements KeyListener,FocusListener,MouseListener
{
    private Rectangle p_Bounds;

    private BufferedImage p_BackBuffer;

    public Rectangle getBounds()
    {
        return p_Bounds;
    }

    
    
    public void mouseClicked(MouseEvent e)
    {
       
        requestFocusInWindow(false);
    }



    public void mouseEntered(MouseEvent e)
    {
        
    }

    public boolean isDisplayable()
    {
        return true;
    }
    

    public void mouseExited(MouseEvent e)
    {
        
    }



    public void mousePressed(MouseEvent e)
    {
        requestFocus();        
    }



    public void mouseReleased(MouseEvent e)
    {
        
    }



    public void focusGained(FocusEvent arg0)
    {
        
    }



    public void focusLost(FocusEvent arg0)
    {
        
    }


    public void keyPressed(KeyEvent _key)
    {
        startup.keyPressed(_key);
    }

    public void keyReleased(KeyEvent _key)
    {
        startup.keyReleased(_key);
    }

    public void keyTyped(KeyEvent _key)
    {
        startup.keyTyped(_key);
    }

    public Rectangle getBounds(Rectangle _rv)
    {
        _rv.x = p_Bounds.x;
        _rv.y = p_Bounds.y;
        _rv.width = p_Bounds.width;
        _rv.height = p_Bounds.height;
        return _rv;
    }

    public int getHeight()
    {
        return p_Bounds.height;
    }

    public Dimension getMaximumSize()
    {
        return new Dimension(p_Bounds.width, p_Bounds.height);
    }

    public Dimension getMinimumSize()
    {
        return new Dimension(p_Bounds.width, p_Bounds.height);
    }

    public Dimension getSize()
    {
        return new Dimension(p_Bounds.width, p_Bounds.height);
    }

    public Dimension getSize(Dimension rv)
    {
        rv.width = p_Bounds.width;
        rv.height = p_Bounds.height;
        return rv;
    }

    public int getX()
    {
        return p_Bounds.x;
    }

    public int getY()
    {
        return p_Bounds.y;
    }

    public void setBounds(int x, int y, int width, int height)
    {
        p_Bounds.x = x;
        p_Bounds.y = y;
    }

    public void setBounds(Rectangle r)
    {
        p_Bounds.x = r.x;
        p_Bounds.y = r.y;
    }

    public void setLocation(int x, int y)
    {
        p_Bounds.x = x;
        p_Bounds.y = y;
    }

    public Point getLocation()
    {
        return new Point(p_Bounds.x, p_Bounds.y);
    }

    public void setLocation(Point p)
    {
        p_Bounds.x = p.x;
        p_Bounds.y = p.y;
    }

    public Dimension getPreferredSize()
    {
        return new Dimension(p_Bounds.width, p_Bounds.height);
    }

    public void reshape(int x, int y, int width, int height)
    {
        p_Bounds.x = x;
        p_Bounds.y = y;
    }

    public void resize(Dimension d)
    {
    }

    public void resize(int width, int height)
    {
    }

    public void setSize(Dimension d)
    {
    }

    public void setSize(int width, int height)
    {
    }

    public Dimension size()
    {
        return new Dimension(p_Bounds.width, p_Bounds.height);
    }

    public int getWidth()
    {
        return p_Bounds.width;
    }

    public TesterPanel(startup.VideoMode _mode)
    {
        super();
        p_Bounds = new Rectangle(0, 0, _mode.i_Width, _mode.i_Height);

        p_BackBuffer = new BufferedImage(_mode.i_Width, _mode.i_Height, BufferedImage.TYPE_INT_RGB);

        setFocusable(true);
        
        addKeyListener(this);
        addMouseListener(this);
    }

    public Point location()
    {
        return getLocation();
    }

    public Dimension minimumSize()
    {
        return getMinimumSize();
    }

    public void changeVideoMode(startup.VideoMode _mode)
    {
        p_Bounds.width = _mode.i_Width;
        p_Bounds.height = _mode.i_Height;
        p_BackBuffer = new BufferedImage(_mode.i_Width, _mode.i_Height, BufferedImage.TYPE_INT_RGB);

        invalidate();
        this.repaint();
    }

    public void updateGameScreen()
    {
        GameView.paintGame(p_BackBuffer.getGraphics());
    }

    public void update(Graphics _g)
    {
        paint(_g);
    }

    public void paint(Graphics _g)
    {
        _g.drawImage(p_BackBuffer, 0, 0, null);
    }
}
