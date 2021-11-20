package com.igormaznitsa.midp;

import javax.microedition.lcdui.*;
import java.io.IOException;
import java.io.DataInputStream;

/*
 * Copyright (C) 2003 Igor A. Maznitsa (igor.maznitsa@igormaznitsa.com).  All rights reserved.
 * Software is confidential and copyrighted. Title to Software and all associated intellectual property rights
 * is retained by Igor A. Maznitsa. You may not make copies of Software, other than a single copy of Software for
 * archival purposes.  Unless enforcement is prohibited by applicable law, you may not modify, decompile, or reverse
 * engineer Software.  You acknowledge that Software is not designed, licensed or intended for use in the design,
 * construction, operation or maintenance of any nuclear facility. Igor A. Maznitsa disclaims any express or implied
 * warranty of fitness for such uses.
 */

public abstract class MenuBlock implements CommandListener
{
    // Screen
    private static final int SCREEN_FLAG_NONE = 0;
    private static final int SCREEN_FLAG_ONEXIT = 1;
    private static final int SCREEN_FLAG_CUSTOMSCREEN = 2;

    // Item flag
    private static final int ITEM_FLAG_NONE = 0;
    private static final int ITEM_FLAG_OPTIONAL = 1;

    // Item
    private static final int ITEM_MENUITEM = 1;
    private static final int ITEM_TEXTBOX = 2;
    private static final int ITEM_IMAGE = 4;
    private static final int ITEM_CUSTOM = 8;
    private static final int ITEM_DELIMITER = 16;

    private static final int ALIGN_CENTER = 0;
    private static final int ALIGN_LEFT = 1;
    private static final int ALIGN_RIGHT = 2;

    // Command
    private static final int COMMAND_FLAG_STATIC = 0;
    private static final int COMMAND_FLAG_OPTIONAL = 1;
    private static final int COMMAND_BACK = 0;
    private static final int COMMAND_USER = 1;

    private static final int MAX_ITEMS_NUMBER_PER_SCREEN = 32;
    private static final int MAX_COMMANDS_NUMBER_PER_SCREEN = 16;

    protected Display p_Display;
    private short[] ash_menuArray;
    private short[] ash_screenStack;
    private int i_MenuStackPointer;
    private short[] ash_itemIdArray;
    private short[] ash_itemLinkScreen;
    private short[] ash_commandDecode;

    protected Displayable p_Form;
    protected LanguageBlock p_languageBlock;
    protected ImageBlock p_ImageBlock;

    private int i_currentScreenFlags;
    public int currentScreenId;
    public int currentErrorScreen;
    public int currentOkScreen;

    private Displayable p_oldCanvas;

    public boolean lg_Active;

    public synchronized final void commandAction(Command _command, Displayable _displayable)
    {
        int i_type = _command.getCommandType();
        int i_cmndId = ash_commandDecode[_command.getPriority()];
        switch (i_type)
        {
            case Command.BACK:
                {
                    // Processing of a back command
                    back();
                    return;
                }
            case Command.SCREEN:
                {
                    // Processing of a list item selecting
                    List p_list = (List) p_Form;
                    int i_selectedItemIndex = p_list.getSelectedIndex();
                    int i_linkedScreen = ash_itemLinkScreen[i_selectedItemIndex];
                    int i_itemId = ash_itemIdArray[i_selectedItemIndex];
                    if (i_linkedScreen < 0)
                    {
                        processListItem(currentScreenId, i_itemId);
                    }
                    else
                    {
                        initScreen(i_linkedScreen);
                    }
                }
                ;
                break;
            case Command.ITEM:
                {
                    // Processing of an user command
                    processCommand(currentScreenId, i_cmndId);
                }
                ;
                break;
        }
    }

    public void setBackground(Displayable _displayable)
    {
        p_oldCanvas = _displayable;
    }

    public MenuBlock(Display _display, String _menuResource, LanguageBlock _languageBlock, ImageBlock _imageBlock) throws IOException
    {
        lg_Active = true;
        p_oldCanvas = _display.getCurrent();
        p_ImageBlock = _imageBlock;
        p_Display = _display;
        p_languageBlock = _languageBlock;
        DataInputStream p_dis = new DataInputStream(this.getClass().getResourceAsStream(_menuResource));
        int i_SummaryLen = p_dis.readUnsignedShort();


        ash_menuArray = new short[i_SummaryLen];
        for (int li = 0; li < i_SummaryLen; li++) ash_menuArray[li] = p_dis.readShort();

        p_dis.close();
        p_dis = null;

        ash_screenStack = new short[10];
        i_MenuStackPointer = -1;
        ash_itemIdArray = new short[MAX_ITEMS_NUMBER_PER_SCREEN];
        ash_itemLinkScreen = new short[MAX_ITEMS_NUMBER_PER_SCREEN];
        ash_commandDecode = new short[MAX_COMMANDS_NUMBER_PER_SCREEN];

        currentScreenId = -1;
        currentErrorScreen = -1;
        currentOkScreen = -1;
        i_currentScreenFlags = 0;
        currentScreenId = -1;
    }

    public void deactivateMenu()
    {
        lg_Active = false;
        clearScreenStack();
    }

