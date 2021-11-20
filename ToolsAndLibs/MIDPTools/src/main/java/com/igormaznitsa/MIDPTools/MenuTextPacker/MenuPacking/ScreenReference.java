package com.igormaznitsa.MIDPTools.MenuTextPacker.MenuPacking;

import java.util.Vector;

class ScreenReference extends Vector
    {
        static final int FLAG_NONE = 0;
        static final int FLAG_ONEXIT = 1;
        static final int FLAG_CUSTOMSCREEN = 2;

        public String s_ScreenId;
        public int i_Flags;
        public String s_ErrorScreenId;
        public String s_OkScreenId;

        public StringReference p_CaptionString;
        public ScreenReference p_ErrorScreen;
        public ScreenReference p_OkScreen;

        public Vector p_CommandVector;

        public int i_Offset;
        public int i_usedItemsFlag;

        public ScreenReference(String _screenId, StringReference _captionId, int _flags, String _errorScreenId, String _okScreenId)
        {
            super();
            p_CommandVector = new Vector();
            s_ScreenId = _screenId;
            i_Flags = _flags;
            s_ErrorScreenId = _errorScreenId;
            s_OkScreenId = _okScreenId;
            p_ErrorScreen = null;
            p_OkScreen = null;
            i_Offset = -1;
            p_CaptionString = _captionId;
            i_usedItemsFlag = 0;
        }
    }