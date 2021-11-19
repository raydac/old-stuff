package com.raydac_research.FormEditor.RrgFormComponents;

import javax.swing.*;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.NoninvertibleTransformException;

public class JRrgFormContainer extends JComponent
{
    protected BufferedImage p_BufferImage;
    protected Graphics p_BufferGraphics;
    protected int i_Scale;
    protected int i_FormWidth, i_FormHeight;
    protected Dimension p_VisualSize = new Dimension(0, 0);
    protected boolean lg_drawGrid;
    protected boolean lg_drawTVGrid;
    protected boolean lg_drawRulers;
    protected int i_GridStep;
    protected Color p_GridColor = Color.gray;
    protected Color p_SelectedColor = Color.yellow;
    protected Color p_ResizingColor = Color.magenta;
    protected Color p_SelectedResizableColorRectangles = Color.green.brighter();
    protected Color p_SelectedNonRssizableColorRectangles = Color.red.brighter();
    protected static final int SELECTED_RECTANGLE_SIZE = 5;

    protected FormContainer p_FormContainer;
    protected AbstractFormComponent p_placedComponent;

    public static final int FILTER_NONE = 0;
    public static final int FILTER_NTSC = 1;
    public static final int FILTER_LCD444 = 2;
    public static final int FILTER_LCD332 = 3;

    protected int i_filterType;

    public static final int BORDER_WIDTH_HEIGHT = 40;

    private boolean lg_Resizing = false;
    private int i_resizingStartX = 0;
    private int i_resizingStartY = 0;
    private int i_resizingEndX = 0;
    private int i_resizingEndY = 0;
    private int i_resizeAnchorType = -1;

    public static final Color COLOR_HORIZONTAL_RULER = Color.green.darker();
    public static final Color COLOR_VERTICAL_RULER = Color.red.darker();
    public static final int RULER_INDICATOR_WIDTH = 8;

    public static final int RULES_WIDTH = 25;
    private static final int RULES_LABEL_SMALL_HEIGHT = RULES_WIDTH >> 2;
    private static final int RULES_LABEL_BIG_HEIGHT = RULES_WIDTH >> 1;

    private static final Color RULES_COLOR = Color.yellow.brighter();
    private static final Color RULES_LABEL_COLOR = Color.black;

    private static final int RULES_LABEL_STEP = 10;
    private static final Font RULES_FONT = new Font("Arial", Font.PLAIN, 8);
    private static final FontMetrics RULES_FONT_METRICS = Toolkit.getDefaultToolkit().getFontMetrics(RULES_FONT);

    public void setPlacedComponent(AbstractFormComponent _component)
    {
        p_placedComponent = _component;
        repaint();
    }

    public void changeResizeCoords(int _x, int _y)
    {
        int i_x = _x / getScale() - JRrgFormContainer.BORDER_WIDTH_HEIGHT;
        int i_y = _y / getScale() - JRrgFormContainer.BORDER_WIDTH_HEIGHT;

        i_resizingEndX = i_x;
        i_resizingEndY = i_y;
        repaint();
    }

    public boolean setResizingToComponent()
    {
        if (!lg_Resizing) return false;
        lg_Resizing = false;

        int i_x = Math.min(i_resizingStartX, i_resizingEndX);
        int i_y = Math.min(i_resizingStartY, i_resizingEndY);
        int i_w = (Math.max(i_resizingStartX, i_resizingEndX) - i_x);
        int i_h = Math.max(i_resizingStartY, i_resizingEndY) - i_y;

        i_w = i_w < p_placedComponent.getMinimumWidth() ? p_placedComponent.getMinimumWidth() : i_w;
        i_h = i_h < p_placedComponent.getMinimumHeight() ? p_placedComponent.getMinimumHeight() : i_h;

        int i_newX = p_placedComponent.getX();
        int i_newY = p_placedComponent.getY();
        int i_oldW = p_placedComponent.getWidth();
        int i_oldH = p_placedComponent.getHeight();

        switch (i_resizeAnchorType)
        {
            case RESIZABLE_DOWN_LEFT:
                {
                    if (i_resizingEndX >= i_resizingStartX || i_resizingEndY <= i_resizingStartY) return false;
                    i_newX += i_oldW - i_w;
                }
                ;
                break;
            case RESIZABLE_DOWN_RIGHT:
                {
                    if (i_resizingEndX <= i_resizingStartX || i_resizingEndY <= i_resizingStartY) return false;
                }
                ;
                break;
            case RESIZABLE_TOP_LEFT:
                {
                    if (i_resizingEndX >= i_resizingStartX || i_resizingEndY >= i_resizingStartY) return false;
                    i_newX += i_oldW - i_w;
                    i_newY += i_oldH - i_h;
                }
                ;
                break;
            case RESIZABLE_TOP_RIGHT:
                {
                    if (i_resizingEndX <= i_resizingStartX || i_resizingEndY >= i_resizingStartY) return false;
                    i_newY += i_oldH - i_h;
                }
                ;
                break;
        }

        p_placedComponent.setWidthHeight(i_w, i_h);
        p_placedComponent.setX(i_newX);
        p_placedComponent.setY(i_newY);

        return true;
    }

