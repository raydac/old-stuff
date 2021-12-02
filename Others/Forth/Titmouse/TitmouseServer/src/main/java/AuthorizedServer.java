import java.net.*;   
import java.io.*;
import java.util.*;  
import R_HTTPResponse.*;  

// Сервер авторизации

public class AuthorizedServer extends Thread
{

        Hashtable SYS_clients; // Указатель на таблицу с текущими клиентами
        Hashtable SYS_passwords; // Указатель на таблицу соответствия паролей и имен

        int listen_port; 
        ServerSocket main_server_socket;
        public ThreadGroup client_group;

        public boolean trace_client;
        public server_par spar; // Указатель на системные параметры сервера

        public void  CloseServer()
        {
                try
                {
                        client_group.checkAccess();
                        client_group.stop();
                        client_group = null;
                        SYS_clients.clear(); 
                }
                catch (Exception e) {} 
         }

        public void run()
        {
                Socket locsckt;
                AuthClient ct;
                while(true)
                {
                        try
                        {
                                // Прослушиваем заданный порт
                                locsckt = main_server_socket.accept();
                                if (client_group.activeCount()>=spar.max_client_con.intValue()) 
                                {
                                        locsckt.close();
                                        yield();
                                        continue;
                                }
                                if  (trace_client) System.out.println("Client "+locsckt.getInetAddress()+" quested authorization");
                                // Запускаем поток клиента и передаем ему параметры                                
                                ct = new AuthClient(client_group,locsckt,SYS_clients,SYS_passwords,trace_client);
                                yield();
                        }
                        catch (Exception e)   { }
                }
        }

        public AuthorizedServer(int port,Hashtable clients, Hashtable passwords, server_par sp) throws IOException 
        {
                super();
                this.setDaemon(true);
                this.setPriority(this.NORM_PRIORITY);  
                spar = sp;
                listen_port = port;
                client_group = new ThreadGroup("AUTHOR_CLIENTS_GROUP") ;
                client_group.setDaemon(false);
                client_group.setMaxPriority(Thread.MAX_PRIORITY); 
                SYS_clients = clients;
                SYS_passwords = passwords; 

                try
                {                
                        main_server_socket = new ServerSocket(listen_port); 
                }
                catch (IOException e)
                {
                        throw new IOException("Error of create the Authorized Server Socket!"); 
                }
                
        }




}
