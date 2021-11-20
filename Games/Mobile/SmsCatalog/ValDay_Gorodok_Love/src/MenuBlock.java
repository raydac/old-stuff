import javax.microedition.lcdui.*;
import java.io.DataInputStream;
import java.io.IOException;

public class MenuBlock implements CommandListener
{
    private static final MenuBlock p_this = new MenuBlock();

    private static final int MB_SCREEN_FLAG_NONE = 0;
    private static final int MB_SCREEN_FLAG_ONEXIT = 1;
    private static final int MB_SCREEN_FLAG_CUSTOMSCREEN = 2;

    private static final int MB_ITEM_FLAG_NONE = 0;
    private static final int MB_ITEM_FLAG_OPTIONAL = 1;

    private static final int MB_ITEM_MENUITEM = 1;
    private static final int MB_ITEM_TEXTBOX = 2;
    private static final int MB_ITEM_IMAGE = 4;
    private static final int MB_ITEM_CUSTOM = 8;
    private static final int MB_ITEM_DELIMITER = 16;

    private static final int MB_ALIGN_CENTER = 0;
    private static final int MB_ALIGN_LEFT = 1;
    private static final int MB_ALIGN_RIGHT = 2;

    private static final int MB_COMMAND_FLAG_STATIC = 0;
    private static final int MB_COMMAND_FLAG_OPTIONAL = 1;
    private static final int MB_COMMAND_BACK = 0;
    private static final int MB_COMMAND_USER = 1;

    private static final int MB_MAX_ITEMS_NUMBER_PER_SCREEN = 32;
    private static final int MB_MAX_COMMANDS_NUMBER_PER_SCREEN = 16;

    private static short[] MB_ash_menuArray;
    private static short[] MB_ash_screenStack;
    private static int MB_i_MenuStackPointer;
    private static short[] MB_ash_itemIdArray;
    private static short[] MB_ash_itemLinkScreen;
    private static short[] MB_ash_commandDecode;

    protected static Displayable MB_p_Form;

    private static int MB_i_currentScreenFlags;
    public static int MB_currentScreenId;

    private static int MB_i_lastListItemSelected;

    private static Displayable MB_p_oldCanvas;
    private static boolean MB_lg_menuActive;

    public static boolean MB_isMenuActive()
    {
        return MB_lg_menuActive;
    }

    public static int MB_getLastItemSelected()
    {
        return MB_i_lastListItemSelected;
    }

    public synchronized final void commandAction(Command _command, Item _item)
    {
        if (MB_p_Form != null && MB_p_Form instanceof Form)
        {
            Form p_frm = (Form) MB_p_Form;
            int i_index = -1;
            for (int li = 0; li < p_frm.size(); li++)
            {
                if (_item.equals(p_frm.get(li)))
                {
                    i_index = li;
                    break;
                }
            }

            if (_command.getCommandType() == Command.BACK)
            {
                if (MB_back(true)) return;
            }
            else
            if (_command.getCommandType() == Command.OK)
            {
                int i_commandID = MB_ash_commandDecode[0];
                Main.processFormItem(MB_currentScreenId, i_index, i_commandID, _item);
            }
            else
            {
                int i_commandID = MB_ash_commandDecode[_command.getPriority()];
                Main.processFormItem(MB_currentScreenId, i_index, i_commandID, _item);
            }
        }
    }

    public synchronized final void commandAction(Command _command, Displayable _displayable)
    {
        int i_type = _command.getCommandType();
        int i_cmndId = MB_ash_commandDecode[_command.getPriority()];
        switch (i_type)
        {
            case Command.BACK:
            {
                if (MB_back(true)) return;
                return;
            }
            case Command.SCREEN:
            {
                if ((MB_i_currentScreenFlags & MB_SCREEN_FLAG_CUSTOMSCREEN) != 0)
                {
                    List p_list = (List) MB_p_Form;
                    int i_selectedItemIndex = p_list.getSelectedIndex();
                    Main.processListItem(MB_currentScreenId, i_selectedItemIndex);
                }
                else
                {
                    List p_list = (List) MB_p_Form;
                    int i_selectedItemIndex = p_list.getSelectedIndex();
                    int i_linkedScreen = MB_ash_itemLinkScreen[i_selectedItemIndex];
                    int i_itemId = MB_ash_itemIdArray[i_selectedItemIndex];
                    if (i_linkedScreen < 0)
                    {
                        Main.processListItem(MB_currentScreenId, i_itemId);
                    }
                    else
                    {
                        MB_i_lastListItemSelected = i_itemId;
                        MB_initScreen(i_linkedScreen, true);
                    }
                }
            }
            ;
            break;
            case Command.OK:
            case Command.ITEM:
            {
                int i_selectedIndex = -1;
                if (MB_p_Form instanceof List)
                {
                    i_selectedIndex = ((List) MB_p_Form).getSelectedIndex();
                }
                else if (MB_p_Form instanceof Form)
                {
                    Form p_f = (Form) MB_p_Form;
                    for (int li = 0; li < p_f.size(); li++)
                    {
                        Item p_itm = p_f.get(li);
                    }
                }

                Main.processCommand(MB_p_Form, MB_currentScreenId, i_cmndId, i_selectedIndex);
            }
            ;
            break;
        }
    }

