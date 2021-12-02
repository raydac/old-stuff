package com.igormaznitsa.R_HTTP;

import java.io.*;
import java.net.Socket;
import java.util.Date;
import java.util.Vector;

/**
 * Класс, реализующий поток обработки запроса клиента
 */
public class R_HTTPClientThread extends Thread {
  public boolean no_closed = true;
  public Socket client_socket; // Сокет выделенный данному потоку
  public R_HTTPSocketSpy sspy; // Поток отслеживающий обрыв связи
  public R_HTTPBinResponse bin_response = null;
  String cl_ip_addr; // Адрес клиента потока
  boolean is_admin = false; // Флаг, показывает, что это поток администратора
  DataInputStream input_stream = null; // Поток чтения данных от клиента
  DataOutputStream output_stream = null;  // Поток посылки данных клиенту
  char[] PostData = null; // Массив данных, полученных от клиента методом POST
  boolean trace_connect; // Если true, то включен режим трассировки соединения
  FileInputStream fis = null; // Поток ввода из запрашиваемого клиентом файла
  server_par spar; // Таблица с параметрами сервера
  long li;
  byte[] b_arr;
  server_par servp; // параметры данного сервера
  BusServerParams busp; // Общесистемные параметры сервера

  /**
   * Конструктор потока, получает tg - имя потоковой группы, shd - домашний каталог, sdp - страница по умолчанию, tc - трассировка, sc - указатель на таблицу с текущими клиентами, sp - указатель на таблицу с соотв. паролями и именами
   */
  public R_HTTPClientThread(ThreadGroup tg, Socket cs, server_par spr, boolean tc, BusServerParams bsp, server_par sp) throws IOException {
    super(tg, "Socket Client");
    servp = sp;
    busp = bsp;
    this.setDaemon(false);
    client_socket = cs;
    if (cs != null) {
      cl_ip_addr = cs.getInetAddress().toString();
    }
    trace_connect = tc;
    spar = spr;

    this.setPriority(this.NORM_PRIORITY);
    this.start();
  }

