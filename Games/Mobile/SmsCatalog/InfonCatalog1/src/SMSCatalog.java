import javax.microedition.lcdui.*;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.InputStream;

public class SMSCatalog
{
    public static final int CONTENTTYPE_NONE = 0;
    public static final int CONTENTTYPE_IMAGE = 3;
    public static final int CONTENTTYPE_GAME = 1;
    public static final int CONTENTTYPE_SOUND = 2;
    public static final int CONTENTTYPE_SMSSERVICE = 4;
    public static final int CONTENTTYPE_INFO = 5;

    private static final int OFFST_COMPONENTNAME = 1;
    private static final int OFFST_RESOURCEID = 3;
    private static final int OFFST_REFERENCEID = 5;
    private static final int OFFST_COSTID = 7;
    private static final int OFFST_SMSNUM = 9;
    private static final int OFFST_PREVURL = 11;
    private static final int OFFST_COMPATIBLETABLE = 14;
    private static final int OFFST_CONTENTTYPE = 13;

    private static final int COMPILER_VERSION = 0x12;
    private static final int FLAG_STRINGS_ASUTF8 = 1;

    public static final int OFFSET_MAINSCREEN = 0;

    private static final int SCREENFLAG_ITEMS = 1;
    private static final int SCREENFLAG_LINKS = 2;

    private static final int COMPONENT_LINK = 0;
    private static final int COMPONENT_CONTENT = 1;

    private byte[] ab_StringArray;
    private short [] ash_StringLinks;
    private byte[] ab_DataBlock;
    private short[] ash_CompatibleBlock;
    private int [] ai_ResourcesOffset;
    private short [] ash_linkedScreensOffsets;

    private static final int MAXLINKEDSCREENS = 256;

    private String s_CommonCost;
    private String s_CommonSMSnum;
    private String s_ClientID;
    private String s_CatalogURL;
    private String s_Send2Friend;

    public  int i_WapOrderUrl;
    public  int i_SupportPhones;
    public  int i_SupportEmail;

    public int i_AgreementText;

    private String s_Resource;

    private int i_Flags;

    public String s_MainAdvertisment;
    public long l_MainAdvertismentValidity;

    private int i_CurrentScreenOffset;
    private int i_CurrentScreenFlags;
    private int i_CurrentScreenDefaultContentType;

    public long l_ValidityCatalogDate;

    public final String getStringForIndex(int _index)
    {
        if (_index==0xFFFF) return null;

        try
        {
            int i_offset = ash_StringLinks[_index] & 0xFFFF;
            int i_len = ab_StringArray.length - i_offset;

            //#if STR_UTF8
            if ((i_Flags & FLAG_STRINGS_ASUTF8) != 0)
            {
                ByteArrayInputStream p_bas = new ByteArrayInputStream(ab_StringArray, i_offset, i_len);
                DataInputStream p_dis = new DataInputStream(p_bas);
                String s_str = p_dis.readUTF();
                p_dis.close();
                p_dis = null;
                p_bas = null;
                return s_str;
            }
            else
            //#endif
            {
                //#if !STR_UTF8
                StringBuffer p_strBuf = new StringBuffer(256);
                while (true)
                {
                    int i_char = ab_StringArray[i_offset++] & 0xFF;
                    if (i_char == 0) break;
                    if (i_char >= 0x80) i_char += 0x350;
                    p_strBuf.append((char) i_char);
                }
                return p_strBuf.toString();
                //#endif
            }
        }
        catch (Throwable _ex)
        {
            return null;
        }
    }

    public final String getCommonCost()
    {
        return s_CommonCost;
    }

    public final String getCommonSMSum()
    {
        return s_CommonSMSnum;
    }

    public final String getClientID()
    {
        return s_ClientID;
    }

    public final String getWapOrderURL()
    {
        if (i_WapOrderUrl==0xFFFF) return null;
        return getStringForIndex(i_WapOrderUrl);
    }

    public final String getSend2FriendNumber()
    {
        return s_Send2Friend;
    }

    public final String getSupportPhones()
    {
        if (i_SupportPhones==0xFFFF) return null;
        return getStringForIndex(i_SupportPhones);
    }

    public final String getSupportEmail()
    {
        if (i_SupportEmail==0xFFFF) return null;
        return getStringForIndex(i_SupportEmail);
    }

