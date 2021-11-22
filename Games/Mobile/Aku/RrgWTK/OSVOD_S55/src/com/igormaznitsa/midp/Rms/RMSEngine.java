package com.igormaznitsa.midp.Rms;
import javax.microedition.rms.RecordEnumeration;
import javax.microedition.rms.RecordStore;
import javax.microedition.rms.RecordStoreException;
import javax.microedition.rms.RecordStoreNotOpenException;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;

public class RMSEngine
{
    public static final String name_of_unreadable_record = "<UNREADABLE>";

    private RecordStore store = null;           // for RMS support
    private String RMSName = null;                // base name
    private int LastError = 0;                    // Last error message
  //  private String NameMask = "              ";   // Filling chars and length of stored name of records
    private String NewRecordContent = null;       // default name of new record
    private int maxRecLimit = 10;                 // Maximal possible amount of records in storage
    /** Storage is unusable and can't be opened */
    private final static byte RMSHasNoRoom = 39;
    /** not enough room for save game */
    private final static byte RMSBaseNotOpen = 40;
    /** storage is corrupted and saved records became to trash */
    private final static byte RMSEmptyList = 41;
    /** impossible request, can't load the unexistent gamerecord */
    private final static byte RMSMaxRecordsLimit = 42;
    /** can't prepare the output stream // can it be? i didn't saw reports like this, autor */
    private final static byte RMSReadingFail = 44;
    /** storage corrupted, can't read the data from storage */
    private final static byte RMSCantDelete = 45;
    /** can't delete record from storage */
    private RecordEnumeration Enum = null;

    /**
     * Constructor of this engine
     * @param Name - base name
     * @param maxRecordsLimit - limitation for amount of saved records
     */
    public RMSEngine(String Name, int maxRecordsLimit)
    {
        RMSName = new String(Name);
        maxRecLimit = maxRecordsLimit;
        OpenStorage();
    }

    /**
     * Opens the storage
     */
    public void OpenStorage()
    {
        try
        {
            store = RecordStore.openRecordStore(RMSName, true);
            Enum = store.enumerateRecords(null, null, true);
        }
        catch (RecordStoreException ex)
        {
            store = null;
        }
    }

    /**
     * Getting number of records into RMS base
     * @return number of the existance records, or -1 if error occured
     */
    public int GetNumRecords()
    {
        int ret = -1;
        if (store != null)
            try
            {
                ret = store.getNumRecords();
            }
            catch (RecordStoreNotOpenException e)
            {
                ret = -1;
            }
        return ret;
    }

    /**
     * Getting list of the named records from base
     * <p><b>Note: Length of Name can't cross over 10 bytes (UTF chars)</b>
     * @param AddNew boolean value , means "Reserve 1 place for new record"
     * @return list of named items, null if not accessible or empty
     * @throws RMSException
     */
    public String[] GetList(boolean AddNew) throws RMSException
    {
        String[] ret = null;
        int q = 0;
        LastError = RMSEmptyList;
        if (store == null)
        {
            LastError = RMSBaseNotOpen;
            throw new RMSException(LastError);
        }
        else if ((q = GetNumRecords()) > 0 || AddNew)
        {
            if (CanAddNewRecord() && AddNew)
            {
                if (q <= 0) q = 0;
                ret = new String[q + 1];
                ret[q] = NewRecordContent;
            }
            else
                ret = new String[q];
            Enum.rebuild();
            Enum.reset();
            for (int i = 0; i < q; i++)
            {
                try
                {
                    ret[i] = new DataInputStream(new ByteArrayInputStream(Enum.nextRecord())).readUTF();
                }
                catch (Exception e)
                {
                    ret[i - 1] = name_of_unreadable_record;
                    e.printStackTrace();
                }
            }
        }
        return ret;
    }

    /**
     * Cares about possibility to add the new record into RMS base
     * @return possible or not
     */
    public boolean CanAddNewRecord()
    {
        if (store == null)
            LastError = RMSEmptyList;
        else if (GetNumRecords() > maxRecLimit)
            LastError = RMSMaxRecordsLimit;
        else
            return true;
        return false;
    }

