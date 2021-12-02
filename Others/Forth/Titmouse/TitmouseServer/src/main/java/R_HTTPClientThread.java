import java.net.*;
import java.io.*;
import java.util.*; 
import R_HTTPResponse.*;   

/** Класс, реализующий поток обработки запроса клиента */
public class R_HTTPClientThread extends Thread 
{
        Socket client_socket; // Сокет выделенный данному потоку
        
       
        boolean is_admin = false; // Флаг, показывает, что это поток администратора

        BufferedReader  input_stream=null; // Поток чтения данных от клиента
        DataOutputStream output_stream=null;  // Поток посылки данных клиенту

        char [] PostData = null; // Массив данных, полученных от клиента методом POST 

        boolean trace_connect; // Если true, то включен режим трассировки соединения
       
        FileInputStream fis=null; // Поток ввода из запрашиваемого клиентом файла

        server_par spar; // Таблица с параметрами сервера

        long li ;
        byte [] b_arr;

        /** Функция передает в поток содержимое строкового вектора */
        public void send_vector(DataOutputStream doss,Vector vctr) throws IOException
        {
                int li;
                for (li=0;li<vctr.size();li++)
                {
                        byte [] barr = ((String)vctr.elementAt(li)).getBytes(); 
                        for (int lli=0; lli<barr.length; lli++) doss.writeByte(barr[lli]); 
                } 
                doss.flush(); 
        }