    public boolean isResizing()
    {
        return lg_Resizing;
    }

    public boolean takeToResizingSelectedComponentAt(int _x, int _y)
    {
        if (p_placedComponent == null) return false;
        if (!p_placedComponent.resizable() || p_placedComponent.isLocked()) return false;
        i_resizeAnchorType = overResizable(_x, _y);

        switch (i_resizeAnchorType)
        {
            case RESIZABLE_NONE:
                {
                    return false;
                }
            case RESIZABLE_TOP_RIGHT:
                {
                    i_resizingStartX = p_placedComponent.getX();
                    i_resizingStartY = p_placedComponent.getY() + p_placedComponent.getHeight();
                    i_resizingEndX = p_placedComponent.getX() + p_placedComponent.getWidth();
                    i_resizingEndY = p_placedComponent.getY();
                }
                ;
                break;
            case RESIZABLE_TOP_LEFT:
                {
                    i_resizingStartX = p_placedComponent.getX() + p_placedComponent.getWidth();
                    i_resizingStartY = p_placedComponent.getY() + p_placedComponent.getHeight();
                    i_resizingEndX = p_placedComponent.getX();
                    i_resizingEndY = p_placedComponent.getY();
                }
                ;
                break;
            case RESIZABLE_DOWN_RIGHT:
                {
                    i_resizingStartX = p_placedComponent.getX();
                    i_resizingStartY = p_placedComponent.getY();
                    i_resizingEndX = p_placedComponent.getX() + p_placedComponent.getWidth();
                    i_resizingEndY = p_placedComponent.getY() + p_placedComponent.getHeight();
                }
                ;
                break;
            case JRrgFormContainer.RESIZABLE_DOWN_LEFT:
                {
                    i_resizingStartX = p_placedComponent.getX() + p_placedComponent.getWidth();
                    i_resizingStartY = p_placedComponent.getY();
                    i_resizingEndX = p_placedComponent.getX();
                    i_resizingEndY = p_placedComponent.getY() + p_placedComponent.getHeight();
                }
                ;
                break;
        }
        lg_Resizing = true;
        repaint();
        return true;
    }

    public void setTVGrid(boolean _flag)
    {
        lg_drawTVGrid = _flag;
    }

    public void setRulers(boolean _flag)
    {
        lg_drawRulers = _flag;
    }

    public void setGrid(boolean _flag)
    {
        lg_drawGrid = _flag;
    }

    public AbstractFormComponent getPlacedComponent()
    {
        return p_placedComponent;
    }

    public void setFormContainer(FormContainer _container)
    {
        p_FormContainer = _container;
        setFormSize(_container.getWidth(), _container.getHeight());
        setBackground(_container.getBackgroundColor());
        fillBufferImage();
        repaint();
    }

    AffineTransform p_rotate1transorm;
    AffineTransform p_rotate1transormInv;

    public JRrgFormContainer()
    {
        setOpaque(false);
        lg_drawTVGrid = false;
        lg_drawGrid = false;
        lg_drawRulers = false;
        i_Scale = 1;
        i_filterType = FILTER_NONE;

        p_rotate1transorm = new AffineTransform();
        p_rotate1transorm.setToRotation(-Math.PI / 2.0d);

        p_rotate1transormInv = null;
        try
        {
            p_rotate1transormInv = p_rotate1transorm.createInverse();
        }
        catch (NoninvertibleTransformException e)
        {
            return;
        }
    }

    public void setFormSize(int _width, int _height)
    {
        p_BufferImage = new BufferedImage(_width, _height, BufferedImage.TYPE_INT_RGB);
        p_BufferGraphics = p_BufferImage.getGraphics();

        i_FormWidth = _width;
        i_FormHeight = _height;
        setScale(i_Scale);
        fillBufferImage();
    }

    public void setGridColor(Color _newColor)
    {
        p_GridColor = _newColor;
    }

    public int getScale()
    {
        return i_Scale;
    }

    public void setScale(int _scale)
    {
        i_Scale = _scale;
        int i_width = (i_FormWidth + (BORDER_WIDTH_HEIGHT << 1)) * i_Scale;
        int i_height = (i_FormHeight + (BORDER_WIDTH_HEIGHT << 1)) * i_Scale;
        p_VisualSize.setSize(i_width, i_height);
        i_GridStep = _scale;
    }

    public Dimension getSize(Dimension rv)
    {
        return p_VisualSize;
    }

