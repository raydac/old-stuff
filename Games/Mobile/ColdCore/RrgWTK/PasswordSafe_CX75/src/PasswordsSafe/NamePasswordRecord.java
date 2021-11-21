package PasswordsSafe;

import java.io.*;

public class NamePasswordRecord
{
    public String s_Name;
    public String s_Password;
    public String s_Text;

    public NamePasswordRecord(byte [] _data) throws IOException
    {
        ByteArrayInputStream p_bis = new ByteArrayInputStream(_data);
        DataInputStream p_dis = new DataInputStream(p_bis);

        s_Name = p_dis.readUTF();
        s_Password = p_dis.readUTF();
        s_Text = p_dis.readUTF();
    }

    public NamePasswordRecord(String _name,String _password,String _text)
    {
        s_Name = _name;
        s_Password = _password;
        s_Text = _text;
    }

    public byte [] packData() throws IOException
    {
        ByteArrayOutputStream p_bas = new ByteArrayOutputStream(128);
        DataOutputStream p_daos = new DataOutputStream(p_bas);
        p_daos.writeUTF(s_Name);
        p_daos.writeUTF(s_Password);
        p_daos.writeUTF(s_Text);
        p_daos.close();
        byte [] ab_result = p_bas.toByteArray();
        return ab_result;
    }
}
