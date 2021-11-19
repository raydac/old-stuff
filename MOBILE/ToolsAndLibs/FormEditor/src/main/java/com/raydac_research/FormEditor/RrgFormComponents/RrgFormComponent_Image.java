package com.raydac_research.FormEditor.RrgFormComponents;

import com.raydac_research.FormEditor.RrgFormResources.RrgResource_Image;
import com.raydac_research.FormEditor.RrgFormResources.AbstractRrgResource;
import com.raydac_research.FormEditor.RrgFormResources.ResourceContainer;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.io.PrintStream;
import java.io.IOException;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class RrgFormComponent_Image extends AbstractFormComponent
{
    public static int MODIFIER_NONE = 0;
    public static int MODIFIER_FLIP_HORZ = 1;
    public static int MODIFIER_FLIP_VERT = 2;

    protected RrgResource_Image p_imageResource;
    protected int i_ScaleFactor;

    protected RrgResource_Image p_imageResource_trans;
    protected int i_ScaleFactor_trans;

    protected static final String XML_IMAGE = "image_resource";
    protected static final String XML_SCALE = "scale";
    protected static final String XML_FLIPV = "flipv";
    protected static final String XML_FLIPH = "fliph";

    protected int i_modifiers = MODIFIER_NONE;

    protected BufferedImage p_modifiedImage;

    public BufferedImage getModifiedImage()
    {
        return p_modifiedImage;
    }

    public void copyTo(AbstractFormComponent _component)
    {
        synchronized (_component)
        {
            super.copyTo(_component);
            RrgFormComponent_Image p_comp = (RrgFormComponent_Image) _component;

            p_comp.i_ScaleFactor = i_ScaleFactor;
            p_comp.i_modifiers = i_modifiers;

            p_comp.p_imageResource = p_imageResource;

            p_comp.resourceUpdated();
        }
    }

    public void startTransaction()
    {
        super.startTransaction();

        p_imageResource_trans = p_imageResource;
        i_ScaleFactor_trans = i_ScaleFactor;
    }

    public void setModifier(int _modifier)
    {
        if (i_modifiers != _modifier)
        {
            i_modifiers = _modifier;
            _updateModifyImage();
        }
    }

    public int getModifiers()
    {
        return i_modifiers;
    }

    private void _updateModifyImage()
    {
        int i_width = 1;
        int i_height = 1;

        if (p_imageResource != null)
        {
            i_width = p_imageResource.getWidth() * i_ScaleFactor;
            i_height = p_imageResource.getHeight() * i_ScaleFactor;
        }

        p_modifiedImage = new BufferedImage(i_width, i_height, BufferedImage.TYPE_INT_ARGB);
        synchronized (p_modifiedImage)
        {
            if (p_imageResource != null)
            {

                int[] ai_ImageBuffer = ((DataBufferInt) p_modifiedImage.getRaster().getDataBuffer()).getData();
                for (int li = 0; li < ai_ImageBuffer.length; li++) ai_ImageBuffer[li] = 0;
                ai_ImageBuffer = null;

                Graphics p_g = p_modifiedImage.getGraphics();
                p_g.drawImage(p_imageResource.getImage(), 0, 0, i_width, i_height, null);

                ai_ImageBuffer = ((DataBufferInt) p_modifiedImage.getRaster().getDataBuffer()).getData();
                if ((i_modifiers & MODIFIER_FLIP_VERT) != 0)
                {

                    // Вертикальный флип
                    if (i_height >= 2)
                    {
                        int i_topLineOffset = 0;
                        int i_downLineOffset = ai_ImageBuffer.length - i_width;

                        int[] ai_lineBuffer = new int[i_width];

                        while (true)
                        {
                            // Copy of the top line to the buffer
                            System.arraycopy(ai_ImageBuffer, i_topLineOffset, ai_lineBuffer, 0, i_width);
                            // Copy of the down line to the top line
                            System.arraycopy(ai_ImageBuffer, i_downLineOffset, ai_ImageBuffer, i_topLineOffset, i_width);
                            // Copy the buffer to the down line
                            System.arraycopy(ai_lineBuffer, 0, ai_ImageBuffer, i_downLineOffset, i_width);

                            i_topLineOffset += i_width;
                            i_downLineOffset -= i_width;

                            if (i_topLineOffset > i_downLineOffset) break;
                        }
                    }
                }

                // Горизонтальный флип
                if ((i_modifiers & MODIFIER_FLIP_HORZ) != 0)
                {
                    if (i_width >= 2)
                    {
                        int i_leftLineOffset = 0;
                        int i_rightLineOffset = i_leftLineOffset + i_width - 1;
                        int i_maxDown = i_rightLineOffset + ((i_height - 1) * i_width);

                        while (i_leftLineOffset < i_rightLineOffset)
                        {
                            int i_vertLOffst = i_leftLineOffset;
                            int i_vertROffst = i_rightLineOffset;
                            while (i_vertLOffst < i_maxDown)
                            {
                                int i_buff = ai_ImageBuffer[i_vertLOffst];
                                ai_ImageBuffer[i_vertLOffst] = ai_ImageBuffer[i_vertROffst];
                                ai_ImageBuffer[i_vertROffst] = i_buff;
                                i_vertLOffst += i_width;
                                i_vertROffst += i_width;
                            }

                            i_leftLineOffset++;
                            i_rightLineOffset--;
                        }
                    }
                }
            }
        }
        setWidthHeight(i_width, i_height);
    }

    public void rollbackTransaction(boolean _anyway)
    {
        if (!lg_transacted && !_anyway) return;
        super.rollbackTransaction(_anyway);

        RrgResource_Image p_imageResource_p = p_imageResource;
        setImageResource(p_imageResource_trans);
        p_imageResource_trans = p_imageResource_p;

        int i_ScaleFactor_p = i_ScaleFactor;
        setScale(i_ScaleFactor_trans);
        i_ScaleFactor_trans = i_ScaleFactor_p;
    }

    public RrgFormComponent_Image()
    {
        super();
        i_ScaleFactor = 1;
        i_modifiers = MODIFIER_NONE;
    }

    public RrgFormComponent_Image(FormContainer _parent, String _id, RrgResource_Image _resource)
    {
        super(_parent, _id, COMPONENT_IMAGE, 0, 0);
        i_ScaleFactor = 1;
        i_modifiers = MODIFIER_NONE;
        setImageResource(_resource);
        _updateModifyImage();
    }

    public int getScale()
    {
        return i_ScaleFactor;
    }

    public void setScale(int _scale)
    {
        if (_scale < 1 || _scale > 10) _scale = 1;
        if (_scale != i_ScaleFactor)
        {
            i_ScaleFactor = _scale;
            _updateModifyImage();
        }
    }

    public void resourceUpdated()
    {
        if (p_imageResource != null) setImageResource(p_imageResource);
    }

    public boolean doesUseResource(AbstractRrgResource _resource)
    {
        if (_resource.equals(p_imageResource)) return true;
        return false;
    }

    public void setImageResource(RrgResource_Image _resource)
    {
        if (_resource == null)
        {
            p_imageResource = null;
            return;
        }
        p_imageResource = _resource;
        _updateModifyImage();
    }

    public RrgResource_Image getImageResource()
    {
        return p_imageResource;
    }

    public void paintContent(Graphics _g, boolean _focused)
    {
        _g.drawImage(p_modifiedImage, i_X, i_Y, null);
    }

    public boolean isVisualComponent()
    {
        return true;
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

        String s_imageResource = p_imageResource == null ? "" : p_imageResource.getResourceID();

        String s_flipH = (i_modifiers & MODIFIER_FLIP_HORZ) == 0 ? "" : XML_FLIPH + "=\"true\"";
        String s_flipV = (i_modifiers & MODIFIER_FLIP_VERT) == 0 ? "" : XML_FLIPV + "=\"true\"";

        s_str = "<" + XML_IMAGE + " id=\"" + s_imageResource + "\" " + XML_SCALE + "=\"" + i_ScaleFactor + "\" " + s_flipH + " " + s_flipV + "/>";
        _printStream.println(s_str);

        s_str = "</" + XML_COMPONENT_INFO + ">";
        _printStream.println(s_str);
    }

    public void _loadFromXML(Element _element, ResourceContainer _container) throws IOException
    {
        NodeList p_images = _element.getElementsByTagName(XML_IMAGE);
        if (p_images.getLength() == 0) throw new IOException("Component " + s_ID + " format error");
        Element p_image = (Element) p_images.item(0);
        String s_resourceID = p_image.getAttribute("id");

        String s_scale = p_image.getAttribute(XML_SCALE);

        if (s_scale.length() == 0) s_scale = "1";

        boolean lg_flipV = p_image.getAttribute(XML_FLIPV).length() != 0;
        boolean lg_flipH = p_image.getAttribute(XML_FLIPH).length() != 0;

        int i_scale;

        try
        {
            i_scale = Integer.parseInt(s_scale);
        }
        catch (NumberFormatException e)
        {
            throw new IOException("Error attribute of \'" + s_ID + "\' component");
        }

        RrgResource_Image p_imageRes = (RrgResource_Image) _container.getResourceForID(s_resourceID);
        if (p_imageRes == null) throw new IOException("I can't find \'" + s_resourceID + "\' resource");
        setImageResource(p_imageRes);
        setScale(i_scale);
        int i_modifiers = (lg_flipV ? MODIFIER_FLIP_VERT : MODIFIER_NONE) | (lg_flipH ? MODIFIER_FLIP_HORZ : MODIFIER_NONE);
        setModifier(i_modifiers);
    }
}
