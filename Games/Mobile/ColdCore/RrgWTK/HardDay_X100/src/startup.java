import javax.microedition.midlet.*;
import javax.microedition.lcdui.*;
import java.io.*;

import javax.microedition.rms.RecordStore;
import javax.microedition.rms.RecordStoreException;
import javax.microedition.rms.RecordStoreFullException;
import java.io.*;
import java.io.*;
import javax.microedition.lcdui.Image;
import java.io.IOException;
import java.io.InputStream;
import java.io.DataInputStream;
import javax.microedition.lcdui.Image;
import java.io.DataInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;

import javax.microedition.lcdui.*;
import java.io.DataInputStream;

import java.io.IOException;
import java.io.InputStream;
import java.io.DataInputStream;
import com.samsung.util.AudioClip;

import java.io.IOException;
import java.util.Vector;
public class startup extends MIDlet implements Runnable, CommandListener
{


  public int i_realScreenWidth,i_realScreenHeight;

    protected class insideCanvas extends Canvas
    {
        protected boolean lg_painted;

        protected int i_xoffst,i_yoffst;

        public insideCanvas()
        {
            super();

            i_realScreenWidth = getWidth();
            i_realScreenHeight = getHeight();

            i_xoffst = (i_realScreenWidth - SCREENWIDTH) >> 1;
            i_yoffst = (i_realScreenHeight - SCREENHEIGHT) >> 1;

            i_xoffst = 0;
            i_yoffst = 0;

            lg_painted = false;
            lg_firstPaint = true;
        }

        protected void keyPressed(int i)
        {
            KeyPressed(i);
        }

        protected void keyReleased(int i)
        {
            KeyReleased(i);
        }

        protected void keyRepeated(int i)
        {

        }


        protected void paint(Graphics _graphics)
        {
            if (lg_firstPaint)
            {
                _graphics.setColor(BACKGROUND_COLOR);
                _graphics.fillRect(0, 0, i_realScreenWidth, i_realScreenHeight);
            }

            {
              _graphics.translate(-_graphics.getTranslateX(),
                                  -_graphics.getTranslateY());
              _graphics.setClip(0,0,SCREENWIDTH,SCREENHEIGHT);
            }




            lg_painted = true;

     paintOnBuffer(_graphics);
            lg_painted = false;


            lg_firstPaint = false;
        }
        protected void showNotify()
        {
           lg_firstPaint = true;
           if(p_Player != null)
             p_Player.i_buttonState = 0; 
        }
    }




      private static final int SCREENWIDTH = 128;
      private static final int SCREENHEIGHT = 128;


    private static final int KEY_MENU = 42;
    private static final int KEY_RIGHT = Canvas.KEY_NUM6;
    private static final int KEY_LEFT = Canvas.KEY_NUM4;
    private static final int KEY_UP = Canvas.KEY_NUM2;
    private static final int KEY_DOWN = Canvas.KEY_NUM8;
    private static final int KEY_FIRE = Canvas.KEY_NUM5;
    private static final int KEY_CANCEL = 35;
    private static final int KEY_ACCEPT = 42;
    private static final int KEY_BACK = -10;

    private static final int KEY_0 = Canvas.KEY_NUM0;
    private static final int KEY_1 = Canvas.KEY_NUM1;
    private static final int KEY_2 = Canvas.KEY_NUM2;
    private static final int KEY_3 = Canvas.KEY_NUM3;
    private static final int KEY_4 = Canvas.KEY_NUM4;
    private static final int KEY_5 = Canvas.KEY_NUM5;
    private static final int KEY_6 = Canvas.KEY_NUM6;
    private static final int KEY_7 = Canvas.KEY_NUM7;
    private static final int KEY_8 = Canvas.KEY_NUM8;
    private static final int KEY_9 = Canvas.KEY_NUM9;

private void playTone(int _freq, long _length, boolean _blocking )
    {
    }

private void setDisplayLight(boolean _turnon)
    {
        try
        {
           if(com.samsung.util.LCDLight.isSupported())
           {
               if(_turnon)
                  com.samsung.util.LCDLight.on(0xfffff);
               else
                    com.samsung.util.LCDLight.off();
           }
        } catch (Exception e){}

    }

private void activateVibrator(long duration)
    {
        try
        {
            if(com.samsung.util.Vibration.isSupported())
            {
          com.samsung.util.Vibration.start((int) duration, 3);
            }
        } catch (Exception e){}
    }


    private int GDS_MAX_TOPRECORDS;
    private int GDS_MAX_SAVEDRECORDS;
    private int GDS_MAX_CHARSINSAVENAME;
    private static final int GDS_MAXNAMESIZE = 8;

    private static final int GDS_FIELD_VIBRATOR = 0;

    private static final int GDS_FIELD_LIGHT = 1;

    private static final int GDS_FIELD_SOUND = 2;

    private static final int GDS_FIELD_GAMEOPTION0 = 3;

    private static final int GDS_FIELD_GAMEOPTION1 = 4;

    private static final int GDS_FIELD_GAMEOPTION2 = 5;

    private static final int GDS_FIELD_LANGUAGE = 6;

    private int gds_i_booleanflags;
    private int gds_i_language;

    private String[] gds_as_TopNames;
    private int[] gds_ai_TopScores;
    private String[] gds_as_SavedGamesNames;
    private boolean gds_lg_isChanged;

    private String gds_s_gameid;
    private int gds_i_maxgamerecordsize;

    private static final int GDS_RECORD_OPTIONRECORD = 1;
    private static final int GDS_RECORD_FIRSTSAVEDRECORD = 2;


    protected boolean gds_lg_RMSisEnabled = true;



    private void InitGameDataStorage(String _gameID,int _maxsaveddata,int _maxcharinname,int _maxsavedgames,int _maxrecordsintoplist) throws Exception
    {
        GDS_MAX_CHARSINSAVENAME = _maxcharinname;
        GDS_MAX_SAVEDRECORDS = _maxsavedgames;
        GDS_MAX_TOPRECORDS = _maxrecordsintoplist;
        gds_s_gameid = _gameID;
        gds_as_SavedGamesNames = new String[GDS_MAX_SAVEDRECORDS];
        gds_as_TopNames = new String[GDS_MAX_TOPRECORDS+1];
        gds_ai_TopScores = new int[GDS_MAX_TOPRECORDS+1];
        gds_lg_isChanged = false;
        gds_i_maxgamerecordsize = _maxsaveddata;

        try
        {
            RecordStore p_optionstore = RecordStore.openRecordStore(gds_s_gameid,true);

            if (p_optionstore.getNumRecords() != 0)
            {
                byte[] ab_optionrecord = p_optionstore.getRecord(GDS_RECORD_OPTIONRECORD);
                DataInputStream p_dis = new DataInputStream(new ByteArrayInputStream(ab_optionrecord));

                gds_i_booleanflags = p_dis.readUnsignedByte();
                gds_i_language = p_dis.readByte();
                GDS_MAX_SAVEDRECORDS = p_dis.readUnsignedByte();

                for (int li = 0;li < GDS_MAX_TOPRECORDS;li++)
                {
                    String s_name = p_dis.readUTF();
                    int i_score = p_dis.readInt();
                    if (s_name.length() == 0)
                    {
                           s_name = null;
                           i_score = -1;
                    }
                    gds_as_TopNames[li] = s_name;
                    gds_ai_TopScores[li] = i_score;
                }

                for (int li = 0;li < GDS_MAX_SAVEDRECORDS;li++)
                {
                    String s_name = p_dis.readUTF();
                    if (s_name.length() == 0) s_name = null;
                    gds_as_SavedGamesNames[li] = s_name;
                }

                p_dis.close();
                p_dis = null;
                p_optionstore.closeRecordStore();
                p_optionstore = null;
                Runtime.getRuntime().gc();
            }
            else
            {
                gds_setBooleanOption(GDS_FIELD_LIGHT,true);
                gds_setBooleanOption(GDS_FIELD_VIBRATOR,true);
                gds_setBooleanOption(GDS_FIELD_SOUND,true);
                gds_setIntOption(GDS_FIELD_LANGUAGE,-1);

                int i_optionlen = gds_calculateOptionRecordLength();
                int i_maxrecords = (p_optionstore.getSizeAvailable() - i_optionlen - 16) / (gds_i_maxgamerecordsize + 16);
                if (i_maxrecords < GDS_MAX_SAVEDRECORDS) GDS_MAX_SAVEDRECORDS = i_maxrecords;

                byte[] ab_bytearr = gds_packOptionToByteArray();
                p_optionstore.addRecord(ab_bytearr,0,ab_bytearr.length);
                ab_bytearr = null;
                Runtime.getRuntime().gc();
                ab_bytearr = new byte[gds_i_maxgamerecordsize + 16];
                for (int li = 0;li < GDS_MAX_SAVEDRECORDS;li++)
                {
                    p_optionstore.addRecord(ab_bytearr,0,ab_bytearr.length);
                    Runtime.getRuntime().gc();
                }
                p_optionstore.closeRecordStore();
                p_optionstore = null;
                Runtime.getRuntime().gc();
            }
        }
        catch (Exception e)
        {
            gds_lg_RMSisEnabled = false;
        }
    }

    private int gds_calculateOptionRecordLength()
    {
        return GDS_MAX_SAVEDRECORDS * (GDS_MAX_CHARSINSAVENAME * 2 + 2) + GDS_MAX_TOPRECORDS * (GDS_MAX_CHARSINSAVENAME * 2 + 6) + 3;
    }

    private byte[] gds_packOptionToByteArray() throws Exception
    {
        int i_maxsize = gds_calculateOptionRecordLength();
        ByteArrayOutputStream p_baos = new ByteArrayOutputStream(i_maxsize);
        DataOutputStream p_dos = new DataOutputStream(p_baos);

        p_dos.writeByte(gds_i_booleanflags);
        p_dos.writeByte(gds_i_language);
        p_dos.writeByte(GDS_MAX_SAVEDRECORDS);

        for (int li = 0;li < GDS_MAX_TOPRECORDS;li++)
        {
            if (gds_as_TopNames[li] == null)
            {
                p_dos.writeUTF("");
            }
            else
            {
                p_dos.writeUTF(gds_as_TopNames[li]);
            }
            p_dos.writeInt(gds_ai_TopScores[li]);
        }

        for (int li = 0;li < GDS_MAX_SAVEDRECORDS;li++)
        {
            if (gds_as_SavedGamesNames[li] == null)
            {
                p_dos.writeUTF("");
            }
            else
            {
                p_dos.writeUTF(gds_as_SavedGamesNames[li]);
            }
        }
        p_dos.flush();
        p_dos = null;
        Runtime.getRuntime().gc();
        if (p_baos.size() > i_maxsize) throw new Exception("Option data are too long");
        int i_len = i_maxsize - p_baos.size();
        for (int li = 0;li < i_len;li++) p_baos.write(0);
        p_baos.flush();
        p_baos.close();
        return p_baos.toByteArray();
    }

    private DataInputStream gds_getSavedGameForIndex(int _index) throws Exception
    {
        DataInputStream p_dis = null;

        if(!gds_lg_RMSisEnabled) throw new Exception("Not enough free RMS");
        try
        {
            int i_indx = _index + GDS_RECORD_FIRSTSAVEDRECORD;
            RecordStore p_gamestore = RecordStore.openRecordStore(gds_s_gameid,false);
            byte[] ab_saveddata = new byte[0];
            ab_saveddata = p_gamestore.getRecord(i_indx);
            p_gamestore.closeRecordStore();
            p_gamestore = null;
            Runtime.getRuntime().gc();
            p_dis = new DataInputStream(new ByteArrayInputStream(ab_saveddata));
        }
        catch (RecordStoreException e)
        {
            throw new Exception(e.getMessage());
        }
        return p_dis;
    }

    private void gds_removeSavedGame(String _name) throws Exception
    {
        int i_index = gds_convertSavedNameToIndex(_name);
        if (i_index < 0) return;
        gds_as_SavedGamesNames[i_index] = null;

        gds_flush();
    }

    private void gds_removeAllSavedGames() throws Exception
    {
        for (int li = 0;li < GDS_MAX_SAVEDRECORDS;li++)
        {
            gds_as_SavedGamesNames[li] = null;
        }
        gds_flush();
    }

    private int gds_getTopListRecordNumber()
    {
        int i_cntr = 0;
        for (int li = 0;li < GDS_MAX_TOPRECORDS;li++)
        {
            if (gds_as_TopNames[li] != null) i_cntr++;
        }
        return i_cntr;
    }

    private void gds_clearTopList()
    {
        for (int li = 0;li < GDS_MAX_TOPRECORDS;li++)
        {
            if (gds_as_TopNames[li] != null)
            {
                gds_lg_isChanged = true;
            }
            gds_as_TopNames[li] = null;
            gds_ai_TopScores[li] = -1;
        }
    }

    private void gds_saveGameDataForName(String _name,byte[] _dataarray) throws Exception
    {
        int i_indx = gds_convertSavedNameToIndex(_name);
        int i_inactive;
        if (i_indx < 0)
            i_inactive = gds_getFirstEmptySavedGame();
        else
            i_inactive = i_indx;
        if (i_inactive < 0) throw new Exception("Disk full");

        gds_saveGameDataForIndex(i_inactive,_dataarray);
        gds_as_SavedGamesNames[i_inactive] = _name;
        gds_flush();
    }

    private void gds_saveGameDataForIndex(int _index,byte [] _dataarray) throws Exception
    {
        if(!gds_lg_RMSisEnabled) throw new Exception("Not enough free RMS");

        byte[] ab_buffer = _dataarray;
        if (_dataarray.length < gds_i_maxgamerecordsize)
        {
            ab_buffer = new byte[gds_i_maxgamerecordsize];
            for (int li = 0;li < _dataarray.length;li++) ab_buffer[li] = _dataarray[li];
        }

        int i_rmsrecord = _index + GDS_RECORD_FIRSTSAVEDRECORD;

        RecordStore p_store = RecordStore.openRecordStore(gds_s_gameid,false);
        p_store.setRecord(i_rmsrecord,ab_buffer,0,ab_buffer.length);
        p_store.closeRecordStore();

        ab_buffer = null;
        p_store = null;
        Runtime.getRuntime().gc();
    }

