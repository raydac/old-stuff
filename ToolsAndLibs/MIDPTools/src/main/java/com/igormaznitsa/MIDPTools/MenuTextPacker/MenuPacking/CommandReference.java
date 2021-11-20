package com.igormaznitsa.MIDPTools.MenuTextPacker.MenuPacking;

import com.igormaznitsa.MIDPTools.MenuTextPacker.MenuPacking.StringReference;

class CommandReference
    {
        public static final int COMMAND_BACK = 0;
        public static final int COMMAND_USER = 1;

        public String s_CommandId;
        public int i_type;
        public StringReference p_StringId;
        public int i_offset;

        public CommandReference(String _id, int _type, StringReference _stringId)
        {
            s_CommandId = _id;
            i_type = _type;
            p_StringId = _stringId;
            i_offset = -1;
        }
    }