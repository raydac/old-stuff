package R_HTTP;

import java.io.*;
import java.util.*; 
import java.text.*;
import java.net.*; 
import RImage.*; 

public class R_HTTPBinResponse
{
        public static String [] ShortMonths = {"Jan","Feb","Mar","Apr","May","Jun","Jul","Aug","Sep","Oct","Nov","Dec",""};
        public static String [] ShortDays   = {"","Sun","Mon","Tue","Wed","Thu","Fri","Sat"};   

        public String rsrc_name;        
        public String typ_file;
        public Date file_date;
        Socket cl_socket;
        File  rsrc_file;
        public boolean IS_Forth_Server_Page = false;
        public String Post_body; // Тело метода POST
        HTTPQueryRecord q_rec;  
        server_par separ; // указатель на таблицу параметров сервера
		String home_dir;
		Thread mainthread;
		BusServerParams busp;
		public ScriptForth scr_frt=null;
        
		public String URLDecode(String src)
        {
                int li=0;
                char cur_char;
                byte [] dim_byte = new byte[1];
                StringBuffer tmp_buf;
                StringBuffer dest = new StringBuffer("");
                for (li=0;li<src.length();li++)
                {
                   cur_char = src.charAt(li);
                                                
                   switch(cur_char)
                   {
                        case '+' : dest.append(" "); break;
                        case '%' : {
                                li++;
                                tmp_buf = new StringBuffer(String.valueOf(src.charAt(li)));
                                li++;  
                                tmp_buf.append(src.charAt(li));
                                try
                                {
                                        dim_byte[0] = (byte)Integer.parseInt(tmp_buf.toString(),16); 
                                        dest.append(new String(dim_byte)); 
                                }
                                catch (Exception e)
                                {
                                        dest.append("%"+tmp_buf.toString());
                                }
                              }; break;  
                        default : dest.append(cur_char); break;
                   }               
                } 
                return dest.toString();
        }

        void FillFieldParametersFromPost(String post_str, ScriptForth sf) throws Exception 
        { 
                StringBuffer name_par=new StringBuffer("");
                StringBuffer body_par=new StringBuffer("");
                boolean is_name = true;
                int li;
                char cur_char;
                for (li=0;li<post_str.length();li++)
                {
                        cur_char = post_str.charAt(li);
                        switch(cur_char)
                        {
                                case '&' : {
                                                if (name_par.length()>0)
                                                {
                                                    sf.AddStringConstant(URLDecode(name_par.toString()),URLDecode(body_par.toString())); 
                                                    name_par=new StringBuffer(""); body_par=new StringBuffer("");    
                                                    is_name=true;    
                                                }
                                            };break; 
                                case '=' : {
                                                is_name = false;
                                           }; break;     
                                default : {
                                                if (is_name) name_par.append(cur_char);
                                                        else body_par.append(cur_char);
                                          };
                        } 
                } 
           if (name_par.length()>0)
           {
              if (body_par.length()>0) sf.AddStringConstant(URLDecode(name_par.toString()),URLDecode(body_par.toString())); 
           }
        }

