/*
  Author : Igor A. Maznitsa
  EMail  : rrg@forth.org.ru
  Date   : 29.03.2002
  (C) 2002 All Copyright by Igor A. Maznitsa
*/
package com.igormaznitsa.LogoEditor;

import java.applet.Applet;
import java.awt.*;
import java.awt.image.PixelGrabber;
import java.awt.event.*;
import java.io.IOException;

public class EditPicture extends Canvas implements MouseListener, MouseMotionListener
{
    public static final int ZOOM_FACTOR = 5;
    public static final String ACTION_IMAGECHANGED = "IMG_CHANGED";
    public static final String ACTION_COMMIT = "IMG_COMMIT";

    protected int[] _current_pattern = null;
    protected int _pattern_width = 0;
    protected int _pattern_height = 0;
    protected boolean _transaction_is_started;
    protected Applet _parent_applet;
    protected ActionListener _act_listener = null;
    protected Image _grid_image = null;

    private Color _clr_bck = new Color(0x00, 0x00, 0x00);
    private Color _clr_fgr = new Color(0xFF, 0xFF, 0xFF);

    protected int _image_width;
    protected int _image_height;
    protected int[] _image_array;
    protected int[] _image_undo_array;
    protected int[] _image_redo_array;
    protected Image _image_buffer;
    protected Image _image_real;
    protected Image _image_transact;
    protected int[] _image_transact_array;
    protected PixelGrabber _pixel_grabber = null;
    protected boolean _is_grab_mode = false;

    protected boolean _is_undo;
    protected boolean _is_redo;

    protected boolean _is_drag_select;

    protected int _start_x = -1;
    protected int _start_y = -1;
    protected boolean _press_paint_button = false;

    protected int _current_state = GR_NONE;
    protected Dimension _dim;

    protected Color _background = new Color(0xA0, 0xE0, 0xA0);
    protected Color _foreground = Color.black;
    protected Color _transact_paint = Color.blue;
    protected Color _transact_erase = Color.red;
    protected Color _selectrect_color = Color.blue;

    protected Rectangle _select_rectangle = null;

    public static final int GR_NONE = -1;
    public static final int GR_PENCIL = 0;
    public static final int GR_LINE = 1;
    public static final int GR_FILLRECT = 2;
    public static final int GR_RECT = 3;
    public static final int GR_CIRCLE = 4;
    public static final int GR_FILLCIRCLE = 5;
    public static final int GR_TEXT = 6;
    public static final int GR_SELECT = 7;
    public static final int GR_PATTERN = 8;

    public void validate()
    {
        super.validate();
        repaint();
    }

    public int[] getSelectedRect()
    {
        if (_select_rectangle == null) return null;
        int[] _sel_rec = new int[_select_rectangle.width * _select_rectangle.height];

        for (int ly = 0; ly < _select_rectangle.height; ly++)
        {
            for (int lx = 0; lx < _select_rectangle.width; lx++)
            {
                _sel_rec[lx + ly * _select_rectangle.width] = _image_array[lx + _select_rectangle.x + (ly + _select_rectangle.y) * _image_width];
            }
        }
        return _sel_rec;
    }

    public Dimension getMaximumSize()
    {
        return _dim;
    }

    public Dimension getMinimumSize()
    {
        return _dim;
    }

    public void setActionListener(ActionListener lstnr)
    {
        _act_listener = lstnr;
    }

    public void mouseClicked(MouseEvent e)
    {

    }

    public void setCurrentPattern(int[] pattern, int w, int h)
    {
        _current_pattern = pattern;
        _pattern_width = w;
        _pattern_height = h;
    }

    protected void beginTransaction()
    {
        _transaction_is_started = true;
        clearTransactImage();
    }

    protected void grabTransactionImage()
    {
        _pixel_grabber = new PixelGrabber(_image_transact, 0, 0, _image_width, _image_height, _image_transact_array, 0, _image_width);
        try
        {
            _pixel_grabber.grabPixels();
        }
        catch (InterruptedException e)
        {
            return;
        }
    }

