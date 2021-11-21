package BusinessPlan;

import BusinessPlan.ProjectsContainer;
import BusinessPlan.Resource;

import java.util.Vector;
import java.io.*;

public class ResourcesContainer
{
    private Vector p_resources;
    private ProjectsContainer p_parent;

    protected boolean lg_changed;

    private Resource p_activeResource;

    public Resource getActiveResource()
    {
        return p_activeResource;
    }

    public void setActiveResource(int _id)
    {
        p_activeResource = (Resource) p_resources.elementAt(_id);
    }

    public boolean setActiveResource(long _uid)
    {
        p_activeResource = getResourceForUID(_uid);
        if (p_activeResource != null) return true;
        return false;
    }

    public void closeActiveResource()
    {
        p_activeResource = null;
    }

    public ProjectsContainer getParent()
    {
        return p_parent;
    }

    public long createNewResource(String _title)
    {
       synchronized(p_resources)
       {
           Resource p_res = new Resource(this,_title);
           addResource(p_res);
           return p_res.getUID();
       }
    }

    public ResourcesContainer(ProjectsContainer _parent)
    {
        p_resources = new Vector();
        p_parent = _parent;
        lg_changed = false;
    }

    public int getResourcesNumber()
    {
        return p_resources.size();
    }

    public Resource getResourceForIndex(int _index)
    {
        return (Resource) p_resources.elementAt(_index);
    }

    public boolean isChanged()
    {
        return lg_changed;
    }

    public Resource getResourceForUID(long _uid)
    {
        synchronized (p_resources)
        {
            for (int li = 0; li < p_resources.size(); li++)
            {
                Resource p_res = (Resource) p_resources.elementAt(li);
                if (p_res.getUID() == _uid) return p_res;
            }
            return null;
        }
    }

    public void removeAllResources()
    {
        synchronized (p_resources)
        {
            p_resources.removeAllElements();
        }
        lg_changed = true;
    }

    public boolean removeResourceForUID(long _uid)
    {
        synchronized (p_resources)
        {
            for (int li = 0; li < p_resources.size(); li++)
            {
                Resource p_res = (Resource) p_resources.elementAt(li);
                if (p_res.getUID() == _uid)
                {
                    p_resources.removeElementAt(li);
                    lg_changed = true;
                    return true;
                }
            }

            p_parent.resourceRemoved(_uid);

            return false;
        }
    }

    public void removeResource(int _index)
    {
        synchronized (p_resources)
        {
            try
            {
                Resource p_res = getResourceForIndex(_index);
                p_resources.removeElementAt(_index);

                p_parent.resourceRemoved(p_res.getUID());

                lg_changed = true;
            }
            catch (Exception e)
            {
            }
        }
    }

    public void addResource(Resource _new)
    {
        synchronized (p_resources)
        {
            if (getResourceForUID(_new.getUID()) == null)
            {
                p_resources.addElement(_new);
                lg_changed = true;
            }
        }
    }

    public void loadFromArray(byte[] _array) throws IOException
    {
        synchronized (p_resources)
        {
            ByteArrayInputStream p_inStr = new ByteArrayInputStream(_array);
            DataInputStream p_dis = new DataInputStream(p_inStr);

            p_resources.removeAllElements();

            int i_number = p_dis.readUnsignedShort();
            for (int li = 0; li < i_number; li++)
            {
                Resource p_res = new Resource(this,"");
                p_res.loadFromStream(p_dis);
                p_resources.addElement(p_res);
            }
            lg_changed = false;
        }
    }

    public byte[] packToArray() throws IOException
    {
        synchronized (p_resources)
        {
            ByteArrayOutputStream p_outStream = new ByteArrayOutputStream(3000);
            DataOutputStream p_ous = new DataOutputStream(p_outStream);

            p_ous.writeShort(p_resources.size());
            for (int li = 0; li < p_resources.size(); li++)
            {
                Resource p_res = (Resource) p_resources.elementAt(li);
                p_res.saveToStream(p_ous);
            }

            p_ous.flush();
            p_ous.close();

            return p_outStream.toByteArray();
        }
    }
}