        public Vector GetResponseHeader(String client_ip_address) throws Exception
        {
                scr_frt=null;
                String ext_file;
                String net_datetime;
                int li;
                ScriptForthBuffer forth_body;

                file_date = new Date(rsrc_file.lastModified()); 
                Date cur_date  = new Date(System.currentTimeMillis()); 
                DateFormatSymbols dfs = new DateFormatSymbols();
                dfs.setShortMonths(ShortMonths);   
                dfs.setShortWeekdays(ShortDays);   

                SimpleDateFormat df = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss 'GMT'"); 
                df.setTimeZone(TimeZone.getTimeZone("GMT")); 
                df.setDateFormatSymbols(dfs); 

                Vector vector_body = new Vector(); 
                IS_Forth_Server_Page = false;
                
                if (typ_file.compareTo("FSP")==0)
                {
                        IS_Forth_Server_Page = true;
                        scr_frt = new ScriptForth(mainthread,cl_socket,busp,separ);
                        
                        int lmime_typ = scr_frt.AddStringConstant("FSP-CNTTYPE","text/html"); 
                        scr_frt.AddStringConstant("FSP-AUTHSCHEME", q_rec.AuthorizationScheme);
                        scr_frt.AddStringConstant("FSP-AUTHSTR",q_rec.AuthorizationNamePassword);
						
                        String AuthClName="";
                        if ((q_rec.AuthorizationNamePassword.length()>0) && q_rec.AuthorizationScheme.equalsIgnoreCase("BASIC"))
                        {
								StringTokenizer lstrt = new StringTokenizer(q_rec.AuthorizationNamePassword,":");
								AuthClName= lstrt.nextToken();  
								scr_frt.AddStringConstant("FSP-AUTHNAME",AuthClName);
								AuthClName= lstrt.nextToken();  
								scr_frt.AddStringConstant("FSP-AUTHPSSWD",AuthClName);
						}  
						else
						{
								scr_frt.AddStringConstant("FSP-AUTHNAME","");
								scr_frt.AddStringConstant("FSP-AUTHPSSWD","");
						}
						
						scr_frt.AddClientIPAddr(); 
                        scr_frt.AddStringConstant("FSP-QUERY",q_rec.CGIString);  
						scr_frt.AddStringConstant("FSP-USERAGENT",q_rec.User_Agent);                          
						scr_frt.AddStringConstant("FSP-ACCEPT",q_rec.Accept);                          

						// Добавляем параметры сервера
                        if (Post_body!=null)
                        {                        
                                scr_frt.AddStringConstant("FSP-POSTBODY",Post_body);
                                if (q_rec.Content_Type.equalsIgnoreCase("application/x-www-form-urlencoded"))
                                {
                                   FillFieldParametersFromPost(Post_body,scr_frt);
                                }           
                        }
                        else
                        {
                                if (q_rec.CGIString.length()>0)
                                {
                                        scr_frt.AddStringConstant("FSP-POSTBODY",q_rec.CGIString);
                                        FillFieldParametersFromPost(q_rec.CGIString,scr_frt);  
                                }  
                                else
                                scr_frt.AddStringConstant("FSP-POSTBODY","");
                        }

                        try
                        {
							OutputStream ltmpos = scr_frt.PlayScript(rsrc_name);
							if (ltmpos instanceof ScriptForthBuffer) forth_body=(ScriptForthBuffer)ltmpos; else forth_body=null;
                        }
                        catch (Exception e)
                        {
								Exception ee = e;
								try
                                {
                                        scr_frt.close(); 
                                }
                                catch(Exception t)
                                {}
                                throw new Exception("str# "+String.valueOf(scr_frt.StringNumber)+":"+ee.getMessage()); 
                        }

                        if (forth_body!=null)
                        {
                                vector_body.addElement("Date: "+df.format(cur_date)+"\r\n"); 
                                vector_body.addElement("Server: RHTTP/1.4(Titmouse)\r\n");
                                vector_body.addElement("Allow: GET, HEAD, POST\r\n");
                                vector_body.addElement("MIME-version: 1.0\r\n");
                                vector_body.addElement("Content-Length: "+String.valueOf(forth_body.len)+"\r\n");
                                vector_body.addElement("Content-type: "+scr_frt.GetStringVar(lmime_typ)+"\r\n");
                                vector_body.addElement("Last-Modified: "+df.format(cur_date)+"\r\n");       
                                vector_body.addElement("\r\n");
                                vector_body.addElement(forth_body);
                        }
                        else 
						{
							if (scr_frt!=null) scr_frt.close();
							scr_frt = null;
							return null;
						}
							
                        file_date = null;
                        cur_date  = null;
                        dfs = null;

						if (scr_frt!=null) scr_frt.close();
						scr_frt = null;

                        return vector_body; 
                } 
        
                vector_body.addElement("Date: "+df.format(cur_date)+"\r\n"); 
                vector_body.addElement("Server: R-HTTP/1.5\r\n");
                vector_body.addElement("Allow: GET, HEAD\r\n");
                vector_body.addElement("MIME-version: 1.0\r\n");
                vector_body.addElement("Content-Length: "+Long.toString(rsrc_file.length())+"\r\n");

				String mime_typ = (String)busp.mtable.get(typ_file);
				if (mime_typ!=null)
				{
					vector_body.addElement("Content-type: "+mime_typ+"\r\n");
				}
				else
					vector_body.addElement("Content-type: text/plain\r\n");

                vector_body.addElement("Last-Modified: "+ df.format(file_date)+"\r\n");
                vector_body.addElement("\r\n");

                cur_date  = null;
                dfs = null;

                try
                {
                        if (scr_frt!=null) scr_frt.close(); 
                }
                catch(Exception e) {}

                return vector_body;     
        }  

        public FileInputStream GetResponseBody() throws IOException 
        {
				FileInputStream fis;
                try
                {
                        fis = new FileInputStream(rsrc_file); 
                }
                catch (Exception e)
                {
                        throw new IOException(); 
                }
                return fis;
        }    

        public R_HTTPBinResponse(Thread mainthr, BusServerParams bsp ,Socket clsock,String h_dir,String r_name,server_par sp,HTTPQueryRecord qrec) throws IOException 
        {
						mainthread = mainthr;
						busp = bsp;
						cl_socket = clsock;
						home_dir = h_dir;
						separ = sp;
                        q_rec = qrec;
						rsrc_name = home_dir + r_name;
						rsrc_name = rsrc_name.toUpperCase(); 
						int li;
                        li = rsrc_name.lastIndexOf('.');
                        if (li<0)
                        {
                                rsrc_name = rsrc_name + sp.default_page.toUpperCase();
                                li = rsrc_name.lastIndexOf('.');
                        } 

						typ_file="";
                        for (int la=(rsrc_name.length()-1);la>li;la--)
                        {
                            typ_file = rsrc_name.charAt(la)+typ_file; 
                        }
 
						// Производим поиск и замену псевдонимов
						while(rsrc_name.indexOf('~')>=0)
						{
							int apos = rsrc_name.indexOf('~');
							String first_part = rsrc_name.substring(0,apos);
							int lii=apos+1;
							StringBuffer lali = new StringBuffer(10); 
							while(lii<rsrc_name.length())
							{
								char lcc = rsrc_name.charAt(lii);
								if ((lcc<'A')||(lcc>'Z')) break;
								else lali.append(lcc); 
								lii++;
							}
							String tmpal = lali.toString();
							if (busp.aliases.containsKey(tmpal))
							{
								rsrc_name = first_part + (String)(busp.aliases.get(tmpal))+rsrc_name.substring(lii);
							}
							else
							{
								rsrc_name = first_part + tmpal+rsrc_name.substring(lii); 
							}
						}
						
						rsrc_file = new File(rsrc_name);

						if ((!rsrc_file.exists())||(rsrc_file.isDirectory())) 
						{
                            rsrc_file = null;
							throw new IOException("0");
						}

						// Проверяем права на доступ к файлу
						//-----------------------------------
						{
							String tmp_cpath = rsrc_file.getCanonicalPath().substring(0,home_dir.length());
							if (!tmp_cpath.equalsIgnoreCase(home_dir)) throw new IOException("000");
						}
						//-----------------------------------
						
        }
}