    protected void commitTransaction()
    {
        copyImageToUndo();
        putTransactionToImage();
        clearTransactImage();
        updateImage();
        _transaction_is_started = false;
        if (_act_listener != null) _act_listener.actionPerformed(new ActionEvent(this, 0, ACTION_COMMIT));
    }

    public void scrollUp()
    {
        copyImageToUndo();
        for (int ly = 0; ly < (_image_height - 1); ly++)
        {
            for (int lx = 0; lx < _image_width; lx++)
            {
                _image_array[lx + ly * _image_width] = _image_array[lx + (ly + 1) * _image_width];
            }
        }

        int lly = (_image_height - 1) * _image_width;
        for (int lx = 0; lx < _image_width; lx++) _image_array[lx + lly] = 0x00;

        updateImage();

        if (_act_listener != null) _act_listener.actionPerformed(new ActionEvent(this, 0, ACTION_COMMIT));
    }

    public void horizMirror()
    {
        copyImageToUndo();
        int llx = _image_width - 1;
        int lv = _image_width >> 1;
        for (int lx = 0; lx < lv; lx++)
        {
            for (int ly = 0; ly < _image_height; ly++)
            {
                int la = _image_array[lx + ly * _image_width];
                int lb = _image_array[llx + ly * _image_width];
                _image_array[lx + ly * _image_width] = lb;
                _image_array[llx + ly * _image_width] = la;
            }
            llx--;
        }
        updateImage();
        if (_act_listener != null) _act_listener.actionPerformed(new ActionEvent(this, 0, ACTION_COMMIT));
    }

    public void vertMirror()
    {
        copyImageToUndo();
        int lly = _image_height - 1;
        int lv = _image_height >> 1;
        for (int ly = 0; ly < lv; ly++)
        {
            for (int lx = 0; lx < _image_width; lx++)
            {
                int la = _image_array[lly * _image_width + lx];
                int lb = _image_array[lx + ly * _image_width];
                _image_array[lly * _image_width + lx] = lb;
                _image_array[lx + ly * _image_width] = la;
            }
            lly--;
        }
        updateImage();

        if (_act_listener != null) _act_listener.actionPerformed(new ActionEvent(this, 0, ACTION_COMMIT));
    }

    public void scrollDown()
    {
        copyImageToUndo();
        for (int ly = (_image_height - 1); ly > 0; ly--)
        {
            for (int lx = 0; lx < _image_width; lx++)
            {
                _image_array[lx + ly * _image_width] = _image_array[lx + (ly - 1) * _image_width];
            }
        }

        for (int lx = 0; lx < _image_width; lx++) _image_array[lx] = 0x00;

        updateImage();

        if (_act_listener != null) _act_listener.actionPerformed(new ActionEvent(this, 0, ACTION_COMMIT));
    }

    public void scrollRight()
    {
        copyImageToUndo();
        for (int lx = (_image_width - 1); lx > 0; lx--)
        {
            for (int ly = 0; ly < _image_height; ly++)
            {
                _image_array[lx + ly * _image_width] = _image_array[lx - 1 + ly * _image_width];
            }
        }

        for (int ly = 0; ly < _image_height; ly++) _image_array[ly * _image_width] = 0x00;

        updateImage();

        if (_act_listener != null) _act_listener.actionPerformed(new ActionEvent(this, 0, ACTION_COMMIT));
    }

    public void scrollLeft()
    {
        copyImageToUndo();
        for (int lx = 0; lx < (_image_width - 1); lx++)
        {
            for (int ly = 0; ly < _image_height; ly++)
            {
                _image_array[lx + ly * _image_width] = _image_array[lx + 1 + ly * _image_width];
            }
        }

        for (int ly = 0; ly < _image_height; ly++) _image_array[ly * _image_width + (_image_width - 1)] = 0x00;

        updateImage();

        if (_act_listener != null) _act_listener.actionPerformed(new ActionEvent(this, 0, ACTION_COMMIT));
    }