  /**
   * Функция формирует и возвращает вектор с HTTP ответом под номером msg
   */
  synchronized static public Vector err_message(int msg, String astr, String server_version) {
    Vector tmp_vector = new Vector(1);
    switch (msg) {
      case 100:
        tmp_vector.addElement("HTTP/1.0 100 Continue\r\n\r\n");
        break;
      case 101:
        tmp_vector.addElement("HTTP/1.0 101 Switchings Protocols\r\n\r\n");
        break;
      case 200:
        tmp_vector.addElement("HTTP/1.0 200 OK\r\n\r\n");
        break;
      case 201:
        tmp_vector.addElement("HTTP/1.0 201 Created\r\n\r\n");
        break;
      case 202:
        tmp_vector.addElement("HTTP/1.0 202 Accepted\r\n\r\n");
        break;
      case 203:
        tmp_vector.addElement("HTTP/1.0 203 Non-Authoritative Information\r\n\r\n");
        break;
      case 204:
        tmp_vector.addElement("HTTP/1.0 204 No Content\r\n\r\n");
        break;
      case 205:
        tmp_vector.addElement("HTTP/1.0 205 Reset Content\r\n\r\n");
        break;
      case 206:
        tmp_vector.addElement("HTTP/1.0 206 Partial Content\r\n\r\n");
        break;
      case 300:
        tmp_vector.addElement("HTTP/1.0 300 Multiple Choices\r\n\r\n");
        break;
      case 301:
        tmp_vector.addElement("HTTP/1.0 301 Moved Permanently\r\n\r\n");
        break;
      case 302:
        tmp_vector.addElement("HTTP/1.0 302 Moved Temporarily\r\n\r\n");
        break;
      case 303:
        tmp_vector.addElement("HTTP/1.0 303 See Other\r\n\r\n");
        break;
      case 304:
        tmp_vector.addElement("HTTP/1.0 304 Not Modified\r\n\r\n");
        break;
      case 305:
        tmp_vector.addElement("HTTP/1.0 305 Use Proxy\r\n\r\n");
        break;
      case 400:
        tmp_vector.addElement("HTTP/1.0 400 Bad Request\r\n\r\n");
        break;
      case 401:
        tmp_vector.addElement("HTTP/1.0 401 Unauthorized\r\n\r\n");
        break;
      case 402:
        tmp_vector.addElement("HTTP/1.0 402 Payment Required\r\n\r\n");
        break;
      case 403:
        tmp_vector.addElement("HTTP/1.0 403 Forbidden resource\r\n\r\n<html><head><title>Forbidden resource</title><meta http-equiv=\"Content-Type\" content=\"text/html; charset=iso-8859-1\"></head><p><font size=\"5\"><strong>HTTP/1.0 403 Forbidden resource</strong></font></p><p>Resource: " + astr + "</p><hr><b><i>" + server_version + "</i></b></body></html>");
        break;
      case 404:
        tmp_vector.addElement("HTTP/1.0 404 Resource not found\r\n\r\n<html><head><title>Resource not found</title><meta http-equiv=\"Content-Type\" content=\"text/html; charset=iso-8859-1\"></head><p><font size=\"5\"><strong>HTTP/1.0 404 Resource not found</strong></font></p><p>Resource: " + astr + "</p><hr><b><i>" + server_version + "</i></b></body></html>");
        break;
      case 405:
        tmp_vector.addElement("HTTP/1.0 405 Method not allow\r\n\r\n<html><head><title>Method not allow</title><meta http-equiv=\"Content-Type\" content=\"text/html; charset=iso-8859-1\"></head><p><font size=\"5\"><strong>HTTP/1.0 405 Method not allow</strong></font></p><p>Resource: " + astr + "</p><hr><b><i>" + server_version + "</i></b></body></html>");
        break;
      case 406:
        tmp_vector.addElement("HTTP/1.0 406 Not Acceptable\r\n\r\n");
        break;
      case 407:
        tmp_vector.addElement("HTTP/1.0 407 Proxy Authentication Required\r\n\r\n");
        break;
      case 408:
        tmp_vector.addElement("HTTP/1.0 408 Request Time-out\r\n\r\n");
        break;
      case 409:
        tmp_vector.addElement("HTTP/1.0 409 Conflict\r\n\r\n");
        break;
      case 410:
        tmp_vector.addElement("HTTP/1.0 410 Gone\r\n\r\n");
        break;
      case 411:
        tmp_vector.addElement("HTTP/1.0 411 Length Required\r\n\r\n");
        break;
      case 412:
        tmp_vector.addElement("HTTP/1.0 412 Precondition Failed\r\n\r\n");
        break;
      case 413:
        tmp_vector.addElement("HTTP/1.0 413 Request Entity Too Large\r\n\r\n");
        break;
      case 414:
        tmp_vector.addElement("HTTP/1.0 414 Request-URI Too Long\r\n\r\n");
        break;
      case 415:
        tmp_vector.addElement("HTTP/1.0 415 Unsupported Media Type\r\n\r\n");
        break;
      case 500:
        tmp_vector.addElement("HTTP/1.0 500 Internal server error\r\n\r\n<html><head><title>Internal server error</title><meta http-equiv=\"Content-Type\" content=\"text/html; charset=iso-8859-1\"></head><p><font size=\"5\"><strong>HTTP/1.0 500 Internal server error</strong></font></p><p>Resource: " + astr + "</p><hr><b><i>" + server_version + "</i></b></body></html>");
        break;
      case 501:
        tmp_vector.addElement("HTTP/1.0 501 Not Implemented\r\n\r\n");
        break;
      case 502:
        tmp_vector.addElement("HTTP/1.0 502 Bad Gateway\r\n\r\n");
        break;
      case 503:
        tmp_vector.addElement("HTTP/1.0 503 Service Unavailable\r\n\r\n");
        break;
      case 504:
        tmp_vector.addElement("HTTP/1.0 504 Gateway Time-out\r\n\r\n");
        break;
      case 505:
        tmp_vector.addElement("HTTP/1.0 505 HTTP Version not supported\r\n\r\n");
        break;
    }

    return tmp_vector;
  }

  /**
   * Функция останавливает поток
   */
  public void Stop() {
    if (bin_response != null) {
      if (bin_response.scr_frt != null) {
        bin_response.scr_frt.IsQuit = true;
      }
    }
    no_closed = false;
  }

