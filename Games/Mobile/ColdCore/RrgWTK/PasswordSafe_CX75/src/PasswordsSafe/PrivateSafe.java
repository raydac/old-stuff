package PasswordsSafe;

import PasswordsSafe.BApp;
import PasswordsSafe.rmsFS;
import PasswordsSafe.AppActionListener;
import PasswordsSafe.AppActionListener;
import PasswordsSafe.rmsFS;

import javax.microedition.rms.RecordStoreException;
import java.util.Vector;
import java.io.*;

public class PrivateSafe implements BApp
{
    private String s_UserName;
    private String s_UserPassword;

    public static final int ACTION_GET_RMS = 0;
    private Vector p_safes;
    private boolean lg_hasNamePassword;
    private long l_hashCodeForNamePassword;

    private static final String ID_SERVICE = "432098";
    private static final String ID_CREDITCARDS = "101";
    private static final String ID_INET = "202";
    private static final String ID_OFFICE = "303";
    private static final String ID_HOUSE = "404";
    private static final String ID_MISC = "505";

    public PrivateSafe()
    {
        s_UserName = "";
        s_UserPassword = "";
        p_safes = new Vector();
    }

    public void resetSafe() throws IOException
    {
        synchronized (p_safes)
        {
            s_UserName = "";
            s_UserPassword = "";
            lg_hasNamePassword = false;
            l_hashCodeForNamePassword = 0;

            for (int li = 0; li < getRecordsNumber(); li++)
            {
                SafeRecord p_recod = getRecordForIndex(li);
                p_recod.resetRecord("", "");
            }
        }
    }

    public String getAppID()
    {
        return "PS89832";
    }

    private long makeHashCodeForNamePassword(String _name, String _password)
    {
        if (_name == null) _name = "";
        if (_password == null) _password = "";
        int i_hashName = _name.hashCode();
        int i_hashPasword = _password.hashCode();
        return ((long) i_hashName << 32) | (long) i_hashPasword;
    }

    public boolean checkNamePassword(String _name, String _password)
    {
        if (s_UserName == null || s_UserPassword == null) return true;
        if (lg_hasNamePassword)
        {
            long l_code = makeHashCodeForNamePassword(_name, _password);

            if (l_code == l_hashCodeForNamePassword)
                return true;
            else
                return false;
        }
        else if (s_UserPassword.equals("")) return true;
        return false;
    }

    public boolean hasNamePassword()
    {
        return lg_hasNamePassword;
    }

    public String getUserName()
    {
        return s_UserName;
    }

    public String getUserPassword()
    {
        return s_UserPassword;
    }

    public boolean changeNamePassword(rmsFS _rms, String _name, String _password) throws Exception
    {
        synchronized (p_safes)
        {
            if (ownKeysPresented()) return false;

            if (_password.equals("")) _name = "";

            for (int li = 0; li < getRecordsNumber(); li++)
            {
                SafeRecord p_record = getRecordForIndex(li);
                p_record.changeUserNamePassword(s_UserName, s_UserPassword, _name, _password);
            }

            s_UserName = _name;
            s_UserPassword = _password;

            if (s_UserName.equals("") || s_UserPassword.equals(""))
            {
                lg_hasNamePassword = false;
                s_UserName = "";
                s_UserPassword = "";
            }
            else
            {
                lg_hasNamePassword = true;
            }

            l_hashCodeForNamePassword = makeHashCodeForNamePassword(_name, _password);


            return true;
        }
    }

    public boolean ownKeysPresented()
    {
        synchronized (p_safes)
        {
            for (int li = 0; li < getRecordsNumber(); li++)
            {
                SafeRecord p_record = getRecordForIndex(li);
                if (p_record.isOwnKeyPresented() || p_record.hasClosedCells()) return true;
            }
        }
        return false;
    }

    public void setNamePassword(String _name, String _password)
    {
        s_UserName = _name;
        s_UserPassword = _password;
    }