    /**
     * Reiniting current screen if it is presented
     */
    public void reinitScreen()
    {
        if (i_MenuStackPointer < 0) return;

        if (currentScreenId >= 0)
        {
            if (p_Form != null)
            {
                p_Display.setCurrent(p_Form);
            }
            else
            {
                i_MenuStackPointer--;
                initScreen(currentScreenId);
            }
        }

        Runtime.getRuntime().gc();
    }

    /**
     * Initing a screen with the offset in the common byte block
     * @param _screenPointer
     */
    public void initScreen(int _screenPointer)
    {
        int i_screenId = _screenPointer;
        closeCurrentScreen();

        // Reading common info for the screen
        int i_temp = ash_menuArray[_screenPointer++];
        int i_ScreenFlags = i_temp >>> 8;
        int i_CaptionId = i_temp & 0xFF;

        int i_itemTypesFlag = ash_menuArray[_screenPointer++];

        String s_ScreenCaption = p_languageBlock.getStringArray()[i_CaptionId];

        int i_ErrorScreen = ash_menuArray[_screenPointer++];
        int i_OkScreen = ash_menuArray[_screenPointer++];

        // Reading item number
        int i_ItemNumber = ash_menuArray[_screenPointer++];
        boolean lg_isForm = false;

        if ((i_itemTypesFlag & ITEM_MENUITEM) != 0)
        {
            // Item list screen
            p_Form = new List(s_ScreenCaption, List.IMPLICIT);
        }
        else
        {
            // Form screen
            p_Form = new Form(s_ScreenCaption);
            lg_isForm = true;
        }

        if ((i_ScreenFlags & SCREEN_FLAG_CUSTOMSCREEN) != 0)
        {
            // Filling the screen by a customer
            customScreen(i_screenId, p_Form);
        }
        else
        {

            // Automaticaly filling the screen
            int i_itemIndex = 0;
            for (int li = 0; li < i_ItemNumber; li++)
            {
                // Reading item


                //Reading ID and Aligning
                int i_tmp = ash_menuArray[_screenPointer++];
                int i_ItemId = i_tmp >>> 8;
                int i_Align = i_tmp & 0xFF;

                // Reading Type and Flags
                i_tmp = ash_menuArray[_screenPointer++];
                int i_ItemType = i_tmp >>> 8;
                int i_ItemFlags = i_tmp & 0xFF;

                int i_StringId = ash_menuArray[_screenPointer++];
                int i_ImageId = ash_menuArray[_screenPointer++];
                int i_LinkScreen = ash_menuArray[_screenPointer++];

                if ((i_ItemFlags & ITEM_FLAG_OPTIONAL) != 0)
                {
                    if (!enableItem(i_screenId, i_ItemId)) continue;
                }

                switch (i_ItemType)
                {
                    case ITEM_CUSTOM:
                        {
                            // Processing a custom item
                            if (lg_isForm)
                            {
                                Item p_Item = (Item) customItem(i_screenId, i_ItemId, false);
                                ((Form) p_Form).append(p_Item);
                            }
                            else
                            {
                                String s_String = (String) customItem(i_screenId, i_ItemId, false);
                                Image p_Image = (Image) customItem(i_screenId, i_ItemId, true);
                                ((List) p_Form).append(s_String, p_Image);
                            }
                        }
                        ;
                        break;
                    case ITEM_IMAGE:
                        {
                            // Processing an image item
                            String s_string = null;
                            Image p_image = null;

                            if (i_StringId >= 0) s_string = p_languageBlock.getStringArray()[i_StringId];
                            if (i_ImageId >= 0) p_image = p_ImageBlock.getImageForID(i_ImageId, false);

                            switch (i_Align)
                            {
                                case ALIGN_CENTER:
                                    i_Align = ImageItem.LAYOUT_CENTER;
                                    break;
                                case ALIGN_LEFT:
                                    i_Align = ImageItem.LAYOUT_LEFT;
                                    break;
                                default :
                                    i_Align = ImageItem.LAYOUT_RIGHT;
                            }

                            ImageItem p_imageItem = new ImageItem(s_string, p_image, i_Align, null);

                            ((Form) p_Form).append(p_imageItem);
                        }
                        ;
                        break;
                    case ITEM_DELIMITER:
                        {
                            // Processing a menu delimiter
                            Image p_Image = null;
                            if (p_ImageBlock != null)
                                p_Image = p_ImageBlock.getImageForID(i_ImageId, false);
                            ((List) p_Form).append("-------", p_Image);
                        }
                    case ITEM_MENUITEM:
                        {
                            // Processing a menu item
                            String s_String = p_languageBlock.getStringArray()[i_StringId];
                            Image p_Image = null;
                            if (p_ImageBlock != null)
                                p_Image = p_ImageBlock.getImageForID(i_ImageId, false);
                            ((List) p_Form).append(s_String, p_Image);
                        }
                        ;
                        break;
                    case ITEM_TEXTBOX:
                        {
                            // Processing a textbox item
                            String s_string = null;
                            if (i_StringId >= 0) s_string = p_languageBlock.getStringArray()[i_StringId];
                            StringItem p_stringItem = new StringItem(null, s_string);
                            ((Form) p_Form).append(p_stringItem);
                        }
                        ;
                        break;
                }
                ash_itemLinkScreen[i_itemIndex] = (short) i_LinkScreen;
                ash_itemIdArray[i_itemIndex++] = (short) i_ItemId;
            }

            // Processing screen's command
            int i_commandNumber = ash_menuArray[_screenPointer++];
            int i_cmndnum = 0;
            for (int li = 0; li < i_commandNumber; li++)
            {
                int i_CommandFlags = ash_menuArray[_screenPointer++];
                int i_CommandLink = ash_menuArray[_screenPointer++];
                int i_CmndId = i_CommandLink;


                i_temp = ash_menuArray[i_CommandLink];
                int i_CommandType = i_temp >>> 8;
                int i_StringId = i_temp & 0xFF;


                if ((i_CommandFlags & COMMAND_FLAG_OPTIONAL) != 0)
                {
                    if (!enableCommand(i_screenId, i_CmndId)) continue;
                }

                String s_commandString = p_languageBlock.getStringArray()[i_StringId];

                Command p_command = null;
                if (i_CommandType == COMMAND_BACK)
                    p_command = new Command(s_commandString, Command.BACK, i_cmndnum);
                if (i_CommandType == COMMAND_USER)
                    p_command = new Command(s_commandString, Command.ITEM, i_cmndnum);

                ash_commandDecode[i_cmndnum++] = (short) i_CmndId;

                p_Form.addCommand(p_command);
            }
        }

        currentScreenId = i_screenId;
        ash_screenStack[++i_MenuStackPointer] = (short) currentScreenId;

        currentErrorScreen = i_ErrorScreen;
        currentOkScreen = i_OkScreen;
        i_currentScreenFlags = i_ScreenFlags;

        p_Form.setCommandListener(this);

        p_Display.setCurrent(p_Form);

        Runtime.getRuntime().gc();

    }

