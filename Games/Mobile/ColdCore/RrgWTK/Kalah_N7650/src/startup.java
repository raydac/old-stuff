//----The beginning of the prefix block------
import com.nokia.mid.ui.*;
import javax.microedition.midlet.*;
import javax.microedition.lcdui.*;
import java.io.*;

import com.nokia.mid.ui.FullCanvas;
import com.nokia.mid.sound.Sound;

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
import com.nokia.mid.sound.Sound;
import com.nokia.mid.sound.SoundListener;
import java.util.Vector;
//----The end of the prefix block------
public class startup extends MIDlet implements Runnable, CommandListener
{


protected class insideCanvas extends FullCanvas
    {
        private int i_realScreenWidth,i_realScreenHeight;
        protected boolean lg_painted;

        protected int i_xoffst,i_yoffst;

        public insideCanvas()
        {
            super();
            i_realScreenWidth = getWidth();
            i_realScreenHeight = getHeight();

            // We have to positioning the inside background buffer to the center of the physicaly screen
            i_xoffst = (i_realScreenWidth - SCREENWIDTH) >> 1;
            i_yoffst = (i_realScreenHeight - SCREENHEIGHT) >> 1;

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

/*
            if(i_MIDletState == STATE_GAMEPLAY || i_MIDletState == STATE_DEMOPLAY)
            {
              _graphics.translate(((i_realScreenWidth-GAMESCREEN_WIDTH)>>1)-_graphics.getTranslateX(),
                                 ((i_realScreenHeight-GAMESCREEN_HEIGHT)>>1)-_graphics.getTranslateY());
              _graphics.setClip(0,0,GAMESCREEN_WIDTH,GAMESCREEN_HEIGHT); //see Nokia's defect list
            }
            else
*/
            int i_dx = i_realScreenWidth-SCREENWIDTH;
            int i_dy = i_realScreenHeight-SCREENHEIGHT;

            if(i_dx!=0 || i_dy!=0)
            {
              _graphics.translate((i_dx>>1)-_graphics.getTranslateX(),
                                 (i_dy>>1)-_graphics.getTranslateY());
              _graphics.setClip(0,0,SCREENWIDTH,SCREENHEIGHT);

/*
              int xx = _graphics.getClipX(),
                  yy = _graphics.getClipY(),
                  ww = _graphics.getClipWidth(),
                  hh = _graphics.getClipHeight();


              if(xx == yy && yy == 00 && ww == i_realScreenWidth && hh == i_realScreenHeight)
              {
                 _graphics.setClip(0,0,SCREENWIDTH,SCREENHEIGHT);
              }
                else
                  {
                       _graphics.setClip(xx+i_dx,yy+i_dy,ww,hh);
                  }

              _graphics.translate((i_dx>>1)-_graphics.getTranslateX(),
                                 (i_dy>>1)-_graphics.getTranslateY());
*/
            }

            lg_painted = true;

     paintOnBuffer(_graphics);
            lg_painted = false;


            lg_firstPaint = false;
        }
        protected void showNotify()
        {
           lg_firstPaint = true;
           if(p_Gamelet != null)
             p_Gamelet.i_PlayerKey = 0;//GameletImpl.PLAYER_BUTTON_NONE;
           i_LastPressedKey = 0;
           i_keyRepeatTimeDelay = Integer.MAX_VALUE;
        }
/*
        protected void hideNotify()
        {
        }
*/
    }

// The start of including of Nokia.java
//--------------------------------
    private static final int SCREENWIDTH = 176;
    private static final int SCREENHEIGHT = 208;

    private static final int KEY_MENU = -6;
    private static final int KEY_RIGHT = FullCanvas.KEY_NUM6;
    private static final int KEY_LEFT = FullCanvas.KEY_NUM4;
    private static final int KEY_UP = FullCanvas.KEY_NUM2;
    private static final int KEY_DOWN = FullCanvas.KEY_NUM8;
    private static final int KEY_FIRE = FullCanvas.KEY_NUM5;
    private static final int KEY_CANCEL = -7;
    private static final int KEY_ACCEPT = -6;
    private static final int KEY_BACK = -10;

    private static final int KEY_0 = FullCanvas.KEY_NUM0;
    private static final int KEY_1 = FullCanvas.KEY_NUM1;
    private static final int KEY_2 = FullCanvas.KEY_NUM2;
    private static final int KEY_3 = FullCanvas.KEY_NUM3;
    private static final int KEY_4 = FullCanvas.KEY_NUM4;
    private static final int KEY_5 = FullCanvas.KEY_NUM5;
    private static final int KEY_6 = FullCanvas.KEY_NUM6;
    private static final int KEY_7 = FullCanvas.KEY_NUM7;
    private static final int KEY_8 = FullCanvas.KEY_NUM8;
    private static final int KEY_9 = FullCanvas.KEY_NUM9;

private void playTone(int _freq, long _length, boolean _blocking )
    {
        Sound p_sound = new Sound(_freq, _length);
        p_sound.play(0);
        if (_blocking)
        {
            try
            {
                Thread.sleep(_length);
            }
            catch (InterruptedException e)
            {
            }
        }
    }

private void setDisplayLight(boolean _turnon)
    {
    }

private void activateVibrator(long duration)
    {
    }
//---The end of the included file Nokia.java---

// The start of including of GameDataStorage.java
//--------------------------------

    // Max number of records in the game top list
    private int GDS_MAX_TOPRECORDS;
    //Max number of saved game records in RMS
    private int GDS_MAX_SAVEDRECORDS;
    // Max number of chars in the name of a game saved record
    private int GDS_MAX_CHARSINSAVENAME;
    // Max size of a name in TopList
    private static final int GDS_MAXNAMESIZE = 8;

    /**
     * Id of the boolean field for VIBRATOR option
     */
    private static final int GDS_FIELD_VIBRATOR = 0;

    /**
     * Id of the boolean field for LIGHT option
     */
    private static final int GDS_FIELD_LIGHT = 1;

    /**
     * Id of the boolean field for SOUND option
     */
    private static final int GDS_FIELD_SOUND = 2;

    /**
     * Game related boolean option
     */
    private static final int GDS_FIELD_GAMEOPTION0 = 3;

    /**
     * Game related boolean option
     */
    private static final int GDS_FIELD_GAMEOPTION1 = 4;

    /**
     * Game related boolean option
     */
    private static final int GDS_FIELD_GAMEOPTION2 = 5;

    /**
     * Id of the byte field for LANGUAGE option
     */
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



    /**
     * @param _gameID the unique identifier for current game
     * @param _maxsaveddata max size of a data block where could be saved game data
     * @param _maxcharinname max number chars in a name
     * @param _maxsavedgames max number of saved games
     * @param _maxrecordsintoplist max number of records in the game top list
     * @throws Exception
     */
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

                // Reading of the boolean flags
                gds_i_booleanflags = p_dis.readUnsignedByte();
                // Reading of the language option
                gds_i_language = p_dis.readByte();
                // Redaing of max saved games
                GDS_MAX_SAVEDRECORDS = p_dis.readUnsignedByte();

                // Reading top list
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

