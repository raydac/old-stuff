package PasswordsSafe;

import PasswordsSafe.CoderEncoder;
import PasswordsSafe.NamePasswordRecord;

import java.io.*;

public class SafeCell
{
    private boolean lg_hasKeyCode;
    public String s_keyCode;
    private String s_name;
    private int i_keyHash;

    private int i_iconIndex;
    private byte [] ab_data;

    public SafeCell(String _title,boolean _hasKey,int _hashKey,byte [] _data,int _iconIndex)
    {
        s_name =_title;
        lg_hasKeyCode = _hasKey;
        i_keyHash = _hashKey;
        s_keyCode = "";
        ab_data = _data;
        i_iconIndex = _iconIndex;
    }

    public void setKeyString(String _key)
    {
        s_keyCode = _key;
    }


    public void setKey(String _key,String _mainName,String _mainPassword) throws IOException
    {
        NamePasswordRecord p_record = decodeData(_mainName,_mainPassword);
        s_keyCode = _key;
        encodeData(_mainName,_mainPassword,p_record);
        if (s_keyCode.equals(""))
        {
            lg_hasKeyCode = false;
            i_keyHash = 0;
        }
        else
        {
            lg_hasKeyCode = true;
            i_keyHash = s_keyCode.hashCode();
        }
    }

    public SafeCell(String _userName, String _userPassword, String _title,NamePasswordRecord _record,int _iconIndex) throws IOException
    {
        s_name = _title;
        lg_hasKeyCode = false;
        i_keyHash = 0;
        s_keyCode = "";

        encodeData(_userName,_userPassword,_record);

        i_iconIndex = _iconIndex;
    }

    public static SafeCell fromArray(byte [] _array) throws IOException
    {
        ByteArrayInputStream p_bais = new ByteArrayInputStream(_array);
        DataInputStream p_dais = new DataInputStream(p_bais);

        String s_name = p_dais.readUTF();
        boolean lg_hasOwnKey = p_dais.readBoolean();
        int i_hashKey = p_dais.readInt();
        int i_iconIndex = p_dais.readUnsignedByte();
        int i_dataLength = p_dais.readUnsignedShort();
        byte [] ab_data = new byte[i_dataLength];
        p_dais.read(ab_data);
        p_dais.close();
        p_dais = null;
        p_bais = null;

        return new SafeCell(s_name,lg_hasOwnKey,i_hashKey,ab_data,i_iconIndex);
    }

    public byte [] packData() throws IOException
    {
        ByteArrayOutputStream p_baos = new ByteArrayOutputStream(128);
        DataOutputStream p_dos = new DataOutputStream(p_baos);

        p_dos.writeUTF(s_name);
        p_dos.writeBoolean(lg_hasKeyCode);
        p_dos.writeInt(i_keyHash);
        p_dos.writeByte(i_iconIndex);
        p_dos.writeShort(ab_data.length);
        p_dos.write(ab_data);
        p_dos.flush();
        p_dos.close();
        p_dos = null;
        return p_baos.toByteArray();
    }

    public int getIconIndex()
    {
        return i_iconIndex;
    }

    public void setIconIndex(int _index)
    {
        i_iconIndex = _index;
    }

    public String getName()
    {
        return s_name;
    }

    public void setName(String _name)
    {
        s_name = _name;
    }

    public boolean checkKey(String _key)
    {
        if (!lg_hasKeyCode) return true;
        if (_key == null) _key = "";
        int i_hash = _key.hashCode();
        return i_hash == i_keyHash;
    }

    public NamePasswordRecord decodeData(String _mainName,String _mainPassword) throws IOException
    {
        String s_password = _mainPassword+":"+s_keyCode;
        byte [] ab_key = CoderEncoder.generateKeyFromString(_mainName,s_password);
        ab_key = CoderEncoder.decodeArray(ab_data,ab_key);
        if (ab_key == null) return null;
        NamePasswordRecord p_record = new NamePasswordRecord(ab_key);
        return p_record;
    }

    public void encodeData(String _mainName,String _mainPassword,NamePasswordRecord _record) throws IOException
    {
        if (s_keyCode == null) s_keyCode = "";
        String s_password = _mainPassword+":"+s_keyCode;
        byte [] ab_key = CoderEncoder.generateKeyFromString(_mainName,s_password);
        ab_data = _record.packData();
        ab_data = CoderEncoder.encodeArray(ab_data,ab_key);
    }

    public void setNewData(String _mainName,String _mainPassword,NamePasswordRecord _record) throws IOException
    {
        String s_password = _mainPassword+":"+s_keyCode;
        byte [] ab_key = CoderEncoder.generateKeyFromString(_mainName,s_password);
        ab_data = CoderEncoder.encodeArray(_record.packData(),ab_key);
    }

    public boolean hasKeyCode()
    {
        return lg_hasKeyCode;
    }

    public boolean changeKeyCode(String _mainName,String _mainPassword,String _newKey,String _oldKey) throws IOException
    {
        if (_oldKey == null) _oldKey = s_keyCode;
        if (!checkKey(_oldKey)) return false;
        if (_newKey == null) _newKey = "";

        setKey(_newKey,_mainName,_mainPassword);

        return true;
    }

}
