import java.net.*;   
import java.io.*;
import java.util.*;  
import R_HTTPResponse.*;

public class R_HTTPServer extends Thread 
{
        ServerSocket main_server_socket; // "Слушающий сокет"
        int max_client_count=100; // Максимально допустимое количество клиентов
        int listen_port; // Номер прослушиваемого порта
        ThreadGroup client_group; // Группа содержит указатели на клиентские потоки
        public String Server_Home_Dir; // Домашняя директория сервера
        public String Server_Default_Page; // Страничка по умолчанию
        public boolean trace_client = false; // Флаг трассировки подключений клиентов
        public Hashtable sysclients; // Таблица клиентов авторизированных в системе
        public Hashtable syspsswrd; // Таблица соответствия имен пользователей и паролей
        server_par spar; // Таблица с параметрами сервера

        public R_HTTPServer(int port,server_par sp) throws IOException 
        {
                super();
                this.setDaemon(true);
                this.setPriority(this.MIN_PRIORITY);
                spar = sp;
                listen_port = port;
                client_group = new ThreadGroup("HTTPClients_Thread_Group") ;
                client_group.setDaemon(false);
                client_group.allowThreadSuspension(true); 
                client_group.setMaxPriority(Thread.MAX_PRIORITY); 

                try
                {                
                        main_server_socket = new ServerSocket(listen_port); 
                }
                catch (IOException e)
                {
                        throw new IOException("Error of create the Server Socket!"); 
                }
                
        }

        public void  CloseServer()
        {
                try
                {
                        client_group.checkAccess();
                        client_group.stop();
                        client_group = null;
                }
                catch (Exception e) {} 
         }

        public void run()
        {
                Socket locsckt;
                R_HTTPClientThread ct;
                while(true)
                {
                        try
                        {
                                // Прослушиваем заданный порт
                                locsckt = main_server_socket.accept();
                                if (client_group.activeCount()>=max_client_count) 
                                {
                                        locsckt.close();
                                        yield();
                                        continue;
                                }
                                if  (trace_client) System.out.println("Connected client "+locsckt.getInetAddress());
                                // Запускаем поток клиента и передаем ему параметры                                
                                ct = new R_HTTPClientThread(client_group,locsckt,spar,trace_client);
                                yield();
                        }
                        catch (Exception e)   { }
                }
        }

}