    public Dimension getPreferredSize()
    {
        return p_VisualSize;
    }

    public Dimension getMaximumSize()
    {
        return p_VisualSize;
    }

    private boolean _checkPointAtRectangle(int _x, int _y, int _w, int _h, int _px, int _py)
    {
        if (_px < _x || _py < _y) return false;
        if (_x + _w < _px || _y + _h < _py) return false;
        return true;
    }

    public static final int RESIZABLE_NONE = -1;
    public static final int RESIZABLE_TOP_LEFT = 0;
    public static final int RESIZABLE_TOP_RIGHT = 1;
    public static final int RESIZABLE_DOWN_LEFT = 2;
    public static final int RESIZABLE_DOWN_RIGHT = 3;

    public int overResizable(int x, int y)
    {
        if (p_placedComponent != null)
        {
            if (p_placedComponent.isPinned() || p_placedComponent.isLocked()) return RESIZABLE_NONE;
            if (!p_placedComponent.resizable()) return RESIZABLE_NONE;

            int i_x = (p_placedComponent.getX() + BORDER_WIDTH_HEIGHT) * i_Scale;
            int i_y = (p_placedComponent.getY() + BORDER_WIDTH_HEIGHT) * i_Scale;
            int i_width = p_placedComponent.getWidth() * i_Scale;
            int i_height = p_placedComponent.getHeight() * i_Scale;

            if (_checkPointAtRectangle(i_x - (SELECTED_RECTANGLE_SIZE >> 1), i_y - (SELECTED_RECTANGLE_SIZE >> 1), SELECTED_RECTANGLE_SIZE, SELECTED_RECTANGLE_SIZE, x, y))
                return RESIZABLE_TOP_LEFT;
            else if (_checkPointAtRectangle(i_x + i_width - (SELECTED_RECTANGLE_SIZE >> 1), i_y - (SELECTED_RECTANGLE_SIZE >> 1), SELECTED_RECTANGLE_SIZE, SELECTED_RECTANGLE_SIZE, x, y))
                return RESIZABLE_TOP_RIGHT;
            else if (_checkPointAtRectangle(i_x - (SELECTED_RECTANGLE_SIZE >> 1), i_y + i_height - (SELECTED_RECTANGLE_SIZE >> 1), SELECTED_RECTANGLE_SIZE, SELECTED_RECTANGLE_SIZE, x, y))
                return RESIZABLE_DOWN_LEFT;
            else if (_checkPointAtRectangle(i_x + i_width - (SELECTED_RECTANGLE_SIZE >> 1), i_y + i_height - (SELECTED_RECTANGLE_SIZE >> 1), SELECTED_RECTANGLE_SIZE, SELECTED_RECTANGLE_SIZE, x, y)) return RESIZABLE_DOWN_RIGHT;
        }

        return RESIZABLE_NONE;
    }

    public static final int OVER_RULER_DEAD_ZONE = 0;
    public static final int OVER_RULER_HORIZ = 1;
    public static final int OVER_RULER_VERT = 2;
    public static final int OVER_RULER_NONE = 3;

    public FormRuler getFocusedRuler(int _x, int _y, int _mouseRulePosition)
    {
        if (p_FormContainer == null || !lg_drawRulers) return null;
        int i_nRules = p_FormContainer.getRulersNumber();
        for (int li = 0; li < i_nRules; li++)
        {
            FormRuler p_ruler = p_FormContainer.getRulerForIndex(li);

            switch (p_ruler.getType())
            {
                case FormRuler.TYPE_HORIZ:
                    {
                        if (_mouseRulePosition == OVER_RULER_HORIZ) continue;
                        int i_y = (p_ruler.getCoord()+BORDER_WIDTH_HEIGHT) * i_Scale;
                        if (_y>=(i_y-(RULER_INDICATOR_WIDTH>>1)) && _y<=(i_y+(RULER_INDICATOR_WIDTH>>1))) return p_ruler;
                    }
                    ;
                    break;
                case FormRuler.TYPE_VERT:
                    {
                        if (_mouseRulePosition == OVER_RULER_VERT) continue;
                        int i_x = (p_ruler.getCoord()+BORDER_WIDTH_HEIGHT) * i_Scale;
                        if (_x>=(i_x-(RULER_INDICATOR_WIDTH>>1)) && _x<=(i_x+(RULER_INDICATOR_WIDTH>>1))) return p_ruler;
                    }
                    ;
                    break;
            }
        }
        return null;
    }

    public int isOverRulers(int _x, int _y)
    {
        if (!lg_drawRulers) return OVER_RULER_NONE;
        Rectangle p_rect = ((JViewport) getParent()).getViewRect();

        int i_x = _x - p_rect.x;
        int i_y = _y - p_rect.y;

        if (i_x < 0 || i_y < 0) return OVER_RULER_NONE;

        if (i_x < RULES_WIDTH && i_y < RULES_WIDTH) return OVER_RULER_DEAD_ZONE;

        if (i_y < RULES_WIDTH) return OVER_RULER_HORIZ;
        if (i_x < RULES_WIDTH) return OVER_RULER_VERT;

        return OVER_RULER_NONE;
    }