    public void clearImage()
    {
        copyImageToUndo();
        clearRealImage();
        updateImage();
        if (_act_listener != null) _act_listener.actionPerformed(new ActionEvent(this, 0, ACTION_COMMIT));
    }

    public void inverseImage()
    {
        copyImageToUndo();
        for (int li = 0; li < _image_array.length; li++) if (_image_array[li] == 0x00) _image_array[li] = 0xFFFFFF; else _image_array[li] = 0x00;
        updateImage();
        if (_act_listener != null) _act_listener.actionPerformed(new ActionEvent(this, 0, ACTION_COMMIT));
    }



    protected void rollbackTransaction()
    {
        clearTransactImage();
        updateImage();
        if (_act_listener != null) _act_listener.actionPerformed(new ActionEvent(this, 0, ACTION_IMAGECHANGED));
        _transaction_is_started = false;
    }

    protected int recalculateX(int x)
    {
        return x / ZOOM_FACTOR;
    }

    protected int recalculateY(int y)
    {
        return y / ZOOM_FACTOR;
    }

    public void mousePressed(MouseEvent e)
    {
        if ((e.getModifiers() & MouseEvent.BUTTON1_MASK) != 0)
        {
            _press_paint_button = true;
        }
        else
        {
            _press_paint_button = false;
        }
        _start_x = recalculateX(e.getX());
        _start_y = recalculateY(e.getY());

        if (_current_state == GR_SELECT)
        {
            if (_select_rectangle != null)
            {
                if (_select_rectangle.contains(_start_x, _start_y))
                {
                    _is_drag_select = true;
                    _current_pattern = getSelectedRect();
                    _pattern_width = _select_rectangle.width;
                    _pattern_height = _select_rectangle.height;
                }
                else
                {
                    _is_drag_select = false;
                }
            }
            else
            {
                _is_drag_select = false;
            }
        }

        beginTransaction();
        mouseDragged(e);
    }

    public void loadImageFromArray(int w,int h,int [] arr)
    {
        initImage(w,h);
        for(int li=0;li<arr.length;li++)
        {
            if ((arr[li]&0xFFFFFF)!=0) _image_array [li] = 0xFFFFFF; else _image_array [li] = 0x00;
        }
        updateImage();
    }

    public void mouseReleased(MouseEvent e)
    {
        commitTransaction();
        if (_act_listener != null) _act_listener.actionPerformed(new ActionEvent(this, 0, ACTION_IMAGECHANGED));
    }

    public void mouseEntered(MouseEvent e)
    {
    }

    public void mouseExited(MouseEvent e)
    {
    }

    public void mouseDragged(MouseEvent e)
    {
        int ldx = recalculateX(e.getX());
        int ldy = recalculateY(e.getY());

        int lsx = Math.min(_start_x, ldx);
        int lsy = Math.min(_start_y, ldy);

        int lx = Math.max(_start_x, ldx);
        int ly = Math.max(_start_y, ldy);

        if (!_transaction_is_started) return;

        if (_current_state != GR_PENCIL) clearTransactImage();

        Graphics g = _image_transact.getGraphics();
        g.setColor(_clr_fgr);

        switch (_current_state)
        {
            case GR_CIRCLE:
                {
                    _is_grab_mode = true;
                    g.drawOval(lsx, lsy, lx - lsx, ly - lsy);
                }
                ;
                break;
            case GR_FILLCIRCLE:
                {
                    _is_grab_mode = true;
                    g.fillOval(lsx, lsy, lx - lsx, ly - lsy);
                }
                ;
                break;
            case GR_FILLRECT:
                {
                    _is_grab_mode = true;
                    g.fillRect(lsx, lsy, lx - lsx, ly - lsy);
                }
                ;
                break;
            case GR_LINE:
                {
                    _is_grab_mode = true;
                    g.drawLine(_start_x, _start_y, ldx, ldy);
                }
                ;
                break;
            case GR_PATTERN:
                {
                    _is_grab_mode = false;
                    drawPattern(g, ldx, ldy);
                }
                ;
                break;
            case GR_PENCIL:
                {
                    _is_grab_mode = true;
                    g.drawLine(ldx, ldy, ldx, ldy);
                }
                ;
                break;
            case GR_RECT:
                {
                    _is_grab_mode = true;
                    g.drawRect(lsx, lsy, lx - lsx, ly - lsy);
                }
                ;
                break;
            case GR_SELECT:
                {
                    _is_grab_mode = false;
                    if (_is_drag_select)
                    {
                        drawPattern(g, ldx, ldy);
                    }
                    else
                    {
                        lx = Math.min(_image_width, lx);
                        ly = Math.min(_image_height, ly);
                        lsx = Math.max(lsx, 0);
                        lsy = Math.max(lsy, 0);
                        _select_rectangle = new Rectangle(lsx, lsy, lx - lsx, ly - lsy);
                    }
                }
                ;
                break;
            case GR_TEXT:
                {
                    _is_grab_mode = false;
                    drawPattern(g, ldx, ldy);
                }
                ;
                break;
        }

        updateImage();

        if (_act_listener != null) _act_listener.actionPerformed(new ActionEvent(this, 0, ACTION_IMAGECHANGED));
    }

