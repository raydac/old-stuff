package com.raydac_research.CatalogCompiler;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.io.IOException;
import java.io.DataOutputStream;

public class ScreenComponent
{
    public static final int COMPONENT_LINK = 0;
    public static final int COMPONENT_ITEM = 1;

    public int i_Type;
    public int i_ContentType;
    public int i_NameID;
    public int i_Index;

    public int i_Reference;
    public int i_Cost;
    public int i_ResID;
    public int i_SMSnum;

    public int i_PreviewURL;

    public Screen p_LinkedScreen;

    public CompatibleRecord [] ap_CompatibleRecords;

    public String s_Preview;
    public String s_ScreenID;
    public String s_ResourceID;

    /**
     * Конструктор
     * @param _element Элемент с описанием элемента каталога
     * @param _pool пул строк
     * @param _compatiblePool пул записей совместимости
     * @param _fuzzyItemInfo флаг, если true то информация по элементам может задаваться гибко и могут отсутствовать некоторые элементы
     * @throws IOException
     */
    public ScreenComponent(Element _element,StringsPool _pool,CompatibleRecordsContainer _compatiblePool,boolean _fuzzyItemInfo,int _screenDefaultType) throws IOException
    {
        i_ContentType = -1;
        i_PreviewURL = 0xFFFF;
        String s_tagName = _element.getNodeName();

        if (s_tagName.equals(CatalogContainer.XML_ITEM))
        {
            i_Type = COMPONENT_ITEM;
        }
        else
        if (s_tagName.equals(CatalogContainer.XML_LINK))
        {
            i_Type = COMPONENT_LINK;
        }
        else
            throw new IOException("Unsupported tag \'"+s_tagName+"\'");

        String s_contentType = _element.getAttribute(CatalogContainer.XML_ATTR_TYPE);
        if (s_contentType.length()==0)
        {
            i_ContentType = _screenDefaultType;
        }
        else
        {
            if (s_contentType.equals(CatalogContainer.CONTENTTYPE_GAME))
                i_ContentType = Screen.CONTENTTYPE_GAME;
            else
            if (s_contentType.equals(CatalogContainer.CONTENTTYPE_IMAGE))
                i_ContentType = Screen.CONTENTTYPE_IMAGE;
            else
            if (s_contentType.equals(CatalogContainer.CONTENTTYPE_SMSSERVICE))
                i_ContentType = Screen.CONTENTTYPE_SMSSERVICE;
            else
            if (s_contentType.equals(CatalogContainer.CONTENTTYPE_SOUND))
                i_ContentType = Screen.CONTENTTYPE_SOUND;
            else
            if (s_contentType.equals(CatalogContainer.CONTENTTYPE_INFO))
                i_ContentType = Screen.CONTENTTYPE_INFO;
            else
                throw new IOException("Unsupported content type ["+s_contentType+"]");
        }

        String s_name = _element.getAttribute(CatalogContainer.XML_ATTR_NAME);
        if (s_name.length()==0) throw new IOException("You have "+s_tagName+" without name");

        i_NameID = _pool.addString(s_name);

        switch(i_Type)
        {
            case COMPONENT_ITEM :
            {
                String s_str = _element.getAttribute(CatalogContainer.XML_ATTR_COST);
                if (s_str.length()==0 || i_ContentType==Screen.CONTENTTYPE_INFO)
                {
                    i_Cost = 0xFFFF;
                }
                else
                {
                    i_Cost = _pool.addString(s_str);
                }

                s_str = _element.getAttribute(CatalogContainer.XML_ATTR_SMSNUM);
                if (s_str.length()==0 || i_ContentType==Screen.CONTENTTYPE_INFO)
                {
                    i_SMSnum = 0xFFFF;
                }
                else
                {
                    i_SMSnum = _pool.addString(s_str);
                }

                s_str = _element.getAttribute(CatalogContainer.XML_ATTR_REF);
                if (s_str.length()==0)
                {
                    if (i_ContentType==Screen.CONTENTTYPE_INFO) throw new IOException("You have an INFO item without reference");

                    i_Reference = 0xFFFF;
                }
                else
                {
                    s_str = s_str.replace('~','\n');
                    i_Reference = _pool.addString(s_str);
                }

                s_str = _element.getAttribute(CatalogContainer.XML_ATTR_RESID);
                if (s_str.length()==0 || i_ContentType==Screen.CONTENTTYPE_INFO)
                {
                    i_ResID = 0xFFFF;
                    s_str = null;
                    if (!_fuzzyItemInfo && i_ContentType!=Screen.CONTENTTYPE_INFO)
                        throw new IOException("You have an item without "+CatalogContainer.XML_ATTR_RESID+" [name="+s_name+"]");
                }
                else
                {
                    i_ResID = _pool.addString(s_str);
                    s_ResourceID = s_str;
                }

                s_str = _element.getAttribute(CatalogContainer.XML_ATTR_PREVIEW);
                if (s_str.length()==0)
                {
                     s_Preview = null;
                }
                else
                {
                    s_Preview = s_str;
                }

                // список совместимости
                if (i_ContentType!=Screen.CONTENTTYPE_INFO)
                {
                    NodeList p_list = _element.getElementsByTagName(CatalogContainer.XML_COMPATIBLE);
                    ap_CompatibleRecords = new CompatibleRecord[p_list.getLength()];
                    for(int li=0;li<p_list.getLength();li++)
                    {
                        CompatibleRecord p_rec = _compatiblePool.addCompatibleRecord((Element)p_list.item(li),_pool);
                        ap_CompatibleRecords[li] = p_rec;
                    }
                }
                else
                {
                    ap_CompatibleRecords = new CompatibleRecord[0];
                }

            };break;
            case COMPONENT_LINK :
            {
                s_ScreenID = _element.getAttribute(CatalogContainer.XML_SCREEN);
                if (s_ScreenID.length() == 0) throw new IOException("You have a link without linked screen name");
                p_LinkedScreen = null;
            };break;
        }

    }

    public void saveToStream(DataOutputStream _dos,StringsPool _pool) throws IOException
    {
        int i_nameOffset = i_NameID;

        System.out.println("Component Type="+i_Type+" Name["+i_NameID+":"+i_nameOffset+"]");

        // Записываем тип компонента
        _dos.writeByte(i_Type);

        // имя компонента
        _dos.writeShort(i_nameOffset);

        switch(i_Type)
        {
            case COMPONENT_ITEM :
            {
               // Resource id
               _dos.writeShort(i_ResID);

               // Reference
               _dos.writeShort(i_Reference);

               // Cost
               _dos.writeShort(i_Cost);

               // SMS num
               _dos.writeShort(i_SMSnum);

               // preview
               _dos.writeShort(i_PreviewURL);

                // Resource content type
                _dos.writeByte(i_ContentType);

               // supported devices
               if (ap_CompatibleRecords.length==0)
                   _dos.writeByte(0);
               else
               {
                   _dos.writeByte(ap_CompatibleRecords.length);
                   for(int li=0;li<ap_CompatibleRecords.length;li++)
                   {
                       _dos.writeShort(ap_CompatibleRecords[li].i_Offset);
                   }
               }
            };break;
            case COMPONENT_LINK :
            {
               // Записываем смещение целевого экран
                _dos.writeShort(p_LinkedScreen.i_ScreenOffset);
            };break;
        }
    }
}