    private int gds_getSavedGamesCount()
    {
        int i_cnt = 0;
        for (int li = 0;li < GDS_MAX_SAVEDRECORDS;li++) if (gds_as_SavedGamesNames[li] != null) i_cnt++;
        return i_cnt;
    }

    private int gds_getFirstEmptySavedGame()
    {
        for (int li = 0;li < GDS_MAX_SAVEDRECORDS;li++) if (gds_as_SavedGamesNames[li] == null) return li;
        return -1;
    }

    private int gds_convertSavedNameToIndex(String _name)
    {
        for (int li = 0;li < GDS_MAX_SAVEDRECORDS;li++)
        {
            String s_name = gds_as_SavedGamesNames[li];
            if (s_name != null)
            {
                if (s_name.equals(_name))
                {
                    return li;
                }
            }
        }
        return -1;
    }

    private String gds_getSavedGameNameForIndex(int _index)
    {
        return gds_as_SavedGamesNames[_index];
    }

    private DataInputStream gds_getSavedGameForName(String _name) throws Exception
    {
        int i_indx = gds_convertSavedNameToIndex(_name);
        if (i_indx < 0)
            return null;
        else
            return gds_getSavedGameForIndex(i_indx);
    }

    private void gds_flush() throws Exception
    {
        if(!gds_lg_RMSisEnabled) return;

        byte[] ab_optionbyte = gds_packOptionToByteArray();

        try
        {
            RecordStore p_recordstore = RecordStore.openRecordStore(gds_s_gameid,false);
            p_recordstore.setRecord(GDS_RECORD_OPTIONRECORD,ab_optionbyte,0,ab_optionbyte.length);
            p_recordstore.closeRecordStore();
        }
        catch (RecordStoreException e)
        {
            throw new Exception(e.getMessage());
        }
        ab_optionbyte = null;
        gds_lg_isChanged = false;
        Runtime.getRuntime().gc();
    }

    private void gds_setBooleanOption(int _fieldId,boolean _value)
    {
        int i_cur = gds_i_booleanflags;
        if (_fieldId <= GDS_FIELD_GAMEOPTION2 && _fieldId >= 0)
        {
            if (_value)
            {
                gds_i_booleanflags |= (1 << _fieldId);
            }
            else
            {
                gds_i_booleanflags = gds_i_booleanflags & (~(1 << _fieldId));
            }
        }
        if (i_cur != gds_i_booleanflags) gds_lg_isChanged = true;
    }

    private boolean gds_getBooleanOption(int _fieldId)
    {
        if (_fieldId <= GDS_FIELD_GAMEOPTION2 && _fieldId >= 0)
        {
            if ((gds_i_booleanflags & (1 << _fieldId)) != 0)
                return true;
            else
                return false;
        }
        else
            return false;
    }

    private void gds_addRecordToTopList(String _name,int _score)
    {
      int i=GDS_MAX_TOPRECORDS-1;
      for (;i>=0;i--)
        if (_score<gds_ai_TopScores[i]) break;
         else {
           gds_ai_TopScores[i+1]=gds_ai_TopScores[i];
           gds_as_TopNames[i+1]=gds_as_TopNames[i];
         }
       gds_ai_TopScores[i+1]=_score;
       gds_as_TopNames[i+1]=_name;
       if(i<GDS_MAX_TOPRECORDS) gds_lg_isChanged = true;

    }

    private int gds_getMinScoreInTopList()
    {
        return gds_ai_TopScores[GDS_MAX_TOPRECORDS-1];
    }

    private int gds_getMaxScoreInTopList()
    {
        if (gds_as_TopNames[0] != null) return gds_ai_TopScores[0]; else return -1;
    }

    private void gds_setIntOption(int _fieldId,int _value)
    {
        if (gds_getIntOption(_fieldId) != _value) gds_lg_isChanged = true;
        switch (_fieldId)
        {
            case GDS_FIELD_LANGUAGE:
                gds_i_language = _value;
        }
    }

    private int gds_getIntOption(int _fieldId)
    {
        switch (_fieldId)
        {
            case GDS_FIELD_LANGUAGE:
                return gds_i_language;
        }
        return -1;
    }

    private String gds_getTopListNameForIndex(int _index)
    {
        return gds_as_TopNames[_index];
    }

    private int gds_getTopListScoresForIndex(int _index)
    {
        return gds_ai_TopScores[_index];
    }

    private void gds_sortTopList()
    {
        for (int li = 0;li < GDS_MAX_TOPRECORDS;li++)
        {
            int i_elem = gds_ai_TopScores[li];
            if (gds_as_TopNames[li] == null) i_elem = -1;

            for (int lii = li;lii < GDS_MAX_TOPRECORDS;lii++)
            {
                int i_curelem = gds_ai_TopScores[lii];
                if (gds_as_TopNames[lii] == null) i_curelem = -1;

                if (i_curelem > i_elem)
                {
                    String s_akk = gds_as_TopNames[lii];
                    int i_akk = gds_ai_TopScores[lii];
                    gds_as_TopNames[lii] = gds_as_TopNames[li];
                    gds_as_TopNames[li] = s_akk;
                    gds_ai_TopScores[lii] = gds_ai_TopScores[li];
                    gds_ai_TopScores[li] = i_akk;
                }
            }
        }
    }

    private String[] lb_as_LanguageNames;
    private String[] lb_as_LanguageIDs;
    private String[] lb_as_LanguageResource;
    private int lb_i_CurrentLanguageIndex = -1;
    private String lb_s_PhoneNativeLanguage;
    private String[] lb_as_TextStringArray;

    private void changeResource(String _resourcename) throws IOException
    {
        Runtime.getRuntime().gc();

        InputStream is = getClass().getResourceAsStream(_resourcename);
        DataInputStream ds = new DataInputStream(is);
        int i_num = ds.readUnsignedByte();
        String[] as_new_array = new String[i_num];

        for (int li = 0;li < i_num;li++)
        {
            as_new_array[li] = ds.readUTF();

            if (as_new_array[li].length() == 0)
                as_new_array[li] = null;
            else
                as_new_array[li] = as_new_array[li].replace('~','\n');
            Runtime.getRuntime().gc();
        }
        lb_as_TextStringArray = null;
        lb_as_TextStringArray = as_new_array;
        ds.close();
        ds = null;
        is = null;
        Runtime.getRuntime().gc();
    }

    private void setLanguage(int _index) throws IOException
    {
        if (lb_i_CurrentLanguageIndex == _index || _index >= lb_as_LanguageNames.length || _index < 0) return;
        changeResource(lb_as_LanguageResource[_index]);
        lb_i_CurrentLanguageIndex = _index;
    }

    private int getIndexForID(String _lang_id)
    {
        _lang_id = _lang_id.trim().toLowerCase();
        for (int li = 0;li < lb_as_LanguageIDs.length;li++)
        {
            if (_lang_id.equals(lb_as_LanguageIDs[li])) return li;
        }
        return -1;
    }


    private void initLanguageBlock(String _language_list,int _language_id) throws IOException
    {
    try
      {
         lb_s_PhoneNativeLanguage = System.getProperty("microedition.locale");
         if(lb_s_PhoneNativeLanguage == null)
         {
              lb_s_PhoneNativeLanguage = "en";
         }
          else
              {
                     lb_s_PhoneNativeLanguage = lb_s_PhoneNativeLanguage.trim().toLowerCase().substring(0,2);
              }

        Runtime.getRuntime().gc();

        InputStream is = getClass().getResourceAsStream(_language_list);
        DataInputStream ds = new DataInputStream(is);

        int i_num = ds.readUnsignedByte();
        lb_as_LanguageNames = new String[i_num];
        lb_as_LanguageIDs = new String[i_num];
        lb_as_LanguageResource = new String[i_num];

        for (int li = 0;li < i_num;li++)
        {
            lb_as_LanguageNames[li] = ds.readUTF();
            lb_as_LanguageIDs[li] = ds.readUTF();
            lb_as_LanguageResource[li] = ds.readUTF();
            Runtime.getRuntime().gc();
        }
        ds.close();
        ds = null;
        is = null;

        if (_language_id < 0)
        {
            _language_id = getIndexForID(lb_s_PhoneNativeLanguage);
        }
        if (_language_id >= 0) setLanguage(_language_id); else setLanguage(0);

        Runtime.getRuntime().gc();
      }
       catch (Exception e) {throw new IOException();}
    }



     private byte[][] PNG_COMMON_PALETTE=null;
     int i_palette_index;

    private int crc32(int crc, byte b)
    {
        final int CRC_POLY = 0xEDB88320;
        int i_crc = (crc ^ b) & 0xFF;

        for (int lj = 0; lj < 8; lj++)
        {
            if ((i_crc & 1) == 0)
            {
                i_crc >>>= 1;
            }
            else
            {
                i_crc = (i_crc >>> 1) ^ CRC_POLY;
            }

        }
        crc = i_crc ^ (crc >>> 8);

        return crc;
    }

    private int crc32(int crc, int b)
    {
        crc = crc32(crc, (byte) (b >>> 24));
        crc = crc32(crc, (byte) (b >>> 16));
        crc = crc32(crc, (byte) (b >>> 8));
        crc = crc32(crc, (byte) b);
        return crc;
    }

    public Object getImageFromInputStream(DataInputStream _inputstream, boolean _readRawData) throws Exception
    {
        final int HEADER_IHDR = 0;
        final int HEADER_PLTE = 1;
        boolean attached_palette=false;
        final int HEADER_IDAT = 2;
        final int HEADER_tRNS = 3;

        int len = _inputstream.readUnsignedShort();
        int i_len = len;

        ByteArrayOutputStream p_baos = new ByteArrayOutputStream(i_len);
        DataOutputStream p_daos = new DataOutputStream(p_baos);

        p_daos.writeLong(0x89504E470D0A1A0Al);


        int i_ln = i_len;
        while (p_baos.size() < (i_ln - 12))
        {
            int i_crc = 0;
            int i_chunk = _inputstream.readUnsignedByte();
            switch (i_chunk)
            {
                case HEADER_IHDR:
                    {
                        int i_width = _inputstream.readUnsignedByte();
                        int i_height = _inputstream.readUnsignedByte();
                        int i_flag = _inputstream.readUnsignedByte();

                        int i_bpp = i_flag >> 4;
                        int i_color = i_flag & 0x07;

                        i_crc = 0x575e51f5;

                        i_crc = crc32(i_crc, i_width);
                        i_crc = crc32(i_crc, i_height);
                        i_crc = crc32(i_crc, (byte) i_bpp);
                        i_crc = crc32(i_crc, (byte) i_color);

                        p_daos.writeLong(0x0000000D49484452l);
                        p_daos.writeInt(i_width);
                        p_daos.writeInt(i_height);

                        p_daos.writeByte(i_bpp);
                        p_daos.writeByte(i_color);

                        p_daos.writeShort(0x0);
                        i_crc = crc32(i_crc, (byte) 0);
                        i_crc = crc32(i_crc, (byte) 0);

                        if ((i_flag & 0x08) == 0)
                        {
                            p_daos.writeByte(0);
                            i_crc = crc32(i_crc, (byte) 0);
                        }
                        else
                        {
                            p_daos.writeByte(1);
                            i_crc = crc32(i_crc, (byte) 1);
                        }
                    }
                    ;
                    break;
                case HEADER_IDAT:
                    {
            if(!attached_palette && PNG_COMMON_PALETTE!=null && PNG_COMMON_PALETTE.length > i_palette_index)
    {
                  p_daos.write(PNG_COMMON_PALETTE[i_palette_index]);
                  i_ln += PNG_COMMON_PALETTE[i_palette_index].length;
       attached_palette = true;
          }

                        i_len = _inputstream.readUnsignedByte();
                        if (i_len >= 0x80)
                        {
                            i_len = ((i_len & 0x7F) << 8) | _inputstream.readUnsignedByte();
                        }
                        p_daos.writeInt(i_len);
                        p_daos.writeInt(0x49444154);

                        i_crc = 0xca50f9e1;

                        for (int li = 0; li < i_len; li++)
                        {
                            byte b_byte = (byte) _inputstream.read();
                            p_daos.writeByte(b_byte);
                            i_crc = crc32(i_crc, b_byte);
                        }
                    }
                    ;
                    break;
                case HEADER_PLTE:
                    {
                        i_len = _inputstream.readUnsignedByte();
                        if (i_len >= 0x80)
                        {
                            i_len = ((i_len & 0x7F) << 8) | _inputstream.readUnsignedByte();
                        }
                        p_daos.writeInt(i_len);
                        p_daos.writeInt(0x504C5445);

                        i_crc = 0xb45776aa;

                        for (int li = 0; li < i_len; li++)
                        {
                            byte b_byte = (byte) _inputstream.read();
                            p_daos.writeByte(b_byte);
                            i_crc = crc32(i_crc, b_byte);
                        }
            attached_palette=true;

                    }
                    ;
                    break;
                case HEADER_tRNS:
                    {
            if(!attached_palette && PNG_COMMON_PALETTE!=null && PNG_COMMON_PALETTE.length > i_palette_index)
    {
                  p_daos.write(PNG_COMMON_PALETTE[i_palette_index]);
                  i_ln += PNG_COMMON_PALETTE[i_palette_index].length;
       attached_palette = true;
          }

                        i_len = _inputstream.readUnsignedByte();
                        if (i_len >= 0x80)
                        {
                            i_len = ((i_len & 0x7F) << 8) | _inputstream.readUnsignedByte();
                        }
                        p_daos.writeInt(i_len);
                        p_daos.writeInt(0x74524e53);

                        i_crc = 0xc9468f33;

                        for (int li = 0; li < i_len; li++)
                        {
                            byte b_byte = (byte) _inputstream.read();
                            p_daos.writeByte(b_byte);
                            i_crc = crc32(i_crc, b_byte);
                        }
                    }
                    ;
                    break;
            }
            p_daos.writeInt(i_crc ^ -1);
        }

        p_daos.writeLong(0x0000000049454E44);
        p_daos.writeInt(0xAE426082);
        p_daos.flush();
        p_daos = null;

        byte [] p_byteArray = p_baos.toByteArray();
        p_baos.close();
        p_baos = null;

        if(_readRawData) return p_byteArray;

        Image p_newImage = Image.createImage(p_byteArray,0,p_byteArray.length);
        p_byteArray = null;
     return p_newImage;
    }

