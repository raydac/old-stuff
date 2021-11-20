package com.igormaznitsa.midp;

import javax.microedition.rms.RecordStore;
import javax.microedition.rms.RecordStoreException;
import javax.microedition.rms.RecordStoreFullException;
import java.io.*;

/*
 * Copyright (C) 2002 Igor A. Maznitsa (igor.maznitsa@igormaznitsa.com).  All rights reserved.
 * Software is confidential and copyrighted. Title to Software and all associated intellectual property rights
 * is retained by Igor A. Maznitsa. You may not make copies of Software, other than a single copy of Software for
 * archival purposes.  Unless enforcement is prohibited by applicable law, you may not modify, decompile, or reverse
 * engineer Software.  You acknowledge that Software is not designed, licensed or intended for use in the design,
 * construction, operation or maintenance of any nuclear facility. Igor A. Maznitsa disclaims any express or implied
 * warranty of fitness for such uses.
 *
 * This class implements of Game Data Storage and contains
 * 1) Game options (light, vibration, sound)
 * 2) Top list
 * 3) List of saved games
 */
public class GameDataStorage
{
    // Max number of records in the game top list
    public int MAX_TOPRECORDS;
    //Max number of saved game records in RMS
    public int MAX_SAVEDRECORDS;
    // Max number of chars in the name of a game saved record
    public int MAX_CHARSINSAVENAME;

    /**
     * Id of the boolean field for VIBRATOR option
     */
    public static final int FIELD_VIBRATOR = 0;

    /**
     * Id of the boolean field for LIGHT option
     */
    public static final int FIELD_LIGHT = 1;

    /**
     * Id of the boolean field for SOUND option
     */
    public static final int FIELD_SOUND = 2;

    /**
     * Game related boolean option
     */
    public static final int FIELD_GAMEOPTION0 = 3;

    /**
     * Game related boolean option
     */
    public static final int FIELD_GAMEOPTION1 = 4;

    /**
     * Game related boolean option
     */
    public static final int FIELD_GAMEOPTION2 = 5;

    /**
     * Id of the byte field for LANGUAGE option
     */
    public static final int FIELD_LANGUAGE = 6;

    protected int i_booleanflags;
    protected int i_language;

    protected String[] as_topnames;
    protected int[] ai_topscores;
    protected String[] as_savedgames;

    protected boolean lg_changed;
    protected String s_gameid;
    protected int i_maxgamerecordsize;

    private static final int RECORD_OPTIONRECORD = 1;
    private static final int RECORD_FIRSTSAVEDRECORD = 2;

