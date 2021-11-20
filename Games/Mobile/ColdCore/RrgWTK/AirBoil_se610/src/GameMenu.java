
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Font;
import java.io.InputStream;
import java.io.DataInputStream;

public class GameMenu
{
    //---------COLOR MENU SCHEME----------------

    //#if (VENDOR=="SIEMENS" && MODEL=="M50") || (VENDOR=="NOKIA" && MODEL=="3410")
    //$protected static final int COLOR_BOX_OUTLINE = 0xFFFFFF;
    //$protected static final int COLOR_BOX_CHEKED = 0xFFFFFF;
    //$protected static final int COLOR_RADIO_CHEKED = 0xFFFFFF;

    //$protected static final int COLOR_ITEM_BACKGROUND = 0x000000;
    //$protected static final int COLOR_ITEM_NONSELECTED = 0xFFFFFF;
    //$protected static final int COLOR_ITEM_SELECTED = 0xFFFFFF;
    //$protected static final int COLOR_SUBITEM_BACKGROUND = 0xFFFFFF;
    //$protected static final int COLOR_SUBITEM_TEXT = 0x000000;
    //#else
    /**
     * Цвет заднего фона для полоски пунктов меню
     */
    protected static final int COLOR_ITEM_BACKGROUND = 0x0000CC;
    /**
     * Цвет текста невыбранного пункта
     */
    protected static final int COLOR_ITEM_NONSELECTED = 0xFFFFFF;
    /**
     * Цвет текста выбранного пункта
     */
    protected static final int COLOR_ITEM_SELECTED = 0xE5FF00;

    /**
     * Цвет границы бокса
     */
    protected static final int COLOR_BOX_OUTLINE = 0xFFBA00;
    /**
     * Цвет контента бокса
     */
    protected static final int COLOR_BOX_CHEKED = 0x8AE200;
    /**
     * Цвет контента радиокнопки
     */
    protected static final int COLOR_RADIO_CHEKED = 0x18C800;


    //#if VENDOR=="SAMSUNG"
    //$protected static final int COLOR_SUBITEM_BACKGROUND = 0xFF7900;
    //$protected static final int COLOR_SUBITEM_TEXT = 0x002FCE;
    //#else
    /**
     * Цвет заднего фона подпунктов
     */
    protected static final int COLOR_SUBITEM_BACKGROUND = 0x0079D8;
    /**
     * Цвет текста подпункта
     */
    protected static final int COLOR_SUBITEM_TEXT = 0xFFFFFF;
    //#endif
    //#endif

    private static final int ITEM_TYPE_BUTTON = 0;
    private static final int ITEM_TYPE_BUTTONLIST = 1;
    private static final int ITEM_TYPE_RADIOLIST = 2;
    private static final int ITEM_TYPE_CHECKLIST = 3;
    private static final int ITEM_TYPE_TEXT = 4;

    private static final int ITEM_FLAGS_NONE = 0;
    private static final int ITEM_FLAGS_ONENTER = 1;
    private static final int ITEM_FLAGS_ONEXIT = 2;
    private static final int ITEM_FLAGS_OPTIONAL = 4;
    private static final int ITEM_FLAGS_CUSTOM = 8;

    private static final int SUBITEM_FLAGS_NONE = 0;
    private static final int SUBITEM_FLAGS_ONENABLE = 1;
    private static final int SUBITEM_FLAGS_ONSTATE = 2;

    public static final int MENUKEY_SELECT = 0;
    public static final int MENUKEY_LEFT = 1;
    public static final int MENUKEY_RIGHT = 2;
    public static final int MENUKEY_UP = 3;
    public static final int MENUKEY_DOWN = 4;

    public static final int OFFSET_SELECTED = 0;
    public static final int OFFSET_FLAGS = 8;
    public static final int OFFSET_TEXT = 16;
    public static final int OFFSET_ID = 24;

    private static byte[] ab_menuArray;

    private static final int MAIN_ITEM_OFFSET = 3;
    private static final int MAX_SUBITEM = 10;

    private static int i_CurrentItemIndex;
    private static int i_CurrentItemTextID;
    private static int i_CurrentItemID;
    private static int i_SubitemsNumber;
    private static int i_CurrentItemType;
    private static final int[] SUBITEMS = new int[MAX_SUBITEM];

    private static int i_PressedKey;
    private static int i_ReleasedKey;
    private static boolean lg_Pressed;
    private static int i_CurrentFocusedSubitem;

