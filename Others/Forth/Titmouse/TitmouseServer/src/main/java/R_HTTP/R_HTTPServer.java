package R_HTTP;

import java.net.*;   
import java.io.*;
import java.util.*;  
import RImage.*;

public class R_HTTPServer extends Thread 
{
		ServerSocket main_server_socket; // "Слушающий сокет"
		public ThreadGroup client_group;
		public boolean trace_client = false; // Флаг трассировки подключений клиентов
        public server_par spar; // Таблица с параметрами сервера
		public BusServerParams busp; // Общие серверные параметры
		boolean ThreadSuspend=false; 
		boolean Closing=false;
		
		synchronized public boolean IsSuspend()
		{
			return ThreadSuspend;	
		}
		
		synchronized public void ServerSuspend()
		{
			this.suspend();
			ThreadSuspend=true; 
		}

		synchronized public void ServerResume()
		{
			this.resume();
			ThreadSuspend=false; 
		}
		
        public R_HTTPServer(int port,server_par sp,BusServerParams bsp) throws IOException 
        {
				super();
			
				busp = bsp;
				this.setDaemon(true);
                this.setPriority(this.NORM_PRIORITY);
                spar = sp;
				client_group = new ThreadGroup("Client group");
				client_group.setDaemon(false);
				client_group.setMaxPriority(Thread.NORM_PRIORITY);
				client_group.allowThreadSuspension(true); 
				try
                {                
                        main_server_socket = new ServerSocket(spar.server_port.intValue()); 
                }
                catch (IOException e)
                {
                        throw new IOException("Error of create the Server Socket!"); 
                }
                
        }

        public void  CloseServer()
        {
				Closing = true;
				try
                {
					Thread [] nthr = new Thread[client_group.activeCount()]; 
					int lli= client_group.enumerate(nthr);
					for(int lii=0;lii<lli;lii++)
					{
						if (nthr[lii]!=null)
						{
							if (nthr[lii] instanceof R_HTTPSocketSpy)
							{
								((R_HTTPSocketSpy)nthr[lii]).AllStop();
							}
							nthr[lii]=null;
						}
					}
					client_group = null;
                }
                catch (Exception e) {} 
         }

        public void run()
        {
                Socket locsckt;
                R_HTTPClientThread ct;
				R_HTTPSocketSpy rssp;
				InetAddress cur_inetaddr=null;
				while(true)
                {
                        try
                        {
                                // Прослушиваем заданный порт
                                locsckt = main_server_socket.accept();
								if (Closing) break;
								if (client_group.activeCount()>=spar.max_client_con.intValue())                                 
								{
                                        locsckt.close();
                                        yield();
                                        continue;
                                }
								cur_inetaddr = locsckt.getInetAddress();
								if  (trace_client) 
								{
									Date tmp_date = new Date(System.currentTimeMillis());
									int tmp_ss = tmp_date.getSeconds();
									int tmp_mm = tmp_date.getMinutes();
									int tmp_hh = tmp_date.getHours();
									System.out.println( String.valueOf(tmp_hh)+":"+String.valueOf(tmp_mm)+":"+String.valueOf(tmp_ss)+" "+cur_inetaddr.toString()+" [port "+spar.server_port+"] connected");
									tmp_date = null;
								}
									// Запускаем поток клиента и передаем ему параметры                                
									ct = new R_HTTPClientThread(client_group,locsckt,spar,trace_client,busp,spar);
									yield();
                        }
                        catch (Exception e){}
                }
        }

}
