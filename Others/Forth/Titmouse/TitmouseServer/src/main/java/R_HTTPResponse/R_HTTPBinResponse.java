package R_HTTPResponse;

import ScriptForth.ScriptForth;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.Socket;
import java.text.DateFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Hashtable;
import java.util.TimeZone;
import java.util.Vector;

public class R_HTTPBinResponse {
  public static String[] ShortMonths = {"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec", ""};
  public static String[] ShortDays = {"", "Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat"};

  public String rsrc_name;
  public String typ_file;
  public Date file_date;
  public boolean IS_Forth_Server_Page = false;
  public String Post_body; // Тело метода POST
  Socket cl_socket;
  File rsrc_file;
  HTTPQueryRecord q_rec;
  boolean is_admin; // Флаг показывает, что ответ затребован администратором системы
  server_par separ; // указатель на таблицу параметров сервера
  Hashtable clients_list; // Таблица, содержит всех зарегистрированных пользователей системы

  public R_HTTPBinResponse(Socket clsock, String url_name, server_par sp, HTTPQueryRecord qrec) throws IOException {
    cl_socket = clsock;
    separ = sp;
    q_rec = qrec;
    int li;
    li = url_name.lastIndexOf('.');
    if (li < 0) {
      url_name = url_name + sp.default_page;
      li = url_name.lastIndexOf('.');
    }
    typ_file = "";
    for (int la = (url_name.length() - 1); la > li; la--) {
      typ_file = url_name.charAt(la) + typ_file;
    }
    typ_file = typ_file.toUpperCase();

    rsrc_name = url_name;

    rsrc_file = new File(url_name);

    if (!rsrc_file.exists()) throw new IOException("0");

    if (rsrc_file.isDirectory()) {
      rsrc_file = null;
      new IOException("0");
    }

  }

  public String URLDecode(String src) {
    int li = 0;
    char cur_char;
    byte[] dim_byte = new byte[1];
    String tmp_buf;
    String dest = "";
    for (li = 0; li < src.length(); li++) {
      cur_char = src.charAt(li);

      switch (cur_char) {
        case '+':
          dest = dest + " ";
          break;
        case '%': {
          li++;
          tmp_buf = String.valueOf(src.charAt(li));
          li++;
          tmp_buf = tmp_buf + src.charAt(li);
          try {
            dim_byte[0] = (byte) Integer.parseInt(tmp_buf, 16);
            dest = dest + new String(dim_byte);
          } catch (Exception e) {
            dest = dest + "%" + tmp_buf;
          }
        }
        ;
        break;
        default:
          dest = dest + cur_char;
          break;
      }
    }
    return dest;
  }

  void FillFieldParametersFromPost(String post_str, ScriptForth sf) throws Exception {
    String name_par = "";
    String body_par = "";
    boolean is_name = true;
    int li;
    char cur_char;
    for (li = 0; li < post_str.length(); li++) {
      cur_char = post_str.charAt(li);
      switch (cur_char) {
        case '&': {
          if (name_par.length() > 0) {
            sf.AddStringConstant(URLDecode(name_par), URLDecode(body_par));
            name_par = "";
            body_par = "";
            is_name = true;
          }
        }
        ;
        break;
        case '=': {
          is_name = false;
        }
        ;
        break;
        default: {
          if (is_name) name_par = name_par + cur_char;
          else body_par = body_par + cur_char;
        }
        ;
      }
    }
    if (name_par.length() > 0) {
      if (body_par.length() > 0) sf.AddStringConstant(URLDecode(name_par), URLDecode(body_par));
    }
  }