        /** Функция формирует и возвращает вектор с HTTP ответом под номером msg */
        static public Vector err_message(int msg)
        {
                Vector tmp_vector = new Vector();

                switch (msg) 
                {
                        case 100 : tmp_vector.addElement("HTTP/1.0 100 Continue\r\n\r\n"); break;
                        case 101 : tmp_vector.addElement("HTTP/1.0 101 Switchings Protocols\r\n\r\n"); break;
                        case 200 : tmp_vector.addElement("HTTP/1.0 200 OK\r\n\r\n");break;
                        case 201 : tmp_vector.addElement("HTTP/1.0 201 Created\r\n\r\n");break;
                        case 202 : tmp_vector.addElement("HTTP/1.0 202 Accepted\r\n\r\n");break;
                        case 203 : tmp_vector.addElement("HTTP/1.0 203 Non-Authoritative Information\r\n\r\n");break;
                        case 204 : tmp_vector.addElement("HTTP/1.0 204 No Content\r\n\r\n");break;
                        case 205 : tmp_vector.addElement("HTTP/1.0 205 Reset Content\r\n\r\n");break;
                        case 206 : tmp_vector.addElement("HTTP/1.0 206 Partial Content\r\n\r\n");break;
                        case 300 : tmp_vector.addElement("HTTP/1.0 300 Multiple Choices\r\n\r\n");break;
                        case 301 : tmp_vector.addElement("HTTP/1.0 301 Moved Permanently\r\n\r\n");break;
                        case 302 : tmp_vector.addElement("HTTP/1.0 302 Moved Temporarily\r\n\r\n");break;
                        case 303 : tmp_vector.addElement("HTTP/1.0 303 See Other\r\n\r\n");break;
                        case 304 : tmp_vector.addElement("HTTP/1.0 304 Not Modified\r\n\r\n");break;
                        case 305 : tmp_vector.addElement("HTTP/1.0 305 Use Proxy\r\n\r\n");break;
                        case 400 : tmp_vector.addElement("HTTP/1.0 400 Bad Request\r\n\r\n");break;
                        case 401 : tmp_vector.addElement("HTTP/1.0 401 Unauthorized\r\n\r\n");break;
                        case 402 : tmp_vector.addElement("HTTP/1.0 402 Payment Required\r\n\r\n");break;
                        case 403 : tmp_vector.addElement("HTTP/1.0 403 Forbidden\r\n\r\n");break;
                        case 404 : {
                                                        tmp_vector.addElement("HTTP/1.0 404 Not Found\r\n\r\n");
                                                        tmp_vector.addElement("<p><h1>HTTP/1.0 404 Not Found</h1><hr></p><p>This resource not found</p>\r\n");
                                             }   break;
                        case 405 : tmp_vector.addElement("HTTP/1.0 405 Method Not Allow\r\n\r\n");{
                                                        tmp_vector.addElement("HTTP/1.0 405 Method Not Allow\r\n\r\n");
                                                        tmp_vector.addElement("<p><h1>HTTP/1.0 405 Not Allow</h1><hr></p><p>Method not allow</p>\r\n");}break;             
                        case 406 : tmp_vector.addElement("HTTP/1.0 406 Not Acceptable\r\n\r\n");break;             
                        case 407 : tmp_vector.addElement("HTTP/1.0 407 Proxy Authentication Required\r\n\r\n");break;             
                        case 408 : tmp_vector.addElement("HTTP/1.0 408 Request Time-out\r\n\r\n");break;             
                        case 409 : tmp_vector.addElement("HTTP/1.0 409 Conflict\r\n\r\n");break;             
                        case 410 : tmp_vector.addElement("HTTP/1.0 410 Gone\r\n\r\n");break;             
                        case 411 : tmp_vector.addElement("HTTP/1.0 411 Length Required\r\n\r\n");break;             
                        case 412 : tmp_vector.addElement("HTTP/1.0 412 Precondition Failed\r\n\r\n");break;             
                        case 413 : tmp_vector.addElement("HTTP/1.0 413 Request Entity Too Large\r\n\r\n");break;             
                        case 414 : tmp_vector.addElement("HTTP/1.0 414 Request-URI Too Long\r\n\r\n");break;             
                        case 415 : tmp_vector.addElement("HTTP/1.0 415 Unsupported Media Type\r\n\r\n");break;             
                        case 500 : {                                                        
                                                        tmp_vector.addElement("HTTP/1.0 500 Internal Server Error\r\n\r\n");
                                                        tmp_vector.addElement("<p><h1>HTTP/1.0 500 Internal Server Error</h1><hr></p><p>Internal server error </p>\r\n");
                                   }; break;             
                        case 501 : tmp_vector.addElement("HTTP/1.0 501 Not Implemented\r\n\r\n");break;             
                        case 502 : tmp_vector.addElement("HTTP/1.0 502 Bad Gateway\r\n\r\n");break;             
                        case 503 : tmp_vector.addElement("HTTP/1.0 503 Service Unavailable\r\n\r\n");break;             
                        case 504 : tmp_vector.addElement("HTTP/1.0 504 Gateway Time-out\r\n\r\n");break;             
                        case 505 : tmp_vector.addElement("HTTP/1.0 505 HTTP Version not supported\r\n\r\n");break;        
                }

                return tmp_vector;
        }

        /** Конструктор потока, получает tg - имя потоковой группы, shd - домашний каталог, sdp - страница по умолчанию, tc - трассировка, sc - указатель на таблицу с текущими клиентами, sp - указатель на таблицу с соотв. паролями и именами */
        public R_HTTPClientThread(ThreadGroup tg,Socket cs,server_par spr, boolean tc) throws IOException 
        {
                super(tg,"HTTP_Client_Thread");
                client_socket = cs;
                trace_connect = tc;
                spar = spr;        

                this.setPriority(this.NORM_PRIORITY);  
                this.start(); 
        }

