package com.raydac_research.CatalogCompiler;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import java.io.*;
import java.util.StringTokenizer;
import java.util.Date;

public class CatalogContainer
{
    public static final int FORMAT_VERSION = 0x12;

    public static final String XML_CATALOG = "Catalog";
    public static final String XML_ATTR_CLIENT = "client";
    public static final String XML_ATTR_URL = "url";
    public static final String XML_ATTR_SMSNUM = "smsnum";
    public static final String XML_ATTR_COST = "cost";
    public static final String XML_ATTR_REF = "ref";
    public static final String XML_ATTR_PREVIEW = "preview";
    public static final String XML_ATTR_VENDOR = "vendor";
    public static final String XML_ATTR_MODELS = "models";
    public static final String XML_ATTR_RESID = "resid";
    public static final String XML_SAVESTRINGASBYTES = "savestringsasbytes";
    public static final String XML_SEND2FRIEND = "send2friend";
    public static final String XML_SCREEN = "scr";
    public static final String XML_ATTR_ID = "id";
    public static final String XML_ATTR_TYPE = "type";
    public static final String XML_ATTR_NAME = "name";
    public static final String XML_LINK = "link";
    public static final String XML_ITEM = "item";
    public static final String XML_FUZZYITEMINFO = "fuzzyiteminfo";
    public static final String XML_COMPATIBLE = "compatible";
    public static final String XML_GAME = "game";
    public static final String XML_ATTR_CLASSNAME = "name";
    public static final String XML_ATTR_TEXT = "text";

    public static final String XML_ADVERTISEMENT = "advertisement";
    public static final String XML_AGREEMENT = "agreement";

    public static final String XML_SUPPORT = "support";
    public static final String XML_ATTR_PHONE = "phone";
    public static final String XML_ATTR_EMAIL = "email";

    public static final String XML_ATTR_VALIDITY = "validity";

    public static final String XML_ATTR_WAPORDER = "waporder";

    public static final String CONTENTTYPE_SOUND = "SOUND";
    public static final String CONTENTTYPE_IMAGE = "IMAGE";
    public static final String CONTENTTYPE_GAME = "GAME";
    public static final String CONTENTTYPE_SMSSERVICE = "SMSSERVICE";
    public static final String CONTENTTYPE_INFO = "INFO";

    public static final int FLAG_STRINGS_UTF8 = 1;

    private StringsPool p_StringsPool;
    private CompatibleRecordsContainer p_CompatibleRecordsContainer;
    private ResourcesContainer p_ResourcesContainer;
    private ScreensContainer p_ScreensContainer;

    private boolean lg_saveAsBytes;

    public String s_AgreementText;
    public int i_AgreementID;
    public String s_clientID;
    public int i_clientID;
    public String s_url;
    public int i_url;
    public String s_Send2Friend;
    public int i_Send2Friend;
    public boolean lg_EnableSend2Friend;

    public String s_MainAdvertisment;
    public int i_MainAdvertisment;
    public long l_MainAdvertismentValidity;

    public String s_GameName;

    public long l_CatalogValidity;

    public String s_supportPhone;
    public int i_supportPhone;

    public String s_supportEmail;
    public int i_supportEmail;

    public String s_wapOrderURL;
    public int i_wapOrderURL;

    private int i_defaultSMSnum;
    private int i_defaultCost;

    protected boolean lg_HasHTTPResources;
    protected boolean lg_HasJARResources;

    public boolean hasFileResources()
    {
        return p_ResourcesContainer.abab_Resources.length!=0;
    }

    private static long getValidityValue(String _str)
    {
        if (_str==null || _str.length()==0) return 0;
            StringTokenizer p_tknz = new StringTokenizer(_str,".");
            int i_day = Integer.parseInt(p_tknz.nextToken().trim());
            int i_month = Integer.parseInt(p_tknz.nextToken().trim());
            int i_year = Integer.parseInt(p_tknz.nextToken().trim());

            Date p_date = new Date(i_year-1900,i_month,i_day,23,59,59);

            long l_CatalogValidity = p_date.getTime();
        return l_CatalogValidity;
    }

