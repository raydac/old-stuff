package com.raydac_research.CatalogCompiler;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.io.IOException;
import java.io.DataOutputStream;
import java.util.Vector;

public class Screen
{
    public static final int FLAG_ITEMS = 1;
    public static final int FLAG_LINKS = 2;

    public static final int CONTENTTYPE_NONE = 0;
    public static final int CONTENTTYPE_IMAGE = 3;
    public static final int CONTENTTYPE_GAME = 1;
    public static final int CONTENTTYPE_SOUND = 2;
    public static final int CONTENTTYPE_SMSSERVICE = 4;
    public static final int CONTENTTYPE_INFO = 5;

    public String s_ScreenID;
    public int i_Name;
    public int i_ContentType;
    public int i_ScreenOffset;

    public ScreenComponent [] ap_Components;

    public Screen(Element _element,StringsPool _pool, CompatibleRecordsContainer _compatible, boolean _fuzzyItemInfo) throws IOException
    {
        s_ScreenID = _element.getAttribute(CatalogContainer.XML_ATTR_ID);
        if (s_ScreenID.length() == 0) s_ScreenID = null;
        String s_name = _element.getAttribute(CatalogContainer.XML_ATTR_NAME);
        if (s_name.length()==0) s_name = null;

        if (s_ScreenID==null && s_name==null) throw new IOException("I can't identify a screen, you have not defined name or id for the screen.");

        if (s_ScreenID==null)
        {
            System.out.println("WARNING: You have a screen without ID so I will be using its name as id ["+s_name+"]");
            s_ScreenID = s_name;
        }

        if (s_name==null)
        {
            System.out.println("WARNING: You have a screen without NAME so I will be using its ID as NAME ["+s_ScreenID+"]");
            s_name = s_ScreenID;
        }

        i_Name = _pool.addString(s_name);

        i_ContentType = 0;

        String s_content = _element.getAttribute(CatalogContainer.XML_ATTR_TYPE);
        if(s_content.length()!=0)
        {
            s_content = s_content.trim().toUpperCase();
            if (s_content.equals(CatalogContainer.CONTENTTYPE_GAME))
            {
                i_ContentType = CONTENTTYPE_GAME;
            }
            else
            if (s_content.equals(CatalogContainer.CONTENTTYPE_IMAGE))
            {
                i_ContentType = CONTENTTYPE_IMAGE;
            }
            else
            if (s_content.equals(CatalogContainer.CONTENTTYPE_SOUND))
            {
                i_ContentType = CONTENTTYPE_SOUND;
            }
            else
            if (s_content.equals(CatalogContainer.CONTENTTYPE_SMSSERVICE))
            {
                i_ContentType = CONTENTTYPE_SMSSERVICE;
            }
            else
            if (s_content.equals(CatalogContainer.CONTENTTYPE_INFO))
            {
                i_ContentType = CONTENTTYPE_INFO;
            }
            else
                throw new IOException("Unsupported type "+s_content);
        }

        NodeList p_list = _element.getElementsByTagName("*");

        Vector p_components = new Vector();

        for(int li=0;li<p_list.getLength();li++)
        {
            Element p_elem = (Element) p_list.item(li);
            if (p_elem.getParentNode().equals(_element))
            {
                ScreenComponent p_component = null;
                try
                {
                    p_component = new ScreenComponent(p_elem,_pool,_compatible,_fuzzyItemInfo,i_ContentType);
                }
                catch (Exception e)
                {
                    System.out.println("SCREEN ID="+s_ScreenID+" NAME="+s_name);
                    throw new IOException(e.getMessage());
                }
                p_components.add(p_component);
            }
        }

        ap_Components = new ScreenComponent[p_components.size()];
        for(int li=0;li<p_components.size();li++)
        {
            ap_Components[li] = (ScreenComponent)p_components.elementAt(li);
        }

        p_components.clear();
    }

    public void saveToStream(DataOutputStream _dos,StringsPool _pool) throws IOException
    {
        System.out.println("Screen id="+s_ScreenID);

        boolean lg_items = false;
        boolean lg_links = false;

        for(int li=0;li<ap_Components.length;li++)
        {
            if (ap_Components[li].i_Type == ScreenComponent.COMPONENT_ITEM) lg_items = true;
            if (ap_Components[li].i_Type == ScreenComponent.COMPONENT_LINK) lg_links = true;
        }

        int i_flags = (lg_items ? FLAG_ITEMS : 0) | (lg_links ? FLAG_LINKS : 0);

        // Имя экрана
        _dos.writeShort(i_Name);

        // Флаги
        _dos.writeByte(i_flags);

        // Тип контента
        _dos.writeByte(i_ContentType);

        System.out.println("Content type = "+i_ContentType);

        // Количество компонент
        _dos.writeByte(ap_Components.length);

        // Записываем компоненты
        for(int li=0;li<ap_Components.length;li++)
        {
            ap_Components[li].saveToStream(_dos,_pool);
        }


    }
}
