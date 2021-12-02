import java.net.*;
import java.io.*;
import java.util.*; 
import R_HTTPResponse.*; 

public class NFDServerMain
{

        /** Выводит на экран список и краткое описание команд сервера*/
        static void help()
        {
                System.out.println("---------------List of commands----------------------------------------");
                System.out.println("  ABOUT           About this server");
                System.out.println("  HELP            This help");
                System.out.println("  MEM             View free memory area");
                System.out.println("  CLOSE           Down server");
                System.out.println("  TRACEON         Enable trace client connections");
                System.out.println("  TRACEOFF        Disable trace client connections");
                System.out.println("  PORT            Change port number");   
                System.out.println("  HOMEDIR         Change home dir");
                System.out.println("  DEFAULTPAGE     Change default page");    
                System.out.println("  MAXCLIENTS      Change quantity of clients");    
                System.out.println("-----------------------------------------------------------------------");
        }

        /** Запись установок сервера в файл */
        static void save_ini_file(server_par sep,String fname) throws Exception
        {
                Properties lprop = new Properties();
                lprop.put("SERVER_PORT",String.valueOf(sep.server_port));
                lprop.put("MAX_CON",String.valueOf(sep.max_client_con));
                lprop.put("HOME_DIR",sep.home_dir);
                lprop.put("DEFAULT_PAGE",sep.default_page);                         
                
                FileOutputStream fos = new FileOutputStream(fname);

                lprop.save(fos,"R-HTTP 1.0 SERVER INI FILE");
                fos.close(); 
                lprop.clear();
                lprop = null;   
        }

        /** Выводит на экран табличку "О программе" */
        static void about()
        {
                System.out.println("============================================================");
                System.out.println("     R-HTTP Server v 1.00 (with support Forth Server Pages)");
                System.out.println("     Author: Igor A. Maznitsa"); 
                System.out.println("     E-Mail:    iam@raydac.spb.ru");
                System.out.println("     Home page: http://www.rrg.da.ru");
                System.out.println("============================================================");
        }