    public static void MB_initMenuBlock(String _menuResource) throws IOException
    {
        MB_lg_menuActive = false;
        MB_p_oldCanvas = null;

        DataInputStream p_dis = new DataInputStream(p_this.getClass().getResourceAsStream(_menuResource));
        int i_SummaryLen = p_dis.readUnsignedShort();
        MB_ash_menuArray = new short[i_SummaryLen];
        for (int li = 0; li < i_SummaryLen; li++) MB_ash_menuArray[li] = p_dis.readShort();
        p_dis.close();
        p_dis = null;

        MB_ash_screenStack = new short[20];
        MB_i_MenuStackPointer = -1;
        MB_ash_itemIdArray = new short[MB_MAX_ITEMS_NUMBER_PER_SCREEN];
        MB_ash_itemLinkScreen = new short[MB_MAX_ITEMS_NUMBER_PER_SCREEN];
        MB_ash_commandDecode = new short[MB_MAX_COMMANDS_NUMBER_PER_SCREEN];

        MB_currentScreenId = -1;
        MB_i_currentScreenFlags = 0;
        MB_currentScreenId = -1;
    }

    public static void MB_activateMenu(Displayable _background, int _startScreenMenu)
    {
        MB_lg_menuActive = true;
        MB_p_oldCanvas = _background;
        MB_initScreen(_startScreenMenu, true);
    }

    public static void MB_deactivateMenu()
    {
        MB_lg_menuActive = false;
        MB_clearScreenStack();
    }

    public static void MB_reinitScreen(boolean _force, boolean _displayImmediately)
    {
        if (MB_i_MenuStackPointer < 0) return;

        if (MB_currentScreenId >= 0)
        {
            if (MB_p_Form != null && !_force)
            {
                Display p_display = Main.p_CurrentDisplay;
                p_display.setCurrent(MB_p_Form);
            }
            else
            {
                MB_i_MenuStackPointer--;
                MB_initScreen(MB_currentScreenId, _displayImmediately);
            }
        }

        Runtime.getRuntime().gc();
    }

    public static void MB_replaceCurrentScreen(int _newScreen, boolean _displayImmediate)
    {
        MB_initScreen(_newScreen, _displayImmediate);
        MB_ash_screenStack[--MB_i_MenuStackPointer] = (short) MB_currentScreenId;
    }