    protected void setPixelInTransactArray(int lx, int ly, int lcolor)
    {
        if ((lx < 0) || (lx >= _image_width)) return;
        if ((ly < 0) || (ly >= _image_height)) return;
        _image_transact_array[lx + ly * _image_width] = lcolor;
    }

    protected void drawPattern(Graphics g, int ldx, int ldy)
    {
        int loffx = _pattern_width >> 1;
        int loffy = _pattern_height >> 1;

        if (_current_state != GR_SELECT)
        {
            for (int lpy = 0; lpy < _pattern_height; lpy++)
            {
                for (int lpx = 0; lpx < _pattern_width; lpx++)
                {
                    if (_current_pattern[lpx + lpy * _pattern_width] != 0)
                    {
                        int lax = ldx + lpx - loffx;
                        int lay = ldy + lpy - loffy;

                        setPixelInTransactArray(lax, lay, 0xFFFFFF);
                    }
                }
            }
        }
        else
        {
            for (int lpy = 0; lpy < _pattern_height; lpy++)
            {
                for (int lpx = 0; lpx < _pattern_width; lpx++)
                {
                    int lax = ldx + lpx - loffx;
                    int lay = ldy + lpy - loffy;

                    if (_current_pattern[lpx + lpy * _pattern_width] != 0)
                    {
                        setPixelInTransactArray(lax, lay, 0xFFFFFF);
                    }
                    else
                    {
                        setPixelInTransactArray(lax, lay, 0x00FF00);
                    }
                }
            }
        }
    }

    public void mouseMoved(MouseEvent e)
    {
    }

    public boolean isRedo()
    {
        return _is_redo;
    }

    public boolean isUndo()
    {
        return _is_undo;
    }

    protected void setPoint(int x, int y)
    {
        if ((x < 0) || (x >= _image_width)) return;
        if ((y < 0) || (y >= _image_height)) return;
        _image_transact_array[x + y * _image_width] = 0xFFFFFF;
    }

    protected void clearTransactArray()
    {
        for (int li = 0; li < _image_transact_array.length; li++) _image_transact_array[li] = 0x00;
    }

    protected void clearTransactImage()
    {
        clearTransactArray();
        Graphics g = _image_transact.getGraphics();
        g.setColor(_clr_bck);
        g.fillRect(0, 0, _image_width, _image_height);
        g.setColor(_clr_fgr);
    }

    protected void clearRealImage()
    {
        for (int li = 0; li < _image_array.length; li++) _image_array[li] = 0x00;
    }

    protected void copyImageToUndo()
    {
        System.arraycopy(_image_array, 0, _image_undo_array, 0, _image_array.length);
        _is_undo = true;
        _is_redo = false;
    }