    public final String getURL()
    {
        return s_CatalogURL;
    }

    public final int getCurrentScreenOffset()
    {
        return i_CurrentScreenOffset;
    }

    public final void setCurrentScreenOffset(int _offset)
    {
        i_CurrentScreenOffset = _offset;
    }

    public final List makeScreenForOffset(int _screenOffset, boolean _translitMode)
    {
        i_CurrentScreenOffset = _screenOffset;
        int i_curIndex = _screenOffset;

        // ������������� ��������� ������
        int i_scrID = (ab_DataBlock[i_curIndex++] & 0xFF) << 8;
        i_scrID |= (ab_DataBlock[i_curIndex++] & 0xFF);

        // �����
        int i_flags = ab_DataBlock[i_curIndex++] & 0xFF;

        // ��� �������� �� ���������
        i_CurrentScreenDefaultContentType = ab_DataBlock[i_curIndex++];

        // ���������� ���������
        int i_components = ab_DataBlock[i_curIndex++] & 0xFF;

        //System.out.println("ScrID = "+i_scrID);
        //System.out.println("ScrFlags = "+i_flags);
        //System.out.println("ComponentsNumber = "+i_components);

        i_CurrentScreenFlags = i_flags;

        String s_str = getStringForIndex(i_scrID);
        String s_ScreenCaption = i_scrID == 0 ? "" : (_translitMode ? russian2translit(s_str) : s_str);

        //System.out.println("screen caption "+s_ScreenCaption);

        List p_list = new List(s_ScreenCaption, Choice.IMPLICIT);

        Image p_iconImg = null;
        if ((i_flags & SCREENFLAG_LINKS) != 0)
        {
            //System.out.println("Links screen ");
            // ����� ������
            p_iconImg = App.ap_Icons[1];
        }

        for (int li = 0; li < i_components; li++)
        {
            //System.out.println("Component #"+li);

            int i_indx = i_curIndex;
            int i_type = ab_DataBlock[i_curIndex++];
            int i_id = (ab_DataBlock[i_curIndex++] & 0xFF) << 8;
            i_id |= (ab_DataBlock[i_curIndex++] & 0xFF);

            //System.out.println("Type "+i_type);
            //System.out.println("ID "+i_id);

            s_str = getStringForIndex(i_id);
            String s_strID = _translitMode ? russian2translit(s_str) : s_str;

            //System.out.println("strID "+s_strID);

            switch (i_type)
            {
                case COMPONENT_LINK :
                {
                    int i_destScreen = (ab_DataBlock[i_curIndex++] & 0xFF) << 8;
                    i_destScreen |= (ab_DataBlock[i_curIndex++] & 0xFF);
                    //System.out.println("Dest screen = "+i_destScreen);
                    ash_linkedScreensOffsets[li] = (short) i_destScreen;
                }
                ;
                break;
                case COMPONENT_CONTENT:
                {
                    ash_linkedScreensOffsets[li] = (short) i_indx;

                    if ((i_flags & SCREENFLAG_ITEMS) != 0)
                    {
                        //System.out.println("Content screen ");
                        // ����� ��������
                        switch (getItemTypeForIndex(li))
                        {
                            case CONTENTTYPE_NONE :
                                p_iconImg = App.ap_Icons[0];
                                break;
                                //����
                            case CONTENTTYPE_GAME :
                                p_iconImg = App.ap_Icons[3];
                                break;
                                //�����
                            case CONTENTTYPE_SOUND :
                                p_iconImg = App.ap_Icons[2];
                                break;
                                //��������
                            case CONTENTTYPE_IMAGE :
                                p_iconImg = App.ap_Icons[4];
                                break;
                                //SMS ������
                            case CONTENTTYPE_SMSSERVICE :
                                p_iconImg = App.ap_Icons[5];
                                break;
                                // ����
                            case CONTENTTYPE_INFO :
                                p_iconImg = App.ap_Icons[6];
                                break;
                        }
                        if (p_iconImg==null) p_iconImg = App.ap_Icons[0];
                    }

                    // ������� ���������
                    i_curIndex+=2; // res id
                    i_curIndex+=2; // reference
                    i_curIndex+=2; // cost
                    i_curIndex+=2; // num
                    i_curIndex+=2; // preview
                    i_curIndex++; // content type

                    // ������� ������� �������������
                    int i_num = ab_DataBlock[i_curIndex++]&0xFF;
                    i_curIndex+=(i_num*2);
                }
                ;
                break;
            }

            p_list.append(s_strID, p_iconImg);
        }

        return p_list;
    }

