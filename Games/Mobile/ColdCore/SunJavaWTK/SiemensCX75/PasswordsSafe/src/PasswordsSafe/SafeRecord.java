package PasswordsSafe;

import PasswordsSafe.rmsFS;
import PasswordsSafe.*;

import javax.microedition.rms.RecordStoreException;
import java.io.*;
import java.util.Vector;

public class SafeRecord
{
    protected String s_Name;
    protected Vector p_Cells;

    public String s_viewName;

    protected boolean lg_hasKey;
    protected boolean lg_cellHasKey;
    protected int i_keyHashCode;
    public String s_Key;
    protected byte[] ab_data;

    protected boolean lg_hasChanged;

    public void setChangeStatus(boolean _flag)
    {
        lg_hasChanged = _flag;
    }

    public boolean hasChanged()
    {
        return lg_hasChanged;
    }

    public boolean hasKeyCode()
    {
        return lg_hasKey;
    }

    public void resetRecord(String _userName,String _userPassword) throws IOException
    {
        lg_hasKey = false;
        lg_cellHasKey = false;
        i_keyHashCode = 0;
        s_Key = "";
        p_Cells.removeAllElements();
        ab_data = packData(_userName,_userPassword);
        lg_hasChanged = true;
    }

    public boolean checkKeyCode(String _key)
    {
        if (!lg_hasKey) return true;
        int i_keyHash = _key.hashCode();
        if (i_keyHash == i_keyHashCode) return true;
        return false;
    }

    public void setKey(String _key)
    {
        if (_key == null)
        {
            lg_hasKey = false;
            s_Key = "";
            i_keyHashCode = 0;
        }
        else
        {
            lg_hasKey = true;
            s_Key = _key;
            i_keyHashCode = _key.hashCode();
        }
    }

    public SafeRecord(String _name,String _userName,String _userPassword) throws IOException
    {
        s_Name = _name;
        lg_hasKey = false;
        lg_cellHasKey = false;
        i_keyHashCode = 0;
        s_Key = "";
        p_Cells = new Vector();
        ab_data = packData(_userName,_userPassword);
        lg_hasChanged = true;
    }

    public int getCellsNumber()
    {
        synchronized (p_Cells)
        {
            return p_Cells.size();
        }
    }

    public SafeCell getCellForIndex(int _index)
    {
        synchronized (p_Cells)
        {
            return (SafeCell) p_Cells.elementAt(_index);
        }
    }

    public void removeCell(String _userName, String _userPassword,SafeCell _cell) throws IOException
    {
        synchronized (p_Cells)
        {
            p_Cells.removeElement(_cell);
            ab_data = packData(_userName,_userPassword);
            lg_hasChanged = true;
            lg_cellHasKey = hasCellKey();
        }
    }

    public boolean hasClosedCells()
    {
        return lg_cellHasKey;
    }

    private boolean hasCellKey()
    {
        synchronized (p_Cells)
        {
            for(int li=0;li<getCellsNumber();li++)
            {
                SafeCell p_cell = getCellForIndex(li);
                if (p_cell.hasKeyCode()) return true;
            }
            return false;
        }
    }

    public void addCell(String _userName, String _userPassword,SafeCell _cell) throws IOException
    {
        synchronized (p_Cells)
        {
            p_Cells.addElement(_cell);
            ab_data = packData(_userName,_userPassword);
            if (_cell.hasKeyCode()) lg_cellHasKey = true;
            lg_hasChanged = true;
        }
    }

    public boolean isOwnKeyPresented()
    {
        synchronized(p_Cells)
        {
            if (hasKeyCode()) return true;
            for(int li=0;li<getCellsNumber();li++)
            {
                SafeCell p_cell = getCellForIndex(li);
                if (p_cell.hasKeyCode()) return true;
            }
        }
        return false;
    }

    public void dataUpdated(String _name,String _password) throws IOException
    {
       ab_data = packData(_name,_password);
       lg_hasChanged =true;
    }

    public boolean changeKey(String _name,String _password,String _newKey,String _oldKey) throws IOException
    {
        synchronized(p_Cells)
        {
            if (!checkKeyCode(_oldKey)) return false;
            s_Key = _oldKey;
            if (!decodeCells(_name,_password)) return false;

            for(int li=0;li<getCellsNumber();li++)
            {
                SafeCell p_cell = getCellForIndex(li);
                NamePasswordRecord p_record = p_cell.decodeData(_name,_password);
                p_cell.encodeData(_name,_password,p_record);
            }
            s_Key = _newKey;

            if (s_Key.equals(""))
            {
                i_keyHashCode = 0;
                lg_hasKey = false;
            }
            else
            {
                i_keyHashCode = s_Key.hashCode();
                lg_hasKey = true;
            }

            ab_data = packData(_name,_password);
            lg_hasChanged = true;
        }
        return true;
    }

