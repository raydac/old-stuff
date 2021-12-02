package R_HTTP;

import java.util.*;

public class ScriptThread extends Thread 
{
		TimeScript time_script;
        ScriptForth  sf;
		BusServerParams busp;
		
        public ScriptThread(TimeScript ts,BusServerParams bsp)
        {
				busp = bsp;
				time_script = ts;
                this.setDaemon(true); 
                this.setPriority(Thread.NORM_PRIORITY);  
                this.start(); 
        }

        public void run()
        {
				sf=null;
				try
                {
                        sf = new ScriptForth(this,null,busp,null); 
                        sf.AddNumberConstant("FS-STARTCOUNTER",time_script.StartCounter);  
                        sf.AddStringConstant("FS-SCRIPTNAME",time_script.ScriptName);  
                        sf.AddStringConstant("FS-FILENAME",time_script.FileName);  
                        sf.PlayScript(time_script.FileName);
                }
                catch (Exception e)
                {
                        System.out.println("\nScript error ["+time_script.ScriptName+"] : "+e.getMessage()+", string "+sf.StringNumber); 
                }
                if (sf!=null) sf.close(); 
                sf=null;
        }
}