    public boolean contains(int x, int y)
    {
        return super.contains(x, y);
//        if (x >= 0 && x < p_VisualSize.width)
//            if (y >= 0 && y < p_VisualSize.height) return true;
//        return false;
    }

    public Dimension getMinimumSize()
    {
        return p_VisualSize;
    }

    public void setFilter(int _filter)
    {
        if (i_filterType != _filter)
        {
            i_filterType = _filter;
            fillBufferImage();
        }
    }

    public int getFilter()
    {
        return i_filterType;
    }

    public void fillBufferImage()
    {
        p_BufferGraphics.setColor(getBackground());
        p_BufferGraphics.fillRect(0, 0, i_FormWidth, i_FormHeight);

        AbstractFormComponent[] ap_Elements = p_FormContainer.getComponentArray();
        int i_size = p_FormContainer.getSize();

        if (p_placedComponent == null)
        {
            for (int li = 0; li < i_size; li++)
            {
                if (ap_Elements[li].isHidden()) continue;
                ap_Elements[li].paintContent(p_BufferGraphics, false);
            }
        }
        else
        {
            for (int li = 0; li < i_size; li++)
            {
                if (ap_Elements[li].isHidden()) continue;
                if (p_placedComponent.equals(ap_Elements[li]))
                    ap_Elements[li].paintContent(p_BufferGraphics, true);
                else
                    ap_Elements[li].paintContent(p_BufferGraphics, false);
            }
        }

        switch (i_filterType)
        {
            case FILTER_NTSC:
                filterNTSC(p_BufferImage);
                break;
            case FILTER_LCD444:
                filterRGB444(p_BufferImage);
                break;
            case FILTER_LCD332:
                filterRGB332(p_BufferImage);
                break;
        }

        repaint();
    }

    private void filterNTSC(BufferedImage _RGBimage)
    {
        int[] ai_ImageBuffer = ((DataBufferInt) _RGBimage.getRaster().getDataBuffer()).getData();

        int i_len = ai_ImageBuffer.length;

        int i_width = _RGBimage.getWidth();
        int i_maxIndex = i_width - 1;

        int i_pointIndex = 0;

        int i_Ei = 0;
        int i_Eq = 0;

        int i_Ei_prev = 0;
        int i_Eq_prev = 0;
        int i_Y = 0;
        int i_Y_prev = 0;
        int i_rgb, i_r, i_g, i_b;

        int i_vertLine = 0;

        for (int li = 0; li < i_len; li++)
        {
            if ((i_pointIndex & 1) == 0)
            {
                i_rgb = ai_ImageBuffer[li];

                i_r = (i_rgb >> 16) & 0xFF;
                i_g = (i_rgb >> 8) & 0xFF;
                i_b = i_rgb & 0xFF;

                //Y = 0.299d * i_r + 0.587d * i_g + 0.114d * i_b;
                i_Y = (0x4C * i_r + 0x96 * i_g + 0x1D * i_b);

                //Ei = 0.596d * i_r - 0.274d * i_g - 0.322d * i_b;
                i_Ei = (0x98 * i_r - 0x46 * i_g - 0x52 * i_b) >> 8;

                //Eq = 0.211d * i_r - 0.523d * i_g + 0.312d * i_b;
                i_Eq = (0x36 * i_r - 0x86 * i_g + 0x50 * i_b) >> 8;

                i_Ei_prev = i_Ei;
                i_Eq_prev = i_Eq;

                // Легкое смазывание яркости
                i_Y += ((i_Y_prev - i_Y) >> 1);

                if ((i_vertLine & 1) != 0) i_Y -= 0x1800;

                if (i_Y > 0xF700)
                    i_Y = 0xF700;
                else if (i_Y < 0x800) i_Y = 0x800;
            }
            else
            {
                i_rgb = ai_ImageBuffer[li];

                i_r = (i_rgb >> 16) & 0xFF;
                i_g = (i_rgb >> 8) & 0xFF;
                i_b = i_rgb & 0xFF;

                i_Y = (0x4C * i_r + 0x96 * i_g + 0x1D * i_b);

                if (i_pointIndex != i_maxIndex)
                {
                    i_rgb = ai_ImageBuffer[li + 1];
                    i_r = (i_rgb >> 16) & 0xFF;
                    i_g = (i_rgb >> 8) & 0xFF;
                    i_b = i_rgb & 0xFF;

                    i_Ei = (0x98 * i_r - 0x46 * i_g - 0x52 * i_b) >> 8;
                    i_Eq = (0x36 * i_r - 0x86 * i_g + 0x50 * i_b) >> 8;

                    i_Ei = (i_Ei + i_Ei_prev) >> 1;
                    i_Eq = (i_Eq + i_Eq_prev) >> 1;
                }
                else
                {
                    i_Ei = i_Ei_prev >> 1;
                    i_Eq = i_Eq_prev >> 1;
                }

                // Легкое смазывание яркости
                i_Y += ((i_Y_prev - i_Y) >> 1);

                if ((i_vertLine & 1) != 0) i_Y -= 0x1800;

                if (i_Y > 0xF700)
                    i_Y = 0xF700;
                else if (i_Y < 0x800) i_Y = 0x800;
            }

            i_Y_prev = i_Y;



            // r = y + .95617 * u + .62143 * v;
            i_r = (i_Y + 0xF5 * i_Ei + 0x9F * i_Eq) >> 8;
            if (i_r < 0)
                i_r = 0;
            else if (i_r > 255) i_r = 255;

            // g = y - 0.27269 * u - .64681 * v;
            i_g = (i_Y - 0x46 * i_Ei - 0xA6 * i_Eq) >> 8;
            if (i_g < 0)
                i_g = 0;
            else if (i_g > 255) i_g = 255;

            //b = y - 1.10374 * u + 1.70062 * v;
            i_b = (i_Y - 0x11A * i_Ei + 0x1B3 * i_Eq) >> 8;

            if (i_b < 0)
                i_b = 0;
            else if (i_b > 255) i_b = 255;

            i_rgb = (i_r << 16) | (i_g << 8) | i_b;

            ai_ImageBuffer[li] = i_rgb;

            i_pointIndex++;
            if (i_pointIndex == i_width)
            {
                i_pointIndex = 0;
                i_vertLine++;
            }
        }
    }