    public final int getScreenOffsetForIndexedLink(int _index)
    {
        return ash_linkedScreensOffsets[_index] & 0xFFFF;
    }

    public final String getItemNameForIndex(int _index, boolean _translit)
    {
        int i_componentOffset = ash_linkedScreensOffsets[_index] & 0xFFFF;
        int i_str = ((ab_DataBlock[i_componentOffset + OFFST_COMPONENTNAME] & 0xFF) << 8);
        i_str |= (ab_DataBlock[i_componentOffset + OFFST_COMPONENTNAME + 1] & 0xFF);
        if (i_str==0xFFFF) return null;
        String s_str = getStringForIndex(i_str);
        String s_strID = s_str==null ? null : _translit ? russian2translit(s_str) : s_str;
        return s_strID;
    }

    public final String getItemResourceIDForIndex(int _index)
    {
        int i_componentOffset = ash_linkedScreensOffsets[_index] & 0xFFFF;
        int i_str = (ab_DataBlock[i_componentOffset + OFFST_RESOURCEID] & 0xFF) << 8;
        i_str |= (ab_DataBlock[i_componentOffset + OFFST_RESOURCEID + 1] & 0xFF);

        String s_strID = getStringForIndex(i_str);

        if (s_strID != null && App.s_DistributorChannel!=null)
        {
            s_strID = App.macroChange(s_strID,"%channel%",App.s_DistributorChannel);
        }

        return s_strID;
    }

    public final String getItemReferenceForIndex(int _index, boolean _translit)
    {
        int i_componentOffset = ash_linkedScreensOffsets[_index] & 0xFFFF;
        int i_str = (ab_DataBlock[i_componentOffset + OFFST_REFERENCEID] & 0xFF) << 8;
        i_str |= (ab_DataBlock[i_componentOffset + OFFST_REFERENCEID + 1] & 0xFF);
        String s_str = getStringForIndex(i_str);
        String s_strID = s_str!=null ? (_translit ? russian2translit(s_str) : s_str):null;
        return s_strID;
    }

    public final String getItemCostForIndex(int _index, boolean _translit)
    {
        int i_componentOffset = ash_linkedScreensOffsets[_index] & 0xFFFF;
        int i_str = (ab_DataBlock[i_componentOffset + OFFST_COSTID] & 0xFF) << 8;
        i_str |= (ab_DataBlock[i_componentOffset + OFFST_COSTID + 1] & 0xFF);

        String s_str = getStringForIndex(i_str);
        String s_strID = s_str == null ? null : (_translit ? russian2translit(s_str) : s_str);

        if (s_strID == null)
        {
            s_strID = getCommonCost();
            if (s_strID != null && _translit) s_strID = russian2translit(s_strID);
        }

        return s_strID;
    }

    public final int getItemTypeForIndex(int _index)
    {
        int i_componentOffset = ash_linkedScreensOffsets[_index] & 0xFFFF;
        int i_type = (ab_DataBlock[i_componentOffset + OFFST_CONTENTTYPE]);

        if (i_type<0) return i_CurrentScreenDefaultContentType;

        return i_type;
    }

    public final String getItemSMSnumForIndex(int _index)
    {
        int i_componentOffset = ash_linkedScreensOffsets[_index] & 0xFFFF;
        int i_str = (ab_DataBlock[i_componentOffset + OFFST_SMSNUM] & 0xFF) << 8;
        i_str |= (ab_DataBlock[i_componentOffset + OFFST_SMSNUM + 1] & 0xFF);

        String s_strID = getStringForIndex(i_str);

        if (s_strID == null)
        {
            s_strID = getCommonSMSum();
        }

        if (s_strID!=null && App.s_DistributorNumber!=null)
        {
            s_strID = App.macroChange(s_strID,"%number%",App.s_DistributorNumber);
        }
        return s_strID;
    }