    public static Font p_MenuFont;
    private static int i_SymbolSize;
    private static int i_MaxSubitemTextSize;

    public static final void initMenuBlock(Class _loader, String _menuResource, Font _menuFont) throws Exception
    {
        InputStream p_is = _loader.getClass().getResourceAsStream(_menuResource);
        DataInputStream p_ds = new DataInputStream(p_is);
        int i_fileid = p_ds.readInt();
        if (i_fileid != 0x45328300) throw new Exception();
        int i_dataSize = p_ds.readUnsignedShort();
        ab_menuArray = new byte[i_dataSize];
        if (p_ds.read(ab_menuArray) != i_dataSize) throw new Exception();
        i_CurrentItemIndex = -1;

        p_MenuFont = _menuFont;
        if (p_MenuFont != null)
        {
            i_SymbolSize = p_MenuFont.getHeight();
        }
        else
        {
            i_SymbolSize = LangBlock.i_FontImage_CharHeight;
        }

        int i_index = 0;
        initItem(i_index++);

        i_PressedKey = -1;
    }

    public static final void releaseMenuBlock()
    {
        ab_menuArray = null;
    }

    public static final void nextItem()
    {
        boolean lg_selected = false;
        int i_menuItemsNumber = MENU_OFFSETS.length;
        for (int li = i_CurrentItemIndex + 1; li < i_menuItemsNumber; li++)
        {
            int i_id = ab_menuArray[MENU_OFFSETS[li]];
            if (startup.onEnable(i_id, -1))
            {
                initItem(li);
                lg_selected = true;
                break;
            }
        }
        if (lg_selected) return;
        for (int li = 0; li < i_CurrentItemIndex; li++)
        {
            int i_id = ab_menuArray[MENU_OFFSETS[li]];
            if (startup.onEnable(i_id, -1))
            {
                initItem(li);
                break;
            }
        }
    }

    public static final void prevItem()
    {
        boolean lg_selected = false;
        int i_menuItemsNumber = MENU_OFFSETS.length;
        for (int li = i_CurrentItemIndex - 1; li >= 0; li--)
        {
            int i_id = ab_menuArray[MENU_OFFSETS[li]];
            if (startup.onEnable(i_id, -1))
            {
                initItem(li);
                lg_selected = true;
                break;
            }
        }
        if (lg_selected) return;
        for (int li = i_menuItemsNumber - 1; li > i_CurrentItemIndex; li--)
        {
            int i_id = ab_menuArray[MENU_OFFSETS[li]];
            if (startup.onEnable(i_id, -1))
            {
                initItem(li);
                break;
            }
        }
    }

    public static final void focusToItem(int _itemID)
    {
        for (int li = 0; li < MENU_OFFSETS.length; li++)
        {
            int i_id = ab_menuArray[MENU_OFFSETS[li]];
            if (i_id == _itemID)
            {
                initItem(li);
            }
        }
    }