    public void initApplication(rmsFS _rms, AppActionListener _listener) throws Exception
    {
        rmsFS.FSRecord p_serviceRecord = _rms.getRecordForName(ID_SERVICE);

        if (p_serviceRecord == null)
        {
            s_UserName = "";
            s_UserPassword = "";
            p_safes = new Vector();
            lg_hasNamePassword = false;
            l_hashCodeForNamePassword = 0;

            SafeRecord p_safeRecord = new SafeRecord(ID_CREDITCARDS, s_UserName, s_UserPassword);
            p_safes.addElement(p_safeRecord);

            p_safeRecord = new SafeRecord(ID_INET, s_UserName, s_UserPassword);
            p_safes.addElement(p_safeRecord);

            p_safeRecord = new SafeRecord(ID_OFFICE, s_UserName, s_UserPassword);
            p_safes.addElement(p_safeRecord);

            p_safeRecord = new SafeRecord(ID_HOUSE, s_UserName, s_UserPassword);
            p_safes.addElement(p_safeRecord);

            p_safeRecord = new SafeRecord(ID_MISC, s_UserName, s_UserPassword);
            p_safes.addElement(p_safeRecord);
        }
        else
        {
            byte[] ab_data = p_serviceRecord.getData();
            ByteArrayInputStream p_bais = new ByteArrayInputStream(ab_data);
            DataInputStream p_dis = new DataInputStream(p_bais);

            lg_hasNamePassword = p_dis.readBoolean();
            l_hashCodeForNamePassword = p_dis.readLong();

            rmsFS.FSRecord p_record = _rms.getRecordForName(ID_CREDITCARDS);
            if (p_record == null) throw new Exception("Can not find creditcards data");
            SafeRecord p_srec = SafeRecord.makeFromByteArray(ID_CREDITCARDS, p_record.getData());
            p_safes.addElement(p_srec);

            p_record = _rms.getRecordForName(ID_INET);
            if (p_record == null) throw new Exception("Can not find inet data");
            p_srec = SafeRecord.makeFromByteArray(ID_INET, p_record.getData());
            p_safes.addElement(p_srec);

            p_record = _rms.getRecordForName(ID_OFFICE);
            if (p_record == null) throw new Exception("Can not find office data");
            p_srec = SafeRecord.makeFromByteArray(ID_OFFICE, p_record.getData());
            p_safes.addElement(p_srec);

            p_record = _rms.getRecordForName(ID_HOUSE);
            if (p_record == null) throw new Exception("Can not find house data");
            p_srec = SafeRecord.makeFromByteArray(ID_HOUSE, p_record.getData());
            p_safes.addElement(p_srec);

            p_record = _rms.getRecordForName(ID_MISC);
            if (p_record == null) throw new Exception("Can not find misc data");
            p_srec = SafeRecord.makeFromByteArray(ID_MISC, p_record.getData());
            p_safes.addElement(p_srec);
        }
    }

    public int getRecordsNumber()
    {
        return p_safes.size();
    }

    public SafeRecord getRecordForIndex(int _index)
    {
        return (SafeRecord) p_safes.elementAt(_index);
    }

    public void saveChangedSafesToRMS(rmsFS _rms) throws Exception
    {
        synchronized (p_safes)
        {
            for (int li = 0; li < getRecordsNumber(); li++)
            {
                SafeRecord p_record = getRecordForIndex(li);
                if (p_record.hasChanged())
                {
                    rmsFS.FSRecord p_rms_record = _rms.getRecordForName(p_record.s_Name);
                    if (p_rms_record == null)
                    {
                        p_rms_record = _rms.createNewRecord(p_record.s_Name, 512);
                    }
                    p_record.saveDataToRMS(p_rms_record, s_UserName, s_UserPassword);
                }
            }
            saveServiceData(_rms);
        }
    }

    private void saveServiceData(rmsFS _rms) throws Exception
    {
        rmsFS.FSRecord p_serviceRecord = _rms.getRecordForName(ID_SERVICE);
        if (p_serviceRecord == null)
        {
            p_serviceRecord = _rms.createNewRecord(ID_SERVICE, 128);
        }
        ByteArrayOutputStream p_outStr = new ByteArrayOutputStream(128);
        DataOutputStream p_dos = new DataOutputStream(p_outStr);

        p_dos.writeBoolean(lg_hasNamePassword);
        p_dos.writeLong(l_hashCodeForNamePassword);

        p_dos.flush();
        p_dos.close();
        byte[] ab_arr = p_outStr.toByteArray();
        p_outStr = null;

        p_serviceRecord.setData(ab_arr, true);
    }


    public void releaseApplication(rmsFS _rms) throws Exception
    {
        saveServiceData(_rms);

        saveChangedSafesToRMS(_rms);
    }
}