    public static void MB_initScreen(int _screenPointer, boolean _displayImmediately)
    {
        int i_screenId = _screenPointer;
        MB_closeCurrentScreen();

        int i_temp = MB_ash_menuArray[_screenPointer++];
        int i_ScreenFlags = i_temp >>> 8;
        int i_CaptionId = i_temp & 0xFF;

        int i_itemTypesFlag = MB_ash_menuArray[_screenPointer++];

        String s_ScreenCaption = Main.getStringForIndex(i_CaptionId);

        int i_ErrorScreen = MB_ash_menuArray[_screenPointer++];
        int i_OkScreen = MB_ash_menuArray[_screenPointer++];

        int i_ItemNumber = MB_ash_menuArray[_screenPointer++];
        boolean lg_isForm = false;

        if ((i_ScreenFlags & MB_SCREEN_FLAG_CUSTOMSCREEN) != 0)
        {
            MB_p_Form = Main.customScreen(i_screenId);

            if (MB_p_Form == null)
            {
                if ((i_ScreenFlags & MB_SCREEN_FLAG_ONEXIT) != 0)
                {
                    MB_currentScreenId = i_screenId;
                    MB_ash_screenStack[++MB_i_MenuStackPointer] = (short) MB_currentScreenId;

                    MB_i_currentScreenFlags = i_ScreenFlags;

                    Main.onExitScreen(null, i_screenId);
                }
                return;
            }
            else
            {
                if (MB_p_Form instanceof List)
                {
                    List p_list = (List) MB_p_Form;
                    p_list.setCommandListener(p_this);
                }
                else if (MB_p_Form instanceof Form)
                {
                    lg_isForm = true;
                    Form p_form = (Form) MB_p_Form;
                    p_form.setCommandListener(p_this);

                }
            }
        }
        else
        {
            if ((i_itemTypesFlag & MB_ITEM_MENUITEM) != 0)
            {
                MB_p_Form = new List(s_ScreenCaption, List.IMPLICIT);
            }
            else
            {
                MB_p_Form = new Form(s_ScreenCaption);
                lg_isForm = true;
            }

            int i_itemIndex = 0;
            for (int li = 0; li < i_ItemNumber; li++)
            {

                int i_tmp = MB_ash_menuArray[_screenPointer++];
                int i_ItemId = i_tmp >>> 8;
                int i_Align = i_tmp & 0xFF;

                i_tmp = MB_ash_menuArray[_screenPointer++];
                int i_ItemType = i_tmp >>> 8;
                int i_ItemFlags = i_tmp & 0xFF;

                int i_StringId = MB_ash_menuArray[_screenPointer++];
                int i_ImageId = MB_ash_menuArray[_screenPointer++];
                int i_LinkScreen = MB_ash_menuArray[_screenPointer++];

                if ((i_ItemFlags & MB_ITEM_FLAG_OPTIONAL) != 0)
                {
                    if (!Main.enableItem(i_screenId, i_ItemId)) continue;
                }

                switch (i_ItemType)
                {
                    //#if _MENU_ITEM_CUSTOM
                    case MB_ITEM_CUSTOM:
                    {
                        if (lg_isForm)
                        {
                            Object p_custItem = Main.customItem(i_screenId, i_ItemId, false);
                            if (p_custItem instanceof String)
                                ((Form) MB_p_Form).append((String) p_custItem);
                            else
                                ((Form) MB_p_Form).append((Item) p_custItem);
                        }
                        else
                        {
                            String s_String = (String) Main.customItem(i_screenId, i_ItemId, false);
                            Image p_Image = (Image) Main.customItem(i_screenId, i_ItemId, true);
                            ((List) MB_p_Form).append(s_String, p_Image);
                        }
                    }
                    ;
                    break;
                    //#endif
                    //#if _MENU_ITEM_IMAGE
                    case MB_ITEM_IMAGE:
                    {
                        String s_string = null;
                        Image p_image = null;

                        if (i_StringId >= 0) s_string = Main.getStringForIndex(i_StringId);
                        //if (i_ImageId >= 0)
                        //    p_image = Main.getImageForIndex(i_ImageId);

                        switch (i_Align)
                        {
                            case MB_ALIGN_CENTER:
                                i_Align = ImageItem.LAYOUT_CENTER;
                                break;
                            case MB_ALIGN_LEFT:
                                i_Align = ImageItem.LAYOUT_LEFT;
                                break;
                            default :
                                i_Align = ImageItem.LAYOUT_RIGHT;
                        }

                        ImageItem p_imageItem = new ImageItem(s_string, p_image, i_Align, null);

                        ((Form) MB_p_Form).append(p_imageItem);
                    }
                    ;
                    break;
                    //#endif
                    //#if _MENU_ITEM_DELIMITER
                    case MB_ITEM_DELIMITER:
                    {
                        //Image p_Image = null;
                        //p_Image = Main.getImageForIndex(i_ImageId);
                        ((List) MB_p_Form).append("-------", null);
                    }
                    ;
                    break;
                    //#endif
                    //#if _MENU_ITEM_MENUITEM
                    case MB_ITEM_MENUITEM:
                    {
                        String s_String = Main.getStringForIndex(i_StringId);
                        ((List) MB_p_Form).append(s_String, null);
                    }
                    ;
                    break;
                    //#endif
                    //#if _MENU_ITEM_TEXTBOX
                    case MB_ITEM_TEXTBOX:
                    {
                        String s_string = null;
                        if (i_StringId >= 0) s_string = Main.getStringForIndex(i_StringId);
                        StringItem p_stringItem = new StringItem(null, s_string);
                        ((Form) MB_p_Form).append(p_stringItem);
                    }
                    ;
                    break;
                    //#endif
                }
                MB_ash_itemLinkScreen[i_itemIndex] = (short) i_LinkScreen;
                MB_ash_itemIdArray[i_itemIndex++] = (short) i_ItemId;
            }
        }

        int i_commandNumber = MB_ash_menuArray[_screenPointer++];
        int i_cmndnum = 0;

        for (int li = 0; li < i_commandNumber; li++)
        {
            int i_CommandFlags = MB_ash_menuArray[_screenPointer++];
            int i_CommandLink = MB_ash_menuArray[_screenPointer++];
            int i_CmndId = i_CommandLink;


            i_temp = MB_ash_menuArray[i_CommandLink];
            int i_CommandType = i_temp >>> 8;
            int i_StringId = i_temp & 0xFF;


            if ((i_CommandFlags & MB_COMMAND_FLAG_OPTIONAL) != 0)
            {
                if (!Main.enableCommand(i_screenId, i_CmndId)) continue;
            }

            String s_commandString = Main.getStringForIndex(i_StringId);

            Command p_command = null;
            if (i_CommandType == MB_COMMAND_BACK)
                p_command = new Command(s_commandString, Command.BACK, i_cmndnum);
            if (i_CommandType == MB_COMMAND_USER)
                p_command = new Command(s_commandString, Command.ITEM, i_cmndnum);

            MB_ash_commandDecode[i_cmndnum++] = (short) i_CmndId;
            MB_p_Form.addCommand(p_command);
        }


        MB_currentScreenId = i_screenId;
        MB_ash_screenStack[++MB_i_MenuStackPointer] = (short) MB_currentScreenId;

        MB_i_currentScreenFlags = i_ScreenFlags;

        MB_p_Form.setCommandListener(p_this);

        if (_displayImmediately)
        {
            Main.p_CurrentDisplay.setCurrent(MB_p_Form);
            try
            {
                Thread.sleep(50);
            }
            catch (Exception e)
            {
            }
        }

        Runtime.getRuntime().gc();
    }