    private static final void initItem(int _itemIndex)
    {
        lg_Pressed = false;
        i_MaxSubitemTextSize = 0;
        i_CurrentFocusedSubitem = 0;

        if (i_CurrentItemIndex >= 0)
        {
            int i_curOffset = MENU_OFFSETS[i_CurrentItemIndex];
            int i_id = ab_menuArray[i_curOffset] & 0xFF;
            i_curOffset += 2;
            int i_flag = ab_menuArray[i_curOffset] & 0xFF;
            if ((i_flag & ITEM_FLAGS_ONEXIT) != 0) startup.onExit(i_id);
        }

        int i_curOffset = MENU_OFFSETS[_itemIndex];
        int i_id = ab_menuArray[i_curOffset++] & 0xFF;
        int i_textID = ab_menuArray[i_curOffset++] & 0xFF;
        int i_typeID = ab_menuArray[i_curOffset++] & 0xFF;
        int i_flag = ab_menuArray[i_curOffset++] & 0xFF;

        int i_subitemsNumber = ab_menuArray[i_curOffset++] & 0xFF;

        i_SubitemsNumber = 0;

        if ((i_flag & ITEM_FLAGS_CUSTOM) == 0)
        {
            // Нормальный пункт
            for (int li = 0; li < i_subitemsNumber; li++)
            {
                int i_sid = ab_menuArray[i_curOffset++] & 0xFF;
                int i_txtId = ab_menuArray[i_curOffset++] & 0xFF;
                int i_sflags = ab_menuArray[i_curOffset++] & 0xFF;

                if ((i_sflags & SUBITEM_FLAGS_ONENABLE) != 0)
                {
                    if (!startup.onEnable(i_id, i_sid)) continue;
                }

                int i_selected = 0;

                switch (i_typeID)
                {
                    case ITEM_TYPE_CHECKLIST:
                    case ITEM_TYPE_RADIOLIST:
                        {
                            i_selected = startup.isSelected(i_id, i_sid) ? 0xFF : 0;
                        }
                        ;break;
                }

                //#if VENDOR=="SAMSUNG"
                //$int i_stringSize = LangBlock.getStringWidth(i_txtId);
                //#else
                final String s_String = LangBlock.getStringForIndex(i_txtId);
                int i_stringSize = p_MenuFont.stringWidth(s_String);
                //#endif
                if (i_MaxSubitemTextSize < i_stringSize) i_MaxSubitemTextSize = i_stringSize;

                int i_packedSubitem = (i_sid << 24) | (i_txtId << 16) | (i_sflags << 8) | i_selected;

                SUBITEMS[i_SubitemsNumber++] = i_packedSubitem;
            }
        }
        else
        {
            // Настраиваемый пункт
            if (i_typeID == ITEM_TYPE_CHECKLIST || i_typeID == ITEM_TYPE_RADIOLIST)
            {
                int i_subitem = 0;
                for (i_subitem = 0; i_subitem < MAX_SUBITEM; i_subitem++)
                {
                    int i_subitemPacked = startup.onCustom(i_id, i_subitem);

                    if (i_subitemPacked == 0) break;

                    SUBITEMS[i_subitem] = i_subitemPacked;

                    //#if VENDOR=="SAMSUNG"
                    //$int i_stringSize = LangBlock.getStringWidth((i_subitemPacked >>> 16) & 0xFF);
                    //#else
                    final String s_String = LangBlock.getStringForIndex((i_subitemPacked >>> 16) & 0xFF);
                    int i_stringSize = p_MenuFont.stringWidth(s_String);
                    //#endif
                    if (i_MaxSubitemTextSize < i_stringSize) i_MaxSubitemTextSize = i_stringSize;
                }
                i_SubitemsNumber = i_subitem;
            }
        }

        if ((i_flag & ITEM_FLAGS_ONENTER) != 0)
        {
            startup.onEnter(i_id);
        }

        i_CurrentItemTextID = i_textID;
        i_CurrentItemType = i_typeID;
        i_CurrentItemIndex = _itemIndex;
        i_CurrentItemID = i_id;
    }

    public static final void pressMenuKey(int _key)
    {
        i_PressedKey = _key;
        i_ReleasedKey = -1;
    }

    public static final boolean processMenu()
    {
        if (i_PressedKey != i_ReleasedKey) return false;
        i_PressedKey = -1;
        switch (i_ReleasedKey)
        {
            case MENUKEY_UP:
                {
                    if (!lg_Pressed) break;
                    i_CurrentFocusedSubitem++;
                    if (i_CurrentFocusedSubitem >= i_SubitemsNumber) i_CurrentFocusedSubitem = 0;
                }
        ;
                break;
            case MENUKEY_DOWN:
                {
                    if (!lg_Pressed) break;
                    i_CurrentFocusedSubitem--;
                    if (i_CurrentFocusedSubitem < 0) i_CurrentFocusedSubitem = i_SubitemsNumber - 1;
                }
        ;
                break;
            case MENUKEY_LEFT:
                {
                    prevItem();
                }
        ;
                break;
            case MENUKEY_RIGHT:
                {
                    nextItem();
                }
        ;
                break;
            case MENUKEY_SELECT:
                {
                    if (i_CurrentItemType == ITEM_TYPE_BUTTON)
                    {
                        startup.onState(i_CurrentItemID, -1, true);
                    }
                    else
                    {
                        if (!lg_Pressed)
                        {
                            lg_Pressed = true;
                        }
                        else
                        {
                            int i_id = SUBITEMS[i_CurrentFocusedSubitem];
                            boolean lg_selected = (i_id & 0xFF) != 0;
                            i_id = (i_id >>> 24) & 0xFF;

                            switch (i_CurrentItemType)
                            {
                                case ITEM_TYPE_BUTTONLIST:
                                    {
                                        startup.onState(i_CurrentItemID, i_id, true);
                                    }
                            ;
                                    break;
                                case ITEM_TYPE_RADIOLIST:
                                    {
                                        if (lg_selected) break;
                                        for (int li = 0; li < i_SubitemsNumber; li++)
                                        {
                                            if (li == i_CurrentFocusedSubitem) continue;
                                            int i_subitem = SUBITEMS[li];
                                            startup.onState(i_CurrentItemID, (i_subitem >>> 24) & 0xFF, false);
                                            SUBITEMS[li] &= 0xFFFFFF00;
                                        }
                                        startup.onState(i_CurrentItemID, (SUBITEMS[i_CurrentFocusedSubitem] >>> 24) & 0xFF, true);
                                    }
                            ;
                                    break;
                                case ITEM_TYPE_CHECKLIST:
                                    {
                                        lg_selected = !lg_selected;
                                        startup.onState(i_CurrentItemID, i_id, lg_selected);
                                        if (lg_selected)
                                            SUBITEMS[i_CurrentFocusedSubitem] |= 0xFF;
                                        else
                                            SUBITEMS[i_CurrentFocusedSubitem] &= 0xFFFFFF00;
                                    }
                            ;
                                    break;

                            }
                        }
                    }
                }
        ;
                break;
        }
        i_ReleasedKey = -1;
        return true;
    }