    public int GetSizeAvailable() throws RMSException
    {
        try
        {
            return store.getSizeAvailable();
        }
        catch (Exception e)
        {
            LastError = RMSBaseNotOpen;
            throw new RMSException(LastError);
        }
    }

    /**
     * Adding new record to base, quantity of records bounded by "RecordLimit" value of the java
     * @param data Data, what will be stored in base
     * @return result of operation successed of failed
     * @throws RMSException
     */
    public boolean AddRecord(byte[] data) throws RMSException
    {
        if (CanAddNewRecord() && data != null)
        {
            try
            {
                store.addRecord(data, 0, data.length);
                return true;
            }
            catch (Exception e)
            {
                LastError = RMSHasNoRoom;
                throw new RMSException(LastError);
            }
        }
        return false;
    }

    /**
     * update record
     * @param Num - number of record for updating
     * @param out - Data, what will be stored in base
     * @return result of operation successed of failed
     * @throws RMSException
     */
    public boolean StoreRecord(int Num, byte[] out) throws RMSException
    {
        if (Num <= 0 || Num > GetNumRecords()) return AddRecord(out);
        if (out != null)
        {
            try
            {
                store.setRecord(Int2ID(Num), out, 0, out.length);
                return true;
            }
            catch (Exception e)
            {
                LastError = RMSHasNoRoom;
                throw new RMSException(LastError);
            }
        }
        return false;
    }

    /**
     * Getting data from storage
     * @param Num - No of record to read
     * @return DataInputStream, with skipped name
     * @throws RMSException
     */
    public DataInputStream GetRecord(int Num) throws RMSException
    {
        DataInputStream dis = null;
        if (Num > 0 || Num <= GetNumRecords())
        {
            try
            {
                dis = new DataInputStream(new ByteArrayInputStream(store.getRecord(Int2ID(Num))));
		dis.readUTF();
//                dis.skipBytes(NameMask.length() + 2);  // skipping name
            }
            catch (Exception e)
            {
                LastError = RMSReadingFail;
                dis = null;
                throw new RMSException(LastError);
            }
        }
        return dis;
    }

    /**
     * Getting list of int values, for TopTen
     * @return []int
     */
    public int[] GetIntTable()
    {
        int[] ret = null;
        if (Enum.numRecords() > 0)
            try
            {
                ret = new int[Enum.numRecords()];
                for (int i = 1; i <= ret.length; i++)
                    ret[i - 1] = GetRecord(i).readInt();
            }
            catch (Exception e)
            {
                ret = null;
            }
        return ret;
    }

    /**
     *  Converts Number of item onto list to ID of RecordStore
     * @param Num testing number
     * @return requested ID
     */
    public int Int2ID(int Num)
    {
        int ret = -1;
        if (Enum != null)
            if (Enum.numRecords() >= Num)
            {
                Enum.rebuild();
                Enum.reset();
                for (int i = 1; i <= Num; i++)
                    try
                    {
                        ret = Enum.nextRecordId();
                    }
                    catch (Exception e)
                    {
                        e.printStackTrace();
                    }
            }
        return ret;
    }

    /**
     * Deleting the existent record
     * @param Num - number of record to delete
     * @throws RMSException
     */
    public void Delete(int Num) throws RMSException
    {
        try
        {
            store.deleteRecord(Int2ID(Num));
        }
        catch (Exception e)
        {
            LastError = RMSCantDelete;
            throw new RMSException(LastError);
        }
    }

    /**
     * Close the RMS storage
     * @return - it's unnecesary, but we returning successfully will it done or not
     */
    public boolean Close()
    {
        if (store != null)
        {
            try
            {
                if (Enum != null) Enum.destroy();
                store.closeRecordStore();
                store = null;
            }
            catch (RecordStoreException ex)
            {
                return false;
            }
        }
        return true;
    }

    /**
     * Killin' the RMS storage
     * @return possible success
     */
    public boolean EraseStorage()
    {
        if (store != null) Close();
        if (RMSName != null)
            try
            {
                store.deleteRecordStore(RMSName);
            }
            catch (Exception e)
            {
                e.printStackTrace();
                return false;
            }
        return true;
    }

    public void setDefName(String s)
    {
        NewRecordContent = s;
    }

}