    protected void copyUndoToImage()
    {
        copyImageToRedo();
        System.arraycopy(_image_undo_array, 0, _image_array, 0, _image_array.length);
        _is_undo = false;
        updateImage();
        if (_act_listener != null) _act_listener.actionPerformed(new ActionEvent(this, 0, ACTION_COMMIT));
    }

    protected void copyRedoToImage()
    {
        copyImageToUndo();
        System.arraycopy(_image_redo_array, 0, _image_array, 0, _image_array.length);
        _is_redo = false;
        updateImage();
        if (_act_listener != null) _act_listener.actionPerformed(new ActionEvent(this, 0, ACTION_COMMIT));
    }

    protected void copyImageToRedo()
    {
        System.arraycopy(_image_array, 0, _image_redo_array, 0, _image_array.length);
        _is_redo = true;
    }

    protected void putTransactionToImage()
    {
        if (!_transaction_is_started) return;

        if (_is_grab_mode) grabTransactionImage();

        if (_current_state != GR_SELECT)
        {
            for (int li = 0; li < _image_transact_array.length; li++)
            {
                if ((_image_transact_array[li] & 0xFFFFFF) != 0)
                {
                    if (_press_paint_button)
                        _image_array[li] = 0xFFFFFF;
                    else
                        _image_array[li] = 0x000000;
                }
            }
        }
        else
        {
            for (int li = 0; li < _image_transact_array.length; li++)
            {
                switch (_image_transact_array[li] & 0xFFFFFF)
                {
                    case 0x00:
                        ;
                        break;
                    case 0x00FF00:
                        if (_press_paint_button) _image_array[li] = 0x00; else _image_array[li] = 0xFFFFFF;
                        break;
                    case 0xFFFFFF:
                        if (_press_paint_button) _image_array[li] = 0xFFFFFF; else _image_array[li] = 0x00;
                        break;
                }
            }
        }
    }

    public void updateImageFromArray(Image img,Color forecolor,Color backcolor)
    {
        int lxx = 0;
        Graphics gr = img.getGraphics();
        for (int ly = 0; ly < _image_height; ly++)
        {
            for (int lx = 0; lx < _image_width; lx++)
            {
                if (_image_array[lxx] != 0)
                {
                    gr.setColor(forecolor);
                }
                else
                {
                    gr.setColor(backcolor);
                }
                gr.drawLine(lx, ly, lx, ly);
                lxx++;
            }
        }
        gr.dispose();
    }

    protected void updateImage()
    {
        if (_transaction_is_started && _is_grab_mode) grabTransactionImage();

        Graphics gr = _image_real.getGraphics();
        gr.setColor(_background);
        gr.fillRect(0, 0, _image_width, _image_height);
        int lxx = 0;
        if (_current_state != GR_SELECT)
        {
            for (int ly = 0; ly < _image_height; ly++)
            {
                for (int lx = 0; lx < _image_width; lx++)
                {
                    if (_transaction_is_started)
                    {
                        if ((_image_transact_array[lxx] & 0xFFFFFF) != 0)
                        {
                            if (_press_paint_button) gr.setColor(_transact_paint); else gr.setColor(_transact_erase);
                            gr.drawLine(lx, ly, lx, ly);
                        }
                        else
                        {
                            if (_image_array[lxx] != 0)
                            {
                                gr.setColor(_foreground);
                                gr.drawLine(lx, ly, lx, ly);
                            }
                        }
                    }
                    else
                    {
                        if (_image_array[lxx] != 0)
                        {
                            gr.setColor(_foreground);
                            gr.drawLine(lx, ly, lx, ly);
                        }
                    }
                    lxx++;
                }
            }
        }
        else
        {
            for (int ly = 0; ly < _image_height; ly++)
            {
                for (int lx = 0; lx < _image_width; lx++)
                {
                    if (_transaction_is_started)
                    {
                        switch (_image_transact_array[lxx] & 0xFFFFFF)
                        {
                            case 0:
                                {
                                    if (_image_array[lxx] != 0)
                                    {
                                        gr.setColor(_foreground);
                                    }
                                    else
                                    {
                                        gr.setColor(_background);
                                    }
                                }
                                ;
                                break;
                            case 0x00FF00:
                                {
                                    if (_press_paint_button)
                                    {
                                        gr.setColor(_transact_erase);
                                    }
                                    else
                                    {
                                        gr.setColor(_transact_paint);
                                    }
                                }
                                ;
                                break;
                            case 0xFFFFFF:
                                {
                                    if (_press_paint_button)
                                    {
                                        gr.setColor(_transact_paint);
                                    }
                                    else
                                    {
                                        gr.setColor(_transact_erase);
                                    }
                                }
                                ;
                                break;
                        }
                        gr.drawLine(lx, ly, lx, ly);
                    }
                    else
                    {
                        if (_image_array[lxx] != 0)
                        {
                            gr.setColor(_foreground);
                            gr.drawLine(lx, ly, lx, ly);
                        }
                    }
                    lxx++;
                }
            }
        }

        Graphics g = _image_buffer.getGraphics();
        g.drawImage(_image_real,1,1,_image_width*ZOOM_FACTOR,_image_height*ZOOM_FACTOR,null);
        if(_grid_image!=null) g.drawImage(_grid_image,1,1,null);
        g.setColor(Color.black);
        g.drawRect(0,0,_image_width*ZOOM_FACTOR+1,_image_height*ZOOM_FACTOR+1);
    }