  public Vector GetResponseHeader(String client_ip_address) throws Exception {
    ScriptForth scr_frt = null;
    String ext_file;
    String net_datetime;
    int li;
    String forth_body;

    file_date = new Date(rsrc_file.lastModified());
    Date cur_date = new Date(System.currentTimeMillis());
    DateFormatSymbols dfs = new DateFormatSymbols();
    dfs.setShortMonths(ShortMonths);
    dfs.setShortWeekdays(ShortDays);

    SimpleDateFormat df = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss 'GMT'");
    df.setTimeZone(TimeZone.getTimeZone("GMT"));
    df.setDateFormatSymbols(dfs);

    Vector vector_body = new Vector();
    IS_Forth_Server_Page = false;

    if (typ_file.compareTo("FSP") == 0) {
      IS_Forth_Server_Page = true;
      scr_frt = new ScriptForth(cl_socket, clients_list);

      int lmime_typ = scr_frt.AddStringConstant("FSP-CNTTYPE", "text/html");
      scr_frt.AddStringConstant("FSP-CLIPADDR", client_ip_address);
      scr_frt.AddStringConstant("CGI-AUTHSCHEME", q_rec.AuthorizationScheme);
      scr_frt.AddStringConstant("CGI-AUTHPSSWRD", q_rec.AuthorizationNamePassword);

      scr_frt.AddStringConstant("FSP-QUERY", q_rec.CGIString);

      // Добавляем параметры сервера
      int al_port = scr_frt.AddNumberValue("ADM-PORT", separ.server_port.intValue());
      int al_send_enable = scr_frt.AddNumberValue("SEND-ENABLE", -1);
      int al_maxc = scr_frt.AddNumberValue("ADM-MAXCLIEN", separ.max_client_con.intValue());
      int al_hd = scr_frt.AddStringConstant("ADM-HOMEDIR", separ.home_dir);
      int al_defp = scr_frt.AddStringConstant("ADM-DEFPAGE", separ.default_page);

      if (Post_body != null) {
        scr_frt.AddStringConstant("POST-BODY", Post_body);
        if (q_rec.Content_Type.equalsIgnoreCase("application/x-www-form-urlencoded")) {
          FillFieldParametersFromPost(Post_body, scr_frt);
        }
      } else {
        if (q_rec.CGIString.length() > 0) {
          scr_frt.AddStringConstant("POST-BODY", q_rec.CGIString);
          FillFieldParametersFromPost(q_rec.CGIString, scr_frt);
        } else
          scr_frt.AddStringConstant("POST-BODY", "");
      }

      try {
        forth_body = (scr_frt.PlayScript(rsrc_name)).toString();
      } catch (Exception e) {
        try {
          scr_frt.close();
        } catch (Exception t) {
        }
        throw new Exception("str# " + String.valueOf(scr_frt.StringNumber) + ":" + e.getMessage());
      }

      if (is_admin) {
        separ.server_port = new Integer((int) scr_frt.GetIntegerVar(al_port));
        separ.max_client_con = new Integer((int) scr_frt.GetIntegerVar(al_maxc));
        separ.home_dir = scr_frt.GetStringVar(al_hd);
        separ.default_page = scr_frt.GetStringVar(al_defp);
      }

      if (scr_frt.GetIntegerVar(al_send_enable) != 0) {
        vector_body.addElement("Date: " + df.format(cur_date) + "\r\n");
        vector_body.addElement("Server: R-HTTP/1.0\r\n");
        vector_body.addElement("Allow: GET, HEAD, POST\r\n");
        vector_body.addElement("MIME-version: 1.0\r\n");
        vector_body.addElement("Content-Length: " + String.valueOf(forth_body.length()) + "\r\n");
        vector_body.addElement("Content-type: " + scr_frt.GetStringVar(lmime_typ) + "\r\n");
        vector_body.addElement("Last-Modified: " + df.format(cur_date) + "\r\n");
        vector_body.addElement("\r\n");
        vector_body.addElement(forth_body);
      } else return null;
      file_date = null;
      cur_date = null;
      dfs = null;

      return vector_body;
    }

    vector_body.addElement("Date: " + df.format(cur_date) + "\r\n");
    vector_body.addElement("Server: R-HTTP/1.0\r\n");
    vector_body.addElement("Allow: GET, HEAD\r\n");
    vector_body.addElement("MIME-version: 1.0\r\n");
    vector_body.addElement("Content-Length: " + Long.toString(rsrc_file.length()) + "\r\n");

    if (typ_file.compareTo("WRL") == 0) vector_body.addElement("Content-type: x-world/x-vrml\r\n");
    else if (typ_file.compareTo("HTML") == 0) vector_body.addElement("Content-type: text/html\r\n");
    else if (typ_file.compareTo("HTM") == 0) vector_body.addElement("Content-type: text/html\r\n");
    else if (typ_file.compareTo("GIF") == 0) vector_body.addElement("Content-type: image/gif\r\n");
    else if (typ_file.compareTo("JPG") == 0) vector_body.addElement("Content-type: image/jpg\r\n");
    else if (typ_file.compareTo("BMP") == 0) vector_body.addElement("Content-type: image/bmp\r\n");
    else if (typ_file.compareTo("TIF") == 0) vector_body.addElement("Content-type: image/tif\r\n");
    else {
      vector_body.addElement("Content-type: text/plain\r\n");
    }

    vector_body.addElement("Last-Modified: " + df.format(file_date) + "\r\n");
    vector_body.addElement("\r\n");

    cur_date = null;
    dfs = null;

    try {
      if (scr_frt != null) scr_frt.close();
    } catch (Exception e) {
    }

    return vector_body;
  }

  public FileInputStream GetResponseBody() throws IOException {
    String tmp_line;
    FileInputStream fis;

    try {
      fis = new FileInputStream(rsrc_file);
    } catch (Exception e) {
      throw new IOException();
    }
    return fis;
  }
}
