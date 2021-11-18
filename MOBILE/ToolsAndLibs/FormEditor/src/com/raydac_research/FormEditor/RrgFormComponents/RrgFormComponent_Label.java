package com.raydac_research.FormEditor.RrgFormComponents;

import com.raydac_research.FormEditor.RrgFormResources.*;

import java.io.PrintStream;
import java.io.IOException;
import java.awt.*;
import java.awt.image.BufferedImage;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class RrgFormComponent_Label extends AbstractFormComponent
{
    protected RrgResource_Font p_labelFont;
    protected RrgResource_Text p_labelText;
    protected RrgResource_Font p_labelFont_trans;
    protected RrgResource_Text p_labelText_trans;
    protected Color p_labelColor;
    protected Color p_labelColor_trans;
    protected BufferedImage p_labelImage;

    protected static final String XML_TEXT_RESOURCE = "text_resource";
    protected static final String XML_FONT_RESOURCE = "font_resource";
    protected static final String XML_PROPERTIES = "properties";
    protected static final String XML_COLOR = "color";

    public void copyTo(AbstractFormComponent _component)
    {
        synchronized (_component)
        {
            super.copyTo(_component);
            RrgFormComponent_Label p_comp = (RrgFormComponent_Label) _component;

            p_comp.p_labelFont = p_labelFont;
            p_comp.p_labelColor = p_labelColor;
            p_comp.p_labelText =  p_labelText;

            p_comp.resourceUpdated();
        }
    }

    public void startTransaction()
    {
        super.startTransaction();
        p_labelColor_trans = p_labelColor;
        p_labelFont_trans = p_labelFont;
        p_labelText_trans = p_labelText;
    }

    public int getMinimumHeight()
    {
        if (p_labelImage != null) return p_labelImage.getHeight(); else return super.getMinimumHeight();
    }

    public int getMinimumWidth()
    {
        if (p_labelImage != null) return p_labelImage.getWidth(); else return super.getMinimumWidth();
    }

    public void rollbackTransaction(boolean _anyway)
    {
        if (!lg_transacted && !_anyway) return;

        RrgResource_Text p_labelText_p = p_labelText;
        setLabelText(p_labelText_trans);
        p_labelText_trans = p_labelText_p;

        Color p_labelColor_p = p_labelColor;
        setLabelColor(p_labelColor_trans);
        p_labelColor_trans = p_labelColor_p;

        RrgResource_Font p_labelFont_p = p_labelFont;
        setLabelFont(p_labelFont_trans);
        p_labelFont_trans = p_labelFont_p;

        super.rollbackTransaction(_anyway);
    }

    public RrgFormComponent_Label()
    {
        super();
        p_labelColor = Color.black;
    }

    public RrgFormComponent_Label(FormContainer _parent, String _id)
    {
        super(_parent, _id, COMPONENT_LABEL, 0, 0);
        p_labelColor = Color.black;
    }

    public void setLabelColor(Color _color)
    {
        if (_color == null) return;
        p_labelColor = _color;
        resourceUpdated();
    }

    public Color getLabelColor()
    {
        return p_labelColor;
    }

    public void setLabelText(RrgResource_Text _text)
    {
        p_labelText = _text;
        resourceUpdated();
    }

    public boolean resizable()
    {
        return false;
    }

    public RrgResource_Text getLabelText()
    {
        return p_labelText;
    }

    public void setLabelFont(RrgResource_Font _font)
    {
        p_labelFont = _font;
        resourceUpdated();
    }

    public RrgResource_Font getLabelFont()
    {
        return p_labelFont;
    }

    public void resourceUpdated()
    {
        if (p_labelFont != null && p_labelText != null)
        {
            p_labelImage = p_labelFont.getFont().makeTransparentStringImage(p_labelText.getText(), p_labelColor);
            setWidthHeight(p_labelImage.getWidth(), p_labelImage.getHeight());
            //if (i_Width < p_labelImage.getWidth()) setWidthHeight(p_labelImage.getWidth(), i_Height);
            //if (i_Height < p_labelImage.getHeight()) setWidthHeight(i_Width, p_labelImage.getHeight());
        }
        else
        {
            p_labelImage = null;
            setWidthHeight(getMinimumWidth(), getMinimumHeight());
        }
    }

    public boolean doesUseResource(AbstractRrgResource _resource)
    {
        if (_resource == null) return false;
        if (p_labelFont != null && p_labelFont.equals(_resource)) return true;
        if (p_labelText != null && p_labelText.equals(_resource)) return true;
        return false;
    }

    public boolean isVisualComponent()
    {
        return true;
    }

    public void paintContent(Graphics _g, boolean _focused)
    {
        if (p_labelImage != null)
        {
            _g.drawImage(p_labelImage, i_X, i_Y, null);
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

        s_str = "<" + XML_PROPERTIES + " " + XML_TEXT_RESOURCE + "=\"" + (p_labelText == null ? "" : p_labelText.getResourceID()) + "\" " + XML_FONT_RESOURCE + "=\"" + (p_labelFont == null ? "" : p_labelFont.getResourceID()) + "\" " + XML_COLOR + "=\"" + (p_labelColor == null ? "" : Integer.toString(p_labelColor.getRGB())) + "\"/>";
        _printStream.println(s_str);

        s_str = "</" + XML_COMPONENT_INFO + ">";
        _printStream.println(s_str);
    }

    public void _loadFromXML(Element _element, ResourceContainer _container) throws IOException
    {
        NodeList p_properties = _element.getElementsByTagName(XML_PROPERTIES);
        if (p_properties.getLength() == 0) throw new IOException("Component " + s_ID + " format error");
        Element p_image = (Element) p_properties.item(0);
        String s_textID = p_image.getAttribute(XML_TEXT_RESOURCE);
        String s_fontID = p_image.getAttribute(XML_FONT_RESOURCE);
        String s_color = p_image.getAttribute(XML_COLOR);

        int i_color;

        if (s_color.length() == 0) s_color = "0";

        try
        {
            i_color = Integer.parseInt(s_color);
        }
        catch (NumberFormatException e)
        {
            throw new IOException("Error attribute of \'" + s_ID + "\' component");
        }

        RrgResource_Text p_textRes = (RrgResource_Text) _container.getResourceForID(s_textID);
        if (p_textRes == null) throw new IOException("I can't find \'" + s_textID + "\' resource");
        RrgResource_Font p_fontRes = (RrgResource_Font) _container.getResourceForID(s_fontID);
        if (p_fontRes == null) throw new IOException("I can't find \'" + s_fontID + "\' resource");

        setLabelText(p_textRes);
        setLabelFont(p_fontRes);
        setLabelColor(new Color(i_color));
    }
}