    private Object[] ib_ImageArray;

    private void initImageBlock(String resource_name) throws Exception
    {
        Runtime.getRuntime().gc();
        InputStream dis = getClass().getResourceAsStream(resource_name);

        DataInputStream ds = new DataInputStream(dis);


         int num = ds.readUnsignedByte();


        long time_stamp = System.currentTimeMillis();

        ib_ImageArray = new Object[num];

        try
        {
            for (int li = 0;li < num;li++)
            {

                int i_imageflag = ds.readUnsignedByte();

                i_palette_index = 0;
                if (i_imageflag == 3)
                {
                    if (PNG_COMMON_PALETTE == null) PNG_COMMON_PALETTE = new byte[1][];
      PNG_COMMON_PALETTE[0] = new byte[ds.readUnsignedShort()]; 
                    ds.read(PNG_COMMON_PALETTE[0]);
      li--;
                    continue;
                }
                if (i_imageflag == 0x13)
                {
                    int amount = ds.readUnsignedByte();
                    if (PNG_COMMON_PALETTE == null || PNG_COMMON_PALETTE.length < amount)
                         PNG_COMMON_PALETTE = new byte[amount][];
                    for(int ldi = 0; ldi < amount; ldi++)
                    {
        PNG_COMMON_PALETTE[ldi] = new byte[ds.readUnsignedShort()]; 
                      ds.read(PNG_COMMON_PALETTE[ldi]);
                    }
      li--;
                    continue;
                }
                if ((i_imageflag & 0x10)!=0)
                {
                    i_palette_index = ds.readUnsignedByte();
                    i_imageflag &= ~0x10;
                }

                int i_imageindx = ds.readUnsignedByte();

                if (i_imageflag == 1)
                {
                    Short p_byte = new Short((short)ds.readUnsignedByte());
                    ib_ImageArray [i_imageindx] = p_byte;
                    continue;
                }

                ib_ImageArray [i_imageindx] = null;

                try
                {
                    if ((i_imageflag & 4)!=0)
                    {
                      ds.readUnsignedShort(); 
                    }



                    if ((i_imageflag & 0x20) != 0)
                    {


                       if ((i_imageflag & 0x23) == 0x23)
                       {
                         ib_ImageArray [i_imageindx] = new String(
                              resource_name.substring(0,resource_name.lastIndexOf('/'))
                              +"/"+i_imageindx+".png"
                         );

                       }


                       else
                       {
                         int i_len = ds.readUnsignedShort();
                         byte [] p_byte = new byte[i_len];
                         ds.read(p_byte);

                         if ((i_imageflag & 2)!=0)
                         {
                            ib_ImageArray [i_imageindx] = p_byte;
                         }
                         else
                         {
                            ib_ImageArray [i_imageindx] = Image.createImage(p_byte,0,p_byte.length);
                         }
                         p_byte = null;
                       }
                    }

                    else

                    if ((i_imageflag & 2)!=0)
                    {
                        ib_ImageArray [i_imageindx] = getImageFromInputStream(ds,true);
                    }
                    else
                    {
                        ib_ImageArray [i_imageindx] = getImageFromInputStream(ds,false);
                    }
                }
                catch(Exception ex)
                {
                }
                Runtime.getRuntime().gc();

                i_itemsLoaded++;

                if(System.currentTimeMillis() - time_stamp  > 250 )
                {
                     time_stamp = System.currentTimeMillis();
                     paintOnBuffer();
                }

            }

            for(int li=0;li<num;li++)
            {
                if (ib_ImageArray[li] instanceof Short)
                {
                    int i_indx = ((Short)ib_ImageArray[li]).shortValue();
                    ib_ImageArray[li] = ib_ImageArray[i_indx];

                }
            }
            Runtime.getRuntime();
        }
        finally
        {
            if (dis != null)
            {
                try
                {
                    dis.close();
                }
                catch (IOException e)
                {
                }
                dis = null;
            }
            ds = null;
            PNG_COMMON_PALETTE=null;
            Runtime.getRuntime().gc();
        }
    }

    private Image getImage(int index)
    {

      Object obj = ib_ImageArray[index];
      if(obj instanceof Image) return (Image)obj;
      if(obj instanceof String)
      {

           try{
              return Image.createImage((String) obj);
           } catch(Exception e)
             {
               return Image.createImage(1,1);
             }
      }
      return Image.createImage((byte[])obj,0,((byte[])obj).length);
    }



    private static final int SCREEN_FLAG_NONE = 0;
    private static final int SCREEN_FLAG_ONEXIT = 1;
    private static final int SCREEN_FLAG_CUSTOMSCREEN = 2;

    private static final int ITEM_FLAG_NONE = 0;
    private static final int ITEM_FLAG_OPTIONAL = 1;

    private static final int ITEM_MENUITEM = 1;
    private static final int ITEM_TEXTBOX = 2;
    private static final int ITEM_IMAGE = 4;
    private static final int ITEM_CUSTOM = 8;
    private static final int ITEM_DELIMITER = 16;

    private static final int ALIGN_CENTER = 0;
    private static final int ALIGN_LEFT = 1;
    private static final int ALIGN_RIGHT = 2;

    private static final int COMMAND_FLAG_STATIC = 0;
    private static final int COMMAND_FLAG_OPTIONAL = 1;
    private static final int COMMAND_BACK = 0;
    private static final int COMMAND_USER = 1;

    private static final int MAX_ITEMS_NUMBER_PER_SCREEN = 32;
    private static final int MAX_COMMANDS_NUMBER_PER_SCREEN = 16;

    private short[] ash_menuArray;
    private short[] ash_screenStack;
    private int i_MenuStackPointer;
    private short[] ash_itemIdArray;
    private short[] ash_itemLinkScreen;
    private short[] ash_commandDecode;

    protected Displayable p_Form;

    private int i_currentScreenFlags;
    private int currentScreenId;
    private int currentErrorScreen;
    private int currentOkScreen;

    private int i_lastListItemSelected;

    private Displayable p_oldCanvas;
    private boolean lg_menuActive;

