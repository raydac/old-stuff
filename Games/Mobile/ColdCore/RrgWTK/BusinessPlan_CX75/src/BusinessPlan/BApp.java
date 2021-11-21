package BusinessPlan;

import BusinessPlan.AppActionListener;

public interface BApp
{
    public String getAppID();
    public void initApplication(rmsFS _rms,AppActionListener _listener) throws Exception;
    public void releaseApplication(rmsFS _rms) throws Exception;
}