    private void filterRGB444(BufferedImage _RGBimage)
    {
        int[] ai_ImageBuffer = ((DataBufferInt) _RGBimage.getRaster().getDataBuffer()).getData();

        int i_len = ai_ImageBuffer.length;

        for (int li = 0; li < i_len; li++)
        {
            int i_rgb = ai_ImageBuffer[li];
            int i_r = (i_rgb >>> 16) & 0xFF;
            int i_g = (i_rgb >>> 8) & 0xFF;
            int i_b = i_rgb & 0xFF;

            i_r = (i_r>>4)*0x11+0x10;
            i_g = (i_g>>4)*0x11+0x10;
            i_b = (i_b>>4)*0x11+0x10;

            if (i_r > 0xFF) i_r = 0xFF;
            if (i_g > 0xFF) i_g = 0xFF;
            if (i_b > 0xFF) i_b = 0xFF;


            ai_ImageBuffer[li] = (i_r<<16) | (i_g<<8) | i_b;
        }
    }

    private void filterRGB332(BufferedImage _RGBimage)
    {
        int[] ai_ImageBuffer = ((DataBufferInt) _RGBimage.getRaster().getDataBuffer()).getData();

        int i_len = ai_ImageBuffer.length;

        for (int li = 0; li < i_len; li++)
        {
            int i_rgb = ai_ImageBuffer[li];
            int i_r = (i_rgb >>> 16) & 0xFF;
            int i_g = (i_rgb >>> 8) & 0xFF;
            int i_b = i_rgb & 0xFF;

            i_r = i_r | 0x1F;
            i_g = i_g | 0x1F;
            i_b = i_b | 0x3F;

            ai_ImageBuffer[li] = (i_r<<16) | (i_g<<8) | i_b;
        }
    }