    public synchronized final void commandAction(Command _command, Displayable _displayable)
    {
        int i_type = _command.getCommandType();
        int i_cmndId = ash_commandDecode[_command.getPriority()];
        switch (i_type)
        {
            case Command.BACK:
                {
                    if (back())
{
initState(i_prevMIDletState);
}
                    return;
                }
            case Command.SCREEN:
                {
                    if ((i_currentScreenFlags & SCREEN_FLAG_CUSTOMSCREEN) != 0)
                    {
                        List p_list = (List) p_Form;
                        int i_selectedItemIndex = p_list.getSelectedIndex();
                        processListItem(currentScreenId, i_selectedItemIndex);
                    }
                    else
                    {
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
                            i_lastListItemSelected = i_itemId;
                            initScreen(i_linkedScreen);
                        }
                    }
                }
                ;
                break;
            case Command.ITEM:
                {
                    int i_selectedIndex = -1;
                    if (p_Form instanceof List)
                    {
                        i_selectedIndex = ((List) p_Form).getSelectedIndex();
                    }
                    processCommand(p_Form,currentScreenId, i_cmndId, i_selectedIndex);
                }
                ;
                break;
        }
    }


    private void initMenuBlock(String _menuResource,Displayable _background) throws Exception
    {
        lg_menuActive = true;
        p_oldCanvas = _background;
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

    private void deactivateMenu()
    {
        lg_menuActive = false;
        clearScreenStack();
    }

    private void reinitScreen(boolean _force)
    {
        if (i_MenuStackPointer < 0) return;

        if (currentScreenId >= 0)
        {
            if (p_Form != null && !_force)
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

    private void initScreen(int _screenPointer)
    {
        int i_screenId = _screenPointer;
        closeCurrentScreen();

        int i_temp = ash_menuArray[_screenPointer++];
        int i_ScreenFlags = i_temp >>> 8;
        int i_CaptionId = i_temp & 0xFF;

        int i_itemTypesFlag = ash_menuArray[_screenPointer++];

        String s_ScreenCaption = lb_as_TextStringArray[i_CaptionId];

        int i_ErrorScreen = ash_menuArray[_screenPointer++];
        int i_OkScreen = ash_menuArray[_screenPointer++];

        int i_ItemNumber = ash_menuArray[_screenPointer++];
        boolean lg_isForm = false;

        if ((i_ScreenFlags & SCREEN_FLAG_CUSTOMSCREEN) != 0)
        {
            p_Form = customScreen(i_screenId);
            if (p_Form instanceof Form) lg_isForm = true;
        }
        else
        {
            if ((i_itemTypesFlag & ITEM_MENUITEM) != 0)
            {
                p_Form = new List(s_ScreenCaption, List.IMPLICIT);
            }
            else
            {
                p_Form = new Form(s_ScreenCaption);
                lg_isForm = true;
            }

            int i_itemIndex = 0;
            for (int li = 0; li < i_ItemNumber; li++)
            {

                int i_tmp = ash_menuArray[_screenPointer++];
                int i_ItemId = i_tmp >>> 8;
                int i_Align = i_tmp & 0xFF;

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
                            if (lg_isForm)
                            {
                                Object p_custItem = customItem(i_screenId, i_ItemId, false);
                                if (p_custItem instanceof String)
                                    ((Form) p_Form).append((String)p_custItem);
                                else
                                    ((Form) p_Form).append((Item)p_custItem);
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
                    case ITEM_MENUITEM:
                        {
                            String s_String = lb_as_TextStringArray[i_StringId];
                            Image p_Image = null;
                            if (i_ImageId>=0) p_Image = (Image) ib_ImageArray[i_ImageId];
                            ((List) p_Form).append(s_String, p_Image);
                        }
                        ;
                        break;
                    case ITEM_TEXTBOX:
                        {
                            String s_string = null;
                            if (i_StringId >= 0) s_string = lb_as_TextStringArray[i_StringId];
                            StringItem p_stringItem = new StringItem(null, s_string);
                            ((Form) p_Form).append(p_stringItem);
                        }
                        ;
                        break;
                }
                ash_itemLinkScreen[i_itemIndex] = (short) i_LinkScreen;
                ash_itemIdArray[i_itemIndex++] = (short) i_ItemId;
            }
        }
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

            String s_commandString = lb_as_TextStringArray[i_StringId];

            Command p_command = null;
            if (i_CommandType == COMMAND_BACK)
                p_command = new Command(s_commandString, Command.BACK, i_cmndnum);
            if (i_CommandType == COMMAND_USER)
                p_command = new Command(s_commandString, Command.ITEM, i_cmndnum);

            ash_commandDecode[i_cmndnum++] = (short) i_CmndId;

            p_Form.addCommand(p_command);
        }

        currentScreenId = i_screenId;
        ash_screenStack[++i_MenuStackPointer] = (short) currentScreenId;

        currentErrorScreen = i_ErrorScreen;
        currentOkScreen = i_OkScreen;
        i_currentScreenFlags = i_ScreenFlags;

        p_Display.setCurrent(p_Form);
        p_Form.setCommandListener(this);

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

    private void clearScreenStack()
    {
        closeCurrentScreen();
        i_MenuStackPointer = -1;
        Displayable p_curCanvas = p_Display.getCurrent();
        if (p_curCanvas == null || !p_curCanvas.equals(p_oldCanvas))
        {
            p_Display.setCurrent(p_oldCanvas);
        }
        Runtime.getRuntime().gc();
    }

    private int getPreviousScreenID()
    {
        int i_indx = i_MenuStackPointer - 1;
        if (i_indx < 0)
            return -1;
        else
            return ash_screenStack[i_indx];
    }

    private void viewOkScreen()
    {
        if (currentOkScreen < 0) return;
        initScreen(currentOkScreen);
    }

    private void viewErrorScreen()
    {
        if (currentErrorScreen < 0) return;
        initScreen(currentErrorScreen);
    }

    private boolean back()
    {
        if (i_MenuStackPointer < 0)
            return true;
        else if (i_MenuStackPointer == 0)
        {
            clearScreenStack();
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

    private boolean back(int _depth)
    {
        int i_newsp = i_MenuStackPointer - _depth;
        if (i_newsp < 0)
        {
            clearScreenStack();
            return true;
        }
        else
        {
            closeCurrentScreen();
            int i_prevScreen = ash_screenStack[i_newsp];
            i_MenuStackPointer = i_newsp;
            initScreen(i_prevScreen);
            i_MenuStackPointer--;
            return false;
        }
    }



    protected Melody[] ap_melody;



    private Vector _samsung_melodies_vector = new Vector();

    public void _initSoundBlock()
    {

    }

    public void playMelody(Melody _melody, boolean _blocking)
    {
           if(!lg_SoundEnabled) return;

        AudioClip p_Player = (AudioClip) _melody.p_NativeFormat;
        p_Player.stop();
        p_Player.play(1, 5);

        if(_blocking)
        {
           try{
              Thread.sleep(_melody.i_MelodyLength);
           }catch(Exception e){}
        }

    }

    public void playMelody(Melody _melody, int iterations, boolean _blocking)
    {
           if(!lg_SoundEnabled) return;

        AudioClip p_Player = (AudioClip) _melody.p_NativeFormat;
        p_Player.stop();
        p_Player.play(iterations, 5);
    }



    public void stopAllMelodies()
    {
        synchronized (_samsung_melodies_vector)
        {
            for (int li = 0; li < _samsung_melodies_vector.size(); li++)
            {
                Melody p_melody = (Melody) _samsung_melodies_vector.elementAt(li);
                AudioClip p_clip = (AudioClip) p_melody.p_NativeFormat;
                p_clip.stop();
            }
        }
    }

    public void stopMelody(Melody _melody)
    {
        AudioClip p_Player = (AudioClip) _melody.p_NativeFormat;
        p_Player.stop();
    }

    public void convertMelody(Melody _melody) throws Exception
    {

        byte[] ab_array = _melody.getMelodyArray();

        int i_type;

        switch (_melody.i_MelodyType)
        {
            case Melody.MELODY_TYPE_MIDI:
                i_type = AudioClip.TYPE_MIDI;
                break;
            case Melody.MELODY_TYPE_MMF:
                i_type = AudioClip.TYPE_MMF;
                break;
            case Melody.MELODY_TYPE_MP3:
                i_type = AudioClip.TYPE_MP3;
                break;
            default :
                throw new IOException("Unknown melody type #"+_melody.i_MelodyType);
        }
        AudioClip p_Player = new AudioClip(i_type, ab_array, 0, ab_array.length);
        _melody.p_NativeFormat = p_Player;
        _melody.removeLoadedArray();

        _samsung_melodies_vector.addElement(_melody);
    }

    public void initSoundBlock(String resource_name) throws Exception
    {
        Runtime.getRuntime().gc();
        InputStream dis = getClass().getResourceAsStream(resource_name);

        DataInputStream ds = new DataInputStream(dis);
        int num = ds.readUnsignedByte();

        ap_melody = new Melody[num];

        for (int li = 0; li < num; li++)
        {
            ap_melody[li] = new Melody(li, ds);
            Runtime.getRuntime().gc();
        }

        for (int li = 0; li < num; li++) convertMelody(ap_melody[li]);
        if (dis != null)
        {
            try
            {
                dis.close();
            }
            catch (IOException e)
            {
            }
            dis = null;
        }
        ds = null;
        Runtime.getRuntime().gc();
        _initSoundBlock();
    }



    protected Image p_InsideDoubleBuffer;
    protected Graphics p_InsideDoubleBufferGraphics;
    private boolean lg_FrontPageView;
    private int i_gameTimeDelay;
    private int i_LastGameScores;
    private int i_GamePlayerState;

    private final static int PLAYER_STATE_NORMAL = 0;
    private final static int PLAYER_STATE_LOST = 1;
    private final static int PLAYER_STATE_WON = 2;

private MobileGameletImpl p_Gamelet;
    CAbstractPlayer [] ap_Players;
    Player p_Player;

    private String s_gameNameForSaving;
    private int i_gameIndexForRewriting;
    private boolean lg_gameIsStopped;


    private boolean lg_SoundEnabled;
    private boolean lg_BackLightEnabled;
    private boolean lg_MainThemeSound = true;

    private static final int BACKGROUND_COLOR = 0x000080;
    private static final int FONT_COLOR = 0x00FF00;

    private insideCanvas p_Canvas;
    private Display p_Display;
    private boolean lg_Active;
    private int i_Timer;
    protected int i_MIDletState = -1;
    private int i_prevMIDletState;
    private int i_LastUserCommand;

    private int i_itemsLoaded;
    private int i_totalItems = TOTAL_IMAGES_NUMBER + 30;

    private int i_offsetOfNamesForSave;

    private boolean lg_FirstRepaintAfterStateChanging;
    protected boolean lg_firstPaint;

    protected boolean lg_FinishImagePictured;
    private int i_selectedGameLevel;
    private boolean lg_startGameAfterDownloading;

    private Image logo;
    private Image menu_icon_image;

    protected static final int GAMESCREEN_WIDTH = SCREENWIDTH;
    protected static final int GAMESCREEN_HEIGHT = SCREENHEIGHT;




    protected static final int STATE_LOADING = 0;
    protected static final int STATE_FRONTPAGE = 1;
    protected static final int STATE_DEMOPLAY = 2;


    protected static final int STATE_GAMEPLAY = 4;
    protected static final int STATE_GAMEOVER = 5;
    protected static final int STATE_FINISHIMAGE = 6;
    protected static final int STATE_MENU = 7;

    private static final int TIMEDELAY_FRONTPAGE = 60*1000/200;  
    private static final int TIMEDELAY_FINISHIMAGE = 30*1000/200; 
    private static final int ANTICLICKDELAY_FINISHIMAGE = 10;
    private static final int TIMEDELAY_DEMOPLAY = 150;

    String s_EchoMessage="";



    protected void startApp() throws MIDletStateChangeException
    {
        if (i_MIDletState >= 0) return;


        lg_Active = true;
        i_Timer = 0;

        i_itemsLoaded = 0;

        p_Display = Display.getDisplay(this);
        p_Canvas = new insideCanvas();
        p_Display.setCurrent(p_Canvas);

        try
        {
        logo = Image.createImage("/res/logo.png");
        }catch(Exception e){logo=null;}

        try
        {
         menu_icon_image = Image.createImage("/res/menu_icon.png");
        }catch(Exception e){menu_icon_image = null;}

        initState(STATE_LOADING);


        p_Player = new Player(0);
        ap_Players = new CAbstractPlayer[]{p_Player};


p_Gamelet = new MobileGameletImpl();

        new Thread(this).start();
    }

    private void initState(int _state)
    {
        lg_FirstRepaintAfterStateChanging = true;
        lg_firstPaint = true;


         if (_state != STATE_LOADING) stopAllMelodies();
         i_action_sound = -1;
         _melody_priority = -1;
         _playing_melody = -1;

        i_prevMIDletState = i_MIDletState;
        i_MIDletState = _state;
        i_Timer = 0;


        switch (i_MIDletState)
        {
            case STATE_MENU:
                {
                    initScreen(SCR_MainMenuSCR);
                }
                ;
                break;
            case STATE_DEMOPLAY:
                {
                    i_selectedGameLevel = -1;
                    lg_startGameAfterDownloading = false;
                    startGame();
                }
                ;
                break;
            case STATE_FRONTPAGE:
                {
                 clearScreenStack();
                    lg_FinishImagePictured = false;
                }
                ;
                break;
            case STATE_FINISHIMAGE:
                {
                    lg_MainThemeSound = true;
                    clearScreenStack();
                    lg_FinishImagePictured = false;
                }
                ;
                break;
            case STATE_GAMEOVER:
                {
                    lg_MainThemeSound = false;
                    clearScreenStack();
                    initScreen(SCR_ViewGameScoreSCR);
                }
                ;
                break;
            case STATE_GAMEPLAY:
                {
                    clearScreenStack();
                }
                ;
                break;
            default:
                {
                    p_Display.setCurrent(p_Canvas);
                }
        }
        paintOnBuffer();
    }

    protected void destroyApp(boolean b) throws MIDletStateChangeException
    {
        destroy();
        notifyDestroyed();
    }

    protected void pauseApp()
    {
        notifyPaused();
    }

    public long i_timeStamp;
    public int i_showdeath_delay;

    public void run()
    {
        int i_timeDelay = 0;
        while (lg_Active)
        {
            i_timeStamp = System.currentTimeMillis();
            switch (i_MIDletState)
            {
                case STATE_LOADING:
                    {
                       s_EchoMessage="";
                        try
                        {
                          s_EchoMessage="initing world";
                            p_Gamelet.globalInitWorld();
                          s_EchoMessage="starting RMS";
InitGameDataStorage(p_Gamelet.getGameTextID(), p_Gamelet.getMaximumDataSize(), 8, 6, 10);
                          s_EchoMessage="processing options";
                            int i_lngindx = gds_getIntOption(GDS_FIELD_LANGUAGE);
                            lg_SoundEnabled = gds_getBooleanOption(GDS_FIELD_SOUND);
                            lg_BackLightEnabled = gds_getBooleanOption(GDS_FIELD_LIGHT);
                          s_EchoMessage="loading language set";
                            initLanguageBlock("/res/langs.bin", i_lngindx);
                            i_itemsLoaded = 10;
                            paintOnBuffer();
                          s_EchoMessage="building menu";
                            initMenuBlock("/res/menu.bin", p_Canvas);
                            i_itemsLoaded += 10;
                            paintOnBuffer();
                          s_EchoMessage="extract graphics";
                            initImageBlock("/res/images.bin");
                            prepareGraphics();
                          s_EchoMessage="loading sounds";
                            initSoundBlock("/res/sound.bin");
                            setSoundItemPriority();
                            i_itemsLoaded += 5;
                            paintOnBuffer();

                          s_EchoMessage="";

                            initState(STATE_FRONTPAGE);

       logo = null;

                            Runtime.getRuntime().gc();

                            continue;
                        }
                        catch (Exception e)
                        {
                            viewAlert("Loading error", "on stage:"+s_EchoMessage+"\n"+e.getClass().getName()+": "+e.getMessage()
                                      +"\nTotal memory: "+Runtime.getRuntime().totalMemory()
                                      +"\nFree memory:"+ Runtime.getRuntime().freeMemory(), AlertType.ERROR, Alert.FOREVER);
                            try
                            {
                                Thread.sleep(15000);
                            }
                            catch (InterruptedException e1)
                            {
                                return;
                            }
                            notifyDestroyed();
                            return;
                        }
                    }
                case STATE_DEMOPLAY:
                    {

                        if (i_Timer >= TIMEDELAY_DEMOPLAY
                            || p_Gamelet.m_iGameState != MobileGamelet.GAMEWORLDSTATE_PLAYED)
                        {
                            endGame();
                            initState(STATE_FRONTPAGE);
                            continue;
                        }


             final int keyset[] = {Canvas.LEFT,Canvas.RIGHT,Canvas.UP,Canvas.DOWN,Canvas.FIRE};

             if((i_Timer & 1) == 0)
             {
               int _key = p_Canvas.getKeyCode(keyset[p_Gamelet.getRandomInt(keyset.length-1)]);


        int i_state = p_Player.i_buttonState;

        switch(_key)
        {
            case Canvas.KEY_NUM1 :
                {
                    i_state |= MoveObject.BUTTON_UP;
                    i_state &= ~MoveObject.BUTTON_DOWN;
                    i_state |= MoveObject.BUTTON_LEFT;
                    i_state &= ~MoveObject.BUTTON_RIGHT;
                }
                ;
                break;

            case Canvas.KEY_NUM2 :
                {
                    i_state |= MoveObject.BUTTON_UP;
                    i_state &= ~MoveObject.BUTTON_DOWN;
                }
                ;
                break;

            case Canvas.KEY_NUM3 :
                {
                    i_state |= MoveObject.BUTTON_UP;
                    i_state &= ~MoveObject.BUTTON_DOWN;
                    i_state |= MoveObject.BUTTON_RIGHT;
                    i_state &= ~MoveObject.BUTTON_LEFT;
                }
                ;
                break;

            case Canvas.KEY_NUM4 :
                {
                    i_state |= MoveObject.BUTTON_LEFT;
                    i_state &= ~MoveObject.BUTTON_RIGHT;
                }
                ;
                break;

            case Canvas.KEY_NUM5 :
                {
                    i_state |= MoveObject.BUTTON_FIRE;
                }
                ;
                break;

            case Canvas.KEY_NUM6 :
                {
                    i_state |= MoveObject.BUTTON_RIGHT;
                    i_state &= ~MoveObject.BUTTON_LEFT;
                }
                ;
                break;

            case Canvas.KEY_NUM7 :
                {
                    i_state |= MoveObject.BUTTON_DOWN;
                    i_state &= ~MoveObject.BUTTON_UP;
                    i_state |= MoveObject.BUTTON_LEFT;
                    i_state &= ~MoveObject.BUTTON_RIGHT;
                }
                ;
                break;

            case Canvas.KEY_NUM8 :
                {
                    i_state |= MoveObject.BUTTON_DOWN;
                    i_state &= ~MoveObject.BUTTON_UP;
                }
                ;
                break;

            case Canvas.KEY_NUM9 :
                {
                    i_state |= MoveObject.BUTTON_DOWN;
                    i_state &= ~MoveObject.BUTTON_UP;
                    i_state |= MoveObject.BUTTON_RIGHT;
                    i_state &= ~MoveObject.BUTTON_LEFT;
                }
                ;
                break;

            default :
                switch (p_Canvas.getGameAction(_key))
                {
                   case Canvas.LEFT:
                       {
                           i_state |= MoveObject.BUTTON_LEFT;
                           i_state &= ~MoveObject.BUTTON_RIGHT;
                       }
                       ;
                       break;
                   case Canvas.RIGHT :
                       {
                           i_state |= MoveObject.BUTTON_RIGHT;
                           i_state &= ~MoveObject.BUTTON_LEFT;
                       }
                       ;
                       break;
                   case Canvas.UP :
                       {
                           i_state |= MoveObject.BUTTON_UP;
                           i_state &= ~MoveObject.BUTTON_DOWN;
                       }
                       ;
                       break;
                   case Canvas.DOWN :
                       {
                           i_state |= MoveObject.BUTTON_DOWN;
                           i_state &= ~MoveObject.BUTTON_UP;
                       }
                       ;
                       break;
                   case Canvas.FIRE :
                       {
                           i_state = MoveObject.BUTTON_FIRE;
                       }
                       ;
                       break;
                }
        }
        p_Player.i_buttonState = i_state;
             }



                    }
                case STATE_GAMEPLAY:
                    {

                        i_timeDelay = i_gameTimeDelay;
                        if (p_Gamelet.getGameWorldState() == MobileGamelet.GAMEWORLDSTATE_PLAYED)
                        {
                            synchronized (p_Gamelet)
                            {
                                p_Gamelet.nextGameStep();

                            }
                            paintOnBuffer();
                            i_timeDelay = Math.min(i_timeDelay,i_timeDelay - (int) (System.currentTimeMillis() - i_timeStamp));
                            if (i_timeDelay <= 0) i_timeDelay = 1;

                             if(p_Canvas.isShown() && i_MIDletState == STATE_GAMEPLAY)
                             {
                               if(i_action_sound < 0)
                               {
                               }
                               else
                               {
                                 playMelody(ap_melody[i_action_sound],false);
                                 i_action_sound = -1;
                               }
                             }

                        }
                        else
                        {
                            if(_playing_melody >= 0 && _last_actioncall <= i_timeStamp)
                            {
                                       stopMelody(ap_melody[_playing_melody]);
                                       _melody_priority = -1;
                                       _playing_melody = -1;
                            }

                            if(p_Gamelet.getWinningList()==null && ++i_showdeath_delay < 11)
                            {
                                   paintOnBuffer();
                            }
                            else

                            if (p_Gamelet.getGameWorldState() == MobileGamelet.GAMEWORLDSTATE_GAMEOVER)
                            {
                                i_LastGameScores = p_Player.m_iPlayerGameScores;
                                CAbstractPlayer [] ap_winningList = p_Gamelet.getWinningList();
                                i_GamePlayerState = (ap_winningList==null) ? PLAYER_STATE_LOST : PLAYER_STATE_WON;
                                i_showdeath_delay = 0;


                                endGame();
                                Runtime.getRuntime().gc();

                                if (i_MIDletState == STATE_DEMOPLAY)
                                {
                                    initState(STATE_FRONTPAGE);
                                }
                                else
                                {
                                    initState(STATE_FINISHIMAGE);
                                }
                                continue;
                            }

                            else
                            {
                                if(++i_showdeath_delay < 11)
                                {
                                     paintOnBuffer();
                                }
                                 else
                                     {
                                        i_showdeath_delay = 0;
                                        p_Player.i_buttonState = 0;
                                        p_Gamelet.resumeGameAfterPlayerLostOrPaused();
                                     }
                            }
                        }
                    }
                    ;
                    break;
                case STATE_FRONTPAGE:
                    {
                         if(lg_MainThemeSound && ap_melody!=null)
                         {
                            Thread.yield();
                            Melody theme = ap_melody[SND_START_MMF];
                            if(theme != null)
                            {
                               i_Timer -= theme.i_MelodyLength/200;
                               playMelody(theme,false);
                            }
                         }
                         lg_MainThemeSound = false;

                            if (i_Timer == TIMEDELAY_FRONTPAGE)
                            {
                                initState(STATE_DEMOPLAY);
                                continue;
                            }
                            else
                                i_timeDelay = 200;
                    }
                    ;
                    break;
                case STATE_FINISHIMAGE:
                    {
                         if(lg_MainThemeSound && ap_melody!=null)
                         {
                           Melody theme = null;

                           if (i_GamePlayerState == PLAYER_STATE_LOST)
                           {
                              theme = ap_melody[SND_FINAL_MMF];
                           }
                           else
                           {
                              theme = ap_melody[SND_FINAL_MMF];
                           }
                           if(theme != null)
                           {
                               i_Timer -= theme.i_MelodyLength/200;
                               playMelody(theme,false);
                           }
                         }
                         lg_MainThemeSound = false;

                            if (i_Timer == TIMEDELAY_FINISHIMAGE)
                            {
                                initState(STATE_GAMEOVER);
                                continue;
                            }
                            else
                                i_timeDelay = 200;
                    }
                    ;
                    break;
                case STATE_GAMEOVER:
                    {
                        i_timeDelay = 200;
                    }
                    ;
                    break;
                default:
                    {
                        i_timeDelay = 100;
                    }
            }

            try
            {
                Thread.sleep(i_timeDelay);
            }
            catch (InterruptedException e)
            {
                break;
            }

               if (lg_BackLightEnabled && (i_Timer & 0x1F) == 0 ) setDisplayLight(lg_BackLightEnabled);

            i_Timer++;
        }
    }

    protected void paintOnBuffer()
    {
        p_Canvas.repaint();
        p_Canvas.serviceRepaints();
    }

    protected void paintOnBuffer(Graphics p_InsideDoubleBufferGraphics)
    {
        switch (i_MIDletState)
        {
            case STATE_LOADING:
                loadingProcessDrawing(p_InsideDoubleBufferGraphics);
                break;
            case STATE_DEMOPLAY:
            case STATE_GAMEPLAY:
                gamePlayDrawing(p_InsideDoubleBufferGraphics);
                break;
            case STATE_FRONTPAGE:
                FrontFinishPageDrawing(p_InsideDoubleBufferGraphics, true);
                break;
            case STATE_FINISHIMAGE:
                FrontFinishPageDrawing(p_InsideDoubleBufferGraphics, false);
                break;
            case STATE_MENU:
                break;
        }
        if (!p_Canvas.lg_painted)
        lg_FirstRepaintAfterStateChanging = false;
    }

    private void loadingProcessDrawing(Graphics _graphics)
    {

        _graphics.setColor(0xFFFFFF);
        _graphics.fillRect(0, 0, SCREENWIDTH, SCREENHEIGHT);


        int i_centerScreenX = SCREENWIDTH>>1;
        int i_centerScreenY = SCREENHEIGHT>>1;
        _graphics.setColor(0x000000);

        int i_Width, i_Height,
            i_x = i_centerScreenX, i_y = i_centerScreenY;
        int i_barHeight = 10;

        int i_barGap = 5;


        if(logo!=null)
        {
          i_Width = logo.getWidth();
          i_Height = logo.getHeight();


        i_x -= (i_Width>>1);

        {
           int i_fillWidth = (i_Width*Math.min(i_itemsLoaded,i_totalItems) + i_totalItems-1)/i_totalItems;

           i_y -= (i_Height + i_barHeight  + i_barGap  )>>1;

           int y = i_y + i_Height + i_barGap;

           i_barHeight--;
           y--;

           if(i_fillWidth>0)
           {
             int color = 15694617;
             int darken = 15694617 - ((15694617 >> 2) & 0x3f3f3f);
             int lighten = 15694617 + (((0xffffff - 15694617) >> 1) & 0x7f7f7f);

             _graphics.fillRect(i_x,y,i_fillWidth,i_barHeight);

             int h = 3;

             int hh = Math.min(h, (i_barHeight-1)>>1);
             int vh = Math.min(h, (i_fillWidth-1)>>1);
             if(vh>0)
             {
               _graphics.setColor(lighten);
               _graphics.fillRect(i_x,y,vh,i_barHeight);
               _graphics.setColor(darken);
               _graphics.fillRect(i_x+i_fillWidth-vh,y,vh,i_barHeight);
             }
              else vh = 0;
             if(hh>0)
             {
               for(int j = 0; j<=hh; j++)
               {
               _graphics.setColor(lighten);
               _graphics.drawLine(i_x,y+j,i_x + i_fillWidth-1-j,y+j);
               _graphics.setColor(darken);
               _graphics.drawLine(i_x+1+j,y+i_barHeight-1-j,i_x + i_fillWidth-1-j,y+i_barHeight-1-j);
               }
             }
              else hh = 0;

             _graphics.setColor(color);
             _graphics.fillRect(i_x +vh,y +hh,i_fillWidth-vh*2,i_barHeight-hh*2);
           }

           if(i_Width - i_fillWidth > 0)
           {
             _graphics.setColor(11323624);
             _graphics.fillRect(i_x+i_fillWidth,y,i_Width - i_fillWidth,i_barHeight);
           }
        }
         _graphics.drawImage(logo,i_x,i_y,0);

        }


    }

    private void FrontFinishPageDrawing(Graphics _graphics, boolean _frontpage)
    {
        try
        {
            Image p_imageForOutput = null;
            if (_frontpage)
            {
p_imageForOutput = getImage(0);
            }
            else if (i_GamePlayerState == PLAYER_STATE_LOST)
            {
p_imageForOutput = getImage(1);
            }
            else
            {
p_imageForOutput = getImage(2);
            }

            _graphics.drawImage(p_imageForOutput,(SCREENWIDTH-p_imageForOutput.getWidth())>>1,(SCREENHEIGHT-p_imageForOutput.getHeight())>>1,0);

        }
        catch (Exception ex)
        {
            _graphics.setColor(0xFF0000);
            _graphics.fillRect(0, 0, SCREENWIDTH, SCREENHEIGHT);
        }
        finally
        {
            Runtime.getRuntime().gc();
        }
        lg_FinishImagePictured = true;

    }

    private void gamePlayDrawing(Graphics _graphics)
    {
        synchronized (p_Gamelet)
        {
            if (lg_gameIsStopped) return;

        int li,i_sx,i_sy,i_w,i_h,i_x,i_y,courier_frame;
        Image courier = null;
        Sprite [] ap_sprites;
        Sprite p_spr;
        Image img;

        boolean draw_letter = false;
        boolean timer_not_allocated = true;







          _graphics.drawImage(getImage(IMG_BACK),0,0,0);


        p_spr = p_Gamelet.pPlayerSprite;
        courier_frame = p_spr.i_Frame;

        i_sx = p_spr.i_mainX>>8;
        i_sy = (p_spr.i_ScreenY + p_spr.i_height>>8) + 2;

        if(p_spr.lg_SpriteActive)
        {
            switch(p_spr.i_ObjectType)
            {
                default:
                    {
                       li = IMG_COURIER_DISAPPEAR_0000;
                       courier_frame = 0;

                       if(p_Gamelet.i_DocumentID != 0)
                       {
                          draw_letter = true;
                       }
                       if(p_spr.i_ObjectType == MobileGameletImpl.SPRITE_COURIER_LIFT)
                       {
                          i_sy -= 1;
                       }
                    }
                    break;
                case MobileGameletImpl.SPRITE_COURIER_LEFT:
                    {
                       li = (p_Gamelet.i_DocumentID != 0) ? IMG_COURIER_DOC_LEFT_0000 : IMG_COURIER_LEFT_0000;
                    }
                    break;
                case MobileGameletImpl.SPRITE_COURIER_RIGHT:
                    {
                       li = (p_Gamelet.i_DocumentID != 0) ? IMG_COURIER_DOC_RIGHT_0000 : IMG_COURIER_RIGHT_0000;
                    }
                    break;
                case MobileGameletImpl.SPRITE_COURIER_DEAD:
                    {
                       li = IMG_COURIER_DISAPPEAR_0000;
                       if((i_Timer & 1)==0) p_spr.i_Frame++;
                    }
                    break;
            }

            courier = getImage(li + (courier_frame % 3));
            i_sx -= (courier.getWidth()>>1);
            i_sy -= courier.getHeight();
        }







        ap_sprites = p_Gamelet.ap_Sprites;


        for(li = MobileGameletImpl.MAX_DOORS; li < ap_sprites.length; li++)
        {
            p_spr = ap_sprites[li];
            if (p_spr.lg_SpriteActive)
            {
                i_x = p_spr.i_mainX>>8;
                i_y = (p_spr.i_ScreenY + p_spr.i_height)>>8;

                switch(p_spr.i_ObjectType)
                {

                    case MobileGameletImpl.SPRITE_SENDER:
                         {
                             img = getImage(IMG_DOOR_OPENED);
                             _graphics.drawImage(img, i_x, i_y - img.getHeight()+1,0);

                             img = getImage(IMG_MAN_HAND_DOC_1 + (p_spr.i_Frame & 1));
                             i_x += (p_spr.i_width>>9) - img.getWidth() +5;
                             i_y += 2;
                         }
                         break;

                    case MobileGameletImpl.SPRITE_RECEIVER:
                         {
                             img = getImage(IMG_DOOR_OPENED);
                             _graphics.drawImage(img, i_x, i_y - img.getHeight() +1,0);

                             if(p_Gamelet.i_DocumentID == p_spr.i_ObjectState)
                             {
                                {
                                   timer_not_allocated = false;
                                   i_w = i_x - 12; i_h = i_y - 22;
                                   gTimer.drawImage(getImage(IMG_BACK),-i_w, -i_h,0);
                                   timer.drawDigits(gTimer, 0,0, timer.i_origRadix - (timer.i_origRadix * p_Gamelet.i_DeliveryTimer / p_Gamelet.i_InitDeliveryTimer)-1);
                                   _graphics.drawImage(imgTimer,i_w,i_h,0);
                                }

                                img = getImage(IMG_MAN_HAND_1 + (p_spr.i_Frame & 1));
                                i_x += (p_spr.i_width>>9) - img.getWidth() +5;
                                i_y += 2;
                             }
                               else
                                    {
                                      img = getImage(IMG_MAN_LIFT);
                                      i_x += (p_spr.i_width>>9) - img.getWidth() +5;
                                      i_y += 2;

                                    }
                         }
                         break;
                    case MobileGameletImpl.SPRITE_SENDER_APPEAR:
                    case MobileGameletImpl.SPRITE_SENDER_DISAPPEAR:
                    case MobileGameletImpl.SPRITE_RECEIVER_APPEAR:
                    case MobileGameletImpl.SPRITE_RECEIVER_DISAPPEAR:
                    case MobileGameletImpl.SPRITE_MANAGER_APPEAR:
                    case MobileGameletImpl.SPRITE_MANAGER_DISAPPEAR:
                         {
                             img = getImage(IMG_DOOR_OPENED);
                             _graphics.drawImage(img, i_x, i_y - img.getHeight()+1,0);
                         }
                         continue;

                    case MobileGameletImpl.SPRITE_LIFT_UP:
                    case MobileGameletImpl.SPRITE_LIFT_DOWN:
                         {
                             img = getImage(IMG_LIFT_PLACE);
                             if(p_spr.i_ObjectState == 1)
                             {
                                 i_y -= img.getHeight();
                                 _graphics.drawImage(img,i_x - (img.getWidth()>>1),i_y,0);
                                 img = getImage(IMG_MAN_LIFT);
                                 i_y += 2;
                             }
                             i_x -= (img.getWidth()>>1);
                        }
                        break;
                    default: continue;
                }
                _graphics.drawImage(img,i_x,i_y - img.getHeight(),0);
            }
        }




        if(i_sy + courier.getHeight() > 128-20)
        if(courier != null)
        {
             _graphics.drawImage(courier,i_sx, i_sy,0);
             courier = null;
             if(draw_letter)
             {
               _graphics.drawImage(getImage(IMG_DOC),i_sx+6,i_sy+10,0);
             }
        }


        p_spr = p_Gamelet.pHamburger;
        if(p_spr.lg_SpriteActive)
        {
            img = getImage(IMG_HUMBURGER);
            i_x = (p_spr.i_mainX >> 8) - (img.getWidth() >>1 );
            i_y = ((p_spr.i_ScreenY + p_spr.i_height) >> 8)  - img.getHeight() +1;
            _graphics.drawImage(img,i_x,i_y,0);
        }




        p_spr = p_Gamelet.pManager;
        if(p_spr.lg_SpriteActive)
        {
            i_x = p_spr.i_mainX>>8;
            i_y = (p_spr.i_ScreenY + p_spr.i_height>>8) +3;

            switch(p_spr.i_ObjectType)
            {
                case MobileGameletImpl.SPRITE_MANAGER_LEFT:
                    {
                       director_left.drawDigits(_graphics,i_x - (director_left.fontWidth>>1),i_y - director_left.fontHeight,p_spr.i_Frame);
                    }
                    break;
                case MobileGameletImpl.SPRITE_MANAGER_RIGHT:
                    {
                       director_right.drawDigits(_graphics,i_x - (director_right.fontWidth>>1),i_y - director_right.fontHeight,p_spr.i_Frame);
                    }
                    break;
                default:
                    {
                        img = getImage(IMG_DIRECTOR);
                        _graphics.drawImage(img,i_x - (img.getWidth()>>1),i_y - img.getHeight(),0);
                    }
                    break;
            }
        }


        if(courier != null)
        {
             _graphics.drawImage(courier,i_sx, i_sy,0);
             courier = null;
             if(draw_letter)
             {
               _graphics.drawImage(getImage(IMG_DOC),i_sx+6,i_sy+10,0);
             }
        }



          if(last_score != p_Player.m_iPlayerGameScores)
          {
             last_score = p_Player.m_iPlayerGameScores;
             img = getImage(IMG_BACK);
             gScore.drawImage(img,-(GAMESCREEN_WIDTH - SCORE_WIDTH -1),0,0);
             font.drawDigits(gScore,score_x,score_y,4,last_score,10);
          }
          _graphics.drawImage(imgScore,GAMESCREEN_WIDTH - SCORE_WIDTH -1,0,0);

          img = getImage(IMG_ICON_LIFE);
          li = p_Gamelet.i_playerAttemptions-1;

          if (p_Gamelet.getGameWorldState() == MobileGamelet.GAMEWORLDSTATE_PLAYERLOST)
          {
              li = ((i_Timer&2)==0) ? li+1 : 0;
          }

          if(last_lives != li)
          {
             last_lives = li;
             gLives.drawImage(getImage(IMG_BACK),-1,0,0);

             i_x = (LIVES_WIDTH - img.getWidth()*li) >> 1;
             i_y = (LIVES_HEIGHT - img.getHeight()) >> 1;

             for(; li > 0 ;li--)
             {
                 gLives.drawImage(img, i_x, i_y,0);
                 i_x += img.getWidth();
             }
          }
          _graphics.drawImage(imgLives,1,0,0);



         if(timer_not_allocated && p_Gamelet.i_DeliveryTimer > 0 && p_Gamelet.i_Clerks > 0)
         {
          timer.drawDigits(_graphics, (GAMESCREEN_WIDTH - timer.fontWidth >> 1)+1, 1, timer.i_origRadix -1 - timer.i_origRadix * p_Gamelet.i_DeliveryTimer  / p_Gamelet.i_InitDeliveryTimer);
         }









       {
         if(menu_icon_image != null)
         {
            _graphics.drawImage(menu_icon_image,
               0
              +5

            ,SCREENHEIGHT - menu_icon_image.getHeight()

              +2
            ,0);
         }
       }






            if((i_Timer & 2) == 0)
            {
                if(i_MIDletState == STATE_DEMOPLAY)
                {
                   Image _image = getImage(IMG_DEMO);
                   _graphics.drawImage(_image,(SCREENWIDTH-_image.getWidth())>>1
                          ,SCREENHEIGHT - (( (SCREENHEIGHT >> 1) + _image.getHeight()) >> 1)
                          ,0);
                }
            }
        }
    }

    private void processListItem(int _screenId, int _itemId)
    {
        switch (_screenId)
        {
            case SCR_MainMenuSCR:
                {
                    switch (_itemId)
                    {
                        case ITEM_ResumeGameITM:
                            {
                                clearScreenStack();
                                initState(STATE_GAMEPLAY);
                            }
                            ;
                            break;

                    }
                }
                ;
                break;
            case SCR_NewGameSCR:
                {
                    int i_level;
                    switch (_itemId)
                    {
                        case ITEM_EasyModeITM:
                            {
                                i_level = 0;
                            }
                            ;
                            break;
                        case ITEM_NormalModeITM:
                            {
                                i_level = 1;
                            }
                            ;
                            break;
                        default :
                            {
                                i_level = 2;
                            }
                    }
                    i_selectedGameLevel = i_level;
                    lg_startGameAfterDownloading = false;
                    startGame();
                }
                ;
                break;
            case SCR_LanguageSelectSCR:
                {
                    try
                    {
                        setLanguage(_itemId);
                        gds_setIntOption(GDS_FIELD_LANGUAGE, _itemId);
                        gds_flush();
                        reinitScreen(true);
                    }
                    catch (Exception e)
                    {
                        viewAlert("RMSError", e.getMessage(), AlertType.ERROR, Alert.FOREVER);
                    }
                }
                ;
                break;
            case SCR_LoadGameSCR:
                {
                    try
                    {
                        DataInputStream p_instr = gds_getSavedGameForIndex(_itemId);
                        loadGame(p_instr);
                        lg_startGameAfterDownloading = true;
                        startGame();
                    }
                    catch (Exception e)
                    {
                        viewAlert("RMSError", e.getMessage(), AlertType.ERROR, Alert.FOREVER);
                    }
                }
                ;
                break;
        }
    }

    private void viewAlert(String _caption, String _text, AlertType _type, int _delay)
    {
        Alert p_alertError = new Alert(_caption, _text, null, _type);
        p_alertError.setTimeout(_delay);
        p_Display.setCurrent(p_alertError);
    }

    private void processCommand(Displayable _screen, int _screenId, int _commandId, int _selectedId)
    {
        switch (_screenId)
        {
            case SCR_ExitSCR:
                {
                    if (_commandId == COMMAND_YesCMD)
                        if (i_prevMIDletState == STATE_GAMEPLAY)
                        {
                            endGame();
                            initState(STATE_FRONTPAGE);
                        }
                        else
                        {
                            try
                            {
                                destroyApp(false);
                            }
                            catch (MIDletStateChangeException e)
                            {
                            }
                            return;
                        }
                }
                ;
                break;
            case SCR_SaveGameSCR:
                {
                    int i_offst = 0;
                    if (gds_getFirstEmptySavedGame() >= 0)
                    {
                        if (_selectedId == 0)
                        {
                            initScreen(SCR_NewGameNameFormSCR);
                            return;
                        }
                        else
                            i_offst = 1;
                    }
                    else
                        i_offst = 0;

                    i_gameIndexForRewriting = _selectedId - i_offst;
                    initScreen(SCR_AreYouSureSCR);
                }
                ;
                break;
            case SCR_PlayerNameFormSCR:
                {
                    Form p_form = (Form) _screen;
                    TextField p_txtfld = (TextField) p_form.get(1);
                    String s_str = p_txtfld.getString().trim();
                    if (s_str.length() == 0) s_str = lb_as_TextStringArray[DefaultPlayerNameTXT];


                    gds_addRecordToTopList(s_str, i_LastGameScores);
                    initScreen(SCR_TopResultsSCR);
                }
                ;
                break;
            case SCR_NewGameNameFormSCR:
                {
                    Form p_form = (Form) _screen;
                    TextField p_txtfld = (TextField) p_form.get(0);
                    s_gameNameForSaving = p_txtfld.getString().trim();
                    if (s_gameNameForSaving.length() == 0)
                        s_gameNameForSaving = lb_as_TextStringArray[DefaultSaveNameTXT];
                    int i_indx = gds_convertSavedNameToIndex(s_gameNameForSaving);
                    if(i_indx >= 0)
                    {
                       i_gameIndexForRewriting = i_indx;
                       initScreen(SCR_AreYouSureSCR);
                    }
                      else
                           try
                           {
                               byte[] ab_array = saveGame();
                               gds_saveGameDataForName(s_gameNameForSaving, ab_array);
                               ab_array = null;
                               back(3);
                               Runtime.getRuntime().gc();
                           }
                           catch (Exception e)
                           {
                               viewAlert("RMSError", e.getMessage(), AlertType.ERROR, Alert.FOREVER);
                           }
                }
                ;
                break;
            case SCR_ViewGameScoreSCR:
                {
                    if (_commandId == COMMAND_AddInTopCMD)
                        initScreen(SCR_PlayerNameFormSCR);
                    else
                    {
                        initState(STATE_FRONTPAGE);
                    }
                }
                ;
                break;
            case SCR_TopResultsSCR:
                {
                    switch (_commandId)
                    {
                        case COMMAND_RemoveAllCMD:
                            {
                                initScreen(SCR_AreYouSureSCR);
                            }
                            ;
                            break;
                        case COMMAND_OkCMD:
                            {
                                initState(STATE_FRONTPAGE);
                            }
                            ;
                            break;
                    }
                }
                ;
                break;
            case SCR_AreYouSureSCR:
                {
                    switch (getPreviousScreenID())
                    {
                        case SCR_SaveGameSCR:
                        case SCR_NewGameNameFormSCR:
                            {
                                try
                                {
                                    byte[] ab_array = saveGame();
                                    gds_saveGameDataForIndex(i_gameIndexForRewriting, ab_array);
                                    gds_flush();
                                    ab_array = null;
                                    back(3);
                                    Runtime.getRuntime().gc();
                                }
                                catch (Exception e)
                                {
                                    viewAlert("RMSError", e.getMessage(), AlertType.ERROR, Alert.FOREVER);
                                }
                            }
                            ;
                            break;
                        case SCR_TopResultsSCR:
                            {
                                gds_clearTopList();
                                back();
                            }
                            ;
                            break;
                    }
                }
                ;
                break;
        }
    }

    private boolean enableCommand(int _screenId, int _commandId)
    {
        switch (_screenId)
        {
            case SCR_TopResultsSCR:
                {
                    boolean lg_clearAllEnabled;
                    if (gds_getTopListRecordNumber() == 0)
                        lg_clearAllEnabled = false;
                    else
                        lg_clearAllEnabled = true;

                    if (i_MIDletState == STATE_GAMEOVER)
                    {
                        switch (_commandId)
                        {
                            case COMMAND_OkCMD:
                                return true;
                            case COMMAND_RemoveAllCMD:
                                return lg_clearAllEnabled;
                            default :
                                return false;
                        }
                    }
                    else
                    {
                        switch (_commandId)
                        {
                            case COMMAND_BackCMD:
                                return true;
                            case COMMAND_RemoveAllCMD:
                                return lg_clearAllEnabled;
                            default :
                                return false;
                        }
                    }
                }
            case SCR_SaveGameSCR:
                {
                    if (true) return false; 

                    if (_commandId == COMMAND_RemoveAllCMD)
                    {
                        if (gds_getSavedGamesCount() != 0)
                            return true;
                        else
                            return false;
                    }
                }
                ;
                break;
            case SCR_ViewGameScoreSCR:
                {
                    if (_commandId == COMMAND_AddInTopCMD)
                    {
                        if (i_LastGameScores >= gds_getMinScoreInTopList())
                            return true;
                        else
                            return false;
                    }
                }
                ;
                break;
            case SCR_MainMenuSCR:
                {
return true;
                }
        }
        return false;
    }

    private Displayable customScreen(int _screenId)
    {
        switch (_screenId)
        {
            case SCR_OnOffSCR:
                {
                    String s_caption = null;
                    int i_selIndex = 1;

                    switch (i_lastListItemSelected)
                    {
                        case ITEM_BackLightITM:
                            s_caption = lb_as_TextStringArray[BackLightTXT];
                            if (gds_getBooleanOption(GDS_FIELD_LIGHT)) i_selIndex = 0;
                            break;
                        case ITEM_SoundITM:
                            s_caption = lb_as_TextStringArray[SoundTXT];
                            if (gds_getBooleanOption(GDS_FIELD_SOUND)) i_selIndex = 0;
                            break;
                    }
                    List p_lst = new List(s_caption, List.EXCLUSIVE);
                    p_lst.append(lb_as_TextStringArray[OnTXT], null);
                    p_lst.append(lb_as_TextStringArray[OffTXT], null);
                    p_lst.setSelectedIndex(i_selIndex, true);
                    return p_lst;
                }
            case SCR_SaveGameSCR:
                {
                    List p_newList = new List(lb_as_TextStringArray[SaveGameTitleTXT], List.IMPLICIT);
                    int i_savedGamesNumber = gds_getSavedGamesCount();

                    boolean lg_thereIsFreePlace = gds_getFirstEmptySavedGame() < 0 ? false : true;

                    if (lg_thereIsFreePlace)
                    {
                        p_newList.append(lb_as_TextStringArray[NewRecordTXT], null);
                    }

                    for (int li = 0; li < i_savedGamesNumber; li++)
                    {
                        String s_savedGameName = gds_getSavedGameNameForIndex(li);
                        p_newList.append(s_savedGameName, null);
                    }

                    return p_newList;
                }
            case SCR_LoadGameSCR:
                {
                    int i_savedGamesNumber = gds_getSavedGamesCount();
                    if (i_savedGamesNumber == 0)
                    {
                        Form p_form = new Form(lb_as_TextStringArray[LoadGameTitleTXT]);
                        p_form.append(lb_as_TextStringArray[ListEmptyTXT]);
                        return p_form;
                    }
                    else
                    {
                        List p_newList = new List(lb_as_TextStringArray[LoadGameTitleTXT], List.IMPLICIT);
                        for (int li = 0; li < i_savedGamesNumber; li++)
                        {
                            String s_savedGameName = gds_getSavedGameNameForIndex(li);
                            p_newList.append(s_savedGameName, null);
                        }
                        return p_newList;
                    }
                }
            case SCR_TopResultsSCR:
                {
                    int i_recordnumber = gds_getTopListRecordNumber();
                    Form p_form = new Form(lb_as_TextStringArray[TopResultsTitleTXT]);
                    if (i_recordnumber == 0)
                    {
                        p_form.append(lb_as_TextStringArray[ListEmptyTXT]);
                    }
                    else
                    {
                        for (int li = 0; li < GDS_MAX_TOPRECORDS; li++)
                        {
                            String s_name = gds_getTopListNameForIndex(li);
                            int i_score = gds_getTopListScoresForIndex(li);
                            StringBuffer p_buf = new StringBuffer();
                            if (s_name != null)
                            {
                                p_buf.append(li + 1);
                                p_buf.append('.');
                                p_buf.append(s_name);
                                p_buf.append("\n    ");
                                p_buf.append(i_score);
                                p_buf.append('\n');
                            }
                            p_form.append(p_buf.toString());
                        }
                        return p_form;
                    }
                    return p_form;
                }
            case SCR_LanguageSelectSCR:
                {
                    List p_list = new List(lb_as_TextStringArray[LanguageSelectTitleTXT], List.IMPLICIT);
                    for (int li = 0; li < lb_as_LanguageNames.length; li++)
                    {
                        p_list.append(lb_as_LanguageNames[li], null);
                    }
                    return p_list;
                }
        }
        return null;
    }

    private Object customItem(int _screenId, int _itemId, boolean _getImage)
    {
        switch (_screenId)
        {
            case SCR_ExitSCR:
                {
                    String p_StrItem = null;
                    if (i_prevMIDletState == STATE_GAMEPLAY)
                        p_StrItem = lb_as_TextStringArray[EndTheGameSessionTXT];
                    else
                        p_StrItem = lb_as_TextStringArray[EndTheGameTXT];
                    return p_StrItem;
                }
            case SCR_NewGameNameFormSCR:
                {
                    TextField p_txtField = new TextField(lb_as_TextStringArray[InputNewGameNameLabelTXT], lb_as_TextStringArray[DefaultSaveNameTXT], 8, TextField.ANY);
                    return p_txtField;
                }
            case SCR_PlayerNameFormSCR:
                {
                    switch (_itemId)
                    {
                        case ITEM_ScoreNumberItem:
                            {
                                String p_strItem = lb_as_TextStringArray[YourScoresTXT] + " " + Integer.toString(i_LastGameScores);
                                return p_strItem;
                            }
                        case ITEM_PlayerNameFieldITM:
                            {
                                TextField p_txtField = new TextField(lb_as_TextStringArray[InputPlayerNameLabelTXT], lb_as_TextStringArray[DefaultPlayerNameTXT], 8, TextField.ANY);
                                return p_txtField;
                            }
                    }
                }
            case SCR_ViewGameScoreSCR:
                {
                    if (_itemId == ITEM_ViewScoreNumberItem)
                    {
                        String p_strItem = lb_as_TextStringArray[YourScoresTXT] + " " + Integer.toString(i_LastGameScores);
                        return p_strItem;
                    }
                }
        }
        return null;
    }

    private boolean enableItem(int _screenId, int _itemId)
    {
        switch (_screenId)
        {
            case SCR_SaveLoadSCR:
                {
                    switch (_itemId)
                    {
                        case ITEM_SaveGameITM:
                            if (i_prevMIDletState == STATE_GAMEPLAY
                            && p_Gamelet.getGameWorldState() == MobileGamelet.GAMEWORLDSTATE_PLAYED)
                                 return true; else return false;
                    }
                }
            case SCR_MainMenuSCR:
                {
                    switch (_itemId)
                    {
                        case ITEM_ResumeGameITM:
                            if (i_prevMIDletState == STATE_GAMEPLAY) return true; else return false;
                        case ITEM_NewGameITM:
                            if (i_prevMIDletState == STATE_GAMEPLAY) return false; else return true;
                        case ITEM_RestartLevelITM:
                            return false;
                    }
                }
            default :
                {
                    switch (_itemId)
                    {
                        case ITEM_SoundITM:
                            {
return true;
                            }
                        case ITEM_BackLightITM:
                            {
return true;
                            }
                        case ITEM_VibrationITM:
                            {
                                return false;
                            }
                    }
                }
        }
        return true;
    }

    private void onExitScreen(Displayable _screen, int _screenId)
    {
        switch (_screenId)
        {
            case SCR_OnOffSCR:
                {
                    List p_list = (List) _screen;
                    boolean[] alg_flagarray = new boolean[2];
                    p_list.getSelectedFlags(alg_flagarray);
                    switch (i_lastListItemSelected)
                    {
                        case ITEM_BackLightITM:
                            {
                                lg_BackLightEnabled = alg_flagarray[0];
                                gds_setBooleanOption(GDS_FIELD_LIGHT, lg_BackLightEnabled);
                                setDisplayLight(lg_BackLightEnabled);
                            }
                            ;
                            break;
                        case ITEM_SoundITM:
                            {
                                lg_SoundEnabled = alg_flagarray[0];
                                gds_setBooleanOption(GDS_FIELD_SOUND, lg_SoundEnabled);
                            }
                            ;
                            break;
                    }
                }
                ;
                break;
        }
    }
    private void startGame()
    {
        lg_firstPaint = true;
        if (!lg_startGameAfterDownloading)
        {
            p_Gamelet.newGameSession(
                   GAMESCREEN_WIDTH,GAMESCREEN_HEIGHT,
                   i_selectedGameLevel < 0 ? 0 : i_selectedGameLevel, ap_Players,
                   this,
                   null);


        }



        if (i_selectedGameLevel >= 0)
        {
            initState(STATE_GAMEPLAY);
        }

        p_Player.i_buttonState = 0;                   
        i_gameTimeDelay = p_Gamelet.getGameStepDelay();
        lg_gameIsStopped = false;
    }

    private byte[] saveGame() throws Exception
    {
        ByteArrayOutputStream p_baos = new ByteArrayOutputStream(p_Gamelet.getMaximumDataSize());
        DataOutputStream p_daos = new DataOutputStream(p_baos);
        p_Gamelet.saveGameData(p_daos);
        p_daos.close();
        byte[] p_gamedata = p_baos.toByteArray();
        p_baos = null;
        p_daos = null;
        Runtime.getRuntime().gc();
        return p_gamedata;
    }

    private void loadGame(DataInputStream _stream) throws Exception
    {
        endGame();
        p_Gamelet.loadGameData(_stream, ap_Players,
        this
        );
        initState(STATE_GAMEPLAY);
    }

    private void endGame()
    {
        {
            synchronized (p_Gamelet)
            {
                p_Gamelet.endGameSession();
                lg_gameIsStopped = true;





            }
            Runtime.getRuntime().gc();
        }
    }
    protected void KeyPressed(int _key)
    {

        switch (i_MIDletState)
        {
            case STATE_FRONTPAGE:
                {
if (lg_FinishImagePictured)
                    initState(STATE_MENU);
                }
                ;
                break;
            case STATE_GAMEPLAY:
                {
                    synchronized (p_Gamelet)
                    {
                        if (_key == KEY_MENU || _key == KEY_CANCEL) initState(STATE_MENU);


        int i_state = p_Player.i_buttonState;

        switch(_key)
        {
            case Canvas.KEY_NUM1 :
                {
                    i_state |= MoveObject.BUTTON_UP;
                    i_state &= ~MoveObject.BUTTON_DOWN;
                    i_state |= MoveObject.BUTTON_LEFT;
                    i_state &= ~MoveObject.BUTTON_RIGHT;
                }
                ;
                break;

            case Canvas.KEY_NUM2 :
                {
                    i_state |= MoveObject.BUTTON_UP;
                    i_state &= ~MoveObject.BUTTON_DOWN;
                }
                ;
                break;

            case Canvas.KEY_NUM3 :
                {
                    i_state |= MoveObject.BUTTON_UP;
                    i_state &= ~MoveObject.BUTTON_DOWN;
                    i_state |= MoveObject.BUTTON_RIGHT;
                    i_state &= ~MoveObject.BUTTON_LEFT;
                }
                ;
                break;

            case Canvas.KEY_NUM4 :
                {
                    i_state |= MoveObject.BUTTON_LEFT;
                    i_state &= ~MoveObject.BUTTON_RIGHT;
                }
                ;
                break;

            case Canvas.KEY_NUM5 :
                {
                    i_state |= MoveObject.BUTTON_FIRE;
                }
                ;
                break;

            case Canvas.KEY_NUM6 :
                {
                    i_state |= MoveObject.BUTTON_RIGHT;
                    i_state &= ~MoveObject.BUTTON_LEFT;
                }
                ;
                break;

            case Canvas.KEY_NUM7 :
                {
                    i_state |= MoveObject.BUTTON_DOWN;
                    i_state &= ~MoveObject.BUTTON_UP;
                    i_state |= MoveObject.BUTTON_LEFT;
                    i_state &= ~MoveObject.BUTTON_RIGHT;
                }
                ;
                break;

            case Canvas.KEY_NUM8 :
                {
                    i_state |= MoveObject.BUTTON_DOWN;
                    i_state &= ~MoveObject.BUTTON_UP;
                }
                ;
                break;

            case Canvas.KEY_NUM9 :
                {
                    i_state |= MoveObject.BUTTON_DOWN;
                    i_state &= ~MoveObject.BUTTON_UP;
                    i_state |= MoveObject.BUTTON_RIGHT;
                    i_state &= ~MoveObject.BUTTON_LEFT;
                }
                ;
                break;

            default :
                switch (p_Canvas.getGameAction(_key))
                {
                   case Canvas.LEFT:
                       {
                           i_state |= MoveObject.BUTTON_LEFT;
                           i_state &= ~MoveObject.BUTTON_RIGHT;
                       }
                       ;
                       break;
                   case Canvas.RIGHT :
                       {
                           i_state |= MoveObject.BUTTON_RIGHT;
                           i_state &= ~MoveObject.BUTTON_LEFT;
                       }
                       ;
                       break;
                   case Canvas.UP :
                       {
                           i_state |= MoveObject.BUTTON_UP;
                           i_state &= ~MoveObject.BUTTON_DOWN;
                       }
                       ;
                       break;
                   case Canvas.DOWN :
                       {
                           i_state |= MoveObject.BUTTON_DOWN;
                           i_state &= ~MoveObject.BUTTON_UP;
                       }
                       ;
                       break;
                   case Canvas.FIRE :
                       {
                           i_state = MoveObject.BUTTON_FIRE;
                       }
                       ;
                       break;
                }
        }
        p_Player.i_buttonState = i_state;
                    }
                }
                ;
                break;
            case STATE_FINISHIMAGE:
                {
                    if (lg_FinishImagePictured && i_Timer > ANTICLICKDELAY_FINISHIMAGE)
                                    initState(STATE_GAMEOVER);
                }
                ;
                break;
            case STATE_DEMOPLAY:
                {
                    {
                        endGame();
                        i_MIDletState = STATE_FRONTPAGE;
                        initState(STATE_MENU);
                    }
                }
                ;
                break;
        }
    }

    protected void KeyReleased(int _key)
    {
        int i_state = p_Player.i_buttonState;

        switch(_key)
        {
            case Canvas.KEY_NUM1 :
                {
                    i_state &= ~MoveObject.BUTTON_UP;
                    i_state &= ~MoveObject.BUTTON_LEFT;
                    break;
                }
            case Canvas.KEY_NUM2 :
                {
                    i_state &= ~MoveObject.BUTTON_UP;
                    break;
                }
            case Canvas.KEY_NUM3 :
                {
                    i_state &= ~MoveObject.BUTTON_UP;
                    i_state &= ~MoveObject.BUTTON_RIGHT;
                    break;
                }
            case Canvas.KEY_NUM4 :
                {
                    i_state &= ~MoveObject.BUTTON_LEFT;
                    break;
                }
            case Canvas.KEY_NUM5 :
                {
                    i_state &= ~MoveObject.BUTTON_FIRE;
                    break;
                }
            case Canvas.KEY_NUM6 :
                {
                    i_state &= ~MoveObject.BUTTON_RIGHT;
                    break;
                }
            case Canvas.KEY_NUM7 :
                {
                    i_state &= ~MoveObject.BUTTON_DOWN;
                    i_state &= ~MoveObject.BUTTON_LEFT;
                    break;
                }
            case Canvas.KEY_NUM8 :
                {
                    i_state &= ~MoveObject.BUTTON_DOWN;
                    break;
                }
            case Canvas.KEY_NUM9 :
                {
                    i_state &= ~MoveObject.BUTTON_DOWN;
                    i_state &= ~MoveObject.BUTTON_RIGHT;
                    break;
                }
            default :
                switch (p_Canvas.getGameAction(_key))
                {
                    case Canvas.FIRE :
                        {
                            i_state &= ~MoveObject.BUTTON_FIRE;
                            break;
                        }

                    case Canvas.LEFT :
                        {
                            i_state &= ~MoveObject.BUTTON_LEFT;
                            break;
                        }
                    case Canvas.RIGHT :
                        {
                            i_state &= ~MoveObject.BUTTON_RIGHT;
                            break;
                        }
                    case Canvas.DOWN :
                        {
                            i_state &= ~MoveObject.BUTTON_DOWN;
                            break;
                        }
                    case Canvas.UP :
                        {
                            i_state &= ~MoveObject.BUTTON_UP;
                            break;
                        }
                    default:
                           {
                               i_state = MoveObject.BUTTON_NONE;
                               break;
                           }
                }
        }

       p_Player.i_buttonState = i_state;

    }

    private void pause()
    {
        if (i_MIDletState != STATE_MENU) initState(STATE_MENU);
    }

    private void destroy()
    {
        if (gds_lg_isChanged)
        {
            try
            {
                gds_flush();
            }
            catch (Exception e)
            {
                viewAlert("RMSError", e.getMessage(), AlertType.ERROR, Alert.FOREVER);
            }
        }
        lg_Active = false;
        if(p_Gamelet != null)p_Gamelet.globalReleaseWorld();
    }


    public int processGameAction(int _arg)
    {
          int i_action_sound = -1;

          switch(_arg)
          {
               case MobileGameletImpl.GAMEACTION_COURIER_EAT_HAMBURGER:
                          {
                            i_action_sound = SND_VZYAL_GAMBURGER_MMF;
                          }
                          break;
               case MobileGameletImpl.GAMEACTION_COURIER_LIFT:
                          {
                          }
                          break;
               case MobileGameletImpl.GAMEACTION_COURIER_LEAVE_LIFT:
                          {
                          }
                          break;
               case MobileGameletImpl.GAMEACTION_COURIER_DEAD:
                          {
                            i_action_sound = SND_GIBEL_KURERA_MMF;
                          }
                          break;
               case MobileGameletImpl.GAMEACTION_MANAGER:
                          {
                            i_action_sound = SND_POYAVLENIE_DIRECTORA_MMF;
                          }
                          break;
               case MobileGameletImpl.GAMEACTION_MANAGER_LIFT:
                          {
                          }
                          break;
               case MobileGameletImpl.GAMEACTION_MANAGER_LEAVE_LIFT:
                          {
                          }
                          break;
               case MobileGameletImpl.GAMEACTION_MANAGER_EAT_HAMBURGER:
                          {
                          }
                          break;
               case MobileGameletImpl.GAMEACTION_MANAGER_AT_OFFICE:
                          {
                          }
                          break;
               case MobileGameletImpl.GAMEACTION_BAD_MANAGER_MEETING:
                          {
                          }
                          break;
               case MobileGameletImpl.GAMEACTION_GOOD_MANAGER_MEETING:
                          {
                          }
                          break;
               case MobileGameletImpl.GAMEACTION_SENDER_SENT:
                          {
                            i_action_sound = SND_OTDAL_DOCUMENT_MMF;
                          }
                          break;
               case MobileGameletImpl.GAMEACTION_RECEIVER_OK:
                          {
                            i_action_sound = SND_VZYAL_DOC_MMF;
                          }
                          break;
               case MobileGameletImpl.GAMEACTION_HAMBURGER:
                          {
                          }
                          break;
               case MobileGameletImpl.GAMEACTION_CLERKS_GENERATED:
                          {
                            i_action_sound = SND_NOVIY_OTPRAVITEL_MMF;
                          }
                          break;
          }
        checkPlayabilityMusic(i_action_sound);

        return 0;
    }

    public int processGameAction(int _arg1, int _arg2)
    {
        return 0;
    }

    public Object processGameAction(int _arg1, Object _obj1)
    {
        return null;
    }

    public Object processGameAction(int _arg1, int _arg2, Object _obj1)
    {
        return null;
    }








     drawDigits font,timer;
     drawDigits director_left,director_right;

     Image imgTimer;
     Graphics gTimer;

     Image imgScore;
     Graphics gScore;
     int last_score = -1;


       final static int SCORE_WIDTH = 40;
       final static int SCORE_HEIGHT = 14;

       int score_x;
       int score_y;

       Image imgLives;
       Graphics gLives;
       int last_lives = -1;

       final static int LIVES_WIDTH = 41;
       final static int LIVES_HEIGHT = 14;



     private void prepareGraphics()
     {
        font                = new drawDigits(getImage(IMG_NUMBERS),10);
        timer               = new drawDigits(getImage(IMG_CLOCK),9,false);

        director_left       = new drawDigits(getImage(IMG_DIRECTOR_LEFT),3,false);
        director_right      = new drawDigits(getImage(IMG_DIRECTOR_RIGHT),3,false);

        imgTimer  = Image.createImage(timer.fontWidth,timer.fontHeight);
        gTimer = imgTimer.getGraphics();


        imgScore = Image.createImage(SCORE_WIDTH,SCORE_HEIGHT);
        gScore = imgScore.getGraphics();

        score_x = (SCORE_WIDTH - font.fontWidth*4) / 2;
        score_y = (SCORE_HEIGHT - font.fontHeight) / 2;

        imgLives = Image.createImage(LIVES_WIDTH,LIVES_HEIGHT);
        gLives = imgLives.getGraphics();

     }












    long _last_actioncall = 0;
    int _melody_priority = -1;
    int _playing_melody = -1;
    int i_action_sound = -1;

    void checkPlayabilityMusic(int i_action_sound)
    {
      if(i_action_sound >= 0 && i_action_sound < ap_melody.length)
       if(ap_melody[i_action_sound].i_MelodyID > _melody_priority
         || _last_actioncall <= i_timeStamp
         || (_playing_melody == i_action_sound
          && _last_actioncall == i_timeStamp + ap_melody[i_action_sound].i_MelodyLength)
         )
       {
         this.i_action_sound = i_action_sound;
         _melody_priority = ap_melody[i_action_sound].i_MelodyID;
         _last_actioncall = i_timeStamp + ap_melody[i_action_sound].i_MelodyLength;
         _playing_melody = i_action_sound;
         return;
       }
    }




    void setSoundItemPriority()
    {
                             int [] sort_idx = {
SND_NOVIY_OTPRAVITEL_MMF      ,610,
SND_OTDAL_DOCUMENT_MMF        ,604,
SND_VZYAL_DOC_MMF             ,386,
SND_VZYAL_GAMBURGER_MMF       ,486,
SND_POYAVLENIE_DIRECTORA_MMF  ,748,
SND_GIBEL_KURERA_MMF          ,891,
SND_FINAL_MMF                 ,0,
SND_START_MMF                 ,0

                             };

                             for (int i = 0; i < sort_idx.length; i+=2)
                             {
                                if (ap_melody[sort_idx[i]] != null)
                                {
                                    ap_melody[sort_idx[i]].i_MelodyID = i>>1;
                                    ap_melody[sort_idx[i]].i_MelodyLength = sort_idx[i+1];
                                }
                             }
    }













private static final int SCR_OptionsSCR = 0;
private static final int SCR_AboutSCR = 28;
private static final int SCR_NewGameSCR = 41;
private static final int SCR_SaveLoadSCR = 64;
private static final int SCR_ViewGameScoreSCR = 82;
private static final int SCR_FatalErrorSCR = 97;
private static final int SCR_AreYouSureSCR = 110;
private static final int SCR_ExitSCR = 125;
private static final int SCR_OnOffSCR = 140;
private static final int SCR_SaveGameSCR = 148;
private static final int SCR_MainMenuSCR = 160;
private static final int SCR_LoadGameSCR = 213;
private static final int SCR_TopResultsSCR = 221;
private static final int SCR_LanguageSelectSCR = 233;
private static final int SCR_PlayerNameFormSCR = 241;
private static final int SCR_HelpSCR = 261;
private static final int SCR_RewriteRecordSCR = 274;
private static final int SCR_NewGameNameFormSCR = 289;


private static final int ITEM_NewGameITM = 0;
private static final int ITEM_HardModeITM = 11;
private static final int ITEM_AboutTextITM = 19;
private static final int ITEM_VibrationITM = 18;
private static final int ITEM_AreYouSureQuestionITM = 21;
private static final int ITEM_RewriteRecordQuestionITM = 22;
private static final int ITEM_SaveGameITM = 12;
private static final int ITEM_TopResultsITM = 6;
private static final int ITEM_FatalErrorTextITM = 23;
private static final int ITEM_NormalModeITM = 10;
private static final int ITEM_SaveLoadITM = 3;
private static final int ITEM_HelpTextITM = 14;
private static final int ITEM_ScoreNumberItem = 25;
private static final int ITEM_AboutITM = 7;
private static final int ITEM_HelpITM = 4;
private static final int ITEM_PlayerNameFieldITM = 26;
private static final int ITEM_ExitTextQuestionITM = 20;
private static final int ITEM_OptionsITM = 5;
private static final int ITEM_RestartLevelITM = 2;
private static final int ITEM_LanguageSelectITM = 15;
private static final int ITEM_ResumeGameITM = 1;
private static final int ITEM_ExitITM = 8;
private static final int ITEM_BackLightITM = 16;
private static final int ITEM_LoadGameITM = 13;
private static final int ITEM_SoundITM = 17;
private static final int ITEM_ViewScoreNumberItem = 27;
private static final int ITEM_EasyModeITM = 9;
private static final int ITEM_NewGameNameFieldITM = 24;


private static final int COMMAND_AddInTopCMD = 304;
private static final int COMMAND_CancelCMD = 305;
private static final int COMMAND_RemoveAllCMD = 306;
private static final int COMMAND_SaveCMD = 307;
private static final int COMMAND_BackCMD = 308;
private static final int COMMAND_NoCMD = 309;
private static final int COMMAND_YesCMD = 310;
private static final int COMMAND_OkCMD = 311;

 private static final int SaveGameTXT = 0;
 private static final int OffTXT = 1;
 private static final int NewGameTXT = 2;
 private static final int ListEmptyTXT = 3;
 private static final int HelpTXT = 4;
 private static final int CancelTXT = 5;
 private static final int SaveCmdTXT = 6;
 private static final int RewriteRecordTXT = 7;
 private static final int SaveLoadTXT = 8;
 private static final int HardModeTXT = 9;
 private static final int InputNewGameNameLabelTXT = 10;
 private static final int AboutTextTXT = 11;
 private static final int YesCmdTXT = 12;
 private static final int OptionsTXT = 13;
 private static final int ExitTitleTXT = 14;
 private static final int BackCmdTXT = 15;
 private static final int GameScoreTitleTXT = 16;
 private static final int NewGameTitleTXT = 17;
 private static final int NormalModeTXT = 18;
 private static final int PlayerNameFormTitleTXT = 19;
 private static final int OkTXT = 20;
 private static final int OnTXT = 21;
 private static final int SaveGameTitleTXT = 22;
 private static final int EndTheGameSessionTXT = 23;
 private static final int RemoveAllCmdTXT = 24;
 private static final int YourScoresTXT = 25;
 private static final int NoCmdTXT = 26;
 private static final int TopResultsTXT = 27;
 private static final int EasyModeTXT = 28;
 private static final int InputPlayerNameLabelTXT = 29;
 private static final int ExitTXT = 30;
 private static final int AddInTopTXT = 31;
 private static final int BackLightTXT = 32;
 private static final int DefaultPlayerNameTXT = 33;
 private static final int LanguageSelectTitleTXT = 34;
 private static final int LanguageSelectTXT = 35;
 private static final int LoadGameTXT = 36;
 private static final int DefaultSaveNameTXT = 37;
 private static final int VibrationTXT = 38;
 private static final int SaveLoadTitleTXT = 39;
 private static final int HelpTextTXT = 40;
 private static final int LoadGameTitleTXT = 41;
 private static final int StageTXT = 42;
 private static final int NewRecordTXT = 43;
 private static final int AreYouSureTXT = 44;
 private static final int MainMenuTitleTXT = 45;
 private static final int AboutTXT = 46;
 private static final int FatalErrorTitleTXT = 47;
 private static final int TopResultsTitleTXT = 48;
 private static final int AreYouSureTitleTXT = 49;
 private static final int HelpTitleTXT = 50;
 private static final int SoundTXT = 51;
 private static final int ResumeGameTXT = 52;
 private static final int NewGameNameFormTitleTXT = 53;
 private static final int RestartLevelTXT = 54;
 private static final int WaitPleaseTXT = 55;
 private static final int EndTheGameTXT = 56;
 private static final int AboutTitleTXT = 57;
 private static final int OptionsTitleTXT = 58;


private static final int IMG_TITLE128X128 = 0;
private static final int IMG_FINAL128X128 = 1;
private static final int IMG_WIN = 2;
private static final int IMG_DEMO = 3;
private static final int IMG_BACK = 4;
private static final int IMG_BACKGROUND128X128 = 5;
private static final int IMG_COURIER_LEFT_0000 = 6;
private static final int IMG_COURIER_LEFT_0001 = 7;
private static final int IMG_COURIER_LEFT_0002 = 8;
private static final int IMG_COURIER_RIGHT_0000 = 9;
private static final int IMG_COURIER_RIGHT_0001 = 10;
private static final int IMG_COURIER_RIGHT_0002 = 11;
private static final int IMG_COURIER_DISAPPEAR_0000 = 12;
private static final int IMG_COURIER_DISAPPEAR_0001 = 13;
private static final int IMG_COURIER_DISAPPEAR_0002 = 14;
private static final int IMG_COURIER_DOC_LEFT_0000 = 15;
private static final int IMG_COURIER_DOC_LEFT_0001 = 16;
private static final int IMG_COURIER_DOC_LEFT_0002 = 17;
private static final int IMG_COURIER_DOC_RIGHT_0000 = 18;
private static final int IMG_COURIER_DOC_RIGHT_0001 = 19;
private static final int IMG_COURIER_DOC_RIGHT_0002 = 20;
private static final int IMG_DIRECTOR = 21;
private static final int IMG_DIRECTOR_LEFT = 22;
private static final int IMG_DIRECTOR_RIGHT = 23;
private static final int IMG_DOC = 24;
private static final int IMG_DOOR_OPENED = 25;
private static final int IMG_HUMBURGER = 26;
private static final int IMG_ICON_LIFE = 27;
private static final int IMG_LIFT_PLACE = 28;
private static final int IMG_MAN_HAND_1 = 29;
private static final int IMG_MAN_HAND_2 = 30;
private static final int IMG_MAN_HAND_DOC_1 = 31;
private static final int IMG_MAN_HAND_DOC_2 = 32;
private static final int IMG_MAN_LIFT = 33;
private static final int IMG_NUMBERS = 34;
private static final int IMG_CLOCK = 35;
private static final int TOTAL_IMAGES_NUMBER = 36;

private static final int SND_FINAL_MMF = 0;
private static final int SND_GIBEL_KURERA_MMF = 1;
private static final int SND_NOVIY_OTPRAVITEL_MMF = 2;
private static final int SND_OTDAL_DOCUMENT_MMF = 3;
private static final int SND_POYAVLENIE_DIRECTORA_MMF = 4;
private static final int SND_START_MMF = 5;
private static final int SND_VZYAL_DOC_MMF = 6;
private static final int SND_VZYAL_GAMBURGER_MMF = 7;

}
