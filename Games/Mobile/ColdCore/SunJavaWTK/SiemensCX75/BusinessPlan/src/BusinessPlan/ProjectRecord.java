package BusinessPlan;

import BusinessPlan.rmsFS;
import BusinessPlan.Project;

public class ProjectRecord
{
    public String s_ProjectName;
    public rmsFS.FSRecord p_rmsRecord;
    public int i_projectIcon;
    public int i_projectPriority;
    public long l_startDate;
    public long l_endDate;
    public int i_rateOfDelivery;
    public long l_person;

    public ProjectRecord(Project _project, rmsFS.FSRecord _record)
    {
        p_rmsRecord = _record;
        s_ProjectName = _project.getName();
        i_projectIcon = _project.getIconIndex();
        i_projectPriority = _project.getPriority();
        l_startDate = _project.getStartDate();
        l_endDate = _project.getEndDate();
        i_rateOfDelivery = _project.getProgress();
        l_person = _project.getResponsiblePerson();
    }

    public ProjectRecord(String _projectName, rmsFS.FSRecord _record, int _icon, int _projectPriority, long _startDate, long _endDate,int _rateOfDelivery,long _person)
    {
        l_startDate = _startDate;
        l_endDate = _endDate;
        s_ProjectName = _projectName;
        p_rmsRecord = _record;
        i_projectIcon = _icon;
        i_projectPriority = _projectPriority;
        i_rateOfDelivery = _rateOfDelivery;
        l_person = _person;
    }

}