    //#if RSRC_FILE
    public final byte [] getResourceForIndex(int _index)
    {
        try
        {
            int i_offset = ai_ResourcesOffset[_index];
            InputStream p_instr = this.getClass().getResourceAsStream(s_Resource);

            p_instr.skip(i_offset);

            int i_len = p_instr.read()<<8;
            i_len |= p_instr.read();

            byte [] ab_byte = new byte[i_len];

            readByteArrayFromInputStream(p_instr,ab_byte);

            try
            {
                if (p_instr!=null) p_instr.close();
            }
            catch (Throwable e)
            {
            }

            return ab_byte;
        }
        catch (Throwable e)
        {
           return null;
        }
    }
    //#endif

    public final int getItemPrevURLForIndex(int _index)
    {
        int i_componentOffset = ash_linkedScreensOffsets[_index] & 0xFFFF;

        int i_str = (ab_DataBlock[i_componentOffset + OFFST_PREVURL] & 0xFF) << 8;
        i_str |= (ab_DataBlock[i_componentOffset + OFFST_PREVURL + 1] & 0xFF);

        return i_str;
    }

    public final String getItemCompatibleModels(int _index)
    {
        int i_componentOffset = ash_linkedScreensOffsets[_index] & 0xFFFF;
        i_componentOffset += OFFST_COMPATIBLETABLE;

        int i_number = ab_DataBlock[i_componentOffset++]&0xFF;

        if (i_number==0) return null;

        StringBuffer p_strBuff= new StringBuffer(128);

        while(i_number!=0)
        {
            int  i_index = (ab_DataBlock[i_componentOffset++]&0xFF)<<8;
            i_index |= (ab_DataBlock[i_componentOffset++]&0xFF);

            i_index >>>=1;

            int i_vendor = ash_CompatibleBlock[i_index++]&0xFFFF;

            //System.out.println("vendor = "+i_vendor);

            int i_modelsNumber = ash_CompatibleBlock[i_index++]&0xFFFF;

            if (p_strBuff.length()!=0) p_strBuff.append('\n');

            p_strBuff.append(getStringForIndex(i_vendor));
            p_strBuff.append('\n');

            for(int li=0;li<i_modelsNumber;li++)
            {
                if (li!=0) p_strBuff.append(',');
                int i_model = ash_CompatibleBlock[i_index++]&0xFFFF;

                //System.out.println("model "+i_model);

                p_strBuff.append(getStringForIndex(i_model));
            }

            i_number--;
        }

        String s_str = p_strBuff.toString();
        p_strBuff = null;

        return s_str;
    }

    public final boolean isCurrentScreenLinksScreen()
    {
        return (i_CurrentScreenFlags & SCREENFLAG_LINKS) != 0;
    }

