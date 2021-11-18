package com.igormaznitsa.MIDPTools.MenuTextPacker.GameMenuPacking;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import java.util.*;
import java.io.*;

import com.igormaznitsa.MIDPTools.MenuTextPacker.MenuPacking.*;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;

public class GameMenuPacker
{
    public static final String XML_MAINITEM = "GameMenu";
    public static final String XML_ITEM = "item";
    public static final String XML_SUBITEM = "subitem";
    public static final String XML_ID = "id";
    public static final String XML_TEXT = "text";
    public static final String XML_FLAGS = "flags";
    public static final String XML_TYPE = "type";

    public static final int FILE_ID = 0x45328300;
    public static final int FILE_VERSION = 0x0100;

    public static Vector p_Items = new Vector();
    public static HashSet p_UsedStrings = new HashSet();

    public static boolean containsItemForID(String _itemID)
    {
        for(int li=0;li<p_Items.size();li++)
        {
            MenuItem p_item = (MenuItem) p_Items.elementAt(li);
            if (p_item.getTextID().equals(_itemID)) return true;
            if (p_item.containsSubitemForID(_itemID)) return true;
        }
        return false;
    }

    public static void packMenu(String _menufile, HashMap _stringtable) throws Exception
    {
        DocumentBuilderFactory p_dbf = DocumentBuilderFactory.newInstance();
        p_dbf.setIgnoringComments(true);
        DocumentBuilder p_db = p_dbf.newDocumentBuilder();

        String s_MenuFileName = _menufile;

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
        if (!p_mainelement.getNodeName().equals(XML_MAINITEM)) throw new IOException("This is not a GameMenu descriptor");

        NodeList p_ItemList = p_mainelement.getElementsByTagName(XML_ITEM);

        int i_id = 0;

        for(int li=0;li<p_ItemList.getLength();li++)
        {
            Element p_item = (Element) p_ItemList.item(li);
            String s_id = p_item.getAttribute(XML_ID).trim();
            String s_type = p_item.getAttribute(XML_TYPE).trim();
            String s_flags = p_item.getAttribute(XML_FLAGS).trim();
            String s_textid = p_item.getAttribute(XML_TEXT).trim();

            if (s_id.length() == 0) throw new IOException("One item doesn't have the ID attribute or the attribute is empty");
            if (s_type.length() == 0) s_type = "BUTTON";
            if (s_flags.length() == 0) s_flags = "NONE";

            // Проверяем уникальность идентификатора
            if (containsItemForID(s_id)) throw new IOException("You have duplicated \'"+s_id+"\' item");


            StringReference p_text = null;

            if (_stringtable != null)
            {
            p_text = (StringReference)_stringtable.get(s_textid);
            if (p_text == null) throw new IOException("You have an wrong text reference for \'"+s_id+"\' item ["+s_textid+"]");
            }
            else
            {
                p_UsedStrings.add(s_textid);
            }

            MenuItem p_menuItem = new MenuItem(s_id,p_text,MenuItem.convertStringFlags(s_flags),MenuItem.convertStringType(s_type));
            p_Items.add(p_menuItem);

            p_menuItem.setID(i_id++);

            if ((p_menuItem.getFlags() & MenuItem.FLAG_CUSTOM)!=0) continue;
            NodeList p_subitems = p_item.getElementsByTagName(XML_SUBITEM);

            for(int ls=0;ls<p_subitems.getLength();ls++)
            {
                Element p_sitem = (Element) p_subitems.item(ls);
                String s_sid = p_sitem.getAttribute(XML_ID).trim();
                String s_stextid = p_sitem.getAttribute(XML_TEXT).trim();
                String s_sflags = p_sitem.getAttribute(XML_FLAGS).trim();

                if (s_sid.length() == 0) throw new IOException("One subitem of \'"+s_id+"\' desn't have the ID");

                StringReference p_stext = null;

                if (_stringtable!=null)
                {
                p_stext = (StringReference)_stringtable.get(s_stextid);
                if (p_stext == null) throw new IOException("You have an wrong text reference for \'"+s_sid+"\' subitem ["+s_stextid+"]");
                }
                else
                {
                    p_UsedStrings.add(s_stextid);
                }

                if (containsItemForID(s_sid)) throw new IOException("You have duplicated a subitem named \'"+s_sid+"\'");

                p_UsedStrings.add(p_stext);

                MenuSubitem p_ssitem = new MenuSubitem(s_sid,p_stext,MenuSubitem.convertStringFlags(s_sflags));
                p_menuItem.addSubItem(p_ssitem);

                p_ssitem.setID(i_id++);
            }
        }

        System.out.println("String table has "+_stringtable.size()+" items");
    }

