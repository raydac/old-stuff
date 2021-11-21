package BusinessPlan;

import java.util.Vector;
import java.io.*;

public class Project
{
    public static final int PRIORITY_LOW = 0;
    public static final int PRIORITY_NORMAL = 1;
    public static final int PRIORITY_HIGH = 2;

    protected Vector p_works;

    protected long l_startdate;
    protected long l_enddate;
    protected String s_name;
    protected int i_iconIndex;
    protected long l_creatingDate;
    protected long l_responsiblePerson; 
    protected String s_comments;
    protected int i_rateOfDelivery;
    protected int i_priority;
    protected ProjectsContainer p_Parent;
    protected Task p_activeTask;

    public long addNewTask(String _title)
    {
        synchronized(p_works)
        {
            Task p_newTask = new Task(this,_title);
            p_works.addElement(p_newTask);
            return p_newTask.getUID();
        }
    }


    public boolean setActiveTask(long _uid)
    {
        p_activeTask = getWorkForUID(_uid);
        if (p_activeTask!=null) return true;
        return false;
    }

    public void setActiveTask(int _index)
    {
        p_activeTask = (Task) p_works.elementAt(_index);

    }

    public Task getActiveTask()
    {
        return p_activeTask;
    }

    public void closeActiveTask()
    {
        p_activeTask = null;
    }

    public int getProgress()
    {
        return i_rateOfDelivery;
    }

    public ProjectsContainer getParent()
    {
        return p_Parent;
    }

    public Project(ProjectsContainer _parent,String _name)
    {
        p_Parent = _parent;
        s_name = _name;
        l_creatingDate = System.currentTimeMillis();
        i_iconIndex = 0;

        i_priority = PRIORITY_NORMAL;

        l_startdate = l_creatingDate;
        l_enddate = l_creatingDate;

        l_responsiblePerson = -1;
        s_comments = "";
        i_rateOfDelivery = 0;

        p_works = new Vector();
    }

    public void setSpecialFlagsForAllWorks(int _value)
    {
        synchronized (p_works)
        {
            for (int li = 0; li < p_works.size(); li++)
            {
                Task p_task = (Task) p_works.elementAt(li);
                p_task.i_special = _value;
            }
        }
    }

    public void removeTask(int _index)
    {
        synchronized(p_works)
        {
            try
            {
                p_works.removeElementAt(_index);
            }
            catch (Exception e)
            {
            }
        }
    }

    public void processBeforeProjectRemove()
    {
        synchronized (p_works)
        {
            setResponsiblePerson(-1);

            for (int li = 0; li < p_works.size(); li++)
            {
                Task p_task = (Task) p_works.elementAt(li);
                p_task.setResource(-1);
            }
        }
    }

    public int getPriority()
    {
        return i_priority;
    }

    public void setComments(String _comments)
    {
        s_comments = _comments;
    }

    public void addWork(Task _task)
    {
        synchronized (p_works)
        {
            p_works.addElement(_task);
        }
    }

    public void removeAllWorks()
    {
        synchronized (p_works)
        {
            p_works.removeAllElements();
        }
    }

    public void removeResource(Resource _resource)
    {
        synchronized (p_works)
        {
            if (l_responsiblePerson >= 0)
            {
                if (l_responsiblePerson == _resource.getUID())
                {
                    Resource p_res = p_Parent.getResourcesContainer().getResourceForUID(l_responsiblePerson);
                    if (p_res != null)
                    {
                        p_res.decNumberOfUsing();
                    }
                }
                l_responsiblePerson = -1;
            }

            for (int li = 0; li < p_works.size(); li++)
            {
                Task p_task = (Task) p_works.elementAt(li);
                long l_res = p_task.getResource();
                if (l_res >= 0)
                {
                    Resource p_res = p_Parent.getResourcesContainer().getResourceForUID(l_res);
                    if (p_res != null)
                    {
                        p_res.decNumberOfUsing();
                        p_task.setResource(-1);
                    }
                }
            }
        }
    }

    public boolean doesUseResource(Resource _resource)
    {
        synchronized (p_works)
        {
            if (l_responsiblePerson >= 0)
            {
                if (l_responsiblePerson == _resource.getUID()) return true;
            }

            long l_resuid = _resource.getUID();
            for (int li = 0; li < p_works.size(); li++)
            {
                Task p_task = (Task) p_works.elementAt(li);
                long l_res = p_task.getResource();
                if (l_res >= 0 && l_resuid == l_res) return true;
            }
        }
        return false;
    }

    public void setPriority(int _level)
    {
        i_priority = _level;
    }

    public void setStartDate(long _date)
    {
        l_startdate = _date;
        if (l_enddate>=0)
        {
            if (l_enddate < l_startdate) l_enddate = l_startdate;
        }
    }