    public static final void releaseMenuKey(int _key)
    {
        i_ReleasedKey = _key;
    }

    private static final int BOX_WIDTH = 5;
    private static final int BOX_HEIGHT = 5;

    private static final int BORDER_WIDTH = 2;
    private static final int SUBITEM_INTERVAL = 2;

    private static final void drawCheckbox(Graphics _g, int _x, int _y, boolean _selected,boolean _focused)
    {
        //#if (VENDOR=="SIEMENS" && MODEL=="M50") || (VENDOR=="NOKIA" && MODEL=="3410")
        //$if (!_focused) _g.setColor(~COLOR_BOX_OUTLINE); else
        //#endif
        _g.setColor(COLOR_BOX_OUTLINE);
        _g.drawRect(_x, _y, BOX_WIDTH, BOX_HEIGHT);
        if (_selected)
        {
            //#if (VENDOR=="SIEMENS" && MODEL=="M50") || (VENDOR=="NOKIA" && MODEL=="3410")
            //$if (!_focused) _g.setColor(~COLOR_BOX_CHEKED); else
            //#endif
            _g.setColor(COLOR_BOX_CHEKED);

            for (int li = 0; li < 3; li++)
            {
                _g.drawLine(_x - 2, _y + 1 + li, _x + (BOX_WIDTH >> 1), _y + BOX_HEIGHT + 2 + li);
                _g.drawLine(_x + (BOX_WIDTH >> 1), _y + BOX_HEIGHT + 2 + li, _x + BOX_WIDTH + 1, _y - 2 + li);
            }
        }
    }

