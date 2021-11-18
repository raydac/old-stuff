package com.igormaznitsa.MIDPTools.MenuTextPacker;

import org.w3c.dom.Element;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import java.io.*;
import java.util.*;

import com.igormaznitsa.MIDPTools.MenuTextPacker.MenuPacking.StringReference;
import com.igormaznitsa.MIDPTools.MenuTextPacker.MenuPacking.MenuPacker;


public class MenuTextPacker
{
    HashMap p_languages = new HashMap();
    HashMap p_strings = new HashMap();

    public static final char [][] achch_charsets = new char[][]{ new char[]{' ','0','1','2','3','4','5','6','7','8','9',':','.',',','!','?','+','-','\'','\"','(',')'},
    new char[]{'A','B','C','D','E','F','G','H','I','Y','K','L','M','N','O','P','Q','R','S','T','U','V','W','X','Y','Z','a','b','c','d','e','f','g','h','i','y','k','l','m','n','o','p','q','r','s','t','u','v','w','x','y','z'},
    new char[]{'А','Б','В','Г','Д','Е','Ж','З','И','Й','К','Л','М','Н','О','П','Р','С','Т','У','Ф','Х','Ц','Ч','Ш','Щ','Ъ','Ы','Ь','Э','Ю','Я','а','б','в','г','д','е','ж','з','и','й','к','л','м','н','о','п','р','с','т','у','ф','х','ц','ч','ш','щ','ъ','ы','ь','э','ю','я'}};

    private class LanguageReference
    {
        String s_id,s_name,s_resource;
        boolean lg_default;

        public LanguageReference(String _id, String _name, String _resource, boolean _default)
        {
            lg_default = _default;
            s_id = _id;
            s_name = _name;
            s_resource = _resource;
        }
    }

    public HashMap getStringTable()
    {
        return this.p_strings;
    }

    public HashMap getLanguageTable()
    {
        return this.p_strings;
    }

    private int convertCharToCharsetByte(char _char)
    {
        for(int li=0;li<achch_charsets.length;li++)
        {
            char [] ach_charset = achch_charsets[li];
            for(int lx=0;lx<ach_charset.length;lx++)
            {
                if (_char == ach_charset[lx])
                {
                    return (li<<6)&lx;
                }
            }
        }
        return -1;
    }

    public byte [] convertStringToByte(String _string,boolean _caseSensetive) throws IOException
    {
        if (_string.length()>256) throw new IOException("The string has more symbols than 256 ["+_string+"]");

        if (!_caseSensetive) _string = _string.toUpperCase();
        byte [] ab_result = new byte[_string.length()+1];

        ab_result[0] = (byte)_string.length();
        for(int li=0;li<_string.length();li++)
        {
            char ch_char = _string.charAt(li);
            int i_code = convertCharToCharsetByte(ch_char);
            if (i_code < 0) throw new IOException("Unknown char \'"+ch_char+"\'");
            ab_result [li+1] = (byte)i_code;
        }
        return ab_result;
    }