    public CatalogContainer(InputStream _catalogXML) throws IOException
    {
        i_clientID = 0xFFFF;
        i_url = 0xFFFF;
        i_supportPhone = 0xFFFF;
        i_supportEmail = 0xFFFF;
        i_wapOrderURL = 0xFFFF;
        i_defaultCost = 0xFFFF;
        i_defaultSMSnum = 0xFFFF;
        i_Send2Friend = 0xFFFF;
        l_MainAdvertismentValidity = 0;
        i_MainAdvertisment = 0xFFFF;
        l_CatalogValidity = 0;
        i_AgreementID = 0xFFFF;

        p_StringsPool = new StringsPool();
        p_CompatibleRecordsContainer = new CompatibleRecordsContainer();

        try
        {
            boolean lg_fuzzyItemInfo = false;

            DocumentBuilderFactory p_dbf = DocumentBuilderFactory.newInstance();
            p_dbf.setIgnoringComments(true);
            DocumentBuilder p_db = p_dbf.newDocumentBuilder();

            Document p_doc = null;
            p_doc = p_db.parse(_catalogXML);
            Element p_mainelement = p_doc.getDocumentElement();

            if (!p_mainelement.getNodeName().equals(XML_CATALOG)) throw new IOException("It's not a catalog xml file");

            if (p_mainelement.getElementsByTagName(XML_SUPPORT).getLength()!=0)
            {
                Element p_elem = (Element) p_mainelement.getElementsByTagName(XML_SUPPORT).item(0);
                s_supportPhone = p_elem.getAttribute(XML_ATTR_PHONE);
                if (s_supportPhone.length()!=0)
                    i_supportPhone = p_StringsPool.addString(s_supportPhone);

                s_supportEmail = p_elem.getAttribute(XML_ATTR_EMAIL);
                if (s_supportEmail.length()!=0)
                    i_supportEmail = p_StringsPool.addString(s_supportEmail);
            }

            if (p_mainelement.getElementsByTagName(XML_SAVESTRINGASBYTES).getLength()!=0)
                lg_saveAsBytes = true;
            else
                lg_saveAsBytes = false;

            if (p_mainelement.getElementsByTagName(XML_FUZZYITEMINFO).getLength()!=0)
                lg_fuzzyItemInfo = true;
            else
                lg_fuzzyItemInfo = false;

            if (p_mainelement.getElementsByTagName(XML_AGREEMENT).getLength()!=0)
            {
                Element p_elem = (Element) p_mainelement.getElementsByTagName(XML_AGREEMENT).item(0);
                s_AgreementText = p_elem.getAttribute(XML_ATTR_TEXT);
                if (s_AgreementText.length()==0) throw new IOException("You have not the agreement text in the AGREEMENT tag");
                i_AgreementID = p_StringsPool.addString(s_AgreementText);
            }

            if (lg_fuzzyItemInfo) System.out.println("Can have fuzzy item info");

            if (p_mainelement.getElementsByTagName(XML_SEND2FRIEND).getLength()!=0)
            {
                Element p_elem = (Element) p_mainelement.getElementsByTagName(XML_SEND2FRIEND).item(0);
                lg_EnableSend2Friend = true;
                s_Send2Friend = p_elem.getAttribute(XML_ATTR_SMSNUM);
                if (s_Send2Friend.length() == 0)
                {
                    s_Send2Friend = null;
                    i_Send2Friend = 0xFFFF;
                }
                else
                {
                    i_Send2Friend = p_StringsPool.addString(s_Send2Friend);
                }
            }
            else
                lg_EnableSend2Friend = false;

            l_MainAdvertismentValidity = 0;
            if (p_mainelement.getElementsByTagName(XML_ADVERTISEMENT).getLength()!=0)
            {
                Element p_elem = (Element) p_mainelement.getElementsByTagName(XML_ADVERTISEMENT).item(0);
                s_MainAdvertisment = p_elem.getAttribute(XML_ATTR_REF);
                if (s_MainAdvertisment.length() == 0) throw new Exception("You have advertisment tag without reference");
                i_MainAdvertisment = p_StringsPool.addString(s_MainAdvertisment);
                String s_validity = p_elem.getAttribute(XML_ATTR_VALIDITY);
                if (s_validity.length()!=0)
                {
                    l_MainAdvertismentValidity = getValidityValue(s_validity);
                }
            }
            else
            {
                s_MainAdvertisment = null;
                i_MainAdvertisment = 0xFFFF;
            }

            if (p_mainelement.getElementsByTagName(XML_GAME).getLength()!=0)
            {
                Element p_elem = (Element) p_mainelement.getElementsByTagName(XML_GAME).item(0);
                s_GameName = p_elem.getAttribute(XML_ATTR_CLASSNAME);
                if (s_GameName.length() == 0) throw new Exception("You have not specified game name");
            }
            else
                s_GameName = null;

            s_url = p_mainelement.getAttribute(XML_ATTR_URL);
            s_clientID = p_mainelement.getAttribute(XML_ATTR_CLIENT);

            if(s_clientID.length()==0) throw new IOException("You must define the client id");
            i_clientID=p_StringsPool.addString(s_clientID);

            if (s_url.length()!=0)
                i_url = p_StringsPool.addString(s_url);

            String s_validity = p_mainelement.getAttribute(XML_ATTR_VALIDITY);
            l_CatalogValidity = getValidityValue(s_validity);
            if (l_CatalogValidity!=0)
            {
                System.out.println("Catalog expired date = "+new Date(l_CatalogValidity));
            }
            String s_txt = p_mainelement.getAttribute(XML_ATTR_SMSNUM);

            if (s_txt.length()==0)
            {
                if (!lg_fuzzyItemInfo) throw new IOException("You must define default sms number");
            }
            else
            {
                i_defaultSMSnum = p_StringsPool.addString(s_txt);
            }

            s_txt = p_mainelement.getAttribute(XML_ATTR_COST);

            if (s_txt.length()==0)
            {
                if (!lg_fuzzyItemInfo) throw new IOException("You must define default cost");
                i_defaultCost = 0xFFFF;
            }
            else
            {
                i_defaultCost = p_StringsPool.addString(s_txt);
            }

            s_wapOrderURL = p_mainelement.getAttribute(XML_ATTR_WAPORDER);
            if (s_wapOrderURL.length()!=0)
                i_wapOrderURL = p_StringsPool.addString(s_wapOrderURL);

            p_ScreensContainer = new ScreensContainer(p_mainelement,p_StringsPool,p_CompatibleRecordsContainer,lg_fuzzyItemInfo);
            p_ResourcesContainer = new ResourcesContainer(this,p_ScreensContainer,p_StringsPool);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            throw new IOException(e.getMessage());
        }
    }

