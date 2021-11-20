package com.raydac_research.FormEditor.RrgFormResources;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.io.PrintStream;
import java.io.IOException;
import java.io.File;

public class RrgResource_Text extends AbstractRrgResource
{
    protected String s_Text;
    protected String s_Text_trans;
    protected static final String XML_TEXT = "text";
    protected static final String XML_VALUE = "value";

    public boolean refreshResource() throws IOException
    {
        return false;
    }

    public void startTransaction()
    {
        super.startTransaction();
        s_Text_trans = s_Text;
    }

    public void rollbackTransaction()
    {
        super.rollbackTransaction();
        s_Text = s_Text_trans;
    }

    public RrgResource_Text(String _id,String _text)
    {
        super(_id,TYPE_TEXT);
        setText(_text);
    }

    public String getText()
    {
        return s_Text;
    }

    public void setText(String _text)
    {
        if (_text == null)
        {
            s_Text = "";
        }
        else
        {
            s_Text = _text;
        }
    }

    public void _saveAsXML(PrintStream _stream,File _file,boolean _relative)
    {
        if (_stream == null) return;
        String s_str = "<"+XML_RESOURCE_INFO+">";
        _stream.println(s_str);

        String s_stttr = s_Text.replaceAll("\n","(%nxt%)");

        s_str="<"+XML_TEXT+" "+XML_VALUE+"=\""+s_stttr+"\"/>";
        _stream.println(s_str);
        _stream.println("</"+XML_RESOURCE_INFO+">");
    }

    public void _loadFromXML(File _file,Element _element) throws IOException
    {
        NodeList p_images =  _element.getElementsByTagName(XML_TEXT);
        if (p_images.getLength() == 0) throw new IOException("<"+XML_TEXT+"> format error");
        Element p_image = (Element)p_images.item(0);
        String s_value = p_image.getAttribute(XML_VALUE);
        s_value = s_value.replace("(%nxt%)","\n");
        
        setText(s_value);
    }
}
