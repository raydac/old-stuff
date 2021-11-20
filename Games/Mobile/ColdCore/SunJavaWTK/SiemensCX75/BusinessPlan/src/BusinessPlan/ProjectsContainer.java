package BusinessPlan;

import BusinessPlan.AppActionListener;
import BusinessPlan.rmsFS;
import BusinessPlan.*;

import java.util.Vector;
import java.io.*;

public class ProjectsContainer implements BApp
{
    private static final String RECORDS_BLOCK = "&%RCRDS%&";
    private static final String RESOURCES_BLOCK = "&%RSRCS%&";

    private ResourcesContainer p_resourceContainer;

    public static final long MILLISECONDS_MINUTE = 60000l;
    public static final long MILLISECONDS_HOUR = MILLISECONDS_MINUTE * 60;
    public static final long MILLISECONDS_WORKDAYDAY = MILLISECONDS_HOUR * 8;
    public static final long MILLISECONDS_FULLDAY = MILLISECONDS_HOUR * 24;

    private Vector p_projectRecords;
    private Project p_activeProject;
    private ProjectRecord p_activeProjectRecord;

    public ResourcesContainer getResourcesContainer()
    {
        return p_resourceContainer;
    }

    public Project getActiveProject()
    {
        return p_activeProject;
    }

    public int getProjectsNumber()
    {
        return p_projectRecords.size();
    }

    public ProjectRecord getProjectRecordForIndex(int _index)
    {
        return (ProjectRecord) p_projectRecords.elementAt(_index);
    }

    protected void resourceRemoved(long _uid)
    {
        synchronized(p_projectRecords)
        {
            for(int li=0;li<p_projectRecords.size();li++)
            {
                ProjectRecord p_rec = (ProjectRecord) p_projectRecords.elementAt(li);
                if (p_rec.l_person == _uid)
                {
                    p_rec.l_person = -1;
                }
            }
        }
    }

    private void saveMRecord(rmsFS _rms) throws Exception
    {
        rmsFS.FSRecord p_mrec = _rms.getRecordForName(RECORDS_BLOCK);
        if (p_mrec != null)
        {
            byte[] ab_mrek = makeMRec();
            p_mrec.setData(ab_mrek, true);
        }
    }

    public void removeProject(int _index, rmsFS _rms) throws Exception
    {
        synchronized (p_projectRecords)
        {
            if (p_activeProject != null) throw new IOException("You have to close active project before");
            ProjectRecord p_record = (ProjectRecord) p_projectRecords.elementAt(_index);
            openProject(_index);
            if (p_activeProject != null)
            {
                p_activeProject.processBeforeProjectRemove();
                closeActiveProject(_rms, true);
            }
            p_projectRecords.removeElementAt(_index);
            _rms.deleteRecord(p_record.p_rmsRecord);
            saveMRecord(_rms);
        }
    }

    public Vector createProblemProjectList()
    {
        Vector p_vector = new Vector();
        long l_currentDate = System.currentTimeMillis();
        synchronized (p_projectRecords)
        {
            for (int li = 0; li < p_projectRecords.size(); li++)
            {
                ProjectRecord p_proj = (ProjectRecord) p_projectRecords.elementAt(li);
                long l_endDate = p_proj.l_endDate;
                if (p_proj.i_rateOfDelivery < 100 && l_endDate >= 0)
                {
                    if (l_endDate <= l_currentDate)
                    {
                        p_vector.addElement(p_proj);
                    }
                }
            }
        }
        return p_vector;
    }

    public void closeActiveProject(rmsFS _rms, boolean _save) throws Exception
    {
        if (p_activeProject != null)
        {
            synchronized (p_activeProject)
            {
                byte[] ab_projectData = p_activeProject.makeArray();
                p_activeProjectRecord.s_ProjectName = p_activeProject.getName();
                p_activeProjectRecord.i_projectIcon = p_activeProject.getIconIndex();
                p_activeProjectRecord.i_projectPriority = p_activeProject.getPriority();
                p_activeProjectRecord.l_startDate = p_activeProject.l_startdate;
                p_activeProjectRecord.l_endDate = p_activeProject.l_enddate;
                p_activeProjectRecord.i_rateOfDelivery = p_activeProject.getProgress();
                p_activeProjectRecord.l_person = p_activeProject.getResponsiblePerson();

                p_activeProjectRecord.p_rmsRecord.setData(ab_projectData, true);

                if (_save) saveMRecord(_rms);

                p_activeProject = null;
                p_activeProjectRecord = null;
            }
        }
    }

    public Project makeEmptyProject(String _name)
    {
        return new Project(this, _name);
    }

    public void saveResources(rmsFS _rms) throws Exception
    {
        if (p_resourceContainer.lg_changed)
        {
            rmsFS.FSRecord p_resourceRec = _rms.getRecordForName(RESOURCES_BLOCK);
            if (p_resourceRec == null) throw new IOException("Can't find the resource record");
            byte[] ab_array = p_resourceContainer.packToArray();
            p_resourceRec.setData(ab_array, true);
            ab_array = null;
            p_resourceContainer.lg_changed = false;
        }
    }