    private void closeCurrentScreen()
    {
        if (p_Form != null)
        {
            if ((i_currentScreenFlags & SCREEN_FLAG_ONEXIT) != 0) onExitScreen(p_Form, currentScreenId);
            p_Form.setCommandListener(null);
            p_Form = null;
        }
    }

    /**
     * Clearing the screen stack and removing current screen
     */
    public void clearScreenStack()
    {
        closeCurrentScreen();
        i_MenuStackPointer = -1;
        p_Display.setCurrent(p_oldCanvas);

        Runtime.getRuntime().gc();
    }

    /**
     * Out the Ok screen for the current screen if it is presented
     */
    public void viewOkScreen()
    {
        if (currentOkScreen < 0) return;
        initScreen(currentOkScreen);
    }

    /**
     * Out the Error screen for the current screen if it is presented
     */
    public void viewErrorScreen()
    {
        if (currentErrorScreen < 0) return;
        initScreen(currentErrorScreen);
    }

    /**
     * Close current screen and return to previous menu in the menu stack
     * @return true if the menu stack is empty else return false
     */
    public boolean back()
    {
        if (i_MenuStackPointer < 0)
            return true;
        else if (i_MenuStackPointer == 0)
        {
            p_Display.setCurrent(p_oldCanvas);

            i_MenuStackPointer = -1;
            return true;
        }
        else
        {
            closeCurrentScreen();
            int i_prevScreen = ash_screenStack[--i_MenuStackPointer];
            initScreen(i_prevScreen);
            i_MenuStackPointer--;
            return false;
        }
    }

    /**
     * Processing a command for an item
     * @param _screenId  the ID of current screen
     * @param _itemId the ID of the item
     */
    public abstract void processListItem(int _screenId, int _itemId);

    /**
     * Process a command by the user
     * @param _screenId  the ID of current screen
     * @param _commandId the id of a command
     */
    public abstract void processCommand(int _screenId, int _commandId);

    /**
     * Allowing to add command in the command list
     * @param _screenId  the ID of current screen
     * @param _commandId
     * @return
     */
    public abstract boolean enableCommand(int _screenId, int _commandId);

    /**
     * Fill a custom screen by the user
     * @param _screenId  the ID of current screen
     * @param _screen a displayable object as the screen
     */
    public abstract void customScreen(int _screenId, Displayable _screen);

    /**
     * Create a custom item by the user
     * @param _screenId  the ID of current screen
     * @param _itemId the ID of the item
     * @param _getImage true if it is waited an Image object for List screen and false if it is waited a String object
     * @return new item as an Item object (for Form) or String and Image objects (for List)
     */
    public abstract Object customItem(int _screenId, int _itemId, boolean _getImage);

    /**
     * Allowing output of an optional item
     * @param _screenId  the ID of current screen
     * @param _itemId the ID of the item
     * @return true if the item is allowed for the screen or else if it is not allowed
     */
    public abstract boolean enableItem(int _screenId, int _itemId);

    /**
     * Processing an exit event for a screen
     * @param _screen a displayable object reflects the screen
     * @param _screenId the screen ID of the screen
     */
    public abstract void onExitScreen(Displayable _screen, int _screenId);
}