    public void setCommandState(int value)
    {
        _current_state = value;
    }

    protected void initImage(int wd,int hg)
    {
        _image_width = wd;
        _image_height = hg;

        _image_array = new int[_image_width * _image_height];
        _image_transact_array = new int[_image_width * _image_height];
        _image_transact = common.createImage(_parent_applet, _image_width, _image_height);
        _image_real = common.createImage(_parent_applet, _image_width, _image_height);
        _image_buffer = common.createImage(_parent_applet, _image_width*ZOOM_FACTOR+2, _image_height*ZOOM_FACTOR+2);

        _image_undo_array = new int[_image_width * _image_height];
        _image_redo_array = new int[_image_width * _image_height];

        _is_undo = false;
        _is_redo = false;

        clearRealImage();
        clearTransactImage();
        _transaction_is_started = false;

        _dim = new Dimension(_image_width * ZOOM_FACTOR, _image_height * ZOOM_FACTOR);
        setSize(_dim);

        updateImage();
    }

    public void setSize(int w, int h)
    {
        super.setSize(_dim.width, _dim.height);
    }

    public void setSize(Dimension dm)
    {
        super.setSize(_dim);
    }

    public EditPicture(Applet apl, int wd, int hg) throws IOException
    {
        super();
        _parent_applet = apl;
        initImage(wd,hg);

        addMouseListener(this);
        addMouseMotionListener(this);
        _dim = new Dimension(_image_width * ZOOM_FACTOR, _image_height * ZOOM_FACTOR);
        setCursor(new Cursor(Cursor.CROSSHAIR_CURSOR));

        try
        {
            _grid_image = common.loadImageResource(apl,"grid.gif");
        }
        catch (IOException e)
        {
            _grid_image = null;
        }

    }

    public void update(Graphics g)
    {
        paint(g);
    }

    public void paint(Graphics g)
    {
        g.drawImage(_image_buffer,0,0,null);

        if ((_select_rectangle != null) && (_current_state == GR_SELECT))
        {
            int llx = _select_rectangle.x * ZOOM_FACTOR;
            int lly = _select_rectangle.y * ZOOM_FACTOR;
            int llw = _select_rectangle.width * ZOOM_FACTOR ;
            int llh = _select_rectangle.height * ZOOM_FACTOR;

            g.setColor(_selectrect_color);
            g.drawRect(llx, lly, llw,llh);
            g.drawRect(llx+1, lly+1 , llw-2,llh-2);
        }
        g.setColor(Color.black);
        g.drawRect(0,0,_image_width * ZOOM_FACTOR+1, _image_height * ZOOM_FACTOR+1);
    }

}
