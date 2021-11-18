package com.raydac_research.FormEditor.RrgFormComponents;

import com.raydac_research.FormEditor.RrgFormResources.AbstractRrgResource;
import com.raydac_research.FormEditor.RrgFormResources.ResourceContainer;

import java.io.PrintStream;
import java.io.IOException;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.util.Vector;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class RrgFormComponent_Path extends AbstractFormComponent
{
    public Vector p_PointVector;
    public Vector p_PointVector_trans;

    public PathPoint p_MainPoint;
    public PathPoint p_MainPoint_trans;

    public int i_PathType;
    public int i_PathType_trans;

    public boolean lg_showSteps = false;

    public boolean lg_showSteps_trans = false;

    private final int CIR_RAD = 3;

    public Color p_pathViewColor;
    public Color p_pathViewColor_trans;

    public static final int PATH_NORMAL = 0;
    public static final int PATH_CYCLIC = 1;
    public static final int PATH_PENDULUM = 2;

    private final int MIN_WIDTH = 32;
    private final int MIN_HEIGHT = 32;

    public void copyTo(AbstractFormComponent _component)
    {
        synchronized (_component)
        {
            super.copyTo(_component);
            RrgFormComponent_Path p_comp = (RrgFormComponent_Path) _component;

            p_comp.i_PathType = i_PathType;
            p_comp.lg_showSteps = lg_showSteps;
            p_comp.p_pathViewColor = p_pathViewColor;

            Vector p_newVector = new Vector(p_PointVector.size());
            for (int li = 0; li < p_PointVector.size(); li++)
            {
                p_newVector.add(((PathPoint) p_PointVector.elementAt(li)).clone());
            }

            p_comp.p_PointVector = p_newVector;
            p_comp.p_MainPoint = (PathPoint) p_MainPoint.clone();

            p_comp.resourceUpdated();
        }
    }

    public void startTransaction()
    {
        super.startTransaction();

        p_PointVector_trans = new Vector(p_PointVector.size());
        for (int li = 0; li < p_PointVector.size(); li++)
        {
            PathPoint p_point = (PathPoint) p_PointVector.elementAt(li);
            p_PointVector_trans.add(p_point.clone());
        }

        p_MainPoint_trans = (PathPoint) p_MainPoint.clone();

        i_PathType_trans = i_PathType;
        lg_showSteps_trans = lg_showSteps;
        p_pathViewColor_trans = p_pathViewColor;
    }

    public void rollbackTransaction(boolean _anyway)
    {
        if (!lg_transacted && !_anyway) return;
        super.rollbackTransaction(_anyway);

        PathPoint p_MainPoint_p = p_MainPoint;
        p_MainPoint = p_MainPoint_trans;
        p_MainPoint_trans = p_MainPoint_p;

        Vector p_PointVector_p = p_PointVector;
        p_PointVector = p_PointVector_trans;
        p_PointVector_trans = p_PointVector_p;

        boolean lg_showSteps_p = lg_showSteps;
        lg_showSteps = lg_showSteps_trans;
        lg_showSteps_trans = lg_showSteps_p;

        Color p_pathViewColor_p = p_pathViewColor;
        p_pathViewColor = p_pathViewColor_trans;
        p_pathViewColor_trans = p_pathViewColor_p;
    }

    public RrgFormComponent_Path(FormContainer _parent, String _id)
    {
        super(_parent, _id, AbstractFormComponent.COMPONENT_PATH, 32, 32);
        p_MainPoint = new PathPoint(0, 0, 0);
        p_PointVector = new Vector();
        i_PathType = PATH_NORMAL;
        p_pathViewColor = Color.magenta;
    }

    public RrgFormComponent_Path()
    {
        super();
        p_MainPoint = null;
        p_PointVector = null;
        i_PathType = PATH_NORMAL;
        p_pathViewColor = Color.magenta;
    }

    public void resourceUpdated()
    {

    }

    public Dimension getPathDimension()
    {
        Dimension p_result = new Dimension(0,0);
        int i_maxx = 0;
        int i_maxy = 0;
        int i_minx = 0;
        int i_miny = 0;

        for (int li = 0; li < p_PointVector.size(); li++)
        {
            PathPoint p_point = (PathPoint) p_PointVector.elementAt(li);
            int i_gx = p_point.i_X;
            int i_gy = p_point.i_Y;
            if (i_gx > i_maxx) i_maxx = i_gx;
            if (i_gy > i_maxy) i_maxy = i_gy;
            if (i_gx < i_minx) i_minx = i_gx;
            if (i_gy < i_miny) i_miny = i_gy;
        }

        int i_w = Math.abs(i_maxx - i_minx);
        int i_h = Math.abs(i_maxy - i_miny);

        p_result.width = i_w;
        p_result.height = i_h;

        return p_result;
    }

    public void updateCoordsForPoints()
    {
        int i_maxx = 0;
        int i_maxy = 0;
        int i_minx = 0;
        int i_miny = 0;

        int i_mCoordX = p_MainPoint.i_X + i_X;
        int i_mCoordY = p_MainPoint.i_Y + i_Y;

        for (int li = 0; li < p_PointVector.size(); li++)
        {
            PathPoint p_point = (PathPoint) p_PointVector.elementAt(li);
            int i_gx = p_point.i_X;
            int i_gy = p_point.i_Y;
            if (i_gx > i_maxx) i_maxx = i_gx;
            if (i_gy > i_maxy) i_maxy = i_gy;
            if (i_gx < i_minx) i_minx = i_gx;
            if (i_gy < i_miny) i_miny = i_gy;
        }

        int i_w = Math.abs(i_maxx - i_minx);
        int i_h = Math.abs(i_maxy - i_miny);

        if (i_w < MIN_WIDTH)
        {
            setWidthHeight(MIN_WIDTH, getHeight());
        }
        else
        {
            i_minx -= 4;
            i_Width = i_w+8;
        }

        if (i_h < MIN_HEIGHT)
        {
            setWidthHeight(getWidth(), MIN_HEIGHT);
        }
        else
        {
            i_miny -= 4;
            i_Height = i_h+8;
        }

        setX(i_mCoordX + i_minx);
        setY(i_mCoordY + i_miny);

        //moveMainPointTo(i_minx,i_miny);
        p_MainPoint.i_X = 0 - i_minx;
        p_MainPoint.i_Y = 0 - i_miny;
    }

    public void moveMainPointTo(int _dx, int _dy)
    {
        int i_dx = _dx + p_MainPoint.i_X;
        int i_dy = _dy + p_MainPoint.i_Y;

        for (int li = 0; li < p_PointVector.size(); li++)
        {
            PathPoint p_point = (PathPoint) p_PointVector.elementAt(li);
            p_point.i_X -= _dx;
            p_point.i_Y -= _dy;
        }

        p_MainPoint.i_X = i_dx;
        p_MainPoint.i_Y = i_dy;
    }

    public void resortIndexesOfPoints()
    {
        for (int li = 0; li < p_PointVector.size(); li++)
        {
            ((PathPoint) p_PointVector.elementAt(li)).i_Index = li;
        }
    }

    private PathPoint getPointInCoords(int _x, int _y)
    {
        int i_x = p_MainPoint.i_X;
        int i_y = p_MainPoint.i_Y;

        i_x = i_X + i_x - CIR_RAD;
        i_y = i_Y + i_y - CIR_RAD;
        int i_x2 = i_x + (CIR_RAD << 1);
        int i_y2 = i_y + (CIR_RAD << 1);

        if (_x >= i_x && _x < i_x2)
            if (_y >= i_y && _y < i_y2)
                return p_MainPoint;

        for (int li = 0; li < p_PointVector.size(); li++)
        {
            PathPoint p_point = (PathPoint) p_PointVector.elementAt(li);
            i_x = p_point.i_X + p_MainPoint.i_X;
            i_y = p_point.i_Y + p_MainPoint.i_Y;

            i_x = i_X + i_x - CIR_RAD;
            i_y = i_Y + i_y - CIR_RAD;
            i_x2 = i_x + (CIR_RAD << 1);
            i_y2 = i_y + (CIR_RAD << 1);

            if (_x >= i_x && _x < i_x2)
                if (_y >= i_y && _y < i_y2)
                    return p_point;
        }

        return null;
    }

    public boolean isFocusedSubComponent(int _x, int _y)
    {
        return getPointInCoords(_x, _y) != null ? true : false;
    }

    private PathPoint p_draggedPoint = null;
    private int i_dragCoordX = 0;
    private int i_dragCoordY = 0;

    public void takeSubcomponentForDrag(int _x, int _y)
    {
        p_draggedPoint = getPointInCoords(_x, _y);

        if (p_draggedPoint.equals(p_MainPoint))
        {
            i_xSubPointOffset = i_X + p_MainPoint.i_X;
            i_ySubPointOffset = i_Y + p_MainPoint.i_Y;
        }
        else
        {
            i_xSubPointOffset = p_draggedPoint.i_X + i_X + p_MainPoint.i_X;
            i_ySubPointOffset = p_draggedPoint.i_Y + i_Y + p_MainPoint.i_Y;
        }

        i_xSubPointOffset -= _x;
        i_ySubPointOffset -= _y;
    }

    public void dragSubcomponent(int _x, int _y)
    {
        if (p_draggedPoint != null)
        {
            i_dragCoordX = _x + i_xSubPointOffset;
            i_dragCoordY = _y + i_ySubPointOffset;
        }
    }

    public void holdSubcomponent(int _x, int _y)
    {
        if (p_draggedPoint != null)
        {
            if (p_draggedPoint.equals(p_MainPoint))
            {
                int i_x = _x - i_X - p_MainPoint.i_X + i_xSubPointOffset;
                int i_y = _y - i_Y - p_MainPoint.i_Y + i_ySubPointOffset;

                moveMainPointTo(i_x, i_y);
            }
            else
            {
                p_draggedPoint.i_X = _x - (p_MainPoint.i_X + i_X) + i_xSubPointOffset;
                p_draggedPoint.i_Y = _y - (p_MainPoint.i_Y + i_Y) + i_ySubPointOffset;
            }
            updateCoordsForPoints();
        }

        p_draggedPoint = null;
    }

    private static Font p_Font = new Font("Arial", Font.PLAIN, 9);

    public void paintContent(Graphics _g, boolean _focused)
    {
        int i_x = 0;
        int i_y = 0;

        _g.setColor(p_pathViewColor);

        int i_sX = p_MainPoint.i_X + i_X;
        int i_sY = p_MainPoint.i_Y + i_Y;

        int i_firstX = 0;
        int i_firstY = 0;

        int i_steps = 0;

        _g.setFont(p_Font);

        for (int li = 0; li < p_PointVector.size(); li++)
        {
            PathPoint p_Point = (PathPoint) p_PointVector.elementAt(li);

            String s_str = Integer.toString(li);

            int i_x2 = i_sX + p_Point.i_X;
            int i_y2 = i_sY + p_Point.i_Y;

            if (li == 0)
            {
                if (_focused)
                {
                    if (p_Point.equals(p_draggedPoint) && _focused)
                    {
                        i_x2 = i_dragCoordX;
                        i_y2 = i_dragCoordY;
                    }

                    _g.setColor(Color.green);

                    _g.fill3DRect(i_x2 - CIR_RAD, i_y2 - CIR_RAD, CIR_RAD << 1, CIR_RAD << 1, true);
                    _g.drawRect(i_x2 - CIR_RAD - 2, i_y2 - CIR_RAD - 2, (CIR_RAD << 1) + 3, (CIR_RAD << 1) + 3);

                    _g.setColor(p_pathViewColor);

                }
                i_firstX = i_x2;
                i_firstY = i_y2;

                i_steps = p_Point.i_Steps;
            }
            else if (li == p_PointVector.size() - 1)
            {
                if (p_Point.equals(p_draggedPoint))
                {
                    i_x2 = i_dragCoordX;
                    i_y2 = i_dragCoordY;
                }

                drawLine(_g, i_x, i_y, i_x2, i_y2, i_steps);
                if (_focused)
                {
                    _g.fill3DRect(i_x2 - CIR_RAD, i_y2 - CIR_RAD, CIR_RAD << 1, CIR_RAD << 1, true);
                }

                i_steps = p_Point.i_Steps;
            }
            else
            {
                if (p_Point.equals(p_draggedPoint))
                {
                    i_x2 = i_dragCoordX;
                    i_y2 = i_dragCoordY;
                }

                drawLine(_g, i_x, i_y, i_x2, i_y2, i_steps);
                if (_focused)
                {
                    _g.fill3DRect(i_x2 - CIR_RAD, i_y2 - CIR_RAD, CIR_RAD << 1, CIR_RAD << 1, true);
                }
                i_steps = p_Point.i_Steps;
            }

            if (p_draggedPoint != null && _focused)
            {
                if (p_draggedPoint.equals(p_Point))
                {
                    _g.setColor(Color.ORANGE);
                    _g.drawRect(i_x2 - (CIR_RAD << 1), i_y2 - (CIR_RAD << 1), (CIR_RAD << 2) - 1, (CIR_RAD << 2) - 1);
                    _g.setColor(p_pathViewColor);
                }
            }

            if (_focused) _g.drawString(s_str, i_x2 + (CIR_RAD << 1), i_y2);

            i_x = i_x2;
            i_y = i_y2;
        }

        if (p_PointVector.size() > 1 && i_PathType == PATH_CYCLIC)
        {
            drawLine(_g, i_x, i_y, i_firstX, i_firstY, i_steps);
        }

        if (_focused)
        {
            i_x = i_X + p_MainPoint.i_X;
            i_y = i_Y + p_MainPoint.i_Y;

            if (p_MainPoint.equals(p_draggedPoint))
            {
                i_x = i_dragCoordX;
                i_y = i_dragCoordY;
            }

            // Рисуем точку отсчета
            _g.setColor(Color.red);
            if (i_x >= i_X && i_x < i_X + i_Width) _g.drawLine(i_x, i_Y, i_x, i_Y + i_Height - 1);
            if (i_y >= i_Y && i_y < i_Y + i_Height) _g.drawLine(i_X, i_y, i_X + i_Width - 1, i_y);

            _g.drawRect(i_x - CIR_RAD, i_y - CIR_RAD, CIR_RAD << 1, CIR_RAD << 1);

            if (p_draggedPoint != null && _focused)
            {
                if (p_draggedPoint.equals(p_MainPoint))
                {
                    _g.setColor(Color.ORANGE);
                    _g.drawRect(i_x - (CIR_RAD << 1), i_y - (CIR_RAD << 1), (CIR_RAD << 2), (CIR_RAD << 2));
                }
            }

            if (_focused)
            {
                _g.setColor(Color.red);
                _g.drawString(s_ID, i_x + (CIR_RAD << 1), i_y);
            }
        }
    }

    private static final MenuItem p_MenuItem_AddPoint = new MenuItem("Add point");
    private static final MenuItem p_MenuItem_RemovePoint = new MenuItem("Remove point");
    private static final MenuItem p_MenuItem_MainPointToCenterX = new MenuItem("Main point to center X");
    private static final MenuItem p_MenuItem_MainPointToCenterY = new MenuItem("Main point to center Y");
    private static final MenuItem p_MenuItem_FurlX = new MenuItem("Coordinate X to 0");
    private static final MenuItem p_MenuItem_FurlY = new MenuItem("Coordinate Y to 0");
    private static final MenuItem p_MenuItem_UnrollX = new MenuItem("Unroll X coords");
    private static final MenuItem p_MenuItem_UnrollY = new MenuItem("Unroll Y coords");
    private static final MenuItem p_MenuItem_FlipV = new MenuItem("Flip Vert.");
    private static final MenuItem p_MenuItem_FlipH = new MenuItem("Flip Horz.");
    private static final MenuItem p_MenuItem_EXCHXY = new MenuItem("Exchange XY");

    private static final String ACT_ADDPOINT = "AP";
    private static final String ACT_REMOVEPOINT = "RP";
    private static final String ACT_MAINPOINTXTOCENTER = "MPXC";
    private static final String ACT_MAINPOINTYTOCENTER = "MPYC";
    private static final String ACT_FLIPV = "FV";
    private static final String ACT_FLIPH = "FH";
    private static final String ACT_FURLX = "FRLX";
    private static final String ACT_FURLY = "FRLY";
    private static final String ACT_UNROLLX = "UNRLX";
    private static final String ACT_UNROLLY = "UNRLY";
    private static final String ACT_EXCHXY = "EXCHXY";

    static
    {
        p_MenuItem_AddPoint.setActionCommand(ACT_ADDPOINT);
        p_MenuItem_RemovePoint.setActionCommand(ACT_REMOVEPOINT);
        p_MenuItem_MainPointToCenterX.setActionCommand(ACT_MAINPOINTXTOCENTER);
        p_MenuItem_MainPointToCenterY.setActionCommand(ACT_MAINPOINTYTOCENTER);
        p_MenuItem_FlipH.setActionCommand(ACT_FLIPH);
        p_MenuItem_FlipV.setActionCommand(ACT_FLIPV);

        p_MenuItem_FurlX.setActionCommand(ACT_FURLX);
        p_MenuItem_FurlY.setActionCommand(ACT_FURLY);
        p_MenuItem_UnrollX.setActionCommand(ACT_UNROLLX);
        p_MenuItem_UnrollY.setActionCommand(ACT_UNROLLY);

        p_MenuItem_EXCHXY.setActionCommand(ACT_EXCHXY);
    }

    private int i_popupX = 0;
    private int i_popupY = 0;

    int i_xSubPointOffset = 0;
    int i_ySubPointOffset = 0;

    private void _flipHorz()
    {
        for (int li = 0; li < p_PointVector.size(); li++)
        {
            PathPoint p_point = (PathPoint) p_PointVector.elementAt(li);
            p_point.i_X = 0 - p_point.i_X;
        }
    }

    private void _flipVert()
    {
        for (int li = 0; li < p_PointVector.size(); li++)
        {
            PathPoint p_point = (PathPoint) p_PointVector.elementAt(li);
            p_point.i_Y = 0 - p_point.i_Y;
        }
    }

    private void _furlX()
    {
        for (int li = 0; li < p_PointVector.size(); li++)
        {
            PathPoint p_point = (PathPoint) p_PointVector.elementAt(li);
            p_point.i_X = 0;
        }
    }

    private void _exchangeXY()
    {
        for (int li = 0; li < p_PointVector.size(); li++)
        {
            PathPoint p_point = (PathPoint) p_PointVector.elementAt(li);
            int i_x = p_point.i_X;
            p_point.i_X = p_point.i_Y;
            p_point.i_Y = i_x;
        }
    }

    private static final int UNROL_STEP = 10;

    private void _unrollX()
    {
        int i_coordX = 0;

        for (int li = 0; li < p_PointVector.size(); li++)
        {
            PathPoint p_point = (PathPoint) p_PointVector.elementAt(li);
            p_point.i_X = i_coordX;
            i_coordX += UNROL_STEP;
        }
    }

    private void _unrollY()
    {
        int i_coordY = 0;

        for (int li = 0; li < p_PointVector.size(); li++)
        {
            PathPoint p_point = (PathPoint) p_PointVector.elementAt(li);
            p_point.i_Y = i_coordY;
            i_coordY += UNROL_STEP;
        }
    }

    private void _furlY()
    {
        for (int li = 0; li < p_PointVector.size(); li++)
        {
            PathPoint p_point = (PathPoint) p_PointVector.elementAt(li);
            p_point.i_Y = 0;
        }
    }

    private void _MainPointToCenterY()
    {
        int i_minY = 0;
        int i_maxY = 0;
        for (int li = 0; li < p_PointVector.size(); li++)
        {
            PathPoint p_point = (PathPoint) p_PointVector.elementAt(li);
            int i_y = p_point.i_Y;
            if (i_y > i_maxY) i_maxY = i_y;
            if (i_y < i_minY) i_minY = i_y;
        }

        int i_yoff = (Math.abs(i_maxY - i_minY) >> 1) + i_minY;
        moveMainPointTo(0, i_yoff);
    }

    private void _MainPointToCenterX()
    {
        int i_minX = 0;
        int i_maxX = 0;
        for (int li = 0; li < p_PointVector.size(); li++)
        {
            PathPoint p_point = (PathPoint) p_PointVector.elementAt(li);
            int i_x = p_point.i_X;
            if (i_x > i_maxX) i_maxX = i_x;
            if (i_x < i_minX) i_minX = i_x;
        }

        int i_xoff = (Math.abs(i_maxX - i_minX) >> 1) + i_minX;
        moveMainPointTo(i_xoff, 0);
    }

    public boolean processPopupAction(String _action)
    {
        if (_action.equals(ACT_ADDPOINT))
        {
            processDoubleMouseClick(i_popupX, i_popupY, MouseEvent.BUTTON1);
            return true;
        }
        else if (_action.equals(ACT_REMOVEPOINT))
        {
            processDoubleMouseClick(i_popupX, i_popupY, MouseEvent.BUTTON3);
            return true;
        }
        else if (_action.equals(ACT_MAINPOINTXTOCENTER))
        {
            _MainPointToCenterX();
            updateCoordsForPoints();
            return true;
        }
        else if (_action.equals(ACT_MAINPOINTYTOCENTER))
        {
            _MainPointToCenterY();
            updateCoordsForPoints();
            return true;
        }
        else if (_action.equals(ACT_FLIPH))
        {
            _flipHorz();
            updateCoordsForPoints();
            return true;
        }
        else if (_action.equals(ACT_FLIPV))
        {
            _flipVert();
            updateCoordsForPoints();
            return true;
        }
        else if (_action.equals(ACT_FURLX))
        {
            _furlX();
            updateCoordsForPoints();
            return true;
        }
        else if (_action.equals(ACT_FURLY))
        {
            _furlY();
            updateCoordsForPoints();
            return true;
        }
        else if (_action.equals(ACT_UNROLLX))
        {
            _unrollX();
            updateCoordsForPoints();
            return true;
        }
        else if (_action.equals(ACT_UNROLLY))
        {
            _unrollY();
            updateCoordsForPoints();
            return true;
        }
        else if (_action.equals(ACT_EXCHXY))
        {
            _exchangeXY();
            updateCoordsForPoints();
            return true;
        }

        return false;
    }

    public Vector getPopupMenuItems(int _x, int _y)
    {
        i_popupX = _x;
        i_popupY = _y;

        Vector p_vector = new Vector();

        p_vector.add(p_MenuItem_AddPoint);
        p_vector.add(p_MenuItem_RemovePoint);

        if (p_PointVector.size() > 1)
        {
            p_vector.add(null);

            p_vector.add(p_MenuItem_MainPointToCenterX);
            p_vector.add(p_MenuItem_MainPointToCenterY);

            p_vector.add(null);

            p_vector.add(p_MenuItem_EXCHXY);
            p_vector.add(p_MenuItem_FlipH);
            p_vector.add(p_MenuItem_FlipV);
            p_vector.add(p_MenuItem_FurlX);
            p_vector.add(p_MenuItem_FurlY);
            p_vector.add(p_MenuItem_UnrollX);
            p_vector.add(p_MenuItem_UnrollY);
        }

        return p_vector;
    }

    public boolean processDoubleMouseClick(int _x, int _y, int _button)
    {
        switch (_button)
        {
            case MouseEvent.BUTTON1:
                {
                    int i_x = _x - i_X - p_MainPoint.i_X;
                    int i_y = _y - i_Y - p_MainPoint.i_Y;

                    PathPoint p_Point = new PathPoint(i_x, i_y, 10);
                    p_PointVector.add(p_Point);
                    resortIndexesOfPoints();
                    return true;
                }
            case MouseEvent.BUTTON3:
                {
                    PathPoint p_point = this.getPointInCoords(_x, _y);
                    if (p_point != null && !p_MainPoint.equals(p_point))
                    {
                        p_PointVector.remove(p_point);
                        resortIndexesOfPoints();
                        return true;
                    }
                }
        }
        return false;
    }

    public boolean doesUseResource(AbstractRrgResource _resource)
    {
        return false;
    }

    public boolean isVisualComponent()
    {
        return true;
    }

    public boolean canBeFocused()
    {
        return true;
    }

    protected static final String XML_POINTS = "Points";
    protected static final String XML_POINT = "point";
    protected static final String XML_COLOR = "color";
    protected static final String XML_PATHTYPE = "PathType";
    protected static final String XML_X = "X";
    protected static final String XML_Y = "Y";
    protected static final String XML_WIDTH = "WIDTH";
    protected static final String XML_HEIGHT = "HEIGHT";
    protected static final String XML_S = "S";
    protected static final String XML_MAINPOINT = "MAINPOINT";
    protected static final String XML_SHOWPOINTS = "SHOWPOINTS";

    private void drawLine(Graphics _g, int _x1, int _y1, int _x2, int _y2, int _steps)
    {
        if (lg_showSteps)
        {
            int i8_targetX = _x2 << 8;
            int i8_targetY = _y2 << 8;
            int i8_curX = _x1 << 8;
            int i8_curY = _y1 << 8;

            int i8_dx = (i8_targetX - i8_curX) / _steps;
            int i8_dy = (i8_targetY - i8_curY) / _steps;

            i8_targetX = i8_curX + i8_dx * _steps;
            i8_targetY = i8_curY + i8_dy * _steps;

            if (Math.abs(i8_dx) < 0x10)
            {
                if (i8_dx > 0)
                    i8_dx = 0x10;
                else
                    i8_dx = -0x10;
            }

            if (Math.abs(i8_dy) < 0x10)
            {
                if (i8_dy > 0)
                    i8_dy = 0x10;
                else
                    i8_dy = -0x10;
            }

            for (int li = 0; li <= _steps; li++)
            {
                int i_x = _x1 + ((i8_dx * li) >> 8);
                int i_y = _y1 + ((i8_dy * li) >> 8);

                if (i_x == _x2 && i_y == _y2) break;

                _g.drawRect(i_x - 1, i_y - 1, 2, 2);
            }
        }
        else
        {
            _g.drawLine(_x1, _y1, _x2, _y2);
        }
    }

    public void _saveAsXML(PrintStream _printStream)
    {
        if (_printStream == null) return;

        String s_str = "<" + XML_COMPONENT_INFO + " " + XML_COLOR + "=\"" + p_pathViewColor.getRGB() + "\" " + XML_PATHTYPE + "=\"" + i_PathType + "\">";
        _printStream.println(s_str);

        _printStream.println("<" + XML_MAINPOINT + " " + XML_X + "=\"" + p_MainPoint.i_X + "\" " + XML_Y + "=\"" + p_MainPoint.i_Y + "\"/>");

        _printStream.println("<" + XML_POINTS + " " + XML_SHOWPOINTS + "=\"" + (lg_showSteps ? "yes" : "no") + "\"" + ">");

        for (int li = 0; li < p_PointVector.size(); li++)
        {
            PathPoint p_point = (PathPoint) p_PointVector.elementAt(li);
            _printStream.println("<" + XML_POINT + " " + XML_X + "=\"" + p_point.i_X + "\" " + XML_Y + "=\"" + p_point.i_Y + "\"" + " " + XML_S + "=\"" + p_point.i_Steps + "\"/>");
        }

        _printStream.println("</" + XML_POINTS + ">");

        s_str = "</" + XML_COMPONENT_INFO + ">";
        _printStream.println(s_str);
    }

    public void _loadFromXML(Element _element, ResourceContainer _container) throws IOException
    {
        String s_color = _element.getAttribute(XML_COLOR);
        String s_pathtype = _element.getAttribute(XML_PATHTYPE);

        try
        {
            p_pathViewColor = new Color(Integer.parseInt(s_color));
            i_PathType = Integer.parseInt(s_pathtype);
        }
        catch (NumberFormatException ex)
        {
            throw new IOException("Component " + s_ID + " format error");
        }

        NodeList p_mainPoint = _element.getElementsByTagName(XML_MAINPOINT);
        if (p_mainPoint.getLength() == 0) throw new IOException("Component " + s_ID + " format error");
        Element p_mainPointEle = (Element) p_mainPoint.item(0);

        String s_x = p_mainPointEle.getAttribute(XML_X);
        String s_y = p_mainPointEle.getAttribute(XML_Y);

        try
        {
            p_MainPoint.i_X = Integer.parseInt(s_x);
            p_MainPoint.i_Y = Integer.parseInt(s_y);
        }
        catch (NumberFormatException e)
        {
            throw new IOException("Component " + s_ID + " format error");
        }

        NodeList p_points = _element.getElementsByTagName(XML_POINTS);
        if (p_points.getLength() == 0) throw new IOException("Component " + s_ID + " format error");
        Element p_pointElements = (Element) p_points.item(0);

        if (p_pointElements.getAttribute(XML_SHOWPOINTS).equals("yes")) lg_showSteps = true; else lg_showSteps = false;

        p_points = p_pointElements.getElementsByTagName(XML_POINT);

        for (int li = 0; li < p_points.getLength(); li++)
        {
            Element p_pEl = (Element) p_points.item(li);
            String s_pX = p_pEl.getAttribute(XML_X);
            String s_pY = p_pEl.getAttribute(XML_Y);
            String s_pS = p_pEl.getAttribute(XML_S);

            int i_x, i_y, i_s;

            try
            {
                i_x = Integer.parseInt(s_pX);
                i_y = Integer.parseInt(s_pY);
                i_s = Integer.parseInt(s_pS);
            }
            catch (NumberFormatException e)
            {
                throw new IOException("Component " + s_ID + " format error");
            }

            p_PointVector.add(new PathPoint(i_x, i_y, i_s));
        }
        resortIndexesOfPoints();
    }


}