    private void _drawTVGrid(Graphics _g)
    {
        int i_BWH = BORDER_WIDTH_HEIGHT * i_Scale;
        int i_BWW = BORDER_WIDTH_HEIGHT * i_Scale;
        int i_FW = i_Scale * i_FormWidth;
        int i_FH = i_Scale * i_FormHeight;

        float f_stpX = i_FW / 30f;
        float f_stpY = i_FH / 9f;

        _g.setColor(Color.darkGray);

        for (float lx = 0; lx < i_FW; lx += f_stpX)
        {
            int i_lx = Math.round(lx);
            _g.drawLine(i_BWW + i_lx, i_BWH, i_BWW + i_lx, i_BWH + i_FH);
        }

        for (float ly = 0; ly < i_FH; ly += f_stpY)
        {
            int i_ly = Math.round(ly);
            _g.drawLine(i_BWW, i_BWH + i_ly, i_BWW + i_FW, i_BWH + i_ly);
        }

        _g.setColor(Color.lightGray);
        _g.drawLine(i_BWW, i_BWH, i_BWW + i_FW, i_BWH + i_FH);
        _g.drawLine(i_BWW, i_BWH + i_FH, i_BWW + i_FW, i_BWH);

        int i_rw = Math.round(0.95f * i_FW);
        int i_rh = Math.round(0.89f * i_FH);

        int i_dx = (i_FW - i_rw) / 2;
        int i_dy = (i_FH - i_rh) / 2;

        int i_rX = Math.round(i_rw * 0.30f);
        int i_rY = Math.round(i_rh * 0.50f);

        _g.drawRoundRect(i_BWW + i_dx, i_BWH + i_dy, i_rw, i_rh, i_rX, i_rY);

        i_rw = Math.round(0.8333f * i_FW);
        i_rh = Math.round(0.7777f * i_FH);

        i_dx = (i_FW - i_rw) / 2;
        i_dy = (i_FH - i_rh) / 2;

        i_rX = Math.round(i_rw * 0.30f);
        i_rY = Math.round(i_rh * 0.50f);

        _g.drawRoundRect(i_BWW + i_dx, i_BWH + i_dy, i_rw, i_rh, i_rX, i_rY);

        _g.drawLine(i_BWW + (i_FW >> 1), i_BWW, i_BWW + (i_FW >> 1), i_BWW + i_FH);

        _g.drawLine(i_BWW, i_BWW + (i_FH >> 1), i_BWW + i_FW, i_BWW + (i_FH >> 1));
    }

    public int getResizingWidth()
    {
        int i_x = Math.min(i_resizingStartX, i_resizingEndX);
        int i_w = Math.max(i_resizingStartX, i_resizingEndX) - i_x;
        return i_w;
    }

    public int getResizingHeight()
    {
        int i_y = Math.min(i_resizingStartY, i_resizingEndY);
        int i_h = Math.max(i_resizingStartY, i_resizingEndY) - i_y;
        return i_h;
    }