                // Reading list of saved games
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
            //throw new Exception(e.getMessage());
        }
    }

    // Calculating of max size of option record
    private int gds_calculateOptionRecordLength()
    {
        return GDS_MAX_SAVEDRECORDS * (GDS_MAX_CHARSINSAVENAME * 2 + 2) + GDS_MAX_TOPRECORDS * (GDS_MAX_CHARSINSAVENAME * 2 + 6) + 3;
    }

    // This function packs all options and saves it to the game storage
    private byte[] gds_packOptionToByteArray() throws Exception
    {
        int i_maxsize = gds_calculateOptionRecordLength();
        ByteArrayOutputStream p_baos = new ByteArrayOutputStream(i_maxsize);
        DataOutputStream p_dos = new DataOutputStream(p_baos);

        // Writing of boolean flags
        p_dos.writeByte(gds_i_booleanflags);
        // Writing of the language parameter
        p_dos.writeByte(gds_i_language);
        // Writing of saved games number
        p_dos.writeByte(GDS_MAX_SAVEDRECORDS);

        // Writing top list
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

        // Writing list of saved games
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

    /**
     * Return DataInputStream object for a saved game for the index
     * @param _index index of the saved game
     * @return DataInputStream object contains game data
     * @throws Exception
     */
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

    /**
     * This function removes a saved game for its name
     * @param _name
     * @throws Exception
     */
    private void gds_removeSavedGame(String _name) throws Exception
    {
        int i_index = gds_convertSavedNameToIndex(_name);
        if (i_index < 0) return;
        gds_as_SavedGamesNames[i_index] = null;

        gds_flush();
    }

    /**
     * This function removes all saved games
     * @throws Exception
     */
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

    /**
     * This function removes all records from the game top list
     */
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

    /**
     * This function saves a game data array to storage for the record name
     * @param _name name of the record, if this name is exists then that record will rewrite
     * @param _dataarray the game data array
     * @throws Exception
     * @throws RecordStoreFullException throws when all game records are filled
     */
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

    /**
     * This function return number of saved games in the storage
     * @return saved games number as an int value
     */
    private int gds_getSavedGamesCount()
    {
        int i_cnt = 0;
        for (int li = 0;li < GDS_MAX_SAVEDRECORDS;li++) if (gds_as_SavedGamesNames[li] != null) i_cnt++;
        return i_cnt;
    }

    // To get a first inactive saved record
    private int gds_getFirstEmptySavedGame()
    {
        for (int li = 0;li < GDS_MAX_SAVEDRECORDS;li++) if (gds_as_SavedGamesNames[li] == null) return li;
        return -1;
    }

    /**
     * Get the index for a saved game name
     * @param _name the name of a saved game
     * @return 0 or more if record is exists or -1 if none
     */
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

    /**
     * Return string view of saved name for index
     * @param _index
     * @return
     */
    private String gds_getSavedGameNameForIndex(int _index)
    {
        return gds_as_SavedGamesNames[_index];
    }

    /**
     * Return DataInputStream object for a saved game for the name
     * @param _name the name of the saved game
     * @return the game data array as a DataInputObect
     * @throws Exception
     */
    private DataInputStream gds_getSavedGameForName(String _name) throws Exception
    {
        int i_indx = gds_convertSavedNameToIndex(_name);
        if (i_indx < 0)
            return null;
        else
            return gds_getSavedGameForIndex(i_indx);
    }

    /**
     * Update game data from the storage to RMS storage
     * @throws Exception
     */
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

    /**
     * Set the value to a boolean option
     * @param _fieldId id of the field
     * @param _value new value
     */
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

    /**
     * Get a boolean option value
     */
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

    /**
     * Adding new record to the game top list
     * @param _name the name of the record
     * @param _scores the scores
     */
    private void gds_addRecordToTopList(String _name,int _score)
    {
      int i=GDS_MAX_TOPRECORDS-1;
      for (;i>=0;i--)
        if (/*(ASCENDING_HIGHSCORE && */_score<gds_ai_TopScores[i]/*) || (!ASCENDING_HIGHSCORE && _score>ai_topscores[i])*/) break;
         else {
           gds_ai_TopScores[i+1]=gds_ai_TopScores[i];
           gds_as_TopNames[i+1]=gds_as_TopNames[i];
         }
       gds_ai_TopScores[i+1]=_score;
       gds_as_TopNames[i+1]=_name;
       if(i<GDS_MAX_TOPRECORDS) gds_lg_isChanged = true;

/*
        int i_indx = GDS_MAX_TOPRECORDS-1;
        gds_as_TopNames[i_indx] = _name;
        gds_ai_TopScores[i_indx] = _scores;
        gds_sortTopList();
        gds_lg_isChanged = true;
*/
    }

    /**
     * Get min score value from the top list
     * @return
     */
    private int gds_getMinScoreInTopList()
    {
        return gds_ai_TopScores[GDS_MAX_TOPRECORDS-1];
/*
        if (gds_as_TopNames[GDS_MAX_TOPRECORDS-1] == null)
             return -1;
         else
             return gds_ai_TopScores[GDS_MAX_TOPRECORDS-1];
*/
    }

    /**
     * Get max score value from the top list
     * @return
     */
    private int gds_getMaxScoreInTopList()
    {
        if (gds_as_TopNames[0] != null) return gds_ai_TopScores[0]; else return -1;
    }

    /**
     * Set the value to an int option
     */
    private void gds_setIntOption(int _fieldId,int _value)
    {
        if (gds_getIntOption(_fieldId) != _value) gds_lg_isChanged = true;
        switch (_fieldId)
        {
            case GDS_FIELD_LANGUAGE:
                gds_i_language = _value;
        }
    }

    /**
     * Get an int value option
     */
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

    // sorting of the game top list
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
//---The end of the included file GameDataStorage.java---

// The start of including of LanguageBlock.java
//--------------------------------
    private String[] lb_as_LanguageNames;
    private String[] lb_as_LanguageIDs;
    private String[] lb_as_LanguageResource;
    private int lb_i_CurrentLanguageIndex = -1;
    private String lb_s_PhoneNativeLanguage;
    private String[] lb_as_TextStringArray;

    /**
     * This function loads texts from a text resource file
     * @param _resourcename Name of file contains text resources
     * @throws IOException
     */
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

    /**
     * Set new language as current
     * @param _index index of new language
     * @throws IOException throws if any error in downloading time
     */
    private void setLanguage(int _index) throws IOException
    {
        if (lb_i_CurrentLanguageIndex == _index || _index >= lb_as_LanguageNames.length || _index < 0) return;
        changeResource(lb_as_LanguageResource[_index]);
        lb_i_CurrentLanguageIndex = _index;
    }

    /**
     * Return index for id of a language
     * @param _lang_id
     * @return
     */
    private int getIndexForID(String _lang_id)
    {
        _lang_id = _lang_id.trim().toLowerCase();
        for (int li = 0;li < lb_as_LanguageIDs.length;li++)
        {
            if (_lang_id.equals(lb_as_LanguageIDs[li])) return li;
        }
        return -1;
    }


    /**
     * @param _language_list Name of the file contains language information
     * @param _language_id Id for language what will be set after downloading or -1 if needs default language
     * @throws IOException
     */
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
//---The end of the included file LanguageBlock.java---

// The start of including of ImageBlock.java
//--------------------------------


// The start of including of standartpng.java
//--------------------------------
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

        // Reading length of image
        int len = _inputstream.readUnsignedShort();
        int i_len = len;

        ByteArrayOutputStream p_baos = new ByteArrayOutputStream(i_len);
        DataOutputStream p_daos = new DataOutputStream(p_baos);

        p_daos.writeLong(0x89504E470D0A1A0Al);


        int i_ln = i_len;
        while (p_baos.size() < (i_ln - 12))
        {
            int i_crc = 0;
            // reading chunk
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
//---The end of the included file standartpng.java---

    private Object[] ib_ImageArray;

    /**
     * @param resource_name File name of resource contains PNG packed images
     * @throws IOException exception which throws when any problem in load or create time of an image
     */
    private void initImageBlock(String resource_name) throws Exception
    {
        Runtime.getRuntime().gc();
        InputStream dis = getClass().getResourceAsStream(resource_name);

        DataInputStream ds = new DataInputStream(dis);


        // Reading of image number
         int num = ds.readUnsignedByte();


        long time_stamp = System.currentTimeMillis();

        ib_ImageArray = new Object[num];

        try
        {
            for (int li = 0;li < num;li++)
            {

                int i_imageflag = ds.readUnsignedByte();

                i_palette_index = 0;//default palette
                if (i_imageflag == 3)
                {
                    //it is a palette
                    if (PNG_COMMON_PALETTE == null) PNG_COMMON_PALETTE = new byte[1][];
      PNG_COMMON_PALETTE[0] = new byte[ds.readUnsignedShort()]; // length of completed PLTE chunk
                    ds.read(PNG_COMMON_PALETTE[0]);
      li--;
                    continue;
                }
                if (i_imageflag == 0x13)
                {
                    //it is a palette set
                    int amount = ds.readUnsignedByte();
                    if (PNG_COMMON_PALETTE == null || PNG_COMMON_PALETTE.length < amount)
                         PNG_COMMON_PALETTE = new byte[amount][];
                    for(int ldi = 0; ldi < amount; ldi++)
                    {
        PNG_COMMON_PALETTE[ldi] = new byte[ds.readUnsignedShort()]; // length of completed PLTE chunk
                      ds.read(PNG_COMMON_PALETTE[ldi]);
                    }
      li--;
                    continue;
                }
                if ((i_imageflag & 0x10)!=0)
                {
                    //it is a palette index
                    i_palette_index = ds.readUnsignedByte();
                    i_imageflag &= ~0x10;
                }

                int i_imageindx = ds.readUnsignedByte();

                if (i_imageflag == 1)
                {
                    //it is a link
                    // reading of a link index
                    Short p_byte = new Short((short)ds.readUnsignedByte());
                    ib_ImageArray [i_imageindx] = p_byte;
                    continue;
                }

                ib_ImageArray [i_imageindx] = null;

                try
                {
                    if ((i_imageflag & 4)!=0)
                    {
                      ds.readUnsignedShort(); // skip offset
                    }



                    if ((i_imageflag & 0x20) != 0)
                    {

                       // outer image

                       if ((i_imageflag & 0x23) == 0x23)
                       {
                         ib_ImageArray [i_imageindx] = Image.createImage(
                              resource_name.substring(resource_name.lastIndexOf('/'))
                              +"/"+i_imageindx+".png"
                         );
                       }

                       // solid image

                       else
                       {
                         int i_len = ds.readUnsignedShort();
                         byte [] p_byte = new byte[i_len];
                         ds.read(p_byte);

                         if ((i_imageflag & 2)!=0)
                         {
                            // Non unpacking
                            ib_ImageArray [i_imageindx] = p_byte;
                         }
                         else
                         {
                            // Creating the image from file
                            ib_ImageArray [i_imageindx] = Image.createImage(p_byte,0,p_byte.length);
                         }
                         p_byte = null;
                       }
                    }

                    else

                    if ((i_imageflag & 2)!=0)
                    {
                        // Non unpacking
                        ib_ImageArray [i_imageindx] = getImageFromInputStream(ds,true);
                    }
                    else
                    {
                        // Creating the image from file
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

            // Links processing
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
            Runtime.getRuntime().gc();
        }
    }

    private Image getImage(int index)
    {

      Object obj = ib_ImageArray[index];
      if(obj instanceof Image) return (Image)obj;
      return Image.createImage((byte[])obj,0,((byte[])obj).length);
    }

//---The end of the included file ImageBlock.java---


// The start of including of MenuBlock.java
//--------------------------------
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
                    // Processing of a back command
                    if (back())
{
initState(i_prevMIDletState);
}
                    return;
                }
            case Command.SCREEN:
                {
                    // Processing of a list item selecting
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
                    // Processing of an user command
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

    /**
     * Reiniting current screen if it is presented
     * @param _force if true then recreate current screen anyway
     */
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

    /**
     * Initing a screen with the offset in the common byte block
     * @param _screenPointer
     */
    private void initScreen(int _screenPointer)
    {
        int i_screenId = _screenPointer;
        closeCurrentScreen();

        // Reading common info for the screen
        int i_temp = ash_menuArray[_screenPointer++];
        int i_ScreenFlags = i_temp >>> 8;
        int i_CaptionId = i_temp & 0xFF;

        int i_itemTypesFlag = ash_menuArray[_screenPointer++];

        String s_ScreenCaption = lb_as_TextStringArray[i_CaptionId];

        int i_ErrorScreen = ash_menuArray[_screenPointer++];
        int i_OkScreen = ash_menuArray[_screenPointer++];

        // Reading item number
        int i_ItemNumber = ash_menuArray[_screenPointer++];
        boolean lg_isForm = false;

        if ((i_ScreenFlags & SCREEN_FLAG_CUSTOMSCREEN) != 0)
        {
            // Filling the screen by a customer
            p_Form = customScreen(i_screenId);
            if (p_Form instanceof Form) lg_isForm = true;
        }
        else
        {
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
                            // Processing a menu item
                            String s_String = lb_as_TextStringArray[i_StringId];
                            Image p_Image = null;
                            if (i_ImageId>=0) p_Image = (Image) ib_ImageArray[i_ImageId];
                            ((List) p_Form).append(s_String, p_Image);
                        }
                        ;
                        break;
                    case ITEM_TEXTBOX:
                        {
                            // Processing a textbox item
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

    /**
     * Clearing the screen stack and removing current screen
     */
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

    /**
     * Return the ID of the previous screen or -1
     * @return the screen ID or -1
     */
    private int getPreviousScreenID()
    {
        int i_indx = i_MenuStackPointer - 1;
        if (i_indx < 0)
            return -1;
        else
            return ash_screenStack[i_indx];
    }

    /**
     * Out the Ok screen for the current screen if it is presented
     */
    private void viewOkScreen()
    {
        if (currentOkScreen < 0) return;
        initScreen(currentOkScreen);
    }

    /**
     * Out the Error screen for the current screen if it is presented
     */
    private void viewErrorScreen()
    {
        if (currentErrorScreen < 0) return;
        initScreen(currentErrorScreen);
    }

    /**
     * Close current screen and return to previous menu in the menu stack
     * @return true if the menu stack is empty else return false
     */
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

    /**
     * Close current screen and return to back in the screen number in the menu stack
     * @param _depth the number of screens to back
     * @return true if the menu stack is empty else return false
     */
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

//---The end of the included file MenuBlock.java---

// The start of including of SoundBlock.java
//--------------------------------
    protected Melody[] ap_melody;

// The start of including of NokiaCommon.java
//--------------------------------
    private final int __PLAYING_BLOCKED_MELODY_TIMEOUT = 30000;
    private final int __NO_CONCURRENT_PLAYING_TIMEOUT = 100;

    private Vector _melodies_vector = new Vector();
    private long _i_timestamp = 0;


    private Melody p_playedmelody;
    private boolean lg_blockingmelody;

    public void _initSoundBlock()
    {
        p_playedmelody = null;
        lg_blockingmelody = false;
    }

    public void convertMelody(Melody _melody) throws Exception
    {

        int i_type;

        switch (_melody.i_MelodyType)
        {
            case Melody.MELODY_TYPE_OTT:
                i_type = Sound.FORMAT_TONE;
                break;
            case Melody.MELODY_TYPE_WAV:
                i_type = Sound.FORMAT_WAV;
                break;
            case Melody.MELODY_TYPE_AMR:
                i_type = Sound.FORMAT_WAV;
                break;
            default :
                throw new IOException("Unsupported melody type #"+_melody.i_MelodyType);
        }

        try
        {
           Sound p_sound = new Sound(_melody.getMelodyArray(), i_type);
           //p_sound.setGain(i_Volume);
           _melody.p_NativeFormat = p_sound;
           _melody.removeLoadedArray();

           _melodies_vector.addElement(_melody);
        }
         catch(Exception e) {}
    }

    public void stopMelody(Melody _melody)
    {
//        if(_melody != null && _melody.p_NativeFormat != null)
        {
            synchronized (_melody)
            {

                if (((Sound) _melody.p_NativeFormat).getState() == Sound.SOUND_PLAYING)
                {
                    ((Sound) _melody.p_NativeFormat).stop();
                }

                if (p_playedmelody == _melody)
                {
                    p_playedmelody = null;
                }
            }
        }
    }

    public void stopAllMelodies()
    {
            for (int li = _melodies_vector.size()-1; li >=0 ; li--)
            {
                stopMelody((Melody)_melodies_vector.elementAt(li));
            }
    }

    public void soundStateChanged(Sound sound, int event)
    {
        Melody p_melody = p_playedmelody;
        if (p_melody == null) return;

        if (event == com.nokia.mid.sound.Sound.SOUND_STOPPED)
          {
                    if (lg_blockingmelody)
                    {
                        synchronized (p_melody)
                        {
                            p_melody.notify();
                        }
                    }
                  //  if (!lg_blockingmelody && p_melodyeventlistener != null) p_melodyeventlistener.melodyEnd(p_melody.getMelodyID());
                    p_melody = null;
                    lg_blockingmelody = false;
          }
    }

    public void playMelody(Melody _melody, boolean _blocking)
    {

       try
       {
           if(
           !lg_SoundEnabled ||
           _melody == null  ||  _melody.p_NativeFormat == null) return;


          if (_blocking)
          {
              int block_timeout;
              stopAllMelodies();
              Sound p_sound = (Sound) _melody.p_NativeFormat;
              p_playedmelody = _melody;
              lg_blockingmelody = true;
              block_timeout = _melody.i_MelodyLength == 0? __PLAYING_BLOCKED_MELODY_TIMEOUT : _melody.i_MelodyLength;
              p_sound.play(1);

              try
              {
                  synchronized (_melody)
                  {
                      _melody.wait(block_timeout);
                  }
              }
              catch (InterruptedException e)
              {
                  return;
              }
              p_playedmelody = null;
          }
          else
          {

//              long stamp = i_SystemTimeMark;
              if(p_playedmelody == null
                || ((Sound)p_playedmelody.p_NativeFormat).getState() != Sound.SOUND_PLAYING
                || _melody.i_MelodyID > p_playedmelody.i_MelodyID
//              || stamp - _i_timestamp >= __NO_CONCURRENT_PLAYING_TIMEOUT
              )

              {

//                  stopAllMelodies();
//                  _i_timestamp = stamp;
// /*
                  stopMelody(_melody);
                  if( p_playedmelody != null && p_playedmelody != _melody)
                  {
                       stopMelody( p_playedmelody);
                  }
// */
                  p_playedmelody = _melody;
                  lg_blockingmelody = false;

                  Sound p_sound = (Sound) _melody.p_NativeFormat;
                  p_sound.play(1);
              }
          }
       } catch(IllegalArgumentException e){}
    }
//---The end of the included file NokiaCommon.java---

    public void initSoundBlock(String resource_name) throws Exception
    {
        Runtime.getRuntime().gc();
        InputStream dis = getClass().getResourceAsStream(resource_name);

        DataInputStream ds = new DataInputStream(dis);
        // Reading of melody number
        int num = ds.readUnsignedByte();

        ap_melody = new Melody[num];

        for (int li = 0; li < num; li++)
        {
            ap_melody[li] = new Melody(li, ds);
            // Reading length of image
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
//---The end of the included file SoundBlock.java---



    protected Image p_InsideDoubleBuffer;
    protected Graphics p_InsideDoubleBufferGraphics;
    private boolean lg_FrontPageView;
    private int i_gameTimeDelay;
    private int i_LastGameScores;
    private int i_GamePlayerState;
private GameletImpl p_Gamelet;
    private String s_gameNameForSaving;
    private int i_gameIndexForRewriting;
    private boolean lg_gameIsStopped;


    private boolean lg_SoundEnabled;
    private boolean lg_MainThemeSound = true;

    //Color section
    private static final int BACKGROUND_COLOR = 0x000080;
    private static final int FONT_COLOR = 0x00FF00;

    // The inside main canvas
    private insideCanvas p_Canvas;
    // The display for the midlet
    private Display p_Display;
    private boolean lg_Active;
    // The inside timer
    private int i_Timer;
    protected int i_MIDletState = -1;
    private int i_prevMIDletState;
    private int i_LastUserCommand;

    private int i_itemsLoaded;
    private int i_totalItems = TOTAL_IMAGES_NUMBER + 30;

    private int i_offsetOfNamesForSave;

    // This flag shows the paint function has been called the first time after state changing
    private boolean lg_FirstRepaintAfterStateChanging;
    protected boolean lg_firstPaint;

    // This flag shows the final picture has been showed
    protected boolean lg_FinishImagePictured;
    // The selected game level
    private int i_selectedGameLevel;
    private boolean lg_startGameAfterDownloading;

    // ! Game variables
    private Image logo;
    private Image cancel_button_image;

    private static final int GAMESCREEN_WIDTH = SCREENWIDTH;
    private static final int GAMESCREEN_HEIGHT = SCREENHEIGHT;



//==================MIDLet========================================================

    // The loading state
    protected static final int STATE_LOADING = 0;
    // The front page viewing state
    protected static final int STATE_FRONTPAGE = 1;
    // The demoplay state
    protected static final int STATE_DEMOPLAY = 2;


    // Game state of playing
    protected static final int STATE_GAMEPLAY = 4;
    // The state of the ending of a game and checking of game scores
    protected static final int STATE_GAMEOVER = 5;
    // Output of a finish image
    protected static final int STATE_FINISHIMAGE = 6;
    // The menu viewing state
    protected static final int STATE_MENU = 7;

    private static final int TIMEDELAY_FRONTPAGE = 60*1000/200;  // 1 minute delay before demo, 200ms - sleep time
    private static final int TIMEDELAY_FINISHIMAGE = 25;
    private static final int ANTICLICKDELAY_FINISHIMAGE = 10;
    private static final int TIMEDELAY_DEMOPLAY = 150;

    protected void startApp() throws MIDletStateChangeException
    {
//        if (i_MIDletState == STATE_MENU) return;
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


        initState(STATE_LOADING);


p_Gamelet = new GameletImpl(GAMESCREEN_WIDTH,GAMESCREEN_HEIGHT,this,null);

        new Thread(this).start();
    }

    private void initState(int _state)
    {
        lg_FirstRepaintAfterStateChanging = true;
        lg_firstPaint = true;


        if (_state != STATE_LOADING) stopAllMelodies();

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
        pause();
    }

    public void run()
    {
        int i_timeDelay = 0;
        while (lg_Active)
        {
            switch (i_MIDletState)
            {
                case STATE_LOADING:
                    {
                        try
                        {
InitGameDataStorage(p_Gamelet.getGameID(), p_Gamelet.getMaxSizeOfSavedGameBlock(), 8, 6, 10);
                            int i_lngindx = gds_getIntOption(GDS_FIELD_LANGUAGE);
                            lg_SoundEnabled = gds_getBooleanOption(GDS_FIELD_SOUND);
                            initLanguageBlock("/res/langs.bin", i_lngindx);
                            i_itemsLoaded = 10;
                            paintOnBuffer();
                            //p_Canvas.repaint();
                            initMenuBlock("/res/menu.bin", p_Canvas);
                            i_itemsLoaded += 20;
                            paintOnBuffer();
                            //p_Canvas.repaint();
                            initImageBlock("/res/images.bin");
                            //p_Canvas.repaint();
                            initSoundBlock("/res/sound.bin");
                            i_itemsLoaded += 30;
                            paintOnBuffer();
                            //p_Canvas.repaint();
                            initState(STATE_FRONTPAGE);

       logo = null;
                            cancel_button_image = null;

                            {
                             int [] sort_idx = {
SND_PRIGLASHENIE_K_HODU_IGROKA_OTT,
SND_HOD_IGROKA_OTT,
SND_SMENA_POZITSII_KURSORA_OTT,
SND_LOST_THEME_OTT,
SND_WON_THEME_OTT,
SND_ZASTAVKA_OTT
                             };
                             for (int i = 0; i < sort_idx.length; i++)
                             {
                                if (ap_melody[sort_idx[i]] != null)
                                {
                                    ap_melody[sort_idx[i]].i_MelodyID = i;
                                }
                             }
                            }

                            continue;
                        }
                        catch (Exception e)
                        {
                            viewAlert("Loading error", e.getMessage(), AlertType.ERROR, Alert.FOREVER);
                            try
                            {
                                Thread.sleep(5000);
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
                            || p_Gamelet.i_PlayerState != Gamelet.PLAYERSTATE_NORMAL)
                        {
                            endGame();
                            initState(STATE_FRONTPAGE);
                            continue;
                        }
// The start of including of _ProcessDemo.java
//--------------------------------
        //---------Insert the keyPressed processing block here


             final int keyset[] = {Canvas.LEFT,Canvas.RIGHT,Canvas.UP,Canvas.DOWN,Canvas.FIRE};

             if((i_Timer & 1) == 0)
             {
               int _key = p_Canvas.getKeyCode(keyset[p_Gamelet.getRandomInt(keyset.length-1)]);
// The start of including of _KeyPressed.java
//--------------------------------
        //---------Insert the keyPressed processing block here

        switch(_key)
        {
            case Canvas.KEY_NUM4 :
                {
                    p_Gamelet.i_PlayerKey = GameletImpl.PLAYERKEY_LEFT;
                }
                ;
                break;
            case Canvas.KEY_NUM6 :
                {
                    p_Gamelet.i_PlayerKey = GameletImpl.PLAYERKEY_RIGHT;
                }
                ;
                break;
            case Canvas.KEY_NUM2 :
                {
                    p_Gamelet.i_PlayerKey = GameletImpl.PLAYERKEY_UP;
                }
                ;
                break;
            case Canvas.KEY_NUM8 :
                {
                    p_Gamelet.i_PlayerKey = GameletImpl.PLAYERKEY_DOWN;
                }
                ;
                break;
            case Canvas.KEY_NUM5 :
                {
                    p_Gamelet.i_PlayerKey = GameletImpl.PLAYERKEY_SELECT;
                }
                ;
                break;
            default :
                switch (p_Canvas.getGameAction(_key))
                {
                   case Canvas.LEFT:
                       {
                           p_Gamelet.i_PlayerKey = GameletImpl.PLAYERKEY_LEFT;
                       }
                       ;
                       break;
                   case Canvas.RIGHT :
                       {
                           p_Gamelet.i_PlayerKey = GameletImpl.PLAYERKEY_RIGHT;
                       }
                       ;
                       break;
                   case Canvas.UP :
                       {
                           p_Gamelet.i_PlayerKey = GameletImpl.PLAYERKEY_UP;
                       }
                       ;
                       break;
                   case Canvas.DOWN :
                       {
                           p_Gamelet.i_PlayerKey = GameletImpl.PLAYERKEY_DOWN;
                       }
                       ;
                       break;
                   case Canvas.FIRE :
                       {
                           p_Gamelet.i_PlayerKey = GameletImpl.PLAYERKEY_SELECT;
                       }
                       ;
                       break;
                     default:
                          {
                               p_Gamelet.i_PlayerKey = GameletImpl.PLAYERKEY_NONE;
                          }
                }
        }
//---The end of the included file _KeyPressed.java---
             }



//---The end of the included file _ProcessDemo.java---
                    }
                case STATE_GAMEPLAY:
                    {

                        i_timeDelay = i_gameTimeDelay;
                        if (p_Gamelet.i_PlayerState == Gamelet.PLAYERSTATE_NORMAL)
                        {
                            long l_startTime = System.currentTimeMillis();
                            lg_keyLock = true;
                            // synchronized (p_Gamelet)
                            {


                                if(--i_keyRepeatTimeDelay <= 0)
                                {
                                   i_keyRepeatTimeDelay = i_keyTimeDelayLatch;
                                   p_Gamelet.i_PlayerKey = i_LastPressedKey;
                                }
                                p_Gamelet.nextGameStep(p_Gamelet);
                                p_Gamelet.i_PlayerKey = 0;

                            }
                            lg_keyLock = false;
                            paintOnBuffer();
                            i_timeDelay = Math.min(i_timeDelay,i_timeDelay - (int) (System.currentTimeMillis() - l_startTime));
                            if (i_timeDelay <= 0) i_timeDelay = 1;
                        }
                        else
                        {

                            if (p_Gamelet.i_GameState == Gamelet.GAMESTATE_OVER)
                            {
                                i_LastGameScores = p_Gamelet.getPlayerScore();
                                i_GamePlayerState = p_Gamelet.i_PlayerState;


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
                                p_Gamelet.resumeGameAfterPlayerLost();
                                p_Gamelet.nextGameStep(p_Gamelet);
                            }
                        }
                    }
                    ;
                    break;
                case STATE_FRONTPAGE:
                    {
                         if(lg_MainThemeSound && ap_melody!=null)
                         {
                            Melody theme = ap_melody[SND_ZASTAVKA_OTT];
                            if(theme != null)
                            {
                               i_Timer -= theme.i_MelodyLength/200/*i_timeDelay*/;
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

                           if (i_GamePlayerState == Gamelet.PLAYERSTATE_LOST)
                           {
                              theme = ap_melody[SND_LOST_THEME_OTT];
                           }
                           else
                           {
                              theme = ap_melody[SND_WON_THEME_OTT];
                           }
                           if(theme != null)
                           {
                               i_Timer -= theme.i_MelodyLength/200/*i_timeDelay*/;
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


            i_Timer++;
        }
    }

    //Цикл отработки отрисовки
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
          //p_Canvas.repaint();
        lg_FirstRepaintAfterStateChanging = false;
    }
//====================================================================================================================

//========================Drawing block=========================================================
    private void loadingProcessDrawing(Graphics _graphics)
    {
// The start of including of _loadingProcessDrawing.java
//--------------------------------
        //---------------Insert the game drawing block here

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

           i_y -= (i_Height + i_barHeight /*bar height*/ + i_barGap /*gap*/ )>>1;

           int y = i_y + i_Height + i_barGap;

           i_barHeight--;
           y--;

           if(i_fillWidth>0)
           {
             int color = 14483456;
             // get 25% darken
             int darken = 14483456 - ((14483456 >> 2) & 0x3f3f3f);
             // get 50% lighten
             int lighten = 14483456 + (((0xffffff - 14483456) >> 1) & 0x7f7f7f);

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
             _graphics.setColor(16772846);
             _graphics.fillRect(i_x+i_fillWidth,y,i_Width - i_fillWidth,i_barHeight);
           }
        }
         _graphics.drawImage(logo,i_x,i_y,0);

        }

       {
        if(cancel_button_image == null)
        try
        {
         cancel_button_image = Image.createImage("/res/button_cancel.png");
        }catch(Exception e)
        {
            cancel_button_image = Image.createImage(1,1);
        }
         {
            _graphics.drawImage(cancel_button_image,
              SCREENWIDTH - cancel_button_image.getWidth()
              +-10

            ,SCREENHEIGHT - cancel_button_image.getHeight()

              +-2
            ,0);
         }
       }

//---The end of the included file _loadingProcessDrawing.java---
    }

    private void FrontFinishPageDrawing(Graphics _graphics, boolean _frontpage)
    {
// The start of including of _FrontFinishPageDrawing.java
//--------------------------------
        try
        {
            Image p_imageForOutput = null;
            if (_frontpage)
            {
                // Drawing the front page
p_imageForOutput = getImage(0);
            }
            else if (i_GamePlayerState == Gamelet.PLAYERSTATE_LOST)
            {
                // Drawing the lost image
p_imageForOutput = getImage(1);
            }
            else
            {
                // Drawing the win image
p_imageForOutput = getImage(2);
            }

            _graphics.drawImage(p_imageForOutput,0,0,0);

            if (!_frontpage)
            {

                    _graphics.drawImage(getImage(p_Gamelet.i_PlayerState == GameletImpl.PLAYERSTATE_LOST?IMG_YOU_LOST:IMG_YOU_WIN),58,28,0);
                    int y = 68;
                    int x = 80;

                    final_font.drawDigits(_graphics,x,y,2,p_Gamelet.getPlayerScore(),10); x+=final_font.fontWidth<<1;
                    final_font.drawDigits(_graphics,x,y,1,10,11); x+=final_font.fontWidth;
                    final_font.drawDigits(_graphics,x,y,2,p_Gamelet.getAIScore(),10);
            }
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

//---The end of the included file _FrontFinishPageDrawing.java---
    }

    /**
     * This function implements the game drawing onto the buffer
     * @param _graphics
     */
    private void gamePlayDrawing(Graphics _graphics)
    {

        if(!lg_firstPaint)
        {
          synchronized (p_Gamelet)
          {
            if (lg_gameIsStopped) return;
          }
        }

        {
// The start of including of _GameDrawing.java
//--------------------------------
        int index;
        Image img;
        HoleStruct hStr;




////////////////////////////////////


        //---------------Insert the game drawing block here



////////////////////////////////////

         _graphics.drawImage(Desk,0,0,0);

         // draw selection
         index = -1;
         if (p_Gamelet.i_currentState == GameletImpl.KALAH_STATE_PLAYER_SELECTING_HOLE)
         {
             index = p_Gamelet.i_selectedPosition;
         }
           else
             if (p_Gamelet.i_currentState == GameletImpl.KALAH_STATE_AI_SELECTED_HOLE)
             {
                  index = p_Gamelet.i_aiHole;
             }
         if(index >= 0 && index < 13)
         {
             i_clockFrame = -1;

             hStr = p_Gamelet.hsHoles[index];
             if(!p_Gamelet.lg_FinalMoving)
             {
                 hole.drawDigits(_graphics,hStr.x0-HOLEFRAME_OFFSET, hStr.y0-1,1,i_Timer % 10,10);
             }
         }

         // draw flying stone
         if (p_Gamelet.p_FlyStone.lg_SpriteActive)
         {
             Sprite spr = p_Gamelet.p_FlyStone;
             index = fly_frames[p_Gamelet.i_FlyIndex*fly_frames.length/(GameletImpl.FLY_PATH_STEPS+2)];
             img = getImage(stone_images[spr.i_Color%stone_images.length]+index);
             _graphics.drawImage(img, spr.i_mainX + 3 - (img.getWidth()>>1), spr.i_mainY + 3 - (img.getHeight()>>1), 0);
         }


         if(i_clockFrame>=0)
         {
             clock.drawDigits(_graphics,SCREENWIDTH - clock.fontWidth>>1,SCREENHEIGHT - clock.fontHeight>>1,1,i_clockFrame,8);
         }





////////////////////////////////////


//        _graphics.setColor(0x0000FF);
//        _graphics.fillRect(0, 0, SCREENWIDTH, SCREENHEIGHT);
//        _graphics.setColor(0xFFFFFF);
//        _graphics.drawString("Game Screen", SCREENWIDTH>>1,(SCREENHEIGHT>>1)-10, Graphics.HCENTER|Graphics.TOP);

//---The end of the included file _GameDrawing.java---

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

//===============================================================
    /**
     * Processing a command for an item
     * @param _screenId  the ID of current screen
     * @param _itemId the ID of the item
     */
    private void processListItem(int _screenId, int _itemId)
    {
        switch (_screenId)
        {
            case SCR_MainMenuSCR:
                {
                    switch (_itemId)
                    {
                        // Resume Game
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
                    // This block allows to select the game difficult level
                    int i_level;
                    switch (_itemId)
                    {
                        // Easy game mode
                        case ITEM_EasyModeITM:
                            {
                                i_level = 0;
                            }
                            ;
                            break;
                            // Normal game mode
                        case ITEM_NormalModeITM:
                            {
                                i_level = 1;
                            }
                            ;
                            break;
                            // Hard game mode
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

    /**
     * Process a command by the user
     * @param _screenId  the ID of current screen
     * @param _commandId the id of a command
     * @param _selectedId the id of the selected item in the list, for forms everytime -1
     */
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
                        // We have "New record" option
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

    /**
     * Allowing to add command in the command list
     * @param _screenId  the ID of current screen
     * @param _commandId
     * @return
     */
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
                    if (true) return false; // Disable possibility of elimination the games from list, ergonomic reason.

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

    /**
     * Fill a custom screen by the user
     * @param _screenId  the ID of current screen
     * @return new screen as a displayable object
     */
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

    /**
     * Create a custom item by the user
     * @param _screenId  the ID of current screen
     * @param _itemId the ID of the item
     * @param _getImage true if it is waited an Image object for List screen and false if it is waited a String object
     * @return new item as an Item object (for Form) or String and Image objects (for List)
     */
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

    /**
     * Allowing output of an optional item
     * @param _screenId  the ID of current screen
     * @param _itemId the ID of the item
     * @return true if the item is allowed for the screen or else if it is not allowed
     */
    private boolean enableItem(int _screenId, int _itemId)
    {
        switch (_screenId)
        {
            case SCR_SaveLoadSCR:
                {
                    switch (_itemId)
                    {
                        case ITEM_SaveGameITM:
                            if(lg_keyLock) return false;
                            if (i_prevMIDletState == STATE_GAMEPLAY
                            && p_Gamelet.i_GameState == GameletImpl.GAMESTATE_PLAYED)
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
                                return false;
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

    /**
     * Processing an exit event for a screen
     * @param _screen a displayable object reflects the screen
     * @param _screenId the screen ID of the screen
     */
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
//========================Game processing==============================================================
    private void startGame()
    {
        lg_firstPaint = true;
        if (!lg_startGameAfterDownloading)
        {
            p_Gamelet.newGameSession(i_selectedGameLevel < 0 ? 0 : i_selectedGameLevel);

        }
// The start of including of _AfterNewGameSessionAndAfterLoading.java
//--------------------------------
    i_LastPressedKey = 0;
    i_keyRepeatTimeDelay = Integer.MAX_VALUE;

    // You should place your functionality here
    //=================================================================================

                       initDesk();

    //=================================================================================
//---The end of the included file _AfterNewGameSessionAndAfterLoading.java---

        if (i_selectedGameLevel >= 0)
        {
            initState(STATE_GAMEPLAY);
        }

        p_Gamelet.i_PlayerKey = 0;                   // PLAYER_BUTTON_NONE;
        i_gameTimeDelay = p_Gamelet.i_GameTimeDelay;
        lg_gameIsStopped = false;

         i_keyTimeDelayLatch = KEY_REPEAT_DELAY * (1000 / 10) / Math.max(i_gameTimeDelay,1);

    }

    private byte[] saveGame() throws Exception
    {
        ByteArrayOutputStream p_baos = new ByteArrayOutputStream(p_Gamelet.getMaxSizeOfSavedGameBlock());
        DataOutputStream p_daos = new DataOutputStream(p_baos);
        p_Gamelet.saveGameState(p_daos);
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
        p_Gamelet.loadGameState(_stream);
        initState(STATE_GAMEPLAY);
    }

    private void endGame()
    {
        //if (i_MIDletState == STATE_DEMOPLAY || i_MIDletState == STATE_GAMEPLAY || i_MIDletState == STATE_MENU)
        {
            synchronized (p_Gamelet)
            {
                p_Gamelet.endGameSession();
                lg_gameIsStopped = true;

// The start of including of _AfterEndGameSession.java
//--------------------------------
    // You should place your functionality here
    //=================================================================================



    //=================================================================================
//---The end of the included file _AfterEndGameSession.java---

            }
            Runtime.getRuntime().gc();
        }
    }
//========================Key processing block=========================================================

      final static int KEY_REPEAT_DELAY = 5; // in hundreds of miliseconds (0,1 sec)
      int i_keyRepeatTimeDelay;
      int i_LastPressedKey;
      int i_keyTimeDelayLatch;
      boolean lg_keyLock;


    protected void KeyPressed(int _key)
    {

        switch (i_MIDletState)
        {
            case STATE_LOADING:
                {
                       if (_key == KEY_CANCEL) notifyDestroyed();
                }
                ;
                break;
            case STATE_FRONTPAGE:
                {
if (lg_FinishImagePictured)
                    initState(STATE_MENU);
                }
                ;
                break;
            case STATE_GAMEPLAY:
                {
                    //if(lg_keyLock) break;

                    synchronized (p_Gamelet)
                    {
                        if (_key == KEY_MENU || _key == KEY_CANCEL) initState(STATE_MENU);
// The start of including of _KeyPressed.java
//--------------------------------
        //---------Insert the keyPressed processing block here

        switch(_key)
        {
            case Canvas.KEY_NUM4 :
                {
                    p_Gamelet.i_PlayerKey = GameletImpl.PLAYERKEY_LEFT;
                }
                ;
                break;
            case Canvas.KEY_NUM6 :
                {
                    p_Gamelet.i_PlayerKey = GameletImpl.PLAYERKEY_RIGHT;
                }
                ;
                break;
            case Canvas.KEY_NUM2 :
                {
                    p_Gamelet.i_PlayerKey = GameletImpl.PLAYERKEY_UP;
                }
                ;
                break;
            case Canvas.KEY_NUM8 :
                {
                    p_Gamelet.i_PlayerKey = GameletImpl.PLAYERKEY_DOWN;
                }
                ;
                break;
            case Canvas.KEY_NUM5 :
                {
                    p_Gamelet.i_PlayerKey = GameletImpl.PLAYERKEY_SELECT;
                }
                ;
                break;
            default :
                switch (p_Canvas.getGameAction(_key))
                {
                   case Canvas.LEFT:
                       {
                           p_Gamelet.i_PlayerKey = GameletImpl.PLAYERKEY_LEFT;
                       }
                       ;
                       break;
                   case Canvas.RIGHT :
                       {
                           p_Gamelet.i_PlayerKey = GameletImpl.PLAYERKEY_RIGHT;
                       }
                       ;
                       break;
                   case Canvas.UP :
                       {
                           p_Gamelet.i_PlayerKey = GameletImpl.PLAYERKEY_UP;
                       }
                       ;
                       break;
                   case Canvas.DOWN :
                       {
                           p_Gamelet.i_PlayerKey = GameletImpl.PLAYERKEY_DOWN;
                       }
                       ;
                       break;
                   case Canvas.FIRE :
                       {
                           p_Gamelet.i_PlayerKey = GameletImpl.PLAYERKEY_SELECT;
                       }
                       ;
                       break;
                     default:
                          {
                               p_Gamelet.i_PlayerKey = GameletImpl.PLAYERKEY_NONE;
                          }
                }
        }
//---The end of the included file _KeyPressed.java---
                        i_LastPressedKey = p_Gamelet.i_PlayerKey;
                        i_keyRepeatTimeDelay = i_keyTimeDelayLatch;
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
                    //synchronized (p_Gamelet)
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

        i_LastPressedKey = 0;
        i_keyRepeatTimeDelay = Integer.MAX_VALUE;


    }

//========================Midlet managing block=========================================================
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
    }

    //=========================Game actions (You must or implement this functions or remove=========
// The start of including of _GameActions.java
//--------------------------------

    protected void gameAction(int actionID)
    {
       switch(actionID)
       {
            case GameletImpl.GAMEACTION_SND_PLAYERMOVE         : playMelody(ap_melody[SND_HOD_IGROKA_OTT],false);break;
            case GameletImpl.GAMEACTION_SND_PLAYERGRABSTONES   : break;
            case GameletImpl.GAMEACTION_SND_WRONGSELECTION     : break;
            case GameletImpl.GAMEACTION_SND_PLAYERGRABAISTONES : break;
            case GameletImpl.GAMEACTION_SND_AIGRABPLAYERSTONES : break;
            case GameletImpl.GAMEACTION_SND_SELECTIONMOVE      : playMelody(ap_melody[SND_SMENA_POZITSII_KURSORA_OTT],false);break;
            case GameletImpl.GAMEACTION_SND_PLAYERTURN         : playMelody(ap_melody[SND_PRIGLASHENIE_K_HODU_IGROKA_OTT],false);break;
          case GameletImpl.GAMEACTION_SHOW_CLOCK:
                   {
                             i_clockFrame = ++i_clockFrame % 8;
                             //paintOnBuffer();
                             p_Canvas.repaint(SCREENWIDTH - clock.fontWidth>>1, SCREENHEIGHT - clock.fontHeight>>1
                                             ,clock.fontWidth,clock.fontHeight);
/*
                             Thread.yield();
                             try
                             {
                                Thread.sleep(i_timeDelay);
                             }
                             catch (InterruptedException e)
                             {}

*/                             p_Canvas.serviceRepaints();

                   }
                   break;
       }
    }

    protected void gameAction(int actionID,int param0)
    {
       if(actionID == GameletImpl.GAMEACTION_PUT_STONE)
            addStoneToHole(param0);
          else
               drawHole(param0);
    }

    protected void gameAction(int actionID,int param0,int param1)
    {

    }

    protected void gameAction(int actionID,int param0,int param1,int param2)
    {

    }

//---The end of the included file _GameActions.java---
    //==============================================================================================

// The start of including of _CommonFunctions.java
//--------------------------------

    // You should place your own functionality of the startup class here
    //=================================================================================


   private final static int HOLEFRAME_OFFSET = 2;

   drawDigits font, final_font, hole, clock;
   Image Desk;
   Graphics gDesk;

   int [] fly_frames = new int[] {0,1,1,2,1,1,0};
   int i_clockFrame;



   private void initDesk()
   {
      if (Desk == null)
      {
         Desk = Image.createImage(GAMESCREEN_WIDTH,GAMESCREEN_HEIGHT);
         gDesk = Desk.getGraphics();
         font = new drawDigits(getImage(IMG_DESKNUMBERS),10);
         final_font = new drawDigits(getImage(IMG_NUMBERS),11);
         hole = new drawDigits(getImage(IMG_LUNKA),10);
         clock = new drawDigits(getImage(IMG_CLOCK),8);
      }
      gDesk.drawImage(getImage(IMG_BACKGROUND),0,0,0);
      for(int i = 0; i< LayoutDefs.HOLES_COUNT; i++)
          drawHole(i);
   }

   int []stone_images = new int[]{IMG_SHAR01_01,IMG_SHAR02_01,IMG_SHAR03_01,IMG_SHAR04_01
                                 ,IMG_SHAR05_01,IMG_SHAR06_01};
   private void drawHole(int n)
   {
         if(gDesk == null) return;
         HoleStruct hStr = p_Gamelet.hsHoles[n];

         gDesk.setClip(hStr.x0, hStr.y0, hStr.w, hStr.h);
         gDesk.drawImage(getImage(IMG_BACKGROUND), 0, 0, 0);

         hStr.stonesCount = p_Gamelet.getStonesCountByIndex(n);
         int SCount = p_Gamelet.GetStoneCount(n);
         Image img;
         Point coord;

         for(int j=0; j<SCount ;j++)
         {
             img = getImage(stone_images[p_Gamelet.GetStoneColor(n,j)%stone_images.length]);
             coord = p_Gamelet.GetCoords(n, j);
             gDesk.drawImage(img, coord.x, coord.y, 0);
         }

        writeAmountOfStones(n, SCount);
   }

   private void addStoneToHole(int n)
   {
         if(gDesk == null) return;
         HoleStruct hStr = p_Gamelet.hsHoles[n];

         gDesk.setClip(hStr.x0, hStr.y0, hStr.w, hStr.h);

         hStr.stonesCount = p_Gamelet.getStonesCountByIndex(n);
         int count = p_Gamelet.GetStoneCount(n)-1;

         Image img = getImage(stone_images[p_Gamelet.GetStoneColor(n,count)%stone_images.length]);
         Point coord = p_Gamelet.GetCoords(n, count);
         gDesk.drawImage(img, coord.x, coord.y, 0);

         writeAmountOfStones(n, ++count);
   }

   private void writeAmountOfStones(int n, int SCount)
   {
        HoleStruct hStr = p_Gamelet.hsHoles[n];
        int amount = SCount>9 ? 2 : 1;
        int x = hStr.x0 + (hStr.w - amount * font.fontWidth >>1) - HOLEFRAME_OFFSET;
        int y;
        if(n <= 6)
          y = hStr.y0 + hStr.h + (font.fontHeight>>1);
         else
          y = hStr.y0 - font.fontHeight - (font.fontHeight>>1);


        //  p_gamelet.StoneArray[f][0]
        gDesk.setClip(hStr.x0 + (hStr.w>>1) - font.fontWidth,y,font.fontWidth<<1,font.fontHeight);
        gDesk.drawImage(getImage(IMG_BACKGROUND), 0, 0, 0);

        if(SCount>0)
             font.drawDigits(gDesk, x, y, amount, SCount, 10);

        gDesk.setClip(0,0,Desk.getWidth(),Desk.getHeight());
   }

//////////////////////////////////////////////////////////////////////







    //=================================================================================
//---The end of the included file _CommonFunctions.java---

//==========================================================================

// The start of including of menu.java
//--------------------------------
//#global _MENU_ITEM_CUSTOM=true
//#global _MENU_ITEM_DELIMITER=false
//#global _MENU_ITEM_IMAGE=false
//#global _MENU_ITEM_MENUITEM=true
//#global _MENU_ITEM_TEXTBOX=true

// Screens

// Screen OptionsSCR
private static final int SCR_OptionsSCR = 0;
// Screen AboutSCR
private static final int SCR_AboutSCR = 28;
// Screen NewGameSCR
private static final int SCR_NewGameSCR = 41;
// Screen SaveLoadSCR
private static final int SCR_SaveLoadSCR = 64;
// Screen ViewGameScoreSCR
private static final int SCR_ViewGameScoreSCR = 82;
// Screen FatalErrorSCR
private static final int SCR_FatalErrorSCR = 97;
// Screen AreYouSureSCR
private static final int SCR_AreYouSureSCR = 110;
// Screen ExitSCR
private static final int SCR_ExitSCR = 125;
// Screen OnOffSCR
private static final int SCR_OnOffSCR = 140;
// Screen SaveGameSCR
private static final int SCR_SaveGameSCR = 148;
// Screen MainMenuSCR
private static final int SCR_MainMenuSCR = 160;
// Screen LoadGameSCR
private static final int SCR_LoadGameSCR = 213;
// Screen TopResultsSCR
private static final int SCR_TopResultsSCR = 221;
// Screen LanguageSelectSCR
private static final int SCR_LanguageSelectSCR = 233;
// Screen PlayerNameFormSCR
private static final int SCR_PlayerNameFormSCR = 241;
// Screen HelpSCR
private static final int SCR_HelpSCR = 261;
// Screen RewriteRecordSCR
private static final int SCR_RewriteRecordSCR = 274;
// Screen NewGameNameFormSCR
private static final int SCR_NewGameNameFormSCR = 289;

// Items

// Item NewGameITM
private static final int ITEM_NewGameITM = 0;
// Item HardModeITM
private static final int ITEM_HardModeITM = 11;
// Item AboutTextITM
private static final int ITEM_AboutTextITM = 19;
// Item VibrationITM
private static final int ITEM_VibrationITM = 18;
// Item AreYouSureQuestionITM
private static final int ITEM_AreYouSureQuestionITM = 21;
// Item RewriteRecordQuestionITM
private static final int ITEM_RewriteRecordQuestionITM = 22;
// Item SaveGameITM
private static final int ITEM_SaveGameITM = 12;
// Item TopResultsITM
private static final int ITEM_TopResultsITM = 6;
// Item FatalErrorTextITM
private static final int ITEM_FatalErrorTextITM = 23;
// Item NormalModeITM
private static final int ITEM_NormalModeITM = 10;
// Item SaveLoadITM
private static final int ITEM_SaveLoadITM = 3;
// Item HelpTextITM
private static final int ITEM_HelpTextITM = 14;
// Item ScoreNumberItem
private static final int ITEM_ScoreNumberItem = 25;
// Item AboutITM
private static final int ITEM_AboutITM = 7;
// Item HelpITM
private static final int ITEM_HelpITM = 4;
// Item PlayerNameFieldITM
private static final int ITEM_PlayerNameFieldITM = 26;
// Item ExitTextQuestionITM
private static final int ITEM_ExitTextQuestionITM = 20;
// Item OptionsITM
private static final int ITEM_OptionsITM = 5;
// Item RestartLevelITM
private static final int ITEM_RestartLevelITM = 2;
// Item LanguageSelectITM
private static final int ITEM_LanguageSelectITM = 15;
// Item ResumeGameITM
private static final int ITEM_ResumeGameITM = 1;
// Item ExitITM
private static final int ITEM_ExitITM = 8;
// Item BackLightITM
private static final int ITEM_BackLightITM = 16;
// Item LoadGameITM
private static final int ITEM_LoadGameITM = 13;
// Item SoundITM
private static final int ITEM_SoundITM = 17;
// Item ViewScoreNumberItem
private static final int ITEM_ViewScoreNumberItem = 27;
// Item EasyModeITM
private static final int ITEM_EasyModeITM = 9;
// Item NewGameNameFieldITM
private static final int ITEM_NewGameNameFieldITM = 24;

// Commands

// Command AddInTopCMD
private static final int COMMAND_AddInTopCMD = 304;
// Command CancelCMD
private static final int COMMAND_CancelCMD = 305;
// Command RemoveAllCMD
private static final int COMMAND_RemoveAllCMD = 306;
// Command SaveCMD
private static final int COMMAND_SaveCMD = 307;
// Command BackCMD
private static final int COMMAND_BackCMD = 308;
// Command NoCMD
private static final int COMMAND_NoCMD = 309;
// Command YesCMD
private static final int COMMAND_YesCMD = 310;
// Command OkCMD
private static final int COMMAND_OkCMD = 311;
//---The end of the included file menu.java---

// The start of including of defTxt.java
//--------------------------------
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
//---The end of the included file defTxt.java---

// The start of including of images.java
//--------------------------------
//#global INDEXRESOLUTION="NORMAL"
//#global CONTAINS_OFFSET=false
//#global MULTIPALETTE_SET=true

private static final int IMG_TITLE = 0;
private static final int IMG_WIN_LOST = 1;
private static final int IMG_VIIGRISH = 2;
private static final int IMG_DEMO = 3;
private static final int IMG_YOU_LOST = 4;
private static final int IMG_YOU_WIN = 5;
private static final int IMG_BACKGROUND = 6;
private static final int IMG_LUNKA = 7;
private static final int IMG_NUMBERS = 8;
private static final int IMG_DESKNUMBERS = 9;
private static final int IMG_SHAR01_01 = 10;
private static final int IMG_SHAR01_02 = 11;
private static final int IMG_SHAR01_03 = 12;
private static final int IMG_SHAR02_01 = 13;
private static final int IMG_SHAR02_02 = 14;
private static final int IMG_SHAR02_03 = 15;
private static final int IMG_SHAR03_01 = 16;
private static final int IMG_SHAR03_02 = 17;
private static final int IMG_SHAR03_03 = 18;
private static final int IMG_SHAR04_01 = 19;
private static final int IMG_SHAR04_02 = 20;
private static final int IMG_SHAR04_03 = 21;
private static final int IMG_SHAR05_01 = 22;
private static final int IMG_SHAR05_02 = 23;
private static final int IMG_SHAR05_03 = 24;
private static final int IMG_SHAR06_01 = 25;
private static final int IMG_SHAR06_02 = 26;
private static final int IMG_SHAR06_03 = 27;
private static final int IMG_CLOCK = 28;
private static final int TOTAL_IMAGES_NUMBER = 29;
//---The end of the included file images.java---

// The start of including of sounds.java
//--------------------------------
private static final int SND_HOD_IGROKA_OTT = 0;
private static final int SND_LOST_THEME_OTT = 1;
private static final int SND_PRIGLASHENIE_K_HODU_IGROKA_OTT = 2;
private static final int SND_SMENA_POZITSII_KURSORA_OTT = 3;
private static final int SND_WON_THEME_OTT = 4;
private static final int SND_ZASTAVKA_OTT = 5;
//---The end of the included file sounds.java---

}