    public SMSCatalog(String _resource) throws Throwable
    {
        InputStream p_instr = this.getClass().getResourceAsStream(_resource);

        DataInputStream p_dis = null;
        i_CurrentScreenOffset = -1;
        ash_linkedScreensOffsets = new short[MAXLINKEDSCREENS];

        try
        {
            p_dis = new DataInputStream(p_instr);

            // ������ ������ �����������
            if (p_dis.readUnsignedByte() != COMPILER_VERSION) throw new Throwable("Bad compiler version");
            // �����
            i_Flags = p_dis.readUnsignedByte();

            // ���� ���������� ��������
            l_ValidityCatalogDate = p_dis.readLong();

            final boolean lg_StringsAsUTF = (i_Flags & FLAG_STRINGS_ASUTF8) != 0;

            // ���������� �����
            final int i_StringsNumber = p_dis.readUnsignedShort();

            // ������
            int i_clientid = p_dis.readUnsignedShort();

            // URL
            int i_url = p_dis.readUnsignedShort();

            // WAP ORDER URL
            i_WapOrderUrl = p_dis.readUnsignedShort();

            // SUPPORT PHONES
            i_SupportPhones = p_dis.readUnsignedShort();

            // SUPPORT EMAIL
            i_SupportEmail = p_dis.readUnsignedShort();

            // AGREEMENT
            i_AgreementText = p_dis.readUnsignedShort();

            // default SMS num
            int i_defSMSnum = p_dis.readUnsignedShort();

            // default cost
            int i_defCost = p_dis.readUnsignedShort();

            // Send2Friend
            int i_send2friend = p_dis.readUnsignedShort();

            // Main advertisment
            int i_mainAdvertisment = p_dis.readUnsignedShort();

            // Main advertisment validity
            l_MainAdvertismentValidity = p_dis.readLong();

            // Strings block size
            int i_stringsBlockSize = p_dis.readUnsignedShort();

            // Size of compatible table
            int i_CompatibleTableSize = p_dis.readUnsignedShort();

            // Size of forms data
            int i_FormsDataSize = p_dis.readUnsignedShort();

            //----------------------------

            // ������ ������
            ab_StringArray = new byte[i_stringsBlockSize];

            readByteArrayFromInputStream(p_dis,ab_StringArray);

            ash_StringLinks = new short[i_StringsNumber];
            //System.out.println("Strings number = "+i_StringsNumber);

            DataInputStream p_strin = null;
            ByteArrayInputStream p_strb = null;

            //#if STR_UTF8
            if (lg_StringsAsUTF)
            {
                p_strb = new ByteArrayInputStream(ab_StringArray);
                p_strin = new DataInputStream(p_strb);
            }
            //#endif

            // ����������� ��������
            int i_offst = 0;
            for (int li = 0; li < i_StringsNumber; li++)
            {
                //#if STR_UTF8
                if (lg_StringsAsUTF)
                {
                    ash_StringLinks[li] = (short) (i_stringsBlockSize - p_strb.available());
                    p_strin.readUTF();
                }
                else
                //#endif
                {
                    //#if !STR_UTF8
                    ash_StringLinks[li] = (short) i_offst;
                    while (true)
                    {
                        int i_val = ab_StringArray[i_offst++];
                        if (i_val == 0) break;
                    }
                    //#endif
                }
            }

            // ������� �������������
            ash_CompatibleBlock = new short[i_CompatibleTableSize];

            for(int li=0;li<i_CompatibleTableSize;li++)
            {
                short sh_data = p_dis.readShort();
                ash_CompatibleBlock[li] = sh_data;
            }

            // ������ ����
            ab_DataBlock = new byte[i_FormsDataSize];
            readByteArrayFromInputStream(p_dis,ab_DataBlock);

            // �������� ��������
            int i_sizeTable = p_dis.readShort();
            ai_ResourcesOffset = new int[i_sizeTable];

            for(int li=0;li<i_sizeTable;li++)
                ai_ResourcesOffset[li] = p_dis.readInt();

            s_CommonCost = getStringForIndex(i_defCost);
            s_CommonSMSnum = getStringForIndex(i_defSMSnum);

            if (i_send2friend==0xFFFF)
                s_Send2Friend = null;
            else
                s_Send2Friend = getStringForIndex(i_send2friend);

            if (i_mainAdvertisment ==0xFFFF)
                s_MainAdvertisment = null;
            else
                s_MainAdvertisment = getStringForIndex(i_mainAdvertisment);

            s_Resource = _resource;

            s_ClientID = getStringForIndex(i_clientid);

            if (i_url==0xFFFF)
                s_CatalogURL = null;
            else
                s_CatalogURL = getStringForIndex(i_url);
        }
        finally
        {
            try
            {
                if (p_instr != null) p_instr.close();
            }
            catch (Throwable e)
            {
            }

            try
            {
                if (p_dis != null) p_dis.close();
            }
            catch (Throwable e)
            {
            }

            p_dis = null;
            p_instr = null;
        }
        i_CurrentScreenOffset = 0;
    }

    public static void readByteArrayFromInputStream(InputStream _inStr, byte [] _array) throws Throwable
    {
        int i_pos = 0;
        int i_readedData = _array.length;

        while(i_readedData>0)
        {
            int i_readed = _inStr.read(_array,i_pos,i_readedData);
            if (i_readed<0) throw new Throwable("Wrong read");
            i_pos+=i_readed;
            i_readedData -= i_readed;
        }
    }

