package com.igormaznitsa.MIDPTools.MenuTextPacker.MenuPacking;

public class CommandLink
{
    public static final int FLAG_STATIC = 0;
    public static final int FLAG_OPTIONAL = 1;

    CommandReference p_CommandReference;
    int i_flag;

    public CommandLink(CommandReference _reference,int _flags)
    {
        p_CommandReference = _reference;
        i_flag = _flags;
    }
}
