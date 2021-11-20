package BusinessPlan;

import BusinessPlan.Project;
import BusinessPlan.Resource;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.DataInputStream;

public class Task
{
    private String s_name;
    private long l_resource;
    private long l_parentWork;
    private long l_startDate;
    private long l_duration;
    private int i_progress;
    private String s_comments;
    private int i_iconIndex;
    private long l_uid;
    private Project p_parent;

    protected long l_deadLine;

    protected int i_special;

    public long getDeadline()
    {
        return l_deadLine;
    }

    public void setDeadline(long _value)
    {
        if (_value<0)
        {
            l_deadLine = -1;
        }
        else
        if (l_startDate>0)
        {
            if (_value<l_startDate)
            {
                l_duration = 0;
                _value = l_startDate;
            }
            else
            {
                long l_diff = ((l_deadLine - l_startDate)/ProjectsContainer.MILLISECONDS_FULLDAY);
                l_diff *= ProjectsContainer.MILLISECONDS_WORKDAYDAY;
                l_duration = l_diff;
            }
            l_deadLine = _value;
        }
        else
        {
            l_deadLine = _value;
        }
    }

    public Task(Project _parent,String _title)
    {
        p_parent = _parent;
        l_uid = System.currentTimeMillis();
        s_name = _title;
        l_resource = -1;
        l_parentWork = -1;
        l_startDate = l_uid;
        l_deadLine = l_uid;
        l_duration = 0;
        i_progress = 0;
        s_comments = "";
        i_iconIndex = 0;
    }

    public long getUID()
    {
        return l_uid;
    }

    public void saveToStream(DataOutputStream _outStream) throws IOException
    {
        _outStream.writeLong(l_uid);
        _outStream.writeUTF(s_name);
        _outStream.writeUTF(s_comments);
        _outStream.writeLong(l_startDate);
        _outStream.writeLong(l_duration);
        _outStream.writeByte(i_progress);
        _outStream.writeByte(i_iconIndex);
        _outStream.writeLong(l_deadLine);

        _outStream.writeLong(l_parentWork);
        _outStream.writeLong(l_resource);
    }

    public void loadFromStream(DataInputStream _inStream) throws IOException
    {
        l_uid = _inStream.readLong();
        s_name = _inStream.readUTF();
        s_comments = _inStream.readUTF();
        l_startDate = _inStream.readLong();
        l_duration = _inStream.readLong();
        i_progress = _inStream.readUnsignedByte();
        i_iconIndex = _inStream.readUnsignedByte();
        l_deadLine = _inStream.readLong();

        l_parentWork = _inStream.readLong();
        l_resource = _inStream.readLong();

        if (l_resource>=0)
        {
            if (p_parent.getParent().getResourcesContainer().getResourceForUID(l_resource)==null)
            {
                l_resource = -1;
            }
        }
    }

    public Project getParent()
    {
        return p_parent;
    }

    public int getIconIndex()
    {
        return i_iconIndex;
    }

    public void setIconIndex(int _index)
    {
        i_iconIndex = _index;
    }


    public long getStartDate()
    {
        return l_startDate;
    }

    public void setStartDate(long _date)
    {
        l_startDate = _date;
        if (l_deadLine>0)
        {
            if (l_startDate>l_deadLine)
            {
                l_deadLine = l_startDate;
                l_duration = 0;
            }
            else
            {
                long l_diff = ((l_deadLine - l_startDate)/ProjectsContainer.MILLISECONDS_FULLDAY);
                l_diff *= ProjectsContainer.MILLISECONDS_WORKDAYDAY;
                if (l_duration>l_diff)
                {
                    l_duration = l_diff;
                }
            }
        }
    }

    public long getDuration()
    {
        return l_duration;
    }

    public void setDuration(long _duration)
    {
        l_duration = _duration;
        if (l_startDate>0 && l_deadLine > 0)
        {
            long l_diff = ((l_deadLine - l_startDate)/ProjectsContainer.MILLISECONDS_FULLDAY);
            l_diff *= ProjectsContainer.MILLISECONDS_WORKDAYDAY;
            if (l_diff<l_duration)
            {
                l_duration = l_diff;
            }
        }
    }

    public void setComments(String _comments)
    {
        s_comments = _comments;
    }

    public String getComments()
    {
        return s_comments;
    }

    public int getProgress()
    {
        return i_progress;
    }

    public void setProgress(int _progress)
    {
        i_progress = _progress;
    }

    public long getParentTask()
    {
        return l_parentWork;
    }

    public void setParentWork(long _parentWork)
    {
        l_parentWork = _parentWork;
    }

    public String getTitle()
    {
        return s_name;
    }

    public void setTitle(String _title)
    {
        s_name = _title;
    }

    public void setResource(long _resource)
    {
        if (l_resource >=0 )
        {
            Resource p_res = p_parent.getParent().getResourcesContainer().getResourceForUID(l_resource);
            if (p_res != null)
            {
                p_res.decNumberOfUsing();
            }
        }

        if (_resource>=0)
        {
            Resource p_res = p_parent.getParent().getResourcesContainer().getResourceForUID(_resource);
            if (p_res != null)
            {
                p_res.incNumberOfUsing();
            }
        }
        l_resource = _resource;
    }

    public long getResource()
    {
        return l_resource;
    }

    public boolean equals(Object obj)
    {
        if (obj == null) return false;
        if (obj instanceof Task)
        {
            if (((Task)obj).l_uid == l_uid) return true;
        }
        return false;
    }
}