    public static String russian2translit(String _russianString)
    {
        int i_len = _russianString.length();
        StringBuffer p_strBuffer = new StringBuffer(i_len + 10);

        for (int li = 0; li < i_len; li++)
        {
            char ch_char = _russianString.charAt(li);
            if ((ch_char < '�' || ch_char > '�') && !(ch_char == '�' || ch_char == '�'))
            {
                p_strBuffer.append(ch_char);
                continue;
            }
            boolean lg_capital = false;
            if ((ch_char >= '�' && ch_char <= '�') || ch_char == '�')
            {
                ch_char = ch_char == '�' ? '�' + 32 : (char) (ch_char - '�' + '�');
                lg_capital = true;
            }
            else if (ch_char == '�') ch_char = '�' + 32;

            boolean lg_spec = false;
            char ch_resultChar = 0;
            String s_resultStr = null;

            switch (ch_char - '�')
            {
                case 0 : //�
                {
                    ch_resultChar = 'a';
                }
                ;
                break;
                case 1 : //�
                {
                    ch_resultChar = 'b';
                }
                ;
                break;
                case 2 : //�
                {
                    ch_resultChar = 'v';
                }
                ;
                break;
                case 3 : //�
                {
                    ch_resultChar = 'g';
                }
                ;
                break;
                case 4 : //�
                {
                    ch_resultChar = 'd';
                }
                ;
                break;
                case 5 : //�
                {
                    ch_resultChar = 'e';
                }
                ;
                break;
                case 6 : //�
                {
                    s_resultStr = "zh";
                }
                ;
                break;
                case 7 : //�
                {
                    ch_resultChar = 'z';
                }
                ;
                break;
                case 8 : //�
                {
                    ch_resultChar = 'i';
                }
                ;
                break;
                case 9 : //�
                {
                    s_resultStr = "i'";
                }
                ;
                break;
                case 10 : //�
                {
                    ch_resultChar = 'k';
                }
                ;
                break;
                case 11 : //�
                {
                    ch_resultChar = 'l';
                }
                ;
                break;
                case 12 : //�
                {
                    ch_resultChar = 'm';
                }
                ;
                break;
                case 13 : //�
                {
                    ch_resultChar = 'n';
                }
                ;
                break;
                case 14 : //�
                {
                    ch_resultChar = 'o';
                }
                ;
                break;
                case 15 : //�
                {
                    ch_resultChar = 'p';
                }
                ;
                break;
                case 16 : //�
                {
                    ch_resultChar = 'r';
                }
                ;
                break;
                case 17 : //�
                {
                    ch_resultChar = 's';
                }
                ;
                break;
                case 18 : //�
                {
                    ch_resultChar = 't';
                }
                ;
                break;
                case 19 : //�
                {
                    ch_resultChar = 'u';
                }
                ;
                break;
                case 20 : //�
                {
                    ch_resultChar = 'f';
                }
                ;
                break;
                case 21 : //�
                {
                    ch_resultChar = 'h';
                }
                ;
                break;
                case 22 : //�
                {
                    ch_resultChar = 'c';
                }
                ;
                break;
                case 23 : //�
                {
                    s_resultStr = "ch";
                }
                ;
                break;
                case 24 : //�
                {
                    s_resultStr = "sh";
                }
                ;
                break;
                case 25 : //�
                {
                    s_resultStr = "shc";
                }
                ;
                break;
                case 26 : //�
                {
                    ch_resultChar = '\'';
                    lg_spec = true;
                }
                ;
                break;
                case 27 : //�
                {
                    ch_resultChar = 'y';
                }
                ;
                break;
                case 28 : //�
                {
                    ch_resultChar = '\'';
                    lg_spec = true;
                }
                ;
                break;
                case 29 : //�
                {
                    s_resultStr = "e\"";
                }
                ;
                break;
                case 30 : //�
                {
                    s_resultStr = "u'";
                }
                ;
                break;
                case 31 : //�
                {
                    s_resultStr = "ya";
                }
                ;
                break;
                case 32 : //�
                {
                    s_resultStr = "yo";
                }
                ;
                break;
            }

            if (s_resultStr != null)
            {
                if (lg_capital && !lg_spec)
                {
                    // ������ ������ ������ �����������, ���� �� ����
                    char ch_c = s_resultStr.charAt(0);
                    ch_c = (char) (ch_c - '�' + '�');
                    p_strBuffer.append(ch_c);
                    p_strBuffer.append(s_resultStr.substring(1));
                }
                else
                {
                    p_strBuffer.append(s_resultStr);
                }
                s_resultStr = null;
            }
            else
            {
                if (lg_capital && !lg_spec)
                {
                    ch_resultChar = (char) (ch_resultChar - '�' + '�');
                }
                p_strBuffer.append(ch_resultChar);
            }
        }

        return p_strBuffer.toString();
    }

}
