package BusinessPlan;

import javax.microedition.rms.RecordStore;
import javax.microedition.rms.RecordStoreException;
import javax.microedition.rms.RecordEnumeration;
import java.io.*;
import java.util.Vector;

public class rmsFS
{
    public class FSRecord
    {
        public String s_name;
        public byte[] ab_record;
        private int i_rmsRecordID;
        private boolean lg_changed;
        private boolean lg_System;

        public boolean isSystem()
        {
            return lg_System;
        }

        public boolean isChanged()
        {
            return lg_changed;
        }

        public FSRecord(String _name, int _rmsRecordID,boolean _systemRecord)
        {
            s_name = _name;
            ab_record = null;
            lg_changed = false;
            i_rmsRecordID = _rmsRecordID;
            lg_System = _systemRecord;
        }

        public byte[] getData() throws RecordStoreException
        {
            if (ab_record == null)
            {
                return p_recordStore.getRecord(i_rmsRecordID);
            }
            else
            {
                return ab_record;
            }
        }

        public void setData(byte[] _data, boolean _immediateFlush) throws RecordStoreException,IOException
        {
            ab_record = _data;
            lg_changed = true;
            if (_immediateFlush)
            {
                writeMainRecord();
                saveRecord();
            }
        }

        public void saveRecord() throws RecordStoreException
        {
            if (ab_record != null)
            {
                if (lg_changed) p_recordStore.setRecord(i_rmsRecordID, ab_record, 0, ab_record.length);
                lg_changed = false;
            }
        }

        public void removeRecord() throws RecordStoreException
        {
            p_recordStore.deleteRecord(i_rmsRecordID);
        }
    }

    private RecordStore p_recordStore;
    private Vector p_Records;

    public int getRecordsNumber()
    {
        return p_Records.size();
    }

    public FSRecord getRecordAtPosition(int _position)
    {
        synchronized (p_Records)
        {
            return (FSRecord) p_Records.elementAt(_position);
        }
    }

    public FSRecord getRecordForName(String _name)
    {
        if (_name==null) return null;
        synchronized(p_Records)
        {
            for(int li=0;li<p_Records.size();li++)
            {
                FSRecord p_record = (FSRecord) p_Records.elementAt(li);
                if (_name.equals(p_record.s_name)) return p_record;
            }
        }
        return null;
    }

    private void writeMainRecord() throws RecordStoreException, IOException
    {
        synchronized (p_Records)
        {
            ByteArrayOutputStream p_outStream = new ByteArrayOutputStream(1024);
            DataOutputStream p_dos = new DataOutputStream(p_outStream);

            p_dos.writeShort(p_Records.size());

            for (int li = 0; li < p_Records.size(); li++)
            {
                FSRecord p_record = (FSRecord) p_Records.elementAt(li);
                p_dos.writeInt(p_record.i_rmsRecordID);
                p_dos.writeUTF(p_record.s_name);
                p_dos.writeBoolean(p_record.lg_System);
            }
            p_dos.flush();
            p_dos.close();
            byte[] ab_data = p_outStream.toByteArray();
            p_outStream = null;
            p_recordStore.setRecord(1, ab_data, 0, ab_data.length);
            ab_data = null;
        }
    }

    private void parseMainRecord(byte [] _data) throws RecordStoreException, IOException
    {
        synchronized (p_Records)
        {
            p_Records.removeAllElements();
            ByteArrayInputStream p_inStream = new ByteArrayInputStream(_data);
            DataInputStream p_dis = new DataInputStream(p_inStream);

            int i_number =  p_dis.readUnsignedShort();

            for (int li = 0; li < i_number; li++)
            {
                int i_recordID =  p_dis.readInt();
                String s_recordName = p_dis.readUTF();
                boolean lg_isSystem = p_dis.readBoolean();
                FSRecord p_newRecord = new FSRecord(s_recordName,i_recordID,lg_isSystem);
                p_Records.addElement(p_newRecord);
            }
            p_dis.close();
        }
    }

    public void close(boolean _saveChangedRecords) throws RecordStoreException, IOException
    {
        synchronized (p_Records)
        {
            writeMainRecord();

            for (int li = 0; li < p_Records.size(); li++)
            {
                FSRecord p_record = (FSRecord) p_Records.elementAt(li);
                if (p_record.isChanged())
                {
                    p_record.saveRecord();
                }
            }

            p_recordStore.closeRecordStore();
        }
    }

    public void deleteRecord(FSRecord _record) throws RecordStoreException
    {
        synchronized (p_Records)
        {
            p_Records.removeElement(_record);
            _record.removeRecord();
        }
    }

    public FSRecord createNewRecord(String _name, int _reservedSize) throws RecordStoreException
    {
        synchronized (p_Records)
        {
            if (p_recordStore.getSizeAvailable() < _reservedSize) return null;

            byte[] ab_array = new byte[_reservedSize];
            int i_recordID = p_recordStore.addRecord(ab_array, 0, _reservedSize);
            ab_array = null;
            FSRecord p_newRecord = new FSRecord(_name, i_recordID,true);
            p_Records.addElement(p_newRecord);
            return p_newRecord;
        }
    }

    public rmsFS(String _storageName) throws RecordStoreException,IOException
    {
        p_recordStore = RecordStore.openRecordStore(_storageName, true, RecordStore.AUTHMODE_PRIVATE, false);
        p_Records = new Vector();

        if (p_recordStore.getNumRecords() == 0)
        {
            int i_mainRecord = p_recordStore.addRecord(new byte[1024], 0, 1024);
            if (i_mainRecord != 1) throw new RecordStoreException("Fatal RMS error");

        }
        else
        {
                byte [] ab_data = p_recordStore.getRecord(1);
                parseMainRecord(ab_data);
        }
    }
}
