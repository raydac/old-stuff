package BusinessPlan;

import javax.microedition.lcdui.*;
import java.io.DataInputStream;
import java.io.IOException;

public class MenuBlock implements CommandListener, ItemCommandListener
{
    public interface Focusable
    {
        public boolean hasFocus();
    }

    private MenuActionListener MB_p_menuActionListener = null;

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


    private short[] MB_ash_menuArray;
    private short[] MB_ash_screenStack;
    private int MB_i_MenuStackPointer;
    private short[] MB_ash_itemIdArray;
    private short[] MB_ash_itemLinkScreen;
    private short[] MB_ash_commandDecode;

    protected Displayable MB_p_Form;

    private int MB_i_currentScreenFlags;
    public int MB_currentScreenId;
    private int MB_currentErrorScreen;
    private int MB_currentOkScreen;

    private int MB_i_lastListItemSelected;

    private Displayable MB_p_oldCanvas;
    private boolean MB_lg_menuActive;

    public boolean MB_isMenuActive()
    {
        return MB_lg_menuActive;
    }

    public int MB_getLastItemSelected()
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

            if (MB_p_menuActionListener != null)
            {
                if (_command.getCommandType() == Command.BACK)
                {
                    if (MB_back(true)) return;
                    return;
                }
                else
                if (_command.getCommandType() == Command.OK)
                {
                    int i_commandID = MB_ash_commandDecode[0];
                    MB_p_menuActionListener.processFormItem(MB_currentScreenId, i_index, i_commandID, _item);
                }
                else
                {
                    int i_commandID = MB_ash_commandDecode[_command.getPriority()];
                    MB_p_menuActionListener.processFormItem(MB_currentScreenId, i_index, i_commandID, _item);
                }
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
                        if (MB_p_menuActionListener != null)
                        {
                            MB_p_menuActionListener.processListItem(MB_currentScreenId, i_selectedItemIndex);
                        }
                    }
                    else
                    {
                        List p_list = (List) MB_p_Form;
                        int i_selectedItemIndex = p_list.getSelectedIndex();
                        int i_linkedScreen = MB_ash_itemLinkScreen[i_selectedItemIndex];
                        int i_itemId = MB_ash_itemIdArray[i_selectedItemIndex];
                        if (i_linkedScreen < 0)
                        {
                            if (MB_p_menuActionListener != null)
                            {
                                MB_p_menuActionListener.processListItem(MB_currentScreenId, i_itemId);
                            }
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
                    else
                    if (MB_p_Form instanceof Form)
                    {
                        Form p_f = (Form) MB_p_Form;
                        for(int li=0;li<p_f.size();li++)
                        {
                            Item p_itm = p_f.get(li);
                            if (p_itm instanceof Focusable)
                            {
                                if (((Focusable)p_itm).hasFocus()) i_selectedIndex = li;
                            }
                        }
                    }

                    if (MB_p_menuActionListener != null)
                    {
                        MB_p_menuActionListener.processCommand(MB_p_Form, MB_currentScreenId, i_cmndId, i_selectedIndex);
                    }
                }
                ;
                break;
        }
    }

    public void MB_initMenuBlock(String _menuResource) throws IOException
    {
        MB_p_menuActionListener = null;
        MB_lg_menuActive = false;
        MB_p_oldCanvas = null;

        DataInputStream p_dis = new DataInputStream(this.getClass().getResourceAsStream(_menuResource));
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
        MB_currentErrorScreen = -1;
        MB_currentOkScreen = -1;
        MB_i_currentScreenFlags = 0;
        MB_currentScreenId = -1;
    }

    public void MB_activateMenu(Displayable _background, int _startScreenMenu, MenuActionListener _menuListener)
    {
        MB_lg_menuActive = true;
        MB_p_oldCanvas = _background;
        MB_p_menuActionListener = _menuListener;
        MB_initScreen(_startScreenMenu, true);
    }

    public void MB_deactivateMenu()
    {
        MB_lg_menuActive = false;
        MB_clearScreenStack();
    }

    public void MB_reinitScreen(boolean _force, boolean _displayImmediately)
    {
        if (MB_i_MenuStackPointer < 0) return;

        if (MB_currentScreenId >= 0)
        {
            if (MB_p_Form != null && !_force)
            {
                if (MB_p_menuActionListener != null)
                    MB_p_menuActionListener.getDisplay().setCurrent(MB_p_Form);
            }
            else
            {
                MB_i_MenuStackPointer--;
                MB_initScreen(MB_currentScreenId, _displayImmediately);
            }
        }

        Runtime.getRuntime().gc();
    }

    public void MB_replaceCurrentScreen(int _newScreen, boolean _displayImmediate)
    {
        MB_initScreen(_newScreen, _displayImmediate);
        MB_ash_screenStack[--MB_i_MenuStackPointer] = (short) MB_currentScreenId;
    }

    public void MB_initScreen(int _screenPointer, boolean _displayImmediately)
    {
        int i_screenId = _screenPointer;
        MB_closeCurrentScreen();

        int i_temp = MB_ash_menuArray[_screenPointer++];
        int i_ScreenFlags = i_temp >>> 8;
        int i_CaptionId = i_temp & 0xFF;

        int i_itemTypesFlag = MB_ash_menuArray[_screenPointer++];

        String s_ScreenCaption = MB_p_menuActionListener != null ? MB_p_menuActionListener.getStringForIndex(i_CaptionId) : "";

        int i_ErrorScreen = MB_ash_menuArray[_screenPointer++];
        int i_OkScreen = MB_ash_menuArray[_screenPointer++];

        int i_ItemNumber = MB_ash_menuArray[_screenPointer++];
        boolean lg_isForm = false;

        if ((i_ScreenFlags & MB_SCREEN_FLAG_CUSTOMSCREEN) != 0)
        {
            if (MB_p_menuActionListener != null)
            {
                MB_p_Form = MB_p_menuActionListener.customScreen(i_screenId);

                if (MB_p_Form == null)
                {
                    if ((i_ScreenFlags & MB_SCREEN_FLAG_ONEXIT) != 0)
                    {
                        MB_currentScreenId = i_screenId;
                        MB_ash_screenStack[++MB_i_MenuStackPointer] = (short) MB_currentScreenId;

                        MB_currentErrorScreen = i_ErrorScreen;
                        MB_currentOkScreen = i_OkScreen;
                        MB_i_currentScreenFlags = i_ScreenFlags;

                        MB_p_menuActionListener.onExitScreen(null, i_screenId);
                    }
                    return;
                }
                else
                {
                    if (MB_p_Form instanceof List)
                    {
                        List p_list = (List) MB_p_Form;
                        p_list.setCommandListener(this);
                    }
                    else if (MB_p_Form instanceof Form)
                    {
                        lg_isForm = true;
                        Form p_form = (Form) MB_p_Form;
                        p_form.setCommandListener(this);

                        for (int li = 0; li < p_form.size(); li++)
                        {
                            Item p_itm = p_form.get(li);
                            if (p_itm instanceof Focusable) p_itm.setItemCommandListener(this);
                        }
                    }
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
                    if (MB_p_menuActionListener != null)
                    {
                        if (!MB_p_menuActionListener.enableItem(i_screenId, i_ItemId)) continue;
                    }
                }

                switch (i_ItemType)
                {
                    case MB_ITEM_CUSTOM:
                        {
                            if (MB_p_menuActionListener != null)
                            {
                                if (lg_isForm)
                                {
                                    Object p_custItem = MB_p_menuActionListener.customItem(i_screenId, i_ItemId, false);
                                    if (p_custItem instanceof String)
                                        ((Form) MB_p_Form).append((String) p_custItem);
                                    else
                                        ((Form) MB_p_Form).append((Item) p_custItem);
                                }
                                else
                                {
                                    String s_String = (String) MB_p_menuActionListener.customItem(i_screenId, i_ItemId, false);
                                    Image p_Image = (Image) MB_p_menuActionListener.customItem(i_screenId, i_ItemId, true);
                                    ((List) MB_p_Form).append(s_String, p_Image);
                                }
                            }
                        }
                        ;
                        break;
                    case MB_ITEM_IMAGE:
                        {
                            String s_string = null;
                            Image p_image = null;

                            if (i_StringId >= 0) s_string = MB_p_menuActionListener != null ? MB_p_menuActionListener.getStringForIndex(i_StringId) : "";
                            if (i_ImageId >= 0)
                                p_image = MB_p_menuActionListener != null ? MB_p_menuActionListener.getImageForIndex(i_ImageId) : null;

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
                    case MB_ITEM_DELIMITER:
                        {
                            Image p_Image = null;
                            p_Image = MB_p_menuActionListener != null ? MB_p_menuActionListener.getImageForIndex(i_ImageId) : null;
                            ((List) MB_p_Form).append("-------", p_Image);
                        }
                        ;
                        break;
                    case MB_ITEM_MENUITEM:
                        {
                            String s_String = MB_p_menuActionListener != null ? MB_p_menuActionListener.getStringForIndex(i_StringId) : "";
                            Image p_Image = null;
                            if (i_ImageId >= 0)
                                p_Image = MB_p_menuActionListener != null ? MB_p_menuActionListener.getImageForIndex(i_ImageId) : null;
                            ((List) MB_p_Form).append(s_String, p_Image);
                        }
                        ;
                        break;
                    case MB_ITEM_TEXTBOX:
                        {
                            String s_string = null;
                            if (i_StringId >= 0) s_string = MB_p_menuActionListener != null ? MB_p_menuActionListener.getStringForIndex(i_StringId) : "";
                            StringItem p_stringItem = new StringItem(null, s_string);
                            ((Form) MB_p_Form).append(p_stringItem);
                        }
                        ;
                        break;
                }
                MB_ash_itemLinkScreen[i_itemIndex] = (short) i_LinkScreen;
                MB_ash_itemIdArray[i_itemIndex++] = (short) i_ItemId;
            }
        }

        int i_commandNumber = MB_ash_menuArray[_screenPointer++];
        int i_cmndnum = 0;

        Command p_defaultCommand = null;

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
                if (MB_p_menuActionListener != null)
                {
                    if (!MB_p_menuActionListener.enableCommand(i_screenId, i_CmndId)) continue;
                }
            }

            String s_commandString = MB_p_menuActionListener != null ? MB_p_menuActionListener.getStringForIndex(i_StringId) : "";

            Command p_command = null;
            if (i_CommandType == MB_COMMAND_BACK)
                p_command = new Command(s_commandString, Command.BACK, i_cmndnum);
            if (i_CommandType == MB_COMMAND_USER)
                p_command = new Command(s_commandString, Command.ITEM, i_cmndnum);

            if (li == 0)
            {
                p_defaultCommand = p_command;
            }

            MB_ash_commandDecode[i_cmndnum++] = (short) i_CmndId;
            MB_p_Form.addCommand(p_command);
            if (li == 0) p_defaultCommand = p_command;
        }

        if (MB_p_Form instanceof Form)
        {
            Form p_frm = (Form) MB_p_Form;

            for (int li = 0; li < p_frm.size(); li++)
            {
                Item p_itm = p_frm.get(li);
                p_itm.setDefaultCommand(p_defaultCommand);
                p_itm.setItemCommandListener(this);
            }

            MB_p_Form.removeCommand(p_defaultCommand);
        }

        MB_currentScreenId = i_screenId;
        MB_ash_screenStack[++MB_i_MenuStackPointer] = (short) MB_currentScreenId;

        MB_currentErrorScreen = i_ErrorScreen;
        MB_currentOkScreen = i_OkScreen;
        MB_i_currentScreenFlags = i_ScreenFlags;

        if (MB_p_menuActionListener != null && _displayImmediately)
            MB_p_menuActionListener.getDisplay().setCurrent(MB_p_Form);

        MB_p_Form.setCommandListener(this);


        Runtime.getRuntime().gc();
    }

    public void MB_closeCurrentScreen()
    {
        if (MB_p_Form != null)
        {
            if ((MB_i_currentScreenFlags & MB_SCREEN_FLAG_ONEXIT) != 0)
            {
                if (MB_p_menuActionListener != null)
                    MB_p_menuActionListener.onExitScreen(MB_p_Form, MB_currentScreenId);
            }
            MB_p_Form.setCommandListener(null);
        }
    }


    public void MB_clearScreenStack()
    {
        MB_closeCurrentScreen();
        MB_i_MenuStackPointer = -1;

        if (MB_p_menuActionListener != null)
        {
            Display p_display = MB_p_menuActionListener.getDisplay();
            Displayable p_curCanvas = p_display.getCurrent();

            if (p_curCanvas == null || !p_curCanvas.equals(MB_p_oldCanvas))
            {
                p_display.setCurrent(MB_p_oldCanvas);
            }
        }
        Runtime.getRuntime().gc();
    }

    public void MB_clearScreenStack(int _screenID, boolean _displayImmediately)
    {
        MB_closeCurrentScreen();
        MB_i_MenuStackPointer = -1;
        MB_initScreen(_screenID, _displayImmediately);
    }

    public int MB_getPreviousScreenID()
    {
        int i_indx = MB_i_MenuStackPointer - 1;
        if (i_indx < 0)
            return -1;
        else
            return MB_ash_screenStack[i_indx];
    }

    public int MB_getIndexOfSelectedItem()
    {
        if (MB_p_Form != null)
        {
            if (MB_p_Form instanceof List)
            {
                List p_list = (List) MB_p_Form;
                return p_list.getSelectedIndex();
            }
            else
            if (MB_p_Form instanceof Form)
            {
                Form p_f = (Form) MB_p_Form;
                for(int li=0;li<p_f.size();li++)
                {
                    Item p_itm = p_f.get(li);
                    if (p_itm instanceof Focusable)
                    {
                        if (((Focusable)p_itm).hasFocus()) return li;
                    }
                }
            }
        }
        return -1;
    }

    public boolean MB_back(boolean _displayImmediately)
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

    public void MB_viewAlert(final String _caption, final String _text, final AlertType _type, final int _delay, boolean _blocked)
    {
        Display p_display = MB_p_menuActionListener.getDisplay();

        final Alert p_alert = new Alert(_caption, _text, null, _type);
        p_alert.setTimeout(_delay);

        p_display.setCurrent(p_alert, MB_p_Form);

        Thread p_thread = new Thread
                (new Runnable()
                {
                    public void run()
                    {
                        if (_delay != Alert.FOREVER)
                        {
                            try
                            {
                                Thread.sleep(_delay + 50);
                            }
                            catch (InterruptedException e)
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
                                catch (InterruptedException e)
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
                                catch (InterruptedException e)
                                {
                                    return;
                                }
                            }
                        }
                    }
                });

        p_thread.start();

        if (_blocked)
        {
            while (p_thread.isAlive())
            {
                try
                {
                    Thread.sleep(50);
                }
                catch (InterruptedException e)
                {
                    break;
                }
            }
        }
    }

    public boolean MB_back(int _depth, boolean _displayImmediately)
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