    private void drawRulers(Graphics2D g, Rectangle _viewRect)
    {
        if (p_FormContainer == null) return;

        synchronized (getParent())
        {
            Rectangle p_ViewRect = _viewRect;

            AffineTransform p_transform = g.getTransform();

            g.setColor(RULES_COLOR);
            g.setFont(RULES_FONT);

            // Вертикальная линейка
            g.fillRect(p_ViewRect.x, p_ViewRect.y, RULES_WIDTH, p_ViewRect.y + p_ViewRect.height);
            // Горизонтальная линейка
            g.fillRect(p_ViewRect.x, p_ViewRect.y, p_ViewRect.x + p_ViewRect.width, RULES_WIDTH);
            // Граница
            g.setColor(Color.black);
            int i_xx = p_ViewRect.x + 1 + RULES_WIDTH;
            int i_yy = p_ViewRect.y + 1 + RULES_WIDTH;
            g.drawLine(i_xx, i_yy, i_xx + p_ViewRect.width, i_yy);
            g.drawLine(i_xx, i_yy, i_xx, i_yy + p_ViewRect.height);

            int i_rulesStep = RULES_LABEL_STEP * i_Scale;

            final int i_BWW = BORDER_WIDTH_HEIGHT * i_Scale;

            int i_fontHeight = RULES_FONT_METRICS.getHeight();

            g.setColor(RULES_LABEL_COLOR);
            // горизонтальные метки
            int i_xStart = ((p_ViewRect.x + RULES_WIDTH) / i_rulesStep) * i_rulesStep;
            int i_xEnd = p_ViewRect.x + p_ViewRect.width;
            int i_Yoffset = p_ViewRect.y;
            int i_Xoffset = (p_ViewRect.x + RULES_WIDTH) % i_rulesStep;
            for (int li = i_xStart; li < i_xEnd; li++)
            {
                if ((i_Xoffset + li) % i_rulesStep == 0)
                {
                    int i_xc = i_Xoffset + li;

                    // Большая метка
                    if (i_Scale == 1)
                    {
                        g.drawLine(i_xc, i_Yoffset, i_xc, i_Yoffset + RULES_LABEL_BIG_HEIGHT - 1);
                    }
                    else
                    {
                        g.fillRect(i_xc - 1, i_Yoffset, 3, RULES_LABEL_BIG_HEIGHT);
                    }
                    if (i_Scale != 1)
                    {
                        // Координата
                        String s_coord = Integer.toString((i_xc - i_BWW) / i_Scale);
                        int i_strWdth = RULES_FONT_METRICS.stringWidth(s_coord);
                        g.drawString(s_coord, i_xc - (i_strWdth >> 1), i_Yoffset + RULES_LABEL_BIG_HEIGHT + i_fontHeight);
                    }
                }
                else
                {
                    if (i_Scale > 2)
                        if ((i_Xoffset + li) % i_Scale == 0)
                        {
                            // Малая метка
                            g.drawLine(i_Xoffset + li, i_Yoffset, i_Xoffset + li, i_Yoffset + RULES_LABEL_SMALL_HEIGHT - 1);
                        }
                }
            }

            // Вертикальные метки
            int i_yStart = ((p_ViewRect.y + RULES_WIDTH) / i_rulesStep) * i_rulesStep;
            int i_yEnd = p_ViewRect.y + p_ViewRect.height;
            i_Xoffset = p_ViewRect.x;
            i_Yoffset = (p_ViewRect.y + RULES_WIDTH) % i_rulesStep;

            for (int li = i_yStart; li < i_yEnd; li++)
            {
                int i_yc = i_Yoffset + li;
                if (i_yc % i_rulesStep == 0)
                {
                    // Большая метка
                    if (i_Scale == 1)
                    {
                        g.drawLine(i_Xoffset, i_yc, i_Xoffset + RULES_LABEL_BIG_HEIGHT - 1, li + i_Yoffset);
                    }
                    else
                    {
                        g.fillRect(i_Xoffset, i_yc - 1, RULES_LABEL_BIG_HEIGHT, 3);
                    }


                    if (i_Scale > 1)
                    {
                        // Координата
                        String s_coord = Integer.toString((i_yc - i_BWW) / i_Scale);
                        int i_strWdth = RULES_FONT_METRICS.stringWidth(s_coord);
                        g.translate(i_Xoffset + RULES_LABEL_BIG_HEIGHT + i_fontHeight, i_yc);
                        g.transform(p_rotate1transorm);
                        g.drawString(s_coord, 0 - (i_strWdth >> 1), 0);
                        g.transform(p_rotate1transormInv);
                        g.translate(-(i_Xoffset + RULES_LABEL_BIG_HEIGHT + i_fontHeight), -i_yc);
                    }
                }
                else
                {
                    if (i_Scale > 2)
                        if ((i_Yoffset + li) % i_Scale == 0)
                        {
                            // Малая метка
                            g.drawLine(i_Xoffset, li + i_Yoffset, i_Xoffset + RULES_LABEL_SMALL_HEIGHT - 1, li + i_Yoffset);
                        }
                }
            }

            for (int li = 0; li < p_FormContainer.getRulersNumber(); li++)
            {
                FormRuler p_ruler = p_FormContainer.getRulerForIndex(li);
                int i_coord = p_ruler.i_Coord * i_Scale + i_BWW;

                switch (p_ruler.getType())
                {
                    case FormRuler.TYPE_VERT:
                        {
                            Color p_color = COLOR_VERTICAL_RULER;
                            g.setColor(p_color);
                            // Проверка видимости
                            if ((i_coord >= (p_ViewRect.x + RULES_WIDTH)) && (i_coord <= (p_ViewRect.x + p_ViewRect.width)))
                            {
                                int i_x = i_coord;
                                g.fill3DRect(i_x - (RULER_INDICATOR_WIDTH >> 1), p_ViewRect.y, RULER_INDICATOR_WIDTH, RULES_WIDTH, true);
                                g.drawLine(i_x, p_ViewRect.y + RULES_WIDTH, i_x, p_ViewRect.y + p_ViewRect.height);
                            }
                        }
                        ;
                        break;
                    case FormRuler.TYPE_HORIZ:
                        {
                            Color p_color = COLOR_HORIZONTAL_RULER;
                            g.setColor(p_color);
                            // Проверка видимости
                            if ((i_coord >= (p_ViewRect.y + RULES_WIDTH)) && (i_coord <= (p_ViewRect.y + p_ViewRect.height)))
                            {
                                int i_y = i_coord;
                                g.fill3DRect(p_ViewRect.x, i_y - (RULER_INDICATOR_WIDTH >> 1), RULES_WIDTH, RULER_INDICATOR_WIDTH, true);
                                g.drawLine(p_ViewRect.x + RULES_WIDTH, i_y, p_ViewRect.x + p_ViewRect.width, i_y);
                            }
                        }
                        ;
                        break;
                }
            }

            g.setTransform(p_transform);
        }
    }