    public MenuTextPacker(String _file,boolean _saveAsByte,boolean _caseSensetive) throws Exception
    {
        String s_name = _file;
        if (s_name.endsWith(".xml"))
        {
            s_name = s_name.substring(0, s_name.length() - 4);
        }

        DocumentBuilderFactory p_dbf = DocumentBuilderFactory.newInstance();
        p_dbf.setIgnoringComments(true);
        DocumentBuilder p_db = p_dbf.newDocumentBuilder();

        Document p_doc = null;
        try
        {
            File p_file = new File(_file);
            System.out.println("XML descriptor is " + p_file.getAbsolutePath());
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
        if (!p_mainelement.getNodeName().equals("TextBlock")) throw new IOException("This is not a TextBlock descriptor");

        Element p_elem = (Element) p_doc.getElementsByTagName("Languages").item(0);
        NodeList p_nodelist = p_elem.getElementsByTagName("language");

        int i_languages_count = 0;
        int i_strings_count = 0;

        for (int li = 0; li < p_nodelist.getLength(); li++)
        {
            Element p_language = (Element) p_nodelist.item(li);
            String s_languageid = p_language.getAttribute("id");
            String s_languagename = p_language.getAttribute("name");
            String s_resourcefile = p_language.getAttribute("resource");

            if (s_languageid == null || s_languagename == null || s_resourcefile == null) throw new IOException("Error language item");

            LanguageReference p_reference = new LanguageReference(s_languageid, s_languagename, "/" + s_resourcefile, li == 0 ? true : false);
            if (p_languages.containsKey(s_languageid)) throw new IOException("Duplicated language ID [" + s_languageid + "]");
            p_languages.put(s_languageid, p_reference);
            i_languages_count++;
        }

        p_elem = (Element) p_doc.getElementsByTagName("Strings").item(0);
        p_nodelist = p_elem.getElementsByTagName("string");
        for (int li = 0; li < p_nodelist.getLength(); li++)
        {
            Element p_string = (Element) p_nodelist.item(li);
            String s_id = p_string.getAttribute("id");
            if (p_strings.containsKey(s_id)) throw new IOException("Duplicated string ID [" + s_id + "]");

            NodeList p_values = p_string.getElementsByTagName("value");

            StringReference p_strref = new StringReference(s_id, li);

            for (int lli = 0; lli < p_values.getLength(); lli++)
            {
                Element p_val = (Element) p_values.item(lli);
                String s_languageid = p_val.getAttribute("langId");
                String s_value = p_val.getAttribute("value");

                if (s_languageid == null || s_value == null) throw new IOException("Error value reference in [" + s_id + "]");

                //if (!p_languages.containsKey(s_languageid)) throw new IOException("Unknown langId in [" + s_id + "]");

                p_strref.addValue(s_languageid, s_value);
            }

            Iterator p_langs = p_languages.values().iterator();
            while (p_langs.hasNext())
            {
                LanguageReference p_ref = (LanguageReference) p_langs.next();
                if (p_strref.getValue(p_ref.s_id) == null) throw new IOException("I can't find value in [" + s_id + "] for [" + p_ref.s_id + "] language");
            }

            p_langs = null;

            p_strings.put(s_id, p_strref);
            i_strings_count++;
        }

        // Sorting of string array
        TreeSet p_sr = new TreeSet(new Comparator()
        {
            public int compare(Object o1, Object o2)
            {
                int i_hash1 = o1.hashCode();
                int i_hash2 = o2.hashCode();

                if (i_hash1 > i_hash2) return 1;
                if (i_hash1 == i_hash2) return 0;
                if (i_hash1 < i_hash2) return -1;
                return 0;
            }
        });

        Iterator p_iter = p_strings.values().iterator();

        while (p_iter.hasNext())
        {
            StringReference p_ref = (StringReference) p_iter.next();
            p_sr.add(p_ref);
        }

        if (i_languages_count > 255) throw new IOException("Too many languages");
        if (i_strings_count > 255) throw new IOException("Too many strings");

        // Output of text constants
        FileOutputStream p_fos = new FileOutputStream(s_name + ".java");
        DataOutputStream p_dos = new DataOutputStream(p_fos);

        p_iter = p_sr.iterator();
        int i_li = 0;
        while (p_iter.hasNext())
        {
            StringReference p_SR = (StringReference) p_iter.next();
            p_dos.writeBytes(" private static final int " + p_SR.s_id + " = " + i_li + ";\r\n");
            p_SR.i_number = i_li;
            i_li++;
        }
        p_dos.flush();
        p_dos.close();

        // Output of language file
        p_fos = new FileOutputStream("langs.bin");
        p_dos = new DataOutputStream(p_fos);

        p_dos.writeByte(i_languages_count);

        p_iter = p_languages.values().iterator();

        String[] as_sortedlanguages = new String[p_languages.size()];

        // Sorting of the language list
        int i_cntr = 1;
        while (p_iter.hasNext())
        {
            LanguageReference p_lr = (LanguageReference) p_iter.next();
            if (p_lr.lg_default)
            {
                as_sortedlanguages[0] = p_lr.s_id;
            }
            else
            {
                as_sortedlanguages[i_cntr++] = p_lr.s_id;
            }
        }

        byte [] ab_languageNamesArray = null;
        if (_saveAsByte)
        {
            ByteArrayOutputStream p_lnbaos = new ByteArrayOutputStream(1024);
            for(int li=0;li<as_sortedlanguages.length;li++)
            {
                LanguageReference p_ref = (LanguageReference) p_languages.get(as_sortedlanguages[li]);
                byte [] ab_array = convertStringToByte(p_ref.s_name,_caseSensetive);
                p_lnbaos.write(ab_array);
                p_lnbaos.flush();
            }
            ab_languageNamesArray = p_lnbaos.toByteArray();
            p_lnbaos = null;
            p_dos.writeShort(ab_languageNamesArray.length);
        }

        for (int li = 0; li < as_sortedlanguages.length; li++)
        {
            LanguageReference p_ref = (LanguageReference) p_languages.get(as_sortedlanguages[li]);

            if (_saveAsByte)
            {
                p_dos.writeUTF(p_ref.s_id);
                p_dos.writeUTF(p_ref.s_resource);
            }
            else
            {
                p_dos.writeUTF(p_ref.s_name);
                p_dos.writeUTF(p_ref.s_id);
                p_dos.writeUTF(p_ref.s_resource);
            }

            File p_nfile = new File(p_ref.s_resource);
            FileOutputStream p_fosstr = new FileOutputStream(p_nfile.getName());
            DataOutputStream p_dosstr = new DataOutputStream(p_fosstr);
            p_nfile = null;

            p_dosstr.writeByte(i_strings_count);

            p_iter = p_sr.iterator();
            ByteArrayOutputStream p_sbaos = new ByteArrayOutputStream(5000);
            DataOutputStream p_bd = new DataOutputStream(p_sbaos);
            while (p_iter.hasNext())
            {
                StringReference p_strref = (StringReference) p_iter.next();
                String p_str = p_strref.getValue(p_ref.s_id);
                if (_saveAsByte)
                {
                    byte [] ab_byteString = convertStringToByte(p_str,_caseSensetive);
                    p_dosstr.write(ab_byteString);
                }
                else
                {
                    p_bd.writeUTF(p_str);
                }
            }
            p_bd.flush();
            byte [] ab_strArray = p_sbaos.toByteArray();

            if (_saveAsByte)
            {
                p_dosstr.writeShort(ab_strArray.length);
            }
            p_dosstr.write(ab_strArray);

            p_dosstr.flush();
            p_dosstr.close();
        }
        p_dos.flush();
        p_dos.close();

    }

    private static void outHelp()
    {
        System.out.println("MenuTextPacker utility.\r\n(C) 2002-2004 All Copyright by Igor Maznitsa");
        System.out.println("\r\nCommand string:");
        System.out.println("com.igormaznitsa.MIDPTools.MenuTextPacker.MenuTextPacker [/b] /t:xml_text_file [/m:xml_menu_file]\r\n");
        System.out.println("/b - save chars as bytes else the chars are written as UTF8");
    }

    public static final void main(String[] _args)
    {
        if (_args.length == 0)
        {
            outHelp();
            System.exit(0);
        }

        boolean lg_saveAsByte = false;
        boolean lg_caseSensetive = false;
        String s_xmltextfile = null;
        String s_xmlmenufile = null;

        for(int li=0;li<_args.length;li++)
        {
            if (_args[li].startsWith("/b"))
                lg_saveAsByte = true;
            else
            if (_args[li].startsWith("/t:"))
            {
                s_xmltextfile = _args[li].substring(3);
            }
            else
            if (_args[li].startsWith("/c"))
            {
                lg_caseSensetive = true;
            }
            else
            if (_args[li].startsWith("/m:"))
            {
                s_xmlmenufile = _args[li].substring(3);
            }
        }

        try
        {
           HashMap p_stringTable = null;

           if (s_xmltextfile != null)
           p_stringTable =  new MenuTextPacker(s_xmltextfile,lg_saveAsByte,lg_caseSensetive).getStringTable();

            if (s_xmlmenufile!=null)
            {
                if (p_stringTable!=null)
                {
                   MenuPacker.packMenu(s_xmlmenufile,p_stringTable);
                    FileOutputStream p_fos = new FileOutputStream("menu.bin");
                    DataOutputStream p_dos = new DataOutputStream(p_fos);

                    MenuPacker.writeMenuToStream(p_dos);
                    p_dos.close();

                    p_fos = new FileOutputStream("menu.java");
                    p_dos = new DataOutputStream(p_fos);

                    MenuPacker.writeMenuConstantsToStream(p_dos);
                    p_dos.close();
                }
                else
                {
                    MenuPacker.packMenu(s_xmlmenufile,null);
                    FileOutputStream p_fos = new FileOutputStream("texts.xml");
                    DataOutputStream p_dos = new DataOutputStream(p_fos);
                    MenuPacker.writeMenuTextIDToStream(p_dos);
                    p_dos.close();
                }
            }
        }
        catch (Exception e)
        {
            e.printStackTrace(System.out);
            System.err.println(e.getMessage());
            System.exit(1);
        }
        System.exit(0);
    }
}
