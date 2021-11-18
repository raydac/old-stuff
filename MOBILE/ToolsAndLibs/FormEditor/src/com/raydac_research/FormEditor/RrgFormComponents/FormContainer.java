package com.raydac_research.FormEditor.RrgFormComponents;

import com.raydac_research.FormEditor.RrgFormResources.AbstractRrgResource;
import com.raydac_research.FormEditor.RrgFormResources.ResourceContainer;

import javax.swing.tree.TreePath;
import javax.swing.event.TreeModelListener;
import javax.swing.event.TreeModelEvent;
import java.util.Vector;
import java.io.PrintStream;
import java.io.IOException;
import java.awt.*;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class FormContainer extends AbstractFormComponent
{
    public static final int MAX_COMPONENT_NUMBER = 1024;

    protected AbstractFormComponent[] ap_storage;
    protected int i_size;

    protected TreeModelListener p_listener;

    protected Color p_backgroundColor;
    protected Color p_SelectedTextColor;
    protected Color p_DisabledTextColor;
    protected Color p_NormalTextColor;
    protected Color p_PressedTextColor;

    public static final String XML_COMPONENT_BACKGROUND_COLOR = "background_color";
    public static final String XML_COMPONENT_NORMAL_COLOR = "normal_color";
    public static final String XML_COMPONENT_SELECTED_COLOR = "selected_color";
    public static final String XML_COMPONENT_DISABLED_COLOR = "disabled_color";
    public static final String XML_COMPONENT_PRESSED_COLOR = "pressed_color";

    public static final String XML_RULERS = "rulers";

    protected Vector p_Rulers;

    public void removeAllRulers()
    {
        p_Rulers.removeAllElements();
    }

    public void addRuler(FormRuler _ruler)
    {
        // Проверяем на существование линейки с такими координатами и тогда не добавляем
        int i_coord = _ruler.getCoord();
        int i_type = _ruler.getType();
        for(int li=0;li<getRulersNumber();li++)
        {
            FormRuler p_ruler = getRulerForIndex(li);
            if (p_ruler.getCoord() == i_coord)
            {
                if (p_ruler.getType() == i_type) return;
            }
        }

        p_Rulers.add(_ruler);
    }

    public void removeRuler(FormRuler _ruler)
    {
        if (_ruler==null) return;
        p_Rulers.remove(_ruler);
    }

    public int getRulersNumber()
    {
        return p_Rulers.size();
    }

    public FormRuler getRulerForIndex(int _index)
    {
        if (_index<0 || _index>=p_Rulers.size()) return null;
        return (FormRuler) p_Rulers.elementAt(_index);
    }

    public Color getBackgroundColor()
    {
        return p_backgroundColor;
    }

    public void copyTo(AbstractFormComponent _component)
    {
        synchronized (_component)
        {
            super.copyTo(_component);
            FormContainer p_comp = (FormContainer) _component;

            p_comp.p_backgroundColor = p_backgroundColor;
            p_comp.p_NormalTextColor = p_NormalTextColor;
            p_comp.p_DisabledTextColor = p_DisabledTextColor;
            p_comp.p_PressedTextColor = p_PressedTextColor;

            p_comp.p_listener = null;

            for(int li=0;li<MAX_COMPONENT_NUMBER;li++)
            {
                if (ap_storage[li]!=null)
                {
                    p_comp.ap_storage[li] = AbstractFormComponent.cloneComponent(p_comp.ap_storage[li]);
                    p_comp.ap_storage[li].setParent(p_comp);
                }
                else
                    p_comp.ap_storage[li] = null;
            }

            p_comp.removeAllRulers();
            for(int li=0;li<getRulersNumber();li++)
            {
                FormRuler p_ruler = getRulerForIndex(li);
                p_comp.addRuler(p_ruler.getCopy());
            }
        }
    }

    public void setBackgroundColor(Color _value)
    {
        p_backgroundColor = _value;
    }

    public void setNormalTextColor(Color _value)
    {
        p_NormalTextColor = _value;
    }

    public Color getNormalTextColor()
    {
        return p_NormalTextColor;
    }

    public void setDisabledTextColor(Color _value)
    {
        p_DisabledTextColor = _value;
    }

    public Color getDisabledTextColor()
    {
        return p_DisabledTextColor;
    }

    public void setPressedTextColor(Color _value)
    {
        p_PressedTextColor = _value;
    }

    public Color getPressedTextColor()
    {
        return p_PressedTextColor;
    }

    public void setSelectedTextColor(Color _value)
    {
        p_SelectedTextColor = _value;
    }

    public Color getSelectedTextColor()
    {
        return p_SelectedTextColor;
    }

    public AbstractFormComponent getComponentAt(int _index)
    {
        if (_index < 0 || _index >= i_size) return null;
        return ap_storage[_index];
    }

    public int getNextFocusableComponent(int _indexCurrent)
    {
        if (_indexCurrent < 0 || _indexCurrent >= (i_size - 1)) return -1;
        for (int li = _indexCurrent + 1; li < i_size; li++)
        {
            if (ap_storage[li].canBeFocused()) return li;
        }
        return -1;
    }

    public int getPreviousFocusableComponent(int _indexCurrent)
    {
        if (_indexCurrent <= 0 || _indexCurrent >= i_size) return -1;
        for (int li = _indexCurrent - 1; li >= 0; li--)
        {
            if (ap_storage[li].canBeFocused()) return li;
        }
        return -1;
    }

    public int getFirstFocusableComponent()
    {
        if (i_size == 0) return -1;
        if (ap_storage[0].canBeFocused()) return 0;
        return getNextFocusableComponent(0);
    }

    public int getLastFocusableComponent()
    {
        if (i_size == 0) return -1;
        if (ap_storage[i_size - 1].canBeFocused()) return i_size - 1;
        return getPreviousFocusableComponent(i_size - 1);
    }

    public void copyFrom(FormContainer _container)
    {
        for (int li = 0; li < MAX_COMPONENT_NUMBER; li++)
        {
            if (_container.ap_storage[li]!=null)
            {
                ap_storage[li] = AbstractFormComponent.cloneComponent(_container.ap_storage[li]);
                ap_storage[li].setParent(this);
            }
            else
            {
                ap_storage[li] = null;
            }
        }
        i_size = _container.i_size;
        if (p_listener != null) p_listener.treeStructureChanged(new TreeModelEvent(this, new TreePath(new Object[]{this})));

        setID(_container.getID());
        setBounds(_container.i_X, _container.i_Y, _container.i_Width, _container.i_Height);
        p_backgroundColor = _container.getBackgroundColor();
        p_NormalTextColor = _container.getNormalTextColor();
        p_SelectedTextColor = _container.getSelectedTextColor();
        p_PressedTextColor = _container.getPressedTextColor();
        p_DisabledTextColor = _container.getDisabledTextColor();

        removeAllRulers();
        for(int li=0;li<_container.getRulersNumber();li++)
        {
            addRuler(_container.getRulerForIndex(li).getCopy());
        }
    }

    public void isUpdated()
    {
        if (p_listener != null) p_listener.treeStructureChanged(new TreeModelEvent(this, new TreePath(new Object[]{this})));
    }

    public Vector getListOfComponentsUseResource(AbstractRrgResource _resource)
    {
        Vector p_newvector = new Vector();
        for (int li = 0; li < i_size; li++)
        {
            if (ap_storage[li].doesUseResource(_resource)) p_newvector.add(ap_storage[li]);
        }
        return p_newvector;
    }

    public TreePath getTreePath(AbstractFormComponent _component)
    {
        return new TreePath(new Object[]{this, _component});
    }

    public int getSize()
    {
        return i_size;
    }

    public AbstractFormComponent getComponentAt(int _x, int _y)
    {
        for (int li = i_size - 1; li >= 0; li--)
        {
            if (ap_storage[li].isHidden()) continue;
            if (ap_storage[li].containsPoint(_x, _y)) return ap_storage[li];
        }
        return null;
    }

    public void changeStateForComponents(int _newState)
    {
        for (int li = 0; li < i_size; li++)
        {
            ap_storage[li].setState(_newState);
        }
    }

    public AbstractFormComponent[] getComponentArray()
    {
        return ap_storage;
    }

    public void addComponent(AbstractFormComponent _component)
    {
        if (_component == null) return;
        _component.setParent(this);
        ap_storage[i_size] = _component;
        i_size++;
        if (p_listener != null)
            p_listener.treeStructureChanged(new TreeModelEvent(this, new TreePath(new Object[]{this})));
    }

    public FormContainer()
    {
        super();
        p_Rulers = new Vector();
        ap_storage = new AbstractFormComponent[MAX_COMPONENT_NUMBER];
        i_size = 0;

        p_backgroundColor = Color.black;
        p_NormalTextColor = Color.green;
        p_DisabledTextColor = Color.gray;
        p_SelectedTextColor = Color.red;
        p_PressedTextColor = Color.blue;
    }

    public FormContainer(String _id, int _width, int _height)
    {
        super(null, _id, COMPONENT_FORM, _width, _height);

        p_Rulers = new Vector();

        p_backgroundColor = Color.black;
        p_NormalTextColor = Color.green;
        p_DisabledTextColor = Color.gray;
        p_SelectedTextColor = Color.red;
        p_PressedTextColor = Color.blue;

        p_listener = null;
        ap_storage = new AbstractFormComponent[MAX_COMPONENT_NUMBER];
        i_size = 0;
    }

    public void fillComponentPropertiesFromForm()
    {
        for (int li = 0; li < getSize(); li++)
        {
            AbstractFormComponent p_component = getComponentAt(li);
            p_component.resourceUpdated();
        }
    }

    public int getIndexForComponent(AbstractFormComponent _component)
    {
        for (int li = 0; li < i_size; li++)
        {
            if (ap_storage[li].equals(_component)) return li;
        }
        return -1;
    }

    public void removeAll()
    {
        for (int li = 0; li < i_size; li++) ap_storage[li] = null;
        i_size = 0;
        if (p_listener != null) p_listener.treeStructureChanged(new TreeModelEvent(this, new TreePath(new Object[]{this})));
    }

    public void removeComponentForIndex(int _index)
    {
        if (_index < 0 || _index >= i_size) return;

        upList(_index);

        if (p_listener != null) p_listener.treeStructureChanged(new TreeModelEvent(this, new TreePath(new Object[]{this})));

    }

    public void insertComponent(int _index, AbstractFormComponent _component)
    {
        downList(_index);
        ap_storage[_index] = _component;

        if (p_listener != null) p_listener.treeStructureChanged(new TreeModelEvent(this, new TreePath(new Object[]{this})));

    }

    public void moveComponentUp(int _index)
    {
        if (_index <= 0 || _index >= i_size) return;
        AbstractFormComponent p_compo = ap_storage[_index - 1];
        ap_storage[_index - 1] = ap_storage[_index];
        ap_storage[_index] = p_compo;

        if (p_listener != null) p_listener.treeStructureChanged(new TreeModelEvent(this, new TreePath(new Object[]{this})));

    }

    public void moveComponentDown(int _index)
    {
        if (_index < 0 || _index >= (i_size - 1)) return;
        AbstractFormComponent p_compo = ap_storage[_index + 1];
        ap_storage[_index + 1] = ap_storage[_index];
        ap_storage[_index] = p_compo;

        if (p_listener != null) p_listener.treeStructureChanged(new TreeModelEvent(this, new TreePath(new Object[]{this})));

    }

    public boolean containsID(String _id)
    {
        for (int li = 0; li < i_size; li++)
        {
            if (ap_storage[li].getID().equals(_id)) return true;
        }
        return false;
    }

    protected void upList(int _startIndex)
    {
        for (int li = _startIndex; li < i_size; li++)
        {
            ap_storage[li] = ap_storage[li + 1];
        }
        i_size--;
    }

    protected void downList(int _startIndex)
    {
        for (int li = i_size; li >= _startIndex; li--)
        {
            ap_storage[li] = ap_storage[li - 1];
        }
        i_size++;
    }

    public void addTreeModelListener(TreeModelListener lst)
    {
        p_listener = lst;
    }

    public Object getChild(Object parent, int index)
    {
        if (this.equals(parent))
        {
            return ap_storage[index];
        }
        return null;
    }

    public int getChildCount(Object parent)
    {
        if (this.equals(parent))
        {
            return i_size;
        }
        return 0;
    }

    public int getIndexOfChild(Object parent, Object child)
    {
        if (parent == null || child == null) return -1;
        if (this.equals(parent))
        {
            return getIndexForComponent((AbstractFormComponent) child);
        }
        else
            return -1;
    }

    public Object getRoot()
    {
        return this;
    }

    public boolean isLeaf(Object node)
    {
        if (this.equals(node)) return false;
        return true;
    }

    public void removeTreeModelListener(TreeModelListener l)
    {
        p_listener = null;
    }

    public void valueForPathChanged(TreePath path, Object newValue)
    {

    }

    public void resourceUpdated()
    {
        for (int li = 0; li < getSize(); li++)
        {
            AbstractFormComponent p_component = getComponentAt(li);
            p_component.resourceUpdated();
        }
    }

    public boolean doesUseResource(AbstractRrgResource _resource)
    {
        return false;
    }

    public void processMouseClick()
    {
    }

    public boolean isVisualComponent()
    {
        return false;
    }

    public boolean canBeFocused()
    {
        return false;
    }

    public static final String XML_FORM_TAG = "form";

    public void _saveAsXML(PrintStream _printStream)
    {
        if (_printStream == null) return;

        synchronized (ap_storage)
        {
            String s_str = "<" + XML_FORM_TAG + " " + XML_COMPONENT_ID + "=\"" + getID() + "\" " + XML_COMPONENT_WIDTH + "=\"" + getWidth() + "\" " + XML_COMPONENT_HEIGHT + "=\"" + getHeight() + "\" " + XML_COMPONENT_BACKGROUND_COLOR + "=\"" + p_backgroundColor.getRGB() + "\" " + XML_COMPONENT_DISABLED_COLOR + "=\"" + p_DisabledTextColor.getRGB() + "\" " + XML_COMPONENT_SELECTED_COLOR + "=\"" + p_SelectedTextColor.getRGB() + "\" " + XML_COMPONENT_NORMAL_COLOR + "=\"" + p_NormalTextColor.getRGB() + "\" " + XML_COMPONENT_PRESSED_COLOR + "=\"" + p_PressedTextColor.getRGB() + "\" " + XML_COMPONENT_CHANNEL + "=\"" + i_channel + "\">";
            _printStream.println(s_str);

            // Записываем линейки
            _printStream.println("<"+XML_RULERS+">");
            for(int li=0;li<getRulersNumber();li++)
            {
                getRulerForIndex(li).saveAsXML(_printStream);
            }
            _printStream.println("</"+XML_RULERS+">");

            // Записываем компоненты
            for (int li = 0; li < getSize(); li++)
            {
                AbstractFormComponent p_component = getComponentAt(li);
                p_component.saveAsXML(_printStream);
            }
            _printStream.println("</" + XML_FORM_TAG + ">");
        }
    }

    public void _loadFromXML(Element _element, ResourceContainer _container) throws IOException
    {
        synchronized (ap_storage)
        {
            removeAll();
            removeAllRulers();

            String s_id = _element.getAttribute(XML_COMPONENT_ID).trim();

            String s_width  = _element.getAttribute(XML_COMPONENT_WIDTH).trim();
            String s_height  = _element.getAttribute(XML_COMPONENT_HEIGHT).trim();

            String s_channel  = _element.getAttribute(XML_COMPONENT_CHANNEL).trim();

            String s_backgroundColor = _element.getAttribute(XML_COMPONENT_BACKGROUND_COLOR);
            String s_pressedColor = _element.getAttribute(XML_COMPONENT_PRESSED_COLOR);
            String s_selectedColor = _element.getAttribute(XML_COMPONENT_SELECTED_COLOR);
            String s_normalColor = _element.getAttribute(XML_COMPONENT_NORMAL_COLOR);
            String s_disabledColor = _element.getAttribute(XML_COMPONENT_DISABLED_COLOR);

            s_ID = s_id;

            Element p_rulersElement = _getElement(_element,XML_RULERS);
            if (p_rulersElement != null)
            {
                NodeList p_rulersList = p_rulersElement.getElementsByTagName(FormRuler.XML_RULER);
                for(int li=0;li<p_rulersList.getLength();li++)
                {
                    Element p_r = (Element) p_rulersList.item(li);
                    if (!p_r.getParentNode().equals(p_rulersElement)) continue;
                    FormRuler p_newRuler = new FormRuler(0,0);
                    p_newRuler.loadFromXML(p_r);
                    addRuler(p_newRuler);
                }
            }

            try
            {
                i_channel   = Integer.parseInt(s_channel);
                i_Width     = Integer.parseInt(s_width);
                i_Height    = Integer.parseInt(s_height);

                p_backgroundColor   = new Color(Integer.parseInt(s_backgroundColor));
                p_SelectedTextColor = new Color(Integer.parseInt(s_selectedColor));
                p_DisabledTextColor = new Color(Integer.parseInt(s_disabledColor));
                p_NormalTextColor   = new Color(Integer.parseInt(s_normalColor));
                p_PressedTextColor  = new Color(Integer.parseInt(s_pressedColor));
            }
            catch (NumberFormatException e)
            {
                throw new IOException("Error attribute in "+s_id+" component");
            }

            lg_pinned = false;
            lg_hidden = false;
            lg_locked = false;

            NodeList p_components = _element.getElementsByTagName(XML_COMPONENT);
            for (int li = 0; li < p_components.getLength(); li++)
            {
                Element p_compo = (Element) p_components.item(li);
                if (!p_compo.getParentNode().equals(_element)) continue;

                String s_ID = p_compo.getAttribute(AbstractFormComponent.XML_COMPONENT_ID);
                String s_type = p_compo.getAttribute(AbstractFormComponent.XML_TYPE);
                if (s_ID.length() == 0 || s_type.length() == 0) throw new IOException("Error file format");

                int i_type = Integer.parseInt(s_type);

                AbstractFormComponent p_newComponent = null;
                switch (i_type)
                {
                    case AbstractFormComponent.COMPONENT_BUTTON:
                        p_newComponent = new RrgFormComponent_Button(this, s_ID, null);
                        break;
                    case AbstractFormComponent.COMPONENT_IMAGE:
                        p_newComponent = new RrgFormComponent_Image(this, s_ID, null);
                        break;
                    case AbstractFormComponent.COMPONENT_LABEL:
                        p_newComponent = new RrgFormComponent_Label(this, s_ID);
                        break;
                    case AbstractFormComponent.COMPONENT_PATH:
                        p_newComponent = new RrgFormComponent_Path(this, s_ID);
                        break;
                    case AbstractFormComponent.COMPONENT_CUSTOMAREA:
                        p_newComponent = new RrgFormComponent_CustomArea(this, s_ID,Color.black,0);
                        break;
                    default:
                        throw new IOException("Unsupported component type " + i_type);
                }

                p_newComponent.loadFromXML(p_compo, _container);
                addComponent(p_newComponent);
            }

            fillComponentPropertiesFromForm();
        }
    }
}
