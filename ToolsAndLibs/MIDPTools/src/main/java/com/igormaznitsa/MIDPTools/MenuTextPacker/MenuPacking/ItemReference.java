package com.igormaznitsa.MIDPTools.MenuTextPacker.MenuPacking;

    class ItemReference
    {
        static final int FLAG_NONE = 0;
        static final int FLAG_OPTIONAL = 1;

        static final int ALIGN_CENTER = 0;
        static final int ALIGN_LEFT = 1;
        static final int ALIGN_RIGHT = 2;

        static final int ITEM_MENUITEM = 1;
        static final int ITEM_TEXTBOX = 2;
        static final int ITEM_IMAGE = 4;
        static final int ITEM_CUSTOM = 8;
        static final int ITEM_DELIMITER = 16;

        int i_Type;
        StringReference p_StringId;
        int i_ImageId;
        String s_LinkScreenId;
        String s_ItemAbsId;
        int i_Flags;
        int i_id;
        int i_align;

        ScreenReference p_LinkScreen;

        public ItemReference(int _type, String _itemId, StringReference _stringId, String _linkScreenID, int _imageId, int _flags,int _id,int _align)
        {
            i_id = _id;
            i_Flags = _flags;
            i_ImageId = _imageId;
            s_LinkScreenId = _linkScreenID;
            s_ItemAbsId = _itemId;
            i_Type = _type;
            p_StringId = _stringId;
        }
    }