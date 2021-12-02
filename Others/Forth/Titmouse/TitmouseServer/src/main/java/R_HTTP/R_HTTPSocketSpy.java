package R_HTTP;

import java.io.*;
import java.net.*;    
import java.util.*; 

public class R_HTTPSocketSpy extends Thread 
{
  Socket spy_socket;

  int sckt_port;
  R_HTTPClientThread owner_thread;
  InetAddress inet_addr;
  InputStream spy_is=null;
  boolean trace_connect;
  boolean StopFlag;
  
  public R_HTTPSocketSpy (boolean trace,ThreadGroup thr,R_HTTPClientThread ownthr) throws IOException
  {
		super(thr,"Client_Socket_Spy");
		trace_connect = trace;
		this.setDaemon(false); 
		owner_thread = ownthr;
        spy_socket = ownthr.client_socket; 
		sckt_port = spy_socket.getLocalPort();
		inet_addr = spy_socket.getInetAddress();
		StopFlag=false;
		spy_is=spy_socket.getInputStream();
		this.setPriority(Thread.MIN_PRIORITY);  
		this.start(); 
  }

  void printMsg()
  {
		spy_socket=null;

		try
		{

			Date tmp_date = new Date(System.currentTimeMillis());
			int tmp_ss = tmp_date.getSeconds();
			int tmp_mm = tmp_date.getMinutes();

			int tmp_hh = tmp_date.getHours();
			if (trace_connect) 
				System.out.println( String.valueOf(tmp_hh)+":"+String.valueOf(tmp_mm)+":"+String.valueOf(tmp_ss)+" "+inet_addr+" [port "+sckt_port+"]"+" client side is close");

			tmp_date = null;
     
		}
		catch (Exception ek) {}
  }
  
  public void AllStop()
  {
	StopFlag=true;  
	if (owner_thread!=null)
	{
		owner_thread.Stop();
		owner_thread=null;
	}
  }

  public void Stop()
  {
	StopFlag=true;  
  }

  
  public void run()
  {
        try
        {
                while(!StopFlag)
                {
                        if (spy_is.read()<0) break;
						Thread.yield(); 
				}
		}
		catch (Exception e)
		{}
		if (!StopFlag)
		{
			owner_thread.Stop();
			printMsg();
		}
		owner_thread=null;
  }

}
