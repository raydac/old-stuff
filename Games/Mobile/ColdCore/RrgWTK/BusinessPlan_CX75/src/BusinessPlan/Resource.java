package BusinessPlan;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.DataInputStream;

public class Resource
{
    private String s_Name;
    private String s_phoneNumber;
    private String s_email;
    private String s_comments;
    private int i_numberOfUsingInProjects;
    private long l_uid;
    private int i_iconIndex;
    private ResourcesContainer p_parent;

    public Resource(ResourcesContainer _parent,String _title)
    {
        p_parent = _parent;

        s_Name = _title;
        s_phoneNumber = "";
        s_email = "";
        s_comments = "";
        i_numberOfUsingInProjects = 0;
        i_iconIndex = 0;

        l_uid = System.currentTimeMillis();
    }

    public void saveToStream(DataOutputStream _stream) throws IOException
    {
        _stream.writeLong(l_uid);
        _stream.writeUTF(s_Name);
        _stream.writeByte(i_iconIndex);
        _stream.writeUTF(s_phoneNumber);
        _stream.writeUTF(s_email);
        _stream.writeUTF(s_comments);
        _stream.writeShort(i_numberOfUsingInProjects);
    }

    public void loadFromStream(DataInputStream _stream) throws IOException
    {
        l_uid = _stream.readLong();
        s_Name = _stream.readUTF();
        i_iconIndex = _stream.readUnsignedByte();
        s_phoneNumber = _stream.readUTF();
        s_email = _stream.readUTF();
        s_comments = _stream.readUTF();
        i_numberOfUsingInProjects = _stream.readUnsignedShort();
    }

    public String getName()
    {
        return s_Name;
    }

    public void setUsingNumber(int _value)
    {
        i_numberOfUsingInProjects = _value;
        p_parent.lg_changed = true;
    }

    public void setName(String _name)
    {
        s_Name = _name;
        p_parent.lg_changed = true;
    }

    public String getPhoneNumber()
    {
        return s_phoneNumber;
    }

    public void setPhoneNumber(String _phone)
    {
        s_phoneNumber = _phone;
        p_parent.lg_changed = true;
    }

    public String getEmail()
    {
        return s_email;
    }

    public void setEmail(String _email)
    {
        s_email = _email;
        p_parent.lg_changed = true;
    }

    public String getComments()
    {
        return s_comments;
    }

    public void setComments(String _comments)
    {
        s_comments = _comments;
        p_parent.lg_changed = true;
    }

    public int getNumberOfUsing()
    {
        return i_numberOfUsingInProjects;
    }

    public void incNumberOfUsing()
    {
        i_numberOfUsingInProjects++;
        p_parent.lg_changed = true;
    }

    public void decNumberOfUsing()
    {
        i_numberOfUsingInProjects--;
        p_parent.lg_changed = true;
    }

    public long getUID()
    {
        return  l_uid;
    }

    public boolean equals(Object obj)
    {
        if (obj==null) return false;
        if (!(obj instanceof Resource)) return false;
        Resource p_res = (Resource) obj;
        if (l_uid == p_res.l_uid) return true;
        return false;
    }

}