  /**
   * Функция передает в поток содержимое строки
   */
  public void send_vector(DataOutputStream doss, Vector vctr) throws IOException {
    int li;
    int loi = vctr.size();
    for (li = 0; li < loi; li++) {
      if (vctr.elementAt(li) == null)
        break;
      else if (vctr.elementAt(li) instanceof String) {
        byte[] barr = ((String) vctr.elementAt(li)).getBytes();
        for (int lli = 0; lli < barr.length; lli++) doss.write(barr[lli]);
      } else {
        if (vctr.elementAt(li) instanceof ScriptForthBuffer) {
          int lsze = ((ScriptForthBuffer) vctr.elementAt(li)).len;
          byte[] larrbyte = ((ScriptForthBuffer) vctr.elementAt(li)).buffermem;
          for (int kli = 0; kli < lsze; kli++) {
            doss.write(larrbyte[kli]);
          }
        }
      }
    }
    doss.flush();
  }

  public void run() {
    Vector client_query = new Vector();
    Vector client_response;
    HTTPQueryRecord qrec;
    String tmp_line;

    int li;

    try {
      // Создание потоков чтения-записи
      input_stream = new DataInputStream(client_socket.getInputStream());
      output_stream = new DataOutputStream(client_socket.getOutputStream());

      //output_stream.writeBytes("HTTP/1.0 200 OK\r\n\r\n");

      client_socket.setSoTimeout(spar.GetTimeDelay());

      // Блок считывания запроса от клиента
      //-------------------------------------
      while (true) {
        tmp_line = input_stream.readLine();
        if ((tmp_line == null) || (tmp_line.length() == 0)) break;
        client_query.addElement(tmp_line);
      }
      //-------------------------------------

      client_socket.setSoTimeout(0);

      // Блок анализа запроса клиента
      qrec = new HTTPQueryRecord(client_query);


      if ((qrec.Method.compareTo("POST") == 0) && (qrec.Content_Length > 0)) {
        int tmp_d;
        PostData = new char[qrec.Content_Length];
        for (int lii = 0; lii < qrec.Content_Length; lii++) {
          tmp_d = input_stream.read();
          if (tmp_d < 0) throw new Exception("Data of POST are absent");
          PostData[lii] = (char) tmp_d;
        }
      }


      sspy = null;

      try {
        sspy = new R_HTTPSocketSpy(this.trace_connect, this.getThreadGroup(), this);
        try {
          bin_response = new R_HTTPBinResponse(this, busp, client_socket, spar.home_dir, qrec.URLString, spar, qrec);
        } catch (IOException err) {
          if (err.getMessage().compareTo("000") == 0) {
            send_vector(output_stream, this.err_message(403, qrec.URLString, busp.ServerVersion));
            Date tmp_date = new Date(System.currentTimeMillis());
            int tmp_ss = tmp_date.getSeconds();
            int tmp_mm = tmp_date.getMinutes();
            int tmp_hh = tmp_date.getHours();
            System.out.println(String.valueOf(tmp_hh) + ":" + String.valueOf(tmp_mm) + ":" + String.valueOf(tmp_ss) + " " + client_socket.getInetAddress() + " [port " + spar.server_port + "] " + "Forbidden resource " + qrec.URLString + " ! Bandit address is " + cl_ip_addr);
            tmp_date = null;
            throw new IOException();
          } else
            throw new IOException(err.getMessage());
        }

        if (PostData != null)
          bin_response.Post_body = String.copyValueOf(PostData);
        else
          bin_response.Post_body = null;

        if (trace_connect) {
          Date tmp_date = new Date(System.currentTimeMillis());
          int tmp_ss = tmp_date.getSeconds();
          int tmp_mm = tmp_date.getMinutes();
          int tmp_hh = tmp_date.getHours();
          System.out.println(String.valueOf(tmp_hh) + ":" + String.valueOf(tmp_mm) + ":" + String.valueOf(tmp_ss) + " " + cl_ip_addr + " [port " + spar.server_port + "] " + qrec.Method + " " + qrec.URLString);
          tmp_date = null;
        }
      } catch (Exception e) {
        if (e.getMessage().compareTo("0") == 0) {
          send_vector(output_stream, this.err_message(404, qrec.URLString, busp.ServerVersion));
          throw new Exception();
        }
      }

      try {
        client_response = null;
        try {
          client_response = bin_response.GetResponseHeader(client_socket.getInetAddress().getHostAddress());
        } catch (Exception e) {
          throw new Exception(e.getMessage());
        }

        if (sspy != null) sspy.Stop();
        sspy = null;

        if (bin_response.IS_Forth_Server_Page) {
          if (client_response != null) {
            output_stream.write("HTTP/1.0 200 OK\r\n".getBytes());
            output_stream.flush();
            send_vector(output_stream, client_response);
          }
        } else {
          // Блок отправки ответа клиенту
          // Выясняем, не изменился ли ресурс
          boolean fl_t = true;
          if ((qrec.IMS_Date != null) && (qrec.Method.compareTo("HEAD") != 0)) {
            try {
              if (qrec.IMS_Date.getTime() == bin_response.file_date.getTime()) fl_t = false;
            } catch (Exception e) {
              fl_t = true;
            }
            ;
            try {
              if (!fl_t) {
                output_stream.write("HTTP/1.0 304 Not Modified\r\n\r\n".getBytes());
                output_stream.flush();
                client_socket.close();
              }
            } catch (Exception er) {
              if (no_closed)
                throw new Exception("Error during dispatch the answer");
              else
                throw new Exception();
            }
            ;
          }

          if (fl_t) {
            output_stream.write("HTTP/1.0 200 OK\r\n".getBytes());
            send_vector(output_stream, client_response);
            // Получаем поток из файла запрошенного клиентом
            fis = bin_response.GetResponseBody();
            b_arr = new byte[fis.available()];

            // Переписываем данные из файлового потока в поток клиента
            if (qrec.Method.compareTo("HEAD") != 0) {
              try {
                while (true) {
                  li = fis.read(b_arr);
                  if (li < 0) break;
                  for (int alii = 0; alii < li; alii++) output_stream.write(b_arr[alii]);
                }
                output_stream.flush();
              } catch (Exception er) {
                throw new Exception("Error during dispatch the answer");
              }
              ;
            }
          }
        }
        bin_response = null;
      } catch (Exception e) {
        if (bin_response != null) {
          if (bin_response.scr_frt != null) {
            bin_response.scr_frt.close();
          }
        }

        System.out.println("Inside error: " + e.getMessage() + " [" + bin_response.rsrc_name + "]");

        if (sspy != null) sspy.Stop();
        sspy = null;
        bin_response = null;
        try {
          client_response = this.err_message(500, qrec.URLString, busp.ServerVersion);
          send_vector(output_stream, client_response);
        } catch (Exception j) {
        }
        try {
          client_socket.close();
          client_socket = null;
        } catch (Exception j) {
        }

        throw new Exception();
      }
      if (fis != null) fis.close();
      fis = null;
      b_arr = null;
    } catch (Exception e) {
      if (e.getMessage() != null) System.out.println("Inside error: " + e.getMessage());
    } finally {
      try {
        output_stream.close();
        input_stream.close();
      } catch (Exception e) {
      }
      try {
        OutputStream los = client_socket.getOutputStream();
        if (los != null) los.flush();
        client_socket.close();
        client_socket = null;
      } catch (Exception e) {
      }
      ;

      if (trace_connect && no_closed) {
        Date tmp_date = new Date(System.currentTimeMillis());
        int tmp_ss = tmp_date.getSeconds();
        int tmp_mm = tmp_date.getMinutes();
        int tmp_hh = tmp_date.getHours();
        try {
          System.out.println(String.valueOf(tmp_hh) + ":" + String.valueOf(tmp_mm) + ":" + String.valueOf(tmp_ss) + " " + cl_ip_addr + " [port " + spar.server_port + "] disconnected ");
        } catch (Exception e) {
        }
        tmp_date = null;
      }
    }
  }

  public void finalize() {
    try {
      if (client_socket != null) client_socket.close();
      client_socket = null;
    } catch (Exception e) {
    }
  }

}