import R_HTTPResponse.ClientRecord;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Enumeration;
import java.util.Hashtable;

public class AuthClient extends Thread {
  Socket cur_socket;
  Hashtable passwords; // таблица паролей
  Hashtable clients;   // таблица подключенных пользователей
  DataInputStream input_stream; // Поток ввода из сокета
  DataOutputStream output_stream; // Поток вывода в сокет

  ClientRecord clr_rec; // Запись текущего клиента в таблице паролей
  ClientRecord clr_rec2; // запись текущего клиента в таблице клиентов

  boolean Add_To_Clients = false; // Флаг, показывает что клиент был добавлен

  boolean trace_clnt = false;

  InetAddress cur_address; // Адрес текущего клиента

  public AuthClient(ThreadGroup tg, Socket sck, Hashtable clnt, Hashtable pswd, boolean trace) throws Exception {
    super(tg, "Auth_client");
    cur_socket = sck;
    passwords = pswd;
    clients = clnt;

    trace_clnt = trace;

    input_stream = new DataInputStream(cur_socket.getInputStream());
    output_stream = new DataOutputStream(cur_socket.getOutputStream());

    this.setDaemon(true);
    this.setPriority(this.NORM_PRIORITY - 2);
    this.start();
  }

  public void run() {
    String tmp_str, tmp_str2;
    int li;
    cur_address = cur_socket.getInetAddress();

    try {
      if (clients.get(cur_address) != null) {
        output_stream.writeBytes("%RECONNECTEDERROR%\r\n");
        cur_socket.close();
        return;
      }
    } catch (Exception e) {
      return;
    }


    try {
      tmp_str = input_stream.readLine();
      if (tmp_str == null) {
        cur_socket.close();
        return;
      }
    } catch (Exception e) {
      return;
    }
    if (tmp_str.compareTo("%QUERYAUTH%") != 0) {
      try {
        input_stream.close();
        output_stream.close();
        cur_socket.close();
        return;
      } catch (Exception e) {
        return;
      }
    }
    try {
      tmp_str = input_stream.readLine();
      tmp_str2 = input_stream.readLine();

      clr_rec = (ClientRecord) passwords.get(tmp_str);

      Enumeration enm = clients.elements();
      while (enm.hasMoreElements()) {
        clr_rec2 = (ClientRecord) enm.nextElement();
        if (clr_rec2.name.compareTo(tmp_str) == 0) {
          break;
        }
        clr_rec2 = null;
      }

      if (clr_rec != null) {
        if (tmp_str2.compareTo(clr_rec.password) == 0) {
          if (clr_rec2 != null) {
            if (clr_rec2.MaxConnectNumber < clr_rec.MaxConnectNumber) {
              clr_rec2.MaxConnectNumber = clr_rec2.MaxConnectNumber + 1;
              Add_To_Clients = true;
            } else {
              output_stream.writeBytes("%TOOMUCHUSERS%\r\n");
              cur_socket.close();
              return;
            }
          } else {
            clr_rec2 = new ClientRecord(clr_rec.name, clr_rec.password, 1);
            clients.put(cur_address, clr_rec2);
            Add_To_Clients = true;
          }
        } else {
          output_stream.writeBytes("%BADUSERPASSWORD%\r\n");
          cur_socket.close();
          return;
        }
        output_stream.writeBytes("%OK%\r\n");

        while (true) {
          try {
            li = input_stream.readByte();
          } catch (Exception e) {
            break;
          }
        }

        clients.remove(cur_address);
        Add_To_Clients = false;
        cur_socket.close();
      } else {
        output_stream.writeBytes("%BADUSERNAME%\r\n");
        cur_socket.close();
        return;
      }

    } catch (Exception e) {
      if (Add_To_Clients) {
        if (clr_rec2.MaxConnectNumber <= 1)
          clients.remove(cur_address);
        else
          clr_rec2.MaxConnectNumber--;
      }
      try {
        if (cur_socket != null) {
          cur_socket.close();
        }
      } catch (Exception s) {
      }
    }

  }
}
