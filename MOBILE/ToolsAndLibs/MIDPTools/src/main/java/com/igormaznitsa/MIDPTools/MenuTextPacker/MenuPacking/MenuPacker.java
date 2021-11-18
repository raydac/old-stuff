package com.igormaznitsa.MIDPTools.MenuTextPacker.MenuPacking;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import java.util.*;
import java.io.*;

import com.igormaznitsa.MIDPTools.MenuTextPacker.MenuPacking.ItemReference;
import com.igormaznitsa.MIDPTools.MenuTextPacker.MenuPacking.CommandLink;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;

public class MenuPacker
{
    public static HashMap p_allItems = new HashMap();
    public static HashMap p_allScreens = new HashMap();
    public static HashMap p_allCommands = new HashMap();
    public static HashSet p_allStringsId = new HashSet();

    private static int i_usedFlags;

    public static void packMenu(String _menufile, HashMap _stringtable) throws Exception
    {
        i_usedFlags = 0;
        String s_MenuFileName = _menufile;

        DocumentBuilderFactory p_dbf = DocumentBuilderFactory.newInstance();
        p_dbf.setIgnoringComments(true);
        DocumentBuilder p_db = p_dbf.newDocumentBuilder();

        Document p_doc = null;
        try
        {
            File p_file = new File(s_MenuFileName);
            System.out.println("XML menu descriptor is " + p_file.getAbsolutePath());
            p_doc = p_db.parse(p_file);
        }
        catch (SAXException e)
        {
            System.out.println("SAX exception :" + e.getMessage());
            System.exit(1);
        }
        catch (IOException e)
        {
            System.out.println("IO exception :" + e.getMessage());
            System.exit(1);
        }

        Element p_mainelement = p_doc.getDocumentElement();
        if (!p_mainelement.getNodeName().equals("Menu")) throw new IOException("This is not a Menu descriptor");

        p_allCommands.clear();
        p_allScreens.clear();
        p_allItems.clear();

        int i_itemId = 0;

        //Command processing
        //Command list <Commands>
        NodeList p_NL = p_mainelement.getElementsByTagName("Commands");
        if (p_NL.getLength() == 0) throw new IOException("You have not any \'Commands\' tag");
        Element p_CommandList = (Element) p_NL.item(0);

        // Tag <command> in the main command list
        NodeList p_Commands = p_CommandList.getElementsByTagName("command");
        for (int lx = 0; lx < p_Commands.getLength(); lx++)
        {
            Element p_item = (Element) p_Commands.item(lx);
            String s_CommandType = p_item.getAttribute("type").toUpperCase();
            String s_StringId = p_item.getAttribute("text");
            String s_CommandId = p_item.getAttribute("id");

            int i_type = CommandReference.COMMAND_USER;
            if (s_CommandType.length() != 0)
            {
                if (s_CommandType.equals("BACK"))
                    i_type = CommandReference.COMMAND_BACK;
                else if (s_CommandType.equals("USER")) i_type = CommandReference.COMMAND_USER;
            }

            StringReference p_sref = null;
            if (_stringtable != null)
            {
                if (!_stringtable.containsKey(s_StringId)) throw new IOException("You have a link to non existent string in the command \'" + s_CommandId + "\' [" + s_StringId + "]");
                p_sref = (StringReference) _stringtable.get(s_StringId);
            }

            p_allStringsId.add(s_StringId);
            CommandReference p_cr = new CommandReference(s_CommandId, i_type, p_sref);
            p_allCommands.put(s_CommandId, p_cr);
        }

        p_NL = p_mainelement.getElementsByTagName("Screens");
        //<Screens>
        if (p_NL.getLength() > 0)
        {
            //<screen>
            NodeList p_ScreenList = ((Element) p_NL.item(0)).getElementsByTagName("screen");

            for (int li = 0; li < p_ScreenList.getLength(); li++)
            {
                Element p_screen = (Element) p_ScreenList.item(li);

                String s_screenId = p_screen.getAttribute("id");
                String s_captionId = p_screen.getAttribute("caption");
                String s_flags = p_screen.getAttribute("flags");
                String s_errorScreenId = p_screen.getAttribute("error");
                String s_okScreenId = p_screen.getAttribute("ok");

                int i_flags = ScreenReference.FLAG_NONE;
                StringTokenizer p_tokenizer = new StringTokenizer(s_flags, "|");

                while (p_tokenizer.hasMoreTokens())
                {
                    String s_token = p_tokenizer.nextToken();
                    if (s_token.equals("NONE"))
                    {
                        i_flags |= ScreenReference.FLAG_NONE;
                    }
                    else if (s_token.equals("ONEXIT"))
                    {
                        i_flags |= ScreenReference.FLAG_ONEXIT;
                    }
                    else if (s_token.equals("CUSTOMSCREEN"))
                    {
                        i_flags |= ScreenReference.FLAG_CUSTOMSCREEN;
                    }
                    else
                        throw new IOException("Unsupported screen flag modifier [" + s_token + "]");
                }

                // Check caption existing in the string table
                StringReference p_Caption = null;
                if (_stringtable != null)
                {
                    if (!_stringtable.containsKey(s_captionId)) throw new IOException("You have a caption link to non existing string [" + s_captionId + "] for the screen with ID=\'" + s_screenId + "\'");

                    p_Caption = (StringReference) _stringtable.get(s_captionId);
                }
                p_allStringsId.add(s_captionId);


                ScreenReference p_screenReference = new ScreenReference(s_screenId, p_Caption, i_flags, s_errorScreenId, s_okScreenId);

                if (p_allScreens.containsKey(s_screenId)) throw new IOException("You have duplicated a screen [" + s_screenId + "]");

                p_allScreens.put(s_screenId, p_screenReference);

                // Commands processing of the <Commands> tag in a <screen> tag
                p_NL = p_screen.getElementsByTagName("Commands");
                if (p_NL.getLength() != 0)
                {
                    p_CommandList = (Element) p_NL.item(0);
                    // <command>
                    p_Commands = p_CommandList.getElementsByTagName("command");
                    for (int lx = 0; lx < p_Commands.getLength(); lx++)
                    {
                        Element p_item = (Element) p_Commands.item(lx);
                        String s_id = p_item.getAttribute("id");
                        CommandReference p_command = (CommandReference) p_allCommands.get(s_id);
                        if (p_command == null) throw new IOException("You have a command link to non exist command [" + s_id + "] in the \'" + p_screenReference.s_ScreenId + "\' screen");

                        int i_flag = CommandLink.FLAG_STATIC;
                        if (p_item.getAttribute("flags").equals("OPTIONAL"))
                            i_flag = CommandLink.FLAG_OPTIONAL;
                        else if (p_item.getAttribute("flags").equals("STATIC")) i_flag = CommandLink.FLAG_STATIC;

                        CommandLink p_CommandLink = new CommandLink(p_command, i_flag);

                        p_screenReference.p_CommandVector.add(p_CommandLink);
                    }
                }

                // processing of the <Items> tag in a <screen> tag
                p_NL = p_screen.getElementsByTagName("Items");
                if (p_NL.getLength() > 0)
                {
                    NodeList p_ItemList = ((Element) p_NL.item(0)).getElementsByTagName("item");

                    int i_screenUseItemFlag = 0;

                    for (int lx = 0; lx < p_ItemList.getLength(); lx++)
                    {
                        Element p_item = (Element) p_ItemList.item(lx);
                        String s_ItemType = p_item.getAttribute("type");
                        String s_textId = p_item.getAttribute("text");
                        String s_flag = p_item.getAttribute("flag");
                        String s_imageIndex = p_item.getAttribute("image");
                        String s_linkScreenIndex = p_item.getAttribute("screen");
                        String s_itemId = p_item.getAttribute("id");
                        String s_align = p_item.getAttribute("align").toUpperCase();

                        int i_align = -1;
                        if (s_align.length() == 0 || s_align.equals("CENTER"))
                        {
                            i_align = ItemReference.ALIGN_CENTER;
                        }
                        else if (s_align.length() == 0 || s_align.equals("LEFT"))
                        {
                            i_align = ItemReference.ALIGN_LEFT;
                        }
                        else if (s_align.length() == 0 || s_align.equals("RIGHT"))
                        {
                            i_align = ItemReference.ALIGN_RIGHT;
                        }
                        else
                        {
                            throw new IOException("You have unsupported align value [" + s_align + "]");
                        }


                        StringReference p_stringRef = null;

                        if (!s_textId.equals(""))
                        {
                            if (_stringtable != null)
                            {
                                p_stringRef = (StringReference) _stringtable.get(s_textId);
                                if (p_stringRef == null) throw new IOException("You have a link to non exist text [" + s_textId + "]");
                            }

                            p_allStringsId.add(s_textId);
                        }

                        int i_imageId = -1;
                        try
                        {
                            if (s_imageIndex.length() != 0)
                                i_imageId = Integer.parseInt(s_imageIndex);
                        }
                        catch (NumberFormatException e)
                        {
                            throw new IOException("You have bad image number format for \'" + s_itemId + "\'");
                        }

                        s_ItemType = s_ItemType.toLowerCase();

                        int i_type = -1;
                        if (s_ItemType.equals("menu"))
                        {
                            i_type = ItemReference.ITEM_MENUITEM;
                        }
                        else if (s_ItemType.equals("textbox"))
                        {
                            i_type = ItemReference.ITEM_TEXTBOX;
                        }
                        else if (s_ItemType.equals("custom"))
                        {
                            i_type = ItemReference.ITEM_CUSTOM;
                        }
                        else if (s_ItemType.equals("image"))
                        {
                            i_type = ItemReference.ITEM_IMAGE;
                        }
                        else if (s_ItemType.equals("delimiter"))
                        {
                            i_type = ItemReference.ITEM_DELIMITER;
                        }
                        else
                            throw new IOException("Unsupported item type "+s_itemId+" [" + s_ItemType + "]");

                        i_screenUseItemFlag |= i_type;
                        i_usedFlags |= i_type;

                        int i_flag = ItemReference.FLAG_NONE;
                        p_tokenizer = new StringTokenizer(s_flag, "|");

                        while (p_tokenizer.hasMoreTokens())
                        {
                            String s_token = p_tokenizer.nextToken();
                            if (s_token.equals("NONE"))
                            {
                                i_flag |= ItemReference.FLAG_NONE;
                            }
                            else if (s_token.equals("OPTIONAL"))
                            {
                                i_flag |= ItemReference.FLAG_OPTIONAL;
                            }
                            else
                                throw new IOException("Unsupported item flag modifier [" + s_token + "]");
                        }

                        ItemReference p_itemReference = new ItemReference(i_type, s_itemId, p_stringRef, s_linkScreenIndex, i_imageId, i_flag, i_itemId++, i_align);

                        if (p_allItems.containsKey(s_itemId)) throw new IOException("Duplicate ID of an item [" + p_itemReference.s_ItemAbsId + "]");
                        p_allItems.put(s_itemId, p_itemReference);

                        p_screenReference.add(p_itemReference);
                    }

                    p_screenReference.i_usedItemsFlag = i_screenUseItemFlag;
                }
            }
        }

        // Post processing of the screen and linking them to other screens
        Iterator p_ScreensIterator = p_allScreens.values().iterator();
        while (p_ScreensIterator.hasNext())
        {
            ScreenReference p_screenObj = (ScreenReference) p_ScreensIterator.next();

            // Linking of an ok screen and an error screen  \
            String s_okscreen = p_screenObj.s_OkScreenId;
            String s_errorscreen = p_screenObj.s_ErrorScreenId;

            if (s_okscreen.length() != 0)
            {
                if (!p_allScreens.containsKey(s_okscreen)) throw new IOException("You have the link to non exist screen as OKScreen in [" + p_screenObj.s_ScreenId + "]");
                p_screenObj.p_OkScreen = (ScreenReference) p_allScreens.get(s_okscreen);
            }

            if (s_errorscreen.length() != 0)
            {
                if (!p_allScreens.containsKey(s_errorscreen)) throw new IOException("You have the link to non exist screen as ErrorScreen in [" + p_screenObj.s_ScreenId + "]");
                p_screenObj.p_ErrorScreen = (ScreenReference) p_allScreens.get(s_errorscreen);
            }

            // Linking items of the screen
            for (int li = 0; li < p_screenObj.size(); li++)
            {
                ItemReference p_item = (ItemReference) p_screenObj.elementAt(li);
                String s_linkScreenName = p_item.s_LinkScreenId;
                if (s_linkScreenName.length() != 0)
                {
                    if (!p_allScreens.containsKey(s_linkScreenName)) throw new IOException("You have the link to non exist screen in [" + p_item.s_ItemAbsId + "," + p_screenObj.s_ScreenId + "]");
                    p_item.p_LinkScreen = (ScreenReference) p_allScreens.get(s_linkScreenName);
                }
            }
        }
    }