    public static void MB_closeCurrentScreen()
    {
        if (MB_p_Form != null)
        {
            if ((MB_i_currentScreenFlags & MB_SCREEN_FLAG_ONEXIT) != 0)
            {
                Main.onExitScreen(MB_p_Form, MB_currentScreenId);
            }
            MB_p_Form.setCommandListener(null);
        }
    }


    public static void MB_clearScreenStack()
    {
        MB_closeCurrentScreen();
        MB_i_MenuStackPointer = -1;

        Display p_display = Main.p_CurrentDisplay;
        Displayable p_curCanvas = p_display.getCurrent();

        if (p_curCanvas == null || !p_curCanvas.equals(MB_p_oldCanvas))
        {
            p_display.setCurrent(MB_p_oldCanvas);
        }
        Runtime.getRuntime().gc();
    }

    public static void MB_clearScreenStack(int _screenID, boolean _displayImmediately)
    {
        MB_closeCurrentScreen();
        MB_i_MenuStackPointer = -1;
        MB_initScreen(_screenID, _displayImmediately);
    }

    public static int MB_getPreviousScreenID()
    {
        int i_indx = MB_i_MenuStackPointer - 1;
        if (i_indx < 0)
            return -1;
        else
            return MB_ash_screenStack[i_indx];
    }

    public static int MB_getIndexOfSelectedItem()
    {
        if (MB_p_Form != null)
        {
            if (MB_p_Form instanceof List)
            {
                List p_list = (List) MB_p_Form;
                return p_list.getSelectedIndex();
            }
            else if (MB_p_Form instanceof Form)
            {
                Form p_f = (Form) MB_p_Form;
                for (int li = 0; li < p_f.size(); li++)
                {
                    Item p_itm = p_f.get(li);
                }
            }
        }
        return -1;
    }

    public static boolean MB_back(boolean _displayImmediately)
    {
        if (MB_i_MenuStackPointer < 0)
            return true;
        else if (MB_i_MenuStackPointer == 0)
        {
            MB_clearScreenStack();
            return true;
        }
        else
        {
            MB_closeCurrentScreen();
            int i_prevScreen = MB_ash_screenStack[--MB_i_MenuStackPointer];
            MB_initScreen(i_prevScreen, _displayImmediately);
            MB_i_MenuStackPointer--;
            return false;
        }
    }

    public static void MB_viewAlert(final String _caption, final String _text,final Image _icon,final AlertType _type, final int _delay, boolean _blocked)
    {
        Display p_display = Main.p_CurrentDisplay;

        final Alert p_alert = new Alert(_caption, _text, _icon, _type);
        p_alert.setTimeout(_delay);

        p_display.setCurrent(p_alert, MB_p_Form);

        if (_delay != Alert.FOREVER)
        {
            try
            {
                Thread.sleep(_delay + 50);
            }
            catch (Throwable e)
            {
            }
        }
        else
        {
            while (!p_alert.isShown())
            {
                try
                {
                    Thread.sleep(30);
                }
                catch (Throwable e)
                {
                    return;
                }
            }
            while (p_alert.isShown())
            {
                try
                {
                    Thread.sleep(30);
                }
                catch (Throwable e)
                {
                    return;
                }
            }
        }

        try
        {
            Thread.sleep(100);
        }
        catch (Throwable  e)
        {
        }
    }

    public static boolean MB_back(int _depth, boolean _displayImmediately)
    {
        int i_newsp = MB_i_MenuStackPointer - _depth;
        if (i_newsp < 0)
        {
            MB_clearScreenStack();
            return true;
        }
        else
        {
            MB_closeCurrentScreen();
            int i_prevScreen = MB_ash_screenStack[i_newsp];
            MB_i_MenuStackPointer = i_newsp;
            MB_initScreen(i_prevScreen, _displayImmediately);
            MB_i_MenuStackPointer--;
            return false;
        }
    }
}
