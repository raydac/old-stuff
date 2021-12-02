package R_HTTPResponse;

import java.util.*;  
import java.io.*;
import java.text.*; 

public class HTTPQueryRecord
{

        public static String [] ShortMonths = {"Jan","Feb","Mar","Apr","May","Jun","Jul","Aug","Sep","Oct","Nov","Dec",""};
        public static String [] ShortDays   = {"","Sun","Mon","Tue","Wed","Thu","Fri","Sat"};   

        public static String [] LongMonths = {"January","February","March","April","May","June","July","August","September","October","November","December",""};
        public static String [] LongDays   = {"","Sunday","Monday","Tuesday","Wednesday","Thursday","Friday","Saturday"};

        public String Method="";
        public String HTTPVersion="";
        public String URLString="";
        public String CGIString="";
        public Date IMS_Date;
        public String IMS_String;
        public String User_Agent="Unknown";
        public String From="Unknown";
        public String AuthorizationNamePassword="";
        public String AuthorizationScheme="";
        public String Referer="";
        public Date Date_Date;
        public String Date_String;
        public String Pragma;
        public int Content_Length;
        public String Content_Type;
     
        public HTTPQueryRecord(Vector qr) throws Exception        
        {
                int li,lpos;
                String buf_string,left_part,right_part;                

                if (qr==null) throw new Exception("Query is null"); 
                if (qr.size()==0) throw new Exception ("Empty query list");                 

                StringTokenizer s_token = new StringTokenizer((String)qr.elementAt(0)," ");   
                
                DateFormatSymbols dfs = new DateFormatSymbols();
                dfs.setShortMonths(ShortMonths);   
                dfs.setShortWeekdays(ShortDays );   
                dfs.setMonths(LongMonths);            
                dfs.setWeekdays(LongDays);  

                SimpleDateFormat df = new SimpleDateFormat("EEE', 'dd' 'MMM' 'yyyy' 'HH:mm:ss' GMT'"); 

                df.setTimeZone(TimeZone.getTimeZone("GMT")); 
                df.setDateFormatSymbols(dfs); 

                try
                {
                Method = s_token.nextToken();
                URLString = s_token.nextToken();                  
                HTTPVersion = s_token.nextToken(); 
                }
                catch (Exception e) {};

                lpos = URLString.indexOf('?');
                if (lpos>=0)
                {
                        CGIString=URLString.substring(lpos+1); 
                        URLString=URLString.substring(0,lpos);
                }        

                URLString = URLString.replace('/',File.separatorChar);  
                if (URLString.length()>7)
                { 
                        buf_string = URLString.substring(0,6);
                        if ((buf_string.equalsIgnoreCase("http://"))||(buf_string.equalsIgnoreCase("http:\\")))
                        {
                                buf_string = buf_string.substring(7);
                                li = buf_string.indexOf(File.separatorChar);
                                if (li>=0)
                                {
                                        buf_string=buf_string.substring(li+1);
                                        if (buf_string.length()==0){buf_string=String.valueOf(File.separatorChar);}    
                                }           
                        }
                }  

                for(li=1;li<qr.size();li++)
                {
                        buf_string = (String)qr.elementAt(li);
                        lpos = buf_string.indexOf(' ');
                        if (lpos>=0)
                        {
                                left_part=buf_string.substring(0,lpos-1).toUpperCase();
                                right_part=buf_string.substring(lpos+1).trim(); 
                                if (left_part.compareTo("DATE")==0)
                                {
                                        Date_String = right_part;
                                }
                                else
                                if (left_part.compareTo("CONTENT-LENGTH")==0)
                                {
                                        Content_Length = (Integer.valueOf(right_part)).intValue();
                                }
                                else
                                if (left_part.compareTo("CONTENT-TYPE")==0)
                                {
                                        Content_Type = right_part;
                                }
                                else
                                if (left_part.compareTo("PRAGMA")==0)
                                {
                                        Pragma = right_part;
                                }
                                else
                                if (left_part.compareTo("AUTHORIZATION")==0)
                                {
                                        StringTokenizer st = new StringTokenizer(right_part," ");
                                        try
                                        {
                                                AuthorizationScheme = st.nextToken();  
                                        }
                                        catch (NoSuchElementException e)
                                        {
                                                AuthorizationScheme = "";  
                                        }
                                        try
                                        {
                                                if (AuthorizationScheme.equalsIgnoreCase("BASIC"))
                                                {  
                                                        AuthorizationNamePassword = st.nextToken();  
                                                        Base64.Decoder bs64dec = Base64.getDecoder();
                                                        AuthorizationNamePassword = new String(bs64dec.decode(AuthorizationNamePassword));
                                                        bs64dec = null;
                                                }
                                                else AuthorizationNamePassword = st.nextToken();
                                        }
                                        catch (NoSuchElementException e)
                                        {
                                                AuthorizationNamePassword  = "";  
                                        }
                                }
                                else
                                if (left_part.compareTo("FROM")==0)
                                {
                                        From = right_part;
                                }
                                else
                                if (left_part.compareTo("IF-MODIFIED-SINCE")==0)
                                {
                                    try
                                    {        
                                        IMS_String = right_part;
                                        IMS_Date = df.parse(IMS_String); 
                                    }    
                                    catch (Exception e) {IMS_Date=null;}
                                }
                                else
                                if (left_part.compareTo("REFERER")==0)
                                {
                                        Referer = right_part;
                                }
                                else
                                if (left_part.compareTo("USER-AGENT")==0)
                                {
                                        User_Agent = right_part;
                                }
                                     
                        }      
                }  
                        qr=null;
        }

}