    public static void writeMenuTextIDToStream(DataOutputStream _outputStream) throws IOException
    {
        Iterator p_iter = p_allStringsId.iterator();
        PrintStream p_printStream = new PrintStream(_outputStream);

        p_printStream.println("<?xml version=\"1.0\" encoding=\"KOI8-R\"?>");
        p_printStream.println("<TextBlock>");
        p_printStream.println("    <Languages>");
        p_printStream.println("        <language id=\"en\" name=\"English\" resource=\"res/english.bin\"/>");
        p_printStream.println("        <language id=\"ru\" name=\"Русский\" resource=\"res/russian.bin\"/>");
        p_printStream.println("    </Languages>");
        p_printStream.println("    <Strings>");
        while (p_iter.hasNext())
        {
            String s_str = (String) p_iter.next();

            p_printStream.println("        <string id=\"" + s_str + "\">");
            p_printStream.println("           <value langId=\"en\" value=\" \"/>");
            p_printStream.println("           <value langId=\"ru\" value=\" \"/>");
            p_printStream.println("        </string>");

        }
        p_printStream.println("    </Strings>");
        p_printStream.println("</TextBlock>");
    }

    public static void writeMenuConstantsToStream(DataOutputStream _outputStream) throws IOException
    {
        // Writing menu.java
        // Writing constants
        if ((i_usedFlags & ItemReference.ITEM_CUSTOM) != 0)
        {
            _outputStream.writeBytes("//#global _MENU_ITEM_CUSTOM=true\r\n");
        }
        else
        {
            _outputStream.writeBytes("//#global _MENU_ITEM_CUSTOM=false\r\n");
        }

        if ((i_usedFlags & ItemReference.ITEM_DELIMITER) != 0)
        {
            _outputStream.writeBytes("//#global _MENU_ITEM_DELIMITER=true\r\n");
        }
        else
        {
            _outputStream.writeBytes("//#global _MENU_ITEM_DELIMITER=false\r\n");
        }

        if ((i_usedFlags & ItemReference.ITEM_IMAGE) != 0)
        {
            _outputStream.writeBytes("//#global _MENU_ITEM_IMAGE=true\r\n");
        }
        else
        {
            _outputStream.writeBytes("//#global _MENU_ITEM_IMAGE=false\r\n");
        }

        if ((i_usedFlags & ItemReference.ITEM_MENUITEM) != 0)
        {
            _outputStream.writeBytes("//#global _MENU_ITEM_MENUITEM=true\r\n");
        }
        else
        {
            _outputStream.writeBytes("//#global _MENU_ITEM_MENUITEM=false\r\n");
        }

        if ((i_usedFlags & ItemReference.ITEM_TEXTBOX) != 0)
        {
            _outputStream.writeBytes("//#global _MENU_ITEM_TEXTBOX=true\r\n");
        }
        else
        {
            _outputStream.writeBytes("//#global _MENU_ITEM_TEXTBOX=false\r\n");
        }

        _outputStream.writeBytes("\r\n");

        // Writing screens
        Iterator p_iter = p_allScreens.values().iterator();
        _outputStream.writeBytes("// Screens\r\n\r\n");
        while (p_iter.hasNext())
        {
            ScreenReference p_scr = (ScreenReference) p_iter.next();
            String s_str = "private static final int SCR_" + p_scr.s_ScreenId + " = " + p_scr.i_Offset + ";\r\n";
            _outputStream.writeBytes("// Screen " + p_scr.s_ScreenId + "\r\n");
            _outputStream.writeBytes(s_str);
        }

        // Writing items
        p_iter = p_allItems.values().iterator();
        _outputStream.writeBytes("\r\n// Items\r\n\r\n");
        while (p_iter.hasNext())
        {
            ItemReference p_scr = (ItemReference) p_iter.next();
            String s_str = "private static final int ITEM_" + p_scr.s_ItemAbsId + " = " + p_scr.i_id + ";\r\n";
            _outputStream.writeBytes("// Item " + p_scr.s_ItemAbsId + "\r\n");
            _outputStream.writeBytes(s_str);
        }

        // Writing commands
        p_iter = p_allCommands.values().iterator();
        _outputStream.writeBytes("\r\n// Commands\r\n\r\n");
        while (p_iter.hasNext())
        {
            CommandReference p_scr = (CommandReference) p_iter.next();
            String s_str = "private static final int COMMAND_" + p_scr.s_CommandId + " = " + p_scr.i_offset + ";\r\n";
            _outputStream.writeBytes("// Command " + p_scr.s_CommandId + "\r\n");
            _outputStream.writeBytes(s_str);
        }
    }