    public void setEndDate(long _date)
    {
        if (l_startdate>=0)
        {
            if (_date < l_startdate) _date = l_startdate;
        }
        l_enddate = _date;
    }

    public void setIconIndex(int _index)
    {
        i_iconIndex = _index;
    }

    public void setName(String _name)
    {
        s_name = _name;
    }

    public void setCreatingDate(long _date)
    {
        l_creatingDate = _date;
    }

    public int getIconIndex()
    {
        return i_iconIndex;
    }

    public void setResponsiblePerson(long _uid)
    {
        if (l_responsiblePerson >= 0)
        {
            Resource p_res = p_Parent.getResourcesContainer().getResourceForUID(l_responsiblePerson);
            p_res.decNumberOfUsing();
        }

        if (_uid >= 0)
        {
            Resource p_res = p_Parent.getResourcesContainer().getResourceForUID(_uid);
            p_res.incNumberOfUsing();
        }

        l_responsiblePerson = _uid;
    }

    public long getResponsiblePerson()
    {
        return l_responsiblePerson;
    }

    public String getComments()
    {
        return s_comments;
    }

    public int getWorksNumber()
    {
        synchronized (p_works)
        {
            return p_works.size();
        }
    }

    public Task getWorkForIndex(int _index)
    {
        synchronized (p_works)
        {
            return (Task) p_works.elementAt(_index);
        }
    }

    public void calculateRateOfDelivery()
    {
        synchronized (p_works)
        {
            int i_summary = 0;
            if (p_works.size() == 0)
            {
                i_rateOfDelivery = -1;
                return;
            }
            for (int li = 0; li < p_works.size(); li++)
            {
                Task p_task = (Task) p_works.elementAt(li);
                i_summary += p_task.getProgress();
            }
            i_rateOfDelivery = i_summary / p_works.size();
        }
    }

    public Task getWorkForUID(long _uid)
    {
        if (_uid < 0) return null;
        synchronized (p_works)
        {
            for (int li = 0; li < p_works.size(); li++)
            {
                Task p_task = (Task) p_works.elementAt(li);
                if (p_task.getUID() == _uid)
                {
                    return p_task;
                }
            }
            return null;
        }
    }

    public long getCreatingDate()
    {
        return l_creatingDate;
    }

    public long getStartDate()
    {
        return l_startdate;
    }

    public String getName()
    {
        return s_name;
    }

    public long getEndDate()
    {
        return l_enddate;
    }

    public void loadFromArray(byte[] _array) throws IOException
    {
        synchronized (p_works)
        {
            ByteArrayInputStream p_baos = new ByteArrayInputStream(_array);
            DataInputStream p_dis = new DataInputStream(p_baos);

            l_startdate = p_dis.readLong();
            l_enddate = p_dis.readLong();
            s_name = p_dis.readUTF();
            i_iconIndex = p_dis.readUnsignedByte();
            l_creatingDate = p_dis.readLong();
            l_responsiblePerson = p_dis.readLong();
            s_comments = p_dis.readUTF();
            i_rateOfDelivery = p_dis.readInt();
            i_priority = p_dis.readUnsignedByte();

            int i_worksNumber = p_dis.readUnsignedShort();

            p_works.removeAllElements();

            Runtime.getRuntime().gc();

            for (int li = 0; li < i_worksNumber; li++)
            {
                Task p_task = new Task(this,"");
                p_task.loadFromStream(p_dis);
                p_works.addElement(p_task);
            }

            if (l_responsiblePerson >= 0)
            {
                if (p_Parent.getResourcesContainer().getResourceForUID(l_responsiblePerson) == null)
                {
                    l_responsiblePerson = -1;
                }
            }

        }
    }

    public byte[] makeArray() throws IOException
    {
        synchronized (p_works)
        {
            calculateRateOfDelivery();

            ByteArrayOutputStream p_baos = new ByteArrayOutputStream(8000);
            DataOutputStream p_dos = new DataOutputStream(p_baos);

            p_dos.writeLong(l_startdate);
            p_dos.writeLong(l_enddate);
            p_dos.writeUTF(s_name);
            p_dos.writeByte(i_iconIndex);
            p_dos.writeLong(l_creatingDate);
            p_dos.writeLong(l_responsiblePerson);
            p_dos.writeUTF(s_comments);
            p_dos.writeInt(i_rateOfDelivery);
            p_dos.writeByte(i_priority);

            p_dos.writeShort(p_works.size());

            for (int li = 0; li < p_works.size(); li++)
            {
                Task p_task = (Task) p_works.elementAt(li);
                p_task.saveToStream(p_dos);
            }
            p_dos.flush();
            p_dos.close();
            return p_baos.toByteArray();
        }
    }
}