    public static final void paintMenu(Graphics _graphics, int _offsetX, int _offsetY, int _screenWidth, int _screenHeight)
    {
        if (ab_menuArray == null || i_CurrentItemIndex < 0) return;

        if (p_MenuFont != null) _graphics.setFont(p_MenuFont);

        final int TEXT_ALGIN = Graphics.TOP | Graphics.LEFT;

        int i_areaX = _offsetX;
        int i_areaY = _offsetY;
        int i_areaWidth = _screenWidth;
        int i_areaHeight = _screenHeight;

        // ================== Обычное текстовое меню ========================
        //#if VENDOR=="SAMSUNG"

        //$int i_leftWidth = LangBlock.i_FontImage_CharWidth;
        //$int i_rightWidth = i_leftWidth;

        //$int i_itemX = i_areaX + ((i_areaWidth - LangBlock.getStringWidth(i_CurrentItemTextID)) >> 1);
        //$int i_itemY = i_areaY + i_areaHeight - i_SymbolSize - BORDER_WIDTH;

        //#else

        //#if (VENDOR=="NOKIA" && MODEL=="7650") || VENDOR=="SUN" || VENDOR=="MOTOROLA" || VENDOR == "SE" || (VENDOR=="SIEMENS" && MODEL=="CX65")
        final String LEFT_ARROW = "<<<";
        final String RIGHT_ARROW = ">>>";
        //#endif

        //#if (VENDOR=="SIEMENS" && (MODEL=="M55" || MODEL=="M50" || MODEL=="S55" || MODEL=="C65")) || (VENDOR=="NOKIA" && (MODEL=="6100" || MODEL=="3410" || MODEL=="3510")) || VENDOR=="LG"
        //$final String LEFT_ARROW = "<<";
        //$final String RIGHT_ARROW = ">>";
        //#endif

        int i_leftWidth = p_MenuFont.stringWidth(LEFT_ARROW);
        int i_rightWidth = p_MenuFont.stringWidth(RIGHT_ARROW);

        final String s_itemText = LangBlock.getStringForIndex(i_CurrentItemTextID);

        int i_itemX = i_areaX + ((i_areaWidth - p_MenuFont.stringWidth(s_itemText)) >> 1);
        int i_itemY = i_areaY + i_areaHeight - i_SymbolSize - BORDER_WIDTH;

        //#endif

        // Отрисовываем фоновую линию для пунктов
        _graphics.setColor(COLOR_ITEM_BACKGROUND);
        int i_lineHeight = (BORDER_WIDTH << 1) + i_SymbolSize;
        int i_lineY = i_areaY + i_areaHeight - i_lineHeight;
        _graphics.fillRect(i_areaX, i_lineY, i_areaWidth, i_lineHeight);

        int i_subItemBlockWidth = 0;

        // Отрисовываем подложку под подпункты
        if (lg_Pressed)
        {
            switch (i_CurrentItemType)
            {
                case ITEM_TYPE_BUTTONLIST:
                case ITEM_TYPE_CHECKLIST:
                case ITEM_TYPE_RADIOLIST:
                    {
                        int i_xoffset = i_CurrentItemType == ITEM_TYPE_BUTTONLIST ? i_itemX - BORDER_WIDTH : i_itemX - (BORDER_WIDTH << 1) - BOX_WIDTH;
                        int i_bckHeight = i_SubitemsNumber * i_SymbolSize + ((i_SubitemsNumber - 1) * SUBITEM_INTERVAL + (BORDER_WIDTH << 1));
                        i_subItemBlockWidth = i_MaxSubitemTextSize + (BORDER_WIDTH << 1) + (i_CurrentItemType == ITEM_TYPE_BUTTONLIST ? 0 : BORDER_WIDTH + BOX_WIDTH);
                        _graphics.setColor(COLOR_SUBITEM_BACKGROUND);
                        _graphics.fillRect(i_xoffset, i_lineY - i_bckHeight, i_subItemBlockWidth, i_bckHeight);

                        // Выбранный пункт
                        int i_yOffset = BORDER_WIDTH + (i_CurrentFocusedSubitem * (i_SymbolSize + SUBITEM_INTERVAL)) + i_SymbolSize;
                        _graphics.setColor(COLOR_SUBITEM_TEXT);
                        _graphics.fillRect(i_xoffset, i_lineY - i_yOffset - 1, i_subItemBlockWidth, i_SymbolSize + 2);
                    }
            ;
                    break;
            }
            _graphics.setColor(COLOR_ITEM_SELECTED);
        }
        else
            _graphics.setColor(COLOR_ITEM_NONSELECTED);


        // Отрисовываем пункт
        //#if VENDOR=="SAMSUNG"
        //$LangBlock.drawStringForIndex(i_CurrentItemTextID,_graphics,i_itemX, i_itemY);
        //#else
        _graphics.drawString(s_itemText, i_itemX, i_itemY, TEXT_ALGIN);
        //#endif

        // Отрисовка указателей
        //#if VENDOR=="SAMSUNG"
        //$final int ARROW_LEFT_CHAR = (8<<4)|12;
        //$final int ARROW_RIGHT_CHAR = (8<<4)|13;

        //$final int HORZ_OFFST = 2;
        //${
        //$    final int i_aX = i_areaX+HORZ_OFFST;
        //$    final int i_aY = i_PressedKey == MENUKEY_LEFT ? i_itemY+1 : i_itemY;
        //$    LangBlock.drawChar(_graphics,ARROW_LEFT_CHAR,i_aX,i_aY);
        //$}

        //${
        //$    final int i_aX = i_areaX + i_areaWidth - i_rightWidth-HORZ_OFFST;
        //$    final int i_aY = i_PressedKey == MENUKEY_RIGHT ? i_itemY+1 : i_itemY;
        //$    LangBlock.drawChar(_graphics,ARROW_RIGHT_CHAR,i_aX,i_aY);
        //$}
        //#else
        if (i_PressedKey == MENUKEY_LEFT)
            _graphics.setColor(COLOR_ITEM_SELECTED);
        else
            _graphics.setColor(COLOR_ITEM_NONSELECTED);

        _graphics.drawString(LEFT_ARROW, i_areaX, i_itemY, TEXT_ALGIN);

        if (i_PressedKey == MENUKEY_RIGHT)
            _graphics.setColor(COLOR_ITEM_SELECTED);
        else
            _graphics.setColor(COLOR_ITEM_NONSELECTED);

        _graphics.drawString(RIGHT_ARROW, i_areaX + i_areaWidth - i_rightWidth, i_itemY, TEXT_ALGIN);
        //#endif


        switch (i_CurrentItemType)
        {
            case ITEM_TYPE_BUTTON:
                break;
            case ITEM_TYPE_RADIOLIST:
            case ITEM_TYPE_BUTTONLIST:
            case ITEM_TYPE_CHECKLIST:
                {
                    if (lg_Pressed)
                    {
                        i_itemY = i_itemY - i_SymbolSize - SUBITEM_INTERVAL - BORDER_WIDTH;

                        for (int li = 0; li < i_SubitemsNumber; li++)
                        {
                            boolean lg_focused = i_CurrentFocusedSubitem == li;

                            int i_subitemPacked = SUBITEMS[li];

                            boolean lg_selected = (i_subitemPacked & 0xFF) != 0;
                            i_subitemPacked >>>= 8;
                            int i_flags = i_subitemPacked & 0xFF;
                            i_subitemPacked >>>= 8;
                            int i_txtId = i_subitemPacked & 0xFF;
                            i_subitemPacked >>>= 8;
                            int i_sId = i_subitemPacked & 0xFF;

                            //#if VENDOR=="SAMSUNG"
                            //$LangBlock.drawStringForIndex(i_txtId,_graphics,i_itemX,i_itemY);
                            //#else

                            String s_text = LangBlock.getStringForIndex(i_txtId);
                            if (lg_focused)
                            {
                                _graphics.setColor(COLOR_SUBITEM_BACKGROUND);
                            }
                            else
                            {
                                _graphics.setColor(COLOR_SUBITEM_TEXT);
                            }

                            //#if VENDOR=="LG"
                            //$_graphics.drawString(s_text, i_itemX+1, i_itemY, TEXT_ALGIN);
                            //#else
                            _graphics.drawString(s_text, i_itemX, i_itemY, TEXT_ALGIN);
                            //#endif

                            //#endif

                            switch (i_CurrentItemType)
                            {
                                    //#if VENDOR=="SAMSUNG"
                                    //$case ITEM_TYPE_CHECKLIST :
                                    //$case ITEM_TYPE_RADIOLIST :
                                    //$    {
                                    //$        final int CHAR_FLAG_OFF = (8<<4)|14;
                                    //$        final int CHAR_FLAG_ON = (8<<4)|15;
                                    //$        int i_char = lg_selected ? CHAR_FLAG_ON : CHAR_FLAG_OFF;
                                    //$        LangBlock.drawChar(_graphics,i_char,i_itemX-LangBlock.i_FontImage_CharWidth-BORDER_WIDTH,i_itemY);
                                    //$    };break;
                                    //#else
                                    case ITEM_TYPE_RADIOLIST:
                                    case ITEM_TYPE_CHECKLIST:
                                    {
                                        drawCheckbox(_graphics, i_itemX - BORDER_WIDTH - BOX_WIDTH, i_itemY + ((i_SymbolSize - BOX_HEIGHT) >> 1), lg_selected,lg_focused);
                                    }
                            ;
                                    break;
                                    //#endif
                            }

                            i_itemY -= (i_SymbolSize + SUBITEM_INTERVAL);
                        }
                    }
                }
        ;
                break;
            case ITEM_TYPE_TEXT:
                {
                    if (lg_Pressed)
                    {

                    }
                }
        ;
                break;
        }

    }

    //-------------------------------------------------------------
    // Offset array
    private static final short[] MENU_OFFSETS = new short[]{(short) 3, (short) 17, (short) 22, (short) 27, (short) 32, (short) 37, (short) 51, (short) 56, (short) 61, (short) 66};
}
