package com.raydac_research.FormEditor.RrgFormComponents;

import com.raydac_research.FormEditor.RrgFormResources.AbstractRrgResource;
import com.raydac_research.FormEditor.RrgFormResources.ResourceContainer;

import java.awt.*;
import java.io.PrintStream;
import java.io.IOException;
import java.util.Vector;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public abstract class AbstractFormComponent
{
    public static final String XML_COMPONENT = "component";
    public static final String XML_TYPE = "type";
    public static final String XML_COMPONENT_ID = "id";
    public static final String XML_COMPONENT_X = "x";
    public static final String XML_COMPONENT_Y = "y";
    public static final String XML_COMPONENT_WIDTH = "width";
    public static final String XML_COMPONENT_HEIGHT = "height";
    public static final String XML_COMPONENT_HIDDEN = "hidden";
    public static final String XML_COMPONENT_PINNED = "pinned";
    public static final String XML_COMPONENT_LOCKED = "locked";
    public static final String XML_COMPONENT_CHANNEL = "channel";
    public static final String XML_COMPONENT_INFO = "component_info";

    public static final int STATE_NORMAL = 0;
    public static final int STATE_SELECTED = 1;
    public static final int STATE_DISABLED = 2;
    public static final int STATE_PRESSED = 3;

    public static final int COMPONENT_IMAGE = 0;
    public static final int COMPONENT_BUTTON = 1;
    public static final int COMPONENT_TEXTAREA = 2;
    public static final int COMPONENT_SCROLLBAR = 3;
    public static final int COMPONENT_FORM = 4;
    public static final int COMPONENT_LABEL = 5;
    public static final int COMPONENT_PATH = 6;
    public static final int COMPONENT_CUSTOMAREA = 7;

    protected String s_ID;
    protected int i_Type;
    protected int i_X;
    protected int i_Y;
    protected int i_Width;
    protected int i_Height;

    protected int i_State;

    protected boolean lg_pinned;
    protected boolean lg_hidden;
    protected boolean lg_locked;

    protected String s_ID_trans;
    protected int i_Type_trans;
    protected int i_X_trans;
    protected int i_Y_trans;
    protected int i_Width_trans;
    protected int i_Height_trans;

    protected int i_State_trans;

    protected boolean lg_pinned_trans;
    protected boolean lg_hidden_trans;
    protected boolean lg_locked_trans;

    protected boolean lg_transacted = false;

    protected int i_channel;

    protected FormContainer p_parent;

    public FormContainer getParent()
    {
        return p_parent;
    }

    public void copyTo(AbstractFormComponent _component)
    {
        _component.s_ID = s_ID;
        _component.i_Type = i_Type;
        _component.i_X = i_X;
        _component.i_Y = i_Y;
        _component.i_Width = i_Width;
        _component.i_Height = i_Height;

        _component.i_State = i_State;

        _component.lg_pinned = lg_pinned;
        _component.lg_hidden = lg_hidden;
        _component.lg_locked = lg_locked;

        _component.i_channel = i_channel;

        _component.p_parent = p_parent;
    }

    public AbstractFormComponent()
    {
        p_parent = null;
        i_channel = 0;
        i_State = STATE_NORMAL;
        s_ID = null;
        i_Type = 0;
        setX(0);
        setY(0);
        setWidthHeight(0, 0);
    }

    public void setParent(FormContainer _parent)
    {
        p_parent = _parent;
    }

    public void startTransaction()
    {
        s_ID_trans = s_ID;
        i_Type_trans = i_Type;
        i_X_trans = i_X;
        i_Y_trans = i_Y;
        i_Width_trans = i_Width;
        i_Height_trans = i_Height;

        i_State_trans = i_State;

        lg_pinned_trans = lg_pinned;
        lg_locked_trans = lg_locked;
        lg_hidden_trans = lg_hidden;

        lg_transacted = true;
    }

    public int getChannel()
    {
        return i_channel;
    }

    public void setChannel(int _channel)
    {
        i_channel = _channel;
    }

    public int getMinimumWidth()
    {
        return 2;
    }

    public int getMinimumHeight()
    {
        return 2;
    }

    public void rollbackTransaction(boolean _anyway)
    {
        if (!lg_transacted && !_anyway) return;

        lg_transacted = false;

        String s_ID_p = s_ID;
        s_ID = s_ID_trans;
        s_ID_trans = s_ID_p;

        int i_Type_p = i_Type;
        i_Type = i_Type_trans;
        i_Type_trans = i_Type_p;

        int i_X_p = i_X;
        i_X = i_X_trans;
        i_X_trans = i_X_p;

        int i_Y_p = i_Y;
        i_Y = i_Y_trans;
        i_Y_trans = i_Y_p;

        int i_Width_p = i_Width;
        i_Width = i_Width_trans;
        i_Width_trans = i_Width_p;

        int i_Height_p = i_Height;
        i_Height = i_Height_trans;
        i_Height_trans = i_Height_p;

        int i_State_p = i_State;
        i_State = i_State_trans;
        i_State_trans = i_State_p;

        boolean lg_pinned_p = lg_pinned;
        lg_pinned = lg_pinned_trans;
        lg_pinned_trans = lg_pinned_p;

        boolean lg_hidden_p = lg_hidden;
        lg_hidden = lg_hidden_trans;
        lg_hidden_trans = lg_hidden_p;

        boolean lg_locked_p = lg_locked;
        lg_locked = lg_locked_trans;
        lg_locked_trans = lg_locked_p;
    }

    public boolean isFocusedSubComponent(int _x, int _y)
    {
        return false;
    }

    public void takeSubcomponentForDrag(int _x, int _y)
    {
    }

    public void dragSubcomponent(int _x, int _y)
    {
    }

    public void holdSubcomponent(int _x, int _y)
    {

    }

    public boolean isPinned()
    {
        return lg_pinned;
    }

    public boolean isLocked()
    {
        return lg_locked;
    }

    public abstract void resourceUpdated();

    public void setPinned(boolean _flag)
    {
        lg_pinned = _flag;
    }

    public void setLocked(boolean _flag)
    {
        lg_locked = _flag;
    }

    public boolean isHidden()
    {
        return lg_hidden;
    }

    public void setHidden(boolean _flag)
    {
        lg_hidden = _flag;
    }

    public AbstractFormComponent(FormContainer _parent, String _id, int _type, int _width, int _height)
    {
        p_parent = _parent;
        i_channel = 0;
        i_State = STATE_NORMAL;
        s_ID = _id;
        i_Type = _type;
        setX(0);
        setY(0);
        setWidthHeight(_width, _height);
    }

    public String getID()
    {
        return s_ID;
    }

    public void setID(String _id)
    {
        s_ID = _id;
    }

    public int getState()
    {
        return i_State;
    }

    public void setState(int _state)
    {
        i_State = _state;
    }

    public int getType()
    {
        return i_Type;
    }

    public int getX()
    {
        return i_X;
    }

    public int getY()
    {
        return i_Y;
    }

    public void setX(int _x)
    {
        i_X = _x;
    }

    public void setY(int _y)
    {
        i_Y = _y;
    }

    public int getWidth()
    {
        return i_Width;
    }

    public int getHeight()
    {
        return i_Height;
    }

    public abstract boolean doesUseResource(AbstractRrgResource _resource);

    public void processMouseClick(int _button)
    {

    }

    public boolean processDoubleMouseClick(int _x, int _y, int _button)
    {
        return false;
    }

    public void setWidthHeight(int _width, int _height)
    {
        i_Width = _width;
        i_Height = _height;
    }

    public void setBounds(int _x, int _y, int _width, int _height)
    {
        setX(_x);
        setY(_y);
        setWidthHeight(_width, _height);
    }

    public String toString()
    {
        return s_ID + " [" + i_channel + "]";
    }

    public abstract boolean isVisualComponent();

    public abstract boolean canBeFocused();

    public boolean processPopupAction(String _action)
    {
        return false;
    }

    public Vector getPopupMenuItems(int _x, int _y)
    {
        return null;
    }

    public boolean containsPoint(int _x, int _y)
    {
        if (_x < i_X || _y < i_Y) return false;
        if (_x >= (i_X + i_Width) || _y >= (i_Y + i_Height)) return false;
        return true;
    }

    public boolean resizable()
    {
        return false;
    }

    public void paintContent(Graphics _g, boolean _focused)
    {
        if (_focused)
        {
            // _g.fill3DRect(i_X,i_Y,i_Width,i_Height,true);
        }
        else
        {
            p_parent.getSelectedTextColor();
            _g.fillRect(i_X, i_Y, i_Width, i_Height);
        }
    }

    public void drawComponentContent(Graphics _g)
    {
        int i_x = i_X;
        int i_y = i_Y;
        i_X = 0;
        i_Y = 0;
        paintContent(_g, false);
        i_X = i_x;
        i_Y = i_y;
    }

    public final void saveAsXML(PrintStream _printStream)
    {
        if (_printStream == null) return;

        String s_str = "<" + XML_COMPONENT + " " + XML_TYPE + "=\"" + getType() + "\" " + XML_COMPONENT_ID + "=\"" + getID() + "\" " + XML_COMPONENT_X + "=\"" + getX() + "\" " + XML_COMPONENT_Y + "=\"" + getY() + "\" " + XML_COMPONENT_WIDTH + "=\"" + getWidth() + "\" " + XML_COMPONENT_HEIGHT + "=\"" + getHeight() + "\" " + (lg_hidden ? XML_COMPONENT_HIDDEN + "=\"yes\"" : "") + " " + (lg_pinned ? XML_COMPONENT_PINNED + "=\"yes\"" : "") + " " + (lg_locked ? XML_COMPONENT_LOCKED + "=\"yes\"" : "") + " " + XML_COMPONENT_CHANNEL + "=\"" + i_channel + "\">";
        _printStream.println(s_str);

        _saveAsXML(_printStream);

        _printStream.println("</" + XML_COMPONENT + ">");
    }

    protected Element _getElement(Element _element, String _name)
    {
        NodeList p_list = _element.getElementsByTagName(_name);
        if (p_list.getLength() == 0) return null;
        return (Element) p_list.item(0);
    }

    public final void loadFromXML(Element _element, ResourceContainer _container) throws IOException
    {
        String s_id = _element.getAttribute(XML_COMPONENT_ID).trim();

        String s_x = _element.getAttribute(XML_COMPONENT_X).trim();
        String s_y = _element.getAttribute(XML_COMPONENT_Y).trim();
        String s_width = _element.getAttribute(XML_COMPONENT_WIDTH).trim();
        String s_height = _element.getAttribute(XML_COMPONENT_HEIGHT).trim();

        String s_channel = _element.getAttribute(XML_COMPONENT_CHANNEL).trim();
        String s_pinned = _element.getAttribute(XML_COMPONENT_PINNED).trim();
        String s_hidden = _element.getAttribute(XML_COMPONENT_HIDDEN).trim();
        String s_locked = _element.getAttribute(XML_COMPONENT_LOCKED).trim();

        s_ID = s_id;

        Element p_componentInfo = _getElement(_element, XML_COMPONENT_INFO);
        if (p_componentInfo == null) throw new IOException("I can't find the info tag for " + s_id + " component");

        try
        {
            i_channel = Integer.parseInt(s_channel);
            i_X = Integer.parseInt(s_x);
            i_Y = Integer.parseInt(s_y);
            i_Width = Integer.parseInt(s_width);
            i_Height = Integer.parseInt(s_height);
        }
        catch (NumberFormatException e)
        {
            throw new IOException("Error attribute in " + s_id + " component");
        }

        lg_pinned = s_pinned.length() > 0 ? true : false;
        lg_hidden = s_hidden.length() > 0 ? true : false;
        lg_locked = s_locked.length() > 0 ? true : false;

        _loadFromXML(p_componentInfo, _container);
    }

    public abstract void _saveAsXML(PrintStream _printStream);

    public abstract void _loadFromXML(Element _element, ResourceContainer _container) throws IOException;


    public static AbstractFormComponent cloneComponent(AbstractFormComponent _component)
    {
        AbstractFormComponent p_newComponent = null;

        switch (_component.i_Type)
        {
            case COMPONENT_BUTTON:
                {
                    p_newComponent = new RrgFormComponent_Button();
                }
                ;
                break;
            case COMPONENT_LABEL:
                {
                    p_newComponent = new RrgFormComponent_Label();
                }
                ;
                break;
            case COMPONENT_CUSTOMAREA:
                {
                    p_newComponent = new RrgFormComponent_CustomArea();
                }
                ;
                break;
            case COMPONENT_IMAGE:
                {
                    p_newComponent = new RrgFormComponent_Image();
                }
                ;
                break;
            case COMPONENT_PATH:
                {
                    p_newComponent = new RrgFormComponent_Path();
                }
                ;
                break;
            case COMPONENT_FORM:
                {
                    p_newComponent = new FormContainer();
                }
                ;
                break;
        }

        _component.copyTo(p_newComponent);

        return p_newComponent;
    }
}

