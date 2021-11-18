package com.igormaznitsa.MIDPTools.MenuTextPacker.GameMenuPacking;

import com.igormaznitsa.MIDPTools.MenuTextPacker.MenuPacking.StringReference;

import java.io.IOException;
import java.util.StringTokenizer;

public class MenuSubitem
{
    public static final int FLAG_NONE = 0;
    public static final int FLAG_ONENABLE = 1;
    public static final int FLAG_ONSTATE = 2;

    protected StringReference p_textString;
    protected String s_textID;
    protected int i_flags;

    protected int i_ID;

    public int getID()
    {
        return i_ID;
    }

    public void setID(int _id)
    {
        i_ID = _id;
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
            if ("ONENABLE".equals(s_str)) i_flag = FLAG_ONENABLE;
            else
            if ("ONSTATE".equals(s_str)) i_flag = FLAG_ONSTATE;
            else
            throw new IOException("Unsupported flag");

            i_flags |= i_flag;
        }
        return i_flags;
    }

    public StringReference getTextString()
    {
        return p_textString;
    }

    public String getTextID()
    {
        return s_textID;
    }

    public int getFlags()
    {
        return i_flags;
    }

    public MenuSubitem(String _textID,StringReference _textReference,int _flags)
    {
        s_textID = _textID;
        p_textString = _textReference;
        i_flags = _flags;
    }
}
