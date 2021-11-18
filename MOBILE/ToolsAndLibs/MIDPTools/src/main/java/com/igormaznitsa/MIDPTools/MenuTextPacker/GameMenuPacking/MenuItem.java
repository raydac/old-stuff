package com.igormaznitsa.MIDPTools.MenuTextPacker.GameMenuPacking;

import com.igormaznitsa.MIDPTools.MenuTextPacker.MenuPacking.StringReference;

import java.util.Vector;
import java.util.StringTokenizer;
import java.io.IOException;

public class MenuItem
        {
    public static final int FLAG_NONE = 0;
    public static final int FLAG_ONENTER = 1;
    public static final int FLAG_ONEXIT = 2;
    public static final int FLAG_OPTIONAL = 4;
    public static final int FLAG_CUSTOM = 8;

    public static final int TYPE_BUTTON = 0;
    public static final int TYPE_BUTTONLIST = 1;
    public static final int TYPE_RADIOLIST = 2;
    public static final int TYPE_CHECKLIST = 3;
    public static final int TYPE_TEXT = 4;

    protected int i_Flags;
    protected int i_Offset;
    protected int i_Type;
    protected String s_TextID;
    protected StringReference p_TextString;

    protected int i_ID;

    protected Vector p_SubItems;

    public void setOffset(int _offset)
    {
        i_Offset = _offset;
    }

    public int getOffset()
    {
        return i_Offset;
    }

    public int getID()
    {
        return i_ID;
    }

    public void setID(int _id)
    {
        i_ID = _id;
    }

    public static int convertStringType(String _type) throws IOException
    {
        if ("BUTTON".equals(_type)) return TYPE_BUTTON;
        if ("BUTTONLIST".equals(_type)) return TYPE_BUTTONLIST;
        if ("RADIOLIST".equals(_type)) return TYPE_RADIOLIST;
        if ("CHECKLIST".equals(_type)) return TYPE_CHECKLIST;
        if ("TEXT".equals(_type)) return TYPE_TEXT;
        throw new IOException("Unsupported item type \'"+_type+"\'");
    }

    public static int convertStringFlags(String _flags) throws IOException
    {
        StringTokenizer p_strTokenizer = new StringTokenizer(_flags, "|");
        int i_flags = 0;
        while (p_strTokenizer.hasMoreTokens())
        {
            String s_str = p_strTokenizer.nextToken().trim().toUpperCase();
            int i_flag = 0;

            if ("NONE".equals(s_str)) i_flag = FLAG_NONE;
            else
            if ("ONENTER".equals(s_str)) i_flag = FLAG_ONENTER;
            else
            if ("ONEXIT".equals(s_str)) i_flag = FLAG_ONEXIT;
            else
            if ("OPTIONAL".equals(s_str)) i_flag = FLAG_OPTIONAL;
            else
            if ("CUSTOM".equals(s_str)) i_flag = FLAG_CUSTOM;
            else
            throw new IOException("Unsupported flag");

            i_flags |= i_flag;
        }
        return i_flags;
    }

    public int getFlags()
    {
        return i_Flags;
}

    public Vector getSubitems()
    {
        return p_SubItems;
    }

    public int getType()
    {
        return i_Type;
}

    public String getTextID()
    {
        return s_TextID;
}

    public StringReference getTextString()
    {
        return p_TextString;
}

    public void addSubItem(MenuSubitem _subitem)
    {
        p_SubItems.add(_subitem);
}

    public boolean containsSubitemForID(String _subitemID)
    {
        for (int li = 0; li < p_SubItems.size(); li++)
        {
            MenuSubitem p_subitem = (MenuSubitem) p_SubItems.elementAt(li);
            if (p_subitem.getTextID().equals(_subitemID)) return true;
        }
        return false;
}

    public MenuItem(String _textId, StringReference _textString, int _flags, int _type)
    {
        i_Flags = _flags;
        s_TextID = _textId;
        i_Type = _type;
        p_TextString = _textString;

        p_SubItems = new Vector();
}
}