    private void readResources(rmsFS _rms) throws Exception
    {
        rmsFS.FSRecord p_resourceRec = _rms.getRecordForName(RESOURCES_BLOCK);
        if (p_resourceRec == null) throw new IOException("Can't find the resource record");
        byte[] ab_array = p_resourceRec.getData();
        p_resourceContainer.loadFromArray(ab_array);
        ab_array = null;
    }

    public void addNewProject(Project _project, rmsFS _rms) throws Exception
    {
        synchronized (p_projectRecords)
        {
            long l_uid = System.currentTimeMillis();
            long l_systemTime = l_uid;
            byte[] ab_name = new byte[8];
            for (int li = 0; li < 8; li++)
            {
                ab_name[li] = (byte) l_systemTime;
                l_systemTime >>= 8;
            }
            String s_recordName = new String(ab_name);
            ab_name = null;

            _project.setCreatingDate(l_uid);

            rmsFS.FSRecord p_newRmsRecord = _rms.createNewRecord(s_recordName, 4000);

            ProjectRecord p_newrec = new ProjectRecord(_project, p_newRmsRecord);

            p_projectRecords.addElement(p_newrec);

            p_activeProject = _project;
            p_activeProjectRecord = p_newrec;
        }
    }

    public void openProject(int _index) throws Exception
    {
        if (p_activeProject != null) throw new IOException("You have opened a project already");
        ProjectRecord p_record = (ProjectRecord) p_projectRecords.elementAt(_index);
        byte[] ab_projectData = p_record.p_rmsRecord.getData();
        Project p_openedProj = new Project(this, "");
        p_openedProj.loadFromArray(ab_projectData);

        p_activeProject = p_openedProj;
        p_activeProjectRecord = p_record;

        ab_projectData = null;
    }

    public ProjectsContainer()
    {
        p_resourceContainer = new ResourcesContainer(this);
        p_projectRecords = new Vector();
        p_activeProject = null;
    }

    public String getAppID()
    {
        return "MPRJ100";
    }

    protected void resourcesPoolUpdated(rmsFS _rms) throws Exception
    {
        saveMRecord(_rms);
    }

    private void parseMRec(byte[] _data, rmsFS _rms) throws IOException
    {
        synchronized (p_projectRecords)
        {
            DataInputStream p_inStream = new DataInputStream(new ByteArrayInputStream(_data));
            p_projectRecords.removeAllElements();
            int i_projectsNumber = p_inStream.readUnsignedShort();
            for (int li = 0; li < i_projectsNumber; li++)
            {
                String s_recordName = p_inStream.readUTF();
                rmsFS.FSRecord p_record = _rms.getRecordForName(s_recordName);
                if (p_record == null) throw new IOException("Can't find a project record");

                String s_projectName = p_inStream.readUTF();
                int i_iconIndex = p_inStream.readUnsignedByte();
                int i_priority = p_inStream.readUnsignedByte();
                long l_startDate = p_inStream.readLong();
                long l_endDate = p_inStream.readLong();
                int i_rateOfdelivery = p_inStream.readByte();
                long l_person = p_inStream.readLong();

                ProjectRecord p_projRecord = new ProjectRecord(s_projectName, p_record, i_iconIndex, i_priority, l_startDate, l_endDate, i_rateOfdelivery, l_person);
                p_projectRecords.addElement(p_projRecord);
            }
            p_inStream.close();
            p_inStream = null;
        }
    }

    private byte[] makeMRec() throws IOException
    {
        synchronized (p_projectRecords)
        {
            ByteArrayOutputStream p_outStream = new ByteArrayOutputStream(4000);
            DataOutputStream p_out = new DataOutputStream(p_outStream);
            p_out.writeShort(p_projectRecords.size());
            for (int li = 0; li < p_projectRecords.size(); li++)
            {
                ProjectRecord p_record = (ProjectRecord) p_projectRecords.elementAt(li);
                p_out.writeUTF(p_record.p_rmsRecord.s_name);
                p_out.writeUTF(p_record.s_ProjectName);
                p_out.writeByte(p_record.i_projectIcon);
                p_out.writeByte(p_record.i_projectPriority);
                p_out.writeLong(p_record.l_startDate);
                p_out.writeLong(p_record.l_endDate);
                p_out.writeByte(p_record.i_rateOfDelivery);
                p_out.writeLong(p_record.l_person);
            }
            p_out.close();
            p_out = null;

            return p_outStream.toByteArray();
        }
    }

    public void initApplication(rmsFS _rms, AppActionListener _listener) throws Exception
    {
        rmsFS.FSRecord p_mrec = _rms.getRecordForName(RECORDS_BLOCK);
        if (p_mrec != null)
        {
            parseMRec(p_mrec.getData(), _rms);
        }
        else
        {
            _rms.createNewRecord(RECORDS_BLOCK, 4000);
        }

        p_mrec = _rms.getRecordForName(RESOURCES_BLOCK);
        if (p_mrec != null)
        {
            readResources(_rms);
        }
        else
        {
            _rms.createNewRecord(RESOURCES_BLOCK, 4000);
        }
    }

    public void releaseApplication(rmsFS _rms) throws Exception
    {
        if (p_activeProject != null)
        {
            closeActiveProject(_rms, true);
        }
        saveResources(_rms);
    }
}
