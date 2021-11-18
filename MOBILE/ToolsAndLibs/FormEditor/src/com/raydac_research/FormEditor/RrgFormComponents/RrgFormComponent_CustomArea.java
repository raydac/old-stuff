package com.raydac_research.FormEditor.RrgFormComponents;

import com.raydac_research.FormEditor.RrgFormResources.*;

import java.io.PrintStream;
import java.io.IOException;
import java.awt.*;
import java.awt.image.BufferedImage;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class RrgFormComponent_CustomArea extends AbstractFormComponent
{
    protected Color p_areaColor;
    protected Color p_arealColor_trans;
    protected int i_areaValue;
    protected int i_areaValue_trans;

    protected static final String XML_PROPERTIES = "properties";
    protected static final String XML_COLOR = "color";
    protected static final String XML_AREAVALUE = "areavalue";

    public void copyTo(AbstractFormComponent _component)
    {
        synchronized (_component)
        {
            super.copyTo(_component);
            RrgFormComponent_CustomArea p_comp = (RrgFormComponent_CustomArea) _component;

            p_comp.p_areaColor = p_areaColor;
            p_comp.i_areaValue = i_areaValue;

            p_comp.resourceUpdated();
        }
    }

    public void startTransaction()
    {
        super.startTransaction();
        p_arealColor_trans = p_areaColor;
    }

    public int getMinimumHeight()
    {
        return 1;
    }

    public int getMinimumWidth()
    {
        return 1;
    }

    public int getAreaValue()
    {
        return i_areaValue;
    }

    public void setAreaValue(int _areaValue)
    {
        i_areaValue = _areaValue;
    }

    public void rollbackTransaction(boolean _anyway)
    {
        if (!lg_transacted && !_anyway) return;

        Color p_labelColor_p = p_areaColor;
        setAreaColor(p_arealColor_trans);
        p_arealColor_trans = p_labelColor_p;

        i_areaValue_trans = i_areaValue;

        super.rollbackTransaction(_anyway);
    }

    public RrgFormComponent_CustomArea(FormContainer _parent, String _id, Color _color, int _value)
    {
        super(_parent, _id, COMPONENT_CUSTOMAREA, 0, 0);
        p_areaColor = _color;
        i_areaValue = _value;
    }

    public RrgFormComponent_CustomArea()
    {
        super();
        p_areaColor = null;
        i_areaValue = 0;
    }

    public void setAreaColor(Color _color)
    {
        if (_color == null) return;
        p_areaColor = _color;
        resourceUpdated();
    }

    public Color getAreaColor()
    {
        return p_areaColor;
    }

    public boolean resizable()
    {
        return true;
    }

    public void resourceUpdated()
    {
    }

    public boolean doesUseResource(AbstractRrgResource _resource)
    {
        return false;
    }

    public boolean isVisualComponent()
    {
        return true;
    }

    private static Font p_Font = new Font("Arial", Font.PLAIN, 9);
    private static FontMetrics p_FMetrics = Toolkit.getDefaultToolkit().getFontMetrics(p_Font);

    public void paintContent(Graphics _g, boolean _focused)
    {
        _g.setColor(p_areaColor);
        _g.drawRect(i_X, i_Y, i_Width - 1, i_Height - 1);

        if (_focused)
        {
            final int STEP = 2;

            // Отрисовываем сфокусированный
            _g.drawRect(i_X, i_Y, i_Width - 1, i_Height - 1);
            _g.drawRect(i_X+1, i_Y+1, i_Width - 3, i_Height - 3);

            // Отрисовываем несфокусированный
            for (int lx = 0; lx < i_Width; lx += STEP)
            {
                for (int ly = 0; ly < i_Height; ly += STEP)
                {
                    _g.drawLine(i_X + lx, i_Y + ly, i_X + lx, i_Y + ly);
                }
            }

            _g.setFont(p_Font);
            String s_str = s_ID + " (" + i_areaValue + ")";
            int i_w = p_FMetrics.stringWidth(s_str+4);
            int i_h = p_FMetrics.getHeight()+4;

            _g.fillRect(i_X + 5, i_Y,i_w,i_h);
            _g.setColor(new Color((p_areaColor.getRGB() & 0xFFFFFF) ^ 0xFFFFFF));
            _g.drawString(s_str, i_X +5+2, i_Y + i_h-2);
        }
        else
        {
            final int STEP = 4;

            // Отрисовываем несфокусированный
            for (int lx = 0; lx < i_Width; lx += STEP)
            {
                for (int ly = 0; ly < i_Height; ly += STEP)
                {
                    _g.drawLine(i_X + lx, i_Y + ly, i_X + lx, i_Y + ly);
                }
            }

            _g.setFont(p_Font);
            String s_str = ""+i_areaValue;
            int i_w = p_FMetrics.stringWidth(s_str+4);
            int i_h = p_FMetrics.getHeight()+4;

            _g.fillRect(i_X + 5, i_Y,i_w,i_h);
            _g.setColor(new Color((p_areaColor.getRGB() & 0xFFFFFF) ^ 0xFFFFFF));
            _g.drawString(s_str, i_X +5+2, i_Y + i_h-2);
        }
    }

    public boolean canBeFocused()
    {
        return false;
    }

    public void _saveAsXML(PrintStream _printStream)
    {
        if (_printStream == null) return;

        String s_str = "<" + XML_COMPONENT_INFO + ">";
        _printStream.println(s_str);

        s_str = "<" + XML_PROPERTIES + " " + XML_AREAVALUE + "=\"" + i_areaValue + "\" " + XML_COLOR + "=\"" + (p_areaColor == null ? "" : Integer.toString(p_areaColor.getRGB())) + "\"/>";
        _printStream.println(s_str);

        s_str = "</" + XML_COMPONENT_INFO + ">";
        _printStream.println(s_str);
    }

    public void _loadFromXML(Element _element, ResourceContainer _container) throws IOException
    {
        NodeList p_properties = _element.getElementsByTagName(XML_PROPERTIES);
        if (p_properties.getLength() == 0) throw new IOException("Component " + s_ID + " format error");
        Element p_image = (Element) p_properties.item(0);
        String s_color = p_image.getAttribute(XML_COLOR);
        String s_areaValue = p_image.getAttribute(XML_AREAVALUE);

        int i_color;
        int i_areavalue;

        if (s_color.length() == 0) s_color = "0";
        if (s_areaValue.length() == 0) s_areaValue = "0";

        try
        {
            i_color = Integer.parseInt(s_color);
            i_areavalue = Integer.parseInt(s_areaValue);
        }
        catch (NumberFormatException e)
        {
            throw new IOException("Error attribute of \'" + s_ID + "\' component");
        }

        setAreaColor(new Color(i_color));
        setAreaValue(i_areavalue);
    }
}