    public boolean needSMSMechanism()
    {
        if (i_defaultSMSnum!=0xFFFF) return true;
        for(int li=0;li<p_ScreensContainer.ap_Screens.length;li++)
        {
            Screen p_scr = p_ScreensContainer.ap_Screens[li];
            for(int lx=0;lx<p_scr.ap_Components.length;lx++)
            {
                ScreenComponent p_compo = p_scr.ap_Components[lx];
                if (p_compo.i_Type==ScreenComponent.COMPONENT_ITEM)
                {
                    if (p_compo.i_SMSnum!=0xFFFF) return true;
                }
            }
        }
        return false;
    }

    public void saveToStream(OutputStream _outStream, PrintStream _flagsStream) throws IOException
    {
        DataOutputStream p_dos = new DataOutputStream(_outStream);

        byte [] ab_stringsArray = p_StringsPool.toByteArray(lg_saveAsBytes);
        byte [] ab_compatibleTable = p_CompatibleRecordsContainer.toByteArray(p_StringsPool);
        byte [] ab_formsData = p_ScreensContainer.toByteArray(p_StringsPool);

//-------------------------header--------------------------
        // format number
        p_dos.writeByte(FORMAT_VERSION);

        System.out.println(lg_saveAsBytes ? "Strings saved as bytes" : "Strings saved as UTF8");

        // flags
        p_dos.writeByte(!lg_saveAsBytes ? FLAG_STRINGS_UTF8 : 0);

        // Validity date
        p_dos.writeLong(l_CatalogValidity);

        // Strings number
        p_dos.writeShort(p_StringsPool.getStringsNumber());

        // Client
        p_dos.writeShort(i_clientID);

        // URL
        p_dos.writeShort(i_url);

        // WAPORDER URL
        p_dos.writeShort(i_wapOrderURL);

        // SUPPORT PHONE
        p_dos.writeShort(i_supportPhone);

        // SUPPORT EMAIL
        p_dos.writeShort(i_supportEmail);

        // AGREEMENT
        p_dos.writeShort(i_AgreementID);

        // default sms num
        p_dos.writeShort(i_defaultSMSnum);

        // default cost
        p_dos.writeShort(i_defaultCost);

        // send2friend
        p_dos.writeShort(i_Send2Friend);

        // main advertisment text
        p_dos.writeShort(i_MainAdvertisment);

        // main advertisment validity
        p_dos.writeLong(l_MainAdvertismentValidity);

        // size of strings block
        if (ab_stringsArray.length>0xFFFF) throw new IOException("Too big strings pool, more than 0xFFFF elements");
        p_dos.writeShort(ab_stringsArray.length);

        // size of compatible table
        p_dos.writeShort(ab_compatibleTable.length/2);

        // size of forms data
        p_dos.writeShort(ab_formsData.length);


//----------------------------data section-------------------------
        // string table
        p_dos.write(ab_stringsArray);

        // compatible data
        p_dos.write(ab_compatibleTable);

        // forms data
        p_dos.write(ab_formsData);

        p_ResourcesContainer.saveToStream(p_dos,p_dos.size());

        System.out.println("Strings array length: "+ab_stringsArray.length);
        System.out.println("Compatible array length: "+ab_compatibleTable.length);
        System.out.println("Forms array length: "+ab_formsData.length);

        if (_flagsStream!=null)
        {
            // Пишем флаги
            if (lg_HasHTTPResources)
                _flagsStream.println("//#global RSRC_HTTP = true");
            else
                _flagsStream.println("//#global RSRC_HTTP = false");

            if (lg_HasJARResources)
                _flagsStream.println("//#global RSRC_JAR = true");
            else
                _flagsStream.println("//#global RSRC_JAR = false");

            if (hasFileResources())
                _flagsStream.println("//#global RSRC_FILE = true");
            else
                _flagsStream.println("//#global RSRC_FILE = false");

            if (lg_saveAsBytes)
                _flagsStream.println("//#global STR_UTF8 = false");
            else
                _flagsStream.println("//#global STR_UTF8 = true");

            if (lg_EnableSend2Friend)
                _flagsStream.println("//#global SEND2FRIEND = true");
            else
                _flagsStream.println("//#global SEND2FRIEND = false");

            if (i_MainAdvertisment==0xFFFF)
                _flagsStream.println("//#global MAINADVERTISMENT = false");
            else
                _flagsStream.println("//#global MAINADVERTISMENT = true");

            if(p_ScreensContainer.containsSMSServicesWithMacroses())
                _flagsStream.println("//#global SMSSERVICEPARSER = true");
            else
                _flagsStream.println("//#global SMSSERVICEPARSER = false");

            if (s_GameName!=null)
                _flagsStream.println("//#global GAMEMODE=\""+s_GameName+'\"');
            else
                _flagsStream.println("//#global GAMEMODE=\"\"");

            if (needSMSMechanism())
                _flagsStream.println("//#global SMSENGINE=true");
            else
                _flagsStream.println("//#global SMSENGINE=false");

            if (i_url!=0xFFFF)
                _flagsStream.println("//#global UPDATEURL=true");
            else
                _flagsStream.println("//#global UPDATEURL=false");

            if (i_AgreementID!=0xFFFF)
                _flagsStream.println("//#global AGREEMENT=true");
            else
                _flagsStream.println("//#global AGREEMENT=false");        }
    }
}