	public static void main(String[] args)
	{
              Calendar clndr = Calendar.getInstance() ;   
              String server_dir = System.getProperty("user.dir"); // Содержит имя каталога из которого запущен сервер
              String kbd_string; 
              server_par serpar = new server_par();   

              // Загрузка установочного файла
              Properties inifile = new Properties();   

              // Загрузка файла server.ini  
              try
              {
                    inifile.load(new FileInputStream(server_dir+File.separatorChar+"server.ini"));
              }
              catch (Exception e) {}
                
              // Блок заполнения системных переменных  
              serpar.home_dir=inifile.getProperty("HOME_DIR");   
              if (serpar.home_dir==null) { serpar.home_dir=server_dir; } 
              serpar.default_page=inifile.getProperty("DEFAULT_PAGE");   
              if (serpar.default_page==null) { serpar.default_page = "index.htm"; } 
              kbd_string = inifile.getProperty("MAX_CON");
              if (kbd_string==null) { serpar.max_client_con = new Integer(100); } 
                else serpar.max_client_con = Integer.valueOf(kbd_string);
              kbd_string = inifile.getProperty("SERVER_PORT");   
              if (kbd_string==null) { serpar.server_port = new Integer(80); } 
                else serpar.server_port = Integer.valueOf(kbd_string);

                inifile = null;

              BufferedReader kbd_rd = new BufferedReader(new InputStreamReader(System.in));    
              try  
              {
                        System.out.println("---===[R-HTTP Server v1.00 has been started]===---");
                        System.out.println("Today "+ clndr.getTime()); 
                        
                        Compiler.enable(); 

                        if (Compiler.compileClasses("R_HTTPResponse"))
                        System.out.println("R_HTTPResponses compiling successfull");
                        else  System.out.println("R_HTTPResponses compiling failed");
                        if (Compiler.compileClasses("ScriptForth"))
                        System.out.println("ScriptForth compiling successfull");
                        else  System.out.println("ScriptForth compiling failed");
                        if (Compiler.compileClasses("R_HTTPClientThread"))
                        System.out.println("R_HTTPClientThread compiling successfull");
                        else  System.out.println("R_HTTPResponses compiling failed");

                        R_HTTPServer MainServer = new R_HTTPServer(serpar.server_port.intValue(),serpar);

                        MainServer.Server_Home_Dir = serpar.home_dir;
                        MainServer.Server_Default_Page = serpar.default_page;                        
                        MainServer.max_client_count = serpar.max_client_con.intValue();                        

                        MainServer.start();

                        while(true)
                        {
                                kbd_string = kbd_rd.readLine();
                                // Блок обработки клавиатурных команд
                                if (kbd_string.equalsIgnoreCase("homedir")) 
                                {
                                        MainServer.trace_client = false; 
                                        System.out.println("Old home directory : "+serpar.home_dir);  
                                        System.out.print("Enter new home directory :>"); 
                                        kbd_string = kbd_rd.readLine().trim();
                                        if (kbd_string.length()==0) continue;
                                        File v_f = new File(kbd_string);
                                        if (!v_f.isDirectory())
                                        {
                                                System.out.println("Error path");  
                                        }
                                        else 
                                        {
                                                serpar.home_dir=kbd_string;
                                                MainServer.Server_Home_Dir = serpar.home_dir; 
                                                try
                                                {
                                                        save_ini_file(serpar,server_dir+File.separatorChar+"server.ini");
                                                }      
                                                catch (Exception e)
                                                {
                                                        System.out.println("Error of writing ini file!");
                                                }
                                        }
                                        v_f = null;
                                }
                                else
                                if (kbd_string.equalsIgnoreCase("defaultpage")) 
                                {
                                        MainServer.trace_client = false; 
                                        System.out.println("Old default page : "+serpar.default_page);  
                                        System.out.print("Enter new default page :>"); 
                                        kbd_string = kbd_rd.readLine().trim();
                                        if (kbd_string.length()==0) continue; 
                                        File v_f = new File(serpar.home_dir+File.separatorChar+kbd_string);
                                        if (!v_f.isFile())
                                        {
                                                System.out.println("Error file");  
                                        }
                                        else 
                                        {
                                                serpar.default_page=kbd_string;
                                                MainServer.Server_Default_Page = serpar.default_page; 
                                                try
                                                {
                                                        save_ini_file(serpar,server_dir+File.separatorChar+"server.ini");
                                                }      
                                                catch (Exception e)
                                                {
                                                        System.out.println("Error of writing ini file!");
                                                }
                                        }
                                        v_f = null;
                                }
                                else
                                if (kbd_string.equalsIgnoreCase("close")) 
                                {
                                        MainServer.CloseServer(); 
                                        MainServer = null;
                                                                                
                                         try
                                         {
                                               save_ini_file(serpar,server_dir+File.separatorChar+"server.ini");
                                          }      
                                          catch (Exception e)
                                          {
                                                  System.out.println("Error of writing ini file!");
                                          }

                                        System.out.println("---===[R-HTTP Server v1.00 has been closed]===---");
                                        return;
                                }
                                else
                                if (kbd_string.equalsIgnoreCase("port"))
                                {
                                        MainServer.trace_client = false; 
                                        System.out.print("Enter new port number:>");
                                        if (kbd_string.length()==0) continue;
                                        kbd_string = kbd_rd.readLine();
                                        try
                                        {
                                                serpar.server_port = Integer.valueOf(kbd_string); 
                                                if ((serpar.server_port.intValue()>65535)||(serpar.server_port.intValue()<0)) throw new Exception();
                                                try
                                                {
                                                        save_ini_file(serpar,server_dir+File.separatorChar+"server.ini");
                                                }      
                                                catch (Exception e)
                                                {
                                                        System.out.println("Error of writing ini file!");
                                                }
                                                System.out.println("For efficiency reload server, please!");        
                                        }
                                        catch (Exception e)
                                        {
                                                System.out.println("Invalid port number!");
                                                serpar.server_port = new Integer(80);
                                                System.out.println("Port reset to default value (80)");
                                        }  
                                }
                                else
                                if (kbd_string.equalsIgnoreCase("maxclients"))
                                {
                                        MainServer.trace_client = false; 
                                        System.out.println("Old quantity of clients "+String.valueOf(serpar.max_client_con));
                                        System.out.print("Enter new client's quantity:>");
                                        if (kbd_string.length()==0) continue;
                                        kbd_string = kbd_rd.readLine();
                                        try
                                        {
                                                if (kbd_string.length()==0) continue; 
                                                serpar.max_client_con = Integer.valueOf(kbd_string); 
                                                if ((serpar.max_client_con.intValue()>65535)||(serpar.max_client_con.intValue()<0)) throw new Exception();
                                                MainServer.max_client_count=serpar.max_client_con.intValue();  
                                                try
                                                {
                                                        save_ini_file(serpar,server_dir+File.separatorChar+"server.ini");
                                                }      
                                                catch (Exception e)
                                                {
                                                        System.out.println("Error of writing ini file!");
                                                }
                                        }
                                        catch (Exception e)
                                        {
                                                System.out.println("Invalid number!");
                                                System.out.println("Set default quantity 100!");
                                                MainServer.max_client_count=100;
                                                serpar.max_client_con=new Integer(100);  
                                        }  
                                }
                                else
                                if (kbd_string.equalsIgnoreCase("about"))
                                {
                                        about(); 
                                }
                                else
                                if (kbd_string.equalsIgnoreCase("traceon"))
                                {
                                        MainServer.trace_client = true;
                                }
                                else
                                if (kbd_string.equalsIgnoreCase("traceoff"))
                                {
                                        MainServer.trace_client = false;
                                }
                                else
                                if (kbd_string.equalsIgnoreCase("help"))
                                {
                                        help(); 
                                }
                                else
                                if (kbd_string.equalsIgnoreCase("mem"))
                                {
                                        System.out.println("Free memory : "+Long.toString(Runtime.getRuntime().freeMemory())+" bytes");    
                                } 
                                else
                                {
                                        System.out.println("Unknown instruction!");
                                }
                        }
              }  
              catch (IOException e){};  
	}
}