    public void doubleClickOnRulers(int _x, int _y, boolean _leftKey)
    {
        int i_overRulersState = isOverRulers(_x, _y);

        int i_xx = (_x - BORDER_WIDTH_HEIGHT * i_Scale) / i_Scale;
        int i_yy = (_y - BORDER_WIDTH_HEIGHT * i_Scale) / i_Scale;

        FormRuler p_newRulerIndicator = null;

        switch (i_overRulersState)
        {
            case OVER_RULER_DEAD_ZONE:
            case OVER_RULER_NONE:
                return;
            case OVER_RULER_VERT:
                {
                    if (!_leftKey)
                    {
                        // Добавляем
                        p_newRulerIndicator = new FormRuler(FormRuler.TYPE_HORIZ, i_yy);
                    }
                    else
                    {
                        // Удаляем
                        FormRuler p_focusedRuler = getFocusedRuler(_x,_y,i_overRulersState);
                        if (p_focusedRuler!=null)
                        {
                            p_FormContainer.removeRuler(p_focusedRuler);
                        }
                    }
                }
                ;
                break;
            case OVER_RULER_HORIZ:
                {
                    if (!_leftKey)
                    {
                        // Добавляем
                        p_newRulerIndicator = new FormRuler(FormRuler.TYPE_VERT, i_xx);
                    }
                    else
                    {
                        // Удаляем
                        FormRuler p_focusedRuler = getFocusedRuler(_x,_y,i_overRulersState);
                        if (p_focusedRuler!=null)
                        {
                            p_FormContainer.removeRuler(p_focusedRuler);
                        }
                    }
                }
                ;
                break;
        }

        if (p_newRulerIndicator != null)
        {
            p_FormContainer.addRuler(p_newRulerIndicator);
        }
        repaint();
    }

    public void paint(Graphics g)
    {
        g.drawImage(p_BufferImage, BORDER_WIDTH_HEIGHT * i_Scale, BORDER_WIDTH_HEIGHT * i_Scale, i_FormWidth * i_Scale, i_FormHeight * i_Scale, null);

        // Drawing of the grid and the TV grid
        if (lg_drawGrid && i_Scale > 1)
        {
            g.setColor(p_GridColor);

            int i_BWH = BORDER_WIDTH_HEIGHT * i_Scale;
            int i_BWW = BORDER_WIDTH_HEIGHT * i_Scale;
            int i_FW = i_Scale * i_FormWidth + i_BWW;
            int i_FH = i_Scale * i_FormHeight + i_BWH;

            for (int ly = i_BWH; ly < i_FH; ly += i_GridStep)
            {
                g.drawLine(i_BWW, ly, i_FW, ly);
            }
            for (int lx = i_BWW; lx < i_FW; lx += i_GridStep)
            {
                g.drawLine(lx, i_BWH, lx, i_FH);
            }
        }

        if (lg_drawTVGrid)
        {
            _drawTVGrid(g);
        }

        if (lg_Resizing)
        {
            int i_x = Math.min(i_resizingStartX, i_resizingEndX);
            int i_y = Math.min(i_resizingStartY, i_resizingEndY);
            int i_w = Math.max(i_resizingStartX, i_resizingEndX) - i_x;
            int i_h = Math.max(i_resizingStartY, i_resizingEndY) - i_y;

            i_x += BORDER_WIDTH_HEIGHT;
            i_y += BORDER_WIDTH_HEIGHT;

            g.setColor(p_ResizingColor);

            g.drawRect(i_x * getScale(), i_y * getScale(), i_w * getScale(), i_h * getScale());
        }
        else if (p_placedComponent != null)
        {
            int i_x = (p_placedComponent.getX() + BORDER_WIDTH_HEIGHT) * i_Scale;
            int i_y = (p_placedComponent.getY() + BORDER_WIDTH_HEIGHT) * i_Scale;
            int i_width = p_placedComponent.getWidth() * i_Scale;
            int i_height = p_placedComponent.getHeight() * i_Scale;
            g.setColor(p_SelectedColor);
            g.drawRect(i_x, i_y, i_width, i_height);

            if (p_placedComponent.resizable() && !p_placedComponent.isLocked() && !p_placedComponent.isPinned())
                g.setColor(p_SelectedResizableColorRectangles);
            else
                g.setColor(p_SelectedNonRssizableColorRectangles);

            g.fill3DRect(i_x - (SELECTED_RECTANGLE_SIZE >> 1), i_y - (SELECTED_RECTANGLE_SIZE >> 1), SELECTED_RECTANGLE_SIZE, SELECTED_RECTANGLE_SIZE, true);
            g.fill3DRect(i_x + i_width - (SELECTED_RECTANGLE_SIZE >> 1), i_y - (SELECTED_RECTANGLE_SIZE >> 1), SELECTED_RECTANGLE_SIZE, SELECTED_RECTANGLE_SIZE, true);
            g.fill3DRect(i_x - (SELECTED_RECTANGLE_SIZE >> 1), i_y + i_height - (SELECTED_RECTANGLE_SIZE >> 1), SELECTED_RECTANGLE_SIZE, SELECTED_RECTANGLE_SIZE, true);
            g.fill3DRect(i_x + i_width - (SELECTED_RECTANGLE_SIZE >> 1), i_y + i_height - (SELECTED_RECTANGLE_SIZE >> 1), SELECTED_RECTANGLE_SIZE, SELECTED_RECTANGLE_SIZE, true);
        }

        // Рисуем линейки
        if (lg_drawRulers) drawRulers((Graphics2D) g, ((JViewport) getParent()).getViewRect());
    }
}
