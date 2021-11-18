package com.raydac_research.FormEditor.RrgFormResources;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.io.PrintStream;
import java.io.IOException;
import java.io.File;

import com.raydac_research.FormEditor.Misc.Utilities;

public abstract class AbstractRrgResource
{
    public static final String XML_RESOURCE = "resource";
    public static final String XML_RESOURCE_INFO = "resource_info";
    public static final String XML_ID = "id";
    public static final String XML_TYPE = "type";

    public static final int TYPE_TEXT = 0;
    public static final int TYPE_IMAGE = 1;
    public static final int TYPE_SOUND = 2;
    public static final int TYPE_FONT = 3;

    protected String s_ResourceID;
    protected int i_Type;
    protected String s_ResourceID_trans;
    protected int i_Type_trans;

    public void startTransaction()
    {
        s_ResourceID_trans = s_ResourceID;
        i_Type_trans = i_Type;
    }

    public void rollbackTransaction()
    {
        s_ResourceID = s_ResourceID_trans;
        i_Type = i_Type_trans;
    }

    public abstract boolean refreshResource() throws IOException;

    public AbstractRrgResource(String _id,int _type)
    {
        s_ResourceID = _id;
        i_Type = _type;
    }

    public final void saveAsXML(PrintStream _stream,File _file,boolean _relative)
    {
        if (_stream == null) return;
        String s_str = "<"+XML_RESOURCE+" "+XML_ID+"=\""+getResourceID()+"\" "+XML_TYPE+"=\""+getType()+"\">";
        _stream.println(s_str);

        _saveAsXML(_stream,_file,_relative);

        _stream.println("</"+XML_RESOURCE+">");
    }

    public void loadFromXML(File _file,Element _element) throws IOException
    {
        String s_id = _element.getAttribute(XML_ID).trim();
        String s_type = _element.getAttribute(XML_TYPE).trim();

        s_ResourceID = s_id;

        try
        {
            i_Type = Integer.parseInt(s_type);
        }
        catch (NumberFormatException e)
        {
            throw new IOException("Error type value for "+s_ResourceID+" resource");
        }

        Element p_resource_info = _getElement(_element,XML_RESOURCE_INFO);

        if (p_resource_info==null) throw new IOException("I can't find the info for "+s_id+" resource");

        _loadFromXML(_file,_element);
    }

    public abstract void _saveAsXML(PrintStream _stream,File _file,boolean _relative);
    public abstract void _loadFromXML(File _file,Element _element) throws IOException;

    protected Element _getElement(Element _element,String _name)
    {
        NodeList p_list = _element.getElementsByTagName(_name);
        if (p_list.getLength() == 0) return null;
        return (Element) p_list.item(0);
    }

    public int getType()
    {
        return i_Type;
    }

    public String getResourceID()
    {
        return s_ResourceID;
    }

    public void setResourceID(String _ID)
    {
        s_ResourceID = _ID;
    }

    public String toString()
    {
        return s_ResourceID;
    }

}