        public void run()
        {
                Vector client_query = new Vector();
                Vector client_response;
                HTTPQueryRecord qrec;
                String tmp_line;

                R_HTTPBinResponse bin_response;

                int li;

                try
                {
                        // Создание потоков чтения-записи 
                        input_stream = new BufferedReader(new InputStreamReader(client_socket.getInputStream()));  
                        output_stream = new DataOutputStream(client_socket.getOutputStream());                  

                        // Блок считывания запроса от клиента
                        //-------------------------------------
                        while(true)
                        {
                                tmp_line = input_stream.readLine();
                                if ((tmp_line==null)||(tmp_line.length()==0)) break;
                                client_query.addElement(tmp_line);
                        }  
                        //-------------------------------------                        

                        // Блок анализа запроса клиента 
                        qrec = new HTTPQueryRecord(client_query); 
                        bin_response = null;

                        if (qrec.Method.compareTo("POST")==0)
                        {
                           PostData = new char[qrec.Content_Length];      
                           input_stream.read(PostData);       
                        }  

                        try
                        {
                                bin_response = new R_HTTPBinResponse(client_socket,spar.home_dir+qrec.URLString,spar,qrec); 
                                if (PostData!=null) bin_response.Post_body =  String.copyValueOf(PostData);
                                        else bin_response.Post_body = null;

                                if (trace_connect) System.out.println("Client "+client_socket.getInetAddress()+" requested resource "+qrec.URLString);    
                        }
                        catch (Exception e)
                        {
                                if (e.getMessage().compareTo("0")==0)
                                {
                                                client_response = this.err_message(404);
                                                send_vector(output_stream,client_response);  
                                                throw new Exception(); 
                                }  
                        }

                        try
                        {
                                client_response = bin_response.GetResponseHeader(client_socket.getInetAddress().getHostAddress()); 
                                if (bin_response.IS_Forth_Server_Page) 
                                {
                                         if (client_response!=null)
                                         {           
                                                output_stream.writeBytes("HTTP/1.0 200 OK\r\n");
                                                output_stream.flush();
                                                send_vector(output_stream,client_response);
                                         }     
                                }
                                else
                                {        
                                        // Блок отправки ответа клиенту
                                        output_stream.writeBytes("HTTP/1.0 200 OK\r\n");
                                        output_stream.flush(); 
                                        send_vector(output_stream,client_response); 
                                        boolean fl_t = false;
                                        try
                                        {
                                                 if (qrec.IMS_Date.getTime()!=bin_response.file_date.getTime()) 
                                                 fl_t = true;
                                        }
                                        catch (Exception e) { fl_t=true;};
                                        if (fl_t)
                                        {
                                           // Получаем поток из файла запрошенного клиентом
                                           fis = bin_response.GetResponseBody(); 
                                           b_arr = new byte [fis.available()];

                                           // Переписываем данные из файлового потока в поток клиента
                                           if (qrec.Method.compareTo("HEAD")!=0)
                                           {   
                                                   while(true)
                                                   {
                                                         li=fis.read(b_arr);
                                                         if (li<0) break;
                                                         output_stream.write(b_arr,0,li); 
                                                   }
                                                   output_stream.flush(); 
                                           } 
                                        }    
                                }
                        }
                        catch(Exception e)
                        {
                                System.out.println("Inside error: "+e.getMessage()+" ["+bin_response.rsrc_name+"]"); 
                                try
                                {
                                        client_response = this.err_message(500);
                                        send_vector(output_stream,client_response);  
                                }
                                catch (Exception j) {}
                                try
                                {
                                        client_socket.close(); 
                                        client_socket = null;
                                }
                                catch (Exception j)
                                {}                                

                                throw new Exception(); 
                        }

                        if (fis!=null) fis.close();
                        fis=null; 
                        b_arr=null;
                 }
                catch (Exception e) 
                { 
                        if (e.getMessage()!=null)  System.out.println("Inside error: "+e.getMessage()); 
                }
                finally 
                {
                        try 
                        {
                                output_stream.close();
                                input_stream.close();
                        }
                        catch ( Exception e) {}
                        try
                        {
                               client_socket.getOutputStream().flush();  
                               client_socket.close();
                        }
                        catch (Exception e) {};
                        if (trace_connect) System.out.println("Disconnected client "+client_socket.getInetAddress());    
                        this.stop();
                }

        }

        public void finalize()
        {
                try
                {
                        client_socket.close();  
                }
                catch(Exception e)
                {}
        }

}