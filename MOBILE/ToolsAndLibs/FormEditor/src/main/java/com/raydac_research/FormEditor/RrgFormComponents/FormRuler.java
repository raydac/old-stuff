package com.raydac_research.FormEditor.RrgFormComponents;

import org.w3c.dom.Element;

import java.io.PrintStream;
import java.io.IOException;

import com.raydac_research.FormEditor.RrgFormResources.ResourceContainer;

public class FormRuler
{
    public static final int TYPE_HORIZ = 0;
    public static final int TYPE_VERT = 1;

    protected int i_Coord;
    protected int i_Type;

    public static final String XML_RULER = "ruler";
    public static final String XML_TYPE = "type";
    public static final String XML_COORD = "coord";

    public int getType()
    {
        return i_Type;
    }

    public void setCoord(int _coord)
    {
        i_Coord = _coord;
    }

    public int getCoord()
    {
        return i_Coord;
    }

    public FormRuler(int _type,int _coord)
    {
        i_Type = _type;
        i_Coord = _coord;
    }

    public void saveAsXML(PrintStream _printStream)
    {
        if (_printStream==null) return;
        String s_str = "<"+XML_RULER+" "+XML_TYPE+"=\""+i_Type+"\" "+XML_COORD+"=\""+i_Coord+"\"/>";
        _printStream.println(s_str);
    }

    public void loadFromXML(Element _element) throws IOException
    {
        String s_Type = _element.getAttribute(XML_TYPE).trim();
        String s_Coord = _element.getAttribute(XML_COORD).trim();

        int i_t = 0;
        int i_c = 0;

        try
        {
            i_t = Integer.parseInt(s_Type);
            i_c = Integer.parseInt(s_Coord);

            if (i_t != TYPE_HORIZ && i_t != TYPE_VERT) throw new NumberFormatException();
        }
        catch (NumberFormatException e)
        {
            throw new IOException("Bad attribut in a ruler value");
        }

        i_Type = i_t;
        i_Coord = i_c;
    }

    public FormRuler getCopy()
    {
        return new FormRuler(i_Type,i_Coord);
    }
}