    /**
     * Constructor
     * @param _gameID the unique identifier for current game
     * @param _maxsaveddata max size of a data block where could be saved game data
     * @param _maxcharinname max number chars in a name
     * @param _maxsavedgames max number of saved games
     * @param _maxrecoedsintoplist max number of records in the game top list
     * @throws IOException
     */
    public GameDataStorage(String _gameID,int _maxsaveddata,int _maxcharinname,int _maxsavedgames,int _maxrecoedsintoplist) throws IOException
    {
        MAX_CHARSINSAVENAME = _maxcharinname;
        MAX_SAVEDRECORDS = _maxsavedgames;
        MAX_TOPRECORDS = _maxrecoedsintoplist;
        s_gameid = _gameID;
        as_savedgames = new String[MAX_SAVEDRECORDS];
        as_topnames = new String[MAX_TOPRECORDS];
        ai_topscores = new int[MAX_TOPRECORDS];
        lg_changed = false;
        i_maxgamerecordsize = _maxsaveddata;

        try
        {
            RecordStore p_optionstore = RecordStore.openRecordStore(s_gameid,true);

            if (p_optionstore.getNumRecords() != 0)
            {
                byte[] ab_optionrecord = p_optionstore.getRecord(RECORD_OPTIONRECORD);
                DataInputStream p_dis = new DataInputStream(new ByteArrayInputStream(ab_optionrecord));

                // Reading of the boolean flags
                i_booleanflags = p_dis.readUnsignedByte();
                // Reading of the language option
                i_language = p_dis.readByte();
                // Redaing of max saved games
                MAX_SAVEDRECORDS = p_dis.readUnsignedByte();

                // Reading top list
                for (int li = 0;li < MAX_TOPRECORDS;li++)
                {
                    String s_name = p_dis.readUTF();
                    if (s_name.length() == 0) s_name = null;
                    int i_score = p_dis.readInt();
                    as_topnames[li] = s_name;
                    ai_topscores[li] = i_score;
                }

                // Reading list of saved games
                for (int li = 0;li < MAX_SAVEDRECORDS;li++)
                {
                    String s_name = p_dis.readUTF();
                    if (s_name.length() == 0) s_name = null;
                    as_savedgames[li] = s_name;
                }

                p_dis.close();
                p_dis = null;
                p_optionstore.closeRecordStore();
                p_optionstore = null;
                Runtime.getRuntime().gc();
            }
            else
            {
                setBooleanOption(FIELD_LIGHT,true);
                setBooleanOption(FIELD_VIBRATOR,true);
                setBooleanOption(FIELD_SOUND,true);
                setIntOption(FIELD_LANGUAGE,-1);

                int i_optionlen = calculateOptionRecordLength();
                int i_maxrecords = (p_optionstore.getSizeAvailable() - i_optionlen - 16) / (i_maxgamerecordsize + 16);
                if (i_maxrecords < MAX_SAVEDRECORDS) MAX_SAVEDRECORDS = i_maxrecords;

                byte[] ab_bytearr = packOptionToByteArray();
                p_optionstore.addRecord(ab_bytearr,0,ab_bytearr.length);
                ab_bytearr = null;
                Runtime.getRuntime().gc();
                ab_bytearr = new byte[i_maxgamerecordsize + 16];
                for (int li = 0;li < MAX_SAVEDRECORDS;li++)
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
            throw new IOException(e.getMessage());
        }
    }

    // Calculating of max size of option record
    private int calculateOptionRecordLength()
    {
        return MAX_SAVEDRECORDS * (MAX_CHARSINSAVENAME * 2 + 2) + MAX_TOPRECORDS * (MAX_CHARSINSAVENAME * 2 + 6) + 3;
    }

    // This function packs all options and saves it to the game storage
    private byte[] packOptionToByteArray() throws IOException
    {
        int i_maxsize = calculateOptionRecordLength();
        ByteArrayOutputStream p_baos = new ByteArrayOutputStream(i_maxsize);
        DataOutputStream p_dos = new DataOutputStream(p_baos);

        // Writing of boolean flags
        p_dos.writeByte(i_booleanflags);
        // Writing of the language parameter
        p_dos.writeByte(i_language);
        // Writing of saved games number
        p_dos.writeByte(MAX_SAVEDRECORDS);

        // Writing top list
        for (int li = 0;li < MAX_TOPRECORDS;li++)
        {
            if (as_topnames[li] == null)
            {
                p_dos.writeUTF("");
            }
            else
            {
                p_dos.writeUTF(as_topnames[li]);
            }
            p_dos.writeInt(ai_topscores[li]);
        }

        // Writing list of saved games
        for (int li = 0;li < MAX_SAVEDRECORDS;li++)
        {
            if (as_savedgames[li] == null)
            {
                p_dos.writeUTF("");
            }
            else
            {
                p_dos.writeUTF(as_savedgames[li]);
            }
        }
        p_dos.flush();
        p_dos = null;
        Runtime.getRuntime().gc();
        if (p_baos.size() > i_maxsize) throw new IOException("Option data are too long");
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
     * @throws IOException
     */
    public DataInputStream getSavedGameForIndex(int _index) throws IOException
    {
        DataInputStream p_dis = null;
        try
        {
            int i_indx = _index + RECORD_FIRSTSAVEDRECORD;
            RecordStore p_gamestore = RecordStore.openRecordStore(s_gameid,false);
            byte[] ab_saveddata = new byte[0];
            ab_saveddata = p_gamestore.getRecord(i_indx);
            p_gamestore.closeRecordStore();
            p_gamestore = null;
            Runtime.getRuntime().gc();
            p_dis = new DataInputStream(new ByteArrayInputStream(ab_saveddata));
        }
        catch (RecordStoreException e)
        {
            throw new IOException(e.getMessage());
        }
        return p_dis;
    }

    /**
     * This function removes a saved game for its name
     * @param _name
     * @throws IOException
     */
    public void removeSavedGame(String _name) throws IOException
    {
        int i_index = convertSavedNameToIndex(_name);
        if (i_index < 0) return;
        as_savedgames[i_index] = null;

        flush();
    }

    /**
     * Return array comntains saved game names
     * @return string array
     */
    public String[] getSavedGameNamesArray()
    {
        return as_savedgames;
    }

    /**
     * This function removes all saved games
     * @throws IOException
     */
    public void removeAllSavedGames() throws IOException
    {
        for (int li = 0;li < MAX_SAVEDRECORDS;li++)
        {
            as_savedgames[li] = null;
        }
        flush();
    }

    /**
     * This function removes all records from the game top list
     */
    public void clearTopList()
    {
        for (int li = 0;li < MAX_TOPRECORDS;li++)
        {
            if (as_topnames[li] != null) lg_changed = true;
            as_topnames[li] = null;
        }
    }

    /**
     * This function saves a game data array to storage for the record name
     * @param _name name of the record, if this name is exists then that record will rewrite
     * @param _dataarray the game data array
     * @throws IOException
     * @throws RecordStoreFullException throws when all game records are filled
     */
    public void saveGameDataForName(String _name,byte[] _dataarray) throws IOException,RecordStoreFullException
    {
        if (_dataarray.length > i_maxgamerecordsize) throw new IOException("Game data are too long");
        byte[] ab_buffer = _dataarray;
        if (_dataarray.length < i_maxgamerecordsize)
        {
            ab_buffer = new byte[i_maxgamerecordsize];
            for (int li = 0;li < _dataarray.length;li++) ab_buffer[li] = _dataarray[li];
        }
        _dataarray = null;

        int i_indx = convertSavedNameToIndex(_name);
        int i_inactive;
        if (i_indx < 0)
        {
            i_inactive = getFirstEmptySavedGame();
        }
        else
        {
            i_inactive = i_indx;
        }

        if (i_inactive < 0) throw new RecordStoreFullException();
        try
        {
            int i_rmsrecord = i_inactive + RECORD_FIRSTSAVEDRECORD;

            RecordStore p_store = RecordStore.openRecordStore(s_gameid,false);
            p_store.setRecord(i_rmsrecord,ab_buffer,0,ab_buffer.length);
            p_store.closeRecordStore();

            as_savedgames[i_inactive] = _name;
            ab_buffer = null;
            p_store = null;
            Runtime.getRuntime().gc();
        }
        catch (RecordStoreException e)
        {
            throw new IOException(e.getMessage());
        }
        flush();
    }

    /**
     * This function return number of saved games in the storage
     * @return saved games number as an int value
     */
    public int getSavedGamesCount()
    {
        int i_cnt = 0;
        for (int li = 0;li < MAX_SAVEDRECORDS;li++) if (as_savedgames[li] != null) i_cnt++;
        return i_cnt;
    }

    // To get a first inactive saved record
    private int getFirstEmptySavedGame()
    {
        for (int li = 0;li < MAX_SAVEDRECORDS;li++) if (as_savedgames[li] == null) return li;
        return -1;
    }

    /**
     * Get the index for a saved game name
     * @param _name the name of a saved game
     * @return 0 or more if record is exists or -1 if none
     */
    public int convertSavedNameToIndex(String _name)
    {
        for (int li = 0;li < MAX_SAVEDRECORDS;li++)
        {
            String s_name = as_savedgames[li];
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
    public String getSavedGameNameForIndex(int _index)
    {
        return as_savedgames[_index];
    }

    /**
     * Return DataInputStream object for a saved game for the name
     * @param _name the name of the saved game
     * @return the game data array as a DataInputObect
     * @throws IOException
     */
    public DataInputStream getSavedGameForName(String _name) throws IOException
    {
        int i_indx = convertSavedNameToIndex(_name);
        if (i_indx < 0)
            return null;
        else
            return getSavedGameForIndex(i_indx);
    }

    /**
     * To check flag of changing of game storage options
     * @return
     */
    public boolean isChanged()
    {
        return lg_changed;
    }

    /**
     * Update game data from the storage to RMS storage
     * @throws IOException
     */
    public void flush() throws IOException
    {
        byte[] ab_optionbyte = packOptionToByteArray();

        try
        {
            RecordStore p_recordstore = RecordStore.openRecordStore(s_gameid,false);
            p_recordstore.setRecord(RECORD_OPTIONRECORD,ab_optionbyte,0,ab_optionbyte.length);
            p_recordstore.closeRecordStore();
        }
        catch (RecordStoreException e)
        {
            throw new IOException(e.getMessage());
        }
        ab_optionbyte = null;
        lg_changed = false;
        Runtime.getRuntime().gc();
    }

    /**
     * Set the value to a boolean option
     * @param _fieldId id of the field
     * @param _value new value
     */
    public void setBooleanOption(int _fieldId,boolean _value)
    {
        int i_cur = i_booleanflags;
        if (_fieldId <= FIELD_GAMEOPTION2 && _fieldId >= 0)
        {
            if (_value)
            {
                i_booleanflags |= (1 << _fieldId);
            }
            else
            {
                i_booleanflags = i_booleanflags & (~(1 << _fieldId));
            }
        }
        if (i_cur != i_booleanflags) lg_changed = true;
    }

    /**
     * Get a boolean option value
     */
    public boolean getBooleanOption(int _fieldId)
    {
        if (_fieldId <= FIELD_GAMEOPTION2 && _fieldId >= 0)
        {
            if ((i_booleanflags & (1 << _fieldId)) != 0)
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
    public void addRecordToTopList(String _name,int _scores)
    {
        int i_indx = getFirstEmptySavedGame();
        if (i_indx < 0)
        {
            if (_scores > getMinScoreInTopList())
            {
                for (int li = MAX_TOPRECORDS - 1;li >= 0;li--)
                {
                    if (as_topnames[li] != null)
                    {
                        i_indx = li;
                        break;
                    }
                }
            }
            else
                return;
        }
        as_topnames[i_indx] = _name;
        ai_topscores[i_indx] = _scores;
        sortTopList();
        lg_changed = true;
    }

    /**
     * Get min score value from the top list
     * @return
     */
    public int getMinScoreInTopList()
    {
        int i_min = 0;
        for (int li = 0;li < MAX_TOPRECORDS;li++)
        {
            if (as_topnames[li] != null)
                i_min = ai_topscores[li];
            else
                break;
        }
        return i_min;
    }

    /**
     * Get max score value from the top list
     * @return
     */
    public int getMaxScoreInTopList()
    {
        if (as_topnames[0] != null) return ai_topscores[0]; else return -1;
    }

    /**
     * Set the value to an int option
     */
    public void setIntOption(int _fieldId,int _value)
    {
        if (getIntOption(_fieldId) != _value) lg_changed = true;
        switch (_fieldId)
        {
            case FIELD_LANGUAGE:
                i_language = _value;
        }
    }

    /**
     * Get an int value option
     */
    public int getIntOption(int _fieldId)
    {
        switch (_fieldId)
        {
            case FIELD_LANGUAGE:
                return i_language;
        }
        return -1;
    }

    public String getTopListNameForIndex(int _index)
    {
        return as_topnames[_index];
    }

    public int getTopListScoresForIndex(int _index)
    {
        return ai_topscores[_index];
    }

    // sorting of the game top list
    private void sortTopList()
    {
        for (int li = 0;li < MAX_TOPRECORDS;li++)
        {
            int i_elem = ai_topscores[li];
            if (as_topnames[li] == null) i_elem = -1;

            for (int lii = li;lii < MAX_TOPRECORDS;lii++)
            {
                int i_curelem = ai_topscores[lii];
                if (as_topnames[lii] == null) i_curelem = -1;

                if (i_curelem > i_elem)
                {
                    String s_akk = as_topnames[lii];
                    int i_akk = ai_topscores[lii];
                    as_topnames[lii] = as_topnames[li];
                    as_topnames[li] = s_akk;
                    ai_topscores[lii] = ai_topscores[li];
                    ai_topscores[li] = i_akk;
                }
            }
        }
    }
}