    protected void changeUserNamePassword(String _oldName, String _oldPassword, String _newName, String _newPassword) throws IOException
    {
        synchronized (p_Cells)
        {
            if (!s_Key.equals("")) throw new IOException("You have a pin code");

            decodeCells(_oldName,_oldPassword);
            for(int li=0;li<getCellsNumber();li++)
            {
                SafeCell p_cell = getCellForIndex(li);
                NamePasswordRecord p_record = p_cell.decodeData(_oldName,_oldPassword);
                p_cell.encodeData(_newName,_newPassword,p_record);
            }
            ab_data = packData(_newName,_newPassword);
            lg_hasChanged = true;
        }
    }

    protected byte[] packData(String _mainName, String _mainPassword) throws IOException
    {
        synchronized (p_Cells)
        {
            ByteArrayOutputStream p_baos = new ByteArrayOutputStream(2048);
            DataOutputStream p_daos = new DataOutputStream(p_baos);

            p_daos.writeShort(getCellsNumber());
            for (int li = 0; li < getCellsNumber(); li++)
            {
                SafeCell p_cell = getCellForIndex(li);
                byte[] ab_packedCell = p_cell.packData();
                p_daos.writeShort(ab_packedCell.length);
                p_daos.write(ab_packedCell);
                p_daos.flush();
                ab_packedCell = null;
            }

            lg_cellHasKey = hasCellKey();

            p_daos.flush();
            p_daos.close();
            p_daos = null;
            ab_data = p_baos.toByteArray();
            p_baos = null;

            String s_password = _mainPassword + ":" + s_Key;
            byte[] ab_codingArray = CoderEncoder.generateKeyFromString(_mainName, s_password);
            lg_hasChanged = true;
            return CoderEncoder.encodeArray(ab_data, ab_codingArray);
        }
    }

    public static SafeRecord makeFromByteArray(String _name, byte[] _array) throws IOException
    {
        ByteArrayInputStream p_bis = new ByteArrayInputStream(_array);
        DataInputStream p_dis = new DataInputStream(p_bis);

        boolean lg_hasKey = p_dis.readBoolean();
        boolean lg_cellHasKey = p_dis.readBoolean();
        int i_hashKeyCode = p_dis.readInt();
        int i_dataLength = p_dis.readShort();
        byte[] ab_data = new byte[i_dataLength];
        p_dis.read(ab_data);

        SafeRecord p_sr = new SafeRecord(_name,"","");
        p_sr.lg_hasKey = lg_hasKey;
        p_sr.i_keyHashCode = i_hashKeyCode;
        p_sr.ab_data = ab_data;
        p_sr.lg_cellHasKey = lg_cellHasKey;

        p_dis.close();
        p_dis = null;
        p_bis = null;

        p_sr.lg_hasChanged = false;

        return p_sr;
    }

    public void saveDataToRMS(rmsFS.FSRecord _record, String _mainName, String _mainPassword) throws IOException, RecordStoreException
    {
        synchronized (p_Cells)
        {
            if (s_Key == null) s_Key = "";
            if (_mainPassword == null) _mainPassword = "";
            if (_mainName == null) _mainName = "";

            ByteArrayOutputStream p_outStream = new ByteArrayOutputStream(ab_data.length+24);
            DataOutputStream p_ost = new DataOutputStream(p_outStream);

            p_ost.writeBoolean(lg_hasKey);
            p_ost.writeBoolean(lg_cellHasKey);
            p_ost.writeInt(i_keyHashCode);
            p_ost.writeShort(ab_data.length);
            p_ost.write(ab_data);
            p_ost.flush();
            p_ost.close();

            byte [] ab_result = p_outStream.toByteArray();

            _record.setData(ab_result, true);
            lg_hasChanged = false;
        }
    }

    public boolean decodeCells(String _mainName, String _mainPassword) throws IOException
    {
        synchronized (p_Cells)
        {
            if (_mainPassword == null) _mainPassword = "";
            if (_mainName == null) _mainName = "";

            String s_password = _mainPassword + ":" + s_Key;

            byte[] ab_keyArray = CoderEncoder.generateKeyFromString(_mainName, s_password);
            byte[] ab_decodedData = CoderEncoder.decodeArray(ab_data, ab_keyArray);

            if (ab_decodedData == null) return false;

            ByteArrayInputStream p_bis = new ByteArrayInputStream(ab_decodedData);
            p_Cells.removeAllElements();

            int i_number = (p_bis.read() & 0xFF) << 8;
            i_number |= (p_bis.read() & 0xFF);

            for (int li = 0; li < i_number; li++)
            {
                int i_dataLength = (p_bis.read() & 0xFF) << 8;
                i_dataLength |= (p_bis.read() & 0xFF);
                byte[] ab_data = new byte[i_dataLength];
                p_bis.read(ab_data);

                SafeCell p_safeCell = SafeCell.fromArray(ab_data);
                p_Cells.addElement(p_safeCell);
            }

            lg_cellHasKey = hasCellKey();

            return true;
        }
    }
}