    public static void writeMenuToStream(DataOutputStream _outputStream) throws IOException
    {
        ByteArrayOutputStream p_baos = new ByteArrayOutputStream();
        DataOutputStream p_daos = new DataOutputStream(p_baos);

        int i_itemLen = 0;

        // Filling offsets for screens
        Iterator p_iter = p_allScreens.values().iterator();
        while (p_iter.hasNext())
        {
            ScreenReference p_screenReference = (ScreenReference) p_iter.next();

            int i_Flags = p_screenReference.i_Flags;

            Vector p_CommandVector = p_screenReference.p_CommandVector;

            // OFFSET
            p_screenReference.i_Offset = p_daos.size() >>> 1;
            // Writing flags and captionId
            p_daos.writeShort(0);
            i_itemLen++;
            // Writing used item types flag
            p_daos.writeShort(0);
            i_itemLen++;
            // Writing pointer to an Error screen
            p_daos.writeShort(0);
            i_itemLen++;
            // Writing pointer to an OK screen
            p_daos.writeShort(0);
            i_itemLen++;

            // Writing item number
            p_daos.writeShort(p_screenReference.size());
            i_itemLen++;

            // Writing items
            for (int li = 0; li < p_screenReference.size(); li++)
            {
                ItemReference p_itemReference = (ItemReference) p_screenReference.elementAt(li);

                int i_Id = p_itemReference.i_id;
                int i_Type = p_itemReference.i_Type;
                int i_Align = p_itemReference.i_align;

                // Writing item ID and align
                p_daos.writeShort((i_Id << 8) | (i_Align));
                i_itemLen++;
                // Writing type and flags
                p_daos.writeShort((i_Type << 8) | (i_Flags));
                i_itemLen++;
                // Writing StringID
                p_daos.writeShort(0);
                i_itemLen++;

                // Writing ImageID
                p_daos.writeShort(0);
                i_itemLen++;

                // Writing the link to a screen
                p_daos.writeShort(0);
                i_itemLen++;
            }

            // Writing commands for the screen

            // Writing number of commands for the screen
            p_daos.writeShort(p_CommandVector.size());
            i_itemLen++;
            for (int li = 0; li < p_CommandVector.size(); li++)
            {
                CommandLink p_link = (CommandLink) p_CommandVector.elementAt(li);

                // Writing flags for the command
                p_daos.writeShort(p_link.i_flag);
                i_itemLen++;
                // Writing link to the command
                p_daos.writeShort(0);
                i_itemLen++;
            }
        }

        int i_SummaryScreenDataSize = p_daos.size() >>> 1;
        p_daos.close();
        p_daos = new DataOutputStream(new ByteArrayOutputStream());

        p_iter = p_allCommands.values().iterator();
        while (p_iter.hasNext())
        {
            CommandReference p_CMNDreference = (CommandReference) p_iter.next();
            p_CMNDreference.i_offset = i_SummaryScreenDataSize + (p_daos.size() >> 1);

            int i_type = p_CMNDreference.i_offset;
            StringReference p_StringId = p_CMNDreference.p_StringId;

            // Writing type and string Id
            p_daos.writeShort((i_type << 8) | p_StringId.i_number);
            i_itemLen++;
        }
        p_daos.close();

//====================================
        _outputStream.writeShort(i_itemLen);

        p_iter = p_allScreens.values().iterator();
        while (p_iter.hasNext())
        {
            ScreenReference p_screenReference = (ScreenReference) p_iter.next();

            String s_ScreenId = p_screenReference.s_ScreenId;
            int i_Flags = p_screenReference.i_Flags;
            StringReference p_CaptionId = p_screenReference.p_CaptionString;
            ScreenReference p_ErrorScreen = p_screenReference.p_ErrorScreen;
            ScreenReference p_OkScreenId = p_screenReference.p_OkScreen;
            int i_usedItems = p_screenReference.i_usedItemsFlag;

            Vector p_CommandVector = p_screenReference.p_CommandVector;

            System.out.print("Save <" + s_ScreenId + "> screen, caption <" + p_CaptionId.s_id + "> ");

            // Writing flags and captionID
            _outputStream.writeShort((i_Flags << 8) | p_CaptionId.i_number);
            // Writing used item types flag
            _outputStream.writeShort(i_usedItems);
            // Writing pointer to an Error screen
            if (p_ErrorScreen == null)
                _outputStream.writeShort(-1);
            else
                _outputStream.writeShort(p_ErrorScreen.i_Offset);
            // Writing pointer to an OK screen
            if (p_OkScreenId == null)
                _outputStream.writeShort(-1);
            else
                _outputStream.writeShort(p_OkScreenId.i_Offset);

            // Writing item number
            _outputStream.writeShort(p_screenReference.size());

            // Writing items if it is not a custom screen
            if ((i_Flags & ScreenReference.FLAG_CUSTOMSCREEN) == 0)
            {
                System.out.print(" items " + p_screenReference.size());
                for (int li = 0; li < p_screenReference.size(); li++)
                {
                    ItemReference p_itemReference = (ItemReference) p_screenReference.elementAt(li);

                    int i_itemId = p_itemReference.i_id;
                    int i_Type = p_itemReference.i_Type;
                    StringReference p_StringId = p_itemReference.p_StringId;
                    int i_ImageId = p_itemReference.i_ImageId;
                    int i_ItemFlag = p_itemReference.i_Flags;
                    ScreenReference p_LinkScreen = p_itemReference.p_LinkScreen;
                    int i_Align = p_itemReference.i_align;


                    // Writing Item Id and align
                    _outputStream.writeShort((i_itemId << 8) | i_Align);
                    // Writing type and flag
                    _outputStream.writeShort((i_Type << 8) | i_ItemFlag);
                    // Writing StringID
                    if (p_StringId == null)
                        _outputStream.writeShort(-1);
                    else
                        _outputStream.writeShort(p_StringId.i_number);
                    // Writing ImageID
                    _outputStream.writeShort(i_ImageId);
                    // Writing the link to a screen
                    if (p_LinkScreen == null)
                    {
                        _outputStream.writeShort(-1);
                    }
                    else
                    {
                        _outputStream.writeShort(p_LinkScreen.i_Offset);
                    }
                }
            }
            else
            {
                System.out.print(" it is a custom screen");
            }
            // Writing commands for the screen

            System.out.println(" commands " + p_CommandVector.size());
            // Writing number of commands for the screen
            _outputStream.writeShort(p_CommandVector.size());
            for (int li = 0; li < p_CommandVector.size(); li++)
            {
                CommandLink p_link = (CommandLink) p_CommandVector.elementAt(li);
                CommandReference p_reference = p_link.p_CommandReference;

                // Writing flags for the command
                _outputStream.writeShort(p_link.i_flag);
                // Writing link to the command
                _outputStream.writeShort(p_reference.i_offset);
            }
        }

        p_iter = p_allCommands.values().iterator();
        while (p_iter.hasNext())
        {
            CommandReference p_CMNDreference = (CommandReference) p_iter.next();

            int i_type = p_CMNDreference.i_type;
            StringReference p_StringId = p_CMNDreference.p_StringId;

            // Writing type and String Id
            _outputStream.writeShort((i_type << 8) | p_StringId.i_number);
        }
    }

}