    public static void writeMenuTextIDToStream(DataOutputStream _outputStream)
    {
        Iterator p_iter = p_UsedStrings.iterator();
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
        // Записываем смещения до пунктов меню
        _outputStream.writeBytes("// Menu item offsets\r\n");
        for(int li=0;li<p_Items.size();li++)
        {
            MenuItem p_item = (MenuItem) p_Items.elementAt(li);
            _outputStream.writeBytes("private static final int ITEM_"+p_item.getTextID()+"_OFFSET = "+p_item.getOffset()+";\r\n");
        }

        // Записываем идентификаторы
        _outputStream.writeBytes("// Identifiers\r\n");
        StringBuffer p_array = new StringBuffer(255);
        for(int li=0;li<p_Items.size();li++)
        {
            MenuItem p_item = (MenuItem) p_Items.elementAt(li);
            _outputStream.writeBytes("private static final int ITEM_ID_"+p_item.getTextID()+" = "+p_item.getID()+";\r\n");
            Vector p_subitems = p_item.getSubitems();
            if (li!=0)
            {
                p_array.append(',');
            }
            p_array.append("(short)");
            p_array.append(Integer.toString(p_item.getOffset()));

            for(int ls=0;ls<p_subitems.size();ls++)
            {
                MenuSubitem p_sitem = (MenuSubitem)p_subitems.elementAt(ls);
                _outputStream.writeBytes("private static final int SUBITEM_ID_"+p_sitem.getTextID()+" = "+p_sitem.getID()+";\r\n");
            }
        }

        _outputStream.writeBytes("// Offset array\r\n");
        _outputStream.writeBytes("private static final short[] MENU_OFFSETS = new short[]{"+p_array.toString()+"};\r\n");

    }

    public static void writeMenuToStream(DataOutputStream _outputStream) throws IOException
    {
        ByteArrayOutputStream p_baos = new ByteArrayOutputStream(10000);
        DataOutputStream p_dos = new DataOutputStream(p_baos);

        // Версия
        p_dos.writeShort(FILE_VERSION);

        // Количество пунктов
        p_dos.writeByte(p_Items.size());

        // Пункты
        for(int li=0;li<p_Items.size();li++)
        {
            MenuItem p_item = (MenuItem) p_Items.elementAt(li);

            p_item.setOffset(p_dos.size());

            // Идентификатор пункта
            p_dos.writeByte(p_item.getID());
            // Идентификатор текста
            p_dos.writeByte(p_item.getTextString().i_number);
            // Тип
            p_dos.writeByte(p_item.getType());
            // Флаги
            p_dos.writeByte(p_item.getFlags());

            // Записываем подпункты
            Vector p_subitems = p_item.getSubitems();

            // Количество подпунктов
            p_dos.writeByte(p_subitems.size());

            for(int ls=0;ls<p_subitems.size();ls++)
            {
                MenuSubitem p_ms = (MenuSubitem) p_subitems.elementAt(ls);
                // Идентификатор подпункта
                p_dos.writeByte(p_ms.getID());
                // Идентификатор текста
                p_dos.writeByte(p_ms.getTextString().i_number);
                // Флаги
                p_dos.writeByte(p_ms.getFlags());
            }
        }

        p_dos.flush();
        byte [] ab_menuArray = p_baos.toByteArray();
        _outputStream.writeInt(FILE_ID);
        _outputStream.writeShort(ab_menuArray.length);
        _outputStream.write(ab_menuArray);
    }

}
