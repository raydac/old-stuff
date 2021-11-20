package com.raydac_research.FormEditor.RrgFormComponents;

import com.raydac_research.FormEditor.RrgFormResources.*;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.PrintStream;
import java.io.IOException;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class RrgFormComponent_Button extends AbstractFormComponent
{
    protected RrgResource_Image p_normalImage;
    protected RrgResource_Image p_selectedImage;
    protected RrgResource_Image p_disabledImage;
    protected RrgResource_Image p_pressedImage;

    protected RrgResource_Sound p_clickSound;
    protected RrgResource_Text p_Text;
    protected RrgResource_Font p_Font;

    protected int i_TextHorzAlign;
    protected int i_TextVertAlign;

    protected BufferedImage p_bufferNormalTextImage;
    protected BufferedImage p_bufferSelectedTextImage;
    protected BufferedImage p_bufferPressedTextImage;
    protected BufferedImage p_bufferDisabledTextImage;

    public static final int TEXT_ALIGN_HORZ_LEFT = 0;
    public static final int TEXT_ALIGN_HORZ_CENTER = 1;
    public static final int TEXT_ALIGN_HORZ_RIGHT = 2;

    public static final int TEXT_ALIGN_VERT_TOP = 0;
    public static final int TEXT_ALIGN_VERT_CENTER = 1;
    public static final int TEXT_ALIGN_VERT_BOTTOM = 2;

    protected static final String XML_NORMAL_IMAGE = "normal_image";
    protected static final String XML_SELECTED_IMAGE = "selected_image";
    protected static final String XML_DISABLED_IMAGE = "disabled_image";
    protected static final String XML_PRESSED_IMAGE = "pressed_image";
    protected static final String XML_FONT = "font";
    protected static final String XML_CLICK_SOUND = "click_sound";
    protected static final String XML_TEXT = "text";
    protected static final String XML_TEXT_HALIGN = "halign";
    protected static final String XML_TEXT_VALIGN = "valign";

    protected RrgResource_Image p_normalImage_trans;
    protected RrgResource_Image p_selectedImage_trans;
    protected RrgResource_Image p_disabledImage_trans;
    protected RrgResource_Image p_pressedImage_trans;

    protected RrgResource_Sound p_clickSound_trans;
    protected RrgResource_Text p_Text_trans;
    protected RrgResource_Font p_Font_trans;

    protected int i_TextHorzAlign_trans;
    protected int i_TextVertAlign_trans;

    protected BufferedImage p_bufferNormalTextImage_trans;
    protected BufferedImage p_bufferSelectedTextImage_trans;
    protected BufferedImage p_bufferPressedTextImage_trans;
    protected BufferedImage p_bufferDisabledTextImage_trans;


    public void copyTo(AbstractFormComponent _component)
    {
        synchronized (_component)
        {
            super.copyTo(_component);
            RrgFormComponent_Button p_comp = (RrgFormComponent_Button) _component;

            p_comp.setNormalImage(p_normalImage);
            p_comp.setSelectedImage(p_selectedImage);
            p_comp.setDisabledImage(p_disabledImage);
            p_comp.setPressedImage(p_pressedImage);

            p_comp.setClickSound(p_clickSound);
            p_comp.setText(p_Text);
            p_comp.setFont(p_Font);

            p_comp.i_TextHorzAlign = i_TextHorzAlign;
            p_comp.i_TextVertAlign = i_TextVertAlign;

            p_comp.resourceUpdated();
        }
    }


    public void startTransaction()
    {
        super.startTransaction();

        p_normalImage_trans = p_normalImage;
        p_selectedImage_trans = p_selectedImage;
        p_disabledImage_trans = p_disabledImage;
        p_pressedImage_trans = p_pressedImage;

        p_clickSound_trans = p_clickSound;
        p_Text_trans = p_Text;
        p_Font_trans = p_Font;

        i_TextHorzAlign_trans = i_TextHorzAlign;
        i_TextVertAlign_trans = i_TextVertAlign;
    }

    public void rollbackTransaction(boolean _anyway)
    {
        if (!lg_transacted && !_anyway) return;

        super.rollbackTransaction(_anyway);

        RrgResource_Image p_normalImage_p = p_normalImage;
        setNormalImage(p_normalImage_trans);
        p_normalImage_trans = p_normalImage_p;

        RrgResource_Image p_selectedImage_p = p_selectedImage;
        setSelectedImage(p_selectedImage_trans);
        p_selectedImage_trans = p_selectedImage_p;

        RrgResource_Image p_disabledImage_p = p_disabledImage;
        setDisabledImage(p_disabledImage_trans);
        p_disabledImage_trans = p_disabledImage_p;

        RrgResource_Image p_pressedImage_p = p_pressedImage;
        setPressedImage(p_pressedImage_trans);
        p_pressedImage_trans = p_pressedImage_p;

        RrgResource_Sound p_clickSound_p = p_clickSound;
        setClickSound(p_clickSound_trans);
        p_clickSound_trans = p_clickSound_p;

        RrgResource_Text p_Text_p = p_Text;
        setText(p_Text_trans);
        p_Text_trans = p_Text_p;

        RrgResource_Font p_Font_p = p_Font;
        setFont(p_Font_trans);
        p_Font_trans = p_Font_p;

        int i_TextHorzAlign_p = i_TextHorzAlign;
        setTextHorzAlign(i_TextHorzAlign_trans);
        i_TextHorzAlign_trans = i_TextHorzAlign_p;

        int i_TextVertAlign_p = i_TextVertAlign;
        setTextVertAlign(i_TextVertAlign_trans);
        i_TextVertAlign_trans = i_TextVertAlign_p;
    }


    public void resourceUpdated()
    {
        if (p_normalImage != null) setNormalImage(p_normalImage);
        updateTextOrFont();
    }

    public void setTextHorzAlign(int _newAlign)
    {
        i_TextHorzAlign = _newAlign;
    }

    public int getTextHorzAlign()
    {
        return i_TextHorzAlign;
    }

    private void updateTextOrFont()
    {
        if (p_Text == null || p_Font == null)
        {
            p_bufferNormalTextImage = null;
            p_bufferSelectedTextImage = null;
            p_bufferPressedTextImage = null;
            p_bufferDisabledTextImage = null;
            return;
        }

        String s_text = p_Text.getText();
        p_bufferNormalTextImage = p_Font.getFont().makeTransparentStringImage(s_text, p_parent.p_NormalTextColor);
        p_bufferSelectedTextImage = p_Font.getFont().makeTransparentStringImage(s_text, p_parent.p_SelectedTextColor);
        p_bufferPressedTextImage = p_Font.getFont().makeTransparentStringImage(s_text, p_parent.p_PressedTextColor);
        p_bufferDisabledTextImage = p_Font.getFont().makeTransparentStringImage(s_text, p_parent.p_DisabledTextColor);
    }

    public void setTextVertAlign(int _newAlign)
    {
        i_TextVertAlign = _newAlign;
    }

    public int getTextVertAlign()
    {
        return i_TextVertAlign;
    }

    public boolean doesUseResource(AbstractRrgResource _resource)
    {
        if (_resource.equals(p_normalImage)) return true;
        if (_resource.equals(p_disabledImage)) return true;
        if (_resource.equals(p_Font)) return true;
        if (_resource.equals(p_clickSound)) return true;
        if (_resource.equals(p_pressedImage)) return true;
        if (_resource.equals(p_selectedImage)) return true;
        if (_resource.equals(p_Text)) return true;
        return false;
    }

    public RrgFormComponent_Button(FormContainer _parent, String _id, RrgResource_Image _normalImage)
    {
        super(_parent, _id, COMPONENT_BUTTON, 0, 0);
        setNormalImage(_normalImage);
        p_clickSound = null;
        p_selectedImage = null;
        p_disabledImage = null;
        p_pressedImage = null;
        p_Text = null;
        setTextHorzAlign(TEXT_ALIGN_HORZ_CENTER);
        setTextVertAlign(TEXT_ALIGN_VERT_CENTER);
    }

    public RrgFormComponent_Button()
    {
        super();
        p_clickSound = null;
        p_selectedImage = null;
        p_disabledImage = null;
        p_pressedImage = null;
        p_Text = null;
    }

    public void processMouseClick(int _button)
    {
        if (p_clickSound != null) p_clickSound.getAudioClip().play();
    }

    public RrgResource_Font getFont()
    {
        return p_Font;
    }

    public void setFont(RrgResource_Font _font)
    {
        p_Font = _font;
        updateTextOrFont();
    }

    public RrgResource_Text getText()
    {
        return p_Text;
    }

    public void setText(RrgResource_Text _text)
    {
        p_Text = _text;
        updateTextOrFont();
    }

    public RrgResource_Sound getClickSound()
    {
        return p_clickSound;
    }

    public void setClickSound(RrgResource_Sound _sound)
    {
        p_clickSound = _sound;
    }

    public RrgResource_Image getNormalImage()
    {
        return p_normalImage;
    }

    public void setNormalImage(RrgResource_Image _image)
    {
        if (_image == null) return;
        p_normalImage = _image;
        setWidthHeight(p_normalImage.getWidth(), p_normalImage.getHeight());
    }

    public RrgResource_Image getSelectedImage()
    {
        return p_selectedImage;
    }

    public void setSelectedImage(RrgResource_Image _image)
    {
        p_selectedImage = _image;
    }

    public RrgResource_Image getDisabledImage()
    {
        return p_disabledImage;
    }

    public void setDisabledImage(RrgResource_Image _image)
    {
        p_disabledImage = _image;
    }

    public RrgResource_Image getPressedImage()
    {
        return p_pressedImage;
    }

    public void setPressedImage(RrgResource_Image _image)
    {
        p_pressedImage = _image;
    }

    public void paintContent(Graphics _g, boolean _focused)
    {
        RrgResource_Image p_imageForDrawing = p_normalImage;

        switch (i_State)
        {
            case STATE_DISABLED:
                {
                    if (p_disabledImage != null) p_imageForDrawing = p_disabledImage;
                }
                ;
                break;
            case STATE_PRESSED:
                {
                    if (p_pressedImage != null) p_imageForDrawing = p_pressedImage;
                }
                ;
                break;
            case STATE_SELECTED:
                {
                    if (p_selectedImage != null) p_imageForDrawing = p_selectedImage;
                }
                ;
                break;
        }

        _g.drawImage(p_imageForDrawing.getImage(), i_X, i_Y, null);

        // Drawing of the text if it is presented
        if (p_bufferNormalTextImage != null)
        {
            BufferedImage p_bImage = null;

            switch (i_State)
            {
                case STATE_NORMAL:
                    p_bImage = p_bufferNormalTextImage;
                    break;
                case STATE_SELECTED:
                    p_bImage = p_bufferSelectedTextImage;
                    break;
                case STATE_PRESSED:
                    p_bImage = p_bufferPressedTextImage;
                    break;
                case STATE_DISABLED:
                    p_bImage = p_bufferDisabledTextImage;
                    break;
            }

            int i_y = p_bImage.getHeight();

            switch (i_TextVertAlign)
            {
                case RrgFormComponent_Button.TEXT_ALIGN_VERT_BOTTOM:
                    i_y = i_Height - i_y;
                    break;
                case RrgFormComponent_Button.TEXT_ALIGN_VERT_CENTER:
                    i_y = (i_Height - i_y) >> 1;
                    break;
                case RrgFormComponent_Button.TEXT_ALIGN_VERT_TOP:
                    i_y = 0;
                    break;
            }

            int i_x = p_bImage.getWidth();

            switch (i_TextHorzAlign)
            {
                case RrgFormComponent_Button.TEXT_ALIGN_HORZ_CENTER:
                    i_x = (i_Width - i_x) >> 1;
                    break;
                case RrgFormComponent_Button.TEXT_ALIGN_HORZ_LEFT:
                    i_x = 0;
                    break;
                case RrgFormComponent_Button.TEXT_ALIGN_HORZ_RIGHT:
                    i_x = i_Width - i_x;
                    break;
            }

            _g.drawImage(p_bImage, i_X + i_x, i_Y + i_y, null);
        }
    }

    public boolean isVisualComponent()
    {
        return true;
    }

    public boolean canBeFocused()
    {
        return true;
    }


    public void _saveAsXML(PrintStream _printStream)
    {
        if (_printStream == null) return;

        String s_str = "<" + XML_COMPONENT_INFO + " " + XML_TEXT_HALIGN + "=\"" + i_TextHorzAlign + "\" " + XML_TEXT_VALIGN + "=\"" + i_TextVertAlign + "\">";
        _printStream.println(s_str);

        String s_normalid = p_normalImage == null ? "" : p_normalImage.getResourceID();
        String s_disabledid = p_disabledImage == null ? "" : p_disabledImage.getResourceID();
        String s_pressedid = p_pressedImage == null ? "" : p_pressedImage.getResourceID();
        String s_selectedid = p_selectedImage == null ? "" : p_selectedImage.getResourceID();
        String s_textid = p_Text == null ? "" : p_Text.getResourceID();
        String s_fornid = p_Font == null ? "" : p_Font.getResourceID();
        String s_clicksound = p_clickSound == null ? "" : p_clickSound.getResourceID();

        s_str = "<" + XML_NORMAL_IMAGE + " id=\"" + s_normalid + "\"/>";
        _printStream.println(s_str);
        s_str = "<" + XML_DISABLED_IMAGE + " id=\"" + s_disabledid + "\"/>";
        _printStream.println(s_str);
        s_str = "<" + XML_PRESSED_IMAGE + " id=\"" + s_pressedid + "\"/>";
        _printStream.println(s_str);
        s_str = "<" + XML_SELECTED_IMAGE + " id=\"" + s_selectedid + "\"/>";
        _printStream.println(s_str);
        s_str = "<" + XML_TEXT + " id=\"" + s_textid + "\"/>";
        _printStream.println(s_str);
        s_str = "<" + XML_FONT + " id=\"" + s_fornid + "\"/>";
        _printStream.println(s_str);
        s_str = "<" + XML_CLICK_SOUND + " id=\"" + s_clicksound + "\"/>";
        _printStream.println(s_str);

        s_str = "</" + XML_COMPONENT_INFO + ">";
        _printStream.println(s_str);
    }

    public void _loadFromXML(Element _element, ResourceContainer _container) throws IOException
    {
        String s_textVAlign = _element.getAttribute(XML_TEXT_VALIGN);
        String s_textHAlign = _element.getAttribute(XML_TEXT_HALIGN);

        int i_vertAlign;
        int i_horzAlign;

        try
        {
            i_horzAlign = Integer.parseInt(s_textHAlign);
            i_vertAlign = Integer.parseInt(s_textVAlign);
        }
        catch (NumberFormatException e)
        {
            throw new IOException("Error an attribute value of \'" + s_ID + "\' component");
        }

        setTextHorzAlign(i_horzAlign);
        setTextVertAlign(i_vertAlign);

        NodeList p_images = _element.getElementsByTagName(XML_NORMAL_IMAGE);
        if (p_images.getLength() == 0) throw new IOException("Component " + s_ID + " format error");
        Element p_image = (Element) p_images.item(0);
        String s_resourceID = p_image.getAttribute("id");
        RrgResource_Image p_imageRes = (RrgResource_Image) _container.getResourceForID(s_resourceID);
        if (p_imageRes == null) throw new IOException("I can't find \'" + s_ID + "\' resource");
        setNormalImage(p_imageRes);

        p_images = _element.getElementsByTagName(XML_DISABLED_IMAGE);
        if (p_images.getLength() == 0) throw new IOException("Component " + s_ID + " format error");
        p_image = (Element) p_images.item(0);
        s_resourceID = p_image.getAttribute("id");
        if (s_resourceID.length() == 0)
        {
            setDisabledImage(null);
        }
        else
        {
            p_imageRes = (RrgResource_Image) _container.getResourceForID(s_resourceID);
            if (p_imageRes == null) throw new IOException("I can't find \'" + s_ID + "\' resource");
            setDisabledImage(p_imageRes);
        }

        p_images = _element.getElementsByTagName(XML_PRESSED_IMAGE);
        if (p_images.getLength() == 0) throw new IOException("Component " + s_ID + " format error");
        p_image = (Element) p_images.item(0);
        s_resourceID = p_image.getAttribute("id");
        if (s_resourceID.length() == 0)
        {
            setPressedImage(null);
        }
        else
        {
            p_imageRes = (RrgResource_Image) _container.getResourceForID(s_resourceID);
            if (p_imageRes == null) throw new IOException("I can't find \'" + s_ID + "\' resource");
            setPressedImage(p_imageRes);
        }

        p_images = _element.getElementsByTagName(XML_SELECTED_IMAGE);
        if (p_images.getLength() == 0) throw new IOException("Component " + s_ID + " format error");
        p_image = (Element) p_images.item(0);
        s_resourceID = p_image.getAttribute("id");
        if (s_resourceID.length() == 0)
        {
            setSelectedImage(null);
        }
        else
        {
            p_imageRes = (RrgResource_Image) _container.getResourceForID(s_resourceID);
            if (p_imageRes == null) throw new IOException("I can't find \'" + s_ID + "\' resource");
            setSelectedImage(p_imageRes);
        }

        p_images = _element.getElementsByTagName(XML_TEXT);
        if (p_images.getLength() == 0) throw new IOException("Component " + s_ID + " format error");
        p_image = (Element) p_images.item(0);
        s_resourceID = p_image.getAttribute("id");
        if (s_resourceID.length() == 0)
        {
            setText(null);
        }
        else
        {
            RrgResource_Text p_textRes = (RrgResource_Text) _container.getResourceForID(s_resourceID);
            if (p_textRes == null) throw new IOException("I can't find \'" + s_ID + "\' resource");
            setText(p_textRes);
        }

        p_images = _element.getElementsByTagName(XML_FONT);
        if (p_images.getLength() == 0) throw new IOException("Component " + s_ID + " format error");
        p_image = (Element) p_images.item(0);
        s_resourceID = p_image.getAttribute("id");
        if (s_resourceID.length() == 0)
        {
            setFont(null);
        }
        else
        {
            RrgResource_Font p_fontRes = (RrgResource_Font) _container.getResourceForID(s_resourceID);
            if (p_fontRes == null) throw new IOException("I can't find \'" + s_ID + "\' resource");
            setFont(p_fontRes);
        }

        p_images = _element.getElementsByTagName(XML_CLICK_SOUND);
        if (p_images.getLength() == 0) throw new IOException("Component " + s_ID + " format error");
        p_image = (Element) p_images.item(0);
        s_resourceID = p_image.getAttribute("id");
        if (s_resourceID.length() == 0)
        {
            setClickSound(null);
        }
        else
        {
            RrgResource_Sound p_soundRes = (RrgResource_Sound) _container.getResourceForID(s_resourceID);
            if (p_soundRes == null) throw new IOException("I can't find \'" + s_ID + "\' resource");
            setClickSound(p_soundRes);
        }
    }

}
